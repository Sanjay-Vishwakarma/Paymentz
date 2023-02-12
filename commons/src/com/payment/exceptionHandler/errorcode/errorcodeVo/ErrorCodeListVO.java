package com.payment.exceptionHandler.errorcode.errorcodeVo;

import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 4/14/15
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class ErrorCodeListVO extends ErrorCodeVO
{
    private List<ErrorCodeVO> listOfError = new ArrayList<ErrorCodeVO>();

    public void addListOfError(ErrorCodeVO errorCodesVO)
    {
        this.listOfError.add(errorCodesVO);
    }

    public List<ErrorCodeVO> getListOfError()
    {
        return listOfError;
    }
}
