package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.sabadell.SabadellUtils;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import org.codehaus.jettison.json.JSONObject;
import org.json.JSONException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Admin on 7/21/18.
 */
public class SabadellBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger= new TransactionLogger(SabadellBackEndServlet.class.getName());

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        doService(request,response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
        doService(request,response);
    }

    public void doService(HttpServletRequest request,HttpServletResponse response) throws  ServletException,IOException{
        transactionLogger.error("-------Inside SabadellBackEndServlet------");
        for (Object key:request.getParameterMap().keySet()){
            transactionLogger.debug("key-----"+key+"="+request.getParameter((String)key));
        }

        Functions functions= new Functions();
        Connection con=null;
        StringBuffer sb=new StringBuffer();
        CommRequestVO commRequestVO= new CommRequestVO();
        CommMerchantVO commMerchantVO= new CommMerchantVO();
        CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        CommResponseVO commResponseVO = null;
        TransactionManager transactionManager = new TransactionManager();
        ActionEntry entry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        auditTrailVO.setActionExecutorName("AcquirerBackEnd");
        SabadellUtils sabadellUtils= new SabadellUtils();
        String Ds_SignatureVersion="";
        String Ds_MerchantParameters="";
        String Ds_Signature="";

        PrintWriter pWriter = response.getWriter();
        String responseCode = "200";
        String responseStatus = "OK";
        String notificationUrl="";

        if(functions.isValueNull(request.getParameter("Ds_SignatureVersion")))
            Ds_SignatureVersion=request.getParameter("Ds_SignatureVersion");
        if(functions.isValueNull(request.getParameter("Ds_MerchantParameters")))
            Ds_MerchantParameters=request.getParameter("Ds_MerchantParameters");
        if(functions.isValueNull(request.getParameter("Ds_Signature")))
            Ds_Signature=request.getParameter("Ds_Signature");

        try
        {
            String trackingId="";
            String Ds_Response="";
            String Ds_TransactionType="";
            String Ds_AuthorisationCode="";
            String Ds_Terminal="";
            String Ds_Date="";
            String Ds_MerchantData="";
            String status="fail";
            String updateStatus="";
            String remark="";
            String decodedParams = sabadellUtils.decodeMerchantParameters(Ds_MerchantParameters);
            JSONObject jsonObject= new JSONObject(decodedParams);
            if(jsonObject!=null)
            {
                if (jsonObject.has("Ds_Response"))
                {
                    Ds_Response = jsonObject.getString("Ds_Response");
                    if ("0000".equals(Ds_Response) || "0099".equalsIgnoreCase(Ds_Response) || "900".equalsIgnoreCase(Ds_Response) || "0900".equalsIgnoreCase(Ds_Response) || "0400".equalsIgnoreCase(Ds_Response) || "400".equalsIgnoreCase(Ds_Response))
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


                transactionLogger.debug("Ds_Response----"+Ds_Response+"----TransactionType---"+Ds_TransactionType+"----Terminal---"+Ds_Terminal);
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

                    if (transactionDetailsVO != null)
                    {
                        String dbStatus = transactionDetailsVO.getStatus();
                        String amount = transactionDetailsVO.getAmount();
                        String accountId = transactionDetailsVO.getAccountId();
                        String ip = transactionDetailsVO.getIpAddress();
                        notificationUrl=transactionDetailsVO.getNotificationUrl();
                        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                        String billingDesc = gatewayAccount.getDisplayName();
                        String key = gatewayAccount.getFRAUD_FTP_USERNAME();
                        commAddressDetailsVO.setCardHolderIpAddress(ip);

                        String calculatedSign = sabadellUtils.createMerchantSignatureNotif(key, Ds_MerchantParameters);

                        String transType = sabadellUtils.getTranstype(Ds_TransactionType);
                        transactionLogger.error("dbStatus-----" + dbStatus + "-----transType---" + transType);
                        if (calculatedSign.equalsIgnoreCase(Ds_Signature))
                        {
                            transactionLogger.error("----signature matched----");
                            if(dbStatus.equals(PZTransactionStatus.AUTH_STARTED.toString())){
                                sabadellUtils.insertSabadellDetails(trackingId,status,Ds_TransactionType,Ds_Terminal,Ds_Date,Ds_AuthorisationCode);
                            }else{
                                sabadellUtils.updateSabadellDetails(trackingId,status,Ds_TransactionType,Ds_Terminal,Ds_Date,Ds_AuthorisationCode);
                            }

                            con = Database.getConnection();
                            sb.append("update transaction_common set ");
                            if(Ds_Terminal.contains("1") && dbStatus.equals(PZTransactionStatus.AUTH_STARTED.toString())){
                                if(transType.equalsIgnoreCase(PZProcessType.SALE.toString())){
                                    commMerchantVO.setIsService("Y");
                                    commRequestVO.setCommMerchantVO(commMerchantVO);
                                    commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                                    commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                                    entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, commResponseVO, commRequestVO, auditTrailVO, null);
                                }else if(transType.equalsIgnoreCase(PZProcessType.AUTH.toString())){
                                    commMerchantVO.setIsService("N");
                                    commRequestVO.setCommMerchantVO(commMerchantVO);
                                    commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                                    commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                                    entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, commResponseVO, commRequestVO, auditTrailVO, null);
                                }
                            }
                            commResponseVO=new CommResponseVO();

                                if (Ds_Terminal.contains("1") && dbStatus.equals(PZTransactionStatus.AUTH_STARTED.toString()))
                                {
                                    if ("success".equalsIgnoreCase(status))
                                    {

                                    if (PZProcessType.SALE.toString().equalsIgnoreCase(transType))
                                    {
                                        updateStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                        if (functions.isValueNull(Ds_MerchantData))
                                        {
                                            remark = Ds_MerchantData;
                                        }
                                        else
                                        {
                                            remark = "Transaction Successful";
                                        }
                                        commResponseVO.setStatus(status);
                                        commResponseVO.setRemark(remark);
                                        commResponseVO.setDescription("Successful");
                                        commResponseVO.setDescriptor(billingDesc);
                                        commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                                        commResponseVO.setTransactionId(trackingId);
                                        commResponseVO.setAuthCode(Ds_AuthorisationCode);
                                        commResponseVO.setBankTransactionDate(URLDecoder.decode(Ds_Date));
                                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.CAPTURE_SUCCESS.toString());
                                        sb.append("captureamount='"+amount+"',");
                                    }
                                    else if (PZProcessType.AUTH.toString().equalsIgnoreCase(transType))
                                    {
                                        updateStatus = PZTransactionStatus.AUTH_SUCCESS.toString();
                                        if (functions.isValueNull(Ds_MerchantData))
                                        {
                                            remark = Ds_MerchantData;
                                        }
                                        else
                                        {
                                            remark = "Transaction Successful";
                                        }
                                        commResponseVO.setStatus(status);
                                        commResponseVO.setRemark(remark);
                                        commResponseVO.setDescription("Successful");
                                        commResponseVO.setDescriptor(billingDesc);
                                        commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                                        commResponseVO.setTransactionId(trackingId);
                                        commResponseVO.setAuthCode(Ds_AuthorisationCode);
                                        commResponseVO.setBankTransactionDate(URLDecoder.decode(Ds_Date));
                                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.AUTH_SUCCESS.toString());
                                    }
                                }else
                                    {
                                        updateStatus = PZTransactionStatus.AUTH_FAILED.toString();
                                        if (functions.isValueNull(Ds_MerchantData))
                                        {
                                            remark = Ds_MerchantData;
                                        }
                                        else
                                        {
                                            remark = "Transaction Failed";
                                        }
                                        commResponseVO.setStatus(status);
                                        commResponseVO.setRemark(remark);
                                        commResponseVO.setDescription("Failed");
                                        commResponseVO.setTransactionType(transType);
                                        commResponseVO.setTransactionId(trackingId);
                                        commResponseVO.setAuthCode(Ds_AuthorisationCode);
                                        commResponseVO.setBankTransactionDate(URLDecoder.decode(Ds_Date));
                                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, commResponseVO, auditTrailVO, null);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.AUTH_FAILED.toString());
                                    }
                                    sb.append("status='" + updateStatus + "',paymentid='" + trackingId + "',remark='" + remark + "' where trackingid=" + trackingId + "");
                                    transactionLogger.debug("-----dbBuffer-----" + sb.toString());
                                    Database.executeUpdate(sb.toString(), con);
                                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, remark, billingDesc);

                                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, remark, billingDesc);
                                    transactionLogger.debug("---------------------done--------------");

                                    if (functions.isValueNull(notificationUrl))
                                    {
                                        transactionLogger.error("inside sending notification---" + notificationUrl);
                                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updateStatus, remark,"");
                                    }

                                    org.json.JSONObject jsonResObject = new org.json.JSONObject();
                                    jsonResObject.put("responseCode", responseCode);
                                    jsonResObject.put("responseStatus", responseStatus);
                                    response.setContentType("application/json");
                                    response.setStatus(200);
                                    pWriter.println(jsonResObject.toString());
                                    pWriter.flush();
                                    return;
                                }
                        }
                        else
                        {
                            transactionLogger.error("------signature not matched-----");
                        }
                    }
                }
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
