package com.payment.ezpaynow;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.vo.ReserveField2VO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Diksha on 22-Jan-20.
 */
public class EzPayNowPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE="ezpaynow";

    private TransactionLogger transactionLogger=new TransactionLogger(EzPayNowPaymentGateway.class.getName());

    private ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.ezpaynow");
    public EzPayNowPaymentGateway(String accountId)
    {
        this.accountId=accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        ReserveField2VO reserveField2VO=commRequestVO.getReserveField2VO();
        Functions functions=new Functions();
        Map<String,String> responseMap=null;
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String mid=gatewayAccount.getMerchantId();
        String paymethod="Check";
        String post_method="sync";
        String processing_mode="sale";
        String redirect_url="";
        String notification_url="";
        String terminal_name=gatewayAccount.getFRAUD_FTP_USERNAME();
        String customer_id=commTransactionDetailsVO.getCustomerId();
        String first_name="";
        String last_name="";
        String state="";
        String country="";
        String bank_name="";
        String address1="";
        String zip="";
        String telephone="";
        String email="";
        String city="";

        if(functions.isValueNull(commAddressDetailsVO.getStreet()))
            address1=commAddressDetailsVO.getStreet();
        if(functions.isValueNull(commAddressDetailsVO.getCity()))
            city=commAddressDetailsVO.getCity();
        if(functions.isValueNull(commAddressDetailsVO.getCountry()))
         country=commAddressDetailsVO.getCountry();
        if(functions.isValueNull(commAddressDetailsVO.getZipCode()))
            zip=commAddressDetailsVO.getZipCode();
        if(functions.isValueNull(commAddressDetailsVO.getPhone()))
            telephone=commAddressDetailsVO.getPhone();

        String amount=commTransactionDetailsVO.getAmount();
        String currency=commTransactionDetailsVO.getCurrency();

        if(functions.isValueNull(commAddressDetailsVO.getEmail()))
            email=commAddressDetailsVO.getEmail();

        String check_number=reserveField2VO.getCheckNumber();
        String routing_number=reserveField2VO.getRoutingNumber();
        String account_number=reserveField2VO.getAccountNumber();
        if(functions.isValueNull(reserveField2VO.getBankName()))
         bank_name=reserveField2VO.getBankName();
        String bank_phone="";
        String customer_ip=commAddressDetailsVO.getCardHolderIpAddress();
        boolean isTest=gatewayAccount.isTest();
        String saleUrl="";
        if(isTest){
            saleUrl=RB.getString("TEST_URL");
        }
        else {
            saleUrl=RB.getString("LIVE_URL");
        }

        try
        {
            first_name= ESAPI.encoder().encodeForURL(commAddressDetailsVO.getFirstname());
            last_name=ESAPI.encoder().encodeForURL(commAddressDetailsVO.getLastname());
            state=ESAPI.encoder().encodeForURL(commAddressDetailsVO.getState());

        }
        catch (EncodingException e)
        {
           transactionLogger.error("EncodingException---->",e);
        }

        if(functions.isValueNull(commMerchantVO.getHostUrl())){
            redirect_url="https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");

        }
        else{
            redirect_url=RB.getString("TERM_URL");
        }

        notification_url=RB.getString("NOTIFY_URL");
        StringBuffer request=new StringBuffer("affiliate="+mid
                +"&paymethod="+paymethod
                +"&post_method="+post_method
                +"&processing_mode="+processing_mode
                +"&order_id="+trackingID
                +"&redirect="+redirect_url
                +"&notification_url="+notification_url
                +"&terminal_name="+terminal_name
                +"&first_name="+first_name
                +"&last_name="+last_name
                +"&address1="+address1
                +"&city="+city
                +"&state="+state
                +"&country="+country
                +"&zip="+zip
                +"&telephone="+telephone
                +"&amount="+amount
                +"&currency="+currency
                +"&email="+email
                +"&check_number="+check_number
                +"&routing_number="+routing_number
                +"&account_number="+account_number
                +"&bank_name="+bank_name
                +"&bank_phone="+bank_phone
                +"&customer_ip="+customer_ip
        );

        if (functions.isValueNull(customer_id))
            request.append("&customer_id="+customer_id);

        transactionLogger.error("Sale Request---->"+ trackingID + "--" + request);
        String response=EzPayNowUtils.doHttpPostConnection(saleUrl,request.toString());
        transactionLogger.error("Sale Response---->"+ trackingID + "--" + response);

        if(functions.isValueNull(response)){
            responseMap=EzPayNowUtils.getResponseVo(response);
            if("APPROVED".equalsIgnoreCase(responseMap.get("status"))){
                comm3DResponseVO.setStatus("success");
                comm3DResponseVO.setTransactionId(responseMap.get("transaction_no"));
                comm3DResponseVO.setResponseHashInfo(responseMap.get("approval_no"));
                comm3DResponseVO.setDescription(responseMap.get("reason"));
                comm3DResponseVO.setRemark(responseMap.get("reason"));
                comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
            }
            else if ("PENDING".equalsIgnoreCase(responseMap.get("status"))){
                comm3DResponseVO.setStatus("pending");
                comm3DResponseVO.setTransactionId(responseMap.get("transaction_no"));
                comm3DResponseVO.setResponseHashInfo(responseMap.get("approval_no"));
                comm3DResponseVO.setDescription(responseMap.get("reason"));
                comm3DResponseVO.setRemark(responseMap.get("reason"));
            }
            else {
                comm3DResponseVO.setStatus("failed");
                comm3DResponseVO.setDescription(responseMap.get("reason"));
                comm3DResponseVO.setRemark(responseMap.get("reason"));
            }
            comm3DResponseVO.setAmount(responseMap.get("amount"));
            comm3DResponseVO.setCurrency(responseMap.get("currency"));
            comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }
        return comm3DResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID,GenericRequestVO requestVO){
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommResponseVO commResponseVO=new CommResponseVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        Functions functions=new Functions();
        String post_method="sync";

        boolean isTest=gatewayAccount.isTest();
        String refundUrl="";
        if (isTest)
            refundUrl=RB.getString("TEST_URL");
        else
            refundUrl=RB.getString("LIVE_URL");

        String mid=gatewayAccount.getMerchantId();
        String paymethod="Check";
        String processing_mode="refund";
        String redirect_url="";
        String terminal_name=gatewayAccount.getFRAUD_FTP_USERNAME();
        String amount=commTransactionDetailsVO.getAmount();
        String currency=commTransactionDetailsVO.getCurrency();
        String reference_transaction_no=commTransactionDetailsVO.getPreviousTransactionId();
        String transaction_memo="";
        String product_description="";
        Map<String,String> responseMap=null;

        if(functions.isValueNull(commMerchantVO.getHostUrl())){
            redirect_url="https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");

        }
        else{
            redirect_url=RB.getString("TERM_URL");
        }


        StringBuffer request=new StringBuffer("");
        request.append("affiliate="+mid
                +"&paymethod="+paymethod
                +"&post_method="+post_method
                +"&processing_mode="+processing_mode
                +"&redirect="+redirect_url
                +"&terminal_name="+terminal_name
                +"&amount="+amount
                +"&currency="+currency
                +"&reference_transaction_no=" +reference_transaction_no
               /* +"&transaction_memo="+transaction_memo
                +"&product_description="+product_description*/
                );

        transactionLogger.error("refund request--->"+ trackingID + "--" + request);
        String response=EzPayNowUtils.doHttpPostConnection(refundUrl,request.toString());
        transactionLogger.error("refund response--->"+ trackingID + "--" + response);
        if (functions.isValueNull(response)){

            responseMap=EzPayNowUtils.getResponseVo(response);
            if ("REFUNDED".equalsIgnoreCase(responseMap.get("status"))){
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionId(responseMap.get("transaction_no"));
                commResponseVO.setResponseHashInfo(responseMap.get("approval_no"));
                commResponseVO.setDescription(responseMap.get("reason"));
                commResponseVO.setRemark(responseMap.get("reason"));
                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
            }
            else {
                commResponseVO.setStatus("failed");
                commResponseVO.setDescription(responseMap.get("reason"));
                commResponseVO.setRemark(responseMap.get("reason"));
            }

        }
        return commResponseVO ;
    }


    public GenericResponseVO processQuery(String trackingID,GenericRequestVO requestVO){

        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommResponseVO commResponseVO=new CommResponseVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        Functions functions=new Functions();
        String post_method="sync";
        Map<String,String> responseMap=null;

        boolean isTest=gatewayAccount.isTest();
        String inquiryUrl="";
        if (isTest)
            inquiryUrl=RB.getString("TEST_URL");
        else
            inquiryUrl=RB.getString("LIVE_URL");

        String mid=gatewayAccount.getMerchantId();
        String processing_mode="transaction_status";
        String redirect_url="";
        String terminal_name=gatewayAccount.getFRAUD_FTP_USERNAME();
        String reference_transaction_no=commTransactionDetailsVO.getPreviousTransactionId();

        if(functions.isValueNull(commMerchantVO.getHostUrl())){
            redirect_url="https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");

        }
        else{
            redirect_url=RB.getString("TERM_URL");
        }

        StringBuffer request=new StringBuffer("");

        request.append("affiliate="+mid
                +"&processing_mode="+processing_mode
                +"&post_method="+post_method
                +"&redirect="+redirect_url
                +"&terminal_name="+terminal_name
                +"&reference_transaction_no="+reference_transaction_no
                );

        transactionLogger.error("Inquiry request--->"+ trackingID + "--" + request);
        String response=EzPayNowUtils.doHttpPostConnection(inquiryUrl,request.toString());
        transactionLogger.error("Inquiry response--->"+ trackingID + "--" + response);

        if (functions.isValueNull(response)){

            responseMap=EzPayNowUtils.getResponseVo(response);
            if ("AUTHORIZED".equalsIgnoreCase(responseMap.get("status"))){
                commResponseVO.setTransactionStatus("success");
                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
            }
            else if("APPROVED".equalsIgnoreCase(responseMap.get("status")))
            {
                commResponseVO.setTransactionStatus("success");
                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                commResponseVO.setTransactionType(PZProcessType.SALE.toString());
            }
            else if("REFUNDED".equalsIgnoreCase(responseMap.get("status")))
            {
                commResponseVO.setTransactionStatus("success");
                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
            }
            else if("FAILED".equalsIgnoreCase(responseMap.get("status")))
            {
                commResponseVO.setTransactionStatus("failed");
                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                commResponseVO.setTransactionType(PZProcessType.SALE.toString());
            }

            else {
                commResponseVO.setTransactionStatus("failed");
                commResponseVO.setTransactionType(PZProcessType.SALE.toString());
            }
            commResponseVO.setTransactionId(responseMap.get("transaction_no"));
            commResponseVO.setResponseHashInfo(responseMap.get("approval_no"));
            commResponseVO.setDescription(responseMap.get("status_description"));
            commResponseVO.setRemark(responseMap.get("status_description"));
            commResponseVO.setAmount(responseMap.get("amount"));
            commResponseVO.setCurrency(responseMap.get("currency"));
            commResponseVO.setMerchantId(responseMap.get("affiliate"));
            commResponseVO.setBankTransactionDate(commTransactionDetailsVO.getResponsetime());
        }

        return commResponseVO;
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
