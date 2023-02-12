package com.payment.perfectmoney;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.PaymentManager;
import com.payment.Enum.PaymentModeEnum;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.net.ssl.SSLHandshakeException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.*;

/**
 * Created by Admin on 7/5/17.
 */
public class PerfectMoneyUtils
{
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.perfectmoney");
    private final static String charset = "UTF-8";
    //private static PerfectMoneyLogger transactionLogger = new PerfectMoneyLogger(PerfectMoneyUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PerfectMoneyUtils.class.getName());
    public static String generateAutoSubmitForm( CommonValidatorVO commonValidatorVO)
    {
        StringBuilder html = new StringBuilder();
        String accountId = commonValidatorVO.getMerchantDetailsVO().getAccountId();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"" + RB.getString("SALEURL") + "").append("\" method=\"post\">\n");
        html.append("<input type=\"hidden\" name=\"PAYEE_ACCOUNT\" id=\"PAYEE_ACCOUNT\" value=\"" + gatewayAccount.getMerchantId() + "\">\n");
        html.append("<input type=\"hidden\" name=\"STATUS_URL\" id=\"STATUS_URL\" value=\"" + RB.getString("STATUSURL") + "\">\n");
        html.append("<input type=\"hidden\" name=\"PAYEE_NAME\" id=\"PAYEE_NAME\" value=\"" + gatewayAccount.getDisplayName() + "\">\n");
        html.append("<input type=\"hidden\" name=\"PAYMENT_AMOUNT\" id=\"PAYMENT_AMOUNT\" value=\"" + commonValidatorVO.getTransDetailsVO().getAmount() + "\">\n");
        html.append("<input type=\"hidden\" name=\"PAYMENT_ID\" id=\"PAYMENT_ID\" value=\"" + commonValidatorVO.getTrackingid() + "\">\n");
        html.append("<input type=\"hidden\" name=\"PAYMENT_UNITS\" id=\"PAYMENT_UNITS\" value=\"" + gatewayAccount.getCurrency() + "\">\n");
        html.append("<input type=\"hidden\" name=\"FORCED_PAYMENT_METHOD\" id=\"FORCED_PAYMENT_METHOD\" value=\""+getForcePaymentMethod(commonValidatorVO.getPaymentType())+"\">\n");
        html.append("<input type=\"hidden\" name=\"PAYMENT_URL\" id=\"PAYMENT_URL\" value=\"" + RB.getString("PAYMENTURL") + "\">\n");
        html.append("<input type=\"hidden\" name=\"NOPAYMENT_URL\" id=\"NOPAYMENT_URL\" value=\"" + RB.getString("NOPAYMENTURL") + "\">\n");
        html.append("</form>");
        transactionLogger.error("form----"+html);
        return html.toString();
    }

    public static String getForcePaymentMethod(String paymodeId)
    {
        String methodName="";
        if (PaymentModeEnum.WALLET_PAYMODE.ordinal()==Integer.parseInt(paymodeId)){
            methodName="account";
        }
        else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == Integer.parseInt(paymodeId)){
            methodName ="wire";
        }
        else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == Integer.parseInt(paymodeId)){
            methodName="voucher";
        }
        else if (PaymentModeEnum.PREPAID_CARD_PAYMODE.ordinal() == Integer.parseInt(paymodeId)){
            methodName="voucher";
        }
        return methodName;
    }

    public static String doPostHTTPSURLConnection(String strURL, String request) throws PZTechnicalViolationException
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        URLConnection conn = null;
        try
        {
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL);
            try
            {
                conn = url.openConnection();
                conn.setConnectTimeout(120000);
                conn.setReadTimeout(120000);
            }
            catch (SSLHandshakeException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("PerfectMoneyUtils.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, io.getMessage(), io.getCause());
            }
            if (conn instanceof HttpURLConnection)
            {
                ((HttpURLConnection) conn).setRequestMethod("POST");
            }
            assert conn != null;
            conn.setDoInput(true);
            conn.setDoOutput(true);
            out = new BufferedOutputStream(conn.getOutputStream());
            byte outBuf[] = request.getBytes(charset);
            out.write(outBuf);
            out.close();
            in = new BufferedInputStream(conn.getInputStream());
            result = ReadByteStream(in);
        }
        catch (UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PerfectMoneyUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PerfectMoneyUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PerfectMoneyUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PerfectMoneyUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("PZConstraintViolationException--->",e);
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("PerfectMoneyUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("PerfectMoneyUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }

    private static String ReadByteStream(BufferedInputStream in) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        LinkedList<PerfectMoney> bufList = new LinkedList<PerfectMoney>();
        int size = 0;
        String buffer = null;
        byte buf[];
        try
        {
            do
            {
                buf = new byte[128];
                int num = in.read(buf);
                if (num == -1)
                    break;
                size += num;
                bufList.add(new PerfectMoney(buf, num));
            }
            while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<PerfectMoney> p = bufList.listIterator(); p.hasNext(); )
            {
                PerfectMoney b = p.next();
                for (int i = 0; i < b.size; )
                {
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }
            }
            buffer = new String(buf, charset);
        }
        catch (UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PerfectMoneyUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        catch (IOException ie)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PerfectMoneyUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ie.getMessage(), ie.getCause());
        }
        return buffer;
    }

    public static String getMD5HashVal(String str) throws PZTechnicalViolationException
    {
        String encryptedString = null;
        byte[] bytesToBeEncrypted;
        try
        {
            // convert string to bytes using a encoding scheme
            bytesToBeEncrypted = str.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] theDigest = md.digest(bytesToBeEncrypted);
            // convert each byte to a hexadecimal digit
            Formatter formatter = new Formatter();
            for (byte b : theDigest)
            {
                formatter.format("%02x", b);
            }
            encryptedString = formatter.toString().toLowerCase();
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PerfectMoneyUtils.class.getName(), "getMD5HashVal()", null, "common", "UnSupportedEncoding Exception while conecting to InPay", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PerfectMoneyUtils.class.getName(), "getMD5HashVal()", null, "common", "NoSuchAlgorithm Exception while conecting to InPay", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return encryptedString;
    }

    public HashMap readPayoutOpResponse(String htmlResponse){

        Document html = Jsoup.parse(htmlResponse);
        Element payeeAccountNameEl=html.select("input[name=Payee_Account_Name]").first();
        Element payeeAccountEl=html.select("input[name=Payee_Account]").first();
        Element payerAccountEl=html.select("input[name=Payer_Account]").first();
        Element paymentAmountEl=html.select("input[name=PAYMENT_AMOUNT]").first();
        Element paymentBatchNumEl=html.select("input[name=PAYMENT_BATCH_NUM]").first();
        Element paymentIdEl=html.select("input[name=PAYMENT_ID]").first();
        Element codeEl=html.select("input[name=code]").first();
        Element periodEl=html.select("input[name=Period]").first();
        Element ErrorEl=html.select("input[name=ERROR]").first();

        String payeeAccountName="";
        String payeeAccount="";
        String payerAccount="";
        String paymentAmount="";
        String paymentBatchNum="";
        String paymentId="";
        String error="";
        String code="";
        String period="";

        if(payeeAccountNameEl!=null){
            payeeAccountName = payeeAccountNameEl.attr("value");
        }
        if(payeeAccountEl!=null){
            payeeAccount = payeeAccountEl.attr("value");
        }
        if(payerAccountEl!=null){
            payerAccount = payerAccountEl.attr("value");
        }
        if(paymentAmountEl!=null){
            paymentAmount = paymentAmountEl.attr("value");
        }
        if(paymentBatchNumEl!=null){
            paymentBatchNum = paymentBatchNumEl.attr("value");
        }
        if(paymentIdEl!=null){
            paymentId = paymentIdEl.attr("value");
        }

        if(ErrorEl!=null){
            error = ErrorEl.attr("value");
        }
        if(codeEl!=null){
            code = codeEl.attr("value");
        }
        if(periodEl!=null){
            period = periodEl.attr("value");
        }

        HashMap hashMap=new HashMap();
        hashMap.put("payeeAccountName",payeeAccountName);
        hashMap.put("payeeAccount",payeeAccount);
        hashMap.put("payerAccount",payerAccount);
        hashMap.put("paymentAmount",paymentAmount);
        hashMap.put("paymentBatchNum",paymentBatchNum);
        hashMap.put("paymentId",paymentId);
        hashMap.put("error",error);
        hashMap.put("code",code);
        hashMap.put("period",period);

        return hashMap;
    }

    public CommResponseVO readInquiryResponse(String inquiryResponse)
    {
        CommResponseVO commResponseVO = new CommResponseVO();
        String status = "failed";

        if(inquiryResponse.contains("\n"))
        {
            transactionLogger.error("Inside New Line---");
            String lines[] = inquiryResponse.split("\\r?\\n");

            int count = 1;
            for (String line : lines)
            {
                if(lines[count].contains(","))
                {
                    String row[] = lines[count].split(",");
                    transactionLogger.error("1---" + row[1]);
                    if (row[1].equalsIgnoreCase("Charge"))
                    {
                        count++;
                    }
                    row = lines[count].split(",");
                    transactionLogger.error("2---" + row[1]);
                    if (row[1].equalsIgnoreCase("Income"))
                    {
                        String time = row[0];//Batch number -- Payment id
                        String batch = row[2];//Batch number -- Payment id
                        String currency = row[3];//Currency
                        String amount = row[4];//Amount
                        String payerAccount = row[6];//Payer Account
                        String payeeAccount = row[7];//Payee Account
                        String remark = row[9];//Description

                        status = "success";

                        commResponseVO.setAmount(amount);
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setRemark(remark);
                        commResponseVO.setTransactionId(batch);
                        commResponseVO.setArn(payerAccount);
                        commResponseVO.setAuthCode(payeeAccount);
                        commResponseVO.setResponseTime(time);

                        break;
                    }
                }
            }
            commResponseVO.setStatus(status);
        }
        else
        {
            System.out.println("No new line");
        }
        /*int i = 1;
        if(lines[i].contains(","))
        {
            String inquiryValues[] = lines[i].split(",");


            System.out.println("Inside Comma---"+inquiryValues[i]);

            if(inquiryValues[1].equalsIgnoreCase("Charge"))
            {
                System.out.println(i+" "+inquiryValues[i]);
                i++;
            }

            inquiryValues = lines[i].split(",");

            System.out.println(i+" "+inquiryValues[i]);
            if(inquiryValues[i].equalsIgnoreCase("Income"))
            {
                System.out.println("All values---"+inquiryValues[i]);
                String time = inquiryValues[0];//Batch number -- Payment id
                String batch = inquiryValues[2];//Batch number -- Payment id
                String currency = inquiryValues[3];//Currency
                String amount = inquiryValues[4];//Amount
                String payerAccount = inquiryValues[6];//Payer Account
                String payeeAccount = inquiryValues[7];//Payee Account
                String remark = inquiryValues[9];//Description

                status = "success";

                commResponseVO.setAmount(amount);
                commResponseVO.setCurrency(currency);
                commResponseVO.setRemark(remark);
                commResponseVO.setTransactionId(batch);
                commResponseVO.setArn(payerAccount);
                commResponseVO.setAuthCode(payeeAccount);
                commResponseVO.setResponseTime(time);
            }
        }
        commResponseVO.setStatus(status);*/


        return commResponseVO;
    }

    public HashMap readS2SVoucherResponse(String htmlResponse){

        Document html = Jsoup.parse(htmlResponse);

        Element paymentBatchNumEl=html.select("input[name=PAYMENT_BATCH_NUM]").first();
        Element VCurrencyEl=html.select("input[name=VOUCHER_AMOUNT_CURRENCY]").first();
        Element ErrorEl=html.select("input[name=ERROR]").first();
        Element VoucherEl=html.select("input[name=VOUCHER_NUM]").first();
        Element VoucherAmountEl=html.select("input[name=VOUCHER_AMOUNT]").first();
        Element payeeAccountEl=html.select("input[name=Payee_Account]").first();

        String payeeAccount="";
        String paymentBatchNum="";
        String error="";
        String vCurrency="";
        String voucherNo="";
        String voucherAmount="";


        if(paymentBatchNumEl!=null){
            paymentBatchNum = paymentBatchNumEl.attr("value");
        }

        if(payeeAccountEl!=null){
            payeeAccount = payeeAccountEl.attr("value");
        }

        if(ErrorEl!=null){
            error = ErrorEl.attr("value");
        }

        if(VCurrencyEl!=null){
            vCurrency = VCurrencyEl.attr("value");
            if("1".equalsIgnoreCase(vCurrency))
                vCurrency = "USD";
            if("2".equalsIgnoreCase(vCurrency))
                vCurrency = "EUR";
        }
        if(VoucherAmountEl!=null){
            voucherAmount = VoucherAmountEl.attr("value");
        }

        if(VoucherEl!=null){
            voucherNo = VoucherEl.attr("value");
        }

        HashMap hashMap=new HashMap();


        hashMap.put("error",error);
        hashMap.put("voucherCurrency",vCurrency);
        hashMap.put("voucherNo",voucherNo);
        hashMap.put("voucherAmount",voucherAmount);
        hashMap.put("payeeAccount",payeeAccount);
        hashMap.put("paymentBatchNum",paymentBatchNum);

        return hashMap;
    }

    public void updateTransaction(CommonValidatorVO commonValidatorVO,String status,String remark,String transactionId,String merchantID)
    {
        Connection con = null;
        StringBuffer dbBuffer = new StringBuffer();
        ActionEntry entry = new ActionEntry();
        PaymentManager paymentManager = new PaymentManager();
        CommResponseVO commResponseVO = new CommResponseVO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        String message = "";
        String dbStatus = "";
        try
        {
            con = Database.getConnection();

            auditTrailVO.setActionExecutorName("S2S");
            auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            if ("success".equalsIgnoreCase(status))
            {
                dbStatus = "capturesuccess";
                message = "Deposit Successful";
                dbBuffer.append("update transaction_common set templateamount='" + commonValidatorVO.getAddressDetailsVO().getTmpl_amount() + "', captureamount='" + commonValidatorVO.getTransDetailsVO().getAmount() + "',paymentid='" + transactionId + "',status='capturesuccess',notificationCount=1");
                commResponseVO.setDescription(message);
                commResponseVO.setStatus(status);
                commResponseVO.setRemark(message);
                commResponseVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                commResponseVO.setTmpl_Amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
                commResponseVO.setTmpl_Currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());

                entry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, auditTrailVO, null);

            }
            else
            {
                dbStatus = "authfailed";
                message = "Deposit Failed";
                dbBuffer.append("update transaction_common set templateamount='" + commonValidatorVO.getAddressDetailsVO().getTmpl_amount() + "',status='authfailed',paymentid='" + transactionId + "',notificationCount=1");
                commResponseVO.setDescription(message);
                commResponseVO.setStatus(status);
                commResponseVO.setRemark(remark);
                commResponseVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                commResponseVO.setTmpl_Amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
                commResponseVO.setTmpl_Currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
                entry.actionEntryForCommon(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
            }
            dbBuffer.append(" ,remark='" + message + "' where trackingid = " + commonValidatorVO.getTrackingid());
            Database.executeUpdate(dbBuffer.toString(), con);
            statusSyncDAO.updateAllTransactionFlowFlag(commonValidatorVO.getTrackingid(), dbStatus);
            paymentManager.insertPerfectMoneyDetails(commonValidatorVO.getTrackingid(), status, commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getCurrency(), merchantID, "", transactionId, "", commonValidatorVO.getAddressDetailsVO().getEmail(), "");
        }
        catch (PZDBViolationException dbe)
        {
            transactionLogger.error("PZDBViolationException updateTransaction---",dbe);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception updateTransaction---",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }

    static class PerfectMoney
    {
        public byte buf[];
        public int size;

        public PerfectMoney(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }

}

