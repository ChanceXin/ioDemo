package test;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
public class Acceptor {
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static List<Integer>  portList = new ArrayList<>();
    public static void main(String[] args) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        Client c10 = new Client(DEFAULT_HOST,8880,7781,0);
        Client c20 = new Client(DEFAULT_HOST,8882,7783,0);
        Client c11 = new Client(DEFAULT_HOST,8880,7788,1);
        Client c21 = new Client(DEFAULT_HOST,8882,7789,1);
        portList.add(c10.getReceivePort());
        portList.add(c20.getReceivePort());

        Handler h1  = new Handler(DEFAULT_HOST,portList,8880);
        Handler h2  = new Handler(DEFAULT_HOST,portList,8882);

        executorService.submit(c10);
        executorService.submit(c20);
        Thread.sleep(1);
        executorService.submit(h1);
        executorService.submit(h2);
        Thread.sleep(1);
        executorService.submit(c11);
        executorService.submit(c21);





    }
}
