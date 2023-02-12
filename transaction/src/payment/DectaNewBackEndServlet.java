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
import com.payment.dectaNew.DectaNewPaymentGateway;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.sms.AsynchronousSmsService;
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
public class DectaNewBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(DectaNewBackEndServlet.class.getName());
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
        transactionLogger.error("-----Inside DectaNewBackEndServlet -----");

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

        transactionLogger.error("----- RESPONSE JSON on DectaNewBackEnd-----" + responseMsg);


        String updatedStatus = "";
        String toId = "";
        String paymentId = "";
        String payModeId = "";
        String cardTypeId = "";
        String isService = "";
        String accountId = "";
        String responceStatus = "";
        String amount = "";
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
                    if (jsonObject.has("referrer")) {
                        if (functions.isValueNull(jsonObject.getString("referrer"))) {
                            trackingId = jsonObject.getString("referrer");
                        }
                    }

                    if (jsonObject.has("id")) {
                        if (functions.isValueNull(jsonObject.getString("id"))) {
                            transactionId = jsonObject.getString("id");
                        }
                    }

                    if (jsonObject.has("transaction_details"))
                    {
                        JSONObject jsonTransactionDetail = jsonObject.getJSONObject("transaction_details");
                        if (jsonTransactionDetail != null) {
                            if (functions.isValueNull(jsonTransactionDetail.getString("arn"))) {
                                arn = jsonTransactionDetail.getString("arn");
                                commResponseVO.setArn(arn);
                            }

                            if (jsonTransactionDetail.has("status_3d_secure")) {
                                if(functions.isValueNull(jsonTransactionDetail.getString("status_3d_secure"))) {
                                    status_3d = jsonTransactionDetail.getString("status_3d_secure");
                                    commResponseVO.setAuthCode(status_3d);
                                }
                            }

                            if (jsonTransactionDetail.has("three_d_secure_status")) {
                                JSONObject jsonThreeDDetail = jsonTransactionDetail.getJSONObject("three_d_secure_status");
                                if (functions.isValueNull(jsonThreeDDetail.getString("description"))) {
                                    description = (jsonThreeDDetail.getString("description"));
                                }
                                if (functions.isValueNull(jsonThreeDDetail.getString("mpi_status_code"))) {
                                    commResponseVO.setResponseHashInfo(jsonThreeDDetail.getString("mpi_status_code"));
                                }
                            }

                            if (jsonTransactionDetail.has("processing_status"))
                            {
                                JSONObject jsonProcessingStatus = jsonTransactionDetail.getJSONObject("processing_status");
                                if (functions.isValueNull(jsonProcessingStatus.getString("description"))) {
                                    remark = (jsonProcessingStatus.getString("description"));
                                }

                                if (functions.isValueNull(jsonProcessingStatus.getString("code"))) {
                                    error_code = (jsonProcessingStatus.getString("code"));
                                }
                            }
                        }
                    }

                    if (jsonObject.has("status")) {
                        status = jsonObject.getString("status");
                    }
                }
            }


            transactionLogger.error("inside decta new backend json trackingid = "+trackingId);
            transactionLogger.error("inside decta new backend json status = "+status);

            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

            toId = transactionDetailsVO.getToid();
//            paymentId = transactionDetailsVO.getPaymentId();
            if(functions.isValueNull(description)) {
                description = transactionDetailsVO.getDescription();
            }
            redirectUrl = transactionDetailsVO.getRedirectURL();
            accountId = transactionDetailsVO.getAccountId();
            orderDescription = transactionDetailsVO.getOrderDescription();
            currency = transactionDetailsVO.getCurrency();
            amount = transactionDetailsVO.getAmount();
            dbStatus = transactionDetailsVO.getStatus();
            tmpl_Amount = transactionDetailsVO.getTemplateamount();
            tmpl_Currency = transactionDetailsVO.getTemplatecurrency();
            payModeId = transactionDetailsVO.getPaymodeId();
            cardTypeId = transactionDetailsVO.getCardTypeId();
            terminalId = transactionDetailsVO.getTerminalId();

            CommRequestVO commRequestVO = new CommRequestVO();
            CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
