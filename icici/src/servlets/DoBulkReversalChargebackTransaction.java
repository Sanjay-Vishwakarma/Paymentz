
import com.directi.pg.*;
import com.logicboxes.util.Util;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.RequestDispatcher;
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
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: May 29, 2012
 * Time: 1:38:54 PM
 * Process for Reversal chargback
 */
public class DoBulkReversalChargebackTransaction extends HttpServlet
{   java.util.Date dt = null;
    StringBuffer merchantbody = null;
    StringBuffer merchantsubject = null;
    static Logger logger = new Logger(DoBulkReversalChargebackTransaction.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
     public synchronized void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();


        User user =  (User)session.getAttribute("ESAPIUserSessionKey");


            if (!Admin.isLoggedIn(session))
            {   logger.debug("invalid user");
                res.sendRedirect("/icici/admin/logout.jsp");
                return;
            }
         res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        //String icicitransid = null;
        String transid = null;
        String orderid = null;
        String merchantid = null;
        String icicimerchantid = null;
        String description = null;
        String authdate = null;
        String capdate = null;
        String company_name = null;
        String contact_emails = null;
        String authval = "";
        String captureval = "";
        String refundval = "";
        String chargebackval = "";
        String transactionOrderDescription = "";
        String cardholder = "";
        String emailaddr = "";
        String ipaddress = "";

        Connection conn = null;
        PreparedStatement p1 = null;
        PreparedStatement p2 = null;
        PreparedStatement p3 = null;
        PreparedStatement p4 = null;
        ResultSet cbReversal = null;
        TransactionEntry transactionEntry = null;
        try
        {
            conn = Database.getConnection();
            transactionEntry = new TransactionEntry();


            String[] icicitransidStr = req.getParameterValues("ch_reversal");

            if (Functions.checkArrayNull(icicitransidStr) == null)
            {
                out.print(Functions.ShowMessage("Error", "Select at least one transaction"));
                return;
            }

            StringBuilder sSuccessMessage = new StringBuilder();
            StringBuilder sErrorMessage = new StringBuilder();
            sErrorMessage.append("Following Transactions are failed while Chargeback Reversal Processing <br> \r\n");
            sSuccessMessage.append("Following Transactions are successful during Chargeback Reversal Processing <br> \r\n");

            for (String icicitransid : icicitransidStr)
            {

                if (!icicitransid.trim().equals(""))
                {
                int intChargebackProcessId = -1;
                try
                {
                    intChargebackProcessId = Integer.parseInt(req.getParameter("hid_" + icicitransid ));
                }
                catch (NumberFormatException ex)
                {
                    logger.error("Invalid Chargeback Process Id" + req.getParameter("hid_" + icicitransid  + " for Tracking Id="+icicitransid));
                    sErrorMessage.append("Tracking Id="+icicitransid +"<br>");
                    continue;
                }

                try
                {
                    conn = Database.getConnection();
                    String query = "select T.*,T.captureamount-T.refundamount as chargebackamount,date_format(from_unixtime(T.dtstamp),'%d/%m/%Y') as authdate,date_format(from_unixtime(T.podbatch),'%d/%m/%Y') as capdate,M.company_name,M.contact_emails from transaction_icicicredit as T,members as M where icicitransid=? and T.toid=M.memberid and status='chargeback'";
                    p1=conn.prepareStatement(query);
                    p1.setString(1,icicitransid);
                    cbReversal = p1.executeQuery();
                    if (cbReversal.next())
                    {
                        transid = cbReversal.getString("transid");
                        cardholder = cbReversal.getString("name");
                        emailaddr = cbReversal.getString("emailaddr");
                        if (cbReversal.getString("ipaddress") != null)
                            ipaddress = cbReversal.getString("ipaddress");
                        orderid = cbReversal.getString("description");
                        authval = cbReversal.getString("amount");
                        captureval = cbReversal.getString("captureamount");
                        refundval = cbReversal.getString("refundamount");
                        chargebackval = cbReversal.getString("chargebackamount");
                        merchantid = cbReversal.getString("toid");
                        icicimerchantid = cbReversal.getString("icicimerchantid");
                        company_name = cbReversal.getString("company_name");
                        contact_emails = cbReversal.getString("contact_emails");
                        authdate = cbReversal.getString("authdate");
                        capdate = cbReversal.getString("capdate");
                        if (cbReversal.getString("orderdescription") != null)
                            transactionOrderDescription = cbReversal.getString("orderdescription");
                    }

                    dt = new java.util.Date();


                    Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
                    description = "Reversal of Chargeback of " + transid + " ( " + orderid + " )";
                    StringBuffer query1 = new StringBuffer("update transaction_icicicredit set");

                    if (Double.parseDouble(refundval) > 0)
                        query1.append(" status='reversed'");  //if it is partially reversed transaction then set status to reversed
                    else
                        query1.append(" status='settled'");

                    query1.append(" where icicitransid=" + ESAPI.encoder().encodeForSQL(me,icicitransid) + " and status='chargeback'");


                    int result = Database.executeUpdate(query1.toString(), conn);


                    if (result != 1)
                    {
                        logger.error("This Chargeback transaction cannot be Reversed for Tracking Id="+icicitransid);
                        sErrorMessage.append("Tracking Id="+icicitransid +"<br>");
                        continue;
                        //out.println(Functions.ShowMessage("Error", "This Chargeback transaction cannot be Reversed."));
                        //return;
                    }

                    logger.debug("icicimerchantid=" + icicimerchantid);
                    logger.debug("merchantid=" + merchantid);
                    logger.debug("description=" + description);
                    logger.debug("chargebackval=" + chargebackval);
                    

                    String query2 = "insert into transactions (toid, totype, fromid, fromtype, description, amount, dtstamp,type) values (? ,'payment',? , 'icicicredit' , ? ,?,?,'chargebackreversal')";
                    p2=conn.prepareStatement(query2);
                    p2.setString(1,merchantid);
                    p2.setString(2,icicimerchantid);
                    p2.setString(3,description);
                    p2.setString(4,chargebackval);
                    p2.setLong(5,(dt.getTime() / 1000));

                    logger.info(query2);
                    int result2= p2.executeUpdate();
                    logger.debug("Merchant ID: " + merchantid);

                    merchantbody = new StringBuffer();
                    merchantsubject = new StringBuffer();
                    merchantbody.append("Dear Sir/Madam. \r\n\r\n");
                    merchantbody.append("Below is the details of the transaction that was taken place through your site.This transaction was claimed chargeback. \r\n\r\n");
                    merchantbody.append("Merchant ID: " + merchantid + "\r\n");
                    merchantbody.append("Company Name: " + company_name + "\r\n\r\n");
                    merchantbody.append("Description: " + orderid + "\r\n");
                    merchantbody.append("Order Description: " + transactionOrderDescription + "\r\n");

                    merchantbody.append("Transaction Amount: Rs. " + authval + "\r\n");
                    merchantbody.append("Capture Amount: Rs. " + captureval + "\r\n");
                    merchantbody.append("Refund Amount: Rs. " + refundval + "\r\n");
                    merchantbody.append("Chargeback Amount: Rs. " + chargebackval + "\r\n");

                    merchantbody.append("Card Holder : " + cardholder + "\r\n");
                    merchantbody.append("Tracking id: " + icicitransid + "\r\n\r\n");
                    merchantbody.append("Date of Authorization: " + authdate + "\r\n");
                    merchantbody.append("Date of Capture: " + capdate + "\r\n\r\n");

                    merchantbody.append("Customer Email Address: " + emailaddr + "\r\n");
                    merchantbody.append("IP	Address: " + ipaddress + "\r\n\r\n\r\n");


                    merchantbody.append("This transaction is accepted by customer.So now you are credited with the chargeback amount.Please check it in your account.\r\n");
                    merchantbody.append("\r\nAdmin Support\r\n");

                    logger.info("calling SendMAil for merchant - chargeback refund");

                    merchantsubject.append("Chargeback reversed -");
                    //  Mail.sendmail("alpesh.s@directi.com",contact_emails, merchantsubject.toString() + company_name, merchantbody.toString());
                    Mail.sendAdminMail(merchantsubject.toString() + company_name, merchantbody.toString());

                    logger.info("called SendMAil for transacute -- chargeback reversed");
                    //sSuccessMessage.append("Sending Mail to following trackingID:-<BR>"+icicitransid);
                    merchantsubject.append(" Orderid:" + orderid + "Chargeback Amount:" + chargebackval + " Card Holder:" + cardholder);
                   // String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
                    //Mail.sendmail(contact_emails, fromAddress, null, null, merchantsubject.toString(), merchantbody.toString());

                    if (result2 != 1)
                    {
                        logger.error("Failed to Reversed. No rows Updated Tracking Id="+icicitransid +"<br>");
                        sErrorMessage.append("Tracking Id="+icicitransid +"<br>");
                        continue;

                        //out.println(Functions.ShowMessage("Error", "Failed to Reversed. No rows Updated"));
                        //return;
                    }
                    sSuccessMessage.append("successful reversal for following TrackingID:-<BR>"+icicitransid);
                    //out.println(Functions.ShowMessage("Information", "Chargeback Reversed."));

                 }
                    catch (SQLException e)
                    {   logger.error("SQL ERROR",e);
                        sErrorMessage.append("Tracking Id="+icicitransid +"<br>");
                        continue;
                         //out.println(Functions.ShowMessage("Error", e.toString()));
                    }
                  try
                {
                    //Marking this transaction as processed
                    String sQuery = "update chargeback_transaction_list set processed='Y' where icicitransid=? and fk_chargeback_process_id=?";
                    p2= conn.prepareStatement(sQuery);

                    p2.setString(1,icicitransid);
                    p2.setInt(2,intChargebackProcessId);
                    p2.executeUpdate();


                    sQuery = "update chargeback_report_process_history set parsed='Y' where chargeback_process_id=? and no_of_rows=no_of_transactions and (select count(*) from chargeback_transaction_list where fk_chargeback_process_id=? and processed!='y')=0";
                    p3= conn.prepareStatement(sQuery);

                    p3.setInt(1,intChargebackProcessId);
                    p3.setInt(2,intChargebackProcessId);
                    p3.executeUpdate();


                    sSuccessMessage.append("Tracking Id="+icicitransid + "<br>" );
                }
                  catch (Exception e)
                {
                    logger.error("Error while Processing Chargeback=" + e.getMessage() + " for Tracking Id="+icicitransid);
                    sErrorMessage.append("Transaction successfully chargeback but not marked as 'Y' in chargeback_transaction_list. Tracking Id="+icicitransid +"<br>");

                }


                }
            }
            StringBuilder chargeBackMessage = new StringBuilder();
            chargeBackMessage.append(sSuccessMessage.toString());
            chargeBackMessage.append("<BR/>");
            chargeBackMessage.append(sErrorMessage.toString());

            String redirectpage = "/servlet/AdminChargebackList?ctoken="+user.getCSRFToken();
            req.setAttribute("cbmessage", chargeBackMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
            rd.forward(req, res);

        }
        catch (SystemError systemError)
        {
            logger.info("Error while Chargeback :" + Util.getStackTrace(systemError));
            out.print(Functions.ShowMessage("Error", "Error while connecting to Database"));
        }
        finally
        {
            Database.closePreparedStatement(p1);
            Database.closePreparedStatement(p2);
            Database.closePreparedStatement(p3);
            Database.closePreparedStatement(p4);
            Database.closeResultSet(cbReversal);
            Database.closeConnection(conn);
        }
    }

}
