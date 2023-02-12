package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.BlacklistManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.payclub.PayClubPaymentGateway;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by Admin on 12/12/2020.
 */

/**
 * Created by Admin on 11/24/2020.
 */
public class PayClubFrontEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger = new TransactionLogger(PayClubFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("---- Inside PayClubFrontEndServlet ---");
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
        CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();

        /*<html><body onLoad='send.submit();'>
        <form action='http://localhost:8081/transaction/Common3DFrontEndServlet' method='post' name='send'>
        <input type='hidden' name='p_mid' value='81094'>
        <input type='hidden' name='p_account_num' value='40001800'>
        <input type='hidden' name='p_trans_num' value='20210102175319115375688'>
        <input type='hidden' name='p_order_num' value='182491'>
        <input type='hidden' name='p_amount' value='11.00'>
        <input type='hidden' name='p_currency' value='USD'>
        <input type='hidden' name='p_pay_result' value='-1'>
        <input type='hidden' name='p_pay_info' value='processing'>
        <input type='hidden' name='p_signmsg' value='1A2344D75AA180075B1CE7AE09262901C80B198FAE2A6AF9FFCA648E2648CFB1'>
        <input type='hidden' name='p_remark' value='182491'><input type='hidden' name='' value='null'>
        <input type='hidden' name='p_card_num' value='513633***3335'>
        <input type='hidden' name='' value='0'> <input type='hidden' name='ext1' value=''> <input type='hidden' name='ext2' value=''> <input type='hidden' name='flag' value='0'>
        <input type='hidden' name='responseCode' value='R0002'>
        <input type='hidden' name='EbanxBarCode' value='null'>
        </form></body></html>
*/

        String trackingId       = request.getParameter("trackingId");
        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key      = (String) enumeration.nextElement();
            String value    = request.getParameter(key);

            transactionLogger.error("frontend trackingid: "+trackingId + " Name=" + key + " Value=" + value);
        }

        String p_order_num   = request.getParameter("p_order_num");
        String p_pay_result  = request.getParameter("p_pay_result");
        String p_pay_info    = request.getParameter("p_pay_info");
        String responseCode  = request.getParameter("responseCode");
        String paymentId     = request.getParameter("p_trans_num");
        String p_billAddress = request.getParameter("p_billAddress");

        String toid = "";
        String firstSix = "";
        String lastFour = "";
        String cardHolderName = "";
        String currency ="";
        String transactionNum = "";
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
        String remark = "";
        String tmpl_currency = "";
        String orderDesc = "";
        String autoRedirect = "";
        String billingDesc = "";
        String partnerName = "";
        String logoName = "";
        String confirmationPage = "";
        String eci = "";
//        String transactionId = "";
        String STATUS = "";
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
            transactionLogger.error("p_order_num ---------->" + p_order_num);
            transactionLogger.error("p_pay_result ---------->" + p_pay_result);
            transactionLogger.error("p_pay_info ---------->" + p_pay_info);
            transactionLogger.error("responseCode ---------->" + responseCode);
            transactionLogger.error("paymentId ---------->" + paymentId);

            CommRequestVO requestVO = new CommRequestVO();
            commTransactionDetailsVO.setOrderId(trackingId);
            requestVO.setTransDetailsVO(commTransactionDetailsVO);

            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            BlacklistVO blacklistVO = new BlacklistVO();
            BlacklistManager blacklistManager = new BlacklistManager();

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
                transactionLogger.debug("dbAmount -----" + amount1);
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
//                transactionId = paymentId;
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
//                    transRespDetails.setTransactionId(transactionId);
                    transRespDetails.setTmpl_Amount(tmpl_amt);
                    transRespDetails.setTmpl_Currency(tmpl_currency);
                    transRespDetails.setCurrency(currency);

                    PayClubPaymentGateway payClubPaymentGateway = new PayClubPaymentGateway(accountId);
                    CommResponseVO commResponseVO = (CommResponseVO) payClubPaymentGateway.processInquiry(requestVO);

//                    p_pay_result = commResponseVO.getErrorCode();
                    transactionNum = commResponseVO.getTransactionId();
                    currency = commResponseVO.getCurrency();
                    String responseAmount = commResponseVO.getAmount();
                    String errorCode = commResponseVO.getErrorCode();
                    String refundTime = commResponseVO.getBankTransactionDate();
//                    description = commResponseVO.getDescription();
                    String inStatus     = commResponseVO.getStatus();
                    String descriptor   = commResponseVO.getDescriptor();

                    transactionLogger.error("p_trans_num ---" + transactionNum);
                    transactionLogger.error("currency ---" + currency);
                    transactionLogger.error("amount ---" + responseAmount);
                    transactionLogger.error("refundTime ---" + refundTime);
