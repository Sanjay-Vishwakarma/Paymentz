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
  Logger logger=new Logger("merchantMapping.jsp");
  Functions functions= new Functions();
%>
<%
  String partnerid=String.valueOf(session.getAttribute("partnerId"));
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","merchantMapping");
%>
<html>
<head>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

  <title><%=company%> | Merchant Agent Mapping</title>

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
    String agentId=Functions.checkStringNull(request.getParameter("agentid"))==null?"":request.getParameter("agentid");
    String memberId=Functions.checkStringNull(request.getParameter("memberid"))==null?"":request.getParameter("memberid");
  String pid=nulltoStr(request.getParameter("pid"));
  String Config=null;
  String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
  if(Roles.contains("superpartner")){

  }else{
    pid = String.valueOf(session.getAttribute("merchantid"));
    Config = "disabled";
  }
    String str="";
    str = str + "&ctoken=" + ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    str = str + "&SRecords=" + pagerecords;
%>

<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/merchantAgentMapping.jsp?ctoken=<%=ctoken%>" method="post" name="form">
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Merchant Agent Mapping</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <br>
            <form action="/partner/net/MerchantMapping?ctoken=<%=ctoken%>" method="post" name="addbankaccount" class="form-horizontal">
              <input type="hidden" value="<%=ctoken%>" id="ctoken" name="ctoken">
              <input type="hidden" value="<%=partnerid%>" id="partnerid" name="partnerid">

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
                  <label class="col-md-3 control-label">Partner Id*</label>
                  <div class="col-md-4">
                    <input type="text"  name="pid"  id="pid" value="<%=pid%>" class="form-control" autocomplete="on">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Agent Id*</label>
                  <div class="col-md-4">
                    <input type="text"  name="agentid"  id="agnt" value="<%=agentId%>" class="form-control" autocomplete="on">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label">Member Id*</label>
                  <div class="col-md-4">
                   <input type="text"  name="memberid" id="member" value="<%=memberId%>" class="form-control" autocomplete="on">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label" style="visibility:hidden;">Button</label>
                  <div class="col-md-1">
                    <button type="submit" class="buttonform btn btn-default" name="action" value="save">
                      <i class="fa fa-save"></i>&nbsp;
                      Save
                    </button>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
<%!
  public static String nulltoStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>