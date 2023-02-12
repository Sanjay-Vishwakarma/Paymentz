package net.partner;

import com.directi.pg.AsyncNotificationService;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.PzEncryptor;
import com.directi.pg.core.GatewayAccountService;
import com.manager.ReconManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.ManualReconVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.PZTransactionStatus;
import com.payment.exceptionHandler.PZDBViolationException;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: Jitendra
 * Date: 23/03/18
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerManualReconProcess extends HttpServlet
{
    private static Logger log = new Logger(PartnerManualReconProcess.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }

    public void doProcess(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner = new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        StringBuilder ErrorMessage = new StringBuilder();
        TransactionManager transactionManager = new TransactionManager();

        String mailStatus="";
        String transactionmail = "false";
        String RefundMail ="false";
        String Chargebackmail="false";
        String status = null;
        String paymentOrderNumber = null;
        String trackingId = null;
        String remark = "";
        String amount = null;
        String desc = null;
        String refundAmount = null;
        String captureAmount = null;
        String chargebackamount = null;
        String reqChargebackAmount = null;
        String dbPaymentNumber = null;
        String runTime = null;
        String accountId = null;
        String notificationUrl = "";
        String fromtype = "";

        String[] iciciTransIdStr = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
        ReconManager reconManager = new ReconManager();
        Functions functions = new Functions();

        Calendar calendar = Calendar.getInstance();
        runTime = dateFormat.format(calendar.getTime());

        ManualReconVO manualReconVO = null;

        if (req.getParameterValues("trackingid") != null)
        {
            iciciTransIdStr = req.getParameterValues("trackingid");
            if (Functions.checkArrayNull(iciciTransIdStr) == null)
            {
                sErrorMessage.append("Select at least one transaction.<BR>");
                req.setAttribute("cbmessage", sErrorMessage.toString());
                RequestDispatcher rd = req.getRequestDispatcher("/manualRecon.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
        }
        else
        {
            sErrorMessage.append("Select at least one transaction.<BR>");
            req.setAttribute("cbmessage", sErrorMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/manualRecon.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        for (String iciciTransId : iciciTransIdStr)
        {
            try
            {
                trackingId = iciciTransId;
                amount = ESAPI.validator().getValidInput("amount", req.getParameter("amount_" + iciciTransId), "SafeString", 20, true);
                paymentOrderNumber = ESAPI.validator().getValidInput("paymentid", req.getParameter("paymentorderno_" + iciciTransId), "companyName", 200, true);
                remark = ESAPI.validator().getValidInput("remark", req.getParameter("remark_" + iciciTransId), "companyName", 100, true);
                status = ESAPI.validator().getValidInput("status", req.getParameter("statustreatment_" + iciciTransId), "SafeString", 20, false);
                desc = ESAPI.validator().getValidInput("description", req.getParameter("description_" + iciciTransId), "SafeString", 80, false);
                dbPaymentNumber = ESAPI.validator().getValidInput("dbpaymentnumber", req.getParameter("dbpaymentnumber_" + iciciTransId), "SafeString", 100, true);
                refundAmount = ESAPI.validator().getValidInput("refundamount", req.getParameter("refundamount_" + iciciTransId), "Amount", 20, true);
                accountId = ESAPI.validator().getValidInput("accountid", req.getParameter("accountid_" + iciciTransId), "SafeString", 30, false);
                captureAmount = ESAPI.validator().getValidInput("amount", req.getParameter("captureamount_" + iciciTransId), "Amount", 20, true);
                notificationUrl = req.getParameter("notificationUrl_" + iciciTransId);
                fromtype = req.getParameter("fromtype" + iciciTransId);
                chargebackamount = ESAPI.validator().getValidInput("chargebackamount", req.getParameter("chargebackamount_" + iciciTransId), "Amount", 20, true);
                if (functions.isValueNull(req.getParameter("chargebackAmount_" + iciciTransId)))
                    reqChargebackAmount = ESAPI.validator().getValidInput("chargebackAmount", req.getParameter("chargebackAmount_" + iciciTransId), "Amount", 20, true);
            }
            catch (ValidationException e)
            {
                ErrorMessage.append("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;\"Invalid Input Parameter for Trackingid: " + trackingId + "<BR>"+ "</h5>");
                continue;
            }
            double chargebackAmount = 0.00;
            if (functions.isValueNull(chargebackamount) && functions.isValueNull(reqChargebackAmount))
            {
                chargebackAmount = Double.parseDouble(chargebackamount) - Double.parseDouble(reqChargebackAmount);
                log.error("chargebackAmount----" + chargebackAmount);
            }

            manualReconVO = new ManualReconVO();
            manualReconVO.setTrackingId(trackingId);
            manualReconVO.setAmount(amount);
            manualReconVO.setPaymentOrderNumber(paymentOrderNumber);
            manualReconVO.setRemark(remark);
            manualReconVO.setStatus(status);
            manualReconVO.setDesc(desc);
            manualReconVO.setDbPaymentNumber(dbPaymentNumber);
            manualReconVO.setRefundAmount(refundAmount);
            manualReconVO.setAccountId(accountId);
            manualReconVO.setActionExecutorId((String) session.getAttribute("merchantid"));
            //manualReconVO.setActionExecutorName("Partner");
            String role = "";
            for (String s : user.getRoles())
            {
                role = role.concat(s);
            }
            manualReconVO.setActionExecutorName(role + "-" + session.getAttribute("username").toString());
            manualReconVO.setTime(runTime);
            manualReconVO.setCaptureAmount(captureAmount);
            try
            {
                boolean isTreatmentGiven = false;
                if ("failed".equalsIgnoreCase(status))
                {
                    if (!functions.isValueNull(remark))
                    {
                        sErrorMessage.append("<tr><td>"+trackingId+"</td><td>"+status+"</td><td>Please Enter Remark</td></tr>");
                    }
                    else
                    {
                        isTreatmentGiven = reconManager.giveFailedTreatment(manualReconVO);
                    }
                    /*if (*//*!functions.isValueNull(remark) ||*//* !functions.isValueNull(paymentOrderNumber))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Please Enter PaymentOrderNumber</td></tr>");
                        //sErrorMessage.append("<tr><td>"+trackingId+"</td><td>"+status+"</td><td>Please Enter Remark or PaymentOrderNumber</td></tr>");
                    }
                    else
                    {
                        isTreatmentGiven = reconManager.giveFailedTreatment(manualReconVO);
                    }*/
                    /*if(functions.isValueNull(remark))
                    {
                        isTreatmentGiven = reconManager.giveFailedTreatment(manualReconVO);
                    }
                    else if(!functions.isValueNull(remark))
                    {
                        sErrorMessage.append("<tr><td>"+trackingId+"</td><td>"+status+"</td><td>Please Enter Remark</td></tr>");
                    }
                    else
                    {
                        sErrorMessage.append("<tr><td>"+trackingId+"</td><td>"+status+"</td><td>Failed Treatment</td></tr>");
                    }*/
                }
                else if ("authstarted".equalsIgnoreCase(status))
                {
                    if (functions.isValueNull(paymentOrderNumber))
                    {
                        isTreatmentGiven = reconManager.giveAuthStartedTreatment(manualReconVO);
                    }
                    else if (!functions.isValueNull(paymentOrderNumber))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Please Enter PaymentOrderNumber</td></tr>");
                    }
                    else
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Failed Treatment</td></tr>");
                    }
                }
                else if ("authstarted_3D".equalsIgnoreCase(status))
                {
                    if (functions.isValueNull(paymentOrderNumber))
                    {
                        isTreatmentGiven = reconManager.giveAuthStarted3dTreatment(manualReconVO);
                    }
                    else if (!functions.isValueNull(paymentOrderNumber))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Please Enter PaymentOrderNumber</td></tr>");
                    }
                    else
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Failed Treatment</td></tr>");
                    }
                }
                else if ("authfailed".equalsIgnoreCase(status))
                {
                    if (!functions.isValueNull(paymentOrderNumber) /*|| !functions.isValueNull(remark)*/)
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Please Enter paymentOrderNumber</td></tr>");
                        //sErrorMessage.append("<tr><td>"+trackingId+"</td><td>"+status+"</td><td>Please Enter paymentOrderNumber and Remark</td></tr>");
                    }
                    else if (!functions.isValueNull(remark))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Please Enter remark</td></tr>");
                    }
                    else
                    {
                        isTreatmentGiven = reconManager.giveAuthFailedTreatment(manualReconVO);
                        if(isTreatmentGiven == true){
                            transactionmail ="true";
                            if(remark!=null && !remark.equals(""))
                            {
                                mailStatus = "Transaction Declined ( "+remark+" )";
                            }else{
                                mailStatus = "Transaction Declined";
                            }
                        }
                    }

                    /*if (functions.isValueNull(paymentOrderNumber) && functions.isValueNull(remark))
                    {
                        isTreatmentGiven = reconManager.giveAuthFailedTreatment(manualReconVO);
                    }
                    else if (!functions.isValueNull(paymentOrderNumber) && !functions.isValueNull(remark))
                    {
                        sErrorMessage.append("<tr><td>"+trackingId+"</td><td>"+status+"</td><td>Please Enter paymentOrderNumber and Remark</td></tr>");
                    }
                    else
                    {
                        sErrorMessage.append("<tr><td>"+trackingId+"</td><td>"+status+"</td><td>Failed Treatment</td></tr>");
                    }*/
                }
                else if ("authsuccessful".equalsIgnoreCase(status))
                {
                    /*if (functions.isValueNull(paymentOrderNumber) && functions.isValueNull(remark))
                    {
                        isTreatmentGiven = reconManager.giveAuthSuccessTreatment(manualReconVO);
                    }
                    else */
                    if (!functions.isValueNull(paymentOrderNumber) /*|| !functions.isValueNull(remark)*/)
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Please Enter paymentOrderNumber </td></tr>");
                        //sErrorMessage.append("<tr><td>"+trackingId+"</td><td>"+status+"</td><td>Please Enter paymentOrderNumber and Remark</td></tr>");
                    }
                    else if (!functions.isValueNull(remark))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Please Enter remark </td></tr>");
                    }
                    else
                    {
                        if (functions.isValueNull(paymentOrderNumber))
                        {
                            isTreatmentGiven = reconManager.giveAuthSuccessTreatment(manualReconVO);
                        }
                        if(isTreatmentGiven == true)
                        {
                            transactionmail = "true";
                            transactionmail="true";
                            if(remark!=null && !remark.equals(""))
                            {
                                mailStatus="Successful ("+remark+")";
                            }
                            else
                            {
                                mailStatus="Successful";
                            }
                        }
                        //sErrorMessage.append("<tr><td>"+trackingId+"</td><td>"+status+"</td><td>Failed Treatment</td></tr>");
                    }
                }
                else if ("capturesuccess".equalsIgnoreCase(status) && functions.isValueNull(fromtype) && "jpbt".equalsIgnoreCase(fromtype))
                {
                    if (!functions.isValueNull(paymentOrderNumber))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Please Enter paymentOrderNumber</td></tr>");
                    }
                    else if (functions.isValueNull(captureAmount) && Double.parseDouble(captureAmount) == 0.00)
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Capture Amount Can Not Be Zero</td></tr>");
                    }
                    else if (!functions.isValueNull(captureAmount))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Failed Treatment</td></tr>");
                    }
                    // if (functions.isValueNull(paymentOrderNumber) && /*functions.isValueNull(remark)&&*/ Double.parseDouble(captureAmount) != 0.00)
                    else
                    {
                        isTreatmentGiven = reconManager.giveCaptureSuccessTreatment(manualReconVO);
                        if(isTreatmentGiven == true)
                        {
                            transactionmail = "true";
                            if(remark!=null && !remark.equals(""))
                            {
                                mailStatus="Successful ("+remark+")";
                            }
                            else
                            {
                                mailStatus="Successful";
                            }
                        }
                    }
                }
                else if ("capturesuccess".equalsIgnoreCase(status) && functions.isValueNull(accountId))
                {
                    if (!functions.isValueNull(paymentOrderNumber) /*|| !functions.isValueNull(remark)*/)
                    {
                        //sErrorMessage.append("<tr><td>"+trackingId+"</td><td>"+status+"</td><td>Please Enter paymentOrderNumber and Remark</td></tr>");
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Please Enter paymentOrderNumber </td></tr>");
                    }
                    else if (!functions.isValueNull(remark))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Please Enter remark </td></tr>");
                    }
                    else if (functions.isValueNull(captureAmount) && Double.parseDouble(captureAmount) == 0.00)
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Capture Amount Can Not Be Zero</td></tr>");
                    }
                    else if (functions.isValueNull(captureAmount) && functions.isValueNull(amount) && Double.parseDouble(captureAmount) > Double.parseDouble(amount))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Cannot Capture Transaction as Capture Amount is greater than Available Amount</td></tr>");
                    }
                    else if (!functions.isValueNull(captureAmount))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>Fail</td><td>Failed Treatement </td></tr>");
                    }
                    else
                    {
                        isTreatmentGiven = reconManager.giveCaptureSuccessTreatment(manualReconVO);
                        if(isTreatmentGiven == true)
                        {
                            transactionmail = "true";
                            if(remark!=null && !remark.equals(""))
                            {
                                mailStatus="Successful ("+remark+")";
                            }
                            else
                            {
                                mailStatus="Successful";
                            }
                        }
                        //  sErrorMessage.append("<tr><td>"+trackingId+"</td><td>"+status+"</td><td>Failed Treatment</td></tr>");
                    }
                }
                else if ("capturefailed".equalsIgnoreCase(status))
                {
                   isTreatmentGiven = reconManager.giveCaptureFailedTreatment(manualReconVO);
                    /*isTreatmentGiven = false;
                    if (isTreatmentGiven)
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Failed Treatment</td></tr>");
                    }*/
                }
                else if ("reversed".equalsIgnoreCase(status))
                {
                    if (!functions.isValueNull(paymentOrderNumber))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Please enter paymentOrderNumber</td></tr>");
                    }
                    /*if (paymentOrderNumber == null || ("").equals(paymentOrderNumber) &&  remark==null || ("").equals(remark))
                    {
                        isTreatmentGiven = reconManager.giveReversedTreatment(manualReconVO);
                        if (isTreatmentGiven == true)
                        {
                            RefundMail = "true";
                            mailStatus = "successful";
                        }
                    } */
                    else if (!functions.isValueNull(remark))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Please enter remark</td></tr>");
                    }
                    else if (functions.isValueNull(refundAmount) && Double.parseDouble(refundAmount) == 0.00)
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Refund Amount Can Not Be Zero</td></tr>");
                    }
                    else if (functions.isValueNull(refundAmount) && functions.isValueNull(captureAmount) &&  Double.parseDouble(captureAmount) < Double.parseDouble(refundAmount))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Cannot Revesal Request Sent Transaction as Refund amount is greater than Available Amount</td></tr>");
                    }
                    else if(!functions.isValueNull(refundAmount))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td> <td>Fail</td> <td>Failed Treatment</td></tr>");
                    }
                    else
                    {
                        if (functions.isValueNull(refundAmount) && functions.isValueNull(captureAmount) &&  Double.parseDouble(captureAmount) >= Double.parseDouble(refundAmount)){
                            isTreatmentGiven = reconManager.giveReversedTreatment(manualReconVO);
                        }
                        if(isTreatmentGiven == true)
                        {
                            RefundMail="true";
                            mailStatus="successful";
                        }

                    }
                }
                else if ("chargebackreversed".equalsIgnoreCase(status))
                {
                    if (functions.isValueNull(reqChargebackAmount) &&  Double.parseDouble(reqChargebackAmount) == 0.00)
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Chargeback Amount Can Not Be Zero</td></tr>");
                    }
                    else if (!functions.isValueNull(paymentOrderNumber) /*&& remark==null || ("").equals(remark)*/)
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Please Enter paymentOrderNumber </td></tr>");
                    }
                    else if(!functions.isValueNull(remark))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Please Enter Remark </td></tr>");
                    }
                    else if (functions.isValueNull(chargebackamount) && functions.isValueNull(reqChargebackAmount) && Double.parseDouble(chargebackamount) < Double.parseDouble(reqChargebackAmount))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Cannot reverse Chargeback Transaction as Requested CBK reverse Amount is greater than Total Chargeback Amount for Tracking Id </td></tr>");
                    }
                    else if (!functions.isValueNull(reqChargebackAmount))
                    {
                        sErrorMessage.append("<tr><td>" + trackingId + "</td> <td>Fail</td> <td>Failed Treatment</td></tr>");
                    }
                    else
                    {
                        manualReconVO.setChargebackAmount(chargebackAmount);
                        manualReconVO.setAmount(reqChargebackAmount);
                        isTreatmentGiven = reconManager.giveChargebackReversedTreatment(manualReconVO);
                        if(isTreatmentGiven == true)
                        {
                            Chargebackmail="true";
                            mailStatus="ChargebackReversed";
                        }
                    }
                }

                if (isTreatmentGiven)
                {
                    //sSuccessMessage.append("Success  Treatement(Status: "+manualReconVO.getStatus()+") for trackingid : "+trackingId+"<BR>");
                    sSuccessMessage.append("<tr><td>" + trackingId + "</td><td>" + status + "</td><td>Success Treatment</td></tr>");
                }
                if (!functions.isValueNull(status))
                {
                    sErrorMessage.append("<tr><td>" + trackingId + "</td><td></td><td>Please Provide status treatment</td></tr>");
                }
            }
            catch (PZDBViolationException pz)
            {
                sErrorMessage.append(trackingId + ",");
            }

            log.error("Notificatin Sending to---" + notificationUrl);
            try
            {
                MerchantDAO merchantDAO = new MerchantDAO();
                MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(iciciTransId);
                merchantDetailsVO = merchantDAO.getMemberDetails(transactionDetailsVO.getToid());

                if (functions.isValueNull(notificationUrl))
                {
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    log.error("status----->" + status);
                    if ("capturesuccess".equalsIgnoreCase(status))
                    {
                        log.error("transactionDetailsVO.getCaptureAmount()--->" + transactionDetailsVO.getCaptureAmount());
                        transactionDetailsVO.setTemplateamount(transactionDetailsVO.getCaptureAmount());
                    }
                    log.error("tmpl_amount----->" + transactionDetailsVO.getTemplateamount());
                    log.error("ReconciliationNotification flag for ---" + transactionDetailsVO.getToid() + "---" + merchantDetailsVO.getReconciliationNotification());
                    if ("Y".equals(merchantDetailsVO.getReconciliationNotification()))
                    {
                        log.error("inside sending notification---" + notificationUrl);
                        transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                        if (functions.isValueNull(transactionDetailsVO.getCcnum()) && functions.isValueNull(transactionDetailsVO.getExpdate()))
                        {
                            transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                            transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));
                        }
                        if (functions.isValueNull(accountId))
                        {
                            transactionDetailsVO.setBillingDesc(GatewayAccountService.getGatewayAccount(accountId).getDisplayName().toString());
                        }
                        asyncNotificationService.sendNotification(transactionDetailsVO, iciciTransId, status, remark);
                    }
                }

                String mailForEvent="";
                if("Y".equals(merchantDetailsVO.getSuccessReconMail()) && transactionmail.equals("true"))
                {
                    mailForEvent="transactionMail";
                    sendTransactionEventMail.sendReconTransactionEventMail(MailEventEnum.RECON_TRANSACTION, iciciTransId, mailStatus, remark, GatewayAccountService.getGatewayAccount(accountId).getDisplayName().toString(),mailForEvent);
                }
                if("Y".equals(merchantDetailsVO.getRefundReconMail()) && RefundMail.equals("true"))
                {
                    mailForEvent="refundMail";
                    mailStatus="Refund Successful";
                    sendTransactionEventMail.sendReconTransactionEventMail(MailEventEnum.RECON_TRANSACTION, iciciTransId, mailStatus, remark, null,mailForEvent);
                }
                    /*if("Y".equals(merchantDetailsVO.getPayoutReconMail()) && PayoutMail.equals("true")){
                        mailForEvent="payoutMail";
                        sendTransactionEventMail.sendReconTransactionEventMail(MailEventEnum.RECON_TRANSACTION, iciciTransId, mailStatus, remark, null,mailForEvent);
                    }*/
                if("Y".equals(merchantDetailsVO.getChargebackReconMail()) && Chargebackmail.equals("true")){
                    mailForEvent="chargebackMail";
                    sendTransactionEventMail.sendReconTransactionEventMail(MailEventEnum.RECON_TRANSACTION, iciciTransId, mailStatus, remark, null,mailForEvent);
                }
            }
            catch (PZDBViolationException e)
            {
                log.error("PZDBViolationException--->", e);
            }
        }

        StringBuilder chargeBackMessage = new StringBuilder();
        if (functions.isValueNull(String.valueOf(ErrorMessage)))
        {
            chargeBackMessage.append(ErrorMessage.toString());
        }
        else
        {
            chargeBackMessage.append("<div><table align=center width=\"40%\" class=\"display table table table-striped table-bordered table-hover dataTable\" style=\"font-family:Open Sans;font-size: 13px;font-weight: 600;width: 70%;\"><tr><td valign=\"middle\" style=\"background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;><font class=\"texthead\" color=\"#FFFFFF\" size=\"4\" >Tracking Id</font></td><td td valign=\"middle\" style=\"background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;><font class=\"texthead\" color=\"#FFFFFF\" size=\"4\">Status</font></td><td td valign=\"middle\" style=\"background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;><font class=\"texthead\" color=\"#FFFFFF\" size=\"4\">Remark</font></td></tr>");
            chargeBackMessage.append(sSuccessMessage.toString());
            chargeBackMessage.append(sErrorMessage.toString());
            chargeBackMessage.append("</table></div>");
        }
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/manualRecon.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
    }
}