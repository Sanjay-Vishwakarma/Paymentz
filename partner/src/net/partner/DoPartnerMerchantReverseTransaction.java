package net.partner;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.CUPPaymentGateway;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MarketPlaceVO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.PaymentProcessFactory;
import com.payment.cup.core.CupPaymentProcess;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.request.PZRefundRequest;
import com.payment.response.PZRefundResponse;
import com.payment.response.PZResponseStatus;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

public class DoPartnerMerchantReverseTransaction extends HttpServlet
{
    private static Logger logger = new Logger(DoPartnerMerchantReverseTransaction.class.getName());
    Connection conn=null;
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session             = req.getSession();
        PartnerFunctions partner        = new PartnerFunctions();
        Functions functions             = new Functions();
        ResourceBundle rb1              = null;
        String language_property1       = (String) session.getAttribute("language_property");
        rb1                             = LoadProperties.getProperty(language_property1);
        String DoPartnerMerchantReverseTransaction_refund_errormsg      = StringUtils.isNotEmpty(rb1.getString("DoPartnerMerchantReverseTransaction_refund_errormsg")) ? rb1.getString("DoPartnerMerchantReverseTransaction_refund_errormsg") : "Please give valid reason and refund amount for reversal.";
        String DoPartnerMerchantReverseTransaction_tracking_errormsg    = StringUtils.isNotEmpty(rb1.getString("DoPartnerMerchantReverseTransaction_tracking_errormsg")) ? rb1.getString("DoPartnerMerchantReverseTransaction_tracking_errormsg") : "Refund Operation is successful for Tracking ID:";

        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        String trackingId           = null;
        String memberId             = req.getParameter("toid");
        //String description=null;
        String refundAmount=null;
        RefundChecker refundChecker=new RefundChecker();
        String reason=null;
        String transId=null;
        String accountId=null;
        String captureAmount=null;
        String gatewayType = null;
        String mailStatus="failed";
        String ipAddress=req.getRemoteAddr();
        String responseMessage="";
        String responseInfo="";
        String currency = "";
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        String transStatus = "";
        String reversedAmount = "";
        String isFraud="";
        String parentTrackingid="";
        String parentCapturedAmount = "";
        String parentReversedAmount = "";
        String parentTransStatus="";
        String childTrackingid="";
        String childCaptureAmount="";
        String childRefundAmount="";
        String childReversedAmount = "";
        String marketPlaceFlag="";
        String childreason="";
        boolean isFraudNew=false;
        boolean childIsFraud=false;
        MerchantDAO merchantDAO                 = new MerchantDAO();
        MarketPlaceVO marketPlaceVO             = null;
        MerchantDetailsVO merchantDetailsVO     = null;
        MerchantDetailsVO merchantDetailsVO1    = null;
        List<MarketPlaceVO> childDetailList     = new ArrayList<>();
        String notificationURL                  = "";


