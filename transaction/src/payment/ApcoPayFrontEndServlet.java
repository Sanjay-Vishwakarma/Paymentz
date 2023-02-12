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
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.apco.core.ApcoPayUtills;
import com.payment.apco.core.ApcoPaymentGateway;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by ThinkPadT410 on 2/2/2017.
 */
public class ApcoPayFrontEndServlet extends PzServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ApcoPayFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public ApcoPayFrontEndServlet()
    {
        super();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        transactionLogger.error("-----Entering into ApcoPayFrontEndServlet-----");
        HttpSession session = req.getSession(true);
        Functions functions = new Functions();

        Enumeration enumeration = req.getParameterNames();
        while (enumeration.hasMoreElements()){
            String key      = (String)enumeration.nextElement();
            String value    = req.getParameter(key);
            transactionLogger.error("key="+key+"-----value="+value);
        }
        String xmlResponse = req.getParameter("params");
        transactionLogger.error("xmlResponse::::" + xmlResponse);

        if (xmlResponse != null)
        {

            TransactionManager transactionManager       = new TransactionManager();
            CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
            GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
            MerchantDetailsVO merchantDetailsVO         = new MerchantDetailsVO();
            MerchantDAO merchantDAO                     = new MerchantDAO();
            GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
            GenericCardDetailsVO cardDetailsVO          = new GenericCardDetailsVO();
            TransactionUtility transactionUtility       = new TransactionUtility();
            StringBuffer sb     = new StringBuffer();
            ActionEntry entry   = new ActionEntry();
            AuditTrailVO auditTrailVO       = new AuditTrailVO();
            StatusSyncDAO statusSyncDAO     = new StatusSyncDAO();
            CommResponseVO commResponseVO   = new CommResponseVO();
            Connection con                  = null;

            String toid = "";
            String description = "";
            String redirectUrl = "";
            String accountId = "";
            String orderDesc = "";
            String currency = "";

            String autoredirect = "";
            String powerBy = "";
            String displayName = "";
            String logoName = "";
            String partnerName = "";

            String amount = "";
            String trackingId = "";
            String status = "";

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
            String ccnum="";
            String notificationUrl="";
            String message="";
            String version="";
            String dbStatus="";
            String pspid="";
            String captureAmount="";
            String declineReason="";
            String remark="";
            String respStatus="";
            String billingDesc="";
            String confirmStatus="";
            String expDate="";
            String expMonth="";
            String expYear="";
            String RedirectMethod="";


            try
            {
                Map<String, String> stringStringMap     = ApcoPayUtills.readApcopayRedirectionXMLReponse(xmlResponse);
                trackingId  = stringStringMap.get("ORef");
                resultCode  = stringStringMap.get("Result");
                pspid           = stringStringMap.get("pspid");
                captureAmount   = stringStringMap.get("Value");
                declineReason   = stringStringMap.get("ExtendedErr");


                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

                if (transactionDetailsVO != null)
                {
                    toid        = transactionDetailsVO.getToid();
                    description = transactionDetailsVO.getDescription();
                    redirectUrl = transactionDetailsVO.getRedirectURL();
                    accountId   = transactionDetailsVO.getAccountId();
                    orderDesc   = transactionDetailsVO.getOrderDescription();
                    currency    = transactionDetailsVO.getCurrency();
                    amount      = transactionDetailsVO.getAmount();
                    tmpl_amt    = transactionDetailsVO.getTemplateamount();
                    tmpl_currency   = transactionDetailsVO.getTemplatecurrency();
                    firstName       = transactionDetailsVO.getFirstName();
                    lastName        = transactionDetailsVO.getLastName();
                    paymodeid       = transactionDetailsVO.getPaymodeId();
                    cardtypeid      = transactionDetailsVO.getCardTypeId();
                    custId          = transactionDetailsVO.getCustomerId();
                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                    version         = transactionDetailsVO.getVersion();
                    dbStatus        = transactionDetailsVO.getStatus();
                    RedirectMethod  = transactionDetailsVO.getRedirectMethod();


                    if(!functions.isValueNull(captureAmount)){
                        captureAmount = amount;
                    }
                    transactionLogger.debug("amount-----"+captureAmount);

                    if(functions.isValueNull(transactionDetailsVO.getCcnum()))
                    {
                        ccnum= PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                    }
                   /* if(functions.isValueNull(transactionDetailsVO.getExpdate())){
                        expDate = Encryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                        String temp[] = expDate.split("/");

                        if (functions.isValueNull(temp[0]))
                        {
                            expMonth = temp[0];
                        }
                        if (functions.isValueNull(temp[1]))
                        {
                            expYear = temp[1];
                        }
                    }*/

                    transactionLogger.debug("num------"+ccnum);
                    merchantDetailsVO   = merchantDAO.getMemberDetails(toid);
                    autoredirect        = merchantDetailsVO.getAutoRedirect();
                    logoName            = merchantDetailsVO.getLogoName();
                    partnerName         = merchantDetailsVO.getPartnerName();
                    email               = transactionDetailsVO.getEmailaddr();
                    powerBy             = merchantDetailsVO.getPoweredBy();

                    billingDesc         = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                    auditTrailVO.setActionExecutorId(toid);


                    if(PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                    {
                        ApcoPaymentGateway apcoPaymentGateway   = new ApcoPaymentGateway(accountId);
                        CommRequestVO commRequestVO             = new CommRequestVO();
                        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();

                        commTransactionDetailsVO.setOrderId(trackingId);
                        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);

                        if(resultCode.equalsIgnoreCase("OK") || resultCode.equalsIgnoreCase("CAPTURED") || resultCode.equalsIgnoreCase("APPROVED") || resultCode.equalsIgnoreCase("PROCESSED"))
                        {

                            commResponseVO = (CommResponseVO) apcoPaymentGateway.processInquiry(commRequestVO);

                            if (commResponseVO != null)
                            {
                                String respAmount       = "";
                                String resStatus        = commResponseVO.getStatus();
                                String transactionId    = commResponseVO.getTransactionId();

                                if (functions.isValueNull(commResponseVO.getAmount()))
                                {
                                    respAmount = commResponseVO.getAmount();
                                }
                                else
                                {
                                    respAmount = amount;
                                }
                                StringBuffer dbBuffer = new StringBuffer();
                                if ("success".equalsIgnoreCase(resStatus) && functions.isValueNull(transactionId))
                                {
                                    status      = "success";
                                    message     = "Transaction Successful";
                                    respStatus  = "capturesuccess";
                                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                                    commResponseVO.setDescription(message);
                                    commResponseVO.setStatus(status);
                                    commResponseVO.setRemark(message);
                                    commResponseVO.setDescriptor(billingDesc);

                                    confirmStatus = "Y";

                                    dbStatus = "capturesuccess";
                                    dbBuffer.append("update transaction_common set captureamount='" + respAmount + "',paymentid='" + transactionId + "',status='capturesuccess'");
                                    entry.actionEntryForCommon(trackingId, respAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                    dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                                    transactionLogger.error("Update Query---" + dbBuffer);
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
                                    respStatus  = "authstarted";
                                    status      = "pending";
                                    message     = "Transaction Pending";
                                    commResponseVO.setStatus(status);
                                    commResponseVO.setDescription(message);
                                    commResponseVO.setRemark(message);
                                }
                            }
                            else
                            {
                                respStatus  = "authstarted";
                                status      = "pending";
                                message     = "Transaction Pending";
                                commResponseVO.setStatus(status);
                                commResponseVO.setDescription(message);
                                commResponseVO.setRemark(message);
                            }
                        }
                        else
                        {
                            respStatus  = "authstarted";
                            status      = "pending";
                            message     = "Transaction Pending";
                            commResponseVO.setStatus(status);
                            commResponseVO.setDescription(message);
                            commResponseVO.setRemark(message);
                        }
                    }
                    else
                    {
                        if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            respStatus = "capturesuccess";
                            message = "Transaction Successful";

                        }
                        else
                        {
                            respStatus  = "authstarted";
                            status      = "pending";
                            message     = "Transaction Pending";
                            commResponseVO.setStatus(status);
                            commResponseVO.setDescription(message);
                            commResponseVO.setRemark(message);
                        }
                    }
                    commonValidatorVO.setTrackingid(trackingId);
                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                    genericTransDetailsVO.setNotificationUrl(notificationUrl);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                    genericTransDetailsVO.setRedirectMethod(RedirectMethod);

                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    addressDetailsVO.setTmpl_amount(tmpl_amt);
                    addressDetailsVO.setTmpl_currency(tmpl_currency);
                    addressDetailsVO.setFirstname(firstName);
                    addressDetailsVO.setLastname(lastName);
                    addressDetailsVO.setEmail(email);
                    cardDetailsVO.setCardNum(ccnum);
                    /*cardDetailsVO.setExpMonth(expMonth);
                    cardDetailsVO.setExpYear(expYear);*/
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
                    commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());
                    commonValidatorVO.setPaymentType(paymodeid);
                    commonValidatorVO.setCardType(cardtypeid);

                    commonValidatorVO.setCustomerId(custId);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    transactionUtility.setToken(commonValidatorVO, status);
                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("inside sending notification---" + notificationUrl);
                        TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                        transactionDetailsVO1.setTransactionMode("3D");
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, respStatus, message,"");
                    }

                    if ("Y".equalsIgnoreCase(autoredirect))
                    {
                        transactionLogger.error("respStatus in Y---" + status);
                        transactionUtility.doAutoRedirect(commonValidatorVO, res, respStatus, billingDesc);
                    }
                    else
                    {

                        session.setAttribute("ctoken", ctoken);
                        req.setAttribute("transDetail", commonValidatorVO);
                        req.setAttribute("responceStatus", respStatus);
                        req.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());

                        String confirmationPage = "";
                        if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                            confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        else
                            confirmationPage = "/confirmationpage.jsp?ctoken=";
                        RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(req, res);
                    }

                }
            }
            catch (SQLException e)
            {
                transactionLogger.error("SQL Exception in ApcoPayFrontEndServlet---", e);
            }
            catch (SystemError e)
            {
                transactionLogger.error("System Exception in ApcoPayFrontEndServlet---", e);
            }
            catch (NoSuchAlgorithmException e)
            {
                transactionLogger.error("NoSuchAlgorithm Exception in ApcoPayFrontEndServlet---", e);
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("SQL Exception in ApcoPayFrontEndServlet---", e);
            }
            catch (Exception e)
            {
                transactionLogger.error("SQL Exception in ApcoPayFrontEndServlet---", e);
            }finally
            {
                Database.closeConnection(con);
            }
        }
    }
}
