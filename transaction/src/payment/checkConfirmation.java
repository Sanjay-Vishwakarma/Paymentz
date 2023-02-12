package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;

/**
 * Created by Admin on 11/16/2018.
 */
public class checkConfirmation extends HttpServlet
{
    private static Logger log = new Logger(checkConfirmation.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(checkConfirmation.class.getName());

    public void doPost(HttpServletRequest req , HttpServletResponse res) throws IOException , ServletException
    {
        transactionLogger.error("------ INSIDE QRConfirmation ------");

        HttpSession session = req.getSession(true);
        String ctoken       = req.getParameter("ctoken");
        session.setAttribute("ctoken", ctoken);

        transactionLogger.error("----inside QRConfirmation ctoken ----" + ctoken);
        for(Object key : req.getParameterMap().keySet())
        {
            /*log.error("----for loop QRConfirmation-----" + key + "=" + req.getParameter((String) key) + "--------------");*/
            transactionLogger.error("----for loop QRConfirmation-----" + key + "=" + req.getParameter((String) key) + "--------------");
        }

        Enumeration en=req.getParameterNames();
        while (en.hasMoreElements()){
            String key = (String) en.nextElement();
            String value=req.getParameter(key);
//            transactionLogger.error("key----" + key + "-----" + value);
        }

        Functions functions= new Functions();
        PrintWriter ps=res.getWriter();

        String trackingId = "";
        String success_status = ""; // from wallet.jsp form submit
        String call_param = "";  // from content.js AJAX call

        try
        {
            String customerId="";
            String terminalId="";
            String description="";
            String orderDescription="";
            String amount="";
            String currency="";
            String payModeId="";
            String cardTypeId="";
            String redirectUrl="";
            String notificationUrl="";
            String billingDesc="";
            String logoName="";
            String partnerName="";
            String autoRedirect="";
            String updateStatus = "";
            String message ="";
            String accountId = "";
            String toId = "";
            String walletAmount = "";
            String walletCurrency = "";
            String tmpl_amount = "";
            String tmpl_currency = "";
            String email = "";
            String firstName = "";
            String lastName = "";
            String dbStatus = "";

            if(functions.isValueNull(req.getParameter("trackingId"))) {
                trackingId = req.getParameter("trackingId");
            }
            if(functions.isValueNull(req.getParameter("success_status")) ) {
                success_status = req.getParameter("success_status");
            }
            if(functions.isValueNull(req.getParameter("param"))) {
                call_param = req.getParameter("param");
            }

            TransactionUtility transactionUtility = new TransactionUtility();
            TransactionManager transactionManager = new TransactionManager();
            TransactionDetailsVO transactionDetailsVO = null;
            transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            accountId = transactionDetailsVO.getAccountId();
            toId = transactionDetailsVO.getToid();

            // customer VO to get wallet id

            transactionLogger.error("in checkconfirmation accountId ----" + accountId);
            transactionLogger.error("in checkconfirmation toId ----" + toId);
            transactionLogger.error("in checkconfirmation trackingId ----" + trackingId);

            CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
            GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
            GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
            GenericCardDetailsVO cardDetailsVO          = new GenericCardDetailsVO();
            GatewayAccount gatewayAccount               = GatewayAccountService.getGatewayAccount(accountId);
            MerchantDetailsVO merchantDetailsVO         = null;
            MerchantDAO merchantDAO                     = new MerchantDAO();
            merchantDetailsVO                           = merchantDAO.getMemberDetails(toId);

            customerId          = transactionDetailsVO.getCustomerId();
            description         = transactionDetailsVO.getDescription();
            orderDescription    = transactionDetailsVO.getOrderDescription();
            amount              = transactionDetailsVO.getAmount();
            currency            = transactionDetailsVO.getCurrency();
            payModeId           = transactionDetailsVO.getPaymodeId();
            cardTypeId          = transactionDetailsVO.getCardTypeId();
            redirectUrl         = transactionDetailsVO.getRedirectURL();
            notificationUrl     = transactionDetailsVO.getNotificationUrl();
            terminalId          = transactionDetailsVO.getTerminalId();
            walletAmount        = transactionDetailsVO.getWalletAmount();
            walletCurrency      = transactionDetailsVO.getWalletCurrency();
            tmpl_amount         = transactionDetailsVO.getTemplateamount();
            tmpl_currency       = transactionDetailsVO.getTemplatecurrency();
            logoName            = merchantDetailsVO.getLogoName();
            partnerName         = merchantDetailsVO.getPartnerName();
            autoRedirect        = merchantDetailsVO.getAutoRedirect();

            dbStatus        = transactionDetailsVO.getStatus();
            transactionLogger.error("dbStatus ---" + dbStatus);

            if(functions.isValueNull(transactionDetailsVO.getFirstName())) {
                firstName = transactionDetailsVO.getFirstName();
            }
            if(functions.isValueNull(transactionDetailsVO.getLastName())) {
                lastName = transactionDetailsVO.getLastName();
            }
            if(functions.isValueNull(transactionDetailsVO.getEmailaddr())) {
                email = transactionDetailsVO.getEmailaddr();
            }

            genericTransDetailsVO.setOrderId(description);
            genericTransDetailsVO.setOrderDesc(orderDescription);
            genericTransDetailsVO.setAmount(amount);
            genericTransDetailsVO.setCurrency(currency);
            genericTransDetailsVO.setBillingDiscriptor(billingDesc);
            genericTransDetailsVO.setNotificationUrl(notificationUrl);
            genericTransDetailsVO.setRedirectUrl(redirectUrl);
            genericTransDetailsVO.setWalletCurrency(walletCurrency);
            genericTransDetailsVO.setWalletAmount(walletAmount);

            addressDetailsVO.setEmail(email);
            addressDetailsVO.setFirstname(firstName);
            addressDetailsVO.setLastname(lastName);
            addressDetailsVO.setTmpl_amount(tmpl_amount);
            addressDetailsVO.setTmpl_currency(tmpl_currency);

            commonValidatorVO.setTrackingid(trackingId);
            commonValidatorVO.setCustomerId(customerId);
            commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
            commonValidatorVO.setCardDetailsVO(cardDetailsVO);
            commonValidatorVO.setTerminalId(terminalId);
            commonValidatorVO.setVersion(transactionDetailsVO.getVersion());
            commonValidatorVO.setLogoName(logoName);
            commonValidatorVO.setPartnerName(partnerName);
            commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            commonValidatorVO.setPaymentType(payModeId);
            commonValidatorVO.setCardType(cardTypeId);

            Connection conn         = null;
            PreparedStatement pst   = null;
            ResultSet rs            = null;
            JSONObject jsonObject   = new JSONObject();

            transactionLogger.error("success_status -----"+success_status);
            transactionLogger.error("call_param -----"+call_param);

            if(call_param.equalsIgnoreCase("ajax"))
            {
                transactionLogger.error("IN else if call_param -------");
                try
                {
                    conn        = Database.getConnection();
                    String sql  = "SELECT STATUS FROM transaction_common WHERE trackingid=?;";

                    pst = conn.prepareStatement(sql);
                    pst.setString(1, trackingId);
                    rs = pst.executeQuery();
                    while (rs.next())
                    {
                        transactionLogger.error("db status -----" + rs.getString("status"));
                        if (rs.getString("status").equalsIgnoreCase("authsuccessful") || rs.getString("status").equalsIgnoreCase("capturesuccess"))
                        {
                            jsonObject.put("status", "success");
                        }
                        else if (rs.getString("status").equalsIgnoreCase("authfailed"))
                        {
                            jsonObject.put("status", "fail");
                        }
                        else{
                            jsonObject.put("status", "N");
                        }
                    }
                    transactionLogger.error("query-----" + pst);
                    res.setContentType("application/json");
                    res.setStatus(200);
                    ps.write(jsonObject.toString());
                    ps.flush();
                    return;
                }
                catch (Exception e)
                {
                    transactionLogger.error("Exception-----", e);
                }
                finally
                {
                    Database.closeConnection(conn);
                    Database.closePreparedStatement(pst);
                    Database.closeResultSet(rs);
                }
            }
            else if(success_status.equalsIgnoreCase("success") || success_status.equalsIgnoreCase("fail"))
            {
                billingDesc         = gatewayAccount.getDisplayName();
                if ("Y".equalsIgnoreCase(autoRedirect))
                {
                    transactionUtility.doAutoRedirect(commonValidatorVO, res, success_status,billingDesc); /*billingDesc*/
                }
                else
                {
                    req.setAttribute("responceStatus", success_status);
                    req.setAttribute("displayName", billingDesc);
                    req.setAttribute("remark", message);
                    req.setAttribute("transDetail", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);

                    String confirmationPage = "";
                    String version = transactionDetailsVO.getVersion();
                    if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";

                    transactionLogger.error("Version value---" + version + "---" + confirmationPage);
                    RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(req, res);
                }
            }else if(success_status.equalsIgnoreCase("pending")){

                transactionLogger.error("checkConfirmation transactionStatus " +trackingId+" "+ dbStatus);

                if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                {
                    billingDesc         = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    success_status      = "success";
                    if(functions.isValueNull(transactionDetailsVO.getRemark()))
                        message = transactionDetailsVO.getRemark();
                    else
                        message = "Transaction Successful";
                    dbStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                }
                else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                {
                    success_status  = "failed";
                    if(functions.isValueNull(transactionDetailsVO.getRemark()))
                        message = transactionDetailsVO.getRemark();
                    else if(!functions.isValueNull(message))
                        message = "Transaction Failed";
                    dbStatus=PZTransactionStatus.AUTH_FAILED.toString();
                }
                else
                {

                    success_status      = "pending";
                    message     = "Transaction pending";
                    dbStatus    = PZTransactionStatus.AUTH_STARTED.toString();
                }


                if ("Y".equalsIgnoreCase(autoRedirect))
                {
                    transactionUtility.doAutoRedirect(commonValidatorVO, res, success_status,billingDesc); /*billingDesc*/
                }
                else
                {
                    req.setAttribute("responceStatus", success_status);
                    req.setAttribute("displayName", billingDesc);
                    req.setAttribute("remark", message);
                    req.setAttribute("transDetail", commonValidatorVO);
                    session.setAttribute("ctoken", ctoken);

                    String confirmationPage = "";
                    String version = transactionDetailsVO.getVersion();
                    if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";

                    transactionLogger.error("Version value---" + version + "---" + confirmationPage);
                    RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(req, res);
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
    }
}