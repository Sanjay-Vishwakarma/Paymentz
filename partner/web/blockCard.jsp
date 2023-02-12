<%--
  Created by IntelliJ IDEA.
  User: Namrata Bari
  Date: 6/11/2020
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
        alert("Please select remark");
        return false;
      }
      else
      {
        return true;
      }
    }
  </script>
</head>
<body>
  <%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  session.setAttribute("submit","Black List");
  String str = "";
  if (partner.isLoggedInPartner(session))
  {
  String firstSix = request.getParameter("firstsix")==null?"":request.getParameter("firstsix");
  String lastFour = request.getParameter("lastfour")==null?"":request.getParameter("lastfour");
  String reason = request.getParameter("reason")==null?"":request.getParameter("reason");
  String role=(String)session.getAttribute("role");
  String username=(String)session.getAttribute("username");
  String actionExecutorId=(String)session.getAttribute("merchantid");
  String actionExecutorName=role+"-"+username;
  String remark=request.getParameter("remark");


    if(firstSix!=null)str = str + "&firstSix=" + firstSix;
    if(reason!=null)str = str + "&reason=" + reason;
    if(lastFour!=null)str = str + "&lastFour=" + lastFour;
  // if(remark!=null)str = str + "&remark=" + remark;


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
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Blocked Card</strong>
              </h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content">
              <div id="horizontal-form">
                <form action="/partner/net/BlockCardDetails?ctoken=<%=ctoken%>" method="post" onsubmit=""  name="forms" >
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
                          <label>First_SIX*</label>
                        </div>
                        <div class="col-md-3">
                           <input size="8" maxlength="6" type="text"  class="form-control" name="firstsix"  value="<%=firstSix%>">
                        </div>
                      </div>
                    </div>
                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label>Last_Four*</label>
                        </div>
                        <div class="col-md-3">
                         <input maxlength="4" size="6" type="text" name="lastfour" class="form-control" value="<%=lastFour%>">
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
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label>Remark</label>
                        </div>
                        <div class="col-md-2">
                          <select name="remark" id="remark" class="form-control" style="width:265px;" value="<%=remark%>">
                            <option value="">All</option>
                            <option value="Chargeback Received">Chargeback Received</option>
                            <option value="Fraud Received">Fraud Received</option>
                            <option value="Stolen Card">Stolen Card</option>
                          </select>
                        </div>
                      </div>
                    </div>


                            <div class="form-group col-md-12">
                      <p style="text-align: center;">
                        <button type="submit" name="upload" value="upload" onclick="return checkremark();"
                                style="display: inline-block!important;"
                                class="btn btn-default center-block"
                                style="background-color: #7eccad"><i class="fa fa-clock-o"></i>
                          &nbsp;&nbsp;Block Card
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

              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Blocked Card List</strong></h2>
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
                <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead>
                  <tr style="color: white; background-color:#7eccad">
                    <th valign="middle" align="center" style="text-align: center">First_SIX</th>
                    <th valign="middle" align="center" style="text-align: center">Last_Four</th>
                    <th valign="middle" align="center" style="text-align: center">Blacklist Reason</th>
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
                  if(functions.isValueNull(blacklistVO.getBlacklistCardReason()))
                  {
                  reason=blacklistVO.getBlacklistCardReason();
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
                  remark="-";
                  }


                    out.println("<tr>");
                    out.println("<td align=center "+style+">"+blacklistVO.getFirstSix()+"</td>");
                    out.println("<td align=center "+style+">"+blacklistVO.getLastFour()+"</td>");
                    out.println("<td align=center "+style+">"+reason+"</td>");
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
            <jsp:param name="page" value="BlockCardDetails"/>
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