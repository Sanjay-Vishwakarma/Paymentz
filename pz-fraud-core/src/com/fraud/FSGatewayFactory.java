package com.fraud;

import com.fraud.fourstop.FourStopFSGateway;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/30/14
 * Time: 7:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class FSGatewayFactory
{
    public static AbstractFSGateway getFSGatewayInstance(String fsGateway)
    {
        if(fsGateway.equals(ATFSGateway.FSNAME))
        {
            return new ATFSGateway();
        }
        else if(fsGateway.equals(FourStopFSGateway.FSNAME))
        {
            return new FourStopFSGateway();
        }
        else
            return null;

    }
}
