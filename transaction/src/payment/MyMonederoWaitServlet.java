package payment;

import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.MyMonederoPaymentGateway;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.directi.pg.core.valueObjects.MyMonederoRequestVO;
import com.directi.pg.core.valueObjects.MyMonederoResponseVO;
import com.invoice.dao.InvoiceEntry;
import com.logicboxes.util.ApplicationProperties;
import com.payment.errors.TransactionError;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: Dhiresh
 * Date: 27/2/13
 * Time: 9:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyMonederoWaitServlet extends PzServlet
{
    private static Logger log=new Logger(MyMonederoWaitServlet.class.getName());
    private static TransactionLogger transactionLogger=new TransactionLogger(MyMonederoWaitServlet.class.getName());

    final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.MyMonederoServlet");
    final String PROXYHOST = RB.getString("PROXYHOST");
    final String PROXYPORT = RB.getString("PROXYPORT");
    final String PROXYSCHEME=RB.getString("PROXYSCHEME");
    final String WAIT=RB.getString("WAIT");

    public void doGet(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException
    {
        doService(request, res);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException
    {
        doService(request, res);
    }

    public void doService(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering doService of WaitServlet");
        transactionLogger.debug("Entering doService of WaitServlet");

        ServletContext sc=getServletContext();
        PrintWriter pWriter = res.getWriter();
        res.setContentType("text/html");
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");

        //HttpSession session = request.getSession();
        Hashtable error = new Hashtable();
        Hashtable otherdetails = new Hashtable();
        Hashtable paramHash = new Hashtable();

        TransactionUtility transactionUtility = new TransactionUtility();
        TransactionError transactionError = new TransactionError();
        ActionEntry entry = new ActionEntry();
        Transaction transaction = new Transaction();
        Merchants merchants = new Merchants();

        //RiskCheckers riskCheckers = new RiskCheckers();

        if (request.getParameter("TOID") == null)
        {
            res.sendRedirect("/merchant/index.jsp");
            return;
        }

        log.debug("CSRF token from request "+request.getParameter("ctoken"));
        transactionLogger.debug("CSRF token from request "+request.getParameter("ctoken"));

        String ctoken = transactionUtility.validateCtoken(request,pWriter);

        /*if(request.getSession()== null)
        {
            pWriter.print(Functions.ShowMessage("Message", "Your session is Expired."));
            //res.sendRedirect("http://www.<hostname>.com/");
            return;
        }
        if(session !=null)
        {
            ctoken =  (String)session.getAttribute("ctoken");

            log.debug("CSRF token from session"+ctoken);
        }

        if(ctoken!=null && !ctoken.equals("") && !request.getParameter("ctoken").equals(ctoken))
        {
            log.debug("CSRF token not match ");
            error.put("Invalid Request", "UnAuthorized member");
        }*/

        Enumeration enumeration = request.getParameterNames();

        while (enumeration.hasMoreElements())
        {
            String name = (String) enumeration.nextElement();
            String value = ESAPI.encoder().encodeForHTML( request.getParameter(name));
            if (value == null)
                value = "";

            paramHash.put(name, value);

            if (name.startsWith("TMPL_"))
                otherdetails.put(name, request.getParameter(name));
        }

        String partnerLogo ="";
        //String amount=null;
        String encoded_TRACKING_ID = ESAPI.encoder().encodeForHTML(request.getParameter("TRACKING_ID"));
        String encoded_DESCRIPTION = ESAPI.encoder().encodeForHTML(request.getParameter("DESCRIPTION"));
        String encoded_ORDER_DESCRIPTION = ESAPI.encoder().encodeForHTML(request.getParameter("ORDER_DESCRIPTION"));
        //validation for mandatory parameter
        error = validateMandatoryParameters(request);
        if(error.size()>0)
        {
            transactionError.displayErrors(error,otherdetails,res,request.getParameter("TOID"),pWriter);
            //sendErrors(error,otherdetails,res,request.getParameter("toid"),pWriter);
            return;
        }

        //validation for optional parameter
        error = validateOptionalParameters(request);
        if(error.size()>0)
        {
            transactionError.displayErrors(error,otherdetails,res,request.getParameter("TOID"),pWriter);
            //sendErrors(error,otherdetails,res,request.getParameter("toid"),pWriter);
            return;
        }

        String toid = request.getParameter("TOID");
        String checksumVal = request.getParameter("CHECKSUM");
        String trackingid = request.getParameter("TRACKING_ID");
        String description = request.getParameter("DESCRIPTION");
        String orderdescription= request.getParameter("ORDER_DESCRIPTION");
        String country = request.getParameter("TMPL_COUNTRYCODE");
        String email = request.getParameter("TMPL_emailaddr");
        String city = request.getParameter("TMPL_city");
        String street = request.getParameter("TMPL_street");
        String zip = request.getParameter("TMPL_zip");
        String state = request.getParameter("TMPL_state");
        String telno = request.getParameter("TMPL_telno");
        String telnocc = request.getParameter("TMPL_telnocc");

        if(error.size()>0)
        {
            transactionError.displayErrors(error,otherdetails,res,toid,encoded_TRACKING_ID,encoded_DESCRIPTION,encoded_ORDER_DESCRIPTION,pWriter);//sendErrors(error,otherdetails,res,request.getParameter("toid"),pWriter);
            return;
        }
        /*StringBuffer sbuf = new StringBuffer();
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
            sbuf.append("<a href=\"javascript:history.go(-1)\">back </></center>");
            otherdetails.put("MESSAGE", sbuf.toString());
            otherdetails.put("TRACKING_ID", encoded_TRACKING_ID);
            otherdetails.put("DESCRIPTION", encoded_DESCRIPTION);
            otherdetails.put("ORDER_DESCRIPTION", encoded_ORDER_DESCRIPTION);
            otherdetails.put("RETRYBUTTON", "<input type=\"button\" value=\"&nbsp;&nbsp;Retry&nbsp;&nbsp;\" onClick=\"javascript:history.go(-1)\">");
            try
            {
                pWriter.println(Template.getErrorPage(toid, otherdetails));
                pWriter.flush();

            }
            catch (SystemError se)
            {   log.error("Excpetion in WaitServlet",se);

            }
            return;
        }*/
        //Limit checker
        String version=null;
        String clkey = "";
        String checksumAlgo = null;
        String sql = "";
        String ipaddress = "N/A";
        //String boilname = "";
        ResultSet rs=null;
        int count=0;
        if(sc.getAttribute("noOfClients")!=null)
            sc.setAttribute("noOfClients", sc.getAttribute("noOfClients"));
        else
            sc.setAttribute("noOfClients", "0");
        Connection con=null;
        String amountInDb=null,accountId="",transactionDescription=null,transactionOrderDescription=null,dtstamp=null;
        String templateCurrency=null,templateAmount=null;
        int memberId=0;
        AuditTrailVO auditTrailVO=new AuditTrailVO();

        try
        {
            count = Integer.parseInt((String) sc.getAttribute("noOfClients"));
            count++;
            sc.setAttribute("noOfClients", String.valueOf(count));

            /*con=Database.getConnection();
            log.debug("Execute select query for gateway Account Service");
            sql = "select amount,toid,description,orderdescription,dtstamp,currency,amount,status,httpheader,accountid from transaction_common where trackingid=?";

            PreparedStatement p1=con.prepareStatement(sql);
            p1.setString(1,trackingid);
            rs = p1.executeQuery();*/

            Hashtable transactionDetails = transaction.getTransactionDetailsForCommon(trackingid);
            if(!transactionDetails.isEmpty()) /*(rs.next())*/
            {
                memberId = Integer.parseInt((String)transactionDetails.get("toid"));
                amountInDb = (String) transactionDetails.get("amount");
                accountId = (String) transactionDetails.get("accountid");
                transactionDescription = (String) transactionDetails.get("description");
                transactionOrderDescription = (String) transactionDetails.get("orderdescription");

                /*memberId = rs.getInt("toid");
                amountInDb = rs.getString("amount");
                accountId = rs.getString("accountid");
                transactionDescription = rs.getString("description");
                transactionOrderDescription = rs.getString("orderdescription");*/

                log.debug("::::::::  transactionOrderDescription"+transactionOrderDescription);
                transactionLogger.debug("::::::::  transactionOrderDescription"+transactionOrderDescription);
                if (transactionOrderDescription == null)
                    transactionOrderDescription = "";
                dtstamp = (String) transactionDetails.get("dtstamp");
                ipaddress = (String) transactionDetails.get("httpheader");
                templateCurrency = (String) transactionDetails.get("currency");
                templateAmount = (String) transactionDetails.get("amount");


                if (!((String)(transactionDetails.get("status"))).equals("begun"))
                {
                    log.debug("inside if for status!=begun");
                    transactionLogger.debug("inside if for status!=begun");
                    String table = "ERROR!!! Your Transaction is already being processed. This can occur if you clicked on the back button and tried to submit this Transaction again. The transaction may succeed or fail, however the status of the Transaction will have to be set manually. Please contact the Merchant to verify the status of the transaction with the following reference numbers and inform him of this message. PLEASE DO NOT TRY to execute this transaction once more from the beginning, or you may end up charging your card twice.<br><br> Please visit at "+ ApplicationProperties.getProperty("COMPANY_SUPPORT_URL")+" to know more about the reason for this error.";
                    transactionError.displayErrorPage(otherdetails,table,res,toid,encoded_TRACKING_ID,encoded_DESCRIPTION,encoded_ORDER_DESCRIPTION,pWriter);

                    /*String table = "ERROR!!! Your Transaction is already being processed. This can occur if you clicked on the back button and tried to submit this Transaction again. The transaction may succeed or fail, however the status of the Transaction will have to be set manually. Please contact the Merchant to verify the status of the transaction with the following reference numbers and inform him of this message. PLEASE DO NOT TRY to execute this transaction once more from the beginning, or you may end up charging your card twice.<br><br> Please visit at "+ ApplicationProperties.getProperty("COMPANY_SUPPORT_URL")+" to know more about the reason for this error.";
                    otherdetails.put("TRACKING_ID", encoded_TRACKING_ID);
                    otherdetails.put("DESCRIPTION", encoded_DESCRIPTION);
                    otherdetails.put("ORDER_DESCRIPTION", encoded_ORDER_DESCRIPTION);
                    otherdetails.put("MESSAGE", table);

                    Database.closeConnection(con);
                    pWriter.println(Template.getErrorPage(memberId + "", otherdetails));
                    pWriter.flush();*/
                    return;
                }
                else
                {
                    Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
                    StringBuffer sb = new StringBuffer("update transaction_common set");
                    sb.append("  street='" + ESAPI.encoder().encodeForSQL(me,street) + "'");
                    sb.append(" , country ='" + ESAPI.encoder().encodeForSQL(me,country) + "'");
                    sb.append(" , city ='" + ESAPI.encoder().encodeForSQL(me,city) + "'");
                    sb.append(" , state ='" + ESAPI.encoder().encodeForSQL(me,state) + "'");
                    sb.append(" , zip ='" + ESAPI.encoder().encodeForSQL(me,zip) + "'");
                    sb.append(", emailaddr ='"+ESAPI.encoder().encodeForSQL(me,email) + "'" );
                    sb.append(",telnocc='"+ ESAPI.encoder().encodeForSQL(me,telnocc)+"'" );
                    sb.append(" , telno ='" + ESAPI.encoder().encodeForSQL(me,telno) + "'");
                    sb.append(", status='authstarted'  where trackingid=" + ESAPI.encoder().encodeForSQL(me,trackingid));


                    //pWriter.println("Query : "+sql);
                    //Database.executeUpdate(sb.toString(), con);
                    transaction.executeUpdate(sb.toString());
                    //Transaction.updateBinDetails(trackingid,"",accountId,email,boilname);
                    con = Database.getConnection();
                    String sqlq="insert into bin_details(icicitransid,accountid,emailaddr) values (?,?,?)";
                    PreparedStatement ps=con.prepareStatement(sqlq);
                    ps.setString(1,trackingid);
                    ps.setString(2,accountId);
                    ps.setString(3,email);

                    auditTrailVO.setActionExecutorId(toid);
                    auditTrailVO.setActionExecutorName("Customer");
                    // Start : Added for Action and Status Entry in c table
                    log.debug("calling Action Entry start");
                    transactionLogger.debug("calling Action Entry start");
                    int actionEntry = entry.actionEntryForMyMonedero(trackingid,amountInDb.toString(),ActionEntry.ACTION_AUTHORISTION_STARTED,ActionEntry.STATUS_AUTHORISTION_STARTED,null,auditTrailVO);
                    entry.closeConnection();
                    log.debug("calling Action Entry end ");
                    transactionLogger.debug("calling Action Entry end ");
                    // End : Added for Action and Status Entry in Details table

                    String INVOICE_NO=request.getParameter("INVOICE_NO");
                    InvoiceEntry invoiceEntry=new InvoiceEntry();
                    if(INVOICE_NO!=null && !INVOICE_NO.equals(""))
                    {
                        invoiceEntry.processInvoice(INVOICE_NO,Integer.parseInt(trackingid),accountId);
                    }
                    //risk checking
                }
            }
            else
            {
                /*otherdetails.put("TRACKING_ID", encoded_TRACKING_ID);
                otherdetails.put("DESCRIPTION", encoded_DESCRIPTION);
                otherdetails.put("ORDER_DESCRIPTION", encoded_ORDER_DESCRIPTION);
                otherdetails.put("MESSAGE", message);*/

                String message = "ERROR!!! We have encountered an internal error while processing your request. Please visit at "+ApplicationProperties.getProperty("COMPANY_SUPPORT_URL")+" and create Support Request to know the status of this transaction.<BR><BR>";
                transactionError.displayErrorPage(otherdetails,message,res,toid,encoded_TRACKING_ID,encoded_DESCRIPTION,encoded_ORDER_DESCRIPTION,pWriter);

                /*try
                {
                    log.debug("Error !!, record not found for tracking id");
                    pWriter.println(Template.getErrorPage(toid, otherdetails));
                    pWriter.flush();
                }
                catch (Exception e)
                {   log.error("Excpetion in WaitServlet ",e);

                }
                Database.closeConnection(con);*/
                return;
            }
        }
        catch(Exception e)
        {
            log.error("ERROR OCCURED",e);
            transactionLogger.error("ERROR OCCURED",e);
        }
        /*finally
        {
            Database.closeConnection(con);
        }*/

        String isPoweredBy = merchants.isPoweredBy(toid);
        if(isPoweredBy.equalsIgnoreCase("Y"))
        {
            otherdetails.put("LOGO","<p align=\"left\"><a href=\"http://www.pz.com\"><IMG border=0 height=40 src=\"/icici/images/logo2.jpg\" width=105></a></p>");
        }
        else
        {
            otherdetails.put("LOGO","<p align=\"left\"></p>");
        }
        try
        {
            partnerLogo = merchants.getPartnerLogo(toid);
        }
        catch(Exception e)
        {
        }
        log.debug("partnerlogo---"+partnerLogo);
        transactionLogger.debug("partnerlogo---"+partnerLogo);
        if(partnerLogo!=null && !partnerLogo.equals(""))
        {
            otherdetails.put("PARTNERLOGO", "<img border=\"0\" height=40 width=105 src=\"/icici/images/"+partnerLogo+"\" >");
            log.debug("add partnerlogo");
            transactionLogger.debug("add partnerlogo");
        }
        otherdetails.put("TEMPLATE", request.getParameter("TEMPLATE"));
        /*String query2="select isPoweredBy from members where memberid= ?";
        try{
            Connection connection= Database.getConnection();
            PreparedStatement pstmnt = connection.prepareStatement(query2);
            pstmnt.setString(1,(String)toid);

            ResultSet x=pstmnt.executeQuery();
            x.next();
            isPoweredBy=x.getString("isPoweredBy");

        }
        catch(Exception e)
        {
            log.error("Exception Occured",e);
        }*/



        try
        {
            Hashtable transactionDetails = merchants.getMemberDetailsForTransaction(toid);
            String isService = "";

            if(!transactionDetails.isEmpty())
            {
                isService = (String) transactionDetails.get("isservice");
                clkey = (String) transactionDetails.get("clkey");
                checksumAlgo = (String) transactionDetails.get("checksumalgo");

                if (clkey == null || clkey.trim().equals(""))
                    throw new SystemError("Could not load Key");


                //log.debug("verify"+Checksum.verifyChecksumV2(memberId + "", transactionDescription, new BigDecimal(amountInDb).doubleValue() + "", clkey, checksumVal, checksumAlgo));
                //log.debug("verify+Checksum.verifyChecksumV2"+memberId + "=="+ transactionDescription+"======="+ new BigDecimal(amountInDb).doubleValue() + "========" + clkey+"=====" +checksumVal +"==============="+ checksumAlgo);
                //log.debug("verify+Checksum.verifyChecksumV2"+Checksum.generateChecksumV2(String.valueOf(memberId), transactionDescription, new BigDecimal(amountInDb).doubleValue() + "", clkey, checksumAlgo));
                if (!Checksum.verifyChecksumV2(memberId + "", transactionDescription,new BigDecimal(amountInDb).doubleValue()+"", clkey, checksumVal, checksumAlgo))
                {

                    String message = "ERROR!!! checksum mismatched. Please visit at "+ApplicationProperties.getProperty("COMPANY_SUPPORT_URL")+" and create Support Request to know the status of this transaction.<BR><BR>";
                    transactionError.displayErrorPage(otherdetails,message,res,toid,encoded_TRACKING_ID,encoded_DESCRIPTION,encoded_ORDER_DESCRIPTION,pWriter);

                    /*otherdetails.put("TRACKING_ID", encoded_TRACKING_ID);
                    otherdetails.put("DESCRIPTION", encoded_DESCRIPTION);
                    otherdetails.put("ORDER_DESCRIPTION", encoded_ORDER_DESCRIPTION);
                    otherdetails.put("MESSAGE", message);

                    try
                    {
                        pWriter.flush();
                        pWriter.println(Template.getErrorPage("" + memberId, otherdetails));
                        pWriter.flush();

                    }
                    catch (Exception e)
                    {
                        log.error("Exception  ",e);
                    }*/
                    return;

                }
            }

            /*con=Database.getConnection();
            String query=null;

            query = "select isservice,clkey,checksumalgo from members where memberid =? ";

            //pWriter.println("Query : "+query);
            PreparedStatement p7=con.prepareStatement(query);
            p7.setInt(1,memberId);
            rs = p7.executeQuery();*/
        }
        catch(Exception e)
        {
            log.error("ERROR OCCURED",e);
            transactionLogger.error("ERROR OCCURED",e);
        }

        log.debug("checkpoint 1");
        transactionLogger.debug("checkpoint 1");

        try
        {
            log.debug("Getting Wait Page");
            transactionLogger.debug("Getting Wait Page");
            otherdetails.put("CONTINUEBUTTON"," ");
            pWriter.println(Template.getWaitPage(memberId + "", otherdetails));
        }
        catch(SystemError e)
        {
            log.error("Error Occured",e);
            transactionLogger.error("Error Occured",e);

            String message = "";
            transactionError.displayErrorPage(otherdetails,message,res,toid,encoded_TRACKING_ID,encoded_DESCRIPTION,encoded_ORDER_DESCRIPTION,pWriter);

            /*otherdetails.put("TRACKING_ID", encoded_TRACKING_ID);
            otherdetails.put("DESCRIPTION", encoded_DESCRIPTION);
            otherdetails.put("ORDER_DESCRIPTION", encoded_ORDER_DESCRIPTION);
            otherdetails.put("MESSAGE", e.getMessage());
            otherdetails.put("RETRYBUTTON", "<input type=\"button\" value=\"&nbsp;&nbsp;Retry&nbsp;&nbsp;\" onClick=\"javascript:history.go(-1)\">");

            
            try
            {
                pWriter.println(Template.getErrorPage(memberId + "", otherdetails));
                pWriter.flush();
            }
            catch(Exception e1)
            {
                log.error("ERROR OCCURED",e1);
                
                
            }*/
            return;
        }
        res.flushBuffer();
        AbstractPaymentGateway pg=null;



        try
        {
            log.debug("Getting pg");
            transactionLogger.debug("Getting pg");
            pg =(MyMonederoPaymentGateway) AbstractPaymentGateway.getGateway(accountId);
        }
        catch (SystemError systemError)
        {
            log.error("System Error:::::::::",systemError);
            transactionLogger.error("System Error:::::::::",systemError);

            String message = "";
            transactionError.displayErrorPage(otherdetails,message,res,toid,encoded_TRACKING_ID,encoded_DESCRIPTION,encoded_ORDER_DESCRIPTION,pWriter);

            /*otherdetails.put("TRACKING_ID", encoded_TRACKING_ID);
            otherdetails.put("DESCRIPTION", encoded_DESCRIPTION);
            otherdetails.put("ORDER_DESCRIPTION", encoded_ORDER_DESCRIPTION);
            otherdetails.put("MESSAGE", systemError.getMessage());
            otherdetails.put("RETRYBUTTON", "<input type=\"button\" value=\"&nbsp;&nbsp;Retry&nbsp;&nbsp;\" onClick=\"javascript:history.go(-1)\">");

           try
           {
                pWriter.println(Template.getErrorPage(memberId + "", otherdetails));
                pWriter.flush();
           }
           catch(Exception e)
           {
               log.error("Error Occured",e);
           }*/
            return;
        }

        MyMonederoResponseVO ewalletResponseVO=new MyMonederoResponseVO();
        log.debug("Seting VO");
        transactionLogger.debug("Seting VO");
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        genericTransDetailsVO.setAmount(request.getParameter("TXN_AMT"));
        genericTransDetailsVO.setCurrency(request.getParameter("CURRENCY"));
        genericTransDetailsVO.setOrderId(request.getParameter("DESCRIPTION"));
        genericTransDetailsVO.setOrderDesc(request.getParameter("ORDERDESCRIPTION"));

        MyMonederoRequestVO ewalletRequestVO= new MyMonederoRequestVO(genericTransDetailsVO,toid);
        String checksum= Checksum.generateChecksumMyMonedero(trackingid, ctoken);

        ewalletRequestVO.setCtoken(ctoken);

        ewalletRequestVO.setUrl(PROXYSCHEME+ "://" + PROXYHOST + ":" +PROXYPORT+WAIT+"?ref="+trackingid+"&checksum="+checksum+"&ctoken="+ctoken+"");
        log.debug("URL IS -----"+ewalletRequestVO.getUrl());
        transactionLogger.debug("URL IS -----"+ewalletRequestVO.getUrl());

        try
        {
            ewalletResponseVO =(MyMonederoResponseVO) pg.processSale(trackingid, ewalletRequestVO);
            log.debug("Received  response");
            transactionLogger.debug("Received  response");
            //Print Response
        }
        catch(Exception e)
        {
            log.error("Exception while capturing the transaction ",e);
            transactionLogger.error("Exception while capturing the transaction ",e);

        }
        log.debug("Error ---"+ewalletResponseVO.getError());
        log.debug("Status---"+ewalletResponseVO.getStatus());
        log.debug("Tracking ID--"+ewalletResponseVO.getTrackingid());
        log.debug("WCTXNID--"+ewalletResponseVO.getWctxnid());
        log.debug("redirecturl--"+ewalletResponseVO.getRedirecturl());

        transactionLogger.debug("Error ---"+ewalletResponseVO.getError());
        transactionLogger.debug("Status---"+ewalletResponseVO.getStatus());
        transactionLogger.debug("Tracking ID--"+ewalletResponseVO.getTrackingid());
        transactionLogger.debug("WCTXNID--"+ewalletResponseVO.getWctxnid());
        transactionLogger.debug("redirecturl--"+ewalletResponseVO.getRedirecturl());

        String redirecturl=ewalletResponseVO.getRedirecturl();
        String wctxnid=ewalletResponseVO.getWctxnid();

        if(redirecturl ==null || redirecturl.equals("") || wctxnid ==null|| wctxnid.equals(""))
        {
            String message = "";
            transactionError.displayErrorPage(otherdetails,message,res,toid,encoded_TRACKING_ID,encoded_DESCRIPTION,encoded_ORDER_DESCRIPTION,pWriter);

            /*otherdetails.put("TRACKING_ID", encoded_TRACKING_ID);
            otherdetails.put("DESCRIPTION", encoded_DESCRIPTION);
            otherdetails.put("ORDER_DESCRIPTION", encoded_ORDER_DESCRIPTION);
            otherdetails.put("MESSAGE", "COULD NOT CONNECT TO THE PAYMENT GATEWAY, PLEASE TRY AGAIN");
            otherdetails.put("RETRYBUTTON", "<input type=\"button\" value=\"&nbsp;&nbsp;Retry&nbsp;&nbsp;\" onClick=\"javascript:history.go(-1)\">");

            try{


                pWriter.println(Template.getErrorPage(memberId + "", otherdetails));
                pWriter.flush();
            }
            catch(Exception e)
            {
                log.error("Error Occured",e);
            }*/
            return;
        }

        //
        sql="update transaction_common set paymentid=? where trackingid=?";
        try
        {
            con=Database.getConnection();
            PreparedStatement ps=con.prepareStatement(sql);
            ps.setString(1,ewalletResponseVO.getWctxnid());
            ps.setString(2,trackingid);
            ps.executeUpdate();
        }
        catch(Exception e)
        {
            log.error("EXCEPTION OCCURED",e);
            transactionLogger.error("EXCEPTION OCCURED",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        try
        {
            log.debug("calling Action Entry start");
            transactionLogger.debug("calling Action Entry start");
            int actionEntry = entry.actionEntryForMyMonedero(trackingid,amountInDb.toString(),ActionEntry.ACTION_REDIRECTION_STARTED,ActionEntry.STATUS_AUTHORISTION_STARTED,ewalletResponseVO,auditTrailVO);
            entry.closeConnection();
            log.debug("calling Action Entry end ");
            transactionLogger.debug("calling Action Entry end ");
        }
        catch(Exception e)
        {
            log.error("EXCEPTION OCCURED",e);
            transactionLogger.error("EXCEPTION OCCURED",e);
        }


        log.debug("ALMOST DONE ");
        transactionLogger.debug("ALMOST DONE ");

        log.debug("REDIRECTING---"+ewalletResponseVO.getRedirecturl());
        transactionLogger.debug("REDIRECTING---"+ewalletResponseVO.getRedirecturl());

//        request.getRequestDispatcher(ewalletResponseVO.getRedirecturl()).forward(request, res);

        //pWriter.println("<center> <b> Click<a href=\""+ ewalletResponseVO.getRedirecturl()+"\" > here </a> to continue paying your transaction</b></center>");




        /*pWriter.println("<form name=\"MyMonedero\" action=\""+ ewalletResponseVO.getRedirecturl()+"\" >");
        pWriter.println("</form>");
        pWriter.println("<script language=\"javascript\">");
        pWriter.println("document.MyMonedero.submit();");
        pWriter.println("</script>");
        pWriter.println("</body>");
        pWriter.println("</html>");
        */

        pWriter.println("<script language=\"javascript\">");
        pWriter.println("location.href=\""+ewalletResponseVO.getRedirecturl()+"\"");
        pWriter.println("</script>");
        pWriter.flush();
    }

    private Hashtable validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        Hashtable error = new Hashtable();
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.TMPL_COUNTRYCODE);
        inputFieldsListOptional.add(InputFields.TMPL_EMAILADDR);
        inputFieldsListOptional.add(InputFields.TMPL_CITY);
        inputFieldsListOptional.add(InputFields.TMPL_STREET);
        inputFieldsListOptional.add(InputFields.TMPL_ZIP);
        inputFieldsListOptional.add(InputFields.TMPL_STATE);
        inputFieldsListOptional.add(InputFields.TMPL_TELNO);
        inputFieldsListOptional.add(InputFields.TMPL_TELNOCC);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional, errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug("Invalid "+errorList.getError(inputFields.toString()).getLogMessage());
                    transactionLogger.debug("Invalid "+errorList.getError(inputFields.toString()).getLogMessage());
                    error.put(inputFields.toString(),"Invalid "+errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }
    private Hashtable validateMandatoryParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        Hashtable error = new Hashtable();
        List<InputFields>  inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID_CAPS);
        inputFieldsListMandatory.add(InputFields.TRACKINGID_CAPS);
        inputFieldsListMandatory.add(InputFields.DESCRIPTION_CAPS);
        inputFieldsListMandatory.add(InputFields.ORDER_DESC);
        inputFieldsListMandatory.add(InputFields.CHECKSUM_CAPS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory, errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListMandatory)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError("Invalid "+inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError("Invalid "+inputFields.toString()).getLogMessage());
                    error.put(inputFields.toString(),"Invalid "+errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;

    }

}
