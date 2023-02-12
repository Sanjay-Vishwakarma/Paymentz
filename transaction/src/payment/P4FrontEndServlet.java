package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PaymentManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.errors.TransactionError;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.p4.gateway.P4Utils;
import com.payment.p4.vos.p4MainVo.Request;
import com.payment.p4.vos.p4MainVo.Response;
import com.payment.p4.vos.queryBlock.Query;
import com.payment.p4.vos.queryBlock.resultSetBlock.ResultSet;
import com.payment.p4.vos.queryBlock.resultSetBlock.resultBlock.Result;
import com.payment.p4.vos.transactionBlock.Transaction;
import com.payment.p4.vos.transactionBlock.eventsBlock.Events;
import com.payment.p4.vos.transactionBlock.eventsBlock.eventBlock.Event;
import com.payment.p4.vos.transactionBlock.identificationBlock.Identification;
import com.payment.p4.vos.transactionBlock.paymentBlock.Payment;
import com.payment.p4.vos.transactionBlock.paymentBlock.clearingBlock.Clearing;
import com.payment.p4.vos.transactionBlock.processingBlock.Processing;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 29/1/15
 * Time: 8:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class P4FrontEndServlet extends PzServlet
{
    private static Logger log = new Logger(P4FrontEndServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(P4FrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    Connection con = null;
   private Functions functions = new Functions();
    public P4FrontEndServlet()
    {
        super();
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
        log.debug("DATA--->"+html);
        return html.toString();
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

        String transactionId=req.getParameter("transactionId");
        String accountId=req.getParameter("accountId");

        //Query
        CommRequestVO commRequestVO = new CommRequestVO();
        P4Utils p4Utils = new P4Utils();

        HttpSession session = req.getSession(true);
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        if (!req.getParameterMap().isEmpty())
        {
            Hashtable error=new Hashtable();
            Hashtable otherdetails = new Hashtable();
            TransactionUtility transactionUtility = new TransactionUtility();
            TransactionError transactionError = new TransactionError();
            ActionEntry entry = new ActionEntry();
            PaymentManager paymentManager = new PaymentManager();
            transactionLogger.debug("-------after paymentManager-------");
            Merchants merchants = new Merchants();
            transactionLogger.debug("-------after merchants-------");

            AuditTrailVO auditTrailVO = new AuditTrailVO();
            //String trackingId = ESAPI.encoder().encodeForSQL(me,req.getParameter("trackingId"));
            String status = "";
            PZTransactionStatus pzTransactionStatus =null;

            String paymentId=null;
            String currency=null;
            String amount=null;
            String toid = "";
            String description = "";
            String redirectUrl = "";
            accountId = "";
            String orderDesc = "";
            String currencyDb = "";
            String clkey = "";
            String checksumAlgo = "";
            String checksumNew = "";
            String autoredirect="";
            String displayName = "";
            String isPowerBy = "";
            String logoName = "";
            String partnerName = "";
            String partnerId = "";

            Hashtable transactionDetails = null;
            try
            {
                transactionDetails = paymentManager.getTransactionDetailsForCommon(transactionId);
            }
            catch(PZDBViolationException s)
            {
                log.error("SQL Exception in P4FrontEndServlet---",s);
                transactionLogger.error("SQL Exception in P4FrontEndServlet---",s);
                PZExceptionHandler.handleDBCVEException(s, toid, null);
            }
            if(transactionDetails!=null && !transactionDetails.isEmpty())
            {
                accountId = (String) transactionDetails.get("accountid");
                paymentId = (String) transactionDetails.get("paymentid");
                amount = (String) transactionDetails.get("amount");
                currency = (String) transactionDetails.get("currency");
                toid = (String) transactionDetails.get("toid");
                description = (String) transactionDetails.get("description");
                orderDesc = (String) transactionDetails.get("orderdescription");
                redirectUrl =(String) transactionDetails.get("redirecturl");

                displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                Request request=p4Utils.getOnlineBankRequestForQuery(commRequestVO, transactionId, paymentId, accountId);

                Response response=p4Utils.sendRequestForOnlineBankTransferQuery(request);

                CommResponseVO commResponseVO=new CommResponseVO();


                if(response!=null)
                {

                    if (response.getQuery() != null)
                    {
                        Query query = response.getQuery();

                        if (query.getResultSet() != null)
                        {
                            ResultSet resultSet = query.getResultSet();
                            if (resultSet.getResult() != null)
                            {
                                Result result = resultSet.getResult();
                                if (result.getTransaction() != null)
                                {
                                    Transaction transaction = result.getTransaction();
                                    if (transaction.getProcessing() != null)
                                    {
                                        Processing processing = transaction.getProcessing();

                                        commResponseVO.setTransactionStatus(processing.getStatus());
                                        commResponseVO.setDescription("NEW(" + processing.getStatus() + ")");
                                        commResponseVO.setDescriptor(displayName);
                                        commResponseVO.setTransactionType(PZProcessType.CAPTURE.name());
                                        if ("TRANSMITTED".equalsIgnoreCase(processing.getStatus()))
                                        {
                                            status = "success";
                                            pzTransactionStatus=PZTransactionStatus.CAPTURE_SUCCESS;
                                        }
                                        else
                                        {
                                            status = "fail";
                                            pzTransactionStatus=PZTransactionStatus.CAPTURE_FAILED;
                                        }
                                    }
                                    if(transaction.getPayment()!=null && transaction.getPayment().size()>0 && transaction.getPayment().get(0)!=null)
                                    {
                                        Payment payment = transaction.getPayment().get(0);
                                        if(payment.getCleared()!=null)
                                        {
                                            Clearing cleared=payment.getCleared();
                                            commResponseVO.setAmount(cleared.getAmount());
                                        }
                                    }
                                    if (transaction.getEvents() != null)
                                    {
                                        Events events = transaction.getEvents();
                                        if (events.getEvent() != null && events.getEvent().size() > 0 && events.getEvent().get(0) != null)
                                        {
                                            Event event = events.getEvent().get(0);
                                            if (event.getIdentification() != null)
                                            {
                                                Identification identification = event.getIdentification();
                                                commResponseVO.setTransactionId(identification.getUniqueID());
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }

                MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
                MerchantDAO merchantDAO = new MerchantDAO();
                try
                {
                    paymentManager.updateTransactionForCommon(commResponseVO,pzTransactionStatus.toString(),transactionId,null,"transaction_common",null,paymentId,null,functions.isValueNull(commResponseVO.getRemark())?commResponseVO.getRemark():"");
                    merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                }
                catch(PZDBViolationException s)
                {
                    log.error("SQL Exception in P4FrontEndServlet---",s);
                    transactionLogger.error("SQL Exception in P4FrontEndServlet---",s);
                    PZExceptionHandler.handleDBCVEException(s, toid, null);
                }
                Hashtable memberDetails = merchants.getMemberPartnerDetailsForTransaction(toid);
                if(!memberDetails.isEmpty())
                {
                    clkey = (String) memberDetails.get("clkey");
                    checksumAlgo = (String) memberDetails.get("checksumalgo");
                    autoredirect = (String) memberDetails.get("autoredirect");
                    isPowerBy = (String) memberDetails.get("isPowerBy");
                    logoName = (String) memberDetails.get("logoName");
                    partnerName = (String) memberDetails.get("partnerName");
                    partnerId = (String) memberDetails.get("partnerId");

                }


                try
                {
                    checksumNew = Checksum.generateChecksumV2(description, String.valueOf(amount), status, clkey, checksumAlgo);
                }
                catch (NoSuchAlgorithmException e)
                {
                    log.error("NoSuchAlgorithm Exception in P4FrontEndServlet---", e);
                    transactionLogger.error("NoSuchAlgorithm Exception in P4FrontEndServlet---", e);
                }


                String respstatus = "Transaction Failed";

                if("Success".equalsIgnoreCase(status))
                {
                    respstatus = "Successfull";
                }
                else if("Abort".equalsIgnoreCase(status))
                {
                    respstatus = "Transaction Aborted (Failed)";
                }
                else if ("TimeOut".equalsIgnoreCase(status))
                {
                    respstatus = "Transaction TimeOut";
                }


                CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
                GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();

                Map saleMap = new TreeMap();
                String respStatus = "N";
                String billingDesc = "";
                if(status!=null && status.contains("Successful") || status.contains("success"))
                {
                    respStatus = "Y";
                    billingDesc = displayName;
                }

                saleMap.put("currency",currency);
                saleMap.put("captureAmount",amount);
                saleMap.put("paymentid",paymentId);
                saleMap.put("checksum",checksumNew);
                saleMap.put("amount",amount);
                saleMap.put("trackingid",transactionId);
                saleMap.put("status",respStatus);
                saleMap.put("descriptor",billingDesc);

                if("Y".equalsIgnoreCase(autoredirect))
                {

                    String redirect = generateAutoSubmitForm(redirectUrl,saleMap);
                    res.setContentType("text/html;charset=UTF-8");
                    res.setCharacterEncoding("UTF-8");

                    try
                    {
                        res.getWriter().write(redirect);
                    }
                    catch (IOException e)
                    {
                        log.error("IO Exception in P4FrontEndServlet---",e);
                        transactionLogger.error("IO Exception in P4FrontEndServlet---",e);
                    }
                }
                else
                {

                    commonValidatorVO.setTrackingid(transactionId);
                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setOrderDesc(orderDesc);
                    req.setAttribute("responceStatus", respstatus);
                    req.setAttribute("displayName", displayName);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    //merchantDetailsVO.setPoweredBy(isPowerBy);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    session.setAttribute("ctoken",ctoken);
                    req.setAttribute("transDetail", commonValidatorVO);
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
        }
    }
}
