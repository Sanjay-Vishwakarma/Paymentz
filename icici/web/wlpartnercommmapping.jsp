<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.PartnerManager" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="com.manager.WLPartnerInvoiceManager" %>
<%@ page import="com.manager.vo.WLPartnerInvoiceVO" %>
<%@ page import="com.manager.ChargeManager" %>
<%@ page import="com.manager.vo.ChargeVO" %>
<%@ page import="java.util.List" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Created by IntelliJ IDEA.
  User: Naushad
  Date: 11/24/16
  Time: 12:52 AM
  To change this template use File | Settings | File Templates.
--%>
<%!
    Logger logger=new Logger("wlpartnercommmapping.jsp");
    Functions functions= new Functions();
%>
<html>
<head>
    <title> WL Partner Commission Mapping</title>

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid.js"></script>

    <%--<script type="text/javascript" src="/icici/javascript/jquery-1.7.1.js"></script>--%>
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script type="text/javascript">
        $('#sandbox-container input').datepicker({
        });
    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd',endDate:'+10y'});
        });
    </script>

</head>
<body>
<%
    Logger logger = new Logger("wlpartnercommmapping.jsp");
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String partnerId=request.getParameter("partnername")==null?"":request.getParameter("partnername");
        String pgtypeId=request.getParameter("gatewaybankname")==null?"":request.getParameter("gatewaybankname");
        String chargeId=request.getParameter("commissionname");
        String str="ctoken=" + ctoken;
        if(pgtypeId!=null){str=str+"&gatewaybankname="+pgtypeId;}
        if(partnerId!=null){str=str+"&partnername="+partnerId;}

        PartnerManager partnerManager=new PartnerManager();
        List<PartnerDetailsVO> partnerList=partnerManager.getAllWhitelabelPartners();
        WLPartnerInvoiceManager wlPartnerInvoiceManager=new WLPartnerInvoiceManager();
        List<WLPartnerInvoiceVO> gatewayList=wlPartnerInvoiceManager.getListOfAllWLPartnerGateways();
        ChargeManager chargeManager=new ChargeManager();
        List<ChargeVO> chargeList=chargeManager.getListOfCharges();

%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                WL Partner Commission Mapping
                <div style="float: right;">
                    <form action="/icici/wlpartnercommmappinglist.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Commission List
                        </button>
                    </form>
                </div>
            </div>
            <form action="/icici/servlet/WLPartnerCommMapping?ctoken=<%=ctoken%>" method="post" name="f1" id="f1">
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <table align="center" width="65%" cellpadding="2" cellspacing="2">
                    <tbody>
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tbody>
                                <tr><td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Partner Name</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <input name="partnername" id="pid1" value="<%=partnerId%>" class="txtbox" autocomplete="on">
                                        <%--<select name="partnername" class="txtbox">
                                        <option value="" selected>Select Partner Name</option>
                                        <%
                                            for(PartnerDetailsVO partnerDetailsVO:partnerList)
                                            {
                                                String isSelected="";
                                                if(partnerDetailsVO.getPartnerId().equalsIgnoreCase(partnerId))
                                                    isSelected="selected";
                                                else
                                                    isSelected="";
                                        %>
                                        <option value="<%=partnerDetailsVO.getPartnerId()%>" <%=isSelected%>><%=partnerDetailsVO.getPartnerId()+"-"+partnerDetailsVO.getCompanyName()%></option>
                                        <%
                                            }
                                        %>
                                    </select>--%>
                                    </td>
                                </tr>
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Gateway</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <input name="gatewaybankname" id="gateway" value="<%=pgtypeId%>" class="txtbox" autocomplete="on">
                                       <%-- <select name="gatewaybankname" id="gateway" class="txtbox">
                                            <option  value="">--All--</option>
                                            <%
                                                for(WLPartnerInvoiceVO wlPartnerInvoiceVO:gatewayList)
                                                {
                                                    String isSelected="";
                                                    if(wlPartnerInvoiceVO.getPgtypeId().equals(pgtypeId))
                                                        isSelected="selected";
                                                    else
                                                        isSelected="";
                                            %>
                                            <option value="<%=wlPartnerInvoiceVO.getPgtypeId()%>" <%=isSelected%>><%=wlPartnerInvoiceVO.getPgtypeId()+"-"+wlPartnerInvoiceVO.getGateway()+"-"+wlPartnerInvoiceVO.getCurrency()%>
                                                    <%
                                                         }
                                                    %>
                                        </select>--%>
                                    </td>
                                </tr>
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Commission Name</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><select name="commissionname" class="txtbox">
                                        <option value="" default>Select Comission Name</option>
                                        <%
                                            for(ChargeVO chargeVO:chargeList)
                                            {
                                                String isSelected="";
                                                if(chargeVO.getChargeid().equals(chargeId))
                                                    isSelected="selected";
                                                else
                                                    isSelected="";
                                        %>
                                        <option value="<%=chargeVO.getChargeid()%>" <%=isSelected%>><%=chargeVO.getChargeid()+"-"+chargeVO.getChargename()%></option>
                                        <%
                                            }
                                        %>
                                    </select></td>
                                </tr>
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Input Required</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="inputrequired" class="txtbox">
                                            <option value="N">N</option>
                                            <option value="Y">Y</option>
                                        </select></td>
                                </tr>
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Sequence Number</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="sequencenum" class="txtbox">
                                            <option value="">Select Sequence Number</option>
                                            <option value="1">01</option>
                                            <option value="2">02</option>
                                            <option value="3">03</option>
                                            <option value="4">04</option>
                                            <option value="5">05</option>
                                            <option value="6">06</option>
                                            <option value="7">07</option>
                                            <option value="8">08</option>
                                            <option value="9">09</option>
                                            <option value="10">10</option>
                                            <option value="11">11</option>
                                            <option value="12">12</option>
                                            <option value="13">13</option>
                                            <option value="14">14</option>
                                            <option value="15">15</option>
                                            <option value="16">16</option>
                                            <option value="17">17</option>
                                            <option value="18">18</option>
                                            <option value="19">19</option>
                                            <option value="20">20</option>
                                            <option value="21">21</option>
                                            <option value="22">22</option>
                                            <option value="23">23</option>
                                            <option value="24">24</option>
                                            <option value="25">25</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Commission Value</td>
                                    <td style="padding: 3px" class="textb" valign="top">:</td>
                                    <td style="padding: 3px" valign="top">
                                        <input class="txtbox" type="Text" maxlength="100"  name="commissionvalue" id="path" size="100" value="">
                                    </td>
                                </tr>
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Start Date</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <input type="text" size="16"  readonly class="datepicker" name="startdate" >
                                    </td>
                                </tr>
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">End Date</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px" style="">
                                        <input type="text" size="16" readonly class="datepicker" name="enddate" >
                                    </td>
                                </tr>
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Is Active</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="isactive" class="txtbox">
                                            <option value="Y">Y</option>
                                            <option value="N">N</option>
                                        </select></td>
                                </tr>
                                </tr>
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                <tr>
                                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" width="43%" class="textb"></td>
                                    <td style="padding: 3px" width="5%" class="textb"></td>
                                    <td style="padding: 3px" width="50%" class="textb">
                                        <button type="submit" class="buttonform" value="Save" style="width:120px ">
                                            <i class="fa fa-sign-in"></i>
                                            &nbsp;&nbsp;Save
                                        </button>
                                    </td>
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
        if (functions.isValueNull((String) request.getAttribute("statusMessage")))
        {
            out.println("<div class=\"reporttable\">");
            out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("statusMessage")));
            out.println("</div>");
        }
    }
%>
</body>
</html>
