package com.manager.vo.fraudruleconfVOs;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/11/15
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccountRuleMappingVO
{
    String accountRuleId;
    String fsAccountId;
    String ruleId;
    String score;
    String status;
    String lastUpdatedOn;

    public String getAccountRuleId()
    {
        return accountRuleId;
    }
    public void setAccountRuleId(String accountRuleId)
    {
        this.accountRuleId = accountRuleId;
    }
    public String getFsAccountId()
    {
        return fsAccountId;
    }
    public void setFsAccountId(String fsAccountId)
    {
        this.fsAccountId = fsAccountId;
    }
    public String getRuleId()
    {
        return ruleId;
    }
    public void setRuleId(String ruleId)
    {
        this.ruleId = ruleId;
    }
    public String getScore()
    {
        return score;
    }
    public void setScore(String score)
    {
        this.score = score;
    }
    public String getStatus()
    {
        return status;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }
    public String getLastUpdatedOn()
    {
        return lastUpdatedOn;
    }
    public void setLastUpdatedOn(String lastUpdatedOn)
    {
        this.lastUpdatedOn = lastUpdatedOn;
    }
}
