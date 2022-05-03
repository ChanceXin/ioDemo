package official.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInputHandler implements Runnable{
    private final String QUIT = "quit";
    private ChatClient chatClient;
    public UserInputHandler(ChatClient chatClient){
        this.chatClient = chatClient;
    }

    @Override
    public void run() {
        try {
            while (true){
                System.out.println("等待客户端输入：");
                String input = null;
                BufferedReader consoleReader = new BufferedReader(
                        new InputStreamReader(System.in)
                );
                input = consoleReader.readLine();
                chatClient.send(input);
                if (chatClient.readyQuit(input)){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
