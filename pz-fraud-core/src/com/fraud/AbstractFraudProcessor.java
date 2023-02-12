package com.fraud;

import com.fraud.vo.*;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/28/14
 * Time: 8:46 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract  class AbstractFraudProcessor
{
    public abstract PZFraudResponseVO newTransaction(PZFraudRequestVO requestVO,String fsid);

    public abstract PZFraudResponseVO updateTransaction(PZFraudRequestVO requestVO,String fsid);

    public abstract PZFraudCustRegResponseVO customerRegistration(PZFraudCustRegRequestVO requestVO,String fsid);

    public abstract PZFraudDocVerifyResponseVO documentIdVerify(PZFraudDocVerifyRequestVO requestVO, String fsid);
}
