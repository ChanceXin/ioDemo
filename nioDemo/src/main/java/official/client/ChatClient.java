package official.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;

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
    private ByteBuffer writeBuffer = ByteBuffer.allocate(BUFFERSIZE);

    public ChatClient(){
        this(DEFAULT_SERVER_HOST,DEFAULT_SERVER_PORT);
    }

    public ChatClient(String host, int port){
        this.port = port;
        this.host = host;

    }

    // 发送消息给服务器
    public void send(String msg) throws IOException {
        if (msg.isEmpty()){
            return;
        }
        writeBuffer.clear();
        writeBuffer.put(charset.encode(msg));
        writeBuffer.flip();
        while (writeBuffer.hasRemaining()){
            client.write(writeBuffer);
        }
        // 检查用于是否准备推出
        if (readyQuit(msg)){
            close(selector);
        }
    }

    public String receive(SocketChannel client) throws IOException {
        readBuffer.clear();
        while (client.read(readBuffer) > 0);
        readBuffer.flip();
        return String.valueOf(charset.decode(readBuffer));
    }

    public boolean readyQuit(String msg){
        return QUIT.equals(msg);
    }

    public void close(Closeable closeable){
        if (closeable != null){
            try {
                closeable.close();
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
                Set<SelectionKey> keySet = selector.selectedKeys();
                for (SelectionKey key : keySet){
                    handles(key);
                }
                // 清楚之前所有注册的事件
                keySet.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClosedSelectorException e){
            // 用户正常退出
        } finally {
            close(selector);
        }
    }

    private void handles(SelectionKey key) throws IOException {
        if (key.isConnectable()){
            SocketChannel client = (SocketChannel) key.channel();
            if (client.isConnectionPending()){
                // true 是连接成功 处于就绪态，调用isfinished完成连接
                client.finishConnect();
                // 连接成功后处理用户输入,和BIO一样没有什么改动
                new Thread(new UserInputHandler(this)).start();
            }
            // 连接成功后，channel 同时可以接收数据，所以需要注册Read事件
            client.register(selector,SelectionKey.OP_READ);
        }else if (key.isReadable()){
            SocketChannel client = (SocketChannel) key.channel();
            String msg = receive(client);
            if (msg.isEmpty()){
                // 服务器消息没发送过来,关最大的
                close(selector);
            }else {
                System.out.println(msg);
            }
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient("127.0.0.1",7777);
        chatClient.start();
    }
}
