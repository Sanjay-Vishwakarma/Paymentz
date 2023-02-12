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
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.onepay.OnePayPaymentGateway;
import com.payment.onepay.OnepayUtils;
import com.payment.payaidpayments.PayaidPaymentUtils;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;

/**
 * Created by Admin on 12/28/2021.
 */

public class OPFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionlogger = new TransactionLogger(OPFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);


    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req, res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering OnepayFrontEndServlet ......", e);
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req, res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering OnepayFrontEndServlet ......", e);
        }
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, PZDBViolationException
    {
        transactionlogger.error("Entering OnepayFrontEndServlet ......");


        HttpSession session = req.getSession();
        Functions functions = new Functions();
        //PayGateCryptoUtils payGateCryptoUtils = new PayGateCryptoUtils();
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

        Connection con = null;
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
        String trackingId = req.getParameter("trackingId");
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
        String errorCode = "";
        String name = "";
        String notificationUrl = "";
        String ccnum = "";
        String expMonth = "";
        String expYear = "";
        String requestIp = "";
        String merchantKey = "";
        String MOP_TYPE = "";
        String txtdesc = "";


        String RESPONSE_DATE_TIME = "";
        String RESPONSE_CODE = "";
        String AMOUNT = "";
        String TXN_ID = "";
        String TXNTYPE = "";
        String CURRENCY_CODE = "";
        String STATUS = "";
        String RESPONSE_MESSAGE = "";
        String CUST_EMAIL = "";
        String CUST_NAME = "";
        String RRN = "";
        String ORDER_ID = "";
        String PAYMENT_TYPE = "";
        String CUST_VPA = "";
        String MERCHANT_PAYMENT_TYPE = "";
        String notificationAmount = "";
        String websiteurl ="";
        String responseAmount = "";

        Enumeration enumeration = req.getParameterNames();

        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = req.getParameter(key);
            transactionlogger.error("---Key---" + key + "---Value---" + value);
        }

        try
        {

            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            CommRequestVO requestVO = new CommRequestVO();
            BlacklistManager blacklistManager = new BlacklistManager();
            BlacklistVO blacklistVO = new BlacklistVO();
            if (transactionDetailsVO != null)
            {
                if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                    ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                toid = transactionDetailsVO.getToid();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                accountId = transactionDetailsVO.getAccountId();
                orderDesc = transactionDetailsVO.getOrderDescription();
                currency = transactionDetailsVO.getCurrency();
                amount = transactionDetailsVO.getAmount();
                tmpl_amt = transactionDetailsVO.getTemplateamount();

                requestIp = Functions.getIpAddress(req);
                transactionlogger.debug("requestIp --- >" + requestIp);
                if (functions.isValueNull(tmpl_amt))
                {
                    tmpl_amt = transactionDetailsVO.getTemplateamount();
                }

                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                if (functions.isValueNull(tmpl_currency))
                {
                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                }
                else
                {
                    transactionlogger.debug("inside else of tmpl_currency --->"+" trackingid  "+trackingId);
                    tmpl_currency = "";
                }
                firstName = transactionDetailsVO.getFirstName();
                lastName = transactionDetailsVO.getLastName();
                name = transactionDetailsVO.getFirstName() + " " + transactionDetailsVO.getLastName();


                paymodeid = transactionDetailsVO.getPaymodeId();
                cardtypeid = transactionDetailsVO.getCardTypeId();
                custId = transactionDetailsVO.getCustomerId();
                transactionId = transactionDetailsVO.getPaymentId();
                dbStatus = transactionDetailsVO.getStatus();
                transactionlogger.error("dbStatus ---" + dbStatus+" trackingid  "+trackingId);

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

                OnePayPaymentGateway onePayPaymentGateway=new OnePayPaymentGateway(accountId);
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                websiteurl = "https://"+gatewayAccount.getFRAUD_FTP_SITE();
                checksum = Checksum.generateChecksumV2(description, String.valueOf(amount), bankTransactionStatus, clkey, checksumAlgo);
                transactionlogger.error("before if AUTH_STARTED---"+dbStatus+" trackingid  "+trackingId);

                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    transactionlogger.error("inside if AUTH_STARTED---"+dbStatus+" trackingid  "+trackingId);
                    StringBuffer dbBuffer = new StringBuffer();
                    //todo remove extra query
                    con = Database.getConnection();

                    commResponseVO = (CommResponseVO) onePayPaymentGateway.processQuery(trackingId, requestVO);

                    TXN_ID = commResponseVO.getTransactionId();
                    String inStatus = commResponseVO.getStatus();
                    String inRemark = commResponseVO.getRemark();
                    String inCode = commResponseVO.getErrorCode();
                    responseAmount = commResponseVO.getAmount();

                    transactionlogger.error("inquiry trackingid--->" + trackingId + " inquiry inStatus--->" + inStatus + " inquiry inCode--->" + inCode + " inquiry inRemark--->" + inRemark);
                    transactionlogger.debug("inquiry TXN_ID--->" + TXN_ID);
                    transactionlogger.debug("inquiry amount--->" + responseAmount);
                    if (functions.isValueNull(responseAmount))
                    {
                        Double compRsAmount = Double.valueOf(responseAmount);
                        Double compDbAmount = Double.valueOf(amount);

                        transactionlogger.error("response amount --->" + compRsAmount);
                        transactionlogger.error(" DB Amount--->" + compDbAmount);
                        if (compDbAmount.equals(compRsAmount))
                        {
                            STATUS = commResponseVO.getStatus();
                            remark = commResponseVO.getRemark();
                            RESPONSE_CODE = commResponseVO.getErrorCode();
                            amount = responseAmount;
                        }

                        else if (!compDbAmount.equals(compRsAmount) && "success".equalsIgnoreCase(inStatus))
                        {
                            remark = "Failed-IRA";
                            transactionlogger.error("trackingid ==== " + trackingId + " inside else Amount incorrect--->" + responseAmount);
                            STATUS = "failed";
                            RESPONSE_CODE = "11111";
                            amount = responseAmount;
                            blacklistVO.setVpaAddress(custId);
                            blacklistVO.setIpAddress(requestIp);
                            blacklistVO.setEmailAddress(email)
                            ;
                            blacklistVO.setActionExecutorId(toid);
                            blacklistVO.setActionExecutorName("PAFrontEndServlet");
                            blacklistVO.setRemark("IncorrectAmount Trackingid : " + trackingId);
                            blacklistManager.commonBlackListing(blacklistVO);
                        }
                    }
                    currency = commResponseVO.getCurrency();
                    txtdesc = commResponseVO.getDescription();
                    String bankTransactionDate = commResponseVO.getBankTransactionDate();
                    commResponseVO.setTmpl_Amount(tmpl_amt);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    commResponseVO.setIpaddress(requestIp);


                    if ( "Success".equalsIgnoreCase(STATUS))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        status = "success";
                        responseStatus = remark;
                        billingDesc = displayName;
                        message = remark;
                        commResponseVO.setDescription(responseStatus);
                        commResponseVO.setStatus(status);
                        commResponseVO.setRemark(responseStatus);
                        commResponseVO.setDescriptor(billingDesc);
                        dbStatus = "capturesuccess";
                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + TXN_ID + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "',rrn='" + RRN + "',status='capturesuccess'" + ",successtimestamp='" + functions.getTimestamp());
                        dbBuffer.append("',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + responseStatus + "' where trackingid = " + trackingId);
                        transactionlogger.error("Update Query---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);//+ ",rrn='" + rrn
                    }


                    else if ("Failed".equalsIgnoreCase(STATUS))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        transactionlogger.error("inside Payaid failed --->");
                        status = "Failed";
                        message = remark;
                        responseStatus = remark;
                        commResponseVO.setStatus(status);
                        commResponseVO.setDescription(responseStatus);
                        commResponseVO.setRemark(responseStatus);

                        dbStatus = "authfailed";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + TXN_ID + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "'" + " ,failuretimestamp='" + functions.getTimestamp());

                        dbBuffer.append("',authorization_code='" + amount + "',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + responseStatus + "' where trackingid = " + trackingId);
                        transactionlogger.error("Update Query---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);

                    }
                    else
                    {
                        //pending
                        dbStatus = "authstarted";
                        status = "Pending";
                        message = "Pending";
                        transactionlogger.error("inside PayaidPay pending condition --->" + STATUS + "--" + message);
                    }
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                }

                else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        transactionlogger.error("inside else success  condition onepay --->" + dbStatus + "--" +" trackingid  "+trackingId);


                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status = "success";
                        if (functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";
                        dbStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    }
                    else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                    {
                        status = "failed";
                        if (functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else if (!functions.isValueNull(message))
                            message = "Transaction Failed";
                        dbStatus = PZTransactionStatus.AUTH_FAILED.toString();

                    }
                    else
                    {
                        status = "pending";
                        message = "Transaction pending";
                        dbStatus = PZTransactionStatus.AUTH_STARTED.toString();

                    }
                }


                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, responseStatus, billingDesc);

                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, responseStatus, billingDesc);

                commonValidatorVO.setStatus(dbStatus);
                commonValidatorVO.setTrackingid(trackingId);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
               // genericTransDetailsVO.setRedirectUrl(redirectUrl);
                transactionlogger.error("websiteurl--------->"+websiteurl);
                if(functions.isValueNull(websiteurl)){
                    transactionlogger.error("inside if websiteurl--------->"+websiteurl);
                    genericTransDetailsVO.setRedirectUrl(websiteurl);
                }
                else{
                    transactionlogger.error("inside else websiteurl--------->"+redirectUrl);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                }
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                addressDetailsVO.setFirstname(firstName);
                addressDetailsVO.setLastname(lastName);
                addressDetailsVO.setCardHolderIpAddress(requestIp);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
                commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());

                if (functions.isValueNull(email))
                    addressDetailsVO.setEmail(email)
                            ;
                else
                    addressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);

                commonValidatorVO.setCustomerId(custId);
                commonValidatorVO.setCustomerBankId(custId);

                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                {
                    transactionlogger.debug("expDate --->" + transactionDetailsVO.getExpdate());

                    String expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                    transactionlogger.debug("expDate --->" + expDate);
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

                if (functions.isValueNull(notificationUrl))
                {
                    transactionlogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, status, message, "");
                    transactionlogger.error("remark message----------------------" + message);
                }


                OnepayUtils onepayUtils=new OnepayUtils();
                if (functions.isValueNull(genericTransDetailsVO.getRedirectUrl()))
                {
                    transactionlogger.error("responseStatus in ---" + dbStatus);
                    onepayUtils.doHttpPostConnection(commonValidatorVO, redirectUrl, billingDesc, status);
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
        catch (PZTechnicalViolationException e)
        {
            transactionlogger.error("PZTechnicalViolationException onepay---", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionlogger.error("NoSuchAlgorithmException  onepay---",e);
        }
        catch (SystemError systemError)
        {
            transactionlogger.error("SystemError  onepay---",systemError);
        }

    }

}
