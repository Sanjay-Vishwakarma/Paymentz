package com.payment.beekash;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payment.beekash.vos.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.codehaus.jettison.json.JSONObject;
import java.io.IOException;
import java.util.ResourceBundle;


/**
 * Created by NIKET on 12/10/2015.
 */
public class BeekashPaymentGateway extends AbstractPaymentGateway
{
    private static Logger logger = new Logger(BeekashPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(BeekashPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "beekash";
    private static Functions functions = new Functions();

    private final static String TEST_SALE_URL = "https://api.beekash.net/v1/CreateCharge";
    private final static String LIVE_SALE_URL = "https://bap098765.beekash.net/ProcessPayment";
    private final static String SALE="Sale";
    private final static String REFUND="Refund";
    private final static String CAPTURE="Capture";
    private final static String INQUIRY="Inquiry";
    private final static String VOID="Cancel";
    private final static String AUTH="Auth";

    private  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.beekash");

    private String url = "https://api.beekash.net/v1/CreateCharge";

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public BeekashPaymentGateway(String accountId)
    {
        this.accountId = accountId;

        Boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        if (isTest)
        {
            url = TEST_SALE_URL;
        }
        else
        {
            url = LIVE_SALE_URL;
        }

    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        logger.debug("ProcessSale Of beekash");
        transactionLogger.error("ProcessSale Of beekash---"+trackingID);
        StringBuffer requestUrl = new StringBuffer();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Request request = new Request();
        Request requestLog = new Request();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        CommCardDetailsVO cardDetailsVO = ((CommRequestVO) requestVO).getCardDetailsVO();
        CommTransactionDetailsVO transDetailsVO = ((CommRequestVO) requestVO).getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = ((CommRequestVO) requestVO).getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        String publish_key=gatewayAccount.getFRAUD_FTP_USERNAME();
        boolean isTest=gatewayAccount.isTest();
        String mid=gatewayAccount.getMerchantId();
        boolean capture=true;

        CommMerchantVO commMerchantVO= ((CommRequestVO) requestVO).getCommMerchantVO();
        String termUrl = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from RB----"+termUrl);
        }

        String url="";
        if("Y".equalsIgnoreCase(is3dSupported)){
            if(isTest){
                url=RB.getString("TEST_URL")+"Process3D";
            }else {
                url=RB.getString("LIVE_URL")+"Process3D";
            }
        }else {
            if(isTest){
                url=RB.getString("TEST_URL")+"CreateCharge";
            }else {
                url=RB.getString("LIVE_URL")+"CreateCharge";
            }
        }

        try
        {
            CardVO cardVO = new CardVO();
            CustomerVO customerVO = new CustomerVO();
            ShippingVO shippingVO = new ShippingVO();
            request.setPublish_key(publish_key);
            requestLog.setPublish_key(publish_key);


            cardVO.setCard_number(cardDetailsVO.getCardNum());
            cardVO.setName_on_card(commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname());
            cardVO.setCvc_code(cardDetailsVO.getcVV());
            cardVO.setExpiry_month(cardDetailsVO.getExpMonth());
            cardVO.setExpiry_year(cardDetailsVO.getExpYear());

            customerVO.setFirst_name(commAddressDetailsVO.getFirstname());
            customerVO.setLast_name(commAddressDetailsVO.getLastname());
            customerVO.setEmail_id(commAddressDetailsVO.getEmail());
            customerVO.setPhone_number(commAddressDetailsVO.getPhone());
            customerVO.setAddress(commAddressDetailsVO.getStreet());
            customerVO.setCity(commAddressDetailsVO.getCity());
            customerVO.setState(commAddressDetailsVO.getState());
            customerVO.setCountry_code(commAddressDetailsVO.getCountry());
            customerVO.setZip_code(commAddressDetailsVO.getZipCode());

            shippingVO.setZip_code(commAddressDetailsVO.getZipCode());
            shippingVO.setAddress(commAddressDetailsVO.getStreet());
            shippingVO.setCity(commAddressDetailsVO.getCity());
            shippingVO.setState(commAddressDetailsVO.getState());
            shippingVO.setCountry(commAddressDetailsVO.getCountry());

            request.setCard(cardVO);
            request.setCustomer(customerVO);
            request.setShipping(shippingVO);
            request.setIp_address(commAddressDetailsVO.getIp());
            request.setAmount(transDetailsVO.getAmount());
            request.setCurrency(transDetailsVO.getCurrency());
            request.setCapture(capture);
            request.setRemarks(transDetailsVO.getOrderDesc());
            request.setCharge_type("charge");
            request.setReturn_url(termUrl+trackingID);

            //requestLog.setCard(cardVO);
            requestLog.setCustomer(customerVO);
            requestLog.setShipping(shippingVO);
            requestLog.setIp_address(commAddressDetailsVO.getIp());
            requestLog.setAmount(transDetailsVO.getAmount());
            requestLog.setCurrency(transDetailsVO.getCurrency());
            requestLog.setCapture(capture);
            requestLog.setRemarks(transDetailsVO.getOrderDesc());
            requestLog.setCharge_type("charge");
            requestLog.setReturn_url(termUrl+trackingID);

            String req = gson.toJson(request);
            String reqLog = gson.toJson(requestLog);
            transactionLogger.error("sale BEEKASH request-----" + reqLog);
            String response = BeekashUtils.doPostHttpUrlConnection(url, req);
            transactionLogger.error("sale BEEKASH "+trackingID+" response-----" + response);

            if (functions.isValueNull(response) && response.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(response);
                String status_code = "";
                String message = "";
                String timeStamp="";
                String bank_Description="";

                if (jsonObject != null)
                {
                    if (jsonObject.has("status_code"))
                    {
                        status_code = jsonObject.getString("status_code");
                    }
                    if (jsonObject.has("message"))
                    {
                        message = jsonObject.getString("message");
                    }

                    if (jsonObject.has("timestamp"))
                    {
                        timeStamp = jsonObject.getString("timestamp");
                    }
                    if (jsonObject.has("bank_description"))
                    {
                        bank_Description = jsonObject.getString("bank_description");
                    }

                    if(status_code.equalsIgnoreCase("1003") && message.equalsIgnoreCase("pending"))
                    {
                        String transaction_id="";
                        String urlForRedirect="";
                        String key="";

                        if (jsonObject.has("result"))
                        {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                            if (jsonObject1.has("transaction_id"))
                            {
                                transaction_id = jsonObject1.getString("transaction_id");
                            }

                            if (jsonObject1.has("return_url"))
                            {
                                urlForRedirect = jsonObject1.getString("return_url");
                            }

                            if (jsonObject1.has("key"))
                            {
                                key = jsonObject1.getString("key");
                            }
                        }
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setUrlFor3DRedirect(urlForRedirect);
                        commResponseVO.setMd(key);
                        commResponseVO.setTransactionId(transaction_id);

                    }else if(status_code.equalsIgnoreCase("1000") && message.equalsIgnoreCase("success"))
                    {
                        String amount="";
                        String currency="";
                        String transactionId="";
                        if (jsonObject.has("result"))
                        {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                            if (jsonObject1.has("id"))
                            {
                                transactionId = jsonObject1.getString("id");
                            }

                            if (jsonObject1.has("amount"))
                            {
                                amount = jsonObject1.getString("amount");
                            }

                            if (jsonObject1.has("currency"))
                            {
                                currency = jsonObject1.getString("currency");
                            }

                        }
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        if(functions.isValueNull(amount)){
                            commResponseVO.setAmount(amount);
                        }
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setTransactionId(transactionId);
                        commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
                        commResponseVO.setTransactionType(SALE);
                        commResponseVO.setTransactionStatus(message);
                        commResponseVO.setMerchantId(mid);
                        commResponseVO.setAuthCode(status_code);
                    }else {
                        commResponseVO.setStatus("fail");
                    }
                    if(functions.isValueNull(bank_Description) && !bank_Description.contains("N/A")){
                        commResponseVO.setDescription(bank_Description);
                    }else {
                        commResponseVO.setDescription(message);
                    }
                    commResponseVO.setBankTransactionDate(timeStamp);
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception::::" , e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        logger.debug("ProcessAuth Of beekash");
        transactionLogger.debug("ProcessAuth Of beekash");
        StringBuffer requestUrl= new StringBuffer();
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        Request request= new Request();
        Request requestLog= new Request();
        Gson gson=new GsonBuilder().disableHtmlEscaping().create();
        CommCardDetailsVO cardDetailsVO=((CommRequestVO)requestVO).getCardDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
        CommMerchantVO merchantVO=((CommRequestVO)requestVO).getCommMerchantVO();
        CommAddressDetailsVO addressDetailsVO=((CommRequestVO)requestVO).getAddressDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String pulish_key=gatewayAccount.getFRAUD_FTP_USERNAME();
        boolean isTest=gatewayAccount.isTest();
        String mid=gatewayAccount.getMerchantId();
        boolean capture=false;

        String termUrl="";
        if(functions.isValueNull(merchantVO.getHostUrl()))
        {
            termUrl="https://"+merchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl=RB.getString("TERM_URL");
            transactionLogger.error("from term url----"+termUrl);
        }

        String url="";
        if (isTest){
            url=RB.getString("TEST_URL")+"CreateCharge";
        }else{
            url=RB.getString("LIVE_URL")+"CreateCharge";
        }

        try{
            CardVO cardVO=new CardVO();
            CustomerVO customerVO=new CustomerVO();
            ShippingVO shippingVO=new ShippingVO();
            request.setPublish_key(pulish_key);
            requestLog.setPublish_key(pulish_key);

            cardVO.setCard_number(cardDetailsVO.getCardNum());
            cardVO.setName_on_card(addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname());
            cardVO.setCvc_code(cardDetailsVO.getcVV());
            cardVO.setExpiry_month(cardDetailsVO.getExpMonth());
            cardVO.setExpiry_year(cardDetailsVO.getExpYear());

            customerVO.setFirst_name(addressDetailsVO.getFirstname());
            customerVO.setLast_name(addressDetailsVO.getLastname());
            customerVO.setEmail_id(addressDetailsVO.getEmail());
            customerVO.setPhone_number(addressDetailsVO.getPhone());
            customerVO.setAddress(addressDetailsVO.getStreet());
            customerVO.setCity(addressDetailsVO.getCity());
            customerVO.setState(addressDetailsVO.getState());
            customerVO.setCountry_code(addressDetailsVO.getCountry());
            customerVO.setZip_code(addressDetailsVO.getZipCode());

            shippingVO.setAddress(addressDetailsVO.getStreet());
            shippingVO.setCity(addressDetailsVO.getCity());
            shippingVO.setState(addressDetailsVO.getState());
            shippingVO.setCountry(addressDetailsVO.getCountry());
            shippingVO.setZip_code(addressDetailsVO.getZipCode());

            request.setCard(cardVO);
            request.setCustomer(customerVO);
            request.setShipping(shippingVO);
            request.setAmount(transactionDetailsVO.getAmount());
            request.setIp_address(addressDetailsVO.getIp());
            request.setCurrency(transactionDetailsVO.getCurrency());
            request.setCharge_type("charge");
            request.setCapture(capture);
            request.setRemarks(transactionDetailsVO.getOrderDesc());

            //requestLog.setCard(cardVO);
            requestLog.setCustomer(customerVO);
            requestLog.setShipping(shippingVO);
            requestLog.setAmount(transactionDetailsVO.getAmount());
            requestLog.setIp_address(addressDetailsVO.getIp());
            requestLog.setCurrency(transactionDetailsVO.getCurrency());
            requestLog.setCharge_type("charge");
            requestLog.setCapture(capture);
            requestLog.setRemarks(transactionDetailsVO.getOrderDesc());

            transactionLogger.debug("trackingID-----"+termUrl+trackingID);

            String req=gson.toJson(request);
            String reqLog=gson.toJson(requestLog);

            transactionLogger.error("auth request-----"+reqLog);
            transactionLogger.error("trackingID-----"+trackingID);

            String response=BeekashUtils.doPostHttpUrlConnection(url,req);
            transactionLogger.error("auth response-----"+response);

            if (functions.isValueNull(response) && response.contains("{")){
                JSONObject jsonObject=new JSONObject(response);
                String status_code="";
                String message="";
                String bankdescription="";
                String timestamp="";
                if(jsonObject!=null)
                {
                    if (jsonObject.has("status_code")){
                        status_code=jsonObject.getString("status_code");
                    }
                    if (jsonObject.has("message")){
                        message=jsonObject.getString("message");
                    }
                    if (jsonObject.has("bank_description")){
                        bankdescription=jsonObject.getString("bank_description");
                    }
                    if (jsonObject.has("timestamp")){
                        timestamp=jsonObject.getString("timestamp");
                    }

                    if (status_code.equalsIgnoreCase("1000") && message.equalsIgnoreCase("success"))
                    {
                        String id = "";
                        String amount = "";
                        String currency = "";
                        if (jsonObject.has("result"))
                        {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                            if (jsonObject1.has("id"))
                            {
                                id = jsonObject1.getString("id");
                            }
                            if (jsonObject1.has("amount"))
                            {
                                amount = jsonObject1.getString("amount");
                            }
                            if (jsonObject1.has(currency))
                            {
                                currency = jsonObject1.getString("currency");
                            }
                        }
                        comm3DResponseVO.setStatus("success");
                        comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        comm3DResponseVO.setTransactionId(id);
                        comm3DResponseVO.setAmount(amount);
                        comm3DResponseVO.setCurrency(currency);
                        comm3DResponseVO.setTransactionType(AUTH);
                        comm3DResponseVO.setTransactionStatus(message);
                        comm3DResponseVO.setMerchantId(mid);
                        comm3DResponseVO.setAuthCode(status_code);
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("fail");
                    }
                }
                comm3DResponseVO.setBankTransactionDate(timestamp);
                if (functions.isValueNull(bankdescription) && !bankdescription.contains("N/A")){
                    comm3DResponseVO.setDescription(bankdescription);
                }else{
                    comm3DResponseVO.setDescription(message);
                }
            }
        }
        catch(Exception e)
        {
            transactionLogger.error("Exception::::" ,e);
        }
        return comm3DResponseVO;

    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        {
            logger.debug("ProcessRefund Of beekash");
            transactionLogger.error("ProcessRefund Of beekash");
            Refund refund=new Refund();
            Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
            CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
            Gson gson=new GsonBuilder().disableHtmlEscaping().create();
            GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
            String pulish_key=gatewayAccount.getFRAUD_FTP_USERNAME();
            boolean isTest=gatewayAccount.isTest();
            String mid=gatewayAccount.getMerchantId();

            String url="";
            if(isTest){
                url=RB.getString("TEST_URL")+"CreateRefund";
            }
            else{
                url=RB.getString("LIVE_URL")+"CreateRefund";
            }

            try
            {
                refund.setPublish_key(pulish_key);
                refund.setTransaction_id(transactionDetailsVO.getPreviousTransactionId());
                refund.setRefund_amount(transactionDetailsVO.getAmount());
                refund.setRemarks(transactionDetailsVO.getOrderDesc());

                String req=gson.toJson(refund);
                transactionLogger.error("Refund request-----"+req);

                String response=BeekashUtils.doPostHttpUrlConnection(url,req);
                transactionLogger.error("Refund response-----"+response);

                if (functions.isValueNull(response) && response.contains("{"))
                {
                    String status_code="";
                    String message="";
                    String bank_description="";
                    String timestamp="";

                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject!=null){
                        if (jsonObject.has("status_code")){
                            status_code=jsonObject.getString("status_code");
                        }
                        if (jsonObject.has("message")){
                            message=jsonObject.getString("message");
                        }
                        if (jsonObject.has("bank_description")){
                            bank_description=jsonObject.getString("bank_description");
                        }
                        if (jsonObject.has("timestamp")){
                            timestamp=jsonObject.getString("timestamp");
                        }

                        if (status_code.equalsIgnoreCase("1000") && message.equalsIgnoreCase("success")){
                            String transaction_id="";
                            String refund_amount="";
                            String remarks="";
                            if (jsonObject.has("result")){

                                JSONObject jsonObject1=jsonObject.getJSONObject("result");

                                if (jsonObject1.has("transaction_id")){
                                    transaction_id=jsonObject1.getString("transaction_id");
                                }
                                if (jsonObject1.has("refund_amount")){
                                    refund_amount=jsonObject1.getString("refund_amount");
                                }
                                if (jsonObject1.has("remarks")){
                                    remarks=jsonObject1.getString("remarks");
                                }
                            }
                            comm3DResponseVO.setStatus("success");
                            comm3DResponseVO.setTransactionId(transaction_id);
                            comm3DResponseVO.setAmount(refund_amount);
                            comm3DResponseVO.setRemark(remarks);
                            comm3DResponseVO.setTransactionType(REFUND);
                            comm3DResponseVO.setTransactionStatus(message);
                            comm3DResponseVO.setMerchantId(mid);
                            comm3DResponseVO.setAuthCode(status_code);
                        }
                        else {
                            comm3DResponseVO.setStatus("fail");
                        }
                    }
                    comm3DResponseVO.setBankTransactionDate(timestamp);
                    if (functions.isValueNull(bank_description) && !bank_description.contains("N/A")){
                        comm3DResponseVO.setDescription(bank_description);
                    }else {
                        comm3DResponseVO.setDescription(message);
                    }
                }
            }
            catch (Exception e)
            {
                transactionLogger.error("Exception::::" , e);
            }
            return comm3DResponseVO;
        }
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        logger.debug("ProcessCapture for Beekash");
        transactionLogger.error("ProcessCapture for Beekash");
        Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String pulish_key=gatewayAccount.getFRAUD_FTP_USERNAME();
        boolean isTest=gatewayAccount.isTest();
        String mid=gatewayAccount.getMerchantId();
        Refund refund=new Refund();
        Gson gson=new GsonBuilder().disableHtmlEscaping().create();

        String url="";
        if(isTest){
            url=RB.getString("TEST_URL")+"CaptureCharge";
        }
        else{
            url=RB.getString("LIVE_URL")+"CaptureCharge";
        }

        try{
            refund.setPublish_key(pulish_key);
            transactionLogger.debug("ID-----"+transactionDetailsVO.getPreviousTransactionId());
            refund.setTransaction_id(transactionDetailsVO.getPreviousTransactionId());
            transactionLogger.debug("RID-----"+refund.getTransaction_id());

            String req=gson.toJson(refund);
            transactionLogger.error("Capture request-----"+req);

            String response=BeekashUtils.doPostHttpUrlConnection(url,req);
            transactionLogger.error("Capture response-----"+response);

            if (functions.isValueNull(response) && response.contains("{")){
                String status_code="";
                String message="";
                String bank_description="";
                String timestamp="";
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject!=null){
                    if (jsonObject.has("status_code")){
                        status_code=jsonObject.getString("status_code");
                    }
                    if (jsonObject.has("message")){
                        message=jsonObject.getString("message");
                    }
                    if (jsonObject.has("bank_description")){
                        bank_description=jsonObject.getString("bank_description");
                    }
                    if (jsonObject.has("timestamp")){
                        timestamp=jsonObject.getString("timestamp");
                    }

                    if (status_code.equalsIgnoreCase("1000") && message.equalsIgnoreCase("success")){
                        String transaction_id="";
                        if (jsonObject.has("result")){
                            JSONObject jsonObject1=jsonObject.getJSONObject("result");
                            if (jsonObject1.has("transaction_id")){
                                transaction_id=jsonObject1.getString("transaction_id");
                            }
                        }
                        comm3DResponseVO.setStatus("success");
                        comm3DResponseVO.setTransactionId(transaction_id);
                        comm3DResponseVO.setTransactionType(CAPTURE);
                        comm3DResponseVO.setTransactionStatus(message);
                        comm3DResponseVO.setMerchantId(mid);
                        comm3DResponseVO.setAuthCode(status_code);
                    }
                    else{
                        comm3DResponseVO.setStatus("fail");
                    }
                }
                comm3DResponseVO.setBankTransactionDate(timestamp);
                if (functions.isValueNull(bank_description) && !bank_description.equalsIgnoreCase("N/A")){
                    comm3DResponseVO.setDescription(bank_description);
                }
                else{
                    comm3DResponseVO.setDescription(message);
                }
            }
        }
        catch (Exception e){
            transactionLogger.error("Exception::::" , e);
        }
        return comm3DResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        logger.debug("");
        transactionLogger.error("");
        Gson gson=new GsonBuilder().disableHtmlEscaping().create();
        Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String pulish_key=gatewayAccount.getFRAUD_FTP_USERNAME();
        boolean isTest=gatewayAccount.isTest();
        String mid=gatewayAccount.getMerchantId();
        Refund refund=new Refund();
        String url="";
        if (isTest){
            url=RB.getString("TEST_URL")+"VoidCharge";
        }
        else{
            url=RB.getString("LIVE_URL")+"VoidCharge";
        }

        try{
            refund.setPublish_key(pulish_key);
            refund.setTransaction_id(transactionDetailsVO.getPreviousTransactionId());
            String req=gson.toJson(refund);
            transactionLogger.error("VoidCharge request-----"+req);
            String response=BeekashUtils.doPostHttpUrlConnection(url,req);
            transactionLogger.error("VoidCharge response-----" + response);

            if (functions.isValueNull(response) && response.contains("{")){
                String status_code="";
                String message="";
                String bank_description="";
                String timestamp="";
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject!=null)
                {
                    if (jsonObject.has("status_code"))
                    {
                        status_code = jsonObject.getString("status_code");
                    }
                    if (jsonObject.has("message"))
                    {
                        message = jsonObject.getString("message");
                    }
                    if (jsonObject.has("bank_description"))
                    {
                        bank_description = jsonObject.getString("bank_description");
                    }
                    if (jsonObject.has("timestamp"))
                    {
                        timestamp = jsonObject.getString("timestamp");
                    }

                    if (status_code.equalsIgnoreCase("1000") && message.equalsIgnoreCase("success"))
                    {
                        String transaction_id = "";
                        if (jsonObject.has("result"))
                        {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                            if (jsonObject1.has("transaction_id"))
                            {
                                transaction_id = jsonObject1.getString("transaction_id");
                            }
                        }
                        comm3DResponseVO.setStatus("success");
                        comm3DResponseVO.setTransactionId(transaction_id);
                        comm3DResponseVO.setTransactionType(VOID);
                        comm3DResponseVO.setTransactionStatus(message);
                        comm3DResponseVO.setMerchantId(mid);
                        comm3DResponseVO.setAuthCode(status_code);
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("fail");
                    }
                }
                comm3DResponseVO.setBankTransactionDate(timestamp);
                if (functions.isValueNull(bank_description) && !bank_description.equalsIgnoreCase("N/A")){
                    comm3DResponseVO.setDescription(bank_description);
                }
                else{
                    comm3DResponseVO.setDescription(message);
                }
            }
        }
        catch (Exception e){
            transactionLogger.error("Exception:::::" ,e);
        }
        return comm3DResponseVO;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        logger.debug("ProcessInquiry for Beekash");
        transactionLogger.error("ProcessInquiry for Beekash");
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=((CommRequestVO)requestVO).getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String pulish_key=gatewayAccount.getFRAUD_FTP_USERNAME();
        boolean isTest=gatewayAccount.isTest();
        Refund refund=new Refund();
        String mid=gatewayAccount.getMerchantId();
        String url="";
        if (isTest){
            url=RB.getString("TEST_URL")+"TransactionStatus";
        }
        else{
            url=RB.getString("LIVE_URL")+"TransactionStatus";
        }

        try{
            refund.setPublish_key(pulish_key);
            refund.setTransaction_id(transactionDetailsVO.getPreviousTransactionId());
            String req=gson.toJson(refund);
            transactionLogger.error("Inquiry request-----"+req);
            String response=BeekashUtils.doPostHttpUrlConnection(url,req);
            transactionLogger.error("Inquiry response-----"+response);

            if (functions.isValueNull(response) && response.contains("{"))
            {
                String status_code = "";
                String message = "";
                String bank_description = "";
                String timestamp = "";
                String id = "";
                String amount = "";
                String currency = "";
                boolean capture;
                String charge_type = "";
                String transaction_date_time = "";

                String tranDescription = "";
                String sale_responseCode = "";
                String sale_responseStatus = "";
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject != null)
                {
                    if (jsonObject.has("status_code"))
                    {
                        status_code = jsonObject.getString("status_code");
                    }
                    if (jsonObject.has("message"))
                    {
                        message = jsonObject.getString("message");
                    }
                    if (jsonObject.has("bank_description"))
                    {
                        bank_description = jsonObject.getString("bank_description");
                    }
                    if (jsonObject.has("timestamp"))
                    {
                        timestamp = jsonObject.getString("timestamp");
                    }
                    if(status_code.equalsIgnoreCase("1000") && message.equalsIgnoreCase("success"))
                    {
                        if (jsonObject.has("result"))
                        {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                            if (jsonObject1.has("id"))
                            {
                                id = jsonObject1.getString("id");
                            }
                            if (jsonObject1.has("amount"))
                            {
                                amount = jsonObject1.getString("amount");
                            }
                            if (jsonObject1.has("currency"))
                            {
                                currency = jsonObject1.getString("currency");
                            }
                            if (jsonObject1.has("capture"))
                            {
                                capture = jsonObject1.getBoolean("capture");
                            }
                            if (jsonObject1.has("charge_type"))
                            {
                                charge_type = jsonObject1.getString("charge_type");
                            }
                            if (jsonObject1.has("transaction_date_time"))
                            {
                                transaction_date_time = jsonObject1.getString("transaction_date_time");
                            }
                            if (jsonObject1.has("status_code"))
                            {
                                sale_responseCode = jsonObject1.getString("status_code");
                            }
                            if (jsonObject1.has("status_description"))
                            {
                                sale_responseStatus = jsonObject1.getString("status_description");
                            }
                            transactionLogger.error("Status code---"+sale_responseCode+"---"+sale_responseStatus);
                            if(sale_responseCode.equalsIgnoreCase("1000") && sale_responseStatus.equalsIgnoreCase("success"))
                            {
                                tranDescription = "Transaction Approved";
                                comm3DResponseVO.setStatus("success");
                                comm3DResponseVO.setTransactionId(id);
                                comm3DResponseVO.setAmount(amount);
                                comm3DResponseVO.setCurrency(currency);
                                comm3DResponseVO.setBankTransactionDate(transaction_date_time);
                                comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                comm3DResponseVO.setTransactionType(INQUIRY);
                                comm3DResponseVO.setTransactionStatus(message);
                                comm3DResponseVO.setMerchantId(mid);
                                comm3DResponseVO.setAuthCode(status_code);
                            }
                            else if(sale_responseCode.equalsIgnoreCase("1003") && sale_responseStatus.equalsIgnoreCase("pending"))
                            {
                                tranDescription = "Transaction Pending";
                                comm3DResponseVO.setStatus("pending");
                                comm3DResponseVO.setTransactionId(id);
                                comm3DResponseVO.setAmount(amount);
                                comm3DResponseVO.setCurrency(currency);
                                comm3DResponseVO.setBankTransactionDate(transaction_date_time);
                                comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                comm3DResponseVO.setTransactionType(INQUIRY);
                                //comm3DResponseVO.setTransactionStatus(message);
                                comm3DResponseVO.setMerchantId(mid);
                                comm3DResponseVO.setAuthCode(status_code);
                            }
                            else
                            {
                                tranDescription = "Transaction Declined";
                                comm3DResponseVO.setStatus("fail");
                                comm3DResponseVO.setTransactionId(id);
                                comm3DResponseVO.setAmount(amount);
                                comm3DResponseVO.setCurrency(currency);
                                comm3DResponseVO.setBankTransactionDate(transaction_date_time);
                                comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                comm3DResponseVO.setTransactionType(INQUIRY);
                                //comm3DResponseVO.setTransactionStatus(message);
                                comm3DResponseVO.setMerchantId(mid);
                                comm3DResponseVO.setAuthCode(status_code);
                            }

                        }
                    }
                    else
                    {
                        comm3DResponseVO.setStatus("fail");
                    }
                }
                comm3DResponseVO.setBankTransactionDate(timestamp);
                if (functions.isValueNull(bank_description) && !bank_description.contains("N/A"))
                {
                    comm3DResponseVO.setDescription(bank_description);
                }
                else
                {
                    comm3DResponseVO.setDescription(tranDescription);
                }
            }
        }
        catch (Exception e){
            transactionLogger.error("Exception:::::" , e);
        }
        return comm3DResponseVO;
    }
}



