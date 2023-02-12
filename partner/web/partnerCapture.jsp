<%@ page errorPage=""
         import="com.directi.pg.Functions,com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.paymentgateway.CredoraxPaymentGateway" %>
<%@ page import="com.directi.pg.core.paymentgateway.EcorePaymentGateway" %>
<%@ page import="com.payment.sbm.core.SBMPaymentGateway" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ include file="top.jsp" %>

<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","partnerCapture");
%>
<html lang="en">
<head>
  <script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
  <script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <link href="/partner/cookies/quicksand_font.css" rel="stylesheet">

  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <title><%=company%> Transaction Management> Merchant Capture</title>


  <script language="javascript">

    function cancelTrans(icicitransid,ctoken,accountid,terminalbuffer,memberid)
    {
      //alert(icicitransid)
      if (confirm("Do u really want to cancel this transaction.\n\n Tracking : " + icicitransid))
      {
        var e = eval("document.f1.mybutton" + icicitransid)
        e.disabled = true;
        //document.f1.mybutton.disabled=true;
        document.location.href = "/partner/net/CancelTransaction?ctoken="+ctoken+"&icicitransid=" + icicitransid+"&accountid="+accountid+"&terminalbuffer="+terminalbuffer+"&memberid="+memberid;
      }

    }
    function partialCapture(accountid,trackingid,ctoken,auth,memberid)
    {
      //alert(icicitransid)
      if (confirm("Do u really want to Partial Capture this transaction.\n\n Tracking : " + trackingid))
      {
        var e = eval("document.f1.mybutton" + trackingid)
        e.disabled = true;
        //document.f1.mybutton.disabled=true;
        document.location.href = "/partner/net/PartnerCapture?ctoken="+ctoken+"&bank="+accountid+"&partialCapture='true'&trackingid="+trackingid+"&paymentid="+auth+"&memberid="+memberid;
      }

    }
    /*function ToggleAll(checkbox)
    {
      flag = checkbox.checked;
      var checkboxes = document.getElementsByName("trackingid");
      var total_boxes = checkboxes.length;

      for (i = 0; i < total_boxes; i++)
      {
        checkboxes[i].checked = flag;
      }*/
      function MakeSelect2()
      {
        $('select').select2();
        $('.dataTables_filter').each(function ()
        {
          $(this).find('label input[type=text]').attr('placeholder', 'Search');
        });
      }

      $(document).ready(function ()
      {
        // Load Datatables and run plugin on tables
        LoadDataTablesScripts(AllTables);
        // Add Drag-n-Drop feature

        WinMove();
      });

  </script>
<%--  <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
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

  <style type="text/css">

    @media (max-width: 640px){

      .icheckbox_square-aero {
        background-color: white!important;
        border: 1px solid #29aaa1!important;
      }
    }

  </style>
  <style type="text/css">
    #ui-id-2
    {
      overflow: auto;
      max-height: 350px;
    }
  </style>

</head>
<body>

