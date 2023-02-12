package com.payment.quickcard;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.codehaus.jettison.json.JSONObject;
import javax.ws.rs.core.MediaType;
import java.util.ResourceBundle;

/**
 * Created by Admin on 4/2/19.
 */
public class QuickCardPaymentGateway extends AbstractPaymentGateway
{
    private static Logger logger = new Logger(QuickCardPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(QuickCardPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "quickcard";
    private static Functions functions = new Functions();
    private final static String SALE="Sale";
    private final static String REFUND="Refund";

    private  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.quickcard");
    String url="";

    public QuickCardPaymentGateway(String accountId)
    {
        this.accountId = accountId;
        transactionLogger.error("accountId------------------------->"+accountId);

        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();


        if (isTest)
        {
            url = RB.getString("TEST_URL");
        }
        else
        {
            url = RB.getString("LIVE_URL");
        }
    }


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        logger.debug("Inside QuickCard Sale-----");
        transactionLogger.error("Inside QuickCard Sale-----");
        Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String IsFxMid = gatewayAccount.getForexMid();
        boolean isTest=gatewayAccount.isTest();
        CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO=((CommRequestVO)requestVO).getAddressDetailsVO();
        CommCardDetailsVO cardDetailsVO=((CommRequestVO)requestVO).getCardDetailsVO();
        String client_id=gatewayAccount.getFRAUD_FTP_USERNAME();
        String client_secret=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String location_id=gatewayAccount.getFRAUD_FTP_PATH();

        transactionLogger.error( "client_id------"+client_id);
        transactionLogger.error( "client_secret------"+client_secret);
        transactionLogger.error( "location_id------"+location_id);
        transactionLogger.error( "IsFxMid---------------------------------------------------->"+IsFxMid);


        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        client.setConnectTimeout(180000);
        client.setReadTimeout(180000);

        WebResource service = null;

        if (isTest)
        {
            transactionLogger.error(":::::inside isTest:::::");
            service = client.resource(RB.getString("TEST_URL"));
            transactionLogger.error(RB.getString("TEST_URL"));
        }
        else
        {
            transactionLogger.error(":::::inside Live:::::");
            service = client.resource(RB.getString("LIVE_URL"));
        }

        try
        {
            String authtokenreq="{\n" +
                    "\"client_id\": \""+ client_id +"\",\n" +
                    "\"client_secret\": \""+client_secret+"\"\n" +
                    "}";

            transactionLogger.error("AuthToken Request for sale-----"+authtokenreq);

            String authtokenres = service.path("oauth").path("token").path("retrieve").header("Content-Type","application/json")
                    .type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class,authtokenreq);

            transactionLogger.error("AuthToken Response for sale-----"+authtokenres);
            String access_token = "";

            if (functions.isValueNull(authtokenres) && authtokenres.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(authtokenres);

                if (jsonObject != null)
                {
                    if (jsonObject.has("access_token")){
                        access_token = jsonObject.getString("access_token");
                        transactionLogger.error("access_token------" + access_token);
                    }
                }
            }
            String phone_number="";
            String card_number="";
            String card_cvv="";
            String amount="";
            String first_name="";
            String last_name="";
            String name="";
            String email="";
            String city="";
            String country="";

            String state="";
            String address="";
            String street="";
            String zipCode="";


            if(functions.isValueNull(addressDetailsVO.getPhone()))
                phone_number = addressDetailsVO.getPhone();
            else
                phone_number = "";

            if(functions.isValueNull(cardDetailsVO.getCardNum()))
                 card_number=cardDetailsVO.getCardNum();
            else
                card_number="";

            if(functions.isValueNull(cardDetailsVO.getcVV()))
                 card_cvv=cardDetailsVO.getcVV();
            else
                card_cvv="";

            if(functions.isValueNull(transactionDetailsVO.getAmount()))
                 amount=transactionDetailsVO.getAmount();
            else
                amount="";

            if(functions.isValueNull(addressDetailsVO.getFirstname()))
                 first_name=addressDetailsVO.getFirstname();
            else
                first_name="";

            if(functions.isValueNull(addressDetailsVO.getLastname()))
                 last_name=addressDetailsVO.getLastname();
            else
                last_name="";

            if(functions.isValueNull(addressDetailsVO.getEmail()))
                 email=addressDetailsVO.getEmail();
            else
                email="";

            if(functions.isValueNull(addressDetailsVO.getCity()))
                 city=addressDetailsVO.getCity();
            else
                city="";

            if (functions.isValueNull(addressDetailsVO.getCountry()))
                 country=addressDetailsVO.getCountry();
            else
                country="";

            if (functions.isValueNull(addressDetailsVO.getState()))
                state=addressDetailsVO.getState();
            else
                state="";

            if (functions.isValueNull(addressDetailsVO.getCity()))
                address=addressDetailsVO.getCity();
            else
                address="";

            if (functions.isValueNull(addressDetailsVO.getStreet()))
                street=addressDetailsVO.getStreet();
            else
                street="";
          transactionLogger.error("IsFxMid  ------------------------------------------------------------->"+IsFxMid);
            if("Y".equalsIgnoreCase(IsFxMid))
            {
                zipCode = "10021";
            }
            else if (functions.isValueNull(addressDetailsVO.getZipCode()))
                    zipCode = addressDetailsVO.getZipCode();
            else
                    zipCode = "";




            name=addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname();

            String year=cardDetailsVO.getExpYear();
            String expyear= year.substring(2,4);
            String exp_date=cardDetailsVO.getExpMonth()+""+expyear;
            transactionLogger.error("year-----"+expyear);

            String request="{" +
                    "\"auth_token\": \""+access_token+"\"," +
                    "\"location_id\": \""+location_id+"\"," +
                    "\"phone_number\": \""+phone_number+"\"," +
                    "\"exp_date\": \""+exp_date+"\"," +
                    "\"card_number\": \""+card_number+"\"," +
                    "\"card_cvv\":\""+ card_cvv+"\"," +
                    "\"amount\": \""+amount+"\"," +
                    "\"first_name\": \"" +first_name+"\"," +
                    "\"last_name\":\""+last_name+"\"," +
                    "\"name\": \""+name+"\"," +
                    "\"email\": \""+email+"\"," +
                    "\"city\": \""+city+"\"," +
                    "\"state\": \""+state+"\"," +
                    "\"address\": \""+address+"\"," +
                    "\"street\": \""+street+"\"," +
                    "\"zip_code\": \""+zipCode+"\"," +
                    "\"country\": \""+country+"\"" +
                    "}";
            String requestlog="{" +
                    "\"auth_token\": \""+access_token+"\"," +
                    "\"location_id\": \""+location_id+"\"," +
                    "\"phone_number\": \""+phone_number+"\"," +
                    "\"exp_date\": \""+functions.maskingNumber(exp_date)+"\"," +
                    "\"card_number\": \""+functions.maskingPan(card_number)+"\"," +
                    "\"card_cvv\":\""+ functions.maskingNumber(card_cvv)+"\"," +
                    "\"amount\": \""+amount+"\"," +
                    "\"first_name\": \"" +first_name+"\"," +
                    "\"last_name\":\""+last_name+"\"," +
                    "\"name\": \""+name+"\"," +
                    "\"email\": \""+email+"\"," +
                    "\"city\": \""+city+"\"," +
                    "\"state\": \""+state+"\"," +
                    "\"address\": \""+address+"\"," +
                    "\"street\": \""+street+"\"," +
                    "\"zip_code\": \""+zipCode+"\"," +
                    "\"country\": \""+country+"\"" +
                    "}";


            transactionLogger.error("Request for Sale---"+trackingID+"--"+requestlog);

            String response = service.path("api").path("registrations").path("virtual_transaction").header("Content-Type","application/json")
                    .type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, request);

            transactionLogger.error("service uri------------"+service.getURI());
            transactionLogger.error("Response for Sale---"+trackingID+"--"+response);

            if (functions.isValueNull(response) && response.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(response);
                String status = "";
                String message = "";
                boolean success=false;
                String transaction_id="";
                Double balance;
                if (jsonObject!=null)
                {
                    if (jsonObject.has("success"))
                    {
                        success = jsonObject.getBoolean("success");
                        transactionLogger.error("Success---" + success);
                    }
                    if (jsonObject.has("status"))
                    {
                        status = jsonObject.getString("status");
                        transactionLogger.error("Status---" + status);
                    }
                    if (jsonObject.has("message"))
                    {
                        message = jsonObject.getString("message");
                        transactionLogger.error("Message---" + message);
                    }
                    if (jsonObject.has("transaction_id"))
                    {
                        transaction_id = jsonObject.getString("transaction_id");
                        transactionLogger.error("transaction_id----" + transaction_id);
                    }
                    if (jsonObject.has("balance"))
                    {
                        balance = jsonObject.getDouble("balance");
                        transactionLogger.error("balance---" + balance);
                    }
                }
                if (status.equalsIgnoreCase("approved") && success)
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionId(transaction_id);
                    comm3DResponseVO.setTransactionType(SALE);
                    comm3DResponseVO.setRemark(status);
                    comm3DResponseVO.setDescription(status);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else {
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setTransactionId(transaction_id);
                    comm3DResponseVO.setTransactionType(SALE);
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }
            }
            else {
                comm3DResponseVO.setStatus("fail");
                comm3DResponseVO.setRemark("fail");
                comm3DResponseVO.setDescription("fail");
                comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception----- "+e);
            comm3DResponseVO.setStatus("fail");
            comm3DResponseVO.setRemark("Unauthorized");
            comm3DResponseVO.setDescription("Unauthorized");
            comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
        }
        return comm3DResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("QuickCardPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. Support Team",null);

    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        logger.debug("Inside QuickCard Refund-----");
        transactionLogger.error("Inside QuickCard Refund-----");
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        CommTransactionDetailsVO transactionDetailsVO = ((CommRequestVO) requestVO).getTransDetailsVO();
        String client_id = gatewayAccount.getFRAUD_FTP_USERNAME();
        String client_secret = gatewayAccount.getFRAUD_FTP_PASSWORD();

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        client.setConnectTimeout(180000);
        client.setReadTimeout(180000);

        WebResource service = null;

        if (isTest)
        {
            transactionLogger.error(":::::inside isTest:::::");
            service = client.resource(RB.getString("TEST_URL"));
        }
        else
        {
            transactionLogger.error(":::::inside Live:::::");
            service = client.resource(RB.getString("LIVE_URL"));
        }

        String captureamount=transactionDetailsVO.getPreviousTransactionAmount();
        transactionLogger.error("Capture Amount-----"+captureamount);

        String amount= transactionDetailsVO.getAmount();
        transactionLogger.error("Amount-----"+amount);

//        if(!captureamount.equals(amount))
//        {
//            PZExceptionHandler.raiseTechnicalViolationException(QuickCardPaymentGateway.class.getName(), "processRefund()", null, "common", "Partial Refund is Not Supported For This Bank(Green Box)", PZTechnicalExceptionEnum.NOT_ALLOWED_BY_GATEWAY,null, "Partial Refund is Not Supported For This Bank(Green Box)", new Throwable("Partial Refund is Not Supported For This Bank(Green Box)"));
//        }

        try
        {
            String authtokenreq = "{\n" +
                    "\"client_id\": \"" + client_id + "\",\n" +
                    "\"client_secret\": \"" + client_secret + "\"\n" +
                    "}";

            transactionLogger.error("AuthToken Request for refund---"+trackingID+"--" + authtokenreq);

            String authtokenres = service.path("oauth").path("token").path("retrieve").header("Content-Type", "application/json")
                    .type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, authtokenreq);

            transactionLogger.error("AuthToken Response for refund---"+trackingID+"--" + authtokenres);
            String access_token = "";

            if (functions.isValueNull(authtokenres) && authtokenres.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(authtokenres);

                if (jsonObject != null)
                {
                    if (jsonObject.has("access_token"))
                    {
                        access_token = jsonObject.getString("access_token");
                        transactionLogger.error("access_token------" + access_token);
                        }
                }
            }

            String refund = "{" +
                    "\"auth_token\": \"" + access_token + "\"," +
                    "\"transact_id\": \"" + transactionDetailsVO.getPreviousTransactionId() + "\"," +
                    "\"partial_amount\": \""+ transactionDetailsVO.getAmount() +"\""+
                    "}";

            transactionLogger.error("Request for Refund---"+trackingID+"--" + refund);

            String response = service.path("api").path("wallets").path("refund_money").header("Content-Type", "application/json")
                    .type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, refund);

