package com.payment.neteller.response;

import com.payment.common.core.CommResponseVO;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sneha on 2/9/2017.
 */
public class NetellerResponse extends CommResponseVO
{
    private String orderId;
    private String merchantRefId;
    private ErrorVO errorVO;
    private Error error;
    private List<Redirects> redirects = new ArrayList<Redirects>();
    private List<Links> links = new ArrayList<Links>();

    public Error getError()
    {
        return error;
    }

    public void setError(Error error)
    {
        this.error = error;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getMerchantRefId()
    {
        return merchantRefId;
    }

    public void setMerchantRefId(String merchantRefId)
    {
        this.merchantRefId = merchantRefId;
    }

    public ErrorVO getErrorVO()
    {
        return errorVO;
    }

    public void setErrorVO(ErrorVO errorVO)
    {
        this.errorVO = errorVO;
    }

    public List<Links> getLinks()
    {
        return links;
    }

    public void setLinks(List<Links> links)
    {
        this.links = links;
    }

    public List<Redirects> getRedirects()
    {
        return redirects;
    }

    public void setRedirects(List<Redirects> redirects)
    {
        this.redirects = redirects;
    }

    /*public void addRedirects(Redirects redirects1)
    {
        this.redirects.add(redirects1);
    }*/
}