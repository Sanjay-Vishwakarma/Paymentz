package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.MerchantConfigManager;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.vo.MarketPlaceVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.zotapay.ZotaPayPaymentProcess;
import com.payment.zotapay.ZotaPayUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Admin on 7/18/2018.
 */
public class ZotaPayBackEndServlet extends HttpServlet
{
    private static TransactionLogger transactionLogger= new TransactionLogger(ZotaPayBackEndServlet.class.getName());

    public void doGet(HttpServletRequest request,HttpServletResponse response){
        doService(request,response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response){
        doService(request,response);
    }

    public void doService(HttpServletRequest request,HttpServletResponse response){

        transactionLogger.error("-----Inside ZotaPayBackEndServlet---------");
        Enumeration param=request.getParameterNames();
        while (param.hasMoreElements()){
            String key=(String)param.nextElement();
            String value =request.getParameter(key);

            transactionLogger.error("Key----"+key+"----Value----"+value);
        }

        CommRequestVO commRequestVO= new CommRequestVO();
        CommMerchantVO commMerchantVO= new CommMerchantVO();
        CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO= new CommTransactionDetailsVO();
        CommResponseVO commResponseVO = null;
        TransactionManager transactionManager = new TransactionManager();
        Functions functions = new Functions();
        MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
        MerchantDetailsVO merchantDetailsVO=null;
        StringBuffer dbBuffer = new StringBuffer();
        ActionEntry entry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        MarketPlaceVO marketPlaceVO=new MarketPlaceVO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        List<MarketPlaceVO> marketPlaceVOList=new ArrayList<>();
        auditTrailVO.setActionExecutorName("AcquirerBackEnd");
        Connection con = null;

        try
        {
            String status = "";
            String trackingId = "";
            String transactionId = "";
            String transtype = "";
            String approval_Code = "";

            if (functions.isValueNull(request.getParameter("status")))
                status = request.getParameter("status");
            if (functions.isValueNull(request.getParameter("client_orderid")))
                trackingId = request.getParameter("client_orderid");
            if (functions.isValueNull(request.getParameter("orderid")))
                transactionId = request.getParameter("orderid");
            if (functions.isValueNull(request.getParameter("type")))
                transtype = request.getParameter("type");
            if (functions.isValueNull(request.getParameter("approval-code")))
                approval_Code = request.getParameter("approval-code");

            con = Database.getConnection();
            String _3dStatus=ZotaPayPaymentProcess.getPreviousStatus(trackingId);
            transactionLogger.debug("detailTableStatus-----" + _3dStatus);
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

            if(functions.isValueNull(transactionDetailsVO.getToid()))
                merchantDetailsVO=merchantConfigManager.getMerchantDetailFromToId(transactionDetailsVO.getToid());
            if(merchantDetailsVO != null)
            {
                if (!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()))
                {
                    marketPlaceVOList = transactionManager.getChildDetailsByParentTrackingid(trackingId);
                }
            }

            if (transactionDetailsVO != null)
            {
                String dbStatus = transactionDetailsVO.getStatus();
                String amount = transactionDetailsVO.getAmount();
                String accountId = transactionDetailsVO.getAccountId();
                String ip=transactionDetailsVO.getIpAddress();
                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                String billingDesc=gatewayAccount.getDisplayName();

                commAddressDetailsVO.setCardHolderIpAddress(ip);
                commTransactionDetailsVO.setResponseHashInfo(transactionId);

                String updateStatus = "";
                String remark = status;

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();


                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus) && "3D_authstarted".equalsIgnoreCase(_3dStatus))
                {
                    if(transtype.equalsIgnoreCase("sale")){
                        commMerchantVO.setIsService("Y");
                        commRequestVO.setCommMerchantVO(commMerchantVO);
                        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                        entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, commResponseVO, commRequestVO, auditTrailVO, null);
                        if(!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()) && marketPlaceVOList!=null && marketPlaceVOList.size()>0)
                        {
                            for(int i=0;i<marketPlaceVOList.size();i++)
                            {
                                marketPlaceVO=marketPlaceVOList.get(i);
                                entry.actionEntryFor3DCommon(marketPlaceVO.getTrackingid(), marketPlaceVO.getAmount(), ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, commResponseVO, commRequestVO, auditTrailVO, null);
                            }
                        }
                    }else if(transtype.equalsIgnoreCase("preauth")){
                        commMerchantVO.setIsService("N");
                        commRequestVO.setCommMerchantVO(commMerchantVO);
                        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                        entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, commResponseVO, commRequestVO, auditTrailVO, null);
                        if(!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()) && marketPlaceVOList!=null && marketPlaceVOList.size()>0)
                        {
                            for(int i=0;i<marketPlaceVOList.size();i++)
                            {
                                marketPlaceVO=marketPlaceVOList.get(i);
                                entry.actionEntryFor3DCommon(marketPlaceVO.getTrackingid(), marketPlaceVO.getAmount(), ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, commResponseVO, commRequestVO, auditTrailVO, null);
                            }
                        }
                    }


                    transactionLogger.debug("-------inside authstarted------" + status);
                    dbBuffer.append("update transaction_common set ");
                    StringBuffer childDBBuffer = null;

                    commResponseVO=new CommResponseVO();
                    if (status.equalsIgnoreCase("approved"))
                    {
                        transactionLogger.debug("inside---" + status);
                        if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                        {
                            if(transtype.equalsIgnoreCase("sale")){
                                remark = "Transaction Successful";
                                updateStatus = "capturesuccess";
                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                commResponseVO.setTransactionId(transactionId);
                                commResponseVO.setErrorCode(approval_Code);
                                commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                                commResponseVO.setDescription(status);
                                commResponseVO.setRemark(remark);
                                commResponseVO.setStatus("success");
                                commResponseVO.setDescriptor(billingDesc);
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.CAPTURE_SUCCESS.toString());
                                if(!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()) && marketPlaceVOList!=null && marketPlaceVOList.size()>0)
                                {
                                    for(int i=0;i<marketPlaceVOList.size();i++)
                                    {
                                        marketPlaceVO=marketPlaceVOList.get(i);
                                        childDBBuffer=new StringBuffer();
                                        commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                                        childDBBuffer.append("update transaction_common set captureamount='" + marketPlaceVO.getAmount() + "',status='capturesuccess',paymentid='" + transactionId + "',remark='" + remark + "' where trackingid=" + marketPlaceVO.getTrackingid());
                                        transactionLogger.error("childDBBuffer------------>" + childDBBuffer);
                                        Database.executeUpdate(childDBBuffer.toString(), con);
                                        entry.actionEntryForCommon(marketPlaceVO.getTrackingid(), marketPlaceVO.getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                        statusSyncDAO.updateAllTransactionFlowFlag(marketPlaceVO.getTrackingid(), "capturesuccess");
                                    }
                                }
                                dbBuffer.append("captureamount='"+amount+"',");
                            }else {
                                remark = "Transaction Successful";
                                updateStatus = "authsuccessful";
                                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                                commResponseVO.setTransactionId(transactionId);
                                commResponseVO.setErrorCode(approval_Code);
                                commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                                commResponseVO.setDescription(status);
                                commResponseVO.setRemark(remark);
                                commResponseVO.setStatus("success");
                                commResponseVO.setDescriptor(billingDesc);
                                entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.AUTH_SUCCESS.toString());
                                for(int i=0;i<marketPlaceVOList.size();i++)
                                {
                                    marketPlaceVO=marketPlaceVOList.get(i);
                                    childDBBuffer=new StringBuffer();
                                    commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                                    childDBBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful' ,remark='" + remark + "' where trackingid = " + marketPlaceVO.getTrackingid());
                                    Database.executeUpdate(childDBBuffer.toString(), con);
                                    entry.actionEntryForCommon(marketPlaceVO.getTrackingid(), marketPlaceVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(marketPlaceVO.getTrackingid(), "authsuccessful");
                                }
                            }
                        }
                    }else {
                        remark = "Transaction Failed";
                        updateStatus = "authfailed";
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setErrorCode(approval_Code);
                        commResponseVO.setTransactionType(transtype);
                        commResponseVO.setDescription(status);
                        commResponseVO.setRemark(remark);
                        commResponseVO.setStatus("fail");
                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, PZTransactionStatus.AUTH_FAILED.toString());
                        if(!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()) && marketPlaceVOList!=null && marketPlaceVOList.size()>0)
                        {
                            for(int i=0;i<marketPlaceVOList.size();i++)
                            {
                                marketPlaceVO=marketPlaceVOList.get(i);
                                childDBBuffer=new StringBuffer();
                                childDBBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',remark='" + remark + "' where trackingid = " + marketPlaceVO.getTrackingid());
                                Database.executeUpdate(childDBBuffer.toString(), con);
                                entry.actionEntryForCommon(marketPlaceVO.getTrackingid(), marketPlaceVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                                statusSyncDAO.updateAllTransactionFlowFlag(marketPlaceVO.getTrackingid(), "authfailed");
                            }
                        }
                    }
                    dbBuffer.append("status='" + updateStatus + "',paymentid='" + transactionId + "',remark='" + remark + "' where trackingid="+trackingId+"");
                    transactionLogger.debug("-----dbBuffer-----" + dbBuffer);
                    Database.executeUpdate(dbBuffer.toString(), con);
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, remark, billingDesc);

                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, remark, billingDesc);

                }
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException::::::", e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError:::::", systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
}
