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
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.imoneypay.IMoneyPayPaymentGateway;
import com.payment.imoneypay.IMoneypPayUtils;
import com.payment.repropay.ReproPayUtills;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

/**
 * Created by Admin on 6/23/2021.
 */
public class RPFrontendServlet extends HttpServlet
{
    private static TransactionLogger transactionlogger = new TransactionLogger(RPFrontendServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req,res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering ReproPayFrontendServlet ......",e);
        }
    }

    public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req,res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering ReproPayFrontendServlet ......",e);
        }
    }

    public void doService(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException, PZDBViolationException
    {
        transactionlogger.error("....Entering ReproPayFrontendServlet ......");
        HttpSession session = req.getSession();
        Functions functions = new Functions();
        ActionEntry entry                           = new ActionEntry();
        AuditTrailVO auditTrailVO                   = new AuditTrailVO();
        TransactionManager transactionManager       = new TransactionManager();
        CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO         = new MerchantDetailsVO();
        MerchantDAO merchantDAO                     = new MerchantDAO();
        CommResponseVO commResponseVO               = new CommResponseVO();
        GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO          = new GenericCardDetailsVO();
        StatusSyncDAO statusSyncDAO                 = new StatusSyncDAO();
        PaymentManager paymentManager               = new PaymentManager();
        TransactionUtility transactionUtility       = new TransactionUtility();
        CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();

        Connection con      = null;
        PreparedStatement p = null;
        ResultSet rs        = null;
        String toid         = "";
        String description  = "";
        String redirectUrl  = "";
        String accountId    = "";
        String orderDesc    = "";
        String currency     = "";
        String clkey        = "";
        String checksumAlgo = "";
        String checksum     = "";
        String autoredirect = "";
        String isService    = "";
        String isPowerBy    = "";
        String logoName     = "";
        String partnerName  = "";
        String amount       = "";
        String trackingId   = req.getParameter("trackingId");
        String status       = "";
        String remark       = "";

        String bankTransactionStatus = "";
        String resultCode            = "";
        String email                 = "";
        String tmpl_amt             = "";
        String tmpl_currency        = "";
        String firstName            = "";
        String lastName             = "";
        String paymodeid            = "";
        String cardtypeid           = "";
        String custId               = "";
        String transactionStatus    = "";
        String confirmStatus        = "";
        String responseStatus       = "";
        String transactionId        = "";
        String message              = "";
        String billingDesc          = "";
        String dbStatus             = "";
        String eci                  = "";
        String paymentid            = "";
        String errorCode            = "";
        String name                 = "";
        String notificationUrl      = "";
        String ccnum                = "";
        String expMonth             = "";
        String expYear              = "";
        String requestIp            = "";
        String merchantKey          = "";
        String MOP_TYPE             = "";
        String txtdesc              = "";
        String RESPONSE_DATE_TIME   = "";
        String paymentOption        = "";
        String AMOUNT               = "";
        String MerchantorderId      = "";
        String VendorOrderId        = "";
        String txtDate              = "";
        String resStatus            = "";
        String signature            = "";
        String STATUS               = "";
        String RESPONSE_CODE        = "";
        String notificationAmount   = "";
        String responseAmount       = "";
        String firstSix       = "";
        String lastFour       = "";
        String cardHolderName       = "";
        String displayName="";

        Enumeration enumeration = req.getParameterNames();

        while (enumeration.hasMoreElements())
        {
            String key      = (String) enumeration.nextElement();
            String value    = req.getParameter(key);
            transactionlogger.error("---Key---" + key + "---Value---" + value);
        }
        String ENCDATA          = "";
        String decryptedData    = "";

        String Ds_SignatureVersion      = "";
        String Ds_MerchantParameters    = "";
        String Ds_Signature             = "";

        String Ds_Response = "";
        String Ds_TransactionType = "";
        String Ds_AuthorisationCode = "";
        String Ds_Terminal = "";
        String Ds_Date = "";
        String Ds_Hour = "";
        String Ds_Amount = "";
        String Ds_Order = "";
        String Ds_MerchantData = "";
        String responseamt="";
        String responsecurrency="";


        try
        {
            transactionlogger.error("..... ReproPayFrontendServlet ......"+trackingId);


            if(functions.isValueNull(req.getParameter("Ds_SignatureVersion"))){
                Ds_SignatureVersion=req.getParameter("Ds_SignatureVersion");
            }

            if(functions.isValueNull(req.getParameter("Ds_MerchantParameters"))){
                Ds_MerchantParameters=req.getParameter("Ds_MerchantParameters");
            }

            if(functions.isValueNull(req.getParameter("Ds_Signature"))){
                Ds_Signature=req.getParameter("Ds_Signature");
            }
            ReproPayUtills reproPayUtills =  new ReproPayUtills();

            String decodedParams = reproPayUtills.decodeMerchantParameters(Ds_MerchantParameters);
            transactionlogger.error("decodedParams -----> "+trackingId+" "+decodedParams);
            JSONObject jsonObject = new JSONObject(decodedParams);

            if (jsonObject != null)
            {
                if (jsonObject.has("Ds_Response"))
                {
                    Ds_Response = jsonObject.getString("Ds_Response");
                    if ("0000".equals(Ds_Response) || "0099".equalsIgnoreCase(Ds_Response))
                    {
                        Ds_Response = "success";
                    }else if("0190".equalsIgnoreCase(Ds_Response) || "0184".equalsIgnoreCase(Ds_Response)){
                        Ds_Response = "failed";
                    }
                }
                if (jsonObject.has("Ds_TransactionType"))
                {
                    Ds_TransactionType = jsonObject.getString("Ds_TransactionType");
                }
                if (jsonObject.has("Ds_AuthorisationCode"))
                {
                    Ds_AuthorisationCode = jsonObject.getString("Ds_AuthorisationCode");
                }
                if (jsonObject.has("Ds_Terminal"))
                {
                    Ds_Terminal = jsonObject.getString("Ds_Terminal");
                }

                if (jsonObject.has("Ds_Date"))
                {
                    Ds_Date = jsonObject.getString("Ds_Date");
                    transactionlogger.error("Ds_Date ---" + Ds_Date);
                }
                if (jsonObject.has("Ds_Hour"))
                {
                    Ds_Hour = jsonObject.getString("Ds_Hour");
                    transactionlogger.error("Ds_Hour ---" + Ds_Hour);
                }
                if (jsonObject.has("Ds_Order"))
                {
                    Ds_Order = jsonObject.getString("Ds_Order");
                    transactionlogger.error("Ds_Order  ---" + Ds_Order);
                }
                if (jsonObject.has("Ds_Amount"))
                {
                    Ds_Amount = jsonObject.getString("Ds_Amount");
                    Ds_Amount  = String.format("%.2f", Double.parseDouble(Ds_Amount)/100);

                }

            }

            TransactionDetailsVO transactionDetailsVO   = transactionManager.getTransDetailFromCommon(trackingId);
            CommRequestVO requestVO                     = new CommRequestVO();
            BlacklistManager blacklistManager           = new BlacklistManager();
            BlacklistVO blacklistVO                     = new BlacklistVO();

            if (transactionDetailsVO != null)
            {
                ccnum        = transactionDetailsVO.getCcnum();
                if (functions.isValueNull(ccnum))
                {
                    ccnum    = PzEncryptor.decryptPAN(ccnum);
                    firstSix = functions.getFirstSix(ccnum);
                    lastFour = functions.getLastFour(ccnum);
                }

                toid        = transactionDetailsVO.getToid();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                accountId   = transactionDetailsVO.getAccountId();
                orderDesc   = transactionDetailsVO.getOrderDescription();
                currency    = transactionDetailsVO.getCurrency();
                amount      = transactionDetailsVO.getAmount();
                tmpl_amt    = transactionDetailsVO.getTemplateamount();

                requestIp = Functions.getIpAddress(req);
                transactionlogger.debug("requestIp --- >" + requestIp);
                transactionlogger.debug("notificationUrl ---" + notificationUrl);
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
                    transactionlogger.debug("inside else of tmpl_currency --->");
                    tmpl_currency = "";
                }
                firstName   = transactionDetailsVO.getFirstName();
                lastName    = transactionDetailsVO.getLastName();
                name        = transactionDetailsVO.getFirstName() + " " + transactionDetailsVO.getLastName();

                if(functions.isValueNull(transactionDetailsVO.getName())){
                    cardHolderName=transactionDetailsVO.getName();
                }
                paymodeid       = transactionDetailsVO.getPaymodeId();
                cardtypeid      = transactionDetailsVO.getCardTypeId();
                custId          = transactionDetailsVO.getCustomerId();
                transactionId   = transactionDetailsVO.getPaymentId();
                dbStatus        = transactionDetailsVO.getStatus();
                transactionlogger.error("dbStatus ---" + dbStatus);

                merchantDetailsVO   = merchantDAO.getMemberDetails(toid);
                clkey               = merchantDetailsVO.getKey();
                checksumAlgo        = merchantDetailsVO.getChecksumAlgo();
                autoredirect        = merchantDetailsVO.getAutoRedirect();
                isPowerBy           = merchantDetailsVO.getIsPoweredBy();
                logoName            = merchantDetailsVO.getLogoName();
                isService           = merchantDetailsVO.getIsService();
                partnerName         = merchantDetailsVO.getPartnerName();
                email               = transactionDetailsVO.getEmailaddr();

                auditTrailVO.setActionExecutorName("RPFrontend");
                auditTrailVO.setActionExecutorId(toid);


                GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
                checksum    = Checksum.generateChecksumV2(description, String.valueOf(amount), bankTransactionStatus, clkey, checksumAlgo);
                displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    transactionlogger.error("inside AUTH_STARTED---");
                    StringBuffer dbBuffer = new StringBuffer();

                    commTransactionDetailsVO.setAmount(amount);
                    commTransactionDetailsVO.setCurrency(currency);

                    requestVO.setTransDetailsVO(commTransactionDetailsVO);

                    con             = Database.getConnection();


                    String inStatus     = Ds_Response;
                    String inRemark     = Ds_Response;
                    String inCode       = Ds_AuthorisationCode;
                    responseAmount      = Ds_Amount;
                    String  TXN_ID      = "";
                    String  RRN         = "";


                    transactionlogger.error("trackingid--->"+trackingId);
                    transactionlogger.error("inStatus--->"+inStatus);
                    transactionlogger.error("amount--->"+responseAmount);
                    transactionlogger.error("inCode--->"+inCode);
                    transactionlogger.error("inRemark--->"+inRemark);

                    if(functions.isValueNull(responseAmount))
                    {
                        Double compRsAmount = Double.valueOf(responseAmount);
                        Double compDbAmount = Double.valueOf(amount);

                        transactionlogger.error("response amount --->" + compRsAmount);
                        transactionlogger.error(" DB Amount--->" + compDbAmount);

                        if (compDbAmount.equals(compRsAmount))
                        {
                            STATUS = Ds_Response;
                            amount = Ds_Amount;
                            remark = Ds_Response;
                        }

                        else if (!compDbAmount.equals(compRsAmount) && "success".equalsIgnoreCase(inStatus))
                        {
                            transactionlogger.error("inside else Amount incorrect--->" + responseAmount);
                            remark = "Failed-IRA";
                            STATUS = "authfailed";
                            amount = responseAmount;
                            blacklistVO.setVpaAddress(custId);
                            blacklistVO.setIpAddress(requestIp);
                            blacklistVO.setEmailAddress(email);
                            blacklistVO.setActionExecutorId(toid);
                            blacklistVO.setActionExecutorName("AcquirerFrontEnd");
                            blacklistVO.setRemark("IncorrectAmount Trackingid : "+trackingId);
                            blacklistVO.setFirstSix(firstSix);
                            blacklistVO.setLastFour(lastFour);
                            blacklistVO.setName(cardHolderName);
                            blacklistManager.commonBlackListing(blacklistVO);
                        }
                    }


                    commResponseVO.setTmpl_Amount(tmpl_amt);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    commResponseVO.setIpaddress(requestIp);


                    if ("success".equalsIgnoreCase(STATUS))
                    {
                        transactionlogger.error("inside Repro succes --->");
                        notificationUrl    = transactionDetailsVO.getNotificationUrl();
                        transactionlogger.error("notificationUrl --->"+notificationUrl);
                        status              = "success";
                        responseStatus      = remark;
                        billingDesc         = displayName;
                        message             = remark;
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(billingDesc);
                        transactionlogger.error("billingDesc --->" + billingDesc);
                        transactionlogger.error("billingDesc --->"+commResponseVO.getDescriptor());
                        commResponseVO.setErrorCode(inCode);
                        commResponseVO.setRemark(remark);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setDescription("Transaction Successfull");
                        commResponseVO.setResponseTime(URLDecoder.decode(Ds_Date + "<br>" + Ds_Hour));
                        transactionlogger.error("Ds_date==============>"+commResponseVO.getResponseTime());
                        commResponseVO.setTransactionId(Ds_Order);
                        commResponseVO.setAuthCode(Ds_AuthorisationCode);
                        dbStatus = "capturesuccess";

                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + Ds_Order + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "',rrn='" + RRN + "" + "',status='capturesuccess'" + " ,successtimestamp='" + functions.getTimestamp());
                        dbBuffer.append("',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + responseStatus + "' where trackingid = " + trackingId);
                        transactionlogger.error("Update Query---" + dbBuffer);

                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);//+ ",rrn='" + rrn

                    }
                    else if("Failed".equalsIgnoreCase(STATUS))
                    {
                        transactionlogger.error("inside Repro failed --->");
                        //billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        status          = "Failed";
                        message         = remark;
                        responseStatus  = remark;
                        billingDesc         = displayName;
                        commResponseVO.setStatus("failed");
                        transactionDetailsVO.setBillingDesc(billingDesc);
                        commResponseVO.setDescriptor(billingDesc);
                        commResponseVO.setErrorCode(inCode);
                        commResponseVO.setRemark(remark);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setResponseTime(URLDecoder.decode(Ds_Date + "<br>" + Ds_Hour));
                        transactionlogger.error("Ds_date==============>"+commResponseVO.getResponseTime());
                        commResponseVO.setTransactionId(Ds_Order);
                        commResponseVO.setAuthCode(Ds_AuthorisationCode);
                        commResponseVO.setDescription("Transaction Failed");
                        dbStatus = "authfailed";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + Ds_Order + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "'" + " ,failuretimestamp='" + functions.getTimestamp());

                        dbBuffer.append("',authorization_code='"+amount+"',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" +responseStatus+ "' where trackingid = " + trackingId);
                        transactionlogger.error("Update Query---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                    }
                    else {
                        //pending
                        dbStatus    = "authstarted";
                        status      = "Pending";
                        message     = "Pending";
                        commResponseVO.setStatus(status);
                        transactionlogger.error("inside ReproPay pending condition --->"+STATUS+"--"+message+"---"+billingDesc);
                    }
                }
                else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status      = "success";

                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";
                        dbStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    }else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus)){
                        status  = "failed";

                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else if(!functions.isValueNull(message))
                            message = "Transaction Failed";
                        dbStatus=PZTransactionStatus.AUTH_FAILED.toString();
                    }
                    else
                    {
                        status      = "pending";
                        message     = "Transaction pending";
                        dbStatus    = PZTransactionStatus.AUTH_STARTED.toString();
                    }
                }
                statusSyncDAO.updateAllTransactionFlowFlag(trackingId,dbStatus);

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
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
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

                if (session.getAttribute("language") !=null)
                {
                    addressDetailsVO.setLanguage(session.getAttribute("language").toString());
                }
                else
                {
                    addressDetailsVO.setLanguage("");
                }

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

                    String expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());

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
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, dbStatus, message, "");
                    transactionlogger.debug("remark message----------------------" + message);
                    transactionlogger.debug("dbStatus message----------------------" + dbStatus);
                }

                if ("Y".equalsIgnoreCase(autoredirect))
                {
                    transactionlogger.debug("responseStatus in ---" + dbStatus);
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
                    String version          = (String)session.getAttribute("version");

                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";
                    session.invalidate();
                    RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(req, res);
                }
            }
        }
        catch (Exception e)
        {
            transactionlogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("RPFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
    }
}
