package com.manager.vo.fraudruleconfVOs;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/11/15
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudRuleChangeRequestVO
{
    public FraudRuleChangeIntimationVO intimationVO;
    public String fraudSystemId;
    public String fraudSystemAccountId;


    public FraudRuleChangeIntimationVO getIntimationVO()
    {
        return intimationVO;
    }
    public String getFraudSystemId()
    {
        return fraudSystemId;
    }
    public void setFraudSystemId(String fraudSystemId)
    {
        this.fraudSystemId = fraudSystemId;
    }
     public String getFraudSystemAccountId()
    {
        return fraudSystemAccountId;
    }
}
