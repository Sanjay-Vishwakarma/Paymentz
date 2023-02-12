package payment;

import com.directi.pg.*;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
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
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created Nikita on 7/19/2017.
 */
public class VoucherMoneyBackEndServlet extends PzServlet
{
    private TransactionLogger transactionLogger = new TransactionLogger(VoucherMoneyBackEndServlet.class.getName());

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
        transactionLogger.error("Entering doPost in VoucherMoneyBackEndServlet::::"+request.getRemoteAddr());

        PrintWriter pWriter = response.getWriter();
        TransactionManager transactionManager = new TransactionManager();
        StringBuilder responseMsg = new StringBuilder();
        Connection con = null;

        String referenceId = request.getParameter("trackingid");


        String customerId="";
        String amount1="";
        String currency="";
        String customerAmount="";
        String customerCurrency="";
        String commissionPaid = "";
        String commCurrency = "";

        String responseMessage="";
        String userMessage="";
        String outcome="";
        String responseCode="200";
        String responseStatus="OK";
        int notificationCount = 0;

        String status="";
        String baseCurrency="";
        String toId="";
        String tmpl_amount="";

        Functions functions = new Functions();

        ActionEntry entry = new ActionEntry();
        String fStatus = "";
        //String nStatus = "";
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        auditTrailVO.setActionExecutorName("AcquirerBackEnd");

