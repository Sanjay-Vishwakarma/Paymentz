package com.manager.vo.merchantmonitoring;


import java.util.Date;

/**
 * Created by Sandip on 5/19/16.
 */
public class MonitoringAlertDetailVO
{
    String monitoringAlertName;
    String monitoringAlertPeriod;
    boolean isAlertToAdmin;
    boolean isAlertToMerchant;
    boolean isAlertToPartner;
    boolean isAlertToAgent;
    String intimationDepartment;
    String alertType;
    boolean isAlertOnly;
    boolean isSuspension;
    boolean isAlertWithAttachment;
    String alertMsg;


    String memberId;
    String alertTeam;
    String reportFileName;
    double actualratio;
    double alertThreshold;
    double suspensionThreshold;
    Date monitroingStartDate;
    Date monitroingEndDate;

    Date alertDate;
    int alertId;
    String terminalId;
    int id;

    public int getId(){return id; }

    public void setId(int id){this.id = id; }

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

    public Date getMonitroingStartDate()
    {
        return monitroingStartDate;
    }

    public void setMonitroingStartDate(Date monitroingStartDate)
    {
        this.monitroingStartDate = monitroingStartDate;
    }

    public Date getMonitroingEndDate()
    {
        return monitroingEndDate;
    }

    public void setMonitroingEndDate(Date monitroingEndDate)
    {
        this.monitroingEndDate = monitroingEndDate;
    }

    public Date getAlertDate()
    {
        return alertDate;
    }

    public void setAlertDate(Date alertDate)
    {
        this.alertDate = alertDate;
    }

    public int getAlertId()
    {
        return alertId;
    }

    public void setAlertId(int alertId)
    {
        this.alertId = alertId;
    }

    public String getTerminalId()
    {
        return terminalId;
    }

    public void setTerminalId(String terminalId)
    {
        this.terminalId = terminalId;
    }

    public String getMonitoringAlertName()
    {
        return monitoringAlertName;
    }

    public void setMonitoringAlertName(String monitoringAlertName)
    {
        this.monitoringAlertName = monitoringAlertName;
    }

    public String getMonitoringAlertPeriod()
    {
        return monitoringAlertPeriod;
    }

    public void setMonitoringAlertPeriod(String monitoringAlertPeriod)
    {
        this.monitoringAlertPeriod = monitoringAlertPeriod;
    }

    public boolean isAlertToAdmin()
    {
        return isAlertToAdmin;
    }

    public void setAlertToAdmin(boolean isAlertToAdmin)
    {
        this.isAlertToAdmin = isAlertToAdmin;
    }

    public boolean isAlertToMerchant()
    {
        return isAlertToMerchant;
    }

    public void setAlertToMerchant(boolean isAlertToMerchant)
    {
        this.isAlertToMerchant = isAlertToMerchant;
    }

    public boolean isAlertToPartner()
    {
        return isAlertToPartner;
    }

    public void setAlertToPartner(boolean isAlertToPartner)
    {
        this.isAlertToPartner = isAlertToPartner;
    }

    public boolean isAlertToAgent()
    {
        return isAlertToAgent;
    }

    public void setAlertToAgent(boolean isAlertToAgent)
    {
        this.isAlertToAgent = isAlertToAgent;
    }

    public String getIntimationDepartment()
    {
        return intimationDepartment;
    }

    public void setIntimationDepartment(String intimationDepartment){ this.intimationDepartment = intimationDepartment; }

    public String getAlertType()
    {
        return alertType;
    }

    public void setAlertType(String alertType)
    {
        this.alertType = alertType;
    }

    public boolean isAlertOnly()
    {
        return isAlertOnly;
    }

    public void setAlertOnly(boolean isAlertOnly)
    {
        this.isAlertOnly = isAlertOnly;
    }

    public boolean isSuspension()
    {
        return isSuspension;
    }

    public void setSuspension(boolean isSuspension)
    {
        this.isSuspension = isSuspension;
    }

    public boolean isAlertWithAttachment()
    {
        return isAlertWithAttachment;
    }

    public void setAlertWithAttachment(boolean isAlertWithAttachment)
    {
        this.isAlertWithAttachment = isAlertWithAttachment;
    }

    public double getActualratio(){ return actualratio; }

    public void setActualratio(double actualratio){ this.actualratio = actualratio; }

    public String getAlertMsg()
    {
        return alertMsg;
    }

    public void setAlertMsg(String alertMsg)
    {
        this.alertMsg = alertMsg;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getAlertTeam()
    {
        return alertTeam;
    }

    public void setAlertTeam(String alertTeam)
    {
        this.alertTeam = alertTeam;
    }

    public String getReportFileName()
    {
        return reportFileName;
    }

    public void setReportFileName(String reportFileName)
    {
        this.reportFileName = reportFileName;
    }

}
