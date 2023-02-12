package com.payment.duspaydirectdebit;

import com.directi.pg.TransactionLogger;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Jitendra on 24-Sep-19.
 */
public class DusPayDDUtils
{
    static TransactionLogger transactionLogger=new TransactionLogger(DusPayDDUtils.class.getName());

    public String getRedirectForm(String trackingId, CommResponseVO response3D)
    {
        transactionLogger.error("redirect url---" + response3D.getRedirectUrl());
        transactionLogger.error("trackingid DusPayDDUtils -------"+trackingId);
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(response3D.getRedirectUrl()).append("\" method=\"post\">\n");
        html.append("</form>");

        transactionLogger.error("form----"+html.toString());
        return html.toString();
    }

    public static CommRequestVO getCommRequestFromUtils(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("Inside DusPayDDUtils -----");
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }
}
