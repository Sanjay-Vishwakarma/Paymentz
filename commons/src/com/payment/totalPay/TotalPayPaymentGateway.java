package com.payment.totalPay;

import com.directi.pg.Base64;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

/**
 * Created by Admin on 7/20/2020.
 */
public class TotalPayPaymentGateway extends AbstractPaymentGateway
{

    public static final String GATEWAY_TYPE = "totalpay";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.totalpay");
    private static TransactionLogger transactionLogger = new TransactionLogger(TotalPayPaymentGateway.class.getName());

    public  TotalPayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public String getMaxWaitDays()
    {
        return null;
    }

    public static void main(String[] args){
        try
        {
            transactionLogger.error("TotalPaygatway:: inside processmainSale()");
            TransactionDetailsVO transactionDetailsVO = new TransactionDetailsVO();
            TotalaPayUtils totalaPayUtils = new TotalaPayUtils();
            Functions functions = new Functions();
            String Key ="a492abfe10119e9b1a60e5d1c057055bcea2244e8de29533fd1deae425b11bf1";                           //RB.getString("LIVE_API_KEY");
            String AuthenticationKey = Base64.encode(Key.getBytes());
            System.out.println(AuthenticationKey);

            StringBuffer request = new StringBuffer();

            request.append("{" +
                    "\"Authorization\":\""+AuthenticationKey+"\"," +
                    "\"userData\":{\"first_name\":\"Göran\"," +
                    "\"last_name\":\"rty\"," +
                    "\"email\":\"test@gmail.com\"," +
                    "\"address\":\"\"," +
                    "\"country\":\"USA\"," +
                    "\"state\":\"CA\"," +
                    "\"city\":\"Anaheim\"," +
                    "\"zip\":\"12315\"," +
                    "\"phone\":\"9845684125\"," +
                    "\"ip\":\"192.168.1.1\"," +
                    "\"birthday\":\"\"," +
                    "\"username\":\"tw\"}," +
                    "\"cardData\":{\"name\":\"Göran rty\","+
                    "\"type\":\"2\"," +
                    "\"number\":\"4242424242424242\"," +
                    "\"month\":\"12\"," +
                    "\"year\":\"2034\"," +
                    "\"cvv\":\"123\"}," +
                    "\"subscription_status\":\"0\"," +
                    "\"ext_order_id\":\"\"," +
                    "\"amount\":\"100.00\"," +
                    "\"currency\":\"USD\"" +
                    "}");


            String Response = "";
            System.out.println(request);
            System.out.println(RB.getString("LIVE_URL"));
            Response = totalaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), AuthenticationKey, request.toString());
            System.out.println(Response);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("BillDeskPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processSale(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("TotalPaygatway:: inside processmainSale()");
        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        TotalaPayUtils totalaPayUtils = new TotalaPayUtils();
        String merchantId = gatewayAccount.getMerchantId();
        String Key = gatewayAccount.getFRAUD_FTP_PASSWORD();                          //RB.getString("LIVE_API_KEY");
        String AuthenticationKey = Base64.encode(Key.getBytes());

        String Currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String card_type="";

        if (functions.isValueNull(transactionDetailsVO.getCurrency()))
        {
            Currency=transactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=commAddressDetailsVO.getTmpl_currency();
        }
        if (functions.isValueNull(transactionDetailsVO.getCardType()))
        {
            String type=GatewayAccountService.getPaymentBrand(transactionDetailsVO.getCardType());
            if(type.equalsIgnoreCase("AMEX"))
            {
                card_type = "1";
            }else if(type.equalsIgnoreCase("VISA"))
            {
                card_type = "2";
            }else if(type.equalsIgnoreCase("MC"))
            {
                card_type = "3";
            }else if(type.equalsIgnoreCase("DISC"))
            {
                card_type = "4";
            }
        }

        String street="";
        String country="";
        String state="";
        String city="";
        String zip="";
        String phone="";

        if(functions.isValueNull(commAddressDetailsVO.getStreet())){
            street=commAddressDetailsVO.getStreet();
        }

        if(functions.isValueNull(commAddressDetailsVO.getCountry())){
            country=commAddressDetailsVO.getCountry();
        }

        if(functions.isValueNull(commAddressDetailsVO.getState())){
            state=commAddressDetailsVO.getState();
        }

        if(functions.isValueNull(commAddressDetailsVO.getCity())){
            city=commAddressDetailsVO.getCity();
        }

        if(functions.isValueNull(commAddressDetailsVO.getZipCode())){
            zip=commAddressDetailsVO.getZipCode();
        }

        if(functions.isValueNull(commAddressDetailsVO.getPhone())){
            phone=commAddressDetailsVO.getPhone();
        }

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat1=new SimpleDateFormat("yyyyMMdd");

        try
        {
            String dateOfBirth="";
            if(functions.isValueNull(commAddressDetailsVO.getBirthdate()))
            {
                if (!commAddressDetailsVO.getBirthdate().contains("-"))
                {
                    dateOfBirth = dateFormat.format(dateFormat1.parse(commAddressDetailsVO.getBirthdate()));
                }
                else
                {
                    dateOfBirth = commAddressDetailsVO.getBirthdate();
                }
            }


            String first_name=new String(commAddressDetailsVO.getFirstname().getBytes(), StandardCharsets.UTF_8);

            String last_name=new String(commAddressDetailsVO.getLastname().getBytes(), StandardCharsets.UTF_8);


            StringBuffer request = new StringBuffer();
            StringBuffer requestlog = new StringBuffer();

            request.append("{" +
                    "\"Authorization\":\""+AuthenticationKey+"\"," +
                    "\"userData\":{\"first_name\":\""+ first_name+"\"," +
                    "\"last_name\":\""+last_name+"\"," +
                    "\"email\":\""+commAddressDetailsVO.getEmail()+"\"," +
                    "\"address\":\""+street+"\"," +
                    "\"country\":\""+country+"\"," +
                    "\"state\":\""+state+"\"," +
                    "\"city\":\""+city+"\"," +
                    "\"zip\":\""+zip+"\"," +
                    "\"phone\":\""+phone+"\"," +
                    "\"ip\":\""+commAddressDetailsVO.getCardHolderIpAddress()+"\"," +
                    "\"birthday\":\""+dateOfBirth+"\"," +
                    "\"username\":\""+merchantId+"\"}," +
                    "\"cardData\":{\"name\":\""+first_name+" "+last_name+"\","+
                    "\"type\":\""+card_type+"\"," +
                    "\"number\":\""+commCardDetailsVO.getCardNum() +"\"," +
                    "\"month\":\""+commCardDetailsVO.getExpMonth()+"\"," +
                    "\"year\":\""+commCardDetailsVO.getExpYear()+"\"," +
                    "\"cvv\":\""+commCardDetailsVO.getcVV()+"\"}," +
                    "\"ext_order_id\":\"\"," +
                    "\"amount\":\""+transactionDetailsVO.getAmount() +"\","+
                    "\"currency\":\""+Currency+"\""+
                    "}");


            requestlog.append("{" +
                    "\"Authorization\":\""+AuthenticationKey+"\"," +
                    "\"userData\":{\"first_name\":\""+ first_name+"\"," +
                    "\"last_name\":\""+last_name+"\"," +
                    "\"email\":\""+commAddressDetailsVO.getEmail()+"\"," +
                    "\"address\":\""+street+"\"," +
                    "\"country\":\""+country+"\"," +
                    "\"state\":\""+state+"\"," +
                    "\"city\":\""+city+"\"," +
                    "\"zip\":\""+zip+"\"," +
                    "\"phone\":\""+phone+"\"," +
                    "\"ip\":\""+commAddressDetailsVO.getCardHolderIpAddress()+"\"," +
                    "\"birthday\":\""+dateOfBirth+"\"," +
                    "\"username\":\""+merchantId+"\"}," +
                    "\"cardData\":{\"name\":\""+first_name+" "+last_name+"\","+
                    "\"type\":\""+card_type+"\"," +
                    "\"number\":\""+functions.maskingPan(commCardDetailsVO.getCardNum()) +"\"," +
                    "\"month\":\""+functions.maskingNumber(commCardDetailsVO.getExpMonth())+"\"," +
                    "\"year\":\""+functions.maskingNumber(commCardDetailsVO.getExpYear())+"\"," +
                    "\"cvv\":\""+functions.maskingNumber(commCardDetailsVO.getcVV())+"\"}," +
                    "\"ext_order_id\":\"\"," +
                    "\"amount\":\""+transactionDetailsVO.getAmount() +"\","+
                    "\"currency\":\""+Currency+"\""+
                    "}");




            String Response = "";
            transactionLogger.error("Totalpay salerequest:::::::for--" + trackingID + "--" + requestlog.toString() );
            Response = totalaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"), AuthenticationKey, request.toString());

            transactionLogger.error("Totalpay saleResponse:::::::for--" + trackingID + "--" + Response);

            if (functions.isValueNull(Response) && !functions.hasHTMLTags(Response) )
            {
                JSONObject jsonObject = new JSONObject(Response);
                String Paymentid = "";
                String Status = "";
                String Responsedate = "";
                String Description = "";


                if (jsonObject != null)
                {
                    if (jsonObject.has("id"))
                    {
                        Paymentid = jsonObject.getString("id");
                    }

                    if (jsonObject.has("status"))
                    {
                        Status = jsonObject.getString("status");
                    }

                    if (jsonObject.has("date"))
                    {
                        Responsedate = jsonObject.getString("date");
                    }


                    if (jsonObject.has("information_data"))
                    {
                        Description = jsonObject.getString("information_data");
                    }
                    if ("APPROVED".equalsIgnoreCase(Status))
                    {
                        commResponseVO.setStatus("success");
                    }
                    else if ("PENDING".equalsIgnoreCase(Status))
                    {
                        commResponseVO.setStatus("pending");
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                    }

                    commResponseVO.setTransactionId(Paymentid);
                    commResponseVO.setDescription(Description);
                    commResponseVO.setRemark(Description);
                    commResponseVO.setResponseTime(Responsedate);
                    commResponseVO.setCurrency(Currency);
                    commResponseVO.setTmpl_Amount(tmpl_amount);
                    commResponseVO.setTmpl_Currency(tmpl_currency);
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());

                }
                else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setRemark("Transaction Declined");
                    commResponseVO.setDescription("Transaction Declined");
                }
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setRemark("Invalid Response");
                commResponseVO.setDescription("Invalid Response");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error(" Totalpay processSale Exception--for--" + trackingID + "--", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("TotalPaygatway:: inside processmainSale()");

        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        TotalaPayUtils totalaPayUtils = new TotalaPayUtils();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String trackingID = transactionDetailsVO.getOrderId();
        String merchantid = gatewayAccount.getMerchantId();
        String Key = gatewayAccount.getFRAUD_FTP_PASSWORD();                          //RB.getString("LIVE_API_KEY");
        String AuthenticationKey = Base64.encode(Key.getBytes());

        String currency="";
        String amount="";

        if (functions.isValueNull(transactionDetailsVO.getCurrency()))
        {
            currency = transactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(transactionDetailsVO.getAmount()))
        {
            amount = transactionDetailsVO.getAmount();
        }

        String paymentid = transactionDetailsVO.getPreviousTransactionId();
        transactionLogger.error("paymentid:::::::" + paymentid);

        try
        {
            if (functions.isValueNull(paymentid))
            {
                String request = paymentid + "?Authorization=" + AuthenticationKey;
                transactionLogger.error("Totalpay Inquiry URL::::::::::for" + trackingID + "--" + RB.getString("LIVE_INQUIRY_URL") + request);

                String Response = "";
                Response = totalaPayUtils.doGetHTTPSURLConnectionClient(RB.getString("LIVE_INQUIRY_URL") + request);
                transactionLogger.error("Totalpay inquiryResponse:::::::for" + trackingID + "--" + Response);

                JSONObject jsonObject = new JSONObject(Response);
                String Paymentid = "";
                String Status = "";
                String Responsedate = "";
                String Description = "";


                if (jsonObject != null)
                {
                    if (jsonObject.has("id"))
                    {
                        Paymentid = jsonObject.getString("id");
                    }

                    if (jsonObject.has("status"))
                    {
                        Status = jsonObject.getString("status");
                    }

                    if (jsonObject.has("date"))
                    {
                        Responsedate = jsonObject.getString("date");
                    }


                    if (jsonObject.has("information_data"))
                    {
                        Description = jsonObject.getString("information_data");
                    }

                    transactionLogger.error("Description:::::::" + Description);


                    if ("APPROVED".equalsIgnoreCase(Status))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setTransactionStatus("success");
                        commResponseVO.setRemark(Description);
                        commResponseVO.setDescription(Description);
                    }
                    else if ("PENDING".equalsIgnoreCase(Status))
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setRemark(Description);
                        commResponseVO.setDescription(Description);
                    }
                    else if ("DECLINED".equalsIgnoreCase(Status) || "CANCELED".equalsIgnoreCase(Status))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionStatus("failed");
                        commResponseVO.setRemark(Description);
                        commResponseVO.setDescription(Description);
                    }
                    else
                    {
                        commResponseVO.setStatus(Status);
                        commResponseVO.setTransactionStatus(Status);
                        commResponseVO.setRemark(Description);
                        commResponseVO.setDescription(Description);
                    }
                }
                else
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setRemark("Transaction Failed");
                    commResponseVO.setDescription("Transaction Failed");
                }

