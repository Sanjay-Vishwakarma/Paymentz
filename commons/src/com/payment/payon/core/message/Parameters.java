package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/9/12
 * Time: 11:04 AM
 * To change this template use File | Settings | File Templates.
 */

public class Parameters
{
    @XStreamImplicit
    private List<Parameter> parameters  = new ArrayList<Parameter>();
}
