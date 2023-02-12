<%@ page import="com.directi.pg.Database,
                 com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 14/7/15
  Time: 7:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String subaccount = null;
  String mid = null;
  String string = null;

  if(subaccount == null) {subaccount = ""; }
  if(mid == null) {mid = ""; }

  if(subaccount != null ) {string = string + "&subaccount" +subaccount; }
  if(mid != null) {string = string + "&mid" +mid;}
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Fraud Management> Manage Merchant Fraud Account</title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>
</head>
<body>
<%
  String memberid=nullToStr(request.getParameter("mid"));
  Logger logger = new Logger("fraudSystemMerchantSubAccountList.jsp");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    if(request.getAttribute("message")!=null)
    {
      out.println(request.getAttribute("message"));
    }
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Merchant Fraud Accounts
        <div style="float: right;">
          <form action="/icici/manageMarchantFraudAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add Merchant Fraud Account" name="submit" class="addnewmember" style="width: 300px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Merchant Fraud Account
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/FraudSystemMerchantSubAccountList?ctoken=<%=ctoken%>" method="post" name="forms">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
          String str="ctoken=" + ctoken;
          int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
          int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

        %>
        <table align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4" height="25px">&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="10%" class="textb" >Member ID</td>
                  <td width="12%" class="textb">
                    <input name="mid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                  <%--  <select name="mid" class="txtbox" style="width: 200px;"><option value="" selected>ALL</option>
                      <%
                        Connection conn = null;
                        try
                        {
                          conn= Database.getConnection();
                          String query = "select memberid, company_name from members where activation='Y' ORDER BY memberid ASC";
                          PreparedStatement pstmt = conn.prepareStatement( query );
                          ResultSet rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\""+rs.getInt("memberid")+"\">"+rs.getInt("memberid")+" - "+rs.getString("company_name")+"</option>");
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
                    </select>--%>
                  </td>
                  <td width="7%" class="textb">&nbsp;</td>
                  <td width="9%" class="textb" >Sub Account/Website</td>
                  <td width="12%" class="textb">
                    <select name="subaccount" class="txtbox" style="width: 350px;"><option value="" selected>ALL</option>
                      <%
                        Connection conn = null;
                        try
                        {
                          conn = Database.getConnection();
                          StringBuffer qry = new StringBuffer("select subacc.fssubaccountid,fs.fsname,acc.accountname,subaccountname from fsaccount_subaccount_mapping as subacc,fraudsystem_account_mapping as acc,fraudsystem_master as fs where subacc.fsaccountid=acc.fsaccountid and acc.fsid=fs.fsid");
                          PreparedStatement pstmt = conn.prepareStatement(qry.toString());
                          ResultSet rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\"" + rs.getInt("subacc.fssubaccountid") + "\">" + rs.getInt("subacc.fssubaccountid") + " : " + rs.getString("subaccountname") + " ( " + rs.getString("acc.accountname") + " : " + rs.getString("fs.fsname") + " ) " + "</option>");
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
                  <td width="9%" class="textb"></td>
                  <td width="9%" class="textb">
                    <button type="submit" class="buttonform">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
                <tr><td colspan="4" height="25px">&nbsp;</td></tr>
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

    String errormsg=(String)request.getAttribute("message");

    if(errormsg!=null)
    {
      out.println("<center><font class=\"textb\">"+errormsg+"</font></center>");
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
    if(records > 0)
    {

  %>

  <div id="containrecord"></div>
  <table align=center width="70%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td width="4%" class="th1">Sr No.</td>
      <td width="8%" class="th1">Member ID</td>
      <td width="20%" class="th1">Fraud Account Name/Website</td>
      <td width="18%" class="th1">Fraud System Account Name</td>
      <td width="15%" class="th1">Fraud System Name</td>
      <td width="15%" class="th1">Sub Merchant UserName</td>
      <td width="10%" class="th0">isActive</td>
      <td width="13%" class="th0">isVisible</td>
      <td width="13%" class="th0">Online Fraud Check</td>
      <td width="13%" class="th0">Is API Call Supported</td>
      <td width="30%" class="th1">Action</td>
    </tr>
    </thead>

    <%
      String style="class=td1";
      String ext="light";
      String subMerchantUserName = "-";

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

        if(temphash.get("submerchantUsername")!=null)
          subMerchantUserName = (String)temphash.get("submerchantUsername");

        out.println("<tr height=10>");

        out.println("<td align=center "+style+">"+srno+ "</td>");
        out.println("<td align=center "+style+">" +ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"<input type=\"hidden\" name=\"memberid\" value=\""+temphash.get("memberid")+"\"></td>");
        out.println("<td align=center "+style+">" +ESAPI.encoder().encodeForHTML((String) temphash.get("subaccountname"))+"<input type=\"hidden\" name=\"subaccountname\" value=\""+temphash.get("subaccountname")+"\"></td>");
        out.println("<td align=center "+style+">" +ESAPI.encoder().encodeForHTML((String) temphash.get("accountname"))+"<input type=\"hidden\" name=\"accountname\" value=\""+temphash.get("accountname")+"\"></td>");
        out.println("<td align=center "+style+">" + ESAPI.encoder().encodeForHTML((String) temphash.get("fsname")) + "<input type=\"hidden\" name=\"fsname\" value=\"" + temphash.get("fsname") + "\"></td>");
        out.println("<td align=center "+style+">" + ESAPI.encoder().encodeForHTML(subMerchantUserName) + "<input type=\"hidden\" name=\"submerchantUsername\" value=\"" + subMerchantUserName + "\"></td>");
        out.println("<td align=center "+style+">" +ESAPI.encoder().encodeForHTML((String) temphash.get("isactive"))+"<input type=\"hidden\" name=\"isactive\" value=\""+temphash.get("isactive")+"\"></td>");
        out.println("<td align=center "+style+">" +ESAPI.encoder().encodeForHTML((String) temphash.get("isvisible"))+"<input type=\"hidden\" name=\"isvisible\" value=\""+temphash.get("isvisible")+"\"></td>");
        out.println("<td align=center "+style+">" +ESAPI.encoder().encodeForHTML((String) temphash.get("isonlinefraudcheck"))+"<input type=\"hidden\" name=\"isonlinefraudcheck\" value=\""+temphash.get("isonlinefraudcheck")+"\"></td>");
        out.println("<td align=center "+style+">" +ESAPI.encoder().encodeForHTML((String) temphash.get("isapiuser"))+"<input type=\"hidden\" name=\"isapiuser\" value=\""+temphash.get("isapiuser")+"\"></td>");
        out.println("<td align=\"center\" "+style+"><form action=\"/icici/servlet/ActionFraudSystemMerchantAccount?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"mappingid\" value=\""+temphash.get("merchantfraudserviceid")+"\"><input type=\"hidden\" name=\"fssubaccountid\" value=\""+temphash.get("fssubaccountid")+"\"> <input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"submit\" class=\"gotoauto\" value=\"Modify\"></form></td></td>");
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
        <jsp:param name="page" value="FraudSystemMerchantSubAccountList"/>
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
<%!
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>
