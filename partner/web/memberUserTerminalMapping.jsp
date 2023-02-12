<%@ page import="com.directi.pg.Functions" %>
<%@ include file="top.jsp"%>
<%@ page import="com.directi.pg.LoadProperties,com.manager.vo.TerminalVO,com.manager.vo.UserVO"%>
<%@ page import="com.payment.MultipleMemberUtill" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 1/5/2016
  Time: 12:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","memberChildList");
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title><%=company%>| Merchant User Management</title>
  <script language="javascript">
      $(document).ready(function () {
          $('input:checkbox[name=alltrans]').parent().children().eq(1).click(function () {

              if($('input:checkbox[name=alltrans]').parent().hasClass("checked"))
              {
                  var checkboxes = document.getElementsByName("terminal");
                  var total_boxes = checkboxes.length;

                  for(i=0; i<total_boxes; i++ )
                  {
                      checkboxes[i].checked =true;
                  }
                  $('input:checkbox[name=terminal]').parent().addClass("checked");
              }
              else
              {
                  var checkboxes = document.getElementsByName("terminal");
                  var total_boxes = checkboxes.length;

                  for(i=0; i<total_boxes; i++ )
                  {
                      checkboxes[i].checked =false;
                  }
                  $('input:checkbox[name=terminal]').parent().removeClass("checked");
              }
          });
      });
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


  <style type="text/css">

    input[type=radio], input[type=checkbox]{
      transform: scale(2);
      -ms-transform: scale(2);
      -webkit-transform: scale(2);
      padding: 10px;
    }

    /*@media (max-width: 640px) {

        table thead{
            display: table-row-group;
        }

        table thead tr th:first-child {
            display: block!important;
        }

        table thead tr th:nth-of-type(2){
            display: none;
        }

        table thead tr th:nth-of-type(3){
            display: none;
        }

        table thead tr th:nth-of-type(4){
            display: none;
        }

        table thead tr th:nth-of-type(5){
            display: none;
        }

        table thead tr th:nth-of-type(6){
            display: none;
        }

        table thead tr th:nth-of-type(7){
            display: none;
        }

    }*/


  </style>

<%--  <script type="">
  $(document).ready(function() {
    //alert("hi checkbox");
    $('div.icheckbox_square-aero').remove();
    $('ins.iCheck-helper').remove();

    /* $('.icheckbox_square-aero').removeClass('icheckbox_square-aero');
     $('.iCheck-helper').removeClass('iCheck-helper');*/
  });
</script>--%>

</head>
<body>

<%
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String memberUserTerminalMapping_Member_Terminal_Details = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Member_Terminal_Details")) ? rb1.getString("memberUserTerminalMapping_Member_Terminal_Details") : "Member Terminal Details";
    String memberUserTerminalMapping_TerminalID = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_TerminalID")) ? rb1.getString("memberUserTerminalMapping_TerminalID") : "Terminal ID";
    String memberUserTerminalMapping_Terminal_Name = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Terminal_Name")) ? rb1.getString("memberUserTerminalMapping_Terminal_Name") : "Terminal Name";
    String memberUserTerminalMapping_Payment_Type = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Payment_Type")) ? rb1.getString("memberUserTerminalMapping_Payment_Type") : "Payment Type";
    String memberUserTerminalMapping_Card_Type = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Card_Type")) ? rb1.getString("memberUserTerminalMapping_Card_Type") : "Card Type";
    String memberUserTerminalMapping_Min = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Min")) ? rb1.getString("memberUserTerminalMapping_Min") : "Min Trax. Amount";
    String memberUserTerminalMapping_Max = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Max")) ? rb1.getString("memberUserTerminalMapping_Max") : "Max Trax. Amount";
    String memberUserTerminalMapping_Update_Selected = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Update_Selected")) ? rb1.getString("memberUserTerminalMapping_Update_Selected") : "Update Selected";
    String memberUserTerminalMapping_Sorry = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_Sorry")) ? rb1.getString("memberUserTerminalMapping_Sorry") : "Sorry";
    String memberUserTerminalMapping_no = StringUtils.isNotEmpty(rb1.getString("memberUserTerminalMapping_no")) ? rb1.getString("memberUserTerminalMapping_no") : "No records found.";
  MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

        <div class="pull-right">
            <div class="btn-group">
                <form action="/partner/net/MemberUserList?ctoken=<%=ctoken%>" method="post" name="form">
                    <%
                        Enumeration<String> stringEnumeration=request.getParameterNames();
                        while(stringEnumeration.hasMoreElements())
                        {
                            String name=stringEnumeration.nextElement();
                            if("accountid".equals(name))
                            {
                                String[] values=request.getParameterValues(name);
                                out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)[1]+"'/>");
                            }
                            else
                                out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                        }
                    %>

                    <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
                        <img style="height: 35px;" src="/partner/images/goBack.png">
                    </button>
                </form>
            </div>
        </div>
        <br><br><br>
      <form name="reversalform" action="AddUserTerminal?ctoken=<%=ctoken%>" method="post">
      <input type="hidden" value="<%=ctoken%>" name="ctoken">
      <%--<div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><i class="fa fa-th-large"></i>&nbsp;&nbsp;<strong>Merchant's Merchant Master</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
