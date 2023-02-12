/**
 * AutoRepair.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.directi.pg.core.swiffpay.MPTransProcess;

public class AutoRepair  implements java.io.Serializable {
    private java.lang.String vin;

    private java.lang.String odometer;

    private java.lang.String workorder;

    private java.lang.String unit;

    private java.lang.String repaircode;

    public AutoRepair() {
    }

    public AutoRepair(
           java.lang.String vin,
           java.lang.String odometer,
           java.lang.String workorder,
           java.lang.String unit,
           java.lang.String repaircode) {
           this.vin = vin;
           this.odometer = odometer;
           this.workorder = workorder;
           this.unit = unit;
           this.repaircode = repaircode;
    }


    /**
     * Gets the vin value for this AutoRepair.
     * 
     * @return vin
     */
    public java.lang.String getVin() {
        return vin;
    }


    /**
     * Sets the vin value for this AutoRepair.
     * 
     * @param vin
     */
    public void setVin(java.lang.String vin) {
        this.vin = vin;
    }


    /**
     * Gets the odometer value for this AutoRepair.
     * 
     * @return odometer
     */
    public java.lang.String getOdometer() {
        return odometer;
    }


    /**
     * Sets the odometer value for this AutoRepair.
     * 
     * @param odometer
     */
    public void setOdometer(java.lang.String odometer) {
        this.odometer = odometer;
    }


    /**
     * Gets the workorder value for this AutoRepair.
     * 
     * @return workorder
     */
    public java.lang.String getWorkorder() {
        return workorder;
    }


    /**
     * Sets the workorder value for this AutoRepair.
     * 
     * @param workorder
     */
    public void setWorkorder(java.lang.String workorder) {
        this.workorder = workorder;
    }


    /**
     * Gets the unit value for this AutoRepair.
     * 
     * @return unit
     */
    public java.lang.String getUnit() {
        return unit;
    }


    /**
     * Sets the unit value for this AutoRepair.
     * 
     * @param unit
     */
    public void setUnit(java.lang.String unit) {
        this.unit = unit;
    }


    /**
     * Gets the repaircode value for this AutoRepair.
     * 
     * @return repaircode
     */
    public java.lang.String getRepaircode() {
        return repaircode;
    }


    /**
     * Sets the repaircode value for this AutoRepair.
     * 
     * @param repaircode
     */
    public void setRepaircode(java.lang.String repaircode) {
        this.repaircode = repaircode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AutoRepair)) return false;
        AutoRepair other = (AutoRepair) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.vin==null && other.getVin()==null) || 
             (this.vin!=null &&
              this.vin.equals(other.getVin()))) &&
            ((this.odometer==null && other.getOdometer()==null) || 
             (this.odometer!=null &&
              this.odometer.equals(other.getOdometer()))) &&
            ((this.workorder==null && other.getWorkorder()==null) || 
             (this.workorder!=null &&
              this.workorder.equals(other.getWorkorder()))) &&
            ((this.unit==null && other.getUnit()==null) || 
             (this.unit!=null &&
              this.unit.equals(other.getUnit()))) &&
            ((this.repaircode==null && other.getRepaircode()==null) || 
             (this.repaircode!=null &&
              this.repaircode.equals(other.getRepaircode())));
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
        if (getVin() != null) {
            _hashCode += getVin().hashCode();
        }
        if (getOdometer() != null) {
            _hashCode += getOdometer().hashCode();
        }
        if (getWorkorder() != null) {
            _hashCode += getWorkorder().hashCode();
        }
        if (getUnit() != null) {
            _hashCode += getUnit().hashCode();
        }
        if (getRepaircode() != null) {
            _hashCode += getRepaircode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AutoRepair.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:MPTransProcess", "AutoRepair"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vin");
        elemField.setXmlName(new javax.xml.namespace.QName("", "vin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("odometer");
        elemField.setXmlName(new javax.xml.namespace.QName("", "odometer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("workorder");
        elemField.setXmlName(new javax.xml.namespace.QName("", "workorder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unit");
        elemField.setXmlName(new javax.xml.namespace.QName("", "unit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("repaircode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "repaircode"));
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
