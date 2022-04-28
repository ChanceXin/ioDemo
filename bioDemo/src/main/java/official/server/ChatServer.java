package official.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ChatServer {


    private int DEFAULT_PORT = 8888;
    private final  String QUIT = "quit";
    private ServerSocket serverSocket;
    //保存 连接Server的客户端的端口，已经Server端创建的Writer对象
    private Map<Integer, Writer> connectedClients;
    ExecutorService executorService = null;

    public ChatServer() {
        connectedClients = new HashMap<>();
        executorService = Executors.newFixedThreadPool(10);
    }

    public synchronized void addClient(Socket socket) throws IOException {
        if (socket != null){
            int port = socket.getPort();
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );
            // 线程不安全
            connectedClients.put(port,bufferedWriter);
            System.out.println("客户端[" + port + "] 已连接到服务器 [" + serverSocket.getLocalPort()+ "]" );
        }else {
            System.out.println(" socket 为null 添加失败");
        }
    }

    public synchronized void removeClient(Socket socket) throws IOException{
        if (socket != null){
            int port = socket.getPort();
            if (connectedClients.containsKey(port)){
                socket.close();
                connectedClients.remove(port);
                System.out.println("客户端[" + port + "] 已断开连接到服务器 [" + serverSocket.getLocalPort()+ "]" );
            }
        }else {
            System.out.println(" socket 为 null ,移除socket失败");
        }
    }

    public synchronized void forwardMessage(Socket socket,String msg) throws IOException {
        if (socket != null){
            int port = socket.getPort();
            for (Integer id : connectedClients.keySet()){
                if (!id.equals(port)){
                    Writer writer = connectedClients.get(id);
                    writer.write(msg);
                    writer.flush();
                }
            }
        }else{
            System.out.println("转发失败");
        }
    }

    public void start(){
        try {

            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("服务器端口【"+ serverSocket.getLocalPort()+"】已开启：");

            while (true){// 建立一个连接 我就生成一个新handler线程去管理
                // 等待客户端连接，serverSocket一直在监听，返回的是serverSocket fork出来的一个socket
                Socket socket = serverSocket.accept();
                // 创建ChatHandler线程;
//                new Thread( new ChatHandler(this,socket)).start();
                executorService.submit(new ChatHandler(this,socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close();
        }
    }

    private synchronized void close() {
        try {
            if (serverSocket != null){
                // 更新的状态 ，线程不安全
                serverSocket.close();
                System.out.println("关闭服务器");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 没有更新和 读取 内存中的变量 所以本身就是线程安全
    public boolean readQuit(String msg) {
        return QUIT.equals(msg);
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        // 注意这里，对象调用函数 ，其实默认将自己本身作为参数传递了进去，在方法里用 this就可以调用
        chatServer.start();
    }
}
