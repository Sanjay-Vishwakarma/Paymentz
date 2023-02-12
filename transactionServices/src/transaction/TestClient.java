package transaction;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: NIKET
 * Date: 6/6/15
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestClient
{
    public static void main(String args[])
    {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        Client client = Client.create(config);
        WebResource service = client.resource(getBaseURI());
        service.setProperty("Content-type", "application/x-www-form-urlencoded");
        service.setProperty("Content-Length", "1000");
        /*// Get XML
        MyBean myBean = new MyBean();
        *//*MyBean bean = service.get(MyBean.class);*//*
        myBean.setMessage("welcome");
        myBean.setName("NIKET");
        *//*System.out.println("BEAN::::"+bean.getName()+" MESSAGE:::"+bean.getMessage());*//*
        System.out.println("Start:::" + service.path("RESTful").path("sample").path("hello").accept(MediaType.TEXT_PLAIN).get(String.class));
        // Get XML for application
        System.out.println();
        *//*service.path("RESTful").path("sample").type(MediaType.APPLICATION_XML).put(myBean);*//*
        myBean.setMessage("welcome again");
        myBean.setName("NIKET");*/

        /*DirectTransactionRequest directTransactionRequest = new DirectTransactionRequest();
        directTransactionRequest.setToId(10134);
        directTransactionRequest.setToType("");
        directTransactionRequest.setDescription("1121212121");*/

        String request = "Username=tcQOD&Password=NZk7BU1Qbw&TransactionID=1509755943&Version=1.0.0";

        //System.out.println(service.type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_XML).post(String.class,request));

    }

    private static URI getBaseURI() {

        return UriBuilder.fromUri("https://test.safecharge.com/QODService/Service.asmx/GetSpecificTransaction").build();

    }
}
