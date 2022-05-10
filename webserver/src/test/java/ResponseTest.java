import org.junit.Assert;
import org.junit.Test;
import tutorial.connector.ConnectorUtils;
import tutorial.connector.Request;
import tutorial.connector.Response;
import util.TestUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ResponseTest {
    public final String validRequest = "GET /index.html HTTP/1.1";
    public final String invalidRequest = "GET /notfound.html HTTP/1.1";

    public final String status200 = "HTTP/1.1 200 OK\r\n\r\n";
    public final String status404 = "HTTP/1.1 404 File not found\r\n\r\n";

    @Test //test user.dir 是子模块路径
    public void givenValidRequest_thenExtrackUri() throws IOException {
        System.out.println(System.getProperty("user.dir"));
//        Request requestTest = TestUtils.CreateRequest(validRequest);
//        ByteArrayOutputStream out  = new ByteArrayOutputStream();
//        Response response = new Response(out);
//        response.setRequest(requestTest);
//        response.sendStaticResource();
//
//        String resource = TestUtils.readFileToString(ConnectorUtils.WEB_ROOT + requestTest.getRequestURI());
//        System.out.println("resouce : =" + status200+  resource);
//        System.out.println("out.toString() : =" + out.toString());
//        Assert.assertEquals(status200 + resource,out.toString());

    }

    @Test
    public void givenInvalidRequest_thenExtrackUri() throws IOException {
        Request requestTest = TestUtils.CreateRequest(invalidRequest);
        ByteArrayOutputStream out  = new ByteArrayOutputStream();
        Response response = new Response(out);
        response.setRequest(requestTest);
        response.sendStaticResource();

        String resource = TestUtils.readFileToString(ConnectorUtils.WEB_ROOT + "/404.html");
        System.out.println("resource : =" + status404 +  resource);
        System.out.println("out.toString() : =" + out.toString());
        Assert.assertEquals(status404 + resource,out.toString());

    }



}
