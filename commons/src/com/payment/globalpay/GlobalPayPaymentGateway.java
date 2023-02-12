package com.payment.globalpay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Rubixpay.RubixpayPaymentProcess;
import com.payment.Rubixpay.RubixpayUtils;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.verve.VerveUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ResourceBundle;

/**
 * Created by Admin on 1/8/2021.
 */
public class GlobalPayPaymentGateway extends AbstractPaymentGateway
{

    private static TransactionLogger transactionlogger = new TransactionLogger(GlobalPayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "globalpay";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.GLOBALPAY");
    String redirecturl= "";

    public GlobalPayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
        transactionlogger.error("GlobalPayPaymentGateway  accountid------------------->" + accountId);
    }


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }


    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("Entering processSale of GlobalPayPaymentGateway......");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        GlobalPayUtils globalPayUtils =new GlobalPayUtils();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        // GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        String payment_Card= GatewayAccountService.getPaymentMode(transactionDetailsVO.getPaymentType());
        String payment_url="";
        String transaction_url="";
        transactionlogger.error("payment_Card  is------------------->"+payment_Card);
        boolean isTest = gatewayAccount.isTest();
       // String GPID="GPZEN099";
      //  String amount="10.00";
        String desc=trackingID;
        String referenceID=trackingID;
        String ProductID=trackingID;
        String CustomerID=trackingID;
        String merchantlogo="";
       // String merchantName="dev";
       // String currency="GHS";
        String GPID=gatewayAccount.getMerchantId();
        transactionlogger.error("mid is-------------->"+GPID);
        String apikey =gatewayAccount.getFRAUD_FTP_PASSWORD();
        transactionlogger.error("apikey is-------------->"+apikey);
        /*String serverkey ="3A6CCEEA-3036-4C24-A63C-DADFA5355276";
        transactionlogger.error("returnUrl is-------------->");*/
        String currency =transactionDetailsVO.getCurrency();
        transactionlogger.error("currency is-------------->"+currency);
        String amount=transactionDetailsVO.getAmount();
        transactionlogger.error("amount is-------------->"+amount);

        String merchantName =commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname();
        transactionlogger.error("merchantName is-------------->"+merchantName);



        String securePaymentGateway="";

        if (isTest){
            payment_url=RB.getString("TEST_SALE_URL");
            transaction_url=RB.getString("GET_TRANSACTION");
            transactionlogger.error("TEST_SALE_URL------------------------->"+payment_url);


        }
        else {
            payment_url=RB.getString("LIVE_SALE_URL");
            transaction_url=RB.getString("GET_TRANSACTION");
            transactionlogger.error("LIVE_SALE_URL------------------------->"+payment_url);
        }

        String request="GPID="+GPID+"&amount="+amount+"&desc="+desc+"&referenceID="+referenceID+
                "&ProductID="+ProductID+"&CustomerID="+CustomerID+"&merchantlogo="+merchantlogo+
                "&merchantName="+merchantName+"&currency="+currency;

        transactionlogger.error("request--------------for----" + request);

        String response= GlobalPayUtils.doHttpPostConnection(payment_url,request);

        redirecturl=transaction_url+ "?GPID=" + GPID+"&tid="+response;
        transactionlogger.error("response--------------for--"+trackingID+"--"+response);
        commResponseVO.setStatus("pending3DConfirmation");
        commResponseVO.setUrlFor3DRedirect(redirecturl);
                transactionlogger.error("redirecturl---------------------------->" + redirecturl);
        globalPayUtils.updateTransaction(trackingID,response);
        return commResponseVO;
    }

    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in Globalpay ---- ");
        String html = "";;
        Comm3DResponseVO transRespDetails = null;

        CommRequestVO commRequestVO = GlobalPayUtils.getRubixPayRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        RubixpayPaymentProcess rubixpayPaymentProcess =new RubixpayPaymentProcess();
        boolean isTest = gatewayAccount.isTest();
        try
        {
            transactionlogger.error("isService Flag -----" + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionlogger.error("autoredirect  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionlogger.error("addressdetail  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionlogger.error("terminal id  is -----" + commonValidatorVO.getTerminalId());
            transactionlogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = rubixpayPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionlogger.error("automatic redirect rubixpay form -- >>"+html);
            }

        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in TapMioPaymentGateway---", e);
        }
        return html;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionlogger.error("-----inside globalpay processInquiry-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        GlobalPayUtils globalPayUtils =new GlobalPayUtils();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        transactionlogger.error("CommTransactionDetailsVO------------------------>"+transactionDetailsVO);
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        transactionlogger.error("inside globalpay processInquiry ----- ");
        transactionlogger.error("accountId is-------------->"+accountId);

        CommResponseVO commResponseVO=new CommResponseVO();
        Functions functions = new Functions();
        String status="";
        String currency="GHS";
        String transaction_status="";
        String inquiry_url="";
        boolean isTest = gatewayAccount.isTest();
        if (isTest){
            inquiry_url=RB.getString("INQUIRY");
            transactionlogger.error("INQUIRY------------------------->"+inquiry_url);


        }
        else {
            inquiry_url=RB.getString("INQUIRY");
            transactionlogger.error("INQUIRY;------------------------->"+inquiry_url);
        }


        String gpid=gatewayAccount.getMerchantId();
        transactionlogger.error("gpid is-------------->"+gpid);

        String trackingid=commRequestVO.getTransDetailsVO().getOrderId();
        transactionlogger.error("trackingid/orderid is-------------->"+trackingid);
        inquiry_url=inquiry_url+"gpid="+gpid+"&ref="+trackingid;

        try
        {



            transactionlogger.error("inquiry request url------------------------> " + inquiry_url);
            String response= globalPayUtils.doHttpPostConnection(inquiry_url,"" );
            transactionlogger.error("process3DSaleConfirmation response -------------------------> " + response);

            String finalstatus="";
            if (response != null)
            {

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("transaction_status"))
                {
                    transaction_status=jsonObject.getString("transaction_status");
                    transactionlogger.error("transaction_statuse -----> " + transaction_status);

                }
                if (jsonObject.has("status"))
                {
                    status=jsonObject.getString("status");
                    transactionlogger.error("_status -----> " + status);

                }


                if (transaction_status.equalsIgnoreCase("APPROVED")||status.equalsIgnoreCase("true"))
                {
                    finalstatus="success";
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus(finalstatus);
                    commResponseVO.setRemark(transaction_status);
                    commResponseVO.setDescription(transaction_status);
                    transactionlogger.error("inside success finalstatus-----> " + finalstatus);
                }


                else if(status.equalsIgnoreCase("false")||transaction_status.equalsIgnoreCase("DECLINED.VBV NOT AUTHENTICATED")){
                    finalstatus="fail";
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionStatus(finalstatus);
                    commResponseVO.setRemark(transaction_status);
                    commResponseVO.setDescription(transaction_status);
                    transactionlogger.error("inside fail finalstatus-----> " + finalstatus);
                }


                commResponseVO.setTransactionStatus(finalstatus);
                commResponseVO.setTransactionId(jsonObject.getString("TID"));
                commResponseVO.setRemark(transaction_status);
                commResponseVO.setDescription(transaction_status);
                commResponseVO.setMerchantId(gpid);
                commResponseVO.setCurrency(currency);
                commResponseVO.setAmount(jsonObject.getString("amount"));
                commResponseVO.setResponseTime(commRequestVO.getTransDetailsVO().getResponsetime());
                commResponseVO.setTransactionType(jsonObject.getString("type"));
                transactionlogger.error("finalstatus-----> " + finalstatus);
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setTransactionStatus("failed");
                commResponseVO.setRemark("Sys:Transaction Fail No Response");
                commResponseVO.setDescription("Sys:Transaction Fail No Response");
            }
        }
        catch (Exception e)
        {
            transactionlogger.error("Exception-----", e);
        }



        return commResponseVO;
    }



}
