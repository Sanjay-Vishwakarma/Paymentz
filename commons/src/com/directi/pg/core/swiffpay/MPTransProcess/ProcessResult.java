/**
 * ProcessResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class ProcessResult  implements java.io.Serializable {
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

    public ProcessResult() {
    }

    public ProcessResult(
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
           int duplicate) {
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
    }


    /**
     * Gets the status value for this ProcessResult.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this ProcessResult.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the result value for this ProcessResult.
     * 
     * @return result
     */
    public java.lang.String getResult() {
        return result;
    }


    /**
     * Sets the result value for this ProcessResult.
     * 
     * @param result
     */
    public void setResult(java.lang.String result) {
        this.result = result;
    }


    /**
     * Gets the historyid value for this ProcessResult.
     * 
     * @return historyid
     */
    public java.lang.String getHistoryid() {
        return historyid;
    }


    /**
     * Sets the historyid value for this ProcessResult.
     * 
     * @param historyid
     */
    public void setHistoryid(java.lang.String historyid) {
        this.historyid = historyid;
    }


    /**
     * Gets the orderid value for this ProcessResult.
     * 
     * @return orderid
     */
    public java.lang.String getOrderid() {
        return orderid;
    }


    /**
     * Sets the orderid value for this ProcessResult.
     * 
     * @param orderid
     */
    public void setOrderid(java.lang.String orderid) {
        this.orderid = orderid;
    }


    /**
     * Gets the refcode value for this ProcessResult.
     * 
     * @return refcode
     */
    public java.lang.String getRefcode() {
        return refcode;
    }


    /**
     * Sets the refcode value for this ProcessResult.
     * 
     * @param refcode
     */
    public void setRefcode(java.lang.String refcode) {
        this.refcode = refcode;
    }


    /**
     * Gets the authcode value for this ProcessResult.
     * 
     * @return authcode
     */
    public java.lang.String getAuthcode() {
        return authcode;
    }


    /**
     * Sets the authcode value for this ProcessResult.
     * 
     * @param authcode
     */
    public void setAuthcode(java.lang.String authcode) {
        this.authcode = authcode;
    }


    /**
     * Gets the total value for this ProcessResult.
     * 
     * @return total
     */
    public float getTotal() {
        return total;
    }


    /**
     * Sets the total value for this ProcessResult.
     * 
     * @param total
     */
    public void setTotal(float total) {
        this.total = total;
    }


    /**
     * Gets the merchantordernumber value for this ProcessResult.
     * 
     * @return merchantordernumber
     */
    public java.lang.String getMerchantordernumber() {
        return merchantordernumber;
    }


    /**
     * Sets the merchantordernumber value for this ProcessResult.
     * 
     * @param merchantordernumber
     */
    public void setMerchantordernumber(java.lang.String merchantordernumber) {
        this.merchantordernumber = merchantordernumber;
    }


    /**
     * Gets the transdate value for this ProcessResult.
     * 
     * @return transdate
     */
    public java.util.Calendar getTransdate() {
        return transdate;
    }


    /**
     * Sets the transdate value for this ProcessResult.
     * 
     * @param transdate
     */
    public void setTransdate(java.util.Calendar transdate) {
        this.transdate = transdate;
    }


    /**
     * Gets the paytype value for this ProcessResult.
     * 
     * @return paytype
     */
    public java.lang.String getPaytype() {
        return paytype;
    }


    /**
     * Sets the paytype value for this ProcessResult.
     * 
     * @param paytype
     */
    public void setPaytype(java.lang.String paytype) {
        this.paytype = paytype;
    }


    /**
     * Gets the duplicate value for this ProcessResult.
     * 
     * @return duplicate
     */
    public int getDuplicate() {
        return duplicate;
    }


    /**
     * Sets the duplicate value for this ProcessResult.
     * 
     * @param duplicate
     */
    public void setDuplicate(int duplicate) {
        this.duplicate = duplicate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProcessResult)) return false;
        ProcessResult other = (ProcessResult) obj;
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
            this.duplicate == other.getDuplicate();
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ProcessResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "ProcessResult"));
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
