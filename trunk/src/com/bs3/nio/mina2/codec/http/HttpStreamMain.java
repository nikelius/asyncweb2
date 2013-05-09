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
/*
 * @see http://amozon.javaeye.com/blog/322528 Mina2.0 example HttpServer
 * MINA1.X��MINA2.0��������Ҫ����jdk1.4,1.5��jdk1.6����nio�ı仯�����м�����ǿ���IoBuffer��
 * 1.x�м��и�httpServer��������2.0���б����޷�ͨ�������ж���streamhandler��httpЭ���ʵ�ֻ��Ǻ��м�ֵ�ġ�
 * ������д�����ڿ��������ˡ�ʹ�����������http://localhost:8080/���Կ������н��
 */
import java.net.InetSocketAddress;
import javax.net.ssl.SSLContext;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.ssl.SslContextFactory;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * @see http://amozon.javaeye.com/blog/322528 Mina2.0 example HttpServer
 * (<b>Entry point</b>) HTTP server
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 (Fri, 13 Jul 2007) $
 */
public class HttpStreamMain {
    /** Choose your favorite port number. */
    private static final int PORT = 8080;

    private static final boolean USE_SSL = false;
    static class Test {
	    public static void main(String[] args) throws Exception {
	    	NioSocketAcceptor acceptor = new NioSocketAcceptor();
	    	// Create TCP/IP connector.
	        IoConnector connector = new NioSocketConnector();
	        // Set connect timeout.
	        connector.setConnectTimeoutMillis(30*1000L);
	        HttpStreamHandler handler = new HttpStreamHandler();
	        // Start proxy.
	        acceptor.setHandler(handler);
	        acceptor.bind(new InetSocketAddress(PORT));
	        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
	        ((SocketSessionConfig) connector.getSessionConfig()).setReuseAddress(false);
	        // Add SSL filter if SSL is enabled.
	        if (USE_SSL) {
	            addSSLSupport(chain);
	        }
	        System.out.println("Listening on port " + PORT);
	    }
	
	    private static void addSSLSupport(DefaultIoFilterChainBuilder chain) throws Exception {
	        System.out.println("SSL is enabled.");
	        SslContextFactory sslContextFactory = new SslContextFactory();
	        SSLContext sslContext = sslContextFactory.newInstance();
	        SslFilter sslFilter = new SslFilter(sslContext);
	        chain.addLast("sslFilter", sslFilter);
	    }
    }
}
