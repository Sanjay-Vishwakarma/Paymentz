package payment;

import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.manager.MerchantConfigManager;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Ecospend.EcospendGatewayAccountVO;
import com.payment.Ecospend.EcospendRequestVo;
import com.payment.Ecospend.EcospendResponseVO;
import com.payment.Ecospend.EcospendUtils;
import com.payment.Enum.CardTypeEnum;
import com.payment.Mail.MailEventEnum;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.sms.AsynchronousSmsService;
import com.payment.validators.vo.CommonValidatorVO;
import payment.util.ReadRequest;
import payment.util.SingleCallPaymentDAO;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * Created by Admin on 9/9/2021.
 */
public class EcospendProcess extends HttpServlet
{
    private static Logger log = new Logger(EcospendProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(EcospendProcess.class.getName());
    Functions functions = new Functions();

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        transactionLogger.error("------ Inside EcospendProcess ------");
        HttpSession session     = req.getSession(true);
        String ctoken           = req.getParameter("ctoken");
        session.setAttribute("ctoken", ctoken);
        CommonValidatorVO commonValidatorVO = null;
        commonValidatorVO                   = ReadRequest.getSpecificRequestParametersForSale1(req);
        transactionLogger.debug("PaymentMode  "+commonValidatorVO.getPaymentMode());
        for (Object key : req.getParameterMap().keySet())
        {
            transactionLogger.error("----for loop Ecospend-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }

        Enumeration en = req.getParameterNames();
        while (en.hasMoreElements())
        {
            String key = (String) en.nextElement();
            String value = req.getParameter(key);
            transactionLogger.error("key----" + key + "-----" + value);
        }

        Functions functions = new Functions();
        SingleCallPaymentDAO singleCallPaymentDAO=new SingleCallPaymentDAO();
        PrintWriter pWriter = res.getWriter();
        String esbank="";
        String purpose="";
        String trackingid="";
        String paymentReference="";
        String AccessToken="";
        String phoneNumber="";
        String customerIp="";
        String Accountid="";
        String phoneNumberCC="";
        String respStatus="";
        String respRemark="";
        String emailsent="";
        String paymentType="";
        String cardtype="";
        String autoRedirect="";
        String tmpl_amount="";
        String tmpl_currency="";
        String isService="";
        String crtype="";
        String cridentification="";
        String crowner_name="";
        String crcurrency="";
        String crbic="";
        String drtype="";
        String dridentification="";
        String drowner_name="";
        String drcurrency="";
        String drbic="";
        String psu_id="";
        String payment_rails="";
        String orderdesc="";
        String currency="";
        String period="";
        String noOfPayments="";
        String firstPaymentDate="";
        String firstPaymentAmount="";
        String lastPaymentAmount="";
        String fromType="";
        String email="";
        String bankname="";
        String isRefundAllow="";

        if(functions.isValueNull(req.getParameter("currency")))
        {
            currency = req.getParameter("currency");                 // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("psuid")))
        {
            psu_id = req.getParameter("psuid");                 // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("paymentRails")))
        {
            payment_rails = req.getParameter("paymentRails");                 // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("drbic")))
        {
            drbic = req.getParameter("drbic");                 // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("draccountType")))
        {
            drtype = req.getParameter("draccountType");                 // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("draccountIdentification")))
        {
            dridentification = req.getParameter("draccountIdentification");                 // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("drownerName")))
        {
            drowner_name = req.getParameter("drownerName");                 // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("drcurrency")))
        {
            drcurrency = req.getParameter("drcurrency");                 // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("crbic")))
        {
            crbic = req.getParameter("crbic");                 // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("crcurrency")))
        {
            crcurrency = req.getParameter("crcurrency");                 // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("beneficiary")))
        {
            crowner_name = req.getParameter("beneficiary");                 // Got From singlecall
        }
        transactionLogger.debug("Accountid From Hidden"+Accountid);
        if(functions.isValueNull(req.getParameter("esbankid")))
        {
            String temp     = req.getParameter("esbankid");
            esbank         = temp.split("\\|")[0];
            bankname       = temp.split("\\|")[1];
            isRefundAllow  = temp.split("\\|")[2];
        }
        transactionLogger.debug("bankname "+bankname);
        transactionLogger.debug("isRefundAllow "+isRefundAllow);

        if(functions.isValueNull(req.getParameter("purpose")))
        {
            purpose = req.getParameter("purpose");                 // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("Fromtype")))
        {
            fromType = req.getParameter("Fromtype");

        }
        if(functions.isValueNull(req.getParameter("drAccNumber")))
        {
            cridentification = req.getParameter("drAccNumber");                 // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("drSortCode")))
        {
            crtype = req.getParameter("drSortCode");                 // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("trackingid")) )
        {
            trackingid = req.getParameter("trackingid");    // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("reference")) )
        {
            paymentReference = req.getParameter("reference");    // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("period")) )
        {
            period = req.getParameter("period");    // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("noOfPayments")) )
        {
            noOfPayments = req.getParameter("noOfPayments");    // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("firstPaymentDate")) )
        {
            firstPaymentDate = req.getParameter("firstPaymentDate");    // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("firstPaymentAmount")) )
        {
            firstPaymentAmount = req.getParameter("firstPaymentAmount");
        }
        if(functions.isValueNull(req.getParameter("lastPaymentAmount")) )
        {
            lastPaymentAmount = req.getParameter("lastPaymentAmount");    // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("accountid")))
        {
            Accountid = req.getParameter("accountid");    // Got From singlecall
            transactionLogger.debug("Accountid From Hidden"+Accountid);
        }
        if(functions.isValueNull(req.getParameter("accesstoken")) )
        {
            AccessToken = req.getParameter("accesstoken");    // Got From singlecall
            transactionLogger.debug("accesstoken From Hidden"+AccessToken);
        }
        if(functions.isValueNull(req.getParameter("telno")) )
        {
            phoneNumber = req.getParameter("telno");    // Got From singlecall
            transactionLogger.debug("phoneNumber From Hidden"+phoneNumber);
        }
        if(functions.isValueNull(req.getParameter("emailaddr")) )
        {
            email = req.getParameter("emailaddr");    // Got From singlecall
            transactionLogger.debug("emailaddr From Hidden"+email);
        }
        if(functions.isValueNull(req.getParameter("phone-CC")) )
        {
            phoneNumberCC = req.getParameter("phone-CC");    // Got From singlecall
            transactionLogger.debug("phoneNumberCC From Hidden"+phoneNumberCC);
        }
        if(functions.isValueNull(req.getParameter("customerIp")) )
        {
            customerIp = req.getParameter("customerIp");    // Got From singlecall
            transactionLogger.debug("customerIp From Hidden"+customerIp);
        }
        if(functions.isValueNull(req.getParameter("emailsent")) )
        {
            emailsent = req.getParameter("emailsent");    // Got From singlecall
            transactionLogger.debug("emailsent From Hidden"+emailsent);
        }
        if(functions.isValueNull(req.getParameter("cardtype")) )
        {
            cardtype = req.getParameter("cardtype");    // Got From singlecall
            transactionLogger.debug("cardtype From Hidden"+cardtype);
        }
        if(functions.isValueNull(req.getParameter("paymentType")) )
        {
            paymentType = req.getParameter("paymentType");    // Got From singlecall
            transactionLogger.debug("paymentType From Hidden"+paymentType);
        }
        if(functions.isValueNull(req.getParameter("autoRedirect")) )
        {
            autoRedirect = req.getParameter("autoRedirect");    // Got From singlecall
            transactionLogger.debug("autoRedirect From Hidden "+autoRedirect);
        }
        if(functions.isValueNull(req.getParameter("tmpl_amount")) )
        {
            tmpl_amount = req.getParameter("tmpl_amount");    // Got From singlecall
            transactionLogger.debug("tmpl_amount From Hidden "+tmpl_amount);
        }
        if(functions.isValueNull(req.getParameter("tmpl_currency")) )
        {
            tmpl_currency = req.getParameter("tmpl_currency");    // Got From singlecall
            transactionLogger.debug("tmpl_currency From Hidden "+tmpl_currency);
        }
        if(functions.isValueNull(req.getParameter("isService")) )
        {
            isService = req.getParameter("isService");    // Got From singlecall
            transactionLogger.debug("isService From Hidden "+isService);
        }
        if(functions.isValueNull(req.getParameter("orderDesc")) )
        {
            orderdesc = req.getParameter("orderDesc");    // Got From singlecall
            transactionLogger.debug("orderDesc `From Hidden "+orderdesc);
        }
        String terminalId = "";

        if(functions.isValueNull(req.getParameter("terminalId")) )
        {
            terminalId = req.getParameter("terminalId");    // Got From singlecall
            transactionLogger.debug("orderDesc `From Hidden "+orderdesc);
        }



        String Debtoraccount    = req.getParameter("Debtoraccount");
        String Scheduleddate    = req.getParameter("scheduledPaymentDate");
        String paymentMethod    = req.getParameter("paymentMethod");
        String phoneNumberNew   = phoneNumber;
        TransactionDetailsVO transactionDetailsVO = null;
        TransactionManager transactionManager   = new TransactionManager();
        transactionDetailsVO                    = transactionManager.getTransDetailFromCommon(trackingid); // Getting Details from transaction_common
        CommCardDetailsVO commCardDetailsVO     = new CommCardDetailsVO();

        CommAddressDetailsVO commAddressDetailsVO   = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO     = new CommTransactionDetailsVO();
        EcospendGatewayAccountVO gatewayAccountVO   = new EcospendGatewayAccountVO();
        commAddressDetailsVO.setTmpl_amount(tmpl_amount);
        commAddressDetailsVO.setTmpl_currency(tmpl_currency);
        EcospendResponseVO ecospendResponseVO   = new EcospendResponseVO();
        EcospendRequestVo ecospendRequestVo     = new EcospendRequestVo();

        transDetailsVO.setOrderDesc(orderdesc);
        transDetailsVO.setCurrency(currency);
        transDetailsVO.setFromtype(fromType);

        ecospendRequestVo.setAccessTken(AccessToken);
        ecospendRequestVo.setReference(paymentReference);
        ecospendRequestVo.setBankid(esbank);
        ecospendRequestVo.setCreditortype(crtype);
        ecospendRequestVo.setCreditorID(cridentification);
        ecospendRequestVo.setCreditorCurrency(crcurrency);
        ecospendRequestVo.setCreditorName(crowner_name);
        ecospendRequestVo.setCreditorBic(crbic);
        ecospendRequestVo.setDebtortype(drtype);
        ecospendRequestVo.setDebtorID(dridentification);
        ecospendRequestVo.setDebtorCurrency(drcurrency);
        ecospendRequestVo.setDebtorName(drowner_name);
        ecospendRequestVo.setDebtorBic(drbic);
        ecospendRequestVo.setPsuid(psu_id);
        ecospendRequestVo.setPaymentrails(payment_rails);
        ecospendRequestVo.setDebtoraccount(Debtoraccount);
        //ecospendRequestVo.setPaylink(Paylink);
        ecospendRequestVo.setScheduledForDate(Scheduleddate);
       // ecospendRequestVo.setScheduledFor(ScheduledFor);
        ecospendRequestVo.setPeriod(period);
        ecospendRequestVo.setNumber_of_payments(noOfPayments);
        ecospendRequestVo.setFirst_payment_date(firstPaymentDate);
        ecospendRequestVo.setFirstPaymentAmount(firstPaymentAmount);
        ecospendRequestVo.setLastPaymentAmount(lastPaymentAmount);
        ecospendRequestVo.setPaymentMethod(paymentMethod);
        ecospendRequestVo.setEmail(email);
        ecospendRequestVo.setPhonecc(phoneNumberCC);

        EcospendUtils ecospendUtils=new EcospendUtils();

        ecospendRequestVo   =ecospendUtils.getEcospendrequestVo(transactionDetailsVO,commAddressDetailsVO,phoneNumberNew,Accountid,ecospendRequestVo,transDetailsVO,gatewayAccountVO);
        ecospendRequestVo.setIsRefundAllow(isRefundAllow);

        EcospendResponseVO transRespDetails = new EcospendResponseVO();
        String billingDiscriptor            = "";
        String mailtransactionStatus        = "Failed";
        PaymentManager paymentManager       = new PaymentManager();
        AuditTrailVO auditTrailVO           = new AuditTrailVO();
        auditTrailVO.setActionExecutorId(ecospendRequestVo.getCommMerchantVO().getMerchantId());
        auditTrailVO.setActionExecutorName("Customer");
        AbstractPaymentGateway pg = null;
        try
        {
            pg = AbstractPaymentGateway.getGateway(Accountid);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("Exception IN Ecospend --- ",systemError);
        }
        if(commonValidatorVO.getAddressDetailsVO().getCountry() != null){
            ecospendRequestVo.getAddressDetailsVO().setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        }
        commonValidatorVO.setPaymentType(paymentType);
        commonValidatorVO.setCardType(cardtype);
       // commonValidatorVO.setTerminalId(transactionDetailsVO.getTerminalId());
        commonValidatorVO.setTerminalId(terminalId);
        commonValidatorVO.setAddressDetailsVO(ecospendRequestVo.getAddressDetailsVO());
        commonValidatorVO.setTransDetailsVO(ecospendRequestVo.getTransDetailsVO());
       // commonValidatorVO.setCustomerId(bankname);
        commonValidatorVO.setProcessingbank(bankname);
        MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
        MerchantDetailsVO merchantDetailsVO         = new MerchantDetailsVO();
        try
        {
            if (functions.isValueNull(ecospendRequestVo.getCommMerchantVO().getMerchantId()))
            {
                merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(ecospendRequestVo.getCommMerchantVO().getMerchantId());
                merchantDetailsVO.setAccountId(Accountid);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                if(functions.isValueNull(merchantDetailsVO.getHostUrl()))
                {
                    ecospendRequestVo.setHostUrl(merchantDetailsVO.getHostUrl());
                }
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("Exception --- "+e.getMessage());
        }
        int detailId= 0; //smsstared entry

        try
        {
                paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
                if (isService.equalsIgnoreCase("N"))
                {
                    transRespDetails = (EcospendResponseVO) pg.processAuthentication(trackingid, ecospendRequestVo);
                    respStatus = "authsuccessful";
                }
                else
                {
                    if (functions.isValueNull(cardtype))
                    {
                        if (String.valueOf(CardTypeEnum.INSTANTPAYMENT.getValue()).equals(cardtype) && functions.isValueNull(Scheduleddate))
                        {
                            transRespDetails = (EcospendResponseVO) pg.processSale(trackingid, ecospendRequestVo);
                        }
                        else if (String.valueOf(CardTypeEnum.STANDINGORDERS.getValue()).equals(cardtype))
                        {
                            transRespDetails = (EcospendResponseVO) pg.processRebilling(trackingid, ecospendRequestVo);
                        }
                        else if (String.valueOf(CardTypeEnum.PAYBYLINK.getValue()).equals(cardtype))
                        {
                            transRespDetails = (EcospendResponseVO) pg.processCapture(trackingid, ecospendRequestVo);
                        }
                        else
                        {
                            transRespDetails = (EcospendResponseVO) pg.processSale(trackingid, ecospendRequestVo);
                        }
                    }
                }
                // Desktop desk = Desktop.getDesktop();

                if(functions.isValueNull(transRespDetails.getStatus()) && transRespDetails.getStatus().equalsIgnoreCase("pending")){
                    transRespDetails.setThreeDVersion("Non-3D");
                    paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                    paymentManager.insertEcospendDetails(transRespDetails,trackingid);
                    //pWriter.println(transRespDetails.getRedirectUrl());
                   // desk.browse(new URI(transRespDetails.getRedirectUrl()));
                    res.sendRedirect(transRespDetails.getRedirectUrl());
                    //pWriter.println());
                    return;
                }
                else
                {
                    respStatus="authfailed";
                    transactionLogger.debug("inside authfail --------------");

                    paymentManager.updateTransactionForCommon(transRespDetails, respStatus, trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());

                }


            if (transRespDetails != null)
            {
                if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                {

                    if (transRespDetails.getDescription() != null && !transRespDetails.getDescription().equals(""))
                    {

                        respRemark = "Successful (" + transRespDetails.getDescription() + ")";
                    }
                    else
                    {
                        respRemark = "Successful";
                    }
                    if (functions.isValueNull(transRespDetails.getDescriptor()))
                    {
                        billingDiscriptor = transRespDetails.getDescriptor();
                    }
                    else
                    {
                        billingDiscriptor = pg.getDisplayName();
                    }
                    billingDiscriptor = "";
                }
                else
                {
                    if (transRespDetails.getDescription() != null && !transRespDetails.getDescription().equals(""))
                    {
                        respRemark = "Failed (" + transRespDetails.getDescription() + ")";
                    }
                    else
                    {
                        respRemark = "Failed";
                    }
                }
                mailtransactionStatus = singleCallPaymentDAO.sendTransactionMail(respStatus, trackingid, respRemark, emailsent, billingDiscriptor);
                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
            }

            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl()))
            {
                transactionLogger.error("inside sending notification---" + commonValidatorVO.getTransDetailsVO().getNotificationUrl());
                commonValidatorVO.getTransDetailsVO().setBillingDiscriptor(billingDiscriptor);

                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(transactionDetailsVO, commonValidatorVO.getTrackingid(), "", "","");
            }

            //auto redirect check
            commonValidatorVO.getTransDetailsVO().setCurrency(transactionDetailsVO.getCurrency());
            commonValidatorVO.getTransDetailsVO().setRedirectUrl(transactionDetailsVO.getRedirectURL());
            commonValidatorVO.getTransDetailsVO().setOrderDesc(transactionDetailsVO.getOrderDescription());
            commonValidatorVO.getTransDetailsVO().setOrderId(transactionDetailsVO.getDescription());
           /* commonValidatorVO.setPaymentType(paymentType);
            commonValidatorVO.setCardType(cardtype);*/
            commonValidatorVO.setCardDetailsVO(commCardDetailsVO);
            commonValidatorVO.setTerminalId(transactionDetailsVO.getTerminalId());
            commonValidatorVO.setStatus(respRemark);

            transactionLogger.debug("currency for auto redirect----" + commonValidatorVO.getTransDetailsVO().getCurrency());
            if ("N".equalsIgnoreCase(autoRedirect))
            {
                req.setAttribute("transDetail", commonValidatorVO);
                //req.setAttribute("errorName", errorName);
                req.setAttribute("responceStatus", mailtransactionStatus);
                req.setAttribute("displayName", billingDiscriptor);
                req.setAttribute("ctoken", ctoken);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                requestDispatcher.forward(req, res);
                return;
            }
            else
            {
                try
                {
                    TransactionUtility transactionUtility = new TransactionUtility();
                    transactionUtility.doAutoRedirect(commonValidatorVO, res, mailtransactionStatus, billingDiscriptor);
                }
                catch (SystemError systemError)
                {
                    log.error("System Error while redirecting to redirect url", systemError);
                }
            }

        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException In EcospendProcess :::::", e);
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException In EcospendProcess :::::", e);
        }
    }
}
