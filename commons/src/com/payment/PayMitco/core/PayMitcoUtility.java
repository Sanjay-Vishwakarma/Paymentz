package com.payment.PayMitco.core;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.vo.ReserveField2VO;
import com.payment.common.core.*;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by ThinkPadT410 on 12/26/2015.
 */
public class PayMitcoUtility
{
    private static TransactionLogger transactionLogger = new TransactionLogger(PayMitcoUtility.class.getName());
    public CommRequestVO getPayMitcoRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        ReserveField2VO reserveField2VO = null;

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());

        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        addressDetailsVO.setCustomerid( commonValidatorVO.getCustomerId());
        transactionLogger.debug("---1. customer id---"+addressDetailsVO.getCustomerid());

        //addressDetailsVO.setTmpl_currency("ACH");
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());

        if(commonValidatorVO.getReserveField2VO()!=null)
        {
            reserveField2VO = new ReserveField2VO();
            reserveField2VO.setAccountType(commonValidatorVO.getReserveField2VO().getAccountType());
            reserveField2VO.setRoutingNumber(commonValidatorVO.getReserveField2VO().getRoutingNumber());
            reserveField2VO.setAccountNumber(commonValidatorVO.getReserveField2VO().getAccountNumber());
            reserveField2VO.setCheckNumber(commonValidatorVO.getReserveField2VO().getCheckNumber());
            reserveField2VO.setBankName(commonValidatorVO.getReserveField2VO().getBankName());
            reserveField2VO.setBankAddress(commonValidatorVO.getReserveField2VO().getBankAddress());
            reserveField2VO.setBankCity(commonValidatorVO.getReserveField2VO().getBankCity());
            reserveField2VO.setBankState(commonValidatorVO.getReserveField2VO().getBankState());
            reserveField2VO.setBankZipcode(commonValidatorVO.getReserveField2VO().getBankZipcode());
        }



        merchantAccountVO.setDisplayName(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantAccountVO.setAliasName(commonValidatorVO.getMerchantDetailsVO().getSiteName());
        merchantAccountVO.setMerchantOrganizationName(commonValidatorVO.getMerchantDetailsVO().getCompany_name());
        merchantAccountVO.setPartnerSupportContactNumber(commonValidatorVO.getMerchantDetailsVO().getPartnerSupportContactNumber());
        merchantAccountVO.setSitename(commonValidatorVO.getMerchantDetailsVO().getSiteName());
        merchantAccountVO.setMerchantSupportNumber(commonValidatorVO.getMerchantDetailsVO().getTelNo());


        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setReserveField2VO(reserveField2VO);

        return commRequestVO;
    }

}
