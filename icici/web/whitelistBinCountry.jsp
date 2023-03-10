<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.WhitelistingDetailsVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Vivek
  Date: 3/9/2020
  Time: 1:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>1.Whitelist Module> Whitelist Bin Country</title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
  <script src="/partner/NewCss/am_multipleselection/bootstrap-multiselect.js"></script>
  <link href="/partner/NewCss/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet" />
  <style>
    .multiselect-container>li {
      padding: 0;
      margin-left: 31px;
    }
    .open>#multiselect-id.dropdown-menu {
      display: block;
      padding-top: 5px;
      padding-bottom: 5px;
    }
    .multiselect-container>li>a>label {
      margin: 0;
      height: 28px;
      padding-left:1px; !important;
      text-align: left;
    }
    span.multiselect-native-select {
      position: relative;
    }

    @supports (-ms-ime-align:auto) {
      span.multiselect-native-select {
        position: static!important;
      }
    }

    select[multiple], select[size] {
      height: auto;
      border-color: rgb(169, 169, 169);
    }
    .widget .btn-group {
      z-index: 1;
    }
    .btn-group, .btn-group-vertical {
      position: relative;
      vertical-align: middle;
      border-radius: 4px;
      width:100%;
      height: 59px;
      background-color: #fff;
    }
    #mainbtn-id.btn-default {
      color: #333;
      background-color: #fff;
      padding: 6px;
      border: 1px solid #b2b2b2;
      height: 33px;
    }
    .btn-group>.btn:first-child {
      margin-left: 0;
    }

    .btn-group>.btn:first-child {
      margin-left: 0;
    }

    .btn-group>.btn, .btn-group-vertical>.btn {
      position: relative;
      float: left;
    }
    .multiselect-container {
      position: absolute;
      list-style-type: none;
      margin: 0;
      padding: 0;
    }
    #multiselect-id.dropdown-menu {
      position: absolute;
      top: 100%;
      width: 100%;
      left: 0;
      z-index: 1000;
      display: none;
      float: left;
      min-width: 100%;
      max-height:110px;
      overflow-y:scroll;
      overflow-x:hidden;
      font-size: 14px;
      font-family: "Open Sans","Helvetica Neue",Helvetica,Arial,sans-serif;
      list-style: none;
      background-color: #fff;
      border: 1px solid #ccc;
      border: 1px solid rgba(0,0,0,0.15);
      border-radius: 4px;
      -webkit-box-shadow: 0 6px 12px rgba(0,0,0,0.175);
      box-shadow: 0 6px 12px rgba(0,0,0,0.175);
      background-clip: padding-box;
    }
    #mainbtn-id .multiselect-selected-text{
      font-size: 12px;
      font: inherit;
      padding-right: 18px;
    }
    #mainbtn-id.btn-default, #mainbtn-id.btn-default:focus, #mainbtn-id.btn-default.focus, #mainbtn-id.btn-default:active, #mainbtn-id.btn-default.active, .open>.dropdown-toggle#mainbtn-id.btn-default {
      color: #333;
      /*color: #fff;*/
      background-color: white!important;
      text-align: left;
      width: 100%;
    }
    .tr1 .multiselect-native-select .btn-group #mainbtn-id .multiselect-selected-text , .tr0 .multiselect-native-select .btn-group #mainbtn-id .multiselect-selected-text
    {
      font-size: 13px;
      font: inherit;
    }
    .tr1 .multiselect-native-select .btn-group #mainbtn-id , .tr0 .multiselect-native-select .btn-group #mainbtn-id
    {
      border: 1px solid #ddd; !important;
    }
    .btn .caret {
      float:right;!important;
      margin-top : 6px;
    }

    .multiselect-clear-filter{
      background: transparent;!important;
      border: none;!important;
      color: black;!important;
      font-size: 20px;!important;
      margin: 0;!important;
      padding: 5px;!important;
      box-shadow: none;!important;
    }
  </style>
