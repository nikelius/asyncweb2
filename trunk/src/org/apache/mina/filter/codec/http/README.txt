--------------------
http://wutaoo.javaeye.com/blog/373640 ʵ��server���衣��ͷѧϰһ��netty�Դ���һ��http serverʵ�֡�
http://amozon.javaeye.com/blog/322528 Mina2.0 example HttpServer
����.����������mina2��ͨ��filter-codec-nettyʹ��netty�Ľ�������
--------------------
�µĵ�ַSVN��ַ
http://trac-hg.assembla.com/scala-mina/browser/filter-codec-http/src/main/java/org/apache/mina/filter/codec/http?rev=31:9fe1c96b96dc
http://trac-hg.assembla.com/scala-mina/browser
--------------------
��1��src_asyncweb_apache_mina1_0.rar	������asyncweb-common-0.9.0-SNAPSHOT.jar����֧��mina2.0m2��
��2��src_asyncweb_apache_mina1_1.rar	�޸� org\apache\asyncweb\common �Ա�֧��mina2.0m3+ (δ�꣬��HttpVersionʱ����Enumʧ��) 
��3��src_asyncweb_apache_mina2_0.rar	��ȡ org\apache\mina\filter\codec\http
��4��src_asyncweb_apache_mina2_1.rar	�޸ģ�֧��mina2.0m3+ �� ���������
�޸�DefaultCookie�࣬����public DefaultCookie(String name, String value)���췽����
�޸�import����org.apache.mina.common.IoBuffer��org.apache.mina.core.buffer.IoBuffer�滻��
�޸�import����org.apache.mina.common.IoSession��org.apache.mina.core.session.IoSession�滻��
�޸�import����org.apache.mina.common.IoAcceptor��org.apache.mina.core.service.IoAcceptor�滻��
��5��src_asyncweb_apache_mina2_2.rar	֧��Play.Server.HttpHandler
�޸ģ�MutableHttpRequest�ӿڣ��������·�����
	public abstract File getFileContent();
	public abstract void setFileContent(File paramFile);
�޸ģ�DefaultHttpMessage�����࣬�������·�����
    private File fileContent;
    public File getFileContent() { return this.fileContent;  }
    public void setFileContent(File fileContent) { this.fileContent = fileContent; }
��6��src_asyncweb_apache_mina2_3.rar	֧�� ContinuationSupport ����
�޸ģ�MutableHttpRequest�����࣬�������·�����
    public Object getAttribute(String name, Object defautValue);
    public Object setAttribute(String name, Object newValue);
