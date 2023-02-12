package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.jpbanktransfer.JPBankTransferVO;
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
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
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
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by Sagar on 28-Sep-19.
 */
public class JPBankTransferFrontEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(JPBankTransferFrontEndServlet.class.getName());

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
        transactionLogger.error("Inside JPBankTransferFrontEndServlet");
        Date date4 = new Date();
        transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction start #########" + date4.getTime());
        HttpSession session = req.getSession(true);
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
        CommRequestVO commRequestVO = null;
        ActionEntry entry = new ActionEntry();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        JPBankTransferVO jpBankTransferVO= new JPBankTransferVO();

        Enumeration enumeration = req.getParameterNames();
        boolean hasElements = enumeration.hasMoreElements();
        transactionLogger.error("hasElements ----" + hasElements);
        String value = "";
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            value = req.getParameter(key);
            transactionLogger.error("JPBankTransferFrontEndServlet Key ---" + key + "--- JPBankTransferFrontEndServlet value ---" + value);
        }
        String trackingId = req.getParameter("trackingId");
        String statusFromResponse = req.getParameter("status");
        String bankName = "";
        String shitenName = "";
        String kouzaType = "";
        String kouzaMeigi = "";
        String company="";
        String kouzaNm = "";
        String result="";
        String item="";
        String shitenNm ="";
        String transferId = "";
        String data = "";
        String isTest = "";
        String currency = "";
        String items = "";
        String bid="";
        String tel="";
        String email="";
        String sid = "";
        String password = "";
        String uid = "";
        String amount = "";
        String nameId = "";
        String toid = "";
        String description = "";
        String redirectUrl = "";
        String accountId = "";
        String orderDesc = "";
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
        String eci = "";


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
                transactionId = paymentid;
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

                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && statusFromResponse.equalsIgnoreCase("success"))//this condition changes as per the response we get after transaction
                {
                    transactionLogger.error("Inside AUTH_STARTED ");
                    transRespDetails = new CommResponseVO();
                    status = "success";
                    responseStatus = "Successful";
                    message = "Transaction Successful";
                    dbStatus = "capturesuccess";
                    // transRespDetails.setDescription(ResponseMsg);
                    transRespDetails.setStatus(status);
                    transRespDetails.setRemark("Transaction Successful");
                    transRespDetails.setDescriptor(billingDesc);
                    transRespDetails.setTransactionId(transactionId);
                    transRespDetails.setTmpl_Amount(tmpl_amt);
                    transRespDetails.setTmpl_Currency(tmpl_currency);
                    transRespDetails.setCurrency(currency);


                    dbBuffer.append("update transaction_common set captureamount='" + amount1 + "',paymentid='" + transactionId + "',status='capturesuccess',remark='" + message + "' where trackingid = " + trackingId);
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);

                }
                else if(PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && statusFromResponse.equalsIgnoreCase("fail"))
                {
                    transactionLogger.error("Inside failed");
                    transRespDetails = new CommResponseVO();
                    status = "failed";
                    responseStatus = "failed";
                    message = "Transaction failed";
                    dbStatus = "failed";
                    // transRespDetails.setDescription(ResponseMsg);
                    transRespDetails.setStatus(status);
                    transRespDetails.setRemark("Transaction Failed");
                    dbBuffer.append("update transaction_common set captureamount='" + amount1 + "',paymentid='" + transactionId + "',status='capturefailed',remark='" + message + "' where trackingid = " + trackingId);
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_CAPTURE_FAILED, ActionEntry.STATUS_CAPTURE_FAILED, transRespDetails, auditTrailVO, null);

                }

                // this part is for sending status response to merchant side
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

                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                // commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setEci(eci);
                if (functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, status, message, "");
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
