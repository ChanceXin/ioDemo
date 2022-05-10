package tutorial.connector;

import tutorial.processor.ServletProcessor;
import tutorial.processor.StaticProcessor;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class Connector implements Runnable{
    private static final int DEFAULT_PORT = 8889;
    private ServerSocketChannel server;
    private Selector selector;
    private int port;
    public Connector(){
        this(DEFAULT_PORT);
    }
    public Connector(int port){
        this.port = port;
    }
    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }
    @Override
    public void run() {
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.socket().bind(new InetSocketAddress(port));
            selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("启动服务器，监听端口" + port);

            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                for (SelectionKey key : selectionKeySet) {
                    // 处理被触发的事件
                    handles(key);
                }
                selectionKeySet.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(server);
        }
    }

    private void handles(SelectionKey key) throws IOException {
        // ACCEPT
        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector,SelectionKey.OP_READ);
        } else {
            // READ
            SocketChannel client = (SocketChannel) key.channel();
            key.cancel(); //这条channel和select 就没有关系了
            client.configureBlocking(true);

            Socket clientSocket = client.socket();
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            Request request = new Request(inputStream);
            request.parse();

            Response response = new Response(outputStream);
            response.setRequest(request);
//            response.sendStaticResource();

            if (request.getRequestURI().startsWith("/servlet/")){
                ServletProcessor processor = new ServletProcessor();
                processor.process(request,response);
            } else {
                StaticProcessor processor = new StaticProcessor();
                processor.process(request,response);
            }
            close(client);
        }
    }

    private void close(Closeable closeable){
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
