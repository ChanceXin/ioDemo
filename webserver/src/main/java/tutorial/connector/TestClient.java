package tutorial.connector;

import java.io.*;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost",8889);
        OutputStream outputStream = socket.getOutputStream();
//        outputStream.write("GET \\index.html HTTP/1.1".getBytes());
        outputStream.write("GET /servlet/TimeServlet HTTP/1.1".getBytes());
        socket.shutdownOutput();
        InputStream inputStream = socket.getInputStream();
        StringBuilder response = new StringBuilder();
        byte[] buffer = new byte[2048];
        int length = 0;
        // 一次是读不完的！！！！！
        while((length=inputStream.read(buffer))!=-1){
            for (int i = 0; i < length; i++) {
                response.append((char) buffer[i]);
            }
        }
        System.out.println(response.toString());
        socket.shutdownInput();
        socket.close();
    }
}
