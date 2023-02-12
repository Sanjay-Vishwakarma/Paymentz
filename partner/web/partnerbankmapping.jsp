<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="com.manager.vo.ActionVO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Namrata Bari.
  Date: 23/10/2019
  Time: 15:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>

<html>
<head>
  <title>Partner Bank Mapping</title>

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

</head>
<body>

<%!
  Logger logger =new Logger("test.jsp");
  private ApplicationManager applicationManager = new ApplicationManager();
  private Functions functions = new Functions();
%>
<%
  logger.debug("Inside MerchantMappingBank");
  session.setAttribute("submit","merchantmappingbank");
  ActionVO actionVO=null;
  java.util.TreeMap<String,String> partneriddetails =null;
  partneriddetails=partner.getPartnerDetailsForMap(String.valueOf(session.getAttribute("merchantid")));
  ApplicationManager applicationManager = new ApplicationManager();
  List<String> bankDetailList = applicationManager.getListOfBankId();
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String partnerbankmapping_Partner_Bank_Mapping = StringUtils.isNotEmpty(rb1.getString("partnerbankmapping_Partner_Bank_Mapping")) ? rb1.getString("partnerbankmapping_Partner_Bank_Mapping") : "Partner Bank Mapping";
  String partnerbankmapping_Partner_ID = StringUtils.isNotEmpty(rb1.getString("partnerbankmapping_Partner_ID")) ? rb1.getString("partnerbankmapping_Partner_ID") : "Partner ID*";
  String partnerbankmappin_Select_PartnerId = StringUtils.isNotEmpty(rb1.getString("partnerbankmappin_Select_PartnerId")) ? rb1.getString("partnerbankmappin_Select_PartnerId") : "Select Partner Id";
  String partnerbankmapping_BankID = StringUtils.isNotEmpty(rb1.getString("partnerbankmapping_BankID")) ? rb1.getString("partnerbankmapping_BankID") : "Bank ID*";
  String partnerbankmapping_Select_BankId = StringUtils.isNotEmpty(rb1.getString("partnerbankmapping_Select_BankId")) ? rb1.getString("partnerbankmapping_Select_BankId") : "Select Bank Id";
  String partnerbankmapping_Save = StringUtils.isNotEmpty(rb1.getString("partnerbankmapping_Save")) ? rb1.getString("partnerbankmapping_Save") : "Save";
%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/merchantmappingbank.jsp?ctoken=<%=ctoken%>" method="post" name="form">
            <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/partner/images/goBack.png">
            </button>
          </form>
        </div>
      </div>
      <br>
      <br>
      <br>
        <div class="row">
          <div class="col-sm-12 portlets ui-sortable">
            <div class="widget">

              <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerbankmapping_Partner_Bank_Mapping%></strong></h2>
              </div>
              <div class="additional-btn">
                <%--<form action="/partner/net/SingleMerchantBankMapping?ctoken=<%=ctoken%>" method="post" name="forms">
                  <button type="submit" value="1_Update" name="action" class="btn btn-default" style="width:250px; font-size:14px">
                    <i class="fa fa-cog"></i>
                    &nbsp;&nbsp;Default Gateway Setting
                  </button>
                </form>--%>
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
              <form action="/partner/net/PartnerBankMapping?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
                <%
                  String errormsg = (String) request.getAttribute("error");
                  String msg = (String) request.getAttribute("msg");
                  if (functions.isValueNull((String)request.getAttribute("error")))
                  {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg +"</h5>");
                  }
                  if(request.getAttribute("msg")!=null)
                  {
                    out.println(Functions.NewShowConfirmation1("Successfull",msg));
                    out.println("<br>");
                  }
                %>

                <div class="widget-content padding">
                  <div id="horizontal-form">
                    <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                    <div class="form-group col-md-1">
                    </div>
                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label><%=partnerbankmapping_Partner_ID%></label>
                        </div>
                        <div class="col-md-3" >
                          <select class="form-control" name="partnerid" id="partnerid">
                            <option value="" default><%=partnerbankmappin_Select_PartnerId%></option>
                            <%
                              for(String pid : partneriddetails.keySet())
                              {
                            %>
                            <option value="<%=pid%>" ><%=partneriddetails.get(pid)%></option>
                            <%
                              }
                            %>
                          </select>
                        </div>
                      </div>
                    </div>

                    <div class="form-group col-md-12">
                      <div class="ui-widget">
                        <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                          <label><%=partnerbankmapping_BankID%></label>
                        </div>
                        <div class="col-md-3" >
                          <select class="form-control" name="bankId" id="bankId">
                            <option value="" default><%=partnerbankmapping_Select_BankId%></option>
                            <%
                              for(String bankDetails : bankDetailList)
                              {
                                if(bankDetails != null)
                                {
                            %>
                            <option value="<%=bankDetails%>"> <%=bankDetails%></option>
                            <%
                                }
                              }
                            %>
                          </select>
                        </div>
                      </div>
                    </div>


                    <div class="form-group col-md-12 text-right center-block">
                        <button type="submit" class="btn btn-default center-block"><i class="fa fa-Save"></i>
                          &nbsp;&nbsp;<%=partnerbankmapping_Save%></button>
                    </div>


                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
<%

%>
</body>
</html>
