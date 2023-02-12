<%@ page import="com.directi.pg.Functions" %>
<%@ include file="Top.jsp"%>
<%@ include file="functions.jsp"%>
<%@ page import="org.owasp.esapi.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 1/5/2016
  Time: 12:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  private String memberChildList_contact_mail;
%><%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
%>
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
  <script>
    $(document).ready(function(){
      $('#myTable').dataTable();
    });
  </script>
  <script language="javascript">
    function DoReverse()
    {
      if (confirm("Do you really want to Delete this User."))
      {
        return true;
      }
      else
        return false;
    }
  </script>
  <title><%=company%> Merchant Management > User Management </title>
</head>
<body>
  <%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  session.setAttribute("submit","User Management");

  int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
  int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

     ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String memberChildList_merchant_user_management = rb1.getString("memberChildList_merchant_user_management");
    String memberChildList_new_user = rb1.getString("memberChildList_new_user");
    String memberChildList_merchant_id = rb1.getString("memberChildList_merchant_id");
    String memberChildList_search = rb1.getString("memberChildList_search");
    String memberChildList_merchant_master_list = rb1.getString("memberChildList_merchant_master_list");
    String memberChildList_sr_no = rb1.getString("memberChildList_sr_no");
    String memberChildList_user_name = rb1.getString("memberChildList_user_name");
    String memberChildList_contact_mail = rb1.getString("memberChildList_contact_mail");
    String memberChildList_action = rb1.getString("memberChildList_action");
    String memberChildList_sorry = rb1.getString("memberChildList_sorry");
    String memberChildList_no_records = rb1.getString("memberChildList_no_records");
   // String memberChildList_telno  =rb1.getString("ccpaymentPage_phone_number");
