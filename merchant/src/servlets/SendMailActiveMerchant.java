import com.directi.pg.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: amit.j
 * Date: Jun 8, 2006
 * Time: 5:55:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class SendMailActiveMerchant extends HttpServlet
{  private static Logger Log = new Logger(SendMailActiveMerchant.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException
    {
        //String from=request.getParameter("from");
        Log.debug("Entering in send mail to merchant");
        ServletContext application = getServletContext();
        PrintWriter pWriter = res.getWriter();
        Connection con = null;
        /*       try{
          con = Database.getConnection();
      }catch (SystemError cnf){
          cnf.printStackTrace();
          return;
      }//try catch ends*/
        String strmailtype=null;
        String strSubject=null;
        String strMsg=null;
        try
        {
        strmailtype = ESAPI.validator().getValidInput("mailtype",request.getParameter("mailtype"),"Mailtype",10,true);

        }
        catch(ValidationException e)
        {
          Log.error("Enter valid type",e);
        }
        strSubject = request.getParameter("subject") + "";
        strMsg = request.getParameter("message") + "";
        Log.debug("Done");
        application.log(" Mailtype " + strmailtype + " strSubject " + strSubject + " strMsg " + strMsg);
        if (Functions.parseData(strmailtype) == null || Functions.parseData(strSubject) == null || Functions.parseData(strMsg) == null)
        {
            res.setContentType("text/html");
            pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">");
            pWriter.println("<HTML> <HEAD>");
            pWriter.println("<TITLE> Send Mail Error</TITLE>");
            pWriter.println("</HEAD> <BODY>");
            pWriter.println("<br><br>Please enter proper data.");
            pWriter.println("<a href=../SendMail.html >   Click here to send message</a>");
            pWriter.println("</BODY></HTML>");
            return;
        }
            ResultSet rsMrchnt = null;
        try
        {
            con = Database.getConnection();
            String strSql = "select notifyemail from members where activation='Y' and notifyemail!=''";
            rsMrchnt = Database.executeQuery(strSql, con);
            res.setContentType("text/html");
            pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">");
            pWriter.println("<HTML> <HEAD>");
            pWriter.println("<TITLE> Mail Sent Successfully</TITLE>");
            pWriter.println("</HEAD> <BODY>");
            //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
            while (rsMrchnt.next())
            {
                if (strmailtype.equals("0"))
                    Mail.sendmail(rsMrchnt.getString("notifyemail"), "", strSubject, strMsg);
                else if (strmailtype.equals("1"))
                    Mail.sendHtmlMail(rsMrchnt.getString("notifyemail"), "", null, null, strSubject, strMsg);
            }
            pWriter.println("<br><br>Mail sent to Active Merchants.");
            pWriter.println("<a href=../SendMail.html >   Click here to send message</a>");
            pWriter.println("</BODY></HTML>");
        }
        catch (SQLException sqe)
        {   Log.error("SQL Exception in send mail",sqe);
            application.log("SQL Exception ", sqe);
            Functions.NewShowConfirmation1("Error", "Internal System Error Send Mail ");
            return;
        }
        catch (SystemError cnf)
        {   Log.error("SYSTEM error is occure in send mail",cnf);
            application.log("System Error ", cnf);
            Functions.NewShowConfirmation1("Error", "Internal System Error Send Mail ");
            return;
        }
        catch (Exception e)
        {   Log.error("Exception occure in send mail",e);
            Functions.NewShowConfirmation1("Error", "Internal System Error Send Mail ");
            application.log("Excetpion in Send Mail", e);
        }
        finally
        {
            Database.closeResultSet(rsMrchnt);
            Database.closeConnection(con);
        }

    }
}
