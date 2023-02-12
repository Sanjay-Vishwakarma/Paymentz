package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.BlacklistManager;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.LetzPay.LetzPayPaymentGateway;
import com.payment.LetzPay.LetzPayUtils;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.safechargeV2.SafeChargeV2PaymentGateway;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by Admin on 3/22/2021.
 */
public class LetzPayFrontEndServlet extends HttpServlet
{
    private static LetzPayGatewayLogger transactionLogger=new LetzPayGatewayLogger(LetzPayFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        transactionLogger.error("--- Inside LetzPayFrontEndServlet ---");
        HttpSession session = request.getSession(true);

        PrintWriter printWriter=response.getWriter();
        CommonValidatorVO commonValidatorVO =new CommonValidatorVO();
        GenericAddressDetailsVO addressDetailsVO=new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO= new GenericCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO= new GenericTransDetailsVO();
        TransactionUtility transactionUtility= new TransactionUtility();
        TransactionManager transactionManager = new TransactionManager();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        Functions functions = new Functions();
        PaymentManager paymentManager=new PaymentManager();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();
        Comm3DRequestVO commRequestVO=new Comm3DRequestVO();
        CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
        CommResponseVO commResponseVO=null;
        ActionEntry actionEntry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        String requestIp=Functions.getIpAddress(request);
        String trackingId="";
        Enumeration enumeration=request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = request.getParameter(key);
            if(key.contains("trackingId"))
                trackingId=key.split("=")[1];

            transactionLogger.error("Name=" + key + "-----Value=" + value);
        }
        String ENCDATA=request.getParameter("ENCDATA");
        String rresponseCode=request.getParameter("RESPONSE_CODE");
        String rrn=request.getParameter("RRN");
        String transactionId=request.getParameter("TXN_ID");
        String rmessage=request.getParameter("RESPONSE_MESSAGE");
        String message="";
        String responseCode="";
        String Status="";
        String resStatus="";
        String rStatus=request.getParameter("STATUS");
        String bankTransactionDate=request.getParameter("RESPONSE_DATE_TIME");
        String PG_REF_NUM=request.getParameter("PG_REF_NUM");

        String toid="";
        String currency="";
        String amount="";
        String dbStatus="";
        String notificationUrl="";
        String version="";
        String accountId="";
        String email="";
        String firstName="";
        String lastName="";
        String paymodeid="";
        String cardtypeid="";
        String redirectUrl="";
        String ccnum="";
        String expDate="";
        String expMonth="";
        String expYear="";
        String status="";
        String transType="";
        String updatedStatus="";
        String terminalId="";
        String customerId="";
        String tmpl_amt="";
        String tmpl_currency="";
        String orderId="";
        String orderDesc="";
        String autoRedirect="";
        String billingDesc="";
        String partnerName="";
        String logoName="";
        String responseAmount="";
        String RESPONSE_CODE="";
        String rsStatus="";


        Connection con=null;


        PreparedStatement preparedStatement=null;

