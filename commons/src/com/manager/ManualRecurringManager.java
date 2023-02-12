package com.manager;

import com.directi.pg.ActionEntry;
import com.directi.pg.AuditTrailVO;
import com.directi.pg.Logger;
import com.directi.pg.core.valueObjects.QwipiResponseVO;
import com.manager.dao.ManualRecurringDAO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by admin on 10/28/2015.
 */
public class ManualRecurringManager
{
    private static Logger logger = new Logger(ManualRecurringManager.class.getName());
    private static ManualRecurringDAO manualRecurringDAO= new ManualRecurringDAO();
    private static ActionEntry actionEntry=new ActionEntry();

    public CommonValidatorVO getQwipiDetailsForManualRebill(String trackingId) throws PZDBViolationException
    {
        return manualRecurringDAO.getQwipiManualRebillDetails(trackingId);
    }
    public int insertNewTransactionQwipi(CommonValidatorVO commonValidatorVO,String status,String header, String desc, String amount ) throws PZDBViolationException
    {
        int trackingId = 0;
        trackingId = manualRecurringDAO.insertNewTransactionQwipi(commonValidatorVO, status, header,desc,amount);
        return trackingId;
    }
    public void updateQwipiRebillAfterResponse(QwipiResponseVO qwipiResponseVO, String status, String trackingid) throws PZDBViolationException
    {
        manualRecurringDAO.updateAuthstartedQwipiRebill(qwipiResponseVO,status,trackingid);
    }
    public CommonValidatorVO getCommontransactionDetails(String trackingid)throws PZDBViolationException
    {
        return manualRecurringDAO.getTransDetailsForCommon(trackingid);
    }
    public int insertAuthStartedForCommon(CommonValidatorVO commonValidatorVO,String status)throws PZDBViolationException
    {
        int trackingId=0;
        trackingId=manualRecurringDAO.insertAuthStartedForCommon(commonValidatorVO);
        return trackingId;

    }
    public void checkAmountlimitForRebill(String amount,String memberid,String accountid) throws PZConstraintViolationException
    {
        manualRecurringDAO.checkAmountLimitForRebill(amount,memberid,accountid);

    }
    public CommonValidatorVO getCashflowManualRebillData(CommonValidatorVO commonValidatorVO, String trackingId) throws PZDBViolationException
    {
        return manualRecurringDAO.getCashflowManualRebillData(commonValidatorVO, trackingId);
    }
    public CommonValidatorVO getPayforasiaManualRebilldata(CommonValidatorVO commonValidatorVO,String trackingId) throws PZDBViolationException
    {
        return manualRecurringDAO.getPayforasiaManualRebillData(commonValidatorVO, trackingId);
    }
}
