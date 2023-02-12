<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.WhitelistingDetailsVO" %>
<%@ page import="java.util.List" %>
<%@ page import="com.manager.vo.PaginationVO" %>
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
  <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
  <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>

  <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
  <script src="/merchant/NewCss/js/jquery-ui.min.js"></script>
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
  <script type="text/javascript">
    $(document).ready(function(){
      $("#sameBin").on("click", function () {
        var startBin
        if ($(this).is(":checked")) {
          binStart = $('[name="startBin"]').val();
          $("#endBin").val(binStart ).prop("readonly", true);
        } else {
          $("#endBin").val("").prop("readonly", false);
        }
      });
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
        document.WhiteListBin.submit();
      }
    }
  </script>
</head>
<body>
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  String startBin=request.getParameter("startBin")==null?"":request.getParameter("startBin");
  String accountid=request.getParameter("accountid")==null?"":request.getParameter("accountid");
  String endBin=request.getParameter("endBin")==null?"":request.getParameter("endBin");
  Functions functions=new Functions();
  int pageno=1;
  int recordsPerPage=15;
  pageno= Functions.convertStringtoInt(request.getParameter("SPageno"),1);
  recordsPerPage=Functions.convertStringtoInt(request.getParameter("SRecords"),15);

  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String whitelistBin_white_list = StringUtils.isNotEmpty(rb1.getString("whitelistBin_white_list"))?rb1.getString("whitelistBin_white_list"): "WhiteList Bin";
  String whitelistBin_id1 = StringUtils.isNotEmpty(rb1.getString("whitelistBin_id1"))?rb1.getString("whitelistBin_id1"): "Merchant ID*";
  String whitelistBin_account_id = StringUtils.isNotEmpty(rb1.getString("whitelistBin_account_id"))?rb1.getString("whitelistBin_account_id"): "Account ID*";
  String whitelistBin_start = StringUtils.isNotEmpty(rb1.getString("whitelistBin_start"))?rb1.getString("whitelistBin_start"): "Start Bin*";
  String whitelistBin_single = StringUtils.isNotEmpty(rb1.getString("whitelistBin_single"))?rb1.getString("whitelistBin_single"): "Single Bin*";
  String whitelistBin_start_card = StringUtils.isNotEmpty(rb1.getString("whitelistBin_start_card"))?rb1.getString("whitelistBin_start_card"): "Start Card";
  String whitelistBin_end = StringUtils.isNotEmpty(rb1.getString("whitelistBin_end"))?rb1.getString("whitelistBin_end"): "End Bin*";
  String whitelistBin_end_card = StringUtils.isNotEmpty(rb1.getString("whitelistBin_end_card"))?rb1.getString("whitelistBin_end_card"): "End Card";
  String whitelistBin_white_bin = StringUtils.isNotEmpty(rb1.getString("whitelistBin_white_bin"))?rb1.getString("whitelistBin_white_bin"): "WhiteList Bin";
  String whitelistBin_white_search = StringUtils.isNotEmpty(rb1.getString("whitelistBin_white_search"))?rb1.getString("whitelistBin_white_search"): "Search";
  String whitelistBin_white_range = StringUtils.isNotEmpty(rb1.getString("whitelistBin_white_range"))?rb1.getString("whitelistBin_white_range"): "Whitelist Bin Range List";
  String whitelistBin_sr_no = StringUtils.isNotEmpty(rb1.getString("whitelistBin_sr_no"))?rb1.getString("whitelistBin_sr_no"): "Sr No.";
  String whitelistBin_start1 = StringUtils.isNotEmpty(rb1.getString("whitelistBin_start1"))?rb1.getString("whitelistBin_start1"): "Start Bin - Start Card";
  String whitelistBin_end_card1 = StringUtils.isNotEmpty(rb1.getString("whitelistBin_end_card1"))?rb1.getString("whitelistBin_end_card1"): "End Bin - End Card";
  String whitelistBin_white_accountid1 = StringUtils.isNotEmpty(rb1.getString("whitelistBin_white_accountid1"))?rb1.getString("whitelistBin_white_accountid1"): "AccountID";
  String whitelistBin_sorry = StringUtils.isNotEmpty(rb1.getString("whitelistBin_sorry"))?rb1.getString("whitelistBin_sorry"): "Sorry";
  String whitelistBin_no_record = StringUtils.isNotEmpty(rb1.getString("whitelistBin_no_record"))?rb1.getString("whitelistBin_no_record"): "No records found.";
  String whitelistBin_Delete = StringUtils.isNotEmpty(rb1.getString("whitelistBin_Delete"))?rb1.getString("whitelistBin_Delete"): "Delete";
  String whitelistBin_Showing_Page = StringUtils.isNotEmpty(rb1.getString("whitelistBin_Showing_Page"))?rb1.getString("whitelistBin_Showing_Page"): "Showing Page";
  String whitelistBin_of = StringUtils.isNotEmpty(rb1.getString("whitelistBin_of"))?rb1.getString("whitelistBin_of"): "of";
  String whitelistBin_records = StringUtils.isNotEmpty(rb1.getString("whitelistBin_records"))?rb1.getString("whitelistBin_records"): "records";
  String whitelistBin_page_no=StringUtils.isNotEmpty(rb1.getString("whitelistBin_page_no"))?rb1.getString("whitelistBin_page_no"):"Page number";
  String whitelistBin_total_no_of_records=StringUtils.isNotEmpty(rb1.getString("whitelistBin_total_no_of_records"))?rb1.getString("whitelistBin_total_no_of_records"):"Total number of records";


