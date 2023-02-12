package payment;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Enumeration;

/**
 * Created by Admin on 1/9/2020.
 */
public class InvoiceAppFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(InvoiceAppFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
   public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
   {
       doService(request,response);
   }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {
        doService(request,response);
    }
    public void doService(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {
        transactionLogger.error("-----Inside InvoiceBackEndServlet---");
        PrintWriter printWriter=response.getWriter();
        Enumeration enumeration = request.getParameterNames();
        Functions functions=new Functions();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = request.getParameter(key);

            transactionLogger.error("Name=" + key + "-----Value=" + value);
        }
        String deviceType=request.getParameter("deviceType");
        String trackingid=request.getParameter("trackingid");
        String paymentId=request.getParameter("paymentId");
        String desc=request.getParameter("desc");
        String status=request.getParameter("status");
        String amount=request.getParameter("amount");
        String resultDescription=request.getParameter("resultDescription");
        String merchantTransactionId=request.getParameter("merchantTransactionId");
        String checksum=request.getParameter("checksum");
        String descriptor=request.getParameter("descriptor");
        String firstName=request.getParameter("firstName");
        String lastName=request.getParameter("lastName");
        String currency=request.getParameter("currency");
        String tmpl_currency=request.getParameter("tmpl_currency");
        String tmpl_amount=request.getParameter("tmpl_amount");
        String timestamp=request.getParameter("timestamp");
        String resultCode=request.getParameter("resultCode");
        String custEmail=request.getParameter("custEmail");
        String eci=request.getParameter("eci");
        String paymentMode=request.getParameter("paymentMode");
        String paymentBrand=request.getParameter("paymentBrand");
        Calendar calendar = Calendar.getInstance();
        transactionLogger.error("Tranx time of fun(): "+calendar.getTime());

        String callbackString="";
        if("android".equals(deviceType)){
            callbackString = "<html><head><script type=\"text/javascript\">" +
                    "  var jsonObjectResponse = {};" +
                    "  jsonObjectResponse[\"status\"] = \""+status+"\";\n" +
                    "  jsonObjectResponse[\"trackingid\"] = \""+trackingid+"\";\n" +
                    "  jsonObjectResponse[\"paymentId\"] = \""+paymentId+"\";\n" +
                    "  jsonObjectResponse[\"desc\"] = \""+desc+"\";\n" +
                    "  jsonObjectResponse[\"merchantTransactionId\"] = \""+merchantTransactionId+"\";\n" +
                    "  jsonObjectResponse[\"checksum\"] = \""+checksum+"\";\n" +
                    "  jsonObjectResponse[\"descriptor\"] = \""+descriptor+"\";\n" +
                    "  jsonObjectResponse[\"firstName\"] = \""+firstName+"\";\n" +
                    "  jsonObjectResponse[\"lastName\"] = \""+lastName+"\";\n" +
                    "  jsonObjectResponse[\"currency\"] = \""+currency+"\";\n" +
                    "  jsonObjectResponse[\"amount\"] = \""+amount+"\";\n" +
                    "  jsonObjectResponse[\"tmpl_currency\"] = \""+tmpl_currency+"\";\n" +
                    "  jsonObjectResponse[\"tmpl_amount\"] = \""+tmpl_amount+"\";\n" +
                    "  jsonObjectResponse[\"timestamp\"] = \""+timestamp+"\";\n" +
                    "  jsonObjectResponse[\"resultCode\"] = \""+resultCode+"\";\n" +
                    "  jsonObjectResponse[\"resultDescription\"] = \""+resultDescription+"\";\n" +
                    "  jsonObjectResponse[\"custEmail\"] = \""+custEmail+"\";\n" +
                    "  jsonObjectResponse[\"eci\"] = \""+eci+"\";\n" +
                    "  jsonObjectResponse[\"paymentMode\"] = \""+paymentMode+"\";\n" +
                    "  jsonObjectResponse[\"paymentBrand\"] = \""+paymentBrand+"\";\n" +
                    "  android.paymentResultListener(JSON.stringify(jsonObjectResponse));\n" +
                    "</script></head></html>";
        }
        else if ("iOS".equals(deviceType)){
            callbackString = "<html><head><script type=\"text/javascript\">" +
                    "window.webkit.messageHandlers.callbackHandler.postMessage({\n" +
                    "\"status\" : \""+status+"\",\n" +
                    "\"trackingid\" : \""+trackingid+"\",\n" +
                    "\"paymentId\" : \""+paymentId+"\",\n" +
                    "\"desc\" : \""+desc+"\",\n" +
                    "\"merchantTransactionId\" : \""+merchantTransactionId+"\",\n" +
                    "\"checksum\" : \""+checksum+"\",\n" +
                    "\"descriptor\" : \""+descriptor+"\",\n" +
                    "\"firstName\" : \""+firstName+"\",\n" +
                    "\"lastName\" : \""+lastName+"\",\n" +
                    "\"currency\" : \""+currency+"\",\n" +
                    "\"amount\" : \""+amount+"\",\n" +
                    "\"tmpl_currency\" : \""+tmpl_currency+"\",\n" +
                    "\"tmpl_amount\" : \""+tmpl_amount+"\",\n" +
                    "\"timestamp\" : \""+timestamp+"\",\n" +
                    "\"resultCode\" : \""+resultCode+"\",\n" +
                    "\"resultDescription\" : \""+resultDescription+"\",\n" +
                    "\"custEmail\" : \""+custEmail+"\",\n" +
                    "\"eci\" : \""+eci+"\",\n" +
                    "\"paymentMode\" : \""+paymentMode+"\",\n" +
                    "\"paymentBrand\" : \""+paymentBrand+"\"});" +
                    "</script></head></html>";
        }

        response.setContentType("text/html");
        printWriter.println(callbackString);
        return;
    }
}
