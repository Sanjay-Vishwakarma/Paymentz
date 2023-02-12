package com.transaction.manager;

import com.manager.vo.MerchantDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.transaction.dao.TransactionAuthDAO;

/**
 * Created by Admin on 6/7/2017.
 */
public class TransactionAuthManager
{
    TransactionAuthDAO authDAO = new TransactionAuthDAO();

    public MerchantDetailsVO getMerchantKey(String login) throws PZDBViolationException
    {
        return authDAO.getMerchantKey(login);
    }
}
