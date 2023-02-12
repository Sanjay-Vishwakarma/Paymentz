<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI"%>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.Logger"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.manager.vo.PartnerCommissionVO" %>
<%@ page import="com.manager.vo.payoutVOs.ChargeMasterVO" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: supriya
  Date: 5/26/14
  Time: 3:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Partner Commission</title>
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
</head>
<body>
<%!
  private static Logger logger=new Logger("actionPartnerCommission.jsp");
%>
<%
  String memberid=nullToStr(request.getParameter("memberid"));
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (!com.directi.pg.Admin.isLoggedIn(session))
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Partner Commission
        <div style="float: right;">
          <form action="/icici/managePartnerCommission.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="addnewmember" value="Add New Partner Commission" name="submit" style="width:350px ">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Partner Commission
            </button>
          </form>
        </div>
      </div><br>
      <form action="/icici/servlet/ListPartnerCommission?ctoken=<%=ctoken%>" method="post" name="forms">
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <%
          String str="ctoken=" + ctoken;
          int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
          int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
        %>
        <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Partner Id</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select name="partnerid" class="txtbox"><option value="" selected></option>
                      <%
                        Connection conn = Database.getConnection();
                        String query = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
                        PreparedStatement pstmt = conn.prepareStatement(query);
                        ResultSet rs = pstmt.executeQuery();
                        while (rs.next())
                        {
                          out.println("<option value=\""+rs.getInt("partnerId")+"\">"+rs.getInt("partnerId")+" - "+rs.getString("partnerName")+"</option>");
                        }
                      %>
                    </select>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" for="mid" >Member Id</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="memberid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                  <%--  <select name="memberid" class="txtbox"><option value="" selected></option>
                      <%
                        conn= Database.getConnection();
                        query = "select memberid, company_name from members where activation='Y' ORDER BY memberid ASC";
                        pstmt = conn.prepareStatement( query );
                        rs = pstmt.executeQuery();
                        while (rs.next())
                        {
                          out.println("<option value=\""+rs.getInt("memberid")+"\">"+rs.getInt("memberid")+" - "+rs.getString("company_name")+"</option>");
                        }
                      %>
                    </select>--%>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" ></td>
                  <td width="3%" class="textb"></td>
                  <td width="10%" class="textb">
                    <button type="submit" class="buttonform" >
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
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
  <%
    String error = (String) request.getAttribute("errormessage");
    PartnerCommissionVO partnerCommissionVO=(PartnerCommissionVO)request.getAttribute("partnerCommissionVO");

    String action = (String) request.getAttribute("action");
    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    if (partnerCommissionVO != null)
    {
      ChargeMasterVO chargeMasterVO=partnerCommissionVO.getChargeMasterVO();
      if (action.equalsIgnoreCase("history"))
      {}
  else
  {
    String style = "class=tr0";
  %> <form action="/icici/servlet/UpdatePartnerCommission?ctoken=<%=ctoken%>" method="post" name="forms" >
  <input type="hidden" name="commissionid" value="<%=partnerCommissionVO.getCommissionId()%>">
  <table align=center class="table table-striped table-bordered table-green dataTable" style="width:50% ">
    <tr <%=style%>>
      <td class="th0" colspan="2"><b>Update Commission</b></td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Partner Id : </td>
      <td class="tr1"><input type="text" class="txtbox1"  size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(partnerCommissionVO.getPartnerId())%>" disabled> </td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Member Id : </td>
      <td class="tr1"><input type="text" class="txtbox1" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(partnerCommissionVO.getMemberId())%>" disabled> </td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Terminal Id : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(partnerCommissionVO.getTerminalId())%>" disabled> </td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Commission Name : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(chargeMasterVO.getChargeName())%>" disabled> </td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Commission Value : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="30" name="commissionvalue" value="<%=Functions.round(partnerCommissionVO.getCommissionValue(),2)%>"></td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Start Date : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="50" name="startdate" value="<%=targetFormat.format(targetFormat.parse(partnerCommissionVO.getStartDate()))%>" disabled></td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">End Date : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="50" name="enddate" value="<%=targetFormat.format(targetFormat.parse(partnerCommissionVO.getEndDate()))%>" disabled></td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Sequence No : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="30" name="" value="<%=ESAPI.encoder().encodeForHTML(partnerCommissionVO.getSequenceNo())%>" disabled> </td>
    </tr>
    <tr <%=style%>>
      <td class="td1"></td>
      <td class="td1"><input type="submit" align="center" class="button" value="Update"></td>
    </tr>
  </table>
</form>
  <%
      }
    }
    else
    {
      out.println(Functions.NewShowConfirmation(error!=null?"Result":"Sorry",error!=null?error:"No Records Found."));
    }

  %>
</div>
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