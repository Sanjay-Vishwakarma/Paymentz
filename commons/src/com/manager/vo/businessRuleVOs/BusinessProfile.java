package com.manager.vo.businessRuleVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * Created by Admin on 8/8/15.
 */
@XmlRootElement(name="businessProfile")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessProfile
{
    private Map<String,ProfileVO> profiles;

    public Map<String, ProfileVO> getProfiles() {
        return profiles;
    }

    public void setProfiles(Map<String, ProfileVO> profiles) {
        this.profiles = profiles;
    }
}
