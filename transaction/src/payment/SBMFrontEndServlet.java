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
import com.payment.common.core.CommRequestVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.sbm.core.SBMResponseVO;
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
 * Created by Jitendra on 22-11-19.
 */
public class SBMFrontEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(SBMFrontEndServlet.class.getName());

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
        transactionLogger.error("Inside SBMFrontEndServlet");
        Date date4 = new Date();
        transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction start #########" + date4.getTime());
        HttpSession session = req.getSession(true);
        Functions functions = new Functions();
        TransactionManager transactionManager = new TransactionManager();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        SBMResponseVO transRespDetails = null;
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        Connection con = null;
        CommRequestVO commRequestVO = null;
        ActionEntry entry = new ActionEntry();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();

        Enumeration enumeration = req.getParameterNames();
        boolean hasElements = enumeration.hasMoreElements();
        transactionLogger.error("hasElements ----" + hasElements);
        String value = "";
        String requestIp = Functions.getIpAddress(req);
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            value = req.getParameter(key);
            transactionLogger.error("SBMFrontEndServlet Key ---" + key + "--- SBMFrontEndServlet value ---" + value);
        }
        String trackingId = req.getParameter("trackingId");
        String actionCodeDescription = req.getParameter("ActionCodeDescription");
        String approvalCode = req.getParameter("approvalCode");
        String referenceNumber = req.getParameter("referenceNumber");
        String orderId = req.getParameter("orderId");
        String postDate = req.getParameter("postDate");
        String orderNumber = req.getParameter("OrderNumber");
        String actionCode = req.getParameter("ActionCode");
        String eci = req.getParameter("eci");
        String cvvValidationResult = req.getParameter("cvvValidationResult");
        String orderStatus = req.getParameter("OrderStatus");
        String toid = "";
        String description = "";
        String redirectUrl = "";
        String accountId = "";
        String orderDesc = "";
        String currency = "";
        //String amount = "";
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
        String transactionStatus = "";
        String transactionId = "";
        String confirmStatus = "";
        String responseStatus = "";
        String responseString = "";
        String custEmail = "";
        String terminalId = "";
        String ccnum = "";

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
                dbStatus = transactionDetailsVO.getStatus();
                transactionLogger.debug("dbStatus -----"+dbStatus);
                notificationUrl = transactionDetailsVO.getNotificationUrl();
                terminalId = transactionDetailsVO.getTerminalId();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                version = transactionDetailsVO.getVersion();
                ccnum = transactionDetailsVO.getCcnum();
                eci = transactionDetailsVO.getEci();
                billingDesc = gatewayAccount.getDisplayName();
                MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
                merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(toid);
                StringBuffer dbBuffer = new StringBuffer();
                transactionId = orderId;
                if (merchantDetailsVO != null)
                {
                    clKey = merchantDetailsVO.getKey();
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                    isService = merchantDetailsVO.getIsService();
                }
                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toid);
                con = Database.getConnection();

                if ((PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus) ) && orderStatus.equals("2") && actionCode.equals("0") )
                {
                    transactionLogger.error("Inside AUTH_STARTED ");
                    transRespDetails = new SBMResponseVO();
                    status = "success";
                    responseStatus = "Successful";
                    message = "Transaction Successful";

                    transRespDetails.setStatus(status);
                    if (functions.isValueNull(actionCodeDescription))
                      transRespDetails.setRemark(actionCodeDescription);
                      transRespDetails.setDescription(actionCodeDescription);

                    transRespDetails.setDescriptor(billingDesc);
                    transRespDetails.setTransactionId(transactionId);
                    transRespDetails.setTmpl_Amount(tmpl_amt);
                    transRespDetails.setTmpl_Currency(tmpl_currency);
                    transRespDetails.setCurrency(currency);

                    transRespDetails.setEci(eci);
                    transRespDetails.setCvvValid(cvvValidationResult);
                    transRespDetails.setDepositAmount(amount1);
                    transRespDetails.setPaymentAuthCode(actionCode);
                    transRespDetails.setErrorCode(actionCode);
                    transRespDetails.setProcessingAuthCode(approvalCode);
                    transRespDetails.setAuthCode(approvalCode);
                    transRespDetails.setReferenceNumber(referenceNumber);
                    transRespDetails.setRrn(referenceNumber);
                    transRespDetails.setIpaddress(requestIp);


                    if(isService.equalsIgnoreCase("Y"))
                    {
                        dbStatus = "capturesuccess";

                        dbBuffer.append("update transaction_common set captureamount='" + amount1 + "',paymentid='" + transactionId + "',eci='" + eci + "',status='capturesuccess',remark='" + actionCodeDescription + "',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',authorization_code='"+approvalCode+"',rrn='"+referenceNumber+"' where trackingid = " + trackingId);
                        transactionLogger.error("update query------------>" + dbBuffer.toString());
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, requestIp);
                    }
                    else
                    {
                        dbStatus = "authsuccessful";

                        dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',status='authsuccessful',eci='" + eci + "',remark='" + actionCodeDescription + "',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "',authorization_code='"+approvalCode+"',rrn='"+referenceNumber+"' where trackingid = " + trackingId);
                        transactionLogger.error("update query------------>" + dbBuffer.toString());
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, auditTrailVO, requestIp);
                    }


                }
                else if(PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus))
                {
                    transactionLogger.error("Inside Else Of AUTH_STARTED");
                    transRespDetails = new SBMResponseVO();
                    status = "fail";
                    responseStatus = "Failed";
                    message = "Transaction Failed";
                    dbStatus = "authfailed";

                    transRespDetails.setStatus(status);
                    if (functions.isValueNull(actionCodeDescription))
                        transRespDetails.setRemark(actionCodeDescription);
                    if (functions.isValueNull(transactionId))
                        transRespDetails.setTransactionId(transactionId);
                    transRespDetails.setDescription(actionCodeDescription);

                    transRespDetails.setEci(eci);
                    transRespDetails.setCvvValid(cvvValidationResult);
                    transRespDetails.setDepositAmount(amount1);
                    transRespDetails.setPaymentAuthCode(actionCode);
                    transRespDetails.setProcessingAuthCode(approvalCode);
                    transRespDetails.setReferenceNumber(referenceNumber);
                    transRespDetails.setCurrency(currency);
                    transRespDetails.setTmpl_Amount(tmpl_amt);
                    transRespDetails.setTmpl_Currency(tmpl_currency);
                    transRespDetails.setIpaddress(requestIp);

                    dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',remark='" + actionCodeDescription + "',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' where trackingid = " + trackingId);
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, requestIp);
                }

                statusSyncDAO.updateAllTransactionFlowFlag(trackingId,dbStatus);
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                commonValidatorVO.setTrackingid(trackingId);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setAmount(amount1);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);
                if (functions.isValueNull(custEmail))
                    addressDetailsVO.setEmail(custEmail);
                if (functions.isValueNull(firstName))
                    addressDetailsVO.setFirstname(firstName);

                if (functions.isValueNull(lastName))
                    addressDetailsVO.setLastname(lastName);
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                if (session.getAttribute("language") !=null)
                {
                    addressDetailsVO.setLanguage(session.getAttribute("language").toString());
                }
                else
                {
                    addressDetailsVO.setLanguage("");
                }

                ccnum = PzEncryptor.decryptPAN(ccnum);
                String expDate= PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                String expMonth="";
                String expYear="";
                String temp[]=expDate.split("/");

                if(functions.isValueNull(temp[0]))
                {
                    expMonth=temp[0];
                }
                if(functions.isValueNull(temp[1]))
                {
                    expYear=temp[1];
                }
                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);

                commonValidatorVO.setCustomerId(transactionDetailsVO.getCustomerId());
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setEci(eci);
                commonValidatorVO.setTerminalId(terminalId);
                commonValidatorVO.setVersion(transactionDetailsVO.getVersion());
                commonValidatorVO.setReason(actionCodeDescription);

                commonValidatorVO.setEci(eci);
                transactionUtility.setToken(commonValidatorVO, status);
                if (functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    transactionDetailsVO1.setTransactionMode("3D");
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, dbStatus, actionCodeDescription, "");
                }
                transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction end #########" + new Date().getTime());
                transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction diff #########" + (new Date().getTime() - date4.getTime()));
                if ("Y".equalsIgnoreCase(autoRedirect))
                {
                    transactionUtility.doAutoRedirect(commonValidatorVO, res, status, billingDesc);
                }
                else
                {
                    session.setAttribute("ctoken", ctoken);
                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", responseStatus);
                    req.setAttribute("remark", actionCodeDescription);
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
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException---->"+e);
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError---->"+se);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }

}
