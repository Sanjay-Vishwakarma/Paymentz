
import com.directi.pg.*;
import com.invoice.dao.InvoiceEntry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Oct 24, 2012
 * Time: 12:25:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class CupProcess extends HttpServlet
{
    static Logger log = new Logger(CupProcess.class.getName());
    Connection con = null;
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.CupServlet");
    final static String CUPSERVLET =  RB.getString("CUPSERVLET");
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {   PrintWriter pWriter = res.getWriter();
        res.setContentType("text/html");
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");
        String ctoken = null;
        HttpSession session = req.getSession();
        Hashtable error = new Hashtable();
        Hashtable otherdetails = new Hashtable();
        if(req.getSession()== null)
        {
            Functions.ShowMessage("Invalid Request","Your session has been expired");
            return;
        }
        if(session !=null)
        {
            ctoken =  (String)session.getAttribute("ctoken");
            log.debug("CSRF token from session"+ctoken);
        }

        if(ctoken!=null && !ctoken.equals("") && !req.getParameter("ctoken").equals(ctoken))
        {
            log.debug("CSRF token not match ");

            Functions.ShowMessage("Invalid Request","UnAuthorized member");
            return;
        }

        log.debug("Entered in CupProcess");
        String imageheader = req.getParameter("HEADER");
        String header=req.getParameter("HEADDER");

        String sTransParameter=req.getParameter("hiddenvariables");
        session.setAttribute("HEADER",imageheader);
        String template=null;
        String vbv=null;
        String autoredirect=null;
        String key = "";
        String val = "";
        //String header="Client ="+req.getRemoteAddr()+":"+req.getServerPort()+",X-Forwarded="+req.getServletPath();

        int TRACKING_ID=0;
        log.debug("header value "+header);
        //Hashtable value= (Hashtable) req.getAttribute("hiddenvariables");
        Functions functions=new Functions();
        Hashtable value=functions.getHashValue(sTransParameter) ;
        //value.put("Image Header",imageheader);
        value.put("HEADER",header);
        value.put("ctoken",ctoken);
        String toid= (String)value.get("TOID");
        PreparedStatement p = null;
        ResultSet rs = null;
        try
        {
            con= Database.getConnection();
            String query="insert into transaction_common(toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?)";
            p=con.prepareStatement(query);

            p.setString(1,(String)value.get("TOID"));
            p.setString(2,(String)value.get("TOTYPE"));
            p.setString(3,(String)value.get("FROMID"));
            p.setString(4,(String)value.get("FROMTYPE"));
            p.setString(5,(String)value.get("DESCRIPTION"));
            p.setString(6,(String)value.get("ORDER_DESCRIPTION"));
            p.setString(7,(String)value.get("TXN_AMT"));
            p.setString(8,(String)value.get("REDIRECTURL"));
            p.setString(9,"authstarted");
            p.setString(10,(String)value.get("ACCOUNTID"));
            p.setString(11,(String)value.get("PAYMODEID"));
            p.setString(12,(String)value.get("CARDTYPEID"));
            p.setString(13,(String)value.get("CURRENCY"));
            p.setString(14,header);
            int num = p.executeUpdate();
            if (num == 1)
            {
                rs = p.getGeneratedKeys();
                if(rs!=null)
                {
                    while(rs.next())
                    {
                        TRACKING_ID = rs.getInt(1);
                    }
                }
            }

            value.put("TRACKING_ID",TRACKING_ID);

            String INVOICE_NO = null;
            INVOICE_NO =(String)value.get("INVOICE_NO");
            InvoiceEntry invoiceEntry=new InvoiceEntry();
            if(INVOICE_NO!=null && !INVOICE_NO.equals(""))
            {
                invoiceEntry.processInvoice(INVOICE_NO,TRACKING_ID,(String)value.get("ACCOUNTID"));
            }

            log.debug("insert data successfully");
            String sql = "select autoredirect,template,vbv from members where memberid =?";
            p=con.prepareStatement(sql);
            p.setString(1,(String) value.get("TOID"));
            rs=p.executeQuery();
            if(rs.next())
            {
                autoredirect= rs.getString("autoredirect");
                template=rs.getString("template");
                vbv=rs.getString("vbv");
            }
        }
        catch(Exception e)
        {
            log.error("Exception occur while insert data",e);
            error.put("Error", "error occur while insert data");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closeConnection(con);
        }
        StringBuffer sbuf = new StringBuffer();

        if (error.size() > 0)
        {
            sbuf.append("Following Parameters are Invalid");
            Enumeration enu = error.keys();

            sbuf.append("<center><table border=1>");
            sbuf.append("<tr bgcolor=\"blue\" >");
            sbuf.append("<td><font color=\"#FFFFFF\" >");
            sbuf.append("Field");
            sbuf.append("</font></td>");
            sbuf.append("<td><font color=\"#FFFFFF\" >");
            sbuf.append("Error");
            sbuf.append("</font></td>");
            sbuf.append("</tr>");
            while (enu.hasMoreElements())
            {
                String field = (String) enu.nextElement();
                sbuf.append("<tr>");
                sbuf.append("<td>");
                sbuf.append(field);
                sbuf.append("</td>");
                sbuf.append("<td>");
                sbuf.append((String) error.get(field));
                sbuf.append("</td>");
                sbuf.append("</tr>");
            }
            sbuf.append("</table>");

            otherdetails.put("MESSAGE", sbuf.toString());
            otherdetails.put("RETRYBUTTON", "<input type=\"button\" value=\"&nbsp;&nbsp;Retry&nbsp;&nbsp;\" onClick=\"javascript:history.go(-1)\">");

            try
            {
                pWriter.println(Template.getError(toid, otherdetails));
                pWriter.flush();
            }
            catch (SystemError se)
            {
                log.error("Excpetion in WaitServlet",se);
            }
            return;
        }

        value.put("autoredirect",autoredirect);
        value.put("TEMPLATE",template);
        value.put("VBV",vbv);

        //redirecting to PayVTServlet
        pWriter.println("<HTML>");
        pWriter.println("<HEAD> <script language=\"javascript\">" +
                "function Load(){" +
                "document.form.submit();" +
                "}" +
                " </script>");
        pWriter.println("</HEAD>");
        pWriter.println("<BODY onload=Load()>");
        pWriter.println("<form name=\"form\" action=\""+CUPSERVLET+"?ctoken="+ctoken+"\" method=\"post\" >");
        Enumeration enu = value.keys();

        while (enu.hasMoreElements())
        {
            key = (String) enu.nextElement();
            val = (String) value.get(key).toString();
            pWriter.println("<input type=hidden name=\""+key.toString()+"\" value=\""+val.toString()+"\" >");
            log.debug("<input type=hidden name=\""+key.toString()+"\" value=\""+val.toString()+"\"");
        }

        pWriter.println("</form>");
        pWriter.println("</BODY>");
        pWriter.println("</HTML>");
    }
}
