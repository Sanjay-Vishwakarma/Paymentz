<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.BlacklistVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.function.Function" %>
<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 4/30/2015
  Time: 6:24 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="functions.jsp"%>
<%@ include file="blocktab.jsp"%>
<html>
<head>
  <title>Blocked Card List</title>
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
        document.BlackListDetails.submit();
      }
    }
  </script>
</head>
<%
  session.setAttribute("submit","Block Cards");
  if(buttonvalue==null)
  {
    buttonvalue=(String)session.getAttribute("submit");
    //System.out.println("buttonvalue::"+buttonvalue);
  }
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  String str = "";

%>
<body>
<div class="row" style="margin-top: 0px">
  <div class="col-lg-12">
    <div class="panel panel-default"style="margin-top: 0px">
      <div class="panel-heading" >
        Blocked Card List
        <div style="float: right;">
          <form action="/icici/uploadBlacklistCardDetails.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Upload Card Details" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Upload Bulk Card Details
            </button>
          </form>
        </div>
      </div><br>
      <%
        if (request.getAttribute("msg")!= null)
        {
          out.println("<center><font class=\"textb\"><b>"+request.getAttribute("msg")+"<br></b></font></center>");
        }
        String errormsg2 = (String) request.getAttribute("sErrorMessage");
        if (errormsg2 != null)
        {
          out.println("<center><font class=\"textb\"><b>"+errormsg2+"<br></b></font></center>");
        }
      %>
      <form action="/icici/servlet/BlackListDetails?ctoken=<%=ctoken%>" method="post" name="form">
        <table align="center" width="25%" cellpadding="2" cellspacing="2" >

          <tr>
            <td>

              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">

                <tr><td colspan="4">&nbsp;</td></tr>
                <%
                  String errormsg1 = (String) request.getAttribute("error");
                  if (errormsg1 != null)
                  {
                    out.println("<center><font class=\"textb\"><b>"+errormsg1+"<br></b></font></center>");
                  }
                  String firstsix = request.getParameter("firstsix")==null?"":request.getParameter("firstsix");
                  String lastfour = request.getParameter("lastfour")==null?"":request.getParameter("lastfour");
                  String remark = request.getParameter("remark")==null?"":request.getParameter("remark");
                  String reason1= request.getParameter("reason")==null?"":request.getParameter("reason");
                %>
                <tr margin="35px;">
                  <td width="0%" class="textb">&nbsp;</td>
                  <td width="40%" class="textb" align="center" >First_SIX & Last_Four</td>
                  <td width="0%" class="textb"></td>
                  <td width="100%" class="textb">
                    <input size="8" maxlength="6" type="text"  class="txtboxsmall" name="firstsix"  value="<%=firstsix%>">******<input maxlength="4" size="6" type="text" name="lastfour" class="txtboxsmall" value="<%=lastfour%>">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="40%" class="textb" align="center">Reason </td>
                  <td width="0%" class="textb"></td>
                  <td width="100%" class="textb">
                    <input maxlength="100" type="text" class="txtbox" name="reason" value="<%=reason1%>" size="20">
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr margin="35px;">
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="40%" class="textb" align="center" >Remark </td>
                  <td width="5%" class="textb"></td>
                  <td width="100%" class="textb">
                  <select name="remark" class="txtboxsmall" style="width:200px;" value="<%=remark%>">
                      <option value="">All</option>
                    <option value="Chargeback Received">Chargeback Received</option>
                    <option value="Fraud Received">Fraud Received</option>
                    <option value="Stolen Card">Stolen Card</option>
                    </select>
                  </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td>&nbsp;&nbsp;</td>
                  <td width="2%" class="textb">
                    <input type="hidden" name="bbtn" />
                    <%--<input type="button" name="bt" value="Block" onclick="{document.frm1.bbtn.value=this.value;document.frm1.submit();}" class="buttonform">--%>
                    <button type="button"  name="bt" value="Block" onclick="{document.form.bbtn.value=this.value;document.form.submit();}" class="buttonform"  >
                      <i class="fa fa-ban"></i>
                      &nbsp;&nbsp;Block
                    </button>
                  </td>
                  <td>&nbsp;&nbsp;</td>
                  <td width="2%" class="textb">
                    <input type="hidden" name="sbtn" />
                    <%--<input type="button" name="bt" value="Search"  onclick="{document.frm1.sbtn.value=this.value;document.frm1.submit();}" class="buttonform">--%>
                    <button type="button"  name="bt" value="Search" onclick="{document.form.sbtn.value=this.value;document.form.submit();}" class="buttonform" >
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
               <%-- <tr margin="35px;">
                  <td colspan="4" width="2%" class="textb" align="center"><button type="submit" name="" style="margin-left:107px;" class="buttonform"  >
                    <i class="fa fa-clock-o"></i>
                    &nbsp;&nbsp;Search
                  </button></td>
                </tr>--%>
                <tr><td colspan="4">&nbsp;</td>
                </tr>
              </table>
            </td>
        </table>
      </form>
    </div>
  </div>
</div>
<div class="reporttable">

  <%

    if(request.getAttribute("listofcards")!=null)
    {
      List<BlacklistVO> cList = (List<BlacklistVO>)request.getAttribute("listofcards");
      //BlacklistVO blacklistVO = (BlacklistVO) request.getAttribute("cardVo");
      PaginationVO paginationVO=(PaginationVO) request.getAttribute("paginationVO");
      paginationVO.setInputs("ctoken=" + ctoken+paginationVO.getInputs());
      Functions functions = new Functions();
      if(cList.size()>0)
      {

  %>
  <form name="BlackListDetails" action="/icici/servlet/BlackListDetails?ctoken=<%=ctoken%>" method="post">
  <table align=center width="100%" class="table table-striped table-bordered table-hover table-green dataTable">
   <thead>
    <tr>
      <td valign="middle" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
      <td  align="center" class="th0">First Six</td>
      <td  align="center" class="th0">Last Four</td>
      <td  align="center" class="th0">Blacklist Reason</td>
      <td  align="center" class="th0">Remark</td>
      <td  align="center" class="th0">Timestamp</td>
      <td  align="center" class="th0">Action Executor Id</td>
      <td  align="center" class="th0">Action Executor Name</td>
    </tr>
    </thead>
      <%

        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        String reason="";
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

        for(BlacklistVO blacklistVO : cList)
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

          if(functions.isValueNull(blacklistVO.getRemark()))
          {
            remark=blacklistVO.getRemark();
          }
          else
          {
            remark="-";
          }
          if(functions.isValueNull(blacklistVO.getBlacklistCardReason()))
          {
            reason=blacklistVO.getBlacklistCardReason();
          }
          else
          {
            reason="-";
          }
          out.println("<tr>");
          out.println("<td " + style + " align=\"center\">&nbsp;<input type=\"checkbox\" name=\"id\" value=\"" + blacklistVO.getId()+ "\"></td>");
          out.println("<td align=center "+style+">"+blacklistVO.getFirstSix()+"</td>");
          out.println("<td align=center "+style+">"+blacklistVO.getLastFour()+"</td>");
          out.println("<td align=center "+style+">"+reason+"</td>");
          out.println("<td align=center "+style+">"+remark+"</td>");
          out.println("<td align=center "+style+">"+blacklistVO.getTimestamp()+"</td>");
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
</body>
<%
      }
      else
      {
        out.println(Functions.NewShowConfirmation("Sorry","No records found."));
      }
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Filter", "Please provide the data to get Blocked Cards"));
    }
%>
</html>
