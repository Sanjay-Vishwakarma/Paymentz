<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>
<%@ page import="com.manager.vo.payoutVOs.MerchantRandomChargesVO" %>


<%--
  Created by IntelliJ IDEA.
  User: Kiran
  Date: 23/7/15
  Time: 6:53 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title></title>
</head>
<body>
  <% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Merchants Random Charges
        <div style="float: right;">
          <form action="/icici/manageMerchantRandomCharge.jsp?ctoken=<%=ctoken%>" method="POST">

            <button type="submit" value="Add New Merchant Random Charges" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Random Charges
            </button>
          </form>
        </div>
      </div>

      <form action="/icici/servlet/ListMerchantRandomCharges?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
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
                  <td width="11%" class="textb" >Member Id</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input  maxlength="15" type="text" name="memberid"  value=""  class="txtbox">
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Terminal Id</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input  maxlength="15" type="text" name="terminalid"  value="" class="txtbox">
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
  <div class="row" style="margin-top: 0px">
    <div class="col-lg-12">
      <div class="panel panel-default" style="margin-top: 0px">
        <div class="panel-heading" >
          Update Merchant Random Charges
        </div>
        <%
          MerchantRandomChargesVO  merchantRandomChargesVO=(MerchantRandomChargesVO)request.getAttribute("merchantRandomChargesVO");
          if(merchantRandomChargesVO!=null)
          {

            String style="class=tr0";
        %>
        <form action="/icici/servlet/ActionMerchantRandomCharges?ctoken=<%=ctoken%>" method="post" name="actionfrm">
          <input type="hidden"  name="action" value="update">
          <input type="hidden"  name="memberid" value="<%=merchantRandomChargesVO.getMemberId()%>">
          <input type="hidden"  name="terminalid" value="<%=merchantRandomChargesVO.getTerminalId()%>">
          <input type="hidden"  name="merchantrdmchargeid" value="<%=merchantRandomChargesVO.getMerchantRdmChargeId()%>">
          <table align="center" width="65%" cellpadding="2" cellspacing="2">


            <tbody>
            <tr>
              <td>
                <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                  <tbody>
                  <tr><td colspan="4">&nbsp;</td>
                  </tr>
                  <tr>
                    <td style="padding: 3px" width="2%" class="textb"&nbsp;></td>
                    <td style="padding: 3px" width="43%" class="textb">Member Id*</td>
                    <td style="padding: 3px" width="5%" class="textb">:</td>
                    <td style="padding: 3px" width="50%" class="textb" size="70" class="txtbox">
                      <input type="text" name="memberid" value="<%=merchantRandomChargesVO.getMemberId()%>" class="textb" disabled align="right">
                    </td>
                  </tr>
                  </tr>
                  <tr><td>&nbsp;</td></tr>
                  <tr>
                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                    <td style="padding: 3px" width="43%" class="textb">Terminal Id *</td>
                    <td style="padding: 3px" width="5%" class="textb">:</td>
                    <td style="padding: 3px" width="50%" class="textb">
                      <input type="text" name="terminalid" value="<%=merchantRandomChargesVO.getTerminalId()%>" class="txtbox"  align="right" disabled>
                    </td>
                  </tr>
                  <tr><td colspan="4">&nbsp;</td>
                  <tr>
                    <td style="padding: 3px" class="textb">&nbsp;</td>
                    <td style="padding: 3px" class="textb">Charge Value Type *</td>
                    <td style="padding: 3px" class="textb">:</td>
                    <td style="padding: 3px">
                      <select name="chargevaluetype" style="width: 165px" class="txtbox">
                        <%
                          if("FlatRate".equals(merchantRandomChargesVO.getChargeValueType()))
                          {
                        %>
                        <option value="FlatRate" selected> FlatRate</option>
                        <option value="Percentage"> Percentage</option>
                        <%}else{%>
                        <option value="FlatRate" > FlatRate</option>
                        <option value="Percentage" selected> Percentage</option>
                        <%}%>
                      </select>
                    </td>
                  </tr>
                  <tr><td colspan="4">&nbsp;</td>
                  <tr>
                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                    <td style="padding: 3px" width="43%" class="textb">Charge Name*</td>
                    <td style="padding: 3px" width="5%" class="textb">:</td>
                    <td style="padding: 3px" width="50%" class="textb">
                      <input class="txtbox"  style="width: 165px" type="text" name="chargename" value="<%=merchantRandomChargesVO.getChargeName()%>">
                    </td>
                  </tr>
                  <tr><td colspan="4">&nbsp;</td>
                  <tr>
                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                    <td style="padding: 3px" width="43%" class="textb">Keyword</td>
                    <td style="padding: 3px" width="5%" class="textb">:</td>
                    <td style="padding: 3px" width="50%" class="textb">
                      <input class="txtbox"  style="width: 165px" type="text" value="GrossBalanceAmount" disabled readonly>
                    </td>
                  </tr>
                  <tr><td>&nbsp;</td></tr>
                  <tr>
                    <td style="padding: 3px" class="textb">&nbsp;</td>
                    <td style="padding: 3px" class="textb">Charge Rate *</td>
                    <td style="padding: 3px" class="textb">:</td>
                    <td style="padding: 3px">
                      <input class="txtbox"  type="text" name="chargerate" align="right" value="<%=Functions.round(merchantRandomChargesVO.getChargeRate(),2)%>">
                    </td>
                  </tr>
                  <tr><td>&nbsp;</td></tr>
                  <tr>
                    <td style="padding: 3px" class="textb">&nbsp;</td>
                    <td style="padding: 3px" class="textb">Charge Counter *</td>
                    <td style="padding: 3px" class="textb">:</td>
                    <td style="padding: 3px">
                      <input class="txtbox"  type="text" name="chargecounter" align="right" value="<%=merchantRandomChargesVO.getChargeCounter()%>">
                    </td>
                  </tr>
                  <tr><td>&nbsp;</td></tr>
                  <tr>
                    <td style="padding: 3px" class="textb">&nbsp;</td>
                    <td style="padding: 3px" class="textb">Charge Amount *</td>
                    <td style="padding: 3px" class="textb">:</td>
                    <td style="padding: 3px">
                      <input class="txtbox" type="text" name="chargeamount" align="right" value="<%=Functions.round(merchantRandomChargesVO.getChargeAmount(),2)%>">
                    </td>
                  </tr>
                  <tr><td>&nbsp;</td></tr>
                  <tr>
                    <td style="padding: 3px" class="textb">&nbsp;</td>
                    <td style="padding: 3px" class="textb">Charge Value *</td>
                    <td style="padding: 3px" class="textb">:</td>
                    <td style="padding: 3px">
                      <input class="txtbox"  type="text" name="chargevalue" align="right" value="<%=Functions.round(merchantRandomChargesVO.getChargeValue(),2)%>">
                    </td>
                  </tr>
                  <tr><td>&nbsp;</td></tr>
                  <tr><td>&nbsp;</td></tr>
                  <tr>
                    <td style="padding: 3px" class="textb">&nbsp;</td>
                    <td style="padding: 3px" class="textb">Charge Remark *</td>
                    <td style="padding: 3px" class="textb">:</td>
                    <td style="padding: 3px">
                      <input class="txtbox"  type="text" name="chargeremark" align="right" value="<%=merchantRandomChargesVO.getChargeRemark()%>">
                    </td>
                  </tr>
                  <tr><td>&nbsp;</td></tr>

                  <tr>
                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                    <td style="padding: 3px" width="43%" class="textb"></td>
                    <td style="padding: 3px" width="5%" class="textb"></td>
                    <td style="padding: 3px" width="50%" class="textb">
                      <button type="submit" class="buttonform">
                        <i class="fa fa-sign-in"></i>
                        &nbsp;&nbsp;Update
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
      }
      else if(request.getAttribute("statusMsg")!=null)
      {
        out.println("<div class=\"reportable\">");
        out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("statusMsg")) );
        out.println("</div>");
      }
      else
      {
        out.println("<div class=\"reportable\">");
        out.println(Functions.NewShowConfirmation("Result","Invalid Action"));
        out.println("</div>");
      }
    }
    else
    {
      response.sendRedirect("/icici/logout.jsp");
      return;
    }
  %></body>
</html>
