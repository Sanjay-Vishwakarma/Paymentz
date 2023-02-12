package payment;

import com.directi.pg.TransactionLogger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Admin on 11/29/2017.
 */
public class TestBENotification extends PzServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(TestBENotification.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doService(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doService(request,response);
    }
    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("Entering doPost in TestBENotification::::");
        //System.out.println("Entering doPost in TestBENotification::::");

        for(Object key : request.getParameterMap().keySet())
        {
            transactionLogger.error("----for loop TestBENotification-----" + key + "=" + request.getParameter((String) key) + "--------------");
        }


        PrintWriter pWriter = response.getWriter();
        String responseCode = "200";
        String responseStatus = "OK";
        StringBuilder responseMsg = new StringBuilder();
        BufferedReader br = request.getReader();
        String str;
        while((str = br.readLine()) != null )
        {
            responseMsg.append(str);
        }

        transactionLogger.error("-----Notification JSON-----"+responseMsg);

        JSONObject jsonResObject = new JSONObject();
        try
        {
            jsonResObject.put("responseCode",responseCode);
            jsonResObject.put("responseStatus",responseStatus);
            //System.out.println("json obj---"+jsonResObject);
            response.setContentType("application/json");
            pWriter.println(jsonResObject.toString());
            pWriter.flush();
            return;
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---",e);
        }

    }
}
