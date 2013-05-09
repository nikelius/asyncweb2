package com.googlecode.asyncweb2.utils;
/****************************************************************
*	CopyLeft: ������GPL��Ȩ��ɣ�ͬʱ�����������ֺ�ע�ͣ�	*
*****************************************************************
 * Created on 2005-7-12
 * @author liusheng<nike.lius@gmail.com>
 * ��־�����࣬�������£�
 * 1����̬����log4j�����ļ���
 * 2������commons-log��slf4j���죬�����л���
 * 3������logMpsp()ҵ����־
 * 20090205 �޸ģ�UtilLog > MyLog
 * 20090205 �޸ģ�������commons-log��slf4j֮���л�
 * 20090205 �޸ģ��ο�play.Logger�����޸ģ�֧���Զ��������
 * 20090224 ���� logMpsp()ҵ����־
 * 20090313 ���÷���3��ֱ��ʹ��log4j��
 * @see http://zeroliu.iteye.com/blog/326595

public interface MyLogInf {  
    public void debug(String message, Object... args);  
    public void info(String message, Object... args);  
    public void warn(String message, Object... args);  
    public void error(Throwable e, String message, Object... args);  
    public boolean isDebugEnabled();//�������ڴ�ӡ����������Ϣ������debug()���Ѿ������赥��������ã�  
}  
 */


public class MyLog {  //implements MyLogInf
    public static MyLog getLog(Class clz)		{ return new MyLog(clz.getName()); }  
    public static MyLog getLog(String clz)		{ return new MyLog(clz);           }  
    static MyLog getLogger()         			{ return getLog(MyLog.class); 		}  
    //------------------ ����1  
//  private org.apache.commons.logging.Log _log = null;  
//  public MyLog(String clz) { _log = org.apache.commons.logging.LogFactory.getLog(clz);}  
    //------------------ ����2  
    private org.slf4j.Logger 	_log = null;  
    public MyLog(String clz) { _log = org.slf4j.LoggerFactory.getLogger(clz);}  
    //------------------  
    public void debug(String message, Object... args){  
        if (_log.isDebugEnabled())  _log.debug(String.format(message, args));  
    }  
    public void info(String message, Object... args) {  
        if (_log.isInfoEnabled())   _log.info(String.format(message, args));  
    }  
    public void warn(String message, Object... args){  
        if (_log.isWarnEnabled())   _log.warn(String.format(message, args));  
    }  
    public void error(Throwable e, String message, Object... args){  
        if (_log.isErrorEnabled())  _log.error(String.format(message, args), e);  
    }  
    //------------------  
    public boolean isDebugEnabled() { return _log.isDebugEnabled();    }  
}  