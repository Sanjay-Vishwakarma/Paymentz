package com.manager.vo.fraudruleconfVOs;
import java.util.List;
/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/10/15
 * Time: 12:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubAccountRuleChangeRequestVO extends FraudRuleChangeRequestVO
{
    private List<FraudSystemSubAccountRuleVO> subAccountRuleVOList;
    private String fraudSystemSubAccountId;
    private String memberId;
    private String partnerId;

    public SubAccountRuleChangeRequestVO(FraudRuleChangeIntimationVO intimationVO,List<FraudSystemSubAccountRuleVO> subAccountRuleVOList,String fraudSystemSubAccountId,String fraudSystemAccountId,String fraudSystemId,String memberId,String partnerId)
    {
        this.intimationVO=intimationVO;
        this.subAccountRuleVOList=subAccountRuleVOList;
        this.fraudSystemSubAccountId=fraudSystemSubAccountId;
        this.fraudSystemAccountId=fraudSystemAccountId;
        this.partnerId=partnerId;
        this.memberId=memberId;
        this.fraudSystemId=fraudSystemId;
    }
    public List<FraudSystemSubAccountRuleVO> getSubAccountRuleVOList()
    {
        return subAccountRuleVOList;
    }
    public String getFraudSystemSubAccountId()
    {
        return fraudSystemSubAccountId;
    }
    public String getMemberId()
    {
        return memberId;
    }
    public String getPartnerId()
    {
        return partnerId;
    }
}
