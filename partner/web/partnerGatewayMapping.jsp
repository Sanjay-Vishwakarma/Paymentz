<%@ page import="com.directi.pg.Database,com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ include file="top.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  Logger logger=new Logger("manageGatewayAccount.jsp");
  Functions functions= new Functions();
%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","gatewayAccountInterface");
%>
<html>
<head>
  <title><%=company%> | Partner Gateway Mapping</title>
  <script src="/merchant/javascript/hidde.js"></script>
  <style type="text/css">
    @media(max-width: 991px) {
      .additional-btn {
        float: left;
        margin-left: 30px;
        margin-top: 10px;
        position: inherit!important;
      }
    }

    @media (min-width: 768px){
      .form-horizontal .control-label {
        text-align: left!important;
      }
    }


    /*    .textb{color: red!important;}*/

  </style>
  <style type="text/css">
    .field-icon
    {
      float:  right;
      margin-top: -25px;
      position: relative;
      z-index: 2;
    }


  </style>

</head>
<body>
<%
  Functions functions = new Functions();
  PartnerFunctions partnerFunctions=new PartnerFunctions();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    String partnerId = (String) session.getAttribute("merchantid");
    String pgtypeId="";
    if (request.getParameter("pgtypeid") != null && request.getParameter("pgtypeid").length() > 0)
      pgtypeId = request.getParameter("pgtypeid");
    String reqPartnerId = Functions.checkStringNull(request.getParameter("partnerId")) == null ? "" : request.getParameter("partnerId");

    TreeMap<String, String> subPartnersDetails = partnerFunctions.getPartnerDetailsForMap(partnerId);
    String str="";
    str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    str = str + "&SRecords=" + pagerecords;

    String daily_card_limit="1";
    String weekly_card_limit="3";
    String monthly_card_limit="5";

    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String partnerGatewayMapping_Partner_Gateway = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_Partner_Gateway")) ? rb1.getString("partnerGatewayMapping_Partner_Gateway") : "Partner Gateway Allocation";
    String partnerGatewayMapping_PgType = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_PgType")) ? rb1.getString("partnerGatewayMapping_PgType") : "PgType Id* :";
    String partnerGatewayMapping_select_PgType = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_select_PgType")) ? rb1.getString("partnerGatewayMapping_select_PgType") : "Select PgType Id";
    String partnerGatewayMapping_Partner_Id = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_Partner_Id")) ? rb1.getString("partnerGatewayMapping_Partner_Id") : "Partner Id* :";
    String partnerGatewayMapping_select_partner = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_select_partner")) ? rb1.getString("partnerGatewayMapping_select_partner") : "Select Partner Id";
    String partnerGatewayMapping_daily_card = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_daily_card")) ? rb1.getString("partnerGatewayMapping_daily_card") : "Daily Card Limit* :";
    String partnerGatewayMapping_weekly_card = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_weekly_card")) ? rb1.getString("partnerGatewayMapping_weekly_card") : "Weekly Card Limit* :";
    String partnerGatewayMapping_monthly_card = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_monthly_card")) ? rb1.getString("partnerGatewayMapping_monthly_card") : "Monthly Card Limit* :";
    String partnerGatewayMapping_Button = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_Button")) ? rb1.getString("partnerGatewayMapping_Button") : "Button";
    String partnerGatewayMapping_Save = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_Save")) ? rb1.getString("partnerGatewayMapping_Save") : "Save";
    String partnerGatewayMapping_Search = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_Search")) ? rb1.getString("partnerGatewayMapping_Search") : "Search";
    String partnerGatewayMapping_report = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_report")) ? rb1.getString("partnerGatewayMapping_report") : "Report Table";
    String partnerGatewayMapping_SrNo = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_SrNo")) ? rb1.getString("partnerGatewayMapping_SrNo") : "Sr No";
    String partnerGatewayMapping_PgTypeId = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_PgTypeId")) ? rb1.getString("partnerGatewayMapping_PgTypeId") : "PgType Id";
    String partnerGatewayMapping_Gateway = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_Gateway")) ? rb1.getString("partnerGatewayMapping_Gateway") : "Gateway Name";
    String partnerGatewayMapping_currency = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_currency")) ? rb1.getString("partnerGatewayMapping_currency") : "Currency";
    String partnerGatewayMapping_PartnerId = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_PartnerId")) ? rb1.getString("partnerGatewayMapping_PartnerId") : "Partner Id";
    String partnerGatewayMapping_Action = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_Action")) ? rb1.getString("partnerGatewayMapping_Action") : "Action";
    String partnerGatewayMapping_delete = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_delete")) ? rb1.getString("partnerGatewayMapping_delete") : "Delete";
    String partnerGatewayMapping_Showing = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_Showing")) ? rb1.getString("partnerGatewayMapping_Showing") : "Showing Page";
    String partnerGatewayMapping_of = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_of")) ? rb1.getString("partnerGatewayMapping_of") : "of";
    String partnerGatewayMapping_records = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_records")) ? rb1.getString("partnerGatewayMapping_records") : "records";
    String partnerGatewayMapping_Sorry = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_Sorry")) ? rb1.getString("partnerGatewayMapping_Sorry") : "Sorry";
    String partnerGatewayMapping_no = StringUtils.isNotEmpty(rb1.getString("partnerGatewayMapping_no")) ? rb1.getString("partnerGatewayMapping_no") : "No records found.";
