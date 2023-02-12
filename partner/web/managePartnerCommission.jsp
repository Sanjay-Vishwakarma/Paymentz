<%@ include file="top.jsp" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.Database,com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%!
  Logger logger=new Logger("managePartnerCommission.jsp");
%>
<%
  String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
  session.setAttribute("submit", "PartnerReports");
  Functions functions = new Functions();
  PartnerFunctions partnerFunctions=new PartnerFunctions();
%>
<head>
  <title><%=company%> | Merchant Charges</title>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>

  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <script type="text/javascript">
    $('#sandbox-container input').datepicker({});
  </script>
  <script>
    $(function ()
    {
      $(".datepicker").datepicker({dateFormat: "yy-mm-dd", endDate: '+10y'});
    });
  </script>
  <style type="text/css">
    #main {
      background-color: #ffffff
    }

    :target:before {
      content: "";
      display: block;
      height: 50px;
      margin: -50px 0 0;
    }

    .table > thead > tr > th {
      font-weight: inherit;
    }

    :target:before {
      content: "";
      display: block;
      height: 90px;
      margin: -50px 0 0;
    }

    footer {
      border-top: none;
      margin-top: 0;
      padding: 0;
    }

    /********************Table Responsive Start**************************/
    @media (max-width: 640px) {
      table {
        border: 0;
      }

      table thead {
        display: none;
      }

      tr:nth-child(odd), tr:nth-child(even) {
        background: #ffffff;
      }

      table td {
        display: block;
        border-bottom: none;
        padding-left: 0;
        padding-right: 0;
      }

      table td:before {
        content: attr(data-label);
        float: left;
        width: 100%;
        font-weight: bold;
      }
    }

    table {
      width: 100%;
      max-width: 100%;
      border-collapse: collapse;
      margin-bottom: 20px;
      display: table;
      border-collapse: separate;
      border-color: grey;
    }

    thead {
      display: table-header-group;
      vertical-align: middle;
      border-color: inherit;
    }

    tr:nth-child(odd) {
      background: #F9F9F9;
    }

    tr {
      display: table-row;
      vertical-align: inherit;
      border-color: inherit;
    }

    th {
      padding-right: 1em;
      text-align: left;
      font-weight: bold;
    }

    td, th {
      display: table-cell;
      vertical-align: inherit;
    }

    tbody {
      display: table-row-group;
      vertical-align: middle;
      border-color: inherit;
    }

    td {
      padding-top: 6px;
      padding-bottom: 6px;
      padding-left: 10px;
      padding-right: 10px;
      vertical-align: top;
      border-bottom: none;
    }

    .table > thead > tr > th, .table > tbody > tr > th, .table > tfoot > tr > th, .table > thead > tr > td, .table > tbody > tr > td, .table > tfoot > tr > td {
      border-top: 1px solid #ddd;
    }

    /********************Table Responsive Ends**************************/
    @media (min-width: 768px) {
      .form-horizontal .control-label {
        text-align: left !important;
      }
    }
  </style>
</head>
<body>
<%
  ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    String partid = (String) session.getAttribute("merchantid");
    String partnerId=Functions.checkStringNull(request.getParameter("partnerId"))==null?"":request.getParameter("partnerId");;
    String memberid=Functions.checkStringNull(request.getParameter("memberid"))==null?"":request.getParameter("memberid");
    String terminalId=Functions.checkStringNull(request.getParameter("terminalid"))==null?"":request.getParameter("terminalid");
    String sequencenum=Functions.checkStringNull(request.getParameter("sequencenum"))==null?"":request.getParameter("sequencenum");
    String commissionvalue=Functions.checkStringNull(request.getParameter("commissionvalue"))==null?"":request.getParameter("commissionvalue");
    String commissionon=Functions.checkStringNull(request.getParameter("commissionon"))==null?"":request.getParameter("commissionon");
    String startDate=Functions.checkStringNull(request.getParameter("startDate"))==null?"":request.getParameter("startDate");
    String endDate=Functions.checkStringNull(request.getParameter("endDate"))==null?"":request.getParameter("endDate");
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String managePartnerCommission_Add_Partner_Commission = StringUtils.isNotEmpty(rb1.getString("managePartnerCommission_Add_Partner_Commission")) ? rb1.getString("managePartnerCommission_Add_Partner_Commission") : "Add Partner Commission";
    String managePartnerCommission_PartnerId = StringUtils.isNotEmpty(rb1.getString("managePartnerCommission_PartnerId")) ? rb1.getString("managePartnerCommission_PartnerId") : "Partner Id* :";
    String managePartnerCommission_MemberId = StringUtils.isNotEmpty(rb1.getString("managePartnerCommission_MemberId")) ? rb1.getString("managePartnerCommission_MemberId") : "Member Id* :";
    String managePartnerCommission_TerminalId = StringUtils.isNotEmpty(rb1.getString("managePartnerCommission_TerminalId")) ? rb1.getString("managePartnerCommission_TerminalId") : "Terminal Id* :";
    String managePartnerCommission_Commission_Name = StringUtils.isNotEmpty(rb1.getString("managePartnerCommission_Commission_Name")) ? rb1.getString("managePartnerCommission_Commission_Name") : "Commission Name* :";
    String managePartnerCommission_Commission_Value = StringUtils.isNotEmpty(rb1.getString("managePartnerCommission_Commission_Value")) ? rb1.getString("managePartnerCommission_Commission_Value") : "Commission Value* :";
    String managePartnerCommission_Start_Date = StringUtils.isNotEmpty(rb1.getString("managePartnerCommission_Start_Date")) ? rb1.getString("managePartnerCommission_Start_Date") : "Start Date* :";
    String managePartnerCommission_End_Date = StringUtils.isNotEmpty(rb1.getString("managePartnerCommission_End_Date")) ? rb1.getString("managePartnerCommission_End_Date") : "End Date* :";
    String managePartnerCommission_Sequence_Number = StringUtils.isNotEmpty(rb1.getString("managePartnerCommission_Sequence_Number")) ? rb1.getString("managePartnerCommission_Sequence_Number") : "Sequence Number* :";
    String managePartnerCommission_Button = StringUtils.isNotEmpty(rb1.getString("managePartnerCommission_Button")) ? rb1.getString("managePartnerCommission_Button") : "Button";
    String managePartnerCommission_SAVE = StringUtils.isNotEmpty(rb1.getString("managePartnerCommission_SAVE")) ? rb1.getString("managePartnerCommission_SAVE") : "SAVE";

