package official.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ChatHandler  {
    private ChatServer server;
    private SocketChannel socketChannel;

    public ChatHandler(ChatServer server,SocketChannel socketChannel){
        this.server = server;
        this.socketChannel = socketChannel;
    }

    public void run() {
        try {
            // 存储新上线用户
            server.addClient(socketChannel);
            // 读取用户发送的消息
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while(socketChannel.read(buffer) != -1){
                buffer.flip();
                while (!buffer.hasRemaining()){
                    server.forwardMessage(socketChannel,buffer);
                }
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.removeClient(socketChannel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
