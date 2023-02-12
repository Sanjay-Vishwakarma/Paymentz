<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.*" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.directi.pg.*" %>
<%@ page import="com.manager.enums.TransReqRejectCheck" %>
<%@ include file="top.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: Rihen
  Date: april 23, 2019
  Time: 4:31:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%!
  TransactionLogger transactionLogger = new TransactionLogger("emiConfiguration.jsp");
  Functions functions=new Functions();

%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","emiConfiguration");
  String partnerId=(String)session.getAttribute("merchantid");

  String startTime = Functions.checkStringNull(request.getParameter("starttime"));
  String endTime = Functions.checkStringNull(request.getParameter("endtime"));
  if (startTime == null) startTime = "00:00:00";
  if (endTime == null) endTime = "23:59:59";

  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);

  String emiConfiguration_EMI_Configuration = rb1.getString("emiConfiguration_EMI_Configuration");
  String emiConfiguration_Merchant_ID = rb1.getString("emiConfiguration_Merchant_ID");
  String emiConfiguration_Terminal_ID = rb1.getString("emiConfiguration_Terminal_ID");
  String emiConfiguration_Search = rb1.getString("emiConfiguration_Search");
  String emiConfiguration_Add_New_EMI = rb1.getString("emiConfiguration_Add_New_EMI");
  String emiConfiguration_From = rb1.getString("emiConfiguration_From");
  String emiConfiguration_To = rb1.getString("emiConfiguration_To");
  String emiConfiguration_EMI_Period = rb1.getString("emiConfiguration_EMI_Period");
  String emiConfiguration_Select_EMI_Period = rb1.getString("emiConfiguration_Select_EMI_Period");
  String emiConfiguration_Save = rb1.getString("emiConfiguration_Save");
  String emiConfiguration_Active = rb1.getString("emiConfiguration_Active");