&lt;%&ndash;            <div class="pull-right">
              <div class="btn-group">
                <form action="/partner/memberChildSignup.jsp?ctoken=<%=ctoken%>"  method="POST">
                  <button class="btn-xs" type="submit" value="memberChildList" name="submit" name="B1" style="background: white;border: 0;">
                    <img style="height: 35px;" src="/partner/images/newuser.png">
                  </button>
                </form>
              </div>
            </div>&ndash;%&gt;

            <div class="widget-content padding">
              <div id="horizontal-form">

                  <%
                    String memberid = (String)request.getAttribute("memberid");
                                        /*if(request.getAttribute("error")!=null)
                                        {
                                          String message = (String) request.getAttribute("error");
                                          if(message != null)
                                            out.println("<center><font class=\"textb\">"+message+"</font></center><br/><br/>");
                                        }*/

                  %>

                  <div class="form-group col-md-6">
                    <label class="col-sm-4 control-label">Member ID</label>
                    <div class="col-sm-6">
                      <input type="text" name="memberid" class="form-control" value="<%=request.getParameter("memberid")%> " disabled>
                    </div>
                  </div>

                  <div class="form-group col-md-6">
                    <label class="col-sm-4 control-label">Member User ID</label>
                    <div class="col-sm-6">
                      <input type="text" name="userid" class="form-control" value="<%=request.getAttribute("userid")%> " disabled>
                    </div>
                  </div>


                  &lt;%&ndash;<div class="form-group col-md-3 has-feedback">&nbsp;</div>&ndash;%&gt;
                  &lt;%&ndash;<div class="form-group col-md-3">
                      <div class="col-sm-offset-2 col-sm-3">
                          <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                              &nbsp;&nbsp;Search</button>
                      </div>
                  </div>&ndash;%&gt;



              </div>
            </div>
          </div>
        </div>
      </div>--%>





      <div class="row reporttable">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=memberUserTerminalMapping_Member_Terminal_Details%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">


              <%
                if(request.getAttribute("terminalVOList")!=null)
                {
                  List<TerminalVO> terminalVOList = (List<TerminalVO>)request.getAttribute("terminalVOList");
                  List<UserVO> userVOList = (List<UserVO>)request.getAttribute("userVOList");
                  Set<String> stringSet=(Set<String>)request.getAttribute("stringSet");
                  //String userId = "";
                  if(terminalVOList.size()>0)
                  {
              %>


              <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                <%--<script type="">
                  $(document).ready(function()
                  {
                    $(".thcheckbox").append("<input type=\"checkbox\" onClick=\"ToggleAll(this);\" name=\"\" class=\"no-margin\">")
                  });
                </script>--%>

                <thead >
                <tr style="background-color: #7eccad !important;color: white;">
                  <th style="text-align: center;" class="thcheckbox"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></th>
                  <%--<th style="text-align: center;">Check</th>--%>
                  <th style="text-align: center;"><%=memberUserTerminalMapping_TerminalID%></th>
                  <th style="text-align: center;"><%=memberUserTerminalMapping_Terminal_Name%></th>
                  <th style="text-align: center;"><%=memberUserTerminalMapping_Payment_Type%></th>
                  <th style="text-align: center;"><%=memberUserTerminalMapping_Card_Type%></th>
                  <th style="text-align: center;"><%=memberUserTerminalMapping_Min%></th>
                  <th style="text-align: center;"><%=memberUserTerminalMapping_Max%></th>
                </tr>

                <%--<tr>
                    <td style="width:5%" class="th0">
                        <input type="checkbox" onClick="ToggleAll(this);" name="" class="no-margin">
                    </td>
                    <td valign="middle" align="center"  class="th0">Terminal ID</td>
                    <td valign="middle" align="center" class="th0">Terminal Name</td>
                    &lt;%&ndash;<td valign="middle" align="center" class="th0">Member Id</td>&ndash;%&gt;
                    <td valign="middle" align="center" class="th0">Payment Type</td>
                    <td valign="middle" align="center" class="th0">Card Type</td>
                    <td valign="middle" align="center" class="th0">Min Trax. Amount</td>
                    <td valign="middle" align="center" class="th0">Max Trax. Amount</td>

                </tr>--%>

                </thead>

                <tbody class="checkclass">

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
                    String accountid = terminalVO.getAccountId();
                    float minAmount = terminalVO.getMin_transaction_amount();
                    float maxAmount = terminalVO.getMax_transaction_amount();
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


                %>



                <%

                        out.println("<tr>");
                        out.println("<td data-label=\"Check\" align=\"center\""+style+">&nbsp;<input type=\"checkbox\" class=\"no-margin\" name=\"terminal\" value=\""+terminalid+"\" "+isChecked+"></td>");
                        out.println("<td data-label=\"Terminal ID\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(terminalid)+"<input type=\"hidden\" name=\"terminalid"+terminalid+"\" value=\""+terminalid+"\"></td>");
                        out.println("<td data-label=\"Terminal Name\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(terminalName)+"<input type=\"hidden\" name=\"terminalname"+terminalid+"\" value=\""+terminalName+"\"></td>");
                        out.println("<td data-label=\"Payment Type\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(paymentType+"-"+terminalVO.getPaymentName())+"<input type=\"hidden\" name=\"paymenttypeid"+terminalid+"\" value=\""+paymentType+"\"></td>");
                        out.println("<td data-label=\"Card Type\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(cardType+"-"+terminalVO.getCardType())+"<input type=\"hidden\" name=\"cardtypeid"+terminalid+"\" value=\""+cardType+"\"></td>");
                        out.println("<td data-label=\"Min Trax. Amount\" align=\"center\""+style+">&nbsp;"+minAmount+"<input type=\"hidden\" name=\"accountid"+terminalid+"\" value=\""+accountid+"\"></td>");
                        out.println("<td data-label=\"Max Trax. Amount\" align=\"center\""+style+">&nbsp;"+maxAmount+"</td>");
                        //out.println("<td align=\"center\""+style+"><form action=\"/icici/servlet/MemberUserTerminalList?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"terminalid\" value=\""+terminalid+"\"><input type=\"hidden\" name=\"memberid\" value=\""+request.getParameter("memberid")+"\"><input type=\"hidden\" name=\"userid\" value=\""+request.getParameter("userid")+"\"><input type=\"hidden\" name=\"paymode\" value=\""+paymentType+"\"><input type=\"hidden\" name=\"cardtype\" value=\""+cardType+"\"><input type=\"submit\" name=\"submit\" value=\"Remove Terminal\" class=\"gotoauto\" ><input type=\"hidden\" name=\"action\" value=\"delete\"></form></td>");

                        out.println("</tr>");
                    }

                %>


                </tbody>


              </table>

                <div align="center">
                    <input type="hidden" value="<%=request.getParameter("memberid")%>" name="memberid">
                    <input type="hidden" value="<%=request.getParameter("userid")%>" name="userid">
                    <button type="submit" class="addnewmember btn btn-default" onClick="return DoUpdate();" >
                        <%=memberUserTerminalMapping_Update_Selected%>
                    </button>
                </div>


                <%--<script type="">
                  $(function()
                  {
                    $(".checkclass").append("<tr>" +
                    "<td style=\"text-align: center; vertical-align: middle;\" > <input  type=\"checkbox\" class=\"no-margin\" name=\"terminal\" value=\"<%=terminalid%>\" <%=isChecked%>></td>" +
                    "<td  data-label=\"Terminal ID\" style=\"text-align: center; vertical-align: middle;\" ><%=ESAPI.encoder().encodeForHTML(terminalid)%><input type=\"hidden\" name=\"terminalid <%=terminalid%>\" value=\"<%=terminalid%>\"></td>" +


                    "<td  data-label=\"Terminal Name\" style=\"text-align: center; vertical-align: middle;\" ><%=ESAPI.encoder().encodeForHTML(terminalName)%><input type=\"hidden\" name=\"terminalname <%=terminalid%>\" value=\"<%=terminalName%>\"></td>" +

                    "<td  data-label=\"Payment Type\" style=\"text-align: center; vertical-align: middle;\" ><%=ESAPI.encoder().encodeForHTML(paymentType+"-"+terminalVO.getPaymentName())%><input type=\"hidden\" name=\"paymenttypeid <%=terminalid%>\" value=\"<%=paymentType%>\"></td>" +

                    "<td  data-label=\"Card Type\" style=\"text-align: center; vertical-align: middle;\" ><%=ESAPI.encoder().encodeForHTML(cardType+"-"+terminalVO.getCardType())%><input type=\"hidden\" name=\"cardtypeid <%=terminalid%>\" value=\"<%=cardType%>\"></td>" +

                    "<td  data-label=\"Min Trax. Amount\" style=\"text-align: center; vertical-align: middle;\" ><%=minAmount%><input type=\"hidden\" name=\"accountid <%=terminalid%>\" value=\"<%=accountid%>\"></td>" +

                    "<td  data-label=\"Max Trax. Amount\" style=\"text-align: center; vertical-align: middle;\" ><%=maxAmount%></td>" +

                    "</tr>");
                  });
                </script>--%>

                <%
                  }
                  else
                  {
                      out.println(Functions.NewShowConfirmation1(memberUserTerminalMapping_Sorry,memberUserTerminalMapping_no));
                  }
                %>

                <%--<thead>
                <tr>
                  <td class="th0" colspan="14">
                    <input type="hidden" value="<%=request.getParameter("memberid")%>" name="memberid">
                    <input type="hidden" value="<%=request.getParameter("userid")%>" name="userid">
                    &lt;%&ndash;<center><button type="submit" class="addnewmember" value="Add">Add</button></center>&ndash;%&gt;
                    <button type="submit" class="addnewmember" style="margin-left:40px; ">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Update
                    </button>
                  </td>
                </tr>

                &lt;%&ndash;<td width="50%" class="textb">
                  <button type="submit" class="buttonform" style="margin-left:40px; ">
                    <i class="fa fa-clock-o"></i>
                    &nbsp;&nbsp;Search
                  </button>
                </td>&ndash;%&gt;
                </thead>--%>




