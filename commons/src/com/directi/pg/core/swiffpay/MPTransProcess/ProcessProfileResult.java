/**
 * ProcessProfileResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class ProcessProfileResult  implements java.io.Serializable {
    private java.lang.String status;

    private java.lang.String result;

    private java.lang.String historyid;

    private java.lang.String orderid;

    private java.lang.String refcode;

    private java.lang.String authcode;

    private float total;

    private java.lang.String merchantordernumber;

    private java.util.Calendar transdate;

    private java.lang.String paytype;

    private int duplicate;

    private java.lang.String userprofileid;

    private java.lang.String last4Digits;

    private java.lang.String ccnum_decrypt;

    private java.lang.String expdate_decrypt;

    private java.lang.String billaddr1;

    private java.lang.String billaddr2;

    private java.lang.String billcity;

    private java.lang.String billstate;

    private java.lang.String billzip;

    public ProcessProfileResult() {
    }

    public ProcessProfileResult(
           java.lang.String status,
           java.lang.String result,
           java.lang.String historyid,
           java.lang.String orderid,
           java.lang.String refcode,
           java.lang.String authcode,
           float total,
           java.lang.String merchantordernumber,
           java.util.Calendar transdate,
           java.lang.String paytype,
           int duplicate,
           java.lang.String userprofileid,
           java.lang.String last4Digits,
           java.lang.String ccnum_decrypt,
           java.lang.String expdate_decrypt,
           java.lang.String billaddr1,
           java.lang.String billaddr2,
           java.lang.String billcity,
           java.lang.String billstate,
           java.lang.String billzip) {
           this.status = status;
           this.result = result;
           this.historyid = historyid;
           this.orderid = orderid;
           this.refcode = refcode;
           this.authcode = authcode;
           this.total = total;
           this.merchantordernumber = merchantordernumber;
           this.transdate = transdate;
           this.paytype = paytype;
           this.duplicate = duplicate;
           this.userprofileid = userprofileid;
           this.last4Digits = last4Digits;
           this.ccnum_decrypt = ccnum_decrypt;
           this.expdate_decrypt = expdate_decrypt;
           this.billaddr1 = billaddr1;
           this.billaddr2 = billaddr2;
           this.billcity = billcity;
           this.billstate = billstate;
           this.billzip = billzip;
    }


    /**
     * Gets the status value for this ProcessProfileResult.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this ProcessProfileResult.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the result value for this ProcessProfileResult.
     * 
     * @return result
     */
    public java.lang.String getResult() {
        return result;
    }


    /**
     * Sets the result value for this ProcessProfileResult.
     * 
     * @param result
     */
    public void setResult(java.lang.String result) {
        this.result = result;
    }


    /**
     * Gets the historyid value for this ProcessProfileResult.
     * 
     * @return historyid
     */
    public java.lang.String getHistoryid() {
        return historyid;
    }


    /**
     * Sets the historyid value for this ProcessProfileResult.
     * 
     * @param historyid
     */
    public void setHistoryid(java.lang.String historyid) {
        this.historyid = historyid;
    }


    /**
     * Gets the orderid value for this ProcessProfileResult.
     * 
     * @return orderid
     */
    public java.lang.String getOrderid() {
        return orderid;
    }


    /**
     * Sets the orderid value for this ProcessProfileResult.
     * 
     * @param orderid
     */
    public void setOrderid(java.lang.String orderid) {
        this.orderid = orderid;
    }


    /**
     * Gets the refcode value for this ProcessProfileResult.
     * 
     * @return refcode
     */
    public java.lang.String getRefcode() {
        return refcode;
    }


    /**
     * Sets the refcode value for this ProcessProfileResult.
     * 
     * @param refcode
     */
    public void setRefcode(java.lang.String refcode) {
        this.refcode = refcode;
    }


    /**
     * Gets the authcode value for this ProcessProfileResult.
     * 
     * @return authcode
     */
    public java.lang.String getAuthcode() {
        return authcode;
    }


    /**
     * Sets the authcode value for this ProcessProfileResult.
     * 
     * @param authcode
     */
    public void setAuthcode(java.lang.String authcode) {
        this.authcode = authcode;
    }


    /**
     * Gets the total value for this ProcessProfileResult.
     * 
     * @return total
     */
    public float getTotal() {
        return total;
    }


    /**
     * Sets the total value for this ProcessProfileResult.
     * 
     * @param total
     */
    public void setTotal(float total) {
        this.total = total;
    }


    /**
     * Gets the merchantordernumber value for this ProcessProfileResult.
     * 
     * @return merchantordernumber
     */
    public java.lang.String getMerchantordernumber() {
        return merchantordernumber;
    }


    /**
     * Sets the merchantordernumber value for this ProcessProfileResult.
     * 
     * @param merchantordernumber
     */
    public void setMerchantordernumber(java.lang.String merchantordernumber) {
        this.merchantordernumber = merchantordernumber;
    }


    /**
     * Gets the transdate value for this ProcessProfileResult.
     * 
     * @return transdate
     */
    public java.util.Calendar getTransdate() {
        return transdate;
    }


    /**
     * Sets the transdate value for this ProcessProfileResult.
     * 
     * @param transdate
     */
    public void setTransdate(java.util.Calendar transdate) {
        this.transdate = transdate;
    }


    /**
     * Gets the paytype value for this ProcessProfileResult.
     * 
     * @return paytype
     */
    public java.lang.String getPaytype() {
        return paytype;
    }


    /**
     * Sets the paytype value for this ProcessProfileResult.
     * 
     * @param paytype
     */
    public void setPaytype(java.lang.String paytype) {
        this.paytype = paytype;
    }


    /**
     * Gets the duplicate value for this ProcessProfileResult.
     * 
     * @return duplicate
     */
    public int getDuplicate() {
        return duplicate;
    }


    /**
     * Sets the duplicate value for this ProcessProfileResult.
     * 
     * @param duplicate
     */
    public void setDuplicate(int duplicate) {
        this.duplicate = duplicate;
    }


    /**
     * Gets the userprofileid value for this ProcessProfileResult.
     * 
     * @return userprofileid
     */
    public java.lang.String getUserprofileid() {
        return userprofileid;
    }


    /**
     * Sets the userprofileid value for this ProcessProfileResult.
     * 
     * @param userprofileid
     */
    public void setUserprofileid(java.lang.String userprofileid) {
        this.userprofileid = userprofileid;
    }


    /**
     * Gets the last4Digits value for this ProcessProfileResult.
     * 
     * @return last4Digits
     */
    public java.lang.String getLast4Digits() {
        return last4Digits;
    }


    /**
     * Sets the last4Digits value for this ProcessProfileResult.
     * 
     * @param last4Digits
     */
    public void setLast4Digits(java.lang.String last4Digits) {
        this.last4Digits = last4Digits;
    }


    /**
     * Gets the ccnum_decrypt value for this ProcessProfileResult.
     * 
     * @return ccnum_decrypt
     */
    public java.lang.String getCcnum_decrypt() {
        return ccnum_decrypt;
    }


    /**
     * Sets the ccnum_decrypt value for this ProcessProfileResult.
     * 
     * @param ccnum_decrypt
     */
    public void setCcnum_decrypt(java.lang.String ccnum_decrypt) {
        this.ccnum_decrypt = ccnum_decrypt;
    }


    /**
     * Gets the expdate_decrypt value for this ProcessProfileResult.
     * 
     * @return expdate_decrypt
     */
    public java.lang.String getExpdate_decrypt() {
        return expdate_decrypt;
    }


    /**
     * Sets the expdate_decrypt value for this ProcessProfileResult.
     * 
     * @param expdate_decrypt
     */
    public void setExpdate_decrypt(java.lang.String expdate_decrypt) {
        this.expdate_decrypt = expdate_decrypt;
    }


    /**
     * Gets the billaddr1 value for this ProcessProfileResult.
     * 
     * @return billaddr1
     */
    public java.lang.String getBilladdr1() {
        return billaddr1;
    }


    /**
     * Sets the billaddr1 value for this ProcessProfileResult.
     * 
     * @param billaddr1
     */
    public void setBilladdr1(java.lang.String billaddr1) {
        this.billaddr1 = billaddr1;
    }


    /**
     * Gets the billaddr2 value for this ProcessProfileResult.
     * 
     * @return billaddr2
     */
    public java.lang.String getBilladdr2() {
        return billaddr2;
    }


    /**
     * Sets the billaddr2 value for this ProcessProfileResult.
     * 
     * @param billaddr2
     */
    public void setBilladdr2(java.lang.String billaddr2) {
        this.billaddr2 = billaddr2;
    }


    /**
     * Gets the billcity value for this ProcessProfileResult.
     * 
     * @return billcity
     */
    public java.lang.String getBillcity() {
        return billcity;
    }


    /**
     * Sets the billcity value for this ProcessProfileResult.
     * 
     * @param billcity
     */
    public void setBillcity(java.lang.String billcity) {
        this.billcity = billcity;
    }


    /**
     * Gets the billstate value for this ProcessProfileResult.
     * 
     * @return billstate
     */
    public java.lang.String getBillstate() {
        return billstate;
    }


    /**
     * Sets the billstate value for this ProcessProfileResult.
     * 
     * @param billstate
     */
    public void setBillstate(java.lang.String billstate) {
        this.billstate = billstate;
    }


    /**
     * Gets the billzip value for this ProcessProfileResult.
     * 
     * @return billzip
     */
    public java.lang.String getBillzip() {
        return billzip;
    }


    /**
     * Sets the billzip value for this ProcessProfileResult.
     * 
     * @param billzip
     */
    public void setBillzip(java.lang.String billzip) {
        this.billzip = billzip;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProcessProfileResult)) return false;
        ProcessProfileResult other = (ProcessProfileResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
            ((this.historyid==null && other.getHistoryid()==null) || 
             (this.historyid!=null &&
              this.historyid.equals(other.getHistoryid()))) &&
            ((this.orderid==null && other.getOrderid()==null) || 
             (this.orderid!=null &&
              this.orderid.equals(other.getOrderid()))) &&
            ((this.refcode==null && other.getRefcode()==null) || 
             (this.refcode!=null &&
              this.refcode.equals(other.getRefcode()))) &&
            ((this.authcode==null && other.getAuthcode()==null) || 
             (this.authcode!=null &&
              this.authcode.equals(other.getAuthcode()))) &&
            this.total == other.getTotal() &&
            ((this.merchantordernumber==null && other.getMerchantordernumber()==null) || 
             (this.merchantordernumber!=null &&
              this.merchantordernumber.equals(other.getMerchantordernumber()))) &&
            ((this.transdate==null && other.getTransdate()==null) || 
             (this.transdate!=null &&
              this.transdate.equals(other.getTransdate()))) &&
            ((this.paytype==null && other.getPaytype()==null) || 
             (this.paytype!=null &&
              this.paytype.equals(other.getPaytype()))) &&
            this.duplicate == other.getDuplicate() &&
            ((this.userprofileid==null && other.getUserprofileid()==null) || 
             (this.userprofileid!=null &&
              this.userprofileid.equals(other.getUserprofileid()))) &&
            ((this.last4Digits==null && other.getLast4Digits()==null) || 
             (this.last4Digits!=null &&
              this.last4Digits.equals(other.getLast4Digits()))) &&
            ((this.ccnum_decrypt==null && other.getCcnum_decrypt()==null) || 
             (this.ccnum_decrypt!=null &&
              this.ccnum_decrypt.equals(other.getCcnum_decrypt()))) &&
            ((this.expdate_decrypt==null && other.getExpdate_decrypt()==null) || 
             (this.expdate_decrypt!=null &&
              this.expdate_decrypt.equals(other.getExpdate_decrypt()))) &&
            ((this.billaddr1==null && other.getBilladdr1()==null) || 
             (this.billaddr1!=null &&
              this.billaddr1.equals(other.getBilladdr1()))) &&
            ((this.billaddr2==null && other.getBilladdr2()==null) || 
             (this.billaddr2!=null &&
              this.billaddr2.equals(other.getBilladdr2()))) &&
            ((this.billcity==null && other.getBillcity()==null) || 
             (this.billcity!=null &&
              this.billcity.equals(other.getBillcity()))) &&
            ((this.billstate==null && other.getBillstate()==null) || 
             (this.billstate!=null &&
              this.billstate.equals(other.getBillstate()))) &&
            ((this.billzip==null && other.getBillzip()==null) || 
             (this.billzip!=null &&
              this.billzip.equals(other.getBillzip())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        if (getHistoryid() != null) {
            _hashCode += getHistoryid().hashCode();
        }
        if (getOrderid() != null) {
            _hashCode += getOrderid().hashCode();
        }
        if (getRefcode() != null) {
            _hashCode += getRefcode().hashCode();
        }
        if (getAuthcode() != null) {
            _hashCode += getAuthcode().hashCode();
        }
        _hashCode += new Float(getTotal()).hashCode();
        if (getMerchantordernumber() != null) {
            _hashCode += getMerchantordernumber().hashCode();
        }
        if (getTransdate() != null) {
            _hashCode += getTransdate().hashCode();
        }
        if (getPaytype() != null) {
            _hashCode += getPaytype().hashCode();
        }
        _hashCode += getDuplicate();
        if (getUserprofileid() != null) {
            _hashCode += getUserprofileid().hashCode();
        }
        if (getLast4Digits() != null) {
            _hashCode += getLast4Digits().hashCode();
        }
        if (getCcnum_decrypt() != null) {
            _hashCode += getCcnum_decrypt().hashCode();
        }
        if (getExpdate_decrypt() != null) {
            _hashCode += getExpdate_decrypt().hashCode();
        }
        if (getBilladdr1() != null) {
            _hashCode += getBilladdr1().hashCode();
        }
        if (getBilladdr2() != null) {
            _hashCode += getBilladdr2().hashCode();
        }
        if (getBillcity() != null) {
            _hashCode += getBillcity().hashCode();
        }
        if (getBillstate() != null) {
            _hashCode += getBillstate().hashCode();
        }
        if (getBillzip() != null) {
            _hashCode += getBillzip().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ProcessProfileResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessProfileResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("", "result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("historyid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "historyid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "orderid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("refcode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "refcode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authcode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "authcode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("total");
        elemField.setXmlName(new javax.xml.namespace.QName("", "total"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantordernumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "merchantordernumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transdate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transdate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paytype");
        elemField.setXmlName(new javax.xml.namespace.QName("", "paytype"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("duplicate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "duplicate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userprofileid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userprofileid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("last4Digits");
        elemField.setXmlName(new javax.xml.namespace.QName("", "last4digits"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ccnum_decrypt");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ccnum_decrypt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expdate_decrypt");
        elemField.setXmlName(new javax.xml.namespace.QName("", "expdate_decrypt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billaddr1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "billaddr1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billaddr2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "billaddr2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billcity");
        elemField.setXmlName(new javax.xml.namespace.QName("", "billcity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billstate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "billstate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billzip");
        elemField.setXmlName(new javax.xml.namespace.QName("", "billzip"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
