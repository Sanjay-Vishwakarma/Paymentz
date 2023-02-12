package com.payment.giftpay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONObject;
import java.util.ResourceBundle;

/**
 * Created by Admin on 4/2/2021.
 */
public class GiftPayPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE         = "giftpay";
    private TransactionLogger transactionLogger     = new TransactionLogger(GiftPayPaymentGateway.class.getName());
    final static ResourceBundle RB                  = LoadProperties.getProperty("com.directi.pg.giftpay");

    public GiftPayPaymentGateway(String accountId)
    {
        this.accountId=accountId;
    }
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside GiftPayPaymentGateway processSale");
        Comm3DResponseVO comm3DResponseVO   = new Comm3DResponseVO();
        CommRequestVO commRequestVO         =(CommRequestVO)requestVO;
        Functions functions                 = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        String description                                  = trackingID;
        String paymentAmount            = commTransactionDetailsVO.getAmount();
        String currency                 = commTransactionDetailsVO.getCurrency();
        CommMerchantVO commMerchantVO   = commRequestVO.getCommMerchantVO();
        String termUrl   = "";
        String apikey    = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretkey = gatewayAccount.getFRAUD_FTP_PASSWORD();


        GiftPayUtils giftPayUtils   = new GiftPayUtils();
        boolean isTest              = gatewayAccount.isTest();
        String saleURL              = "";
            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingID;
            transactionLogger.error("From HOST_URL----" + termUrl);
            }
            else
            {
                termUrl = RB.getString("TERM_URL") + trackingID;
                transactionLogger.error("From RB TERM_URL ----" + termUrl);
            }
            if (isTest)
            {
                saleURL = RB.getString("TEST_URL");
            }
            else
            {
                saleURL = RB.getString("LIVE_URL");
            }

        try
        {
            String saleresponse="";
            StringBuffer salerequest=new StringBuffer();
            salerequest.append("{" + "\"description\":\""+description+"\","+
                            "\"paymentAmount\":\""+paymentAmount+"\","+
                            "\"returnUrl\":\""+termUrl+"\","+
                            "\"currency\":\""+currency+"\"" +
                            "}");

            transactionLogger.error("Salerequest-------" + trackingID + "----"+salerequest);
            saleresponse=giftPayUtils.doPostHTTPSURLConnectionClient(saleURL, salerequest.toString(),apikey+":"+secretkey);
            transactionLogger.error("Saleresponse-------" +  trackingID + "----"+saleresponse);

            if(functions.isValueNull(saleresponse))
            {
                JSONObject responseJSON = new JSONObject(saleresponse);
                String paymentRequestId="";
                String securePaymentGateway="";
                String status="";
                String code="";
                String message="";

                if(responseJSON!=null)
                {
                    if(responseJSON.has("paymentRequestId"))
                    {
                        paymentRequestId=responseJSON.getString("paymentRequestId");
                    }
                    if(responseJSON.has("securePaymentGateway"))
                    {
                        securePaymentGateway = responseJSON.getString("securePaymentGateway");
                    }
                    if(responseJSON.has("status"))
                    {
                        status = responseJSON.getString("status");
                    }
                    if(responseJSON.has("code"))
                    {
                        code = responseJSON.getString("code");
                    }
                    if(responseJSON.has("message"))
                    {
                        message = responseJSON.getString("message");
                    }

                    if(responseJSON.has("paymentRequestId"))
                    {
                        comm3DResponseVO.setStatus("pending3DConfirmation");
                        comm3DResponseVO.setTransactionId(paymentRequestId);
                        comm3DResponseVO.setUrlFor3DRedirect(securePaymentGateway);
                        giftPayUtils.updateTransaction(trackingID,paymentRequestId);
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("failed");
                        comm3DResponseVO.setTransactionId(paymentRequestId);
                        comm3DResponseVO.setDescription(message);
                        comm3DResponseVO.setRemark(message);
                        comm3DResponseVO.setErrorCode(code);

                    }
                }
            }
        }
        catch (Exception e)
        {

            transactionLogger.error("Exception-----" + trackingID + "----", e);
        }
      return comm3DResponseVO;

    }
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {

        transactionLogger.error(":::::Entered into processAutoRedirect for giftpay:::::");


        CommRequestVO commRequestVO =null;
        GiftPayPaymentProcess giftPayPaymentProcess = new GiftPayPaymentProcess();
        String form="";

       try
       {
        commRequestVO = GiftPayUtils.getGiftpayRequestVO(commonValidatorVO);
          Comm3DResponseVO comm3DResponseVO = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
           if("pending3DConfirmation".equalsIgnoreCase(comm3DResponseVO.getStatus()))
           {
               form = giftPayPaymentProcess.get3DConfirmationForm(commonValidatorVO.getTrackingid(), "", comm3DResponseVO);
           }
         transactionLogger.error("form---->" + form);


       }

      catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException--",e);
        }


        return form;

        }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {

        transactionLogger.error("-----Inside Giftpay processInquiry-----");
        Functions functions                                 = new Functions();
        Comm3DResponseVO commResponseVO                     = new Comm3DResponseVO();
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO   = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount                       = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest              = gatewayAccount.isTest();
        String apikey               = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretkey            = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String paymentId            = commTransactionDetailsVO.getPreviousTransactionId();
        String currency             = commTransactionDetailsVO.getCurrency();
        GiftPayUtils giftPayUtils   = new GiftPayUtils();
        String status       = "";
        String remark       = "";
        String descriptor   = "";
        String response     = "";
        String message      = "";
        String code         = "";
        String paymentStatus = "";
        String inquiryURL    = "";
        try
        {
            if (isTest)
            {
                inquiryURL = RB.getString("INQUIRY_URL");
            }
            else
            {
                inquiryURL = RB.getString("INQUIRY_URL");
            }


            String inquiry_response         = "";
            StringBuffer inquiry_request    = new StringBuffer();
            inquiry_request.append("{" + "\"paymentId\":\"" + paymentId + "\"" + "}");

            transactionLogger.error("Inquiry request-------"  +trackingID+"----"+ inquiry_request);
            inquiry_response = giftPayUtils.doPostHTTPSURLConnectionClient(inquiryURL, inquiry_request.toString(), apikey + ":" + secretkey);
            transactionLogger.error("Inquiry response-------" +trackingID+"----"+ inquiry_response);
            if(functions.isValueNull(inquiry_response) && inquiry_response.contains("{"))
            {
                JSONObject responseJSON = new JSONObject(inquiry_response);
                if(responseJSON != null)
                {
                    if(responseJSON.has("paymentStatus"))
                    {
                        paymentStatus   = responseJSON.getString("paymentStatus");
                    }
                    if(responseJSON.has("message"))
                    {
                        message = responseJSON.getString("message");
                    }
                    if(responseJSON.has("code"))
                    {
                        code    = responseJSON.getString("code");
                    }
                    if(responseJSON.has("status"))
                    {
                        status  = responseJSON.getString("status");
                    }
                    if(paymentStatus.equals("success"))
                    {
                      commResponseVO.setStatus("success");
                      commResponseVO.setTransactionId(paymentId);
                      commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                      commResponseVO.setRemark("Transaction Successful");
                      commResponseVO.setDescription("Transaction Successful");
                      commResponseVO.setTransactionStatus(paymentStatus);
                    }
                    else if(paymentStatus.equals("pending"))
                    {
                      commResponseVO.setStatus("pending");
                      commResponseVO.setRemark("Transaction Pending");
                      commResponseVO.setDescription("Transaction Pending");
                      commResponseVO.setTransactionStatus(paymentStatus);
                      commResponseVO.setTransactionId(paymentId);
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setRemark("Transaction failed");
                        commResponseVO.setDescription("Transaction failed");
                        commResponseVO.setTransactionStatus(paymentStatus);
                    }
                    commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                    commResponseVO.setCurrency(currency);
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                }
                else{
                    commResponseVO.setStatus("pending");
                    commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                    commResponseVO.setTransactionStatus("pending");
                }
            }
            else{
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");  // set inquiry response not found
                commResponseVO.setTransactionStatus("pending");
            }
        }


            catch (Exception e)

            {
               transactionLogger.error("Exception----",e);
            }

            return commResponseVO;
        }

    public String getMaxWaitDays()
    {
        return null;
    }

}
