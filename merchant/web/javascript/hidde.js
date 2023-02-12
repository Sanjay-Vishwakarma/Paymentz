        function bodyonload(){
            var table = $("#myTable").find("tr");
             console.log(table)

            //var td =$("#myTable").find("tr").find("td");
            for( var i=0; i <table.length ;i++){
                console.log("in bodyload",document.getElementById((table[i].getElementsByTagName("td")[2].getElementsByTagName("input")[1].id)+"_hidden").value)
                loadhideemail(table[i].getElementsByTagName("td")[2].getElementsByTagName("input")[1].id)
                loadhideemail(table[i].getElementsByTagName("td")[3].getElementsByTagName("input")[1].id)
                loadhideemail(table[i].getElementsByTagName("td")[4].getElementsByTagName("input")[1].id)
                loadphonehide(table[i].getElementsByTagName("td")[5].getElementsByTagName("input")[1].id)
                hideMerchantKey(document.getElementById("merchant_key").id);
            }



            // add a class with the name  hidedivemail / hidedivphone  and id to any tag .

            for(var j = 0 ; j < $(".hidedivemail").length ; j++){
                loadhideemail($(".hidedivemail")[j].id)
            }
            for(var j = 0 ; j < $(".hidedivphone").length ; j++){
                loadphonehide($(".hidedivphone")[j].id)
            }

        }

        function loadhideemail(id){
            if(document.getElementById(id+"_hidden"))
            {
                if(!document.getElementById(id).value){
                    $("#" + id).parent().find('span').remove('span')
                }
                var original_email = document.getElementById(id +"_hidden").value;
                var email ;
                if(document.getElementById(id).value){
                  email =   document.getElementById(id).value;
                }
                else{
                  email   = document.getElementById(id).innerText;
                }

                var hiddenEmail = "";
                var i;
                for (i = 0; i < email.length; i++)
                {
                    if (i > 1 && i < email.indexOf("@"))
                    {
                        if (email[i] != '.' && email[i] != '-' && email[i] != '_')
                        {
                            hiddenEmail += "x";
                        }
                        else
                        {
                            hiddenEmail += email[i];
                        }
                    }
                    else
                    {
                        hiddenEmail += email[i];
                    }
                }
                document.getElementById(id).value = hiddenEmail;
            }
        }

        function loadphonehide(id){
            if(document.getElementById(id+"_hidden"))
            {
                if(!document.getElementById(id).value){
                    $("#" + id).parent().find('span').remove('span')
                }
                var original_phone = document.getElementById(id + "_hidden").value;
                var phoneno = document.getElementById(id).value;
                var hiddenphoneno = "";
                var i;
                for (i = 0; i < phoneno.length; i++)
                {
                    if (i < (phoneno.length - 3 ))
                    {
                        if (phoneno[i] != '.' && phoneno[i] != '-' && phoneno[i] != '_')
                        {
                            hiddenphoneno += "x";
                        }
                        else
                        {
                            hiddenphoneno += phoneno[i];
                        }
                    }
                    else
                    {
                        hiddenphoneno += phoneno[i];
                    }
                }
                document.getElementById(id).value = hiddenphoneno;
            }
        }

        function hideemail(id)
        {
            if(document.getElementById(id+"_hidden").value)
            {
                var original_email = document.getElementById(id + "_hidden").value;
                console.log($("#" + id))
                if ($("#" + id).parent().find('span').hasClass('fa-eye'))
                {
                    console.log("in toggle if")
                    $("#" + id).parent().find('span').removeClass('fa-eye').addClass('fa-eye-slash');
                    if(document.getElementById(id).value){
                        email =   document.getElementById(id).value;
                    }
                    else{
                        email   = document.getElementById(id).innerText;
                    }
                    var hiddenEmail = "";
                    var i;
                    for (i = 0; i < email.length; i++)
                    {
                        if (i > 1 && i < email.indexOf("@"))
                        {
                            if (email[i] != '.' && email[i] != '-' && email[i] != '_')
                            {
                                hiddenEmail += "x";
                            }
                            else
                            {
                                hiddenEmail += email[i];
                            }
                        }
                        else
                        {
                            hiddenEmail += email[i];
                        }
                    }
                    document.getElementById(id).value = hiddenEmail;
                }
                else
                {
                    console.log("in toggle else");
                    $("#" + id).parent().find('span').removeClass('fa-eye-slash').addClass('fa-eye');
                    document.getElementById(id).value = original_email;
                }
            }
        }
        var c = 0;
        function hidePhone(id){
            if(document.getElementById(id+"_hidden"))
            {
                var original_phone = document.getElementById(id +"_hidden").value;
                if ($("#" + id).parent().find('span').hasClass('fa-eye'))
                {
                    c = 1;
                    $("#" + id).parent().find('span').removeClass('fa-eye').addClass('fa-eye-slash');
                    var phoneno = document.getElementById(id).value;
                    var hiddenphoneno = "";
                    var i;
                    for (i = 0; i < phoneno.length; i++)
                    {
                        if (i < (phoneno.length - 3 ))
                        {
                            if (phoneno[i] != '.' && phoneno[i] != '-' && phoneno[i] != '_')
                            {
                                hiddenphoneno += "x";
                            }
                            else
                            {
                                hiddenphoneno += phoneno[i];
                            }
                        }
                        else
                        {
                            hiddenphoneno += phoneno[i];
                        }
                    }
                    document.getElementById(id).value = hiddenphoneno;
                }
                else
                {
                    $("#" + id).parent().find('span').removeClass('fa-eye-slash').addClass('fa-eye');
                    document.getElementById(id).value = original_phone;
                    c = 0;
                }
            }
        }

        function setvalue(id){
            document.getElementById(id+"_hidden").value = document.getElementById(id).value
        }

        function hideshowpass(spanid,inputid)
        {
            var x = document.getElementById(inputid);
            if (x.type === "password")
            {
                $("#"+spanid).removeClass('fa-eye-slash').addClass('fa-eye')
                x.type = "text";
            }
            else
            {
                $("#"+spanid).removeClass('fa-eye').addClass('fa-eye-slash')
                x.type = "password";
            }
        }


        function mouseoverPass(spanid,inputid) {
            var x = document.getElementById(inputid);
            if (x.type === "password")
            {
                $("#"+spanid).removeClass('fa-eye-slash').addClass('fa-eye')
                x.type = "text";
            }
        }
        function mouseoutPass(spanid,inputid) {
            var x = document.getElementById(inputid);
            if (x.type === "text")
            {
                $("#"+spanid).removeClass('fa-eye').addClass('fa-eye-slash')
                x.type = "password";
            }
        }

        function showicon(inputid,spanicon){
            if(document.getElementById(inputid).value)
            {
                $("#" + spanicon).removeClass("hide")
            }
            else{
                $("#" + spanicon).addClass("hide")
            }
        }

        function decrypt(spanid,inputid,msg) {
            var x = document.getElementById(inputid);
            $("#" + spanid).removeClass('fa-eye-slash').addClass('fa-eye')
            x.value = msg;

        }
        function encrypt(spanid,inputid,msg) {
            var x = document.getElementById(inputid);
            $("#"+spanid).removeClass('fa-eye').addClass('fa-eye-slash')
            x.value = msg;
        }


        function hideMerchantKey(id){
            if(document.getElementById(id+"_hidden"))
            {
                if ($("#" + id).parent().find('span').hasClass('fa-eye'))
                {
                    $("#" + id).parent().find('span').removeClass('fa-eye').addClass('fa-eye-slash');
                    var merchantKey = document.getElementById(id).value;
                    var hiddenMerchantKey = "";
                    var i;
                    for (i = 0; i < merchantKey.length; i++)
                    {
                        hiddenMerchantKey += "x";
                    }
                    document.getElementById(id).value = hiddenMerchantKey;
                }
                else
                {
                    $("#" + id).parent().find('span').removeClass('fa-eye-slash').addClass('fa-eye');
                    document.getElementById(id).value = document.getElementById(id +"_hidden").value;
                }
            }
        }
        //JSON for English language.
        var languageCountry;
        $(function() {
                languageCountry = {
                    "Aland Islands":'Aland Islands',
                    "Albania":'Albania',
                    "Algeria":'Algeria',
                    "American Samoa":'American Samoa',
                    "Andorra":'Andorra',
                    "Angola":'Angola',
                    "Anguilla":'Anguilla',
                    "Antarctica":'Antarctica',
                    "Antigua and Barbuda":'Antigua and Barbuda',
                    "Argentina":'Argentina',
                    "Armenia":'Armenia',
                    "Aruba":'Aruba',
                    "Australia":'Australia',
                    "Austria":'Austria',
                    "Azerbaijan":'Azerbaijan',
                    "Bahrain":'Bahrain',
                    "Bangladesh":'Bangladesh',
                    "Barbados":'Barbados',
                    "Belgium":'Belgium',
                    "Belize":'Belize',
                    "Benin":'Benin',
                    "Bermuda":'Bermuda',
                    "Bhutan":'Bhutan',
                    "Bolivia":'Bolivia',
                    "Bosnia and Herzegovina":'Bosnia and Herzegovina',
                    "Bouvet Island":'Bouvet Island',
                    "Brazil":'Brazil',
                    "British Indian Ocean Territory":'British Indian Ocean Territory',
                    "British Virgin Islands":'British Virgin Islands',
                    "Brunei":'Brunei',
                    "Bulgaria":'Bulgaria',
                    "Burkina Faso":'Burkina Faso',
                    "Cambodia":'Cambodia',
                    "Cameroon":'Cameroon',
                    "Canada":'Canada',
                    "Cape Verde":'Cape Verde',
                    "Cayman Islands":'Cayman Islands',
                    "Chad":'Chad',
                    "Chile":'Chile',
                    "China":'China',
                    "Christmas Island":'Christmas Island',
                    "Cocos (Keeling) Islands":'Cocos (Keeling) Islands',
                    "Colombia":'Colombia',
                    "Comoros":'Comoros',
                    "Cook Islands":'Cook Islands',
                    "Costa Rica":'Costa Rica',
                    "Cote d` Ivoire":'Cote d` Ivoire',
                    "Croatia":'Croatia',
                    "Cyprus":'Cyprus',
                    "Czech Republic":'Czech Republic',
                    "Denmark":'Denmark',
                    "Djibouti":'Djibouti',
                    "Dominica":'Dominica',
                    "Dominican Republic":'Dominican Republic',
                    "Ecuador":'Ecuador',
                    "Egypt":'Egypt',
                    "El Salvador":'El Salvador',
                    "Equatorial Guinea":'Equatorial Guinea',
                    "Eritrea":'Eritrea',
                    "Estonia":'Estonia',
                    "England":'England',
                    "Falkland Islands":'Falkland Islands',
                    "Faroe Islands":'Faroe Islands',
                    "Federated States of Micronesia":'Federated States of Micronesia',
                    "Fiji":'Fiji',
                    "Finland":'Finland',
                    "France":'France',
                    "French Guiana":'French Guiana',
                    "French Polynesia":'French Polynesia',
                    "French Southern and Antarctic Lands":'French Southern and Antarctic Lands',
                    "Gabon":'Gabon',
                    "Gambia":'Gambia',
                    "Georgia":'Georgia',
                    "Germany":'Germany',
                    "Gibraltar":'Gibraltar',
                    "Greece":'Greece',
                    "Greenland":'Greenland',
                    "Grenada":'Grenada',
                    "Guadeloupe":'Guadeloupe',
                    "Guam":'Guam',
                    "Guatemala":'Guatemala',
                    "Guernsey":'Guernsey',
                    "Guinea":'Guinea',
                    "Guinea-Bissau":'Guinea-Bissau',
                    "Guyana":'Guyana',
                    "Haiti":'Haiti',
                    "Heard Island & McDonald Islands":'Heard Island & McDonald Islands',
                    "Honduras":'Honduras',
                    "Hong Kong":'Hong Kong',
                    "Hungary":'Hungary',
                    "Iceland":'Iceland',
                    "India":'India',
                    "Indonesia":'Indonesia',
                    "Ireland":'Ireland',
                    "Israel":'Israel',
                    "Italy":'Italy',
                    "Jamaica":'Jamaica',
                    "Japan":'Japan',
                    "Jersey":'Jersey',
                    "Jordan":'Jordan',
                    "Kazakhstan":'Kazakhstan',
                    "Kenya":'Kenya',
                    "Kiribati":'Kiribati',
                    "Kuwait":'Kuwait',
                    "Kyrgyzstan":'Kyrgyzstan',
                    "Laos":'Laos',
                    "Latvia":'Latvia',
                    "Lesotho":'Lesotho',
                    "Liberia":'Liberia',
                    "Liechtenstein":'Liechtenstein',
                    "Lithuania":'Lithuania',
                    "Luxembourg":'Luxembourg',
                    "Macau, China":'Macau, China',
                    "Macedonia":'Macedonia',
                    "Madagascar":'Madagascar',
                    "Malawi":'Malawi',
                    "Malaysia":'Malaysia',
                    "Maldives":'Maldives',
                    "Mali":'Mali',
                    "Malta":'Malta',
                    "Marshall Islands":'Marshall Islands',
                    "Martinique":'Martinique',
                    "Mauritania":'Mauritania',
                    "Mauritius":'Mauritius',
                    "Mayotte":'Mayotte',
                    "Mexico":'Mexico',
                    "Moldova":'Moldova',
                    "Monaco":'Monaco',
                    "Mongolia":'Mongolia',
                    "Montenegro":'Montenegro',
                    "Montserrat":'Montserrat',
                    "Morocco":'Morocco',
                    "Mozambique":'Mozambique',
                    "Myanmar":'Myanmar',
                    "Namibia":'Namibia',
                    "Nauru":'Nauru',
                    "Nepal":'Nepal',
                    "Netherlands Antilles":'Netherlands Antilles',
                    "Netherlands":'Netherlands',
                    "New Caledonia":'New Caledonia',
                    "New Zealand":'New Zealand',
                    "Niger":'Niger',
                    "Nigeria":'Nigeria',
                    "Niue":'Niue',
                    "Norfolk Island":'Norfolk Island',
                    "Northern Mariana Islands":'Northern Mariana Islands',
                    "Norway":'Norway',
                    "Northern Ireland":'Northern Ireland',
                    "Oman":'Oman',
                    "Pakistan":'Pakistan',
                    "Palau":'Palau',
                    "Palestinian Authority":'Palestinian Authority',
                    "Panama":'Panama',
                    "Papua New Guinea":'Papua New Guinea',
                    "Paraguay":'Paraguay',
                    "Peru":'Peru',
                    "Philippines":'Philippines',
                    "Pitcairn Islands":'Pitcairn Islands',
                    "Poland":'Poland',
                    "Portugal":'Portugal',
                    "Puerto Rico":'Puerto Rico',
                    "Qatar":'Qatar',
                    "Republic of the Congo":'Republic of the Congo',
                    "Reunion":'Reunion',
                    "Romania":'Romania',
                    "Russia":'Russia',
                    "Rwanda":'Rwanda',
                    "Saint Barthelemy":'Saint Barthelemy',
                    "Saint Helena, Ascension & Tristan daCunha":'Saint Helena, Ascension & Tristan daCunha',
                    "Saint Kitts and Nevis":'Saint Kitts and Nevis',
                    "Saint Lucia":'Saint Lucia',
                    "Saint Martin":'Saint Martin',
                    "Saint Pierre and Miquelon":'Saint Pierre and Miquelon',
                    "Saint Vincent and Grenadines":'Saint Vincent and Grenadines',
                    "Samoa":'Samoa',
                    "San Marino":'San Marino',
                    "Sao Tome and Principe":'Sao Tome and Principe',
                    "Saudi Arabia":'Saudi Arabia',
                    "Scotland":'Scotland',
                    "Senegal":'Senegal',
                    "Serbia":'Serbia',
                    "Seychelles":'Seychelles',
                    "Sierra Leone":'Sierra Leone',
                    "Singapore":'Singapore',
                    "Slovakia":'Slovakia',
                    "Slovenia":'Slovenia',
                    "Solomon Islands":'Solomon Islands',
                    "South Africa":'South Africa',
                    "South Georgia & South Sandwich Islands":'South Georgia & South Sandwich Islands',
                    "South Korea":'South Korea',
                    "Spain":'Spain',
                    "Sri Lanka":'Sri Lanka',
                    "Suriname":'Suriname',
                    "Svalbard and Jan Mayen":'Svalbard and Jan Mayen',
                    "Swaziland":'Swaziland',
                    "Sweden":'Sweden',
                    "Switzerland":'Switzerland',
                    "Taiwan":'Taiwan',
                    "Tajikistan":'Tajikistan',
                    "Tanzania":'Tanzania',
                    "Thailand":'Thailand',
                    "Timor-Leste":'Timor-Leste',
                    "Togo":'Togo',
                    "Tokelau":'Tokelau',
                    "Tonga":'Tonga',
                    "Turkey":'Turkey',
                    "Turkmenistan":'Turkmenistan',
                    "Turks and Caicos Islands":'Turks and Caicos Islands',
                    "Tuvalu":'Tuvalu',
                    "Uganda":'Uganda',
                    "United Arab Emirates":'United Arab Emirates',
                    "United Kingdom":'United Kingdom',
                    "United States":'United States',
                    "United States Virgin Islands":'United States Virgin Islands',
                    "Uruguay":'Uruguay',
                    "Uzbekistan":'Uzbekistan',
                    "Vanuatu":'Vanuatu',
                    "Vatican City":'Vatican City',
                    "Vietnam":'Vietnam',
                    "Wallis and Futuna":'Wallis and Futuna',
                    "Wales":'Wales',
                    "Western Sahara":'Western Sahara',
                    "Zambia":'Zambia'
                };

        });
        $("#country-input").on("keyup", function (e)
        {
            if($(this).val() == "")
            {
                //alert("No Data");
                $("ul#autocomplete_id").hide();
            }
            else
            {
                //alert("Has Data");
                $("ul#autocomplete_id").show();
            }

            /*if ($("ul#autocomplete_id").has("li").length == 0) {
             //alert("No Li");
             $(this).hide();
             }
             else {
             //alert("Has Li");
             $(this).show();
             }*/
        });
        function countryhide(countryvalue,hiddencountryvalue,pincode,hiddenpincode){
            var country_val = $("#"+countryvalue).val();
            var x, countryName;
            //console.log("country_val++"+country_val);
            for (x in languageCountry) {
                //console.log("country_val X++"+x);
                if(country_val == x)
                {
                    //console.log("country_val final++"+x);
                    countryName = languageCountry[x];
                }
            }
            var hiddencountry_val = document.getElementById([hiddencountryvalue]).value;

            var autocomp = document.getElementById('autocomplete_id');

            if (countryName == "Afghanistan")
            {
                document.getElementById([hiddencountryvalue]).value = 'AF';
                document.getElementById([pincode]).value = '093';
                document.getElementById([hiddenpincode]).value = '093';
                autocomp.style.display = "none";
            }
            else if (countryName == "Aland Islands")
            {
                document.getElementById([hiddencountryvalue]).value = 'AX';
                document.getElementById([pincode]).value = '358';
                document.getElementById([hiddenpincode]).value = '358';
                autocomp.style.display = "none";
            }
            else if (countryName == "Albania")
            {
                document.getElementById([hiddencountryvalue]).value = 'AL';
                document.getElementById([pincode]).value = '355';
                document.getElementById([hiddenpincode]).value = '355';
                autocomp.style.display = "none";
            }
            else if (countryName == "Algeria")
            {
                document.getElementById([hiddencountryvalue]).value = 'DZ';
                document.getElementById([pincode]).value = '231';
                document.getElementById([hiddenpincode]).value = '231';
                autocomp.style.display = "none";
            }
            else if (countryName == "American Samoa")
            {
                document.getElementById([hiddencountryvalue]).value = 'AS';
                document.getElementById([pincode]).value = '684';
                document.getElementById([hiddenpincode]).value = '684';
                autocomp.style.display = "none";
            }
            else if (countryName == "Andorra")
            {
                document.getElementById([hiddencountryvalue]).value = 'AD';
                document.getElementById([pincode]).value = '376';
                document.getElementById([hiddenpincode]).value = '376';
                autocomp.style.display = "none";
            }
            else if (countryName == "Angola")
            {
                document.getElementById([hiddencountryvalue]).value = 'AO';
                document.getElementById([pincode]).value = '244';
                document.getElementById([hiddenpincode]).value = '244';
                autocomp.style.display = "none";
            }
            else if (countryName == "Anguilla")
            {
                document.getElementById([hiddencountryvalue]).value = 'AI';
                document.getElementById([pincode]).value = '001';
                document.getElementById([hiddenpincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Antarctica")
            {
                document.getElementById([hiddencountryvalue]).value = 'AQ';
                document.getElementById([pincode]).value = '000';
                document.getElementById([hiddenpincode]).value = '000';
                autocomp.style.display = "none";
            }
            else if (countryName == "Antigua and Barbuda")
            {
                document.getElementById([hiddencountryvalue]).value = 'AG';
                document.getElementById([pincode]).value = '001';
                document.getElementById([hiddenpincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Argentina")
            {
                document.getElementById([hiddencountryvalue]).value = 'AR';
                document.getElementById([pincode]).value = '054';
                document.getElementById([hiddenpincode]).value = '054';
                autocomp.style.display = "none";
            }
            else if (countryName == "Armenia")
            {
                document.getElementById([hiddencountryvalue]).value = 'AM';
                document.getElementById([pincode]).value = '374';
                document.getElementById([hiddenpincode]).value = '374';
                autocomp.style.display = "none";
            }
            else if (countryName == "Aruba")
            {
                document.getElementById([hiddencountryvalue]).value = 'AW';
                document.getElementById([pincode]).value = '297';
                document.getElementById([hiddenpincode]).value = '297';
                autocomp.style.display = "none";
            }
            else if (countryName == "Australia")
            {
                document.getElementById([hiddencountryvalue]).value = 'AU';
                document.getElementById([pincode]).value = '061';
                document.getElementById([hiddenpincode]).value = '061';
                autocomp.style.display = "none";
            }
            else if (countryName == "Austria")
            {
                document.getElementById([hiddencountryvalue]).value = 'AT';
                document.getElementById([pincode]).value = '043';
                document.getElementById([hiddenpincode]).value = '043';
                autocomp.style.display = "none";
            }
            else if (countryName == "Azerbaijan")
            {
                document.getElementById([hiddencountryvalue]).value = 'AZ';
                document.getElementById([pincode]).value = '994';
                document.getElementById([hiddenpincode]).value = '994';
                autocomp.style.display = "none";
            }
            else if (countryName == "Bahamas")
            {
                document.getElementById([hiddencountryvalue]).value = 'BS';
                document.getElementById([pincode]).value = '001';
                document.getElementById([hiddenpincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Bahrain")
            {
                document.getElementById([hiddencountryvalue]).value = 'BH';
                document.getElementById([pincode]).value = '973';
                document.getElementById([hiddenpincode]).value = '973';
                autocomp.style.display = "none";
            }
            else if (countryName == "Bangladesh")
            {
                document.getElementById([hiddencountryvalue]).value = 'BD';
                document.getElementById([pincode]).value = '880';
                autocomp.style.display = "none";
            }
            else if (countryName == "Barbados")
            {
                document.getElementById([hiddencountryvalue]).value = 'BB';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Belarus")
            {
                document.getElementById([hiddencountryvalue]).value = 'BY';
                document.getElementById([pincode]).value = '375';
                autocomp.style.display = "none";
            }
            else if (countryName == "Belgium")
            {
                document.getElementById([hiddencountryvalue]).value = 'BE';
                document.getElementById([pincode]).value = '032';
                autocomp.style.display = "none";
            }
            else if (countryName == "Belize")
            {
                document.getElementById([hiddencountryvalue]).value = 'BZ';
                document.getElementById([pincode]).value = '501';
                autocomp.style.display = "none";
            }
            else if (countryName == "Benin")
            {
                document.getElementById([hiddencountryvalue]).value = 'BJ';
                document.getElementById([pincode]).value = '229';
                autocomp.style.display = "none";
            }
            else if (countryName == "Bermuda")
            {
                document.getElementById([hiddencountryvalue]).value = 'BM';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Bhutan")
            {
                document.getElementById([hiddencountryvalue]).value = 'BT';
                document.getElementById([pincode]).value = '975';
                autocomp.style.display = "none";
            }
            else if (countryName == "Bolivia")
            {
                document.getElementById([hiddencountryvalue]).value = 'BO';
                document.getElementById([pincode]).value = '591';
                autocomp.style.display = "none";
            }
            else if (countryName == "Bosnia and Herzegovina")
            {
                document.getElementById([hiddencountryvalue]).value = 'BA';
                document.getElementById([pincode]).value = '387';
                autocomp.style.display = "none";
            }
            else if (countryName == "Botswana")
            {
                document.getElementById([hiddencountryvalue]).value = 'BW';
                document.getElementById([pincode]).value = '267';
                autocomp.style.display = "none";
            }
            else if (countryName == "Bouvet Island")
            {
                document.getElementById([hiddencountryvalue]).value = 'BV';
                document.getElementById([pincode]).value = '000';
                autocomp.style.display = "none";
            }
            else if (countryName == "Brazil")
            {
                document.getElementById([hiddencountryvalue]).value = 'BR';
                document.getElementById([pincode]).value = '055';
                autocomp.style.display = "none";
            }
            else if (countryName == "British Indian Ocean Territory")
            {
                document.getElementById([hiddencountryvalue]).value = 'IO';
                document.getElementById([pincode]).value = '246';
                autocomp.style.display = "none";
            }
            else if (countryName == "British Virgin Islands")
            {
                document.getElementById([hiddencountryvalue]).value = 'VG';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Brunei")
            {
                document.getElementById([hiddencountryvalue]).value = 'BN';
                document.getElementById([pincode]).value = '673';
                autocomp.style.display = "none";
            }
            else if (countryName == "Bulgaria")
            {
                document.getElementById([hiddencountryvalue]).value = 'BG';
                document.getElementById([pincode]).value = '359';
                autocomp.style.display = "none";
            }
            else if (countryName == "Burkina Faso")
            {
                document.getElementById([hiddencountryvalue]).value = 'BF';
                document.getElementById([pincode]).value = '226';
                autocomp.style.display = "none";
            }
            else if (countryName == "Burundi")
            {
                document.getElementById([hiddencountryvalue]).value = 'BI';
                document.getElementById([pincode]).value = '257';
                autocomp.style.display = "none";
            }
            else if (countryName == "Cambodia")
            {
                document.getElementById([hiddencountryvalue]).value = 'KH';
                document.getElementById([pincode]).value = '855';
                autocomp.style.display = "none";
            }
            else if (countryName == "Cameroon")
            {
                document.getElementById([hiddencountryvalue]).value = 'CM';
                document.getElementById([pincode]).value = '237';
                autocomp.style.display = "none";
            }
            else if (countryName == "Canada")
            {
                document.getElementById([hiddencountryvalue]).value = 'CA';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Cape Verde")
            {
                document.getElementById([hiddencountryvalue]).value = 'CV';
                document.getElementById([pincode]).value = '238';
                autocomp.style.display = "none";
            }
            else if (countryName == "Cayman Islands")
            {
                document.getElementById([hiddencountryvalue]).value = 'KY';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Central African Republic")
            {
                document.getElementById([hiddencountryvalue]).value = 'CF';
                document.getElementById([pincode]).value = '236';
                autocomp.style.display = "none";
            }
            else if (countryName == "Chad")
            {
                document.getElementById([hiddencountryvalue]).value = 'TD';
                document.getElementById([pincode]).value = '235';
                autocomp.style.display = "none";
            }
            else if (countryName == "Chile")
            {
                document.getElementById([hiddencountryvalue]).value = 'CL';
                document.getElementById([pincode]).value = '056';
                autocomp.style.display = "none";
            }
            else if (countryName == "China")
            {
                document.getElementById([hiddencountryvalue]).value = 'CN';
                document.getElementById([pincode]).value = '086';
                autocomp.style.display = "none";
            }
            else if (countryName == "Christmas Island")
            {
                document.getElementById([hiddencountryvalue]).value = 'CX';
                document.getElementById([pincode]).value = '061';
                autocomp.style.display = "none";
            }
            else if (countryName == "Cocos (Keeling Islands)")
            {
                document.getElementById([hiddencountryvalue]).value = 'CC';
                document.getElementById([pincode]).value = '061';
                autocomp.style.display = "none";
            }
            else if (countryName == "Colombia")
            {
                document.getElementById([hiddencountryvalue]).value = 'CO';
                document.getElementById([pincode]).value = '057';
                autocomp.style.display = "none";
            }
            else if (countryName == "Comoros")
            {
                document.getElementById([hiddencountryvalue]).value = 'KM';
                document.getElementById([pincode]).value = '269';
                autocomp.style.display = "none";
            }
            else if (countryName == "Cook Islands")
            {
                document.getElementById([hiddencountryvalue]).value = 'CK';
                document.getElementById([pincode]).value = '682';
                autocomp.style.display = "none";
            }
            else if (countryName == "Costa Rica")
            {
                document.getElementById([hiddencountryvalue]).value = 'CR';
                document.getElementById([pincode]).value = '506';
                autocomp.style.display = "none";
            }
            else if (countryName == "Cote d` Ivoire")
            {
                document.getElementById([hiddencountryvalue]).value = 'CI';
                document.getElementById([pincode]).value = '225';
                autocomp.style.display = "none";
            }
            else if (countryName == "Croatia")
            {
                document.getElementById([hiddencountryvalue]).value = 'HR';
                document.getElementById([pincode]).value = '385';
                autocomp.style.display = "none";
            }
            else if (countryName == "Cuba")
            {
                document.getElementById([hiddencountryvalue]).value = 'CU';
                document.getElementById([pincode]).value = '053';
                autocomp.style.display = "none";
            }
            else if (countryName == "Cyprus")
            {
                document.getElementById([hiddencountryvalue]).value = 'CY';
                document.getElementById([pincode]).value = '357';
                autocomp.style.display = "none";
            }
            else if (countryName == "Czech Republic")
            {
                document.getElementById([hiddencountryvalue]).value = 'CZ';
                document.getElementById([pincode]).value = '420';
                autocomp.style.display = "none";
            }
            else if (countryName == "Democratic Republic of the Congo")
            {
                document.getElementById([hiddencountryvalue]).value = 'CD';
                document.getElementById([pincode]).value = '243';
                autocomp.style.display = "none";
            }
            else if (countryName == "Denmark")
            {
                document.getElementById([hiddencountryvalue]).value = 'DK';
                document.getElementById([pincode]).value = '045';
                autocomp.style.display = "none";
            }
            else if (countryName == "Djibouti")
            {
                document.getElementById([hiddencountryvalue]).value = 'DJ';
                document.getElementById([pincode]).value = '253';
                autocomp.style.display = "none";
            }
            else if (countryName == "Dominica")
            {
                document.getElementById([hiddencountryvalue]).value = 'DM';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Dominican Republic")
            {
                document.getElementById([hiddencountryvalue]).value = 'DO';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Ecuador")
            {
                document.getElementById([hiddencountryvalue]).value = 'EC';
                document.getElementById([pincode]).value = '593';
                autocomp.style.display = "none";
            }
            else if (countryName == "Egypt")
            {
                document.getElementById([hiddencountryvalue]).value = 'EG';
                document.getElementById([pincode]).value = '020';
                autocomp.style.display = "none";
            }
            else if (countryName == "El Salvador")
            {
                document.getElementById([hiddencountryvalue]).value = 'SV';
                document.getElementById([pincode]).value = '503';
                autocomp.style.display = "none";
            }
            else if (countryName == "Equatorial Guinea")
            {
                document.getElementById([hiddencountryvalue]).value = 'GQ';
                document.getElementById([pincode]).value = '240';
                autocomp.style.display = "none";
            }
            else if (countryName == "Eritrea")
            {
                document.getElementById([hiddencountryvalue]).value = 'ER';
                document.getElementById([pincode]).value = '291';
                autocomp.style.display = "none";
            }
            else if (countryName == "Estonia")
            {
                document.getElementById([hiddencountryvalue]).value = 'EE';
                document.getElementById([pincode]).value = '372';
                autocomp.style.display = "none";
            }
            else if (countryName == "Ethiopia")
            {
                document.getElementById([hiddencountryvalue]).value = 'ET';
                document.getElementById([pincode]).value = '251';
                autocomp.style.display = "none";
            }
            else if (countryName == "Falkland Islands")
            {
                document.getElementById([hiddencountryvalue]).value = 'FK';
                document.getElementById([pincode]).value = '500';
                autocomp.style.display = "none";
            }
            else if (countryName == "Faroe Islands")
            {
                document.getElementById([hiddencountryvalue]).value = 'FO';
                document.getElementById([pincode]).value = '298';
                autocomp.style.display = "none";
            }
            else if (countryName == "Federated States of Micronesia")
            {
                document.getElementById([hiddencountryvalue]).value = 'FM';
                document.getElementById([pincode]).value = '691';
                autocomp.style.display = "none";
            }
            else if (countryName == "Fiji")
            {
                document.getElementById([hiddencountryvalue]).value = 'FJ';
                document.getElementById([pincode]).value = '679';
                autocomp.style.display = "none";
            }
            else if (countryName == "Finland")
            {
                document.getElementById([hiddencountryvalue]).value = 'FI';
                document.getElementById([pincode]).value = '358';
                autocomp.style.display = "none";
            }
            else if (countryName == "France")
            {
                document.getElementById([hiddencountryvalue]).value = 'FR';
                document.getElementById([pincode]).value = '033';
                autocomp.style.display = "none";
            }
            else if (countryName == "French Guiana")
            {
                document.getElementById([hiddencountryvalue]).value = 'GF';
                document.getElementById([pincode]).value = '594';
                autocomp.style.display = "none";
            }
            else if (countryName == "French Polynesia")
            {
                document.getElementById([hiddencountryvalue]).value = 'PF';
                document.getElementById([pincode]).value = '689';
                autocomp.style.display = "none";
            }
            else if (countryName == "French Southern and Antarctic Lands")
            {
                document.getElementById([hiddencountryvalue]).value = 'TF';
                document.getElementById([pincode]).value = '000';
                autocomp.style.display = "none";
            }
            else if (countryName == "Gabon")
            {
                document.getElementById([hiddencountryvalue]).value = 'GA';
                document.getElementById([pincode]).value = '241';
                autocomp.style.display = "none";
            }
            else if (countryName == "Gambia")
            {
                document.getElementById([hiddencountryvalue]).value = 'GM';
                document.getElementById([pincode]).value = '220';
                autocomp.style.display = "none";
            }
            else if (countryName == "Georgia")
            {
                document.getElementById([hiddencountryvalue]).value = 'GE';
                document.getElementById([pincode]).value = '995';
                autocomp.style.display = "none";
            }
            else if (countryName == "Germany")
            {
                document.getElementById([hiddencountryvalue]).value = 'DE';
                document.getElementById([pincode]).value = '049';
                document.getElementById([hiddenpincode]).value = '049';
                autocomp.style.display = "none";
            }
            else if (countryName == "Ghana")
            {
                document.getElementById([hiddencountryvalue]).value = 'GH';
                document.getElementById([pincode]).value = '233';
                autocomp.style.display = "none";
            }
            else if (countryName == "Gibraltar")
            {
                document.getElementById([hiddencountryvalue]).value = 'GI';
                document.getElementById([pincode]).value = '350';
                autocomp.style.display = "none";
            }
            else if (countryName == "Greece")
            {
                document.getElementById([hiddencountryvalue]).value = 'GR';
                document.getElementById([pincode]).value = '030';
                autocomp.style.display = "none";
            }
            else if (countryName == "Greenland")
            {
                document.getElementById([hiddencountryvalue]).value = 'GL';
                document.getElementById([pincode]).value = '299';
                autocomp.style.display = "none";}
            else if (countryName == "Grenada")
            {
                document.getElementById([hiddencountryvalue]).value = 'GD';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Guadeloupe")
            {
                document.getElementById([hiddencountryvalue]).value = 'GP';
                document.getElementById([pincode]).value = '590';
                autocomp.style.display = "none";
            }
            else if (countryName == "Guam")
            {
                document.getElementById([hiddencountryvalue]).value = 'GU';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Guatemala")
            {
                document.getElementById([hiddencountryvalue]).value = 'GT';
                document.getElementById([pincode]).value = '502';
                autocomp.style.display = "none";
            }
            else if (countryName == "Guernsey")
            {
                document.getElementById([hiddencountryvalue]).value = 'GG';
                document.getElementById([pincode]).value = '000';
                autocomp.style.display = "none";
            }
            else if (countryName == "Guinea")
            {
                document.getElementById([hiddencountryvalue]).value = 'GN';
                document.getElementById([pincode]).value = '224';
                autocomp.style.display = "none";
            }
            else if (countryName == "Guinea-Bissau")
            {
                document.getElementById([hiddencountryvalue]).value = 'GW';
                document.getElementById([pincode]).value = '245';
                autocomp.style.display = "none";
            }
            else if (countryName == "Guyana")
            {
                document.getElementById([hiddencountryvalue]).value = 'GY';
                document.getElementById([pincode]).value = '592';
                autocomp.style.display = "none";
            }
            else if (countryName == "Haiti")
            {
                document.getElementById([hiddencountryvalue]).value = 'HT';
                document.getElementById([pincode]).value = '509';
                autocomp.style.display = "none";
            }
            else if (countryName == "Heard Island & McDonald Islands")
            {
                document.getElementById([hiddencountryvalue]).value = 'HM';
                document.getElementById([pincode]).value = '672';
                autocomp.style.display = "none";
            }
            else if (countryName == "Honduras")
            {
                document.getElementById([hiddencountryvalue]).value = 'HN';
                document.getElementById([pincode]).value = '504';
                autocomp.style.display = "none";
            }
            else if (countryName == "Hong Kong")
            {
                document.getElementById([hiddencountryvalue]).value = 'HK';
                document.getElementById([pincode]).value = '852';
                document.getElementById([hiddenpincode]).value = '852';
                autocomp.style.display = "none";
            }
            else if (countryName == "Hungary")
            {
                document.getElementById([hiddencountryvalue]).value = 'HU';
                document.getElementById([pincode]).value = '036';
                autocomp.style.display = "none";
            }
            else if (countryName == "Iceland")
            {
                document.getElementById([hiddencountryvalue]).value = 'IS';
                document.getElementById([pincode]).value = '354';
                autocomp.style.display = "none";
            }
            else if (countryName == "India")
            {
                //alert("YES its India!!!");
                document.getElementById([hiddencountryvalue]).value = 'IN';
                document.getElementById([pincode]).value = '091';
                document.getElementById([hiddenpincode]).value = '091';
                autocomp.style.display = "none";
            }
            else if (countryName == "Indonesia")
            {
                document.getElementById([hiddencountryvalue]).value = 'ID';
                document.getElementById([pincode]).value = '062';
                autocomp.style.display = "none";
            }
            else if (countryName == "Iran")
            {
                document.getElementById([hiddencountryvalue]).value = 'IR';
                document.getElementById([pincode]).value = '098';
                autocomp.style.display = "none";
            }
            else if (countryName == "Iraq")
            {
                document.getElementById([hiddencountryvalue]).value = 'IQ';
                document.getElementById([pincode]).value = '964';
                autocomp.style.display = "none";
            }
            else if (countryName == "Ireland")
            {
                document.getElementById([hiddencountryvalue]).value = 'IE';
                document.getElementById([pincode]).value = '353';
                autocomp.style.display = "none";
            }
            else if (countryName == "Israel")
            {
                document.getElementById([hiddencountryvalue]).value = 'IL';
                document.getElementById([pincode]).value = '972';
                autocomp.style.display = "none";
            }
            else if (countryName == "Italy")
            {
                document.getElementById([hiddencountryvalue]).value = 'IT';
                document.getElementById([pincode]).value = '039';
                autocomp.style.display = "none";
            }
            else if (countryName == "Jamaica")
            {
                document.getElementById([hiddencountryvalue]).value = 'JM';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Japan")
            {
                document.getElementById([hiddencountryvalue]).value = 'JP';
                document.getElementById([pincode]).value = '081';
                document.getElementById([hiddenpincode]).value = '081';
                autocomp.style.display = "none";
            }
            else if (countryName == "Jersey")
            {
                document.getElementById([hiddencountryvalue]).value = 'JE';
                document.getElementById([pincode]).value = '044';
                autocomp.style.display = "none";
            }
            else if (countryName == "Jordan")
            {
                document.getElementById([hiddencountryvalue]).value = 'JO';
                document.getElementById([pincode]).value = '962';
                autocomp.style.display = "none";
            }
            else if (countryName == "Kazakhstan")
            {
                document.getElementById([hiddencountryvalue]).value = 'KZ';
                document.getElementById([pincode]).value = '007';
                autocomp.style.display = "none";
            }
            else if (countryName == "Kenya")
            {
                document.getElementById([hiddencountryvalue]).value = 'KE';
                document.getElementById([pincode]).value = '254';
                autocomp.style.display = "none";
            }
            else if (countryName == "Kiribati")
            {
                document.getElementById([hiddencountryvalue]).value = 'KI';
                document.getElementById([pincode]).value = '686';
                autocomp.style.display = "none";
            }
            else if (countryName == "Kuwait")
            {
                document.getElementById([hiddencountryvalue]).value = 'KW';
                document.getElementById([pincode]).value = '965';
                autocomp.style.display = "none";
            }
            else if (countryName == "Kyrgystan")
            {
                document.getElementById([hiddencountryvalue]).value = 'KG';
                document.getElementById([pincode]).value = '996';
                autocomp.style.display = "none";
            }
            else if (countryName == "Laos")
            {
                document.getElementById([hiddencountryvalue]).value = 'LA';
                document.getElementById([pincode]).value = '856';
                autocomp.style.display = "none";
            }
            else if (countryName == "Latvia")
            {
                document.getElementById([hiddencountryvalue]).value = 'LV';
                document.getElementById([pincode]).value = '371';
                autocomp.style.display = "none";
            }
            else if (countryName == "Lebanon")
            {
                document.getElementById([hiddencountryvalue]).value = 'LB';
                document.getElementById([pincode]).value = '961';
                autocomp.style.display = "none";
            }
            else if (countryName == "Lesotho")
            {
                document.getElementById([hiddencountryvalue]).value = 'LS';
                document.getElementById([pincode]).value = '266';
                autocomp.style.display = "none";
            }
            else if (countryName == "Liberia")
            {
                document.getElementById([hiddencountryvalue]).value = 'LR';
                document.getElementById([pincode]).value = '231';
                autocomp.style.display = "none";
            }
            else if (countryName == "Libya")
            {
                document.getElementById([hiddencountryvalue]).value = 'LY';
                document.getElementById([pincode]).value = '218';
                autocomp.style.display = "none";
            }
            else if (countryName == "Liechtenstein")
            {
                document.getElementById([hiddencountryvalue]).value = 'LI';
                document.getElementById([pincode]).value = '423';
                autocomp.style.display = "none";
            }
            else if (countryName == "Lithuania")
            {
                document.getElementById([hiddencountryvalue]).value = 'LT';
                document.getElementById([pincode]).value = '370';
                autocomp.style.display = "none";
            }
            else if (countryName == "Luxembourg")
            {
                document.getElementById([hiddencountryvalue]).value = 'LU';
                document.getElementById([pincode]).value = '352';
                autocomp.style.display = "none";
            }
            else if (countryName == "Macau, China")
            {
                document.getElementById([hiddencountryvalue]).value = 'MO';
                document.getElementById([pincode]).value = '853';
                document.getElementById([hiddenpincode]).value = '853';
                autocomp.style.display = "none";
            }
            else if (countryName == "Macedonia")
            {
                document.getElementById([hiddencountryvalue]).value = 'MK';
                document.getElementById([pincode]).value = '389';
                autocomp.style.display = "none";
            }
            else if (countryName == "Madagascar")
            {
                document.getElementById([hiddencountryvalue]).value = 'MG';
                document.getElementById([pincode]).value = '261';
                autocomp.style.display = "none";
            }
            else if (countryName == "Malawi")
            {
                document.getElementById([hiddencountryvalue]).value = 'MW';
                document.getElementById([pincode]).value = '265';
                autocomp.style.display = "none";
            }
            else if (countryName == "Malaysia")
            {
                document.getElementById([hiddencountryvalue]).value = 'MY';
                document.getElementById([pincode]).value = '060';
                autocomp.style.display = "none";
            }
            else if (countryName == "Maldives")
            {
                document.getElementById([hiddencountryvalue]).value = 'MV';
                document.getElementById([pincode]).value = '960';
                autocomp.style.display = "none";
            }
            else if (countryName == "Mali")
            {
                document.getElementById([hiddencountryvalue]).value = 'ML';
                document.getElementById([pincode]).value = '223';
                autocomp.style.display = "none";
            }
            else if (countryName == "Malta")
            {
                document.getElementById([hiddencountryvalue]).value = 'MT';
                document.getElementById([pincode]).value = '356';
                autocomp.style.display = "none";
            }
            else if (countryName == "Marshall Islands")
            {
                document.getElementById([hiddencountryvalue]).value = 'MH';
                document.getElementById([pincode]).value = '692';
                autocomp.style.display = "none";
            }
            else if (countryName == "Martinique")
            {
                document.getElementById([hiddencountryvalue]).value = 'MQ';
                document.getElementById([pincode]).value = '596';
                autocomp.style.display = "none";
            }
            else if (countryName == "Mauritania")
            {
                document.getElementById([hiddencountryvalue]).value = 'MR';
                document.getElementById([pincode]).value = '222';
                autocomp.style.display = "none";
            }
            else if (countryName == "Mauritius")
            {
                document.getElementById([hiddencountryvalue]).value = 'MU';
                document.getElementById([pincode]).value = '230';
                document.getElementById([hiddenpincode]).value = '230';
                autocomp.style.display = "none";
            }
            else if (countryName == "Mayotte")
            {
                document.getElementById([hiddencountryvalue]).value = 'YT';
                document.getElementById([pincode]).value = '269';
                autocomp.style.display = "none";
            }
            else if (countryName == "Mexico")
            {
                document.getElementById([hiddencountryvalue]).value = 'MX';
                document.getElementById([pincode]).value = '052';
                autocomp.style.display = "none";
            }
            else if (countryName == "Moldova")
            {
                document.getElementById([hiddencountryvalue]).value = 'MD';
                document.getElementById([pincode]).value = '373';
                autocomp.style.display = "none";
            }
            else if (countryName == "Monaco")
            {
                document.getElementById([hiddencountryvalue]).value = 'MC';
                document.getElementById([pincode]).value = '377';
                autocomp.style.display = "none";
            }
            else if (countryName == "Mongolia")
            {
                document.getElementById([hiddencountryvalue]).value = 'MN';
                document.getElementById([pincode]).value = '976';
                autocomp.style.display = "none";
            }
            else if (countryName == "Montenegro")
            {
                document.getElementById([hiddencountryvalue]).value = 'ME';
                document.getElementById([pincode]).value = '382';
                autocomp.style.display = "none";
            }
            else if (countryName == "Montserrat")
            {
                document.getElementById([hiddencountryvalue]).value = 'MS';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Morocco")
            {
                document.getElementById([hiddencountryvalue]).value = 'MA';
                document.getElementById([pincode]).value = '212';
                autocomp.style.display = "none";
            }
            else if (countryName == "Mozambique")
            {
                document.getElementById([hiddencountryvalue]).value = 'MZ';
                document.getElementById([pincode]).value = '258';
                autocomp.style.display = "none";
            }
            else if (countryName == "Myanmar")
            {
                document.getElementById([hiddencountryvalue]).value = 'MM';
                document.getElementById([pincode]).value = '095';
                autocomp.style.display = "none";
            }
            else if (countryName == "Namibia")
            {
                document.getElementById([hiddencountryvalue]).value = 'NA';
                document.getElementById([pincode]).value = '264';
                autocomp.style.display = "none";
            }
            else if (countryName == "Nauru")
            {
                document.getElementById([hiddencountryvalue]).value = 'NR';
                document.getElementById([pincode]).value = '674';
                autocomp.style.display = "none";
            }
            else if (countryName == "Nepal")
            {
                document.getElementById([hiddencountryvalue]).value = 'NP';
                document.getElementById([pincode]).value = '977';
                autocomp.style.display = "none";
            }
            else if (countryName == "Netherlands Antilles")
            {
                document.getElementById([hiddencountryvalue]).value = 'AN';
                document.getElementById([pincode]).value = '599';
                autocomp.style.display = "none";
            }
            else if (countryName == "Netherlands")
            {
                document.getElementById([hiddencountryvalue]).value = 'NL';
                document.getElementById([pincode]).value = '031';
                autocomp.style.display = "none";
            }
            else if (countryName == "New Caledonia")
            {
                document.getElementById([hiddencountryvalue]).value = 'NC';
                document.getElementById([pincode]).value = '687';
                autocomp.style.display = "none";
            }
            else if (countryName == "New Zealand")
            {
                document.getElementById([hiddencountryvalue]).value = 'NZ';
                document.getElementById([pincode]).value = '064';
                autocomp.style.display = "none";
            }
            else if (countryName == "Nicaragua")
            {
                document.getElementById([hiddencountryvalue]).value = 'NI';
                document.getElementById([pincode]).value = '505';
                autocomp.style.display = "none";
            }
            else if (countryName == "Niger")
            {
                document.getElementById([hiddencountryvalue]).value = 'NE';
                document.getElementById([pincode]).value = '227';
                autocomp.style.display = "none";
            }
            else if (countryName == "Nigeria")
            {
                document.getElementById([hiddencountryvalue]).value = 'NG';
                document.getElementById([pincode]).value = '234';
                autocomp.style.display = "none";
            }
            else if (countryName == "Niue")
            {
                document.getElementById([hiddencountryvalue]).value = 'NU';
                document.getElementById([pincode]).value = '683';
                autocomp.style.display = "none";
            }
            else if (countryName == "Norfolk Island")
            {
                document.getElementById([hiddencountryvalue]).value = 'NF';
                document.getElementById([pincode]).value = '672';
                autocomp.style.display = "none";
            }
            else if (countryName == "North Korea")
            {
                document.getElementById([hiddencountryvalue]).value = 'KP';
                document.getElementById([pincode]).value = '850';
                autocomp.style.display = "none";
            }
            else if (countryName == "Northern Mariana Islands")
            {
                document.getElementById([hiddencountryvalue]).value = 'MP';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Norway")
            {
                document.getElementById([hiddencountryvalue]).value = 'NO';
                document.getElementById([pincode]).value = '047';
                autocomp.style.display = "none";
            }
            else if (countryName == "Oman")
            {
                document.getElementById([hiddencountryvalue]).value = 'OM';
                document.getElementById([pincode]).value = '968';
                autocomp.style.display = "none";
            }
            else if (countryName == "Pakistan")
            {
                document.getElementById([hiddencountryvalue]).value = 'PK';
                document.getElementById([pincode]).value = '092';
                autocomp.style.display = "none";
            }
            else if (countryName == "Palau")
            {
                document.getElementById([hiddencountryvalue]).value = 'PW';
                document.getElementById([pincode]).value = '680';
                autocomp.style.display = "none";
            }
            else if (countryName == "Palestinian Authority")
            {
                document.getElementById([hiddencountryvalue]).value = 'PS';
                document.getElementById([pincode]).value = '970';
                autocomp.style.display = "none";
            }
            else if (countryName == "Panama")
            {
                document.getElementById([hiddencountryvalue]).value = 'PA';
                document.getElementById([pincode]).value = '507';
                autocomp.style.display = "none";
            }
            else if (countryName == "Papua New Guinea")
            {
                document.getElementById([hiddencountryvalue]).value = 'PG';
                document.getElementById([pincode]).value = '675';
                autocomp.style.display = "none";
            }
            else if (countryName == "Paraguay")
            {
                document.getElementById([hiddencountryvalue]).value = 'PY';
                document.getElementById([pincode]).value = '595';
                autocomp.style.display = "none";
            }
            else if (countryName == "Peru")
            {
                document.getElementById([hiddencountryvalue]).value = 'PE';
                document.getElementById([pincode]).value = '051';
                autocomp.style.display = "none";
            }
            else if (countryName == "Philippines")
            {
                document.getElementById([hiddencountryvalue]).value = 'PH';
                document.getElementById([pincode]).value = '063';
                autocomp.style.display = "none";
            }
            else if (countryName == "Pitcairn Islands")
            {
                document.getElementById([hiddencountryvalue]).value = 'PN';
                document.getElementById([pincode]).value = '064';
                autocomp.style.display = "none";
            }
            else if (countryName == "Poland")
            {
                document.getElementById([hiddencountryvalue]).value = 'PL';
                document.getElementById([pincode]).value = '048';
                autocomp.style.display = "none";
            }
            else if (countryName == "Portugal")
            {
                document.getElementById([hiddencountryvalue]).value = 'PT';
                document.getElementById([pincode]).value = '351';
                autocomp.style.display = "none";
            }
            else if (countryName == "Puerto Rico")
            {
                document.getElementById([hiddencountryvalue]).value = 'PR';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Qatar")
            {
                document.getElementById([hiddencountryvalue]).value = 'QA';
                document.getElementById([pincode]).value = '974';
                autocomp.style.display = "none";
            }
            else if (countryName == "Republic of the Congo")
            {
                document.getElementById([hiddencountryvalue]).value = 'CG';
                document.getElementById([pincode]).value = '242';
                autocomp.style.display = "none";
            }
            else if (countryName == "Reunion")
            {
                document.getElementById([hiddencountryvalue]).value = 'RE';
                document.getElementById([pincode]).value = '262';
                autocomp.style.display = "none";
            }
            else if (countryName == "Romania")
            {
                document.getElementById([hiddencountryvalue]).value = 'RO';
                document.getElementById([pincode]).value = '040';
                autocomp.style.display = "none";
            }
            else if (countryName == "Russia")
            {
                document.getElementById([hiddencountryvalue]).value = 'RU';
                document.getElementById([pincode]).value = '007';
                autocomp.style.display = "none";
            }
            else if (countryName == "Rwanda")
            {
                document.getElementById([hiddencountryvalue]).value = 'RW';
                document.getElementById([pincode]).value = '250';
                autocomp.style.display = "none";
            }
            else if (countryName == "Saint Barthelemy")
            {
                document.getElementById([hiddencountryvalue]).value = 'BL';
                document.getElementById([pincode]).value = '590';
                autocomp.style.display = "none";
            }
            else if (countryName == "Saint Helena, Ascension & Tristan daCunha")
            {
                document.getElementById([hiddencountryvalue]).value = 'SH';
                document.getElementById([pincode]).value = '290';
                autocomp.style.display = "none";
            }
            else if (countryName == "Saint Kitts and Nevis")
            {
                document.getElementById([hiddencountryvalue]).value = 'KN';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Saint Lucia")
            {
                document.getElementById([hiddencountryvalue]).value = 'LC';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Saint Martin")
            {
                document.getElementById([hiddencountryvalue]).value = 'MF';
                document.getElementById([pincode]).value = '590';
                autocomp.style.display = "none";
            }
            else if (countryName == "Saint Pierre and Miquelon")
            {
                document.getElementById([hiddencountryvalue]).value = 'PM';
                document.getElementById([pincode]).value = '508';
                autocomp.style.display = "none";
            }
            else if (countryName == "Saint Vincent and Grenadines")
            {
                document.getElementById([hiddencountryvalue]).value = 'VC';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Samoa")
            {
                document.getElementById([hiddencountryvalue]).value = 'WS';
                document.getElementById([pincode]).value = '685';
                autocomp.style.display = "none";
            }
            else if (countryName == "San Marino")
            {
                document.getElementById([hiddencountryvalue]).value = 'SM';
                document.getElementById([pincode]).value = '378';
                autocomp.style.display = "none";
            }
            else if (countryName == "Sao Tome and Principe")
            {
                document.getElementById([hiddencountryvalue]).value = 'ST';
                document.getElementById([pincode]).value = '239';
                autocomp.style.display = "none";
            }
            else if (countryName == "Saudi Arabia")
            {
                document.getElementById([hiddencountryvalue]).value = 'SA';
                document.getElementById([pincode]).value = '966';
                autocomp.style.display = "none";
            }
            else if (countryName == "Senegal")
            {
                document.getElementById([hiddencountryvalue]).value = 'SN';
                document.getElementById([pincode]).value = '221';
                autocomp.style.display = "none";
            }
            else if (countryName == "Serbia")
            {
                document.getElementById([hiddencountryvalue]).value = 'RS';
                document.getElementById([pincode]).value = '381';
                autocomp.style.display = "none";
            }
            else if (countryName == "Seychelles")
            {
                document.getElementById([hiddencountryvalue]).value = 'SC';
                document.getElementById([pincode]).value = '248';
                autocomp.style.display = "none";
            }
            else if (countryName == "Sierra Leone")
            {
                document.getElementById([hiddencountryvalue]).value = 'SL';
                document.getElementById([pincode]).value = '232';
                autocomp.style.display = "none";
            }
            else if (countryName == "Singapore")
            {
                document.getElementById([hiddencountryvalue]).value = 'SG';
                document.getElementById([pincode]).value = '065';
                autocomp.style.display = "none";
            }
            else if (countryName == "Slovakia")
            {
                document.getElementById([hiddencountryvalue]).value = 'SK';
                document.getElementById([pincode]).value = '421';
                autocomp.style.display = "none";
            }
            else if (countryName == "Slovenia")
            {
                document.getElementById([hiddencountryvalue]).value = 'SI';
                document.getElementById([pincode]).value = '386';
                autocomp.style.display = "none";
            }
            else if (countryName == "Solomon Islands")
            {
                document.getElementById([hiddencountryvalue]).value = 'SB';
                document.getElementById([pincode]).value = '677';
                autocomp.style.display = "none";
            }
            else if (countryName == "Somalia")
            {
                document.getElementById([hiddencountryvalue]).value = 'SO';
                document.getElementById([pincode]).value = '252';
                autocomp.style.display = "none";
            }
            else if (countryName == "South Africa")
            {
                document.getElementById([hiddencountryvalue]).value = 'ZA';
                document.getElementById([pincode]).value = '027';
                document.getElementById([hiddenpincode]).value = '027';
                autocomp.style.display = "none";
            }
            else if (countryName == "South Georgia & South Sandwich Islands")
            {
                document.getElementById([hiddencountryvalue]).value = 'GS';
                document.getElementById([pincode]).value = '000';
                autocomp.style.display = "none";
            }
            else if (countryName == "South Korea")
            {
                document.getElementById([hiddencountryvalue]).value = 'KR';
                document.getElementById([pincode]).value = '082';
                document.getElementById([hiddenpincode]).value = '082';
                autocomp.style.display = "none";
            }
            else if (countryName == "Spain")
            {
                document.getElementById([hiddencountryvalue]).value = 'ES';
                document.getElementById([pincode]).value = '034';
                document.getElementById([hiddenpincode]).value = '034';
                autocomp.style.display = "none";
            }
            else if (countryName == "Sri Lanka")
            {
                document.getElementById([hiddencountryvalue]).value = 'LK';
                document.getElementById([pincode]).value = '094';
                document.getElementById([hiddenpincode]).value = '094';
                autocomp.style.display = "none";
            }
            else if (countryName == "Sudan")
            {
                document.getElementById([hiddencountryvalue]).value = 'SD';
                document.getElementById([pincode]).value = '249';
                autocomp.style.display = "none";
            }
            else if (countryName == "Suriname")
            {
                document.getElementById([hiddencountryvalue]).value = 'SR';
                document.getElementById([pincode]).value = '597';
                autocomp.style.display = "none";
            }
            else if (countryName == "Svalbard and Jan Mayen")
            {
                document.getElementById([hiddencountryvalue]).value = 'SJ';
                document.getElementById([pincode]).value = '047';
                autocomp.style.display = "none";
            }
            else if (countryName == "Swaziland")
            {
                document.getElementById([hiddencountryvalue]).value = 'SZ';
                document.getElementById([pincode]).value = '268';
                autocomp.style.display = "none";
            }
            else if (countryName == "Sweden")
            {
                document.getElementById([hiddencountryvalue]).value = 'SE';
                document.getElementById([pincode]).value = '046';
                autocomp.style.display = "none";
            }
            else if (countryName == "Switzerland")
            {
                document.getElementById([hiddencountryvalue]).value = 'CH';
                document.getElementById([pincode]).value = '041';
                document.getElementById([hiddenpincode]).value = '041';
                autocomp.style.display = "none";
            }
            else if (countryName == "Syria")
            {
                document.getElementById([hiddencountryvalue]).value = 'SY';
                document.getElementById([pincode]).value = '963';
                document.getElementById([hiddenpincode]).value = '963';
                autocomp.style.display = "none";
            }
            else if (countryName == "Taiwan")
            {
                document.getElementById([hiddencountryvalue]).value = 'TW';
                document.getElementById([pincode]).value = '886';
                autocomp.style.display = "none";
            }
            else if (countryName == "Tajikistan")
            {
                document.getElementById([hiddencountryvalue]).value = 'TJ';
                document.getElementById([pincode]).value = '992';
                autocomp.style.display = "none";
            }
            else if (countryName == "Tanzania")
            {
                document.getElementById([hiddencountryvalue]).value = 'TZ';
                document.getElementById([pincode]).value = '255';
                autocomp.style.display = "none";
            }
            else if (countryName == "Thailand")
            {
                document.getElementById([hiddencountryvalue]).value = 'TH';
                document.getElementById([pincode]).value = '066';
                document.getElementById([hiddenpincode]).value = '066';
                autocomp.style.display = "none";
            }
            else if (countryName == "Timor-Leste")
            {
                document.getElementById([hiddencountryvalue]).value = 'TL';
                document.getElementById([pincode]).value = '670';
                autocomp.style.display = "none";
            }
            else if (countryName == "Togo")
            {
                document.getElementById([hiddencountryvalue]).value = 'TG';
                document.getElementById([pincode]).value = '228';
                autocomp.style.display = "none";
            }
            else if (countryName == "Tokelau")
            {
                document.getElementById([hiddencountryvalue]).value = 'TK';
                document.getElementById([pincode]).value = '690';
                autocomp.style.display = "none";
            }
            else if (countryName == "Tonga")
            {
                document.getElementById([hiddencountryvalue]).value = 'TO';
                document.getElementById([pincode]).value = '676';
                autocomp.style.display = "none";
            }
            else if (countryName == "Trinidad and Tobago")
            {
                document.getElementById([hiddencountryvalue]).value = 'TT';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Tunisia")
            {
                document.getElementById([hiddencountryvalue]).value = 'TN';
                document.getElementById([pincode]).value = '216';
                autocomp.style.display = "none";
            }
            else if (countryName == "Turkey")
            {
                document.getElementById([hiddencountryvalue]).value = 'TR';
                document.getElementById([pincode]).value = '090';
                document.getElementById([hiddenpincode]).value = '090';
                autocomp.style.display = "none";
            }
            else if (countryName == "Turkmenistan")
            {
                document.getElementById([hiddencountryvalue]).value = 'TM';
                document.getElementById([pincode]).value = '993';
                autocomp.style.display = "none";
            }
            else if (countryName == "Turks and Caicos Islands")
            {
                document.getElementById([hiddencountryvalue]).value = 'TC';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Tuvalu")
            {
                document.getElementById([hiddencountryvalue]).value = 'TV';
                document.getElementById([pincode]).value = '688';
                autocomp.style.display = "none";
            }
            else if (countryName == "Uganda")
            {
                document.getElementById([hiddencountryvalue]).value = 'UG';
                document.getElementById([pincode]).value = '296';
                autocomp.style.display = "none";
            }
            else if (countryName == "Ukraine")
            {
                document.getElementById([hiddencountryvalue]).value = 'UA';
                document.getElementById([pincode]).value = '380';
                autocomp.style.display = "none";
            }
            else if (countryName == "United Arab Emirates")
            {
                document.getElementById([hiddencountryvalue]).value = 'AE';
                document.getElementById([pincode]).value = '971';
                document.getElementById([hiddenpincode]).value = '971';
                autocomp.style.display = "none";
            }
            else if (countryName == "United Kingdom")
            {
                //alert("YES its United Kingdom");
                document.getElementById([hiddencountryvalue]).value = 'GB';
                document.getElementById([pincode]).value = '044';
                document.getElementById([hiddenpincode]).value = '044';
                autocomp.style.display = "none";
            }
            else if (countryName == "United States")
            {
                //alert("YES its United States");
                document.getElementById([hiddencountryvalue]).value = 'US';
                document.getElementById([pincode]).value = '001';
                document.getElementById([hiddenpincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "United States Virgin Islands")
            {
                //alert("YES its United States");
                document.getElementById([hiddencountryvalue]).value = 'VI';
                document.getElementById([pincode]).value = '001';
                autocomp.style.display = "none";
            }
            else if (countryName == "Uruguay")
            {
                document.getElementById([hiddencountryvalue]).value = 'UY';
                document.getElementById([pincode]).value = '598';
                autocomp.style.display = "none";
            }
            else if (countryName == "Uzbekistan")
            {
                document.getElementById([hiddencountryvalue]).value = 'UZ';
                document.getElementById([pincode]).value = '998';
                autocomp.style.display = "none";
            }
            else if (countryName == "Vanuatu")
            {
                document.getElementById([hiddencountryvalue]).value = 'VU';
                document.getElementById([pincode]).value = '678';
                autocomp.style.display = "none";
            }
            else if (countryName == "Vatican City")
            {
                document.getElementById([hiddencountryvalue]).value = 'VA';
                document.getElementById([pincode]).value = '379';
                autocomp.style.display = "none";
            }
            else if (countryName == "Venezuela")
            {
                document.getElementById([hiddencountryvalue]).value = 'VE';
                document.getElementById([pincode]).value = '058';
                autocomp.style.display = "none";
            }
            else if (countryName == "Vietnam")
            {
                document.getElementById([hiddencountryvalue]).value = 'VN';
                document.getElementById([pincode]).value = '084';
                autocomp.style.display = "none";
            }
            else if(countryName=="Wales")
            {
                document.getElementById([hiddencountryvalue]).value = 'WL';
                document.getElementById([pincode]).value = '044';
                autocomp.style.display = "none";
            }
            else if (countryName == "Wallis and Futuna")
            {
                document.getElementById([hiddencountryvalue]).value = 'WF';
                document.getElementById([pincode]).value = '681';
                autocomp.style.display = "none";
            }
            else if (countryName == "Western Sahara")
            {
                document.getElementById([hiddencountryvalue]).value = 'EH';
                document.getElementById([pincode]).value = '212';
                autocomp.style.display = "none";
            }
            else if (countryName == "Yemen")
            {
                document.getElementById([hiddencountryvalue]).value = 'YE';
                document.getElementById([pincode]).value = '967';
                autocomp.style.display = "none";
            }
            else if (countryName == "Zambia")
            {
                document.getElementById([hiddencountryvalue]).value = 'ZM';
                document.getElementById([pincode]).value = '260';
                autocomp.style.display = "none";
            }
            else if (countryName == "Zimbabwe")
                {
                    document.getElementById([hiddencountryvalue]).value = 'ZW';
                    document.getElementById([pincode]).value = '263';
                    document.getElementById([hiddenpincode]).value = '263';
                    autocomp.style.display = "none";
                }
                else
                {
                    //alert("NO its Some other country");
                    document.getElementById([countryvalue]).value = '';
                    document.getElementById([hiddencountryvalue]).value = '';
                    document.getElementById([pincode]).value = '';
                    document.getElementById([hiddenpincode]).value = '';
                    autocomp.style.display = "none";
                }

        }

        //This Functionality is used for checkout template changes
        //onload
        function colorPicker(first, to)
        {
            console.log("first ++++" + first);
            console.log("to ++++" + to);
            $("#" + to).val($("#" + first).val());
            var isTemplateFlag =  $("#isTemplate").val();
            if(isTemplateFlag == "Y")
            {
                if (first == "NEW_CHECKOUT_BODYNFOOTER_COLOR_picker" || first == "NEW_CHECKOUT_BODYNFOOTER_COLOR")
                {
                    var current = "display:none;";
                    if($("#" + first).val() != "")
                    {
                        if ($("#cardinfo").hasClass("activeTab"))
                        {
                            $(".tab-pane").attr('style','background-color:' + $("#" + first).val() + ' !important;');
                            $("#CreditCards").css("display","inline-block");
                            $("#cardinfo").show();
                            $("#personalinfo").hide();
                        }
                        else if ($("#personalinfo").hasClass("activeTab"))
                        {
                            $(".tab-pane").attr('style','background-color:' + $("#" + first).val() + ' !important;');
                            $("#CreditCards").css("display","inline-block");
                            $("#cardinfo").hide();
                            $("#personalinfo").show();
                        }
                        else
                        {
                            $(".tab-pane").attr('style','background-color:' + $("#" + first).val() + ' !important;');
                            $("#CreditCards").css("display","none");
                        }
                        $(".footer-tab").attr('style','background-color:' + $("#" + first).val() + ' !important;');
                        $("#personalinfo .form-control").attr('style', 'background-color:' + $("#" + first).val() + ' !important;');
                        $("#cardinfo .form-control").attr('style', 'background-color:' + $("#" + first).val() + ' !important;');
                    }else{
                        if ($("#cardinfo").hasClass("activeTab"))
                        {
                            $(".tab-pane").attr('style','background-color:#fff !important;');
                            $("#CreditCards").css("display","inline-block");
                            $("#cardinfo").show();
                            $("#personalinfo").hide();
                        }
                        else if ($("#personalinfo").hasClass("activeTab"))
                        {
                            $(".tab-pane").attr('style','background-color:#fff !important;');
                            $("#CreditCards").css("display","inline-block");
                            $("#cardinfo").hide();
                            $("#personalinfo").show();
                        }
                        else
                        {
                            $(".tab-pane").attr('style','background-color:#fff !important;');
                            $("#CreditCards").css("display","none");
                        }
                        $(".footer-tab").attr('style', 'background-color:#fff !important;');
                        $("#personalinfo .form-control").attr('style', 'background-color: #fff!important;');
                        $("#cardinfo .form-control").attr('style', 'background-color: #fff!important;');
                    }
                }
                else if (first == "NEW_CHECKOUT_HEADERBACKGROUND_COLOR_picker" || first == "NEW_CHECKOUT_HEADERBACKGROUND_COLOR")
                {
                    var current = $('#NEW_CHECKOUT_HEADER_FONT_COLOR').val();
                    if($("#" + first).val() != "")
                    {
                        $(".header").attr('style', 'background-color:' + $("#" + first).val() + ' !important;color:' + current + ' !important;');
                    }else{
                        $(".header").attr('style', 'background-color: #7eccad!important;color:' + current + ' !important;');
                    }
                }
                else if (first == "NEW_CHECKOUT_HEADER_FONT_COLOR_picker" || first == "NEW_CHECKOUT_HEADER_FONT_COLOR")
                {
                    var current = $('#NEW_CHECKOUT_HEADERBACKGROUND_COLOR').val();
                    if($("#" + first).val() != "")
                    {
                        $(".header").attr('style', 'color:' + $("#" + first).val() + ' !important;background-color:' + current + ' !important;');
                    }else
                    {
                        $(".header").attr('style', 'color:#fff !important;background-color:' + current + ' !important;');
                    }
                }
                else if (first == "NEW_CHECKOUT_NAVIGATIONBAR_COLOR_picker" || first == "NEW_CHECKOUT_NAVIGATIONBAR_COLOR")
                {
                    var current = $('#NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR').val();
                    if($("#" + first).val() != "")
                    {
                        $(".top-bar").attr('style', 'background-color:' + $("#" + first).val() + ' !important;color:' + current + ' !important;');
                    }else
                    {
                        $(".top-bar").attr('style', 'background-color:#7eccad !important;color:' + current + ' !important;');
                    }
                }
                else if (first == "NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR_picker" || first == "NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR")
                {
                    var current = $('#NEW_CHECKOUT_NAVIGATIONBAR_COLOR').val();
                    //$(".top-bar").attr('style','color:'+$("#" + first).val()+' !important;');
                    if($("#" + first).val() != "")
                    {
                        $(".top-bar").attr('style', 'color:' + $("#" + first).val() + ' !important;background-color:' + current + ' !important;');
                    }else
                    {
                        $(".top-bar").attr('style', 'color:#000000 !important;background-color:' + current + ' !important;');
                    }
                }
                else if (first == "NEW_CHECKOUT_BUTTON_FONT_COLOR_picker" || first == "NEW_CHECKOUT_BUTTON_FONT_COLOR")
                {
                    var current = $('#NEW_CHECKOUT_BUTTON_COLOR').val();
                    if($("#" + first).val() != "")
                    {
                        $(".pay-button").attr('style', 'color:' + $("#" + first).val() + ' !important;background-color:' + current + ' !important;');
                    }else
                    {
                        $(".pay-button").attr('style', 'color:#000000 !important;background-color:' + current + ' !important;');
                    }
                }
                else if (first == "NEW_CHECKOUT_BUTTON_COLOR_picker" || first == "NEW_CHECKOUT_BUTTON_COLOR")
                {
                    var current = $('#NEW_CHECKOUT_BUTTON_FONT_COLOR').val();
                    var current1 = "padding-top: 8px; padding-left: 5px; float: left; width: 20px; height: 36px; cursor: pointer;"
                    if($("#" + first).val() != "")
                    {
                        $(".pay-button").attr('style', 'background-color:' + $("#" + first).val() + ' !important;color:' + current + ' !important;');
                        if ($("#cardinfo").hasClass("activeTab") || $("#personalinfo").hasClass("activeTab"))
                        {
                            $(".prev-btn").attr('style', current1 + 'background-color:' + $("#" + first).val() + ' !important;color:' + current + ' !important;');
                        }
                    }else
                    {
                        {
                            $(".pay-button").attr('style', 'background-color:transparent !important;color:' + current + ' !important;');
                            if ($("#cardinfo").hasClass("activeTab") || $("#personalinfo").hasClass("activeTab"))
                            {
                                $(".prev-btn").attr('style', current1 + 'background-color:#7eccad  !important;color:' + current + ' !important;');
                            }
                        }
                    }
                }

                else if (first == "NEW_CHECKOUT_FULLBACKGROUND_COLOR_picker" || first == "NEW_CHECKOUT_FULLBACKGROUND_COLOR")
                {
                    var current = "font-family: Montserrat;"
                    if($("#" + first).val() != "")
                    {
                        $(".background").attr('style', current + 'background-color:' + $("#" + first).val() + ' !important;');
                    }else
                    {
                        $(".background").attr('style', current + 'background-color:#fff !important;');
                    }
                }
                else if (first == "NEW_CHECKOUT_LABEL_FONT_COLOR_picker" || first == "NEW_CHECKOUT_LABEL_FONT_COLOR")
                {
                    var consentDisplay = $("#consentdisplay").val();
                    var current1 = "margin-left: 12px;margin-right: 12px;margin-bottom: 55px;visibility:inherit;";
                    var current2 = "margin-left: 12px;margin-right: 12px;margin-bottom: 55px;visibility:hidden";
                    if($("#" + first).val() != "")
                    {
                        $(".label-style").attr('style', 'color:' + $("#" + first).val() + ' !important;');
                        $(".form-label").attr('style', 'color:' + $("#" + first).val() + ' !important;');
                    }else{
                        $(".label-style").attr('style', 'color:#000000 !important;');
                        $(".form-label").attr('style', 'color:#000000 !important;');
                    }


                        if($("#" + first).val() != "")
                        {
                            if(consentDisplay == "Y")
                            {
                                $(".terms-checkbox").attr('style', 'color:' + $("#" + first).val() + ' !important;'+current1);
                            }else
                            {
                                $(".terms-checkbox").attr('style', current2 + 'color:' + $("#" + first).val() + ' !important;'+current2);
                            }
                        }else
                        {
                            if(consentDisplay == "Y")
                            {
                                $(".terms-checkbox").attr('style','color:#000000 !important;'+current1);
                            }else
                            {
                                $(".terms-checkbox").attr('style','color:#000000 !important;'+current2);
                            }
                        }

                }
                else if (first == "NEW_CHECKOUT_BOX_SHADOW_picker" || first == "NEW_CHECKOUT_BOX_SHADOW")
                {
                    if($("#" + first).val() != "")
                    {
                        $(".mainDiv").attr('style', 'box-shadow: 0px 0px 20px ' + $("#" + first).val() + ' !important;');
                    }else
                    {
                        $(".mainDiv").attr('style', 'box-shadow: 0px 0px 20px rgba(0, 0, 0, 0.5) !important;');
                    }
                }
                else if (first == "NEW_CHECKOUT_TIMER_COLOR_picker" || first == "NEW_CHECKOUT_TIMER_COLOR")
                {
                    /*var current = "width: 100%;padding: 7px 12px;display: inline-flex;"*/

                        if($("#" + first).val() != "")
                        {
                            $(".timeout").attr('style', 'color:' + $("#" + first).val() + ' !important;');
                        }else
                        {
                            $(".timeout").attr('style', 'color:#000000 !important;');
                        }

                }
                else if (first == "NEW_CHECKOUT_ICON_COLOR_picker" || first == "NEW_CHECKOUT_ICON_COLOR")
                {
                    if($("#" + first).val() != "")
                    {
                        $(".tab-icon").attr('style', 'color:' + $("#" + first).val() + ' !important;');
                    }else
                    {
                        $(".tab-icon").attr('style', 'color: #3583D2 !important;');
                    }
                }
                else if(first == "NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR_picker" || first == "NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR")
                {
                    var current = $('#NEW_CHECKOUT_FOOTER_FONT_COLOR').val();
                    var flagSupportSection = $("[name=supportSection]").val();
                    var current1 = "width: 100%;padding: 7px 12px;display: inline-flex;"
                    if($("#" + first).val() != "")
                    {
                        $(".modal-footer").attr('style', current1 + 'background-color:' + $("#" + first).val() + ' !important;');
                    }
                    else{
                        $(".modal-footer").attr('style', current1 + 'background-color:#7eccad !important;');
                    }

                        if($("#" + first).val() != "")
                        {
                            $(".merchant-details").attr('style', 'background-color:' + $("#" + first).val() + ' !important;color:' + current + ' !important;');
                        }else
                        {
                            $(".merchant-details").attr('style', 'background-color:#3583d2 !important;color:' + current + ' !important;');
                        }

                }
                else if(first == "NEW_CHECKOUT_FOOTER_FONT_COLOR_picker" || first == "NEW_CHECKOUT_FOOTER_FONT_COLOR")
                {
                    var flagSupportSection = $("[name=supportSection]").val();
                    var current = $('#NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR').val();

                        if($("#" + first).val() != "")
                        {
                            $(".merchant-details").attr('style', 'color:' + $("#" + first).val() + ' !important;background-color:' + current + ' !important;');
                        }else
                        {
                            $(".merchant-details").attr('style', 'color:#fff !important;background-color:' + current + ' !important;');

                        }

                    /*var current1 = "width: 100%;padding: 7px 12px;display: inline-flex;"
                     $(".modal-footer").attr('style',current1+'background-color:'+$("#" + first).val()+' !important;');*/
                }
            }
        }




        function PaymentOptionHideShow(method, engLabel)
        {
            pay_methods = engLabel;
            label = method;
            var no_of_options = $("#" + pay_methods).find(".tabs-li-wallet");

            if (pay_methods == 'CreditCards')
            {

                showTab(0, 'CreditCards');
                /*cardInfo('CreditCards');*/
            }

            document.getElementById('payMethodDisplay').innerText = label;
            document.getElementById('payMethod').innerText = pay_methods;
            $("#topRight").show();
            $("#backArrow").show();
            $("#options").hide();
            /* $('#' + pay_methods).addClass('active');
             $("#" + pay_methods + "Option").show();

             backButtonConfig();*/
        }
        var  currentTab = 0;
        function showTab(n, method)
        {
            console.log("showTab >>> "+n, method);
            $("#CreditCards").css("display","inline-block");
            $("#personalinfo").show();
            if($("#personalinfo").is(":visible"))
            {
                $("#personalinfo").addClass("activeTab");
                $("#personalinfo").show();
                $("#cardinfo").hide();
                $("#terms").hide();
                $("#paybutton").show();
                $("#backArrow").show();
                $("#payLabel").text("Next");
            }else
            {

                $("#personalinfo").removeClass("activeTab");
                $("#cardinfo").addClass("activeTab");
                $("#personalinfo").hide();
                $("#cardinfo").show();
                $("#paybutton").show();
                $("#terms").show();
                $("#backArrow").show();
                $("#payLabel").text("PAY  $50.00");
            }

        }

        function nextPrev()
        {
            $("#personalinfo").removeClass("activeTab");
            $("#cardinfo").addClass("activeTab");
            $("#personalinfo").hide();
            $("#cardinfo").show();
            $("#terms").show();
            $("#payLabel").text("PAY  $50.00");
        }

        function back()
        {
            if($("#cardinfo").hasClass("activeTab"))
            {
                $("#personalinfo").show();
                if($("#personalinfo").is(":visible"))
                {
                    $("#cardinfo").hide();
                    $("#personalinfo").show();
                    $("#cardinfo").removeClass("activeTab");
                    $("#personalinfo").addClass("activeTab");
                    $("#payLabel").text("Next");
                    $("#terms").hide();
                }
                else
                {
                    $("#payMethodDisplay").text("Select a Payment Method");
                    $("#cardinfo").hide();
                    $("#personalinfo").hide();
                    $("#options").show();
                    $("#personalinfo").removeClass("activeTab");
                    $("#cardinfo").removeClass("activeTab");
                    $("#backArrow").hide();
                    $("#paybutton").hide();
                    $("#terms").hide();
                }
            }
            else if($("#personalinfo").hasClass("activeTab"))
            {
                $("#payMethodDisplay").text("Select a Payment Method");
                $("#cardinfo").hide();
                $("#personalinfo").hide();
                $("#options").show();
                $("#personalinfo").removeClass("activeTab");
                $("#cardinfo").removeClass("activeTab");
                $("#backArrow").hide();
                $("#paybutton").hide();
                $("#terms").hide();
            }
        }
        var timerOn = true;


        //Send OTP functionality

        function disableResend1()
        {
           /* $('#cancleOtpPopup').css('pointer-events', 'none');
            $('#cancleOtpPopup').css('cursor', 'auto');
            $('#cancleOtpPopup').prop('disabled', 'true');*/
            timer1(60);
        }

        //Function to set timer for resend
        function timer1(remaining) {
            //$("#cancleOtpPopup").css("display","none");
            var m = Math.floor(remaining / 60);
            var s = remaining % 60;

            m = m < 10 ? '0' + m : m;
            s = s < 10 ? '0' + s : s;
            document.getElementById('mainTimer').innerHTML = "Resend OTP In: <b>" +m + ':' + s+"</b>";
            $('#regenerateOTPText').css('pointer-events', 'none');
            $('#regenerateOTPText').css('cursor', 'auto');
            $('#regenerateOTPText').css('opacity', '0.5');
            $('#regenerateOTPText').prop('disabled', 'true');

            //document.getElementById('regenerateOTPText').innerHTML = "";
            remaining -= 1;

            if(remaining >= 0 && timerOn) {
                setTimeout(function() {
                    timer1(remaining);
                }, 1000);
                return;
            }

            if(!timerOn) {
                // Do validate stuff here
                return;
            }

            // Do timeout stuff here
            console.log('Timeout for otp');
            $("#messageOTP").text("");
            //$("#cancleOtpPopup").css("display","block");
            document.getElementById('mainTimer').innerHTML="";
            $('#regenerateOTPText').css('pointer-events', 'inherit');
            $('#regenerateOTPText').css('cursor', 'pointer');
            $('#regenerateOTPText').css('opacity', '1');
            $('#regenerateOTPText').removeAttr("disabled");
            //document.getElementById('regenerateOTPText').innerHTML = "Resend OTP"


        }

        //Function to resend OTP
        function regenerateOTP1()
        {
            disableResend1();
            $("#messageOTP").text("");
            sendOTP1();
        }

        function verifyOTP1()
        {
            var userName = $("#userName").val();
            var partnerId = $("#partId").val();
            var skey = $("#clkey").val();
            var merchantID = $("#memberId").val();
            var phone =$("#phonenophonecc").val();
            var merchantTransactionId = $("#memberTransactionId").val();
            var email="";
            var emailOtp="0";
            var mobileOtp = $("#fisrtM").val()+$("#secondM").val()+$("#thirdM").val()+$("#fourthM").val()+$("#fifthM").val()+$("#sixthM").val();
            console.log("mobileOtp++++"+mobileOtp);
            sendOTPData = {
                "merchant.mobilenumber":phone,
                "merchant.email":email,
                "merchant.smsotp":mobileOtp,
                "merchant.emailotp":emailOtp,
                "merchant.merchanttransactionid": merchantTransactionId,
                "authentication.memberId":merchantID
            }
            $.ajax({
                url: "/merchantServices/api/v1/authToken",
                type: 'post',
                async: true,
                data: {
                    "merchant.username": userName,
                    "authentication.partnerId": partnerId,
                    "authentication.sKey": skey
                },
                success: function (data) {
                    console.log("inside success otp",data);
                    console.log("inside success otp authToken",data.AuthToken);
                    authToken = data.AuthToken;
                    if(authToken != "")
                    {
                        $.ajax({
                            url: "/merchantServices/api/v1/verifyLoginMerchantOtp",
                            headers: {
                                "AuthToken": authToken
                            },
                            type: 'post',
                            async: false,
                            data: sendOTPData,
                            success: function (data)
                            {
                                if(data.result.description == undefined)
                                {
                                    $("#messageOTP").text("Please Retry login.");
                                }
                                console.log("inside success verifyyy otp", data.result.description);
                                var description = data.result.description;
                                $("#messageOTP").text(description);
                                if(data.result.description == "Successful OTP verification")
                                {
                                    $("#otpVerify").submit();
                                }
                                else if(data.result.description == "Please generate new OTP.")
                                {
                                    $("#messageOTP").text("Please generate new OTP.");
                                    $('#submit1').css('pointer-events', 'none');
                                    $('#submit1').css('cursor', 'auto');
                                    $('#submit1').css('opacity', '0.5');
                                }
                                else
                                {
                                    $("#messageOTP").text("OTP Verification Failed.");
                                }
                            },
                            error: function ()
                            {
                                console.log("inside error send otp")
                                // return false;
                            }
                        });
                    }
                },
                error: function () {
                    console.log("inside error verify otp");
                    //return false;
                }
            });

        }

        //Send OTP
        function sendOTP1()
        {
            var userName = $("#userName").val();
            var partnerId = $("#partId").val();
            var skey = $("#clkey").val();
            var merchantID = $("#memberId").val();
            var merchantTransactionId = $("#memberTransactionId").val();
            var phone =$("#phonenophonecc").val();
            var email="";
            sendOTPData = {
                "merchant.mobilenumber":phone,
                "merchant.email":email,
                "merchant.merchanttransactionid": merchantTransactionId,
                "authentication.memberId": merchantID
            }
            $.ajax({
                url: "/merchantServices/api/v1/authToken",
                type: 'post',
                async: false,
                data: {
                    "merchant.username": userName,
                    "authentication.partnerId": partnerId,
                    "authentication.sKey": skey
                },
                success: function (data) {
                    console.log("inside success otp",data);
                    console.log("inside success otp authToken",data.AuthToken);
                    authToken = data.AuthToken;
                    if(authToken != "")
                    {
                        $.ajax({
                            url: "/merchantServices/api/v1/createLoginMerchantOTP",
                            headers: {
                                "AuthToken": authToken
                            },
                            type: 'post',
                            async: false,
                            data: sendOTPData,
                            success: function (data)
                            {
                                if(data.result == undefined)
                                {
                                    $("#messageOTP").text("Please Retry login.");
                                }
                                if(description =="Exceed OTP Generation limit, Please try after an hour")
                                {
                                    $("#messageOTP").text("Exceed OTP Generation limit, Please Retry login after an hour");
                                    $('#regenerateOTPText').css('pointer-events', 'none');
                                    $('#regenerateOTPText').css('cursor', 'auto');
                                    $('#regenerateOTPText').css('opacity', '0.5');

                                    $('#submit1').css('pointer-events', 'none');
                                    $('#submit1').css('cursor', 'auto');
                                    $('#submit1').css('opacity', '0.5');
                                    $("#mainTimer").hide();
                                }
                                else
                                {
                                    console.log("inside success verifyyy otp", data.result.description);
                                    var description = data.result.description;
                                    $("#messageOTP").text(description);
                                    $('#submit1').css('pointer-events', 'inherit');
                                    $('#submit1').css('cursor', 'pointer');
                                    $('#submit1').css('opacity', '1');
                                }

                            },
                            error: function ()
                            {
                                console.log("inside error send otp")
                                // return false;
                            }
                        });
                    }
                },
                error: function () {
                    console.log("inside error verify otp");
                    //return false;
                }
            });
        }
        function inputInsideOtpInput(el) {
            var value = el.value;
            var numbers = value.replace(/[^0-9]/g, "");
            el.value = numbers;
            if (el.value.length > 1){
                el.value = el.value[el.value.length - 1];
            }
            try {
                if(el.value == null || el.value == ""){
                    this.foucusOnInput(el.previousElementSibling);
                }else {
                    this.foucusOnInput(el.nextElementSibling);
                }
            }catch (e) {
                console.log(e);
            }
        }

        function foucusOnInput(ele)
        {
            ele.focus();
            var val = ele.value;
            ele.value = "";
            // ele.value = val;
            setTimeout(function ()
            {
                ele.value = val;
            })
        }

