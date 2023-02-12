<%@ page import="com.directi.pg.Database,
                 com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.fraudruleconfVOs.RuleMasterVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.List" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 25/7/15
  Time: 3:27 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
  Logger logger = new Logger("accountFraudRuleList.jsp");
  session.setAttribute("submit","accountFraudRuleList");
%>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Fraud Management> Accounts Rule Setting</title>
  <script language="javascript">
    function doChanges(data,ruleid)
    {
      if(data.checked)
      {
        document.getElementById('cbk_'+ruleid).value="Enable";
      }
      else
      {
        document.getElementById('cbk_'+ruleid).value="Disable";
      }
      document.getElementById('status_'+ruleid).value=document.getElementById('cbk_'+ruleid).value;
    }

    function DoUpdate()
    {
      var checkboxes = document.getElementsByName("ruleid");

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
        alert("select at least one rule");
        return false;
      }
      if (confirm("Do you really want to update all selected rule."))
      {
        document.updateAccountLevelFraudConfform.submit();
      }
    }
    function ToggleAll(checkbox)
    {
      flag = checkbox.checked;
      var checkboxes = document.getElementsByName("ruleid");
      var total_boxes = checkboxes.length;

      for(i=0; i<total_boxes; i++ )
      {
        checkboxes[i].checked =flag;
      }
    }

  </script>
</head>
<body class="bodybackground">
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Manage Account Fraud Rule
        <div style="float: right;"> </div>
      </div>
      <form action="/icici/servlet/AccountFraudRuleList?ctoken=<%=ctoken%>" method="post" name="forms" >
        <table  align="center" width="85%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="20%" class="textb" >Fraud System AccountID </td>
                  <td width="42%" class="textb">
                    <select  name="fsaccountid" class="txtbox" style="width: 200px;" >
                      <option value="">Select Fraud System AccountID</option>
                      <%
                        Connection conn = null;
                        try
                        {
                          conn=Database.getConnection();
                          ResultSet rs = Database.executeQuery("select fsaccountid,accountname from fraudsystem_account_mapping",conn);
                          while (rs.next())
                          {
                            out.println("<option value="+rs.getString("fsaccountid")+">"+rs.getString("fsaccountid")+" - "+rs.getString("accountname")+"</option>");
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
                  <td width="40%" class="textb" ></td>
                  <td width="25%" class="textb"></td>
                  <td width="25%" class="textb"></td>
                  <td width="50%" class="textb">
                    <button type="submit" class="buttonform" style="margin-left:40px; ">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
              </table>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</div>
<%
  Functions functions=new Functions();
  String msg=(String)request.getAttribute("msg");
  String updateMsg=(String)request.getAttribute("updateMsg");
  List<RuleMasterVO> accountLevelRuleMapping=(List<RuleMasterVO>)request.getAttribute("accountLevelRuleMapping");
  String fsaccountid = (String) request.getAttribute("fsaccountid");
  if(accountLevelRuleMapping != null && fsaccountid != null)
  {
    PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
    paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
    int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
%>
<div class="reporttable">
  <table style="border:1px solid #34495e;" bgcolor="white" align="center" width="100%"   cellpadding="4" cellspacing="4">
    <tr align="center">
      <td valign="middle" align="center " style="font-size:15px " class="tr0">
      </td>
    </tr>
    <tr>
      <td style="padding:2px ">
        <form name="updateAccountLevelFraudConfform" action="/icici/servlet/ManageAccountFraudRule?ctoken=<%=ctoken%>" method="post">
          <input type="hidden" name="fsaccountid" value="<%=fsaccountid%>" >
          <table border="0"   width="100%"  align="center" class="table table-striped table-bordered table-hover table-green dataTable">
            <thead>
            <tr>
              <td style="padding:3px " valign="middle" class="th0" width="5%" ><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
              <td style="padding:3px " valign="middle" class="th0" width="5%" >Rule ID</td>
              <td style="padding:3px " valign="middle" class="th0" width="50%" >Rule Name</td>
              <td style="padding:3px " valign="middle" class="th0" width="10%" >Score</td>
              <td style="padding:3px " valign="middle" class="th0" width="6%" >Status</td>
            </tr>
            </thead>
            <%
              for(RuleMasterVO ruleMasterVO:accountLevelRuleMapping)
              {
                String ext="";
                if("Enable".equals(ruleMasterVO.getRuleDescription()))
                {
                  ext="checked";
                }
            %>
            <tr>
              <td valign="middle" align="center" width="5%"><input type="checkbox" name="ruleid" value="<%=ruleMasterVO.getRuleId()%>"></td>
              <td  align="center"  size="10" class="textb" width="5%"><%=ruleMasterVO.getRuleId()%> </td>
              <td  align="center"  size="10" class="textb" width="50%"><%=ruleMasterVO.getRuleName()%>
                <input type="hidden" class="textb" name="rulename_<%=ruleMasterVO.getRuleId()%>" id="rulename_<%=ruleMasterVO.getRuleId()%>" value="<%=ruleMasterVO.getRuleName()%>">
              </td>
              <td valign="middle" align="center" class="tr0" width="10%">
                <input type="text" class="textb" maxlength="2" style="width:75px" valign="middle" name="score_<%=ruleMasterVO.getRuleId()%>" value="<%=ruleMasterVO.getDefaultScore()%>" class="txtbox">
              </td>
              <td valign="middle" align="center" class="tr0" width="6%">
                <input type="hidden" name="status_<%=ruleMasterVO.getRuleId()%>" id="status_<%=ruleMasterVO.getRuleId()%>" value="<%=ruleMasterVO.getRuleDescription()%>">
                <input type="checkbox" id="cbk_<%=ruleMasterVO.getRuleId()%>" value="<%=ruleMasterVO.getRuleDescription()%>" valign="middle" <%=ext%> onclick="doChanges(this,<%=ruleMasterVO.getRuleId()%>)">
              </td>
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
              <td valign="middle" align="center"  class="th0" colspan="11">
                <button type="button" value="Update" class="addnewmember" onClick="return DoUpdate();" >
                  Update Selected
                </button>
              </td>
            </tr>
            </thead>
          </table>
        </form>
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
            <jsp:param name="page" value="AccountFraudRuleList"/>
            <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
            <jsp:param name="orderby" value=""/>
          </jsp:include>
        </center>
      </td>
    </tr>

  </table>
</div>
<%
    }
    else if(msg!=null)
    {
      out.println("<div class=\"reporttable\">");
      out.println(Functions.NewShowConfirmation("Result",msg));
      out.println("</div>");
    }
    else if(functions.isValueNull(updateMsg))
    {
      out.println("<div class=\"reporttable\">");
      out.println(Functions.NewShowConfirmation("Result",updateMsg));
      out.println("</div>");
    }
    else
    {
      out.println("<div class=\"reporttable\">");
      out.println(Functions.NewShowConfirmation("Result", "No Records Found."));
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
