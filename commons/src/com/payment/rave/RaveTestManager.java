package com.payment.rave;

/**
 * Created by admin on 10/28/2017.
 */
public class RaveTestManager
{
    private static RaveUtils raveUtils= new RaveUtils();
    public static void main(String[] args) throws Exception
    {


        /*String secKey ="FLWSECK-4127f15e63c9098402dcc7891798fb0f-X";
        String pubKey ="FLWPUBK-1cf610974690c2560cb4c36f4921244a-X";*/


        /*TripleDES tripleDES=new TripleDES();
        String key=tripleDES.getKey(secKey);*/

        //System.out.println("key::::"+key);

        try
        {
           /* SimpleDateFormat inputDate = new SimpleDateFormat("yyyymmdd");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");*/

          /*Functions functions=new Functions();

          String response="{\"status\":\"success\",\"message\":\"V-COMP\",\"data\":{\"id\":48516,\"txRef\":\"45798\",\"orderRef\":\"URF_1509723287968_3135635\",\"flwRef\":\"FLW-MOCK-79899376f9b1b464f306eb681c4e4ffd\",\"redirectUrl\":\"<hostname>\",\"device_fingerprint\":\"69e6b7f0b72037aa8428b70fbe03986c\",\"settlement_token\":null,\"cycle\":\"one-time\",\"amount\":10,\"charged_amount\":10,\"appfee\":0,\"merchantfee\":0,\"merchantbearsfee\":1,\"chargeResponseCode\":\"02\",\"raveRef\":\"RV3150972328771524C5A109F6\",\"chargeResponseMessage\":\"Success-Pending-otp-validation\",\"authModelUsed\":\"VBVSECURECODE\",\"currency\":\"USD\",\"IP\":\"::ffff:127.0.0.1\",\"narration\":\"Test transaction\",\"status\":\"success-pending-validation\",\"vbvrespmessage\":\"Approved. Successful\",\"authurl\":\"http://flw-pms-dev.eu-west-1.elasticbeanstalk.com/mockvbvpage?ref=FLW-MOCK-79899376f9b1b464f306eb681c4e4ffd&code=00&message=Approved. Successful&receiptno=RN1509723287974\",\"vbvrespcode\":\"00\",\"acctvalrespmsg\":null,\"acctvalrespcode\":null,\"paymentType\":\"card\",\"paymentPlan\":null,\"paymentPage\":null,\"paymentId\":\"2\",\"fraud_status\":\"ok\",\"charge_type\":\"normal\",\"is_live\":0,\"createdAt\":\"2017-11-03T15:34:47.000Z\",\"updatedAt\":\"2017-11-03T15:34:49.000Z\",\"deletedAt\":null,\"customerId\":7178,\"AccountId\":134,\"customer\":{\"id\":7178,\"phone\":\"091-9730490101\",\"fullName\":\"Sandip Kolekar\",\"customertoken\":null,\"email\":\"<hostname>\",\"createdAt\":\"2017-11-03T15:34:47.000Z\",\"updatedAt\":\"2017-11-03T15:34:47.000Z\",\"deletedAt\":null,\"AccountId\":134},\"customercandosubsequentnoauth\":true}}";



            if(functions.isValueNull(response)){

                if("283".equals(clearSettleResponseVO.getCode()))
                {
                    if ("PENDING".equals(clearSettleResponseVO.getStatus())){
                        status = "pending";
                        commResponseVO.setRedirectUrl(URLDecoder.decode(clearSettleResponseVO.getPurchaseUrl()));
                        if (functions.isValueNull(clearSettleResponseVO.getDescriptor())){
                            descriptor = clearSettleResponseVO.getDescriptor();
                        }
                        else{
                            descriptor=gatewayAccount.getDisplayName();
                        }
                    }else{
                        status = "failed";
                        remark=clearSettleResponseVO.getMessage();
                    }
                }
                else{
                    status = "failed";
                    remark=clearSettleResponseVO.getMessage();
                }
            }else{
                status="failed";
                remark="Bank connectivity issue";
            }

            if("success".equals(status)){

            }

            System.out.println("jsonObject:::"+jsonObject);
            System.out.println("status:::"+status);
            System.out.println("data:::"+data);
            System.out.println("jsonObjData1:::"+jsonObjData1);*/

            String secKey ="FLWSECK-c51891678d48c39eff3701ff686bdb69-X";
            String pubKey ="FLWPUBK-8cd258c49f38e05292e5472b2b15906e-X";

           String data = "{ \"PBFPubKey\":\"FLWPUBK-8cd258c49f38e05292e5472b2b15906e-X\"," +
                   "\"cardno\":\"4999082100029373\", " +
                   "\"charge_type\":\"preauth\", " +
                   "\"currency\": \"NGN\", " +
                   "\"country\": \"NG\", " +
                   "\"cvv\":\"812\"," +
                   "\"amount\":\"10\", " +
                   "\"expiryyear\":\"2019\"," +
                   "\"expirymonth\":\"09\"," +
                   "\"email\": \"roshan.mathew@pz.com\"," +
                   "\"firstname\": \"roshan\","+
                   "\"lastname\": \"mathew\"," +
                   "\"IP\":\"115.96.19.10\"," +
                   "\"narration\": \"Test transaction\"," +
                   "\"txRef\":\"4567899\"," +
                   "\"phonenumber\":\"+91-7045496070\"," +
                   "\"pin\":\"3310\"," +
                   "\"suggested_auth\":\"PIN\"," +
                   "\"device_fingerprint\": \"69e6b7f0b72037aa8428b70fbe03986c\"," +
                   "\"redirect_url\": \"http://www.abcd.com\" " +
                   "}";
            String key=raveUtils.getKey(secKey);
            String encData=raveUtils.encryptData(data,key);//tripleDES.encryptData(data, key);
            /*System.out.println("encData::::"+encData);
            System.out.println(data);
*/
            String data1 = "{\"PBFPubKey\":\"FLWPUBK-8cd258c49f38e05292e5472b2b15906e-X\"," +
                    "\"client\":\"" + encData + "\"," +
                    "\"alg\": \"3DES-24\" " +
                    "}";
                    //System.out.println(data1);

            /*String data2= "{\"flwRef\":\"FLW00854366\"," +
                    "\"SECKEY\": \"FLWSECK-c51891678d48c39eff3701ff686bdb69-X\" " +
                    "}";
            System.out.println(data2);
*/
                  /*String data2="{\"ref\":\"FLW-MOCK-550f427272c7e92ef4b4f44fde978394\"," +
                            "\"amount\": \"10.00\", " +
                            "\"seckey\": \"FLWSECK-e6db11d1f8a6208de8cb2f94e293450e-X\" " +
                            "}";*/

             /*String data2="{\"ref\":\"FLW00860688\"," +
                    "\"action\": \"void\", " +
                    "\"SECKEY\": \"FLWSECK-c51891678d48c39eff3701ff686bdb69-X\" " +
                    "}";
            System.out.println(data2);*/

            //System.out.println("data2::::::"+data2);

            String responseData = raveUtils. doPostHTTPSURLConnectionFromData("http://flw-pms-dev.eu-west-1.elasticbeanstalk.com/flwv3-pug/getpaidx/api/charge", data1);
            //System.out.println("responseData--" + responseData);


            /*org.codehaus.jackson.map.ObjectMapper objectMapper = new org.codehaus.jackson.map.ObjectMapper();
            objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            RaveResponseVO raveResponseVO = objectMapper.readValue(responseData, RaveResponseVO.class);*/

        }
       /* catch (JsonMappingException e){
            PZExceptionHandler.raiseTechnicalViolationException(RavePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (JsonParseException e){
            PZExceptionHandler.raiseTechnicalViolationException(RavePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Parsing while placing transaction", PZTechnicalExceptionEnum.JSON_PARSE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e){
            PZExceptionHandler.raiseTechnicalViolationException(RavePaymentGateway.class.getName(), "processSale()", null, "common", "Technical Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }*/
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }
}
