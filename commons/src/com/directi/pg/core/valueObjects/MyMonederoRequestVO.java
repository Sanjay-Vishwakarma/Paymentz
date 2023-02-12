package com.directi.pg.core.valueObjects;

import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;

/**
 * Created by IntelliJ IDEA.
 * User: admin1
 * Date: 2/18/13
 * Time: 7:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyMonederoRequestVO extends GenericRequestVO
{
    String ctoken;
    GenericTransDetailsVO genericTransDetailsVO;
    String memberid;
    String wctxnid;
    String url;
    String userId;


    //constructors


    public MyMonederoRequestVO()
    {
    }

    public MyMonederoRequestVO(GenericTransDetailsVO genericTransDetailsVO, String memberid)
    {
        this.genericTransDetailsVO = genericTransDetailsVO;
        this.memberid = memberid;
    }



    public MyMonederoRequestVO(GenericTransDetailsVO genericTransDetailsVO, String memberid,String ctoken)
    {
        this.ctoken = ctoken;
        this.genericTransDetailsVO = genericTransDetailsVO;
        this.memberid = memberid;
    }

    public MyMonederoRequestVO(GenericTransDetailsVO genericTransDetailsVO, String memberid, String ctoken, String wctxnid)
    {
        this.ctoken = ctoken;
        this.genericTransDetailsVO = genericTransDetailsVO;
        this.memberid = memberid;
        this.wctxnid = wctxnid;
    }

    public MyMonederoRequestVO(String ctoken, GenericTransDetailsVO genericTransDetailsVO, String memberid, String wctxnid, String url, String userId)
    {
        this.ctoken = ctoken;
        this.genericTransDetailsVO = genericTransDetailsVO;
        this.memberid = memberid;
        this.wctxnid = wctxnid;
        this.url = url;
        this.userId = userId;
    }

//getter setter


    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getWctxnid()
    {
        return wctxnid;
    }

    public void setWctxnid(String wctxnid)
    {
        this.wctxnid = wctxnid;
    }

    public String getCtoken()
    {
        return ctoken;
    }

    public void setCtoken(String ctoken)
    {
        this.ctoken = ctoken;
    }

    public String getMemberid()
    {
        return memberid;
    }

    public void setMemberid(String memberid)
    {
        this.memberid = memberid;
    }

    public GenericTransDetailsVO getGenericTransDetailsVO()
    {
        return genericTransDetailsVO;
    }

    public void setGenericTransDetailsVO(GenericTransDetailsVO genericTransDetailsVO)
    {
        this.genericTransDetailsVO = genericTransDetailsVO;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    

}
