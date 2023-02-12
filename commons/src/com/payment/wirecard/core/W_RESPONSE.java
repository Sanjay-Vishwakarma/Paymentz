package com.payment.wirecard.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/27/13
 * Time: 7:50 PM
 * To change this template use File | Settings | File Templates.
 */

@XStreamAlias("W_RESPONSE")
public class W_RESPONSE
{
    @XStreamAlias("W_JOB")
    W_JOB w_job;

    public W_JOB getW_job()
    {
        return w_job;
    }

    public void setW_job(W_JOB w_job)
    {
        this.w_job = w_job;
    }

}
