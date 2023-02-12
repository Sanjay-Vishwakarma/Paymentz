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
import com.payment.common.core.CommResponseVO;
import com.payment.cupUPI.UnionPayInternationalErrorCode;
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
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by Balaji on 06-Nov-19.
 */
public class PayBoutiqueFrontEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(PayBoutiqueFrontEndServlet.class.getName());
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
        transactionLogger.error("Inside PayBoutiqueFrontEndServlet --->");
        Date date4 = new Date();
        //transactionLogger.debug("PayBoutiqueFrontEndServlet : DirectTransactionRESTIMPL processDirectTransaction start #########" + date4.getTime());

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

        Enumeration enumeration = req.getParameterNames();
        boolean hasElements = enumeration.hasMoreElements();
        transactionLogger.error("PayBoutiqueFrontEndServlet : hasElements ----" + hasElements);
        String value = "";

        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            value = req.getParameter(key);
            transactionLogger.error("PayBoutiqueFrontEndServlet : Key = value --- " + key + " = " + value);
        }

        String trackingId="";
        String status="";
        String merchant_order="";
        String reference_id="";
        String merchant_amount="";
        String merchant_currency="";
        String error_code= "";
        String error_message="";
        if(functions.isValueNull(req.getQueryString())){

            String queryString=req.getQueryString();
            if(queryString.contains("amp;")){
                transactionLogger.debug("PayBoutiqueFrontEndServlet : inside amp; contains if");
                 trackingId=req.getParameter("trackingId");//20191015142142
                 status=req.getParameter("status");//Success
                 merchant_order=req.getParameter("amp;merchant_order");//1510191442
                 reference_id=req.getParameter("amp;reference_id");//5089803
                 merchant_amount=req.getParameter("amp;merchant_amount");//10.00
                 merchant_currency=req.getParameter("amp;merchant_currency");//RUB
                 error_code= req.getParameter("amp;error_code");
                 error_message=req.getParameter("amp;error_message");
            }else{

                transactionLogger.debug("PayBoutiqueFrontEndServlet : inside amp; contains else");
                  trackingId=req.getParameter("trackingId");
                  status=req.getParameter("amp;status");
                  merchant_order=req.getParameter("merchant_order");
                  reference_id=req.getParameter("reference_id");
                  merchant_amount=req.getParameter("merchant_amount");
                  merchant_currency=req.getParameter("merchant_currency");
                  error_code= req.getParameter("error_code");
                  error_message=req.getParameter("error_message");
            }
            transactionLogger.debug("status from front end ------------"+status);
        }

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
        String message = "";
        String transactionId = "";
        String responseStatus = "";
        String custEmail = "";
        String terminalId = "";
        String ccnum = "";
        String eci = "";

        try
        {
            transactionLogger.error("PayBoutiqueFrontEndServlet :  trackingId_response ---------->" + trackingId);
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if (transactionDetailsVO != null)
            {
                accountId = transactionDetailsVO.getAccountId();
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                toid = transactionDetailsVO.getToid();
                orderDesc = transactionDetailsVO.getOrderDescription();
                currency = transactionDetailsVO.getCurrency();
                // if(transactionDetailsVO.getAmount().contains("."))
                // amount = transactionDetailsVO.getAmount().replace(".","");
                amount = transactionDetailsVO.getAmount();
//                tmpl_amt = transactionDetailsVO.getTemplateamount();
//                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                firstName = transactionDetailsVO.getFirstName();
                description = transactionDetailsVO.getDescription();
                lastName = transactionDetailsVO.getLastName();
                paymodeid = transactionDetailsVO.getPaymodeId();
                cardtypeid = transactionDetailsVO.getCardTypeId();
                custEmail = transactionDetailsVO.getEmailaddr();
                custId = transactionDetailsVO.getCustomerId();
                paymentid = transactionDetailsVO.getPaymentId();
                dbStatus = transactionDetailsVO.getStatus();
                transactionLogger.debug("PayBoutiqueFrontEndServlet : dbStatus -----"+dbStatus);
                notificationUrl = transactionDetailsVO.getNotificationUrl();
                terminalId = transactionDetailsVO.getTerminalId();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                version = transactionDetailsVO.getVersion();
                ccnum = transactionDetailsVO.getCcnum();
                eci = transactionDetailsVO.getEci();
                redirectUrl=transactionDetailsVO.getRedirectURL();
                MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
                merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(toid);
                StringBuffer dbBuffer = new StringBuffer();
                transactionId = transactionDetailsVO.getPaymentId();
                if (merchantDetailsVO != null)
                {
                    clKey = merchantDetailsVO.getKey();
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
//                    isService = merchantDetailsVO.getIsService();
                }
                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toid);
                con = Database.getConnection();



                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && status.equalsIgnoreCase("success"))
                {
                    transactionLogger.error("PayBoutiqueFrontEndServlet : Inside AUTH_STARTED ");
                    transRespDetails = new CommResponseVO();
                    billingDesc = gatewayAccount.getDisplayName();
                    status = "success";
                    responseStatus = "Successful";
                    message = "Transaction Successful";
                    dbStatus = "capturesuccess";
                    // transRespDetails.setDescription(ResponseMsg);
                    transRespDetails.setStatus(status);
                    transRespDetails.setRemark("Transaction Successful");
                    transRespDetails.setDescriptor(billingDesc);
                    transRespDetails.setTransactionId(transactionId);

                    dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess',remark='" + message + "' where trackingid = " + trackingId);
                    transactionLogger.error("PayBoutiqueFrontEndServlet : update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                }
                else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                {
                    status = "success";
                    message = "SYS: Transaction Successful";
                    responseStatus = "Successful";
                    dbStatus = "capturesuccess";

                    dbBuffer.append("update transaction_common set status='capturesuccess',remark='"+message+"' where trackingid ='" + trackingId+"'");
                    transactionLogger.error("PayBoutiqueFrontEndServlet : update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                }
                else if(PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && status.equalsIgnoreCase("fail"))
                {
                    transactionLogger.error("PayBoutiqueFrontEndServlet : Inside Else Of AUTH_STARTED");
                    transRespDetails = new CommResponseVO();
                    status = "fail";
                    responseStatus = "Failed";
                    message = "Transaction Failed";
                    dbStatus = "authfailed";
                    //  transRespDetails.setDescription(ResponseMsg);
                    // transRespDetails.setStatus(status);
                    transRespDetails.setRemark("Transaction Failed");
                    // transRespDetails.setTransactionId(transactionId);

                    dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',remark='" + message + "' where trackingid = " + trackingId);
                    transactionLogger.error("PayBoutiqueFrontEndServlet : update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                }
                else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                {
                    status = "fail";
                    message = "SYS: Transaction Failed";
                    responseStatus = "Failed";
                    dbStatus = "authfailed";

                    dbBuffer.append("update transaction_common set status='authfailed',remark='"+message+"' where trackingid ='" + trackingId+"'");
                    transactionLogger.error("PayBoutiqueFrontEndServlet : update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);

                }
                statusSyncDAO.updateAllTransactionFlowFlag(trackingId,dbStatus);
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                genericTransDetailsVO.setAmount(amount);
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
               // cardDetailsVO.setCardNum(Encryptor.decryptPAN(ccnum));

                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                // commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setEci(eci);
                if (functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, dbStatus, message, "");
                }

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
        }catch (PZDBViolationException e)
        {
            transactionLogger.error("PayBoutiqueFrontEndServlet : PZDBViolationException---->"+e);
        }
        catch (SystemError se)
        {
            transactionLogger.error("PayBoutiqueFrontEndServlet : SystemError---->"+se);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
