package com.fraud.fourstop;

import com.fraud.PZFraudProcessor;
import com.fraud.vo.FSGenericRequestVO;
import com.fraud.vo.FSGenericResponseVO;
import com.fraud.vo.PZFraudRequestVO;
import com.fraud.vo.PZFraudResponseVO;

/**
 * Created with IntelliJ IDEA.
 * User: Supriya
 * Date: 11/10/16
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class FourStopFraudProcessor extends PZFraudProcessor
{
    public void setSpecificReqParameters(PZFraudRequestVO requestVO,FSGenericRequestVO fsGenericRequestVO)
    {
        //System.out.println("setSpecificReqParameters FourStopFraudProcessor");

    }
    public void setSpecificResParameters(PZFraudResponseVO requestVO,FSGenericResponseVO fsGenericResponseVO)
    {
        //System.out.println("setSpecificResParameters FourStopFraudProcessor ");

    }
}
