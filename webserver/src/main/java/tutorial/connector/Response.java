package tutorial.connector;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Response {
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

}
