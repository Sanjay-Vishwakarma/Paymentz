<%@ page import="com.directi.pg.Database,com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.manager.PartnerManager" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="com.manager.vo.WLPartnerInvoiceVO" %>
<%@ page import="com.manager.WLPartnerInvoiceManager" %>
<%@ page import="java.util.List" %>
<%@ include file="index.jsp" %>
<%@ include file="functions.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: Naushad
  Date: 11/26/2016
  Time: 6:53 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid.js"></script>

<html>
<head>
    <title>Settings> Charges Interface> WhiteLabel Partner Commission</title>
    <script language="javascript">
        function ToggleAll(checkbox)
        {
            flag= checkbox.checked;
            var checkboxes= document.getElementsByName("id");
            var total_boxes= checkboxes.length;

            for(i=0; i<total_boxes; i++)
            {
                checkboxes[i].checked= flag;
            }
        }
        </script>
        <script language="javascript">
        function Delete()
        {
            var checkboxes= document.getElementsByName("id");
            var checked=[];

            var total_boxes= checkboxes.length;
            flag= false;
            for(i=0; i<total_boxes; i++)
            {
                if(checkboxes[i].checked)
                {
                    flag= true;
                    checked.push(checkboxes[i].value);
                    checked.join(',');
                }
            }
            document.getElementById("ids").value= checked;
            if(!flag)
            {
                alert("Select at least one record");
                return false;
            }
            if(confirm("Do you really want to Delete all selected Data."))
            {
                document.delete.submit();
            }
        }
    </script>
</head>
<body>
<%
    Logger logger = new Logger("wlpartnercommmappinglist.jsp");
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String partnerId = request.getParameter("partnername") == null ? "" : request.getParameter("partnername");
        String pgtypeId = request.getParameter("gatewaybankname") == null ? "" : request.getParameter("gatewaybankname");
        String str = "ctoken=" + ctoken;
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;

        if (pgtypeId != null)
        {
            str = str + "&gatewaybankname=" + pgtypeId;
        }
        if (partnerId != null)
        {
            str = str + "&partnername=" + partnerId;
        }
        PartnerManager partnerManager = new PartnerManager();
        List<PartnerDetailsVO> partnerList = partnerManager.getAllWhitelabelPartners();
        WLPartnerInvoiceManager wlPartnerInvoiceManager = new WLPartnerInvoiceManager();
        List<WLPartnerInvoiceVO> gatewayList = wlPartnerInvoiceManager.getListOfAllWLPartnerGateways();

