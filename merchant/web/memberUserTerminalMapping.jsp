<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="Top.jsp"%>
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
  User: ThinkPadT410
  Date: 2/1/2016
  Time: 12:21 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <title>Merchant User Terminal</title>
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
    function check(flagNew)
    {
      $("#reversalform").submit(function(){
        if(flagNew)
        {
          if ($('input:checkbox').filter(':checked').length < 1)
          {
            flagNew=false;
            alert("Check at least one Terminal!");
            return false;
          }
        }
      });
    }
  </script>
</head>
<body >
<%
  MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
  session.setAttribute("submit","User Management");
  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String memberUserTerminalMapping_Merchant_Master = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Merchant_Master"))?rb1.getString("memberUserTerminalMapping_Merchant_Master"): "Merchant Master List";
  String memberUserTerminalMapping_Go_Back = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Go_Back"))?rb1.getString("memberUserTerminalMapping_Go_Back"): "Go Back";
  String memberUserTerminalMapping_TerminalID = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_TerminalID"))?rb1.getString("memberUserTerminalMapping_TerminalID"): "Terminal ID";
  String memberUserTerminalMapping_Terminal_Name = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Terminal_Name"))?rb1.getString("memberUserTerminalMapping_Terminal_Name"): "Terminal Name";
  String memberUserTerminalMapping_Payment_Type = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Payment_Type"))?rb1.getString("memberUserTerminalMapping_Payment_Type"): "Payment Type";
  String memberUserTerminalMapping_Card_Type = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Card_Type"))?rb1.getString("memberUserTerminalMapping_Card_Type"): "Card Type";
  String memberUserTerminalMapping_Min = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Min"))?rb1.getString("memberUserTerminalMapping_Min"): "Min Trax. Amount";
  String memberUserTerminalMapping_Max = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Max"))?rb1.getString("memberUserTerminalMapping_Max"): "Max Trax. Amount";
  String memberUserTerminalMapping_Update = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Update"))?rb1.getString("memberUserTerminalMapping_Update"): "Update";
  String memberUserTerminalMapping_Sorry = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Sorry"))?rb1.getString("memberUserTerminalMapping_Sorry"): "Sorry";
  String memberUserTerminalMapping_no = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_no"))?rb1.getString("memberUserTerminalMapping_no"): "No records found for given search criteria.";
  String memberUserTerminalMapping_Filter = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Filter"))?rb1.getString("memberUserTerminalMapping_Filter"): "Filter";
  String memberUserTerminalMapping_please = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_please"))?rb1.getString("memberUserTerminalMapping_please"): "Please provide MerchantID to get TerminalList";

