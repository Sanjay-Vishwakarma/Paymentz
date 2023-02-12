<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.BlacklistVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 5/5/2015
  Time: 4:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="blocktab.jsp"%>
<html>
<head>
  <title>Block Name Address</title>
  <script>
    function ToggleAll(checkbox)
    {
      flag = checkbox.checked;
      var checkboxes = document.getElementsByName("id");
      var total_boxes = checkboxes.length;

      for(i=0; i<total_boxes; i++ )
      {
        checkboxes[i].checked =flag;
      }
    }
    function Unblock()
    {
      var checkboxes = document.getElementsByName("id");
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
        alert("Select at least one record");
        return false;
      }
      if (confirm("Do you really want to unblock all selected Data."))
      {
        document.BlacklistName.submit();
      }
    }
  </script>
</head>
<%
  session.setAttribute("submit","Block Names");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
%>
<body>
<div class="row" style="margin-top: 0px">
  <div class="col-lg-12">
    <div class="panel panel-default"style="margin-top: 0px">
      <div class="panel-heading" >
        Blocked Name List
      </div>
      <%
        Functions functions = new Functions();
        String errormsg1 = (String) request.getAttribute("error");
        if (errormsg1 != null)
        {
          out.println("<center><font class=\"textb\"><b>"+errormsg1+"<br></b></font></center>");
        }
        if(request.getAttribute("msg")!=null)
        {
          out.println("<center><font class=\"textb\"><b>"+request.getAttribute("msg")+"<br></b></font></center>");
        }
      %>
      <%--<table  align="center" width="100%" cellpadding="5" cellspacing="0" style="margin-left:2.5%;margin-right: 2.5% ">

        </tr>--%>
      <form action="/icici/servlet/BlacklistName?ctoken=<%=ctoken%>" name="frm1" method="post">
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <table align="center" width="45%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr>
                  <td colspan="4">&nbsp;</td>
                </tr>
                <tr>
                  <td  class="textb">&nbsp;</td>
                  <td  class="textb" >FirstName & LastName</td>
                  <td  class="textb">:</td>
                  <td>
                    <input type=text name="name" maxlength="50"  value="" class="txtbox" size="10">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td  class="textb">&nbsp;</td>
                  <td  class="textb" >Reason*</td>
                  <td  class="textb">:</td>
                  <td>
                    <input type=text name="reason" maxlength="100"  value="" class="txtbox" size="10">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td  class="textb">&nbsp;</td>
                  <td  class="textb" >Remark</td>
                  <td  class="textb">:</td>
                  <td>
                    <input type=text name="remark" maxlength="100"  value="" class="txtbox" size="10">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td>&nbsp;&nbsp;</td>
                  <td width="2%" class="textb">
                    <input type="hidden" name="bbtn" />
                    <button type="button" name="bt" value="Block" onclick="{document.frm1.bbtn.value=this.value;document.frm1.submit();}" class="buttonform">
                      <i class="fa fa-ban"></i>
                      &nbsp;&nbsp;Block
                    </button>
                  </td>
                  <td>&nbsp;&nbsp;</td>
                  <td>
                    <input type="hidden" name="sbtn" />
                    <button type="button" name="bt" value="Search" onclick="{document.frm1.sbtn.value=this.value;document.frm1.submit();}" class="buttonform" >
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
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

      <%-- <tr><td colspan="4">&nbsp;</td>
       </tr>
     </table>--%>

    </div>
  </div>
</div>

<div class="reporttable">
  <%
    if(request.getAttribute("listofname")!=null)
    {
      List<BlacklistVO> iList = (List<BlacklistVO>)request.getAttribute("listofname");
      PaginationVO paginationVO=(PaginationVO) request.getAttribute("paginationVO");
      paginationVO.setInputs("ctoken=" + ctoken+paginationVO.getInputs());
      if(iList.size()>0)
      {

  %>
  <form name="BlacklistName" action="/icici/servlet/BlacklistName?ctoken=<%=ctoken%>" method="post">
  <table align=center width="100%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td valign="middle" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
      <td  align="center" class="th0">Full Name</td>
      <td  align="center" class="th0">Reason</td>
      <td align="center" class="th0">TimeStamp</td>
      <td align="center" class="th0">Remark</td>
      <td  align="center" class="th0">Action Executor Id</td>
      <td  align="center" class="th0">Action Executor Name</td>
    </tr>
    </thead>

    <%
      String role="Admin";
      String username=(String)session.getAttribute("username");
      String actionExecutorId=(String)session.getAttribute("merchantid");
      String actionExecutorName=role+"-"+username;
      String Remark= "";
      String Reason= "";
      String style="class=td1";
      String ext="light";
      int pos = 1;
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
      for(BlacklistVO blacklistVO : iList)
      {
        if(functions.isValueNull(blacklistVO.getActionExecutorId()))
        {
          actionExecutorId=blacklistVO.getActionExecutorId();
        }
        else
        {
          actionExecutorId="-";
        }
        if(functions.isValueNull(blacklistVO.getActionExecutorName()))
        {
          actionExecutorName=blacklistVO.getActionExecutorName();
        }
        else
        {
          actionExecutorName="-";
        }
        if (functions.isValueNull(blacklistVO.getBlacklistReason()))
        {
          Reason = blacklistVO.getBlacklistReason();
        }
        else
        {
          Reason = "-";
        }
        if (functions.isValueNull(blacklistVO.getRemark()))
        {
          Remark = blacklistVO.getRemark();
        }
        else
        {
          Remark = "-";
        }
        out.println("<tr>");
        out.println("<td " + style + " align=\"center\">&nbsp;<input type=\"checkbox\" name=\"id\" value=\"" + blacklistVO.getId()+ "\"></td>");
        out.println("<td align=center "+style+">"+blacklistVO.getName()+"</td>");
        out.println("<td align=center "+style+">"+Reason+"</td>");
        out.println("<td align=center "+style+">"+blacklistVO.getTimestamp()+"</td>");
        out.println("<td align=center "+style+">"+Remark+"</td>");
        out.println("<td align=center "+style+">"+actionExecutorId+"</td>");
        out.println("<td align=center "+style+">"+actionExecutorName+"</td>");
        out.println("</tr>");
        pos++;
      }

    %>
  </table>
    <table width="100%">
      <thead>
      <tr>
        <td width="15%" align="center">
          <input type="hidden" name="unblock" value="unblock"><input type="button" name="unblock" class="addnewmember" value="Unblock" onclick="return Unblock();">
        </td>
      </tr>
      </thead>
    </table>
  </form>
  <table align=center valign=top><tr>
    <td align=center>
      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
        <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
        <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
        <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
        <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
        <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </tr>
  </table>
</div>
<%
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
    }
  }
  else
  {
    out.println(Functions.NewShowConfirmation("Filter", "Please provide the data to get Blocked Names List"));
  }

%>
</body>
</html>
