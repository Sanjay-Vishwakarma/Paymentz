<%--
  Created by IntelliJ IDEA.
  User: Vivek
  Date: 3/5/2020
  Time: 1:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.directi.pg.Functions,com.directi.pg.LoadProperties" %>
<%@ include file="routingTab.jsp"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ResourceBundle" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  PartnerFunctions partnerFunctions=new PartnerFunctions();
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String binCountryRouting_Bin_Country = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_Bin_Country")) ? rb1.getString("binCountryRouting_Bin_Country") : "Bin Country Routing Settings";
  String binCountryRouting_PartnerID = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_PartnerID")) ? rb1.getString("binCountryRouting_PartnerID") : "Partner ID";
  String binCountryRouting_MerchantID = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_MerchantID")) ? rb1.getString("binCountryRouting_MerchantID") : "Merchant ID*";
  String binCountryRouting_AccountID = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_AccountID")) ? rb1.getString("binCountryRouting_AccountID") : "Account ID*";
  String binCountryRouting_Country = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_Country")) ? rb1.getString("binCountryRouting_Country") : "Country*";
  String binCountryRouting_Search = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_Search")) ? rb1.getString("binCountryRouting_Search") : "Search";
  String binCountryRouting_Upload = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_Upload")) ? rb1.getString("binCountryRouting_Upload") : "Upload";
  String binCountryRouting_Report_Table = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_Report_Table")) ? rb1.getString("binCountryRouting_Report_Table") : "Report Table";
  String binCountryRouting_Member_Id = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_Member_Id")) ? rb1.getString("binCountryRouting_Member_Id") : "Member Id";
  String binCountryRouting_account_Id = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_account_Id")) ? rb1.getString("binCountryRouting_account_Id") : "Account Id";
  String binCountryRouting_Country1 = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_Country1")) ? rb1.getString("binCountryRouting_Country1") : "Country";
  String binCountryRouting_Action_Executor = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_Action_Executor")) ? rb1.getString("binCountryRouting_Action_Executor") : "Action Executor Id";
  String binCountryRouting_Action_name = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_Action_name")) ? rb1.getString("binCountryRouting_Action_name") : "Action Executor Name";
  String binCountryRouting_Showing_Page = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_Showing_Page")) ? rb1.getString("binCountryRouting_Showing_Page") : "Showing Page";
  String binCountryRouting_Showing_of = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_Showing_of")) ? rb1.getString("binCountryRouting_Showing_of") : "of";
  String binCountryRouting_records = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_records")) ? rb1.getString("binCountryRouting_records") : "records";
  String binCountryRouting_Sorry = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_Sorry")) ? rb1.getString("binCountryRouting_Sorry") : "Sorry";
  String binCountryRouting_no = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_no")) ? rb1.getString("binCountryRouting_no") : "No records found.";
  String binCountryRouting_page_no = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_page_no")) ? rb1.getString("binCountryRouting_page_no") : "Page number";
  String binCountryRouting_total_no_of_records = StringUtils.isNotEmpty(rb1.getString("binCountryRouting_total_no_of_records")) ? rb1.getString("binCountryRouting_total_no_of_records") : "Total number of records";

