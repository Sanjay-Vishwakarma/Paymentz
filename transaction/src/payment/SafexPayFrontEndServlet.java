package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.BlacklistManager;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.paygate.ag.common.utils.PayGateCryptoUtils;
import com.payment.Enum.PZProcessType;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.safexpay.SafexPayPaymentGateway;
import com.payment.safexpay.SafexPayUtils;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * Created by Admin on 6/13/18.
 */
public class SafexPayFrontEndServlet extends HttpServlet
{
    private  final static SafexPayGatewayLogger transactionLogger= new SafexPayGatewayLogger(SafexPayFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        doService(req,res);
      //  res.setHeader("Cache-Control","no-cache, no-store,must-revalidate");
    }

    public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        doService(req,res);
       // res.setHeader("Cache-Control","no-cache, no-store,must-revalidate");
    }

    public void doService(HttpServletRequest req,HttpServletResponse res)throws ServletException, IOException
    {

        transactionLogger.error("-----inside SafexPayFrontEndServlet-----");
        HttpSession session = req.getSession();
        Functions functions = new Functions();
        PayGateCryptoUtils payGateCryptoUtils = new PayGateCryptoUtils();
        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        TransactionManager transactionManager = new TransactionManager();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        CommResponseVO commResponseVO = new CommResponseVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        PaymentManager paymentManager = new PaymentManager();
        TransactionUtility transactionUtility = new TransactionUtility();
        CommRequestVO commRequestVO= new CommRequestVO();
        CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
        Connection con=null;
        PreparedStatement p = null;
        ResultSet rs = null;
        String toid = "";
        String description = "";
        String redirectUrl = "";
        String accountId = "";
        String orderDesc = "";
        String currency = "";
        String clkey = "";
        String checksumAlgo = "";
        String checksum = "";
        String autoredirect = "";
        String isService = "";
        String displayName = "";
        String isPowerBy = "";
        String logoName = "";
        String partnerName = "";

        String amount = "";
        String trackingId = "";
        String status = "";
        String remark = "";

        String bankTransactionStatus = "";
        String resultCode = "";
        String email = "";

        String tmpl_amt = "";
        String tmpl_currency = "";
        String firstName = "";
        String lastName = "";
        String paymodeid = "";
        String cardtypeid = "";
        String custId = "";
        String transactionStatus = "";
        String confirmStatus = "";
        String responseStatus = "";
        String transactionId = "";
        String message = "";
        String billingDesc = "";
        String dbStatus = "";
        String eci = "";
        String paymentid = "";
        String errorCode="";
        String name ="";
        String notificationUrl="";
        String ccnum = "";
        String expMonth="";
        String expYear="";
        String requestIp = "";
        String authorization_code = "";
        String responseAmount = "";
        String RESPONSE_CODE = "";
        String instatus = "";
        String firstSix = "";
        String lastFour = "";
        String cardHolderName = "";

        String merchantKey ="";
        Enumeration enumeration = req.getParameterNames();

        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = req.getParameter(key);
            transactionLogger.error("---Key---" + key + "---Value---" + value);
        }

        try
        {
            String txn_response = req.getParameter("txn_response");
            String pg_details = req.getParameter("pg_details");
            String fraud_details = req.getParameter("fraud_details");
            String other_details = req.getParameter("other_details");
            trackingId = req.getParameter("trackingId");

            String order_no = "";
            String ag_ref = "";
            String res_code = "";
            String res_message = "";

            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

            if (transactionDetailsVO != null)
            {
                SafexPayUtils safexPayUtils = new SafexPayUtils();

                ccnum = transactionDetailsVO.getCcnum();
                toid = transactionDetailsVO.getToid();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                accountId = transactionDetailsVO.getAccountId();
                orderDesc = transactionDetailsVO.getOrderDescription();
                currency = transactionDetailsVO.getCurrency();
                amount = transactionDetailsVO.getAmount();
                tmpl_amt = transactionDetailsVO.getTemplateamount();

                requestIp = Functions.getIpAddress(req);
               // authorization_code=transactionDetailsVO.getau
                transactionLogger.error("requestIp --- >"+requestIp);
                //transactionLogger.error("notificationUrl ---"+notificationUrl);
                if (functions.isValueNull(ccnum))
                {
                    ccnum = PzEncryptor.decryptPAN(ccnum);
                    firstSix = functions.getFirstSix(ccnum);
                    lastFour = functions.getLastFour(ccnum);
                }
                if(functions.isValueNull(transactionDetailsVO.getName())){
                    cardHolderName=transactionDetailsVO.getName();
                }
                if(functions.isValueNull(tmpl_amt))
                {
                    tmpl_amt = transactionDetailsVO.getTemplateamount();
                }
                else
                {  transactionLogger.error("inside else of tmpl_amnt--->");
                    tmpl_amt = null;
                }
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                if (functions.isValueNull(tmpl_currency))
                {
                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                }
                else
                {   transactionLogger.error("inside else of tmpl_currency --->");
                    tmpl_currency = "";
                }
                firstName = transactionDetailsVO.getFirstName();
                lastName = transactionDetailsVO.getLastName();
                name=transactionDetailsVO.getFirstName() +" "+ transactionDetailsVO.getLastName();


                paymodeid = transactionDetailsVO.getPaymodeId();
                cardtypeid = transactionDetailsVO.getCardTypeId();
                custId = transactionDetailsVO.getCustomerId();
                paymentid = transactionDetailsVO.getPaymentId();
                dbStatus = transactionDetailsVO.getStatus();
                transactionLogger.error("dbStatus ---"+dbStatus);

                merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                clkey = merchantDetailsVO.getKey();
                checksumAlgo = merchantDetailsVO.getChecksumAlgo();
                autoredirect = merchantDetailsVO.getAutoRedirect();
                isPowerBy = merchantDetailsVO.getIsPoweredBy();
                logoName = merchantDetailsVO.getLogoName();
                isService = merchantDetailsVO.getIsService();
                partnerName = merchantDetailsVO.getPartnerName();
                email = transactionDetailsVO.getEmailaddr();

                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toid);

                GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);


                merchantKey=gatewayAccount.getFRAUD_FTP_USERNAME();

                String decryptedTxn_response = PayGateCryptoUtils.decrypt(txn_response, merchantKey);
                transactionLogger.error("decryptedTxn_response is :"+decryptedTxn_response);
                String decryptedPg_details = PayGateCryptoUtils.decrypt(pg_details, merchantKey);
                String decryptedFraud_details = PayGateCryptoUtils.decrypt(fraud_details, merchantKey);
                String decryptedOther_details = PayGateCryptoUtils.decrypt(other_details, merchantKey);

                String[] txn_response_List = decryptedTxn_response.split(Pattern.quote("|"));
                String[] pg_details_List = decryptedPg_details.split(Pattern.quote("|"));
                String[] fraud_details_List = decryptedFraud_details.split(Pattern.quote("|"));
                String[] other_details_List = decryptedOther_details.split(Pattern.quote("|"));

                if (functions.isValueNull(txn_response_List[2].toString()))
                    order_no = txn_response_List[2].toString();
                if (functions.isValueNull(txn_response_List[2].toString()))
                    responseAmount = txn_response_List[3].toString();
                if (functions.isValueNull(txn_response_List[8].toString()))
                    transactionId = txn_response_List[8].toString();
                if (functions.isValueNull(txn_response_List[10].toString()))
                    transactionStatus = txn_response_List[10].toString();
                if (functions.isValueNull(txn_response_List[11].toString()))
                    errorCode = txn_response_List[11].toString();
                if (functions.isValueNull(txn_response_List[12].toString()))
                    message = txn_response_List[12].toString();

                transactionLogger.error("order_no----" + order_no);
                transactionLogger.error("safexpay direct frontend responseAmount----" + responseAmount);
                transactionLogger.error("ag_ref----" + transactionId);
                transactionLogger.error("status----" + transactionStatus);
                transactionLogger.error("res_code----" + errorCode);
                transactionLogger.error("res_message----" + message);



                displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                checksum = Checksum.generateChecksumV2(description, String.valueOf(amount), bankTransactionStatus, clkey, checksumAlgo);
                SafexPayPaymentGateway safexPayPaymentGateway=new SafexPayPaymentGateway(accountId);

                commTransactionDetailsVO.setOrderId(trackingId);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                transactionStatus="";
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    transactionLogger.error("inside AUTH_STARTED---");
                    StringBuffer dbBuffer = new StringBuffer();

                    con = Database.getConnection();

                    BlacklistManager blacklistManager=new BlacklistManager();
                    BlacklistVO blacklistVO=new BlacklistVO();
                    commResponseVO= (CommResponseVO) safexPayPaymentGateway.processInquiry(commRequestVO);
                    transactionId=commResponseVO.getTransactionId();
                    errorCode=commResponseVO.getAuthCode();

                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    transactionLogger.error(" inquiry  transactionId--->" + transactionId);
                    transactionLogger.error(" inquiry errorCode--->" + errorCode);

                    responseAmount=commResponseVO.getAmount();
                    if(functions.isValueNull(responseAmount))
                    {
                        Double compRsAmount = Double.valueOf(responseAmount);
                        Double compDbAmount = Double.valueOf(amount);
                        transactionLogger.error("safexpay inquiry response amount --->" + compRsAmount);
                        transactionLogger.error(" DB Amount--->" + compDbAmount);
                        if (compDbAmount.equals(compRsAmount))
                        {
                            transactionStatus = commResponseVO.getTransactionStatus();
                            amount = responseAmount;
                            message = commResponseVO.getRemark();
                        }

                        else
                        {
                            transactionStatus = "authfailed";
                            message = "Failed-IRA";
                            transactionLogger.error("inside else Amount incorrect--->" + responseAmount);
                            RESPONSE_CODE = "11111";
                            amount = responseAmount;
                            blacklistVO.setVpaAddress(custId);
                            blacklistVO.setIpAddress(requestIp);
                            blacklistVO.setEmailAddress(email);
                            blacklistVO.setActionExecutorId(toid);
                            blacklistVO.setActionExecutorName("SafexPayFrontEndServlet");
                            blacklistVO.setRemark("IncorrectAmount Trackingid : "+trackingId);
                            blacklistVO.setFirstSix(firstSix);
                            blacklistVO.setLastFour(lastFour);
                            blacklistVO.setName(cardHolderName);
                            blacklistManager.commonBlackListing(blacklistVO);
                        }
                    }
                    commResponseVO.setIpaddress(requestIp);
                    if ("success".equalsIgnoreCase(transactionStatus))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        status = "success";
                        responseStatus = "success";
                        billingDesc = displayName;

                        commResponseVO.setDescription(message);
                        commResponseVO.setStatus(status);
                        commResponseVO.setRemark(message);
                        commResponseVO.setDescriptor(billingDesc);
                       // confirmStatus = "Y";
                        dbStatus = "capturesuccess";
                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',templateamount=" + tmpl_amt + ",templatecurrency='" + tmpl_currency + "',status='capturesuccess'" + ",successtimestamp='" + functions.getTimestamp());
                        dbBuffer.append("',remark='" + message + "' where trackingid = " + trackingId);
                        transactionLogger.error("Update Query---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);

                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);
                    }

                    else if("fail".equalsIgnoreCase(transactionStatus)||"failed".equalsIgnoreCase(transactionStatus))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        transactionLogger.error("inside safexpay failed --->");
                        status = "failed";
                        responseStatus = "Failed";
                        commResponseVO.setStatus(status);
                        commResponseVO.setDescription(message);
                        commResponseVO.setRemark(message);

                        dbStatus = "authfailed";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',templateamount=" + tmpl_amt + ",templatecurrency='" + tmpl_currency + "'" + ",failuretimestamp='" + functions.getTimestamp());
                       /* if("11111".equalsIgnoreCase(RESPONSE_CODE)&&"success".equalsIgnoreCase(commResponseVO.getTransactionStatus())){
                            dbBuffer.append(",captureamount='"+ amount+"'");
                        }*/
                        dbBuffer.append("',remark='" + message + "' where trackingid = " + trackingId);
                        transactionLogger.error("Update Query---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);

                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                    }
                    else
                    {
                        responseStatus="pending";
                        status="pending";
                        dbStatus="authstarted";
                        transactionLogger.error("inside else pending--->"+transactionStatus);

                    }

                }

                else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = displayName;
                        status = "success";
                        message = "Transaction Successful";
                        responseStatus = "Successful";
                        dbStatus = "capturesuccess";
                    }
                    else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                    {
                        status = "failed";
                        message = "Transaction Declined";
                        responseStatus = "Failed";
                        dbStatus = "authfailed";
                    }  // set pending
                    else{
                        status = "pending";
                        message = "pending";
                        responseStatus = "pending";
                        dbStatus = "authstarted";
                    }
                }

                statusSyncDAO.updateAllTransactionFlowFlag(trackingId,dbStatus);
              /*  AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
*/
                commonValidatorVO.setTrackingid(trackingId);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                addressDetailsVO.setFirstname(firstName);
                addressDetailsVO.setLastname(lastName);
                addressDetailsVO.setCardHolderIpAddress(requestIp);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
                commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());
                if (functions.isValueNull(email))
                    addressDetailsVO.setEmail(email);
                else
                    addressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);

                commonValidatorVO.setCustomerId(custId);
                commonValidatorVO.setCustomerBankId(custId);


                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                {
                    transactionLogger.error("expDate --->"+transactionDetailsVO.getExpdate());

                    String expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                    transactionLogger.error("expDate --->"+expDate);
                    String temp[] = expDate.split("/");

                    if (functions.isValueNull(temp[0]))
                    {
                        expMonth = temp[0];
                    }
                    if (functions.isValueNull(temp[1]))
                    {
                        expYear = temp[1];
                    }
                }

                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);

                transactionUtility.setToken(commonValidatorVO, dbStatus);

                if (functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, status, message, "");
                }

                if ("Y".equalsIgnoreCase(autoredirect))
                {
                    transactionLogger.error("responseStatus in ---" + status);
                    transactionUtility.doAutoRedirect(commonValidatorVO, res, status, billingDesc);
                }
                else
                {
                    session.setAttribute("ctoken", ctoken);
                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", status);
                    req.setAttribute("displayName",displayName);
                    req.setAttribute("remark", message);
                    String confirmationPage = "";
                    String version = (String)session.getAttribute("version");
                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    session.invalidate();
                    RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(req, res);
                }
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("SafexPayFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException:::::", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("SafexPayFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause(), toid, null);
        }

        catch (SystemError e)
        {
            transactionLogger.error("SystemError:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("SafexPayFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("SafexPayFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("PZConstraintViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("SafexPayFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
    }
}

