package com.payment.europay.core;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/1/13
 * Time: 11:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class EuroPayPaymentGateway extends AbstractPaymentGateway
{

    public static final String GATEWAY_TYPE = "EuroPay";
    private static Logger log = new Logger(EuroPayPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(EuroPayPaymentGateway.class.getName());

    private static final String Url = "https://gate.opx.io/transactions/";
    //private static final String Url = "https://gate.opx.io/transactions/refund";
    //private static final String Url = "https://gate.opx.io/transactions/process";
    //private static final String queryUrl = "https://gate.opx.io/transactions/getFull";

    public EuroPayPaymentGateway(String accountid)
    {
        this.accountId = accountid;
    }

    public String getServiceKey()
    {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey)
    {
        this.serviceKey = serviceKey;
    }

    public String getRoutingKey()
    {
        return routingKey;
    }

    public void setRoutingKey(String routingKey)
    {
        this.routingKey = routingKey;
    }

    private String serviceKey;
    private String routingKey;

    public EuroPayPaymentGateway() throws SystemError
    {
    }


    @Override
    public String getMaxWaitDays()
    {
        return "5";
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
      EuroPayRequestVO euroPayRequestVO = (EuroPayRequestVO) requestVO;

      serviceKey = euroPayRequestVO.getCommMerchantVO().getMerchantUsername();
      routingKey = euroPayRequestVO.getCommMerchantVO().getPassword();

      EuroPayIdentity euroPayIdentity  = new EuroPayIdentity();
      euroPayIdentity.setIp(euroPayRequestVO.getAddressDetailsVO().getIp());
      euroPayIdentity.setEmail(euroPayRequestVO.getAddressDetailsVO().getEmail());

      EuroPayAddress euroPayAddress = new EuroPayAddress();

      if(euroPayRequestVO.getType() == null)
        euroPayAddress.setType("Private");
      else
        euroPayAddress.setType(euroPayRequestVO.getType());

      euroPayAddress.setCountry(euroPayRequestVO.getAddressDetailsVO().getCountry());
      euroPayAddress.setCity(euroPayRequestVO.getAddressDetailsVO().getCity());
      euroPayAddress.setState(euroPayRequestVO.getAddressDetailsVO().getState());
      euroPayAddress.setStreet(euroPayRequestVO.getAddressDetailsVO().getStreet());
      euroPayAddress.setZip(euroPayRequestVO.getAddressDetailsVO().getZipCode());

      EuroPayPayment euroPayPayment = new EuroPayPayment();

      if(euroPayRequestVO.getUsageL1() == null)
        euroPayPayment.setUsageL1("Sale Reference Usage 1");
     else
        euroPayPayment.setUsageL1(euroPayRequestVO.getUsageL1());

      if(euroPayRequestVO.getUsageL2() == null)
        euroPayPayment.setUsageL2("Sale Reference Usage 2");
      else
        euroPayPayment.setUsageL2(euroPayRequestVO.getUsageL2());

      String amount = euroPayRequestVO.getTransDetailsVO().getAmount();
      String newAmount = amount.replace(".","");
      euroPayPayment.setAmount(newAmount);

      euroPayPayment.setCurrency(euroPayRequestVO.getTransDetailsVO().getCurrency());

      EuroPayCustomer euroPayCustomer = new EuroPayCustomer();
      euroPayCustomer.setFirstName(euroPayRequestVO.getAddressDetailsVO().getFirstname());
      euroPayCustomer.setLastName(euroPayRequestVO.getAddressDetailsVO().getLastname());

      if(euroPayRequestVO.getSalutation() == null)
            euroPayCustomer.setSalutation("Salutation");
      else
            euroPayCustomer.setSalutation(euroPayRequestVO.getSalutation());


      if(euroPayRequestVO.getBirthDay() == null)
        euroPayCustomer.setBirthDay("06");
      else
        euroPayCustomer.setBirthDay(euroPayRequestVO.getBirthDay());

      if(euroPayRequestVO.getBirthMonth() == null)
        euroPayCustomer.setBirthMonth("08");
      else
        euroPayCustomer.setBirthMonth(euroPayRequestVO.getBirthMonth());

      if(euroPayRequestVO.getBirthYear() == null)
        euroPayCustomer.setBirthYear("1985");
      else
        euroPayCustomer.setBirthYear(euroPayRequestVO.getBirthYear());


      euroPayCustomer.setEuroPayAddress(euroPayAddress);
      euroPayCustomer.setEuroPayIdentity(euroPayIdentity);

      EuroPayCard euroPayCard = new EuroPayCard();
      euroPayRequestVO.getCardDetailsVO().getCardHolderFirstName();
      euroPayCard.setOwnerFirstName(euroPayRequestVO.getAddressDetailsVO().getFirstname());
      euroPayCard.setOwnerLastName(euroPayRequestVO.getAddressDetailsVO().getLastname());
      euroPayCard.setExpireMonth(euroPayRequestVO.getCardDetailsVO().getExpMonth());
      euroPayCard.setExpireYear(euroPayRequestVO.getCardDetailsVO().getExpYear());
      euroPayCard.setVerification(euroPayRequestVO.getCardDetailsVO().getcVV());
      euroPayCard.setNumber(euroPayRequestVO.getCardDetailsVO().getCardNum());
      euroPayCard.setBrand(euroPayRequestVO.getCardDetailsVO().getCardType());
       if(euroPayCard.getBrand() == null)
           euroPayCard.setBrand("VISA");

      //Date date = new Date()

     DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
	 Date date = new Date();
     String dt = dateFormat.format(date);
     EuroPayTransactions euroPayTransactions = new EuroPayTransactions();
     euroPayTransactions.setMcTxDate(dt);

      euroPayTransactions.setMethod("CC");
      euroPayTransactions.setType("DB");

      euroPayTransactions.setMcTxId(trackingID);

      euroPayTransactions.setEuroPayPayment(euroPayPayment);
      euroPayTransactions.setEuroPayCustomer(euroPayCustomer);
      euroPayTransactions.setEuroPayCard(euroPayCard);

      TransactionsRequest transactionsRequest = new TransactionsRequest();
      transactionsRequest.setAccountId(euroPayRequestVO.getCommMerchantVO().getMerchantId());
      transactionsRequest.setMode("sync");
      transactionsRequest.setEuroPayTransactions(euroPayTransactions);
      transactions_response response = new transactions_response();


                EuroPayResponseVO euroPayResponseVO = new EuroPayResponseVO();

                response = processSaleRequest(transactionsRequest);

                if(response.getCode().equalsIgnoreCase("400.500.004"))
                {
                   String desc = ErrorsCodes.errors.get(response.getCode()).toString();
                   euroPayResponseVO.setDescription(desc);
                   euroPayResponseVO.setErrorCode(response.getCode());
                   euroPayResponseVO.setStatus("fail");
                   euroPayResponseVO.setDescriptor(desc);
                   return  euroPayResponseVO;
                }

                String statusCode = response.getProcessResults().getCode();
                String status = statusCode  == "000.000.000" ? "success":"fail";

                euroPayResponseVO.setErrorCode(statusCode);
                euroPayResponseVO.setStatus(status);
                euroPayResponseVO.setTransactionId(response.getProcessResults().getTxId());

                String msg = response.getProcessResults().getMessage().toString();
                Response msgResponse = new Response();
                XStream msgXstream = new XStream();
                msgXstream.alias("response",Response.class);
                msgResponse  = (Response)  msgXstream.fromXML(msg);

                log.debug(" xml respone : " + msgResponse);
                transactionLogger.debug(" xml respone : " + msgResponse);



                String description = msgResponse.getRemark();

                log.debug("remark : " + description);
                transactionLogger.debug("remark : " + description);
                euroPayResponseVO.setDescription(description);
                euroPayResponseVO.setMcTxId(response.getProcessResults().getMcTxId());
                euroPayResponseVO.setTxId(response.getProcessResults().getTxId());
                euroPayResponseVO.setDeltaMsec(response.getProcessResults().getDeltaMsec());
                euroPayResponseVO.setDescription(description);

                log.debug(response.toString());
                transactionLogger.debug(response.toString());

                return  euroPayResponseVO;


    }

public transactions_response processSaleRequest(TransactionsRequest request) throws PZTechnicalViolationException
{
        transactions_response response = new transactions_response();

        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.aliasPackage("","com.payment.europay.core");
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        StringBuilder xmlOut = sb.append(System.getProperty("line.separator")).append(xstream.toXML(request));
        //StringBuilder xmlOut = sb.append(xstream.toXML(request));

        String str = xmlOut.toString();
        str = str.replaceAll("__","_");

        //str = "<transactions_request accountId=\"1\" mode=\"sync\"><transactions type=\"DB\" method=\"CC\" mcTxDate=\"2013-07-07T06:59:43+0000\" mcTxId=\"OAt2woZ\"><payment usageL2=\"Reference line 2\" usageL1=\"Reference line 1\" amount=\"96\" currency=\"USD\"/><customer birthDay=\"08\" birthMonth=\"06\" birthYear=\"1965\" salutation=\"MRS\" lastName=\"Doe\" firstName=\"Jenny\"><addresses type=\"private\" country=\"US\" state=\"LA\" street=\"1184 Maple St Ext\" zip=\"70122\" city=\"New Orleans\"/><identity ip=\"17.146.233.11\" email=\"john_jenny.doe@mac.com\"/><phones type=\"mobile_private\" number=\"+49123456789\"/><phones type=\"landline_private\" number=\"0049123456789\"/></customer><creditCard ownerLastName=\"Arnold\" ownerFirstName=\"Frank\" expireMonth=\"07\" expireYear=\"2015\" verification=\"531\" number=\"4485895710308\" brand=\"VISA\"/></transactions></transactions_request>";

        try
        {
            //getAuthToken();

            String processUrl = Url + "process";
            PostMethod post = new PostMethod(processUrl);
            post.setRequestHeader("Content-Type","application/xml");
            post.setRequestHeader("Service-Key",serviceKey);
            post.setRequestHeader("Routing-Key",routingKey);

            HttpClient httpClient = new HttpClient();

            log.debug("Sale request" + str);
            transactionLogger.debug("Sale request" + str);
            post.setRequestEntity(new StringRequestEntity(str,"text/xml", "ISO-8859-1"));

            httpClient.executeMethod(post);
            String responseXML = new String(post.getResponseBody());
            log.debug(responseXML);
            transactionLogger.debug(responseXML);

            XStream responseXstream = new XStream();
            responseXstream.alias("transactions_response",transactions_response.class);
            response = (transactions_response)xstream.fromXML(responseXML);

            log.debug(response.getCode());
            log.debug(response.getVersion());

            transactionLogger.debug(response.getCode());
            transactionLogger.debug(response.getVersion());
            if(!response.getCode().equalsIgnoreCase("400.500.004"))
            {
                log.debug(response.getProcessResults().getCode());
                log.debug(response.getProcessResults().getCode());

                transactionLogger.debug(response.getProcessResults().getCode());
                transactionLogger.debug(response.getProcessResults().getCode());
            }
        }
        catch (HttpException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EuroPayPaymentGateway.class.getName(),"processSaleRequest()",null,"common","Https Exception while placing transaction", PZTechnicalExceptionEnum.HTTP_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EuroPayPaymentGateway.class.getName(), "processSaleRequest()", null, "common", "UnsupportedEncoding Exception while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EuroPayPaymentGateway.class.getName(), "processSaleRequest()", null, "common", "IO Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }


        return response;
    }

    private String getAuthToken()
        {
            String transactionGuid = "";
            Connection conn = null;
            try
            {

                conn = Database.getConnection();
                String auth_details = "select  service_key, routing_key from europay_auth_details where status = 'ACTIVE'";
                PreparedStatement authstmt = conn.prepareStatement(auth_details);
                ResultSet rsAuthDetails = authstmt.executeQuery();
                if (rsAuthDetails.next())
                {
                    serviceKey = rsAuthDetails.getString("service_key");
                    routingKey = rsAuthDetails.getString("routing_key");
                }
            }
            catch (Exception e)
            {
                transactionLogger.error("Exception ---",e);
            }
            finally
            {
                Database.closeConnection(conn);
            }
            return transactionGuid;
        }


    @Override
    public GenericResponseVO processRefund(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
            transactions_response response = new transactions_response();

            CommRequestVO euroPayRequestVO = (CommRequestVO) requestVO;

            serviceKey = euroPayRequestVO.getCommMerchantVO().getMerchantUsername();
            routingKey = euroPayRequestVO.getCommMerchantVO().getPassword();

            String amount =  euroPayRequestVO.getTransDetailsVO().getAmount();
            String txId =  euroPayRequestVO.getTransDetailsVO().getPreviousTransactionId();
            int txnPaise = (new BigDecimal(amount).multiply(new BigDecimal(100.00))).intValue();


                RefundRequest request = new RefundRequest();
                String refundUrl = Url + "refund";
                request.url = refundUrl;
                request.setAmount(txnPaise);
                request.setTxId(txId);

                EuroPayResponseVO euroPayResponseVO = new EuroPayResponseVO();
                response = processRefundRequest(request);

                String statusCode = response.getProcessResults().getCode();
                String status = statusCode  == "000.000.000" ? "success":"fail";

                euroPayResponseVO.setErrorCode(statusCode);
                euroPayResponseVO.setStatus(status);

                return euroPayResponseVO;


    }


    public transactions_response processRefundRequest(RefundRequest request) throws PZTechnicalViolationException
    {
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.aliasPackage("","com.payment.europay.core");
        StringBuilder sb = new StringBuilder();
        sb.append("txId");
        sb.append("=");
        sb.append(request.getTxId());
        sb.append("&amount");
        sb.append("=");
        sb.append(request.getAmount());

        transactions_response response = new transactions_response();

        try
        {
            //getAuthToken();

            HttpClient httpClient = new HttpClient();
            String refundUrl = Url + "refund";
            PostMethod post = new PostMethod(refundUrl);

            post.setRequestHeader("Accept","application/xml");
            post.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
            post.setRequestHeader("Service-Key",serviceKey);
            post.setRequestHeader("Routing-Key",routingKey);
            log.debug(sb.toString());
            transactionLogger.debug(sb.toString());

            NameValuePair[] params = {new NameValuePair("requestXML", sb.toString())};
            post.setRequestBody(params);
            httpClient.executeMethod(post);

            String responseXML = new String(post.getResponseBodyAsString());
            log.debug(responseXML);
            transactionLogger.debug(responseXML);

            XStream responseXstream = new XStream();
            responseXstream.alias("transactions_response",transactions_response.class);
            response = (transactions_response)xstream.fromXML(responseXML);
        }
        catch (HttpException e)
        {
           PZExceptionHandler.raiseTechnicalViolationException(EuroPayPayment.class.getName(),"processRefundRequest()",null,"common","Http Exception while refunding transaction",PZTechnicalExceptionEnum.HTTP_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
           PZExceptionHandler.raiseTechnicalViolationException(EuroPayPayment.class.getName(),"processRefundRequest()",null,"common","IO Exception while refunding transaction",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }


        return response;
    }

    @Override
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
            transactions_response response = new transactions_response();
            CommRequestVO euroPayRequestVO = (CommRequestVO) requestVO;

            routingKey = euroPayRequestVO.getCommMerchantVO().getMerchantUsername();
            serviceKey = euroPayRequestVO.getCommMerchantVO().getPassword();

            String txId =  euroPayRequestVO.getTransDetailsVO().getPreviousTransactionId();
            String accountId =   euroPayRequestVO.getCommMerchantVO().getMerchantId();

                response = processQueryRequest(txId, accountId);

                EuroPayResponseVO euroPayResponseVO = new EuroPayResponseVO();
                String statusCode = response.getProcessResults().getCode();
                String status = statusCode  == "400.500.000" ? "success":"fail";

                euroPayResponseVO.setErrorCode(statusCode);
                euroPayResponseVO.setStatus(status);

        return  response;

    }

    public transactions_response processQueryRequest(String txId,String accountId) throws PZTechnicalViolationException
    {
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.aliasPackage("","com.payment.europay.core");
        StringBuilder sb = new StringBuilder();
        sb.append("txId=");
        sb.append(txId);
        sb.append("&accountId=");
        sb.append(accountId);

        transactions_response response = new transactions_response();

        log.debug(" response " + response.toString());
        transactionLogger.debug(" response " + response.toString());

        try
        {

            //getAuthToken();

            HttpClient httpClient = new HttpClient();
            String queryUrl = Url + "getFull";
            PostMethod post = new PostMethod(queryUrl);

            log.debug(" qu4ery url " + queryUrl);
            transactionLogger.debug(" qu4ery url " + queryUrl);

            serviceKey = "f3f3abdb4ca0db2b4ba76deaee9544e44a6861b643e3804f83771435";
            routingKey = "173e0f75d663be1fe1f4883110bd592114254aea0bb4c245272e60b6";

            post.setRequestHeader("Accept","application/xml");
            post.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
            post.setRequestHeader("Service-Key",serviceKey);
            post.setRequestHeader("Routing-Key",routingKey);
            log.debug(sb.toString());
            transactionLogger.debug(sb.toString());

            NameValuePair[] params = {new NameValuePair("requestXML", sb.toString())};
            post.setRequestBody(params);
            httpClient.executeMethod(post);

            String responseXML = new String(post.getResponseBodyAsString());
            log.debug(responseXML);
            transactionLogger.debug(responseXML);

            responseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><transactions_response version=\"1.0.2-202-2013-08-09_18-16-04\" code=\"400.500.000\"><searchResults terminalName=\"\" partnerName=\"\" merchantName=\"Transactworld Ltd.\" currency=\"USD\" creditAmountFloat=\"11.0\" debitAmountFloat=\"0.0\" creditAmount=\"1100\" debitAmount=\"0\" channelName=\"Andaz VISA MC 37\" message=\"\" reference=\"Reference Line 1 Reference Line 2\" code=\"800.900.100\" endStamp=\"1376766398711\" startStamp=\"1376766395264\" type=\"DB\" method=\"CC\" destination=\"Andaz via EPS\" mcTxId=\"108767\" txId=\"00001G6\"><authorization serviceIP=\"115.247.0.9\" serviceKey=\"f3f3abdb4ca0db2b4ba76deaee9544e44a6861b643e3804f83771435\" routingKey=\"173e0f75d663be1fe1f4883110bd592114254aea0bb4c245272e60b6\"/><token ccBrand=\"VISA\" ownerLastName=\"latst\" ownerFirstName=\"tetst\" expireMonth=\"1\" expireYear=\"2014\" lead6=\"411111\" trail4=\"1122\" panAlias=\"9080122011001121122\" value=\"c73ad1fa6c069d6d5cf433cfac502665ba1e02fec2e1cabfe68e338a\"/><transactionLog created=\"1376766395270\" type=\"CUSTOMER_DETAILS\" log=\"{&quot;addresses&quot;:[{&quot;city&quot;:&quot;city&quot;,&quot;zip&quot;:&quot;123123&quot;,&quot;street&quot;:&quot;test ad&quot;,&quot;state&quot;:&quot;Maharashtra&quot;,&quot;country&quot;:&quot;IN&quot;,&quot;type&quot;:&quot;Private&quot;}],&quot;identity&quot;:{&quot;email&quot;:&quot;aatest@test.com&quot;,&quot;ip&quot;:&quot;191.162.98.89&quot;},&quot;firstName&quot;:&quot;test&quot;,&quot;lastName&quot;:&quot;last&quot;,&quot;salutation&quot;:&quot;Salutation&quot;,&quot;birthYear&quot;:&quot;1985&quot;,&quot;birthMonth&quot;:&quot;08&quot;,&quot;birthDay&quot;:&quot;06&quot;}\" logId=\"11230\"/><transactionLog created=\"1376766398704\" type=\"CONNECTOR_RESPONSE\" log=\"&quot;{\\&quot;status\\&quot;:\\&quot;declined\\&quot;,\\&quot;customer_id\\&quot;:347102,\\&quot;customer_account_id\\&quot;:346715,\\&quot;transaction_id\\&quot;:1124686,\\&quot;original_transaction_id\\&quot;:1124686,\\&quot;message\\&quot;:\\&quot;declined-transaction\\&quot;,\\&quot;raw_message\\&quot;:\\&quot;\\u003c?xml version\\u003d\\\\\\&quot;1.0\\\\\\&quot; encoding\\u003d\\\\\\&quot;UTF-8\\\\\\&quot;?\\u003e\\\\n\\u003cresponse\\u003e\\\\n\\\\n\\\\n\\u003coperation\\u003e01\\u003c\\\\/operation\\u003e\\u003cresultCode\\u003e1\\u003c\\\\/resultCode\\u003e\\u003cmerNo\\u003e10352\\u003c\\\\/merNo\\u003e\\u003cbillNo\\u003e1124686\\u003c\\\\/billNo\\u003e\\u003ccurrency\\u003eUSD\\u003c\\\\/currency\\u003e\\u003camount\\u003e11.00\\u003c\\\\/amount\\u003e\\u003cdateTime\\u003e20130818030612\\u003c\\\\/dateTime\\u003e\\u003cpaymentOrderNo\\u003e103526640862\\u003c\\\\/paymentOrderNo\\u003e\\u003cremark\\u003ePayment Declined(To many attempts using this card within 24 hours. Please try again later or deposit using an alternative card.Thank you)\\u003c\\\\/remark\\u003e\\u003cmd5Info\\u003e4A78C4F84A905C34D775D41D48899ABF\\u003c\\\\/md5Info\\u003e\\u003cbillingDescriptor\\u003e\\u003c\\\\/billingDescriptor\\u003e\\\\n\\\\n\\u003c\\\\/response\\u003e\\&quot;,\\&quot;results\\&quot;:null,\\&quot;pass_through\\&quot;:null}&quot;\" logId=\"11231\"/><transactionLog created=\"1376766398709\" type=\"DESTINATION_RESPONSE\" log=\"&quot;\\u003c?xml version\\u003d\\&quot;1.0\\&quot; encoding\\u003d\\&quot;UTF-8\\&quot;?\\u003e\\n\\u003cresponse\\u003e\\n\\n\\n\\u003coperation\\u003e01\\u003c/operation\\u003e\\u003cresultCode\\u003e1\\u003c/resultCode\\u003e\\u003cmerNo\\u003e10352\\u003c/merNo\\u003e\\u003cbillNo\\u003e1124686\\u003c/billNo\\u003e\\u003ccurrency\\u003eUSD\\u003c/currency\\u003e\\u003camount\\u003e11.00\\u003c/amount\\u003e\\u003cdateTime\\u003e20130818030612\\u003c/dateTime\\u003e\\u003cpaymentOrderNo\\u003e103526640862\\u003c/paymentOrderNo\\u003e\\u003cremark\\u003ePayment Declined(To many attempts using this card within 24 hours. Please try again later or deposit using an alternative card.Thank you)\\u003c/remark\\u003e\\u003cmd5Info\\u003e4A78C4F84A905C34D775D41D48899ABF\\u003c/md5Info\\u003e\\u003cbillingDescriptor\\u003e\\u003c/billingDescriptor\\u003e\\n\\n\\u003c/response\\u003e&quot;\" logId=\"11232\"/><transactionLog created=\"1376766398716\" type=\"GATE_RESPONSE\" log=\"{&quot;txId&quot;:&quot;00001G6&quot;,&quot;mcTxId&quot;:&quot;108767&quot;,&quot;code&quot;:&quot;800.900.100&quot;,&quot;message&quot;:&quot;\\u003c?xml version\\u003d\\&quot;1.0\\&quot; encoding\\u003d\\&quot;UTF-8\\&quot;?\\u003e\\n\\u003cresponse\\u003e\\n\\n\\n\\u003coperation\\u003e01\\u003c/operation\\u003e\\u003cresultCode\\u003e1\\u003c/resultCode\\u003e\\u003cmerNo\\u003e10352\\u003c/merNo\\u003e\\u003cbillNo\\u003e1124686\\u003c/billNo\\u003e\\u003ccurrency\\u003eUSD\\u003c/currency\\u003e\\u003camount\\u003e11.00\\u003c/amount\\u003e\\u003cdateTime\\u003e20130818030612\\u003c/dateTime\\u003e\\u003cpaymentOrderNo\\u003e103526640862\\u003c/paymentOrderNo\\u003e\\u003cremark\\u003ePayment Declined(To many attempts using this card within 24 hours. Please try again later or deposit using an alternative card.Thank you)\\u003c/remark\\u003e\\u003cmd5Info\\u003e4A78C4F84A905C34D775D41D48899ABF\\u003c/md5Info\\u003e\\u003cbillingDescriptor\\u003e\\u003c/billingDescriptor\\u003e\\n\\n\\u003c/response\\u003e&quot;,&quot;startDate&quot;:&quot;2013-08-17T19:06:35+0000&quot;,&quot;startStamp&quot;:1376766395264,&quot;endDate&quot;:&quot;2013-08-17T19:06:38+0000&quot;,&quot;endStamp&quot;:1376766398711,&quot;deltaMsec&quot;:3447}\" logId=\"11233\"/><transactionLog created=\"1376813019485\" type=\"GATE_RESPONSE\" log=\"{&quot;processResults&quot;:[{&quot;txId&quot;:&quot;00001G6&quot;,&quot;code&quot;:&quot;100.400.304&quot;,&quot;message&quot;:&quot;Attempting to refund declined transaction&quot;}],&quot;code&quot;:&quot;100.400.304&quot;,&quot;version&quot;:&quot;1.0.2-202-2013-08-09_18-16-04&quot;}\" logId=\"11254\"/></searchResults></transactions_response>";

            XStream responseXstream = new XStream();
            responseXstream.alias("transactions_response",transactions_response.class);
            response = (transactions_response)xstream.fromXML(responseXML);

        }
        catch (HttpException e)
        {
           PZExceptionHandler.raiseTechnicalViolationException(EuroPayPaymentGateway.class.getName(),"processQueryRequest()",null,"common","Http Exception while Querying transaction",PZTechnicalExceptionEnum.HTTP_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(EuroPayPaymentGateway.class.getName(), "processQueryRequest()", null, "common", "IO Exception while Querying transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }

        log.debug(" code " + response.getCode());
        log.debug("version " + response.getVersion());

        transactionLogger.debug(" code " + response.getCode());
        transactionLogger.debug("version " + response.getVersion());


        SearchResults sr = response.getSearchResults();
        log.debug(" search " + sr);
        transactionLogger.debug(" search " + sr);

//        List<TransactionLog> tl = sr.getTransactionLog();
//
//        String msg;
//
//        for(TransactionLog tlogs: tl)
//        {
//            String type = tlogs.getType();
//            if(type.equalsIgnoreCase("CONNECTOR_RESPONSE"))
//            {
//                msg = tlogs.log;
//                log.debug(" Msg : " + msg.toString());
//            }
//            //log.debug("Tlogs " + tlogs.);
//        }
        //log.debug(response.getProcessResults().getCode());
//        log.debug(response.getProcessResults().getCode());


        return response;
    }

//    public static void main(String[] args)
//    {
//        String one = "12";
//        String two = "21";
//        Float startTime = Float.parseFloat(one);
//        Float endTime = Float.parseFloat(two);
//        Float responseTime = endTime - startTime;
//        log.debug(responseTime);
//
//
//    }
}