package com.fraud.fourstop;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.fraud.AbstractFSGateway;
import com.fraud.FraudSystemService;
import com.fraud.vo.FSGenericRequestVO;
import com.fraud.vo.FSGenericResponseVO;
import com.google.gson.Gson;
import com.logicboxes.util.ApplicationProperties;
import com.manager.vo.fraudruleconfVOs.FraudAccountDetailsVO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Supriya
 * Date: 11/10/16
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class FourStopFSGateway extends AbstractFSGateway
{
    private static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.fourstop");
    public final static String FSNAME = "fourstop";
    private final static String AT_TEST_URL_NEWTRANSACTION = RB.getString("AT_TEST_URL_NEWTRANSACTION");
    //private final static String AT_TEST_URL_NEWTRANSACTION = "http://private-e2ea5-verifyglobalriskcoreservices.apiary-mock.com/newtransaction";
    private final static String AT_TEST_URL_UPDATETRANSACTION = RB.getString("AT_TEST_URL_UPDATETRANSACTION");
    private final static String AT_LIVE_URL_NEWTRANSACTION = RB.getString("AT_LIVE_URL_NEWTRANSACTION");
    private final static String AT_LIVE_URL_UPDATETRANSACTION = RB.getString("AT_LIVE_URL_UPDATETRANSACTION");
    private final static String AT_TEST_URL_CUSTOMERREGISTRATION = RB.getString("AT_TEST_URL_CUSTOMERREGISTRATION");
    private final static String AT_TEST_URL_CUSTOMERUPDATATION = RB.getString("AT_TEST_URL_CUSTOMERUPDATATION");
    private final static String AT_TEST_URL_DOCUMENTVERIFICATION = RB.getString("AT_TEST_URL_DOCUMENTVERIFICATION");
    private static Logger logger = new Logger(FourStopFSGateway.class.getName());
    private static TransactionLogger transactionLogger= new TransactionLogger(FourStopFSGateway.class.getName());
    String AT_TEST_MEMBER_ID="INT-U3cz7Wi733Is1uK";
    String AT_TEST_PASSWORD="R574QrN22yQ8XgH";
    String AT_TEST_USERNAME = "";
    String AT_TEST_USERNUMBER = "";
    String AT_LIVE_MEMBER_ID="";
    String AT_LIVE_PASSWORD="";
    String AT_LIVE_USERNAME="";
    String AT_LIVE_USERNUMBER="";
    String DOCUMENT_FILE_PATH = ApplicationProperties.getProperty("DOCUMENT_FILE_PATH");

    @Override
    public FSGenericResponseVO newTransaction(FSGenericRequestVO requestVO)
    {
        FourStopRequestVO fourStopRequestVO=(FourStopRequestVO)requestVO;
        FraudAccountDetailsVO accountDetailsVO=fourStopRequestVO.getFraudAccountDetailsVO();
        FourStopResponseVO responseVO=new FourStopResponseVO();
        Functions functions = new Functions();

        if(functions.isValueNull(accountDetailsVO.getSubmerchantUsername()) && functions.isValueNull(accountDetailsVO.getSubmerchantPassword()))
        {
            fourStopRequestVO.setMerchant_id(accountDetailsVO.getSubmerchantUsername());
            fourStopRequestVO.setPassword(accountDetailsVO.getSubmerchantPassword());
        }
        else
        {
            fourStopRequestVO.setMerchant_id(accountDetailsVO.getFraudSystemMerchantId());
            fourStopRequestVO.setPassword(accountDetailsVO.getPassword());
        }
        fourStopRequestVO.setWebsite(accountDetailsVO.getSubMerchantId());
        fourStopRequestVO.setUser_name(fourStopRequestVO.getCustomer_information_email());
        fourStopRequestVO.setUser_number(fourStopRequestVO.getCustomer_information_email());

        if("Y".equals(accountDetailsVO.getIsTest()))
        {
            HashMap requestHash=fourStopRequestVO.getHashMap();
            HashMap responseHash=callAPI(requestHash,AT_TEST_URL_NEWTRANSACTION);
            responseVO.setHashMap(responseHash);
        }
        else
        {
            HashMap requestHash=fourStopRequestVO.getHashMap();
            HashMap responseHash=callAPI(requestHash,AT_TEST_URL_NEWTRANSACTION);
            responseVO.setHashMap(responseHash);
        }
        return responseVO;
    }
    @Override
    public  FSGenericResponseVO updateTransaction(FSGenericRequestVO requestVO)
    {
        FourStopRequestVO fourStopRequestVO=(FourStopRequestVO)requestVO;
        FourStopResponseVO fourStopResponseVO=new FourStopResponseVO();
        boolean isTest= FraudSystemService.isMemberInTestMode(fourStopRequestVO.getMemberId());
        if(isTest)
        {
            fourStopRequestVO.setMerchant_id(AT_TEST_MEMBER_ID);
            fourStopRequestVO.setPassword(AT_TEST_PASSWORD);
            HashMap requestHash=fourStopRequestVO.getHashMap();
            HashMap responseHash=callAPI(requestHash,AT_TEST_URL_UPDATETRANSACTION);
            fourStopResponseVO.setHashMap(responseHash);
        }
        else
        {
            fourStopRequestVO.setMerchant_id(AT_LIVE_MEMBER_ID);
            fourStopRequestVO.setPassword(AT_LIVE_PASSWORD);
            HashMap requestHash=fourStopRequestVO.getHashMap();
            HashMap responseHash=callAPI(requestHash,AT_LIVE_URL_UPDATETRANSACTION);
            fourStopResponseVO.setHashMap(responseHash);
        }
        return fourStopResponseVO;

    }
    @Override
    public FSGenericResponseVO customerRegistration(FSGenericRequestVO requestVO)
    {
        FourStopRequestVO fourStopRequestVO=(FourStopRequestVO)requestVO;
        FraudAccountDetailsVO accountDetailsVO=fourStopRequestVO.getFraudAccountDetailsVO();
        FourStopResponseVO responseVO=new FourStopResponseVO();

        fourStopRequestVO.setMerchant_id(accountDetailsVO.getFraudSystemMerchantId());
        fourStopRequestVO.setPassword(accountDetailsVO.getPassword());
        fourStopRequestVO.setWebsite("");
        fourStopRequestVO.setUser_name(fourStopRequestVO.getCustomer_information_email());
        //fourStopRequestVO.setUser_number(fourStopRequestVO.getCustomer_information_email());

        String isTest="Y";
        if("Y".equals(accountDetailsVO.getIsTest()))
        {
            logger.error("inside test :");
            HashMap requestHash=fourStopRequestVO.getHashMap();
            logger.error("requestHash ::::: " + requestHash);
            HashMap responseHash=callAPI(requestHash,AT_TEST_URL_CUSTOMERREGISTRATION);
            responseVO.setHashMap(responseHash);

            JSONObject jsonObject=responseVO.getJsonObject();
            Map readJsonData = new Gson().fromJson(String.valueOf(jsonObject), Map.class);
            responseVO.setCustomerRegId((String) readJsonData.get("id"));
            responseVO.setScore((Double) readJsonData.get("score"));
            responseVO.setRecommendation((String) readJsonData.get("rec"));
            responseVO.setConfidence_level((Double) readJsonData.get("confidence_level"));
            responseVO.setDescription((String) readJsonData.get("description"));
            responseVO.setJsonObject(jsonObject);
        }
        else
        {
            HashMap requestHash=fourStopRequestVO.getHashMap();
            HashMap responseHash=callAPI(requestHash,AT_TEST_URL_CUSTOMERREGISTRATION);
            responseVO.setHashMap(responseHash);

            JSONObject jsonObject=responseVO.getJsonObject();
            Map readJsonData = new Gson().fromJson(String.valueOf(jsonObject), Map.class);
            responseVO.setCustomerRegId((String) readJsonData.get("id"));
            responseVO.setScore((Double) readJsonData.get("score"));
            responseVO.setRecommendation((String) readJsonData.get("rec"));
            responseVO.setConfidence_level((Double) readJsonData.get("confidence_level"));
            responseVO.setDescription((String) readJsonData.get("description"));
            responseVO.setJsonObject(jsonObject);
        }
        return responseVO;
    }

    @Override
    public FSGenericResponseVO customerUpdatation(FSGenericRequestVO requestVO)
    {
        FourStopRequestVO fourStopRequestVO=(FourStopRequestVO)requestVO;
        FourStopResponseVO fourStopResponseVO=new FourStopResponseVO();
        FraudAccountDetailsVO accountDetailsVO=fourStopRequestVO.getFraudAccountDetailsVO();
        // boolean isTest= FraudSystemService.isMemberInTestMode(fourStopRequestVO.getMemberId());

        boolean isTest=true;
        if(isTest)
        {
            fourStopRequestVO.setMerchant_id(accountDetailsVO.getFraudSystemMerchantId());
            fourStopRequestVO.setPassword(accountDetailsVO.getPassword());
            HashMap requestHash=fourStopRequestVO.getHashMap();
            HashMap responseHash=callAPI(requestHash,AT_TEST_URL_CUSTOMERUPDATATION);
            fourStopResponseVO.setHashMap(responseHash);
        }
        else
        {
            fourStopRequestVO.setMerchant_id(accountDetailsVO.getFraudSystemMerchantId());
            fourStopRequestVO.setPassword(accountDetailsVO.getPassword());
            HashMap requestHash=fourStopRequestVO.getHashMap();
            HashMap responseHash=callAPI(requestHash,AT_TEST_URL_CUSTOMERUPDATATION);
            fourStopResponseVO.setHashMap(responseHash);
        }
        return fourStopResponseVO;
    }

    @Override
    public FSGenericResponseVO documentIdVerify(FSGenericRequestVO requestVO)
    {
        FourStopRequestVO fourStopRequestVO=(FourStopRequestVO)requestVO;
        FraudAccountDetailsVO accountDetailsVO=fourStopRequestVO.getFraudAccountDetailsVO();
        FourStopResponseVO fourStopResponseVO=new FourStopResponseVO();
        fourStopRequestVO.setMerchant_id(accountDetailsVO.getFraudSystemMerchantId());
        fourStopRequestVO.setPassword(accountDetailsVO.getPassword());
        fourStopRequestVO.setUser_name(fourStopRequestVO.getCustomer_information_email());
        fourStopRequestVO.setUser_number(fourStopRequestVO.getCustomer_information_email());

        List errorList=new ArrayList();

        logger.error("details:::" + accountDetailsVO.getFraudSystemMerchantId() + "---" + accountDetailsVO.getPassword());

        String charset = "UTF-8";

        File uploadFile1 = new File(fourStopRequestVO.getFilePath()+fourStopRequestVO.getFileName());
        File uploadFile2 = new File(fourStopRequestVO.getFilePath()+fourStopRequestVO.getFileName2());
        File uploadFile3 = new File(fourStopRequestVO.getFilePath()+fourStopRequestVO.getFileName3());
        File uploadFile4 = new File(fourStopRequestVO.getFilePath()+fourStopRequestVO.getFileName4());

        logger.error("uploadFile1 :"+uploadFile1);
        logger.error("uploadFile2 :"+uploadFile2);
        logger.error("uploadFile3 :"+uploadFile3);
        logger.error("uploadFile4 :"+uploadFile4);

        try {

            MultipartUtility multipart = new MultipartUtility(AT_TEST_URL_DOCUMENTVERIFICATION, charset);

            multipart.addFormField("merchant_id", fourStopRequestVO.getMerchant_id());
            multipart.addFormField("password", fourStopRequestVO.getPassword());
            multipart.addFormField("user_name", fourStopRequestVO.getUser_name());
            multipart.addFormField("user_number", fourStopRequestVO.getUser_number());
            multipart.addFormField("customer_registration_id", fourStopRequestVO.getCustomer_registration_id());
            multipart.addFormField("method", fourStopRequestVO.getMethod());

            logger.error("customer_registration_id : " + fourStopRequestVO.getCustomer_registration_id());
            logger.error("method : " + fourStopRequestVO.getMethod());

            if(uploadFile1.exists()){
                multipart.addFilePart("doc", uploadFile1);
            }
            if(uploadFile2.exists()){
                multipart.addFilePart("doc2", uploadFile2);
            }
            if(uploadFile3.exists()){
                multipart.addFilePart("doc3", uploadFile3);
            }
            if(uploadFile4.exists()){
                multipart.addFilePart("doc4", uploadFile4);
            }


            // multipart.addFilePart("doc3", uploadFile3);
            // multipart.addFilePart("doc4", uploadFile4);

            List<String> response = multipart.finish();

            JSONArray genreArray=null;
            for(String responseStr:response){
                try{
                    genreArray =new JSONArray(responseStr);
                    if(genreArray!=null){
                        JSONObject jsonObject=(JSONObject)genreArray.get(0);
                        JSONObject jsonObject1=new JSONObject();
                        if (!genreArray.isNull(1)){
                            jsonObject1=(JSONObject)genreArray.get(1);
                        }
                        Iterator i = jsonObject.keys();
                        HashMap responseMap = new HashMap();
                        while (i.hasNext())
                        {
                            String key = (String) i.next();
                            responseMap.put(key,jsonObject.get(key));

                            if ("status".equals(key)){
                                fourStopResponseVO.setStatus1((Integer) jsonObject.get(key));
                            }
                            else if ("reference_id".equals(key)){
                                fourStopResponseVO.setReference_id((String) jsonObject.get(key));
                            }
                            else if ("description".equals(key)){
                                fourStopResponseVO.setDescription1((String) jsonObject.get(key));
                            }
                            else if ("kyc_source".equals(key)){
                                fourStopResponseVO.setKyc_source((String) jsonObject.get(key));
                            }
                        }

                        if (jsonObject1!=null){
                            Iterator ii = jsonObject1.keys();
                            HashMap responseMap1 = new HashMap();
                            while (ii.hasNext())
                            {
                                String key = (String) ii.next();
                                responseMap1.put(key,jsonObject1.get(key));
                                if ("status".equals(key)){
                                    logger.error("inside json 2: " + (Integer) jsonObject1.get(key));
                                    fourStopResponseVO.setStatus2((Integer) jsonObject1.get(key));
                                }
                                else if ("reference_id".equals(key)){
                                    fourStopResponseVO.setReference_id2((String) jsonObject1.get(key));
                                }
                                else if ("description".equals(key)){
                                    fourStopResponseVO.setDescription2((String) jsonObject1.get(key));
                                }
                                else if ("kyc_source".equals(key)){
                                    fourStopResponseVO.setKyc_source2((String) jsonObject1.get(key));
                                }
                            }

                        }
                    }

                }catch (Exception e){
                    logger.error("Exception--->",e);
                }
            }
            logger.error("SERVER REPLIED : ");

            for (String line : response) {
                logger.error(line);
            }
        } catch (IOException ex) {
            logger.error("IOException--->", ex);
        }
        return fourStopResponseVO;
    }

    public HashMap callAPI(HashMap requestMap, String URL)
    {
        HashMap responseMap = new HashMap();
        FourStopUtils fourStopUtils= new FourStopUtils();
        String reqParameters = fourStopUtils.joinMapValue(requestMap, '&');
        logger.error("Request  ====  "+reqParameters);
        JSONObject json =null;

        try
        {
            Date date541=new Date();
            String response = fourStopUtils.doPostURLConnection(URL,reqParameters);
            transactionLogger.debug("PZFraudProcessor doPostURLConnection(fraud gateway call) diff time 541########"+(new Date().getTime()-date541.getTime()));
            logger.error("Response  ====  "+response);
            json = new JSONObject(response);
            Iterator i = json.keys();
            while (i.hasNext())
            {
                String key = (String) i.next();
                logger.error("Response  ====> key:  "+key+"   Value:"+json.get(key));
                //System.out.println("Response  ====> key:  "+key+"   Value:"+json.get(key));
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
