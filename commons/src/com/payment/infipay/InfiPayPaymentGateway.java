package com.payment.infipay;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.google.gson.Gson;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.payg.AsyncPayGPayoutQueryService;
import com.payment.payg.PayGUtils;
import org.apache.log4j.LogManager;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.*;

/**
 * Created by Admin on 2022-01-22.
 */
public class InfiPayPaymentGateway extends AbstractPaymentGateway
{

    public static final String GATEWAY_TYPE     = "infipay";
    private final static ResourceBundle RB      = LoadProperties.getProperty("com.directi.pg.InfiPay");

    private static TransactionLogger transactionLogger = new TransactionLogger(InfiPayPaymentGateway.class.getName());
    org.apache.log4j.Logger facileroLogger              = LogManager.getLogger("facilerolog");


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public InfiPayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("BoomBillPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public static ArrayList<CommTransactionDetailsVO> processInitialBankCode(String trackingID, String currency) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processQuery() of InfiPayPaymentGateway.....");
        Functions functions                                 = new Functions();
        InfiPayUtils infiPayUtils                           = new InfiPayUtils();
        ArrayList<CommTransactionDetailsVO> bankList = new ArrayList<>();
        try
        {
            String REQUEST_URL = RB.getString("LIVE_BANK_URL")+currency;
            String response = infiPayUtils.doGetHttpUrlConnection(REQUEST_URL);
            transactionLogger.error("processInitialSale() Response--" + trackingID + "--->" + response);

            if (functions.isValueNull(response) && functions.isJSONValid(response) )
            {
                JSONObject jsonObject = new JSONObject(response);

                JSONArray jsonArray = new JSONArray();
                if(jsonObject.has("data")){
                    jsonArray = jsonObject.getJSONArray("data");

                    for(int i=0;i<= jsonArray.length()-1;i++){
                        CommTransactionDetailsVO commTransactionDetailsVOBank = new CommTransactionDetailsVO();
                        JSONObject bankJson = jsonArray.getJSONObject(i);

                        commTransactionDetailsVOBank.setBankAccountNo(bankJson.getString("code"));
                        commTransactionDetailsVOBank.setCustomerBankAccountName(bankJson.getString("name"));
                        commTransactionDetailsVOBank.setPayoutType(bankJson.getBoolean("po")+"");

                        bankList.add(commTransactionDetailsVOBank);

                    }
                }

            }
            transactionLogger.error("processInitialSale() Response--" + trackingID + "--->" + bankList.size());
        }
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(InfiPayPaymentGateway.class.getName(), "processInitialSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return  bankList;

    }
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processSale() of InfiPayPaymentGateway........");
        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO transactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);
        String sKey             = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String CID              = gatewayAccount.getMerchantId();

        InfiPayUtils infiPayUtils                       = new InfiPayUtils();
        String bankCode = "";
        String bankName = "";
        String bankCodeNameString = "";



        String refIdPartner    =  trackingID;
        String currency   =  "";

        String bank         = "";
        String accName      = "";
        String accNum        =  "";
        String amount      =  "";
        String token         =  "";
        String REQUEST_URL  =  "";
        String REDIRECT_URL =  "";
        String Hash         = "";
        String toType       = "";

        JSONObject requestJSON = new JSONObject();
        JSONObject requestLogJson;
        String payment_brand             = transactionDetailsVO.getCardType();

        try
        {

            if(functions.isValueNull(transactionDetailsVO.getTotype())){
                toType                =  transactionDetailsVO.getTotype();
            }
            transactionLogger.error("ToType >>>>>>>>>."+toType);

            transactionLogger.error("payment_brand >>>>>>>>>>>>>>>>>> "+payment_brand);
            payment_brand = InfiPayUtils.getPaymentBrand(payment_brand);
            transactionLogger.error("payment_brand >>>>>>>>>>>>>>>>>> "+payment_brand);


            if (functions.isValueNull(commMerchantVO.getHostUrl()))
                REDIRECT_URL = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingID;
            else
                REDIRECT_URL = RB.getString("REDIRECT_URL") + trackingID;


            amount          = InfiPayUtils.getAmount(transactionDetailsVO.getAmount());
            currency        = transactionDetailsVO.getCurrency();

            transactionLogger.error("transactionDetailsVO.getCustomerBankAccountName() "+transactionDetailsVO.getCustomerBankAccountName());

            if ("IB".equalsIgnoreCase(payment_brand) || "BT".equalsIgnoreCase(payment_brand))
            {
                if(functions.isValueNull(transactionDetailsVO.getCustomerBankAccountName())){
                    bankCodeNameString = transactionDetailsVO.getCustomerBankAccountName();
                }

                if(functions.isValueNull(bankCodeNameString) && bankCodeNameString.contains("_") ){
                    bankCode = bankCodeNameString.split("_")[0];
                    bankName = bankCodeNameString.split("_")[1];
                }
            }

            REQUEST_URL = RB.getString("LIVE_SALE_URL");

            if ("IB".equalsIgnoreCase(payment_brand) || "BT".equalsIgnoreCase(payment_brand) || "VA".equalsIgnoreCase(payment_brand))
            {
                Hash = CID + "|" + refIdPartner + "|" + payment_brand + "|" + amount + "|" + bankCode;
                token = infiPayUtils.getHmac256Signature(Hash, sKey.getBytes());
            }
            else
            {
                Hash = CID + "|" + refIdPartner + "|" + payment_brand + "|" + amount;
                token = infiPayUtils.getHmac256Signature(Hash, sKey.getBytes());
            }


            requestJSON.put("CID", CID);
            requestJSON.put("refIdPartner", refIdPartner);
            requestJSON.put("currency", currency);
            requestJSON.put("gateway", payment_brand);

            if ("IB".equalsIgnoreCase(payment_brand) || "BT".equalsIgnoreCase(payment_brand))
            {
                requestJSON.put("bank", bankCode);

            }
            if("MOMO".equalsIgnoreCase(payment_brand))
            {
                requestJSON.put("accNum", accNum);
            }
            requestJSON.put("amount", amount);
            requestJSON.put("token", token);


            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("TrackingID >>>>"+trackingID+" token >>>>>>>>>>>>>> "+token);
                facileroLogger.error("processSale() Request ---->" + trackingID + " RequestURL:" + REQUEST_URL + " RequestParameters" + requestJSON.toString());
            }else{
                transactionLogger.error("processSale() Request --->" + trackingID + " RequestURL:" + REQUEST_URL + " RequestParameters" + requestJSON.toString());
            }

            String response = InfiPayUtils.doPostHttpUrlConnection(REQUEST_URL, requestJSON.toString());


            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("processSale() Response--" + trackingID + "--->" + response);
            }else{
                transactionLogger.error("processSale() Response--" + trackingID + "--->" + response);
            }

            if (functions.isValueNull(response) && functions.isJSONValid(response))
            {


                String errorCode = "";
                String mess = "";
                String refId = "";
                String refIdPartnerIB = "";
                String gatewayIB = "";
                String amountIB = "";
                String url = "";

                String refIdPartnerBT = "";
                String gatewayBT = "";
                String amountBT = "";
                String bankBT   = "";
                String accNumBT = "";
                String accNameBT= "";
                String code = "";

                String phoneNum = "";
                String phoneName = "";


                JSONObject responseJson     = new JSONObject(response);
                JSONObject dataJSON         = null;
                JSONObject payment          = null;

                if (responseJson.has("errCode"))
                {
                    errorCode = responseJson.getString("errCode");
                }
                if (responseJson.has("mess"))
                {
                    mess = responseJson.getString("mess");
                }

                if (responseJson.has("data"))
                {
                    if (functions.isJSONValid(responseJson.getString("data")))
                    {
                        dataJSON = responseJson.getJSONObject("data");
                    }


                    if(dataJSON != null)
                    {
                        if (dataJSON.has("refId"))
                        {
                            refId = dataJSON.getString("refId");
                        }
                        if (dataJSON.has("refIdPartner"))
                        {
                            refIdPartnerIB = dataJSON.getString("refIdPartner");
                        }
                        if (dataJSON.has("gateway"))
                        {
                            gatewayIB = dataJSON.getString("gateway");
                        }
                        if (dataJSON.has("amount"))
                        {
                            amountIB = dataJSON.getString("amount");
                        }
                        if (dataJSON.has("payment"))
                        {
                            payment = dataJSON.getJSONObject("payment");

                            //Bank Transfer
                            if(payment.has("bank"))
                            {
                                bankBT = payment.getString("bank");
                            }
                            if(payment.has("accNum") )
                            {
                                accNumBT = payment.getString("accNum");
                            }
                            if(payment.has("accName") )
                            {
                                accNameBT = payment.getString("accName");
                            }
                            if(payment.has("code") )
                            {
                                code = payment.getString("code");
                            }

                            // MOMO
                            if(payment.has("phoneNum"))
                            {
                                phoneNum = payment.getString("phoneNum");
                            }
                            if (payment.has("phoneName"))
                            {
                                phoneName = payment.getString("phoneName");
                            }
                            if(payment.has("url") )
                            {
                                url = payment.getString("url");
                            }
                        }

                    }


                }


                if ("000".equalsIgnoreCase(errorCode))
                {
                    comm3DResponseVO.setStatus("pending3DConfirmation");
                    comm3DResponseVO.setTransactionStatus("pending3DConfirmation");
                    comm3DResponseVO.setUrlFor3DRedirect(url);
                    comm3DResponseVO.setTransactionId(refIdPartnerIB);
                    comm3DResponseVO.setAmount(amountIB);
                    comm3DResponseVO.setRemark(mess);
                    comm3DResponseVO.setDescription(mess);

                }
                else if ("001".equalsIgnoreCase(errorCode) || "1010".equalsIgnoreCase(errorCode) || "002".equalsIgnoreCase(errorCode) || "003".equalsIgnoreCase(errorCode)|| "004".equalsIgnoreCase(errorCode)|| "005".equalsIgnoreCase(errorCode)|| "006".equalsIgnoreCase(errorCode)|| "007".equalsIgnoreCase(errorCode)|| "010".equalsIgnoreCase(errorCode)|| "011".equalsIgnoreCase(errorCode)|| "012".equalsIgnoreCase(errorCode)|| "013".equalsIgnoreCase(errorCode) || "014".equalsIgnoreCase(errorCode) || "015".equalsIgnoreCase(errorCode))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(mess);
                    comm3DResponseVO.setDescription(mess);
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }



            }else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");

            }



        }
        catch (Exception e)
        {
            transactionLogger.error("SSLHandshakeException ==>" , e);
            PZExceptionHandler.raiseTechnicalViolationException(InfiPayPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processPayout(String trackingId,GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside Infipay process payout----");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = commRequestVO.getCardDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest                                      = gatewayAccount.isTest();
        CommAddressDetailsVO commAddressDetailsVO           = commRequestVO.getAddressDetailsVO();
        PayGUtils payGUtils = new PayGUtils();
        String sKey             = gatewayAccount.getFRAUD_FTP_PATH();
        transactionLogger.error("sKey ========>" + sKey);
        String CID              = gatewayAccount.getFRAUD_FILE_SHORT_NAME();
        String MID              = gatewayAccount.getFRAUD_FTP_USERNAME();
        transactionLogger.error("CID ========>" + CID);

        InfiPayUtils infiPayUtils                       = new InfiPayUtils();
        String bank         = "";
        String accName      = "";
        String refIdPartner    =  trackingId;
        String payment_brand= commTransactionDetailsVO.getCardType();
        transactionLogger.error("payment_brand0 >>>>>>>>>....>>>>>>>>"+payment_brand);
        String currency     = "";

        String accNum        =  "";
        String amount      =  "";
        String token         =  "";
        String REQUEST_URL  =  "";
        String REDIRECT_URL =  "";
        String Hash         = "";
        String transferType = "";
        String bankcode     = "";
        String bankName     = "";
        String cardType     = "";
        String gateway     = "";



        JSONObject jsonObject   = new JSONObject();
        String toType                =  "";
        try
        {
            payment_brand = InfiPayUtils.getPaymentBrand(payment_brand);
            transactionLogger.error("payment_brand >>>>>>>>>...>>>>>>>>"+payment_brand);

            if(functions.isValueNull(commTransactionDetailsVO.getTotype())){
                toType                =  commTransactionDetailsVO.getTotype();
            }
            transactionLogger.error("ToType ----> "+ trackingId+" "+toType);

            REQUEST_URL = RB.getString("PAYOUT_LIVE_URL");

            if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
            {
                currency = commTransactionDetailsVO.getCurrency();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getAmount()))
            {
                amount = InfiPayUtils.getAmount(commTransactionDetailsVO.getAmount());
            }
            if (functions.isValueNull(commTransactionDetailsVO.getCustomerBankAccountName()))
            {
                accName = commTransactionDetailsVO.getCustomerBankAccountName();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getBankAccountNo()))
            {
                accNum   = commTransactionDetailsVO.getBankAccountNo();
            }
            if (functions.isValueNull(commTransactionDetailsVO.getCustomerBankCode()))
            {
                bankcode    = commTransactionDetailsVO.getCustomerBankCode();
            }

            if(functions.isValueNull(commTransactionDetailsVO.getBankTransferType())){
                cardType    = commTransactionDetailsVO.getBankTransferType();
            }

            boolean bankTransferAllow=false;

            if(cardType.equalsIgnoreCase("BT") && payment_brand.equalsIgnoreCase("BT"))
            {
                gateway = "OUTBT";
                List<CommTransactionDetailsVO> bankCodeList = processInitialBankCode(trackingId, currency);

                for (CommTransactionDetailsVO payoutCode : bankCodeList)
                {
                    if (bankcode.equalsIgnoreCase(payoutCode.getBankAccountNo()) && payoutCode.getPayoutType().equalsIgnoreCase("true"))
                    {
                        bankcode            = payoutCode.getBankAccountNo();
                        bankName            = payoutCode.getCustomerBankAccountName();
                        bankTransferAllow   = true;
                        break;
                    }

                }

                if(!bankTransferAllow)
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark("Payout is not Active For "+bankName);
                    comm3DResponseVO.setDescription("Payout Is not Active For "+bankName);

                    return  comm3DResponseVO;
                }

                Hash    = CID+"|"+refIdPartner+"|"+gateway+"|"+amount+"|"+bankcode;
                token   = infiPayUtils.getHmac256Signature(Hash, sKey.getBytes());


            }else if (cardType.equalsIgnoreCase("MOMO") && payment_brand.equalsIgnoreCase("MOMO"))
            {
                gateway = "OUTMOMO";
                Hash = CID+"|"+refIdPartner+"|"+gateway+"|"+amount;
                token = infiPayUtils.getHmac256Signature(Hash, sKey.getBytes());
            }

            transactionLogger.error("gateway ------>  "+gateway +" Hash -----> "+Hash);
            transactionLogger.error("token for OUTBT >>>>>>>>>>>>>"+token);


            jsonObject.put("CID",CID);
            jsonObject.put("refIdPartner",refIdPartner);
            jsonObject.put("currency",currency);
            jsonObject.put("gateway",gateway);

            if(payment_brand.equalsIgnoreCase("BT"))
                jsonObject.put("bank",bankcode);

            jsonObject.put("amount",amount);
            jsonObject.put("accName",accName);
            jsonObject.put("accNum",accNum);
            jsonObject.put("token",token);



            transactionLogger.error("Request Log Parameters: " + trackingId + " " + jsonObject.toString());


            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error(" RequestURL  >>>>>>>>>>>>>> "+trackingId +" "+REQUEST_URL);
                facileroLogger.error("processPayout() Request --->" + trackingId + " " + jsonObject.toString());
            }else{
                transactionLogger.error(" RequestURL  >>>>>>>>>>>>>> "+trackingId +" "+REQUEST_URL);
                transactionLogger.error("processPayout() Request ---->" + trackingId + " " + jsonObject.toString());
            }


            String response = InfiPayUtils.doPostHttpUrlConnection(REQUEST_URL, jsonObject.toString());

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("processPayout() Response ---->" + trackingId + " RequestURL:" + REQUEST_URL + " Response " + response);
            }else{
                transactionLogger.error("processPayout() Response ---->" + trackingId + " RequestURL:" + REQUEST_URL + " Response " + response);
            }

            if (functions.isValueNull(response) && functions.isJSONValid(response) )
            {
                String errorCode          =  "";
                String mess          = "";
                String refId  =   "";
                String resrefIdPartner       =   "";
                String resgateway=   "";
                String resamount = "";
                String rescurrency = "";

                JSONObject responseJson = new JSONObject(response);
                JSONObject dataJSON    = null;


                if(responseJson.has("errCode"))
                {
                    errorCode = responseJson.getString("errCode");
                }
                if(responseJson.has("mess"))
                {
                    mess = responseJson.getString("mess");
                }

                if (responseJson.has("data") && responseJson.get("data")!= null)
                {
                    dataJSON = responseJson.getJSONObject("data");

                    if(dataJSON != null)
                    {
                        if (dataJSON.has("refId"))
                        {
                            refId = dataJSON.getString("refId");
                        }
                        if (dataJSON.has("refIdPartner"))
                        {
                            resrefIdPartner = dataJSON.getString("refIdPartner");
                        }
                        if (dataJSON.has("gateway"))
                        {
                            resgateway = dataJSON.getString("gateway");
                        }
                        if (dataJSON.has("amount"))
                        {
                            resamount = dataJSON.getString("amount");
                            double amt  = Double.parseDouble(resamount);
                            resamount      = String.format("%.2f", amt);
                        }
                        if (dataJSON.has("currency"))
                        {
                            rescurrency = dataJSON.getString("currency");
                        }
                    }
                }

                if ("001".equalsIgnoreCase(errorCode) || "1010".equalsIgnoreCase(errorCode) || "002".equalsIgnoreCase(errorCode) || "003".equalsIgnoreCase(errorCode)|| "004".equalsIgnoreCase(errorCode)|| "005".equalsIgnoreCase(errorCode)|| "006".equalsIgnoreCase(errorCode)|| "007".equalsIgnoreCase(errorCode)|| "010".equalsIgnoreCase(errorCode)|| "011".equalsIgnoreCase(errorCode)|| "012".equalsIgnoreCase(errorCode)|| "013".equalsIgnoreCase(errorCode) || "014".equalsIgnoreCase(errorCode) || "015".equalsIgnoreCase(errorCode))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(mess);
                    comm3DResponseVO.setDescription(mess);
                }
                else if("000".equalsIgnoreCase(errorCode) && "Successful create request".equalsIgnoreCase(mess))
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription(mess);
                    comm3DResponseVO.setRemark(mess);
                    comm3DResponseVO.setTransactionStatus("pending");
                }else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }

            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }




        }
        catch (Exception e){
            transactionLogger.error("Exception----trackingid---->"+trackingId+"--->",e);
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processQuery() of InfiPayPaymentGateway......");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        InfiPayUtils infiPayUtils                           = new InfiPayUtils();



        String REQUEST_URL  = "";
        String hash          = "";
        String refIdPartner    = "";
        String CID            =    gatewayAccount.getMerchantId();
        String token  = "";
        String sKey             = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String toType           = "";


        JSONObject requestJSON = new JSONObject();

        try
        {

            if(functions.isValueNull(commTransactionDetailsVO.getTotype())){
                toType                =  commTransactionDetailsVO.getTotype();
            }

            refIdPartner          = trackingID;
            REQUEST_URL        =   RB.getString("LIVE_INQUIRY_URL");
            hash                = CID+"|"+refIdPartner;
            token               = infiPayUtils.getHmac256Signature(hash, sKey.getBytes());


            requestJSON.put("CID", CID);
            requestJSON.put("refIdPartner", refIdPartner);
            requestJSON.put("token", token);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("processInquery() Request ---->" + trackingID + " RequestURL:"+REQUEST_URL+ " RequestParameters" +requestJSON.toString());
            }else{
                transactionLogger.error("processInquery() Request ---->" + trackingID + " RequestURL:"+REQUEST_URL+ " RequestParameters" +requestJSON.toString());
            }

            String response =   InfiPayUtils.doPostHttpUrlConnection(REQUEST_URL, requestJSON.toString());

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("processQuery() Response--" + trackingID + " " + response);
            }else{
                transactionLogger.error("processQuery() Response--" + trackingID + " " + response);
            }


            if (functions.isValueNull(response) && functions.isJSONValid(response) )
            {
                String errorCode          =  "";
                String mess          = "";
                String resCID    =   "";
                String refId  =   "";
                String resrefIdPartner       =   "";
                String gateway=   "";
                String gwd    =   "";
                String amount = "";
                String updated = "";
                String status = "";
                String err    = "";

                JSONObject responseJson = new JSONObject(response);
                JSONObject dataJSON    = null;


                if(responseJson.has("errorCode"))
                {
                    errorCode = responseJson.getString("errorCode");
                }
                if(responseJson.has("mess"))
                {
                    mess = responseJson.getString("mess");
                }

                if (responseJson.has("data"))
                {
                    dataJSON = responseJson.getJSONObject("data");

                    if(dataJSON != null)
                    {
                        if (dataJSON.has("updated"))
                        {
                            updated = dataJSON.getString("updated");
                        }
                        if (dataJSON.has("CID"))
                        {
                            resCID = dataJSON.getString("CID");
                        }
                        if (dataJSON.has("refId"))
                        {
                            refId = dataJSON.getString("refId");
                        }
                        if (dataJSON.has("refIdPartner"))
                        {
                            resrefIdPartner = dataJSON.getString("refIdPartner");
                        }
                        if (dataJSON.has("gateway"))
                        {
                            gateway = dataJSON.getString("gateway");
                        }
                        if (dataJSON.has("gwd"))
                        {
                            gwd = dataJSON.getString("gwd");
                        }
                        if (dataJSON.has("amount"))
                        {
                            amount = dataJSON.getString("amount");
                            double amt  = Double.parseDouble(amount);
                            amount      = String.format("%.2f", amt);
                        }
                        if (dataJSON.has("status"))
                        {
                            status = dataJSON.getString("status");
                        }
                        if (dataJSON.has("err"))
                        {
                            err = dataJSON.getString("err");
                        }
                    }
                }

                if("SUCCESS".equalsIgnoreCase(status))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setRemark(mess);
                    comm3DResponseVO.setCurrency(gatewayAccount.getCurrency());
                    comm3DResponseVO.setDescription(status);
                    comm3DResponseVO.setTransactionId(refIdPartner);
                    comm3DResponseVO.setResponseHashInfo(refIdPartner);
                    comm3DResponseVO.setResponseTime(updated);
                    comm3DResponseVO.setMerchantId(CID);
                }

                else if ("FAILED".equalsIgnoreCase(status))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setRemark(mess);
                    comm3DResponseVO.setDescription(status);
                    comm3DResponseVO.setTransactionId(refIdPartner);
                    comm3DResponseVO.setResponseHashInfo(refIdPartner);
                    comm3DResponseVO.setResponseTime(updated);
                    comm3DResponseVO.setMerchantId(CID);
                    comm3DResponseVO.setCurrency(gatewayAccount.getCurrency());
                }else if ("001".equalsIgnoreCase(errorCode))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(mess);
                    comm3DResponseVO.setDescription(mess);
                    comm3DResponseVO.setResponseTime(updated);
                    comm3DResponseVO.setMerchantId(CID);
                    comm3DResponseVO.setCurrency(gatewayAccount.getCurrency());
                }
                else if(status.equalsIgnoreCase("PROCESSING") || status.equalsIgnoreCase("HOLD"))
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription(status);  // set inquiry response not found
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setRemark(mess);
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setRemark(mess);
                    comm3DResponseVO.setDescription(status);
                    comm3DResponseVO.setTransactionId(refIdPartner);
                    comm3DResponseVO.setResponseHashInfo(refIdPartner);
                    comm3DResponseVO.setResponseTime(updated);
                    comm3DResponseVO.setMerchantId(CID);
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setCurrency(gatewayAccount.getCurrency());
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }

            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(InfiPayPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processPayoutInquiry(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Entering processPayoutQuery() of InfiPayPaymentGateway......");
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO                   = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        Functions functions                                 = new Functions();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        InfiPayUtils infiPayUtils                           = new InfiPayUtils();



        String REQUEST_URL  = "";
        String hash          = "";
        String refIdPartner    = "";
        String CID            =    gatewayAccount.getFRAUD_FILE_SHORT_NAME();
        String token  = "";
        String sKey             = gatewayAccount.getFRAUD_FTP_PATH();

        JSONObject requestJSON = new JSONObject();

        try
        {

            refIdPartner          = trackingID;
            REQUEST_URL        =   RB.getString("LIVE_PAYOUT_INQUIRY_URL");
            hash                = CID+"|"+refIdPartner;
            token               = infiPayUtils.getHmac256Signature(hash, sKey.getBytes());


            requestJSON.put("CID", CID);
            requestJSON.put("refIdPartner", refIdPartner);
            requestJSON.put("token", token);

            transactionLogger.error("processPayoutInquery() Request ---->" + trackingID + " RequestURL:"+REQUEST_URL+ " RequestParameters" +requestJSON.toString());
            String response =   InfiPayUtils.doPostHttpUrlConnection(REQUEST_URL, requestJSON.toString());
            transactionLogger.error("processPayoutQuery() Response--" + trackingID + " " + response);

            if (functions.isValueNull(response) && functions.isJSONValid(response) )
            {
                String errorCode          =  "";
                String mess          = "";
                String resCID    =   "";
                String refId  =   "";
                String resrefIdPartner       =   "";
                String gateway=   "";
                String gwd    =   "";
                String amount = "";
                String updated = "";
                String status = "";
                String err    = "";

                JSONObject responseJson = new JSONObject(response);
                JSONObject dataJSON    = null;


                if(responseJson.has("errorCode"))
                {
                    errorCode = responseJson.getString("errorCode");
                }
                if(responseJson.has("mess"))
                {
                    mess = responseJson.getString("mess");
                }

                if (responseJson.has("data"))
                {
                    dataJSON = responseJson.getJSONObject("data");

                    if(dataJSON != null)
                    {
                        if (dataJSON.has("updated"))
                        {
                            updated = dataJSON.getString("updated");
                        }
                        if (dataJSON.has("CID"))
                        {
                            resCID = dataJSON.getString("CID");
                        }
                        if (dataJSON.has("refId"))
                        {
                            refId = dataJSON.getString("refId");
                        }
                        if (dataJSON.has("refIdPartner"))
                        {
                            resrefIdPartner = dataJSON.getString("refIdPartner");
                        }
                        if (dataJSON.has("gateway"))
                        {
                            gateway = dataJSON.getString("gateway");
                        }
                        if (dataJSON.has("gwd"))
                        {
                            gwd = dataJSON.getString("gwd");
                        }
                        if (dataJSON.has("amount"))
                        {
                            amount = dataJSON.getString("amount");
                            double amt  = Double.parseDouble(amount);
                            amount      = String.format("%.2f", amt);
                        }
                        if (dataJSON.has("status"))
                        {
                            status = dataJSON.getString("status");
                        }
                        if (dataJSON.has("err"))
                        {
                            err = dataJSON.getString("err");
                        }
                    }
                }

                if("SUCCESS".equalsIgnoreCase(status))
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionStatus("success");
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setRemark(mess);
                    comm3DResponseVO.setCurrency(gatewayAccount.getCurrency());
                    comm3DResponseVO.setDescription(status);
                    comm3DResponseVO.setTransactionId(refIdPartner);
                    comm3DResponseVO.setResponseHashInfo(refIdPartner);
                    comm3DResponseVO.setResponseTime(updated);
                    comm3DResponseVO.setMerchantId(CID);
                }

                else if ("FAILED".equalsIgnoreCase(status))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setRemark(mess);
                    comm3DResponseVO.setDescription(status);
                    comm3DResponseVO.setTransactionId(refIdPartner);
                    comm3DResponseVO.setResponseHashInfo(refIdPartner);
                    comm3DResponseVO.setResponseTime(updated);
                    comm3DResponseVO.setMerchantId(CID);
                    comm3DResponseVO.setCurrency(gatewayAccount.getCurrency());
                }else if ("001".equalsIgnoreCase(errorCode))
                {
                    comm3DResponseVO.setStatus("failed");
                    comm3DResponseVO.setTransactionStatus("failed");
                    comm3DResponseVO.setRemark(mess);
                    comm3DResponseVO.setDescription(mess);
                    comm3DResponseVO.setResponseTime(updated);
                    comm3DResponseVO.setMerchantId(CID);
                    comm3DResponseVO.setCurrency(gatewayAccount.getCurrency());
                }
                else if(status.equalsIgnoreCase("PROCESSING") || status.equalsIgnoreCase("HOLD"))
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription(status);  // set inquiry response not found
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setRemark(mess);
                    comm3DResponseVO.setAmount(amount);
                    comm3DResponseVO.setRemark(mess);
                    comm3DResponseVO.setDescription(status);
                    comm3DResponseVO.setTransactionId(refIdPartner);
                    comm3DResponseVO.setResponseHashInfo(refIdPartner);
                    comm3DResponseVO.setResponseTime(updated);
                    comm3DResponseVO.setMerchantId(CID);
                    comm3DResponseVO.setTransactionStatus("pending");
                    comm3DResponseVO.setCurrency(gatewayAccount.getCurrency());
                }
                else
                {
                    comm3DResponseVO.setStatus("pending");
                    comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    comm3DResponseVO.setTransactionStatus("pending");
                }

            }
            else
            {
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                comm3DResponseVO.setTransactionStatus("pending");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(InfiPayPaymentGateway.class.getName(), "processQuery()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }


}
