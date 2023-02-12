package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.MerchantConfigManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.sabadell.SabadellPaymentProcess;
import com.payment.sabadell.SabadellUtils;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
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
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Admin on 7/16/18.
 */
public class SabadellFrontEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger = new TransactionLogger(SabadellFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("-----Inside SabadellFrontEndServlet-----");
        Enumeration params = request.getParameterNames();
        while (params.hasMoreElements())
        {
            String key = (String) params.nextElement();
            String value = request.getParameter(key);

            transactionLogger.debug("key-----" + key + "-----value-----" + value);

        }
        HttpSession session=request.getSession(true);
        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        MerchantDAO merchantDAO= new MerchantDAO();
        CommResponseVO transRespDetails = null;


        MerchantDetailsVO merchantDetailsVO = null;
        Connection con = null;

        String toId = "";
        String payModeId = "";
        String cardTypeId = "";
        String isService = "";
        String accountId = "";
        String status = "";
        String responceStatus = "";
        String amount = "";
        String description = "";
        String orderDescription = "";
        String redirectUrl = "";
        String clKey = "";
        String checksumNew = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String confirmStatus = "";
        String isPowerBy = "";
        String firstName = "";
        String lastName = "";
        String tmpl_Amount = "";
        String tmpl_Currency = "";
        String ccnum = "";
        String currency = "";
        String billingDesc = "";
        String message = "";
        String email = "";
        String dbStatus = "";

        String transactionId = "";
        String transactionStatus = "";
        String customerId = "";
        String eci = "";
        String trackingId="";




        Functions functions = new Functions();
        SabadellUtils sabadellUtils= new SabadellUtils();
        String Ds_SignatureVersion="";
        String Ds_MerchantParameters="";
        String Ds_Signature="";

        if(functions.isValueNull(request.getParameter("Ds_SignatureVersion")))
            Ds_SignatureVersion=request.getParameter("Ds_SignatureVersion");
        if(functions.isValueNull(request.getParameter("Ds_MerchantParameters")))
            Ds_MerchantParameters=request.getParameter("Ds_MerchantParameters");
        if(functions.isValueNull(request.getParameter("Ds_Signature")))
            Ds_Signature=request.getParameter("Ds_Signature");

        transactionLogger.debug("Ds_Signature-----"+Ds_Signature);

        try
        {
            String Ds_Response = "";
            String Ds_TransactionType = "";
            String Ds_AuthorisationCode = "";
            String Ds_Terminal = "";
            String Ds_Date = "";
            String Ds_MerchantData = "";
            status = "fail";
            String remark = "";
            String decodedParams = sabadellUtils.decodeMerchantParameters(Ds_MerchantParameters);
            JSONObject jsonObject = new JSONObject(decodedParams);
            if (jsonObject != null)
            {
                if (jsonObject.has("Ds_Response"))
                {
                    Ds_Response = jsonObject.getString("Ds_Response");
                    if ("0000".equals(Ds_Response) || "0099".equalsIgnoreCase(Ds_Response))
                    {
                        status = "success";
                    }
                }
                if (jsonObject.has("Ds_TransactionType"))
                {
                    Ds_TransactionType = jsonObject.getString("Ds_TransactionType");
                }
                if (jsonObject.has("Ds_AuthorisationCode"))
                {
                    Ds_AuthorisationCode = jsonObject.getString("Ds_AuthorisationCode");
                }
                if (jsonObject.has("Ds_Terminal"))
                {
                    Ds_Terminal = jsonObject.getString("Ds_Terminal");
                }
                if (jsonObject.has("Ds_Order"))
                {
                    trackingId = jsonObject.getString("Ds_Order");
                }
                if (jsonObject.has("Ds_Date"))
                {
                    Ds_Date = jsonObject.getString("Ds_Date");
                }
                if (jsonObject.has("Ds_MerchantData"))
                {
                    Ds_MerchantData = jsonObject.getString("Ds_MerchantData");
                }

                SabadellPaymentProcess paymentProcess = new SabadellPaymentProcess();
                CommRequestVO commRequestVO = new CommRequestVO();

                paymentProcess.setSabadellRequestVO(commRequestVO, trackingId);

                CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
                description = transactionDetailsVO.getOrderId();
                orderDescription = transactionDetailsVO.getOrderDesc();
                amount = transactionDetailsVO.getAmount();
                currency = transactionDetailsVO.getCurrency();
                toId = transactionDetailsVO.getToId();
                payModeId = transactionDetailsVO.getPaymentType();
                cardTypeId = transactionDetailsVO.getCardType();
                redirectUrl = transactionDetailsVO.getRedirectUrl();
                dbStatus = transactionDetailsVO.getPrevTransactionStatus();

                CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
                email = commAddressDetailsVO.getEmail();
                if (functions.isValueNull(commAddressDetailsVO.getFirstname()))
                    firstName = commAddressDetailsVO.getFirstname();
                if (functions.isValueNull(commAddressDetailsVO.getLastname()))
                    lastName = commAddressDetailsVO.getLastname();
                tmpl_Amount = commAddressDetailsVO.getTmpl_amount();
                tmpl_Currency = commAddressDetailsVO.getTmpl_currency();
                if (functions.isValueNull(commAddressDetailsVO.getCustomerid()))
                    customerId = commAddressDetailsVO.getCustomerid();
                CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
                ccnum = commCardDetailsVO.getCardNum();
                CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
                accountId = commMerchantVO.getAccountId();

                CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
                commTransactionDetailsVO.setPreviousTransactionId(transactionId);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);

                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toId);

                if (merchantDetailsVO != null)
                {
                    clKey = merchantDetailsVO.getKey();
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                    isService = merchantDetailsVO.getIsService();
                }

                commMerchantVO.setIsService(isService);
                commRequestVO.setCommMerchantVO(commMerchantVO);
                String transType = "Sale";
                transactionLogger.debug("dbStatus-----" + dbStatus);
                transactionLogger.debug("key------"+gatewayAccount.getFRAUD_FTP_USERNAME());
                String calculatedSign = sabadellUtils.createMerchantSignatureNotif(gatewayAccount.getFRAUD_FTP_USERNAME(), Ds_MerchantParameters);
                transactionLogger.debug("calculatedSign-----"+calculatedSign);
                try
                {
                    if (calculatedSign.equalsIgnoreCase(Ds_Signature))
                    {
                        if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && Ds_Terminal.contains("1"))
                        {

                            if ("N".equals(isService))
                            {
                                entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, null);
                                transType = "Auth";
                            }
                            else
                            {
                                entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, commRequestVO, auditTrailVO, null);
                            }

                            transRespDetails = new CommResponseVO();
                            if ("success".equalsIgnoreCase(status))
                            {
                                transRespDetails.setStatus("success");
                                transRespDetails.setDescriptor(gatewayAccount.getDisplayName());
                                if (functions.isValueNull(Ds_MerchantData))
                                {
                                    remark = Ds_MerchantData;
                                }
                                else
                                {
                                    remark = "Transaction Successful";
                                }
                            }
                            else
                            {
                                remark = "Transaction Failed";
                                transRespDetails.setStatus(status);
                            }
                            transRespDetails.setTransactionId(transactionId);
                            transRespDetails.setAuthCode(Ds_AuthorisationCode);
                            transRespDetails.setRemark(remark);
                            transRespDetails.setDescription(status);
                            transRespDetails.setBankTransactionDate(URLDecoder.decode(Ds_Date));


                            if (transRespDetails != null)
                            {
                                transactionStatus = transRespDetails.getStatus();
                                transactionId = transRespDetails.getTransactionId();
                                message = transRespDetails.getDescription();
                                eci = transRespDetails.getEci();
                                transactionLogger.error("eci--------" + eci);

                            }
                            transactionLogger.debug("Remark------" + transRespDetails.getDescription());


                            StringBuffer dbBuffer = new StringBuffer();
                            if ("success".equals(transactionStatus))
                            {
                                status = "success";
                                confirmStatus = "Y";
                                responceStatus = "Successful";
                                billingDesc = gatewayAccount.getDisplayName();
                                if ("Sale".equalsIgnoreCase(transType))
                                {
                                    transRespDetails.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                                    dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess' ,eci='" + eci + "'");
                                    paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                }
                                else
                                {
                                    transRespDetails.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                                    dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful' ,eci='" + eci + "'");
                                    paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                                }
                            }
                            else
                            {
                                confirmStatus = "N";
                                status = "fail";
                                responceStatus = "Failed(" + message + ")";
                                dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',eci='" + eci + "'");
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                            }
                            dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            transactionLogger.debug("-----dbBuffer-----" + dbBuffer);
                            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                            AsynchronousSmsService smsService = new AsynchronousSmsService();
                            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                        }
                        else
                        {
                            if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                            {
                                billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                                status = "success";
                                message = "Transaction Successful";
                                responceStatus = "Successful";
                            }
                            else
                            {
                                status = "fail";
                                message = "Transaction Declined";
                                responceStatus = "Failed";
                            }
                        }
                        commonValidatorVO.setTrackingid(trackingId);
                        genericTransDetailsVO.setOrderId(description);
                        genericTransDetailsVO.setOrderDesc(orderDescription);
                        genericTransDetailsVO.setAmount(amount);
                        genericTransDetailsVO.setCurrency(currency);

                        genericTransDetailsVO.setRedirectUrl(redirectUrl);
                        commonValidatorVO.setLogoName(logoName);
                        commonValidatorVO.setPartnerName(partnerName);
                        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                        commonValidatorVO.setPaymentType(payModeId);
                        commonValidatorVO.setCardType(cardTypeId);
                        if (functions.isValueNull(email))
                            addressDetailsVO.setEmail(email);
                        if (functions.isValueNull(firstName))
                            addressDetailsVO.setFirstname(firstName);

                        if (functions.isValueNull(lastName))
                            addressDetailsVO.setLastname(lastName);
                        addressDetailsVO.setTmpl_amount(tmpl_Amount);
                        addressDetailsVO.setTmpl_currency(tmpl_Currency);
                        cardDetailsVO.setCardNum(ccnum);

                        commonValidatorVO.setCustomerId(customerId);
                        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                        commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                        commonValidatorVO.setEci(eci);

                        if ("Y".equalsIgnoreCase(autoRedirect))
                        {
                            transactionUtility.doAutoRedirect(commonValidatorVO, response, responceStatus, billingDesc);
                        }
                        else
                        {
                            request.setAttribute("responceStatus", responceStatus);
                            request.setAttribute("displayName", billingDesc);
                            request.setAttribute("remark", message);
                            request.setAttribute("transDetail", commonValidatorVO);
                            session.setAttribute("ctoken", ctoken);
                            String confirmationPage = "";
                            String version = (String)session.getAttribute("version");
                            if(functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                                confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                            else
                                confirmationPage = "/confirmationpage.jsp?ctoken=";

                            RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                            rd.forward(request, response);
                            session.invalidate();
                        }
                    }else{
                        transactionLogger.error("------Signature Mis-matched-----");
                    }
                }
                catch (SystemError se)
                {
                    transactionLogger.error("SystemError::::::", se);
                    PZExceptionHandler.raiseAndHandleDBViolationException("SabdellFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause(), toId, null);
                }
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("SabdellFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (Exception e)
        {
           transactionLogger.error("Exception-----",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
