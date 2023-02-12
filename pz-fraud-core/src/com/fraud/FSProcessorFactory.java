package com.fraud;

import com.fraud.at.ATFraudProcessor;
import com.fraud.fourstop.FourStopFSGateway;
import com.fraud.fourstop.FourStopFraudProcessor;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/31/14
 * Time: 7:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class FSProcessorFactory
{
    public static AbstractFraudProcessor getFraudProcessorInstance(String fsname)
    {
        if(fsname.equals(ATFSGateway.FSNAME))
        {
            return new ATFraudProcessor();
        }
        if(fsname.equals(FourStopFSGateway.FSNAME))
        {
            return new FourStopFraudProcessor();
        }
        return null;
    }
}
