package com.payment.request;

import com.directi.pg.AuditTrailVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 2/16/13
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class PZChargebackRequest extends PZRequest
{
    private String cbAmount;

    private String cbReason;

    private boolean isAdmin;

    private String captureAmount;

    private String refundAmount;
    private String chargebackDate;

    public AuditTrailVO getAuditTrailVO()
    {
        return auditTrailVO;
    }

    public void setAuditTrailVO(AuditTrailVO auditTrailVO)
    {
        this.auditTrailVO = auditTrailVO;
    }

    public AuditTrailVO auditTrailVO;
    public boolean isAdmin()
    {
        return isAdmin;
    }

    public void setAdmin(boolean admin)
    {
        isAdmin = admin;
    }

    public String getCbAmount()
    {
        return cbAmount;
    }

    public void setCbAmount(String cbAmount)
    {
        this.cbAmount = cbAmount;
    }

    public String getCbReason()
    {
        return cbReason;
    }

    public String getCaptureAmount()
    {
        return captureAmount;
    }

    public void setCaptureAmount(String captureAmount)
    {
        this.captureAmount = captureAmount;
    }

    public void setCbReason(String cbReason)
    {
        this.cbReason = cbReason;
    }

    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public String getChargebackDate()
    {
        return chargebackDate;
    }

    public void setChargebackDate(String chargebackDate)
    {
        this.chargebackDate = chargebackDate;
    }
}
