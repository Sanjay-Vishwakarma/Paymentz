package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.FlutterWave.FlutterWavePaymentGateway;
import com.payment.FlutterWave.FlutterWaveUtils;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Jitendra on 22-Apr-19.
 */
public class FlutterWaveFrontEndServlet  extends HttpServlet
{
    //private TransactionLogger transactionLogger = new TransactionLogger(FlutterWaveFrontEndServlet.class.getName());
    private FlutterWaveLogger transactionLogger = new FlutterWaveLogger(FlutterWaveFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("Inside FlutterWaveFrontEndServlet -----");
        TransactionManager transactionManager = new TransactionManager();
        ActionEntry entry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        MerchantDetailsVO merchantDetailsVO = null;
        HttpSession session = request.getSession(true);
        MerchantDAO merchantDAO = new MerchantDAO();
        PaymentManager paymentManager = new PaymentManager();
        Functions functions = new Functions();

        String toId = "",accountId = "", dbStatus = "", status = "", amount = "", orderDesc = "", redirectUrl = "", logoName = "",
                partnerName = "",  responseStatus = "", currency = "", billingDesc = "", custEmail = "", tmpl_amt = "", tmpl_currency = "",
                payModeId = "", cardTypeId = "",  firstName = "", lastName = "", trackingId = "", customerId = "", version = "", notificationUrl = "", terminalId = "",
                autoRedirect = "", message = "", updatedStatus = "", transactionStatus="",transactionId="",confirmStatus="",ccnum="",expMonth="",expYear="",expDate="",bankTransactionDate="",orderId="";

        String txRef="",flwRef="",chargeResponseCode="",Validated_chargeResponseCode="",authModelUsed="",vbvrespmessage="";
        String requestIp=Functions.getIpAddress(request);
        Connection con = null;
        String toType               = "";


        Enumeration enumeration = request.getParameterNames();
        boolean hasElements = enumeration.hasMoreElements();
        transactionLogger.debug("hasElements ----" + hasElements);
        String value="";
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            value = request.getParameter(key);
            transactionLogger.error("FlutterWaveFrontEndServlet Key-----" + key + "----FlutterWaveFrontEndServlet value----" + value);
        }

