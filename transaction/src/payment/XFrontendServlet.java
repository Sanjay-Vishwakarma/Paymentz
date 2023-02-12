package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.BlacklistManager;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.xcepts.XceptsPaymentGateway;
import com.payment.xcepts.XceptsUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;


/**
 * Created by Admin on 2022-05-14.
 */
public class XFrontendServlet extends HttpServlet
{
    private static TransactionLogger transactionlogger = new TransactionLogger(XFrontendServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        try
        {
            doService(req,res);
        }
        catch (PZDBViolationException e)
        {
            transactionlogger.error("Entering XFrontendServlet ......",e);
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
            transactionlogger.error("Entering XFrontendServlet ......",e);
        }
    }

    public void doService(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException, PZDBViolationException
    {
        transactionlogger.error("....Entering XFrontendServlet ......");
        HttpSession session = req.getSession();
        Functions functions = new Functions();
        ActionEntry entry                           = new ActionEntry();
        AuditTrailVO auditTrailVO                   = new AuditTrailVO();
        TransactionManager transactionManager       = new TransactionManager();
        CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO         = new MerchantDetailsVO();
        MerchantDAO merchantDAO                     = new MerchantDAO();
        Comm3DResponseVO comm3DResponseVO           = new Comm3DResponseVO();
        GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO          = new GenericCardDetailsVO();
        StatusSyncDAO statusSyncDAO                 = new StatusSyncDAO();
        PaymentManager paymentManager               = new PaymentManager();
        TransactionUtility transactionUtility       = new TransactionUtility();
        MySQLCodec me                               = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();

        Connection con      = null;
        PreparedStatement p = null;
        ResultSet rs        = null;
        String toid         = "";
        String description  = "";
        String redirectUrl  = "";
        String accountId    = "";
        String orderDesc    = "";
        String currency     = "";
        String autoredirect = "";
        String displayName  = "";
        String logoName     = "";
        String partnerName  = "";
        String amount       = "";
        String trackingId   = req.getParameter("trackingId");
        String status       = "";
        String remark       = "";

        String name                 = "";
        String email                = "";
        String tmpl_amt             = "";
        String tmpl_currency        = "";
        String firstName            = "";
        String lastName             = "";
        String paymodeid            = "";
        String cardtypeid           = "";
        String custId               = "";
        String transactionId        = "";
        String message              = "";
        String billingDesc          = "";
        String dbStatus             = "";
        String notificationUrl      = "";
        String ccnum                = "";
        String expMonth             = "";
        String expYear              = "";
        String requestIp            = "";
        String txtdesc              = "";
        String STATUS               = "";
        String responseAmount       = "";
        String firstSix             = "";
        String lastFour             = "";
        String cardHolderName       = "";
        String terminalId           = "";
        String TXN_ID               = "";
        String addParams            = "";

        String result         = "";
        String responsecode   = "";
        String authcode       = "";
        String ECI            = "";
        String RRN            = "";
        String Rtrackid       = "";
        String Rterminalid    = "";
        String threedreason   = "";
        String Ramount        = "";
        String Rcurrency      = "";
        String signature      = "";
        String Rudf5 = "";
        String billingDescriptor            = "";
        String dynamic_billing_descriptor   = "";

        try
        {
            if (functions.isValueNull(req.getParameter("trackingId")))
            {
                trackingId = req.getParameter("trackingId");
            }
            else if (functions.isValueNull(req.getParameter("trackids")))
            {
                trackingId = req.getParameter("trackids");
            }
            else if (functions.isValueNull(req.getParameter("trackid")))
            {
                trackingId = req.getParameter("trackid");
            }

            transactionlogger.error("----- XFrontendServlet Response -----"+trackingId);
            Enumeration enumeration = req.getParameterNames();
            while (enumeration.hasMoreElements())
            {
                String key      = (String) enumeration.nextElement();
                String value    = req.getParameter(key);
                transactionlogger.error(trackingId+"---Key---" + key + "---Value---" + value);
            }

            BufferedReader br=req.getReader();
            StringBuilder responseMsg = new StringBuilder();
            String str1;
            while ((str1=br.readLine())!=null)
            {
                responseMsg.append(str1);
            }
            transactionlogger.error("----- XFrontendServlet Response -----" + responseMsg);

            if (functions.isValueNull(req.getParameter("result")))
                result = req.getParameter("result");
            else if (functions.isValueNull(req.getParameter("results")))
                result = req.getParameter("results");

            if (functions.isValueNull(req.getParameter("responsecode")))
                responsecode = req.getParameter("responsecode");
            else if (functions.isValueNull(req.getParameter("responsecodes")))
                responsecode = req.getParameter("responsecodes");

            if (functions.isValueNull(req.getParameter("authcode")))
                authcode = req.getParameter("authcode");
            else if (functions.isValueNull(req.getParameter("auths")))
                authcode = req.getParameter("auths");

            if (functions.isValueNull(req.getParameter("rrn")))
                RRN = req.getParameter("rrn");
            else if (functions.isValueNull(req.getParameter("rrns")))
                RRN = req.getParameter("rrns");

            if (functions.isValueNull(req.getParameter("eci")))
                ECI = req.getParameter("eci");
            else if (functions.isValueNull(req.getParameter("ecis")))
                ECI = req.getParameter("ecis");

            if (functions.isValueNull(req.getParameter("tranid")))
                transactionId = req.getParameter("tranid");
            else if (functions.isValueNull(req.getParameter("transid")))
                transactionId = req.getParameter("transid");

            if (functions.isValueNull(req.getParameter("trackid")))
                Rtrackid = req.getParameter("trackid");
            else if (functions.isValueNull(req.getParameter("trackid")))
                Rtrackid = req.getParameter("trackid");

            if (functions.isValueNull(req.getParameter("terminalid")))
                Rterminalid = req.getParameter("terminalid");

            if (functions.isValueNull(req.getParameter("threedreason")))
                threedreason = req.getParameter("threedreason");

            if (functions.isValueNull(req.getParameter("billingDescriptor")))
                billingDescriptor = req.getParameter("billingDescriptor");

            if (functions.isValueNull(req.getParameter("dynamic_billing_descriptor")))
                dynamic_billing_descriptor = req.getParameter("dynamic_billing_descriptor");

            if (functions.isValueNull(req.getParameter("amount")))
                Ramount = req.getParameter("amount");
            else if (functions.isValueNull(req.getParameter("amounts")))
                Ramount = req.getParameter("amounts");

            if (functions.isValueNull(req.getParameter("currency")))
                Rcurrency = req.getParameter("currency");
            else if (functions.isValueNull(req.getParameter("currencys")))
                Rcurrency = req.getParameter("currencys");

            if (functions.isValueNull(req.getParameter("signature")))
                signature = req.getParameter("signature");

            if (functions.isValueNull(req.getParameter("udf5")))
                Rudf5 = req.getParameter("udf5");
            else if (functions.isValueNull(req.getParameter("udfs5")))
                Rudf5 = req.getParameter("udfs5");



            TransactionDetailsVO transactionDetailsVO   = transactionManager.getTransDetailFromCommon(trackingId);
            CommRequestVO requestVO                     = new CommRequestVO();
            BlacklistManager blacklistManager           = new BlacklistManager();
            BlacklistVO blacklistVO                     = new BlacklistVO();

            if (transactionDetailsVO != null)
            {
                ccnum        = transactionDetailsVO.getCcnum();
                if (functions.isValueNull(ccnum))
                {
                    ccnum    = PzEncryptor.decryptPAN(ccnum);
                    firstSix = functions.getFirstSix(ccnum);
                    lastFour = functions.getLastFour(ccnum);
                }
                toid        = transactionDetailsVO.getToid();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                accountId   = transactionDetailsVO.getAccountId();
                orderDesc   = transactionDetailsVO.getOrderDescription();
                currency    = transactionDetailsVO.getCurrency();
                amount      = transactionDetailsVO.getAmount();

                terminalId = transactionDetailsVO.getTerminalId();
                remark     = transactionDetailsVO.getRemark();

                requestIp = Functions.getIpAddress(req);
                transactionlogger.error("requestIp --- >" + requestIp);
                transactionlogger.error("notificationUrl ---" + notificationUrl);

                if (functions.isValueNull(transactionDetailsVO.getTemplateamount()))
                    tmpl_amt = transactionDetailsVO.getTemplateamount();
                else
                    tmpl_amt = amount;

                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                if (!functions.isValueNull(tmpl_currency))
                {
                    tmpl_currency = currency;
                }

                if (!functions.isValueNull(currency))
                {
                    currency = Rcurrency;
                }

                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                {
                    String expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());

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

                firstName   = transactionDetailsVO.getFirstName();
                lastName    = transactionDetailsVO.getLastName();
                name        = transactionDetailsVO.getFirstName() + " " + transactionDetailsVO.getLastName();

                if(functions.isValueNull(transactionDetailsVO.getName())){
                    cardHolderName=transactionDetailsVO.getName();
                }
                paymodeid       = transactionDetailsVO.getPaymodeId();
                cardtypeid      = transactionDetailsVO.getCardTypeId();
                custId          = transactionDetailsVO.getCustomerId();
                if (!functions.isValueNull(transactionId))
                {
                    transactionId   = transactionDetailsVO.getPaymentId();
                }
                dbStatus        = transactionDetailsVO.getStatus();
                transactionlogger.error("dbStatus ---" + dbStatus);

                merchantDetailsVO   = merchantDAO.getMemberDetails(toid);
                autoredirect        = merchantDetailsVO.getAutoRedirect();
                logoName            = merchantDetailsVO.getLogoName();
                partnerName         = merchantDetailsVO.getPartnerName();
                email               = transactionDetailsVO.getEmailaddr();

                auditTrailVO.setActionExecutorName("XFrontendServlet");
                auditTrailVO.setActionExecutorId(toid);

                XceptsPaymentGateway xceptsPaymentGateway = new XceptsPaymentGateway(accountId);
                displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                transactionlogger.error(trackingId+" dbStatus ----> "+ dbStatus);

                if (functions.isValueNull(threedreason))
                    remark = threedreason;
                else if (functions.isValueNull(Rudf5))
                    remark = Rudf5;
                else
                    remark = result;
                
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                {
                    transactionlogger.error("inside AUTH_STARTED---");
                    StringBuffer dbBuffer = new StringBuffer();

                    commTransactionDetailsVO.setAmount(amount);
                    commTransactionDetailsVO.setCurrency(currency);
                    commTransactionDetailsVO.setPreviousTransactionId(transactionId);
                    requestVO.setTransDetailsVO(commTransactionDetailsVO);

                    con                 = Database.getConnection();
                    comm3DResponseVO    = (Comm3DResponseVO) xceptsPaymentGateway.processQuery(trackingId, requestVO);

                    String inStatus     = comm3DResponseVO.getStatus();
                    String inRemark     = comm3DResponseVO.getRemark();
                    String inCode       = comm3DResponseVO.getAuthCode();
                    responseAmount      = comm3DResponseVO.getAmount();
                    TXN_ID              = comm3DResponseVO.getTransactionId();
                    String errorcode    = comm3DResponseVO.getErrorCode();
                    String responsetime = comm3DResponseVO.getResponseTime();

                    if (!functions.isValueNull(TXN_ID))
                    {
                        TXN_ID = transactionId;
                    }

                    if (functions.isValueNull(RRN) && functions.isValueNull(comm3DResponseVO.getRrn()))
                    {
                        RRN = comm3DResponseVO.getRrn();
                    }


                    transactionlogger.error("inquiry trackingid--->"+trackingId);
                    transactionlogger.error("inquiry inStatus--->"+inStatus);
                    transactionlogger.error("inquiry amount--->"+responseAmount);
                    transactionlogger.error("inquiry inCode--->"+inCode);
                    transactionlogger.error("inquiry inRemark--->"+inRemark);
                    transactionlogger.error("inquiry VendorOrderId--->"+TXN_ID);
                    transactionlogger.error("frontend response amount --->" + Ramount);

                    if(functions.isValueNull(responseAmount) || functions.isValueNull(Ramount))
                    {
                        Double compRsAmount;
                        Double compDbAmount;
                        if (!functions.isValueNull(responseAmount) && functions.isValueNull(Ramount)) {
                            compRsAmount = Double.valueOf(Ramount);
                        }else {
                            compRsAmount = Double.valueOf(responseAmount);
                        }
                        compDbAmount = Double.valueOf(amount);

                        transactionlogger.error("inquiry response amount --->" + compRsAmount);
                        transactionlogger.error(" DB Amount--->" + compDbAmount);

                        if (compDbAmount.equals(compRsAmount))
                        {
                            STATUS = comm3DResponseVO.getStatus();
                            amount = comm3DResponseVO.getAmount();
                            if (!functions.isValueNull(amount))
                            {
                                amount = Ramount;
                            }
                            remark = comm3DResponseVO.getRemark();
                        }
                        else if (!compDbAmount.equals(compRsAmount) && "success".equalsIgnoreCase(inStatus))
                        {
                            transactionlogger.error(trackingId+" XFrontendServlet inside else Amount incorrect--->" + responseAmount);
                            remark = "Failed-IRA";
                            STATUS = "failed";
                            amount = responseAmount;
                            blacklistVO.setVpaAddress(custId);
                            blacklistVO.setIpAddress(requestIp);
                            blacklistVO.setEmailAddress(email);
                            blacklistVO.setActionExecutorId(toid);
                            blacklistVO.setActionExecutorName("AcquirerFrontEnd");
                            blacklistVO.setRemark("IncorrectAmount Trackingid : "+trackingId);
                            blacklistVO.setFirstSix(firstSix);
                            blacklistVO.setLastFour(lastFour);
                            blacklistVO.setName(cardHolderName);
                            blacklistManager.commonBlackListing(blacklistVO);
                        }
                    }
                    currency    = comm3DResponseVO.getCurrency();
                    txtdesc     = comm3DResponseVO.getDescription();
                    String bankTransactionDate  = comm3DResponseVO.getBankTransactionDate();

                    comm3DResponseVO.setTmpl_Amount(tmpl_amt);
                    comm3DResponseVO.setTmpl_Currency(tmpl_currency);
                    comm3DResponseVO.setIpaddress(requestIp);
                    comm3DResponseVO.setErrorCode(errorcode);
                    comm3DResponseVO.setResponseTime(responsetime);
                    comm3DResponseVO.setTransactionId(TXN_ID);
                    if (functions.isValueNull(ECI))
                    {
                        comm3DResponseVO.setEci(ECI);
                    }

                    if (!functions.isValueNull(currency))
                    {
                        comm3DResponseVO.setCurrency(Rcurrency);
                        currency = Rcurrency;
                    }

                    transactionlogger.error("currency--->"+currency +" "+Rcurrency);
                    transactionlogger.error("inquiry txtdesc--->"+txtdesc);
                    transactionlogger.error("inquiry bankTransactionDate--->"+bankTransactionDate);
                    transactionlogger.error("remark--->"+remark);

                    if("success".equalsIgnoreCase(STATUS))
                    {
                        transactionlogger.error("sucess  front end operation: "+ STATUS);

                        if (functions.isValueNull(remark))
                            message = remark;
                        else if (functions.isValueNull(result))
                            message = result;
                        else
                            message = "Payment successful";

                        if (functions.isValueNull(dynamic_billing_descriptor))
                            billingDesc = dynamic_billing_descriptor;
                        else if (functions.isValueNull(billingDescriptor))
                            billingDesc = billingDescriptor;
                        else
                            billingDesc = displayName;

                        comm3DResponseVO.setDescriptor(billingDesc);

                        notificationUrl    = transactionDetailsVO.getNotificationUrl();
                        status              = "success";
                        dbStatus = "capturesuccess";
                        comm3DResponseVO.setStatus(dbStatus);

                        if (functions.isValueNull(RRN))
                            addParams +=  "',rrn='" + RRN;

                        if (functions.isValueNull(ECI))
                            addParams +=  "',eci='" + ECI;

                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + TXN_ID + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + addParams + "" +"',status='capturesuccess'" + " ,successtimestamp='" + functions.getTimestamp());
                        dbBuffer.append("',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                        transactionlogger.error("Update Query---" + dbBuffer);

                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, comm3DResponseVO, auditTrailVO, requestIp);//+ ",rrn='" + rrn
                    }
                    else if("failed".equalsIgnoreCase(STATUS))
                    {
                        transactionlogger.error("failed  front end operation: " + STATUS);

                        if (functions.isValueNull(remark))
                            message = remark;
                        else if (functions.isValueNull(result))
                            message = result;
                        else
                            message = "Transaction Failed";

                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        status          = "failed";
                        dbStatus        = "authfailed";
                        comm3DResponseVO.setStatus(dbStatus);

                        if (functions.isValueNull(authcode))
                            addParams +=  "',authorization_code='" + authcode;

                        if (functions.isValueNull(ECI))
                            addParams +=  "',eci='" + ECI;

                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + TXN_ID + "',templateamount='" + tmpl_amt + "',templatecurrency='" + tmpl_currency + "'" + " ,failuretimestamp='" + functions.getTimestamp());
                        dbBuffer.append(addParams+"',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' ,remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                        transactionlogger.error("Update Query---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, comm3DResponseVO, auditTrailVO, requestIp);
                    }
                    else
                    {
                        //pending
                        dbStatus    = "authstarted";
                        status      = "Pending";
                        message     = "Pending";
                    }
                }
                else
                {
                    if (PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                    {
                        if (functions.isValueNull(dynamic_billing_descriptor))
                            billingDesc = dynamic_billing_descriptor;
                        else if (functions.isValueNull(billingDescriptor))
                            billingDesc = billingDescriptor;
                        else
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                        if (functions.isValueNull(remark))
                            message = remark;
                        else if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";

                        status      = "success";
                        dbStatus    = PZTransactionStatus.AUTH_SUCCESS.toString();
                    }
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        if (functions.isValueNull(dynamic_billing_descriptor))
                            billingDesc = dynamic_billing_descriptor;
                        else if (functions.isValueNull(billingDescriptor))
                            billingDesc = billingDescriptor;
                        else
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();


                        if (functions.isValueNull(remark))
                            message = remark;
                        else if (functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";

                        status   = "success";
                        dbStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    }
                    else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                    {
                        if (functions.isValueNull(remark))
                            message = remark;
                        else if (functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else if (!functions.isValueNull(message))
                            message = "Transaction Failed";

                        status   = "failed";
                        dbStatus = PZTransactionStatus.AUTH_FAILED.toString();
                    }
                    else
                    {
                        status   = "pending";
                        message  = "Transaction pending";
                        dbStatus = PZTransactionStatus.AUTH_STARTED.toString();
                    }
                }
                statusSyncDAO.updateAllTransactionFlowFlag(trackingId,dbStatus);

//                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
//                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

//                AsynchronousSmsService smsService = new AsynchronousSmsService();
//                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setEci(ECI);
                genericTransDetailsVO.setRrn(RRN);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);

                if (functions.isValueNull(email))
                    addressDetailsVO.setEmail(email);
                else
                    addressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                addressDetailsVO.setFirstname(firstName);
                addressDetailsVO.setLastname(lastName);
                addressDetailsVO.setCardHolderIpAddress(requestIp);

                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);

                commonValidatorVO.setStatus(dbStatus);
                commonValidatorVO.setTrackingid(trackingId);
                commonValidatorVO.setTerminalId(terminalId);
                commonValidatorVO.setReason(message);
                commonValidatorVO.setBankDescription(message);
                commonValidatorVO.setBankCode(responsecode);
                commonValidatorVO.setReferenceid(Rtrackid);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);
                commonValidatorVO.setCustomerId(custId);
                commonValidatorVO.setCustomerBankId(custId);
                commonValidatorVO.setAccountId(accountId);
                commonValidatorVO.setEci(ECI);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);

                transactionUtility.setToken(commonValidatorVO, dbStatus);

                if (functions.isValueNull(notificationUrl))
                {
                    transactionlogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    transactionDetailsVO1.setBillingDesc(billingDesc);
                    transactionDetailsVO1.setBankReferenceId(TXN_ID);
                    transactionDetailsVO1.setRemark(message);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, dbStatus, message, "");
                    transactionlogger.error("remark message----------------------" + message);
                }

                if ("Y".equalsIgnoreCase(autoredirect))
                {
                    transactionlogger.error("responseStatus in ---" + dbStatus);
                    transactionUtility.doAutoRedirect(commonValidatorVO, res, status, billingDesc);
                }
                else
                {
                    session.setAttribute("ctoken", ctoken);

                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", status);
                    req.setAttribute("displayName",billingDesc);
                    req.setAttribute("remark", message);

                    String confirmationPage = "";
                    String version          = (String)session.getAttribute("version");

                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    session.invalidate();
                    RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(req, res);
                }
            }
        }
        catch (Exception e)
        {
            transactionlogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("XFrontendServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
    }

}
