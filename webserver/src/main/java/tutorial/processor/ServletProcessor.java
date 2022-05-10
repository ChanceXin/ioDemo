package tutorial.processor;

import tutorial.connector.*;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ServletProcessor {
    URLClassLoader getServeletLoader() throws MalformedURLException {
        File webroot = new File(ConnectorUtils.WEB_ROOT);
        URL webrootUrl = webroot.toURI().toURL();
        return  new URLClassLoader(new URL[]{webrootUrl});
    }
    Servlet getServlet(URLClassLoader loader,Request request) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        // /servlet/TimeServlet(具体名字)
        String uri = request.getRequestURI();
        String servletName = uri.substring(uri.lastIndexOf("/") +1);
        Class servletClass = loader.loadClass(servletName);
        Servlet servlet = (Servlet) servletClass.newInstance();
        return servlet;
    }
    public void process(Request request, Response response) throws MalformedURLException {
        URLClassLoader loader = getServeletLoader();
        try {
            RequestFacade requestFacade = new RequestFacade(request);
            ResponseFacade responseFacade = new ResponseFacade(response);
            Servlet servlet = getServlet(loader,request);
            servlet.service(requestFacade,responseFacade);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
