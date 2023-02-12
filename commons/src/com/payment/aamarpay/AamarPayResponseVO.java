package com.payment.aamarpay;

import com.payment.common.core.Comm3DResponseVO;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by Admin on 9/4/2021.
 */
public class AamarPayResponseVO extends Comm3DResponseVO
{
    private String card_type;
    private String optiontype;
    private String card_id;
    private String img_medium;
    private String url;
    List<AamarPayResponseVO> paymentList;

    public String getCard_type()
    {
        return card_type;
    }

    public void setCard_type(String card_type)
    {
        this.card_type = card_type;
    }

    public String getOptiontype()
    {
        return optiontype;
    }

    public void setOptiontype(String optiontype)
    {
        this.optiontype = optiontype;
    }

    public String getCard_id()
    {
        return card_id;
    }

    public void setCard_id(String card_id)
    {
        this.card_id = card_id;
    }

    public String getImg_medium()
    {
        return img_medium;
    }

    public void setImg_medium(String img_medium)
    {
        this.img_medium = img_medium;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public List<AamarPayResponseVO> getPaymentList()
    {
        return paymentList;
    }

    public void setPaymentList(List<AamarPayResponseVO> paymentList)
    {
        this.paymentList = paymentList;
    }
}
