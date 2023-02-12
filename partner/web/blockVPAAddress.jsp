<%--
  Created by IntelliJ IDEA.
  User: Kalyani
  Date: 28/06/2021
  Time: 2:13 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.BlacklistVO" %>
<%@ page import="java.util.*" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.vo.PaginationVO" %>
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

      tr:nth-child(odd), tr:nth-child(even) {background: #ffffff;}
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

    tr:nth-child(odd) {background: #F9F9F9;}
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
      padding-top: 6px;
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

  <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
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
    function checkremark()
    {
      var value=document.getElementById("remark").value;
      console.log("value->"+value);
      if(value==="")
      {
        alert("Please select Remark");
        return false;
      }
      else
      {
        return true;
      }
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
      if (confirm("Do you really want to unblock all selected Data."))
      {
        document.BlockEmail.submit();
      }
    }
  </script>
</head>
<body>
  <%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  session.setAttribute("submit","Block VPA Address");
  String str = "";
  if (partner.isLoggedInPartner(session))
  {
  String vpaAddress = request.getParameter("vpaAddress")==null?"":request.getParameter("vpaAddress");
  String reason = request.getParameter("reason")==null?"":request.getParameter("reason");
  String actionExecutorId="";
  String actionExecutorName="";


    if(vpaAddress!=null)str = str + "&vpaAddress=" + vpaAddress;
    if(reason!=null)str = str + "&reason=" + reason;


  str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
  int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
  str = str + "&SRecords=" + pagerecords;

%>
<body>
<div class="content-page">
  <div class="content" style="padding:0px 20px ;margin: 0px;">
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Block VPA Address</strong>
              </h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content">
              <div id="horizontal-form">
                <form action="/partner/net/BlockVPAAddress?ctoken=<%=ctoken%>" method="post" onsubmit=""  name="forms" >
                  <div align="center">
                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                    <%
                      PartnerFunctions functions = new PartnerFunctions();
                      String message = (String) request.getAttribute("error");
                      String sMessage = (String) request.getAttribute("message");
                      if (functions.isValueNull(message))
                      {
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                      }
                      if(functions.isValueNull(sMessage))
                      {
                        out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+sMessage+"</h5>");
                      }

                    %>
                    <div class="form-group col-md-1">
                    </div>
                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label>VPA Address</label>
                        </div>
                        <div class="col-md-3">
                          <input size="8" maxlength="24" type="text"  class="form-control" name="vpaAddress"  value="<%=vpaAddress%>">
                        </div>
                      </div>
                    </div>
                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label>Blacklist Reason*</label>
                        </div>
                        <div class="col-md-3">
                          <input  type="text" name="reason" class="form-control" value="<%=reason%>">
                        </div>
                      </div>
                    </div>

                    <div class="form-group col-md-12">
                      <p style="text-align: center;">
                        <button type="submit" name="upload" value="upload" onclick="return checkremark();"
                                style="display: inline-block!important;"
                                class="btn btn-default center-block"
                                style="background-color: #7eccad"><i class="fa fa-clock-o"></i>
                          &nbsp;&nbsp;Block VPA Address
                        </button>
                        <button type="submit" class="btn btn-default center-block"
                                style="display: inline-block!important;"><i
                                class="fa fa-clock-o"></i>
                          &nbsp;&nbsp;Search
                        </button>
                      </p>
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

              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Blocked VPA Address</strong></h2>
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
                  PaginationVO paginationVO=(PaginationVO) request.getAttribute("paginationVO");
                  List<BlacklistVO> cList = (List<BlacklistVO>)request.getAttribute("listofcard");
                  paginationVO.setInputs("ctoken=" + ctoken+paginationVO.getInputs());

                  if(cList.size()>0 )
                  {
              %>
              <br>
              <form action="/partner/net/BlockVPADelete?ctoken=<%=ctoken%>" method="POST" name="BlockEmail">
                <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead>
                  <tr style="color: white; background-color:#7eccad">
                    <th valign="middle" align="center" style="text-align: center"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                    <th valign="middle" align="center" style="text-align: center">VPA Address</th>
                    <th valign="middle" align="center" style="text-align: center">Blacklist Reason</th>
                    <th valign="middle" align="center" style="text-align: center">Timestamp</th>
                    <th valign="middle" align="center" style="text-align: center">Remark</th>
                    <th valign="middle" align="center" style="text-align: center">Action Executor Id</th>
                    <th valign="middle" align="center" style="text-align: center">Action Executor Name</th>

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
                  pos = (pagerecords * (pageno-1))+1;
                  String Reason="";
                  String remark="";
                  for(BlacklistVO blacklistVO : cList)
                  {
                  if(functions.isValueNull(blacklistVO.getActionExecutorId()))
                  {
                  actionExecutorId=blacklistVO.getActionExecutorId();
                  }
                  else
                  {
                  actionExecutorId="-";
                  }
                  if(functions.isValueNull(blacklistVO.getActionExecutorName()))
                  {
                  actionExecutorName=blacklistVO.getActionExecutorName();
                  }
                  else
                  {
                  actionExecutorName="-";
                  }
                  if(functions.isValueNull(blacklistVO.getBlacklistReason()))
                  {
                  reason=blacklistVO.getBlacklistReason();
                  }
                  else
                  {
                  reason="";
                  }
                  if(functions.isValueNull(blacklistVO.getRemark()))
                  {
                  remark=blacklistVO.getRemark();
                  }
                  else
                  {
                  remark="";
                  }
                    out.println("<tr>");
                    out.println("<td valign=\"middle\" data-label=\"id\" align=\"center\" "+style+">&nbsp;<input type=\"checkbox\" name=\"id\" value="+blacklistVO.getId()+"></td>");
                    out.println("<td align=center "+style+">"+blacklistVO.getVpaAddress()+"</td>");
                    out.println("<td align=center "+style+">"+reason+"</td>");
                    out.println("<td align=center "+style+">"+blacklistVO.getTimestamp()+"</td>");
                    out.println("<td align=center "+style+">"+remark+"</td>");
                    out.println("<td align=center "+style+">"+actionExecutorId+"</td>");
                    out.println("<td align=center "+style+">"+actionExecutorName+"</td>");
                    out.println("</form></td>");
                    out.println("</tr>");
                    pos++;
                  }
                  %>
            </div>
          </div>
          </table>
          <table width="100%">
            <thead>
            <tr>
              <td width="15%" align="center">
                <input type="hidden" name="delete" value="delete"><input type="button" name="delete" class="btn btn-default gotoauto" value="Unblock" onclick="return Delete();">
              </td>
            </tr>
            </thead>
          </table>
          </form>
          <%
            int TotalPageNo;
            if(paginationVO.getTotalRecords()%pagerecords!=0)
            {
              TotalPageNo=paginationVO.getTotalRecords()/pagerecords+1;
            }
            else
            {
              TotalPageNo=paginationVO.getTotalRecords()/pagerecords;
            }
          %>
          <div id="showingid"><strong>Page number  <%=pageno%>  of  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
          <div id="showingid"><strong>Total number of records  <%=paginationVO.getTotalRecords()%> </strong></div>
          <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
            <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
            <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
            <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
            <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
            <jsp:param name="orderby" value=""/>
            <jsp:param name="page" value="BlockVPAAddress"/>
          </jsp:include>
          <%
              }
              else
              {
                out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
              }
            }
            else
            {
              out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
            }
          %>
        </div>
      </div>
    </div>
  </div>
</div>
</div>
<%
  }
%>
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
