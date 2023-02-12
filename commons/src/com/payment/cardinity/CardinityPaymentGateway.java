package com.payment.cardinity;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.google.gson.Gson;
import com.payment.Enum.PZProcessType;
import com.payment.cardinity.exceptions.CardinityException;
import com.payment.cardinity.exceptions.ValidationException;
import com.payment.cardinity.model.*;
import com.payment.cardinity.model.Void;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Created by Uday on 4/18/18.
 */
public class CardinityPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "cardinity";
    private final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.cardinity");
    private final ResourceBundle RB2=LoadProperties.getProperty("com.directi.pg.2CharCountryList");
    TransactionLogger transactionLogger = new TransactionLogger(CardinityPaymentGateway.class.getName());

    public CardinityPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public static void main(String[] args)
    {
        CardinityClient client = new CardinityClient("test_xwtk3yqf1xfjw4o0yi3gc45bccfuoa", "hntcg2mri0a9ghvaigtoyplm8acjbnd1hkyoudhbs0pdtd6qch");


            Gson gson = new Gson();
            Payment payment = new Payment();
           /* payment.setAmount(new BigDecimal(50.00));
            payment.setCurrency("EUR");
            payment.setCountry("IN");
            payment.setPaymentMethod(Payment.Method.CARD);
            payment.setOrderId("87356");
            payment.setSettle(false);
           // payment.setDescription("3d-fail");

            Card card = new Card();
            card.setPan("4111111111111111");
            card.setCvc(123);
            card.setExpMonth(1);
            card.setExpYear(2020);
            card.setHolder("Uday Raj");
            payment.setPaymentInstrument(card);*/

           // PaymentValidator paymentValidator= new PaymentValidator();
         //   paymentValidator.validate(payment);


           /* String request = gson.toJson(payment);
             System.out.println("Request-----" + request);*/
       /* UUID paymentId= UUID.fromString("ad2a851a-b444-4e53-b4a9-dc61dfbffd42");
        String paRes="3d-pass";*/

           /* Refund refund = new Refund();
            refund.setAmount(new BigDecimal(50.00));*/
            // refund.setDescription("fail");
             UUID paymentId = UUID.fromString("0f3298d1-4416-47be-9912-68f84a0cd6ce");
           // UUID refund1 = UUID.fromString("b2299f9e-697c-4878-b425-d0cd17089e1b");
          //   Result<Payment> result = client.createPayment(payment);


            //  Result<Payment> result=client.finalizePayment(paymentId,paRes);


           // Result<Refund> result = client.createRefund(paymentId, refund);


            //Result<Settlement> result=client.getSettlement(paymentId1,paymentId);

            //   Result<Payment> result=client.getPayment(paymentId);

           /* Settlement settlement= new Settlement();
            settlement.setAmount(new BigDecimal(50.00));
            //settlement.setDescription("fail");
            UUID paymentId=UUID.fromString("fda5f944-ed36-46ec-b6bd-5e11acab1a73");
            Result<Settlement> result=client.createSettlement(paymentId,settlement);*/
        /*Void aVoid= new Void();
        UUID paymentId=UUID.fromString("48402484-650d-4423-88bc-b758d1f4f019");
        Result<Void> result=client.createVoid(paymentId,aVoid);*/
           /* UUID settlementId = UUID.fromString("7345b45f-865a-4a49-8f0d-b8b22338b5a9");
            UUID paymentId = UUID.fromString("fda5f944-ed36-46ec-b6bd-5e11acab1a73");
            Result<Settlement> result = client.getSettlement(paymentId, settlementId);

            if (result.isValid()) {
                Settlement settlement = result.getItem();
            }
            else {
                CardinityError error = result.getCardinityError();
            }*/


            //   UUID paymentId=UUID.fromString("e8557570-2626-48bf-8094-6d0ad59166e5"); //e8557570-2626-48bf-8094-6d0ad59166e5
          //  UUID refundId=UUID.fromString("90a53289-ab53-471e-afce-ab87eb4d3b11"); //90a53289-ab53-471e-afce-ab87eb4d3b11

            Result<Payment> result = client.getPayment(paymentId);
            if (result.isValid()) {
                 payment = result.getItem();
            }
            else {
                CardinityError error = result.getCardinityError();
            }
          /*  Result<Refund> result = client.getRefund(paymentId, refundId);

            if (result.isValid()) {
                Refund refund = result.getItem();
            }
            else {
                CardinityError error = result.getCardinityError();

            }

            if(result.getItem()!=null){
                System.out.println("Hello");
            }else if(result.getCardinityError()!=null){
                System.out.println("Bye");
            }*/
            String respone = gson.toJson(result);
            //System.out.println("respone-----" + respone);



       /* catch (CardinityException ce)
        {
            ("CardinityException----" + ce);
        }*/
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----Inside processSale------");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        Functions functions= new Functions();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Gson gson = new Gson();
        Boolean isTest = gatewayAccount.isTest();
        String key = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
        String termUrl="";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TEST_TERM_URL");
            transactionLogger.error("from RB----"+termUrl);
        }
        try {
            CardinityClient client = new CardinityClient(key, secret);

            Card card = new Card();
            card.setPan(commCardDetailsVO.getCardNum());
            card.setCvc(Integer.parseInt(commCardDetailsVO.getcVV()));
            card.setExpMonth(Integer.parseInt(commCardDetailsVO.getExpMonth()));
            card.setExpYear(Integer.parseInt(commCardDetailsVO.getExpYear()));
            card.setHolder(commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());

            Payment payment = new Payment();
            payment.setAmount(new BigDecimal(commTransactionDetailsVO.getAmount()));
            payment.setCurrency(commTransactionDetailsVO.getCurrency());
            if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                if(commAddressDetailsVO.getCountry().length()==3){
                    payment.setCountry(RB2.getString(commAddressDetailsVO.getCountry()));
                }else {
                    payment.setCountry(commAddressDetailsVO.getCountry());
                }
            }else {
                payment.setCountry(commCardDetailsVO.getCountry_code_A2());
            }

            payment.setPaymentMethod(Payment.Method.CARD);
            payment.setOrderId(trackingID);
            payment.setSettle(true);                 //true value describe -sale
            if ("Y".equalsIgnoreCase(is3dSupported))
            {
                payment.setDescription("3d-pass");
            }
            else
            {
                payment.setDescription(commTransactionDetailsVO.getOrderDesc());
            }
            payment.setPaymentInstrument(card);
            String saleRequest = gson.toJson(payment);
            //transactionLogger.error("saleRequest-----" + saleRequest);
            Result<Payment> result = null;
            if (isTest)
            {
                result = client.createPayment(payment);
            }
            else
            {
                result = client.createPayment(payment);
            }
            if (result != null)
            {
                String saleResponse = gson.toJson(result);
                transactionLogger.error("saleResponse-----" + saleResponse);

                if (result.isValid() && result.getItem().getStatus() == Payment.Status.APPROVED)
                {
                    UUID paymentId = result.getItem().getId();
                    transactionLogger.error("paymentId-----" + String.valueOf(paymentId));
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setRemark("Transaction Successful");
                    comm3DResponseVO.setTransactionId(String.valueOf(paymentId));
                    comm3DResponseVO.setDescription(String.valueOf(result.getItem().getStatus()));
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    comm3DResponseVO.setBankTransactionDate(String.valueOf(result.getItem().getCreated()));
                    comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    comm3DResponseVO.setTransactionType(PZProcessType.SALE.toString());

                }
                else if (result.isValid() && result.getItem().getStatus() == Payment.Status.PENDING)
                {
                    UUID paymentId = result.getItem().getId();
                    String acsURL = result.getItem().getAuthorizationInformation().getUrl();
                    String paReq = result.getItem().getAuthorizationInformation().getData();
                    comm3DResponseVO.setStatus("pending3DConfirmation");
                    comm3DResponseVO.setRemark("3D Authentication Pending");
                    comm3DResponseVO.setUrlFor3DRedirect(acsURL);
                    comm3DResponseVO.setPaReq(paReq);
                    comm3DResponseVO.setTerURL(termUrl+trackingID);
                    comm3DResponseVO.setMd(trackingID +"@"+ String.valueOf(paymentId));
                    comm3DResponseVO.setTransactionId(String.valueOf(paymentId));
                    comm3DResponseVO.setDescription(String.valueOf(result.getItem().getStatus()));
                    comm3DResponseVO.setBankTransactionDate(String.valueOf(result.getItem().getCreated()));
                    comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    comm3DResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                }
                else if (result.isValid())
                {
                    String declineReason = result.getItem().getError();
                    UUID paymentId = result.getItem().getId();
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setRemark(declineReason);
                    comm3DResponseVO.setTransactionId(String.valueOf(paymentId));
                    comm3DResponseVO.setDescription(declineReason);
                    comm3DResponseVO.setBankTransactionDate(String.valueOf(result.getItem().getCreated()));
                    comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    comm3DResponseVO.setTransactionType(PZProcessType.SALE.toString());
                }
                else
                {
                    CardinityError error = result.getCardinityError();
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setRemark(error.getDetail());
                    comm3DResponseVO.setDescription(error.getTitle());
                    comm3DResponseVO.setErrorCode(String.valueOf(error.getStatus()));
                    comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    comm3DResponseVO.setTransactionType(PZProcessType.SALE.toString());
                }
            }
            else
            {
                comm3DResponseVO.setStatus("fail");
                comm3DResponseVO.setRemark("Technical Error");
                comm3DResponseVO.setDescription("Technical Error");
                comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                comm3DResponseVO.setTransactionType(PZProcessType.SALE.toString());
            }
            comm3DResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
            comm3DResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
            comm3DResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
        }
        catch (ValidationException e)
        {
            transactionLogger.error("ValidationException:::::", e);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "processSale()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (CardinityException ce)
        {
            transactionLogger.error("CardinityException:::::", ce);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "processSale()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.BANK_CONNECTIVITY_ISSUE, null, ce.getMessage(), ce.getCause());
        }
        return comm3DResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----Inside processAuthentication------");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        Functions functions= new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Gson gson = new Gson();
        Boolean isTest = gatewayAccount.isTest();
        String key = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
        String termUrl="";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TEST_TERM_URL");
            transactionLogger.error("from RB----"+termUrl);
        }
        try
        {
            CardinityClient client = new CardinityClient(key, secret);

            Card card = new Card();
            card.setPan(commCardDetailsVO.getCardNum());
            card.setCvc(Integer.parseInt(commCardDetailsVO.getcVV()));
            card.setExpMonth(Integer.parseInt(commCardDetailsVO.getExpMonth()));
            card.setExpYear(Integer.parseInt(commCardDetailsVO.getExpYear()));
            card.setHolder(commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname());

            Payment payment = new Payment();
            payment.setAmount(new BigDecimal(commTransactionDetailsVO.getAmount()));
            payment.setCurrency(commTransactionDetailsVO.getCurrency());
            if(functions.isValueNull(commAddressDetailsVO.getCountry())){
                if(commAddressDetailsVO.getCountry().length()==3){
                    payment.setCountry(RB2.getString(commAddressDetailsVO.getCountry()));
                }else {
                    payment.setCountry(commAddressDetailsVO.getCountry());
                }
            }else {
                payment.setCountry(commCardDetailsVO.getCountry_code_A2());
            }
            payment.setPaymentMethod(Payment.Method.CARD);
            payment.setOrderId(trackingID);
            payment.setSettle(false);    //false value describe -auth
            if ("Y".equalsIgnoreCase(is3dSupported))
            {
                payment.setDescription("3d-pass");
            }
            else
            {
                payment.setDescription(commTransactionDetailsVO.getOrderDesc());
            }

            payment.setPaymentInstrument(card);
            String authRequest = gson.toJson(payment);
            //transactionLogger.error("authRequest-----" + authRequest);
            Result<Payment> result = null;
            if (isTest)
            {
                result = client.createPayment(payment);
            }
            else
            {
                result = client.createPayment(payment);
            }
            if (result != null)
            {
                String authResponse = gson.toJson(result);
                transactionLogger.error("authResponse-----" + authResponse);

                if (result.isValid() && result.getItem().getStatus() == Payment.Status.APPROVED)
                {
                    UUID paymentId = result.getItem().getId();
                    transactionLogger.error("paymentId-----" + String.valueOf(paymentId));
                    comm3DResponseVO.setStatus("success");
                    comm3DResponseVO.setRemark("Transaction Successful");
                    comm3DResponseVO.setTransactionId(String.valueOf(paymentId));
                    comm3DResponseVO.setDescription(String.valueOf(result.getItem().getStatus()));
                    comm3DResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    comm3DResponseVO.setBankTransactionDate(String.valueOf(result.getItem().getCreated()));
                    comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    comm3DResponseVO.setTransactionType(PZProcessType.AUTH.toString());

                }
                else if (result.isValid() && result.getItem().getStatus() == Payment.Status.PENDING)
                {
                    UUID paymentId = result.getItem().getId();
                    transactionLogger.error("paymentId-----" + String.valueOf(paymentId));
                    String acsURL = result.getItem().getAuthorizationInformation().getUrl();
                    String paReq = result.getItem().getAuthorizationInformation().getData();
                    comm3DResponseVO.setStatus("pending3DConfirmation");
                    comm3DResponseVO.setRemark("3D Authentication Pending");
                    comm3DResponseVO.setUrlFor3DRedirect(acsURL);
                    comm3DResponseVO.setPaReq(paReq);
                    comm3DResponseVO.setTerURL(termUrl+trackingID);
                    comm3DResponseVO.setMd(trackingID +"@"+ String.valueOf(paymentId));
                    comm3DResponseVO.setTransactionId(String.valueOf(paymentId));
                    comm3DResponseVO.setDescription(String.valueOf(result.getItem().getStatus()));
                    comm3DResponseVO.setBankTransactionDate(String.valueOf(result.getItem().getCreated()));
                    comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    comm3DResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                }
                else if (result.isValid())
                {
                    String declineReason = result.getItem().getError();
                    UUID paymentId = result.getItem().getId();
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setRemark(declineReason);
                    comm3DResponseVO.setTransactionId(String.valueOf(paymentId));
                    comm3DResponseVO.setDescription(declineReason);
                    comm3DResponseVO.setBankTransactionDate(String.valueOf(result.getItem().getCreated()));
                    comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    comm3DResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                }
                else
                {
                    CardinityError error = result.getCardinityError();
                    comm3DResponseVO.setStatus("fail");
                    comm3DResponseVO.setRemark(error.getDetail());
                    comm3DResponseVO.setDescription(error.getTitle());
                    comm3DResponseVO.setErrorCode(String.valueOf(error.getStatus()));
                    comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    comm3DResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                }
            }
            else
            {
                comm3DResponseVO.setStatus("fail");
                comm3DResponseVO.setRemark("Technical Error");
                comm3DResponseVO.setDescription("Technical Error");
                comm3DResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                comm3DResponseVO.setTransactionType(PZProcessType.AUTH.toString());
            }
            comm3DResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
            comm3DResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
            comm3DResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
        }
        catch (ValidationException e)
        {
            transactionLogger.error("ValidationException:::::", e);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "processAuth()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (CardinityException ce)
        {
            transactionLogger.error("CardinityException:::::", ce);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "processAuth()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.BANK_CONNECTIVITY_ISSUE, null, ce.getMessage(), ce.getCause());
        }
        return comm3DResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----Inside Capture-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Gson gson = new Gson();
        Boolean isTest = gatewayAccount.isTest();
        String key = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try
        {
            CardinityClient client = new CardinityClient(key, secret);

            Settlement settlement = new Settlement();
            settlement.setAmount(new BigDecimal(commTransactionDetailsVO.getAmount()));
            UUID paymentId = UUID.fromString(commTransactionDetailsVO.getPreviousTransactionId());
            Result<Settlement> result = null;
            if (isTest)
            {
                result = client.createSettlement(paymentId, settlement);
            }
            else
            {
                result = client.createSettlement(paymentId, settlement);
            }
            if (result != null)
            {
                String captureResponse = gson.toJson(result);
                transactionLogger.error("captureResponse-----" + captureResponse);

                if (result.isValid() && result.getItem().getStatus() == Settlement.Status.APPROVED)
                {
                    settlement = result.getItem();
                    UUID newPaymentId = settlement.getId();
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Capture Successful");
                    commResponseVO.setTransactionId(String.valueOf(newPaymentId));
                    commResponseVO.setDescription(String.valueOf(settlement.getStatus()));
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setBankTransactionDate(String.valueOf(settlement.getCreated()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());

                }

                else if (result.isValid())
                {
                    String declineReason = result.getItem().getError();
                    UUID newPaymentId = settlement.getId();
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(declineReason);
                    commResponseVO.setTransactionId(String.valueOf(newPaymentId));
                    commResponseVO.setDescription(result.getItem().getDescription());
                    commResponseVO.setBankTransactionDate(String.valueOf(result.getItem().getCreated()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
                }
                else
                {
                    CardinityError error = result.getCardinityError();
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(error.getDetail());
                    commResponseVO.setDescription(error.getTitle());
                    commResponseVO.setErrorCode(String.valueOf(error.getStatus()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Technical Error");
                commResponseVO.setDescription("Technical Error");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
            }

        }
        catch (ValidationException e)
        {
            transactionLogger.error("ValidationException:::::", e);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "processCapture()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());


        }
        catch (CardinityException ce)
        {
            transactionLogger.error("CardinityException:::::", ce);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "processCapture()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.BANK_CONNECTIVITY_ISSUE, null, ce.getMessage(), ce.getCause());

        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----Inside processRefund-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Gson gson = new Gson();
        Boolean isTest = gatewayAccount.isTest();
        String key = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try
        {
            CardinityClient client = new CardinityClient(key, secret);

            transactionLogger.debug("Previous TransactionId------"+commTransactionDetailsVO.getPreviousTransactionId());
            transactionLogger.debug("Previous Amount------"+commTransactionDetailsVO.getAmount());
            Refund refund = new Refund();
            refund.setAmount(new BigDecimal(commTransactionDetailsVO.getAmount()));
            UUID paymentId = UUID.fromString(commTransactionDetailsVO.getPreviousTransactionId());
            Result<Refund> result = null;
            if (isTest)
            {
                result = client.createRefund(paymentId, refund);
            }
            else
            {
                result = client.createRefund(paymentId, refund);
            }
            if (result != null)
            {
                String refundResponse = gson.toJson(result);
                transactionLogger.error("refundResponse-----" + refundResponse);

                /** Request was valid and settlement was approved. */
                if (result.isValid() && result.getItem().getStatus() == Refund.Status.APPROVED)
                {
                    refund = result.getItem();
                    UUID newPaymentId = refund.getId();
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Refund Successful");
                    commResponseVO.setTransactionId(String.valueOf(newPaymentId));
                    commResponseVO.setDescription(String.valueOf(refund.getStatus()));
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setBankTransactionDate(String.valueOf(refund.getCreated()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.REFUND.toString());

                }

                /** Request was valid but settlement was declined. */
                else if (result.isValid())
                {
                    String declineReason = result.getItem().getError();
                    // proceed with declined settlement flow
                    UUID newPaymentId = refund.getId();
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(declineReason);
                    commResponseVO.setTransactionId(String.valueOf(newPaymentId));
                    commResponseVO.setDescription(result.getItem().getDescription());
                    commResponseVO.setBankTransactionDate(String.valueOf(result.getItem().getCreated()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                }

                /** Request was invalid. */
                else
                {
                    CardinityError error = result.getCardinityError();
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(error.getDetail());
                    commResponseVO.setDescription(error.getTitle());
                    commResponseVO.setErrorCode(String.valueOf(error.getStatus()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Technical Error");
                commResponseVO.setDescription("Technical Error");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
            }

        }
        catch (ValidationException e)
        {
            transactionLogger.error("ValidationException:::::", e);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "processRefund()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());


        }
        catch (CardinityException ce)
        {
            transactionLogger.error("CardinityException:::::", ce);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "processRefund()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.BANK_CONNECTIVITY_ISSUE, null, ce.getMessage(), ce.getCause());

        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----Inside processRefund-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Gson gson = new Gson();
        Boolean isTest = gatewayAccount.isTest();
        String key = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        try
        {
            CardinityClient client = new CardinityClient(key, secret);

            Void aVoid = new Void();
            UUID paymentId = UUID.fromString(commTransactionDetailsVO.getPreviousTransactionId());
            Result<Void> result = null;
            if (isTest)
            {
                result = client.createVoid(paymentId, aVoid);
            }
            else
            {
                result = client.createVoid(paymentId, aVoid);
            }
            if (result != null)
            {
                String refundResponse = gson.toJson(result);
                transactionLogger.error("captureResponse-----" + refundResponse);

                if (result.isValid() && result.getItem().getStatus() == Void.Status.APPROVED)
                {
                    aVoid = result.getItem();
                    UUID newPaymentId = aVoid.getId();
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Cancel Successful");
                    commResponseVO.setTransactionId(String.valueOf(newPaymentId));
                    commResponseVO.setDescription(String.valueOf(aVoid.getStatus()));
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setBankTransactionDate(String.valueOf(aVoid.getCreated()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
                }
                else if (result.isValid())
                {
                    String declineReason = result.getItem().getError();

                    UUID newPaymentId = aVoid.getId();
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(declineReason);
                    commResponseVO.setTransactionId(String.valueOf(newPaymentId));
                    commResponseVO.setDescription(result.getItem().getDescription());
                    commResponseVO.setBankTransactionDate(String.valueOf(result.getItem().getCreated()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
                }
                else
                {
                    CardinityError error = result.getCardinityError();
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(error.getDetail());
                    commResponseVO.setDescription(error.getTitle());
                    commResponseVO.setErrorCode(String.valueOf(error.getStatus()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Technical Error");
                commResponseVO.setDescription("Technical Error");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
            }

        }
        catch (ValidationException e)
        {
            transactionLogger.error("ValidationException:::::", e);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "processRefund()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());

        }
        catch (CardinityException ce)
        {
            transactionLogger.error("CardinityException:::::", ce);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "processRefund()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.BANK_CONNECTIVITY_ISSUE, null, ce.getMessage(), ce.getCause());

        }
        return commResponseVO;
    }

    public GenericResponseVO process3DConfirmation(GenericRequestVO requestVO, String PaRes) throws PZTechnicalViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("-----inside process3DConfirmation-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Gson gson = new Gson();
        Boolean isTest = gatewayAccount.isTest();
        String key = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        transactionLogger.debug("paymentid-----"+commTransactionDetailsVO.getPreviousTransactionId());

        try
        {
            CardinityClient client = new CardinityClient(key, secret);

            UUID paymentId = UUID.fromString(commTransactionDetailsVO.getPreviousTransactionId());
            String paRes = PaRes;

            Result<Payment> result = null;
            if (isTest)
            {
                result = client.finalizePayment(paymentId, paRes);
            }
            else
            {
                result = client.finalizePayment(paymentId, paRes);
            }
            if (result != null)
            {
                String finalResponse = gson.toJson(result);
                transactionLogger.error("finalResponse-----" + finalResponse);
                if (result.isValid() && result.getItem().getStatus() == Payment.Status.APPROVED)
                {
                    UUID newPaymentId = result.getItem().getId();
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Transaction Successful");
                    commResponseVO.setTransactionId(String.valueOf(newPaymentId));
                    commResponseVO.setDescription(String.valueOf(result.getItem().getStatus()));
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setBankTransactionDate(String.valueOf(result.getItem().getCreated()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                }
                else if (result.isValid())
                {
                    String declineReason = result.getItem().getError();
                    UUID newPaymentId = result.getItem().getId();
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(declineReason);
                    commResponseVO.setTransactionId(String.valueOf(newPaymentId));
                    commResponseVO.setDescription(declineReason);
                    commResponseVO.setBankTransactionDate(String.valueOf(result.getItem().getCreated()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                }
                else
                {
                    CardinityError error = result.getCardinityError();
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(error.getDetail());
                    commResponseVO.setDescription(error.getTitle());
                    commResponseVO.setErrorCode(String.valueOf(error.getStatus()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                }
            }else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Technical Error");
                commResponseVO.setDescription("Technical Error");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
            }
            commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
            commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
            commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
        }
        catch (ValidationException e)
        {
            transactionLogger.error("ValidationException:::::", e);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "process3DConfirmation()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());

        }
        catch (CardinityException ce)
        {
            transactionLogger.error("CardinityException:::::", ce);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "process3DConfirmation()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.BANK_CONNECTIVITY_ISSUE, null, ce.getMessage(), ce.getCause());

        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCommon3DAuthConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processCommon3DAuthConfirmation-----");
        Comm3DRequestVO commRequestVO = (Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO= commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Gson gson = new Gson();
        Boolean isTest = gatewayAccount.isTest();
        String key = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        transactionLogger.debug("paymentid-----"+commTransactionDetailsVO.getPreviousTransactionId());

        try
        {
            CardinityClient client = new CardinityClient(key, secret);

            UUID paymentId = UUID.fromString(commTransactionDetailsVO.getPreviousTransactionId());
            String paRes = commRequestVO.getPaRes();

            Result<Payment> result = null;
            if (isTest)
            {
                result = client.finalizePayment(paymentId, paRes);
            }
            else
            {
                result = client.finalizePayment(paymentId, paRes);
            }
            if (result != null)
            {
                String finalResponse = gson.toJson(result);
                transactionLogger.error("finalResponse-----" + finalResponse);
                if (result.isValid() && result.getItem().getStatus() == Payment.Status.APPROVED)
                {
                    UUID newPaymentId = result.getItem().getId();
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Transaction Successful");
                    commResponseVO.setTransactionId(String.valueOf(newPaymentId));
                    commResponseVO.setDescription(String.valueOf(result.getItem().getStatus()));
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setBankTransactionDate(String.valueOf(result.getItem().getCreated()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                }
                else if (result.isValid())
                {
                    String declineReason = result.getItem().getError();
                    UUID newPaymentId = result.getItem().getId();
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(declineReason);
                    commResponseVO.setTransactionId(String.valueOf(newPaymentId));
                    commResponseVO.setDescription(declineReason);
                    commResponseVO.setBankTransactionDate(String.valueOf(result.getItem().getCreated()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                }
                else
                {
                    CardinityError error = result.getCardinityError();
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(error.getDetail());
                    commResponseVO.setDescription(error.getTitle());
                    commResponseVO.setErrorCode(String.valueOf(error.getStatus()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                }
            }else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Technical Error");
                commResponseVO.setDescription("Technical Error");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
            }
            commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
            commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
            commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
        }
        catch (ValidationException e)
        {
            transactionLogger.error("ValidationException:::::", e);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "process3DAuthConfirmation()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());

        }
        catch (CardinityException ce)
        {
            transactionLogger.error("CardinityException:::::", ce);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "process3DAuthConfirmation()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.BANK_CONNECTIVITY_ISSUE, null, ce.getMessage(), ce.getCause());

        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processCommon3DSaleConfirmation-----");
        Comm3DRequestVO commRequestVO = (Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO= commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Gson gson = new Gson();
        Boolean isTest = gatewayAccount.isTest();
        String key = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        transactionLogger.debug("paymentid-----"+commTransactionDetailsVO.getPreviousTransactionId());

        try
        {
            CardinityClient client = new CardinityClient(key, secret);

            UUID paymentId = UUID.fromString(commTransactionDetailsVO.getPreviousTransactionId());
            String paRes = commRequestVO.getPaRes();

            Result<Payment> result = null;
            if (isTest)
            {
                result = client.finalizePayment(paymentId, paRes);
            }
            else
            {
                result = client.finalizePayment(paymentId, paRes);
            }
            if (result != null)
            {
                String finalResponse = gson.toJson(result);
                transactionLogger.error("finalResponse-----" + finalResponse);
                if (result.isValid() && result.getItem().getStatus() == Payment.Status.APPROVED)
                {
                    UUID newPaymentId = result.getItem().getId();
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("Transaction Successful");
                    commResponseVO.setTransactionId(String.valueOf(newPaymentId));
                    commResponseVO.setDescription(String.valueOf(result.getItem().getStatus()));
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setBankTransactionDate(String.valueOf(result.getItem().getCreated()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                }
                else if (result.isValid())
                {
                    String declineReason = result.getItem().getError();
                    UUID newPaymentId = result.getItem().getId();
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(declineReason);
                    commResponseVO.setTransactionId(String.valueOf(newPaymentId));
                    commResponseVO.setDescription(declineReason);
                    commResponseVO.setBankTransactionDate(String.valueOf(result.getItem().getCreated()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                }
                else
                {
                    CardinityError error = result.getCardinityError();
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(error.getDetail());
                    commResponseVO.setDescription(error.getTitle());
                    commResponseVO.setErrorCode(String.valueOf(error.getStatus()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                }
            }else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Technical Error");
                commResponseVO.setDescription("Technical Error");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
            }
            commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
            commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
            commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
        }
        catch (ValidationException e)
        {
            transactionLogger.error("ValidationException:::::", e);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "process3DSaleConfirmation()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());

        }
        catch (CardinityException ce)
        {
            transactionLogger.error("CardinityException:::::", ce);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "process3DSaleConfirmation()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.BANK_CONNECTIVITY_ISSUE, null, ce.getMessage(), ce.getCause());

        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("-----Inside processInquiry------");
        Gson gson= new Gson();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        Functions functions= new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String key = gatewayAccount.getFRAUD_FTP_USERNAME();
        String secret = gatewayAccount.getFRAUD_FTP_PASSWORD();
        Boolean isTest = gatewayAccount.isTest();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        String dbstatus=commTransactionDetailsVO.getPrevTransactionStatus();
        String status="";

        UUID paymentId=UUID.fromString("00000000-0000-0000-0000-000000000000");
        UUID transactionId=UUID.fromString("00000000-0000-0000-0000-000000000000");

        if(functions.isValueNull(commTransactionDetailsVO.getPreviousTransactionId())){
            paymentId = UUID.fromString(commTransactionDetailsVO.getPreviousTransactionId());
        }
        if(functions.isValueNull(commTransactionDetailsVO.getPaymentId())){
            transactionId =UUID.fromString(commTransactionDetailsVO.getPaymentId());
        }


        try
        {
            CardinityClient client = new CardinityClient(key, secret);

            transactionLogger.error("status-----"+dbstatus);
            transactionLogger.error("paymentid-----"+paymentId);
            transactionLogger.error("transactionId-----"+transactionId);
            if(dbstatus.equalsIgnoreCase("authstarted") || dbstatus.equalsIgnoreCase("capturesuccess") || dbstatus.equalsIgnoreCase("authsuccessful") || dbstatus.equalsIgnoreCase("authfailed")){
                Result<Payment> result =null;
                if(dbstatus.equalsIgnoreCase("capturesuccess")){
                    result=client.getPayment(transactionId);
                    String response=gson.toJson(result);
                    transactionLogger.error("Inquiry response-----"+response);
                    if (result.isValid()) {
                        result.getItem();
                    }
                    else {
                        CardinityError error = result.getCardinityError();
                        if(error.getStatus()==404){
                            Result<Settlement> result1 = client.getSettlement(paymentId, transactionId);
                            String response1=gson.toJson(result1);
                            transactionLogger.error("Inquiry response-----"+response1);

                            if (result1.isValid()) {
                                result1.getItem();
                                commResponseVO.setAmount(String.valueOf(result1.getItem().getAmount()));
                                commResponseVO.setCurrency(String.valueOf(result1.getItem().getCurrency()));
                                commResponseVO.setTransactionStatus(String.valueOf(result1.getItem().getStatus()));
                                commResponseVO.setDescription(String.valueOf(result1.getItem().getStatus()));
                                commResponseVO.setBankTransactionDate(String.valueOf(result1.getItem().getCreated()));
                                commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
                                commResponseVO.setTransactionId(String.valueOf(result1.getItem().getId()));
                            }
                            else {
                                result.getCardinityError();
                                commResponseVO.setTransactionStatus(String.valueOf(result1.getCardinityError().getTitle()));
                                commResponseVO.setDescription(String.valueOf(result1.getItem().getStatus()));
                            }
                            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                            return commResponseVO ;
                        }
                    }
                }else {
                    result = client.getPayment(paymentId);
                    if (result.isValid()) {
                        result.getItem();
                    }
                    else
                    {
                        result.getCardinityError();
                    }
                }
                String response=gson.toJson(result);
                transactionLogger.error("Inquiry response-----"+response);
                if(result.getItem()!=null){
                     String transactionStatus=String.valueOf(result.getItem().getStatus());
                    if(transactionStatus.equalsIgnoreCase("approved")){
                        status="success";
                    }else if(transactionStatus.equalsIgnoreCase("declined")){
                        status="fail";
                    }else if(transactionStatus.equalsIgnoreCase("pending")){
                        status="pending";
                    }
                    commResponseVO.setStatus(status);
                    commResponseVO.setAmount(String.valueOf(result.getItem().getAmount()));
                    commResponseVO.setCurrency(String.valueOf(result.getItem().getCurrency()));
                    commResponseVO.setTransactionStatus(String.valueOf(result.getItem().getStatus()));
                    commResponseVO.setDescription(String.valueOf(result.getItem().getStatus()));
                    commResponseVO.setBankTransactionDate(String.valueOf(result.getItem().getCreated()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setTransactionId(String.valueOf(result.getItem().getId()));
                    if(String.valueOf(result.getItem().getType()).equalsIgnoreCase("purchase")){
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }else {
                        commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                    }
                }else if(result.getCardinityError()!=null){
                    commResponseVO.setTransactionStatus(String.valueOf(result.getCardinityError().getTitle()));
                    commResponseVO.setDescription(String.valueOf(result.getCardinityError().getTitle()));
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }

            }else if(dbstatus.equalsIgnoreCase("capturestarted"))
            {

                Result<Settlement> result = client.getSettlement(paymentId, transactionId);

                if (result.isValid()) {
                    result.getItem();
                }
                else {
                    result.getCardinityError();
                }
                String response=gson.toJson(result);
                transactionLogger.error("Inquiry response-----"+response);

                if(result.getItem()!=null){
                    commResponseVO.setAmount(String.valueOf(result.getItem().getAmount()));
                    commResponseVO.setCurrency(String.valueOf(result.getItem().getCurrency()));
                    commResponseVO.setTransactionStatus(String.valueOf(result.getItem().getStatus()));
                    commResponseVO.setDescription(String.valueOf(result.getItem().getStatus()));
                    commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
                    commResponseVO.setTransactionId(String.valueOf(result.getItem().getId()));
                }else if(result.getCardinityError()!=null){
                    commResponseVO.setTransactionStatus(String.valueOf(result.getCardinityError().getTitle()));
                    commResponseVO.setDescription(String.valueOf(result.getItem().getStatus()));
                }
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

            }else if(dbstatus.equalsIgnoreCase("markedforreversal") || dbstatus.equalsIgnoreCase("reversed") || dbstatus.equalsIgnoreCase("partialrefund"))
            {

                Result<Refund> result = client.getRefund(paymentId, transactionId);

                if (result.isValid()) {
                    result.getItem();
                }
                else
                {
                    result.getCardinityError();
                }
                String response=gson.toJson(result);
                transactionLogger.error("Inquiry response-----"+response);

                if(result.getItem()!=null){
                    String transactionStatus=String.valueOf(result.getItem().getStatus());
                    if(transactionStatus.equalsIgnoreCase("approved")){
                        status="success";
                    }else{
                        status="fail";
                    }
                    commResponseVO.setAmount(String.valueOf(result.getItem().getAmount()));
                    commResponseVO.setCurrency(String.valueOf(result.getItem().getCurrency()));
                    commResponseVO.setTransactionStatus(String.valueOf(result.getItem().getStatus()));
                    commResponseVO.setDescription(String.valueOf(result.getItem().getStatus()));
                    commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                    commResponseVO.setTransactionId(String.valueOf(result.getItem().getId()));
                }else if(result.getCardinityError()!=null){
                    commResponseVO.setTransactionStatus(String.valueOf(result.getCardinityError().getTitle()));
                    commResponseVO.setDescription(String.valueOf(result.getItem().getStatus()));
                }
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }else if(dbstatus.equalsIgnoreCase("cancelstarted") || dbstatus.equalsIgnoreCase("authcancelled"))
            {

                Result<Void> result = client.getVoid(paymentId, transactionId);

                if (result.isValid()) {
                  result.getItem();
                }
                else {
                   result.getCardinityError();
                }
                String response=gson.toJson(result);
                transactionLogger.error("Inquiry response-----"+response);
                if(result.getItem()!=null){
                    commResponseVO.setTransactionStatus(String.valueOf(result.getItem().getStatus()));
                    commResponseVO.setDescription(String.valueOf(result.getItem().getStatus()));
                    commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
                    commResponseVO.setTransactionId(String.valueOf(result.getItem().getId()));
                }else if(result.getCardinityError()!=null){
                    commResponseVO.setTransactionStatus(String.valueOf(result.getCardinityError().getTitle()));
                    commResponseVO.setDescription(String.valueOf(result.getCardinityError().getTitle()));
                }
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }

        }catch (ValidationException e)
        {
            transactionLogger.error("ValidationException:::::", e);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "process3DConfirmation()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());

        }
        catch (CardinityException ce)
        {
            transactionLogger.error("CardinityException:::::", ce);
            PZExceptionHandler.raiseTechnicalViolationException(CardinityPaymentGateway.class.getName(), "process3DConfirmation()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.BANK_CONNECTIVITY_ISSUE, null, ce.getMessage(), ce.getCause());

        }
        return commResponseVO;
    }
}
