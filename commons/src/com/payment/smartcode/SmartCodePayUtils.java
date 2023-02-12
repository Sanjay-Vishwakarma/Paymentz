package com.payment.smartcode;

import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Header;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import com.manager.dao.MerchantDAO;
import com.manager.dao.PayoutDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.ProductDetailsVO;
import com.manager.vo.TerminalVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by Admin on 3/8/2021.
 */
public class SmartCodePayUtils
{

    private final static ResourceBundle BPBANKSID           = LoadProperties.getProperty("com.directi.pg.PUBANKS");
    private final  static ResourceBundle rbAmount           = LoadProperties.getProperty("com.directi.pg.puma");

    private static TransactionLogger transactionlogger      = new TransactionLogger(SmartCodePayUtils.class.getName());
    public static final String ALGO                         = "AES";
    private static Stack<MessageDigest> stack       = new Stack<MessageDigest>();
    private final static String HASH_ALGORITHM      = "SHA-512";
    public static final String CHARSETNAME          = "UTF-8";
    private final static String INVOICE_GENERATION_PATH = ApplicationProperties.getProperty("INVOICE_GENERATION_PATH");
    private final static String PARTNER_LOGO_PATH       = ApplicationProperties.getProperty("PARTNER_LOGO_PATH");
    final static String NEW             = "new";
    final static String REGENERATE      = "regenerate";
    final static String CANCEL          = "cancel";
    Functions functions                 = new Functions();
    private static final ResourceBundle countryName = LoadProperties.getProperty("com.directi.pg.countrycodenamepairlist");

    public  static String getLast2DigitOfExpiryYear(String ExpiryYear)
    {
        Functions functions =  new Functions();
        String expiryYearLast2Digit = "";
        if (functions.isValueNull(ExpiryYear)){
            expiryYearLast2Digit = ExpiryYear.substring(ExpiryYear.length()-2,ExpiryYear.length());
        }
        return expiryYearLast2Digit;
    }


    public static String getBankName(String BankCode ){
        String BankName="";
        try
        {
            BankName = BPBANKSID.getString(BankCode);
        }catch (Exception e)
        {
            transactionlogger.error("Exception--->",e);
        }

        return BankName;
    }


    public static String  getPaymentType(String paymentMode)
    {
        String payMode = "";
        if("CC".equalsIgnoreCase(paymentMode))
            payMode = "credit";
        if("DC".equalsIgnoreCase(paymentMode))
            payMode = "DebitCard";
        if("NBI".equalsIgnoreCase(paymentMode))
            payMode = "Netbanking";
        if("UPI".equalsIgnoreCase(paymentMode))
            payMode = "UPI";
        if("EWI".equalsIgnoreCase(paymentMode))
            payMode = "Wallet";

        return payMode;
    }


    public static String getPaymentBrand(String paymentMode)
    {
        String payBrand = "";
        if("1".equalsIgnoreCase(paymentMode))
            payBrand = "VISA";
        if("2".equalsIgnoreCase(paymentMode))
            payBrand = "MAST";
        if("23".equalsIgnoreCase(paymentMode))
            payBrand = "RUPAY";
       return payBrand;
    }


