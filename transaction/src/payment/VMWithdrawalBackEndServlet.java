package payment;

import com.directi.pg.*;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommRequestVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.vouchermoney.VoucherMoneyPaymentProcess;
import com.payment.vouchermoney.VoucherMoneyResponse;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

/**
 * Created Nikita on 7/19/2017.
 */
public class VMWithdrawalBackEndServlet extends PzServlet
{
    private VoucherMoneyLogger transactionLogger = new VoucherMoneyLogger(VMWithdrawalBackEndServlet.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doService(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doService(request,response);
    }
    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.error("Entering doPost in VMWithdrawalBackEndServlet::::"+request.getRemoteAddr());

        PrintWriter pWriter = response.getWriter();
        TransactionManager transactionManager = new TransactionManager();
        StringBuilder responseMsg = new StringBuilder();
        Connection con = null;

        String referenceId = "";


        String responseMessage="";
        String userMessage="";
        String outcome="";
        String responseCode="200";
        String responseStatus="OK";
        int notificationCount = 0;

        String status="";

        String successMessage = "";
        String errorMessage = "";
        String voucherGenerated = "";
        String voucherAmount = "";
        String voucherCurrency = "";
        String merchantUserCommission = "";
        String commissionCurrency = "";

        Functions functions = new Functions();

        ActionEntry entry = new ActionEntry();
        String fStatus = "";
        //String nStatus = "";
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        auditTrailVO.setActionExecutorName("AcquirerBackEnd");

        //VoucherMoneyResponse voucherMoneyResponse = new VoucherMoneyResponse();
        try
        {
            BufferedReader br = request.getReader();
            String str;
            while((str = br.readLine()) != null ){
                responseMsg.append(str);
            }

            transactionLogger.error("-----Withdrawal response json-----"+responseMsg);
            JSONObject jsonObject = new JSONObject(responseMsg.toString());

            if(jsonObject!=null)
            {
                referenceId = jsonObject.getString("paymentReference");//trackingid
                //customerId = jsonObject.getString("customerId");
                voucherAmount = jsonObject.getString("newVoucherAmount");//amount
                voucherCurrency = jsonObject.getString("newVoucherCurrency");//currency
                voucherGenerated = jsonObject.getString("newVoucherGenerated");//success / fail
                successMessage = jsonObject.getString("successMessage");//remark / description
                errorMessage = jsonObject.getString("errorMessage");//remark / description
                merchantUserCommission = jsonObject.getString("merchantUsersCommission");
                commissionCurrency = jsonObject.getString("commissionCurrency");//

                //customerAmount = jsonObject.getString("amountInCustomerCurrency");
                //customerCurrency = jsonObject.getString("customerCurrency");
            }
            //transactionLogger.error("referenceId---" + referenceId + "--customerId---" + customerId + "--amount---" + amount1 + "--currency---" + currency + "--customerAmount---" + customerAmount + "--customerCurrency---" + customerCurrency);
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(referenceId);


            status = transactionDetailsVO.getStatus();
            //baseCurrency=transactionDetailsVO.getCurrency();
            int actionEntry=0;
            PaymentManager paymentManager = new PaymentManager();
            CommRequestVO commRequestVO = new CommRequestVO();
            VoucherMoneyResponse transRespDetails = new VoucherMoneyResponse();
            VoucherMoneyPaymentProcess voucherMoneyPaymentProcess = new VoucherMoneyPaymentProcess();

            if(PZTransactionStatus.PAYOUT_STARTED.toString().equals(status))
            {
                transactionLogger.error("voucherGenerated---"+voucherGenerated);

                transRespDetails.setAmount(voucherAmount);
                transRespDetails.setCurrency(voucherCurrency);
                transRespDetails.setTmpl_Amount(transactionDetailsVO.getTemplateamount());
                transRespDetails.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                transRespDetails.setMerchantUsersCommission(merchantUserCommission);
                transRespDetails.setCommissionCurrency(commissionCurrency);
                if(voucherGenerated.equalsIgnoreCase("true") && functions.isValueNull(successMessage))
                {
                    fStatus = "payoutsuccessful";
                    responseMessage = "Withdrawal Successful";
                    transRespDetails.setRemark("Commission Amount "+merchantUserCommission+" "+commissionCurrency);
                    transRespDetails.setDescription(successMessage);

                    paymentManager.updateTransactionStatusAfterResponse(String.valueOf(referenceId), ActionEntry.STATUS_PAYOUT_SUCCESSFUL, voucherAmount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, transRespDetails.getTransactionId(),null);
                    entry.actionEntryForCommon(referenceId, voucherAmount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                    voucherMoneyPaymentProcess.actionEntryExtension(Integer.parseInt(referenceId),referenceId,voucherAmount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL,transRespDetails,commRequestVO);
                }
                else
                {
                    fStatus = "payoutfailed";
                    responseMessage = "Withdrawal Failed";
                    transRespDetails.setRemark(errorMessage);
                    transRespDetails.setDescription(errorMessage);

                    paymentManager.updateTransactionStatusAfterResponse(String.valueOf(referenceId), ActionEntry.STATUS_PAYOUT_FAILED, voucherAmount, transRespDetails.getDescription(), transRespDetails.getTransactionId(),null);
                    entry.actionEntryForCommon(referenceId, voucherAmount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, transRespDetails, auditTrailVO, null);
                    voucherMoneyPaymentProcess.actionEntryExtension(Integer.parseInt(referenceId), referenceId, voucherAmount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, transRespDetails, commRequestVO);

                }
                //sent merchant notification
                if(functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
                {
                    transactionLogger.error("inside sending notification from VM Withdrawal---"+transactionDetailsVO.getNotificationUrl());

                    transactionDetailsVO.setTemplateamount(transactionDetailsVO.getTemplateamount());
                    transactionDetailsVO.setTemplatecurrency(transactionDetailsVO.getTemplatecurrency());

                    transactionDetailsVO.setCommissionToPay(merchantUserCommission);
                    transactionDetailsVO.setCommCurrency(commissionCurrency);

                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO,referenceId,fStatus,responseMessage,"VM");

                }
                JSONObject jsonResObject = new JSONObject();
                jsonResObject.put("responseCode",responseCode);
                jsonResObject.put("responseStatus",responseStatus);
                jsonResObject.put("responseMessage","Withdraw Approved");
                jsonResObject.put("userMessage","Withdraw Approved");
                jsonResObject.put("outcome","CONFIRMED");
                response.setContentType("application/json");
                pWriter.println(jsonResObject.toString());
                pWriter.flush();
                return;
            }


        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException:::::",e);
            responseMessage="PAYMENT FAILED";
            userMessage="Internal server error";
            outcome="REJECTED";
            try{
                JSONObject jsonResObject = new JSONObject();
                jsonResObject.put("responseCode",responseCode);
                jsonResObject.put("responseStatus",responseStatus);
                jsonResObject.put("responseMessage",responseMessage);
                jsonResObject.put("userMessage",userMessage);
                jsonResObject.put("outcome",outcome);

                response.setContentType("application/json");
                pWriter.println(jsonResObject.toString());
                pWriter.flush();
                return;
            }
            catch (JSONException ze){
                //transactionLogger.error("JSONException:::::",ze);
            }
        }

        catch (JSONException e)
        {
            transactionLogger.error("JSONException:::::",e);
            responseMessage="PAYMENT FAILED";
            userMessage="Internal server error";
            outcome="REJECTED";
            try{
                JSONObject jsonResObject = new JSONObject();
                jsonResObject.put("responseCode",responseCode);
                jsonResObject.put("responseStatus",responseStatus);
                jsonResObject.put("responseMessage",responseMessage);
                jsonResObject.put("userMessage",userMessage);
                jsonResObject.put("outcome",outcome);

                response.setContentType("application/json");
                pWriter.println(jsonResObject.toString());
                pWriter.flush();
                return;
            }
            catch (JSONException ze)
            {
                transactionLogger.error("JSONException:::::",ze);
            }
        }

        //transactionLogger.error("Leaving from VoucherMoneyBackEndServlet:::::");

    }
}
