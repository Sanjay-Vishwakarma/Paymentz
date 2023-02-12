package com.fraud;
import com.fraud.vo.FSGenericRequestVO;
import com.fraud.vo.FSGenericResponseVO;



/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/30/14
 * Time: 7:35 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractFSGateway
{
    public abstract FSGenericResponseVO newTransaction(FSGenericRequestVO requestVO);

    public abstract FSGenericResponseVO updateTransaction(FSGenericRequestVO requestVO);

    public abstract FSGenericResponseVO customerRegistration(FSGenericRequestVO requestVO);

    public abstract FSGenericResponseVO customerUpdatation(FSGenericRequestVO requestVO);

    public abstract FSGenericResponseVO documentIdVerify(FSGenericRequestVO requestVO);
}
