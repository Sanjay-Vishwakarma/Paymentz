package com.manager.vo.fraudruleconfVOs;

import java.util.List;
/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/11/15
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccountRuleChangeRequestVO extends FraudRuleChangeRequestVO
{
    private List<FraudSystemAccountRuleVO> systemAccountRuleVOList;
    public AccountRuleChangeRequestVO(FraudRuleChangeIntimationVO intimationVO,List<FraudSystemAccountRuleVO> systemAccountRuleVOList,String fraudSystemAccountId,String fraudSystemId)
    {
        this.intimationVO=intimationVO;
        this.systemAccountRuleVOList=systemAccountRuleVOList;
        this.fraudSystemAccountId=fraudSystemAccountId;
        this.fraudSystemId=fraudSystemId;
    }
    public List<FraudSystemAccountRuleVO> getSystemAccountRuleVOList()
        {
        return systemAccountRuleVOList;
    }
}
