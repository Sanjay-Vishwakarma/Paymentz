package com.payment.apcoFastpay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.payment.apco.core.ApcoPayUtills;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 5/21/18.
 */
public class ApcoFastpayPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE_APC0_ALDRAPAY="aldrapay";
    public static final String GATEWAY_TYPE_APCO_PURPLEPAY="purplepay";
    public static final String GATEWAY_TYPE_APCO_RAVE="ravedirect";
    public static final String GATEWAY_TYPE_APCO_FAST_PAY="fastpay";
    private static TransactionLogger transactionLogger= new TransactionLogger(ApcoFastpayPaymentGateway.class.getName());
    ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.ApcoPayServlet");
    public  ApcoFastpayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }


    public static void main(String[] args)
    {
        try
        {
            String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <soap:Body>\n" +
                    "    <PopulateTransactionData2 xmlns=\"https://www.apsp.biz/\">\n" +
                    "      <MerchID>5299</MerchID>\n" +
                    "      <Password>oF2wWC0lfuDrY1</Password>\n" +
                    "      <CardType>MASTERCARD</CardType>\n" +
                    "      <CardNo>5453010000066167</CardNo>\n" +     //4012888888881881    //4773654827386427
                    "      <ExpMonth>01</ExpMonth>\n" +
                    "      <ExpYear>2020</ExpYear>\n" +
                    "      <Ext>777</Ext>\n" +
                    "      <CardHolderName>Uday Raj</CardHolderName>\n" +
                    "      <CardHolderAddress>Malad Mindspace</CardHolderAddress>\n" +
                    "      <CardIssueNum></CardIssueNum>\n" +
                    "      <CardStartMonth></CardStartMonth>\n" +
                    "      <CardStartYear></CardStartYear>\n" +
                    "      <PspID></PspID>\n" +
                    "    </PopulateTransactionData2>\n" +
                    "  </soap:Body>\n" +
                    "</soap:Envelope>";

           // System.out.println("Request-----"+request);

            String response = ApcoPayUtills.doPostHTTPSURLConnection("https://www.apsp.biz:9085/MerchantServices.asmx", request);
          //  String response1 = ApcoPayUtills.doPostHTTPSURLConnection("https://www.apsp.biz:9085/MerchantServices.asmx", request);

           // System.out.println("Response------"+response);
            HashMap map= (HashMap) ApcoFastpayUtils.readApcopayRedirectionXMLReponse(response);
            //System.out.println("MAPHHYBGYG----"+ map.get("PopulateTransactionData2Result"));

        }
        catch (Exception e){
            transactionLogger.error("Exception----- ApcoFastpayPaymentGateway",e);
        }
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("ApcoFastpayPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside processSale-----");
        Functions functions = new Functions();
        CommRequestVO commRequestVO= (CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO= commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);

        String MerchID = gatewayAccount.getMerchantId();
        String Pass = gatewayAccount.getFRAUD_FTP_PASSWORD();
        Boolean isTest=gatewayAccount.isTest();

        try{
            String request="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <soap:Body>\n" +
                    "    <PopulateTransactionData2 xmlns=\"https://www.apsp.biz/\">\n" +
                    "      <MerchID>"+MerchID+"</MerchID>\n" +
                    "      <Password>"+Pass+"</Password>\n" +
                    "      <CardType>"+ApcoFastpayUtils.getCardType(commCardDetailsVO.getCardType())+"</CardType>\n" +
                    "      <CardNo>"+commCardDetailsVO.getCardNum()+"</CardNo>\n" +
                    "      <ExpMonth>"+commCardDetailsVO.getExpMonth()+"</ExpMonth>\n" +
                    "      <ExpYear>"+commCardDetailsVO.getExpYear()+"</ExpYear>\n" +
                    "      <Ext>"+commCardDetailsVO.getcVV()+"</Ext>\n" +
                    "      <CardHolderName>"+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"</CardHolderName>\n" +
                    "      <CardHolderAddress>"+commAddressDetailsVO.getStreet()+"</CardHolderAddress>\n" +
                    "      <CardIssueNum></CardIssueNum>\n" +
                    "      <CardStartMonth></CardStartMonth>\n" +
                    "      <CardStartYear></CardStartYear>\n" +
                    "      <PspID></PspID>\n" +
                    "    </PopulateTransactionData2>\n" +
                    "  </soap:Body>\n" +
                    "</soap:Envelope>";

            String requestLog="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <soap:Body>\n" +
                    "    <PopulateTransactionData2 xmlns=\"https://www.apsp.biz/\">\n" +
                    "      <MerchID>"+MerchID+"</MerchID>\n" +
                    "      <Password>"+Pass+"</Password>\n" +
                    "      <CardType>"+ApcoFastpayUtils.getCardType(commCardDetailsVO.getCardType())+"</CardType>\n" +
                    "      <CardNo>"+functions.maskingPan(commCardDetailsVO.getCardNum())+"</CardNo>\n" +
                    "      <ExpMonth>"+functions.maskingNumber(commCardDetailsVO.getExpMonth())+"</ExpMonth>\n" +
                    "      <ExpYear>"+functions.maskingNumber(commCardDetailsVO.getExpYear())+"</ExpYear>\n" +
                    "      <Ext>"+functions.maskingNumber(commCardDetailsVO.getcVV())+"</Ext>\n" +
                    "      <CardHolderName>"+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"</CardHolderName>\n" +
                    "      <CardHolderAddress>"+commAddressDetailsVO.getStreet()+"</CardHolderAddress>\n" +
                    "      <CardIssueNum></CardIssueNum>\n" +
                    "      <CardStartMonth></CardStartMonth>\n" +
                    "      <CardStartYear></CardStartYear>\n" +
                    "      <PspID></PspID>\n" +
                    "    </PopulateTransactionData2>\n" +
                    "  </soap:Body>\n" +
                    "</soap:Envelope>";

            transactionLogger.error("request---"+trackingID+"--"+requestLog);

            String response="";
            if(isTest)
            {
                 response = ApcoFastpayUtils.doPostHTTPSURLConnection(RB.getString("Test_token_url"), request);
            }else {
                 response = ApcoFastpayUtils.doPostHTTPSURLConnection(RB.getString("Live_token_url"), request);
            }

            transactionLogger.error("response---"+trackingID+"--"+response);
            if (functions.isValueNull(response))
            {
                String ticketToken = "";
                HashMap map = (HashMap) ApcoFastpayUtils.readApcopayRedirectionXMLReponse(response);
                transactionLogger.error("Response-----" +trackingID + "--" +  response);
                if (map != null || map.size() != 0)
                {
                    ticketToken = (String) map.get("PopulateTransactionData2Result");
                    transactionLogger.debug("ticketToken-----" + ticketToken);
                    commResponseVO.setStatus("pending");
                    commResponseVO.setRemark("Transaction Pending");
                    commResponseVO.setDescription("Pending");
                    commResponseVO.setResponseHashInfo(ticketToken);
                }
                else
                {
                    commResponseVO.setStatus("Failed");
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Failed");
                }
            }
            else
            {
                commResponseVO.setStatus("Failed");
                commResponseVO.setRemark("Transaction Failed");
                commResponseVO.setDescription("Failed");
            }
        }catch (Exception e)
        {
            transactionLogger.error("Exception----->",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Entering into processRefund of ApcoPaymentGateway::::");
        transactionLogger.error("Entering into processRefund of ApcoPaymentGateway::::");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        String language = commRequestVO.getAddressDetailsVO().getLanguage();
        String TrType = "12";

        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String profileId = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretWord = gatewayAccount.getFRAUD_FTP_PATH();
        String orderId = commTransactionDetailsVO.getOrderId();
        boolean isTest = gatewayAccount.isTest();
        String testString = "";
        String URL = "";
        if (isTest)
        {
            testString = "<TEST />";
            URL = RB.getString("URL");

        }
        else
        {
            URL = RB.getString("URL");
        }
        String amount = transactionDetailsVO.getAmount();
        String setPreviousTransactionId = transactionDetailsVO.getPreviousTransactionId();
        String status = "";
        String remark = "";

        String redirectURLSuccess = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            redirectURLSuccess = "https://" + commMerchantVO.getHostUrl() + "/transaction/ApcoPayFrontEndServlet";
        }
        else
        {
            redirectURLSuccess = RB.getString("APCOPAY_FRONTEND");
        }

        try
        {
            String refund1 = "<Transaction hash=\"" + secretWord + "\">" +
                    "<ProfileID>" + profileId + "</ProfileID>" +
                    "<Value>" + amount + "</Value>" +
                    "<Curr>" + currencyCode + "</Curr>" +
                    "<Lang>" + language + "</Lang>" +
                    "<ORef>" + orderId + "</ORef>" +
                    "<PspID>" + setPreviousTransactionId + "</PspID>" +
                    "<ActionType>" + TrType + "</ActionType>" +
                    "<UDF1 />" +
                    "<UDF2 />" +
                    "<UDF3 />" +
                    "<RedirectionURL>" + redirectURLSuccess + "</RedirectionURL>" +
                    testString +
                    "</Transaction>";

            transactionLogger.error("-----refund1 request-----" +trackingID + "--" +  refund1);
            String hash = ApcoPayUtills.getMD5HashVal(refund1);
            transactionLogger.error("-----refund1 hash-----" + trackingID + "--" + hash);

            String refund1HashedRequest = "params=<Transaction hash=\"" + hash + "\">" +
                    "<ProfileID>" + profileId + "</ProfileID>" +
                    "<Value>" + amount + "</Value>" +
                    "<Curr>" + currencyCode + "</Curr>" +
                    "<Lang>" + language + "</Lang>" +
                    "<ORef>" + orderId + "</ORef>" +
                    "<PspID>" + setPreviousTransactionId + "</PspID>" +
                    "<ActionType>" + TrType + "</ActionType>" +
                    "<UDF1 />" +
                    "<UDF2 />" +
                    "<UDF3 />" +
                    "<RedirectionURL>" + redirectURLSuccess + "</RedirectionURL>" +
                    testString +
                    "</Transaction>";

            transactionLogger.error("----- hashed refund1 request-----" +trackingID + "--" +  refund1HashedRequest);
            String refund1Response = ApcoPayUtills.doPostHTTPSURLConnection(URL, refund1HashedRequest);
            transactionLogger.error("-----refund1 response-----" + trackingID + "--" + refund1Response);
            if (functions.isValueNull(refund1Response))
            {
                Map<String, String> stringStringMap = ApcoPayUtills.readApcoPayoutResponse(refund1Response);
                transactionLogger.error("stringStringMap:::::" + stringStringMap);
                if (stringStringMap != null)
                {
                    if ("OK".equals(stringStringMap.get("Status")))
                    {
                        status = "success";
                        remark = "Refund Successful";
                    }
                    else
                    {
                        status = "failed";
                        if (stringStringMap.get("ErrorMsg") != null)
                        {
                            remark = stringStringMap.get("ErrorMsg");
                        }
                        else
                        {
                            remark = "Refund Failed";
                        }
                    }
                }
                else
                {
                    status = "failed";
                    remark = "Refund Failed";
                }
            }
            else
            {
                status = "failed";
                remark = "Refund Failed";
            }

            commResponseVO.setStatus(status);
            commResponseVO.setRemark(remark);
            commResponseVO.setDescription(remark);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception ----- ApcoFastpayPaymentGateway",e);
        }
        return commResponseVO;

    }

}