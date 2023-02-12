package com.payment.fraudAPI;

import com.directi.pg.Logger;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 5/10/14
 * Time: 8:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATRiskAPI
{
    private static Logger log = new Logger(ATRiskAPI.class.getName());
    private final static String AT_URL_customerLogin = "http://api-int.acuitytec.com/customerlogin";
    private final static String AT_URL_binCheck = "http://www.acuitytec.com//gdb/bincheck";
    private final static String AT_URL_customerLogout = "http://api-int.acuitytec.com/customerlogout";
    private final static String AT_URL_cardNumberCheck = "http://www.acuitytec.com/gdb/cardnumbercheck";
    private final static String AT_URL_newTransaction = "http://api-int.acuitytec.com/newtransaction";
    private final static String AT_URL_customerRegistration = "http://api-int.acuitytec.com/customerregistration";
    private final static String AT_URL_emailCheck = "http://www.acuitytec.com/gdb/emailcheck";

    private final static String AT_URL_makeCall = "http://api-int.acuitytec.com/makecall";
    private final static String AT_URL_updateBonus = "http://api-int.acuitytec.com/updatebonus";
    private final static String AT_URL_updateCall = "http://api-int.acuitytec.com/updatecall";
    private final static String AT_URL_updateTransaction = "http://api-int.acuitytec.com/updatetransaction";
    private final static String AT_URL_verifyBonus = "http://api-int.acuitytec.com/verifybonus";

   //New Transaction

    public NewTransactionResponseVO newTransaction(NewTransactionRequestVO requestVO)
    {
        HashMap requestMap = requestVO.getHashMap();

        HashMap responseMap = callAPI(requestMap,AT_URL_newTransaction);

        NewTransactionResponseVO responseVO= new NewTransactionResponseVO();

        responseVO.setHashMap(responseMap);

        return responseVO;
    }

    // Update Transaction

    public UpdateTransactionResponseVO updateTransaction(UpdateTransactionRequestVO requestVO)
    {
        HashMap requestMap = requestVO.getHashMap();

        HashMap responseMap = callAPI(requestMap, AT_URL_updateTransaction);

        UpdateTransactionResponseVO responseVO = new UpdateTransactionResponseVO();

        responseVO.setHashMap(responseMap);

        return responseVO;
    }


    //Customer Login

    public LoginResponseVO customerLogin(LoginRequestVO requestVO)
    {
        HashMap requestMap = requestVO.getHashMap();

        HashMap responseMap = callAPI(requestMap, AT_URL_customerLogin);

        LoginResponseVO responseVO= new LoginResponseVO();

        responseVO.setHashMap(responseMap);

        return responseVO;

    }

    // Customer Logout

    public LogoutResponseVO customerLogout(LogoutRequestVO requestVO)
    {
        HashMap requestMap = requestVO.getHashMap();

        HashMap responseMap = callAPI(requestMap, AT_URL_customerLogout);

        LogoutResponseVO responseVO= new LogoutResponseVO();

        responseVO.setHashMap(responseMap);

        return responseVO;
    }



    //Bin Check

    public BinCheckResponseVO binCheck(BinCheckRequestVO requestVO)
    {
        HashMap requestMap = requestVO.getHashMap();

        HashMap responseMap = callAPI(requestMap, AT_URL_binCheck);

        BinCheckResponseVO responseVO = new BinCheckResponseVO();

        responseVO.setHashMap(responseMap);

        return responseVO;
    }

    //Card Number Check

    public CardNumberCheckResponseVO cardNumberCheck(CardNumberCheckRequestVO requestVO)
    {
        HashMap requestMap = requestVO.getHashMap();

        HashMap responseMap = callAPI(requestMap, AT_URL_cardNumberCheck);

        CardNumberCheckResponseVO responseVO = new CardNumberCheckResponseVO();

        responseVO.setHashMap(responseMap);

        return responseVO;
    }

    //Email Check

    public EmailCheckResponseVO emailCheck(EmailCheckRequestVO requestVO)
    {
        HashMap requestMap = requestVO.getHashMap();

        HashMap responseMap = callAPI(requestMap, AT_URL_emailCheck);

        EmailCheckResponseVO responseVO = new EmailCheckResponseVO();

        responseVO.setHashMap(responseMap);

        return responseVO;

    }

    public HashMap callAPI(HashMap requestMap, String URL)
    {

        HashMap responseMap = new HashMap();

        ConnectionUtils connUtil = new ConnectionUtils();

        String reqParameters = connUtil.joinMapValue(requestMap, '&');

        //System.out.println("Request  ====  "+reqParameters);

        log.debug("Request  ====  "+reqParameters);

        JSONObject json =null;

        try
        {
            String response = connUtil.doPostURLConnection(URL,reqParameters);

            //System.out.println("Response  ====  "+response);
            log.debug("Response  ====  "+response);

            json = new JSONObject(response);

            Iterator i = json.keys();

            while (i.hasNext())
            {
                String key = (String) i.next();

                log.debug("Response  ====> key:  "+key+"   Value:"+json.get(key));
                //System.out.println("Response  ====> key:  "+key+"   Value:"+json.get(key));
                responseMap.put(key,json.get(key));

            }

        }
        catch (Exception e)
        {
            log.info("Exception while calling AquityAPI"+e);
        }

        return responseMap;
    }


}
