package com.payment.epay;

import com.directi.pg.ActionEntry;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Admin on 10/31/17.
 */
public class EpayPaymentGateway extends AbstractPaymentGateway
{
    public  static final String GATEWAY_TYPE="epay";
    final  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.epay");
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    TransactionLogger transactionLogger=new TransactionLogger(EpayPaymentGateway.class.getName());
    public EpayPaymentGateway (String accountId){this.accountId=accountId;}

    public static void main(String[] args)
    {
    /* PHP sample code: {
        # Coding of request
            $ENCODED = base64_encode('DATA');
        # Generation of control amount
        $CHECKSUM = hmac('sha1', $ENCODED, $secret);*/

        try{
            String secretKey="TMNAI2TAK8KURU45GJOFYX7E07VT6FUAFXC4MSZ7PKDL2LY0WRTTYI1ML5449TZP";

           // String saleRequest="INVOICE=544552\nAMOUNT=22.80\nCURRENCY=BGN\nMIN=D946769233\nMEMAIL=<emailaddress>\nCIN=D532186860\nCEMAIL=<emailaddress>\nEXP_TIME=01.08.2020";

           String payoutRequest="INVOICE=544578\nAMOUNT=22.80\nCURRENCY=BGN\nMIN=D946769233\nRCPT_NAME=jinesh\nRCPT_PID=1111111110";

           // String encodedResponse="SU5WT0lDRT01NTU2MTpTVEFUVVM9UEFJRDpQQVlfVElNRT\n" +
             //       "0yMDE3MTEwMjExNTIyMzpTVEFOPTAwMDAwMDpCQ09ERT0wMDAwMDAK";


         //   System.out.println("encodedResponse:::::"+encodedResponse);

            String encoded=new String(Base64.encodeBase64((payoutRequest).getBytes()));

         //   System.out.println("encoded:::::"+encoded);

         //   String decoded=new String(Base64.decodeBase64(encodedResponse));

        // /*   System.out.println("decoded:::::"+decoded);
            /*Map<String, String> responseMap = EpayPaymentGateway.getDecoded(decoded);

            String status="";
            if (responseMap != null)
            {
                if ("PAID".equals(responseMap.get("STATUS")))
                {
                    status = "OK";
                }
                else
                {
                    status = "ERR";
                }
                System.out.println("status::::"+status);
            }*/

            String hmac = calculateRFC2104HMAC(encoded,secretKey);

           // System.out.println(hmac);
           // String saleResponse=EpayUtils.doGetHTTPSURLConnectionClient(RB.getString("TEST_PAYOUT_URL")+"?PAGE=paylogin&ENCODED="+encoded+"&"+"CHECKSUM="+hmac);
         //  String payoutResponse=EpayUtils.doGetHTTPSURLConnectionClient(RB.getString("TEST_PAYOUT_URL")+"?ENCODED="+encoded+"&"+"CHECKSUM="+hmac);

        //   System.out.println("saleResponse:::::"+payoutResponse);

            String payoutResponse="D000000000135508";

            String payMoney=EpayUtils.doGetHTTPSURLConnectionClient(RB.getString("PAY_MONEY")+payoutResponse);

           //System.out.println("payMoney:::::"+payMoney);



        }catch(Exception e){
           // e.printStackTrace();
        }
    }
    public static Map<String, String> getDecoded(String query)
    {
        String[] params = query.split(":");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String[] p = param.split("=");
            String name = p[0];
            if (p.length > 1)
            {
                String value = p[1];
                map.put(name, value);
                //System.out.println(name + ":::" + value);
            }
        }
        return map;
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public static String calculateRFC2104HMAC(String data, String key)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
    {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes()));
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error(":::::Entering into  processSale:::::");
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO =commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String min=gatewayAccount.getMerchantId();
        String email=gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretKey=gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest=gatewayAccount.isTest();


        try{
            String saleRequest="INVOICE="+trackingID+"\nAMOUNT="+transactionDetailsVO.getAmount()+"\nCURRENCY="+transactionDetailsVO.getCurrency()+"\nMIN="+min+"\nMEMAIL="+email+"\nCIN=D532186860\nCEMAIL="+commAddressDetailsVO.getEmail()+"";
            String encoded=new String(Base64.encodeBase64((saleRequest).getBytes()));
            String checksum = calculateRFC2104HMAC(encoded,secretKey);

            transactionLogger.error("saleRequest:::::"+trackingID + "--" + saleRequest);
            transactionLogger.error("encoded:::::"+encoded);
            transactionLogger.error("checksum:::::"+checksum);

            String saleResponse="";
            String sys_code="";
            if(isTest){
                saleResponse=EpayUtils.doGetHTTPSURLConnectionClient(RB.getString("TEST_DIRECT_SALE_URL")+"?ENCODED="+encoded+"&"+"CHECKSUM="+checksum);
            }else{
                saleResponse=EpayUtils.doGetHTTPSURLConnectionClient(RB.getString("LIVE_DIRECT_SALE_URL")+"?ENCODED="+encoded+"&"+"CHECKSUM="+checksum);
            }
            transactionLogger.error("saleResponse:::::"+trackingID + "--" + saleResponse);

            String status = "fail";
            String remark="Declined";
            String descriptor="";

            if(saleResponse.contains("SYS_CODE"))
            {
                status = "pending";
                String[] parts = saleResponse.split("=");
                sys_code= parts[1];
                descriptor=gatewayAccount.getDisplayName();
                remark="Transaction is in process";
                commResponseVO.setTransactionId(sys_code);

            }
            commResponseVO.setStatus(status);
            commResponseVO.setRemark(remark);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setTransactionType("sale");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

        }catch(Exception e){
            transactionLogger.error("Exception:::::"+e.getMessage());
        }return commResponseVO;

    }

    @Override
    public GenericResponseVO processPayout(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error(":::::Entering into  processPayout:::::");
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO transactionDetailsVO =commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String min=gatewayAccount.getMerchantId();
        String email=gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretKey=gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest=gatewayAccount.isTest();


        try{
            String payoutRequest="INVOICE="+trackingId+"\nAMOUNT="+transactionDetailsVO.getAmount()+"\nCURRENCY="+transactionDetailsVO.getCurrency()+"\nMIN="+min+"\nRCPT_NAME="+commAddressDetailsVO.getFirstname()+"\nRCPT_PID=1111111110";
            String encoded=new String(Base64.encodeBase64((payoutRequest).getBytes()));
            String checksum = calculateRFC2104HMAC(encoded,secretKey);

            transactionLogger.error("payoutRequest:::::"+trackingId + "--" + payoutRequest);
            transactionLogger.error("encoded:::::"+encoded);
            transactionLogger.error("checksum:::::"+checksum);

            String payoutResponse="";
            String sys_code="";
            if(isTest){
                payoutResponse=EpayUtils.doGetHTTPSURLConnectionClient(RB.getString("TEST_PAYOUT_URL")+"?ENCODED="+encoded+"&"+"CHECKSUM="+checksum);
            }else{
                payoutResponse=EpayUtils.doGetHTTPSURLConnectionClient(RB.getString("LIVE_PAYOUT_URL")+"?ENCODED="+encoded+"&"+"CHECKSUM="+checksum);
            }
            transactionLogger.error("payoutResponse:::::"+trackingId + "--" + payoutResponse);

            String status = "fail";
            String remark="Declined";
            String descriptor="";

            if(payoutResponse.contains("SYS_CODE"))
            {
                status = "pending";
                String[] parts = payoutResponse.split("=");
                sys_code= parts[1];
                descriptor=gatewayAccount.getDisplayName();
                remark="Transaction is in process";
                commResponseVO.setTransactionId(sys_code);
                String sys_code_Response="";
                if(isTest)
                {
                     sys_code_Response = EpayUtils.doGetHTTPSURLConnectionClient(RB.getString("PAY_MONEY") + sys_code);
                }
                else
                {
                     sys_code_Response = EpayUtils.doGetHTTPSURLConnectionClient(RB.getString("PAY_MONEY") + sys_code);
                }
                transactionLogger.error("sys_code_Response:::::::"+sys_code_Response);
            }
            commResponseVO.setStatus(status);
            commResponseVO.setRemark(remark);
            commResponseVO.setDescriptor(descriptor);
            commResponseVO.setDescription(remark);
            commResponseVO.setTransactionType("payout");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));


        }catch(Exception e){
            transactionLogger.error("Exception:::::"+e.getMessage());

        }return commResponseVO;

    }

    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        ActionEntry actionEntry= new ActionEntry();
        actionEntry.actionEntryExtensionforEpay(commonValidatorVO);
        transactionLogger.error(":::::Entered into processAutoRedirect for EPay:::::");

        String html = "";

        html = EpayUtils.getEpayRequest(commonValidatorVO);
        return html;
    }

}
