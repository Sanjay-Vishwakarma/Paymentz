package com.payment.ecore.core.request;

import com.payment.request.PZCaptureRequest;

/**
 * Created with IntelliJ IDEA.
 * User: Chandan
 * Date: 2/16/13
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class EcoreCaptureRequest extends PZCaptureRequest
{


    public void setPod(String pod)
    {
        this.pod = pod;
    }

    public String getPod()
    {
        return pod;
    }

    private String pod;

    public String getPodBatch()
    {
        return PodBatch;
    }

    public void setPodBatch(String podBatch)
    {
        PodBatch = podBatch;
    }

    private String PodBatch;
}
