package com.payment.tojika;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.*;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by Admin on 14-03-2019.
 */
public class TojikaUtils
{
    static TransactionLogger transactionLogger= new TransactionLogger(TojikaUtils.class.getName());

    public static  String getAuthTokenTojika(String id,String subject,String secretKey)
    {
        transactionLogger.debug("Inside getAuthTokenTojika");
        Algorithm algorithmHS = null;
        String token = null;
        try
        {
            algorithmHS = Algorithm.HMAC256(secretKey.getBytes());

            token = JWT.create()
                    .withSubject(subject)
                    .withClaim("jti",id)
                    .sign(algorithmHS);
        }
        catch (JWTCreationException exception)
        {
            transactionLogger.debug("JWTCreationException ---"+exception);
        }
        return token;
    }

    public static boolean verifyToken(String token,String secretKey)
    {
        try
        {
            Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
            transactionLogger.debug("algorithm is -------------"+algorithm);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);

            transactionLogger.debug("decode jwt ----" + jwt);
        } catch (JWTVerificationException exception){
            //Invalid signature/claims
            transactionLogger.debug("JWTVerificationException ---"+exception);
            return false;
        }

        return true;
    }


    public String getRedirectForm(String trackingId, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed..... redirect url---" + response3D.getRedirectUrl());
        transactionLogger.debug("trackingid inside Tojika Utils-------"+trackingId);

        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(response3D.getRedirectUrl()).append("\" method=\"post\">\n");
        //  html.append("<input type=\"hidden\" name=\"clientid\" value=\"" + response3D.getRequestMap().get("clientid") + "\">");
        //  html.append("<input type=\"hidden\" name=\"storetype\" value=\"3d\">");
        // html.append("<input type=\"hidden\" name=\"hash\" value=\"" + response3D.getRequestMap().get("hash") + "\">");
        html.append("<input type=\"hidden\" name=\"vendor_id\" value=\"" + response3D.getRequestMap().get("vendor_id") + "\">");
        html.append("<input type=\"hidden\" name=\"message\" value=\"" + response3D.getRequestMap().get("message") + "\">");
        html.append("<input type=\"hidden\" name=\"amount\" value=\"" +response3D.getRequestMap().get("amount")+ "\">");
        html.append("<input type=\"hidden\" name=\"currency\" value=\"" +response3D.getRequestMap().get("currency")+ "\">");
        html.append("<input type=\"hidden\" name=\"txid\" value=\"" + trackingId + "\">");
        html.append("<input type=\"hidden\" name=\"countrycode\" value=\"" +response3D.getRequestMap().get("countrycode")+ "\">");
        html.append("<input type=\"hidden\" name=\"isTest\" value=\"" +response3D.getRequestMap().get("isTest")+ "\">");
        html.append("<input type=\"hidden\" name=\"fail\" value=\""+response3D.getRequestMap().get("fail")+"\">");
        html.append("<input type=\"hidden\" name=\"success\" value=\""+response3D.getRequestMap().get("success")+"\">");
        html.append("<input type=\"hidden\" name=\"firstname\" value=\"" +response3D.getRequestMap().get("firstname")+ "\">");
        html.append("<input type=\"hidden\" name=\"surname\" value=\"" +response3D.getRequestMap().get("surname")+ "\">");
        html.append("<input type=\"hidden\" name=\"address\" value=\"" + response3D.getRequestMap().get("address") + "\">");
        html.append("<input type=\"hidden\" name=\"city\" value=\"" +response3D.getRequestMap().get("city")+ "\">");
        html.append("<input type=\"hidden\" name=\"postcode\" value=\"" +response3D.getRequestMap().get("postcode")+ "\">");
        html.append("<input type=\"hidden\" name=\"email\" value=\"" +response3D.getRequestMap().get("email")+ "\">");
        html.append("<input type=\"hidden\" name=\"jwt\" value=\"" +response3D.getRequestMap().get("jwt")+ "\">");
        html.append("<input type=\"hidden\" name=\"encoding\" value=\"utf-8\">");
        html.append("</form>\n");

        transactionLogger.debug("form----"+html.toString());
        return html.toString();
    }

    public static CommRequestVO getTojikaHPPRequest(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.debug("Inside TojikaUtils getTojikaHPPRequest -----");
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commAddressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        commAddressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        commAddressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        commAddressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        commAddressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        commAddressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        commAddressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());

        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);
        commRequestVO.setAddressDetailsVO(commAddressDetailsVO);

        return commRequestVO;
    }

}
