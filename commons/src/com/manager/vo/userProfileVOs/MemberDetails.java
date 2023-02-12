package com.manager.vo.userProfileVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Admin on 21/8/15.
 */
@XmlRootElement(name="member")
@XmlAccessorType(XmlAccessType.FIELD)
public class MemberDetails {

    MerchantVO userSetting;
    TemplateVO templateSetting;

    public MerchantVO getUserSetting() {
        return userSetting;
    }

    public void setUserSetting(MerchantVO userSetting) {
        this.userSetting = userSetting;
    }

    public TemplateVO getTemplateSetting() {
        return templateSetting;
    }

    public void setTemplateSetting(TemplateVO templateSetting) {
        this.templateSetting = templateSetting;
    }
}
