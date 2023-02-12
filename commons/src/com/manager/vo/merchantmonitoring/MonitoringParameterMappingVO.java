package com.manager.vo.merchantmonitoring;

import com.manager.vo.TerminalVO;

/**
 * Created by Vishal on 5/12/2016.
 */
public class MonitoringParameterMappingVO
{
    int mappingId;
    int memberId;
    int terminalId;
    int monitoringParameterId;
    double alertThreshold;
    double suspensionThreshold;

    String isAlertToAdmin;
    String isAlertToAdminSales;
    String isAlertToAdminRF;
    String isAlertToAdminCB;
    String isAlertToAdminFraud;
    String isAlertToAdminTech;


    String isAlertToMerchant;
    String isAlertToMerchantSales;
    String isAlertToMerchantRF;
    String isAlertToMerchantCB;
    String isAlertToMerchantFraud;
    String isAlertToMerchantTech;

    String isAlertToPartner;
    String isAlertToPartnerSales;
    String isAlertToPartnerRF;
    String isAlertToPartnerCB;
    String isAlertToPartnerFraud;
    String isAlertToPartnerTech;

    String isAlertToAgent;
    String isAlertToAgentSales;
    String isAlertToAgentRF;
    String isAlertToAgentCB;
    String isAlertToAgentFraud;
    String isAlertToAgentTech;

    String alertActivation;
    String suspensionActivation;
    String creationTime;
    String lastUpdatedTime;
    String alertMessage;
    String mappingFrequency;
    boolean isCustomSetting;

    String isDailyExecution;
    String isWeeklyExecution;
    String isMonthlyExecution;

    Double weeklyAlertThreshold;
    Double weeklySuspensionThreshold;
    Double monthlyAlertThreshold;
    Double monthlySuspensionThreshold;

    String displayChartType;

    MonitoringParameterVO monitoringParameterVO;
    TerminalVO terminalVO;


    public int getMappingId()
    {
        return mappingId;
    }

    public void setMappingId(int mappingId)
    {
        this.mappingId = mappingId;
    }

    public int getMemberId()
    {
        return memberId;
    }

    public void setMemberId(int memberId)
    {
        this.memberId = memberId;
    }

    public int getTerminalId()
    {
        return terminalId;
    }

    public void setTerminalId(int terminalId)
    {
        this.terminalId = terminalId;
    }

    public int getMonitoringParameterId()
    {
        return monitoringParameterId;
    }

    public void setMonitoringParameterId(int monitoringParameterId)
    {
        this.monitoringParameterId = monitoringParameterId;
    }
    public double getAlertThreshold()
    {
        return alertThreshold;
    }

    public void setAlertThreshold(double alertThreshold)
    {
        this.alertThreshold = alertThreshold;
    }

    public double getSuspensionThreshold()
    {
        return suspensionThreshold;
    }

    public void setSuspensionThreshold(double suspensionThreshold)
    {
        this.suspensionThreshold = suspensionThreshold;
    }

    public String getIsAlertToAdmin()
    {
        return isAlertToAdmin;
    }

    public void setIsAlertToAdmin(String isAlertToAdmin)
    {
        this.isAlertToAdmin = isAlertToAdmin;
    }

    public String getIsAlertToMerchant()
    {
        return isAlertToMerchant;
    }

    public void setIsAlertToMerchant(String isAlertToMerchant)
    {
        this.isAlertToMerchant = isAlertToMerchant;
    }

    public String getIsAlertToPartner()
    {
        return isAlertToPartner;
    }

    public void setIsAlertToPartner(String isAlertToPartner)
    {
        this.isAlertToPartner = isAlertToPartner;
    }

    public String getIsAlertToAgent()
    {
        return isAlertToAgent;
    }

    public void setIsAlertToAgent(String isAlertToAgent)
    {
        this.isAlertToAgent = isAlertToAgent;
    }

    public String getAlertActivation()
    {
        return alertActivation;
    }

    public void setAlertActivation(String alertActivation)
    {
        this.alertActivation = alertActivation;
    }

    public String getSuspensionActivation()
    {
        return suspensionActivation;
    }

