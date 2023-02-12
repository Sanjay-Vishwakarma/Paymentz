package com.manager.vo;

import java.util.List;

/**
 * Created by sandip on 10/9/15.
 * V G ,./;'3654
 * \][POIUYTREWQ
 */
public class PartnerCommissionReportVO extends CommissionReportVO
{
    List<PartnerCommissionVO> partnerCommissionVOList;
    public List<PartnerCommissionVO> getPartnerCommissionVOList()
    {
        return partnerCommissionVOList;
    }
    public void setPartnerCommissionVOList(List<PartnerCommissionVO> partnerCommissionVOList)
    {
        this.partnerCommissionVOList = partnerCommissionVOList;
    }
}
