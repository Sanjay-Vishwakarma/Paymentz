
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransactionMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TransactionMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="sync"/>
 *     &lt;enumeration value="async"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TransactionMode")
@XmlEnum
public enum TransactionMode {

    @XmlEnumValue("sync")
    SYNC("sync"),
    @XmlEnumValue("async")
    ASYNC("async");
    private final String value;

    TransactionMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TransactionMode fromValue(String v) {
        for (TransactionMode c: TransactionMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
