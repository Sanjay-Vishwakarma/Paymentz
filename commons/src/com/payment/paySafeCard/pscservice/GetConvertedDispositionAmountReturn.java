/**
 * GetConvertedDispositionAmountReturn.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.paySafeCard.pscservice;

public class GetConvertedDispositionAmountReturn  implements java.io.Serializable {
    private int resultCode;

    private int errorCode;

    private java.lang.String mtid;

    private java.lang.String[] subId;

    private double cardAmount;

    private java.lang.String cardCurrency;

    private java.lang.String dispositionCurrency;

    private double convertedDispositionAmount;

    public GetConvertedDispositionAmountReturn() {
    }

    public GetConvertedDispositionAmountReturn(
           int resultCode,
           int errorCode,
           java.lang.String mtid,
           java.lang.String[] subId,
           double cardAmount,
           java.lang.String cardCurrency,
           java.lang.String dispositionCurrency,
           double convertedDispositionAmount) {
           this.resultCode = resultCode;
           this.errorCode = errorCode;
           this.mtid = mtid;
           this.subId = subId;
           this.cardAmount = cardAmount;
           this.cardCurrency = cardCurrency;
           this.dispositionCurrency = dispositionCurrency;
           this.convertedDispositionAmount = convertedDispositionAmount;
    }


    /**
     * Gets the resultCode value for this GetConvertedDispositionAmountReturn.
     * 
     * @return resultCode
     */
    public int getResultCode() {
        return resultCode;
    }


    /**
     * Sets the resultCode value for this GetConvertedDispositionAmountReturn.
     * 
     * @param resultCode
     */
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }


    /**
     * Gets the errorCode value for this GetConvertedDispositionAmountReturn.
     * 
     * @return errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this GetConvertedDispositionAmountReturn.
     * 
     * @param errorCode
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Gets the mtid value for this GetConvertedDispositionAmountReturn.
     * 
     * @return mtid
     */
    public java.lang.String getMtid() {
        return mtid;
    }


    /**
     * Sets the mtid value for this GetConvertedDispositionAmountReturn.
     * 
     * @param mtid
     */
    public void setMtid(java.lang.String mtid) {
        this.mtid = mtid;
    }


    /**
     * Gets the subId value for this GetConvertedDispositionAmountReturn.
     * 
     * @return subId
     */
    public java.lang.String[] getSubId() {
        return subId;
    }


    /**
     * Sets the subId value for this GetConvertedDispositionAmountReturn.
     * 
     * @param subId
     */
    public void setSubId(java.lang.String[] subId) {
        this.subId = subId;
    }

    public java.lang.String getSubId(int i) {
        return this.subId[i];
    }

    public void setSubId(int i, java.lang.String _value) {
        this.subId[i] = _value;
    }


    /**
     * Gets the cardAmount value for this GetConvertedDispositionAmountReturn.
     * 
     * @return cardAmount
     */
    public double getCardAmount() {
        return cardAmount;
    }


    /**
     * Sets the cardAmount value for this GetConvertedDispositionAmountReturn.
     * 
     * @param cardAmount
     */
    public void setCardAmount(double cardAmount) {
        this.cardAmount = cardAmount;
    }


    /**
     * Gets the cardCurrency value for this GetConvertedDispositionAmountReturn.
     * 
     * @return cardCurrency
     */
    public java.lang.String getCardCurrency() {
        return cardCurrency;
    }


    /**
     * Sets the cardCurrency value for this GetConvertedDispositionAmountReturn.
     * 
     * @param cardCurrency
     */
    public void setCardCurrency(java.lang.String cardCurrency) {
        this.cardCurrency = cardCurrency;
    }


    /**
     * Gets the dispositionCurrency value for this GetConvertedDispositionAmountReturn.
     * 
     * @return dispositionCurrency
     */
    public java.lang.String getDispositionCurrency() {
        return dispositionCurrency;
    }


    /**
     * Sets the dispositionCurrency value for this GetConvertedDispositionAmountReturn.
     * 
     * @param dispositionCurrency
     */
    public void setDispositionCurrency(java.lang.String dispositionCurrency) {
        this.dispositionCurrency = dispositionCurrency;
    }


    /**
     * Gets the convertedDispositionAmount value for this GetConvertedDispositionAmountReturn.
     * 
     * @return convertedDispositionAmount
     */
    public double getConvertedDispositionAmount() {
        return convertedDispositionAmount;
    }


    /**
     * Sets the convertedDispositionAmount value for this GetConvertedDispositionAmountReturn.
     * 
     * @param convertedDispositionAmount
     */
    public void setConvertedDispositionAmount(double convertedDispositionAmount) {
        this.convertedDispositionAmount = convertedDispositionAmount;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetConvertedDispositionAmountReturn)) return false;
        GetConvertedDispositionAmountReturn other = (GetConvertedDispositionAmountReturn) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.resultCode == other.getResultCode() &&
            this.errorCode == other.getErrorCode() &&
            ((this.mtid==null && other.getMtid()==null) || 
             (this.mtid!=null &&
              this.mtid.equals(other.getMtid()))) &&
            ((this.subId==null && other.getSubId()==null) || 
             (this.subId!=null &&
              java.util.Arrays.equals(this.subId, other.getSubId()))) &&
            this.cardAmount == other.getCardAmount() &&
            ((this.cardCurrency==null && other.getCardCurrency()==null) || 
             (this.cardCurrency!=null &&
              this.cardCurrency.equals(other.getCardCurrency()))) &&
            ((this.dispositionCurrency==null && other.getDispositionCurrency()==null) || 
             (this.dispositionCurrency!=null &&
              this.dispositionCurrency.equals(other.getDispositionCurrency()))) &&
            this.convertedDispositionAmount == other.getConvertedDispositionAmount();
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
        _hashCode += getResultCode();
        _hashCode += getErrorCode();
        if (getMtid() != null) {
            _hashCode += getMtid().hashCode();
        }
        if (getSubId() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSubId());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSubId(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += new Double(getCardAmount()).hashCode();
        if (getCardCurrency() != null) {
            _hashCode += getCardCurrency().hashCode();
        }
        if (getDispositionCurrency() != null) {
            _hashCode += getDispositionCurrency().hashCode();
        }
        _hashCode += new Double(getConvertedDispositionAmount()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetConvertedDispositionAmountReturn.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:pscservice", "GetConvertedDispositionAmountReturn"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "resultCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "errorCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mtid");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "mtid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "subId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "cardAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardCurrency");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "cardCurrency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dispositionCurrency");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "dispositionCurrency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("convertedDispositionAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "convertedDispositionAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
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
