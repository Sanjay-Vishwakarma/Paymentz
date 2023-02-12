package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
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
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by Admin on 11/24/17.
 */
public class TrustlyFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(TrustlyFrontEndServlet.class.getName());
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
        transactionLogger.error("-----Inside TrustlyFrontEndServlet------");
        TransactionManager transactionManager = new TransactionManager();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        MerchantDAO merchantDAO= new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO = null;
        Functions functions = new Functions();
        HttpSession session = req.getSession(true);
        String toId = "";
        String accountId = "";
        String status = "";
        String amount = "";
        String description = "";
        String redirectUrl = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String orderDesc = "";
        String currency = "";
        String message = "";
        String billingDesc = "";
        String transType = "sale";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String dbstatus = "";
        String paymodeid = "";
        String cardtypeid = "";
        String email = "";
        String customerid="";
        String firstName="";
        String lastName="";
        String responseStatus="";
        String confirmStatus="";

        for (Object key : req.getParameterMap().keySet())
        {
            transactionLogger.error("----for loop TrustlyFrontEndServlet-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }

            if (functions.isValueNull(req.getParameter("trackingId")))
            {
                String trackingId = req.getParameter("trackingId");
                String transactionStatus = req.getParameter("status");


                transactionLogger.error("trackingId::::::" + trackingId);
                transactionLogger.error("transactionStatus::::::" + transactionStatus);



                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

                if (transactionDetailsVO != null)
                {


                    toId = transactionDetailsVO.getToid();
                    accountId = transactionDetailsVO.getAccountId();
                    amount = transactionDetailsVO.getAmount();
                    description = transactionDetailsVO.getDescription();
                    redirectUrl = transactionDetailsVO.getRedirectURL();
                    tmpl_amount = transactionDetailsVO.getTemplateamount();
                    tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                    dbstatus = transactionDetailsVO.getStatus();
                    paymodeid = transactionDetailsVO.getPaymodeId();
                    cardtypeid = transactionDetailsVO.getCardTypeId();
                    email = transactionDetailsVO.getEmailaddr();
                    firstName=transactionDetailsVO.getFirstName();
                    lastName=transactionDetailsVO.getLastName();
                    customerid=transactionDetailsVO.getCustomerId();


                    if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                        orderDesc = transactionDetailsVO.getOrderDescription();
                    currency = transactionDetailsVO.getCurrency();

                    try{
                        Thread.sleep(3000);

                        if (functions.isValueNull(transactionStatus))
                        {

                            auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                            auditTrailVO.setActionExecutorId(toId);

                            merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                            if(merchantDetailsVO!=null){
                                autoRedirect=merchantDetailsVO.getAutoRedirect();
                                logoName=merchantDetailsVO.getLogoName();
                                partnerName=merchantDetailsVO.getPartnerName();
                            }
                            transactionLogger.error("dbstatus------" + dbstatus);

                            if ("success".equalsIgnoreCase(transactionStatus))
                            {
                                status = "Transaction Successful";
                                responseStatus="Successful";
                                confirmStatus = "Y";
                                billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);


                                AsynchronousSmsService smsService = new AsynchronousSmsService();
                                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                            }else
                            {
                                status = "Transaction Failed";
                                responseStatus="Failed";
                                confirmStatus = "N";
                            }
                        }
                        else
                        {
                            status = "Transaction Failed";
                            responseStatus="Failed";
                            confirmStatus = "N";
                        }


                        commonValidatorVO.setTrackingid(trackingId);
                        genericTransDetailsVO.setOrderId(description);
                        genericTransDetailsVO.setAmount(amount);
                        genericTransDetailsVO.setCurrency(currency);
                        genericTransDetailsVO.setOrderDesc(orderDesc);

                        commonValidatorVO.setCardType(cardtypeid);
                        commonValidatorVO.setPaymentType(paymodeid);
                        genericTransDetailsVO.setRedirectUrl(redirectUrl);
                        commonValidatorVO.setLogoName(logoName);
                        commonValidatorVO.setPartnerName(partnerName);
                        addressDetailsVO.setTmpl_amount(tmpl_amount);
                        addressDetailsVO.setTmpl_currency(tmpl_currency);
                        addressDetailsVO.setEmail(email);
                        addressDetailsVO.setFirstname(firstName);
                        addressDetailsVO.setLastname(lastName);
                        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                        commonValidatorVO.setCustomerId(customerid);

                        if ("Y".equalsIgnoreCase(autoRedirect))
                        {
                            TransactionUtility transactionUtility = new TransactionUtility();
                            transactionUtility.doAutoRedirect(commonValidatorVO, res, responseStatus, billingDesc);
                        }
                        else
                        {

                            session.setAttribute("ctoken", ctoken);
                            req.setAttribute("transDetail", commonValidatorVO);
                            req.setAttribute("responceStatus", responseStatus);
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
                    catch(PZDBViolationException e)
                    {
                        transactionLogger.error("PZDBViolationException----", e);
                    }
                    catch (SystemError se)
                    {
                        transactionLogger.error("SystemError::::::", se);
                        PZExceptionHandler.raiseAndHandleDBViolationException("TrustlyFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
                    }
                    catch (InterruptedException ne)
                    {
                        transactionLogger.error("NoSuchAlgorithmException:::::", ne);
                        PZExceptionHandler.raiseAndHandleTechnicalViolationException("TrustlyFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ne.getMessage(), ne.getCause(), toId, null);
                    }
                }
            }
        }
    }



