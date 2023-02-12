package com.directi.pg.core.valueObjects;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 20, 2012
 * Time: 9:38:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayLineVoucherTransDetailsVO  extends GenericTransDetailsVO
{
     String referenceId ;

    public PayLineVoucherTransDetailsVO()
    {
    }

    public String getReferenceId()
    {
        return referenceId;
    }

    public void setReferenceId(String referenceId)
    {
        this.referenceId = referenceId;
    }
}