<%--
                </tbody>


              </table>

              <div align="center">
                <input type="hidden" value="<%=request.getParameter("memberid")%>" name="memberid">
                <input type="hidden" value="<%=request.getParameter("userid")%>" name="userid">
                <button type="submit" class="addnewmember btn btn-default" onClick="return DoUpdate();" >
                  Update Selected
                </button>
              </div>--%>

              <%--<div id="showingid"><strong>Showing Page <%=paginationVO.getPageNo()%> of <%=paginationVO.getTotalRecords()%> records</strong></div>

              <div>
                  <jsp:include page="page.jsp" flush="true">
                      <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                      <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                      <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                      <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                      <jsp:param name="page" value="ListMerchantFraudRule"/>
                      <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                      <jsp:param name="orderby" value=""/>
                  </jsp:include>
              </div>

              <br><br>--%>


              <%--<div align="center">
                <input type="hidden" value="<%=request.getParameter("memberid")%>" name="memberid">
                <input type="hidden" value="<%=request.getParameter("userid")%>" name="userid">
                <button type="button" value="Update" class="btn btn-default" onClick="return DoUpdate();" >
                  Update Selected
                </button>
              </div>--%>

            </div>
          </div>
        </div>
      </div>

      </form>

      <%
            }
          }
          else
          {
            out.println(Functions.NewShowConfirmation1("Filter","Please provide MemberID to get TerminalList"));
          }


      %>

    </div>
  </div>
</div>


</body>
</html>