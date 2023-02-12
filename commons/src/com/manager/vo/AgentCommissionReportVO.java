package com.manager.vo;
import java.util.List;

/**
 * Created by sandip on 10/14/2015.
 */
public class AgentCommissionReportVO extends CommissionReportVO
{
    AgentDetailsVO agentDetailsVO;
    List<AgentCommissionVO> agentCommissionVOList;

    public AgentDetailsVO getAgentDetailsVO()
    {
        return agentDetailsVO;
    }
    public void setAgentDetailsVO(AgentDetailsVO agentDetailsVO)
    {
        this.agentDetailsVO = agentDetailsVO;
    }
    public List<AgentCommissionVO> getAgentCommissionVOList()
    {
        return agentCommissionVOList;
    }
    public void setAgentCommissionVOList(List<AgentCommissionVO> agentCommissionVOList)
    {
        this.agentCommissionVOList = agentCommissionVOList;
    }
}
