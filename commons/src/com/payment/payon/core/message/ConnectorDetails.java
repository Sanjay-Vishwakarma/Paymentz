package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/17/12
 * Time: 10:39 PM
 * To change this template use File | Settings | File Templates.
 */

public class ConnectorDetails
{
    public List<Result> getResult()
    {
        return result;
    }

    public void setResult(List<Result> result)
    {
        this.result = result;
    }

    @XStreamImplicit
    private List<Result> result=new ArrayList<Result>();
}
