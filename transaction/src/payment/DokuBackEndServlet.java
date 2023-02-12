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
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

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
public class DokuBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(DokuBackEndServlet.class.getName());
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
        transactionLogger.error("-----Inside DokuBackEndServlet -----");

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

        transactionLogger.error("----- RESPONSE JSON on DokuBackEndServlet -----" + responseMsg);


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
        String logoName = "";
        String partnerName = "";
        String firstName = "";
        String lastName = "";
        String tmpl_Amount = "";
        String tmpl_Currency = "";
        String ccnum = "";
        String expMonth="";
        String expYear="";
        String currency = "";
        String billingDesc = "";
        String message = "";
        String email = "";
        String dbStatus = "";
        String customerId = "";
        String eci = "";
        String trackingId="";
        String notificationUrl="";
        String terminalId="";
        String expDate ="";
        String status_3d = "";
        String status = "";
        String arn = "";
        String transactionId ="";
        String remark = "";
        String error_code = "";
        String paymentMode = ""; // mode of payment
        String rrn = "";
        String responseHashInfo = "";
        String bankTransDate = "";

        HttpSession session = request.getSession(true);

        Functions functions = new Functions();
        TransactionManager transactionManager = new TransactionManager();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        CommResponseVO commResponseVO = new CommResponseVO();
        ActionEntry entry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        AbstractPaymentGateway pg = null;
        Connection con = null;

        try
        {
            if(functions.isValueNull(responseMsg.toString()))
            {
                JSONObject jsonObject = new JSONObject(responseMsg.toString());

                if (jsonObject != null) {

                    if (jsonObject.has("order"))
                    {
                        JSONObject orderObject = jsonObject.getJSONObject("order");

                        if (functions.isValueNull(orderObject.getString("invoice_number"))) {
                            trackingId = orderObject.getString("invoice_number");
                        }

                        if (functions.isValueNull(orderObject.getString("amount"))) {
                            resAmount = orderObject.getString("amount");
                            resAmount=String.format("%.2f", Double.parseDouble(resAmount));
                            tmpl_Amount = resAmount;
                        }
                    }

                    if (jsonObject.has("transaction"))
                    {
                        JSONObject jsonTransactionDetail = jsonObject.getJSONObject("transaction");
                        if (jsonTransactionDetail != null)
                        {
                            if (functions.isValueNull(jsonTransactionDetail.getString("status")))
                            {
                                status = jsonTransactionDetail.getString("status");
                            }

                            if (functions.isValueNull(jsonTransactionDetail.getString("date"))) {
                                bankTransDate = jsonTransactionDetail.getString("date");
                            }
                        }
                    }


                    if (jsonObject.has("virtual_account_payment"))
                    {
                        JSONObject virtualAccountObj = jsonObject.getJSONObject("virtual_account_payment");
                        if (virtualAccountObj != null)
                        {
                            if (functions.isValueNull(virtualAccountObj.getString("reference_number")))
                            {
                                rrn = virtualAccountObj.getString("reference_number");
                            }

                            JSONArray identifierArray =virtualAccountObj.getJSONArray("identifier");
                            for (int i = 0; i < identifierArray.length(); i++) {
                                JSONObject identifierDetails = identifierArray.getJSONObject(i);
                                String name = identifierDetails.getString("name");
                                if("TRANSACTION_ID".equalsIgnoreCase(name)){
                                    transactionId = identifierDetails.getString("value");
                                }
                            }
                        }
                    }

                    if(jsonObject.has("channel")){
                        JSONObject channelObj = jsonObject.getJSONObject("channel");

                        if (functions.isValueNull(channelObj.getString("id"))) {
                            paymentMode = channelObj.getString("id");
                        }
                    }

                    // in case of CREDIT CARD only
                    if (jsonObject.has("card_payment"))
                    {
                        JSONObject cardObject = jsonObject.getJSONObject("card_payment");
                        if (functions.isValueNull(cardObject.getString("response_code")))
                        {
                            error_code = cardObject.getString("response_code");
                        }

                        if (functions.isValueNull(cardObject.getString("response_message")))
                        {
                            description = cardObject.getString("response_message");
                        }

                        if (functions.isValueNull(cardObject.getString("masked_card_number")))
                        {
                            responseHashInfo = cardObject.getString("masked_card_number");
                        }
                    }

                    // in case of VIRTUAL ACCOUNT only
                    if (jsonObject.has("virtual_account_info"))
                    {
                        JSONObject virtualAccountObj = jsonObject.getJSONObject("virtual_account_info");
                        if (functions.isValueNull(virtualAccountObj.getString("virtual_account_number")))
                        {
                            responseHashInfo = virtualAccountObj.getString("virtual_account_number");
                        }
                    }
                }
            }


            transactionLogger.error("inside DOKU backend json trackingid = "+trackingId);
            transactionLogger.error("inside DOKU backend json status = "+status);

            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

            toId = transactionDetailsVO.getToid();
            if(!functions.isValueNull(description)) {
                description = transactionDetailsVO.getDescription();
            }
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

            CommRequestVO commRequestVO = new CommRequestVO();
            CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
            commTransactionDetailsVO.setPreviousTransactionId(transactionId);
            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);


            if(functions.isValueNull(transactionDetailsVO.getFirstName())) {
                firstName = transactionDetailsVO.getFirstName();
            }
            if(functions.isValueNull(transactionDetailsVO.getLastName())) {
                lastName = transactionDetailsVO.getLastName();
            }
            if(functions.isValueNull(transactionDetailsVO.getEci())) {
                eci = transactionDetailsVO.getEci();
            }
            if(functions.isValueNull(transactionDetailsVO.getEmailaddr())) {
                email = transactionDetailsVO.getEmailaddr();
            }
            if(functions.isValueNull(transactionDetailsVO.getCustomerId())) {
                customerId = transactionDetailsVO.getCustomerId();
            }
            /*if(functions.isValueNull(transactionDetailsVO.getCcnum())) {
                ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
            }
            if(functions.isValueNull(transactionDetailsVO.getExpdate())) {
                expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                String temp[]=expDate.split("/");

                if(functions.isValueNull(temp[0])) {
                    expMonth=temp[0];
                }
                if(functions.isValueNull(temp[1])) {
                    expYear=temp[1];
                }
            }*/

            merchantDetailsVO = merchantDAO.getMemberDetails(toId);

            auditTrailVO.setActionExecutorName("AcquirerBackEnd");
            auditTrailVO.setActionExecutorId(toId);

            if (merchantDetailsVO != null)
            {
                logoName = merchantDetailsVO.getLogoName();
                partnerName = merchantDetailsVO.getPartnerName();
                isService = merchantDetailsVO.getIsService();
            }

