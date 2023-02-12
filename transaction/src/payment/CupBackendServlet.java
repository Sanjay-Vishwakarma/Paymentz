package payment;

import com.directi.pg.*;
import com.directi.pg.core.valueObjects.CupResponseVO;
import com.payment.Mail.AsynchronousMailService;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Oct 24, 2012
 * Time: 12:29:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class CupBackendServlet extends PzServlet
{
    public CupBackendServlet()
    {
        super();
    }
    private static Logger log = new Logger(CupBackendServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(CupBackendServlet.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        ServletContext ctx = getServletContext();
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();

        //PrintWriter pWriter = res.getWriter();
        res.setContentType("text/html");
        //pWriter.println(key+" = "+Arrays.toString(value));
        log.debug("---Backend IpAddress---" + req.getRemoteAddr());
        log.debug("---Backend HOST---" + req.getRemoteHost());
        log.debug("---Backend URI---" + req.getRequestURI());
        log.debug("---Backend URL---"+req.getRequestURL());

        transactionLogger.debug("---Backend IpAddress---" + req.getRemoteAddr());
        transactionLogger.debug("---Backend HOST---" + req.getRemoteHost());
        transactionLogger.debug("---Backend URI---" + req.getRequestURI());
        transactionLogger.debug("---Backend URL---"+req.getRequestURL());

        String headername = "";
        for(Enumeration e = req.getHeaderNames();
            e.hasMoreElements();)
        {
            headername = (String)e.nextElement();
            log.debug("----backend---"+req.getHeader(headername) + "<br/>");
            transactionLogger.debug("----backend---"+req.getHeader(headername) + "<br/>");
        }

        Connection con = null;
        ResultSet rsToid= null;
        ResultSet rs1= null;
        ResultSet rs= null;

        PreparedStatement pToid = null;
        PreparedStatement p1= null;
        PreparedStatement p= null;

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        String orderId = ESAPI.encoder().encodeForSQL(me,req.getParameter("orderNumber"));
        String transId = ESAPI.encoder().encodeForSQL(me,req.getParameter("qid"));
        String trackingId =null;
        try
        {
            con = Database.getConnection();

            String orderNumber=null;
            String toId = null;

            String sqlToid = "select toid, description from transaction_common where trackingid = ? ";

            pToid = con.prepareStatement(sqlToid);
            pToid.setString(1,trackingId);

            rsToid=pToid.executeQuery();
            while(rsToid.next())
            {
                toId = rsToid.getString("toid");
                orderNumber= rsToid.getString("description");
            }

            //System.out.println("---toid---"+toId+"---OrderNumber---"+orderNumber);

        int count = 0;
        String sQuery = "select responsetransactionid from transaction_common_details where responsetransactionid=?";
        p1= con.prepareStatement(sQuery);

        p1.setString(1,transId);
        rs1=p1.executeQuery();
        while(rs1.next())
        {
            count++;
        }
        if(count==0)
        {
            String amountInCents = ESAPI.encoder().encodeForSQL(me,req.getParameter("settleAmount"));
            //String captureAmount = String.valueOf(Integer.parseInt(amountInCents)/100);
            if(amountInCents ==null)
            {
                amountInCents="000";
            }
            else
            {
                if(amountInCents.length()<3)
                {
                    if(amountInCents.length()==2)
                    {
                        amountInCents = "0" + amountInCents;
                    }
                    if(amountInCents.length()==1)
                    {
                        amountInCents = "00" + amountInCents;
                    }
                }
            }
            String captureAmount = amountInCents.substring(0,amountInCents.length()-2) + "." + amountInCents.substring(amountInCents.length()-2);

            String transType = req.getParameter("transType");

            CupResponseVO responseVO = new CupResponseVO();

            responseVO.setResponseCode(ESAPI.encoder().encodeForSQL(me,req.getParameter("respCode")));
            responseVO.setProcessingTime(ESAPI.encoder().encodeForSQL(me, req.getParameter("respTime")));
            responseVO.setTransactionID(transId);
            String message = ESAPI.encoder().encodeForSQL(me,req.getParameter("respMsg"));
            responseVO.setDescription(message);


            String sql = "select trackingid from transaction_common_details where detailid = ? ";
            p=con.prepareStatement(sql);

            p.setString(1,orderId);
            rs=p.executeQuery();
            while(rs.next())
            {
                trackingId= rs.getString("trackingid");

            }

            AuditTrailVO auditTrailVO=new AuditTrailVO();
            auditTrailVO.setActionExecutorId(toId);
            auditTrailVO.setActionExecutorName("Customer");
            ActionEntry entry = new ActionEntry();
                StringBuffer sb = new StringBuffer();

                sb.append("update transaction_common set ");
                //sb.append(" transid='" + transId + "'");

                String status = null;
                if ((req.getParameter("respMsg").equals("Success!")))
                {
                    status="Y";
                    if("01".equals(transType)){
                        sb.append(" captureamount='" + captureAmount + "'");
                        sb.append(", status='capturesuccess'");

                        responseVO.setTransType("01");
                        entry.actionEntryForCUP(trackingId,captureAmount,ActionEntry.ACTION_CAPTURE_SUCCESSFUL,ActionEntry.STATUS_CAPTURE_SUCCESSFUL,responseVO,null,auditTrailVO,null);
                    }
                    else if("02".equals(transType)){
                        sb.append(" status='authsuccessful'");
                        responseVO.setTransType("02");
                        entry.actionEntryForCUP(trackingId,captureAmount,ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL,ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL,responseVO,null,auditTrailVO,null);

                    }

                }
                else
                {
                    status="N";
                    sb.append(" status = 'authfailed' ");
                    entry.actionEntryForCUP(trackingId,captureAmount,ActionEntry.ACTION_AUTHORISTION_FAILED,ActionEntry.STATUS_AUTHORISTION_FAILED,responseVO,null,auditTrailVO,null);
                }
                sb.append(" where trackingid = '"+trackingId+"'");

                if("01".equals(transType) || "02".equals(transType)){
                    int result = Database.executeUpdate(sb.toString(), con);

                    if (result != 1)
                    {
                        Database.rollback(con);
                        log.debug("Leaving do service with error in result, result is " + result + " and not 1");
                        transactionLogger.debug("Leaving do service with error in result, result is " + result + " and not 1");
                        //Mail.sendAdminMail("Exception while Updating status", "\r\n\r\nException has occured while updating status for tracking id=" + trackingId + "\r\nAuth code=" + responseVO.getResponseCode() + "\r\nAuth message=" + responseVO.getDescription());
                        asynchronousMailService.sendAdminMail("Exception while Updating status", "\r\n\r\nException has occured while updating status for tracking id=" + trackingId + "\r\nAuth code=" + responseVO.getResponseCode() + "\r\nAuth message=" + responseVO.getDescription());

                    }


                    String clkey = null;
                    String checksumAlgo = null;
                    String checksum = null;
                    String notificationUrl=null;

                    con = Database.getConnection();
                    String query = "select clkey,checksumalgo,notificationUrl from members where memberid=?";
                    p=con.prepareStatement(query);
                    p.setString(1,toId);
                    rs = p.executeQuery();
                    if (rs.next())
                    {
                        notificationUrl = rs.getString("notificationUrl");
                        clkey = rs.getString("clkey");
                        checksumAlgo = rs.getString("checksumalgo");

                    }
                    checksum = Checksum.generateChecksumV2(orderNumber, String.valueOf(captureAmount), status, clkey, checksumAlgo);

                    Map<String, String> map = new TreeMap<String, String>();
                    map.put("status", URLEncoder.encode(status, "UTF-8"));
                    map.put("message", URLEncoder.encode(message, "UTF-8"));
                    map.put("desc", URLEncoder.encode(orderNumber, "UTF-8"));
                    map.put("amount", URLEncoder.encode(captureAmount, "UTF-8"));
                    map.put("newchecksum", URLEncoder.encode(checksum, "UTF-8"));
                    map.put("trackingid", URLEncoder.encode(trackingId, "UTF-8"));

                    if(notificationUrl!=null && !notificationUrl.equals(""))
                    {
                        String html = generateAutoSubmitForm(notificationUrl,map);
                        res.setContentType("text/html;charset=UTF-8");
                        res.setCharacterEncoding("UTF-8");

                        res.getWriter().write(html);
                        log.debug("NOTIFICATION URL="+notificationUrl);
                        transactionLogger.debug("NOTIFICATION URL="+notificationUrl);
                    }

                }
            }
        }
        catch(Exception e){
            log.error("Error while processing transaction response:",e);
            transactionLogger.error("Error while processing transaction response:",e);
            //e.printStackTrace();
        }
        finally {
            Database.closeConnection(con);
            Database.closePreparedStatement(p);
            Database.closePreparedStatement(p1);
            Database.closePreparedStatement(pToid);
            Database.closeResultSet(rs);
            Database.closeResultSet(rs1);
            Database.closeResultSet(rsToid);
        }
    }

    private static String generateAutoSubmitForm(String actionUrl, Map<String, String> paramMap) {
        StringBuilder html = new StringBuilder();
        //html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(actionUrl).append("\" method=\"post\">\n");

        for (String key : paramMap.keySet()) {
            html.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + paramMap.get(key) + "\">\n");
        }
        html.append("</form>\n");
        html.append("<script language=\"javascript\">");
        html.append("document.pay_form.submit();");
        html.append("</script>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }

}