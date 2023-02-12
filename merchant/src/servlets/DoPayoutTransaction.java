import com.directi.pg.*;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.manager.TerminalManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.helper.TransactionHelper;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.PaymentProcessFactory;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.request.PZPayoutRequest;
import com.payment.response.PZPayoutResponse;
import com.payment.response.PZResponseStatus;
import com.payment.validators.vo.CommonValidatorVO;
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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Suraj on 7/21/2017.
 */
public class DoPayoutTransaction extends HttpServlet
{
    private static Logger logger = new Logger(DoPayoutTransaction.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        Merchants merchants = new Merchants();
        if(!merchants.isLoggedIn(session)){
            res.sendRedirect("/merchant/logout.jsp");
            return;
        }

        User user               = (User)session.getAttribute("ESAPIUserSessionKey");
        String memberId         = (String) session.getAttribute("merchantid");
        String ipAddress        = req.getRemoteAddr();
        Functions functions     = new Functions();
        ResourceBundle rb1      = null;
        String language_property1   = (String)session.getAttribute("language_property");
        rb1                         = LoadProperties.getProperty(language_property1);
        String DoPayoutTransaction_OrderId_errormsg     = StringUtils.isNotEmpty(rb1.getString("DoPayoutTransaction_OrderId_errormsg"))?rb1.getString("DoPayoutTransaction_OrderId_errormsg"): "Invalid Order Id";
        String DoPayoutTransaction_Order_errormsg       = StringUtils.isNotEmpty(rb1.getString("DoPayoutTransaction_Order_errormsg"))?rb1.getString("DoPayoutTransaction_Order_errormsg"): "Invalid Order Description";
        String DoPayoutTransaction_payout_errormsg      = StringUtils.isNotEmpty(rb1.getString("DoPayoutTransaction_payout_errormsg"))?rb1.getString("DoPayoutTransaction_payout_errormsg"): "Invalid Payout Amount";
        String DoPayoutTransaction_bitcoin_errormsg     = StringUtils.isNotEmpty(rb1.getString("DoPayoutTransaction_bitcoin_errormsg"))?rb1.getString("DoPayoutTransaction_bitcoin_errormsg"): "Invalid Customer Bitcoin Address";
        String DoPayoutTransaction_customer_errormsg    = StringUtils.isNotEmpty(rb1.getString("DoPayoutTransaction_customer_errormsg"))?rb1.getString("DoPayoutTransaction_customer_errormsg"): "Invalid Customer Bank Code";
        String DoPayoutTransaction_bank_errormsg        = StringUtils.isNotEmpty(rb1.getString("DoPayoutTransaction_bank_errormsg"))?rb1.getString("DoPayoutTransaction_bank_errormsg"): "Invalid Customer Bank Account Number";
        String DoPayoutTransaction_bank1_errormsg       = StringUtils.isNotEmpty(rb1.getString("DoPayoutTransaction_bank1_errormsg"))?rb1.getString("DoPayoutTransaction_bank1_errormsg"): "Invalid  Bank Account Number";
        String DoPayoutTransaction_name_errormsg        = StringUtils.isNotEmpty(rb1.getString("DoPayoutTransaction_name_errormsg"))?rb1.getString("DoPayoutTransaction_name_errormsg"): "Invalid  Bank Account Name ";
        String DoPayoutTransaction_ifsc_errormsg        = StringUtils.isNotEmpty(rb1.getString("DoPayoutTransaction_ifsc_errormsg"))?rb1.getString("DoPayoutTransaction_ifsc_errormsg"): "Invalid Bank Ifsc Code";
        String DoPayoutTransaction_error_errormsg       = StringUtils.isNotEmpty(rb1.getString("DoPayoutTransaction_error_errormsg"))?rb1.getString("DoPayoutTransaction_error_errormsg"): "Internal error while processing your request";
        String pod_Brand    = StringUtils.isNotEmpty(rb1.getString("pod_Brand"))?rb1.getString("pod_Brand"): "Brand";
        String pod_Amount   = StringUtils.isNotEmpty(rb1.getString("pod_Amount"))?rb1.getString("pod_Amount"): "Amount";
        String pod_Currency = StringUtils.isNotEmpty(rb1.getString("pod_Currency"))?rb1.getString("pod_Currency"): "Currency";
        String pod_Status   = StringUtils.isNotEmpty(rb1.getString("pod_Status"))?rb1.getString("pod_Status"): "Status";
        String fromType     = "";
        if(functions.isValueNull(req.getParameter("fromType")))
        {
            fromType    = req.getParameter("fromType");
            logger.debug("fromType DoPayoutTransaction ------------"+fromType);
        }
        AuditTrailVO auditTrailVO   = new AuditTrailVO();
        MerchantDAO merchantDAO     = new MerchantDAO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();

        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);

