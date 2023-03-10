var state_options;

function StateLabel(inputId, hiddenId, stateId) {
    console.log("inside state label = ",inputId, hiddenId, stateId)
    pincodecc(inputId, hiddenId);
    var country_code = document.getElementById(hiddenId).value;

    switch (country_code)
    {
        case "AD":
            state_options = [
                {label: "Canillo", value: "2"}, {label: "Encamp", value: "3"}, {
                    label: "La Massana",
                    value: "4"
                }, {label: "Ordino", value: "5"},
                {label: "Sant Julià de Lòria", value: "6"}, {
                    label: "Andorra la Vella",
                    value: "7"
                }, {label: "Escaldes-Engordany", value: "8"}
            ];
            document.getElementById(stateId).innerHTML = "Parish";
            break;
        case "AE":
            state_options = [
                {label: "Ajman", value: "AJ"}, {label: "Abu Dhabi", value: "AZ"}, {
                    label: "Dubai",
                    value: "DU"
                }, {label: "Al Fujayrah", value: "FU"},
                {label: "Ra’s al Khaymah", value: "RK"}, {label: "Sharjah", value: "SH"}, {
                    label: "Umm al Qaywayn",
                    value: "UQ"
                }
            ];
            document.getElementById(stateId).innerHTML = "Emirate";
            break;
        case "AF":
            state_options = [
                {label: "Balkh", value: "BAL"}, {label: "Bamyan", value: "BAM"}, {
                    label: "Badghis",
                    value: "BDG"
                }, {label: "Badakhshan", value: "BDS"},
                {label: "Baghlan", value: "BGL"}, {label: "Daykundi", value: "DAY"}, {
                    label: "Farah",
                    value: "FRA"
                }, {label: "Faryab", value: "FYB"},
                {label: "Ghazni", value: "GHA"}, {label: "Ghor", value: "GHO"}, {
                    label: "Helmand",
                    value: "HEL"
                }, {label: "Herat", value: "HER"},
                {label: "Jowzjan", value: "JOW"}, {label: "Kabul", value: "KAB"}, {
                    label: "Kandahar",
                    value: "KAN"
                }, {label: "Kapisa", value: "KAP"},
                {label: "Kunduz", value: "KDZ"}, {label: "Khost", value: "KHO"}, {
                    label: "Kuna",
                    value: "KNR"
                }, {label: "Laghman", value: "LAG"},
                {label: "Logar", value: "LOG"}, {label: "Nangarhar", value: "NAN"}, {
                    label: "Nimroz",
                    value: "NIM"
                }, {label: "Nuristan", value: "NUR"},
                {label: "Panjshayr", value: "PAN"}, {label: "Parwan", value: "PAR"}, {
                    label: "Paktiya",
                    value: "PIA"
                }, {label: "Paktika", value: "PKA"},
                {label: "Samangan", value: "SAM"}, {label: "Sar-e Pul", value: "SAR"}, {
                    label: "Takhar",
                    value: "TAK"
                }, {label: "Uruzgan", value: "URU"},
                {label: "Wardak", value: "WAR"}, {label: "Zabul", value: "ZAB"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "AG":
            state_options = [
                {label: "Saint George", value: "3"}, {label: "Saint John", value: "4"}, {
                    label: "Saint Mary",
                    value: "5"
                }, {label: "Saint Paul", value: "6"},
                {label: "Saint Peter", value: "7"}, {label: "Saint Philip", value: "8"}, {
                    label: "Barbuda",
                    value: "10"
                }, {label: "Redonda", value: "11"}
            ];
            document.getElementById(stateId).innerHTML = "Parish";
            break;
        case "AL":
            state_options = [
                {label: "Berat", value: "1"}, {label: "Durrës", value: "2"}, {
                    label: "Elbasan",
                    value: "3"
                }, {label: "Fier", value: "4"},
                {label: "Gjirokastër", value: "5"}, {label: "Korçë", value: "6"}, {
                    label: "Kukës",
                    value: "7"
                }, {label: "Lezhë", value: "8"},
                {label: "Dibër", value: "9"}, {label: "Shkodër", value: "10"}, {
                    label: "Tiranë",
                    value: "11"
                }, {label: "Vlorë", value: "12"}
            ];
            document.getElementById(stateId).innerHTML = "County";
            break;
        case "AM":
            state_options = [
                {label: "Aragac?otn", value: "AG"}, {label: "Ararat", value: "AR"}, {
                    label: "Armavir",
                    value: "AV"
                }, {label: "Erevan", value: "ER"},
                {label: "Gegark'unik'", value: "GR"}, {label: "Kotayk'", value: "KT"}, {
                    label: "Lo?i",
                    value: "LO"
                }, {label: "Širak", value: "SH"},
                {label: "Syunik'", value: "SU"}, {label: "Tavuš", value: "TV"}, {label: "Vayoc Jor", value: "VD"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "AO":
            state_options = [
                {label: "Bengo", value: "BGO"}, {label: "Benguela", value: "BGU"}, {
                    label: "Bié",
                    value: "BIE"
                }, {label: "Cabinda", value: "CAB"},
                {label: "Kuando Kubango", value: "CCU"}, {label: "Cunene'", value: "CNN"}, {
                    label: "Kwanza Norte",
                    value: "CNO"
                }, {label: "Kwanza Sul", value: "CUS"},
                {label: "Huambo", value: "HUA"}, {label: "Huíla", value: "HUI"}, {
                    label: "Lunda Norte",
                    value: "LNO"
                }, {label: "Lunda Sul", value: "LSU"},
                {label: "Luanda", value: "LUA"}, {label: "Malange", value: "MAL"}, {
                    label: "Moxico",
                    value: "MOX"
                }, {label: "Namibe", value: "NAM"},
                {label: "Uíge", value: "UIG"}, {label: "Zaire", value: "ZAI"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "AR":
            state_options = [
                {label: "Salta", value: "A"}, {
                    label: "Buenos Aires",
                    value: "B"
                }, {label: "Ciudad Autónoma de Buenos Aires", value: "C"}, {label: "San Luis", value: "D"},
                {label: "Entre Ríos", value: "E"}, {label: "La Rioja'", value: "F"}, {
                    label: "Santiago del Estero",
                    value: "G"
                }, {label: "Chaco", value: "H"},
                {label: "San Juan", value: "J"}, {label: "Catamarca", value: "K"}, {
                    label: "La Pampa",
                    value: "L"
                }, {label: "Mendoza", value: "M"},
                {label: "Misiones", value: "N"}, {label: "Formosa", value: "P"}, {
                    label: "Neuquén",
                    value: "Q"
                }, {label: "Río Negro", value: "R"},
                {label: "Santa Fe", value: "S"}, {label: "Tucumán", value: "T"}, {
                    label: "Chubut",
                    value: "U"
                }, {label: "Tierra del Fuego", value: "V"},
                {label: "Corrientes", value: "W"}, {label: "Córdoba", value: "X"}, {
                    label: "Jujuy",
                    value: "Y"
                }, {label: "Santa Cruz", value: "Z"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "AT":
            state_options = [
                {label: "Burgenland", value: "1"}, {label: "Kärnten", value: "2"}, {
                    label: "Niederösterreich",
                    value: "3"
                }, {label: "Oberösterreich", value: "4"},
                {label: "Salzburg", value: "5"}, {label: "Steiermark", value: "6"}, {
                    label: "Tirol",
                    value: "7"
                }, {label: "Vorarlberg", value: "8"},
                {label: "Wien", value: "9"}
            ];
            document.getElementById(stateId).innerHTML = "State";
            break;
        case "AU":
            state_options = [
                {label: "Australian Capital Territory", value: "ACT"}, {
                    label: "New South Wales",
                    value: "NSW"
                }, {label: "Northern Territory", value: "NT"}, {label: "Queensland", value: "QLD"},
                {label: "South Australia", value: "SA"}, {label: "Tasmania", value: "TAS"}, {
                    label: "Victoria",
                    value: "VIC"
                }, {label: "Western Australia", value: "WA"}
            ];
            document.getElementById(stateId).innerHTML = "State";
            break;
        case "AZ":
            state_options = [
                {label: "Oguz", value: "OGU"}, {label: "Ordubad", value: "ORD"}, {
                    label: "Q?b?l?",
                    value: "QAB"
                }, {label: "Qax", value: "QAX"},
                {label: "Qazax", value: "QAZ"}, {label: "Quba", value: "QBA"}, {
                    label: "Qubadli",
                    value: "QBI"
                }, {label: "Qobustan", value: "QOB"},
                {label: "Qusar", value: "QUS"}, {label: "S?ki", value: "SAK"}, {
                    label: "Sabirabad",
                    value: "SAB"
                }, {label: "S?d?r?k", value: "SAD"},
                {label: "Sahbuz", value: "SAH"}, {label: "Salyan", value: "SAL"}, {
                    label: "S?rur",
                    value: "SAR"
                }, {label: "Saatli", value: "SAT"},
                {label: "Sabran", value: "SBN"}, {label: "Siy?z?n", value: "SIY"}, {
                    label: "Sumqayit",
                    value: "SM"
                }, {label: "Samaxi", value: "SMI"},
                {label: "Samux", value: "SMX"}, {label: "Sirvan", value: "SR"}, {
                    label: "Susa",
                    value: "SUS"
                }, {label: "T?rt?r", value: "TAR"},
                {label: "Tovuz", value: "TOV"}, {label: "Ucar", value: "UCA"}, {
                    label: "Xank?ndi",
                    value: "XA"
                }, {label: "Xaçmaz", value: "XAC"},
                {label: "Xocali", value: "XCI"}, {label: "Xizi", value: "XIZ"}, {
                    label: "Xocav?nd",
                    value: "XVD"
                }, {label: "Yardimli", value: "YAR"},
                {label: "Yevlax", value: "YEV"}, {label: "Z?ngilan", value: "ZAN"}, {
                    label: "Zaqatala",
                    value: "ZAQ"
                }, {label: "Z?rdab", value: "ZAR"},
                {label: "Abseron", value: "ABS"}, {label: "Agstafa", value: "AGA"}, {
                    label: "Agcab?di",
                    value: "AGC"
                }, {label: "Agdam", value: "AGM"},
                {label: "Agdas", value: "AGS"}, {label: "Agsu", value: "AGU"}, {
                    label: "Astara",
                    value: "AST"
                }, {label: "Baki", value: "BA"},
                {label: "Bab?k", value: "BAB"}, {label: "Balak?n", value: "BAL"}, {
                    label: "B?rd?",
                    value: "BAR"
                }, {label: "Beyl?qan", value: "BEY"},
                {label: "Bil?suvar", value: "BIL"}, {label: "C?brayil", value: "CAB"}, {
                    label: "C?lilabad",
                    value: "CAL"
                }, {label: "Culfa", value: "CUL"},
                {label: "Dask?s?n", value: "DAS"}, {label: "Füzuli", value: "FUZ"}, {
                    label: "G?nc?",
                    value: "GA"
                }, {label: "G?d?b?y", value: "GAD"},
                {label: "Goranboy", value: "GOR"}, {label: "Göyçay", value: "GOY"}, {
                    label: "Göygöl",
                    value: "GYG"
                }, {label: "Haciqabul", value: "HAC"},
                {label: "Imisli", value: "IMI"}, {label: "Ismayilli", value: "ISM"}, {
                    label: "K?lb?c?r",
                    value: "KAL"
                }, {label: "K?ng?rli", value: "KAN"},
                {label: "Kürd?mir", value: "KUR"}, {label: "L?nk?ran", value: "LA"}, {
                    label: "Laçin",
                    value: "LAC"
                }, {label: "L?nk?ran", value: "LAN"},
                {label: "Lerik", value: "LER"}, {label: "Masalli", value: "MAS"}, {
                    label: "Ming?çevir",
                    value: "MI"
                }, {label: "Naftalan", value: "NA"},
                {label: "Neftçala", value: "NEF"}, {label: "Naxçivan", value: "NV"}, {label: "Naxçivan", value: "NX"}
            ];
            document.getElementById(stateId).innerHTML = "Rayon";
            break;
        case "BA":
            state_options = [
                {label: "Federacija Bosne i Hercegovine", value: "BIH"}, {
                    label: "Brcko distrikt",
                    value: "BRC"
                }, {label: "Republika Srpska", value: "SRP"}
            ];
            document.getElementById(stateId).innerHTML = "Entity";
            break;
        case "BB":
            state_options = [
                {label: "Christ Church", value: "1"}, {label: "Saint Andrew", value: "2"}, {
                    label: "Saint George",
                    value: "3"
                }, {label: "Saint James", value: "4"},
                {label: "Saint John", value: "5"}, {label: "Saint Joseph", value: "6"}, {
                    label: "Saint Lucy",
                    value: "7"
                }, {label: "Saint Michael", value: "8"},
                {label: "Saint Peter", value: "9"}, {label: "Saint Philip", value: "10"}, {
                    label: "Saint Thomas",
                    value: "11"
                }
            ];
            document.getElementById(stateId).innerHTML = "Parish";
            break;
        case "BD":
            state_options = [
                {label: "Bandarban", value: "1"}, {label: "Barguna", value: "2"}, {
                    label: "Bogra",
                    value: "3"
                }, {label: "Brahmanbaria", value: "4"},
                {label: "Bagerhat", value: "5"}, {label: "Barisal", value: "6"}, {
                    label: "Bhola",
                    value: "7"
                }, {label: "Comilla", value: "8"},
                {label: "Chandpur", value: "9"}, {label: "Chittagong", value: "10"}, {
                    label: "Cox's Bazar",
                    value: "11"
                }, {label: "Chuadanga", value: "12"},
                {label: "Dhaka", value: "13"}, {label: "Dinajpur", value: "14"}, {
                    label: "Faridpur",
                    value: "15"
                }, {label: "Feni", value: "16"},
                {label: "Gopalganj", value: "17"}, {label: "Gazipur", value: "18"}, {
                    label: "Gaibandha",
                    value: "19"
                }, {label: "Habiganj", value: "20"},
                {label: "Jamalpur", value: "21"}, {label: "Jessore", value: "22"}, {
                    label: "Jhenaidah",
                    value: "23"
                }, {label: "Joypurhat", value: "24"},
                {label: "Jhalakathi", value: "25"}, {label: "Kishoreganj", value: "26"}, {
                    label: "Khulna",
                    value: "27"
                }, {label: "Kurigram", value: "28"},
                {label: "Khagrachhari", value: "29"}, {label: "Kushtia", value: "30"}, {
                    label: "Lakshmipur",
                    value: "31"
                }, {label: "Lalmonirhat", value: "32"},
                {label: "Manikganj", value: "33"}, {label: "Mymensingh", value: "34"}, {
                    label: "Munshiganj",
                    value: "35"
                }, {label: "Madaripur", value: "36"},
                {label: "Magura", value: "37"}, {label: "Moulvibazar", value: "38"}, {
                    label: "Meherpur",
                    value: "39"
                }, {label: "Narayanganj", value: "40"},
                {label: "Netrakona", value: "41"}, {label: "Narsingdi", value: "42"}, {
                    label: "Narail",
                    value: "43"
                }, {label: "Natore", value: "44"},
                {label: "Chapai Nawabganj", value: "45"}, {label: "Nilphamari", value: "46"}, {
                    label: "Noakhali",
                    value: "47"
                }, {label: "Naogaon", value: "48"},
                {label: "Pabna", value: "49"}, {label: "Pirojpur", value: "50"}, {
                    label: "Patuakhali",
                    value: "51"
                }, {label: "Panchagarh", value: "52"},
                {label: "Rajbari", value: "53"}, {label: "Rajshahi", value: "54"}, {
                    label: "Rangpur",
                    value: "55"
                }, {label: "Rangamati", value: "56"},
                {label: "Sherpur", value: "57"}, {label: "Satkhira", value: "58"}, {
                    label: "Sirajganj",
                    value: "59"
                }, {label: "Sylhet", value: "60"},
                {label: "Sunamganj", value: "61"}, {label: "Shariatpur", value: "62"}, {
                    label: "Tangail",
                    value: "63"
                }, {label: "Thakurgaon", value: "64"}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "BE":
            state_options = [
                {label: "Bruxelles-Capitale, Région de", value: "BRU"}, {
                    label: "Antwerpen",
                    value: "VAN"
                }, {label: "Vlaams Brabant", value: "VBR"}, {label: "Limburg", value: "VLI"},
                {label: "Oost-Vlaanderen", value: "VOV"}, {
                    label: "West-Vlaanderen",
                    value: "VWV"
                }, {label: "Brabant wallon", value: "WBR"}, {label: "Hainaut", value: "WHT"},
                {label: "Liège", value: "WLG"}, {label: "Luxembourg", value: "WLX"}, {label: "Namur", value: "WNA"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "BF":
            state_options = [
                {label: "Balé", value: "BAL"}, {label: "Bam", value: "BAM"}, {
                    label: "Banwa",
                    value: "BAN"
                }, {label: "Bazèga", value: "BAZ"},
                {label: "Bougouriba", value: "BGR"}, {label: "Boulgou", value: "BLG"}, {
                    label: "Boulkiemdé",
                    value: "BLK"
                }, {label: "Comoé", value: "COM"},
                {label: "Ganzourgou", value: "GAN"}, {label: "Gnagna", value: "GNA"}, {
                    label: "Gourma",
                    value: "GOU"
                }, {label: "Houet", value: "HOU"},
                {label: "Ioba", value: "IOB"}, {label: "Kadiogo", value: "KAD"}, {
                    label: "Kénédougou",
                    value: "KEN"
                }, {label: "Komondjari", value: "KMD"},
                {label: "Kompienga", value: "KMP"}, {label: "Koulpélogo", value: "KOP"}, {
                    label: "Kossi",
                    value: "KOS"
                }, {label: "Kouritenga", value: "KOT"},
                {label: "Kourwéogo", value: "KOW"}, {label: "Léraba", value: "LER"}, {
                    label: "Loroum",
                    value: "LOR"
                }, {label: "Mouhoun", value: "MOU"},
                {label: "Namentenga", value: "NAM"}, {label: "Nahouri", value: "NAO"}, {
                    label: "Nayala",
                    value: "NAY"
                }, {label: "Noumbiel", value: "NOU"},
                {label: "Oubritenga", value: "OUB"}, {label: "Oudalan", value: "OUD"}, {
                    label: "Passoré",
                    value: "PAS"
                }, {label: "Poni", value: "PON"},
                {label: "Séno", value: "SEN"}, {label: "Sissili", value: "SIS"}, {
                    label: "Sanmatenga",
                    value: "SMT"
                }, {label: "Sanguié", value: "SNG"},
                {label: "Soum", value: "SOM"}, {label: "Sourou", value: "SOR"}, {
                    label: "Tapoa",
                    value: "TAP"
                }, {label: "Tuy", value: "TUI"},
                {label: "Yagha", value: "YAG"}, {label: "Yatenga", value: "YAT"}, {
                    label: "Ziro",
                    value: "ZIR"
                }, {label: "Zondoma", value: "ZON"},
                {label: "Zoundwéogo", value: "ZOU"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "BG":
            state_options = [
                {label: "Blagoevgrad", value: "1"}, {label: "Burgas", value: "2"}, {
                    label: "Varna",
                    value: "3"
                }, {label: "Veliko Tarnovo", value: "4"},
                {label: "Vidin", value: "5"}, {label: "Vratsa", value: "6"}, {
                    label: "Gabrovo",
                    value: "7"
                }, {label: "Dobrich", value: "8"},
                {label: "Kardzhali", value: "9"}, {label: "Kjustendil", value: "10"}, {
                    label: "Lovech",
                    value: "11"
                }, {label: "Montana", value: "12"},
                {label: "Pazardzik", value: "13"}, {label: "Pernik", value: "14"}, {
                    label: "Pleven",
                    value: "15"
                }, {label: "Plovdiv", value: "16"},
                {label: "Razgrad", value: "17"}, {label: "Ruse", value: "18"}, {
                    label: "Silistra",
                    value: "19"
                }, {label: "Sliven", value: "20"},
                {label: "Smolyan", value: "21"}, {label: "Sofia (stolitsa)", value: "22"}, {
                    label: "Sofia",
                    value: "23"
                }, {label: "Stara Zagora", value: "24"},
                {label: "Targovishte", value: "25"}, {label: "Haskovo", value: "26"}, {
                    label: "Shumen",
                    value: "27"
                }, {label: "Yambol", value: "28"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "BH":
            state_options = [
                {label: "Al ‘Asimah", value: "13"}, {label: "Al Janubiyah", value: "14"}, {
                    label: "Al Mu?arraq",
                    value: "15"
                }, {label: "Ash Shamaliyah", value: "17"}
            ];
            document.getElementById(stateId).innerHTML = "Governorat";
            break;
        case "BI":
            state_options = [
                {label: "Bubanza", value: "BB"}, {label: "Bujumbura Rural", value: "BL"}, {
                    label: "Bujumbura Mairie",
                    value: "BM"
                }, {label: "Bururi", value: "BR"},
                {label: "Cankuzo", value: "CA"}, {label: "Cibitoke", value: "CI"}, {
                    label: "Gitega",
                    value: "GI"
                }, {label: "Kirundo", value: "KI"},
                {label: "Karuzi", value: "KR"}, {label: "Kayanza", value: "KY"}, {
                    label: "Makamba",
                    value: "MA"
                }, {label: "Muramvya", value: "MU"},
                {label: "Mwaro", value: "MW"}, {label: "Muyinga", value: "MY"}, {
                    label: "Ngozi",
                    value: "NG"
                }, {label: "Rumonge", value: "RM"},
                {label: "Rutana", value: "RT"}, {label: "Ruyigi", value: "RY"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "BJ":
            state_options = [
                {label: "Atacora", value: "AK"}, {label: "Alibori", value: "AL"}, {
                    label: "Atlantique",
                    value: "AQ"
                }, {label: "Borgou", value: "BO"},
                {label: "Collines", value: "CO"}, {label: "Donga", value: "DO"}, {
                    label: "Couffo",
                    value: "KO"
                }, {label: "Littoral", value: "LI"},
                {label: "Mono", value: "MO"}, {label: "Ouémé", value: "OU"}, {
                    label: "Plateau",
                    value: "PL"
                }, {label: "Zou", value: "ZO"}
            ];
            document.getElementById(stateId).innerHTML = "Department";
            break;
        case "BN":
            state_options = [
                {label: "Belait", value: "BE"}, {label: "Brunei-Muara", value: "BM"}, {
                    label: "Temburong",
                    value: "TE"
                }, {label: "Tutong", value: "TU"}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "BO":
            state_options = [
                {label: "El Beni", value: "B"}, {label: "Cochabamba", value: "C"}, {
                    label: "Chuquisaca",
                    value: "H"
                }, {label: "La Paz", value: "L"},
                {label: "Pando", value: "N"}, {label: "Oruro", value: "O"}, {
                    label: "Potosí",
                    value: "P"
                }, {label: "Santa Cruz", value: "S"},
                {label: "Tarija", value: "T"}
            ];
            document.getElementById(stateId).innerHTML = "Department";
            break;
        case "BQ":
            state_options = [
                {label: "Bonaire", value: "BO"}, {label: "Saba", value: "SA"}, {label: "Sint Eustatius", value: "SE"}
            ];
            document.getElementById(stateId).innerHTML = "Special Municipality";
            break;
        case "BR":
            state_options = [
                {label: "Acre", value: "AC"}, {label: "Alagoas", value: "AL"}, {
                    label: "Amazonas",
                    value: "AM"
                }, {label: "Amapá", value: "AP"},
                {label: "Bahia", value: "BA"}, {label: "Ceará", value: "CE"}, {
                    label: "Distrito Federal",
                    value: "DF"
                }, {label: "Espírito Santo", value: "ES"},
                {label: "Goiás", value: "GO"}, {label: "Maranhão", value: "MA"}, {
                    label: "Minas Gerais",
                    value: "MG"
                }, {label: "Mato Grosso do Sul", value: "MS"},
                {label: "Mato Grosso", value: "MT"}, {label: "Pará", value: "PA"}, {
                    label: "Paraíba",
                    value: "PB"
                }, {label: "Pernambuco", value: "PE"},
                {label: "Piauí", value: "PI"}, {label: "Paraná", value: "PR"}, {
                    label: "Rio de Janeiro",
                    value: "RJ"
                }, {label: "Rio Grande do Norte", value: "RN"},
                {label: "Santa Catarina", value: "SC"}, {label: "Sergipe", value: "SE"}, {
                    label: "São Paulo",
                    value: "SP"
                }, {label: "Tocantins", value: "TO"}
            ];
            document.getElementById(stateId).innerHTML = "State";
            break;
        case "BS":
            state_options = [
                {label: "Acklins", value: "AK"}, {label: "Bimini", value: "BI"}, {
                    label: "Black Point",
                    value: "BP"
                }, {label: "Berry Islands", value: "BY"},
                {label: "Central Eleuthera", value: "CE"}, {
                    label: "Cat Island",
                    value: "CI"
                }, {label: "Crooked Island and Long Cay", value: "CK"}, {label: "Central Abaco", value: "CO"},
                {label: "Central Andros", value: "CS"}, {label: "East Grand Bahama", value: "EG"}, {
                    label: "Exuma",
                    value: "EX"
                }, {label: "City of Freeport", value: "FP"},
                {label: "Grand Cay", value: "GC"}, {label: "Harbour Island", value: "HI"}, {
                    label: "Hope Town",
                    value: "HT"
                }, {label: "Inagua", value: "IN"},
                {label: "Long Island", value: "LI"}, {label: "Mangrove Cay", value: "MC"}, {
                    label: "Mayaguana",
                    value: "MG"
                }, {label: "Moore's Island", value: "MI"},
                {label: "North Eleuthera", value: "NE"}, {label: "North Abaco", value: "NO"}, {
                    label: "North Andros",
                    value: "NS"
                }, {label: "Rum Cay", value: "RC"},
                {label: "Ragged Island", value: "RI"}, {label: "South Andros", value: "SA"}, {
                    label: "South Eleuthera",
                    value: "SE"
                }, {label: "South Abaco", value: "SO"},
                {label: "San Salvador", value: "SS"}, {
                    label: "Spanish Wells",
                    value: "SW"
                }, {label: "West Grand Bahama", value: "WG"}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "BT":
            state_options = [
                {label: "Paro", value: "11"}, {label: "Chhukha", value: "12"}, {
                    label: "Haa",
                    value: "13"
                }, {label: "Samtse", value: "14"},
                {label: "Thimphu", value: "15"}, {label: "Tsirang", value: "21"}, {
                    label: "Dagana",
                    value: "22"
                }, {label: "Punakha", value: "23"},
                {label: "Wangdue Phodrang", value: "24"}, {label: "Sarpang", value: "31"}, {
                    label: "Trongsa",
                    value: "32"
                }, {label: "Bumthang", value: "33"},
                {label: "Zhemgang", value: "34"}, {label: "Trashigang", value: "41"}, {
                    label: "Monggar",
                    value: "42"
                }, {label: "Pemagatshel", value: "43"},
                {label: "Lhuentse", value: "44"}, {label: "Samdrup Jongkhar", value: "45"}, {
                    label: "Gasa",
                    value: "GA"
                }, {label: "Trashi Yangtse", value: "TY"}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "BW":
            state_options = [
                {label: "Central", value: "CE"}, {label: "Chobe", value: "CH"}, {
                    label: "Francistown",
                    value: "FR"
                }, {label: "Gaborone", value: "GA"},
                {label: "Ghanzi", value: "GH"}, {label: "Jwaneng", value: "JW"}, {
                    label: "Kgalagadi",
                    value: "KG"
                }, {label: "Kgatleng", value: "KL"},
                {label: "Kweneng", value: "KW"}, {label: "Lobatse", value: "LO"}, {
                    label: "North East",
                    value: "NE"
                }, {label: "North West", value: "NW"},
                {label: "South East", value: "SE"}, {label: "Southern", value: "SO"}, {
                    label: "Selibe Phikwe",
                    value: "SP"
                }, {label: "Sowa Town", value: "ST"}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "BY":
            state_options = [
                {label: "Bresckaja voblasc(be)-Brestskaja oblast'(ru)", value: "BR"}, {
                    label: "Horad Minsk",
                    value: "HM"
                }, {
                    label: "Homyel'skaya voblasts'(be)-Gomel'skaya oblast'(ru)",
                    value: "HO"
                }, {label: "Hrodzenskaya voblasts'(be)-Grodnenskaya oblast'(ru)", value: "HR"},
                {
                    label: "Mahilyowskaya voblasts'(be)-Mogilevskaya oblast'(ru)",
                    value: "MA"
                }, {
                    label: "Minskaya voblasts'(be)-Minskaya oblast'(ru)",
                    value: "MI"
                }, {label: "Vitsyebskaya voblasts'(be)-Vitebskaya oblast'(ru)", value: "VI"}
            ];
            document.getElementById(stateId).innerHTML = "Oblast";
            break;
        case "BZ":
            state_options = [
                {label: "Belize", value: "BZ"}, {label: "Cayo", value: "CY"}, {
                    label: "Corozal",
                    value: "CZL"
                }, {label: "Orange Walk", value: "OW"},
                {label: "Stann Creek", value: "SC"}, {label: "Toledo", value: "TOL"}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "CA":
            state_options = [
                {label: "Alberta", value: "AB"}, {label: "British Columbia", value: "BC"}, {
                    label: "Manitoba",
                    value: "MB"
                }, {label: "New Brunswick", value: "NB"},
                {label: "Newfoundland and Labrador", value: "NL"}, {
                    label: "Nova Scotia",
                    value: "NS"
                }, {label: "Northwest Territories", value: "NT"}, {label: "Nunavut", value: "NU"},
                {label: "Ontario", value: "ON"}, {label: "Prince Edward Island", value: "PE"}, {
                    label: "Quebec",
                    value: "QC"
                }, {label: "Saskatchewan", value: "SK"},
                {label: "Yukon", value: "YT"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "CD":
            state_options = [
                {label: "Kongo Central", value: "BC"}, {label: "Bas-Uélé", value: "BU"}, {
                    label: "Équateur",
                    value: "EQ"
                }, {label: "Haut-Katanga", value: "HK"},
                {label: "Haut-Lomami", value: "HL"}, {label: "Haut-Uélé", value: "HU"}, {
                    label: "Ituri",
                    value: "IT"
                }, {label: "Kasaï Central", value: "KC"},
                {label: "Kasaï Oriental", value: "KE"}, {label: "Kwango", value: "KG"}, {
                    label: "Kwilu",
                    value: "KL"
                }, {label: "Kinshasa", value: "KN"},
                {label: "Kasaï", value: "KS"}, {label: "Lomami", value: "LO"}, {
                    label: "Lualaba",
                    value: "LU"
                }, {label: "Maniema", value: "MA"},
                {label: "Mai-Ndombe", value: "MN"}, {label: "Mongala", value: "MO"}, {
                    label: "Nord-Kivu",
                    value: "NK"
                }, {label: "Nord-Ubangi", value: "NU"},
                {label: "Sankuru", value: "SA"}, {label: "Sud-Kivu", value: "SK"}, {
                    label: "Sud-Ubangi",
                    value: "SU"
                }, {label: "Tanganyika", value: "TA"},
                {label: "Tshopo", value: "TO"}, {label: "Tshuapa", value: "TU"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "CF":
            state_options = [
                {label: "Ouham", value: "AC"}, {label: "Bamingui-Bangoran", value: "BB"}, {
                    label: "Bangui",
                    value: "BGF"
                }, {label: "Basse-Kotto", value: "BK"},
                {label: "Haute-Kotto", value: "HK"}, {label: "Haut-Mbomou", value: "HM"}, {
                    label: "Mambéré-Kadéï",
                    value: "HS"
                }, {label: "Gribingui", value: "KB"},
                {label: "Kémo-Gribingui", value: "KG"}, {label: "Lobaye", value: "LB"}, {
                    label: "Mbomou",
                    value: "MB"
                }, {label: "Ombella-Mpoko", value: "MP"},
                {label: "Nana-Mambéré", value: "NM"}, {label: "Ouham-Pendé", value: "OP"}, {
                    label: "Sangha",
                    value: "SE"
                }, {label: "Ouaka", value: "UK"},
                {label: "Vakaga", value: "VK"}
            ];
            document.getElementById(stateId).innerHTML = "Prefecture";
            break;
        case "CG":
            state_options = [
                {label: "Bouenza", value: "11"}, {label: "Pool", value: "12"}, {
                    label: "Sangha",
                    value: "13"
                }, {label: "Plateaux", value: "14"},
                {label: "Cuvette-Ouest", value: "15"}, {label: "Pointe-Noire", value: "16"}, {
                    label: "Lékoumou",
                    value: "2"
                }, {label: "Kouilou", value: "5"},
                {label: "Likouala", value: "7"}, {label: "Cuvette", value: "8"}, {
                    label: "Niari",
                    value: "9"
                }, {label: "Brazzaville", value: "BZV"}
            ];
            document.getElementById(stateId).innerHTML = "Department";
            break;
        case "CH":
            state_options = [
                {label: "Aargau (de)", value: "AG"}, {
                    label: "Appenzell Innerrhoden (de)",
                    value: "AI"
                }, {label: "Appenzell Ausserrhoden (de)", value: "AR"}, {label: "Bern (de)", value: "BE"},
                {label: "Basel-Landschaft (de)", value: "BL"}, {
                    label: "Basel-Stadt (de)",
                    value: "BS"
                }, {label: "Fribourg (fr)", value: "FR"}, {label: "Genève (fr)", value: "GE"},
                {label: "Glarus (de)", value: "GL"}, {label: "Graubünden (de)", value: "GR"}, {
                    label: "Jura (fr)",
                    value: "JU"
                }, {label: "Luzern (de)", value: "LU"},
                {label: "Neuchâtel (fr)", value: "NE"}, {label: "Nidwalden (de)", value: "NW"}, {
                    label: "Obwalden (de)",
                    value: "OW"
                }, {label: "Sankt Gallen (de)", value: "SG"},
                {label: "Schaffhausen (de)", value: "SH"}, {
                    label: "Solothurn (de)",
                    value: "SO"
                }, {label: "Schwyz (de)", value: "SZ"}, {label: "Thurgau (de)", value: "TG"},
                {label: "Ticino (it)", value: "TI"}, {label: "Uri (de)", value: "UR"}, {
                    label: "Vaud (fr)",
                    value: "VD"
                }, {label: "Valais (fr)", value: "VS"},
                {label: "Zug (de)", value: "ZG"}, {label: "Zürich (de)", value: "ZH"}
            ];
            document.getElementById(stateId).innerHTML = "Canton";
            break;
        case "CI":
            state_options = [
                {label: "Abidjan", value: "AB"}, {label: "Bas-Sassandra", value: "BS"}, {
                    label: "Comoé",
                    value: "CM"
                }, {label: "Denguélé", value: "DN"},
                {label: "Gôh-Djiboua", value: "GD"}, {label: "Lacs", value: "LC"}, {
                    label: "Lagunes",
                    value: "LG"
                }, {label: "Montagnes", value: "MG"},
                {label: "Sassandra-Marahoué", value: "SM"}, {
                    label: "Savanes",
                    value: "SV"
                }, {label: "Vallée du Bandama", value: "VB"}, {label: "Woroba", value: "WR"},
                {label: "Yamoussoukro", value: "YM"}, {label: "Zanzan", value: "ZZ"}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "CL":
            state_options = [
                {label: "Aisén del General Carlos Ibañez del Campo", value: "AI"}, {
                    label: "Antofagasta",
                    value: "AN"
                }, {label: "Arica y Parinacota", value: "AP"}, {label: "La Araucanía", value: "AR"},
                {label: "Atacama", value: "AT"}, {label: "Biobío", value: "BI"}, {
                    label: "Coquimbo",
                    value: "CO"
                }, {label: "Libertador General Bernardo O'Higgins", value: "LI"},
                {label: "Los Lagos", value: "LL"}, {label: "Los Ríos", value: "LR"}, {
                    label: "Magallanes",
                    value: "MA"
                }, {label: "Maule", value: "ML"},
                {label: "Región Metropolitana de Santiago", value: "RM"}, {
                    label: "Tarapacá",
                    value: "TA"
                }, {label: "Valparaíso", value: "VS"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "CM":
            state_options = [
                {label: "Adamaoua", value: "AD"}, {label: "Centre", value: "CE"}, {
                    label: "Far North",
                    value: "EN"
                }, {label: "East", value: "ES"},
                {label: "Littoral", value: "LT"}, {label: "North", value: "NO"}, {
                    label: "North-West",
                    value: "NW"
                }, {label: "West", value: "OU"},
                {label: "South", value: "SU"}, {label: "South-West", value: "SW"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "CN":
            state_options = [
                {label: "Beijing", value: "11"}, {label: "Tianjin", value: "12"}, {
                    label: "Hebei",
                    value: "13"
                }, {label: "Shanxi", value: "14"},
                {label: "Nei Mongol (mn)", value: "15"}, {label: "Liaoning", value: "21"}, {
                    label: "Jilin",
                    value: "22"
                }, {label: "Heilongjiang", value: "23"},
                {label: "Shanghai", value: "31"}, {label: "Jiangsu", value: "32"}, {
                    label: "Zhejiang",
                    value: "33"
                }, {label: "Anhui", value: "34"},
                {label: "Fujian", value: "35"}, {label: "Jiangxi", value: "36"}, {
                    label: "Shandong",
                    value: "37"
                }, {label: "Henan", value: "41"},
                {label: "Hubei", value: "42"}, {label: "Hunan", value: "43"}, {
                    label: "Guangdong",
                    value: "44"
                }, {label: "Guangxi", value: "45"},
                {label: "Hainan", value: "46"}, {label: "Chongqing", value: "50"}, {
                    label: "Sichuan",
                    value: "51"
                }, {label: "Guizhou", value: "52"},
                {label: "Yunnan", value: "53"}, {label: "Xizang", value: "54"}, {
                    label: "Shaanxi",
                    value: "61"
                }, {label: "Gansu", value: "62"},
                {label: "Qinghai", value: "63"}, {label: "Ningxia", value: "64"}, {
                    label: "Xinjiang",
                    value: "65"
                }, {label: "Taiwan", value: "71"},
                {label: "Xianggang", value: "91"}, {label: "Aomen", value: "92"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "CO":
            state_options = [
                {label: "Amazonas", value: "AMA"}, {label: "Antioquia", value: "ANT"}, {
                    label: "Arauca",
                    value: "ARA"
                }, {label: "Atlántico", value: "ATL"},
                {label: "Bolívar", value: "BOL"}, {label: "Boyacá", value: "BOY"}, {
                    label: "Caldas",
                    value: "CAL"
                }, {label: "Caquetá", value: "CAQ"},
                {label: "Casanare", value: "CAS"}, {label: "Cauca", value: "CAU"}, {
                    label: "Cesar",
                    value: "CES"
                }, {label: "Chocó", value: "CHO"},
                {label: "Córdoba", value: "COR"}, {
                    label: "Cundinamarca",
                    value: "CUN"
                }, {label: "Distrito Capital de Bogotá", value: "DC"}, {label: "Guainía", value: "GUA"},
                {label: "Guaviare", value: "GUV"}, {label: "Huila", value: "HUI"}, {
                    label: "La Guajira",
                    value: "LAG"
                }, {label: "Magdalena", value: "MAG"},
                {label: "Meta", value: "MET"}, {label: "Nariño", value: "NAR"}, {
                    label: "Norte de Santander",
                    value: "NSA"
                }, {label: "Putumayo", value: "PUT"},
                {label: "Quindío", value: "QUI"}, {label: "Risaralda", value: "RIS"}, {
                    label: "Santander",
                    value: "SAN"
                }, {label: "San Andrés, Providencia y Santa Catalina", value: "SAP"},
                {label: "Sucre", value: "SUC"}, {label: "Tolima", value: "TOL"}, {
                    label: "Valle del Cauca",
                    value: "VAC"
                }, {label: "Vaupés", value: "VAU"},
                {label: "Vichada", value: "VID"}
            ];
            document.getElementById(stateId).innerHTML = "Department";
            break;
        case "CR":
            state_options = [
                {label: "Alajuela", value: "A"}, {label: "Cartago", value: "C"}, {
                    label: "Guanacaste",
                    value: "G"
                }, {label: "Heredia", value: "H"},
                {label: "Limón", value: "L"}, {label: "Puntarenas", value: "P"}, {label: "San José", value: "SJ"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "CU":
            state_options = [
                {label: "Pinar del Río", value: "1"}, {label: "La Habana", value: "2"}, {
                    label: "Ciudad de La Habana",
                    value: "3"
                }, {label: "Matanzas", value: "4"},
                {label: "Villa Clara", value: "5"}, {label: "Cienfuegos", value: "6"}, {
                    label: "Sancti Spíritus",
                    value: "7"
                }, {label: "Ciego de ??vila", value: "8"},
                {label: "Camagüey", value: "9"}, {label: "Las Tunas", value: "10"}, {
                    label: "Holguín",
                    value: "11"
                }, {label: "Granma", value: "12"},
                {label: "Santiago de Cuba", value: "13"}, {label: "Guantánamo", value: "14"}, {
                    label: "Artemisa",
                    value: "15"
                }, {label: "Mayabeque", value: "16"},
                {label: "Isla de la Juventud", value: "99"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "CV":
            state_options = [
                {label: "Brava", value: "BR"}, {label: "Boa Vista", value: "BV"}, {
                    label: "Santa Catarina",
                    value: "CA"
                }, {label: "Santa Catarina do Fogo", value: "CF"},
                {label: "Santa Cruz", value: "CR"}, {label: "Maio", value: "MA"}, {
                    label: "Mosteiros",
                    value: "MO"
                }, {label: "Paul", value: "PA"},
                {label: "Porto Novo", value: "PN"}, {label: "Praia", value: "PR"}, {
                    label: "Ribeira Brava",
                    value: "RB"
                }, {label: "Ribeira Grande", value: "RG"},
                {label: "Ribeira Grande de Santiago", value: "RS"}, {
                    label: "São Domingos",
                    value: "SD"
                }, {label: "São Filipe", value: "SF"}, {label: "Sal", value: "SL"},
                {label: "São Miguel", value: "SM"}, {
                    label: "São Lourenço dos Órgãos",
                    value: "SO"
                }, {label: "São Salvador do Mundo", value: "SS"}, {label: "São Vicente", value: "SV"},
                {label: "Tarrafal", value: "TA"}, {label: "Tarrafal de São Nicolau", value: "TS"}
            ];
            document.getElementById(stateId).innerHTML = "Municipality";
            break;
        case "CY":
            state_options = [
                {label: "Lefkosia", value: "1"}, {label: "Lemesos", value: "2"}, {
                    label: "Larnaka",
                    value: "3"
                }, {label: "Ammochostos", value: "4"},
                {label: "Pafos", value: "5"}, {label: "Keryneia", value: "6"}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "CZ":
            state_options = [
                {label: "Praha, Hlavní mešto", value: "10"}, {
                    label: "Stredoceský kraj",
                    value: "20"
                }, {label: "Jihoceský kraj", value: "31"}, {label: "Plzenský kraj", value: "32"},
                {label: "Karlovarský kraj", value: "41"}, {
                    label: "Ústecký kraj",
                    value: "42"
                }, {label: "Liberecký kraj", value: "51"}, {label: "Královéhradecký kraj", value: "52"},
                {label: "Pardubický kraj", value: "53"}, {
                    label: "Kraj Vysocina",
                    value: "63"
                }, {label: "Jihomoravský kraj", value: "64"}, {label: "Olomoucký kraj", value: "71"},
                {label: "Zlínský kraj", value: "72"}, {label: "Moravskoslezský kraj", value: "80"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "DE":
            state_options = [
                {label: "Brandenburg", value: "BB"}, {label: "Berlin", value: "BE"}, {
                    label: "Baden-Württemberg",
                    value: "BW"
                }, {label: "Bayern", value: "BY"},
                {label: "Bremen", value: "HB"}, {label: "Hessen", value: "HE"}, {
                    label: "Hamburg",
                    value: "HH"
                }, {label: "Mecklenburg-Vorpommern", value: "MV"},
                {label: "Niedersachsen", value: "NI"}, {
                    label: "Nordrhein-Westfalen",
                    value: "NW"
                }, {label: "Rheinland-Pfalz", value: "RP"}, {label: "Schleswig-Holstein", value: "SH"},
                {label: "Saarland", value: "SL"}, {label: "Sachsen", value: "SN"}, {
                    label: "Sachsen-Anhalt",
                    value: "ST"
                }, {label: "Thüringen", value: "TH"}
            ];
            document.getElementById(stateId).innerHTML = "Land";
            break;
        case "DJ":
            state_options = [
                {label: "Arta", value: "AR"}, {label: "Ali Sabieh", value: "AS"}, {
                    label: "Dikhil",
                    value: "DI"
                }, {label: "Djibouti", value: "DJ"},
                {label: "Obock", value: "OB"}, {label: "Tadjourah", value: "TA"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "DK":
            state_options = [
                {label: "Nordjylland", value: "81"}, {label: "Midtjylland", value: "82"}, {
                    label: "Syddanmark",
                    value: "83"
                }, {label: "Hovedstaden", value: "84"},
                {label: "Sjælland", value: "85"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "DM":
            state_options = [
                {label: "Saint Andrew", value: "2"}, {label: "Saint David", value: "3"}, {
                    label: "Saint George",
                    value: "4"
                }, {label: "Saint John", value: "5"},
                {label: "Saint Joseph", value: "6"}, {label: "Saint Luke", value: "7"}, {
                    label: "Saint Mark",
                    value: "8"
                }, {label: "Saint Patrick", value: "9"},
                {label: "Saint Paul", value: "10"}, {label: "Saint Peter", value: "11"}
            ];
            document.getElementById(stateId).innerHTML = "Parish";
            break;
        case "DO":
            state_options = [
                {label: "Distrito Nacional (Santo Domingo)", value: "1"}, {
                    label: "Azua",
                    value: "2"
                }, {label: "Baoruco", value: "3"}, {label: "Barahona", value: "4"},
                {label: "Dajabón", value: "5"}, {label: "Duarte", value: "6"}, {
                    label: "Elías Piña",
                    value: "7"
                }, {label: "El Seibo", value: "8"},
                {label: "Espaillat", value: "9"}, {label: "Independencia", value: "10"}, {
                    label: "La Altagracia",
                    value: "11"
                }, {label: "La Romana", value: "12"},
                {label: "La Vega", value: "13"}, {label: "María Trinidad Sánchez", value: "14"}, {
                    label: "Monte Cristi",
                    value: "15"
                }, {label: "Pedernales", value: "16"},
                {label: "Peravia", value: "17"}, {label: "Puerto Plata", value: "18"}, {
                    label: "Hermanas Mirabal",
                    value: "19"
                }, {label: "Samaná", value: "20"},
                {label: "San Cristóbal", value: "21"}, {label: "San Juan", value: "22"}, {
                    label: "San Pedro de Macorís",
                    value: "23"
                }, {label: "Sánchez Ramírez", value: "24"},
                {label: "Santiago", value: "25"}, {label: "Santiago Rodríguez", value: "26"}, {
                    label: "Valverde",
                    value: "27"
                }, {label: "Monseñor Nouel", value: "28"},
                {label: "Monte Plata", value: "29"}, {label: "Hato Mayor", value: "30"}, {
                    label: "San José de Ocoa",
                    value: "31"
                }, {label: "Santo Domingo", value: "32"},
                {label: "Cibao Nordeste", value: "33"}, {label: "Cibao Noroeste", value: "34"}, {
                    label: "Cibao Norte",
                    value: "35"
                }, {label: "Cibao Sur", value: "36"},
                {label: "El Valle", value: "37"}, {label: "Enriquillo", value: "38"}, {
                    label: "Higuamo",
                    value: "39"
                }, {label: "Ozama", value: "40"},
                {label: "Valdesia", value: "41"}, {label: "Yuma", value: "42"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "DZ":
            state_options = [
                {label: "Adrar", value: "1"}, {label: "Chlef", value: "2"}, {
                    label: "Laghouat",
                    value: "3"
                }, {label: "Oum el Bouaghi", value: "4"},
                {label: "Batna", value: "5"}, {label: "Béjaïa", value: "6"}, {
                    label: "Biskra",
                    value: "7"
                }, {label: "Béchar", value: "8"},
                {label: "Blida", value: "9"}, {label: "Bouira", value: "10"}, {
                    label: "Tamanrasset",
                    value: "11"
                }, {label: "Tébessa", value: "12"},
                {label: "Tlemcen", value: "13"}, {label: "Tiaret", value: "14"}, {
                    label: "Tizi Ouzou",
                    value: "15"
                }, {label: "Alger", value: "16"},
                {label: "Djelfa", value: "17"}, {label: "Jijel", value: "18"}, {
                    label: "Sétif",
                    value: "19"
                }, {label: "Saïda", value: "20"},
                {label: "Skikda", value: "21"}, {label: "Sidi Bel Abbès", value: "22"}, {
                    label: "Annaba",
                    value: "23"
                }, {label: "Guelma", value: "24"},
                {label: "Constantine", value: "25"}, {label: "Médéa", value: "26"}, {
                    label: "Mostaganem",
                    value: "27"
                }, {label: "M'sila", value: "28"},
                {label: "Mascara", value: "29"}, {label: "Ouargla", value: "30"}, {
                    label: "Oran",
                    value: "31"
                }, {label: "El Bayadh", value: "32"},
                {label: "Illizi", value: "33"}, {label: "Bordj Bou Arréridj", value: "34"}, {
                    label: "Boumerdès",
                    value: "35"
                }, {label: "El Tarf", value: "36"},
                {label: "Tindouf", value: "37"}, {label: "Tissemsilt", value: "38"}, {
                    label: "El Oued",
                    value: "39"
                }, {label: "Khenchela", value: "40"},
                {label: "Souk Ahras", value: "41"}, {label: "Tipaza", value: "42"}, {
                    label: "Mila",
                    value: "43"
                }, {label: "Aïn Defla", value: "44"},
                {label: "Naama", value: "45"}, {label: "Aïn Témouchent", value: "46"}, {
                    label: "Ghardaïa",
                    value: "47"
                }, {label: "Relizane", value: "48"},
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "EC":
            state_options = [
                {label: "Azuay", value: "A"}, {label: "Bolívar", value: "B"}, {
                    label: "Carchi",
                    value: "C"
                }, {label: "Orellana", value: "D"},
                {label: "Esmeraldas", value: "E"}, {label: "Cañar", value: "F"}, {
                    label: "Guayas",
                    value: "G"
                }, {label: "Chimborazo", value: "H"},
                {label: "Imbabura", value: "I"}, {label: "Loja", value: "L"}, {
                    label: "Manabí",
                    value: "M"
                }, {label: "Napo", value: "N"},
                {label: "El Oro", value: "O"}, {label: "Pichincha", value: "P"}, {
                    label: "Los Ríos",
                    value: "R"
                }, {label: "Morona-Santiago", value: "S"},
                {label: "Santo Domingo de los Tsáchilas", value: "SD"}, {
                    label: "Santa Elena",
                    value: "SE"
                }, {label: "Tungurahua", value: "T"}, {label: "Sucumbíos", value: "U"},
                {label: "Galápagos", value: "W"}, {label: "Cotopaxi", value: "X"}, {
                    label: "Pastaza",
                    value: "Y"
                }, {label: "Zamora Chinchipe", value: "Z"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "EE":
            state_options = [
                {label: "Harjumaa", value: "37"}, {label: "Hiiumaa", value: "39"}, {
                    label: "Ida-Virumaa",
                    value: "44"
                }, {label: "Jõgevamaa", value: "49"},
                {label: "Järvamaa", value: "51"}, {label: "Läänemaa", value: "57"}, {
                    label: "Lääne-Virumaa",
                    value: "59"
                }, {label: "Põlvamaa", value: "65"},
                {label: "Pärnumaa", value: "67"}, {label: "Raplamaa", value: "70"}, {
                    label: "Saaremaa",
                    value: "74"
                }, {label: "Tartumaa", value: "78"},
                {label: "Valgamaa", value: "82"}, {label: "Viljandimaa", value: "84"}, {label: "Võrumaa", value: "86"}
            ];
            document.getElementById(stateId).innerHTML = "County";
            break;
        case "EG":
            state_options = [
                {label: "AI Iskandariyah", value: "ALX"}, {label: "Aswan", value: "ASN"}, {
                    label: "Asyut",
                    value: "AST"
                }, {label: "Al Ba?r al A?mar", value: "BA"},
                {label: "Al Bu?ayrah", value: "BH"}, {label: "Bani Suwayf", value: "BNS"}, {
                    label: "AI Qahirah",
                    value: "C"
                }, {label: "Ad Daqahliyah", value: "DK"},
                {label: "Dumyat", value: "DT"}, {label: "AI Fayyum", value: "FYM"}, {
                    label: "AI Gharbiyah",
                    value: "GH"
                }, {label: "AI Jizah", value: "GZ"},
                {label: "AI Isma 'iliyah", value: "IS"}, {label: "Janub Sina'", value: "JS"}, {
                    label: "AI Qalyubiyah",
                    value: "KB"
                }, {label: "Kafr ash Shaykh", value: "KFS"},
                {label: "Qina", value: "KN"}, {label: "Al Uqsur", value: "LX"}, {
                    label: "AI Minya",
                    value: "MN"
                }, {label: "AI Minufiyah", value: "MNF"},
                {label: "Matruh", value: "MT"}, {label: "Bur Sa'id", value: "PTS"}, {
                    label: "Suhaj",
                    value: "SHG"
                }, {label: "Shamal Sina'", value: "SIN"},
                {label: "As Suways", value: "SUZ"}, {label: "AI Wadi al Jadid", value: "WAD"}
            ];
            document.getElementById(stateId).innerHTML = "Governorate";
            break;
        case "ER":
            state_options = [
                {label: "‘Anseba", value: "AN"}, {label: "Debubawi K’eyyi? Ba?ri", value: "DK"}, {
                    label: "Debub",
                    value: "DU"
                }, {label: "Gash-Barka", value: "GB"},
                {label: "Ma’ikel", value: "MA"}, {label: "Semienawi K’eyyi? Ba?ri", value: "SK"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "ES":
            state_options = [
                {label: "Alicante / Alacant", value: "A"}, {label: "Albacete", value: "AB"}, {
                    label: "Almería",
                    value: "AL"
                }, {label: "??vila", value: "AV"},
                {label: "Barcelona", value: "B"}, {label: "Badajoz", value: "BA"}, {
                    label: "Biskaia",
                    value: "BI"
                }, {label: "Burgos", value: "BU"},
                {label: "A Coruña", value: "C"}, {label: "Cádiz", value: "CA"}, {
                    label: "Cáceres",
                    value: "CC"
                }, {label: "Ceuta", value: "CE"},
                {label: "Córdoba", value: "CO"}, {label: "Ciudad Real", value: "CR"}, {
                    label: "Castellón / Castelló",
                    value: "CS"
                }, {label: "Cuenca", value: "CU"},
                {label: "Las Palmas", value: "GC"}, {label: "Girona  [Gerona]", value: "GI"}, {
                    label: "Granada",
                    value: "GR"
                }, {label: "Guadalajara", value: "GU"},
                {label: "Huelva", value: "H"}, {label: "Huesca", value: "HU"}, {
                    label: "Jaén",
                    value: "J"
                }, {label: "Lleida  [Lérida]", value: "L"},
                {label: "León", value: "LE"}, {label: "La Rioja", value: "LO"}, {
                    label: "Lugo",
                    value: "LU"
                }, {label: "Madrid", value: "M"},
                {label: "Málaga", value: "MA"}, {label: "Melilla", value: "ML"}, {
                    label: "Murcia",
                    value: "MU"
                }, {label: "Navarra / Nafarroa", value: "NA"},
                {label: "Asturias", value: "O"}, {label: "Ourense  [Orense]", value: "OR"}, {
                    label: "Palencia",
                    value: "P"
                }, {label: "Balears  [Baleares]", value: "PM"},
                {label: "Pontevedra  [Pontevedra]", value: "PO"}, {label: "Cantabria", value: "S"}, {
                    label: "Salamanca",
                    value: "SA"
                }, {label: "Sevilla", value: "SE"},
                {label: "Segovia", value: "SG"}, {label: "Soria", value: "SO"}, {
                    label: "Gipuzkoa",
                    value: "SS"
                }, {label: "Tarragona  [Tarragona]", value: "T"},
                {label: "Teruel", value: "TE"}, {label: "Santa Cruz de Tenerife", value: "TF"}, {
                    label: "Toledo",
                    value: "TO"
                }, {label: "Valencia / València", value: "V"},
                {label: "Valladolid", value: "VA"}, {label: "??lava / Araba", value: "VI"}, {
                    label: "Zaragoza",
                    value: "Z"
                }, {label: "Zamora", value: "ZA"},
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "ET":
            state_options = [
                {label: "Adis Abeba", value: "AA"}, {label: "Afar", value: "AF"}, {
                    label: "Amara",
                    value: "AM"
                }, {label: "Binshangul Gumuz", value: "BE"},
                {label: "Dire Dawa", value: "DD"}, {label: "Gambela Hizboch", value: "GA"}, {
                    label: "Hareri Hizb",
                    value: "HA"
                }, {label: "Oromiya", value: "OR"},
                {label: "YeDebub Biheroch Bihereseboch na Hizboch", value: "SN"}, {
                    label: "Sumale",
                    value: "SO"
                }, {label: "Tigray", value: "TI"}
            ];
            document.getElementById(stateId).innerHTML = "State";
            break;
        case "FI":
            state_options = [
                {label: "Ahvenanmaan maakunta", value: "1"}, {
                    label: "Etelä-Karjala",
                    value: "2"
                }, {label: "Etelä-Pohjanmaa", value: "3"}, {label: "Etelä-Savo", value: "4"},
                {label: "Kainuu", value: "5"}, {label: "Kanta-Häme", value: "6"}, {
                    label: "Keski-Pohjanmaa",
                    value: "7"
                }, {label: "Keski-Suomi", value: "8"},
                {label: "Kymenlaakso", value: "9"}, {label: "Lappi", value: "10"}, {
                    label: "Pirkanmaa",
                    value: "11"
                }, {label: "Pohjanmaa", value: "12"},
                {label: "Pohjois-Karjala", value: "13"}, {
                    label: "Pohjois-Pohjanmaa",
                    value: "14"
                }, {label: "Pohjois-Savo", value: "15"}, {label: "Päijät-Häme", value: "16"},
                {label: "Satakunta", value: "17"}, {label: "Uusimaa", value: "18"}, {
                    label: "Varsinais-Suomi",
                    value: "19"
                }
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "FJ":
            state_options = [
                {label: "Ba", value: "1"}, {label: "Bua", value: "2"}, {
                    label: "Cakaudrove",
                    value: "3"
                }, {label: "Kadavu", value: "4"},
                {label: "Lau", value: "5"}, {label: "Lomaiviti", value: "6"}, {
                    label: "Macuata",
                    value: "7"
                }, {label: "Nadroga and Navosa", value: "8"},
                {label: "Naitasiri", value: "9"}, {label: "Namosi", value: "10"}, {
                    label: "Ra",
                    value: "11"
                }, {label: "Rewa", value: "12"},
                {label: "Serua", value: "13"}, {label: "Tailevu", value: "14"}, {label: "Rotuma", value: "R"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "FM":
            state_options = [
                {label: "Kosrae", value: "KSA"}, {label: "Pohnpei", value: "PNI"}, {
                    label: "Chuuk",
                    value: "TRK"
                }, {label: "Yap", value: "YAP"}
            ];
            document.getElementById(stateId).innerHTML = "State";
            break;
        case "FR":
            state_options = [
                {"label": "Ain", "value": "1"}, {"label": "Aisne", "value": "2"}, {
                    "label": "Allier",
                    "value": "3"
                }, {"label": "Alpes-de-Haute-Provence", "value": "4"},
                {"label": "Hautes-Alpes", "value": "5"}, {
                    "label": "Alpes-Maritimes",
                    "value": "6"
                }, {"label": "Ardèche", "value": "7"}, {"label": "Ardennes", "value": "8"},
                {"label": "Ariège", "value": "9"}, {"label": "Aube", "value": "10"}, {
                    "label": "Aude",
                    "value": "11"
                }, {"label": "Aveyron", "value": "12"},
                {"label": "Bouches-du-Rhône", "value": "13"}, {"label": "Calvados", "value": "14"}, {
                    "label": "Cantal",
                    "value": "15"
                }, {"label": "Charente", "value": "16"},
                {"label": "Charente-Maritime", "value": "17"}, {"label": "Cher", "value": "18"}, {
                    "label": "Corrèze",
                    "value": "19"
                }, {"label": "Côte-d'Or", "value": "21"},
                {"label": "Côtes-d'Armor", "value": "22"}, {"label": "Creuse", "value": "23"}, {
                    "label": "Dordogne",
                    "value": "24"
                }, {"label": "Doubs", "value": "25"},
                {"label": "Drôme", "value": "26"}, {"label": "Eure", "value": "27"}, {
                    "label": "Eure-et-Loir",
                    "value": "28"
                }, {"label": "Finistère", "value": "29"},
                {"label": "Corse-du-Sud", "value": "2A"}, {"label": "Haute-Corse", "value": "2B"}, {
                    "label": "Gard",
                    "value": "30"
                }, {"label": "Haute-Garonne", "value": "31"},
                {"label": "Gers", "value": "32"}, {"label": "Gironde", "value": "33"}, {
                    "label": "Hérault",
                    "value": "34"
                }, {"label": "Ille-et-Vilaine", "value": "35"},
                {"label": "Indre", "value": "36"}, {"label": "Indre-et-Loire", "value": "37"}, {
                    "label": "Isère",
                    "value": "38"
                }, {"label": "Jura", "value": "39"},
                {"label": "Landes", "value": "40"}, {"label": "Loir-et-Cher", "value": "41"}, {
                    "label": "Loire",
                    "value": "42"
                }, {"label": "Haute-Loire", "value": "43"},
                {"label": "Loire-Atlantique", "value": "44"}, {"label": "Loiret", "value": "45"}, {
                    "label": "Lot",
                    "value": "46"
                }, {"label": "Lot-et-Garonne", "value": "47"},
                {"label": "Lozère", "value": "48"}, {"label": "Maine-et-Loire", "value": "49"}, {
                    "label": "Manche",
                    "value": "50"
                }, {"label": "Marne", "value": "51"},
                {"label": "Haute-Marne", "value": "52"}, {
                    "label": "Mayenne",
                    "value": "53"
                }, {"label": "Meurthe-et-Moselle", "value": "54"}, {"label": "Meuse", "value": "55"},
                {"label": "Morbihan", "value": "56"}, {"label": "Moselle", "value": "57"}, {
                    "label": "Nièvre",
                    "value": "58"
                }, {"label": "Nord", "value": "59"},
                {"label": "Oise", "value": "60"}, {"label": "Orne", "value": "61"}, {
                    "label": "Pas-de-Calais",
                    "value": "62"
                }, {"label": "Puy-de-Dôme", "value": "63"},
                {"label": "Pyrénées-Atlantiques", "value": "64"}, {
                    "label": "Hautes-Pyrénées",
                    "value": "65"
                }, {"label": "Pyrénées-Orientales", "value": "66"}, {"label": "Bas-Rhin", "value": "67"},
                {"label": "Haut-Rhin", "value": "68"}, {"label": "Rhône", "value": "69"}, {
                    "label": "Haute-Saône",
                    "value": "70"
                }, {"label": "Saône-et-Loire", "value": "71"},
                {"label": "Sarthe", "value": "72"}, {"label": "Savoie", "value": "73"}, {
                    "label": "Haute-Savoie",
                    "value": "74"
                }, {"label": "Paris", "value": "75"},
                {"label": "Seine-Maritime", "value": "76"}, {
                    "label": "Seine-et-Marne",
                    "value": "77"
                }, {"label": "Yvelines", "value": "78"}, {"label": "Deux-Sèvres", "value": "79"},
                {"label": "Somme", "value": "80"}, {"label": "Tarn", "value": "81"}, {
                    "label": "Tarn-et-Garonne",
                    "value": "82"
                }, {"label": "Var", "value": "83"},
                {"label": "Vaucluse", "value": "84"}, {"label": "Vendée", "value": "85"}, {
                    "label": "Vienne",
                    "value": "86"
                }, {"label": "Haute-Vienne", "value": "87"},
                {"label": "Vosges", "value": "88"}, {
                    "label": "Yonne",
                    "value": "89"
                }, {"label": "Territoire de Belfort", "value": "90"}, {"label": "Essonne", "value": "91"},
                {"label": "Hauts-de-Seine", "value": "92"}, {
                    "label": "Seine-Saint-Denis",
                    "value": "93"
                }, {"label": "Val-de-Marne", "value": "94"}, {"label": "Val-d'Oise", "value": "95"}
            ];
            document.getElementById(stateId).innerHTML = "Metropolitan department";
            break;
        case "GA":
            state_options = [
                {"label": "Estuaire", "value": 1}, {"label": "Haut-Ogooué", "value": 2}, {
                    "label": "Moyen-Ogooué",
                    "value": 3
                }, {"label": "Ngounié", "value": 4},
                {"label": "Nyanga", "value": 5}, {"label": "Ogooué-Ivindo", "value": 6}, {
                    "label": "Ogooué-Lolo",
                    "value": 7
                }, {"label": "Ogooué-Maritime", "value": 8},
                {"label": "Woleu-Ntem", "value": 9}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "GB":
            state_options = [
                {"label": "Armagh, Banbridge and Craigavon", "value": "ABC"}, {
                    "label": "Aberdeenshire",
                    "value": "ABD"
                }, {"label": "Aberdeen City", "value": "ABE"}, {"label": "Argyll and Bute", "value": "AGB"},
                {"label": "Isle of Anglesey [Sir Ynys Môn GB-YNM]", "value": "AGY"}, {
                    "label": "Ards and North Down",
                    "value": "AND"
                }, {"label": "Antrim and Newtownabbey", "value": "ANN"}, {"label": "Angus", "value": "ANS"},
                {"label": "Armagh", "value": "ARM"}, {
                    "label": "Bath and North East Somerset",
                    "value": "BAS"
                }, {"label": "Blackburn with Darwen", "value": "BBD"}, {"label": "Bedford", "value": "BDF"},
                {"label": "Barking and Dagenham", "value": "BDG"}, {
                    "label": "Brent",
                    "value": "BEN"
                }, {"label": "Bexley", "value": "BEX"}, {"label": "Belfast", "value": "BFS"},
                {"label": "Bridgend [Pen-y-bont ar Ogwr GB-POG]", "value": "BGE"}, {
                    "label": "Blaenau Gwent",
                    "value": "BGW"
                }, {"label": "Birmingham", "value": "BIR"}, {"label": "Buckinghamshire", "value": "BKM"},
                {"label": "Bournemouth", "value": "BMH"}, {
                    "label": "Barnet",
                    "value": "BNE"
                }, {"label": "Brighton and Hove", "value": "BNH"}, {"label": "Barnsley", "value": "BNS"},
                {"label": "Bolton", "value": "BOL"}, {
                    "label": "Blackpool",
                    "value": "BPL"
                }, {"label": "Bracknell Forest", "value": "BRC"}, {"label": "Bradford", "value": "BRD"},
                {"label": "Bromley", "value": "BRY"}, {"label": "Bristol, City of", "value": "BST"}, {
                    "label": "Bury",
                    "value": "BUR"
                }, {"label": "Cambridgeshire", "value": "CAM"},
                {"label": "Caerphilly [Caerffili GB-CAF]", "value": "CAY"}, {
                    "label": "Central Bedfordshire",
                    "value": "CBF"
                }, {"label": "Causeway Coast and Glens", "value": "CCG"}, {
                    "label": "Ceredigion [Sir Ceredigion]",
                    "value": "CGN"
                },
                {"label": "Cheshire East", "value": "CHE"}, {
                    "label": "Cheshire West and Chester",
                    "value": "CHW"
                }, {"label": "Calderdale", "value": "CLD"}, {"label": "Clackmannanshire", "value": "CLK"},
                {"label": "Cumbria", "value": "CMA"}, {
                    "label": "Camden",
                    "value": "CMD"
                }, {"label": "Carmarthenshire [Sir Gaerfyrddin GB-GFY]", "value": "CMN"}, {
                    "label": "Cornwall",
                    "value": "CON"
                },
                {"label": "Coventry", "value": "COV"}, {
                    "label": "Cardiff [Caerdydd GB-CRD]",
                    "value": "CRF"
                }, {"label": "Croydon", "value": "CRY"}, {"label": "Conwy", "value": "CWY"},
                {"label": "Darlington", "value": "DAL"}, {
                    "label": "Derbyshire",
                    "value": "DBY"
                }, {"label": "Denbighshire [Sir Ddinbych GB-DDB]", "value": "DEN"}, {"label": "Derby", "value": "DER"},
                {"label": "Devon", "value": "DEV"}, {
                    "label": "Dumfries and Galloway",
                    "value": "DGY"
                }, {"label": "Doncaster", "value": "DNC"}, {"label": "Dundee City", "value": "DND"},
                {"label": "Dorset", "value": "DOR"}, {
                    "label": "Derry and Strabane",
                    "value": "DRS"
                }, {"label": "Dudley", "value": "DUD"}, {"label": "Durham", "value": "DUR"},
                {"label": "Ealing", "value": "EAL"}, {
                    "label": "East Ayrshire",
                    "value": "EAY"
                }, {"label": "Edinburgh, City of", "value": "EDH"}, {"label": "East Dunbartonshire", "value": "EDU"},
                {"label": "East Lothian", "value": "ELN"}, {
                    "label": "Eilean Siar",
                    "value": "ELS"
                }, {"label": "Enfield", "value": "ENF"}, {"label": "East Renfrewshire", "value": "ERW"},
                {"label": "East Riding of Yorkshire", "value": "ERY"}, {
                    "label": "Essex",
                    "value": "ESS"
                }, {"label": "East Sussex", "value": "ESX"}, {"label": "Falkirk", "value": "FAL"},
                {"label": "Fife", "value": "FIF"}, {
                    "label": "Flintshire [Sir y Fflint GB-FFL]",
                    "value": "FLN"
                }, {"label": "\tFermanagh and Omagh", "value": "FMO"}, {"label": "Gateshead", "value": "GAT"},
                {"label": "Glasgow City", "value": "GLG"}, {
                    "label": "Gloucestershire",
                    "value": "GLS"
                }, {"label": "Greenwich", "value": "GRE"}, {"label": "Gwynedd", "value": "GWN"},
                {"label": "Halton", "value": "HAL"}, {"label": "Hampshire", "value": "HAM"}, {
                    "label": "Havering",
                    "value": "HAV"
                }, {"label": "Hackney", "value": "HCK"},
                {"label": "Herefordshire", "value": "HEF"}, {
                    "label": "Hillingdon",
                    "value": "HIL"
                }, {"label": "Highland", "value": "HLD"}, {"label": "Hammersmith and Fulham", "value": "HMF"},
                {"label": "Hounslow", "value": "HNS"}, {
                    "label": "Hartlepool",
                    "value": "HPL"
                }, {"label": "Hertfordshire", "value": "HRT"}, {"label": "Harrow", "value": "HRW"},
                {"label": "Haringey", "value": "HRY"}, {
                    "label": "Isles of Scilly",
                    "value": "IOS"
                }, {"label": "Isle of Wight", "value": "IOW"}, {"label": "Islington", "value": "ISL"},
                {"label": "Inverclyde", "value": "IVC"}, {
                    "label": "Kensington and Chelsea",
                    "value": "KEC"
                }, {"label": "Kent", "value": "KEN"}, {"label": "Kingston upon Hull", "value": "KHL"},
                {"label": "Kirklees", "value": "KIR"}, {
                    "label": "Kingston upon Thames",
                    "value": "KTT"
                }, {"label": "Knowsley", "value": "KWL"}, {"label": "Lancashire", "value": "LAN"},
                {"label": "Lisburn and Castlereagh", "value": "LBC"}, {
                    "label": "Lambeth",
                    "value": "LBH"
                }, {"label": "Leicester", "value": "LCE"}, {"label": "Leeds", "value": "LDS"},
                {"label": "Leicestershire", "value": "LEC"}, {
                    "label": "Lewisham",
                    "value": "LEW"
                }, {"label": "Lincolnshire", "value": "LIN"}, {"label": "Liverpool", "value": "LIV"},
                {"label": "London, City of", "value": "LND"}, {
                    "label": "Luton",
                    "value": "LUT"
                }, {"label": "Manchester", "value": "MAN"}, {"label": "Middlesbrough", "value": "MDB"},
                {"label": "Medway", "value": "MDW"}, {
                    "label": "Mid and East Antrim",
                    "value": "MEA"
                }, {"label": "Milton Keynes", "value": "MIK"}, {"label": "Midlothian", "value": "MLN"},
                {"label": "Monmouthshire [Sir Fynwy GB-FYN]", "value": "MON"}, {
                    "label": "Merton",
                    "value": "MRT"
                }, {"label": "Moray", "value": "MRY"}, {
                    "label": "Merthyr Tydfil [Merthyr Tudful GB-MTU]",
                    "value": "MTY"
                },
                {"label": "Mid Ulster", "value": "MUL"}, {
                    "label": "North Ayrshire",
                    "value": "NAY"
                }, {"label": "Northumberland", "value": "NBL"}, {"label": "North East Lincolnshire", "value": "NEL"},
                {"label": "Newcastle upon Tyne", "value": "NET"}, {
                    "label": "Norfolk",
                    "value": "NFK"
                }, {"label": "Nottingham", "value": "NGM"}, {"label": "North Lanarkshire", "value": "NLK"},
                {"label": "North Lincolnshire", "value": "NLN"}, {
                    "label": "Newry, Mourne and Down",
                    "value": "NMD"
                }, {"label": "North Somerset", "value": "NSM"}, {"label": "Northamptonshire", "value": "NTH"},
                {
                    "label": "Neath Port Talbot [Castell-nedd Port Talbot GB-CTL]",
                    "value": "NTL"
                }, {"label": "Nottinghamshire", "value": "NTT"}, {
                    "label": "North Tyneside",
                    "value": "NTY"
                }, {"label": "Newham", "value": "NWM"},
                {"label": "Newport [Casnewydd GB-CNW]", "value": "NWP"}, {
                    "label": "North Yorkshire",
                    "value": "NYK"
                }, {"label": "Oldham", "value": "OLD"}, {"label": "Orkney Islands", "value": "ORK"},
                {"label": "Oxfordshire", "value": "OXF"}, {
                    "label": "Pembrokeshire [Sir Benfro GB-BNF]",
                    "value": "PEM"
                }, {"label": "Perth and Kinross", "value": "PKN"}, {"label": "Plymouth", "value": "PLY"},
                {"label": "Poole", "value": "POL"}, {"label": "Portsmouth", "value": "POR"}, {
                    "label": "Powys",
                    "value": "POW"
                }, {"label": "Peterborough", "value": "PTE"},
                {"label": "Redcar and Cleveland", "value": "RCC"}, {
                    "label": "Rochdale",
                    "value": "RCH"
                }, {"label": "Rhondda, Cynon, Taff [Rhondda, Cynon,Taf]", "value": "RCT"}, {
                    "label": "Redbridge",
                    "value": "RDB"
                },
                {"label": "Reading", "value": "RDG"}, {
                    "label": "Renfrewshire",
                    "value": "RFW"
                }, {"label": "Richmond upon Thames", "value": "RIC"}, {"label": "Rotherham", "value": "ROT"},
                {"label": "Rutland", "value": "RUT"}, {"label": "Sandwell", "value": "SAW"}, {
                    "label": "South Ayrshire",
                    "value": "SAY"
                }, {"label": "Scottish Borders, The", "value": "SCB"},
                {"label": "Suffolk", "value": "SFK"}, {
                    "label": "Sefton",
                    "value": "SFT"
                }, {"label": "South Gloucestershire", "value": "SGC"}, {"label": "Sheffield", "value": "SHF"},
                {"label": "St. Helens", "value": "SHN"}, {"label": "Shropshire", "value": "SHR"}, {
                    "label": "Stockport",
                    "value": "SKP"
                }, {"label": "Salford", "value": "SLF"},
                {"label": "Slough", "value": "SLG"}, {
                    "label": "South Lanarkshire",
                    "value": "SLK"
                }, {"label": "Sunderland", "value": "SND"}, {"label": "Solihull", "value": "SOL"},
                {"label": "Somerset", "value": "SOM"}, {"label": "Southend-on-Sea", "value": "SOS"}, {
                    "label": "Surrey",
                    "value": "SRY"
                }, {"label": "Stoke-on-Trent", "value": "STE"},
                {"label": "Stirling", "value": "STG"}, {"label": "Southampton", "value": "STH"}, {
                    "label": "Sutton",
                    "value": "STN"
                }, {"label": "Staffordshire", "value": "STS"},
                {"label": "Stockton-on-Tees", "value": "STT"}, {
                    "label": "South Tyneside",
                    "value": "STY"
                }, {"label": "Swansea [Abertawe GB-ATA]", "value": "SWA"}, {"label": "Swindon", "value": "SWD"},
                {"label": "Southwark", "value": "SWK"}, {
                    "label": "Tameside",
                    "value": "TAM"
                }, {"label": "Telford and Wrekin", "value": "TFW"}, {"label": "Thurrock", "value": "THR"},
                {"label": "Torbay", "value": "TOB"}, {
                    "label": "Torfaen [Tor-faen]",
                    "value": "TOF"
                }, {"label": "Trafford", "value": "TRF"}, {"label": "Tower Hamlets", "value": "TWH"},
                {"label": "Vale of Glamorgan, The [Bro Morgannwg GB-BMG]", "value": "VGL"}, {
                    "label": "Warwickshire",
                    "value": "WAR"
                }, {"label": "West Berkshire", "value": "WBK"}, {"label": "West Dunbartonshire", "value": "WDU"},
                {"label": "Waltham Forest", "value": "WFT"}, {"label": "Wigan", "value": "WGN"}, {
                    "label": "Wiltshire",
                    "value": "WIL"
                }, {"label": "Wakefield", "value": "WKF"},
                {"label": "Walsall", "value": "WLL"}, {
                    "label": "West Lothian",
                    "value": "WLN"
                }, {"label": "Wolverhampton", "value": "WLV"}, {"label": "Wandsworth", "value": "WND"},
                {"label": "Windsor and Maidenhead", "value": "WNM"}, {
                    "label": "Wokingham",
                    "value": "WOK"
                }, {"label": "Worcestershire", "value": "WOR"}, {"label": "Wirral", "value": "WRL"},
                {"label": "Warrington", "value": "WRT"}, {
                    "label": "Wrexham [Wrecsam GB-WRC]",
                    "value": "WRX"
                }, {"label": "Westminster", "value": "WSM"}, {"label": "West Sussex", "value": "WSX"},
                {"label": "York", "value": "YOR"}, {"label": "Shetland Islands", "value": "ZET"}
            ];
            document.getElementById(stateId).innerHTML = "County";
            break;
        case "GD":
            state_options = [
                {"label": "Saint Andrew", "value": 1}, {"label": "Saint David", "value": 2}, {
                    "label": "Saint George",
                    "value": 3
                }, {"label": "Saint John", "value": 4},
                {"label": "Saint Mark", "value": 5}, {
                    "label": "Saint Patrick",
                    "value": 6
                }, {"label": "Southern Grenadine Islands", "value": 10}
            ];
            document.getElementById(stateId).innerHTML = "Parish";
            break;
        case "GE":
            state_options = [
                {"label": "Abkhazia", "value": "AB"}, {"label": "Ajaria", "value": "AJ"}, {
                    "label": "Guria",
                    "value": "GU"
                }, {"label": "Imeret'i", "value": "IM"},
                {"label": "Kakhet'i", "value": "KA"}, {
                    "label": "K'vemo K'art'li",
                    "value": "KK"
                }, {"label": "Mts'khet'a-Mt'ianet'i", "value": "MM"}, {
                    "label": "Racha-Lech’khumi-K’vemo Svanet’i",
                    "value": "RL"
                },
                {"label": "Samts'khe-Javakhet'i", "value": "SJ"}, {
                    "label": "Shida K'art'li",
                    "value": "SK"
                }, {"label": "Samegrelo-Zemo Svanet'i", "value": "SZ"}, {"label": "T'bilisi", "value": "TB"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "GH":
            state_options = [
                {"label": "Greater Accra", "value": "AA"}, {"label": "Ashanti", "value": "AH"}, {
                    "label": "Brong-Ahafo",
                    "value": "BA"
                }, {"label": "Central", "value": "CP"},
                {"label": "Eastern", "value": "EP"}, {"label": "Northern", "value": "NP"}, {
                    "label": "Volta",
                    "value": "TV"
                }, {"label": "Upper East", "value": "UE"},
                {"label": "Upper West", "value": "UW"}, {"label": "Western", "value": "WP"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "GL":
            state_options = [
                {"label": "Kommune Kujalleq", "value": "KU"}, {
                    "label": "Qaasuitsup Kommunia",
                    "value": "QA"
                }, {"label": "Qeqqata Kommunia", "value": "QE"}, {"label": "Kommuneqarfik Sermersooq", "value": "SM"}
            ];
            document.getElementById(stateId).innerHTML = "Municipality";
            break;
        case "GM":
            state_options = [
                {"label": "Banjul", "value": "B"}, {"label": "Lower River", "value": "L"}, {
                    "label": "Central River",
                    "value": "M"
                }, {"label": "North Bank", "value": "N"},
                {"label": "Upper River", "value": "U"}, {"label": "Western", "value": "W"}
            ];
            document.getElementById(stateId).innerHTML = "Division";
            break;
        case "GN":
            state_options = [
                {"label": "Beyla", "value": "BE"}, {"label": "Boffa", "value": "BF"}, {
                    "label": "Boké",
                    "value": "BK"
                }, {"label": "Coyah", "value": "CO"},
                {"label": "Dabola", "value": "DB"}, {"label": "Dinguiraye", "value": "DI"}, {
                    "label": "Dalaba",
                    "value": "DL"
                }, {"label": "Dubréka", "value": "DU"},
                {"label": "Faranah", "value": "FA"}, {"label": "Forécariah", "value": "FO"}, {
                    "label": "Fria",
                    "value": "FR"
                }, {"label": "Gaoual", "value": "GA"},
                {"label": "Guékédou", "value": "GU"}, {"label": "Kankan", "value": "KA"}, {
                    "label": "Koubia",
                    "value": "KB"
                }, {"label": "Kindia", "value": "KD"},
                {"label": "Kérouané", "value": "KE"}, {"label": "Koundara", "value": "KN"}, {
                    "label": "Kouroussa",
                    "value": "KO"
                }, {"label": "Kissidougou", "value": "KS"},
                {"label": "Labé", "value": "LA"}, {"label": "Lélouma", "value": "LE"}, {
                    "label": "Lola",
                    "value": "LO"
                }, {"label": "Macenta", "value": "MC"},
                {"label": "Mandiana", "value": "MD"}, {"label": "Mali", "value": "ML"}, {
                    "label": "Mamou",
                    "value": "MM"
                }, {"label": "Nzérékoré", "value": "NZ"},
                {"label": "Pita", "value": "PI"}, {"label": "Siguiri", "value": "SI"}, {
                    "label": "Télimélé",
                    "value": "TE"
                }, {"label": "Tougué", "value": "TO"},
                {"label": "Yomou", "value": "YO"}
            ];
            document.getElementById(stateId).innerHTML = "Prefecture";
            break;
        case "GQ":
            state_options = [
                {"label": "Annobón", "value": "AN"}, {"label": "Bioko Norte", "value": "BN"}, {
                    "label": "Bioko Sur",
                    "value": "BS"
                }, {"label": "Centro Sur", "value": "CS"},
                {"label": "Kié-Ntem", "value": "KN"}, {"label": "Litoral", "value": "LI"}, {
                    "label": "Wele-Nzas",
                    "value": "WN"
                }
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "GR":
            state_options = [
                {"label": "Anatolikí Makedonía kai Thráki", "value": "A"}, {
                    "label": "Kentrikí Makedonía",
                    "value": "B"
                }, {"label": "Dytiki Makedonia", "value": "C"}, {"label": "??peiros", "value": "D"},
                {"label": "Thessalía", "value": "E"}, {"label": "Ionía Nísia", "value": "F"}, {
                    "label": "Dytikí Elláda",
                    "value": "G"
                }, {"label": "Stereá Elláda", "value": "H"},
                {"label": "Attikí", "value": "I"}, {"label": "Peloponnísos", "value": "J"}, {
                    "label": "Voreío Aigaío",
                    "value": "K"
                }, {"label": "Notío Aigaío", "value": "L"},
                {"label": "Kríti", "value": "M"}
            ];
            document.getElementById(stateId).innerHTML = "Administrative region";
            break;
        case "GT":
            state_options = [
                {"label": "Alta Verapaz", "value": "AV"}, {
                    "label": "Baja Verapaz",
                    "value": "BV"
                }, {"label": "Chimaltenango", "value": "CM"}, {"label": "Chiquimula", "value": "CQ"},
                {"label": "Escuintla", "value": "ES"}, {"label": "Guatemala", "value": "GU"}, {
                    "label": "Huehuetenango",
                    "value": "HU"
                }, {"label": "Izabal", "value": "IZ"},
                {"label": "Jalapa", "value": "JA"}, {"label": "Jutiapa", "value": "JU"}, {
                    "label": "Petén",
                    "value": "PE"
                }, {"label": "El Progreso", "value": "PR"},
                {"label": "Quiché", "value": "QC"}, {"label": "Quetzaltenango", "value": "QZ"}, {
                    "label": "Retalhuleu",
                    "value": "RE"
                }, {"label": "Sacatepéquez", "value": "SA"},
                {"label": "San Marcos", "value": "SM"}, {"label": "Sololá", "value": "SO"}, {
                    "label": "Santa Rosa",
                    "value": "SR"
                }, {"label": "Suchitepéquez", "value": "SU"},
                {"label": "Totonicapán", "value": "TO"}, {"label": "Zacapa", "value": "ZA"}
            ];
            document.getElementById(stateId).innerHTML = "Department";
            break;
        case "GW":
            state_options = [
                {"label": "Bafatá", "value": "BA"}, {"label": "Bolama", "value": "BL"}, {
                    "label": "Biombo",
                    "value": "BM"
                }, {"label": "Bissau", "value": "BS"},
                {"label": "Cacheu", "value": "CA"}, {"label": "Gabú", "value": "GA"}, {
                    "label": "Oio",
                    "value": "OI"
                }, {"label": "Quinara", "value": "QU"},
                {"label": "Tombali", "value": "TO"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "GY":
            state_options = [
                {"label": "Barima-Waini", "value": "BA"}, {
                    "label": "Cuyuni-Mazaruni",
                    "value": "CU"
                }, {"label": "Demerara-Mahaica", "value": "DE"}, {"label": "East Berbice-Corentyne", "value": "EB"},
                {"label": "Essequibo Islands-West Demerara", "value": "ES"}, {
                    "label": "Mahaica-Berbice",
                    "value": "MA"
                }, {"label": "Pomeroon-Supenaam", "value": "PM"}, {"label": "Potaro-Siparuni", "value": "PT"},
                {"label": "Upper Demerara-Berbice", "value": "UD"}, {
                    "label": "Upper Takutu-Upper Essequibo",
                    "value": "UT"
                }
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "HN":
            state_options = [
                {"label": "Atlántida", "value": "AT"}, {"label": "Choluteca", "value": "CH"}, {
                    "label": "Colón",
                    "value": "CL"
                }, {"label": "Comayagua", "value": "CM"},
                {"label": "Copán", "value": "CP"}, {"label": "Cortés", "value": "CR"}, {
                    "label": "El Paraíso",
                    "value": "EP"
                }, {"label": "Francisco Morazán", "value": "FM"},
                {"label": "Gracias a Dios", "value": "GD"}, {
                    "label": "Islas de la Bahía",
                    "value": "IB"
                }, {"label": "Intibucá", "value": "IN"}, {"label": "Lempira", "value": "LE"},
                {"label": "La Paz", "value": "LP"}, {"label": "Ocotepeque", "value": "OC"}, {
                    "label": "Olancho",
                    "value": "OL"
                }, {"label": "Santa Bárbara", "value": "SB"},
                {"label": "Valle", "value": "VA"}, {"label": "Yoro", "value": "YO"}
            ];
            document.getElementById(stateId).innerHTML = "Department";
            break;
        case "HR":
            state_options = [
                {"label": "Zagrebacka županija", "value": 1}, {
                    "label": "Krapinsko-zagorska županija",
                    "value": 2
                }, {"label": "Sisacko-moslavacka županija", "value": 3}, {"label": "Karlovacka županija", "value": 4},
                {"label": "Varaždinska županija", "value": 5}, {
                    "label": "Koprivnicko-križevacka županija",
                    "value": 6
                }, {"label": "Bjelovarsko-bilogorska županija", "value": 7}, {
                    "label": "Primorsko-goranska županija",
                    "value": 8
                },
                {"label": "Licko-senjska županija", "value": 9}, {
                    "label": "Viroviticko-podravska županija",
                    "value": 10
                }, {"label": "Požeško-slavonska županija", "value": 11}, {
                    "label": "Brodsko-posavska županija",
                    "value": 12
                },
                {"label": "Zadarska županija", "value": 13}, {
                    "label": "Osjecko-baranjska županija",
                    "value": 14
                }, {"label": "Šibensko-kninska županija", "value": 15}, {
                    "label": "Vukovarsko-srijemska županija",
                    "value": 16
                },
                {"label": "Splitsko-dalmatinska županija", "value": 17}, {
                    "label": "Istarska županija",
                    "value": 18
                }, {"label": "Dubrovacko-neretvanska županija", "value": 19}, {
                    "label": "Medimurska županija",
                    "value": 20
                },
                {"label": "Grad Zagreb", "value": 21}
            ];
            document.getElementById(stateId).innerHTML = "County";
            break;
        case "HT":
            state_options = [
                {"label": "Artibonite", "value": "AR"}, {"label": "Centre", "value": "CE"}, {
                    "label": "Grande’Anse",
                    "value": "GA"
                }, {"label": "Nord", "value": "ND"},
                {"label": "Nord-Est", "value": "NE"}, {"label": "Nippes", "value": "NI"}, {
                    "label": "Nord-Ouest",
                    "value": "NO"
                }, {"label": "Ouest", "value": "OU"},
                {"label": "Sud", "value": "SD"}, {"label": "Sud-Est", "value": "SE"}
            ];
            document.getElementById(stateId).innerHTML = "Department";
            break;
        case "HU":
            state_options = [
                {"label": "Baranya", "value": "BA"}, {"label": "Békéscsaba", "value": "BC"}, {
                    "label": "Békés",
                    "value": "BE"
                }, {"label": "Bács-Kiskun", "value": "BK"},
                {"label": "Budapest", "value": "BU"}, {
                    "label": "Borsod-Abaúj-Zemplén",
                    "value": "BZ"
                }, {"label": "Csongrád", "value": "CS"}, {"label": "Debrecen", "value": "DE"},
                {"label": "Dunaújváros", "value": "DU"}, {"label": "Eger", "value": "EG"}, {
                    "label": "Érd",
                    "value": "ER"
                }, {"label": "Fejér", "value": "FE"},
                {"label": "Gyor-Moson-Sopron", "value": "GS"}, {
                    "label": "Gyor",
                    "value": "GY"
                }, {"label": "Hajdú-Bihar", "value": "HB"}, {"label": "Heves", "value": "HE"},
                {"label": "Hódmezovásárhely", "value": "HV"}, {
                    "label": "Jász-Nagykun-Szolnok",
                    "value": "JN"
                }, {"label": "Komárom-Esztergom", "value": "KE"}, {"label": "Kecskemét", "value": "KM"},
                {"label": "Kaposvár", "value": "KV"}, {"label": "Miskolc", "value": "MI"}, {
                    "label": "Nagykanizsa",
                    "value": "NK"
                }, {"label": "Nógrád", "value": "NO"},
                {"label": "Nyíregyháza", "value": "NY"}, {"label": "Pest", "value": "PE"}, {
                    "label": "Pécs",
                    "value": "PS"
                }, {"label": "Szeged", "value": "SD"},
                {"label": "Székesfehérvár", "value": "SF"}, {
                    "label": "Szombathely",
                    "value": "SH"
                }, {"label": "Szolnok", "value": "SK"}, {"label": "Sopron", "value": "SN"},
                {"label": "Somogy", "value": "SO"}, {"label": "Szekszárd", "value": "SS"}, {
                    "label": "Salgótarján",
                    "value": "ST"
                }, {"label": "Szabolcs-Szatmár-Bereg", "value": "SZ"},
                {"label": "Tatabánya", "value": "TB"}, {"label": "Tolna", "value": "TO"}, {
                    "label": "Vas",
                    "value": "VA"
                }, {"label": "Veszprém", "value": "VE"},
                {"label": "Veszprém", "value": "VM"}, {"label": "Zala", "value": "ZA"}, {
                    "label": "Zalaegerszeg",
                    "value": "ZE"
                }
            ];
            document.getElementById(stateId).innerHTML = "City with county rights";
            break;
        case "ID":
            state_options = [
                {"label": "Aceh", "value": "AC"}, {
                    "label": "Bali",
                    "value": "BA"
                }, {"label": "Kepulauan Bangka Belitung", "value": "BB"}, {"label": "Bengkulu", "value": "BE"},
                {"label": "Banten", "value": "BT"}, {"label": "Gorontalo", "value": "GO"}, {
                    "label": "Jambi",
                    "value": "JA"
                }, {"label": "Jawa Barat", "value": "JB"},
                {"label": "Jawa Timur", "value": "JI"}, {
                    "label": "Jakarta Raya",
                    "value": "JK"
                }, {"label": "Jawa Tengah", "value": "JT"}, {"label": "Kalimantan Barat", "value": "KB"},
                {"label": "Kalimantan Timur", "value": "KI"}, {
                    "label": "Kepulauan Riau",
                    "value": "KR"
                }, {"label": "Kalimantan Selatan", "value": "KS"}, {"label": "Kalimantan Tengah", "value": "KT"},
                {"label": "Kalimantan Utara", "value": "KU"}, {"label": "Lampung", "value": "LA"}, {
                    "label": "Maluku",
                    "value": "MA"
                }, {"label": "Maluku Utara", "value": "MU"},
                {"label": "Nusa Tenggara Barat", "value": "NB"}, {
                    "label": "Nusa Tenggara Timur",
                    "value": "NT"
                }, {"label": "Papua", "value": "PA"}, {"label": "Papua Barat", "value": "PB"},
                {"label": "Riau", "value": "RI"}, {
                    "label": "Sulawesi Utara",
                    "value": "SA"
                }, {"label": "Sumatera Barat", "value": "SB"}, {"label": "Sulawesi Tenggara", "value": "SG"},
                {"label": "Sulawesi Selatan", "value": "SN"}, {
                    "label": "Sulawesi Barat",
                    "value": "SR"
                }, {"label": "Sumatera Selatan", "value": "SS"}, {"label": "Sulawesi Tengah", "value": "ST"},
                {"label": "Sumatera Utara", "value": "SU"}, {"label": "Yogyakarta", "value": "YO"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "IE":
            state_options = [
                {"label": "Clare", "value": "CE"}, {"label": "Cavan", "value": "CN"}, {
                    "label": "Cork",
                    "value": "CO"
                }, {"label": "Carlow", "value": "CW"},
                {"label": "Dublin", "value": "D"}, {"label": "Donegal", "value": "DL"}, {
                    "label": "Galway",
                    "value": "G"
                }, {"label": "Kildare", "value": "KE"},
                {"label": "Kilkenny", "value": "KK"}, {"label": "Kerry", "value": "KY"}, {
                    "label": "Longford",
                    "value": "LD"
                }, {"label": "Louth", "value": "LH"},
                {"label": "Limerick", "value": "LK"}, {"label": "Leitrim", "value": "LM"}, {
                    "label": "Laois",
                    "value": "LS"
                }, {"label": "Meath", "value": "MH"},
                {"label": "Monaghan", "value": "MN"}, {"label": "Mayo", "value": "MO"}, {
                    "label": "Offaly",
                    "value": "OY"
                }, {"label": "Roscommon", "value": "RN"},
                {"label": "Sligo", "value": "SO"}, {"label": "Tipperary", "value": "TA"}, {
                    "label": "Waterford",
                    "value": "WD"
                }, {"label": "Westmeath", "value": "WH"},
                {"label": "Wicklow", "value": "WW"}, {"label": "Wexford", "value": "WX"}
            ];
            document.getElementById(stateId).innerHTML = "County";
            break;
        case "IL":
            state_options = [
                {"label": "Al Janubi", "value": "D"}, {"label": "H_efa", "value": "HA"}, {
                    "label": "Yerushalayim",
                    "value": "JM"
                }, {"label": "Al Awsat", "value": "M"},
                {"label": "Tel-Aviv", "value": "TA"}, {"label": "Ash Shamali", "value": "Z"}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "IN":
            state_options = [
                {"label": "Andaman and Nicobar Islands", "value": "AN"}, {
                    "label": "Andhra Pradesh",
                    "value": "AP"
                }, {"label": "Arunachal Pradesh", "value": "AR"}, {"label": "Assam", "value": "AS"},
                {"label": "Bihar", "value": "BR"}, {"label": "Chandigarh", "value": "CH"}, {
                    "label": "Chhattisgarh",
                    "value": "CT"
                }, {"label": "Daman and Diu", "value": "DD"},
                {"label": "Delhi", "value": "DL"}, {"label": "Dadra and Nagar Haveli", "value": "DN"}, {
                    "label": "Goa",
                    "value": "GA"
                }, {"label": "Gujarat", "value": "GJ"},
                {"label": "Himachal Pradesh", "value": "HP"}, {
                    "label": "Haryana",
                    "value": "HR"
                }, {"label": "Jharkhand", "value": "JH"}, {"label": "Jammu and Kashmir", "value": "JK"},
                {"label": "Karnataka", "value": "KA"}, {"label": "Kerala", "value": "KL"}, {
                    "label": "Lakshadweep",
                    "value": "LD"
                }, {"label": "Maharashtra", "value": "MH"},
                {"label": "Meghalaya", "value": "ML"}, {"label": "Manipur", "value": "MN"}, {
                    "label": "Madhya Pradesh",
                    "value": "MP"
                }, {"label": "Mizoram", "value": "MZ"},
                {"label": "Nagaland", "value": "NL"}, {"label": "Odisha", "value": "OR"}, {
                    "label": "Punjab",
                    "value": "PB"
                }, {"label": "Puducherry", "value": "PY"},
                {"label": "Rajasthan", "value": "RJ"}, {"label": "Sikkim", "value": "SK"}, {
                    "label": "Telangana",
                    "value": "TG"
                }, {"label": "Tamil Nadu", "value": "TN"},
                {"label": "Tripura", "value": "TR"}, {"label": "Uttar Pradesh", "value": "UP"}, {
                    "label": "Uttarakhand",
                    "value": "UT"
                }, {"label": "West Bengal", "value": "WB"}
            ];
            document.getElementById(stateId).innerHTML = "State";
            break;
        case "IQ":
            state_options = [
                {"label": "AI Anbar", "value": "AN"}, {"label": "Arbil", "value": "AR"}, {
                    "label": "Al Basrah",
                    "value": "BA"
                }, {"label": "Babil", "value": "BB"},
                {"label": "Baghdad", "value": "BG"}, {"label": "Dahuk", "value": "DA"}, {
                    "label": "Diyalá",
                    "value": "DI"
                }, {"label": "Dhi Qar", "value": "DQ"},
                {"label": "Karbala'", "value": "KA"}, {"label": "Kirkuk", "value": "KI"}, {
                    "label": "Maysan",
                    "value": "MA"
                }, {"label": "AI Muthanná", "value": "MU"},
                {"label": "An Najaf", "value": "NA"}, {"label": "Ninawá", "value": "NI"}, {
                    "label": "Al Qadisiyah",
                    "value": "QA"
                }, {"label": "Salah ad Din", "value": "SD"},
                {"label": "As Sulaymaniyah", "value": "SU"}, {"label": "Wasit", "value": "WA"}
            ];
            document.getElementById(stateId).innerHTML = "Governorate";
            break;
        case "IR":
            state_options = [
                {"label": "AZarbayjan-e Sharqi", "value": 1}, {
                    "label": "AZarbayjan-e Gharbi",
                    "value": 2
                }, {"label": "Ardabil", "value": 3}, {"label": "Esfahan", "value": 4},
                {"label": "Ilam", "value": 5}, {"label": "Bushehr", "value": 6}, {
                    "label": "Tehran",
                    "value": 7
                }, {"label": "Chahar Ma?al va Bakhtiari", "value": 8},
                {"label": "Khuzestan", "value": 10}, {"label": "Zanjan", "value": 11}, {
                    "label": "Semnan",
                    "value": 12
                }, {"label": "Sistan va Baluchestan", "value": 13},
                {"label": "Fars", "value": 14}, {"label": "Kerman", "value": 15}, {
                    "label": "Kordestan",
                    "value": 16
                }, {"label": "Kermanshah", "value": 17},
                {"label": "Kohgiluyeh va Bowyer A?mad", "value": 18}, {
                    "label": "Gilan",
                    "value": 19
                }, {"label": "Lorestan", "value": 20}, {"label": "Mazandaran", "value": 21},
                {"label": "Markazi", "value": 22}, {"label": "Hormozgan", "value": 23}, {
                    "label": "Hamadan",
                    "value": 24
                }, {"label": "Yazd", "value": 25},
                {"label": "Qom", "value": 26}, {"label": "Golestan", "value": 27}, {
                    "label": "Qazvin",
                    "value": 28
                }, {"label": "Khorasan-e Jonubi", "value": 29},
                {"label": "Khorasan-e Ra?avi", "value": 30}, {
                    "label": "Khorasan-e Shomali",
                    "value": 31
                }, {"label": "Alborz", "value": 32}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "IS":
            state_options = [
                {"label": "Höfuðborgarsvæði", "value": 1}, {"label": "Suðurnes", "value": 2}, {
                    "label": "Vesturland",
                    "value": 3
                }, {"label": "Vestfirðir", "value": 4},
                {"label": "Norðurland vestra", "value": 5}, {
                    "label": "Norðurland eystra",
                    "value": 6
                }, {"label": "Austurland", "value": 7}, {"label": "Suðurland", "value": 8}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "IT":
            state_options = [
                {"label": "Agrigento", "value": "AG"}, {"label": "Alessandria", "value": "AL"}, {
                    "label": "Ancona",
                    "value": "AN"
                }, {"label": "Aosta / Aoste (fr)", "value": "AO"},
                {"label": "Ascoli Piceno", "value": "AP"}, {"label": "L'Aquila", "value": "AQ"}, {
                    "label": "Arezzo",
                    "value": "AR"
                }, {"label": "Asti", "value": "AT"},
                {"label": "Avellino", "value": "AV"}, {"label": "Bari", "value": "BA"}, {
                    "label": "Bergamo",
                    "value": "BG"
                }, {"label": "Biella", "value": "BI"},
                {"label": "Belluno", "value": "BL"}, {"label": "Benevento", "value": "BN"}, {
                    "label": "Bologna",
                    "value": "BO"
                }, {"label": "Brindisi", "value": "BR"},
                {"label": "Brescia", "value": "BS"}, {
                    "label": "Barletta-Andria-Trani",
                    "value": "BT"
                }, {"label": "Bolzano / Bozen (de)", "value": "BZ"}, {"label": "Cagliari", "value": "CA"},
                {"label": "Campobasso", "value": "CB"}, {"label": "Caserta", "value": "CE"}, {
                    "label": "Chieti",
                    "value": "CH"
                }, {"label": "Carbonia-Iglesias", "value": "CI"},
                {"label": "Caltanissetta", "value": "CL"}, {"label": "Cuneo", "value": "CN"}, {
                    "label": "Como",
                    "value": "CO"
                }, {"label": "Cremona", "value": "CR"},
                {"label": "Cosenza", "value": "CS"}, {"label": "Catania", "value": "CT"}, {
                    "label": "Catanzaro",
                    "value": "CZ"
                }, {"label": "Enna", "value": "EN"},
                {"label": "Forlì-Cesena", "value": "FC"}, {"label": "Ferrara", "value": "FE"}, {
                    "label": "Foggia",
                    "value": "FG"
                }, {"label": "Firenze", "value": "FI"},
                {"label": "Fermo", "value": "FM"}, {"label": "Frosinone", "value": "FR"}, {
                    "label": "Genova",
                    "value": "GE"
                }, {"label": "Gorizia", "value": "GO"},
                {"label": "Grosseto", "value": "GR"}, {"label": "Imperia", "value": "IM"}, {
                    "label": "Isernia",
                    "value": "IS"
                }, {"label": "Crotone", "value": "KR"},
                {"label": "Lecco", "value": "LC"}, {"label": "Lecce", "value": "LE"}, {
                    "label": "Livorno",
                    "value": "LI"
                }, {"label": "Lodi", "value": "LO"},
                {"label": "Latina", "value": "LT"}, {"label": "Lucca", "value": "LU"}, {
                    "label": "Monza e Brianza",
                    "value": "MB"
                }, {"label": "Macerata", "value": "MC"},
                {"label": "Messina", "value": "ME"}, {"label": "Milano", "value": "MI"}, {
                    "label": "Mantova",
                    "value": "MN"
                }, {"label": "Modena", "value": "MO"},
                {"label": "Massa-Carrara", "value": "MS"}, {"label": "Matera", "value": "MT"}, {
                    "label": "Napoli",
                    "value": "NA"
                }, {"label": "Novara", "value": "NO"},
                {"label": "Nuoro", "value": "NU"}, {"label": "Ogliastra", "value": "OG"}, {
                    "label": "Oristano",
                    "value": "OR"
                }, {"label": "Olbia-Tempio", "value": "OT"},
                {"label": "Palermo", "value": "PA"}, {"label": "Piacenza", "value": "PC"}, {
                    "label": "Padova",
                    "value": "PD"
                }, {"label": "Pescara", "value": "PE"},
                {"label": "Perugia", "value": "PG"}, {"label": "Pisa", "value": "PI"}, {
                    "label": "Pordenone",
                    "value": "PN"
                }, {"label": "Prato", "value": "PO"},
                {"label": "Parma", "value": "PR"}, {"label": "Pistoia", "value": "PT"}, {
                    "label": "Pesaro e Urbino",
                    "value": "PU"
                }, {"label": "Pavia", "value": "PV"},
                {"label": "Potenza", "value": "PZ"}, {"label": "Ravenna", "value": "RA"}, {
                    "label": "Reggio Calabria",
                    "value": "RC"
                }, {"label": "Reggio Emilia", "value": "RE"},
                {"label": "Ragusa", "value": "RG"}, {"label": "Rieti", "value": "RI"}, {
                    "label": "Roma",
                    "value": "RM"
                }, {"label": "Rimini", "value": "RN"},
                {"label": "Rovigo", "value": "RO"}, {"label": "Salerno", "value": "SA"}, {
                    "label": "Siena",
                    "value": "SI"
                }, {"label": "Sondrio", "value": "SO"},
                {"label": "La Spezia", "value": "SP"}, {"label": "Siracusa", "value": "SR"}, {
                    "label": "Sassari",
                    "value": "SS"
                }, {"label": "Savona", "value": "SV"},
                {"label": "Taranto", "value": "TA"}, {"label": "Teramo", "value": "TE"}, {
                    "label": "Trento",
                    "value": "TN"
                }, {"label": "Torino", "value": "TO"},
                {"label": "Trapani", "value": "TP"}, {"label": "Terni", "value": "TR"}, {
                    "label": "Trieste",
                    "value": "TS"
                }, {"label": "Treviso", "value": "TV"},
                {"label": "Udine", "value": "UD"}, {"label": "Varese", "value": "VA"}, {
                    "label": "Verbano-Cusio-Ossola",
                    "value": "VB"
                }, {"label": "Vercelli", "value": "VC"},
                {"label": "Venezia", "value": "VE"}, {"label": "Vicenza", "value": "VI"}, {
                    "label": "Verona",
                    "value": "VR"
                }, {"label": "Medio Campidano", "value": "VS"},
                {"label": "Viterbo", "value": "VT"}, {"label": "Vibo Valentia", "value": "VV"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "JM":
            state_options = [
                {"label": "Kingston", "value": 1}, {"label": "Saint Andrew", "value": 2}, {
                    "label": "Saint Thomas",
                    "value": 3
                }, {"label": "Portland", "value": 4},
                {"label": "Saint Mary", "value": 5}, {"label": "Saint Ann", "value": 6}, {
                    "label": "Trelawny",
                    "value": 7
                }, {"label": "Saint James", "value": 8},
                {"label": "Hanover", "value": 9}, {"label": "Westmoreland", "value": 10}, {
                    "label": "Saint Elizabeth",
                    "value": 11
                }, {"label": "Manchester", "value": 12},
                {"label": "Clarendon", "value": 13}, {"label": "Saint Catherine", "value": 14}
            ];
            document.getElementById(stateId).innerHTML = "Partish";
            break;
        case "JO":
            state_options = [
                {"label": "‘Ajlun", "value": "AJ"}, {"label": "Al ‘A¯simah", "value": "AM"}, {
                    "label": "Al ‘Aqabah",
                    "value": "AQ"
                }, {"label": "At Tafilah", "value": "AT"},
                {"label": "Az Zarqa’", "value": "AZ"}, {"label": "Al Balqa’", "value": "BA"}, {
                    "label": "Irbid",
                    "value": "IR"
                }, {"label": "Jarash", "value": "JA"},
                {"label": "AI Karak", "value": "KA"}, {"label": "AI Mafraq", "value": "MA"}, {
                    "label": "Madaba",
                    "value": "MD"
                }, {"label": "Ma‘an", "value": "MN"}
            ];
            document.getElementById(stateId).innerHTML = "Governorate";
            break;
        case "JP":
            state_options = [
                {"label": "Hokkaidô [Hokkaido]", "value": 1}, {"label": "Aomori", "value": 2}, {
                    "label": "Iwate",
                    "value": 3
                }, {"label": "Miyagi", "value": 4},
                {"label": "Akita", "value": 5}, {"label": "Yamagata", "value": 6}, {
                    "label": "Hukusima [Fukushima]",
                    "value": 7
                }, {"label": "Ibaraki", "value": 8},
                {"label": "Totigi [Tochigi]", "value": 9}, {"label": "Gunma", "value": 10}, {
                    "label": "Saitama",
                    "value": 11
                }, {"label": "Tiba [Chiba]", "value": 12},
                {"label": "Tôkyô [Tokyo]", "value": 13}, {"label": "Kanagawa", "value": 14}, {
                    "label": "Niigata",
                    "value": 15
                }, {"label": "Toyama", "value": 16},
                {"label": "Isikawa [Ishikawa]", "value": 17}, {
                    "label": "Hukui [Fukui]",
                    "value": 18
                }, {"label": "Yamanasi [Yamanashi]", "value": 19}, {"label": "Nagano", "value": 20},
                {"label": "Gihu [Gifu]", "value": 21}, {
                    "label": "Sizuoka [Shizuoka]",
                    "value": 22
                }, {"label": "Aiti [Aichi]", "value": 23}, {"label": "Mie", "value": 24},
                {"label": "Siga [Shiga]", "value": 25}, {
                    "label": "Hyôgo [Kyoto]",
                    "value": 26
                }, {"label": "Ôsaka [Osaka]", "value": 27}, {"label": "Hyôgo[Hyogo]", "value": 28},
                {"label": "Nara", "value": 29}, {"label": "Wakayama", "value": 30}, {
                    "label": "Tottori",
                    "value": 31
                }, {"label": "Simane [Shimane]", "value": 32},
                {"label": "Okayama", "value": 33}, {
                    "label": "Hirosima [Hiroshima]",
                    "value": 34
                }, {"label": "Yamaguti [Yamaguchi]", "value": 35}, {"label": "Tokusima [Tokushima]", "value": 36},
                {"label": "Kagawa", "value": 37}, {"label": "Ehime", "value": 38}, {
                    "label": "Kôti [Kochi]",
                    "value": 39
                }, {"label": "Hukuoka [Fukuoka]", "value": 40},
                {"label": "Saga", "value": 41}, {"label": "Nagasaki", "value": 42}, {
                    "label": "Kumamoto",
                    "value": 43
                }, {"label": "Ôita [Oita]", "value": 44},
                {"label": "Miyazaki", "value": 45}, {"label": "Kagosima [Kagoshima]", "value": 46}, {
                    "label": "Okinawa",
                    "value": 47
                }
            ];
            document.getElementById(stateId).innerHTML = "Prefecture";
            break;
        case "KE":
            state_options = [
                {"label": "Baringo", "value": 1}, {"label": "Bomet", "value": 2}, {
                    "label": "Bungoma",
                    "value": 3
                }, {"label": "Busia", "value": 4},
                {"label": "Elgeyo/Marakwet", "value": 5}, {"label": "Embu", "value": 6}, {
                    "label": "Garissa",
                    "value": 7
                }, {"label": "Homa Bay", "value": 8},
                {"label": "Isiolo", "value": 9}, {"label": "Kajiado", "value": 10}, {
                    "label": "Kakamega",
                    "value": 11
                }, {"label": "Kericho", "value": 12},
                {"label": "Kiambu", "value": 13}, {"label": "Kilifi", "value": 14}, {
                    "label": "Kirinyaga",
                    "value": 15
                }, {"label": "Kisii", "value": 16},
                {"label": "Kisumu", "value": 17}, {"label": "Kitui", "value": 18}, {
                    "label": "Kwale",
                    "value": 19
                }, {"label": "Laikipia", "value": 20},
                {"label": "Lamu", "value": 21}, {"label": "Machakos", "value": 22}, {
                    "label": "Makueni",
                    "value": 23
                }, {"label": "Mandera", "value": 24},
                {"label": "Marsabit", "value": 25}, {"label": "Meru", "value": 26}, {
                    "label": "Migori",
                    "value": 27
                }, {"label": "Mombasa", "value": 28},
                {"label": "Murang'a", "value": 29}, {"label": "Nairobi City", "value": 30}, {
                    "label": "Nakuru",
                    "value": 31
                }, {"label": "Nandi", "value": 32},
                {"label": "Narok", "value": 33}, {"label": "Nyamira", "value": 34}, {
                    "label": "Nyandarua",
                    "value": 35
                }, {"label": "Nyeri", "value": 36},
                {"label": "Samburu", "value": 37}, {"label": "Siaya", "value": 38}, {
                    "label": "Taita/Taveta",
                    "value": 39
                }, {"label": "Tana River", "value": 40},
                {"label": "Tharaka-Nithi", "value": 41}, {"label": "Trans Nzoia", "value": 42}, {
                    "label": "Turkana",
                    "value": 43
                }, {"label": "Uasin Gishu", "value": 44},
                {"label": "Vihiga", "value": 45}, {"label": "Wajir", "value": 46}, {"label": "West Pokot", "value": 47}
            ];
            document.getElementById(stateId).innerHTML = "County";
            break;
        case "KG":
            state_options = [
                {"label": "Batken", "value": "B"}, {"label": "Chüy", "value": "C"}, {
                    "label": "Bishkek",
                    "value": "GB"
                }, {"label": "Osh", "value": "GO"},
                {"label": "Jalal-Abad", "value": "J"}, {"label": "Naryn", "value": "N"}, {
                    "label": "Osh",
                    "value": "O"
                }, {"label": "Talas", "value": "T"},
                {"label": "Ysyk-Köl", "value": "Y"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "KH":
            state_options = [
                {"label": "Banteay Mean Chey [Bântéay Méanchey]", "value": 1}, {
                    "label": "Kracheh [Krâchéh]",
                    "value": 10
                }, {"label": "Mondol Kiri [Môndól Kiri]", "value": 11}, {
                    "label": "Phnom Penh [Phnum Pénh]",
                    "value": 12
                },
                {"label": "Preah Vihear [Preah Vihéar]", "value": 13}, {
                    "label": "Prey Veaeng [Prey Vêng]",
                    "value": 14
                }, {"label": "Pousaat [Pouthisat]", "value": 15}, {"label": "Rotanak Kiri [Rôtânôkiri]", "value": 16},
                {"label": "Siem Reab [Siemréab]", "value": 17}, {
                    "label": "Krong Preah Sihanouk [Krong Preah Sihanouk]",
                    "value": 18
                }, {"label": "Stueng Traeng [Stoeng Trêng]", "value": 19}, {
                    "label": "Baat Dambang [Batdâmbâng]",
                    "value": 2
                },
                {"label": "Svaay Rieng [Svay Rieng]", "value": 20}, {
                    "label": "Taakaev [Takêv]",
                    "value": 21
                }, {"label": "Otdar Mean Chey [Otdâr Méanchey]", "value": 22}, {
                    "label": "Krong Kaeb [Krong Kêb]",
                    "value": 23
                },
                {"label": "Krong Pailin [Krong Pailin]", "value": 24}, {
                    "label": "Tbong Khmum",
                    "value": 25
                }, {"label": "Kampong Chaam [Kâmpóng Cham]", "value": 3}, {
                    "label": "Kampong Chhnang [Kâmpóng Chhnang]",
                    "value": 4
                },
                {"label": "Kampong Spueu [Kâmpóng Spœ]", "value": 5}, {
                    "label": "Kampong Thum [Kâmpóng Thum]",
                    "value": 6
                }, {"label": "Kampot [Kâmpôt]", "value": 7}, {"label": "Kandaal [Kândal]", "value": 8},
                {"label": "Kaoh Kong [Kaôh Kong]", "value": 9}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "KI":
            state_options = [
                {"label": "Gilbert Islands", "value": "G"}, {
                    "label": "Line Islands",
                    "value": "L"
                }, {"label": "Phoenix Islands", "value": "P"}
            ];
            document.getElementById(stateId).innerHTML = "Group of islands";
            break;
        case "KM":
            state_options = [
                {"label": "Anjouan", "value": "A"}, {"label": "Grande Comore", "value": "G"}, {
                    "label": "Mohéli",
                    "value": "M"
                }
            ];
            document.getElementById(stateId).innerHTML = "Island";
            break;
        case "KN":
            state_options = [
                {"label": "Christ Church Nichola Town", "value": 1}, {
                    "label": "Saint Anne Sandy Point",
                    "value": 2
                }, {"label": "Saint George Basseterre", "value": 3}, {"label": "Saint George Gingerland", "value": 4},
                {"label": "Saint James Windward", "value": 5}, {
                    "label": "Saint John Capisterre",
                    "value": 6
                }, {"label": "Saint John Figtree", "value": 7}, {"label": "Saint Mary Cayon", "value": 8},
                {"label": "Saint Paul Capisterre", "value": 9}, {
                    "label": "Saint Paul Charlestown",
                    "value": 10
                }, {"label": "Saint Peter Basseterre", "value": 11}, {"label": "Saint Thomas Lowland", "value": 12},
                {"label": "Saint Thomas Middle Island", "value": 13}, {"label": "Trinity Palmetto Point", "value": 15}
            ];
            document.getElementById(stateId).innerHTML = "Parish";
            break;
        case "KP":
            state_options = [
                {"label": "Phyeongyang", "value": 1}, {
                    "label": "Phyeongannamto",
                    "value": 2
                }, {"label": "Phyeonganpukto", "value": 3}, {"label": "Jakangto", "value": 4},
                {"label": "Hwanghainamto", "value": 5}, {"label": "Hwanghaipukto", "value": 6}, {
                    "label": "Kangweonto",
                    "value": 7
                }, {"label": "Hamkyeongnamto", "value": 8},
                {"label": "Hamkyeongpukto", "value": 9}, {"label": "Ryanggang-do", "value": 10}, {
                    "label": "Rason",
                    "value": 13
                }, {"label": "Namp’o", "value": 14}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "KR":
            state_options = [
                {"label": "Seoul-teukbyeolsi [Seoul]", "value": 11}, {
                    "label": "Busan Gwang'yeogsi [Pusan-Kwangyokshi]",
                    "value": 26
                }, {"label": "Daegu Gwang'yeogsi [Taegu-Kwangyokshi]", "value": 27},
                {
                    "label": "Incheon Gwang'yeogsi [Incheon]",
                    "value": 28
                }, {
                    "label": "Gwangju Gwang'yeogsi [Kwangju-Kwangyokshi]",
                    "value": 29
                }, {
                    "label": "Daejeon Gwang'yeogsi [Taejon-Kwangyokshi]",
                    "value": 30
                }, {"label": "Ulsan Gwang'yeogsi [Ulsan-Kwangyokshi]", "value": 31},
                {"label": "Gyeonggido [Kyonggi-do]", "value": 41}, {
                    "label": "Gang'weondo [Kang-won-do]",
                    "value": 42
                }, {
                    "label": "Chungcheongbugdo [Ch'ungch'ongbuk-do]",
                    "value": 43
                }, {"label": "Chungcheongnamdo [Ch'ungch'ongnam-do]", "value": 44},
                {"label": "Jeonrabugdo [Chollabuk-do]", "value": 45}, {
                    "label": "Jeonranamdo [Chollanam-do]",
                    "value": 46
                }, {
                    "label": "Gyeongsangbugdo [Kyongsangbuk-do]",
                    "value": 47
                }, {"label": "Gyeongsangnamdo [Kyongsangnam-do]", "value": 48},
                {"label": "Jeju-teukbyeoljachido [Jeju]", "value": 49}, {"label": "Sejong", "value": 50}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "KW":
            state_options = [
                {"label": "Al A?madi", "value": "AH"}, {"label": "Al Farwaniyah", "value": "FA"}, {
                    "label": "?awalli",
                    "value": "HA"
                }, {"label": "Al Jahra’", "value": "JA"},
                {"label": "Al ‘Asimah", "value": "KU"}, {"label": "Mubarak al Kabir", "value": "MU"}
            ];
            document.getElementById(stateId).innerHTML = "Governorate";
            break;
        case "KZ":
            state_options = [
                {"label": "Aqmola oblysy", "value": "AKM"}, {
                    "label": "Aqtöbe oblysy",
                    "value": "AKT"
                }, {"label": "Almaty", "value": "ALA"}, {"label": "Almaty oblysy", "value": "ALM"},
                {"label": "Astana", "value": "AST"}, {"label": "Atyrau oblysy", "value": "ATY"}, {
                    "label": "Bayqonyr",
                    "value": "BAY"
                }, {"label": "Qaraghandy oblysy", "value": "KAR"},
                {"label": "Qostanay oblysy", "value": "KUS"}, {
                    "label": "Qyzylorda oblysy",
                    "value": "KZY"
                }, {"label": "Mangghystau oblysy", "value": "MAN"}, {"label": "Pavlodar oblysy", "value": "PAV"},
                {"label": "Soltüstik Qazaqstan oblysy", "value": "SEV"}, {
                    "label": "Shyghys Qazaqstan oblysy",
                    "value": "VOS"
                }, {"label": "Ongtüstik Qazaqstan oblysy", "value": "YUZ"}, {
                    "label": "Batys Qazaqstan oblysy",
                    "value": "ZAP"
                },
                {"label": "Zhambyl oblysy", "value": "ZHA"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "LA":
            state_options = [
                {"label": "Attapu [Attapeu]", "value": "AT"}, {
                    "label": "Bokèo",
                    "value": "BK"
                }, {"label": "Bolikhamxai [Borikhamxay]", "value": "BL"}, {
                    "label": "Champasak [Champasack]",
                    "value": "CH"
                },
                {"label": "Houaphan [Huaphanh]", "value": "HO"}, {
                    "label": "Khammouan [Khammuane]",
                    "value": "KH"
                }, {"label": "Louang Namtha [Luangnamtha]", "value": "LM"}, {
                    "label": "Louangphabang [Luangprabang]",
                    "value": "LP"
                },
                {"label": "Oudômxai [Oudomxay]", "value": "OU"}, {
                    "label": "Phôngsali [Phongsaly]",
                    "value": "PH"
                }, {"label": "Salavan [Saravane]", "value": "SL"}, {
                    "label": "Savannakhét [Savannakhet]",
                    "value": "SV"
                },
                {"label": "Vientiane", "value": "VI"}, {
                    "label": "Vientiane",
                    "value": "VT"
                }, {"label": "Xaignabouli [Xayabury]", "value": "XA"}, {"label": "Xékong [Sekong]", "value": "XE"},
                {"label": "Xiangkhouang [Xiengkhuang]", "value": "XI"}, {
                    "label": "Xaisômboun [Xaysomboon]",
                    "value": "XS"
                }
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "LB":
            state_options = [
                {"label": "Aakkâr", "value": "AK"}, {"label": "Liban-Nord", "value": "AS"}, {
                    "label": "Beyrouth",
                    "value": "BA"
                }, {"label": "Baalbek-Hermel", "value": "BH"},
                {"label": "El Béqaa", "value": "BI"}, {"label": "Liban-Sud", "value": "JA"}, {
                    "label": "Mont-Liban",
                    "value": "JL"
                }, {"label": "Nabatîyé", "value": "NA"}
            ];
            document.getElementById(stateId).innerHTML = "Governorate";
            break;
        case "LC":
            state_options = [
                {"label": "Anse la Raye", "value": 1}, {"label": "Castries", "value": 2}, {
                    "label": "Choiseul",
                    "value": 3
                }, {"label": "Dennery", "value": 5},
                {"label": "Gros Islet", "value": 6}, {"label": "Laborie", "value": 7}, {
                    "label": "Micoud",
                    "value": 8
                }, {"label": "Soufrière", "value": 10},
                {"label": "Vieux Fort", "value": 11}, {"label": "Canaries", "value": 12}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "LI":
            state_options = [
                {"label": "Balzers", "value": 1}, {"label": "Eschen", "value": 2}, {
                    "label": "Gamprin",
                    "value": 3
                }, {"label": "Mauren", "value": 4},
                {"label": "Planken", "value": 5}, {"label": "Ruggell", "value": 6}, {
                    "label": "Schaan",
                    "value": 7
                }, {"label": "Schellenberg", "value": 8},
                {"label": "Triesen", "value": 9}, {"label": "Triesenberg", "value": 10}, {"label": "Vaduz", "value": 11}
            ];
            document.getElementById(stateId).innerHTML = "Commune";
            break;
        case "LK":
            state_options = [
                {"label": "Basnahira pa?ata", "value": 1}, {"label": "Colombo", "value": 11}, {
                    "label": "Gampaha",
                    "value": 12
                }, {"label": "Kalutara", "value": 13},
                {"label": "Madhyama pa?ata", "value": 2}, {"label": "Kandy", "value": 21}, {
                    "label": "Matale",
                    "value": 22
                }, {"label": "Nuwara Eliya", "value": 23},
                {"label": "Daku?u pa?ata", "value": 3}, {"label": "Galle", "value": 31}, {
                    "label": "Matara",
                    "value": 32
                }, {"label": "Hambantota", "value": 33},
                {"label": "Uturu pa?ata", "value": 4}, {"label": "Jaffna", "value": 41}, {
                    "label": "Kilinochchi",
                    "value": 42
                }, {"label": "Mannar", "value": 43},
                {"label": "Vavuniya", "value": 44}, {
                    "label": "Mullaittivu",
                    "value": 45
                }, {"label": "Næ?genahira pa?ata", "value": 5}, {"label": "Batticaloa", "value": 51},
                {"label": "Ampara", "value": 52}, {"label": "Trincomalee", "value": 53}, {
                    "label": "Vayamba pa?ata",
                    "value": 6
                }, {"label": "Kurunegala", "value": 61},
                {"label": "Puttalam", "value": 62}, {
                    "label": "Uturumæ?da pa?ata",
                    "value": 7
                }, {"label": "Anuradhapura", "value": 71}, {"label": "Polonnaruwa", "value": 72},
                {"label": "Uva pa?ata", "value": 8}, {"label": "Badulla", "value": 81}, {
                    "label": "Monaragala",
                    "value": 82
                }, {"label": "Sabaragamuva pa?ata", "value": 9},
                {"label": "Ratnapura", "value": 91}, {"label": "Kegalla", "value": 92}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "LR":
            state_options = [
                {"label": "Bong", "value": "BG"}, {"label": "Bomi", "value": "BM"}, {
                    "label": "Grand Cape Mount",
                    "value": "CM"
                }, {"label": "Grand Bassa", "value": "GB"},
                {"label": "Grand Gedeh", "value": "GG"}, {"label": "Grand Kru", "value": "GK"}, {
                    "label": "Gbarpolu",
                    "value": "GP"
                }, {"label": "Lofa", "value": "LO"},
                {"label": "Margibi", "value": "MG"}, {"label": "Montserrado", "value": "MO"}, {
                    "label": "Maryland",
                    "value": "MY"
                }, {"label": "Nimba", "value": "NI"},
                {"label": "River Gee", "value": "RG"}, {"label": "River Cess", "value": "RI"}, {
                    "label": "Sinoe",
                    "value": "SI"
                }
            ];
            document.getElementById(stateId).innerHTML = "County";
            break;
        case "LS":
            state_options = [
                {"label": "Maseru", "value": "A"}, {"label": "Butha-Buthe", "value": "B"}, {
                    "label": "Leribe",
                    "value": "C"
                }, {"label": "Berea", "value": "D"},
                {"label": "Mafeteng", "value": "E"}, {"label": "Mohale's Hoek", "value": "F"}, {
                    "label": "Quthing",
                    "value": "G"
                }, {"label": "Qacha's Nek", "value": "H"},
                {"label": "Mokhotlong", "value": "J"}, {"label": "Thaba-Tseka", "value": "K"}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "LT":
            state_options = [
                {"label": "Akmene", "value": 1}, {"label": "Alytaus miestas", "value": 2}, {
                    "label": "Alytus",
                    "value": 3
                }, {"label": "Anykšciai", "value": 4},
                {"label": "Birštono", "value": 5}, {"label": "Biržai", "value": 6}, {
                    "label": "Druskininkai",
                    "value": 7
                }, {"label": "Elektrénai", "value": 8},
                {"label": "Ignalina", "value": 9}, {"label": "Jonava", "value": 10}, {
                    "label": "Joniškis",
                    "value": 11
                }, {"label": "Jurbarkas", "value": 12},
                {"label": "Kaišiadorys", "value": 13}, {"label": "Kalvarijos", "value": 14}, {
                    "label": "Kauno miestas",
                    "value": 15
                }, {"label": "Kaunas", "value": 16},
                {"label": "Kazlu Rudos", "value": 17}, {"label": "Kedainiai", "value": 18}, {
                    "label": "Kelme",
                    "value": 19
                }, {"label": "Klaipedos miestas", "value": 20},
                {"label": "Klaipeda", "value": 21}, {"label": "Kretinga", "value": 22}, {
                    "label": "Kupiškis",
                    "value": 23
                }, {"label": "Lazdijai", "value": 24},
                {"label": "Marijampole", "value": 25}, {"label": "Mažeikiai", "value": 26}, {
                    "label": "Moletai",
                    "value": 27
                }, {"label": "Neringa", "value": 28},
                {"label": "Pagégiai", "value": 29}, {"label": "Pakruojis", "value": 30}, {
                    "label": "Palangos miestas",
                    "value": 31
                }, {"label": "Panevežio miestas", "value": 32},
                {"label": "Panevežys", "value": 33}, {"label": "Pasvalys", "value": 34}, {
                    "label": "Plunge",
                    "value": 35
                }, {"label": "Prienai", "value": 36},
                {"label": "Radviliškis", "value": 37}, {"label": "Raseiniai", "value": 38}, {
                    "label": "Rietavo",
                    "value": 39
                }, {"label": "Rokiškis", "value": 40},
                {"label": "Šakiai", "value": 41}, {"label": "Šalcininkai", "value": 42}, {
                    "label": "Šiauliu miestas",
                    "value": 43
                }, {"label": "Šiauliai", "value": 44},
                {"label": "Šilale", "value": 45}, {"label": "Šilute", "value": 46}, {
                    "label": "Širvintos",
                    "value": 47
                }, {"label": "Skuodas", "value": 48},
                {"label": "Švencionys", "value": 49}, {"label": "Taurage", "value": 50}, {
                    "label": "Telšiai",
                    "value": 51
                }, {"label": "Trakai", "value": 52},
                {"label": "Ukmerge", "value": 53}, {"label": "Utena", "value": 54}, {
                    "label": "Varena",
                    "value": 55
                }, {"label": "Vilkaviškis", "value": 56},
                {"label": "Vilniaus miestas", "value": 57}, {"label": "Vilnius", "value": 58}, {
                    "label": "Visaginas",
                    "value": 59
                }, {"label": "Zarasai", "value": 60},
                {"label": "Alytaus Apskritis", "value": "AL"}, {
                    "label": "Klaipedos apskritis",
                    "value": "KL"
                }, {"label": "Kauno Apskritis", "value": "KU"}, {"label": "Marijampoles apskritis", "value": "MR"},
                {"label": "Panevežio apskritis", "value": "PN"}, {
                    "label": "Šiauliu Apskritis",
                    "value": "SA"
                }, {"label": "Taurages apskritis", "value": "TA"}, {"label": "Telšiu Apskritis", "value": "TE"},
                {"label": "Utenos Apskritis", "value": "UT"}, {"label": "Vilniaus Apskritis", "value": "VL"}
            ];
            document.getElementById(stateId).innerHTML = "District municipality";
            break;
        case "LU":
            state_options = [
                {"label": "Capellen", "value": "CA"}, {"label": "Clervaux", "value": "CL"}, {
                    "label": "Diekirch",
                    "value": "DI"
                }, {"label": "Echternach", "value": "EC"},
                {"label": "Esch-sur-Alzette", "value": "ES"}, {
                    "label": "Gréivemaacher",
                    "value": "GR"
                }, {"label": "Luxembourg", "value": "LU"}, {"label": "Mersch", "value": "ME"},
                {"label": "Redange", "value": "RD"}, {"label": "Remich", "value": "RM"}, {
                    "label": "Vianden",
                    "value": "VD"
                }, {"label": "Wiltz", "value": "WI"}
            ];
            document.getElementById(stateId).innerHTML = "Canton";
            break;
        case "LV":
            state_options = [
                {"label": "Aglonas novads (Aglona)", "value": 1}, {
                    "label": "Aizkraukles novads (Aizkraukle)",
                    "value": 2
                }, {"label": "Aizputes novads (Aizpute)", "value": 3}, {
                    "label": "Aknistes novads (Akniste)",
                    "value": 4
                },
                {"label": "Alojas novads (Aloja)", "value": 5}, {
                    "label": "Alsungas novads (Alsunga)",
                    "value": 6
                }, {"label": "Aluksnes novads (Aluksne)", "value": 7}, {"label": "Amatas novads (Amata)", "value": 8},
                {"label": "Apes novads (Ape)", "value": 9}, {
                    "label": "Auces novads (Auce)",
                    "value": 10
                }, {"label": "Adažu novads (Adaži)", "value": 11}, {"label": "Babites novads (Babite)", "value": 12},
                {"label": "Baldones novads (Baldone)", "value": 13}, {
                    "label": "Baltinavas novads (Baltinava)",
                    "value": 14
                }, {"label": "Balvu novads (Balvi)", "value": 15}, {"label": "Bauskas novads (Bauska)", "value": 16},
                {"label": "Beverinas novads (Beverina)", "value": 17}, {
                    "label": "Brocenu novads (Broceni)",
                    "value": 18
                }, {"label": "Burtnieku novads (Burtnieki)", "value": 19}, {
                    "label": "Carnikavas novads (Carnikava)",
                    "value": 20
                },
                {"label": "Cesvaines novads (Cesvaine)", "value": 21}, {
                    "label": "Cesu novads (Cesis)",
                    "value": 22
                }, {"label": "Ciblas novads (Cibla)", "value": 23}, {"label": "Dagdas novads (Dagda)", "value": 24},
                {"label": "Daugavpils novads (Daugavpils)", "value": 25}, {
                    "label": "Dobeles novads (Dobele)",
                    "value": 26
                }, {"label": "Dundagas novads (Dundaga)", "value": 27}, {"label": "Durbes novads (Durbe)", "value": 28},
                {"label": "Engures novads (Engure)", "value": 29}, {
                    "label": "Erglu novads (Ergli)",
                    "value": 30
                }, {"label": "Garkalnes novads (Garkalne)", "value": 31}, {
                    "label": "Grobinas novads (Grobina)",
                    "value": 32
                },
                {"label": "Gulbenes novads (Gulbene)", "value": 33}, {
                    "label": "Iecavas novads (Iecava)",
                    "value": 34
                }, {"label": "Ikškiles novads (Ikškile)", "value": 35}, {
                    "label": "Ilukstes novads (Ilukste)",
                    "value": 36
                },
                {"label": "Incukalna novads (Incukalns)", "value": 37}, {
                    "label": "Jaunjelgavas novads (Jaunjelgava)",
                    "value": 38
                }, {
                    "label": "Jaunpiebalgas novads\n(Jaunpiebalga)",
                    "value": 39
                }, {"label": "Jaunpils novads (Jaunpils)", "value": 40},
                {"label": "Jelgavas novads (Jelgava)", "value": 41}, {
                    "label": "Jekabpils novads (Jekabpils)",
                    "value": 42
                }, {"label": "Kandavas novads (Kandava)", "value": 43}, {
                    "label": "Karsavas novads (Karsava)",
                    "value": 44
                },
                {"label": "Kocenu novads (Koceni)", "value": 45}, {
                    "label": "Kokneses novads (Koknese)",
                    "value": 46
                }, {"label": "Kraslavas novads (Kraslava)", "value": 47}, {
                    "label": "Krimuldas novads (Krimulda)",
                    "value": 48
                },
                {"label": "Krustpils novads (Krustpils)", "value": 49}, {
                    "label": "Kuldigas novads (Kuldiga)",
                    "value": 50
                }, {"label": "Keguma novads (Kegums)", "value": 51}, {"label": "Kekavas novads (Kekava)", "value": 52},
                {"label": "Lielvardes novads (Lielvarde)", "value": 53}, {
                    "label": "Limbažu novads (Limbaži)",
                    "value": 54
                }, {"label": "Ligatnes novads (Ligatne)", "value": 55}, {
                    "label": "Livanu novads (Livani)",
                    "value": 56
                },
                {"label": "Lubanas novads (Lubana)", "value": 57}, {
                    "label": "Ludzas novads (Ludza)",
                    "value": 58
                }, {"label": "Madonas novads (Madona)", "value": 59}, {
                    "label": "Mazsalacas novads (Mazsalaca)",
                    "value": 60
                },
                {"label": "Malpils novads (Malpils)", "value": 61}, {
                    "label": "Marupes novads (Marupe)",
                    "value": 62
                }, {"label": "Mersraga novads (Mersrags)", "value": 63}, {
                    "label": "Naukšenu novads (Naukšeni)",
                    "value": 64
                },
                {"label": "Neretas novads (Nereta)", "value": 65}, {
                    "label": "Nicas novads (Nica)",
                    "value": 66
                }, {"label": "Ogres novads (Ogre)", "value": 67}, {"label": "Olaines novads (Olaine)", "value": 68},
                {"label": "Ozolnieku novads (Ozolnieki)", "value": 69}, {
                    "label": "Pargaujas novads (Pargauja)",
                    "value": 70
                }, {"label": "Pavilostas novads (Pavilosta)", "value": 71}, {
                    "label": "Plavinu novads (Plavinas)",
                    "value": 72
                },
                {"label": "Preilu novads (Preili)", "value": 73}, {
                    "label": "Priekules novads (Priekule)",
                    "value": 74
                }, {"label": "Priekulu novads (Priekuli)", "value": 75}, {
                    "label": "Raunas novads (Rauna)",
                    "value": 76
                },
                {"label": "Rezeknes novads (Rezekne)", "value": 77}, {
                    "label": "Riebinu novads (Riebini)",
                    "value": 78
                }, {"label": "Rojas novads (Roja)", "value": 79}, {"label": "Ropažu novads (Ropaži)", "value": 80},
                {"label": "Rucavas novads (Rucava)", "value": 81}, {
                    "label": "Rugaju novads (Rugaji)",
                    "value": 82
                }, {"label": "Rundales novads (Rundale)", "value": 83}, {
                    "label": "Rujienas novads (Rujiena)",
                    "value": 84
                },
                {"label": "Salas novads (Sala)", "value": 85}, {
                    "label": "Salacgrivas novads (Salacgriva)",
                    "value": 86
                }, {"label": "Salaspils novads (Salaspils)", "value": 87}, {
                    "label": "Saldus novads (Saldus)",
                    "value": 88
                },
                {"label": "Saulkrastu novads (Saulkrasti)", "value": 89}, {
                    "label": "Sejas novads (Seja)",
                    "value": 90
                }, {"label": "Siguldas novads (Sigulda)", "value": 91}, {
                    "label": "Skriveru novads (Skriveri)",
                    "value": 92
                },
                {"label": "Skrundas novads (Skrunda)", "value": 93}, {
                    "label": "Smiltenes novads (Smiltene)",
                    "value": 94
                }, {"label": "Stopinu novads (Stopini)", "value": 95}, {
                    "label": "Strencu novads (Strenci)",
                    "value": 96
                },
                {"label": "Talsu novads (Talsi)", "value": 97}, {
                    "label": "Tervetes novads (Tervete)",
                    "value": 98
                }, {"label": "Tukuma novads (Tukums)", "value": 99}, {
                    "label": "Vainodes novads (Vainode)",
                    "value": 100
                },
                {"label": "Valkas novads (Valka)", "value": 101}, {
                    "label": "Varaklanu novads (Varaklani)",
                    "value": 102
                }, {"label": "Varkavas novads (Varkava)", "value": 103}, {
                    "label": "Vecpiebalgas novads\n(Vecpiebalga)",
                    "value": 104
                },
                {"label": "Vecumnieku novads (Vecumnieki)", "value": 105}, {
                    "label": "Ventspils novads (Ventspils)",
                    "value": 106
                }, {"label": "Viesites novads (Viesite)", "value": 107}, {
                    "label": "Vilakas novads (Vilaka)",
                    "value": 108
                },
                {"label": "Vilanu novads (Vilani)", "value": 109}, {
                    "label": "Zilupes novads (Zilupe)",
                    "value": 110
                }, {"label": "Daugavpils", "value": "DGV"}, {"label": "Jelgava", "value": "JEL"},
                {"label": "Jekabpils", "value": "JKB"}, {"label": "Jurmala", "value": "JUR"}, {
                    "label": "Liepaja",
                    "value": "LPX"
                }, {"label": "Rezekne", "value": "REZ"},
                {"label": "Riga", "value": "RIX"}, {"label": "Ventspils", "value": "VEN"}, {
                    "label": "Valmiera",
                    "value": "VMR"
                }
            ];
            document.getElementById(stateId).innerHTML = "Municipality";
            break;
        case "LY":
            state_options = [
                {"label": "Banghazi", "value": "BA"}, {"label": "Al Butnan", "value": "BU"}, {
                    "label": "Darnah",
                    "value": "DR"
                }, {"label": "Ghat", "value": "GT"},
                {"label": "Al Jabal al Akh?ar", "value": "JA"}, {
                    "label": "Al Jabal al Gharbi",
                    "value": "JG"
                }, {"label": "Al Jafarah", "value": "JI"}, {"label": "Al Jufrah", "value": "JU"},
                {"label": "Al Kufrah", "value": "KF"}, {"label": "Al Marqab", "value": "MB"}, {
                    "label": "Misratah",
                    "value": "MI"
                }, {"label": "Al Marj", "value": "MJ"},
                {"label": "Murzuq", "value": "MQ"}, {"label": "Nalut", "value": "NL"}, {
                    "label": "An Nuqat al Khams",
                    "value": "NQ"
                }, {"label": "Sabha", "value": "SB"},
                {"label": "Surt", "value": "SR"}, {"label": "Tarabulus", "value": "TB"}, {
                    "label": "Al Wa?at",
                    "value": "WA"
                }, {"label": "Wadi al Hayat", "value": "WD"},
                {"label": "Wadi ash Shati?", "value": "WS"}, {"label": "Az Zawiyah", "value": "ZA"}
            ];
            document.getElementById(stateId).innerHTML = "Popularate";
            break;
        case "MA":
            state_options = [
                {"label": "Tanger-Tétouan", "value": 1}, {
                    "label": "Gharb-Chrarda-Beni Hssen",
                    "value": 2
                }, {"label": "Taza-Al Hoceima-Taounate", "value": 3}, {"label": "L'Oriental", "value": 4},
                {"label": "Fès-Boulemane", "value": 5}, {
                    "label": "Meknès-Tafilalet",
                    "value": 6
                }, {"label": "Rabat-Salé-Zemmour-Zaer", "value": 7}, {"label": "Grand Casablanca", "value": 8},
                {"label": "Chaouia-Ouardigha", "value": 9}, {
                    "label": "Doukhala-Abda",
                    "value": 10
                }, {"label": "Marrakech-Tensift-Al Haouz", "value": 11}, {"label": "Tadla-Azilal", "value": 12},
                {"label": "Sous-Massa-Draa", "value": 13}, {
                    "label": "Guelmim-Es Semara",
                    "value": 14
                }, {"label": "Laâyoune-Boujdour-Sakia el Hamra", "value": 15}, {
                    "label": "Oued ed Dahab-Lagouira",
                    "value": 16
                },
                {"label": "Agadir-Ida-Outanane", "value": "AGD"}, {
                    "label": "Aousserd",
                    "value": "AOU"
                }, {"label": "Assa-Zag", "value": "ASZ"}, {"label": "Azilal", "value": "AZI"},
                {"label": "Beni Mellal", "value": "BEM"}, {"label": "Berkane", "value": "BER"}, {
                    "label": "Ben Slimane",
                    "value": "BES"
                }, {"label": "Boujdour (EH)", "value": "BOD"},
                {"label": "Boulemane", "value": "BOM"}, {
                    "label": "Casablanca [Dar el Beïda]*",
                    "value": "CAS"
                }, {"label": "Chefchaouene", "value": "CHE"}, {"label": "Chichaoua", "value": "CHI"},
                {"label": "Chtouka-Ait Baha", "value": "CHT"}, {
                    "label": "Errachidia",
                    "value": "ERR"
                }, {"label": "Essaouira", "value": "ESI"}, {"label": "Es Smara (EH)", "value": "ESM"},
                {"label": "Fahs-Beni Makada", "value": "FAH"}, {
                    "label": "Fès-Dar-Dbibegh",
                    "value": "FES"
                }, {"label": "Figuig", "value": "FIG"}, {"label": "Guelmim", "value": "GUE"},
                {"label": "El Hajeb", "value": "HAJ"}, {"label": "Al Haouz", "value": "HAO"}, {
                    "label": "Al Hoceïma",
                    "value": "HOC"
                }, {"label": "Ifrane", "value": "IFR"},
                {"label": "Inezgane-Ait Melloul", "value": "INE"}, {
                    "label": "El Jadida",
                    "value": "JDI"
                }, {"label": "Jrada", "value": "JRA"}, {"label": "Kénitra", "value": "KEN"},
                {"label": "Kelaat Sraghna", "value": "KES"}, {
                    "label": "Khemisset",
                    "value": "KHE"
                }, {"label": "Khenifra", "value": "KHN"}, {"label": "Khouribga", "value": "KHO"},
                {"label": "Laâyoune", "value": "LAA"}, {"label": "Larache", "value": "LAR"}, {
                    "label": "Médiouna",
                    "value": "MED"
                }, {"label": "Meknès*", "value": "MEK"},
                {"label": "Marrakech-Medina", "value": "MMD"}, {
                    "label": "Marrakech-Menara",
                    "value": "MMN"
                }, {"label": "Mohammadia", "value": "MOH"}, {"label": "Moulay Yacoub", "value": "MOU"},
                {"label": "Nador", "value": "NAD"}, {"label": "Nouaceur", "value": "NOU"}, {
                    "label": "Ouarzazate",
                    "value": "OUA"
                }, {"label": "Oued ed Dahab (EH)", "value": "OUD"},
                {"label": "Oujda-Angad", "value": "OUJ"}, {"label": "Rabat", "value": "RAB"}, {
                    "label": "Safi",
                    "value": "SAF"
                }, {"label": "Salé", "value": "SAL"},
                {"label": "Sefrou", "value": "SEF"}, {"label": "Settat", "value": "SET"}, {
                    "label": "Sidi Kacem",
                    "value": "SIK"
                }, {"label": "Skhirate-Témara", "value": "SKH"},
                {"label": "Sidi Youssef Ben Ali", "value": "SYB"}, {
                    "label": "Taourirt",
                    "value": "TAI"
                }, {"label": "Taounate", "value": "TAO"}, {"label": "Taroudannt", "value": "TAR"},
                {"label": "Tata", "value": "TAT"}, {"label": "Taza", "value": "TAZ"}, {
                    "label": "Tétouan*",
                    "value": "TET"
                }, {"label": "Tiznit", "value": "TIZ"},
                {"label": "Tanger-Assilah", "value": "TNG"}, {"label": "Tan-Tan", "value": "TNT"}, {
                    "label": "Zagora",
                    "value": "ZAG"
                }
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "MC":
            state_options = [
                {"label": "La Colle", "value": "CL"}, {"label": "La Condamine", "value": "CO"}, {
                    "label": "Fontvieille",
                    "value": "FO"
                }, {"label": "La Gare", "value": "GA"},
                {"label": "Jardin Exotique", "value": "JE"}, {
                    "label": "Larvotto",
                    "value": "LA"
                }, {"label": "Malbousquet", "value": "MA"}, {"label": "Monte-Carlo", "value": "MC"},
                {"label": "Moneghetti", "value": "MG"}, {"label": "Monaco-Ville", "value": "MO"}, {
                    "label": "Moulins",
                    "value": "MU"
                }, {"label": "Port-Hercule", "value": "PH"},
                {"label": "Sainte-Dévote", "value": "SD"}, {"label": "La Source", "value": "SO"}, {
                    "label": "Spélugues",
                    "value": "SP"
                }, {"label": "Saint-Roman", "value": "SR"},
                {"label": "Vallon de la Rousse", "value": "VR"}
            ];
            document.getElementById(stateId).innerHTML = "Quarter";
            break;
        case "MD":
            state_options = [
                {"label": "Anenii Noi", "value": "AN"}, {"label": "Balti", "value": "BA"}, {
                    "label": "Bender [Tighina]",
                    "value": "BD"
                }, {"label": "Briceni", "value": "BR"},
                {"label": "Basarabeasca", "value": "BS"}, {"label": "Cahul", "value": "CA"}, {
                    "label": "Calarasi",
                    "value": "CL"
                }, {"label": "Cimislia", "value": "CM"},
                {"label": "Criuleni", "value": "CR"}, {"label": "Causeni", "value": "CS"}, {
                    "label": "Cantemir",
                    "value": "CT"
                }, {"label": "Chisinau", "value": "CU"},
                {"label": "Donduseni", "value": "DO"}, {"label": "Drochia", "value": "DR"}, {
                    "label": "Dubasari",
                    "value": "DU"
                }, {"label": "Edinet", "value": "ED"},
                {"label": "Falesti", "value": "FA"}, {
                    "label": "Floresti",
                    "value": "FL"
                }, {"label": "Gagauzia, Unitatea teritoriala autonoma (UTAG)", "value": "GA"}, {
                    "label": "Glodeni",
                    "value": "GL"
                },
                {"label": "Hîncesti", "value": "HI"}, {"label": "Ialoveni", "value": "IA"}, {
                    "label": "Leova",
                    "value": "LE"
                }, {"label": "Nisporeni", "value": "NI"},
                {"label": "Ocniþa", "value": "OC"}, {"label": "Orhei", "value": "OR"}, {
                    "label": "Rezina",
                    "value": "RE"
                }, {"label": "Rîscani", "value": "RI"},
                {"label": "Soldanesti", "value": "SD"}, {
                    "label": "Sîngerei",
                    "value": "SI"
                }, {"label": "Stînga Nistrului, unitatea teritoriala din", "value": "SN"}, {
                    "label": "Soroca",
                    "value": "SO"
                },
                {"label": "Straseni", "value": "ST"}, {"label": "Stefan Voda", "value": "SV"}, {
                    "label": "Taraclia",
                    "value": "TA"
                }, {"label": "Telenesti", "value": "TE"},
                {"label": "Ungheni", "value": "UN"}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "ME":
            state_options = [
                {"label": "Andrijevica", "value": 1}, {"label": "Bar", "value": 2}, {
                    "label": "Berane",
                    "value": 3
                }, {"label": "Bijelo Polje", "value": 4},
                {"label": "Budva", "value": 5}, {"label": "Cetinje", "value": 6}, {
                    "label": "Danilovgrad",
                    "value": 7
                }, {"label": "Herceg-Novi", "value": 8},
                {"label": "Kolašin", "value": 9}, {"label": "Kotor", "value": 10}, {
                    "label": "Mojkovac",
                    "value": 11
                }, {"label": "Nikšic´", "value": 12},
                {"label": "Plav", "value": 13}, {"label": "Pljevlja", "value": 14}, {
                    "label": "Plužine",
                    "value": 15
                }, {"label": "Podgorica", "value": 16},
                {"label": "Rožaje", "value": 17}, {"label": "Šavnik", "value": 18}, {
                    "label": "Tivat",
                    "value": 19
                }, {"label": "Ulcinj", "value": 20},
                {"label": "Žabljak", "value": 21}, {"label": "Gusinje", "value": 22}, {"label": "Petnjica", "value": 23}
            ];
            document.getElementById(stateId).innerHTML = "Municipality";
            break;
        case "MG":
            state_options = [
                {"label": "Toamasina", "value": "A"}, {"label": "Antsiranana", "value": "D"}, {
                    "label": "Fianarantsoa",
                    "value": "F"
                }, {"label": "Mahajanga", "value": "M"},
                {"label": "Antananarivo", "value": "T"}, {"label": "Toliara", "value": "U"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "MH":
            state_options = [
                {"label": "Ailuk", "value": "ALK"}, {"label": "Ailinglaplap", "value": "ALL"}, {
                    "label": "Arno",
                    "value": "ARN"
                }, {"label": "Aur", "value": "AUR"},
                {"label": "Ebon", "value": "EBO"}, {"label": "Enewetak & Ujelang", "value": "ENI"}, {
                    "label": "Jabat",
                    "value": "JAB"
                }, {"label": "Jaluit", "value": "JAL"},
                {"label": "Bikini & Kili", "value": "KIL"}, {
                    "label": "Kwajalein",
                    "value": "KWA"
                }, {"label": "Ralik chain", "value": "L"}, {"label": "Lae", "value": "LAE"},
                {"label": "Lib", "value": "LIB"}, {"label": "Likiep", "value": "LIK"}, {
                    "label": "Majuro",
                    "value": "MAJ"
                }, {"label": "Maloelap", "value": "MAL"},
                {"label": "Mejit", "value": "MEJ"}, {"label": "Mili", "value": "MIL"}, {
                    "label": "Namdrik",
                    "value": "NMK"
                }, {"label": "Namu", "value": "NMU"},
                {"label": "Rongelap", "value": "RON"}, {"label": "Ratak chain", "value": "T"}, {
                    "label": "Ujae",
                    "value": "UJA"
                }, {"label": "Utrik", "value": "UTI"},
                {"label": "Wotho", "value": "WTH"}, {"label": "Wotje", "value": "WTJ"}
            ];
            document.getElementById(stateId).innerHTML = "Municipality";
            break;
        case "MK":
            state_options = [
                {"label": "Aracinovo", "value": 2}, {"label": "Berovo", "value": 3}, {
                    "label": "Bitola",
                    "value": 4
                }, {"label": "Bogdanci", "value": 5},
                {"label": "Bogovinje", "value": 6}, {"label": "Bosilovo", "value": 7}, {
                    "label": "Brvenica",
                    "value": 8
                }, {"label": "Valandovo", "value": 10},
                {"label": "Vasilevo", "value": 11}, {"label": "Vevcani", "value": 12}, {
                    "label": "Veles",
                    "value": 13
                }, {"label": "Vinica", "value": 14},
                {"label": "Vrapcište", "value": 16}, {"label": "Gevgelija", "value": 18}, {
                    "label": "Gostivar",
                    "value": 19
                }, {"label": "Gradsko", "value": 20},
                {"label": "Debar", "value": 21}, {"label": "Debarca", "value": 22}, {
                    "label": "Delcevo",
                    "value": 23
                }, {"label": "Demir Kapija", "value": 24},
                {"label": "Demir Hisar", "value": 25}, {"label": "Dojran", "value": 26}, {
                    "label": "Dolneni",
                    "value": 27
                }, {"label": "Želino", "value": 30},
                {"label": "Zelenikovo", "value": 32}, {"label": "Zrnovci", "value": 33}, {
                    "label": "Ilinden",
                    "value": 34
                }, {"label": "Jegunovce", "value": 35},
                {"label": "Kavadarci", "value": 36}, {"label": "Karbinci", "value": 37}, {
                    "label": "Kicevo",
                    "value": 40
                }, {"label": "Konce", "value": 41},
                {"label": "Kocani", "value": 42}, {"label": "Kratovo", "value": 43}, {
                    "label": "Kriva Palanka",
                    "value": 44
                }, {"label": "Krivogaštani", "value": 45},
                {"label": "Kruševo", "value": 46}, {"label": "Kumanovo", "value": 47}, {
                    "label": "Lipkovo",
                    "value": 48
                }, {"label": "Lozovo", "value": 49},
                {"label": "Mavrovo-i-Rostuša", "value": 50}, {
                    "label": "Makedonska Kamenica",
                    "value": 51
                }, {"label": "Makedonski Brod", "value": 52}, {"label": "Mogila", "value": 53},
                {"label": "Negotino", "value": 54}, {"label": "Novaci", "value": 55}, {
                    "label": "Novo Selo",
                    "value": 56
                }, {"label": "Ohrid", "value": 58},
                {"label": "Petrovec", "value": 59}, {"label": "Pehcevo", "value": 60}, {
                    "label": "Plasnica",
                    "value": 61
                }, {"label": "Prilep", "value": 62},
                {"label": "Probištip", "value": 63}, {"label": "Radoviš", "value": 64}, {
                    "label": "Rankovce",
                    "value": 65
                }, {"label": "Resen", "value": 66},
                {"label": "Rosoman", "value": 67}, {"label": "Sveti Nikole", "value": 69}, {
                    "label": "Sopište",
                    "value": 70
                }, {"label": "Staro Nagoricane", "value": 71},
                {"label": "Struga", "value": 72}, {"label": "Strumica", "value": 73}, {
                    "label": "Studenicani",
                    "value": 74
                }, {"label": "Tearce", "value": 75},
                {"label": "Tetovo", "value": 76}, {"label": "Centar Župa", "value": 78}, {
                    "label": "Caška",
                    "value": 80
                }, {"label": "Cešinovo-Obleševo", "value": 81},
                {"label": "Cucer Sandevo", "value": 82}, {"label": "Štip", "value": 83}, {
                    "label": "Skopje",
                    "value": 85
                }
            ];
            document.getElementById(stateId).innerHTML = "Municipality";
            break;
        case "ML":
            state_options = [
                {"label": "Kayes", "value": 1}, {"label": "Taoudénit", "value": 10}, {
                    "label": "Koulikoro",
                    "value": 2
                }, {"label": "Sikasso", "value": 3},
                {"label": "Ségou", "value": 4}, {"label": "Mopti", "value": 5}, {
                    "label": "Tombouctou",
                    "value": 6
                }, {"label": "Gao", "value": 7},
                {"label": "Kidal", "value": 8}, {"label": "Ménaka", "value": 9}, {"label": "Bamako", "value": "BKO"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "MM":
            state_options = [
                {"label": "Sagaing", "value": 1}, {"label": "Bago", "value": 2}, {
                    "label": "Magway",
                    "value": 3
                }, {"label": "Mandalay", "value": 4},
                {"label": "Tanintharyi", "value": 5}, {"label": "Yangon", "value": 6}, {
                    "label": "Ayeyarwady",
                    "value": 7
                }, {"label": "Kachin", "value": 11},
                {"label": "Kayah", "value": 12}, {"label": "Kayin", "value": 13}, {
                    "label": "Chin",
                    "value": 14
                }, {"label": "Mon", "value": 15},
                {"label": "Rakhine", "value": 16}, {"label": "Shan", "value": 17}, {"label": "Nay Pyi Taw", "value": 18}
            ];
            document.getElementById(stateId).innerHTML = "State";
            break;
        case "MN":
            state_options = [
                {"label": "Orhon", "value": 35}, {"label": "Darhan uul", "value": 37}, {
                    "label": "Hentiy",
                    "value": 39
                }, {"label": "Hövagöl", "value": 41},
                {"label": "Hovd", "value": 43}, {"label": "Uvs", "value": 46}, {
                    "label": "Töv",
                    "value": 47
                }, {"label": "Selenge", "value": 49},
                {"label": "Sühbaatar", "value": 51}, {"label": "Ömnögovi", "value": 53}, {
                    "label": "Övörhangay",
                    "value": 55
                }, {"label": "Dzavhan", "value": 57},
                {"label": "Dundgovi", "value": 59}, {"label": "Dornod", "value": 61}, {
                    "label": "Dornogovi",
                    "value": 63
                }, {"label": "Govi-Sümber", "value": 64},
                {"label": "Govi-Altay", "value": 65}, {"label": "Bulgan", "value": 67}, {
                    "label": "Bayanhongor",
                    "value": 69
                }, {"label": "Bayan-Ölgiy", "value": 71},
                {"label": "Arhangay", "value": 73}, {"label": "Ulaanbaatar", "value": 1}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "MR":
            state_options = [
                {"label": "Hodh ech Chargui", "value": 1}, {"label": "Hodh el Gharbi", "value": 2}, {
                    "label": "Assaba",
                    "value": 3
                }, {"label": "Gorgol", "value": 4},
                {"label": "Brakna", "value": 5}, {"label": "Trarza", "value": 6}, {
                    "label": "Adrar",
                    "value": 7
                }, {"label": "Dakhlet Nouâdhibou", "value": 8},
                {"label": "Tagant", "value": 9}, {"label": "Guidimaka", "value": 10}, {
                    "label": "Tiris Zemmour",
                    "value": 11
                }, {"label": "Inchiri", "value": 12},
                {"label": "Nouakchott Ouest", "value": 13}, {
                    "label": "Nouakchott Nord",
                    "value": 14
                }, {"label": "Nouakchott Sud", "value": 15}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "MT":
            state_options = [
                {"label": "Attard", "value": 1}, {"label": "Balzan", "value": 2}, {
                    "label": "Birgu",
                    "value": 3
                }, {"label": "Birkirkara", "value": 4},
                {"label": "Birzebbuga", "value": 5}, {"label": "Bormla", "value": 6}, {
                    "label": "Dingli",
                    "value": 7
                }, {"label": "Fgura", "value": 8},
                {"label": "Floriana", "value": 9}, {"label": "Fontana", "value": 10}, {
                    "label": "Gudja",
                    "value": 11
                }, {"label": "Gzira", "value": 12},
                {"label": "Ghajnsielem", "value": 13}, {"label": "Gharb", "value": 14}, {
                    "label": "Gharghur",
                    "value": 15
                }, {"label": "Ghasri", "value": 16},
                {"label": "Ghaxaq", "value": 17}, {"label": "Hamrun", "value": 18}, {
                    "label": "Iklin",
                    "value": 19
                }, {"label": "Isla", "value": 20},
                {"label": "Kalkara", "value": 21}, {"label": "Kercem", "value": 22}, {
                    "label": "Kirkop",
                    "value": 23
                }, {"label": "Lija", "value": 24},
                {"label": "Luqa", "value": 25}, {"label": "Marsa", "value": 26}, {
                    "label": "Marsaskala",
                    "value": 27
                }, {"label": "Marsaxlokk", "value": 28},
                {"label": "Mdina", "value": 29}, {"label": "Mellieha", "value": 30}, {
                    "label": "Mgarr",
                    "value": 31
                }, {"label": "Mosta", "value": 32},
                {"label": "Mqabba", "value": 33}, {"label": "Msida", "value": 34}, {
                    "label": "Mtarfa",
                    "value": 35
                }, {"label": "Munxar", "value": 36},
                {"label": "Nadur", "value": 37}, {"label": "Naxxar", "value": 38}, {
                    "label": "Paola",
                    "value": 39
                }, {"label": "Pembroke", "value": 40},
                {"label": "Pietà", "value": 41}, {"label": "Qala", "value": 42}, {
                    "label": "Qormi",
                    "value": 43
                }, {"label": "Qrendi", "value": 44},
                {"label": "Rabat Gozo", "value": 45}, {"label": "Rabat Malta", "value": 46}, {
                    "label": "Safi",
                    "value": 47
                }, {"label": "Saint Julian's", "value": 48},
                {"label": "Saint John", "value": 49}, {
                    "label": "Saint Lawrence",
                    "value": 50
                }, {"label": "Saint Paul's Bay", "value": 51}, {"label": "Sannat", "value": 52},
                {"label": "Saint Lucia's", "value": 53}, {"label": "Santa Venera", "value": 54}, {
                    "label": "Siggiewi",
                    "value": 55
                }, {"label": "Sliema", "value": 56},
                {"label": "Swieqi", "value": 57}, {"label": "Ta' Xbiex", "value": 58}, {
                    "label": "Tarxien",
                    "value": 59
                }, {"label": "Valletta", "value": 60},
                {"label": "Xaghra", "value": 61}, {"label": "Xewkija", "value": 62}, {
                    "label": "Xghajra",
                    "value": 63
                }, {"label": "Zabbar", "value": 64},
                {"label": "Zebbug Gozo", "value": 65}, {"label": "Zebbug Malta", "value": 66}, {
                    "label": "Zejtun",
                    "value": 67
                }, {"label": "Zurrieq", "value": 68}
            ];
            document.getElementById(stateId).innerHTML = "Local council";
            break;
        case "MU":
            state_options = [
                {"label": "Agalega Islands", "value": "AG"}, {
                    "label": "Black River",
                    "value": "BL"
                }, {
                    "label": "Beau Bassin-Rose Hill",
                    "value": "BR"
                }, {"label": "Cargados Carajos Shoals [Saint Brandon Islands]", "value": "CC"},
                {"label": "Curepipe", "value": "CU"}, {"label": "Flacq", "value": "FL"}, {
                    "label": "Grand Port",
                    "value": "GP"
                }, {"label": "Moka", "value": "MO"},
                {"label": "Pamplemousses", "value": "PA"}, {
                    "label": "Port Louis",
                    "value": "PL"
                }, {"label": "Port Louis", "value": "PU"}, {"label": "Plaines wilhems", "value": "PW"},
                {"label": "Quatre Bornes", "value": "QB"}, {
                    "label": "Rodrigues Island",
                    "value": "RO"
                }, {"label": "Rivière du Rempart", "value": "RR"}, {"label": "Savanne", "value": "SA"},
                {"label": "Vacoas-Phoenix", "value": "VP"}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "MV":
            state_options = [
                {"label": "Alifu Dhaalu", "value": 0}, {"label": "Seenu", "value": 1}, {
                    "label": "Alifu Alifu",
                    "value": 2
                }, {"label": "Lhaviyani", "value": 3},
                {"label": "Vaavu", "value": 4}, {"label": "Hahdhunmathi", "value": 5}, {
                    "label": "Haa Alif",
                    "value": 7
                }, {"label": "Thaa", "value": 8},
                {"label": "Meemu", "value": 12}, {"label": "Raa", "value": 13}, {
                    "label": "Faafu",
                    "value": 14
                }, {"label": "Dhaalu", "value": 17},
                {"label": "Baa", "value": 20}, {"label": "Haa Dhaalu", "value": 23}, {
                    "label": "Shaviyani",
                    "value": 24
                }, {"label": "Noonu", "value": 25},
                {"label": "Kaafu", "value": 26}, {"label": "Gaafu Alifu", "value": 27}, {
                    "label": "Gaafu Dhaalu",
                    "value": 28
                }, {"label": "Gnaviyani", "value": 29},
                {"label": "Medhu", "value": "CE"}, {"label": "Male", "value": "MLE"}, {
                    "label": "Medhu-Uthuru",
                    "value": "NC"
                }, {"label": "Uthuru", "value": "NO"},
                {"label": "Medhu-Dhekunu", "value": "SC"}, {
                    "label": "Dhekunu",
                    "value": "SU"
                }, {"label": "Mathi-Uthuru", "value": "UN"}, {"label": "Mathi-Dhekunu", "value": "US"}
            ];
            document.getElementById(stateId).innerHTML = "Administrative atoll";
            break;
        case "MW":
            state_options = [
                {"label": "Balaka", "value": "BA"}, {"label": "Blantyre", "value": "BL"}, {
                    "label": "Central",
                    "value": "C"
                }, {"label": "Chikwawa", "value": "CK"},
                {"label": "Chiradzulu", "value": "CR"}, {"label": "Chitipa", "value": "CT"}, {
                    "label": "Dedza",
                    "value": "DE"
                }, {"label": "Dowa", "value": "DO"},
                {"label": "Karonga", "value": "KR"}, {"label": "Kasungu", "value": "KS"}, {
                    "label": "Lilongwe",
                    "value": "LI"
                }, {"label": "Likoma", "value": "LK"},
                {"label": "Mchinji", "value": "MC"}, {"label": "Mangochi", "value": "MG"}, {
                    "label": "Machinga",
                    "value": "MH"
                }, {"label": "Mulanje", "value": "MU"},
                {"label": "Mwanza", "value": "MW"}, {"label": "Mzimba", "value": "MZ"}, {
                    "label": "Northern",
                    "value": "N"
                }, {"label": "Nkhata Bay", "value": "NB"},
                {"label": "Neno", "value": "NE"}, {"label": "Ntchisi", "value": "NI"}, {
                    "label": "Nkhotakota",
                    "value": "NK"
                }, {"label": "Nsanje", "value": "NS"},
                {"label": "Ntcheu", "value": "NU"}, {"label": "Phalombe", "value": "PH"}, {
                    "label": "Rumphi",
                    "value": "RU"
                }, {"label": "Southern", "value": "S"},
                {"label": "Salima", "value": "SA"}, {"label": "Thyolo", "value": "TH"}, {
                    "label": "Zomba",
                    "value": "ZO"
                }
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "MX":
            state_options = [
                {"label": "Aguascalientes", "value": "AGU"}, {
                    "label": "Baja California",
                    "value": "BCN"
                }, {"label": "Baja California Sur", "value": "BCS"}, {"label": "Campeche", "value": "CAM"},
                {"label": "Chihuahua", "value": "CHH"}, {
                    "label": "Chiapas",
                    "value": "CHP"
                }, {"label": "Ciudad de México", "value": "CMX"}, {"label": "Coahuila de Zaragoza", "value": "COA"},
                {"label": "Colima", "value": "COL"}, {"label": "Durango", "value": "DUR"}, {
                    "label": "Guerrero",
                    "value": "GRO"
                }, {"label": "Guanajuato", "value": "GUA"},
                {"label": "Hidalgo", "value": "HID"}, {"label": "Jalisco", "value": "JAL"}, {
                    "label": "México",
                    "value": "MEX"
                }, {"label": "Michoacán de Ocampo", "value": "MIC"},
                {"label": "Morelos", "value": "MOR"}, {"label": "Nayarit", "value": "NAY"}, {
                    "label": "Nuevo León",
                    "value": "NLE"
                }, {"label": "Oaxaca", "value": "OAX"},
                {"label": "Puebla", "value": "PUE"}, {"label": "Querétaro", "value": "QUE"}, {
                    "label": "Quintana Roo",
                    "value": "ROO"
                }, {"label": "Sinaloa", "value": "SIN"},
                {"label": "San Luis Potosí", "value": "SLP"}, {"label": "Sonora", "value": "SON"}, {
                    "label": "Tabasco",
                    "value": "TAB"
                }, {"label": "Tamaulipas", "value": "TAM"},
                {"label": "Tlaxcala", "value": "TLA"}, {
                    "label": "Veracruz de Ignacio de la Llave",
                    "value": "VER"
                }, {"label": "Yucatán", "value": "YUC"}, {"label": "Zacatecas", "value": "ZAC"}
            ];
            document.getElementById(stateId).innerHTML = "State";
            break;
        case "MY":
            state_options = [
                {"label": "Johor", "value": 1}, {"label": "Kedah", "value": 2}, {
                    "label": "Kelantan",
                    "value": 3
                }, {"label": "Melaka", "value": 4},
                {"label": "Negeri Sembilan", "value": 5}, {"label": "Pahang", "value": 6}, {
                    "label": "Pulau Pinang",
                    "value": 7
                }, {"label": "Perak", "value": 8},
                {"label": "Perlis", "value": 9}, {"label": "Selangor", "value": 10}, {
                    "label": "Terengganu",
                    "value": 11
                }, {"label": "Sabah", "value": 12},
                {"label": "Sarawak", "value": 13}, {
                    "label": "Wilayah Persekutuan Kuala Lumpur",
                    "value": 14
                }, {"label": "Wilayah Persekutuan Labuan", "value": 15}, {
                    "label": "Wilayah Persekutuan Putrajaya",
                    "value": 16
                }
            ];
            document.getElementById(stateId).innerHTML = "State";
            break;
        case "MZ":
            state_options = [
                {"label": "Niaosa", "value": "A"}, {"label": "Manica", "value": "B"}, {
                    "label": "Gaza",
                    "value": "G"
                }, {"label": "Inhambane", "value": "I"},
                {"label": "Maputo", "value": "L"}, {"label": "Maputo", "value": "MPM"}, {
                    "label": "Nampula",
                    "value": "N"
                }, {"label": "Cabo Delgado", "value": "P"},
                {"label": "Zambézia", "value": "Q"}, {"label": "Sofala", "value": "S"}, {"label": "Tete", "value": "T"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "NA":
            state_options = [
                {"label": "Zambezi", "value": "CA"}, {"label": "Erongo", "value": "ER"}, {
                    "label": "Hardap",
                    "value": "HA"
                }, {"label": "Karas", "value": "KA"},
                {"label": "Kavango East", "value": "KE"}, {"label": "Khomas", "value": "KH"}, {
                    "label": "Kunene",
                    "value": "KU"
                }, {"label": "Kavango West", "value": "KW"},
                {"label": "Otjozondjupa", "value": "OD"}, {"label": "Omaheke", "value": "OH"}, {
                    "label": "Oshana",
                    "value": "ON"
                }, {"label": "Omusati", "value": "OS"},
                {"label": "Oshikoto", "value": "OT"}, {"label": "Ohangwena", "value": "OW"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "NE":
            state_options = [
                {"label": "Agadez", "value": 1}, {"label": "Diffa", "value": 2}, {
                    "label": "Dosso",
                    "value": 3
                }, {"label": "Maradi", "value": 4},
                {"label": "Tahoua", "value": 5}, {"label": "Tillabéri", "value": 6}, {
                    "label": "Zinder",
                    "value": 7
                }, {"label": "Niamey", "value": 8}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "NG":
            state_options = [
                {"label": "Abia", "value": "AB"}, {"label": "Adamawa", "value": "AD"}, {
                    "label": "Akwa Ibom",
                    "value": "AK"
                }, {"label": "Anambra", "value": "AN"},
                {"label": "Bauchi", "value": "BA"}, {"label": "Benue", "value": "BE"}, {
                    "label": "Borno",
                    "value": "BO"
                }, {"label": "Bayelsa", "value": "BY"},
                {"label": "Cross River", "value": "CR"}, {"label": "Delta", "value": "DE"}, {
                    "label": "Ebonyi",
                    "value": "EB"
                }, {"label": "Edo", "value": "ED"},
                {"label": "Ekiti", "value": "EK"}, {
                    "label": "Enugu",
                    "value": "EN"
                }, {"label": "Abuja Capital Territory", "value": "FC"}, {"label": "Gombe", "value": "GO"},
                {"label": "Imo", "value": "IM"}, {"label": "Jigawa", "value": "JI"}, {
                    "label": "Kaduna",
                    "value": "KD"
                }, {"label": "Kebbi", "value": "KE"},
                {"label": "Kano", "value": "KN"}, {"label": "Kogi", "value": "KO"}, {
                    "label": "Katsina",
                    "value": "KT"
                }, {"label": "Kwara", "value": "KW"},
                {"label": "Lagos", "value": "LA"}, {"label": "Nasarawa", "value": "NA"}, {
                    "label": "Niger",
                    "value": "NI"
                }, {"label": "Ogun", "value": "OG"},
                {"label": "Ondo", "value": "ON"}, {"label": "Osun", "value": "OS"}, {
                    "label": "Oyo",
                    "value": "OY"
                }, {"label": "Plateau", "value": "PL"},
                {"label": "Rivers", "value": "RI"}, {"label": "Sokoto", "value": "SO"}, {
                    "label": "Taraba",
                    "value": "TA"
                }, {"label": "Yobe", "value": "YO"},
                {"label": "Zamfara", "value": "ZA"}
            ];
            document.getElementById(stateId).innerHTML = "State";
            break;
        case "NI":
            state_options = [
                {"label": "Atlántico Norte", "value": "AN"}, {
                    "label": "Atlántico Sur",
                    "value": "AS"
                }, {"label": "Boaco", "value": "BO"}, {"label": "Carazo", "value": "CA"},
                {"label": "Chinandega", "value": "CI"}, {"label": "Chontales", "value": "CO"}, {
                    "label": "Estelí",
                    "value": "ES"
                }, {"label": "Granada", "value": "GR"},
                {"label": "Jinotega", "value": "JI"}, {"label": "León", "value": "LE"}, {
                    "label": "Madriz",
                    "value": "MD"
                }, {"label": "Managua", "value": "MN"},
                {"label": "Masaya", "value": "MS"}, {"label": "Matagalpa", "value": "MT"}, {
                    "label": "Nueva Segovia",
                    "value": "NS"
                }, {"label": "Rivas", "value": "RI"},
                {"label": "Río San Juan", "value": "SJ"}
            ];
            document.getElementById(stateId).innerHTML = "Department";
            break;
        case "NL":
            state_options = [
                {"label": "Drenthe", "value": "DR"}, {"label": "Flevoland", "value": "FL"}, {
                    "label": "Fryslân",
                    "value": "FR"
                }, {"label": "Gelderland", "value": "GE"},
                {"label": "Groningen", "value": "GR"}, {"label": "Limburg", "value": "LI"}, {
                    "label": "Noord-Brabant",
                    "value": "NB"
                }, {"label": "Noord-Holland", "value": "NH"},
                {"label": "Overijssel", "value": "OV"}, {"label": "Utrecht", "value": "UT"}, {
                    "label": "Zeeland",
                    "value": "ZE"
                }, {"label": "Zuid-Holland", "value": "ZH"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "NO":
            state_options = [
                {"label": "Østfold", "value": 1}, {"label": "Akershus", "value": 2}, {
                    "label": "Oslo",
                    "value": 3
                }, {"label": "Hedmark", "value": 4},
                {"label": "Oppland", "value": 5}, {"label": "Buskerud", "value": 6}, {
                    "label": "Vestfold",
                    "value": 7
                }, {"label": "Telemark", "value": 8},
                {"label": "Aust-Agder", "value": 9}, {"label": "Vest-Agder", "value": 10}, {
                    "label": "Rogaland",
                    "value": 11
                }, {"label": "Hordaland", "value": 12},
                {"label": "Sogn og Fjordane", "value": 14}, {
                    "label": "Møre og Romsdal",
                    "value": 15
                }, {"label": "Sør-Trøndelag", "value": 16}, {"label": "Nord-Trøndelag", "value": 17},
                {"label": "Nordland", "value": 18}, {"label": "Troms", "value": 19}, {
                    "label": "Finnmark",
                    "value": 20
                }, {"label": "Svalbard (Arctic Region) (See\nalso country code SJ)", "value": 21},
                {"label": "Jan Mayen (Arctic Region) (See\nalso country code SJ)", "value": 22}
            ];
            document.getElementById(stateId).innerHTML = "County";
            break;
        case "NP":
            state_options = [
                {"label": "Bagmati", "value": "BA"}, {"label": "Bheri", "value": "BH"}, {
                    "label": "Dhawalagiri",
                    "value": "DH"
                }, {"label": "Gandaki", "value": "GA"},
                {"label": "Janakpur", "value": "JA"}, {"label": "Karnali", "value": "KA"}, {
                    "label": "Kosi [Koshi]",
                    "value": "KO"
                }, {"label": "Lumbini", "value": "LU"},
                {"label": "Mahakali", "value": "MA"}, {"label": "Mechi", "value": "ME"}, {
                    "label": "Narayani",
                    "value": "NA"
                }, {"label": "Rapti", "value": "RA"},
                {"label": "Sagarmatha", "value": "SA"}, {"label": "Seti", "value": "SE"}
            ];
            document.getElementById(stateId).innerHTML = "Zone";
            break;
        case "NR":
            state_options = [
                {"label": "Aiwo", "value": 1}, {"label": "Anabar", "value": 2}, {
                    "label": "Anetan",
                    "value": 3
                }, {"label": "Anibare", "value": 4},
                {"label": "Baitsi", "value": 5}, {"label": "Boe", "value": 6}, {
                    "label": "Buada",
                    "value": 7
                }, {"label": "Denigomodu", "value": 8},
                {"label": "Ewa", "value": 9}, {"label": "Ijuw", "value": 10}, {
                    "label": "Meneng",
                    "value": 11
                }, {"label": "Nibok", "value": 12},
                {"label": "Uaboe", "value": 13}, {"label": "Yaren", "value": 14}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "NZ":
            state_options = [
                {"label": "Auckland", "value": "AUK"}, {
                    "label": "Bay of Plenty",
                    "value": "BOP"
                }, {"label": "Canterbury", "value": "CAN"}, {"label": "Chatham Islands Territory", "value": "CIT"},
                {"label": "Gisborne", "value": "GIS"}, {
                    "label": "Hawkes's Bay",
                    "value": "HKB"
                }, {"label": "Marlborough", "value": "MBH"}, {"label": "Manawatu-Wanganui", "value": "MWT"},
                {"label": "Nelson", "value": "NSN"}, {"label": "Northland", "value": "NTL"}, {
                    "label": "Otago",
                    "value": "OTA"
                }, {"label": "Southland", "value": "STL"},
                {"label": "Tasman", "value": "TAS"}, {"label": "Taranaki", "value": "TKI"}, {
                    "label": "Wellington",
                    "value": "WGN"
                }, {"label": "Waikato", "value": "WKO"},
                {"label": "West Coast", "value": "WTC"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "OM":
            state_options = [
                {"label": "Janub al Batinah", "value": "BJ"}, {
                    "label": "Shamal al Batinah",
                    "value": "BS"
                }, {"label": "Al Buraymi", "value": "BU"}, {"label": "Ad Dakhiliyah", "value": "DA"},
                {"label": "Masqat", "value": "MA"}, {
                    "label": "Musandam",
                    "value": "MU"
                }, {"label": "Janub ash Sharqiyah", "value": "SJ"}, {"label": "Shamal ash Sharqiyah", "value": "SS"},
                {"label": "AI Wusta", "value": "WU"}, {"label": "Az Zahirah", "value": "ZA"}, {
                    "label": "Z¸ufar",
                    "value": "ZU"
                }
            ];
            document.getElementById(stateId).innerHTML = "Governorate";
            break;
        case "PA":
            state_options = [
                {"label": "Bocas del Toro", "value": 1}, {"label": "Panamá Oeste", "value": 10}, {
                    "label": "Coclé",
                    "value": 2
                }, {"label": "Colón", "value": 3},
                {"label": "Chiriquí", "value": 4}, {"label": "Darién", "value": 5}, {
                    "label": "Herrera",
                    "value": 6
                }, {"label": "Los Santos", "value": 7},
                {"label": "Panamá", "value": 8}, {"label": "Veraguas", "value": 9}, {
                    "label": "Emberá",
                    "value": "EM"
                }, {"label": "Guna Yala", "value": "KY"},
                {"label": "Ngöbe-Buglé", "value": "NB"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "PE":
            state_options = [
                {"label": "Amazonas", "value": "AMA"}, {"label": "Ancash", "value": "ANC"}, {
                    "label": "Apurímac",
                    "value": "APU"
                }, {"label": "Arequipa", "value": "ARE"},
                {"label": "Ayacucho", "value": "AYA"}, {"label": "Cajamarca", "value": "CAJ"}, {
                    "label": "El Callao",
                    "value": "CAL"
                }, {"label": "Cuzco [Cusco]", "value": "CUS"},
                {"label": "Huánuco", "value": "HUC"}, {"label": "Huancavelica", "value": "HUV"}, {
                    "label": "Ica",
                    "value": "ICA"
                }, {"label": "Junín", "value": "JUN"},
                {"label": "La Libertad", "value": "LAL"}, {"label": "Lambayeque", "value": "LAM"}, {
                    "label": "Lima",
                    "value": "LIM"
                }, {"label": "Lima hatun llaqta", "value": "LMA"},
                {"label": "Loreto", "value": "LOR"}, {"label": "Madre de Dios", "value": "MDD"}, {
                    "label": "Moquegua",
                    "value": "MOQ"
                }, {"label": "Pasco", "value": "PAS"},
                {"label": "Piura", "value": "PIU"}, {"label": "Puno", "value": "PUN"}, {
                    "label": "San Martín",
                    "value": "SAM"
                }, {"label": "Tacna", "value": "TAC"},
                {"label": "Tumbes", "value": "TUM"}, {"label": "Ucayali", "value": "UCA"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "PG":
            state_options = [
                {"label": "Chimbu", "value": "CPK"}, {"label": "Central", "value": "CPM"}, {
                    "label": "East New Britain",
                    "value": "EBR"
                }, {"label": "Eastern Highlands", "value": "EHG"},
                {"label": "Enga", "value": "EPW"}, {"label": "East Sepik", "value": "ESW"}, {
                    "label": "Gulf",
                    "value": "GPK"
                }, {"label": "Hela", "value": "HLA"},
                {"label": "Jiwaka", "value": "JWK"}, {"label": "Milne Bay", "value": "MBA"}, {
                    "label": "Morobe",
                    "value": "MPL"
                }, {"label": "Madang", "value": "MPM"},
                {"label": "Manus", "value": "MRL"}, {
                    "label": "National Capital District (Port Moresby)",
                    "value": "NCD"
                }, {"label": "New Ireland", "value": "NIK"}, {"label": "Northern", "value": "NPP"},
                {"label": "Bougainville", "value": "NSB"}, {
                    "label": "West Sepik",
                    "value": "SAN"
                }, {"label": "Southern Highlands", "value": "SHM"}, {"label": "West New Britain", "value": "WBK"},
                {"label": "Western Highlands", "value": "WHM"}, {"label": "Western", "value": "WPD"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "PH":
            state_options = [
                {"label": "National Capital Région (Manila)", "value": 0}, {
                    "label": "Abra",
                    "value": "ABR"
                }, {"label": "Agusan del Norte", "value": "AGN"}, {"label": "Agusan del Sur", "value": "AGS"},
                {"label": "Aklan", "value": "AKL"}, {"label": "Albay", "value": "ALB"}, {
                    "label": "Antique",
                    "value": "ANT"
                }, {"label": "Apayao", "value": "APA"},
                {"label": "Aurora", "value": "AUR"}, {"label": "Bataan", "value": "BAN"}, {
                    "label": "Basilan",
                    "value": "BAS"
                }, {"label": "Benguet", "value": "BEN"},
                {"label": "Biliran", "value": "BIL"}, {"label": "Bohol", "value": "BOH"}, {
                    "label": "Batangas",
                    "value": "BTG"
                }, {"label": "Batanes", "value": "BTN"},
                {"label": "Bukidnon", "value": "BUK"}, {"label": "Bulacan", "value": "BUL"}, {
                    "label": "Cagayan",
                    "value": "CAG"
                }, {"label": "Camiguin", "value": "CAM"},
                {"label": "Camarines Norte", "value": "CAN"}, {
                    "label": "Capiz",
                    "value": "CAP"
                }, {"label": "Camarines Sur", "value": "CAS"}, {"label": "Catanduanes", "value": "CAT"},
                {"label": "Cavite", "value": "CAV"}, {"label": "Cebu", "value": "CEB"}, {
                    "label": "Compostela Valley",
                    "value": "COM"
                }, {"label": "Davao Oriental", "value": "DAO"},
                {"label": "Davao del Sur", "value": "DAS"}, {
                    "label": "Davao del Norte",
                    "value": "DAV"
                }, {"label": "Dinagat Islands", "value": "DIN"}, {"label": "Kanlurang Dabaw", "value": "DVO"},
                {"label": "Eastern Samar", "value": "EAS"}, {"label": "Guimaras", "value": "GUI"}, {
                    "label": "Ifugao",
                    "value": "IFU"
                }, {"label": "Iloilo", "value": "ILI"},
                {"label": "Ilocos Norte", "value": "ILN"}, {"label": "Ilocos Sur", "value": "ILS"}, {
                    "label": "Isabela",
                    "value": "ISA"
                }, {"label": "Kalinga-Apayao", "value": "KAL"},
                {"label": "Laguna", "value": "LAG"}, {
                    "label": "Lanao del Norte",
                    "value": "LAN"
                }, {"label": "Lanao del Sur", "value": "LAS"}, {"label": "Leyte", "value": "LEY"},
                {"label": "La Union", "value": "LUN"}, {"label": "Marinduque", "value": "MAD"}, {
                    "label": "Maguindanao",
                    "value": "MAG"
                }, {"label": "Masbate", "value": "MAS"},
                {"label": "Mindoro Occidental", "value": "MDC"}, {
                    "label": "Mindoro Oriental",
                    "value": "MDR"
                }, {"label": "Mountain Province", "value": "MOU"}, {"label": "Misamis Occidental", "value": "MSC"},
                {"label": "Misamis Oriental", "value": "MSR"}, {
                    "label": "Kotabato",
                    "value": "NCO"
                }, {"label": "Negros occidental", "value": "NEC"}, {"label": "Negros oriental", "value": "NER"},
                {"label": "Northern Samar", "value": "NSA"}, {
                    "label": "Nueva Ecija",
                    "value": "NUE"
                }, {"label": "Nueva Vizcaya", "value": "NUV"}, {"label": "Pampanga", "value": "PAM"},
                {"label": "Pangasinan", "value": "PAN"}, {"label": "Palawan", "value": "PLW"}, {
                    "label": "Quezon",
                    "value": "QUE"
                }, {"label": "Quirino", "value": "QUI"},
                {"label": "Rizal", "value": "RIZ"}, {"label": "Romblon", "value": "ROM"}, {
                    "label": "Sarangani",
                    "value": "SAR"
                }, {"label": "South Cotabato", "value": "SCO"},
                {"label": "Siquijor", "value": "SIG"}, {"label": "Southern Leyte", "value": "SLE"}, {
                    "label": "Sulu",
                    "value": "SLU"
                }, {"label": "Sorsogon", "value": "SOR"},
                {"label": "Sultan Kudarat", "value": "SUK"}, {
                    "label": "Surigao del Norte",
                    "value": "SUN"
                }, {"label": "Surigao del Sur", "value": "SUR"}, {"label": "Tarlac", "value": "TAR"},
                {"label": "Tawi-Tawi", "value": "TAW"}, {
                    "label": "Western Samar",
                    "value": "WSA"
                }, {"label": "Zamboanga del Norte", "value": "ZAN"}, {"label": "Zamboanga del Sur", "value": "ZAS"},
                {"label": "Zambales", "value": "ZMB"}, {
                    "label": "Zamboanga Sibuguey [Zamboanga Sibugay]",
                    "value": "ZSI"
                }
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "PK":
            state_options = [
                {"label": "Balochistan", "value": "BA"}, {
                    "label": "Gilgit-Baltistan",
                    "value": "GB"
                }, {"label": "Islamabad", "value": "IS"}, {"label": "Azad Jammu and Kashmir", "value": "JK"},
                {"label": "Khyber Pakhtunkhwa", "value": "KP"}, {"label": "Punjab", "value": "PB"}, {
                    "label": "Sindh",
                    "value": "SD"
                }, {"label": "Federally Administered Tribal Areas", "value": "TA"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "PL":
            state_options = [
                {"label": "Dolnoslaskie", "value": "DS"}, {
                    "label": "Kujawsko-pomorskie",
                    "value": "KP"
                }, {"label": "Lubuskie", "value": "LB"}, {"label": "Lódzkie", "value": "LD"},
                {"label": "Lubelskie", "value": "LU"}, {"label": "Malopolskie", "value": "MA"}, {
                    "label": "Mazowieckie",
                    "value": "MZ"
                }, {"label": "Opolskie", "value": "OP"},
                {"label": "Podlaskie", "value": "PD"}, {"label": "Podkarpackie", "value": "PK"}, {
                    "label": "Pomorskie",
                    "value": "PM"
                }, {"label": "Swietokrzyskie", "value": "SK"},
                {"label": "Slaskie", "value": "SL"}, {
                    "label": "Warminsko-mazurskie",
                    "value": "WN"
                }, {"label": "Wielkopolskie", "value": "WP"}, {"label": "Zachodniopomorskie", "value": "ZP"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "PS":
            state_options = [
                {"label": "Bethlehem", "value": "BTH"}, {"label": "Deir El Balah", "value": "DEB"}, {
                    "label": "Gaza",
                    "value": "GZA"
                }, {"label": "Hebron", "value": "HBN"},
                {"label": "Jerusalem", "value": "JEM"}, {
                    "label": "Jericho and Al Aghwar",
                    "value": "JRH"
                }, {"label": "Khan Yunis", "value": "KYS"}, {"label": "Nablus", "value": "NBS"},
                {"label": "North Gaza", "value": "NGZ"}, {"label": "Qalqilya", "value": "QQA"}, {
                    "label": "Ramallah",
                    "value": "RBH"
                }, {"label": "Rafah", "value": "RFH"},
                {"label": "Salfit", "value": "SLT"}, {"label": "Tubas", "value": "TBS"}, {
                    "label": "Tulkarm",
                    "value": "TKM"
                }
            ];
            document.getElementById(stateId).innerHTML = "Governorate";
            break;
        case "PT":
            state_options = [
                {"label": "Aveiro", "value": 1}, {"label": "Beja", "value": 2}, {
                    "label": "Braga",
                    "value": 3
                }, {"label": "Bragança", "value": 4},
                {"label": "Castelo Branco", "value": 5}, {"label": "Coimbra", "value": 6}, {
                    "label": "Évora",
                    "value": 7
                }, {"label": "Faro", "value": 8},
                {"label": "Guarda", "value": 9}, {"label": "Leiria", "value": 10}, {
                    "label": "Lisboa",
                    "value": 11
                }, {"label": "Portalegre", "value": 12},
                {"label": "Porto", "value": 13}, {"label": "Santarém", "value": 14}, {
                    "label": "Setúbal",
                    "value": 15
                }, {"label": "Viana do Castelo", "value": 16},
                {"label": "Vila Real", "value": 17}, {
                    "label": "Viseu",
                    "value": 18
                }, {"label": "Região Autónoma dos Açores", "value": 20}, {
                    "label": "Região Autónoma da Madeira",
                    "value": 30
                }
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "PW":
            state_options = [
                {"label": "Aimeliik", "value": 2}, {"label": "Airai", "value": 4}, {
                    "label": "Angaur",
                    "value": 10
                }, {"label": "Hatohobei", "value": 50},
                {"label": "Kayangel", "value": 100}, {"label": "Koror", "value": 150}, {
                    "label": "Melekeok",
                    "value": 212
                }, {"label": "Ngaraard", "value": 214},
                {"label": "Ngarchelong", "value": 218}, {"label": "Ngardmau", "value": 222}, {
                    "label": "Ngatpang",
                    "value": 224
                }, {"label": "Ngchesar", "value": 226},
                {"label": "Ngeremlengui", "value": 227}, {"label": "Ngiwal", "value": 228}, {
                    "label": "Peleliu",
                    "value": 350
                }, {"label": "Sonsorol", "value": 370}
            ];
            document.getElementById(stateId).innerHTML = "State";
            break;
        case "PY":
            state_options = [
                {"label": "Concepción", "value": 1}, {"label": "Alto Paraná", "value": 10}, {
                    "label": "Central",
                    "value": 11
                }, {"label": "Ñeembucú", "value": 12},
                {"label": "Amambay", "value": 13}, {"label": "Canindeyú", "value": 14}, {
                    "label": "Presidente Hayes",
                    "value": 15
                }, {"label": "Alto Paraguay", "value": 16},
                {"label": "Boquerón", "value": 19}, {"label": "San Pedro", "value": 2}, {
                    "label": "Cordillera",
                    "value": 3
                }, {"label": "Guairá", "value": 4},
                {"label": "Caaguazú", "value": 5}, {"label": "Caazapá", "value": 6}, {
                    "label": "Itapúa",
                    "value": 7
                }, {"label": "Misiones", "value": 8},
                {"label": "Paraguarí", "value": 9}, {"label": "Asunción", "value": "ASU"}
            ];
            document.getElementById(stateId).innerHTML = "Department";
            break;
        case "QA":
            state_options = [
                {"label": "Ad Dawhah", "value": "DA"}, {
                    "label": "Al Khawr wa adh Dhakhirah",
                    "value": "KH"
                }, {"label": "Ash Shamal", "value": "MS"}, {"label": "Ar Rayyan", "value": "RA"},
                {"label": "Ash Shi?aniyah", "value": "SH"}, {
                    "label": "Umm Salal",
                    "value": "US"
                }, {"label": "Al Wakrah", "value": "WA"}, {"label": "Az¸ Z¸a‘ayin", "value": "ZA"}
            ];
            document.getElementById(stateId).innerHTML = "Municipality";
            break;
        case "RO":
            state_options = [
                {"label": "Alba", "value": "AB"}, {"label": "Arges", "value": "AG"}, {
                    "label": "Arad",
                    "value": "AR"
                }, {"label": "Bucuresti", "value": "B"},
                {"label": "Bacau", "value": "BC"}, {"label": "Bihor", "value": "BH"}, {
                    "label": "Bistrita-Nasaud",
                    "value": "BN"
                }, {"label": "Braila", "value": "BR"},
                {"label": "Botosani", "value": "BT"}, {"label": "Brasov", "value": "BV"}, {
                    "label": "Buzau",
                    "value": "BZ"
                }, {"label": "Cluj", "value": "CJ"},
                {"label": "Calarasi", "value": "CL"}, {"label": "Caras-Severin", "value": "CS"}, {
                    "label": "Constarta",
                    "value": "CT"
                }, {"label": "Covasna", "value": "CV"},
                {"label": "Dâmbovita", "value": "DB"}, {"label": "Dolj", "value": "DJ"}, {
                    "label": "Gorj",
                    "value": "GJ"
                }, {"label": "Galati", "value": "GL"},
                {"label": "Giurgiu", "value": "GR"}, {"label": "Hunedoara", "value": "HD"}, {
                    "label": "Harghita",
                    "value": "HR"
                }, {"label": "Ilfov", "value": "IF"},
                {"label": "Ialomita", "value": "IL"}, {"label": "Iasi", "value": "IS"}, {
                    "label": "Mehedinti",
                    "value": "MH"
                }, {"label": "Maramures", "value": "MM"},
                {"label": "Mures", "value": "MS"}, {"label": "Neamt", "value": "NT"}, {
                    "label": "Olt",
                    "value": "OT"
                }, {"label": "Prahova", "value": "PH"},
                {"label": "Sibiu", "value": "SB"}, {"label": "Salaj", "value": "SJ"}, {
                    "label": "Satu Mare",
                    "value": "SM"
                }, {"label": "Suceava", "value": "SV"},
                {"label": "Tulcea", "value": "TL"}, {"label": "Timis", "value": "TM"}, {
                    "label": "Teleorman",
                    "value": "TR"
                }, {"label": "Vâlcea", "value": "VL"},
                {"label": "Vrancea", "value": "VN"}, {"label": "Vaslui", "value": "VS"}
            ];
            document.getElementById(stateId).innerHTML = "Departments";
            break;
        case "RS":
            state_options = [
                {"label": "Beograd", "value": 0}, {
                    "label": "Severnobacki okrug",
                    "value": 1
                }, {"label": "Srednjebanatski okrug", "value": 2}, {"label": "Severnobanatski okrug", "value": 3},
                {"label": "Južnobanatski okrug", "value": 4}, {
                    "label": "Zapadnobacki okrug",
                    "value": 5
                }, {"label": "Južnobacki okrug", "value": 6}, {"label": "Sremski okrug", "value": 7},
                {"label": "Macvanski okrug", "value": 8}, {
                    "label": "Kolubarski okrug",
                    "value": 9
                }, {"label": "Podunavski okrug", "value": 10}, {"label": "Branicevski okrug", "value": 11},
                {"label": "Šumadijski okrug", "value": 12}, {
                    "label": "Pomoravski okrug",
                    "value": 13
                }, {"label": "Borski okrug", "value": 14}, {"label": "Zajecarski okrug", "value": 15},
                {"label": "Zlatiborski okrug", "value": 16}, {
                    "label": "Moravicki okrug",
                    "value": 17
                }, {"label": "Raški okrug", "value": 18}, {"label": "Rasinski okrug", "value": 19},
                {"label": "Nišavski okrug", "value": 20}, {
                    "label": "Toplicki okrug",
                    "value": 21
                }, {"label": "Pirotski okrug", "value": 22}, {"label": "Jablanicki okrug", "value": 23},
                {"label": "Pcinjski okrug", "value": 24}, {
                    "label": "Kosovski okrug",
                    "value": 25
                }, {"label": "Pecki okrug", "value": 26}, {"label": "Prizrenski okrug", "value": 27},
                {"label": "Kosovsko-Mitrovacki okrug", "value": 28}, {
                    "label": "Kosovsko-Pomoravski okrug",
                    "value": 29
                }, {"label": "Kosovo-Metohija", "value": "KM"}, {"label": "Vojvodina", "value": "VO"}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "RU":
            state_options = [
                {"label": "Adygeya, Respublika", "value": "AD"}, {
                    "label": "Altay, Respublika",
                    "value": "AL"
                }, {"label": "Altayskiy kray", "value": "ALT"}, {"label": "Amurskaya oblast'", "value": "AMU"},
                {"label": "Arkhangel'skaya oblast,", "value": "ARK"}, {
                    "label": "Astrakhanskaya oblast'",
                    "value": "AST"
                }, {"label": "Bashkortostan, Respublika", "value": "BA"}, {
                    "label": "Belgorodskaya oblast'",
                    "value": "BEL"
                },
                {"label": "Bryanskaya oblast'", "value": "BRY"}, {
                    "label": "Buryatiya, Respublika",
                    "value": "BU"
                }, {"label": "Chechenskaya Respublika", "value": "CE"}, {
                    "label": "Chelyabinskaya oblast'",
                    "value": "CHE"
                },
                {"label": "Chukotskiy avtonomnyy okrug", "value": "CHU"}, {
                    "label": "Chuvashskaya Respublika",
                    "value": "CU"
                }, {
                    "label": "Dagestan, Respublika",
                    "value": "DA"
                }, {"label": "Ingushskaya Respublika [Respublika Ingushetiya]", "value": "IN"},
                {"label": "Irkutskaya oblast'", "value": "IRK"}, {
                    "label": "Ivanovskaya oblast'",
                    "value": "IVA"
                }, {"label": "Kamchatskaya oblast'", "value": "KAM"}, {
                    "label": "Kabardino-Balkarskaya Respublika",
                    "value": "KB"
                },
                {"label": "Karachayevo-Cherkesskaya Respublika", "value": "KC"}, {
                    "label": "Krasnodarskiy kray",
                    "value": "KDA"
                }, {"label": "Kemerovskaya oblast'", "value": "KEM"}, {
                    "label": "Kaliningradskaya oblast,",
                    "value": "KGD"
                },
                {"label": "Kurganskaya oblast'", "value": "KGN"}, {
                    "label": "Khabarovskiy kray",
                    "value": "KHA"
                }, {"label": "Khanty-Mansiyskiy avtonomnyy okrug", "value": "KHM"}, {
                    "label": "Kirovskaya oblast'",
                    "value": "KIR"
                },
                {"label": "Khakasiya, Respublika", "value": "KK"}, {
                    "label": "Kalmykiya, Respublika",
                    "value": "KL"
                }, {"label": "Kaluzhskaya oblast'", "value": "KLU"}, {"label": "Komi, Respublika", "value": "KO"},
                {"label": "Kostromskaya oblast'", "value": "KOS"}, {
                    "label": "Kareliya, Respublika",
                    "value": "KR"
                }, {"label": "Kurskaya oblast'", "value": "KRS"}, {"label": "Krasnoyarskiy kray", "value": "KYA"},
                {"label": "Leningradskaya oblast'", "value": "LEN"}, {
                    "label": "Lipetskaya oblast'",
                    "value": "LIP"
                }, {"label": "Magadanskaya oblast'", "value": "MAG"}, {"label": "Mariy El, Respublika", "value": "ME"},
                {"label": "Mordoviya, Respublika", "value": "MO"}, {
                    "label": "Moskovskaya oblast'",
                    "value": "MOS"
                }, {"label": "Moskva", "value": "MOW"}, {"label": "Murmanskaya oblast'", "value": "MUR"},
                {"label": "Nenetskiy avtonomnyy okrug", "value": "NEN"}, {
                    "label": "Novgorodskaya oblast'",
                    "value": "NGR"
                }, {"label": "Nizhegorodskaya oblast'", "value": "NIZ"}, {
                    "label": "Novosibirskaya oblast'",
                    "value": "NVS"
                },
                {"label": "Omskaya oblast'", "value": "OMS"}, {
                    "label": "Orenburgskaya oblast'",
                    "value": "ORE"
                }, {"label": "Orlovskaya oblast'", "value": "ORL"}, {"label": "Perm", "value": "PER"},
                {"label": "Penzenskaya oblast'", "value": "PNZ"}, {
                    "label": "Primorskiy kray",
                    "value": "PRI"
                }, {"label": "Pskovskaya oblast'", "value": "PSK"}, {"label": "Rostovskaya oblast'", "value": "ROS"},
                {"label": "Ryazanskaya oblast'", "value": "RYA"}, {
                    "label": "Sakha, Respublika [Yakutiya]",
                    "value": "SA"
                }, {"label": "Sakhalinskaya oblast'", "value": "SAK"}, {"label": "Samarskaya oblast'", "value": "SAM"},
                {
                    "label": "Saratovskaya oblast'",
                    "value": "SAR"
                }, {
                    "label": "Severnaya Osetiya,Respublika Alaniya [Respublika Severnaya Osetiya-Alaniya]",
                    "value": "SE"
                }, {"label": "Smolenskaya oblast'", "value": "SMO"}, {"label": "Sankt-Peterburg", "value": "SPE"},
                {"label": "Stavropol'skiy kray", "value": "STA"}, {
                    "label": "Sverdlovskaya oblast'",
                    "value": "SVE"
                }, {"label": "Tatarstan, Respublika", "value": "TA"}, {"label": "Tambovskaya oblast'", "value": "TAM"},
                {"label": "Tomskaya oblast'", "value": "TOM"}, {
                    "label": "Tul'skaya oblast'",
                    "value": "TUL"
                }, {"label": "Tverskaya oblast'", "value": "TVE"}, {"label": "Tyva, Respublika [Tuva]", "value": "TY"},
                {"label": "Tyumenskaya oblast'", "value": "TYU"}, {
                    "label": "Udmurtskaya Respublika",
                    "value": "UD"
                }, {"label": "Ul'yanovskaya oblast'", "value": "ULY"}, {
                    "label": "Volgogradskaya oblast'",
                    "value": "VGG"
                },
                {"label": "Vladimirskaya oblast'", "value": "VLA"}, {
                    "label": "Vologodskaya oblast'",
                    "value": "VLG"
                }, {"label": "Voronezhskaya oblast'", "value": "VOR"}, {
                    "label": "Yamalo-Nenetskiy avtonomnyy okrug",
                    "value": "YAN"
                },
                {"label": "Yaroslavskaya oblast'", "value": "YAR"}, {
                    "label": "Yevreyskaya avtonomnaya oblast'",
                    "value": "YEV"
                }, {"label": "Zabaykal'skiy kray", "value": "ZAB"}
            ];
            document.getElementById(stateId).innerHTML = "Administrative region";
            break;
        case "RW":
            state_options = [
                {"label": "Ville de Kigali", "value": 1}, {"label": "Est", "value": 2}, {
                    "label": "Nord",
                    "value": 3
                }, {"label": "Ouest", "value": 4},
                {"label": "Sud", "value": 5}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "SA":
            state_options = [
                {"label": "Ar Riyad", "value": 1}, {
                    "label": "Makkah al Mukarramah",
                    "value": 2
                }, {"label": "Al Madinah al Munawwarah", "value": 3}, {"label": "Ash Sharqiyah", "value": 4},
                {"label": "AI Qasim", "value": 5}, {"label": "Ha'il", "value": 6}, {
                    "label": "Tabuk",
                    "value": 7
                }, {"label": "AI Hudud ash Shamaliyah", "value": 8},
                {"label": "Jazan", "value": 9}, {"label": "Najran", "value": 10}, {
                    "label": "AI Bahah",
                    "value": 11
                }, {"label": "AI Jawf", "value": 12},
                {"label": "'Asir", "value": 14}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "SB":
            state_options = [
                {"label": "Central", "value": "CE"}, {
                    "label": "Choiseul",
                    "value": "CH"
                }, {"label": "Capital Territory (Honiara)", "value": "CT"}, {"label": "Guadalcanal", "value": "GU"},
                {"label": "Isabel", "value": "IS"}, {"label": "Makira-Ulawa", "value": "MK"}, {
                    "label": "Malaita",
                    "value": "ML"
                }, {"label": "Rennell and Bellona", "value": "RB"},
                {"label": "Temotu", "value": "TE"}, {"label": "Western", "value": "WE"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "SC":
            state_options = [
                {"label": "Anse aux Pins", "value": 1}, {"label": "Anse Boileau", "value": 2}, {
                    "label": "Anse Étoile",
                    "value": 3
                }, {"label": "Au Cap", "value": 4},
                {"label": "Anse Royale", "value": 5}, {
                    "label": "Baie Lazare",
                    "value": 6
                }, {"label": "Baie Sainte Anne", "value": 7}, {"label": "Beau Vallon", "value": 8},
                {"label": "Bel Air", "value": 9}, {"label": "Bel Ombre", "value": 10}, {
                    "label": "Cascade",
                    "value": 11
                }, {"label": "Glacis", "value": 12},
                {"label": "Grand'Anse Mahé", "value": 13}, {
                    "label": "Grand'Anse Praslin",
                    "value": 14
                }, {"label": "La Digue", "value": 15}, {"label": "La Rivière Anglaise", "value": 16},
                {"label": "Mont Buxton", "value": 17}, {"label": "Mont Fleuri", "value": 18}, {
                    "label": "Plaisance",
                    "value": 19
                }, {"label": "Pointe La Rue", "value": 20},
                {"label": "Port Glaud", "value": 21}, {"label": "Saint Louis", "value": 22}, {
                    "label": "Takamaka",
                    "value": 23
                }, {"label": "Lemamel", "value": 24},
                {"label": "Ros Kaiman", "value": 25}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "SD":
            state_options = [
                {"label": "Wasat Darfur Zalinjay", "value": "DC"}, {
                    "label": "Sharq Darfur",
                    "value": "DE"
                }, {"label": "Shamal Darfur", "value": "DN"}, {"label": "Janub Darfur", "value": "DS"},
                {"label": "Gharb Darfur", "value": "DW"}, {
                    "label": "Al Qadarif",
                    "value": "GD"
                }, {"label": "Gharb Kurdufan", "value": "GK"}, {"label": "Al Jazirah", "value": "GZ"},
                {"label": "Kassala", "value": "KA"}, {
                    "label": "Al Khartum",
                    "value": "KH"
                }, {"label": "Shiamal Kurdufan", "value": "KN"}, {"label": "Janub Kurdufan", "value": "KS"},
                {"label": "An Nil al Azraq", "value": "NB"}, {
                    "label": "Ash Shamaliyah",
                    "value": "NO"
                }, {"label": "Nahr an Nil", "value": "NR"}, {"label": "An Nil al Abya?", "value": "NW"},
                {"label": "Al Ba?r al A?mar", "value": "RS"}, {"label": "Sinnar", "value": "SI"}
            ];
            document.getElementById(stateId).innerHTML = "State";
            break;
        case "SE":
            state_options = [
                {"label": "Stockholms län", "value": "AB"}, {
                    "label": "Västerbottens län",
                    "value": "AC"
                }, {"label": "Norrbottens län", "value": "BD"}, {"label": "Uppsala län", "value": "C"},
                {"label": "Södermanlands län", "value": "D"}, {
                    "label": "Östergötlands län",
                    "value": "E"
                }, {"label": "Jönköpings län", "value": "F"}, {"label": "Kronoborgs län", "value": "G"},
                {"label": "Kalmar län", "value": "H"}, {
                    "label": "Gotlands län",
                    "value": "I"
                }, {"label": "Blekinge län", "value": "K"}, {"label": "Skåne län", "value": "M"},
                {"label": "Hallands län", "value": "N"}, {
                    "label": "Västra Götalands län",
                    "value": "O"
                }, {"label": "Värmlands län", "value": "S"}, {"label": "Örebro län", "value": "T"},
                {"label": "Västmanlands län", "value": "U"}, {
                    "label": "Dalarnes län",
                    "value": "W"
                }, {"label": "Gävleborgs län", "value": "X"}, {"label": "Västernorrlands län", "value": "Y"},
                {"label": "Jämtlands län", "value": "Z"}
            ];
            document.getElementById(stateId).innerHTML = "County";
            break;
        case "SG":
            state_options = [
                {"label": "Central Singapore", "value": 1}, {"label": "North East", "value": 2}, {
                    "label": "North West",
                    "value": 3
                }, {"label": "South East", "value": 4},
                {"label": "South West", "value": 5}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "SH":
            state_options = [
                {"label": "Ascension", "value": "AC"}, {
                    "label": "Saint Helena",
                    "value": "HL"
                }, {"label": "Tristan da Cunha", "value": "TA"}
            ];
            document.getElementById(stateId).innerHTML = "Geographical entity";
            break;
        case "SI":
            state_options = [
                {"label": "Ajdovšcina", "value": 1}, {"label": "Beltinci", "value": 2}, {
                    "label": "Bled",
                    "value": 3
                }, {"label": "Bohinj", "value": 4},
                {"label": "Borovnica", "value": 5}, {"label": "Bovec", "value": 6}, {
                    "label": "Brda",
                    "value": 7
                }, {"label": "Brezovica", "value": 8},
                {"label": "Brežice", "value": 9}, {"label": "Tišina", "value": 10}, {
                    "label": "Celje",
                    "value": 11
                }, {"label": "Cerklje na Gorenjskem", "value": 12},
                {"label": "Cerknica", "value": 13}, {"label": "Cerkno", "value": 14}, {
                    "label": "Crenšovci",
                    "value": 15
                }, {"label": "Crna na Koroškem", "value": 16},
                {"label": "Crnomelj", "value": 17}, {"label": "Destrnik", "value": 18}, {
                    "label": "Divaca",
                    "value": 19
                }, {"label": "Dobrepolje", "value": 20},
                {"label": "Dobrova-Polhov Gradec", "value": 21}, {
                    "label": "Dol pri Ljubljani",
                    "value": 22
                }, {"label": "Domžale", "value": 23}, {"label": "Dornava", "value": 24},
                {"label": "Dravograd", "value": 25}, {"label": "Duplek", "value": 26}, {
                    "label": "Gorenja vas-Poljane",
                    "value": 27
                }, {"label": "Gorišnica", "value": 28},
                {"label": "Gornja Radgona", "value": 29}, {
                    "label": "Gornji Grad",
                    "value": 30
                }, {"label": "Gornji Petrovci", "value": 31}, {"label": "Grosuplje", "value": 32},
                {"label": "Šalovci", "value": 33}, {"label": "Hrastnik", "value": 34}, {
                    "label": "Hrpelje-Kozina",
                    "value": 35
                }, {"label": "Idrija", "value": 36},
                {"label": "Ig", "value": 37}, {"label": "Ilirska Bistrica", "value": 38}, {
                    "label": "Ivancna Gorica",
                    "value": 39
                }, {"label": "Izola", "value": 40},
                {"label": "Jesenice", "value": 41}, {"label": "Juršinci", "value": 42}, {
                    "label": "Kamnik",
                    "value": 43
                }, {"label": "Kanal", "value": 44},
                {"label": "Kidricevo", "value": 45}, {"label": "Kobarid", "value": 46}, {
                    "label": "Kobilje",
                    "value": 47
                }, {"label": "Kocevje", "value": 48},
                {"label": "Komen", "value": 49}, {"label": "Koper", "value": 50}, {
                    "label": "Kozje",
                    "value": 51
                }, {"label": "Kranj", "value": 52},
                {"label": "Kranjska Gora", "value": 53}, {"label": "Krško", "value": 54}, {
                    "label": "Kungota",
                    "value": 55
                }, {"label": "Kuzma", "value": 56},
                {"label": "Laško", "value": 57}, {"label": "Lenart", "value": 58}, {
                    "label": "Lendava",
                    "value": 59
                }, {"label": "Litija", "value": 60},
                {"label": "Ljubljana", "value": 61}, {"label": "Ljubno", "value": 62}, {
                    "label": "Ljutomer",
                    "value": 63
                }, {"label": "Logatec", "value": 64},
                {"label": "Loška dolina", "value": 65}, {"label": "Loški Potok", "value": 66}, {
                    "label": "Luce",
                    "value": 67
                }, {"label": "Lukovica", "value": 68},
                {"label": "Majšperk", "value": 69}, {"label": "Maribor", "value": 70}, {
                    "label": "Medvode",
                    "value": 71
                }, {"label": "Mengeš", "value": 72},
                {"label": "Metlika", "value": 73}, {"label": "Mežica", "value": 74}, {
                    "label": "Miren-Kostanjevica",
                    "value": 75
                }, {"label": "Mislinja", "value": 76},
                {"label": "Moravce", "value": 77}, {"label": "Moravske Toplice", "value": 78}, {
                    "label": "Mozirje",
                    "value": 79
                }, {"label": "Murska Sobota", "value": 80},
                {"label": "Muta", "value": 81}, {"label": "Naklo", "value": 82}, {
                    "label": "Nazarje",
                    "value": 83
                }, {"label": "Nova Gorica", "value": 84},
                {"label": "Novo mesto", "value": 85}, {"label": "Odranci", "value": 86}, {
                    "label": "Ormož",
                    "value": 87
                }, {"label": "Osilnica", "value": 88},
                {"label": "Pesnica", "value": 89}, {"label": "Piran", "value": 90}, {
                    "label": "Pivka",
                    "value": 91
                }, {"label": "Podcetrtek", "value": 92},
                {"label": "Podvelka", "value": 93}, {"label": "Postojna", "value": 94}, {
                    "label": "Preddvor",
                    "value": 95
                }, {"label": "Ptuj", "value": 96},
                {"label": "Puconci", "value": 97}, {"label": "Race-Fram", "value": 98}, {
                    "label": "Radece",
                    "value": 99
                }, {"label": "Radenci", "value": 100},
                {"label": "Radlje ob Dravi", "value": 101}, {
                    "label": "Radovljica",
                    "value": 102
                }, {"label": "Ravne na Koroškem", "value": 103}, {"label": "Ribnica", "value": 104},
                {"label": "Rogašovci", "value": 105}, {"label": "Rogaška Slatina", "value": 106}, {
                    "label": "Rogatec",
                    "value": 107
                }, {"label": "Ruše", "value": 108},
                {"label": "Semic", "value": 109}, {"label": "Sevnica", "value": 110}, {
                    "label": "Sežana",
                    "value": 111
                }, {"label": "Slovenj Gradec", "value": 112},
                {"label": "Slovenska Bistrica", "value": 113}, {
                    "label": "Slovenske Konjice",
                    "value": 114
                }, {"label": "Starše", "value": 115}, {"label": "Sveti Jurij", "value": 116},
                {"label": "Šencur", "value": 117}, {"label": "Šentilj", "value": 118}, {
                    "label": "Šentjernej",
                    "value": 119
                }, {"label": "Šentjur", "value": 120},
                {"label": "Škocjan", "value": 121}, {"label": "Škofja Loka", "value": 122}, {
                    "label": "Škofljica",
                    "value": 123
                }, {"label": "Šmarje pri Jelšah", "value": 124},
                {"label": "Šmartno ob Paki", "value": 125}, {"label": "Šoštanj", "value": 126}, {
                    "label": "Štore",
                    "value": 127
                }, {"label": "Tolmin", "value": 128},
                {"label": "Trbovlje", "value": 129}, {"label": "Trebnje", "value": 130}, {
                    "label": "Tržic",
                    "value": 131
                }, {"label": "Turnišce", "value": 132},
                {"label": "Velenje", "value": 133}, {"label": "Velike Lašce", "value": 134}, {
                    "label": "Videm",
                    "value": 135
                }, {"label": "Vipava", "value": 136},
                {"label": "Vitanje", "value": 137}, {"label": "Vodice", "value": 138}, {
                    "label": "Vojnik",
                    "value": 139
                }, {"label": "Vrhnika", "value": 140},
                {"label": "Vuzenica", "value": 141}, {"label": "Zagorje ob Savi", "value": 142}, {
                    "label": "Zavrc",
                    "value": 143
                }, {"label": "Zrece", "value": 144},
                {"label": "Železniki", "value": 146}, {"label": "Žiri", "value": 147}, {
                    "label": "Benedikt",
                    "value": 148
                }, {"label": "Bistrica ob Sotli", "value": 149},
                {"label": "Bloke", "value": 150}, {"label": "Braslovce", "value": 151}, {
                    "label": "Cankova",
                    "value": 152
                }, {"label": "Cerkvenjak", "value": 153},
                {"label": "Dobje", "value": 154}, {"label": "Dobrna", "value": 155}, {
                    "label": "Dobrovnik",
                    "value": 156
                }, {"label": "Dolenjske Toplice", "value": 157},
                {"label": "Grad", "value": 158}, {"label": "Hajdina", "value": 159}, {
                    "label": "Hoce-Slivnica",
                    "value": 160
                }, {"label": "Hodoš", "value": 161},
                {"label": "Horjul", "value": 162}, {"label": "Jezersko", "value": 163}, {
                    "label": "Komenda",
                    "value": 164
                }, {"label": "Kostel", "value": 165},
                {"label": "Križevci", "value": 166}, {
                    "label": "Lovrenc na Pohorju",
                    "value": 167
                }, {"label": "Markovci", "value": 168}, {"label": "Miklavž na Dravskem polju", "value": 169},
                {"label": "Mirna Pec", "value": 170}, {"label": "Oplotnica", "value": 171}, {
                    "label": "Podlehnik",
                    "value": 172
                }, {"label": "Polzela", "value": 173},
                {"label": "Prebold", "value": 174}, {"label": "Prevalje", "value": 175}, {
                    "label": "Razkrižje",
                    "value": 176
                }, {"label": "Ribnica na Pohorju", "value": 177},
                {"label": "Selnica ob Dravi", "value": 178}, {"label": "Sodražica", "value": 179}, {
                    "label": "Solcava",
                    "value": 180
                }, {"label": "Sveta Ana", "value": 181},
                {"label": "Sveti Andraž v Slovenskih goricah", "value": 182}, {
                    "label": "Šempeter-Vrtojba",
                    "value": 183
                }, {"label": "Tabor", "value": 184}, {"label": "Trnovska vas", "value": 185},
                {"label": "Trzin", "value": 186}, {"label": "Velika Polana", "value": 187}, {
                    "label": "Veržej",
                    "value": 188
                }, {"label": "Vransko", "value": 189},
                {"label": "Žalec", "value": 190}, {"label": "Žetale", "value": 191}, {
                    "label": "Žirovnica",
                    "value": 192
                }, {"label": "Žužemberk", "value": 193},
                {"label": "Šmartno pri Litiji", "value": 194}, {"label": "Apace", "value": 195}, {
                    "label": "Cirkulane",
                    "value": 196
                }, {"label": "Kosanjevica na Krki", "value": 197},
                {"label": "Makole", "value": 198}, {"label": "Mokronog-Trebelno", "value": 199}, {
                    "label": "Poljcane",
                    "value": 200
                }, {"label": "Renèe-Vogrsko", "value": 201},
                {"label": "Središce ob Dravi", "value": 202}, {
                    "label": "Straža",
                    "value": 203
                }, {"label": "Sveta Trojica v Slovenskih\nGoricah", "value": 204}, {
                    "label": "Sveti Tomaž",
                    "value": 205
                },
                {"label": "Šmarješke Toplice", "value": 206}, {
                    "label": "Gorje",
                    "value": 207
                }, {"label": "Log-Dragomer", "value": 208}, {"label": "Recica ob Savinji", "value": 209},
                {"label": "Sveti Jurij v Slovenskih Goricah", "value": 210}, {
                    "label": "Šentrupert",
                    "value": 211
                }, {"label": "Mirna", "value": 212}, {"label": "Ankaran", "value": 213}
            ];
            document.getElementById(stateId).innerHTML = "Municipality";
            break;
        case "SK":
            state_options = [
                {"label": "Banskobystrický kraj", "value": "BC"}, {
                    "label": "Bratislavský kraj",
                    "value": "BL"
                }, {"label": "Košický kraj", "value": "KI"}, {"label": "Nitriansky kraj", "value": "NI"},
                {"label": "Prešovský kraj", "value": "PV"}, {
                    "label": "Trnavský kraj",
                    "value": "TA"
                }, {"label": "Trenciansky kraj", "value": "TC"}, {"label": "Žilinský kraj", "value": "ZI"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "SL":
            state_options = [
                {"label": "Eastern", "value": "E"}, {"label": "Northern", "value": "N"}, {
                    "label": "Southern",
                    "value": "S"
                }, {"label": "Western Area (Freetown)", "value": "W"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "SM":
            state_options = [
                {"label": "Acquaviva", "value": 1}, {"label": "Chiesanuova", "value": 2}, {
                    "label": "Domagnano",
                    "value": 3
                }, {"label": "Faetano", "value": 4},
                {"label": "Fiorentino", "value": 5}, {"label": "Borgo Maggiore", "value": 6}, {
                    "label": "San Marino",
                    "value": 7
                }, {"label": "Montegiardino", "value": 8},
                {"label": "Serravalle", "value": 9}
            ];
            document.getElementById(stateId).innerHTML = "Municipality";
            break;
        case "SN":
            state_options = [
                {"label": "Diourbel", "value": "DB"}, {"label": "Dakar", "value": "DK"}, {
                    "label": "Fatick",
                    "value": "FK"
                }, {"label": "Kaffrine", "value": "KA"},
                {"label": "Kolda", "value": "KD"}, {"label": "Kédougou", "value": "KE"}, {
                    "label": "Kaolack",
                    "value": "KL"
                }, {"label": "Louga", "value": "LG"},
                {"label": "Matam", "value": "MT"}, {"label": "Sédhiou", "value": "SE"}, {
                    "label": "Saint-Louis",
                    "value": "SL"
                }, {"label": "Tambacounda", "value": "TC"},
                {"label": "Thiès", "value": "TH"}, {"label": "Ziguinchor", "value": "ZG"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "SO":
            state_options = [
                {"label": "Awdal", "value": "AW"}, {"label": "Bakool", "value": "BK"}, {
                    "label": "Banaadir",
                    "value": "BN"
                }, {"label": "Bari", "value": "BR"},
                {"label": "Bay", "value": "BY"}, {"label": "Galguduud", "value": "GA"}, {
                    "label": "Gedo",
                    "value": "GE"
                }, {"label": "Hiiraan", "value": "HI"},
                {"label": "Jubbada Dhexe", "value": "JD"}, {"label": "Jubbada Hoose", "value": "JH"}, {
                    "label": "Mudug",
                    "value": "MU"
                }, {"label": "Nugaal", "value": "NU"},
                {"label": "Sanaag", "value": "SA"}, {
                    "label": "Shabeellaha Dhexe",
                    "value": "SD"
                }, {"label": "Shabeellaha Hoose", "value": "SH"}, {"label": "Sool", "value": "SO"},
                {"label": "Togdheer", "value": "TO"}, {"label": "Woqooyi Galbeed", "value": "WO"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "SR":
            state_options = [
                {"label": "Brokopondo", "value": "BR"}, {"label": "Commewijne", "value": "CM"}, {
                    "label": "Coronie",
                    "value": "CR"
                }, {"label": "Marowijne", "value": "MA"},
                {"label": "Nickerie", "value": "NI"}, {"label": "Paramaribo", "value": "PM"}, {
                    "label": "Para",
                    "value": "PR"
                }, {"label": "Saramacca", "value": "SA"},
                {"label": "Sipaliwini", "value": "SI"}, {"label": "Wanica", "value": "WA"}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "SS":
            state_options = [
                {"label": "Northern Bahr el Ghazal", "value": "BN"}, {
                    "label": "Western Bahr el Ghazal",
                    "value": "BW"
                }, {"label": "Central Equatoria", "value": "EC"}, {"label": "Eastern Equatoria", "value": "EE"},
                {"label": "Western Equatoria", "value": "EW"}, {"label": "Jonglei", "value": "JG"}, {
                    "label": "Lakes",
                    "value": "LK"
                }, {"label": "Upper Nile", "value": "NU"},
                {"label": "Unity", "value": "UY"}, {"label": "Warrap", "value": "WR"}
            ];
            document.getElementById(stateId).innerHTML = "State";
            break;
        case "ST":
            state_options = [
                {"label": "Príncipe", "value": "P"}, {"label": "São Tomé", "value": "S"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "SV":
            state_options = [
                {"label": "Ahuachapán", "value": "AH"}, {"label": "Cabañas", "value": "CA"}, {
                    "label": "Chalatenango",
                    "value": "CH"
                }, {"label": "Cuscatlán", "value": "CU"},
                {"label": "La Libertad", "value": "LI"}, {"label": "Morazán", "value": "MO"}, {
                    "label": "La Paz",
                    "value": "PA"
                }, {"label": "Santa Ana", "value": "SA"},
                {"label": "San Miguel", "value": "SM"}, {"label": "Sonsonate", "value": "SO"}, {
                    "label": "San Salvador",
                    "value": "SS"
                }, {"label": "San Vicente", "value": "SV"},
                {"label": "La Unión", "value": "UN"}, {"label": "Usulután", "value": "US"}
            ];
            document.getElementById(stateId).innerHTML = "Department";
            break;
        case "SY":
            state_options = [
                {"label": "Dimashq", "value": "DI"}, {"label": "Dar'a", "value": "DR"}, {
                    "label": "Dayr az Zawr",
                    "value": "DY"
                }, {"label": "AI Hasakah", "value": "HA"},
                {"label": "Hims", "value": "HI"}, {"label": "Halab", "value": "HL"}, {
                    "label": "Hamah",
                    "value": "HM"
                }, {"label": "Idlib", "value": "ID"},
                {"label": "AI Ladhiqiyah", "value": "LA"}, {
                    "label": "AI Qunaytirah",
                    "value": "QU"
                }, {"label": "Ar Raqqah", "value": "RA"}, {"label": "Rif Dimashq", "value": "RD"},
                {"label": "As Suwayda'", "value": "SU"}, {"label": "Tartus", "value": "TA"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "TD":
            state_options = [
                {"label": "Batha", "value": "BA"}, {"label": "Ba?r al Ghazal", "value": "BG"}, {
                    "label": "Burku",
                    "value": "BO"
                }, {"label": "Chari-Baguirmi", "value": "CB"},
                {"label": "Ennedi-Est", "value": "EE"}, {"label": "Ennedi-Ouest", "value": "EO"}, {
                    "label": "Guéra",
                    "value": "GR"
                }, {"label": "Hadjer Lamis", "value": "HL"},
                {"label": "Kanem", "value": "KA"}, {"label": "Lac", "value": "LC"}, {
                    "label": "Logone-Occidental",
                    "value": "LO"
                }, {"label": "Logone-Oriental", "value": "LR"},
                {"label": "Mandoul", "value": "MA"}, {
                    "label": "Moyen-Chari",
                    "value": "MC"
                }, {"label": "Mayo-Kebbi-Est", "value": "ME"}, {"label": "Mayo-Kebbi-Ouest", "value": "MO"},
                {"label": "Ville de Ndjamena", "value": "ND"}, {"label": "Ouaddaï", "value": "OD"}, {
                    "label": "Salamat",
                    "value": "SA"
                }, {"label": "Sila", "value": "SI"},
                {"label": "Tandjilé", "value": "TA"}, {"label": "Tibasti", "value": "TI"}, {
                    "label": "Wadi Fira",
                    "value": "WF"
                }
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "TG":
            state_options = [
                {"label": "Centrale", "value": "C"}, {"label": "Kara", "value": "K"}, {
                    "label": "Maritime (Région)",
                    "value": "M"
                }, {"label": "Plateaux", "value": "P"},
                {"label": "Savanes", "value": "S"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "TH":
            state_options = [
                {"label": "Krung Thep Maha Nakhon [Bangkok]", "value": 10}, {
                    "label": "Samut Prakan",
                    "value": 11
                }, {"label": "Nonthaburi", "value": 12}, {"label": "Pathum Thani", "value": 13},
                {"label": "Phra Nakhon Si Ayutthaya", "value": 14}, {
                    "label": "Ang Thong",
                    "value": 15
                }, {"label": "Lop Buri", "value": 16}, {"label": "Sing Buri", "value": 17},
                {"label": "Chai Nat", "value": 18}, {"label": "Saraburi", "value": 19}, {
                    "label": "Chon Buri",
                    "value": 20
                }, {"label": "Rayong", "value": 21},
                {"label": "Chanthaburi", "value": 22}, {"label": "Trat", "value": 23}, {
                    "label": "Chachoengsao",
                    "value": 24
                }, {"label": "Prachin Buri", "value": 25},
                {"label": "Nakhon Nayok", "value": 26}, {
                    "label": "Sa Kaeo",
                    "value": 27
                }, {"label": "Nakhon Ratchasima", "value": 30}, {"label": "Buri Ram", "value": 31},
                {"label": "Surin", "value": 32}, {"label": "Si Sa Ket", "value": 33}, {
                    "label": "Ubon Ratchathani",
                    "value": 34
                }, {"label": "Yasothon", "value": 35},
                {"label": "Chaiyaphum", "value": 36}, {"label": "Amnat Charoen", "value": 37}, {
                    "label": "Bueng Kan",
                    "value": 38
                }, {"label": "Nong Bua Lam Phu", "value": 39},
                {"label": "Khon Kaen", "value": 40}, {"label": "Udon Thani", "value": 41}, {
                    "label": "Loei",
                    "value": 42
                }, {"label": "Nong Khai", "value": 43},
                {"label": "Maha Sarakham", "value": 44}, {"label": "Roi Et", "value": 45}, {
                    "label": "Kalasin",
                    "value": 46
                }, {"label": "Sakon Nakhon", "value": 47},
                {"label": "Nakhon Phanom", "value": 48}, {"label": "Mukdahan", "value": 49}, {
                    "label": "Chiang Mai",
                    "value": 50
                }, {"label": "Lamphun", "value": 51},
                {"label": "Lampang", "value": 52}, {"label": "Uttaradit", "value": 53}, {
                    "label": "Phrae",
                    "value": 54
                }, {"label": "Nan", "value": 55},
                {"label": "Phayao", "value": 56}, {"label": "Chiang Rai", "value": 57}, {
                    "label": "Mae Hong Son",
                    "value": 58
                }, {"label": "Nakhon Sawan", "value": 60},
                {"label": "Uthai Thani", "value": 61}, {"label": "Kamphaeng Phet", "value": 62}, {
                    "label": "Tak",
                    "value": 63
                }, {"label": "Sukhothai", "value": 64},
                {"label": "Phitsanulok", "value": 65}, {"label": "Phichit", "value": 66}, {
                    "label": "Phetchabun",
                    "value": 67
                }, {"label": "Ratchaburi", "value": 70},
                {"label": "Kanchanaburi", "value": 71}, {
                    "label": "Suphan Buri",
                    "value": 72
                }, {"label": "Nakhon Pathom", "value": 73}, {"label": "Samut Sakhon", "value": 74},
                {"label": "Samut Songkhram", "value": 75}, {
                    "label": "Phetchaburi",
                    "value": 76
                }, {"label": "Prachuap Khiri Khan", "value": 77}, {"label": "Nakhon Si Thammarat", "value": 80},
                {"label": "Krabi", "value": 81}, {"label": "Phangnga", "value": 82}, {
                    "label": "Phuket",
                    "value": 83
                }, {"label": "Surat Thani", "value": 84},
                {"label": "Ranong", "value": 85}, {"label": "Chumphon", "value": 86}, {
                    "label": "Songkhla",
                    "value": 90
                }, {"label": "Satun", "value": 91},
                {"label": "Trang", "value": 92}, {"label": "Phatthalung", "value": 93}, {
                    "label": "Pattani",
                    "value": 94
                }, {"label": "Yala", "value": 95},
                {"label": "Narathiwat", "value": 96}, {"label": "Phatthaya", "value": "S"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "TJ":
            state_options = [
                {"label": "Dushanbe", "value": "DU"}, {
                    "label": "Kuhistoni Badakhshon",
                    "value": "GB"
                }, {"label": "Khatlon", "value": "KT"}, {"label": "nohiyahoi tobei jumhurí", "value": "RA"},
                {"label": "Sughd", "value": "SU"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "TL":
            state_options = [
                {"label": "Aileu", "value": "AL"}, {"label": "Ainaro", "value": "AN"}, {
                    "label": "Baucau",
                    "value": "BA"
                }, {"label": "Bobonaro", "value": "BO"},
                {"label": "Cova Lima", "value": "CO"}, {"label": "Díli", "value": "DI"}, {
                    "label": "Ermera",
                    "value": "ER"
                }, {"label": "Lautem", "value": "LA"},
                {"label": "Liquiça", "value": "LI"}, {"label": "Manufahi", "value": "MF"}, {
                    "label": "Manatuto",
                    "value": "MT"
                }, {"label": "Oecussi", "value": "OE"},
                {"label": "Viqueque", "value": "VI"}
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "TM":
            state_options = [
                {"label": "Ahal", "value": "A"}, {"label": "Balkan", "value": "B"}, {
                    "label": "Dasoguz",
                    "value": "D"
                }, {"label": "Lebap", "value": "L"},
                {"label": "Mary", "value": "M"}, {"label": "Asgabat", "value": "S"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "TN":
            state_options = [
                {"label": "Tunis", "value": 11}, {"label": "L'Ariana", "value": 12}, {
                    "label": "Ben Arous",
                    "value": 13
                }, {"label": "La Manouba", "value": 14},
                {"label": "Nabeul", "value": 21}, {"label": "Zaghouan", "value": 22}, {
                    "label": "Bizerte",
                    "value": 23
                }, {"label": "Béja", "value": 31},
                {"label": "Jendouba", "value": 32}, {"label": "Le Kef", "value": 33}, {
                    "label": "Siliana",
                    "value": 34
                }, {"label": "Kairouan", "value": 41},
                {"label": "Kasserine", "value": 42}, {"label": "Sidi Bouzid", "value": 43}, {
                    "label": "Sousse",
                    "value": 51
                }, {"label": "Monastir", "value": 52},
                {"label": "Mahdia", "value": 53}, {"label": "Sfax", "value": 61}, {
                    "label": "Gafsa",
                    "value": 71
                }, {"label": "Tozeur", "value": 72},
                {"label": "Kébili", "value": 73}, {"label": "Gabès", "value": 81}, {
                    "label": "Médenine",
                    "value": 82
                }, {"label": "Tataouine", "value": 83}
            ];
            document.getElementById(stateId).innerHTML = "Governorate";
            break;
        case "TO":
            state_options = [
                {"label": "'Eua", "value": 1}, {"label": "Ha'apai", "value": 2}, {
                    "label": "Niuas",
                    "value": 3
                }, {"label": "Tongatapu", "value": 4},
                {"label": "Vava'u", "value": 5}
            ];
            document.getElementById(stateId).innerHTML = "Division";
            break;
        case "TR":
            state_options = [
                {"label": "Adana", "value": 1}, {"label": "Adiyaman", "value": 2}, {
                    "label": "Afyonkarahisar",
                    "value": 3
                }, {"label": "Agri", "value": 4},
                {"label": "Amasya", "value": 5}, {"label": "Ankara", "value": 6}, {
                    "label": "Antalya",
                    "value": 7
                }, {"label": "Artvin", "value": 8},
                {"label": "Aydin", "value": 9}, {"label": "Balikesir", "value": 10}, {
                    "label": "Bilecik",
                    "value": 11
                }, {"label": "Bingöl", "value": 12},
                {"label": "Bitlis", "value": 13}, {"label": "Bolu", "value": 14}, {
                    "label": "Burdur",
                    "value": 15
                }, {"label": "Bursa", "value": 16},
                {"label": "Canakkale", "value": 17}, {"label": "Çankiri", "value": 18}, {
                    "label": "Corum",
                    "value": 19
                }, {"label": "Denizli", "value": 20},
                {"label": "Diyarbakir", "value": 21}, {"label": "Edirne", "value": 22}, {
                    "label": "Elazig",
                    "value": 23
                }, {"label": "Erzincan", "value": 24},
                {"label": "Erzurum", "value": 25}, {"label": "Eskisehir", "value": 26}, {
                    "label": "Gaziantep",
                    "value": 27
                }, {"label": "Giresun", "value": 28},
                {"label": "Gümüshane", "value": 29}, {"label": "Hakkari", "value": 30}, {
                    "label": "Hatay",
                    "value": 31
                }, {"label": "Isparta", "value": 32},
                {"label": "Mersin", "value": 33}, {"label": "Istanbul", "value": 34}, {
                    "label": "Izmir",
                    "value": 35
                }, {"label": "Kars", "value": 36},
                {"label": "Kastamonu", "value": 37}, {"label": "Kayseri", "value": 38}, {
                    "label": "Kirklareli",
                    "value": 39
                }, {"label": "Kirsehir", "value": 40},
                {"label": "Kocaeli", "value": 41}, {"label": "Konya", "value": 42}, {
                    "label": "Kütahya",
                    "value": 43
                }, {"label": "Malatya", "value": 44},
                {"label": "Manisa", "value": 45}, {"label": "Kahramanmaras", "value": 46}, {
                    "label": "Mardin",
                    "value": 47
                }, {"label": "Mugla", "value": 48},
                {"label": "Mus", "value": 49}, {"label": "Nevsehir", "value": 50}, {
                    "label": "Nigde",
                    "value": 51
                }, {"label": "Ordu", "value": 52},
                {"label": "Rize", "value": 53}, {"label": "Sakarya", "value": 54}, {
                    "label": "Samsun",
                    "value": 55
                }, {"label": "Siirt", "value": 56},
                {"label": "Sinop", "value": 57}, {"label": "Sivas", "value": 58}, {
                    "label": "Tekirdag",
                    "value": 59
                }, {"label": "Tokat", "value": 60},
                {"label": "Trabzon", "value": 61}, {"label": "Tunceli", "value": 62}, {
                    "label": "Sanliurfa",
                    "value": 63
                }, {"label": "Usak", "value": 64},
                {"label": "Van", "value": 65}, {"label": "Yozgat", "value": 66}, {
                    "label": "Zonguldak",
                    "value": 67
                }, {"label": "Aksaray", "value": 68},
                {"label": "Bayburt", "value": 69}, {"label": "Karaman", "value": 70}, {
                    "label": "Kirikkale",
                    "value": 71
                }, {"label": "Batman", "value": 72},
                {"label": "Sirnak", "value": 73}, {"label": "Bartin", "value": 74}, {
                    "label": "Ardahan",
                    "value": 75
                }, {"label": "Igdir", "value": 76},
                {"label": "Yalova", "value": 77}, {"label": "Karabuk", "value": 78}, {
                    "label": "Kilis",
                    "value": 79
                }, {"label": "Osmaniye", "value": 80},
                {"label": "Düzce", "value": 81}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "TT":
            state_options = [
                {"label": "Arima", "value": "ARI"}, {
                    "label": "Chaguanas",
                    "value": "CHA"
                }, {"label": "Couva-Tabaquite-Talparo", "value": "CTT"}, {"label": "Diego Martin", "value": "DMN"},
                {"label": "Mayaro-Rio Claro", "value": "MRC"}, {
                    "label": "Penal-Debe",
                    "value": "PED"
                }, {"label": "Port of Spain", "value": "POS"}, {"label": "Princes Town", "value": "PRT"},
                {"label": "Point Fortin", "value": "PTF"}, {
                    "label": "San Fernando",
                    "value": "SFO"
                }, {"label": "Sangre Grande", "value": "SGE"}, {"label": "Siparia", "value": "SIP"},
                {"label": "San Juan-Laventille", "value": "SJL"}, {
                    "label": "Tobago",
                    "value": "TOB"
                }, {"label": "Tunapuna-Piarco", "value": "TUP"}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "TV":
            state_options = [
                {"label": "Funafuti", "value": "FUN"}, {"label": "Niutao", "value": "NIT"}, {
                    "label": "Nui",
                    "value": "NIU"
                }, {"label": "Nukufetau", "value": "NKF"},
                {"label": "Nukulaelae", "value": "NKL"}, {"label": "Nanumea", "value": "NMA"}, {
                    "label": "Nanumaga",
                    "value": "NMG"
                }, {"label": "Vaitupu", "value": "VAI"}
            ];
            document.getElementById(stateId).innerHTML = "Island council";
            break;
        case "TW":
            state_options = [
                {"label": "Changhua", "value": "CHA"}, {"label": "Chiayi", "value": "CYI"}, {
                    "label": "Chiayi",
                    "value": "CYQ"
                }, {"label": "Hsinchu", "value": "HSQ"},
                {"label": "Hsinchu", "value": "HSZ"}, {"label": "Hualien", "value": "HUA"}, {
                    "label": "Yilan",
                    "value": "ILA"
                }, {"label": "Keelung", "value": "KEE"},
                {"label": "Kaohsiung", "value": "KHH"}, {"label": "Kinmen", "value": "KIN"}, {
                    "label": "Lienchiang",
                    "value": "LIE"
                }, {"label": "Miaoli", "value": "MIA"},
                {"label": "Nantou", "value": "NAN"}, {"label": "New Taipei", "value": "NWT"}, {
                    "label": "Penghu",
                    "value": "PEN"
                }, {"label": "Pingtung", "value": "PIF"},
                {"label": "Taoyuan", "value": "TAO"}, {"label": "Tainan", "value": "TNN"}, {
                    "label": "Taipei",
                    "value": "TPE"
                }, {"label": "Taitung", "value": "TTT"},
                {"label": "Taichung", "value": "TXG"}, {"label": "Yunlin", "value": "YUN"}
            ];
            document.getElementById(stateId).innerHTML = "County";
            break;
        case "TZ":
            state_options = [
                {"label": "Arusha", "value": 1}, {"label": "Dar es Salaam", "value": 2}, {
                    "label": "Dodoma",
                    "value": 3
                }, {"label": "Iringa", "value": 4},
                {"label": "Kagera", "value": 5}, {"label": "Kaskazini Pemba", "value": 6}, {
                    "label": "Kaskazini Unguja",
                    "value": 7
                }, {"label": "Kigoma", "value": 8},
                {"label": "Kilimanjaro", "value": 9}, {"label": "Kusini Pemba", "value": 10}, {
                    "label": "Kusini Unguja",
                    "value": 11
                }, {"label": "Lindi", "value": 12},
                {"label": "Mara", "value": 13}, {"label": "Mbeya", "value": 14}, {
                    "label": "Mjini Magharibi",
                    "value": 15
                }, {"label": "Morogoro", "value": 16},
                {"label": "Mtwara", "value": 17}, {"label": "Mwanza", "value": 18}, {
                    "label": "Pwani",
                    "value": 19
                }, {"label": "Rukwa", "value": 20},
                {"label": "Ruvuma", "value": 21}, {"label": "Shinyanga", "value": 22}, {
                    "label": "Singida",
                    "value": 23
                }, {"label": "Tabora", "value": 24},
                {"label": "Tanga", "value": 25}, {"label": "Manyara", "value": 26}, {
                    "label": "Geita",
                    "value": 27
                }, {"label": "Katavi", "value": 28},
                {"label": "Njombe", "value": 29}, {"label": "Simiyu", "value": 30}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "UA":
            state_options = [
                {"label": "Vinnytska oblast", "value": 5}, {
                    "label": "Volynska oblast",
                    "value": 7
                }, {"label": "Luhanska oblast", "value": 9}, {"label": "Dnipropetrovska oblast", "value": 12},
                {"label": "Donetska oblast", "value": 14}, {
                    "label": "Zhytomyrska oblast",
                    "value": 18
                }, {"label": "Zakarpatska oblast", "value": 21}, {"label": "Zaporizka oblast", "value": 23},
                {"label": "Ivano-Frankivska oblast", "value": 26}, {
                    "label": "Kyiv",
                    "value": 30
                }, {"label": "Kyivska oblast", "value": 32}, {"label": "Kirovohradska oblast", "value": 35},
                {"label": "Sevastopol", "value": 40}, {
                    "label": "Avtonomna Respublika Krym",
                    "value": 43
                }, {"label": "Lvivska oblast", "value": 46}, {"label": "Mykolaivska oblast", "value": 48},
                {"label": "Odeska oblast", "value": 51}, {
                    "label": "Poltavska oblast",
                    "value": 53
                }, {"label": "Rivnenska oblast", "value": 56}, {"label": "Sumska oblast", "value": 59},
                {"label": "Ternopilska oblast", "value": 61}, {
                    "label": "Kharkivska oblast",
                    "value": 63
                }, {"label": "Khersonska oblast", "value": 65}, {"label": "Khmelnytska oblast", "value": 68},
                {"label": "Cherkaska oblast", "value": 71}, {
                    "label": "Chernihivska oblast",
                    "value": 74
                }, {"label": "Chernivetska oblast", "value": 77}
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "UG":
            state_options = [
                {"label": "Kalangala", "value": 101}, {"label": "Kampala", "value": 102}, {
                    "label": "Kiboga",
                    "value": 103
                }, {"label": "Luwero", "value": 104},
                {"label": "Masaka", "value": 105}, {"label": "Mpigi", "value": 106}, {
                    "label": "Mubende",
                    "value": 107
                }, {"label": "Mukono", "value": 108},
                {"label": "Nakasongola", "value": 109}, {"label": "Rakai", "value": 110}, {
                    "label": "Sembabule",
                    "value": 111
                }, {"label": "Kayunga", "value": 112},
                {"label": "Wakiso", "value": 113}, {"label": "Lyantonde", "value": 114}, {
                    "label": "Mityana",
                    "value": 115
                }, {"label": "Nakaseke", "value": 116},
                {"label": "Buikwe", "value": 117}, {"label": "Bukomansibi", "value": 118}, {
                    "label": "Butambala",
                    "value": 119
                }, {"label": "Buvuma", "value": 120},
                {"label": "Gomba", "value": 121}, {"label": "Kalungu", "value": 122}, {
                    "label": "Kyankwanzi",
                    "value": 123
                }, {"label": "Lwengo", "value": 124},
                {"label": "Kyotera", "value": 125}, {"label": "Bugiri", "value": 201}, {
                    "label": "Busia",
                    "value": 202
                }, {"label": "Iganga", "value": 203},
                {"label": "Jinja", "value": 204}, {"label": "Kamuli", "value": 205}, {
                    "label": "Kapchorwa",
                    "value": 206
                }, {"label": "Katakwi", "value": 207},
                {"label": "Kumi", "value": 208}, {"label": "Mbale", "value": 209}, {
                    "label": "Pallisa",
                    "value": 210
                }, {"label": "Soroti", "value": 211},
                {"label": "Tororo", "value": 212}, {"label": "Kaberamaido", "value": 213}, {
                    "label": "Sironko",
                    "value": 215
                }, {"label": "Amuria", "value": 216},
                {"label": "Budaka", "value": 217}, {"label": "Bududa", "value": 218}, {
                    "label": "Bukedea",
                    "value": 219
                }, {"label": "Bukwa", "value": 220},
                {"label": "Butaleja", "value": 221}, {"label": "Kaliro", "value": 222}, {
                    "label": "Manafwa",
                    "value": 223
                }, {"label": "Namutumba", "value": 224},
                {"label": "Bulambuli", "value": 225}, {"label": "Buyende", "value": 226}, {
                    "label": "Kibuku",
                    "value": 227
                }, {"label": "Kween", "value": 228},
                {"label": "Luuka", "value": 229}, {"label": "Namayingo", "value": 230}, {
                    "label": "Ngora",
                    "value": 231
                }, {"label": "Serere", "value": 232},
                {"label": "Butebo", "value": 233}, {"label": "Namisindwa", "value": 234}, {
                    "label": "Mayuge",
                    "value": 244
                }, {"label": "Adjumani", "value": 301},
                {"label": "Apac", "value": 302}, {"label": "Arua", "value": 303}, {
                    "label": "Gulu",
                    "value": 304
                }, {"label": "Kitgum", "value": 305},
                {"label": "Kotido", "value": 306}, {"label": "Lira", "value": 307}, {
                    "label": "Moroto",
                    "value": 308
                }, {"label": "Moyo", "value": 309},
                {"label": "Nebbi", "value": 310}, {"label": "Nakapiripirit", "value": 311}, {
                    "label": "Pader",
                    "value": 312
                }, {"label": "Yumbe", "value": 313},
                {"label": "Abim", "value": 314}, {"label": "Amolatar", "value": 315}, {
                    "label": "Amuru",
                    "value": 316
                }, {"label": "Dokolo", "value": 317},
                {"label": "Kaabong", "value": 318}, {"label": "Koboko", "value": 319}, {
                    "label": "Maracha",
                    "value": 320
                }, {"label": "Oyam", "value": 321},
                {"label": "Kole", "value": 325}, {"label": "Lamwo", "value": 326}, {
                    "label": "Napak",
                    "value": 327
                }, {"label": "Nwoya", "value": 328},
                {"label": "Otuke", "value": 329}, {"label": "Zombo", "value": 330}, {
                    "label": "Omoro",
                    "value": 331
                }, {"label": "Pakwach", "value": 332},
                {"label": "Bundibugyo", "value": 401}, {"label": "Bushenyi", "value": 402}, {
                    "label": "Hoima",
                    "value": 403
                }, {"label": "Kabale", "value": 404},
                {"label": "Kabarole", "value": 405}, {"label": "Kasese", "value": 406}, {
                    "label": "Kibaale",
                    "value": 407
                }, {"label": "Kisoro", "value": 408},
                {"label": "Masindi", "value": 409}, {"label": "Mbarara", "value": 410}, {
                    "label": "Ntungamo",
                    "value": 411
                }, {"label": "Rukungiri", "value": 412},
                {"label": "Kamwenge", "value": 413}, {"label": "Kanungu", "value": 414}, {
                    "label": "Kyenjojo",
                    "value": 415
                }, {"label": "Buliisa", "value": 416},
                {"label": "Ibanda", "value": 417}, {"label": "Isingiro", "value": 418}, {
                    "label": "Kiruhura",
                    "value": 419
                }, {"label": "Buhweju", "value": 420},
                {"label": "Kiryandongo", "value": 421}, {"label": "Kyegegwa", "value": 422}, {
                    "label": "Mitooma",
                    "value": 423
                }, {"label": "Ntoroko", "value": 424},
                {"label": "Rubirizi", "value": 425}, {"label": "Sheema", "value": 426}, {
                    "label": "Kagadi",
                    "value": 427
                }, {"label": "Kakumiro", "value": 428},
                {"label": "Rubanda", "value": 429}, {"label": "Bunyangabu", "value": 430}, {
                    "label": "Rukiga",
                    "value": 431
                }
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "UM":
            state_options = [
                {"label": "Johnston Atoll", "value": 67}, {
                    "label": "Midway Islands",
                    "value": 71
                }, {"label": "Navassa Island ", "value": 76}, {"label": "Wake Island ", "value": 79},
                {"label": "Baker Island", "value": 81}, {
                    "label": "Howland Island",
                    "value": 84
                }, {"label": "Jarvis Island", "value": 86}, {"label": "Kingman Reef", "value": 89},
                {"label": "Palmyra Atoll", "value": 95}
            ];
            document.getElementById(stateId).innerHTML = "Islands";
            break;
        case "US":
            state_options = [
                {"label": "Alaska", "value": "AK"}, {"label": "Alabama", "value": "AL"}, {
                    "label": "Arkansas",
                    "value": "AR"
                }, {"label": "American Samoa ", "value": "AS"},
                {"label": "Arizona", "value": "AZ"}, {"label": "California", "value": "CA"}, {
                    "label": "Colorado",
                    "value": "CO"
                }, {"label": "Connecticut", "value": "CT"},
                {"label": "District of Columbia", "value": "DC"}, {
                    "label": "Delaware",
                    "value": "DE"
                }, {"label": "Florida", "value": "FL"}, {"label": "Georgia", "value": "GA"},
                {"label": "Guam", "value": "GU"}, {"label": "Hawaii", "value": "HI"}, {
                    "label": "Iowa",
                    "value": "IA"
                }, {"label": "Idaho", "value": "ID"},
                {"label": "Illinois", "value": "IL"}, {"label": "Indiana", "value": "IN"}, {
                    "label": "Kansas",
                    "value": "KS"
                }, {"label": "Kentucky", "value": "KY"},
                {"label": "Louisiana", "value": "LA"}, {"label": "Massachusetts", "value": "MA"}, {
                    "label": "Maryland",
                    "value": "MD"
                }, {"label": "Maine", "value": "ME"},
                {"label": "Michigan", "value": "MI"}, {"label": "Minnesota", "value": "MN"}, {
                    "label": "Missouri",
                    "value": "MO"
                }, {"label": "Northern Mariana Islands", "value": "MP"},
                {"label": "Mississippi", "value": "MS"}, {
                    "label": "Montana",
                    "value": "MT"
                }, {"label": "North Carolina", "value": "NC"}, {"label": "North Dakota", "value": "ND"},
                {"label": "Nebraska", "value": "NE"}, {"label": "New Hampshire", "value": "NH"}, {
                    "label": "New Jersey",
                    "value": "NJ"
                }, {"label": "New Mexico", "value": "NM"},
                {"label": "Nevada", "value": "NV"}, {"label": "New York", "value": "NY"}, {
                    "label": "Ohio",
                    "value": "OH"
                }, {"label": "Oklahoma", "value": "OK"},
                {"label": "Oregon", "value": "OR"}, {"label": "Pennsylvania", "value": "PA"}, {
                    "label": "Puerto Rico",
                    "value": "PR"
                }, {"label": "Rhode Island", "value": "RI"},
                {"label": "South Carolina", "value": "SC"}, {
                    "label": "South Dakota",
                    "value": "SD"
                }, {"label": "Tennessee", "value": "TN"}, {"label": "Texas", "value": "TX"},
                {"label": "U.S. Minor Outlying Islands", "value": "UM"}, {
                    "label": "Utah",
                    "value": "UT"
                }, {"label": "Virginia", "value": "VA"}, {"label": "Virgin Islands of the U.S.", "value": "VI"},
                {"label": "Vermont", "value": "VT"}, {"label": "Washington", "value": "WA"}, {
                    "label": "Wisconsin",
                    "value": "WI"
                }, {"label": "West Virginia", "value": "WV"},
                {"label": "Wyoming", "value": "WY"}
            ];
            document.getElementById(stateId).innerHTML = "State";
            break;
        case "UY":
            state_options = [
                {"label": "Artigas", "value": "AR"}, {"label": "Canelones", "value": "CA"}, {
                    "label": "Cerro Largo",
                    "value": "CL"
                }, {"label": "Colonia", "value": "CO"},
                {"label": "Durazno", "value": "DU"}, {"label": "Florida", "value": "FD"}, {
                    "label": "Flores",
                    "value": "FS"
                }, {"label": "Lavalleja", "value": "LA"},
                {"label": "Maldonado", "value": "MA"}, {"label": "Montevideo", "value": "MO"}, {
                    "label": "Paysandú",
                    "value": "PA"
                }, {"label": "Río Negro", "value": "RN"},
                {"label": "Rocha", "value": "RO"}, {"label": "Rivera", "value": "RV"}, {
                    "label": "Salto",
                    "value": "SA"
                }, {"label": "San José", "value": "SJ"},
                {"label": "Soriano", "value": "SO"}, {"label": "Tacuarembó", "value": "TA"}, {
                    "label": "Treinta y Tres",
                    "value": "TT"
                }
            ];
            document.getElementById(stateId).innerHTML = "Department";
            break;
        case "UZ":
            state_options = [
                {"label": "Andijon", "value": "AN"}, {"label": "Bukhoro", "value": "BU"}, {
                    "label": "Farg‘ona",
                    "value": "FA"
                }, {"label": "Jizzax", "value": "JI"},
                {"label": "Khorazm", "value": "KH"}, {"label": "Namangan", "value": "NG"}, {
                    "label": "Nawoiy",
                    "value": "NW"
                }, {"label": "Qashqadaryo", "value": "QA"},
                {"label": "Qoraqalpog‘iston Respublikasi", "value": "QR"}, {
                    "label": "Samarqand",
                    "value": "SA"
                }, {"label": "Sirdaryo", "value": "SI"}, {"label": "Surkhondaryo", "value": "SU"},
                {"label": "Toshkent", "value": "TK"}, {"label": "Toshkent", "value": "TO"}, {
                    "label": "Xorazm",
                    "value": "XO"
                }
            ];
            document.getElementById(stateId).innerHTML = "Region";
            break;
        case "VC":
            state_options = [
                {"label": "Charlotte", "value": 1}, {"label": "Saint Andrew", "value": 2}, {
                    "label": "Saint David",
                    "value": 3
                }, {"label": "Saint George", "value": 4},
                {"label": "Saint Patrick", "value": 5}, {"label": "Grenadines", "value": 6}
            ];
            document.getElementById(stateId).innerHTML = "Parish";
            break;
        case "VE":
            state_options = [
                {"label": "Distrito Capital", "value": "A"}, {"label": "Anzoátegui", "value": "B"}, {
                    "label": "Apure",
                    "value": "C"
                }, {"label": "Aragua", "value": "D"},
                {"label": "Barinas", "value": "E"}, {"label": "Bolívar", "value": "F"}, {
                    "label": "Carabobo",
                    "value": "G"
                }, {"label": "Cojedes", "value": "H"},
                {"label": "Falcón", "value": "I"}, {"label": "Guárico", "value": "J"}, {
                    "label": "Lara",
                    "value": "K"
                }, {"label": "Mérida", "value": "L"},
                {"label": "Miranda", "value": "M"}, {"label": "Monagas", "value": "N"}, {
                    "label": "Nueva Esparta",
                    "value": "O"
                }, {"label": "Portuguesa", "value": "P"},
                {"label": "Sucre", "value": "R"}, {"label": "Táchira", "value": "S"}, {
                    "label": "Trujillo",
                    "value": "T"
                }, {"label": "Yaracuy", "value": "U"},
                {"label": "Zulia", "value": "V"}, {"label": "Dependencias Federales", "value": "W"}, {
                    "label": "vargas",
                    "value": "X"
                }, {"label": "Delta Amacuro", "value": "Y"},
                {"label": "Amazonas", "value": "Z"}
            ];
            document.getElementById(stateId).innerHTML = "State";
            break;
        case "VN":
            state_options = [
                {"label": "Lai Châu", "value": 1}, {"label": "Lào Cai", "value": 2}, {
                    "label": "Hà Giang",
                    "value": 3
                }, {"label": "Cao B?ng", "value": 4},
                {"label": "Son La", "value": 5}, {"label": "Yên Bái", "value": 6}, {
                    "label": "Tuyên Quang",
                    "value": 7
                }, {"label": "L?ng Son", "value": 9},
                {"label": "Qu?ng Ninh", "value": 13}, {"label": "Hòa Bình", "value": 14}, {
                    "label": "Ninh Bình",
                    "value": 18
                }, {"label": "Thái Bình", "value": 20},
                {"label": "Thanh Hóa", "value": 21}, {"label": "Ngh? An", "value": 22}, {
                    "label": "Hà Tinh",
                    "value": 23
                }, {"label": "Qu?ng Bình", "value": 24},
                {"label": "Qu?ng Tr?", "value": 25}, {"label": "Th?a Thiên-Hu?", "value": 26}, {
                    "label": "Qu?ng Nam",
                    "value": 27
                }, {"label": "Kon Tum", "value": 28},
                {"label": "Qu?ng Ngãi", "value": 29}, {"label": "Gia Lai", "value": 30}, {
                    "label": "Bình ???nh",
                    "value": 31
                }, {"label": "Phú Yên", "value": 32},
                {"label": "???k L?k", "value": 33}, {"label": "Khánh Hòa", "value": 34}, {
                    "label": "Lâm ???ng",
                    "value": 35
                }, {"label": "Ninh Thu?n", "value": 36},
                {"label": "Tây Ninh", "value": 37}, {"label": "???ng Nai", "value": 39}, {
                    "label": "Bình Thu?n",
                    "value": 40
                }, {"label": "Long An", "value": 41},
                {"label": "Bà R?a - Vung Tàu", "value": 43}, {"label": "An Giang", "value": 44}, {
                    "label": "???ng Tháp",
                    "value": 45
                }, {"label": "Ti?n Giang", "value": 46},
                {"label": "Ki?n Giang", "value": 47}, {"label": "Vinh Long", "value": 49}, {
                    "label": "B?n Tre",
                    "value": 50
                }, {"label": "Trà Vinh", "value": 51},
                {"label": "Sóc Trang", "value": 52}, {"label": "B?c K?n", "value": 53}, {
                    "label": "B?c Giang",
                    "value": 54
                }, {"label": "B?c Liêu", "value": 55},
                {"label": "B?c Ninh", "value": 56}, {"label": "Bình Duong", "value": 57}, {
                    "label": "Bình Phu?c",
                    "value": 58
                }, {"label": "Cà Mau", "value": 59},
                {"label": "H?i Duong", "value": 61}, {"label": "Hà Nam", "value": 63}, {
                    "label": "Hung Yên",
                    "value": 66
                }, {"label": "Nam ???nh", "value": 67},
                {"label": "Phú Th?", "value": 68}, {"label": "Thái Nguyên", "value": 69}, {
                    "label": "Vinh Phúc",
                    "value": 70
                }, {"label": "??i?n Biên", "value": 71},
                {"label": "???k Nông", "value": 72}, {"label": "H?u Giang", "value": 73}, {
                    "label": "Can Tho",
                    "value": "CT"
                }, {"label": "Da Nang, thanh pho", "value": "DN"},
                {"label": "Ha Noi", "value": "HN"}, {"label": "Hai Phong", "value": "HP"}, {
                    "label": "Ho Chi Minh",
                    "value": "SG"
                }
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "VU":
            state_options = [
                {"label": "Malampa", "value": "MAP"}, {"label": "Pénama", "value": "PAM"}, {
                    "label": "Sanma",
                    "value": "SAM"
                }, {"label": "Shéfa", "value": "SEE"},
                {"label": "Taféa", "value": "TAE"}, {"label": "Torba", "value": "TOB"}
            ];
            document.getElementById(stateId).innerHTML = "State";
            break;
        case "WS":
            state_options = [
                {"label": "A'ana", "value": "AA"}, {"label": "Aiga-i-le-Tai", "value": "AL"}, {
                    "label": "Atua",
                    "value": "AT"
                }, {"label": "Fa'asaleleaga", "value": "FA"},
                {"label": "Gaga'emauga", "value": "GE"}, {"label": "Gagaifomauga", "value": "GI"}, {
                    "label": "Palauli",
                    "value": "PA"
                }, {"label": "Satupa 'itea", "value": "SA"},
                {"label": "Tuamasaga", "value": "TU"}, {"label": "Va'a-o-Fonoti", "value": "VF"}, {
                    "label": "Vaisigano",
                    "value": "VS"
                }
            ];
            document.getElementById(stateId).innerHTML = "District";
            break;
        case "YE":
            state_options = [
                {"label": "Abyan", "value": "AB"}, {"label": "‘Adan", "value": "AD"}, {
                    "label": "‘Amran",
                    "value": "AM"
                }, {"label": "Al Bay?a’", "value": "BA"},
                {"label": "A? ?ali‘", "value": "DA"}, {"label": "Dhamar", "value": "DH"}, {
                    "label": "?a?ramawt",
                    "value": "HD"
                }, {"label": "?ajjah", "value": "HJ"},
                {"label": "Al ?udaydah", "value": "HU"}, {"label": "Ibb", "value": "IB"}, {
                    "label": "Al Jawf",
                    "value": "JA"
                }, {"label": "Lahij", "value": "LA"},
                {"label": "Ma’rib", "value": "MA"}, {"label": "Al Mahrah", "value": "MR"}, {
                    "label": "Al Ma?wit",
                    "value": "MW"
                }, {"label": "Raymah", "value": "RA"},
                {"label": "Amanat al ‘Asimah [city]", "value": "SA"}, {
                    "label": "Sa'dah",
                    "value": "SD"
                }, {"label": "Shabwah", "value": "SH"}, {"label": "San?a'", "value": "SN"},
                {"label": "Arkhabil Suqutrá", "value": "SU"}, {"label": "Ta'izz", "value": "TA"}
            ];
            document.getElementById(stateId).innerHTML = "Governorate";
            break;
        case "ZA":
            state_options = [
                {"label": "Eastern Cape", "value": "EC"}, {"label": "Free State", "value": "FS"}, {
                    "label": "Gauteng",
                    "value": "GT"
                }, {"label": "Limpopo", "value": "LP"},
                {"label": "Mpumalanga", "value": "MP"}, {
                    "label": "Northern Cape",
                    "value": "NC"
                }, {"label": "Kwazulu-Natal", "value": "NL"}, {"label": "North-West", "value": "NW"},
                {"label": "Western Cape", "value": "WC"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "ZM":
            state_options = [
                {"label": "Western", "value": 1}, {"label": "Central", "value": 2}, {
                    "label": "Eastern",
                    "value": 3
                }, {"label": "Luapula", "value": 4},
                {"label": "Northern", "value": 5}, {"label": "North-Western", "value": 6}, {
                    "label": "Southern",
                    "value": 7
                }, {"label": "Copperbelt", "value": 8},
                {"label": "Lusaka", "value": 9}, {"label": "Muchinga", "value": 10}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        case "ZW":
            state_options = [
                {"label": "Bulawayo", "value": "BU"}, {"label": "Harare", "value": "HA"}, {
                    "label": "Manicaland",
                    "value": "MA"
                }, {"label": "Mashonaland Central", "value": "MC"},
                {"label": "Mashonaland East", "value": "ME"}, {
                    "label": "Midlands",
                    "value": "MI"
                }, {"label": "Matabeleland North", "value": "MN"}, {"label": "Matabeleland South", "value": "MS"},
                {"label": "Masvingo", "value": "MV"}, {"label": "Mashonaland West", "value": "MW"}
            ];
            document.getElementById(stateId).innerHTML = "Province";
            break;
        default :
            state_options = "";
            document.getElementById(stateId).innerHTML = "State";
            break;
    }

}

function stateinp()
{
    var availableTags = state_options;

    $("#state").autocomplete({
        source: availableTags
    });
    $("#statedc").autocomplete({
        source: availableTags
    });
    $("#state_ach").autocomplete({
        source: availableTags
    });
    $("#chkstate").autocomplete({
        source: availableTags
    });
    //dp = DusPay
    $("#dpstate").autocomplete({
        source: availableTags
    });
    // DirectDebit , SEPA.jsp
    $("#dbstate").autocomplete({
        source: availableTags
    });
    // CupUpi.jsp
    $("#state_cu").autocomplete({
        source: availableTags
    });
    //ep = EzPay
    $("#epstate").autocomplete({
        source: availableTags
    });
}