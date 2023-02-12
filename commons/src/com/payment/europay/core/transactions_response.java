package com.payment.europay.core;

import com.payment.common.core.CommResponseVO;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/8/13
 * Time: 1:01 AM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("transactions_response")
public class transactions_response  extends CommResponseVO
{
    @XStreamAlias("version")
    @XStreamAsAttribute
    String version;

    @XStreamAlias("code")
    @XStreamAsAttribute
    String code;

    @XStreamAlias("searchResults")
    SearchResults searchResults;

    @XStreamAlias("processResults")
    ProcessResults processResults;



    public ProcessResults getProcessResults()
    {
        return processResults;
    }

    public void setProcessResults(ProcessResults processResults)
    {
        this.processResults = processResults;
    }

    public SearchResults getSearchResults()
    {
        return searchResults;
    }

    public void setSearchResults(SearchResults searchResults)
    {
        this.searchResults = searchResults;
    }


    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }
}