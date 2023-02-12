package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.MerchantConfigManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.processing.core.ProcessingPaymentGateway;
import com.payment.processing.core.ProcessingPaymentProcess;
import com.payment.processing.core.ProcessingRequestVO;
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
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Roshan on 2/8/2018.
 */
public class ProcessingFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ProcessingFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }
    public void doService(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        HttpSession session = request.getSession(true);

        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO= new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();

        MerchantDetailsVO merchantDetailsVO = null;
        Connection con = null;

        String toId = "";
        String payModeId = "";
        String cardTypeId = "";
        String isService = "";
        String accountId = "";
        String status = "";
        String responseStatus = "";
        String amount = "";
        String description = "";
        String orderDescription = "";
        String redirectUrl = "";
        String clKey = "";
        String checksumNew = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String confirmStatus = "";
        String isPowerBy = "";

        String token = "";
        String currency = "";
        String billingDesc = "";
        String message="";
        String email="";
        String dbStatus="";
        String firsName = "";
        String lastName = "";

        String transactionId="";
        String transactionStatus="";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String ccnum="";
        String eci="";
        String customerId="";

        Functions functions = new Functions();
        Enumeration enumeration=request.getParameterNames();

        String PARes="";

        PARes=request.getParameter("PaRes");
        String MD=request.getParameter("MD");
        String trackingId=request.getParameter("trackingId");

        try
        {
            ProcessingPaymentProcess processingPaymentProcess=new ProcessingPaymentProcess();
            ProcessingRequestVO commRequestVO=new ProcessingRequestVO();

            processingPaymentProcess.setProcessingRequestVO(commRequestVO, trackingId);

            CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
            description=transactionDetailsVO.getOrderId();
            orderDescription=transactionDetailsVO.getOrderDesc();
            amount=transactionDetailsVO.getAmount();
            currency=transactionDetailsVO.getCurrency();
            toId=transactionDetailsVO.getToId();
            payModeId=transactionDetailsVO.getPaymentType();
            cardTypeId=transactionDetailsVO.getCardType();
            redirectUrl=transactionDetailsVO.getRedirectUrl();
            dbStatus=transactionDetailsVO.getPrevTransactionStatus();
            customerId=transactionDetailsVO.getCustomerId();


            CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
            email=commAddressDetailsVO.getEmail();

            if (functions.isValueNull(commAddressDetailsVO.getFirstname()))
                firsName = commAddressDetailsVO.getFirstname();

            if (functions.isValueNull(commAddressDetailsVO.getLastname()))
                lastName = commAddressDetailsVO.getLastname();

            tmpl_amount=commAddressDetailsVO.getTmpl_amount();
            tmpl_currency=commAddressDetailsVO.getTmpl_currency();

            CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
            ccnum=commCardDetailsVO.getCardNum();


            transactionLogger.debug("tmpl_Amount-----"+tmpl_amount);
            transactionLogger.debug("tmpl_Currency-----"+tmpl_currency);
            transactionLogger.debug("ccnum-----"+ccnum);

            CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
            accountId=commMerchantVO.getAccountId();

            commRequestVO.setPARes(PARes);
            commRequestVO.setMD(MD);

            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            MerchantDAO merchantDAO= new MerchantDAO();
            merchantDetailsVO=merchantDAO.getMemberDetails(toId);

            auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
            auditTrailVO.setActionExecutorId(toId);

            if(merchantDetailsVO!=null){
                autoRedirect = merchantDetailsVO.getAutoRedirect();
                logoName = merchantDetailsVO.getLogoName();
                partnerName = merchantDetailsVO.getPartnerName();
                isService = merchantDetailsVO.getService();
            }


            try
            {
                CommResponseVO transRespDetails=null;
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    String transType = "sale";
                    if("N".equals(isService)){
                        transType = "auth";
                    }
                    ProcessingPaymentGateway processingPaymentGateway=new ProcessingPaymentGateway(accountId);
                    entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, null);
                    transRespDetails=(CommResponseVO)processingPaymentGateway.process3DSaleConfirmation(trackingId,commRequestVO,transType);


                    if (transRespDetails!= null){
                        transactionStatus=transRespDetails.getStatus();
                        transactionId=transRespDetails.getTransactionId();
                        message=transRespDetails.getRemark();
                        eci=transRespDetails.getEci();
                        transactionLogger.debug("eci in front end------------"+eci);
                    }



                    StringBuffer dbBuffer = new StringBuffer();
                    if ("success".equals(transactionStatus))
                    {
                        status = "success";
                        confirmStatus = "Y";
                        responseStatus = "Successful";
                        billingDesc = gatewayAccount.getDisplayName();
                        if ("sale".equals(transType))
                        {
                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess',eci='"+eci+"'");
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                        }
                        else
                        {
                            dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful',eci='"+eci+"'");
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                        }
                    }
                    else
                    {
                        confirmStatus = "N";
                        status = "fail";
                        responseStatus = "Failed(" + message + ")";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='"+eci+"'");
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                    }

                    dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                    con = Database.getConnection();
                    Database.executeUpdate(dbBuffer.toString(), con);

                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                }else
                {
                    if(PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status = "success";
                        message = "Transaction Successful";
                        responseStatus = "Successful";
                    }
                    else
                    {
                        status = "fail";
                        message = "Transaction Declined";
                        responseStatus = "Failed";
                    }
                }
                commonValidatorVO.setTrackingid(trackingId);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setOrderDesc(orderDescription);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);

                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setPaymentType(payModeId);
                commonValidatorVO.setCardType(cardTypeId);
                commonValidatorVO.setCustomerId(customerId);
                if (functions.isValueNull(email))
                    addressDetailsVO.setEmail(email);
                if (functions.isValueNull(firsName))
                    addressDetailsVO.setFirstname(firsName);
                if (functions.isValueNull(lastName))
                    addressDetailsVO.setLastname(lastName);

                addressDetailsVO.setTmpl_amount(tmpl_amount);
                addressDetailsVO.setTmpl_currency(tmpl_currency);

                cardDetailsVO.setCardNum(ccnum);

                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setEci(eci);

                if ("Y".equalsIgnoreCase(autoRedirect) && functions.isValueNull(redirectUrl))
                {
                    transactionUtility.doAutoRedirect(commonValidatorVO, response, responseStatus, billingDesc);
                }
                else
                {
                    request.setAttribute("responceStatus", responseStatus);
                    request.setAttribute("displayName", billingDesc);
                    request.setAttribute("remark", message);
                    request.setAttribute("transDetail", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);

                    String confirmationPage = "";
                    String version = (String)session.getAttribute("version");
                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";
                    RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(request, response);
                    session.invalidate();
                }

            }
            catch (SystemError se){
                transactionLogger.error("SystemError::::::", se);
                PZExceptionHandler.raiseAndHandleDBViolationException("ProcessingFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
            }
            finally{
                Database.closeConnection(con);
            }
        }
        catch(PZTechnicalViolationException e){
            transactionLogger.error("PZTechnicalViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("ProcessingFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch(PZConstraintViolationException e){
            transactionLogger.error("PZConstraintViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleConstraintViolationException("ProcessingFrontEndServlet.java", "doService()", null, "Transaction", null, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch(PZDBViolationException e){
            transactionLogger.error("PZDBViolationException:::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("ProcessingFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch(NoSuchAlgorithmException ne){
            transactionLogger.error("NoSuchAlgorithmException:::::", ne);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("ProcessingFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ne.getMessage(), ne.getCause(), toId, null);
        }
        catch(Exception e){
            transactionLogger.error("Exception:::::",e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("ProcessingFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause(), toId, null);
        }
    }
}
