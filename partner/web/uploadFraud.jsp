<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: NAMRATA BARI
  Date: 16/09/19
  Time: 3:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp" %>
<%
  //String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","Fraud Upload");
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String uploadFraud_Upload_Fraud = StringUtils.isNotEmpty(rb1.getString("uploadFraud_Upload_Fraud")) ? rb1.getString("uploadFraud_Upload_Fraud") : "Upload Fraud";
  String uploadFraud_Upload_Fraud_file = StringUtils.isNotEmpty(rb1.getString("uploadFraud_Upload_Fraud_file")) ? rb1.getString("uploadFraud_Upload_Fraud_file") : "Upload Fraud File";
  String uploadFraud_Upload = StringUtils.isNotEmpty(rb1.getString("uploadFraud_Upload")) ? rb1.getString("uploadFraud_Upload") : "Upload";


  %>
<html>
<head>
  <title> Upload Fraud</title>
</head>
<style type="text/css">
  @media (min-width: 768px) {
    .form-horizontal .control-label {
      text-align: left !important;
    }

    table {
      width: 100%;
      max-width: 40%;
      border-collapse: collapse;
      margin-bottom: 20px;
      display: table;
      border-collapse: separate;
      /* border-color: grey; */
    }
    .txtbox {
      color: #001962;
      text-valign: center;
      font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
      font-size: 12px;
      FONT-WEIGHT: normal;
      width: 142px;
      height: 25px;
      -webkit-border-radius: 4px;
      -moz-border-radius: 4px;
      border-radius: 4px;
    }

    .textb {
      color: #001962;
      text-valign: center;
      font-family: "Open Sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
      font-size: 12px;
      /* font-weight: bold; */
    }

    td {
      padding-top: 10px;
      padding-bottom: 10px;
      padding-left: 10px;
      padding-right: 10px;
      vertical-align: top;
      border-bottom: none;
    }

    button, input, optgroup, select, textarea {
      color: inherit;
      font: inherit;
      margin: 0;
    }

    .tablestyle{
      font-family: Open Sans;
      font-size: 13px;
      font-weight: 600;
      width: 100%;
      max-width:100%;
    }

    .table.table {
      clear: both;
      margin-top: 6px !important;
      margin-bottom: 6px !important;
      max-width: none !important;
      background-color: transparent;
    }

    .table-bordered {
      border: 1px solid #ddd;
    }

    .tdstyle{
      background-color: #7eccad !important;;
    }
  }
</style>
<script>
  function check()
  {
    var retpath = document.FIRCForm.File.value;
    var pos = retpath.indexOf(".");
    var filename = "";
    if (pos != -1)
      filename = retpath.substring(pos + 1);
    else
      filename = retpath;
    if (filename == ('xls') || filename == ('xlsx'))
    {
      return true;
    }
    alert('Please select a .xls or .xlsx file instead!');
    return false;
  }
</script>
<body>
<%
  user = (User) session.getAttribute("ESAPIUserSessionKey");
  ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
%>
<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/markFraudTransaction.jsp?ctoken=<%=ctoken%>" method="post" name="form">
            <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/partner/images/goBack.png">
            </button>
          </form>
        </div>
      </div>
      <br>
      <br>
      <br>
      <div class="row">
        <div class="col-md-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-upload"></i>&nbsp;&nbsp;<strong><%=uploadFraud_Upload_Fraud%></strong></h2>

              <div class="additional-btn">

                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <form name="FIRCForm" action="/partner/net/UploadFraudFile?ctoken=<%=ctoken%>"
                  method="post" ENCTYPE="multipart/form-data">
              <div class="panel panel-default" style="background-color: #f8f8f8;">
                <br>
                <table>
                  <tr>
              <div class="col-lg-12">
                <div>
                  <div class="form-group col-md-12">
                    <div class="ui-widget">
                      <div class="col-sm-offset-3 col-md-2" style="margin-top:6px;">
                        <label ><%=uploadFraud_Upload_Fraud_file%></label>
                      </div>
                      <div class="col-md-3" >
                        <input name="File" type="file" value="choose File"
                               class="btn btn-default">
                      </div>
                    </div>
                  </div>
                  <div class="form-group col-md-12">
                    <div class="col-md-10 text-right center-block" style="width: 91.5%">
                      <button type="submit" class="btn btn-default center-block" onclick="return check()"><i class="fa fa-upload"></i>
                        &nbsp;&nbsp;&nbsp;&nbsp;<%=uploadFraud_Upload%></button>
                    </div>
                    </div>
                </div>
              </div>
                  </tr>
                </table>
                </div>
            </form>
       </div>
          </div>
        </div>
      <%
        String Msg = (String) request.getAttribute("msg");
        String Result = (String) request.getAttribute("Result");
        if(Msg != null  && Result!= null){
      %>
      <table align="center" width="50%" cellpadding="2" cellspacing="2" style="max-width: 80%;">
        <tr>
          <td>
            <table bgcolor="#ecf0f1" align="center" cellpadding="0" cellspacing="0" style="max-width: 80%;">
              <tr height=30>
                <td colspan="4" bgcolor="#7eccad" class="texthead" align="center" ><font
                        color="#FFFFFF"
                        size="2"
                        face="Open Sans,Helvetica Neue,Helvetica,Arial,Palatino Linotype', 'Book Antiqua, Palatino, serif">
                  <%=Msg%>
                </font></td>
              </tr>

              <tr>
                <td align="center" class="textb" style="max-width: 80%;"><%=Result%>
                </td>
              </tr>
              <tr>
                <td>&nbsp;</td>
              </tr>
            </table>
        </tr>
        </td> </table>
      <%
        }
      %>

    </div>
      <%
    }
    else
    {
        response.sendRedirect("/partner/logout.jsp");
        return;
    }
%>
</body>
</html>
