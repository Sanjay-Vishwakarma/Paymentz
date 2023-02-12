import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.Template;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;


public class PreWaitServlet extends TcServlet
{
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.Servlet");
    final static String VBVWAITSERVLET = RB.getString("VBVWAITSERVLET");
    final static String PROXYHOST = RB.getString("PROXYHOST");
    final static String PROXYPORT = RB.getString("PROXYPORT");
//    PROXYHOST + ":" +PROXYPORT+
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession(false);
        Hashtable otherdetails = new Hashtable();
        PrintWriter pWriter = res.getWriter();
        ServletContext application = getServletContext();
        res.setContentType( "text/html" );
        pWriter.println( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">" );

        if (session == null)
        {
            String message = "ERROR!!! your session has expired.<BR><BR>";
            otherdetails.put("TRACKING_ID", "");
            String description = (String) req.getParameter("orderdesc"); // I may get description from MPI so getting it
            if (description != null)
                otherdetails.put("DESCRIPTION", description);
            else
                otherdetails.put("DESCRIPTION", "");

            otherdetails.put("ORDER_DESCRIPTION", "");
            otherdetails.put("TMPL_CUSTOMISE", "");
            otherdetails.put("TMPL_MSG", "");
            otherdetails.put("MESSAGE", message);

            try
            {
                pWriter.flush();
                pWriter.println(Template.getErrorPage(req.getParameter("shoppingContext"), otherdetails));
            }
            catch (Exception e)
            {
                application.log("Exception in PreWaitServlet "+e);
            }
            return;

        }
        else
        {
            pWriter.println("<html>");
            pWriter.println("<body>");

            Hashtable paramHash = (Hashtable) session.getAttribute("PARAMETERS");

            //check tracking id set in session and same returned from 3DS is same.
            if (paramHash == null || paramHash.get("TRACKING_ID") == null || !((String)paramHash.get("TRACKING_ID")).equals((String)req.getParameter("shoppingcontext")))
            {

                String message = "<b>ERROR!!!</b> We have encountered an internal error while processing your request. This happened because of a connectivity issue with the Credit Card Processor. Please retry this transaction afresh.<BR>";

                try
                {
                    pWriter.flush();
                    //RequestDispatcher rd=request.getRequestDispatcher("/icici/servlet/Error.jsp");
                    //rd.forward(request,res);
                    pWriter.println("<form name=\"error\" action=\"/icici/servlet/ErrorServlet\" method=\"post\" >");
                    pWriter.println("<input type=\"hidden\" name=\"ERROR\" value=\"" + URLEncoder.encode(message) + "\">");
                    pWriter.println("</form>");
                    pWriter.println("<script language=\"javascript\">");
                    pWriter.println("document.error.submit();");
                    pWriter.println("</script>");
                    pWriter.println("</body>");
                    pWriter.println("</html>");


                }
                catch (Exception e)
                {
                    application.log("exception occur " + e.toString());
                }
                return;


            }

            pWriter.println("<form name=\"prewait\" action=\"" + req.getScheme() + "://" + PROXYHOST +":"+PROXYPORT+ VBVWAITSERVLET + "\" method=\"post\" >");
            Enumeration paramEnum = paramHash.keys();
            while (paramEnum.hasMoreElements())
            {
                String key = (String) paramEnum.nextElement();
                pWriter.println("<input type=\"hidden\" name=\"" + key + "\" value=\"" + (String) paramHash.get(key) + "\">");
            }

            //retrieve all parameters got from 3DSecure
            paramEnum = req.getParameterNames();
            while (paramEnum.hasMoreElements())
            {
                String key = (String) paramEnum.nextElement();
                pWriter.println("<input type=\"hidden\" name=\"" + key + "\" value=\"" + (String) req.getParameter(key) + "\">");
            }

            pWriter.println("</form>");
            pWriter.println("<script language=\"javascript\">");
            pWriter.println("document.prewait.submit();");
            pWriter.println("</script>");
            pWriter.println("</body>");
            pWriter.println("</html>");

        }

    }

}