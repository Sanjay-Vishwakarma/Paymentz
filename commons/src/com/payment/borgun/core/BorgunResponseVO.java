package com.payment.borgun.core;

import com.payment.common.core.CommResponseVO;

import java.util.Map;

/**
 * Created by Admin on 5/13/2017.
 */
public class BorgunResponseVO extends CommResponseVO
{
    Map<String,InquiryTransaction> listMap;

    public Map<String, InquiryTransaction> getListMap()
    {
        return listMap;
    }

    public void setListMap(Map<String, InquiryTransaction> listMap)
    {
        this.listMap = listMap;
    }
}
