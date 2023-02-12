package com.payment.DusPay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.vo.ReserveField2VO;
import com.payment.Enum.PZProcessType;
import com.payment.PayMitco.core.PayMitcoResponseVO;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Jitendra on 04-Dec-18.
 */
public class DusPayPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "DusPay";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.DusPay");
    private static Logger log = new Logger(DusPayPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(DusPayPaymentGateway.class.getName());

    public DusPayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO)throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("DusPayPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException,PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.debug("---Entering processSale of DusPayPaymentGateway---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions = new Functions();
        PayMitcoResponseVO commResponseVO = new PayMitcoResponseVO();

        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        ReserveField2VO reserveField2VO = commRequestVO.getReserveField2VO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

        String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String type = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String bid = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        boolean isTest = GatewayAccountService.getGatewayAccount(accountId).isTest();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency()))
        {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        String ip = "";
        if (functions.isValueNull(addressDetailsVO.getCardHolderIpAddress()))
        {
            ip = addressDetailsVO.getCardHolderIpAddress();
        }
        else
        {
            ip = addressDetailsVO.getIp();
        }

        String termUrl = "";
        transactionLogger.error("---host url----" + commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl())){
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            transactionLogger.error("---from host url---" + termUrl);
        }
        else{
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("---from Properties----" + termUrl);
        }
        // Dynamic Descriptor
        String descriptor = gatewayAccount.getDisplayName();
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
            descriptor = merchantTollFree + "-" + merchantSiteURL;
        }
        transactionLogger.error("descriptor ---"+descriptor);

        try
        {
            String saleReq =
                    "gid=" + merchantId +
                            "&bid=" +bid+
                            "&type=" + type+
                            "&memo="+ descriptor+      //displayName+
                            "&echecksend=curl" +
                            "&purchaser_echecknumber=161" +
                            "&client_ip=" + ip +
                            "&purchaser_firstname=" + addressDetailsVO.getFirstname() +
                            "&purchaser_lastname=" + addressDetailsVO.getLastname() +
                            "&purchaser_email=" + addressDetailsVO.getEmail() +
                            "&purchaser_phone=" + addressDetailsVO.getPhone() +
                            "&purchaser_address=" + addressDetailsVO.getStreet() +
                            "&purchaser_city=" + addressDetailsVO.getCity() +
                            "&purchaser_state=" + addressDetailsVO.getState() +
                            "&purchaser_zipcode=" + addressDetailsVO.getZipCode() +
                            "&transaction_amount=" + transDetailsVO.getAmount() +
                            "&purchaser_account=" + reserveField2VO.getAccountNumber() +
                            "&purchaser_routing=" + reserveField2VO.getRoutingNumber() +
                            "&bank_name="+ reserveField2VO.getBankName() +
                            "&bank_address=" +reserveField2VO.getBankAddress() +
                            "&bank_city=" +reserveField2VO.getBankCity() +
                            "&bank_state="+reserveField2VO.getBankState() +
                            "&bank_zipcode="+ reserveField2VO.getBankZipcode()+
                            "&notify_url="+ termUrl+trackingID+
                            "&id_order="+ trackingID;

            transactionLogger.error("---notify_URL/termURL---"+termUrl+trackingID);
            transactionLogger.error("---sale request---" + saleReq);
            String saleRes="";
            if(isTest)
            {
                transactionLogger.error("---inside isTest---" + RB.getString("ECheckSaleURL"));
                saleRes = DusPayUtils.doPostHTTPSURLConnectionClient(saleReq, RB.getString("ECheckSaleURL"));
            }
            else
            {
                transactionLogger.error("---inside isLive---" + RB.getString("ECheckSaleURL"));
                saleRes = DusPayUtils.doPostHTTPSURLConnectionClient(saleReq, RB.getString("ECheckSaleURL"));
            }
            transactionLogger.debug("---saleRes---"+saleRes);

            String status = "";
            String resStatus = "";
            String transaction_id = "";
            String bank_name = "";
            String purchaser_account = "";
            String transaction_amount = "";
            //String descriptor = "";
            String error = "";

            if ((functions.isValueNull(saleRes)) && saleRes.contains("{"))
            {
                String responseDescriptor="";
                JSONObject jsonObject = new JSONObject(saleRes);
                if (jsonObject.has("status"))
                {
                    resStatus = jsonObject.getString("status");
                    transactionLogger.debug("---resStatus---" + resStatus);
                }
                if (jsonObject.has("transaction_id"))
                {
                    transaction_id = jsonObject.getString("transaction_id");
                    transactionLogger.debug("---transaction_id---" + transaction_id);
                }
                if (jsonObject.has("bank_name"))
                {
                    bank_name = jsonObject.getString("bank_name");
                    transactionLogger.debug("---bank_name---" + bank_name);
                }

                if (jsonObject.has("purchaser_account"))
                {
                    purchaser_account = jsonObject.getString("purchaser_account");
                    transactionLogger.debug("---purchaser_account---" + purchaser_account);
                }
                if (jsonObject.has("transaction_amount"))
                {
                    transaction_amount = jsonObject.getString("transaction_amount");
                    transactionLogger.debug("---transaction_amount---" + transaction_amount);
                }
                if (jsonObject.has("descriptor"))
                {
                    responseDescriptor = jsonObject.getString("descriptor");
                    transactionLogger.debug("---responseDscriptor---" + responseDescriptor);
                }
                if (jsonObject.has("error"))
                {
                    error = jsonObject.getString("error");
                    transactionLogger.debug("---error---" + error);
                }
                commRequestVO.getTransDetailsVO().setPreviousTransactionId(transaction_id);
                commResponseVO= (PayMitcoResponseVO) this.processInquiry(commRequestVO);
                if (functions.isValueNull(commResponseVO.getStatus()) && commResponseVO.getStatus().equalsIgnoreCase("success"))
                {
                    transactionLogger.error("---inside if-----");
                    status = "success";
                    commResponseVO.setRemark(resStatus);
                    if (functions.isValueNull(responseDescriptor))
                    {
                        commResponseVO.setDescriptor(responseDescriptor);
                    }
                    else
                    {
                        commResponseVO.setDescriptor(descriptor);
                    }
                    commResponseVO.setDescription(resStatus);
                }
                else
                {
                    status = "fail";
                    commResponseVO.setRemark(error);
                    commResponseVO.setDescription("Failed " + error);
                }
                commResponseVO.setStatus(status);
                commResponseVO.setPaymitcoTransactionType("sale");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setIpaddress(ip);
                commResponseVO.setAccountNumber(purchaser_account);
                commResponseVO.setRoutingNumber(reserveField2VO.getRoutingNumber());
                commResponseVO.setAmount(transDetailsVO.getAmount());
                commResponseVO.setCurrency(currency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
                commResponseVO.setTransactionId(transaction_id);
                commResponseVO.setBankName(bank_name);
                commResponseVO.setBankAddress(reserveField2VO.getBankAddress());
                commResponseVO.setBankCity(reserveField2VO.getBankCity());
                commResponseVO.setBankState(reserveField2VO.getBankState());
                commResponseVO.setBankZipcode(reserveField2VO.getBankZipcode());
                commResponseVO.setCustomerId(commRequestVO.getCustomerId());
                commResponseVO.setTransactionID(transaction_id);
            }
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DusPayPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("---Entering into processInquery---");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        PayMitcoResponseVO commResponseVO = new PayMitcoResponseVO();
        Functions functions = new Functions();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        ReserveField2VO reserveField2VO = commRequestVO.getReserveField2VO();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();

        try
        {
            String inquireReq =RB.getString("ECheckInquiryURL")+"?"+"transaction_id=" + commRequestVO.getTransDetailsVO().getPreviousTransactionId();
            transactionLogger.debug("---inquireReq---"+inquireReq);

            String inquireRes = DusPayUtils.doGetHttpConnection(inquireReq);
            transactionLogger.debug("---res---" + inquireRes);

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
                    transactionLogger.debug("---status_nm---" + status_nm);
                }
                if (jsonObject.has("status"))
                {
                    resStatus = jsonObject.getString("status");
                    transactionLogger.debug("---resStatus---" + resStatus);
                }
                if (jsonObject.has("amount"))
                {
                    amount = jsonObject.getString("amount");
                    transactionLogger.debug("---amount---" + amount);
                }
                if (jsonObject.has("transaction_id"))
                {
                    transaction_id = jsonObject.getString("transaction_id");
                    transactionLogger.debug("---transaction_id---" + transaction_id);
                }

                if (jsonObject.has("descriptor"))
                {
                    descriptor = jsonObject.getString("descriptor");
                    if (functions.isValueNull(descriptor)){
                        descriptor=GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    }
                    transactionLogger.debug("---descriptor---" + descriptor);
                }
                else
                {
                    descriptor=GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                }
                if (jsonObject.has("tdate"))
                {
                    tdate = jsonObject.getString("tdate");
                    transactionLogger.debug("---tdate---" + tdate);
                }
                if (jsonObject.has("curr"))
                {
                    curr = jsonObject.getString("curr");
                    transactionLogger.debug("---curr---" + curr);
                }
                if (jsonObject.has("mid"))
                {
                    mid = jsonObject.getString("mid");
                    transactionLogger.debug("---mid---" + mid);
                }
                if (jsonObject.has("id_order"))
                {
                    id_order = jsonObject.getString("id_order");
                    transactionLogger.debug("---id_order---" + id_order);
                }
                if (jsonObject.has("error"))
                {
                    error = jsonObject.getString("error");
                    transactionLogger.debug("---error---" + error);
                }
                if ((status_nm.equalsIgnoreCase("0") && resStatus.equalsIgnoreCase("Pending")) || (status_nm.equalsIgnoreCase("1") && resStatus.equalsIgnoreCase("Completed")))
                {
                    transactionLogger.error("---inside sucess part-----");
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
                commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                commResponseVO.setAuthCode("-");
                commResponseVO.setTransactionStatus(status);
            }
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DusPayPaymentGateway.class.getName(), "processInquiry()", null, "common", "Technical Exception while Inquiry", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays ()
    {
        return null;
    }
}