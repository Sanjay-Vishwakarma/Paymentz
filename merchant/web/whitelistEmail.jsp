<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.WhitelistingDetailsVO" %>
<%@ page import="java.util.List" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="functions.jsp"%>
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
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css" integrity="sha384-hWVjflwFxL6sNzntih27bfxkr27PmbbK/iSvJ+a4+0owXq79v+lsFkW54bOGbiDQ" crossorigin="anonymous">
  <link rel="stylesheet" href="/partner/NewCss/css/jquery.dataTables.min.css">
  <script src="/partner/NewCss/js/jquery.dataTables.min.js"></script>
  <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
  <script src="/merchant/javascript/autocomplete_merchant_terminalid.js"></script>
  <script src="/merchant/javascript/hidde.js"></script>
  <link href="/partner/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/partner/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script type="text/javascript" src="/merchant/transactionCSS/js/content.js"></script>
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
      if (confirm("Do you really want to unblock all selected Data."))
      {
        document.WhiteListEmail.submit();
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
  String emailAddr=request.getParameter("emailaddr")==null?"":request.getParameter("emailaddr");
  String name=request.getParameter("name")==null?"":request.getParameter("name");
  String ipAddress=request.getParameter("ipAddress")==null?"":request.getParameter("ipAddress");
  String accountid=request.getParameter("accountid")==null?"":request.getParameter("accountid");
  String expiryDate=Functions.checkStringNull(request.getParameter("expiryDate")) == null ? "" : request.getParameter("expiryDate");

  Functions functions=new Functions();
  int pageno=1;
  int recordsPerPage=15;
  pageno= Functions.convertStringtoInt(request.getParameter("SPageno"),1);
  recordsPerPage=Functions.convertStringtoInt(request.getParameter("SRecords"),15);
   ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String whitelistEmail_whitelist_email = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_whitelist_email"))?rb1.getString("whitelistEmail_whitelist_email"): "WhiteList Email";
  String whitelistEmail_merchantid1 = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_merchantid1"))?rb1.getString("whitelistEmail_merchantid1"): "Merchant ID*";
  String whitelistEmail_account_id = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_account_id"))?rb1.getString("whitelistEmail_account_id"): "Account ID*";
  String whitelistEmail_email = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_email"))?rb1.getString("whitelistEmail_email"): "Email Address*";
  String whitelistEmail_holder = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_holder"))?rb1.getString("whitelistEmail_holder"): "Card Holder Name";
  String whitelistEmail_address = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_address"))?rb1.getString("whitelistEmail_address"): "IP Address";
  String whitelistEmail_expiry = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_expiry"))?rb1.getString("whitelistEmail_expiry"): "Expiry Date";
  String whitelistEmail_email1 = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_email1"))?rb1.getString("whitelistEmail_email1"): "WhiteList Email";
  String whitelistEmail_search = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_search"))?rb1.getString("whitelistEmail_search"): "Search";
  String whitelistEmail_list = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_list"))?rb1.getString("whitelistEmail_list"): "Whitelist Email List";
  String whitelistEmail_sr_no = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_sr_no"))?rb1.getString("whitelistEmail_sr_no"): "Sr No.";
  String whitelistEmail_email2 = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_email2"))?rb1.getString("whitelistEmail_email2"): "Email Address";
  String whitelistEmail_name = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_name"))?rb1.getString("whitelistEmail_name"): "Name";
  String whitelistEmail_ip = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_ip"))?rb1.getString("whitelistEmail_ip"): "IP Address";
  String whitelistEmail_id = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_id"))?rb1.getString("whitelistEmail_id"): "AccountID";
  String whitelistEmail_Delete = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_Delete"))?rb1.getString("whitelistEmail_Delete"): "Delete";
  String whitelistEmail_Showing_Page = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_Showing_Page"))?rb1.getString("whitelistEmail_Showing_Page"): "Showing Page";
  String whitelistEmail_of = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_of"))?rb1.getString("whitelistEmail_of"): "of";
  String whitelistEmail_records = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_records"))?rb1.getString("whitelistEmail_records"): "records";
  String whitelistEmail_Sorry = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_Sorry"))?rb1.getString("whitelistEmail_Sorry"): "Sorry";
  String whitelistEmail_no = StringUtils.isNotEmpty(rb1.getString("whitelistEmail_no"))?rb1.getString("whitelistEmail_no"): "No records found.";
  String whitelistEmail_page_no=StringUtils.isNotEmpty(rb1.getString("whitelistEmail_page_no"))?rb1.getString("whitelistEmail_page_no"):"Page number";
  String whitelistEmail_total_no_of_records=StringUtils.isNotEmpty(rb1.getString("whitelistEmail_total_no_of_records"))?rb1.getString("whitelistEmail_total_no_of_records"):"Total number of records";

%>
<body>
<div class="content-page">
  <div class="content" style="padding:0px 20px;margin: 0px;">
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=whitelistEmail_whitelist_email%></strong>
              </h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content">
              <div id="horizontal-form">
                <form action="/merchant/servlet/WhiteListEmail?ctoken=<%=ctoken%>" method="post" name="forms" >
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
                      <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;text-align: center">
                        <label><%=whitelistEmail_merchantid1%></label>
                      </div>
                      <div class="col-md-3">
                        <input type="text" name="merchantid" class="form-control"  value="<%=(String) session.getAttribute("merchantid")%>" disabled>
                      </div>
                    </div>
                  </div>

                 <%-- <div class="form-group col-md-12">
                    <div class="ui-widget">
                      <div class="col-sm-offset-2 col-md-3"
                           style="margin-top:10px;text-align: center">
                        <label><%=whitelistEmail_account_id%></label>
                      </div>
                      <div class="col-md-3">
                        <input name="accountid" id="accountid" value="<%=accountid%>"
                               class="form-control">
                      </div>
                    </div>
                  </div>--%>

                  <div class="form-group col-md-12">
                    <div class="ui-widget">
                      <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;text-align: center">
                        <label><%=whitelistEmail_email%></label>
                      </div>
                      <div class="col-md-3">
                        <input name="emailaddr" id="emailaddr" name="emailaddr" type="text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailAddr)%>" class="form-control">
                      </div>
                    </div>
                  </div>

                  <div class="form-group col-md-12">
                    <div class="ui-widget">
                      <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;text-align: center">
                        <label><%=whitelistEmail_holder%></label>
                      </div>
                      <div class="col-md-3">
                        <input id="name" name="name" type="text"  value="<%=name%>"class="form-control">
                      </div>
                    </div>
                  </div>

                  <div class="form-group col-md-12">
                    <div class="ui-widget">
                      <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;text-align: center">
                        <label><%=whitelistEmail_address%></label>
                      </div>
                      <div class="col-md-3">
                        <input id="ipAddress" name="ipAddress" type="text"  value="<%=ipAddress%>"class="form-control">
                      </div>
                    </div>
                  </div>

                  <div class="form-group col-md-12">
                    <div class="ui-widget">
                      <div class="col-sm-offset-2 col-md-3"
                           style="margin-top:10px;text-align: center">
                        <label><%=whitelistEmail_expiry%></label>
                      </div>
                      <div class="col-md-3">
                        <div class="CardExpiry"><input type="text" id="Expiry"  class="form-control" style="width:100%"  placeholder="MM/YY" name="expiryDate" onkeypress="return isNumberKey(event)" onblur="expiryCheck('Expiry','CardExpiry')" autocomplete="off" onkeyup="addSlash(event,'Expiry')" value=<%=expiryDate%>></div>
                      </div>
                    </div>
                  </div>

                  <div class="form-group col-md-12">
                    <p style="text-align: center;">
                      <%--<button type="submit" name="upload" value="upload" style="display: inline-block!important;" class="btn btn-default center-block"><i class="fa fa-clock-o"></i>
                        &nbsp;&nbsp;Upload</button>--%>
                        <button type="submit" name="upload" value="upload"
                                style="display: inline-block!important;"
                                class="btn btn-default center-block"><i class="fa fa-clock-o"></i>
                          &nbsp;&nbsp;<%=whitelistEmail_email1%>
                        </button>
                      <button type="submit" class="btn btn-default center-block" style="display: inline-block!important;"><i class="fa fa-clock-o"></i>
                        &nbsp;&nbsp;<%=whitelistEmail_search%></button>
                    </p>
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=whitelistEmail_list%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <%
                if(request.getAttribute("listOfEmail")!=null)
                {
                  List<WhitelistingDetailsVO> cList = (List<WhitelistingDetailsVO>)request.getAttribute("listOfEmail");
                  PaginationVO paginationVO = (PaginationVO)request.getAttribute("paginationVO");
                  paginationVO.setInputs("ctoken="+ctoken+paginationVO.getInputs());
                  if(cList.size()>0)
                  {
              %>
              <br>
              <form name="WhiteListEmail" action="/merchant/servlet/WhiteListEmail?ctoken=<%=ctoken%>" method="post">
              <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr style="color: white;">
                  <th valign="middle" align="center" style="text-align: center"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                  <th valign="middle" align="center" style="text-align: center"><%=whitelistEmail_sr_no%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=whitelistEmail_email2%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=whitelistEmail_name%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=whitelistEmail_ip%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=whitelistEmail_id%></th>
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
                  int srno=pos+(pageno-1)*recordsPerPage;
                    String email="";
                    String name1="";
                    String maskingName="";
                    String ipAddress1="";
                    if(functions.isValueNull(whitelistingDetailsVO.getEmail())){
                        email=whitelistingDetailsVO.getEmail();
                    }
                    else {
                    email="-";
                    }
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
                    out.println("<td align=center "+style+">"+email+"</td>");
                    out.println("<td align=center "+style+">");
                     if(functions.isValueNull(name1) || !"".equals(name1))
                    out.println("<label id=\"name"+pos+"\">"+maskingName+"</label><span style=\"z-index: 2;font-size:12px;margin-top:5px;margin-right:20px; float:right;\" toggle=\"#password-field\" class=\"far fa-eye-slash toggle-password\" onmousedown=\"unMasking('showHidepass1"+pos+"','name"+pos+"','"+name1+"')\" onmouseup=\"masking('showHidepass1"+pos+"','name"+pos+"','"+maskingName+"')\" id=\"showHidepass1"+pos+"\" >");
                    out.println("</td>");
                    out.println("<td align=center "+style+">"+ipAddress1+"</td>");
                    out.println("<td align=center "+style+">"+whitelistingDetailsVO.getAccountid()+"</td>");
                    out.println("</tr>");
                    pos++;
                  }
                  %>
                </table>
                <table width="100%">
                  <thead>
                  <tr>
                    <td width="15%" align="center">

                      <input type="hidden" name="delete" value="delete"><input type="button" name="delete" class="btn btn-default gotoauto" value="<%=whitelistEmail_Delete%>" onclick="return Delete();">
                    </td>
                  </tr>
                  </thead>
                </table>
              </form>
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
                  <div id="showingid"><strong><%=whitelistEmail_page_no%> <%=paginationVO.getPageNo()%> <%=whitelistEmail_of%>  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                  <div id="showingid"><strong><%=whitelistEmail_total_no_of_records%>   <%=paginationVO.getTotalRecords()%> </strong></div>
                  <jsp:include page="page.jsp" flush="true">
                    <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                    <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                    <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/><jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                    <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
                    <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                    <jsp:param name="orderby" value=""/>
                  </jsp:include>
            </div>
          </div>
          <%
              }
              else
              {
                out.println(Functions.NewShowConfirmation1(whitelistEmail_Sorry, whitelistEmail_no));
              }
            }
            else
            {
              out.println(Functions.NewShowConfirmation1(whitelistEmail_Sorry, whitelistEmail_no));
            }
          %>
        </div>
      </div>
    </div>
  </div>
</div>
</div>
</body>
</html>