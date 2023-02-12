package com.directi.pg;

import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.EcorePaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.util.Util;
import com.manager.PaymentManager;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.TransactionVO;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailService;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.validators.vo.DirectInquiryValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.Date;

//import PaymentClient.*;

public class Transaction
{
    private static final ResourceBundle RB2 = LoadProperties.getProperty("com.directi.pg.Servlet");
    private static final ResourceBundle RBTemplate = LoadProperties.getProperty("com.directi.pg.template");
    //static Category cat = Category.getInstance(Transaction.class.getName());
    static Logger logger = new Logger(Transaction.class.getName());
    static TransactionLogger transactionLogger= new TransactionLogger(Transaction.class.getName());
    private static String chargebackMailTemplate;
    private static String retrivalRequestMailTemplate;
    private static String retrivalRequestChargebackMailTemplate;
    private static String forgotPasswordMailTemplate;
    private static String signUpMailTemplate;
    private static String signUpMailToSupportTemplate;
    private static String settlementcronmail;
    private static String reconcilationmail;
    private static Hashtable chargeBackCodeMap= new Hashtable();
    private static Hashtable authCodeMap= new Hashtable();
    private static String fraudTransactionReverseTemplate;
    //added for the sending mail to merchant for reversal tarnsaction
    private static String transactionReverseTemplate;
    private static String transactionFailTemplate;
    static
    {
        try
        {
            loadMailTemplates();
            loadChargBackCodes();
            loadAuthorisationCodes();
        }
        catch (Exception e)
        {
            logger.error("Error while loading mail templates ",e);
            try
            {
                Mail.sendAdminMail("Unable to load mail templates for pz", "Error while loading mail templates " + Functions.getStackTrace(e));
            }
            catch (SystemError systemError)
            {
                // ignore error
            }
        }
    }
    boolean isLogEnabled = Boolean.parseBoolean(ApplicationProperties.getProperty("IS_LOG_ENABLED"));

    private static void sendRetrivalRequestMail(ResultSet rs, String icicitransid, String date) throws SQLException, SystemError
    {
        String contact_emails = rs.getString("contact_emails");
        String merchantid = rs.getString("memberid");
        String company_name = rs.getString("company_name");
        String orderid = rs.getString("description");
        String orderDescription = rs.getString("orderdescription");

        String accountId = rs.getString("transactionaccountid");
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String currency = account.getCurrency();
        String address = account.getAddress();

        String amount = rs.getString("amount");
        String captureAmount = rs.getString("captureamount");
        String refundAmount = rs.getString("refundamount");
        String chargebackAmount = rs.getString("disputedAmount");
        String cardHolder = rs.getString("name");
        String authDate = rs.getString("authdate");
        String captureDate = rs.getString("capdate");
        String emailAddr = rs.getString("emailaddr");
        String IPAddr = rs.getString("ipaddress");
        //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");

        Hashtable contents = new Hashtable();
        contents.put("DATE", date);
        contents.put("MERCHANTID", merchantid);
        contents.put("COMPANY_NAME", company_name);
        contents.put("ORDERID", orderid);
        contents.put("ORDER_DESCRIPTION", orderDescription);
        contents.put("CURRENCY", currency);
        contents.put("AMOUNT", amount);
        contents.put("CURRENCY", currency);
        contents.put("ADDRESS", address);
        if (captureAmount != null)
            contents.put("CAPTURE_AMOUNT", captureAmount);
        if (refundAmount != null)
            contents.put("REFUND_AMOUNT", refundAmount);
        if (chargebackAmount != null)
            contents.put("CHARGEBACK_AMOUNT", chargebackAmount);
        contents.put("CARD_HOLDER", cardHolder);
        contents.put("TRACKING_ID", icicitransid);
        contents.put("AUTH_DATE", authDate);
        contents.put("CAPTURE_DATE", captureDate);
        contents.put("EMAIL_ADDR", emailAddr);
        contents.put("IP_ADDRESS", IPAddr);

        logger.debug("Sending RetrivalRequest Mail");
        String retrivalRequestMail = Functions.replaceTag(retrivalRequestMailTemplate, contents);
        //Mail.sendHtmlMail(contact_emails, fromAddress, null, null, "Retrieval Request received for Tracking Id: " + icicitransid, retrivalRequestMail);
    }

    public static String isRetrivalRequestAlreadySent(String icicitransid) throws SystemError, SQLException
    {
        Connection dbconn = null;
        try
        {

            ResultSet rsGetDetails;
            dbconn = Database.getConnection();
            String query = "select status from  retrival_receipt_history where icicitransid =? " ;
            PreparedStatement pstmt=dbconn.prepareStatement(query);
            pstmt.setString(1,icicitransid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                return rs.getString("status");
            }
            else
            {
                return null;
            }
        }
        finally
        {
            Database.closeConnection(dbconn);
        }
    }

   /* public static void main(String[] args)
    {
        System.out.println("Entered in main");


    }*/

    private static void loadMailTemplates() throws Exception
    {
        String templatesFilePath = RBTemplate.getString("PATH");
        File chargebackFile = new File(templatesFilePath + "/mailtemplates/ChargebackMail.template");
        File retrivalRequestFile = new File(templatesFilePath + "/mailtemplates/RetrivalRequestMail.template");
        File retrivalRequestChargebackFile = new File(templatesFilePath + "/mailtemplates/RetrivalRequestChargebackMail.template");
        File forgotPasswordfile = new File(templatesFilePath + "/mailtemplates/ForgotPasswordMail.template");
        File signUpfile = new File(templatesFilePath + "/mailtemplates/SignUpMail.template");
        File signUpSupportfile = new File(templatesFilePath + "/mailtemplates/SignUpMailToSupport.template");
        File fraudTransactionReversefile = new File(templatesFilePath + "/mailtemplates/FraudTransactionReversalMail.template");
        File transactionReversefile = new File(templatesFilePath + "/mailtemplates/transactionReversalMail.template");
        File settlementcron = new File(templatesFilePath + "/mailtemplates/settlementcronmail.template");
        File reconcilationmailfile = new File(templatesFilePath + "/mailtemplates/reconcilationmail.template");
        File transactionfailfile = new File(templatesFilePath + "/mailtemplates/TransactionReportMail.template");
        chargebackMailTemplate = getMailContents(chargebackFile);
        retrivalRequestMailTemplate = getMailContents(retrivalRequestFile);
        retrivalRequestChargebackMailTemplate = getMailContents(retrivalRequestChargebackFile);
        forgotPasswordMailTemplate = getMailContents(forgotPasswordfile);
        signUpMailTemplate = getMailContents(signUpfile);
        signUpMailToSupportTemplate = getMailContents(signUpSupportfile);
        fraudTransactionReverseTemplate =  getMailContents(fraudTransactionReversefile);
        transactionReverseTemplate =  getMailContents(transactionReversefile);
        settlementcronmail= getMailContents(settlementcron);
        reconcilationmail = getMailContents(reconcilationmailfile);
        transactionFailTemplate =  getMailContents(transactionfailfile);
    }

    private static void loadChargBackCodes()
    {
        Connection dbconn = null;
        try{
            dbconn = Database.getConnection();
            Statement stmnt = dbconn.createStatement();
            String query = "select code,reason from cb_codes" ;

            ResultSet rs = stmnt.executeQuery(query);
            chargeBackCodeMap = Functions.getDetailedHashFromResultSet(rs);
        }
        catch (Exception e)
        {
            logger.error("Error while loading chrgeback codes ",e);
        }
        finally
        {
            Database.closeConnection(dbconn);
        }
    }

    private static void loadAuthorisationCodes()
    {
        Connection dbconn = null;
        try{
            dbconn = Database.getConnection();
            Statement stmnt = dbconn.createStatement();
            String query = "select code,reason from rs_codes" ;

            ResultSet rs = stmnt.executeQuery(query);
            authCodeMap = Functions.getDetailedHashFromResultSet(rs);
        }
        catch (Exception e)
        {
            logger.error("Error while loading authorisation codes ",e);
        }
        finally
        {
            Database.closeConnection(dbconn);
        }
    }

    private static String getMailContents(File templateFile)
            throws IOException
    {
        FileReader fr = new FileReader(templateFile);
        BufferedReader br = new BufferedReader(fr);
        StringBuffer mailContents = new StringBuffer();
        String line = null;
        while ((line = br.readLine()) != null)
        {
            mailContents.append(line);
        }
        return mailContents.toString();
    }

