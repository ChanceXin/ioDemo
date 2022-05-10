package tutorial.connector;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class Response implements ServletResponse {
    /*
    HTTP/1.1 200 OK
    */
    private static final int BUFFER_SIZE = 2048;
    public void setRequest(Request request) {
        this.request = request;
    }


    Request request;
    OutputStream output;

    public Response(OutputStream output){
        this.output = output;
    }
    public void sendStaticResource() throws IOException {
        File file = new File(ConnectorUtils.WEB_ROOT,request.getRequestURI());
        try {
            write(file,HttpStatus.SC_OK);
        } catch (IOException e){
            write(new File(ConnectorUtils.WEB_ROOT,"404.html"),HttpStatus.SC_NOT_FOUND);
        }
    }
    private void write(File resource,HttpStatus status) throws IOException {
        try (FileInputStream fis = new FileInputStream(resource)){
            // http 状态信息
            output.write(ConnectorUtils.renderStatus(status).getBytes());
            byte[] buffer = new byte[BUFFER_SIZE];
            int length = 0;
            // 资源文件里的数据
            while ((length = fis.read(buffer,0,BUFFER_SIZE) )!= -1){
                output.write(buffer,0,length);
            }
            }
        }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        PrintWriter writer = new PrintWriter(output,true);
        return writer;
    }

    @Override
    public void setCharacterEncoding(String s) {

    }

    @Override
    public void setContentLength(int i) {

    }

    @Override
    public void setContentLengthLong(long l) {

    }

    @Override
    public void setContentType(String s) {

    }

    @Override
    public void setBufferSize(int i) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }
}
