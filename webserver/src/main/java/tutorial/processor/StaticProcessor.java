package tutorial.processor;

import tutorial.connector.Request;
import tutorial.connector.Response;

import java.io.IOException;

public class StaticProcessor {
    public void process(Request request, Response response){
        try {
            response.sendStaticResource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
