package payment;

import com.directi.pg.*;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 8/30/14
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class InPayFrontEndServlet extends PzServlet
{
    private final static String URL = "https://test-secure.inpay.com";
    private static TransactionLogger transactionLogger = new TransactionLogger(InPayFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public InPayFrontEndServlet()
    {
        super();
    }

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
        transactionLogger.debug("-------Enter in doService of InPayFrontEndServlet-------");
        res.setContentType("text/html");
        HttpSession session = req.getSession(true);

        for(Object key : req.getParameterMap().keySet())
        {
            transactionLogger.debug("---------"+key+"="+req.getParameter((String)key)+"--------------");
        }

        if (!req.getParameterMap().isEmpty())
        {
            String currency = req.getParameter("inpay_transfer_currency");
            String captureAmount = req.getParameter("inpay_transfer_amount");
            String paymentId = req.getParameter("inpay_invoice_reference");
            //String checkSum = "";
            String amount = req.getParameter("inpay_transfer_amount");
            String trackingId = req.getParameter("inpay_order_id");
            //String resTime = req.getParameter("invoice_created_at");
            String status = req.getParameter("inpay_invoice_status");

            String toid = "";
            String description = "";
            String redirectUrl = "";
            String orderDesc = "";
            String autoredirect = "";
            String displayName = "";
            String isPowerBy = "";
            String logoName = "";
            String partnerName = "";
            String tmpl_amount = "";
            String tmpl_currency = "";
            String paymodeid = "";
            String cardtypeid = "";
            String email = "";
            String customerId="";
            String firstName="";
            String lastName="";
            String message="";
            String responceStatus="fail";

            try
            {
                CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
                MerchantDAO merchantDAO= new MerchantDAO();
                GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
                MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
                GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
                TransactionUtility transactionUtility = new TransactionUtility();
                TransactionManager transactionManager = new TransactionManager();
                Functions functions = new Functions();
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                toid = transactionDetailsVO.getToid();
                amount = transactionDetailsVO.getAmount();
                tmpl_amount = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                    orderDesc = transactionDetailsVO.getOrderDescription();
                currency = transactionDetailsVO.getCurrency();
                paymodeid = transactionDetailsVO.getPaymodeId();
                cardtypeid = transactionDetailsVO.getCardTypeId();
                email = transactionDetailsVO.getEmailaddr();
                customerId=transactionDetailsVO.getCustomerId();
                firstName=transactionDetailsVO.getFirstName();
                lastName=transactionDetailsVO.getLastName();


                merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                if (merchantDetailsVO != null)
                {
                    autoredirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                }


                String billingDesc = "";
                if (status != null && status.contains("Successful") || status.contains("success"))
                {
                    responceStatus = "Successful";
                    billingDesc = displayName;
                }

                commonValidatorVO.setTrackingid(trackingId);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);

                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);
                if (functions.isValueNull(email))
                    addressDetailsVO.setEmail(email);
                if (functions.isValueNull(firstName))
                    addressDetailsVO.setFirstname(firstName);

                if (functions.isValueNull(lastName))
                    addressDetailsVO.setLastname(lastName);
                addressDetailsVO.setTmpl_amount(tmpl_amount);
                addressDetailsVO.setTmpl_currency(tmpl_currency);

                commonValidatorVO.setCustomerId(customerId);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);

                if ("Y".equalsIgnoreCase(autoredirect))
                {
                    transactionUtility.doAutoRedirect(commonValidatorVO, res, responceStatus, billingDesc);
                }
                else
                {
                    req.setAttribute("responceStatus", responceStatus);
                    req.setAttribute("displayName", billingDesc);
                    req.setAttribute("remark", message);
                    req.setAttribute("transDetail", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);

                    String confirmationPage = "";
                    String version = (String) session.getAttribute("version");
                    if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";
                    RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(req, res);
                    session.invalidate();
                }
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, null, null);
            }
            catch (PZDBViolationException tve)
            {
                transactionLogger.error("PZDBViolationException:::::", tve);
            }
            catch (SystemError e)
            {
                transactionLogger.error("SystemError:::::", e);
                PZExceptionHandler.raiseAndHandleDBViolationException("InPayFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
            }
        }
    }
}