%>
<body>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=memberChildList_merchant_user_management%></strong>
              </h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="pull-right">
              <div class="btn-group">
                <form action="/merchant/memberChildSignup.jsp?ctoken=<%=ctoken%>" method="POST">
                  <button type="hidden" name="submit" value="User Management" class="btn btn-default" style="display: -webkit-box; margin-right: 15px;"><i class="fa fa-user" aria-hidden="true"></i>&nbsp;&nbsp;<%=memberChildList_new_user%></button>
                </form>
              </div>
            </div>
            <div class="widget-content">
              <div id="horizontal-form">
                <form action="/merchant/servlet/MemberUserList?ctoken=<%=ctoken%>" method="post" name="forms" >

                  <input type="hidden" value="<%=ctoken%>" name="ctoken">

                  <%
                    if(request.getAttribute("error")!=null)
                    {
                      String message = (String) request.getAttribute("error");
                      if(message != null)
                        out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                    }
                  %>
                  
                  <div class="form-group">
                    <label class="col-sm-2 control-label"><%=memberChildList_merchant_id%></label>
                    <div class="col-sm-4 has-feedback">
                      <input type="text" name="merchantid" class="form-control"  value="<%=(String) session.getAttribute("merchantid")%>" disabled>
                    </div>
                    <div class="form-group col-sm-2"></div>
                    <div class="form-group col-sm-4">
                      <button type="submit" class="btn btn-default" <%--style="margin-left:300px;padding-right: 50px;"--%>>
                        <i class="fa fa-clock-o"></i>
                        &nbsp;&nbsp;<%=memberChildList_search%>
                      </button>
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=memberChildList_merchant_master_list%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <%
                int records = 0;
                Hashtable temphash=null;
                Hashtable detailHash = (Hashtable)request.getAttribute("detailHash");
                if(detailHash!=null && (detailHash.size()!=0 && detailHash.size()!=1))
                {
                  detailHash = (Hashtable)request.getAttribute("detailHash");
                  records=Integer.parseInt((String) detailHash.get("records"));
                }
                if(records>0)
                {
              %>
              <br>
              <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr style="color: white;">
                  <th valign="middle" align="center" style="text-align: center"><%=memberChildList_sr_no%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=memberChildList_user_name%></th>
                  <th valign="middle" align="center" style="text-align: center"><%=memberChildList_contact_mail%></th>
                  <th valign="middle" align="center" style="text-align: center">Phone Number</th>
                  <th valign="middle" align="center" colspan="5" style="text-align: center"><%=memberChildList_action%></th>
                </tr>
                </thead>
                <tbody>
                <%
                  Functions functions = new Functions();
                  StringBuffer requestParameter= new StringBuffer();
                  Enumeration<String> requestName=request.getParameterNames();
                  while(requestName.hasMoreElements())
                  {
                    String name=requestName.nextElement();
                    if("SPageno".equals(name) || "SRecords".equals(name))
                    {

                    }
                    else
                      requestParameter.append("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                  }
                  requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                  requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");
                  String style="class=td1";
                  String ext="light";
                  for(int i = 1;i<=records;i++)
                  {
                    String id=Integer.toString(i);

                    int srno=i+ records;

                    if(i%2==0)
                    {
                      style="class=tr1";
                      ext="dark";
                    }
                    else
                    {
                      style="class=tr0";
                      ext="light";
                    }

                    temphash=(Hashtable)detailHash.get(id);
                    out.println("<tr>");
                    out.println("<td align=\"center\" data-label=\"Sr no\">&nbsp;"+i+ "</td>");
                    out.println("<td align=\"center\" data-label=\"Login\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("login"))+"<input type=\"hidden\" name=\"login\" value=\""+temphash.get("login")+"\"></td>");
                    out.println("<td align=\"center\" data-label=\"Contact Email\">&nbsp;"+functions.getEmailMasking((String) temphash.get("contact_emails"))+"<input type=\"hidden\" name=\"emailaddress\" value=\"" + temphash.get("contact_emails")+"\"></td>");
                    if(functions.isValueNull((String) temphash.get("telno")))
                    {
                      out.println("<td align=\"center\" data-label=\"telno\">&nbsp;" + functions.getPhoneNumMasking((String) temphash.get("telno")) + "<input type=\"hidden\" name=\"telno\" value=\"" + temphash.get("telno") + "\"></td>");
                    }
                    else{
                      out.println("<td align=\"center\" data-label=\"telno\">&nbsp;" +"-"+ "<input type=\"hidden\" name=\"telno\" value=\"" + "-" + "\"></td>");

                    }
                      out.println("<td align=\"center\" data-label=\"Action\"><form action=\"/merchant/servlet/EditMemberUserList?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><button type=\"submit\" name=\"User Management\" class=\"btn btn-default\" ><input type=\"hidden\" name=\"action\" value=\"View\"><i class=\"fa fa-share\"></i></button></form></td>");
                    out.println("<td align=\"center\" "+style+"><form action=\"/merchant/servlet/EditMemberUserList?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"merchantid\" value=\""+ESAPI.encoder().encodeForHTML((String) session.getAttribute("merchantid"))+"\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><button type=\"submit\" name=\"User Management\" value=\"Edit\" class=\"btn btn-default\"><input type=\"hidden\" name=\"action\" value=\"modify1\"><i class=\"fa fa-edit\"></i></button></form></td>");
                    out.println("<td align=\"center\" "+style+">" +
                            "<form action=\"/merchant/servlet/EditMemberUserList?ctoken="+ctoken+"\" method=\"POST\" name=\"formAction\" onSubmit=\"return DoReverse('"+ctoken+"')\">" +
                            "<input type=\"hidden\" name=\"merchantid\" value=\""+ESAPI.encoder().encodeForHTML((String) session.getAttribute("merchantid"))+"\">" +
                            "<input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\">" +
                            "<input type=\"hidden\" name=\"userid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("userid"))+"\">" +
                            "<input type=\"hidden\" name=\"action\" value=\"delete\">" +
                            "<button type=\"submit\" name=\"User Management\" value=\"Delete\" class=\"btn btn-default\">"+
                            "<i class=\"fa fa-times fa fa-white\"></i>"+
                            "</button>" +
                            "</form></td>");
                    out.println("<td align=\"center\" "+style+"><form action=\"/merchant/servlet/MemberUserTerminalList?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"merchantid\" value=\""+ESAPI.encoder().encodeForHTML((String)request.getAttribute("merchantid"))+"\"><input type=\"hidden\" name=\"userid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("userid"))+"\"><input type=\"submit\" name=\"User Management\" value=\"Add Terminal\" class=\"btn btn-default\">");
                    out.println(requestParameter);
                    out.println("</form></td>");
                    out.println("<td align=\"center\" "+style+"><form action=\"/merchant/allocationUser.jsp?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"merchantid\" value=\""+ESAPI.encoder().encodeForHTML((String)request.getAttribute("merchantid"))+"\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><input type=\"hidden\" name=\"userid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("userid"))+"\"><input type=\"submit\" name=\"MerchantModuleMappingList\" value=\"Module Allocation\" class=\"btn btn-default\" width=\"100\"></form></td>");
                    out.println("</tr>");
                  }
                %>
                <%
                  }
                  else if (records==0)
                  {
                    out.println(Functions.NewShowConfirmation1(memberChildList_sorry,memberChildList_no_records));
                  }
                %>
                </tbody>
              </table>
              <%--</div>--%>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>