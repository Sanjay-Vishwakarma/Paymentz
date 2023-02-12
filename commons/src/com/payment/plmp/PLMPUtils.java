package com.payment.plmp;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.payment.common.core.*;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Rihen on 5/28/2019.
 */
public class PLMPUtils
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PLMPUtils.class.getName());

    public static CommRequestVO getPLMPRequestVO(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("---------- In getPLMPRequestVO -------");
        Functions functions=new Functions();
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merchantId = account.getMerchantId();

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail())) {
            addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())) {
            addressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }
        addressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        addressDetailsVO.setCustomerid(commonValidatorVO.getCustomerId());
        transDetailsVO.setRedirectUrl(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTrackingid());

        if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getWalletAmount())) {
            transDetailsVO.setWalletAmount(commonValidatorVO.getTransDetailsVO().getWalletAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getWalletCurrency())) {
            transDetailsVO.setWalletCurrency(commonValidatorVO.getTransDetailsVO().getWalletCurrency());
        }

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setMerchantId(merchantId);
        merchantAccountVO.setDisplayName(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantAccountVO.setAliasName(commonValidatorVO.getMerchantDetailsVO().getSiteName());

        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);

        return commRequestVO;
    }

}