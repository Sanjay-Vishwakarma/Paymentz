package com.payment.libill_payment.core;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by Nikita on 14/10/2015.
 */
public class LibillPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(LibillPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(LibillPaymentGateway.class.getName());

    public static final String GATEWAY_TYPE = "libill";

    public String getMaxWaitDays()
    {
        return null;
    }

    public  LibillPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        log.debug("Entering processAuthentication of LibillPaymentGateway...");
        transactionLogger.debug("Entering processAuthentication of LibillPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();

        String  endPoint = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String merchant_control = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE();

        String PREAUTH_URL = "https://sandbox.libill.com/paynet/api/v2/preauth/"+endPoint;

        String control = LibillUtills.processTxSHA(endPoint,trackingID,transDetailsVO.getAmount(),addressDetailsVO.getEmail(),merchant_control.trim());
        log.debug("CONTROL---------->"+control);

        String authRequest = "client_orderid="+trackingID+"\n" +
                "&order_desc="+transDetailsVO.getOrderDesc()+"\n" +
                "&first_name="+addressDetailsVO.getFirstname()+"\n" +
                "&last_name="+addressDetailsVO.getLastname()+"\n" +
                "&ssn="+addressDetailsVO.getSsn()+"\n" +
                "&birthday="+addressDetailsVO.getBirthdate()+"\n" +
                "&address1="+addressDetailsVO.getStreet()+"\n" +
                "&city="+addressDetailsVO.getCity()+"\n" +
                "&state="+addressDetailsVO.getState()+"\n" +
                "&zip_code="+addressDetailsVO.getZipCode()+"\n" +
                "&country="+addressDetailsVO.getCountry()+"\n" +
                "&phone="+addressDetailsVO.getPhone()+"\n" +
                "&cell_phone="+addressDetailsVO.getTelnocc()+"\n" +
                "&amount="+transDetailsVO.getAmount()+"\n" +
                "&email="+addressDetailsVO.getEmail()+"\n" +
                "&currency="+transDetailsVO.getCurrency()+"\n" +
                "&ipaddress="+addressDetailsVO.getIp()+"\n" +
                "&site_url=www.google.com\n" +
                "&credit_card_number="+cardDetailsVO.getCardNum()+"\n" +
                "&card_printed_name="+cardDetailsVO.getCardHolderName()+"\n" +
                "&expire_month="+cardDetailsVO.getExpMonth()+"\n" +
                "&expire_year="+cardDetailsVO.getExpYear()+"\n" +
                "&cvv2="+cardDetailsVO.getcVV()+"\n" +
                "&destination=www.twitch.tv/dreadztv\n" +
                "&merchant_control="+merchant_control+"\n" +
                "&redirect_url="+transDetailsVO.getRedirectUrl()+"\n" +
                "&server_callback_url=http://doc.libill.com/doc/dummy.htm\n" +
                "&merchant_data=VIP customer\n" +
                "&control="+control+"";

        String authRequestlog = "client_orderid="+trackingID+"\n" +
                "&order_desc="+transDetailsVO.getOrderDesc()+"\n" +
                "&first_name="+addressDetailsVO.getFirstname()+"\n" +
                "&last_name="+addressDetailsVO.getLastname()+"\n" +
                "&ssn="+addressDetailsVO.getSsn()+"\n" +
                "&birthday="+addressDetailsVO.getBirthdate()+"\n" +
                "&address1="+addressDetailsVO.getStreet()+"\n" +
                "&city="+addressDetailsVO.getCity()+"\n" +
                "&state="+addressDetailsVO.getState()+"\n" +
                "&zip_code="+addressDetailsVO.getZipCode()+"\n" +
                "&country="+addressDetailsVO.getCountry()+"\n" +
                "&phone="+addressDetailsVO.getPhone()+"\n" +
                "&cell_phone="+addressDetailsVO.getTelnocc()+"\n" +
                "&amount="+transDetailsVO.getAmount()+"\n" +
                "&email="+addressDetailsVO.getEmail()+"\n" +
                "&currency="+transDetailsVO.getCurrency()+"\n" +
                "&ipaddress="+addressDetailsVO.getIp()+"\n" +
                "&site_url=www.google.com\n" +
                "&credit_card_number="+functions.maskingPan(cardDetailsVO.getCardNum())+"\n" +
                "&card_printed_name="+cardDetailsVO.getCardHolderName()+"\n" +
                "&expire_month="+functions.maskingNumber(cardDetailsVO.getExpMonth())+"\n" +
                "&expire_year="+functions.maskingNumber(cardDetailsVO.getExpYear())+"\n" +
                "&cvv2="+functions.maskingNumber(cardDetailsVO.getcVV())+"\n" +
                "&destination=www.twitch.tv/dreadztv\n" +
                "&merchant_control="+merchant_control+"\n" +
                "&redirect_url="+transDetailsVO.getRedirectUrl()+"\n" +
                "&server_callback_url=http://doc.libill.com/doc/dummy.htm\n" +
                "&merchant_data=VIP customer\n" +
                "&control="+control+"";

        log.debug("Preauth Request-------->"+trackingID + "--" + authRequestlog);
        String authResponse = LibillUtills.doPostHTTPSURLConnection(PREAUTH_URL,authRequest);
        log.debug("Preauth Response-------->"+trackingID + "--" + authResponse);

        HashMap responseMap = new HashMap();
        responseMap = LibillUtills.readSaleResponse(authResponse);
        String status = "fail";

        if(!functions.isValueNull(String.valueOf(responseMap.get("error-message"))) && !functions.isValueNull(String.valueOf(responseMap.get("error-code"))))
        {
            status = "success";
            commResponseVO.setStatus(status);
            log.debug("status---" + status);
            log.debug("type---" + responseMap.get("type"));
            log.debug("serial-number---" + responseMap.get("serial-number"));
            log.debug("Merchant-order-id---" + responseMap.get("merchant-order-id"));
            log.debug("paynet-order-id---" + responseMap.get("paynet-order-id"));
            log.debug("end-point-id---" + responseMap.get("end-point-id"));
            commResponseVO.setDescription("Transaction Successful");
            commResponseVO.setTransactionStatus(status);
            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
        }
        else
        {
            commResponseVO.setStatus("fail");
            commResponseVO.setErrorCode((String) responseMap.get("error-code"));
            commResponseVO.setRemark("Transaction Failed");
            commResponseVO.setDescription((String) responseMap.get("error-message"));
        }
        log.debug("client_orderid---" + trackingID);
        commResponseVO.setTransactionId((String) responseMap.get("paynet-order-id"));
        commResponseVO.setResponseHashInfo((String) responseMap.get("end-point-id"));
        commResponseVO.setMerchantOrderId((String) responseMap.get("merchant-order-id"));
        commResponseVO.setTransactionType("auth");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

        return commResponseVO;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering processSale of LibillPaymentGateway...");
        transactionLogger.debug("Entering processSale of LibillPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();

        String  endPoint = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String merchant_control = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE();

        String TESTURL = "https://sandbox.libill.com/paynet/api/v2/sale/"+endPoint;

        String control = LibillUtills.processTxSHA(endPoint,trackingID,transDetailsVO.getAmount(),addressDetailsVO.getEmail(),merchant_control.trim());
        log.debug("CONTROL---------->"+control);

        String saleRequest="client_orderid="+trackingID+"\n" +
                "&order_desc="+transDetailsVO.getOrderDesc()+"\n" +
                "&first_name="+addressDetailsVO.getFirstname()+"\n" +
                "&last_name="+addressDetailsVO.getLastname()+"\n" +
                "&ssn="+addressDetailsVO.getSsn()+"\n" +
                "&birthday="+addressDetailsVO.getBirthdate()+"\n" +
                "&address1="+addressDetailsVO.getStreet()+"\n" +
                "&city="+addressDetailsVO.getCity()+"\n" +
                "&state="+addressDetailsVO.getState()+"\n" +
                "&zip_code="+addressDetailsVO.getZipCode()+"\n" +
                "&country="+addressDetailsVO.getCountry()+"\n" +
                "&phone="+addressDetailsVO.getPhone()+"\n" +
                "&cell_phone="+addressDetailsVO.getTelnocc()+"\n" +
                "&amount="+transDetailsVO.getAmount()+"\n" +
                "&email="+addressDetailsVO.getEmail()+"\n" +
                "&currency="+transDetailsVO.getCurrency()+"\n" +
                "&ipaddress="+addressDetailsVO.getIp()+"\n" +
                "&site_url=www.google.com\n" +
                "&credit_card_number="+cardDetailsVO.getCardNum()+"\n" +
                "&card_printed_name="+cardDetailsVO.getCardHolderName()+"\n" +
                "&expire_month="+cardDetailsVO.getExpMonth()+"\n" +
                "&expire_year="+cardDetailsVO.getExpYear()+"\n" +
                "&cvv2="+cardDetailsVO.getcVV()+"\n" +
                "&destination-card-no=5517783028437719\n" +
                "&purpose=www.twitch.tv/dreadztv\n" +
                "&merchant_control="+merchant_control+"\n" +
                "&redirect_url="+transDetailsVO.getRedirectUrl()+"\n" +
                "&server_callback_url=http://doc.libill.com/doc/dummy.htm\n" +
                "&merchant_data=VIP customer\n" +
                "&control="+control+"\n";

        String saleRequestlog="client_orderid="+trackingID+"\n" +
                "&order_desc="+transDetailsVO.getOrderDesc()+"\n" +
                "&first_name="+addressDetailsVO.getFirstname()+"\n" +
                "&last_name="+addressDetailsVO.getLastname()+"\n" +
                "&ssn="+addressDetailsVO.getSsn()+"\n" +
                "&birthday="+addressDetailsVO.getBirthdate()+"\n" +
                "&address1="+addressDetailsVO.getStreet()+"\n" +
                "&city="+addressDetailsVO.getCity()+"\n" +
                "&state="+addressDetailsVO.getState()+"\n" +
                "&zip_code="+addressDetailsVO.getZipCode()+"\n" +
                "&country="+addressDetailsVO.getCountry()+"\n" +
                "&phone="+addressDetailsVO.getPhone()+"\n" +
                "&cell_phone="+addressDetailsVO.getTelnocc()+"\n" +
                "&amount="+transDetailsVO.getAmount()+"\n" +
                "&email="+addressDetailsVO.getEmail()+"\n" +
                "&currency="+transDetailsVO.getCurrency()+"\n" +
                "&ipaddress="+addressDetailsVO.getIp()+"\n" +
                "&site_url=www.google.com\n" +
                "&credit_card_number="+functions.maskingPan(cardDetailsVO.getCardNum())+"\n" +
                "&card_printed_name="+cardDetailsVO.getCardHolderName()+"\n" +
                "&expire_month="+functions.maskingNumber(cardDetailsVO.getExpMonth())+"\n" +
                "&expire_year="+functions.maskingNumber(cardDetailsVO.getExpYear())+"\n" +
                "&cvv2="+functions.maskingNumber(cardDetailsVO.getcVV()) + "\n" +
                "&destination-card-no=5517783028437719\n" +
                "&purpose=www.twitch.tv/dreadztv\n" +
                "&merchant_control=" + merchant_control + "\n" +
                "&redirect_url=" + transDetailsVO.getRedirectUrl() + "\n" +
                "&server_callback_url=http://doc.libill.com/doc/dummy.htm\n" +
                "&merchant_data=VIP customer\n" +
                "&control=" + control+"\n";

        log.debug("Sale Request--------->" + trackingID + "--" + saleRequestlog);
        String saleResponse=LibillUtills.doPostHTTPSURLConnection(TESTURL, saleRequest);

        log.debug("Sale Response--------->"+trackingID + "--" + saleResponse);

        HashMap responseMap = new HashMap();
        responseMap = LibillUtills.readSaleResponse(saleResponse);
        String status = "fail";

        if(!functions.isValueNull(String.valueOf(responseMap.get("error-message"))) && !functions.isValueNull(String.valueOf(responseMap.get("error-code"))))
        {
            status = "success";
            commResponseVO.setStatus(status);
            log.debug("status---" + status);
            log.debug("type---" + responseMap.get("type"));
            log.debug("serial-number---" + responseMap.get("serial-number"));
            log.debug("Merchant-order-id---" + responseMap.get("merchant-order-id"));
            log.debug("paynet-order-id---" + responseMap.get("paynet-order-id"));
            log.debug("end-point-id---" + responseMap.get("end-point-id"));
            commResponseVO.setDescription("Transaction Successful");
            commResponseVO.setTransactionStatus(status);
            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
        }
        else
        {
            commResponseVO.setStatus("fail");
            commResponseVO.setErrorCode((String) responseMap.get("error-code"));
            commResponseVO.setRemark("Transaction Failed");
            commResponseVO.setDescription((String) responseMap.get("error-message"));
        }

        log.debug("client_orderid---" + trackingID);
        commResponseVO.setTransactionId((String) responseMap.get("paynet-order-id"));//paynet
        commResponseVO.setResponseHashInfo((String) responseMap.get("end-point-id"));//end point
        commResponseVO.setMerchantOrderId((String) responseMap.get("merchant-order-id"));//merchant order id
        commResponseVO.setTransactionType("sale");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering processVoid of LibillPaymentGateway...");
        transactionLogger.debug("Entering processVoid of LibillPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();

        String  endPoint = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String merchant_control = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE();
        String login = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();

        String VOID_URL = "https://sandbox.libill.com/paynet/api/v2/void/"+endPoint;

        String control = LibillUtills.cancelTxSHA(login.trim(),trackingID.trim(),transactionDetailsVO.getPreviousTransactionId().trim(),merchant_control.trim());
        log.debug("CONTROL-------->"+login+trackingID+transactionDetailsVO.getPreviousTransactionId()+merchant_control);
        log.debug("CONTROL-------->"+control);

        String cancelRequest = "login="+login+"\n" +
                "&client_orderid="+trackingID+"\n" +
                "&orderid="+transactionDetailsVO.getPreviousTransactionId()+"\n" +
                "&comment=test\n" +
                "&control="+control+"";

        String cancelResponse = LibillUtills.doPostHTTPSURLConnection(VOID_URL,cancelRequest);
        log.debug("Cancel Request----->"+trackingID + "--" + cancelRequest);
        log.debug("Cancel Response----->"+trackingID + "--" + cancelResponse);

        HashMap responseMap = new HashMap();
        responseMap = LibillUtills.readSaleResponse(cancelResponse);
        String status = "fail";

        if(!functions.isValueNull(String.valueOf(responseMap.get("error-message"))) && !functions.isValueNull(String.valueOf(responseMap.get("error-code"))))
        {
            status = "success";
            commResponseVO.setStatus(status);
            log.debug("status---" + status);
            log.debug("type---" + responseMap.get("type"));
            log.debug("serial-number---" + responseMap.get("serial-number"));
            log.debug("Merchant-order-id---" + responseMap.get("merchant-order-id"));
            log.debug("paynet-order-id---" + responseMap.get("paynet-order-id"));
            log.debug("end-point-id---" + responseMap.get("end-point-id"));
            commResponseVO.setDescription("Transaction Successful");
            commResponseVO.setTransactionStatus(status);
            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
        }
        else
        {
            commResponseVO.setDescription("Transaction Failed");
            commResponseVO.setStatus("fail");
            commResponseVO.setErrorCode((String) responseMap.get("error-code"));
            commResponseVO.setDescription((String) responseMap.get("error-message"));
        }
        commResponseVO.setTransactionType("cancel");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering processRefund of LibillPaymentGateway...");
        transactionLogger.debug("Entering processRefund of LibillPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();

        String  endPoint = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String merchant_control = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE();
        String login = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();

        String REFUND_URL = "https://sandbox.libill.com/paynet/api/v2/return/"+endPoint;

        String control = LibillUtills.captureTxSHA(login.trim(),trackingID.trim(),transactionDetailsVO.getPreviousTransactionId().trim(),genericTransDetailsVO.getAmount().trim(),genericTransDetailsVO.getCurrency().trim(),merchant_control.trim());
        log.debug("CONTROL----->"+control);

        String refundRequest = "login="+login+"\n" +
                "&client_orderid="+trackingID+"\n" +
                "&orderid="+transactionDetailsVO.getPreviousTransactionId()+"\n" +
                "&amount="+genericTransDetailsVO.getAmount()+"\n" +
                "&currency="+genericTransDetailsVO.getCurrency()+"\n" +
                "&comment=test\n" +
                "&control="+control+"";

        String refundResponse = LibillUtills.doPostHTTPSURLConnection(REFUND_URL,refundRequest);
        log.debug("Refund Request=======>"+trackingID + "--" + refundRequest);
        log.debug("Refund Response=======>"+trackingID + "--" + refundResponse);

        HashMap responseMap = new HashMap();
        responseMap = LibillUtills.readSaleResponse(refundResponse);
        String status = "fail";

        if(!functions.isValueNull(String.valueOf(responseMap.get("error-message"))) && !functions.isValueNull(String.valueOf(responseMap.get("error-code"))))
        {
            status = "success";
            commResponseVO.setStatus(status);
            log.debug("status---" + status);
            log.debug("type---" + responseMap.get("type"));
            log.debug("serial-number---" + responseMap.get("serial-number"));
            log.debug("Merchant-order-id---" + responseMap.get("merchant-order-id"));
            log.debug("paynet-order-id---" + responseMap.get("paynet-order-id"));
            log.debug("end-point-id---" + responseMap.get("end-point-id"));
            commResponseVO.setDescription("Transaction Successful");
            commResponseVO.setTransactionStatus(status);
            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
        }
        else
        {
            commResponseVO.setDescription("Transaction Failed");
            commResponseVO.setStatus("fail");
            commResponseVO.setErrorCode((String) responseMap.get("error-code"));
            commResponseVO.setDescription((String) responseMap.get("error-message"));
        }
        commResponseVO.setTransactionType("refund");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering processCapture of LibillPaymentGateway...");
        transactionLogger.debug("Entering processCapture of LibillPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();

        String  endPoint = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String merchant_control = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE();
        String login = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();

        String CAPTURE_URL = "https://sandbox.libill.com/paynet/api/v2/capture/"+endPoint;

        String control = LibillUtills.captureTxSHA(login.trim(),trackingID.trim(),transactionDetailsVO.getPreviousTransactionId().trim(),genericTransDetailsVO.getAmount().trim(),genericTransDetailsVO.getCurrency().trim(),merchant_control.trim());
        log.debug("CONTROL----->"+control);

        String captureRequest = "login="+login+"\n" +
                "&client_orderid="+trackingID+"\n" +
                "&orderid="+transactionDetailsVO.getPreviousTransactionId()+"\n" +
                "&amount="+genericTransDetailsVO.getAmount()+"\n" +
                "&currency="+genericTransDetailsVO.getCurrency()+"\n" +
                "&control="+control+"";

        String captureResponse = LibillUtills.doPostHTTPSURLConnection(CAPTURE_URL,captureRequest);
        log.debug("Capture Request------->"+trackingID + "--" + captureRequest);
        log.debug("Capture Response------>"+trackingID + "--" + captureResponse);

        HashMap responseMap = new HashMap();
        responseMap = LibillUtills.readSaleResponse(captureResponse);
        String status = "fail";

        if(!functions.isValueNull(String.valueOf(responseMap.get("error-message"))) && !functions.isValueNull(String.valueOf(responseMap.get("error-code"))))
        {
            status = "success";
            commResponseVO.setStatus(status);
            log.debug("status---" + status);
            log.debug("type---" + responseMap.get("type"));
            log.debug("serial-number---" + responseMap.get("serial-number"));
            log.debug("Merchant-order-id---" + responseMap.get("merchant-order-id"));
            log.debug("paynet-order-id---" + responseMap.get("paynet-order-id"));
            log.debug("end-point-id---" + responseMap.get("end-point-id"));
            commResponseVO.setDescription("Transaction Successful");
            commResponseVO.setTransactionStatus(status);
            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
        }
        else
        {
            commResponseVO.setDescription("Transaction Failed");
            commResponseVO.setStatus("fail");
            commResponseVO.setErrorCode((String) responseMap.get("error-code"));
            commResponseVO.setDescription((String) responseMap.get("error-message"));
        }
        commResponseVO.setTransactionType("capture");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingid, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("LibillPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public static void main(String[] args) throws PZTechnicalViolationException
    {
        String testUrl="https://secure.traxx.com/Payments/WebServer?";
        String testUrl1="https://sandbox.libill.com/paynet/api/v2/sale/117";

        String req = "request%5Ftype=AUTHORIZE" +
                "&username=merch11275" +
                "&password=hpzFf275s" +
                "&merchant%5Fid=KCA00XGY633SMBD8" +
                "&trxn%5Ftype=CAPTURE" +
                "&track%5Fid=512140807" +
                "&currency%5Fcode=USD" +
                "&amount=20.50" +
                "&payment%5Fmethod=2" +
                "&card%5Faccount=5464000000000000" +
                "&card%5Fexpiry=1010" +
                "&cvv=476" +
                "&ip%5Faddress=203.208.22.44" +
                "&site%5Furl=www.mydomain.com" +
                "&first%5Fname=John" +
                "&last%5Fname=jmith" +
                "&address1=87MainStreet" +
                "&city=SanFrancisco&state=CA" +
                "&country%5Fcode=US" +
                "&zip%5Fcode=1000" +
                "&email=jsmith@mail.com";

        String merchant_control = LibillUtills.processTxSHA("117","Test123","50.00","nikita.lanjewar@pz.com","83173B5B-0605-48CA-9DA0-49F07FD3DF4B");
        //System.out.println("control----"+merchant_control);
        String saleRequest="client_orderid=Test123\n" +
                "&order_desc=Test123\n" +
                "&first_name=Nikita\n" +
                "&last_name=Lanjewar\n" +
                "&ssn=\n" +
                "&birthday=\n" +
                "&address1=Malad12\n" +
                "&city=Mumbai\n" +
                "&state=MH\n" +
                "&zip_code=400064\n" +
                "&country=IN\n" +
                "&phone=9876543210\n" +
                "&cell_phone=\n" +
                "&amount=50.00\n" +
                "&email=nikita.lanjewar@pz.com\n" +
                "&currency=USD\n" +
                "&ipaddress=\n" +
                "&site_url=www.google.com\n" +
                "&credit_card_number=4444333322221111\n" +
                "&card_printed_name=Nikita Lanjewar\n" +
                "&expire_month=06\n" +
                "&expire_year=2019\n" +
                "&cvv2=123\n" +
                "&destination-card-no=5517783028437719\n" +
                "&purpose=www.twitch.tv/dreadztv\n" +
                "&merchant_control=\n" +
                "&redirect_url=\n" +
                "&server_callback_url=http://doc.libill.com/doc/dummy.htm\n" +
                "&merchant_data=VIP customer\n" +
                "&control="+merchant_control+"\n";

        String res = LibillUtills.doPostHTTPSURLConnection(saleRequest,testUrl1);
        /*System.out.println("req----"+saleRequest);
        System.out.println("resp----"+res);*/

    }
}
