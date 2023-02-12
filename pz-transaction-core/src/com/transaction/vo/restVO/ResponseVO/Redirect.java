package com.transaction.vo.restVO.ResponseVO;

import com.transaction.vo.restVO.RequestVO.Parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sneha on 2/8/2016.
 */
@XmlRootElement(name="redirect")
@XmlAccessorType(XmlAccessType.FIELD)
public class Redirect
{
    @XmlElement (name="url")
    String url;

    @XmlElement(name="method")
    String method;

    @XmlElement(name="target")
    String target;

    @XmlElement (name="parameters")
    List<Parameters> listofParameters = new ArrayList<Parameters>();

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    /*public Parameters getParameters()
    {
        return parameters;
    }

    public void setParameters(Parameters parameters)
    {
        this.parameters = parameters;
    }*/

    public void addListOfParameters(Parameters parameters)
    {
        this.listofParameters.add(parameters);
    }

    public List<Parameters> getListOfAsyncParameterVo()
    {
        return listofParameters;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }
}
