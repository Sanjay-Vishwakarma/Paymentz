package com.manager.vo.fraudruleconfVOs;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/11/15
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudSystemSubAccountRuleVO
{
    String subAccountRuleId;
    String fsSubAccountId;
    String ruleId;
    String score;
    String status;
    String lastUpdated;
    String ruleName;
    String value;
    String value1;
    String value2;

    public String getRuleName()
    {
        return ruleName;
    }

    public void setRuleName(String ruleName)
    {
        this.ruleName = ruleName;
    }



    public String getSubAccountName()
    {
        return subAccountName;
    }

    public void setSubAccountName(String subAccountName)
    {
        this.subAccountName = subAccountName;
    }

    String subAccountName;

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

    public String getValue() {return value;}

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue1() {return value1;}

    public void setValue1(String value1) {this.value1 = value1;}

    public String getValue2() {return value2;}

    public void setValue2(String value2) {this.value2 = value2;}
}
