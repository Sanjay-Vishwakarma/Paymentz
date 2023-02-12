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
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CurrencyCodeISO4217;
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
import java.util.Enumeration;


public class OculusFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(OculusFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
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
        transactionLogger.error("-----Inside OculusFrontEndServlet---");
        Enumeration enumeration = req.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = req.getParameter(key);

            transactionLogger.error("Name=" + key + "-----Value=" + value);
        }
        HttpSession session=req.getSession();
        Functions functions=new Functions();
        TransactionManager transactionManager=new TransactionManager();
        MerchantDAO merchantDAO=new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        CommResponseVO commResponseVO=new CommResponseVO();
        ActionEntry entry=new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        CommonValidatorVO commonValidatorVO=new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO=new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO=new GenericCardDetailsVO();
        TransactionUtility transactionUtility=new TransactionUtility();
        Connection con=null;
        String trackingId = req.getParameter("ticketNumber");
        String responseStatus = req.getParameter("result");
        String message = req.getParameter("resultDetail");
        String paymentId = req.getParameter("transactionID");
        String prevpaymentId = req.getParameter("referenceNumber");
        String respAmount = req.getParameter("amount");
        String currencyCode = req.getParameter("currencyCode");
        String ipAddress = Functions.getIpAddress(req);
        String toId="",accountId="",amount="",orderId="",orderDesc="",redirectUrl="",tmpl_amt="",tmpl_currency="",dbStatus="",payModeId="",cardTypeId="",customerId="",custEmail="",version="",notificationUrl="",
                terminalId="",firstName="",lastName="",ccnum="",expDate="",expMonth="",expYear="",currency="",autoRedirect="",logoName="",partnerName="",isService="",confirmStatus="",status="",billingDesc="",
                updatedStatus="",remark="", TrackingId="";


        transactionLogger.error("trackingId ===== " + trackingId);
        transactionLogger.error("responseStatus ===== " + responseStatus);
        transactionLogger.error("message ===== " + message);
        transactionLogger.error("paymentId ===== " + paymentId);

        try
        {
//            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailsFromCommonByPaymentId(trackingId);
            TransactionDetailsVO transactionDetailsVO   = transactionManager.getTransDetailFromCommon(trackingId);
            if (transactionDetailsVO != null)
            {
                toId = transactionDetailsVO.getToid();
                accountId = transactionDetailsVO.getAccountId();
                amount = transactionDetailsVO.getAmount();
                transactionLogger.error("amount -------" + amount);
                orderId = transactionDetailsVO.getDescription();
                orderDesc = transactionDetailsVO.getOrderDescription();
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
                firstName = transactionDetailsVO.getFirstName();
                lastName = transactionDetailsVO.getLastName();
                remark = transactionDetailsVO.getRemark();
                billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                TrackingId = transactionDetailsVO.getTrackingid();
                if (merchantDetailsVO != null)
                {
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                    isService=merchantDetailsVO.getIsService();
                }
                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toId);
                currency = transactionDetailsVO.getCurrency();
                transactionLogger.error("dbStatus-----" + dbStatus);

                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                {
                    transactionLogger.error("inside authstarted -------");
                    StringBuffer dbBuffer = new StringBuffer();
                    commResponseVO.setIpaddress(ipAddress);
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setCurrency(currency);
                     commResponseVO.setDescription(message);
                    commResponseVO.setRemark(message);
                    if ("Approved".equalsIgnoreCase(responseStatus))
                    {
                        String convertedCurrency = CurrencyCodeISO4217.getAlphaCurrencyCode(req.getParameter("currencyCode"));
                        String convertedAmount = req.getParameter("amount");

                        transactionLogger.error("convertedCurrency ===== " + convertedCurrency);
                        transactionLogger.error("convertedAmount ===== " + convertedAmount);

                        message="Transaction Successful";
                        status="success";
                        commResponseVO.setStatus(status);
                        commResponseVO.setDescriptor(billingDesc);

                        confirmStatus = "Y";
                        dbStatus = "capturesuccess";
                        updatedStatus = "capturesuccess";
                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',status='capturesuccess'" +" ,walletAmount='" + convertedAmount +"' ,walletCurrency='" + convertedCurrency + "'");
                        entry.actionEntryForCommon(TrackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, ipAddress);
                    }
                    else
                    {
                        confirmStatus = "N";
                        status = "Failed("+message+")";
                        commResponseVO.setStatus(status);
                        dbStatus = "authfailed";
                        updatedStatus="authfailed";
                        dbBuffer.append("update transaction_common set status='authfailed'");
                        entry.actionEntryForCommon(TrackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, ipAddress);
                    }
                    dbBuffer.append(" ,paymentId='"+paymentId+"',remark='" + message + "' where trackingid = " + TrackingId);
                    transactionLogger.error("Update transaction --->" + dbBuffer.toString());
                    con = Database.getConnection();
                    Database.executeUpdate(dbBuffer.toString(), con);
                    statusSyncDAO.updateAllTransactionFlowFlag(TrackingId, dbStatus);

                    /*AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(TrackingId), status, message, billingDesc);

                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(TrackingId), status, message, billingDesc);*/
                }
                else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        status = "Successful";
                        message = "Transaction Successful";
                        updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();

                    }else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus)){

                        status = "Failed";
                        message = "Transaction Failed";
                        updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();

                    }
                    else
                    {
                        status = "Failed";
                        message = "Transaction Declined";
                        updatedStatus=PZTransactionStatus.FAILED.toString();

                    }
                }

                genericTransDetailsVO.setOrderId(orderId);
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
                addressDetailsVO.setCardHolderIpAddress(ipAddress);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setPaymentType(payModeId);
                commonValidatorVO.setCardType(cardTypeId);
                commonValidatorVO.setTrackingid(String.valueOf(TrackingId));

                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setCustomerId(customerId);
                commonValidatorVO.setTerminalId(terminalId);
                //commonValidatorVO.setActionType(actionExecutorName); // Used For Vt Issue

                if (functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, TrackingId, updatedStatus, message, "");
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
                    req.setAttribute("displayName", billingDesc);
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
            //e.printStackTrace();
            transactionLogger.error("PZDBViolationException in OculusFrontEndServlet--->",e);
        }
        catch (SystemError systemError)
        {
            // systemError.printStackTrace();
            transactionLogger.error("SystemError in OculusFrontEndServlet--->",systemError);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            transactionLogger.error("Exception in OculusFrontEndServlet--->",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
