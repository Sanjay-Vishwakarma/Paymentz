package com.fraud;


import com.fraud.at.ATRequestVO;
import com.fraud.fourstop.FourStopFSGateway;
import com.fraud.fourstop.FourStopRequestVO;
import com.fraud.vo.FSGenericRequestVO;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/30/14
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class FSRequestVOFactory
{
    public static FSGenericRequestVO getFSRequestVO(String fsGateway)
    {
        if(fsGateway.equals(ATFSGateway.FSNAME))
        {
            return new ATRequestVO();
        }
        else if(fsGateway.equals(FourStopFSGateway.FSNAME))
        {
            return new FourStopRequestVO();
        }
        else
            return null;

    }
}
