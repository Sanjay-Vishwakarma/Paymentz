<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.WhitelistingDetailsVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ include file="functions.jsp"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="whitelistTab.jsp" %>
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

    tr:nth-child(odd) {background: transparent !important;}
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
    .eye-icon
    {
      float: right;
      z-index: 2;
      margin-right: 5px;
    }
    .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td{border-top: 1px solid #ddd;}
    /********************Table Responsive Ends**************************/
  </style>
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css" integrity="sha384-hWVjflwFxL6sNzntih27bfxkr27PmbbK/iSvJ+a4+0owXq79v+lsFkW54bOGbiDQ" crossorigin="anonymous">
  <link rel="stylesheet" href="/partner/NewCss/css/jquery.dataTables.min.css">
  <script src="/partner/NewCss/js/jquery.dataTables.min.js"></script>
<%--
  <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
--%>
  <script src="/merchant/NewCss/libs/bootstrap/js/umd/popper.js"></script>
<%--
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
--%>
  <script src="/merchant/javascript/autocomplete_merchant_terminalid.js"></script>
  <script src="/merchant/javascript/hidde.js"></script>
  <link href="/partner/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/partner/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script type="text/javascript" src="/merchant/transactionCSS/js/content.js"></script>
  <script src="/merchant/javascript/autocomplete_merchant_terminalid.js"></script>
  <script type="text/javascript">
    $('#sandbox-container input').datepicker({
    });
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
        document.WhitelistDelete.submit();
      }
    }
    function unMasking(spanid,inputid,msg)
    {
      var x=document.getElementById(inputid);
      $("#" + spanid).removeClass('fa-eye-slash').addClass('fa-eye');
      x.innerText=msg;
    }
    function masking(spanid,inputid,msg)
    {
      var x=document.getElementById(inputid);
      $("#" + spanid).removeClass('fa-eye').addClass('fa-eye-slash');
      x.innerText=msg;
    }
  </script>
