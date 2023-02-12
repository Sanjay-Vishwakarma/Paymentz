package com.manager;

import com.manager.dao.RefundDAO;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TransactionVO;
import com.payment.exceptionHandler.PZDBViolationException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 12/19/14
 * Time: 9:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class RefundManager
{
    RefundDAO refundDAO = new RefundDAO();
    public List<TransactionVO> getQwipiRefundList(TransactionVO transactionVO,InputDateVO inputDateVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        return  refundDAO.getQwipiRefundList(transactionVO,inputDateVO,paginationVO);
    }

    public List<TransactionVO> getEcoreRefundList(TransactionVO transactionVO,InputDateVO inputDateVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        return refundDAO.getEcoreRefundList(transactionVO,inputDateVO,paginationVO);
    }

    public List<TransactionVO> getCommonRefundList(TransactionVO transactionVO,InputDateVO inputDateVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        return refundDAO.getCommonRefundList(transactionVO,inputDateVO,paginationVO);
    }
}
