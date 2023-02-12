package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.MerchantConfigManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.PaymentProcessFactory;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.emax_high_risk.core.Transaction;
import com.payment.emexpay.EmexpayPaymentGateway;
import com.payment.emexpay.EmexpayPaymentProcess;
import com.payment.emexpay.EmexpayRequestVO;
import com.payment.emexpay.EmexpayVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
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
 * Created by Admin on 2/6/18.
 */
public class EMXPFrontEndServlet extends HttpServlet

{
    //EmexpayLogger transactionLogger= new EmexpayLogger(EMXPFrontEndServlet.class.getName());
    TransactionLogger transactionLogger= new TransactionLogger(EMXPFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    Functions functions = new Functions();

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
        transactionLogger.error("Entering into EMXPFrontEndServlet-----"+request.getParameter("trackingId"));
        HttpSession session=request.getSession(true);
        Enumeration enumeration= request.getParameterNames();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();

        while(enumeration.hasMoreElements()){

            String keyName=(String)enumeration.nextElement();
            String keyValue=request.getParameter(keyName);
            transactionLogger.error("Emexpay frontend:::"+keyName + ":" + keyValue);
        }
        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
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
        String currency = "";
        String billingDesc = "";
        String message = "";
        String email = "";
        String dbStatus="";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String ccnum="";
        String firstName="";
        String lastName="";
        String customerId="";

        String transactionId = "";
        String transactionStatus = "";
        String eci="";

        String paRes="";
        String md="";
        String trackingId="";
        String pa_res_url="";
        String id="";
        String uId = "";
        String bankErrorCode = "";
        String pzTrackingid = "";

        paRes=request.getParameter("PaRes");
        md=request.getParameter("MD");
        trackingId=request.getParameter("trackingId");
        uId = request.getParameter("uid");

        if(functions.isValueNull(md))
        {
            String MD[]=md.split("=");
            id=MD[0];
            pa_res_url=MD[1];

        }
        EmexpayPaymentProcess emexpayPaymentProcess = new EmexpayPaymentProcess();
        EmexpayRequestVO commRequestVO = new EmexpayRequestVO();

        try
        {

            emexpayPaymentProcess.setEmexpayRequestVO(commRequestVO, trackingId, paRes,id,pa_res_url);

            CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
            description = transactionDetailsVO.getOrderId();
            orderDescription = transactionDetailsVO.getOrderDesc();
            amount = transactionDetailsVO.getAmount();
            currency = transactionDetailsVO.getCurrency();
            toId = transactionDetailsVO.getToId();
            payModeId = transactionDetailsVO.getPaymentType();
            cardTypeId = transactionDetailsVO.getCardType();
            redirectUrl = transactionDetailsVO.getRedirectUrl();
            dbStatus=transactionDetailsVO.getPrevTransactionStatus();
            tmpl_amount = transactionDetailsVO.getTemplateAmount();
            tmpl_currency = transactionDetailsVO.getTemplateCurrency();


            transactionLogger.debug("tmpl_amount----"+tmpl_amount);
            transactionLogger.debug("tmpl_currency----"+tmpl_currency);
            CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
            ccnum=PzEncryptor.decryptPAN(commCardDetailsVO.getCardNum());

            transactionLogger.debug("ccnum----"+ccnum);

            CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
            if(functions.isValueNull(commAddressDetailsVO.getFirstname()))
                firstName=commAddressDetailsVO.getFirstname();
            if(functions.isValueNull(commAddressDetailsVO.getLastname()))
                lastName=commAddressDetailsVO.getLastname();
            if(functions.isValueNull(commAddressDetailsVO.getCustomerid()))
                customerId=commAddressDetailsVO.getCustomerid();

            email = commAddressDetailsVO.getEmail();

            CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
            accountId = commMerchantVO.getAccountId();

            CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
            commTransactionDetailsVO.setPreviousTransactionId(trackingId);
            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);

            EmexpayPaymentGateway emexpayPaymentGateway = new EmexpayPaymentGateway(accountId);
            MerchantDAO merchantDAO= new MerchantDAO();
            merchantDetailsVO = merchantDAO.getMemberDetails(toId);

            auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
            auditTrailVO.setActionExecutorId(toId);

            if (merchantDetailsVO != null)
            {
                clKey = merchantDetailsVO.getKey();
                autoRedirect = merchantDetailsVO.getAutoRedirect();
                logoName = merchantDetailsVO.getLogoName();
                partnerName = merchantDetailsVO.getPartnerName();
                isService = merchantDetailsVO.getIsService();
            }

            String transType = "Sale";
            EmexpayVO transRespDetails = null;
            transactionLogger.error("dbStatus-----"+dbStatus+"--"+trackingId);
            AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(trackingId), Integer.parseInt(accountId));
            if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
            {
                if ("N".equals(isService))
                {
                    entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, null);
                    if(functions.isValueNull(paRes) && functions.isValueNull(md))
                        transRespDetails = (EmexpayVO) emexpayPaymentGateway.process3DAuthConfirmation(trackingId, commRequestVO);
                    else
                    {
                        commTransactionDetailsVO.setPreviousTransactionId(uId);
                        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                        transRespDetails = (EmexpayVO) emexpayPaymentGateway.processQuery(trackingId, commRequestVO);
                    }
                    transType = "Auth";
                }
                else
                {
                    entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, null);
                    if(functions.isValueNull(paRes) && functions.isValueNull(md))
                        transRespDetails = (EmexpayVO) emexpayPaymentGateway.process3DSaleConfirmation(trackingId, commRequestVO);
                    else
                    {
                        commTransactionDetailsVO.setPreviousTransactionId(uId);
                        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                        transRespDetails = (EmexpayVO) emexpayPaymentGateway.processQuery(trackingId, commRequestVO);
                    }

                }

                if (transRespDetails != null)
                {
                    transactionStatus = transRespDetails.getStatus();
                    transactionId = transRespDetails.getTransactionId();
                    message = transRespDetails.getRemark();
                    eci=transRespDetails.getEci();
                    bankErrorCode = transRespDetails.getErrorCode();
                    pzTrackingid = transRespDetails.getMerchantOrderId();
                }
                StringBuffer dbBuffer = new StringBuffer();
                transactionLogger.error("From Request="+trackingId+"--From Response="+pzTrackingid);
                transactionLogger.error("Bank Error Code---"+bankErrorCode);
                if(functions.isValueNull(pzTrackingid) && trackingId.equals(pzTrackingid))
                {
                    if ("success".equals(transactionStatus) && ("000".equals(bankErrorCode) || "05".equals(bankErrorCode)))
                    {
                        status = "success";
                        confirmStatus = "Y";
                        responceStatus = "Successful";
                        billingDesc = transRespDetails.getDescriptor();
                        if ("Sale".equalsIgnoreCase(transType))
                        {
                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess',eci='" + eci + "'");
                            paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                        }
                        else
                        {
                            dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful',eci='" + eci + "'");
                            paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                        }
                    }
                    else
                    {
                        confirmStatus = "N";
                        status = "fail";
                        responceStatus = "Failed(" + message + ")";
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='" + eci + "'");
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                    }
                    dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                    con = Database.getConnection();
                    Database.executeUpdate(dbBuffer.toString(), con);
                    transactionLogger.error("-----dbBuffer-----" + dbBuffer);

                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                }
                else //pending
                {
                    transactionLogger.error("Inside 2nd Time Inquiry Called---");
                    responceStatus = this.inquiryStatusUpdateCallTwo(trackingId,accountId,merchantDetailsVO,con,transactionDetailsVO,paymentProcess);

                    if(!functions.isValueNull(responceStatus))
                    {
                        status = "pending";
                        responceStatus = "Pending";
                    }
                    //responceStatus = "Pending";
                }
            }
            else
            {
                if(PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
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
            commCardDetailsVO.setCardNum(ccnum);
            if (functions.isValueNull(email))
                addressDetailsVO.setEmail(email);
            if(functions.isValueNull(firstName))
            addressDetailsVO.setFirstname(firstName);
            if(functions.isValueNull(lastName))
                addressDetailsVO.setLastname(lastName);
            addressDetailsVO.setTmpl_amount(tmpl_amount);
            addressDetailsVO.setTmpl_currency(tmpl_currency);

            commonValidatorVO.setCustomerId(customerId);
            commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
            commonValidatorVO.setCardDetailsVO(commCardDetailsVO);
            commonValidatorVO.setTransDetailsVO(transactionDetailsVO);
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
                if(functions.isValueNull(commTransactionDetailsVO.getVersion()) && commTransactionDetailsVO.getVersion().equalsIgnoreCase("2"))
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
            PZExceptionHandler.raiseAndHandleDBViolationException("EMXPFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("EMXPFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("EMXPFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("error::::", e);
        }

        finally
        {
            if(con!=null)
                Database.closeConnection(con);
        }
    }

    public String inquiryStatusUpdateCallTwo(String trackingId,String accountId,MerchantDetailsVO merchantDetailsVO,Connection con,CommTransactionDetailsVO transactionDetailsVO,AbstractPaymentProcess paymentProcess)throws PZConstraintViolationException,PZGenericConstraintViolationException,SystemError
    {
        transactionLogger.error("inside inquiryStatusUpdateCallTwo---"+trackingId);
        String transactionStatus = "";
        String transactionId = "";
        String message = "";
        String eci = "";
        String bankErrorCode = "";
        String pzTrackingid = "";
        String status = "";
        String responceStatus = "";
        String billingDesc = "";
        String transType = "Sale";

        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();

        String amount = transactionDetailsVO.getAmount();

        if("N".equalsIgnoreCase(merchantDetailsVO.getIsService()))
            transType = "Auth";

        EmexpayPaymentGateway emexpayPaymentGateway = new EmexpayPaymentGateway(accountId);
        EmexpayRequestVO commRequestVO = new EmexpayRequestVO();

        //every time 2nd call goes with trackingid
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);

        transactionLogger.error("Before processQuery from call2---"+trackingId);
        EmexpayVO transRespDetails = (EmexpayVO) emexpayPaymentGateway.processQuery(trackingId, commRequestVO);
        transactionLogger.error("After processQuery from call2---"+trackingId);

        if (transRespDetails != null)
        {
            transactionStatus = transRespDetails.getStatus();
            transactionId = transRespDetails.getTransactionId();
            message = transRespDetails.getRemark();
            eci=transRespDetails.getEci();
            bankErrorCode = transRespDetails.getErrorCode();
            pzTrackingid = transRespDetails.getMerchantOrderId();
        }

        StringBuffer dbBuffer = new StringBuffer();
        transactionLogger.error("From Request call2="+trackingId+"--From Response call2="+pzTrackingid);
        transactionLogger.error("Bank Error Code call2---"+bankErrorCode);
        if(functions.isValueNull(pzTrackingid) && trackingId.equals(pzTrackingid))
        {
            if ("success".equals(transactionStatus) && ("000".equals(bankErrorCode) || "05".equals(bankErrorCode)))
            {
                status = "success";
                responceStatus = "Successful";
                billingDesc = transRespDetails.getDescriptor();

                if ("Sale".equalsIgnoreCase(transType))
                {
                    dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess',eci='" + eci + "'");
                    paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                }
                else
                {
                    dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful',eci='" + eci + "'");
                    paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                }
            }
            else
            {
                status = "fail";
                responceStatus = "Failed(" + message + ")";
                dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='" + eci + "'");
                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
            }
            dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
            con = Database.getConnection();
            Database.executeUpdate(dbBuffer.toString(), con);
            transactionLogger.error("-----dbBuffer call2-----" + dbBuffer);

            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

            AsynchronousSmsService smsService = new AsynchronousSmsService();
            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

           if(con!=null){
               Database.closeConnection(con);
           }
        }
        return responceStatus;
    }
}
