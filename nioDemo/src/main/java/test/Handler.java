package test;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Handler implements Runnable{
    private final String QUIT = "quit";
    private List<Integer> sendPortList;
    private final String DEFAULT_CLIENT_HOST = "127.0.0.1";
    private int receivePort ;
    private ServerSocket receiveSocket = null;

    public Handler(String host ,List<Integer> sendPortList,int receivePort){
        this.sendPortList = sendPortList;
        this.receivePort = receivePort;
//        receiveMsg(receivePort,host);
    }

    public void receiveMsg(String host,int receivePort) {
        BufferedReader bufferedReader = null;
        List<BufferedWriter> bufferedWriterList =  new ArrayList<>();
        try {
            receiveSocket = new ServerSocket(receivePort);
            System.out.println("receiveSocket创建正常");

            while (true){
                Socket socket = receiveSocket.accept();
                for(int i = 0;i < sendPortList.size();i++){
                    System.out.println(Thread.currentThread().getName() + "创建 " + (i+1)+"次  socket");
                    BufferedWriter item = creatMsgWriter(host,sendPortList.get(i));

                    bufferedWriterList.add(item);
                }
                bufferedReader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                String msg = null;
                // 持续接受消息并发送给其他的client
                while (!(msg = bufferedReader.readLine()).equals(QUIT)){
                    System.out.println("服务器端口："+ socket.getLocalPort()+ "收到 客户端端口："+ socket.getPort()+  msg);
                    sendAllMsg(bufferedWriterList,msg);
                }
                if(msg.equals(QUIT)){
                    System.out.println("结束群聊");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(receiveSocket != null){
                try {
                    // 关闭资源
                    receiveSocket.close();
                    bufferedReader.close();
                    for(int i = 0;i < bufferedWriterList.size();i++){
                        BufferedWriter item = bufferedWriterList.get(i);
                        try {
                            item.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendAllMsg(List<BufferedWriter> bufferedWriterList,String msg) {

        BufferedWriter item = null;
        for(int i = 0;i < bufferedWriterList.size();i++){

            try {
                item = bufferedWriterList.get(i);
                item.write(msg);
                item.flush();
                System.out.println("服务器转发了msg :" +msg  );
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public BufferedWriter creatMsgWriter(String host,int serverPort){
        BufferedWriter bufferedWriter = null;
        try {
            Socket socket = new Socket(host,serverPort);
            System.out.println("发送全部消息： 创建从服务器端口："+ socket.getLocalPort()+ "发送 portList的客户端端口："+ socket.getPort() +"的socket");
            bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            return bufferedWriter;
        }
    }




    @Override
    public void run() {
        System.out.println("Hander 启动");
        receiveMsg(DEFAULT_CLIENT_HOST,this.getReceivePort());
    }
}