        String trackingId       = null;
        String payoutAmount     = null;
        String orderId          = null;
        String customerBitcoinAddress   = null;
        String orderDescription         = null;
        String accountId                = null;
        String terminalId               = null;
        String customerAccount=null;
        String customerEmail=null;
        String customerId = "";
        String responseMessage="";
        String custBankId = "";
        String EOL="<BR>";

//        for zotapay(bank details)
        String customerBankCode             = "";
        String customerBankAccountNumber    = "";
        String customerBankAccountName      = "";

        String bankTransferType = "";
        String bankAccountNo    = "";
        String bankIfsc         = "";


        if(!ESAPI.validator().isValidInput("orderid", req.getParameter("orderid"), "Description", 100, false)){
            responseMessage = DoPayoutTransaction_OrderId_errormsg+EOL;
        }
        else
        {
            orderId = req.getParameter("orderid");
        }

        if(!ESAPI.validator().isValidInput("orderdescription", req.getParameter("orderdescription"), "Description", 100, false)){
            responseMessage = responseMessage+DoPayoutTransaction_Order_errormsg+EOL;
        }
        else
        {
            orderDescription = req.getParameter("orderdescription");
        }

        if(!ESAPI.validator().isValidInput("payoutamount", req.getParameter("payoutamount"), "Amount", 10, false)){
            responseMessage = responseMessage+DoPayoutTransaction_payout_errormsg+EOL;

        }
        else
        {
            payoutAmount = req.getParameter("payoutamount");
        }

        if (functions.isValueNull(fromType) &&(fromType.equalsIgnoreCase("bitcoinpg") || fromType.equalsIgnoreCase("bitclear") ) )
        {
            if (!ESAPI.validator().isValidInput("customerbitcoinaddress", req.getParameter("customerbitcoinaddress"), "Description", 100, false))
            {
                responseMessage = responseMessage + DoPayoutTransaction_bitcoin_errormsg + EOL;
            }
            else
            {
                customerBitcoinAddress = req.getParameter("customerbitcoinaddress");
            }
        }

//        for zotaPay (bank details)
        if (functions.isValueNull(fromType) && fromType.equalsIgnoreCase("zota"))
        {
            if (!ESAPI.validator().isValidInput("bankCode", req.getParameter("bankCode"), "SafeString", 100, false))
            {
                responseMessage = responseMessage + DoPayoutTransaction_customer_errormsg + EOL;
            }
            else
            {
                customerBankCode = req.getParameter("bankCode");
            }
            if (!ESAPI.validator().isValidInput("bankAccountNumber", req.getParameter("bankAccountNumber"), "SafeString", 100, false))
            {
                responseMessage = responseMessage + DoPayoutTransaction_bank_errormsg + EOL;
            }
            else
            {
                customerBankAccountNumber = req.getParameter("bankAccountNumber");
            }
            if (!ESAPI.validator().isValidInput("bankAccountName", req.getParameter("bankAccountName"), "SafeString", 100, false))
            {
                responseMessage = responseMessage + DoPayoutTransaction_bank_errormsg + EOL;
            }
            else
            {
                customerBankAccountName = req.getParameter("bankAccountName");
            }
            //System.out.println("Bank Code---"+customerBankCode);
            //System.out.println("Bank customerBankAccountNumber---"+customerBankAccountNumber);
            //System.out.println("Bank customerBankAccountName---"+customerBankAccountName);
        }

