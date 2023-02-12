<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: NAMRATA B. BARI
  Date: 20/01/2020
  Time: 16:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<html>
<head>
  <title>Add New System Account</title>
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
  <script>
    $(document).ready(function(){
      var w = $(window).width();

      //alert(w);

      if(w > 990){
        //alert("It's greater than 990px");
        $("body").removeClass("smallscreen").addClass("widescreen");
        $("#wrapper").removeClass("enlarged");
      }
      else{
        //alert("It's less than 990px");
        $("body").removeClass("widescreen").addClass("smallscreen");
        $("#wrapper").addClass("enlarged");
        $(".left ul").removeAttr("style");
      }
    });

  </script>
</head>
<body>

<%!
  Logger logger =new Logger("test.jsp");
  private ApplicationManager applicationManager = new ApplicationManager();
  private Functions functions = new Functions();
%>
<%
  if (partner.isLoggedInPartner(session))
  {
    logger.debug("Fraud System Account Master");
    session.setAttribute("submit","Fraud System Account Master");
    String fsid=Functions.checkStringNull(request.getParameter("fsid")) == null ? "" : request.getParameter("fsid");
    String accountName = Functions.checkStringNull(request.getParameter("accountName")) == null ? "" : request.getParameter("accountName");
    String userName = Functions.checkStringNull(request.getParameter("userName")) == null ? "" : request.getParameter("userName");
    String contactname = Functions.checkStringNull(request.getParameter("contactname")) == null ? "" : request.getParameter("contactname");
    String contactemail = Functions.checkStringNull(request.getParameter("contactemail")) == null ? "" : request.getParameter("contactemail");
    String isTest = Functions.checkStringNull(request.getParameter("isTest")) == null ? "" : request.getParameter("isTest");
    String pwd = Functions.checkStringNull(request.getParameter("pwd")) == null ? "" : request.getParameter("pwd");
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String manageFraudSystemAccount_Add_New_System = StringUtils.isNotEmpty(rb1.getString("manageFraudSystemAccount_Add_New_System")) ? rb1.getString("manageFraudSystemAccount_Add_New_System") : "Add New System Account";
    String manageFraudSystemAccount_Fraud_System = StringUtils.isNotEmpty(rb1.getString("manageFraudSystemAccount_Fraud_System")) ? rb1.getString("manageFraudSystemAccount_Fraud_System") : "Fraud System*";
    String manageFraudSystemAccount_merchantid = StringUtils.isNotEmpty(rb1.getString("manageFraudSystemAccount_merchantid")) ? rb1.getString("manageFraudSystemAccount_merchantid") : "Fraud System Merchant Id/Account Name*";
    String manageFraudSystemAccount_User_Name = StringUtils.isNotEmpty(rb1.getString("manageFraudSystemAccount_User_Name")) ? rb1.getString("manageFraudSystemAccount_User_Name") : "User Name";
    String manageFraudSystemAccount_Password = StringUtils.isNotEmpty(rb1.getString("manageFraudSystemAccount_Password")) ? rb1.getString("manageFraudSystemAccount_Password") : "Password";
    String manageFraudSystemAccount_Person_name = StringUtils.isNotEmpty(rb1.getString("manageFraudSystemAccount_Person_name")) ? rb1.getString("manageFraudSystemAccount_Person_name") : "Contact Person Name*";
    String manageFraudSystemAccount_mail = StringUtils.isNotEmpty(rb1.getString("manageFraudSystemAccount_mail")) ? rb1.getString("manageFraudSystemAccount_mail") : "Contact Email*";
    String manageFraudSystemAccount_isTest = StringUtils.isNotEmpty(rb1.getString("manageFraudSystemAccount_isTest")) ? rb1.getString("manageFraudSystemAccount_isTest") : "isTest";
    String manageFraudSystemAccount_N = StringUtils.isNotEmpty(rb1.getString("manageFraudSystemAccount_N")) ? rb1.getString("manageFraudSystemAccount_N") : "N";
    String manageFraudSystemAccount_Y = StringUtils.isNotEmpty(rb1.getString("manageFraudSystemAccount_Y")) ? rb1.getString("manageFraudSystemAccount_Y") : "Y";
    String manageFraudSystemAccount_Save = StringUtils.isNotEmpty(rb1.getString("manageFraudSystemAccount_Save")) ? rb1.getString("manageFraudSystemAccount_Save") : "Save";
    String manageFraudSystemAccount_Allocate_Account = StringUtils.isNotEmpty(rb1.getString("manageFraudSystemAccount_Allocate_Account")) ? rb1.getString("manageFraudSystemAccount_Allocate_Account") : "Allocate Account";
    String manageFraudSystemAccount_Account_List = StringUtils.isNotEmpty(rb1.getString("manageFraudSystemAccount_Account_List")) ? rb1.getString("manageFraudSystemAccount_Account_List") : "Account List";
    String manageFraudSystemAccount_Allocated_Account = StringUtils.isNotEmpty(rb1.getString("manageFraudSystemAccount_Allocated_Account")) ? rb1.getString("manageFraudSystemAccount_Allocated_Account") : " Allocated Account List";

%>

<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/managePartnerFraudSystemAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button  class="btn-xs" type="submit" name="submit" name="B1" value="Fraud System Account Master"
                     style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 8px 21px;font-size: 10px;cursor: pointer;">
              <%=manageFraudSystemAccount_Allocate_Account%> &nbsp; <i class ="fa fa-sign-in" style="font-size: small; color: white;"></i>
            </button>
          </form>
        </div>
      </div>

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/fraudSystemAccountList.jsp?ctoken=<%=ctoken%>" method="POST">
            <button  class="btn-xs" type="submit" name="submit" name="B1" value="Fraud System Account Master"
                     style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 8px 21px;font-size: 10px;cursor: pointer;">
              <%=manageFraudSystemAccount_Account_List%> &nbsp; <i class ="fa fa-sign-in" style="font-size: small; color: white;"></i>
            </button>
          </form>
        </div>
      </div>

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/managePartnerAllocatedAccountList.jsp?ctoken=<%=ctoken%>" method="POST">
            <button  class="btn-xs" type="submit" name="submit" name="B1" value="Fraud System Account Master"
                     style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 8px 21px;font-size: 10px;cursor: pointer;">
             <%=manageFraudSystemAccount_Allocated_Account%> &nbsp; <i class ="fa fa-sign-in" style="font-size: small; color: white;"></i>
            </button>
          </form>
        </div>
      </div>

      <br><br><br><br><br>
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=manageFraudSystemAccount_Add_New_System%></strong></h2>
            </div>
            <div class="additional-btn">
              <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
              <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
            </div>

                <form action="/partner/net/ManageFraudSystemAccount?ctoken=<%=ctoken%>" method="post" name="form1" id="form1" class="form-horizontal">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <%
                    String message = (String)request.getAttribute("statusMsg");
                    String error = (String)request.getAttribute("error");
                    Functions functions=new Functions();
                    if(functions.isValueNull(message)){
                      out.println(Functions.NewShowConfirmation1("Result",message));
                    }
                    else if(functions.isValueNull(error)){
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");

                    }
                  %>
                  <div class="widget-content padding">
                    <div class="form-group">
                      <label  class="col-md-4 control-label"><%=manageFraudSystemAccount_Fraud_System%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <select class="form-control" name="fsid" >
                          <option value="Select Fraud System" selected>Select Fraud System</option>
                          <%

                            Hashtable<String,String> fraudSystem = FraudSystemService.getFraudSystem();
                            Iterator it1 = fraudSystem.entrySet().iterator();
                            while (it1.hasNext())
                            {
                              Map.Entry pair = (Map.Entry)it1.next();
                              if(fsid.equals(pair.getKey()))
                              {
                                out.println("<option value=\"" + pair.getKey() + "\" selected>" + pair.getKey() + " - " + pair.getValue() + "</option>");
                              }else
                              {
                                out.println("<option value=\"" + pair.getKey() + "\">" + pair.getKey() + " - " + pair.getValue() + "</option>");
                              }
                            }
                          %>
                        </select>
                        </div>
                      <div class="col-md-6"></div>
                    </div>
                    <div class="form-group">
                      <label class="col-md-4 control-label"><%=manageFraudSystemAccount_merchantid%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <input name="accountName"  style="border: 1px solid #b2b2b2;" value="<%=accountName%>" class="form-control" />
                      </div>
                      <div class="col-md-6"></div>
                    </div>
                    <div class="form-group">
                      <label  class="col-md-4 control-label"><%=manageFraudSystemAccount_User_Name%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <input name="userName" style="border: 1px solid #b2b2b2;" value="<%=userName%>" class="form-control"/>
                      </div>
                      <div class="col-md-6"></div>
                    </div>


                    <div class="form-group">
                      <label class="col-md-4 control-label"><%=manageFraudSystemAccount_Password%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <input name="pwd" style="border: 1px solid #b2b2b2;" value="<%=pwd%>" class="form-control" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');"/>
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <label  class="col-md-4 control-label"><%=manageFraudSystemAccount_Person_name%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <input name="contactname"  style="border: 1px solid #b2b2b2;" value="<%=contactname%>" class="form-control" />
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <label  class="col-md-4 control-label"><%=manageFraudSystemAccount_mail%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <input name="contactemail"  style="border: 1px solid #b2b2b2;" value="<%=contactemail%>" class="form-control" />
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <label  class="col-md-4 control-label"><%=manageFraudSystemAccount_isTest%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <select name="isTest" class="form-control">
                          <%
                            if(isTest.equals("N")){
                          %>
                          <option value="N"> <%=manageFraudSystemAccount_N%></option>
                          <option value="Y"> <%=manageFraudSystemAccount_Y%></option>
                          <%
                            }
                            else
                            {
                          %>
                          <option value="Y"> <%=manageFraudSystemAccount_Y%></option>
                          <option value="N"> <%=manageFraudSystemAccount_N%></option>
                          <%
                            }
                          %>
                        </select>
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group col-md-12 has-feedback">
                      <center>
                        <label >&nbsp;</label>
                        <input type="hidden" value="1" name="step">
                        <button type="submit" class="btn btn-default" name="action" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=manageFraudSystemAccount_Save%></button>
                      </center>
                    </div>
                  </div>
                </form>
                <div>
                </div>
              </div>
            </div>

            <%

            %>
          </div>
        </div>
      </div>
        <%
  }
  else
  {
  response.sendRedirect("/partner/logout.jsp");
  return;
  }
%>
</body>
</html>