        try
        {
            reason          = ESAPI.validator().getValidInput("reason", req.getParameter("reason"), "alphanum", 100, false);
            refundAmount    = ESAPI.validator().getValidInput("refundamount", req.getParameter("refundamount"), "SafeString", 20, false);
            trackingId      = ESAPI.validator().getValidInput("icicitransid", req.getParameter("icicitransid"), "Numbers", 20, false);
            transId         = ESAPI.validator().getValidInput("transid", req.getParameter("transid"), "Numbers", 20, false);
            accountId       = ESAPI.validator().getValidInput("accountid", req.getParameter("accountid"), "Numbers", 20, true);
            captureAmount   = req.getParameter("captureamount");
            currency        = req.getParameter("currency");
            isFraud         = req.getParameter("isFraud");
            parentTrackingid        = req.getParameter("parentTrackingid");
            parentCapturedAmount    = req.getParameter("parentcaptureAmount");
            marketPlaceFlag         = req.getParameter("marketPlaceFlag");
            String childhashsize    = req.getParameter("childhashsize")==null?"0":req.getParameter("childhashsize");
            String[] ids            = req.getParameterValues("checkbox_id");
            float childRefundAmtSum = 0;
            if(ids != null && !"N".equalsIgnoreCase(marketPlaceFlag))
            {
                for(String i:ids)
                {
                     marketPlaceVO          = new MarketPlaceVO();
                    childTrackingid         =req.getParameter("childicicitransid"+i);
                    childreason             = req.getParameter("childreason" + i)==null?reason:req.getParameter("childreason" + i);
                    Hashtable transDetails  = TransactionEntry.getTransDetails(childTrackingid);
                    String childToid=(String)transDetails.get("toid");
                    merchantDetailsVO       = merchantDAO.getMemberDetails(childToid);
                    childReversedAmount     = (String) transDetails.get("refundamount");
                    childRefundAmount       = ESAPI.validator().getValidInput("refundamount", req.getParameter("childrefundamount" + i), "Amount", 20, false);
                    childRefundAmtSum += Float.parseFloat(childRefundAmount);
                    childCaptureAmount = req.getParameter("childcaptureamount" + i);
                    marketPlaceVO.setMemberid(childToid);
                    marketPlaceVO.setStatus((String) transDetails.get("status"));
                    marketPlaceVO.setMerchantDetailsVO(merchantDetailsVO);
                    marketPlaceVO.setTrackingid(childTrackingid);
                    marketPlaceVO.setCapturedAmount(childCaptureAmount);
                    marketPlaceVO.setRefundAmount(childRefundAmount);
                    marketPlaceVO.setReversedAmount(childReversedAmount);
                    marketPlaceVO.setRefundReason(childreason);
                    if ("Y".equals(req.getParameter("isFraud" + i)))
                        childIsFraud = true;
                    marketPlaceVO.setFraud(childIsFraud);
                    childDetailList.add(marketPlaceVO);
                }
                if(!refundAmount.equals(String.format("%.2f",childRefundAmtSum)))
                {
                    responseMessage = "Reversal Amount Mismatch.";
                    req.setAttribute("message",responseMessage);
                    RequestDispatcher rd = req.getRequestDispatcher("/net/GetRefundDetails?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
            }
            if("N".equalsIgnoreCase(marketPlaceFlag) && !"0".equalsIgnoreCase(childhashsize))//Full Refund for Parent when marketPlace Flag is N
            {
                if(Double.parseDouble(refundAmount) <= Double.parseDouble(captureAmount))
                {
                    responseMessage = "Refund functionality is not supported. Please contact your Administrator.";
                    req.setAttribute("message",responseMessage);
                    RequestDispatcher rd = req.getRequestDispatcher("/net/GetRefundDetails?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
            }
        }
        catch (ValidationException e)
        {
            logger.error("Invalid data for reason and amount", e);
            PZExceptionHandler.raiseAndHandleConstraintViolationException("DoPartnerMerchantReverseTransaction.java","doPost()",null,"Merchant","Validation Exception Thrown:::", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,e.getMessage(),e.getCause(),memberId, PZOperations.MERCHANT_REFUND);
            responseMessage=DoPartnerMerchantReverseTransaction_refund_errormsg;
            req.setAttribute("message",responseMessage);
            RequestDispatcher rd = req.getRequestDispatcher("/net/GetRefundDetails?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        catch (PZDBViolationException e)
        {
            logger.error("Invalid data for reason and amount", e);
            PZExceptionHandler.raiseAndHandleConstraintViolationException("DoPartnerMerchantReverseTransaction.java","doPost()",null,"Merchant","Validation Exception Thrown:::", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,e.getMessage(),e.getCause(),memberId, PZOperations.MERCHANT_REFUND);
            responseMessage=DoPartnerMerchantReverseTransaction_refund_errormsg;
            req.setAttribute("message",responseMessage);
            RequestDispatcher rd = req.getRequestDispatcher("/net/GetRefundDetails?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        try
        {
            try
            {
                Double.parseDouble(refundAmount);
            }
            catch (NumberFormatException n)
            {
                logger.error("NumberFormatException in Partner Refund---",n);
                throw new Exception("Error while Parsing refund amount in double");
            }
            if (!Functions.checkAccuracy(refundAmount, 2))
            {
                PZExceptionHandler.raiseConstraintViolationException("DoPartnerMerchantReverseTransaction.java","doPost()",null,"Merchant", ErrorMessages.REFUND_AMT_ACCURACY,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }
            if (accountId != null && !accountId.equals(""))
            {
                gatewayType = GatewayAccountService.getGatewayAccount(accountId).getGateway();
            }
            if(!refundChecker.isRefundAllowed(memberId))
            {
                PZExceptionHandler.raiseConstraintViolationException("DoPartnerMerchantReverseTransaction.java","doPost()",null,"Merchant", ErrorMessages.REFUND_ALLOWED,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }
            Hashtable transDetails          = TransactionEntry.getTransDetails(trackingId);
            transStatus                     = (String) transDetails.get("status");
            reversedAmount                  = (String) transDetails.get("refundamount");
            merchantDetailsVO               = merchantDAO.getMemberDetails(memberId);
            String transactionDate          = (String) transDetails.get("transactiondate");
            if(transDetails.get("notificationUrl") != null){
                notificationURL          = (String) transDetails.get("notificationUrl");
            }

            SimpleDateFormat targetFormat   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long d                          = Functions.DATEDIFF(transactionDate, targetFormat.format(new Date()));
            int refundAllowedDays           = 0;
            //int refundAllowedDays = Integer.parseInt(merchantDetailsVO.getRefundAllowedDays());
            if(!"N".equalsIgnoreCase(marketPlaceFlag) && (childDetailList != null || childDetailList.size()>0))
            {
                for(int i=0;i<childDetailList.size();i++)
                {
                    marketPlaceVO   = (MarketPlaceVO)childDetailList.get(i);
                    logger.error("marketPlaceVO.getMemberid()--------------->"+marketPlaceVO.getMemberid());
                    if(!refundChecker.isRefundAllowed(marketPlaceVO.getMemberid()))
                    {
                        PZExceptionHandler.raiseConstraintViolationException("DoReverseTransaction.java","doPost()",null,"Merchant", ErrorMessages.VENDOR_REFUND_ALLOWED,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
                    }
                    merchantDetailsVO1  = marketPlaceVO.getMerchantDetailsVO();
                    String childstatus  = marketPlaceVO.getStatus();
                    if("N".equalsIgnoreCase(merchantDetailsVO1.getPartialRefund())&& Double.parseDouble(marketPlaceVO.getCapturedAmount())!=Double.parseDouble(marketPlaceVO.getRefundAmount()))
                    {
                        responseMessage = "Partial Refund is not allowed for vendor merchant.";
                        req.setAttribute("message",responseMessage);
                        RequestDispatcher rd = req.getRequestDispatcher("/net/PartnerMerchantRefundList?ctoken="+user.getCSRFToken());
                        rd.forward(req, res);
                        return;
                    }
                    if("N".equalsIgnoreCase(merchantDetailsVO1.getMultipleRefund()) && childstatus.equalsIgnoreCase(PZTransactionStatus.REVERSED.toString())){
                        responseMessage = "Multiple Refund is not allowed for vendor merchant.";
                        req.setAttribute("message",responseMessage);
                        RequestDispatcher rd = req.getRequestDispatcher("/net/PartnerMerchantRefundList?ctoken="+user.getCSRFToken());
                        rd.forward(req, res);
                        return;
                    }
                    refundAllowedDays = Integer.parseInt(merchantDetailsVO1.getRefundAllowedDays());
                    if (d > refundAllowedDays)
                    {
                        PZExceptionHandler.raiseConstraintViolationException("DoReverseTransaction.java", "doPost()", null, "Merchant", ErrorMessages.REFUND_ALLOWEDDAYS_VALIDATION, PZConstraintExceptionEnum.INVALID_REFUND_REQUEST, null, null, null);
                    }
                    else if(d == refundAllowedDays){
                        long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000;
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "dd/MM/yyyy HH:mm:ss");
                        Date dateIni    = null;
                        Date dateFin     = null;
                        dateIni     = format.parse(transactionDate);
                        dateFin     = format.parse(targetFormat.format(new Date()));
                        long days   = (dateFin.getTime() - dateIni.getTime());
                        if(days > MILLISECS_PER_DAY)
                        {
                            PZExceptionHandler.raiseConstraintViolationException("DoReverseTransaction.java", "doPost()", null, "Merchant", ErrorMessages.REFUND_ALLOWEDDAYS_VALIDATION, PZConstraintExceptionEnum.INVALID_REFUND_REQUEST, null, null, null);

                        }
                    }
                }
            }
            if(functions.isValueNull(parentTrackingid) && !"N".equalsIgnoreCase(marketPlaceFlag))
            {
                Hashtable parentTransDetails    = TransactionEntry.getTransDetails(parentTrackingid);
                parentReversedAmount            = (String) parentTransDetails.get("refundamount");
                String toid                     = (String) parentTransDetails.get("toid");
                merchantDetailsVO1              = merchantDAO.getMemberDetails(toid);
                parentTransStatus               = (String) parentTransDetails.get("status");
                if(!refundChecker.isRefundAllowed(toid))
                {
                    PZExceptionHandler.raiseConstraintViolationException("DoReverseTransaction.java","doPost()",null,"Merchant", ErrorMessages.PARENT_REFUND_ALLOWED,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
                }
                refundAllowedDays = Integer.parseInt(merchantDetailsVO1.getRefundAllowedDays());
                if (d > refundAllowedDays)
                {
                    PZExceptionHandler.raiseConstraintViolationException("DoReverseTransaction.java", "doPost()", null, "Merchant", ErrorMessages.REFUND_ALLOWEDDAYS_VALIDATION, PZConstraintExceptionEnum.INVALID_REFUND_REQUEST, null, null, null);
                }else if(d == refundAllowedDays){
                    long MILLISECS_PER_DAY  = 24 * 60 * 60 * 1000;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "dd/MM/yyyy HH:mm:ss");
                    Date dateIni    = null;
                    Date dateFin    = null;
                    dateIni         = format.parse(transactionDate);
                    dateFin         = format.parse(targetFormat.format(new Date()));
                    long days       = (dateFin.getTime() - dateIni.getTime());
                    if(days > MILLISECS_PER_DAY)
                    {
                        PZExceptionHandler.raiseConstraintViolationException("DoReverseTransaction.java", "doPost()", null, "Merchant", ErrorMessages.REFUND_ALLOWEDDAYS_VALIDATION, PZConstraintExceptionEnum.INVALID_REFUND_REQUEST, null, null, null);

                    }
                }
            }
            refundAllowedDays = Integer.parseInt(merchantDetailsVO.getRefundAllowedDays());
            if (d > refundAllowedDays)
            {
                PZExceptionHandler.raiseConstraintViolationException("DoReverseTransaction.java", "doPost()", null, "Merchant", ErrorMessages.REFUND_ALLOWEDDAYS_VALIDATION, PZConstraintExceptionEnum.INVALID_REFUND_REQUEST, null, null, null);
            }else if(d == refundAllowedDays){
                long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000;
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // "dd/MM/yyyy HH:mm:ss");
                Date dateIni    = null;
                Date dateFin    = null;
                dateIni         = format.parse(transactionDate);
                dateFin         = format.parse(targetFormat.format(new Date()));
                long days       = (dateFin.getTime() - dateIni.getTime());
                if(days > MILLISECS_PER_DAY)
                {
                    PZExceptionHandler.raiseConstraintViolationException("DoReverseTransaction.java", "doPost()", null, "Merchant", ErrorMessages.REFUND_ALLOWEDDAYS_VALIDATION, PZConstraintExceptionEnum.INVALID_REFUND_REQUEST, null, null, null);

                }
            }
            if("Y".equals(isFraud)){
                isFraudNew=true;
            }

            if (!trackingId.trim().equals("") && !transId.trim().equals("") && !reason.trim().equals(""))
            {
                if (functions.isValueNull(gatewayType) && CUPPaymentGateway.GATEWAY_TYPE.equals(gatewayType))
                {
                    if (!trackingId.trim().equals("") && !transId.trim().equals("") && !reason.trim().equals(""))
                    {
                        AbstractPaymentProcess payment  = new CupPaymentProcess();
                        PZRefundRequest refundRequest   = new PZRefundRequest();
                        refundRequest.setMemberId(Integer.parseInt(memberId));
                        refundRequest.setAccountId(Integer.parseInt(accountId));
                        refundRequest.setTrackingId(Integer.parseInt(trackingId));
                        refundRequest.setRefundAmount(refundAmount);
                        refundRequest.setRefundReason(reason);
                        refundRequest.setFraud(isFraudNew);
                        refundRequest.setNotificationURL(notificationURL);
                        PZRefundResponse response   = payment.refund(refundRequest);
                        PZResponseStatus status     = response.getStatus();
                        if (PZResponseStatus.ERROR.equals(status))
                        {
                            throw new SystemError();
                        }
                        else if (PZResponseStatus.FAILED.equals(status))
                        {
                            responseMessage="Refund Operation is failed for Tracking ID: "+trackingId;
                        }
                        else if (response != null && (response.getStatus()).equals(PZResponseStatus.SUCCESS))
                        {   mailStatus      = "successful";
                            responseMessage = DoPartnerMerchantReverseTransaction_tracking_errormsg+trackingId;
                            statusSyncDAO.updateAllRefundTransactionFlowFlag(trackingId,"reversed");
                        }
                        else
                        {
                            responseMessage="Refund has been decline for Tracking ID: "+trackingId;
                        }
                    }
                }
                else
                {
                    AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accountId));

                    PZRefundRequest refundRequest = new PZRefundRequest();
                    refundRequest.setAccountId(Integer.valueOf(accountId));
                    refundRequest.setMemberId(Integer.parseInt(memberId));
                    if(functions.isValueNull(parentTrackingid) && !"N".equalsIgnoreCase(marketPlaceFlag))
                    {
                        refundRequest.setTrackingId(Integer.parseInt(parentTrackingid));
                        refundRequest.setRequestedTrackingid(Integer.parseInt(trackingId));
                        refundRequest.setReversedAmount(parentReversedAmount);
                        refundRequest.setRequestedReversedAmount(reversedAmount);
                        refundRequest.setCaptureAmount(parentCapturedAmount);
                        refundRequest.setRequestedCaptureAmount(captureAmount);
                    }
                    else
                    {
                        refundRequest.setTrackingId(Integer.parseInt(trackingId));
                        refundRequest.setCaptureAmount(captureAmount);
                        refundRequest.setReversedAmount(reversedAmount);
                        if((childDetailList != null || childDetailList.size()>0) && !"N".equalsIgnoreCase(marketPlaceFlag))
                        {
                            refundRequest.setChildDetailsList(childDetailList);
                        }
                    }
                    refundRequest.setMarketPlaceFlag(marketPlaceFlag);
                    refundRequest.setRefundAmount(refundAmount);
                    refundRequest.setRefundReason(reason);
                    refundRequest.setIpAddress(ipAddress);
                    refundRequest.setCurrency(currency);
                    auditTrailVO.setActionExecutorId(memberId);
                    refundRequest.setTransactionStatus(transStatus);
                    refundRequest.setNotificationURL(notificationURL);
                    //auditTrailVO.setActionExecutorName(user.getRoles().toString());
                    String role = "";
                    for (String s:user.getRoles())
                    {
                        role=role.concat(s);
                    }
                    auditTrailVO.setActionExecutorName(role+"-"+session.getAttribute("username").toString());
                    PZRefundResponse refundResponse     = null;
                    PZResponseStatus responseStatus     = null;
                    refundChecker                       = new RefundChecker();
                    refundRequest.setAuditTrailVO(auditTrailVO);
                    refundRequest.setFraud(isFraudNew);

                    if(refundChecker.isRefundAllowed(memberId))
                    {
                        if("N".equalsIgnoreCase(merchantDetailsVO.getPartialRefund())&& Double.parseDouble(refundRequest.getCaptureAmount())!=Double.parseDouble(refundRequest.getRefundAmount())){
                            responseMessage="Partial Refund is not allowed.";
                        }else if("N".equalsIgnoreCase(merchantDetailsVO.getMultipleRefund()) && transStatus.equalsIgnoreCase(PZTransactionStatus.REVERSED.toString())){
                            responseMessage="Multiple Refund is not allowed.";
                        }else {
                            boolean isAllowed=true;
                            if(functions.isValueNull(parentTrackingid) && !"N".equalsIgnoreCase(marketPlaceFlag))
                            {
                                if("N".equalsIgnoreCase(merchantDetailsVO1.getMultipleRefund()) && parentTransStatus.equalsIgnoreCase(PZTransactionStatus.REVERSED.toString())){
                                    responseMessage="Multiple Refund is not allowed for Parent Merchant.";
                                    isAllowed=false;
                                }
                            }
                            if(isAllowed)
                            {
                                refundResponse = paymentProcess.refund(refundRequest);
                                responseStatus = refundResponse.getStatus();
                                if (PZResponseStatus.ERROR.equals(responseStatus))
                                {
                                    responseMessage = "Refund Operation encountered internal error while reversing for Tracking ID: " + trackingId;
                                    responseMessage = refundResponse.getResponseDesceiption();
                                }
                                else if (PZResponseStatus.FAILED.equals(responseStatus))
                                {
                                    responseMessage = "Refund Operation is failed for Tracking ID: " + trackingId;
                                }
                                else if (PZResponseStatus.SUCCESS.equals(responseStatus))
                                {
                                    mailStatus = "successful";
                                    responseInfo = DoPartnerMerchantReverseTransaction_tracking_errormsg + trackingId;
                                }
                                else if (PZResponseStatus.PENDING.equals(responseStatus))
                                {
                                    responseMessage = refundResponse.getResponseDesceiption();
                                    mailStatus = responseMessage;
                                }
                            }
                        }

                    }
                    else
                    {
                        responseMessage="Refund is not allowed.";
                    }
                }
                if ("Y".equalsIgnoreCase(merchantDetailsVO.getIsRefundEmailSent()))
                {
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.REFUND_TRANSACTION,trackingId,mailStatus,responseMessage,null);

                }
                if (functions.isValueNull(parentTrackingid) && !"N".equalsIgnoreCase(marketPlaceFlag))
                {
                    if ("Y".equalsIgnoreCase(merchantDetailsVO1.getIsRefundEmailSent()))
                    {
                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.REFUND_TRANSACTION, parentTrackingid, mailStatus, responseMessage, null);
                    }
                }
                if(!"N".equalsIgnoreCase(marketPlaceFlag) && (childDetailList != null || childDetailList.size()>0))
                {
                    for (int i = 0; i < childDetailList.size(); i++)
                    {
                        marketPlaceVO       = (MarketPlaceVO) childDetailList.get(i);
                        merchantDetailsVO1  = marketPlaceVO.getMerchantDetailsVO();
                        logger.error("marketPlaceVO.getTrackingid()---------->"+marketPlaceVO.getTrackingid());
                        if(functions.isValueNull(marketPlaceVO.getRefundReason()))
                            childreason = marketPlaceVO.getRefundReason();
                        else
                            childreason = responseMessage;
                        if ("Y".equalsIgnoreCase(merchantDetailsVO1.getIsRefundEmailSent()))
                        {
                            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            asynchronousMailService.sendEmail(MailEventEnum.REFUND_TRANSACTION, marketPlaceVO.getTrackingid(), mailStatus, childreason, null);
                        }
                    }
                }
                AsynchronousSmsService smsService   = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.REFUND_TRANSACTION,trackingId,mailStatus,reason,null);
            }
            else
            {
                throw new Exception("Tracking ID should not be empty");
            }
        }
        catch (PZConstraintViolationException cve)
        {
            logger.error("PZConstraintViolationException in DoReverseTransaction---",cve);
            PZExceptionHandler.handleCVEException(cve, memberId, PZOperations.MERCHANT_REFUND);
            responseMessage = cve.getPzConstraint().getMessage();
            req.setAttribute("message",responseMessage);
            RequestDispatcher rd = req.getRequestDispatcher("/net/GetRefundDetails?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        catch (Exception e)
        {
            responseMessage="Refund Operation can't be executed for Tracking ID: "+trackingId;
            logger.error("Exception", e);
            PZExceptionHandler.raiseAndHandleGenericViolationException("DoPartnerMerchantReverseTransaction.java","doPost()",null,"Merchant",ErrorMessages.REFUND_EXCEPTION+trackingId,null,e.getMessage(),e.getCause(),memberId,PZOperations.MERCHANT_REFUND);
        }
        req.setAttribute("message",responseMessage);
        req.setAttribute("success",responseInfo);
        RequestDispatcher rd = req.getRequestDispatcher("/net/PartnerMerchantRefundList?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}