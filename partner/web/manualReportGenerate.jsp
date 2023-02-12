</html>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.BankWireManagerVO" %>
<%@ page import="com.manager.utils.CommonFunctionUtil" %>
<%@ page import="com.manager.vo.payoutVOs.SettlementCycleVO" %>
<%@ include file="top.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Created by IntelliJ IDEA.
  User: Mahima
  Date: 1/24/2018
  Time: 4:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%
  ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
    session.setAttribute("submit", "Add Settlement Cycle");
    String memberid=nullToStr(request.getParameter("memberid"));

    BankWireManagerVO bankWireManagerVO = (BankWireManagerVO) session.getAttribute("bankWireManagerVO");
    String partnerId=request.getParameter("partnerId");
    Functions functions = new Functions();
    String errorMessage = (String) request.getAttribute("sberror");

    String settledDate = "";
    String rrDate = "";
    String netFinalAmount = "";
    String unPaidAmount = "";
    String balanceAmount="";
    String amount="";

    CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
    if (bankWireManagerVO != null && functions.isValueNull(bankWireManagerVO.getSettleddate()))
    {
      settledDate = commonFunctionUtil.convertTimestampToDatepicker(bankWireManagerVO.getSettleddate());
    }
    if (bankWireManagerVO != null && functions.isValueNull(bankWireManagerVO.getRollingreservereleasedateupto()))
    {
      rrDate = commonFunctionUtil.convertTimestampToDatepicker(bankWireManagerVO.getRollingreservereleasedateupto());
    }
    if (bankWireManagerVO != null && functions.isValueNull(bankWireManagerVO.getNetfinal_amount()))
    {
      netFinalAmount = Functions.round(Double.valueOf(bankWireManagerVO.getNetfinal_amount()), 2);
    }
    if (bankWireManagerVO != null && functions.isValueNull(bankWireManagerVO.getUnpaid_amount()))
    {
      unPaidAmount = Functions.round(Double.valueOf(bankWireManagerVO.getUnpaid_amount()), 2);
    }
