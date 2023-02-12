package com.payment.common.core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 4/28/13
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class CountryCodeISO3166
{

    //    AALAND ISLANDS                                  AX      ALA     248
//    AFGHANISTAN                                     AF      AFG     004
//    ALBANIA                                         AL      ALB     008
//    ALGERIA                                         DZ      DZA     012
//    AMERICAN SAMOA                                  AS      ASM     016
//    ANDORRA                                         AD      AND     020
//    ANGOLA                                          AO      AGO     024
//    ANGUILLA                                        AI      AIA     660
//    ANTARCTICA                                      AQ      ATA     010
//    ANTIGUA AND BARBUDA                             AG      ATG     028
//    ARGENTINA                                       AR      ARG     032
//    ARMENIA                                         AM      ARM     051
//    ARUBA                                           AW      ABW     533
//    AUSTRALIA                                       AU      AUS     036
//    AUSTRIA                                         AT      AUT     040
//    AZERBAIJAN                                      AZ      AZE     031
//    BAHAMAS                                         BS      BHS     044
//    BAHRAIN                                         BH      BHR     048
//    BANGLADESH                                      BD      BGD     050
//    BARBADOS                                        BB      BRB     052
//    BELARUS                                         BY      BLR     112
//    BELGIUM                                         BE      BEL     056
//    BELIZE                                          BZ      BLZ     084
//    BENIN                                           BJ      BEN     204
//    BERMUDA                                         BM      BMU     060
//    BHUTAN                                          BT      BTN     064
//    BOLIVIA                                         BO      BOL     068
//    BOSNIA AND HERZEGOWINA                          BA      BIH     070
//    BOTSWANA                                        BW      BWA     072
//    BOUVET ISLAND                                   BV      BVT     074
//    BRAZIL                                          BR      BRA     076
//    BRITISH INDIAN OCEAN TERRITORY                  IO      IOT     086
//    BRUNEI DARUSSALAM                               BN      BRN     096
//    BULGARIA                                        BG      BGR     100
//    BURKINA FASO                                    BF      BFA     854
//    BURUNDI                                         BI      BDI     108
//    CAMBODIA                                        KH      KHM     116
//    CAMEROON                                        CM      CMR     120
//    CANADA                                          CA      CAN     124
//    CAPE VERDE                                      CV      CPV     132
//    CAYMAN ISLANDS                                  KY      CYM     136
//    CENTRAL AFRICAN REPUBLIC                        CF      CAF     140
//    CHAD                                            TD      TCD     148
//    CHILE                                           CL      CHL     152
//    CHINA                                           CN      CHN     156
//    CHRISTMAS ISLAND                                CX      CXR     162
//    COCOS (KEELING) ISLANDS                         CC      CCK     166
//    COLOMBIA                                        CO      COL     170
//    COMOROS                                         KM      COM     174
//    CONGO, Democratic Republic of (was Zaire)       CD      COD     180
//    CONGO, Republic of                              CG      COG     178
//    COOK ISLANDS                                    CK      COK     184
//    COSTA RICA                                      CR      CRI     188
//    COTE D'IVOIRE                                   CI      CIV     384
//    CROATIA (local name: Hrvatska)                  HR      HRV     191
//    CUBA                                            CU      CUB     192
//    CYPRUS                                          CY      CYP     196
//    CZECH REPUBLIC                                  CZ      CZE     203
//    DENMARK                                         DK      DNK     208
//    DJIBOUTI                                        DJ      DJI     262
//    DOMINICA                                        DM      DMA     212
//    DOMINICAN REPUBLIC                              DO      DOM     214
//    ECUADOR                                         EC      ECU     218
//    EGYPT                                           EG      EGY     818
//    EL SALVADOR                                     SV      SLV     222
//    EQUATORIAL GUINEA                               GQ      GNQ     226
//    ERITREA                                         ER      ERI     232
//    ESTONIA                                         EE      EST     233
//    ETHIOPIA                                        ET      ETH     231
//    FALKLAND ISLANDS (MALVINAS)                     FK      FLK     238
//    FAROE ISLANDS                                   FO      FRO     234
//    FIJI                                            FJ      FJI     242
//    FINLAND                                         FI      FIN     246
//    FRANCE                                          FR      FRA     250
//    FRENCH GUIANA                                   GF      GUF     254
//    FRENCH POLYNESIA                                PF      PYF     258
//    FRENCH SOUTHERN TERRITORIES                     TF      ATF     260
//    GABON                                           GA      GAB     266
//    GAMBIA                                          GM      GMB     270
//    GEORGIA                                         GE      GEO     268
//    GERMANY                                         DE      DEU     276
//    GHANA                                           GH      GHA     288
//    GIBRALTAR                                       GI      GIB     292
//    GREECE                                          GR      GRC     300
//    GREENLAND                                       GL      GRL     304
//    GRENADA                                         GD      GRD     308
//    GUADELOUPE                                      GP      GLP     312
//    GUAM                                            GU      GUM     316
//    GUATEMALA                                       GT      GTM     320
//    GUINEA                                          GN      GIN     324
//    GUINEA-BISSAU                                   GW      GNB     624
//    GUYANA                                          GY      GUY     328
//    HAITI                                           HT      HTI     332
//    HEARD AND MC DONALD ISLANDS                     HM      HMD     334
//    HONDURAS                                        HN      HND     340
//    HONG KONG                                       HK      HKG     344
//    HUNGARY                                         HU      HUN     348
//    ICELAND                                         IS      ISL     352
//    INDIA                                           IN      IND     356
//    INDONESIA                                       ID      IDN     360
//    IRAN (ISLAMIC REPUBLIC OF)                      IR      IRN     364
//    IRAQ                                            IQ      IRQ     368
//    IRELAND                                         IE      IRL     372
//    ISRAEL                                          IL      ISR     376
//    ITALY                                           IT      ITA     380
//    JAMAICA                                         JM      JAM     388
//    JAPAN                                           JP      JPN     392
//    JORDAN                                          JO      JOR     400
//    KAZAKHSTAN                                      KZ      KAZ     398
//    KENYA                                           KE      KEN     404
//    KIRIBATI                                        KI      KIR     296
//    KOREA, DEMOCRATIC PEOPLE'S REPUBLIC OF          KP      PRK     408
//    KOREA, REPUBLIC OF                              KR      KOR     410
//    KUWAIT                                          KW      KWT     414
//    KYRGYZSTAN                                      KG      KGZ     417
//    LAO PEOPLE'S DEMOCRATIC REPUBLIC                LA      LAO     418
//    LATVIA                                          LV      LVA     428
//    LEBANON                                         LB      LBN     422
//    LESOTHO                                         LS      LSO     426
//    LIBERIA                                         LR      LBR     430
//    LIBYAN ARAB JAMAHIRIYA                          LY      LBY     434
//    LIECHTENSTEIN                                   LI      LIE     438
//    LITHUANIA                                       LT      LTU     440
//    LUXEMBOURG                                      LU      LUX     442
//    MACAU                                           MO      MAC     446
//    MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF      MK      MKD     807
//    MADAGASCAR                                      MG      MDG     450
//    MALAWI                                          MW      MWI     454
//    MALAYSIA                                        MY      MYS     458
//    MALDIVES                                        MV      MDV     462
//    MALI                                            ML      MLI     466
//    MALTA                                           MT      MLT     470
//    MARSHALL ISLANDS                                MH      MHL     584
//    MARTINIQUE                                      MQ      MTQ     474
//    MAURITANIA                                      MR      MRT     478
//    MAURITIUS                                       MU      MUS     480
//    MAYOTTE                                         YT      MYT     175
//    MEXICO                                          MX      MEX     484
//    MICRONESIA, FEDERATED STATES OF                 FM      FSM     583
//    MOLDOVA, REPUBLIC OF                            MD      MDA     498
//    MONACO                                          MC      MCO     492
//    MONGOLIA                                        MN      MNG     496
//    MONTSERRAT                                      MS      MSR     500
//    MOROCCO                                         MA      MAR     504
//    MOZAMBIQUE                                      MZ      MOZ     508
//    MYANMAR                                         MM      MMR     104
//    NAMIBIA                                         NA      NAM     516
//    NAURU                                           NR      NRU     520
//    NEPAL                                           NP      NPL     524
//    NETHERLANDS                                     NL      NLD     528
//    NETHERLANDS ANTILLES                            AN      ANT     530
//    NEW CALEDONIA                                   NC      NCL     540
//    NEW ZEALAND                                     NZ      NZL     554
//    NICARAGUA                                       NI      NIC     558
//    NIGER                                           NE      NER     562
//    NIGERIA                                         NG      NGA     566
//    NIUE                                            NU      NIU     570
//    NORFOLK ISLAND                                  NF      NFK     574
//    NORTHERN MARIANA ISLANDS                        MP      MNP     580
//    NORWAY                                          NO      NOR     578
//    OMAN                                            OM      OMN     512
//    PAKISTAN                                        PK      PAK     586
//    PALAU                                           PW      PLW     585
//    PALESTINIAN TERRITORY, Occupied                 PS      PSE     275
//    PANAMA                                          PA      PAN     591
//    PAPUA NEW GUINEA                                PG      PNG     598
//    PARAGUAY                                        PY      PRY     600
//    PERU                                            PE      PER     604
//    PHILIPPINES                                     PH      PHL     608
//    PITCAIRN                                        PN      PCN     612
//    POLAND                                          PL      POL     616
//    PORTUGAL                                        PT      PRT     620
//    PUERTO RICO                                     PR      PRI     630
//    QATAR                                           QA      QAT     634
//    REUNION                                         RE      REU     638
//    ROMANIA                                         RO      ROU     642
//    RUSSIAN FEDERATION                              RU      RUS     643
//    RWANDA                                          RW      RWA     646
//    SAINT HELENA                                    SH      SHN     654
//    SAINT KITTS AND NEVIS                           KN      KNA     659
//    SAINT LUCIA                                     LC      LCA     662
//    SAINT PIERRE AND MIQUELON                       PM      SPM     666
//    SAINT VINCENT AND THE GRENADINES                VC      VCT     670
//    SAMOA                                           WS      WSM     882
//    SAN MARINO                                      SM      SMR     674
//    SAO TOME AND PRINCIPE                           ST      STP     678
//    SAUDI ARABIA                                    SA      SAU     682
//    SENEGAL                                         SN      SEN     686
//    SERBIA AND MONTENEGRO                           CS      SCG     891
//    SEYCHELLES                                      SC      SYC     690
//    SIERRA LEONE                                    SL      SLE     694
//    SINGAPORE                                       SG      SGP     702
//    SLOVAKIA                                        SK      SVK     703
//    SLOVENIA                                        SI      SVN     705
//    SOLOMON ISLANDS                                 SB      SLB     090
//    SOMALIA                                         SO      SOM     706
//    SOUTH AFRICA                                    ZA      ZAF     710
//    SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS    GS      SGS     239
//    SPAIN                                           ES      ESP     724
//    SRI LANKA                                       LK      LKA     144
//    SUDAN                                           SD      SDN     736
//    SURINAME                                        SR      SUR     740
//    SVALBARD AND JAN MAYEN ISLANDS                  SJ      SJM     744
//    SWAZILAND                                       SZ      SWZ     748
//    SWEDEN                                          SE      SWE     752
//    SWITZERLAND                                     CH      CHE     756
//    SYRIAN ARAB REPUBLIC                            SY      SYR     760
//    TAIWAN                                          TW      TWN     158
//    TAJIKISTAN                                      TJ      TJK     762
//    TANZANIA, UNITED REPUBLIC OF                    TZ      TZA     834
//    THAILAND                                        TH      THA     764
//    TIMOR-LESTE                                     TL      TLS     626
//    TOGO                                            TG      TGO     768
//    TOKELAU                                         TK      TKL     772
//    TONGA                                           TO      TON     776
//    TRINIDAD AND TOBAGO                             TT      TTO     780
//    TUNISIA                                         TN      TUN     788
//    TURKEY                                          TR      TUR     792
//    TURKMENISTAN                                    TM      TKM     795
//    TURKS AND CAICOS ISLANDS                        TC      TCA     796
//    TUVALU                                          TV      TUV     798
//    UGANDA                                          UG      UGA     800
//    UKRAINE                                         UA      UKR     804
//    UNITED ARAB EMIRATES                            AE      ARE     784
//    UNITED KINGDOM                                  GB      GBR     826
//    UNITED STATES                                   US      USA     840
//    UNITED STATES MINOR OUTLYING ISLANDS            UM      UMI     581
//    URUGUAY                                         UY      URY     858
//    UZBEKISTAN                                      UZ      UZB     860
//    VANUATU                                         VU      VUT     548
//    VATICAN CITY STATE (HOLY SEE)                   VA      VAT     336
//    VENEZUELA                                       VE      VEN     862
//    VIET NAM                                        VN      VNM     704
//    VIRGIN ISLANDS (BRITISH)                        VG      VGB     092
//    VIRGIN ISLANDS (U.S.)                           VI      VIR     850
//    WALLIS AND FUTUNA ISLANDS                       WF      WLF     876
//    WESTERN SAHARA                                  EH      ESH     732
//    YEMEN                                           YE      YEM     887
//    ZAMBIA                                          ZM      ZMB     894
//    ZIMBABWE                                        ZW      ZWE     716
//
    private static HashMap<String, String> countryCodeHash = new HashMap<String, String>();

    static
    {
        countryCodeHash.put("AX", "248");
        countryCodeHash.put("AF", "004");
        countryCodeHash.put("AL", "008");
        countryCodeHash.put("DZ", "012");
        countryCodeHash.put("AS", "016");
        countryCodeHash.put("AD", "020");
        countryCodeHash.put("AO", "024");
        countryCodeHash.put("AI", "660");
        countryCodeHash.put("AQ", "010");
        countryCodeHash.put("AG", "028");
        countryCodeHash.put("AR", "032");
        countryCodeHash.put("AM", "051");
        countryCodeHash.put("AW", "533");
        countryCodeHash.put("AU", "036");
        countryCodeHash.put("AT", "040");
        countryCodeHash.put("AZ", "031");
        countryCodeHash.put("BS", "044");
        countryCodeHash.put("BH", "048");
        countryCodeHash.put("BD", "050");
        countryCodeHash.put("BB", "052");
        countryCodeHash.put("BY", "112");
        countryCodeHash.put("BE", "056");
        countryCodeHash.put("BZ", "084");
        countryCodeHash.put("BJ", "204");
        countryCodeHash.put("BM", "060");
        countryCodeHash.put("BT", "064");
        countryCodeHash.put("BO", "068");
        countryCodeHash.put("BA", "070");
        countryCodeHash.put("BW", "072");
        countryCodeHash.put("BV", "074");
        countryCodeHash.put("BR", "076");
        countryCodeHash.put("IO", "086");
        countryCodeHash.put("BN", "096");
        countryCodeHash.put("BG", "100");
        countryCodeHash.put("BF", "854");
        countryCodeHash.put("BI", "108");
        countryCodeHash.put("KH", "116");
        countryCodeHash.put("CM", "120");
        countryCodeHash.put("CA", "124");
        countryCodeHash.put("CV", "132");
        countryCodeHash.put("KY", "136");
        countryCodeHash.put("CF", "140");
        countryCodeHash.put("TD", "148");
        countryCodeHash.put("CL", "152");
        countryCodeHash.put("CN", "156");
        countryCodeHash.put("CX", "162");
        countryCodeHash.put("CC", "166");
        countryCodeHash.put("CO", "170");
        countryCodeHash.put("KM", "174");
        countryCodeHash.put("CD", "180");
        countryCodeHash.put("CG", "178");
        countryCodeHash.put("CK", "184");
        countryCodeHash.put("CR", "188");
        countryCodeHash.put("CI", "384");
        countryCodeHash.put("HR", "191");
        countryCodeHash.put("CU", "192");
        countryCodeHash.put("CY", "196");
        countryCodeHash.put("CZ", "203");
        countryCodeHash.put("DK", "208");
        countryCodeHash.put("DJ", "262");
        countryCodeHash.put("DM", "212");
        countryCodeHash.put("DO", "214");
        countryCodeHash.put("EC", "218");
        countryCodeHash.put("EG", "818");
        countryCodeHash.put("SV", "222");
        countryCodeHash.put("GQ", "226");
        countryCodeHash.put("ER", "232");
        countryCodeHash.put("EE", "233");
        countryCodeHash.put("ET", "231");
        countryCodeHash.put("FK", "238");
        countryCodeHash.put("FO", "234");
        countryCodeHash.put("FJ", "242");
        countryCodeHash.put("FI", "246");
        countryCodeHash.put("FR", "250");
        countryCodeHash.put("GF", "254");
        countryCodeHash.put("PF", "258");
        countryCodeHash.put("TF", "260");
        countryCodeHash.put("GA", "266");
        countryCodeHash.put("GM", "270");
        countryCodeHash.put("GE", "268");
        countryCodeHash.put("DE", "276");
        countryCodeHash.put("GH", "288");
        countryCodeHash.put("GI", "292");
        countryCodeHash.put("GR", "300");
        countryCodeHash.put("GL", "304");
        countryCodeHash.put("GD", "308");
        countryCodeHash.put("GP", "312");
        countryCodeHash.put("GU", "316");
        countryCodeHash.put("GT", "320");
        countryCodeHash.put("GN", "324");
        countryCodeHash.put("GW", "624");
        countryCodeHash.put("GY", "328");
        countryCodeHash.put("HT", "332");
        countryCodeHash.put("HM", "334");
        countryCodeHash.put("HN", "340");
        countryCodeHash.put("HK", "344");
        countryCodeHash.put("HU", "348");
        countryCodeHash.put("IS", "352");
        countryCodeHash.put("IN", "356");
        countryCodeHash.put("ID", "360");
        countryCodeHash.put("IR", "364");
        countryCodeHash.put("IQ", "368");
        countryCodeHash.put("IE", "372");
        countryCodeHash.put("IL", "376");
        countryCodeHash.put("IT", "380");
        countryCodeHash.put("JM", "388");
        countryCodeHash.put("JP", "392");
        countryCodeHash.put("JO", "400");
        countryCodeHash.put("KZ", "398");
        countryCodeHash.put("KE", "404");
        countryCodeHash.put("KI", "296");
        countryCodeHash.put("KP", "408");
        countryCodeHash.put("KR", "410");
        countryCodeHash.put("KW", "414");
        countryCodeHash.put("KG", "417");
        countryCodeHash.put("LA", "418");
        countryCodeHash.put("LV", "428");
        countryCodeHash.put("LB", "422");
        countryCodeHash.put("LS", "426");
        countryCodeHash.put("LR", "430");
        countryCodeHash.put("LY", "434");
        countryCodeHash.put("LI", "438");
        countryCodeHash.put("LT", "440");
        countryCodeHash.put("LU", "442");
        countryCodeHash.put("MO", "446");
        countryCodeHash.put("MK", "807");
        countryCodeHash.put("MG", "450");
        countryCodeHash.put("MW", "454");
        countryCodeHash.put("MY", "458");
        countryCodeHash.put("MV", "462");
        countryCodeHash.put("ML", "466");
        countryCodeHash.put("MT", "470");
        countryCodeHash.put("MH", "584");
        countryCodeHash.put("MQ", "474");
        countryCodeHash.put("MR", "478");
        countryCodeHash.put("MU", "480");
        countryCodeHash.put("YT", "175");
        countryCodeHash.put("MX", "484");
        countryCodeHash.put("FM", "583");
        countryCodeHash.put("MD", "498");
        countryCodeHash.put("MC", "492");
        countryCodeHash.put("MN", "496");
        countryCodeHash.put("MS", "500");
        countryCodeHash.put("MA", "504");
        countryCodeHash.put("MZ", "508");
        countryCodeHash.put("MM", "104");
        countryCodeHash.put("NA", "516");
        countryCodeHash.put("NR", "520");
        countryCodeHash.put("NP", "524");
        countryCodeHash.put("NL", "528");
        countryCodeHash.put("AN", "530");
        countryCodeHash.put("NC", "540");
        countryCodeHash.put("NZ", "554");
        countryCodeHash.put("NI", "558");
        countryCodeHash.put("NE", "562");
        countryCodeHash.put("NG", "566");
        countryCodeHash.put("NU", "570");
        countryCodeHash.put("NF", "574");
        countryCodeHash.put("MP", "580");
        countryCodeHash.put("NO", "578");
        countryCodeHash.put("OM", "512");
        countryCodeHash.put("PK", "586");
        countryCodeHash.put("PW", "585");
        countryCodeHash.put("PS", "275");
        countryCodeHash.put("PA", "591");
        countryCodeHash.put("PG", "598");
        countryCodeHash.put("PY", "600");
        countryCodeHash.put("PE", "604");
        countryCodeHash.put("PH", "608");
        countryCodeHash.put("PN", "612");
        countryCodeHash.put("PL", "616");
        countryCodeHash.put("PT", "620");
        countryCodeHash.put("PR", "630");
        countryCodeHash.put("QA", "634");
        countryCodeHash.put("RE", "638");
        countryCodeHash.put("RO", "642");
        countryCodeHash.put("RU", "643");
        countryCodeHash.put("RW", "646");
        countryCodeHash.put("SH", "654");
        countryCodeHash.put("KN", "659");
        countryCodeHash.put("LC", "662");
        countryCodeHash.put("PM", "666");
        countryCodeHash.put("VC", "670");
        countryCodeHash.put("WS", "882");
        countryCodeHash.put("SM", "674");
        countryCodeHash.put("ST", "678");
        countryCodeHash.put("SA", "682");
        countryCodeHash.put("SN", "686");
        countryCodeHash.put("CS", "891");
        countryCodeHash.put("SC", "690");
        countryCodeHash.put("SL", "694");
        countryCodeHash.put("SG", "702");
        countryCodeHash.put("SK", "703");
        countryCodeHash.put("SI", "705");
        countryCodeHash.put("SB", "090");
        countryCodeHash.put("SO", "706");
        countryCodeHash.put("ZA", "710");
        countryCodeHash.put("GS", "239");
        countryCodeHash.put("ES", "724");
        countryCodeHash.put("LK", "144");
        countryCodeHash.put("SD", "736");
        countryCodeHash.put("SR", "740");
        countryCodeHash.put("SJ", "744");
        countryCodeHash.put("SZ", "748");
        countryCodeHash.put("SE", "752");
        countryCodeHash.put("CH", "756");
        countryCodeHash.put("SY", "760");
        countryCodeHash.put("TW", "158");
        countryCodeHash.put("TJ", "762");
        countryCodeHash.put("TZ", "834");
        countryCodeHash.put("TH", "764");
        countryCodeHash.put("TL", "626");
        countryCodeHash.put("TG", "768");
        countryCodeHash.put("TK", "772");
        countryCodeHash.put("TO", "776");
        countryCodeHash.put("TT", "780");
        countryCodeHash.put("TN", "788");
        countryCodeHash.put("TR", "792");
        countryCodeHash.put("TM", "795");
        countryCodeHash.put("TC", "796");
        countryCodeHash.put("TV", "798");
        countryCodeHash.put("UG", "800");
        countryCodeHash.put("UA", "804");
        countryCodeHash.put("AE", "784");
        countryCodeHash.put("GB", "826");
        countryCodeHash.put("US", "840");
        countryCodeHash.put("UM", "581");
        countryCodeHash.put("UY", "858");
        countryCodeHash.put("UZ", "860");
        countryCodeHash.put("VU", "548");
        countryCodeHash.put("VA", "336");
        countryCodeHash.put("VE", "862");
        countryCodeHash.put("VN", "704");
        countryCodeHash.put("VG", "092");
        countryCodeHash.put("VI", "850");
        countryCodeHash.put("WF", "876");
        countryCodeHash.put("EH", "732");
        countryCodeHash.put("YE", "887");
        countryCodeHash.put("ZM", "894");
        countryCodeHash.put("ZW", "716");


    }

    public static String getNumericCountryCode(String twoCharacterCountryCode)
    {
        return countryCodeHash.get(twoCharacterCountryCode.toUpperCase());
    }


}
