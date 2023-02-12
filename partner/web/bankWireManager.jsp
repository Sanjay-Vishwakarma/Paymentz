<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.vo.BankWireManagerVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Mahima
  Date: 31-01-2020
  Time: 15:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp" %>
<html>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  String partnerid= String.valueOf(session.getAttribute("partnerId"));
%>
<head>
  <title><%=company%> Bank Wire  </title>
  <script type="text/javascript" src="/partner/javascript/autocomplete.js"></script>
</head>
<body>

<%
  if (partner.isLoggedInPartner(session)) {
    Functions functions = new Functions();
    session.setAttribute("submit","bankWire");
    String str="";

    String bankWireId=functions.isValueNull(request.getParameter("bankwiremangerid"))?request.getParameter("bankwiremangerid"):"";
    String accountId=functions.isValueNull(request.getParameter("accountid"))?request.getParameter("accountid"):"";

    int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), 10);
    str = str + "&ctoken=" + ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    str = str + "&SRecords=" + pagerecords;
    str = str + "&accountid=" + accountId;
    str = str + "&bankwiremangerid=" + bankWireId;
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String bankWireManager_Bank_Wire_Manager = StringUtils.isNotEmpty(rb1.getString("bankWireManager_Bank_Wire_Manager")) ? rb1.getString("bankWireManager_Bank_Wire_Manager") : "Bank Wire Manager";
    String bankWireManager_Bank_Wire_ID = StringUtils.isNotEmpty(rb1.getString("bankWireManager_Bank_Wire_ID")) ? rb1.getString("bankWireManager_Bank_Wire_ID") : "Bank Wire ID";
    String bankWireManager_Account_Id = StringUtils.isNotEmpty(rb1.getString("bankWireManager_Account_Id")) ? rb1.getString("bankWireManager_Account_Id") : "Account Id";
    String bankWireManager_Search = StringUtils.isNotEmpty(rb1.getString("bankWireManager_Search")) ? rb1.getString("bankWireManager_Search") : "Search";
    String bankWireManager_Report_Table = StringUtils.isNotEmpty(rb1.getString("bankWireManager_Report_Table")) ? rb1.getString("bankWireManager_Report_Table") : "Report Table";
    String bankWireManager_SrNo = StringUtils.isNotEmpty(rb1.getString("bankWireManager_SrNo")) ? rb1.getString("bankWireManager_SrNo") : "Sr No.";
    String bankWireManager_Settled_Date = StringUtils.isNotEmpty(rb1.getString("bankWireManager_Settled_Date")) ? rb1.getString("bankWireManager_Settled_Date") : "Settled Date";
    String bankWireManager_AccountId = StringUtils.isNotEmpty(rb1.getString("bankWireManager_AccountId")) ? rb1.getString("bankWireManager_AccountId") : "AccountId";
    String bankWireManager_Bank = StringUtils.isNotEmpty(rb1.getString("bankWireManager_Bank")) ? rb1.getString("bankWireManager_Bank") : "Bank";
    String bankWireManager_Start = StringUtils.isNotEmpty(rb1.getString("bankWireManager_Start")) ? rb1.getString("bankWireManager_Start") : "Start";
    String bankWireManager_Date = StringUtils.isNotEmpty(rb1.getString("bankWireManager_Date")) ? rb1.getString("bankWireManager_Date") : "Date";
    String bankWireManager_End = StringUtils.isNotEmpty(rb1.getString("bankWireManager_End")) ? rb1.getString("bankWireManager_End") : "End";
    String bankWireManager_Action = StringUtils.isNotEmpty(rb1.getString("bankWireManager_Action")) ? rb1.getString("bankWireManager_Action") : "Action";
    String bankWireManager_Showing_Page = StringUtils.isNotEmpty(rb1.getString("bankWireManager_Showing_Page")) ? rb1.getString("bankWireManager_Showing_Page") : "Showing Page";
    String bankWireManager_of = StringUtils.isNotEmpty(rb1.getString("bankWireManager_of")) ? rb1.getString("bankWireManager_of") : "of";
    String bankWireManager_records = StringUtils.isNotEmpty(rb1.getString("bankWireManager_records")) ? rb1.getString("bankWireManager_records") : "records";
    String bankWireManager_Sorry = StringUtils.isNotEmpty(rb1.getString("bankWireManager_Sorry")) ? rb1.getString("bankWireManager_Sorry") : "Sorry";
    String bankWireManager_no = StringUtils.isNotEmpty(rb1.getString("bankWireManager_no")) ? rb1.getString("bankWireManager_no") : "No records found.";
    String bankWireManager_View = StringUtils.isNotEmpty(rb1.getString("bankWireManager_View")) ? rb1.getString("bankWireManager_View") : "View";
%>

