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
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.decta.core.DectaSMSPaymentGateway;
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

/**
 * Created by Trupti on 5/19/2017.
 */
public class DCFrontendNotification extends PzServlet
{
    private static Logger log                           = new Logger(DCFrontendNotification.class.getName());
    private static TransactionLogger transactionLogger  = new TransactionLogger(DCFrontendNotification.class.getName());
    String ctoken                                       = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public DCFrontendNotification()
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
        log.debug("-------Enter in doService of DectaFrontendNotification---ip address----" + req.getHeader("X-Forwarded-For"));
        log.error("-------Enter in doService of DectaFrontendNotification---ip address----" + req.getHeader("X-Forwarded-For"));
        transactionLogger.debug("-------Enter in doService of DectaFrontendNotification-------" + req.getHeader("X-Forwarded-For"));
        res.setContentType("text/html");
        PrintWriter out         = res.getWriter();
        HttpSession session     = req.getSession(true);

        CommResponseVO commResponseVO   = new CommResponseVO();
        CommRequestVO commRequestVO     = new CommRequestVO();
        Functions functions             = new Functions();
        for (Object key : req.getParameterMap().keySet())
        {
            log.error("----for loop DectaFrontendNotification-----" + key + "=" + req.getParameter((String) key) + "--------------");
            transactionLogger.error("----for loop DectaFrontendNotification-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }
        ActionEntry entry           = new ActionEntry();
        AuditTrailVO auditTrailVO   = new AuditTrailVO();

        log.error("tracking id for DectaFrontendNotification---" + req.getParameter("merchant_transaction_id"));
        if (functions.isValueNull(req.getParameter("merchant_transaction_id")))
        {
            String trackingid = req.getParameter("merchant_transaction_id");

            Connection con      = null;
            PreparedStatement p = null;
            ResultSet rs        = null;

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
            String kStatus = "Failed";
            String billingDesc = "";
            String isTokenizationAllowed = "N";
            String isAddressDetailsRequired = "N";
            String tmpl_amt = "";
            String tmpl_currency = "";
            String token = "";
            String paymodeid = "";
            String cardtypeid= "";
            String firstName = "";
            String lastName = "";
            String redirectMethod = "";

            TransactionManager transactionManager       = new TransactionManager();
            CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
            GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
            GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
            MerchantDetailsVO merchantDetailsVO         = new MerchantDetailsVO();
            MerchantDAO merchantDAO                     = new MerchantDAO();

            try
            {
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);

                dbTrackingid    = transactionDetailsVO.getTrackingid();
                toid            = transactionDetailsVO.getToid();
                description     = transactionDetailsVO.getDescription();
                redirectUrl     = transactionDetailsVO.getRedirectURL();
                accountId       = transactionDetailsVO.getAccountId();
                orderDesc       = transactionDetailsVO.getOrderDescription();
                currency        = transactionDetailsVO.getCurrency();
                amount          = transactionDetailsVO.getAmount();
                dbStatus        = transactionDetailsVO.getStatus();
                tmpl_amt        = transactionDetailsVO.getTemplateamount();
                tmpl_currency   = transactionDetailsVO.getTemplatecurrency();
                paymodeid       = transactionDetailsVO.getPaymodeId();
                cardtypeid      = transactionDetailsVO.getCardTypeId();
                firstName       = transactionDetailsVO.getFirstName();
                lastName        = transactionDetailsVO.getLastName();
                redirectMethod  = transactionDetailsVO.getRedirectMethod();

                con = Database.getConnection();

                String query    = "select m.clkey,checksumalgo,m.autoredirect,m.partnerid,logoName,m.isPoweredBy,partnerName,m.isService,m.isTokenizationAllowed,m.isAddrDetailsRequired FROM members AS m,partners AS p WHERE m.partnerId=p.partnerId AND m.memberid=?";
                p               = con.prepareStatement(query);
                p.setString(1, toid);
                rs = p.executeQuery();
                log.error("selected toid from trackingid---" + p);
                rs = p.executeQuery();
                if (rs.next())
                {
                    clkey           = rs.getString("clkey");
                    checksumAlgo    = rs.getString("checksumalgo");
                    autoredirect    = rs.getString("autoredirect");
                    isPowerBy       = rs.getString("isPoweredBy");
                    logoName        = rs.getString("logoName");
                    partnerName     = rs.getString("partnerName");
                    partnerId       = rs.getString("partnerId");
                    isService                   = rs.getString("isService");
                    isTokenizationAllowed       = rs.getString("isTokenizationAllowed");
                    isAddressDetailsRequired    = rs.getString("isAddrDetailsRequired");
                }


                DectaSMSPaymentGateway rsPg                         = new DectaSMSPaymentGateway(accountId);
                CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();
                commTransactionDetailsVO.setPreviousTransactionId(trackingid);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);

                commResponseVO  = (CommResponseVO) rsPg.processInquiry(commRequestVO);

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                GatewayAccount account  = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                String pgtypeid         = account.getPgTypeId();
                GatewayType gatewayType = GatewayTypeService.getGatewayType(pgtypeid);
                String bankIP           = gatewayType.getBank_ipaddress();

                log.debug("bank ip address in DectabackendNotification---" + bankIP);
                transactionLogger.debug("bank ip address in DectabackendNotification---" + bankIP);

                status = commResponseVO.getStatus();

                if ("Success".equalsIgnoreCase(status) || "HoldOk".equalsIgnoreCase(status))
                {
                    kStatus         = "Successful";
                    billingDesc     = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    commResponseVO.setDescriptor(billingDesc);
                    if ("Y".equals(isTokenizationAllowed))
                    {
                        TokenManager tokenManager   = new TokenManager();
                        String cardNumber           = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                        String strToken             = tokenManager.isCardAvailable(toid, cardNumber);
                        if (functions.isValueNull(strToken))
                        {
                            token = strToken;
                        }
                        else
                        {
                            token = tokenManager.createTokenByTrackingId(trackingid, transactionDetailsVO);
                        }
                    }
                }

                checksumNew         = Checksum.generateChecksumForStandardKit(trackingid, description, String.valueOf(amount), kStatus, clkey);
                String cardtype     = Functions.getCardType(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));

                commonValidatorVO.setTrackingid(trackingid);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                String respstatus = status;

                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("Customer3D");
                log.error("DectaFrontendNotification Status Before Condition---" + dbStatus);

                if (PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus))
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append("update transaction_common set ");
                    if ("Success".equalsIgnoreCase(status) || "HoldOk".equalsIgnoreCase(status))
                    {
                        if ("Y".equalsIgnoreCase(isService) && (!functions.isValueNull(transactionDetailsVO.getTransactionType()) || "DB".equalsIgnoreCase(transactionDetailsVO.getTransactionType())))
                        {
                            respstatus = "Successful Transaction";
                            log.debug("---status in DectaFrontendNotification---" + respstatus);
                            sb.append(" captureamount='" + amount + "'");
                            sb.append(", paymentid='" + commResponseVO.getTransactionId() + "'");
                            sb.append(", remark='" + commResponseVO.getRemark() + "'");
                            sb.append(", status='capturesuccess'");
                            sb.append(", cardtype='" + cardtype + "'");
                            entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                        }
                        else
                        {
                            respstatus = "Authorization Successful";
                            log.debug("---status in DectaFrontendNotification---" + respstatus);
                            sb.append(" amount='" + amount + "'");
                            sb.append(", paymentid='" + commResponseVO.getTransactionId() + "'");
                            sb.append(", remark='" + commResponseVO.getRemark() + "'");
                            sb.append(", status='authsuccessful'");
                            sb.append(", cardtype='" + cardtype + "'");
                            entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                        }

                    }
                    else
                    {
                        respstatus = "Failed";
                        log.debug("---status in DectaFrontendNotification---" + respstatus);
                        sb.append(" amount='" + amount + "'");
                        sb.append(", paymentid='" + commResponseVO.getTransactionId() + "'");
                        sb.append(", remark='" + commResponseVO.getRemark() + "'");
                        sb.append(", status='authfailed'");
                        sb.append(", cardtype='" + cardtype + "'");
                        entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.ACTION_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                    }
                    sb.append(" where trackingid = " + trackingid);
                    log.debug("common update query DectaFrontendNotification---" + sb.toString());
                    Database.executeUpdate(sb.toString(), con);
                }

                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), status, null, null);

                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                genericTransDetailsVO.setRedirectMethod(redirectMethod);

                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                addressDetailsVO.setFirstname(firstName);
                addressDetailsVO.setLastname(lastName);

                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);
                commonValidatorVO.setTrackingid(trackingid);

                merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                commonValidatorVO.setToken(token);

                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                log.error("----autoredirect----" + autoredirect + "---redirecturl----" + redirectUrl);
                log.error("DectaFrontendNotification values------" + description + "-" + checksumNew + "-" + amount + "-" + trackingid + "-" + kStatus);

                if ("Y".equalsIgnoreCase(autoredirect))
                {
                    TransactionUtility transactionUtility = new TransactionUtility();
                    transactionUtility.doAutoRedirect(commonValidatorVO,res,kStatus,billingDesc);
                }
                else
                {
                    log.error("inside else autoredirect----" + autoredirect);

                    session.setAttribute("ctoken", ctoken);
                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", kStatus);
                    req.setAttribute("displayName", billingDesc);
                    req.setAttribute("remark", commResponseVO.getRemark());
                    String confirmationPage = "";
                    String version = (String)session.getAttribute("version");
                    if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";
                    session.invalidate();
                    RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(req, res);
                }
            }
            catch (PZTechnicalViolationException e)
            {
                log.error("error:::", e);
                transactionLogger.error("error:::", e);
                PZExceptionHandler.handleTechicalCVEException(e, toid, null);
            }
            catch (PZDBViolationException dbe)
            {
                log.error("error:::", dbe);
                transactionLogger.error("error:::", dbe);
                PZExceptionHandler.handleDBCVEException(dbe, toid, null);
            }
            catch (SystemError se)
            {
                log.error("error:::", se);
                transactionLogger.error("error:::", se);
                PZExceptionHandler.raiseAndHandleDBViolationException("DCFrontendNotification.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toid, null);
            }
            catch (SQLException e)
            {
                log.error("error:::", e);
                transactionLogger.error("error:::", e);
                PZExceptionHandler.raiseAndHandleDBViolationException("DCFrontendNotification.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toid, null);
            }
            catch (NoSuchAlgorithmException ne)
            {
                log.error("error:::", ne);
                transactionLogger.error("error:::", ne);
                PZExceptionHandler.raiseAndHandleTechnicalViolationException("DCFrontendNotification.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ne.getMessage(), ne.getCause(), toid, null);
            }
            finally
            {
                Database.closePreparedStatement(p);
                Database.closeResultSet(rs);
                Database.closeConnection(con);
            }
        }

    }
}