    public String processCaptureAndTransaction(Connection con,
                                               String icicimerchantid, String trackingId, String authId,
                                               String authCode, String authRRN, String captureAmount, String memberId,
                                               String description, String accountId)
            throws SystemError
    {
        String message = "";
        try
        {
            //Statement stmt = con.createStatement();
            logger.debug("Entering processCaptureAndTransaction");

            String capturedata = "Transid :" + trackingId + ", authid : " + authId + ", amount : " + captureAmount + ", memberid : " + memberId + ", description : " + description;



            String query = "update transaction_icicicredit set status = 'capturestarted',capturedata= ? where  status='authsuccessful' and icicitransid = ?";

            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1,capturedata);
            pstmt.setString(2,trackingId);
            int result = pstmt.executeUpdate();
            //Database.commit(con);



            logger.debug("result=" + result);
            if (result == 1)
            {

                // Start : Added for Action and Status Entry in Action History table
                Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
                ActionEntry entry = new ActionEntry();
                int actionEntry = entry.actionEntry(trackingId,captureAmount,ActionEntry.ACTION_CAPTURE_STARTED,ActionEntry.STATUS_CAPTURE_STARTED);
                entry.closeConnection();

                // End : Added for Action and Status Entry in Action History table


                Hashtable hash = null;

                //Process Capture
                if (!(icicimerchantid.equals(RB2.getString("MID1")) || icicimerchantid.equals(RB2.getString(RB2.getString("MID1") + "_MID2"))))
                {
                    AbstractPaymentGateway pg = AbstractPaymentGateway.getGateway(accountId);
                    hash = pg.processCapture(trackingId, captureAmount, authId, authCode, authRRN);
                }
                else
                {
                    AbstractPaymentGateway pg = AbstractPaymentGateway.getGateway(accountId);
                    hash = pg.processCapture(trackingId, captureAmount, authId, authCode, authRRN);
                }
                // Updating transaction_icicicredit

                StringBuffer sb = new StringBuffer();
                sb.append("update transaction_icicicredit set");

                if (hash != null && hash.get("captureqsiresponsecode").equals("0"))
                {
                    logger.debug("Capture Successsful:");
                    sb.append(" status = 'capturesuccess' ");
                    message = (String) hash.get("captureqsiresponsedesc");

                }
                else
                {
                    logger.debug("Capture Failed:");
                    sb.append(" status = 'capturestarted',captureresult='Capture Failed' ");
                    message = "There was an error while executing the capture";

                }

                Enumeration e = hash.keys();
                while (e.hasMoreElements())
                {
                    String key = (String) e.nextElement();
                    sb.append(" , " + key + " = '" + hash.get(key) + "' ");
                }

                sb.append(" where icicitransid = " + ESAPI.encoder().encodeForSQL(me, trackingId));

                result = Database.executeUpdate(sb.toString(),con);

                //Database.commit(con);

                if (hash != null && hash.get("captureqsiresponsecode").equals("0"))
                {
                    logger.debug("Capture Successsful:");

                    // Start : Added for Action and Status Entry in Action History table

                    entry = new ActionEntry();
                    actionEntry = entry.actionEntry(trackingId,captureAmount,ActionEntry.ACTION_CAPTURE_SUCCESSFUL,ActionEntry.STATUS_CAPTURE_SUCCESSFUL);
                    entry.closeConnection();

                    // End : Added for Action and Status Entry in Action History table

                }
            }
            else
                logger.debug("Couldn't retrieve result as status is not authsuccessful");

        }
        catch (SQLException e)
        {
            logger.error("Leaving Transaction throwing SQL Exception as System Error : ",e);
            throw new SystemError("Error : " + e.getMessage());
        }
        catch (NullPointerException e)
        {
            logger.error("Leaving Transaction catching NullPointerException : " ,e);
            message = "There was an error while executing the capture";
        }
        //End of inserts.
        logger.debug("Leaving processCaptureAndTransaction");
        return message;

    }

    public String processCaptureForPayVT(Connection con,
                                         String icicimerchantid, String trackingId, String authId,
                                         String authCode, String authRRN, String captureAmount, String memberId,
                                         String description, String accountId)
    {
        String message = "";
        try
        {
            Statement stmt = con.createStatement();
            logger.debug("Entering processCaptureForPayVT");

            String capturedata = "Transid :" + trackingId + ", authid : " + authId + ", amount : " + captureAmount + ", memberid : " + memberId + ", description : " + description;



            String query = "update transaction_icicicredit set status = 'capturestarted',capturedata= ? where  status='authsuccessful' and icicitransid = ?";

            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1,capturedata);
            pstmt.setString(2,trackingId);
            int result = pstmt.executeUpdate();
            Database.commit(con);



            logger.debug("result=" + result);
            if (result == 1)
            {

                // Start : Added for Action and Status Entry in Action History table

                ActionEntry entry = new ActionEntry();
                int actionEntry = entry.actionEntryForPayVT(trackingId,captureAmount,ActionEntry.ACTION_CAPTURE_STARTED,ActionEntry.STATUS_CAPTURE_STARTED,null,null,null,null);
                entry.closeConnection();

                // End : Added for Action and Status Entry in Action History table



                GenericCardDetailsVO cardDetail= new GenericCardDetailsVO();
                GenericAddressDetailsVO AddressDetail= new GenericAddressDetailsVO();
                GenericTransDetailsVO TransDetail = new GenericTransDetailsVO(null,null,null,null,null);



                TransDetail.setAmount(captureAmount);
                TransDetail.setCurrency(GatewayAccountService.getGatewayAccount(accountId).getCurrency());
                TransDetail.setOrderId(trackingId);
                TransDetail.setOrderDesc(description);

                PayVTRequestVO requestVO = new PayVTRequestVO(cardDetail,AddressDetail, TransDetail);
                //Process Capture
                PayVTResponseVO  responseVO=null;
                AbstractPaymentGateway pg = AbstractPaymentGateway.getGateway(accountId);
                responseVO = (PayVTResponseVO)pg.processCapture(trackingId, requestVO);

                // Updating transaction_icicicredit

                StringBuffer sb = new StringBuffer();
                sb.append("update transaction_payvt set");

                if (responseVO != null && responseVO.getResponsecode().equals("0"))
                {
                    logger.debug("Capture Successsful:");
                    sb.append(" status = 'capturesuccess' ");
                    message = responseVO.getMessage();

                }
                else
                {
                    logger.debug("Capture Failed:");
                    sb.append(" status = 'capturestarted',captureresult='Capture Failed' ");
                    message = "There was an error while executing the capture";

                }

                sb.append(" where trackingid = " + trackingId);

                result = stmt.executeUpdate(sb.toString());

                Database.commit(con);

                if (responseVO != null && responseVO.getResponsecode().equals("0"))
                {
                    logger.debug("Capture Successsful:");

                    // Start : Added for Action and Status Entry in Action History table

                    entry = new ActionEntry();
                    actionEntry = entry.actionEntryForPayVT(trackingId,captureAmount,ActionEntry.ACTION_CAPTURE_SUCCESSFUL,ActionEntry.STATUS_CAPTURE_SUCCESSFUL,responseVO.getTransId(),responseVO.getResponsecode(),responseVO.getResult(),responseVO.getMessage());
                    entry.closeConnection();

                    // End : Added for Action and Status Entry in Action History table

                }
            }
            else
                logger.debug("Couldn't retrieve result as status is not authsuccessful");

        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("Leaving Transaction catching PZTechnicalException : " ,e);
            message = "There was an error while executing the capture";
            PZExceptionHandler.handleTechicalCVEException(e,"trackingId:::::"+trackingId,"Technical Voilation exception while capturing transaction via PayVt");
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("Leaving Transaction catching PZConstraintViolation Exception: " ,e);
            message = "There was an error while executing the capture";
            PZExceptionHandler.handleCVEException(e,"trackingId:::::"+trackingId,"Constraint Voilation exception while capturing transaction via PayVt");
        }
        catch (PZDBViolationException e)
        {
            logger.error("Leaving Transaction catching PZDBViolationException : " ,e);
            message = "There was an error while executing the capture";
            PZExceptionHandler.handleDBCVEException(e,"trackingId:::::"+trackingId,"DB Voilation exception while capturing transaction via PayVt");
        }
        catch (SQLException e)
        {
            logger.error("Leaving Transaction throwing SQL Exception as System Error : ",e);
            message = "There was an error while executing the capture";
            PZExceptionHandler.raiseAndHandleDBViolationException(Transaction.class.getName(),"processCaptureForPayVT()",null,"common","DB Exception",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"Capture Process for PayVt");
        }
        catch (NullPointerException e)
        {
            logger.error("Leaving Transaction catching NullPointerException : " ,e);
            message = "There was an error while executing the capture";
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(Transaction.class.getName(),"processCaptureForPayVT()",null,"common","DB Exception",PZTechnicalExceptionEnum.NULL_POINTER_EXCEPTION ,null,e.getMessage(),e.getCause(),null,"Capture Process for PayVt");
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving Transaction throwing System Error Exception as System Error : ",systemError);
            message = "There was an error while executing the capture";
            PZExceptionHandler.raiseAndHandleDBViolationException(Transaction.class.getName(),"processCaptureForPayVT()",null,"common","DB Exception",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),null,"Capture Process for PayVt");
        }
        //End of inserts.
        logger.debug("Leaving processCaptureAndTransaction");
        return message;

    }

    public String processCaptureForEcore(Connection con, String trackingId,
                                         String accountId, String fromId, String authCode, String transId, String captureAmount)

    {
        String message = "";
        try
        {
            Statement stmt = con.createStatement();
            logger.debug("Entering processCaptureForEcore");
            AuditTrailVO auditTrailVO=new AuditTrailVO();
            auditTrailVO.setActionExecutorId("0");
            auditTrailVO.setActionExecutorName("Admin");

            String query = "update transaction_ecore set status = 'capturestarted' where  status='authsuccessful' and trackingid = ?";

            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1,trackingId);
            int result = pstmt.executeUpdate();
            //Database.commit(con);

            logger.debug("result=" + result);
            if (result == 1)
            {

                // Start : Added for Action and Status Entry in Action History table

                ActionEntry entry = new ActionEntry();
                int actionEntry = entry.actionEntryForEcore(trackingId,captureAmount,ActionEntry.ACTION_CAPTURE_STARTED,ActionEntry.STATUS_CAPTURE_STARTED,null,null,auditTrailVO);
                entry.closeConnection();

                // End : Added for Action and Status Entry in Action History table


                EcorePaymentGateway pg =(EcorePaymentGateway) AbstractPaymentGateway.getGateway(accountId);
                EcoreResponseVO response = (EcoreResponseVO)pg.processCapture( fromId, authCode,transId) ;

                StringBuffer sb = new StringBuffer();
                sb.append("update transaction_ecore set");
                if (response != null && response.getResponseCode().equals("100"))
                {
                    logger.debug("Capture Successsful:");
                    sb.append(" status = 'capturesuccess' ");
                    sb.append(", ecorePaymentOrderNumber = '"+response.getTransactionID()+"'");
                    message = response.getStatusDescription();

                }
                else
                {
                    logger.debug("Capture Failed:");
                    sb.append(" status = 'capturestarted' ");
                    sb.append(", ecorePaymentOrderNumber = '"+response.getTransactionID()+"'");
                    message = "Capture Failed from Bank";

                }

                sb.append(" where trackingid = " + trackingId);

                result = stmt.executeUpdate(sb.toString());

                //Database.commit(con);

                if (response != null && response.getResponseCode().equals("100"))
                {
                    logger.debug("Capture Successsful:");

                    // Start : Added for Action and Status Entry in Action History table

                    entry = new ActionEntry();
                    actionEntry = entry.actionEntryForEcore(trackingId,captureAmount,ActionEntry.ACTION_CAPTURE_SUCCESSFUL,ActionEntry.STATUS_CAPTURE_SUCCESSFUL,null,response,auditTrailVO);
                    entry.closeConnection();

                    // End : Added for Action and Status Entry in Action History table

                }
            }
            else
                logger.debug("Couldn't retrieve result as status is not authsuccessful");

        }


        catch (PZTechnicalViolationException e)
        {
            logger.error("Leaving Transaction catching PZTechnicalException : " ,e);
            message = "There was an error while executing the capture";
            PZExceptionHandler.handleTechicalCVEException(e,"trackingId:::::"+trackingId,"Technical Voilation exception while capturing transaction via Ecore");
        }
        catch (PZDBViolationException e)
        {
            logger.error("Leaving Transaction catching PZDBViolationException : " ,e);
            message = "There was an error while executing the capture";
            PZExceptionHandler.handleDBCVEException(e,"trackingId:::::"+trackingId,"DB Voilation exception while capturing transaction via Ecore");
        }
        catch (NullPointerException e)
        {
            logger.error("Leaving Transaction catching NullPointerException : " ,e);
            message = "There was an error while executing the capture";
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(Transaction.class.getName(),"processCaptureForEcore()",null,"common","DB Exception",PZTechnicalExceptionEnum.NULL_POINTER_EXCEPTION ,null,e.getMessage(),e.getCause(),null,"Capture Process for Ecore");
        }
        catch (SQLException e)
        {
            logger.error("Leaving Transaction throwing SQL Exception as System Error : ",e);
            message = "There was an error while executing the capture";
            PZExceptionHandler.raiseAndHandleDBViolationException(Transaction.class.getName(),"processCaptureForEcore()",null,"common","DB Exception",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"Capture Process for Ecore");
        }
        catch (SystemError systemError)
        {
            logger.error("Leaving Transaction throwing System Error Exception as System Error : ",systemError);
            message = "There was an error while executing the capture";
            PZExceptionHandler.raiseAndHandleDBViolationException(Transaction.class.getName(),"processCaptureForEcore()",null,"common","DB Exception",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),null,"Capture Process for Ecore");
        }
        //End of inserts.
        logger.debug("Leaving processCaptureForEcore");
        return message;

    }

    public String getTrackingID(String authID, String captureID)
    {
        String trackingID = null;
        try
        {
            trackingID = null;
            Connection conn = null;
            ResultSet rs;
            try
            {
                conn = Database.getConnection();
                String s="select icicitransid from transaction_icicicredit where captureid = ? and authid = ?";
                PreparedStatement p=conn.prepareStatement(s);
                p.setString(1,captureID);
                p.setString(2,authID);
                rs = p.executeQuery();
                if (rs.next())
                    trackingID = rs.getString("icicitransid");
            }
            catch (SystemError systemError)
            {
                logger.error("Error",systemError);

            }
            catch (SQLException e)
            {
                logger.error("Error" ,e);
            }
            finally
            {
                if (conn != null)
                    conn.close();
            }
        }
        catch (SQLException e)
        {

        }
        return trackingID;
    }

    public Hashtable getTerminalDetails(int terminalid)
    {
        Hashtable terminaldetails = new Hashtable();
        Connection con = null;
        try
        {
            con=Database.getConnection();
            transactionLogger.debug("Transaction.getTerminalDetails from member_account_details ::: DB Call");
            String query2 = "select accountid,memberid,paymodeid,cardtypeid,isActive from member_account_mapping where terminalid =?";
            PreparedStatement pstmt2 = con.prepareStatement(query2);
            pstmt2.setInt(1, terminalid);
            ResultSet rs2 = pstmt2.executeQuery();
            if (rs2.next())
            {
                terminaldetails.put("cardtypeid",rs2.getInt("cardtypeid")) ;
                terminaldetails.put("paymodeid",rs2.getInt("paymodeid")) ;
                terminaldetails.put("memberid",rs2.getString("memberid")) ;
                terminaldetails.put("accountid",rs2.getString("accountid")) ;
                terminaldetails.put("isActive",rs2.getString("isActive")) ;

            }
        }
        catch (Exception e)
        {
            logger.error("Exception occur", e);
        }
        finally
        {
            Database.closeConnection(con);
        }

        return terminaldetails;
    }

    public int getTerminalId(String accountid, String toid, int paymenttype, int cardtype)
    {
        int terminalid = -1;
        Connection con = null;
        try
        {
            con=Database.getConnection();
            transactionLogger.debug("Transaction.getTerminalId getting terminal id from member_account_mapping ::: DB Call");
            String query2 = "select terminalid from member_account_mapping where accountid =? and memberid=? and paymodeid=? and cardtypeid=? and isActive='Y'";
            PreparedStatement pstmt2 = con.prepareStatement(query2);
            pstmt2.setString(1, accountid);
            pstmt2.setString(2, toid);
            pstmt2.setInt(3, paymenttype);
            pstmt2.setInt(4, cardtype);
            ResultSet rs2 = pstmt2.executeQuery();
            if (rs2.next())
            {

                terminalid = rs2.getInt("terminalid");
            }
        }
        catch (Exception e)
        {
            logger.error("Exception occur", e);
        }
        finally
        {
            Database.closeConnection(con);
        }

        return terminalid;
    }

    public String getAccountId(String toid, int paymenttype, int cardtype)
    {
        String accountId="0";
        Connection con = null;
        try
        {
            con=Database.getConnection();
            transactionLogger.debug("Transaction.getAccountId from toid,payment,cardtype ::: DB Call");
            String query2 = "select accountid from member_account_mapping where memberid=? and paymodeid=? and cardtypeid=? and isActive='Y'";
            PreparedStatement pstmt2 = con.prepareStatement(query2);
            pstmt2.setString(1, toid);
            pstmt2.setInt(2, paymenttype);
            pstmt2.setInt(3, cardtype);
            ResultSet rs2 = pstmt2.executeQuery();
            if (rs2.next())
            {

                accountId = rs2.getString("accountid");
            }
        }
        catch (Exception e)
        {
            logger.error("Exception occur", e);
        }
        finally
        {
            Database.closeConnection(con);
        }

        return accountId;
    }

    public String getAccountID(String trackingId)
    {
        String accountID = null;
        accountID = null;
        Connection conn = null;
        ResultSet rs;
        try
        {
            conn = Database.getConnection();
            String s="select accountid from bin_details where icicitransid = ?";
            PreparedStatement p=conn.prepareStatement(s);
            p.setString(1,trackingId);
            rs = p.executeQuery();
            while(rs.next())
                accountID = rs.getString("accountid");
            logger.debug("accountid----"+p);
            logger.debug("accountid----"+accountID);
        }
        catch (SystemError systemError)
        {
            logger.error("Error",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Error" ,e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return accountID;
    }

    public String searchAccountIdFromALLTables(String trackingId)
    {
        String accountID = null;
        Set<String> gatewaySet = new HashSet<String>();
        gatewaySet.addAll(GatewayTypeService.getGateways());
        Iterator i = gatewaySet.iterator();
        try
        {
            accountID = null;
            Connection conn = null;
            String tablename = "";
            ResultSet rs;
            while(i.hasNext() && accountID==null)
            {
                tablename = Database.getTableName((String)i.next());

                try
                {

                    conn = Database.getConnection();
                    String s="";
                    if(tablename.equals("transaction_icicicredit"))
                        s="select accountid from "+tablename+" where icicitransid = ?";
                    else
                        s="select accountid from "+tablename+" where trackingid = ?";

                    PreparedStatement p=conn.prepareStatement(s);
                    p.setString(1,trackingId);
                    rs = p.executeQuery();
                    if (rs.next())
                    {
                        accountID = rs.getString("accountid");

                    }
                }
                catch (SystemError systemError)
                {
                    logger.error("Error",systemError);

                }
                catch (SQLException e)
                {
                    logger.error("Error" ,e);
                }
                finally
                {
                    if (conn != null)
                        conn.close();
                }


            }


        }
        catch (SQLException e)
        {

        }
        return accountID;
    }

    public void processChargeback(String icicitransid, String date, String cbrefnumber, String cbamount, String cbreason, String partial) throws SQLException, SystemError
    {
        StringBuffer merchantsubject = new StringBuffer();
        Connection conn = null;
        String query = null;
        String captureAmount = "";
        String refundAmount = "";
        String amount = "";
        String orderDescription = "";
        String cardHolder = "";
        String emailAddr = "";
        String IPAddr = "";
        String hrcode = "";
        java.util.Date dt = null;
        String transid = null;
        String orderid = null;
        String memberId = null;
        String description = null;
        String authDate = null;
        String captureDate = null;

        String company_name = null;
        String contact_emails = null;
        //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");


        transid = null;
        description = null;

        try
        {
            conn = Database.getConnection();
            dt = new java.util.Date();

            if (Functions.checkStringNull(cbrefnumber) == null)
            {
                throw new SystemError("Please put value for CB REF NUMBER.");
            }

            query = "select T.*,T.captureamount-T.refundamount as cbamount,date_format(from_unixtime(T.dtstamp),'%d/%m/%Y') as authdate,date_format(from_unixtime(T.podbatch),'%d/%m/%Y') as capdate,M.company_name,M.contact_emails,M.chargebackcharge,M.taxper,M.reversalcharge from transaction_icicicredit as T,members as M where icicitransid=? and T.toid=M.memberid and ( T.status='settled' or  T.status='reversed')";
            PreparedStatement p=conn.prepareStatement(query);
            p.setString(1,icicitransid);
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                    /*try
                    {

                        if ((Double.parseDouble(cbamount) != rs.getDouble("cbamount") && partial == null) || Functions.checkStringNull(cbamount) == null || Double.parseDouble(cbamount) > rs.getDouble("cbamount"))
                        {
                            throw new SystemError("Please put valid Chargeback amount. <BR> Max chargeback Amount : " + rs.getDouble("cbamount") + " <BR> Amount Entered : " + cbamount);
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        throw new SystemError("Please put valid Chargeback amount.");
                    }
                    */
                transid = rs.getString("transid");
                orderid = rs.getString("description");

                //Code Added for the Chargeback Reason Description - Start
                int intCBCode=0;
                if(cbreason!=null)
                {
                    ResultSet rsReasonCode = null;
                    try
                    {
                        intCBCode = Integer.parseInt(cbreason);
                        String sQry = "select reason from cb_codes where `code`=" +intCBCode;
                        rsReasonCode = Database.executeQuery(sQry,conn);
                        if(rsReasonCode.next())
                        {
                            cbreason = rsReasonCode.getString("reason");
                        }
                        else
                        {
                            intCBCode=0;
                        }
                    }
                    catch(NumberFormatException nfe)
                    {
                        logger.debug("Chargeback Reason does not contain the code");
                        intCBCode=0;
                    }
                    catch(SystemError se)
                    {
                        logger.debug("Database Fetching Error while reading Chargeback Reason Description");
                        intCBCode=0;
                    }
                }
                if(intCBCode==0)
                {
                    if(chargeBackCodeMap.get(cbreason)!=null)
                        cbreason = (String)chargeBackCodeMap.get(cbreason);
                }
                //Code Added for the Chargeback Reason Description - End
                cardHolder = rs.getString("name");
                emailAddr = rs.getString("emailaddr");
                IPAddr = rs.getString("ipaddress");
                hrcode = rs.getString("hrcode");
                refundAmount = rs.getString("refundamount");
                captureAmount = rs.getString("captureamount");
                amount = rs.getString("amount");

                memberId = rs.getString("toid");

                company_name = rs.getString("company_name");
                contact_emails = rs.getString("contact_emails");
                authDate = rs.getString("authdate");
                captureDate = rs.getString("capdate");
                orderDescription = rs.getString("orderdescription");
                String taxPer = rs.getString("taxper");
                int chargebackCharge = rs.getInt("chargebackcharge");
                String accountId = rs.getString("accountid");
                GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                String currency = account.getCurrency();
                String address = account.getAddress();


                String status = rs.getString("status");
                int currtaxper = rs.getInt("M.taxper");
                String rsdescription = rs.getString("description");
                String icicimerchantid = rs.getString("icicimerchantid");
                String reversalCharges = rs.getString("reversalcharge");
                BigDecimal bdConst = new BigDecimal("0.01");


                TransactionEntry entry = new TransactionEntry();
                int transactionEntry =0;
                //Case 1  : Received full charge back for settled Transaction
                //Case 2 : Received partial charge back for settled Transaction
                //Case 3 : Received partial charge back for partially reversed Transaction
                // & Chargeback amount <= capture amount � refund amount

                if(status.equals("settled")||(status.equals("reversed")&& (Double.parseDouble(cbamount) <= rs.getDouble("cbamount"))))
                {
                    description = "Reversal of " + transid + " ( Chargeback of orderid : " + orderid + " for " + cbreason + ")";
                    transactionEntry = entry.chargebackTransaction(cbrefnumber, cbreason, cbamount, icicitransid, memberId, description, chargebackCharge, account, taxPer);
                }

                //Case 4 : Received full charge back for partially reversed Transaction
                //Case 5 : Received full charge back for full reversed Transaction
                //Case 6 : Received partial charge back for full reversed Transaction
                //Case 7 : Received partial charge back for partially reversed Transaction  &  Chargeback amount > capture amount � refund amount

                else if(status.equals("reversed")&& (Double.parseDouble(cbamount) > rs.getDouble("cbamount")))
                {
                    //call cancelReverseTransaction
                    String cancelledAmount =  String.valueOf(Double.parseDouble(cbamount) - rs.getDouble("cbamount"));
                    description = "Cancellation of Reversal of " + transid + " (  To Auto adjust Chargeback Received ) ";
                    entry.cancelReverseTransaction(refundAmount,cancelledAmount,icicitransid,memberId,icicimerchantid,description,account, rsdescription, transid, currtaxper, bdConst, reversalCharges, memberId);


                    description = "Reversal of " + transid + " ( Chargeback of orderid : " + orderid + " for " + cbreason + ")";
                    transactionEntry = entry.chargebackTransaction(cbrefnumber, cbreason, cbamount, icicitransid, memberId, description, chargebackCharge, account, taxPer);
                }




                entry.closeConnection();

                String rr_status = isRetrivalRequestAlreadySent(icicitransid);
                if (transactionEntry != 0)
                {
                    Hashtable contents = new Hashtable();
                    contents.put("DATE", date);
                    contents.put("MERCHANTID", memberId);
                    contents.put("COMPANY_NAME", company_name);
                    contents.put("ORDERID", orderid);
                    contents.put("ORDER_DESCRIPTION", orderDescription);
                    contents.put("CURRENCY", currency);
                    contents.put("AMOUNT", amount);
                    contents.put("CURRENCY", currency);
                    if (captureAmount != null)
                        contents.put("CAPTURE_AMOUNT", captureAmount);
                    if (refundAmount != null)
                    {
                        if(status.equals("reversed")&& (Double.parseDouble(cbamount) > rs.getDouble("cbamount")))
                            contents.put("REFUND_AMOUNT",  String.valueOf(Double.parseDouble(captureAmount) - Double.parseDouble(cbamount)));
                        else
                            contents.put("REFUND_AMOUNT", refundAmount);
                    }
                    if (cbamount != null)
                        contents.put("CHARGEBACK_AMOUNT", cbamount);
                    if (cbreason != null)
                        contents.put("CHARGEBACK_REASON", cbreason);
                    contents.put("CARD_HOLDER", cardHolder);
                    contents.put("TRACKING_ID", icicitransid);
                    contents.put("AUTH_DATE", authDate);
                    contents.put("CAPTURE_DATE", captureDate);
                    contents.put("EMAIL_ADDR", emailAddr);
                    contents.put("IP_ADDRESS", IPAddr);
                    contents.put("ADDRESS", address);

                    merchantsubject.append("Chargebacks received for " + cbreason + " - ");
                    if (rr_status == null)
                    {
                        String chargebackMail = Functions.replaceTag(chargebackMailTemplate, contents);
                        logger.info("calling SendMAil for merchant - chargeback");
                        Mail.sendAdminMail(merchantsubject.toString() + company_name + " Hrcode:" + hrcode, chargebackMail);

                        logger.info("called SendMAil for transacute -- chargeback");
                        merchantsubject.append(" Orderid:" + description + "Capture Amount:" + captureAmount);
                        //Mail.sendHtmlMail(contact_emails, fromAddress, null, null, merchantsubject.toString(), chargebackMail);

                        logger.info("called SendMAil for merchant -- chargeback");
                    }
                    else
                    {
                        String retrivalRequestChargebackMail = Functions.replaceTag(retrivalRequestChargebackMailTemplate, contents);
                        logger.info("calling SendMAil for merchant - chargeback due to retrival request");
                        Mail.sendAdminMail("Charge Back received against previous Retrieval Request for Tracking Id: " + icicitransid, retrivalRequestChargebackMail);

                        logger.info("called SendMAil for transacute -- chargeback due to retrival request");
                        merchantsubject.append(" Orderid:" + description + "Capture Amount:" + captureAmount);
                        //Mail.sendHtmlMail(contact_emails, fromAddress, null, null, "Charge Back received against previous Retrieval Request for Tracking Id: " + icicitransid, retrivalRequestChargebackMail);

                        logger.info("called SendMAil for merchant -- chargeback due to retrival request");
                    }

                }

            }
            else
            {
                throw new SystemError("Chargedback for this transaction cannot be processed.");
            }
        }
        catch(Exception e)
        {
            logger.error(Util.getStackTrace(e));
            throw new SystemError("Error whle performing chargedback.");
        }
        finally
        {
            Database.closeConnection(conn);
        }

    }

    public void genericProcessChargeback(String trackingid, String date, String cbrefnumber, String cbamount, String cbreason, String partial,GenericResponseVO chargeBackDetails,GatewayAccount gatewayAccount,AuditTrailVO auditTrailVOLocal) throws SQLException, SystemError
    {
        StringBuffer merchantsubject = new StringBuffer();
        Connection conn = null;
        String query = null;
        String captureAmount = "";
        String refundAmount = "";
        String amount = "";
        String orderDescription = "";
        String cardHolder = "";
        String emailAddr = "";
        String IPAddr = "";
        String hrcode = "";
        java.util.Date dt = null;
        String transid = null;
        String orderid = null;
        String memberId = null;
        String description = null;
        String authDate = null;
        String captureDate = null;
        HashMap cbMailforCustomer=new HashMap();
        MailService mailService=new MailService();
        String company_name = null;
        String contact_emails = null;



        transid = null;
        description = null;

        String accountId =null;

        if(gatewayAccount!=null && gatewayAccount.getAccountId()!=0)
        {
            accountId = String.valueOf(gatewayAccount.getAccountId());
        }
        if(accountId == null || accountId.equals("0") || accountId.equals(""))
        {
            accountId   = getAccountID(trackingid);       //get AccountId from bin-details table
        }

        if(accountId == null || accountId.equals("0") || accountId.equals(""))
        {
            accountId = searchAccountIdFromALLTables(trackingid);       //get Account Id after searching all tables
        }

        GatewayAccount account = null;

        if(gatewayAccount!=null)
        {
            account=gatewayAccount;
        }
        else
        {
            account= GatewayAccountService.getGatewayAccount(accountId);
        }


        String currency = account.getCurrency();
        String address = account.getAddress();
        if(address==null)
        {
            address ="";
        }

        String tablename = Database.getTableName(account.getGateway());

        try
        {
            conn = Database.getConnection();
            dt = new java.util.Date();

            if (Functions.checkStringNull(cbrefnumber) == null)
            {
                cbrefnumber = "Not Available for TrackingId_"+trackingid;
            }
            if(tablename.equals("transaction_icicicredit"))
                query = "select T.*,T.captureamount-T.refundamount as cbamount,date_format(from_unixtime(T.dtstamp),'%d/%m/%Y') as authdate,date_format(from_unixtime(T.podbatch),'%d/%m/%Y') as capdate,M.company_name,M.contact_emails,M.chargebackcharge,M.taxper,M.reversalcharge from "+tablename+" as T,members as M where icicitransid=" + trackingid + " and T.toid=M.memberid and ( T.status='settled' or  T.status='reversed')";
            else
                query = "select T.*,T.captureamount-T.refundamount as cbamount,date_format(from_unixtime(T.dtstamp),'%d/%m/%Y') as authdate,date_format(from_unixtime(T.podbatch),'%d/%m/%Y') as capdate,M.company_name,M.contact_emails,M.chargebackcharge,M.taxper,M.reversalcharge from "+tablename+" as T,members as M where trackingid=" + trackingid + " and T.toid=M.memberid and ( T.status='settled' or  T.status='reversed')";

            ResultSet rs = Database.executeQuery(query, conn);
            if (rs.next())
            {

                //transid = rs.getString("transid");
                transid = rs.getString("trackingid");
                orderid = rs.getString("description");

                //Code Added for the Chargeback Reason Description - Start
                int intCBCode=0;
                if(cbreason!=null)
                {
                    ResultSet rsReasonCode = null;
                    try
                    {
                        intCBCode = Integer.parseInt(cbreason);
                        String sQry = "select reason from cb_codes where `code`=" +intCBCode;
                        rsReasonCode = Database.executeQuery(sQry,conn);
                        if(rsReasonCode.next())
                        {
                            cbreason = rsReasonCode.getString("reason");
                        }
                        else
                        {
                            intCBCode=0;
                        }
                    }
                    catch(NumberFormatException nfe)
                    {
                        logger.debug("Chargeback Reason does not contain the code");
                        intCBCode=0;
                    }
                    catch(SystemError se)
                    {
                        logger.debug("Database Fetching Error while reading Chargeback Reason Description");
                        intCBCode=0;
                    }
                }
                /*if(intCBCode==0)
                {
                    if(chargeBackCodeMap.get(cbreason)!=null)
                        cbreason = (String)chargeBackCodeMap.get(cbreason);
                }*/
                //Code Added for the Chargeback Reason Description - End
                cardHolder = rs.getString("name");
                emailAddr = rs.getString("emailaddr");
                IPAddr = rs.getString("ipaddress");
                hrcode = rs.getString("hrcode");
                refundAmount = rs.getString("refundamount");
                captureAmount = rs.getString("captureamount");
                amount = rs.getString("amount");
                String customerEmail = rs.getString("emailaddr");
                memberId = rs.getString("toid");

                company_name = rs.getString("company_name");
                contact_emails = rs.getString("contact_emails");
                authDate = rs.getString("authdate");
                captureDate = rs.getString("capdate");
                orderDescription = rs.getString("orderdescription");
                String taxPer = rs.getString("taxper");
                int chargebackCharge = rs.getInt("chargebackcharge");



                String status = rs.getString("status");
                int currtaxper = rs.getInt("M.taxper");
                String rsdescription = rs.getString("description");
                String icicimerchantid = rs.getString("fromid");
                String reversalCharges = rs.getString("reversalcharge");
                BigDecimal bdConst = new BigDecimal("0.01");
                if(cbamount==null || cbamount.equals(""))
                {
                    cbamount = rs.getString("cbamount");
                }

                TransactionEntry entry = new TransactionEntry();
                int transactionEntry =0;
                //Case 1  : Received full charge back for settled Transaction
                //Case 2 : Received partial charge back for settled Transaction
                //Case 3 : Received partial charge back for partially reversed Transaction
                // & Chargeback amount <= capture amount � refund amount

                if(status.equals("settled")||(status.equals("reversed")&& (Double.parseDouble(cbamount) <= rs.getDouble("cbamount"))))
                {
                    description = "Reversal of " + transid + " ( Chargeback of orderid : " + orderid + " for " + cbreason + ")";
                    transactionEntry = entry.newGenericChargebackTransaction(cbrefnumber, cbreason, cbamount, String.valueOf(trackingid),String.valueOf(account.getAccountId()),description, chargeBackDetails,auditTrailVOLocal);
                }

                //Case 4 : Received full charge back for partially reversed Transaction
                //Case 5 : Received full charge back for full reversed Transaction
                //Case 6 : Received partial charge back for full reversed Transaction
                //Case 7 : Received partial charge back for partially reversed Transaction  &  Chargeback amount > capture amount � refund amount

                else if(status.equals("reversed")&& (Double.parseDouble(cbamount) > rs.getDouble("cbamount")))
                {



                    //call cancelReverseTransaction
                    String cancelledAmount =  String.valueOf(Double.parseDouble(cbamount) - rs.getDouble("cbamount"));
                    description = "Cancellation of Reversal of " + transid + " (  To Auto adjust Chargeback Received ) ";
                    entry.newGenericCancelReverseTransaction(trackingid,cancelledAmount,refundAmount,String.valueOf(account.getAccountId()),null );


                    description = "Reversal of " + transid + " ( Chargeback of orderid : " + orderid + " for " + cbreason + ")";
                    transactionEntry = entry.newGenericChargebackTransaction(cbrefnumber, cbreason, cbamount, String.valueOf(trackingid),String.valueOf(account.getAccountId()),description, chargeBackDetails,auditTrailVOLocal);
                }
                entry.closeConnection();

                String rr_status = isRetrivalRequestAlreadySent(trackingid);
            }
            else
            {
                throw new SystemError("Chargedback for this transaction cannot be processed.");
            }
            StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
            statusSyncDAO.updateAllChargebackTransactionFlowFlag(trackingid,"chargeback",conn);
        }
        catch(Exception e)
        {
            transactionLogger.error("Error whle performing chargedback---",e);
            throw new SystemError("Error whle performing chargedback.");
        }
        finally
        {
            Database.closeConnection(conn);
        }

    }
    public void genericProcessChargebackNew(String trackingid,String orderId,String cbrefnumber, String cbamount, String cbreason,String dbCaptureAmt,String dbRefundAmt,String dbChargebackAmt,String dbStatus,GenericResponseVO chargeBackDetails,GatewayAccount gatewayAccount,AuditTrailVO auditTrailVOLocal) throws SQLException, SystemError
    {
        Connection conn = null;
        String query = null;
        String description = null;

        try
        {
            TransactionEntry entry = new TransactionEntry();
            int transactionEntry =0;
            double captureAmountdb=Double.parseDouble(dbCaptureAmt);
            double chargebackAmountdb=Double.parseDouble(dbChargebackAmt);

            //Case 1: If status=capturesuccess or settled
            if(dbStatus.equals("capturesuccess") || dbStatus.equals("settled"))
            {
                description = "Reversal of " + trackingid + " ( Chargeback of orderid : " + orderId + " for " + cbreason + ")";
                transactionEntry = entry.newGenericChargebackTransactionNew(cbrefnumber, cbreason, cbamount, String.valueOf(trackingid), orderId, String.valueOf(gatewayAccount.getAccountId()), description, chargeBackDetails, auditTrailVOLocal);
            }

            else if((dbStatus.equals("reversed")))
            {
                //Case 2 : Received partial charge back for partially reversed Transaction
                // & Chargeback amount <= capture amount � refund amount
                if (Double.parseDouble(cbamount) <= Double.parseDouble(dbCaptureAmt) - Double.parseDouble(dbRefundAmt))
                {
                    description = "Chargeback of " + trackingid + " ( Chargeback of orderid : " + orderId + " for " + cbreason + ")";
                    transactionEntry = entry.newGenericChargebackTransactionNew(cbrefnumber, cbreason, cbamount, String.valueOf(trackingid), orderId, String.valueOf(gatewayAccount.getAccountId()), description, chargeBackDetails, auditTrailVOLocal);
                }
                //Case 3 : Received full charge back for partially reversed Transaction
                //Case 4 : Received full charge back for full reversed Transaction
                //Case 5 : Received partial charge back for full reversed Transaction
                //Case 6 : Received partial charge back for partially reversed Transaction  &  Chargeback amount > capture amount � refund amount
                else if(Double.parseDouble(cbamount) >= Double.parseDouble(dbCaptureAmt) - Double.parseDouble(dbRefundAmt))
                {
                    //call cancelReverseTransaction
                    String cancelledAmount = String.valueOf(Double.parseDouble(cbamount) - (Double.parseDouble(dbCaptureAmt) - Double.parseDouble(dbRefundAmt)));
                    logger.error("cancelledAmount:::::"+cancelledAmount);
                    description = "Cancellation of Reversal of " + trackingid + " (  To Auto adjust Chargeback Received ) ";
                    entry.newGenericCancelReverseTransactionNew(trackingid, cancelledAmount, dbRefundAmt, dbStatus,String.valueOf(gatewayAccount.getAccountId()),null );
                    description = "Chargeback of " + trackingid + " ( Chargeback of orderid : " + orderId + " for " + cbreason + ")";
                    transactionEntry = entry.newGenericChargebackTransactionNew(cbrefnumber, cbreason, cbamount, String.valueOf(trackingid), orderId, String.valueOf(gatewayAccount.getAccountId()), description, chargeBackDetails, auditTrailVOLocal);
                }
            }else if(dbStatus.equals("chargeback")){
                //Case 7 : Received Partial charge back and cbamount<=dbcaptureAmt-dbChargebackAmt
                double minusValue= Double.parseDouble(String.format("%.2f",captureAmountdb-chargebackAmountdb));
                if (Double.parseDouble(cbamount) <= minusValue)
                {
                    description = "chargeback of " + trackingid + " ( Chargeback of orderid : " + orderId + " for " + cbreason + ")";
                    transactionEntry = entry.newGenericChargebackTransactionNew(cbrefnumber, cbreason, cbamount, String.valueOf(trackingid), orderId, String.valueOf(gatewayAccount.getAccountId()), description, chargeBackDetails, auditTrailVOLocal);
                }
            }
            else if(dbStatus.equals("chargebackreversed")){
                logger.error("inside dbStatus chargebackreversed---"+dbStatus);
                //Case 8 : Received Secound Chargeback For the same Id
                double minusValue= Double.parseDouble(String.format("%.2f",captureAmountdb-chargebackAmountdb));
                if (Double.parseDouble(cbamount) <= minusValue)
                {
                    description = "chargeback of " + trackingid + " ( Chargeback of orderid : " + orderId + " for " + cbreason + ")";
                    transactionEntry = entry.newGenericChargebackTransactionForChargebackReversed(cbrefnumber, cbreason, cbamount, String.valueOf(trackingid), orderId, String.valueOf(gatewayAccount.getAccountId()), description, chargeBackDetails, auditTrailVOLocal);
                }
            }
            logger.error("transactionEntry::::"+transactionEntry+"-----"+trackingid);
            entry.closeConnection();
            String rr_status = isRetrivalRequestAlreadySent(trackingid);
            StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
            conn = Database.getConnection();
            statusSyncDAO.updateAllChargebackTransactionFlowFlag(trackingid,"chargeback",conn);
        }
        catch(Exception e)
        {
            transactionLogger.error("Error whle performing chargedback---",e);
            throw new SystemError("Error whle performing chargedback.");
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public void processSendRetrivalRequest(String icicitransid, String date) throws SQLException, SystemError
    {
        Connection dbconn = null;
        String query = null;
        ResultSet rs = null;
        try
        {
            dbconn = Database.getConnection();
            query = "insert into retrival_receipt_history values(?,'rrsent',unix_timestamp(),0)";

            String getDetails = "select M.*,TC.*,TC.captureamount-TC.refundamount as disputedAmount,TC.accountid as transactionaccountid, date_format(from_unixtime(TC.dtstamp),'%d/%m/%Y') as authdate,date_format(from_unixtime(TC.podbatch),'%d/%m/%Y') as capdate from transaction_icicicredit TC,members M where status = 'settled' and toid=memberid and icicitransid =? ";
            PreparedStatement ps=dbconn.prepareStatement(getDetails);
            ps.setString(1,icicitransid);
            rs = ps.executeQuery();
            if (rs.next())
            {
                PreparedStatement pstmt=dbconn.prepareStatement(query);
                pstmt.setString(1,icicitransid);
                int result = pstmt.executeUpdate();
                if (result == 0)
                {
                    sendRetrivalRequestMail(rs, icicitransid, date);
                }
                else
                {
                    logger.debug("Unable to send retrival request for  Transaction Id ");
                    throw new SystemError("Unable to send retrival request for  Transaction Id " + icicitransid);
                }
            }
            else
            {
                logger.debug("invalid Transaction Id ");
                throw new SystemError("invalid Transaction Id " + icicitransid);
            }
        }
        catch (SystemError e)
        {

            if (e.getMessage().indexOf("Duplicate entry '" + icicitransid + "'") != -1)
            {
                logger.debug("Since duplicate entry update rrsent date for existing transaction entry");
                query = "update retrival_receipt_history set rr_sent_date=unix_timestamp() where status = 'rrsent' and icicitransid = ?";
                PreparedStatement pst=dbconn.prepareStatement(query);
                pst.setString(1,icicitransid);
                int result = pst.executeUpdate();
                if (result != 0)
                {
                    sendRetrivalRequestMail(rs, icicitransid, date);
                }
                else
                {
                    logger.debug("Required Documents have already been received for transaction ID" );
                    throw new SystemError("Required Documents have already been received for transaction ID" + icicitransid);
                }
            }
            else
            {
                logger.error(Functions.getStackTrace(e));
                throw e;
            }
        }
        finally
        {
            Database.closeConnection(dbconn);
        }
    }

    public void executeChargeback(String accountId,String transStatus,String cbreason,String amount,String cbamount,String trackingId,GenericResponseVO settlementDetails,AuditTrailVO auditTrailVO)
    {
        logger.debug("Inside executeChargeback method :::");

        //update primary table
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String tablename = Database.getTableName(account.getGateway());

        logger.debug("Account Details---"+account);

        Connection conn = null;
        ActionEntry entry = new ActionEntry();
        try
        {
            conn = Database.getConnection();
            Database.setAutoCommit(conn,false);

            String query = "";
            if(transStatus.equals("capturesuccess"))
            {
                logger.debug("if status---" + transStatus);
                query = "update transaction_common set status='settled' where trackingid=?";
                PreparedStatement p1 = conn.prepareStatement(query);
                p1.setString(1, trackingId);
                p1.executeUpdate();
                logger.debug("Update query from transaction---" + p1);

                int actionEntry = entry.genericActionEntry(trackingId,cbamount,ActionEntry.ACTION_CREDIT,ActionEntry.STATUS_CREDIT,null,account.getGateway(),settlementDetails,auditTrailVO);
            }
            else if(transStatus.equals("markedforreversal"))
            {
                logger.debug("if status---" + transStatus);
                query = "update transaction_common set status='reversed',refundamount='"+amount+"' where status='markedforreversal' and trackingid=?";
                PreparedStatement p1 = conn.prepareStatement(query);
                p1.setString(1, trackingId);
                p1.executeUpdate();
                logger.debug("Update query from transaction---" + p1);

                int actionEntry = entry.genericActionEntry(trackingId,cbamount,ActionEntry.ACTION_REVERSAL_SUCCESSFUL,ActionEntry.STATUS_REVERSAL_SUCCESSFUL,null,account.getGateway(),settlementDetails,auditTrailVO);
            }


            /*//commented by nikita
            String sQuery = "update transaction_common set status='chargeback',chargebackinfo='" + cbreason + "',chargebackamount=" + cbamount + " where trackingid=" + trackingId + " and ( status='settled' or ( status='reversed' and captureamount>refundamount))";*/
            Functions functions = new Functions();
            String sQuery = "update transaction_common set status='chargeback',chargebackinfo='" + cbreason + "',chargebackamount=" + cbamount + ",chargebacktimestamp='"+functions.getTimestamp()+"' where trackingid=" + trackingId + " and ( status='settled' or  status='reversed')";
            PreparedStatement p = conn.prepareStatement(sQuery);
            p.executeUpdate();
            logger.debug("Update status chargeback---" + p);

            logger.debug("ActionExecutorId---------"+auditTrailVO.getActionExecutorId());
            int actionEntry = entry.genericActionEntry(trackingId,cbamount,ActionEntry.ACTION_CHARGEBACK_RACEIVED,ActionEntry.STATUS_CHARGEBACK_RACEIVED,null,account.getGateway(),settlementDetails,auditTrailVO);
            logger.debug("ActionEntry for Chargeback received");

            //update transaction bin detail
            StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
            logger.debug("Before update status--");
            statusSyncDAO.updateAllChargebackTransactionFlowFlag(trackingId,"chargeback",conn);
            logger.debug("After update status--");
            Database.commit(conn);

        }
        catch (SQLException e)
        {
            Database.rollback(conn);
            logger.error("SQL Exception while chargeback transaction via Common Process",e);
        }
        catch (SystemError systemError)
        {
            Database.rollback(conn);
            logger.error("System Error while chargeback transaction via Common Process", systemError);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public void processDocReceived(String icicitransid) throws SystemError, SQLException
    {
        Connection dbconn = null;
        dbconn = Database.getConnection();
        String rr_status = isRetrivalRequestAlreadySent(icicitransid);
        if (rr_status != null)
        {
            String query = "update retrival_receipt_history set status ='docreceived',docs_received_date=unix_timestamp() where icicitransid = ?";
            PreparedStatement pst=dbconn.prepareStatement(query);
            pst.setString(1, icicitransid);
            pst.executeUpdate();
        }
        else
        {
            logger.debug("Retrival Request has not been sent for Transaction Id" + icicitransid);
            throw new SystemError("Retrival Request has not been sent for Transaction Id" + icicitransid);
        }
        Database.closeConnection(dbconn);
    }

    public Hashtable getAuthCodeMap()
    {
        return authCodeMap;
    }

    public boolean sendForgotPasswordMail(String emailAddr, String login, String password) throws SystemError
    {

        Date date = new Date();
        String strDate = date.toString();

        String company = ApplicationProperties.getProperty("COMPANY");
        String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
        String supportFromAddress = ApplicationProperties.getProperty("SUPPORT_FROM_ADDRESS");
        String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        Hashtable values = new Hashtable();
        values.put("COMPANY", company);
        values.put("SUPPORT_URL", supportUrl);
        values.put("LIVE_URL", liveUrl);
        values.put("TIME", strDate);
        if (login != null)
        {
            values.put("MESSAGE", "Merchant Interface Login Details");
            values.put("USERNAME", "USERNAME : " + login);
        }
        else
        {
            values.put("MESSAGE", "Transaction Password");
        }
        values.put("PASSWORD", password);

        String mailMessage = Functions.replaceTag(forgotPasswordMailTemplate, values);


        //Mail.sendHtmlMail(emailAddr, supportFromAddress, null, null, "Forgot Password", mailMessage);
        return true;
    }

    public Hashtable<String, String> getDetails(String trackingId) throws SystemError, SQLException
    {

        Connection conn = Database.getConnection();
        String s="select * from transaction_icicicredit where icicitransid =? ";
        PreparedStatement pst=conn.prepareStatement(s);
        pst.setString(1,trackingId);
        ResultSet rs = pst.executeQuery();
        Hashtable<String, String> detailsOngateway = null;
        if (rs.next())
        {
            String accountId = rs.getString("accountid");
            AbstractPaymentGateway pg = AbstractPaymentGateway.getGateway(accountId);
            detailsOngateway = pg.getStatus(trackingId);

        }
        return detailsOngateway;

    }

    public Hashtable<String, String> getDetails(String trackingId, String memberId, String newChecksum, String version, String rediectUrl) throws SystemError, SQLException
    {

        try
        {
            if (Checksum.verifyNewChecksum(trackingId, memberId, newChecksum))
            {
                Hashtable<String, String> details = new Hashtable<String, String>();
                Connection conn = null;
                try
                {
                    conn = Database.getConnection();
                    //Statement stmnt = conn.createStatement();
                    String query = "select T.amount,T.toid,T.status,T.description,T.orderdescription,T.authqsiresponsedesc,T.captureamount,M.clkey,M.checksumalgo,M.chargeper,M.fixamount,M.autoredirect from transaction_icicicredit T,members M where T.toid = M.memberid and icicitransid =? and T.toid =? ";

                    PreparedStatement pstmt=conn.prepareStatement(query);
                    pstmt.setString(1,trackingId);
                    pstmt.setString(2,memberId);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next())
                    {
                        String statusInDb = rs.getString("status");
                        String processingCompleted = "false";
                        String transactionStatus="";
                        if ("begun".equals(statusInDb) || "authstarted".equals(statusInDb))
                        {   processingCompleted = "false";
                            //return details;
                        }

                        if ("authfailed".equals(statusInDb))
                        {
                            transactionStatus = "N";
                            processingCompleted = "true";
                        }
                        else if ("proofrequired".equals(statusInDb))
                        {
                            transactionStatus = "P";
                            processingCompleted = "true";
                        }

                        else
                        if ("authsuccessful".equals(statusInDb) || "capturestarted".equals(statusInDb) || "capturesuccess".equals(statusInDb))
                        {
                            transactionStatus = "Y";
                            processingCompleted = "true";
                        }
                        else
                        {   processingCompleted = "false";
                            // return details;
                        }
                        String transactionMessage="";
                        String transactionDescription = rs.getString("description");
                        String orderdescription = rs.getString("orderdescription");
                        if(rs.getString("authqsiresponsedesc") != null)
                        {
                            transactionMessage = rs.getString("authqsiresponsedesc");
                        }
                        String checksumAlgo = rs.getString("checksumalgo");
                        String clkey = rs.getString("clkey");
                        String txnamount = rs.getString("amount");
                        String chargeper = rs.getString("chargeper");
                        String fixamount = rs.getString("fixamount");
                        String capturedamount = rs.getString("captureamount");
                        String autoredirect = rs.getString("autoredirect");
                        if (capturedamount == null)
                            capturedamount = "0";
                        BigDecimal charge = new BigDecimal(chargeper);
                        BigDecimal amount = new BigDecimal(capturedamount);
                        BigDecimal chargeamount = amount.multiply(charge.multiply(new BigDecimal(1 / 10000.00)));
                        chargeamount = chargeamount.add(new BigDecimal(fixamount));
                        chargeamount = chargeamount.setScale(2, BigDecimal.ROUND_HALF_UP);
                        String chargeamt = chargeamount.toString();
                        String checksum;
                        try
                        {
                            if (version.equals("2"))
                                checksum = Checksum.generateChecksumV3(memberId, transactionStatus, transactionMessage, transactionDescription, "" + txnamount, chargeamt, clkey, checksumAlgo);
                            else
                                checksum = Checksum.generateChecksumV1(memberId, transactionStatus, transactionMessage, transactionDescription, "" + txnamount, clkey, checksumAlgo);
                        }
                        catch (NoSuchAlgorithmException e)
                        {
                            throw new SystemError("Error while processing ...");
                        }
                        if (transactionStatus.equals("P"))
                            details.put("PROOFREQUIRED", "Y");
                        else
                            details.put("PROOFREQUIRED", "N");

                        details.put("status", transactionStatus);
                        details.put("message", transactionMessage);
                        details.put("amount", txnamount);
                        details.put("chargeamt", chargeamt);
                        details.put("checksum", checksum);
                        details.put("AUTOREDIRECT", autoredirect);
                        details.put("TOID", memberId);
                        details.put("DESCRIPTION", transactionDescription);
                        details.put("ORDER_DESCRIPTION", orderdescription);
                        details.put("TRACKING_ID", trackingId);
                        details.put("REDIRECTURL", rediectUrl);
                        details.put("processingCompleted", processingCompleted);

                    }
                    else
                    {
                        throw new SystemError("Transaction Not Found");
                    }
                }
                finally
                {
                    Database.closeConnection(conn);
                }

                return details;
            }
            else
            {
                throw new SystemError("Checksum Mismatch ....");
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new SystemError("Error .. " + e.getMessage());
        }
    }


    public void sendReverseFraudTransactionMail(ArrayList<Hashtable> listOfRefunds, String memberId)
            throws SystemError
    {
        Hashtable mailValues = new Hashtable();
        String company = ApplicationProperties.getProperty("COMPANY");
        String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
        //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");

        String toAddress = null;
        String accountId = null;
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            //Statement stmnt = conn.createStatement();
            String query = "select M.notifyemail,M.accountid from members M where  M.memberid = ?";

            PreparedStatement pstmt=conn.prepareStatement(query);
            pstmt.setString(1,memberId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                toAddress = rs.getString("notifyemail");
                accountId = rs.getString("accountid");
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving Transaction throwing SQL Exception as System Error : ",e);
            throw new SystemError("Error : " + e.getMessage());
        }

        StringBuffer details = new StringBuffer();
        Hashtable refundDetails = null;

        for(int i=0; i<listOfRefunds.size();i++)
        {


            refundDetails = listOfRefunds.get(i);
            details.append("<tr >");
            details.append("<td >"+ refundDetails.get("icicitransid")+ "</td>");
            details.append("<td >"+ refundDetails.get("captureamount")+ "</td>");
            details.append("<td >"+ refundDetails.get("refundamount")+ "</td>");
            details.append("<td >"+ refundDetails.get("cardholdername")+ "</td>");
            details.append("<td >"+ refundDetails.get("description")+ "</td>");
            details.append("</tr >");

        }

        mailValues.put("DETAILS", details.toString());

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String currency = account.getCurrency();

        mailValues.put("CURRENCY", currency);
        mailValues.put("COMPANY", company);
        mailValues.put("LIVE_URL", liveUrl);
        mailValues.put("SUPPORT_URL", supportUrl);


        String fraudTransactionReverseMail = Functions.replaceTag(fraudTransactionReverseTemplate, mailValues);
        // Mail.sendHtmlMail(toAddress, fromAddress, null, null, " List of Fraudulent Transactions ", fraudTransactionReverseMail);


    }


    public void sendReverseTransactionMail(ArrayList<Hashtable> listOfRefunds, String memberId)
            throws SystemError
    {

        Hashtable mailValues = new Hashtable();
        String company = ApplicationProperties.getProperty("COMPANY");
        String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
        //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");

        String toAddress = null;
        String accountId = null;
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            //Statement stmnt = conn.createStatement();
            String query = "select M.notifyemail,M.accountid from members M where  M.memberid = ?";

            PreparedStatement pstmt=conn.prepareStatement(query);
            pstmt.setString(1,memberId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                toAddress = rs.getString("notifyemail");
                accountId = rs.getString("accountid");
            }
        }
        catch (SQLException e)
        {
            logger.error("Leaving Transaction throwing SQL Exception as System Error : ",e);
            throw new SystemError("Error : " + e.getMessage());
        }

        StringBuffer details = new StringBuffer();
        Hashtable refundDetails = null;

        for(int i=0; i<listOfRefunds.size();i++)
        {


            refundDetails = listOfRefunds.get(i);
            details.append("<tr >");
            details.append("<td >"+ refundDetails.get("icicitransid")+ "</td>");
            details.append("<td >"+ refundDetails.get("captureamount")+ "</td>");
            details.append("<td >"+ refundDetails.get("refundamount")+ "</td>");
            details.append("<td >"+ refundDetails.get("cardholdername")+ "</td>");
            details.append("<td >"+ refundDetails.get("description")+ "</td>");
            details.append("</tr >");

        }

        mailValues.put("DETAILS", details.toString());

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String currency = account.getCurrency();

        mailValues.put("CURRENCY", currency);
        mailValues.put("COMPANY", company);
        mailValues.put("LIVE_URL", liveUrl);
        mailValues.put("SUPPORT_URL", supportUrl);


        String transactionReverseMail = Functions.replaceTag(transactionReverseTemplate, mailValues);
        //Mail.sendHtmlMail(toAddress, fromAddress, null, null, " List of Reversal Transactions ", transactionReverseMail);


    }

    public void sendSettlementCron(StringBuffer data,String displayName)
            throws SystemError
    {
        SendTransactionEventMailUtil sendTransactionEventMailUtil=new SendTransactionEventMailUtil();
        sendTransactionEventMailUtil.sendTransactionEventMail(MailEventEnum.ADMIN_SETTLEMENT_REPORT,"",displayName,data.toString(),null);
    }

    public void sendReconcilationMail(StringBuffer data,String toAddress)
            throws SystemError
    {
        Hashtable mailValues = new Hashtable();
        String company = ApplicationProperties.getProperty("COMPANY");

        //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
        if(toAddress==null)
        {
            toAddress =  ApplicationProperties.getProperty("COMPANY_ADMIN_EMAIL");
        }


        mailValues.put("DETAILS", data.toString());

        mailValues.put("COMPANY", company);
        String reconcilationMail = Functions.replaceTag(reconcilationmail, mailValues);
        logger.debug(reconcilationMail);
        // Mail.sendHtmlMail(toAddress, fromAddress, null, null, " List of Reconciled Transactions ", reconcilationMail);
    }

    public void sendFailTransactionMail(String to,String cc,StringBuffer transfail,String fdate,String tdate,String com,String toid,StringBuffer status,int record,String grand)
            throws SystemError
    {
        Hashtable mailValues = new Hashtable();
        String company = ApplicationProperties.getProperty("COMPANY");
        String liveUrl = ApplicationProperties.getProperty("COMPANY_LIVE_URL");
        String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
        //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
        //mailValues.put("MERCHANT_NAME",merchantname);
        mailValues.put("MERCHANTID",toid);
        mailValues.put("COMPENY",com);
        mailValues.put("FROMDATE", fdate);
        mailValues.put("TODATE", tdate);
        mailValues.put("TOTAL",record+"");
        mailValues.put("STATUSDETAILS", status.toString());
        mailValues.put("GRAND", grand);
        mailValues.put("DETAILS", transfail.toString());
        mailValues.put("COMPANY", company);
        String transactionReverseMail = Functions.replaceTag(transactionFailTemplate, mailValues);

        Mail.sendHtmlMail(to,"", cc, null, "Report of Failed Transactions", transactionReverseMail);
    }


    public int insertTranseQwipi(String toid, String totype, String fromid, String fromtype, String description, String orderDescription, String amount, String redirectUrl,String status, String accountId, int paymodeId, int cardtypeId, String currency, String httpHeader,String ipaddress,String terminalid) throws PZDBViolationException
    {
        int trackingId = 0;
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query="insert into transaction_qwipi(toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress,terminalid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?)";
            PreparedStatement p=con.prepareStatement(query);

            p.setString(1,toid);
            p.setString(2,totype);
            p.setString(3,fromid);
            p.setString(4,fromtype);
            p.setString(5,description);
            p.setString(6,orderDescription);
            p.setString(7,amount);
            p.setString(8,redirectUrl);
            p.setString(9,status);
            p.setString(10,accountId);
            p.setInt(11,paymodeId);
            p.setInt(12,cardtypeId);
            p.setString(13,currency);
            p.setString(14,httpHeader);
            p.setString(15,ipaddress);
            p.setString(16,terminalid);
            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if(rs!=null)
                {
                    while(rs.next())
                    {
                        trackingId = rs.getInt(1);
                    }
                }
            }
            logger.debug("insert query for qwipi---"+p);
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java","insertTranseQwipiException()",null,"common","SQL Exception thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException s)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "insertTranseQwipiException()", null, "common", "SQL Exception thrown:::", PZDBExceptionEnum.INCORRECT_QUERY,null, s.getMessage(), s.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        return trackingId;
    }

    public int insertTransEcore(String toid, String totype, String fromid, String fromtype, String descreption, String orderdescription, String amount, String redirecturl, String accontid, int paymodeid, int cardtypeid, String currency, String httphadder,String ipaddress,String terminalid) throws PZDBViolationException
    {
        int trackingId = 0;
        Connection con= null;
        try
        {
            con = Database.getConnection();
            String query="insert into transaction_ecore(toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress,terminalid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?,?)";
            PreparedStatement p=con.prepareStatement(query);

            p.setString(1,toid);
            p.setString(2,totype);
            p.setString(3,fromid);
            p.setString(4,fromtype);
            p.setString(5,descreption);
            p.setString(6,orderdescription);
            p.setString(7,amount);
            p.setString(8,redirecturl);

            p.setString(9,"begun");
            p.setString(10,accontid);
            p.setInt(11, paymodeid);
            p.setInt(12, cardtypeid);
            p.setString(13,currency);
            p.setString(14,httphadder);
            p.setString(15,ipaddress);
            p.setString(16,terminalid);
            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if(rs!=null)
                {
                    while(rs.next())
                    {

                        trackingId = rs.getInt(1);
                    }
                }
            }
        }
        catch (SQLException s)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java","insertTranseQwipiException()",null,"common","SQL Exception thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,s.getMessage(),s.getCause());
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java","insertTranseQwipiException()",null,"common","SQL Exception thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }

        finally
        {
            Database.closeConnection(con);
        }

        return trackingId;

    }
    public int insertTransCommon(String toid, String totype, String fromid, String fromtype, String descreption, String orderdescription, String amount, String redirecturl, String accontid, int paymodeid, int cardtypeid, String currency, String httphadder,String ipaddress) throws PZDBViolationException
    {
        int trackingId = 0;
        Connection con= null;
        try
        {
            con= Database.getConnection();
            String query="insert into transaction_common(toid,totype,fromid,fromtype,description,orderdescription,amount,redirecturl,status,accountid,paymodeid,cardtypeid,currency,dtstamp,httpheader,ipaddress) values(?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()),?,?)";
            PreparedStatement p=con.prepareStatement(query);
            p.setString(1,toid);
            p.setString(2,totype);
            p.setString(3,fromid);
            p.setString(4,fromtype);
            p.setString(5,descreption);
            p.setString(6,orderdescription);
            p.setString(7,amount);
            p.setString(8,redirecturl);
            p.setString(9,"begun");
            p.setString(10,accontid);
            p.setInt(11,paymodeid);
            p.setInt(12,cardtypeid);
            p.setString(13,currency);
            p.setString(14,httphadder);
            p.setString(15,ipaddress);
            int num = p.executeUpdate();
            if (num == 1)
            {
                ResultSet rs = p.getGeneratedKeys();

                if(rs!=null)
                {
                    while(rs.next())
                    {
                        trackingId = rs.getInt(1);
                    }
                }
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java","insertTranseQwipiException()",null,"common","SQL Exception thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException s)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "insertTranseQwipiException()", null, "common", "SQL Exception thrown:::", PZDBExceptionEnum.INCORRECT_QUERY,null, s.getMessage(), s.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return trackingId;

    }
    public void updateCurrencyCodeQwipi(String curcode, String curval, String trackingId)
    {
        Connection con =null;
        try
        {
            if (curcode != null)
            {

                con = Database.getConnection();
                logger.debug("Updating the details"+curval);
                String strsql="update transaction_qwipi set templatecurrency =? ,templateamount =? where trackingid  =?";
                PreparedStatement p2=con.prepareStatement(strsql);
                p2.setString(1,curcode);
                p2.setString(2,curval);
                p2.setString(3,trackingId);
                p2.executeUpdate();
            }
        }
        catch (SQLException systemError)
        {   logger.error("Sql Exception :::::",systemError);
            logger.error("Exception while updating template fees : " + systemError.getMessage());
        }
        catch (SystemError systemError)
        {   logger.error("System Error ",systemError);

        }
        finally
        {
            Database.closeConnection(con);
        }
    }
    public void updateCurrencyCodeEcore(String curcode, String curval, String trackingId)
    {
        Connection con =null;
        try
        {
            if (curcode != null)
            {

                con = Database.getConnection();
                logger.debug("Updating the details"+curval);
                String strsql="update transaction_ecore set templatecurrency =? ,templateamount =? where trackingid  =?";
                PreparedStatement p2=con.prepareStatement(strsql);
                p2.setString(1,curcode);
                p2.setString(2,curval);
                p2.setString(3,trackingId);
                p2.executeUpdate();
            }
        }
        catch (SQLException systemError)
        {
            logger.error("Sql Exception :::::",systemError);

        }
        catch (SystemError systemError)
        {
            logger.error("System Error ",systemError);

        }
        finally
        {
            Database.closeConnection(con);
        }
    }
    public void updateCurrencyCodeCommon(String curcode, String curval, String trackingId)
    {
        Connection con = null;
        try
        {
            if (curcode != null)
            {
                con = Database.getConnection();
                logger.debug("Updating the details" + curval);
                String strsql = "update transaction_common set templatecurrency =? ,templateamount =? where trackingid  =?";
                PreparedStatement p2 = con.prepareStatement(strsql);
                p2.setString(1, curcode);
                p2.setString(2, curval);
                p2.setString(3, trackingId);
                p2.executeUpdate();
            }
        }
        catch (SQLException systemError)
        {
            logger.error("Sql Exception :::::", systemError);

        }
        catch (SystemError systemError)
        {
            logger.error("System Error ", systemError);

        }
        finally
        {
            Database.closeConnection(con);
        }
    }

    public void updateTransactionStatusCommon(String updatedstatus, String trackingId) throws SystemError
    {
        Connection conn = null;
        Statement stmt = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer sb = new StringBuffer("update transaction_common set");
        sb.append(" status='"+updatedstatus+"'  where trackingid=" + ESAPI.encoder().encodeForSQL(me, trackingId));
        logger.debug("query::::"+sb.toString());
        try
        {   conn = Database.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sb.toString());
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            throw new SystemError(se.toString());

        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public void updateTransactionStatusCommon(String updatedstatus, String trackingId,String remark) throws SystemError
    {
        Connection conn = null;
        Statement stmt = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer sb = new StringBuffer("update transaction_common set");
        sb.append(" status='"+updatedstatus+"',"+"remark ='"+remark+"' where trackingid=" + ESAPI.encoder().encodeForSQL(me, trackingId));
        logger.debug("query::::"+sb.toString());
        try
        {   conn = Database.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sb.toString());
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            throw new SystemError(se.toString());

        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public void updateTransactionStatusEcore(String updatedstatus, String trackingId)throws PZDBViolationException
    {
        Connection conn = null;
        Statement stmt = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer sb = new StringBuffer("update transaction_ecore set");
        sb.append(" status='"+updatedstatus+"'  where trackingid=" + ESAPI.encoder().encodeForSQL(me, trackingId));
        logger.debug("query::::"+sb.toString());
        try
        {   conn = Database.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sb.toString());
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java","updateTransactionStatusForQwipi()",null,"common","SQL Exception thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException s)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "updateTransactionStatusForQwipi()", null, "common", "SQL Exception thrown:::", PZDBExceptionEnum.INCORRECT_QUERY,null, s.getMessage(), s.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }


    }

    public void updateTransactionStatusForQwipi(String updatedstatus, String trackingId)throws PZDBViolationException
    {
        Connection conn = null;
        Statement stmt = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {
            conn = Database.getConnection();
            StringBuffer sb = new StringBuffer("update transaction_qwipi set");
            sb.append(" status='"+updatedstatus+"'  where trackingid=" + ESAPI.encoder().encodeForSQL(me, trackingId));
            logger.debug("query::::"+sb.toString());

            stmt = conn.createStatement();
            stmt.executeUpdate(sb.toString());
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java","updateTransactionStatusForQwipi()",null,"common","SQL Exception thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException s)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "updateTransactionStatusForQwipi()", null, "common", "SQL Exception thrown:::", PZDBExceptionEnum.INCORRECT_QUERY,null, s.getMessage(), s.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }


    }
    /*public void updateGenericTransactionStatus(String updatedstatus,String trackingId,String tablename) throws SystemError
    {
        if(tablename.equals("transaction_qwipi"))
        {
            updateTransactionStatusForQwipi(PZTransactionStatus.FAILED.toString(),trackingId);
        }
        else if(tablename.equals("transaction_ecore"))
        {
            updateTransactionStatusEcore(PZTransactionStatus.FAILED.toString(),trackingId);
        }
        else if(tablename.equals("transaction_common"))
        {
            updateTransactionStatusCommon(PZTransactionStatus.FAILED.toString(),trackingId);
        }
    }*/
    public int executeUpdate(String updateQuery) throws SystemError
    {
        Connection conn = null;
        Statement stmt = null;
        int result = -999999;
        logger.debug("query::::"+updateQuery.toString());
        try
        {   conn = Database.getConnection();
            stmt = conn.createStatement();
            result = stmt.executeUpdate(updateQuery.toString());
        }
        catch (SQLException se)
        {
            Database.rollback(conn);
            Mail.sendAdminMail("Exception while Update", "\r\n\r\nException has occured while execute update \r\n\r\n" + updateQuery);
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            throw new SystemError(se.toString());

        }
        finally
        {
            Database.closeConnection(conn);
        }

        return result;
    }


    public Hashtable getTransactionDetailsForEcore(String trackingId) throws PZDBViolationException
    {

        Connection con =null;
        Hashtable detailhash = new Hashtable();

        try
        {
            con=Database.getConnection();
            String sql = "select amount,toid,fromid,description,orderdescription,dtstamp,redirecturl,ccnum,totype,captureamount,currency,amount,status,httpheader,accountId,timestamp from transaction_ecore where trackingid=?";
            PreparedStatement p1=con.prepareStatement(sql);
            p1.setString(1,trackingId);
            ResultSet rs = p1.executeQuery();

            if (rs.next())
            {

                detailhash.put("toid", String.valueOf(rs.getInt("toid")));
                detailhash.put("amount", rs.getString("amount"));
                detailhash.put("accountid", rs.getString("accountid"));
                detailhash.put("fromid", rs.getString("fromid"));
                detailhash.put("description", rs.getString("description"));
                detailhash.put("orderdescription", rs.getString("orderdescription"));
                detailhash.put("dtstamp", rs.getString("dtstamp"));
                detailhash.put("currency", rs.getString("currency"));
                detailhash.put("amount", rs.getString("amount"));
                detailhash.put("status", rs.getString("status"));
                //added for single call
                detailhash.put("redirecturl",rs.getString("redirecturl"));
                detailhash.put("ccnum",rs.getString("ccnum"));
                detailhash.put("totype",rs.getString("totype"));
                detailhash.put("captureamount",rs.getString("captureamount"));

            }

            //added for single call
            String query="SELECT ecorePaymentOrderNumber FROM transaction_ecore_details WHERE trackingid = (SELECT MAX(trackingid)  FROM transaction_ecore_details WHERE parentid =? AND ecorePaymentOrderNumber IS NOT NULL)";
            PreparedStatement p=con.prepareStatement(query);
            p.setString(1,trackingId);
            ResultSet rs2 =p.executeQuery();
            if(rs2.next())
            {
                detailhash.put("transId",rs2.getString("ecorePaymentOrderNumber"));
            }

        }
        catch (SystemError se)
        {
            logger.error("System Error",se);
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "getTransactionDetailsForEcore()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        catch (SQLException s)
        {
            logger.error("SQLException",s);
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "getTransactionDetailsForEcore()", null, "common", "SQL Exception Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, s.getMessage(), s.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return detailhash;
    }

    public Hashtable getMidKeyForEcore(String accountId)
    {

        Connection con =null;
        String MD5key =null;
        String mid = null;
        Hashtable ecoreHash = new Hashtable();
        try
        {
            con=Database.getConnection();
            String q1= "select midkey,mid from gateway_accounts_ecore where accountid=?";
            PreparedStatement p8=con.prepareStatement(q1);
            p8.setString(1,accountId);
            ResultSet rs = p8.executeQuery();
            if (rs.next())
            {
                ecoreHash.put("mid",rs.getString("mid"));
                ecoreHash.put("midkey",rs.getString("midkey"));
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            PZExceptionHandler.raiseAndHandleDBViolationException("Transaction.java","getMidKeyForEcore()",null,"common","SQL Exception Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,se.getMessage(),se.getCause(),null,null);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseAndHandleGenericViolationException("Transaction.java", "getMidKeyForEcore()", null, "common", "SQL Exception Thrown:::",null, se.getMessage(), se.getCause(), null, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return ecoreHash ;
    }


    public void updateBinDetails(String trackingId, String ccpan, String accountId,String emailaddr, String boiledname )
    {
        Connection con =null;
        try
        {
            Functions functions = new Functions();
            PaymentManager paymentManager=new PaymentManager();
            String subCardType = "Consumer Card";
            if (functions.isValueNull(ccpan))
            {
                subCardType = paymentManager.getSubCardType(ccpan.substring(0, 6));
            }
            con = Database.getConnection();
            String qry="insert into bin_details (icicitransid, first_six, last_four, accountid,emailaddr,boiledname,subcard_type) values(?,?,?,?,?,?,?)";
            PreparedStatement pstmt=con.prepareStatement(qry);
            pstmt.setString(1,trackingId);
            if (functions.isValueNull(ccpan))
            {
                pstmt.setString(2, ccpan.substring(0, 6));
                pstmt.setString(3, ccpan.substring(ccpan.length() - 4));
            }
            else
            {
                pstmt.setString(2, "");
                pstmt.setString(3, "");
            }
            pstmt.setString(4,accountId);
            pstmt.setString(5,emailaddr);
            pstmt.setString(6,boiledname);
            pstmt.setString(7,subCardType);
            pstmt.executeUpdate();
        }
        catch (SQLException systemError)
        {   logger.error("Sql Exception :::::",systemError);

            //ToDO raise and handle DB Exception

        }
        catch (SystemError systemError)
        {   logger.error("System Error ",systemError);
            //ToDO raise and handle DB Exception
        }
        finally
        {
            Database.closeConnection(con);
        }

    }

    public void updateBinDetails(CommonValidatorVO commonValidatorVO)
    {
        Connection con =null;
        Functions functions                 = new Functions();
        PaymentManager paymentManager       = new PaymentManager();
        String subCardType                  = "Consumer Card";
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = null;
        GenericTransDetailsVO genericTransDetailsVO = null;
        if (commonValidatorVO.getAddressDetailsVO() != null)
        {
            genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        }
        else
        {
            genericAddressDetailsVO = new GenericAddressDetailsVO();
        }
        if (commonValidatorVO.getTransDetailsVO() != null)
        {
            genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        }
        else
        {
            genericTransDetailsVO = new GenericTransDetailsVO();
        }
        String firstSix = "";
        String lastfour  = "";
        try
        {

            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                subCardType = paymentManager.getSubCardType(commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6));
            }

            if(merchantDetailsVO.getIsCardStorageRequired().equalsIgnoreCase("N") && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum())){
                firstSix  =  "******";
                lastfour  =  commonValidatorVO.getCardDetailsVO().getCardNum().substring(commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4);
            }else{
                if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
                {
                    firstSix =  commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                    lastfour =  commonValidatorVO.getCardDetailsVO().getCardNum().substring(commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4);
                }
            }
            con             = Database.getConnection();
            String qry      ="insert into bin_details (icicitransid, first_six, last_four, accountid,emailaddr,boiledname,subcard_type, bin_brand, bin_sub_brand, bin_card_type, bin_card_category,bin_usage_type,bin_country_code_A3,bin_trans_type,bin_country_code_A2,country_name,issuing_bank,customer_phone,customer_id,merchant_id,customer_ip,customer_ipCountry,customer_country,trans_currency,trans_amount,customer_email,trans_status,trans_dtstamp) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))";

            PreparedStatement pstmt =con.prepareStatement(qry);
            pstmt.setString(1,commonValidatorVO.getTrackingid());

            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
               /* pstmt.setString(2, commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6));
                pstmt.setString(3, commonValidatorVO.getCardDetailsVO().getCardNum().substring(commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4));
                */
                pstmt.setString(2, firstSix);
                pstmt.setString(3, lastfour);
            }
            else
            {
                pstmt.setString(2, "");
                pstmt.setString(3, "");
            }
            pstmt.setString(4,commonValidatorVO.getMerchantDetailsVO().getAccountId());
            pstmt.setString(5,commonValidatorVO.getAddressDetailsVO().getEmail());
            pstmt.setString(6,commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
            pstmt.setString(7,subCardType);
            transactionLogger.error("commonValidatorVO.getCardDetailsVO().getBin_brand()----------->"+commonValidatorVO.getCardDetailsVO().getBin_brand());
            pstmt.setString(8,commonValidatorVO.getCardDetailsVO().getBin_brand());
            pstmt.setString(9,commonValidatorVO.getCardDetailsVO().getBin_sub_brand());
            pstmt.setString(10,commonValidatorVO.getCardDetailsVO().getBin_card_type());
            pstmt.setString(11,commonValidatorVO.getCardDetailsVO().getBin_card_category());
            pstmt.setString(12,commonValidatorVO.getCardDetailsVO().getBin_usage_type());
            pstmt.setString(13,commonValidatorVO.getCardDetailsVO().getCountry_code_A3());
            pstmt.setString(14,commonValidatorVO.getCardDetailsVO().getTrans_type());
            pstmt.setString(15,commonValidatorVO.getCardDetailsVO().getCountry_code_A2());
            pstmt.setString(16,commonValidatorVO.getCardDetailsVO().getCountryName());
            pstmt.setString(17,commonValidatorVO.getCardDetailsVO().getIssuingBank());


            if (functions.isValueNull(genericAddressDetailsVO.getPhone()))
            {
                pstmt.setString(18, genericAddressDetailsVO.getPhone());
            }
            else
            {
                pstmt.setString(18, "");
            }
            if (functions.isValueNull(commonValidatorVO.getCustomerId()))
            {
                pstmt.setString(19, commonValidatorVO.getCustomerId());
            }
            else
            {
                pstmt.setString(19, "");
            }
            if (functions.isValueNull(merchantDetailsVO.getMemberId()))
            {
                pstmt.setString(20, merchantDetailsVO.getMemberId());
            }
            else
            {
                pstmt.setString(20, "");
            }
            if (functions.isValueNull(genericAddressDetailsVO.getIp()))
            {
                pstmt.setString(21, genericAddressDetailsVO.getIp());
            }
            else
            {
                pstmt.setString(21, "");
            }
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
            {
                pstmt.setString(22, functions.getIPCountryShort(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()));
            }
            else
            {
                pstmt.setString(22, "");
            }
            if (functions.isValueNull(genericAddressDetailsVO.getCountry()))
            {
                pstmt.setString(23, genericAddressDetailsVO.getCountry());
            }
            else
            {
                pstmt.setString(23, "");
            }
            if (functions.isValueNull(genericTransDetailsVO.getCurrency()))
            {
                pstmt.setString(24, genericTransDetailsVO.getCurrency());
            }
            else
            {
                pstmt.setString(24, "");
            }
            if (functions.isValueNull(genericTransDetailsVO.getAmount()))
            {
                pstmt.setString(25, genericTransDetailsVO.getAmount());
            }
            else
            {
                pstmt.setString(25, "");
            }
            if (functions.isValueNull(genericAddressDetailsVO.getEmail()))
            {
                pstmt.setString(26, genericAddressDetailsVO.getEmail());
            }
            else
            {
                pstmt.setString(26, "");
            }
            pstmt.setString(27, "authstarted");

            pstmt.executeUpdate();
        }
        catch (SQLException systemError)
        {   logger.error("Sql Exception :::::",systemError);

            //ToDO raise and handle DB Exception

        }
        catch (SystemError systemError)
        {   logger.error("System Error ",systemError);
            //ToDO raise and handle DB Exception
        }
        finally
        {
            Database.closeConnection(con);
        }

    }


    /**
     *
     * @param errorNumber
     * @return
     * @throws SystemError
     */
    public String  getReason(String errorNumber)throws SystemError
    {
        Connection con = null;
        ResultSet rs = null;
        String reason ="";
        try
        {
            con = Database.getConnection();
            String q1="select reason from rs_codes where code=?";
            PreparedStatement p9=con.prepareStatement(q1);
            p9.setString(1,errorNumber);
            rs = p9.executeQuery();

            if(rs.next())
            {
                reason=rs.getString("reason");
            }
        }
        catch (SQLException e)
        {
            logger.error("SQLEXCEPTION",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return reason;
    }

    public Hashtable getMidKeyForQwipi(String accountId)
    {
        Connection con = null;
        Hashtable qwipiMidDetails = new Hashtable();
        try
        {
            con = Database.getConnection();

            String q1 = "select midkey,isksnurl from gateway_accounts_qwipi where accountid=?";
            PreparedStatement p8 = con.prepareStatement(q1);
            p8.setString(1, accountId);
            ResultSet rs = p8.executeQuery();
            if (rs.next())
            {
                qwipiMidDetails.put("midkey",rs.getString("midkey").trim());
                qwipiMidDetails.put("isksnurlflag",rs.getString("isksnurl"));
            }
        }
        catch (SQLException e)
        {
            logger.error("midkey is missing", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("Transaction.java","getMidKeyForQwipi()",null,"common","SQLException Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,null);
        }
        catch (SystemError se)
        {
            logger.error("midkey is missing", se);
            PZExceptionHandler.raiseAndHandleGenericViolationException("Transaction.java", "getMidKeyForQwipi()", null, "common", "SQLException Thrown:::", null,se.getMessage(), se.getCause(), null, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return qwipiMidDetails;
    }

    public Hashtable getQwipiTransDetails(String toid,String trackigid,String description) throws PZDBViolationException
    {
        Hashtable orderdata=new Hashtable();
        ResultSet rs2 = null;
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query="select redirecturl,ccnum,totype,fromid,amount,currency,redirecturl,emailaddr,accountid,captureamount from transaction_qwipi where trackingid=? and toid=? and description=?";
            PreparedStatement p=con.prepareStatement(query);
            p.setString(1,trackigid);
            p.setString(2,toid);
            p.setString(3,description);
            rs2 =p.executeQuery();
            if(rs2.next())
            {
                orderdata.put("accountid",rs2.getString("accountid"));
                orderdata.put("redirecturl",rs2.getString("redirecturl"));
                orderdata.put("ccnum",rs2.getString("ccnum"));
                orderdata.put("totype",rs2.getString("totype"));
                orderdata.put("amount",rs2.getString("amount"));
                orderdata.put("captureamount",rs2.getString("captureamount"));
            }
            else
            {

            }
        }
        catch (SystemError se)
        {
            logger.error("System Error",se);
            PZExceptionHandler.raiseDBViolationException("Transaction.java","getQwipiTransDetails()",null,"common","SQL Exception Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException s)
        {
            logger.error("SQLException",s);
            PZExceptionHandler.raiseDBViolationException("Transaction.java","getQwipiTransDetails()",null,"common","SQL Exception Thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,s.getMessage(),s.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return orderdata;
    }

    public Hashtable getTransactionDetailsForQwipi(String trackingId) throws SQLException
    {
        Hashtable transaDetails = new Hashtable();
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String sql = "select amount,toid,fromid,description,orderdescription,dtstamp,redirecturl,ccnum,totype,captureamount,currency,amount,status,httpheader,accountId,timestamp from transaction_qwipi where trackingid=?";
            PreparedStatement p1=con.prepareStatement(sql);
            p1.setString(1,trackingId);
            ResultSet rs = p1.executeQuery();
            if(rs.next())
            {
                transaDetails.put("toid",String.valueOf(rs.getInt("toid"))) ;
                transaDetails.put("amount",rs.getString("amount")) ;
                transaDetails.put("accountid",rs.getString("accountid")) ;
                transaDetails.put("fromid",rs.getString("fromid")) ;
                transaDetails.put("description",rs.getString("description"));
                transaDetails.put("orderdescription",rs.getString("orderdescription"));
                transaDetails.put("dtstamp",rs.getString("dtstamp"));
                transaDetails.put("currency",rs.getString("currency"));
                transaDetails.put("amount",rs.getString("amount"));
                transaDetails.put("status",rs.getString("status"));

                //added for single call

                transaDetails.put("redirecturl",rs.getString("redirecturl"));
                transaDetails.put("ccnum",rs.getString("ccnum"));
                transaDetails.put("totype",rs.getString("totype"));
                transaDetails.put("captureamount",rs.getString("captureamount"));
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transaDetails;
    }

    public Hashtable getTransactionDetailsForCommon(String trackingId)
    {
        Hashtable transaDetails = new Hashtable();
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String sql = "select amount,paymentid,emailaddr,toid,fromid,description,orderdescription,dtstamp,status,httpheader,templatecurrency,templateamount,redirecturl,ccnum,name,accountId,currency,ipaddress from transaction_common where trackingid =?";
            PreparedStatement p1=con.prepareStatement(sql);
            p1.setString(1,trackingId);
            ResultSet rs = p1.executeQuery();

            if (rs.next())
            {
                transaDetails.put("toid",String.valueOf(rs.getInt("toid")));
                transaDetails.put("amount",rs.getString("amount"));
                transaDetails.put("accountid",rs.getString("accountid"));
                transaDetails.put("fromid",rs.getString("fromid"));
                if(rs.getString("paymentid") ==null)
                    transaDetails.put("paymentid","");
                else
                    transaDetails.put("paymentid",rs.getString("paymentid"));
                transaDetails.put("description",rs.getString("description"));
                transaDetails.put("orderdescription",rs.getString("orderdescription"));
                transaDetails.put("dtstamp",rs.getString("dtstamp"));
                transaDetails.put("httpheader",rs.getString("httpheader"));
                if(rs.getString("templatecurrency") ==null)
                    transaDetails.put("templatecurrency","");
                else
                    transaDetails.put("templatecurrency",rs.getString("templatecurrency"));
                transaDetails.put("templateamount",rs.getString("templateamount"));
                transaDetails.put("status",rs.getString("status"));
                transaDetails.put("emailaddr",rs.getString("emailaddr"));
                if(rs.getString("name") ==null)
                    transaDetails.put("name","");
                else
                    transaDetails.put("name",rs.getString("name"));
                if(PzEncryptor.decryptPAN(rs.getString("ccnum")) == null)
                    transaDetails.put("ccnum","");
                else
                    transaDetails.put("ccnum", PzEncryptor.decryptPAN(rs.getString("ccnum")));
                transaDetails.put("currency", PzEncryptor.decryptPAN(rs.getString("currency")));
                transaDetails.put("redirecturl",rs.getString("redirecturl"));
                if(rs.getString("ipaddress") ==null)
                    transaDetails.put("ipaddress","");
                else
                    transaDetails.put("ipaddress",rs.getString("ipaddress"));

            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transaDetails;
    }

    public Hashtable getTransactionDetailsForCommon(String trackingId,String description)
    {
        Hashtable transaDetails = new Hashtable();
        Connection con = null;
        Functions functions = new Functions();
        try
        {
            StringBuffer sb = new StringBuffer();
            con = Database.getConnection();
            String sFields = "amount,paymentid,emailaddr,toid,fromid,description,orderdescription,dtstamp,status,httpheader,templatecurrency,templateamount,redirecturl,ccnum,name,accountId,currency,ipaddress,remark ";

            sb.append("select "+sFields+" from transaction_common where ");

            if(functions.isValueNull(trackingId) && functions.isValueNull(description))
            {
                sb.append("trackingid="+trackingId+" AND description='"+description+"'");
            }
            if(functions.isValueNull(trackingId) && !functions.isValueNull(description))
            {
                sb.append("trackingid="+trackingId);
            }
            else if(!functions.isValueNull(trackingId) && functions.isValueNull(description))
            {
                sb.append("description='"+description+"'");
            }

            PreparedStatement p1=con.prepareStatement(sb.toString());
            logger.debug("cancel query---"+p1);

            ResultSet rs = p1.executeQuery();

            if (rs.next())
            {
                transaDetails.put("toid",String.valueOf(rs.getInt("toid")));
                transaDetails.put("amount",rs.getString("amount"));
                if (rs.getString("accountid") != null)
                    transaDetails.put("accountid",rs.getString("accountid"));
                if(rs.getString("fromid") !=null)
                    transaDetails.put("fromid",rs.getString("fromid"));
                if(rs.getString("paymentid") ==null)
                    transaDetails.put("paymentid","");
                else
                    transaDetails.put("paymentid",rs.getString("paymentid"));
                transaDetails.put("description",rs.getString("description"));
                transaDetails.put("orderdescription",rs.getString("orderdescription"));
                transaDetails.put("dtstamp",rs.getString("dtstamp"));
                transaDetails.put("httpheader",rs.getString("httpheader"));
                if(rs.getString("templatecurrency") ==null)
                    transaDetails.put("templatecurrency","");
                else
                    transaDetails.put("templatecurrency",rs.getString("templatecurrency"));
                if (rs.getString("templateamount") == null)
                    transaDetails.put("templateamount","");
                else
                    transaDetails.put("templateamount",rs.getString("templateamount"));
                transaDetails.put("status",rs.getString("status"));
                transaDetails.put("emailaddr",rs.getString("emailaddr"));
                if(rs.getString("name") ==null)
                    transaDetails.put("name","");
                else
                    transaDetails.put("name",rs.getString("name"));
                if(PzEncryptor.decryptPAN(rs.getString("ccnum")) == null)
                    transaDetails.put("ccnum","");
                else
                    transaDetails.put("ccnum", PzEncryptor.decryptPAN(rs.getString("ccnum")));
                transaDetails.put("currency", PzEncryptor.decryptPAN(rs.getString("currency")));
                transaDetails.put("redirecturl",rs.getString("redirecturl"));
                if(rs.getString("ipaddress") ==null)
                    transaDetails.put("ipaddress","");
                else
                    transaDetails.put("ipaddress",rs.getString("ipaddress"));

                if (rs.getString("remark") == null)
                    transaDetails.put("remark","");
                else
                    transaDetails.put("remark",rs.getString("remark"));

            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transaDetails;
    }


    public Hashtable getTransactionDetailsForCommonforWS(String trackingId,String description)
    {
        Hashtable transaDetails = new Hashtable();
        Connection con = null;
        Functions functions = new Functions();
        int count = 0;
        try
        {
            StringBuffer sb = new StringBuffer();
            con = Database.getConnection();
            String sFields = "amount,paymentid,emailaddr,toid,fromid,description,orderdescription,dtstamp,status,httpheader,templatecurrency,templateamount,redirecturl,ccnum,name,accountId,currency,ipaddress ";

            sb.append("select "+sFields+" from transaction_common where ");

            if(functions.isValueNull(trackingId) && functions.isValueNull(description))
            {
                sb.append("trackingid="+trackingId+" AND description like '"+description+"%'");
            }
            if(functions.isValueNull(trackingId) && !functions.isValueNull(description))
            {
                sb.append("trackingid="+trackingId);
            }
            else if(!functions.isValueNull(trackingId) && functions.isValueNull(description))
            {
                sb.append("description like '"+description+"%'");
            }

            PreparedStatement p1=con.prepareStatement(sb.toString());

            ResultSet rs = p1.executeQuery();

            while (rs.next())
            {
                transaDetails.put("toid",String.valueOf(rs.getInt("toid")));
                transaDetails.put("amount",rs.getString("amount"));
                transaDetails.put("accountid",rs.getString("accountid"));
                transaDetails.put("fromid",rs.getString("fromid"));
                if(rs.getString("paymentid") ==null)
                    transaDetails.put("paymentid","");
                else
                    transaDetails.put("paymentid",rs.getString("paymentid"));
                transaDetails.put("description",rs.getString("description"));
                transaDetails.put("orderdescription",rs.getString("orderdescription"));
                transaDetails.put("dtstamp",rs.getString("dtstamp"));
                transaDetails.put("httpheader",rs.getString("httpheader"));
                if(rs.getString("templatecurrency") ==null)
                    transaDetails.put("templatecurrency","");
                else
                    transaDetails.put("templatecurrency",rs.getString("templatecurrency"));
                transaDetails.put("templateamount",rs.getString("templateamount"));
                transaDetails.put("status",rs.getString("status"));
                transaDetails.put("emailaddr",rs.getString("emailaddr"));
                if(rs.getString("name") ==null)
                    transaDetails.put("name","");
                else
                    transaDetails.put("name",rs.getString("name"));
                if(PzEncryptor.decryptPAN(rs.getString("ccnum")) == null)
                    transaDetails.put("ccnum","");
                else
                    transaDetails.put("ccnum", PzEncryptor.decryptPAN(rs.getString("ccnum")));
                transaDetails.put("currency", PzEncryptor.decryptPAN(rs.getString("currency")));
                transaDetails.put("redirecturl",rs.getString("redirecturl"));
                if(rs.getString("ipaddress") ==null)
                    transaDetails.put("ipaddress","");
                else
                    transaDetails.put("ipaddress",rs.getString("ipaddress"));
                count++;
            }
            logger.debug("get status---"+sb.toString());
            //System.out.println("count---"+count);
            transaDetails.put("count",count);
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transaDetails;
    }

    public Hashtable getAuthTransactionCommon(String memberid,String trackingId) throws PZDBViolationException
    {

        Connection conn = null;
        Hashtable authHash = new Hashtable();
        try
        {
            conn            = Database.getConnection();
            String query    = "select T.amount, T.accountid,T.currency,M.company_name,M.contact_emails,M.currency,M.taxper,M.reversalcharge, M.isExcessCaptureAllowed,T.notificationUrl from transaction_common as T,members as M where toid=? and T.toid=M.memberid and trackingid = ? and status='authsuccessful'  order by trackingid asc";
            PreparedStatement authTransPreparedStatement = conn.prepareStatement(query);
            logger.debug("query in capture---"+authTransPreparedStatement);

            authTransPreparedStatement.setString(1, memberid);
            authTransPreparedStatement.setString(2, trackingId);

            logger.debug("Transactions authTransPreparedStatement---"+authTransPreparedStatement);

            ResultSet rsAuthtransaction = authTransPreparedStatement.executeQuery();
            if (rsAuthtransaction.next())
            {

                authHash.put("trackingid", trackingId);
                authHash.put("amount",rsAuthtransaction.getString("amount"));
                authHash.put("accountid",rsAuthtransaction.getString("accountid"));
                authHash.put("isExcessCaptureAllowed",rsAuthtransaction.getString("isExcessCaptureAllowed"));
                authHash.put("currency",rsAuthtransaction.getString("currency"));
                if(rsAuthtransaction.getString("notificationUrl") != null){
                    authHash.put("notificationUrl",rsAuthtransaction.getString("notificationUrl"));
                }else{
                    authHash.put("notificationUrl","");
                }

            }
        }
        catch (SQLException e)
        {
            logger.error("SQLException----",e);
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "getAuthTransactionCommon()", null, "common", "SQL Exception Thrown:::::", PZDBExceptionEnum.INCORRECT_QUERY,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error("SystemError:::::",se);
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "getAuthTransactionCommon()", null, "common", "SQL Exception Thrown:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return authHash;
    }

    public Hashtable getAuthTransactionCommon(String requestId,String trackingId,String flightMode) throws PZDBViolationException
    {

        Connection conn = null;
        Hashtable authHash = new Hashtable();
        try
        {
            conn = Database.getConnection();
            String query = "";
            if("Y".equalsIgnoreCase(flightMode))
            {
                query = "select T.trackingid,T.amount,T.accountid,T.toid,M.isExcessCaptureAllowed from transaction_common as T,members as M where totype=(SELECT partnerName FROM partners WHERE partnerId=?) and T.toid=M.memberid and trackingid = ? and status='authsuccessful'  order by trackingid asc";
            }
            else
            {
                query = "select T.trackingid,T.amount,T.accountid,T.toid,M.isExcessCaptureAllowed from transaction_common as T,members as M where toid=? and T.toid=M.memberid and trackingid = ? and status='authsuccessful'  order by trackingid asc";
            }


            PreparedStatement authTransPreparedStatement = conn.prepareStatement(query);
            logger.debug("query in capture---"+authTransPreparedStatement);

            authTransPreparedStatement.setString(1, requestId);
            authTransPreparedStatement.setString(2, trackingId);

            logger.debug("Transactions authTransPreparedStatement---"+authTransPreparedStatement);

            ResultSet rsAuthtransaction = authTransPreparedStatement.executeQuery();
            if (rsAuthtransaction.next())
            {

                authHash.put("trackingid", rsAuthtransaction.getString("trackingid"));
                authHash.put("amount",rsAuthtransaction.getString("amount"));
                authHash.put("accountid",rsAuthtransaction.getString("accountid"));
                authHash.put("isExcessCaptureAllowed",rsAuthtransaction.getString("isExcessCaptureAllowed"));
                authHash.put("toid",rsAuthtransaction.getString("toid"));

            }
        }
        catch (SQLException e)
        {
            //logger.error(":::::"+e.getMessage());
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "getAuthTransactionCommon()", null, "common", "SQL Exception Thrown:::::", PZDBExceptionEnum.INCORRECT_QUERY,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            //logger.error(":::::"+se.getMessage());
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "getAuthTransactionCommon()", null, "common", "SQL Exception Thrown:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return authHash;
    }

    public Hashtable getCaptureTransactionCommon(String trackingId, String memberId) throws PZDBViolationException
    {
        Connection conn     = null;
        Hashtable transHash = new Hashtable();
        try
        {
            conn            = Database.getConnection();
            String query    = "select T.accountid,T.amount, T.refundamount, T.captureamount ,FROM_UNIXTIME(T.dtstamp) as transactiondate,T.currency, T.status,T.notificationUrl from transaction_common as T,members as M where T.trackingid=? and T.toid=M.memberid and toid=? and status IN('settled','capturesuccess','reversed') and T.captureamount>T.refundamount";
            PreparedStatement authTransPreparedStatement = conn.prepareStatement(query);

            authTransPreparedStatement.setString(1,trackingId);
            authTransPreparedStatement.setString(2, memberId);
            if (isLogEnabled)
                logger.debug("query in refund-----"+authTransPreparedStatement);

            ResultSet rstransaction = authTransPreparedStatement.executeQuery();
            if (rstransaction.next())
            {

                transHash.put("trackingid", trackingId);
                transHash.put("amount",rstransaction.getString("amount"));
                transHash.put("accountid",rstransaction.getString("accountid"));
                transHash.put("transactiondate", rstransaction.getString("transactiondate"));
                transHash.put("transactionstatus", rstransaction.getString("status"));
                transHash.put("currency", rstransaction.getString("currency"));
                transHash.put("refundamount", rstransaction.getString("refundamount"));
                transHash.put("captureamount", rstransaction.getString("captureamount"));
                transHash.put("status", rstransaction.getString("status"));

                if(rstransaction.getString("notificationUrl") != null){
                    transHash.put("notificationUrl",rstransaction.getString("notificationUrl"));
                }else{
                    transHash.put("notificationUrl","");
                }

            }
        }
        catch (SQLException e)
        {
            logger.error(":::::",e);
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "getCaptureTransactionCommon()", null, "common", "SQL Exception Thrown:::::", PZDBExceptionEnum.INCORRECT_QUERY,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error(":::::",se);
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "getCaptureTransactionCommon()", null, "common", "SQL Exception Thrown:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }

        finally
        {
            Database.closeConnection(conn);
        }

        return transHash;
    }

    public Hashtable getTransactionDetailsForCommon(String trackingId,String description,String toid)
    {
        Hashtable transaDetails = new Hashtable();
        Connection con = null;
        Functions functions = new Functions();
        try
        {
            StringBuffer sb = new StringBuffer();
            con = Database.getConnection();
            String sFields = "amount,paymentid,toid,emailaddr,fromid,description,orderdescription,dtstamp,status,httpheader,templatecurrency,templateamount,redirecturl,ccnum,name,accountId,currency,ipaddress, terminalid ";

            sb.append("select "+sFields+" from transaction_common where toid ="+toid);

            if(functions.isValueNull(trackingId) && functions.isValueNull(description))
            {
                sb.append(" AND trackingid="+trackingId+" AND description='"+description+"'");
            }
            if(functions.isValueNull(trackingId) && !functions.isValueNull(description))
            {
                sb.append(" AND trackingid="+trackingId);
            }
            else if(!functions.isValueNull(trackingId) && functions.isValueNull(description))
            {
                sb.append(" AND description='"+description+"'");
            }

            PreparedStatement p1=con.prepareStatement(sb.toString());
            logger.debug("cancel query---"+p1);

            ResultSet rs = p1.executeQuery();

            if (rs.next())
            {
                transaDetails.put("toid",String.valueOf(rs.getInt("toid")));
                transaDetails.put("amount",rs.getString("amount"));
                transaDetails.put("accountid",rs.getString("accountid"));
                transaDetails.put("fromid",rs.getString("fromid"));
                if(rs.getString("paymentid") ==null)
                    transaDetails.put("paymentid","");
                else
                    transaDetails.put("paymentid",rs.getString("paymentid"));
                transaDetails.put("description",rs.getString("description"));
                if(rs.getString("orderdescription")==null)
                    transaDetails.put("orderdescription","");
                else
                    transaDetails.put("orderdescription",rs.getString("orderdescription"));
                transaDetails.put("dtstamp",rs.getString("dtstamp"));
                transaDetails.put("httpheader",rs.getString("httpheader"));
                if(rs.getString("templatecurrency") ==null)
                    transaDetails.put("templatecurrency","");
                else
                    transaDetails.put("templatecurrency",rs.getString("templatecurrency"));
                if(!functions.isValueNull(rs.getString("templateamount")))
                    transaDetails.put("templateamount","");
                else
                    transaDetails.put("templateamount",rs.getString("templateamount"));
                transaDetails.put("status",rs.getString("status"));
                if(!functions.isValueNull(rs.getString("emailaddr")))
                    transaDetails.put("emailaddr","");
                else
                    transaDetails.put("emailaddr",rs.getString("emailaddr"));
                if(rs.getString("name") ==null)
                    transaDetails.put("name","");
                else
                    transaDetails.put("name",rs.getString("name"));
                if(PzEncryptor.decryptPAN(rs.getString("ccnum")) == null)
                    transaDetails.put("ccnum","");
                else
                    transaDetails.put("ccnum", PzEncryptor.decryptPAN(rs.getString("ccnum")));
                transaDetails.put("currency",rs.getString("currency"));
                transaDetails.put("redirecturl",rs.getString("redirecturl"));
                if(rs.getString("ipaddress") ==null)
                    transaDetails.put("ipaddress","");
                else
                    transaDetails.put("ipaddress",rs.getString("ipaddress"));
                transaDetails.put("terminalid",rs.getString("terminalid"));

            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transaDetails;
    }

    public Hashtable getTransactionDetailsForCommon(String trackingId,String description,String requestId,String flightMode)
    {
        Hashtable transaDetails = new Hashtable();
        Connection con = null;
        Functions functions = new Functions();
        try
        {
            StringBuffer sb = new StringBuffer();
            con = Database.getConnection();
            String sFields = "amount,paymentid,toid,emailaddr,fromid,description,orderdescription,dtstamp,status,httpheader,templatecurrency,templateamount,redirecturl,ccnum,name,accountId,currency,ipaddress ";

            if("Y".equalsIgnoreCase(flightMode))
            {
                sb.append("select "+sFields+" from transaction_common where totype=(SELECT partnerName FROM partners WHERE partnerId="+requestId+") and status='authsuccessful' ");
            }
            else
            {
                sb.append("select "+sFields+" from transaction_common where toid ="+requestId+" and status='authsuccessful'" );
            }

            if(functions.isValueNull(trackingId) && functions.isValueNull(description))
            {
                sb.append(" AND trackingid="+trackingId+" AND description='"+description+"'");
            }
            if(functions.isValueNull(trackingId) && !functions.isValueNull(description))
            {
                sb.append(" AND trackingid="+trackingId);
            }
            else if(!functions.isValueNull(trackingId) && functions.isValueNull(description))
            {
                sb.append(" AND description='"+description+"'");
            }

            PreparedStatement p1=con.prepareStatement(sb.toString());
            logger.debug("cancel query---"+p1);

            ResultSet rs = p1.executeQuery();

            if (rs.next())
            {
                transaDetails.put("toid",String.valueOf(rs.getInt("toid")));
                transaDetails.put("amount",rs.getString("amount"));
                transaDetails.put("accountid",rs.getString("accountid"));
                transaDetails.put("fromid",rs.getString("fromid"));
                if(rs.getString("paymentid") ==null)
                    transaDetails.put("paymentid","");
                else
                    transaDetails.put("paymentid",rs.getString("paymentid"));
                transaDetails.put("description",rs.getString("description"));
                transaDetails.put("orderdescription",rs.getString("orderdescription"));
                transaDetails.put("dtstamp",rs.getString("dtstamp"));
                transaDetails.put("httpheader",rs.getString("httpheader"));
                if(rs.getString("templatecurrency") ==null)
                    transaDetails.put("templatecurrency","");
                else
                    transaDetails.put("templatecurrency",rs.getString("templatecurrency"));
                transaDetails.put("templateamount",rs.getString("templateamount"));
                transaDetails.put("status",rs.getString("status"));
                transaDetails.put("emailaddr",rs.getString("emailaddr"));
                if(rs.getString("name") ==null)
                    transaDetails.put("name","");
                else
                    transaDetails.put("name",rs.getString("name"));
                if(PzEncryptor.decryptPAN(rs.getString("ccnum")) == null)
                    transaDetails.put("ccnum","");
                else
                    transaDetails.put("ccnum", PzEncryptor.decryptPAN(rs.getString("ccnum")));
                transaDetails.put("currency", PzEncryptor.decryptPAN(rs.getString("currency")));
                transaDetails.put("redirecturl",rs.getString("redirecturl"));
                if(rs.getString("ipaddress") ==null)
                    transaDetails.put("ipaddress","");
                else
                    transaDetails.put("ipaddress",rs.getString("ipaddress"));

            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transaDetails;
    }


    public Hashtable getCaptureTransactionCommon(String trackingId, String requestId,String flightMode) throws PZDBViolationException
    {
        Connection conn = null;
        Hashtable transHash = new Hashtable();
        try
        {
            conn = Database.getConnection();
            String query = "";
            //String query = "select T.trackingid,T.amount,T.accountid,FROM_UNIXTIME(T.dtstamp) as transactiondate,M.company_name,M.contact_emails,M.currency,M.taxper,M.reversalcharge from transaction_common as T,members as M where T.trackingid=? and T.toid=M.memberid and toid=? and status IN('settled','capturesuccess')";
            if("Y".equalsIgnoreCase(flightMode))
            {
                query = "select trackingid,amount,accountid,FROM_UNIXTIME(dtstamp) as transactiondate,toid from transaction_common where trackingid=? and totype=(SELECT partnerName FROM partners WHERE partnerId=?) and status IN('settled','capturesuccess')";
            }

            else
            {
                query = "select T.*,FROM_UNIXTIME(T.dtstamp) as transactiondate,M.company_name,M.contact_emails,M.currency,M.taxper,M.reversalcharge from transaction_common as T,members as M where T.trackingid=? and T.toid=M.memberid and toid=? and status IN('settled','capturesuccess')";
            }
            PreparedStatement authTransPreparedStatement = conn.prepareStatement(query);
            authTransPreparedStatement.setString(1,trackingId);
            authTransPreparedStatement.setString(2, requestId);



            logger.debug("query in refund-----"+authTransPreparedStatement);

            ResultSet rstransaction = authTransPreparedStatement.executeQuery();
            if (rstransaction.next())
            {
                transHash.put("trackingid", rstransaction.getString("trackingid"));
                transHash.put("amount",rstransaction.getString("amount"));
                transHash.put("accountid",rstransaction.getString("accountid"));
                transHash.put("transactiondate", rstransaction.getString("transactiondate"));
                transHash.put("toid", rstransaction.getString("toid"));
            }
        }
        catch (SQLException e)
        {
            logger.error(":::::",e);
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "getCaptureTransactionCommon()", null, "common", "Internal Error occurs. Please contact admin:::", PZDBExceptionEnum.INCORRECT_QUERY,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error(":::::",se);
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "getCaptureTransactionCommon()", null, "common", "Internal Error occurs. Please contact admin:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }

        finally
        {
            Database.closeConnection(conn);
        }

        return transHash;
    }

    public Hashtable getCaptureTransactionQwipi(String trackingId, String memberId) throws PZDBViolationException
    {

        Connection conn = null;
        Hashtable transHash = new Hashtable();
        try
        {
            conn = Database.getConnection();
            String query = "select T.*,FROM_UNIXTIME(T.dtstamp) as transactiondate,M.company_name,M.contact_emails,M.currency,M.taxper,M.reversalcharge,T.fixamount from transaction_qwipi as T,members as M where T.trackingid=? and T.toid=M.memberid and toid=? and status IN('settled','capturesuccess')";
            PreparedStatement authTransPreparedStatement = conn.prepareStatement(query);

            authTransPreparedStatement.setString(1, trackingId);
            authTransPreparedStatement.setString(2, memberId);

            ResultSet rstransaction = authTransPreparedStatement.executeQuery();
            if (rstransaction.next())
            {

                transHash.put("trackingid",rstransaction.getString("trackingid"));
                transHash.put("transid", rstransaction.getString("transid"));
                transHash.put("accountid", rstransaction.getString("accountid"));
                transHash.put("fixamount", rstransaction.getString("fixamount"));
                transHash.put("qwipiPaymentOrderNumber", rstransaction.getString("qwipiPaymentOrderNumber"));
                transHash.put("description", rstransaction.getString("description"));
                transHash.put("amount", rstransaction.getString("amount"));
                transHash.put("dtstamp", rstransaction.getString("dtstamp"));
                transHash.put("fromid", rstransaction.getString("fromid"));
                transHash.put("toid", rstransaction.getString("toid"));
                transHash.put("name", rstransaction.getString("name"));
                transHash.put("chargeper", rstransaction.getInt("chargeper"));
                transHash.put("T.taxper", rstransaction.getInt("T.taxper"));
                transHash.put("T.taxper", rstransaction.getInt("M.taxper"));
                transHash.put("M.reversalcharge", rstransaction.getString("M.reversalcharge"));
                transHash.put("status", rstransaction.getString("status"));
                transHash.put("transactiondate", rstransaction.getString("transactiondate"));

            }
        }
        catch (SQLException e)
        {
            logger.error(":::::"+e.getMessage());
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "getCaptureTransactionQwipi()", null, "Common/directi/pg", "SQL Exception Thrown:::::", PZDBExceptionEnum.INCORRECT_QUERY,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error(":::::"+se.getMessage());
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "getCaptureTransactionQwipi()", null, "Common/directi/pg", "SQL Exception Thrown:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }

        finally
        {
            Database.closeConnection(conn);
        }

        return transHash;
    }

    public Hashtable getCaptureTransactionEcore(String trackingId, String memberId) throws PZDBViolationException
    {

        Connection conn = null;
        Hashtable transHash = new Hashtable();
        try
        {
            conn = Database.getConnection();
            String query = "select T.*,FROM_UNIXTIME(T.dtstamp) as transactiondate,TD.ecorePaymentOrderNumber, M.company_name,M.contact_emails,M.currency,M.taxper,M.reversalcharge,T.fixamount from transaction_ecore as T, transaction_ecore_details as TD,members as M where T.trackingid=? and T.trackingid=TD.parentid and T.toid=M.memberid and T.toid=? and TD.status='capturesuccess' and T.status IN('settled','capturesuccess')";
            PreparedStatement authTransPreparedStatement = conn.prepareStatement(query);

            authTransPreparedStatement.setString(1, trackingId);
            authTransPreparedStatement.setString(2, memberId);

            ResultSet rstransaction = authTransPreparedStatement.executeQuery();
            if (rstransaction.next())
            {

                transHash.put("amount",rstransaction.getString("amount"));
                transHash.put("dtstamp",rstransaction.getString("dtstamp"));
                transHash.put("accountid",rstransaction.getString("accountid"));
                transHash.put("status",rstransaction.getString("status"));
                transHash.put("transactiondate", rstransaction.getString("transactiondate"));

            }
        }
        catch (SQLException e)
        {
            logger.error(":::::"+e.getMessage());
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "getCaptureTransactionQwipi()", null, "Common/directi/pg", "SQL Exception Thrown:::::", PZDBExceptionEnum.INCORRECT_QUERY,null, e.getMessage(), e.getCause());
        }
        catch (SystemError se)
        {
            logger.error(":::::"+se.getMessage());
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "getCaptureTransactionQwipi()", null, "Common/directi/pg", "SQL Exception Thrown:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return transHash;
    }

    public String getEcorePaymentOrderNumber(String trackingId) throws PZDBViolationException
    {
        Connection conn = null;
        String transId = null;
        try
        {
            conn = Database.getConnection();
            String query = "select ecorePaymentOrderNumber from transaction_ecore_details where parentid= ? and status='capturesuccess'";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1,trackingId);
            ResultSet rs1 = pstmt.executeQuery();

            if (rs1.next())
            {
                transId = rs1.getString("ecorePaymentOrderNumber");
            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
            PZExceptionHandler.raiseDBViolationException("Transaction.java","getEcorePaymentOrderNumber()",null, "common", "SQL Exception Thrown:::::", PZDBExceptionEnum.SQL_EXCEPTION,null,se.getMessage(),se.getCause());
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
            PZExceptionHandler.raiseDBViolationException("Transaction.java","getEcorePaymentOrderNumber()",null, "common", "SQL Exception Thrown:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

        return transId;
    }

    public void updateVoucherId(String trackingid)
    {
        Connection conn = null;
        String transId = null;
        try
        {
            conn = Database.getConnection();
            String query = "update transaction_common set voucherid = null where trackingid=?" ;
            PreparedStatement p2=conn.prepareStatement(query);
            p2.setString(1,trackingid);
            p2.executeQuery();

        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public void updateTrnsactionStatusForMyMonedero(String captureAmount,String eWalletId,String remark,String trackingId,String status)
    {
        Connection conn = null;
        MyMonederoResponseVO mmresp=null;
        try
        {
            conn = Database.getConnection();
            String sql="update transaction_common set status=?, captureamount=? ,  ewalletid=?,remark=?  where trackingid=?";
            PreparedStatement ps=conn.prepareStatement(sql);

            ps.setString(1,status);
            ps.setString(2,captureAmount);
            ps.setString(3,mmresp.getSourceID());
            ps.setString(4,mmresp.getError());
            ps.setString(5,trackingId);
            logger.debug(ps.toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", e);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
        }
    }

    public String getTableNameFromTrackingid(String trackingId)
    {
        Transaction transaction = new Transaction();
        String accountId =  transaction.getAccountID(trackingId);
        GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
        String tableName = Database.getTableName(gatewayType.getGateway());
        return tableName;
    }

    public HashMap getTransactionDetailsFromTrackingID(String trackingid,String tableName) throws PZDBViolationException
    {
        Connection connection=null;
        HashMap detailhash=new HashMap();
        try
        {
            String sql="";
            connection=Database.getConnection();

            sql = "select amount,toid,fromid,description,orderdescription,dtstamp,redirecturl,ccnum,totype,captureamount,currency,amount,status,httpheader,accountId,timestamp from transaction_common where trackingid=?";

            /*if(tableName.equals("transaction_qwipi"))
            {
                sql = "select amount,toid,fromid,description,orderdescription,dtstamp,redirecturl,ccnum,totype,captureamount,currency,amount,status,httpheader,accountId,timestamp from transaction_qwipi where trackingid=?";
            }
            else if(tableName.equals("transaction_ecore"))
            {
                sql = "select amount,toid,fromid,description,orderdescription,dtstamp,redirecturl,ccnum,totype,captureamount,currency,amount,status,httpheader,accountId,timestamp from transaction_ecore where trackingid=?";
            }
            else
            {
                sql = "select amount,toid,fromid,description,orderdescription,dtstamp,redirecturl,ccnum,totype,captureamount,currency,amount,status,httpheader,accountId,timestamp from transaction_common where trackingid=?";
            }*/

            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,trackingid);
            ResultSet resultSet=preparedStatement.executeQuery();

            if(resultSet.next())
            {
                detailhash.put("toid", String.valueOf(resultSet.getInt("toid")));
                detailhash.put("amount", resultSet.getString("amount"));
                detailhash.put("accountid", resultSet.getString("accountid"));
                detailhash.put("fromid", resultSet.getString("fromid"));
                detailhash.put("description", resultSet.getString("description"));
                detailhash.put("orderdescription", resultSet.getString("orderdescription"));
                detailhash.put("dtstamp", resultSet.getString("dtstamp"));
                detailhash.put("currency", resultSet.getString("currency"));
                detailhash.put("amount", resultSet.getString("amount"));
                detailhash.put("status", resultSet.getString("status"));
                detailhash.put("redirecturl",resultSet.getString("redirecturl"));
                detailhash.put("ccnum",resultSet.getString("ccnum"));
                detailhash.put("totype",resultSet.getString("totype"));
                detailhash.put("captureamount",resultSet.getString("captureamount"));
            }
        }
        catch (SystemError se)
        {
            logger.error("SystemError:::::",se);
            PZExceptionHandler.raiseDBViolationException("Transaction.java","getTransactionDetailsFromTrackingID()",null, "Common/directi/pg", "SQL Exception Thrown:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::",e);
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "getTransactionDetailsFromTrackingID()", null, "Common/directi/pg", "SQL Exception Thrown:::::", PZDBExceptionEnum.INCORRECT_QUERY,null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return detailhash;
    }

    public String getGatewayTypeNameFromTrackingId(String trackingId)
    {
        String sQuery = "SELECT gateway FROM gateway_type WHERE pgtypeid = (SELECT pgtypeid FROM gateway_accounts WHERE accountid = (SELECT accountid FROM bin_details WHERE icicitransid=?))";
        Connection conn = null;
        ResultSet rsGatewayTypeName = null;
        String sGatewayType = null;
        try
        {
            conn = Database.getConnection();
            PreparedStatement pStmt = conn.prepareStatement(sQuery);
            pStmt.setString(1,trackingId);
            rsGatewayTypeName = pStmt.executeQuery();
            if(rsGatewayTypeName.next())
            {
                sGatewayType = rsGatewayTypeName.getString(1);
            }
        }
        catch (SQLException e)
        {
            logger.error("Error in getGatewayTypeNameFromTrackingId(String trackingId) Method of Transaction class:SQLException ::::::: Leaving Transaction = ", e);
        }
        catch (SystemError se)
        {
            logger.error("System Error = ", se);
        }
        return sGatewayType;
    }
    public void updateTransactionStatusForQwipi(String updatedstatus, String trackingId,String remark)throws PZDBViolationException
    {
        Connection conn = null;
        Statement stmt = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {
            conn = Database.getConnection();
            StringBuffer sb = new StringBuffer("update transaction_qwipi set");
            //sb.append(" status='"+updatedstatus+"'  where trackingid=" + ESAPI.encoder().encodeForSQL(me, trackingId));
            sb.append(" status='"+updatedstatus+"',"+"remark ='"+remark+"' where trackingid=" + ESAPI.encoder().encodeForSQL(me, trackingId));

            logger.debug("query::::"+sb.toString());

            stmt = conn.createStatement();
            stmt.executeUpdate(sb.toString());
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java","updateTransactionStatusForQwipi()",null,"common","SQL Exception thrown:::",PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException s)
        {
            PZExceptionHandler.raiseDBViolationException("Transaction.java", "updateTransactionStatusForQwipi()", null, "common", "SQL Exception thrown:::", PZDBExceptionEnum.INCORRECT_QUERY,null, s.getMessage(), s.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public TerminalVO getAccountIdTerminalVO(String toid, int paymenttype, int cardtype)
    {
        String accountId="0";
        Connection con = null;
        TerminalVO terminalVO=null;
        try
        {
            con=Database.getConnection();
            //String query2 = "select accountid,isActive,max_transaction_amount,min_transaction_amount,addressDetails,addressValidation,terminalid,cardDetailRequired,is_recurring,isManualRecurring,isPSTTerminal from member_account_mapping where memberid=? and paymodeid=? and cardtypeid=? and isActive='Y'";
            String query2 = "select gt.currency,mam.accountid,mam.isActive,mam.max_transaction_amount,mam.min_transaction_amount,mam.addressDetails,mam.addressValidation,mam.terminalid,mam.cardDetailRequired,mam.is_recurring,mam.isManualRecurring,mam.isPSTTerminal,mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc,gt.pgtypeid,mam.emi_support from member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt where mam.memberid=? and mam.paymodeid=? and mam.cardtypeid=? and mam.isActive='Y' AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid  AND gt.currency != 'ALL' AND mam.binRouting='N' ORDER BY mam.priority,mam.terminalid";
            PreparedStatement pstmt2 = con.prepareStatement(query2);
            pstmt2.setString(1, toid);
            pstmt2.setInt(2, paymenttype);
            pstmt2.setInt(3, cardtype);
            transactionLogger.debug("getAccountIdTerminalVO pstmt2----" + pstmt2);

            ResultSet rs2 = pstmt2.executeQuery();
            if (rs2.next())
            {
                terminalVO = new TerminalVO();
                accountId = rs2.getString("accountid");
                terminalVO.setMax_transaction_amount(rs2.getFloat("max_transaction_amount"));
                terminalVO.setMin_transaction_amount(rs2.getFloat("min_transaction_amount"));
                terminalVO.setTerminalId(rs2.getString("terminalid"));
                terminalVO.setIsActive(rs2.getString("isActive"));
                terminalVO.setAddressDetails(rs2.getString("addressDetails"));
                terminalVO.setAddressValidation(rs2.getString("addressValidation"));
                terminalVO.setCardDetailRequired(rs2.getString("cardDetailRequired"));
                terminalVO.setIsRecurring(rs2.getString("is_recurring"));
                terminalVO.setIsManualRecurring(rs2.getString("isManualRecurring"));
                terminalVO.setIsPSTTerminal(rs2.getString("isPSTTerminal"));
                terminalVO.setAccountId(accountId);
                terminalVO.setCurrency(rs2.getString("currency"));
                terminalVO.setAutoRedirectRequest(rs2.getString("autoRedirectRequest"));
                terminalVO.setReject3DCard(rs2.getString("reject3DCard"));
                terminalVO.setCurrencyConversion(rs2.getString("currency_conversion"));
                terminalVO.setConversionCurrency(rs2.getString("conversion_currency"));
                terminalVO.setIsCardWhitelisted(rs2.getString("isCardWhitelisted"));
                terminalVO.setIsEmailWhitelisted(rs2.getString("isEmailWhitelisted"));
                terminalVO.setCardLimitCheckTerminalLevel(rs2.getString("cardLimitCheck"));
                terminalVO.setCardAmountLimitCheckTerminalLevel(rs2.getString("cardAmountLimitCheck"));
                terminalVO.setAmountLimitCheckTerminalLevel(rs2.getString("amountLimitCheck"));
                terminalVO.setCardLimitCheckAccountLevel(rs2.getString("cardLimitAccountLevel"));
                terminalVO.setCardAmountLimitCheckAccountLevel(rs2.getString("cardAmountLimitCheckAcc"));
                terminalVO.setAmountLimitCheckAccountLevel(rs2.getString("amountLimitCheckAcc"));
                terminalVO.setGateway_id(rs2.getString("pgtypeid"));
                terminalVO.setIsEmi_support(rs2.getString("emi_support"));
            }
        }
        catch (Exception e)
        {
            logger.error("Exception occur", e);
        }
        finally
        {
            Database.closeConnection(con);
        }

        return terminalVO;
    }

    public TerminalVO getAccountIdTerminalVO(String toid, int paymenttype, int cardtype, String accId)
    {
        String accountId="0";
        Connection con = null;
        TerminalVO terminalVO=null;
        Functions functions=new Functions();
        try
        {
            con=Database.getConnection();
            //String query2 = "select accountid,isActive,max_transaction_amount,min_transaction_amount,addressDetails,addressValidation,terminalid,cardDetailRequired,is_recurring,isManualRecurring,isPSTTerminal from member_account_mapping where memberid=? and paymodeid=? and cardtypeid=? and isActive='Y'";
            StringBuffer query2 = new StringBuffer("select gt.currency,mam.accountid,mam.isActive,mam.max_transaction_amount,mam.min_transaction_amount,mam.addressDetails,mam.addressValidation,mam.terminalid,mam.cardDetailRequired,mam.is_recurring,mam.isManualRecurring,mam.isPSTTerminal,mam.autoRedirectRequest,mam.reject3DCard,mam.currency_conversion,mam.conversion_currency, mam.isCardWhitelisted, mam.isEmailWhitelisted, mam.cardLimitCheck, mam.cardAmountLimitCheck, mam.amountLimitCheck, ga.cardLimitCheck AS cardLimitAccountLevel, ga.cardAmountLimitCheckAcc, ga.amountLimitCheckAcc,gt.pgtypeid,mam.emi_support from member_account_mapping AS mam,gateway_accounts AS ga, gateway_type AS gt where mam.memberid=? and mam.paymodeid=? and mam.cardtypeid=? and mam.isActive='Y' AND mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid  AND gt.currency != 'ALL' AND mam.binRouting='N'");
            if(functions.isValueNull(accId))
                query2.append(" AND mam.accountid=?");
            query2.append(" ORDER BY mam.priority,mam.terminalid");
            PreparedStatement pstmt2 = con.prepareStatement(query2.toString());
            pstmt2.setString(1, toid);
            pstmt2.setInt(2, paymenttype);
            pstmt2.setInt(3, cardtype);
            if(functions.isValueNull(accId))
                pstmt2.setString(4,accId);
            transactionLogger.debug("getAccountIdTerminalVO pstmt2----" + pstmt2);

            ResultSet rs2 = pstmt2.executeQuery();
            if (rs2.next())
            {
                terminalVO = new TerminalVO();
                accountId = rs2.getString("accountid");
                terminalVO.setMax_transaction_amount(rs2.getFloat("max_transaction_amount"));
                terminalVO.setMin_transaction_amount(rs2.getFloat("min_transaction_amount"));
                terminalVO.setTerminalId(rs2.getString("terminalid"));
                terminalVO.setIsActive(rs2.getString("isActive"));
                terminalVO.setAddressDetails(rs2.getString("addressDetails"));
                terminalVO.setAddressValidation(rs2.getString("addressValidation"));
                terminalVO.setCardDetailRequired(rs2.getString("cardDetailRequired"));
                terminalVO.setIsRecurring(rs2.getString("is_recurring"));
                terminalVO.setIsManualRecurring(rs2.getString("isManualRecurring"));
                terminalVO.setIsPSTTerminal(rs2.getString("isPSTTerminal"));
                terminalVO.setAccountId(accountId);
                terminalVO.setCurrency(rs2.getString("currency"));
                terminalVO.setAutoRedirectRequest(rs2.getString("autoRedirectRequest"));
                terminalVO.setReject3DCard(rs2.getString("reject3DCard"));
                terminalVO.setCurrencyConversion(rs2.getString("currency_conversion"));
                terminalVO.setConversionCurrency(rs2.getString("conversion_currency"));
                terminalVO.setIsCardWhitelisted(rs2.getString("isCardWhitelisted"));
                terminalVO.setIsEmailWhitelisted(rs2.getString("isEmailWhitelisted"));
                terminalVO.setCardLimitCheckTerminalLevel(rs2.getString("cardLimitCheck"));
                terminalVO.setCardAmountLimitCheckTerminalLevel(rs2.getString("cardAmountLimitCheck"));
                terminalVO.setAmountLimitCheckTerminalLevel(rs2.getString("amountLimitCheck"));
                terminalVO.setCardLimitCheckAccountLevel(rs2.getString("cardLimitAccountLevel"));
                terminalVO.setCardAmountLimitCheckAccountLevel(rs2.getString("cardAmountLimitCheckAcc"));
                terminalVO.setAmountLimitCheckAccountLevel(rs2.getString("amountLimitCheckAcc"));
                terminalVO.setGateway_id(rs2.getString("pgtypeid"));
                terminalVO.setIsEmi_support(rs2.getString("emi_support"));
            }
        }
        catch (Exception e)
        {
            logger.error("Exception occur", e);
        }
        finally
        {
            Database.closeConnection(con);
        }

        return terminalVO;
    }

    public List<TransactionVO> getTransactionDetailsList(DirectInquiryValidatorVO directInquiryValidatorVO,String tablename)throws PZDBViolationException
    {
        Connection conn = null;
        Functions functions = new Functions();
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();
        TransactionVO transactionVO = null;
        List<TransactionVO> listTransactionVO = new ArrayList<TransactionVO>();

        try
        {
            conn = Database.getConnection();
            sb.append("select trackingid,amount,orderdescription,description,captureamount,refundamount,status from "+tablename+"");

            if("Y".equalsIgnoreCase(directInquiryValidatorVO.getMerchantDetailsVO().getSplitPaymentAllowed()))
            {
                if (functions.isValueNull(directInquiryValidatorVO.getTrackingid()) && functions.isValueNull(directInquiryValidatorVO.getDescription()))
                {
                    sb.append(" where trackingid in(" + directInquiryValidatorVO.getTrackingid() + ") AND description like '" + directInquiryValidatorVO.getDescription() + "%'");
                }
                else if (!functions.isValueNull(directInquiryValidatorVO.getTrackingid()) && functions.isValueNull(directInquiryValidatorVO.getDescription()))
                {
                    sb.append(" where description like '" + directInquiryValidatorVO.getDescription() + "%' ");
                }
                else if (functions.isValueNull(directInquiryValidatorVO.getTrackingid()) && !functions.isValueNull(directInquiryValidatorVO.getDescription()))
                {
                    sb.append(" where trackingid in(" + directInquiryValidatorVO.getTrackingid() +")");
                }
            }
            else
            {
                if (functions.isValueNull(directInquiryValidatorVO.getTrackingid()) && functions.isValueNull(directInquiryValidatorVO.getDescription()))
                {
                    sb.append(" where trackingid =" + directInquiryValidatorVO.getTrackingid() + " AND description ='" + directInquiryValidatorVO.getDescription() + "'");
                }
                else if (!functions.isValueNull(directInquiryValidatorVO.getTrackingid()) && functions.isValueNull(directInquiryValidatorVO.getDescription()))
                {
                    sb.append(" where description ='" + directInquiryValidatorVO.getDescription() + "' order by trackingid desc limit 1");
                }
                else if (functions.isValueNull(directInquiryValidatorVO.getTrackingid()) && !functions.isValueNull(directInquiryValidatorVO.getDescription()))
                {
                    sb.append(" where trackingid =" + directInquiryValidatorVO.getTrackingid());
                }
            }
            PreparedStatement ps = conn.prepareStatement(sb.toString());

            rs = ps.executeQuery();
            while (rs.next())
            {
                transactionVO = new TransactionVO();
                transactionVO.setTrackingId(rs.getString("trackingid"));
                transactionVO.setAmount(rs.getString("amount"));
                transactionVO.setOrderDesc(rs.getString("orderdescription"));
                transactionVO.setOrderId(rs.getString("description"));
                transactionVO.setCaptureAmount(rs.getDouble("captureamount"));
                transactionVO.setRefundAmount(rs.getString("refundamount"));
                transactionVO.setStatus(rs.getString("status"));
                listTransactionVO.add(transactionVO);
            }
            logger.debug("---status query---"+sb.toString());
        }
        catch (SystemError se)
        {
            logger.debug("exception---"+se);
            PZExceptionHandler.raiseDBViolationException("SingleCallGenericStatus","getTransactionDetails()",null,"Transaction",se.getMessage(), PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            logger.debug("exception---"+e);
            PZExceptionHandler.raiseDBViolationException("SingleCallGenericStatus","getTransactionDetails()",null,"Transaction",e.getMessage(), PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return listTransactionVO;
    }

    public Hashtable getTransactionDetails(String trackingId,String description,String tablename)throws PZDBViolationException
    {
        Hashtable transDetails = null;
        Connection conn = null;
        Functions functions = new Functions();
        ResultSet rs =null;
        StringBuffer sb = new StringBuffer();

        try
        {
            conn = Database.getConnection();

            sb.append("select trackingid,toid,amount,orderdescription,description,captureamount,refundamount,status,paymodeid,cardtypeid,ccnum,name,expdate,remark,accountid,emailaddr,templateamount,templatecurrency,firstname,lastname,currency,eci,payoutamount,fromtype,notificationUrl ,customerId from "+tablename+" where ");
            if(functions.isValueNull(trackingId) && functions.isValueNull(description))
            {
                sb.append("trackingid='"+trackingId+"' AND description ='"+description+"'");
            }
            else if(!functions.isValueNull(trackingId) && functions.isValueNull(description))
            {
                sb.append("description ='"+description+"' order by trackingid desc limit 1 ");
            }
            else if(functions.isValueNull(trackingId) && !functions.isValueNull(description))
            {
                sb.append("trackingid='"+trackingId+"'");
            }

            PreparedStatement ps = conn.prepareStatement(sb.toString());
            //logger.error("---status query ps---" + ps);
            rs = ps.executeQuery();
            if (rs.next())
            {
                transDetails = new Hashtable();
                transDetails.put("toid", rs.getString("toid"));
                transDetails.put("trackingid", rs.getString("trackingid"));
                transDetails.put("description",rs.getString("description"));

                if(functions.isValueNull(rs.getString("amount")))
                    transDetails.put("amount", rs.getString("amount"));
                else
                    transDetails.put("amount", "");

                if(functions.isValueNull(rs.getString("orderdescription")))
                    transDetails.put("orderdescription",rs.getString("orderdescription"));
                else
                    transDetails.put("orderdescription","");

                if(functions.isValueNull(rs.getString("captureamount")))
                    transDetails.put("captureamount", rs.getString("captureamount"));
                else
                    transDetails.put("captureamount", "");

                if(functions.isValueNull(rs.getString("refundamount")))
                    transDetails.put("refundamount",rs.getString("refundamount"));
                else
                    transDetails.put("refundamount", "");

                if(functions.isValueNull(rs.getString("fromtype")))
                    transDetails.put("fromtype",rs.getString("fromtype"));
                else
                    transDetails.put("fromtype", "");

                if(functions.isValueNull(rs.getString("paymodeid")))
                    transDetails.put("paymodeid",rs.getString("paymodeid"));
                else
                    transDetails.put("paymodeid", "");

                if(functions.isValueNull(rs.getString("cardtypeid")))
                    transDetails.put("cardtypeid",rs.getString("cardtypeid"));
                else
                    transDetails.put("cardtypeid","");
                if(functions.isValueNull(rs.getString("ccnum")))
                    transDetails.put("ccnum",rs.getString("ccnum"));
                else
                    transDetails.put("ccnum", "");
                if(functions.isValueNull(rs.getString("name")))
                    transDetails.put("name",rs.getString("name"));
                else
                    transDetails.put("name", "");
                if(functions.isValueNull(rs.getString("expdate")))
                    transDetails.put("expdate",rs.getString("expdate"));
                else
                    transDetails.put("expdate", "");
                if(functions.isValueNull(rs.getString("remark")))
                    transDetails.put("remark",rs.getString("remark"));
                else
                    transDetails.put("remark","");
                if(functions.isValueNull(rs.getString("accountid")))
                    transDetails.put("accountid",rs.getString("accountid"));
                else
                    transDetails.put("accountid","");
                if(functions.isValueNull(rs.getString("emailaddr")))
                    transDetails.put("emailaddr",rs.getString("emailaddr"));
                else
                    transDetails.put("emailaddr","");
                if(functions.isValueNull(rs.getString("templateamount")))
                    transDetails.put("templateamount",rs.getString("templateamount"));
                else
                    transDetails.put("templateamount","");
                if(functions.isValueNull(rs.getString("templatecurrency")))
                    transDetails.put("templatecurrency",rs.getString("templatecurrency"));
                else
                    transDetails.put("templatecurrency","");
                if(functions.isValueNull(rs.getString("firstname")))
                    transDetails.put("firstname",rs.getString("firstname"));
                else
                    transDetails.put("firstname", "");
                if(functions.isValueNull(rs.getString("lastname")))
                    transDetails.put("lastname",rs.getString("lastname"));
                else
                    transDetails.put("lastname", "");
                if(functions.isValueNull(rs.getString("currency")))
                    transDetails.put("currency",rs.getString("currency"));
                else
                    transDetails.put("currency", "");
                if(functions.isValueNull(rs.getString("eci")))
                    transDetails.put("eci",rs.getString("eci"));
                else
                    transDetails.put("eci", "");

                if(functions.isValueNull(rs.getString("customerId")))
                    transDetails.put("customerId", rs.getString("customerId"));
                else
                    transDetails.put("customerId", "");


                if (functions.isValueNull(rs.getString("payoutamount")))
                    transDetails.put("payoutamount",rs.getString("payoutamount"));
                else
                    transDetails.put("payoutamount", "");

                if(functions.isValueNull(rs.getString("status")))
                    transDetails.put("status",rs.getString("status"));
                else
                    transDetails.put("status", "");
                if(functions.isValueNull(rs.getString("notificationUrl")))
                    transDetails.put("notificationUrl",rs.getString("notificationUrl"));
                else
                    transDetails.put("notificationUrl", "");

                String query = "Select responsedescriptor,errorName from transaction_common_details where trackingid='" + rs.getString("trackingid") + "'";

                PreparedStatement ps1 = conn.prepareStatement(query);
                //logger.error("---status query ps1---" + ps1);
                ResultSet rs1 = ps1.executeQuery();
                transDetails.put("responsedescriptor", "");
                transDetails.put("errorName", "");

                while (rs1.next())
                {
                    if (functions.isValueNull(rs1.getString("responsedescriptor")))
                        transDetails.put("responsedescriptor", rs1.getString("responsedescriptor"));
                    if (functions.isValueNull(rs1.getString("errorName")) && (transDetails.get("status").equals("begun") || transDetails.get("status").equals("begun")))
                        transDetails.put("errorName", rs1.getString("errorName"));
                }

            }

        }
        catch (SystemError se)
        {
            logger.error("exception---" , se);
            PZExceptionHandler.raiseDBViolationException("SingleCallGenericStatus","getTransactionDetails()",null,"Transaction",se.getMessage(), PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            logger.error("exception---", e);
            PZExceptionHandler.raiseDBViolationException("SingleCallGenericStatus","getTransactionDetails()",null,"Transaction",e.getMessage(), PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transDetails;
    }


    public DirectKitResponseVO getTransactionForRejected(String toid, String description) throws PZDBViolationException
    {
        logger.debug("Inside getTransactionForRejected Method:::");
        transactionLogger.debug("Inside getTransactionForRejected Method:::");
        DirectKitResponseVO directKitResponseVO =  new DirectKitResponseVO();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Hashtable transDetails = new Hashtable();
        try
        {
            connection = Database.getConnection();
            String query = "SELECT * FROM transaction_fail_log WHERE description =?";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1,description);
            logger.debug("query in getTransactionForRejected---"+pstmt);
            transactionLogger.debug("query in getTransactionForRejected---"+pstmt);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                directKitResponseVO.setMemberId(rs.getString("toid"));
                directKitResponseVO.setRemark(rs.getString("remark"));
                directKitResponseVO.setAmount(rs.getString("amount"));
                directKitResponseVO.setStatus("rejected_transaction");
                directKitResponseVO.setStatusMsg(rs.getString("rejectreason"));

            }
        }
        catch (SystemError systemError)
        {
            logger.debug("exception---"+systemError);
            PZExceptionHandler.raiseDBViolationException("Transaction", "getTransactionForRejected()", null, "Transaction", systemError.getMessage(), PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.debug("exception---"+e);
            PZExceptionHandler.raiseDBViolationException("Transaction", "getTransactionForRejected()", null, "Transaction", e.getMessage(), PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }

        return directKitResponseVO;
    }

    public Hashtable getTransactionDetailsForSplitStatus(String description,String tablename)throws PZDBViolationException
    {
        Hashtable transDetails = new Hashtable();
        Connection conn = null;
        Functions functions = new Functions();
        ResultSet rs =null;
        StringBuffer sb = new StringBuffer();
        try
        {
            conn = Database.getConnection();
            sb.append("select trackingid,amount,orderdescription,description,captureamount,refundamount,status from "+tablename+"");

            if(functions.isValueNull(description))
            {
                sb.append(" where description like '"+description+"%'");
            }

            PreparedStatement ps = conn.prepareStatement(sb.toString());

            rs = ps.executeQuery();
            if (rs.next())
            {
                transDetails.put("trackingid",rs.getString("trackingid"));
                transDetails.put("amount",rs.getString("amount"));
                transDetails.put("orderdescription",rs.getString("orderdescription"));
                transDetails.put("description",rs.getString("description"));
                transDetails.put("captureamount",rs.getString("captureamount"));
                transDetails.put("refundamount",rs.getString("refundamount"));
                transDetails.put("status",rs.getString("status"));
            }
            logger.debug("---status query---"+sb.toString());
        }
        catch (SystemError se)
        {
            logger.debug("exception---"+se);
            PZExceptionHandler.raiseDBViolationException("SingleCallGenericStatus","getTransactionDetails()",null,"Transaction",se.getMessage(), PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause());
        }
        catch (SQLException e)
        {
            logger.debug("exception---"+e);
            PZExceptionHandler.raiseDBViolationException("SingleCallGenericStatus","getTransactionDetails()",null,"Transaction",e.getMessage(), PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transDetails;
    }

    public void updateBinDetailsforWebService(CommonValidatorVO commonValidatorVO)
    {
        Connection con                      = null;
        String firstSix                     = "";
        String lastfour                     = "";
        Functions functions                 = new Functions();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        try
        {
            if(merchantDetailsVO.getIsCardStorageRequired().equalsIgnoreCase("N") && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum())){
                firstSix  =  "******";
                lastfour  =  commonValidatorVO.getCardDetailsVO().getCardNum().substring(commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4);
            }else{
                if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
                {
                    firstSix =  commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                    lastfour =  commonValidatorVO.getCardDetailsVO().getCardNum().substring(commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4);
                }
            }

            con                     = Database.getConnection();
            String qry              = "insert into bin_details (icicitransid, first_six, last_four, accountid,emailaddr,boiledname,flightMode,bin_brand, bin_sub_brand, bin_card_type, bin_card_category,bin_usage_type,bin_country_code_A3,bin_trans_type,bin_country_code_A2,country_name,issuing_bank,customer_email,customer_phone,merchant_id,trans_dtstamp) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,unix_timestamp(now()))";
            PreparedStatement pstmt = con.prepareStatement(qry);

            pstmt.setString(1,commonValidatorVO.getTrackingid());
           /* pstmt.setString(2, commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6));
            pstmt.setString(3,commonValidatorVO.getCardDetailsVO().getCardNum().substring(commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4));*/
            pstmt.setString(2, firstSix);
            pstmt.setString(3,lastfour);
            pstmt.setString(4,commonValidatorVO.getMerchantDetailsVO().getAccountId());
            pstmt.setString(5,commonValidatorVO.getAddressDetailsVO().getEmail());
            pstmt.setString(6,commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
            pstmt.setString(7,commonValidatorVO.getMerchantDetailsVO().getFlightMode());
            pstmt.setString(8,commonValidatorVO.getCardDetailsVO().getBin_brand());
            pstmt.setString(9,commonValidatorVO.getCardDetailsVO().getBin_sub_brand());
            pstmt.setString(10,commonValidatorVO.getCardDetailsVO().getBin_card_type());
            pstmt.setString(11,commonValidatorVO.getCardDetailsVO().getBin_card_category());
            pstmt.setString(12,commonValidatorVO.getCardDetailsVO().getBin_usage_type());
            pstmt.setString(13,commonValidatorVO.getCardDetailsVO().getCountry_code_A3());
            pstmt.setString(14,commonValidatorVO.getCardDetailsVO().getTrans_type());
            pstmt.setString(15,commonValidatorVO.getCardDetailsVO().getCountry_code_A2());
            pstmt.setString(16,commonValidatorVO.getCardDetailsVO().getCountryName());
            pstmt.setString(17,commonValidatorVO.getCardDetailsVO().getIssuingBank());
            pstmt.setString(18,commonValidatorVO.getAddressDetailsVO().getEmail());
            pstmt.setString(19,commonValidatorVO.getAddressDetailsVO().getPhone());
            pstmt.setString(20,commonValidatorVO.getMerchantDetailsVO().getMemberId());


            pstmt.executeUpdate();

        }
        catch (SQLException systemError)
        {   logger.error("Sql Exception :::::",systemError);

            //ToDO raise and handle DB Exception

        }
        catch (SystemError systemError)
        {   logger.error("System Error ",systemError);
            //ToDO raise and handle DB Exception
        }
        finally
        {
            Database.closeConnection(con);
        }

    }

    public void updateBinDetailsforWebService(String trackingId, String bic,String iban, String accountId,String emailaddr, String boiledname, String flightMode )
    {
        Connection con =null;
        try
        {

            con = Database.getConnection();
            String qry="insert into bin_details (icicitransid, bic,iban , accountid,emailaddr,boiledname,flightMode) values(?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt=con.prepareStatement(qry);
            pstmt.setString(1,trackingId);
            pstmt.setString(2,bic);
            pstmt.setString(3,iban);
            //pstmt.setString(3,ccpan.substring(ccpan.length() - 4));
            pstmt.setString(4,accountId);
            pstmt.setString(5,emailaddr);
            pstmt.setString(6,boiledname);
            pstmt.setString(7,flightMode);

            pstmt.executeUpdate();

        }
        catch (SQLException systemError)
        {   logger.error("Sql Exception :::::",systemError);

            //ToDO raise and handle DB Exception

        }
        catch (SystemError systemError)
        {   logger.error("System Error ",systemError);
            //ToDO raise and handle DB Exception
        }
        finally
        {
            Database.closeConnection(con);
        }

    }

    public void updateBinDetailsforAccountWebService(String trackingId,String accountId,String emailaddr, String boiledname, String flightMode )
    {
        Connection con =null;
        try
        {

            con = Database.getConnection();
            String qry="insert into bin_details (icicitransid, accountid,emailaddr,boiledname,flightMode) values(?,?,?,?,?)";
            PreparedStatement pstmt=con.prepareStatement(qry);
            pstmt.setString(1,trackingId);
            pstmt.setString(2,accountId);
            pstmt.setString(3,emailaddr);
            pstmt.setString(4,boiledname);
            pstmt.setString(5,flightMode);

            pstmt.executeUpdate();

        }
        catch (SQLException systemError)
        {   logger.error("Sql Exception :::::",systemError);

            //ToDO raise and handle DB Exception

        }
        catch (SystemError systemError)
        {   logger.error("System Error ",systemError);
            //ToDO raise and handle DB Exception
        }
        finally
        {
            Database.closeConnection(con);
        }

    }
    public Hashtable getTransactionDetailsFormCommonForCancel(String trackingId,String toid)
    {
        Hashtable transaDetails = new Hashtable();
        Connection con = null;
        Functions functions = new Functions();
        try
        {
            StringBuffer sb = new StringBuffer();
            con = Database.getConnection();
            String sFields = "amount,paymentid,emailaddr,toid,fromid,description,orderdescription,dtstamp,status,httpheader,templatecurrency,templateamount,redirecturl,ccnum,name,accountId,currency,ipaddress, terminalid,notificationUrl ";

            sb.append("select "+sFields+" from transaction_common where ");

            if(functions.isValueNull(trackingId) && functions.isValueNull(toid))
            {
                sb.append("trackingid="+trackingId+" AND toid='"+toid+"'");
            }
            if(functions.isValueNull(trackingId) && !functions.isValueNull(toid))
            {
                sb.append("trackingid="+trackingId);
            }
            else if(!functions.isValueNull(trackingId) && functions.isValueNull(toid))
            {
                sb.append("toid='"+toid+"'");
            }
            sb.append(" AND STATUS='authsuccessful' ");

            PreparedStatement p1=con.prepareStatement(sb.toString());
            logger.debug("cancel query---"+p1);

            ResultSet rs = p1.executeQuery();

            if (rs.next())
            {
                transaDetails.put("toid",String.valueOf(rs.getInt("toid")));
                transaDetails.put("amount",rs.getString("amount"));
                transaDetails.put("accountid",rs.getString("accountid"));
                transaDetails.put("fromid",rs.getString("fromid"));
                if(rs.getString("paymentid") ==null)
                    transaDetails.put("paymentid","");
                else
                    transaDetails.put("paymentid",rs.getString("paymentid"));
                transaDetails.put("description",rs.getString("description"));
                transaDetails.put("orderdescription",rs.getString("orderdescription")!=null?rs.getString("orderdescription"):"");
                transaDetails.put("dtstamp",rs.getString("dtstamp"));
                transaDetails.put("httpheader",rs.getString("httpheader"));
                if(rs.getString("templatecurrency") ==null)
                    transaDetails.put("templatecurrency","");
                else
                    transaDetails.put("templatecurrency",rs.getString("templatecurrency"));
                if(rs.getString("templateamount") ==null)
                    transaDetails.put("templateamount","");
                else
                    transaDetails.put("templateamount",rs.getString("templateamount"));
                transaDetails.put("status",rs.getString("status"));
                transaDetails.put("emailaddr",rs.getString("emailaddr"));
                if(rs.getString("name") ==null)
                    transaDetails.put("name","");
                else
                    transaDetails.put("name",rs.getString("name"));
                if(PzEncryptor.decryptPAN(rs.getString("ccnum")) == null)
                    transaDetails.put("ccnum","");
                else
                    transaDetails.put("ccnum", PzEncryptor.decryptPAN(rs.getString("ccnum")));
                transaDetails.put("currency",rs.getString("currency"));
                transaDetails.put("redirecturl",rs.getString("redirecturl"));
                if(rs.getString("ipaddress") ==null)
                    transaDetails.put("ipaddress","");
                else
                    transaDetails.put("ipaddress",rs.getString("ipaddress"));
                transaDetails.put("terminalid",rs.getString("terminalid"));

                if(rs.getString("notificationUrl") !=null)
                {
                    transaDetails.put("notificationUrl",rs.getString("notificationUrl"));
                }else{
                    transaDetails.put("notificationUrl", "");
                }


            }
        }
        catch (SQLException se)
        {
            logger.error("SQLException ::::::: Leaving transactionEntry ", se);
        }
        catch (SystemError se)
        {
            logger.error("System Error ", se);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return transaDetails;
    }

    public String getPaymentModeForRest(String paymentType)
    {
        Connection con = null;
        String paymode="";

        try
        {
            con=Database.getConnection();
            String query = "SELECT paymode FROM payment_type WHERE paymodeid=?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1,paymentType);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                paymode = rs.getString("paymode");
            }
        }
        catch (Exception e)
        {
            logger.error("Exception occur", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return paymode;
    }

    public String getPaymentBrandForRest(String cardType)
    {
        Connection con = null;
        String cardtype="";

        try
        {
            con=Database.getConnection();
            String query = "SELECT cardtype FROM card_type WHERE cardtypeid=?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1,cardType);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                cardtype = rs.getString("cardtype");
            }
        }
        catch (Exception e)
        {
            logger.error("Exception occur", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return cardtype;
    }


}