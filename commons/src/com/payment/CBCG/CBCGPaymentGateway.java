package com.payment.CBCG;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Transaction;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ResourceBundle;

/**
 * Created by Admin on 8/10/2020.
 */
public class CBCGPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE="cbcg";
    final static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.cbcg");
    private static TransactionLogger transactionLogger=new TransactionLogger(CBCGPaymentGateway.class.getName());
    public CBCGPaymentGateway(String accountId){this.accountId=accountId;}

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("---inside sale---");
        Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        Functions functions=new Functions();

        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String mid=gatewayAccount.getMerchantId();
        String apiKey=gatewayAccount.getPassword();
        boolean isTest=gatewayAccount.isTest();

        String firstName="";
        String lastName="";
        String phone="";
        String email="";
        String address="";
        String country="";
        String state="";
        String city="";
        String zip="";
        String status="";
        String remark="";

        String saleUrl="";
        String termUrl="";
        if(isTest)
        {
            saleUrl=RB.getString("TEST_SALE_URL");
        }else
        {
            saleUrl=RB.getString("LIVE_SALE_URL");
        }
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL")+trackingID;
            transactionLogger.error("From HOST_URL----" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL")+trackingID;
            transactionLogger.error("From RB TERM_URL ----" + termUrl);
        }
        StringBuffer request=new StringBuffer("");
        try
        {

            if(functions.isValueNull(commAddressDetailsVO.getFirstname()))
                firstName= URLEncoder.encode(commAddressDetailsVO.getFirstname(),"UTF-8");
            if(functions.isValueNull(commAddressDetailsVO.getLastname()))
                lastName= URLEncoder.encode(commAddressDetailsVO.getLastname(),"UTF-8");
            if(functions.isValueNull(commAddressDetailsVO.getEmail()))
                email= URLEncoder.encode(commAddressDetailsVO.getEmail(),"UTF-8");
            if(functions.isValueNull(commAddressDetailsVO.getPhone()))
                phone= commAddressDetailsVO.getPhone();
            if(functions.isValueNull(commAddressDetailsVO.getStreet()))
                address= URLEncoder.encode(commAddressDetailsVO.getStreet(),"UTF-8");
            if(functions.isValueNull(commAddressDetailsVO.getCountry()))
                country= commAddressDetailsVO.getCountry();
            if(functions.isValueNull(commAddressDetailsVO.getState()))
                state= URLEncoder.encode(commAddressDetailsVO.getState(),"UTF-8");
            if(functions.isValueNull(commAddressDetailsVO.getCity()))
                city= URLEncoder.encode(commAddressDetailsVO.getCity(),"UTF-8");
            if(functions.isValueNull(commAddressDetailsVO.getZipCode()))
                zip=commAddressDetailsVO.getZipCode();

            request.append("api_id=" +mid+
                    "&amount=" +commTransactionDetailsVO.getAmount()+
                    "&reference=" +trackingID+
                    "&hashKey=" +CBCGUtils.sha256Encoding(mid + apiKey + commTransactionDetailsVO.getAmount() + trackingID)+
                    "&cust_name=" +firstName+
                    "&cust_surname=" +lastName+
                    "&cust_address=" +address+
                    "&cust_phone=" +phone+
                    "&cust_email=" +email+
                    "&cust_country=" +country+
                    "&cust_state=" +state+
                    "&cust_city=" +city+
                    "&cust_zip=" +zip+
                    "&cc_number=" +commCardDetailsVO.getCardNum()+
                    "&cc_expiry_month=" +commCardDetailsVO.getExpMonth()+
                    "&cc_expiry_year=" +commCardDetailsVO.getExpYear()+
                    "&cc_name=" +firstName+" "+lastName+
                    "&cc_cvv=" +commCardDetailsVO.getcVV()+
                    "&response_url="+termUrl);
            transactionLogger.error("Sale request for trackingId--"+trackingID+"--"+request);
            String response=CBCGUtils.doHttpPostConnection(saleUrl,request.toString());
            transactionLogger.error("Sale response for trackingId--"+trackingID+"--"+response);
            if(functions.isValueNull(response))
            {
                JSONObject responseJSON = new JSONObject(response);
                if (responseJSON.has("Status"))
                {
                    status = responseJSON.getString("Status");
                    if (responseJSON.has("TrxID"))
                        comm3DResponseVO.setTransactionId(responseJSON.getString("TrxID"));
                    if (responseJSON.has("ResponseDate"))
                        comm3DResponseVO.setResponseTime(responseJSON.getString("ResponseDate"));
                    if (responseJSON.has("Information"))
                        remark = responseJSON.getString("Information");

                    if ("Approved".equalsIgnoreCase(status))
                    {
                        comm3DResponseVO.setStatus("success");
                        if (functions.isValueNull(remark))
                        {
                            comm3DResponseVO.setRemark(remark);
                            comm3DResponseVO.setDescription(remark);
                        }
                        else
                        {
                            comm3DResponseVO.setRemark("Transaction Successful");
                            comm3DResponseVO.setDescription("Transaction Successful");
                        }
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("fail");
                        if (functions.isValueNull(remark))
                        {
                            comm3DResponseVO.setRemark(remark);
                            comm3DResponseVO.setDescription(remark);
                        }
                        else
                        {
                            comm3DResponseVO.setRemark("Transaction failed");
                            comm3DResponseVO.setDescription("Transaction failed");
                        }
                    }
                }else
                {
                    comm3DResponseVO.setStatus("fail");
                    if(responseJSON.has("error_code"))
                        comm3DResponseVO.setErrorCode(responseJSON.getString("error_code"));
                    if(responseJSON.has("error_message"))
                    {
                        comm3DResponseVO.setDescription(responseJSON.getString("error_message"));
                        comm3DResponseVO.setRemark(responseJSON.getString("error_message"));
                    }else
                    {
                        comm3DResponseVO.setRemark("Transaction failed");
                        comm3DResponseVO.setDescription("Transaction failed");
                    }
                }
            }else
            {
                comm3DResponseVO.setStatus("failed");
                comm3DResponseVO.setRemark("No Response received");
                comm3DResponseVO.setDescription("No Response received");
            }
        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException---"+trackingID+"--",e);
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---" + trackingID + "--", e);
        }
        return comm3DResponseVO;
    }
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside processQuery ---");
        Functions functions=new Functions();
        CommResponseVO commResponseVO=new CommResponseVO();
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String mid=gatewayAccount.getMerchantId();
        boolean isTest=gatewayAccount.isTest();
        String inquiryUrl="";
        String amount=commTransactionDetailsVO.getAmount();
        String currency=commTransactionDetailsVO.getCurrency();

        if(isTest)
        {
            inquiryUrl=RB.getString("TEST_INQUIRY_URL")+mid+"/"+trackingID;
        }else
        {
            inquiryUrl=RB.getString("LIVE_INQUIRY_URL")+mid+"/"+trackingID;
        }
        try
        {
            transactionLogger.error("inquiry request--->" + inquiryUrl);
            String response = CBCGUtils.doHttpGetConnection(inquiryUrl);
            transactionLogger.error("inquiry response--" + trackingID + "-->" + response);
            if (functions.isValueNull(response))
            {
                commResponseVO.setAmount(amount);
                commResponseVO.setCurrency(currency);
                commResponseVO.setTransactionType("SALE");
                commResponseVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
                JSONObject responseJSON = new JSONObject(response);
                if(responseJSON.has("Status"))
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionStatus(responseJSON.getString("Status"));
                    if(responseJSON.has("Information"))
                    {
                        commResponseVO.setDescription(responseJSON.getString("Information"));
                        commResponseVO.setRemark(responseJSON.getString("Information"));
                    }
                    if(responseJSON.has("ResponseDate"))
                    {
                        commResponseVO.setBankTransactionDate(responseJSON.getString("ResponseDate"));
                        commResponseVO.setResponseTime(responseJSON.getString("ResponseDate"));
                    }
                    if(responseJSON.has("Amount"))
                        commResponseVO.setAmount(responseJSON.getString("Amount"));
                    if(responseJSON.has("TrxID"))
                        commResponseVO.setTransactionId(responseJSON.getString("TrxID"));
                }else
                {
                    commResponseVO.setStatus("fail");
                    if(responseJSON.has("error_message"))
                    {
                        commResponseVO.setDescription(responseJSON.getString("error_message"));
                        commResponseVO.setRemark(responseJSON.getString("error_message"));
                    }
                    if(responseJSON.has("error_code"))
                        commResponseVO.setErrorCode(responseJSON.getString("error_code"));
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Transaction Declined");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException CBCGPaymentGateway -------",e);
        }
        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
