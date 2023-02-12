/**
 * MPIVEResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003;

public class MPIVEResponse  implements java.io.Serializable {
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MPIVEResponse.class, true);
    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "MPIVEResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("account");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Account"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("behaviour");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Behaviour"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.EFTXN.DAL.Model", "CardAccount3DSecureSetting.ThreeDBehaviourType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("externalID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "ExternalID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "VEResult"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminal");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/Transactium.Resin.Applications.PSPSoap.v1003", "Terminal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }
    private String account;
    private String[] behaviour;
    private String externalID;
    private com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.VEResult result;
    private String terminal;
    private Object __equalsCalc = null;
    private boolean __hashCodeCalc = false;


    public MPIVEResponse() {
    }


    public MPIVEResponse(
           String account,
           String[] behaviour,
           String externalID,
           com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.VEResult result,
           String terminal) {
           this.account = account;
           this.behaviour = behaviour;
           this.externalID = externalID;
           this.result = result;
           this.terminal = terminal;
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
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Gets the account value for this MPIVEResponse.
     *
     * @return account
     */
    public String getAccount() {
        return account;
    }

    /**
     * Sets the account value for this MPIVEResponse.
     *
     * @param account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * Gets the behaviour value for this MPIVEResponse.
     *
     * @return behaviour
     */
    public String[] getBehaviour() {
        return behaviour;
    }

    /**
     * Sets the behaviour value for this MPIVEResponse.
     *
     * @param behaviour
     */
    public void setBehaviour(String[] behaviour) {
        this.behaviour = behaviour;
    }

    /**
     * Gets the externalID value for this MPIVEResponse.
     *
     * @return externalID
     */
    public String getExternalID() {
        return externalID;
    }

    /**
     * Sets the externalID value for this MPIVEResponse.
     *
     * @param externalID
     */
    public void setExternalID(String externalID) {
        this.externalID = externalID;
    }

    /**
     * Gets the result value for this MPIVEResponse.
     *
     * @return result
     */
    public com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.VEResult getResult() {
        return result;
    }

    /**
     * Sets the result value for this MPIVEResponse.
     *
     * @param result
     */
    public void setResult(com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.VEResult result) {
        this.result = result;
    }

    /**
     * Gets the terminal value for this MPIVEResponse.
     *
     * @return terminal
     */
    public String getTerminal() {
        return terminal;
    }

    /**
     * Sets the terminal value for this MPIVEResponse.
     *
     * @param terminal
     */
    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof MPIVEResponse)) return false;
        MPIVEResponse other = (MPIVEResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.account==null && other.getAccount()==null) ||
             (this.account!=null &&
              this.account.equals(other.getAccount()))) &&
            ((this.behaviour==null && other.getBehaviour()==null) ||
             (this.behaviour!=null &&
              java.util.Arrays.equals(this.behaviour, other.getBehaviour()))) &&
            ((this.externalID==null && other.getExternalID()==null) ||
             (this.externalID!=null &&
              this.externalID.equals(other.getExternalID()))) &&
            ((this.result==null && other.getResult()==null) ||
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
            ((this.terminal==null && other.getTerminal()==null) ||
             (this.terminal!=null &&
              this.terminal.equals(other.getTerminal())));
        __equalsCalc = null;
        return _equals;
    }

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAccount() != null) {
            _hashCode += getAccount().hashCode();
        }
        if (getBehaviour() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getBehaviour());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getBehaviour(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getExternalID() != null) {
            _hashCode += getExternalID().hashCode();
        }
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        if (getTerminal() != null) {
            _hashCode += getTerminal().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
