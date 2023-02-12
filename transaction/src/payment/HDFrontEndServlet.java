package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.hdfc.HDFCPaymentGatewayUtils;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by Admin on 2022-03-08.
 */
public class HDFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionlogger = new TransactionLogger(HDFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req,res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering HDFrontEndServlet ......",e);
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
            transactionlogger.error("Entering HDFrontEndServlet ......",e);
        }
    }

    public void doService(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException, PZDBViolationException
    {
        transactionlogger.error("....Entering HDFrontEndServlet ......");
        HttpSession session = req.getSession();
        Functions functions = new Functions();
        ActionEntry entry                           = new ActionEntry();
        AuditTrailVO auditTrailVO                   = new AuditTrailVO();
        TransactionManager transactionManager       = new TransactionManager();
        CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO         = new MerchantDetailsVO();
        MerchantDAO merchantDAO                     = new MerchantDAO();
        Comm3DResponseVO comm3DResponseVO           = new Comm3DResponseVO();
        GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO          = new GenericCardDetailsVO();
        StatusSyncDAO statusSyncDAO                 = new StatusSyncDAO();
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
        String displayName  = "";
        String isPowerBy    = "";
        String logoName     = "";
        String partnerName  = "";
        String amount       = "";
        String trackingId   = req.getParameter("trackingId");
        String status       = "";
        String remark       = "";

        String bankTransactionStatus = "";
        String name                 = "";
        String email                = "";
        String tmpl_amt             = "";
        String tmpl_currency        = "";
        String firstName            = "";
        String lastName             = "";
        String paymodeid            = "";
        String cardtypeid           = "";
        String custId               = "";
        String transactionStatus    = "";
        String message              = "";
        String billingDesc          = "";
        String dbStatus             = "";
        String notificationUrl      = "";
        String ccnum                = "";
        String expMonth             = "";
        String expYear              = "";
        String requestIp            = "";
        String STATUS               = "";
        String responseAmount       = "";
        String firstSix             = "";
        String lastFour             = "";
        String cardHolderName       = "";
        String terminalId           = "";
        String RRN                  = "";
        String response             = "";
        String xmlResult            = "";
        String xmlAuth              = "";
        String xmlAuthRespCode      = "";
        String xmlRef               = "";
        String xmlAvr               = "";
        String xmlPostdate          = "";
        String xmlPaymentid         = "";
        String xmlTranid            = "";
        String xmlTrackid           = "";
        String tranData             = "";
        String ErrorText            = "";
        String xmlError_code_tag    = "";
        String xmlError_text        = "";


        try
        {
            transactionlogger.error("----- HDFrontEndServlet Response -----"+trackingId);
            Enumeration enumeration = req.getParameterNames();
            while (enumeration.hasMoreElements())
            {
                String key      = (String) enumeration.nextElement();
                String value    = req.getParameter(key);
                transactionlogger.error(trackingId+"---Key---" + key + "---Value---" + value);
            }

            BufferedReader br=req.getReader();
            StringBuffer responseMsg = new StringBuffer();
            String str1;
            while ((str1=br.readLine())!=null)
            {
                responseMsg.append(str1);
            }
            transactionlogger.error("----- HDFrontEndServlet Response -----" + responseMsg);

            if (functions.isValueNull(req.getParameter("trandata")))
            {
                tranData = req.getParameter("trandata");
            }

            if (functions.isValueNull(req.getParameter("ErrorText")))
            {
                ErrorText = req.getParameter("ErrorText");
            }

            if (functions.isValueNull(req.getParameter("paymentid")))
            {
                xmlPaymentid = req.getParameter("paymentid");
            }

            if (functions.isValueNull(req.getParameter("tranid")))
            {
                xmlTranid = req.getParameter("tranid");
            }

            TransactionDetailsVO transactionDetailsVO   = transactionManager.getTransDetailFromCommon(trackingId);
            CommRequestVO requestVO                     = new CommRequestVO();

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

                terminalId = transactionDetailsVO.getTerminalId();
                remark     = transactionDetailsVO.getRemark();

                requestIp = Functions.getIpAddress(req);
                transactionlogger.error("requestIp --- >" + requestIp);
                transactionlogger.error("notificationUrl ---" + notificationUrl);

                if (functions.isValueNull(transactionDetailsVO.getTemplateamount()))
                    tmpl_amt = transactionDetailsVO.getTemplateamount();
                else
                    tmpl_amt = amount;

                if (functions.isValueNull(transactionDetailsVO.getTemplatecurrency()))
                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                else
                    tmpl_currency = currency;

                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                {
                    String expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                    String temp[] = expDate.split("/");

                    if (functions.isValueNull(temp[0]))
                        expMonth = temp[0];

                    if (functions.isValueNull(temp[1]))
                        expYear = temp[1];
                }

                firstName   = transactionDetailsVO.getFirstName();
                lastName    = transactionDetailsVO.getLastName();
                name        = transactionDetailsVO.getFirstName() + " " + transactionDetailsVO.getLastName();

                if(functions.isValueNull(transactionDetailsVO.getName()))
                    cardHolderName = transactionDetailsVO.getName();

                paymodeid       = transactionDetailsVO.getPaymodeId();
                cardtypeid      = transactionDetailsVO.getCardTypeId();
                custId          = transactionDetailsVO.getCustomerId();
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

                auditTrailVO.setActionExecutorName("HDFrontEndServlet");
                auditTrailVO.setActionExecutorId(toid);

                GatewayAccount gatewayAccount          = GatewayAccountService.getGatewayAccount(accountId);
                displayName                            = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                String EncryptionKey                   = gatewayAccount.getCHARGEBACK_FTP_PATH();

                if (functions.isValueNull(tranData) && functions.isValueNull(EncryptionKey))
                {
                    transactionlogger.error("inside if to decrypt response---"+trackingId+"-->"+tranData);
                    response = HDFCPaymentGatewayUtils.decryptText(EncryptionKey,tranData);
                    transactionlogger.error("decrypted response---"+trackingId+"-->"+response);
                    if (functions.isValueNull(response))
                    {
                        Map<String,String> xmlReadedresponseHM = HDFCPaymentGatewayUtils.readHDFCxmlResponse(response);
                        transactionlogger.error(trackingId +"---->xmlReadedresponseHM:"+xmlReadedresponseHM);
                        if (xmlReadedresponseHM != null)
                        {
                            if (xmlReadedresponseHM.containsKey("result") && functions.isValueNull(xmlReadedresponseHM.get("result")))
                            {
                                xmlResult = xmlReadedresponseHM.get("result");
                            }

                            if (xmlReadedresponseHM.containsKey("auth") && functions.isValueNull(xmlReadedresponseHM.get("auth")))
                            {
                                xmlAuth = xmlReadedresponseHM.get("auth");
                            }

                            if (xmlReadedresponseHM.containsKey("authRespCode") && functions.isValueNull(xmlReadedresponseHM.get("authRespCode")))
                            {
                                xmlAuthRespCode = xmlReadedresponseHM.get("authRespCode");
                            }

                            if (xmlReadedresponseHM.containsKey("ref") && functions.isValueNull(xmlReadedresponseHM.get("ref")))
                            {
                                xmlRef = xmlReadedresponseHM.get("ref");
                            }

                            if (xmlReadedresponseHM.containsKey("avr") && functions.isValueNull(xmlReadedresponseHM.get("avr")))
                            {
                                xmlAvr = xmlReadedresponseHM.get("avr");
                            }

                            if (xmlReadedresponseHM.containsKey("postdate") && functions.isValueNull(xmlReadedresponseHM.get("postdate")))
                            {
                                xmlPostdate = xmlReadedresponseHM.get("postdate");
                            }

                            if (xmlReadedresponseHM.containsKey("paymentid") && functions.isValueNull(xmlReadedresponseHM.get("paymentid")))
                            {
                                xmlPaymentid = xmlReadedresponseHM.get("paymentid");
                            }

                            if (xmlReadedresponseHM.containsKey("tranid") && functions.isValueNull(xmlReadedresponseHM.get("tranid")))
                            {
                                xmlTranid = xmlReadedresponseHM.get("tranid");
                            }

                            if (xmlReadedresponseHM.containsKey("trackid") && functions.isValueNull(xmlReadedresponseHM.get("trackid")))
                            {
                                xmlTrackid = xmlReadedresponseHM.get("trackid");
                            }

                            if (xmlReadedresponseHM.containsKey("error_code_tag") && functions.isValueNull(xmlReadedresponseHM.get("error_code_tag")))
                            {
                                xmlError_code_tag = xmlReadedresponseHM.get("error_code_tag");
                            }

                            if (xmlReadedresponseHM.containsKey("error_text") && functions.isValueNull(xmlReadedresponseHM.get("error_text")))
                            {
                                xmlError_text = xmlReadedresponseHM.get("error_text");
                            }
                        }
                    }
                }
                else
                {
                    transactionlogger.error(trackingId + "----> either tranData key_value pair not found in response! or encryption key not found from configurations! or else got ErrorText key_value pair in response.");
                }

                if (!functions.isValueNull(remark) && functions.isValueNull(xmlError_text))
                    remark = xmlError_text;
                else if (!functions.isValueNull(remark) && functions.isValueNull(ErrorText))
                    remark = ErrorText;
                else
                    remark = xmlResult;

                transactionlogger.error("status dbStatus ----> "+ dbStatus);
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                {
                    transactionlogger.error("inside AUTH_STARTED---");
                    StringBuffer dbBuffer = new StringBuffer();

                    commTransactionDetailsVO.setAmount(amount);
                    commTransactionDetailsVO.setCurrency(currency);

                    requestVO.setTransDetailsVO(commTransactionDetailsVO);

                    con = Database.getConnection();

                    comm3DResponseVO.setTmpl_Amount(tmpl_amt);
                    comm3DResponseVO.setTmpl_Currency(tmpl_currency);
                    comm3DResponseVO.setIpaddress(requestIp);

                    if (functions.isValueNull(xmlError_code_tag))
                        comm3DResponseVO.setErrorCode(xmlError_code_tag);
                    else if (functions.isValueNull(ErrorText) && ErrorText.split("-").length > 0 && functions.isValueNull(ErrorText.split("-")[0]))
                        comm3DResponseVO.setErrorCode(ErrorText.split("-")[0]);
                    else
                        comm3DResponseVO.setErrorCode(xmlAuth);

                    comm3DResponseVO.setResponseTime(xmlPostdate);
                    comm3DResponseVO.setTransactionId(xmlTranid);
                    comm3DResponseVO.setResponseHashInfo(xmlTranid);

                    transactionlogger.error("currency--->"+currency);
                    transactionlogger.error("remark--->"+remark);
                    comm3DResponseVO.setRemark(remark);
                    if("CAPTURED".equalsIgnoreCase(xmlResult) || "APPROVED".equalsIgnoreCase(xmlResult))
                    {
                        transactionlogger.error("sucess  front end operation:" +xmlResult);
                        notificationUrl  = transactionDetailsVO.getNotificationUrl();
                        status           = "success";
                        billingDesc      = displayName;
                        message          = remark;
                        comm3DResponseVO.setDescriptor(billingDesc);

                        dbStatus = "capturesuccess";

                        comm3DResponseVO.setStatus(dbStatus);

                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + xmlPaymentid + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency +  "',rrn='" +RRN+ "" +"',status='capturesuccess'" + " ,successtimestamp='" + functions.getTimestamp());
                        dbBuffer.append("',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + message + "' where trackingid = " + trackingId);
                        transactionlogger.error("Update Query---" + dbBuffer);

                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, comm3DResponseVO, auditTrailVO, requestIp);//+ ",rrn='" + rrn
                    }
                    else if((functions.isValueNull(xmlError_code_tag) && functions.isValueNull(xmlError_text)) || functions.isValueNull(ErrorText))  // todo are we have check errorcode or just check not null in errorcode
                    {
                        transactionlogger.error("failed  front end operation: "+ xmlError_code_tag+" && "+ xmlError_text + " || " + ErrorText);
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        status          = "failed";
                        message         = remark;

                        dbStatus = "authfailed";

                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + xmlPaymentid + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "'" + " ,failuretimestamp='" + functions.getTimestamp());
                        dbBuffer.append("',authorization_code='"+xmlError_code_tag+"',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" +message+ "' where trackingid = " + trackingId);
                        transactionlogger.error("Update Query---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, comm3DResponseVO, auditTrailVO, requestIp);
                    }
                    else
                    {
                        //pending
                        dbStatus    = "authstarted";
                        status      = "Pending";
                        message     = "Pending";
                    }
                }
                else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status      = "success";

                        if (functions.isValueNull(remark))
                            message = remark;
                        else if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";

                        dbStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    }
                    else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                    {
                        status   = "failed";

                        if (functions.isValueNull(remark))
                            message = remark;
                        else if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Failed";

                        dbStatus = PZTransactionStatus.AUTH_FAILED.toString();
                    }
                    else
                    {
                        transactionlogger.error("inside HDFrontEndServlet transactionStatus " + transactionStatus);
                        status      = "pending";
                        message     = "Transaction pending";
                        dbStatus    = PZTransactionStatus.AUTH_STARTED.toString();
                    }
                }
                statusSyncDAO.updateAllTransactionFlowFlag(trackingId,dbStatus);

