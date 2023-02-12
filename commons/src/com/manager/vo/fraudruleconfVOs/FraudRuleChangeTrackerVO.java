package com.manager.vo.fraudruleconfVOs;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/8/15

 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.

 */
public class FraudRuleChangeTrackerVO

{
    String fraudRuleTrackerId;
    String fraudSystemId;
    String fsAccountId;
    String fsSubAccountId;
    String ruleId;
    String previousScore;
    String newScore;
    String previousStatus;
    String newStatus;
    String intimationId;
    String creationDate;
    String ruleName;
    String ruleDescription;
    String fsSubAccountName;

    public String getFraudRuleTrackerId()
    {
        return fraudRuleTrackerId;
    }
    public void setFraudRuleTrackerId(String fraudRuleTrackerId)
    {
        this.fraudRuleTrackerId = fraudRuleTrackerId;
    }
    public String getFraudSystemId()
    {
        return fraudSystemId;
    }
    public void setFraudSystemId(String fraudSystemId)
    {
        this.fraudSystemId = fraudSystemId;
    }
    public String getFsAccountId()
    {
        return fsAccountId;
    }
    public void setFsAccountId(String fsAccountId)
    {
        this.fsAccountId = fsAccountId;
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
    public String getPreviousScore()
    {
        return previousScore;
    }
    public void setPreviousScore(String previousScore)
    {
        this.previousScore = previousScore;
    }
    public String getNewScore()
    {
        return newScore;
    }
    public void setNewScore(String newScore)
    {
        this.newScore = newScore;
    }
    public String getPreviousStatus()
    {
        return previousStatus;
    }
    public void setPreviousStatus(String previousStatus)
    {
        this.previousStatus = previousStatus;
    }
    public String getNewStatus()
    {
        return newStatus;
    }
    public void setNewStatus(String newStatus)
    {
        this.newStatus = newStatus;
    }
    public String getIntimationId()
    {
        return intimationId;
    }
    public void setIntimationId(String intimationId)
    {
        this.intimationId = intimationId;
    }
    public String getCreationDate()
    {
        return creationDate;
    }
    public void setCreationDate(String creationDate)
    {
        this.creationDate = creationDate;
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
    public String getFsSubAccountName()
    {
        return fsSubAccountName;
    }
    public void setFsSubAccountName(String fsSubAccountName) {this.fsSubAccountName = fsSubAccountName;}
    public void setRuleDescription(String ruleDescription) {this.ruleDescription = ruleDescription;}
}
