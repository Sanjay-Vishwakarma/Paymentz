package com.payment.hdfc;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

import java.util.ResourceBundle;

/**
 * Created by Admin on 2022-03-08.
 */
public class HDFCPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE             = "HDFC";
    final static ResourceBundle RB                      = LoadProperties.getProperty("com.directi.pg.HDFCPaymentGateway");
    private static TransactionLogger transactionLogger  = new TransactionLogger(HDFCPaymentGateway.class.getName());

    public HDFCPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("HDFCPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Inside processSale FOR hdfc payment gateway---");

        CommRequestVO commRequestVO                     = (CommRequestVO) requestVO;
        Comm3DResponseVO comm3DResponseVO               = new Comm3DResponseVO();
        Functions functions                             = new Functions();
        GenericTransDetailsVO genericTransDetailsVO     = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO       = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO                   = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount                   = GatewayAccountService.getGatewayAccount(accountId);

        String merchantId       = gatewayAccount.getMerchantId();
        String id               = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password         = gatewayAccount.getFRAUD_FTP_PASSWORD();
        String EncryptionKey    = gatewayAccount.getCHARGEBACK_FTP_PATH();
        String is3DSupported    = gatewayAccount.get_3DSupportAccount();
        boolean isTest          = gatewayAccount.isTest();

        String action        = "";
        String langid        = "";
        String REQUEST_URL   = "";
        String RETURN_URL    = "";
        String amt           = "";
        String currencycode  = "";
        String trackid       = "";
        String responseURL   = "";
        String errorURL      = "";
        String Card          = "";
        String expyear       = "";
        String expmonth      = "";
        String CVV2          = "";
        String firstName     = "";
        String lastName      = "";
        String member        = "";


        try
        {
            if(functions.isValueNull(commMerchantVO.getHostUrl()))
                RETURN_URL ="https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL")+trackingID;
            else
                RETURN_URL = RB.getString("RETURN_URL") + trackingID;

            trackid       = trackingID;
            responseURL   = RETURN_URL;
            errorURL      = RETURN_URL;
            amt           = genericTransDetailsVO.getAmount();
            currencycode  = CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());
            Card          = genericCardDetailsVO.getCardNum();
            expmonth      = genericCardDetailsVO.getExpMonth();
            expyear       = genericCardDetailsVO.getExpYear();
            CVV2          = genericCardDetailsVO.getcVV();
            langid        = "USA";
            firstName     = genericAddressDetailsVO.getFirstname();
            lastName      = genericAddressDetailsVO.getLastname();
            member        = firstName + " " + lastName;

            if(isTest)
            {
                REQUEST_URL = RB.getString("TEST_SALE");
            }
            else
            {
                REQUEST_URL = RB.getString("LIVE_SALE");
            }

            // 1=>purchase , 4=> preAuth as per bank jsp
            if ("Y".equalsIgnoreCase(is3DSupported))
            {
                action = "4";
                comm3DResponseVO.setThreeDVersion("3Dv1");
            }
            else
            {
                action = "1";
                comm3DResponseVO.setThreeDVersion("Non-3D");
            }

            String requestParameters = "<request><card>"+Card+"</card><cvv2>"+CVV2+"</cvv2><currencycode>"+currencycode+"</currencycode><expyear>"+expyear+"</expyear><expmonth>"+expmonth+"</expmonth><langid>"+langid+"</langid><member>"+member+"</member><amt>"+amt+"</amt><action>"+action+"</action><trackid>"+trackid+"</trackid><udf1></udf1><udf2></udf2><udf3></udf3><udf4></udf4><udf5></udf5><id>"+id+"</id><password>"+password+"</password></request>";
            String requestParametersLOG = "<request><card>"+functions.maskingPan(Card)+"</card><cvv2>"+functions.maskingNumber(CVV2)+"</cvv2><currencycode>"+currencycode+"</currencycode><expyear>"+functions.maskingNumber(expyear)+"</expyear><expmonth>"+functions.maskingNumber(expmonth)+"</expmonth><langid>"+langid+"</langid><member>"+functions.maskingFirstName(firstName)+" "+ functions.maskingLastName(lastName)+"</member><amt>"+amt+"</amt><action>"+action+"</action><trackid>"+trackid+"</trackid><udf1></udf1><udf2></udf2><udf3></udf3><udf4></udf4><udf5></udf5><id>"+id+"</id><password>"+functions.maskingFirstName(password)+"</password></request>";
            transactionLogger.error("processSale requestParameters---"+trackingID+"--:" + requestParametersLOG);

            String encryptedRequestData = HDFCPaymentGatewayUtils.encryptText(EncryptionKey,requestParameters);

            String request = REQUEST_URL+"&trandata="+encryptedRequestData + "&errorURL="+errorURL + "&responseURL="+responseURL + "&tranportalId="+id;
            transactionLogger.error("processSale REQUEST---"+ trackingID +"--->" +request);

            if (functions.isValueNull(request))
            {
                comm3DResponseVO.setStatus("pending3DConfirmation");
                comm3DResponseVO.setRedirectUrl(request);
            }
        }
        catch (Exception e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(HDFCPaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while capturing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return comm3DResponseVO;
    }

}
