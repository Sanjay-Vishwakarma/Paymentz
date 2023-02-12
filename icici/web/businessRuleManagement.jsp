<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.businessRuleVOs.RuleVO" %>
<%@ page import="com.manager.ProfileManagementManager" %>
<%@ page import="java.util.List" %>

<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 9/10/14
  Time: 3:21 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp" %>
<%!private ProfileManagementManager profileManagementManager = new ProfileManagementManager();
  private Functions functions = new Functions();
%>

<html>
<head>

  <title>IFE Rule Management> Business Rule</title>
</head>
<body class="bodybackground">

<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading" >
        Business Rule Details
        <div style="float: right;">
          <form action="/icici/servlet/SingleBusinessRuleDetails?ctoken=<%=ctoken%>" method="POST">

            <button class="addnewmember" type="submit" value="1_Add" name="action" style="width:300px ">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Business Rule
            </button>
          </form>
        </div>
      </div>
      <%

        if(request.getAttribute("error")!=null)
        {
          ValidationErrorList error = (ValidationErrorList) request.getAttribute("error");
          for (ValidationException errorList : error.errors())
          {
            out.println("<center><font class=\"textb\">" + errorList.getMessage() + "</font></center>");
          }
        }
        if(request.getAttribute("catchError")!=null)
        {
          out.println("<center><font class=\"textb\">" + request.getAttribute("catchError") + "</font></center>");
        }
        if(request.getAttribute("DELETED")!=null)
        {
          out.println("<center><font class=\"textb\">Deleted Successfully</font></center>");
        }
      %>

      <%
        List<RuleVO> businessRuleVOs=profileManagementManager.getListOfBusinessRuleDetails(null, null);
      %>
      <form action="/icici/servlet/BusinessRuleDetails?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">

          <tr>
            <td>

              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                <tr><td colspan="8">&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Rule Name</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select name="businessRuleId">
                      <%=profileManagementManager.getOptionTagForBusinessRuleWithoutId(businessRuleVOs, request.getParameter("businessRuleId")).toString()%>
                    </select>
                  </td>
                  <td width="6%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" ></td>
                  <td width="3%" class="textb"><button type="submit" class="buttonform" name="action">
                    <i class="fa fa-clock-o"></i>
                    &nbsp;&nbsp;Search
                  </button></td>
                  <td width="10%" class="textb">

                  </td>

                </tr>

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
    if(request.getAttribute("businessRuleVOList")!=null)
    {
      List<RuleVO> businessRuleVOList= (List<RuleVO>) request.getAttribute("businessRuleVOList");
      PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
      paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
      if(!businessRuleVOList.isEmpty())
      {
        int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
  %>
  <form action="/icici/servlet/SingleBusinessRuleDetails?ctoken=<%=ctoken%>" method="POST">
    <input type="hidden" name="businessRuleId" value="<%=request.getParameter("businessRuleId")%>"/>
    <table align=center width="100%" border="1" class="table table-striped table-bordered table-green dataTable">
      <tr>
        <td width="2%"valign="middle" align="center" class="th0">Sr&nbsp;No</td>
        <td width="15%" valign="middle" align="center" class="th0">Rule&nbsp;Name</td>
        <td width="15%" valign="middle" align="center" class="th0">Rule&nbsp;Description</td>
        <td width="25%" valign="middle" align="center" class="th0">Rule&nbsp;Label</td>
        <td width="25%" valign="middle" align="center" class="th0">Rule&nbsp;Type</td>
        <td width="25%" valign="middle" align="center" class="th0" colspan="3">Action</td>
      </tr>
      <%
        for(RuleVO  ruleVO:businessRuleVOList)
        {

      %>
      <tr>

        <td class="tr0" align="center"><%=srno%></td>
        <td class="tr0" align="center"><%=ruleVO.getName()%></td>
        <td class="tr0" align="center"><%=ruleVO.getDescription()%></td>
        <td class="tr0" align="center"><%=ruleVO.getLabel()%></td>
        <td class="tr0" align="center"><%=ruleVO.getRuleType()%></td>
        <td class="tr0" align="center">
          <button type="submit" class="button" value="<%=ruleVO.getId()+"_View"%>" name="action">View</button>
        </td>
        <td class="tr0" align="center">
          <button type="submit" class="button" value="<%=ruleVO.getId()+"_Edit"%>" name="action">Edit</button>
        </td>
        <td class="tr0" align="center">
          <button type="submit" class="button" value="<%=ruleVO.getId()+"_Delete"%>" name="action">Delete</button>
        </td>
      </tr>

      <%
          srno++;
        }
      %>
      <tr>
        <td  align="left" class="th0" colspan="2">Total Records : <%=paginationVO.getTotalRecords()%></td>
        <td  align="right" class="th0">Page No : <%=paginationVO.getPageNo()%></td>
        <td class="th0">&nbsp;</td>
        <td class="th0">&nbsp;</td>
        <td class="th0">&nbsp;</td>
        <td class="th0">&nbsp;</td>
        <td class="th0">&nbsp;</td>
      </tr>
    </table>
  </form>
  <div>
    <jsp:include page="page.jsp" flush="true">
      <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
      <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
      <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
      <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
      <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
      <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
      <jsp:param name="orderby" value=""/>
    </jsp:include>
  </div>

  <%
      }
      else
      {
        out.println(Functions.NewShowConfirmation("Sorry","No records found"));
      }
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Filter", "Please Provide search criteria for Business Rule Details"));
    }

  %>

</div>

</body>
</html>