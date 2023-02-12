package com.manager;
import com.directi.pg.SystemError;
import com.manager.dao.WLPartnerInvoiceDAO;
import com.manager.vo.WLPartnerCommissionVO;
import com.manager.vo.WLPartnerInvoiceVO;
import com.payment.exceptionHandler.PZDBViolationException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

/**
 *
 Created by IntelliJ IDEA.
 User: Supriya
 Date: 5/9/2016
 Time: 3:06 PM
 To change this template use File | Settings | File Templates.
 */
public class WLPartnerInvoiceManager
{
    WLPartnerInvoiceDAO wlPartnerInvoiceDAO=new WLPartnerInvoiceDAO();
    public List<WLPartnerCommissionVO> getWLPartnerInvoiceCommissions(String partnerId, String pgtypeId)throws PZDBViolationException
    {
        return  wlPartnerInvoiceDAO.getWLPartnerInvoiceCommissions(partnerId,pgtypeId);
    }
    public Hashtable getListOfWLPartnerInvoices(String fdtstamp, String tdtstamp, String partnerId, String isPaid,int start, int end)throws  PZDBViolationException
    {
        return wlPartnerInvoiceDAO.getListOfWLPartnerInvoices(fdtstamp, tdtstamp, partnerId, isPaid, start, end);
    }

    public Hashtable getListOfWLPartnerCommissions(String partnerId, String pgtypeId,int start, int end)throws  PZDBViolationException
    {
        return wlPartnerInvoiceDAO.getListOfWLPartnerCommissions(partnerId, pgtypeId,start, end);
    }

    public double getWLPartnerUnpaidAmount(String partnerId)
    {
        return wlPartnerInvoiceDAO.getWLPartnerUnpaidAmount(partnerId);
    }

    public String getPartnerFirstTransactionDate(String partnerName) throws PZDBViolationException
    {
        return wlPartnerInvoiceDAO.getPartnerFirstTransactionDate(partnerName);
    }

    public WLPartnerInvoiceVO getWLInvoiceReport(String invoiceId) throws SystemError, SQLException
    {
        return wlPartnerInvoiceDAO.getWLInvoiceReport(invoiceId);
    }

    public boolean updateWLPartnerInvoice(WLPartnerInvoiceVO wlPartnerInvoiceVO) throws SQLException, SystemError
    {
        return wlPartnerInvoiceDAO.updateWLPartnerInvoice(wlPartnerInvoiceVO);
    }

    public  List<WLPartnerInvoiceVO> getListOfAllWLPartnerGateways() throws  PZDBViolationException
    {
        return wlPartnerInvoiceDAO.getListOfAllWLPartnerGateways();
    }

}
