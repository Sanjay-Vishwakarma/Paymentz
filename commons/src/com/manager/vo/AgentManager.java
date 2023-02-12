package com.manager.vo;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.dao.AgentDAO;
import java.util.Hashtable;
import java.util.List;
/**
 * Created by admin on 19-04-2017.
 */
public class AgentManager
{
    Logger logger = new Logger(AgentManager.class.getName());
    AgentDAO agentDAO = new AgentDAO();

    Functions functions = new Functions();

    public List<AgentDetailsVO> getAgentDetails() throws Exception
    {

        return agentDAO.getAgentDetails();
    }
    public Hashtable getAgentWireList(String toid,String terminalid,String agentId,String is_paid,String fdtstamp, String tdtstamp,int pageno, int records)
    {
        return AgentDAO.getAgentWireList(toid,terminalid, agentId, is_paid, fdtstamp, tdtstamp, pageno, records);
    }
    public List<MerchantDetailsVO> getAgentMemberListFromMapping()
    {
        return agentDAO.getAgentMemberListFromMapping();
    }

    public List<MerchantDetailsVO> getAgentMemberListFromMapped(String agentid)
    {
        return agentDAO.getAgentMemberListFromMapped(agentid);
    }
    public List<MerchantDetailsVO> getAgentMemberAccountListFromMapped(String memberId,String agentId)
    {
        return agentDAO.getAgentMemberAccountListFromMapped(memberId,agentId);
    }
}
