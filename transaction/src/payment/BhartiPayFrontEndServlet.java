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
import com.payment.Enum.PZProcessType;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.bhartiPay.BhartiPayPaymentGateway;
import com.payment.bhartiPay.BhartiPayUtils;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;

/**
 * Created by Devendra on 1/31/2020.
 */
public class BhartiPayFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionlogger = new TransactionLogger(BhartiPayFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req,res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("PZDBViolationException  in BhartiPayFrontEndServlet---", e);
        }
    }

    public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req,res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("PZDBViolationException in BhartiPayFrontEndServlet---", e);
        }
    }

    public void doService(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException, PZDBViolationException
    {
        transactionlogger.error("Entering BhartiPayFrontEndServlet ......");


        HttpSession session = req.getSession();
        Functions functions = new Functions();
        //PayGateCryptoUtils payGateCryptoUtils = new PayGateCryptoUtils();
        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        TransactionManager transactionManager = new TransactionManager();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        CommResponseVO commResponseVO = new CommResponseVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        PaymentManager paymentManager = new PaymentManager();
        TransactionUtility transactionUtility = new TransactionUtility();
        Connection con = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        String toid = "";
        String description = "";
        String redirectUrl = "";
        String accountId = "";
        String orderDesc = "";
        String currency = "";
        String clkey = "";
        String checksumAlgo = "";
        String checksum = "";
        String autoredirect = "";
        String isService = "";
        String displayName = "";
        String isPowerBy = "";
        String logoName = "";
        String partnerName = "";

        String amount = "";
        String trackingId = req.getParameter("trackingId");
        String status = "";
        String remark = "";

        String bankTransactionStatus = "";
        String resultCode = "";
        String email = "";

        String tmpl_amt = "";
        String tmpl_currency = "";
        String firstName = "";
        String lastName = "";
        String paymodeid = "";
        String cardtypeid = "";
        String custId = "";
        String transactionStatus = "";
        String confirmStatus = "";
        String responseStatus = "";
        String transactionId = "";
        String message = "";
        String billingDesc = "";
        String dbStatus = "";
        String eci = "";
        String paymentid = "";
        String errorCode = "";
        String name = "";
        String notificationUrl = "";
        String ccnum = "";
        String expMonth = "";
        String expYear = "";
        String requestIp = "";
        String merchantKey = "";
        String MOP_TYPE="";


        String RESPONSE_DATE_TIME ="";
        String RESPONSE_CODE ="";
        String AMOUNT ="";
        String TXN_ID ="";
        String TXNTYPE ="";
        String CURRENCY_CODE ="";
        String STATUS ="";
        String RESPONSE_MESSAGE ="";
        String CUST_EMAIL ="";
        String CUST_NAME ="";
        String RRN ="";
        String ORDER_ID ="";
        String PAYMENT_TYPE ="";
        String CUST_VPA ="";
        String MERCHANT_PAYMENT_TYPE ="";


        Enumeration enumeration = req.getParameterNames();

        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = req.getParameter(key);
            transactionlogger.error("---Key---" + key + "---Value---" + value);
        }

        try
        {
           /* if(functions.isValueNull(req.getParameter("RESPONSE_DATE_TIME")))
            {
              RESPONSE_DATE_TIME = req.getParameter("RESPONSE_DATE_TIME");
            }
           if(functions.isValueNull(req.getParameter("RESPONSE_CODE")))
            {
               RESPONSE_CODE = req.getParameter("RESPONSE_CODE");
            }
           if(functions.isValueNull(req.getParameter("AMOUNT")))
            {
                AMOUNT = req.getParameter("AMOUNT");
            }
           if(functions.isValueNull(req.getParameter("TXN_ID")))
            {
                 TXN_ID = req.getParameter("TXN_ID");
            }
           if(functions.isValueNull(req.getParameter("TXNTYPE")))
            {
                 TXNTYPE = req.getParameter("TXNTYPE");
            }
           if(functions.isValueNull(req.getParameter("CURRENCY_CODE")))
            {
                 CURRENCY_CODE = req.getParameter("CURRENCY_CODE");
            }
           if(functions.isValueNull(req.getParameter("STATUS")))
            {
              STATUS = req.getParameter("STATUS");
            }
           if(functions.isValueNull(req.getParameter("RESPONSE_MESSAGE")))
            {
                 RESPONSE_MESSAGE = req.getParameter("RESPONSE_MESSAGE");
            }
           if(functions.isValueNull(req.getParameter("CUST_EMAIL")))
            {
                 CUST_EMAIL = req.getParameter("CUST_EMAIL");
            }
           if(functions.isValueNull(req.getParameter("CUST_NAME")))
            {
                 CUST_NAME = req.getParameter("CUST_NAME");
            }
            if(functions.isValueNull(req.getParameter("RRN")))
            {
                 RRN = req.getParameter("RRN");
            }
            if(functions.isValueNull(req.getParameter("ORDER_ID")))
            {
                 ORDER_ID = req.getParameter("ORDER_ID");
            }
            if(functions.isValueNull(req.getParameter("PAYMENT_TYPE")))
            {
                 PAYMENT_TYPE=req.getParameter("PAYMENT_TYPE");
            }
           if(functions.isValueNull(req.getParameter("MERCHANT_PAYMENT_TYPE")))
            {
                MERCHANT_PAYMENT_TYPE=req.getParameter("MERCHANT_PAYMENT_TYPE");
            }  */

            if(functions.isValueNull(req.getParameter("CUST_VPA")))
            {
                CUST_VPA=req.getParameter("CUST_VPA");
            }

            /*transactionlogger.error(" RESPONSE_DATE_TIME--->" + RESPONSE_DATE_TIME);

            transactionlogger.error(" RESPONSE_CODE--->" + RESPONSE_CODE);

            transactionlogger.error(" AMOUNT--->" + AMOUNT);

            transactionlogger.error(" TXN_ID--->" + TXN_ID);

            transactionlogger.error(" TXNTYPE--->" + TXNTYPE);

            transactionlogger.error(" CURRENCY_CODE--->" + CURRENCY_CODE);

            transactionlogger.error(" STATUS--->" + STATUS);

            transactionlogger.error(" RESPONSE_MESSAGE--->" + RESPONSE_MESSAGE);

            transactionlogger.error(" CUST_EMAIL--->" + CUST_EMAIL);

            transactionlogger.error(" CUST_NAME--->" + CUST_NAME);

            transactionlogger.error(" RRN--->" + RRN);
            transactionlogger.error(" ORDER_ID--->" + ORDER_ID);

            transactionlogger.error("PAYMENT_TYPE--->"+PAYMENT_TYPE);
*/
            transactionlogger.error("CUST_VPA--->"+CUST_VPA);

            transactionlogger.error("MERCHANT_PAYMENT_TYPE--->"+MERCHANT_PAYMENT_TYPE);
            String MOP_TYPE1="";
            if(functions.isValueNull(req.getParameter("MOP_TYPE"))&&"NB".equalsIgnoreCase(PAYMENT_TYPE)){
                MOP_TYPE1=req.getParameter("MOP_TYPE");
                MOP_TYPE=BhartiPayUtils.getBankName(MOP_TYPE1);
                transactionlogger.error("MOP_TYPE------->"+MOP_TYPE);
            }
            else{
                MOP_TYPE="";
            }

           /* String INTERNAL_ACQUIRER_TYPE=req.getParameter("INTERNAL_ACQUIRER_TYPE");//for PhonePe
            transactionlogger.error("INTERNAL_ACQUIRER_TYPE---->"+INTERNAL_ACQUIRER_TYPE);*/


            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

        if (transactionDetailsVO != null)
        {
            if(functions.isValueNull(transactionDetailsVO.getCcnum()))
            ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());

            toid = transactionDetailsVO.getToid();
            description = transactionDetailsVO.getDescription();
            redirectUrl = transactionDetailsVO.getRedirectURL();
            accountId = transactionDetailsVO.getAccountId();
            orderDesc = transactionDetailsVO.getOrderDescription();
            currency = transactionDetailsVO.getCurrency();
            amount = transactionDetailsVO.getAmount();
            tmpl_amt = transactionDetailsVO.getTemplateamount();
            notificationUrl = transactionDetailsVO.getNotificationUrl();
            requestIp = Functions.getIpAddress(req);
            transactionlogger.error("requestIp --- >" + requestIp);
            transactionlogger.error("notificationUrl ---" + notificationUrl);
            if (functions.isValueNull(tmpl_amt))
            {
                tmpl_amt = transactionDetailsVO.getTemplateamount();
            }
            else
            {
                transactionlogger.error("inside else of tmpl_amnt--->");
                tmpl_amt = null;
            }
            tmpl_currency = transactionDetailsVO.getTemplatecurrency();
            if (functions.isValueNull(tmpl_currency))
            {
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
            }
            else
            {
                transactionlogger.error("inside else of tmpl_currency --->");
                tmpl_currency = "";
            }
            firstName = transactionDetailsVO.getFirstName();
            lastName = transactionDetailsVO.getLastName();
            name = transactionDetailsVO.getFirstName() + " " + transactionDetailsVO.getLastName();


            paymodeid = transactionDetailsVO.getPaymodeId();
            cardtypeid = transactionDetailsVO.getCardTypeId();
            custId = transactionDetailsVO.getCustomerId();
            transactionId = transactionDetailsVO.getPaymentId();
            dbStatus = transactionDetailsVO.getStatus();
            transactionlogger.error("dbStatus ---" + dbStatus);

            merchantDetailsVO = merchantDAO.getMemberDetails(toid);
            clkey = merchantDetailsVO.getKey();
            checksumAlgo = merchantDetailsVO.getChecksumAlgo();
            autoredirect = merchantDetailsVO.getAutoRedirect();
            isPowerBy = merchantDetailsVO.getIsPoweredBy();
            logoName = merchantDetailsVO.getLogoName();
            isService = merchantDetailsVO.getIsService();
            partnerName = merchantDetailsVO.getPartnerName();
            email = transactionDetailsVO.getEmailaddr();

            auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
            auditTrailVO.setActionExecutorId(toid);

           // GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            CommRequestVO requestVO = new CommRequestVO();
            displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
            BhartiPayPaymentGateway bhartiPayPaymentGateway = new BhartiPayPaymentGateway(accountId);

            checksum = Checksum.generateChecksumV2(description, String.valueOf(amount), bankTransactionStatus, clkey, checksumAlgo);

            if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
            {


                transactionlogger.error("inside AUTH_STARTED---");
                StringBuffer dbBuffer = new StringBuffer();

                con = Database.getConnection();
                String query = "select m.clkey,m.autoredirect,m.isService,logoName,partnerName FROM members AS m,partners AS p WHERE m.partnerId=p.partnerId AND m.memberid=?";
                p = con.prepareStatement(query);
                p.setString(1, toid);
                rs = p.executeQuery();
                if (rs.next())
                {
                    clkey = rs.getString("clkey");
                    autoredirect = rs.getString("autoredirect");
                    logoName = rs.getString("logoName");
                    partnerName = rs.getString("partnerName");
                    isService = rs.getString("isService");
                }
                commResponseVO= (CommResponseVO) bhartiPayPaymentGateway.processQuery(trackingId, requestVO);

                TXN_ID =commResponseVO.getTransactionId();
                commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                STATUS= commResponseVO.getStatus();
                RESPONSE_CODE= commResponseVO.getAuthCode();
                RESPONSE_MESSAGE=commResponseVO.getRemark();
                commResponseVO.setIpaddress(requestIp);
                transactionlogger.error("bhartiPay inquiry trackingid--->" + trackingId);
                transactionlogger.error("bhartiPay inquiry TXN_ID--->"+TXN_ID);
                transactionlogger.error("bhartiPay inquiry STATUS--->" + STATUS);
                transactionlogger.error("bhartiPay inquiry amount--->"+amount);
                transactionlogger.error("bhartiPay inquiry RESPONSE_CODE--->"+RESPONSE_CODE);
                transactionlogger.error("bhartiPay inquiry currency--->"+currency);
                transactionlogger.error("bhartiPay inquiry remark--->" + RESPONSE_MESSAGE);
                if ("000".equalsIgnoreCase(RESPONSE_CODE))
                {
                    status = "success";
                    responseStatus = RESPONSE_MESSAGE;
                    billingDesc = displayName;

                    commResponseVO.setDescription(responseStatus);
                    commResponseVO.setStatus(status);
                    commResponseVO.setRemark(responseStatus);
                    commResponseVO.setDescriptor(billingDesc);

                    confirmStatus = "Y";

                    dbStatus = "capturesuccess";

                    dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + TXN_ID + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency +  "',rrn='" + RRN +"',status='capturesuccess'");
                    if("up".equalsIgnoreCase(PAYMENT_TYPE)){
                        dbBuffer.append(",customerId='" + CUST_VPA+"'");

                    }
                    if("NB".equalsIgnoreCase(MERCHANT_PAYMENT_TYPE)){
                        dbBuffer.append(",customerId='" + MOP_TYPE+"'");

                    }

                    dbBuffer.append(",customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + responseStatus + "' where trackingid = " + trackingId);
                    transactionlogger.error("Update Query---" + dbBuffer);
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);//+ ",rrn='" + rrn
                }
                else
                {
                    transactionlogger.error("inside BhartiPay failed --->");
                    status = "failed";
                    responseStatus = RESPONSE_MESSAGE;
                    commResponseVO.setStatus(status);
                    commResponseVO.setDescription(responseStatus);
                    commResponseVO.setRemark(responseStatus);

                    dbStatus = "authfailed";
                    dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + TXN_ID + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "'");
                    if("up".equalsIgnoreCase(PAYMENT_TYPE)){
                        dbBuffer.append(",customerId='" + CUST_VPA+"'");

                    }
                    if("NB".equalsIgnoreCase(MERCHANT_PAYMENT_TYPE)){
                        dbBuffer.append(",customerId='" + MOP_TYPE+"'");

                    }

                    dbBuffer.append(",customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" +status+ "' where trackingid = " + trackingId);
                    transactionlogger.error("Update Query---" + dbBuffer);
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);

                }
                    if(functions.isValueNull(MOP_TYPE)){
                        custId=MOP_TYPE;
                    }

                        else {
                        custId=CUST_VPA;
                             }

                transactionlogger.error("custId------------------->"+custId);
            }

            else
            {
                if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                {
                    transactionlogger.error("dbstatus before capture------------------->"+dbStatus);
                    billingDesc = displayName;
                    status = "success";
                    message = "Transaction Successful";
                    responseStatus = "Successful";
                    dbStatus = "capturesuccess";
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId,dbStatus);
                    transactionlogger.error("dbstatus AFTER capture------------------>"+dbStatus);

                }
                else
                {
                    status = "fail";
                    message = "Transaction Declined";
                    responseStatus = "Failed";
                    dbStatus = "authfailed";
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId,dbStatus);
                }
            }


            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, responseStatus, billingDesc);

            AsynchronousSmsService smsService = new AsynchronousSmsService();
            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, responseStatus, billingDesc);

            commonValidatorVO.setStatus(dbStatus);
            commonValidatorVO.setTrackingid(trackingId);
            genericTransDetailsVO.setOrderId(description);
            genericTransDetailsVO.setAmount(amount);
            genericTransDetailsVO.setCurrency(currency);
            genericTransDetailsVO.setOrderDesc(orderDesc);
            genericTransDetailsVO.setRedirectUrl(redirectUrl);
            genericTransDetailsVO.setNotificationUrl(notificationUrl);
            genericTransDetailsVO.setBillingDiscriptor(billingDesc);
            commonValidatorVO.setLogoName(logoName);
            commonValidatorVO.setPartnerName(partnerName);
            commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            merchantDetailsVO = merchantDAO.getMemberDetails(toid);
            addressDetailsVO.setTmpl_amount(tmpl_amt);
            addressDetailsVO.setTmpl_currency(tmpl_currency);
            addressDetailsVO.setFirstname(firstName);
            addressDetailsVO.setLastname(lastName);
            addressDetailsVO.setCardHolderIpAddress(requestIp);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
            commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());

            if (functions.isValueNull(email))
                addressDetailsVO.setEmail(email);
            else
                addressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
            commonValidatorVO.setPaymentType(paymodeid);
            commonValidatorVO.setCardType(cardtypeid);

            commonValidatorVO.setCustomerId(custId);
            commonValidatorVO.setCustomerBankId(custId);

            if (functions.isValueNull(transactionDetailsVO.getExpdate()))
            {
                transactionlogger.error("expDate --->"+transactionDetailsVO.getExpdate());

                String expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                transactionlogger.error("expDate --->"+expDate);
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

            cardDetailsVO.setCardNum(ccnum);
            cardDetailsVO.setExpMonth(expMonth);
            cardDetailsVO.setExpYear(expYear);
            commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
            commonValidatorVO.setCardDetailsVO(cardDetailsVO);

            if (functions.isValueNull(notificationUrl))
            {
                transactionlogger.error("inside sending notification---" + notificationUrl);
                TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, dbStatus, message, "");
                transactionlogger.error("remark message----------------------"+message);
            }

            if ("Y".equalsIgnoreCase(autoredirect))
            {
                transactionlogger.error("responseStatus in ---" + dbStatus);
                transactionUtility.doAutoRedirect(commonValidatorVO, res, dbStatus, billingDesc);
            }
            else
            {
                session.setAttribute("ctoken", ctoken);
                req.setAttribute("transDetail", commonValidatorVO);
                req.setAttribute("responceStatus", dbStatus);
                req.setAttribute("displayName",displayName);
                req.setAttribute("remark", message);
                String confirmationPage = "";
                String version = (String)session.getAttribute("version");
                if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                    confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                else
                    confirmationPage = "/confirmationpage.jsp?ctoken=";
                session.invalidate();
                RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                rd.forward(req, res);
            }

        }
        }
        catch (Exception e)
        {
            transactionlogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("BhartiPayFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }


    }
}