<%

  /* String partnerId = (String) session.getAttribute("merchantid");*/
  String bank = (String) session.getAttribute("bank");
  String memberID = nullToStr(request.getParameter("memberid"));
  String pid = nullToStr(request.getParameter("pid"));
  String Config =null;
  String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
  if(Roles.contains("superpartner")){

  }else{
    pid = String.valueOf(session.getAttribute("merchantid"));
    Config = "disabled";
  }
  String partnerid = session.getAttribute("merchantid").toString();

  String str = "";
  int pageno =1;
  int pagerecords=15;

     pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
     pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);

  str = str + "&SRecords=" + pagerecords;
  str = str + "&bank=" + bank;

  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);


  String partnerCapture_Merchant_Proof_Of_Delivery = rb1.getString("partnerCapture_Merchant_Proof_Of_Delivery");
  String partnerCapture_Tracking_ID = rb1.getString("partnerCapture_Tracking_ID");
  String partnerCapture_Partner_ID = rb1.getString("partnerCapture_Partner_ID");
  String partnerCapture_Merchant_ID = rb1.getString("partnerCapture_Merchant_ID");
  String partnerCapture_Auth_Code = rb1.getString("partnerCapture_Auth_Code");
  String partnerCapture_Path = rb1.getString("partnerCapture_Path");
  String partnerCapture_Search = rb1.getString("partnerCapture_Search");
  String partnerCapture_Capture_Data = rb1.getString("partnerCapture_Capture_Data");
  String partnerCapture_Date = rb1.getString("partnerCapture_Date");
  String partnerCapture_Transaction = rb1.getString("partnerCapture_Transaction");
  String partnerCapture_Description = rb1.getString("partnerCapture_Description");
  String partnerCapture_PayMode = rb1.getString("partnerCapture_PayMode");
  String partnerCapture_CardType = rb1.getString("partnerCapture_CardType");
  String partnerCapture_Amount = rb1.getString("partnerCapture_Amount");
  String partnerCapture_Currency = rb1.getString("partnerCapture_Currency");
  String partnerCapture_Status = rb1.getString("partnerCapture_Status");
  String partnerCapture_Terminal = rb1.getString("partnerCapture_Terminal");
  String partnerCapture_POD = rb1.getString("partnerCapture_POD");
  String partnerCapture_Tracking = rb1.getString("partnerCapture_Tracking");
  String partnerCapture_No = rb1.getString("partnerCapture_No");
  String partnerCapture_Shipment = rb1.getString("partnerCapture_Shipment");
  String partnerCapture_Action = rb1.getString("partnerCapture_Action");
  String partnerCapture_Please = rb1.getString("partnerCapture_Please");
  String partnerCapture_Capture = rb1.getString("partnerCapture_Capture");
  String partnerCapture_ShowingPage = rb1.getString("partnerCapture_ShowingPage");
  String partnerCapture_of = rb1.getString("partnerCapture_of");
  String partnerCapture_records = rb1.getString("partnerCapture_records");
  String partnerCapture_Sorry = rb1.getString("partnerCapture_Sorry");
  String partnerCapture_No_records_found = rb1.getString("partnerCapture_No_records_found");
  String partnerCapture_Filter = rb1.getString("partnerCapture_Filter");
  String partnerCapture_page_no = rb1.getString("partnerCapture_page_no");
  String partnerCapture_total_no_of_records = rb1.getString("partnerCapture_total_no_of_records");


  MerchantDAO merchantDAO = new MerchantDAO();
  //LinkedHashMap<Integer, String> memberMap = merchantDAO.getPartnerMembersDetail(partnerid);

