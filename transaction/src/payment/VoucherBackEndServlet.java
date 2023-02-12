package payment;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Admin on 7/17/2019.
 */
public class VoucherBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger= new TransactionLogger(CommonBackEndServlet.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doService(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doService(request,response);
    }
    public void doService(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {
        for (Object key : request.getParameterMap().keySet())
        {
            transactionLogger.debug("----for loop CommonBackEndServlet-----" + key + "=" + request.getParameter((String) key) + "----------");
        }
    }
}
