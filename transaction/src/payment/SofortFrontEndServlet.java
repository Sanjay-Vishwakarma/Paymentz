package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
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
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.reference.DefaultEncoder;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 29/1/15
 * Time: 8:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class SofortFrontEndServlet   extends PzServlet
{
    public SofortFrontEndServlet()
    {
        super();
    }
    private static Logger log = new Logger(SofortFrontEndServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SofortFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    Connection con = null;

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
        log.debug("-------Enter in doService of SofortFrontEndServlet-------");
        transactionLogger.debug("-------Enter in doService of SofortFrontEndServlet-------");
        ServletContext ctx = getServletContext();
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession(true);
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        if (!req.getParameterMap().isEmpty())
        {
            TransactionUtility transactionUtility = new TransactionUtility();
            TransactionManager transactionManager=new TransactionManager();
            MerchantDAO merchantDAO= new MerchantDAO();
            MerchantDetailsVO merchantDetailsVO= new MerchantDetailsVO();
            Functions functions= new Functions();

            String trackingId = ESAPI.encoder().encodeForSQL(me,req.getParameter("trackingId"));
            String status = ESAPI.encoder().encodeForSQL(me,req.getParameter("status"));

            String toid = "";
            String description = "";
            String redirectUrl = "";
            String accountId = "";
            String orderDesc = "";
            String autoredirect="";
            String displayName = "";
            String isPowerBy = "";
            String logoName = "";
            String partnerName = "";
            String tmpl_amount="";
            String tmpl_currency="";
            String paymodeid="";
            String cardtypeid="";
            String email="";
            String customerId="";
            String firstName="";
            String lastName="";
            String message="";
            String amount="";
            String currency="";

            try
            {

                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                toid = transactionDetailsVO.getToid();
                amount = transactionDetailsVO.getAmount();
                tmpl_amount = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                accountId = transactionDetailsVO.getAccountId();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                    orderDesc = transactionDetailsVO.getOrderDescription();
                currency = transactionDetailsVO.getCurrency();
                paymodeid = transactionDetailsVO.getPaymodeId();
                cardtypeid = transactionDetailsVO.getCardTypeId();
                email = transactionDetailsVO.getEmailaddr();
                customerId = transactionDetailsVO.getCustomerId();
                firstName = transactionDetailsVO.getFirstName();
                lastName = transactionDetailsVO.getLastName();

                displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                if (merchantDetailsVO != null)
                {
                    autoredirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                }




                if ("Success".equalsIgnoreCase(status))
                {
                    message = "Successful";
                }
                else if ("Abort".equalsIgnoreCase(status))
                {
                    message = "Transaction Aborted (Failed)";
                }
                else if ("TimeOut".equalsIgnoreCase(status))
                {
                    message = "Transaction TimeOut";
                }
                else
                {
                    message = "Transaction Declined";
                }

                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, null, null);

                CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
                GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
                GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();


                String billingDesc = "";
                String responceStatus="";
                if (status != null && status.contains("Successful") || status.contains("success"))
                {
                    responceStatus = "Successful";
                    billingDesc = displayName;
                }
                else
                {
                    responceStatus="failed";
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


            } catch (PZDBViolationException tve)
            {
                transactionLogger.error("PZDBViolationException:::::", tve);
            }
            catch (SystemError e)
            {
                transactionLogger.error("SystemError:::::", e);
                PZExceptionHandler.raiseAndHandleDBViolationException("SofortFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
            }
        }
    }
}