</head>
<body>
  <%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  String firstSix=request.getParameter("firstsix")==null?"":request.getParameter("firstsix");
  String lastFour=request.getParameter("lastfour")==null?"":request.getParameter("lastfour");
  String accountId=request.getParameter("accountid")==null?"":request.getParameter("accountid");
  String emailAddr=request.getParameter("emailAddr")==null?"":request.getParameter("emailAddr");
  String name=request.getParameter("name")==null?"":request.getParameter("name");
  String ipAddress=request.getParameter("ipAddress")==null?"":request.getParameter("ipAddress");
  String expiryDate=Functions.checkStringNull(request.getParameter("expiryDate")) == null ? "" : request.getParameter("expiryDate");

  Functions functions=new Functions();
  int pageno=1;
  int recordsPerPage=15;
  pageno= Functions.convertStringtoInt(request.getParameter("SPageno"),1);
  recordsPerPage=Functions.convertStringtoInt(request.getParameter("SRecords"),15);

 ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String whitelist_whitelist_card1 = StringUtils.isNotEmpty(rb1.getString("whitelist_whitelist_card1"))?rb1.getString("whitelist_whitelist_card1"): "WhiteList Card";
  String whitelist_merchantid1 = StringUtils.isNotEmpty(rb1.getString("whitelist_merchantid1"))?rb1.getString("whitelist_merchantid1"): "Merchant ID*";
  String whitelist_account_id = StringUtils.isNotEmpty(rb1.getString("whitelist_account_id"))?rb1.getString("whitelist_account_id"): "Account ID";
  String whitelist_first_six = StringUtils.isNotEmpty(rb1.getString("whitelist_first_six"))?rb1.getString("whitelist_first_six"): "First Six*";
  String whitelist_last = StringUtils.isNotEmpty(rb1.getString("whitelist_last"))?rb1.getString("whitelist_last"): "Last Four*";
  String whitelist_email1 = StringUtils.isNotEmpty(rb1.getString("whitelist_email1"))?rb1.getString("whitelist_email1"): "Email Address";
  String whitelist_holder = StringUtils.isNotEmpty(rb1.getString("whitelist_holder"))?rb1.getString("whitelist_holder"): "Card Holder Name";
  String whitelist_ip = StringUtils.isNotEmpty(rb1.getString("whitelist_ip"))?rb1.getString("whitelist_ip"): "IP Address";
  String whitelist_expiry = StringUtils.isNotEmpty(rb1.getString("whitelist_expiry"))?rb1.getString("whitelist_expiry"): "Expiry Date";
  String whitelist_card = StringUtils.isNotEmpty(rb1.getString("whitelist_card"))?rb1.getString("whitelist_card"): "Whitelist Card";
  String whitelist_search = StringUtils.isNotEmpty(rb1.getString("whitelist_search"))?rb1.getString("whitelist_search"): "Search";
  String whitelist_card_list = StringUtils.isNotEmpty(rb1.getString("whitelist_card_list"))?rb1.getString("whitelist_card_list"): "Whitelist Card List";
  String whitelist_sorry = StringUtils.isNotEmpty(rb1.getString("whitelist_sorry"))?rb1.getString("whitelist_sorry"): "Sorry";
  String whitelist_no_records = StringUtils.isNotEmpty(rb1.getString("whitelist_no_records"))?rb1.getString("whitelist_no_records"): "No records found.";
  String whitelist_Sr_No = StringUtils.isNotEmpty(rb1.getString("whitelist_Sr_No"))?rb1.getString("whitelist_Sr_No"): "Sr No.";
  String whitelist_First_Six = StringUtils.isNotEmpty(rb1.getString("whitelist_First_Six"))?rb1.getString("whitelist_First_Six"): "First Six";
  String whitelist_Last_Four = StringUtils.isNotEmpty(rb1.getString("whitelist_Last_Four"))?rb1.getString("whitelist_Last_Four"): "Last Four";
  String whitelist_EmailAddress = StringUtils.isNotEmpty(rb1.getString("whitelist_EmailAddress"))?rb1.getString("whitelist_EmailAddress"): "Email Address";
  String whitelist_Name = StringUtils.isNotEmpty(rb1.getString("whitelist_Name"))?rb1.getString("whitelist_Name"): "Name";
  String whitelist_IPAddress = StringUtils.isNotEmpty(rb1.getString("whitelist_IPAddress"))?rb1.getString("whitelist_IPAddress"): "IP Address";
  String whitelist_AccountID = StringUtils.isNotEmpty(rb1.getString("whitelist_AccountID"))?rb1.getString("whitelist_AccountID"): "AccountID";
  String whitelist_Showing_Page = StringUtils.isNotEmpty(rb1.getString("whitelist_Showing_Page"))?rb1.getString("whitelist_Showing_Page"): "Showing Page";
  String whitelist_of = StringUtils.isNotEmpty(rb1.getString("whitelist_of"))?rb1.getString("whitelist_of"): "of";
  String whitelist_records = StringUtils.isNotEmpty(rb1.getString("whitelist_records"))?rb1.getString("whitelist_records"): "records";
  String whitelist_Delete = StringUtils.isNotEmpty(rb1.getString("whitelist_Delete"))?rb1.getString("whitelist_Delete"): "Delete";
  String whitelist_page_no=StringUtils.isNotEmpty(rb1.getString("whitelist_page_no"))?rb1.getString("whitelist_page_no"):"Page number";
  String whitelist_total_no_of_records=StringUtils.isNotEmpty(rb1.getString("whitelist_total_no_of_records"))?rb1.getString("whitelist_total_no_of_records"):"Total number of records";

  %>

