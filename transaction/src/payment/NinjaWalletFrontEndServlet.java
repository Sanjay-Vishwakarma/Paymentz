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
import com.payment.PZTransactionStatus;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
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
import java.util.Enumeration;

/**
 * Created by Admin on 6/20/2019.
 */
public class NinjaWalletFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(NinjaWalletFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doService(request, response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws  ServletException, IOException
    {
        doService(request,response);
    }
    public void doService(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException
    {
        transactionLogger.error("Inside NinjaWalletFrontEndServlet......!!!");

        Connection connection = null;
        TransactionManager transactionManager = new TransactionManager();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = new GenericCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        MerchantDetailsVO merchantDetailsVO = null;
        ActionEntry actionEntry = new ActionEntry();
        MerchantDAO merchantDAO = new MerchantDAO();
        Functions functions = new Functions();
        Enumeration enumeration = request.getParameterNames();
        boolean hasElements = enumeration.hasMoreElements();
        Comm3DResponseVO comm3DResponseVO1 = new Comm3DResponseVO();
        HttpSession httpSession = request.getSession(true);
        transactionLogger.error("Boolean hasElements......"+hasElements);


        String toId = "",accountId = "", dbStatus = "", status = "", amount = "", description = "", redirectUrl = "",orderDesc="", logoName = "",
                partnerName = "",  responseStatus = "", currency = "", billingDesc = "", custEmail = "", tmpl_amt = "", tmpl_currency = "",
                payModeId = "", cardTypeId = "",  trackingId = "", customerId = "", version = "", notificationUrl = "", terminalId = "",
                autoRedirect = "", message = "", updatedStatus = "",desc="", firstname="", lastname="",ccnum="";

        while(enumeration.hasMoreElements())
        {
            String key= (String) enumeration.nextElement();
            String value = request.getParameter(key);
            transactionLogger.error("Key....."+key +"Value....."+value);
        }
        if (functions.isValueNull(request.getParameter("trackingId")))
        {
            trackingId = request.getParameter("trackingId");
        }
        if (functions.isValueNull(request.getParameter("status")))
        {
            responseStatus= request.getParameter("status");
        }

        transactionLogger.error("TrackingID....."+trackingId);
        transactionLogger.error("ResponseStatus....."+responseStatus);

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
                    terminalId = transactionDetailsVO.getTerminalId();
                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                    currency = transactionDetailsVO.getCurrency();
                    version = transactionDetailsVO.getVersion();
                    firstname=transactionDetailsVO.getFirstName();
                    lastname=transactionDetailsVO.getLastName();
                    billingDesc=transactionDetailsVO.getBillingDesc();
                    ccnum=transactionDetailsVO.getCcnum();
                    if(functions.isValueNull(ccnum))
                    {
                        ccnum= PzEncryptor.decryptPAN(ccnum);
                    }

                    connection = Database.getConnection();

                    merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                    if (merchantDetailsVO != null)
                    {
                        autoRedirect = merchantDetailsVO.getAutoRedirect();
                        partnerName = merchantDetailsVO.getPartnerName();
                        logoName = merchantDetailsVO.getLogoName();
                    }
                    auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                    auditTrailVO.setActionExecutorId(toId);
                    if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                    {
                        orderDesc = transactionDetailsVO.getOrderDescription();
                        transactionLogger.error("orderDesc----"+orderDesc);
                    }

                    transactionLogger.error("dbstatus....." + dbStatus);

                    String action = "";
                    String query = "";

                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && responseStatus.equalsIgnoreCase("success"))
                    {
                        transactionLogger.error("Inside AUTH_STARTED-----------" + responseStatus);
                        status = "success";
                        comm3DResponseVO1.setStatus("success");
                        comm3DResponseVO1.setDescriptor(billingDesc);
                        message = "SYS: Transaction Successful";
                        comm3DResponseVO1.setRemark(message);
                        updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        action = ActionEntry.ACTION_CAPTURE_SUCCESSFUL.toString();
                        query = "update transaction_common set status='" + updatedStatus + "',remark='" + message + "',captureamount='" + amount + "' where trackingid='" + trackingId + "'";
                        transactionLogger.error("-----query-----" + query);
                        Database.executeUpdate(query.toString(), connection);
                        actionEntry.actionEntryForCommon(trackingId, amount, action, updatedStatus, comm3DResponseVO1, auditTrailVO, null);
                    }
                    else if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && responseStatus.equalsIgnoreCase("failed"))
                    {
                        transactionLogger.error("Inside AUTH_STARTED-----------" + responseStatus);
                        status = "fail";
                        comm3DResponseVO1.setStatus("fail");
                        message = "SYS: Transaction Failed";
                        comm3DResponseVO1.setRemark(message);
                        updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        action = ActionEntry.ACTION_AUTHORISTION_FAILED.toString();
                        query = "update transaction_common set status='" + updatedStatus + "',remark='" + message + "',captureamount='" + amount + "' where trackingid='" + trackingId + "'";
                        transactionLogger.error("-----query-----" + query);
                        Database.executeUpdate(query.toString(), connection);
                        actionEntry.actionEntryForCommon(trackingId, amount, action, updatedStatus, comm3DResponseVO1, auditTrailVO, null);
                    }
                    else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus) && responseStatus.equalsIgnoreCase("failed"))
                    {
                        transactionLogger.error("Inside AUTH_FAILED-----------" + responseStatus);
                        status = "fail";
                        message = "SYS: Transaction Failed";
                        comm3DResponseVO1.setRemark(message);
                        updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        action = ActionEntry.ACTION_AUTHORISTION_FAILED.toString();
                        query = "update transaction_common set status='" + updatedStatus + "',remark='" + message + "',captureamount='" + amount + "' where trackingid='" + trackingId + "'";
                        transactionLogger.error("-----query-----" + query);
                        Database.executeUpdate(query.toString(), connection);
                        actionEntry.actionEntryForCommon(trackingId, amount, action, updatedStatus, comm3DResponseVO1, auditTrailVO, null);
                    }
                    else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) && responseStatus.equalsIgnoreCase("success"))
                    {
                        transactionLogger.error("Inside CAPTURE_SUCCESS-----------" + responseStatus);
                        status = "success";
                        comm3DResponseVO1.setStatus("success");
                        comm3DResponseVO1.setDescriptor(billingDesc);
                        message = "SYS: Transaction Successful";
                        comm3DResponseVO1.setRemark(message);
                        updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    }
                    else
                    {
                        status = "fail";
                    }
                    Database.closeConnection(connection);

                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);

                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                    genericTransDetailsVO.setNotificationUrl(notificationUrl);
                    genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                    genericTransDetailsVO.setOrderDesc(orderDesc);

                    genericAddressDetailsVO.setFirstname(firstname);
                    genericAddressDetailsVO.setLastname(lastname);
                    genericAddressDetailsVO.setEmail(custEmail);
                    genericAddressDetailsVO.setTmpl_amount(tmpl_amt);
                    genericAddressDetailsVO.setTmpl_currency(tmpl_currency);
                    genericCardDetailsVO.setCardNum(ccnum);

                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setCardType(cardTypeId);
                    commonValidatorVO.setTrackingid(trackingId);
                    commonValidatorVO.setPaymentType(payModeId);
                    commonValidatorVO.setStatus(status);

                    commonValidatorVO.setCardDetailsVO(genericCardDetailsVO);
                    commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setTerminalId(terminalId);
                    commonValidatorVO.setCustomerId(customerId);

                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("inside sending notification---" + notificationUrl);
                        TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, updatedStatus, message, "");
                    }

                    if ("Y".equalsIgnoreCase(autoRedirect))
                    {
                        if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getRedirectUrl()))
                        {
                            transactionUtility.doAutoRedirect(commonValidatorVO, response, status, billingDesc);
                        }

                        httpSession.setAttribute("ctoken", ctoken);
                        request.setAttribute("transDetail", commonValidatorVO);
                        request.setAttribute("responceStatus", status);
                        request.setAttribute("remark", message);
                        request.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        String confirmationPage = "";

                        if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        {
                            confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        }
                        else
                        {
                            confirmationPage = "/confirmationpage.jsp?ctoken=";
                        }
                        RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(request, response);
                    }
                    else
                    {
                        httpSession.setAttribute("ctoken", ctoken);
                        request.setAttribute("transDetail", commonValidatorVO);
                        request.setAttribute("responceStatus", status);
                        request.setAttribute("remark", message);
                        request.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        String confirmationPage = "";

                        if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        {
                            confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        }
                        else
                        {
                            confirmationPage = "/confirmationpage.jsp?ctoken=";
                        }
                        RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(request, response);
                    }
                }
            }
        }
        catch(SystemError se)
        {
            transactionLogger.error("SystemError.....", se);
            PZExceptionHandler.raiseAndHandleDBViolationException("NinjaWalletFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
        }
        catch (PZDBViolationException tve)
        {
            transactionLogger.error("PZDBViolationException.....", tve);
        }
        finally
        {
            Database.closeConnection(connection);
        }
    }
}
