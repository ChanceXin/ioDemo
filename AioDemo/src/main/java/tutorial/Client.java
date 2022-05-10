package tutorial;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Client {
    final String LOCALHOST = "localhost";
    final int DEFAULT_PORT = 8888;
    AsynchronousSocketChannel clientChannel;

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
        // 创建Channel
        try {
            clientChannel = AsynchronousSocketChannel.open();

            Future<Void> future = clientChannel.connect(new InetSocketAddress(LOCALHOST, DEFAULT_PORT));

            future.get();
            // 等待用户的输入
            BufferedReader consoleReader =
                    new BufferedReader(new InputStreamReader(System.in));
            while (true) {

                String input = consoleReader.readLine();
                byte[] inputBytes = input.getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(inputBytes);
                // Futuer是一个记录了返回了多少个字节

                Future<Integer> writeResult = clientChannel.write(buffer); // 写入通道，数组从buffer中读
                writeResult.get(); //没有返回结果会阻塞
                buffer.flip();

                // 读通道，写入buffer
                Future<Integer> readResult = clientChannel.read(buffer);

                readResult.get(); //没有返回结果阻塞
                String echo = new String(buffer.array());
                buffer.clear();
                System.out.println(echo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            close(clientChannel);
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
