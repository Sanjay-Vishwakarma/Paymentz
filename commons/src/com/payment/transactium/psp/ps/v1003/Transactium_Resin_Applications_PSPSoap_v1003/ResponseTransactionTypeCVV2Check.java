/**
 * ResponseTransactionTypeCVV2Check.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class ResponseTransactionTypeCVV2Check implements java.io.Serializable {

    private static java.util.HashMap _table_ = new java.util.HashMap();
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ResponseTransactionTypeCVV2Check.class);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Response.TransactionType.CVV2Check"));
    }
    private String _value_;
    // Constructor
    protected ResponseTransactionTypeCVV2Check(String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final String _Absent = "Absent";
    public static final String _Present = "Present";
    public static final String _Valid = "Valid";
    public static final String _Invalid = "Invalid";
    public static final String _NotChecked = "NotChecked";
    public static final ResponseTransactionTypeCVV2Check Absent = new ResponseTransactionTypeCVV2Check(_Absent);
    public static final ResponseTransactionTypeCVV2Check Present = new ResponseTransactionTypeCVV2Check(_Present);
    public static final ResponseTransactionTypeCVV2Check Valid = new ResponseTransactionTypeCVV2Check(_Valid);
    public static final ResponseTransactionTypeCVV2Check Invalid = new ResponseTransactionTypeCVV2Check(_Invalid);
    public static final ResponseTransactionTypeCVV2Check NotChecked = new ResponseTransactionTypeCVV2Check(_NotChecked);

    public static ResponseTransactionTypeCVV2Check fromValue(String value)
          throws IllegalArgumentException {
        ResponseTransactionTypeCVV2Check enumeration = (ResponseTransactionTypeCVV2Check)
            _table_.get(value);
        if (enumeration==null) throw new IllegalArgumentException();
        return enumeration;
    }

    public static ResponseTransactionTypeCVV2Check fromString(String value)
          throws IllegalArgumentException {
        return fromValue(value);
    }

    public static org.apache.axis.encoding.Serializer getSerializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }

    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    public String getValue() { return _value_;}

    public boolean equals(Object obj) {return (obj == this);}

    public int hashCode() { return toString().hashCode();}

    public String toString() { return _value_;}

    public Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}

}
