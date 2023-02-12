package payment;

import com.directi.pg.*;
import com.directi.pg.Base64;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PaymentManager;
import com.manager.TerminalManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
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
import org.codehaus.jettison.json.JSONObject;
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
import java.util.*;

/**
 * Created by Admin on 9/18/2020.
 */
public class SafeChargeV2FrontEndServlet extends HttpServlet
{
    TransactionLogger transactionLogger= new TransactionLogger(SafeChargeV2FrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        PrintWriter printWriter=response.getWriter();
        HttpSession session = request.getSession(true);
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
        CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
        CommResponseVO commResponseVO=null;
        ActionEntry actionEntry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        String requestIp=Functions.getIpAddress(request);

        Enumeration enumeration=request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = request.getParameter(key);

            transactionLogger.error("Name=" + key + "-----Value=" + value);
        }

        String trackingId=request.getParameter("trackingId");
        String cRes=request.getParameter("cres");
        String PaRes=request.getParameter("PaRes");
        String MD=request.getParameter("MD");
        String cvv="";
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
        String eci="";
        String threeDSServerTransID="";
        String acsTransID="";
        String transStatus="";
        String messageType="";
        Connection con=null;
        PreparedStatement preparedStatement=null;

        if (functions.isValueNull(MD))
        {
            cvv = PzEncryptor.decryptCVV(MD);
        }
        try
        {
        if(functions.isValueNull(cRes))
        {
            cRes=new String(Base64.decode(cRes));
            if(cRes.contains("{"))
            {
                JSONObject cResJSON=new JSONObject(cRes);
                if(cResJSON.has("threeDSServerTransID"))
                    threeDSServerTransID=cResJSON.getString("threeDSServerTransID");
                if(cResJSON.has("acsTransID"))
                    acsTransID=cResJSON.getString("acsTransID");
                if(cResJSON.has("transStatus"))
                    transStatus=cResJSON.getString("transStatus");
                if(!functions.isValueNull(transStatus))
                    transStatus="N";
                if(cResJSON.has("messageType"))
                    messageType=cResJSON.getString("messageType");
            }
        }
        transactionLogger.error("cRes------>"+cRes);
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
                eci = transactionDetailsVO.getEci();
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
                /*if(!functions.isValueNull(cvv))
                {
                    cvv=paymentManager.getCvv(commonValidatorVO);
                    if(functions.isValueNull(cvv))
                        cvv=PzEncryptor.decryptCVV(cvv);
                }
*/
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

                transactionLogger.error("dbStatus---->"+dbStatus);
                if(PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equalsIgnoreCase(dbStatus))
                {
                    notificationUrl=transactionDetailsVO.getNotificationUrl();
                    SafeChargeV2PaymentGateway safeChargeV2PaymentGateway=new SafeChargeV2PaymentGateway(accountId);
                    commTransactionDetailsVO.setAmount(amount);
                    commTransactionDetailsVO.setCurrency(currency);
                    commTransactionDetailsVO.setPreviousTransactionId(paymentid);
                    commTransactionDetailsVO.setCres(cRes);
                    commCardDetailsVO.setCardNum(ccnum);
                    commCardDetailsVO.setExpMonth(expMonth);
                    commCardDetailsVO.setExpYear(expYear);
                    commCardDetailsVO.setcVV(cvv);
                    commAddressDetailsVO.setFirstname(firstName);
                    commAddressDetailsVO.setLastname(lastName);
                    commAddressDetailsVO.setCountry(country);
                    commAddressDetailsVO.setCity(city);
                    commAddressDetailsVO.setState(state);
                    commAddressDetailsVO.setStreet(street);
                    commAddressDetailsVO.setZipCode(zip);
                    commAddressDetailsVO.setEmail(email);
                    commAddressDetailsVO.setCardHolderIpAddress(requestIp);
                    commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                    commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                    commRequestVO.setCardDetailsVO(commCardDetailsVO);
                    commRequestVO.setPaRes(PaRes);
                    if(!"N".equalsIgnoreCase(transStatus))
                    {
                        if ("Y".equalsIgnoreCase(isService) && (!functions.isValueNull(transactionDetailsVO.getTransactionType()) || "DB".equalsIgnoreCase(transactionDetailsVO.getTransactionType())))
                        {
                            actionEntry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, commResponseVO, commRequestVO, auditTrailVO, null);
                            commResponseVO = (CommResponseVO) safeChargeV2PaymentGateway.processCommon3DSaleConfirmation(trackingId, commRequestVO);
                            transType = "Sale";
                        }
                        else
                        {
                            actionEntry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, commResponseVO, commRequestVO, auditTrailVO, null);
                            commResponseVO = (CommResponseVO) safeChargeV2PaymentGateway.processCommon3DAuthConfirmation(trackingId, commRequestVO);
                            transType = "Auth";
                        }
                    }else
                    {
                        commResponseVO=new CommResponseVO();
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark("Authentication Failed");
                        commResponseVO.setDescription("Authentication Failed");
                    }

