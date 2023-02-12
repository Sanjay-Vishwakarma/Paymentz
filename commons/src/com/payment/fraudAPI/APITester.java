package com.payment.fraudAPI;

import java.sql.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 5/14/14
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class APITester
{

    public static void main(String[] args) throws SQLException
    {

          ATFraudCheckerCron atFraudCheckerCron = new ATFraudCheckerCron();

          //atFraudCheckerCron.newTransactionAPI();

          //atFraudCheckerCron.updateTransactionAPI();

          //atFraudCheckerCron.sendFraudTrnsactionAlert();

          //atFraudCheckerCron.autoReverseTransaction();

          //atFraudCheckerCron.autoReverseTransaction();

          //FraudTransactionDBA ftd=new FraudTransactionDBA();

          //atFraudCheckerCron.sendFraudTransactionAlert();




    }








    //Customer Login
    private static void testlogin()
    {
        ATRiskAPI at = new ATRiskAPI();
        HashMap requestMap = new HashMap();
        requestMap.put("merchant_id", "INT-V8224F32s78416U");
        requestMap.put("password", "JbYKc7OxE7sJran");
        requestMap.put("user_name", "jhon");
        requestMap.put("user_number", "525689");
        requestMap.put("player_status", "new");
        requestMap.put("reg_date", "2014-05-10");
        requestMap.put("reg_ip_address", "192.168.1.7");
        requestMap.put("reg_device_id", "01:23:45:67:89:ab");
        requestMap.put("ip_address", "192.168.1.7");

        LoginRequestVO requestVO = new LoginRequestVO();
        requestVO.setHashMap(requestMap);
        LoginResponseVO responseHash = at.customerLogin(requestVO);

    }
    //New Transaction

    private static void testNewTransaction(HashMap  requestMap)
    {
        ATRiskAPI at = new ATRiskAPI();

        requestMap.put("merchant_id","INT-V8224F32s78416U");
        requestMap.put("password", "JbYKc7OxE7sJran");
        requestMap.put("session_id","p9ahkvhe0i5q97eadfmql1e4u1");
        requestMap.put("user_number","525689");
        requestMap.put("user_name","jhon");
        requestMap.put("player_status","new");
        requestMap.put("reg_date","2014-05-12");
        requestMap.put("reg_ip_address","192.168.1.7");
        requestMap.put("reg_device_id","01:23:45:67:89:ab");
        requestMap.put("customer_information[first_name]","Jonh");

        requestMap.put("customer_information[last_name]","Doe");
        requestMap.put("customer_information[email]","jonh@doe.com");
        requestMap.put("customer_information[address1]","adress x");
        requestMap.put("customer_information[address2]","adress y");
        requestMap.put("customer_information[city]","Miami");
        requestMap.put("customer_information[province]","Florida");
        requestMap.put("customer_information[country]","US");
        requestMap.put("customer_information[postal_code]","12345");
        requestMap.put("customer_information[phone1]","55555555");
        requestMap.put("customer_information[phone2]","55555555");
        requestMap.put("customer_information[dob]","1983-06-02");
        requestMap.put("customer_information[gender]","M");
        requestMap.put("customer_information[id_type]","SSN");
        requestMap.put("customer_information[id_value]","111111111");
        requestMap.put("first_dep_date","2012-02-09");
        requestMap.put("first_with_date","2012-02-09");
        requestMap.put("dep_count","8");
        requestMap.put("with_count","7");
        requestMap.put("current_balance","1000");
        requestMap.put("deposit_limits[pay_method_type]","CC");
        requestMap.put("deposit_limits[dl_min]","20");
        requestMap.put("deposit_limits[dl_daily]","20");
        requestMap.put("deposit_limits[dl_weekly]","20");
        requestMap.put("deposit_limits[dl_monthly]","20");
        requestMap.put("trans_id","1399879403");
        requestMap.put("payment_method[bin]","518214");
        requestMap.put("payment_method[last_digits]","0129");
        requestMap.put("payment_method[card_hash]","4a8b36d1d08f424b8addf82509fd6cf9");
        requestMap.put("amount","1000");
        requestMap.put("currency","USD");
        requestMap.put("time","2014-05-12 04:24:55");
        requestMap.put("status","0");
        requestMap.put("ip","192.168.0.1");
        requestMap.put("device_id","01:23:45:67:89:ab");
        requestMap.put("quantity","3");
        requestMap.put("local_time","2014-05-12 04:24:55");
        requestMap.put("source","a");
        requestMap.put("website","a");
        requestMap.put("custom_variable","a");
        requestMap.put("pass","pass1234");
        requestMap.put("affiliate_name","Happy Dog");
        requestMap.put("affiliate_id","15679");
        requestMap.put("affiliate_sub_id","79846");
        requestMap.put("mc_code","0742");
        requestMap.put("billing_first_name","Jonh");
        requestMap.put("billing_last_name","Doe");
        requestMap.put("billing_email","jonh@doe.com");
        requestMap.put("billing_address1","adress x");
        requestMap.put("billing_address2","adress y");
        requestMap.put("billing_city","Miami");
        requestMap.put("billing_province","Florida");
        requestMap.put("billing_country","US");
        requestMap.put("billing_postal_code","12345");
        requestMap.put("billing_phone1","55555555");
        requestMap.put("billing_phone2","55555555");
        requestMap.put("billing_gender","M");

        NewTransactionRequestVO requestVO = new NewTransactionRequestVO();

        requestVO.setHashMap(requestMap);

        NewTransactionResponseVO responseVO = new NewTransactionResponseVO();

        responseVO=at.newTransaction(requestVO);

        System.out.println("internal transaction id "+responseVO.getInternal_trans_id());

    }

// Customer Logout

    private static void testCustomerLogout()
    {
        ATRiskAPI at = new ATRiskAPI();
        HashMap requestMap = new HashMap();
        requestMap.put("merchant_id","INT-V8224F32s78416U");
        requestMap.put("password", "JbYKc7OxE7sJran");
        requestMap.put("session_id", "r21hbtq6c0e556co81vcndf0t4");
        LogoutRequestVO requestVO = new LogoutRequestVO();
        requestVO.setHashMap(requestMap);
        at.customerLogout(requestVO);

    }
    private static void testBinCheck()
    {
        ATRiskAPI at = new ATRiskAPI();
        HashMap requestMap = new HashMap();
        requestMap.put("merchant_id","INT-V8224F32s78416U");
        requestMap.put("password", "JbYKc7OxE7sJran");
        requestMap.put("bin", "444433");
        BinCheckRequestVO requestVO = new BinCheckRequestVO();
        requestVO.setHashMap(requestMap);
        at.binCheck(requestVO);

    }
    private static void testEmailCheck()
    {
        ATRiskAPI at = new ATRiskAPI();
        HashMap requestMap = new HashMap();
        requestMap.put("merchant_id","INT-V8224F32s78416U");
        requestMap.put("password", "JbYKc7OxE7sJran");
        requestMap.put("emm_user_partial", "raj");
        requestMap.put("emm_domain", "pz.com");
        requestMap.put("emh","");

        EmailCheckRequestVO requestVO = new EmailCheckRequestVO();
        requestVO.setHashMap(requestMap);
        at.emailCheck(requestVO);
    }

    private static void testUpdateTransaction()
    {

        ATRiskAPI at = new ATRiskAPI();
        HashMap requestMap = new HashMap();
        requestMap.put("session_id","df5gkbu0oaigal47ma4no7l7l1");
        requestMap.put("merchant_id", "INT-V8224F32s78416U");
        requestMap.put("password", "JbYKc7OxE7sJran");
        requestMap.put("trans_id","1399879403");
        requestMap.put("internal_trans_id","1069707");
        requestMap.put("processor","false");
        requestMap.put("status", "0");
        requestMap.put("reason","");
        UpdateTransactionRequestVO requestVO = new UpdateTransactionRequestVO();
        requestVO.setHashMap(requestMap);
        at.updateTransaction(requestVO);

    }

    private static void testCardNumberCheck()
    {


        ATRiskAPI at = new ATRiskAPI();
        HashMap requestMap = new HashMap();
        requestMap.put("merchant_id","INT-V8224F32s78416U");
        requestMap.put("password", "JbYKc7OxE7sJran");
        requestMap.put("bin", "454347");
        requestMap.put("last_4", "9996");
        requestMap.put("reason","");
        CardNumberCheckRequestVO requestVO = new CardNumberCheckRequestVO();
        requestVO.setHashMap(requestMap);
        at.cardNumberCheck(requestVO);
    }

}
