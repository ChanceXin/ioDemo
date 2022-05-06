package tutorial;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;

public class Server {

    final String LOCALHOST = "localhost";
    final int DEFAULT_PORT = 8888;
    AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
                System.out.println("关闭" + closeable);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void start() {
        // 绑定端口
        try {
            // AsynchronousChannelGroup 类似一个线程池
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(LOCALHOST,DEFAULT_PORT));
            System.out.println("启动服务器，监听端口：" + DEFAULT_PORT);
            while (true) {
                // attachment 可以用于携带一些额外的信息，第二个参数就是回调处理的对象
                asynchronousServerSocketChannel.accept(null, new AcceptHandler());
                // 只是保证主线程不太快返回的应对措施，在这里进行一个阻塞
                System.in.read();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(asynchronousServerSocketChannel);
        }
    }
    public static void main(String[] args) {
        Server server = new Server();
        server.start();

    }
    // AcceptHandler  返回一个Channel
    private class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {
        @Override
        public void completed(AsynchronousSocketChannel result, Object attachment) {
            if (asynchronousServerSocketChannel.isOpen()) {
                // 底层限制了accpet的栈递归调用
                asynchronousServerSocketChannel.accept(null,this);
            }
            AsynchronousSocketChannel clientChannel = result;
            if (clientChannel != null && clientChannel.isOpen()) {
                // 处理客户端通道 读写的 ClientHandler
                ClientHandler handler = new ClientHandler(clientChannel);

                ByteBuffer buffer = ByteBuffer.allocate(1024);
                Map<String,Object> info = new HashMap<>();
                info.put("type","read");
                info.put("buffer",buffer);
                clientChannel.read(buffer, info, handler);
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            // 处理错误
        }
    }

    private class ClientHandler implements CompletionHandler<Integer,Object>{
        private AsynchronousSocketChannel clientChannel;

        public ClientHandler(AsynchronousSocketChannel clientChannel) {
            this.clientChannel = clientChannel;
        }
        @Override
        public void completed(Integer result, Object attachment) {
            Map<String,Object> info = (Map<String, Object>) attachment;
            String type = (String) info.get("type");
            if ("read".equals(type)){
                ByteBuffer buffer = (ByteBuffer) info.get("buffer");
                // 再讲数据转出去
                buffer.flip();
                info.put("type","write");
                clientChannel.write(buffer,info,this);
                buffer.clear();
            } else if ("write".equals(type)) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                info.put("type","read");
                info.put("buffer",buffer);
                clientChannel.read(buffer, info, this);
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {

        }
    }
}


