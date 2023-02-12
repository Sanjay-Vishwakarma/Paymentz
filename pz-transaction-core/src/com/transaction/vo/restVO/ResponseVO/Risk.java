package com.transaction.vo.restVO.ResponseVO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Sneha on 2/8/2016.
 */
@XmlRootElement(name="risk")
@XmlAccessorType(XmlAccessType.FIELD)
public class Risk
{
    @XmlElement (name="score")
    String score;

    public String getScore()
    {
        return score;
    }

    public void setScore(String score)
    {
        this.score = score;
    }
}
