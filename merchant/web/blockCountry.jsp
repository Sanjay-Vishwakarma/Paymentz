<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.BlacklistVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="java.util.List" %>
<%@ include file="functions.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="blocktab.jsp" %>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <style type="text/css">
    #main{background-color: #ffffff}
    :target:before {
      content: "";
      display: block;
      height: 50px;
      margin: -50px 0 0;
    }
    .table > thead > tr > th {font-weight: inherit;}
    :target:before {
      content: "";
      display: block;
      height: 90px;
      margin: -50px 0 0;
    }
    footer{border-top:none;margin-top: 0;padding: 0;}
    /********************Table Responsive Start**************************/
    @media (max-width: 640px){
      table {border: 0;}
      table thead { display: none;}

      /*tr:nth-child(odd), tr:nth-child(even) {background: #ffffff;}*/
      table td {
        display: block;
        border-bottom: none;
        padding-left: 0;
        padding-right: 0;
      }

      table td:before {
        content: attr(data-label);
        float: left;
        width: 100%;
        font-weight: bold;
      }
    }

    table {
      width: 100%;
      max-width: 100%;
      border-collapse: collapse;
      margin-bottom: 20px;
      display: table;
      border-collapse: separate;
      border-color: grey;
    }

    thead {
      display: table-header-group;
      vertical-align: middle;
      border-color: inherit;
    }

    tr:nth-child(odd) {background: transparent;}
    tr {
      display: table-row;
      vertical-align: inherit;
      border-color: inherit;
    }

    th {padding-right: 1em;text-align: left;font-weight: bold;}
    td, th {display: table-cell;vertical-align: inherit;}
    tbody {
      display: table-row-group;
      vertical-align: middle;
      border-color: inherit;
    }

    td {
      padding-top: 6px !important;
      padding-bottom: 6px;
      padding-left: 10px;
      padding-right: 10px;
      vertical-align: top;
      border-bottom: none;
    }
    .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td{border-top: 1px solid #ddd;}
    /********************Table Responsive Ends**************************/
  </style>
  <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
  <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>

 <%-- <script src="/merchant/NewCss/js/jquery-1.12.4.min.js"></script>
  <script src="/merchant/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/merchant/javascript/autocomplete_merchant_terminalid.js"></script>
  <script>

    $(document).ready(function(){

      var w = $(window).width();

      //alert(w);

      if(w > 990){
        //alert("It's greater than 990px");
        $("body").removeClass("smallscreen").addClass("widescreen");
        $("#wrapper").removeClass("enlarged");
      }
      else{
        //alert("It's less than 990px");
        $("body").removeClass("widescreen").addClass("smallscreen");
        $("#wrapper").addClass("enlarged");
        $(".left ul").removeAttr("style");
      }
    });

  </script>
  <script>
    function ToggleAll(checkbox)
    {
      flag = checkbox.checked;
      var checkboxes = document.getElementsByName("id");
      var total_boxes = checkboxes.length;

      for(i=0; i<total_boxes; i++ )
      {
        checkboxes[i].checked =flag;
      }
    }
    function Unblock()
    {
      var checkboxes = document.getElementsByName("id");
      var total_boxes = checkboxes.length;
      flag = false;

      for(i=0; i<total_boxes; i++ )
      {
        if(checkboxes[i].checked)
        {
          flag= true;
          break;
        }
      }
      if(!flag)
      {
        alert("Select at least one record");
        return false;
      }
      if (confirm("Do you really want to unblock all selected Data."))
      {
        document.BlockCountry.submit();
      }
    }
  </script>
</head>
<body>
  <%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  String country=request.getParameter("countryList")==null?"":request.getParameter("countryList");
  Functions functions=new Functions();
  int pageno=1;
  int recordsPerPage=15;
  pageno= Functions.convertStringtoInt(request.getParameter("SPageno"),1);
  recordsPerPage=Functions.convertStringtoInt(request.getParameter("SRecords"),15);

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String blockCountry_blocked_country1 = !functions.isEmptyOrNull(rb1.getString("blockCountry_blocked_country1"))?rb1.getString("blockCountry_blocked_country1"): "Blocked Country";
    String blockCountry_merchantid1 = !functions.isEmptyOrNull(rb1.getString("blockCountry_merchantid1"))?rb1.getString("blockCountry_merchantid1"): "Merchant ID*";
    String blockCountry_country_name = !functions.isEmptyOrNull(rb1.getString("blockCountry_country_name"))?rb1.getString("blockCountry_country_name"): "Country Name";
    String blockCountry_select_a_country = !functions.isEmptyOrNull(rb1.getString("blockCountry_select_a_country"))?rb1.getString("blockCountry_select_a_country"): "Select a Country";
    String blockCountry_search = !functions.isEmptyOrNull(rb1.getString("blockCountry_search"))?rb1.getString("blockCountry_search"): "Search";
    String blockCountry_blocked_country_list = !functions.isEmptyOrNull(rb1.getString("blockCountry_blocked_country_list"))?rb1.getString("blockCountry_blocked_country_list"): "Blocked Country List";
    String blockCountry_sorry = !functions.isEmptyOrNull(rb1.getString("blockCountry_sorry"))?rb1.getString("blockCountry_sorry"): "Sorry";
    String blockCountry_no_records = !functions.isEmptyOrNull(rb1.getString("blockCountry_no_records"))?rb1.getString("blockCountry_no_records"): "No records found.";
    String blockCountry_Sr_No = !functions.isEmptyOrNull(rb1.getString("blockCountry_Sr_No"))?rb1.getString("blockCountry_Sr_No"): "Sr No.";
    String blockCountry_Country_Name = !functions.isEmptyOrNull(rb1.getString("blockCountry_Country_Name"))?rb1.getString("blockCountry_Country_Name"): "Country Name";
    String blockCountry_AccountID = !functions.isEmptyOrNull(rb1.getString("blockCountry_AccountID"))?rb1.getString("blockCountry_AccountID"): "AccountID";
    String blockCountry_Showing_Page = !functions.isEmptyOrNull(rb1.getString("blockCountry_Showing_Page"))?rb1.getString("blockCountry_Showing_Page"): "Showing Page";
    String blockCountry_of = !functions.isEmptyOrNull(rb1.getString("blockCountry_of"))?rb1.getString("blockCountry_of"): "of";
    String blockCountry_records = !functions.isEmptyOrNull(rb1.getString("blockCountry_records"))?rb1.getString("blockCountry_records"): "records";
    String blockCountry_page_no=StringUtils.isNotEmpty(rb1.getString("blockCountry_page_no"))?rb1.getString("blockCountry_page_no"):"Page number";
    String blockCountry_total_no_of_records=StringUtils.isNotEmpty(rb1.getString("blockCountry_total_no_of_records"))?rb1.getString("blockCountry_total_no_of_records"):"Total number of records";

%>
<body>
<div class="content-page">
  <div class="content" style="padding:0px 20px ;margin: 0px;">
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=blockCountry_blocked_country1%></strong>
              </h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content">
              <div id="horizontal-form">
                <form action="/merchant/servlet/BlockCountry?ctoken=<%=ctoken%>" method="post" name="forms" >
                  <div align="center">
                    <input type="hidden" value="<%=ctoken%>" id="ctoken">
                    <input type="hidden" value="<%=(String) session.getAttribute("merchantid")%>" id="merchantid">
                    <%
                      String message = (String) request.getAttribute("error");
                      if (functions.isValueNull(message))
                      {
                        out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                      }
                    %>
                    <div class="form-group col-md-1">
                    </div>

                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label><%=blockCountry_merchantid1%></label>
                        </div>
                        <div class="col-md-3">
                          <input type="text" name="merchantid" class="form-control"  value="<%=(String) session.getAttribute("merchantid")%>" disabled>
                        </div>
                      </div>
                    </div>

                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label><%=blockCountry_country_name%></label>
                        </div>
                        <div class="col-md-2">
                          <select name="countryList" class="form-control" <%--style="width:265px;"--%>>
                            <option value="Select Country"><%=blockCountry_select_a_country%></option>

                            <option value="AF|AFG|093|Afghanistan">Afghanistan</option>
                            <option value="AX|ALA|358|Aland Islands">Aland Islands</option>
                            <option value="AL|ALB|355|Albania">Albania</option>
                            <option value="DZ|DZA|231|Algeria">Algeria</option>
                            <option value="AS|ASM|684|American Samoa">American Samoa</option>
                            <option value="AD|AND|376|Andorra">Andorra</option>
                            <option value="AO|AGO|244|Angola">Angola</option>
                            <option value="AI|AIA|001|Anguilla">Anguilla</option>
                            <option value="AQ|ATA|000|Antarctica">Antarctica</option>
                            <option value="AG|ATG|001|Antigua and Barbuda">Antigua and Barbuda</option>
                            <option value="AR|ARG|054|Argentina">Argentina</option>
                            <option value="AM|ARM|374|Armenia">Armenia</option>
                            <option value="AW|ABW|297|Aruba">Aruba</option>
                            <option value="AU|AUS|061|Australia">Australia</option>
                            <option value="AT|AUT|043|Austria">Austria</option>
                            <option value="AZ|AZE|994|Azerbaijan">Azerbaijan</option>
                            <option value="BS|BHS|001|Bahamas">Bahamas</option>
                            <option value="BH|BHR|973|Bahrain">Bahrain</option>
                            <option value="BD|BGD|880|Bangladesh">Bangladesh</option>
                            <option value="BB|BRB|001|Barbados">Barbados</option>
                            <option value="BY|BLR|375|Belarus">Belarus</option>
                            <option value="BE|BEL|032|Belgium">Belgium</option>
                            <option value="BZ|BLZ|501|Belize">Belize</option>
                            <option value="BJ|BEN|229|Benin">Benin</option>
                            <option value="BM|BMU|001|Bermuda">Bermuda</option>
                            <option value="BT|BTN|975|Bhutan">Bhutan</option>
                            <option value="BO|BOL|591|Bolivia">Bolivia</option>
                            <option value="BA|BIH|387|Bosnia and Herzegovina">Bosnia and Herzegovina</option>
                            <option value="BW|BWA|267|Botswana">Botswana</option>
                            <option value="BV|BVT|000|Bouvet Island">Bouvet Island</option>
                            <option value="BR|BRA|055|Brazil">Brazil</option>
                            <option value="IO|IOT|246|British Indian Ocean Territory">British Indian Ocean Territory</option>
                            <option value="VG|VGB|001|British Virgin Islands">British Virgin Islands</option>
                            <option value="BN|BRN|673|Brunei">Brunei</option>
                            <option value="BG|BGR|359|Bulgaria">Bulgaria</option>
                            <option value="BF|BFA|226|Burkina Faso">Burkina Faso</option>
                            <option value="BI|BDI|257|Burundi">Burundi</option>
                            <option value="KH|KHM|855|Cambodia">Cambodia</option>
                            <option value="CM|CMR|237|Cameroon">Cameroon</option>
                            <option value="CA|CAN|001|Canada">Canada</option>
                            <option value="CV|CPV|238|Cape Verde">Cape Verde</option>
                            <option value="KY|CYM|001|Cayman Islands">Cayman Islands</option>
                            <option value="CF|CAF|236|Central African Republic">Central African Republic</option>
                            <option value="TD|TCD|235|Chad">Chad</option>
                            <option value="CL|CHL|056|Chile">Chile</option>
                            <option value="CN|CHN|086|China">China</option>
                            <option value="CX|CXR|061|Christmas Island">Christmas Island</option>
                            <option value="CC|CCK|061|Cocos (Keeling) Islands">Cocos (Keeling) Islands</option>
                            <option value="CO|COL|057|Colombia">Colombia</option>
                            <option value="KM|COM|269|Comoros">Comoros</option>
                            <option value="CK|COK|682|Cook Islands">Cook Islands</option>
                            <option value="CR|CRI|506|Costa Rica">Costa Rica</option>
                            <option value="CI|CIV|225|Cote d'Ivoire">Cote d'Ivoire</option>
                            <option value="HR|HRV|385|Croatia">Croatia</option>
                            <option value="CU|CUB|053|Cuba">Cuba</option>
                            <option value="CY|CYP|357|Cyprus">Cyprus</option>
                            <option value="CZ|CZE|420|Czech Republic">Czech Republic</option>
                            <option value="CD|COD|243|Democratic Republic of the Congo">Democratic Republic of the Congo</option>
                            <option value="DK|DNK|045|Denmark">Denmark</option>
                            <option value="DJ|DJI|253|Djibouti">Djibouti</option>
                            <option value="DM|DMA|001|Dominica">Dominica</option>
                            <option value="DO|DOM|001|Dominican Republic">Dominican Republic</option>
                            <option value="EC|ECU|593|Ecuador">Ecuador</option>
                            <option value="EG|EGY|020|Egypt">Egypt</option>
                            <option value="SV|SLV|503|El Salvador">El Salvador</option>
                            <option value="GQ|GNQ|240|Equatorial Guinea">Equatorial Guinea</option>
                            <option value="ER|ERI|291|Eritrea">Eritrea</option>
                            <option value="EE|EST|372|Estonia">Estonia</option>
                            <option value="ET|ETH|251|Ethiopia">Ethiopia</option>
                            <option value="FK|FLK|500|Falkland Islands">Falkland Islands</option>
                            <option value="FO|FRO|298|Faroe Islands">Faroe Islands</option>
                            <option value="FJ|FJI|679|Fiji">Fiji</option>
                            <option value="FI|FIN|358|Finland">Finland</option>
                            <option value="FR|FRA|033|France">France</option>
                            <option value="GF|GUF|594|French Guiana">French Guiana</option>
                            <option value="PF|PYF|689|French Polynesia">French Polynesia</option>
                            <option value="TF|ATF|000|French Southern and Antarctic Lands">French Southern and Antarctic Lands</option>
                            <option value="GA|GAB|241|Gabon">Gabon</option>
                            <option value="GM|GMB|220|Gambia">Gambia</option>
                            <option value="GE|GEO|995|Georgia">Georgia</option>
                            <option value="DE|DEU|049|Germany">Germany</option>
                            <option value="GH|GHA|233|Ghana">Ghana</option>
                            <option value="GI|GIB|350|Gibraltar">Gibraltar</option>
                            <option value="GR|GRC|030|Greece">Greece</option>
                            <option value="GL|GRL|299|Greenland">Greenland</option>
                            <option value="GD|GRD|001|Grenada">Grenada</option>
                            <option value="GP|GLP|590|Guadeloupe">Guadeloupe</option>
                            <option value="GU|GUM|001|Guam">Guam</option>
                            <option value="GT|GTM|502|Guatemala">Guatemala</option>
                            <option value="GG|GGY|000|Guernsey">Guernsey</option>
                            <option value="GN|GIN|224|Guinea">Guinea</option>
                            <option value="GW|GNB|245|Guinea-Bissau">Guinea-Bissau</option>
                            <option value="GY|GUY|592|Guyana">Guyana</option>
                            <option value="HT|HTI|509|Haiti">Haiti</option>
                            <option value="HM|HMD|672|Heard Island & McDonald Islands">Heard Island & McDonald Islands</option>
                            <option value="HN|HND|504|Honduras">Honduras</option>
                            <option value="HK|HKG|852|Hong Kong">Hong Kong</option>
                            <option value="HU|HUN|036|Hungary">Hungary</option>
                            <option value="IS|ISL|354|Iceland">Iceland</option>
                            <option value="IN|IND|091|India">India</option>
                            <option value="ID|IDN|062|Indonesia">Indonesia</option>
                            <option value="IR|IRN|098|Iran">Iran</option>
                            <option value="IQ|IRQ|964|Iraq">Iraq</option>
                            <option value="IE|IRL|353|Ireland">Ireland</option>
                            <option value="IL|ISR|972|Israel">Israel</option>
                            <option value="IT|ITA|039|Italy">Italy</option>
                            <option value="JM|JAM|001|Jamaica">Jamaica</option>
                            <option value="JP|JPN|081|Japan">Japan</option>
                            <option value="JE|JEY|044|Jersey">Jersey</option>
                            <option value="JO|JOR|962|Jordan">Jordan</option>
                            <option value="KZ|KAZ|007|Kazakhstan">Kazakhstan</option>
                            <option value="KE|KEN|254|Kenya">Kenya</option>
                            <option value="KI|KIR|686|Kiribati">Kiribati</option>
                            <option value="KW|KWT|965|Kuwait">Kuwait</option>
                            <option value="KG|KGZ|996|Kyrgyzstan">Kyrgyzstan</option>
                            <option value="LA|LAO|856|Laos">Laos</option>
                            <option value="LV|LVA|371|Latvia">Latvia</option>
                            <option value="LB|LBN|961|Lebanon">Lebanon</option>
                            <option value="LS|LSO|266|Lesotho">Lesotho</option>
                            <option value="LR|LBR|231|Liberia">Liberia</option>
                            <option value="LY|LBY|218|Libya">Libya</option>
                            <option value="LI|LIE|423|Liechtenstein">Liechtenstein</option>
                            <option value="LT|LTU|370|Lithuania">Lithuania</option>
                            <option value="LU|LUX|352|Luxembourg">Luxembourg</option>
                            <option value="MO|MAC|853|Macau, China">Macau, China</option>
                            <option value="MK|MKD|389|Macedonia">Macedonia</option>
                            <option value="MG|MDG|261|Madagascar">Madagascar</option>
                            <option value="MW|MWI|265|Malawi">Malawi</option>
                            <option value="MY|MYS|060|Malaysia">Malaysia</option>
                            <option value="MV|MDV|960|Maldives">Maldives</option>
                            <option value="ML|MLI|223|Mali">Mali</option>
                            <option value="MT|MLT|356|Malta">Malta</option>
                            <option value="MH|MHL|692|Marshall Islands">Marshall Islands</option>
                            <option value="MQ|MTQ|596|Martinique">Martinique</option>
                            <option value="MR|MRT|222|Mauritania">Mauritania</option>
                            <option value="MU|MUS|230|Mauritius">Mauritius</option>
                            <option value="YT|MYT|269|Mayotte">Mayotte</option>
                            <option value="MX|MEX|052|Mexico">Mexico</option>
                            <option value="FM|FSM|691|Micronesia, Federated States of">Micronesia, Federated States of</option>
                            <option value="MD|MDA|373|Moldova">Moldova</option>
                            <option value="MC|MCO|377|Monaco">Monaco</option>
                            <option value="MN|MNG|976|Mongolia">Mongolia</option>
                            <option value="ME|MNE|382|Montenegro">Montenegro</option>
                            <option value="MS|MSR|001|Montserrat">Montserrat</option>
                            <option value="MA|MAR|212|Morocco">Morocco</option>
                            <option value="MZ|MOZ|258|Mozambique">Mozambique</option>
                            <option value="MM|MMR|095|Myanmar">Myanmar</option>
                            <option value="NA|NAM|264|Namibia">Namibia</option>
                            <option value="NR|NRU|674|Nauru">Nauru</option>
                            <option value="NP|NPL|977|Nepal">Nepal</option>
                            <option value="AN|ANT|599|Netherlands Antilles">Netherlands Antilles</option>
                            <option value="NL|NLD|031|Netherlands">Netherlands</option>
                            <option value="NC|NCL|687|New Caledonia">New Caledonia</option>
                            <option value="NZ|NZL|064|New Zealand">New Zealand</option>
                            <option value="NI|NIC|505|Nicaragua">Nicaragua</option>
                            <option value="NE|NER|227|Niger">Niger</option>
                            <option value="NG|NGA|234|Nigeria">Nigeria</option>
                            <option value="NU|NIU|683|Niue">Niue</option>
                            <option value="NF|NFK|672|Norfolk Island">Norfolk Island</option>
                            <option value="KP|PRK|850|North Korea">North Korea</option>
                            <option value="MP|MNP|001|Northern Mariana Islands">Northern Mariana Islands</option>
                            <option value="NO|NOR|047|Norway">Norway</option>
                            <option value="OM|OMN|968|Oman">Oman</option>
                            <option value="PK|PAK|092|Pakistan">Pakistan</option>
                            <option value="PW|PLW|680|Palau">Palau</option>
                            <option value="PS|PSE|970|Palestinian Authority">Palestinian Authority</option>
                            <option value="PA|PAN|507|Panama">Panama</option>
                            <option value="PG|PNG|675|Papua New Guinea">Papua New Guinea</option>
                            <option value="PY|PRY|595|Paraguay">Paraguay</option>
                            <option value="PE|PER|051|Peru">Peru</option>
                            <option value="PH|PHL|063|Philippines">Philippines</option>
                            <option value="PN|PCN|064|Pitcairn Islands">Pitcairn Islands</option>
                            <option value="PL|POL|048|Poland">Poland</option>
                            <option value="PT|PRT|351|Portugal">Portugal</option>
                            <option value="PR|PRI|001|Puerto Rico">Puerto Rico</option>
                            <option value="QA|QAT|974|Qatar">Qatar</option>
                            <option value="CG|COD|242|Republic of the Congo">Republic of the Congo</option>
                            <option value="RE|REU|262|Reunion">Reunion</option>
                            <option value="RO|ROU|040|Romania">Romania</option>
                            <option value="RU|RUS|007|Russia">Russia</option>
                            <option value="RW|RWA|250|Rwanda">Rwanda</option>
                            <option value="BL|BLM|590|Saint Barthelemy">Saint Barthelemy</option>
                            <option value="SH|SHN|290|Saint Helena, Ascension & Tristan daCunha">Saint Helena, Ascension & Tristan daCunha</option>
                            <option value="KN|KNA|001|Saint Kitts and Nevis">Saint Kitts and Nevis</option>
                            <option value="LC|LCA|001|Saint Lucia">Saint Lucia</option>
                            <option value="MF|MAF|590|Saint Martin">Saint Martin</option>
                            <option value="PM|SPM|508|Saint Pierre and Miquelon">Saint Pierre and Miquelon</option>
                            <option value="VC|VCT|001|Saint Vincent and Grenadines">Saint Vincent and Grenadines</option>
                            <option value="WS|WSM|685|Samoa">Samoa</option>
                            <option value="SM|SMR|378|San Marino">San Marino</option>
                            <option value="ST|STP|239|Sao Tome and Principe">Sao Tome and Principe</option>
                            <option value="SA|SAU|966|Saudi Arabia">Saudi Arabia</option>
                            <option value="SN|SEN|221|Senegal">Senegal</option>
                            <option value="RS|SRB|381|Serbia">Serbia</option>
                            <option value="SC|SYC|248|Seychelles">Seychelles</option>
                            <option value="SL|SLE|232|Sierra Leone">Sierra Leone</option>
                            <option value="SG|SGP|065|Singapore">Singapore</option>
                            <option value="SK|SVK|421|Slovakia">Slovakia</option>
                            <option value="SI|SVN|386|Slovenia">Slovenia</option>
                            <option value="SB|SLB|677|Solomon Islands">Solomon Islands</option>
                            <option value="SO|SOM|252|Somalia">Somalia</option>
                            <option value="ZA|ZAF|027|South Africa">South Africa</option>
                            <option value="GS|SGS|000|South Georgia & South Sandwich Islands">South Georgia & South Sandwich Islands</option>
                            <option value="KR|KOR|082|South Korea">South Korea</option>
                            <option value="ES|ESP|034|Spain">Spain</option>
                            <option value="LK|LKA|094|Sri Lanka">Sri Lanka</option>
                            <option value="SD|SDN|249|Sudan">Sudan</option>
                            <option value="SR|SUR|597|Suriname">Suriname</option>
                            <option value="SJ|SJM|047|Svalbard and Jan Mayen">Svalbard and Jan Mayen</option>
                            <option value="SZ|SWZ|268|Swaziland">Swaziland</option>
                            <option value="SE|SWE|046|Sweden">Sweden</option>
                            <option value="CH|CHE|041|Switzerland">Switzerland</option>
                            <option value="SY|SYR|963|Syria">Syria</option>
                            <option value="TW|TWN|886|Taiwan">Taiwan</option>
                            <option value="TJ|TJK|992|Tajikistan">Tajikistan</option>
                            <option value="TZ|TZA|255|Tanzania">Tanzania</option>
                            <option value="TH|THA|066|Thailand">Thailand</option>
                            <option value="TL|TLS|670|Timor-Leste">Timor-Leste</option>
                            <option value="TG|TLS|228|Togo">Togo</option>
                            <option value="TK|TKL|690|Tokelau">Tokelau</option>
                            <option value="TO|TON|676|Tonga">Tonga</option>
                            <option value="TT|TTO|001|Trinidad and Tobago">Trinidad and Tobago</option>
                            <option value="TN|TUN|216|Tunisia">Tunisia</option>
                            <option value="TR|TUR|090|Turkey">Turkey</option>
                            <option value="TM|TKM|993|Turkmenistan">Turkmenistan</option>
                            <option value="TC|TCA|001|Turks and Caicos Islands">Turks and Caicos Islands</option>
                            <option value="TV|TUV|688|Tuvalu">Tuvalu</option>
                            <option value="UG|UGA|256|Uganda">Uganda</option>
                            <option value="UA|UKR|380|Ukraine">Ukraine</option>
                            <option value="AE|ARE|971|United Arab Emirates">United Arab Emirates</option>
                            <option value="GB|GBR|044|United Kingdom">United Kingdom</option>
                            <option value="US|USA|001|United States">United States</option>
                            <option value="VI|VIR|001|United States Virgin Islands">United States Virgin Islands</option>
                            <option value="UY|URY|598|Uruguay">Uruguay</option>
                            <option value="UZ|UZB|998|Uzbekistan">Uzbekistan</option>
                            <option value="VU|VUT|678|Vanuatu">Vanuatu</option>
                            <option value="VA|VAT|379|Vatican City">Vatican City</option>
                            <option value="VE|VEN|058|Venezuela">Venezuela</option>
                            <option value="VN|VNM|084|Vietnam">Vietnam</option>
                            <option value="WF|WLF|681|Wallis and Futuna">Wallis and Futuna</option>
                            <option value="EH|ESH|212|Western Sahara">Western Sahara</option>
                            <option value="YE|YEM|967|Yemen">Yemen</option>
                            <option value="ZM|ZMB|260|Zambia">Zambia</option>
                            <option value="ZW|ZWE|263|Zimbabwe">Zimbabwe</option>
                          </select>
                        </div>
                      </div>
                    </div>

                    <div class="form-group col-md-12">
                      <div class="col-sm-offset-4 col-md-4" style="margin-top:10px;">
                        <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                          &nbsp;&nbsp;<%=blockCountry_search%></button>
                      </div>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=blockCountry_blocked_country_list%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <%
                if(request.getAttribute("listOfCountry")!=null)
                {
                  List<BlacklistVO> cList = (List<BlacklistVO>)request.getAttribute("listOfCountry");
                  PaginationVO paginationVO = (PaginationVO)request.getAttribute("paginationVO");
                  paginationVO.setInputs("ctoken="+ctoken+paginationVO.getInputs());
                  if(cList.size()>0)
                  {
              %>
              <br>
             <%-- <form name="BlockCountry" action="/merchant/servlet/BlockCountry?ctoken=<%=ctoken%>" method="post">--%>
              <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr style="color: white;">
                  <th valign="middle" align="center" style="text-align: center"><%=blockCountry_Sr_No%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=blockCountry_Country_Name%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=blockCountry_AccountID%></th>
                </tr>
                </thead>
                <tbody>
                  <%
                  String style="class=td1";
                  String ext="light";
                  int pos = 1;
                  if(pos%2==0)
                  {
                    style="class=tr0";
                    ext="dark";
                  }
                  else
                  {
                    style="class=tr1";
                    ext="light";
                  }
                  for(BlacklistVO blacklistVO : cList)
                  {
                   int srno=pos+(pageno-1)*recordsPerPage;
                    out.println("<tr>");
                    out.println("<td align=center "+style+">&nbsp;"+srno+"</td>");
                    out.println("<td align=center "+style+">"+blacklistVO.getCountry()+"</td>");
                    out.println("<td align=center "+style+">"+blacklistVO.getAccountId()+"</td>");
                    out.println("</tr>");
                    pos++;
                  }
                  %>
              </table>
              <%--<table width="100%">
                <thead>
                <tr>
                  <td width="15%" align="center">
                    <input type="hidden" name="accountid" value="<%=accountid%>">
                    <input type="hidden" name="countryList" value="<%=country%>">
                    <input type="hidden" name="unblock" value="unblock"><input type="button" name="unblock" class="btn btn-default center-block" value="Unblock" onclick="return Unblock();">
                  </td>
                </tr>
                </thead>
              </table>
              </form>--%>
              <%
                int TotalPageNo;
                if(paginationVO.getTotalRecords()%paginationVO.getRecordsPerPage()!=0)
                {
                  TotalPageNo =paginationVO.getTotalRecords()/paginationVO.getRecordsPerPage()+1;
                }
                else
                {
                  TotalPageNo=paginationVO.getTotalRecords()/paginationVO.getRecordsPerPage();
                }
              %>
              <div id="showingid"><strong><%=blockCountry_page_no%> <%=paginationVO.getPageNo()%> <%=blockCountry_of%> <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
              <div id="showingid"><strong><%=blockCountry_total_no_of_records%>   <%=paginationVO.getTotalRecords()%> </strong></div>
              <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
                <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                <jsp:param name="orderby" value=""/>
              </jsp:include>
            </div>
          </div>
        </div>
      </div>
      <%
          }
          else
          {
            out.println(Functions.NewShowConfirmation1(blockCountry_sorry, blockCountry_no_records));
          }
        }
          else
          {
            out.println(Functions.NewShowConfirmation1(blockCountry_sorry, blockCountry_no_records));
          }
      %>
    </div>
  </div>
</div>
</body>
</html>
