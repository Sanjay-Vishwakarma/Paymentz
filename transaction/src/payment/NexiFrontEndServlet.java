package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.MerchantConfigManager;
import com.manager.TransactionManager;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.nexi.NexiPaymentGateway;
import com.payment.nexi.NexiPaymentProcess;
import com.payment.nexi.NexiUtils;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
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
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by Admin on 5/24/2019.
 */
//@WebServlet(name = "NexiFrontEndServlet")
public class NexiFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(NexiFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    //private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.nexi");
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        /*Enumeration enumeration=req.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String key = (String) enumeration.nextElement();
            String value = req.getParameter(key);

            transactionLogger.error("Name=" + key + "-----Value=" + value);
        }*/

        Date date4 = new Date();
        transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction start #########" + date4.getTime());
        HttpSession session = req.getSession(true);
        Functions functions=new Functions();
        TransactionManager transactionManager=new TransactionManager();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommResponseVO transRespDetails = null;
        CommonValidatorVO commonValidatorVO=new CommonValidatorVO();
        GenericAddressDetailsVO addressDetailsVO=new GenericAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO=new GenericTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        TransactionUtility transactionUtility=new TransactionUtility();
        NexiPaymentProcess paymentProcess = new NexiPaymentProcess();
        Connection con=null;
        ActionEntry entry = new ActionEntry();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        String values=req.getParameter("trackingId");
        String value[]=null;
        String trackingid="";
        String redirectMethod="";
        String esito = "";//message either OK/KO
        if(values.contains("?"))
        {
            value = values.split("\\?");
            trackingid=value[0];
            if(value[1].contains("="))
                esito=value[1].split("=")[1];
        }
        else
        {
            trackingid=values;
            values=req.getParameter("method");
            if(values.contains("?"))
            {
                value = values.split("\\?");
                redirectMethod=value[0];
                if(value[1].contains("="))
                    esito=value[1].split("=")[1];
            }
        }
        String idOperazione = req.getParameter("idOperazione");//Operation id
        String xpayNonce = req.getParameter("xpayNonce");
        String timeStamp = req.getParameter("timeStamp");
        String mac = req.getParameter("mac");
        //JSONObject json1 = new JSONObject();
        CommRequestVO commRequestVO=null;
        String toid="";
        String description="";
        String redirectUrl="";
        String accountId="";
        String orderDesc="";
        String currency="";
        String amount="";
        String amount1="";
        String tmpl_amt="";
        String tmpl_currency="";
        String firstName="";
        String lastName="";
        String paymodeid="";
        String cardtypeid="";
        String custId="";
        String paymentid="";
        String dbStatus="";
        String notificationUrl="";
        String clKey="";
        String logoName="";
        String partnerName="";
        String autoRedirect="";
        String version="";
        String confirmationPage = "";
        String billingDesc="";
        String status="";
        String message="";
        String isService = "";
        String transactionStatus = "";
        String transactionId = "";
        String confirmStatus = "";
        String responseStatus = "";
        String responseString = "";
        String custEmail = "";
        String terminalId = "";
        String ccnum = "";
        String eci = "";
        String requestIp=Functions.getIpAddress(req);
        try
        {
            transactionLogger.error("trackingId---------->"+trackingid);
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);

            if (transactionDetailsVO != null)
            {
                toid = transactionDetailsVO.getToid();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                accountId = transactionDetailsVO.getAccountId();
                orderDesc = transactionDetailsVO.getOrderDescription();
                currency = transactionDetailsVO.getCurrency();
                if(transactionDetailsVO.getAmount().contains("."))
                amount = transactionDetailsVO.getAmount().replace(".","");
                amount1=transactionDetailsVO.getAmount();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                firstName = transactionDetailsVO.getFirstName();
                lastName = transactionDetailsVO.getLastName();
                paymodeid = transactionDetailsVO.getPaymodeId();
                cardtypeid = transactionDetailsVO.getCardTypeId();
                custEmail = transactionDetailsVO.getEmailaddr();
                custId = transactionDetailsVO.getCustomerId();
                paymentid = transactionDetailsVO.getPaymentId();
                dbStatus=transactionDetailsVO.getStatus();
                notificationUrl=transactionDetailsVO.getNotificationUrl();
                terminalId = transactionDetailsVO.getTerminalId();
                tmpl_amt=transactionDetailsVO.getTemplateamount();
                tmpl_currency=transactionDetailsVO.getTemplatecurrency();
                version=transactionDetailsVO.getVersion();
                ccnum=transactionDetailsVO.getCcnum();
                eci=transactionDetailsVO.getEci();
                NexiPaymentGateway nexiPaymentGateway=new NexiPaymentGateway(accountId);
                GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
                MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
                merchantDetailsVO = merchantConfigManager.getMerchantDetailFromToId(toid);
                StringBuffer dbBuffer = new StringBuffer();
                transactionId = idOperazione;
                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toid);
                if (merchantDetailsVO != null)
                {
                    clKey = merchantDetailsVO.getKey();
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                    isService = merchantDetailsVO.getIsService();
                }
                con = Database.getConnection();
                if("OK".equalsIgnoreCase(esito))
                {
                    Hashtable dataHash = NexiUtils.getValuesFromDb(accountId);

                    String apikey = (String) dataHash.get("3d_api_key");
                    String secureKey = (String) dataHash.get("3d_mac");
                    ;
                    String isRecurring = gatewayAccount.get_3DSupportAccount();

                    String stringaMac2 = "esito=" + esito + "idOperazione=" + idOperazione + "xpayNonce=" + xpayNonce + "timeStamp=" + timeStamp + secureKey;
                    String macCalculated = NexiUtils.hashMac(stringaMac2);
                    if (!macCalculated.equalsIgnoreCase(mac))
                    {
                        transactionLogger.error("Invalid Mac :" + macCalculated);
                        status = "failed";
                        message = "Invalid Authentication";

                        transRespDetails = new CommResponseVO();
                        status = "failed";
                        confirmStatus = "N";
                        responseStatus = "Failed(" + message + ")";
                        transRespDetails.setStatus(status);
                        transRespDetails.setDescriptor(billingDesc);
                        transRespDetails.setRemark(message);
                        dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',remark='" + message + "',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' where trackingid = " + trackingid);
                        transactionLogger.error("update query------------>" + dbBuffer.toString());
                        Database.executeUpdate(dbBuffer.toString(), con);
                        entry.actionEntryForCommon(trackingid, amount1, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, requestIp);

                    }
                    else
                    {
                        commRequestVO = new CommRequestVO();
                        paymentProcess.setNexiRequestVO(commRequestVO, trackingid, amount, currency, xpayNonce);

                        entry.actionEntryFor3DCommon(trackingid, amount1, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, requestIp);
                        transRespDetails = (CommResponseVO) nexiPaymentGateway.processCommon3DSaleConfirmation(trackingid, commRequestVO);

                        transactionId = transRespDetails.getTransactionId();
                        if ("success".equalsIgnoreCase(transRespDetails.getStatus()))
                        {
                            status = "success";
                            confirmStatus = "Y";
                            responseStatus = "Successful";
                            billingDesc = gatewayAccount.getDisplayName();
                            message = "Transaction Successful";
                            transRespDetails.setStatus(status);
                            transRespDetails.setDescriptor(billingDesc);
                            transRespDetails.setRemark(message);

                            dbBuffer.append("update transaction_common set captureamount='" + amount1 + "',paymentid='" + transactionId + "',status='capturesuccess',remark='" + message + "',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' where trackingid = " + trackingid);
                            entry.actionEntryForCommon(trackingid, amount1, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, requestIp);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingid, "capturesuccess");

                    /*else
                    {
                        *//*dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful'");
                        paymentProcess.actionEntry(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingid,"authsuccessful");*//*
                    }*/
                            transactionLogger.error("update query------------>" + dbBuffer.toString());
                            Database.executeUpdate(dbBuffer.toString(), con);

                            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), status, message, billingDesc);

                            AsynchronousSmsService smsService = new AsynchronousSmsService();
                            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), status, message, billingDesc);
                        }
                        else
                        {
                            status = "failed";
                            transactionLogger.error("message-------->" + transRespDetails.getErrorName());
                            message = transRespDetails.getErrorName();
                            confirmStatus = "N";
                            responseStatus = "Failed(" + message + ")";
                            transRespDetails.setStatus(status);
                            transRespDetails.setDescriptor(billingDesc);
                            transRespDetails.setRemark(message);
                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',remark='" + message + "',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' where trackingid = " + trackingid);
                            transactionLogger.error("update query------------>" + dbBuffer.toString());
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingid, amount1, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, requestIp);
                        }

                    }
                }
                else
                {
                    status = "failed";
                    transRespDetails = new CommResponseVO();
                    message = req.getParameter("messaggio");
                    confirmStatus = "N";
                    responseStatus = "Failed(" + message + ")";
                    transRespDetails.setStatus(status);
                    transRespDetails.setDescriptor(billingDesc);
                    transRespDetails.setRemark(message);
                    dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',remark='" + message + "',customerIp='" + requestIp + "',customerIpCountry='" + functions.getIPCountryShort(requestIp) + "' where trackingid = " + trackingid);
                    transactionLogger.error("update query------------>" + dbBuffer.toString());
                    Database.executeUpdate(dbBuffer.toString(), con);
                    entry.actionEntryForCommon(trackingid, amount1, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, requestIp);
                }
                genericTransDetailsVO.setAmount(amount1);
                genericTransDetailsVO.setCurrency(currency);
                genericTransDetailsVO.setOrderDesc(orderDesc);
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
                genericTransDetailsVO.setOrderId(description);
                genericTransDetailsVO.setNotificationUrl(notificationUrl);
                genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                genericTransDetailsVO.setRedirectMethod(redirectMethod);
                addressDetailsVO.setEmail(custEmail);
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);

                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                commonValidatorVO.setCustomerId(custId);
                commonValidatorVO.setTerminalId(terminalId);
                commonValidatorVO.setLogoName(logoName);
                commonValidatorVO.setPartnerName(partnerName);
                commonValidatorVO.setPaymentType(paymodeid);
                commonValidatorVO.setCardType(cardtypeid);
                commonValidatorVO.setTrackingid(trackingid);
                commonValidatorVO.setStatus(message);

                if (functions.isValueNull(custEmail)){
                    addressDetailsVO.setEmail(custEmail);}
                if (functions.isValueNull(firstName)){
                    addressDetailsVO.setFirstname(firstName);}

                if (functions.isValueNull(lastName)){
                    addressDetailsVO.setLastname(lastName);}
                addressDetailsVO.setTmpl_amount(tmpl_amt);
                addressDetailsVO.setTmpl_currency(tmpl_currency);
                cardDetailsVO.setCardNum(PzEncryptor.decryptPAN(ccnum));

                commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                commonValidatorVO.setEci(eci);
                transactionUtility.setToken(commonValidatorVO, status);
                if (functions.isValueNull(notificationUrl))
                {
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                    transactionDetailsVO1.setTransactionMode("3D");
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO1, trackingid, status, message, "");
                }
                transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction end #########" + new Date().getTime());
                transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction diff #########" + (new Date().getTime() - date4.getTime()));
                if ("Y".equalsIgnoreCase(autoRedirect))
                {
                    transactionUtility.doAutoRedirect(commonValidatorVO, res, status, billingDesc);
                }
                else
                {
                    session.setAttribute("ctoken", ctoken);
                    req.setAttribute("transDetail", commonValidatorVO);
                    req.setAttribute("responceStatus", responseStatus);
                    req.setAttribute("remark", message);
                    req.setAttribute("displayName", GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                    else
                        confirmationPage = "/confirmationpage.jsp?ctoken=";
                    RequestDispatcher rd = req.getRequestDispatcher(confirmationPage + ctoken);
                    rd.forward(req, res);
                }

            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---->"+e);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception---->"+e);
        }
        finally
        {
            Database.closeConnection(con);
        }


    }
}
