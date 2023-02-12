package com.payment.endeavourmpi;

/**
 * Created by SurajT. on 11/13/2017.
 */
public class EnrollmentResponseVO
{
    String result;
    String status;
    String avr;
    String PAReq;
    String acsUrl;
    String trackId;
    String MD;
    String creq;
    String threeDSSessionData;
    String threeDSServerTransID;
    String eci;
    String CAVV;
    String XID;
    String dsTransId;
    String _20BytesBinaryCAVV;
    String _20BytesBinaryXID;
    String threeDVersion;

    byte[] _20BytesBinaryCAVVBytes;
    byte[] _20BytesBinaryXIDBytes;

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getAvr()
    {
        return avr;
    }

    public void setAvr(String avr)
    {
        this.avr = avr;
    }

    public String getPAReq()
    {
        return PAReq;
    }

    public void setPAReq(String PAReq)
    {
        this.PAReq = PAReq;
    }

    public String getAcsUrl()
    {
        return acsUrl;
    }

    public void setAcsUrl(String acsUrl)
    {
        this.acsUrl = acsUrl;
    }

    public String getTrackId()
    {
        return trackId;
    }

    public void setTrackId(String trackId)
    {
        this.trackId = trackId;
    }

    public String getMD()
    {
        return MD;
    }

    public void setMD(String MD)
    {
        this.MD = MD;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getCreq()
    {
        return creq;
    }

    public void setCreq(String creq)
    {
        this.creq = creq;
    }

    public String getThreeDSSessionData()
    {
        return threeDSSessionData;
    }

    public void setThreeDSSessionData(String threeDSSessionData)
    {
        this.threeDSSessionData = threeDSSessionData;
    }

    public String getEci()
    {
        return eci;
    }

    public void setEci(String eci)
    {
        this.eci = eci;
    }

    public String getCAVV()
    {
        return CAVV;
    }

    public void setCAVV(String CAVV)
    {
        this.CAVV = CAVV;
    }

    public String getXID()
    {
        return XID;
    }

    public void setXID(String XID)
    {
        this.XID = XID;
    }

    public String get_20BytesBinaryCAVV()
    {
        return _20BytesBinaryCAVV;
    }

    public void set_20BytesBinaryCAVV(String _20BytesBinaryCAVV)
    {
        this._20BytesBinaryCAVV = _20BytesBinaryCAVV;
    }

    public String get_20BytesBinaryXID()
    {
        return _20BytesBinaryXID;
    }

    public void set_20BytesBinaryXID(String _20BytesBinaryXID)
    {
        this._20BytesBinaryXID = _20BytesBinaryXID;
    }

    public byte[] get_20BytesBinaryCAVVBytes()
    {
        return _20BytesBinaryCAVVBytes;
    }

    public void set_20BytesBinaryCAVVBytes(byte[] _20BytesBinaryCAVVBytes)
    {
        this._20BytesBinaryCAVVBytes = _20BytesBinaryCAVVBytes;
    }

    public byte[] get_20BytesBinaryXIDBytes()
    {
        return _20BytesBinaryXIDBytes;
    }

    public void set_20BytesBinaryXIDBytes(byte[] _20BytesBinaryXIDBytes)
    {
        this._20BytesBinaryXIDBytes = _20BytesBinaryXIDBytes;
    }

    public String getDsTransId()
    {
        return dsTransId;
    }

    public void setDsTransId(String dsTransId)
    {
        this.dsTransId = dsTransId;
    }

    public String getThreeDSServerTransID()
    {

        return threeDSServerTransID;
    }

    public void setThreeDSServerTransID(String threeDSServerTransID)
    {
        this.threeDSServerTransID = threeDSServerTransID;
    }

    public String getThreeDVersion()
    {
        return threeDVersion;
    }

    public void setThreeDVersion(String threeDVersion)
    {
        this.threeDVersion = threeDVersion;
    }
}
