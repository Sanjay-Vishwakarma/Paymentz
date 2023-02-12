
package com.payment.europay.core.message;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Currency.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Currency">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ALL"/>
 *     &lt;enumeration value="AFN"/>
 *     &lt;enumeration value="ARS"/>
 *     &lt;enumeration value="AWG"/>
 *     &lt;enumeration value="AUD"/>
 *     &lt;enumeration value="AZN"/>
 *     &lt;enumeration value="BSD"/>
 *     &lt;enumeration value="BBD"/>
 *     &lt;enumeration value="BYR"/>
 *     &lt;enumeration value="BZD"/>
 *     &lt;enumeration value="BMD"/>
 *     &lt;enumeration value="BOB"/>
 *     &lt;enumeration value="BAM"/>
 *     &lt;enumeration value="BWP"/>
 *     &lt;enumeration value="BGN"/>
 *     &lt;enumeration value="BRL"/>
 *     &lt;enumeration value="BND"/>
 *     &lt;enumeration value="KHR"/>
 *     &lt;enumeration value="CAD"/>
 *     &lt;enumeration value="KYD"/>
 *     &lt;enumeration value="CLP"/>
 *     &lt;enumeration value="CNY"/>
 *     &lt;enumeration value="COP"/>
 *     &lt;enumeration value="CRC"/>
 *     &lt;enumeration value="HRK"/>
 *     &lt;enumeration value="CUP"/>
 *     &lt;enumeration value="CZK"/>
 *     &lt;enumeration value="DKK"/>
 *     &lt;enumeration value="DOP"/>
 *     &lt;enumeration value="XCD"/>
 *     &lt;enumeration value="EGP"/>
 *     &lt;enumeration value="SVC"/>
 *     &lt;enumeration value="EEK"/>
 *     &lt;enumeration value="EUR"/>
 *     &lt;enumeration value="FKP"/>
 *     &lt;enumeration value="FJD"/>
 *     &lt;enumeration value="GHC"/>
 *     &lt;enumeration value="GIP"/>
 *     &lt;enumeration value="GTQ"/>
 *     &lt;enumeration value="GGP"/>
 *     &lt;enumeration value="GYD"/>
 *     &lt;enumeration value="HNL"/>
 *     &lt;enumeration value="HKD"/>
 *     &lt;enumeration value="HUF"/>
 *     &lt;enumeration value="ISK"/>
 *     &lt;enumeration value="INR"/>
 *     &lt;enumeration value="IDR"/>
 *     &lt;enumeration value="IRR"/>
 *     &lt;enumeration value="IMP"/>
 *     &lt;enumeration value="ILS"/>
 *     &lt;enumeration value="JMD"/>
 *     &lt;enumeration value="JPY"/>
 *     &lt;enumeration value="JEP"/>
 *     &lt;enumeration value="KZT"/>
 *     &lt;enumeration value="KPW"/>
 *     &lt;enumeration value="KRW"/>
 *     &lt;enumeration value="KGS"/>
 *     &lt;enumeration value="LAK"/>
 *     &lt;enumeration value="LVL"/>
 *     &lt;enumeration value="LBP"/>
 *     &lt;enumeration value="LRD"/>
 *     &lt;enumeration value="LTL"/>
 *     &lt;enumeration value="MKD"/>
 *     &lt;enumeration value="MYR"/>
 *     &lt;enumeration value="MUR"/>
 *     &lt;enumeration value="MXN"/>
 *     &lt;enumeration value="MNT"/>
 *     &lt;enumeration value="MZN"/>
 *     &lt;enumeration value="NAD"/>
 *     &lt;enumeration value="NPR"/>
 *     &lt;enumeration value="ANG"/>
 *     &lt;enumeration value="NZD"/>
 *     &lt;enumeration value="NIO"/>
 *     &lt;enumeration value="NGN"/>
 *     &lt;enumeration value="NOK"/>
 *     &lt;enumeration value="OMR"/>
 *     &lt;enumeration value="PKR"/>
 *     &lt;enumeration value="PAB"/>
 *     &lt;enumeration value="PYG"/>
 *     &lt;enumeration value="PEN"/>
 *     &lt;enumeration value="PHP"/>
 *     &lt;enumeration value="PLN"/>
 *     &lt;enumeration value="QAR"/>
 *     &lt;enumeration value="RON"/>
 *     &lt;enumeration value="RUB"/>
 *     &lt;enumeration value="SHP"/>
 *     &lt;enumeration value="SAR"/>
 *     &lt;enumeration value="RSD"/>
 *     &lt;enumeration value="SCR"/>
 *     &lt;enumeration value="SGD"/>
 *     &lt;enumeration value="SBD"/>
 *     &lt;enumeration value="SOS"/>
 *     &lt;enumeration value="ZAR"/>
 *     &lt;enumeration value="LKR"/>
 *     &lt;enumeration value="SEK"/>
 *     &lt;enumeration value="CHF"/>
 *     &lt;enumeration value="SRD"/>
 *     &lt;enumeration value="SYP"/>
 *     &lt;enumeration value="TWD"/>
 *     &lt;enumeration value="THB"/>
 *     &lt;enumeration value="TTD"/>
 *     &lt;enumeration value="TRY"/>
 *     &lt;enumeration value="TRL"/>
 *     &lt;enumeration value="TVD"/>
 *     &lt;enumeration value="UAH"/>
 *     &lt;enumeration value="GBP"/>
 *     &lt;enumeration value="USD"/>
 *     &lt;enumeration value="UYU"/>
 *     &lt;enumeration value="UZS"/>
 *     &lt;enumeration value="VEF"/>
 *     &lt;enumeration value="VND"/>
 *     &lt;enumeration value="YER"/>
 *     &lt;enumeration value="ZWD"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Currency")
@XmlEnum
public enum Currency {

    ALL,
    AFN,
    ARS,
    AWG,
    AUD,
    AZN,
    BSD,
    BBD,
    BYR,
    BZD,
    BMD,
    BOB,
    BAM,
    BWP,
    BGN,
    BRL,
    BND,
    KHR,
    CAD,
    KYD,
    CLP,
    CNY,
    COP,
    CRC,
    HRK,
    CUP,
    CZK,
    DKK,
    DOP,
    XCD,
    EGP,
    SVC,
    EEK,
    EUR,
    FKP,
    FJD,
    GHC,
    GIP,
    GTQ,
    GGP,
    GYD,
    HNL,
    HKD,
    HUF,
    ISK,
    INR,
    IDR,
    IRR,
    IMP,
    ILS,
    JMD,
    JPY,
    JEP,
    KZT,
    KPW,
    KRW,
    KGS,
    LAK,
    LVL,
    LBP,
    LRD,
    LTL,
    MKD,
    MYR,
    MUR,
    MXN,
    MNT,
    MZN,
    NAD,
    NPR,
    ANG,
    NZD,
    NIO,
    NGN,
    NOK,
    OMR,
    PKR,
    PAB,
    PYG,
    PEN,
    PHP,
    PLN,
    QAR,
    RON,
    RUB,
    SHP,
    SAR,
    RSD,
    SCR,
    SGD,
    SBD,
    SOS,
    ZAR,
    LKR,
    SEK,
    CHF,
    SRD,
    SYP,
    TWD,
    THB,
    TTD,
    TRY,
    TRL,
    TVD,
    UAH,
    GBP,
    USD,
    UYU,
    UZS,
    VEF,
    VND,
    YER,
    ZWD;

    public String value() {
        return name();
    }

    public static Currency fromValue(String v) {
        return valueOf(v);
    }

}