<body>
<div class="content-page">
  <div class="content" style="padding:0px 20px ;margin: 0px;">
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=whitelist_whitelist_card1%></strong>
              </h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="pull-right">
              <div class="btn-group">
                <form action="/merchant/servlet/UploadBulkCards?ctoken=<%=ctoken%>" method="POST">
                  <input type="hidden" name="action" value="getdata">
                  <input type="hidden" name="merchantid" value="<%=(String) session.getAttribute("merchantid")%>">
                  <input type="hidden" name="toid" value="<%=(String) session.getAttribute("merchantid")%>">


                  <button class="btn-lg" style="background-color: #98A3A3; border-radius: 25px;color: white;padding: 12px 16px;font-size: 16px;cursor: pointer; margin-right: 30px;">Upload Bulk Card
                  </button>
                </form>
              </div>
            </div>
            <div class="widget-content">
              <div id="horizontal-form">
                <form action="/merchant/servlet/WhiteListCard?ctoken=<%=ctoken%>" method="post" name="forms" >
                  <div align="center">
                    <input type="hidden" value="<%=ctoken%>" id="ctoken">
                    <input type="hidden" value="<%=(String) session.getAttribute("merchantid")%>" id="merchantid">
                      <%
                      String errormsg1 = (String) request.getAttribute("error");
                     String msg = (String) request.getAttribute("msg");
                    if (functions.isValueNull(errormsg1))
                    {
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                    }
                    if(functions.isValueNull(msg))
                    {
                      out.println(Functions.NewShowConfirmation1("Sorry", msg));
                    }
                    %>
                    <div class="form-group col-md-1">
                    </div>

                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label><%=whitelist_merchantid1%></label>
                        </div>
                        <div class="col-md-3">
                          <input type="text" name="merchantid" class="form-control"  value="<%=(String) session.getAttribute("merchantid")%>" disabled>
                        </div>
                      </div>
                    </div>

                   <%-- <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label><%=whitelist_account_id%>*</label>
                        </div>
                        <div class="col-md-3">
                          <input id="accountid" name="accountid"  value="<%=accountId%>" class="form-control">
                        </div>
                      </div>
                    </div>--%>

                    <%--<div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label>Account ID*</label>
                        </div>
                        <div class="col-md-3">
                          <input id="accountid" name="accountid"  value="<%=accountId%>" class="form-control">
                        </div>
                      </div>
                    </div>--%>

                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label><%=whitelist_first_six%></label>
                        </div>
                        <div class="col-md-3">
                          <input id="firstsix" name="firstsix" maxlength="6" size="6" value="<%=firstSix%>" class="form-control">
                        </div>
                      </div>
                    </div>

                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label><%=whitelist_last%></label>
                        </div>
                        <div class="col-md-3">
                          <input id="lastfour" name="lastfour" maxlength="4" size="4" value="<%=lastFour%>"class="form-control">
                        </div>
                      </div>
                    </div>

                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label><%=whitelist_email1%></label>
                        </div>
                        <div class="col-md-3">
                          <input id="emailaddr" name="emailAddr" type="text" value="<%=emailAddr%>" class="form-control">
                        </div>
                      </div>
                    </div>

                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label><%=whitelist_holder%></label>
                        </div>
                        <div class="col-md-3">
                          <input id="name" name="name" type="text"  value="<%=name%>"class="form-control">
                        </div>
                      </div>
                    </div>

                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label><%=whitelist_ip%></label>
                        </div>
                        <div class="col-md-3">
                          <input id="ipAddress" maxlength="16" name="ipAddress" type="text"  value="<%=ipAddress%>"class="form-control">
                        </div>
                      </div>
                    </div>

                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label><%=whitelist_expiry%></label>
                        </div>
                        <div class="col-md-3">
                          <div class="CardExpiry"><input type="text" id="Expiry"  class="form-control" style="width:100%"  placeholder="MM/YY" name="expiryDate" onkeypress="return isNumberKey(event)" onblur="expiryCheck('Expiry','CardExpiry')" autocomplete="off" onkeyup="addSlash(event,'Expiry')" value=<%=expiryDate%>></div>
                        </div>
                      </div>
                    </div>

                    <div class="form-group col-md-12">
                      <p style="text-align: center;">
                        <button type="submit" name="upload" value="upload"
                                style="display: inline-block!important;"
                                class="btn btn-default center-block"><i
                                class="fa fa-clock-o"></i>
                          &nbsp;&nbsp;<%=whitelist_card%>
                        </button>
                        <button type="submit" class="btn btn-default center-block" style="display: inline-block!important;"><i class="fa fa-clock-o"></i>
                          &nbsp;&nbsp;<%=whitelist_search%></button>
                      </p>
                    </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="row reporttable">
      <div class="col-md-12">
        <div class="widget">
          <div class="widget-header">
            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=whitelist_card_list%></strong></h2>
            <div class="additional-btn">
              <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
              <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
            </div>
          </div>
          <div class="widget-content padding">
            <%
              if(request.getAttribute("listofcard")!=null)
              {
                List<WhitelistingDetailsVO> cList = (List<WhitelistingDetailsVO>)request.getAttribute("listofcard");
                PaginationVO paginationVO = (PaginationVO)request.getAttribute("paginationVO");
                paginationVO.setInputs("ctoken="+ctoken+paginationVO.getInputs());
                if(cList.size()>0)
                {
            %>
            <br>
            <div class="pull-right">
              <div class="btn-group">
                <form name="exportform" method="post" action="/merchant/servlet/ExportWhiteList?ctoken=<%=ctoken%>" >
                  <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstSix)%>" name="firstsix">
                  <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastFour)%>" name="lastfour">
                  <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailAddr)%>" name="emailAddr">
                  <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(name)%>" name="name">
                  <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(ipAddress)%>" name="ipAddress">
                  <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(accountId)%>" name="accountid">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken">

                  <button class="btn-xs" type="submit" style="background: white;border: 0;">
                    <img style="height: 40px;" src="/merchant/images/excel.png">
                  </button>
                </form>
              </div>
            </div>
            <form action="/merchant/servlet/WhiteListCard?ctoken=<%=ctoken%>" method="POST" name="WhitelistDelete">
              <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr style="color: white;">
                  <th valign="middle" align="center" style="text-align: center"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></th>
                  <th valign="middle" align="center" style="text-align: center"><%=whitelist_Sr_No%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=whitelist_First_Six%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=whitelist_Last_Four%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=whitelist_EmailAddress%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=whitelist_Name%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=whitelist_IPAddress%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=whitelist_AccountID%></th>
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
                  for(WhitelistingDetailsVO whitelistingDetailsVO : cList)
                  {
                  int srno=pos+((pageno)*recordsPerPage);
                    String email="";
                    String name1="";
                    String maskingName="";
                    String ipAddress1="";
                    if(functions.isValueNull(whitelistingDetailsVO.getEmail()))
                    email=whitelistingDetailsVO.getEmail();

                    if(functions.isValueNull(whitelistingDetailsVO.getName()))
                    {
                      name1=whitelistingDetailsVO.getName();
                      maskingName=functions.getNameMasking(name1);
                    }
                    if(functions.isValueNull(whitelistingDetailsVO.getIpAddress()))
                    {
                      ipAddress1=whitelistingDetailsVO.getIpAddress();
                    }

                    out.println("<tr>");
                    out.println("<td valign=\"middle\" data-label=\"id\" align=\"center\" "+style+">&nbsp;<input type=\"checkbox\" name=\"id\" value="+whitelistingDetailsVO.getId()+"></td>");
                    out.println("<td align=center "+style+">&nbsp;"+srno+"</td>");
                    out.println("<td align=center "+style+">"+whitelistingDetailsVO.getFirstsix()+"</td>");
                    out.println("<td align=center "+style+">"+whitelistingDetailsVO.getLastfour()+"</td>");
                    out.println("<td align=center "+style+">"+email+"</td>");
                    out.println("<td align=center "+style+">");
                    if(functions.isValueNull(name1) || !"".equals(name1))
                    out.println("<label id=\"name"+pos+"\">"+maskingName+"</label><span style=\"z-index: 2;font-size:12px;margin-top:5px;margin-right:5px; float:right;\" toggle=\"#password-field\" class=\"far fa-eye-slash toggle-password\" onmousedown=\"unMasking('showHidepass1"+pos+"','name"+pos+"','"+name1+"')\" onmouseup=\"masking('showHidepass1"+pos+"','name"+pos+"','"+maskingName+"')\" id=\"showHidepass1"+pos+"\" >");
                    out.println("</td>");
                    out.println("<td align=center "+style+">"+ipAddress1+"</td>");
                    out.println("<td align=center "+style+">"+whitelistingDetailsVO.getAccountid()+"</td>");
                    out.println("</tr>");
                    pos++;
                  }
                  %>
              </table>
          <%--  <table width="100%">
              <thead>
              <tr>
                <td width="15%" align="center">
                  <input type="hidden" name="delete" value="delete"><input type="button" name="delete" class="btn btn-default gotoauto" value="<%=whitelist_Delete%>" onclick="return Delete();">
                </td>
              </tr>
              </thead>
            </table>--%>
              <%--<table width="100%">
                <thead>
                <tr>
                  <td width="15%" align="center">
                    <input type="hidden" name="accountid" value="<%=accountid%>">
                    <input type="hidden" name="firstsix" value="<%=firstSix%>">
                    <input type="hidden" name="lastfour" value="<%=lastFour%>">
                    <input type="hidden" name="emailAddr" value="<%=emailAddr%>">
                    <input type="hidden" name="delete" value="delete"><input type="button" name="delete" class="btn btn-default center-block" value="Delete" onclick="return Delete();">
                  </td>
                </tr>
                </thead>
              </table>--%>
           </form>
          </div>
        </div>
      </div>
    </div>

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
    <div id="showingid"><strong><%=whitelist_page_no%> <%=paginationVO.getPageNo()%> <%=whitelist_of%> <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
    <div id="showingid"><strong><%=whitelist_total_no_of_records%>   <%=paginationVO.getTotalRecords()%> </strong></div>

    <%--<div id="showingid"><strong><%=whitelist_Showing_Page%> <%=paginationVO.getPageNo()%> <%=whitelist_of%> <%=paginationVO.getTotalRecords()%></strong></div>--%>
    <jsp:include page="page.jsp" flush="true">
      <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
      <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
      <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
      <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
      <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
      <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
      <jsp:param name="orderby" value=""/>
    </jsp:include>
    <%
        }
        else
        {
          out.println(Functions.NewShowConfirmation1(whitelist_sorry,whitelist_no_records));
        }
      }
        else
        {
          out.println(Functions.NewShowConfirmation1(whitelist_sorry, whitelist_no_records));
        }
    %>
  </div>
</div>
</div>
</body>
</html>