package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.FlutterWave.FlutterWaveUtils;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.xpate.XPatePaymentGateway;
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
import java.util.Enumeration;

/**
 * Created by Jitendra on 13-Dec-19.
 */
public class XPateFrontEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(XPateFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        transactionLogger.error("Inside XPateFrontEndServlet --- ");
        HttpSession session = req.getSession(true);
        Functions functions = new Functions();
        TransactionManager transactionManager = new TransactionManager();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommResponseVO commResponseVO = null;
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        Connection con = null;
        CommRequestVO commRequestVO = null;
        ActionEntry entry = new ActionEntry();
        MerchantDAO merchantDAO = new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        String requestIp=Functions.getIpAddress(req);
        Enumeration enumeration = req.getParameterNames();
        boolean hasElements = enumeration.hasMoreElements();
        transactionLogger.error("XPateFrontEndServlet : hasElements ----" + hasElements);
        String value = "";

        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            value = req.getParameter(key);
            transactionLogger.error("XPateFrontEndServlet : Key = value --- " + key + " = " + value);
        }

        String trackingId=req.getParameter("trackingId");
        String PaRes=req.getParameter("PaRes");
        String MD=req.getParameter("MD");
        String callbackurl=req.getParameter("cb");

        String toid = "";
        String description = "";
        String redirectUrl = "";
        String accountId = "";
        String orderDesc = "";
        String currency = "";
        String amount = "";
        String tmpl_amt = "";
        String tmpl_currency = "";
        String firstName = "";
        String lastName = "";
        String paymodeid = "";
        String cardtypeid = "";
        String custId = "";
       // String paymentid = "";
        String dbStatus = "";
        String notificationUrl = "";
        String clKey = "";
        String logoName = "";
        String partnerName = "";
        String autoRedirect = "";
        String version = "";
        String confirmationPage = "";
        String billingDesc = "";
        String message = "";
        String transactionId = "";
        String responseStatus = "";
        String custEmail = "";
        String terminalId = "";
        String ccnum = "";
        String eci = "";
        String transactionStatus = "";
        String status = "";
        String confirmStatus = "";
        String updatedStatus = "";
        String expDate = "";
        String expMonth = "";
        String expYear = "";
        String customerId = "";
        String payModeId = "";
        String cardTypeId = "";
        String toId = "";
        String paymentId = "";
        String isService = "";
        String street="";
        String zip="";
        String state="";
        String city="";
        String country="";
        String telno="";
        String telcc="";


        try
        {
            transactionLogger.error("XPateFrontEndServlet :  trackingId_response ---------->" + trackingId);
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if (transactionDetailsVO != null)
            {
                transactionLogger.error("inside transactionDetailsVO not null ");
                FlutterWaveUtils flutterWaveUtils=new FlutterWaveUtils();
                String actionExecutorName=flutterWaveUtils.getActionExecutorName(trackingId);
                toId = transactionDetailsVO.getToid();
                paymentId=transactionDetailsVO.getPaymentId();
                transactionLogger.error("paymentId ---"+paymentId);
                accountId = transactionDetailsVO.getAccountId();
                amount = transactionDetailsVO.getAmount();
                transactionLogger.error("amount -------" + amount);
                orderDesc = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                dbStatus = transactionDetailsVO.getStatus();
                payModeId = transactionDetailsVO.getPaymodeId();
                cardTypeId = transactionDetailsVO.getCardTypeId();
                custEmail = transactionDetailsVO.getEmailaddr();
                customerId = transactionDetailsVO.getCustomerId();
                version = transactionDetailsVO.getVersion();
                notificationUrl = transactionDetailsVO.getNotificationUrl();
                terminalId = transactionDetailsVO.getTerminalId();
                firstName=transactionDetailsVO.getFirstName();
                lastName=transactionDetailsVO.getLastName();
                zip = transactionDetailsVO.getZip();
                street = transactionDetailsVO.getStreet();
                state = transactionDetailsVO.getState();
                city = transactionDetailsVO.getCity();
                country = transactionDetailsVO.getCountry();
                telcc = transactionDetailsVO.getTelcc();
                telno = transactionDetailsVO.getTelno();
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                if (merchantDetailsVO != null)
                {
                    clKey = merchantDetailsVO.getKey();
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                    isService = merchantDetailsVO.getIsService();
                }
                billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
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

                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toId);
                if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                {
                    orderDesc = transactionDetailsVO.getOrderDescription();
                }
                currency = transactionDetailsVO.getCurrency();
                transactionLogger.error("dbStatus-----" + dbStatus);
                XPatePaymentGateway xPatePaymentGateway=new XPatePaymentGateway(accountId);

                if ((PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus)) && functions.isValueNull(callbackurl) )
                {
                    transactionLogger.error("XPateFrontEndServlet : Inside AUTH_STARTED ");
                    StringBuffer dbBuffer = new StringBuffer();

                    try
                    {
                        commResponseVO = (CommResponseVO) xPatePaymentGateway.process3DSaleConfirmation(PaRes, MD,callbackurl,commRequestVO);
                        if (commResponseVO != null)
                        {
                            transactionLogger.error("status from process3DSaleConfirmation -----"+commResponseVO.getStatus());
                            if (functions.isValueNull(commResponseVO.getStatus()))
                            {
                                transactionStatus=commResponseVO.getStatus();
                                message=commResponseVO.getRemark();
                            }
                            commResponseVO.setTmpl_Amount(tmpl_amt);
                            commResponseVO.setTmpl_Currency(tmpl_currency);
                            commResponseVO.setIpaddress(requestIp);
                            commResponseVO.setDescription(message);
                            commResponseVO.setRemark(message);
                            commResponseVO.setAmount(amount);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setTransactionId(paymentId);

                            if ("success".equalsIgnoreCase(transactionStatus))
                            {
                                if ("Y".equalsIgnoreCase(isService) && (!functions.isValueNull(transactionDetailsVO.getTransactionType()) || "DB".equalsIgnoreCase(transactionDetailsVO.getTransactionType())))
                                {
                                    transactionLogger.error("inside transactionStatus success");
                                    status = "Successful";
                                    commResponseVO.setDescriptor(billingDesc);
                                    confirmStatus = "Y";
                                    dbStatus = "capturesuccess";
                                    updatedStatus = "capturesuccess";
                                    dbBuffer.append("update transaction_common set captureamount='" + amount + "',status='capturesuccess'");
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);
                                }
                                else
                                {
                                    transactionLogger.error("inside transactionStatus success isService N");
                                    status = "Successful";
                                    commResponseVO.setDescriptor(billingDesc);
                                    confirmStatus = "Y";
                                    dbStatus = "authsuccessful";
                                    updatedStatus = "authsuccessful";
                                    dbBuffer.append("update transaction_common set amount='" + amount + "',status='authsuccessful'");
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);

                                }
                            }
                            else
                            {
                                confirmStatus = "N";
                                status = "fail";
                                dbStatus = "authfailed";
                                updatedStatus="authfailed";
                                dbBuffer.append("update transaction_common set amount='" + amount + "',status='authfailed'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                            }
                            dbBuffer.append(",customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + message + "',paymentid='" + paymentId +"' where trackingid = " + trackingId);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);

                            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                            AsynchronousSmsService smsService = new AsynchronousSmsService();
                            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                        }
                        else
                        {
                            transactionLogger.debug("-----inside pending-----");
                            status = "pending";
                            message = "FE:Transaction is pending";
                            updatedStatus = "pending";
                        }
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
                        message = "Transaction Successful";
                        updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();

                    }
                    else if(PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status = "success";
                        message = "Transaction Successful";
                        updatedStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                    }
                    else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                    {
                        status = "fail";
                        message = "Transaction Failed";
                        updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();

                    }
                    else if(!functions.isValueNull(callbackurl))
                    {
                        status = "pending";
                        message = "Transaction Pending";
                        updatedStatus=PZTransactionStatus.AUTHSTARTED_3D.toString();
                    }
                    else
                    {
                        status = "fail";
                        message = "Transaction Declined";
                        updatedStatus=PZTransactionStatus.FAILED.toString();
                    }
                }

                genericTransDetailsVO.setOrderId(orderDesc);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);

                addressDetailsVO.setEmail(custEmail);
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                addressDetailsVO.setFirstname(firstName);
                addressDetailsVO.setLastname(lastName);
                addressDetailsVO.setCity(city);
                addressDetailsVO.setZipCode(zip);
                addressDetailsVO.setStreet(street);
                addressDetailsVO.setCountry(country);
                addressDetailsVO.setTelnocc(telcc);
                addressDetailsVO.setPhone(telno);
                addressDetailsVO.setState(state);
                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setPaymentType(payModeId);
                commonValidatorVO.setCardType(cardTypeId);
                commonValidatorVO.setTrackingid(trackingId);

                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setCustomerId(customerId);
                commonValidatorVO.setTerminalId(terminalId);
                commonValidatorVO.setReason(message);
                commonValidatorVO.setActionType(actionExecutorName); // Used For Vt Issue

                transactionUtility.setToken(commonValidatorVO, status);
                if (functions.isValueNull(notificationUrl) && ("3D".equals(merchantDetailsVO.getTransactionNotification())||"Both".equals(merchantDetailsVO.getTransactionNotification())))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, updatedStatus, message, "");
                }

                if ("Y".equalsIgnoreCase(autoRedirect))
                {
                    transactionUtility.doAutoRedirect(commonValidatorVO, res, status, billingDesc);
                }
                else
                {
                    transactionLogger.debug("-----inside confirmation page-----");
                    session.setAttribute("ctoken", ctoken);
                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", status);
                    req.setAttribute("remark", message);
                    req.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());

                    if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";
                    RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(req, res);

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