//            commTransactionDetailsVO.setPreviousTransactionId(paymentId);
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
            if(functions.isValueNull(transactionDetailsVO.getCcnum())) {
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
            }

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

            try
            {
                StringBuffer sb = new StringBuffer();

                transactionLogger.error("=== db status ==="+dbStatus);
                transactionLogger.error("=== transactionID ==="+commResponseVO.getTransactionId());

                if(PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus))
                {
                    if(functions.isValueNull(transactionDetailsVO.getNotificationUrl())) {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                    }

                    sb.append("update transaction_common set ");

                    if("paid".equalsIgnoreCase(status) || "hold".equalsIgnoreCase(status))
                    {
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

                            sb.append(" where trackingid = " + trackingId);
                            con = Database.getConnection();
                            transactionLogger.error("common update query DectaFrontendNotification---" + sb.toString());
                            Database.executeUpdate(sb.toString(), con);

                            if(status_3d.equalsIgnoreCase("true"))
                            {
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, commResponseVO, auditTrailVO, null);
                            }
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);

                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                        }
                        else // SALE
                        {
                            transactionLogger.error("--- in Sale , capturesuccess ---");
                            updatedStatus = "capturesuccess";
                            sb.append("remark='"+remark+"'");
                            sb.append(", status='capturesuccess'");
                            sb.append(", paymentid='"+transactionId+"'");
                            sb.append(", captureamount="+amount+"");

                            sb.append(" where trackingid = " + trackingId);
                            con = Database.getConnection();
                            transactionLogger.error("common update query DectaFrontendNotification---" + sb.toString());
                            Database.executeUpdate(sb.toString(), con);

                            if(status_3d.equalsIgnoreCase("true"))
                            {
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, commResponseVO, auditTrailVO, null);
                            }
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);

                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                        }
                    }
                    else
                    {
                        transactionLogger.error("--- in else STATUS failed ---");
                        message = "Transaction Declined";
                        if(!functions.isValueNull(remark)) {
                            remark = message;
                        }
                        responceStatus = "fail";
                        updatedStatus = "authfailed";
                        commResponseVO.setRemark(remark);
                        commResponseVO.setDescription(message);
                        commResponseVO.setErrorCode(error_code);
                        commResponseVO.setStatus("fail");
                        sb.append("remark='"+remark+"'");
                        sb.append(", status='authfailed'");
                        sb.append(", paymentid='"+transactionId+"'");

                        sb.append(" where trackingid = " + trackingId);
                        con = Database.getConnection();
                        transactionLogger.error("common update query DectaFrontendNotification---" + sb.toString());
                        Database.executeUpdate(sb.toString(), con);

                        if(status_3d.equalsIgnoreCase("true"))
                        {
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, commResponseVO, auditTrailVO, null);
                        }
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);

                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }

//                    sb.append(" where trackingid = " + trackingId);
//                    con = Database.getConnection();
//                    transactionLogger.error("common update query DectaFrontendNotification---" + sb.toString());
//                    Database.executeUpdate(sb.toString(), con);


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
                    transactionLogger.error("inside Decta New Back end sending notification---"+notificationUrl+"--- for trackingid---"+trackingId);
                    TransactionDetailsVO transactionDetailsVO1=transactionUtility.getTransactionDetails(commonValidatorVO);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1,trackingId,updatedStatus,message,"");
                }
            }

            catch (SystemError se)
            {
                transactionLogger.error("SystemError::::::", se);
                PZExceptionHandler.raiseAndHandleDBViolationException("DectaNewBackEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("DectaNewBackEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (Exception systemError)
        {
            transactionLogger.error("Exception In DectaNewBackEndServlet ::::", systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }


}