%>
<%!
  private static Logger log=new Logger("binRouting.jsp");
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title><%=company%> | Merchant Bin Routing </title>

  <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <script src="/partner/NewCss/am_multipleselection/bootstrap-multiselect.js"></script>
  <link href="/partner/NewCss/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet" />

  <style type="text/css">
    .center-block{
      display: inline-block;
      margin-right: auto;
      margin-left: auto;
    }
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
      height: 30px;
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
      max-height:160px;
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
      overflow: hidden;
      text-overflow: ellipsis;
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
    function DoReverse(ctoken)
    {
      if (confirm("Do you really want to Delete this ?"))
      {
        return true;
      }
      else
        return false;
    }
    function ToggleAll(checkbox)
    {
      console.log("inside ToggleAll")
      flag = checkbox.checked;
      var checkboxes = document.getElementsByName("id");
      var total_boxes = checkboxes.length;
      console.log("total_boxes--->"+total_boxes);
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

      for(i=0; i<total_boxes; i++ ){
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
      if (confirm("Do you really want to Delete all selected Data."))
      {
        document.BinDelete.submit();
      }
    }
  </script>
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

</head>
<body class="bodybackground">
<div class="content-page">
  <div class="content" style="padding:0px 20px ;margin: 0px;">
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=binCountryRouting_Bin_Country%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
              session.setAttribute("submit","binrouting");
              String str = "";
              if (partner.isLoggedInPartner(session))
              {
                String role=(String)session.getAttribute("role");
                String username=(String)session.getAttribute("username");
                String actionExecutorId=(String)session.getAttribute("merchantid");
                String actionExecutorName=role+"-"+username;
                String memberid=nullToStr(request.getParameter("memberid"));
                String pid=nullToStr(request.getParameter("pid"));
                String Config =null;
                String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
                if(Roles.contains("superpartner")){

                }else{
                  pid = String.valueOf(session.getAttribute("merchantid"));
                  Config = "disabled";
                }
                String accountid=nullToStr(request.getParameter("accountid"));
                String country=nullToStr(request.getParameter("country"));
                String partnerid = session.getAttribute("partnerId").toString();

                str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                if(memberid!=null)str = str + "&memberid=" + memberid;
                if(pid!=null)str = str + "&pid=" + pid;
                if(accountid!=null)str = str + "&accountid=" + accountid;
                if(country!=null)str = str + "&country=" + country;
                else
                  memberid="";
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
            <div id="">
              <form action="/partner/net/BinCountryRouting?ctoken=<%=ctoken%>" method="post" name="F1" onsubmit="" class="form-horizontal" style="text-align: center">
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                <%
                  int pageno=partnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                  int pagerecords=partnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
                  String errormsg1 = (String) request.getAttribute("error");
                  String success = (String) request.getAttribute("success");
                  if (partnerFunctions.isValueNull(errormsg1))
                  {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                  }
                  if(partnerFunctions.isValueNull(success))
                  {
                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+success+"</h5>");
                  }
                %>
                <div class="form-group col-md-1">
                </div>
                <div class="form-group col-md-12">
                  <div class="ui-widget">
                    <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                      <label for="pid" ><%=binCountryRouting_PartnerID%></label>
                    </div>
                    <div class="col-md-3" >
                      <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                      <input name="pid" type="hidden" value="<%=pid%>">
                    </div>
                  </div>
                </div>

                <div class="form-group col-md-12">
                  <div class="ui-widget">
                    <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                      <label for="member" ><%=binCountryRouting_MerchantID%></label>
                    </div>
                    <div class="col-md-3" >
                      <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                    </div>
                  </div>
                </div>

                <div class="form-group col-md-12">
                  <div class="ui-widget">
                    <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                      <label><%=binCountryRouting_AccountID%></label>
                    </div>
                    <div class="col-md-3" >
                      <input name="accountid" id="account_id" value="<%=accountid%>"  class="form-control">
                    </div>
                  </div>
                </div>

                <div class="form-group col-md-12">
                  <div>
                    <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                      <label><%=binCountryRouting_Country%></label>
                    </div>
                    <div class="col-md-3" >
                      <select class="form-group" id="country-select" multiple="multiple" value="<%=country%>">
                        <option value="AFG">Afghanistan</option>
                        <option value="ALA">Åland Islands</option>
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
                        <option value="CIV">Côte d'Ivoire</option>
                        <option value="HRV">Croatia</option>
                        <option value="CUB">Cuba</option>
                        <option value="CUW">Curaçao</option>
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
                        <option value="REU">Réunion</option>
                        <option value="ROU">Romania</option>
                        <option value="RUS">Russian Federation</option>
                        <option value="RWA">Rwanda</option>
                        <option value="BLM">Saint Barthélemy</option>
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
                    </div>
                  </div>
                </div>

                <div class="form-group col-md-12">
                  <div class="col-md-6 text-right center-block">
                    <button type="submit" class="btn btn-default center-block"><i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;<%=binCountryRouting_Search%></button>
                  </div>
                  <div class="col-md-6 text-left center-block">
                    <button type="submit" name="upload" value="upload" class="btn btn-default center-block" onclick="return checkCountrySelect()"><i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;<%=binCountryRouting_Upload%></button>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>

      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=binCountryRouting_Report_Table%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-y: auto;">
              <%

                StringBuffer requestParameter = new StringBuffer();
                /*Enumeration<String> stringEnumeration = request.getParameterNames();
                while(stringEnumeration.hasMoreElements())
                {
                  String name=stringEnumeration.nextElement();
                  if("SPageno".equals(name) || "SRecords".equals(name))
                  {

                  }
                  else
                    requestParameter.append("<input type='hidden' name='"+name+"' value=\""+request.getParameter(name)+"\"/>");
                }*/
                requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");
                HashMap hash = (HashMap)request.getAttribute("whitelistCountryDetails");

                HashMap temphash=null;
                int records=0;
                int totalrecords=0;
                String currentblock=request.getParameter("currentblock");
                if(currentblock==null)
                  currentblock="1";
                try
                {
                  records=Integer.parseInt((String)hash.get("records"));
                  totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
                }
                catch(Exception ex)
                {
                  log.debug("Exception::");
                }
                if(hash!=null)
                {
                  hash = (HashMap)request.getAttribute("whitelistCountryDetails");
                }
                if(records>0)
                {
              %>
              <form action="/partner/net/DeleteBinCountry?ctoken=<%=ctoken%>" method="POST" name="BinDelete">
                <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead>
                  <tr>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=binCountryRouting_Member_Id%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=binCountryRouting_account_Id%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=binCountryRouting_Country1%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=binCountryRouting_Action_Executor%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=binCountryRouting_Action_name%></b></td>
                  </tr>
                  </thead>
                  <%

                    Functions functions = new Functions();
                    String style="class=td1";
                    String ext="light";
                    for(int pos=1;pos<=records;pos++)
                    {
                      String id=Integer.toString(pos);
                      int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);
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
                      temphash=(HashMap)hash.get(id);
                      String countryRes="";

                      if(functions.isValueNull((String)temphash.get("country")))
                        countryRes=(String)temphash.get("country");
                      if(functions.isValueNull((String)temphash.get("actionExecutorId")))
                      {
                        actionExecutorId=(String)temphash.get("actionExecutorId");
                      }
                      else{actionExecutorId="-";}
                      if(functions.isValueNull((String)temphash.get("actionExecutorName")))
                      {
                        actionExecutorName=(String)temphash.get("actionExecutorName");
                      }
                      else {actionExecutorName="-";}



                      out.println("<tr id=\"maindata\">");
                      out.println("<td valign=\"middle\" data-label=\"id\" align=\"center\" "+style+">&nbsp;<input type=\"checkbox\" name=\"id\" value="+ESAPI.encoder().encodeForHTML((String)temphash.get("id"))+"></td>");
                      out.println("<td valign=\"middle\" data-label=\"Member Id\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("memberid"))+"</td>");
                      out.println("<td valign=\"middle\" data-label=\"Account Id\" align=\"center\"" + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("accountid")) + "</td>");
                      out.println("<td valign=\"middle\" data-label=\"Start Bin\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(countryRes)+ "</td>");
                      out.println("<td valign=\"middle\" data-label=\"Action Executor Id\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorId)+ "</td>");
                      out.println("<td valign=\"middle\" data-label=\"Action Executor Name\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorName)+ "</td>");
                      out.println(requestParameter.toString());
                      out.println("</form></td>");
                      out.println("</tr>");
                    }
                  %>
                </table>
                <table width="100%">
                  <thead>
                  <tr>
                    <td width="15%" align="center">
                      <input type="hidden" name="memberid" value="<%=memberid%>">
                      <input type="hidden" name="accountid" value="<%=accountid%>">
                      <input type="hidden" name="startBin" value="<%=country%>">
                      <input type="hidden" name="delete" value="delete"><input type="button" name="delete" class="btn btn-default gotoauto" value="Delete" onclick="return Delete();">
                    </td>
                  </tr>
                  </thead>
                </table>
              </form>
            </div>
          </div>
        </div>
      </div>

      <%
        int TotalPageNo;
        if(totalrecords%pagerecords!=0)
        {
          TotalPageNo=totalrecords/pagerecords+1;
        }
        else
        {
          TotalPageNo=totalrecords/pagerecords;
        }
      %>
      <div id="showingid"><strong><%=binCountryRouting_page_no%> <%=pageno%> <%=binCountryRouting_Showing_of%> <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
      <div id="showingid"><strong><%=binCountryRouting_total_no_of_records%>   <%=totalrecords%> </strong></div>


      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="BinCountryRouting"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
        <%
            }
            else
            {
              out.println(Functions.NewShowConfirmation1(binCountryRouting_Sorry, binCountryRouting_no));
            }
        %>
      </div>
    </div>
      </div>
    <%
      }
      else
      {
      response.sendRedirect("/partner/logout.jsp");
      return;
      }
    %>
    </div>
  </div>

</body>
</html>
<%!
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>