package com.payment.zotapay;

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
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;

import java.net.URLDecoder;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 7/9/2018.
 */
public class ZotapayPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ZotapayPaymentGateway.class.getName());

    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.zotapay");

    public static final String GATEWAY_TYPE = "zotapay";

    public ZotapayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static void main(String[] args)
    {
        try{

            String test_url = "https://sandbox.zotapay.com/paynet/api/v2/sale/4043";
            String merchant_control = ZotaPayUtils.getControl("4043" + "54568" + "1000" + "rihen.dedhia@pz.com" + "C6B5F75C-8A47-415D-BC58-E606B9CE1838");
            System.out.println("merchant control-----"+merchant_control);
            String request =
                    "client_orderid=54568\n" +
                    "&order_desc=Test Order Description\n" +
                    "&first_name=Rihen\n" +
                    "&last_name=Dedhia\n" +
                    "&address1=Malad\n" +
                    "&city=Mumbai\n" +
                    "&state=MH\n" +
                    "&zip_code=400065\n" +
                    "&country=IN\n" +
                    "&phone=%2B12063582043\n" +
                    "&cell_phone=%2B19023384543\n" +
                    "&amount=10.00\n" +
                    "&email=rihen.dedhia@pz.com\n" +
                    "&currency=USD\n" +
                    "&ipaddress=115.96.19.10\n" +
                    "&credit_card_number=4222222489425\n" +
                    "&card_printed_name=RIHEN DEDHIA\n" +
                    "&expire_month=12\n" +
                    "&expire_year=2099\n" +
                    "&cvv2=321\n" +
                    //"&redirect_url=http://localhost:8081/transaction/PVFrontEndServlet\n" +
                    "&server_callback_url=https://staging.pz.com/transaction/ZotapayFrontEndServlet\n" +
                    "&control="+merchant_control+"";

            System.out.println("Request----"+request);
            String response=ZotaPayUtils.doPostHTTPSURLConnectionClient(test_url,request);
            System.out.println("Response-----"+response);


/*            String status_test_url = "https://sandbox.zotapay.com/paynet/api/v2/status/4190";
            String status_control = ZotaPayUtils.getControl("70Trades"+"56551"+"961379"+"C6B5F75C-8A47-415D-BC58-E606B9CE1838");
            String status_request = "login=70Trades\n" +
                    "&client_orderid=56551\n" +
                    "&orderid=961379\n" +
                    "&by-request-sn=00000000-0000-0000-0000-0000016ca835\n" +
                    "&control="+status_control+"";
            System.out.println("status_request-----"+status_request);
            String status_response=ZotaPayUtils.doPostHTTPSURLConnectionClient(status_test_url,status_request);
            System.out.println("status-----"+status_response);*/

        }
        catch (Exception e){
            transactionLogger.debug("error----------------"+e);
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
        transactionLogger.error("-----inside processSale-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO= commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        boolean isTest = gatewayAccount.isTest();

        String endPoint = ZotaPayUtils.EndPointCurrency(commTransactionDetailsVO.getCurrency(), is3dSupported,isTest);
        String controlAmount = ZotaPayUtils.getAmount(commTransactionDetailsVO.getAmount());
        String email = commAddressDetailsVO.getEmail();
        String controlKey = gatewayAccount.getFRAUD_FTP_PATH();

        String orderDesc = commTransactionDetailsVO.getOrderId();
        if (orderDesc.equals(trackingID))
        {
            orderDesc = commTransactionDetailsVO.getMerchantOrderId();
        }
        if (!functions.isValueNull(orderDesc))
        {
            orderDesc = commTransactionDetailsVO.getOrderDesc();
        }

        String callbackUrl="";

        transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
        String termUrl = "";

        if("Y".equalsIgnoreCase(is3dSupported)){
            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
                transactionLogger.error("from host url----"+termUrl);
            }
            else
            {
                termUrl = RB.getString("REDIRECT_URL");
                transactionLogger.error("from RB----"+termUrl);
            }
        }else {
            termUrl="https://www.google.com/";
        }
        callbackUrl=RB.getString("CALL_BACK_URL");
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=commAddressDetailsVO.getTmpl_currency();
        }

        try
        {
            String control = ZotaPayUtils.getControl(endPoint.trim() + trackingID.trim() + controlAmount.trim() + email.trim() + controlKey.trim());
            String saleRequest =
                    "client_orderid=" + trackingID + "\n" +
                    "&order_desc=" + orderDesc + "\n" +
                    "&first_name=" + commAddressDetailsVO.getFirstname() + "\n" +
                    "&last_name=" + commAddressDetailsVO.getLastname() + "\n" +
                    "&address1=" + commAddressDetailsVO.getStreet() + "\n" +
                    "&city=" + commAddressDetailsVO.getCity() + "\n" +
                    "&state=" + commAddressDetailsVO.getState() + "\n" +
                    "&zip_code=" + commAddressDetailsVO.getZipCode() + "\n" +
                    "&country=" + commAddressDetailsVO.getCountry() + "\n" +
                    "&phone=" + commAddressDetailsVO.getPhone() + "\n" +
                    "&amount=" + commTransactionDetailsVO.getAmount() + "\n" +
                    "&email=" + commAddressDetailsVO.getEmail() + "\n" +
                    "&currency=" + commTransactionDetailsVO.getCurrency() + "\n" +
                    "&ipaddress=" + commAddressDetailsVO.getCardHolderIpAddress() + "\n" +
                    "&credit_card_number=" + commCardDetailsVO.getCardNum() + "\n" +
                    "&card_printed_name=" + commAddressDetailsVO.getFirstname() +" "+commAddressDetailsVO.getLastname()+ "\n" +
                    "&expire_month=" + commCardDetailsVO.getExpMonth() + "\n" +
                    "&expire_year=" + commCardDetailsVO.getExpYear() + "\n" +
                    "&cvv2=" + commCardDetailsVO.getcVV() + "\n" +
                    "&redirect_url="+termUrl+trackingID+"\n" +
                    "&server_callback_url=" + callbackUrl + "\n" +
                    "&control=" + control + "";
  String saleRequestlog =
                    "client_orderid=" + trackingID + "\n" +
                    "&order_desc=" + orderDesc + "\n" +
                    "&first_name=" + commAddressDetailsVO.getFirstname() + "\n" +
                    "&last_name=" + commAddressDetailsVO.getLastname() + "\n" +
                    "&address1=" + commAddressDetailsVO.getStreet() + "\n" +
                    "&city=" + commAddressDetailsVO.getCity() + "\n" +
                    "&state=" + commAddressDetailsVO.getState() + "\n" +
                    "&zip_code=" + commAddressDetailsVO.getZipCode() + "\n" +
                    "&country=" + commAddressDetailsVO.getCountry() + "\n" +
                    "&phone=" + commAddressDetailsVO.getPhone() + "\n" +
                    "&amount=" + commTransactionDetailsVO.getAmount() + "\n" +
                    "&email=" + commAddressDetailsVO.getEmail() + "\n" +
                    "&currency=" + commTransactionDetailsVO.getCurrency() + "\n" +
                    "&ipaddress=" + commAddressDetailsVO.getCardHolderIpAddress() + "\n" +
                    "&credit_card_number=" + functions.maskingPan(commCardDetailsVO.getCardNum()) + "\n" +
                    "&card_printed_name=" + commAddressDetailsVO.getFirstname() +" "+commAddressDetailsVO.getLastname()+ "\n" +
                            "&expire_month=" + functions.maskingNumber(commCardDetailsVO.getExpMonth()) + "\n" +
                            "&expire_year=" + functions.maskingNumber(commCardDetailsVO.getExpYear()) + "\n" +
                            "&cvv2=" + functions.maskingNumber(commCardDetailsVO.getcVV()) + "\n" +
                    "&redirect_url="+termUrl+trackingID+"\n" +
                    "&server_callback_url=" + callbackUrl + "\n" +
                    "&control=" + control + "";


            transactionLogger.error("sale request----" + saleRequestlog);

            String saleResponse = "";

            if (isTest)
            {
                saleResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("SALE_TEST_URL") + endPoint, saleRequest);
            }
            else
            {
                saleResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("SALE_LIVE_URL") + endPoint, saleRequest);
            }

            transactionLogger.error("saleResponse------" + saleResponse);
            if (functions.isValueNull(saleResponse))
            {
                Map<String, String> responseMap = ZotaPayUtils.getQueryMap(saleResponse);
                if (responseMap != null)
                {
                    String srn = responseMap.get("serial-number");
                    String client_id = responseMap.get("merchant-order-id");
                    String transactionId="";
                    if(functions.isValueNull(responseMap.get("paynet-order-id")))
                        transactionId = responseMap.get("paynet-order-id");
                    String input = gatewayAccount.getMerchantId().trim() + client_id.trim() + transactionId.trim() + controlKey.trim();
                    String status_control = ZotaPayUtils.getControl(input.trim());

                    String orderRequest =
                            "login=" + gatewayAccount.getMerchantId() + "\n" +
                            "&client_orderid=" + client_id + "\n" +
                            "&orderid=" + transactionId + "\n" +
                            "&by-request-sn=" + srn + "\n" +
                            "&control=" + status_control + "";

                    transactionLogger.error("order status request----" + orderRequest);

                    Thread.sleep(5000);
                    String saleStatusResponse = "";

                    if (isTest)
                    {
                        saleStatusResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_ORDER_STATUS_URL") + endPoint, orderRequest);
                    }
                    else
                    {
                        saleStatusResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_ORDER_STATUS_URL") + endPoint, orderRequest);
                    }

                    transactionLogger.error("saleStatusResponse-----" + saleStatusResponse);

                    if (functions.isValueNull(saleStatusResponse))
                    {
                        Map<String, String> saleStatusResponseMap = ZotaPayUtils.getQueryMap(saleStatusResponse);
                        if (saleStatusResponseMap != null)
                        {
                            String html = "";
                            String sale_approval_code = "";
                            String sale_processor_debit_arn = "";
                            String sale_transactionId = saleStatusResponseMap.get("paynet-order-id");
                            String sale_status = saleStatusResponseMap.get("status");
                            String sale_transaction_type = saleStatusResponseMap.get("transaction-type");
                            String sale_processing_date = saleStatusResponseMap.get("paynet-processing-date");

                            transactionLogger.debug("sale status -----------" + sale_status);

                            if (functions.isValueNull(saleStatusResponseMap.get("html")))
                            {
                                html = saleStatusResponseMap.get("html");
                            }
                            if (functions.isValueNull(saleStatusResponseMap.get("approval-code")))
                            {
                                sale_approval_code = saleStatusResponseMap.get("approval-code");
                            }
                            if (functions.isValueNull(saleStatusResponseMap.get("processor-debit-arn")))
                            {
                                sale_processor_debit_arn = saleStatusResponseMap.get("processor-debit-arn");
                            }

                            if ("processing".equals(sale_status) && functions.isValueNull(html))
                            {
                                transactionLogger.debug("inside----3dConfimation-----");
                                commResponseVO.setStatus("pending3DConfirmation");
                                commResponseVO.setUrlFor3DRedirect(URLDecoder.decode(html));
                                commResponseVO.setRemark(sale_status);
                                commResponseVO.setDescription("Pending 3D Authenication");

                            }
                            else if ("approved".equalsIgnoreCase(sale_status))
                            {
                                transactionLogger.debug("in approved-------" + sale_status);
                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark("Transaction Successful");
                                commResponseVO.setDescription(sale_status);
                                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                            }
                            else
                            {
                                transactionLogger.debug("in fail-------" + sale_status);
                                commResponseVO.setStatus("fail");
                                if(functions.isValueNull(sale_status)){
                                    commResponseVO.setRemark(sale_status);
                                    commResponseVO.setDescription(sale_status);
                                }else {
                                    commResponseVO.setRemark("Transaction Failed");
                                    commResponseVO.setDescription("Transaction Failed");
                                }

                            }
                            commResponseVO.setBankTransactionDate(URLDecoder.decode(sale_processing_date));
                            commResponseVO.setErrorCode(sale_approval_code);
                            commResponseVO.setArn(sale_processor_debit_arn);
                            commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                            commResponseVO.setTransactionId(sale_transactionId);
                            commResponseVO.setResponseHashInfo(srn+"@"+endPoint);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setTmpl_Amount(tmpl_amount);
                            commResponseVO.setTmpl_Currency(tmpl_currency);

                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
           transactionLogger.error("Exception-----", e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processAuthentication-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        boolean isTest = gatewayAccount.isTest();

        String endPoint = ZotaPayUtils.EndPointCurrency(commTransactionDetailsVO.getCurrency(), is3dSupported ,isTest);
        String controlAmount = ZotaPayUtils.getAmount(commTransactionDetailsVO.getAmount());
        String email = commAddressDetailsVO.getEmail();
        String controlKey = gatewayAccount.getFRAUD_FTP_PATH();


        String orderDesc = commTransactionDetailsVO.getOrderId();
        if (orderDesc.equals(trackingID))
        {
            orderDesc = commTransactionDetailsVO.getMerchantOrderId();
        }
        if (!functions.isValueNull(orderDesc))
        {
            orderDesc = commTransactionDetailsVO.getOrderDesc();
        }

        String callbackUrl="";
        transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
        String termUrl = "";

        if("Y".equalsIgnoreCase(is3dSupported)){
            if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
                transactionLogger.error("from host url----"+termUrl);
            }
            else
            {
                termUrl = RB.getString("REDIRECT_URL");
                transactionLogger.error("from RB----"+termUrl);
            }
        }else {
            termUrl="https://www.google.com/";
        }
        callbackUrl=RB.getString("CALL_BACK_URL");
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=commAddressDetailsVO.getTmpl_currency();
        }

        try
        {
            String inp = endPoint.trim() + trackingID.trim() + controlAmount.trim() + email.trim() + controlKey.trim();
            String control = ZotaPayUtils.getControl(inp.trim());
            String authRequest =
                    "client_orderid=" + trackingID + "\n" +
                    "&credit_card_number=" + commCardDetailsVO.getCardNum() + "\n" +
                    "&expire_year=" + commCardDetailsVO.getExpYear() + "\n" +
                    "&expire_month=" + commCardDetailsVO.getExpMonth() + "\n" +
                    "&cvv2=" + commCardDetailsVO.getcVV() + "\n" +
                    "&amount=" + commTransactionDetailsVO.getAmount() + "\n" +
                    "&card_printed_name="+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+ "\n" +
                    "&ipaddress=" + commAddressDetailsVO.getCardHolderIpAddress() + "\n" +
                    "&state=" + commAddressDetailsVO.getState() + "\n" +
                    "&currency=" + commTransactionDetailsVO.getCurrency() + "\n" +
                    "&phone=" + commAddressDetailsVO.getPhone() + "\n" +
                    "&zip_code=" + commAddressDetailsVO.getZipCode() + "\n" +
                    "&order_desc=" + orderDesc + "\n" +
                    "&email=" + email + "\n" +
                    "&country=" + commAddressDetailsVO.getCountry() + "\n" +
                    "&city=" + commAddressDetailsVO.getCity() + "\n" +
                    "&address1=" + commAddressDetailsVO.getStreet() + "\n" +
                    "&redirect_url="+termUrl+trackingID+"\n" +
                    "&server_callback_url=" + callbackUrl + "\n" +
                    "&control=" + control + "";
  String authRequestlog =
                    "client_orderid=" + trackingID + "\n" +
                    "&credit_card_number=" + functions.maskingPan(commCardDetailsVO.getCardNum()) + "\n" +
                            "&expire_month=" + functions.maskingNumber(commCardDetailsVO.getExpMonth()) + "\n" +
                            "&expire_year=" + functions.maskingNumber(commCardDetailsVO.getExpYear()) + "\n" +
                            "&cvv2=" + functions.maskingNumber(commCardDetailsVO.getcVV()) + "\n" +
                    "&amount=" + commTransactionDetailsVO.getAmount() + "\n" +
                    "&card_printed_name="+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+ "\n" +
                    "&ipaddress=" + commAddressDetailsVO.getCardHolderIpAddress() + "\n" +
                    "&state=" + commAddressDetailsVO.getState() + "\n" +
                    "&currency=" + commTransactionDetailsVO.getCurrency() + "\n" +
                    "&phone=" + commAddressDetailsVO.getPhone() + "\n" +
                    "&zip_code=" + commAddressDetailsVO.getZipCode() + "\n" +
                    "&order_desc=" + orderDesc + "\n" +
                    "&email=" + email + "\n" +
                    "&country=" + commAddressDetailsVO.getCountry() + "\n" +
                    "&city=" + commAddressDetailsVO.getCity() + "\n" +
                    "&address1=" + commAddressDetailsVO.getStreet() + "\n" +
                    "&redirect_url="+termUrl+trackingID+"\n" +
                    "&server_callback_url=" + callbackUrl + "\n" +
                    "&control=" + control + "";

            transactionLogger.error("preauth request----" + authRequestlog);

            String authResponse = "";

            if (isTest)
            {
                authResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("AUTH_TEST_URL") + endPoint, authRequest);
            }
            else
            {
                authResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("AUTH_LIVE_URL") + endPoint, authRequest);
            }

            transactionLogger.error("authResponse------" + authResponse);
            if (functions.isValueNull(authResponse))
            {
                Map<String, String> responseMap = ZotaPayUtils.getQueryMap(authResponse);
                if (responseMap != null)
                {
                    String srn = responseMap.get("serial-number");
                    String client_id = responseMap.get("merchant-order-id");
                    String transactionId = responseMap.get("paynet-order-id");
                    String input = gatewayAccount.getMerchantId().trim() + client_id.trim() + transactionId.trim() + controlKey.trim();
                    String status_control = ZotaPayUtils.getControl(input.trim());

                    String orderRequest =
                            "login=" + gatewayAccount.getMerchantId() + "\n" +
                            "&client_orderid=" + client_id + "\n" +
                            "&orderid=" + transactionId + "\n" +
                            "&by-request-sn=" + srn + "\n" +
                            "&control=" + status_control + "";

                    transactionLogger.error("order status request----" + orderRequest);

                    Thread.sleep(5000);
                    String authStatusResponse = "";

                    if (isTest)
                    {
                        authStatusResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_ORDER_STATUS_URL") + endPoint, orderRequest);
                    }
                    else
                    {
                        authStatusResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_ORDER_STATUS_URL") + endPoint, orderRequest);
                    }

                    transactionLogger.error("authStatusResponse-----" + authStatusResponse);

                    if (functions.isValueNull(authStatusResponse))
                    {
                        Map<String, String> saleStatusResponseMap = ZotaPayUtils.getQueryMap(authStatusResponse);
                        if (saleStatusResponseMap != null)
                        {
                            String html = "";
                            String auth_approval_code = "";
                            String auth_processor_debit_arn = "";
                            String auth_transactionId = saleStatusResponseMap.get("paynet-order-id");
                            String auth_status = saleStatusResponseMap.get("status");
                            String auth_transaction_type = saleStatusResponseMap.get("transaction-type");
                            String auth_processing_date = saleStatusResponseMap.get("paynet-processing-date");

                            transactionLogger.debug("auth_status-----------" + auth_status);

                            if (functions.isValueNull(saleStatusResponseMap.get("html")))
                            {
                                html = saleStatusResponseMap.get("html");
                            }
                            if (functions.isValueNull(saleStatusResponseMap.get("approval-code")))
                            {
                                auth_approval_code = saleStatusResponseMap.get("approval-code");
                            }
                            if (functions.isValueNull(saleStatusResponseMap.get("processor-debit-arn")))
                            {
                                auth_processor_debit_arn = saleStatusResponseMap.get("processor-debit-arn");
                            }

                            if ("processing".equals(auth_status) && functions.isValueNull(html))
                            {
                                transactionLogger.debug("inside----3dConfimation-----");
                                commResponseVO.setStatus("pending3DConfirmation");
                                commResponseVO.setUrlFor3DRedirect(URLDecoder.decode(html));
                                commResponseVO.setRemark(auth_status);
                                commResponseVO.setDescription("Pending 3D Authenication");

                            }
                            else if ("approved".equalsIgnoreCase(auth_status))
                            {
                                transactionLogger.debug("in approved-------" + auth_status);
                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark("Transaction Successful");
                                commResponseVO.setDescription(auth_status);
                                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                            }
                            else
                            {
                                transactionLogger.debug("in fail-------" + auth_status);
                                commResponseVO.setStatus("fail");
                                if(functions.isValueNull(auth_status)){
                                    commResponseVO.setRemark(auth_status);
                                    commResponseVO.setDescription(auth_status);
                                }else {
                                    commResponseVO.setRemark("Transaction Failed");
                                    commResponseVO.setDescription("Transaction Failed");
                                }
                            }
                            commResponseVO.setBankTransactionDate(URLDecoder.decode(auth_processing_date));
                            commResponseVO.setErrorCode(auth_approval_code);
                            commResponseVO.setArn(auth_processor_debit_arn);
                            commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                            commResponseVO.setTransactionId(auth_transactionId);
                            commResponseVO.setResponseHashInfo(srn+"@"+endPoint);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setTmpl_Amount(tmpl_amount);
                            commResponseVO.setTmpl_Currency(tmpl_currency);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
           transactionLogger.error("Exception-----", e);
        }

        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processCapture-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();

        String controlKey = gatewayAccount.getFRAUD_FTP_PATH();

        String endPoint ="";
        if(functions.isValueNull(commTransactionDetailsVO.getResponseHashInfo())){
            String data[] = commTransactionDetailsVO.getResponseHashInfo().split("@");
            endPoint=data[1];
        }
        transactionLogger.debug("endpoint-----"+endPoint);
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=commAddressDetailsVO.getTmpl_currency();
        }

        try
        {
            String inp = gatewayAccount.getMerchantId().trim() + trackingID.trim() + commTransactionDetailsVO.getPreviousTransactionId().trim() + controlKey.trim();
            String control = ZotaPayUtils.getControl(inp.trim());

            String captureRequest =
                    "login=" + gatewayAccount.getMerchantId() + "\n" +
                    "&client_orderid=" + trackingID + "\n" +
                    "&orderid=" + commTransactionDetailsVO.getPreviousTransactionId() + "\n" +
                    "&control=" + control + "";

            transactionLogger.error("captureRequest----" + captureRequest);

            String captureResponse = "";

            if (isTest)
            {
                captureResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("CAPTURE_TEST_URL") + endPoint, captureRequest);
            }
            else
            {
                captureResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("CAPTURE_LIVE_URL") + endPoint, captureRequest);
            }

            transactionLogger.error("captureResponse------" + captureResponse);
            if (functions.isValueNull(captureResponse))
            {
                Map<String, String> responseMap = ZotaPayUtils.getQueryMap(captureResponse);
                if (responseMap != null)
                {
                    String srn = responseMap.get("serial-number");
                    String client_id = responseMap.get("merchant-order-id");
                    String transactionId = responseMap.get("paynet-order-id");
                    String input = gatewayAccount.getMerchantId().trim() + client_id.trim() + transactionId.trim() + controlKey.trim();
                    String status_control = ZotaPayUtils.getControl(input.trim());

                    String orderRequest =
                            "login=" + gatewayAccount.getMerchantId() + "\n" +
                            "&client_orderid=" + client_id + "\n" +
                            "&orderid=" + transactionId + "\n" +
                            "&by-request-sn=" + srn + "\n" +
                            "&control=" + status_control + "";

                    transactionLogger.error("order status request----" + orderRequest);

                    Thread.sleep(5000);
                    String captureStatusResponse = "";

                    if (isTest)
                    {
                        captureStatusResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_ORDER_STATUS_URL") + endPoint, orderRequest);
                    }
                    else
                    {
                        captureStatusResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_ORDER_STATUS_URL") + endPoint, orderRequest);
                    }

                    transactionLogger.error("captureStatusResponse-----" + captureStatusResponse);

                    if (functions.isValueNull(captureStatusResponse))
                    {
                        Map<String, String> saleStatusResponseMap = ZotaPayUtils.getQueryMap(captureStatusResponse);
                        if (saleStatusResponseMap != null)
                        {
                            String capture_approval_code = "";
                            String capture_processor_debit_arn = "";
                            String capture_transactionId = saleStatusResponseMap.get("paynet-order-id");
                            String capture_status = saleStatusResponseMap.get("status");
                            String capture_transaction_type = saleStatusResponseMap.get("transaction-type");
                            String capture_processing_date = saleStatusResponseMap.get("paynet-processing-date");

                            transactionLogger.debug("capture_status-----------" + capture_status);

                            if (functions.isValueNull(saleStatusResponseMap.get("approval-code")))
                            {
                                capture_approval_code = saleStatusResponseMap.get("approval-code");
                            }
                            if (functions.isValueNull(saleStatusResponseMap.get("processor-debit-arn")))
                            {
                                capture_processor_debit_arn = saleStatusResponseMap.get("processor-debit-arn");
                            }

                            if ("processing".equals(capture_status))
                            {
                                transactionLogger.debug("in processing-----");
                                commResponseVO.setStatus("Pending");
                                commResponseVO.setRemark(capture_status);
                                commResponseVO.setDescription(capture_status);

                            }
                            else if ("approved".equalsIgnoreCase(capture_status))
                            {
                                transactionLogger.debug("in approved-------" + capture_status);
                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark("Transaction Successful");
                                commResponseVO.setDescription(capture_status);
                                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                            }
                            else
                            {
                                transactionLogger.debug("in fail-------" + capture_status);
                                commResponseVO.setStatus("fail");
                                if(functions.isValueNull(capture_status)){
                                    commResponseVO.setRemark(capture_status);
                                    commResponseVO.setDescription(capture_status);
                                }else {
                                    commResponseVO.setRemark("Transaction Failed");
                                    commResponseVO.setDescription("Transaction Failed");
                                }
                            }
                            commResponseVO.setBankTransactionDate(URLDecoder.decode(capture_processing_date));
                            commResponseVO.setErrorCode(capture_approval_code);
                            commResponseVO.setArn(capture_processor_debit_arn);
                            commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
                            commResponseVO.setTransactionId(capture_transactionId);
                            commResponseVO.setResponseHashInfo(srn+"@"+endPoint);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setTmpl_Amount(tmpl_amount);
                            commResponseVO.setTmpl_Currency(tmpl_currency);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
           transactionLogger.error("Exception-----", e);
        }

        return commResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processVoid-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();

        Functions functions = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        boolean isTest = gatewayAccount.isTest();

        String endPoint ="";
        if(functions.isValueNull(commTransactionDetailsVO.getResponseHashInfo())){
            String data[] = commTransactionDetailsVO.getResponseHashInfo().split("@");
            endPoint=data[1];
        }
        transactionLogger.debug("endpoint-----"+endPoint);
        String controlKey = gatewayAccount.getFRAUD_FTP_PATH();
        String currency="";
        String tmpl_amount="";
        String tmpl_currency="";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=commAddressDetailsVO.getTmpl_currency();
        }


        try
        {
            transactionLogger.debug("Order desc-------------" + commTransactionDetailsVO.getOrderDesc());
            String inp = gatewayAccount.getMerchantId().trim() + trackingID.trim() + commTransactionDetailsVO.getPreviousTransactionId().trim() + controlKey.trim();
            String control = ZotaPayUtils.getControl(inp.trim());

            String voidRequest =
                    "login=" + gatewayAccount.getMerchantId() + "\n" +
                    "&client_orderid=" + trackingID + "\n" +
                    "&orderid=" + commTransactionDetailsVO.getPreviousTransactionId() + "\n" +
                    "&comment=" + commTransactionDetailsVO.getOrderDesc() + "\n" +
                    "&control=" + control + "";

            transactionLogger.error("voidRequest----" + voidRequest);

            String voidResponse = "";

            if (isTest)
            {
                voidResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("VOID_TEST_URL") + endPoint, voidRequest);
            }
            else
            {
                voidResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("VOID_LIVE_URL") + endPoint, voidRequest);
            }

            transactionLogger.error("voidResponse------" + voidResponse);
            if (functions.isValueNull(voidResponse))
            {
                Map<String, String> responseMap = ZotaPayUtils.getQueryMap(voidResponse);
                if (responseMap != null)
                {
                    String srn = responseMap.get("serial-number");
                    String client_id = responseMap.get("merchant-order-id");
                    String transactionId = responseMap.get("paynet-order-id");
                    String input = gatewayAccount.getMerchantId().trim() + client_id.trim() + transactionId.trim() + controlKey.trim();
                    String status_control = ZotaPayUtils.getControl(input.trim());

                    String orderRequest =
                            "login=" + gatewayAccount.getMerchantId() + "\n" +
                            "&client_orderid=" + client_id + "\n" +
                            "&orderid=" + transactionId + "\n" +
                            "&by-request-sn=" + srn + "\n" +
                            "&control=" + status_control + "";

                    transactionLogger.error("order status request----" + orderRequest);

                    Thread.sleep(5000);
                    String voidStatusResponse = "";

                    if (isTest)
                    {
                        voidStatusResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_ORDER_STATUS_URL") + endPoint, orderRequest);
                    }
                    else
                    {
                        voidStatusResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_ORDER_STATUS_URL") + endPoint, orderRequest);
                    }

                    transactionLogger.error("voidStatusResponse-----" + voidStatusResponse);

                    if (functions.isValueNull(voidStatusResponse))
                    {
                        Map<String, String> saleStatusResponseMap = ZotaPayUtils.getQueryMap(voidStatusResponse);
                        if (saleStatusResponseMap != null)
                        {
                            String void_approval_code = "";
                            String void_processor_debit_arn = "";
                            String void_transactionId = saleStatusResponseMap.get("paynet-order-id");
                            String void_status = saleStatusResponseMap.get("status");
                            String void_transaction_type = saleStatusResponseMap.get("transaction-type");
                            String void_processing_date = saleStatusResponseMap.get("paynet-processing-date");

                            transactionLogger.debug("void_status-----------" + void_status);

                            if (functions.isValueNull(saleStatusResponseMap.get("approval-code")))
                            {
                                void_approval_code = saleStatusResponseMap.get("approval-code");
                            }
                            if (functions.isValueNull(saleStatusResponseMap.get("processor-debit-arn")))
                            {
                                void_processor_debit_arn = saleStatusResponseMap.get("processor-debit-arn");
                            }

                            if ("processing".equals(void_status))
                            {
                                transactionLogger.debug("in processing-----");
                                commResponseVO.setStatus("Pending");
                                commResponseVO.setRemark(void_status);
                                commResponseVO.setDescription(void_status);

                            }
                            else if ("approved".equalsIgnoreCase(void_status))
                            {
                                transactionLogger.debug("in approved-------" + void_status);
                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark("Transaction Successful");
                                commResponseVO.setDescription(void_status);
                                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                            }
                            else
                            {
                                transactionLogger.debug("in fail-------" + void_status);
                                commResponseVO.setStatus("fail");
                                if(functions.isValueNull(void_status)){
                                    commResponseVO.setRemark(void_status);
                                    commResponseVO.setDescription(void_status);
                                }else {
                                    commResponseVO.setRemark("Transaction Failed");
                                    commResponseVO.setDescription("Transaction Failed");
                                }
                            }
                            commResponseVO.setBankTransactionDate(URLDecoder.decode(void_processing_date));
                            commResponseVO.setErrorCode(void_approval_code);
                            commResponseVO.setArn(void_processor_debit_arn);
                            commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
                            commResponseVO.setTransactionId(void_transactionId);
                            commResponseVO.setResponseHashInfo(srn+"@"+endPoint);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setTmpl_Amount(tmpl_amount);
                            commResponseVO.setTmpl_Currency(tmpl_currency);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
           transactionLogger.error("Exception-----", e);
        }

        return commResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processRefund-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();

        Functions functions = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        boolean isTest = gatewayAccount.isTest();

        String endPoint ="";
        if(functions.isValueNull(commTransactionDetailsVO.getResponseHashInfo())){
            String data[] = commTransactionDetailsVO.getResponseHashInfo().split("@");
            endPoint=data[1];
        }
        transactionLogger.debug("endpoint-----"+endPoint);
        String controlKey = gatewayAccount.getFRAUD_FTP_PATH();
        String controlAmount = ZotaPayUtils.getAmount(commTransactionDetailsVO.getAmount());
        String currency="";

        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }

        try
        {
            transactionLogger.debug("Order desc-------------" + commTransactionDetailsVO.getOrderDesc());
            String inp = gatewayAccount.getMerchantId().trim() + trackingID.trim() + commTransactionDetailsVO.getPreviousTransactionId().trim()+ controlAmount.trim() + commTransactionDetailsVO.getCurrency().trim() + controlKey.trim();
            String control = ZotaPayUtils.getControl(inp.trim());

            String refundRequest =
                    "login=" + gatewayAccount.getMerchantId() + "\n" +
                    "&client_orderid=" + trackingID + "\n" +
                    "&orderid=" + commTransactionDetailsVO.getPreviousTransactionId() + "\n" +
                    "&amount=" + commTransactionDetailsVO.getAmount() + "\n" +
                    "&currency=" + commTransactionDetailsVO.getCurrency() + "\n" +
                    "&control=" + control + "\n" +
                    "&comment="+ commTransactionDetailsVO.getOrderDesc()+"";

            transactionLogger.error("refundRequest----" + refundRequest);

            String refundResponse = "";

            if (isTest)
            {
                refundResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("REFUND_TEST_URL") + endPoint, refundRequest);
            }
            else
            {
                refundResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("REFUND_LIVE_URL") + endPoint, refundRequest);
            }

            transactionLogger.error("refundResponse------" + refundResponse);
            if (functions.isValueNull(refundResponse))
            {
                Map<String, String> responseMap = ZotaPayUtils.getQueryMap(refundResponse);
                if (responseMap != null)
                {
                    String srn = responseMap.get("serial-number");
                    String client_id = responseMap.get("merchant-order-id");
                    String transactionId = responseMap.get("paynet-order-id");
                    String input = gatewayAccount.getMerchantId().trim() + client_id.trim() + transactionId.trim() + controlKey.trim();
                    String status_control = ZotaPayUtils.getControl(input.trim());

                    transactionLogger.debug("status-Control-----" + status_control);
                    String orderRequest =
                            "login=" + gatewayAccount.getMerchantId() + "\n" +
                            "&client_orderid=" + client_id + "\n" +
                            "&orderid=" + transactionId + "\n" +
                            "&by-request-sn=" + srn + "\n" +
                            "&control=" + status_control + "";

                    transactionLogger.error("order status request----" + orderRequest);

                    Thread.sleep(5000);
                    String refundStatusResponse = "";

                    if (isTest)
                    {
                        refundStatusResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_ORDER_STATUS_URL") + endPoint, orderRequest);
                    }
                    else
                    {
                        refundStatusResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_ORDER_STATUS_URL") + endPoint, orderRequest);
                    }

                    transactionLogger.error("refundStatusResponse-----" + refundStatusResponse);

                    if (functions.isValueNull(refundStatusResponse))
                    {
                        Map<String, String> saleStatusResponseMap = ZotaPayUtils.getQueryMap(refundStatusResponse);
                        if (saleStatusResponseMap != null)
                        {
                            String refund_approval_code = "";
                            String refund_processor_debit_arn = "";
                            String refund_transactionId = saleStatusResponseMap.get("paynet-order-id");
                            String refund_status = saleStatusResponseMap.get("status");
                            String refund_transaction_type = saleStatusResponseMap.get("transaction-type");
                            String refund_processing_date = saleStatusResponseMap.get("paynet-processing-date");
                            String refund_reversal_amount = saleStatusResponseMap.get("reversal-amount");

                            transactionLogger.debug("refund_status-----------" + refund_status);

                            if (functions.isValueNull(saleStatusResponseMap.get("approval-code")))
                            {
                                refund_approval_code = saleStatusResponseMap.get("approval-code");
                            }
                            if (functions.isValueNull(saleStatusResponseMap.get("processor-debit-arn")))
                            {
                                refund_processor_debit_arn = saleStatusResponseMap.get("processor-debit-arn");
                            }

                            if ("processing".equals(refund_status))
                            {
                                transactionLogger.debug("in processing-----");
                                commResponseVO.setStatus("Pending");
                                commResponseVO.setRemark(refund_status);
                                commResponseVO.setDescription(refund_status);

                            }
                            else if ("approved".equalsIgnoreCase(refund_status))
                            {
                                transactionLogger.debug("in approved-------" + refund_status);
                                commResponseVO.setStatus("success");
                                commResponseVO.setRemark("Transaction Successful");
                                commResponseVO.setDescription(refund_status);
                                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                            }
                            else
                            {
                                transactionLogger.debug("in fail-------" + refund_status);
                                commResponseVO.setStatus("fail");
                                if(functions.isValueNull(refund_status)){
                                    commResponseVO.setRemark(refund_status);
                                    commResponseVO.setDescription(refund_status);
                                }else {
                                    commResponseVO.setRemark("Transaction Failed");
                                    commResponseVO.setDescription("Transaction Failed");
                                }
                            }
                            commResponseVO.setBankTransactionDate(URLDecoder.decode(refund_processing_date));
                            commResponseVO.setErrorCode(refund_approval_code);
                            commResponseVO.setArn(refund_processor_debit_arn);
                            commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                            commResponseVO.setTransactionId(refund_transactionId);
                            commResponseVO.setResponseHashInfo(srn+"@"+endPoint);
                            commResponseVO.setCurrency(currency);
                            commResponseVO.setAmount(refund_reversal_amount);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }

        return commResponseVO;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("-----inside processInquiry-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();

        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        String controlKey = gatewayAccount.getFRAUD_FTP_PATH();


        String input = gatewayAccount.getMerchantId().trim() + commTransactionDetailsVO.getOrderId().trim() + commTransactionDetailsVO.getPreviousTransactionId().trim() + controlKey.trim();
        String status_control = ZotaPayUtils.getControl(input.trim());

       String srn="";
        String endpoint="";
        if(functions.isValueNull(commTransactionDetailsVO.getResponseHashInfo())){
            String data[] = commTransactionDetailsVO.getResponseHashInfo().split("@");
            srn=data[0];
            endpoint=data[1];
        }
        transactionLogger.debug("srn-----"+srn);
        transactionLogger.debug("endpoint-----"+endpoint);
        try
        {
            String orderRequest =
                    "login=" + gatewayAccount.getMerchantId() + "\n" +
                    "&client_orderid=" + commTransactionDetailsVO.getOrderId() + "\n" +
                    "&orderid=" + commTransactionDetailsVO.getPreviousTransactionId() + "\n" +
                    "&by-request-sn=" + srn + "\n" +
                    "&control=" + status_control + "";

            transactionLogger.error("order status request----" + orderRequest);

            Thread.sleep(5000);
            String inquiryStatusResponse = "";

            if (isTest)
            {
                inquiryStatusResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_ORDER_STATUS_URL") + endpoint, orderRequest);
            }
            else
            {
                inquiryStatusResponse = ZotaPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_ORDER_STATUS_URL") + endpoint, orderRequest);
            }

            transactionLogger.error("inquiryStatusResponse-----" + inquiryStatusResponse);

            if (functions.isValueNull(inquiryStatusResponse))
            {
                Map<String, String> saleStatusResponseMap = ZotaPayUtils.getQueryMap(inquiryStatusResponse);
                if (saleStatusResponseMap != null)
                {
                    String inquiry_approval_code = "";
                    String inquiry_processor_debit_arn = "";
                    String inquiry_transactionId = saleStatusResponseMap.get("paynet-order-id");
                    String inquiry_status = saleStatusResponseMap.get("status");
                    String inquiry_transaction_type = saleStatusResponseMap.get("transaction-type");
                    String inquiry_processing_date = saleStatusResponseMap.get("paynet-processing-date");
                    String inquiry_amount=saleStatusResponseMap.get("amount");
                    String inquiry_currency=saleStatusResponseMap.get("currency");

                    transactionLogger.debug("inquiry_status-----------" + inquiry_status);

                    if (functions.isValueNull(saleStatusResponseMap.get("approval-code")))
                    {
                        inquiry_approval_code = saleStatusResponseMap.get("approval-code");
                    }
                    if (functions.isValueNull(saleStatusResponseMap.get("processor-debit-arn")))
                    {
                        inquiry_processor_debit_arn = saleStatusResponseMap.get("processor-debit-arn");
                    }

                    if ("processing".equals(inquiry_status))
                    {
                        transactionLogger.debug("in processing-----");
                        commResponseVO.setStatus("Pending");
                        commResponseVO.setRemark(inquiry_status);
                        commResponseVO.setDescription(inquiry_status);


                    }
                    else if ("approved".equalsIgnoreCase(inquiry_status))
                    {
                        transactionLogger.debug("in approved-------" + inquiry_status);
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark("Transaction Successful");
                        commResponseVO.setDescription(inquiry_status);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else
                    {
                        transactionLogger.debug("in fail-------" + inquiry_status);
                        commResponseVO.setStatus("fail");
                        if(functions.isValueNull(inquiry_status)){
                            commResponseVO.setRemark(inquiry_status);
                            commResponseVO.setDescription(inquiry_status);
                        }else {
                            commResponseVO.setRemark("Transaction Failed");
                            commResponseVO.setDescription("Transaction Failed");
                        }
                    }
                    commResponseVO.setTransactionStatus(inquiry_status);
                    commResponseVO.setBankTransactionDate(URLDecoder.decode(inquiry_processing_date));
                    commResponseVO.setErrorCode(inquiry_approval_code);
                    commResponseVO.setArn(inquiry_processor_debit_arn);
                    commResponseVO.setTransactionType(ZotaPayUtils.getTransactionType(inquiry_transaction_type));
                    commResponseVO.setTransactionId(inquiry_transactionId);
                    commResponseVO.setAmount(inquiry_amount);
                    commResponseVO.setCurrency(inquiry_currency);
                    commResponseVO.setMerchantId(gatewayAccount.getMerchantId());

                }
            }

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }
}
