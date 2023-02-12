package com.directi.pg.core.cup;

import com.directi.pg.Database;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;



/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: Aug 20, 2012
 * Time: 8:38:40 PM
 * To change this template use File | Settings | File Templates.
 * Copyright: Admin
 *
 */
public class CupUtils
{
    public final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.CupServlet");

    // version
    public final static String version = RB.getString("version");

    // character encoding
    public final static String charset = RB.getString("charset");

    public final static String acqCode = RB.getString("acqCode");

    public final static String transTimeout = RB.getString("transTimeout");

    // Base website addresses (Please switch by your requirement)

    public final static String testPayUrl = RB.getString("testPayUrl");
    public final static String testBSPayUrl = RB.getString("testBSPayUrl");
    public final static String testQueryUrl = RB.getString("testQueryUrl");

    public final static String livePayUrl = RB.getString("livePayUrl");
    public final static String liveBSPayUrl = RB.getString("liveBSPayUrl");
    public final static String liveQueryUrl = RB.getString("liveQueryUrl");

    public final static String merFrontEndUrl = RB.getString("merFrontEndUrl");
    public final static String merBackEndUrl = RB.getString("merBackEndUrl");

    // encryption
    public final static String signType_MD5 = "MD5";
    public final static String signType_SHA1withRSA = "SHA1withRSA";

    // Store security key, necessarily same as the configuration in UnionPay merchants website
    public final static String testSecurityKey = RB.getString("testSecurityKey");
    public final static String liveSecurityKey = RB.getString("liveSecurityKey");
    // signature
    public final static String signature = "signature";
    public final static String signMethod = "signMethod";
    //Compose purchase request package
    public final static String[] reqVo = new String[]{
            "version",
            "charset",
            "transType",
            "origQid",
            "merId",
            "merAbbr",
            "acqCode",
            "merCode",
            "commodityUrl",
            "commodityName",
            "commodityUnitPrice",
            "commodityQuantity",
            "commodityDiscount",
            "transferFee",
            "orderNumber",
            "orderAmount",
            "orderCurrency",
            "orderTime",
            "customerIp",
            "customerName",
            "defaultPayType",
            "defaultBankNumber",
            "transTimeout",
            "frontEndUrl",
            "backEndUrl",
            "merReserved"
    };
    public final static String[] notifyVo = new String[]{
            "charset",
            "cupReserved",
            "exchangeDate",
            "exchangeRate",
            "merAbbr",
            "merId",
            "orderAmount",
            "orderCurrency",
            "orderNumber",
            "qid",
            "respCode",
            "respMsg",
            "respTime",
            "settleAmount",
            "settleCurrency",
            "settleDate",
            "traceNumber",
            "traceTime",
            "transType",
            "version"
    };
    public final static String[] queryVo = new String[]{
            "version",
            "charset",
            "transType",
            "merId",
            "orderNumber",
            "orderTime",
            "merReserved"
    };
    private static Logger log = new Logger(CupUtils.class.getName());

