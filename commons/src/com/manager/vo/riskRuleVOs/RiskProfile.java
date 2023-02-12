package com.manager.vo.riskRuleVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * Created by Admin on 7/8/15.
 */
@XmlRootElement(name="riskProfile")
@XmlAccessorType(XmlAccessType.FIELD)
public class RiskProfile
{
    private Map<String,ProfileVO> profiles;

    public Map<String, ProfileVO> getProfiles() {
        return profiles;
    }

    public void setProfiles(Map<String, ProfileVO> profiles) {
        this.profiles = profiles;
    }
}
