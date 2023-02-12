package com.manager.vo.merchantmonitoring;

/**
 * Created by Vishal on 5/11/2016.
 */
public class MonitoringParameterVO
{
  int monitoringParameterId;
  String monitoringParameterName;
  String monitoingParaTechName;
  String monitoingCategory;
  String monitoingKeyword;
  String monitoingSubKeyword;
  String monitoingAlertCategory;
  String monitoingFrequency;
  String monitoingOnChannel;
  String monitoingDeviation;
  String monitoringUnit;
  double defaultAlertThreshold;
  double defaultSuspensionThreshold;

  String defaultIsAlertToAdmin;
  String defaultIsAlertToAdminSales;
  String defaultIsAlertToAdminRF;
  String defaultIsAlertToAdminCB;
  String defaultIsAlertToAdminFraud;
  String defaultIsAlertToAdminTech;

  String defaultIsAlertToMerchant;
  String defaultIsAlertToMerchantSales;
  String defaultIsAlertToMerchantRF;
  String defaultIsAlertToMerchantCB;
  String defaultIsAlertToMerchantFraud;
  String defaultIsAlertToMerchantTech;

  String defaultIsAlertToPartner;
  String defaultIsAlertToPartnerSales;
  String defaultIsAlertToPartnerRF;
  String defaultIsAlertToPartnerCB;
  String defaultIsAlertToPartnerFraud;
  String defaultIsAlertToPartnerTech;

  String defaultIsAlertToAgent;
  String defaultIsAlertToAgentSales;
  String defaultIsAlertToAgentRF;
  String defaultIsAlertToAgentCB;
  String defaultIsAlertToAgentFraud;
  String defaultIsAlertToAgentTech;

  String defaultAlertActivation;
  String defaultSuspensionActivation;
  String defaultAlertMsg;

  String isDailyExecution;
  String isWeeklyExecution;
  String isMonthlyExecution;

  Double weeklyAlertThreshold;
  Double weeklySuspensionThreshold;
  Double monthlyAlertThreshold;
  Double monthlySuspensionThreshold;
  String displayChartType;

  public int getMonitoringParameterId()
  {
    return monitoringParameterId;
  }

  public void setMonitoringParameterId(int monitoringParameterId)
  {
    this.monitoringParameterId = monitoringParameterId;
  }

  public String getMonitoringParameterName()
  {
    return monitoringParameterName;
  }

  public void setMonitoringParameterName(String monitoringParameterName)
  {
    this.monitoringParameterName = monitoringParameterName;
  }

  public String getMonitoingParaTechName()
  {
    return monitoingParaTechName;
  }

  public void setMonitoingParaTechName(String monitoingParaTechName)
  {
    this.monitoingParaTechName = monitoingParaTechName;
  }

  public String getMonitoingCategory()
  {
    return monitoingCategory;
  }

  public void setMonitoingCategory(String monitoingCategory)
  {
    this.monitoingCategory = monitoingCategory;
  }

  public String getMonitoingKeyword()
  {
    return monitoingKeyword;
  }

  public void setMonitoingKeyword(String monitoingKeyword)
  {
    this.monitoingKeyword = monitoingKeyword;
  }

  public String getMonitoingSubKeyword()
  {
    return monitoingSubKeyword;
  }

  public void setMonitoingSubKeyword(String monitoingSubKeyword)
  {
    this.monitoingSubKeyword = monitoingSubKeyword;
  }

  public String getMonitoingAlertCategory()
  {
    return monitoingAlertCategory;
  }

  public void setMonitoingAlertCategory(String monitoingAlertCategory)
  {
    this.monitoingAlertCategory = monitoingAlertCategory;
  }

  public String getMonitoingFrequency()
  {
    return monitoingFrequency;
  }

  public void setMonitoingFrequency(String monitoingFrequency)
  {
    this.monitoingFrequency = monitoingFrequency;
  }

  public String getMonitoingOnChannel()
  {
    return monitoingOnChannel;
  }

  public void setMonitoingOnChannel(String monitoingOnChannel)
  {
    this.monitoingOnChannel = monitoingOnChannel;
  }

  public String getMonitoingDeviation()
  {
    return monitoingDeviation;
  }

  public void setMonitoingDeviation(String monitoingDeviation)
  {
    this.monitoingDeviation = monitoingDeviation;
  }

  public String getMonitoringUnit()
  {
    return monitoringUnit;
  }

