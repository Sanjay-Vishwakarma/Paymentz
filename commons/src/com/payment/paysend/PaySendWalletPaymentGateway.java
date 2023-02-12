package com.payment.paysend;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;

import java.util.ResourceBundle;

/**
 * Created by Admin on 6/10/2019.
 */
public class PaySendWalletPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PaySendWalletPaymentGateway.class.getName());

    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.paysend");

    public static final String GATEWAY_TYPE = "pswallet";

    public PaySendWalletPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public String getMaxWaitDays() {   return null; }

    public static void main(String[] args) {}

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processSale-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();

        String projectId = gatewayAccount.getMerchantId();

        try
        {

            commResponseVO.setRedirectUrl(RB.getString("TOP_UP"));
            commResponseVO.setStatus("Pending");

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }

        return commResponseVO;
    }

    @Override
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        CommRequestVO commRequestVO = null;
        String html = "";
        PaySendUtils paySendUtils= new PaySendUtils();
        Comm3DResponseVO transRespDetails = null;
        commRequestVO = paySendUtils.getPaySendRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();

        String projectId = gatewayAccount.getMerchantId();

        try
        {
            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);

            if (transRespDetails != null)
            {
                html = PaySendUtils.getPaySendWalletForm(commonValidatorVO,transRespDetails,projectId);
            }

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception ------- "+e);
        }

        transactionLogger.error("html-----------"+html);
        return html;
    }

}