package com.payment.elegro;

import com.directi.pg.ElegroLogger;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONObject;

import java.util.ResourceBundle;

/**
 * Created by Admin on 1/11/2019.
 */
public class ElegroPaymentGateway extends AbstractPaymentGateway
{
    //ElegroLogger transactionLogger= new ElegroLogger(ElegroPaymentGateway.class.getName());
    TransactionLogger transactionLogger= new TransactionLogger(ElegroPaymentGateway.class.getName());
    public static final  String GATEWAY_TYPE="elegro";
    final  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.elegro");

    public ElegroPaymentGateway(String accountid){this.accountId=accountid;};

    public static void main(String[] args)
    {
        try
        {
            Functions functions =new Functions();
            JSONObject payoutRequest = new JSONObject();

            payoutRequest.put("amount", "200");
            payoutRequest.put("email", "245");
            payoutRequest.put("address", "65465");
            payoutRequest.put("coinType", "658");
            payoutRequest.put("currency", "656");

            System.out.println(" req --- "+payoutRequest.toString());
            System.out.println(" payoutRequest.toString() -- "+functions.isValueNull(payoutRequest.toString()));
            System.out.println(" payoutRequest.toString().contains() -- "+payoutRequest.toString().contains("{"));

            if (functions.isValueNull(payoutRequest.toString()) && payoutRequest.toString().contains("{"))
            {
                JSONObject jsonobj1 = new JSONObject(payoutRequest.toString());
                if (jsonobj1.has("amount") && jsonobj1.getString("amount").equals("200"))
                {
                    System.out.println("amt --- "+jsonobj1.getString("amount"));
                    System.out.println("amt --- "+jsonobj1.getString("amount").equals("200"));
                }
            }

        }
        catch (Exception e){
            System.out.println("exception ---- "+e);
        }
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug(" ------- inside processSale -------");
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        ElegroResponseVO commResponseVO= new ElegroResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);

        try
        {
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

            transactionLogger.error("AMPUNT ----- "+commTransactionDetailsVO.getAmount());
            String amount=commTransactionDetailsVO.getAmount();
            String cuurency=commTransactionDetailsVO.getCurrency();
            String publicKey=gatewayAccount.getFRAUD_FTP_USERNAME();

            commResponseVO.setAmount(amount);
            commResponseVO.setCurrency(cuurency);
            commResponseVO.setPublickey(publicKey);
            commResponseVO.setMerchantOrderId(trackingID);
            commResponseVO.setRedirectUrl(termUrl+trackingID);

            transactionLogger.debug("amt------"+commResponseVO.getAmount());
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }


    @Override
    public GenericResponseVO processPayout(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.debug(" ------- inside processPayout -------");
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        ElegroResponseVO commResponseVO= new ElegroResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();

        String privateKey = gatewayAccount.getFRAUD_FTP_PASSWORD();
        transactionLogger.error("num ---- "+privateKey);

        try
        {
            transactionLogger.debug("amount----"+commTransactionDetailsVO.getAmount());
            transactionLogger.debug("Email----"+ genericAddressDetailsVO.getEmail());
            transactionLogger.debug("address----"+ commTransactionDetailsVO.getWalletId());
            transactionLogger.debug("CoinType----"+commTransactionDetailsVO.getWalletCurrency());
            transactionLogger.debug("currency----"+commTransactionDetailsVO.getCurrency());

            JSONObject payoutRequest = new JSONObject();

            payoutRequest.put("amount" ,   commTransactionDetailsVO.getAmount());
            payoutRequest.put("email" ,    genericAddressDetailsVO.getEmail());
            payoutRequest.put("address" ,  commTransactionDetailsVO.getWalletId());
            payoutRequest.put("coinType" , commTransactionDetailsVO.getWalletCurrency());
            payoutRequest.put("currency" , commTransactionDetailsVO.getCurrency());

            transactionLogger.error("payoutRequest ------- "+payoutRequest.toString());

            String payoutResponse = "";
            if (isTest)
            {
                payoutResponse = ElegroUtils.doPostHTTPSURLConnectionClient(RB.getString("PAYOUT_TEST") , payoutRequest.toString() ,privateKey);
            }
            else
            {
                payoutResponse = ElegroUtils.doPostHTTPSURLConnectionClient(RB.getString("PAYOUT_LIVE") , payoutRequest.toString() ,privateKey);
            }

            transactionLogger.error("payoutResponse ------ " + payoutResponse);

            if (functions.isValueNull(payoutResponse) && payoutResponse.contains("{"))
            {
                JSONObject jsonobj1 = new JSONObject(payoutResponse.toString());
                if (jsonobj1 != null)
                {
                    //in case of error
                    if (jsonobj1.has("statusCode"))
                    {
                        commResponseVO.setStatus("failed");
                    }

                    if (jsonobj1.has("message"))
                    {
                        commResponseVO.setRemark(jsonobj1.getString("message"));
                        commResponseVO.setDescription(jsonobj1.getString("message"));
                    }

                    if(jsonobj1.has("tx_ref_code"))
                    {
                        commResponseVO.setTransactionId(jsonobj1.getString("tx_ref_code"));

                    }
                    if(jsonobj1.has("sender"))
                    {
                        JSONObject jsonObjSender=jsonobj1.getJSONObject("sender");
                        if(jsonObjSender.has("currency"))
                        {
                            commResponseVO.setCurrency(jsonObjSender.getString("currency"));
                        }
                        if(jsonObjSender.has("amount_minus_fees"))
                        {
                            commResponseVO.setAmount(jsonObjSender.getString("amount_minus_fees"));
                        }
                    }

                    if(jsonobj1.has("receiver"))
                    {
                        JSONObject jsonObjReceiver=jsonobj1.getJSONObject("receiver");
                        if(jsonObjReceiver.has("currency"))
                        {
                            commResponseVO.setWalletCurrecny(jsonObjReceiver.getString("currency"));
                        }
                        if(jsonObjReceiver.has("amount"))
                        {
                            commResponseVO.setWalletAmount(jsonObjReceiver.getString("amount"));
                        }
                        if(jsonObjReceiver.has("address"))
                        {
                            commResponseVO.setWalletId(jsonObjReceiver.getString("address"));
                        }
                        commResponseVO.setStatus("success");
                        commResponseVO.setDescription("SYS: Successful");
                        commResponseVO.setRemark("SYS: Transaction Successful");
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                }
            }

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }


    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.debug(" ------- inside processInquiry -------");
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        ElegroResponseVO commResponseVO= new ElegroResponseVO();
        Functions functions = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);

        try
        {
            transactionLogger.debug("amt------"+commResponseVO.getAmount());
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }

    @Override
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {

        CommRequestVO commRequestVO = null;
        String html = "";
        ElegroUtils elegroUtils = new ElegroUtils();
        ElegroResponseVO transRespDetails = null;
        commRequestVO = elegroUtils.getElegroRequest(commonValidatorVO);
        transRespDetails = (ElegroResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        if(transRespDetails!=null){
            html= elegroUtils.getAutoSubmitForm(transRespDetails,gatewayAccount.isTest());
        }

        transactionLogger.error("html-----------"+html);
        return html;

    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
