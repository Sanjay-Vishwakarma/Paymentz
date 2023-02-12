<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI"%>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 2/11/14
  Time: 3:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title> Partner Banks</title>
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
        Partner Details
        <div style="float: right;">
          <form action="/icici/partnerLogo.jsp?ctoken=<%=ctoken%>" method="POST">

            <button type="submit" class="addnewmember" value="Add New Partner Logo" name="submit">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Partner Logo
            </button>
          </form>
        </div>
        <div style="float: right;">
          <form action="/icici/partnersignup.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="addnewmember" value="Add New Partner" name="submit">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Partner
            </button>
          </form>
        </div>
      </div>
      <br><br>
      <form action="/icici/servlet/ListPartnerDetails?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
          String partnerId=request.getParameter("partnerid");
          String partnerName=request.getParameter("partnerName");

          String str="ctoken=" + ctoken;
          if(partnerId!=null)str = str + "&partnerid=" + partnerId;
          if(partnerId!=null)str = str + "&partnerName=" + partnerName;

          int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
          int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
        %>
        <table  align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr>
                  <td colspan="4">&nbsp;</td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="30%" class="textb" >Partner Id</td>
                  <td width="0%" class="textb"></td>
                  <td width="25%" class="textb">
                    <input maxlength="15" type="text" name="partnerId"  value="" class="txtbox">
                  </td>

                  <td width="2%" class="textb"></td>
                  <td width="45%" class="textb">Partner Name</td>
                  <td width="8%" class="textb"></td>
                  <td width="48%" class="textb">
                    <input maxlength="15" type="text" name="partnerName"  value="" class="txtbox">

                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="20%" class="textb" ></td>
                  <td width="0%" class="textb"></td>
                  <td width="22%" class="textb"></td>

                  <td width="2%" class="textb"></td>
                  <td width="45%" class="textb" ></td>
                  <td width="8%" class="textb"></td>
                  <td width="40%" class="textb"></td>

                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="20%" class="textb" ></td>
                  <td width="5%" class="textb"></td>
                  <td width="22%" class="textb">

                  </td>

                  <td width="10%" class="textb">&nbsp;</td>
                  <td width="40%" class="textb" ></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%"  align="center">
                    <button type="submit" class="buttonform" style="margin-left:40px; ">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="20%" class="textb" ></td>
                  <td width="0%" class="textb"></td>
                  <td width="22%" class="textb"></td>

                  <td width="2%" class="textb"></td>
                  <td width="45%" class="textb" ></td>
                  <td width="8%" class="textb"></td>
                  <td width="40%" class="textb"></td>
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
  <%  Hashtable hash = (Hashtable)request.getAttribute("transdetails");
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
  <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td valign="middle" align="center"class="th0">Sr No</td>
      <td valign="middle" align="center"class="th0">Bank Name</td>
      <td valign="middle" align="center"class="th0">Currency</td>
      <td valign="middle" align="center"class="th0">Mapping Date</td>
    </tr>
    </thead>
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
        GatewayType gatewayType= GatewayTypeService.getGatewayType((String) temphash.get("pgtypeid"));
        out.println("<tr>");
        out.println("<td align=center "+style+">&nbsp;"+srno+ "</td>");
        out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML(gatewayType.getGateway())+"</td>");
        out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML(gatewayType.getCurrency())+"</td>");
        out.println("<td align=center "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("creationdate"))+"</td>");
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
        <jsp:param name="page" value="ViewPartnerBankDetails"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </tr>
  </table>
  <%
    }
    else if(functions.isValueNull((String)request.getAttribute("message")))
    {
      out.println(Functions.NewShowConfirmation("Sorry",(String)request.getAttribute("message")));
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry","No Records Found."));
    }


  %>

  <%
    }
    else
    {
      response.sendRedirect("/icici/logout.jsp");
      return;
    }
  %>
</div>
</body>
</html>