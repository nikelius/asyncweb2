package com.googlecode.asyncweb2.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.channels.Channel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class MyUtil {
	private static final MyLog _log = MyLog.getLog(MyUtil.class);
	//----------------
	private static final String Key_DecoderQueueMax = "global.decoder.queue.max";
	public static void setDecoderQueueMax(int vNew) {
		int vOld = getDecoderQueueMax();
		if (vNew==vOld)		return;	//�Ѿ�һ�£���������
		if (vNew<=0)		vNew = 999;
		System.setProperty(Key_DecoderQueueMax, Integer.toString(vNew));
		_log.debug("# setDecoderQueueMax()...%d => %d", vOld, vNew);
	}
	public static int getDecoderQueueMax() {//�޸�ȱʡֵ99ΪInteger.MAX_VALUE
	//	return MyUtil.getMapValue(System.getProperties(), Key_DecoderQueueMax, 99);
	//	return MyUtil.getMapValue(System.getProperties(), Key_DecoderQueueMax, Integer.MAX_VALUE);
		return MyUtil.getMapValue(System.getProperties(), Key_DecoderQueueMax, 9999);
	}
	public static int getMapValue(Map<?,?> mmap, String objId, int defValue) {
		Object obj = mmap.get(objId);
		if (obj == null)	return defValue;
		return Integer.parseInt((String) obj);
	}

	//----------------

	public static String int2id(int id)		{ return String.format("%08X", id);	}

	public static boolean assertLength(String str, int minLength){
		if (str==null)							return false;
		if (str.length()<Math.max(minLength,1))	return false;
		return true;		
	}
	
	public static String strDateTime(String fmt, Date dateTime) {	//����1
		if (fmt==null || fmt.length()==0)	fmt = "yyyy-MM-dd HH:mm:ss.SSS";
		if (dateTime == null)	dateTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(fmt);
		return formatter.format(dateTime);
	}
	public static String strDateTime(String fmt, long dateTime) {	//����2
	//	if (fmt==null || fmt.length()==0)	fmt = "yyyyMMddHHmmssSSS";
		if (fmt==null || fmt.length()==0)	fmt = "yyyy-MM-dd HH:mm:ss.SSS";
		if (dateTime<=0)	dateTime = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
		sdf.applyPattern(fmt);//"yyyy��MM��dd�� HHʱmm��ss��"
		return sdf.format(dateTime);
	}
	
	public static boolean writeBinary(String fn, byte[] inBuf) {
		return writeBinary(fn, inBuf, inBuf.length, true);
	}
	public static boolean writeBinary(String fn, byte[] inBuf, int inLen) {
		return writeBinary(fn, inBuf, inLen, true);
	}
	public static boolean writeBinary(String fn, byte[] inBuf, int inLen, boolean bOverWrite) {
		if (!bOverWrite) {
			File f = new File(fn);
			if (f.exists()) {
				_log.debug("W: %s...FAIL/exists", fn);
				return false;
			}
		}
		BufferedOutputStream fos = null;
		boolean ok = false;
		try{
			fos = new BufferedOutputStream(new FileOutputStream(fn));
			fos.write(inBuf, 0, inLen);
			fos.flush();
			ok = true;
			_log.info("W: %s...%dB", fn, inLen);
		}catch(IOException e) {
			_log.error(e, "E writeBinary(%s) %s", fn, e.toString());
		}finally{
			MyUtil.close(fos, null);
		}
		return ok;
	}
	public static void close(Object closable, String info) {
		if (closable==null)	return;
		try {
			if (closable instanceof OutputStream) {
				OutputStream ostream = (OutputStream)closable;
				ostream.flush();
				ostream.close();				
			}else if (closable instanceof Writer) {
				Writer writer = ((Writer)closable);
				writer.flush();
				writer.close();
			}else if (closable instanceof Socket)	{
				Socket sock = (Socket)closable;
				if (sock.isConnected() && !sock.isClosed())	{ //@see Util.close()
					sock.shutdownOutput();
					MyUtil.sleepMSec(15, info+"#shutdownOutput()");
					sock.shutdownInput();
					sock.close();
				}
			}else if (closable instanceof Connection)	{
				Connection conn = (Connection)closable;
				//E close(JDBC4Connection) java.sql.SQLException: Can't call commit when autocommit=true
				if (! conn.isClosed()) {
					if (!conn.getAutoCommit()){
						try{ conn.commit(); }catch(Exception e){/*NULL*/}
					}
					conn.close();//ȷ���ر�
				}
            }else if (closable instanceof Statement)	{
                ((Statement)closable).close();
            }else if (closable instanceof ResultSet)	{
                ((ResultSet)closable).close();
			}else if (closable instanceof Channel) {
				((Channel)closable).close();
			}else {
				MyUtil.invoke(closable, "close");//������ã��ٶȱȽ�����
			}
			//_log.debug("* %s...closed", closable.getClass().getSimpleName());
			if (info!=null)	_log.debug("* close(%s)...ok %s", closable, info);
			
		}catch(Exception e){
			_log.error(e, "E close(%s) %s", closable.getClass().getSimpleName(), e);
		}
	}
	public static void sleepMSec(long msec, String info) {
		try {
			if (info!=null || msec>15000) {//if (msec>=1000) {//
				_log.debug("# sleepMSec(%d)...%s", msec, info);
			}
			Thread.sleep(msec);
		} catch (InterruptedException e) { /* ����*/	}
	}
	public static Object invoke(Object target, String methodname) throws Exception {
		Object resp = MyUtil.invoke(target, methodname, new Object[0], true);
		return resp;
	}
	/** ֧�ֶ��������֧��int�Ȼ������ͣ�
	 * Object invoke(Object target, String methodname, Object[] args);//ȱ�㣬�޷�����int��Integer�����ܵ���ʧ�ܡ�
	 * Object invoke(Object target, String methodname, Object[] args, boolean useOriginType);//֧��int�Ȼ������͡�
	 */
	public static Object invoke(Object target, String methodname, Object[] args, boolean useOriginType)
	throws NoSuchMethodException, InvocationTargetException,IllegalAccessException {
		if (target == null)		throw new IllegalArgumentException("target=null");
		if (methodname == null)	throw new IllegalArgumentException("method=null");
		Class<?>[] argsType = new Class<?>[args.length];// ��������
		for(int i = 0; i < args.length; i++) {
			argsType[i] = args[i].getClass();
			if (useOriginType) {//֧��int/long/boolean�Ȼ������͡�
				if (args[i] instanceof Integer)			argsType[i] = int.class;
				else if (args[i] instanceof Long)		argsType[i] = long.class;
				else if (args[i] instanceof Short)		argsType[i] = short.class;
				else if (args[i] instanceof Character)	argsType[i] = char.class;
				else if (args[i] instanceof Boolean)	argsType[i] = boolean.class;
				else if (args[i] instanceof Byte)		argsType[i] = byte.class;
				else if (args[i] instanceof Float)		argsType[i] = float.class;
				else if (args[i] instanceof Double)		argsType[i] = double.class;
			}			
		}
		Method method = getAnyMethod(target.getClass(), methodname, argsType);
		method.setAccessible(true);//����IllegalAccessException�쳣(can not access a member of class)
		Object resp = method.invoke(target, args);
		return resp;
	}
	public static Method getAnyMethod(Class<?> targetClz, String methodname, Class<?>[] argsType)
	throws SecurityException, NoSuchMethodException {
		Method method = null;
		try {
			method = targetClz.getMethod(methodname, argsType);//ֻ�ܻ�ȡpublic������
		}catch(java.lang.NoSuchMethodException e) {
			method = targetClz.getDeclaredMethod(methodname, argsType);//ֻ�ܻ�ȡ��public������
		}
		if (method==null)	{
			throw new NoSuchMethodException("getAnyMethod==null");
		}
		method.setAccessible(true);//20090821 ����IllegalAccessException�쳣
		return method;
	}

	// bcd�������㷨��ԭ�㷨��Ϊbcd0
	private static final String bcd_table = "0123456789ABCDEF";
	public static String bcd(byte[] bin, int off, int len) {
		if (off > bin.length)	return null;
		len = Math.min(len, bin.length - off);
		char[] cbuf = new char[len * 2];
		for (int i = off; i < off + len; i++) {
			int x = (i - off) * 2;
			cbuf[x] 	= bcd_table.charAt(bin[i] >>> 4 & 0xf);
			cbuf[x+1] 	= bcd_table.charAt(bin[i] & 0xf);
			//_log.debug("%s:%s", cbuf[x], cbuf[x+1]);
		}
		// _log.debug("/%d", cbuf.length);
		return new String(cbuf);
	}
	public static byte[] readFully(InputStream is, boolean buffered, int bufSize) throws IOException {
		//����cLen==-1�����⣨��Content-Length�������������Ϊֹ��
		bufSize = Math.max(bufSize, 8192);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (buffered) is = new BufferedInputStream(is, bufSize);//Ϊ������ٶ�
		byte[] buf = new byte[bufSize];
		do {
			int rBytes = is.read(buf, 0, buf.length);//������
			if (-1 == rBytes)	break; // ֱ��������Ϊֹ������
			baos.write(buf, 0, rBytes); // д�룻
		} while (true);
		return baos.toByteArray();	
	}
	public static byte[] readFully(InputStream is, int size) throws IOException {
//		return readFully_0(is, size);
		return readFully_2(is, size);
	}
	static byte[] readFully_2(InputStream is, int size) throws IOException {
		if (size<0)	throw new IOException("readFully_2(size<0))");//δ����size<0
		byte[] out = new byte[size];
		if (size==0)	return out;	//@return byte[0]
		int	n, offset=0;
		do {//ע�⣬MyUtil.readFully����δ����cLen==-1�����⣨��Content-Length�
			n = is.read(out, offset, size-offset);
			if (n==-1)	throw new IOException("readFully_2.read()...EOF");
			offset += n;
		}while(offset < size);	
		return out;//@return byte[0]
	}


}
