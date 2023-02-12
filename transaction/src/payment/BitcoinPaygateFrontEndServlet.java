package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.bitcoinpayget.BitcoinPaygateUtils;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
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
import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by Admin on 19-Jun-19.
 */
public class BitcoinPaygateFrontEndServlet extends PzServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(BitcoinPaygateFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

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
        transactionLogger.error("Inside BitcoinPaygetFrontEndServlet ----");
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
        MerchantDAO merchantDAO = new MerchantDAO();
        PaymentManager paymentManager = new PaymentManager();
        Functions functions = new Functions();
        HttpSession session = req.getSession(true);
        Connection con = null;
        CommResponseVO transRespDetails = new CommResponseVO();
        String toId = "";
        String accountId = "";
        String status = "";
        String query = "";
        ActionEntry actionEntry = new ActionEntry();
        String action = "";
        String amount = "";
        String description = "";
        String redirectUrl = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String confirmStatus = "";
        String orderDesc = "";
        String currency = "";
        String message = "";
        String billingDesc = "";
        String transType = "sale";
        String dbStatus = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String trackingId = "";
        String paymodeid = "";
        String cardtypeid = "";
        String custEmail = "";
        String customerid = "";
        String transactionStatus = "";
        String version = "";
        String notificationUrl = "";
        String terminalid = "";
        String updatedStatus = "";
        String paymentid = "";
        String firstName = "";
        String lastName = "";
        String RedirectMethod   = "";
        String ipAddress        = Functions.getIpAddress(req);

        Enumeration enumeration = req.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String keyName = (String) enumeration.nextElement();
            transactionLogger.error(keyName + ":" + req.getParameter(keyName));
        }

        if (functions.isValueNull(req.getParameter("trackingId")))
        {
            trackingId              = req.getParameter("trackingId");
            String frontEndStatus   = req.getParameter("status");
            //String customerNumber = req.getParameter("customerNumber");
            transactionLogger.error("trackingId::::::" + trackingId);
            transactionLogger.error("frontEndStatus::::::" + frontEndStatus);
            try
            {
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null)
                {
                    transactionLogger.error("inside try ----------------");
                    toId        = transactionDetailsVO.getToid();
                    accountId   = transactionDetailsVO.getAccountId();
                    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                    amount          = transactionDetailsVO.getAmount();
                    description     = transactionDetailsVO.getDescription();
                    redirectUrl     = transactionDetailsVO.getRedirectURL();
                    tmpl_amount     = transactionDetailsVO.getTemplateamount();
                    tmpl_currency   = transactionDetailsVO.getTemplatecurrency();
                    dbStatus        = transactionDetailsVO.getStatus();
                    con             = Database.getConnection();
                    paymodeid       = transactionDetailsVO.getPaymodeId();
                    cardtypeid      = transactionDetailsVO.getCardTypeId();
                    custEmail       = transactionDetailsVO.getEmailaddr();
                    customerid      = transactionDetailsVO.getCustomerId();
                    version         = transactionDetailsVO.getVersion();
                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                    terminalid      = transactionDetailsVO.getTerminalId();
                    firstName       = transactionDetailsVO.getFirstName();
                    lastName        = transactionDetailsVO.getLastName();
                    RedirectMethod  = transactionDetailsVO.getRedirectMethod();
                    transactionLogger.debug("payment id -------------"+transactionDetailsVO.getPaymentId());
                    paymentid       = transactionDetailsVO.getPaymentId();

                    HashMap hashMap = new HashMap();
                    BitcoinPaygateUtils bitcoinPaygateUtils = new BitcoinPaygateUtils();
                    hashMap                                 = bitcoinPaygateUtils.getResponsehashinfo(trackingId);
                    String responceHashInfoAddress          = hashMap.get("responsehashinfo").toString();
                    transactionLogger.error("responceHashInfoAddress ------------ "+responceHashInfoAddress);


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
                        orderDesc = transactionDetailsVO.getOrderDescription();
                    currency = transactionDetailsVO.getCurrency();
                    transactionLogger.error("dbStatus-----" + dbStatus);
                    transRespDetails.setTransactionId(paymentid);
                    transRespDetails.setResponseHashInfo(responceHashInfoAddress);

                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && frontEndStatus.equalsIgnoreCase("confirmed"))
                    {
                        transactionLogger.debug("Inside If AUTH_STARTED && confirmed");
                        status      = "success";
                        transRespDetails.setStatus("success");
                        billingDesc = gatewayAccount.getDisplayName();
                        transRespDetails.setDescriptor(gatewayAccount.getDisplayName());
                        message     = "SYS: Transaction Successful";
                        transRespDetails.setRemark(message);
                        updatedStatus   = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        action          = ActionEntry.ACTION_CAPTURE_SUCCESSFUL.toString();
                        query           = "update transaction_common set status='" + updatedStatus + "',remark='" + message + "',paymentid='" + paymentid + "',templateamount='"+tmpl_amount+"',templatecurrency='"+tmpl_currency+"',captureamount='" + amount + "',customerIp='"+ipAddress+"',customerIpCountry='"+functions.getIPCountryShort(ipAddress)+"' where trackingid='" + trackingId + "'";
                        transactionLogger.error("-----query-----" + query);
                        Database.executeUpdate(query.toString(), con);
                        actionEntry.actionEntryForCommon(trackingId, amount, action, updatedStatus, transRespDetails, auditTrailVO, ipAddress);
                    }
                    else if(PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && (frontEndStatus.equalsIgnoreCase("unconfirmed") || frontEndStatus.equalsIgnoreCase("invalid") || frontEndStatus.equalsIgnoreCase("underpaid")))
                    {
                        transactionLogger.debug("-----inside pending-----");
                        status          = "pending";
                        message         = "SYS:Transaction is pending";
                        updatedStatus   = "authstarted";
                    }
                    else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc     = gatewayAccount.getDisplayName();
                        status          = "success";
                        message         = "Transaction Successful";
                        updatedStatus   = "capturesuccess";
                    }
                    else
                    {
                        status          = "fail";
                        message         = "Transaction Declined";
                        updatedStatus   = "authfailed";
                    }
                   /* else
                    {
                        transactionLogger.debug("-----inside fail-----");
                        status = "fail";
                        message = "SYS: Transaction Failed";
                       // transRespDetails.setStatus("success");
                        transRespDetails.setRemark(message);
                        updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                      //  action = ActionEntry.ACTION_AUTHORISTION_FAILED.toString();
                       // query = "update transaction_common set status='" + updatedStatus + "',remark='" + message + "',paymentid='" + paymentid + "',templateamount='"+tmpl_amount+"',templatecurrency='"+tmpl_currency+"',amount='" + amount + "' where trackingid='" + trackingId + "'";
                      //  Database.executeUpdate(query.toString(), con);
                       // actionEntry.actionEntryForCommon(trackingId, amount, action, updatedStatus, transRespDetails, auditTrailVO, null);
                    }*/

                    transactionLogger.debug("after if and else ---------");
                    Database.closeConnection(con);
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);

                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                    genericTransDetailsVO.setNotificationUrl(notificationUrl);
                    genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                    genericTransDetailsVO.setRedirectMethod(RedirectMethod);

                    addressDetailsVO.setEmail(custEmail);
                    addressDetailsVO.setTmpl_amount(tmpl_amount);
                    addressDetailsVO.setTmpl_currency(tmpl_currency);
                    addressDetailsVO.setFirstname(firstName);
                    addressDetailsVO.setLastname(lastName);
                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setPaymentType(paymodeid);
                    commonValidatorVO.setCardType(cardtypeid);
                    commonValidatorVO.setTrackingid(trackingId);
                    commonValidatorVO.setStatus(message);

                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setCustomerId(customerid);
                    commonValidatorVO.setTerminalId(terminalid);

                    if (functions.isValueNull(notificationUrl) && !"pending".equalsIgnoreCase(status))
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
                        transactionLogger.debug("Inside else ------- --------- -------");
                        session.setAttribute("ctoken", ctoken);
                        req.setAttribute("transDetail", commonValidatorVO);
                        req.setAttribute("responceStatus", status);
                        req.setAttribute("remark", message);
                        req.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        String confirmationPage = "";

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
                transactionLogger.error("PZDBViolationException----"+trackingId+"--->",e);
            }
            catch (SystemError systemError)
            {
                transactionLogger.error("SystemError----" + trackingId + "--->", systemError);
            }
            finally
            {
                Database.closeConnection(con);
            }
        }
    }
}