</head>
<body>
<script>
  function checkCountrySelect()
  {
    var text=document.getElementById("country-select").value;
    if(text=="")
    {
      alert("Please select at least one country")
      return false;
    }
    return true;
  }
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
  function Delete()
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
    if (confirm("Do you really want to delete all selected Data."))
    {
      document.WhitelistBin.submit();
    }
  }
</script>
<%
  try
  {
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  String pgtypeid = request.getParameter("pgtypeid")==null?"":request.getParameter("pgtypeid");
  String memberId = request.getParameter("toid")==null?"":request.getParameter("toid");
  String accountId=request.getParameter("accountid")==null?"":request.getParameter("accountid");
  String country=request.getParameter("country")==null?"":request.getParameter("country");

  Functions functions=new Functions();
%>
<script type="text/javascript">
  $(document).ready(function() {
    var value=[];
    var details='<%=country%>';
    console.log("Details::::"+details);
    value=details.split(",");
    for(var i in value)
    {
      $("#country-select option[value='"+value[i]+"']").prop('selected', true);
    }
    $('#country-select').multiselect({
      enableFiltering: true,
      enableCaseInsensitiveFiltering:true,
      buttonTitle: function (options, select)
      {
        var text=$('#country-select').val();
        console.log("text--->"+text);
        if(text===null)
        {
          $('#country').val("");
          return "Select Country";
        }
        $('#country').val(text);
        return text;
      }
    });

  });
</script>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        WhiteList Bin Country
      </div>
      <br>
      <form action="/icici/servlet/WhitelistBinCountry?ctoken=<%=ctoken%>" name="frm1" method="post">
        <input type="hidden" id="ctoken" name="ctoken" value="<%=ctoken%>">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <%
                String errormsg1 = (String) request.getAttribute("error");
                if (errormsg1 != null)
                {
                  out.println("<center><font class=\"textb\"><b>"+errormsg1+"<br></b></font></center>");
                }
                if(request.getAttribute("msg")!=null)
                {
                  out.println("<center><font class=\"textb\"><b>"+request.getAttribute("msg")+"<br></b></font></center>");
                }
              %>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Gateway</td>
                  <td class="textb">:</td>
                  <td>
                    <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Account ID *</td>
                  <td class="textb">:</td>
                  <td>
                    <input name="accountid" id="accountid1" value="<%=accountId%>" class="txtbox" autocomplete="on">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">Member ID *</td>
                  <td width="5%" class="textb">:</td>
                  <td>
                    <input name="toid" id="memid" value="<%=memberId%>" class="txtbox" autocomplete="on">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">Country *</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb">
                    <select class="form-group" id="country-select" multiple="multiple" value="<%=country%>">
                      <option value="AFG">Afghanistan</option>
                      <option value="ALA">??land Islands</option>
                      <option value="ALB">Albania</option>
                      <option value="DZA">Algeria</option>
                      <option value="ASM">American Samoa</option>
                      <option value="AND">Andorra</option>
                      <option value="AGO">Angola</option>
                      <option value="AIA">Anguilla</option>
                      <option value="ATA">Antarctica</option>
                      <option value="ATG">Antigua and Barbuda</option>
                      <option value="ARG">Argentina</option>
                      <option value="ARM">Armenia</option>
                      <option value="ABW">Aruba</option>
                      <option value="AUS">Australia</option>
                      <option value="AUT">Austria</option>
                      <option value="AZE">Azerbaijan</option>
                      <option value="BHS">Bahamas</option>
                      <option value="BHR">Bahrain</option>
                      <option value="BGD">Bangladesh</option>
                      <option value="BRB">Barbados</option>
                      <option value="BLR">Belarus</option>
                      <option value="BEL">Belgium</option>
                      <option value="BLZ">Belize</option>
                      <option value="BEN">Benin</option>
                      <option value="BMU">Bermuda</option>
                      <option value="BTN">Bhutan</option>
                      <option value="BOL">Bolivia, Plurinational State of</option>
                      <option value="BES">Bonaire, Sint Eustatius and Saba</option>
                      <option value="BIH">Bosnia and Herzegovina</option>
                      <option value="BWA">Botswana</option>
                      <option value="BVT">Bouvet Island</option>
                      <option value="BRA">Brazil</option>
                      <option value="IOT">British Indian Ocean Territory</option>
                      <option value="BRN">Brunei Darussalam</option>
                      <option value="BGR">Bulgaria</option>
                      <option value="BFA">Burkina Faso</option>
                      <option value="BDI">Burundi</option>
                      <option value="KHM">Cambodia</option>
                      <option value="CMR">Cameroon</option>
                      <option value="CAN">Canada</option>
                      <option value="CPV">Cape Verde</option>
                      <option value="CYM">Cayman Islands</option>
                      <option value="CAF">Central African Republic</option>
                      <option value="TCD">Chad</option>
                      <option value="CHL">Chile</option>
                      <option value="CHN">China</option>
                      <option value="CXR">Christmas Island</option>
                      <option value="CCK">Cocos (Keeling) Islands</option>
                      <option value="COL">Colombia</option>
                      <option value="COM">Comoros</option>
                      <option value="COG">Congo</option>
                      <option value="COD">Congo, the Democratic Republic of the</option>
                      <option value="COK">Cook Islands</option>
                      <option value="CRI">Costa Rica</option>
                      <option value="CIV">C??te d'Ivoire</option>
                      <option value="HRV">Croatia</option>
                      <option value="CUB">Cuba</option>
                      <option value="CUW">Cura??ao</option>
                      <option value="CYP">Cyprus</option>
                      <option value="CZE">Czech Republic</option>
                      <option value="DNK">Denmark</option>
                      <option value="DJI">Djibouti</option>
                      <option value="DMA">Dominica</option>
                      <option value="DOM">Dominican Republic</option>
                      <option value="ECU">Ecuador</option>
                      <option value="EGY">Egypt</option>
                      <option value="SLV">El Salvador</option>
                      <option value="GNQ">Equatorial Guinea</option>
                      <option value="ERI">Eritrea</option>
                      <option value="EST">Estonia</option>
                      <option value="ETH">Ethiopia</option>
                      <option value="FLK">Falkland Islands (Malvinas)</option>
                      <option value="FRO">Faroe Islands</option>
                      <option value="FJI">Fiji</option>
                      <option value="FIN">Finland</option>
                      <option value="FRA">France</option>
                      <option value="GUF">French Guiana</option>
                      <option value="PYF">French Polynesia</option>
                      <option value="ATF">French Southern Territories</option>
                      <option value="GAB">Gabon</option>
                      <option value="GMB">Gambia</option>
                      <option value="GEO">Georgia</option>
                      <option value="DEU">Germany</option>
                      <option value="GHA">Ghana</option>
                      <option value="GIB">Gibraltar</option>
                      <option value="GRC">Greece</option>
                      <option value="GRL">Greenland</option>
                      <option value="GRD">Grenada</option>
                      <option value="GLP">Guadeloupe</option>
                      <option value="GUM">Guam</option>
                      <option value="GTM">Guatemala</option>
                      <option value="GGY">Guernsey</option>
                      <option value="GIN">Guinea</option>
                      <option value="GNB">Guinea-Bissau</option>
                      <option value="GUY">Guyana</option>
                      <option value="HTI">Haiti</option>
                      <option value="HMD">Heard Island and McDonald Islands</option>
                      <option value="VAT">Holy See (Vatican City State)</option>
                      <option value="HND">Honduras</option>
                      <option value="HKG">Hong Kong</option>
                      <option value="HUN">Hungary</option>
                      <option value="ISL">Iceland</option>
                      <option value="IND">India</option>
                      <option value="IDN">Indonesia</option>
                      <option value="IRN">Iran, Islamic Republic of</option>
                      <option value="IRQ">Iraq</option>
                      <option value="IRL">Ireland</option>
                      <option value="IMN">Isle of Man</option>
                      <option value="ISR">Israel</option>
                      <option value="ITA">Italy</option>
                      <option value="JAM">Jamaica</option>
                      <option value="JPN">Japan</option>
                      <option value="JEY">Jersey</option>
                      <option value="JOR">Jordan</option>
                      <option value="KAZ">Kazakhstan</option>
                      <option value="KEN">Kenya</option>
                      <option value="KIR">Kiribati</option>
                      <option value="PRK">Korea, Democratic People's Republic of</option>
                      <option value="KOR">Korea, Republic of</option>
                      <option value="KWT">Kuwait</option>
                      <option value="KGZ">Kyrgyzstan</option>
                      <option value="LAO">Lao People's Democratic Republic</option>
                      <option value="LVA">Latvia</option>
                      <option value="LBN">Lebanon</option>
                      <option value="LSO">Lesotho</option>
                      <option value="LBR">Liberia</option>
                      <option value="LBY">Libya</option>
                      <option value="LIE">Liechtenstein</option>
                      <option value="LTU">Lithuania</option>
                      <option value="LUX">Luxembourg</option>
                      <option value="MAC">Macao</option>
                      <option value="MKD">Macedonia, the former Yugoslav Republic of</option>
                      <option value="MDG">Madagascar</option>
                      <option value="MWI">Malawi</option>
                      <option value="MYS">Malaysia</option>
                      <option value="MDV">Maldives</option>
                      <option value="MLI">Mali</option>
                      <option value="MLT">Malta</option>
                      <option value="MHL">Marshall Islands</option>
                      <option value="MTQ">Martinique</option>
                      <option value="MRT">Mauritania</option>
                      <option value="MUS">Mauritius</option>
                      <option value="MYT">Mayotte</option>
                      <option value="MEX">Mexico</option>
                      <option value="FSM">Micronesia, Federated States of</option>
                      <option value="MDA">Moldova, Republic of</option>
                      <option value="MCO">Monaco</option>
                      <option value="MNG">Mongolia</option>
                      <option value="MNE">Montenegro</option>
                      <option value="MSR">Montserrat</option>
                      <option value="MAR">Morocco</option>
                      <option value="MOZ">Mozambique</option>
                      <option value="MMR">Myanmar</option>
                      <option value="NAM">Namibia</option>
                      <option value="NRU">Nauru</option>
                      <option value="NPL">Nepal</option>
                      <option value="NLD">Netherlands</option>
                      <option value="NCL">New Caledonia</option>
                      <option value="NZL">New Zealand</option>
                      <option value="NIC">Nicaragua</option>
                      <option value="NER">Niger</option>
                      <option value="NGA">Nigeria</option>
                      <option value="NIU">Niue</option>
                      <option value="NFK">Norfolk Island</option>
                      <option value="MNP">Northern Mariana Islands</option>
                      <option value="NOR">Norway</option>
                      <option value="OMN">Oman</option>
                      <option value="PAK">Pakistan</option>
                      <option value="PLW">Palau</option>
                      <option value="PSE">Palestinian Territory, Occupied</option>
                      <option value="PAN">Panama</option>
                      <option value="PNG">Papua New Guinea</option>
                      <option value="PRY">Paraguay</option>
                      <option value="PER">Peru</option>
                      <option value="PHL">Philippines</option>
                      <option value="PCN">Pitcairn</option>
                      <option value="POL">Poland</option>
                      <option value="PRT">Portugal</option>
                      <option value="PRI">Puerto Rico</option>
                      <option value="QAT">Qatar</option>
                      <option value="REU">R??union</option>
                      <option value="ROU">Romania</option>
                      <option value="RUS">Russian Federation</option>
                      <option value="RWA">Rwanda</option>
                      <option value="BLM">Saint Barth??lemy</option>
                      <option value="SHN">Saint Helena, Ascension and Tristan da Cunha</option>
                      <option value="KNA">Saint Kitts and Nevis</option>
                      <option value="LCA">Saint Lucia</option>
                      <option value="MAF">Saint Martin (French part)</option>
                      <option value="SPM">Saint Pierre and Miquelon</option>
                      <option value="VCT">Saint Vincent and the Grenadines</option>
                      <option value="WSM">Samoa</option>
                      <option value="SMR">San Marino</option>
                      <option value="STP">Sao Tome and Principe</option>
                      <option value="SAU">Saudi Arabia</option>
                      <option value="SEN">Senegal</option>
                      <option value="SRB">Serbia</option>
                      <option value="SYC">Seychelles</option>
                      <option value="SLE">Sierra Leone</option>
                      <option value="SGP">Singapore</option>
                      <option value="SXM">Sint Maarten (Dutch part)</option>
                      <option value="SVK">Slovakia</option>
                      <option value="SVN">Slovenia</option>
                      <option value="SLB">Solomon Islands</option>
                      <option value="SOM">Somalia</option>
                      <option value="ZAF">South Africa</option>
                      <option value="SGS">South Georgia and the South Sandwich Islands</option>
                      <option value="SSD">South Sudan</option>
                      <option value="ESP">Spain</option>
                      <option value="LKA">Sri Lanka</option>
                      <option value="SDN">Sudan</option>
                      <option value="SUR">Suriname</option>
                      <option value="SJM">Svalbard and Jan Mayen</option>
                      <option value="SWZ">Swaziland</option>
                      <option value="SWE">Sweden</option>
                      <option value="CHE">Switzerland</option>
                      <option value="SYR">Syrian Arab Republic</option>
                      <option value="TWN">Taiwan, Province of China</option>
                      <option value="TJK">Tajikistan</option>
                      <option value="TZA">Tanzania, United Republic of</option>
                      <option value="THA">Thailand</option>
                      <option value="TLS">Timor-Leste</option>
                      <option value="TGO">Togo</option>
                      <option value="TKL">Tokelau</option>
                      <option value="TON">Tonga</option>
                      <option value="TTO">Trinidad and Tobago</option>
                      <option value="TUN">Tunisia</option>
                      <option value="TUR">Turkey</option>
                      <option value="TKM">Turkmenistan</option>
                      <option value="TCA">Turks and Caicos Islands</option>
                      <option value="TUV">Tuvalu</option>
                      <option value="UGA">Uganda</option>
                      <option value="UKR">Ukraine</option>
                      <option value="ARE">United Arab Emirates</option>
                      <option value="GBR">United Kingdom</option>
                      <option value="USA">United States</option>
                      <option value="UMI">United States Minor Outlying Islands</option>
                      <option value="URY">Uruguay</option>
                      <option value="UZB">Uzbekistan</option>
                      <option value="VUT">Vanuatu</option>
                      <option value="VEN">Venezuela, Bolivarian Republic of</option>
                      <option value="VNM">Viet Nam</option>
                      <option value="VGB">Virgin Islands, British</option>
                      <option value="VIR">Virgin Islands, U.S.</option>
                      <option value="WLF">Wallis and Futuna</option>
                      <option value="ESH">Western Sahara</option>
                      <option value="YEM">Yemen</option>
                      <option value="ZMB">Zambia</option>
                      <option value="ZWE">Zimbabwe</option>
                    </select>
                    <input type="hidden" id="country"  name="country" value="">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td>&nbsp;&nbsp;</td>
                  <td width="2%" class="textb">
                    <input type="hidden" name="bbtn" />
                    <input type="button" name="bt" value="WhiteList" onclick="{document.frm1.bbtn.value=this.value;document.frm1.submit();}" class="buttonform">
                  </td>
                  <td>&nbsp;&nbsp;</td>
                  <td>
                    <input type="hidden" name="sbtn" />
                    <input type="button" name="bt" value="Search" onclick="{document.frm1.sbtn.value=this.value;document.frm1.submit();}" class="buttonform">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                </tbody>
              </table>
            </td>
          </tr>
          </tbody>
        </table>
      </form>
    </div>
  </div>
</div>
<div class="reporttable">
  <%
    String currentblock=request.getParameter("currentblock");

    if(currentblock==null)
      currentblock="1";
    if(request.getAttribute("listofBinCountry")!=null)
    {
      List<WhitelistingDetailsVO> iList = (List<WhitelistingDetailsVO>)request.getAttribute("listofBinCountry");
      PaginationVO paginationVO=(PaginationVO) request.getAttribute("paginationVO");
      paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);

      if(iList.size()>0)
      {
  %>
  <form name="WhitelistBin" action="/icici/servlet/WhitelistBinCountry?ctoken=<%=ctoken%>" method="post">
    <table align=center width="100%" class="table table-striped table-bordered table-hover table-green dataTable">
      <thead>
      <tr>
        <td valign="middle" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
        <td  align="center" class="th0">Member ID</td>
        <td  align="center" class="th0">Account ID</td>
        <td  align="center" class="th0">Country</td>
        <td  align="center" class="th0">Action Executor Id</td>
        <td  align="center" class="th0">Action Executor Name</td>
      </tr>
      </thead>
      <%
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        String style="class=td1";
        String ext="light";
        int pos = 1;
        for(WhitelistingDetailsVO whitelistingDetailsVO : iList)
        {
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

          if(!functions.isValueNull(whitelistingDetailsVO.getActionExecutorId()))
          {
            actionExecutorId="-";

          }
          else
          {
            actionExecutorId=whitelistingDetailsVO.getActionExecutorId();
          }

          if(!functions.isValueNull(whitelistingDetailsVO.getActionExecutorName()))
          {
            actionExecutorName="-";
          }
          else{

            actionExecutorName=whitelistingDetailsVO.getActionExecutorName();

          }

          out.println("<tr>");
          out.println("<td " + style + " align=\"center\">&nbsp;<input type=\"checkbox\" name=\"id\" value=\"" + whitelistingDetailsVO.getId()+ "\"></td>");
          out.println("<td align=center "+style+">&nbsp;" + ESAPI.encoder().encodeForHTML(whitelistingDetailsVO.getMemberid())+"</td>");
          out.println("<td align=center "+style+">&nbsp;" + ESAPI.encoder().encodeForHTML(String.valueOf(whitelistingDetailsVO.getAccountid()))+"</td>");
          out.println("<td align=center "+style+">&nbsp;" + ESAPI.encoder().encodeForHTML(whitelistingDetailsVO.getCountry())+"</td>");
          out.println("<td align=center "+style+">&nbsp;" + actionExecutorId+"</td>");
          out.println("<td align=center "+style+">&nbsp;" + actionExecutorName+"</td>");
          out.println("</tr>");
          pos++;
        }
      %>
    </table>
    <table width="100%">
      <thead>
      <tr>
        <td width="15%" align="center">
          <input type="hidden" name="toid" value="<%=memberId%>">
          <input type="hidden" name="accountid" value="<%=accountId%>">
          <input type="hidden" name="delete" value="delete"><input type="button" name="delete" class="addnewmember" value="Delete" onclick="return Delete();">
        </td>
      </tr>
      </thead>
    </table>
  </form>
  <table align=center valign=top><tr>
    <td align=center>
      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
        <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
        <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
        <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
        <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
        <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </tr>
  </table>
</div>
</div>
<%
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
    }
  }
  else
  {
    out.println(Functions.NewShowConfirmation("Filter", "Please provide the data to get Whitelist Bin Country List"));
  }
%>
</body>
<%
  }
  else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
  }
  catch (Exception e)
  {
  e.printStackTrace();
  }
%>
</html>