        VoucherMoneyResponse voucherMoneyResponse = new VoucherMoneyResponse();
        try
        {
            BufferedReader br = request.getReader();
            String str;
            while((str = br.readLine()) != null ){
                responseMsg.append(str);
            }

            transactionLogger.error("-----payment confirmation response json-----"+responseMsg);
            JSONObject jsonObject = new JSONObject(responseMsg.toString());

            if(jsonObject!=null)
            {
                referenceId = jsonObject.getString("paymentReference");
                customerId = jsonObject.getString("customerId");
                amount1 = jsonObject.getString("amount");
                currency = jsonObject.getString("currency");
                customerAmount = jsonObject.getString("amountInCustomerCurrency");
                customerCurrency = jsonObject.getString("customerCurrency");

                if(jsonObject.has("commissionPaidToUser"))
                    commissionPaid = jsonObject.getString("commissionPaidToUser");

                if(jsonObject.has("commissionCurrency"))
                    commCurrency = jsonObject.getString("commissionCurrency");
            }
            if(functions.isValueNull(customerAmount)) // for JPY Currency
            {
                double amt = Double.parseDouble(customerAmount);
                 customerAmount=(String.format("%.2f",amt));
            }
            if(functions.isValueNull(amount1)) // for JPY Currency
            {
                double amt = Double.parseDouble(amount1);
                amount1=(String.format("%.2f",amt));
            }
            transactionLogger.debug("customerAmount------" + customerAmount);
            transactionLogger.debug("customerCurrency------" + customerCurrency);
            //transactionLogger.error("referenceId---" + referenceId + "--customerId---" + customerId + "--amount---" + amount1 + "--currency---" + currency + "--customerAmount---" + customerAmount + "--customerCurrency---" + customerCurrency);
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(referenceId);


            if(transactionDetailsVO==null || transactionDetailsVO.getToid()==null)
            {
                //transactionLogger.error("-----before sending response to VM Invalid Reference---");
                responseMessage="PAYMENT FAILED";
                userMessage="Invalid Reference";
                outcome="REJECTED";

                JSONObject jsonResObject = new JSONObject();
                jsonResObject.put("responseCode",responseCode);
                jsonResObject.put("responseStatus",responseStatus);
                jsonResObject.put("responseMessage",responseMessage);
                jsonResObject.put("userMessage",userMessage);
                jsonResObject.put("outcome",outcome);

                response.setContentType("application/json");
                pWriter.println(jsonResObject.toString());
                pWriter.flush();
                //transactionLogger.error("-----after sending response to VM Invalid Reference---"+userMessage+"---"+outcome);
                return;
            }

            transactionLogger.error("dbAmount------"+transactionDetailsVO.getAmount());
            transactionLogger.error("responseAmount------"+amount1);

            status = transactionDetailsVO.getStatus();
            baseCurrency=transactionDetailsVO.getCurrency();
            toId=transactionDetailsVO.getToid();
            tmpl_amount=transactionDetailsVO.getTemplateamount();


            MerchantDAO merchantDAO = new MerchantDAO();
            MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(toId);


            if(PZTransactionStatus.AUTH_STARTED.toString().equals(status))
            {
                float customerAmt= Float.parseFloat(customerAmount);
                float tmpl_Amt=Float.parseFloat(tmpl_amount);
                float amt1=Float.parseFloat(amount1);

                transactionLogger.error("customerAmt-----"+customerAmt);
                transactionLogger.error("tmpl_Amt-----"+tmpl_Amt);
                transactionLogger.error("amt1-----"+amt1);
                transactionLogger.error("Vbv-----"+merchantDetailsVO.getVbvLogo());

                if(merchantDetailsVO !=null){

                    if(merchantDetailsVO.getVbvLogo().equals("Y") && customerAmt<tmpl_Amt ){
                       // float Amt=amt1/customerAmt*tmpl_Amt;

                        String minimumAmt=(String.format("%.2f",tmpl_Amt));

                        responseMessage="PAYMENT FAILED";
                        userMessage="Amount should not be less then "+minimumAmt+" "+customerCurrency+"";
                        transactionLogger.debug("userMessage-----"+userMessage);
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
                            transactionLogger.error("JSONException:::::",ze);
                        }
                    }
                }
                // ToDo check if flag VBV is true and customer amount is less than  tmpl_amount  the fail the request and give message that  "Amount should not be less than
                // amount1 / cutsomer amount * tmpl_amount (currency)


                //transactionLogger.error("-----before sending successful response to VM---"+referenceId+"---"+status);
                responseMessage="PAYMENT SUCCESS";
                fStatus = "authsuccessful";
                userMessage="Deposit request was sent to merchant successfully.";
                outcome="CONFIRMED";
                notificationCount = 1;

                JSONObject jsonResObject = new JSONObject();
                jsonResObject.put("responseCode",responseCode);
                jsonResObject.put("responseStatus",responseStatus);
                jsonResObject.put("responseMessage",responseMessage);
                jsonResObject.put("userMessage",userMessage);
                jsonResObject.put("outcome",outcome);
                response.setContentType("application/json");
                pWriter.println(jsonResObject.toString());
                pWriter.flush();
                //transactionLogger.error("-----after sending successful response to VM---" + referenceId + "---" + status);

                StringBuilder sb1  = new StringBuilder();

                sb1.append("update transaction_common set  status='"+fStatus+"', notificationCount='"+notificationCount+"', amount='"+amount1+"',currency='"+currency+"',remark='"+userMessage+"' ,templateamount='" + customerAmount + "' ,templatecurrency='"+customerCurrency+"' where trackingid =" + referenceId +"");
                con = Database.getConnection();
                int result = Database.executeUpdate(sb1.toString(), con);
                Database.closeConnection(con);

                //transactionLogger.error("-----after updating main table---" + referenceId + "---" + fStatus);

                voucherMoneyResponse.setAmount(amount1);
                voucherMoneyResponse.setTransactionType("sale");
                voucherMoneyResponse.setRemark(userMessage);
                voucherMoneyResponse.setStatus("success");

                voucherMoneyResponse.setCommissionToPay(commissionPaid);
                voucherMoneyResponse.setCommissionPaidCurrency(commCurrency);
                voucherMoneyResponse.setCurrency(currency);
                voucherMoneyResponse.setTmpl_Amount(customerAmount);
                voucherMoneyResponse.setTmpl_Currency(customerCurrency);

                entry.actionEntryForCommon(referenceId, amount1, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, voucherMoneyResponse, auditTrailVO, null);
                updateDetailTable(voucherMoneyResponse,referenceId);

                transactionLogger.error("sending notification from BackEnd---"+transactionDetailsVO.getNotificationUrl()+"---"+referenceId);
                if(functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
                {
                    transactionLogger.error("inside sending notification from BackEnd---"+transactionDetailsVO.getNotificationUrl());

                    transactionDetailsVO.setTemplateamount(customerAmount);
                    transactionDetailsVO.setTemplatecurrency(customerCurrency);

                    if(functions.isValueNull(amount1))
                    {
                        transactionDetailsVO.setAmount(Functions.roundOff(amount1));
                    }

                    transactionDetailsVO.setCommissionToPay(commissionPaid);
                    transactionDetailsVO.setCommCurrency(commCurrency);

                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(transactionDetailsVO,referenceId,fStatus,responseMessage,"VM");

                }
                transactionLogger.error("-----after sending Notification to Betcart from BackEnd---"+referenceId+"---"+fStatus);
                return;
            }
            else if(PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(status))
            {
                //transactionLogger.error("-----before sending capture successful rejected response to VM---"+referenceId+"---"+status);
                responseMessage="PAYMENT FAILED";
                userMessage="Deposit request was already sent to merchant successfully.";
                outcome="REJECTED";
                try
                {
                    JSONObject jsonResObject = new JSONObject();
                    jsonResObject.put("responseCode",responseCode);
                    jsonResObject.put("responseStatus",responseStatus);
                    jsonResObject.put("responseMessage",responseMessage);
                    jsonResObject.put("userMessage",userMessage);
                    jsonResObject.put("outcome",outcome);

                    response.setContentType("application/json");
                    pWriter.println(jsonResObject.toString());
                    pWriter.flush();
                    transactionLogger.error("-----after sending capture successful rejected response to VM---"+referenceId+"---"+outcome);
                    return;
                }
                catch (JSONException ze)
                {
                    transactionLogger.error("JSONException:::::",ze);
                }
            }
            else
            {
                transactionLogger.error("-----before sending Invalid Response response to VM ---"+referenceId+"---"+status);
                responseMessage="PAYMENT FAILED";
                userMessage="Invalid Reference";
                outcome="REJECTED";
                try
                {
                    JSONObject jsonResObject = new JSONObject();
                    jsonResObject.put("responseCode",responseCode);
                    jsonResObject.put("responseStatus",responseStatus);
                    jsonResObject.put("responseMessage",responseMessage);
                    jsonResObject.put("userMessage",userMessage);
                    jsonResObject.put("outcome",outcome);

                    response.setContentType("application/json");
                    pWriter.println(jsonResObject.toString());
                    pWriter.flush();
                    transactionLogger.error("-----after sending Invalid Response response to VM---"+referenceId+"---"+status);
                    return;
                }
                catch (JSONException ze)
                {
                    transactionLogger.error("JSONException:::::",ze);
                }
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
                transactionLogger.error("JSONException:::::",ze);
            }
        }
        catch (JSONException e){
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
            catch (JSONException ze){
                transactionLogger.error("JSONException:::::",ze);
            }
        }
        catch (SystemError systemError){
            //transactionLogger.error("SystemError:::::",systemError);
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
            catch (JSONException e){
                transactionLogger.error("JSONException:::::",e);
            }

            finally
            {
                if (con!=null)
                {
                    Database.closeConnection(con);
                }
            }
        }
        //transactionLogger.error("Leaving from VoucherMoneyBackEndServlet:::::");

    }

    private void updateDetailTable(VoucherMoneyResponse vmResponse,String trackingId)throws PZDBViolationException
    {
        Connection conn= null;
        PreparedStatement pstmt = null;

        String merchantUserComm = "";
        String commCurrency = "";
        String commToPay = "";
        String commToUserCurrency = "";
        if(vmResponse!=null)
        {
            System.out.println("vm response---"+vmResponse.getMerchantUsersCommission());
            System.out.println("vm response deposit comm---"+vmResponse.getCommissionPaidCurrency());
            merchantUserComm = vmResponse.getMerchantUsersCommission();
            commCurrency = vmResponse.getCommissionCurrency();
            commToPay = vmResponse.getCommissionToPay();
            commToUserCurrency = vmResponse.getCommissionPaidCurrency();
        }
        try
        {
            conn = Database.getConnection();
            String sql = "update transaction_vouchermoney_details set merchantUsersCommission=?,merchantUserCommCurrency=?,commissionPaidToUser=?,commPaidToUserCurrency=? where trackingid=?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, merchantUserComm);
            pstmt.setString(2, commCurrency);
            pstmt.setString(3, commToPay);
            pstmt.setString(4, commToUserCurrency);
            pstmt.setString(5, trackingId + "");

            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(VoucherMoneyBackEndServlet.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(VoucherMoneyBackEndServlet.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
    }
}
