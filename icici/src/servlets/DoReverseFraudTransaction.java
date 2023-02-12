import com.directi.pg.Admin;
import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;


public class DoReverseFraudTransaction extends HttpServlet
{

    static Logger logger = new Logger(DoReverseFraudTransaction.class.getName());
    private static String OLD_MerchantId = ApplicationProperties.getProperty("ICICI_OLD_MERCHANTID");
    private static List listOfOldMerchantIDs = Functions.getVectorFromArray(Functions.convertCommaseperatedStringtoStringarr(OLD_MerchantId));

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        Transaction transaction = new Transaction();
        HttpSession session = req.getSession();

            User user =  (User)session.getAttribute("ESAPIUserSessionKey");
            if (!Admin.isLoggedIn(session))
            {   logger.debug("invalid user");
                res.sendRedirect("/icici/logout.jsp");
                return;
            }
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        Connection conn = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        TransactionEntry transactionEntry = null;
        transactionEntry = new TransactionEntry();

        String query = null;
        String icicimerchantid = null;
        String refundamount = null;
        String authamount = null;
        String captureamount = null;
        String description = null;
        String authid = null;
        String captureid = null;
        String toid = null;
        String company_name = null;
        String contact_emails = null;

        //Addded for fraud reversal mail
        String accountId = null;
        String cardholdername = null;
        BigDecimal bdConst = new BigDecimal("0.01");
        List failList = new ArrayList();
        String[] icicitransidStr = req.getParameterValues("icicitransid");

        if (Functions.checkArrayNull(icicitransidStr) == null)
        {
            sErrorMessage.append("Select at least one transaction.");
        }
        //Added for fraud reversal mail
        HashMap<String,Collection> membersMap = new HashMap<String,Collection>();
        Collection<Hashtable> listOfRefunds = null;
        Hashtable refundDetails = null;

