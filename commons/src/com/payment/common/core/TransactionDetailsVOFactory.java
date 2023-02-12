package com.payment.common.core;

import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.manager.vo.TransactionDetailsVO;
import com.payment.procesosmc.ProcesosMCPaymentGateway;
import com.payment.procesosmc.ProcesosMCTransactionDetailsVO;
import com.payment.visaNet.VisaNetPaymentGateway;
import com.payment.visaNet.VisaNetTransactionDetailsVO;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 6/19/15
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionDetailsVOFactory
{
    public static TransactionDetailsVO getTransactionDetailsVOInstance(int accountId)
    {
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
        GatewayType gatewayType = GatewayTypeService.getGatewayType(gatewayAccount.getPgTypeId());
        String gateway = gatewayType.getGateway();

        if (VisaNetPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway)){
            return new VisaNetTransactionDetailsVO();
        }
        if (ProcesosMCPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gateway)){
            return new ProcesosMCTransactionDetailsVO();
        }
        else{
            return new TransactionDetailsVO();
        }

    }

}
