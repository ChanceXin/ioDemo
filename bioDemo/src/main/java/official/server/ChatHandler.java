package official.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatHandler implements Runnable {
    private ChatServer server;
    private Socket socket;

    public ChatHandler(ChatServer server,Socket socket){
        this.server = server;
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            // 存储新上线用户
            server.addClient(socket);

            // 读取用户发送的消息
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            String msg = null;
            while ((msg = reader.readLine()) != null){
                String fwdmsg = "客户端 【" + socket.getPort() + "】 ：" + msg;
                server.forwardMessage(socket,fwdmsg);

                if (server.readQuit(msg)){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 只用移除  不用关闭 socket
                server.removeClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
