/**
 * Created by Nikhil Poojari on 07-Apr-18.
 */

// Countries

//JSON for English language.
var all_english={
     "Aland Islands": '/merchant/images/flags/Aland_Islands.png',
     "Albania": '/merchant/images/flags/Albania.png',
     "Algeria": '/merchant/images/flags/Algeria.png',
     "American Samoa": '/merchant/images/flags/American_Samoa.png',
     "Andorra": '/merchant/images/flags/Andorra.png',
     "Angola": '/merchant/images/flags/Angola.png',
     "Anguilla": '/merchant/images/flags/Anguilla.png',
     "Antarctica": '/merchant/images/flags/Antarctica.png',
     "Antigua and Barbuda": '/merchant/images/flags/Antigua_and_Barbuda.png',
     "Argentina": '/merchant/images/flags/Argentina.png',
     "Armenia": '/merchant/images/flags/Armenia.png',
     "Aruba": '/merchant/images/flags/Aruba.png',
     "Australia": '/merchant/images/flags/Australia.png',
     "Austria": '/merchant/images/flags/Austria.png',
     "Azerbaijan": '/merchant/images/flags/Azerbaijan.png',
     "Bahrain": '/merchant/images/flags/Bahrain.png',
     "Bangladesh": '/merchant/images/flags/Bangladesh.png',
     "Barbados": '/merchant/images/flags/Barbados.png',
     "Belgium": '/merchant/images/flags/Belgium.png',
     "Belize": '/merchant/images/flags/Belize.png',
     "Benin": '/merchant/images/flags/Benin.png',
     "Bermuda": '/merchant/images/flags/Bermuda.png',
     "Bhutan": '/merchant/images/flags/Bhutan.png',
     "Bolivia": '/merchant/images/flags/Bolivia.png',
     "Bosnia and Herzegovina": '/merchant/images/flags/Bosnia_and_Herzegovina.png',
     "Bouvet Island": '/merchant/images/flags/Bouvet_Island.png',
     "Brazil": '/merchant/images/flags/Brazil.png',
     "British Indian Ocean Territory": '/merchant/images/flags/British_Indian_Ocean_Territory.png',
     "British Virgin Islands": '/merchant/images/flags/British_Virgin_Islands.png',
     "Brunei": '/merchant/images/flags/Brunei.png',
     "Bulgaria": '/merchant/images/flags/Bulgaria.png',
     "Burkina Faso": '/merchant/images/flags/Burkina_Faso.png',
     "Cambodia": '/merchant/images/flags/Cambodia.png',
     "Cameroon": '/merchant/images/flags/Cameroon.png',
     "Canada": '/merchant/images/flags/Canada.png',
     "Cape Verde": '/merchant/images/flags/Cape_Verde.png',
     "Cayman Islands": '/merchant/images/flags/Cayman_Islands.png',
     "Chad": '/merchant/images/flags/Chad.png',
     "Chile": '/merchant/images/flags/Chile.png',
     "China": '/merchant/images/flags/China.png',
     "Christmas Island": '/merchant/images/flags/Christmas_Island.png',
     "Cocos (Keeling) Islands": '/merchant/images/flags/Cocos_Islands.png',
     "Colombia": '/merchant/images/flags/Colombia.png',
     "Comoros": '/merchant/images/flags/Comoros.png',
     "Cook Islands": '/merchant/images/flags/Cook_Islands.png',
     "Costa Rica": '/merchant/images/flags/Costa_Rica.png',
     "Cote d` Ivoire": '/merchant/images/flags/Cote_d`_Ivoire.png',
     "Croatia": '/merchant/images/flags/Croatia.png',
     "Cyprus": '/merchant/images/flags/Cyprus.png',
     "Czech Republic": '/merchant/images/flags/Czech_Republic.png',
     "Denmark": '/merchant/images/flags/Denmark.png',
     "Djibouti": '/merchant/images/flags/Djibouti.png',
     "Dominica": '/merchant/images/flags/Dominica.png',
     "Dominican Republic": '/merchant/images/flags/Dominican_Republic.png',
     "Ecuador": '/merchant/images/flags/Ecuador.png',
     "Egypt": '/merchant/images/flags/Egypt.png',
     "El Salvador": '/merchant/images/flags/El_Salvador.png',
     "Equatorial Guinea": '/merchant/images/flags/Equatorial_Guinea.png',
     "Eritrea": '/merchant/images/flags/Eritrea.png',
     "Estonia": '/merchant/images/flags/Estonia.png',
     "England": '/merchant/images/flags/United_Kingdom.png',
     "Falkland Islands": '/merchant/images/flags/Falkland_Islands.png',
     "Faroe Islands": '/merchant/images/flags/Faroe_Islands.png',
     "Federated States of Micronesia": '/merchant/images/flags/Federated_States_of_Micronesia.png',
     "Fiji": '/merchant/images/flags/Fiji.png',
     "Finland": '/merchant/images/flags/Finland.png',
     "France": '/merchant/images/flags/France.png',
     "French Guiana": '/merchant/images/flags/French_Guiana.png',
     "French Polynesia": '/merchant/images/flags/French_Polynesia.png',
     "French Southern and Antarctic Lands": '/merchant/images/flags/French_Southern_and_Antarctic_Lands.png',
     "Gabon": '/merchant/images/flags/Gabon.png',
     "Gambia": '/merchant/images/flags/Gambia.png',
     "Georgia": '/merchant/images/flags/Georgia.png',
     "Germany": '/merchant/images/flags/Germany.png',
     "Gibraltar": '/merchant/images/flags/Gibraltar.png',
     "Greece": '/merchant/images/flags/Greece.png',
     "Greenland": '/merchant/images/flags/Greenland.png',
     "Grenada": '/merchant/images/flags/Grenada.png',
     "Guadeloupe": '/merchant/images/flags/Guadeloupe.png',
     "Guam": '/merchant/images/flags/Guam.png',
     "Guatemala": '/merchant/images/flags/Guatemala.png',
     "Guernsey": '/merchant/images/flags/Guernsey.png',
     "Guinea": '/merchant/images/flags/Guinea.png',
     "Guinea-Bissau": '/merchant/images/flags/Guinea_Bissau.png',
     "Guyana": '/merchant/images/flags/Guyana.png',
     "Haiti": '/merchant/images/flags/Haiti.png',
     "Heard Island & McDonald Islands": '/merchant/images/flags/Heard_Island_and_McDonald_Islands.png',
     "Honduras": '/merchant/images/flags/Honduras.png',
     "Hong Kong": '/merchant/images/flags/Hong_Kong.png',
     "Hungary": '/merchant/images/flags/Hungary.png',
     "Iceland": '/merchant/images/flags/Iceland.png',
     "India": '/merchant/images/flags/India.png',
     "Indonesia": '/merchant/images/flags/Indonesia.png',
     "Ireland": '/merchant/images/flags/Ireland.png',
     "Israel": '/merchant/images/flags/Israel.png',
     "Italy": '/merchant/images/flags/Italy.png',
     "Jamaica": '/merchant/images/flags/Jamaica.png',
     "Japan": '/merchant/images/flags/Japan.png',
     "Jersey": '/merchant/images/flags/Jersey.png',
     "Jordan": '/merchant/images/flags/Jordan.png',
     "Kazakhstan": '/merchant/images/flags/Kazakhstan.png',
     "Kenya": '/merchant/images/flags/Kenya.png',
     "Kiribati": '/merchant/images/flags/Kiribati.png',
     "Kuwait": '/merchant/images/flags/Kuwait.png',
     "Kyrgyzstan": '/merchant/images/flags/Kyrgyzstan.png',
     "Laos": '/merchant/images/flags/Laos.png',
     "Latvia": '/merchant/images/flags/Latvia.png',
     "Lesotho": '/merchant/images/flags/Lesotho.png',
     "Liberia": '/merchant/images/flags/Liberia.png',
     "Liechtenstein": '/merchant/images/flags/Liechtenstein.png',
     "Lithuania": '/merchant/images/flags/Lithuania.png',
     "Luxembourg": '/merchant/images/flags/Luxembourg.png',
     "Macau, China": '/merchant/images/flags/Macau.png',
     "Macedonia": '/merchant/images/flags/Macedonia.png',
     "Madagascar": '/merchant/images/flags/Madagascar.png',
     "Malawi": '/merchant/images/flags/Malawi.png',
     "Malaysia": '/merchant/images/flags/Malaysia.png',
     "Maldives": '/merchant/images/flags/Maldives.png',
     "Mali": '/merchant/images/flags/Mali.png',
     "Malta": '/merchant/images/flags/Malta.png',
     "Marshall Islands": '/merchant/images/flags/Marshall_Islands.png',
     "Martinique": '/merchant/images/flags/Martinique.png',
     "Mauritania": '/merchant/images/flags/Mauritania.png',
     "Mauritius": '/merchant/images/flags/Mauritius.png',
     "Mayotte": '/merchant/images/flags/Mayotte.png',
     "Mexico": '/merchant/images/flags/Mexico.png',
     "Moldova": '/merchant/images/flags/Moldova.png',
     "Monaco": '/merchant/images/flags/Monaco.png',
     "Mongolia": '/merchant/images/flags/Mongolia.png',
     "Montenegro": '/merchant/images/flags/Montenegro.png',
     "Montserrat": '/merchant/images/flags/Montserrat.png',
     "Morocco": '/merchant/images/flags/Morocco.png',
     "Mozambique": '/merchant/images/flags/Mozambique.png',
     "Myanmar": '/merchant/images/flags/Myanmar.png',
     "Namibia": '/merchant/images/flags/Namibia.png',
     "Nauru": '/merchant/images/flags/Nauru.png',
     "Nepal": '/merchant/images/flags/Nepal.png',
     "Netherlands Antilles": '/merchant/images/flags/Netherlands_Antilles.png',
     "Netherlands": '/merchant/images/flags/Netherlands.png',
     "New Caledonia": '/merchant/images/flags/New_Caledonia.png',
     "New Zealand": '/merchant/images/flags/New_Zealand.png',
     "Niger": '/merchant/images/flags/Niger.png',
     "Nigeria": '/merchant/images/flags/Nigeria.png',
     "Niue": '/merchant/images/flags/Niue.png',
     "Norfolk Island": '/merchant/images/flags/Norfolk_Island.png',
     "Northern Mariana Islands": '/merchant/images/flags/Northern_Mariana_Islands.png',
     "Norway": '/merchant/images/flags/Norway.png',
     "Northern Ireland": '/merchant/images/flags/United_Kingdom.png',
     "Oman": '/merchant/images/flags/Oman.png',
     "Pakistan": '/merchant/images/flags/Pakistan.png',
     "Palau": '/merchant/images/flags/Palau.png',
     "Palestinian Authority": '/merchant/images/flags/Palestinian_Authority.png',
     "Panama": '/merchant/images/flags/Panama.png',
     "Papua New Guinea": '/merchant/images/flags/Papua_New_Guinea.png',
     "Paraguay": '/merchant/images/flags/Paraguay.png',
     "Peru": '/merchant/images/flags/Peru.png',
     "Philippines": '/merchant/images/flags/Philippines.png',
     "Pitcairn Islands": '/merchant/images/flags/Pitcairn_Islands.png',
     "Poland": '/merchant/images/flags/Poland.png',
     "Portugal": '/merchant/images/flags/Portugal.png',
     "Puerto Rico": '/merchant/images/flags/Puerto_Rico.png',
     "Qatar": '/merchant/images/flags/Qatar.png',
     "Republic of the Congo": '/merchant/images/flags/Republic_of_the_Congo.png',
     "Reunion": '/merchant/images/flags/Reunion.png',
     "Romania": '/merchant/images/flags/Romania.png',
     "Russia": '/merchant/images/flags/Russia.png',
     "Rwanda": '/merchant/images/flags/Rwanda.png',
     "Saint Barthelemy": '/merchant/images/flags/Saint_Barthelemy.png',
     "Saint Helena, Ascension & Tristan daCunha": '/merchant/images/flags/Saint_Helena_Ascension_and_Tristan_daCunha.png',
     "Saint Kitts and Nevis": '/merchant/images/flags/Saint_Kitts_and_Nevis.png',
     "Saint Lucia": '/merchant/images/flags/Saint_Lucia.png',
     "Saint Martin": '/merchant/images/flags/Saint_Martin.png',
     "Saint Pierre and Miquelon": '/merchant/images/flags/Saint_Pierre_and_Miquelon.png',
     "Saint Vincent and Grenadines": '/merchant/images/flags/Saint_Vincent_and_Grenadines.png',
     "Samoa": '/merchant/images/flags/Samoa.png',
     "San Marino": '/merchant/images/flags/San_Marino.png',
     "Sao Tome and Principe": '/merchant/images/flags/Sao_Tome_and_Principe.png',
     "Saudi Arabia": '/merchant/images/flags/Saudi_Arabia.png',
     "Scotland": '/merchant/images/flags/United_Kingdom.png',
     "Senegal": '/merchant/images/flags/Senegal.png',
     "Serbia": '/merchant/images/flags/Serbia.png',
     "Seychelles": '/merchant/images/flags/Seychelles.png',
     "Sierra Leone": '/merchant/images/flags/Sierra_Leone.png',
     "Singapore": '/merchant/images/flags/Singapore.png',
     "Slovakia": '/merchant/images/flags/Slovakia.png',
     "Slovenia": '/merchant/images/flags/Slovenia.png',
     "Solomon Islands": '/merchant/images/flags/Solomon_Islands.png',
     "South Africa": '/merchant/images/flags/South_Africa.png',
     "South Georgia & South Sandwich Islands": '/merchant/images/flags/South_Georgia_and_South_Sandwich_Islands.png',
     "South Korea": '/merchant/images/flags/South_Korea.png',
     "Spain": '/merchant/images/flags/Spain.png',
     "Sri Lanka": '/merchant/images/flags/Sri_Lanka.png',
     "Suriname": '/merchant/images/flags/Suriname.png',
     "Svalbard and Jan Mayen": '/merchant/images/flags/Svalbard_and_Jan_Mayen.png',
     "Swaziland": '/merchant/images/flags/Swaziland.png',
     "Sweden": '/merchant/images/flags/Sweden.png',
     "Switzerland": '/merchant/images/flags/Switzerland.png',
     "Taiwan": '/merchant/images/flags/Taiwan.png',
     "Tajikistan": '/merchant/images/flags/Tajikistan.png',
     "Tanzania": '/merchant/images/flags/Tanzania.png',
     "Thailand": '/merchant/images/flags/Thailand.png',
     "Timor-Leste": '/merchant/images/flags/Timor_Leste.png',
     "Togo": '/merchant/images/flags/Togo.png',
     "Tokelau": '/merchant/images/flags/Tokelau.png',
     "Tonga": '/merchant/images/flags/Tonga.png',
     "Turkey": '/merchant/images/flags/Turkey.png',
     "Turkmenistan": '/merchant/images/flags/Turkmenistan.png',
     "Turks and Caicos Islands": '/merchant/images/flags/Turks_and_Caicos_Islands.png',
     "Tuvalu": '/merchant/images/flags/Tuvalu.png',
     "Uganda": '/merchant/images/flags/Uganda.png',
     "United Arab Emirates": '/merchant/images/flags/United_Arab_Emirates.png',
     "United Kingdom": '/merchant/images/flags/United_Kingdom.png',
     "United States": '/merchant/images/flags/United_States.png',
     "United States Virgin Islands": '/merchant/images/flags/United_States_Virgin_Islands.png',
     "Uruguay": '/merchant/images/flags/Uruguay.png',
     "Uzbekistan": '/merchant/images/flags/Uzbekistan.png',
     "Vanuatu": '/merchant/images/flags/Vanuatu.png',
     "Vatican City": '/merchant/images/flags/Vatican_City.png',
     "Vietnam": '/merchant/images/flags/Vietnam.png',
     "Wallis and Futuna": '/merchant/images/flags/Wallis_and_Futuna.png',
     "Wales": '/merchant/images/flags/United_Kingdom.png',
     "Western Sahara": '/merchant/images/flags/Western_Sahara.png',
     "Zambia": '/merchant/images/flags/Zambia.png'
    };


$(function() {
    $('input.country').autocomplete({

        data: all_english
    });
});








