<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>
<%@ page
        import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.payment.MultipleMemberUtill" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.UserVO" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 1/5/2016
  Time: 12:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Member User Terminal</title>
  <script language="javascript">
    function ToggleAll(checkbox)
    {
      flag = checkbox.checked;
      var checkboxes = document.getElementsByName("terminal");
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
  MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Merchant's Merchant Master
        <div style="float: right;">

        </div>
      </div>
      <form name="reversalform" action="AddUserTerminal?ctoken=<%=ctoken%>" method="post">

        <br>
        <p align="right"><button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-arrow-circle-left" aria-hidden="true"></i>Go Back</button>
          <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table  align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
          <tr>
            <td>

              <%
                String memberid = (String)request.getAttribute("memberid");
                /*if(request.getAttribute("error")!=null)
                {
                  String message = (String) request.getAttribute("error");
                  if(message != null)
                    out.println("<center><font class=\"textb\">"+message+"</font></center><br/><br/>");
                }*/

              %>
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="20%" class="textb" >Member Id</td>
                  <td width="0%" class="textb"></td>
                  <td width="22%" class="textb">
                    <input type="text" name="memberid" value="<%=request.getParameter("memberid")%> " disabled>
                  </td>

                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="20%" class="textb" >Member User Id</td>
                  <td width="0%" class="textb"></td>
                  <td width="22%" class="textb">
                    <input type="text" name="userid" value="<%=request.getAttribute("userid")%> " disabled>
                  </td>

                </tr>
              </table>
            </td>
          </tr>
        </table>
    </div>
  </div>
</div>

<div class="reporttable">

  <br>
  <%
    if(request.getAttribute("terminalVOList")!=null)
    {
      List<TerminalVO> terminalVOList = (List<TerminalVO>)request.getAttribute("terminalVOList");
      List<UserVO> userVOList = (List<UserVO>)request.getAttribute("userVOList");
      Set<String> stringSet=(Set<String>)request.getAttribute("stringSet");
      String userId = "";
    if(terminalVOList.size()>0)
    {
  %>

  <br>

  <table align=center style="width:80%" width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>


    <tr>
      <td style="width:5%" class="th0">
        <input type="checkbox" onClick="ToggleAll(this);" name="" class="no-margin">
      </td>
      <td valign="middle" align="center"  class="th0">Terminal ID</td>
      <td valign="middle" align="center" class="th0">Terminal Name</td>
      <%--<td valign="middle" align="center" class="th0">Member Id</td>--%>
      <td valign="middle" align="center" class="th0">Payment Type</td>
      <td valign="middle" align="center" class="th0">Card Type</td>
      <td valign="middle" align="center" class="th0">Min Trax. Amount</td>
      <td valign="middle" align="center" class="th0">Max Trax. Amount</td>

    </tr>
    </thead>
      <%
        String style="class=td1";
        String ext="light";
        String userTerminal = "";


        Hashtable hashtable=new Hashtable();
        for(TerminalVO terminalVO : terminalVOList)
        {
          style="class=tr0";
          String terminalid = terminalVO.getTerminalId();
          String paymentType = terminalVO.getPaymodeId();
          String cardType = terminalVO.getCardTypeId();
          String terminalName = terminalVO.getDisplayName();
          float minAmount = terminalVO.getMin_transaction_amount();
          float maxAmount = terminalVO.getMax_transaction_amount();
          String accountid = terminalVO.getAccountId();
          String isChecked="";
          /*for(UserVO userVO : userVOList)
          {
            userTerminal = userVO.getUserTerminalId();
          }*/

          /*else
          {

          }*/
          if(stringSet.contains(terminalid))
          {
            isChecked="checked";
          }

          out.println("<tr>");
          out.println("<td align=\"center\""+style+">&nbsp;<input type=\"checkbox\" class=\"no-margin\" name=\"terminal\" value=\""+terminalid+"\" "+isChecked+"></td>");
          out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(terminalid)+"<input type=\"hidden\" name=\"terminalid"+terminalid+"\" value=\""+terminalid+"\"></td>");
          out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(terminalName)+"<input type=\"hidden\" name=\"terminalname"+terminalid+"\" value=\""+terminalName+"\"></td>");
          out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(paymentType+"-"+terminalVO.getPaymentName())+"<input type=\"hidden\" name=\"paymenttypeid"+terminalid+"\" value=\""+paymentType+"\"></td>");
          out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(cardType+"-"+terminalVO.getCardType())+"<input type=\"hidden\" name=\"cardtypeid"+terminalid+"\" value=\""+cardType+"\"></td>");
          out.println("<td align=\"center\""+style+">&nbsp;"+minAmount+"<input type=\"hidden\" name=\"accountid"+terminalid+"\" value=\""+accountid+"\"></td>");
          out.println("<td align=\"center\""+style+">&nbsp;"+maxAmount+"</td>");
          //out.println("<td align=\"center\""+style+"><form action=\"/icici/servlet/MemberUserTerminalList?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"terminalid\" value=\""+terminalid+"\"><input type=\"hidden\" name=\"memberid\" value=\""+request.getParameter("memberid")+"\"><input type=\"hidden\" name=\"userid\" value=\""+request.getParameter("userid")+"\"><input type=\"hidden\" name=\"paymode\" value=\""+paymentType+"\"><input type=\"hidden\" name=\"cardtype\" value=\""+cardType+"\"><input type=\"submit\" name=\"submit\" value=\"Remove Terminal\" class=\"gotoauto\" ><input type=\"hidden\" name=\"action\" value=\"delete\"></form></td>");

          out.println("</tr>");
        }
        %>
    <thead>
    <tr>
      <td class="th0" colspan="14">
        <input type="hidden" value="<%=request.getParameter("memberid")%>" name="memberid">
        <input type="hidden" value="<%=request.getParameter("userid")%>" name="userid">
        <%--<center><button type="submit" class="addnewmember" value="Add">Add</button></center>--%>
        <button type="submit" class="addnewmember" style="margin-left:40px; ">
          <i class="fa fa-clock-o"></i>
          &nbsp;&nbsp;Update
        </button>
      </td>
    </tr>
    <%--<td width="50%" class="textb">
      <button type="submit" class="buttonform" style="margin-left:40px; ">
        <i class="fa fa-clock-o"></i>
        &nbsp;&nbsp;Search
      </button>
    </td>--%>
    </thead>
  </table>
    </form>
</div>


<%
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry","No records found."));
    }
  }
    else
    {
      out.println(Functions.NewShowConfirmation("Filter","Please provide MemberID to get TerminalList"));
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
