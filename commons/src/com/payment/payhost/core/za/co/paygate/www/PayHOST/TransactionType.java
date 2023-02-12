/**
 * TransactionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class TransactionType implements java.io.Serializable {
    private org.apache.axis.types.Token _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected TransactionType(org.apache.axis.types.Token value)
    {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final org.apache.axis.types.Token _Authorisation = new org.apache.axis.types.Token("Authorisation");
    public static final org.apache.axis.types.Token _Settlement = new org.apache.axis.types.Token("Settlement");
    public static final org.apache.axis.types.Token _Refund = new org.apache.axis.types.Token("Refund");
    public static final org.apache.axis.types.Token _Payout = new org.apache.axis.types.Token("Payout");
    public static final org.apache.axis.types.Token _Purchase = new org.apache.axis.types.Token("Purchase");
    public static final TransactionType Authorisation = new TransactionType(_Authorisation);
    public static final TransactionType Settlement = new TransactionType(_Settlement);
    public static final TransactionType Refund = new TransactionType(_Refund);
    public static final TransactionType Payout = new TransactionType(_Payout);
    public static final TransactionType Purchase = new TransactionType(_Purchase);
    public org.apache.axis.types.Token getValue() { return _value_;}
    public static TransactionType fromValue(org.apache.axis.types.Token value)
          throws java.lang.IllegalArgumentException {
        TransactionType enumeration = (TransactionType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static TransactionType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(TransactionType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "TransactionType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc()
    {
        return typeDesc;
    }

}
