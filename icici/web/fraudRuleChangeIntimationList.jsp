<%@ page import="com.directi.pg.Database"%>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.fraudruleconfVOs.FraudRuleChangeIntimationVO" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.List" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 13/8/15
  Time: 1:25 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">

  <title>Fraud Management> Fraud Rule Change Intimation</title>

  <script language="javascript">
    function DoUpdate()
    {
      var checkboxes = document.getElementsByName("intimationid");

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
        alert("select at least one intimation");
        return false;
      }
      if (confirm("Do you really want to send all selected intimation."))
      {
        document.fraudRuleSendIntimation.submit();
      }
    }

    function ToggleAll(checkbox)
    {
      flag = checkbox.checked;
      var checkboxes = document.getElementsByName("intimationid");
      var total_boxes = checkboxes.length;

      for(i=0; i<total_boxes; i++ )
      {
        checkboxes[i].checked =flag;
      }
    }
  </script>

</head>
<body>

<%
  Logger logger = new Logger("fraudRuleChangeIntimationList.jsp");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Fraud Rule Change Intimation
      </div>
      <form action="/icici/servlet/FraudRuleChangeIntimationList?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4" height="20px">&nbsp;</td></tr>
                <tr>
                  <td width="1%" class="textb">&nbsp;</td>
                  <td width="9%" class="textb" align="center">Fraud Account ID</td>
                  <td width="12%" class="textb">
                    <select name="fsaccountid" class="txtbox" style="width: 200px;"><option value="">Select Fraud Account ID</option>
                      <%
                        Connection con = null;
                        try
                        {
                          con = Database.getConnection();
                          StringBuffer qry = new StringBuffer("select fsaccountid,accountname from fraudsystem_account_mapping");
                          PreparedStatement pstmt = con.prepareStatement(qry.toString());
                          ResultSet rs = pstmt.executeQuery();
                          while(rs.next())
                          {
                            out.println("<option value="+rs.getInt("fsaccountid")+">" +rs.getInt("fsaccountid")+ " - " +rs.getString("accountname")+ "</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception"+e);
                        }
                        finally
                        {
                          Database.closeConnection(con);
                        }
                      %>
                    </select>
                  </td>
                  <td width="10%" class="textb" align="center">Status</td>
                  <td width="12%" class="textb">
                    <select name="status" class="txtbox" style="width: 200px;">
                      <option value="" selected>Select Status</option>
                      <option value="Initiated">Initiated</option>
                      <option value="Intimated">Intimated</option>
                      <option value="Changed">Changed</option>
                      <option value="Rejected">Rejected</option>
                    </select>
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
    String msg = (String) request.getAttribute("msg");
    String fsaccountid = (String) request.getAttribute("fsaccountid");
    List<FraudRuleChangeIntimationVO> intimationVOList = (List<FraudRuleChangeIntimationVO>) request.getAttribute("intimationVO");
    if(intimationVOList != null)
    {
      if(intimationVOList.size() > 0)
      {
        PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
        paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
        int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
  %>
  <div id="containrecord">
    <table style="border:1px solid #34495e;" bgcolor="white" align="center" width="100%" cellpadding="4" cellspacing="4">
      <tr align="center">
        <td valign="middle" align="center " style="font-size:15px " class="tr0"></td>
      </tr>
      <tr>
        <td style="padding:2px ">
          <table border="0"   width="100%"  align="center" class="table table-striped table-bordered table-hover table-green dataTable">
            <form name="fraudRuleSendIntimation" action="/icici/servlet/FraudRuleSendIntimation?ctoken=<%=ctoken%>" method="post">
              <input type="hidden" name="fsaccountid" value="<%=fsaccountid%>">
              <thead>
              <tr>
                <td style="padding:3px " valign="middle" class="th0" ><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                <td style="padding:3px " valign="middle" class="th0" >Sr No.</td>
                <td style="padding:3px " valign="middle" class="th0" >Intimation ID</td>
                <td style="padding:3px " valign="middle" class="th0" >Fraud System Name</td>
                <td style="padding:3px " valign="middle" class="th0" >Account ID</td>
                <td style="padding:3px " valign="middle" class="th0" >Sun Account ID</td>
                <td style="padding:3px " valign="middle" class="th0" >Partner ID</td>
                <td style="padding:3px " valign="middle" class="th0" >Member ID</td>
                <td style="padding:3px " valign="middle" class="th0" >Status</td>
                <td style="padding:3px " valign="middle" class="th0" >Created On</td>
              </tr>
              </thead>
              <%
                for(FraudRuleChangeIntimationVO intimationVO : intimationVOList)
                {
              %>
              <tr>
                <td valign="middle" align="center" width="7%"><input type="checkbox" name="intimationid" value="<%=intimationVO.getChangeIntimationId()%>"></td>
                <td  align="center"  size="10" class="textb" width="7%" ><%=srno%></td>
                <td  align="center"  size="10" class="textb" width="9%" ><%=intimationVO.getChangeIntimationId()%></td>
                <td  align="center"  size="10" class="textb" width="15%" ><%=FraudSystemService.getFSGateway(intimationVO.getFraudSystemId())%></td>
                <td  align="center"  size="10" class="textb" width="8%" ><%=intimationVO.getFsAccountId()%></td>
                <td  align="center"  size="10" class="textb" width="9%" name="fssubaccountid"><%=intimationVO.getFsSubAccountId()%></td>
                <td  align="center"  size="10" class="textb" width="9%" ><%=intimationVO.getPartnerId()%></td>
                <td  align="center"  size="10" class="textb" width="10%" ><%=intimationVO.getMemberId()%></td>
                <td  align="center"  size="10" class="textb" width="12%" ><%=intimationVO.getStatus()%></td>
                <td  align="center"  size="10" class="textb" width="50%" ><%=intimationVO.getCreationDate()%></td>
              </tr>
              <%
                  srno++;
                }
              %>
              <thead>
              <tr>
                <td valign="middle" align="center"  class="th0" >
                  Total:<%=paginationVO.getTotalRecords()%>
                </td>
                <td valign="middle" align="center"  class="th0" colspan="10">
                  <button type="button" value="Update" class="addnewmember" onClick="return DoUpdate();" >
                    Send Intimation
                  </button>
                </td>
              </tr>
              </thead>
            </form>
          </table>
        </td>
      </tr>

      <tr>
        <td colspan="5">
          <center>
            <jsp:include page="page.jsp" flush="true">
              <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
              <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
              <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
              <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
              <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
              <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
              <jsp:param name="orderby" value=""/>
            </jsp:include>
          </center>
        </td>
      </tr>
    </table>
  </div>
  <br>
  <%
      }
      else
      {
        out.println(Functions.NewShowConfirmation("Result","No data found"));
      }
    }
    else if(msg != null)
    {
      out.println(Functions.NewShowConfirmation("Result",msg.toString()));
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
