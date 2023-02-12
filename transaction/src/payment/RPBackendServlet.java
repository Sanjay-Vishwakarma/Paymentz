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
import com.payment.Enum.PZProcessType;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.repropay.ReproPayUtills;
import com.payment.response.PZResponseStatus;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;

/**
 * Created by Admin on 12/4/2021.
 */
public class RPBackendServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(RPBackendServlet.class.getName());

    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req,res);
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("Entering ReproPayBackendServlet ......",e);
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
            transactionLogger.error("Entering ReproPayBackendServlet ......",e);
        }
    }
    public void doService(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException, PZDBViolationException
    {
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
        PrintWriter pWriter = res.getWriter();
        String responseCode = "200";
        String returnResStatus = "OK";
        while (enumeration.hasMoreElements())
        {
            String key      = (String) enumeration.nextElement();
            String value    = req.getParameter(key);
            transactionLogger.error("---Key---" + key + "---Value---" + value);
        }

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

        try{
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
            transactionLogger.error("decodedParams -----> "+trackingId+" "+decodedParams);
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
                    transactionLogger.error("---  Ds_TransactionType ---" + Ds_TransactionType);

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
                    transactionLogger.error("Ds_Date ---" + Ds_Date);
                }
                if (jsonObject.has("Ds_Hour"))
                {
                    Ds_Hour = jsonObject.getString("Ds_Hour");
                    transactionLogger.error("Ds_Hour ---" + Ds_Hour);
                }
                if (jsonObject.has("Ds_Order"))
                {
                    Ds_Order = jsonObject.getString("Ds_Order");
                    transactionLogger.error("Ds_Order ---" + Ds_Order);
                }
                if (jsonObject.has("Ds_Amount"))
                {
                    Ds_Amount = jsonObject.getString("Ds_Amount");
                    Ds_Amount  = String.format("%.2f", Double.parseDouble(Ds_Amount)/100);
                    transactionLogger.error("Ds_Amount response amount================>"+Ds_Amount);


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
                transactionLogger.debug("requestIp --- >" + requestIp);
                transactionLogger.debug("notificationUrl ---" + notificationUrl);
                transactionLogger.error("transactionDetailsVO.getAmount()================>"+amount);

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
                    transactionLogger.debug("inside else of tmpl_currency --->");
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
                transactionLogger.error("dbStatus ---" + dbStatus);

                merchantDetailsVO   = merchantDAO.getMemberDetails(toid);
                clkey               = merchantDetailsVO.getKey();
                checksumAlgo        = merchantDetailsVO.getChecksumAlgo();
                autoredirect        = merchantDetailsVO.getAutoRedirect();
                isPowerBy           = merchantDetailsVO.getIsPoweredBy();
                logoName            = merchantDetailsVO.getLogoName();
                isService           = merchantDetailsVO.getIsService();
                partnerName         = merchantDetailsVO.getPartnerName();
                email               = transactionDetailsVO.getEmailaddr();

                auditTrailVO.setActionExecutorName("RPBackend");
                auditTrailVO.setActionExecutorId(toid);


                GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
               // billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                checksum    = Checksum.generateChecksumV2(description, String.valueOf(amount), bankTransactionStatus, clkey, checksumAlgo);
                displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    transactionLogger.error("inside AUTH_STARTED---");
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


                    transactionLogger.error("trackingid--->"+trackingId);
                    transactionLogger.error("inStatus--->"+inStatus);
                    transactionLogger.error("amount--->"+responseAmount);
                    transactionLogger.error("inCode--->"+inCode);
                    transactionLogger.error("inRemark--->"+inRemark);

                    if(functions.isValueNull(responseAmount))
                    {
                        Double compRsAmount = Double.valueOf(responseAmount);
                        Double compDbAmount = Double.valueOf(amount);

                        transactionLogger.error("response amount --->" + compRsAmount);
                        transactionLogger.error(" DB Amount--->" + compDbAmount);

                        if (compDbAmount.equals(compRsAmount))
                        {
                            STATUS = Ds_Response;
                            amount = Ds_Amount;
                            remark = Ds_Response;
                        }

                        else if (!compDbAmount.equals(compRsAmount) && "success".equalsIgnoreCase(inStatus))
                        {
                            transactionLogger.error("inside else Amount incorrect--->" + responseAmount);
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
                        transactionLogger.error("inside Repro succes --->");
                        displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        notificationUrl    = transactionDetailsVO.getNotificationUrl();
                        transactionLogger.error("notificationUrl --->"+notificationUrl);
                        status              = "success";
                        responseStatus      = remark;
                        billingDesc         = displayName;
                        message             = remark;
                        transactionLogger.error("billingDesc --->" + billingDesc);
                        transactionLogger.error("billingDesc --->"+commResponseVO.getDescriptor());

                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(billingDesc);
                        commResponseVO.setErrorCode(inCode);
                        commResponseVO.setRemark(remark);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setDescription("Transaction Successfull");
                        commResponseVO.setResponseTime(URLDecoder.decode(Ds_Date + "<br>" + Ds_Hour));
                        transactionLogger.error("Ds_date==============>"+commResponseVO.getResponseTime());
                        commResponseVO.setTransactionType(Ds_TransactionType);
                        commResponseVO.setTransactionId(Ds_Order);
                        commResponseVO.setAuthCode(Ds_AuthorisationCode);
                        dbStatus = "capturesuccess";

                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + Ds_Order + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "',rrn='" + RRN + "" + "',status='capturesuccess'" + " ,successtimestamp='" + functions.getTimestamp());
                        dbBuffer.append("',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + responseStatus + "' where trackingid = " + trackingId);
                        transactionLogger.error("Update Query---" + dbBuffer);

                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);//+ ",rrn='" + rrn


                    }
                    else if("Failed".equalsIgnoreCase(STATUS))
                    {
                        transactionLogger.error("inside Repro failed --->");
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
                        commResponseVO.setTransactionType(Ds_TransactionType);
                        commResponseVO.setTransactionId(Ds_Order);
                        commResponseVO.setAuthCode(Ds_AuthorisationCode);
                        commResponseVO.setDescription("Transaction Failed");

                        dbStatus = "authfailed";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + Ds_Order + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "'" + " ,failuretimestamp='" + functions.getTimestamp());

                        dbBuffer.append("',authorization_code='" + amount + "',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + responseStatus + "' where trackingid = " + trackingId);
                        transactionLogger.error("Update Query---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                    }
                    else {
                        //pending
                        dbStatus    = "authstarted";
                        status      = "Pending";
                        message     = "Pending";
                        commResponseVO.setStatus(status);
                        transactionLogger.error("inside ReproPay pending condition --->"+STATUS+"--"+message+"---"+billingDesc);
                    }
                }
                else if (Ds_TransactionType.equalsIgnoreCase("3"))
                {
                    transactionLogger.error("--- in REFUND , Ds_TransactionType ---" + Ds_TransactionType);

                    if (PZTransactionStatus.MARKED_FOR_REVERSAL.toString().equalsIgnoreCase(dbStatus))
                    {
                            transactionLogger.error("--- in REFUND , reversesuccess ---"+PZTransactionStatus.MARKED_FOR_REVERSAL.toString());
                            status = "success";
                            displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                            auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                            auditTrailVO.setActionExecutorId(toid);
                            confirmStatus = "Y";
                            dbStatus = PZTransactionStatus.REVERSED.toString();
                            String refundstatus = "reversed";
                            String refundedAmount = transactionDetailsVO.getRefundAmount();
                            String captureAmount = transactionDetailsVO.getCaptureAmount();
                            double refAmount = Double.parseDouble(refundedAmount) + Double.parseDouble(Ds_Amount);
                            transactionLogger.error("status============>"+status+ "remark====>"+remark);

                            transactionLogger.error("double refAmount================>"+refAmount);
                            transactionLogger.error("Ds_Amount response amount================>"+Ds_Amount);
                            transactionLogger.error("amount================>"+amount);
                            String actionEntryAction = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                            String actionEntryStatus = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;
                            transactionStatus = PZResponseStatus.SUCCESS.toString();

                            commResponseVO.setStatus("success");
                            commResponseVO.setRemark(status);
                            commResponseVO.setDescriptor(displayName);
                            commResponseVO.setTransactionStatus(status);
                            commResponseVO.setResponseTime(URLDecoder.decode(Ds_Date + "<br>" + Ds_Hour));
                            commResponseVO.setTransactionId(Ds_Order);
                            commResponseVO.setAuthCode(Ds_AuthorisationCode);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setTmpl_Amount(tmpl_amt);
                            commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                            commResponseVO.setTransactionType(Ds_TransactionType);
                            commResponseVO.setErrorCode(Ds_AuthorisationCode);
                            commResponseVO.setDescription("Reversal Successful");

                            transactionLogger.error("captureAmount==========>"+captureAmount);
                            transactionLogger.error("refAmount============>"+refAmount);
                            if (Double.parseDouble(captureAmount) > Double.parseDouble(String.valueOf(refAmount)))
                            {
                                status = "reversed";
                                dbStatus = "";
                                actionEntryAction = ActionEntry.ACTION_PARTIAL_REFUND;
                                actionEntryStatus = ActionEntry.STATUS_PARTIAL_REFUND;
                                transactionStatus = PZResponseStatus.PARTIALREFUND.toString();
                            }

                            if (captureAmount.equals(String.format("%.2f", refAmount)))
                            {
                                status = "reversed";
                                actionEntryAction = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                                actionEntryStatus = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;
                                transactionStatus = PZResponseStatus.SUCCESS.toString();
                            }
                            StringBuffer dbBuffer=new StringBuffer();
                            dbBuffer.append("update transaction_common set status='reversed',refundAmount='" + String.format("%.2f", refAmount) +  "',firstname='" + firstName + "',lastname='" + lastName + "',ccnum='"+  ccnum  + "',refundtimestamp='" + functions.getTimestamp() +  "'where trackingid = " + trackingId);
                            transactionLogger.error("dbBuffer->" + dbBuffer);

                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);

                            entry.actionEntryForCommon(trackingId, String.format("%.2f", refAmount), actionEntryAction, actionEntryStatus, commResponseVO, auditTrailVO, null);
                            String updatedStatus = PZTransactionStatus.REVERSED.toString();
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);

                            if (functions.isValueNull(notificationUrl))
                            {
                                transactionLogger.error("inside sending notification---" + notificationUrl);
                                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                transactionDetailsVO.setBillingDesc(displayName);
                                asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updatedStatus, remark, "");
                            }


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
                }statusSyncDAO.updateAllTransactionFlowFlag(trackingId,dbStatus);

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
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, dbStatus, message, "");
                    transactionLogger.debug("remark message----------------------" + message);
                    transactionLogger.debug("dbStatus message----------------------" + dbStatus);
                }

            }
            JSONObject jsonResObject = new JSONObject();
            jsonResObject.put("responseCode", responseCode);
            jsonResObject.put("responseStatus", returnResStatus);
            res.setContentType("application/json");
            res.setStatus(200);
            pWriter.println(jsonResObject.toString());
            pWriter.flush();
            return;
        }
        catch (Exception e){
            transactionLogger.error("Exception Inside ReproPayBackendServlet============>"+e);
        }


    }

}
