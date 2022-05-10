package official;

import tutorial.Client;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static final String LOCALHOST = "localhost";
    private static int DEFAULT_PORT = 8888;
    private static final String QUIT = "quit";
    private static final int BUFFER = 1024;
    private static final int THREADPOOL_SIZE = 8;
    private AsynchronousChannelGroup channelGroup;
    private AsynchronousServerSocketChannel serverChannel;
    private List<ClientHandler> connectedClients;

    //解码
    private Charset charset = Charset.forName("UTF-8");
    private int port;

    public ChatServer(){
        this(DEFAULT_PORT);
    }

    public ChatServer(int port) {
        this.port = port;
        this.connectedClients = new ArrayList<>();
    }

    public  void forwardMessage(AsynchronousSocketChannel client, String fwdMsg) throws IOException, InterruptedException {

    }

    public void start(){
        try {
            // 创建线程池
            ExecutorService executorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
            // 创建channelgroup
            channelGroup = AsynchronousChannelGroup.withThreadPool(executorService);
            // 用自定义的ChannelGroup创建serverChannel
            serverChannel = AsynchronousServerSocketChannel.open(channelGroup);
            // 绑定端口
            serverChannel.bind(new InetSocketAddress(LOCALHOST,port));
            System.out.println("服务器端口【"+ port+"】已开启：");

            while (true){
                serverChannel.accept(null,new AcceptHandler());
                System.in.read();// 避免过于频繁的轮询，浪费cpu资源。
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭selector 会关闭 selector上注册的channel
            close(serverChannel);
        }
    }



    private String getClientName(AsynchronousSocketChannel client) {
        return "客户端 【" + client + "】";
    }

    private String receive(ByteBuffer client) throws IOException {
        return null;
    }

    private void close(Closeable closeable){
        if (closeable != null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 没有更新和 读取 内存中的变量 所以本身就是线程安全
    public boolean readQuit(String msg) {
        return QUIT.equals(msg);
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer(7777);
        // 注意这里，对象调用函数 ，其实默认将自己本身作为参数传递了进去，在方法里用 this就可以调用
        chatServer.start();
    }

    private class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel,Object>{// AsynchronousSocketChannel指的是accept 接受的那个客户端的Channel
        @Override
        public void completed(AsynchronousSocketChannel clientChannel, Object attachment) {
            if (serverChannel.isOpen()) {
                serverChannel.accept(null,this);
            }
            if (clientChannel != null && clientChannel.isOpen()){
                ClientHandler handler = new ClientHandler(clientChannel); // TODO 处理接收的客户端的handler
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER);
                // TODO 将新用户添加到在线用户列表
                addClient(handler);
                clientChannel.read(buffer,buffer,handler);// ByteBuffer,Attachment,CompeletionHandler

            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            // 业务逻辑
            System.out.println("连接失败" + exc);
        }
    }

    private void addClient(ClientHandler handler) {
    }

    private class ClientHandler implements CompletionHandler<Integer,Object>{
        AsynchronousSocketChannel clientChannel;
        public ClientHandler(AsynchronousSocketChannel clientChannel) {
            this.clientChannel = clientChannel;
        }

        @Override
        public void completed(Integer result, Object attachment) {
            ByteBuffer buffer = (ByteBuffer) attachment;
            if (buffer != null) {
                // 客户端异常
                // TODO 将客户移除出在线客户列表,因为handler有channel对象，所有直接存handler
            } else {
                buffer.flip();
//                String fwdMsg = receive(buffer);
//                System.out.println(getClientName(clientChannel) + fwdMsg);
//                forwardMessage(clientChannel,fwdMsg);
                buffer.clear();

//                if (readQuit(fwdMsg)){}

            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {

        }
    }
}

