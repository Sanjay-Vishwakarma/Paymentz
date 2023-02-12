package com.payment.allPay88;



import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.vo.ReserveField2VO;
import com.payment.awepay.AwepayBundle.core.AwePayUtils;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by jeet on 28-11-2018.
 */
public class AllPay88PaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "AllPay88";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.AllPay88");
    private static TransactionLogger transactionLogger = new TransactionLogger(AllPay88PaymentGateway.class.getName());

   public AllPay88PaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }
    public static void main(String[] args)
    {
        Date currentDate = new Date();
        try
        {
            String apiKey="MFpicLNaRE5GarE54P62eU8pcCpHhpmTLROwcaAkl4OOA9bLZQ90hGGipFEp6Ypf";
            String cid="601";
            String uid="12325";
            long time=currentDate.getTime() / 1000;
            String amount="10.00";
            String order_id="22141";
            String category="remit";
            String from_bank_flag="BCCB";

            String Reqparameter="cid="+cid+"&uid="+uid+"&time="+time+"&amount="+amount+"&order_id="+order_id+"&category="+category+"&from_bank_flag="+from_bank_flag;
            String sign = AllPay88Utils.getSignature(Reqparameter.toString(), apiKey);

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(RB.getString("SALE_URL"));
            httpPost.addHeader("Content-Hmac", sign);
            httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
            httpPost.setEntity(new StringEntity(Reqparameter.toString(), "UTF-8"));
            CloseableHttpResponse response2 = httpclient.execute(httpPost);
            String response="";
            response = EntityUtils.toString(response2.getEntity(), "UTF-8");

        }catch (Exception j)
        {
            transactionLogger.error("Exception-----",j);
        }
    }

    public GenericResponseVO processSale(String trackingId, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;

        AllPay88ResponseVO commResponseVO= new AllPay88ResponseVO();
        ReserveField2VO field2VO=commRequestVO.getReserveField2VO();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        try
        {
         Date currentDate = new Date();
         String cid = gatewayAccount.getMerchantId();
         transactionLogger.debug("---cid---"+cid);
         String uid = transDetailsVO.getCustomerId();
         String time = String.valueOf(currentDate.getTime() / 1000);
         String amount = transDetailsVO.getAmount();
         String order_id  = trackingId;
         String category  = "remit";
         String from_bank_flag =field2VO.getBankName();
         String apiKey=gatewayAccount.getFRAUD_FTP_PASSWORD();

         String Reqparameter="cid="+cid+"&uid="+uid+"&time="+time+"&amount="+amount+"&order_id="+order_id+"&category="+category+"&from_bank_flag="+from_bank_flag;
         String sign = AllPay88Utils.getSignature(Reqparameter.toString(), apiKey);
         transactionLogger.debug("sign 123115::::::"+sign);

           CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(RB.getString("SALE_URL"));
            httpPost.addHeader("Content-Hmac", sign);
            httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
            httpPost.setEntity(new StringEntity(Reqparameter.toString(), "UTF-8"));
            CloseableHttpResponse response2 = httpclient.execute(httpPost);
            String response="";
            response = EntityUtils.toString(response2.getEntity(), "UTF-8");
            transactionLogger.error("sale response-----" + response);
            boolean status=false;
            String respAmount="";
            String action="";
            String bankflag="";
            String cardnumber="";
            String cardname="";
            String location="";
            String msg="";
            if(functions.isValueNull(response) && response.contains("{")){
                JSONObject jsonObject= new JSONObject(response);
                if(jsonObject!=null){
                    if(jsonObject.has("success")){
                         status=jsonObject.getBoolean("success");
                    }
                    if(jsonObject.has("msg")){
                        msg=jsonObject.getString("msg");
                    }

                    if(jsonObject.has("data")){
                            JSONObject jsonObject1=jsonObject.getJSONObject("data");

                        if(jsonObject1.has("action")){
                             action=jsonObject1.getString("action");
                        }
                        if(jsonObject1.has("amount")){
                            respAmount=jsonObject1.getString("amount");
                        }

                        if(jsonObject1.has("card")){
                            JSONObject jsonObject2=jsonObject1.getJSONObject("card");

                            if(jsonObject2.has("bankflag")){
                                 bankflag=jsonObject2.getString("bankflag");
                            }
                            if(jsonObject2.has("cardnumber")){
                                 cardnumber=jsonObject2.getString("cardnumber");
                            }
                            if(jsonObject2.has("cardname")){
                                 cardname=jsonObject2.getString("cardname");
                            }
                            if(jsonObject2.has("location")){
                                 location=jsonObject2.getString("location");
                            }
                        }
                    }
                    transactionLogger.debug("status--"+status+"--respAmount--"+respAmount+"---action--"+action+"---bankflag--"+bankflag+"--cardnumber--"+cardnumber+"--cardname--"+cardname+"--location--"+location+"----msg---"+msg);
                    if(status){
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setRemark("SYS :Transaction Successful");
                        commResponseVO.setDescription("SYS :Approved");
                        commResponseVO.setCardnumber(cardnumber);
                        commResponseVO.setLocation(location);
                        commResponseVO.setCardname(cardname);
                        commResponseVO.setBankflag(bankflag);
                    }else {
                        commResponseVO.setStatus("failed");
                        if(functions.isValueNull(msg)){
                            commResponseVO.setRemark(msg);
                            commResponseVO.setDescription(msg);
                        }else {
                            commResponseVO.setRemark("SYS: Transaction failed");
                            commResponseVO.setDescription("SYS: Declined");
                        }
                    }
                    if(functions.isValueNull(respAmount)){
                        commResponseVO.setAmount(respAmount);
                    }

                }
            }
          }
        catch (Exception e)
        {
              transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
