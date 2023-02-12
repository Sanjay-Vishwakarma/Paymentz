<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.PartnerModuleManager" %>
<%@ page import="com.manager.enums.PartnerModuleEnum" %>
<%@ page import="com.manager.vo.PartnerSubModuleVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ include file="index.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: sanjeet
  Date: 4/9/19
  Time: 02:42 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    session.setAttribute("submit", "partnerUserModuleAllocation");
    String style = "class=td1";
    String userId = request.getParameter("userid");
    String partnerId = request.getParameter("partnerid");
    String error = (String) request.getAttribute("error");
    String message = (String) request.getAttribute("message");
    String successMessage = (String) request.getAttribute("success");
    String isRefund = (String) session.getAttribute("isRefund");
    PartnerModuleManager partnerModuleManager = new PartnerModuleManager();
    String superadminid = partnerModuleManager.getSuperAdminId(partnerId);
    HashMap<String,List> hash = null;
    /*if(partnerModuleManager.isPartneridExistforModules(partnerId)){
        hash = partnerModuleManager.partnerUserModulesList(partnerId);
    }
    else{
        hash = partnerModuleManager.getPartnerModulesList();
    }*/

        if(partnerModuleManager.isPartneridExistforModules(partnerId)){
            hash = partnerModuleManager.partnerUserModulesList(partnerId);
        }
        else if(superadminid.equals("1") || superadminid.equals("0"))
        {
            hash = partnerModuleManager.getPartnerModulesList();
        }else if(partnerModuleManager.isPartneridExistforModules(superadminid)){
           hash = partnerModuleManager.partnerUserModulesList(superadminid);
        }else{
            hash = partnerModuleManager.getPartnerModulesList();
        }

    Set<String> moduleSet1 = partnerModuleManager.getPartnerAccessModuleSet(String.valueOf(userId));
    List<PartnerSubModuleVO> list = null;
    Functions functions = new Functions();
%>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title>Partner Module Management</title>
</head>
<style type="text/css">
    input[type=radio], input[type=checkbox] {
        transform: scale(2);
        -ms-transform: scale(2);
        -webkit-transform: scale(2);
        padding: 10px;
    }

    .control-label {
        font-size: 15px;
    }
</style>
<body>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>

<script>

    function changeModuleId(moduleid,list){

        for(var i=0;i<list.length;i++)
        {
            if (document.getElementById("moduleid_" + moduleid).checked)
            {
                document.getElementById("submoduleid_"+list[i]).disabled = false;
            }
            else
                document.getElementById("submoduleid_"+list[i]).disabled = true;
                document.getElementById("submoduleid_"+list[i]).checked = false;

        }

    }

    function ToggleAll(checkbox)
    {
        var selects = document.getElementsByName("submoduleid");
        if (checkbox.checked)
        {
            // Iterate each checkbox
            $(':checkbox').each(function ()
            {
                this.checked = true;
                this.disabled = false;
            });
        }
        else
        {
            $(':checkbox').each(function ()
            {
                this.checked = false;
                var rowCount = selects.length;
                for(var i=0; i<rowCount; i++) {
                    selects[i].disabled = true;
                }
            });
        }
    }
</script>

