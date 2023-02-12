<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI"%>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Naushad
  Date: 12/5/16
  Time: 3:09 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%!
  Functions functions = new Functions();
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">

  <title>Merchants management > Risk Rule Master</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Risk Rule Master
        <div style="float: right;">
          <form action="/icici/addNewRiskParameter.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="addnewmember" value="Add New Partner Logo" name="submit">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Risk Rule
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/MonitoringParameterMaster?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
          Connection conn=null;
          String query=null;
          PreparedStatement pstmt=null;
          ResultSet rs=null;
          String str="ctoken=" + ctoken;
          str=str+"&isinputrequired="+request.getParameter("isinputrequired");
          str=str+"&monitoing_para_name="+request.getParameter("monitoing_para_name");
          str=str+"&monitoing_onchannel="+request.getParameter("monitoing_onchannel");

          int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
          int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
        %>
        <table align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Risk Rule Name</td>
                  <td width="12%" class="textb">
                  <select name="monitoing_para_name" style="width: 130%">
                    <option value="" selected>--Select--</option>
                    <%
                      try
                      {
                        //conn=Database.getConnection();
                        conn=Database.getRDBConnection();
                        query = "select * from  monitoring_parameter_master";
                        pstmt = conn.prepareStatement( query );
                        rs = pstmt.executeQuery();
                        while (rs.next())
                        {
                          out.println("<option value=\""+rs.getString("monitoing_para_name")+"\">"+rs.getString("monitoing_para_id")+"- "+rs.getString("monitoing_para_name")+"</option>");
                        }
                      }
                      catch(Exception e){
                      }
                      finally
                      {
                        Database.closeConnection(conn);
                      }
                    %>
                  </select>
                 <%-- <td width="12%" class="textb">
                    <input  maxlength="255" type="text" name="monitoing_para_name"  value="" class="txtbox">
                  </td>--%>
                  <td width="6%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Terminal/Merchant Maturity</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select name="monitoing_onchannel" class="txtbox">
                      <option value="" selected>--Select--</option>
                      <option value="New">New (<90 Days)</option>
                      <option value="Old">Old (>90 Days)</option>
                      <option value="All">All</option>
                    </select>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>

                  <td width="3%" class="textb"></td>
                  <td width="10%" class="textb">
                    <button type="submit" class="buttonform">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</div>
<div class="reporttable">
  <%  String errormsg1 = (String)request.getAttribute("message");
    if (errormsg1 == null)
    {
      errormsg1 = "";
    }
    else
    {
      out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" >");
      out.println(errormsg1);
      out.println("</font></td></tr></table>");
    }
  %>
  <%  Hashtable hash = (Hashtable)request.getAttribute("transdetails");

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
  <form action="/icici/servlet/UpdateMonitoringParameterMaster">
    <input type="hidden" name="ctoken" value="<%=ctoken%>">
    <table align=center style="width:100%" class="table table-striped table-bordered table-hover table-green dataTable">
      <thead>
      <tr>
        <td valign="middle" align="center" class="th0">Sr No</td>
        <td valign="middle" align="center" class="th0">Risk Rule Id</td>
        <td valign="middle" align="center" class="th0">Risk Rule Name</td>
        <td valign="middle" align="center" class="th0">Risk Rule Tech Name</td>
        <td valign="middle" align="center" class="th0">Monitoring Unit</td>
        <td valign="middle" align="center" class="th0">Monitoring Category</td>
        <td valign="middle" align="center" class="th0">Monitoring Deviation</td>
        <td valign="middle" align="center" class="th0">Monitoring Keyword</td>
        <td valign="middle" align="center" class="th0">Monitoring Sub Keyword</td>
        <td valign="middle" align="center" class="th0">Monitoring Alert Category</td>
        <td valign="middle" align="center" class="th0">Monitoring Channel</td>
        <%--<td valign="middle" align="center" class="th0">Monitoring Frequency</td>--%>
        <td valign="middle" align="center" class="th0">Action</td>
      </tr>
      </thead>
      <%
        String style="class=td1";
        String ext="light";

        for(int pos=1;pos<=records;pos++)
        {
          String id=Integer.toString(pos);
          String techChargeName="";
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
          if(functions.isValueNull((String)temphash.get("keyname")))
          {
            techChargeName= (String) temphash.get("keyname");
          }
          out.println("<tr>");
          out.println("<td align=center "+style+">&nbsp;"+srno+ "</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("monitoing_para_id"))+"</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("monitoing_para_name"))+"</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("monitoing_para_tech_name"))+"</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("monitoring_unit"))+"</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("monitoing_category"))+"</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("monitoring_deviation"))+"</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("monitoing_keyword"))+"</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("monitoing_subkeyword"))+"</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("monitoing_alert_category"))+"</td>");
          out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("monitoing_onchannel"))+"</td>");
          /*out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("monitoing_frequency"))+"</td>");*/
          out.println("<td class=\"tr0\" align=\"center\"><button type=\"submit\" class=\"button\" value="+temphash.get("monitoing_para_id")+" name=\"modify\">Modify</button></td>");
          out.println("</tr>");
        }
      %>
    </table>
  </form>
  <table align=center valign=top><tr>
    <td align=center>
      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="MonitoringParameterMaster"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </tr>
  </table>
  <%
    }
    else if(request.getAttribute("status")!=null)
    {
      out.println(Functions.NewShowConfirmation("status",request.getAttribute("statusDescription").toString()));
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
    }
  %>
</div>
<%
  }
  else
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
</body>
</html>