package com.googlecode.asyncweb2.httpcodec;

import java.io.IOException;
import java.util.List;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.AbstractProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.http.HttpRequestDecoder;
import org.apache.mina.filter.codec.http.HttpResponseDecoder;
import org.apache.mina.filter.codec.statemachine.DecodingState;
import org.apache.mina.filter.codec.statemachine.DecodingStateProtocolDecoder;

import com.googlecode.asyncweb2.utils.MyLog;
import com.googlecode.asyncweb2.utils.MyUtil;

/** 
 * ���HttpCodec�����Ӷ��б������ܣ���ֹOOM�쳣
 * ����HttpCodecQΪHttpCodecFactory3Q���ο�HttpCodecFactory2�޸Ļ��档
 * �ô�HttpCodecFactory2�̳ж�����HttpCodec��
 */
public class HttpCodecFactory3Q extends HttpCodecFactory2 {
	private static final MyLog _log = MyLog.getLog(HttpCodecFactory3Q.class);
	//--------------
	@Override
	protected ProtocolDecoder newDecoder(IoSession session) {
    	if(session.getService() instanceof IoAcceptor) {
    		return new HttpRequestDecoder3();
        }else{
        	return new HttpResponseDecoder3();
        }
	}
	//--------------
	protected static class HttpRequestDecoder3 extends DecodingStateProtocolDecoder {
		public HttpRequestDecoder3() {
			super(new HttpRequestDecodingState3());
		}
	}
	protected static class HttpRequestDecodingState3 extends HttpRequestDecoder.HttpRequestDecodingState2 {
		@Override
        protected DecodingState finishDecode(List<Object> childProducts, ProtocolDecoderOutput out) throws Exception {
			decode_puts(out, childProducts);
            return null;
        }
	}
	//--------------
	public class HttpResponseDecoder3 extends DecodingStateProtocolDecoder {
		public HttpResponseDecoder3() {
			super(new HttpResponseDecodingState3());
		}
	}
	protected static class HttpResponseDecodingState3 extends HttpResponseDecoder.HttpResponseDecodingState2 {
		@Override
		protected DecodingState finishDecode(List<Object> childProducts, ProtocolDecoderOutput out) throws Exception {
			decode_puts(out, childProducts);
            return null;
        }
	}
	//--------------
	protected static int decode_queue_max() {//����ReadQueue����С��
		return MyUtil.getDecoderQueueMax();
	}
	protected static void decode_puts(ProtocolDecoderOutput out, List<Object> childProducts) throws IOException {
		if (childProducts==null || childProducts.size()==0)		return;	//����Ϊ0
		int qsize = 0, qsizeMax = decode_queue_max();
		if (out instanceof AbstractProtocolDecoderOutput) {
			qsize = ((AbstractProtocolDecoderOutput)out).getMessageQueue().size();
		}else {
			_log.warn("E.TYPE decode_puts(%d)...OUT=%s", childProducts.size(), out.getClass());
		}
		if (qsize>qsizeMax) {	//������out������
			_log.warn("E.DROP decode_puts(%d)...q=%d/%d,rest=%d,msg=%s", childProducts.size(), qsize, qsizeMax);
		}else {//�澯
			for (Object m: childProducts)	out.write(m);
			//out.flush();	//��mina2.0.0-m4�в���flush��������mina2.0.0-m4���ѱ�ȡ����
			if (qsize>0) {
				_log.debug("E.BUSY decode_puts(%d)...q=%d/%d", childProducts.size(), qsize, qsizeMax);
			}//else _log.debug("# decode_puts(%d)...q=%d/%d...Idle", childProducts.size(), qsize, qsizeMax);
		}
		//in.shrink();	//only 1/4 or less of the current capacity is being used.
		//_log.debug("### decode_puts(%d)...qsize=%d", childProducts.size(), ((AbstractProtocolDecoderOutput)out).getMessageQueue().size());
	}
	//--------------

}
