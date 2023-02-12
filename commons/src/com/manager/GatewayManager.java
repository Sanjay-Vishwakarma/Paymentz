package com.manager;

import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.manager.dao.GatewayAccountDAO;
import com.manager.dao.GatewayTypeDAO;
import com.manager.vo.FileDetailsVO;
import com.manager.vo.gatewayVOs.GatewayAccountVO;
import com.manager.vo.gatewayVOs.GatewayTypeVO;
import com.payment.exceptionHandler.PZDBViolationException;

import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 9/1/14
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class GatewayManager
{
    //global declaration
    GatewayAccountDAO gatewayAccountDAO = new GatewayAccountDAO();
    GatewayTypeDAO gatewayTypeDAO=new GatewayTypeDAO();
    //Functions instance
    Functions functions = new Functions();

    public GatewayTypeVO getGatewayTypeForPgTypeId(String pgTypeId)
    {
        GatewayTypeVO gatewayTypeVO = new GatewayTypeVO();
        gatewayTypeVO.setPgTYypeId(pgTypeId);
        gatewayTypeVO.setGatewayType(GatewayTypeService.getGatewayType(pgTypeId));
        return gatewayTypeVO;
    }

    public GatewayAccountVO getGatewayAccountForAccountId(String accountId)
    {
        GatewayAccountVO gatewayAccountVO = new GatewayAccountVO();
        gatewayAccountVO.setAccountId(accountId);
        gatewayAccountVO.setGatewayAccount(GatewayAccountService.getGatewayAccount(accountId));
        return gatewayAccountVO;
    }

    public List<GatewayTypeVO>  getListOfAllGatewayType()
    {
        List<GatewayTypeVO> gatewayTypeVOList = new ArrayList<GatewayTypeVO>();

        List<GatewayType> gatewayTypes = GatewayTypeService.getAllGatewayTypes();
        for(GatewayType gatewayType : gatewayTypes)
        {
            GatewayTypeVO gatewayTypeVO = new GatewayTypeVO();
            gatewayTypeVO.setPgTYypeId(gatewayType.getPgTypeId());
            gatewayTypeVO.setGatewayType(gatewayType);
            gatewayTypeVOList.add(gatewayTypeVO);
        }
        return gatewayTypeVOList;
    }

    public List<GatewayAccountVO> getListOfAllGatewayAccount()
    {
        List<GatewayAccountVO> gatewayAccountVOList = new ArrayList<GatewayAccountVO>();

        for(Map.Entry<String, GatewayAccount> gatewayAccountPairs : GatewayAccountService.gatewayAccounts.entrySet())
        {
            GatewayAccountVO gatewayAccountVO = new GatewayAccountVO();
            gatewayAccountVO.setAccountId(gatewayAccountPairs.getKey());
            gatewayAccountVO.setGatewayAccount(gatewayAccountPairs.getValue());
            gatewayAccountVOList.add(gatewayAccountVO);
        }
        return gatewayAccountVOList;
    }

    public GatewayAccountVO getGatewayAccountAsPerPgTypeIdAndAccountId(String accountId,String pgTypeId)
    {
        return (gatewayAccountDAO.getGatewayAccountFromPgTypeIdAndAccountId(accountId,pgTypeId));
    }
    public String getGatewayAccountFirstTrans(String accountId,String tableName)
    {
        return gatewayAccountDAO.getGatewayAccountFirstTrans(accountId,tableName);
    }
    public boolean isRefereedToBank(String agentId,String pgTypeId) throws Exception
    {
        return gatewayTypeDAO.isRefereedToBank(agentId,pgTypeId);
    }
    public boolean isPartnerRefereedToBank(String partnerId,String pgTypeId) throws Exception
    {
        return gatewayTypeDAO.isPartnerRefereedToBank(partnerId,pgTypeId);
    }
    public boolean isRefereedToBankAnyBank(String agentId) throws Exception
    {
        return gatewayTypeDAO.isRefereedToBankAnyBank(agentId);
    }
    public boolean isPartnerRefereedToAnyBank(String partnerId) throws Exception
    {
        return gatewayTypeDAO.isPartnerRefereedToAnyBank(partnerId);
    }
    public String addNewGatewayType(GatewayType gatewayType)
    {
        boolean status=gatewayTypeDAO.isGatewayTypeAvailable(gatewayType);
        String result="";
        if(status)//true
        {
            result="Gateway Type with same name and Currency Exists, Please specify some other name";
        }
        else
        {
            result=gatewayTypeDAO.addNewGatewayType(gatewayType);
        }
        return result;
    }
    public String updateGatewayType(GatewayType gatewayType)
    {
        return gatewayTypeDAO.updateGatewayType(gatewayType);
    }
    public List<GatewayType> getGatewayTypeRefereedByAgent(String agentId)throws SystemError,SQLException
    {
        return gatewayTypeDAO.getGatewayTypeRefereedByAgent(agentId);
    }
    public List<GatewayType> getGatewayTypeRefereedByPartner(String partnerId)throws SystemError,SQLException
    {
        return gatewayTypeDAO.getGatewayTypeRefereedByPartner(partnerId);
    }

    public TreeMap<String, String> getGatewayTypePartner(String partnerId)throws SystemError,SQLException
    {
        return gatewayTypeDAO.getGatewayTypePartner(partnerId);
    }
    public TreeMap<String, String> getIssuingBankForMerchant(String merchantid)throws SystemError,SQLException
    {
        return gatewayTypeDAO.getIssuingBankForMerchant(merchantid);
    }
    public TreeMap<String, String> getIssuingBankForPartnert(String partnername)throws SystemError,SQLException
    {
        return gatewayTypeDAO.getIssuingBankForPartnert(partnername);
    }
    public Set<String> getGatewayAccounts(String gateway)throws SystemError
    {
        return gatewayAccountDAO.getGatewayAccounts(gateway);
    }
    //this is to get the gateway type Vo for which the table name is there
    public List<GatewayTypeVO>  getListOfAllGatewayTypeWithAvailableTemplateName()
    {
        List<GatewayTypeVO> gatewayTypeVOList = new ArrayList<GatewayTypeVO>();

        List<GatewayType> gatewayTypes = GatewayTypeService.getAllGatewayTypes();
        for(GatewayType gatewayType : gatewayTypes)
        {
            if(functions.isValueNull(gatewayType.getTemplatename()))
            {
                GatewayTypeVO gatewayTypeVO = new GatewayTypeVO();
                gatewayTypeVO.setPgTYypeId(gatewayType.getPgTypeId());
                gatewayTypeVO.setGatewayType(gatewayType);
                gatewayTypeVOList.add(gatewayTypeVO);
            }
        }
        return gatewayTypeVOList;
    }
    //update the template name
    public boolean updateTemplateNameOfGatewayType(FileDetailsVO fileDetailsVO) throws PZDBViolationException
    {
       return gatewayTypeDAO.updateTemplateNameOfGatewayType(fileDetailsVO);
    }

    /**
     * Get all partner mapped gateway
     * @param partnerId
     * @return
     * @throws PZDBViolationException
     */
    public List<GatewayTypeVO> getAllGatewayMappedToPartner(String partnerId) throws PZDBViolationException
    {
        return gatewayTypeDAO.getAllGatewayForPartner(partnerId);
    }
    public boolean updateDefaultApplicationGatewayForPartnerAndPgTypeId(String partnerId,String pgTypeId,boolean defaultApplication) throws PZDBViolationException
    {
        return gatewayTypeDAO.updateDefaultApplicationGatewayForPartnerAndPgTypeId(partnerId, pgTypeId,defaultApplication);
    }

    public List<GatewayType> getPartnerProcessingBanksList(String partnerId) throws SystemError, SQLException
    {
        return gatewayTypeDAO.getPartnerProcessingBanksList(partnerId);
    }

    public Set<String> getGatewayList(String partnerid, String merchantid, String accoutId) throws PZDBViolationException
    {
        return gatewayTypeDAO.getGatewayList(merchantid, partnerid, accoutId);
    }
}
