<%@ page import="com.directi.pg.core.jpbanktransfer.JPBankTransferVO" %>
<%@ page import="com.payment.common.core.CommResponseVO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericTransDetailsVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>

<%@ page import="com.directi.pg.*" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericTransDetailsVO" %>
<%@ page import="com.payment.exceptionHandler.errorcode.ErrorCodeUtils" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>

<%

    JPBankTransferVO jpBankTransferVO = (JPBankTransferVO) request.getAttribute("transRespDetails");
    String ctoken= (String) request.getAttribute("ctoken");
    String bankName = jpBankTransferVO.getBankName();
    String shitenName = jpBankTransferVO.getShitenName();
    String kouzaType = jpBankTransferVO.getKouzaType();
    String kouzaMeigi = jpBankTransferVO.getKouzaMeigi();
    String company = jpBankTransferVO.getCompany();
    String kouzaNm = jpBankTransferVO.getKouzaNm();
    String result = jpBankTransferVO.getResult();
    String shitenNm = jpBankTransferVO.getShitenNm();
    String bid = jpBankTransferVO.getBid();
    String tel = jpBankTransferVO.getTel();
    String email = jpBankTransferVO.getEmail();
    String nameId = jpBankTransferVO.getNameId();

    out.println("<tr>");
    out.println("<td>ctoken --->"+ctoken+"\n"+"</td>");
    out.println("<td>Request URI--->"+request.getRequestURI()+"\n"+"</td>");
    out.println("<td>bankName---->"+bankName+"\n"+"</td>");
    out.println("<td>shitenName---->"+shitenName+"\n"+"</td>");
    out.println("<td>kouzaType---->" + kouzaType+"\n"+"</td>");
    out.println("<td>kouzaMeigi---->"+kouzaMeigi+"</td>");
    out.println("<td>company---->"+company+"</td>");
    out.println("<td>result---->"+result+"</td>");
    out.println("<td>kouzaNm---->"+kouzaNm+"</td>");
    out.println("<td>shitenNm---->"+shitenNm+"</td>");
    out.println("<td>bid---->"+bid+"</td>");
    out.println("<td>tel---->"+tel+"</td>");
    out.println("<td>email---->"+email+"</td>");
    out.println("<td>nameId---->"+nameId+"</td>");
    out.println("</tr>");
%>

<html>
<body>
<h1> Hello </h1>
</body>
</html>