%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <form action="/merchant/servlet/MemberUserList?ctoken=<%=ctoken%>"  method="post">
        <div class="pull-right">
          <div class="btn-group">


            <%
              Enumeration<String> stringEnumeration=request.getParameterNames();
              while(stringEnumeration.hasMoreElements())
              {
                String name=stringEnumeration.nextElement();
                if("trackingid".equals(name))
                {
                  String[] values=request.getParameterValues(name);

                  out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)[1]+"'/>");
                }
                else
                  out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
              }
            %>
            <%--<button class="btn-xs" type="submit" name="B2" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/merchant/images/goBack.png">
            </button>--%>
            <button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp;<%=memberUserTerminalMapping_Go_Back%></button>


          </div>
        </div>
      </form>
      <br><br>

      <form name="reversalform" id="reversalform" action="AddUserTerminal?ctoken=<%=ctoken%>" method="post">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <div class="row">
          <div class="col-md-12">
            <div class="widget">
              <div class="widget-header">
                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=memberUserTerminalMapping_Merchant_Master%></strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>


              <div class="widget-content" style="overflow-y: auto;">
                <%--<div class="table-responsive datatable">--%>

                <br>
                <%
                  String memberid = (String)request.getAttribute("merchantid");
                  if(request.getAttribute("terminalVOList")!=null)
                  {
                    List<TerminalVO> terminalVOList = (List<TerminalVO>)request.getAttribute("terminalVOList");
                    List<UserVO> userVOList = (List<UserVO>)request.getAttribute("userVOList");
                    Set<String> stringSet=(Set<String>)request.getAttribute("stringSet");

                    if(terminalVOList.size()>0)
                    {
                %>

                <br>
                <table class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead>
                  <tr style="color: white;">
                    <td style="width:5%" style="text-align: center">
                      <input type="checkbox" onClick="ToggleAll(this);" name="" class="no-margin" style="margin-left: 38%;">
                    </td>
                    <th style="text-align: center"><%=memberUserTerminalMapping_TerminalID%></th>
                    <th style="text-align: center"><%=memberUserTerminalMapping_Terminal_Name%></th>
                    <th style="text-align: center"><%=memberUserTerminalMapping_Payment_Type%></th>
                    <th style="text-align: center"><%=memberUserTerminalMapping_Card_Type%></th>
                    <th style="text-align: center"><%=memberUserTerminalMapping_Min%></th>
                    <th style="text-align: center"><%=memberUserTerminalMapping_Max%></th>

                  </tr>
                  </thead>
                  <tbody>
                  <%
                    String style="class=td1";
                    String ext="light";
                    String userTerminal = "";


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
                      out.println("<td align=\"center\" "+style+">&nbsp;<input type=\"checkbox\" class=\"no-margin\" name=\"terminal\" value=\""+terminalid+"\" "+isChecked+"></td>");
                      out.println("<td align=\"center\" data-label=\"Terminal ID\">&nbsp;"+ESAPI.encoder().encodeForHTML(terminalid)+"<input type=\"hidden\" name=\"terminalid"+terminalid+"\" value=\""+terminalid+"\"></td>");
                      out.println("<td align=\"center\" data-label=\"Terminal Name\">&nbsp;"+ESAPI.encoder().encodeForHTML(terminalName)+"<input type=\"hidden\" name=\"terminalname"+terminalid+"\" value=\""+terminalName+"\"></td>");
                      out.println("<td align=\"center\" data-label=\"Payment Type\">&nbsp;"+ESAPI.encoder().encodeForHTML(paymentType+"-"+terminalVO.getPaymentName())+"<input type=\"hidden\" name=\"paymenttypeid"+terminalid+"\" value=\""+paymentType+"\"></td>");
                      out.println("<td align=\"center\" data-label=\"Card Type\">&nbsp;"+ESAPI.encoder().encodeForHTML(cardType+"-"+terminalVO.getCardType())+"<input type=\"hidden\" name=\"cardtypeid"+terminalid+"\" value=\""+cardType+"\"></td>");
                      out.println("<td align=\"center\" data-label=\"Min Trax. Amount\">&nbsp;"+minAmount+"<input type=\"hidden\" name=\"accountid"+terminalid+"\" value=\""+accountid+"\"></td>");
                      out.println("<td align=\"center\" data-label=\"Max Trax. Amount\">&nbsp;"+maxAmount+"</td>");

                      out.println("</tr>");
                    }
                  %>
                  <td colspan="14">
                    <input type="hidden" value="<%=request.getParameter("merchantid")%>" name="merchantid">
                    <input type="hidden" value="<%=request.getParameter("userid")%>" name="userid">

                    <center>
                      <div class="form-group col-md-12">
                        <button type="submit" class="btn btn-default" onclick="check(true)" <%--style="margin-left:40px;background-color: #68C39F; color: #ffffff "--%>>
                          <i class="fa fa-clock-o"></i>
                          &nbsp;&nbsp;<%=memberUserTerminalMapping_Update%>
                        </button>
                      </div>
                    </center>

                  </td>
                  </tbody>
                  <thead>
                  <tr>

                  </tr>

                  <%--<td width="50%" class="textb">
                    <button type="submit" class="buttonform" style="margin-left:40px; ">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>--%>

                  </thead>
                </table>

              </div>
            </div>
          </div>
        </div>
        <%--</div>--%>

        <%
            }
            else
            {
              out.println(Functions.NewShowConfirmation1(memberUserTerminalMapping_Sorry,memberUserTerminalMapping_no));
            }
          }
          else
          {
            out.println(Functions.NewShowConfirmation1(memberUserTerminalMapping_Filter,memberUserTerminalMapping_please));

          }


        %>
      </form>
    </div>
  </div>
</div>
</body>
</html>