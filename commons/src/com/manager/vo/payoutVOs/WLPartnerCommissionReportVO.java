package com.manager.vo.payoutVOs;

import com.manager.vo.PartnerDetailsVO;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sandip
 * Date: 12/12/16
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class WLPartnerCommissionReportVO
{
    double totalTransactionFee;
    double totalCommissionAmount;
    double serviceTaxChargesAmount;
    double previousBalanceAmount;
    double netFinalFeeAmount;
    double otherFeeAmount;
    String invoiceDate;
    String customer;
    String pspName;
    String currency;
    String bankName;
    String invoiceNumber;
    String startDate;
    String endDate;
    String startPeriod;
    String endPeriod;

    double randomChargeValue;
    String randomChargeName;

    HashMap<String, WLPartnerCommissionDetailsVO> otherFeeHashMap;
    List<WLPartnerPerBankCommissionReportVO> wlPartnerPerBankCommissionReportVOList;
    HashMap<String, ServiceTaxChargeVO> serviceTaxChargeVOHashMapFinal;

    PartnerDetailsVO partnerDetailsVO;

    public HashMap<String, WLPartnerCommissionDetailsVO> getOtherFeeHashMap()
    {
        return otherFeeHashMap;
    }

    public void setOtherFeeHashMap(HashMap<String, WLPartnerCommissionDetailsVO> otherFeeHashMap)
    {
        this.otherFeeHashMap = otherFeeHashMap;
    }
    public PartnerDetailsVO getPartnerDetailsVO()
    {
        return partnerDetailsVO;
    }

    public void setPartnerDetailsVO(PartnerDetailsVO partnerDetailsVO)
    {
        this.partnerDetailsVO = partnerDetailsVO;
    }

    public double getNetFinalFeeAmount()
    {
        return netFinalFeeAmount;
    }

    public void setNetFinalFeeAmount(double netFinalFeeAmount)
    {
        this.netFinalFeeAmount = netFinalFeeAmount;
    }


    public String getInvoiceDate()
    {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate)
    {
        this.invoiceDate = invoiceDate;
    }

    public String getPspName()
    {
        return pspName;
    }

    public void setPspName(String pspName)
    {
        this.pspName = pspName;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getStartPeriod()
    {
        return startPeriod;
    }

    public void setStartPeriod(String startPeriod)
    {
        this.startPeriod = startPeriod;
    }

    public String getEndPeriod()
    {
        return endPeriod;
    }

    public void setEndPeriod(String endPeriod)
    {
        this.endPeriod = endPeriod;
    }

    public String getCustomer()
    {
        return customer;
    }

    public void setCustomer(String customer)
    {
        this.customer = customer;
    }

    public String getBankName()
    {
        return bankName;
    }
    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    public String getInvoiceNumber()
    {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber)
    {
        this.invoiceNumber = invoiceNumber;
    }

    public double getTotalCommissionAmount()
    {
        return totalCommissionAmount;
    }

    public void setTotalCommissionAmount(double totalCommissionAmount)
    {
        this.totalCommissionAmount = totalCommissionAmount;
    }

    public double getPreviousBalanceAmount()
    {
        return previousBalanceAmount;
    }

    public void setPreviousBalanceAmount(double previousBalanceAmount)
    {
        this.previousBalanceAmount = previousBalanceAmount;
    }

    public List<WLPartnerPerBankCommissionReportVO> getWlPartnerPerBankCommissionReportVOList()
    {
        return wlPartnerPerBankCommissionReportVOList;
    }

    public void setWlPartnerPerBankCommissionReportVOList(List<WLPartnerPerBankCommissionReportVO> wlPartnerPerBankCommissionReportVOList)
    {
        this.wlPartnerPerBankCommissionReportVOList = wlPartnerPerBankCommissionReportVOList;
    }

    public double getOtherFeeAmount()
    {
        return otherFeeAmount;
    }

    public void setOtherFeeAmount(double otherFeeAmount)
    {
        this.otherFeeAmount = otherFeeAmount;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public double getRandomChargeValue()
    {
        return randomChargeValue;
    }

    public void setRandomChargeValue(double randomChargeValue)
    {
        this.randomChargeValue = randomChargeValue;
    }

    public String getRandomChargeName()
    {
        return randomChargeName;
    }

    public void setRandomChargeName(String randomChargeName)
    {
        this.randomChargeName = randomChargeName;
    }

    public double getTotalTransactionFee()
    {
        return totalTransactionFee;
    }

    public void setTotalTransactionFee(double totalTransactionFee)
    {
        this.totalTransactionFee = totalTransactionFee;
    }

    public double getServiceTaxChargesAmount()
    {
        return serviceTaxChargesAmount;
    }

    public void setServiceTaxChargesAmount(double serviceTaxChargesAmount)
    {
        this.serviceTaxChargesAmount = serviceTaxChargesAmount;
    }

    public HashMap<String, ServiceTaxChargeVO> getServiceTaxChargeVOHashMapFinal()
    {
        return serviceTaxChargeVOHashMapFinal;
    }

    public void setServiceTaxChargeVOHashMapFinal(HashMap<String, ServiceTaxChargeVO> serviceTaxChargeVOHashMapFinal)
    {
        this.serviceTaxChargeVOHashMapFinal = serviceTaxChargeVOHashMapFinal;
    }
}
