<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ include file="Top.jsp" %>
<%
  session.setAttribute("submit","EMI Configuration");
  String memberid = (String)session.getAttribute("merchantid");

  String fromdate = null;
  String todate = null;
  Date date = new Date();
  SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

  String Date = originalFormat.format(date);
  date.setDate(1);
  String fromDate = originalFormat.format(date);

  fromdate = Functions.checkStringNull(request.getParameter("fdate")) == null ? fromDate : request.getParameter("fdate");
  todate = Functions.checkStringNull(request.getParameter("tdate")) == null ? Date : request.getParameter("tdate");

  Calendar rightNow = Calendar.getInstance();
  String str = "";
  if (fromdate == null) fromdate = "" + 1;
  if (todate == null) todate = "" + rightNow.get(rightNow.DATE);
  String currentyear= ""+rightNow.get(rightNow.YEAR);

  if (fromdate != null) str = str + "fdate=" + fromdate;
  if (todate != null) str = str + "&tdate=" + todate;

  str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

  String startTime = Functions.checkStringNull(request.getParameter("starttime"));
  String endTime = Functions.checkStringNull(request.getParameter("endtime"));
  if (startTime == null) startTime = "00:00:00";
  if (endTime == null) endTime = "23:59:59";
%>
<html lang="en">
<head>
  <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
  <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>

  <%--<script src="/merchant/NewCss/am_multipleselection/jquery-multipleselection.js"></script>--%>
  <script src="/merchant/NewCss/am_multipleselection/bootstrap-multiselect.js"></script>
  <link href="/merchant/NewCss/am_multipleselection/bootstrap-multiselect.css" rel="stylesheet" />

  <style type="text/css">
    .padd{
      padding: 0px 15px !important;
    }
    .marginBottom{
      margin-bottom: 20px;
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
    /********************************************************************************/
  </style>
  <script>
    $(function () {
      $('#emiPeriod').multiselect({
        buttonText: function(options, select) {
          if (options.length === 0) {
            return 'Select EMI Period...';
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
  <script type="text/javascript">
    $('#sandbox-container input').datepicker({
    });
  </script>
  <script>
    $(function() {
      $(".datepicker").datepicker({dateFormat: "yy-mm-dd",endDate:'+10y'});
    });
  </script>

</head>
<body class="pace-done widescreen fixed-left-void">
<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Add EMI Details</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <form name="form" method="post" action="/merchant/servlet/EmiConfig?ctoken=<%=ctoken%>" >
              <div id="rows">
                <%
                  String message = (String) request.getAttribute("sErrorMessage");
                  if (functions.isValueNull(message))
                  {
                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                  }

                  if(request.getAttribute("emiVOList")!=null)
                  {
                    List<EmiVO> emiVOList = (List<EmiVO>)request.getAttribute("emiVOList");
                    int i=0;
                    if(emiVOList.size()>0)
                    {
                      for(EmiVO emiVO:emiVOList)
                      {
                        String isChecked = "";
                        i++;
                        if(emiVO.getIsActive().equalsIgnoreCase("Y"))
                          isChecked = "checked";
                %>

                <div class="widget-content padding" style="display:inline-block !important;">
                  <div id="horizontal-form1">
                    <div class="form-group col-xs-12 col-sm-2 col-md-2 col-lg-1">
                      <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" style="padding: 0;">
                        <label>Active</label>
                      </div>
                      <input type="radio" name="active" value="Y" style="align-content: center" <%=isChecked%>>
                    </div>

                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-3">
                      <div class="col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                        <label>From*</label>
                        <input type="text" name="fdate" class="datepicker form-control" value="<%=emiVO.getStartDate()%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                      </div>

                      <div class="col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;">
                        <label class="hide_label">&nbsp;</label>
                        <input type='text' class="form-control" placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=emiVO.getStartTime()%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                      </div>
                    </div>

                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-3">
                      <div class="col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                        <label>To*</label>
                        <input type="text" name="tdate" class="datepicker form-control" value="<%=emiVO.getEndDate()%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                      </div>
                      <div class="col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;">
                        <label class="hide_label">&nbsp;</label>
                        <input type='text' class="form-control" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=emiVO.getEndTime()%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                      </div>
                    </div>

                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-3">
                      <div class="col-xs-12 col-sm-6 col-md-6 col-lg-12" style="padding: 0;">
                        <label>EMI Period*</label>
                        <div class="form-group col-md-12 has-feedback" style="padding: 0;" >
                          <select size="5" multiple="multiple" id="emiPeriod_<%=i%>" class="form-group" value="<%=emiVO.getEmiPeriod()%>">
                            <option value="-1" disabled>Select EMI Period</option>
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
                                return 'Select EMI Period...';
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
                  </div>
                </div>
                <%
                  }
                }
                else
                {
                %>
                <div class="widget-content padding" style="display:inline-block !important;">
                  <div id="horizontal-form1">
                    <div class="form-group col-xs-12 col-sm-2 col-md-2 col-lg-1">
                      <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12" style="padding: 0;">
                        <label>Active</label>
                      </div>
                      <input type="radio" name="active" value="Y" style="align-content: center">
                    </div>

                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-3">
                      <div class="col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                        <label>From*</label>
                        <input type="text" name="fdate" class="datepicker form-control" value="" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                      </div>

                      <div class="col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;">
                        <label class="hide_label">&nbsp;</label>
                        <input type='text' class="form-control" placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=startTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                      </div>
                    </div>

                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-3">
                      <div class="col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                        <label>To*</label>
                        <input type="text" name="tdate" class="datepicker form-control" value="" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                      </div>
                      <div class="col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;">
                        <label class="hide_label">&nbsp;</label>
                        <input type='text' class="form-control" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=endTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                      </div>
                    </div>

                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-3">
                      <div class="col-xs-12 col-sm-6 col-md-6 col-lg-12" style="padding: 0;">
                        <label>EMI Period*</label>
                        <div class="form-group col-md-12 has-feedback" style="padding: 0;">
                          <select size="5" multiple="multiple" id="emiPeriod" class="form-group">
                            <option style="" disabled>Select EMI Period</option>
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
                  </div>
                </div>
                <%
                    }
                  }
                %>
              </div>
              <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                  <div class="widget">
                    <br>
                    <div class="form-group col-md-5 has-feedback"></div>
                    <div class="form-group col-md-2 has-feedback">
                      <button type="submit" name="create" value="create" class="btn btn-default" >
                        <i class="fa fa-save"></i>
                        &nbsp;&nbsp;Save
                      </button>
                    </div>
                    <div class="form-group col-md-5 has-feedback"></div>
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
</div>
</body>
</html>