        // for safexpay integeration payout ...
        if (functions.isValueNull(fromType) && fromType.equalsIgnoreCase("safexpay"))
        {
            if (!ESAPI.validator().isValidInput("transferType", req.getParameter("transferType"), "StrictString", 5, false))
            {
                responseMessage = responseMessage + "Invalid Bank Transfer Type" + EOL;
            }
            else
            {
                bankTransferType = req.getParameter("transferType");
            }
            if (!ESAPI.validator().isValidInput("bankAccountNumber", req.getParameter("bankAccountNumber"), "OnlyNumber",35, false))
            {
                responseMessage = responseMessage + DoPayoutTransaction_bank1_errormsg + EOL;
            }
            else
            {
                bankAccountNo = req.getParameter("bankAccountNumber");
            }
            if (!ESAPI.validator().isValidInput("bankAccountName", req.getParameter("bankAccountName"), "SafeString", 35, false))
            {
                responseMessage = responseMessage + DoPayoutTransaction_name_errormsg + EOL;
            }
            else
            {
                customerBankAccountName = req.getParameter("bankAccountName");
            }
            if (!ESAPI.validator().isValidInput("bankIfsc", req.getParameter("bankIfsc"), "IfscCode", 11, false))
            {
                responseMessage = responseMessage + DoPayoutTransaction_ifsc_errormsg + EOL;
            }
            else
            {
                bankIfsc = req.getParameter("bankIfsc");
            }
            /*System.out.println("transferType ---"+bankTransferType);
            System.out.println("bankAccountNo ---"+bankAccountNo);
            System.out.println("bankIfsc---"+bankIfsc);
            System.out.println("bankAccountName is -- >"+customerBankAccountName);*/
        }
        if(functions.isValueNull(responseMessage))
        {
            req.setAttribute("message",responseMessage);
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/GetPayoutDetails?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        try{
            trackingId  = ESAPI.validator().getValidInput("icicitransid", req.getParameter("icicitransid"), "Numbers", 20, false);
            accountId   = ESAPI.validator().getValidInput("accountid", req.getParameter("accountid"), "Numbers", 20, true);
            terminalId  = ESAPI.validator().getValidInput("terminalid", req.getParameter("terminalid"), "Numbers", 20, true);
        }
        catch (ValidationException e){
            logger.error("Invalid INPUT", e);
            PZExceptionHandler.raiseAndHandleConstraintViolationException("DoPayoutTransaction.java", "doPost()", null, "Merchant", "Validation Exception Thrown:::", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, e.getMessage(), e.getCause(), memberId, PZOperations.MERCHANT_REFUND);
            responseMessage = DoPayoutTransaction_error_errormsg;
            req.setAttribute("message", responseMessage);
            RequestDispatcher rd = req.getRequestDispatcher("/servlet/GetPayoutDetails?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        try{
            if (!Functions.checkAccuracy(payoutAmount, 2)){
                PZExceptionHandler.raiseConstraintViolationException("DoPayoutTransaction.java","doPost()",null,"Merchant", ErrorMessages.PAYOUT_AMT_ACCURACY,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }

            MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
            if (!functions.isValueNull(merchantDetailsVO.getMemberId())){
                PZExceptionHandler.raiseConstraintViolationException("DoPayoutTransaction.java", "doPost()", null, "Merchant", ErrorMessages.INVALID_MEMBER_AUTHENTICATION, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, null, null);
            }

            //Add merchant order id unique check.
            TransactionHelper transactionHelper = new TransactionHelper();
            String uniqueOrder                  = transactionHelper.checkorderuniqueness(merchantDetailsVO.getMemberId(), "",orderId);
            if (!uniqueOrder.equals("")){
                PZExceptionHandler.raiseConstraintViolationException("DoPayoutTransaction.java","doPost()",null,"Merchant", ErrorMessages.DUPLICATE_DESCRIPTION,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }

            double maxTransactionAmount = 0.00;
            double minTransactionAmount = 0.00;
            TerminalManager terminalManager = new TerminalManager();
            TerminalVO terminalVO           = terminalManager.getMemberTerminalfromTerminal(terminalId);

            if(terminalVO != null){
                minTransactionAmount    = terminalVO.getMin_transaction_amount();
                maxTransactionAmount    = terminalVO.getMax_transaction_amount();
            }
            else{
                String error = "Terminal details not found";
                PZExceptionHandler.raiseConstraintViolationException("DoPayoutTransaction.java", "doPost()", null, "Merchant",error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
            }

            //Added min-max transaction amount check.
            Float amt = Float.parseFloat(payoutAmount);
            if (amt > maxTransactionAmount)
            {
                String error = "Amount greater than maximum transaction amount";
                PZExceptionHandler.raiseConstraintViolationException("DoPayoutTransaction.java", "doPost()", null, "Merchant",error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
            }
            if (amt < minTransactionAmount)
            {
                String error = "Amount less than minimum transaction amount";
                PZExceptionHandler.raiseConstraintViolationException("DoPayoutTransaction.java", "doPost()", null, "Merchant",error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
            }

            if (!trackingId.trim().equals("") && !orderId.trim().equals(""))
            {
                TransactionManager transactionManager       = new TransactionManager();
                TransactionDetailsVO transactionDetailsVO   = transactionManager.getTransDetailFromCommon(trackingId);

                if(transactionDetailsVO != null){
                    customerEmail   = transactionDetailsVO.getEmailaddr();
                }

                AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(trackingId), Integer.parseInt(accountId));
                //CommCardDetailsVO commCardDetailsVO=paymentProcess.getCustomerAccountDetails(trackingId);

                commonValidatorVO.setTrackingid(trackingId);

                commonValidatorVO = paymentProcess.getExtentionDetails(commonValidatorVO);

                //add validation
                if(commonValidatorVO.getCustAccount()!=null){
                    customerAccount = commonValidatorVO.getCustAccount();
                }
                if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()))
                {
                    customerEmail = commonValidatorVO.getAddressDetailsVO().getEmail();
                }
                if (functions.isValueNull(commonValidatorVO.getCustomerId()))
                {
                    customerId = commonValidatorVO.getCustomerId();
                }
                if (functions.isValueNull(commonValidatorVO.getCustomerBankId()))
                {
                    custBankId = commonValidatorVO.getCustomerBankId();
                }

                auditTrailVO.setActionExecutorId(memberId);
                String role = "";
                for (String s : user.getRoles())
                {
                    role    = role.concat(s);
                }
                auditTrailVO.setActionExecutorName(role+"-"+session.getAttribute("username").toString());
                PZPayoutRequest pzPayoutRequest = new PZPayoutRequest();

                pzPayoutRequest.setTrackingId(Integer.valueOf(trackingId));
                pzPayoutRequest.setPayoutAmount(payoutAmount);
                pzPayoutRequest.setAccountId(Integer.valueOf(accountId));
                pzPayoutRequest.setMemberId(Integer.valueOf(memberId));
                pzPayoutRequest.setOrderId(orderId);
                pzPayoutRequest.setOrderDescription(orderDescription);
                pzPayoutRequest.setIpAddress(ipAddress);
                pzPayoutRequest.setTerminalId(terminalId);
                pzPayoutRequest.setCustomerEmail(customerEmail);
                pzPayoutRequest.setCustomerAccount(customerAccount);
                pzPayoutRequest.setAuditTrailVO(auditTrailVO);
                pzPayoutRequest.setCustomerId(customerId);
                pzPayoutRequest.setCustomerBankId(custBankId);
                pzPayoutRequest.setCustomerBitcoinAddress(customerBitcoinAddress);

                pzPayoutRequest.setCustomerBankCode(customerBankCode);
                pzPayoutRequest.setCustomerBankAccountName(customerBankAccountName);
                pzPayoutRequest.setCustomerBankAccountNumber(customerBankAccountNumber);

                pzPayoutRequest.setBankTransferType(bankTransferType);
                pzPayoutRequest.setBankAccountNo(bankAccountNo);
                pzPayoutRequest.setBankIfsc(bankIfsc);


                PZPayoutResponse payoutResponse = paymentProcess.payout(pzPayoutRequest);
                if(PZResponseStatus.SUCCESS.equals(payoutResponse.getStatus()) || PZResponseStatus.PAYOUTSUCCESSFUL.equals(payoutResponse.getStatus())){
                    responseMessage="Y";
                }
                else if(PZResponseStatus.FAILED.equals(payoutResponse.getStatus()) || PZResponseStatus.PAYOUTFAILED.equals(payoutResponse.getStatus())){
                    responseMessage="F";
                }
                else if(PZResponseStatus.PENDING.equals(payoutResponse.getStatus())){
                    responseMessage ="P";
                }

                Map hiddenVariables = new HashMap();
                hiddenVariables.put("orderid",pzPayoutRequest.getOrderId());
                hiddenVariables.put("description",pzPayoutRequest.getOrderDescription());
                hiddenVariables.put("status",responseMessage);
                hiddenVariables.put("statusDesc",payoutResponse.getResponseDesceiption());
                hiddenVariables.put("trackingid", Integer.valueOf(payoutResponse.getTrackingId()));
                hiddenVariables.put("resAmount", payoutResponse.getPayoutAmount());
                hiddenVariables.put("vouchernumber",payoutResponse.getVoucherNumber() );

                req.setAttribute("hiddenResponse",hiddenVariables);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/payoutconfirmation.jsp?ctoken="+user.getCSRFToken());
                requestDispatcher.forward(req, res);
                return;
            }
            else{
                throw new Exception("Tracking ID should not be empty");
            }
        }
        catch(PZConstraintViolationException cve){
            logger.error("PZConstraintViolationException in DoReverseTransaction---",cve);
            PZExceptionHandler.handleCVEException(cve,memberId,PZOperations.MERCHANT_PAYOUT);
            responseMessage = cve.getPzConstraint().getMessage();
        }
        catch (SystemError se){
            logger.error("SystemError",se);
            responseMessage = DoPayoutTransaction_error_errormsg;
        }
        catch (SQLException e){
            logger.error("SqlException", e);
            responseMessage = DoPayoutTransaction_error_errormsg;
        }
        catch (Exception e){
            responseMessage = DoPayoutTransaction_error_errormsg;
            logger.error("Exception", e);
        }
        req.setAttribute("message",responseMessage);
        RequestDispatcher rd = req.getRequestDispatcher("/payoutlist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}
