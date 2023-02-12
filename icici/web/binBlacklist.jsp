<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.BlacklistVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.List" %>
<%@ page import="com.directi.pg.Logger" %>
<%--
  Created by IntelliJ IDEA.
  User: Mahima
  Date: 6/10/2018
  Time: 4:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="blocktab.jsp"%>
<%!
  Logger logger= new Logger("binBlacklist.jsp");
%>
<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
<html>
<head>
  <title>Block Bin</title>
  <script>
    $(document).ready(function(){
      $("#sameBin").on("click", function () {
        var startBin
        if ($(this).is(":checked")) {
          binStart = $('[name="startBin"]').val();
          $("#endBin").val(binStart ).prop("readonly", true);
        } else {
          $("#endBin").val("").prop("readonly", false);
        }
      });
    });
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
        document.BlacklistBin.submit();
      }
    }
  </script>
</head>
<%
  session.setAttribute("submit","Block Bin");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  String pgtypeid = request.getParameter("pgtypeid")==null?"":request.getParameter("pgtypeid");
  String memberId = request.getParameter("toid")==null?"":request.getParameter("toid");
  String binStart=request.getParameter("startBin")==null?"":request.getParameter("startBin");
  String binEnd=request.getParameter("endBin")==null?"":request.getParameter("endBin");
  String reason= request.getParameter("reason")==null?"":request.getParameter("reason");
  String remark= request.getParameter("remark")==null?"":request.getParameter("remark");
%>
<body>
<div class="row" style="margin-top: 0px">
  <div class="col-lg-12">
    <div class="panel panel-default"style="margin-top: 0px">
      <div class="panel-heading" >
        Blocked Bin List
        <div style="float: right;">
          <form action="/icici/uploadBlacklistBin.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
            <button type="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Upload Bulk Bin
            </button>
          </form>
        </div>
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
      <form action="/icici/servlet/BlacklistBin?ctoken=<%=ctoken%>" name="frm1" method="post">
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <table align="center" width="25%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr><td colspan="4">&nbsp;</td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Gateway</td>
                  <td class="textb">:</td>
                  <td>
                    <input name="pgtypeid" id="gateway1" value="" class="txtbox" autocomplete="on">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Account ID*</td>
                  <td class="textb">:</td>
                  <td>
                    <input name="accountid" id="accountid1" value="" class="txtbox" autocomplete="on">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">Member ID*</td>
                  <td width="5%" class="textb">:</td>
                  <td>
                    <input name="toid" id="memberid1" value="" class="txtbox" autocomplete="on">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">Start Bin*</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb">
                    <input type=text  id="startBin" name="startBin" maxlength="6" size="6"  class="txtbox" style="width:60px" value="">
                    <input type="checkbox" id="sameBin" style="width:40px">Single Bin
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">End Bin*</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb">
                    <input type=text  id="endBin" name="endBin" maxlength="6" size="6"  class="txtbox" style="width:60px" value="">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">Reason*</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb">
                    <input type=text   name="reason"  class="txtbox" maxlength="100" size="10"  value="">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">Remark</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb">
                    <input type=text   name="remark"  class="txtbox" maxlength="100" size="10"  value="">
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
                    <button type="button" name="bt" value="Search" onclick="{document.frm1.sbtn.value=this.value;document.frm1.submit();}" class="buttonform">
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
      </table>
    </div>
  </div>
</div>

<div class="reporttable">
  <%
    if(request.getAttribute("listofBin")!=null)
    {
      List<BlacklistVO> iList = (List<BlacklistVO>)request.getAttribute("listofBin");
      PaginationVO paginationVO=(PaginationVO) request.getAttribute("paginationVO");
      paginationVO.setInputs("ctoken=" + ctoken+paginationVO.getInputs());
      if(iList.size()>0)
      {
  %>
  <form name="BlacklistBin" action="/icici/servlet/BlacklistBin?ctoken=<%=ctoken%>" method="post">
  <table align=center width="100%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td valign="middle" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
      <td  align="center" class="th0">Member ID</td>
      <td  align="center" class="th0">Account ID</td>
      <td  align="center" class="th0">Start Bin </td>
      <td  align="center" class="th0">End Bin </td>
      <td  align="center"  class="th0">Reason</td>
      <td  align="center" class="th0">TimeStamp</td>
      <td  align="center" class="th0">Remark</td>
      <td  align="center" class="th0">Action Executor Id</td>
      <td  align="center" class="th0">Action Executor Name</td>
    </tr>
    </thead>
    <%
      String role="Admin";
      String username=(String)session.getAttribute("username");
      String actionExecutorId=(String)session.getAttribute("merchantid");
      String actionExecutorName=role+"-"+username;
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
        String Remark="";
        if (functions.isValueNull(blacklistVO.getRemark()))
        {
          Remark= blacklistVO.getRemark();
        }
        else
        {
          Remark ="-";
        }
        out.println("<tr>");
        out.println("<td " + style + " align=\"center\">&nbsp;<input type=\"checkbox\" name=\"id\" value=\"" + blacklistVO.getId()+ "\"></td>");
        out.println("<td align=center "+style+">&nbsp;" + ESAPI.encoder().encodeForHTML(blacklistVO.getMemberId())+"</td>");
        out.println("<td align=center "+style+">&nbsp;" + ESAPI.encoder().encodeForHTML(blacklistVO.getAccountId())+"</td>");
        out.println("<td align=center "+style+">&nbsp;" + ESAPI.encoder().encodeForHTML(blacklistVO.getBinStart())+"</td>");
        out.println("<td align=center "+style+">&nbsp;" + ESAPI.encoder().encodeForHTML(blacklistVO.getBinEnd())+"</td>");
        out.println("<td align=center "+style+">" +blacklistVO.getBlacklistReason()+"</td>");
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
    out.println(Functions.NewShowConfirmation("Filter", "Please provide the data to get Blocked Bin List"));
  }

%>
</body>
</html>