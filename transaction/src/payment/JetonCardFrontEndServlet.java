package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.MerchantConfigManager;
import com.manager.TransactionManager;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.luqapay.JetonCardPaymentGateway;
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
import java.sql.Connection;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by Admin on 2/20/2020.
 */
public class JetonCardFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger= new TransactionLogger(JetonCardFrontEndServlet.class.getName());

    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doService(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doService(request,response);
    }

    public void doService(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {
        transactionLogger.error("Inside JetonCardFrontEndServlet");
        Date date4 = new Date();
        HttpSession session = request.getSession(true);
        Functions functions = new Functions();
        TransactionManager transactionManager = new TransactionManager();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommResponseVO transRespDetails = null;
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        Connection con = null;
        ActionEntry entry = new ActionEntry();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();

        Enumeration enumeration = request.getParameterNames();
        boolean hasElements = enumeration.hasMoreElements();
        transactionLogger.error("hasElements ----" + hasElements);
        String value = "";
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            value = request.getParameter(key);
            transactionLogger.error("JetonCardFrontEndServlet Key ---" + key + "--- JetonCardFrontEndServlet value ---" + value);
        }
        String trackingId = request.getParameter("trackingId");
        String statusFromResponse = request.getParameter("status");
        String toid = "";
        String description = "";
        String redirectUrl = "";
        String accountId = "";
        String orderDesc = "";
        String currency = "";
        String amount1 = "";
        String tmpl_amt = "";
        String tmpl_currency = "";
        String firstName = "";
        String lastName = "";
        String paymodeid = "";
        String cardtypeid = "";
        String custId = "";
        String paymentid = "";
        String dbStatus = "";
        String notificationUrl = "";
        String clKey = "";
        String logoName = "";
        String partnerName = "";
        String autoRedirect = "";
        String version = "";
        String confirmationPage = "";
        String billingDesc = "";
        String status = "";
        String message = "";
        String isService = "";
        String transactionId = "";
        String responseStatus = "";
        String custEmail = "";
        String terminalId = "";
        String ccnum = "";
        String expYear = "";
        String expMonth = "";
        String expDate = "";
        String eci = "";
        String amount = "";
        String ipAddress = Functions.getIpAddress(request);

        try
        {
            transactionLogger.error("trackingId ---------->" + trackingId);
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

            if (transactionDetailsVO != null)
            {
                accountId = transactionDetailsVO.getAccountId();
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                toid = transactionDetailsVO.getToid();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                orderDesc = transactionDetailsVO.getOrderDescription();
                currency = transactionDetailsVO.getCurrency();
                amount1 = transactionDetailsVO.getAmount();
                firstName = transactionDetailsVO.getFirstName();
                lastName = transactionDetailsVO.getLastName();
                paymodeid = transactionDetailsVO.getPaymodeId();
                cardtypeid = transactionDetailsVO.getCardTypeId();
                custEmail = transactionDetailsVO.getEmailaddr();
                custId = transactionDetailsVO.getCustomerId();
                paymentid = transactionDetailsVO.getPaymentId();
                dbStatus = transactionDetailsVO.getStatus();
                terminalId = transactionDetailsVO.getTerminalId();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                version = transactionDetailsVO.getVersion();
                ccnum = transactionDetailsVO.getCcnum();
                expDate=transactionDetailsVO.getExpdate();
                amount=transactionDetailsVO.getAmount();
                eci = transactionDetailsVO.getEci();
                billingDesc = gatewayAccount.getDisplayName();
                MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
                merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(toid);
                StringBuffer dbBuffer = new StringBuffer();
                transactionId = paymentid;
                if (merchantDetailsVO != null)
                {
                    clKey = merchantDetailsVO.getKey();
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                    isService = merchantDetailsVO.getIsService();
                }
                CommRequestVO commRequestVO = new CommRequestVO();
                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
                CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
                JetonCardPaymentGateway jetonCardPaymentGateway=new JetonCardPaymentGateway(accountId);

                commTransactionDetailsVO.setPreviousTransactionId(paymentid);
                commTransactionDetailsVO.setOrderId(trackingId);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                commRequestVO.setCommMerchantVO(commMerchantVO);
                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toid);
                con = Database.getConnection();

                transactionLogger.debug("dbStatus -----"+dbStatus);
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus))
                {
                    transactionLogger.error("Inside AUTH_STARTED ");
                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                    transRespDetails = new CommResponseVO();
                    transRespDetails.setIpaddress(ipAddress);
                    CommResponseVO genericResponseVO = null;
                    entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, genericResponseVO, commRequestVO, auditTrailVO, null);
                    genericResponseVO = (CommResponseVO) jetonCardPaymentGateway.processQuery(trackingId, commRequestVO);
                    transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                    String udbStatus=transactionDetailsVO.getStatus();
                    transactionLogger.debug("dbStatus -----"+udbStatus);
                    if (genericResponseVO != null && PZTransactionStatus.AUTH_STARTED.toString().equals(udbStatus))
                    {
                        if ("success".equalsIgnoreCase(genericResponseVO.getStatus()))
                        {
                            status = "success";
                            responseStatus = genericResponseVO.getRemark();
                            message = genericResponseVO.getRemark();
                            transRespDetails.setStatus(status);
                            transRespDetails.setRemark(message);
                            transRespDetails.setDescription(message);
                            transRespDetails.setDescriptor(billingDesc);
                            transRespDetails.setTransactionId(genericResponseVO.getTransactionId());
                            transRespDetails.setTmpl_Amount(tmpl_amt);
                            transRespDetails.setTmpl_Currency(tmpl_currency);
                            transRespDetails.setCurrency(currency);

                            status = "capturesuccess";
                            dbStatus = "capturesuccess";
                            dbBuffer.append("update transaction_common set captureamount='" + amount1 + "',status='capturesuccess',remark='" + message + "',customerIp='"+ipAddress+"',customerIpCountry='"+functions.getIPCountryShort(ipAddress)+"' where trackingid = " + trackingId);
                            transactionLogger.error("update query------------>" + dbBuffer.toString());
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, ipAddress);
                        }else if("pending".equalsIgnoreCase(genericResponseVO.getStatus()))
                        {
                            status = "pending";
                            message = genericResponseVO.getRemark();
                            responseStatus = genericResponseVO.getRemark();
                        }
                        else
                        {
                            billingDesc="";
                            status = "authfailed";
                            message = genericResponseVO.getRemark();
                            responseStatus = genericResponseVO.getRemark();
                            transRespDetails.setRemark(message);
                            transRespDetails.setDescription(message);
                            transRespDetails.setTransactionId(genericResponseVO.getTransactionId());
                            dbBuffer.append("update transaction_common set status='authfailed',remark='" + message + "',customerIp='"+ipAddress+"',customerIpCountry='"+functions.getIPCountryShort(ipAddress)+"' where trackingid = " + trackingId);
                            transactionLogger.error("update query------------>" + dbBuffer.toString());
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, ipAddress);
                        }
                    }

                } else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        message = "Transaction Successful";
                        responseStatus = "success";
                        status=PZTransactionStatus.CAPTURE_SUCCESS.toString();

                    }else if(PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus)){
                        message = "Transaction Successful";
                        responseStatus = "success";
                        status=PZTransactionStatus.AUTH_SUCCESS.toString();
                    }else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus)){
                        message = "Transaction Declined";
                        responseStatus="fail";
                        status=PZTransactionStatus.AUTH_FAILED.toString();
                    }
                    else
                    {
                        message = "Transaction Declined";
                        responseStatus = "Transaction Failed";
                        status=PZTransactionStatus.FAILED.toString();
                    }
                }

                statusSyncDAO.updateAllTransactionFlowFlag(trackingId,dbStatus);
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                genericTransDetailsVO.setAmount(amount1);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                // genericTransDetailsVO.setRedirectMethod(redirectMethod);
                addressDetailsVO.setEmail(custEmail);
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);

                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setCustomerId(custId);
                commonValidatorVO.setTerminalId(terminalId);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);
                commonValidatorVO.setTrackingid(trackingId);
                commonValidatorVO.setStatus(message);

                if (functions.isValueNull(custEmail)){
                    addressDetailsVO.setEmail(custEmail);}
                if (functions.isValueNull(firstName)){
                    addressDetailsVO.setFirstname(firstName);}

                if (functions.isValueNull(lastName)){
                    addressDetailsVO.setLastname(lastName);}
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                cardDetailsVO.setCardNum(PzEncryptor.decryptPAN(ccnum));
                if (functions.isValueNull(expDate))
                {
                    expDate = PzEncryptor.decryptExpiryDate(expDate);
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
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);

                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                // commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setEci(eci);
                transactionUtility.setToken(commonValidatorVO, status);
                if (functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    transactionDetailsVO1.setTransactionMode("3D");
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, status, message, "");
                }
                if ("Y".equalsIgnoreCase(autoRedirect))
                {
                    transactionUtility.doAutoRedirect(commonValidatorVO, response, status, billingDesc);
                }
                else
                {
                    session.setAttribute("ctoken", ctoken);
                    request.setAttribute("transDetail", commonValidatorVO);
                    request.setAttribute("responceStatus", responseStatus);
                    request.setAttribute("remark", message);
                    request.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";
                    RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(request, response);
                }
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException---->",e);
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError---->",se);
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("PZConstraintViolationException---->", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