%>
<html>
<head>
  <title><%=company%> | Add Bank Wire</title>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
  <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

  <script type="text/javascript" language="JavaScript">
    function validate(){
      check();
      check1();
      var xls=check();
      var pdf=check1();
      if(xls && pdf){
        return true;
      }
      if(!xls){
        alert('Please select a .xls file! ');
      }
      if(!pdf){
        alert('Please select a .pdf file! ');
      }
      return false;
    }
    function check()
    {
      var retPath = document.FIRCForm.file.value;
      var pos = retPath.lastIndexOf(".");
      var filename = "";
      if (pos != -1)
        filename = retPath.substring(pos + 1);
      else
        filename = retPath;

      var settledTransFile=retPath.split("\\").pop();
      document.getElementById('settledTransFile').value=settledTransFile;

      if (filename == ('xls'))
      {
        return true;
      }
      else
      {
        document.FIRCForm.settledTransFile.value = "";
        return false;
      }
    }
    function check1()
    {
      var retPath = document.FIRCForm.manualfile.value;
      var pos = retPath.lastIndexOf(".");
      var filename = "";
      if (pos != -1)
        filename = retPath.substring(pos + 1);
      else
        filename = retPath;
      var settledReportFile=retPath.split("\\").pop();
      document.getElementById('settledReportFile').value=settledReportFile;

      if (filename == ('pdf'))
      {
        return true;
      }
      else{
        document.FIRCForm.settledReportFile.value = "";
        return false;
      }
    }
  </script>
  <script type="text/javascript">
    $('#sandbox-container input').datepicker({});
  </script>
  <script>
    $(function ()
    {
      $(".datepicker").datepicker({dateFormat: "yy-mm-dd", endDate: '+10y'});
    });
  </script>
  <style type="text/css">
    #main {
      background-color: #ffffff
    }

    :target:before {
      content: "";
      display: block;
      height: 50px;
      margin: -50px 0 0;
    }

    .table > thead > tr > th {
      font-weight: inherit;
    }

    :target:before {
      content: "";
      display: block;
      height: 90px;
      margin: -50px 0 0;
    }

    footer {
      border-top: none;
      margin-top: 0;
      padding: 0;
    }

    /********************Table Responsive Start**************************/
    @media (max-width: 640px) {
      table {
        border: 0;
      }

      table thead {
        display: none;
      }

      tr:nth-child(odd), tr:nth-child(even) {
        background: #ffffff;
      }

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

    tr:nth-child(odd) {
      background: #F9F9F9;
    }

    tr {
      display: table-row;
      vertical-align: inherit;
      border-color: inherit;
    }

    th {
      padding-right: 1em;
      text-align: left;
      font-weight: bold;
    }

    td, th {
      display: table-cell;
      vertical-align: inherit;
    }

    tbody {
      display: table-row-group;
      vertical-align: middle;
      border-color: inherit;
    }

    td {
      padding-top: 6px;
      padding-bottom: 6px;
      padding-left: 10px;
      padding-right: 10px;
      vertical-align: top;
      border-bottom: none;
    }

    .table > thead > tr > th, .table > tbody > tr > th, .table > tfoot > tr > th, .table > thead > tr > td, .table > tbody > tr > td, .table > tfoot > tr > td {
      border-top: 1px solid #ddd;
    }

    /********************Table Responsive Ends**************************/
    @media (min-width: 768px) {
      .form-horizontal .control-label {
        text-align: left !important;
      }
    }
  </style>
</head>
<body>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="row">
        <div class="col-md-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Bank Manual Wire Creation</strong></h2>

              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <br>

            <div class="widget-content padding">
              <div id="horizontal-form">
                <form name="FIRCForm" action="/partner/net/ManualReportGenerate?ctoken=<%=ctoken%>" method="POST" class="form-horizontal" enctype="multipart/form-data">
                  <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>">
                  <input type="hidden" value="<%=partnerId%>" name="partnerId" id="partnerid">
                  <input type="hidden" id="settledTransFile" value="" name="settledTransFile">
                  <input type="hidden" id="settledReportFile" value="" name="settledReportFile">

                  <div class="widget-content padding">
                    <div class="form-group">
                      <div class="col-md-12">
                        <%
                          if (functions.isValueNull(errorMessage))
                          {
                            out.print("<center>" + errorMessage + "</center>");
                          }
                        %>
                      </div>
                    </div>

                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-2 control-label">Account ID<br>
                      </label>

                      <div class="col-md-4">
                        <input type="text" class="form-control" name="accountid"
                               value="<%=bankWireManagerVO.getAccountId()%>" size="35" readonly>
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-2 control-label">Partner ID<br>
                      </label>

                      <div class="col-md-4">
                        <input type="text" class="form-control" id="pid" name="partnerId"
                               value="<%=partnerId%>" size="35" readonly>
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-2 control-label">Merchant ID*<br>
                      </label>

                      <div class="col-md-4">
                        <input name="memberid" id="member" value="<%=memberid%>" class="form-control" background-color: #ffffff; autocomplete="on"  >
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-2 control-label">Bank Settlement Start Date*<br>
                      </label>

                      <%
                        String start_date[] = commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getBank_start_date());
                        String Bank_start_date = start_date[0]+" "+start_date[1];

                        String end_date[] = commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getBank_end_date());
                        String Bank_end_date = end_date[0]+" "+end_date[1];

                        String server_start_date[] = commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getServer_start_date());
                        String Server_start_date = server_start_date[0]+" "+server_start_date[1];

                        String server_end_date[] = commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getServer_end_date());
                        String Server_end_date = server_end_date[0]+" "+server_end_date[1];
                      %>
                      <div class="col-md-4">
                        <input type="text" class="form-control" name="bsdate"
                               value="<%=Bank_start_date%>" size="35"
                               readonly>
                      </div>
                      <div class="col-md-6"></div>
                    </div>
                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-2 control-label">Bank Settlement End Date*<br>
                      </label>

                      <div class="col-md-4">
                        <input type="text" class="form-control" name="bedate"
                               value="<%=Bank_end_date%>" size="35"
                               readonly>
                      </div>
                      <div class="col-md-6"></div>
                    </div>
                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-2 control-label">Server Settlement Start Date*<br>
                      </label>

                      <div class="col-md-4">
                        <input type="text" class="form-control" name="ssdate"
                               value="<%=Server_start_date%>" size="35"
                               readonly>
                      </div>
                      <div class="col-md-6"></div>
                    </div>
                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-2 control-label">Server Settlement End Date*<br>
                      </label>


                      <div class="col-md-4">
                        <input type="text" class="form-control" name="sedate"
                               value="<%=Server_end_date%>" size="35"
                               readonly>
                      </div>
                      <div class="col-md-6"></div>
                    </div>
                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-2 control-label">Is Money Received*<br>
                      </label>

                      <div class="col-md-4">
                        <select name="ispaid" class="form-control" readonly>
                          <%
                            if ("N".equalsIgnoreCase(bankWireManagerVO.getIspaid()))
                            {%>
                          <option value="Y">Y</option>
                          <option value="N" selected>N</option>
                          <%
                          }
                          else
                          {
                          %>
                          <option value="Y">Y</option>
                          <option value="N">N</option>
                          <%
                            }
                          %>
                        </select>
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-2 control-label">Money Received Date<br>
                      </label>

                      <div class="col-md-4">
                        <input type="text" size="16" name="sdate" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute (settledDate)%>" readonly>
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-2 control-label">Amount*<br>
                      </label>

                      <div class="col-md-4">
                        <input type="text" class="form-control" name="amount"
                               value="<%=amount%>" placeHolder="0.00" size="9">
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-2 control-label">Balance Amount*<br>
                      </label>

                      <div class="col-md-4">
                        <input type="text" class="form-control" name="balanceamount"
                               value="<%=balanceAmount%>" placeHolder="0.00" size="9">
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-2 control-label">UnPaid Amount<br>
                      </label>

                      <div class="col-md-4">
                        <input type="text" class="form-control" name="unpaidamt"
                               value="<%=unPaidAmount%>" placeHolder="0.00" size="9" readonly>
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-2 control-label">Net Final Amount<br>
                      </label>

                      <div class="col-md-4">
                        <input type="text" class="form-control" name="finalamount"
                               value="<%=netFinalAmount%>" placeHolder="0.00" size="9" readonly>
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-2 control-label">RR Release Date*<br>
                      </label>

                      <div class="col-md-4">
                        <input type="text" size="16" name="rrdate" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute (rrDate)%>" readonly>
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group ">
                      <div class="col-md-2"></div>
                      <label class="col-md-2 control-label">Transaction File From Bank(.xls)*<br>
                      </label>

                      <div class="col-md-3">
                        <input type="file" class="btn btn-default form-control" name="file" value=""
                               onchange="return check()">
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group ">
                      <div class="col-md-2"></div>
                      <label class="col-md-2 control-label">Report From Bank(.pdf)*<br>
                      </label>

                      <div class="col-md-3">
                        <input type="file" class="btn btn-default form-control" name="manualfile" value=""
                               onchange="return check1()">
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <div class="col-md-8" align="center">
                        <button type="submit" value="next" name="action" class="btn btn-default"
                                id="next" onclick="return validate()"
                                style="display: -webkit-box;"><i class="fa fa-save">Upload Payout Report
                        </i></button>
                      </div>
                    </div>
                    <div class="form-group col-md-8 has-feedback"></div>
                    <div class="form-group col-md-8 has-feedback"></div>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
<%
  }
  else
  {
    response.sendRedirect("/partner/logout.jsp");
    return;
  }
%>
<%!
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>