        StringBuilder responseMsg = new StringBuilder();
        BufferedReader br = request.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }

        transactionLogger.error("-----Notification JSON-----" + value);
        transactionLogger.error("-----responseMsg JSON-----" + responseMsg);
        try
        {
            if(value.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(value.toString());
                if (jsonObject != null)
                {
                    if (jsonObject.has("txRef"))
                    {
                        trackingId = jsonObject.getString("txRef");
                        transactionLogger.debug("txRef ---" + trackingId);
                    }
                    if (jsonObject.has("flwRef"))
                    {
                        flwRef = jsonObject.getString("flwRef");
                        transactionId=jsonObject.getString("flwRef");
                        transactionLogger.debug("flwRef ---" + flwRef);
                    }
                    if (jsonObject.has("amount"))
                    {
                        amount = jsonObject.getString("amount");
                        transactionLogger.debug("amount ---" + amount);
                    }
                    if (jsonObject.has("chargeResponseCode"))
                    {
                        chargeResponseCode = jsonObject.getString("chargeResponseCode");
                        transactionLogger.debug("chargeResponseCode ---" + chargeResponseCode);
                    }
                    if (jsonObject.has("authModelUsed"))
                    {
                        authModelUsed = jsonObject.getString("authModelUsed");
                        transactionLogger.debug("authModelUsed ---" + authModelUsed);
                    }
                    if (jsonObject.has("vbvrespmessage"))
                    {
                        vbvrespmessage = jsonObject.getString("vbvrespmessage");
                        transactionLogger.debug("vbvrespmessage ---" + vbvrespmessage);
                        if(vbvrespmessage.contains(":"))
                            vbvrespmessage=vbvrespmessage.split(":")[0];
                        message=vbvrespmessage;
                    }
                    if (jsonObject.has("status"))
                    {
                        transactionStatus = jsonObject.getString("status");
                        transactionLogger.debug("status ---" + transactionStatus);
                    }
                    if (jsonObject.has("createdAt"))
                    {
                        bankTransactionDate= jsonObject.getString("createdAt");
                        transactionLogger.debug("bankTransactionDate ---" + bankTransactionDate);
                    }
                    if(jsonObject.has("data") && !functions.isValueNull(trackingId))
                    {
                        JSONObject data=jsonObject.getJSONObject("data");
                        if (data.has("txRef"))
                        {
                            trackingId = data.getString("txRef");
                            transactionLogger.debug("txRef ---" + trackingId);
                        }else if (data.has("tx_ref"))
                        {
                            trackingId = data.getString("tx_ref");
                            transactionLogger.debug("txRef ---" + trackingId);
                        }
                        if (data.has("flwRef"))
                        {
                            flwRef = data.getString("flwRef");
                            transactionLogger.debug("flwRef ---" + flwRef);
                        }else if (data.has("flw_ref"))
                        {
                            flwRef = data.getString("flw_ref");
                            transactionLogger.debug("flwRef ---" + flwRef);
                        }

                        if (data.has("amount"))
                        {
                            amount = data.getString("amount");
                            transactionLogger.debug("amount ---" + amount);
                        }
                        if (data.has("status"))
                        {
                            status = data.getString("status");
                            transactionLogger.debug("status ---" + status);
                        }
                        if (data.has("currency"))
                        {
                            currency = data.getString("currency");
                            transactionLogger.debug("currency ---" + currency);
                        }
                    }
                }
            }

            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if (transactionDetailsVO != null && functions.isValueNull(transactionDetailsVO.getTrackingid()))
            {
                FlutterWaveUtils flutterWaveUtils=new FlutterWaveUtils();
                String actionExecutorName=flutterWaveUtils.getActionExecutorName(trackingId);
                //actionExecutorName Used For FlutterWave Issue on conformation page to remove continue button in 3D Case.
                // As clicking on continue leads to blank page.
                transactionLogger.debug("actionExecutorName From VT ---"+actionExecutorName);
                toId        = transactionDetailsVO.getToid();
                accountId   = transactionDetailsVO.getAccountId();
                amount      = transactionDetailsVO.getAmount();
                transactionLogger.debug("amount -------"+amount);
                orderId         = transactionDetailsVO.getDescription();
                redirectUrl     = transactionDetailsVO.getRedirectURL();
                tmpl_amt        = transactionDetailsVO.getTemplateamount();
                tmpl_currency   = transactionDetailsVO.getTemplatecurrency();
                dbStatus        = transactionDetailsVO.getStatus();
                payModeId       = transactionDetailsVO.getPaymodeId();
                cardTypeId      = transactionDetailsVO.getCardTypeId();
                custEmail       = transactionDetailsVO.getEmailaddr();
                customerId      = transactionDetailsVO.getCustomerId();
                version         = transactionDetailsVO.getVersion();
                notificationUrl = transactionDetailsVO.getNotificationUrl();
                terminalId      = transactionDetailsVO.getTerminalId();
                firstName       = transactionDetailsVO.getFirstName();
                lastName        = transactionDetailsVO.getLastName();
                toType              = transactionDetailsVO.getTotype();
                if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                {
                    ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                }
                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                {
                    expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
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
                //expMonth=transactionDetailsVO.gete();
               // expYear=cardDetailsVO.getExpYear();
                //transactionLogger.debug("ccnum -----"+ccnum);

                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                if (merchantDetailsVO != null)
                {
                    autoRedirect    = merchantDetailsVO.getAutoRedirect();
                    logoName        = merchantDetailsVO.getLogoName();
                    partnerName     = merchantDetailsVO.getPartnerName();
                }
                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toId);
                if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                {
                    orderDesc = transactionDetailsVO.getOrderDescription();
                }
                currency    = transactionDetailsVO.getCurrency();
                transactionLogger.error("dbStatus-----" + dbStatus);
                Codec me    = new MySQLCodec(MySQLCodec.Mode.STANDARD);
//-------------------------------------------------------------------------------------------------------------------------------------------------

                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus))
                {
                    StringBuffer dbBuffer = new StringBuffer();
                   /* StringBuffer dbBuffer = new StringBuffer();
                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    status = "success";
                    message = "SYS: Transaction Successful";
                    updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();*/

                    //TODO process3DSaleConfirmation
                    CommRequestVO commRequestVO=null;
                    FlutterWavePaymentGateway flutterWavePaymentGateway=new FlutterWavePaymentGateway(accountId);
                    try
                    {
                        CommResponseVO commResponseVO=new CommResponseVO();
                        CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();
                        commRequestVO = new CommRequestVO();
                        commTransactionDetailsVO.setTotype(toType);

                        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);

                        if(!functions.isValueNull(transactionStatus))
                        {
                            commResponseVO = (CommResponseVO) flutterWavePaymentGateway.processCommon3DSaleConfirmation(trackingId, commRequestVO);
                            if (commResponseVO != null)
                            {
                                transactionLogger.debug("Inside FlutterWaveFrontEnd ---");
                                transactionLogger.error("status -----" + commResponseVO.getStatus());
                                transactionLogger.debug("getDescriptor -----" + commResponseVO.getDescriptor());
                                transactionLogger.debug("getDescription -----" + commResponseVO.getDescription());
                                transactionLogger.debug("getAmount -----" + commResponseVO.getAmount());
                                transactionLogger.debug("getRemark -----" + commResponseVO.getRemark());
                                transactionLogger.debug("getCurrency -----" + commResponseVO.getCurrency());
                                transactionLogger.debug("getBankTransactionDate -----" + commResponseVO.getBankTransactionDate());
                                transactionLogger.debug("getEci -----" + commResponseVO.getEci());
                                transactionLogger.debug("getTransactionId -----" + commResponseVO.getTransactionId());
                                transactionLogger.error("getErrorCode -----" + commResponseVO.getErrorCode());
                                transactionLogger.debug("getTransactionStatus -----" + commResponseVO.getTransactionStatus());

                                if (functions.isValueNull(commResponseVO.getStatus()))
                                {
                                    transactionStatus = commResponseVO.getStatus();
                                }
                                if (functions.isValueNull(commResponseVO.getTransactionId()))
                                {
                                    transactionId = commResponseVO.getTransactionId();
                                }
                                if (functions.isValueNull(commResponseVO.getDescription()))
                                {
                                    message = commResponseVO.getDescription();
                                }
                           /* if (functions.isValueNull(commResponseVO.getAmount()))
                            {
                                amount=commResponseVO.getAmount();
                            }*/
                                if (functions.isValueNull(commResponseVO.getCurrency()))
                                {
                                    currency = commResponseVO.getCurrency();
                                }
                                if (functions.isValueNull(commResponseVO.getErrorCode()))
                                {
                                    Validated_chargeResponseCode = commResponseVO.getErrorCode();
                                }
                            }
                        }else
                        {
                            commResponseVO.setTransactionId(transactionId);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setErrorCode(chargeResponseCode);
                            commResponseVO.setBankTransactionDate(bankTransactionDate);
                        }

                            commResponseVO.setTmpl_Amount(tmpl_amt);
                            commResponseVO.setTmpl_Currency(tmpl_currency);
                            commResponseVO.setIpaddress(requestIp);
                            if (("successful".equalsIgnoreCase(transactionStatus))||("success".equalsIgnoreCase(transactionStatus) && "00".equals(Validated_chargeResponseCode)))
                            {
                                status      = "Successful";
                                billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                if(functions.isValueNull(message))
                                {
                                    commResponseVO.setDescription(message);
                                    commResponseVO.setRemark(message);
                                }else
                                {
                                    commResponseVO.setDescription(status);
                                    commResponseVO.setRemark(status);
                                }

                                commResponseVO.setStatus(status);
                                commResponseVO.setDescriptor(billingDesc);

                                confirmStatus   = "Y";
                                dbStatus        = "capturesuccess";
                                updatedStatus   = "capturesuccess";
                                con             = Database.getConnection();
                                dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',remark='" + ESAPI.encoder().encodeForSQL(me,message) + "',successtimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                                Database.executeUpdate(dbBuffer.toString(), con);
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                            }
                            else if("failed".equalsIgnoreCase(transactionStatus) || "fail".equalsIgnoreCase(transactionStatus))
                            {
                                confirmStatus   = "N";
                                status          = "fail";
                                commResponseVO.setStatus(status);
                                commResponseVO.setDescription(message);
                                commResponseVO.setRemark(message);
                                dbStatus            = "authfailed";
                                updatedStatus       = "authfailed";
                                con                 = Database.getConnection();
                                dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "',failuretimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                                Database.executeUpdate(dbBuffer.toString(), con);
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                            }
                            /*AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                            AsynchronousSmsService smsService = new AsynchronousSmsService();
                            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);*/
                        /*}
                        else
                        {
                            transactionLogger.debug("-----inside pending-----");
                            status = "pending";
                            message = "FE:Transaction is pending";
                            updatedStatus = "pending";
                        }*/

                    }
                    catch (PZGenericConstraintViolationException e)
                    {
                        transactionLogger.error("PZGenericConstraintViolationException :::::", e);
                    }
                }
                else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status = "success";
                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";
                        updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();

                    }else if(PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus)){
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status = "success";
                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";
                        updatedStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                    }else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus)){
                        status = "fail";
                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else if(!functions.isValueNull(message))
                            message = "Transaction Failed";
                        updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                        if(functions.isValueNull(trackingId))
                        {
                            con = Database.getConnection();
                            StringBuffer dbBuffer = new StringBuffer();
                            dbBuffer.append("update transaction_common set customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                            Database.executeUpdate(dbBuffer.toString(), con);
                        }

                    }
                    else
                    {
                        status = "fail";
                        message = "Transaction Declined";
                        updatedStatus=PZTransactionStatus.FAILED.toString();

                    }
                }

                genericTransDetailsVO.setOrderId(orderId);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                genericTransDetailsVO.setRedirectMethod(transactionDetailsVO.getRedirectMethod());


                addressDetailsVO.setEmail(custEmail);
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                addressDetailsVO.setFirstname(firstName);
                addressDetailsVO.setLastname(lastName);
                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setPaymentType(payModeId);
                commonValidatorVO.setCardType(cardTypeId);
                commonValidatorVO.setTrackingid(trackingId);
                commonValidatorVO.setReason(message);

                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setCustomerId(customerId);
                commonValidatorVO.setTerminalId(terminalId);
                commonValidatorVO.setActionType(actionExecutorName); // Used For Vt Issue
                transactionUtility.setToken(commonValidatorVO, updatedStatus);

                if (functions.isValueNull(notificationUrl) && functions.isValueNull(updatedStatus))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    transactionDetailsVO1.setTransactionMode("3D");
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, updatedStatus, message, "");
                }

                if ("Y".equalsIgnoreCase(autoRedirect))
                {
                    transactionUtility.doAutoRedirect(commonValidatorVO, response, status, billingDesc);
                }
                else
                {
                    transactionLogger.debug("-----inside confirmation page-----");
                    session.setAttribute("ctoken", ctoken);
                    request.setAttribute("transDetail", commonValidatorVO);
                    request.setAttribute("responceStatus", status);
                    request.setAttribute("remark", message);
                    request.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    String confirmationPage = "";

                    if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";
                    RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(request, response);

                }
            }

        }
        catch(SystemError se)
        {
            transactionLogger.error("SystemError::::::", se);
            PZExceptionHandler.raiseAndHandleDBViolationException("TojikaFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
        }
        catch (PZDBViolationException tve)
        {
            transactionLogger.error("PZDBViolationException:::::", tve);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception:::::", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