        for (String icicitransid : icicitransidStr)
        {
            boolean refunded = false;
            StringBuffer mailbody = null;

            refundamount = req.getParameter("refundamount_" + icicitransid);
            try
            {
                Double.parseDouble(refundamount);
            }
            catch (NumberFormatException ex)
            {
                logger.error("Invalid refund amount " ,ex);
                failList.add(icicitransid);
                continue;
            }

            if (!Functions.checkAccuracy(refundamount, 2))
            {
                logger.debug("Refund Amount should be 2 decimal places accurate." + refundamount);
                failList.add(icicitransid);
                continue;
            }

            if (Functions.checkStringNull(icicitransid) != null)
            {
                try
                {
                    conn = Database.getConnection();
                    query = "select T.*,M.company_name,M.contact_emails,M.currency,M.taxper,M.reversalcharge from transaction_icicicredit as T,members as M where T.icicitransid=? and T.toid=M.memberid and status='settled'";
                    p = conn.prepareStatement(query);
                    p.setString(1,icicitransid);
                    rs = p.executeQuery();

                    if (rs.next())
                    {
                        icicitransid = rs.getString("icicitransid");
                        String transid = rs.getString("transid");
                        description = "Reversal of " + transid + " ( Fraudulent Transaction ) ";
                        authid = rs.getString("authid");
                        captureid = rs.getString("captureid");
                        String captureCode = rs.getString("capturecode");
                        String captureRRN = rs.getString("capturereceiptno");
                        toid = rs.getString("toid");
                        company_name = rs.getString("company_name");
                        contact_emails = rs.getString("contact_emails");
                        int chargeper = rs.getInt("chargeper");
                        int transactiontaxper = rs.getInt("T.taxper");
                        int currtaxper = rs.getInt("M.taxper");

                        String rsdescription = rs.getString("description");
                        icicimerchantid = rs.getString("icicimerchantid");
                        authamount = rs.getString("amount");
                        captureamount = rs.getString("captureamount");

                        accountId = rs.getString("accountid");
                        cardholdername = rs.getString("name");

                        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                        String reversalCharges = rs.getString("reversalcharge");

                        if (Double.parseDouble(refundamount) > Double.parseDouble(captureamount))
                        {
                            logger.debug("Refund Amount " + refundamount + " is greater than capture amount " + captureamount);
                            failList.add(icicitransid);
                            continue;
                        }
                        transactionEntry.reverseTransaction(refundamount, icicitransid, toid, icicimerchantid, description, chargeper, account, rsdescription, transid, currtaxper, bdConst, transactiontaxper, reversalCharges, toid);
                        //Mark this transaction as processed as transaction entry is already made.
                        Database.executeUpdate("update fraud_transaction_list set processed='Y' where icicitransid="+icicitransid, conn);

                        //Now Reverse transaction on the gateway
                        AbstractPaymentGateway pg = AbstractPaymentGateway.getGateway(accountId);
                        logger.info("callng processRefund");

                        Hashtable hash = pg.processRefund(icicitransid, refundamount, captureid, captureCode, captureRRN);
                        if (hash != null && ((String) hash.get("refundqsiresponsecode")).equals("0"))
                        {
                            StringBuffer sb = new StringBuffer();
                            sb.append("update transaction_icicicredit set status='reversed'");

                            Enumeration e = hash.keys();
                            while (e.hasMoreElements())
                            {
                                String key = (String) e.nextElement();
                                sb.append(" , " + key + " = '" + (String) hash.get(key) + "' ");
                            }

                            sb.append(" where icicitransid=" + icicitransid + " and status='markedforreversal'");

                            int rows = Database.executeUpdate(sb.toString(), conn);
                            logger.debug("No of Rows updated : " + rows + "<br>");

                            if (rows == 1)
                            {
                                refunded = true;
                            }
                        }

                     // preparing collections of refunds as per merchant
                        refundDetails = new Hashtable();
                        refundDetails.put("icicitransid",icicitransid);
                        refundDetails.put("captureamount",captureamount);
                        refundDetails.put("refundamount",refundamount);
                        refundDetails.put("description",rsdescription);
                        refundDetails.put("accountid",accountId);
                        refundDetails.put("cardholdername",Functions.decryptString(cardholdername));

                        if(membersMap.get(toid)==null)
                        {
                            listOfRefunds = new ArrayList<Hashtable>();
                            listOfRefunds.add(refundDetails);
                            membersMap.put(toid,listOfRefunds);
                        }
                        else
                        {
                             listOfRefunds = membersMap.get(toid);
                             listOfRefunds.add(refundDetails);
                             membersMap.put(toid,listOfRefunds);
                        }
                    }
                }
                catch (SystemError se)
                {
                    logger.error("Error while reversal :" ,se);
                }
                catch (Exception e)
                {
                    logger.error("Error while reversal :",e);
                }
                finally
                {
                    Database.closeResultSet(rs);
                    Database.closePreparedStatement(p);
                    Database.closeConnection(conn);
                }//try catch ends

                //If refund did not happen successfully then send mail to admin to refund transaction manually
                try
                {
                    if (!refunded)
                    {
                        failList.add(icicitransid);

                        mailbody = new StringBuffer("This transaction has to be reversed .\r\n\r\n");
                        mailbody.append("Icici Merchantid").append(" : ").append(icicimerchantid).append("\r\n");
                        mailbody.append("Icicitransid").append(" : ").append(icicitransid).append("\r\n");
                        mailbody.append("Transaction Amount").append(" : ").append(authamount).append("\r\n");
                        mailbody.append("Capture Amount").append(" : ").append(captureamount).append("\r\n");
                        mailbody.append("Refundamount").append(" : ").append(refundamount).append("\r\n");
                        mailbody.append("Description").append(" : ").append(description).append("\r\n");
                        mailbody.append("Authid").append(" : ").append(authid).append("\r\n");
                        mailbody.append("Captureid").append(" : ").append(captureid).append("\r\n");
                        mailbody.append("Toid").append(" : ").append(toid).append("\r\n");
                        mailbody.append("Company Name").append(" : ").append(company_name).append("\r\n");

                        logger.info("calling SendMAil for support- reversal");
                        String adminEmail = ApplicationProperties.getProperty("COMPANY_ADMIN_EMAIL");
                        Mail.sendmail(contact_emails, adminEmail, "Reverse these Transaction.", mailbody.toString());
                        logger.info("called SendMAil for support-- reversal");
                    }
                }
                catch (SystemError se)
                {
                    logger.error("Error while reversal :",se);
                }
            }
        }

        transactionEntry.closeConnection();

        Set members = membersMap.keySet();
        for(Object memberid : members )
        {
            logger.info("calling sendReverseFraudTransactionMail");
            try
            {
            transaction.sendReverseFraudTransactionMail((ArrayList)membersMap.get(memberid),(String)memberid);
            }
            catch(SystemError se)
            {
               logger.error("Error while sending reversal mail:",se);
            }
            logger.info("called sendReverseFraudTransactionMail");
        }

        if (failList.size() > 0)

            sErrorMessage.append("Error while reversing transactions with icicitransid - " + Functions.commaseperatedString(Util.getStringArrayFromVector(failList)));
        else
            sSuccessMessage.append("Selected transactions reversed successfully");

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        logger.debug("forwarding to member preference");
        RequestDispatcher rd = req.getRequestDispatcher("/servlet/AdminFraudList?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }//post ends
}