  public void setMonitoringUnit(String monitoringUnit)
  {
    this.monitoringUnit = monitoringUnit;
  }

  public double getDefaultAlertThreshold()
  {
    return defaultAlertThreshold;
  }

  public void setDefaultAlertThreshold(double defaultAlertThreshold)
  {
    this.defaultAlertThreshold = defaultAlertThreshold;
  }

  public double getDefaultSuspensionThreshold()
  {
    return defaultSuspensionThreshold;
  }

  public void setDefaultSuspensionThreshold(double defaultSuspensionThreshold)
  {
    this.defaultSuspensionThreshold = defaultSuspensionThreshold;
  }

  public String getDefaultIsAlertToAdmin()
  {
    return defaultIsAlertToAdmin;
  }

  public void setDefaultIsAlertToAdmin(String defaultIsAlertToAdmin)
  {
    this.defaultIsAlertToAdmin = defaultIsAlertToAdmin;
  }

  public String getDefaultIsAlertToMerchant()
  {
    return defaultIsAlertToMerchant;
  }

  public void setDefaultIsAlertToMerchant(String defaultIsAlertToMerchant)
  {
    this.defaultIsAlertToMerchant = defaultIsAlertToMerchant;
  }

  public String getDefaultIsAlertToPartner()
  {
    return defaultIsAlertToPartner;
  }

  public void setDefaultIsAlertToPartner(String defaultIsAlertToPartner)
  {
    this.defaultIsAlertToPartner = defaultIsAlertToPartner;
  }

  public String getDefaultIsAlertToAgent()
  {
    return defaultIsAlertToAgent;
  }

  public void setDefaultIsAlertToAgent(String defaultIsAlertToAgent)
  {
    this.defaultIsAlertToAgent = defaultIsAlertToAgent;
  }

  public String getDefaultAlertActivation()
  {
    return defaultAlertActivation;
  }

  public void setDefaultAlertActivation(String defaultAlertActivation)
  {
    this.defaultAlertActivation = defaultAlertActivation;
  }

  public String getDefaultSuspensionActivation()
  {
    return defaultSuspensionActivation;
  }

  public void setDefaultSuspensionActivation(String defaultSuspensionActivation)
  {
    this.defaultSuspensionActivation = defaultSuspensionActivation;
  }

  public String getDefaultAlertMsg()
  {
    return defaultAlertMsg;
  }

  public void setDefaultAlertMsg(String defaultAlertMsg)
  {
    this.defaultAlertMsg = defaultAlertMsg;
  }

  public String getDefaultIsAlertToAdminSales()
  {
    return defaultIsAlertToAdminSales;
  }

  public void setDefaultIsAlertToAdminSales(String defaultIsAlertToAdminSales)
  {
    this.defaultIsAlertToAdminSales = defaultIsAlertToAdminSales;
  }

  public String getDefaultIsAlertToAdminRF()
  {
    return defaultIsAlertToAdminRF;
  }

  public void setDefaultIsAlertToAdminRF(String defaultIsAlertToAdminRF)
  {
    this.defaultIsAlertToAdminRF = defaultIsAlertToAdminRF;
  }

  public String getDefaultIsAlertToAdminCB()
  {
    return defaultIsAlertToAdminCB;
  }

  public void setDefaultIsAlertToAdminCB(String defaultIsAlertToAdminCB)
  {
    this.defaultIsAlertToAdminCB = defaultIsAlertToAdminCB;
  }

  public String getDefaultIsAlertToAdminFraud()
  {
    return defaultIsAlertToAdminFraud;
  }

  public void setDefaultIsAlertToAdminFraud(String defaultIsAlertToAdminFraud)
  {
    this.defaultIsAlertToAdminFraud = defaultIsAlertToAdminFraud;
  }

  public String getDefaultIsAlertToAdminTech()
  {
    return defaultIsAlertToAdminTech;
  }

  public void setDefaultIsAlertToAdminTech(String defaultIsAlertToAdminTech)
  {
    this.defaultIsAlertToAdminTech = defaultIsAlertToAdminTech;
  }

  public String getDefaultIsAlertToMerchantSales()
  {
    return defaultIsAlertToMerchantSales;
  }

  public void setDefaultIsAlertToMerchantSales(String defaultIsAlertToMerchantSales)
  {
    this.defaultIsAlertToMerchantSales = defaultIsAlertToMerchantSales;
  }