//            commResponseVO.setTransactionId(paymentId);
            commResponseVO.setTransactionId(transactionId);
            commResponseVO.setCurrency(currency);
            commResponseVO.setAmount(amount);
            commResponseVO.setTmpl_Amount(tmpl_Amount);
            commResponseVO.setTmpl_Currency(tmpl_Currency);
            commResponseVO.setArn(paymentMode);
            commResponseVO.setRrn(rrn);
            commResponseVO.setResponseHashInfo(responseHashInfo);
            commResponseVO.setBankTransactionDate(bankTransDate);

            try
            {
                StringBuffer sb = new StringBuffer();

                transactionLogger.error("=== db status ==="+dbStatus);
                transactionLogger.error("=== transactionID ==="+commResponseVO.getTransactionId());
                if(functions.isValueNull(resAmount))
                {
                    amount=resAmount;
                    Double compRsAmount = Double.valueOf(resAmount);
                    Double compDbAmount = Double.valueOf(amount);
                    transactionLogger.error("response amount --->" + compRsAmount);
                    transactionLogger.error(" DB Amount--->" + compDbAmount);
                    if (!compDbAmount.equals(compRsAmount))
                    {
                        status = "failed";
                        message = "Failed-IRA";
                    }
                }

                if(PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus))
                {

                    sb.append("update transaction_common set ");

                    if("SUCCESS".equalsIgnoreCase(status))
                    {
                        if(functions.isValueNull(transactionDetailsVO.getNotificationUrl())) {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                        }
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        message = "Transaction Successful";
                        responceStatus = "Successful";

                        if(!functions.isValueNull(remark)) {
                            remark = message;
                        }
                        commResponseVO.setRemark(remark);
                        commResponseVO.setDescription(message);
                        commResponseVO.setDescriptor(billingDesc);
                        commResponseVO.setStatus("success");
                        if ("N".equalsIgnoreCase(isService) || "PA".equalsIgnoreCase(transactionDetailsVO.getTransactionType())) // AUTH
                        {
                            transactionLogger.error("--- in Auth , authstarted---");
                            updatedStatus = "authsuccessful";
                            sb.append("remark='"+remark+"'");
                            sb.append(", status='authsuccessful'");
                            sb.append(", paymentid='"+transactionId+"'");
                            sb.append(", arn='"+paymentMode+"'");
                            sb.append(", successtimestamp='" + functions.getTimestamp() + "'");

                            sb.append(" where trackingid = " + trackingId);
                            con = Database.getConnection();
                            transactionLogger.error("common update query DokuBackEndServlet Notification---" + sb.toString());
                            Database.executeUpdate(sb.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);

                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                        }
                        else // SALE
                        {
                            if(functions.isValueNull(transactionDetailsVO.getNotificationUrl())) {
                                notificationUrl = transactionDetailsVO.getNotificationUrl();
                            }
                            transactionLogger.error("--- in Sale , capturesuccess ---");
                            updatedStatus = "capturesuccess";
                            sb.append("remark='"+remark+"'");
                            sb.append(", status='capturesuccess'");
                            sb.append(", paymentid='"+transactionId+"'");
                            sb.append(", captureamount="+amount+"");
                            sb.append(", arn='"+paymentMode+"'");
                            sb.append(", successtimestamp='" + functions.getTimestamp() + "'");
                            sb.append(" where trackingid = " + trackingId);
                            con = Database.getConnection();
                            transactionLogger.error("common update query DokuBackEndServlet Notification---" + sb.toString());
                            Database.executeUpdate(sb.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);

                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                        }
                    }
                    else if ("failed".equalsIgnoreCase(status) || "EXPIRED".equalsIgnoreCase(status))
                    {
                        if(functions.isValueNull(transactionDetailsVO.getNotificationUrl())) {
                            notificationUrl = transactionDetailsVO.getNotificationUrl();
                        }
                        transactionLogger.error("--- in else STATUS failed ---");
                        if(!functions.isValueNull(message))
                            message = "Transaction Declined";
                        if(!functions.isValueNull(remark)) {
                            remark = message;
                        }
                        responceStatus = "failed";
                        updatedStatus = "authfailed";
                        commResponseVO.setRemark(remark);
                        commResponseVO.setDescription(message);
                        commResponseVO.setErrorCode(error_code);
                        commResponseVO.setStatus("fail");
                        sb.append("remark='" + remark + "'");
                        sb.append(", status='authfailed'");
                        sb.append(", paymentid='"+transactionId+"'");
                        sb.append(", arn='"+paymentMode+"'");
                        sb.append(", failuretimestamp='"+functions.getTimestamp()+"'");

                        sb.append(" where trackingid = " + trackingId);
                        con = Database.getConnection();
                        transactionLogger.error("common update query DokuBackEndServlet Notification---" + sb.toString());
                        Database.executeUpdate(sb.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);

                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
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

                if (session.getAttribute("language") !=null) {
                    addressDetailsVO.setLanguage(session.getAttribute("language").toString());
                }
                else {
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

                transactionUtility.setToken(commonValidatorVO,responceStatus);


                AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), responceStatus, message, billingDesc);

                AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), responceStatus, message, billingDesc);


                if(functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside DokuBackEndServlet sending notification---"+notificationUrl+"--- for trackingid---"+trackingId);
                    TransactionDetailsVO transactionDetailsVO1=transactionUtility.getTransactionDetails(commonValidatorVO);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1,trackingId,updatedStatus,message,"");
                }
            }

            catch (SystemError se)
            {
                transactionLogger.error("SystemError::::::", se);
                PZExceptionHandler.raiseAndHandleDBViolationException("DokuBackEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("DokuBackEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (Exception systemError)
        {
            transactionLogger.error("Exception In DokuBackEndServlet ::::", systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }


}