    private static void consume(MessageDigest digest) {
        stack.push(digest);
    }
    public static String getAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2    = Double.valueOf(amount);
        String amt      = d.format(dObj2);
        return amt;
    }

    public void updateMainTableEntry(String remark, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET remark=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, remark);
            ps2.setString(2, trackingid);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionlogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionlogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null){
                Database.closeConnection(connection);
            }
        }
    }


    public static void updateOrderid(String paymentid, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET paymentid=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, paymentid);
            ps2.setString(2, trackingid);
            transactionlogger.error("payg updateOrderid--->"+ps2);

            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionlogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionlogger.error("SystemError---", s);
        }
        finally
        {
            if(connection!=null){
                Database.closeConnection(connection);
            }
        }
    }

    public Boolean updateTransaction (String trackingid, String  customerId ){

        transactionlogger.error("in side  updateTransaction----------->"+customerId);
        Connection con = null;
        PreparedStatement psUpdateTransaction = null;
        boolean isUpdate=false;
        try
        {

            con                 = Database.getConnection();
            String update       = "update transaction_common set customerId= ? where trackingid=?";
            psUpdateTransaction = con.prepareStatement(update.toString());
            psUpdateTransaction.setString(1, customerId);
            psUpdateTransaction.setString(2, trackingid);
            transactionlogger.error("transaction common query----"+psUpdateTransaction);
            int i=psUpdateTransaction.executeUpdate();
            if(i>0)
            {
                isUpdate=true;
            }
        }

        catch (SQLException e)
        {
            transactionlogger.error("SQLException----",e);

        }
        catch (SystemError systemError)
        {
            transactionlogger.error("SystemError----", systemError);
        }
        finally
        {
            Database.closePreparedStatement(psUpdateTransaction);
            if(con!=null){
                Database.closeConnection(con);
            }
        }
        return isUpdate;

    }

    public void updateRRNMainTableEntry(String RRN, String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET rrn=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, RRN);
            ps2.setString(2, trackingid);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionlogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionlogger.error("SystemError---", s);
        }
        finally
        {
            if(connection != null){
                Database.closeConnection(connection);
            }
        }
    }

    public CommRequestVO getSmartCodeRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO             = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO   = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO         = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO           = new CommMerchantVO();
        TerminalVO terminalVO                   = new TerminalVO();
        terminalVO                              = commonValidatorVO.getTerminalVO();
        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCustomerBankId(commonValidatorVO.getProcessorName());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());
        transactionlogger.error("utils vpa--->" + commonValidatorVO.getVpa_address());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());

        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        if(functions.isValueNull(commonValidatorVO.getTerminalVO().getAutoRedirectRequest())){
        commRequestVO.setAutoRedirectFlag(commonValidatorVO.getTerminalVO().getAutoRedirectRequest());
    }

        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }

    public static String doGetHTTPSURLConnectionClient(String url,String request,String base64Credentials) throws PZTechnicalViolationException
    {
        String result   = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.addRequestHeader("Authorization", "Basic "+base64Credentials);

            post.setRequestBody(request);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result          = response;
        }
        catch (HttpException he)
        {
            transactionlogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("SmartCodePayUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("SmartCodePayUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }finally
        {
            post.releaseConnection();
        }

        if (result == null)
            return "";
        else
            return result;
    }

    static String doPostHttpUrlConnection(String Request_url, String data, String trackingId) throws Exception
    {

        HttpClient httpClient = new HttpClient();

        PostMethod postMethod = new PostMethod(Request_url);
        String result= "";
        try
        {
            postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");

            postMethod.setRequestBody(data);
            httpClient.executeMethod(postMethod);
            result = new String(postMethod.getResponseBody());

            org.apache.commons.httpclient.Header[] requestHeaders =  postMethod.getRequestHeaders();
            transactionlogger.error(trackingId+" "+"request Headers[]:");
            for(org.apache.commons.httpclient.Header header1 : requestHeaders)
            {
                transactionlogger.error(trackingId+" "+header1.getName() + " : " + header1.getValue());
            }

            org.apache.commons.httpclient.Header[] responseHeaders =  postMethod.getResponseHeaders();
            transactionlogger.error(trackingId+" "+"response Headers[]:");
            for(org.apache.commons.httpclient.Header header2 : responseHeaders)
            {
                transactionlogger.error(trackingId+" "+header2.getName() + " : " + header2.getValue());
            }

        }
        catch (HttpException he)
        {
            transactionlogger.error("HttpException----"+he);
            PZExceptionHandler.raiseTechnicalViolationException("BoomBillUtils.java", "doGetHTTPSURLConnectionClient()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.HTTP_EXCEPTION, null, he.getMessage(), he.getCause());
        }
        catch (IOException io)
        {
            transactionlogger.error("IOException----"+io);
            PZExceptionHandler.raiseTechnicalViolationException("BoomBillUtils.java","doGetHTTPSURLConnectionClient()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }
        finally
        {
            postMethod.releaseConnection();
        }
        return result;

    }


    public void updatePayGTransctionId(String transctionId,String paymentid ,String trackingid)
    {
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1     = "UPDATE transaction_common SET authorization_code=?,paymentid=? WHERE trackingid=?";
            PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
            ps2.setString(1, transctionId);
            ps2.setString(2, paymentid);
            ps2.setString(3, trackingid);
            ps2.executeUpdate();
        }
        catch (SQLException se)
        {
            transactionlogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionlogger.error("SystemError---", s);
        }
        finally
        {
            if(connection != null){
                Database.closeConnection(connection);
            }
        }
    }

    public String encrypt1(String data, String generatedKey, String payId) throws Exception {
        try {

            // String generatedKey = (getHash(salt+payId)).substring(0,32);
            String ivString     = generatedKey.substring(0,16);
            Key keyObj          = null;
            keyObj              = new SecretKeySpec(generatedKey.getBytes(), ALGO);
            IvParameterSpec iv  = new IvParameterSpec(ivString.getBytes("UTF-8"));
//			IvParameterSpec iv = new IvParameterSpec(key.getBytes(ConfigurationConstants.DEFAULT_ENCODING_UTF_8.getValue()));
            Cipher cipher       = Cipher.getInstance("AES" + "/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, keyObj, iv);

            byte[] encValue = cipher.doFinal(data.getBytes("UTF-8"));

            java.util.Base64.Encoder base64Encoder    = java.util.Base64.getEncoder().withoutPadding();
            String base64EncodedData        = base64Encoder.encodeToString(encValue);

            return base64EncodedData;
        } catch (Exception e) {
            throw new Exception("Error during encryption process");
        }
    }

    public String decrypt1(String data,String generatedKey, String payId) throws Exception {
        try {
            // String generatedKey = (getHash(salt+payId)).substring(0,32);
            String ivString     = generatedKey.substring(0,16);
            IvParameterSpec iv  = new IvParameterSpec(ivString.getBytes("UTF-8"));
//			IvParameterSpec iv = new IvParameterSpec(key.getBytes(ConfigurationConstants.DEFAULT_ENCODING_UTF_8.getValue()));
            Cipher cipher       = Cipher.getInstance(ALGO + "/CBC/PKCS5PADDING");
            Key keyObj          = null;
            keyObj              = new SecretKeySpec(generatedKey.getBytes(), ALGO);
            cipher.init(Cipher.DECRYPT_MODE, keyObj, iv);

            byte[] decodedData  = java.util.Base64.getDecoder().decode(data);
            byte[] decValue     = cipher.doFinal(decodedData);

            return new String(decValue);

        } catch (Exception e) {
            throw new Exception("Error during decryption process");

        }
    }

    public static String getCardNumber(String str,int start,int end){
        String mask= "";
        if(str !=null && !(str.isEmpty()) && (str.length()>=10)) {
            mask=str.substring(start, end);
        }
        return mask;
    }

    public static String  geDummyMobileNo()
    {  String randomMobileNo="";
        int num1, num2, num3; //3 numbers in area code
        int set2, set3; //sequence 2 and 3 of the phone number
        int low = 7;
        int high = 9;

        Random generator = new Random();

        num1 = generator.nextInt(high - low) + low;
        num2 = generator.nextInt(high - low) + low;
        num3 = generator.nextInt(high - low) + low;

        set2 = generator.nextInt(643) + 100;

        set3 = generator.nextInt(8999) + 1000;

        transactionlogger.error(num1 + "" + num2 + "" + num3 + set2 + set3);
        return randomMobileNo =num1 + "" + num2 + "" + num3 + set2 + set3;
    }


    public List getProductAndPricelist (ArrayList <Integer> priceList,int reqAmount) {
        HashMap <String,String> pricelistMap        = new HashMap();
        List <ProductDetailsVO> productList         = new ArrayList<>();
        int addedAmount         = 0;
        int tempamount          = 0;
        List<Integer> newList   = new ArrayList<>();
        int remainListValue     = 0;
        int a=1;

        for(int i: priceList){
            if(i <= reqAmount){
                newList.add(i);
            }
        }
        if(reqAmount <= 7000){
            Collections.shuffle(newList);
        }
        else{
            Collections.sort(newList,Collections.reverseOrder());
        }
        transactionlogger.error("newList--->" + newList);
        transactionlogger.error("reqAmount--->" + reqAmount);
        for(int i:newList){
            boolean flag=true;
            a=0;
            if (i <= reqAmount && tempamount+i<=reqAmount)
            {
                transactionlogger.error("in loop addedAmount-->" + addedAmount);
                transactionlogger.error("in loop i-->" + i);

                while(flag)
                {
                    if(tempamount+i*a<=reqAmount){
                        a++;
                    }
                    else{
                        flag    = false;
                        a       = a-1;
                        transactionlogger.error("product count x--> " + a);

                     //   pricelistMap.put(String.valueOf(i),String.valueOf(a));
                        pricelistMap.put(String.valueOf(i), String.valueOf(a));
                    }
                }
                tempamount      = tempamount + addedAmount + i*a;
                int remainder   =  reqAmount%tempamount;
                transactionlogger.error(" in loop tempamount-->" + tempamount);
                transactionlogger.error(" in loop remainder-->" + remainder);
                if(remainder == 0||remainder <= 100){
                    pricelistMap.put("remain", String.valueOf(remainder));
                    break;
                }
                if(newList.contains(remainder) && remainder < tempamount) {
                    transactionlogger.error("index value-->" + newList.get(newList.indexOf(remainder)));
                    remainListValue= newList.get(newList.indexOf(remainder));
                }
                else{
                    continue;
                }

                if (remainListValue>0){
                    tempamount = tempamount + remainListValue;
                    pricelistMap.put("remain", String.valueOf(remainListValue));
                }
                else{
                    addedAmount = tempamount;
                }
                transactionlogger.error(" in loop addedAmount-->" + addedAmount);
            }
            else{
                if(reqAmount==tempamount){
                    break;
                }
                transactionlogger.error(" in loop addedAmount-->" + addedAmount);

            }
        }
        if(addedAmount==0)
        {
            addedAmount = tempamount;

        }
        transactionlogger.error(" final addedAmount-->" + addedAmount);
        transactionlogger.error("pricelistMap-->" + pricelistMap);
        //  transactionlogger.error(" pricelistMap-->"+pricelistMap);
        if(pricelistMap.size() > 0 && pricelistMap != null){
        Iterator keys   = pricelistMap.keySet().iterator();
        while(keys.hasNext())
        {
            ProductDetailsVO productDetailsVO=new ProductDetailsVO();
            String key = String.valueOf(keys.next());
            productDetailsVO.setProductAmount(key);
            productDetailsVO.setProductUnit(pricelistMap.get(key));
            productDetailsVO.setShippingAmount(pricelistMap.get("remain"));

            transactionlogger.error("inside while key -->" + key);
            transactionlogger.error("inside while rbAmount.containsKey(key) -->" + rbAmount.containsKey(key));
            if (rbAmount.containsKey(key))
            {
                String[] productdetails = rbAmount.getString(key).split(":");
                productDetailsVO.setSKU(productdetails[0]);
                productDetailsVO.setProductName(productdetails[1]);
                productList.add(productDetailsVO);
            }

        }
        }

        return productList;
    }

    public boolean insertProductDetails(List <ProductDetailsVO> productList, String trackingid)
    {
        Connection conn = null;
        PreparedStatement pstmt2 = null;
        boolean flag=false;
        try
        {
            conn                = Database.getConnection();
            String insertQuery  = "INSERT INTO invoice_product_details (invoiceno,description,amount,quantity,productsku,tax) VALUES(?,?,?,?,?,?)";
            pstmt2              = conn.prepareStatement(insertQuery);
            conn.setAutoCommit(false);

            for(ProductDetailsVO productDetailsVO : productList){
                pstmt2.setInt(1, Integer.parseInt(trackingid));
                pstmt2.setString(2, productDetailsVO.getProductName());
                pstmt2.setString(3, productDetailsVO.getProductAmount());
                pstmt2.setString(4, productDetailsVO.getProductUnit());
                pstmt2.setString(5, productDetailsVO.getSKU());
                pstmt2.setString(6, productDetailsVO.getShippingAmount());

                pstmt2.addBatch();
            }

            transactionlogger.error("insertQuery product details ---" + insertQuery);
            transactionlogger.error("insertQuery product details ---" + pstmt2);
            int[]insertDetailCounts = pstmt2.executeBatch();
            if(insertDetailCounts.length > 0){
                flag = true;
                transactionlogger.error("inside if insertDetailCounts product details ---" + Arrays.toString(insertDetailCounts));
            }
        }
        catch (SystemError se)
        {
            transactionlogger.error("SystemError-->"+se);
        }
        catch (SQLException e)
        {
            transactionlogger.error("SQLException-->"+e);

        }
        finally
        {
            Database.closePreparedStatement(pstmt2);
            Database.closeConnection(conn);
        }
        return flag;
    }
    public static void sendHTMLMail(Hashtable values,String isCredential,String action,String oldinvoiceno,String invoiceno ) throws SystemError
    {


        transactionlogger.error("invoice no" + invoiceno);
        AsynchronousMailService mailService  = new AsynchronousMailService();
        List<ProductDetailsVO> listProducts  = (List<ProductDetailsVO>) values.get("listOfProducts");
        Functions functions                  = new Functions();

        String lateFee          = "";
        String duedate          = String.valueOf(values.get("duedate"));

        double afterExpAmount   = 0.0d;
        String afterExpAmount1  = "";

        transactionlogger.debug("latefee----" + String.valueOf(values.get("latefee")));
        if (functions.isValueNull(String.valueOf(values.get("latefee"))) && values.get("islatefee").equals("Y"))
        {
            lateFee = String.valueOf(values.get("latefee"));
        }
        else
        {
            lateFee="00.00";
        }

        HashMap mailValue   = new HashMap();

        mailValue.put(MailPlaceHolder.TOID,values.get("memberid"));
        mailValue.put(MailPlaceHolder.NAME,values.get("custname"));
        mailValue.put(MailPlaceHolder.DATE,values.get("TIME"));
        mailValue.put(MailPlaceHolder.INVOICENO,invoiceno);
        mailValue.put(MailPlaceHolder.ORDERID,values.get("orderid"));
        mailValue.put(MailPlaceHolder.DESC,values.get("orderdesc"));
        mailValue.put(MailPlaceHolder.AMOUNT,values.get("amount"));
        mailValue.put(MailPlaceHolder.CURRENCY,values.get("currency"));
        mailValue.put(MailPlaceHolder.CARDHOLDERNAME, values.get("custname"));
        mailValue.put(MailPlaceHolder.MERCHANTCOMPANYNAME,values.get("companyname"));
        mailValue.put(MailPlaceHolder.MCOMNAME,values.get("sitename"));
        mailValue.put(MailPlaceHolder.LATE_FEE, lateFee);
        mailValue.put(MailPlaceHolder.CustomerEmail, values.get("custemail"));
        mailValue.put(MailPlaceHolder.CTOKEN,values.get("ctoken").toString().substring(0, 16));
        mailValue.put(MailPlaceHolder.TERMINALID, values.get("TERMINALID"));
        mailValue.put(MailPlaceHolder.MerchantEmail, values.get("contact_emails"));
        mailValue.put(MailPlaceHolder.TERMINALID, values.get("TERMINALID"));
        mailValue.put(MailPlaceHolder.PAYMENTTERMS,values.get("paymentterms"));
        mailValue.put(MailPlaceHolder.DUE_DATE,values.get("duedate"));
        mailValue.put(MailPlaceHolder.LANG_FOR_INVOICE,values.get("langForInvoice"));

        transactionlogger.error("listProducts.size()  >>>>>>> "+listProducts.size());

        if(values.get("TERMINALID") != null)
        {
            mailValue.put(MailPlaceHolder.TERMINALID, values.get("TERMINALID"));
        }
        else
        {
            mailValue.put(MailPlaceHolder.TERMINALID,"");
        }
        if(functions.isValueNull(lateFee))
        {
            mailValue.put(MailPlaceHolder.LATE_FEE, values.get("latefee"));
            String amount   = (String) values.get("amount");
            afterExpAmount  = Double.parseDouble(amount) + Double.parseDouble(lateFee);

            afterExpAmount1 = String.valueOf(afterExpAmount);
            if (afterExpAmount != 0)
            {
                DecimalFormat twoPlaces = new DecimalFormat("0.00");
                afterExpAmount1         = twoPlaces.format(afterExpAmount);
            }
        }
        else
        {
            mailValue.put(MailPlaceHolder.LATE_FEE,"");
        }

        String mailMessage = null;
        transactionlogger.debug(action + values);
        if(listProducts.size() > 0)
        {
            StringBuffer mailBuffer         = new StringBuffer();
            StringBuffer productBuffer      = new StringBuffer();

            for (ProductDetailsVO productList : listProducts)
            {

                if (!functions.isValueNull(productList.getShippingAmount()))
                {
                    productList.setShippingAmount("0");
                }
                if (!functions.isValueNull(productList.getProductUnit()))
                {
                    productList.setProductUnit("0");
                }


               // productBuffer.append(functions.setTableDataforMail(productList.getProductDescription(), productList.getProductUnit(), productList.getProductAmount(), productList.getQuantity(), productList.getTax(), productList.getProductTotal(), "N"));
                productBuffer.append(functions.setTableDataforMail(productList.getProductName(), productList.getProductUnit(), productList.getProductAmount(), productList.getProductUnit(), productList.getShippingAmount(), productList.getProductAmount(), "N"));

            }
            // productBuffer.append(functions.setTableDataforMail("", "","","", "Tax Amount"+"("+values.get("gst")+"%)",(String)values.get("taxamount"),"Y"));
                    /*productBuffer.append(functions.setTableDataforMail("", "","","", "Grand Total",(String)values.get("amount"),"Y"));
                    if (values.get("islatefee").equals("Y"))
                    {
                        productBuffer.append(functions.setTableDataforMail("", "","","", " Amount After Due Date "+duedate+" Late Fee "+"("+values.get("currency")+ lateFee+")",afterExpAmount1,"Y"));
                    }*/
            // productBuffer.append("</table>");
            mailBuffer.append(productBuffer);
            // mailBuffer.append("<br>");
            mailValue.put(MailPlaceHolder.MULTIPALTRANSACTION, mailBuffer.toString());
            mailValue.put(MailPlaceHolder.GST, values.get("gst"));
            mailValue.put(MailPlaceHolder.TAXAMOUNT, values.get("taxamount"));
            mailValue.put(MailPlaceHolder.TOTALAMOUNT, values.get("amount"));
            mailValue.put(MailPlaceHolder.DUEDATE, duedate);
            mailValue.put(MailPlaceHolder.LATEFEE, values.get("currency")+" "+lateFee);
            mailValue.put(MailPlaceHolder.AFTEREXPAMOUNT, afterExpAmount1);
            transactionlogger.debug("mail buffer for invoice---" + mailBuffer);

        }
        else
        {
            mailValue.put(MailPlaceHolder.MULTIPALTRANSACTION, "");
        }
        Font f1 = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20);
        f1.setColor(Color.BLACK);

        Font f2 = FontFactory.getFont(FontFactory.TIMES_BOLD,15);
        f2.setColor(Color.WHITE);

        Font f3 = FontFactory.getFont(FontFactory.TIMES_BOLD,15);
        f3.setColor(Color.BLACK);
        //code for invoice PDF
        try
        {
            PayoutDAO payoutDAO         = new PayoutDAO();
            HashMap partnerDetails      = payoutDAO.getPartnerDetails(String.valueOf(values.get("memberid")));
            String partnerLogoName      = (String)partnerDetails.get("logoName");
            Document document  = new Document(PageSize.A3,40,40,40,40);
            SimpleDateFormat targetFormat       = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String invoiceFileName                  = ("Invoice"+invoiceno);
            List<ProductDetailsVO> productListHashMap    = (List<ProductDetailsVO>) values.get("listOfProducts");
            invoiceFileName = invoiceFileName+".pdf";
            File filePath   = new File(INVOICE_GENERATION_PATH + invoiceFileName);
            try
            {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));


            }
            catch ( DocumentException e)
            {
                //e.printStackTrace();
                transactionlogger.debug("DocumentException :" +e);
            }
            document.open();
            Image partnerImageInstance = Image.getInstance(PARTNER_LOGO_PATH + partnerLogoName);
            partnerImageInstance.scaleAbsolute(150f, 150f);
            //partnerImageInstance.scaleAbsoluteHeight(40f);
            partnerImageInstance.scaleAbsoluteWidth(partnerImageInstance.getWidth());

            Table table = null;
            table       = new Table(6);
            table.setWidth(100);
            table.setBorderColor(new Color(0, 0, 0));
            table.setPadding(1);
            Cell partnerNameCaptionCell = new Cell();
            partnerNameCaptionCell.setColspan(2);
            partnerNameCaptionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            partnerNameCaptionCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                   /* partnerNameCaptionCell.setLeading(10);*/
            MerchantDetailsVO merchantDetailsVO     = new MerchantDetailsVO();
            MerchantDAO merchantDAO                 = new MerchantDAO();
            merchantDetailsVO                       = merchantDAO.getMemberDetails(String.valueOf(values.get("memberid")));


            String country      = countryName.getString(merchantDetailsVO.getCountry());
            Paragraph paragraph1 = new Paragraph(merchantDetailsVO.getCompany_name());
            Paragraph paragraph2 = new Paragraph(merchantDetailsVO.getAddress()+ " "+merchantDetailsVO.getCity());
            Paragraph paragraph3 = new Paragraph(merchantDetailsVO.getState() +" "+ country);
            partnerNameCaptionCell.add(paragraph1);
            partnerNameCaptionCell.add(paragraph2);
            partnerNameCaptionCell.add(paragraph3);

            Cell reportingDateCaptionCe11 = new Cell(partnerImageInstance);
            reportingDateCaptionCe11.setColspan(2);
            //reportingDateCaptionCe11.setBackgroundColor(Color.white.brighter());
            reportingDateCaptionCe11.setHorizontalAlignment(Element.ALIGN_CENTER);
            reportingDateCaptionCe11.setVerticalAlignment(Element.ALIGN_MIDDLE);

            Cell partnerLogoCell = new Cell(new Paragraph(("INVOICE:#"+invoiceno),f1));
            partnerLogoCell.setColspan(2);
            partnerLogoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            partnerLogoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            table.addCell(partnerNameCaptionCell);
            table.addCell(reportingDateCaptionCe11);
            table.addCell(partnerLogoCell);

            Cell invoicedetails = new Cell("Order Details");
            invoicedetails.setColspan(3);
            invoicedetails.setHorizontalAlignment(Element.ALIGN_CENTER);
            invoicedetails.setVerticalAlignment(Element.ALIGN_CENTER);
            invoicedetails.setBackgroundColor(Color.gray.brighter());

            Cell customerdetails    = new Cell("Customer Details");
            customerdetails.setColspan(3);
            customerdetails.setHorizontalAlignment(Element.ALIGN_CENTER);
            customerdetails.setVerticalAlignment(Element.ALIGN_CENTER);
            customerdetails.setBackgroundColor(Color.gray.brighter());

            table.addCell(invoicedetails);
            table.addCell(customerdetails);

            Cell partnerNameLabel   = new Cell("Order ID:" +values.get("orderid"));
            String custName         = values.get("custname")!=null? (String) values.get("custname") :"";
            Cell partnerNameValue   = new Cell("Customer Name:"+custName);
            partnerNameLabel.setColspan(3);
            partnerNameValue.setColspan(3);

            table.addCell(partnerNameLabel);
            table.addCell(partnerNameValue);

            Cell memberIdLabel  = new Cell("Order Date/Invoice Date: "+values.get("TIME"));
            Cell memberIdValue  = new Cell("Customer Email:"+values.get("custemail"));

            memberIdLabel.setColspan(3);
            memberIdValue.setColspan(3);

            table.addCell(memberIdLabel);
            table.addCell(memberIdValue);

            String custaddress="";
            if (functions.isValueNull(String.valueOf(values.get("address"))))
            {
                custaddress= String.valueOf(values.get("address"));
            }

            Cell terminalIdLabel    = new Cell("Order Description: "+values.get("orderdesc"));
            Cell terminalValue      = new Cell("Customer Address: "+custaddress);

            terminalIdLabel.setColspan(3);
            terminalValue.setColspan(3);

            table.addCell(terminalIdLabel);
            table.addCell(terminalValue);

            if (listProducts.size()>0)
            {
                String taxamounts="";
                if (functions.isValueNull(String.valueOf(values.get("gst"))))
                {
                    taxamounts  = String.valueOf(values.get("gst"));
                }

                String phonecc  = "";
                if (functions.isValueNull(String.valueOf(values.get("phonecc"))))
                {
                    phonecc = (String.valueOf(values.get("phonecc")));
                }
                Cell invoicegst         = new Cell("GST/VAT: " + "" + taxamounts + "%");
                Cell customerphone      = new Cell("Customer TelNo:" +phonecc + "-" + values.get("phone"));
                invoicegst.setColspan(3);
                customerphone.setColspan(3);

                table.addCell(invoicegst);
                table.addCell(customerphone);
            }
            else
            {
                String phonecc = "";
                if (functions.isValueNull(String.valueOf(values.get("phonecc"))))
                {
                    phonecc=(String.valueOf(values.get("phonecc")));
                }

                Cell invoicegst     = new Cell("GST/VAT:"+"");
                Cell customerphone  = new Cell("Customer TelNo:" + phonecc + "-" + values.get("phone"));

                invoicegst.setColspan(3);
                customerphone.setColspan(3);
                table.addCell(invoicegst);
                table.addCell(customerphone);

            }


            if(listProducts.size()>0)
            {

                String str = null;
                byte[] utf8;

                String cuurrency ="";
                Locale usLocale = Locale.US;
                Locale ukLocale = Locale.UK;
                Locale frLocale = Locale.FRANCE;
                Locale jpLocale = Locale.JAPAN;
                Locale cdLocale = Locale.CANADA;


                Currency currency = Currency.getInstance(usLocale);
                Currency currency1 = Currency.getInstance(ukLocale);
                Currency currency2 = Currency.getInstance(frLocale);
                Currency currency3 = Currency.getInstance(jpLocale);
                Currency currency4 = Currency.getInstance(cdLocale);

                if (values.get("currency").equals("USD"))
                {
                    cuurrency =currency.getSymbol(usLocale);
                }
                if (values.get("currency").equals("GBP"))
                {
                    cuurrency =currency1.getSymbol(ukLocale);
                }
                if (values.get("currency").equals("EUR"))
                {
                    cuurrency =currency2.getSymbol(frLocale);
                }
                if (values.get("currency").equals("JPY"))
                {
                    str = "\u00A5";
                    utf8 = str.getBytes("UTF-8");
                    cuurrency = new String(utf8, "UTF-8");
                }
                if (values.get("currency").equals("CAD"))
                {
                    cuurrency =currency4.getSymbol(cdLocale);
                }


                Cell invoiceProductList = new Cell("Invoice Product List");
                invoiceProductList.setColspan(6);
                invoiceProductList.setHorizontalAlignment(Element.ALIGN_CENTER);
                invoiceProductList.setBackgroundColor(Color.gray.brighter());
                table.addCell(invoiceProductList);

                Cell invoicedesclabel       = new Cell("Product");
                Cell invoiceunitlabel       = new Cell("Unit");
                Cell invoiceamountlabel     = new Cell("Amount");
                Cell invoicequantitylabel   = new Cell("Quantity");
                Cell invoicetaxlabel        = new Cell("Tax(%)");
                Cell invoicesublabel        = new Cell("Sub Total");


                invoicedesclabel.setBackgroundColor(Color.gray.brighter());
                invoiceunitlabel.setBackgroundColor(Color.gray.brighter());
                invoiceamountlabel.setBackgroundColor(Color.gray.brighter());
                invoicequantitylabel.setBackgroundColor(Color.gray.brighter());
                invoicetaxlabel.setBackgroundColor(Color.gray.brighter());
                invoicesublabel.setBackgroundColor(Color.gray.brighter());

                invoicedesclabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                invoiceunitlabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                invoiceamountlabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                invoicequantitylabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                invoicetaxlabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                invoicesublabel.setHorizontalAlignment(Element.ALIGN_CENTER);

                table.addCell(invoicedesclabel);
                table.addCell(invoiceunitlabel);
                table.addCell(invoiceamountlabel);
                table.addCell(invoicequantitylabel);
                table.addCell(invoicetaxlabel);
                table.addCell(invoicesublabel);

                Cell invoiceDescvalue, invoiceUnitvalue, invoiceAmountvalue, invoiceQuanityvalue, invoiceTaxvalue, invoiceSubtotalvalue;

                double addtax           = 0;
                int quantityy           = 0;
                double producttotals    = 0;
                String productfinal     ="";
                for (int i = 0; i < listProducts.size(); i++)
                {
                    ProductDetailsVO productDetailsVO = listProducts.get(i);
                    //invoiceDescvalue = new Cell(productList.getProductDescription());
                    invoiceDescvalue = new Cell(productDetailsVO.getProductName());
                    String unit      = "";
                    if (functions.isValueNull(productDetailsVO.getProductUnit()))
                    {
                        unit = productDetailsVO.getProductUnit();
                    }

                    transactionlogger.error("productDetailsVO.getProductAmount() "+productDetailsVO.getProductAmount());
                    transactionlogger.error("productDetailsVO.getShippingAmount() "+productDetailsVO.getShippingAmount());
                    String prodtotal    = productDetailsVO.getProductAmount();

                    invoiceUnitvalue        = new Cell(""+(i+1));
                    invoiceAmountvalue      = new Cell(cuurrency+" "+productDetailsVO.getProductAmount());
                    invoiceQuanityvalue     = new Cell(productDetailsVO.getProductUnit());
                    invoiceTaxvalue         = new Cell(productDetailsVO.getShippingAmount());
                    //invoiceSubtotalvalue    = new Cell(cuurrency+" "+productDetailsVO.getProductAmount());
                    invoiceSubtotalvalue    = new Cell(cuurrency+" "+(Double.parseDouble(prodtotal) * Integer.parseInt(productDetailsVO.getProductUnit())));

                    double TotalAmount = Double.parseDouble(prodtotal) * Integer.parseInt(productDetailsVO.getProductUnit());
                    addtax              += Double.parseDouble(productDetailsVO.getShippingAmount());
                    quantityy           += Integer.parseInt(productDetailsVO.getProductUnit());
                    //producttotals       += Double.parseDouble(prodtotal);
                    producttotals       += TotalAmount;
                    productfinal        = String.format("%.2f", producttotals);

                    invoiceDescvalue.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceUnitvalue.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceAmountvalue.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceQuanityvalue.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceTaxvalue.setHorizontalAlignment(Element.ALIGN_CENTER);
                    invoiceSubtotalvalue.setHorizontalAlignment(Element.ALIGN_CENTER);

                    table.addCell(invoiceDescvalue);
                    table.addCell(invoiceUnitvalue);
                    table.addCell(invoiceAmountvalue);
                    table.addCell(invoiceQuanityvalue);
                    table.addCell(invoiceTaxvalue);
                    table.addCell(invoiceSubtotalvalue);
                }
                Cell total  = new Cell("Total");
                total.setColspan(3);
                total.setHorizontalAlignment(Element.ALIGN_CENTER);


                Cell quantity = new Cell(String.valueOf(quantityy));
                quantity.setColspan(1);
                quantity.setBackgroundColor(Color.white.brighter());
                quantity.setHorizontalAlignment(Element.ALIGN_CENTER);

                Cell amount = new Cell(String.valueOf(addtax));
                amount.setColspan(1);
                amount.setHorizontalAlignment(Element.ALIGN_CENTER);

                Cell tax = new Cell(String.valueOf(cuurrency+" "+productfinal));
                tax.setColspan(1);
                tax.setHorizontalAlignment(Element.ALIGN_CENTER);

                table.addCell(total);
                table.addCell(quantity);
                table.addCell(amount);
                table.addCell(tax);

                Cell partnerCommissionAmountLabel = new Cell("Tax Amount");
                //Cell partnerCommissionAmountValue = new Cell(cuurrency+" "+String.valueOf(values.get("taxamount")));
                Cell partnerCommissionAmountValue = new Cell(cuurrency+" "+String.valueOf(addtax));

                partnerCommissionAmountLabel.setColspan(5);
                partnerCommissionAmountLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                partnerCommissionAmountValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(partnerCommissionAmountLabel);
                table.addCell(partnerCommissionAmountValue);
            }
            else
            {
                Cell invoicedetail = new Cell("Invoice Details");
                invoicedetail.setColspan(6);
                invoicedetail.setHorizontalAlignment(Element.ALIGN_CENTER);
                invoicedetail.setBackgroundColor(Color.gray.brighter());
                table.addCell(invoicedetail);
            }
            transactionlogger.error("log 9" );


            String currencySymbol = null;
            String str = null;
            byte[] utf8;
            String cuurrency="";
            Locale usLocale = Locale.US;
            Locale ukLocale = Locale.UK;
            Locale frLocale = Locale.FRANCE;
            Locale jpLocale = Locale.JAPAN;
            Locale cdLocale = Locale.CANADA;

            Currency currency = Currency.getInstance(usLocale);
            Currency currency1 = Currency.getInstance(ukLocale);
            Currency currency2 = Currency.getInstance(frLocale);
            Currency currency3 = Currency.getInstance(jpLocale);
            Currency currency4 = Currency.getInstance(cdLocale);

            if (values.get("currency").equals("USD"))
            {
                cuurrency =currency.getSymbol(usLocale);
            }
            if (values.get("currency").equals("GBP"))
            {
                cuurrency =currency1.getSymbol(ukLocale);
            }
            if (values.get("currency").equals("EUR"))
            {
                cuurrency =currency2.getSymbol(frLocale);
            }
            if (values.get("currency").equals("JPY"))
            {

                str = "\u00A5";
                utf8 = str.getBytes("UTF-8");
                cuurrency = new String(utf8, "UTF-8");

            }
            if (values.get("currency").equals("CAD"))
            {
                cuurrency =currency4.getSymbol(cdLocale);
            }

            transactionlogger.error("log 10" );
            Cell partnerChargesAmountLabel  = new Cell("Total Amount");
            Cell partnerChargesAmountValue  = new Cell(new Paragraph(cuurrency+" "+String.valueOf(values.get("amount")),f3));


            partnerChargesAmountLabel.setColspan(5);
            partnerChargesAmountValue.setHorizontalAlignment(Element.ALIGN_CENTER);
            partnerChargesAmountLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(partnerChargesAmountLabel);
            table.addCell(partnerChargesAmountValue);


            if (values.get("islatefee").equals("Y"))
            {
                Cell lateFeeLabel = new Cell("Amount After Due Date " + duedate + " Late Fee " + "(" + cuurrency+" "+ lateFee + ")");
                Cell lateFeeValue = new Cell(new Paragraph(cuurrency+" "+ afterExpAmount1,f3));
                lateFeeLabel.setColspan(5);

                lateFeeValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                lateFeeLabel.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(lateFeeLabel);
                table.addCell(lateFeeValue);
            }
            document.add(table);
            Paragraph paragraph     = new Paragraph();
            paragraph.setAlignment(Element.ALIGN_CENTER);
            Chunk chunk             = new Chunk("This is a computer generated invoice. No signature required.");
            paragraph.add(chunk);
            document.add(paragraph);

            MerchantDetailsVO merchantDetailsVO1    = new MerchantDetailsVO();
            MerchantDAO merchantDAO1                = new MerchantDAO();
            merchantDetailsVO1                      = merchantDAO1.getMemberDetails(String.valueOf(values.get("memberid")));


            if (merchantDetailsVO1.getIsPoweredBy().equals("Y"))
            {
                Image poweredByLogoValue = Image.getInstance(PARTNER_LOGO_PATH + "poweredby_new_logo.png");
                poweredByLogoValue.setAlignment(Element.ALIGN_RIGHT);
                document.add(poweredByLogoValue);
            }
            document.close();


        }
        catch (Exception ex)
        {
            transactionlogger.error("Exception >>>>>>>>>>>>>>>>>> "+ex);
            ex.printStackTrace();

        }
        if(action.equals(NEW))
        {
            mailValue.put(MailPlaceHolder.MSG," ");
            mailValue.put(MailPlaceHolder.INVOICE_URL,"");
            mailValue.put(MailPlaceHolder.ATTACHMENTFILENAME,"Invoice"+invoiceno+".pdf");
            mailValue.put(MailPlaceHolder.ATTACHMENTFILEPATH,INVOICE_GENERATION_PATH+"Invoice"+invoiceno+".pdf");
            mailService.sendMerchantMonitoringAlert(MailEventEnum.GENERATING_INVOICE_BY_MERCHANT, mailValue);

        }
        else if(action.equals(REGENERATE))
        {
            mailValue.put(MailPlaceHolder.MSG,values.get("MSG"));
            mailValue.put(MailPlaceHolder.ATTACHMENTFILENAME,"Invoice"+invoiceno+".pdf");
            mailValue.put(MailPlaceHolder.ATTACHMENTFILEPATH,INVOICE_GENERATION_PATH+"Invoice"+invoiceno+".pdf");
            mailService.sendMerchantMonitoringAlert(MailEventEnum.GENERATING_INVOICE_BY_MERCHANT,mailValue);
        }
        else if(action.equals(CANCEL))
        {
            mailValue.put(MailPlaceHolder.MSG,values.get("MSG"));
            mailService.sendMerchantMonitoringAlert(MailEventEnum.CANCELED_INVOICE,mailValue);
        }
    }

    public  static  byte[] generateHmacSHA256(String algorithm, byte[] key, byte[] message)
    {
        Mac mac = null;
        try
        {
            mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key,algorithm));
        }
        catch (Exception e)
        {
            transactionlogger.error("Exception while cretaing hmac --> " + e);
        }
        return mac.doFinal(message);
    }

    public static String bytesToHex(byte[] hashInBytes)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hashInBytes.length; i++) {
            sb.append(Integer.toString((hashInBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String getHmac256Signature(String message, byte[] key)
    {
        byte[] bytes = generateHmacSHA256("HmacSHA256", key, message.getBytes());
        return bytesToHex(bytes).toUpperCase();
    }




}