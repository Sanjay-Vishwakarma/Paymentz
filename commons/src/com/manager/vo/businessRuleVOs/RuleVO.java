package com.manager.vo.businessRuleVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Admin on 8/8/15.
 */
@XmlRootElement(name="rule")
@XmlAccessorType(XmlAccessType.FIELD)
public class RuleVO
{
    private String ruleType;

    private String id;
    private String name;
    private String label;
    private String description;
    private String help;
    private boolean isApplicable;
    private String query;

    List<RuleOperation> RuleOperation;

    public String getRuleType()
    {
        return ruleType;
    }

    public void setRuleType(String ruleType)
    {
        this.ruleType = ruleType;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public boolean isApplicable() {
        return isApplicable;
    }

    public void setIsApplicable(boolean isApplicable) {
        this.isApplicable = isApplicable;
    }

    public void setApplicable(boolean isApplicable)
    {
        this.isApplicable = isApplicable;
    }

    public List<RuleOperation> getRuleOperation()
    {
        return RuleOperation;
    }

    public void setRuleOperation(List<RuleOperation> ruleOperation)
    {
        RuleOperation = ruleOperation;
    }

    public boolean equals(Object o){
        if(o == null)
            return false;
        if(!(o instanceof RuleVO))
            return false;

        RuleVO ruleVO = (RuleVO) o;

        return this.getId().equals(ruleVO.getId());
    }
}