%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                WL Partner Commission
                <div style="float: right;">
                    <form action="/icici/wlpartnercommmapping.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add New WL Partner Commission" name="submit"
                                style="width:250px ">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Map New Commission
                        </button>
                    </form>
                </div>
            </div>
            <form action="/icici/servlet/WLPartnerCommMappingList?ctoken=<%=ctoken%>" method="post" name="f1">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%
                    int pageno = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
                    int pagerecords = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
                %>
                <table align="center" width="95%" cellpadding="2" cellspacing="2"
                       style="margin-left:1.5%;margin-right: 2.5% ">
                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb">Partner ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="partnername" id="pid1" value="<%=partnerId%>" class="txtbox"
                                               autocomplete="on">
                                        <%--<select name="partnername" id="partnerid" class="txtbox">
                                            <option value="" selected>Select Partner ID</option>
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
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb">Gateway</td>
                                    <td width="5%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="gatewaybankname" id="gateway" value="<%=pgtypeId%>" class="txtbox"
                                               autocomplete="on">
                                        <%--  <select name="gatewaybankname" id="gateway" class="txtbox">
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
                                            <option data-pid="<%=wlPartnerInvoiceVO.getPgtypeId()%>" value="<%=wlPartnerInvoiceVO.getPgtypeId()%>" <%=isSelected%>><%=wlPartnerInvoiceVO.getPgtypeId()+"-"+wlPartnerInvoiceVO.getGateway()+"-"+wlPartnerInvoiceVO.getCurrency()%>

                                                <%
                                              }
                                          %>
                                          </select>--%>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <button type="submit" class="buttonform">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<div class="reporttable">
    <% String errormsg1 = (String) request.getAttribute("message");
        if (errormsg1 == null)
        {
            errormsg1 = "";
        }
        else
        {
            out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" >");
            out.println(errormsg1);
            out.println("</font></td></tr></table>");
        }
    %>
    <%
        Functions functions = new Functions();
        String success1= (String)request.getAttribute("success1");

        if (success1== null){
            success1="0";
        Hashtable hash = (Hashtable) request.getAttribute("transdetails");
        Hashtable temphash = null;
        int records = 0;
        int totalrecords = 0;

        String currentblock = request.getParameter("currentblock");
        if (currentblock == null)
            currentblock = "1";
        try
        {
            records = Integer.parseInt((String) hash.get("records"));
            totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
        }
        catch (Exception ex)
        {

        }
        if (hash != null)
        {
            hash = (Hashtable) request.getAttribute("transdetails");
        }
        if (records > 0)
        {
    %>
    <div id="containrecord"></div>
    <table align=center width="70%" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <td width="4%" class="th0"><b><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></b></td>
            <td width="4%" class="th0">Sr No</td>
            <td width="5%" class="th0">Partner Name</td>
            <td width="5%" class="th0">Gateway/Bank Name</td>
            <td width="5%" class="th0">Currency</td>
            <td width="8%" class="th0">Commission Name</td>
            <td width="4%" class="th1">Commission Value</td>
            <td width="4%" class="th1">Start Date</td>
            <td width="4%" class="th1">End Date</td>
            <td width="4%" class="th1">Sequence Number</td>
            <td width="4%" class="th1">Is Active</td>
            <td width="4%" class="th1" colspan="2">Action</td>
            <td width="4%" class="th1">Action Executor Id</td>
            <td width="4%" class="th1">Action Executor Name</td>
        </tr>
        </thead>
            <%
        String style="class=td1";
        String ext="light";
        String gatewayType="";
        String currency="";
        for(int pos=1;pos<=records;pos++)
        {
            String id=Integer.toString(pos);
            int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);
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

            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
            temphash=(Hashtable)hash.get(id);
             String check = (String) temphash.get("keyword");

            if(temphash.get("gateway")!=null)
            {
              gatewayType=(String)temphash.get("gateway");
            }
            if(temphash.get("currency")!=null)
            {
              currency=(String)temphash.get("currency");
            }
              if(functions.isValueNull((String)temphash.get("actionExecutorId")))
            {
                actionExecutorId=(String)temphash.get("actionExecutorId");
            }
            else
            {
                actionExecutorId="-";
            }
            if(functions.isValueNull((String)temphash.get("actionExecutorName")))
            {
                actionExecutorName=(String)temphash.get("actionExecutorName");
            }
            else
            {
                actionExecutorName="-";
            }
            out.println("<tr>");
            out.println("<td align=\"center\" " +style+">&nbsp;<input type=\"checkbox\" name=\"id\" value=\""  +ESAPI.encoder().encodeForHTML((String)temphash.get("id"))+ "\"></td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+srno+ "</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("partnerName"))+"</td>");
            if(check.equals("ServiceTax"))
            {
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML("-")+"</td>");
            }
            else
            {
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(gatewayType)+"</td>");
            }

            if(check.equals("ServiceTax"))
            {
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML("-")+"</td>");
            }
            else
            {
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(currency)+"</td>");
            }
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargename"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("commission_value"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+targetFormat.format(targetFormat.parse((String)temphash.get("start_date")))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+targetFormat.format(targetFormat.parse((String)temphash.get("end_date")))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("sequence_no"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("isActive"))+"</td>");
            out.println("<td align=\"center\" " + style + "><form action=\"/icici/servlet/ActionWLPartnerCommMapping?ctoken=" + ctoken + "\" method=\"post\"><input type=\"hidden\" name=\"commissionid\" value=\"" + temphash.get("id") + "\"><input type=\"hidden\" name=\"partnername\" value=\"" + partnerId + "\"><input type=\"hidden\" name=\"gatewaybankname\" value=\"" + pgtypeId + "\"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"submit\" class=\"goto\" value=\"Modify\"></form></td>");
            out.println("<td align=\"center\" " + style + "><form action=\"/icici/servlet/ActionWLPartnerCommMapping?ctoken=" + ctoken + "\" method=\"post\"><input type=\"hidden\" name=\"mappingid\" value=\"" + temphash.get("mappingid") + "\"><input type=\"hidden\" name=\"partnername\" value=\"" + partnerId + "\"><input type=\"hidden\" name=\"gatewaybankname\" value=\"" + pgtypeId + "\"><input type=\"hidden\" name=\"action\" value=\"history\"><input type=\"submit\" class=\"goto\" value=\"History\" disabled></form></td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorId)+"</td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorName)+"</td>");
        }
    %>
        </table>
        <form action="/icici/servlet/WLPartnerCommMappingList?ctoken=<%=ctoken%>" name="delete" method="post">
            <table width="100%">
                <thead>
                    <tr>
                        <td width="15%" align="center">
                            <input type="hidden" name="delete" value="delete">
                            <input type="hidden" id="ids" name="ids" value="">
                            <input type="button" name="delete" value="Delete" class="btn btn-default center-block" onclick="return Delete();">
                        </td>
                    </tr>
                </thead>
            </table>
        </form>
        <table align=center valign=top>
            <tr>
                <td align=center>
                    <jsp:include page="page.jsp" flush="true">
                        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                        <jsp:param name="numrows" value="<%=pagerecords%>"/>
                        <jsp:param name="pageno" value="<%=pageno%>"/>
                        <jsp:param name="str" value="<%=str%>"/>
                        <jsp:param name="page" value="WLPartnerCommMappingList"/>
                        <jsp:param name="currentblock" value="<%=currentblock%>"/>
                        <jsp:param name="orderby" value=""/>
                    </jsp:include>
                </td>
            </tr>
        </table>
            <%
      }
      else if(functions.isValueNull((String)request.getAttribute("statusMsg")))
      {
      out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("statusMsg")));
      }
      else
      {
         out.println(Functions.NewShowConfirmation("Result", "No Records Found."));
      }
                }
                else
                 {
                    out.println(Functions.NewShowConfirmation("Result",success1));
                 }
  %>
</div>
<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>
