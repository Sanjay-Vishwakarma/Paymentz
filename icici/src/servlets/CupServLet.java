
import com.directi.pg.*;
import com.directi.pg.core.cup.CupUtils;
import com.directi.pg.core.valueObjects.CupResponseVO;
import org.owasp.esapi.ESAPI;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

/**
 * Name: Payment request class
 * Function: payment request class in servlet mode
 * Class Attributes:
 * Version: 1.0
 * Date: 2011-03-11
 * Author: China UnionPay UPOP Team
 * Copyright: China UnionPay
 * Explanatory Notes: The following code is just sample code provided for the merchant test. The merchant can program according to the needs of your own sites and the technical documentation. It isn't necessary to use the following code. The code is for reference only.
 * */

public class CupServLet extends HttpServlet {
    static Logger log = new Logger(CupServLet.class.getName());
    Connection con = null;
    private String gatewayPayUrl;
    private String securityKey;
    /**
     *
     */
    private static final long serialVersionUID = -6247574514957274823L;
    static int i= 0;
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.CupServlet");

    public void init() {

    }

    public void initializeGateway(String testAccount){
        if(testAccount.equals("N")){
            /* Production environment */
            gatewayPayUrl = CupUtils.livePayUrl;
            securityKey = CupUtils.liveSecurityKey;
        }
        else{
            /* Test environment */
            gatewayPayUrl = CupUtils.testPayUrl;
            securityKey = CupUtils.testSecurityKey;
        }
    }
    public void service(HttpServletRequest request, HttpServletResponse response) {

        String description = ESAPI.encoder().encodeForHTML(request.getParameter("DESCRIPTION"));
        String amount = ESAPI.encoder().encodeForHTML(request.getParameter("TXN_AMT"));
        //String frontEndUrl = request.getParameter("REDIRECTURL");
        String frontEndUrl = RB.getString("merFrontEndUrl");
        String trackingId = ESAPI.encoder().encodeForHTML(request.getParameter("TRACKING_ID"));
        String ipAddress = request.getRemoteAddr();
        String currency = ESAPI.encoder().encodeForHTML(request.getParameter("CURRENCY"));
        String memberId = ESAPI.encoder().encodeForHTML(request.getParameter("TOID"));
        String timeNow = CupUtils.getBeijingTime();
        String currencyCode = null;
        String isService = null;
        String transType = null;
        String merchantId = null;
        String merchantName = null;
        String mcc = null;
        String testAccount = null;
        int detailId = 0;
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        //Fetch currency code from currency
        try{
        con= Database.getConnection();
        String sql = "select c.currencycode, m.isservice from currency_code c, members m where c.currency = ? and  m.memberid =?";
        PreparedStatement p=con.prepareStatement(sql);

        p.setString(1,currency);
        p.setString(2,memberId);
        ResultSet rs=p.executeQuery();
        if(rs.next())
        {
            currencyCode= rs.getString("currencycode");
            isService = rs.getString("isservice");

        }
            if ("Y".equals(isService))  {
                transType = "01";
            }
            else{
                transType = "02";
            }
            String accountId = ESAPI.encoder().encodeForHTML(request.getParameter("ACCOUNTID"));
            sql = "select merchantid, displayname, merchantcategorycode, istestaccount from gateway_accounts ga, gateway_accounts_cup gac where ga.accountid=? and gac.accountid=?";
            p=con.prepareStatement(sql);

            p.setString(1,accountId);
            p.setString(2,accountId);
            rs=p.executeQuery();
            if(rs.next())
            {
                merchantId= rs.getString("merchantid");
                merchantName = rs.getString("displayname");
                mcc = rs.getString("merchantcategorycode");
                testAccount = rs.getString("istestaccount");

            }
            String qry="insert into bin_details (icicitransid, accountid) values(?,?)";
            PreparedStatement pstmt=con.prepareStatement(qry);
            pstmt.setString(1,trackingId);
            pstmt.setString(2,accountId);
            pstmt.executeUpdate();
            auditTrailVO.setActionExecutorId(memberId);
            auditTrailVO.setActionExecutorName("Admin");
        log.debug("calling Action Entry start");
        ActionEntry entry = new ActionEntry();
        CupResponseVO responseVo = new CupResponseVO();
        responseVo.setTransType(transType);
        responseVo.setProcessingTime(timeNow);
        detailId = entry.actionEntryForCUP(trackingId,amount.toString(),ActionEntry.ACTION_AUTHORISTION_STARTED,ActionEntry.STATUS_AUTHORISTION_STARTED,responseVo,null,auditTrailVO,null);
        entry.closeConnection();
        log.debug("calling Action Entry end ");
        //Database.commit(con);
        }
        catch(Exception e){
            log.error("Exception occur while insert data", e);
            Database.closeConnection(con);
        }
        initializeGateway(testAccount);
        String[] valueVo = new String[]{
                CupUtils.version,//Protocol version
                CupUtils.charset,//Character Encoding
                transType,//Transaction type
                "",//The original transaction serial number
                merchantId,//Merchant code
                merchantName,//Merchant short name
                CupUtils.acqCode,//Acquirer code (Only need fill when acquirer access in)
                mcc,//Merchant category (Acquirer need fill when access in)
                "",//Product URL
                "",//Product name
                "",//Product unit price, unit: Fen
                "",//Product quantity
                "",//Discount, unit: Fen
                "",//Shipping fee, unit: Fen
                String.valueOf(detailId),//Order Number (Requires merchants to generate)
                CupUtils.convertDollarToCents(amount),//Amount of the transaction, unit: Fen
                currencyCode,//Transaction currency
                timeNow,//Transaction time
                ipAddress,//User IP
                "",//User real name
                "",//Default payment
                "",//Default bank code
                CupUtils.transTimeout,//Transaction timeout
                frontEndUrl,//Frontend callback merchant URL
                CupUtils.merBackEndUrl,//Backend callback URL
                ""//Merchant reserved fields
        };

        String signType =null;
        if (!CupUtils.signType_SHA1withRSA.equalsIgnoreCase(RB.getString("signMethod"))) {
            signType = CupUtils.signType_MD5;
        }
        else{
            signType =CupUtils.signType_SHA1withRSA;
        }

        String html = CupUtils.createPayHtml(valueVo, signType, gatewayPayUrl, securityKey);//redirect to UnionPay website for payment

        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding(CupUtils.charset);
        try {
            response.getWriter().write(html);
        } catch (IOException e) {

        }
        response.setStatus(HttpServletResponse.SC_OK);
    }


}
