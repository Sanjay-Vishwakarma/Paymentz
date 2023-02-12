package com.payment.repropay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericDeviceDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.sabadell.SabadellUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Jyoti on 12/3/2021.
 */
public class ReproPayPaymentGateway extends AbstractPaymentGateway
{

    private static TransactionLogger transactionLogger=new TransactionLogger(ReproPayPaymentGateway.class.getName());

    private static Logger log=new Logger(ReproPayPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE="repropay";
    ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.Repropay");
    //private static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.Repropay");
    public ReproPayPaymentGateway(String accountId){
        this.accountId=accountId;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("******Inside ReproPayPaymentGateway processSale()*************");
        log.error("******Inside ReproPayPaymentGateway processSale()*************");

        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO=new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        Functions functions=new Functions();
        ReproPayUtills reproPayUtills=new ReproPayUtills();

        String DS_MERCHANT_ORDER            = trackingID;
        String DS_MERCHANT_AMOUNT           =reproPayUtills.getAmount(commTransactionDetailsVO.getAmount());
        String DS_MERCHANT_CURRENCY         =CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency());
        String DS_MERCHANT_MERCHANTCODE     =gatewayAccount.getMerchantId();//"240100131";

        String DS_MERCHANT_TERMINAL="100";
        String DS_MERCHANT_TRANSACTIONTYPE="0";

        String termUrl = "";
        boolean isTest=gatewayAccount.isTest();
        String REQUEST_URL="";

        String DS_MERCHANT_URLKO="";
        String DS_MERCHANT_URLOK="";

        //notification URL
        String DS_MERCHANT_MERCHANTURL = RB.getString("NOTIFICATION_URL")+trackingID;

        try
        {

            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
               // termUrl = "http://localhost:8081"+RB.getString("HOST_URL");
                transactionLogger.error("from host url----"+termUrl);
            }
            else
            {
                termUrl = RB.getString("RETURN_URL");
                transactionLogger.error("from RB----"+termUrl);
            }

            //return url
            DS_MERCHANT_URLKO    =termUrl+trackingID;
            DS_MERCHANT_URLOK    =termUrl+trackingID;


            if(isTest){
                REQUEST_URL =RB.getString("TEST_URL");
            }
            else {
                REQUEST_URL =RB.getString("LIVE_URL");
            }

            ReproPayUtills sha256 = new ReproPayUtills();

            transactionLogger.error("DS_MERCHANT_MERCHANTURL  "+DS_MERCHANT_MERCHANTURL);
            transactionLogger.error("DS_MERCHANT_URLOK  "+DS_MERCHANT_URLOK);
            transactionLogger.error("DS_MERCHANT_URLKO  "+DS_MERCHANT_URLKO);


            sha256.setParameter("DS_MERCHANT_AMOUNT", DS_MERCHANT_AMOUNT);
            sha256.setParameter("DS_MERCHANT_ORDER", DS_MERCHANT_ORDER);
            sha256.setParameter("DS_MERCHANT_MERCHANTCODE", DS_MERCHANT_MERCHANTCODE);
            sha256.setParameter("DS_MERCHANT_CURRENCY", DS_MERCHANT_CURRENCY);
            sha256.setParameter("DS_MERCHANT_TRANSACTIONTYPE", DS_MERCHANT_TRANSACTIONTYPE);
            sha256.setParameter("DS_MERCHANT_TERMINAL", DS_MERCHANT_TERMINAL);
            sha256.setParameter("DS_MERCHANT_MERCHANTURL", DS_MERCHANT_MERCHANTURL);
            sha256.setParameter("DS_MERCHANT_URLOK", DS_MERCHANT_URLOK);
            sha256.setParameter("DS_MERCHANT_URLKO",DS_MERCHANT_URLKO);

            transactionLogger.error("Request Parameter  " + trackingID + "---" + sha256.toString());

            String params = sha256.createMerchantParameters();
            transactionLogger.error("DS_MERCHANT_AMOUNT--" + trackingID + "---" + DS_MERCHANT_AMOUNT);

            transactionLogger.error("params--" + trackingID + "---" + params);
            System.out.println("DS_MERCHANT_ORDER-----"+DS_MERCHANT_ORDER);


            String key=gatewayAccount.getFRAUD_FTP_USERNAME();//sq7HjrUOBfKmC576ILgskD5srU870gJ7
            String HMAC_SHA_Version=gatewayAccount.getFRAUD_FTP_PATH();//"HMAC_SHA256_V1"

            String signature=sha256.createMerchantSignature(key);
            System.out.println("Signature-----"+signature);

            HashMap<String,String> map=new HashMap<String,String>();
            map.put("Ds_MerchantParameters",params);
            map.put("Ds_SignatureVersion",HMAC_SHA_Version);
            map.put("Ds_Signature",signature);

            transactionLogger.error("Sale JSON request-for--" + trackingID + "-->"+map);

            commResponseVO.setRequestMap(map);
            commResponseVO.setUrlFor3DRedirect(REQUEST_URL);
            commResponseVO.setStatus("pending3DConfirmation");
            transactionLogger.error("final requestMap ---> "+trackingID+" "+map);


        }catch (Exception e){
            e.printStackTrace();
        }


