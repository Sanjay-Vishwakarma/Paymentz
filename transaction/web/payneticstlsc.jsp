<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericTransDetailsVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%
    Functions functions = new Functions();
    CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
    MerchantDetailsVO merchantDetailsVO = standardKitValidatorVO.getMerchantDetailsVO();
    //GenericTransDetailsVO genericTransDetailsVO = standardKitValidatorVO.getTransDetailsVO();
    String id = "", bd = "";
    if (merchantDetailsVO != null && functions.isValueNull(merchantDetailsVO.getMemberId()))
        id = merchantDetailsVO.getMemberId();
    /*if (genericTransDetailsVO != null && functions.isValueNull(genericTransDetailsVO.getBillingDiscriptor()))
        bd = genericTransDetailsVO.getBillingDiscriptor();*/
%>
<form name="flgxfrm_659">
    <input type="hidden" name="id" value="<%=id%>">
    <input type="hidden" name="bd" value="<%=bd%>">
    <input type="hidden" name="uniqueID" value="">
    <input type="hidden" name="other1" value="">
    <input type="hidden" name="other2" value="">
</form>
<!--lporirxe//-->
<div id="flpx_659_957" style="display:none;"></div>
<noscript>
    <div style="display:none"><img
            src="http://flx659.lporirxe.com/flp/mngpix.php?qid=235393f5735393f5935363&id=<AffiliateID>&sid=<SubID>&uid=<Transaction/Uniqiue ID>&oid=<Offer ID>"/>
    </div>
</noscript>
<script type="text/javascript" language="javascript">
    var _flbtn = "";
    var _fltrf = 1;
    (function ()
    {
        var tci = "flx659";
        var flcachebuster = Math.round((new Date()).getTime() / 1000);
        document.write(unescape("%3Cscript src='" + ((document.location.protocol == 'https:') ?
        'https://' + tci + '.lporirxe.com' : 'http://' + tci + '.lporirxe.com') + "/" + "flp" + "/" + "ncvp.js?"
        + 'c=659' + '&i=' + flcachebuster + "' type='text/javascript' charset='ISO-8859-1'%3E%3C/script%3E"));
    })();
</script>
<script>
    (function ()
    {
        var objcnt = 0;
        var flto = setInterval(function ()
        {
            objcnt++;
            if (typeof FLPXobj == 'object')
            {
                clearInterval(flto);
                Initflf();
            }
            if (objcnt > 4)
            {
                clearInterval(flto);
            }
        }, 100);

        function Initflf()
        {
            FLPXobj.procinit(['659', '957']);
            var myto = setInterval(function ()
            {
                clearInterval(myto);
                var status = FLPXobj.pxstatus;
            }, 500);
        }
    })();
</script>