%>
<html>
<head>
  <title><%=company%>  EMI Configuration</title>

  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
  <link href="/merchant/NewCss/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet" />
  <link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
  <link href="/partner/cookies/cookies_popup.css" rel="stylesheet">

  <style type="text/css">

    #myTable th{text-align: center;}

    #myTable td{
      font-family: Open Sans;
      font-size: 13px;
      font-weight: 600;
    }

    #myTable .button3 {
      text-indent: 0!important;
      width: 100%;
      height: inherit!important;
      display: block;
      color: #000000;
      padding: 0px;
      background: transparent!important;
      border: 0px solid #dedede;
      text-align: center!important;
      font-family: "Open Sans","Helvetica Neue",Helvetica,Arial,sans-serif;
      font-size: 12px;
    }


    #checkboxes {
      display: none;
      border: 1px #dadada solid;
      position: absolute;
      width: 100%;
      background-color: #ffffff;
      z-index: 1;
      height: 130px;
      overflow-x: auto;
    }

    #checkboxes label {
      display: block;
    }

    #checkboxes label:hover {
      background-color: #1e90ff;
    }

    input[type="checkbox"]{
      width: 18px; /*Desired width*/
      height: 18px; /*Desired height*/
    }
    .icheckbox_square-aero{
      margin: 3px 5px;
    }

    /********************************************************************************/

    .multiselect-container>li {
      padding: 0;
      margin-left: 31px;
    }
    .open>#multiselect-id.dropdown-menu {
      display: block;
      overflow-y: scroll;
      height: 470%;
      width: 100%;
    }
    .multiselect-container>li>a>label {
      margin: 0;
      height: 24px;
      font-weight: 400;
      padding: 3px 20px 3px 40px;
    }
    span.multiselect-native-select {
      position: relative;
    }

    @supports (-ms-ime-align:auto) {
      span.multiselect-native-select {
        position: static!important;
      }
    }

    span.multiselect-native-select select {
      border: 0!important;
      clip: rect(0 0 0 0)!important;
      height: 1px!important;
      margin: -1px -1px -1px -3px!important;
      overflow: hidden!important;
      padding: 0!important;
      position: absolute!important;
      width: 1px!important;
      left: 50%;
      top: 30px;
    }
    select[multiple], select[size] {
      height: auto;
    }
    .widget .btn-group {
      z-index: 1;
    }

    .btn-group, .btn-group-vertical {
      position: relative;
      display: flex;
      vertical-align: middle;

    }
    .btn {

      display: inline-block;
      padding: 6px 12px;
      margin-bottom: 0;
      font-size: 14px;
      font-weight: normal;
      line-height: 1.428571429;
      text-align: center;
      white-space: nowrap;
      vertical-align: middle;
      cursor: pointer;
      background-image: none;
      border: 1px solid transparent;
      -webkit-user-select: none;
      -moz-user-select: none;
      -ms-user-select: none;
      -o-user-select: none;
      user-select: none;
    }
    #mainbtn-id.btn-default {
      color: #333;
      background-color: #fff;
      border-color: #ccc;
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
      left: 0;
      z-index: 1000;
      display: none;
      float: left;
      min-width: 160px;
      padding: 5px 0;
      margin: 2px 0 0;
      font-size: 14px;
      list-style: none;
      background-color: #fff;
      border: 1px solid #ccc;
      border: 1px solid rgba(0,0,0,0.15);
      border-radius: 4px;
      -webkit-box-shadow: 0 6px 12px rgba(0,0,0,0.175);
      box-shadow: 0 6px 12px rgba(0,0,0,0.175);
      background-clip: padding-box;
    }
    #mainbtn-id.btn-default, #mainbtn-id.btn-default:focus, #mainbtn-id.btn-default.focus, #mainbtn-id.btn-default:active, #mainbtn-id.btn-default.active, .open>.dropdown-toggle#mainbtn-id.btn-default {
      color: #333;
      /*color: #fff;*/
      background-color: white!important;
      border-color: #ddd!important;
      text-align: left;
      width: 100%;
    }
    .caret {
      display: inline-block;
      width: 0;
      height: 0;
      margin-left: 2px;
      vertical-align: middle;
      border-top: 4px solid;
      border-right: 4px solid transparent;
      border-left: 4px solid transparent;
      float: right;
      margin-top: 8px;
    }

    #mainbtn-id
    {
      overflow: hidden;
      display: block;
    }

    .emi-date{
      cursor: auto;
      background-color: #ffffff;
      opacity: 1;
      width: 100%;
      border-radius: 0;
      padding: 0px;
      text-align: center;
    }

    .emi-time{
      cursor: auto;
      background-color: #ffffff;
      opacity: 1;
      padding: 0px;
      text-align: center;
    }

    /********************************************************************************/

  </style>

</head>

<body class="bodybackground">

<%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
<script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
<script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
<script src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
<script src="/merchant/NewCss/am_multipleselection/bootstrap-multiselect.js"></script>

<script>
  $(function () {
    $('#emiPeriod').multiselect({
      buttonText: function(options, select) {
        if (options.length === 0) {
          return '<%=emiConfiguration_Select_EMI_Period%>';
        }
        else {
          var labels = [];
          console.log(options);
          options.each(function() {
            labels.push($(this).val());
          });
          document.getElementById('emicode').value = labels;
          return labels.join(', ') + '';
        }
      }
    });
  });

</script>
<script>
  var expanded = false;
  function showCheckboxes() {
    var checkboxes = document.getElementById("checkboxes");
    if (!expanded) {
      checkboxes.style.display = "block";
      expanded = true;
    } else {
      checkboxes.style.display = "none";
      expanded = false;
    }
  }

  function hideCheckboxes(){
    console.log("in hideCheckboxes")
    var checkboxes = document.getElementById("checkboxes");
    checkboxes.style.display = "none";
    expanded = false;
  }

</script>
<script language="javascript">
  function isint(form)
  {
    if (isNaN(form.numrows.value))
      return false;
    else
      return true;
  }
</script>
<script type="text/javascript">
  $('#sandbox-container input').datepicker({
  });
