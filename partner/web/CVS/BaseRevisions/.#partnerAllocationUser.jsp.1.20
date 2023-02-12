<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.PartnerModuleManager" %>
<%@ page import="com.manager.vo.PartnerSubModuleVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 3/9/15
  Time: 9:58 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
  session.setAttribute("submit", "partnerChildList");
  Functions functions = new Functions();
  String style="class=td1";

  String userId=request.getParameter("userid");
  String isRefund= (String) session.getAttribute("isRefund");
  String error = (String) request.getAttribute("error");
  String message = (String) request.getAttribute("message");
  String successMessage = (String) request.getAttribute("success");
  String login = request.getParameter("login");
  String partnerid = request.getParameter("partnerid");
  PartnerModuleManager partnerModuleManager=new PartnerModuleManager();
  String superadminid = partnerModuleManager.getSuperAdminId(partnerid);

  HashMap<String,List> hash= null;
  /*if(partnerModuleManager.isPartneridExistforModules(partnerid)){
    hash = partnerModuleManager.partnerUserModulesList(partnerid);
  }
  else{
    hash = partnerModuleManager.getPartnerModulesList();
  }*/
   PartnerFunctions partnerFunctions = new PartnerFunctions();
   String Role1 = partnerFunctions.getRole(user.getAccountName());
  if(Role1.equals("subpartner") || Role1.equals("childsuperpartner")){
    hash = partnerModuleManager.partnerUserAssignModulesList(String.valueOf(userId));
  }
  else
  {
    if (partnerModuleManager.isPartneridExistforModules(partnerid))
    {
      hash = partnerModuleManager.partnerUserModulesList(partnerid);
    }
    else if (superadminid.equals("1") || superadminid.equals("0"))
    {
      hash = partnerModuleManager.getPartnerModulesList();
    }
    else if (partnerModuleManager.isPartneridExistforModules(superadminid))
    {
      hash = partnerModuleManager.partnerUserModulesList(superadminid);
    }
    else
    {
      hash = partnerModuleManager.getPartnerModulesList();
    }
  }
  Set<String> moduleSet1 = partnerModuleManager.getPartnerAccessModuleSet(String.valueOf(userId));
  List<PartnerSubModuleVO> list=null;
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String partnerAllocationUser_Partner_Module_Management = StringUtils.isNotEmpty(rb1.getString("partnerAllocationUser_Partner_Module_Management")) ? rb1.getString("partnerAllocationUser_Partner_Module_Management") : "Partner Module Management";
  String partnerAllocationUser_Partner_User = StringUtils.isNotEmpty(rb1.getString("partnerAllocationUser_Partner_User")) ? rb1.getString("partnerAllocationUser_Partner_User") : "Partner User";
  String partnerAllocationUser_Module_Name = StringUtils.isNotEmpty(rb1.getString("partnerAllocationUser_Module_Name")) ? rb1.getString("partnerAllocationUser_Module_Name") : "Module Name";
  String partnerAllocationUser_Save = StringUtils.isNotEmpty(rb1.getString("partnerAllocationUser_Save")) ? rb1.getString("partnerAllocationUser_Save") : "Save";

%>
<style type="text/css">

  input[type=radio], input[type=checkbox]{
    transform: scale(2);
    -ms-transform: scale(2);
    -webkit-transform: scale(2);
    padding: 10px;
  }
</style>
<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
<script src="/partner/NewCss/js/jquery-ui.min.js"></script>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title><%=company%> | Partner Module Management</title>
  <script>
    function ToggleAll(checkbox)
  {
  console.log("yes");
  if (checkbox.checked)
  {
  // Iterate each checkbox
  $(':checkbox').each(function ()
  {
  this.checked = true;
  });
  }
  else
  {
  $(':checkbox').each(function ()
  {
  this.checked = false;
  });
  }
  }
  </script>
</head>
<body>
<%!
  Logger logger=new Logger("partnerAllocationUser");
%>
<%
  session.setAttribute("submit","partnerChildList");
