/**
 * PaymentMethodType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.payhost.core.za.co.paygate.www.PayHOST;

public class PaymentMethodType implements java.io.Serializable {
    private org.apache.axis.types.Token _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected PaymentMethodType(org.apache.axis.types.Token value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final org.apache.axis.types.Token _CC = new org.apache.axis.types.Token("CC");
    public static final org.apache.axis.types.Token _DC = new org.apache.axis.types.Token("DC");
    public static final org.apache.axis.types.Token _EW = new org.apache.axis.types.Token("EW");
    public static final org.apache.axis.types.Token _BT = new org.apache.axis.types.Token("BT");
    public static final org.apache.axis.types.Token _CV = new org.apache.axis.types.Token("CV");
    public static final org.apache.axis.types.Token _PC = new org.apache.axis.types.Token("PC");
    public static final PaymentMethodType CC = new PaymentMethodType(_CC);
    public static final PaymentMethodType DC = new PaymentMethodType(_DC);
    public static final PaymentMethodType EW = new PaymentMethodType(_EW);
    public static final PaymentMethodType BT = new PaymentMethodType(_BT);
    public static final PaymentMethodType CV = new PaymentMethodType(_CV);
    public static final PaymentMethodType PC = new PaymentMethodType(_PC);
    public org.apache.axis.types.Token getValue() { return _value_;}
    public static PaymentMethodType fromValue(org.apache.axis.types.Token value)
          throws java.lang.IllegalArgumentException {
        PaymentMethodType enumeration = (PaymentMethodType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static PaymentMethodType fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(PaymentMethodType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.paygate.co.za/PayHOST", "PaymentMethodType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
