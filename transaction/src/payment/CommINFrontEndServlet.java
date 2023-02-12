package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.MerchantConfigManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by Admin on 8/9/2017.
 */
public class CommINFrontEndServlet extends PzServlet
{
    private static Logger log = new Logger(CommINFrontEndServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(CommINFrontEndServlet.class.getName());
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
        //System.out.println("-------Enter in doService of BDFrontEndServlet---ip address----" + req.getHeader("X-Forwarded-For"));
        log.error("-------Enter in doService of BDFrontEndServlet---ip address----" + req.getHeader("X-Forwarded-For"));
        transactionLogger.debug("-------Enter in doService of BDFrontEndServlet-------" + req.getHeader("X-Forwarded-For"));
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession(true);

        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO commRequestVO = new CommRequestVO();
        for (Object key : req.getParameterMap().keySet())
        {
            log.error("----for loop BDFrontEndServlet-----" + key + "=" + req.getParameter((String) key) + "--------------");
            transactionLogger.error("----for loop BDFrontEndServlet-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }

        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO=new AuditTrailVO();

        String bdResponse = req.getParameter("msg");
        String hidRequestId = req.getParameter("hidRequestId");//PGIBL1000
        String hidOperation = req.getParameter("hidOperation");//B101

        transactionLogger.debug("bdResponse---"+bdResponse);

        String sData[] = bdResponse.split("\\|");

        String trackingid = sData[1];
        String paymentId = sData[2];
        String bankRefNo = sData[3];
        String bankId = sData[5];
        String errorCode = sData[14];

        transactionLogger.debug("trackingid---"+trackingid);
        transactionLogger.debug("paymentId---"+paymentId);
        transactionLogger.debug("bankRefNo---"+bankRefNo);
        transactionLogger.debug("bankId---"+bankId);
        transactionLogger.debug("errorCode---"+errorCode);

        TransactionManager transactionManager=new TransactionManager();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO= new GenericCardDetailsVO();
        TransactionUtility transactionUtility=new TransactionUtility();
        Connection con = null;
        PreparedStatement p=null;
        ResultSet rs =null;

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
        String tmpl_currency = "";
        String tmpl_amount = "";
        String paymodeid = "";
        String cardtypeid = "";
        String firstName = "";
        String lastName = "";
        String customerId = "";
        String cardno = "";
        String responseStatus = "";
        String notificationUrl = "";
        String expMonth = "";
        String expYear = "";
        String RedirectMethod = "";
        Functions functions = new Functions();

        try
        {
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);

            toid            = transactionDetailsVO.getToid();
            description     = transactionDetailsVO.getDescription();
            redirectUrl     = transactionDetailsVO.getRedirectURL();
            notificationUrl = transactionDetailsVO.getNotificationUrl();
            accountId       = transactionDetailsVO.getAccountId();
            orderDesc       = transactionDetailsVO.getOrderDescription();
            currency        = transactionDetailsVO.getCurrency();
            amount          = transactionDetailsVO.getAmount();
            dbStatus        = transactionDetailsVO.getStatus();
            tmpl_amount     = transactionDetailsVO.getTemplateamount();
            tmpl_currency   = transactionDetailsVO.getTemplatecurrency();
            paymodeid       = transactionDetailsVO.getPaymodeId();
            cardtypeid      = transactionDetailsVO.getCardTypeId();
            firstName       = transactionDetailsVO.getFirstName();
            lastName        = transactionDetailsVO.getLastName();
            customerId      = transactionDetailsVO.getCustomerId();
            RedirectMethod  = transactionDetailsVO.getRedirectMethod();

            if(functions.isValueNull(transactionDetailsVO.getCcnum()))
                cardno = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
            con = Database.getConnection();

            MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
            merchantDetailsVO                           = merchantConfigManager.getMerchantDetailFromToId(toid);
            if (merchantDetailsVO != null)
            {
                //clKey = merchantDetailsVO.getKey();
                autoredirect    = merchantDetailsVO.getAutoRedirect();
                logoName        = merchantDetailsVO.getLogoName();
                partnerName     = merchantDetailsVO.getPartnerName();
                isService       = merchantDetailsVO.getIsService();
            }


            commonValidatorVO.setTrackingid(trackingid);
            genericTransDetailsVO.setOrderId(description);
            genericTransDetailsVO.setAmount(amount);
            genericTransDetailsVO.setCurrency(currency);
            genericTransDetailsVO.setOrderDesc(orderDesc);
            String respstatus = status;

            auditTrailVO.setActionExecutorId(toid);
            auditTrailVO.setActionExecutorName("Customer3D");

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setTransactionId(paymentId);
            commResponseVO.setErrorCode(errorCode);
            commResponseVO.setTransactionType("sale");

            StringBuffer sb = new StringBuffer();
            sb.append("update transaction_common set ");

            if("authstarted".equalsIgnoreCase(dbStatus))
            {
                if(errorCode.equals("0300"))
                {
                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    if("Y".equalsIgnoreCase(isService) && (!functions.isValueNull(transactionDetailsVO.getTransactionType()) || "DB".equalsIgnoreCase(transactionDetailsVO.getTransactionType())))
                    {
                        dbStatus        = "capturesuccess";
                        respstatus      = "Successful Transaction";
                        responseStatus  = "Successful";
                        log.debug("---status in BDFrontEndServlet---" + respstatus);
                        sb.append(" captureamount='" + amount + "'");
                        sb.append(", paymentid='" + paymentId + "'");
                        sb.append(", remark='" + respstatus + "'");
                        sb.append(", status='capturesuccess'");
                        commResponseVO.setRemark(respstatus);
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(billingDesc);
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO,auditTrailVO,null);
                    }
                    else
                    {
                        dbStatus        = "authsuccessful";
                        responseStatus  = "Successful";
                        respstatus      = "Authorization Successful";
                        log.debug("---status in BDFrontEndServlet---" + respstatus);
                        sb.append(" amount='" + amount + "'");
                        sb.append(", paymentid='" + paymentId + "'");
                        sb.append(", remark='" + respstatus + "'");
                        sb.append(", status='authsuccessful'");
                        commResponseVO.setRemark(respstatus);
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(billingDesc);
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO,auditTrailVO,null);
                    }

                }
                else
                {
                    dbStatus        = "authfailed";
                    respstatus      = "Failed";
                    responseStatus  = "Failed";
                    log.debug("---status in BDFrontEndServlet---" + respstatus);
                    sb.append(" amount='" + amount + "'");
                    sb.append(", paymentid='" + paymentId + "'");
                    sb.append(", remark='" + respstatus + "'");
                    sb.append(", status='authfailed'");
                    commResponseVO.setRemark(respstatus);
                    commResponseVO.setStatus("fail");
                    entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.ACTION_AUTHORISTION_FAILED, commResponseVO,auditTrailVO,null);
                }
                sb.append(" where trackingid = "+trackingid);
                log.debug("common update query BDFrontEndServlet---"+sb.toString());
                Database.executeUpdate(sb.toString(),con);
                statusSyncDAO.updateAllTransactionFlowFlag(trackingid, dbStatus);
            }

