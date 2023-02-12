package com.payment.payon.core;

import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/28/12
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayOnVBVDetailsVO extends GenericVO
{

    private String eci;
    private String verification;
    private String xid;

    public String getEci()
    {
        return eci;
    }

    public void setEci(String eci)
    {
        this.eci = eci;
    }

    public String getVerification()
    {
        return verification;
    }

    public void setVerification(String verification)
    {
        this.verification = verification;
    }

    public String getXid()
    {
        return xid;
    }

    public void setXid(String xid)
    {
        this.xid = xid;
    }
}
