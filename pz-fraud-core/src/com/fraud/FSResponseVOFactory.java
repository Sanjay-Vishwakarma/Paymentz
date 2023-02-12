package com.fraud;


import com.fraud.at.ATResponseVO;
import com.fraud.fourstop.FourStopFSGateway;
import com.fraud.fourstop.FourStopResponseVO;
import com.fraud.vo.FSGenericResponseVO;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/30/14
 * Time: 7:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class FSResponseVOFactory
{
    public static FSGenericResponseVO getFSResponseVO(String fsGateway)
    {
        if(fsGateway.equals(ATFSGateway.FSNAME))
        {
            return new ATResponseVO();
        }
        else if(fsGateway.equals(FourStopFSGateway.FSNAME))
        {
            return new FourStopResponseVO();
        }
        else
            return null;

    }

}
