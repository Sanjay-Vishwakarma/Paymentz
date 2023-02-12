package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.MerchantConfigManager;
import com.manager.vo.MerchantDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.paynetics.core.PayneticsGateway;
import com.payment.paynetics.core.PayneticsPaymentProcess;
import com.payment.paynetics.core.PayneticsRequestVO;
import com.payment.sms.AsynchronousSmsService;
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
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by admin on 12/6/2017.
 */
public class PayneticsFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PayneticsFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        doService(req,res);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        HttpSession session = request.getSession(true);

        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();

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
        String PARes="";
        String eci="";
        String errorName = "";

        String transactionId="";
        String transactionStatus="";

        Functions functions=new Functions();
        Transaction transaction=new Transaction();

        Enumeration enumeration=request.getParameterNames();
        while(enumeration.hasMoreElements()){
            String keyName=(String)enumeration.nextElement();
            String keyValue=request.getParameter(keyName);
            transactionLogger.error(keyName+":"+keyValue);
        }

         PARes=request.getParameter("PaRes");
        String trackingId=request.getParameter("referenceNo");

        try
        {
            PayneticsPaymentProcess payneticsPaymentProcess=new PayneticsPaymentProcess();
            PayneticsRequestVO commRequestVO=new PayneticsRequestVO();

            payneticsPaymentProcess.setPayneticsRequestVO(commRequestVO, trackingId);

            CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
            description=transactionDetailsVO.getOrderId();
            orderDescription=transactionDetailsVO.getOrderDesc();
            amount=transactionDetailsVO.getAmount();
            currency=transactionDetailsVO.getCurrency();
            toId=transactionDetailsVO.getToId();
            payModeId=transactionDetailsVO.getPaymentType();
            cardTypeId=transactionDetailsVO.getCardType();
            redirectUrl=transactionDetailsVO.getRedirectUrl();

            CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
            email=commAddressDetailsVO.getEmail();

            CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
            accountId=commMerchantVO.getAccountId();

            commRequestVO.setPARes(PARes);

            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            MerchantConfigManager merchantConfigManager=new MerchantConfigManager();
            merchantDetailsVO=merchantConfigManager.getMerchantDetailFromToId(toId);

            auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
            auditTrailVO.setActionExecutorId(toId);

            if(merchantDetailsVO!=null){
                clKey = merchantDetailsVO.getKey();
                autoRedirect = merchantDetailsVO.getAutoRedirect();
                logoName = merchantDetailsVO.getLogoName();
                partnerName = merchantDetailsVO.getPartnerName();
                isService = merchantDetailsVO.getService();
            }

            String transType = "sale";
            if("N".equals(isService)){
                transType = "auth";
            }
            CommResponseVO transRespDetails=null;

            PayneticsGateway payneticsGateway=new PayneticsGateway(accountId);
            entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, null);
            transRespDetails=(CommResponseVO)payneticsGateway.process3DSaleConfirmation(trackingId,commRequestVO,transType);

            if (transRespDetails!= null){
                transactionStatus=transRespDetails.getStatus();
                transactionId=transRespDetails.getTransactionId();
                message=transRespDetails.getRemark();
                eci=transRespDetails.getEci();
                if (functions.isValueNull(transRespDetails.getErrorName()))
                    errorName = transRespDetails.getErrorName();

            }

            try
            {
                StringBuffer dbBuffer = new StringBuffer();
                if ("success".equals(transactionStatus)){
                    status = "success";
                    confirmStatus = "Y";
                    responseStatus="Successful";
                    billingDesc=gatewayAccount.getDisplayName();
                    if ("sale".equals(transType)){
                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId+ "',successtimestamp='" + functions.getTimestamp() + "',status='capturesuccess',eci='"+eci+"'");
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                    }
                    else{
                        dbBuffer.append("update transaction_common set paymentid='" + transactionId+ "',successtimestamp='" + functions.getTimestamp() + "' ,status='authsuccessful',eci='"+eci+"'");
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                    }
                }
                else{
                    confirmStatus = "N";
                    status = "fail";
                    responseStatus="Failed("+message+")";
                    dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',failuretimestamp='" + functions.getTimestamp() +
"',eci='"+eci+"'");
                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                }
                dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                con=Database.getConnection();
                Database.executeUpdate(dbBuffer.toString(), con);

                /*AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
*/
                checksumNew = Checksum.generateChecksumForStandardKit(trackingId, description, String.valueOf(amount), confirmStatus, clKey);

                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setOrderDesc(orderDescription);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);

                merchantDetailsVO.setPoweredBy(isPowerBy);

                if (functions.isValueNull(email))
                    addressDetailsVO.setEmail(email);

                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setPaymentType(payModeId);
                commonValidatorVO.setCardType(cardTypeId);
                commonValidatorVO.setTrackingid(trackingId);

                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setEci(eci);
                commonValidatorVO.setErrorName(errorName);

                if("Y".equalsIgnoreCase(autoRedirect))
                {
                    TransactionUtility transactionUtility = new TransactionUtility();
                    transactionUtility.doAutoRedirect(commonValidatorVO,response,responseStatus,billingDesc);
                }
                else
                {
                    request.setAttribute("responceStatus", responseStatus);
                    request.setAttribute("displayName", billingDesc);
                    request.setAttribute("remark", message);
                    request.setAttribute("transDetail", commonValidatorVO);
                    request.setAttribute("errorName", errorName);
                    session.setAttribute("ctoken", ctoken);

                    String confirmationPage = "";
                    String version = (String)session.getAttribute("version");
                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";
                    session.invalidate();
                    RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(request, response);
                }
            }
            catch (SystemError se){
                transactionLogger.error("SystemError::::::", se);
                PZExceptionHandler.raiseAndHandleDBViolationException("PayneticsFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
            }
            finally{
                Database.closeConnection(con);
            }
        }
        catch(PZTechnicalViolationException e){
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("PayneticsFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch(PZConstraintViolationException e){
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleConstraintViolationException("PayneticsFrontEndServlet.java", "doService()", null, "Transaction", null, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch(PZDBViolationException e){
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("PayneticsFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch(NoSuchAlgorithmException ne){
            transactionLogger.error("error:::", ne);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("PayneticsFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ne.getMessage(), ne.getCause(), toId, null);
        }
    }
}