                commResponseVO.setBankTransactionDate(Responsedate);
                commResponseVO.setTransactionId(Paymentid);
                commResponseVO.setMerchantId(merchantid);
                commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setAmount(amount);
                commResponseVO.setCurrency(currency);
            }
            else
            {
                commResponseVO.setDescription("Transaction Failed");
                commResponseVO.setRemark("Transaction Failed");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error(" Totalpay Inquiry Exception--for--" + trackingID + "--", e);
        }
        return commResponseVO;
    }


    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("TotalPaygatway:: inside processmainSale()");

        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        TotalaPayUtils totalaPayUtils = new TotalaPayUtils();
        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String merchantid = gatewayAccount.getMerchantId();
        String Key = gatewayAccount.getFRAUD_FTP_PASSWORD();                          //RB.getString("LIVE_API_KEY");
        String AuthenticationKey = Base64.encode(Key.getBytes());



        String paymentid = transactionDetailsVO.getPreviousTransactionId().trim();
        String amount=transactionDetailsVO.getAmount();
        String message="";
        String status="";
        transactionLogger.error("Totalpay Refund amount::::::::::" + amount);


        try
        {
            StringBuffer request = new StringBuffer();

            request.append("{" +
                    "\"Authorization\":\"" + AuthenticationKey + "\"," +
                    "\"amount\":\""+amount+"\"" +
                    "}");

            transactionLogger.error("Totalpay Refund URL::::::::::" + trackingID + "--" + RB.getString("LIVE_REFUND_URL") + paymentid);


            String Response = "";
            Response = totalaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_REFUND_URL") + paymentid, AuthenticationKey, request.toString());
            transactionLogger.error("Totalpay RefundResponse:::::::for" + trackingID + "--" + Response);

            JSONObject jsonObject = new JSONObject(Response);

            if (jsonObject != null)
            {
                if (jsonObject.has("information_data"))
                {
                    message = jsonObject.getString("information_data");
                }

                if (jsonObject.has("status"))
                {
                    status=jsonObject.getString("status");
                }

                if ("REFUNDED".equalsIgnoreCase(status))
                {
                    transactionLogger.error("Refund successfull:::::::for" + trackingID + "--" + amount);
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescription(message);
                    commResponseVO.setRemark(message);
                    commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
                else
                {
                    transactionLogger.error("Refund failed:::::::for" + trackingID);
                    commResponseVO.setStatus("fail");
                    commResponseVO.setDescription("Transaction Failed");
                    commResponseVO.setRemark("Transaction Failed");
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Transaction Failed");
                commResponseVO.setDescription("Transaction Failed");
            }

        }
        catch (Exception e)
        {
            transactionLogger.error(" Totalpay Refund Exception--for--" + trackingID + "--", e);
        }
        return commResponseVO;
    }

}


