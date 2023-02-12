<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.BlacklistVO" %>
<%@ page import="java.util.*" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
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

    #ui-id-2
    {
      overflow: auto;
      max-height: 350px;
    }
  </style>
  <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
  <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>

  <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <script language="javascript">
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
      if (confirm("Do you really want to Delete all selected Data."))
      {
        document.BinDelete.submit();
      }
    }
  </script>
  <script>
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
</head>
<body>
<%
 // Functions functions = new Functions();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  session.setAttribute("submit","Black List");
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
    String startBin=nullToStr(request.getParameter("startBin"));
    String endBin=nullToStr(request.getParameter("endBin"));
    String accountid=nullToStr(request.getParameter("accountid"));
    String partnerid = session.getAttribute("partnerId").toString();

    String str="";

    if(memberid!=null)str = str + "&memberid=" + memberid;
    if(accountid!=null)str = str + "&accountid=" + accountid;
    if(startBin!=null)str = str + "&startBin=" + startBin;
    if(endBin!=null)str = str + "&endBin=" + endBin;
    if(pid!=null)str = pid + "&pid=" + pid;

    str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
    str = str + "&SRecords=" + pagerecords;
%>
<div class="content-page">
  <div class="content" style="padding:0px 20px ;margin: 0px;">
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Blocked Bin</strong>
              </h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content">
              <div id="horizontal-form">
                <form action="/partner/net/BlockBin?ctoken=<%=ctoken%>" method="post" name="forms" >
                  <div align="center">
                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                    <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                    <%
                      PartnerFunctions functions = new PartnerFunctions();
                      String message = (String) request.getAttribute("error");
                      String msg = (String) request.getAttribute("msg");
                      if (functions.isValueNull(message))
                      {
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                      }
                      if (functions.isValueNull(msg))
                      {
                        out.println(Functions.NewShowConfirmation1("Sorry", msg));
                      }
                    %>
                    <div class="form-group col-md-1">
                    </div>
                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label>Partner ID</label>
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
                          <label>Merchant ID*</label>
                        </div>
                        <div class="col-md-3" >
                          <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                        </div>
                      </div>
                    </div>

                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label>Account ID*</label>
                        </div>
                        <div class="col-md-3">
                          <input name="accountid" id="account_id"value="<%=accountid%>"  class="form-control">
                        </div>
                      </div>
                    </div>

                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label>Start Bin*</label>
                        </div>
                        <div class="col-md-2">
                          <input id="startBin" name="startBin" maxlength="6" size="6" value="<%=startBin%>"class="form-control">
                        </div>
                        <div class="col-md-2">
                          <input type="checkbox" id="sameBin" style="width:40px">Single Bin
                        </div>
                      </div>
                    </div>

                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label>End Bin*</label>
                        </div>
                        <div class="col-md-2">
                          <input id="endBin" name="endBin" maxlength="6" size="6" value="<%=endBin%>"class="form-control">
                        </div>
                      </div>
                    </div>

                    <div class="form-group col-md-12">
                      <p style="text-align: center;">
                        <button type="submit" name="upload" value="upload"
                                style="display: inline-block!important;"
                                class="btn btn-default center-block"
                                style="background-color: #7eccad"><i class="fa fa-clock-o"></i>
                          &nbsp;&nbsp;Block Bin
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Blocked Bin Range List</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <%
                StringBuffer requestParameter = new StringBuffer();
                Enumeration<String> stringEnumeration = request.getParameterNames();
                while(stringEnumeration.hasMoreElements())
                {
                  String name=stringEnumeration.nextElement();
                  if("SPageno".equals(name) || "SRecords".equals(name))
                  {

                  }
                  else
                    requestParameter.append("<input type='hidden' name='"+name+"' value=\""+request.getParameter(name)+"\"/>");
                }
                requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");

                int records=0;
                int totalrecords=0;
                String currentblock=request.getParameter("currentblock");
                if(currentblock==null)
                  currentblock="1";
                try
                {
                  records= (int) request.getAttribute("records");
                  totalrecords=(int)request.getAttribute("totalrecords");
                }
                catch(Exception ex)
                {
                }

                if(request.getAttribute("listOfBin")!=null)
                {
                  List<BlacklistVO> cList = (List<BlacklistVO>)request.getAttribute("listOfBin");
                  if(cList.size()>0 && records > 0)
                  {
              %>
              <br>
              <form action="/partner/net/BlackBinListDelete?ctoken=<%=ctoken%>" method="POST" name="BinDelete">
              <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr style="color: white; background-color:#7eccad">
                  <th valign="middle" align="center" style="text-align: center"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                  <th valign="middle" align="center" style="text-align: center">Member ID</th>
                  <th valign="middle" align="center" style="text-align: center">AccountID</th>
                  <th valign="middle" align="center" style="text-align: center">Start Bin</th>
                  <th valign="middle" align="center" style="text-align: center">End Bin</th>
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
                  else{
                  actionExecutorName="-";
                  }
                    out.println("<tr>");
                    out.println("<td valign=\"middle\" data-label=\"id\" align=\"center\" "+style+">&nbsp;<input type=\"checkbox\" name=\"id\" value="+blacklistVO.getId()+"></td>");
                    out.println("<td align=center "+style+">"+blacklistVO.getMemberId()+"<input type=\"hidden\" name=\"memberid_"+blacklistVO.getId()+"\" value="+blacklistVO.getMemberId()+"></td>");
                    out.println("<td align=center "+style+">"+blacklistVO.getAccountId()+"</td>");
                    out.println("<td align=center "+style+">"+blacklistVO.getBinStart()+"</td>");
                    out.println("<td align=center "+style+">"+blacklistVO.getBinEnd()+"</td>");
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
                <input type="hidden" name="delete" value="delete"><input type="button" name="delete" class="btn btn-default gotoauto" value="Delete" onclick="return Delete();">
              </td>
            </tr>
            </thead>
          </table>
          </form>
          <% int TotalPageNo;
            if(totalrecords%pagerecords!=0)
            {
              TotalPageNo=totalrecords/pagerecords+1;
            }
            else
            {
              TotalPageNo=totalrecords/pagerecords;
            }
          %>
          <div id="showingid"><strong>Page number  <%=pageno%>  of  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
          <div id="showingid"><strong>Total number of records  <%=totalrecords%> </strong></div>
          <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
            <jsp:param name="numrows" value="<%=pagerecords%>"/>
            <jsp:param name="pageno" value="<%=pageno%>"/>
            <jsp:param name="str" value="<%=str%>"/>
            <jsp:param name="page" value="BlockBin"/>
            <jsp:param name="currentblock" value="<%=currentblock%>"/>
            <jsp:param name="orderby" value=""/>
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