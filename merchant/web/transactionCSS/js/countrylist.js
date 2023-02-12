$(function() {
    var availableTags = ["Afghanistan","Aland Islands","Albania","Algeria","American Samoa","Andorra","Angola","Anguilla","Antarctica","Antigua and Barbuda","Argentina","Armenia","Aruba","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bermuda","Bhutan","Bolivia","Bosnia and Herzegovina","Botswana","Bouvet Island","Brazil","British Indian Ocean Territory","British Virgin Islands","Brunei","Bulgaria","Burkina Faso","Burundi","Cambodia","Cameroon","Canada","Cape Verde","Cayman Islands","Central African Republic","Chad","Chile","China","Christmas Island","Cocos (Keeling) Islands","Colombia","Comoros","Cook Islands","Costa Rica","Cote d'Ivoire","Croatia","Cuba","Cyprus","Czech Republic","Democratic Republic of the Congo","Denmark","Djibouti","Dominica","Dominican Republic","Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea","Estonia","Ethiopia","Falkland Islands","Faroe Islands","Fiji","Finland","France","French Guiana","French Polynesia","French Southern and Antarctic Lands","Gabon","Gambia","Georgia","Germany","Ghana","Gibraltar","Greece","Greenland","Grenada","Guadeloupe","Guam","Guatemala","Guernsey","Guinea","Guinea-Bissau","Guyana","Haiti","Heard Island & McDonald Islands","Honduras","Hong Kong","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Israel","Italy","Jamaica","Japan","Jersey","Jordan","Kazakhstan","Kenya","Kiribati","Kuwait","Kyrgyzstan","Laos","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macau, China","Macedonia","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Marshall Islands","Martinique","Mauritania","Mauritius","Mayotte","Mexico","Micronesia, Federated States of","Moldova","Monaco","Mongolia","Montenegro","Montserrat","Morocco","Mozambique","Myanmar","Namibia","Nauru","Nepal","Netherlands Antilles","Netherlands","New Caledonia","New Zealand","Nicaragua","Niger","Nigeria","Niue","Norfolk Island","North Korea","Northern Mariana Islands","Norway","Oman","Pakistan","Palau","Palestinian Authority","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Pitcairn Islands","Poland","Portugal","Puerto Rico","Qatar","Republic of the Congo","Reunion","Romania","Russia","Rwanda","Saint Barthelemy","Saint Helena, Ascension & Tristan daCunha","Saint Kitts and Nevis","Saint Lucia","Saint Martin","Saint Pierre and Miquelon","Saint Vincent and Grenadines","Samoa","San Marino","Sao Tome and Principe","Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Slovakia","Slovenia","Solomon Islands","Somalia","South Africa","South Georgia & South Sandwich Islands","South Korea","Spain","Sri Lanka","Sudan","Suriname","Svalbard and Jan Mayen","Swaziland","Sweden","Switzerland","Syria","Taiwan","Tajikistan","Tanzania","Thailand","Timor-Leste","Togo","Tokelau","Tonga","Trinidad and Tobago","Tunisia","Turkey","Turkmenistan","Turks and Caicos Islands","Tuvalu","Uganda","Ukraine","United Arab Emirates","United Kingdom","United States","United States Virgin Islands","Uruguay","Uzbekistan","Vanuatu","Vatican City","Venezuela","Vietnam","Wallis and Futuna","Western Sahara","Yemen","Zambia","Zimbabwe"
    ];

    $( "#country_input_optional").autocomplete({
        source: availableTags
    });
    $( "#country_input").autocomplete({
        source: availableTags
    });
    $( "#country_input2" ).autocomplete({
        source: availableTags
    });
    $( "#country_input3" ).autocomplete({
        source: availableTags
    });
    $( "#country_input4" ).autocomplete({
        source: availableTags
    });
    $( "#country_input5" ).autocomplete({
        source: availableTags
    });
    $( "#country_input_cs" ).autocomplete({
        source: availableTags
    });
    $( "#country_input_optionaldc").autocomplete({
        source: availableTags
    });
    $( "#country_inputdc").autocomplete({
        source: availableTags
    });
    $( "#country_input_ACH").autocomplete({
        source: availableTags
    });
    $( "#country_input_CHK").autocomplete({
        source: availableTags
    });
    $( "#country_input_DP").autocomplete({
        source: availableTags
    });
    $( "#country_input_db").autocomplete({
        source: availableTags
    });
    $( "#country_input_tojika").autocomplete({
        source: availableTags
    });
    $( "#country_input_cu").autocomplete({ // CupUpi
        source: availableTags
    });
    $( "#country_input_EP").autocomplete({ // EzPay
        source: availableTags
    });
    $( "#country_input_zp").autocomplete({ // ZOTA
        source: availableTags
    });
    $( "#country_input_telecash").autocomplete({ // TELECASH
        source: availableTags
    });
    $( "#country_input_ecocash").autocomplete({ // ECOCASH
        source: availableTags
    });
    $( "#country_input_telecash_MB").autocomplete({ // TELECASH
        source: availableTags
    });
    $( "#country_input_ecocash_MB").autocomplete({ // ECOCASH
        source: availableTags
    });
});