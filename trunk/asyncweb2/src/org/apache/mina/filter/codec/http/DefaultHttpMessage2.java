package org.apache.mina.filter.codec.http;

import java.util.Map;
import java.util.TreeMap;

/**
 * ����DefaultHttpMessage��չ���ṩprotected����Ȩ�ޣ��޸�cookies�ṹ��
 * 	protected final Set<Cookie> cookies = new TreeSet<Cookie>(CookieComparator.INSTANCE);
 * 	protected final Map<String,Cookie> cookies = new TreeMap<String,Cookie>(HttpHeaderNameComparator.INSTANCE);
 * @author Liusheng
 *
 */
public class DefaultHttpMessage2 extends DefaultHttpMessage{
	private static final long serialVersionUID = 4915122328252892057L;
	protected final Map<String,Cookie> cookies = new TreeMap<String,Cookie>();
}
