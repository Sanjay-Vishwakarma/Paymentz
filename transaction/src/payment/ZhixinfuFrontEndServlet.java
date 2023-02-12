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
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by Admin on 11/24/2020.
 */
public class ZhixinfuFrontEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger = new TransactionLogger(ZhixinfuFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("---- Inside ZhixinfuFrontEndServlet ---");
        PrintWriter printWriter = response.getWriter();
        HttpSession session = request.getSession(true);
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        TransactionManager transactionManager = new TransactionManager();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        Functions functions = new Functions();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        CommResponseVO transRespDetails = new CommResponseVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();

        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = request.getParameter(key);

            transactionLogger.error("Name=" + key + "-----Value=" + value);
        }

        String trackingId = request.getParameter("trackingId");
        String tradeStatus = request.getParameter("tradeStatus");
        String remark=request.getParameter("remark");
        String code=request.getParameter("code");
        String paymentId=request.getParameter("sysOrdId");
        String toid = "";
        String currency = "";
        String dbStatus = "";
        String notificationUrl = "";
        String accountId = "";
        String firstName = "";
        String lastName = "";
        String paymodeid = "";
        String cardtypeid = "";
        String redirectUrl = "";
        String ccnum = "";
        String expDate = "";
        String expMonth = "";
        String expYear = "";
        String status = "";
        String message = "";
        String terminalId = "";
        String tmpl_amt = "";
        String tmpl_currency = "";
        String orderDesc = "";
        String autoRedirect = "";
        String billingDesc = "";
        String partnerName = "";
        String logoName = "";
        String confirmationPage = "";
        String eci = "";
        String transactionId = "";
        String description = "";
        String amount1 = "";
        String custId = "";
        String ipAddress = Functions.getIpAddress(request);
        String custEmail = "";
        Connection con = null;
        ActionEntry entry = new ActionEntry();
        StringBuilder responseMsg = new StringBuilder();

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
                transactionLogger.debug("dbStatus -----" + dbStatus);
                terminalId = transactionDetailsVO.getTerminalId();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
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
                    eci = transactionDetailsVO.getEci();


                    merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                    StringBuffer dbBuffer = new StringBuffer();
                    transactionId = paymentId;
                    if (merchantDetailsVO != null)
                    {
                        autoRedirect = merchantDetailsVO.getAutoRedirect();
                        logoName = merchantDetailsVO.getLogoName();
                        partnerName = merchantDetailsVO.getPartnerName();
                    }
                    auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                    auditTrailVO.setActionExecutorId(toid);
                    con = Database.getConnection();

                    if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                    {
                        transRespDetails = new CommResponseVO();
                        transRespDetails.setTransactionId(transactionId);
                        transRespDetails.setTmpl_Amount(tmpl_amt);
                        transRespDetails.setTmpl_Currency(tmpl_currency);
                        transRespDetails.setCurrency(currency);

                        if (tradeStatus.equalsIgnoreCase("completed"))
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            transactionLogger.error("Inside AUTH_STARTED ");

                            billingDesc = gatewayAccount.getDisplayName();
                            status = "success";
                            if(functions.isValueNull(remark))
                            {
                                message=remark;
                            }
                            else
                            {
                                message = "Transaction Successful";
                            }

                            dbStatus = "capturesuccess";
                            transRespDetails.setStatus(status);
                            transRespDetails.setRemark(message);
                            transRespDetails.setDescriptor(billingDesc);
                            transRespDetails.setDescription(description);


                            dbBuffer.append("update transaction_common set captureamount='" + amount1 + "',paymentid='" + transactionId + "',status='capturesuccess',remark='" + message + "',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "' where trackingid = " + trackingId);
                            transactionLogger.error("update query------------>" + dbBuffer.toString());
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                        }
                        else
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                            transactionLogger.error("Inside AUTH_STARTED ");

                            status = "failed";
                            if(functions.isValueNull(remark))
                            {
                                message=remark;
                            }
                            else
                            {
                                message = "Transaction failed";
                            }

                            dbStatus = "authfailed";
                            transRespDetails.setStatus(status);
                            transRespDetails.setRemark(message);
                            transRespDetails.setDescription(description);


                            dbBuffer.append("update transaction_common set paymentid='" + transactionId + "',status='authfailed',remark='" + message + "',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "' where trackingid = " + trackingId);
                            transactionLogger.error("update query------------>" + dbBuffer.toString());
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                        }
                    }
                    else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = gatewayAccount.getDisplayName();
                        status = "success";
                        message = "SYS: Transaction Successful";
                        dbStatus = "capturesuccess";

                        dbBuffer.append("update transaction_common set customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "' where trackingid ='" + trackingId + "'");
                        transactionLogger.error("update query------------>" + dbBuffer.toString());
                        Database.executeUpdate(dbBuffer.toString(), con);
                    }
                   else if (PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.FAILED.toString().equalsIgnoreCase(dbStatus))
                    {
                        status = "authfailed";
                        message = "SYS: Transaction authfailed";
                        dbStatus = "authfailed";
                        message = "Transaction authfailed";

                        dbBuffer.append("update transaction_common set status='authfailed',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "' where trackingid ='" + trackingId + "'");
                        transactionLogger.error("update query------------>" + dbBuffer.toString());
                        Database.executeUpdate(dbBuffer.toString(), con);
                    }



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

                    if (functions.isValueNull(custEmail))
                    {
                        addressDetailsVO.setEmail(custEmail);
                    }
                    if (functions.isValueNull(firstName))
                    {
                        addressDetailsVO.setFirstname(firstName);
                    }

                    if (functions.isValueNull(lastName))
                    {
                        addressDetailsVO.setLastname(lastName);
                    }
                    addressDetailsVO.setTmpl_amount(tmpl_amt);
                    addressDetailsVO.setTmpl_currency(tmpl_currency);
                    cardDetailsVO.setCardNum(ccnum);
                    cardDetailsVO.setExpMonth(expMonth);
                    cardDetailsVO.setExpYear(expYear);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setEci(eci);
                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("inside sending notification---" + notificationUrl);
                        TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                        transactionDetailsVO1.setBillingDesc(billingDesc);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, status, message, "");
                    }
                    transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction end #########" + new Date().getTime());
                    if ("Y".equalsIgnoreCase(autoRedirect))
                    {
                        transactionUtility.doAutoRedirect(commonValidatorVO, response, dbStatus, billingDesc);
                    }
                    else
                    {
                        session.setAttribute("ctoken", ctoken);
                        request.setAttribute("transDetail", commonValidatorVO);
                        request.setAttribute("responceStatus", dbStatus);
                        request.setAttribute("remark", message);
                        request.setAttribute("displayName", billingDesc);
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                        RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(request, response);
                    }
                }

        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException---->" + e);
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError---->" + se);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception--" + trackingId + "-->", e);
        }

        finally
        {
            Database.closeConnection(con);
        }


    }

}