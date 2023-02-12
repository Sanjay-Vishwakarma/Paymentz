/**
 * PayoutState.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.paySafeCard.pscservice;

public class PayoutState  implements java.io.Serializable {
    private java.lang.String mid;

    private java.lang.String currency;

    private double totalPayoutAmount;

    private double totalPaymentAmount;

    private double creditLine;

    private double totalPayoutBalance;

    private double dailyPayoutLimit;

    private double dailyPayoutAmount;

    private double dailyPayoutBalance;

    public PayoutState() {
    }

    public PayoutState(
           java.lang.String mid,
           java.lang.String currency,
           double totalPayoutAmount,
           double totalPaymentAmount,
           double creditLine,
           double totalPayoutBalance,
           double dailyPayoutLimit,
           double dailyPayoutAmount,
           double dailyPayoutBalance) {
           this.mid = mid;
           this.currency = currency;
           this.totalPayoutAmount = totalPayoutAmount;
           this.totalPaymentAmount = totalPaymentAmount;
           this.creditLine = creditLine;
           this.totalPayoutBalance = totalPayoutBalance;
           this.dailyPayoutLimit = dailyPayoutLimit;
           this.dailyPayoutAmount = dailyPayoutAmount;
           this.dailyPayoutBalance = dailyPayoutBalance;
    }


    /**
     * Gets the mid value for this PayoutState.
     * 
     * @return mid
     */
    public java.lang.String getMid() {
        return mid;
    }


    /**
     * Sets the mid value for this PayoutState.
     * 
     * @param mid
     */
    public void setMid(java.lang.String mid) {
        this.mid = mid;
    }


    /**
     * Gets the currency value for this PayoutState.
     * 
     * @return currency
     */
    public java.lang.String getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this PayoutState.
     * 
     * @param currency
     */
    public void setCurrency(java.lang.String currency) {
        this.currency = currency;
    }


    /**
     * Gets the totalPayoutAmount value for this PayoutState.
     * 
     * @return totalPayoutAmount
     */
    public double getTotalPayoutAmount() {
        return totalPayoutAmount;
    }


    /**
     * Sets the totalPayoutAmount value for this PayoutState.
     * 
     * @param totalPayoutAmount
     */
    public void setTotalPayoutAmount(double totalPayoutAmount) {
        this.totalPayoutAmount = totalPayoutAmount;
    }


    /**
     * Gets the totalPaymentAmount value for this PayoutState.
     * 
     * @return totalPaymentAmount
     */
    public double getTotalPaymentAmount() {
        return totalPaymentAmount;
    }


    /**
     * Sets the totalPaymentAmount value for this PayoutState.
     * 
     * @param totalPaymentAmount
     */
    public void setTotalPaymentAmount(double totalPaymentAmount) {
        this.totalPaymentAmount = totalPaymentAmount;
    }


    /**
     * Gets the creditLine value for this PayoutState.
     * 
     * @return creditLine
     */
    public double getCreditLine() {
        return creditLine;
    }


    /**
     * Sets the creditLine value for this PayoutState.
     * 
     * @param creditLine
     */
    public void setCreditLine(double creditLine) {
        this.creditLine = creditLine;
    }


    /**
     * Gets the totalPayoutBalance value for this PayoutState.
     * 
     * @return totalPayoutBalance
     */
    public double getTotalPayoutBalance() {
        return totalPayoutBalance;
    }


    /**
     * Sets the totalPayoutBalance value for this PayoutState.
     * 
     * @param totalPayoutBalance
     */
    public void setTotalPayoutBalance(double totalPayoutBalance) {
        this.totalPayoutBalance = totalPayoutBalance;
    }


    /**
     * Gets the dailyPayoutLimit value for this PayoutState.
     * 
     * @return dailyPayoutLimit
     */
    public double getDailyPayoutLimit() {
        return dailyPayoutLimit;
    }


    /**
     * Sets the dailyPayoutLimit value for this PayoutState.
     * 
     * @param dailyPayoutLimit
     */
    public void setDailyPayoutLimit(double dailyPayoutLimit) {
        this.dailyPayoutLimit = dailyPayoutLimit;
    }


    /**
     * Gets the dailyPayoutAmount value for this PayoutState.
     * 
     * @return dailyPayoutAmount
     */
    public double getDailyPayoutAmount() {
        return dailyPayoutAmount;
    }


    /**
     * Sets the dailyPayoutAmount value for this PayoutState.
     * 
     * @param dailyPayoutAmount
     */
    public void setDailyPayoutAmount(double dailyPayoutAmount) {
        this.dailyPayoutAmount = dailyPayoutAmount;
    }


    /**
     * Gets the dailyPayoutBalance value for this PayoutState.
     * 
     * @return dailyPayoutBalance
     */
    public double getDailyPayoutBalance() {
        return dailyPayoutBalance;
    }


    /**
     * Sets the dailyPayoutBalance value for this PayoutState.
     * 
     * @param dailyPayoutBalance
     */
    public void setDailyPayoutBalance(double dailyPayoutBalance) {
        this.dailyPayoutBalance = dailyPayoutBalance;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayoutState)) return false;
        PayoutState other = (PayoutState) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.mid==null && other.getMid()==null) || 
             (this.mid!=null &&
              this.mid.equals(other.getMid()))) &&
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            this.totalPayoutAmount == other.getTotalPayoutAmount() &&
            this.totalPaymentAmount == other.getTotalPaymentAmount() &&
            this.creditLine == other.getCreditLine() &&
            this.totalPayoutBalance == other.getTotalPayoutBalance() &&
            this.dailyPayoutLimit == other.getDailyPayoutLimit() &&
            this.dailyPayoutAmount == other.getDailyPayoutAmount() &&
            this.dailyPayoutBalance == other.getDailyPayoutBalance();
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
        if (getMid() != null) {
            _hashCode += getMid().hashCode();
        }
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        _hashCode += new Double(getTotalPayoutAmount()).hashCode();
        _hashCode += new Double(getTotalPaymentAmount()).hashCode();
        _hashCode += new Double(getCreditLine()).hashCode();
        _hashCode += new Double(getTotalPayoutBalance()).hashCode();
        _hashCode += new Double(getDailyPayoutLimit()).hashCode();
        _hashCode += new Double(getDailyPayoutAmount()).hashCode();
        _hashCode += new Double(getDailyPayoutBalance()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayoutState.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:pscservice", "payoutState"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mid");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "mid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currency");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "currency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalPayoutAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "totalPayoutAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalPaymentAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "totalPaymentAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creditLine");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "creditLine"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalPayoutBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "totalPayoutBalance"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dailyPayoutLimit");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "dailyPayoutLimit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dailyPayoutAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "dailyPayoutAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dailyPayoutBalance");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:pscservice", "dailyPayoutBalance"));
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
