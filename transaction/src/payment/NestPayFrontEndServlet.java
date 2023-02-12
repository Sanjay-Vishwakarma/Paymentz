package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.MerchantConfigManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.nestpay.NestPayPaymentGateway;
import com.payment.nestpay.NestPayRequestVO;
import com.payment.nestpay.NestPaymentProcess;
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

/**
 * Created by Admin on 7/6/18.
 */
public class NestPayFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger= new TransactionLogger(NestPayFrontEndServlet.class.getName());
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
        transactionLogger.error("-----inside NestPayFrontEndServlet-----");


        HttpSession session = request.getSession(true);
        Functions functions= new Functions();
        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        CommResponseVO commResponseVO = new CommResponseVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Connection con=null;

        String key="";
        String value="";
        String status="";
        String eci="";
        String xid="";
        String cavv="";
        String oid="";
        String mdStatus="";
        String md="";
        String message="";
        String HASHPARAMS="";
        String HASHPARAMSVAL="";
        String enrolledStatus="";
        String responsetransType="";
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
        String trackingId = "";
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
        String billingDesc = "";
        String dbStatus = "";
        String paymentid = "";
        String errorCode="";
        String responceStatus="";
        String num="";
        String cardNum="";
        String notificationUrl="";
        String version="";
        String updatedStatus="";

        Enumeration enumeration= request.getParameterNames();
        while (enumeration.hasMoreElements()){
            key=(String)enumeration.nextElement();
            value=request.getParameter(key);
            transactionLogger.debug("key---"+key+"----value---"+value);
        }

        if(functions.isValueNull(request.getParameter("status")))
            status=request.getParameter("status");
        if(functions.isValueNull(request.getParameter("mdStatus")))
            mdStatus=request.getParameter("mdStatus");
        if(functions.isValueNull(request.getParameter("eci")))
            eci=request.getParameter("eci");
        if(functions.isValueNull(request.getParameter("xid")))
            xid=request.getParameter("xid");
        if(functions.isValueNull(request.getParameter("oid")))
            trackingId=request.getParameter("oid");
        if(functions.isValueNull(request.getParameter("mdErrorMsg")))
            message=request.getParameter("mdErrorMsg");
        if(functions.isValueNull(request.getParameter("HASHPARAMS")))
            HASHPARAMS=request.getParameter("HASHPARAMS");
        if(functions.isValueNull(request.getParameter("HASHPARAMSVAL")))
            HASHPARAMSVAL=request.getParameter("HASHPARAMSVAL");
        if(functions.isValueNull(request.getParameter("md")))
            md=request.getParameter("md");
        if(functions.isValueNull(request.getParameter("cavv")))
            cavv=request.getParameter("cavv");
        if(functions.isValueNull(request.getParameter("veresEnrolledStatus")))
            enrolledStatus=request.getParameter("veresEnrolledStatus");
        if(functions.isValueNull(request.getParameter("trantype")))
            responsetransType=request.getParameter("trantype");
        if(functions.isValueNull(request.getParameter("num")))
            num=request.getParameter("num");


        String remoteAddr = Functions.getIpAddress(request);
        String reqIp = "";
        if(remoteAddr.contains(","))
        {
            String sIp[] = remoteAddr.split(",");
            reqIp = sIp[0].trim();
        }
        else
        {
            reqIp = remoteAddr;
        }
        transactionLogger.debug("reqIp-----"+reqIp);

        try
        {
            NestPaymentProcess paymentProcess = new NestPaymentProcess();
            NestPayRequestVO commRequestVO = new NestPayRequestVO();

            paymentProcess.setNestPayRequestVO(commRequestVO, trackingId);

            CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
            CommCardDetailsVO commCardDetailsVO= commRequestVO.getCardDetailsVO();
            description = transactionDetailsVO.getOrderId();
            orderDesc = transactionDetailsVO.getOrderDesc();
            amount = transactionDetailsVO.getAmount();
            currency = transactionDetailsVO.getCurrency();
            toid = transactionDetailsVO.getToId();
            paymodeid = transactionDetailsVO.getPaymentType();
            cardtypeid = transactionDetailsVO.getCardType();
            redirectUrl = transactionDetailsVO.getRedirectUrl();
            dbStatus=transactionDetailsVO.getPrevTransactionStatus();
            custId=transactionDetailsVO.getCustomerId();
            cardNum=commCardDetailsVO.getCardNum();
            notificationUrl=transactionDetailsVO.getNotificationUrl();
            version=transactionDetailsVO.getVersion();

            transactionLogger.debug("custid-----"+custId);

            CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
            email = commAddressDetailsVO.getEmail();
            if (functions.isValueNull(commAddressDetailsVO.getFirstname()))
                firstName=commAddressDetailsVO.getFirstname();
            if(functions.isValueNull(commAddressDetailsVO.getLastname()))
                lastName=commAddressDetailsVO.getLastname();
            tmpl_amt=commAddressDetailsVO.getTmpl_amount();
            tmpl_currency=commAddressDetailsVO.getTmpl_currency();

            commAddressDetailsVO.setCardHolderIpAddress(reqIp);
            CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
            accountId = commMerchantVO.getAccountId();

            CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
            commTransactionDetailsVO.setPreviousTransactionId(trackingId);
            commRequestVO.getCardDetailsVO().setcVV(PzEncryptor.decryptCVV(num));
            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
            commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
            commRequestVO.setEci(eci);
            commRequestVO.setNumber(md);
            commRequestVO.setCavv(cavv);
            commRequestVO.setXid(xid);
            commRequestVO.setTransType(responsetransType);

            NestPayPaymentGateway nestPayPaymentGateway = new NestPayPaymentGateway(accountId);
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
            merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(toid);

            auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
            auditTrailVO.setActionExecutorId(toid);

            if (merchantDetailsVO != null)
            {
                clkey = merchantDetailsVO.getKey();
                autoredirect = merchantDetailsVO.getAutoRedirect();
                logoName = merchantDetailsVO.getLogoName();
                partnerName = merchantDetailsVO.getPartnerName();
                isService = merchantDetailsVO.getIsService();
                isPowerBy= merchantDetailsVO.getIsPoweredBy();
            }

            commMerchantVO.setIsService(isService);
            String transType = "Sale";
            CommResponseVO transRespDetails = null;
            transactionLogger.debug("dbStatus-----"+dbStatus);

                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    if ("N".equals(isService))
                    {
                        if(mdStatus.equals("1") || mdStatus.equals("2") || mdStatus.equals("3") || mdStatus.equals("4")){
                            entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, null);
                            transRespDetails = (CommResponseVO) nestPayPaymentGateway.process3DConfirmation(trackingId,commRequestVO);
                        }else if ((mdStatus.equals("5") || mdStatus.equals("7") || mdStatus.equals("8"))){
                            transRespDetails = (CommResponseVO) nestPayPaymentGateway.processAuthConfirmation(trackingId, commRequestVO);
                        }
                        transType = "Auth";
                    }
                    else
                    {
                        if(mdStatus.equals("1") || mdStatus.equals("2") || mdStatus.equals("3") || mdStatus.equals("4")){
                            entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, null);
                            transRespDetails = (CommResponseVO) nestPayPaymentGateway.process3DConfirmation(trackingId,commRequestVO);
                        }else if ((mdStatus.equals("5") || mdStatus.equals("7") || mdStatus.equals("8"))){
                            transRespDetails = (CommResponseVO) nestPayPaymentGateway.processSaleConfirmation(trackingId,commRequestVO);
                        }
                    }

                    if(mdStatus.equals("0") || mdStatus.equals("6")){
                        transactionStatus="fail";
                        message=message;
                    }
                    if (transRespDetails != null)
                    {
                        transactionStatus = transRespDetails.getStatus();
                        transactionId = transRespDetails.getTransactionId();
                        message = transRespDetails.getDescription();
                        transactionLogger.error("eci--------"+eci);

                    }

                    StringBuffer dbBuffer = new StringBuffer();
                    if ("success".equals(transactionStatus))
                    {
                        status = "success";
                        confirmStatus = "Y";
                        responseStatus = "Successful";
                        billingDesc = gatewayAccount.getDisplayName();
                        if ("Sale".equalsIgnoreCase(transType))
                        {
                            if(mdStatus.equals("1") || mdStatus.equals("2") || mdStatus.equals("3") || mdStatus.equals("4")){
                                transRespDetails.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                            }else {
                                transRespDetails.setTransactionType(PZProcessType.SALE.toString());
                            }
                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess' ,eci='"+eci+"'");
                            paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails,null,auditTrailVO);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId,"capturesuccess");
                            updatedStatus="capturesuccess";
                        }
                        else
                        {
                            if(mdStatus.equals("1") || mdStatus.equals("2") || mdStatus.equals("3") || mdStatus.equals("4")){
                                transRespDetails.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                            }else {
                                transRespDetails.setTransactionType(PZProcessType.AUTH.toString());
                            }
                            dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful' ,eci='"+eci+"'");
                            paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId,"authsuccessful");
                            updatedStatus="authsuccessful";
                        }
                    }
                    else
                    {
                        confirmStatus = "N";
                        status = "fail";
                        responseStatus = "Failed(" + message + ")";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='"+eci+"'");
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                        updatedStatus="authfailed";
                    }
                    dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                    con = Database.getConnection();
                    Database.executeUpdate(dbBuffer.toString(), con);
                    transactionLogger.debug("-----dbBuffer-----" + dbBuffer);

                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                }
                else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = displayName;
                        status = "success";
                        message = "Transaction Successful";
                        responseStatus = "Successful";
                        updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    }
                    else if(PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus)){
                        billingDesc = displayName;
                        status = "success";
                        message = "Transaction Successful";
                        responseStatus = "Successful";
                        updatedStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                    }
                    else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus)){
                        status = "fail";
                        message = "Transaction Failed";
                        responseStatus = "Failed";
                        updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                    }
                    else
                    {
                        status = "fail";
                        message = "Transaction Declined";
                        responseStatus = "Failed";
                        updatedStatus=PZTransactionStatus.FAILED.toString();
                    }
                }

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
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);
                addressDetailsVO.setEmail(email);
                addressDetailsVO.setFirstname(firstName);
                addressDetailsVO.setLastname(lastName);

                commCardDetailsVO.setCardNum(cardNum);
                commonValidatorVO.setCustomerId(custId);
                commonValidatorVO.setCustomerBankId(custId);
                commonValidatorVO.setEci(eci);

                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setCardDetailsVO(commCardDetailsVO);

            if (functions.isValueNull(notificationUrl))
            {
                transactionLogger.error("inside sending notification---" + notificationUrl);
                TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, updatedStatus, message,"");
            }

                if ("Y".equalsIgnoreCase(autoredirect))
                {
                    transactionLogger.error("responseStatus in ---" + responseStatus);
                    transactionUtility.doAutoRedirect(commonValidatorVO, response, responseStatus, billingDesc);
                }
                else
                {

                    session.setAttribute("ctoken", ctoken);
                    request.setAttribute("transDetail", commonValidatorVO);
                    request.setAttribute("responceStatus", responseStatus);
                    request.setAttribute("displayName",billingDesc);
                    String confirmationPage = "";
                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";

                    RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(request, response);
                }



        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("NestPayFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
      /*  catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException:::::", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("SafexPayFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause(), toid, null);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("SafexPayFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }*/
        catch (SystemError e)
        {
            transactionLogger.error("SystemError:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("NestPayFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