        try
        {
            TransactionDetailsVO transactionDetailsVO=transactionManager.getTransDetailFromCommon(trackingId);
            if(transactionDetailsVO!=null && functions.isValueNull(transactionDetailsVO.getTrackingid()))
            {
                toid=transactionDetailsVO.getToid();
                amount=transactionDetailsVO.getAmount();
                currency=transactionDetailsVO.getCurrency();
                dbStatus=transactionDetailsVO.getStatus();
                version=transactionDetailsVO.getVersion();
                accountId=transactionDetailsVO.getAccountId();
                firstName = transactionDetailsVO.getFirstName();
                lastName = transactionDetailsVO.getLastName();
                paymodeid = transactionDetailsVO.getPaymodeId();
                cardtypeid = transactionDetailsVO.getCardTypeId();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                email = transactionDetailsVO.getEmailaddr();
                terminalId = transactionDetailsVO.getTerminalId();
                customerId = transactionDetailsVO.getCustomerId();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                orderId = transactionDetailsVO.getDescription();
                orderDesc = transactionDetailsVO.getOrderDescription();

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
                commonValidatorVO.setTrackingid(trackingId);

                merchantDetailsVO=merchantDAO.getMemberDetails(toid);
                if(merchantDetailsVO!=null)
                {
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                }
                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toid);
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
               /* if(functions.isValueNull(ENCDATA))
                {
                    HashMap<String,String> hashMap= LetzPayUtils.getHashMap(ENCDATA,gatewayAccount.getPassword());
                    if(!functions.isValueNull(responseCode))
                        responseCode=hashMap.get("RESPONSE_CODE");
                    if(!functions.isValueNull(transactionId))
                        transactionId=hashMap.get("TXN_ID");
                    if(!functions.isValueNull(message))
                        message=hashMap.get("RESPONSE_MESSAGE");
                    if(!functions.isValueNull(message))
                        message=hashMap.get("PG_TXN_MESSAGE");
                    if(!functions.isValueNull(resStatus))
                        resStatus=hashMap.get("STATUS");
                    if(!functions.isValueNull(bankTransactionDate))
                        bankTransactionDate=hashMap.get("RESPONSE_DATE_TIME");

                }else
                {*/
                    commTransactionDetailsVO.setAmount(amount);
                    commTransactionDetailsVO.setCurrency(currency);
                    commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                    LetzPayPaymentGateway letzPayPaymentGateway=new LetzPayPaymentGateway(accountId);
                    commResponseVO= (CommResponseVO) letzPayPaymentGateway.processQuery(trackingId,commRequestVO);
                    BlacklistManager blacklistManager=new BlacklistManager();
                    BlacklistVO blacklistVO=new BlacklistVO();

              //  }
                transactionLogger.error("dbStatus---->"+dbStatus);
                if(PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                {   con= Database.getConnection();

                    responseAmount=commResponseVO.getAmount();
                    if(functions.isValueNull(responseAmount))
                    {
                    Double compRsAmount= Double.valueOf(responseAmount);
                    Double compDbAmount= Double.valueOf(amount);

                    if(compDbAmount.equals(compRsAmount)){

                        responseCode=commResponseVO.getErrorCode();
                        resStatus=commResponseVO.getStatus();
                        message=commResponseVO.getRemark();
                    }

                    else if(!compDbAmount.equals(compRsAmount))
                    {
                        message="Failed-IRA";
                        transactionLogger.debug("inside else Amount incorrect--->" + responseAmount);
                        resStatus= "authfailed";
                        RESPONSE_CODE="11111";
                        amount=responseAmount;
                        blacklistVO.setVpaAddress(customerId);
                        blacklistVO.setIpAddress(requestIp);
                        blacklistVO.setEmailAddress(email);
                        blacklistVO.setActionExecutorId(toid);
                        blacklistVO.setActionExecutorName("LetzPayFrontEndServlet");
                        blacklistVO.setRemark("IncorrectAmount Trackingid : "+trackingId);
                        blacklistManager.commonBlackListing(blacklistVO);
                    }
                    }

                        if("000".equalsIgnoreCase(responseCode) && ("Captured".equalsIgnoreCase(resStatus)|| "success".equalsIgnoreCase(resStatus)))
                        {
                            rsStatus="success";
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
                            billingDesc=gatewayAccount.getDisplayName();
                            commResponseVO.setDescriptor(billingDesc);
                            updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                            status="Transaction Successful";
                            StringBuffer dbQuery = new StringBuffer("update transaction_common set captureamount=?,status=?,paymentid=?,remark=?,customerIp=?,customerIpCountry=?,eci=?,rrn=?, successtimestamp = ? where trackingId=?");
                            preparedStatement=con.prepareStatement(dbQuery.toString());
                            preparedStatement.setString(1,amount);
                            preparedStatement.setString(2,updatedStatus);
                            preparedStatement.setString(3,transactionId);
                            preparedStatement.setString(4,message);
                            preparedStatement.setString(5,requestIp);
                            preparedStatement.setString(6,functions.getIPCountryShort(requestIp));
                            preparedStatement.setString(7,commResponseVO.getEci());
                            preparedStatement.setString(8,rrn);
                            preparedStatement.setString(9, functions.getTimestamp());
                            preparedStatement.setString(10,trackingId);
                            preparedStatement.executeUpdate();
                            transactionLogger.error("Update Query-->"+preparedStatement);
                            actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                        }
                        else if("Failed".equalsIgnoreCase(resStatus)||"authfailed".equalsIgnoreCase(resStatus))
                        {
                            rsStatus="failed";
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
                            updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                            status="Transaction Failed";
                            StringBuffer dbQuery = new StringBuffer("update transaction_common set status=?,remark=?,customerIp=?,customerIpCountry=?,eci=?,paymentid=?, failuretimestamp = ? where trackingId=?");
                            preparedStatement=con.prepareStatement(dbQuery.toString());
                            preparedStatement.setString(1,updatedStatus);
                            preparedStatement.setString(2,message);
                            preparedStatement.setString(3,requestIp);
                            preparedStatement.setString(4,functions.getIPCountryShort(requestIp));
                            preparedStatement.setString(5,commResponseVO.getEci());
                            preparedStatement.setString(6,transactionId);
                            preparedStatement.setString(7, functions.getTimestamp());
                            preparedStatement.setString(8,trackingId);
                            preparedStatement.executeUpdate();
                            transactionLogger.error("Update Query-->"+preparedStatement);
                            actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                        }
                    else{
                            commResponseVO.setStatus("pending");
                            commResponseVO.setRemark("pending");
                            commResponseVO.setDescription("pending");
                            updatedStatus="pending";
                            rsStatus="pending";
                        }
                    if(!"authstarted".equalsIgnoreCase(updatedStatus))
                    {
                        AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                        AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                    }
                }else
                {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus))
                    {
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status = "success";
                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";
                        updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();

                    }else if(PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus)){
                        billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                        status = "success";
                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else
                            message = "Transaction Successful";
                        updatedStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                    }else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus)){
                        status = "fail";
                        if(functions.isValueNull(transactionDetailsVO.getRemark()))
                            message = transactionDetailsVO.getRemark();
                        else if(!functions.isValueNull(message))
                            message = "Transaction Failed";
                        updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                        if(functions.isValueNull(trackingId))
                        {
                            con = Database.getConnection();
                            StringBuffer dbBuffer = new StringBuffer();
                            dbBuffer.append("update transaction_common set customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' where trackingid = " + trackingId);
                            Database.executeUpdate(dbBuffer.toString(), con);
                        }

                    }
                    else
                    {
                        status = "pending";
                        message = "Transaction pending";
                        updatedStatus="pending";
                        rsStatus="pending";

                    }
                }
                genericTransDetailsVO.setOrderId(orderId);
                genericTransDetailsVO.setAmount(amount);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);

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
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);
                commonValidatorVO.setTrackingid(trackingId);
                commonValidatorVO.setReason(message);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setCustomerId(customerId);
                commonValidatorVO.setTerminalId(terminalId);
                transactionUtility.setToken(commonValidatorVO, dbStatus);

                transactionLogger.error("TransactionNotification flag for ---"+toid+"---"+merchantDetailsVO.getTransactionNotification());
                if (functions.isValueNull(notificationUrl) && functions.isValueNull(updatedStatus) && !"authstarted".equalsIgnoreCase(updatedStatus))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                  //  transactionDetailsVO1.setTransactionMode("3D");
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, rsStatus, message, "");
                }

                if ("Y".equalsIgnoreCase(autoRedirect))
                {
                    transactionUtility.doAutoRedirect(commonValidatorVO, response, rsStatus, billingDesc);
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
            transactionLogger.error("PZDBViolationException--" + trackingId + "-->", e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("systemError--" + trackingId + "-->", systemError);

        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException--" + trackingId + "-->", e);

        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException--" + trackingId + "-->", e);
        }
        catch (Exception e){
            transactionLogger.error("Exception--" + trackingId + "-->", e);

        }
        finally
        {
            Database.closeConnection(con);
        }



    }
}
