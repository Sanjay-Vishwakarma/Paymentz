<%@ page import="com.payment.payforasia.core.PayforasiaAccount" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.UUID" %><%
    String uniqueId = UUID.randomUUID().toString();
    String accountId = request.getParameter("accountId");

    PayforasiaAccount payforasiaAccount = new PayforasiaAccount();
    Hashtable dataHash = payforasiaAccount.getValuesFromDb(accountId);
    String gatewayNo = (String) dataHash.get("gatewayno");
    String merchantNo = (String) dataHash.get("mid");

%>
<script type="text/javascript" src="https://pay.wonderlandpay.com/pub/js/fb/tag.js?merNo=<%=merchantNo%>&gatewayNo=<%=gatewayNo%>&uniqueId=<%=uniqueId%>"> </script>
<script>
    window.addEventListener('load', function() {
        Fingerprint2.get(function(components) {
            var murmur = Fingerprint2.x64hash128(components.map(function(pair) {
                return pair.value}).join(), 31) ;
            document.getElementById("deviceNo").value = murmur;
            document.getElementById("uniqueId").value = '<%=uniqueId%>';
        })
    });
</script>