//                    transactionLogger.error("description ---" + description);
                    transactionLogger.error("errorCode ---" + errorCode);

                    if(functions.isValueNull(responseAmount))
                    {
                        Double compRsAmount = Double.valueOf(responseAmount);
                        Double compDbAmount = Double.valueOf(amount1);

                        transactionLogger.error("response amount --->" + compRsAmount);
                        transactionLogger.error(" DB Amount--->" + compDbAmount);

                        if (compDbAmount.equals(compRsAmount))
                        {
                            STATUS = commResponseVO.getStatus();
                            amount1 = commResponseVO.getAmount();
//                            remark = commResponseVO.getRemark();
                        }
                        else if (!compDbAmount.equals(compRsAmount) && "success".equalsIgnoreCase(inStatus))
                        {
                            transactionLogger.error("inside else Amount incorrect--->" + responseAmount);
                            remark = "Failed-IRA";
                            STATUS = "authfailed";
                            amount1 = responseAmount;
                            blacklistVO.setVpaAddress(custId);
                            blacklistVO.setIpAddress(ipAddress);
                            blacklistVO.setEmailAddress(custEmail);
                            blacklistVO.setActionExecutorId(toid);
                            blacklistVO.setActionExecutorName("AcquirerFrontEnd");
                            blacklistVO.setRemark("IncorrectAmount");
                            blacklistVO.setFirstSix(firstSix);
                            blacklistVO.setLastFour(lastFour);
                            blacklistVO.setName(cardHolderName);
                            blacklistManager.commonBlackListing(blacklistVO);
                        }
                    }

                    if ("Success".equalsIgnoreCase(STATUS))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        transactionLogger.error("Inside AUTH_STARTED success");

                        if (functions.isValueNull(descriptor))
                            billingDesc = descriptor;
                        else if (functions.isValueNull(p_billAddress))
                            billingDesc = p_billAddress;
                        else
                            billingDesc = gatewayAccount.getDisplayName();

                        status = "success";
                        if(functions.isValueNull(p_pay_info))
                        {
                            message=p_pay_info;
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

                        dbBuffer.append("update transaction_common set captureamount='" + amount1 + "',paymentid='" + transactionNum + "',status='capturesuccess',remark='" + message + "',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "',successtimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                        transactionLogger.error("update query------------>" + dbBuffer.toString());
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
//                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                    }
                    else if("Failed".equalsIgnoreCase(STATUS))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        transactionLogger.error("Inside AUTH_STARTED ");

                        status = "failed";
                        if(functions.isValueNull(p_pay_info))
                        {
                            message=p_pay_info;
                        }
                        else
                        {
                            message = "Transaction failed";
                        }

                        dbStatus = "authfailed";
                        transRespDetails.setStatus(status);
                        transRespDetails.setRemark(message);
                        transRespDetails.setDescription(description);


                        dbBuffer.append("update transaction_common set paymentid='" + transactionNum + "',status='authfailed',remark='" + message + "',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "',failuretimestamp='" + functions.getTimestamp() + "' where trackingid = " + trackingId);
                        transactionLogger.error("update query------------>" + dbBuffer.toString());
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount1, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
//                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                    }
                    else
                    {
                        billingDesc = gatewayAccount.getDisplayName();
                        status = "pending";

                        if(functions.isValueNull(p_pay_info))
                        {
                            message=p_pay_info;
                        }
                        else
                        {
                            message = "Transaction pending";
                        }
                        dbStatus = "pending";

                        transactionLogger.error("inside Payclub pending condition --->"+STATUS+"--"+message);
                    }
                }
                else if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                {
                    if (functions.isValueNull(p_billAddress))
                        billingDesc = p_billAddress;
                    else
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

                else
                {
                    status      = "pending";
                    message     = "Transaction pending";
                    dbStatus    = PZTransactionStatus.AUTH_STARTED.toString();
                }
                transactionLogger.error("payclub frontend billingDesc--"+trackingId+"-->+" + billingDesc);
                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);

                /*AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                */

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
                transactionUtility.setToken(commonValidatorVO, status);

                if(!functions.isValueNull(notificationUrl) && functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                    notificationUrl = merchantDetailsVO.getNotificationUrl();
                }

                if (functions.isValueNull(notificationUrl)&& !status.equalsIgnoreCase("pending"))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    transactionDetailsVO1.setBillingDesc(billingDesc);
                    transactionDetailsVO1.setBankReferenceId(transactionNum);
                    transactionDetailsVO1.setTransactionMode("3D");
                    payment.AsyncNotificationService asyncNotificationService = payment.AsyncNotificationService.getInstance();
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
            transactionLogger.error("PZDBViolationException---->" , e);
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError---->" , se);
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