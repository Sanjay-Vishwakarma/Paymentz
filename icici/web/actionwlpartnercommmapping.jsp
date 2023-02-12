<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger" %>
<%@ page import="com.manager.PartnerManager" %>
<%@ page import="com.manager.WLPartnerInvoiceManager" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="com.manager.vo.WLPartnerInvoiceVO" %>
<%@ page import="com.manager.vo.payoutVOs.WLPartnerCommissionVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Naushad
  Date: 11/26/2016
  Time: 6:53 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>WL Partner Commission</title>

</head>
<body>
<%
  Logger logger = new Logger("actionwlpartnercommmapping.jsp");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String partnerId=request.getParameter("partnername");
    String pgtypeId=request.getParameter("gatewaybankname");

    String str="ctoken=" + ctoken;
    if(pgtypeId!=null){str=str+"&gatewaybankname="+pgtypeId;}
    if(partnerId!=null){str=str+"&partnername="+partnerId;}

    PartnerManager partnerManager=new PartnerManager();
    List<PartnerDetailsVO> partnerList=partnerManager.getAllWhitelabelPartners();
    WLPartnerInvoiceManager wlPartnerInvoiceManager=new WLPartnerInvoiceManager();
    List<WLPartnerInvoiceVO> gatewayList=wlPartnerInvoiceManager.getListOfAllWLPartnerGateways();

%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading" >
        WL Partner Commission
        <div style="float: right;">
          <form action="/icici/wlpartnercommmapping.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="addnewmember" value="Add New WL Partner Commission" name="submit" style="width:250px ">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Map New Commission
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/WLPartnerCommMappingList?ctoken=<%=ctoken%>" method="post" name="f1">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb">Partner ID</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select name="partnername" class="txtbox" >
                      <option value="" selected >Select Partner ID</option>
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
                    </select
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Gateway</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select name="gatewaybankname" class="txtbox"><option value="" selected>--All--</option>
                      <%
                        for(WLPartnerInvoiceVO wlPartnerInvoiceVO:gatewayList)
                        {
                          String isSelected="";
                          if(wlPartnerInvoiceVO.getPgtypeId().equals(pgtypeId))
                            isSelected="";
                          else
                            isSelected="";
                      %>
                      <option value="<%=wlPartnerInvoiceVO.getPgtypeId()%>" <%=isSelected%>><%=wlPartnerInvoiceVO.getPgtypeId()+"-"+wlPartnerInvoiceVO.getGateway()+"-"+wlPartnerInvoiceVO.getCurrency()%>
                          <%
                        }
                    %>

                    </select>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" ></td>
                  <td width="3%" class="textb"></td>
                  <td width="10%" class="textb">
                    <button type="submit" class="buttonform" >
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
  <%

    Hashtable innerhash = new Hashtable();
    Hashtable temphash=null;
    String gatewayName = "";
    String currency = "";
    String error = (String) request.getAttribute("errormessage");
    WLPartnerCommissionVO wlPartnerCommissionVO=(WLPartnerCommissionVO)request.getAttribute("wlPartnerCommissionVO");
    String action = (String) request.getAttribute("action");
    SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Functions function = new Functions();

    if (wlPartnerCommissionVO != null)
    {
      if (function.isValueNull(wlPartnerCommissionVO.getGateway()))
      {
        gatewayName = wlPartnerCommissionVO.getGateway();
      }
      if (function.isValueNull(wlPartnerCommissionVO.getCurrency()))
      {
        currency = wlPartnerCommissionVO.getCurrency();
      }
      if (action.equalsIgnoreCase("history"))
      {}
      else
      {
        String style = "class=tr0";

  %> <form action="/icici/servlet/UpdateWLPartnerCommMapping?ctoken=<%=ctoken%>" method="post" name="forms" >
  <input type="hidden" name="commissionid" value="<%=wlPartnerCommissionVO.getCommissionId()%>">
  <table align=center class="table table-striped table-bordered table-green dataTable" style="width:50% ">
    <tr <%=style%>>
      <td class="th0" colspan="2"><b>Update WL Partner Commission</b></td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Partner Name : </td>
      <td class="tr1"><input type="text" class="txtbox1"  size="30" name="" value=" <%=ESAPI.encoder().encodeForHTML(wlPartnerCommissionVO.getPartnerName())%>" disabled> </td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Gateway/Bank Name : </td>
      <td class="tr1"><input type="text" class="txtbox1" size="30" name="gateway"
                             value="<%=ESAPI.encoder().encodeForHTML(gatewayName)%>" disabled></td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Currency : </td>
      <td class="tr1"><input type="text" class="txtbox1" size="30" name="currency"
                             value="<%=ESAPI.encoder().encodeForHTML(currency)%>" disabled></td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Commission Name : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="30" name="chargename" value="<%=ESAPI.encoder().encodeForHTML(wlPartnerCommissionVO.getChargeName())%>" disabled> </td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Commission Value : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="30" name="commission_value" value="<%=wlPartnerCommissionVO.getCommissionValue()%>"></td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Is Input required : </td>
      <td class="tr1"><select name="isinput_required">
        <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTML(wlPartnerCommissionVO.getIsInputRequired())));%></select></td>
      </select> </td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Start Date : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="50" name="start_date" value="<%=targetFormat.format(targetFormat.parse(wlPartnerCommissionVO.getStartDate()))%>" disabled></td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">End Date : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="50"  name="end_date" value="<%=targetFormat.format(targetFormat.parse(wlPartnerCommissionVO.getEndDate()))%>" disabled></td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Sequence No : </td>
      <td class="tr1"><input type="text"  class="txtbox1" size="30" name="sequence_no" value="<%=ESAPI.encoder().encodeForHTML(wlPartnerCommissionVO.getSequenceNo())%>" disabled> </td>
    </tr>
    <tr <%=style%>>
      <td class="tr1">Is Active : </td>
      <td class="tr1"><select name="isActive">
        <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTML(wlPartnerCommissionVO.getIsActive())));%></select></td>
    </tr>
    <tr <%=style%>>
      <td class="td1"></td>
      <td class="td1"><input type="submit" align="center" class="button" value="Update"></td>
    </tr>
  </table>
</form>
  <%
        }
      }
      else
      {
        out.println(Functions.NewShowConfirmation(error!=null?"Result":"Sorry",error!=null?error:"No Records Found."));
      }
    }
  %>
</div>
</body>
</html>