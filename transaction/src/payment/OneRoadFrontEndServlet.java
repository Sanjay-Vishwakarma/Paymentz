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
import com.payment.OneRoadPayments.OneRoadUtils;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
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

/**
 * Created by Jeet Gupta on 05-04-2019.
 */
public class OneRoadFrontEndServlet extends PzServlet
{
    private static Logger log = new Logger(OneRoadFrontEndServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(OneRoadFrontEndServlet.class.getName());
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
        transactionLogger.error("Inside OneRoadFrontEndServlet -----");
        TransactionManager transactionManager = new TransactionManager();
        Connection con = null;
        ActionEntry entry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        MerchantDetailsVO merchantDetailsVO = null;
        ActionEntry actionEntry = new ActionEntry();
        HttpSession session = req.getSession(true);
        MerchantDAO merchantDAO = new MerchantDAO();
        PaymentManager paymentManager = new PaymentManager();
        Functions functions = new Functions();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        OneRoadUtils oneRoadUtils =new  OneRoadUtils();
        Enumeration enumeration = req.getParameterNames();
        boolean hasElements = enumeration.hasMoreElements();
        transactionLogger.debug("hasElements ----" + hasElements);
        Comm3DResponseVO transRespDetails = null;

        String toId = "",accountId = "", dbStatus = "", status = "", amount = "", description = "", redirectUrl = "",orderDesc="", logoName = "",
                partnerName = "",  responseStatus = "", currency = "", billingDesc = "", custEmail = "", tmpl_amt = "", tmpl_currency = "",
                payModeId = "", cardTypeId = "",  trackingId = "", customerId = "", version = "", notificationUrl = "", terminalId = "",
                autoRedirect = "", message = "", updatedStatus = "",desc="";


        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = req.getParameter(key);
            transactionLogger.error("Key-----" + key + "----value----" + value);
        }
        if (functions.isValueNull(req.getParameter("trackingId")))
        {
            trackingId = req.getParameter("trackingId");
        }
        if (functions.isValueNull(req.getParameter("success")))
        {
            responseStatus = req.getParameter("success");
        }
        try
        {
            {
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null)
                {
                    toId = transactionDetailsVO.getToid();
                    accountId = transactionDetailsVO.getAccountId();
                    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                    amount = transactionDetailsVO.getAmount();
                    description = transactionDetailsVO.getDescription();
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
                    con =Database.getConnection();

                    merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                    if (merchantDetailsVO != null)
                    {
                        autoRedirect = merchantDetailsVO.getAutoRedirect();
                        logoName = merchantDetailsVO.getLogoName();
                        partnerName = merchantDetailsVO.getPartnerName();
                    }
                    auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                    auditTrailVO.setActionExecutorId(toId);
                    if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                    {
                        orderDesc = transactionDetailsVO.getOrderDescription();
                    }
                    currency = transactionDetailsVO.getCurrency();
                    transactionLogger.error("dbStatus-----" + dbStatus);
                    transRespDetails = new Comm3DResponseVO();
                    String action="";
                    String query="";

                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && responseStatus.equalsIgnoreCase("Y"))
                    {
                        status = "success";
                        transRespDetails.setStatus("success");
                        transRespDetails.setDescriptor(gatewayAccount.getDisplayName());
                        message = "SYS: Transaction Successful";
                        transRespDetails.setRemark(message);
                        updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        action=ActionEntry.ACTION_CAPTURE_SUCCESSFUL.toString();
                        query="update transaction_common set status='"+updatedStatus+"',remark='"+message+"',amount='"+amount+"' where trackingid='"+trackingId+"'";
                        transactionLogger.error("-----query-----" + query);
                        Database.executeUpdate(query.toString(), con);
                        actionEntry.actionEntryForCommon(trackingId, amount, action, updatedStatus, transRespDetails, auditTrailVO, null);
                    }
                    else if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && responseStatus.equalsIgnoreCase("N"))
                    {
                        status = "fail";
                        transRespDetails.setStatus("fail");
                        message = "SYS: Transaction Failed";
                        transRespDetails.setRemark(message);
                        updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        action=ActionEntry.ACTION_AUTHORISTION_FAILED.toString();
                        query="update transaction_common set status='"+updatedStatus+"',remark='"+message+"',amount='"+amount+"' where trackingid='"+trackingId+"'";
                        transactionLogger.error("-----query-----" + query);
                        Database.executeUpdate(query.toString(), con);
                        actionEntry.actionEntryForCommon(trackingId, amount, action, updatedStatus, transRespDetails, auditTrailVO, null);
                    }
                    else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus) && responseStatus.equalsIgnoreCase("N"))
                    {
                        status = "fail";
                        message = "SYS: Transaction Failed";
                        transRespDetails.setRemark(message);
                        updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        action=ActionEntry.ACTION_AUTHORISTION_FAILED.toString();
                        query="update transaction_common set status='"+updatedStatus+"',remark='"+message+"',amount='"+amount+"' where trackingid='"+trackingId+"'";
                        transactionLogger.error("-----query-----" + query);
                        Database.executeUpdate(query.toString(), con);
                        actionEntry.actionEntryForCommon(trackingId, amount, action, updatedStatus, transRespDetails, auditTrailVO, null);
                    }
                    else if(PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus)&& responseStatus.equalsIgnoreCase("Y"))
                    {
                        status = "success";
                        transRespDetails.setStatus("success");
                        transRespDetails.setDescriptor(gatewayAccount.getDisplayName());
                        message = "SYS: Transaction Successful";
                        transRespDetails.setRemark(message);
                        updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    }
                    else
                    {
                        status = "fail";
                    }
                    Database.closeConnection(con);
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);

                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                    genericTransDetailsVO.setNotificationUrl(notificationUrl);
                    genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                    addressDetailsVO.setEmail(custEmail);
                    addressDetailsVO.setTmpl_amount(tmpl_amt);
                    addressDetailsVO.setTmpl_currency(tmpl_currency);
                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setPaymentType(payModeId);
                    commonValidatorVO.setCardType(cardTypeId);
                    commonValidatorVO.setTrackingid(trackingId);
                    commonValidatorVO.setStatus(message);

                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setCustomerId(customerId);
                    commonValidatorVO.setTerminalId(terminalId);

                    if (functions.isValueNull(notificationUrl))
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
        }
        catch(SystemError se)
        {
            transactionLogger.error("SystemError::::::", se);
            PZExceptionHandler.raiseAndHandleDBViolationException("OneRoadFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
        }
        catch (PZDBViolationException tve)
        {
            transactionLogger.error("PZDBViolationException:::::", tve);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