            /*AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), status, null,null);
*/
            AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), responseStatus, respstatus, billingDesc);

            AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), responseStatus, respstatus, billingDesc);


            genericTransDetailsVO.setNotificationUrl(notificationUrl);
            genericTransDetailsVO.setOrderId(description);
            genericTransDetailsVO.setAmount(amount);
            genericTransDetailsVO.setCurrency(currency);
            genericTransDetailsVO.setOrderDesc(orderDesc);
            genericTransDetailsVO.setRedirectUrl(redirectUrl);
            genericTransDetailsVO.setBillingDiscriptor(billingDesc);
            genericTransDetailsVO.setRedirectMethod(RedirectMethod);

            addressDetailsVO.setTmpl_amount(tmpl_amount);
            addressDetailsVO.setTmpl_currency(tmpl_currency);
            if (functions.isValueNull(firstName))
                addressDetailsVO.setFirstname(firstName);

            if (functions.isValueNull(lastName))
                addressDetailsVO.setLastname(lastName);

            commonValidatorVO.setLogoName(logoName);
            commonValidatorVO.setPartnerName(partnerName);
            commonValidatorVO.setPaymentType(paymodeid);
            commonValidatorVO.setCardType(cardtypeid);
            cardDetailsVO.setCardNum(cardno);

            if(functions.isValueNull(customerId))
                commonValidatorVO.setCustomerId(customerId);
            else
                commonValidatorVO.setCustomerId(transactionDetailsVO.getCustomerId() );
            commonValidatorVO.setTrackingid(trackingid);
            if (functions.isValueNull(transactionDetailsVO.getExpdate()))
            {


                String expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());

                String temp[] = expDate.split("/");

                if (functions.isValueNull(temp[0]))
                {
                    expMonth = temp[0];
                }
                if (functions.isValueNull(temp[1]))
                {
                    expYear = temp[1];
                }
            }


            cardDetailsVO.setExpMonth(expMonth);
            cardDetailsVO.setExpYear(expYear);
            commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
            commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            commonValidatorVO.setCardDetailsVO(cardDetailsVO);
            transactionUtility.setToken(commonValidatorVO, dbStatus);
            log.error("----autoredirect----" + autoredirect + "---redirecturl----" + redirectUrl);
            log.error("RBFEndNotification values------" + description + "-" + checksumNew + "-" + amount + "-" + trackingid + "-" + kStatus);

            transactionLogger.error("notification---" + notificationUrl);
            if (functions.isValueNull(notificationUrl))
            {
                transactionLogger.error("inside sending notification---" + notificationUrl);
                TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                transactionLogger.error("inside sending notification transactionDetailsVO1---" + transactionDetailsVO1);
                transactionDetailsVO1.setTransactionMode("3D");
                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(transactionDetailsVO1, trackingid, dbStatus, respstatus, "");
            }
            if("Y".equalsIgnoreCase(autoredirect))
            {
                log.error("inside autoredirect----"+autoredirect);
                transactionUtility.doAutoRedirect(commonValidatorVO,res,responseStatus,billingDesc);
            }
            else
            {
                log.error("inside else autoredirect----"+autoredirect);
                req.setAttribute("responceStatus", respstatus);
                req.setAttribute("displayName", billingDesc);
                req.setAttribute("remark", respstatus);
                req.setAttribute("transDetail", commonValidatorVO);
                session.setAttribute("ctoken", ctoken);

                String confirmationPage = "";
//                String version = (String)session.getAttribute("version");
//                if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                    confirmationPage = "/confirmationCheckout.jsp?ctoken=";
//                else
//                    confirmationPage = "/confirmationpage.jsp?ctoken=";


                RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                rd.forward(req, res);
                session.invalidate();
            }

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
            PZExceptionHandler.raiseAndHandleDBViolationException("CommINFrontEndServlet.java","doService()",null,"Transaction",null, PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause(),toid,null);
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