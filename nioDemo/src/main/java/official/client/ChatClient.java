package official.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class ChatClient {
    private static final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private static final int DEFAULT_SERVER_PORT = 8888;
    private static final String QUIT = "quit";
    private static final int BUFFERSIZE = 1024;

    private int port;
    private String host;
    private SocketChannel client;
    private Charset charset = Charset.forName("UTF-8");
    private Selector selector;
    private ByteBuffer readBuffer = ByteBuffer.allocate(BUFFERSIZE);
    private ByteBuffer WeadBuffer = ByteBuffer.allocate(BUFFERSIZE);

    public ChatClient(){
        this(DEFAULT_SERVER_HOST,DEFAULT_SERVER_PORT);
    }

    public ChatClient(String host, int port){
        this.port = port;
        this.host = host;

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
            client = SocketChannel.open();
            client.configureBlocking(false);
            selector = Selector.open();
            client.register(selector, SelectionKey.OP_CONNECT);
            client.connect(new InetSocketAddress(host,port));
            while (true){
                selector.select();

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
