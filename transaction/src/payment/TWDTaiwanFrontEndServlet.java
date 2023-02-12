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
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.TWDTaiwan.TWDTaiwanUtils;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Created by Admin on 12/24/2020.
 */
public class TWDTaiwanFrontEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger=new TransactionLogger(TWDTaiwanFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        transactionLogger.error("--- Inside TWDTaiwanFrontEndServlet ---");
        HttpSession session=request.getSession();
        Functions functions=new Functions();
        TransactionManager transactionManager=new TransactionManager();
        MerchantDAO merchantDAO=new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO=null;
        CommonValidatorVO commonValidatorVO=new CommonValidatorVO();
        Comm3DResponseVO commResponseVO=new Comm3DResponseVO();
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        ActionEntry actionEntry=new ActionEntry();
        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
        GenericCardDetailsVO cardDetailsVO=new GenericCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO=new GenericAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        TransactionUtility transactionUtility=new TransactionUtility();

        Enumeration enumeration=request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = request.getParameter(key);

            transactionLogger.error("Name=" + key + "-----Value=" + value);
        }
        String trackingId=request.getParameter("trackingId");
        String resStatus=request.getParameter("status");
        String txid=request.getParameter("txid");
        String transactionId=request.getParameter("parent_txid");
        if(!functions.isValueNull(transactionId))
            transactionId=txid;
        String comment=request.getParameter("comment");
        String descriptor=request.getParameter("descriptor");
        String error=request.getParameter("error");
        String error_type=request.getParameter("error_type");
        String error_sys=request.getParameter("error_sys");
        String error_msg=request.getParameter("error_msg");
        String error_info=request.getParameter("error_info");
        String error_code=request.getParameter("error_code");
        String requestIp=Functions.getIpAddress(request);
        if(functions.isValueNull(error_msg))
        {
            String code=error_msg;
            String errorMsg = TWDTaiwanUtils.getErrorMessage(code);
            if(functions.isValueNull(errorMsg))
            {
                error_msg=errorMsg;
                error_code = code;
            }
        }
        if(functions.isValueNull(error_msg) && error_msg.contains(":"))
        {
            error_code = error_msg.split(":")[0];
            error_msg= TWDTaiwanUtils.getErrorMessage(error_code);
        }
        String toid="";
        String currency="";
        String amount="";
        String dbStatus="";
        String clKey="";
        String notificationUrl="";
        String version="";
        String accountId="";
        String paymentid="";
        String zip="";
        String street="";
        String state="";
        String city="";
        String country="";
        String email="";
        String telcc="";
        String telno="";
        String firstName="";
        String lastName="";
        String paymodeid="";
        String cardtypeid="";
        String redirectUrl="";
        String isService="";
        String ccnum="";
        String expDate="";
        String expMonth="";
        String expYear="";
        String status="";
        String transType="";
        String updatedStatus="";
        String message="";
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
                paymentid=transactionDetailsVO.getPaymentId();
                zip = transactionDetailsVO.getZip();
                street = transactionDetailsVO.getStreet();
                state = transactionDetailsVO.getState();
                city = transactionDetailsVO.getCity();
                country = transactionDetailsVO.getCountry();
                telcc = transactionDetailsVO.getTelcc();
                telno = transactionDetailsVO.getTelno();
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
                    clKey=merchantDetailsVO.getKey();
                    isService=merchantDetailsVO.getIsService();
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                }
                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toid);
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

                if(functions.isValueNull(comment))
                    message=comment;
                else if(functions.isValueNull(error))
                    message=error;
                else if(functions.isValueNull(error_info))
                    message=error_info;
                else if(functions.isValueNull(error_msg) && error_msg.length()>3)
                    message=error_msg;

                transactionLogger.error("dbStatus---->"+dbStatus);
                if(PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                {
                    notificationUrl=transactionDetailsVO.getNotificationUrl();
                    con= Database.getConnection();
                    if("OK".equalsIgnoreCase(resStatus))
                    {
                        status="success";
                        billingDesc=gatewayAccount.getDisplayName();
                        if(functions.isValueNull(descriptor))
                            commResponseVO.setDescriptor(descriptor);
                        else
                            commResponseVO.setDescriptor(billingDesc);

                        if(!functions.isValueNull(message))
                            message="Transaction Successful";
                        commResponseVO.setRemark(message);
                        commResponseVO.setStatus(status);
                        updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                        StringBuffer dbQuery = new StringBuffer("update transaction_common set captureamount=?,status=?,paymentid=?,remark=?,customerIp=?,customerIpCountry=? where trackingId=?");
                        preparedStatement=con.prepareStatement(dbQuery.toString());
                        preparedStatement.setString(1,amount);
                        preparedStatement.setString(2,updatedStatus);
                        preparedStatement.setString(3,transactionId);
                        preparedStatement.setString(4,message);
                        preparedStatement.setString(5,requestIp);
                        preparedStatement.setString(6,functions.getIPCountryShort(requestIp));
                        preparedStatement.setString(7,trackingId);
                        preparedStatement.executeUpdate();
                        transactionLogger.error("Update Query-->"+preparedStatement);
                        actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }else
                    {
                        status="failed";
                        if(!functions.isValueNull(message))
                            message="Transaction failed";
                        commResponseVO.setRemark(message);
                        commResponseVO.setStatus("failed");
                        commResponseVO.setErrorCode(error_code);
                        updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                        StringBuffer dbQuery = new StringBuffer("update transaction_common set status=?,remark=?,customerIp=?,customerIpCountry=? where trackingId=?");
                        preparedStatement=con.prepareStatement(dbQuery.toString());
                        preparedStatement.setString(1,updatedStatus);
                        preparedStatement.setString(2,message);
                        preparedStatement.setString(3,requestIp);
                        preparedStatement.setString(4,functions.getIPCountryShort(requestIp));
                        preparedStatement.setString(5,trackingId);
                        preparedStatement.executeUpdate();
                        transactionLogger.error("Update Query-->"+preparedStatement);
                        actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                    }
                    AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
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
                        if(functions.isValueNull(trackingId))
                        {
                            con = Database.getConnection();
                            StringBuffer dbBuffer = new StringBuffer();
                            dbBuffer.append("update transaction_common set customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' where trackingid = " + trackingId);
                            Database.executeUpdate(dbBuffer.toString(), con);
                        }

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
                        status = "fail";
                        message = "Transaction Declined";
                        updatedStatus=PZTransactionStatus.FAILED.toString();

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

                if (functions.isValueNull(notificationUrl) && functions.isValueNull(updatedStatus))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
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
            transactionLogger.error("PZDBViolationException--"+trackingId+"-->",e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError-="+trackingId+"-->",systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException-="+trackingId+"-->", e);
        }catch (Exception e){
            transactionLogger.error("Exception--"+trackingId+"-->", e);
        }
        finally
        {
            Database.closeConnection(con);
        }

    }
}
