package com.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.directi.pg.Logger;
import org.owasp.esapi.ESAPI;

import java.util.Date;

/**
 * Created by Admin on 6/7/2017.
 */
public class AuthFunctions
{
    private static Logger log = new Logger(AuthFunctions.class.getName());

    //private final static ResourceBundle RB = LoadProperties.getProperty("com.auth.Auth");

    /**
     * Auth Token using key only without username and role
     * @return
     */
    public String getAuthToken()
    {

        Algorithm algorithmHS = null;
        String token = null;

        Date date = new Date();
        long t = date.getTime();
        Date expirationTime = new Date(t + 3600000l);
        //HMAC
        try
        {
            algorithmHS = Algorithm.HMAC256(ESAPI.securityConfiguration().getMasterKey());

            token = JWT.create()
                    .withIssuer("PZ")
                    .withExpiresAt(expirationTime)
                    .sign(algorithmHS);
        }
        catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.

            log.error("JWTCreationException in getAuthToken---", exception);
        }


        return token;

    }

    /**
     * Auth Token with username but without role
     * @param username
     * @return
     */
    public String getAuthToken(String username)
    {

        Algorithm algorithmHS = null;
        String token = null;
        Date date = new Date();
        long t = date.getTime();
        Date expirationTime = new Date(t + 3600000l);
        //HMAC
        try
        {
            algorithmHS = Algorithm.HMAC256(ESAPI.securityConfiguration().getMasterKey());

            token = JWT.create()
                    .withIssuer("PZ")
                    .withExpiresAt(expirationTime)
                    .withSubject(username)
                    .sign(algorithmHS);
        }
        catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
            log.error("JWTCreationException in getAuthToken(String username)---", exception);
        }


        return token;

    }

    /**
     * Auth Token with username and role
     * @param username
     * @param role
     * @return
     */
    public String getAuthToken(String username, String role)
    {

        Algorithm algorithmHS = null;
        String token = null;
        Date date = new Date();
        long t = date.getTime();
        Date expirationTime = new Date(t + 3600000l);
        //HMAC
        try
        {
            algorithmHS = Algorithm.HMAC256(ESAPI.securityConfiguration().getMasterKey());

            token = JWT.create()
                    .withIssuer("PZ")
                    .withExpiresAt(expirationTime)
                    .withSubject(username)
                    .withClaim("role",role)
                    .sign(algorithmHS);
        }
        catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
            log.error("JWTCreationException in getAuthToken(String username, String role)---", exception);
        }


        return token;

    }
    public String getAuthToken(String username, String role,String merchantId,String partnerId)
    {
        Algorithm algorithmHS = null;
        String token = null;
        Date date = new Date();
        long t = date.getTime();
        Date expirationTime = new Date(t + 3600000l);
        //HMAC
        try
        {
            algorithmHS = Algorithm.HMAC256(ESAPI.securityConfiguration().getMasterKey());

            token = JWT.create()
                    .withIssuer("PZ")
                    .withExpiresAt(expirationTime)
                    .withSubject(username)
                    .withClaim("role", role)
                    .withClaim("merchantid",merchantId)
                    .withClaim("partnerid",partnerId)
                    .sign(algorithmHS);
        }
        catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
            log.error("JWTCreationException in getAuthToken(String username, String role)---", exception);
        }
        return token;

    }
    /**
     * Verify token with key only
     * @param token
     * @return
     */
    public boolean verifyToken(String token)
    {
        try {
            Algorithm algorithm = Algorithm.HMAC256(ESAPI.securityConfiguration().getMasterKey());
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("PZ")
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
        } catch (JWTVerificationException exception){
            //Invalid signature/claims
            log.error("JWTVerificationException in verifyToken(String token)---", exception);
            return false;
        }

        return true;
    }

    /**
     * Verify token with key and username
     * @param token
     * @param username
     * @return
     */
    public boolean verifyToken(String token,String username)
    {
        try {
            Algorithm algorithm = Algorithm.HMAC256(ESAPI.securityConfiguration().getMasterKey());
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("PZ")
                    .withSubject(username)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
        } catch (JWTVerificationException exception){
            //Invalid signature/claims
            log.error("JWTVerificationException in verifyToken(String token,String username)---", exception);
            return false;
        }

        return true;
    }

    /**
     * Verify token with username and role
     * @param token
     * @param username
     * @param role
     * @return
     */
    public boolean verifyToken(String token,String username,String role)
    {


        try {
            Algorithm algorithm = Algorithm.HMAC256(ESAPI.securityConfiguration().getMasterKey());
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("PZ")
                    .withSubject(username)
                    .withClaim("role",role)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);

        }
        catch (TokenExpiredException e)
        {
            log.error("TokenExpiredException in verifyToken(String token,String username,String role)-----",e);
            return false;
        }
        catch (JWTVerificationException exception)
        {
            //Invalid signature/claims
            log.error("JWTVerificationException in verifyTokenverifyToken(String token,String username,String role)---",exception);
            return false;
        }
        /*catch (Exception e)
        {
            log.error("JWTDecodeException in verifyToken---",e);
            return false;
        }*/

        return true;
    }

    /**
     * Verify token with username and role
     * @param token
     * @param username
     * @param role
     * @return
     */
    public boolean verifyExpiry(String token,String username,String role)
    {


        try {
            Algorithm algorithm = Algorithm.HMAC256(ESAPI.securityConfiguration().getMasterKey());
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("PZ")
                    .withSubject(username)
                    .withClaim("role",role)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);

        }
        catch (TokenExpiredException e)
        {
            log.error("TokenExpiredException in verifyExpiry(String token,String username,String role)-----",e);
            return true;
        }
        catch (JWTVerificationException exception)
        {
            //Invalid signature/claims
            log.error("JWTVerificationException in verifyExpiry(String token,String username,String role)---",exception);
            return false;
        }
        /*catch (Exception e)
        {
            log.error("JWTDecodeException in verifyToken---",e);
            return false;
        }*/

        return true;
    }

    public String getUserName(String token)
    {
        String username;
        try
        {
            log.error("token in getUserName---"+token);
            DecodedJWT jwt = JWT.decode(token);
            username = jwt.getSubject();
        } catch (JWTDecodeException exception){
            //Invalid token
            log.error("JWTDecodeException in getUserName(String token)---",exception);
            return null;
        }

        return username;
    }

    public String getUserRole(String token)
    {
        String role;
        try
        {
            DecodedJWT jwt = JWT.decode(token);
            role = jwt.getClaim("role").asString();

        }
        catch (JWTDecodeException exception){
            //Invalid token
            log.error("JWTDecodeException in getUserRole(String token)---",exception);
            return null;
        }

        return role;
    }
    public String getMerchantId(String token)
    {
        String role;
        try
        {
            DecodedJWT jwt = JWT.decode(token);
            role = jwt.getClaim("merchantid").asString();

        }
        catch (JWTDecodeException exception){
            //Invalid token
            log.error("JWTDecodeException in getUserRole(String token)---",exception);
            return null;
        }

        return role;
    }

    public String getPartnerId(String token)
    {
        String role;
        try
        {
            DecodedJWT jwt = JWT.decode(token);
            role = jwt.getClaim("partnerid").asString();

        }
        catch (JWTDecodeException exception){
            //Invalid token
            log.error("JWTDecodeException in getUserRole(String token)---",exception);
            return null;
        }

        return role;
    }



    public String getUserKey(String token)
    {
        String key;
        try
        {
            log.error("token in getUserName---"+token);
            DecodedJWT jwt = JWT.decode(token);
            key = jwt.getSubject();
        } catch (JWTDecodeException exception){
            //Invalid token
            log.error("JWTDecodeException in getUserName(String token)---",exception);
            return null;
        }

        return key;
    }

    public boolean verifyTokenWithKey(String token,String key,String role)
    {
        try {
            Algorithm algorithm = Algorithm.HMAC256(ESAPI.securityConfiguration().getMasterKey());
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("PZ")
                    .withSubject(key)
                    .withClaim("role",role)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);

        }
        catch (TokenExpiredException e)
        {
            log.error("TokenExpiredException in verifyToken(String token,String username,String role)-----",e);
            return false;
        }
        catch (JWTVerificationException exception)
        {
            //Invalid signature/claims
            log.error("JWTVerificationException in verifyTokenverifyToken(String token,String username,String role)---",exception);
            return false;
        }
        /*catch (Exception e)
        {
            log.error("JWTDecodeException in verifyToken---",e);
            return false;
        }*/

        return true;
    }

    public String getAuthTokenWithKey(String key, String role)
    {

        Algorithm algorithmHS = null;
        String token = null;
        Date date = new Date();
        long t = date.getTime();
        Date expirationTime = new Date(t + 3600000l);
        //HMAC
        try
        {
            algorithmHS = Algorithm.HMAC256(ESAPI.securityConfiguration().getMasterKey());

            token = JWT.create()
                    .withIssuer("PZ")
                    .withExpiresAt(expirationTime)
                    .withSubject(key)
                    .withClaim("role",role)
                    .sign(algorithmHS);
        }
        catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
            log.error("JWTCreationException in getAuthToken(String username, String role)---", exception);
        }


        return token;

    }


    public static void main(String[] args)
    {
        AuthFunctions a = new AuthFunctions();
        System.out.println("token---"+a.getAuthToken("MxLroANjyBS36KyWSHcOLByIgBijxei2"));
    }
}
