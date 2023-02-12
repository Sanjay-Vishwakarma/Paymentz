package com.fraud;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.fraud.at.ATRequestVO;
import com.fraud.at.ATResponseVO;
import com.fraud.at.AtUtils;
import com.fraud.vo.FSGenericRequestVO;
import com.fraud.vo.FSGenericResponseVO;
import com.manager.vo.fraudruleconfVOs.FraudAccountDetailsVO;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/30/14
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATFSGateway  extends AbstractFSGateway
{
    private static Logger logger = new Logger(ATFSGateway.class.getName());
    private static TransactionLogger transactionLogger= new TransactionLogger(ATFSGateway.class.getName());
    public final static String FSNAME="AcuityTech";

    String AT_TEST_MEMBER_ID="INT-V8224F32s78416U";
    String AT_TEST_PASSWORD="JbYKc7OxE7sJran";
    String AT_TEST_USERNAME="Jimmy Mehta";
    String AT_TEST_USERNUMBER="4454715454";

    String AT_LIVE_MEMBER_ID="PZ-02gQa4586jd9Z7V";
    String AT_LIVE_PASSWORD="4725yDXSTEp8nfn";
    String AT_LIVE_USERNAME="jmehta";
    String AT_LIVE_USERNUMBER="445471545445";

    private final static String AT_TEST_URL_NEWTRANSACTION = "http://api-int.acuitytec.com/newtransaction";
    private final static String AT_TEST_URL_UPDATETRANSACTION = "http://api-int.acuitytec.com/updatetransaction";

    private final static String AT_LIVE_URL_NEWTRANSACTION = "https://service1.acuitytec.com/api/newtransaction";
    private final static String AT_LIVE_URL_UPDATETRANSACTION = "https://service1.acuitytec.com/api/updatetransaction";


    @Override
    public FSGenericResponseVO newTransaction(FSGenericRequestVO requestVO)
    {
        ATRequestVO atRequestVO=(ATRequestVO)requestVO;
        //boolean isTest=FraudSystemService.isMemberInTestMode(atRequestVO.getMemberId());
        FraudAccountDetailsVO accountDetailsVO=atRequestVO.getFraudAccountDetailsVO();
        ATResponseVO responseVO=new ATResponseVO();

        //String userName=atRequestVO.getCustomer_information_first_name()+" "+atRequestVO.getCustomer_information_last_name();
        //String userNumber=atRequestVO.getCustomer_information_first_name()+atRequestVO.getCustomer_information_last_name()+atRequestVO.getPayment_method_bin()+atRequestVO.getPayment_method_last_digits();

        atRequestVO.setMerchant_id(accountDetailsVO.getFraudSystemMerchantId());
        atRequestVO.setPassword(accountDetailsVO.getPassword());
        atRequestVO.setWebsite(accountDetailsVO.getSubMerchantId());
        atRequestVO.setUser_name(atRequestVO.getCustomer_information_email());
        atRequestVO.setUser_number(atRequestVO.getCustomer_information_email());

        if("Y".equals(accountDetailsVO.getIsTest()))
        {
            //atRequestVO.setMerchant_id(AT_TEST_MEMBER_ID);
            //atRequestVO.setPassword(AT_TEST_PASSWORD);
            //atRequestVO.setUser_name(AT_TEST_USERNAME);
            //atRequestVO.setUser_number(AT_TEST_USERNUMBER);
            HashMap requestHash=atRequestVO.getHashMap();
            HashMap responseHash=callAPI(requestHash,AT_TEST_URL_NEWTRANSACTION);
            responseVO.setHashMap(responseHash);
        }
        else
        {
            //atRequestVO.setMerchant_id(AT_LIVE_MEMBER_ID);
            //atRequestVO.setPassword(AT_LIVE_PASSWORD);
            //atRequestVO.setUser_name(AT_LIVE_USERNAME);
            //atRequestVO.setUser_number(AT_LIVE_USERNUMBER);
            HashMap requestHash=atRequestVO.getHashMap();
            HashMap responseHash=callAPI(requestHash,AT_LIVE_URL_NEWTRANSACTION);
            responseVO.setHashMap(responseHash);
        }
        return responseVO;
    }
    @Override
    public  FSGenericResponseVO updateTransaction(FSGenericRequestVO requestVO)
    {
        ATRequestVO atRequestVO=(ATRequestVO)requestVO;
        ATResponseVO atResponseVO=new ATResponseVO();
        boolean isTest=FraudSystemService.isMemberInTestMode(atRequestVO.getMemberId());
        if(isTest)
        {
            atRequestVO.setMerchant_id(AT_TEST_MEMBER_ID);
            atRequestVO.setPassword(AT_TEST_PASSWORD);
            HashMap requestHash=atRequestVO.getHashMap();
            HashMap responseHash=callAPI(requestHash,AT_TEST_URL_UPDATETRANSACTION);
            atResponseVO.setHashMap(responseHash);
        }
        else
        {
            atRequestVO.setMerchant_id(AT_LIVE_MEMBER_ID);
            atRequestVO.setPassword(AT_LIVE_PASSWORD);
            HashMap requestHash=atRequestVO.getHashMap();
            HashMap responseHash=callAPI(requestHash,AT_LIVE_URL_UPDATETRANSACTION);
            atResponseVO.setHashMap(responseHash);
        }
        return atResponseVO;

    }

    @Override
    public FSGenericResponseVO customerRegistration(FSGenericRequestVO requestVO)
    {
        return null;
    }

    @Override
    public FSGenericResponseVO customerUpdatation(FSGenericRequestVO requestVO)
    {
        return null;
    }

    @Override
    public FSGenericResponseVO documentIdVerify(FSGenericRequestVO requestVO)
    {
        return null;
    }


    public HashMap callAPI(HashMap requestMap, String URL)
    {

        HashMap responseMap = new HashMap();
        AtUtils connUtil = new AtUtils();
        String reqParameters = connUtil.joinMapValue(requestMap, '&');

        logger.error("Request  ====  "+reqParameters);

        JSONObject json =null;

        try
        {
            Date date541=new Date();
            String response = connUtil.doPostURLConnection(URL,reqParameters);
            transactionLogger.debug("PZFraudProcessor doPostURLConnection(fraud gateway call) diff time 541########"+(new Date().getTime()-date541.getTime()));
            logger.error("Response  ====  "+response);
            json = new JSONObject(response);
            Iterator i = json.keys();
            while (i.hasNext())
            {
                String key = (String) i.next();
                logger.error("Response  ====> key:  "+key+"   Value:"+json.get(key));
                /*System.out.println("Response  ====> key:  "+key+"   Value:"+json.get(key));*/
                responseMap.put(key,json.get(key));
            }
            responseMap.put("jsonObject",json);

        }
        catch (Exception e)
        {
            responseMap.put("status","1");
            responseMap.put("score","0");
            responseMap.put("desc",e.getMessage());
            logger.error(e.getMessage());
        }
        return responseMap;

    }

}
