<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  Logger logger=new Logger("managePartnerCommission.jsp");
  Functions functions= new Functions();
%>
<html>
<head>
  <%--Datepicker css format--%>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
  <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

  <script>
    $(function() {
      $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd',endDate:'+10y'});
    });
  </script>
  <title> Partner Commission</title>
  <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js"></script>
</head>
<body>
<%
  String memberid=nullToStr(request.getParameter("memberid"));
  Logger logger = new Logger("managePartnerCommission.jsp");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (!com.directi.pg.Admin.isLoggedIn(session))
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading" >
        Partner Commission
        <div style="float: right;">
          <form action="/icici/listPartnerCommission.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Partner Commission Master" name="submit" class="addnewmember" style="width: 250px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Partner Commission Master
            </button>
          </form>
        </div>
      </div>
      <form name="addtype" action="/icici/servlet/ManagePartnerCommission?ctoken=<%=ctoken%>" method="post" name="form1">
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr><td colspan="4">&nbsp;</td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Partner Id*</td>
                  <td width="3%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <input name="partnerid" class="txtbox" id="allpid" autocomplete="on">
                    <%--<select name="partnerid" class="txtbox"><option value="" selected></option>
                      <%
                        Connection conn = null;
                        PreparedStatement pstmt = null;
                        ResultSet rs = null;
                        try
                        {
                          conn = Database.getConnection();
                          String query = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
                          pstmt = conn.prepareStatement(query);
                          rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\"" + rs.getInt("partnerId") + "\">" + rs.getInt("partnerId") + " - " + rs.getString("partnerName") + "</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception:::::" + e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>--%>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" for="mid">Member Id*</td>
                  <td width="3%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <input name="memberid" id="midy" value="<%=memberid%>" class="txtbox" autocomplete="on">
                   <%-- <select name="memberid" class="txtbox"><option value="" selected></option>
                      <%
                        try
                        {
                          conn = Database.getConnection();
                          String query = "select memberid, company_name from members where activation='Y' ORDER BY memberid ASC";
                          pstmt = conn.prepareStatement(query);
                          rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\"" + rs.getInt("memberid") + "\">" + rs.getInt("memberid") + " - " + rs.getString("company_name") + "</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception:::::" + e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>--%>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Terminal Id*</td>
                  <td width="3%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <input name="terminalid" class="txtbox" id="tid" autocomplete="on">
                    <%--<select name="terminalid" class="txtbox"><option value="" selected></option>
                      <%
                        Connection conn = null;
                        PreparedStatement pstmt = null;
                        ResultSet rs = null;
                        try
                        {
                          conn = Database.getConnection();
                          String query = "SELECT terminalid FROM member_account_mapping";
                          pstmt = conn.prepareStatement(query);
                          rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\"" + rs.getString("terminalid") + "\"  >" + rs.getString("terminalid") + "</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception:::::" + e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>>
                    </select>--%>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Commission Name*</td>
                  <td width="3%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <select name="commissionon" class="txtbox"><option value="" selected> -- select -- </option>
                      <%
                        Connection conn = null;
                        PreparedStatement pstmt = null;
                        ResultSet rs = null;
                        try
                        {
                          conn = Database.getConnection();
                          String query = "Select chargeid, chargename from charge_master";
                          pstmt = conn.prepareStatement(query);
                          rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\"" + rs.getString("chargeid") + "\">" + rs.getString("chargename") + "</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception:::::" + e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Commission Value*</td>
                  <td class="textb">:</td>
                  <td>
                    <input maxlength="15" class="txtbox" type="text" name="commissionvalue"  value="">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Start Date*</td>
                  <td width="3%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <input type="text" readonly class="datepicker" style="width:142px" name="startDate">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>

                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >End Date*</td>
                  <td width="3%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <input type="text" readonly class="datepicker" style="width:142px" name="endDate" >
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Sequence Number*</td>
                  <td class="textb">:</td>
                  <td>
                    <input maxlength="15" class="txtbox" type="text" name="sequencenum"  value="">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb"></td>
                  <td style="padding: 3px" width="5%" class="textb"></td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <button type="submit" class="buttonform" value="Add" style="width:200px ">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Add
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
            </td>
          </tr>
          </tbody>
        </table>
      </form>
    </div>
  </div>
</div>
<%
  if(functions.isValueNull((String)request.getAttribute("statusMsg")))
  {
    out.println("<div class=\"reporttable\">");
    out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("statusMsg")));
    out.println("</div>");
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