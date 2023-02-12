<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.payoutVOs.ChargeMasterVO" %>
<%@ page import="servlets.ChargesUtils" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 1/19/15
  Time: 7:32 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp"%>
<%!
    Functions functions = new Functions();
%>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <title></title>
</head>
<body>
    <%
  ChargeMasterVO chargeMasterVO=(ChargeMasterVO)request.getAttribute("chargeMasterVO");
  if(chargeMasterVO!=null)
  {
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Update Charge Master
            </div>

            <form action="/icici/servlet/UpdateChargeMaster?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <table align="center" width="65%" cellpadding="2" cellspacing="2">

                    <tbody>
                    <%
                        if(request.getParameter("MES")!=null)
                        {
                            String mes=request.getParameter("MES");
                            if(mes.equals("ERR"))
                            {
                                String error=(String)request.getAttribute("error");
                                out.println("<center><font class=\"textb\">" +error + "</font></center>");
                            }

                        }
                    %>
                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tbody>
                                <tr><td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb">Charge Id</td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%" class="textb">
                                        <input maxlength="50" type="text" name="chargeid" class="txtbox" value="<%=chargeMasterVO.getChargeId()%>" readonly style="background-color: rgb(229, 229, 229)">
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
                                    <td width="43%" class="textb">Charge Name*</td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%" class="textb">
                                        <input maxlength="255" type="text" name="chargename" class="txtbox" value="<%=chargeMasterVO.getChargeName()%>">
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
                                            <option value="Y" <%if(chargeMasterVO.getInInputRequired().equals("Y")) out.println("selected");%>>Yes</option>
                                            <option value="N" <%if(chargeMasterVO.getInInputRequired().equals("N")) out.println("selected");%>>No</option>
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
                                        <input maxlength="255" type="text" name="chargeTechName" class="txtbox" value="<%=chargeMasterVO.getKeyName()%>">
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
                                        <select name="valuetype" class="txtbox">
                                            <option value=""></option>
                                            <%
                                                String valueType=chargeMasterVO.getValueType();
                                                for (ChargesUtils.unit unit: ChargesUtils.unit.values()){
                                                    String tx1="";
                                                    if(unit.name().equals(valueType))
                                                    {
                                                        tx1="selected";
                                                    }
                                                    %>
                                                     <option value=<%=unit.name()%> <%=tx1%>><%=unit.name()%></option>
                                                    <%

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
                                            <option value=""></option>
                                            <%
                                                String category=chargeMasterVO.getCategory();
                                                for (ChargesUtils.category cat: ChargesUtils.category.values()){
                                                    String tx1="";
                                                    if(cat.name().equals(category))
                                                    {
                                                        tx1="selected";
                                                    }
                                                    %>
                                            <option value=<%=cat.name()%> <%=tx1%>><%=cat.name()%></option>
                                            <%
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
                                            <option value=""></option>
                                            <%
                                                String keyword=chargeMasterVO.getKeyword();
                                                for (ChargesUtils.keyword key: ChargesUtils.keyword.values()){
                                                    String tx1="";
                                                    if(key.name().equals(keyword))
                                                    {
                                                        tx1="selected";
                                                    }
                                                    %>
                                                     <option value=<%=key.name()%> <%=tx1%>><%=key.name()%></option>
                                                    <%

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
                                    <td class="textb">Sub-Keyword*</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <select name="subkeyword" class="txtbox">
                                            <option value=""></option>
                                            <%
                                                String subKeyword=chargeMasterVO.getSubKeyword();
                                                for (ChargesUtils.subKeyword subKey: ChargesUtils.subKeyword.values()){
                                                    String tx1="";
                                                    if(subKey.name().equals(subKeyword))
                                                    {
                                                        tx1="selected";
                                                    }
                                                    %>
                                            <option value=<%=subKey.name()%> <%=tx1%>><%=subKey.name()%></option>
                                            <%

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
                                            <option value=""></option>
                                            <%
                                                String frequency=chargeMasterVO.getFrequency();
                                                for (ChargesUtils.frequency freq: ChargesUtils.frequency.values()){
                                                    String tx1="";
                                                    if(freq.name().equals(frequency))
                                                    {
                                                        tx1="selected";
                                                    }
                                                    %>
                                            <option value=<%=freq.name()%> <%=tx1%>><%=freq.name()%></option>
                                            <%

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
                                        <%
                                            String sequencenum="";
                                            if(chargeMasterVO.getSequenceNumber()!=null)
                                            {
                                                sequencenum=chargeMasterVO.getSequenceNumber();
                                            }
                                        %>
                                        <input maxlength="50" type="text" name="sequencenum" class="txtbox" value="<%=sequencenum%>">
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
                                        <button type="submit" class="buttonform" name="update" value="<%=chargeMasterVO.getChargeId()%>">
                                            <i class="fa fa-sign-in"></i>
                                            &nbsp;&nbsp;Update
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
      }
      else
      {
        out.println("<br><br><br>");
        out.println(Functions.NewShowConfirmation("Sorry","No Records Found"));
      }
    %>
