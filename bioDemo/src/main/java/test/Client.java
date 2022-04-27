package test;

import com.sun.xml.internal.ws.resources.SenderMessages;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
@Data
@NoArgsConstructor
public class Client implements Runnable{

    private final String QUIT = "quit";
    private final String SEND = "send";
    private final String RECV = "receive";
    private int sendPort;
    private int receivePort;
    private int type;
    private final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private Socket sendSocket = null;
    private ServerSocket receiveSocket = null;

    public Client(String host,int sendPort,int receivePort,int type){
        this.sendPort = sendPort;
        this.receivePort = receivePort;
        this.type = type;

//        this.sendMsg(host,sendPort);
//        this.receiveMsg(receivePort);
    }
    public void sendMsg(String host,int serverPort){
        BufferedWriter bufferedWriter = null;
        BufferedReader consoleReader = null;
        try {
            sendSocket = new Socket(host,serverPort);
            bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(sendSocket.getOutputStream())
            );
            consoleReader = new BufferedReader(
                    new InputStreamReader(System.in)
            );
            while (true){
                System.out.println("请输入 ");
                String input = consoleReader.readLine();
                if (input!=null){
                    bufferedWriter.write(input +"\n");
                    bufferedWriter.flush();
                }
                if (QUIT.equals(input)){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bufferedWriter.close();
                sendSocket.close();
                consoleReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void receiveMsg(int receivePort){
        BufferedReader bufferedReader = null;
        try {
            this.receiveSocket = new ServerSocket(receivePort);
            while (true) {
                Socket socket = receiveSocket.accept();
                System.out.println("在客户端： "+ socket.getLocalPort()+"收到服务端口 ：" + socket.getPort());
                bufferedReader  = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                System.out.println("收到了全部消息1");
                String msg = null;
                while (!(msg = bufferedReader.readLine()).equals(QUIT)){
                    System.out.println("客户端 【" + socket.getPort() + "】 从服务器收到消息： " + msg);
                }
                if(msg.equals(QUIT)){
                    System.out.println("结束群聊");
                    break;
                }
            }
            System.out.println("收到了全部消息1");


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (receiveSocket !=null){
                try {
                    receiveSocket.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        if(this.type==0){
            System.out.println("client 端口"+getReceivePort()+"接收启动");
            receiveMsg(getReceivePort());
            System.out.println("接受关闭");
        }else {
            System.out.println("client 发送启动");
            sendMsg(DEFAULT_SERVER_HOST,getSendPort());
            System.out.println("发送关闭");
        }




    }
}
