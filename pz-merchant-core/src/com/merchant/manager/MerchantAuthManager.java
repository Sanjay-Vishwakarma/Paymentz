package com.merchant.manager;

import com.manager.vo.MerchantDetailsVO;
import com.merchant.dao.MerchantAuthDAO;
import com.payment.exceptionHandler.PZDBViolationException;

/**
 * Created by Admin on 6/7/2017.
 */
public class MerchantAuthManager
{
    MerchantAuthDAO merchantAuthDAO = new MerchantAuthDAO();

    public MerchantDetailsVO getMerchantKey(String login) throws PZDBViolationException
    {
        return merchantAuthDAO.getMerchantKey(login);
    }
}
