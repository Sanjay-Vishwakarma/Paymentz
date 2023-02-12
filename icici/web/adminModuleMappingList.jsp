<%@ page import="com.directi.pg.Database"%>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Naushad
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
  <title>Module Management> Admin Module Mapping</title>
</head>
<body>
<%!
  Logger logger=new Logger("Admin Module Mapping");
%>
<% Logger logger = new Logger("adminModuleMappingList.jsp");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Admin Module Management
        <div style="float: right;">
          <form action="/icici/manageAdminModulesMapping.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Mapping
            </button>
          </form>
        </div>
      </div>
      <br>
      <form action="/icici/servlet/AdminModuleMappingList?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
          String str="ctoken=" + ctoken;
          int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
          int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
        %>
        <table border="0" align="center" width="100%" cellpadding="2" cellspacing="2">
          <tr>
            <td width="2%" class="textb">&nbsp;</td>
            <td align="center" width="20%" class="textb" >Admin User</td>
            <td width="0%" class="textb"></td>
            <td width="22%" class="textb">
              <select name="adminid" class="txtbox" style="width:186px">
                <option value="0">Select Admin User</option>
                <%
                  Connection conn = null;
                  PreparedStatement pstmt = null;
                  ResultSet rs = null;
                  try
                  {
                    conn = Database.getConnection();
                    String  query = "select * from admin";
                    pstmt = conn.prepareStatement( query );
                    rs = pstmt.executeQuery();
                    while (rs.next())
                    {
                      out.println("<option value=\""+rs.getInt("adminid")+"\">"+rs.getInt("adminid")+" - "+rs.getString("login")+"</option>");
                    }
                  }
                  catch (Exception e)
                  {
                    logger.error("Exception"+e);
                  }
                  finally
                  {
                    Database.closeConnection(conn);
                  }
                %>
              </select>
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
  Hashtable hash = (Hashtable)request.getAttribute("transdetails");
  Functions functions=new Functions();

  Hashtable temphash=null;
  int records=0;
  int totalrecords=0;

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
<div class="reporttable" style="margin-bottom: 9px;">
  <table  border="0" width="100%" class="table table-striped table-bordered table-green dataTable" style="margin-bottom: 0px">
    <thead>
    <tr>
      <td valign="middle" align="center" class="th0">Mapping Id</td>
      <td valign="middle" align="center" class="th0">Module Name</td>
      <td valign="middle" align="center" class="th0">Action</td>
    </tr>
    </thead>
    <%
      String style="class=td1";
      String ext="light";
      for(int pos=1;pos<=records;pos++)
      {
        String id=Integer.toString(pos);

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
        String mappingId=(String)temphash.get("mappingid");
    %>
    <tr>
      <td align="center" <%=style%>><%=mappingId%></td>
      <td align="center" <%=style%>><%=(String)temphash.get("modulename")%></td>
      <td align="center" <%=style%> style="line-height:0.00;">&nbsp;<form action="/icici/servlet/ActionAdminModule?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=mappingId%>> <input type="hidden" name="action" value="remove"> <input type="submit" class="gotoauto" value="Remove"></form></td>
    </tr>
    <%}
    %>
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
