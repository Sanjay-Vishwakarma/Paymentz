import com.directi.pg.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;

public class PreviewServlet extends HttpServlet
{
    private static Logger Log = new Logger(PreviewServlet.class.getName());

    public PreviewServlet()
    {
        super();
    }

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
        PrintWriter pWriter = res.getWriter();
        res.setContentType("text/html");
        String redirectpage = null;
        Hashtable hash = new Hashtable();
        HttpSession session = request.getSession();
        Merchants merchants = new Merchants();

        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/logout.jsp");
            return;
        }
        Hashtable detailhash = new Hashtable();

        try
        {

            boolean flag = true;
            String name = "";
            StringBuffer strbuf = new StringBuffer();// to store all request parameter value


            Enumeration enumparanames = request.getParameterNames();
            while (enumparanames.hasMoreElements())
            {
                name = (String) enumparanames.nextElement();

                if (name.equalsIgnoreCase("List"))
                    continue;

                String value = request.getParameter(name);
                value = URLEncoder.encode(value);
                strbuf.append(name + "=" + value + "&");

                if (name.equals("HEADER"))
                {
                    String header = (String) request.getParameter("HEADER");
                    header = Jsoup.clean(header, Whitelist.relaxed());
                    if (!Functions.isValidHeader(header) || !Functions.isValidSrt(header))
                        flag = false;

                    detailhash.put(name, header);

                }
                else
                {
                    if (!Functions.isValidSrt((String) request.getParameter(name)))
                        flag = false;
                    else
                        detailhash.put(name, (String) request.getParameter(name));
                }
            }

            hash = Template.getMemberTemplateDetails((String) session.getAttribute("merchantid"));
            for (int i = 1; i <= Template.noofimages; i++)
            {
                if ((String) (hash.get("IMAGE" + i)) != null)
                {
                    detailhash.put("IMAGE" + i, (String) hash.get("IMAGE" + i));
                }
            }

            detailhash.put("TEMPLATE", "N");

            String type = (String) request.getParameter("type");
            String pgno = request.getParameter("pgno");
            int i = 1;
            if (pgno != null)
            {
                i = Integer.parseInt(pgno) + 1;
            }



            if (i == 1)
            {
                detailhash.put("CONTINUEBUTTON", "<input type=\"button\" onclick=\"document.location.href=\'/merchant/servlet/PreviewServlet?pgno=" + i + "&type=fail&" + strbuf.toString() + "\'\" value=\"Fail Transaction Demo\"><br><br><input type=\"button\" onclick=\"document.location.href=\'/merchant/servlet/PreviewServlet?pgno=" + i + "&type=success&" + strbuf.toString() + "\'\" value=\"Successful Transaction Demo\">");

            }
            else if (i == 2)
            {

                if (type.equals("fail"))
                    detailhash.put("CONTINUEBUTTON", "<input type=\"button\" onclick=\"document.location.href=\'/merchant/servlet/PreviewServlet?pgno=3&type=error&" + strbuf.toString() + "\'\" value=\"Error Page Demo\"><br><br><input type=\"button\" onclick=\"document.location.href=\'/merchant/servlet/PreviewServlet?pgno=" + i + "&type=fail&" + strbuf.toString() + "\'\" value=\"Continue Demo\">");
                else if (type.equals("success"))
                    detailhash.put("CONTINUEBUTTON", "<input type=\"button\" onclick=\"document.location.href=\'/merchant/servlet/PreviewServlet?pgno=3&type=error&" + strbuf.toString() + "\'\" value=\"Error Page Demo\"><br><br><input type=\"button\" onclick=\"document.location.href=\'/merchant/servlet/PreviewServlet?pgno=" + i + "&type=success&" + strbuf.toString() + "\'\" value=\"Continue Demo\">");

            }
            else if (i == 3 || i == 4)
                detailhash.put("CONTINUEBUTTON", "");

            detailhash.put("TRACKING_ID", "0000000");
            detailhash.put("ORDER_ID", "Demo");
            detailhash.put("DESCRIPTION", "Demo");
            detailhash.put("TXN_AMT", "1000.00");
            detailhash.put("ORDER_DESCRIPTION", "This is description about your product.");


            String viewpage = "";
            Log.debug("flag=" +flag);
            if (flag)
            {
                Log.debug("All data are correct");
                Log.debug("switch=" +i);
                switch (i)
                {
                    case 1:
                    {   Log.debug("showing credit page preview for data");
                        viewpage = Template.getCreditPage((String) session.getAttribute("merchantid"), detailhash);

                        Log.debug("creditpage=" + viewpage);
                        pWriter.println(viewpage);
                    }
                    break;
                    case 2:
                    {   Log.debug("showing wait page preview for data" );
                        viewpage = Template.getWaitPage((String) session.getAttribute("merchantid"), detailhash);
                        Log.debug("wait page=" + viewpage);
                        pWriter.println(viewpage);
                    }
                    break;
                    case 3:
                    {
                        if (type.equals("fail"))
                        {
                            detailhash.put("MESSAGE", "Invalid Card number");
                            detailhash.put("TRANSACTIONSTATUS", "Failed");
                        }
                        else
                        {
                            detailhash.put("MESSAGE", "Successful Transaction");
                            detailhash.put("TRANSACTIONSTATUS", "Successful");
                        }
                        Log.debug("showing confirmation page preview for data" );
                        detailhash.put("DISPLAYNAME",session.getAttribute("displayname"));
                        viewpage = Template.getConfirmationPage((String) session.getAttribute("merchantid"), detailhash);

                        pWriter.println(viewpage);
                    }
                    break;
                    case 4:
                    {   Log.debug("showing error page preview for data" );
                        detailhash.put("MESSAGE", "ERROR!!! Your Transaction is already being processed. This can occur if you clicked on the back button and tried to submit this Transaction again. The transaction may succeed or fail, however the status of the Transaction will have to be set manually. Please contact the Merchant to verify the status of the transaction with the following reference numbers and inform him of this message. PLEASE DO NOT TRY to execute this transaction once more from the beginning, or you may end up charging your card twice.<BR><BR>");
                        viewpage = Template.getErrorPage((String) session.getAttribute("merchantid"), detailhash);
                        Log.debug("error page=" + viewpage);
                        pWriter.println(viewpage);
                    }
                    break;
                }

            }
            else
            {  Log.debug("<table width=\"100%\" align=\"center\"><tr><td class=\"text\" align=\"center\"><b>*One or more of the fields is not properly inserted.</b></td></tr><tr><td class=\"text\" align=\"center\" ><b>*Do not put script tag or form tag or html tag in header</b></td></tr><tr><td class=\"text\" align=\"center\" ><b>*Do not put ' anywhere</b></td></tr></table>");
                pWriter.println("<table width=\"100%\" align=\"center\"><tr><td class=\"text\" align=\"center\"><b>*One or more of the fields is not properly inserted.</b></td></tr><tr><td class=\"text\" align=\"center\" ><b>*Do not put script tag or form tag or html tag in header</b></td></tr><tr><td class=\"text\" align=\"center\" ><b>*Do not put ' anywhere</b></td></tr></table>");
            }
        }
        catch (SystemError se)
        {
            Log.error("Leaving Merchants throwing SQL Exception as System Error : ", se);
            redirectpage = "/error.jsp";
        }
        catch (Exception e)
        {
            Log.error("Leaving Merchants throwing Exception : ", e);
            redirectpage = "/error.jsp";
        }
    }
}