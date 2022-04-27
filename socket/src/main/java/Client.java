import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        final String QUIT = "quit";
        final int DEFAULT_PORT = 7783;
        final String DEFAULT_SERVER_HOST = "127.0.0.1";
        Socket socket = null;
        Socket socket1 = null;
        BufferedReader bufferedReader = null;
        BufferedReader consoloReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            // 创建客户端Socket （隐式的绑定了端口）
            // 这个构造函数对server发起了请求。
            socket = new Socket(DEFAULT_SERVER_HOST,DEFAULT_PORT);
            System.out.println("启动客户端 " +socket.getLocalPort());
            socket1 = new Socket(DEFAULT_SERVER_HOST,DEFAULT_PORT);
            System.out.println("启动客户端 " +socket1.getLocalPort());
            // 创建IO流
            bufferedReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );

            // 等待用户输入信息
            consoloReader = new BufferedReader(
                    new InputStreamReader(System.in)
            );
            String input = null;
            while (true) {
                input = consoloReader.readLine();
                // 发送给服务器
                bufferedWriter.write(input+"\n"); // \n 确保能区分回车
                bufferedWriter.flush();

                // 读取服务器返回的消息
                String msg = bufferedReader.readLine();
                System.out.println("从服务器收到: " + msg);
                if (QUIT.equals(input)){
                    System.out.println("客户端发起结束");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null){
                try {
                    bufferedWriter.close();
                    if (bufferedReader !=null){
                        bufferedReader.close();
                    }
                    System.out.println("关闭Socket");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
