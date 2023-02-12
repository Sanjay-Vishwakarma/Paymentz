package com.payment.TWDTaiwan;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Vivek on 12/23/2020.
 */
public class TWDTaiwanPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger=new TransactionLogger(TWDTaiwanPaymentGateway.class.getName());
    public static final String GATEWAY_TYPE="TWDTaiwan";
    private  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.TWDTaiwan");
    public TWDTaiwanPaymentGateway(String accountId){this.accountId=accountId;}
    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("--- Inside TWDTaiwanPaymentGateway ---");
        Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
        CommRequestVO commRequestVO= (CommRequestVO) requestVO;
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HHmmss");
        Functions functions=new Functions();

        String paymentUrl="";
        String firstName="";
        String lastName="";
        String city="";
        String state="";
        String country="";
        String street="";
        String zip="";
        String termUrl="";
        String notifyUrl="";
        String hash="";
        String card_type="";
        String phone="";
        String customerIp="";
        String timestamp=simpleDateFormat.format(new Date());

        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String sid=gatewayAccount.getMerchantId();
        String rcode=gatewayAccount.getPassword();
        boolean isTest=gatewayAccount.isTest();
        if(isTest)
            paymentUrl=RB.getString("TEST_PAYMENT_URL");
        else
            paymentUrl=RB.getString("LIVE_PAYMENT_URL");
        try
        {
            if(functions.isValueNull(commMerchantVO.getHostUrl()))
                termUrl="https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingID;
            else
                termUrl=RB.getString("TERM_URL")+trackingID;
            notifyUrl=RB.getString("NOTIFY_URL")+trackingID;

            if(functions.isValueNull(addressDetailsVO.getFirstname()))
                firstName= ESAPI.encoder().encodeForURL(addressDetailsVO.getFirstname());
            if(functions.isValueNull(addressDetailsVO.getLastname()))
                lastName= ESAPI.encoder().encodeForURL(addressDetailsVO.getLastname());
            if(functions.isValueNull(addressDetailsVO.getStreet()))
                street= ESAPI.encoder().encodeForURL(addressDetailsVO.getStreet());
            if(functions.isValueNull(addressDetailsVO.getCity()))
                city= ESAPI.encoder().encodeForURL(addressDetailsVO.getCity());
            if(functions.isValueNull(addressDetailsVO.getState()))
                state= ESAPI.encoder().encodeForURL(addressDetailsVO.getState());
            zip= addressDetailsVO.getZipCode();
            country= addressDetailsVO.getCountry();
            customerIp= addressDetailsVO.getCardHolderIpAddress();
            if (functions.isValueNull(addressDetailsVO.getPhone()))
            {
                phone = addressDetailsVO.getPhone();
                if(phone.contains("+"))
                    phone=phone.replaceAll("\\+","");
            }
            String cardType=GatewayAccountService.getCardType(transactionDetailsVO.getCardType());
            transactionLogger.error("Card type-->"+cardType);
            card_type=TWDTaiwanUtils.getCardType(GatewayAccountService.getCardType(transactionDetailsVO.getCardType()));
            hash=TWDTaiwanUtils.generateHash(sid+timestamp+transactionDetailsVO.getAmount()+transactionDetailsVO.getCurrency()+rcode);
            StringBuffer json=new StringBuffer();
            StringBuffer jsonLog=new StringBuffer();
            json.append("{\"sid\" :\""+sid+"\",\"tid\" :\""+trackingID+"\",\"postback_url\" :\""+notifyUrl+"\",\"redirect_url\" :\""+termUrl+"\",\"hash\" :\""+hash+"\",\"timestamp\" :\""+timestamp+"\"");
            jsonLog.append("{\"sid\" :\""+sid+"\",\"tid\" :\""+trackingID+"\",\"postback_url\" :\""+notifyUrl+"\",\"redirect_url\" :\""+termUrl+"\",\"hash\" :\""+hash+"\",\"timestamp\" :\""+timestamp+"\"");
            if("TWD".equalsIgnoreCase(cardType))
            {
                json.append(",\"card_type\" :\"allcard\"");
                jsonLog.append(",\"card_type\" :\"allcard\"");
            }else
            {
                json.append(",\"card_no\" :\""+commCardDetailsVO.getCardNum()+"\",\"card_exp_year\" :\""+commCardDetailsVO.getExpYear()+"\",\"card_exp_month\" :\""+commCardDetailsVO.getExpMonth()+"\",\"card_ccv\" :\""+commCardDetailsVO.getcVV()+"\",\"tx_action\" :\"PAYMENT\"");
                jsonLog.append(",\"card_no\" :\""+functions.maskingPan(commCardDetailsVO.getCardNum())+"\",\"card_exp_year\" :\""+functions.maskingNumber(commCardDetailsVO.getExpYear())+"\",\"card_exp_month\" :\""+functions.maskingNumber(commCardDetailsVO.getExpMonth())+"\",\"card_ccv\" :\""+functions.maskingNumber(commCardDetailsVO.getcVV())+"\",\"tx_action\" :\"PAYMENT\"");
            }
            json.append(",\"firstname\" :\""+firstName+"\",\"lastname\" :\"" + lastName + "\",\"phone\" :\""+phone+"\",\"email\" :\""+addressDetailsVO.getEmail()+"\",\"address\" :\""+street+"\",\"suburb_city\" :\""+city+"\",\"state\" :\""+state+"\",\"postcode\" :\""+zip+"\",\"country\" :\""+country+"\",\"uip\" :\""+customerIp+"\",\"currency\" :\""+transactionDetailsVO.getCurrency()+"\",\"amount_shipping\" :\"0.00\",\"amount_coupon\" :\"0.00\",\"amount_tax\" :\"0.00\",\"item_quantity[]\" :\"1\",\"item_name[]\" :\"Test\",\"item_no[]\" :\""+trackingID+"_1\",\"item_desc[]\" :\""+transactionDetailsVO.getOrderId()+"\",\"item_amount_unit[]\" :\""+transactionDetailsVO.getAmount()+"\"}");
            jsonLog.append(",\"firstname\" :\""+firstName+"\",\"lastname\" :\"" + lastName + "\",\"phone\" :\""+phone+"\",\"email\" :\""+addressDetailsVO.getEmail()+"\",\"address\" :\""+street+"\",\"suburb_city\" :\""+city+"\",\"state\" :\""+state+"\",\"postcode\" :\""+zip+"\",\"country\" :\""+country+"\",\"uip\" :\""+customerIp+"\",\"currency\" :\""+transactionDetailsVO.getCurrency()+"\",\"amount_shipping\" :\"0.00\",\"amount_coupon\" :\"0.00\",\"amount_tax\" :\"0.00\",\"item_quantity[]\" :\"1\",\"item_name[]\" :\"Test\",\"item_no[]\" :\""+trackingID+"_1\",\"item_desc[]\" :\""+transactionDetailsVO.getOrderId()+"\",\"item_amount_unit[]\" :\""+transactionDetailsVO.getAmount()+"\"}");
            transactionLogger.error("jsonLog--->"+jsonLog);
            comm3DResponseVO.setStatus("pending3DConfirmation");
            comm3DResponseVO.setUrlFor3DRedirect(paymentUrl);
            comm3DResponseVO.setPaReq(json.toString());
        }
        catch (EncodingException e)
        {
            transactionLogger.error("EncodingException---"+trackingID+"-->", e);
        }

        return comm3DResponseVO;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlweavePaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);

    }
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(":::::Entered into processAutoRedirect for TWDTaiwan:::::");
        CommRequestVO commRequestVO = null;
        Functions functions=new Functions();
        String html = "";
        String trackingID=commonValidatorVO.getTrackingid();
        try
        {
            commRequestVO=TWDTaiwanUtils.getTaiwanRequestVO(commonValidatorVO);
            transactionLogger.error("--- Inside TWDTaiwanPaymentGateway ---");
            Comm3DResponseVO comm3DResponseVO=new Comm3DResponseVO();
            CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
            CommAddressDetailsVO addressDetailsVO=commRequestVO.getAddressDetailsVO();
            CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HHmmss");

            String paymentUrl="";
            String firstName="";
            String lastName="";
            String city="";
            String state="";
            String country="";
            String street="";
            String zip="";
            String termUrl="";
            String notifyUrl="";
            String hash="";
            String phone="";
            String orderDesc="";
            String timestamp=simpleDateFormat.format(new Date());

            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            String sid=gatewayAccount.getMerchantId();
            String rcode=gatewayAccount.getPassword();
            boolean isTest=gatewayAccount.isTest();
            if(isTest)
                paymentUrl=RB.getString("TEST_PAYMENT_URL");
            else
                paymentUrl=RB.getString("LIVE_PAYMENT_URL");

            if(functions.isValueNull(commMerchantVO.getHostUrl()))
                termUrl="https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingID;
            else
                termUrl=RB.getString("TERM_URL")+trackingID;
            notifyUrl=RB.getString("NOTIFY_URL")+trackingID;
            if(functions.isValueNull(addressDetailsVO.getFirstname()))
                firstName= ESAPI.encoder().encodeForURL(addressDetailsVO.getFirstname());
            if(functions.isValueNull(addressDetailsVO.getLastname()))
                lastName= ESAPI.encoder().encodeForURL(addressDetailsVO.getLastname());
            if(functions.isValueNull(addressDetailsVO.getStreet()))
                street= ESAPI.encoder().encodeForURL(addressDetailsVO.getStreet());
            if(functions.isValueNull(addressDetailsVO.getCity()))
                city= ESAPI.encoder().encodeForURL(addressDetailsVO.getCity());
            if(functions.isValueNull(addressDetailsVO.getState()))
                state= ESAPI.encoder().encodeForURL(addressDetailsVO.getState());
            zip= addressDetailsVO.getZipCode();
            country= addressDetailsVO.getCountry();
            if(functions.isValueNull(transactionDetailsVO.getOrderDesc()))
                orderDesc=transactionDetailsVO.getOrderDesc();
            if (functions.isValueNull(addressDetailsVO.getPhone()))
            {
                phone = addressDetailsVO.getPhone();
                if(phone.contains("+"))
                    phone=phone.replaceAll("\\+","");
            }
            hash=TWDTaiwanUtils.generateHash(sid+timestamp+transactionDetailsVO.getAmount()+transactionDetailsVO.getCurrency()+rcode);

            StringBuffer form = new StringBuffer("<form action=\""+paymentUrl+"\" method=\"post\" name=\"paymentForm\">");
            form.append("<input type=\"hidden\" name=\"sid\" value=\""+sid+"\">");
            form.append("<input type=\"hidden\" name=\"tid\" value=\""+trackingID+"\">");
            form.append("<input type=\"hidden\" name=\"postback_url\" value=\""+notifyUrl+"\">");
            form.append("<input type=\"hidden\" name=\"redirect_url\" value=\""+termUrl+"\">");
            form.append("<input type=\"hidden\" name=\"hash\" value=\""+hash+"\">");
            form.append("<input type=\"hidden\" name=\"timestamp\" value=\""+timestamp+"\">");
            form.append("<input type=\"hidden\" name=\"card_type\" value=\"allcard\">");
            form.append("<input type=\"hidden\" name=\"firstname\" value=\""+firstName+"\">");
            form.append("<input type=\"hidden\" name=\"lastname\" value=\""+lastName+"\">");
            form.append("<input type=\"hidden\" name=\"phone\" value=\""+phone+"\">");
            form.append("<input type=\"hidden\" name=\"email\" value=\""+addressDetailsVO.getEmail()+"\">");
            form.append("<input type=\"hidden\" name=\"address\" value=\""+street+"\">");
            form.append("<input type=\"hidden\" name=\"suburb_city\" value=\""+city+"\">");
            form.append("<input type=\"hidden\" name=\"state\" value=\""+state+"\">");
            form.append("<input type=\"hidden\" name=\"postcode\" value=\""+zip+"\">");
            form.append("<input type=\"hidden\" name=\"country\" value=\""+country+"\">");
            form.append("<input type=\"hidden\" name=\"currency\" value=\""+transactionDetailsVO.getCurrency()+"\">");
            form.append("<input type=\"hidden\" name=\"amount_shipping\" value=\"0.00\">");
            form.append("<input type=\"hidden\" name=\"amount_coupon\" value=\"0.00\">");
            form.append("<input type=\"hidden\" name=\"amount_tax\" value=\"0.00\">");
            form.append("<input type=\"hidden\" name=\"item_quantity[]\" value=\"1\">");
            form.append("<input type=\"hidden\" name=\"item_name[]\" value=\""+transactionDetailsVO.getOrderId()+"\">");
            form.append("<input type=\"hidden\" name=\"item_no[]\" value=\""+trackingID+"_1\">");
            form.append("<input type=\"hidden\" name=\"item_desc[]\" value=\""+orderDesc+"\">");
            form.append("<input type=\"hidden\" name=\"item_amount_unit[]\" value=\""+transactionDetailsVO.getAmount()+"\">");
            form.append("</form>");
            form.append("<script>document.paymentForm.submit();</script>");

            comm3DResponseVO.setStatus("pending3DConfirmation");
            comm3DResponseVO.setUrlFor3DRedirect(form.toString());

            transactionLogger.error("form---->"+form);

            html=form.toString();
        }
        catch (EncodingException e)
        {
            transactionLogger.error("EncodingException---"+trackingID+"-->", e);
        }
        return html;
    }
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("--- inside processQuery() ---");
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommRequestVO commRequestVO=(CommRequestVO) requestVO;
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();

        String inquiryUrl="";
        String transactionId=transDetailsVO.getPreviousTransactionId();
        String hash="";
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String sid=gatewayAccount.getMerchantId();
        String rcode=gatewayAccount.getPassword();
        String status="";
        String parent_txid="";
        String txid="";
        String tx_action="";
        String amount=transDetailsVO.getAmount();
        String currency=transDetailsVO.getCurrency();
        String error_msg="";
        String code="";
        boolean isTest=gatewayAccount.isTest();
        try
        {
            hash = TWDTaiwanUtils.generateHash(sid + rcode);
            if (isTest)
                inquiryUrl = RB.getString("TEST_INQUIRY_URL");
            else
                inquiryUrl = RB.getString("LIVE_INQUIRY_URL");

            inquiryUrl += transactionId + "/getstatus?hash=" + hash;
            transactionLogger.error("inquiryUrl----" + trackingID + "--->" + inquiryUrl);
            String response = TWDTaiwanUtils.doHttpPostConnection(inquiryUrl, "");
            transactionLogger.error("Inquiry response---" + trackingID + "--->" + response);
            if (functions.isValueNull(response))
            {
                JSONObject responseJSON=new JSONObject(response);
                if(responseJSON.has("status"))
                    status=responseJSON.getString("status");
                if(responseJSON.has("parent_txid"))
                    parent_txid=responseJSON.getString("parent_txid");
                if(responseJSON.has("txid"))
                    txid=responseJSON.getString("txid");
                if(responseJSON.has("tx_action"))
                    tx_action=responseJSON.getString("tx_action");
                if(responseJSON.has("amount"))
                    amount=responseJSON.getString("amount");
                if(responseJSON.has("currency"))
                    currency=responseJSON.getString("currency");
                if(responseJSON.has("error_msg"))
                    error_msg=responseJSON.getString("error_msg");
                if(responseJSON.has("code"))
                    code=responseJSON.getString("code");
                if("OK".equalsIgnoreCase(status) && "SETTLEMENT".equalsIgnoreCase(tx_action))
                {
                    commResponseVO.setTransactionStatus("success");
                    commResponseVO.setStatus("success");
                    if(!functions.isValueNull(error_msg))
                        error_msg="Transaction successful";
                }else if("OK".equalsIgnoreCase(status) && "PREAUTH".equalsIgnoreCase(tx_action))
                {
                    commResponseVO.setTransactionStatus("failed");
                    commResponseVO.setStatus("failed");
                    if(!functions.isValueNull(error_msg))
                        error_msg="Transaction failed";
                }
                else if(!"500".equalsIgnoreCase(code))
                {
                    commResponseVO.setStatus("failed");
                    commResponseVO.setTransactionStatus("failed");

                    if(!functions.isValueNull(error_msg))
                        error_msg="Transaction failed";
                }
                commResponseVO.setRemark(error_msg);
                commResponseVO.setDescription(error_msg);
                commResponseVO.setAmount(amount);
                commResponseVO.setAuthCode(code);
                commResponseVO.setTransactionId(transactionId);
                commResponseVO.setCurrency(currency);
                commResponseVO.setTransactionType(tx_action);
                commResponseVO.setMerchantId(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException---"+trackingID+"--->",e);
        }
        return commResponseVO;
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
