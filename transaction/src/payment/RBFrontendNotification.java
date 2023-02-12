package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TokenManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.ReitumuBank.core.ReitumuBankSMSPaymentGateway;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 2/11/15
 * Time: 7:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class RBFrontendNotification extends PzServlet
{
    public RBFrontendNotification()
    {
        super();
    }
    private static Logger log = new Logger(RBFrontendNotification.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(RBFrontendNotification.class.getName());
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
        log.debug("-------Enter in doService of RBFrontendNotification---ip address----"+req.getHeader("X-Forwarded-For"));
        log.error("-------Enter in doService of RBFrontendNotification---ip address----"+req.getHeader("X-Forwarded-For"));
        transactionLogger.debug("-------Enter in doService of RBFrontendNotification-------"+req.getHeader("X-Forwarded-For"));
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession(true);

        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = new CommRequestVO();
        for(Object key : req.getParameterMap().keySet())
        {
            log.error("----for loop RBFrontendNotification-----" + key + "=" + req.getParameter((String) key) + "--------------");
            transactionLogger.error("----for loop RBFrontendNotification-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }
        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO=new AuditTrailVO();

        log.error("tracking id for RBFrontEnd---"+req.getParameter("merchant_transaction_id"));
        String trackingid = req.getParameter("merchant_transaction_id");
        Connection con = null;
        PreparedStatement p=null;
        ResultSet rs = null;
        String toid = "";
        String description = "";
        String redirectUrl = "";
        String accountId = "";
        String orderDesc = "";
        String currency = "";
        String clkey = "";
        String checksumAlgo = "";
        String checksumNew = "";
        String autoredirect = "";
        String isPowerBy = "";
        String logoName = "";
        String partnerName = "";
        String partnerId = "";
        String amount = "";
        String status = "";
        String dbTrackingid = "";
        String dbStatus = "";
        String isService = "";
        String kStatus = "N";
        String billingDesc = "";
        String isTokenizationAllowed = "N";
        String isAddressDetailsRequired= "N";
        String token="";
        String template_amount = "";
        String template_currency = "";

        TransactionManager transactionManager=new TransactionManager();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();
        Functions functions =new Functions();

        try
        {
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);

            dbTrackingid = transactionDetailsVO.getTrackingid();
            toid = transactionDetailsVO.getToid();
            description = transactionDetailsVO.getDescription();
            redirectUrl = transactionDetailsVO.getRedirectURL();
            accountId = transactionDetailsVO.getAccountId();
            orderDesc = transactionDetailsVO.getOrderDescription();
            currency = transactionDetailsVO.getCurrency();
            amount = transactionDetailsVO.getAmount();
            dbStatus = transactionDetailsVO.getStatus();
            template_amount = transactionDetailsVO.getTemplateamount();
            template_currency = transactionDetailsVO.getTemplatecurrency();

            con = Database.getConnection();

            String query = "select m.clkey,checksumalgo,m.autoredirect,m.partnerid,logoName,m.isPoweredBy,partnerName,m.isService,m.isTokenizationAllowed,m.isAddrDetailsRequired FROM members AS m,partners AS p WHERE m.partnerId=p.partnerId AND m.memberid=?" ;
            p=con.prepareStatement(query);
            p.setString(1,toid);
            rs=p.executeQuery();
            log.error("selected toid from trackingid---"+p);
            rs = p.executeQuery();
            if (rs.next())
            {
                clkey = rs.getString("clkey");
                checksumAlgo = rs.getString("checksumalgo");
                autoredirect = rs.getString("autoredirect");
                isPowerBy = rs.getString("isPoweredBy");
                logoName = rs.getString("logoName");
                partnerName = rs.getString("partnerName");
                partnerId = rs.getString("partnerId");
                isService = rs.getString("isService");
                isTokenizationAllowed = rs.getString("isTokenizationAllowed");
                isAddressDetailsRequired = rs.getString("isAddrDetailsRequired");
            }


            ReitumuBankSMSPaymentGateway rsPg = new ReitumuBankSMSPaymentGateway(accountId);
            CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
            commTransactionDetailsVO.setPreviousTransactionId(trackingid);
            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
            commResponseVO = (CommResponseVO) rsPg.processInquiry(commRequestVO);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

            GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
            String pgtypeid = account.getPgTypeId();
            GatewayType gatewayType = GatewayTypeService.getGatewayType(pgtypeid);
            String bankIP = gatewayType.getBank_ipaddress();

            log.debug("bank ip address in RBBackendNotification---"+bankIP);
            transactionLogger.debug("bank ip address in RBBackendNotification---"+bankIP);

            status = commResponseVO.getStatus();

            if("Success".equalsIgnoreCase(status) || "HoldOk".equalsIgnoreCase(status))
            {
                kStatus = "Y";
                billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                commResponseVO.setDescriptor(billingDesc);
                log.debug("billing decs----"+billingDesc);
                if("Y".equals(isTokenizationAllowed))
                {
                    TokenManager tokenManager=new TokenManager();
                    String cardNumber= PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                    String strToken = tokenManager.isCardAvailable(toid,cardNumber);
                    if(functions.isValueNull(strToken))
                    {
                        token=strToken;
                    }
                    else
                    {
                        token=tokenManager.createTokenByTrackingId(trackingid,transactionDetailsVO);
                    }
                }
            }

            checksumNew = Checksum.generateChecksumForStandardKit(trackingid, description, String.valueOf(amount), kStatus, clkey);

            commonValidatorVO.setTrackingid(trackingid);
            genericTransDetailsVO.setOrderId(description);
            genericTransDetailsVO.setAmount(amount);
            genericTransDetailsVO.setCurrency(currency);
            genericTransDetailsVO.setOrderDesc(orderDesc);
            String respstatus = status;
            String cardtype = Functions.getCardType(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
            //System.out.println("card num----"+Functions.getCardType(Encryptor.decryptPAN(transactionDetailsVO.getCcnum())));
            //System.out.println("cardtype----"+cardtype);
            auditTrailVO.setActionExecutorId(toid);
            auditTrailVO.setActionExecutorName("Customer3D");
            log.error("RBFrontendNotification if check condition---"+bankIP);
            /*if(req.getHeader("X-Forwarded-For").equals(bankIP))
            {*/
            StringBuffer sb = new StringBuffer();
            sb.append("update transaction_common set ");

            if("authstarted".equalsIgnoreCase(dbStatus))
            {
                if("Success".equalsIgnoreCase(status) || "HoldOk".equalsIgnoreCase(status))
                {
                    if("Y".equalsIgnoreCase(isService) && (!functions.isValueNull(transactionDetailsVO.getTransactionType()) || "DB".equalsIgnoreCase(transactionDetailsVO.getTransactionType())))
                    {
                        respstatus = "Successful Transaction";
                        log.debug("---status in RBFrontendNotification---"+respstatus);
                        sb.append(" captureamount='" + amount + "'");
                        sb.append(", paymentid='" + commResponseVO.getResponseHashInfo() + "'");
                        sb.append(", remark='" + commResponseVO.getRemark() + "'");
                        sb.append(", status='capturesuccess'");
                        sb.append(", cardtype='" + cardtype +"'");
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO,auditTrailVO,null);
                    }
                    else
                    {
                        respstatus = "Authorization Successful";
                        log.debug("---status in RBFrontendNotification---"+respstatus);
                        sb.append(" amount='" + amount + "'");
                        sb.append(", paymentid='" + commResponseVO.getResponseHashInfo() + "'");
                        sb.append(", remark='" + commResponseVO.getRemark() + "'");
                        sb.append(", status='authsuccessful'");
                        sb.append(", cardtype='" + cardtype +"'");
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO,auditTrailVO,null);
                    }

                }
                else
                {
                    respstatus = "Failed";
                    log.debug("---status in RBFrontendNotification---"+respstatus);
                    sb.append(" amount='" + amount + "'");
                    sb.append(", paymentid='" + commResponseVO.getResponseHashInfo() + "'");
                    sb.append(", remark='" + commResponseVO.getRemark() + "'");
                    sb.append(", status='authfailed'");
                    sb.append(", cardtype='" + cardtype +"'");
                    entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.ACTION_AUTHORISTION_FAILED, commResponseVO,auditTrailVO,null);
                }
                sb.append(" where trackingid = "+trackingid);
                log.debug("common update query RBFrontendNotification---"+sb.toString());
                Database.executeUpdate(sb.toString(),con);
            }

            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), status, null,null);

            Map saleMap = new TreeMap();

            saleMap.put("desc",description);
            saleMap.put("checksum",checksumNew);
            saleMap.put("amount",amount);
            saleMap.put("trackingid",trackingid);
            saleMap.put("status",kStatus);
            saleMap.put("descriptor",billingDesc);
            saleMap.put("token",token);
            saleMap.put("tmpl_amt",template_amount);
            saleMap.put("tmpl_currency",template_currency);

            log.error("----autoredirect----" + autoredirect + "---redirecturl----" + redirectUrl);
            log.error("RBFEndNotification values------" + description + "-" + checksumNew + "-" + amount + "-" + trackingid + "-" + kStatus);

            if("Y".equalsIgnoreCase(autoredirect))
            {
                log.error("inside autoredirect----"+autoredirect);
                String redirect = generateAutoSubmitForm(redirectUrl,saleMap);
                res.setContentType("text/html;charset=UTF-8");
                res.setCharacterEncoding("UTF-8");
                //System.out.println("redirect---"+redirect);
                try
                {
                    res.getWriter().write(redirect);
                }
                catch (IOException e)
                {
                    log.error("IO Exception in InPayFrontEndServlet---",e);
                    transactionLogger.error("IO Exception in InPayFrontEndServlet---",e);
                }
            }

            else
            {
                log.error("inside else autoredirect----"+autoredirect);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                //merchantDetailsVO.setPoweredBy(isPowerBy);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                //commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                addressDetailsVO.setTmpl_amount(template_amount);
                addressDetailsVO.setTmpl_currency(template_currency);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setToken(token);

                session.setAttribute("ctoken",ctoken);
                req.setAttribute("transDetail", commonValidatorVO);
                req.setAttribute("responceStatus", respstatus);
                req.setAttribute("displayName", commResponseVO.getDescriptor());
                String confirmationPage = "";
                String version = (String)session.getAttribute("version");
                if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                    confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                else
                    confirmationPage = "/confirmationpage.jsp?ctoken=";
                session.invalidate();
                RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                rd.forward(req,res);
            }

        }
        catch (PZTechnicalViolationException e)
        {
            log.error("error:::",e);
            transactionLogger.error("error:::",e);
            PZExceptionHandler.handleTechicalCVEException(e,toid,null);
        }
        catch (PZDBViolationException dbe)
        {
            log.error("error:::",dbe);
            transactionLogger.error("error:::",dbe);
            PZExceptionHandler.handleDBCVEException(dbe,toid,null);
        }
        catch (SystemError se)
        {
            log.error("error:::",se);
            transactionLogger.error("error:::",se);
            PZExceptionHandler.raiseAndHandleDBViolationException("RBFrontendNotification.java","doService()",null,"Transaction",null, PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause(),toid,null);
        }
        catch (SQLException e)
        {
            log.error("error:::",e);
            transactionLogger.error("error:::",e);
            PZExceptionHandler.raiseAndHandleDBViolationException("RBFrontendNotification.java","doService()",null,"Transaction",null, PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause(),toid,null);
        }
        catch (NoSuchAlgorithmException ne)
        {
            log.error("error:::",ne);
            transactionLogger.error("error:::",ne);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("RBFrontendNotification.java","doService()",null,"Transaction",null, PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,ne.getMessage(),ne.getCause(),toid,null);
        }
        finally
        {
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
    }

    private static String generateAutoSubmitForm(String actionUrl, Map<String, String> paramMap)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(actionUrl).append("\" method=\"post\">\n");

        for (String key : paramMap.keySet()) {
            html.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + paramMap.get(key) + "\">\n");
        }
        html.append("</form>\n");
        html.append("<script language=\"javascript\">");
        html.append("document.pay_form.submit();");
        html.append("</script>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }

}