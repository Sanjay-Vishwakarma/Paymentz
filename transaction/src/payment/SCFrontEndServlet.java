package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.SafeChargePaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.directi.pg.core.valueObjects.SafeChargeRequestVO;
import com.manager.MerchantConfigManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.SafeChargePaymentProcess;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
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
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Sandip on 10/20/2017.
 */
public class SCFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(SCFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);


    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        doService(req, res);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession(true);

        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO= new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();

        MerchantDetailsVO merchantDetailsVO = null;
        Connection con = null;

        String toId = "";
        String payModeId = "";
        String cardTypeId = "";
        String isService = "";
        String accountId = "";
        String status = "";
        String responceStatus = "";
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
        String orderDesc = "";
        String currency = "";
        String billingDesc = "";
        String message = "";
        String email = "";
        String dbStatus="";
        String PARes ="";
        String firstName="";
        String lastName="";
        String tmpl_Amount="";
        String tmpl_Currency="";
        String ccnum="";

        String transactionId = "";
        String transactionStatus = "";
        String eci="";
        String customerId="";

        Functions functions = new Functions();
        Transaction transaction = new Transaction();

        Enumeration enumeration = request.getParameterNames();
       /* while (enumeration.hasMoreElements())
        {
            String keyName = (String) enumeration.nextElement();
            String keyValue = request.getParameter(keyName);
            transactionLogger.error(keyName + ":" + keyValue);
        }
*/
        /*if ( functions.isValueNull(request.getParameter("PaRes")) )
        {*/
             PARes = request.getParameter("PaRes");
            String trackingIdCVVString = request.getParameter("MD");
            String trackingId = "";
            String cvv = "";


            try
            {
               /* if(functions.isValueNull(PARes)){
                    ParesDecodeRequestVO paresDecodeRequestVO=new ParesDecodeRequestVO();
                    paresDecodeRequestVO.setMassageID(trackingId);
                    paresDecodeRequestVO.setPares(PARes);
                    paresDecodeRequestVO.setTrackid(trackingId);

                    EndeavourMPIGateway endeavourMPIGateway=new EndeavourMPIGateway();
                    ParesDecodeResponseVO paresDecodeResponseVO=endeavourMPIGateway.processParesDecode(paresDecodeRequestVO);

                    if(functions.isValueNull(paresDecodeResponseVO.getEci())){
                        eci=paresDecodeResponseVO.getEci();
                        transactionLogger.error("ECI-----"+eci);
                    }
                }*/

                if (functions.isValueNull(trackingIdCVVString))
                {
                    String trackingIdCVVArr[] = trackingIdCVVString.split("@");
                    trackingId = trackingIdCVVArr[0];
                    cvv = trackingIdCVVArr[1];
                }

                SafeChargePaymentProcess safeChargePaymentProcess = new SafeChargePaymentProcess();
                SafeChargeRequestVO commRequestVO = new SafeChargeRequestVO();

                safeChargePaymentProcess.setSafeChargeRequestVO(commRequestVO, trackingId, PARes, cvv);

                CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
                description = transactionDetailsVO.getOrderId();
                orderDescription = transactionDetailsVO.getOrderDesc();
                amount = transactionDetailsVO.getAmount();
                currency = transactionDetailsVO.getCurrency();
                toId = transactionDetailsVO.getToId();
                payModeId = transactionDetailsVO.getPaymentType();
                cardTypeId = transactionDetailsVO.getCardType();
                dbStatus=transactionDetailsVO.getPrevTransactionStatus();
                redirectUrl=transactionDetailsVO.getRedirectUrl();
                customerId=transactionDetailsVO.getCustomerId();

                CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
                ccnum=commCardDetailsVO.getCardNum();

                CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
                email = commAddressDetailsVO.getEmail();
                tmpl_Amount=commAddressDetailsVO.getTmpl_amount();
                tmpl_Currency=commAddressDetailsVO.getTmpl_currency();

                if(functions.isValueNull(commAddressDetailsVO.getFirstname()))
                    firstName=commAddressDetailsVO.getFirstname();
                if(functions.isValueNull(commAddressDetailsVO.getLastname()))
                    lastName=commAddressDetailsVO.getLastname();

                CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
                accountId = commMerchantVO.getAccountId();
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                MerchantDAO merchantDAO= new MerchantDAO();
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);

                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toId);

                if (merchantDetailsVO != null)
                {
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                    isService = merchantDetailsVO.getIsService();
                }

                try
                {
                    CommResponseVO transRespDetails=null;
                    transactionLogger.error("DBStatus Safecharge FrontEnd---"+dbStatus);
                    if(PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                    {
                        String transType = "Sale";
                        if ("N".equals(isService))
                        {
                            transType = "Auth";
                        }
                        transactionLogger.error("before process3DSaleConfirmation Call---");
                        SafeChargePaymentGateway safeChargePaymentGateway = new SafeChargePaymentGateway(accountId);
                        entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, null);
                         transRespDetails = (CommResponseVO) safeChargePaymentGateway.process3DSaleConfirmation(trackingId, commRequestVO);

                        if (transRespDetails != null)
                        {
                            transactionStatus = transRespDetails.getStatus();
                            transactionId = transRespDetails.getTransactionId();
                            message = transRespDetails.getRemark();
                        }
                        transactionLogger.error("Response after process3DSaleConfirmation---"+message+"--"+transactionStatus);

                        StringBuffer dbBuffer = new StringBuffer();
                        if ("success".equals(transactionStatus))
                        {
                            status = "success";
                            confirmStatus = "Y";
                            responceStatus = "Successful";
                            billingDesc = gatewayAccount.getDisplayName();
                            if ("Sale".equalsIgnoreCase(transType))
                            {
                                dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess',eci='"+eci+"'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                            }
                            else
                            {
                                dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful',eci='"+eci+"'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                            }
                        }
                        else
                        {
                            confirmStatus = "N";
                            status = "fail";
                            responceStatus = "Failed(" + message + ")";
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
                        if(PZTransactionStatus.CAPTURE_SUCCESS.toString().equals("capturesuccess") || PZTransactionStatus.AUTH_SUCCESS.toString().equals("authsuccessful"))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            message = "Transaction Successful";
                            responceStatus = "Successful";
                        }
                        else
                        {
                            status = "fail";
                            message = "Transaction Declined";
                            responceStatus = "Failed";
                        }
                    }

                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setOrderDesc(orderDescription);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);

                    cardDetailsVO.setCardNum(ccnum);

                    if (functions.isValueNull(email))
                        addressDetailsVO.setEmail(email);
                    if (functions.isValueNull(firstName))
                        addressDetailsVO.setFirstname(firstName);
                    if (functions.isValueNull(lastName))
                        addressDetailsVO.setLastname(lastName);

                    addressDetailsVO.setTmpl_amount(tmpl_Amount);
                    addressDetailsVO.setTmpl_currency(tmpl_Currency);

                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setPaymentType(payModeId);
                    commonValidatorVO.setCardType(cardTypeId);
                    commonValidatorVO.setTrackingid(trackingId);
                    commonValidatorVO.setCustomerId(customerId);

                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setEci(eci);

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
                catch (SystemError se)
                {
                    transactionLogger.error("SystemError::::::", se);
                    PZExceptionHandler.raiseAndHandleDBViolationException("SCFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
                }
                finally
                {
                    Database.closeConnection(con);
                }
            }
            catch (PZTechnicalViolationException e)
            {
                transactionLogger.error("error::::", e);
                PZExceptionHandler.raiseAndHandleTechnicalViolationException("SCFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause(), toId, null);
                //transactionUtility.doAutoRedirect(commonValidatorVO, response, responceStatus, billingDesc);
            }
            catch (PZConstraintViolationException e)
            {
                transactionLogger.error("error::::", e);
                PZExceptionHandler.raiseAndHandleConstraintViolationException("SCFrontEndServlet.java", "doService()", null, "Transaction", null, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, e.getMessage(), e.getCause(), toId, null);
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("error::::", e);
                PZExceptionHandler.raiseAndHandleDBViolationException("SCFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
            }
        }
   /* }*/
}
