package com.manager;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.dao.AdminDAO;
import com.manager.vo.AdminDetailsVO;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.User;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 3/11/15
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminManager
{
    private static Logger logger=new Logger(AdminManager.class.getName());
    AdminDAO adminDAO=new AdminDAO();

    public List<AdminDetailsVO> getAdminUsersList(AdminDetailsVO adminDetailsVO,PaginationVO paginationVO)throws PZDBViolationException
    {
        logger.debug("Entering into Admin Manager inside getAdminUsersList");
        return adminDAO.getAdminUsersList(adminDetailsVO,paginationVO);
    }
    public boolean isUniqueLoginName(AdminDetailsVO adminDetailsInputVO)throws PZDBViolationException
    {
        return adminDAO.isUniqueName(adminDetailsInputVO);
    }
    public String addNewAdminUser(AdminDetailsVO adminDetailsInputVO,User user)throws PZDBViolationException
    {
      return  adminDAO.addNewAdminUser(adminDetailsInputVO,user);
    }
    public void removeAdminUserEntries(AdminDetailsVO adminDetailsVO)throws PZDBViolationException
    {
      adminDAO.removeAdminUserEntries(adminDetailsVO);
    }
    public boolean adminForgotPassword(String login, User user, String remoteAddr, String Header, String actionExecuterId,String adminid) throws SystemError
    {
        return adminDAO.adminForgotPassword(login,user,remoteAddr,Header,actionExecuterId,adminid);
    }
    public String getLoginAndisActive(String adminid)
    {
        return adminDAO.getLoginAndisActive(adminid);
    }
    public  String getAdminFromLogin(String login)
    {
        return adminDAO.getAdminFromLogin(login);
    }
    public  String getUpdateAction(String adminid, String isActive, String login)
    {
        return adminDAO.getUpdateAction(adminid,isActive,login);
    }
}