        return commResponseVO;
    }

    @Override
    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(" ---- Autoredict in  ReproPay ---- ");
        String html                         = "";
        Comm3DResponseVO transRespDetails   = null;
        MerchantDetailsVO merchantDetailsVO = null;

        MerchantDAO merchantDAO             = new MerchantDAO();

        merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        ReproPayUtills reproPayUtills                           = new ReproPayUtills();
        ReproPayPaymentProcess reproPayPaymentProcess           = new ReproPayPaymentProcess();
        CommRequestVO commRequestVO          = reproPayUtills.getReproPayRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount       = GatewayAccountService.getGatewayAccount(accountId);

        try
        {
            transactionLogger.error("isService Flag -----" + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionLogger.error("autoredirect  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionLogger.error("addressdetail  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionLogger.error("terminal id  is -----" + commonValidatorVO.getTerminalId());
            transactionLogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());

            if(("n").equalsIgnoreCase(merchantDetailsVO.getIsService())) {
                transRespDetails = (Comm3DResponseVO) this.processAuthentication(commonValidatorVO.getTrackingid(), commRequestVO);
            }
            else {
                transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            }
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = reproPayPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionLogger.error("automatic redirect Repropay form -- >>"+html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in IMoneyPayPaymentGateway---", e);
        }
        return html;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {

        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("ReproPayPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);

    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("******Inside ReproPayPaymentGateway processRefund()*************");
        log.error("******Inside ReproPayPaymentGateway processRefund()*************");

        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO=new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        Functions functions=new Functions();
        ReproPayUtills reproPayUtills=new ReproPayUtills();
        ReproPayPaymentProcess reproPayPaymentProcess=new ReproPayPaymentProcess();

        String DS_MERCHANT_ORDER            =                 trackingID;
        String DS_MERCHANT_AMOUNT           =reproPayUtills.getAmount(commTransactionDetailsVO.getAmount());
        String DS_MERCHANT_CURRENCY         =CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency());
        String DS_MERCHANT_MERCHANTCODE     =gatewayAccount.getMerchantId();//"240100131";
        String DS_MERCHANT_MERCHANTURL      =RB.getString("NOTIFICATION_URL")+trackingID;//"https://staging.paymentz.com/transaction/Common3DFrontEndServlet";
        String DS_MERCHANT_TERMINAL="100";
        String DS_MERCHANT_TRANSACTIONTYPE="3";

        String termUrl = "";
        boolean isTest=gatewayAccount.isTest();
        String REQUEST_URL="";
        String DS_MERCHANT_URLKO="";
        String DS_MERCHANT_URLOK="";

        try
        {


            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
                transactionLogger.error("from host url----"+termUrl);
            }
            else
            {
                termUrl = RB.getString("RETURN_URL");
                transactionLogger.error("from RB----"+termUrl);
            }

            //return url
            DS_MERCHANT_URLKO    =termUrl+trackingID;
            DS_MERCHANT_URLOK    =termUrl+trackingID;


            if(isTest){
                REQUEST_URL =RB.getString("TEST_URL");
            }
            else {
                REQUEST_URL =RB.getString("LIVE_URL");
            }

            transactionLogger.error("DS_MERCHANT_MERCHANTURL ===========>"+DS_MERCHANT_MERCHANTURL);
            transactionLogger.error("DS_MERCHANT_AMOUNT ===========>"+DS_MERCHANT_AMOUNT);
            transactionLogger.error("DS_MERCHANT_ORDER ===========>"+DS_MERCHANT_ORDER);
            transactionLogger.error("DS_MERCHANT_MERCHANTCODE ===========>"+DS_MERCHANT_MERCHANTCODE);
            transactionLogger.error("DS_MERCHANT_CURRENCY ===========>"+DS_MERCHANT_CURRENCY);
            transactionLogger.error("DS_MERCHANT_TRANSACTIONTYPE ===========>"+DS_MERCHANT_TRANSACTIONTYPE);
            transactionLogger.error("DS_MERCHANT_TERMINAL===========>"+DS_MERCHANT_TERMINAL);

            ReproPayUtills sha256 = new ReproPayUtills();

            sha256.setParameter("DS_MERCHANT_AMOUNT", DS_MERCHANT_AMOUNT);
            sha256.setParameter("DS_MERCHANT_ORDER", DS_MERCHANT_ORDER);
            sha256.setParameter("DS_MERCHANT_MERCHANTCODE", DS_MERCHANT_MERCHANTCODE);
            sha256.setParameter("DS_MERCHANT_CURRENCY", DS_MERCHANT_CURRENCY);
            sha256.setParameter("DS_MERCHANT_TRANSACTIONTYPE", DS_MERCHANT_TRANSACTIONTYPE);
            sha256.setParameter("DS_MERCHANT_TERMINAL", DS_MERCHANT_TERMINAL);
            sha256.setParameter("DS_MERCHANT_MERCHANTURL", DS_MERCHANT_MERCHANTURL);

            transactionLogger.error("request--" + trackingID + "---" + sha256);

            boolean validateJson= reproPayUtills.isJSONValid(sha256.toString());

            transactionLogger.error("validateJson======>"+validateJson);

            String params = sha256.createMerchantParameters();

            transactionLogger.error("params--" + trackingID + "---" + params);
            System.out.println("DS_MERCHANT_ORDER-----" + DS_MERCHANT_ORDER);

            String key=gatewayAccount.getFRAUD_FTP_USERNAME();//sq7HjrUOBfKmC576ILgskD5srU870gJ7
            String HMAC_SHA_Version=gatewayAccount.getFRAUD_FTP_PATH();//"HMAC_SHA256_V1"

            String signature=sha256.createMerchantSignature(key);
            transactionLogger.error("Signature-----"+signature);


            String request="Ds_SignatureVersion="+HMAC_SHA_Version+"" +
                    "&Ds_MerchantParameters="+params+
                    "&Ds_Signature="+signature+"";
            transactionLogger.error("request-----for--" + trackingID +" -->" + request);

            String response="";
            if(isTest){
                response= reproPayUtills.doPostHTTPSURLConnection(RB.getString("TEST_URL"), request);
            }else {
                response= reproPayUtills.doPostHTTPSURLConnection(RB.getString("LIVE_URL"), request);
            }
            transactionLogger.debug("response-----for--" + trackingID + "-->"+response);


           /*
           *//* HttpClient httpClient = new HttpClient();
            PostMethod postMethod = new PostMethod(response);

            httpClient.executeMethod(postMethod);
            transactionLogger.error("Response Refund code---"+postMethod.getStatusCode());
*/
            TransactionManager transactionManager = new TransactionManager();
            transactionLogger.error("inside if ");
            TransactionDetailsVO updatedTransactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingID);
            if("Reversed".equalsIgnoreCase(updatedTransactionDetailsVO.getStatus())){
                transactionLogger.error("inside  if reversed condition ");
                commResponseVO.setStatus("success");
                commResponseVO.setAmount(updatedTransactionDetailsVO.getAmount());
                commResponseVO.setAmount(updatedTransactionDetailsVO.getAmount());
                commResponseVO.setCurrency(updatedTransactionDetailsVO.getCurrency());
                commResponseVO.setRemark(updatedTransactionDetailsVO.getRemark());
                return commResponseVO;

            }
            else{
                commResponseVO.setStatus("pending");
                commResponseVO.setRemark("pending");

            }



        }catch (Exception e){
            e.printStackTrace();
        }


        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {

        transactionLogger.error("******Inside ReproPayPaymentGateway processSale()*************");
        log.error("******Inside ReproPayPaymentGateway processSale()*************");

        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO=new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        Functions functions=new Functions();
        ReproPayUtills reproPayUtills=new ReproPayUtills();

        String DS_MERCHANT_ORDER            = trackingID;
        String DS_MERCHANT_AMOUNT           =reproPayUtills.getAmount(commTransactionDetailsVO.getAmount());
        String DS_MERCHANT_CURRENCY         =CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency());
        String DS_MERCHANT_MERCHANTCODE     =gatewayAccount.getMerchantId();//"240100131";
        String DS_MERCHANT_MERCHANTURL      =RB.getString("MERCHANT_URL")+trackingID;//"https://staging.paymentz.com/transaction/Common3DFrontEndServlet";
        String DS_MERCHANT_TERMINAL="100";
        String DS_MERCHANT_TRANSACTIONTYPE="2";

        String termUrl = "";
        boolean isTest=gatewayAccount.isTest();
        String REQUEST_URL="";
        String DS_MERCHANT_URLKO="";
        String DS_MERCHANT_URLOK="";

        try
        {

            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
                transactionLogger.error("from host url----"+termUrl);
            }
            else
            {
                termUrl = RB.getString("RETURN_URL");
                transactionLogger.error("from RB----"+termUrl);
            }

            DS_MERCHANT_URLKO    =termUrl+trackingID;//"https://staging.paymentz.com/transaction/Common3DFrontEndServlet";
            DS_MERCHANT_URLOK    =termUrl+trackingID;//"https://staging.paymentz.com/transaction/Common3DFrontEndServlet";


            if(isTest){
                REQUEST_URL =RB.getString("TEST_URL");
            }
            else {
                REQUEST_URL =RB.getString("LIVE_URL");
            }
            ReproPayUtills sha256 = new ReproPayUtills();

            sha256.setParameter("DS_MERCHANT_AMOUNT", DS_MERCHANT_AMOUNT);
            sha256.setParameter("DS_MERCHANT_ORDER", DS_MERCHANT_ORDER);
            sha256.setParameter("DS_MERCHANT_MERCHANTCODE", DS_MERCHANT_MERCHANTCODE);
            sha256.setParameter("DS_MERCHANT_CURRENCY", DS_MERCHANT_CURRENCY);
            sha256.setParameter("DS_MERCHANT_TRANSACTIONTYPE", DS_MERCHANT_TRANSACTIONTYPE);
            sha256.setParameter("DS_MERCHANT_TERMINAL", DS_MERCHANT_TERMINAL);
            sha256.setParameter("DS_MERCHANT_MERCHANTURL", DS_MERCHANT_MERCHANTURL);
            sha256.setParameter("DS_MERCHANT_URLOK", DS_MERCHANT_URLOK);
            sha256.setParameter("DS_MERCHANT_URLKO",DS_MERCHANT_URLKO);

            String params = sha256.createMerchantParameters();

            transactionLogger.error("params--" + trackingID + "---" + params);
            System.out.println("DS_MERCHANT_ORDER-----" + DS_MERCHANT_ORDER);

            //String key="sq7HjrUOBfKmC576ILgskD5srU870gJ7";
            String key=gatewayAccount.getFRAUD_FTP_USERNAME();//sq7HjrUOBfKmC576ILgskD5srU870gJ7
            String HMAC_SHA_Version=gatewayAccount.getFRAUD_FTP_PATH();//"HMAC_SHA256_V1"

            String signature=sha256.createMerchantSignature(key);
            System.out.println("Signature-----"+signature);

            HashMap<String,String> map=new HashMap<String,String>();
            map.put("Ds_MerchantParameters",params);
            map.put("Ds_SignatureVersion",HMAC_SHA_Version);
            map.put("Ds_Signature",signature);


            transactionLogger.error("Capture JSON request=========for--" + trackingID + "-->"+map);

            commResponseVO.setRequestMap(map);
            commResponseVO.setUrlFor3DRedirect(REQUEST_URL);
            commResponseVO.setStatus("pending3DConfirmation");
            transactionLogger.error("final requestMap ---> "+trackingID+" "+map);


        }catch (Exception e){
            e.printStackTrace();
        }


        return commResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("******Inside ReproPayPaymentGateway processSale()*************");
        log.error("******Inside ReproPayPaymentGateway processSale()*************");

        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO=new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        Functions functions=new Functions();
        ReproPayUtills reproPayUtills=new ReproPayUtills();

        String DS_MERCHANT_ORDER            = trackingID;
        String DS_MERCHANT_AMOUNT           =reproPayUtills.getAmount(commTransactionDetailsVO.getAmount());
        String DS_MERCHANT_CURRENCY         =CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency());
        String DS_MERCHANT_MERCHANTCODE     =gatewayAccount.getMerchantId();//"240100131";
        String DS_MERCHANT_MERCHANTURL      =RB.getString("MERCHANT_URL")+trackingID;//"https://staging.paymentz.com/transaction/Common3DFrontEndServlet";
        String DS_MERCHANT_TERMINAL="100";
        String DS_MERCHANT_TRANSACTIONTYPE="9";

        String termUrl = "";
        boolean isTest=gatewayAccount.isTest();
        String REQUEST_URL="";
        String DS_MERCHANT_URLKO="";
        String DS_MERCHANT_URLOK="";

        try
        {

            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
                transactionLogger.error("from host url----"+termUrl);
            }
            else
            {
                termUrl = RB.getString("RETURN_URL");
                transactionLogger.error("from RB----"+termUrl);
            }

            DS_MERCHANT_URLKO    =termUrl+trackingID;//"https://staging.paymentz.com/transaction/Common3DFrontEndServlet";
            DS_MERCHANT_URLOK    =termUrl+trackingID;//"https://staging.paymentz.com/transaction/Common3DFrontEndServlet";


            if(isTest){
                REQUEST_URL =RB.getString("TEST_URL");
            }
            else {
                REQUEST_URL =RB.getString("LIVE_URL");
            }
            ReproPayUtills sha256 = new ReproPayUtills();

            sha256.setParameter("DS_MERCHANT_AMOUNT", DS_MERCHANT_AMOUNT);
            sha256.setParameter("DS_MERCHANT_ORDER", DS_MERCHANT_ORDER);
            sha256.setParameter("DS_MERCHANT_MERCHANTCODE", DS_MERCHANT_MERCHANTCODE);
            sha256.setParameter("DS_MERCHANT_CURRENCY", DS_MERCHANT_CURRENCY);
            sha256.setParameter("DS_MERCHANT_TRANSACTIONTYPE", DS_MERCHANT_TRANSACTIONTYPE);
            sha256.setParameter("DS_MERCHANT_TERMINAL", DS_MERCHANT_TERMINAL);
            sha256.setParameter("DS_MERCHANT_MERCHANTURL", DS_MERCHANT_MERCHANTURL);
            sha256.setParameter("DS_MERCHANT_URLOK", DS_MERCHANT_URLOK);
            sha256.setParameter("DS_MERCHANT_URLKO",DS_MERCHANT_URLKO);

            String params = sha256.createMerchantParameters();

            transactionLogger.error("params--" + trackingID + "---" + params);
            System.out.println("DS_MERCHANT_ORDER-----"+DS_MERCHANT_ORDER);

            //String key="sq7HjrUOBfKmC576ILgskD5srU870gJ7";
            String key=gatewayAccount.getFRAUD_FTP_USERNAME();//sq7HjrUOBfKmC576ILgskD5srU870gJ7
            String HMAC_SHA_Version=gatewayAccount.getFRAUD_FTP_PATH();//"HMAC_SHA256_V1"

            String signature=sha256.createMerchantSignature(key);
            System.out.println("Signature-----"+signature);

            HashMap<String,String> map=new HashMap<String,String>();
            map.put("Ds_MerchantParameters",params);
            map.put("Ds_SignatureVersion",HMAC_SHA_Version);
            map.put("Ds_Signature",signature);

            transactionLogger.error("Cancel JSON request===========>for--" + trackingID + "-->"+map);

            commResponseVO.setRequestMap(map);
            commResponseVO.setUrlFor3DRedirect(REQUEST_URL);
            commResponseVO.setStatus("pending3DConfirmation");
            transactionLogger.error("final requestMap ---> "+trackingID+" "+map);


        }catch (Exception e){
            e.printStackTrace();
        }
        return commResponseVO;
    }
}
