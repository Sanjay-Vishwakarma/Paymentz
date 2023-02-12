import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import org.owasp.esapi.User;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class RawConfirmBatch extends HttpServlet
{

    private static Logger logger = new Logger(RawConfirmBatch.class.getName());


    java.util.Date dt = null;
    ResultSet rs2 = null;


    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        Merchants merchants = new Merchants();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        logger.debug("Entering in RawConfirmBatch");
        String query = null;
        String merchantid = null;
        String pathtoraw = null;

        StringBuffer debugger = new StringBuffer();

        String description = null;

        int result = 0;
        int count = 0;
        int toid = 0;
        int prevtoid = 0;
        int trackingId = 0;
        int paymentTransId = 0;
        BigDecimal peramt = null;
        boolean custremindermail = false;
        PrintWriter out = response.getWriter();
        ServletContext application = getServletContext();
        response.setContentType("text/html");
        BigDecimal bdConst = new BigDecimal("0.01");
        TransactionEntry transactionEntry = new TransactionEntry();
        //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");

        try
        {
            pathtoraw = ApplicationProperties.getProperty("MPR_FILE_STORE");
        }
        catch (Exception ex)
        {
            logger.error("ICICI Merchanid Not Found",ex);
            return;
        }


        StringBuffer rawdataBuf = new StringBuffer();

        String filename = request.getParameter("filename");

        try
        {
            BufferedReader rin = new BufferedReader(new FileReader(pathtoraw + filename));
            String temp = null;

            while ((temp = rin.readLine()) != null)
            {
                rawdataBuf.append(temp + "\r\n");
            }
            rin.close();

        }
        catch (Exception ex)
        {   logger.error("Eaw file not found, Occure Exception ",ex);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            out.println(Functions.ShowMessage("Raw File Not Found", sw.toString()));

            return;
        }

        //reset variables
        toid = 0;
        prevtoid = 0;
        trackingId = 0;
        paymentTransId = 0;

        //application.setAttribute(podbatch,podbatch);

        Hashtable mailHash = new Hashtable();
        Vector custMailVect = new Vector();
        Vector noMailCustEmailVect = new Vector();
        Hashtable unSettledHash = new Hashtable();
        Hashtable settledHash = new Hashtable();
        int notSettledCount = 0;
        int settledCount = 0;
        int totalCount = 0;


        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal settledAmount = new BigDecimal("0");
        BigDecimal unsettledAmount = new BigDecimal("0");

        BigDecimal percent = null;

        StringBuffer body = new StringBuffer();
        body.append("<html><head><style>\r\n.tr1{background:#FaEEE0;text-valign: center;text-align: LEFT;font-size:11px;font-family:arial,verdana,helvetica}\r\n\r\n.heading{background:#800000;text-valign: center;text-align: LEFT;font-size:12px;color:#ffffff;font-family:arial,verdana,helvetica}\r\n.tr2{background:#fffeec;text-valign: center;text-align: LEFT;font-size:11px;font-family:arial,verdana,helvetica}\r\n</style></head><body text=\"#800000\">");


        String rawdata = rawdataBuf.toString();

        if (rawdata == null)
        {
            out.println("Raw data not passed <br><br>");
            return;
        }

        if (filename == null)
        {
            out.println("filename not passed <br><br>");
            return;
        }

        StringTokenizer stz = new StringTokenizer(rawdata, "\r\n");
        Connection conn = null;
        PreparedStatement pst = null;
        PreparedStatement p1 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        try
        {

            conn = Database.getConnection();


            while (stz.hasMoreElements())
            {
                String value = (String) stz.nextElement();
                application.log("Value of Row : " + value);
                StringTokenizer innerStz = new StringTokenizer(value, "^");
                totalCount++;

                if (innerStz.hasMoreElements())
                {
                    innerStz.nextElement();
                    String date = (String) innerStz.nextElement();
                    String captureRRN = (String) innerStz.nextElement();
                    String authCode = (String) innerStz.nextElement();
                    String tempamount = (String) innerStz.nextElement();

                    BigDecimal amount = new BigDecimal(tempamount.trim());

                    amount = amount.multiply(new BigDecimal("0.01"));

                    debugger.append("amount " + amount + "<br><br>");

                    totalAmount = totalAmount.add(amount);


                    query = "select *,bin_details.last_four as cardnumber,expdate,date_format(from_unixtime(transaction_icicicredit.dtstamp),'%d-%m-%Y') as date,transaction_icicicredit.chargeper as transpercfee,transaction_icicicredit.fixamount as transfixfee,company_name,sitename,custremindermail,currency,templateamount,templatecurrency,transaction_icicicredit.taxper,transaction_icicicredit.accountid from transaction_icicicredit,members,bin_details where captureamount=? and capturereceiptno=? and status='podsent' and toid=memberid and transaction_icicicredit.icicitransid = bin_details.icicitransid";
                    pst=conn.prepareStatement(query);
                    pst.setDouble(1,amount.doubleValue());
                    pst.setString(2,captureRRN);
                    rs = pst.executeQuery();


                    if (rs.next())
                    {
                        dt = new java.util.Date();
                        toid = rs.getInt("toid");
                        custremindermail = rs.getBoolean("custremindermail");
                        String name = rs.getString("name");
                        String ccnum =rs.getString("cardnumber");
                        String expdate = PzEncryptor.decryptExpiryDate(rs.getString("expdate"));
                        String emailaddr = rs.getString("emailaddr");
                        String companyname = rs.getString("company_name");
                        String sitename = rs.getString("sitename");
                        String brandname = rs.getString("brandname");
                        description = rs.getString("description");
                        String orderdescription = rs.getString("orderdescription");
                        String transAmount = rs.getString("amount");

                        String taxper = rs.getString("taxper");
                        String templateAmount = rs.getString("templateamount");
                        String templateCurrency = rs.getString("templatecurrency");
                        String accountId = rs.getString("accountid");
                        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                        trackingId = rs.getInt("icicitransid");
                        merchantid = account.getMerchantId();
                        String currency = account.getCurrency();
                        //Calcutating aount in rupees
                        BigDecimal amt = new BigDecimal(rs.getString("captureamount"));
                        //amt=amount.divide(new BigDecimal(100.00),2);
                        //amt = new BigDecimal(/100.0);
                        amt = amt.setScale(2, BigDecimal.ROUND_HALF_UP);

                        Hashtable innerHash = new Hashtable();
                        innerHash.put("NAME", name);
                        innerHash.put("CCNUM", ccnum);
                        innerHash.put("EXPDATE", expdate);
                        innerHash.put("EMAILADDR", emailaddr);
                        innerHash.put("TRACKINGID", trackingId + "");
                        innerHash.put("DESCRIPTION", description);
                        innerHash.put("ORDERDESCRIPTION", orderdescription);
                        innerHash.put("COMPANYNAME", companyname);
                        innerHash.put("SITENAME", sitename);
                        innerHash.put("BRANDNAME", brandname);
                        innerHash.put("TRANSAMOUNT", transAmount);
                        innerHash.put("CAPAMOUNT", rs.getString("captureamount") + "");
                        innerHash.put("DATE", rs.getString("date"));
                        innerHash.put("CURRENCY", currency);
                        if (templateCurrency != null && !currency.equals(templateCurrency))
                            innerHash.put("TMPL_TRANSACTION", "( approximately " + templateCurrency + " " + templateAmount + " ) ");
                        String displayName = account.getDisplayName();
                        innerHash.put("DISPLAYNAME", displayName);

                        custMailVect.add(innerHash);
                        innerHash=null;
                        if (!custremindermail)
                            noMailCustEmailVect.add(emailaddr);

                        //when all transactions are started entering chargeper at the time of transaction
                        //We can comment above step and directly retrieve specific charge from transaction_icicicredit table
                        //we will charge at the percentage that was there at the time of transaction not at the time of settlement

                        String transfee = rs.getString("transpercfee");
                        String transfixfee = rs.getString("transfixfee");
                        percent = new BigDecimal(transfee);

                        paymentTransId = transactionEntry.creditTransaction(toid, merchantid, description, amt, percent, transfixfee, account, taxper, trackingId);

                        StringBuffer tempBuf = new StringBuffer();

                        StringBuffer prevMail = (StringBuffer) mailHash.get("" + toid);

                        if (prevMail != null)
                            tempBuf.append(prevMail.toString());

                        tempBuf.append("<tr class=\"tr" + (settledCount % 2 + 1) + "\">");
                        tempBuf.append("<td>&nbsp;" + trackingId + "</td>");
                        tempBuf.append("<td>&nbsp;" + amount + "</td>");
                        tempBuf.append("<td>&nbsp;" + paymentTransId + "</td>");
                        tempBuf.append("<td>&nbsp;" + description + "</td>");
                        tempBuf.append("</tr>");

                        mailHash.put("" + toid, tempBuf);

                        settledAmount = settledAmount.add(amount);
                        settledCount++;
                        //settledHash.put(""+settledCount,value);
                        settledHash.put("" + settledCount, date + "\t" + captureRRN + "\t" + authCode + "\t" + amount);
                        ccnum=null;
                        expdate=null;
                    }//if rs ends
                    else
                    {
                        unsettledAmount = unsettledAmount.add(amount);
                        notSettledCount++;
                        //unSettledHash.put(""+notSettledCount,value);
                        unSettledHash.put("" + notSettledCount, date + "\t" + captureRRN + "\t" + authCode + "\t" + amount);
                    }
                }//if ends
            }//while ends
            transactionEntry.closeConnection();
            debugger.append("<br><br>");
            debugger.append("Mail content " + mailHash);

            dt = new java.util.Date();
            query = "insert into rawsettlement (filename, totalno, settledno, unsettledno, amount,settledamount, unsettledamount, dtstamp) values (?,?,?,?,?,?,?,?)";

            debugger.append(query + "<br>");
            pst=conn.prepareStatement(query);
            pst.setString(1,filename);
            pst.setInt(2,totalCount);
            pst.setInt(3,settledCount);
            pst.setInt(4,notSettledCount);
            pst.setDouble(5,totalAmount.doubleValue());
            pst.setDouble(6,settledAmount.doubleValue());
            pst.setDouble(7,unsettledAmount.doubleValue());
            pst.setLong(8,(dt.getTime() / 1000));
            result = pst.executeUpdate();

            //sending mail to the Merchant
            Enumeration enu = mailHash.keys();

            while (enu.hasMoreElements())
            {
                String memberid = (String) enu.nextElement();

                StringBuffer mail = new StringBuffer(body.toString());
                mail.append("<table><tr><td>This is to inform you that Funds for the following Transaction has been transfered Succesfully to your Account.</td></tr>");
                mail.append("<tr><td>&nbsp;</td></tr>");
                mail.append("<tr><td>");
                mail.append("<b>Member ID:</b> " + memberid + "<br>");

                try
                {
                    String companyname = merchants.getCompany(memberid);
                    mail.append("<b>Company Name:</b> " + companyname + "<br>");
                }
                catch (Exception e)
                {
                    logger.error("Company name not found for memberid ",e);
                }
                TransactionEntry te = new TransactionEntry(Integer.parseInt(memberid));
                mail.append("<b>Current Balance:</b> " + te.getBalance());
                mail.append("</td></tr>");
                mail.append("<tr><td>&nbsp;</td></tr>");
                mail.append("</table><br>");

                mail.append("<table border=1>");
                mail.append("<tr  class=\"heading\">");
                mail.append("<td>Tracking ID</td>");
                mail.append("<td>Amount</td>");
                mail.append("<td>Transaction ID</td>");
                mail.append("<td>Description</td>");
                mail.append("</tr>");

                if ((StringBuffer) mailHash.get(memberid) != null)
                    mail.append(((StringBuffer) mailHash.get(memberid)).toString());

                mail.append("</table><br><br>");
                mail.append("<font face=\"arial,verdana,helvetica\" size=\"2\" >Tracking ID is ID under <i>Transactions</i> menu<br>");
                mail.append("Transaction ID is ID under <i>Accounts</i> menu<br></font>");
                mail.append("</body></html>");

                debugger.append("Mail body starts<br>" + mail + "<br>Mail body ends");

                query = "select contact_emails from members where memberid= ?";
                p1 = conn.prepareStatement(query);
                p1.setString(1,memberid);

                debugger.append(query + "<br>");
                rs2 = p1.executeQuery();

                if (rs2.next())
                {
                    String contact_emails = rs2.getString(1);
                    logger.debug("calling SendMAil for Merchant- Confirm Batch " + contact_emails);
                    //Mail.sendHtmlMail(contact_emails, fromAddress, null, null, "CONFIRMATION OF TRANSFER OF FUNDS", mail.toString());
                    logger.debug("called SendMAil for Merchant-- Confirm Batch " + contact_emails);
                }


            }



            StringBuffer tcbody = new StringBuffer();
            tcbody.append("Total Transactions : " + totalCount + "\r\n");
            tcbody.append("Total Settled Transactions : " + settledCount + "\r\n");
            tcbody.append("Total UnSettled Transactions : " + notSettledCount + "\r\n");

            tcbody.append("\r\n");

            tcbody.append("Total Amount : " + totalAmount + "\r\n");
            tcbody.append("Total Settled Amount : " + settledAmount + "\r\n");
            tcbody.append("Total UnSettled Amount : " + unsettledAmount + "\r\n");


            int settledsize = settledHash.size();
            if (settledsize > 0)
            {
                tcbody.append("\r\nSettled Transactions: \r\n");
                tcbody.append("Date \t captureRRN \t AuthCode \t Amount \r\n");
                tcbody.append("---- \t -------- \t ---------- \t ------ \r\n");
            }
            for (int i = 1; i <= settledsize; i++)
            {
                tcbody.append((String) settledHash.get("" + i) + " \r\n");
            }


            int unsettledsize = unSettledHash.size();
            if (unsettledsize > 0)
            {
                tcbody.append("\r\nUnSettled Transactions: \r\n");
                tcbody.append("Date \t captureRRN \t AuthCode \t Amount \r\n");
                tcbody.append("---- \t -------- \t ---------- \t ------ \r\n");
            }
            for (int i = 1; i <= unsettledsize; i++)
            {
                tcbody.append((String) unSettledHash.get("" + i) + " \r\n");
            }


            out.println("<textarea cols=75 rows=50>" + tcbody + "</textarea><br>");

            logger.debug("calling SendMAil for Confirm Batch");
            Mail.sendNotificationMail("CONFIRMATION OF TRANSFER OF FUNDS", tcbody.toString());
            logger.debug("called SendMAil for -- Confirm Batch");

//sending mail to customer


            Enumeration custMailEnum = custMailVect.elements();
            while (custMailEnum.hasMoreElements())
            {
                Hashtable transData = (Hashtable) custMailEnum.nextElement();
                transData.put("SIGNATURE",Functions.generateSignature(toid+""));
                String customermailmsg = Functions.replaceTag((String) application.getAttribute("CUSTOMERNOTIFICATION"), transData);
                String emailaddr = (String) transData.get("EMAILADDR");
                String trackingid = (String) transData.get("TRACKINGID");
                if (! noMailCustEmailVect.contains(emailaddr))
                {
                    logger.debug("Sending mail to customer at " + emailaddr);
                    //Mail.sendHtmlMail(emailaddr, fromAddress, null, null, "Your Order at " + (String) transData.get("SITENAME"), customermailmsg);
                    logger.debug("Sent mail to customer");
                }
                else
                    logger.debug("Settlement notification mail is not sent to " + emailaddr + " for trackingID=" + trackingid + "as Merchant asked not to send mail.");
            }

        }
        catch (SystemError se)
        {

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            out.println(Functions.ShowMessage("stacktrace", sw.toString()));


            logger.error("SystemError :::: ",se);
            out.println(Functions.ShowMessage("Error", se.toString() + "<br><br>Query :" + query));
        }
        catch (Exception e)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            out.println(Functions.ShowMessage("stacktrace", sw.toString()));

            logger.error("Exception ::::::",e);
            out.println(Functions.ShowMessage("Error!", e.toString()));
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeResultSet(rs2);
            Database.closePreparedStatement(p1);
            Database.closePreparedStatement(pst);
            Database.closeConnection(conn);
            out.println("<br><br><br><br><br><br><br><br>");
            out.println("<b>Debug Info </b><br>");
            out.println(debugger.toString());
        }

    }


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        service(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        service(request, response);
    }//post ends

    private String getBatchName()
    {
        return "" + System.currentTimeMillis() / 1000;
    }
}
