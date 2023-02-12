package com.payment.cajarural;

import com.directi.pg.Database;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.*;
import java.security.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Admin on 11/30/2021.
 */
public class CajaRuralUtils
{

    private static TransactionLogger transactionLogger=new TransactionLogger(CajaRuralUtils.class.getName());


    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }
    public CommRequestVO getCajaruralRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO             = new CommRequestVO();
        CommAddressDetailsVO addressDetailsVO   = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO         = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO           = new CommMerchantVO();

        commMerchantVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());

        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getProcessorName());
        commRequestVO.setCustomerId(commonValidatorVO.getVpa_address());
        transactionLogger.error("utils vpa--->" + commonValidatorVO.getVpa_address());

        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }


    public static CommRequestVO getCommRequestFromUtils(CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("inside cajaruralutils getCommRequestFromUtils");
        CommRequestVO commRequestVO = new CommRequestVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();

        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();

        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();

        addressDetailsVO.setFirstname(genericAddressDetailsVO.getFirstname());
        addressDetailsVO.setLastname(genericAddressDetailsVO.getLastname());
        addressDetailsVO.setStreet(genericAddressDetailsVO.getStreet());
        addressDetailsVO.setCity(genericAddressDetailsVO.getCity());
        addressDetailsVO.setPhone(genericAddressDetailsVO.getPhone());
        addressDetailsVO.setState(genericAddressDetailsVO.getState());
        addressDetailsVO.setIp(genericAddressDetailsVO.getIp());
        addressDetailsVO.setEmail(genericAddressDetailsVO.getEmail());
        addressDetailsVO.setZipCode(genericAddressDetailsVO.getZipCode());
        addressDetailsVO.setCountry(genericAddressDetailsVO.getCountry());

        commMerchantVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setCustomerId(commonValidatorVO.getCustomerId());

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setCommMerchantVO(commMerchantVO);

        return commRequestVO;
    }

    public static String generateAutoSubmitForm(Comm3DResponseVO commResponseVO)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(commResponseVO.getUrlFor3DRedirect()).append("\" method=\"POST\">\n");
        html.append("</form>\n");
        return html.toString();
    }



    private final static String charset = "UTF-8";



    /** Numero de bytes para obtener cadenas multiplos de 8 */
    private final short OCHO = 8;

    /** Constante de array de inicializacin */
    private final byte [] IV = {0, 0, 0, 0, 0, 0, 0, 0};

    /** Array de DatosEntrada */
    private JSONObject jsonObj = new JSONObject();

    /** Set parameter */
    public void setParameter(final String key, final String value) throws org.json.JSONException
    {
        jsonObj.put(key, value);
    }

    /** Get parameter */
    public String getParameter(final String key) throws org.json.JSONException
    {
        return jsonObj.getString(key);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////// 					FUNCIONES AUXILIARES: 											  ///////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    /** 3DES Function
     * @throws java.security.InvalidKeyException
     * @throws javax.crypto.NoSuchPaddingException
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.InvalidAlgorithmParameterException
     * @throws java.io.UnsupportedEncodingException
     * @throws javax.crypto.BadPaddingException
     * @throws javax.crypto.IllegalBlockSizeException */
    public byte [] encrypt_3DES(final String claveHex, final String datos) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException
    {
        byte [] ciphertext = null;
        // Crea la clave
        DESedeKeySpec desKeySpec = new DESedeKeySpec(toByteArray(claveHex));
        SecretKey desKey = new SecretKeySpec(desKeySpec.getKey(), "DESede");
        // Crea un cifrador
        Cipher desCipher = Cipher.getInstance("DESede/CBC/NoPadding");

        // Inicializa el cifrador para encriptar
        desCipher.init(Cipher.ENCRYPT_MODE, desKey, new IvParameterSpec(IV));

        // Se aaden los 0 en bytes necesarios para que sea un mltiplo de 8
        int numeroCerosNecesarios = OCHO - (datos.length() % OCHO);
        if (numeroCerosNecesarios == OCHO) {
            numeroCerosNecesarios = 0;
        }
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        array.write(datos.getBytes("UTF-8"), 0, datos.length());
        for (int i = 0; i < numeroCerosNecesarios; i++) {
            array.write(0);
        }
        byte [] cleartext = array.toByteArray();
        // Encripta el texto
        ciphertext = desCipher.doFinal(cleartext);
        return ciphertext;
    }

    /** Base64 y HEX Functions
     * @throws UnsupportedEncodingException */
    public String encodeB64String(final byte [] data) throws UnsupportedEncodingException {
        return new String(org.apache.commons.codec.binary.Base64.encodeBase64(data), "UTF-8");
    }

    public byte [] encodeB64(final byte [] data) {
        return org.apache.commons.codec.binary.Base64.encodeBase64(data);
    }

    public byte [] encodeB64UrlSafe(final byte [] data) {
        byte [] encode = org.apache.commons.codec.binary.Base64.encodeBase64(data);
        for (int i = 0; i < encode.length; i++) {
            if (encode[i] == '+') {
                encode[i] = '-';
            } else if (encode[i] == '/') {
                encode[i] = '_';
            }
        }
        return encode;
    }

    public String decodeB64String(final byte [] data) throws UnsupportedEncodingException {
        return new String(org.apache.commons.codec.binary.Base64.decodeBase64(data), "UTF-8");
    }

    public byte [] decodeB64(final byte [] data) {
        return org.apache.commons.codec.binary.Base64.decodeBase64(data);
    }

    public byte [] decodeB64UrlSafe(final byte [] data) {
        byte [] encode = org.bouncycastle.util.Arrays.copyOf(data, data.length);
        for (int i = 0; i < encode.length; i++) {
            if (encode[i] == '-') {
                encode[i] = '+';
            } else if (encode[i] == '_') {
                encode[i] = '/';
            }
        }
        return org.apache.commons.codec.binary.Base64.decodeBase64(encode);
    }

    public String toHexadecimal(byte [] datos, int numBytes) {
        String resultado = "";
        ByteArrayInputStream input = new ByteArrayInputStream(datos, 0, numBytes);
        String cadAux;
        int leido = input.read();
        while (leido != -1) {
            cadAux = Integer.toHexString(leido);
            if (cadAux.length() < 2)// Hay que aadir un 0
                resultado += "0";
            resultado += cadAux;
            leido = input.read();
        }
        return resultado;
    }

    public byte[] toByteArray(String cadena){
        //Si es impar se aade un 0 delante
        if(cadena.length() % 2 != 0)
            cadena = "0"+cadena;

        int longitud = cadena.length()/2;
        int posicion = 0;
        String cadenaAux =null;
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        for(int i=0 ;i < longitud ;i++)
        {
            cadenaAux = cadena.substring(posicion,posicion+2);
            posicion +=2;
            salida.write((char)Integer.parseInt(cadenaAux,16));
        }
        return salida.toByteArray();
    }

    /** MAC Function
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws IllegalStateException */
    public byte [] mac256(final String dsMerchantParameters, final byte [] secretKo) throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException, UnsupportedEncodingException {
        // Se hace el MAC con la clave de la operacin "Ko" y se codifica en BASE64
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secretKo, "HmacSHA256");
        sha256HMAC.init(secretKey);
        byte [] hash = sha256HMAC.doFinal(dsMerchantParameters.getBytes("UTF-8"));
        return hash;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////// 		FUNCIONES PARA LA GENERACIN DEL FORMULARIO DE PAGO: 				 ////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    public String getOrder() throws org.json.JSONException
    {
        if (getParameter("DS_MERCHANT_ORDER") == null || getParameter("DS_MERCHANT_ORDER").equals("")) {
            return getParameter("Ds_Merchant_Order");
        } else {
            return getParameter("DS_MERCHANT_ORDER");
        }
    }

    public String createMerchantParameters() throws UnsupportedEncodingException {
        String jsonString = jsonObj.toString();
        String res = encodeB64String(jsonString.getBytes("UTF-8"));
        return res;
    }

    public String createMerchantSignature(final String claveComercio) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, org.json.JSONException
    {
        String merchantParams = createMerchantParameters();

        byte [] clave = decodeB64(claveComercio.getBytes("UTF-8"));
        String secretKc = toHexadecimal(clave, clave.length);
        byte [] secretKo = encrypt_3DES(secretKc, getOrder());

        // Se hace el MAC con la clave de la operacin "Ko" y se codifica en BASE64
        byte [] hash = mac256(merchantParams, secretKo);
        String res = encodeB64String(hash);
        return res;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////// FUNCIONES PARA LA RECEPCIN DE DATOS DE PAGO (Notif, URLOK y URLKO): ////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    public String getOrderNotif() throws org.json.JSONException
    {
        if (getParameter("Ds_Order") == null || getParameter("Ds_Order").equals("")) {
            return getParameter("DS_ORDER");
        } else {
            return getParameter("Ds_Order");
        }
    }

    public String getOrderNotifSOAP(final String datos) {
        int posPedidoIni = datos.indexOf("<Ds_Order>");
        int tamPedidoIni = "<Ds_Order>".length();
        int posPedidoFin = datos.indexOf("</Ds_Order>");
        return datos.substring(posPedidoIni + tamPedidoIni, posPedidoFin);
    }

    public String getRequestNotifSOAP(final String datos) {
        int posReqIni = datos.indexOf("<Request");
        int posReqFin = datos.indexOf("</Request>");
        int tamReqFin = "</Request>".length();
        return datos.substring(posReqIni, posReqFin + tamReqFin);
    }

    public String getResponseNotifSOAP(final String datos) {
        int posResIni = datos.indexOf("<Response");
        int posResFin = datos.indexOf("</Response>");
        int tamResFin = "</Response>".length();
        return datos.substring(posResIni, posResFin + tamResFin);
    }

    public String decodeMerchantParameters(final String datos) throws UnsupportedEncodingException, org.json.JSONException
    {
        byte [] res = decodeB64UrlSafe(datos.getBytes("UTF-8"));
        String params = new String(res, "UTF-8");
        jsonObj = new JSONObject(params);
        return new String(res, "UTF-8");
    }

    public String createMerchantSignatureNotif(final String claveComercio, final String merchantParams) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, org.json.JSONException
    {
        byte [] clave = decodeB64(claveComercio.getBytes("UTF-8"));
        String secretKc = toHexadecimal(clave, clave.length);
        byte [] secretKo = encrypt_3DES(secretKc, getOrderNotif());

        // Se hace el MAC con la clave de la operacin "Ko" y se codifica en BASE64
        byte [] hash = mac256(merchantParams, secretKo);
        byte [] res = encodeB64UrlSafe(hash);
        return new String(res, "UTF-8");
    }

    /******  Notificaciones SOAP ENTRADA *****
     * @throws UnsupportedEncodingException
     * @throws IllegalStateException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException */
    public String createMerchantSignatureNotifSOAPRequest(final String claveComercio, final String request) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte [] clave = decodeB64(claveComercio.getBytes("UTF-8"));
        String secretKc = toHexadecimal(clave, clave.length);
        byte [] secretKo = encrypt_3DES(secretKc, getOrderNotifSOAP(request));

        // Se hace el MAC con la clave de la operacin "Ko" y se codifica en BASE64
        byte [] hash = mac256(getRequestNotifSOAP(request), secretKo);
        byte [] res = encodeB64(hash);
        return new String(res, "UTF-8");
    }

    /******  Notificaciones SOAP SALIDA *****
     * @throws UnsupportedEncodingException
     * @throws IllegalStateException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException */
    public String createMerchantSignatureNotifSOAPResponse(final String claveComercio, final String response, final String numPedido) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte [] clave = decodeB64(claveComercio.getBytes("UTF-8"));
        String secretKc = toHexadecimal(clave, clave.length);
        byte [] secretKo = encrypt_3DES(secretKc, numPedido);

        // Se hace el MAC con la clave de la operacin "Ko" y se codifica en BASE64
        byte [] hash = mac256(getResponseNotifSOAP(response), secretKo);
        byte [] res = encodeB64(hash);
        return new String(res, "UTF-8");
    }

    /*
        public static void updateBillingDescriptor(String descriptor,String trackingid){
            Connection connection=null;
            try{
                connection=Database.getConnection();
                String updateQuery="UPDATE transaction_common_details SET responsedescriptor= ? WHERE trackingid = ?";
                PreparedStatement preparedStatement=connection.prepareStatement(updateQuery);
                preparedStatement.setString(1,descriptor);
                preparedStatement.setString(2,trackingid);
                preparedStatement.executeUpdate();

            }
            catch (SystemError se)
            {
                transactionLogger.error("SystemError---", se);
            }
            catch (SQLException e)
            {
                transactionLogger.error("SQLException---", e);
            }
            finally
            {
                if(connection!=null){
                    Database.closeConnection(connection);
                }
            }
        }
    */
    public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException
    {
        System.out.println(("strURL:::::" + strURL));
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {
            org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            transactionLogger.error("HttpException---->",he);
        }
        catch (IOException io){
            transactionLogger.error("IOException---->", io);
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String doPostHTTPSURLConnection(String strURL, String request) throws PZTechnicalViolationException
    {
        String result = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        URLConnection conn = null;
        try
        {
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            URL url = new URL(strURL);
            try
            {
                conn = url.openConnection();
                conn.setConnectTimeout(120000);
                conn.setReadTimeout(120000);
            }
            catch (SSLHandshakeException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("CajaRuralUtils.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE, null, io.getMessage(), io.getCause());
            }
            if (conn instanceof HttpURLConnection)
            {
                ((HttpURLConnection) conn).setRequestMethod("POST");
            }
            assert conn != null;
            conn.setDoInput(true);
            conn.setDoOutput(true);
            out = new BufferedOutputStream(conn.getOutputStream());
            byte outBuf[] = request.getBytes(charset);
            out.write(outBuf);
            out.close();
            in = new BufferedInputStream(conn.getInputStream());
            result = ReadByteStream(in);
        }
        catch (UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("CajaRuralUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("CajaRuralUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON, null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("CajaRuralUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION, null, pe.getMessage(), pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("CajaRuralUtils.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, ex.getMessage(), ex.getCause());
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("PZConstraintViolationException---->",e);
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("SkrillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("SkrillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred. Please contact your Admin:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
                }
            }
        }
        if (result == null)
            return "";
        else
            return result;
    }

    private static String ReadByteStream(BufferedInputStream in) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        LinkedList<Cajarural> bufList = new LinkedList<Cajarural>();
        int size = 0;
        String buffer = null;
        byte buf[];
        try
        {
            do
            {
                buf = new byte[128];
                int num = in.read(buf);
                if (num == -1)
                    break;
                size += num;
                bufList.add(new Cajarural(buf, num));
            }
            while (true);
            buf = new byte[size];
            int pos = 0;
            for (ListIterator<Cajarural> p = bufList.listIterator(); p.hasNext(); )
            {
                Cajarural b = p.next();
                for (int i = 0; i < b.size; )
                {
                    buf[pos] = b.buf[i];
                    i++;
                    pos++;
                }
            }
            buffer = new String(buf, charset);
        }
        catch (UnsupportedEncodingException ue)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SKrillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        catch (IOException ie)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SKrillUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ie.getMessage(), ie.getCause());
        }
        return buffer;
    }
    static class Cajarural
    {
        public byte buf[];
        public int size;

        public Cajarural(byte b[], int s)
        {
            buf = b;
            size = s;
        }
    }

    public String getTranstype(String transType){
        if("0".equals(transType)){
            transType= PZProcessType.SALE.toString();
        }else if("1".equals(transType)){
            transType=PZProcessType.AUTH.toString();
        }else if("2".equals(transType)){
            transType=PZProcessType.CAPTURE.toString();
        }else if("3".equals(transType)){
            transType=PZProcessType.REFUND.toString();
        }else if("9".equals(transType)){
            transType=PZProcessType.CANCEL.toString();
        }
        return transType;
    }
    public  String getAmount(String amount)
    {
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;

        Integer newAmount = dObj2.intValue();

        return newAmount.toString();
    }

    public HashMap getPreviousTransactionDeatils(String trackingid){
        Connection conn= null;
        PreparedStatement ps= null;
        HashMap hashMap=null;
        String status="";
        ResultSet rs= null;
        try{
            String query="Select * from transaction_Cajarural_details where trackingid='"+trackingid+"'";
            conn= Database.getConnection();
            ps= conn.prepareStatement(query);
            rs=ps.executeQuery();
            if (rs.next()){
                hashMap=new HashMap();
                hashMap.put("status", rs.getString("Ds_Response"));
                hashMap.put("transaType", rs.getString("Ds_TransactionType"));
                hashMap.put("terminal", rs.getString("Ds_Terminal"));
                hashMap.put("date", rs.getString("Ds_Date"));

            }
            transactionLogger.debug("Sql Query-----"+ps);
        }
        catch (SystemError e)
        {
            transactionLogger.error("SystemError-----",e);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException-----",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
        return hashMap;
    }
    public static String getResponseForm(HashMap<String, String> parameters,String url){
        //transactionlogger.error("inside  IMoneyPayPaymentProcess Form---");

        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +url+ "\">" ;
        Iterator keys=parameters.keySet().iterator();
        while (keys.hasNext())
        {
            String key= (String) keys.next();
            form+="<input type=\"hidden\" name=\""+key+"\"  value=\""+parameters.get(key)+"\">";

        }
        form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        System.out.println("Cajarural PaymentProcess Form---"+form.toString());
        return form.toString();
    }


    public void insertCajaruralDetails(String Trackingid,String Ds_Response,String Ds_TransactionType,String Ds_Terminal,String Ds_Date,String Ds_AuthorisationCode){
        Connection conn=null;
        PreparedStatement ps=null;
        try{
            conn=Database.getConnection();
            String query="insert into transaction_Cajarural_details(Trackingid,Ds_Response,Ds_TransactionType,Ds_Terminal,Ds_Date,Ds_AuthorisationCode) values(?,?,?,?,?,?)";
            ps=conn.prepareStatement(query);
            ps.setString(1,Trackingid);
            ps.setString(2,Ds_Response);
            ps.setString(3,Ds_TransactionType);
            ps.setString(4,Ds_Terminal);
            ps.setString(5,Ds_Date);
            ps.setString(6,Ds_AuthorisationCode);
            ps.executeUpdate();

            transactionLogger.debug("SqlQuery-----"+ps);
        }
        catch (SystemError s)
        {
            transactionLogger.error("SystemError-----",s);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException-----",e);
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }

    public void updateCajaruralDetails(String Trackingid,String Ds_Response,String Ds_TransactionType,String Ds_Terminal,String Ds_Date,String Ds_AuthorisationCode){
        Connection conn=null;
        PreparedStatement ps=null;
        try{
            conn=Database.getConnection();
            String query="UPDATE transaction_Cajarural_details set Ds_Response=?,Ds_TransactionType=?,Ds_Terminal=?,Ds_Date=?,Ds_AuthorisationCode=? where Trackingid=? ";
            ps=conn.prepareStatement(query);
            ps.setString(1,Ds_Response);
            ps.setString(2,Ds_TransactionType);
            ps.setString(3,Ds_Terminal);
            ps.setString(4,Ds_Date);
            ps.setString(5,Ds_AuthorisationCode);
            ps.setString(6,Trackingid);
            ps.executeUpdate();

            transactionLogger.debug("SqlQuery-----"+ps);
        }
        catch (SystemError s)
        {
            transactionLogger.error("SystemError-----",s);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException-----",e);
        }
        finally
        {
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }


}
