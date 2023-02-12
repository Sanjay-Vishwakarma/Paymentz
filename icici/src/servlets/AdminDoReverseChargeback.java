import com.directi.pg.*;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;

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
import java.util.ArrayList;
import java.util.List;


public class AdminDoReverseChargeback extends HttpServlet
{

    private static Logger logger = new Logger(AdminDoReverseChargeback.class.getName());

    StringBuffer merchantbody = null;
    StringBuffer merchantsubject = null;

    Database db = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    PreparedStatement p = null;
    ResultSet rs = null;
    ResultSet rs1 = null;
    String query = null;

    java.util.Date dt = null;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        logger.debug("Entering in AdminDoReverseChargeback");
        String icicitransid = null;
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

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        db = new Database();

        merchantbody = new StringBuffer();
        merchantsubject = new StringBuffer();
        merchantbody.append("Dear Sir/Madam. \r\n\r\n");
        merchantbody.append("Below is the details of the transaction that was taken place through your site.This transaction was claimed chargeback. \r\n\r\n");

        try
        {
            validateMandatoryParameter(req);
            icicitransid = req.getParameter("icicitransid");

            logger.debug("parameter icicitransid"+req.getParameter("icicitransid"));
            if (!icicitransid.trim().equals(""))
            {

                conn = db.getConnection();
                query = "select T.*,T.captureamount-T.refundamount as chargebackamount,date_format(from_unixtime(T.dtstamp),'%d/%m/%Y') as authdate,date_format(from_unixtime(T.podbatch),'%d/%m/%Y') as capdate,M.company_name,M.contact_emails from transaction_icicicredit as T,members as M where icicitransid=? and T.toid=M.memberid and status='chargeback'";

                pstmt = conn.prepareStatement(query);
                pstmt.setString(1,icicitransid);
                rs1 = pstmt.executeQuery();
                if (rs1.next())
                {
                    transid = rs1.getString("transid");
                    cardholder = rs1.getString("name");
                    emailaddr = rs1.getString("emailaddr");
                    if (rs1.getString("ipaddress") != null)
                        ipaddress = rs1.getString("ipaddress");
                    orderid = rs1.getString("description");
                    authval = rs1.getString("amount");
                    captureval = rs1.getString("captureamount");
                    refundval = rs1.getString("refundamount");
                    chargebackval = rs1.getString("chargebackamount");
                    merchantid = rs1.getString("toid");
                    icicimerchantid = rs1.getString("icicimerchantid");
                    company_name = rs1.getString("company_name");
                    contact_emails = rs1.getString("contact_emails");
                    authdate = rs1.getString("authdate");
                    capdate = rs1.getString("capdate");
                    if (rs1.getString("orderdescription") != null)
                        transactionOrderDescription = rs1.getString("orderdescription");
                }

                dt = new java.util.Date();
                query = "select * from transactions where description like 'Reversal of " + transid + "%' ";//and type='chargeback'"; in older transaction type is not set.

                rs = db.executeQuery(query, conn);
                if (rs.next())
                {

                    transid = rs.getString("transid");
                    description = "Reversal of Chargeback of " + transid + " ( " + orderid + " )";
                    StringBuffer query1 = new StringBuffer("update transaction_icicicredit set");
                    Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
                    if (Double.parseDouble(refundval) > 0)
                        query1.append(" status='reversed'");  //if it is partially reversed transaction then set status to reversed
                    else
                        query1.append(" status='settled'");

                    query1.append(" where icicitransid=" + ESAPI.encoder().encodeForSQL(me,icicitransid) + " and status='chargeback'");
                    int result = db.executeUpdate(query1.toString(), conn);
                    logger.debug("No of Rows updated : " + result + "<br>");

                    if (result != 1)
                    {
                        out.println(Functions.ShowMessage("Error", "This Chargeback transaction cannot be Reversed."));
                        return;
                    }

                    if (Double.parseDouble(refundval) > 0)
                    {
                         // Start : Added for Action and Status Entry in Action History table
                         ActionEntry entry = new ActionEntry();
                         int actionEntry = entry.actionEntry(icicitransid,refundval,ActionEntry.ACTION_PARTIAL_CHARGEBACK_REVERSED,ActionEntry.STATUS_PARTIAL_CHARGEBACK_REVERSED);
                         entry.closeConnection();
                         // End : Added for Action and Status Entry in Action History table
                    }
                    else
                    {
                        // Start : Added for Action and Status Entry in Action History table
                         ActionEntry entry = new ActionEntry();
                         int actionEntry = entry.actionEntry(icicitransid,refundval,ActionEntry.ACTION_CHARGEBACK_REVERSED,ActionEntry.STATUS_CHARGEBACK_REVERSED);
                         entry.closeConnection();
                         // End : Added for Action and Status Entry in Action History table
                    }

                    logger.debug("merchantid=" + icicimerchantid);
                    query = "insert into transactions (toid, totype, fromid, fromtype, description, amount, dtstamp,type) values (? ,'payment',? , 'icicicredit' , ? , ?,?,'chargebackreversal')";
                    logger.info(query);
                    p = conn.prepareStatement(query);
                    p.setString(1,merchantid);
                    p.setString(2,icicimerchantid);
                    p.setString(3,description);
                    p.setString(4,chargebackval);
                    p.setDouble(5,(dt.getTime() / 1000));
                    result = p.executeUpdate();

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

                    merchantsubject.append(" Orderid:" + orderid + "Chargeback Amount:" + chargebackval + " Card Holder:" + cardholder);
                    //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
                    //Mail.sendmail(contact_emails, fromAddress, null, null, merchantsubject.toString(), merchantbody.toString());
                    logger.info("called SendMAil for merchant -- chargeback reversed");

                     sSuccessMessage.append("Chargeback Reversed of "+icicitransid+" transactionID  \r\n");
                }
                else
                {
                      sErrorMessage.append("No entry in Transaction table <br> \r\n");
                     logger.debug(sErrorMessage);
                }
            }
            else
            {
                sErrorMessage.append("Invalid Tracking ID <br> \r\n");
                logger.debug(sErrorMessage);
                //return;
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError while reversal :",se);
           sErrorMessage.append("Transaction is not complet <br> \r\n");
        }
        catch (Exception e)
        {
            logger.error("Error while reversal :",e);
            sErrorMessage.append("Transaction is not complet <br> \r\n");
        }//try catch ends
        finally
        {
            Database.closeResultSet(rs1);
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(p);
            Database.closeConnection(conn);
        }

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        String redirectpage = "/servlet/AdminChargebackReverseList?ctoken="+user.getCSRFToken();
        req.setAttribute("cbreversemessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
    }//post ends
    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.ICICITRANSEID);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}