    public void setSuspensionActivation(String suspensionActivation)
    {
        this.suspensionActivation = suspensionActivation;
    }
    public String getCreationTime()
    {
        return creationTime;
    }

    public void setCreationTime(String creationTime)
    {
        this.creationTime = creationTime;
    }

    public String getLastUpdatedTime()
    {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime)
    {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public MonitoringParameterVO getMonitoringParameterVO()
    {
        return monitoringParameterVO;
    }

    public void setMonitoringParameterVO(MonitoringParameterVO monitoringParameterVO)
    {
        this.monitoringParameterVO = monitoringParameterVO;
    }

    public TerminalVO getTerminalVO()
    {
        return terminalVO;
    }

    public void setTerminalVO(TerminalVO terminalVO)
    {
        this.terminalVO = terminalVO;
    }

    public String getAlertMessage()
    {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage)
    {
        this.alertMessage = alertMessage;
    }

    public boolean isCustomSetting()
    {
        return isCustomSetting;
    }

    public void setCustomSetting(boolean isCustomSetting)
    {
        this.isCustomSetting = isCustomSetting;
    }

    public String getMappingFrequency()
    {
        return mappingFrequency;
    }

    public void setMappingFrequency(String mappingFrequency)
    {
        this.mappingFrequency = mappingFrequency;
    }

    public String getIsAlertToAdminSales()
    {
        return isAlertToAdminSales;
    }

    public void setIsAlertToAdminSales(String isAlertToAdminSales)
    {
        this.isAlertToAdminSales = isAlertToAdminSales;
    }

    public String getIsAlertToAdminRF()
    {
        return isAlertToAdminRF;
    }

    public void setIsAlertToAdminRF(String isAlertToAdminRF)
    {
        this.isAlertToAdminRF = isAlertToAdminRF;
    }

    public String getIsAlertToAdminCB()
    {
        return isAlertToAdminCB;
    }

    public void setIsAlertToAdminCB(String isAlertToAdminCB)
    {
        this.isAlertToAdminCB = isAlertToAdminCB;
    }

    public String getIsAlertToAdminFraud()
    {
        return isAlertToAdminFraud;
    }

    public void setIsAlertToAdminFraud(String isAlertToAdminFraud)
    {
        this.isAlertToAdminFraud = isAlertToAdminFraud;
    }

    public String getIsAlertToAdminTech()
    {
        return isAlertToAdminTech;
    }

    public void setIsAlertToAdminTech(String isAlertToAdminTech)
    {
        this.isAlertToAdminTech = isAlertToAdminTech;
    }

    public String getIsAlertToMerchantSales()
    {
        return isAlertToMerchantSales;
    }

    public void setIsAlertToMerchantSales(String isAlertToMerchantSales)
    {
        this.isAlertToMerchantSales = isAlertToMerchantSales;
    }

    public String getIsAlertToMerchantRF()
    {
        return isAlertToMerchantRF;
    }

    public void setIsAlertToMerchantRF(String isAlertToMerchantRF)
    {
        this.isAlertToMerchantRF = isAlertToMerchantRF;
    }

    public String getIsAlertToMerchantCB()
    {
        return isAlertToMerchantCB;
    }

    public void setIsAlertToMerchantCB(String isAlertToMerchantCB)
    {
        this.isAlertToMerchantCB = isAlertToMerchantCB;
    }

    public String getIsAlertToMerchantFraud()
    {
        return isAlertToMerchantFraud;
    }

    public void setIsAlertToMerchantFraud(String isAlertToMerchantFraud)
    {
        this.isAlertToMerchantFraud = isAlertToMerchantFraud;
    }

    public String getIsAlertToMerchantTech()
    {
        return isAlertToMerchantTech;
    }

    public void setIsAlertToMerchantTech(String isAlertToMerchantTech)
    {
        this.isAlertToMerchantTech = isAlertToMerchantTech;
    }

    public String getIsAlertToPartnerSales()
    {
        return isAlertToPartnerSales;
    }

    public void setIsAlertToPartnerSales(String isAlertToPartnerSales)
    {
        this.isAlertToPartnerSales = isAlertToPartnerSales;
    }

    public String getIsAlertToPartnerRF()
    {
        return isAlertToPartnerRF;
    }

    public void setIsAlertToPartnerRF(String isAlertToPartnerRF)
    {
        this.isAlertToPartnerRF = isAlertToPartnerRF;
    }

    public String getIsAlertToPartnerCB()
    {
        return isAlertToPartnerCB;
    }

    public void setIsAlertToPartnerCB(String isAlertToPartnerCB)
    {
        this.isAlertToPartnerCB = isAlertToPartnerCB;
    }

    public String getIsAlertToPartnerFraud()
    {
        return isAlertToPartnerFraud;
    }

    public void setIsAlertToPartnerFraud(String isAlertToPartnerFraud)
    {
        this.isAlertToPartnerFraud = isAlertToPartnerFraud;
    }

    public String getIsAlertToPartnerTech()
    {
        return isAlertToPartnerTech;
    }

    public void setIsAlertToPartnerTech(String isAlertToPartnerTech)
    {
        this.isAlertToPartnerTech = isAlertToPartnerTech;
    }

    public String getIsAlertToAgentRF()
    {
        return isAlertToAgentRF;
    }

    public void setIsAlertToAgentRF(String isAlertToAgentRF)
    {
        this.isAlertToAgentRF = isAlertToAgentRF;
    }

    public String getIsAlertToAgentSales()
    {
        return isAlertToAgentSales;
    }

    public void setIsAlertToAgentSales(String isAlertToAgentSales)
    {
        this.isAlertToAgentSales = isAlertToAgentSales;
    }

    public String getIsAlertToAgentCB()
    {
        return isAlertToAgentCB;
    }

    public void setIsAlertToAgentCB(String isAlertToAgentCB)
    {
        this.isAlertToAgentCB = isAlertToAgentCB;
    }

    public String getIsAlertToAgentFraud()
    {
        return isAlertToAgentFraud;
    }

    public void setIsAlertToAgentFraud(String isAlertToAgentFraud)
    {
        this.isAlertToAgentFraud = isAlertToAgentFraud;
    }

    public String getIsAlertToAgentTech()
    {
        return isAlertToAgentTech;
    }

    public void setIsAlertToAgentTech(String isAlertToAgentTech)
    {
        this.isAlertToAgentTech = isAlertToAgentTech;
    }

    public String getIsDailyExecution()
    {
        return isDailyExecution;
    }

    public void setIsDailyExecution(String isDailyExecution)
    {
        this.isDailyExecution = isDailyExecution;
    }

    public String getIsWeeklyExecution()
    {
        return isWeeklyExecution;
    }

    public void setIsWeeklyExecution(String isWeeklyExecution)
    {
        this.isWeeklyExecution = isWeeklyExecution;
    }

    public String getIsMonthlyExecution()
    {
        return isMonthlyExecution;
    }

    public void setIsMonthlyExecution(String isMonthlyExecution)
    {
        this.isMonthlyExecution = isMonthlyExecution;
    }

    public Double getWeeklyAlertThreshold()
    {
        return weeklyAlertThreshold;
    }

    public void setWeeklyAlertThreshold(Double weeklyAlertThreshold)
    {
        this.weeklyAlertThreshold = weeklyAlertThreshold;
    }

    public Double getWeeklySuspensionThreshold()
    {
        return weeklySuspensionThreshold;
    }

    public void setWeeklySuspensionThreshold(Double weeklySuspensionThreshold)
    {
        this.weeklySuspensionThreshold = weeklySuspensionThreshold;
    }

    public Double getMonthlyAlertThreshold()
    {
        return monthlyAlertThreshold;
    }

    public void setMonthlyAlertThreshold(Double monthlyAlertThreshold)
    {
        this.monthlyAlertThreshold = monthlyAlertThreshold;
    }

    public Double getMonthlySuspensionThreshold()
    {
        return monthlySuspensionThreshold;
    }

    public void setMonthlySuspensionThreshold(Double monthlySuspensionThreshold)
    {
        this.monthlySuspensionThreshold = monthlySuspensionThreshold;
    }
    public String getDisplayChartType()
    {
        return displayChartType;
    }

    public void setDisplayChartType(String displayChartType)
    {
        this.displayChartType = displayChartType;
    }
}
