import org.junit.Assert;
import org.junit.Test;
import tutorial.connector.Request;
import util.TestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class RequestTest {
    private static final String validRequest = "GET /index.html HTTP/1.1";
    @Test
    public void givenValidRequest_thenExtrackUri(){
        Request requestTest = TestUtils.CreateRequest(validRequest);
        Assert.assertEquals("/index.html",requestTest.getRequestURI());
    }

}
