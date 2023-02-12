package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

/**
 * Created by Vivek on 5/13/2020.
 */
public class XpateBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(XpateBackEndServlet.class.getName());
    private static Logger log = new Logger(XpateBackEndServlet.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("----------------Inside Xpate BackEnd Servlet-----------------");
        PrintWriter pWriter = response.getWriter();
        String responseCode = "200";
        String responseStatus = "OK";
        StringBuilder responseMsg = new StringBuilder();
        BufferedReader br = request.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            responseMsg.append(str);
        }

        transactionLogger.error("-----Notification JSON-----" + responseMsg);

        TransactionManager transactionManager=new TransactionManager();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();
        Functions functions=new Functions();
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        CommResponseVO commResponseVO=new CommResponseVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO=new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO=new GenericCardDetailsVO();
        CommonValidatorVO commonValidatorVO=new CommonValidatorVO();
        ActionEntry entry = new ActionEntry();
        TransactionUtility transactionUtility=new TransactionUtility();
        MySQLCodec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Connection con=null;

        String transactionId="";
        String type="";
        String trackingId="";
        String status="";
        String toId="";
        String accountId="";
        String amount="";
        String orderDesc="";
        String orderId="";
        String dbStatus="";
        String custEmail="";
        String customerId="";
        String notificationUrl="";
        String terminalId="";
        String clKey="";
        String isService="";
        String billingDesc="";
        String ccnum="";
        String expDate="";
        String expMonth="";
        String expYear="";
        String currency="";
        String updatedStatus="";
        String message="";
        String tmpl_amt="";
        String tmpl_currency="";
        String firstName="";
        String lastName="";
        String payModeId="";
        String cardTypeId="";
        String actionExecutorName="";
        try
        {
            JSONObject jsonObject = new JSONObject(responseMsg.toString());
            if(jsonObject.has("id"))
                transactionId=jsonObject.getString("id");
            if(jsonObject.has("type"))
                type=jsonObject.getString("type");
            if(jsonObject.has("status"))
                status=jsonObject.getString("status");
            if(jsonObject.has("purchase") && !jsonObject.isNull("purchase"))
            {
                if(jsonObject.getJSONObject("purchase").has("products") && !jsonObject.getJSONObject("purchase").isNull("products"))
                {
                    JSONArray products=jsonObject.getJSONObject("purchase").getJSONArray("products");
                    trackingId=products.getJSONObject(0).getString("name");
                }
            }
            if(jsonObject.has("transaction_data") && !jsonObject.isNull("transaction_data"))
            {
                if(jsonObject.getJSONObject("transaction_data").has("attempts") && !jsonObject.getJSONObject("transaction_data").isNull("attempts"))
                {
                    JSONArray attempts=jsonObject.getJSONObject("transaction_data").getJSONArray("attempts");
                    transactionLogger.error("attempts--->"+attempts);
                    if(attempts.length()>0 && attempts.getJSONObject(0).has("error") && !attempts.getJSONObject(0).isNull("error"))
                    {
                        JSONObject error=attempts.getJSONObject(0).getJSONObject("error");
                        if(error.has("message"))
                            message=error.getString("message");
                    }
                }
            }
            transactionLogger.error("XPateBackEndServlet :  trackingId_response ---------->" + trackingId);
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if (transactionDetailsVO != null)
            {
                transactionLogger.error("inside transactionDetailsVO not null ");
                //String actionExecutorName=flutterWaveUtils.getActionExecutorName(trackingId);
                toId = transactionDetailsVO.getToid();
                accountId = transactionDetailsVO.getAccountId();
                amount = transactionDetailsVO.getAmount();
                transactionLogger.error("amount -------" + amount);
                orderId = transactionDetailsVO.getDescription();
                dbStatus = transactionDetailsVO.getStatus();
                custEmail = transactionDetailsVO.getEmailaddr();
                customerId = transactionDetailsVO.getCustomerId();
                notificationUrl = transactionDetailsVO.getNotificationUrl();
                terminalId = transactionDetailsVO.getTerminalId();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                firstName=transactionDetailsVO.getFirstName();
                lastName=transactionDetailsVO.getLastName();
                payModeId = transactionDetailsVO.getPaymodeId();
                cardTypeId = transactionDetailsVO.getCardTypeId();
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                if (merchantDetailsVO != null)
                {
                    clKey = merchantDetailsVO.getKey();
                    isService = merchantDetailsVO.getIsService();
                }
                billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
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

                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toId);
                if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                {
                    orderDesc = transactionDetailsVO.getOrderDescription();
                }
                currency = transactionDetailsVO.getCurrency();
                StringBuffer dbBuffer=new StringBuffer();
                transactionLogger.error("type--->"+type);
                transactionLogger.error("status--->"+status);
                transactionLogger.error("dbStatus--->"+dbStatus);
                if("purchase".equalsIgnoreCase(type))
                {
                    if ((PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) || PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus)) && !"created".equalsIgnoreCase(status))
                    {
                        if("paid".equalsIgnoreCase(status))
                        {
                            if(!functions.isValueNull(message))
                                message="Transaction Successful";
                            if ("Y".equalsIgnoreCase(isService) && (!functions.isValueNull(transactionDetailsVO.getTransactionType()) || "DB".equalsIgnoreCase(transactionDetailsVO.getTransactionType())))
                            {
                                transactionLogger.error("inside transactionStatus success");
                                status = "Successful";
                                commResponseVO.setDescriptor(billingDesc);
                                commResponseVO.setRemark(message);
                                commResponseVO.setDescription(message);
                                dbStatus = "capturesuccess";
                                updatedStatus = "capturesuccess";
                                dbBuffer.append("update transaction_common set captureamount='" + amount + "',status='capturesuccess',remark='" + ESAPI.encoder().encodeForSQL(me,message) + "',paymentid='" + transactionId +"' where trackingid = " + trackingId);
                                con = Database.getConnection();
                                transactionLogger.error("dbBuffer--->"+dbBuffer);
                                Database.executeUpdate(dbBuffer.toString(), con);
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, "");
                            }
                            else
                            {
                                transactionLogger.error("inside transactionStatus success isService N");
                                status = "Successful";
                                commResponseVO.setDescriptor(billingDesc);
                                commResponseVO.setRemark(message);
                                commResponseVO.setDescription(message);
                                dbStatus = "authsuccessful";
                                updatedStatus = "authsuccessful";
                                dbBuffer.append("update transaction_common set amount='" + amount + "',status='authsuccessful',remark='" + ESAPI.encoder().encodeForSQL(me,message) + "',paymentid='" + transactionId +"' where trackingid = " + trackingId);
                                con = Database.getConnection();
                                transactionLogger.error("dbBuffer--->"+dbBuffer);
                                Database.executeUpdate(dbBuffer.toString(), con);
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, "");

                            }
                        }else if("error".equalsIgnoreCase(status))
                        {
                            status = "fail";
                            dbStatus = "authfailed";
                            updatedStatus="authfailed";
                            if(!functions.isValueNull(message))
                                message="Transaction failed";
                            commResponseVO.setRemark(message);
                            commResponseVO.setDescription(message);
                            dbBuffer.append("update transaction_common set amount='" + amount + "',status='authfailed',remark='" + ESAPI.encoder().encodeForSQL(me,message) + "',paymentid='" + transactionId +"' where trackingid = " + trackingId);
                            con = Database.getConnection();
                            transactionLogger.error("dbBuffer--->"+dbBuffer);
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, "");
                        }
                        genericTransDetailsVO.setOrderId(orderId);
                        genericTransDetailsVO.setAmount(amount);
                        genericTransDetailsVO.setCurrency(currency);
                        genericTransDetailsVO.setOrderDesc(orderDesc);
                        genericTransDetailsVO.setNotificationUrl(notificationUrl);
                        genericTransDetailsVO.setBillingDiscriptor(commResponseVO.getDescriptor());

                        addressDetailsVO.setEmail(custEmail);
                        addressDetailsVO.setTmpl_amount(tmpl_amt);
                        addressDetailsVO.setTmpl_currency(tmpl_currency);
                        addressDetailsVO.setFirstname(firstName);
                        addressDetailsVO.setLastname(lastName);
                        cardDetailsVO.setCardNum(ccnum);
                        cardDetailsVO.setExpMonth(expMonth);
                        cardDetailsVO.setExpYear(expYear);
                        commonValidatorVO.setPaymentType(payModeId);
                        commonValidatorVO.setCardType(cardTypeId);
                        commonValidatorVO.setTrackingid(trackingId);

                        commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                        commonValidatorVO.setCustomerId(customerId);
                        commonValidatorVO.setTerminalId(terminalId);
                        commonValidatorVO.setReason(message);
                        commonValidatorVO.setActionType(actionExecutorName); // Used For Vt Issue

                        if (functions.isValueNull(notificationUrl))
                        {
                            transactionLogger.error("inside sending notification---" + notificationUrl);
                            TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, updatedStatus, message, "");
                        }
                    }else
                    {
                        if(functions.isValueNull(message))
                        {
                            dbBuffer.append("update transaction_common set remark='" + ESAPI.encoder().encodeForSQL(me,message) + "' where trackingid = " + trackingId);
                            con = Database.getConnection();
                            transactionLogger.error("dbBuffer--->"+dbBuffer);
                            Database.executeUpdate(dbBuffer.toString(), con);
                        }
                    }
                }
            }
            JSONObject jsonResObject = new JSONObject();
            jsonResObject.put("responseCode", responseCode);
            jsonResObject.put("responseStatus", responseStatus);
            response.setContentType("application/json");
            response.setStatus(200);
            pWriter.println(jsonResObject.toString());
            pWriter.flush();
            return;
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException--->",e);
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException--->", e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError--->", systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
