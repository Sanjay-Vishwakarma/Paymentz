<%@ page import="com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: naushad
  Date: 9/2/16
  Time: 9:58 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">

  <title>Module Management> Admin Module Master</title>
</head>
<body>
<%!
  Logger logger=new Logger("addNewAdminModule");
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Admin Module Management
        <div style="float: right;">
          <form action="/icici/addNewAdminModule.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Module
            </button>
          </form>
        </div>
      </div>
      <br>
      <form action="/icici/servlet/AdminModuleList?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
          String str="ctoken=" + ctoken;
          int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
          int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
        %>
        <table border="0" align="center" width="100%" cellpadding="2" cellspacing="2">
          <tr>
            <td width="2%" class="textb">&nbsp;</td>
            <td align="center" width="20%" class="textb" >Module Name</td>
            <td width="0%" class="textb"></td>
            <td width="22%" class="textb">
              <input maxlength="100" type="text" name="modulename" class="txtbox" value="" style="width:186px">
            </td>
            <td width="10%" class="textb">&nbsp;</td>
            <td width="50%" class="textb">
              <button type="submit" class="buttonform" style="margin-left:40px; ">
                <i class="fa fa-clock-o"></i>
                &nbsp;&nbsp;Search
              </button>
            </td>
            <td width="40%" class="textb" ></td>
            <td width="5%" class="textb"></td>
          </tr>
          <tr>
            <td width="2%" class="textb">&nbsp;</td>
            <td align="center" width="20%" class="textb" ></td>
            <td width="0%" class="textb"></td>
            <td width="22%" class="textb"></td>
            <td width="10%" class="textb">&nbsp;</td>
            <td width="50%" class="textb"></td>
            <td width="40%" class="textb" ></td>
            <td width="5%" class="textb"></td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</div>
<%
  Functions functions=new Functions();
  Hashtable hash = (Hashtable)request.getAttribute("transdetails");
  Hashtable temphash=null;
  int records=0;
  int totalrecords=0;

  String error=(String ) request.getAttribute("errormessage");
  if(error !=null)
  {
    out.println(error);

  }
  String currentblock=request.getParameter("currentblock");

  if(currentblock==null)
    currentblock="1";

  try
  {
    records=Integer.parseInt((String)hash.get("records"));
    totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
  }
  catch(Exception ex)
  {

  }
  if(hash!=null)
  {
    hash = (Hashtable)request.getAttribute("transdetails");
  }
  if(records>0)
  {
%>
<div class="reporttable">
  <table align=center width="60%" class="table table-striped table-bordered table-hover table-green dataTable">
    <tr>
      <td valign="middle" align="center" class="th0">Module Id</td>
      <td valign="middle" align="center" class="th0">Name</td>
      <td valign="middle" align="center" class="th0">Creation Time</td>
    </tr>
    <%
      String style="class=td1";
      String ext="light";
      for(int pos=1;pos<=records;pos++)
      {
        String id=Integer.toString(pos);

        int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

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
        temphash=(Hashtable)hash.get(id);
        out.println("<tr>");
        out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("moduleid"))+"</td>");
        out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("modulename"))+"</td>");
        out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("modulecreationtime"))+"</td>");
        out.println("</tr>");
      }
    %>
  </table>
  <table align=center valign=top><tr>
    <td align=center>
      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="AdminModuleList"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </tr>
  </table>
</div>

<%
    }
    else if(functions.isValueNull((String ) request.getAttribute("errormessage")))
    {
      out.println("<div class=\"reporttable\" style=\"margin-bottom: 9px;\">");
      out.println(Functions.NewShowConfirmation("Result",(String ) request.getAttribute("errormessage")));
      out.println("</div>");
    }
    else
    {
      out.println("<div class=\"reporttable\" style=\"margin-bottom: 9px;\">");
      out.println(Functions.NewShowConfirmation("Sorry","No records found."));
      out.println("</div>");
    }
  }
  else
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
</body>
</html>

