package com.manager.vo.fraudruleconfVOs;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/11/15
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class RuleMasterVO
{
    String ruleId;
    String ruleName;
    String ruleDescription;
    String ruleGroup;
    String defaultScore;
    String defaultStatus;
    String creationOn;
    String lastUpdatedOn;
    String defaultValue;
    String isActive;

    public String getRuleId()
    {
        return ruleId;
    }
    public void setRuleId(String ruleId)
    {
        this.ruleId = ruleId;
    }
    public String getRuleName()
    {
        return ruleName;
    }
    public void setRuleName(String ruleName)
    {
        this.ruleName = ruleName;
    }
    public String getRuleDescription()
    {
        return ruleDescription;
    }
    public void setRuleDescription(String ruleDescription)
    {
        this.ruleDescription = ruleDescription;
    }
    public String getRuleGroup()
    {
        return ruleGroup;
    }
    public void setRuleGroup(String ruleGroup)
    {
        this.ruleGroup = ruleGroup;
    }
    public String getDefaultScore()
    {
        return defaultScore;
    }
    public void setDefaultScore(String defaultScore)
    {
        this.defaultScore = defaultScore;
    }
    public String getCreationOn()
    {
        return creationOn;
    }
    public void setCreationOn(String creationOn)
    {
        this.creationOn = creationOn;
    }
    public String getLastUpdatedOn()
    {
        return lastUpdatedOn;
    }
    public void setLastUpdatedOn(String lastUpdatedOn)
    {
        this.lastUpdatedOn = lastUpdatedOn;
    }
    public String getDefaultStatus()
    {
        return defaultStatus;
    }
    public void setDefaultStatus(String defaultStatus)
    {
        this.defaultStatus = defaultStatus;
    }
    public String getDefaultValue() {return defaultValue;}
    public void setDefaultValue(String defaultValue) {this.defaultValue = defaultValue;}
    public String getIsActive() {return isActive;}
    public void setIsActive(String isActive) {this.isActive = isActive;}
}
