package com.manager;

import com.directi.pg.Logger;
import com.manager.dao.WiresDAO;
import com.manager.vo.TerminalVO;
import com.manager.vo.payoutVOs.MerchantWireVO;
import com.payment.exceptionHandler.PZDBViolationException;

/**
 * Created by sandip on 6/15/2017.
 */
public class WiresManager
{
    private static Logger logger = new Logger(WiresManager.class.getName());
    WiresDAO wiresDAO=new WiresDAO();
    public MerchantWireVO getMerchantRecentWire(TerminalVO terminalVO)throws PZDBViolationException
    {
        return wiresDAO.getMerchantRecentWire(terminalVO);
    }
}
