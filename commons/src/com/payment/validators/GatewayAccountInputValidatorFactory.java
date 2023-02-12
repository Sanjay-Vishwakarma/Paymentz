package com.payment.validators;

import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.paymentgateway.TrustsPayPaymentGateway;
import com.payment.inpay.InPayGatewayAccountInputValidator;
import com.payment.inpay.InPayPaymentGateway;
import com.payment.payforasia.core.PayforasiaGatewayAccountInputValidator;
import com.payment.payforasia.core.PayforasiaPaymentGateway;
import com.payment.payon.core.PayOnGateway;
import com.payment.payon.core.PayOnGatewayAccountInputValidator;
import com.payment.pbs.core.PbsGatewayAccountInputValidator;
import com.payment.pbs.core.PbsPaymentGateway;
import com.payment.trustspay.TrustsPayGatewayAccountInputValidator;

/**
 * Created by supriya on 5/11/16.
 */
public class GatewayAccountInputValidatorFactory
{
    public static CommonGatewayAccountInputValidator getRequestVOInstance(String pgTypeId)
    {
        GatewayType gatewayType = GatewayTypeService.getGatewayType(pgTypeId);
        String gateway = gatewayType.getGateway();
        if (PayforasiaPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new PayforasiaGatewayAccountInputValidator();
        }
        else if(PayOnGateway.GATEWAY_TYPE_PAYON.equalsIgnoreCase(gateway))
        {
            return new PayOnGatewayAccountInputValidator();
        }
        else if(PayOnGateway.GATEWAY_TYPE_CATELLA.equalsIgnoreCase(gateway))
        {
            return new PayOnGatewayAccountInputValidator();
        }
        else if(PbsPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new PbsGatewayAccountInputValidator();
        }
        else if(TrustsPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new TrustsPayGatewayAccountInputValidator();
        }
        else if(InPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway))
        {
            return new InPayGatewayAccountInputValidator();
        }
        else
        {
            return new CommonGatewayAccountInputValidator();
        }

    }



}
