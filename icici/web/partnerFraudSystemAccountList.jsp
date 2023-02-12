<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI"%>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ include file="functions.jsp"%>
<%@ page import="com.directi.pg.Logger"%>
<%@ include file="index.jsp"%>
<%!
  Logger logger=new Logger("partnerFraudSystemAccountList.jsp");
  Functions functions= new Functions();
%>
<%--
  Created by IntelliJ IDEA.
  User: Supriya
  Date: 13/7/15
  Time: 4:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String partnerId = Functions.checkStringNull(request.getParameter("partnerid"));
  String fsAccountId = Functions.checkStringNull(request.getParameter("fsaccountid"));
  String str = "";

  if(partnerId == null)
  {partnerId = "";}

  if(fsAccountId == null)
  {fsAccountId = "";}

  str="ctoken="+ctoken;
  if(partnerId != null) {str=str+"&partnerid=" + partnerId;}
  if(fsAccountId != null) {str=str+"&fsaccountid=" +fsAccountId;}
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title> FraudSystem Accounts Mapping</title>
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
        PSP Fraud System Accounts
        <div style="float: right;">
          <form action="/icici/managePartnerFraudSystemAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="New PSP Fraud System Account" name="submit" class="addnewmember" style="width: 200px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;New PSP Fraud System Account
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/PartnerFraudSystemAccountList?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
          int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
          int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
        %>
        <table align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4" height="20px">&nbsp;</td></tr>
                <tr>
                  <td width="1%" class="textb">&nbsp;</td>
                  <td width="9%" class="textb" align="center">Partner</td>
                  <td width="12%" class="textb">
                    <select name="partnerid" class="txtbox" style="width: 200px;"><option value="" selected>ALL</option>
                      <%

                        Connection conn=null;
                        try
                        {
                          conn= Database.getConnection();
                          String  query = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
                          PreparedStatement pstmt = conn.prepareStatement( query );
                          ResultSet rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\""+rs.getInt("partnerId")+"\" >"+rs.getInt("partnerId")+" - "+rs.getString("partnerName")+"</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception::::::"+e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>
                  </td>
                  <td width="10%" class="textb" align="center">Fraud System AccountId</td>
                  <td width="12%" class="textb">
                    <input  maxlength="255" type="text" name="fsaccountid"  value="" class="txtbox">
                  </td>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" align="right">
                    <button type="submit" class="buttonform">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
                <tr><td colspan="4" height="20px">&nbsp;</td></tr>
              </table>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</div>
<div class="reporttable">
  <%
    Hashtable hash = (Hashtable)request.getAttribute("transdetails");
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

    if(records > 0)
    {
  %>
  <div id="containrecord"></div>
  <table align=center width="70%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr >
      <td width="5%" class="th1">Sr No</td>
      <td width="10%" class="th1">Partner Id</td>
      <td width="10%" class="th1">Fraud System Account ID</td>

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
        out.println("<tr height=20px>");

        out.println("<td align=center width=\"5%\" "+style+">&nbsp;"+srno+ "</td>");
        out.println("<td align=center width=\"10%\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("partnerid"))+"<input type=\"hidden\" name=\"partnerid\" value=\""+temphash.get("partnerid")+"\"></td>");
        out.println("<td align=center width=\"10%\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("fsaccountid"))+"<input type=\"hidden\" name=\"fsaccountid\" value=\""+temphash.get("fsaccountid")+"\"></td>");
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
        <jsp:param name="page" value="PartnerFraudSystemAccountList"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </tr>
  </table>
  <%
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
