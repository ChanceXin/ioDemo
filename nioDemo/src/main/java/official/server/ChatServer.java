package official.server;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChatServer {

    private static int DEFAULT_PORT = 8888;
    private static final String QUIT = "quit";
    private static final int BUFFER = 1024;

    // 与bio不同处
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    // 读取从客户端发送到serversocketChannel里的buffer
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    // 写道其他客户端socketChannel转发信息的 buffer
    private ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
    //解码
    private Charset charset = Charset.forName("UTF-8");
    private int port;

    public ChatServer(){
        this(DEFAULT_PORT);
    }

    public ChatServer(int port) {
        this.port = port;
    }

    public  void forwardMessage(SocketChannel client, String fwdMsg) throws IOException, InterruptedException {
        for (SelectionKey key : selector.keys()){
            Channel connectedClient = key.channel();
            if (connectedClient instanceof ServerSocketChannel){
                continue;
            }
            if (key.isValid() && !key.channel().equals(client)){
                Thread.sleep(5000);
                System.out.println("转发了一次");
                writeBuffer.clear();
                writeBuffer.put(charset.encode(getClientName(client) + fwdMsg));
                writeBuffer.flip();
                while (writeBuffer.hasRemaining()){
                    // 调用强转后的对象 要加一层括号
                    ((SocketChannel)connectedClient).write(writeBuffer);
                }
            }
        }
    }

    public void start(){
        try {
            serverSocketChannel = ServerSocketChannel.open();
            // 设置为非阻塞式
            serverSocketChannel.configureBlocking(false);
            // 绑定接受连接的端口
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            // 启动selector，并注册ServerSocketChannel需要被监听的状态
            // 最后总结一下Selector.open()干了啥：
            //主要完成建立Pipe，并把pipe的读写文件描述符放入pollArray中,这个pollArray是Selector的枢纽。Linux下则是直接使用系统的pipe。
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务器端口【"+ port+"】已开启：");

            while (true){
                // 该行为会阻塞,通过循环监听多个状态的变化。
                selector.select();
                // 接受所有状态改变的事件keys
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey key : selectionKeys){
                    // 处理被处罚的事件
                    handles(key);
                }
                // 清空所有处理过的keys。
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭selector 会关闭 selector上注册的channel
            close(selector);
        }
    }

    private void handles(SelectionKey key) throws IOException, InterruptedException {
        // ACCEPT事件 - 和客户端建立了连接
        if (key.isAcceptable()){
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            // 同Bio 类似通过server的accept 返回 client
            SocketChannel client = serverSocketChannel.accept();
            client.configureBlocking(false);
            // 服务器和客户端建立连接后，就会发生数据传输，所以监听Read事件
            client.register(selector,SelectionKey.OP_READ);
            System.out.println(getClientName(client) + "已连接");
        } else if (key.isReadable()){
            // READ事件 - 客户端发送了消息
            SocketChannel client  = (SocketChannel) key.channel();
            String fwdMsg = receive(client);
            if (fwdMsg.isEmpty()){
                System.out.println(getClientName(client) + "发送字符为空 异常");
                key.cancel();
                selector.wakeup(); // 强制返回阻塞调用selector,重新检测一遍所有channel的状态。
            }else {
                if (readQuit(fwdMsg)){
                    key.cancel();
                    selector.wakeup();
                    System.out.println(getClientName(client) + "已断开");
                }
                forwardMessage(client,fwdMsg);
            }
        }
    }

    private String getClientName(SocketChannel client) {
        return "客户端 【" + client.socket().getPort() + "】";
    }

    private String receive(SocketChannel client) throws IOException {
        readBuffer.clear();// 类变量，每次先清空一下再使用
        // 没有括号，直接while完
        while (client.read(readBuffer) > 0); // 与fileChannel 的-1标志不同
        readBuffer.flip();
        return String.valueOf(charset.decode(readBuffer));
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
}
