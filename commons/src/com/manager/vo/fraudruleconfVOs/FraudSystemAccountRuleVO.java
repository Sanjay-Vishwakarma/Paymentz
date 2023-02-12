package com.manager.vo.fraudruleconfVOs;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/11/15
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudSystemAccountRuleVO
{
    String accountRuleId;
    String fsAccountId;
    String ruleId;
    String score;
    String status;
    String lastUpdatedOn;
    String ruleName;
    String accountName;
    String value;
    String value1;
    String value2;

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

    public String getRuleName()
    {
        return ruleName;
    }

    public void setRuleName(String ruleName)
    {
        this.ruleName = ruleName;
    }

    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue1() {return value1;}

    public void setValue1(String value1) {this.value1 = value1;}

    public String getValue2() {return value2;}

    public void setValue2(String value2) {this.value2 = value2;}
}
