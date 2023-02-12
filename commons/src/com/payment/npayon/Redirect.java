package com.payment.npayon;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by admin on 6/23/2018.
 */
public class Redirect
{
    String url;
    List<Parameter> parameters;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public List<Parameter> getParameters()
    {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters)
    {
        this.parameters = parameters;
    }
}
