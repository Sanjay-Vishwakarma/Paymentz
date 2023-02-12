package com.manager.dao;

import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayType;
import com.manager.vo.CommissionVO;
import com.manager.vo.ISOCommReportVO;
import com.manager.vo.WLPartnerCommissionVO;
import com.manager.vo.WLPartnerInvoiceVO;
import com.payment.exceptionHandler.PZDBViolationException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
/**
 * Created with IntelliJ IDEA.
 * User: Sandip
 * Date: 19/8/16
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommissionManager
{
    CommissionDAO commissionDAO=new CommissionDAO();
    public List<CommissionVO> getGatewayAccountCommission(String accountId)throws PZDBViolationException
    {
      return commissionDAO.getGatewayAccountCommission(accountId);
    }
    public List<CommissionVO> getGatewayAccountDynamicInputCommissions(String accountId)throws PZDBViolationException
    {
        return commissionDAO.getGatewayAccountDynamicInputCommissions(accountId);
    }
    public ISOCommReportVO getLastISOCommWireReport(GatewayAccount gatewayAccount)throws SystemError,SQLException
    {
        return commissionDAO.getLastISOCommWireReport(gatewayAccount);
    }
    public ISOCommReportVO getISOCommWireReport(String isoCommId)throws SystemError,SQLException
    {
        return commissionDAO.getISOCommWireReport(isoCommId);
    }
    public long getGatewayAccountWireCount(GatewayAccount gatewayAccount)throws PZDBViolationException
    {
        return commissionDAO.getGatewayAccountWireCount(gatewayAccount);
    }
    public long getWLPartnerWireCount(GatewayType gatewayType, String partnerId) throws PZDBViolationException
    {
        return commissionDAO.getWLPartnerWireCount(gatewayType, partnerId);
    }

    public long getWLPartnerWireCount(String partnerId) throws PZDBViolationException
    {
        return commissionDAO.getWLPartnerWireCount(partnerId);
    }
    public List<WLPartnerCommissionVO> getWLPartnerDynamicCommissions(String partnerId, String pgtypeId) throws PZDBViolationException
    {
        return commissionDAO.getWLPartnerDynamicCommissions(partnerId, pgtypeId);
    }

    public List<WLPartnerCommissionVO> getWLPartnerDynamicCommissions(String partnerId) throws PZDBViolationException
    {
        return commissionDAO.getWLPartnerDynamicCommissions(partnerId);
    }
    public List<WLPartnerCommissionVO> getWLPartnerCommissionsList(String partnerId, String pgTypeId) throws PZDBViolationException
    {
        return commissionDAO.getWLPartnerCommissionsList(partnerId, pgTypeId);
    }
    public WLPartnerInvoiceVO getLastWLPartnerInvoiceDetails(String pgTypeId, String partnerId) throws PZDBViolationException
    {
        return commissionDAO.getLastWLPartnerInvoiceDetails(pgTypeId, partnerId);
    }

    public HashMap<String, List<WLPartnerCommissionVO>> getWLPartnerCommissionsList(String partnerId) throws PZDBViolationException
    {
        return commissionDAO.getWLPartnerCommissionsList(partnerId);
    }

    public WLPartnerInvoiceVO getLastWLPartnerInvoiceDetails(String partnerId) throws PZDBViolationException
    {
        return commissionDAO.getLastWLPartnerInvoiceDetails(partnerId);
    }
}