<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <%--<div class="pull-right">
        <div class="btn-group">
          <form action="/partner/addNewBankWire.jsp?ctoken=<%=ctoken%>" method="POST">
            <button class="btn btn-default" type="submit" value="bankWire">
              <input type="hidden" name="name" value="Add">
              Add New BankWire
            </button>
          </form>
        </div>
      </div>
      <br>
      <br>--%>

      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp; <%=bankWireManager_Bank_Wire_Manager%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              </div>
            </div>

            <div class="widget-content padding">
              <div id="horizontal-form">
                <form action="/partner/net/BankWireManager?ctoken=<%=ctoken%>" method="post" name="forms">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <%
                    if (request.getAttribute("Error") != null) {
                      String message = (String) request.getAttribute("Error");
                      if (message != null)
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                    }
                    ValidationErrorList error= (ValidationErrorList) request.getAttribute("error");
                    if(error!=null){
                      for(Object errorList : error.errors())
                      {
                        ValidationException ve = (ValidationException) errorList;
                        out.println("<center><font class=\"textb\">" + ve.getMessage() + "</font></center>");
                      }
                    }
                  %>

                  <div class="form-group col-md-4 has-feedback">
                    <label ><%=bankWireManager_Bank_Wire_ID%></label>
                    <input  type="text" name="bankwiremangerid" maxlength="10" class="form-control" value="<%= bankWireId%>">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label ><%=bankWireManager_Account_Id%></label>
                    <input  type="text" name="accountid" maxlength="10" class="form-control" value="<%= accountId%>">
                  </div>

                  <div class="form-group col-md-6">
                    <label style="color: transparent;">&nbsp;</label>
                    <button type="submit" class="btn btn-default" style="display:block;">
                      <i class="far fa-clock"></i>
                      &nbsp;<%=bankWireManager_Search%>
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Report table code -->
      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=bankWireManager_Report_Table%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-y: auto;">
              <%
                List<BankWireManagerVO> bankWireManagerVOList = (List<BankWireManagerVO>) request.getAttribute("BankWireManagerVOList");
                if(bankWireManagerVOList !=null)
                {
                  if(bankWireManagerVOList.size()>0)
                  {
                    PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
                    paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);

                    int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
              %>
              <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr height="50px" align="center">
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=bankWireManager_SrNo%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=bankWireManager_Bank_Wire_ID%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=bankWireManager_Settled_Date%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=bankWireManager_AccountId%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=bankWireManager_Bank%>&nbsp;<%=bankWireManager_Start%>&nbsp;<%=bankWireManager_Date%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=bankWireManager_Bank%>&nbsp;<%=bankWireManager_End%>&nbsp;<%=bankWireManager_Date%></b></td>
                  <td  colspan="2" valign="middle" align="center" style="background-color: #7eccad ;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=bankWireManager_Action%></b></td>
                </tr>
                </thead>
                <%
                  String style = "class=td1";
                  String ext = "light";
                  int pos = 1;
                  for(BankWireManagerVO bankWireManagerVO:bankWireManagerVOList)
                  {
                    String id=Integer.toString(pos);
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

                    out.println("<tr id=\"maindata\">");
                    out.println("<td valign=\"middle\" data-label=\"Sr No\" align=\"center\" "+style+">&nbsp;"+srno+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Bank Wire Id\" align=\"center\""+style+">&nbsp;"+bankWireManagerVO.getBankwiremanagerId()+"</td>");
                    out.println("<td valign=\"middle\" data-label=\"Settled Date\" align=\"center\""+style+">&nbsp;"+bankWireManagerVO.getSettleddate()+"</td>");
                    out.println("<td valign=\"middle\" data-label=\"Account Id\" align=\"center\""+style+">&nbsp;"+bankWireManagerVO.getAccountId()+"</td>");
                    out.println("<td valign=\"middle\" data-label=\"Bank Start Date\" align=\"center\""+style+">&nbsp;"+bankWireManagerVO.getBank_start_date()+"</td>");
                    out.println("<td valign=\"middle\" data-label=\"Bank End Date \" align=\"center\""+style+">&nbsp;"+bankWireManagerVO.getBank_end_date()+"</td>");
                    out.print("<td valign=\"middle\" style=\"padding: 10px 0px;\" data-label=\"Action\" align=\"center\""+style+"><form action=\"/partner/net/ViewOrEditBankWireManager?ctoken="+ctoken+"\" method=\"POST\" style=\"margin: 0;\"><input class=\"gotoauto btn btn-default\" type=\"submit\" value="+bankWireManager_View+">" +
                            "<input type=\"hidden\" name=\"action\" value=\""+bankWireManagerVO.getBankwiremanagerId()+"_View"+"\"><input type=\"hidden\" name=\"accountid\" value=\""+bankWireManagerVO.getAccountId()+"\"></form></td>");
                    /*out.print("<td valign=\"middle\" style=\"padding: 10px 0px;\" data-label=\"Action\" align=\"center\""+style+"><form action=\"/partner/net/ViewOrEditBankWireManager?ctoken="+ctoken+"\" method=\"POST\" style=\"margin: 0;\"><input class=\"gotoauto btn btn-default\" type=\"submit\" value=\"Edit\">" +
                            "<input type=\"hidden\" name=\"action\" value=\""+bankWireManagerVO.getBankwiremanagerId()+"_Edit"+"\"><input type=\"hidden\" name=\"accountid\" value=\""+bankWireManagerVO.getAccountId()+"\"></form></td>");
*/                    out.println("</tr>");
                    srno++;
                  }
                %>
              </table>
            </div>
          </div>
        </div>
      </div>
      <div id="showingid"><strong><%=bankWireManager_Showing_Page%> <%=paginationVO.getPageNo()%> <%=bankWireManager_of%> <%=paginationVO.getTotalRecords()%> <%=bankWireManager_records%></strong></div>

      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
        <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
        <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
        <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
        <jsp:param name="page" value="BankWireManager"/>
        <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
      <%
          }else {
            out.println(Functions.NewShowConfirmation1(bankWireManager_Sorry, bankWireManager_no));
          }
        } else {
          out.println(Functions.NewShowConfirmation1(bankWireManager_Sorry, bankWireManager_no));
        }
      %>
      <%
        } else {
          response.sendRedirect("/partner/logout.jsp");
          return;
        }
      %>
    </div>
  </div>
</div>
</body>
</html>