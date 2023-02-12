package com.payment.skrill;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PaymentManager;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by admin on 10-02-2017.
 */
public class SkrillPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "skrill";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.skrill");
    private static TransactionLogger transactionLogger = new TransactionLogger(SkrillPaymentGateway.class.getName());
    Functions functions=new Functions();

    public SkrillPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static void main(String[] args) throws PZTechnicalViolationException
    {
        try
        {
            /*String password = "qwerfdsa@321";
            String Md5password = SkrillUtills.getMD5HashVal(password);
            String sendMoneyRequest1 = "action=prepare" +
                    "&password=" + Md5password + "" +
                    "&amount=1.00" +
                    "&currency=GBP" +
                    "&subject=your order is ready" +
                    "&note=Details are available ";
            System.out.println("request1::::" + sendMoneyRequest1);
            String sendMoneyresponse1 = SkrillUtills.doPostHTTPSURLConnection("https://www.skrill.com/app/pay.pl", sendMoneyRequest1);
            System.out.println("response1::::" + sendMoneyresponse1);
            String sid = "";
            Map<String, String> responseMap = SkrillUtills.readStepOneResponse(sendMoneyresponse1);
            if ((responseMap.get("error") == "" && responseMap.get("error").equals("")) && responseMap.get("sid") != null)
            {
                sid = responseMap.get("sid");
                transactionLogger.debug("sid:::" + responseMap.get("sid"));
                String sendMoneyRequest2 =
                        "action=transfer" +
                                "&sid=" + sid;

                transactionLogger.debug("Request2::::" + sendMoneyRequest2);
                String sendMoneyResponse2 = SkrillUtills.doPostHTTPSURLConnection("https://www.skrill.com/app/pay.pl", sendMoneyRequest2);
                System.out.println("Response2::::" + sendMoneyResponse2);
                transactionLogger.debug("Response2::::" + sendMoneyResponse2);

                Map<String, String> sendMoneystringStringMap = SkrillUtills.readStepTwoResponse(sendMoneyResponse2);
                System.out.println("stringStringMap:::" + sendMoneystringStringMap);*/

            SkrillPaymentGateway sp = new SkrillPaymentGateway("");
            sp.processInquiry("45025",null);
            /*String password = "qwerfdsa@321";
            String Md5password = SkrillUtills.getMD5HashVal(password);
            String request =
                    "action=status_trn" +
                            "&password=" + Md5password + "" +
                            "&trn_id=" + "22222" + "";//53896

            String response = SkrillUtills.doPostHTTPSURLConnection(RB.getString("QUERYURL"), request);

            System.out.println("response::::" +response);*/
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }


    }

    @Override
    public String getMaxWaitDays()
    {
        return "5";
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("SkrillPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by Skrill gateway. Please contact your Tech. support Team:::",null);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering into SkrillPaymentGateway processSale()");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();

        String sid = "";
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        if (functions.isValueNull(transDetailsVO.getCurrency()))
        {
            currency=transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=addressDetailsVO.getTmpl_currency();
        }

        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
            String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
            String hashPassword = SkrillUtills.getMD5HashVal(password);

            String saleRequest =
                    "prepare_only=1" + /*"&merchant_id=" + URLEncoder.encode(gatewayAccount.getMerchantId(), "UTF-8")+*/"&pay_to_email=" + URLEncoder.encode(userName, "UTF-8") + "&status_url=" + URLEncoder.encode(RB.getString("STATUSURL"), "UTF-8") + "&status_url2=" + URLEncoder.encode(userName, "UTF-8") + "&language=" + URLEncoder.encode("EN", "UTF-8") + "&amount=" + URLEncoder.encode(transDetailsVO.getAmount(), "UTF-8") + "&currency=" + URLEncoder.encode(transDetailsVO.getCurrency(), "UTF-8") + "&detail1_description=" + URLEncoder.encode(transDetailsVO.getOrderDesc(), "UTF-8") + "&transaction_id=" + URLEncoder.encode(trackingID, "UTF-8") + "&return_url=" + URLEncoder.encode(RB.getString("RETURNURL"), "UTF-8") + "&cancel_url=" + URLEncoder.encode(RB.getString("CANCELURL"), "UTF-8") /*+ "&password=" + URLEncoder.encode(hashPassword, "UTF-8")*/;

            transactionLogger.error("sale request::::" + saleRequest);
            String saleResponse = SkrillUtills.doPostHTTPSURLConnection(RB.getString("SALEURL"), saleRequest);
            transactionLogger.error("sale response:::::" + saleResponse);
            sid = saleResponse.trim();
            /*Map<String, String> stepOneResponseMap = SkrillUtills.readStepOneResponse(saleResponse);
            if ((stepOneResponseMap.get("error") == "" && stepOneResponseMap.get("error").equals("")) && stepOneResponseMap.get("sid") != null)
            {
                sid = stepOneResponseMap.get("sid");
            }*/
        }
        catch (Exception se)
        {
            transactionLogger.error("Exception while connecting with bank", se);
            PZExceptionHandler.raiseTechnicalViolationException("SkrillPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        commResponseVO.setResponseHashInfo(sid);
        commResponseVO.setCurrency(currency);
        commResponseVO.setTmpl_Amount(tmpl_amount);
        commResponseVO.setTmpl_Currency(tmpl_currency);
        return commResponseVO;
    }

    public GenericResponseVO processInquiry(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering into SkrillPaymentGateway processInquiry()");

        SkrillResponseVO commResponseVO = new SkrillResponseVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();//"qwerfdsa@321";
        String email = gatewayAccount.getFRAUD_FTP_USERNAME();

       /* String password = "qwerfdsa@321";*/

        String hashPwd = SkrillUtills.getMD5HashVal(password);
        String status = "";


        try
        {
            String request =
                    "action=status_trn" +
                            "&email=" + email + "" +
                            "&password=" + hashPwd + "" +
                            "&trn_id=" + trackingID + "";

            String response = SkrillUtills.doPostHTTPSURLConnection(RB.getString("QUERYURL"), request);

            transactionLogger.error("response::::" + response);
            if(response.contains("200"))
            {
                Map<String, String> responseMap = SkrillUtills.getQueryMap(response);

                transactionLogger.error("inquiry map skrill---"+responseMap);

                if (responseMap != null)
                {
                    if ("2".equals(responseMap.get("status")))
                    {
                        status = "success";
                    }
                    else
                    {
                        status = "failed";
                    }

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    commResponseVO.setMerchantOrderId(responseMap.get("transaction_id"));
                    commResponseVO.setTransactionId(responseMap.get("mb_transaction_id"));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setAmount(responseMap.get("amount"));
                    commResponseVO.setErrorCode(responseMap.get("status"));
                    commResponseVO.setStatus(status);
                    commResponseVO.setTransactionType("inquiry");
                    commResponseVO.setFromEmail(URLDecoder.decode(responseMap.get("pay_from_email"), "UTF-8")); //
                    commResponseVO.setCustomerAmount(responseMap.get("mb_amount"));
                    commResponseVO.setCustomerCurrency(responseMap.get("mb_currency"));
                    commResponseVO.setResponseHashInfo(responseMap.get("pay_to_email"));


                }
            }
            else
            {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Transaction Fail");
                commResponseVO.setTransactionType("inquiry");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }

        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException while Decoading---",e);
            PZExceptionHandler.raiseTechnicalViolationException("SkrillPaymentGateway.java", "processRefund()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering into SkrillPaymentGateway processInquiry()");

        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        SkrillResponseVO commResponseVO = new SkrillResponseVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();//"qwerfdsa@321";
        String email = gatewayAccount.getFRAUD_FTP_USERNAME();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        String trackingID=commTransactionDetailsVO.getOrderId();
        transactionLogger.debug("trackingId-----"+trackingID);


        String hashPwd = SkrillUtills.getMD5HashVal(password);
        String status = "";


        try
        {
            String request =
                    "action=status_trn" +
                            "&email=" + email + "" +
                            "&password=" + hashPwd + "" +
                            "&trn_id=" + trackingID + "";

            String response = SkrillUtills.doPostHTTPSURLConnection(RB.getString("QUERYURL"), request);

            transactionLogger.error("response::::" + response);
            if(response.contains("200"))
            {
                Map<String, String> responseMap = SkrillUtills.getQueryMap(response);

                transactionLogger.error("inquiry map skrill---"+responseMap);

                if (responseMap != null)
                {
                    if ("2".equals(responseMap.get("status")))
                    {
                        status = "success";
                        commResponseVO.setTransactionStatus("successful");
                        commResponseVO.setDescription("Transaction Successful");
                    }
                    else
                    {
                        status = "failed";
                        commResponseVO.setTransactionStatus("failed");
                        commResponseVO.setDescription("Transaction Failed");
                    }

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    commResponseVO.setMerchantOrderId(responseMap.get("transaction_id"));
                    commResponseVO.setMerchantId(responseMap.get("200\t\tOK\n" +
                            "merchant_id"));
                    commResponseVO.setAuthCode("-");
                    commResponseVO.setCurrency(responseMap.get("currency"));
                    commResponseVO.setTransactionId(responseMap.get("mb_transaction_id"));
                    commResponseVO.setBankTransactionDate("-");
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setAmount(responseMap.get("amount"));
                    commResponseVO.setErrorCode(responseMap.get("status"));
                    commResponseVO.setStatus(status);
                    commResponseVO.setTransactionType("inquiry");
                    commResponseVO.setFromEmail(URLDecoder.decode(responseMap.get("pay_from_email"), "UTF-8")); //
                    commResponseVO.setCustomerAmount(responseMap.get("mb_amount"));
                    commResponseVO.setCustomerCurrency(responseMap.get("mb_currency"));
                    commResponseVO.setResponseHashInfo(responseMap.get("pay_to_email"));


                }
            }
            else
            {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Transaction Fail");
                commResponseVO.setTransactionType("inquiry");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }

        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException while Decoading---",e);
            PZExceptionHandler.raiseTechnicalViolationException("SkrillPaymentGateway.java", "processRefund()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering into SkrillPaymentGateway processRefund()");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        String currency="";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }
        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            String password = gatewayAccount.getFRAUD_FTP_PASSWORD();//"qwerfdsa@321";
            String email = gatewayAccount.getFRAUD_FTP_USERNAME();
            String hashPwd = SkrillUtills.getMD5HashVal(password);
            String sid = "";

            String request =
                    "action=prepare" +
                            "&email=" + email + "" +
                            "&password=" + hashPwd + "" +
                            "&transaction_id=" + trackingID + "" +
                            "&amount=" + commTransactionDetailsVO.getAmount() + "" +
                            "&refund_note=test reason";

            transactionLogger.debug("request::::" + request);
            String response = SkrillUtills.doPostHTTPSURLConnection(RB.getString("REFUNDURL"), request);
            transactionLogger.debug("response::::" + response);

            Map<String, String> responseMap = SkrillUtills.readStepOneResponse(response);
            if ((responseMap.get("error") == "" && responseMap.get("error").equals("")) && responseMap.get("sid") != null)
            {
                sid = responseMap.get("sid");
                transactionLogger.debug("sid:::" + responseMap.get("sid"));
                String refundRequest2 =
                        "action=refund" +
                                "&sid=" + sid;

                transactionLogger.debug("refundRequest2::::" + refundRequest2);
                String refundResponse2 = SkrillUtills.doPostHTTPSURLConnection(RB.getString("REFUNDURL"), refundRequest2);
                transactionLogger.debug("refundResponse2::::" + refundResponse2);

                Map<String, String> refundResponseMap = SkrillUtills.readStepTworefundResponse(refundResponse2);
                String status = "";
                if (!refundResponseMap.equals("") && refundResponseMap != null)
                {
                    if (refundResponseMap.get("status").equals("2"))
                    {
                        status = "success";
                    }
                    else
                    {
                        status = "fail";
                    }

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    commResponseVO.setStatus(status);
                    commResponseVO.setTransactionId(refundResponseMap.get("transaction_id"));
                    commResponseVO.setErrorCode(refundResponseMap.get("status"));
                    commResponseVO.setRemark(refundResponseMap.get("status_msg"));
                    commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    commResponseVO.setTransactionType("refund");
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setCurrency(currency);
                }
            }
        }
        catch (Exception se)
        {
            transactionLogger.error("Exception while connecting with bank", se);
            PZExceptionHandler.raiseTechnicalViolationException("SkrillPaymentGateway.java", "processRefund()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }

        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        PZExceptionHandler.raiseConstraintViolationException("SkrillPaymentGateway","processCapture()",null,"common","Functionality not allowed for supporting Gateway", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
        return null;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        PZExceptionHandler.raiseConstraintViolationException("SkrillPaymentGateway", "processVoid()", null, "common", "Functionality not allowed for supporting Gateway", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
        return null;
    }

    public GenericResponseVO processPayout(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Entering into SkrillPaymentGateway processPayout()");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO  commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(commMerchantVO.getAccountId());

        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String hashPwd = SkrillUtills.getMD5HashVal(password);
        String status="";
        String remark="";
        String errorCode="";
        String transactionId="";
        String desc="";
        try{

            String payoutRequest = "action=prepare" +
                    "&email="+ username +
                    "&password=" + hashPwd + "" +
                    "&amount=" + commTransactionDetailsVO.getAmount() + "" +
                    "&currency=" + commTransactionDetailsVO.getCurrency() + "" +
                    "&bnf_email="+ commAddressDetailsVO.getEmail()+
                    "&subject=your order is ready" +
                    "&note=Details are available ";

            transactionLogger.error("-----step1 payoutRequest-----" + payoutRequest);
            String step1PayoutResponse = SkrillUtills.doPostHTTPSURLConnection(RB.getString("PAYOUTURL"), payoutRequest);
            transactionLogger.error("-----step1 payoutResponse-----" + step1PayoutResponse);

            Map<String, String> responseMap = SkrillUtills.readStepOneResponse(step1PayoutResponse);
            if(responseMap!=null && responseMap.get("error") == "" && responseMap.get("sid") != null){
                String sid = responseMap.get("sid");
                transactionLogger.error("sid-----" + responseMap.get("sid"));
                String step2PayoutRequest ="action=transfer&sid=" + sid;
                transactionLogger.error("-----step2PayoutRequest-----" + step2PayoutRequest);
                String step2PayoutResponse = SkrillUtills.doPostHTTPSURLConnection(RB.getString("PAYOUTURL"), step2PayoutRequest);
                transactionLogger.error("-----step2PayoutResponse-----" + step2PayoutResponse);

                Map<String,String> step2PayoutResponseMap = SkrillUtills.readStepTwoResponse(step2PayoutResponse);
                transactionLogger.error("step2PayoutResponseMap:::::" + step2PayoutResponseMap);
                if (step2PayoutResponseMap != null){
                    if ("2".equals(step2PayoutResponseMap.get("status"))){
                        status = "success";
                        desc = "Payout Successful";
                    }
                    else{
                        status = "fail";
                        desc = "Payout Failed";
                    }
                    remark=step2PayoutResponseMap.get("status_msg");
                    errorCode=step2PayoutResponseMap.get("status");
                    transactionId=step2PayoutResponseMap.get("id");
                }
                else{
                    status="pending";
                    remark="Pending Request";
                }
            }
            else{
                status="pending";
                remark="Pending Request";
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setStatus(status);
            commResponseVO.setTransactionId(transactionId);
            commResponseVO.setErrorCode(errorCode);
            commResponseVO.setRemark(remark);
            commResponseVO.setDescription(desc);
            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
            commResponseVO.setTransactionType("payout");
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            return commResponseVO;
        }
        catch (Exception se){
            transactionLogger.error("Exception while connecting with bank", se);
            PZExceptionHandler.raiseTechnicalViolationException("SkrillPaymentGateway.java", "processPayout()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        return commResponseVO;
    }

    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(":::::Entered into processAutoRedirect for Skrill:::::");
        CommRequestVO commRequestVO = null;
        String html = "";
        SkrillUtills skrillUtills=new SkrillUtills();
        PaymentManager paymentManager=new PaymentManager();
        GenericResponseVO transRespDetails = null;

        paymentManager.insertSkrillNetellerDetailEntry(commonValidatorVO, "transaction_skrill_details");
        commRequestVO = skrillUtills.getSkrillRequestVO(commonValidatorVO);

        transRespDetails = this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);

        CommResponseVO commResponseVO = (CommResponseVO) transRespDetails;

        html = SkrillUtills.generateAutoSubmitForm(commResponseVO.getResponseHashInfo(), commonValidatorVO);
        return html;
    }

}