            transactionLogger.error("Response for refund---"+trackingID+"--" + response);

            if (functions.isValueNull(response) && response.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(response);
                String status = "";
                String message = "";
                boolean success = false;
                String descriptor_id = "";

                if (jsonObject != null)
                {
                    if (jsonObject.has("status"))
                    {
                        status = jsonObject.getString("status");
                        transactionLogger.error("Status-----" + status);
                    }
                    if (jsonObject.has("message"))
                    {
                        message = jsonObject.getString("message");
                        transactionLogger.error("Message-----" + message);
                    }
                    if (jsonObject.has("success"))
                    {
                        success = jsonObject.getBoolean("success");
                        transactionLogger.error("Success-----" + success);
                    }
                    if (jsonObject.has("descriptor_id"))
                    {
                        descriptor_id = jsonObject.getString("descriptor_id");
                        transactionLogger.error("descriptor_id----" + descriptor_id);
                    }
                }
                if (status.equalsIgnoreCase("approved") && success)
                {
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setTransactionType(REFUND);
                    comm3DResponseVO.setRemark(status);
                    comm3DResponseVO.setDescription(status);
                    comm3DResponseVO.setTransactionId(descriptor_id);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());

                }
                else
                {
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setTransactionId(descriptor_id);
                    comm3DResponseVO.setTransactionType(REFUND);
                    comm3DResponseVO.setRemark(message);
                    comm3DResponseVO.setDescription(message);
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }

            }
            else
            {
                comm3DResponseVO.setStatus("fail");
                comm3DResponseVO.setRemark("fail");
                comm3DResponseVO.setDescription("fail");
                comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception", e);
            comm3DResponseVO.setStatus("fail");
            comm3DResponseVO.setRemark("Unauthorized");
            comm3DResponseVO.setDescription("Unauthorized");
            comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
        }
        return comm3DResponseVO;
    }
}