%>

<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/gatewayAccountInterface.jsp?ctoken=<%=ctoken%>" method="post" name="form">
            <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/partner/images/goBack.png">
            </button>
          </form>
        </div>
      </div>
      <br><br><br>

      <div class="row">
        <div class="col-md-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerGatewayMapping_Partner_Gateway%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <br>
            <form action="/partner/net/PartnerGatewayMapping?ctoken=<%=ctoken%>" method="post" name="addbankaccount" class="form-horizontal">
              <input type="hidden" value="<%=ctoken%>" name="ctoken">
              <div class="widget-content padding">
                <%
                  String msg = (String) request.getAttribute("statusMsg");
                  if(functions.isValueNull(msg))
                  {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + msg + "</h5>");
                  }
                %>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=partnerGatewayMapping_PgType%></label>
                  <div class="col-md-4">
                    <select id="pgtypeid" name="pgtypeid" class="form-control"> <option value="" default><%=partnerGatewayMapping_select_PgType%></option>
                      <%
                        Connection con = null;
                        try
                        {
                          con = Database.getConnection();
                          StringBuffer qry = new StringBuffer("SELECT gtype.pgtypeid,gtype.gateway,gtype.name,gtype.currency,gtpm.partnerid FROM gateway_type AS gtype JOIN gatewaytype_partner_mapping AS gtpm ON gtype.pgtypeid=gtpm.pgtypeid AND gtpm.partnerid=?");
                          PreparedStatement pstmt = con.prepareStatement(qry.toString());
                          pstmt.setString(1, partnerId);
                          ResultSet rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            String selected = "";
                            if (rs.getString("pgtypeid").equals(pgtypeId))
                            {
                              selected = "selected";
                            }
                            out.println("<option value=" + rs.getString("pgtypeid") + " " + selected + ">" + rs.getString("name") + "</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception::::" + e);
                        }
                        finally
                        {
                          Database.closeConnection(con);
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=partnerGatewayMapping_Partner_Id%></label>
                  <div class="col-md-4">
                    <select name="partnerId" class="form-control">
                      <option value=""><%=partnerGatewayMapping_select_partner%></option>
                      <%
                        for(String pid : subPartnersDetails.keySet())
                        {
                          String isSelected = "";
                          if (pid.equals(reqPartnerId))
                          {
                            isSelected = "selected";
                          }
                      %>
                      <option value="<%=pid%>" <%=isSelected%>><%=subPartnersDetails.get(pid)%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=partnerGatewayMapping_daily_card%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" value="<%=daily_card_limit%>" name="daily_card_limit" id="daily_card_limit" size="30">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=partnerGatewayMapping_weekly_card%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" value="<%=weekly_card_limit%>" name="weekly_card_limit" id="weekly_card_limit" size="30">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label"><%=partnerGatewayMapping_monthly_card%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" value="<%=monthly_card_limit%>" name="monthly_card_limit" id="monthly_card_limit" size="30">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label" style="visibility:hidden;"><%=partnerGatewayMapping_Button%></label>
                  <div class="col-md-1">
                    <button type="submit" class="buttonform btn btn-default" name="action" value="save">
                      <i class="fa fa-save"></i>&nbsp;
                      <%=partnerGatewayMapping_Save%>
                    </button>
                  </div>
                  <div class="col-md-5">
                    <button type="submit" class="buttonform btn btn-default">
                      <i class="fa fa-save"></i>&nbsp;
                      <%=partnerGatewayMapping_Search%>
                    </button>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>

      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerGatewayMapping_report%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-x: auto;">

              <%  StringBuffer requestParameter = new StringBuffer();
                Enumeration<String> parameterNames = request.getParameterNames();
                while(parameterNames.hasMoreElements())
                {
                  String name=parameterNames.nextElement();
                  if("SPageno".equals(name) || "SRecords".equals(name))
                  {

                  }
                  else
                    requestParameter.append("<input type='hidden' name='"+name+"' value=\""+request.getParameter(name)+"\"/>");
                }
                requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");


                // Hashtable hash = (Hashtable)request.getAttribute("transdetails");
                HashMap hash = (HashMap)request.getAttribute("transdetails");

                HashMap temphash=null;
                int records=0;
                int totalrecords=0;

                String errormsg=(String)request.getAttribute("message");
                String currentblock=request.getParameter("currentblock");
                if(currentblock==null)
                  currentblock="1";
                try
                {
                  records=Integer.parseInt((String)hash.get("records"));
                  totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
                }
                catch(Exception ex)
                {

                }
                if(hash!=null)
                {
                  hash = (HashMap)request.getAttribute("transdetails");
                }
                if(records>0)
                {
              %>
              <table class="table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerGatewayMapping_SrNo%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerGatewayMapping_PgTypeId%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerGatewayMapping_Gateway%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerGatewayMapping_currency%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerGatewayMapping_PartnerId%></b></td>
                  <td  colspan="2" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerGatewayMapping_Action%></b></td>
                </tr>
                </thead>

                <%
                  String style="class=td1";
                  String ext="light";
                  for(int pos=1;pos<=records;pos++)
                  {
                    String id = Integer.toString(pos);
                    style = "class=\"tr" + (pos + 1) % 2 + "\"";
                    int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);
                    temphash=(HashMap)hash.get(id);
                    out.println("<tr id=\"maindata\">");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"Sr No\" align=\"center\">&nbsp;"+srno+ "</td>");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"Account Id\" align=\"center\">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("pgtypeid"))+"<input type=\"hidden\" name=\"pgtypeid\" value=\""+temphash.get("pgtypeid")+"\"></td>");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"Merchant Id\" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("gateway"))+"<input type=\"hidden\" name=\"gateway\" value=\""+temphash.get("gateway")+"\"></td>");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"Currency\" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("currency"))+"<input type=\"hidden\" name=\"currency\" value=\""+temphash.get("currency")+"\"></td>");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"Bank Name\" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("partnerid"))+"<input type=\"hidden\" name=\"partnerid\" value=\""+temphash.get("partnerid")+"\"></td>");
                    out.println("<td "+style+" valign=\"middle\" data-label=\"Action\" align=\"center\"><form action=\"/partner/net/PartnerGatewayMapping?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"pgtypeid\" value=\"" + ESAPI.encoder().encodeForHTML((String) temphash.get("pgtypeid"))+"\"><input type=\"hidden\" name=\"partnerId\" value=\"" + ESAPI.encoder().encodeForHTML((String) temphash.get("partnerid"))+"\"><input type=\"submit\" name=\"submit\" class=\"gotoauto btn btn-default\" value="+partnerGatewayMapping_delete+"><input type=\"hidden\" name=\"action\" value=\"Delete\"></form></td>");
                    out.println("</tr>");
                  }
                %>
              </table>
              <div id="showingid"><strong><%=partnerGatewayMapping_Showing%> <%=pageno%> <%=partnerGatewayMapping_of%> <%=totalrecords%> <%=partnerGatewayMapping_records%></strong></div>
              <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="PartnerGatewayMapping"/>
                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                <jsp:param name="orderby" value=""/>
              </jsp:include>
              <%
                }
                else
                {
                  out.println(Functions.NewShowConfirmation1(partnerGatewayMapping_Sorry, partnerGatewayMapping_no));
                }
              %>
              <%
                }
                else
                {
                  response.sendRedirect("/partner/logout.jsp");
                  return;
                }
              %>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>