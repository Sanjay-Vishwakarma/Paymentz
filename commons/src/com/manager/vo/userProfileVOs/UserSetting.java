package com.manager.vo.userProfileVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * Created by Admin on 21/8/15.
 */
@XmlRootElement(name="userProfile")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserSetting {

    private Map<String,MemberDetails> members;

    public Map<String, MemberDetails> getMembers() {
        return members;
    }

    public void setMembers(Map<String, MemberDetails> members) {
        this.members = members;
    }
}
