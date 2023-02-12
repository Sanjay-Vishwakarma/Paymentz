package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.BlacklistManager;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.skrill.SkrillPaymentGateway;
import com.payment.skrill.SkrillResponseVO;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Admin on 5/5/2017.
 */
public class SkrillFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(SkrillFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        transactionLogger.error("Inside SkrillFrontEndServlet---");
        TransactionManager transactionManager = new TransactionManager();
        ActionEntry entry = new ActionEntry();
        CommResponseVO commResponseVO = new CommResponseVO();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericRequestVO genericRequestVO = new GenericRequestVO();
        MerchantDetailsVO merchantDetailsVO = null;
        MerchantDAO merchantDAO = new MerchantDAO();
        Functions functions = new Functions();
        HttpSession session = req.getSession(true);
        Connection con = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        PaymentManager paymentManager = new PaymentManager();
        BlacklistManager blacklistManager=new BlacklistManager();
        BlacklistVO blacklistVO=new BlacklistVO();
        String toId = "";
        String accountId = "";
        String status = "";
        String amount = "";
        String description = "";
        String redirectUrl = "";
        String clKeNy = "";
        String checksumNew = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String confirmStatus = "";
        String token = "";
        String orderDesc = "";
        String currency = "";
        String transactionId = "";
        String transactionStatus = "";
        String errorCode = "";
        String message = "";
        String billingDesc = "";
        String transType = "sale";
        String dbStatus = "";
        String tmpl_amt = "";
        String tmpl_currency = "";
        String paymodeid = "";
        String cardtypeid = "";
        String email = "";
        String customerEmail = "";
        String customerAmount = "";
        String customerCurrency = "";
        String respStatus = "";
        String trackingId = "";
        String cardHolderName = "";
        String phone = "";
        String isPowerBy="";
        String requestIp=Functions.getIpAddress(req);

        for (Object key : req.getParameterMap().keySet())
        {
            transactionLogger.error("----for loop SkrillFrontEndServlet-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }

        if (functions.isValueNull(req.getParameter("transaction_id")))
        {
            if (!functions.isNumericVal(req.getParameter("transaction_id")))
                trackingId = "";
            else
                trackingId = ESAPI.encoder().encodeForSQL(me, req.getParameter("transaction_id"));

            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            toId = transactionDetailsVO.getToid();
            accountId = transactionDetailsVO.getAccountId();
            amount = transactionDetailsVO.getAmount();
            tmpl_amt = transactionDetailsVO.getTemplateamount();
            tmpl_currency = transactionDetailsVO.getTemplatecurrency();
            description = transactionDetailsVO.getDescription();
            redirectUrl = transactionDetailsVO.getRedirectURL();
            if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                orderDesc = transactionDetailsVO.getOrderDescription();
            currency = transactionDetailsVO.getCurrency();
            paymodeid = transactionDetailsVO.getPaymodeId();
            cardtypeid = transactionDetailsVO.getCardTypeId();
            email = transactionDetailsVO.getEmailaddr();
            dbStatus = transactionDetailsVO.getStatus();
            cardHolderName = transactionDetailsVO.getName();
            phone = transactionDetailsVO.getTelno();

            SkrillPaymentGateway skrillPaymentGateway = new SkrillPaymentGateway(accountId);
            StatusSyncDAO statusSyncDAO = new StatusSyncDAO();

            try
            {
                transactionLogger.debug("dbStatus-------" + dbStatus);

                SkrillResponseVO commResponseVO1 = (SkrillResponseVO) skrillPaymentGateway.processInquiry(trackingId, genericRequestVO);
                transactionStatus = commResponseVO1.getStatus();
                errorCode = commResponseVO1.getErrorCode();
                transactionId = commResponseVO1.getTransactionId();

                if (functions.isValueNull(commResponseVO1.getFromEmail()))
                    customerEmail = commResponseVO1.getFromEmail();

                if (functions.isValueNull(commResponseVO1.getCustomerAmount()))
                    customerAmount = commResponseVO1.getCustomerAmount();
                else
                    customerAmount = tmpl_amt;

                if (functions.isValueNull(commResponseVO1.getCustomerCurrency()))
                    customerCurrency = commResponseVO1.getCustomerCurrency();
                else
                    customerCurrency = tmpl_currency;

                transactionLogger.error("from email---" + commResponseVO1.getFromEmail());
                transactionLogger.error("Customer Currency---" + commResponseVO1.getCustomerCurrency());
                transactionLogger.error("Customer Amount---" + commResponseVO1.getCustomerAmount());

                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                if(merchantDetailsVO!=null){
                    autoRedirect=merchantDetailsVO.getAutoRedirect();
                    logoName=merchantDetailsVO.getLogoName();
                    partnerName=merchantDetailsVO.getPartnerName();
                }

                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {


                    if (functions.isValueNull(errorCode) && functions.isValueNull(transactionStatus))
                    {
                        if(functions.isValueNull(commResponseVO1.getAmount()))
                        {
                            Double dbAmount=Double.parseDouble(transactionDetailsVO.getAmount());
                            Double resAmount=Double.parseDouble(commResponseVO1.getAmount());
                            amount=commResponseVO1.getAmount();
                            if (dbAmount!=resAmount && "success".equalsIgnoreCase(transactionStatus))
                            {
                                transactionStatus="failed";
                                commResponseVO.setStatus("failed");
                                message = "Failed-IRA";

                                blacklistVO.setEmailAddress(email);
                                blacklistVO.setActionExecutorId(toId);
                                blacklistVO.setActionExecutorName("AcquirerFrontEnd");
                                blacklistVO.setRemark("IncorrectAmount");
                                blacklistVO.setName(cardHolderName);
                                blacklistVO.setPhone(phone);
                                blacklistVO.setIpAddress(requestIp);
                                blacklistManager.commonBlackListing(blacklistVO);
                            }
                        }
                    /*try
                    {*/
                        auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                        auditTrailVO.setActionExecutorId(toId);
                        StringBuffer dbBuffer = new StringBuffer();


                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setErrorCode(errorCode);
                        commResponseVO.setTransactionType(transType);
                        commResponseVO.setTransactionStatus(transactionStatus);

                        if ("2".equals(errorCode) && "success".equalsIgnoreCase(transactionStatus))
                        {
                            status = "success";
                            message = "Transaction Successful";
                            respStatus = "Successful";
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                            commResponseVO.setDescription(message);
                            commResponseVO.setStatus(status);
                            commResponseVO.setRemark(message);
                            commResponseVO.setDescriptor(billingDesc);

                            confirmStatus = "Y";

                            dbStatus = "capturesuccess";
                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',templateamount='" + customerAmount + "',templatecurrency='" + customerCurrency + "',status='capturesuccess'");
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                            paymentManager.updateSkrillNetellerDetailEntry(commResponseVO1, trackingId);
                        }
                        else
                        {
                            respStatus="failed";
                            confirmStatus = "N";
                            status = "fail";
                            if(!functions.isValueNull(message))
                                message = "Transaction Failed";
                            commResponseVO.setStatus(status);
                            commResponseVO.setDescription(message);
                            commResponseVO.setRemark(message);
                            dbStatus = "authfailed";
                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',templateamount='" + customerAmount + "',templatecurrency='" + customerCurrency + "'");
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                            paymentManager.updateSkrillNetellerDetailEntry(commResponseVO1, trackingId);
                        }

                        dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                        transactionLogger.debug("Update Query---" + dbBuffer);
                        con = Database.getConnection();
                        Database.executeUpdate(dbBuffer.toString(), con);
                    }
                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, dbStatus);
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                }

                else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status = "success";
                        message = "Transaction Successful";
                        respStatus = "Successful";
                    }
                    else
                    {
                        status = "fail";
                        message = "Transaction Declined";
                        respStatus = "Failed";
                    }
                }


                String custId = "";
                String custEmail = "";
                String custBankId = "";

                HashMap hashMap = paymentManager.getExtnDetailsForSkrill(trackingId);


                if (functions.isValueNull((String) hashMap.get("customerBankId")))
                    custBankId = (String) hashMap.get("customerBankId");

                commonValidatorVO.setTrackingid(trackingId);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                addressDetailsVO.setTmpl_amount(customerAmount);
                addressDetailsVO.setTmpl_currency(customerCurrency);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
                commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());
                if (functions.isValueNull(customerEmail))
                    addressDetailsVO.setEmail(customerEmail);
                else
                    addressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);

                commonValidatorVO.setCustomerId(transactionDetailsVO.getCustomerId());
                commonValidatorVO.setCustomerBankId(custBankId);

                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);

                if ("Y".equalsIgnoreCase(autoRedirect))
                {
                    TransactionUtility transactionUtility = new TransactionUtility();
                    transactionLogger.error("respStatus in Y---" + respStatus);
                    transactionUtility.doAutoRedirect(commonValidatorVO, res, respStatus, billingDesc);
                }
                else
                {

                    session.setAttribute("ctoken", ctoken);
                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", respStatus);
                    req.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    String confirmationPage = "";
                    String version = (String)session.getAttribute("version");
                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";

                    RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(req, res);
                    session.invalidate();
                }

            }
            catch (PZTechnicalViolationException e)
            {
                transactionLogger.error("PZTechnicalViolationException:::::", e);
            }
            catch (PZDBViolationException tve)
            {
                transactionLogger.error("PZDBViolationException:::::", tve);
            }
            catch (SystemError e)
            {
                transactionLogger.error("SystemError:::::", e);
                PZExceptionHandler.raiseAndHandleDBViolationException("SkrillFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
            }finally
            {
                Database.closePreparedStatement(p);
                Database.closeResultSet(rs);
                Database.closeConnection(con);
            }

        }
    }

}