�޸ģ�DefaultHttpRequest�����࣬ʵ�����·�����
	private Map<String, Object> attributes = new TreeMap<String, Object>();//Lius Add
	public Object getAttribute(String name, Object defautValue){
	public Object setAttribute(String name, Object newValue){
�޸ģ�DefaultHttpMessage�࣬�������Ȩ�ޣ�����IHttp.Response�̳У�
    protected HttpVersion protocolVersion = HttpVersion.HTTP_1_1;
    protected final Map<String, List<String>> headers = new TreeMap<String, List<String>>(HttpHeaderNameComparator.INSTANCE);
    protected final Set<Cookie> cookies = new TreeSet<Cookie>(CookieComparator.INSTANCE);
    protected transient IoBuffer content = IoBuffer.allocate(0);
	
--------------------
��1����ȡ���ڣ�asyncweb-common-0.9.0-SNAPSHOT.jar ����֧��mina2.0.2-��
1���� Play����л�ȡ��
2��ֱ������jar�ļ�
http://m2.safehaus.org/org/safehaus/asyncweb/asyncweb-core/
 0.8.3-SNAPSHOT/         02-Nov-2006 02:53      -  
 0.9.0-SNAPSHOT/         13-Jun-2007 22:41      -  
http://m2.safehaus.org/org/safehaus/asyncweb/asyncweb-core/0.9.0-SNAPSHOT/
 asyncweb-core-0.9.0-..> 29-Nov-2006 02:30   154k  
 asyncweb-core-0.9.0-..> 05-Dec-2006 02:30   155k  
 asyncweb-core-0.9.0-..> 05-Dec-2006 02:37   155k  
 asyncweb-core-0.9.0-..> 06-Dec-2006 03:41   154k  
 asyncweb-core-0.9.0-..> 13-Dec-2006 04:06   154k  
 asyncweb-core-0.9.0-..> 29-Jan-2007 20:31   154k  
 asyncweb-core-0.9.0-..> 13-Jun-2007 22:41   154k  
3������mina-filter-codec-http�����Ϣ
* Moved AsyncWeb HTTP codec to mina-filter-codec-http
* Rewrote the existing HTTP client codec
* ProtocolCodecFactory.getEncoder/getDecoder() now are provided with an IoSession parameter.
http://svn.apache.org/viewvc/mina/trunk/filter-codec-http/src/main/java/org/apache/mina/filter/codec/http/?pathrev=612014
--- Index of /mina/trunk/filter-codec-http/src/main/java/org/apache/mina/filter/codec/http
--- Files shown: 33  
--- Directory revision: 612014 (of 739911) 
--- Sticky Revision: (Current path doesn't exist after revision 615465)  
----File   Rev  Age  Author  Last log entry  
 Parent Directory          
 ChunkedBodyDecodingState.java   606112  13 months  trustin  Fixed ChunkedBodyDecodingState fails to decode a chunk when there's a whitespace... 
 Cookie.java   596187  14 months  trustin  * Moved AsyncWeb HTTP codec to mina-filter-codec-http * Rewrote the existing HTT... 
 CookieComparator.java   596189  14 months  trustin  Updated author and version tags 
 DefaultCookie.java   596189  14 months  trustin  Updated author and version tags 
 DefaultHttpMessage.java   596260  14 months  trustin  Made sure constants are reused in mina-filter-codec-http 
 DefaultHttpRequest.java   596271  14 months  trustin  * Removed too trivial constants * More constant variable utilization  
 DefaultHttpResponse.java   596187  14 months  trustin  * Moved AsyncWeb HTTP codec to mina-filter-codec-http * Rewrote the existing HTT... 
 HttpCodecFactory.java   602847  13 months  trustin  Another silly mistake.. swapped response encoder and request encoder :-( 
 HttpCodecUtils.java   596271  14 months  trustin  * Removed too trivial constants * More constant variable utilization  
 HttpDateFormat.java   596260  14 months  trustin  Made sure constants are reused in mina-filter-codec-http 
 HttpHeaderConstants.java   596271  14 months  trustin  * Removed too trivial constants * More constant variable utilization  
 HttpHeaderDecodingState.java   612014  12 months  trustin  Related issue: DIRMINA-505 (OOM errors when handling badly formed HTTP requests)... 
 HttpHeaderNameComparator.java   596190  14 months  trustin  Updated the access modifiers of the classes 
 HttpMessage.java   596187  14 months  trustin  * Moved AsyncWeb HTTP codec to mina-filter-codec-http * Rewrote the existing HTT... 
 HttpMethod.java   596189  14 months  trustin  Updated author and version tags 
 HttpRequest.java   596187  14 months  trustin  * Moved AsyncWeb HTTP codec to mina-filter-codec-http * Rewrote the existing HTT... 
 HttpRequestDecoder.java   601996  13 months  trustin  * Renamed ConsumeToDisconnectionDecodingState to ConsumeToEndOfSessionDecodingSt... 
 HttpRequestDecoderException.java   596189  14 months  trustin  Updated author and version tags 
 HttpRequestDecodingState.java   612014  12 months  trustin  Related issue: DIRMINA-505 (OOM errors when handling badly formed HTTP requests)... 
 HttpRequestEncoder.java   597288  14 months  trustin  Made sure HttpRequestEncoder calls MutableHttpRequest.normalize. 
 HttpRequestLineDecodingState.java   596260  14 months  trustin  Made sure constants are reused in mina-filter-codec-http 
 HttpResponse.java   596187  14 months  trustin  * Moved AsyncWeb HTTP codec to mina-filter-codec-http * Rewrote the existing HTT... 
 HttpResponseDecoder.java   601996  13 months  trustin  * Renamed ConsumeToDisconnectionDecodingState to ConsumeToEndOfSessionDecodingSt... 
 HttpResponseDecodingState.java   602785  13 months  trustin  Added small sanity check to keep the decoder from logging unnecessary exceptions 
 HttpResponseEncoder.java   596271  14 months  trustin  * Removed too trivial constants * More constant variable utilization  
 HttpResponseLineDecodingState.java   596260  14 months  trustin  Made sure constants are reused in mina-filter-codec-http 
 HttpResponseStatus.java   596189  14 months  trustin  Updated author and version tags 
 HttpVersion.java   596189  14 months  trustin  Updated author and version tags 
 HttpVersionDecodingState.java   602839  13 months  trustin  * Improved HttpVersionDecodingState to throw ProtocolDecoderException even if it... 
 MutableCookie.java   596189  14 months  trustin  Updated author and version tags 
 MutableHttpMessage.java   596189  14 months  trustin  Updated author and version tags 
 MutableHttpRequest.java   596279  14 months  trustin  Added more JavaDoc to avoid confusion related with normalization. 
 MutableHttpResponse.java   596187  14 months  trustin  * Moved AsyncWeb HTTP codec to mina-filter-codec-http * Rewrote the existing HTT... 
--------------------------------------------------------------------------------
��������ֻ���mina1�汾
http://svn.apache.org/viewvc/mina/branches/1.0/example/src/main/java/org/apache/mina/example/httpserver/
index of /mina/branches/1.0/example/src/main/java/org/apache/mina/example/httpserver
Files shown:	0
Directory revision:	555855 (of 721370)
Sticky Revision:	   
File  	Rev	Age	Author	Last log entry
  Parent Directory	 	 	 	 
  codec/	 555855	 16 months	 trustin	 Resolved issue: DIRMINA-378 (Reformat code using new coding conventions) * Refor...
  stream/	 555855	 16 months	 trustin	 Resolved issue: DIRMINA-378 (Reformat code using new coding conventions) * Refor...
-------------------------------------------------------------------------------
��������ֻ���mina1�汾
http://svn.apache.org/viewvc/mina/branches/1.0/example/src/main/java/org/apache/mina/example/httpserver/codec/
File  	Rev	Age	Author	Last log entry
  Parent Directory	 	 	 	 
  HttpRequestDecoder.java	 555855	 18 months	 trustin	 Resolved issue: DIRMINA-378 (Reformat code using new coding conventions) * Refor...
  HttpRequestMessage.java	 555855	 18 months	 trustin	 Resolved issue: DIRMINA-378 (Reformat code using new coding conventions) * Refor...
  HttpResponseEncoder.java	 555855	 18 months	 trustin	 Resolved issue: DIRMINA-378 (Reformat code using new coding conventions) * Refor...
  HttpResponseMessage.java	 555855	 18 months	 trustin	 Resolved issue: DIRMINA-378 (Reformat code using new coding conventions) * Refor...
  HttpServerProtocolCodecFactory.java	 555855	 18 months	 trustin	 Resolved issue: DIRMINA-378 (Reformat code using new coding conventions) * Refor...
  Server.java	 		555855	 18 months	 trustin	 Resolved issue: DIRMINA-378 (Reformat code using new coding conventions) * Refor...
  ServerHandler.java	 	555855	 18 months	 trustin	 Resolved issue: DIRMINA-378 (Reformat code using new coding conventions) * Refor...
  package.html	 		476545	 2 years	 rooneg	 Move Mina's branches into its new home.
http://svn.apache.org/viewvc/mina/branches/1.0/example/src/main/java/org/apache/mina/example/httpserver/stream/
File  	Rev	Age	Author	Last log entry
  Parent Directory	 	 	 	 
  HttpProtocolHandler.java	555855	 18 months	 trustin	 Resolved issue: DIRMINA-378 (Reformat code using new coding conventions) * Refor...
  Main.java	 		555855	 18 months	 trustin	 Resolved issue: DIRMINA-378 (Reformat code using new coding conventions) * Refor...
  package.html	 		476545	 2 years	 rooneg	 Move Mina's branches into its new home.
-------------------------------------------------------------------------------