</script>
<script>
  $(function() {
    $(".datepicker").datepicker({dateFormat: "yy-mm-dd",endDate:'+10y'});
  });
</script>
<script>
  function showEMI(){
    console.log("MID", document.getElementById("mid").value);
    console.log("TID", document.getElementById("tid").value);

    var mid = document.getElementById("mid").value;
    var tid = document.getElementById("tid").value;
    if(mid && tid)
    {
      document.getElementById("emiTable").classList.remove("hide");
    }
    else
    {
      document.getElementById("emiTable").classList.add("hide");
      alert("Please select Member Id and Terminal Id");
    }
  }
</script>
<%
  session.setAttribute("submit","emiConfiguration");
  String memberid=Functions.checkStringNull(request.getParameter("memberid"))==null?"":request.getParameter("memberid");
  String pid=Functions.checkStringNull(request.getParameter("pid"))==null?"":request.getParameter("pid");
  String terminalid = Functions.checkStringNull(request.getParameter("terminal"))==null?"":request.getParameter("terminal");

%>

<div class="content-page">
  <div class="content">
    <div class="page-heading">

      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=emiConfiguration_EMI_Configuration%></strong></h2>
            </div>
            <%
              String Config =null;
              String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
              if(Roles.contains("superpartner")){

              }else{
                pid = String.valueOf(session.getAttribute("merchantid"));
                Config = "disabled";
              }
              String errMessage = (String) request.getAttribute("error");
              transactionLogger.error("Message ---- "+errMessage);
              if (functions.isValueNull(errMessage))
              {
                out.println("<h5 class=\"bg-info\" style=\"text-align: center;margin:15px\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + errMessage + "</h5>");
              }
            %>
            <form name="form" method="post" action="/partner/net/EmiConfiguration?ctoken=<%=ctoken%>" >
              <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
              <input type="hidden" value="<%=partnerId%>" name="partnerid" id="partnerid">
              <div class="widget-content padding" style="display:inline-block !important;">
                <div class="form-group col-xs-12 col-sm-12 col-md-5 col-lg-5 has-feedback">
                  <div class="ui-widget">
                    <label for="pid">Partner ID</label>
                    <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                    <input type ="hidden" name="pid" value="<%=pid%>">
                  </div>
                </div>
                <div class="form-group col-xs-12 col-sm-12 col-md-5 col-lg-5 has-feedback">
                  <div class="ui-widget">
                    <label for="member"><%=emiConfiguration_Merchant_ID%>*</label>
                    <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                  </div>
                </div>
                <div class="form-group col-xs-12 col-sm-12 col-md-5 col-lg-5 has-feedback">
                  <div class="ui-widget">
                    <label for="terminal"><%=emiConfiguration_Terminal_ID%>*</label>
                    <input name="terminal" id="terminal" value="<%=terminalid%>" class="form-control" autocomplete="on">
                  </div>
                </div>
                <div class="form-group col-xs-12 col-sm-12 col-md-2 col-lg-2">
                  <label style="color: transparent;"><%=emiConfiguration_Search%></label>
                  <button type="submit" class="btn btn-default" style="display:block;">
                    <i class="fa fa-clock-o"></i>
                    &nbsp;&nbsp;<%=emiConfiguration_Search%>
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>


      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp; <%=emiConfiguration_Add_New_EMI%></strong></h2>
            </div>
            <form name="form" method="post" action="/partner/net/EmiConfiguration?ctoken=<%=ctoken%>" >
              <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
              <input type="hidden" value="<%=partnerId%>" name="partnerid" id="partnerid">
              <div id="rows">
                <%
                  String message = (String) request.getAttribute("sErrorMessage");
                  transactionLogger.error("Message ---- "+message);
                  if (functions.isValueNull(message))
                  {
                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;margin:15px\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                  }
                %>
                <div class="widget-content padding" style="display:inline-block !important;">
                  <div id="horizontal-form1">
                    <input type ="hidden" name="pid" value="<%=pid%>">
                    <div class="form-group col-xs-12 col-sm-6 col-md-4 col-lg-2 has-feedback">
                    <div class="ui-widget">
                        <label for="member"><%=emiConfiguration_Merchant_ID%></label>
                        <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on" readonly>
                      </div>
                    </div>
                    <div class="form-group col-xs-12 col-sm-6 col-md-4 col-lg-2 has-feedback">
                      <div class="ui-widget">
                        <label for="tid"><%=emiConfiguration_Terminal_ID%></label>
                        <input name="terminal" id="terminal" value="<%=terminalid%>" class="form-control" autocomplete="on" readonly>
                      </div>
                    </div>

                    <%

                      try
                      {
                        transactionLogger.error("request.getAttribute(\"emiVOList\")-------------->"+request.getAttribute("emiVOList"));
                        List<EmiVO> emiVOList = (List<EmiVO>)request.getAttribute("emiVOList");
                        int i=0;
                        if(emiVOList!=null && emiVOList.size()>0)
                        {
                          for(EmiVO emiVO:emiVOList)
                          {
                            String isChecked = "";
                            i++;
                            if(emiVO.getIsActive().equalsIgnoreCase("Y"))
                              isChecked = "checked";
                    %>

                    <div class="form-group col-xs-12 col-sm-6 col-md-4 col-lg-2">
                      <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6" style="padding: 0;">
                        <label><%=emiConfiguration_From%></label>
                        <input type="text" name="fdate" class="datepicker form-control emi-date" value="<%=emiVO.getStartDate()%>" autocomplete="off" />
                      </div>
                      <div class="col-xs-6 col-sm-6 col-md-2 col-lg-6" style="padding: 0;">
                        <label class="hide_label">&nbsp;</label>
                        <input type='text' class="form-control emi-time" placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=emiVO.getStartTime()%>" />
                      </div>
                    </div>

                    <div class="form-group col-xs-12 col-sm-6 col-md-4 col-lg-2">
                      <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6" style="padding: 0;">
                        <label><%=emiConfiguration_To%></label>
                        <input type="text" name="tdate" class="datepicker form-control emi-date" value="<%=emiVO.getEndDate()%>" autocomplete="off" />
                      </div>
                      <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6" style="padding: 0;">
                        <label class="hide_label">&nbsp;</label>
                        <input type='text' class="form-control emi-time" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=emiVO.getEndTime()%>" />
                      </div>
                    </div>

                    <div class="form-group col-xs-12 col-sm-6 col-md-4 col-lg-3">
                      <div class="col-xs-12 col-sm-6 col-md-6 col-lg-12" style="padding: 0;">
                        <label><%=emiConfiguration_EMI_Period%></label>
                        <div class="form-group col-md-12 has-feedback" style="padding: 0;" >
                          <select size="5" multiple="multiple" id="emiPeriod_<%=i%>" class="form-group" value="<%=emiVO.getEmiPeriod()%>">
                            <option value="-1" disabled><%=emiConfiguration_Select_EMI_Period%></option>
                            <option value="3">3 Months</option>
                            <option value="4">4 Months</option>
                            <option value="5">5 Months</option>
                            <option value="6">6 Months</option>
                            <option value="7">7 Months</option>
                            <option value="8">8 Months</option>
                            <option value="9">9 Months</option>
                            <option value="10">10 Months</option>
                            <option value="11">11 Months</option>
                            <option value="12">12 Months</option>
                            <option value="15">15 Months</option>
                            <option value="24">24 Months</option>
                          </select>
                        </div>
                        <input type="hidden" id="emicode_<%=i%>" name="emiPeriod" value="<%=emiVO.getEmiPeriod()%>">
                        <div class="col-md-6"></div>
                      </div>
                    </div>

                    <div class="form-group col-xs-12 col-sm-2 col-md-4 col-lg-1">
                      <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" style="padding: 0;">
                        <label><%=emiConfiguration_Active%></label>
                      </div>
                      <input type="checkbox" name="active" value="Y" style="align-content: center" <%=isChecked%>>
                    </div>

                    <script>
                      console.log("<%=emiVO.getEmiPeriod()%>");
                      $(function () {
                        $('#emiPeriod_<%=i%>').multiselect({
                          buttonText: function(options, select) {
                            if (options.length === 0){
                              if(<%=emiVO.getEmiPeriod()%>){
                                console.log(" in if"  );
                                return '<%=emiVO.getEmiPeriod()%>';
                              }
                              else
                              {
                                console.log(" in else if ");
                                return '<%=emiConfiguration_Select_EMI_Period%>';
                              }
                            }
                            else {
                              var labels = [];
                              console.log(options);
                              options.each(function() {
                                labels.push($(this).val());
                              });
                              document.getElementById('emicode_<%=i%>').value = labels;
                              return labels.join(', ') + '';
                            }
                          }
                        });
                      });
                    </script>
                    <%
                      }
                    }
                    else
                    {
                    %>

                    <div class="form-group col-xs-12 col-sm-6 col-md-4 col-lg-2">
                      <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6" style="padding: 0;">
                        <label><%=emiConfiguration_From%></label>
                        <input type="text" name="fdate" class="datepicker form-control emi-date" value="" autocomplete="off" />
                      </div>
                      <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6" style="padding: 0;">
                        <label class="hide_label">&nbsp;</label>
                        <input type='text' class="form-control emi-time" placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=startTime%>" />
                      </div>
                    </div>

                    <div class="form-group col-xs-12 col-sm-6 col-md-4 col-lg-2">
                      <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6" style="padding: 0;">
                        <label><%=emiConfiguration_To%></label>
                        <input type="text" name="tdate" class="datepicker form-control emi-date" value="" autocomplete="off"/>
                      </div>
                      <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6" style="padding: 0;">
                        <label class="hide_label">&nbsp;</label>
                        <input type='text' class="form-control emi-time" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=endTime%>" />
                      </div>
                    </div>

                    <div class="form-group col-xs-12 col-sm-6 col-md-4 col-lg-3">
                      <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" style="padding: 0;">
                        <label><%=emiConfiguration_EMI_Period%></label>
                        <div class="form-group col-md-12 has-feedback" style="padding: 0;">
                          <select size="5" multiple="multiple" id="emiPeriod" class="form-group">
                            <option style="" disabled><%=emiConfiguration_Select_EMI_Period%></option>
                            <option value="3">3 Months</option>
                            <option value="4">4 Months</option>
                            <option value="5">5 Months</option>
                            <option value="6">6 Months</option>
                            <option value="7">7 Months</option>
                            <option value="8">8 Months</option>
                            <option value="9">9 Months</option>
                            <option value="10">10 Months</option>
                            <option value="11">11 Months</option>
                            <option value="12">12 Months</option>
                            <option value="15">15 Months</option>
                            <option value="24">24 Months</option>
                          </select>
                        </div>
                        <input type="hidden" id="emicode" name="emiPeriod" value="">
                        <div class="col-md-6"></div>
                      </div>
                    </div>

                    <div class="form-group col-xs-12 col-sm-2 col-md-4 col-lg-1" style="text-align: center">
                      <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" style="padding: 0;">
                        <label><%=emiConfiguration_Active%></label>
                      </div>
                      <input type="checkbox" name="active" value="Y" style="align-content: center">
                    </div>

                    <%
                        }
                      }
                      catch (Exception e){

                        transactionLogger.error("Exception --- "+e);
                      }
                    %>
                  </div>
                </div>
              </div>
              <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                  <div class="widget">
                    <br>
                    <div class="form-group has-feedback" style="text-align: center">
                      <button type="submit" name="create" value="create" class="btn btn-default" >
                        <i class="fa fa-save"></i>
                        &nbsp;&nbsp;<%=emiConfiguration_Save%>
                      </button>
                    </div>
                    <br>
                  </div>
                </div>
              </div>

            </form>
          </div>
        </div>
      </div>

    </div>
  </div>
</div>

<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>

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