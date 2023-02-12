package com.payment.endeavourmpi;

/**
 * Created by SurajT on 11/13/2017.
 */
public class ParesDecodeResponseVO
{
    public String pares;
    public String signature;
    public String cavv;
    public String purchAmount;
    public String xid;
    public String MessageID;
    public String date;
    public String status;
    public String merID;
    public String exponent;
    public String pan;
    public String cavvAlgorithm;
    public String eci;
    public String trackid;
    public String version;
    public String dsTransId;

    String _20BytesBinaryCAVV;
    String _20BytesBinaryXID;

    byte[] _20BytesBinaryCAVVBytes;
    byte[] _20BytesBinaryXIDBytes;

    public String getPares()
    {
        return pares;
    }

    public void setPares(String pares)
    {
        this.pares = pares;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    public String getCavv()
    {
        return cavv;
    }

    public void setCavv(String cavv)
    {
        this.cavv = cavv;
    }

    public String getPurchAmount()
    {
        return purchAmount;
    }

    public void setPurchAmount(String purchAmount)
    {
        this.purchAmount = purchAmount;
    }

    public String getXid()
    {
        return xid;
    }

    public void setXid(String xid)
    {
        this.xid = xid;
    }

    public String getMessageID()
    {
        return MessageID;
    }

    public void setMessageID(String messageID)
    {
        MessageID = messageID;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getMerID()
    {
        return merID;
    }

    public void setMerID(String merID)
    {
        this.merID = merID;
    }

    public String getExponent()
    {
        return exponent;
    }

    public void setExponent(String exponent)
    {
        this.exponent = exponent;
    }

    public String getPan()
    {
        return pan;
    }

    public void setPan(String pan)
    {
        this.pan = pan;
    }

    public String getCavvAlgorithm()
    {
        return cavvAlgorithm;
    }

    public void setCavvAlgorithm(String cavvAlgorithm)
    {
        this.cavvAlgorithm = cavvAlgorithm;
    }

    public String getEci()
    {
        return eci;
    }

    public void setEci(String eci)
    {
        this.eci = eci;
    }

    public String getTrackid()
    {
        return trackid;
    }

    public void setTrackid(String trackid)
    {
        this.trackid = trackid;
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

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getDsTransId()
    {
        return dsTransId;
    }

    public void setDsTransId(String dsTransId)
    {
        this.dsTransId = dsTransId;
    }
}