//                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
//                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

//                AsynchronousSmsService smsService = new AsynchronousSmsService();
//                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);

                if (functions.isValueNull(email))
                    addressDetailsVO.setEmail(email);
                else
                    addressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                addressDetailsVO.setFirstname(firstName);
                addressDetailsVO.setLastname(lastName);
                addressDetailsVO.setCardHolderIpAddress(requestIp);

                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);

                commonValidatorVO.setStatus(dbStatus);
                commonValidatorVO.setTrackingid(trackingId);
                commonValidatorVO.setTerminalId(terminalId);
                commonValidatorVO.setReason(message);
                commonValidatorVO.setBankDescription(message);
                commonValidatorVO.setBankCode(xmlAuthRespCode);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);
                commonValidatorVO.setCustomerId(custId);
                commonValidatorVO.setCustomerBankId(custId);
                commonValidatorVO.setAccountId(accountId);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);

                transactionUtility.setToken(commonValidatorVO, dbStatus);

                if (functions.isValueNull(notificationUrl))
                {
                    transactionlogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    transactionDetailsVO1.setBillingDesc(billingDesc);
                    transactionDetailsVO1.setBankReferenceId(xmlPaymentid);
                    transactionDetailsVO1.setRemark(message);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, status, message, "");
                    transactionlogger.error("remark message----------------------" + message);
                }

                if ("Y".equalsIgnoreCase(autoredirect))
                {
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
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    session.invalidate();
                    RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(req, res);
                }
            }
        }
        catch (Exception e)
        {
            transactionlogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("HDFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
    }
}
