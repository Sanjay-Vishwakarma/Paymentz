
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
import com.payment.statussync.StatusSyncDAO;
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
import java.util.HashMap;

public class EpayFrontEndServlet extends PzServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(EpayFrontEndServlet.class.getName());

    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest req, HttpServletResponse res)
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res)
    {
        for(Object key : req.getParameterMap().keySet())
        {
            transactionLogger.error("----for loop EpayFrontEndServlet-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }

        HttpSession session = req.getSession(true);
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        CommResponseVO commResponseVO= new CommResponseVO();
        TransactionManager transactionManager = new TransactionManager();
        Functions functions= new Functions();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        PaymentManager paymentManager = new PaymentManager();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        ActionEntry entry = new ActionEntry();

        StringBuffer dbBuffer = new StringBuffer();

        AuditTrailVO auditTrailVO = new AuditTrailVO();
        auditTrailVO.setActionExecutorName("AcquirerFrontEnd");

        if (!req.getParameterMap().isEmpty())
        {
            String transactionStatus = ESAPI.encoder().encodeForSQL(me, req.getParameter("STATUS"));
            String trackingId = ESAPI.encoder().encodeForSQL(me, req.getParameter("trackingId"));

            if (functions.isValueNull(trackingId))
            {
                transactionLogger.error("trackingid and status---" + trackingId + "-" + transactionStatus);

                //Transaction Details
                String toid = "";
                String description = "";
                String redirectUrl = "";
                String accountId = "";
                String orderDesc = "";
                String currency = "";
                String amount = "";
                String remark = "";
                String status = "";
                String captureAmount = "";

                //Merchant Details
                String clkey = "";
                String checksumAlgo = "";
                String autoredirect = "";
                String isPowerBy = "";
                String logoName = "";
                String partnerName = "";
                String displayName = "";
                String dbStatus = "";
                String email = "";

                String paymodeid = "";
                String cardtypeid = "";

                String tmpl_amt = "";
                String tmpl_currency = "";
                String customerid="";

                String respstatus = "";
                String resStatus = "";
                String billingDesc = "";
                String table_name = "transaction_epay_details";


                try
                {
                    TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

                    if (transactionDetailsVO != null)
                    {
                        toid = transactionDetailsVO.getToid();
                        description = transactionDetailsVO.getDescription();
                        redirectUrl = transactionDetailsVO.getRedirectURL();
                        accountId = transactionDetailsVO.getAccountId();
                        orderDesc = transactionDetailsVO.getOrderDescription();
                        currency = transactionDetailsVO.getCurrency();
                        amount = transactionDetailsVO.getAmount();
                        email = transactionDetailsVO.getEmailaddr();
                        merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                        autoredirect = merchantDetailsVO.getAutoRedirect();
                        logoName = merchantDetailsVO.getLogoName();
                        partnerName = merchantDetailsVO.getPartnerName();
                        paymodeid = transactionDetailsVO.getPaymodeId();
                        cardtypeid = transactionDetailsVO.getCardTypeId();
                        customerid=transactionDetailsVO.getCustomerId();


                        tmpl_amt = transactionDetailsVO.getTemplateamount();
                        tmpl_currency = transactionDetailsVO.getTemplatecurrency();


                        displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        auditTrailVO.setActionExecutorId(toid);


                        //dbBuffer.append("update transaction_common set ");
                        if ("OK".equalsIgnoreCase(transactionStatus))
                        {
                            transactionLogger.error("Inside success condition for Epay---" + transactionStatus);
                            respstatus = "Successful";
                            resStatus = "Y";
                            billingDesc = displayName;
                            if (functions.isValueNull(email))
                            {
                                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), transactionStatus, respstatus, displayName);
                            }
                        }
                        else
                        {
                            transactionLogger.error("Inside failed condition for Epay---" + transactionStatus);

                            respstatus = "Failed";
                            resStatus = "N";
                        }

                        String custEmail = "";
                        String custBankId = "";

                        HashMap hashMap = paymentManager.getExtnDetailsForEpay(trackingId);

                        if (functions.isValueNull((String) hashMap.get("customerEmail")))
                        {
                            custEmail = (String) hashMap.get("customerEmail");
                        }
                        if (functions.isValueNull((String) hashMap.get("customerBankId")))
                        {
                            custBankId = (String) hashMap.get("customerBankId");
                        }
                        commonValidatorVO.setTrackingid(trackingId);
                        genericTransDetailsVO.setOrderId(description);
                        genericTransDetailsVO.setAmount(amount);
                        genericTransDetailsVO.setCurrency(currency);
                        genericTransDetailsVO.setOrderDesc(orderDesc);
                        genericTransDetailsVO.setRedirectUrl(redirectUrl);
                        commonValidatorVO.setLogoName(logoName);
                        commonValidatorVO.setPartnerName(partnerName);
                        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                        addressDetailsVO.setTmpl_amount(tmpl_amt);
                        addressDetailsVO.setTmpl_currency(tmpl_currency);
                        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                        commonValidatorVO.setPaymentType(transactionDetailsVO.getPaymodeId());
                        commonValidatorVO.setCardType(transactionDetailsVO.getCardTypeId());
                        if (functions.isValueNull(custEmail))
                            addressDetailsVO.setEmail(custEmail);
                        else
                            addressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
                        commonValidatorVO.setPaymentType(paymodeid);
                        commonValidatorVO.setCardType(cardtypeid);

                        commonValidatorVO.setCustomerId(customerid);
                        commonValidatorVO.setCustomerBankId(custBankId);

                        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);

                        if ("Y".equalsIgnoreCase(autoredirect))
                        {
                            TransactionUtility transactionUtility = new TransactionUtility();
                            transactionLogger.error("respStatus in Y---" + respstatus);
                            transactionUtility.doAutoRedirect(commonValidatorVO, res, respstatus, billingDesc);
                        }
                        else
                        {

                            session.setAttribute("ctoken", ctoken);
                            req.setAttribute("transDetail", commonValidatorVO);
                            req.setAttribute("responceStatus", respstatus);
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
                }
                catch (SystemError se){
                    transactionLogger.error("PZDBViolationException----",se);
                }
                catch(PZDBViolationException e)
                {
                    transactionLogger.error("PZDBViolationException----", e);
                }
                catch(ServletException e)
                {
                    transactionLogger.error("ServletException----", e);
                }
                catch(IOException e)
                {
                    transactionLogger.error("IOException----", e);
                }
            }
        }
    }
}


