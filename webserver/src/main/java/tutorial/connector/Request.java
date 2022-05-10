package tutorial.connector;

import javax.servlet.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

public class Request implements ServletRequest {

   /* GET /index.html HTTP/1.1
        Host:localhost:8888
        Connection: keep-alive
        Cache-Control: max-age=0
        Upgrade-Insercure-Requests: 1
        User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.0.0 Safari/537.36
     */
    private static final int BUFFER_SIZE = 1024;
    private InputStream input;
    private String uri;
    public Request(InputStream input){
        this.input = input;
    }

    public String getRequestURI(){
        return uri;
    }

    public String parse(){ // 读字节流 存入Byte数组 转为字符串
        int length = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            length = input.read(buffer);// 返回Integer数值 表示读的字节数
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder requestStr = new StringBuilder();
        for (int j = 0; j < length; j++){
            requestStr.append((char) buffer[j]);
        }
        // 将读出的字符串解析出需要的数据
        uri = parseUri(requestStr.toString());
        return uri;
    }
//    public String parse2(){ // 利用java包直接将字符流转为字符串
//        String msg = null;
//        try {
//             BufferedReader reader =  new BufferedReader( new InputStreamReader(input));
//            msg = reader.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // 将读出的字符串解析出需要的数据
//        uri = parseUri(msg);
//        return uri;
//    }

    private String parseUri(String s) {
        int index1,index2;
        index1 = s.indexOf(' '); //找到第一个空格的位置
        if (index1 != -1){
            index2 = s.indexOf(' ',index1 +1); // 从第一个空格后面找的第一个空格
            if (index2 > index1){
                return s.substring(index1 + 1, index2);
            }
        }
        return "";
    }

    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getParameter(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String s) {
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return null;
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    @Override
    public String getRealPath(String s) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }
}
