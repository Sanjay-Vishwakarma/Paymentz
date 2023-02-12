package com.manager.vo.fraudruleconfVOs;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/11/15
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubAccountRuleMappingVO
{
    String subAccountRuleId;
    String fsSubAccountId;
    String ruleId;
    String score;
    String status;
    String lastUpdated;

    public String getSubAccountRuleId()
    {
        return subAccountRuleId;
    }
    public void setSubAccountRuleId(String subAccountRuleId)
    {
        this.subAccountRuleId = subAccountRuleId;
    }
    public String getFsSubAccountId()
    {
        return fsSubAccountId;
    }
    public void setFsSubAccountId(String fsSubAccountId)
    {
        this.fsSubAccountId = fsSubAccountId;
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
    public String getLastUpdated()
    {
        return lastUpdated;
    }
    public void setLastUpdated(String lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }
}
