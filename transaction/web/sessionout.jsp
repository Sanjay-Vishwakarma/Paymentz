<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO" %>
<%@ page import="com.payment.exceptionHandler.errorcode.ErrorCodeUtils" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.*" %>
<%@ page import="com.manager.vo.TransactionDetailsVO" %>
<%@ page import="payment.*" %>
<%@ page import="com.directi.pg.AsyncNotificationService" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericTransDetailsVO" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericCardDetailsVO" %>
<%@ page import="com.manager.PaymentManager" %>
<%--
<%@ include file="ietest.jsp" %>
--%>
<%
    session.invalidate();
    ESAPI.authenticator().logout();
%>
<html>
<head>
    <title>Session Expired</title>

    <%
        Functions functions=new Functions();
        Logger log = new Logger("sessionout.jsp");
        log.debug("------------redirect url-------" + request.getParameter("redirectUrl"));

        String redirect = "";
        String trackingid = "";
        String desc = "";
        String amount = "";
        String status = "";
        String clkey = "";
        String language = "";
        String currency = "";
        String notificationUrl = "";
        String[] childTrackingid=null;
        String marketPlaceFlag="";

        if(functions.isValueNull(request.getParameter("redirectUrl")))
        {
            redirect = request.getParameter("redirectUrl");
            trackingid = request.getParameter("trackingid");
            desc = request.getParameter("desc");
            amount = request.getParameter("amount");
            status = request.getParameter("status");
            clkey = request.getParameter("clkey");
            language = request.getParameter("language");
            currency = request.getParameter("currency");
            notificationUrl = request.getParameter("notificationUrl");
            childTrackingid=request.getParameterValues("marketPlaceTrackingid[]");
            marketPlaceFlag=request.getParameter("marketPlaceFlag");
        }

        ResourceBundle rb1 = null;
        String lang = "";
        String multiLanguage = "";

        if(functions.isValueNull(language))
        {
            lang = language.toLowerCase();
            if ("ja".equalsIgnoreCase(lang))
            {
                rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutSessionout_ja");
            }
            else
            {
                rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutSessionout_en-us");
            }
        }
        else if(functions.isValueNull(request.getHeader("Accept-Language")))
        {
            multiLanguage = request.getHeader("Accept-Language");
            String sLanguage[] = multiLanguage.split(",");
            if(functions.isValueNull(sLanguage[0]))
            {
                if ("ja".equalsIgnoreCase(sLanguage[0]))
                {
                    rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutSessionout_ja");
                }
                else
                {
                    rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutSessionout_en-us");
                }
            }
            else
            {
                rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutSessionout_en-us");
            }
        }
        else
        {
            rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutSessionout_en-us");
        }

        String varSessionExpired = rb1.getString("VAR_SESSION_EXPIRED");
        String varBack=rb1.getString("VAR_BACK");
        String varLine1=rb1.getString("VAR_LINE_1");
        String varLine2=rb1.getString("VAR_LINE_2");
        String varLine3=rb1.getString("VAR_LINE_3");
        String varLine4=rb1.getString("VAR_LINE_4");
    %>

    <style>
        .panelheading_color
        {
        <%=request.getParameter("panelheading_color")!=null?"background-color:"+request.getParameter("panelheading_color").toString()+"!important":""%>;
        }
        .headpanelfont_color
        {
        <%=request.getParameter("headpanelfont_color")!=null?"color:"+request.getParameter("headpanelfont_color").toString()+"!important":""%>;
        }
        .bodypanelfont_color
        {
        <%=request.getParameter("bodypanelfont_color")!=null?"color:"+request.getParameter("bodypanelfont_color").toString()+"!important":""%>;
        }
        .panelbody_color
        {
        <%=request.getParameter("panelbody_color")!=null?"background-color:"+request.getParameter("panelbody_color").toString()+"!important":""%>;
        }
        .mainbackgroundcolor
        {
        <%=request.getParameter("mainbackgroundcolor")!=null?"background:"+request.getParameter("mainbackgroundcolor").toString()+"!important":""%>;
        }
    </style>

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap.min.css">
    <link rel="stylesheet" href="/merchant/transactionCSS/css/main.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>
</head>
<body class="bodybackground mainbackgroundcolor" >


<div class="container-fluid ">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">
            <div class="logo form foreground bodypanelfont_color panelbody_color" >
                <div class="logo form foreground bodypanelfont_color panelbody_color">
                    <center><%=varSessionExpired%></center>
                    <center> <%=varLine1%> <br><br> <%=varLine2%> <br/> <%=varLine3%><%=varLine4%> </center>
                </div>
                <%
                    AuditTrailVO auditTrailVO = new AuditTrailVO();
                    CommonValidatorVO commonValidatorVO = new CommonValidatorVO();

                    GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();
                    MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
                    GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
                    GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();

                    commonValidatorVO.setTrackingid(trackingid);
                    transDetailsVO.setOrderId(desc);
                    transDetailsVO.setAmount(amount);
                    transDetailsVO.setCurrency(currency);
                    transDetailsVO.setNotificationUrl(notificationUrl);

                    merchantDetailsVO.setKey(clkey);

                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setTransDetailsVO(transDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);

                    PaymentManager paymentManager = new PaymentManager();
                    if(!"split".equalsIgnoreCase(marketPlaceFlag))
                        paymentManager.updateFailedSessionoutTransaction(commonValidatorVO,trackingid,auditTrailVO);
                    if(childTrackingid != null && !"N".equalsIgnoreCase(marketPlaceFlag)){
                        for(int i=0;i<childTrackingid.length;i++)
                        {
                            paymentManager.updateFailedSessionoutTransaction(commonValidatorVO, childTrackingid[i], auditTrailVO);
                        }
                    }

                    if(functions.isValueNull(notificationUrl))
                    {
                        TransactionUtility transactionUtility = new TransactionUtility();

                        TransactionDetailsVO transactionDetailsVO1=transactionUtility.getTransactionDetails(commonValidatorVO);
                        payment.AsyncNotificationService asyncNotificationService = payment.AsyncNotificationService.getInstance();
                        asyncNotificationService.sendNotification(transactionDetailsVO1,trackingid,"failed","Payment session timed out","");
                    }

                    if(functions.isValueNull(request.getParameter("redirectUrl")))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
                        String checkSum = Checksum.generateChecksumForStandardKit(trackingid, desc, amount, status, clkey);
                        errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.TRANSACTION_REJECTED);
                %>

                <form action="<%=redirect%>" method="post">
                    <input type="hidden" name="trackingid" value="<%=trackingid%>">
                    <input type="hidden" name="paymentId" value="<%=trackingid%>">
                    <input type="hidden" name="desc" value="<%=desc%>">
                    <input type="hidden" name="merchantTransactionId" value="<%=desc%>">
                    <input type="hidden" name="amount" value="<%=amount%>">
                    <input type="hidden" name="currency" value="<%=currency%>">
                    <input type="hidden" name="status" value="N">
                    <input type="hidden" name="checksum" value="<%=checkSum%>">
                    <input type="hidden" name="resultCode" value="<%=errorCodeVO.getApiCode()%>">
                    <input type="hidden" name="resultDescription" value="<%=errorCodeVO.getApiDescription()%>">
                    <button type="submit" class="btn btn-default"><%=varBack%></button>
                </form>

                <%
                    }
                %>
            </div>
        </div>
    </div>
</div>
</body>
</html>