/**
 * SaleResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.twoGatePay.Interface_pkg;

public class SaleResponse  implements java.io.Serializable {
    private java.lang.String purchaseid;

    private int transactionid;

    private java.lang.String transactionstatus;

    private java.lang.String transactionerrorcode;

    private java.lang.String acsurl;

    private java.lang.String md;

    private java.lang.String acsrequestmessage;

    private java.lang.String bankdescriptor;

    public SaleResponse() {
    }

    public SaleResponse(
           java.lang.String purchaseid,
           int transactionid,
           java.lang.String transactionstatus,
           java.lang.String transactionerrorcode,
           java.lang.String acsurl,
           java.lang.String md,
           java.lang.String acsrequestmessage,
           java.lang.String bankdescriptor) {
           this.purchaseid = purchaseid;
           this.transactionid = transactionid;
           this.transactionstatus = transactionstatus;
           this.transactionerrorcode = transactionerrorcode;
           this.acsurl = acsurl;
           this.md = md;
           this.acsrequestmessage = acsrequestmessage;
           this.bankdescriptor = bankdescriptor;
    }


    /**
     * Gets the purchaseid value for this SaleResponse.
     * 
     * @return purchaseid
     */
    public java.lang.String getPurchaseid() {
        return purchaseid;
    }


    /**
     * Sets the purchaseid value for this SaleResponse.
     * 
     * @param purchaseid
     */
    public void setPurchaseid(java.lang.String purchaseid) {
        this.purchaseid = purchaseid;
    }


    /**
     * Gets the transactionid value for this SaleResponse.
     * 
     * @return transactionid
     */
    public int getTransactionid() {
        return transactionid;
    }


    /**
     * Sets the transactionid value for this SaleResponse.
     * 
     * @param transactionid
     */
    public void setTransactionid(int transactionid) {
        this.transactionid = transactionid;
    }


    /**
     * Gets the transactionstatus value for this SaleResponse.
     * 
     * @return transactionstatus
     */
    public java.lang.String getTransactionstatus() {
        return transactionstatus;
    }


    /**
     * Sets the transactionstatus value for this SaleResponse.
     * 
     * @param transactionstatus
     */
    public void setTransactionstatus(java.lang.String transactionstatus) {
        this.transactionstatus = transactionstatus;
    }


    /**
     * Gets the transactionerrorcode value for this SaleResponse.
     * 
     * @return transactionerrorcode
     */
    public java.lang.String getTransactionerrorcode() {
        return transactionerrorcode;
    }


    /**
     * Sets the transactionerrorcode value for this SaleResponse.
     * 
     * @param transactionerrorcode
     */
    public void setTransactionerrorcode(java.lang.String transactionerrorcode) {
        this.transactionerrorcode = transactionerrorcode;
    }


    /**
     * Gets the acsurl value for this SaleResponse.
     * 
     * @return acsurl
     */
    public java.lang.String getAcsurl() {
        return acsurl;
    }


    /**
     * Sets the acsurl value for this SaleResponse.
     * 
     * @param acsurl
     */
    public void setAcsurl(java.lang.String acsurl) {
        this.acsurl = acsurl;
    }


    /**
     * Gets the md value for this SaleResponse.
     * 
     * @return md
     */
    public java.lang.String getMd() {
        return md;
    }


    /**
     * Sets the md value for this SaleResponse.
     * 
     * @param md
     */
    public void setMd(java.lang.String md) {
        this.md = md;
    }


    /**
     * Gets the acsrequestmessage value for this SaleResponse.
     * 
     * @return acsrequestmessage
     */
    public java.lang.String getAcsrequestmessage() {
        return acsrequestmessage;
    }


    /**
     * Sets the acsrequestmessage value for this SaleResponse.
     * 
     * @param acsrequestmessage
     */
    public void setAcsrequestmessage(java.lang.String acsrequestmessage) {
        this.acsrequestmessage = acsrequestmessage;
    }


    /**
     * Gets the bankdescriptor value for this SaleResponse.
     * 
     * @return bankdescriptor
     */
    public java.lang.String getBankdescriptor() {
        return bankdescriptor;
    }


    /**
     * Sets the bankdescriptor value for this SaleResponse.
     * 
     * @param bankdescriptor
     */
    public void setBankdescriptor(java.lang.String bankdescriptor) {
        this.bankdescriptor = bankdescriptor;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SaleResponse)) return false;
        SaleResponse other = (SaleResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.purchaseid==null && other.getPurchaseid()==null) || 
             (this.purchaseid!=null &&
              this.purchaseid.equals(other.getPurchaseid()))) &&
            this.transactionid == other.getTransactionid() &&
            ((this.transactionstatus==null && other.getTransactionstatus()==null) || 
             (this.transactionstatus!=null &&
              this.transactionstatus.equals(other.getTransactionstatus()))) &&
            ((this.transactionerrorcode==null && other.getTransactionerrorcode()==null) || 
             (this.transactionerrorcode!=null &&
              this.transactionerrorcode.equals(other.getTransactionerrorcode()))) &&
            ((this.acsurl==null && other.getAcsurl()==null) || 
             (this.acsurl!=null &&
              this.acsurl.equals(other.getAcsurl()))) &&
            ((this.md==null && other.getMd()==null) || 
             (this.md!=null &&
              this.md.equals(other.getMd()))) &&
            ((this.acsrequestmessage==null && other.getAcsrequestmessage()==null) || 
             (this.acsrequestmessage!=null &&
              this.acsrequestmessage.equals(other.getAcsrequestmessage()))) &&
            ((this.bankdescriptor==null && other.getBankdescriptor()==null) || 
             (this.bankdescriptor!=null &&
              this.bankdescriptor.equals(other.getBankdescriptor())));
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
        if (getPurchaseid() != null) {
            _hashCode += getPurchaseid().hashCode();
        }
        _hashCode += getTransactionid();
        if (getTransactionstatus() != null) {
            _hashCode += getTransactionstatus().hashCode();
        }
        if (getTransactionerrorcode() != null) {
            _hashCode += getTransactionerrorcode().hashCode();
        }
        if (getAcsurl() != null) {
            _hashCode += getAcsurl().hashCode();
        }
        if (getMd() != null) {
            _hashCode += getMd().hashCode();
        }
        if (getAcsrequestmessage() != null) {
            _hashCode += getAcsrequestmessage().hashCode();
        }
        if (getBankdescriptor() != null) {
            _hashCode += getBankdescriptor().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SaleResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:Interface", "SaleResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchaseid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "purchaseid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionstatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionstatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionerrorcode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "transactionerrorcode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acsurl");
        elemField.setXmlName(new javax.xml.namespace.QName("", "acsurl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("md");
        elemField.setXmlName(new javax.xml.namespace.QName("", "md"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acsrequestmessage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "acsrequestmessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankdescriptor");
        elemField.setXmlName(new javax.xml.namespace.QName("", "bankdescriptor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
