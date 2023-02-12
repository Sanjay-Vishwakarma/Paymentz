package com.manager.vo.merchantmonitoring;

/**
 * Created by Vishal on 7/29/2016.
 */
public class MonitoringRuleLogVO
{
    String historyId;
    String actionExecutor;
    String modifiedOn;

    MonitoringParameterMappingVO monitoringParameterMappingVO;

    public String getHistoryId()
    {
        return historyId;
    }

    public void setHistoryId(String historyId)
    {
        this.historyId = historyId;
    }

    public String getActionExecutor()
    {
        return actionExecutor;
    }
    public void setActionExecutor(String actionExecutor)
    {
        this.actionExecutor = actionExecutor;
    }
    public String getModifiedOn()
    {
        return modifiedOn;
    }

    public void setModifiedOn(String modifiedOn)
    {
        this.modifiedOn = modifiedOn;
    }
    public MonitoringParameterMappingVO getMonitoringParameterMappingVO()
    {
        return monitoringParameterMappingVO;
    }
    public void setMonitoringParameterMappingVO(MonitoringParameterMappingVO monitoringParameterMappingVO)
    {
        this.monitoringParameterMappingVO = monitoringParameterMappingVO;
    }
}
