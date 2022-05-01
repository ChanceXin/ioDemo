package official.client;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class ChatClient {
    private final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private final int DEFAULT_SERVER_PORT = 8888;
    private final String QUIT = "quit";
//    public static Charset charset = Charset.forName("UTF-8");
//
//    public static CharsetEncoder encoder = charset.newEncoder();
//
//    public static CharsetDecoder decoder = charset.newDecoder();

    private Socket socket;
    private SocketChannel socketChannel;

    public ChatClient(){

    }

    // 发送消息给服务器
    public void send(ByteBuffer buffer) throws IOException {
        if (buffer != null){

            if (socketChannel.isOpen()){
                //将buffer发给服务器
            }
        }
    }

    public String receive() throws IOException {
        String msg = "";
        if (!socketChannel.isOpen()){
            // 如果没读到 可能是因为没有 \n
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while (socketChannel.read(buffer) != -1){
                while (buffer.hasRemaining()){
                    //将buffer字符解析出来
                }
            }
        }
        return msg;
    }

    public boolean readyQuit(String msg){
        return QUIT.equals(msg);
    }

    public void close(){
        if (writer != null){
            try {
                System.out.println("关闭socket");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start()  {
        try {
            socket = new Socket(DEFAULT_SERVER_HOST,DEFAULT_SERVER_PORT);
            socketChannel = socket.getChannel();

            // 处理用户的输入

            // 读取服务器转发的消息
            String msgbuffer = null;
            while ((msg = receive()) != null){
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
                close();
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start();
    }
}