%>
<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/listPartnerCommission.jsp?ctoken=<%=ctoken%>" method="POST">
            <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/partner/images/goBack.png">
            </button>
          </form>
        </div>
      </div>
      <br><br><br>

      <div class="col-md-12 portlets ui-sortable">
        <div class="widget">
          <div class="widget-header transparent">
            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=managePartnerCommission_Add_Partner_Commission%></strong></h2>

            <div class="additional-btn">
              <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
              <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
            </div>
          </div>
          <br>

          <div class="widget-content padding">
            <div id="horizontal-form">
              <%
                String errormessage = (String) request.getAttribute("statusMsg");
                if (functions.isValueNull(errormessage))
                {
                  out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormessage + "</h5>");
                }
              %>
              <div class="widget-content padding" style="overflow-y: auto;">
                <form action="/partner/net/ManagePartnerCommission?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <input type="hidden" value="<%=partid%>" name="superAdminId" id="partnerid">
                  <div class="form-group">
                    <div class="col-md-2"></div>
                    <label class="col-md-4 control-label"><%=managePartnerCommission_PartnerId%></label>
                    <div class="col-md-4">
                      <input name="partnerId" id="PID" value="<%=partnerId%>" class="form-control" autocomplete="on">
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-2"></div>
                    <label class="col-md-4 control-label"><%=managePartnerCommission_MemberId%></label>
                    <div class="col-md-4">
                      <input name="memberid" id="MID" value="<%=memberid%>" class="form-control" autocomplete="on">
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-2"></div>
                    <label class="col-md-4 control-label"><%=managePartnerCommission_TerminalId%></label>
                    <div class="col-md-4">
                      <input type="text" size="30" class="form-control" name="terminalid" id="terminalALL" value="<%=terminalId%>">
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-2"></div>
                    <label class="col-md-4 control-label"><%=managePartnerCommission_Commission_Name%></label>
                    <div class="col-md-4">
                      <select name="commissionon" class="form-control"><option value="" selected></option>
                        <%
                          Connection conn = null;
                          PreparedStatement pstmt = null;
                          ResultSet rs = null;
                          try
                          {
                            conn = Database.getConnection();
                            String query = "Select chargeid, chargename from charge_master";
                            pstmt = conn.prepareStatement(query);
                            rs = pstmt.executeQuery();
                            String selected="";
                            while (rs.next())
                            {
                              if(commissionon.equals(rs.getString("chargeid")))
                                selected="selected";
                              else
                                selected="";
                              out.println("<option value=\"" + rs.getString("chargeid") + "\""+selected+">" + rs.getString("chargename") + "</option>");
                            }
                          }
                          catch (Exception e)
                          {
                               logger.error("Exception---" + e);
                          }
                          finally
                          {
                            Database.closeConnection(conn);
                          }
                        %>
                      </select>
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-2"></div>
                    <label class="col-md-4 control-label"><%=managePartnerCommission_Commission_Value%></label>
                    <div class="col-md-4">
                      <input maxlength="15" class="form-control" type="text" name="commissionvalue"  value="<%=commissionvalue%>">
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-2"></div>
                    <label class="col-md-4 control-label"><%=managePartnerCommission_Start_Date%></label>
                    <div class="col-md-4">
                      <input class="datepicker form-control" type="text" name="startDate"  value="<%=startDate%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;" >
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-2"></div>
                    <label class="col-md-4 control-label"><%=managePartnerCommission_End_Date%></label>
                    <div class="col-md-4">
                      <input class="datepicker form-control" type="text" name="endDate"   value="<%=endDate%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;">
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-2"></div>
                    <label class="col-md-4 control-label"><%=managePartnerCommission_Sequence_Number%></label>
                    <div class="col-md-4">
                      <input type="text" size="30" class="form-control" name="sequencenum" value="<%=sequencenum%>">
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-3 control-label" style="visibility: hidden;"><%=managePartnerCommission_Button%></label>
                    <div class="col-md-4">
                      <input type="hidden" value="1" name="step">
                      <button type="submit" class="btn btn-default" id="submit" name="action"
                              value="Save" style="display: -webkit-box;"><i
                              class="fa fa-sign-in"></i>&nbsp;&nbsp;<%=managePartnerCommission_SAVE%>
                      </button>
                    </div>
                    <div class="col-md-6"></div>
                  </div>
                </form>
              </div>
              <%
                }
                else
                {
                  response.sendRedirect("/partner/Logout.jsp");
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
<%!
  public String getPreviousDate(String source)
  {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date2 = null;
    try
    {
      date2 = sdf.parse(source);
    }
    catch (ParseException e)
    {
      logger.error("Parse Exception while  getting  PreviousDate",e);

    }
    Date dateBefore = new Date(date2.getTime() + 1 * 24 * 3600 * 1000);
    String sDate2 = sdf.format(dateBefore);
    return sDate2;
  }
%>