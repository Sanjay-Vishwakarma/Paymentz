package com.directi.pg.core.paymentgateway;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.cup.CupUtils;
import com.directi.pg.core.valueObjects.CupRequestVO;
import com.directi.pg.core.valueObjects.CupResponseVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: A135654
 * Date: Nov 8, 2012
 * Time: 1:03:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class CUPPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(CUPPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(CUPPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "CUP";

    private String backstagePayUrl;
    private String queryUrl;
    private String securityKey;

    public CUPPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Process Purchase = Pre-Auth + Auth Completion(Capture)
     *
     * @param trackingID
     * @param requestVO
     * @return
     * @throws SystemError
     */
    public GenericResponseVO processPurchase(String trackingID, GenericRequestVO requestVO)
    {
        return null;
    }
    public GenericResponseVO processPurchaseCancellation(String trackingID, GenericRequestVO requestVO)
    {
        return null;
    }
    public CUPPaymentGateway(){

    }

    public GenericResponseVO processRefund(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CupRequestVO request = (CupRequestVO) requestVO;
        CupResponseVO response = new CupResponseVO();
        initializeGateway(request.getTestAccount());
        String[] valueVo = {
                CupUtils.version,//Protocol version
                CupUtils.charset,
                "31",//request.getTransType(),
                request.getTransactionId(),
                request.getMerchantId(),
                request.getMerchantName(),
                CupUtils.acqCode,
                request.getMcc(),
                "",
                "",
                "",
                "",
                "",
                "",
                request.getDetailsId(),
                request.getAmount(),
                request.getCurrencyCode(),
                request.getOrderTime(),
                request.getIp(),
                "",
                "",
                "",
                CupUtils.transTimeout,
                CupUtils.merFrontEndUrl,
                CupUtils.merBackEndUrl,
                ""
        };

        log.debug("-----Refund Value VO-----"+valueVo.toString());
        transactionLogger.debug("-----Refund Value VO-----"+valueVo.toString());
        //CupUtils.doPostQueryCmd("http://202.101.25.184/UpopWeb/api/BSPay.action", new QuickPayUtils().createBackStrForBackTrans(valueVo, QuickPayConf.reqVo)

        String req = CupUtils.createBackStrForBackTrans(valueVo, CupUtils.reqVo, securityKey);
        log.debug("---Refund Request---"+req);
        transactionLogger.debug("---Refund Request---"+req);
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            URL url = new URL(backstagePayUrl);
            URLConnection con = url.openConnection();

            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            out = new BufferedOutputStream(con.getOutputStream());
            byte[] outBuf = req.getBytes("UTF-8");
            out.write(outBuf);
            out.close();
            in = new BufferedInputStream(con.getInputStream());
            result = CupUtils.ReadByteStream(in);
        }
        catch (MalformedURLException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CUPPaymentGateway.class.getName(),"processRefund()",null,"common","MalFormed URL Exception while Refunding transaction", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,e.getMessage(),e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CUPPaymentGateway.class.getName(), "processRefund()", null, "common", "UnSupported Encoding  Exception while Refunding transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CUPPaymentGateway.class.getName(), "processRefund()", null, "common", "IO Exception while Refunding transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally {
            if (out != null)
                try {
                    out.close();
                }
                catch (IOException localIOException2) {
                }
            if (in != null)
                try {
                    in.close();
                }
                catch (IOException localIOException3) {
                }
        }
        Map<String, String> map = CupUtils.createResultMap(result);
        response.setResponseCode(map.get("respCode"));
        response.setStatusDescription(map.get("respMsg"));
        response.setTransactionID(map.get("qid"));
        response.setProcessingTime(map.get("respTime"));
        return response;
    }
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CupRequestVO request = (CupRequestVO) requestVO;
        CupResponseVO response = new CupResponseVO();
        initializeGateway("Y");
        String[] valueVo = {
                CupUtils.version,//Protocol version
                CupUtils.charset,
                request.getTransType(),
                request.getMerchantId(),
                request.getDetailsId(),
                request.getOrderTime(),
                "{acqCode="+CupUtils.acqCode+"}"
        };


        String req = CupUtils.createBackStrForBackTrans(valueVo, CupUtils.queryVo, securityKey);
        //System.out.println("request---"+req);
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {

            URL url = new URL(queryUrl);
            URLConnection con = url.openConnection();

            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            out = new BufferedOutputStream(con.getOutputStream());
            byte[] outBuf = req.getBytes("UTF-8");
            out.write(outBuf);
            out.close();
            in = new BufferedInputStream(con.getInputStream());
            result = CupUtils.ReadByteStream(in);
        }
        catch (MalformedURLException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CUPPaymentGateway.class.getName(),"processQuery()",null,"common","MalFormed URL Exception while Querying transaction", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,e.getMessage(),e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CUPPaymentGateway.class.getName(), "processQuery()", null, "common", "UnSupported Encoding Exception while Querying transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null,e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(CUPPaymentGateway.class.getName(), "processQuery()", null, "common", "IO Exception while Querying transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null,e.getMessage(), e.getCause());
        }
        finally {
            if (out != null)
                try {
                    out.close();
                }
                catch (IOException localIOException2) {
                }
            if (in != null)
                try {
                    in.close();
                }
                catch (IOException localIOException3) {
                }
        }
        Map<String, String> map = CupUtils.createResultMap(result);
        response.setResponseCode(map.get("respCode"));
        response.setStatusDescription(map.get("respMsg"));
        response.setTransactionID(map.get("qid"));
        response.setProcessingTime(map.get("respTime"));

        /*System.out.println("Response code---"+map.get("respCode"));
        System.out.println("Response Msg---"+map.get("queryResult"));*/

        return response;
    }
    public GenericResponseVO processPreAuthorization(String trackingID,GenericRequestVO requestVO)
   {
           return null;
   }
    public GenericResponseVO processPreAuthorizationCancellation(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
       CupRequestVO request = (CupRequestVO) requestVO;
       CupResponseVO response = new CupResponseVO();
       initializeGateway(request.getTestAccount());
       String[] valueVo = {
               CupUtils.version,//Protocol version
               CupUtils.charset,
               request.getTransType(),
               request.getTransactionId(),
               request.getMerchantId(),
               request.getMerchantName(),
               CupUtils.acqCode,
               request.getMcc(),
               "",
               "",
               "",
               "",
               "",
               "",
               request.getDetailsId(),
               request.getAmount(),
               request.getCurrencyCode(),
               request.getOrderTime(),
               request.getIp(),
               "",
               "",
               "",
               CupUtils.transTimeout,
               CupUtils.merFrontEndUrl,
               CupUtils.merBackEndUrl,
               ""
       };


       //CupUtils.doPostQueryCmd("http://202.101.25.184/UpopWeb/api/BSPay.action", new QuickPayUtils().createBackStrForBackTrans(valueVo, QuickPayConf.reqVo)

       String req = CupUtils.createBackStrForBackTrans(valueVo, CupUtils.reqVo, securityKey);
       String result = null;
       BufferedInputStream in = null;
       BufferedOutputStream out = null;
       try {

           URL url = new URL(backstagePayUrl);
           URLConnection con = url.openConnection();

           con.setUseCaches(false);
           con.setDoInput(true);
           con.setDoOutput(true);
           out = new BufferedOutputStream(con.getOutputStream());
           byte[] outBuf = req.getBytes("UTF-8");
           out.write(outBuf);
           out.close();
           in = new BufferedInputStream(con.getInputStream());
           result = CupUtils.ReadByteStream(in);
       }
       catch (MalformedURLException e)
       {
           PZExceptionHandler.raiseTechnicalViolationException(CUPPaymentGateway.class.getName(), "processPreAuthorizationCancellation()", null, "common", "MalFormed URL Exception while Pre Auth Cancellation transaction", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null, e.getMessage(), e.getCause());
       }
       catch (UnsupportedEncodingException e)
       {
           PZExceptionHandler.raiseTechnicalViolationException(CUPPaymentGateway.class.getName(), "processPreAuthorizationCancellation()", null, "common", "UnSupported Encoding Exception while Pre Auth Cancellation transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
       }
       catch (IOException e)
       {
           PZExceptionHandler.raiseTechnicalViolationException(CUPPaymentGateway.class.getName(), "processPreAuthorizationCancellation()", null, "common", "IO Exception while Pre Auth Cancellation transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
       }
       finally {
           if (out != null)
               try {
                   out.close();
               }
               catch (IOException localIOException2) {
               }
           if (in != null)
               try {
                   in.close();
               }
               catch (IOException localIOException3) {
               }
       }
       Map<String, String> map = CupUtils.createResultMap(result);
       response.setResponseCode(map.get("respCode"));
       response.setStatusDescription(map.get("respMsg"));
       response.setTransactionID(map.get("qid"));
       response.setProcessingTime(map.get("respTime"));
       return response;
   }
   public GenericResponseVO processPreAuthorizationCompletionCancellation(String trackingID,GenericRequestVO requestVO)
   {
       CupRequestVO request = (CupRequestVO) requestVO;
       CupResponseVO response = new CupResponseVO();
       initializeGateway(request.getTestAccount());
       String[] valueVo = {
               CupUtils.version,//Protocol version
               CupUtils.charset,
               request.getTransType(),
               request.getTransactionId(),
               request.getMerchantId(),
               request.getMerchantName(),
               CupUtils.acqCode,
               request.getMcc(),
               "",
               "",
               "",
               "",
               "",
               "",
               request.getDetailsId(),
               request.getAmount(),
               request.getCurrencyCode(),
               request.getOrderTime(),
               request.getIp(),
               "",
               "",
               "",
               CupUtils.transTimeout,
               CupUtils.merFrontEndUrl,
               CupUtils.merBackEndUrl,
               ""
       };


       //CupUtils.doPostQueryCmd("http://202.101.25.184/UpopWeb/api/BSPay.action", new QuickPayUtils().createBackStrForBackTrans(valueVo, QuickPayConf.reqVo)

       String req = CupUtils.createBackStrForBackTrans(valueVo, CupUtils.reqVo, securityKey);
       String result = null;
       BufferedInputStream in = null;
       BufferedOutputStream out = null;
       try {

           URL url = new URL(backstagePayUrl);
           URLConnection con = url.openConnection();

           con.setUseCaches(false);
           con.setDoInput(true);
           con.setDoOutput(true);
           out = new BufferedOutputStream(con.getOutputStream());
           byte[] outBuf = req.getBytes("UTF-8");
           out.write(outBuf);
           out.close();
           in = new BufferedInputStream(con.getInputStream());
           result = CupUtils.ReadByteStream(in);
       } catch (Exception ex) {
           return null;
       } finally {
           if (out != null)
               try {
                   out.close();
               }
               catch (IOException localIOException2) {
               }
           if (in != null)
               try {
                   in.close();
               }
               catch (IOException localIOException3) {
               }
       }
       Map<String, String> map = CupUtils.createResultMap(result);
       response.setResponseCode(map.get("respCode"));
       response.setStatusDescription(map.get("respMsg"));
       response.setTransactionID(map.get("qid"));
       response.setProcessingTime(map.get("respTime"));
       return response;
   }
    public GenericResponseVO processPreAuthorizationCompletion(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
       CupRequestVO request = (CupRequestVO) requestVO;
       CupResponseVO response = new CupResponseVO();
       initializeGateway(request.getTestAccount());
       String[] valueVo = {
               CupUtils.version,//Protocol version
               CupUtils.charset,
               request.getTransType(),
               request.getTransactionId(),
               request.getMerchantId(),
               request.getMerchantName(),
               CupUtils.acqCode,
               request.getMcc(),
               "",
               "",
               "",
               "",
               "",
               "",
               request.getDetailsId(),
               request.getAmount(),
               request.getCurrencyCode(),
               request.getOrderTime(),
               request.getIp(),
               "",
               "",
               "",
               CupUtils.transTimeout,
               CupUtils.merFrontEndUrl,
               CupUtils.merBackEndUrl,
               ""
       };


       //CupUtils.doPostQueryCmd("http://202.101.25.184/UpopWeb/api/BSPay.action", new QuickPayUtils().createBackStrForBackTrans(valueVo, QuickPayConf.reqVo)

       String req = CupUtils.createBackStrForBackTrans(valueVo, CupUtils.reqVo, securityKey);
       String result = null;
       BufferedInputStream in = null;
       BufferedOutputStream out = null;
       try {

           URL url = new URL(backstagePayUrl);
           URLConnection con = url.openConnection();

           con.setUseCaches(false);
           con.setDoInput(true);
           con.setDoOutput(true);
           out = new BufferedOutputStream(con.getOutputStream());
           byte[] outBuf = req.getBytes("UTF-8");
           out.write(outBuf);
           out.close();
           in = new BufferedInputStream(con.getInputStream());
           result = CupUtils.ReadByteStream(in);
       }
       catch (MalformedURLException e)
       {
           PZExceptionHandler.raiseTechnicalViolationException(CUPPaymentGateway.class.getName(),"processPreAuthorizationCompletion()",null,"common","MalFormed URL Exception while Pre Authorization transaction", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null,e.getMessage(),e.getCause());
       }
       catch (UnsupportedEncodingException e)
       {
           PZExceptionHandler.raiseTechnicalViolationException(CUPPaymentGateway.class.getName(),"processPreAuthorizationCompletion()",null,"common","UnSupported Encoding Exception while Pre Authorization transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,e.getMessage(),e.getCause());
       }
       catch (IOException e)
       {
           PZExceptionHandler.raiseTechnicalViolationException(CUPPaymentGateway.class.getName(),"processPreAuthorizationCompletion()",null,"common","IO Exception while Pre Authorization transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
       }
       finally {
           if (out != null)
               try {
                   out.close();
               }
               catch (IOException localIOException2) {
               }
           if (in != null)
               try {
                   in.close();
               }
               catch (IOException localIOException3) {
               }
       }
       Map<String, String> map = CupUtils.createResultMap(result);
       response.setResponseCode(map.get("respCode"));
       response.setStatusDescription(map.get("respMsg"));
       response.setTransactionID(map.get("qid"));
       response.setProcessingTime(map.get("respTime"));
       return response;
   }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("CUPPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public void initializeGateway(String testAccount){
        if(testAccount.equals("N")){
            /* Production environment */
            backstagePayUrl = CupUtils.liveBSPayUrl;
            queryUrl = CupUtils.liveQueryUrl;
            securityKey = CupUtils.liveSecurityKey;
        }
        else{
            /* Test environment */
            backstagePayUrl = CupUtils.testBSPayUrl;
            queryUrl = CupUtils.testQueryUrl;
            securityKey = CupUtils.testSecurityKey;
        }
    }

    /*public static void main(String[] args)
    {
        CUPPaymentGateway cupPaymentGateway = new CUPPaymentGateway("");
        CupRequestVO request = new CupRequestVO();
        GenericResponseVO responseVO = new GenericResponseVO();
        request.setTransType("02");
        request.setMerchantId("400308260000001");
        request.setDetailsId("10002618");
        request.setOrderTime("20140903173904");

        try{
            responseVO = cupPaymentGateway.processQuery("12211",request);
            //System.out.println("request---"+request.toString());

        }
        catch (SystemError e)
        {
            log.error("System error",e);
            e.printStackTrace();
        }
        //System.out.println("Response---"+responseVO.toString());
    }*/
}
