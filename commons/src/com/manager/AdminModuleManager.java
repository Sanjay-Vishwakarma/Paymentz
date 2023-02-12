package com.manager;

import com.directi.pg.Logger;
import com.manager.dao.AdminModuleDAO;
import com.manager.vo.AdminModuleVO;
import com.manager.vo.AdminModulesMappingVO;
import com.payment.exceptionHandler.PZDBViolationException;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 3/9/15
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminModuleManager
{
    Logger logger=new Logger(AdminModuleManager.class.getName());
    private AdminModuleDAO adminModuleDAO=new AdminModuleDAO();


    public Set<String> getAdminAccessModuleSet(String adminId)throws PZDBViolationException
    {
        logger.debug("Entering Into AdminModule Manager");
        return adminModuleDAO.getAdminAccessModuleSet(adminId);
    }
    public String addNewAdminModule(AdminModuleVO adminModuleVO)throws PZDBViolationException
    {
        return adminModuleDAO.addNewAdminModule(adminModuleVO);
    }
    public boolean isUniqueName(AdminModuleVO adminModuleVO)throws PZDBViolationException
    {
        return adminModuleDAO.isUniqueName(adminModuleVO);
    }
    public String addNewModuleMapping(AdminModulesMappingVO adminModulesMappingVO)throws PZDBViolationException
    {
        return adminModuleDAO.addNewModuleMapping(adminModulesMappingVO);
    }
    public boolean isMappingAvailable(AdminModulesMappingVO adminModulesMappingVO)throws PZDBViolationException
    {
        return adminModuleDAO.isMappingAvailable(adminModulesMappingVO);
    }
    public boolean removeAdminModuleMapping(String  moduleId)throws PZDBViolationException
    {
        return adminModuleDAO.removeAdminModuleMapping(moduleId);
    }
    public boolean isCronMappingAvailable(String adminid)throws PZDBViolationException
    {
        return  adminModuleDAO.isCronMappingAvailable(adminid);
    }
}
