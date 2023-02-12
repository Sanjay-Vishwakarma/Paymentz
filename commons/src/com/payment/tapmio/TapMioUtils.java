package com.payment.tapmio;

import com.directi.pg.Base64;
import com.directi.pg.Database;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.emexpay.vo.request;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Admin on 5/14/2020.
 */
public class TapMioUtils
{

    private static TransactionLogger transactionlogger = new TransactionLogger(TapMioUtils.class.getName());

        public  String doHttpPostConnection(String url, String TapmioAuthentication)
        {
            String result = "";
            PostMethod post = new PostMethod(url);
            try
            {
                HttpClient httpClient = new HttpClient();
                post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                post.addRequestHeader("Tapmio-Authentication", TapmioAuthentication);
              //  post.setRequestBody(request);
                httpClient.executeMethod(post);
                String response = new String(post.getResponseBody());
                result = response;
            }
            catch (HttpException e)
            {
                transactionlogger.error("HttpException---->",e);
            }
            catch (IOException e)
            {
                transactionlogger.error("IOException---->", e);
            }


            return result;
        }


    public CommRequestVO getTapMioRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getProcessorName());


        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        return commRequestVO;
    }

   public Boolean updateTransaction (String trackingid, String paymentid){

       transactionlogger.error("in side  updateTransaction----------->");
       Connection con = null;
       PreparedStatement psUpdateTransaction = null;
       boolean isUpdate=false;
       try
       {

            con= Database.getConnection();
           String update = "update transaction_common set paymentid = ? where trackingid=?";
           psUpdateTransaction = con.prepareStatement(update.toString());
           psUpdateTransaction.setString(1,paymentid);
           psUpdateTransaction.setString(2,trackingid);
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
           Database.closeConnection(con);
       }
       return isUpdate;

   }
}