  public String getDefaultIsAlertToMerchantRF()
  {
    return defaultIsAlertToMerchantRF;
  }

  public void setDefaultIsAlertToMerchantRF(String defaultIsAlertToMerchantRF)
  {
    this.defaultIsAlertToMerchantRF = defaultIsAlertToMerchantRF;
  }

  public String getDefaultIsAlertToMerchantCB()
  {
    return defaultIsAlertToMerchantCB;
  }

  public void setDefaultIsAlertToMerchantCB(String defaultIsAlertToMerchantCB)
  {
    this.defaultIsAlertToMerchantCB = defaultIsAlertToMerchantCB;
  }

  public String getDefaultIsAlertToMerchantFraud()
  {
    return defaultIsAlertToMerchantFraud;
  }

  public void setDefaultIsAlertToMerchantFraud(String defaultIsAlertToMerchantFraud)
  {
    this.defaultIsAlertToMerchantFraud = defaultIsAlertToMerchantFraud;
  }

  public String getDefaultIsAlertToMerchantTech()
  {
    return defaultIsAlertToMerchantTech;
  }

  public void setDefaultIsAlertToMerchantTech(String defaultIsAlertToMerchantTech)
  {
    this.defaultIsAlertToMerchantTech = defaultIsAlertToMerchantTech;
  }

  public String getDefaultIsAlertToPartnerSales()
  {
    return defaultIsAlertToPartnerSales;
  }

  public void setDefaultIsAlertToPartnerSales(String defaultIsAlertToPartnerSales)
  {
    this.defaultIsAlertToPartnerSales = defaultIsAlertToPartnerSales;
  }

  public String getDefaultIsAlertToPartnerRF()
  {
    return defaultIsAlertToPartnerRF;
  }

  public void setDefaultIsAlertToPartnerRF(String defaultIsAlertToPartnerRF)
  {
    this.defaultIsAlertToPartnerRF = defaultIsAlertToPartnerRF;
  }

  public String getDefaultIsAlertToPartnerCB()
  {
    return defaultIsAlertToPartnerCB;
  }

  public void setDefaultIsAlertToPartnerCB(String defaultIsAlertToPartnerCB)
  {
    this.defaultIsAlertToPartnerCB = defaultIsAlertToPartnerCB;
  }

  public String getDefaultIsAlertToPartnerFraud()
  {
    return defaultIsAlertToPartnerFraud;
  }

  public void setDefaultIsAlertToPartnerFraud(String defaultIsAlertToPartnerFraud)
  {
    this.defaultIsAlertToPartnerFraud = defaultIsAlertToPartnerFraud;
  }

  public String getDefaultIsAlertToPartnerTech()
  {
    return defaultIsAlertToPartnerTech;
  }

  public void setDefaultIsAlertToPartnerTech(String defaultIsAlertToPartnerTech)
  {
    this.defaultIsAlertToPartnerTech = defaultIsAlertToPartnerTech;
  }

  public String getDefaultIsAlertToAgentSales()
  {
    return defaultIsAlertToAgentSales;
  }

  public void setDefaultIsAlertToAgentSales(String defaultIsAlertToAgentSales)
  {
    this.defaultIsAlertToAgentSales = defaultIsAlertToAgentSales;
  }

  public String getDefaultIsAlertToAgentRF()
  {
    return defaultIsAlertToAgentRF;
  }

  public void setDefaultIsAlertToAgentRF(String defaultIsAlertToAgentRF)
  {
    this.defaultIsAlertToAgentRF = defaultIsAlertToAgentRF;
  }

  public String getDefaultIsAlertToAgentCB()
  {
    return defaultIsAlertToAgentCB;
  }

  public void setDefaultIsAlertToAgentCB(String defaultIsAlertToAgentCB)
  {
    this.defaultIsAlertToAgentCB = defaultIsAlertToAgentCB;
  }

  public String getDefaultIsAlertToAgentFraud()
  {
    return defaultIsAlertToAgentFraud;
  }

  public void setDefaultIsAlertToAgentFraud(String defaultIsAlertToAgentFraud)
  {
    this.defaultIsAlertToAgentFraud = defaultIsAlertToAgentFraud;
  }

  public String getDefaultIsAlertToAgentTech()
  {
    return defaultIsAlertToAgentTech;
  }

  public void setDefaultIsAlertToAgentTech(String defaultIsAlertToAgentTech)
  {
    this.defaultIsAlertToAgentTech = defaultIsAlertToAgentTech;
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
