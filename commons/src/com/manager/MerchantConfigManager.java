package com.manager;

import com.directi.pg.Logger;
import com.manager.dao.AccountDAO;
import com.manager.dao.MerchantDAO;
import com.manager.vo.InputDateVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.memeberConfigVOS.MemberAccountMappingVO;
import com.manager.vo.memeberConfigVOS.MerchantConfigCombinationVO;
import com.manager.vo.payoutVOs.MerchantWireVO;
import com.payment.exceptionHandler.PZDBViolationException;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: NIKET
 * Date: 12/11/14
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantConfigManager
{
    private static  Logger logger = new Logger(MerchantConfigManager.class.getName());

    private static MerchantDAO merchantDAO = new MerchantDAO();
    private static AccountDAO accountDAO = new AccountDAO();
    //getting single member Details & MemberAccountMapping Details
    public MerchantConfigCombinationVO setMemberDetailsAndMemberAccountDetails(TerminalVO terminalVO) throws PZDBViolationException
    {
        MemberAccountMappingVO memberAccountMappingVO = null;

        MerchantConfigCombinationVO merchantConfigCombinationVO = new MerchantConfigCombinationVO();
        merchantConfigCombinationVO.setMerchantDetailsVO(merchantDAO.getMemberDetails(terminalVO.getMemberId()));
        memberAccountMappingVO=merchantDAO.getMemberAccountMappingDetails(terminalVO).get(0);
        merchantConfigCombinationVO.setChargeVOs(accountDAO.getChargesAsPerTerminal(terminalVO));
        merchantConfigCombinationVO.setMemberAccountMappingVO(memberAccountMappingVO);
        return merchantConfigCombinationVO;
    }

    public MerchantDetailsVO getMerchantDetailFromToId(String toid) throws PZDBViolationException
    {
        MerchantDetailsVO merchantDetailsVO =new MerchantDetailsVO();
        merchantDetailsVO = merchantDAO.getMemberDetails(toid);
        return merchantDetailsVO;
    }
    public List<MerchantWireVO> getAgentMerchantWireReport(String agentid, String terminalid, InputDateVO dateVO, String memberid,String status, PaginationVO paginationVO) throws PZDBViolationException
    {
        return merchantDAO.getAgentMerchantWireReport(agentid,terminalid,dateVO,memberid,status,paginationVO);
    }

    public Map<String,Object> getSavedMemberTemplateDetails(String memberId) throws PZDBViolationException
    {
        return merchantDAO.getSavedMemberTemplateDetails(memberId);
    }
    public Map<String,Object> getMemberPartnerTemplateDetails(String partnerid) throws PZDBViolationException
    {
        return merchantDAO.getMemberPartnerTemplateDetails(partnerid);
    }
    public boolean insertMemberTemplateDetails(Map<String, Object> merchantTemplateInformation, String memberId) throws PZDBViolationException
    {
        return merchantDAO.insertTemplatePreferences(merchantTemplateInformation, memberId);
    }

    public boolean updateMemberTemplateDetails(Map<String, Object> merchantTemplateInformation, String memberId) throws PZDBViolationException
    {
        return merchantDAO.updateTemplatePreferences(merchantTemplateInformation, memberId);
    }

    public boolean deleteMemberTemplateDetails(Map<String, Object> merchantTemplateInformation, String memberId) throws PZDBViolationException
    {
        return merchantDAO.deleteMemberTemplateDetails(merchantTemplateInformation,memberId);
    }
}
