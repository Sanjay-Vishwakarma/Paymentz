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
import com.payment.PayEasyWorld.PayEasyWorldUtils;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Created by Admin on 5/24/2021.
 */
public class PayEasyWorldFrontEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(PayEasyWorldFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws  IOException,ServletException
    {
        transactionLogger.error(":::Inside PayEasyWorldFrontEndServlet:::");
        Enumeration enumeration=request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = request.getParameter(key);

            transactionLogger.error("Name=" + key + "-----Value=" + value);
        }
        HttpSession session = request.getSession(true);
        CommonValidatorVO commonValidatorVO =new CommonValidatorVO();
        GenericAddressDetailsVO addressDetailsVO=new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO= new GenericCardDetailsVO();
        GenericTransDetailsVO transDetailsVO= new GenericTransDetailsVO();
        TransactionUtility transactionUtility= new TransactionUtility();
        TransactionManager transactionManager = new TransactionManager();
        ActionEntry actionEntry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        String requestIp=Functions.getIpAddress(request);
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        Functions functions=new Functions();
        MerchantDAO merchantDAO=new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO=null;
        CommResponseVO commResponseVO=null;
        Connection con=null;
        PreparedStatement preparedStatement=null;

        String trackingId=request.getParameter("trackingId");
        String paymentId=request.getParameter("tradeNo");
        String orderSucceed=request.getParameter("orderSucceed");
        String orderAmount=request.getParameter("orderAmount");
        String resultCode=request.getParameter("resultCode");
        String message=request.getParameter("orderResult");
        String acquirer=request.getParameter("acquirer");
        String signInfo=request.getParameter("signInfo");
        String merNo=request.getParameter("merNo");
        String terNo=request.getParameter("terNo");

        String toId="";
        String accountId="";
        String amount="";
        String orderId="";
        String redirectUrl="";
        String tmpl_amt="";
        String tmpl_currency="";
        String dbStatus="";
        String payModeId="";
        String cardTypeId="";
        String customerId="";
        String version="";
        String notificationUrl="";
        String terminalId="";
        String firstName="";
        String lastName="";
        String ccnum="";
        String expDate="";
        String expMonth="";
        String expYear="";
        String autoRedirect="";
        String logoName="";
        String partnerName="";
        String orderDesc="";
        String currency="";
        String billingDesc="";
        String updatedStatus="";
        String email="";
        String status="";
        try{
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if (transactionDetailsVO != null && functions.isValueNull(transactionDetailsVO.getTrackingid()))
            {
                toId = transactionDetailsVO.getToid();
                accountId = transactionDetailsVO.getAccountId();
                amount = transactionDetailsVO.getAmount();
                transactionLogger.debug("amount -------" + amount);
                orderId = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                dbStatus = transactionDetailsVO.getStatus();
                payModeId = transactionDetailsVO.getPaymodeId();
                cardTypeId = transactionDetailsVO.getCardTypeId();
                email = transactionDetailsVO.getEmailaddr();
                customerId = transactionDetailsVO.getCustomerId();
                version = transactionDetailsVO.getVersion();
                //notificationUrl = transactionDetailsVO.getNotificationUrl();
                terminalId = transactionDetailsVO.getTerminalId();
                firstName = transactionDetailsVO.getFirstName();
                lastName = transactionDetailsVO.getLastName();
                if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                {
                    ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                }
                if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                {
                    expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
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
                GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                if (merchantDetailsVO != null)
                {
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                }
                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toId);
                if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                {
                    orderDesc = transactionDetailsVO.getOrderDescription();
                }
                currency = transactionDetailsVO.getCurrency();
                con=Database.getConnection();
                transactionLogger.error("dbStatus-----" + dbStatus);
                String generatedSignMsg= PayEasyWorldUtils.SHA256forSales(gatewayAccount.getMerchantId().trim()+gatewayAccount.getFRAUD_FTP_USERNAME()+paymentId.trim()+trackingId.trim()+currency.trim()+orderAmount.trim()+orderSucceed.trim()+gatewayAccount.getFRAUD_FTP_PASSWORD().trim());
                if(!signInfo.trim().equalsIgnoreCase(generatedSignMsg))
                {
                    message="SignInfo Mismatch!";
                    orderSucceed="0";
                }
                if(functions.isValueNull(orderAmount))
                    orderAmount=String.format("%.2f",Double.parseDouble(orderAmount));
                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus))
                {
                    commResponseVO=new CommResponseVO();
                    commResponseVO.setTransactionId(paymentId);
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setAmount(orderAmount);
                    commResponseVO.setTmpl_Amount(tmpl_amt);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    commResponseVO.setIpaddress(requestIp);
                    commResponseVO.setErrorCode(resultCode);

                    if("1".equalsIgnoreCase(orderSucceed)){
                        commResponseVO.setStatus("success");
                        notificationUrl=transactionDetailsVO.getNotificationUrl();
                        if(functions.isValueNull(message))
                        {
                            commResponseVO.setRemark(message);
                            commResponseVO.setDescription(message);
                        }else
                        {
                            commResponseVO.setRemark("Transaction Successful");
                            commResponseVO.setDescription("Transaction Successful");
                        }
                        if(functions.isValueNull(acquirer) && "Y".equalsIgnoreCase(gatewayAccount.getIsDynamicDescriptor()))
                            billingDesc=acquirer;
                        else
                            billingDesc=gatewayAccount.getDisplayName();

                        commResponseVO.setDescriptor(billingDesc);
                        updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        status="Transaction Successful";
                        StringBuffer dbQuery = new StringBuffer("update transaction_common set captureamount=?,status=?,paymentid=?,remark=?,customerIp=?,customerIpCountry=? where trackingId=?");
                        preparedStatement=con.prepareStatement(dbQuery.toString());
                        preparedStatement.setString(1,orderAmount);
                        preparedStatement.setString(2,updatedStatus);
                        preparedStatement.setString(3,paymentId);
                        preparedStatement.setString(4,message);
                        preparedStatement.setString(5,requestIp);
                        preparedStatement.setString(6,functions.getIPCountryShort(requestIp));
                        preparedStatement.setString(7,trackingId);
                        preparedStatement.executeUpdate();
                        transactionLogger.error("Update Query-->"+preparedStatement);
                        actionEntry.actionEntryForCommon(trackingId, orderAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }
                    if("-1".equals(orderSucceed) || "-2".equals(orderSucceed)){
                        updatedStatus=PZTransactionStatus.AUTH_STARTED.toString();
                        message="Transaction is pending";
                        status="Pending";
                    }else if("0".equalsIgnoreCase(orderSucceed))
                    {
                        commResponseVO.setStatus("failed");
                        notificationUrl=transactionDetailsVO.getNotificationUrl();
                        if(functions.isValueNull(message))
                        {
                            commResponseVO.setRemark(message);
                            commResponseVO.setDescription(message);
                        }else
                        {
                            commResponseVO.setRemark("Transaction Failed");
                            commResponseVO.setDescription("Transaction Failed");
                        }
                        status=commResponseVO.getRemark();
                        updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                        StringBuffer dbQuery = new StringBuffer("update transaction_common set status=?,remark=?,customerIp=?,customerIpCountry=?,paymentid=? where trackingId=?");
                        preparedStatement=con.prepareStatement(dbQuery.toString());
                        preparedStatement.setString(1,updatedStatus);
                        preparedStatement.setString(2,message);
                        preparedStatement.setString(3,requestIp);
                        preparedStatement.setString(4,functions.getIPCountryShort(requestIp));
                        preparedStatement.setString(5,paymentId);
                        preparedStatement.setString(6,trackingId);
                        preparedStatement.executeUpdate();
                        transactionLogger.error("Update Query-->"+preparedStatement);
                        actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }
                }else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";
                        updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        status="Transaction Successful";

                    }else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus)){
                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else if(!functions.isValueNull(message))
                            message = "Transaction Failed";
                        updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                        if(functions.isValueNull(trackingId))
                        {
                            StringBuffer dbBuffer = new StringBuffer();
                            dbBuffer.append("update transaction_common set customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' where trackingid = " + trackingId);
                            Database.executeUpdate(dbBuffer.toString(), con);
                        }

                    }
                    else
                    {
                        message = "Transaction Declined";
                        updatedStatus=PZTransactionStatus.FAILED.toString();

                    }
                }
                transDetailsVO.setOrderId(orderId);
                transDetailsVO.setAmount(amount);
                transDetailsVO.setCurrency(currency);
                transDetailsVO.setOrderDesc(orderDesc);
                transDetailsVO.setRedirectUrl(redirectUrl);
                transDetailsVO.setNotificationUrl(notificationUrl);
                transDetailsVO.setBillingDiscriptor(billingDesc);

                addressDetailsVO.setEmail(email);
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                addressDetailsVO.setFirstname(firstName);
                addressDetailsVO.setLastname(lastName);
                cardDetailsVO.setCardNum(ccnum);
                cardDetailsVO.setExpMonth(expMonth);
                cardDetailsVO.setExpYear(expYear);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setPaymentType(payModeId);
                commonValidatorVO.setCardType(cardTypeId);
                commonValidatorVO.setTrackingid(trackingId);
                commonValidatorVO.setReason(message);

                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setTransDetailsVO(transDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setCustomerId(customerId);
                commonValidatorVO.setTerminalId(terminalId);
                transactionUtility.setToken(commonValidatorVO, dbStatus);

                transactionLogger.error("TransactionNotification flag for ---"+toId+"---"+merchantDetailsVO.getTransactionNotification());
                if (functions.isValueNull(notificationUrl) && functions.isValueNull(updatedStatus) && !"authstarted".equalsIgnoreCase(updatedStatus))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    transactionDetailsVO1.setTransactionMode("3D");
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, updatedStatus, message, "");
                }

                if ("Y".equalsIgnoreCase(autoRedirect))
                {
                    transactionUtility.doAutoRedirect(commonValidatorVO, response, updatedStatus, billingDesc);
                }
                else
                {
                    transactionLogger.debug("-----inside confirmation page-----");
                    session.setAttribute("ctoken", ctoken);
                    request.setAttribute("transDetail", commonValidatorVO);
                    request.setAttribute("responceStatus", status);
                    request.setAttribute("remark", message);
                    request.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    String confirmationPage = "";
                    confirmationPage = "/confirmationCheckout.jsp?ctoken=";

                    RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(request, response);

                }
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException---"+trackingId+"-->",e);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException---" + trackingId + "-->", e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError---" + trackingId + "-->", systemError);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException---" + trackingId + "-->", e);
        }finally
        {
            Database.closeConnection(con);
        }


    }
}