                    if(commResponseVO!=null)
                    {
                        status=commResponseVO.getStatus();
                        con= Database.getConnection();
                        eci=commResponseVO.getEci();
                        if("success".equalsIgnoreCase(status))
                        {
                            if("Sale".equalsIgnoreCase(transType))
                            {
                                billingDesc=gatewayAccount.getDisplayName();
                                commResponseVO.setDescriptor(billingDesc);
                                message=commResponseVO.getRemark();
                                updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                StringBuffer dbQuery = new StringBuffer("update transaction_common set captureamount=?,status=?,paymentid=?,remark=?,customerIp=?,customerIpCountry=?,eci=?,authorization_code=? where trackingId=?");
                                preparedStatement=con.prepareStatement(dbQuery.toString());
                                preparedStatement.setString(1,amount);
                                preparedStatement.setString(2,updatedStatus);
                                preparedStatement.setString(3,commResponseVO.getTransactionId());
                                preparedStatement.setString(4,message);
                                preparedStatement.setString(5,requestIp);
                                preparedStatement.setString(6,functions.getIPCountryShort(requestIp));
                                preparedStatement.setString(7,commResponseVO.getEci());
                                preparedStatement.setString(8,commResponseVO.getAuthCode());
                                preparedStatement.setString(9,trackingId);
                                preparedStatement.executeUpdate();
                                transactionLogger.error("Update Query-->"+preparedStatement);
                                actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                            }else
                            {
                                billingDesc=gatewayAccount.getDisplayName();
                                commResponseVO.setDescriptor(billingDesc);
                                message=commResponseVO.getRemark();
                                updatedStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                                StringBuffer dbQuery = new StringBuffer("update transaction_common set captureamount=?,status=?,paymentid=?,remark=?,customerIp=?,customerIpCountry=?,eci=?,authorization_code=? where trackingId=?");
                                preparedStatement=con.prepareStatement(dbQuery.toString());
                                preparedStatement.setString(1,amount);
                                preparedStatement.setString(2,updatedStatus);
                                preparedStatement.setString(3,commResponseVO.getTransactionId());
                                preparedStatement.setString(4,message);
                                preparedStatement.setString(5,requestIp);
                                preparedStatement.setString(6,functions.getIPCountryShort(requestIp));
                                preparedStatement.setString(7,commResponseVO.getEci());
                                preparedStatement.setString(8,commResponseVO.getAuthCode());
                                preparedStatement.setString(9,trackingId);
                                preparedStatement.executeUpdate();
                                transactionLogger.error("Update Query-->"+preparedStatement);
                                actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, requestIp);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                            }
                        }else
                        {
                            message=commResponseVO.getRemark();
                            updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                            StringBuffer dbQuery = new StringBuffer("update transaction_common set status=?,remark=?,customerIp=?,customerIpCountry=?,eci=?,authorization_code=? where trackingId=?");
                            preparedStatement=con.prepareStatement(dbQuery.toString());
                            preparedStatement.setString(1,updatedStatus);
                            preparedStatement.setString(2,message);
                            preparedStatement.setString(3,requestIp);
                            preparedStatement.setString(4,functions.getIPCountryShort(requestIp));
                            preparedStatement.setString(5,commResponseVO.getEci());
                            preparedStatement.setString(6,commResponseVO.getAuthCode());
                            preparedStatement.setString(7,trackingId);
                            preparedStatement.executeUpdate();
                            transactionLogger.error("Update Query-->"+preparedStatement);
                            actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, requestIp);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, updatedStatus);
                        }
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
                commonValidatorVO.setEci(eci);

                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setCustomerId(customerId);
                commonValidatorVO.setTerminalId(terminalId);
                transactionUtility.setToken(commonValidatorVO, dbStatus);

                transactionLogger.error("TransactionNotification flag for ---"+toid+"---"+merchantDetailsVO.getTransactionNotification());
                if (functions.isValueNull(notificationUrl) && functions.isValueNull(updatedStatus))
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
            transactionLogger.error("PZDBViolationException--" + trackingId + "-->", e);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException--" + trackingId + "-->", e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("systemError--" + trackingId + "-->", systemError);

        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException" + trackingId + "-->", e);

        }catch (Exception e){
            transactionLogger.error("Exception" + trackingId + "-->", e);

        }
        finally
        {
            Database.closeConnection(con);
        }


    }
}
