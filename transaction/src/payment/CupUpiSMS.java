package payment;

import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.manager.MerchantConfigManager;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.MailEventEnum;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommResponseVO;
import com.payment.cupUPI.*;
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
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Created by Jitendra.p on 11-Jul-19.
 */
public class CupUpiSMS extends HttpServlet
{
    private static Logger log = new Logger(CupUpiSMS.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(CupUpiSMS.class.getName());
    Functions functions = new Functions();

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        transactionLogger.error("------ Inside CupUpi ------");
        HttpSession session = req.getSession(true);
        String ctoken = req.getParameter("ctoken");
        session.setAttribute("ctoken", ctoken);
        CommonValidatorVO commonValidatorVO = null;
        commonValidatorVO = ReadRequest.getSpecificRequestParametersForSale1(req);
        transactionLogger.debug("card number  "+commonValidatorVO.getPaymentMode());

        for (Object key : req.getParameterMap().keySet())
        {
            transactionLogger.error("----for loop Cup-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }

        Enumeration en = req.getParameterNames();
        while (en.hasMoreElements())
        {
            String key = (String) en.nextElement();
            String value = req.getParameter(key);
            transactionLogger.error("key----" + key + "-----" + value);
        }

        Functions functions= new Functions();
        SingleCallPaymentDAO singleCallPaymentDAO=new SingleCallPaymentDAO();
        PrintWriter ps=res.getWriter();
        String smsCode="";
        String status="";
        String trackingid="";
        String toId="";
        String accountid="";
        String cardNumber="";
        String expiryMonth="";
        String expiryYear="";
        String cvv="";
        String phoneNumber="";
        String customerIp="";
        String toid="";
        String phoneNumberCC="";
        String respStatus="";
        String respRemark="";
        String emailsent="";
        String autoRedirect="";
        String tmpl_amount="";
        String tmpl_currency="";
        String isService="";

        if(functions.isValueNull(req.getParameter("code")))
        {
            smsCode = req.getParameter("code");                 // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("trackingid")) )
        {
            trackingid = req.getParameter("trackingid");    // Got From singlecall
        }
        if(functions.isValueNull(req.getParameter("cardNumber")) )
        {
            cardNumber = PzEncryptor.decryptPAN(req.getParameter("cardNumber"));    // Got From singlecall
            transactionLogger.debug("Card Number From Hidden"+cardNumber);
        }
        if(functions.isValueNull(req.getParameter("expiryMonth")) )
        {
            expiryMonth = PzEncryptor.decryptExpiryDate(req.getParameter("expiryMonth"));    // Got From singlecall
            transactionLogger.debug("expiryMonth From Hidden debug"+expiryMonth);
        }
        if(functions.isValueNull(req.getParameter("expiryYear")) )
        {
            expiryYear = PzEncryptor.decryptExpiryDate(req.getParameter("expiryYear"));    // Got From singlecall
            transactionLogger.debug("expiryYear From Hidden debug"+expiryYear);
        }
        if(functions.isValueNull(req.getParameter("cvv")) )
        {
            cvv = PzEncryptor.decryptCVV(req.getParameter("cvv"));    // Got From singlecall
            transactionLogger.debug("cvv From Hidden"+cvv);
        }
        if(functions.isValueNull(req.getParameter("phoneNumber")) )
        {
            phoneNumber = req.getParameter("phoneNumber");    // Got From singlecall
            transactionLogger.debug("phoneNumber From Hidden"+phoneNumber);
        }
        if(functions.isValueNull(req.getParameter("phoneNumber")) )
        {
            phoneNumberCC = req.getParameter("phoneNumberCC");    // Got From singlecall
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

        String phoneNumberNew=phoneNumberCC+"-"+phoneNumber;
        TransactionDetailsVO transactionDetailsVO = null;
        TransactionManager transactionManager = new TransactionManager();
        transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid); // Getting Details from transaction_common
        CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
        commCardDetailsVO.setCardNum(cardNumber);
        commCardDetailsVO.setExpMonth(expiryMonth);
        commCardDetailsVO.setExpYear(expiryYear);
        commCardDetailsVO.setcVV(cvv);

        CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
        commAddressDetailsVO.setTmpl_amount(tmpl_amount);
        commAddressDetailsVO.setTmpl_currency(tmpl_currency);
        UnionPayInternationalResponseVO UnionPayInternationalResponseVO1=new UnionPayInternationalResponseVO();
        UnionPayInternationalRequestVO unionPayInternationalRequestVO=new UnionPayInternationalRequestVO();
        UnionPayInternationalUtils unionPayInternationalUtils=new UnionPayInternationalUtils();
        UnionPayInternationalUtility unionPayInternationalUtility=new UnionPayInternationalUtility();
        // as we are not getting unionPayInternationalRequestVO here we are filling unionPayInternationalRequestVO from transactionDetailsVO
        unionPayInternationalRequestVO=unionPayInternationalUtility.getUnionPayRequestVO(transactionDetailsVO,commCardDetailsVO,commAddressDetailsVO,phoneNumberNew);

       // unionPayInternationalRequestVO.setAddressDetailsVO("");
        CommResponseVO transRespDetails=new CommResponseVO();
        String billingDiscriptor = "";
        String mailtransactionStatus = "Failed";
        PaymentManager paymentManager = new PaymentManager();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        auditTrailVO.setActionExecutorId(unionPayInternationalRequestVO.getCommMerchantVO().getMerchantId());
        auditTrailVO.setActionExecutorName("Customer");
        AbstractPaymentGateway pg = null;
        try
        {
            pg = AbstractPaymentGateway.getGateway(unionPayInternationalRequestVO.getCommMerchantVO().getAccountId());
            System.out.println("pg ---------"+pg);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("Exception IN CupUpiSMS --- ",systemError);
        }

        UnionPayInternationalPaymentGateway unionPayInternationalPaymentGateway=new UnionPayInternationalPaymentGateway(unionPayInternationalRequestVO.getCommMerchantVO().getAccountId());
        commonValidatorVO.setCardDetailsVO(unionPayInternationalRequestVO.getCardDetailsVO());
        commonValidatorVO.setAddressDetailsVO(unionPayInternationalRequestVO.getAddressDetailsVO());
        commonValidatorVO.setTransDetailsVO(unionPayInternationalRequestVO.getTransDetailsVO());
        MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        try
        {
            if (functions.isValueNull(unionPayInternationalRequestVO.getCommMerchantVO().getMerchantId()))
            {
                merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(unionPayInternationalRequestVO.getCommMerchantVO().getMerchantId());
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("Exception --- "+e.getMessage());
        }
        int detailId= 0; //smsstared entry
        try
        {
            paymentManager.insertEnrollmentStartedTransactionEntryForCupUPI(commonValidatorVO, trackingid, auditTrailVO, "enrollmentstarted");
            transRespDetails= (UnionPayInternationalResponseVO) unionPayInternationalPaymentGateway.processEasyEnrollment(trackingid, unionPayInternationalRequestVO, smsCode);
            transactionLogger.error("after UnionPayInternationalResponseVO1 --- "+transRespDetails.getStatus());
        }
        catch (PZDBViolationException e)
        {
           transactionLogger.error("Exception --- "+e.getMessage());
        }
        try
        {
            if (transRespDetails.getStatus().equalsIgnoreCase("success"))
            {
                transactionLogger.debug("Inside success of enrollment ----- ");
                // Insert this card into upi_bin_card for next time use.
                String queryResult=UnionPayInternationalUtils.insertCardForEnrollment(unionPayInternationalRequestVO.getCardDetailsVO().getCardNum(), unionPayInternationalRequestVO.getAddressDetailsVO().getPhone());
                transactionLogger.error("queryResult ----------"+queryResult);
                System.out.println("queryResult in CupUpi SMS ------------"+queryResult);

                paymentManager.insertAuthStartedTransactionEntryForCupUPI(commonValidatorVO, trackingid, auditTrailVO);
                if (isService.equalsIgnoreCase("N"))
                {
                    transRespDetails = (CommResponseVO) pg.processAuthentication(trackingid, unionPayInternationalRequestVO);
                    respStatus = "authsuccessful";
                }
                else
                {
                    transRespDetails = (CommResponseVO) pg.processSale(trackingid, unionPayInternationalRequestVO);
                    respStatus = "capturesuccess";
                }
                if (transRespDetails.getStatus().equalsIgnoreCase("success"))
                {

                    transactionLogger.debug("inside capturesuccess --------------");
                   // paymentManager.updateTransactionForCupUpi(transRespDetails, "capturesuccess", trackingid, auditTrailVO, "transaction_common", customerIp); // have to set value for audit trail and ip
                    paymentManager.updateTransactionForCommon(transRespDetails, respStatus, trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());

                }
                else
                {
                    respStatus="authfailed";
                    transactionLogger.debug("inside authfail --------------");

                   // paymentManager.updateTransactionForCupUpi(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common", customerIp); // have to set value for audit trail and ip
                    paymentManager.updateTransactionForCommon(transRespDetails, respStatus, trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());

                }
            }
            else
            {
                respStatus="authfailed";
                transactionLogger.debug("Inside This enroolllme fail");
              //  paymentManager.updateTransactionForCommon(transRespDetails, responseStatus, trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
               // transRespDetails.setTransactionId("");
                paymentManager.updateTransactionForCupUpi(transRespDetails, "authfailed", trackingid, auditTrailVO, "transaction_common",customerIp); // have to set value for audit trail and ip
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
                else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("fail"))
                {
                    transactionLogger.debug("Inside this ----------------------");
                    log.debug("CHK BillingDescriptor in SingleCallPayment----" + transRespDetails.getDescriptor());
                    transactionLogger.debug("CHK BillingDescriptor in SingleCallPayment----" + transRespDetails.getDescriptor());
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
           // commonValidatorVO.getTransDetailsVO().setAmount(transactionDetailsVO.getAmount());
            commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
            commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());
            commonValidatorVO.setEci(transactionDetailsVO.getEci());
            commonValidatorVO.setCardDetailsVO(commCardDetailsVO);
           // commonValidatorVO.setAddressDetailsVO(unionPayInternationalRequestVO.getAddressDetailsVO());
            commonValidatorVO.setTerminalId(transactionDetailsVO.getTerminalId());
            commonValidatorVO.setCustomerId(transactionDetailsVO.getCustomerId());
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
                System.out.println("cup upi java Inside  auto redirect Y - ");
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
            transactionLogger.error("PZDBViolationException In CupUpiSMS :::::", e);
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException In CupUpiSMS :::::", e);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException In CupUpiSMS :::::", e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError In CupUpiSMS :::::", systemError);
        }
    }


}
