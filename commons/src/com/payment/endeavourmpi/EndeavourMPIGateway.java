package com.payment.endeavourmpi;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by SurajT. on 11/13/2017.
 */
public class EndeavourMPIGateway
{
    public static final String GATEWAY_TYPE = "EndeavourMPIGateway";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.endeavourmpigateway");
    private static TransactionLogger transactionLogger = new TransactionLogger(EndeavourMPIGateway.class.getName());

    public EnrollmentResponseVO processEnrollment(EnrollmentRequestVO enrollmentRequestVO) throws PZTechnicalViolationException
    {
        Functions functions=new Functions();
        EnrollmentResponseVO enrollmentResponseVO = new EnrollmentResponseVO();
        String EnrollmentRequest =
                "mid=" +enrollmentRequestVO.getMid() +
                        "&name="+enrollmentRequestVO.getName() +
                        "&pan=" +enrollmentRequestVO.getPan() +
                        "&expiry="+enrollmentRequestVO.getExpiry() +
                        "&currency="+enrollmentRequestVO.getCurrency() +
                        "&amount=" +enrollmentRequestVO.getAmount() +
                        "&desc=" + enrollmentRequestVO.getTrackid() +
                        "&useragent="+enrollmentRequestVO.getUseragent() +
                        "&accept="+enrollmentRequestVO.getAccept() +
                        "&trackid="+enrollmentRequestVO.getTrackid();
        String EnrollmentRequestLog =
                "mid=" +enrollmentRequestVO.getMid() +
                        "&name="+enrollmentRequestVO.getName() +
                        "&pan=" +functions.maskingPan(enrollmentRequestVO.getPan()) +
                        "&expiry="+functions.maskingNumber(enrollmentRequestVO.getExpiry()) +
                        "&currency="+enrollmentRequestVO.getCurrency() +
                        "&amount=" +enrollmentRequestVO.getAmount() +
                        "&desc=" + enrollmentRequestVO.getTrackid() +
                        "&useragent="+enrollmentRequestVO.getUseragent() +
                        "&accept="+enrollmentRequestVO.getAccept() +
                        "&trackid="+enrollmentRequestVO.getTrackid();

        transactionLogger.error("-----enrollment request--"+enrollmentRequestVO.getTrackid()+"---:" + EnrollmentRequestLog);
        String enrollmentResponse = EndeavourMPIUtils.doPostHTTPSURLConnection(RB.getString("TEST_ENROLLMENT_URL"), EnrollmentRequest);
        transactionLogger.error("-----enrollment response--"+enrollmentRequestVO.getTrackid()+"---:" + enrollmentResponse);

        Map<String, String> responseMap = EndeavourMPIUtils.getQueryMap(enrollmentResponse);
        if(responseMap!=null){
            enrollmentResponseVO.setResult(responseMap.get("result"));
            enrollmentResponseVO.setAcsUrl(responseMap.get("url"));
            enrollmentResponseVO.setAvr(responseMap.get("avr"));
            enrollmentResponseVO.setPAReq(responseMap.get("pareq"));
            enrollmentResponseVO.setTrackId(responseMap.get("trackid"));
            enrollmentResponseVO.setThreeDVersion("3Dv1");
        }
        return enrollmentResponseVO;
    }
    public ParesDecodeResponseVO processParesDecode(ParesDecodeRequestVO paresDecodeRequestVO) throws PZTechnicalViolationException
    {
        ParesDecodeResponseVO paresDecodeResponseVO = new ParesDecodeResponseVO();
        String pareq =
                "<EPG>" +
                        "<MESSAGE id=\"" + paresDecodeRequestVO.getMassageID() + "\">" +
                        "<MPI>" +
                        "<pares>" + paresDecodeRequestVO.getPares() + "</pares>" +
                        "<trackid>" + paresDecodeRequestVO.getTrackid() + "</trackid >" +
                        "</MPI >" +
                        "</MESSAGE>" +
                        "</EPG>";

        transactionLogger.error("-----decode pares--"+paresDecodeRequestVO.getTrackid()+"---:" + pareq);
        String pares = EndeavourMPIUtils.doPostHTTPSURLConnection(RB.getString("TEST_PARESDECODE_URL"), pareq);
        transactionLogger.error("-----decoded pares--"+paresDecodeRequestVO.getTrackid()+"---:" + pares);
        Map readResponse = EndeavourMPIUtils.ReadParesResponse(StringUtils.trim(pares));
        if(readResponse != null)
        {
            paresDecodeResponseVO.setSignature((String) readResponse.get("signature"));
            paresDecodeResponseVO.setCavv((String) readResponse.get("cavv"));
            paresDecodeResponseVO.setPurchAmount((String) readResponse.get("purchAmount"));
            paresDecodeResponseVO.setXid((String) readResponse.get("xid"));
            paresDecodeResponseVO.setMessageID((String) readResponse.get("MessageID"));
            paresDecodeResponseVO.setDate((String) readResponse.get("date"));
            paresDecodeResponseVO.setStatus((String) readResponse.get("status"));
            paresDecodeResponseVO.setMerID((String) readResponse.get("merID"));
            paresDecodeResponseVO.setExponent((String) readResponse.get("exponent"));
            paresDecodeResponseVO.setPan((String) readResponse.get("pan"));
            paresDecodeResponseVO.setCavvAlgorithm((String) readResponse.get("cavvAlgorithm"));
            paresDecodeResponseVO.setEci((String) readResponse.get("eci"));
            paresDecodeResponseVO.setTrackid((String) readResponse.get("trackid"));

            paresDecodeResponseVO.set_20BytesBinaryCAVVBytes(com.directi.pg.Base64.decode(paresDecodeResponseVO.getCavv()));
            paresDecodeResponseVO.set_20BytesBinaryXIDBytes(com.directi.pg.Base64.decode(paresDecodeResponseVO.getXid()));

            String _20BytesBinaryCAVV = new String(com.directi.pg.Base64.decode(paresDecodeResponseVO.getCavv()));
            String _20BytesBinaryXID = new String(com.directi.pg.Base64.decode(paresDecodeResponseVO.getXid()));

            paresDecodeResponseVO.set_20BytesBinaryCAVV(_20BytesBinaryCAVV);
            paresDecodeResponseVO.set_20BytesBinaryXID(_20BytesBinaryXID);
        }
        return paresDecodeResponseVO;
    }
}