%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/net/PartnerUserList?ctoken=<%=ctoken%>" method="post" name="form">
            <%
              Enumeration<String> stringEnumeration=request.getParameterNames();
              while(stringEnumeration.hasMoreElements())
              {
                String name=stringEnumeration.nextElement();
                if("accountid".equals(name))
                {
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
      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerAllocationUser_Partner_Module_Management%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <%
              if(functions.isValueNull(error)){
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+error+"</h5>");
              }
              if(functions.isValueNull(message)){
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+message+"</h5>");
              }
              if(functions.isValueNull(successMessage)){
                out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+successMessage+"</h5>");
              }
            %>
            <div class="widget-content padding" style="overflow-x: auto;">
              <form  action="/partner/net/PartnerAllocationUser?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <input type="hidden" value="<%=userId%>" name="userid">
                <input type="hidden" value="<%=login%>" name="login">
                <input type="hidden" value="<%=partnerid%>" name="partnerid">
                <div class="widget-content padding">
                  <div class="form-group">
                    <%--<div class="col-md-2"></div>--%>
                    <label class="col-md-4 control-label"><%=partnerAllocationUser_Partner_User%></label>
                    <div class="col-md-4">
                      <input type="text" class="form-control" name="login" value="<%=ESAPI.encoder().encodeForHTML(login)%>"disabled>
                    </div>
                    <div class="col-md-6"></div>
                  </div>
                  <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                      <div class="widget">
                        <%
                          Set<String> keys=hash.keySet();
                          if(keys.size()==0)
                          {
                            out.println("<BR><BR><BR>");
                            out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
                          }else{
                          %>
                        <div class="widget-header transparent">
                        </div>
                        <div class="widget-content padding">
                          <table align=left width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                            <thead>
                            <tr>
                              <td  valign="left" align="left" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" ><b><%=partnerAllocationUser_Module_Name%></b>&nbsp;&nbsp;
                                <input type="checkbox" name="select-all" id="select-all"
                                       onclick="ToggleAll(this)"/>
                              </td>
                            </tr>
                            </thead>
                          </table>
                          <%
                            PartnerFunctions partnerfunction = new PartnerFunctions();
                            String Role = partnerfunction.getRoleofPartner(partnerid);
                            for (String key1:keys)
                            {
                              String modules[]=key1.split("-");
                              String moduleId=modules[0];
                              String moduleName=modules[1];
                              list=hash.get(moduleId+"-"+moduleName);
                              String checked="";
                              if(moduleSet1.contains(moduleName))
                              {
                                checked="checked";
                              }
                              if(PartnerModuleEnum.AGENT_MANAGEMENT.toString().equals(moduleName))
                              {
                                if (!Role.contains("superpartner"))
                                {
                                  continue;
                                }
                              }
                          %>
                          <table align=left width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                            <tr>
                              <td align="left" style="width: 15%"></td>
                              <td align="left">
                                <input type="checkbox" class="no-margin" name="moduleid" <%=checked%> style=" width: 10px;float: left;" value=<%=moduleId%>>&nbsp;&nbsp;<%=moduleName.replace("_"," ")%><br>
                              </td>
                            </tr>
                            <%
                              if(list!=null && list.size()>0)
                              {
                            %>
                            <tr>
                              <table align=right width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <%
                                  for(PartnerSubModuleVO partnerSubModuleVO:list)
                                  {
                                    String check1="";
                                    String disabled="";
                                    if ("N".equals(isRefund) && PartnerModuleEnum.MERCHANT_REFUND.toString().equals(partnerSubModuleVO.getSubModuleName())){
                                      continue;
                                    }
                                    if (moduleSet1.contains(partnerSubModuleVO.getSubModuleName())){
                                      check1="checked";
                                    }
                                %>
                                <tr>
                                  <td align="left" style="width:30%;">
                                  </td>
                                  <td align="left" <%=style%>>
                                    <%
                                      /*if(PartnerModuleEnum.GENERATE_SECRET_KEY.toString().equals(partnerSubModuleVO.getSubModuleName())){
                                        *//*disabled="disabled";*//*
                                        continue;
                                      }*/
                                        if(PartnerModuleEnum.PARTNER_MASTER.toString().equals(partnerSubModuleVO.getSubModuleName())){
                                          if(!Role.contains("superpartner")){
                                            continue;
                                          }
                                          /*else{
                                            disabled="disabled";
                                          }*/
                                      }
                                     /* if(PartnerModuleEnum.MERCHANT_UNBLOCKED_ACCOUNT.toString().equals(partnerSubModuleVO.getSubModuleName())){
                                        disabled="disabled";
                                      }
                                      if(PartnerModuleEnum.MERCHANT_USER_UNBLOCKED_ACCOUNT.toString().equals(partnerSubModuleVO.getSubModuleName())){
                                        disabled="disabled";
                                      }*/
                                    %>
                                    <input type="checkbox" <%=disabled%>  style=" width: 10px;float: left;" <%=check1%> name="submoduleid" value=<%=partnerSubModuleVO.getSubModuleId()%>>&nbsp;&nbsp;<%=partnerSubModuleVO.getSubModuleName().replace("_", " ")%></td>
                                </tr>
                                <%
                                  }
                                %>
                              </table>
                            </tr>
                            <%
                                }
                              }
                            %>
                          </table>
                          <div class="form-group col-md-5"></div>
                          <div class="form-group col-md-4">
                            <button type="submit" class="btn btn-default" value="Save">
                              <i class="fa fa-sign-in"></i>
                              &nbsp;&nbsp;<%=partnerAllocationUser_Save%>
                            </button>
                            <%
                              }
                            %>
                          </div>
                        </div>
                      </div>
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
</div>
</body>
</html>