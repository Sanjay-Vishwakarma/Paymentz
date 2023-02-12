<script>
  var countryval = document.getElementById('getcountval').value;
  $('[name=country] option').filter(function() {
    if (countryval == 'GB') {
      return ($(this).text() == 'United Kingdom');
    } else if (countryval == 'US') {
      return ($(this).text() == 'United States');
    } else if (countryval == 'AF') {
      return ($(this).text() == 'Afghanistan');
    } else if (countryval == 'AX') {
      return ($(this).text() == 'Aland Islands');
    } else if (countryval == 'AL') {
      return ($(this).text() == 'Albania');
    } else if (countryval == 'DZ') {
      return ($(this).text() == 'Algeria');
    } else if (countryval == 'AS') {
      return ($(this).text() == 'American Samoa');
    } else if (countryval == 'AD') {
      return ($(this).text() == 'Andorra');
    } else if (countryval == 'AO') {
      return ($(this).text() == 'Angola');
    } else if (countryval == 'AI') {
      return ($(this).text() == 'Anguilla');
    } else if (countryval == 'AQ') {
      return ($(this).text() == 'Antarctica');
    } else if (countryval == 'AG') {
      return ($(this).text() == 'Antigua and Barbuda');
    } else if (countryval == 'AR') {
      return ($(this).text() == 'Argentina');
    } else if (countryval == 'AM') {
      return ($(this).text() == 'Armenia');
    } else if (countryval == 'AW') {
      return ($(this).text() == 'Aruba');
    } else if (countryval == 'AU') {
      return ($(this).text() == 'Australia');
    } else if (countryval == 'AT') {
      return ($(this).text() == 'Austria');
    } else if (countryval == 'AZ') {
      return ($(this).text() == 'Azerbaijan');
    } else if (countryval == 'BS') {
      return ($(this).text() == 'Bahamas');
    } else if (countryval == 'BH') {
      return ($(this).text() == 'Bahrain');
    } else if (countryval == 'BD') {
      return ($(this).text() == 'Bangladesh');
    } else if (countryval == 'BB') {
      return ($(this).text() == 'Barbados');
    } else if (countryval == 'BY') {
      return ($(this).text() == 'Belarus');
    } else if (countryval == 'BE') {
      return ($(this).text() == 'Belgium');
    } else if (countryval == 'BZ') {
      return ($(this).text() == 'Belize');
    } else if (countryval == 'BJ') {
      return ($(this).text() == 'Benin');
    } else if (countryval == 'BM') {
      return ($(this).text() == 'Bermuda');
    } else if (countryval == 'BT') {
      return ($(this).text() == 'Bhutan');
    } else if (countryval == 'BO') {
      return ($(this).text() == 'Bolivia');
    } else if (countryval == 'BA') {
      return ($(this).text() == 'Bosnia and Herzegovina');
    } else if (countryval == 'BW') {
      return ($(this).text() == 'Botswana');
    } else if (countryval == 'BV') {
      return ($(this).text() == 'Bouvet Island');
    } else if (countryval == 'BR') {
      return ($(this).text() == 'Brazil');
    } else if (countryval == 'IO') {
      return ($(this).text() == 'British Indian Ocean Territory');
    } else if (countryval == 'VG') {
      return ($(this).text() == 'British Virgin Islands');
    } else if (countryval == 'BN') {
      return ($(this).text() == 'Brunei');
    } else if (countryval == 'BG') {
      return ($(this).text() == 'Bulgaria');
    } else if (countryval == 'BF') {
      return ($(this).text() == 'Burkina Faso');
    } else if (countryval == 'BI') {
      return ($(this).text() == 'Burundi');
    } else if (countryval == 'KH') {
      return ($(this).text() == 'Cambodia');
    } else if (countryval == 'CM') {
      return ($(this).text() == 'Cameroon');
    } else if (countryval == 'CA') {
      return ($(this).text() == 'Canada');
    } else if (countryval == 'CV') {
      return ($(this).text() == 'Cape Verde');
    } else if (countryval == 'KY') {
      return ($(this).text() == 'Cayman Islands');
    } else if (countryval == 'CF') {
      return ($(this).text() == 'Central African Republic');
    } else if (countryval == 'TD') {
      return ($(this).text() == 'Chad');
    } else if (countryval == 'CL') {
      return ($(this).text() == 'Chile');
    } else if (countryval == 'CN') {
      return ($(this).text() == 'China');
    } else if (countryval == 'CX') {
      return ($(this).text() == 'Christmas Island');
    } else if (countryval == 'CC') {
      return ($(this).text() == 'Cocos (Keeling) Islands');
    } else if (countryval == 'CO') {
      return ($(this).text() == 'Colombia');
    } else if (countryval == 'KM') {
      return ($(this).text() == 'Comoros');
    } else if (countryval == 'CK') {
      return ($(this).text() == 'Cook Islands');
    } else if (countryval == 'CR') {
      return ($(this).text() == 'Costa Rica');
    } else if (countryval == 'CI') {
      return ($(this).text() == 'Cote d` Ivoire ');
    } else if (countryval == 'HR') {
      return ($(this).text() == 'Croatia');
    } else if (countryval == 'CU') {
      return ($(this).text() == 'Cuba');
    } else if (countryval == 'CY') {
      return ($(this).text() == 'Cyprus');
    } else if (countryval == 'CZ') {
      return ($(this).text() == 'Czech Republic');
    } else if (countryval == 'CD') {
      return ($(this).text() == 'Democratic Republic of the Congo');
    } else if (countryval == 'DK') {
      return ($(this).text() == 'Denmark');
    } else if (countryval == 'DJ') {
      return ($(this).text() == 'Djibouti');
    } else if (countryval == 'DM') {
      return ($(this).text() == 'Dominica');
    } else if (countryval == 'DO') {
      return ($(this).text() == 'Dominican Republic');
    } else if (countryval == 'EC') {
      return ($(this).text() == 'Ecuador');
    } else if (countryval == 'EG') {
      return ($(this).text() == 'Egypt');
    } else if (countryval == 'SV') {
      return ($(this).text() == 'El Salvador');
    } else if (countryval == 'GQ') {
      return ($(this).text() == 'Equatorial Guinea');
    } else if (countryval == 'ER') {
      return ($(this).text() == 'Eritrea');
    } else if (countryval == 'EE') {
      return ($(this).text() == 'Estonia');
    } else if (countryval == 'ET') {
      return ($(this).text() == 'Ethiopia');
    } else if (countryval == 'FK') {
      return ($(this).text() == 'Falkland Islands');
    } else if (countryval == 'FO') {
      return ($(this).text() == 'Faroe Islands');
    } else if (countryval == 'FJ') {
      return ($(this).text() == 'Fiji');
    } else if (countryval == 'FI') {
      return ($(this).text() == 'Finland');
    } else if (countryval == 'FR') {
      return ($(this).text() == 'France');
    } else if (countryval == 'GF') {
      return ($(this).text() == 'French Guiana');
    } else if (countryval == 'PF') {
      return ($(this).text() == 'French Polynesia');
    } else if (countryval == 'TF') {
      return ($(this).text() == 'French Southern and Antarctic Lands');
    } else if (countryval == 'GA') {
      return ($(this).text() == 'Gabon');
    } else if (countryval == 'GM') {
      return ($(this).text() == 'Gambia');
    } else if (countryval == 'GE') {
      return ($(this).text() == 'Georgia');
    } else if (countryval == 'DE') {
      return ($(this).text() == 'Germany');
    } else if (countryval == 'GH') {
      return ($(this).text() == 'Ghana');
    } else if (countryval == 'GI') {
      return ($(this).text() == 'Gibraltar');
    } else if (countryval == 'GR') {
      return ($(this).text() == 'Greece');
    } else if (countryval == 'GL') {
      return ($(this).text() == 'Greenland');
    } else if (countryval == 'GD') {
      return ($(this).text() == 'Grenada');
    } else if (countryval == 'GP') {
      return ($(this).text() == 'Guadeloupe');
    } else if (countryval == 'GU') {
      return ($(this).text() == 'Guam');
    } else if (countryval == 'GT') {
      return ($(this).text() == 'Guatemala');
    } else if (countryval == 'GG') {
      return ($(this).text() == 'Guernsey');
    } else if (countryval == 'GN') {
      return ($(this).text() == 'Guinea');
    } else if (countryval == 'GW') {
      return ($(this).text() == 'Guinea-Bissau');
    } else if (countryval == 'GY') {
      return ($(this).text() == 'Guyana');
    } else if (countryval == 'HT') {
      return ($(this).text() == 'Haiti');
    } else if (countryval == 'HM') {
      return ($(this).text() == 'Heard Island & McDonald Islands');
    } else if (countryval == 'HN') {
      return ($(this).text() == 'Honduras');
    } else if (countryval == 'HK') {
      return ($(this).text() == 'Hong Kong');
    } else if (countryval == 'HU') {
      return ($(this).text() == 'Hungary');
    } else if (countryval == 'IS') {
      return ($(this).text() == 'Iceland');
    } else if (countryval == 'IN') {
      return ($(this).text() == 'India');
    } else if (countryval == 'ID') {
      return ($(this).text() == 'Indonesia');
    } else if (countryval == 'IR') {
      return ($(this).text() == 'Iran');
    } else if (countryval == 'IQ') {
      return ($(this).text() == 'Iraq');
    } else if (countryval == 'IE') {
      return ($(this).text() == 'Ireland');
    } else if (countryval == 'IL') {
      return ($(this).text() == 'Israel');
    } else if (countryval == 'IT') {
      return ($(this).text() == 'Italy');
    } else if (countryval == 'JM') {
      return ($(this).text() == 'Jamaica');
    } else if (countryval == 'JP') {
      return ($(this).text() == 'Japan');
    } else if (countryval == 'JE') {
      return ($(this).text() == 'Jersey');
    } else if (countryval == 'JO') {
      return ($(this).text() == 'Jordan');
    } else if (countryval == 'KZ') {
      return ($(this).text() == 'Kazakhstan');
    } else if (countryval == 'KE') {
      return ($(this).text() == 'Kenya');
    } else if (countryval == 'KI') {
      return ($(this).text() == 'Kiribati');
    } else if (countryval == 'KW') {
      return ($(this).text() == 'Kuwait');
    } else if (countryval == 'KG') {
      return ($(this).text() == 'Kyrgyzstan');
    } else if (countryval == 'LA') {
      return ($(this).text() == 'Laos');
    } else if (countryval == 'LV') {
      return ($(this).text() == 'Latvia');
    } else if (countryval == 'LB') {
      return ($(this).text() == 'Lebanon');
    } else if (countryval == 'LS') {
      return ($(this).text() == 'Lesotho');
    } else if (countryval == 'LR') {
      return ($(this).text() == 'Liberia');
    } else if (countryval == 'LY') {
      return ($(this).text() == 'Libya');
    } else if (countryval == 'LI') {
      return ($(this).text() == 'Liechtenstein');
    } else if (countryval == 'LT') {
      return ($(this).text() == 'Lithuania');
    } else if (countryval == 'LU') {
      return ($(this).text() == 'Luxembourg');
    } else if (countryval == 'MO') {
      return ($(this).text() == 'Macau, China');
    } else if (countryval == 'MK') {
      return ($(this).text() == 'Macedonia');
    } else if (countryval == 'MG') {
      return ($(this).text() == 'Madagascar');
    } else if (countryval == 'MW') {
      return ($(this).text() == 'Malawi');
    } else if (countryval == 'MY') {
      return ($(this).text() == 'Malaysia');
    } else if (countryval == 'MV') {
      return ($(this).text() == 'Maldives');
    } else if (countryval == 'ML') {
      return ($(this).text() == 'Mali');
    } else if (countryval == 'MT') {
      return ($(this).text() == 'Malta');
    } else if (countryval == 'MH') {
      return ($(this).text() == 'Marshall Islands');
    } else if (countryval == 'MQ') {
      return ($(this).text() == 'Martinique');
    } else if (countryval == 'MR') {
      return ($(this).text() == 'Mauritania');
    } else if (countryval == 'MU') {
      return ($(this).text() == 'Mauritius');
    } else if (countryval == 'YT') {
      return ($(this).text() == 'Mayotte');
    } else if (countryval == 'MX') {
      return ($(this).text() == 'Mexico');
    } else if (countryval == 'FM') {
      return ($(this).text() == 'Micronesia, Federated States of');
    } else if (countryval == 'MD') {
      return ($(this).text() == 'Moldova');
    } else if (countryval == 'MC') {
      return ($(this).text() == 'Monaco');
    } else if (countryval == 'MN') {
      return ($(this).text() == 'Mongolia');
    } else if (countryval == 'ME') {
      return ($(this).text() == 'Montenegro');
    } else if (countryval == 'MS') {
      return ($(this).text() == 'Montserrat');
    } else if (countryval == 'MA') {
      return ($(this).text() == 'Morocco');
    } else if (countryval == 'MZ') {
      return ($(this).text() == 'Mozambique');
    } else if (countryval == 'MM') {
      return ($(this).text() == 'Myanmar');
    } else if (countryval == 'NA') {
      return ($(this).text() == 'Namibia');
    } else if (countryval == 'NR') {
      return ($(this).text() == 'Nauru');
    } else if (countryval == 'NP') {
      return ($(this).text() == 'Nepal');
    } else if (countryval == 'AN') {
      return ($(this).text() == 'Netherlands Antilles');
    } else if (countryval == 'NL') {
      return ($(this).text() == 'Netherlands');
    } else if (countryval == 'NC') {
      return ($(this).text() == 'New Caledonia');
    } else if (countryval == 'NZ') {
      return ($(this).text() == 'New Zealand');
    } else if (countryval == 'NI') {
      return ($(this).text() == 'Nicaragua');
    } else if (countryval == 'NE') {
      return ($(this).text() == 'Niger');
    } else if (countryval == 'NG') {
      return ($(this).text() == 'Nigeria');
    } else if (countryval == 'NU') {
      return ($(this).text() == 'Niue');
    } else if (countryval == 'NF') {
      return ($(this).text() == 'Norfolk Island');
    } else if (countryval == 'KP') {
      return ($(this).text() == 'North Korea');
    } else if (countryval == 'MP') {
      return ($(this).text() == 'Northern Mariana Islands');
    } else if (countryval == 'NO') {
      return ($(this).text() == 'Norway');
    } else if (countryval == 'OM') {
      return ($(this).text() == 'Oman');
    } else if (countryval == 'PK') {
      return ($(this).text() == 'Pakistan');
    } else if (countryval == 'PW') {
      return ($(this).text() == 'Palau');
    } else if (countryval == 'PS') {
      return ($(this).text() == 'Palestinian Authority');
    } else if (countryval == 'PA') {
      return ($(this).text() == 'Panama');
    } else if (countryval == 'PG') {
      return ($(this).text() == 'Papua New Guinea');
    } else if (countryval == 'PY') {
      return ($(this).text() == 'Paraguay');
    } else if (countryval == 'PE') {
      return ($(this).text() == 'Peru');
    } else if (countryval == 'PH') {
      return ($(this).text() == 'Philippines');
    } else if (countryval == 'PN') {
      return ($(this).text() == 'Pitcairn Islands');
    } else if (countryval == 'PL') {
      return ($(this).text() == 'Poland');
    } else if (countryval == 'PT') {
      return ($(this).text() == 'Portugal');
    } else if (countryval == 'PR') {
      return ($(this).text() == 'Puerto Rico');
    } else if (countryval == 'QA') {
      return ($(this).text() == 'Qatar');
    } else if (countryval == 'CG') {
      return ($(this).text() == 'Republic of the Congo');
    } else if (countryval == 'RE') {
      return ($(this).text() == 'Reunion');
    } else if (countryval == 'RO') {
      return ($(this).text() == 'Romania');
    } else if (countryval == 'RU') {
      return ($(this).text() == 'Russia');
    } else if (countryval == 'RW') {
      return ($(this).text() == 'Rwanda');
    } else if (countryval == 'BL') {
      return ($(this).text() == 'Saint Barthelemy');
    } else if (countryval == 'SH') {
      return ($(this).text() == 'Saint Helena, Ascension & Tristan daCunha');
    } else if (countryval == 'KN') {
      return ($(this).text() == 'Saint Kitts and Nevis');
    } else if (countryval == 'LC') {
      return ($(this).text() == 'Saint Lucia');
    } else if (countryval == 'MF') {
      return ($(this).text() == 'Saint Martin');
    } else if (countryval == 'PM') {
      return ($(this).text() == 'Saint Pierre and Miquelon');
    } else if (countryval == 'VC') {
      return ($(this).text() == 'Saint Vincent and Grenadines');
    } else if (countryval == 'WS') {
      return ($(this).text() == 'Samoa');
    } else if (countryval == 'SM') {
      return ($(this).text() == 'San Marino');
    } else if (countryval == 'ST') {
      return ($(this).text() == 'Sao Tome and Principe');
    } else if (countryval == 'SA') {
      return ($(this).text() == 'Saudi Arabia');
    } else if (countryval == 'SN') {
      return ($(this).text() == 'Senegal');
    } else if (countryval == 'RS') {
      return ($(this).text() == 'Serbia');
    } else if (countryval == 'SC') {
      return ($(this).text() == 'Seychelles');
    } else if (countryval == 'SL') {
      return ($(this).text() == 'Sierra Leone');
    } else if (countryval == 'SG') {
      return ($(this).text() == 'Singapore');
    } else if (countryval == 'SK') {
      return ($(this).text() == 'Slovakia');
    } else if (countryval == 'SI') {
      return ($(this).text() == 'Slovenia');
    } else if (countryval == 'SB') {
      return ($(this).text() == 'Solomon Islands');
    } else if (countryval == 'SO') {
      return ($(this).text() == 'Somalia');
    } else if (countryval == 'ZA') {
      return ($(this).text() == 'South Africa');
    } else if (countryval == 'GS') {
      return ($(this).text() == 'South Georgia & South Sandwich Islands');
    } else if (countryval == 'KR') {
      return ($(this).text() == 'South Korea');
    } else if (countryval == 'ES') {
      return ($(this).text() == 'Spain');
    } else if (countryval == 'LK') {
      return ($(this).text() == 'Sri Lanka');
    } else if (countryval == 'SD') {
      return ($(this).text() == 'Sudan');
    } else if (countryval == 'SR') {
      return ($(this).text() == 'Suriname');
    } else if (countryval == 'SJ') {
      return ($(this).text() == 'Svalbard and Jan Mayen');
    } else if (countryval == 'SZ') {
      return ($(this).text() == 'Swaziland');
    } else if (countryval == 'SE') {
      return ($(this).text() == 'Sweden');
    } else if (countryval == 'CH') {
      return ($(this).text() == 'Switzerland');
    } else if (countryval == 'SY') {
      return ($(this).text() == 'Syria');
    } else if (countryval == 'TW') {
      return ($(this).text() == 'Taiwan');
    } else if (countryval == 'TJ') {
      return ($(this).text() == 'Tajikistan');
    } else if (countryval == 'TZ') {
      return ($(this).text() == 'Tanzania');
    } else if (countryval == 'TH') {
      return ($(this).text() == 'Thailand');
    } else if (countryval == 'TL') {
      return ($(this).text() == 'Timor-Leste');
    } else if (countryval == 'TG') {
      return ($(this).text() == 'Togo');
    } else if (countryval == 'TK') {
      return ($(this).text() == 'Tokelau');
    } else if (countryval == 'TO') {
      return ($(this).text() == 'Tonga');
    } else if (countryval == 'TT') {
      return ($(this).text() == 'Trinidad and Tobago');
    } else if (countryval == 'TN') {
      return ($(this).text() == 'Tunisia');
    } else if (countryval == 'TR') {
      return ($(this).text() == 'Turkey');
    } else if (countryval == 'TM') {
      return ($(this).text() == 'Turkmenistan');
    } else if (countryval == 'TC') {
      return ($(this).text() == 'Turks and Caicos Islands');
    } else if (countryval == 'TV') {
      return ($(this).text() == 'Tuvalu');
    } else if (countryval == 'UG') {
      return ($(this).text() == 'Uganda');
    } else if (countryval == 'UA') {
      return ($(this).text() == 'Ukraine');
    } else if (countryval == 'AE') {
      return ($(this).text() == 'United Arab Emirates');
    } else if (countryval == 'VI') {
      return ($(this).text() == 'United States Virgin Islands');
    } else if (countryval == 'UY') {
      return ($(this).text() == 'Uruguay');
    } else if (countryval == 'UZ') {
      return ($(this).text() == 'Uzbekistan');
    } else if (countryval == 'VU') {
      return ($(this).text() == 'Vanuatu');
    } else if (countryval == 'VA') {
      return ($(this).text() == 'Vatican City');
    } else if (countryval == 'VE') {
      return ($(this).text() == 'Venezuela');
    } else if (countryval == 'VN') {
      return ($(this).text() == 'Vietnam');
    } else if (countryval == 'WF') {
      return ($(this).text() == 'Wallis and Futuna');
    } else if (countryval == 'EH') {
      return ($(this).text() == 'Western Sahara');
    } else if (countryval == 'YE') {
      return ($(this).text() == 'Yemen');
    } else if (countryval == 'ZM') {
      return ($(this).text() == 'Zambia');
    } else if (countryval == 'ZW') {
      return ($(this).text() == 'Zimbabwe');
    } else {
      return ($(this).text() == 'Select Country');
    }
  }).prop('selected', true);
</script>