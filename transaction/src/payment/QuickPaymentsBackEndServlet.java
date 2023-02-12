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
import com.manager.vo.TerminalVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONObject;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by Admin on 4/30/2021.
 */
public class QuickPaymentsBackEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger = new TransactionLogger(QuickPaymentsBackEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doPost(request,response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException,ServletException
    {
        transactionLogger.error("----Inside QuickPaymentsBackEndServlet----");
        String trackingId = request.getParameter("trackingId");
        HttpSession session = request.getSession(true);
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        TransactionManager transactionManager = new TransactionManager();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        Functions functions = new Functions();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        StringBuilder responseMsg = new StringBuilder();
        MySQLCodec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        CommResponseVO transRespDetails = new CommResponseVO();
        PrintWriter pWriter = response.getWriter();
        PaymentManager paymentManager=new PaymentManager();
        TerminalVO terminalVO=new TerminalVO();

        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = request.getParameter(key);
            transactionLogger.error("Name=" + key + "-----Value=" + value);
        }
        String secretId =  request.getParameter("secretId");//paymentid
        String mcTxId ="";
        String action=request.getParameter("action");
        String req_amount=request.getParameter("amount");
        if (functions.isValueNull(req_amount))
        {
            req_amount = String.format("%.2f", Double.parseDouble(req_amount));
        }
        String btcamount=request.getParameter("btcamount");
        String notes=request.getParameter("notes");
        String address=request.getParameter("address");
        String btcaddress="";
        if(functions.isValueNull("btcaddress"))
        {
            btcaddress=request.getParameter("btcaddress");
        }
        String index="";//
        if(functions.isValueNull(index))
        {
           index= request.getParameter("index");
        }


        BufferedReader br = request.getReader();
        String str = "";
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }
        transactionLogger.error("----responseMsg----" + responseMsg.toString());
        String currency = "";
        String toId = "";
        String accountId = "";
        String dbStatus = "";
        String billingDesc = "";
        String notificationUrl = "";
        String description = "";
        String tmpl_amt = "";
        String tmpl_currency = "";
        String firstName = "";
        String lastName = "";
        String paymodeid = "";
        String cardtypeid = "";
        String redirectUrl = "";
        String ccnum = "";
        String expDate = "";
        String expMonth = "";
        String expYear = "";
        String terminalId = "";
        String orderDesc = "";
        String autoRedirect = "";
        String partnerName = "";
        String logoName = "";
        String confirmationPage = "";
        String eci = "";
        String transactionId = "";
        String amount1 = "";
        String custId = "";
        String ipAddress = Functions.getIpAddress(request);
        String custEmail = "";
        String city = "";
        String state = "";
        String country = "";
        String zip = "";
        String email = "";
        String phone = "";
        String toid = "";
        String paymentId = "";
        String status = "";
        String message = "";
        StringBuffer dbBuffer = new StringBuffer();
        String amount="";
        String arn="";
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        Connection con = null;
        ActionEntry entry = new ActionEntry();
        boolean isRecordNotFound=false;
        TransactionDetailsVO transactionDetailsVO =null;

        transactionLogger.error("trackingId-->" + trackingId);
        try
        {
            /*if (functions.isValueNull(trackingId))
            {
                transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            }
            if (transactionDetailsVO==null)
            {*/
                //fetch data using paymentID select query
                transactionDetailsVO = transactionManager.getTransDetailsFromCommonByPaymentId(secretId);
            //}
            transactionLogger.error("transactionDetailsVO--->"+transactionDetailsVO);
            if (transactionDetailsVO != null)
            {
                trackingId = transactionDetailsVO.getTrackingid();
                mcTxId = transactionDetailsVO.getPaymentId();
                accountId = transactionDetailsVO.getAccountId();
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                dbStatus = transactionDetailsVO.getStatus();
                amount = transactionDetailsVO.getAmount();
                currency = transactionDetailsVO.getCurrency();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                toid=transactionDetailsVO.getToid();

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
                transactionId = paymentId;
                if (merchantDetailsVO != null)
                {
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                }
                auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                auditTrailVO.setActionExecutorId(toid);

                transactionLogger.error("dbStatus--->"+dbStatus);
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    con = Database.getConnection();
                    transRespDetails = new CommResponseVO();
                    transRespDetails.setTransactionId(transactionId);
                    transRespDetails.setTmpl_Amount(req_amount);
                    transRespDetails.setTmpl_Currency(tmpl_currency);
                    transRespDetails.setCurrency(currency);

                    transRespDetails.setResponseHashInfo(mcTxId);

                    if (action.equalsIgnoreCase("approve"))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        transactionLogger.error("Inside AUTH_STARTED ");

                        billingDesc = gatewayAccount.getDisplayName();
                        status = "success";
                        if (functions.isValueNull(notes))
                        {
                            message = notes;
                        }
                        else
                        {
                            message = "Transaction Successful";
                        }

                        dbStatus = "capturesuccess";
                        transRespDetails.setStatus(status);
                        transRespDetails.setRemark(message);
                        transRespDetails.setDescriptor(billingDesc);
                        transRespDetails.setDescription(message);

                        dbBuffer.append("update transaction_common set captureamount='" + req_amount + "',paymentid='" + secretId + "',status='capturesuccess',remark='" + message + "',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "',arn='" + btcaddress + "_" + index + "' where trackingid = " + trackingId);
                        transactionLogger.error("update query------------>" + dbBuffer.toString());
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, req_amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                    }
                    else if (action.equalsIgnoreCase("cancel") || action.equalsIgnoreCase("deny"))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        transactionLogger.error("Inside AUTH_STARTED ");

                        status = "failed";

                        if (functions.isValueNull(notes))
                        {
                            message = notes;
                        }
                        else if (action.equalsIgnoreCase("cancel"))
                        {
                            message = "Transaction Failed by bank";
                        }

                        dbStatus = "authfailed";
                        transRespDetails.setStatus(status);
                        transRespDetails.setRemark(message);
                        transRespDetails.setTransactionId(transactionId);
                        transRespDetails.setDescription(message);


                        dbBuffer.append("update transaction_common set paymentid='" + secretId + "',status='authfailed',remark='" + message + "',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "',arn='" + btcaddress + "_" + index + "' where trackingid = " + trackingId);
                        transactionLogger.error("update query------------>" + dbBuffer.toString());
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, req_amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                    }
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    genericTransDetailsVO.setAmount(req_amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setNotificationUrl(notificationUrl);
                    genericTransDetailsVO.setBillingDiscriptor(billingDesc);

                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    commonValidatorVO.setCustomerId(custId);
                    commonValidatorVO.setTerminalId(terminalId);
                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setPaymentType(paymodeid);
                    commonValidatorVO.setCardType(cardtypeid);
                    commonValidatorVO.setTrackingid(trackingId);
                    commonValidatorVO.setStatus(message);
                    addressDetailsVO.setCity(city);
                    addressDetailsVO.setState(state);
                    addressDetailsVO.setCountry(country);
                    addressDetailsVO.setStreet(address);
                    addressDetailsVO.setZipCode(zip);
                    addressDetailsVO.setPhone(phone);


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
                    addressDetailsVO.setTmpl_amount(req_amount);
                    addressDetailsVO.setTmpl_currency(tmpl_currency);
                    cardDetailsVO.setCardNum(ccnum);
                    cardDetailsVO.setExpMonth(expMonth);
                    cardDetailsVO.setExpYear(expYear);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setEci(eci);
                    transactionUtility.setToken(commonValidatorVO, dbStatus);
                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("inside sending notification---" + notificationUrl);
                        TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                        transactionDetailsVO1.setBillingDesc(billingDesc);
                        transactionDetailsVO1.setTransactionMode("3D");
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, dbStatus, message, "");
                    }

                }

                else if (functions.isValueNull(transactionDetailsVO.getPaymentId()) && !transactionDetailsVO.getPaymentId().contains(transactionId))
                {
                    //transactionDetailsVO = transactionManager.getTransDetailsFromCommon(index-1,toId);
                    if (transactionDetailsVO == null)
                    {
                        isRecordNotFound = true;
                    }
                }
            }
            else
            {
                isRecordNotFound = true;
            }

            if (isRecordNotFound)
            {
                transactionLogger.error("---Record Not found---");
                int lastindex = Integer.parseInt(index) - 1;
                arn = arn + "_" + lastindex;
                transactionDetailsVO = transactionManager.getTransDetailsForQuickpayments(arn, toid);
                if (transactionDetailsVO != null)
                {
                    accountId = transactionDetailsVO.getAccountId();

                    terminalId = transactionDetailsVO.getTerminalId();
                    toid = transactionDetailsVO.getToid();
                    firstName = transactionDetailsVO.getFirstName();
                    lastName = transactionDetailsVO.getLastName();
                    custEmail = transactionDetailsVO.getEmailaddr();
                    currency = transactionDetailsVO.getCurrency();
                    notificationUrl = transactionDetailsVO.getNotificationUrl();

                    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                    merchantDetailsVO = merchantDAO.getMemberDetails(toid);

                    terminalVO.setTerminalId(terminalId);
                    terminalVO.setPaymodeId(transactionDetailsVO.getPaymodeId());
                    terminalVO.setCardTypeId(transactionDetailsVO.getCardTypeId());
                    genericTransDetailsVO.setAmount(req_amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setNotificationUrl(notificationUrl);
                    genericTransDetailsVO.setRedirectUrl(transactionDetailsVO.getRedirectURL());
                    genericTransDetailsVO.setFromid(transactionDetailsVO.getFromid());
                    genericTransDetailsVO.setFromtype(transactionDetailsVO.getFromtype());
                    genericTransDetailsVO.setTotype(transactionDetailsVO.getTotype());
                    merchantDetailsVO.setAccountId(accountId);
                    addressDetailsVO.setEmail(custEmail);
                    addressDetailsVO.setFirstname(firstName);
                    addressDetailsVO.setLastname(lastName);
                    addressDetailsVO.setTmpl_amount(req_amount);
                    addressDetailsVO.setTmpl_currency(currency);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());
                    commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
                    commonValidatorVO.setAccountId(accountId);
                    commonValidatorVO.setTerminalId(terminalId);
                    commonValidatorVO.setCustomerId(transactionDetailsVO.getCustomerId());
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setTerminalVO(terminalVO);
                    int trackingid = paymentManager.insertAuthStartedTransactionEntryForAsyncFlow(commonValidatorVO, auditTrailVO);
                    trackingId = String.valueOf(trackingid);
                    transactionLogger.error("Inside Capture Success for Tracking Id =" + trackingId);
                    auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                    auditTrailVO.setActionExecutorId(toid);
                    con = Database.getConnection();
                    transRespDetails = new CommResponseVO();
                    transRespDetails.setTransactionId(secretId);
                    transRespDetails.setTmpl_Amount(req_amount);
                    transRespDetails.setTmpl_Currency(tmpl_currency);
                    transRespDetails.setCurrency(currency);

                    if (action.equalsIgnoreCase("approved"))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        transactionLogger.error("Inside AUTH_STARTED ");

                        billingDesc = gatewayAccount.getDisplayName();
                        status = "success";
                        if (functions.isValueNull(notes))
                        {
                            message = notes;
                        }
                        else
                        {
                            message = "Transaction Successful";
                        }

                        dbStatus = "capturesuccess";
                        transRespDetails.setStatus(status);
                        transRespDetails.setRemark(message);
                        transRespDetails.setDescriptor(billingDesc);
                        transRespDetails.setDescription(message);


                        dbBuffer.append("update transaction_common set captureamount='" + req_amount + "',paymentid='" + secretId + "',status='capturesuccess',remark='" + message + "',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "',arn='" + btcaddress + "_" + index + "' where trackingid = " + trackingId);
                        transactionLogger.error("update query------------>" + dbBuffer.toString());
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, req_amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                    }
                    else if (action.equalsIgnoreCase("cancel") || action.equalsIgnoreCase("deny"))
                    {
                        notificationUrl = transactionDetailsVO.getNotificationUrl();
                        transactionLogger.error("Inside AUTH_STARTED ");

                        status = "failed";

                        if (functions.isValueNull(notes))
                        {
                            message = notes;
                        }
                        else if (action.equalsIgnoreCase("cancel"))
                        {
                            message = "Transaction Failed by bank";
                        }

                        dbStatus = "authfailed";
                        transRespDetails.setStatus(status);
                        transRespDetails.setRemark(message);
                        transRespDetails.setTransactionId(transactionId);
                        transRespDetails.setDescription(message);


                        dbBuffer.append("update transaction_common set paymentid='" + secretId + "',status='authfailed',remark='" + message + "',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "',arn='" + btcaddress + "_" + index + "' where trackingid = " + trackingId);
                        transactionLogger.error("update query------------>" + dbBuffer.toString());
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingId, req_amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                    }


                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    if(!functions.isValueNull(notificationUrl) && functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                        notificationUrl = merchantDetailsVO.getNotificationUrl();
                    }

                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("inside sending notification---" + notificationUrl);
                        TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                        transactionDetailsVO1.setAmount(req_amount);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, status, message, "");
                    }
                }

            }


            JSONObject jsonResObject = new JSONObject();
            String responseCode = "200";
            String responseStatus = "success";
            jsonResObject.put("responseCode", responseCode);
            jsonResObject.put("responseStatus", responseStatus);
            response.setContentType("application/json");
            response.setStatus(200);
            pWriter.println(jsonResObject.toString());
            pWriter.flush();
            return;

        }
        catch(PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException---->"+ trackingId + "-->", e);
        }
        catch(SystemError se)
        {
            transactionLogger.error("SystemError---->"+ trackingId + "-->", se);
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception--" + trackingId + "-->", e);
        }

        finally
        {
            Database.closeConnection(con);
        }

   }
}


