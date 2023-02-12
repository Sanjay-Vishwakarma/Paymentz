
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CCBrand.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CCBrand">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ANAMileage"/>
 *     &lt;enumeration value="OdeonClub"/>
 *     &lt;enumeration value="UATPCard"/>
 *     &lt;enumeration value="AMEX"/>
 *     &lt;enumeration value="VISA"/>
 *     &lt;enumeration value="DinersClub"/>
 *     &lt;enumeration value="DinersClubIntl"/>
 *     &lt;enumeration value="JCB"/>
 *     &lt;enumeration value="Voyager"/>
 *     &lt;enumeration value="Discover"/>
 *     &lt;enumeration value="MasterCard"/>
 *     &lt;enumeration value="Maestro"/>
 *     &lt;enumeration value="SWITCH"/>
 *     &lt;enumeration value="Dankort"/>
 *     &lt;enumeration value="ChinaUnionPay"/>
 *     &lt;enumeration value="Unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CCBrand")
@XmlEnum
public enum CCBrand {

    @XmlEnumValue("ANAMileage")
    ANA_MILEAGE("ANAMileage"),
    @XmlEnumValue("OdeonClub")
    ODEON_CLUB("OdeonClub"),
    @XmlEnumValue("UATPCard")
    UATP_CARD("UATPCard"),
    AMEX("AMEX"),
    VISA("VISA"),
    @XmlEnumValue("DinersClub")
    DINERS_CLUB("DinersClub"),
    @XmlEnumValue("DinersClubIntl")
    DINERS_CLUB_INTL("DinersClubIntl"),
    JCB("JCB"),
    @XmlEnumValue("Voyager")
    VOYAGER("Voyager"),
    @XmlEnumValue("Discover")
    DISCOVER("Discover"),
    @XmlEnumValue("MasterCard")
    MASTER_CARD("MasterCard"),
    @XmlEnumValue("Maestro")
    MAESTRO("Maestro"),
    SWITCH("SWITCH"),
    @XmlEnumValue("Dankort")
    DANKORT("Dankort"),
    @XmlEnumValue("ChinaUnionPay")
    CHINA_UNION_PAY("ChinaUnionPay"),
    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown");
    private final String value;

    CCBrand(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CCBrand fromValue(String v) {
        for (CCBrand c: CCBrand.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
