<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.WhitelistingDetailsVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Mahima
  Date: 6/10/2018
  Time: 4:12 PM
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>
  <title>1.Whitelist Module> WhiteList Bin</title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
</head>
<body>
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
  function Delete()
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
    if (confirm("Do you really want to delete all selected Data."))
    {
      document.WhitelistBin.submit();
    }
  }
</script>
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  String pgtypeid = request.getParameter("pgtypeid")==null?"":request.getParameter("pgtypeid");
  String memberId = request.getParameter("toid")==null?"":request.getParameter("toid");
  String accountId=request.getParameter("accountid")==null?"":request.getParameter("accountid");
  String binStart=request.getParameter("startBin")==null?"":request.getParameter("startBin");
  String binEnd=request.getParameter("endBin")==null?"":request.getParameter("endBin");
  String startCard="0000000000";
  //startCard=request.getParameter("startCard")==null?"":request.getParameter("startCard");
  String endCard="9999999999";
  //endCard=request.getParameter("endCard")==null?"":request.getParameter("endCard");
  Functions functions=new Functions();
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        WhiteList Bin
        <div style="float: right;">
          <form action="/icici/uploadBin.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
            <button type="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Upload Bin
            </button>
          </form>
        </div>
      </div>
      <br>
      <form action="/icici/servlet/WhitelistBin?ctoken=<%=ctoken%>" name="frm1" method="post">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <%
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
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Gateway</td>
                  <td class="textb">:</td>
                  <td>
                    <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Account ID*</td>
                  <td class="textb">:</td>
                  <td>
                    <input name="accountid" id="accountid1" value="<%=accountId%>" class="txtbox" autocomplete="on">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">Member ID*</td>
                  <td width="5%" class="textb">:</td>
                  <td>
                    <input name="toid" id="memberid1" value="<%=memberId%>" class="txtbox" autocomplete="on">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">Start Bin*</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb">
                    <input type=text  id="startBin" name="startBin" maxlength="6" size="6"  class="txtbox" style="width:60px" value="<%=binStart%>">
                    <input type="checkbox" id="sameBin" style="width:40px">Single Bin
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>

                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">Start Card*</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb">
                    <input type=text  id="startCard" name="startCard" maxlength="13" size="6"  class="txtbox" style="width:100px" value="<%=startCard%>">
                    <%--<input type="checkbox" id="sameBin" style="width:40px">Single Bin--%>
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">End Bin*</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb">
                    <input type=text  id="endBin" name="endBin" maxlength="6" size="6"  class="txtbox" style="width:60px" value="<%=binEnd%>">
                  </td>
                </tr>

                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">End Card*</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb">
                    <input type=text  id="endCard" name="endCard" maxlength="13" size="6"  class="txtbox" style="width:100px" value="<%=endCard%>">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                  <td>&nbsp;&nbsp;</td>
                  <td width="2%" class="textb">
                    <input type="hidden" name="bbtn" />
                    <input type="button" name="bt" value="WhiteList" onclick="{document.frm1.bbtn.value=this.value;document.frm1.submit();}" class="buttonform">
                  </td>
                  <td>&nbsp;&nbsp;</td>
                  <td>
                    <input type="hidden" name="sbtn" />
                    <input type="button" name="bt" value="Search" onclick="{document.frm1.sbtn.value=this.value;document.frm1.submit();}" class="buttonform">
                  </td>
                </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
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
<div class="reporttable">
  <%
    String currentblock=request.getParameter("currentblock");

    if(currentblock==null)
      currentblock="1";
    if(request.getAttribute("listofBin")!=null)
    {
      List<WhitelistingDetailsVO> iList = (List<WhitelistingDetailsVO>)request.getAttribute("listofBin");
      PaginationVO paginationVO=(PaginationVO) request.getAttribute("paginationVO");
      paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);

      if(iList.size()>0)
      {
  %>
  <form name="WhitelistBin" action="/icici/servlet/WhitelistBin?ctoken=<%=ctoken%>" method="post">
    <table align=center width="100%" class="table table-striped table-bordered table-hover table-green dataTable">
      <thead>
      <tr>
        <td valign="middle" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
        <td  align="center" class="th0">Member ID</td>
        <td  align="center" class="th0">Account ID</td>
        <td  align="center" class="th0">Start Bin - Start Card</td>
        <td  align="center" class="th0">End Bin - End Card </td>
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
        String startBin="";
        String endBin="";
        String cardStart="";
        String cardEnd="";
        int pos = 1;
        for(WhitelistingDetailsVO whitelistingDetailsVO : iList)
        {
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
          if(functions.isValueNull(whitelistingDetailsVO.getStartBin()))
            startBin=whitelistingDetailsVO.getStartBin();
          if(functions.isValueNull(whitelistingDetailsVO.getEndBin()))
            endBin=whitelistingDetailsVO.getEndBin();

          if(functions.isValueNull(whitelistingDetailsVO.getStartCard()))
            cardStart=whitelistingDetailsVO.getStartCard();
          if(functions.isValueNull(whitelistingDetailsVO.getEndCard()))
            cardEnd=whitelistingDetailsVO.getEndCard();


          if(functions.isValueNull(whitelistingDetailsVO.getActionExecutorId()))
          {

            actionExecutorId=whitelistingDetailsVO.getActionExecutorId();
          }
          else
          {
            actionExecutorId="-";
          }

          if(functions.isValueNull(whitelistingDetailsVO.getActionExecutorName()))
          {
            actionExecutorName=whitelistingDetailsVO.getActionExecutorName();
          }
          else{

            actionExecutorName="-";

          }
          out.println("<tr>");
          out.println("<td " + style + " align=\"center\">&nbsp;<input type=\"checkbox\" name=\"id\" value=\"" + whitelistingDetailsVO.getId()+ "\"></td>");
          out.println("<td align=center "+style+">&nbsp;" + ESAPI.encoder().encodeForHTML(whitelistingDetailsVO.getMemberid())+"</td>");
          out.println("<td align=center "+style+">&nbsp;" + ESAPI.encoder().encodeForHTML(String.valueOf(whitelistingDetailsVO.getAccountid()))+"</td>");
          out.println("<td align=center "+style+">&nbsp;" + ESAPI.encoder().encodeForHTML(startBin)+"-"+ ESAPI.encoder().encodeForHTML(cardStart)+"</td>");
          out.println("<td align=center "+style+">&nbsp;" + ESAPI.encoder().encodeForHTML(endBin)+"-"+ESAPI.encoder().encodeForHTML(cardEnd)+"</td>");
          out.println("<td align=center "+style+">&nbsp;" + ESAPI.encoder().encodeForHTML(actionExecutorId)+"</td>");
          out.println("<td align=center "+style+">&nbsp;" + ESAPI.encoder().encodeForHTML(actionExecutorName)+"</td>");
          //System.out.println(actionExecutorId);
          //System.out.println(actionExecutorName);
          out.println("</tr>");
          pos++;
        }
      %>
    </table>
    <table width="100%">
      <thead>
      <tr>
        <td width="15%" align="center">
          <input type="hidden" name="toid" value="<%=memberId%>">
          <input type="hidden" name="accountid" value="<%=accountId%>">
          <input type="hidden" name="delete" value="delete"><input type="button" name="delete" class="addnewmember" value="Delete" onclick="return Delete();">
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
    out.println(Functions.NewShowConfirmation("Filter", "Please provide the data to get Whitelist Bin List"));
  }
%>
</body>
</html>