package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.doku.DokuPaymentGateway;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONObject;
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
import java.util.Enumeration;

/**
 * Created by Rihen on 7/27/2018.
 */
public class DokuFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(DokuFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("-----Inside DokuFrontEndServlet -----");

        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = request.getParameter(key);

            transactionLogger.error("Key-----" + key + "----value----" + value);
        }

        StringBuilder responseMsg = new StringBuilder();
        BufferedReader br = request.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }

        transactionLogger.error("----- RESPONSE JSON on DokuFrontEndServlet -----" + responseMsg);

        String req_status = "";
        String updatedStatus = "";
        String toId = "";
        String paymentId = "";
        String payModeId = "";
        String cardTypeId = "";
        String isService = "";
        String accountId = "";
        String responceStatus = "";
        String amount = "";
        String resAmount = "";
        String description = "";
        String orderDescription = "";
        String redirectUrl = "";
        String clKey = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String firstName = "";
        String lastName = "";
        String tmpl_Amount = "";
        String tmpl_Currency = "";
        String ccnum = "";
        String expMonth = "";
        String expYear = "";
        String currency = "";
        String billingDesc = "";
        String message = "";
        String email = "";
        String dbStatus = "";
        String customerId = "";
        String eci = "";
        String trackingId = "";
        String notificationUrl = "";
        String terminalId = "";
        String expDate = "";
        String respPaymentMode = "";
        String rrn = "";
        String requestIp = Functions.getIpAddress(request);

        String status_3d = "";
        String remark = "";

        HttpSession session = request.getSession(true);

        Functions functions = new Functions();
        TransactionManager transactionManager = new TransactionManager();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        ActionEntry entry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        Connection con = null;

        try
        {
            if (functions.isValueNull(request.getParameter("trackingId")))
                trackingId = request.getParameter("trackingId");

            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if(functions.isValueNull(transactionDetailsVO.getTrackingid()))
            {
                transactionLogger.error("=== tracking id ===" + trackingId);
                transactionLogger.error("=== toid ===" + transactionDetailsVO.getToid());
                toId = transactionDetailsVO.getToid();
//            paymentId = transactionDetailsVO.getPaymentId();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                accountId = transactionDetailsVO.getAccountId();
                orderDescription = transactionDetailsVO.getOrderDescription();
                currency = transactionDetailsVO.getCurrency();
                amount = transactionDetailsVO.getAmount();
                dbStatus = transactionDetailsVO.getStatus();
//            tmpl_Amount = transactionDetailsVO.getTemplateamount();
                tmpl_Currency = transactionDetailsVO.getTemplatecurrency();
                payModeId = transactionDetailsVO.getPaymodeId();
                cardTypeId = transactionDetailsVO.getCardTypeId();
                terminalId = transactionDetailsVO.getTerminalId();


                commTransactionDetailsVO.setOrderId(trackingId);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                DokuPaymentGateway dokuPaymentGateway = new DokuPaymentGateway(accountId);
                commResponseVO = (CommResponseVO) dokuPaymentGateway.processInquiry(commRequestVO);

                resAmount = commResponseVO.getAmount();
                tmpl_Amount = commResponseVO.getAmount();
                respPaymentMode = commResponseVO.getArn();
                paymentId = commResponseVO.getTransactionId();
                rrn = commResponseVO.getRrn();
                req_status = commResponseVO.getStatus();
                if (functions.isValueNull(commResponseVO.getRemark()))
                {
                    remark = commResponseVO.getRemark();
                }
                if (functions.isValueNull(transactionDetailsVO.getFirstName()))
                {
                    firstName = transactionDetailsVO.getFirstName();
                }
                if (functions.isValueNull(transactionDetailsVO.getLastName()))
                {
                    lastName = transactionDetailsVO.getLastName();
                }
                if (functions.isValueNull(transactionDetailsVO.getEci()))
                {
                    eci = transactionDetailsVO.getEci();
                }
                if (functions.isValueNull(transactionDetailsVO.getEmailaddr()))
                {
                    email = transactionDetailsVO.getEmailaddr();
                }
                if (functions.isValueNull(transactionDetailsVO.getCustomerId()))
                {
                    customerId = transactionDetailsVO.getCustomerId();
                }
            /*if (functions.isValueNull(transactionDetailsVO.getCcnum()))
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
*/
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);

                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toId);

                if (merchantDetailsVO != null)
                {
                    clKey = merchantDetailsVO.getKey();
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                    isService = merchantDetailsVO.getIsService();
                }
                if(functions.isValueNull(resAmount))
                {
                    amount=resAmount;
                    Double compRsAmount = Double.valueOf(resAmount);
                    Double compDbAmount = Double.valueOf(amount);
                    transactionLogger.error("response amount --->" + compRsAmount);
                    transactionLogger.error(" DB Amount--->" + compDbAmount);
                    if (!compDbAmount.equals(compRsAmount))
                    {
                        req_status = "failed";
                        message = "Failed-IRA";
                    }
                }

                commResponseVO.setTransactionId(paymentId);
                commResponseVO.setCurrency(currency);
                commResponseVO.setAmount(amount);
                commResponseVO.setTmpl_Amount(tmpl_Amount);
                commResponseVO.setTmpl_Currency(tmpl_Currency);
                commResponseVO.setRrn(rrn);
                commResponseVO.setArn(respPaymentMode);

                try
                {
                    StringBuffer sb = new StringBuffer();

                    transactionLogger.error("=== db status ===" + dbStatus);
                    transactionLogger.error("=== transactionID ===" + commResponseVO.getTransactionId());

                    if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus))
                    {

                        if (functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
                        {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                        }

                        sb.append("update transaction_common set ");

                        if ("SUCCESS".equalsIgnoreCase(req_status))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            message = "Transaction Successful";
                            responceStatus = "Successful";

                            commResponseVO.setRemark(remark);
                            commResponseVO.setDescriptor(billingDesc);
                            commResponseVO.setStatus("success");
                            if ("N".equalsIgnoreCase(isService) || "PA".equalsIgnoreCase(transactionDetailsVO.getTransactionType())) // AUTH
                            {
                                transactionLogger.error("--- in Auth , authstarted---");
                                updatedStatus = "authsuccessful";
                                sb.append("remark='" + remark + "'");
                                sb.append(", status='authsuccessful'");
                                sb.append(", arn='" + respPaymentMode + "'");
                                sb.append(", customerIp='" + requestIp + "'");
                                sb.append(", customerIpCountry='" + functions.getIPCountryShort(requestIp) + "'");
                                sb.append(", successtimestamp='" + functions.getTimestamp() + "'");
                                sb.append(" where trackingid = " + trackingId);
                                con = Database.getConnection();
                                transactionLogger.error("common update query DokuFrontEndServlet Notification---" + sb.toString());
                                Database.executeUpdate(sb.toString(), con);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);

                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                            }
                            else // SALE
                            {
                                transactionLogger.error("--- in Sale , capturesuccess ---");
                                updatedStatus = "capturesuccess";
                                sb.append("remark='" + remark + "'");
                                sb.append(", status='capturesuccess'");
                                sb.append(", captureamount='" + amount + "'");
                                sb.append(", arn='" + respPaymentMode + "'");
                                sb.append(", customerIp='" + requestIp + "'");
                                sb.append(", customerIpCountry='" + functions.getIPCountryShort(requestIp) + "'");
                                sb.append(", successtimestamp='" + functions.getTimestamp() + "'");
                                sb.append(" where trackingid = " + trackingId);
                                con = Database.getConnection();
                                transactionLogger.error("common update query DokuFrontEndServlet Notification---" + sb.toString());
                                Database.executeUpdate(sb.toString(), con);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                            }
                        }
                        else if ("failed".equalsIgnoreCase(req_status))
                        {
                            transactionLogger.error("--- in else STATUS failed ---");
                            if(!functions.isValueNull(message))
                                message = "Transaction Declined";
                            responceStatus = "failed";
                            updatedStatus = "authfailed";
                            commResponseVO.setRemark(remark);
                            commResponseVO.setStatus("fail");
                            sb.append("remark='" + remark + "'");
                            sb.append(", status='authfailed'");
                            sb.append(", arn='" + respPaymentMode + "'");
                            sb.append(", customerIp='" + requestIp + "'");
                            sb.append(", customerIpCountry='" + functions.getIPCountryShort(requestIp) + "'");
                            sb.append(", failuretimestamp='" + functions.getTimestamp() + "'");

                            sb.append(" where trackingid = " + trackingId);
                            con = Database.getConnection();
                            transactionLogger.error("common update query DokuFrontEndServlet Notification---" + sb.toString());
                            Database.executeUpdate(sb.toString(), con);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                        }
                        else
                        {
                            responceStatus = "Transaction pending, please check transaction status after sometime";
                            message = "Transaction is pending";
                            updatedStatus = PZTransactionStatus.AUTH_STARTED.toString();
                        }


                    }
                    else
                    {
                        if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            responceStatus = "success";
                            if (functions.isValueNull(transactionDetailsVO.getRemark()))
                                message = transactionDetailsVO.getRemark();
                            else
                                message = "Transaction Successful";
                            updatedStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();

                        }
                        else if (PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            responceStatus = "success";
                            if (functions.isValueNull(transactionDetailsVO.getRemark()))
                                message = transactionDetailsVO.getRemark();
                            else
                                message = "Transaction Successful";
                            updatedStatus = PZTransactionStatus.AUTH_SUCCESS.toString();
                        }
                        else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                        {
                            responceStatus = "fail";
                            if (functions.isValueNull(transactionDetailsVO.getRemark()))
                                message = transactionDetailsVO.getRemark();
                            else if (!functions.isValueNull(message))
                                message = "Transaction Failed";
                            updatedStatus = PZTransactionStatus.AUTH_FAILED.toString();
                        }
                        else
                        {
                            responceStatus = "pending";
                            message = "Transaction is pending";
                            updatedStatus = PZTransactionStatus.AUTH_STARTED.toString();
                        }
                    }


                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setOrderDesc(orderDescription);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                    genericTransDetailsVO.setNotificationUrl(notificationUrl);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                    addressDetailsVO.setEmail(email);
                    addressDetailsVO.setFirstname(firstName);
                    addressDetailsVO.setLastname(lastName);
                    addressDetailsVO.setTmpl_amount(tmpl_Amount);
                    addressDetailsVO.setTmpl_currency(tmpl_Currency);
                    cardDetailsVO.setCardNum(ccnum);
                    cardDetailsVO.setExpMonth(expMonth);
                    cardDetailsVO.setExpYear(expYear);

                    if (session.getAttribute("language") != null)
                    {
                        addressDetailsVO.setLanguage(session.getAttribute("language").toString());
                    }
                    else
                    {
                        addressDetailsVO.setLanguage("");
                    }

                    commonValidatorVO.setTrackingid(trackingId);
                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setPaymentType(payModeId);
                    commonValidatorVO.setCardType(cardTypeId);
                    commonValidatorVO.setCustomerId(customerId);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setEci(eci);
                    commonValidatorVO.setTerminalId(terminalId);
                    commonValidatorVO.setVersion(transactionDetailsVO.getVersion());

                    transactionUtility.setToken(commonValidatorVO, responceStatus);


                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("inside sending notification---" + notificationUrl + "--- for trackingid---" + trackingId);
                        TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, updatedStatus, message, "");
                    }

                    if ("Y".equalsIgnoreCase(autoRedirect))
                    {
                        transactionUtility.doAutoRedirect(commonValidatorVO, response, responceStatus, billingDesc);
                    }
                    else
                    {
                        request.setAttribute("responceStatus", responceStatus);
                        request.setAttribute("displayName", billingDesc);
                        request.setAttribute("remark", message);
                        request.setAttribute("transDetail", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);

                        String confirmationPage = "";
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";

                        RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(request, response);
                    }
                }
                catch (SystemError se)
                {
                    transactionLogger.error("SystemError::::::", se);
                    PZExceptionHandler.raiseAndHandleDBViolationException("DokuFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
                }
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("DokuFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (Exception systemError)
        {
            transactionLogger.error("Exception IN DokuFrontEndServlet::::", systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }


}