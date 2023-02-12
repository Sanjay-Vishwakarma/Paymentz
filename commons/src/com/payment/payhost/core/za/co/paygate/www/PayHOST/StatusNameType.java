/**
 * StatusNameType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class StatusNameType implements java.io.Serializable {
    private org.apache.axis.types.Token _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected StatusNameType(org.apache.axis.types.Token value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final org.apache.axis.types.Token _Error = new org.apache.axis.types.Token("Error");
    public static final org.apache.axis.types.Token _Pending = new org.apache.axis.types.Token("Pending");
    public static final org.apache.axis.types.Token _Cancelled = new org.apache.axis.types.Token("Cancelled");
    public static final org.apache.axis.types.Token _Completed = new org.apache.axis.types.Token("Completed");
    public static final org.apache.axis.types.Token _RiskRejected = new org.apache.axis.types.Token("RiskRejected");
    public static final org.apache.axis.types.Token _ValidationError = new org.apache.axis.types.Token("ValidationError");
    public static final org.apache.axis.types.Token _WebRedirectRequired = new org.apache.axis.types.Token("WebRedirectRequired");
    public static final org.apache.axis.types.Token _ThreeDSecureRedirectRequired = new org.apache.axis.types.Token("ThreeDSecureRedirectRequired");
    public static final org.apache.axis.types.Token _VaultFailure = new org.apache.axis.types.Token("VaultFailure");
    public static final StatusNameType Error = new StatusNameType(_Error);
    public static final StatusNameType Pending = new StatusNameType(_Pending);
    public static final StatusNameType Cancelled = new StatusNameType(_Cancelled);
    public static final StatusNameType Completed = new StatusNameType(_Completed);
    public static final StatusNameType RiskRejected = new StatusNameType(_RiskRejected);
    public static final StatusNameType ValidationError = new StatusNameType(_ValidationError);
    public static final StatusNameType WebRedirectRequired = new StatusNameType(_WebRedirectRequired);
    public static final StatusNameType ThreeDSecureRedirectRequired = new StatusNameType(_ThreeDSecureRedirectRequired);
    public static final StatusNameType VaultFailure = new StatusNameType(_VaultFailure);
    public org.apache.axis.types.Token getValue() { return _value_;}
    public static StatusNameType fromValue(org.apache.axis.types.Token value)
          throws java.lang.IllegalArgumentException {
        StatusNameType enumeration = (StatusNameType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static StatusNameType fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        try {
            return fromValue(new org.apache.axis.types.Token(value));
        } catch (Exception e) {
            throw new java.lang.IllegalArgumentException();
        }
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_.toString();}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(StatusNameType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "StatusNameType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
