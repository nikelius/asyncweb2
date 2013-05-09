/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package com.bs3.nio.mina2.codec.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import com.bs3.utils.MyLog;
import com.bs3.utils.MyUtil;


/**
 * A {@link MessageDecoder} that decodes {@link HttpRequest}.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 (Fri, 13 Jul 2007) $
 * 修改：取消HttpRequestDecoder构造方法，取消私有变量request（HttpRequestMessage类型）
 * 不足：
 * 1，只处理了GET/POST；
 * 2，要求一次性完成HTTP解析
 * 3，只处理了Content-Length并区分大小写，兼容性不足。
 * 
 */
public class HttpRequestDecoder2 extends MessageDecoderAdapter {
	private static final MyLog _log = MyLog.getLog(HttpRequestDecoder2.class);
    private static final byte[] CONTENT_LENGTH = new String("Content-Length:").getBytes();
    //private static final String CRLFCRLF = "\r\n\r\n";
	private CharsetDecoder m_charset = Charset.defaultCharset().newDecoder();
    public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
        try {// Return NEED_DATA if the whole header is not read yet. //DONE 要求一次性完成HTTP解析
            return isCompleted(in) ? MessageDecoderResult.OK : MessageDecoderResult.NEED_DATA;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return MessageDecoderResult.NOT_OK;
    }
    public MessageDecoderResult decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        HttpRequestMessage m = decodeBody(in);// Try to decode body
        if (m != null) {
	        out.write(m);
	        return MessageDecoderResult.OK;
        }else {// Return NEED_DATA if the body is not fully read.
        	return MessageDecoderResult.NEED_DATA;
        }
    }
    private boolean isCompleted(IoBuffer in) throws Exception {
        if (in.remaining() < 4)		return false;//至少需要包含CRLF+CRLF
        // to speed up things we check if the Http request is a GET or POST
        if (this.isStartWith(in, "GET")) {
            // Http GET request therefore the last 4 bytes should be 0x0D 0x0A 0x0D 0x0A
            return this.isEndWithCrlf2(in);
        } else if (this.isStartWith(in, "POST")) {//Http POST request
            // first the position of the 0x0D 0x0A 0x0D 0x0A bytes
            int eoh = this.parseHeadersLength(in);//TODO 原始代码为逆序搜索，可能存在BUG，修改。
            if (eoh == -1)	return false;	//EOH = End of Headers
            int clen = parseContentLength(in, eoh);
            return (eoh+clen == in.remaining());//如果一次到达多个报文？？不会（RPC模式？）
        }
        // the message is not complete and we need more data
        return false;
    }

    private HttpRequestMessage decodeBody(IoBuffer in) {
    	HttpRequestMessage request = new HttpRequestMessage();
        try {
            request.setHeaders(parseRequest(new StringReader(in.getString(m_charset))));
            return request;
        } catch (CharacterCodingException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private Map parseRequest(Reader is) {
        Map map = new HashMap();
        BufferedReader rdr = new BufferedReader(is);

        try {
            // Get request URL.
            String line = rdr.readLine();
            if (line==null)	{
            	return map;
            }
            String[] url = line.split(" ");
            if (url.length < 3)
                return map;

            map.put("URI", new String[] { line });
            map.put("Method", new String[] { url[0].toUpperCase() });
            map.put("Context", new String[] { url[1].substring(1) });
            map.put("Protocol", new String[] { url[2] });
            // Read header
            while (MyUtil.assertLength(line = rdr.readLine(), 1)) {
                String[] tokens = line.split(": ");
                map.put(tokens[0], new String[] { tokens[1] });
            }

            // If method 'POST' then read Content-Length worth of data
            if (url[0].equalsIgnoreCase("POST")) {
                int len = Integer.parseInt(((String[]) map
                        .get("Content-Length"))[0]);
                char[] buf = new char[len];
                if (rdr.read(buf) == len) {
                    line = String.copyValueOf(buf);
                }
            } else if (url[0].equalsIgnoreCase("GET")) {
                int idx = url[1].indexOf('?');
                if (idx != -1) {
                    map.put("Context", new String[] { url[1].substring(1, idx) });
                    line = url[1].substring(idx + 1);
                } else {
                    line = null;
                }
            }
            if (line != null) {
                String[] match = line.split("\\&");
                for (int i = 0; i < match.length; i++) {
                    String[] params = new String[1];
                    String[] tokens = match[i].split("=");
                    switch (tokens.length) {
                    case 0:
                        map.put("@".concat(match[i]), new String[] {});
                        break;
                    case 1:
                        map.put("@".concat(tokens[0]), new String[] {});
                        break;
                    default:
                        String name = "@".concat(tokens[0]);
                        if (map.containsKey(name)) {
                            params = (String[]) map.get(name);
                            String[] tmp = new String[params.length + 1];
                            for (int j = 0; j < params.length; j++)
                                tmp[j] = params[j];
                            params = null;
                            params = tmp;
                        }
                        params[params.length - 1] = tokens[1].trim();
                        map.put(name, params);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return map;
    }
    //-------------------
    private boolean isStartWith(IoBuffer in, String method) throws Exception {
    	char[] chars = method.toCharArray(); 
    	boolean ok = true;
    	for(int i=0; i<chars.length; i++) {
    		ok = (in.get(i) == (byte)chars[i]);
    		if (!ok)	break;
    	}
    	_log.debug("# isStartWith(%s) = %s", method, ok);
    	return ok;
    }
    private boolean isEndWithCrlf2(IoBuffer in) throws Exception {
    	boolean ok = this.isEndWithCrlf2(in, in.remaining() - 1);//最后一个字符。
    	_log.debug("# isEndWithCrlf2() = %s", ok);
    	return ok;
    }
    private boolean isEndWithCrlf2(IoBuffer in, int lastIdx) throws Exception {
    	int i = lastIdx;
    	boolean ok = (	in.get(i-3)==0x0D && in.get(i-2)==0x0A
    				&& in.get(i-1)==0x0D && in.get(i)==0x0A);
    	return	ok;
    }
    private int parseHeadersLength(IoBuffer in) throws Exception {
    	int last = in.remaining() - 1;	//最后一个字符。
        int eoh = -1;	//CRLF+CRLF结尾处，即Body的起始位置。
        for (int i=3; i<last; i++) {//最后位置
        	if (this.isEndWithCrlf2(in, i)){
                eoh = i + 1;
                break;
            }
        }
        _log.debug("# isContainCrlf2() = %s/contain:%s", eoh, (eoh!=-1));
        return eoh;
    }
    private int parseContentLength(IoBuffer in, int eoh) throws Exception {
    	int last = in.remaining() - 1;	//最后一个字符。
    	for (int i = 0; i < last; i++) {
            boolean found = false;//CONTENT_LENGTH
            for (int j = 0; j < CONTENT_LENGTH.length; j++) {
                if (in.get(i + j) != CONTENT_LENGTH[j]) {
                    found = false;
                    break;
                }
                found = true;
            }
            if (found) {
                // retrieve value from this position till next 0x0D 0x0A
                StringBuilder contentLength = new StringBuilder();
                for (int j = i + CONTENT_LENGTH.length; j < last; j++) {
                    if (in.get(j) == 0x0D)	break;
                    contentLength.append(new String(new byte[] { in.get(j) }));
                }
                // if content-length worth of data has been received then the message is complete
                return Integer.parseInt(contentLength.toString().trim());
            }
        }
        return -1;
    }
}
