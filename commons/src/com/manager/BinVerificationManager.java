package com.manager;

import com.manager.dao.BinVerificationDao;
import com.manager.vo.BinResponseVO;
import com.payment.exceptionHandler.PZDBViolationException;
/**
 * Created by Jitendra on 14-Jun-18.
 */

public class BinVerificationManager
{
    BinVerificationDao binVerificationDao=new BinVerificationDao();
    public BinResponseVO getBinDetailsFromFirstSix(String bin)throws PZDBViolationException
    {
        return binVerificationDao.getBinDetailsFromFirstSix(bin);
    }
}
