package com.manager;

import com.directi.pg.DefaultUser;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.dao.AuthenticationDAO;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;

import java.util.List;

/**This is for Security Purpose only
 * Created by NIKET on 11-06-2016.
 */
public class AuthenticationManager
{
    private Logger logger = new Logger(AuthenticationManager.class.getName());

    private Functions functions = new Functions();

    private AuthenticationDAO authenticationDAO = new AuthenticationDAO();

    public List<DefaultUser> getAllLockedUser(PaginationVO paginationVO) throws PZDBViolationException
    {
        return authenticationDAO.getAllLockedUser(paginationVO);
    }

    public boolean unlockUser(String accountId) throws PZDBViolationException
    {
        return authenticationDAO.unlockUser(accountId);
    }

    public boolean getUnBlockedAccount(String login)
    {
        return authenticationDAO.getUnBlockedAccount(login);
    }
}
