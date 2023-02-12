package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/29/13
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("Risk")
public class ResponseRisk
{
    public String getScore()
    {
        return score;
    }

    public void setScore(String score)
    {
        this.score = score;
    }

    @XStreamAsAttribute
    private String score="";
}
