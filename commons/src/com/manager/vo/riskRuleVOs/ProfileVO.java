package com.manager.vo.riskRuleVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Admin on 7/8/15.
 */
@XmlRootElement(name="profile")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProfileVO {

    private String id;
    private String name;


    private List<RuleVO> rules;

    public List<RuleVO> getRules() {
        return rules;
    }

    public void setRules(List<RuleVO> rules) {
        this.rules = rules;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
