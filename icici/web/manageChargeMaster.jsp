<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.manager.ChargeManager" %>
<%@ page import="com.manager.ChargeValidationManager" %>
<%@ page import="com.manager.vo.payoutVOs.ChargeMasterVO" %>
<%@ page import="servlets.ChargesUtils" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="functions.jsp" %>

<%@ include file="index.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 11/8/12
  Time: 3:09 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <title></title>
</head>
<body>
<%!
    Logger logger = new Logger("manageChargeMaster");
%>
<% ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    String role = "Admin";
    String username = (String) session.getAttribute("username");
    String actionExecutorId = (String) session.getAttribute("merchantid");
    String actionExecutorName = role + "-" + username;

    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Create New Charge
                <div style="float: right;">
                    <form action="/icici/listChargeMaster.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" value="Charge Master" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Charge Master
                        </button>
                    </form>
                </div>
            </div>
            <br>

            <form action="/icici/manageChargeMaster.jsp?ctoken=<%=ctoken%>" method="post" name="forms">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <input type="hidden" value="true" name="isSubmitted">
                <table align="center" width="65%" cellpadding="2" cellspacing="2">

                    <tbody>
                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tbody>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb">Charge Name*</td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%" class="textb">
                                        <input maxlength="255" type="text" name="chargename" class="txtbox" value="">
                                    </td>

                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb"></td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Input Required?*</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <select name="isinputrequired" class="txtbox">
                                            <option value=""> -- select -- </option>
                                            <option value="Y">Yes</option>
                                            <option value="N" selected>No</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb"></td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Charge Technical Name</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <input maxlength="255" type="text" name="chargeTechName" class="txtbox"
                                               value="">
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb"></td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Charge Unit*</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <select name="chargetype" class="txtbox">
                                            <option value=""> -- select -- </option>
                                            <%
                                                for (ChargesUtils.unit unit : ChargesUtils.unit.values())
                                                {
                                                    out.println("<option value=\"" + unit.name() + "\">" + unit.name() + "</option>");
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb"></td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Category*</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <select name="category" class="txtbox">
                                            <option value=""> -- select -- </option>
                                            <%
                                                for (ChargesUtils.category cat : ChargesUtils.category.values())
                                                {
                                                    out.println("<option value=\"" + cat.name() + "\">" + cat.name() + "</option>");
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb"></td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Keyword*</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <select name="keyword" class="txtbox">
                                            <option value=""> -- select -- </option>
                                            <%
                                                for (ChargesUtils.keyword key : ChargesUtils.keyword.values())
                                                {
                                                    out.println("<option value=\"" + key.name() + "\">" + key.name() + "</option>");
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb"></td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">SubKeyword*</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <select name="subkeyword" class="txtbox">
                                            <option value=""> -- select -- </option>
                                            <%
                                                for (ChargesUtils.subKeyword subKey : ChargesUtils.subKeyword.values())
                                                {
                                                    out.println("<option value=\"" + subKey.name() + "\">" + subKey.name() + "</option>");
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb"></td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Frequency*</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <select name="frequency" class="txtbox">
                                            <option value=""> -- select -- </option>
                                            <%
                                                for (ChargesUtils.frequency freq : ChargesUtils.frequency.values())
                                                {
                                                    out.println("<option value=\"" + freq.name() + "\">" + freq.name() + "</option>");
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb"></td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Sequence Number*</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <input maxlength="15" class="txtbox" type="text" name="sequencenum" value="">
                                    </td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb"></td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb">
                                        <button type="submit" class="buttonform" value="Save">
                                            <i class="fa fa-sign-in"></i>
                                            &nbsp;&nbsp;Save
                                        </button>
                                    </td>
                                </tr>

                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb"></td>
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

    String isSubmitted = request.getParameter("isSubmitted");
    if ("true".equals(isSubmitted))
    {
%>
<div class="reporttable">
    <%
                try
                {


                    ChargeMasterVO chargeMasterVO = new ChargeMasterVO();
                    chargeMasterVO.setChargeName(request.getParameter("chargename"));
                    chargeMasterVO.setInInputRequired(request.getParameter("isinputrequired"));
                    chargeMasterVO.setKeyName(request.getParameter("chargeTechName"));
                    chargeMasterVO.setValueType(request.getParameter("chargetype"));
                    chargeMasterVO.setCategory(request.getParameter("category"));
                    chargeMasterVO.setKeyword(request.getParameter("keyword"));
                    chargeMasterVO.setSubKeyword(request.getParameter("subkeyword"));
                    chargeMasterVO.setFrequency(request.getParameter("frequency"));
                    chargeMasterVO.setSequenceNumber(request.getParameter("sequencenum"));
                    chargeMasterVO.setActionExecutorId(actionExecutorId);
                    chargeMasterVO.setActionExecutorName(actionExecutorName);
                    ChargeValidationManager validationManager = new ChargeValidationManager();
                    String error = validationManager.performMandatoryChargeMasterValidation(chargeMasterVO);
                    error = error + validationManager.performOptionalChargeMasterValidation(chargeMasterVO);
                    Functions functions = new Functions();
                    if (functions.isValueNull(request.getParameter("chargename"))&&functions.hasHTMLTags(request.getParameter("chargename")))
                    {
                        error = error + "Invalid Chargename.";
                    }
                    if (functions.isValueNull(request.getParameter("chargeTechName"))&&functions.hasHTMLTags(request.getParameter("chargeTechName")))
                    {
                        error = error + "Invalid Charge Technical Name.";
                    }
                    if (error.length() > 0)
                    {
                        out.println(Functions.NewShowConfirmation("Failed", error));
                    }
                    else
                    {
                        ChargeManager chargeManager = new ChargeManager();
                        String status = chargeManager.addNewBusinessCharge(chargeMasterVO);
                        if ("success".equals(status))
                        {
                            ChargesUtils.loadCharges();
                            out.println(Functions.NewShowConfirmation("Success", "New Charge Created Successfully."));
                        }
                        else
                        {
                            out.println(Functions.NewShowConfirmation("Failed", "New Charge Creation Failed."));
                        }
                    }
                }
                catch (SystemError systemError)
                {
                    logger.error("SystemError::::::" + systemError);
                    out.println(Functions.NewShowConfirmation("Error", systemError.getMessage()));
                }
                catch (SQLException se)
                {
                    logger.error("SQLException:::::" + se);
                    out.println(Functions.NewShowConfirmation("Error", se.getMessage()));
                }
                catch (Exception e)
                {
                    logger.error("GenericException:::: " + e);
                    out.println(Functions.NewShowConfirmation("Error", e.getMessage()));
                }
            }
        }
        else
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
    %>
</div>
</body>
</html>