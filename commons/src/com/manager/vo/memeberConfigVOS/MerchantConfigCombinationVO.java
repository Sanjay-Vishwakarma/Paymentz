package com.manager.vo.memeberConfigVOS;

import com.manager.vo.ChargeVO;
import com.manager.vo.MerchantDetailsVO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12/11/14
 * Time: 9:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantConfigCombinationVO
{
    //merchant details vo
    MerchantDetailsVO merchantDetailsVO;
    //member_account_mapping
    MemberAccountMappingVO memberAccountMappingVO;
    //List of Charges from member_account_charges_mapping
    List<ChargeVO> chargeVOs ;

    public MerchantDetailsVO getMerchantDetailsVO()
    {
        return merchantDetailsVO;
    }

    public void setMerchantDetailsVO(MerchantDetailsVO merchantDetailsVO)
    {
        this.merchantDetailsVO = merchantDetailsVO;
    }

    public MemberAccountMappingVO getMemberAccountMappingVO()
    {
        return memberAccountMappingVO;
    }

    public void setMemberAccountMappingVO(MemberAccountMappingVO memberAccountMappingVO)
    {
        this.memberAccountMappingVO = memberAccountMappingVO;
    }

    public List<ChargeVO> getChargeVOs()
    {
        return chargeVOs;
    }

    public void setChargeVOs(List<ChargeVO> chargeVOs)
    {
        this.chargeVOs = chargeVOs;
    }
}
