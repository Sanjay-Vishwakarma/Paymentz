package com.payment.paysec;

import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.payforasia.core.PayforasiaUtils;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Created by Admin on 19/12/2015.
 */
public class PaySecPaymentGateway extends AbstractPaymentGateway
{
    //private static Logger log=new Logger(PaySecPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PaySecPaymentGateway.class.getName());

    public static final String GATEWAY_TYPE="paysec";

    private static final String URL="https://merchant.PaySec.com/API/cpCheckCompany.asp";
    private static final String REFUND="https://merchant.PaySec.com/api/refund.asp";
    private static final String STATUS="https://merchant.PaySec.com/api/getpaymentstatus.asp";
    private static final String CALLBACKURL="";

    private static final ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.PaySecServlet");

    public PaySecPaymentGateway(String accountid)
    {
        this.accountId = accountid;
    }

    public static final String CURRENCY = "v_currency";
    public static final String AMOUNT = "v_amount";
    public static final String FIRSTNAME = "v_firstname";
    public static final String LASTNAME = "v_lastname";
    public static final String EMAIL = "v_billemail";
    public static final String STATE = "v_shipstate";
    public static final String POST = "v_shippost";

    public static final String BSTREET = "v_billstreet";
    public static final String BCITY = "v_billcity";
    public static final String BSTATE = "v_billstate";
    public static final String BPOST = "v_billpost";
    public static final String BCOUNTRY = "v_billcountry";
    public static final String BPHONE = "v_billphone";
    public static final String MERCHANTID = "cid";
    public static final String CARTID = "v_CartID";
    public static final String SIGNATURE = "signature";

    public static final String BCALLBACKURL = "callBackURL";

    public static final String TESTURL = "https://merchant.PaySec.com/API/cpCheckCompany.asp";

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public GenericResponseVO processAuthentication(String trackingId,GenericRequestVO genericRequestVO)
    {
        return null;
    }

    public GenericResponseVO processSale(String trackingId,GenericRequestVO genericRequestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) genericRequestVO;

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();

        PaySecUtils paySecUtils = new PaySecUtils();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        //GatewayAccount signaturekey = GatewayAccountService.getGatewayAccount(account.getFRAUD_FTP_PATH());


        String merchantId = account.getMerchantId();
        //String merchantKey = merchantId;
        String signaturekey = account.getPassword();
        CommResponseVO commResponseVO = new CommResponseVO();
        int amount =paySecUtils.getAmount(genericTransDetailsVO.getAmount());

        Map authmap = new TreeMap();

        try
        {
            authmap.put(CURRENCY, account.getCurrency());
            authmap.put(AMOUNT, genericTransDetailsVO.getAmount());
            authmap.put(FIRSTNAME, genericAddressDetailsVO.getFirstname());
            authmap.put(LASTNAME, genericAddressDetailsVO.getLastname());
            authmap.put(EMAIL, genericAddressDetailsVO.getEmail());
            authmap.put(STATE, genericAddressDetailsVO.getState());
            authmap.put(POST, genericAddressDetailsVO.getZipCode());

            authmap.put(BSTREET, genericAddressDetailsVO.getStreet());
            authmap.put(BCITY, genericAddressDetailsVO.getCity());
            authmap.put(BSTATE, genericAddressDetailsVO.getState());
            authmap.put(BPOST, genericAddressDetailsVO.getZipCode());
            authmap.put(BCOUNTRY, genericAddressDetailsVO.getCountry());
            authmap.put(BPHONE, genericAddressDetailsVO.getPhone());
            authmap.put(MERCHANTID, merchantId);
            authmap.put(CARTID, trackingId);
            authmap.put(SIGNATURE,paySecUtils.generateMD5ChecksumDirectKit(signaturekey.toUpperCase(), merchantId.toUpperCase(), trackingId, amount, account.getCurrency().toUpperCase()));
            authmap.put(BCALLBACKURL, "https://staging.pz.com/transaction/PaysecBackendNotification");

            String strRequest = PayforasiaUtils.joinMapValue(authmap, '&');
            transactionLogger.error("-------sale request-------" + strRequest);
            String response = PayforasiaUtils.doPostHTTPSURLConnection(TESTURL, strRequest);
            transactionLogger.error("-------sale response-------" + response);
        }
        catch (NoSuchAlgorithmException e)
        {
         transactionLogger.error("NoSuchAlgorithmException======"+e);
        }

        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }


    public static void main(String[] args)
    {
        String url = "";

        String currency = "CNY";
        String amount = "125.00";
        String firstname = "Swamy";
        String lastname ="Rapolu";
        String email = "swamyrapolu1@gmail.com";
        String state = "Maharashtra";
        String post = "50000";

        String bstreet = "No.1 Stuart!Road";
        String city = "Kuala Lumpur";
        String bstate = "Wilayah Persekutuan";
        String bpost = "50000";
        String country = "Malaysia";
        String phone = "60388887777";
        String merchantid= "M104-C-196";
        String cartid = "0029471JKWN823";
        String merchantkey = "Ir8Ve0Ev3";
        String callbackurl = "https://merchant.chopstickpay.com/samples/callbackURL.asp";

        String signature = "merchant_key="+merchantkey+"&merchant_id"+merchantid+"&cart_id"+cartid+"&amount"+amount+"&currency"+currency;
        String chksum = "";

        PaySecUtils paySecUtils = new PaySecUtils();
        try
        {
            chksum =paySecUtils.generateMD5ChecksumDirectKit("Ir8Ve0Ev3".toUpperCase(), "M106-C-470", "0029471JKWN831", 14500, "CNY");
        }
        catch (Exception e)
        {

        }



        transactionLogger.debug("chk sum "+chksum);
    }



}