<div class="content-page">
    <div class="content">
        <div class="page-heading">
            <div class="pull-right">
                <div class="btn-group">
                 <%--   <form action="/icici/PartnerUserList?ctoken=<%=ctoken%>" method="post" name="form">

                        <%
                            Enumeration<String> stringEnumeration = request.getParameterNames();
                            while (stringEnumeration.hasMoreElements())
                            {
                                String name = stringEnumeration.nextElement();
                                if ("accountid".equals(name))
                                {
                                    out.println("<input type='hidden' name='" + name + "' value='" + request.getParameterValues(name)[1] + "'/>");
                                }
                                else
                                    out.println("<input type='hidden' name='" + name + "' value='" + request.getParameter(name) + "'/>");
                            }
                        %>


                    </form>--%>
                </div>
            </div>
            <br><br><br>

            <div class="row">
                <div class="col-lg-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <strong> Partner Module Management </strong>
                            <div style="float: right;">
                                <form action="/icici/servlet/PartnerUserList?ctoken=<%=ctoken%>" name="form" method="post">
                                        <%
                                            Enumeration<String> aName = request.getParameterNames();
                                            while (aName.hasMoreElements())
                                            {
                                                String name = aName.nextElement();
                                                String value = request.getParameter(name);
                                                if (value == null || value.equals("null"))
                                                {
                                                    value = "";
                                                }
                                        %>
                                        <input type=hidden name=<%=name%> value=<%=value%>>
                                        <%
                                            }
                                        %>
                                        <button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i
                                                class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp;Go Back
                                        </button>
                                    </form>

                            </div>
                        </div>
                        &nbsp;&nbsp;

                        <div class="widget-content padding" style="overflow-x: auto;">
                            <%
                                if (functions.isValueNull(error))
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                                }
                                if (functions.isValueNull(message))
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                                }
                                if (functions.isValueNull(successMessage))
                                {
                                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + successMessage + "</h5>");
                                }
                            %>
                            <form action="/icici/servlet/PartnerUserModuleAllocation?ctoken=<%=ctoken%>" method="post"
                                  name="forms" class="form-horizontal">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <input type="hidden" value="<%=request.getParameter("userid")%>" name="userid">
                                <input type="hidden" value="<%=request.getParameter("memberid")%>" name="memberid">
                                <input type="hidden" value="<%=request.getParameter("login")%>" name="login">
                                <input type="hidden" value="<%=request.getParameter("partnerid")%>" name="partnerid">

                                <div class="widget-content padding">
                                    <div class="form-group">
                                        <label class="col-md-5 control-label">Partner User</label>

                                        <div class="col-md-4">
                                            <input type="text" class="form-control" name="login"
                                                   value="<%=ESAPI.encoder().encodeForHTML(request.getParameter("login"))%>"
                                                   disabled>
                                        </div>
                                        <div class="col-md-6"></div>
                                    </div>
                                    <%
                                        Set<String> keys=hash.keySet();
                                        if(keys.size()==0)
                                        {
                                            out.println("<BR><BR><BR>");
                                            out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp; No Records Found  </h5>");                                        }else{
                                    %>
                                    <div class="widget-content padding">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <table align=center width="50%"
                                               class="display table table table-striped table-bordered table-hover dataTable"
                                               style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                            <thead>
                                            <tr>
                                                <td valign="left" align="left"
                                                    style="background-color: #34495e !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                    <b>&nbsp;&nbsp;Module Name&nbsp;&nbsp;</b>
                                                    <input type="checkbox" name="select-all" id="select-all"
                                                           onclick="ToggleAll(this)"/></td>
                                            </tr>
                                            </thead>
                                        </table>

                                        <%
                                            String style_new="";
                                            for (String key1 : keys)
                                            {
                                                String modules[] = key1.split("-");
                                                String moduleId = modules[0];
                                                String moduleName = modules[1];
                                                list = hash.get(moduleId + "-" + moduleName);
                                                String checked = "";
                                                if (moduleSet1.contains(moduleName))
                                                {
                                                    checked = "checked";
                                                }
                                                String role= partnerModuleManager.getRoleofPartner(request.getParameter("partnerid"));
                                                if(PartnerModuleEnum.AGENT_MANAGEMENT.toString().equals (moduleName))
                                                {
                                                    if (!role.contains("superpartner"))
                                                    {
                                                        style_new = "display:none";
                                                    }
                                                    else
                                                    {
                                                         style_new="";
                                                    }
                                                }
                                                ArrayList jslist= new ArrayList();
                                                for (PartnerSubModuleVO partnerSubModuleVO : list)
                                                {
                                                    jslist.add(partnerSubModuleVO.getSubModuleId());
                                                }
                                        %>
                                        <table align=center width="50%"
                                               class="display table table table-striped table-bordered table-hover dataTable"
                                               style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                            <tr style="<%=style_new%>">
                                                <td align="left" style="width: 20%">
                                                </td>
                                                <td align="left">
                                                    <input type="checkbox" id="moduleid_<%=moduleId%>" name="moduleid" <%=checked%> onclick="changeModuleId('<%=moduleId%>',<%=jslist%>);"
                                                           style=" width: 10px;float: left;" value=<%=moduleId%>>&nbsp;&nbsp;<%=moduleName.replace("_", " ")%>
                                                    <br>
                                                </td>
                                            </tr>
                                            <%
                                                if (list != null && list.size() > 0)
                                                {
                                            %>
                                            <tr>
                                                <table align=center width="50%"
                                                       class="display table table table-striped table-bordered table-hover dataTable"
                                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                                                    <%
                                                        for (PartnerSubModuleVO partnerSubModuleVO : list)
                                                        {
                                                            String check1 = "";
                                                            String disabled = "";
                                                            String style1 = "";
                                                            if ("N".equals(isRefund) && PartnerModuleEnum.MERCHANT_REFUND.toString().equals(partnerSubModuleVO.getSubModuleName()))
                                                            {
                                                                continue;
                                                            }
                                                            if (moduleSet1.contains(partnerSubModuleVO.getSubModuleName()))
                                                            {
                                                                check1 = "checked";
                                                            }

                                                            if(checked.equals(""))
                                                            {
                                                                disabled ="disabled";
                                                            }
                                                            if (PartnerModuleEnum.GENERATE_SECRET_KEY.toString().equals(partnerSubModuleVO.getSubModuleName()))
                                                            {
                                                                style1 = "display:none";
                                                            }
                                                            String Role = partnerModuleManager.getRoleofPartner(request.getParameter("partnerid"));
                                                            if(PartnerModuleEnum.PARTNER_MASTER.toString().equals(partnerSubModuleVO.getSubModuleName()))
                                                            {
                                                                if (!Role.contains("superpartner"))
                                                                {
                                                                    style1 = "display:none";
                                                                }
                                                            }
                                                            if(PartnerModuleEnum.AGENT_MASTER.toString().equals(partnerSubModuleVO.getSubModuleName()))
                                                            {
                                                                if (!Role.contains("superpartner"))
                                                                {
                                                                    style1 = "display:none";
                                                                }
                                                            }
                                                            if(PartnerModuleEnum.AGENT_WHITELIST_DETAILS.toString().equals(partnerSubModuleVO.getSubModuleName()))
                                                            {
                                                                if(!Role.contains("superpartner"))
                                                                {
                                                                    style1 = "display:none";
                                                                }
                                                            }
                                                            if(PartnerModuleEnum.MERCHANT_AGENT_MAPPING.toString().equals(partnerSubModuleVO.getSubModuleName()))
                                                            {
                                                                if(!Role.contains("superpartner"))
                                                                {
                                                                    style1 = "display:none";
                                                                }
                                                            }
                                                    %>
                                                    <tr style="<%=style1%>">
                                                        <td align="left" style="width: 30%">
                                                        </td>
                                                        <td align="left" <%=style%>>

                                                            <input type="checkbox" <%=disabled%>
                                                                   style=" width: 10px;float: left;" <%=check1%>
                                                                   name="submoduleid" id="submoduleid_<%=partnerSubModuleVO.getSubModuleId()%>"
                                                                   value=<%=partnerSubModuleVO.getSubModuleId()%>>&nbsp;&nbsp;<%=partnerSubModuleVO.getSubModuleName().replace("_", " ")%>
                                                        </td>
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
                                        <br>

                                        <div class="form-group col-md-12 has-feedback">
                                            <center>
                                                <label>&nbsp;</label>
                                                <input type="hidden" value="1" name="step">
                                                <button type="submit" class="btn btn-default" id="submit" name="action"
                                                        value="Save" style="display: -webkit-box;"><i
                                                        class="fa fa-save"></i>&nbsp;&nbsp;Save
                                                </button>
                                            </center>
                                        </div>
                                        <%
                                        }
                                        %>
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
</div>
</body>
</html>