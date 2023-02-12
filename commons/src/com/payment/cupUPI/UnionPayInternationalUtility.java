package com.payment.cupUPI;

import com.directi.pg.UnionPayInternationalLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Jitendra on 06-Jul-19.
 */
public class UnionPayInternationalUtility
{
    private static UnionPayInternationalLogger transactionLogger = new UnionPayInternationalLogger(UnionPayInternationalUtility.class.getName());
    public CommRequestVO getUnionPayRequestVO(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("Inside UnionPayInternationalUtility getUnionPayRequestVO 1 ::");
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
       // GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
       // addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
       // addressDetailsVO.setTelnocc(commonValidatorVO.getAddressDetailsVO().getTelnocc());
        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getTelnocc()+"-"+commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        addressDetailsVO.setCustomerid(commonValidatorVO.getCustomerId());
        addressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        addressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());

        commCardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
        commCardDetailsVO.setExpMonth( commonValidatorVO.getCardDetailsVO().getExpMonth());
        commCardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
        commCardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());


        commTransactionDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commTransactionDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        commTransactionDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        commTransactionDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());


        merchantAccountVO.setDisplayName(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantAccountVO.setAliasName(commonValidatorVO.getMerchantDetailsVO().getSiteName());
        merchantAccountVO.setMerchantOrganizationName(commonValidatorVO.getMerchantDetailsVO().getCompany_name());
        merchantAccountVO.setPartnerSupportContactNumber(commonValidatorVO.getMerchantDetailsVO().getPartnerSupportContactNumber());
        merchantAccountVO.setSitename(commonValidatorVO.getMerchantDetailsVO().getSiteName());
        merchantAccountVO.setMerchantSupportNumber(commonValidatorVO.getMerchantDetailsVO().getTelNo());
        merchantAccountVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());


        commRequestVO =PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
        commRequestVO.setCardDetailsVO(commCardDetailsVO);
        transactionLogger.error("After Setting All VO In CommRequestVO");

        return commRequestVO;
    }

    public UnionPayInternationalRequestVO getUnionPayRequestVO(TransactionDetailsVO transactionDetailsVO,CommCardDetailsVO commCardDetailsVO,CommAddressDetailsVO commAddressDetailsVO,String phoneNumberNew)
    {
        transactionLogger.error("Inside UnionPayInternationalUtility --------------");
        transactionLogger.error("Inside UnionPayInternationalUtility phone number --------------"+phoneNumberNew);
        UnionPayInternationalRequestVO unionPayInternationalRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
       // CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO1=new CommCardDetailsVO();
        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        System.out.println("account id in Utility line 82"+transactionDetailsVO.getAccountId());
        GatewayAccount account = GatewayAccountService.getGatewayAccount(transactionDetailsVO.getAccountId());

        addressDetailsVO.setFirstname(transactionDetailsVO.getFirstName());
        addressDetailsVO.setLastname(transactionDetailsVO.getLastName());
        addressDetailsVO.setCity(transactionDetailsVO.getCity());
        addressDetailsVO.setCountry(transactionDetailsVO.getCountry());
        addressDetailsVO.setPhone(transactionDetailsVO.getTelcc());
        addressDetailsVO.setIp(transactionDetailsVO.getIpAddress());
        addressDetailsVO.setPhone(phoneNumberNew);
        addressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
        addressDetailsVO.setState(transactionDetailsVO.getState());
        addressDetailsVO.setStreet(transactionDetailsVO.getStreet());
        addressDetailsVO.setZipCode(transactionDetailsVO.getZip());
        addressDetailsVO.setCustomerid(transactionDetailsVO.getCustomerId());
        addressDetailsVO.setTmpl_amount(commAddressDetailsVO.getTmpl_amount());
        addressDetailsVO.setTmpl_currency(commAddressDetailsVO.getTmpl_currency());
        transactionLogger.debug("Expiry date for easy enrollemnt --------"+transactionDetailsVO.getExpdate());
        transactionLogger.debug("Expiry date for easy enrollemnt --------"+transactionDetailsVO.getExpdate());
        commCardDetailsVO1.setCardNum(commCardDetailsVO.getCardNum());
        commCardDetailsVO1.setExpMonth(commCardDetailsVO.getExpMonth());
        commCardDetailsVO1.setExpYear(commCardDetailsVO.getExpYear());
        commCardDetailsVO1.setcVV(commCardDetailsVO.getcVV());


        transDetailsVO.setAmount(transactionDetailsVO.getAmount());
        transDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
       // transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
       // transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
      //  transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());


       /* merchantAccountVO.setDisplayName(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantAccountVO.setAliasName(commonValidatorVO.getMerchantDetailsVO().getSiteName());
        merchantAccountVO.setMerchantOrganizationName(commonValidatorVO.getMerchantDetailsVO().getCompany_name());
        merchantAccountVO.setPartnerSupportContactNumber(commonValidatorVO.getMerchantDetailsVO().getPartnerSupportContactNumber());
        merchantAccountVO.setSitename(commonValidatorVO.getMerchantDetailsVO().getSiteName());
        merchantAccountVO.setMerchantSupportNumber(commonValidatorVO.getMerchantDetailsVO().getTelNo());*/
        merchantAccountVO.setAccountId(transactionDetailsVO.getAccountId());
        merchantAccountVO.setMerchantId(transactionDetailsVO.getToid());

        transactionLogger.error("account id  ------------"+transactionDetailsVO.getAccountId());
        unionPayInternationalRequestVO = (UnionPayInternationalRequestVO) PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(transactionDetailsVO.getAccountId()));

        unionPayInternationalRequestVO.setAddressDetailsVO(addressDetailsVO);
        unionPayInternationalRequestVO.setCommMerchantVO(merchantAccountVO);
        unionPayInternationalRequestVO.setTransDetailsVO(transDetailsVO);
        unionPayInternationalRequestVO.setCardDetailsVO(commCardDetailsVO1);
       // unionPayInternationalRequestVO.setCardDetailsVO(commCardDetailsVO);
        transactionLogger.error("Here after setting allvalues in unionPayInternationalRequestVO------------");

        return unionPayInternationalRequestVO;
    }
}
