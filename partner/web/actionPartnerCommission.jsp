<%@ include file="top.jsp" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.manager.vo.PartnerCommissionVO" %>
<%@ page import="com.manager.vo.payoutVOs.ChargeMasterVO" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
  session.setAttribute("submit", "PartnerReports");
  Functions functions = new Functions();
  PartnerFunctions partnerFunctions=new PartnerFunctions();
%>
<head>
  <title><%=company%> | Merchant Charges</title>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

  <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
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
    PartnerCommissionVO partnerCommissionVO=(PartnerCommissionVO)request.getAttribute("partnerCommissionVO");
    String partid = (String) session.getAttribute("merchantid");
    String partnerId=Functions.checkStringNull(request.getParameter("partnerId"))==null?"":request.getParameter("partnerId");;
    String memberid=Functions.checkStringNull(request.getParameter("memberid"))==null?"":request.getParameter("memberid");
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

      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Partner Commission</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <div id="horizontal-form">
                <form action="/partner/net/ListPartnerCommission?ctoken=<%=ctoken%>" method="post" name="forms">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <%
                    if(request.getAttribute("error")!=null)
                    {
                      String message = (String) request.getAttribute("error");
                      if(message != null)
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                    }
                    if(request.getAttribute("success")!=null)
                    {
                      String success = (String) request.getAttribute("success");
                      if(success != null)
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + success + "</h5>");
                    }
                  %>

                  <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                  <div class="ui-widget form-group col-md-4 has-feedback">
                    <label>Partner ID</label>
                    <input name="partnerId" id="PID" value="<%=partnerId%>" class="form-control" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label>Merchant ID</label>
                    <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4">
                    <label style="color: transparent;">Path</label>
                    <button type="submit" class="btn btn-default" style="display:block;">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Edit Partner Commission</strong></h2>

              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <br>
            <%
              String errormessage = (String) request.getAttribute("statusMsg");
              if (functions.isValueNull(errormessage))
              {
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormessage + "</h5>");
              }
              if (partnerCommissionVO != null)
              {
                ChargeMasterVO chargeMasterVO=partnerCommissionVO.getChargeMasterVO();
                System.out.println("ChargeValue:::"+partnerCommissionVO.getCommissionValue());
            %>
            <div class="widget-content padding" style="overflow-y: auto;">
              <form action="/partner/net/ActionPartnerCommission?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
                <input type="hidden" name="commissionid" value="<%=partnerCommissionVO.getCommissionId()%>">
                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label">Commission Id* :</label>
                  <div class="col-md-4">
                    <input name="commissionId" value="<%=partnerCommissionVO.getCommissionId()%>" class="form-control" autocomplete="on" disabled>
                  </div>
                  <div class="col-md-6"></div>
                </div>
                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label">Partner Id* :</label>
                  <div class="col-md-4">
                    <input name="partnerId" id="PID" value="<%=partnerCommissionVO.getPartnerId()%>" class="form-control" autocomplete="on" disabled>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label">Member Id* :</label>
                  <div class="col-md-4">
                    <input name="memberid" id="member" value="<%=partnerCommissionVO.getMemberId()%>" class="form-control" autocomplete="on" disabled>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label">Terminal Id :</label>
                  <div class="col-md-4">
                    <input type="text" size="30" class="form-control" name="terminalid" id="terminalALL" value="<%=partnerCommissionVO.getTerminalId()%>" disabled>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label">Commission Name* :</label>
                  <div class="col-md-4">
                    <input type="text" size="30" class="form-control" name="chargename" value="<%=chargeMasterVO.getChargeName()%>" disabled>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label">Commission Value* :</label>
                  <div class="col-md-4">
                    <input maxlength="15" class="form-control" type="text" name="commissionvalue"  value="<%=Functions.round(partnerCommissionVO.getCommissionValue(),2)%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label">Start Date* :</label>
                  <div class="col-md-4">
                    <input class="datepicker form-control" type="text" name="startDate"  value="<%=partnerCommissionVO.getStartDate()%>"disabled>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label">End Date* :</label>
                  <div class="col-md-4">
                    <input class="datepicker form-control" type="text" name="startDate"  value="<%=partnerCommissionVO.getStartDate()%>" disabled>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label">Sequence Number* :</label>
                  <div class="col-md-4">
                    <input type="text" size="30" class="form-control" name="sequencenum" value="<%=partnerCommissionVO.getSequenceNo()%>" disabled>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label" style="visibility: hidden;">Button</label>
                  <div class="col-md-4">
                    <input type="hidden" value="1" name="step">
                    <button type="submit" class="btn btn-default" id="submit" name="action"
                            value="Update" style="display: -webkit-box;"><i
                            class="fa fa-sign-in"></i>&nbsp;&nbsp;UPDATE
                    </button>
                  </div>
                  <div class="col-md-6"></div>
                </div>
              </form>
            </div>
            <%
                }
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
      //logger.error("Parse Exception while  getting  PreviousDate",e);
    }
    Date dateBefore = new Date(date2.getTime() + 1 * 24 * 3600 * 1000);
    String sDate2 = sdf.format(dateBefore);
    return sDate2;
  }
%>