package com.fraud.at;
import com.fraud.PZFraudProcessor;
import com.fraud.vo.FSGenericRequestVO;
import com.fraud.vo.FSGenericResponseVO;
import com.fraud.vo.PZFraudRequestVO;
import com.fraud.vo.PZFraudResponseVO;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/31/14
 * Time: 7:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATFraudProcessor extends PZFraudProcessor
{
    public void setSpecificReqParameters(PZFraudRequestVO requestVO,FSGenericRequestVO fsGenericRequestVO)
    {
        //System.out.println("setSpecificReqParameters ATFraudProcessor");

    }
    public void setSpecificResParameters(PZFraudResponseVO requestVO,FSGenericResponseVO fsGenericResponseVO)
    {
        //System.out.println("setSpecificResParameters ATFraudProcessor ");

    }
}
