package com.payment.DusPayCard;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.DusPay.DusPayUtils;
import com.payment.Enum.PZProcessType;
import com.payment.PayMitco.core.PayMitcoResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Jeet Gupta on 04-06-2019.
 */
public class DusPayCardPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "DusPayCard";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.DusPay");
    private static Logger log = new Logger(DusPayCardPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(DusPayCardPaymentGateway.class.getName());

    public DusPayCardPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }


    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException,PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("--- Entering processSale of DusPayCardPaymentGateway ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions = new Functions();
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();

        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

        String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String bid = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        String descriptor = "";
        String merchantTollFree="";
        if (functions.isValueNull(commMerchantVO.getMerchantSupportNumber()))
        {
            merchantTollFree=commMerchantVO.getMerchantSupportNumber();
        }
        String merchantSiteURL="";
        if (functions.isValueNull(commMerchantVO.getSitename()))
        {
            merchantSiteURL=commMerchantVO.getSitename();
        }
        if (gatewayAccount.getIsDynamicDescriptor().equalsIgnoreCase("Y"))
        {
            descriptor = merchantTollFree + " - " + merchantSiteURL;
        }
        transactionLogger.error("descriptor ---"+descriptor);
        String  mode="";
        if (isTest)
            mode="test";
        else
            mode="live";
        transactionLogger.error("mode is --->"+isTest);
        String currency = "";
        String product =GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH();
        String accountid=GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FILE_SHORT_NAME();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        String cardsend ="curl";
        String action="product";
        String price="";
        String tmpl_amount="";
        String tmpl_currency="";
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        String notify_url="";
        String termUrl = "";
        transactionLogger.error("host url----" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            notify_url="https://" + commMerchantVO.getHostUrl() + RB.getString("NotifyURL");
            transactionLogger.error("from RB host url----" + termUrl);
            transactionLogger.error("from RB notify_url----" + notify_url);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            notify_url=RB.getString("LocalNotifyURL");
            transactionLogger.error("from RB----" + termUrl);
            transactionLogger.error("from RB LocalNotifyURL ----" + notify_url);
        }

        String product_name=GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        transactionLogger.error("product _name is -------->" + product_name);
        currency = transDetailsVO.getCurrency();
        transactionLogger.error("currency is ------>" + currency);
        price=transDetailsVO.getAmount();
        transactionLogger.error("price is ----->" + price);

        String ip = "";
        if (functions.isValueNull(addressDetailsVO.getCardHolderIpAddress()))
            ip = addressDetailsVO.getCardHolderIpAddress();
        else
            ip = addressDetailsVO.getIp();

        try
        {
            String saleReq =
                    "member=" + merchantId +
                            "&bid=" + bid +
                            "&product=" +product+
                            "&accountid=" + accountid +
                            "&cardsend=" + cardsend +
                            "&client_ip=" + ip +
                            "&action=" + action +
                            "&mode=" +mode+
                            "&price=" + price +
                            "&curr=" + currency +
                            "&product_name=" + product_name + //hardcoded produc-name
                            "&ccholder=" + ESAPI.encoder().encodeForURL(addressDetailsVO.getFirstname()) +  //customer firstName...
                            "&ccholder_lname=" +  ESAPI.encoder().encodeForURL(addressDetailsVO.getLastname()) + //  customer lastName...
                            "&email=" + addressDetailsVO.getEmail() +  //customer billing Email..
                            "&bill_street_1=" + addressDetailsVO.getStreet()+  //customer Shipping Street ..
                            "&bill_city=" + addressDetailsVO.getCity() +  //  shipping city..
                           // "&bill_state="+addressDetailsVO.getState()+
                            "&bill_country=" + addressDetailsVO.getCountry() + //shipping country...
                            "&bill_zip="+addressDetailsVO.getZipCode()+
                            "&bill_phone=" + addressDetailsVO.getPhone() +  // customer billing phone...
                            "&ccno=" + cardDetailsVO.getCardNum()+ //cardnumber ...
                            "&ccvv=" + cardDetailsVO.getcVV() + //cvv ..
                            "&month=" + cardDetailsVO.getExpMonth() + // exp month of card..
                            "&year=" + cardDetailsVO.getExpYear()+
                            "&id_order="+trackingID+
                            "&notify_url="+notify_url+trackingID+
                            "&success_url="+termUrl+trackingID+"&status=success"+
                            "&error_url="+termUrl+trackingID+"&status=fail";


            String saleReqLog =
                    "member=" + merchantId +
                            "&bid=" + bid +
                            "&product=" +product+
                            "&accountid=" + accountid +
                            "&cardsend=" + cardsend +
                            "&client_ip=" + ip +
                            "&action=" + action +
                            "&mode=" +mode+
                            "&price=" + price +
                            "&curr=" + currency +
                            "&product_name=" + product_name + //hardcoded produc-name
                            "&ccholder=" + ESAPI.encoder().encodeForURL(addressDetailsVO.getFirstname()) +  //customer firstName...
                            "&ccholder_lname=" +  ESAPI.encoder().encodeForURL(addressDetailsVO.getLastname()) + //  customer lastName...
                            "&email=" + addressDetailsVO.getEmail() +  //customer billing Email..
                            "&bill_street_1=" + addressDetailsVO.getStreet()+  //customer Shipping Street ..
                            "&bill_city=" + addressDetailsVO.getCity() +  //  shipping city..
                            // "&bill_state="+addressDetailsVO.getState()+
                            "&bill_country=" + addressDetailsVO.getCountry() + //shipping country...
                            "&bill_zip="+addressDetailsVO.getZipCode()+
                            "&bill_phone=" + addressDetailsVO.getPhone() +  // customer billing phone...
                            "&ccno=" + functions.maskingPan(cardDetailsVO.getCardNum())+ //cardnumber ...
                            "&ccvv=" + functions.maskingNumber(cardDetailsVO.getcVV()) + //cvv ..
                            "&month=" + functions.maskingNumber(cardDetailsVO.getExpMonth()) + // exp month of card..
                            "&year=" + functions.maskingNumber(cardDetailsVO.getExpYear())+
                            "&id_order="+trackingID+
                            "&notify_url="+notify_url+trackingID+
                            "&success_url="+termUrl+trackingID+"&status=success"+
                            "&error_url="+termUrl+trackingID+"&status=fail";


            transactionLogger.error("---sale request---" + saleReqLog);
            String saleRes = "";
            if (isTest)
            {
                transactionLogger.error("---inside isTest---" + RB.getString("CardSaleURL"));
                saleRes = DusPayUtils.doPostHTTPSURLConnectionClient(saleReq, RB.getString("CardSaleURL"));
            }
            else
            {
                transactionLogger.error("---inside isLive---" + RB.getString("CardSaleURL"));
                saleRes = DusPayUtils.doPostHTTPSURLConnectionClient(saleReq, RB.getString("CardSaleURL"));
            }
            transactionLogger.error("---saleRes---" + saleRes);

            if (functions.isValueNull(saleRes) && saleRes.contains("{"))
            {
                String transaction_id="";
                String status_nm="";
                String status="";
                String id_order="";
                String reason="";
                String amt="";
                String callbacks="";

                JSONObject jsonObject = new JSONObject(saleRes);

                if (jsonObject != null)
                {
                    if(!jsonObject.has("error"))
                    {
                        if (jsonObject.has("transaction_id"))
                        {
                            transaction_id = jsonObject.getString("transaction_id");
                        }
                        if (jsonObject.has("status_nm"))
                        {
                            status_nm = jsonObject.getString("status_nm");
                        }
                        if (jsonObject.has("status"))
                        {
                            status = jsonObject.getString("status");
                        }
                        if (jsonObject.has("price"))
                        {
                            price = jsonObject.getString("price");
                        }
                        if (jsonObject.has("id_order"))
                        {
                            id_order = jsonObject.getString("id_order");
                        }
                        if (jsonObject.has("reason"))
                        {
                            reason = jsonObject.getString("reason");
                        }
                        if (jsonObject.has("amt"))
                        {
                            amt = jsonObject.getString("amt");
                        }
                        if (jsonObject.has("callbacks"))
                        {
                            callbacks = jsonObject.getString("callbacks");

                        }

                        JSONObject jsonObject1 = jsonObject.getJSONObject("info");
                        if (jsonObject1 != null)
                        {
                            if (jsonObject1.has("accountid"))
                            {
                                accountid = jsonObject1.getString("accountid");
                            }
                            if (jsonObject1.has("mode"))
                            {
                                mode = jsonObject1.getString("mode");
                            }
                        }

                        transactionLogger.error("transaction_id =" + transaction_id + "" + "status_nm=" + status_nm + "" + "status=" + status + "" + "price=" + price + "" + "id_order=" + id_order + "" + "reason=" + reason + "" + "amt=" + amt + "" + "callbacks=" + callbacks + "" + "accountid=" + accountid + "" + "mode=" + mode);

                        if (((isTest)&& (status_nm.equalsIgnoreCase("1") && status.equalsIgnoreCase("Completed"))) || (status_nm.equalsIgnoreCase("9") && status.equalsIgnoreCase("Test Transaction")))
                        {
                            transactionLogger.error("inside isTest");
                            commResponseVO.setStatus("success");
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        }
                        else if ((!isTest) && ((status_nm.equalsIgnoreCase("1") && status.equalsIgnoreCase("Completed"))))
                        {
                            transactionLogger.error("Inside isLive");
                            commResponseVO.setStatus("success");
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        }
                        else
                        {
                            commResponseVO.setStatus("fail");
                        }
                        commResponseVO.setRemark(status);
                        commResponseVO.setDescription(status);
                        commResponseVO.setTransactionId(transaction_id);
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        commResponseVO.setIpaddress(ip);
                        commResponseVO.setAmount(transDetailsVO.getAmount());
                        commResponseVO.setCurrency(currency);
                        commResponseVO.setTmpl_Amount(tmpl_amount);
                        commResponseVO.setTmpl_Currency(tmpl_currency);
                        commResponseVO.setErrorCode(status_nm);
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        String error= jsonObject.getString("error");
                        commResponseVO.setRemark(error);
                        commResponseVO.setDescription(error);
                    }
                }
                else
                {
                    commResponseVO.setStatus("fail");
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DusPayCardPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO)throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("DusPayCardPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("---Entering into processInquiry---");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        PayMitcoResponseVO commResponseVO = new PayMitcoResponseVO();
        Functions functions = new Functions();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        transactionLogger.debug("trackingId in inquiry---"+transDetailsVO.getOrderId());
        TransactionManager transactionManager =new TransactionManager();
        TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(transDetailsVO.getOrderId());
        transactionLogger.debug("status is -----"+transactionDetailsVO.getStatus());
        String transaction_status=transactionDetailsVO.getStatus();


        try
        {
            String inquireReq =RB.getString("ECheckInquiryURL")+"?"+"transaction_id=" + commRequestVO.getTransDetailsVO().getPreviousTransactionId();
            transactionLogger.error("---inquiry Req---" + inquireReq);

            String inquireRes = DusPayUtils.doGetHttpConnection(inquireReq);
            transactionLogger.error("---inquiry Res---" + inquireRes);

            String resStatus = "";
            String transaction_id = "";
            String tdate = "";
            String descriptor = "";
            String amount ="";
            String curr ="";
            String mid ="";
            String status ="";
            String status_nm ="";
            String id_order ="";
            String error ="";

            if ((functions.isValueNull(inquireRes)) && inquireRes.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(inquireRes);
                if (jsonObject.has("status_nm"))
                {
                    status_nm = jsonObject.getString("status_nm");
                }
                if (jsonObject.has("status"))
                {
                    resStatus = jsonObject.getString("status");
                }
                if (jsonObject.has("amount"))
                {
                    amount = jsonObject.getString("amount");
                }
                if (jsonObject.has("transaction_id"))
                {
                    transaction_id = jsonObject.getString("transaction_id");
                }

                if (jsonObject.has("descriptor"))
                {
                    descriptor = jsonObject.getString("descriptor");
                    if (functions.isValueNull(descriptor)){
                        descriptor=GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    }
                }
                else
                {
                    descriptor=GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                }
                if (jsonObject.has("tdate"))
                {
                    tdate = jsonObject.getString("tdate");
                }
                if (jsonObject.has("curr"))
                {
                    curr = jsonObject.getString("curr");
                }
                if (jsonObject.has("mid"))
                {
                    mid = jsonObject.getString("mid");
                }
                if (jsonObject.has("id_order"))
                {
                    id_order = jsonObject.getString("id_order");
                }
                if (jsonObject.has("error"))
                {
                    error = jsonObject.getString("error");
                }

                transactionLogger.error("status_nm="+status_nm+""+"resStatus="+resStatus+""+"amount="+amount+""+"transaction_id="+transaction_id+""+"descriptor="+descriptor+""+"tdate="+tdate+""+"curr="+curr+""+"mid="+mid+""+"id_order="+id_order+"error="+error);

                if ((status_nm.equalsIgnoreCase("0") && resStatus.equalsIgnoreCase("Pending")) || (status_nm.equalsIgnoreCase("1") && resStatus.equalsIgnoreCase("Completed")) || (status_nm.equals("9")))
                {
                    status = "success";
                    commResponseVO.setTransactionId(transaction_id);
                    commResponseVO.setRemark(resStatus);
                    commResponseVO.setDescriptor(descriptor);
                    commResponseVO.setDescription(resStatus);
                    commResponseVO.setAmount(amount);
                    commResponseVO.setMerchantId(mid);
                    commResponseVO.setBankTransactionDate(tdate);
                    commResponseVO.setCurrency(curr);
                }
                else
                {
                    status = "fail";
                    commResponseVO.setRemark(error);
                    commResponseVO.setTransactionId("-");
                    commResponseVO.setDescription("Failed " + error);
                    commResponseVO.setMerchantId(merchantId);
                    commResponseVO.setAmount(transDetailsVO.getAmount());
                    commResponseVO.setCurrency(transDetailsVO.getCurrency());
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                }
                commResponseVO.setStatus(status);
                if (transaction_status.equalsIgnoreCase("capturesuccess"))
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                else
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());

                commResponseVO.setAuthCode("-");
                commResponseVO.setTransactionStatus(status);
            }
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DusPayCardPaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception while Inquiry", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays ()
    {
        return null;
    }


}