%>
<div class="content-page">
  <div class="content" style="padding: 0px 20px;margin: 0px;">
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=whitelistBin_white_list%></strong>
              </h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content">
              <div id="horizontal-form">
                <form action="/merchant/servlet/WhiteListBin?ctoken=<%=ctoken%>" method="post" name="forms" >
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
                    String startCard="0000000000";
                    String endCard="9999999999";
                  %>
                  <div class="form-group col-md-1">
                  </div>
                  <div class="form-group col-md-12">
                    <div class="ui-widget">
                      <div class="col-sm-offset-2 col-md-3" style="margin-top:10px; text-align: center">
                        <label><%=whitelistBin_id1%></label>
                      </div>
                      <div class="col-md-3">
                        <input type="text" name="merchantid" class="form-control"  value="<%=(String) session.getAttribute("merchantid")%>" disabled>
                      </div>
                    </div>
                  </div>

                  <div class="form-group col-md-12">
                    <div class="ui-widget">
                      <div class="col-sm-offset-2 col-md-3"
                           style="margin-top:10px; text-align: center">
                        <label><%=whitelistBin_account_id%></label>
                      </div>
                      <div class="col-md-3">
                        <input name="accountid" id="accountid" value="<%=accountid%>"
                               class="form-control">
                      </div>
                    </div>
                  </div>

                  <div class="form-group col-md-12">
                    <div class="ui-widget">
                      <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;text-align: center">
                        <label><%=whitelistBin_start%></label>
                      </div>
                      <div class="col-md-2">
                        <input id="startBin" name="startBin" maxlength="6" size="6" value="<%=startBin%>"class="form-control">
                      </div>
                      <div class="col-md-2">
                        <input type="checkbox" id="sameBin" style="width:40px"><%=whitelistBin_single%>
                      </div>
                    </div>
                  </div>

                  <div class="form-group col-md-12">
                    <div class="ui-widget">
                      <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;text-align: center">
                        <label><%=whitelistBin_start_card%></label>
                      </div>
                      <div class="col-md-2">
                        <input type=text  id="startCard" name="startCard" maxlength="13" size="6"  class="form-control"  value="<%=startCard%>">
                      </div>
                    </div>
                  </div>

                  <div class="form-group col-md-12">
                    <div class="ui-widget">
                      <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;text-align: center">
                        <label><%=whitelistBin_end%></label>
                      </div>
                      <div class="col-md-2">
                        <input id="endBin" name="endBin" maxlength="6" size="6" value="<%=endBin%>"class="form-control">
                      </div>
                    </div>
                  </div>

                  <div class="form-group col-md-12">
                    <div class="ui-widget">
                      <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;text-align: center">
                        <label><%=whitelistBin_end_card%></label>
                      </div>
                      <div class="col-md-2">
                        <input type=text  id="endCard" name="endCard" maxlength="13" size="6"  class="form-control"  value="<%=endCard%>">
                      </div>
                    </div>
                  </div>

                  <div class="form-group col-md-12">
                    <p style="text-align: center;">
                        <button type="submit" name="upload" value="upload"
                                style="display: inline-block!important;"
                                class="btn btn-default center-block"
                                style="background-color: #7eccad"><i class="fa fa-clock-o"></i>
                          &nbsp;&nbsp;<%=whitelistBin_white_bin%>
                        </button>
                      <button type="submit" class="btn btn-default center-block" style="display: inline-block!important;"><i class="fa fa-clock-o"></i>
                        &nbsp;&nbsp;<%=whitelistBin_white_search%></button>
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=whitelistBin_white_range%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <%
                if(request.getAttribute("listOfBin")!=null)
                {
                  List<WhitelistingDetailsVO> cList = (List<WhitelistingDetailsVO>)request.getAttribute("listOfBin");
                  PaginationVO paginationVO = (PaginationVO)request.getAttribute("paginationVO");
                  paginationVO.setInputs("ctoken="+ctoken+paginationVO.getInputs());
                  if(cList.size()>0)
                  {
              %>
              <br>
              <form name="WhiteListBin" action="/merchant/servlet/WhiteListBin?ctoken=<%=ctoken%>" method="post">
              <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr style="color: white;">
                  <th valign="middle" align="center" style="text-align: center"><input type="checkbox" onclick="ToggleAll(this);" name="alltrans"></th>
                  <th valign="middle" align="center" style="text-align: center"><%=whitelistBin_sr_no%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=whitelistBin_start1%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=whitelistBin_end_card1%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=whitelistBin_white_accountid1%></th>
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
                    out.println("<tr>");
                    out.println("<td valign=\"middle\" data-label=\"id\" align=\"center\" "+style+">&nbsp;<input type=\"checkbox\" name=\"id\" value="+whitelistingDetailsVO.getId()+"></td>");
                    out.println("<td align=center "+style+">&nbsp;"+srno+"</td>");
                    out.println("<td align=center "+style+">"+whitelistingDetailsVO.getStartBin()+" - "+whitelistingDetailsVO.getStartCard()+"</td>");
                    out.println("<td align=center "+style+">"+whitelistingDetailsVO.getEndBin()+" - "+whitelistingDetailsVO.getEndCard()+"</td>");
                    out.println("<td align=center "+style+">"+whitelistingDetailsVO.getAccountid()+"</td>");
                    out.println("</tr>");
                    pos++;
                  }
                  %>
              </table>
                <table>
                  <tr>
                    <td width="15%" align="center">
                      <input type="hidden" name="delete" value="delete"><input type="button" name="delete" class="btn btn-default gotoauto" value="<%=whitelistBin_Delete%>" onclick="return Delete();">
                    </td>
                  </tr>
                </table>
              </form>
              <%--<table width="100%">
                <thead>
                <tr>
                  <td width="15%" align="center">
                    <input type="hidden" name="accountid" value="<%=accountid%>">
                    <input type="hidden" name="startBin" value="<%=startBin%>">
                    <input type="hidden" name="endBin" value="<%=endBin%>">
                    <input type="hidden" name="delete" value="delete"><input type="button" name="delete" class="btn btn-default center-block" value="Delete" onclick="return Delete();">
                  </td>
                </tr>
                </thead>
              </table>
              </form>--%>
              <% int TotalPageNo;
                if(paginationVO.getTotalRecords()%paginationVO.getRecordsPerPage()!=0)
                {
                  TotalPageNo=paginationVO.getTotalRecords()/paginationVO.getRecordsPerPage()+1;
                }
                else
                {
                  TotalPageNo=paginationVO.getTotalRecords()/paginationVO.getRecordsPerPage();
                }
                %>
              <div id="showingid"><strong><%=whitelistBin_page_no%> <%=paginationVO.getPageNo()%> <%=whitelistBin_of%> <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
              <div id="showingid"><strong><%=whitelistBin_total_no_of_records%>   <%=paginationVO.getTotalRecords()%> </strong></div>
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
          <%
              }
              else
              {
                out.println(Functions.NewShowConfirmation1(whitelistBin_sorry, whitelistBin_no_record));
              }
            }
            else
            {
              out.println(Functions.NewShowConfirmation1(whitelistBin_sorry, whitelistBin_no_record));
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
