<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.dao.BankDao" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
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
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","PartnerReports");
%>
<html>
<head>
  <title><%=company%> Merchant Transactions</title>
</head>
<style type="text/css">
  @media(max-width: 991px) {
    .additional-btn {
      float: left;
      margin-left: 30px;
      margin-top: 10px;
      position: inherit!important;
    }
  }
</style>
<body>
<%
  List<String> stringList=(List)request.getAttribute("result");
  String wireId=request.getParameter("wireId");
  String partnerid=(String) session.getAttribute("merchantid");
  Functions functions=new Functions();
  String listOfPartnerId="";
  if(functions.isValueNull(partnerid)){
    listOfPartnerId=partner.getSubpartner((String) session.getAttribute("merchantid"));
  }
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String partnercommissioncron_Generate_Merchant_Wire_Report = StringUtils.isNotEmpty(rb1.getString("partnercommissioncron_Generate_Merchant_Wire_Report")) ? rb1.getString("partnercommissioncron_Generate_Merchant_Wire_Report") : "Generate Merchant Wire Report";
  String partnercommissioncron_WireId = StringUtils.isNotEmpty(rb1.getString("partnercommissioncron_WireId")) ? rb1.getString("partnercommissioncron_WireId") : "Wire Id";
  String partnercommissioncron_Select_WireId = StringUtils.isNotEmpty(rb1.getString("partnercommissioncron_Select_WireId")) ? rb1.getString("partnercommissioncron_Select_WireId") : "Select Wire Id";
  String partnercommissioncron_Next = StringUtils.isNotEmpty(rb1.getString("partnercommissioncron_Next")) ? rb1.getString("partnercommissioncron_Next") : "Next";
  String partnercommissioncron_MemberId = StringUtils.isNotEmpty(rb1.getString("partnercommissioncron_MemberId")) ? rb1.getString("partnercommissioncron_MemberId") : "Member Id";
  String partnercommissioncron_AccountId = StringUtils.isNotEmpty(rb1.getString("partnercommissioncron_AccountId")) ? rb1.getString("partnercommissioncron_AccountId") : "Account Id";
  String partnercommissioncron_TerminalId = StringUtils.isNotEmpty(rb1.getString("partnercommissioncron_TerminalId")) ? rb1.getString("partnercommissioncron_TerminalId") : "Terminal Id";
  String partnercommissioncron_Status = StringUtils.isNotEmpty(rb1.getString("partnercommissioncron_Status")) ? rb1.getString("partnercommissioncron_Status") : "Status";
  String partnercommissioncron_Description = StringUtils.isNotEmpty(rb1.getString("partnercommissioncron_Description")) ? rb1.getString("partnercommissioncron_Description") : "Description";

%>

<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/partnerWireReports.jsp?ctoken=<%=ctoken%>" method="post" name="form">
            <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/partner/images/goBack.png">
            </button>
          </form>
        </div>
      </div>

      <br><br><br>
      <div class="row">
        <div class="col-md-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnercommissioncron_Generate_Merchant_Wire_Report%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              </div>
            </div>
            <br>
            <form action="/partner/net/GeneratePartnerWireReport?ctoken=<%=ctoken%>" method="POST" id="myForm" name="f1" class="form-horizontal">
              <input  name="ctoken" type="hidden" value="<%=ctoken%>" id="ctoken">
              <div class="widget-content padding">
                  <%
                String  error= (String) request.getAttribute("statusMsg");
                if(error!=null) {
                  out.println("<center><font class=\"textb\">" + error + "</font></center>");
                }
              %>
                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=partnercommissioncron_WireId%></label>
                  <div class="col-md-4">
                    <select name="wireId" class="form-control" id="wireId" style="width: 200px;">
                      <option value=""><%=partnercommissioncron_Select_WireId%></option>
                      <%
                        BankDao bankDAO=new BankDao();
                        List<TerminalVO> accountList=bankDAO.getPartnerWireDetails(listOfPartnerId);
                        String selected="";
                        for(TerminalVO terminalVO:accountList)
                        {
                          if(terminalVO.getWireId().equals(wireId))
                            selected = "selected";
                          else
                            selected="";
                          out.println("<option value=\""+terminalVO.getWireId()+"\""+selected+">"+terminalVO.getWireId()+" - "+terminalVO.getAccountId()+" - "+terminalVO.getMemberId()+"</option>");
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-4"></div>
                </div>

                <div class="widget-content padding">
                  <div id="horizontal-form">
                    <div class="form-group col-md-12 has-feedback">
                      <center>
                        <label >&nbsp;</label>
                        <button type="submit" class="btn btn-default" style="display: -webkit-box;">
                          <input type="hidden" name="action" value="next">
                          <i class="fa fa-save"></i>&nbsp;&nbsp;<%=partnercommissioncron_Next%>
                        </button>
                      </center>
                    </div>
                  </div>
                </div>
            </form>
          </div>
          <%
            if(stringList!=null){
          %>
          <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
            <thead>
            <tr height="50px" align="center">
              <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnercommissioncron_WireId%></b></td>
              <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnercommissioncron_MemberId%></b></td>
              <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnercommissioncron_AccountId%></b></td>
              <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnercommissioncron_TerminalId%></b></td>
              <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnercommissioncron_Status%></b></td>
              <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnercommissioncron_Description%></b></td>
            </tr>
            </thead>
            <%
              for(String s:stringList)
              {
                String responseArr[]=s.split(":");
            %>
            <tr>
              <td align="center" class="tr0"><%=responseArr[0]%></td>
              <td align="center" class="tr1"><%=responseArr[1]%></td>
              <td align="center" class="tr0"><%=responseArr[2]%></td>
              <td align="center" class="tr1"><%=responseArr[3]%></td>
              <td align="center" class="tr1"><%=responseArr[4]%></td>
              <td align="center" class="tr1"><%=responseArr[5]%></td>
            </tr>
            <%
              }
            %>
          </table>
          <%
            }
          %>
        </div>
      </div>
    </div>
  </div>
  </div>
</body>
</html>