    public static String getBeijingTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sdf.format(new Date());
    }

    public static String createPayHtml(String[] valueVo, String signType, String gateWay, String securityKey) {
        Map<String, String> map = new TreeMap<String, String>();
        for (int i = 0; i < reqVo.length; i++) {
            map.put(reqVo[i], valueVo[i]);
        }

        map.put("signature", signMap(map, signType, securityKey));
        map.put("signMethod", signType);
        String payForm = generateAutoSubmitForm(gateWay, map);

        return payForm;
    }

    public static String createPayHtmlinNewTab(String[] valueVo, String signType, String gateWay, String securityKey) {
        Map<String, String> map = new TreeMap<String, String>();
        for (int i = 0; i < reqVo.length; i++) {
            map.put(reqVo[i], valueVo[i]);
        }

        map.put("signature", signMap(map, signType, securityKey));
        map.put("signMethod", signType);
        String payForm = generateAutoSubmitForminNewTab(gateWay, map);

        return payForm;
    }

    private static String signMap(Map<String, String> map, String signMethod, String securityKey) {
        if (signType_MD5.equalsIgnoreCase(signMethod)) {
            String strBeforeMd5 = joinMapValue(map, '&') + md5(securityKey);
            //System.out.println(strBeforeMd5);
            return md5(strBeforeMd5);
        } else {
            return signWithRSA(md5(joinMapValue(map, '&') + md5(securityKey)));
        }
    }

    private static String md5(String str) {

        if (str == null) {
            return null;
        }

        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance(signType_MD5);
            messageDigest.reset();
            messageDigest.update(str.getBytes(charset));
        } catch (NoSuchAlgorithmException e) {

            return str;
        } catch (UnsupportedEncodingException e) {
            return str;
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }

        return md5StrBuff.toString();
    }

    private static String signWithRSA(String signData) {
        String privateKeyPath = "D:/work/Test/data/upop_private.key";
        ObjectInputStream priObjectIs = null;
        try {
            priObjectIs = new ObjectInputStream(new FileInputStream(privateKeyPath));
            PrivateKey privateKey = PrivateKey.class.cast(priObjectIs.readObject());
            Signature dsa = Signature.getInstance(signType_SHA1withRSA);
            dsa.initSign(privateKey);
            dsa.update(signData.getBytes(charset));
            byte[] signature = dsa.sign();
            return DatatypeConverter.printBase64Binary(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (priObjectIs != null) {
                try {
                    priObjectIs.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static String convertDollarToCents (String dollar){
        int amount = (int)(Double.parseDouble(dollar)*100);
        return String.valueOf(amount);
    }

    private static String generateAutoSubmitForm(String actionUrl, Map<String, String> paramMap) {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(actionUrl).append("\" method=\"post\" >\n");

        for (String key : paramMap.keySet()) {
            html.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + paramMap.get(key) + "\">\n");
        }
        html.append("</form>\n");
        return html.toString();
    }

    private static String generateAutoSubmitForminNewTab(String actionUrl, Map<String, String> paramMap) {
        StringBuilder html = new StringBuilder();
        html.append("<form target=\"_blank\" id=\"pay_form\" name=\"pay_form\" action=\"").append(actionUrl).append("\" method=\"post\" >\n");

        for (String key : paramMap.keySet()) {
            html.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + paramMap.get(key) + "\">\n");
        }
        //html.append("<input type=\"submit\" value=\"Pay Now\" onclick=\"this.disabled = true\"> \n");
        html.append("</form>\n");
        return html.toString();
    }

    private static String joinMapValue(Map<String, String> map, char connector) {
        StringBuffer b = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            b.append(entry.getKey());
            b.append('=');
            if (entry.getValue() != null) {
                b.append(entry.getValue());
            }
            b.append(connector);
        }
        return b.toString();
    }

    public static String createBackStrForBackTrans(String[] valueVo, String[] keyVo, String securityKey)
    {
        Map map = new TreeMap();
        for (int i = 0; i < keyVo.length; i++) {
            map.put(keyVo[i], valueVo[i]);
        }
        map.put("signature", signMap(map, "MD5", securityKey));
        map.put("signMethod", "MD5");
        return joinMapValueBySpecial(map, '&');
    }

    private static String joinMapValueBySpecial(Map<String, String> map, char connector) {
        StringBuffer b = new StringBuffer();
        for (Map.Entry entry : map.entrySet())
        {
            b.append((String)entry.getKey());
            b.append('=');
            if (entry.getValue() != null) {
                try {
                    b.append(URLEncoder.encode((String) entry.getValue(), "UTF-8"));
                }
                catch (UnsupportedEncodingException e) {
                    log.error("UnsupportedEncodingException--->",e);
                }
            }
            b.append(connector);
        }
        return b.toString();
    }

    public static String doPostQueryCmd(String strURL, String req)
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            URL url = new URL(strURL);
            URLConnection con = url.openConnection();

            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            out = new BufferedOutputStream(con.getOutputStream());
            byte[] outBuf = req.getBytes("UTF-8");
            out.write(outBuf);
            out.close();
            in = new BufferedInputStream(con.getInputStream());
            result = ReadByteStream(in);
        } catch (Exception ex) {
            //System.out.print(ex);
            return "";
        } finally {
            if (out != null)
                try {
                    out.close();
                }
                catch (IOException localIOException2) {
                }
            if (in != null)
                try {
                    in.close();
                }
                catch (IOException localIOException3) {
                }
        }
        if (result == null) {
            return "";
        }
        return result;
    }

    public static String ReadByteStream(BufferedInputStream in) throws IOException {
        LinkedList<Mybuf> bufList = new LinkedList<Mybuf>();
        int size = 0;
        byte buf[];
        do {
            buf = new byte[128];
            int num = in.read(buf);
            if (num == -1)
                break;
            size += num;
            bufList.add(new Mybuf(buf, num));
        } while (true);
        buf = new byte[size];
        int pos = 0;
        for (ListIterator<Mybuf> p = bufList.listIterator(); p.hasNext();) {
            Mybuf b = p.next();
            for (int i = 0; i < b.size;) {
                buf[pos] = b.buf[i];
                i++;
                pos++;
            }

        }

        return new String(buf, charset);
    }

    public static Map createResultMap(String result){
        Map<String, String> map = new HashMap<String, String>();
        StringTokenizer tok = new StringTokenizer(result, "&", false);
        String[] params = result.split("&");
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken();
            String[] keyValue = token.split("=");
            if (keyValue.length==2){
                map.put(keyValue[0],keyValue[1]);
            }
        }
        return map;
    }

    public HashMap getAccountDetails(String accountid,String currency) throws PZDBViolationException
    {
        Connection connection=null;
        HashMap accountDetail=new HashMap();
        try
        {
            connection= Database.getConnection();
            String sql = "select merchantid, displayname, merchantcategorycode, istestaccount from gateway_accounts ga, gateway_accounts_cup gac where ga.accountid=? and gac.accountid=?";
            PreparedStatement p=connection.prepareStatement(sql);

            p.setString(1,accountid);
            p.setString(2,accountid);
            ResultSet rs=p.executeQuery();
            if(rs.next())
            {
                accountDetail.put("merchantid",rs.getString("merchantid"));
                accountDetail.put("displayname",rs.getString("displayname"));
                accountDetail.put("merchantcategorycode",rs.getString("merchantcategorycode"));
                accountDetail.put("istestaccount",rs.getString("istestaccount"));
            }

            String sql1="select currencycode from currency_code where currency = ?";
            p=connection.prepareStatement(sql1);
            p.setString(1,currency);
            ResultSet resultSet=p.executeQuery();
            if(resultSet.next())
            {
                accountDetail.put("currencycode",resultSet.getString("currencycode"));
            }
        }
        catch (SystemError systemError)
        {
            log.error("system Error while collect cup account Detail",systemError);
            PZExceptionHandler.raiseDBViolationException("CupUtils.java","getAccountDetails()",null,"Common","SQL Exception Thrown:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Sql error while collect cup account Detail",e);
            PZExceptionHandler.raiseDBViolationException("CupUtils.java","getAccountDetails()",null,"Common","SQL Exception Thrown:::::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }
        finally {
            Database.closeConnection(connection);
        }
        return accountDetail;
    }

    public String getCupRequest(CommonValidatorVO commonValidatorVO,int detailId) throws PZDBViolationException
    {
        String gatewayPayUrl;
        String securityKey;
        String transType="";
        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        if ("Y".equals(commonValidatorVO.getMerchantDetailsVO().getService()))  {
            transType = "01";
        }
        else
        {
            transType = "02";
        }
        CupUtils cupUtils = new CupUtils();
        HashMap accountDetail = cupUtils.getAccountDetails(commonValidatorVO.getMerchantDetailsVO().getAccountId(), account.getCurrency());

        if (accountDetail.get("istestaccount").equals("N"))
        {
            /* Production environment */
            gatewayPayUrl = CupUtils.livePayUrl;
            securityKey = CupUtils.liveSecurityKey;
        }
        else
        {
            /* Test environment */
            gatewayPayUrl = CupUtils.testPayUrl;
            securityKey = CupUtils.testSecurityKey;
        }

        String[] valueVo = new String[]{
                CupUtils.version,//Protocol version
                CupUtils.charset,//Character Encoding
                transType,//Transaction type
                "",//The original transaction serial number
                (String) accountDetail.get("merchantid"),//Merchant code
                (String) accountDetail.get("displayname"),//Merchant short name
                CupUtils.acqCode,//Acquirer code (Only need fill when acquirer access in)
                (String) accountDetail.get("merchantcategorycode"),//Merchant category (Acquirer need fill when access in)
                "",//Product URL
                "",//Product name
                "",//Product unit price, unit: Fen
                "",//Product quantity
                "",//Discount, unit: Fen
                "",//Shipping fee, unit: Fen
                String.valueOf(detailId),//Order Number (Requires merchants to generate)
                CupUtils.convertDollarToCents(commonValidatorVO.getTransDetailsVO().getAmount()),//Amount of the transaction, unit: Fen
                (String) accountDetail.get("currencycode"),//Transaction currency
                CupUtils.getBeijingTime(),//Transaction time
                commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),//User IP
                "",//User real name
                "",//Default payment
                "",//Default bank code
                CupUtils.transTimeout,//Transaction timeout
                RB.getString("merFrontEndUrl"),//Frontend callback merchant URL
                CupUtils.merBackEndUrl,//Backend callback URL
                ""//Merchant reserved fields{orderTimeoutDate=20140912172500}
        };

        String signType = null;
        if (!CupUtils.signType_SHA1withRSA.equalsIgnoreCase(RB.getString("signMethod")))
        {
            signType = CupUtils.signType_MD5;
        }
        else
        {
            signType = CupUtils.signType_SHA1withRSA;
        }

        String html = CupUtils.createPayHtml(valueVo, signType, gatewayPayUrl, securityKey);//redirect to UnionPay website for payment

        return html;
    }


}
class Mybuf {

    public byte buf[];
    public int size;

    public Mybuf(byte b[], int s) {
        buf = b;
        size = s;
    }
}
