import com.directi.pg.Logger;
import com.directi.pg.core.BankConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/10/15
 * Time: 7:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class BankConnHealthCheck extends HttpServlet
{   private static Logger log = new Logger(BankConnection.class.getName());
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        String toid=req.getParameter("toid");
        StringBuffer message=new StringBuffer();
        boolean isSuccess=true;
        PrintWriter pWriter = res.getWriter();
        if(toid.equals("10103"))
        {
            BankConnection bankConnection=new BankConnection();
            try
            {
                String bankConnectionMessage= bankConnection.checkConnection();
                String[] temp=bankConnectionMessage.split("<br>");
                for(int i=1;i<temp.length;i++)
                {
                    if(temp[i].contains("Failed"))
                    {   isSuccess=false;
                        message.append(""+temp[i]+"<BR>");
                    }
                }
                if(!isSuccess)
                {
                    pWriter.write("^BANK_CONNECTION_FAILED:" + message.toString() + "~");

                }
            }
            catch (Exception e)
            {
                log.error("Error while checking bank connection",e);
            }
        }
        else
        {
            pWriter.write("^Invalid Request:~");
        }
    }
}
