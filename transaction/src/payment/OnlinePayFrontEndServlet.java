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
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
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
 * Created by Rihen on 10-Aug-19.
 */
public class OnlinePayFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(OnlinePayFrontEndServlet.class.getName());
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
        transactionLogger.error("-----inside OnlinePayFrontEndServlet-----");
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

        Enumeration enumeration = req.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String keyName = (String) enumeration.nextElement();
            transactionLogger.error(keyName + ":" + req.getParameter(keyName));
        }

        if (functions.isValueNull(req.getParameter("trackingId")))
        {
            trackingId = req.getParameter("trackingId");
            String frontEndStatus = req.getParameter("status");
            transactionLogger.error("trackingId::::::" + trackingId);
            transactionLogger.error("frontEndStatus::::::" + frontEndStatus);
            try
            {
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null)
                {
                    transactionLogger.error("inside try ----------------");
                    toId = transactionDetailsVO.getToid();
                    accountId = transactionDetailsVO.getAccountId();
                    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                    amount = transactionDetailsVO.getAmount();
                    description = transactionDetailsVO.getDescription();
                    redirectUrl = transactionDetailsVO.getRedirectURL();
                    tmpl_amount = transactionDetailsVO.getTemplateamount();
                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                    dbStatus = transactionDetailsVO.getStatus();
                    con =Database.getConnection();
                    paymodeid = transactionDetailsVO.getPaymodeId();
                    cardtypeid = transactionDetailsVO.getCardTypeId();
                    custEmail = transactionDetailsVO.getEmailaddr();
                    customerid = transactionDetailsVO.getCustomerId();
                    version = transactionDetailsVO.getVersion();
                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                    terminalid = transactionDetailsVO.getTerminalId();
                    firstName=transactionDetailsVO.getFirstName();
                    lastName=transactionDetailsVO.getLastName();
                    transactionLogger.debug("payment id -------------"+transactionDetailsVO.getPaymentId());
                    paymentid=transactionDetailsVO.getPaymentId();


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
                        orderDesc = transactionDetailsVO.getOrderDescription();
                    currency = transactionDetailsVO.getCurrency();
                    transRespDetails.setTransactionId(paymentid);
                    transactionLogger.error("dbStatus-----" + dbStatus);
                    transactionLogger.error("frontEndStatus-----" + frontEndStatus);

                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && frontEndStatus.equalsIgnoreCase("success"))
                    {
                        transactionLogger.debug("-----inside if status -----"+frontEndStatus);
                        status = "success";
                        transRespDetails.setStatus("success");
                        transRespDetails.setDescriptor(gatewayAccount.getDisplayName());
                        message = "SYS: Transaction Successful";
                        transRespDetails.setRemark(message);
                        updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        action = ActionEntry.ACTION_CAPTURE_SUCCESSFUL.toString();
                        query = "update transaction_common set status='" + updatedStatus + "',remark='" + message + "',paymentid='" + paymentid + "',templateamount='"+tmpl_amount+"',templatecurrency='"+tmpl_currency+"',captureamount='" + amount + "' where trackingid='" + trackingId + "'";
                        transactionLogger.error("-----query-----" + query);
                        Database.executeUpdate(query.toString(), con);
                        actionEntry.actionEntryForCommon(trackingId, amount, action, updatedStatus, transRespDetails, auditTrailVO, null);
                    }
                    else if(PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && frontEndStatus.equalsIgnoreCase("fail"))
                    {
                        transactionLogger.debug("-----inside else if status -----"+frontEndStatus);
                        status = "fail";
                        transRespDetails.setStatus("failed");
                        transRespDetails.setDescriptor(gatewayAccount.getDisplayName());
                        message = "SYS:Transaction Declined";
                        transRespDetails.setRemark(message);
                        updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        action = ActionEntry.ACTION_CAPTURE_SUCCESSFUL.toString();
                        query = "update transaction_common set status='" + updatedStatus + "',remark='" + message + "',paymentid='" + paymentid + "',templateamount='"+tmpl_amount+"',templatecurrency='"+tmpl_currency+"' where trackingid='" + trackingId + "'";
                        transactionLogger.error("-----query-----" + query);
                        Database.executeUpdate(query.toString(), con);
                        actionEntry.actionEntryForCommon(trackingId, amount, action, updatedStatus, transRespDetails, auditTrailVO, null);
                    }
                    else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        status = "success";
                        message = "Transaction Successful";
                        updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    }
                    else
                    {
                        status = "fail";
                        message = "Transaction Declined";
                        updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                    }


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
                        transactionLogger.debug("Inside else auto redirect NO ------- --------- -------");
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
                transactionLogger.error("PZDBViolationException  ::::: ", e);
            }
            catch (SystemError systemError)
            {
                transactionLogger.error("SystemError  ::::: ", systemError);
            }
            finally
            {
                Database.closeConnection(con);
            }
        }


    }
}