%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="row">

        <div class="col-sm-12 portlets ui-sortable">

          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company  %> <%=partnerCapture_Merchant_Proof_Of_Delivery%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <form name="form" method="post" action="/partner/net/PartnerCapture?ctoken=<%=ctoken%>">
                <%
                String error=(String) request.getAttribute("error");
                if( error!=null)
                {
                  out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                }
              %>

              <div class="widget-content padding">
                <div id="horizontal-form">

                  <div class="form-group col-md-3 has-feedback">
                    <label><%=partnerCapture_Tracking_ID%></label>
                    <input type="text" name="trackingid" class="form-control" placeholder="Tracking ID" >
                  </div>

                  <div class="form-group col-md-3 has-feedback">
                    <div class="ui-widget">
                    <label ><%=partnerCapture_Partner_ID%></label>
                    <input name="pid" id="pid" maxlength="20" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                      <input name="pid" type="hidden" value="<%=pid%>">
                  </div>
                </div>

                  <div class="form-group col-md-3 has-feedback">
                    <div class="ui-widget">
                    <label ><%=partnerCapture_Merchant_ID%></label>
                    <input name="memberid" id="member" value="<%=memberID%>" class="form-control" autocomplete="on">
                  </div>
                </div>

                <div class="form-group col-md-3 has-feedback">
                  <label><%=partnerCapture_Auth_Code%></label>
                  <input type=text name="paymentid" class="form-control" maxlength="100"  placeholder="Auth code">
                </div>

                <div class="form-group col-md-3">
                  <label style="color: transparent;"><%=partnerCapture_Path%></label>
                  <button type="submit" name="B1" class="btn btn-default" style="display:block;">
                    <i class="fa fa-clock-o"></i>
                    &nbsp;&nbsp;<%=partnerCapture_Search%>
                  </button>
                </div>

              </div>
          </div>
          </form>
        </div>
      </div>
    </div>

    <form name="f1" method="post" action="/partner/net/PodSubmit">
      <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
      <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerCapture_Capture_Data%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-y: auto;">
              <%

                HashMap hash = (HashMap) request.getAttribute("poddetails");

                //out.println(hash);
                ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                str = "";
                HashMap temphash = null;
                pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
                pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
                str = str + "&SRecords=" + pagerecords;
                str = str + "&ctoken=" + ctoken;
                str = str + "&memberid=" + memberID;
                int records = 0;
                int totalrecords = 0;
                int currentblock = 1;

                try
                {
                  records = Integer.parseInt((String) hash.get("records"));
                  totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                  currentblock = Integer.parseInt(request.getParameter("currentblock"));
                }
                catch (Exception ex)
                {
                }

                String style = "class=tr1";
                String ext = "light";

                if (request.getAttribute("message") != null)
                {
                  out.println(ESAPI.encoder().encodeForHTML((String) request.getAttribute("message")));
                }
                if(!"partnerCapture".equals(request.getParameter("submit")))
                {
                  if (records > 0)
                  {
                    session.setAttribute("poddetails",hash);
              %>
              <table id="myTable" class="display table table-striped table-bordered"  style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead style="background-color: #7eccad !important;color: white;">
                <tr>
                  <td align="center" style="background-color: #7eccad !important;"><input type="checkbox"<%-- onclick="ToggleAll(this);"--%> name="alltrans"></td>
                  <td width="50%" style="text-align: center;background-color: #7eccad !important;"><%=partnerCapture_Date%></td>
                  <td style="text-align: center;background-color: #7eccad !important;"><%=partnerCapture_Transaction%></td>
                  <td style="text-align: center;background-color: #7eccad !important;"><%=partnerCapture_Description%></td>
                  <td style="text-align: center;background-color: #7eccad !important;"><%=partnerCapture_PayMode%></td>
                  <td style="text-align: center;background-color: #7eccad !important;"><%=partnerCapture_CardType%></td>
                  <td style="text-align: center;background-color: #7eccad !important;"><%=partnerCapture_Amount%></td>
                  <td style="text-align: center;background-color: #7eccad !important;"><%=partnerCapture_Currency%></td>
                  <td style="text-align: center;background-color: #7eccad !important;"><%=partnerCapture_Status%></td>
                  <td style="text-align: center;background-color: #7eccad !important;"><%=partnerCapture_Terminal%></td>
                  <td style="text-align: center;background-color: #7eccad !important;"><%=partnerCapture_POD%>&nbsp;<%=partnerCapture_Tracking%>&nbsp;<%=partnerCapture_No%></td>
                  <td style="text-align: center;background-color: #7eccad !important;"><%=partnerCapture_Shipment%></td>
                  <td colspan="2" style="text-align: center;background-color: #7eccad !important;"><%=partnerCapture_Action%></td>

                </tr>
                </thead>
                <%

                  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

                  for (int pos = 1; pos <= records; pos++)
                  {
                    String id = Integer.toString(pos);

                    int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                    style = "class=\"tr" + pos % 2 + "\"";
                    temphash = (HashMap) hash.get(id);

                    String date = Functions.convertDtstamptoDate((String) temphash.get("dtstamp"));
                    ctoken= request.getParameter("ctoken");
                    String productname = (String) temphash.get("productkey");
                    ctoken =((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                    out.println("<tr>");
                %>
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <input type="hidden" value="<%=memberID%>" name="memberid">
                <tbody>

                <%

                    String paymodeid = (String) temphash.get("paymodeid");
                    String cardtypeid = (String) temphash.get("cardtype");
                    out.println("<td "+style+" align=\"center\">&nbsp;<input type=\"checkbox\" name=\"trackingid\" value=\""+temphash.get("trackingid")+"\"></td>");
                    out.println("<td style=\"text-align: center\" data-label=\"Date\">&nbsp;" + date + "</td>");
                    out.println("<td style=\"text-align: center\" data-label=\"Transaction ID\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid")) + "</td>");
                    out.println("<td style=\"text-align: center\" data-label=\"Description\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("description")) + "</td>");


                    if(paymodeid==null || paymodeid.equals("") )
                    {
                      out.println("<td style=\"text-align: center\">&nbsp;" + "-" + "</td>");
                    }
                    else
                    {

                      out.println("<td style=\"text-align: center\" data-label=\"Pay Mode\" align=\"center\">&nbsp;" + paymodeid + "</td>");
                    }
                    if(cardtypeid==null || cardtypeid.equals("") )
                    {
                      out.println("<td style=\"text-align: center\">&nbsp;" + "-" + "</td>");
                    }
                    else
                    {
                      out.println("<td style=\"text-align: center\" data-label=\"Card Type\" align=\"center\">&nbsp;" + cardtypeid + "</td>");


                    }
                    Functions functions = new Functions();

                    String amount = (String) temphash.get("amount");
                    String currency = (String) temphash.get("currency");
                    if ("JPY".equalsIgnoreCase(currency))
                    {
                      amount = functions.printNumber(Locale.JAPAN, amount);
                    }
                    else if ("EUR".equalsIgnoreCase(currency))
                    {
                      amount = functions.printNumber(Locale.FRANCE, amount);
                    }
                    else if ("GBP".equalsIgnoreCase(currency))
                    {
                      amount = functions.printNumber(Locale.UK, amount);
                    }
                    else if ("USD".equalsIgnoreCase(currency))
                    {
                      amount = functions.printNumber(Locale.US, amount);
                    }
                    out.println("<td style=\"text-align: center\" data-label=\"Amount\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
                    out.println("<td " + style + " align=\"center\" data-label=\"Currency\">&nbsp;" + ESAPI.encoder().encodeForHTML(currency) + "</td>");
                    out.println("<td style=\"text-align: center\" data-label=\"Status\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("status")) + "</td>");
                    out.println("<td style=\"text-align: center\" data-label=\"Terminal\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("terminalid")) + "</td>");
                    out.println("<td style=\"text-align: center\" data-label=\"POD (Shipment Tracking No)*\" align=\"center\"><center><input type=text maxlength=\"25\" name=podNO_"+ ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+" value=\"\" class=\"form-control\" style=\"width: inherit;\"></center><input type=hidden name= accountid_"+ ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+" value=\""+(String) temphash.get("accountid")+"\"</td>");
                    out.println("<td style=\"text-align: center;\" data-label=\"Shipment Tracker Site*\" align=\"center\"><center><input type=text maxlength=\"40\" name=podSITE_"+ ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+" value=\"\" class=\"form-control\" style=\"width: inherit;\"></center></td>");

                    String accountId = (String) temphash.get("accountid");
                    /*String fromtype = GatewayAccountService.getGatewayAccount(accountId).getGateway();

                    if(temphash.get("status").equals("authsuccessful") || (temphash.get("status").equals("capturesuccess") && SBMPaymentGateway.GATEWAY_TYPE.equals(fromtype)))
                    {
                      out.println("<td " + style + " data-label=\"Action\" align=\"center\"><input type=button class=\"buttonpod1hide btn btn-default\" name=mybutton" + (String) temphash.get("trackingid") + " value=\"Cancel\" class=\"buttonpod1 btn btn-default\" onClick=\"cancelTrans('" + (String) temphash.get("trackingid") + "','"+ctoken+"','"+(String) temphash.get("accountid")+"','"+""+"','"+memberID+"');\" ></td>");
                    }
                    else
                    {
                      out.println("<td " + style + " data-label=\"Action\" align=\"center\"><input type=button class=\"buttonpod1hide btn btn-default\" name=mybutton" + (String) temphash.get("trackingid") + " value=\"Cancel\" class=\"buttonpod1 btn btn-default\" onClick=\"cancelTrans('" + (String) temphash.get("trackingid") + "','"+ctoken+"','"+(String) temphash.get("accountid")+"','"+""+"','"+memberID+"');\"  disabled></td>");
                    }
*/
                  String fromtype="";
                  String Status= (String) temphash.get("status");
                  fromtype= (String) temphash.get("fromtype");
                  String disabled="";

                    if (Status.equals("authsuccessful") || (Status.equals("capturesuccess") && SBMPaymentGateway.GATEWAY_TYPE.equals(fromtype) && functions.isValueNull(fromtype)))
                    {
                      disabled = "";
                    }
                    else
                    {
                      disabled = "disabled";
                    }

                    out.println("<td " + style + " data-label=\"Action\" align=\"center\"><input type=button class=\"buttonpod1hide btn btn-default\" name=mybutton" + (String) temphash.get("trackingid") + " value=\"Cancel\" class=\"buttonpod1 btn btn-default\" onClick=\"cancelTrans('" + (String) temphash.get("trackingid") + "','" + ctoken + "','" + (String) temphash.get("accountid") + "','" + "" + "','" + memberID + "');\" " + disabled + " ></td>");


                    if (EcorePaymentGateway.GATEWAY_TYPE.equals(fromtype) || CredoraxPaymentGateway.GATEWAY_TYPE.equals(fromtype) && functions.isValueNull(fromtype))
                    {
                      //out.println("<td " + style + ">&nbsp;<font face=\"arial,verdana,helvetica\"  size=\"1\" ><a href=\"#\" onclick=\"(alert('Partial capture is not supported on your account'))\"> <font class=\"textb\"> Partial Capture </font></a></font></td>");
                      out.println("<td style=\"text-align: center\" data-label=\"Action\" align=\"center\"><input type=button class=\"buttonpod2hide btn btn-default\" name=mybutton" + (String) temphash.get("trackingid") + " value=\"Partial Capture\"  disabled></td>");
                    }
                    else
                    {
                      //out.println("<td " + style + ">&nbsp;<font face=\"arial,verdana,helvetica\"  size=\"1\" ><a href=\"/merchant/servlet/Pod?bank="+ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accountid"))+"&partialCapture=true &trackingid=" + ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("trackingid")) + "&ctoken="+ctoken+"\"> <font class=\"textb\"> Partial Capture </font></a></font></td>");
                      if (temphash.get("status").equals("authsuccessful"))
                      {
                        String paymentidId = "";
                        if (null != request.getAttribute("paymentid"))
                          paymentidId = (String) request.getAttribute("paymentid");
                        //System.out.println("member in jsp----"+memberID);
                        out.println("<td style=\"text-align: center\" data-label=\"Action\" align=\"center\"><input type=button class=\"buttonpod2 btn btn-default\" name=mybutton" + (String) temphash.get("trackingid") + " value=\"Partial Capture\" onClick=\"partialCapture('" + (String) temphash.get("accountid") + "','" + (String) temphash.get("trackingid") + "','" + ctoken + "','" + paymentidId + "','" + memberID + "');\"></td>");
                        //out.println("<td " + style + " align=\"center\"><input type=button name=mybutton" + (String) temphash.get("trackingid") + " value=\"Cancel\" onClick=\"cancelTrans('" + (String) temphash.get("trackingid") + "','"+ctoken+"','"+(String) temphash.get("accountid")+"');\" ></td>");
                      }
                      else
                      {
                        out.println("<td style=\"text-align: center\" data-label=\"Action\" align=\"center\"><input type=button class=\"buttonpod2hide btn btn-default\" name=mybutton" + (String) temphash.get("trackingid") + " value=\"Partial Capture\"   disabled></td>");
                      }
                    }
                  }
                %>


                </tbody>
              </table>
              <table width="100%">
                <tr>
                  <td class="textb" align="center"><%=partnerCapture_Please%> </td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                </tr>
                <tr>
                  <td width="15%" align="center">
                    <button type="submit" class="btn btn-default" value="Capture AND/OR Submit Shipping Details" >
                      <span><i class="fa fa-clock-o"></i></span>
                      &nbsp;&nbsp;<%=partnerCapture_Capture%>
                    </button>
                  </td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>

                </tr>
              </table>
            </div>
          </div>
        </div>
      </div>
    </form>

      <% int TotalPageNo;
        if(totalrecords%pagerecords!=0)
        {
          TotalPageNo=totalrecords/pagerecords+1;
        }
        else
        {
          TotalPageNo=totalrecords/pagerecords;
        }
      %>
      <div id="showingid"><strong><%=partnerCapture_page_no%> <%=pageno%>  <%=partnerCapture_of%>  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
      <div id="showingid"><strong><%=partnerCapture_total_no_of_records%>   <%=totalrecords%> </strong></div>

    <jsp:include page="page.jsp" flush="true">
      <jsp:param name="numrecords" value="<%=totalrecords%>"/>
      <jsp:param name="numrows" value="<%=pagerecords%>"/>
      <jsp:param name="pageno" value="<%=pageno%>"/>
      <jsp:param name="str" value="<%=str%>"/>
      <jsp:param name="page" value="PartnerCapture"/>
      <jsp:param name="currentblock" value="<%=currentblock%>"/>
      <jsp:param name="orderby" value=""/>
    </jsp:include>
    <%
        }
        else
        {
          out.println(Functions.NewShowConfirmation1(partnerCapture_Sorry, partnerCapture_No_records_found));
        }
      }
      else if("partnerCapture".equals(request.getParameter("submit")))
      {
        out.println(Functions.NewShowConfirmation1(partnerCapture_Filter, partnerCapture_No_records_found));
      }
    %>

  </div>
</div>
</div>
<script language="javascript">
  $(document).ready(function () {
    $('input:checkbox[name=alltrans]').parent().children().eq(1).click(function () {

      if($('input:checkbox[name=alltrans]').parent().hasClass("checked"))
      {
        var checkboxes = document.getElementsByName("trackingid");
        var total_boxes = checkboxes.length;

        for(i=0; i<total_boxes; i++ )
        {
          checkboxes[i].checked =true;
        }
        $('input:checkbox[name=trackingid]').parent().addClass("checked");
      }
      else
      {
        var checkboxes = document.getElementsByName("trackingid");
        var total_boxes = checkboxes.length;

        for(i=0; i<total_boxes; i++ )
        {
          checkboxes[i].checked =false;
        }
        $('input:checkbox[name=trackingid]').parent().removeClass("checked");
      }
    });
  });
  </script>
</body>
</html>
<%!
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>