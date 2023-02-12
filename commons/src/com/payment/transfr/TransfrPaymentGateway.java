package com.payment.transfr;

import com.directi.pg.AuditTrailVO;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.google.gson.JsonObject;
import com.manager.PaymentManager;
import com.payment.Enum.PZProcessType;
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
import java.util.ResourceBundle;

/**
 * Created by admin on 27-Apr-22.
 */
public class TransfrPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "transfr";
    private static TransactionLogger transactionLogger = new TransactionLogger(TransfrPaymentGateway.class.getName());
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.transfrpay");

    public TransfrPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processSale of TransfrPaymentGateway -----");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        try
        {
            String authToken = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            boolean isTest = gatewayAccount.isTest();
            String saleURL = "";
            String amount = String.valueOf((int) ((Double.parseDouble(transDetailsVO.getAmount())) * 100));
            String currency = transDetailsVO.getCurrency().toLowerCase();
            String returnURL = RB.getString("returnURL") + trackingID;

            if (isTest)
            {
                saleURL = RB.getString("TEST_SALE_URL");
            }
            else
            {
                saleURL = RB.getString("LIVE_SALE_URL");
            }

            transactionLogger.error("saleURL ===== " + saleURL);

            JsonObject jsonRequest = new JsonObject();
            jsonRequest.addProperty("amount", amount);
            jsonRequest.addProperty("currency", currency);
            jsonRequest.addProperty("return_url", returnURL);

            transactionLogger.error("Transfr Sale Request for ===== " + trackingID + " ===== " + jsonRequest);

            String response = TransfrUtils.doPostHTTPSURLConnectionClient(saleURL, jsonRequest.toString(), authToken);

            transactionLogger.error("Transfr Sale Response for ===== " + trackingID + " ===== " + response);


            if (functions.isJSONValid(response))
            {
                JSONObject jsonResponse = new JSONObject(response);

                if(jsonResponse.has("data") && jsonResponse.getJSONObject("data") != null)
                {
                    JSONObject jsonDataObject = jsonResponse.getJSONObject("data");

                    String status = "";
                    String paymentUrl = "";
                    String paymentid = "";
                    String confirmedStatus = "";

                    if(jsonDataObject.has("status") && functions.isValueNull(jsonDataObject.getString("status")))
                    {
                        status = jsonDataObject.getString("status");
                    }
                    if(jsonDataObject.has("payment_url") && functions.isValueNull(jsonDataObject.getString("payment_url")))
                    {
                        paymentUrl = jsonDataObject.getString("payment_url");
                    }
                    if(jsonDataObject.has("intent_id") && functions.isValueNull(jsonDataObject.getString("intent_id")))
                    {
                        paymentid = jsonDataObject.getString("intent_id");
                    }
                    if(jsonDataObject.has("confirmed_status") && functions.isValueNull(jsonDataObject.getString("confirmed_status")))
                    {
                        confirmedStatus = jsonDataObject.getString("confirmed_status");
                    }

                    transactionLogger.error("For " + trackingID + "sale response ---- status == " + status + ", paymentUrl == " + paymentUrl + ", paymentid == " + paymentid + ", confirmedStatus == " + confirmedStatus);

                    if("PENDING_PAYMENT".equalsIgnoreCase(status) && functions.isValueNull(paymentUrl))
                    {
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setUrlFor3DRedirect(paymentUrl);
                        commResponseVO.setRedirectUrl(paymentUrl);
                        commResponseVO.setDescription(status);
                        commResponseVO.setRemark(status);
                    }
                    else if("FAILED".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescription(status);
                        commResponseVO.setRemark(status);
                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setDescription(status);
                        commResponseVO.setRemark(status);
                    }

                    TransfrUtils.updatePaymentId(trackingID, paymentid);
                    commResponseVO.setTransactionId(paymentid);
                }
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setTransactionStatus("pending");
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException ===== ", e);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception ===== ", e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside TransferPaymentGateway processQuery ---");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String authToken = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();

        boolean isTest = gatewayAccount.isTest();
        String inquiryUrl = "";
        String paymentid = commTransactionDetailsVO.getPreviousTransactionId();

        try
        {
            if (isTest)
            {
                inquiryUrl = RB.getString("TEST_INQUIRY_URL") + paymentid;
            }
            else
            {
                inquiryUrl = RB.getString("LIVE_INQUIRY_URL") + paymentid;
            }

            transactionLogger.error("TransfrPaymentGateway Inquiry Request for ==== " + trackingID + " ===== " + inquiryUrl);

            String response = TransfrUtils.doGetHTTPSURLConnectionClient(inquiryUrl, authToken);

            transactionLogger.error("TransfrPaymentGateway Inquiry response ==== " + trackingID + " ===== " + response);

            if (functions.isJSONValid(response))
            {
                JSONObject jsonResponse = new JSONObject(response);

                if(jsonResponse.has("data") && jsonResponse.getJSONObject("data") != null)
                {
                    JSONObject jsonDataObject = jsonResponse.getJSONObject("data");

                    String status = "";
                    String createdAt = "";
                    String responsePaymentid = "";
                    String confirmedStatus = "";
                    String amount = "";

                    if(jsonDataObject.has("status") && functions.isValueNull(jsonDataObject.getString("status")))
                    {
                        status = jsonDataObject.getString("status");
//                        status = "SUCCESS";
                    }
                    if(jsonDataObject.has("created_at") && functions.isValueNull(jsonDataObject.getString("created_at")))
                    {
                        createdAt = jsonDataObject.getString("created_at");
                    }
                    if(jsonDataObject.has("intent_id") && functions.isValueNull(jsonDataObject.getString("intent_id")))
                    {
                        responsePaymentid = jsonDataObject.getString("intent_id");
                    }
                    if(jsonDataObject.has("confirmed_status") && functions.isValueNull(jsonDataObject.getString("confirmed_status")))
                    {
                        confirmedStatus = jsonDataObject.getString("confirmed_status");
                    }
                    if(jsonDataObject.has("amount") && functions.isValueNull(jsonDataObject.getString("amount")))
                    {
                        amount = jsonDataObject.getString("amount");
                    }

                    transactionLogger.error("For " + trackingID + " inquiry response ---- status == " + status + ", createdAt == " + createdAt + ", responsePaymentid == " + responsePaymentid + ", confirmedStatus == " + confirmedStatus);

                    if("SUCCESS".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("Success");
                        commResponseVO.setTransactionStatus("Success");
                        commResponseVO.setRemark(status);
                        commResponseVO.setDescription(status);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        commResponseVO.setBankTransactionDate(createdAt);
                        commResponseVO.setResponseTime(createdAt);
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                        if(functions.isValueNull(responsePaymentid))
                            commResponseVO.setTransactionId(responsePaymentid);
                    }
                    else if("FAILED".equalsIgnoreCase(status))
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                        commResponseVO.setDescription(status);
                        commResponseVO.setAmount(amount);
                        commResponseVO.setRemark(status);
                        commResponseVO.setBankTransactionDate(createdAt);
                        commResponseVO.setResponseTime(createdAt);
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());

                        if(functions.isValueNull(responsePaymentid))
                            commResponseVO.setTransactionId(responsePaymentid);
                    }
                    else
                    {
                        commResponseVO.setStatus("pending");
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                        commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                        commResponseVO.setTransactionStatus("pending");
                        commResponseVO.setBankTransactionDate(createdAt);
                        commResponseVO.setResponseTime(createdAt);
                    }
                }
            }
            else
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setRemark("Your Transaction is Pending Please check the status after some time");
                commResponseVO.setTransactionStatus("pending");
            }

        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException ===== ", e);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception ===== ", e);
        }
        return commResponseVO;
    }

    public String  processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(" ---- TransfrPaymentGateway Autoredict in processAutoRedirect ---- ");
        String html = "";;
        Comm3DResponseVO transRespDetails = null;
        TransfrUtils transfrUtils =new TransfrUtils();
        CommRequestVO commRequestVO = transfrUtils.getTransfrRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        TransfrPaymentProcess transfrPaymentProcess = new TransfrPaymentProcess();
        PaymentManager paymentManager = new PaymentManager();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        auditTrailVO.setActionExecutorName("CustomerCheckout");

        boolean isTest = gatewayAccount.isTest();
        try
        {
            transactionLogger.error("isService Flag ----- " + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionLogger.error("autoredirect  flag----- " + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionLogger.error("addressdetail  flag----- " + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionLogger.error("terminal id  is ----- " + commonValidatorVO.getTerminalId());
            transactionLogger.error("tracking id  is ----- " + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = transfrPaymentProcess.get3DConfirmationForm(commonValidatorVO,transRespDetails) ;
                transactionLogger.error("automatic redirect wealthpay form -- >> "+html);
            }
            else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("fail"))
            {
                paymentManager.updateTransactionForCommon(transRespDetails, "authfailed", commonValidatorVO.getTrackingid(), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                html="failed";
            }

        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in TapMioPaymentGateway--- ", e);
        }
        return html;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("Transfr","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
