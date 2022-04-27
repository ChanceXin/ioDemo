import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        final String QUIT = "quit";
        final int DEFAULT_PORT = 7783;
        ServerSocket serverSocket = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            // 绑定监听端口
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("启动服务器，监听端口 :" + DEFAULT_PORT);
            System.out.println("启动服务器，本地端口 :" + serverSocket.getLocalPort());
            while (true){ // 这个true 保证当前客户端关闭会话后 ，服务端可重新开启监听
                //等待客户端连接，若没收到阻塞
                Socket socket = serverSocket.accept();

                System.out.println("客户端【" +socket.getPort() +"】已连接");
                 reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                 writer = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())
                );
                String msg = null;
                while (!(msg = reader.readLine()).equals(QUIT)) {
                    // 读取客户端发送的消息
                    System.out.println("从客户端【" + socket.getPort() + "】 ：收到消息" + msg);
                    //回复客户发送的消息
                    writer.write("服务器 ： " + msg + "\n"); //\n是可以让客户点也可是使用readLine()
                    writer.flush();//将缓冲区的数据发送出去
                    if (msg.equals("quit")){
                        System.out.println("******************当前客户端结束会话*************************");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //为了保证 及时抛出异常也能关闭释放资源
            if (serverSocket != null){
                try {
                    serverSocket.close();//是否也可以关闭Buffer装饰类
                    reader.close();
                    writer.close();
                    System.out.println("关闭 ServerSocket");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
