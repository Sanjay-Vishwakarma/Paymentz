package com.payment.paynetics.core;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by admin on 9/5/2017.
 */
public class PayneticsUtils
{
    TransactionLogger transactionLogger=new TransactionLogger(PayneticsUtils.class.getName());

    public static void main(String[] args)
    {
        PayneticsUtils payneticsUtils=new PayneticsUtils();
        try
        {
            String amount = "149.98";
            String newAmount = payneticsUtils.getCentAmount(amount);
            //System.out.println("newAmount::::::" + newAmount);

            //String P060_81=payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%08d", "1222"),"81");//Payment Facilitator ID
            //String P060_82=payneticsUtils.getDATA_IN_LTV_FORMAT(String.format("%011d","3456789"),"82");//ISO ID

            //System.out.println("P060_81:"+String.format("%08d",1222));
            //System.out.println("P060_82:"+P060_82);

            //String sequneceGenerationNumber="F0F0F0F0F0F2F3F400";

            //String sequneceNumber=StringUtils.substring(sequneceGenerationNumber,0, 16);


            //System.out.println("sequneceNumber:"+sequneceNumber.replace("F","").trim());

            //byte[] sequneceNumber1=ISOUtil.hex2byte(sequneceNumber);
            //System.out.println(""+ISOUtil.parseInt(sequneceNumber1, "F"));
            //System.out.println("sequneceNumber1");

            //System.out.println(payneticsUtils.generateAuthorizationIdentificationResponse());


        }
        catch (Exception e){

        }
        //String formatted = String.format("%06d", 999999);
        //System.out.println("formatted:::"+formatted);

        //PayneticsUtils payneticsUtils=new PayneticsUtils();
        //String currentSTAN=payneticsUtils.getNextSTAN("1602055555","16P15555");
        //String nextSequenceNumber=payneticsUtils.getNextSequenceNumber("1602055555","16P15555","80");
        //System.out.println("currentSTAN::::::"+currentSTAN);
        //System.out.println("nextSequenceNumber::::::"+nextSequenceNumber);
        //System.out.println("ASCII to EBCDIC SequenceNumber::::::"+ISOUtil.hexString(ISOUtil.asciiToEbcdic(nextSequenceNumber))+"00");

        //String LTV_FORMAT_CVV=payneticsUtils.getDATA_IN_LTV_FORMAT("Reservierung", "35");
        //System.out.println("LTV_FORMAT_CVV:::::"+LTV_FORMAT_CVV);

        //String padded = String.format("%-20s", "Hahnstrasse 25");
        //System.out.println("padded::::"+padded);




        //System.out.println("dadad"+ISOUtil.hexString(ISOUtil.asciiToEbcdic("Reservierung")));;
        //String hexTagNumber=ISOUtil.hexString(ISOUtil.asciiToEbcdic(subField));
        //String hexValue=ISOUtil.hexString(ISOUtil.asciiToEbcdic(value));
        //String hexTotalLength=ISOUtil.hexString(ISOUtil.asciiToEbcdic(String.format("%03d",subField.length()+value.length())));
        //LTV_FORMAT_DATA=hexTotalLength+hexTagNumber+hexValue;

        /*PayneticsUtils payneticsUtils=new PayneticsUtils();
        ISOMsg isoMsg=null;
        try
        {
            //isoMsg=payneticsUtils.get0800SampleResponseMessage();
        }
        catch (ISOException iso){
          iso.printStackTrace();
        }*/
        //String SequenceNumber=ISOUtil.ebcdicToAscii(ISOUtil.hex2byte("F0F0F0F0F0F0F0F1"));
        //System.out.println("Hex to ASCII SequenceNumber::::::"+SequenceNumber);
        //System.out.println("ASCII to EBCDIC SequenceNumber::::::"+ISOUtil.hexString(ISOUtil.asciiToEbcdic("000001")));




    }

    public ISOMsg getISOMessage(HashMap<String,String> hashMap,GenericPackager packager)throws ISOException
    {
        Functions functions=new Functions();
        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setPackager(packager);
        if(hashMap!=null){
            if(functions.isValueNull(hashMap.get("MTYP"))){
                isoMsg.setMTI(hashMap.get("MTYP"));
                transactionLogger.error("Added MTI :"+hashMap.get("MTYP"));
            }
            if(functions.isValueNull(hashMap.get("P002"))){
                isoMsg.set("2", hashMap.get("P002"));
                transactionLogger.error("Added Primary Account Number :"+functions.maskingPan(hashMap.get("P002")));
            }
            if(functions.isValueNull(hashMap.get("P003"))){
                isoMsg.set("3", hashMap.get("P003"));
                transactionLogger.error("Added Processing Code :"+hashMap.get("P003"));
            }
            if(functions.isValueNull(hashMap.get("P004"))){
                isoMsg.set("4", hashMap.get("P004"));
                transactionLogger.error("Added Transaction Amount:"+hashMap.get("P004"));
            }
            if(functions.isValueNull(hashMap.get("P011"))){
                isoMsg.set("11",hashMap.get("P011"));
                transactionLogger.error("Added Systems Trace Audit Number:"+hashMap.get("P011"));
            }
            if(functions.isValueNull(hashMap.get("P012"))){
                isoMsg.set("12", hashMap.get("P012"));
                transactionLogger.error("Added Time, Local Transaction:"+hashMap.get("P012"));
            }
            if(functions.isValueNull(hashMap.get("P013"))){
                isoMsg.set("13", hashMap.get("P013"));
                transactionLogger.error("Added Date, Local Transaction:" + hashMap.get("P013"));
            }
            if(functions.isValueNull(hashMap.get("P014"))){
                isoMsg.set("14", hashMap.get("P014"));
                transactionLogger.error("Added Card Expiry Date:" +functions.maskingNumber(hashMap.get("P014")));
            }
            if(functions.isValueNull(hashMap.get("P017"))){
                isoMsg.set("17", hashMap.get("P017"));
                transactionLogger.error("Added Capture Reference:" + hashMap.get("P017"));
            }
            if(functions.isValueNull(hashMap.get("P022"))){
                isoMsg.set("22", hashMap.get("P022"));
                transactionLogger.error("Added POS Entry Mode Code:" + hashMap.get("P022"));
            }
            if(functions.isValueNull(hashMap.get("P025"))){
                isoMsg.set("25", hashMap.get("P025"));
                transactionLogger.error("Added POS Condition Code:" + hashMap.get("P025"));
            }
            if(functions.isValueNull(hashMap.get("P032"))){
                isoMsg.set("32", hashMap.get("P032"));
                transactionLogger.error("Added Acquiring Institution ID Code:" + hashMap.get("P032"));
            }
            if(functions.isValueNull(hashMap.get("P037"))){
                isoMsg.set("37", hashMap.get("P037"));
                transactionLogger.error("Added Retrival Reference Number:" + hashMap.get("P037"));
            }
            if(functions.isValueNull(hashMap.get("P038"))){
                isoMsg.set("38", hashMap.get("P038"));
                transactionLogger.error("Added Authorization Identification Response:" + hashMap.get("P038"));
            }
            if(functions.isValueNull(hashMap.get("P041"))){
                isoMsg.set("41", hashMap.get("P041"));
                transactionLogger.error("Added POS Terminal ID:" + hashMap.get("P041"));
            }
            if(functions.isValueNull(hashMap.get("P042"))){
                isoMsg.set("42", hashMap.get("P042"));
                transactionLogger.error("Added Card Acceptor ID Code:" + hashMap.get("P042"));
            }
            if(functions.isValueNull(hashMap.get("P043"))){
                isoMsg.set("43", hashMap.get("P043"));
                transactionLogger.error("Added Card Acceptor Name/Location:" + hashMap.get("P043"));
            }
            if(functions.isValueNull(hashMap.get("P046"))){
                isoMsg.set("46",hashMap.get("P046"));
                transactionLogger.error("Added CCTI-ID:" + hashMap.get("P046"));
            }
            if(functions.isValueNull(hashMap.get("P049"))){
                isoMsg.set("49", hashMap.get("P049"));
                transactionLogger.error("Added Transaction Currency Code:" + hashMap.get("P049"));
            }
            if(functions.isValueNull(hashMap.get("P057"))){
                isoMsg.set("57",hashMap.get("P057"));
                transactionLogger.error("Added Sequence-Generation Number:"+hashMap.get("P057"));
            }
            if(functions.isValueNull(hashMap.get("P060"))){
                isoMsg.set("60", hashMap.get("P060"));
                transactionLogger.error("Added Additional Data:" + hashMap.get("P060"));
            }
            if(functions.isValueNull(hashMap.get("P061"))){
                isoMsg.set("61", hashMap.get("P061"));
                transactionLogger.error("Added Additional Data:" + hashMap.get("P061"));
            }
            if(functions.isValueNull(hashMap.get("P063"))){
                isoMsg.set("63", hashMap.get("P063"));
                transactionLogger.error("Added Message Format Version Number:" + hashMap.get("P063"));
            }
        }
        return isoMsg;
    }

    public ISOMsg getISOMessageNew(HashMap<String,String> hashMap,GenericPackager packager)throws ISOException
    {
        Functions functions=new Functions();
        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setPackager(packager);
        if(hashMap!=null)
        {
            if(functions.isValueNull(hashMap.get("MTYP"))){
                isoMsg.setMTI(hashMap.get("MTYP"));//Message Type
                transactionLogger.error("Added Field:Message Type ");
                //System.out.println("Added Field MTI:Message Type ");
            }
            if(functions.isValueNull(hashMap.get("P002"))){
                isoMsg.set("2",hashMap.get("P002"));//Primary Account Number
                transactionLogger.error("Added Field 2:Primary Account Number:" + functions.maskingPan(hashMap.get("P002")));
                //System.out.println("Added Field 2:Primary Account Number:" + hashMap.get("P002"));
            }
            if(functions.isValueNull(hashMap.get("P003"))){
                isoMsg.set("3",hashMap.get("P003"));//Processing Code
                transactionLogger.error("Added Field 3:Processing Code");
                //System.out.println("Added Field 3:Processing Code");
            }
            if(functions.isValueNull(hashMap.get("P004"))){
                isoMsg.set("4",hashMap.get("P004"));//Transaction Amount
                transactionLogger.error("Added Field 4:Transaction Amount");
                //System.out.println("Added Field 4:Transaction Amount");
            }
            if(functions.isValueNull(hashMap.get("P011"))){
                isoMsg.set("11",hashMap.get("P011"));//Systems Trace Audit Number
                transactionLogger.error("Added Field 11:Systems Trace Audit Number");
                //System.out.println("Added Field 11:Systems Trace Audit Number");
            }
            if(functions.isValueNull(hashMap.get("P012"))){
                isoMsg.set("12",hashMap.get("P012"));//Time, Local Transaction
                transactionLogger.error("Added Field 12:Time, Local Transaction");
                //System.out.println("Added Field 12:Time, Local Transaction");
            }
            if(functions.isValueNull(hashMap.get("P013"))){
                isoMsg.set("13",hashMap.get("P013"));//Date, Local Transaction
                transactionLogger.error("Added Field 13:Date, Local Transaction");
                //System.out.println("Added Field 13:Date, Local Transaction");
            }
            if(functions.isValueNull(hashMap.get("P014"))){
                isoMsg.set("14",hashMap.get("P014"));//Card Expiry Date
                transactionLogger.error("Added Field 14:Card Expiry Date");
                //System.out.println("Added Field 14:Card Expiry Date");
            }
            if(functions.isValueNull(hashMap.get("P017"))){
                isoMsg.set("17",hashMap.get("P017"));//Capture Reference
                transactionLogger.error("Added Field 17:Capture Reference");
                //System.out.println("Added Field 17:Capture Reference");
            }
            if(functions.isValueNull(hashMap.get("P022"))){
                isoMsg.set("22",hashMap.get("P022"));//POS Entry Mode Code
                transactionLogger.error("Added Field 22:POS Entry Mode Code");
                //System.out.println("Added Field 22:POS Entry Mode Code");
            }

            ISOMsg inner  = new ISOMsg (22); // goes at outter field 22
            if(functions.isValueNull(hashMap.get("P022_1"))){
                inner.set (new ISOField(1,hashMap.get("P022_1")));

                transactionLogger.error("Added Field 22.1:PAN Entry mode");
                //System.out.println("Added Field 22.1:PAN Entry mode");
            }
            if(functions.isValueNull(hashMap.get("P022_2"))){
                inner.set (new ISOField(2,hashMap.get("P022_2")));
                isoMsg.set (inner);
                transactionLogger.error("Added Field 22.2:EMV & PIN Entry capability");
                //System.out.println("Added Field 22.2:EMV & PIN Entry capability");
            }
            if(functions.isValueNull(hashMap.get("P025"))){
                isoMsg.set("25",hashMap.get("P025"));//POS Condition Code
                transactionLogger.error("Added Field 25:POS Condition Code");
                //System.out.println("Added Field 25:POS Condition Code");
            }
            if(functions.isValueNull(hashMap.get("P032"))){
                isoMsg.set("32",hashMap.get("P032"));//Acquiring Institution ID Code
                transactionLogger.error("Added Field 32:Acquiring Institution ID Code");
                //System.out.println("Added Field 32:Acquiring Institution ID Code");
            }
            if(functions.isValueNull(hashMap.get("P041"))){
                isoMsg.set("41",hashMap.get("P041"));//POS Terminal ID
                transactionLogger.error("Added Field 41:POS Terminal ID");
                //System.out.println("Added Field 41:POS Terminal ID");
            }
            if(functions.isValueNull(hashMap.get("P042"))){
                isoMsg.set("42",hashMap.get("P042"));//Card Acceptor ID Code
                transactionLogger.error("Added Field 42:Card Acceptor ID Code");
                //System.out.println("Added Field 42:Card Acceptor ID Code");
            }
            if(functions.isValueNull(hashMap.get("P046"))){
                isoMsg.set("46",hashMap.get("P046"));//CCTI-ID
                transactionLogger.error("Added Field 46:CCTI-ID");
                //System.out.println("Added Field 46:CCTI-ID");
            }
            if(functions.isValueNull(hashMap.get("P049"))){
                isoMsg.set("49",hashMap.get("P049"));//Transaction Currency Code
                transactionLogger.error("Added Field 49:Transaction Currency Code");
                //System.out.println("Added Field 49:Transaction Currency Code");
            }
            if(functions.isValueNull(hashMap.get("P057"))){
                isoMsg.set("57",hashMap.get("P057"));//Sequence-Generation Number
                transactionLogger.error("Added Field 57:Sequence-Generation Number");
                //System.out.println("Added Field 57:Sequence-Generation Number");
            }
            if(functions.isValueNull(hashMap.get("P060"))){
                isoMsg.set("60",hashMap.get("P060"));//Additional Data
                transactionLogger.error("Added Field 60:Additional Data");
                //System.out.println("Added Field 60:Additional Data");
            }

            inner=new ISOMsg(60); // goes at outter field 60
            if(functions.isValueNull(hashMap.get("P060_30"))){
                inner.set (new ISOField(30,hashMap.get("P060_30")));//CVV2
                transactionLogger.error("Added Field 60.30:CVV2");
                //System.out.println("Added Field 60.30:CVV2");
            }
            if(functions.isValueNull(hashMap.get("P060_35"))){
                inner.set (new ISOField(35,hashMap.get("P060_35")));//Additional merchant data
                transactionLogger.error("Added Field 60.35:Additional merchant data");
                //System.out.println("Added Field 60.35:Additional merchant data");
            }
            if(functions.isValueNull(hashMap.get("P060_40"))){
                inner.set (new ISOField(40,hashMap.get("P060_40")));//Indicator for electronic commerce
                transactionLogger.error("Added Field 60.40:Indicator for electronic commerce");
                //System.out.println("Added Field 60.40:Indicator for electronic commerce");
            }
            if(functions.isValueNull(hashMap.get("P060_81"))){
                inner.set (new ISOField(81,hashMap.get("P060_81")));//Payment Facilitator ID
                transactionLogger.error("Added Field 60.81:Payment Facilitator ID");
                //System.out.println("Added Field 60.81:Payment Facilitator ID");
            }
            if(functions.isValueNull(hashMap.get("P060_82"))){
                inner.set (new ISOField(82,hashMap.get("P060_82")));//Independent Sales Organization
                transactionLogger.error("Added Field 60.82:Independent Sales Organization");
                //System.out.println("Added Field 60.82:Independent Sales Organization");
            }
            isoMsg.set (inner);
            if(functions.isValueNull(hashMap.get("P063"))){
                isoMsg.set("63",hashMap.get("P063"));//Message Format Version Number
                transactionLogger.error("Added Field 63:Message Format Version Number");
                //System.out.println("Added Field:Message Format Version Number");
            }
        }
        return isoMsg;
    }

    public String getLocalTime(Calendar calendar){
        String localTime="";
        String HOUR_OF_DAY="";
        String MINUTE="";
        String SECOND="";
        if(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)).length()==1){
            HOUR_OF_DAY="0"+String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        }else{
            HOUR_OF_DAY=String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        }

        if(String.valueOf(calendar.get(Calendar.MINUTE)).length()==1){
            MINUTE="0"+String.valueOf(calendar.get(Calendar.MINUTE));
        }else{
            MINUTE=String.valueOf(calendar.get(Calendar.MINUTE));
        }
        if(String.valueOf(calendar.get(Calendar.SECOND)).length()==1){
            SECOND="0"+String.valueOf(calendar.get(Calendar.SECOND));
        }else{
            SECOND=String.valueOf(calendar.get(Calendar.SECOND));
        }
        localTime=HOUR_OF_DAY+MINUTE+SECOND;
        return localTime;
    }

    public String getLocalDate(Calendar calendar){
        String localDate="";
        int monthNumber=calendar.get(Calendar.MONTH)+1;
        int dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);

        String monthNumberStr=String.valueOf(monthNumber);
        String dayOfMonthStr=String.valueOf(dayOfMonth);
        if(monthNumberStr.length()==1){
            monthNumberStr="0"+monthNumberStr;
        }
        if(dayOfMonthStr.length()==1){
            dayOfMonthStr="0"+dayOfMonthStr;
        }
        localDate=monthNumberStr+dayOfMonthStr;
        return localDate;
    }

    public String getCardExpiry(String cardExpiryMonth,String cardExpiryYear){
        return cardExpiryYear.substring(2,4)+cardExpiryMonth;
    }

    /*public String getCentAmount(String amount)
    {
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        Integer newAmount = dObj2.intValue();

        return newAmount.toString();
    }*/

    public String getCentAmount(String amount)
    {
        BigDecimal bigDecimal = new BigDecimal(amount);
        bigDecimal = bigDecimal.multiply(new BigDecimal(100));
        Long newAmount = bigDecimal.longValue();
        return newAmount.toString();
    }

    public String getJPYAmount(String amount)
    {
        double amt= Double.parseDouble(amount);
        double roundOff=Math.round(amt);
        int value=(int)roundOff;
        amount=String.valueOf(value);
        return amount.toString();
    }

    public String getMTI(String transactionType){//OK
        String MTI="";
        if ("sale".equalsIgnoreCase(transactionType) || "refund".equalsIgnoreCase(transactionType) || "recurring".equalsIgnoreCase(transactionType) || "payout".equalsIgnoreCase(transactionType))
        {
            MTI="0200";
        }
        else if("auth".equalsIgnoreCase(transactionType)){
            MTI="0100";
        }
        else if("reversal".equalsIgnoreCase(transactionType)){
            MTI="0400";
        }
        else if("capture".equalsIgnoreCase(transactionType)){
            MTI="0220";
        }
        return MTI;
    }

    public String getPOSEntryModeCode(String PANEntryMode){//OK
        if("MoTo".equalsIgnoreCase(PANEntryMode)){
            return "012";
        }
        else{
            return "812";
        }
    }

    public String getEComIndicatorCode(String PANEntryMode){//OK
        if("MoTo".equalsIgnoreCase(PANEntryMode)){
            return "";
        }
        else{
            return "07";
        }
    }
    public String getECI(String PO46){
        if ("80".equals(PO46) || "83".equals(PO46))
        {
            return "12";
        }
        else if ("81".equals(PO46) || "82".equals(PO46))// for MC and Maestro
        {
            //sending sending 07 for attempted 3D
            return "07";
        }
        else
        {
            return "07";
        }

    }

    public String getProcessingCode(String transactionType){//OK
        String processingCode="";
        if ("sale".equalsIgnoreCase(transactionType) || "auth".equalsIgnoreCase(transactionType) || "reversal".equalsIgnoreCase(transactionType) || "recurring".equalsIgnoreCase(transactionType) || "capture".equalsIgnoreCase(transactionType))
        {
            processingCode="000000";
        }
        else if("cash".equalsIgnoreCase(transactionType)){
            processingCode="010000";
        }else if("update".equalsIgnoreCase(transactionType)){
            processingCode="020000";
        }
        else if ("refund".equalsIgnoreCase(transactionType) || "payout".equalsIgnoreCase(transactionType))
        {
            processingCode="200000";
        }
        else if("inquiry".equalsIgnoreCase(transactionType)){
            processingCode="300000";
        }
        return processingCode;
    }

    public String getPOSConditionalCode(String transactionType){
        String posConditionalCode="";
        if ("sale".equalsIgnoreCase(transactionType) || "refund".equalsIgnoreCase(transactionType) || "recurring".equalsIgnoreCase(transactionType) || "payout".equalsIgnoreCase(transactionType))
        {
            posConditionalCode="00";
        }
        else if ("auth".equalsIgnoreCase(transactionType) || "reversal".equalsIgnoreCase(transactionType))
        {
            posConditionalCode="06";
        }
        else if("capture".equalsIgnoreCase(transactionType)){
            posConditionalCode="76";
        }
        return posConditionalCode;
    }

    public String getCCTIID(String cardNumber){
        String CCTIID="";
        String cardSchema=Functions.getCardType(cardNumber);
        if("VISA".equalsIgnoreCase(cardSchema)){
            CCTIID="80";
        }else if("MC".equalsIgnoreCase(cardSchema)){
            CCTIID="81";
        }else if("Maestro".equalsIgnoreCase(cardSchema)){
            CCTIID="82";
        }else if("VPay".equalsIgnoreCase(cardSchema)){
            CCTIID="83";
        }
        return CCTIID;
    }

    public String getElectronicComm(String cardNumber){
        String CCTIID="";
        String cardSchema=Functions.getCardType(cardNumber);
        if("VISA".equalsIgnoreCase(cardSchema)){
            CCTIID="10";
        }else if("MC".equalsIgnoreCase(cardSchema)){
            CCTIID="11";
        }else if("Maestro".equalsIgnoreCase(cardSchema)){
            CCTIID="11";
        }
        return CCTIID;
    }

    public String getPANEntryModeCode(String PANEntryMode){
        if("Ecommerce".equalsIgnoreCase(PANEntryMode)){
            return "81";
        }else if("MOTO".equalsIgnoreCase(PANEntryMode)){
            return "01";
        }
        return null;
    }

    public PayneticsGatewayAccountVO getAccountDetails(String accountId)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        PayneticsGatewayAccountVO accountDetailsVO=null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String query = "select * from gateway_accounts_paynetics where accountid=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,accountId);
            rs = stmt.executeQuery();
            if(rs.next())
            {
                accountDetailsVO=new PayneticsGatewayAccountVO();
                accountDetailsVO.setAccountId(rs.getString("accountid"));
                accountDetailsVO.setMerchantId(rs.getString("merchant_id"));
                accountDetailsVO.setTerminalId(rs.getString("terminal_id"));
                accountDetailsVO.setAcquiringInstitutionIdCode(rs.getString("acquiring_institution_id_code"));
                accountDetailsVO.setCardAcceptorNameLocation(rs.getString("card_acceptor_name_location"));
                accountDetailsVO.setAdditionalMerchantData(rs.getString("additional_merchant_data"));
                accountDetailsVO.setPaymentFacilitatorId(rs.getString("payment_facilitator_id"));
                accountDetailsVO.setIndependentSalesOrganization(rs.getString("independent_sales_organization"));
                accountDetailsVO.setSubMerchantId(rs.getString("sub_merchant_id"));
                accountDetailsVO.setSubMerchantMccCode(rs.getString("sub_merchant_mcc_code"));
                accountDetailsVO.setSubMerchantZip(rs.getString("sub_merchant_zip"));
                accountDetailsVO.setSubMerchantStreet(rs.getString("sub_merchant_street"));
                accountDetailsVO.setSubMerchantCity(rs.getString("sub_merchant_city"));
                accountDetailsVO.setMerchantUrl(rs.getString("merchant_url"));
                accountDetailsVO.setGenerationNumber(rs.getString("genetation_number"));
                accountDetailsVO.setMpiMid(rs.getString("mpi_mid"));
                accountDetailsVO.setIsTestWithSimulator(rs.getString("isTestWithSimulator"));
                accountDetailsVO.setIsQMuxActive(rs.getString("isQMuxActive"));
            }
        }
        catch (SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch (SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return accountDetailsVO;
    }

    public ISOMsg get0800SampleResponseMessage(ISOMsg requestIsoMsg)throws ISOException{
        Calendar calendar=Calendar.getInstance();
        ISOMsg isoMsg=new ISOMsg();

        isoMsg.setMTI("0810");
        isoMsg.set("11",requestIsoMsg.getString("11"));
        isoMsg.set("12",getLocalTime(calendar));
        isoMsg.set("13",getLocalDate(calendar));
        isoMsg.set("32",requestIsoMsg.getString("32"));
        isoMsg.set("39","00");
        isoMsg.set("41",requestIsoMsg.getString("41"));
        isoMsg.set("42",requestIsoMsg.getString("42"));
        isoMsg.set("46",requestIsoMsg.getString("46"));
        isoMsg.set("57","F0F0F0F0F0F6F3F400");

        /*transactionLogger.error("Sample MTI= " + isoMsg.getMTI());
        transactionLogger.error("Sample 11 = " + isoMsg.getString("11"));
        transactionLogger.error("Sample 12 = " + isoMsg.getString("12"));
        transactionLogger.error("Sample 13 = " + isoMsg.getString("13"));
        transactionLogger.error("Sample 32 = " + isoMsg.getString("32"));
        transactionLogger.error("Sample 39 = " + isoMsg.getString("39"));
        transactionLogger.error("Sample 41 = " + isoMsg.getString("41"));
        transactionLogger.error("Sample 42 = " + isoMsg.getString("42"));
        transactionLogger.error("Sample 46 = " + isoMsg.getString("46"));
        transactionLogger.error("Sample 57 = " + isoMsg.getString("57"));*/

        return isoMsg;
    }

    public synchronized String getNextSTAN(String merchantId, String terminalId)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String currentSTAN="";
        long previousSTAN=0;
        try
        {
            conn = Database.getConnection();
            String query = "select max(stan) as stan from transaction_paynetics_details where merchant_id=? and terminal_id=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,merchantId);
            stmt.setString(2,terminalId);
            rs = stmt.executeQuery();
            if(rs.next()){
                previousSTAN=rs.getLong("stan");
            }
        }
        catch(SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch(SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        currentSTAN = String.format("%06d", previousSTAN+1);
        return currentSTAN;
    }

    public String getNextSequenceNumber(String merchantId,String terminalId,String CCTI_ID)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String nextSequenceNumber="";
        long previousSequenceNumber=0;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT sequence_number FROM transaction_paynetics_details WHERE merchant_id=? AND terminal_id=? AND ccti_id=? ORDER BY id DESC LIMIT 1";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,merchantId);
            stmt.setString(2,terminalId);
            stmt.setString(3,CCTI_ID);
            rs = stmt.executeQuery();
            if(rs.next()){
                previousSequenceNumber=rs.getLong("sequence_number");
            }
        }
        catch(SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch(SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        nextSequenceNumber =String.format("%08d", previousSequenceNumber + 1);
        return nextSequenceNumber;
    }

    public String getTransactionSTAMP(String trackingid)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String previousSequenceNumber = "";
        try
        {
            conn = Database.getConnection();
            String query = "SELECT responseTranStamp FROM transaction_paynetics_details WHERE tracking_id=? ORDER BY id DESC LIMIT 1";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,trackingid);

            rs = stmt.executeQuery();
            if(rs.next()){
                previousSequenceNumber=rs.getString("responseTranStamp");
                transactionLogger.error("responseTranStamp from DB---"+previousSequenceNumber);
            }
        }
        catch(SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch(SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return previousSequenceNumber;
    }

    /*public synchronized STANSeqVO getNextSequenceNumber(String merchantId,String terminalId,String CCTI_ID,String trackingId,String transactionType)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        STANSeqVO stanSeqVO=new STANSeqVO();

        long previousSequenceNumber=0;
        long previousSTAN=0;

        String nextSequenceNumber="";
        String nextSTAN="";

        try
        {
            conn = Database.getConnection();
            String query = "SELECT sequence_number FROM transaction_paynetics_details WHERE merchant_id=? AND terminal_id=? AND ccti_id=? ORDER BY id DESC LIMIT 1";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,merchantId);
            stmt.setString(2,terminalId);
            stmt.setString(3,CCTI_ID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                previousSequenceNumber=rs.getLong("sequence_number");
            }

            query = "select max(stan) as stan from transaction_paynetics_details where merchant_id=? and terminal_id=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,merchantId);
            stmt.setString(2,terminalId);
            rs = stmt.executeQuery();
            if(rs.next()){
                previousSTAN=rs.getLong("stan");
            }

            nextSequenceNumber =String.format("%08d", previousSequenceNumber + 1);
            nextSTAN = String.format("%06d", previousSTAN+1);

            query = "insert into transaction_paynetics_details(id,tracking_id,stan,merchant_id,terminal_id,sequence_number,ccti_id,transaction_type) values(NULL,?,?,?,?,?,?,?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,trackingId);
            stmt.setString(2,nextSTAN);
            stmt.setString(3,merchantId);
            stmt.setString(4,terminalId);
            stmt.setString(5,nextSequenceNumber);
            stmt.setString(6,CCTI_ID);
            stmt.setString(7,transactionType);
            int k = stmt.executeUpdate();
            *//*if(k>0){*//*
            stanSeqVO.setSequenceNumber(nextSequenceNumber);
            stanSeqVO.setSTAN(nextSTAN);
            *//*}*//*
        }
        catch(SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch(SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeConnection(conn);
        }
        return stanSeqVO;
    }*/

    public boolean checkChannelAvailability(String merchantId, String terminalId, String CCTI_ID)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean checkChannelAvailability = false;
        String responseCode = "";
        Functions functions = new Functions();
        try
        {
            conn = Database.getConnection();
            String query = "SELECT response_code FROM transaction_paynetics_details WHERE merchant_id=? AND terminal_id=? AND ccti_id=? ORDER BY id DESC LIMIT 1";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, merchantId);
            stmt.setString(2, terminalId);
            stmt.setString(3, CCTI_ID);
            rs = stmt.executeQuery();
            if (rs.next())
            {
                responseCode = rs.getString("response_code");
                if (functions.isValueNull(responseCode))
                {
                    checkChannelAvailability = true;
                }
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError::::::", se);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return checkChannelAvailability;
    }

    public boolean updateSequenceNumber(String trackingId, String sequenceNumber, String STAN, String merchantId, String terminalId, String CCTI_ID)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean updated = false;
        try
        {
            conn = Database.getConnection();
            String query = "update transaction_paynetics_details set sequence_number=? where tracking_id=? and stan=? and merchant_id=? and terminal_id=? and ccti_id=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, sequenceNumber);
            stmt.setString(2, trackingId);
            stmt.setString(3, STAN);
            stmt.setString(4, merchantId);
            stmt.setString(5, terminalId);
            stmt.setString(6, CCTI_ID);
            int k = stmt.executeUpdate();
            if (k > 0)
            {
                updated = true;
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError::::::", se);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return updated;
    }

    public boolean updatePayneticsDetails(String trackingId, String authIdentificationResponse, String responseCode, String additionalResponseData, String amount)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean updated = false;
        try
        {
            conn = Database.getConnection();
            String query = "update transaction_paynetics_details set auth_identification_response=?,response_code=?,additional_response_data=?,amount=? where tracking_id=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, authIdentificationResponse);
            stmt.setString(2, responseCode);
            stmt.setString(3, additionalResponseData);
            stmt.setString(4, amount);
            stmt.setString(5, trackingId);
            int k = stmt.executeUpdate();
            if (k > 0)
            {
                updated = true;
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError::::::", se);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return updated;
    }

    public boolean updatePayneticsDetailsWithTStamp(String trackingId, String authIdentificationResponse, String responseCode, String additionalResponseData, String amount, String tStamp)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean updated = false;
        try
        {
            conn = Database.getConnection();
            String query = "update transaction_paynetics_details set auth_identification_response=?,response_code=?,additional_response_data=?,amount=?,responseTranStamp=? where tracking_id=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, authIdentificationResponse);
            stmt.setString(2, responseCode);
            stmt.setString(3, additionalResponseData);
            stmt.setString(4, amount);
            stmt.setString(5, tStamp);
            stmt.setString(6, trackingId);
            int k = stmt.executeUpdate();
            if (k > 0)
            {
                updated = true;
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError::::::", se);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return updated;
    }


    public boolean updateNewSTANSEQ(String trackingId, String sequenceNumber, String stan)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean updated = false;
        try
        {
            conn = Database.getConnection();
            String query = "update transaction_paynetics_details set sequence_number=?,stan=? where tracking_id=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, sequenceNumber);
            stmt.setString(2, stan);
            stmt.setString(3, trackingId);
            int k = stmt.executeUpdate();
            if (k > 0)
            {
                updated = true;
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError::::::", se);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return updated;
    }

    public String getApprovedSTAN(String trackingId,String bankApprovalId)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String approvalSTANStr="";

        try
        {
            conn = Database.getConnection();
            String query = "SELECT stan FROM transaction_paynetics_details WHERE tracking_id=? AND auth_identification_response=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,trackingId);
            stmt.setString(2,bankApprovalId);
            rs = stmt.executeQuery();
            if(rs.next()){
                approvalSTANStr="000001"+rs.getString("stan");
            }
        }
        catch(SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch(SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return approvalSTANStr;
    }

    public String getDATA_IN_LTV_FORMAT(String value, String subField){
        String LTV_FORMAT_DATA="";
        try
        {
            String hexTagNumber=ISOUtil.hexString(ISOUtil.asciiToEbcdic(subField));
            String hexValue=ISOUtil.hexString(ISOUtil.asciiToEbcdic(value.getBytes()));
            String hexTotalLength=ISOUtil.hexString(ISOUtil.asciiToEbcdic(String.format("%03d",subField.length()+value.length())));
            LTV_FORMAT_DATA=hexTotalLength+hexTagNumber+hexValue;
        }
        catch(Exception e){
            transactionLogger.error("Exception::::::",e);
        }
        return LTV_FORMAT_DATA;
    }

    public String getDATA_IN_LTV_LTV_FORMAT(String value, String subField)
    {
        String LTV_FORMAT_DATA = "";
        try
        {
            String hexTagNumber = ISOUtil.hexString(ISOUtil.asciiToEbcdic(subField));
            int length = subField.length() + value.length() / 2;
            String hexTotalLength = ISOUtil.hexString(ISOUtil.asciiToEbcdic(String.format("%03d", length)));
            LTV_FORMAT_DATA = hexTotalLength + hexTagNumber + value;
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception::::::", e);
        }
        return LTV_FORMAT_DATA;
    }

    public String getDATA_IN_LTV_FORMAT(byte[] value, String subField)
    {
        String LTV_FORMAT_DATA = "";
        try
        {
            String hexTagNumber = ISOUtil.hexString(ISOUtil.asciiToEbcdic(subField));
            String hexValue = ISOUtil.hexString(ISOUtil.asciiToEbcdic(value));
            String hexTotalLength = ISOUtil.hexString(ISOUtil.asciiToEbcdic(String.format("%03d", subField.length() + value.length)));
            LTV_FORMAT_DATA = hexTotalLength + hexTagNumber + hexValue;
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception::::::", e);
        }
        return LTV_FORMAT_DATA;
    }

    public String getDATA_IN_LTV_FORMATForCAVV(byte[] value, String subField)
    {
        String LTV_FORMAT_DATA = "";
        try
        {
            String hexTagNumber = ISOUtil.hexString(ISOUtil.asciiToEbcdic(subField));
            String hexValue = ISOUtil.hexString(value);
            String hexTotalLength = ISOUtil.hexString(ISOUtil.asciiToEbcdic(String.format("%03d", subField.length() + value.length)));
            LTV_FORMAT_DATA = hexTotalLength + hexTagNumber + hexValue;
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception::::::", e);
        }
        return LTV_FORMAT_DATA;
    }

    public String getStringDATA_IN_LTV_FORMAT(String value, String subField){
        String LTV_FORMAT_DATA="";
        try
        {
            String hexTagNumber=ISOUtil.hexString(ISOUtil.asciiToEbcdic(subField));
            String hexValue=ISOUtil.hexString(ISOUtil.asciiToEbcdic(value));
            String hexTotalLength=ISOUtil.hexString(ISOUtil.asciiToEbcdic(String.format("%03d",subField.length()+value.length())));
            LTV_FORMAT_DATA=hexTotalLength+hexTagNumber+hexValue;
        }
        catch(Exception e){
            transactionLogger.error("Exception::::::",e);
        }
        return LTV_FORMAT_DATA;
    }

    public boolean registerSTANSeqData(String trackingId, String STAN, String merchantId, String terminalId, String sequenceNumber, String CCTI_ID, String transactionType)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean inserted = false;
        try
        {
            conn = Database.getConnection();
            String query = "insert into transaction_paynetics_details(id,tracking_id,stan,merchant_id,terminal_id,sequence_number,ccti_id,transaction_type) values(NULL,?,?,?,?,?,?,?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, trackingId);
            stmt.setString(2, STAN);
            stmt.setString(3, merchantId);
            stmt.setString(4, terminalId);
            stmt.setString(5, sequenceNumber);
            stmt.setString(6, CCTI_ID);
            stmt.setString(7, transactionType);
            int k = stmt.executeUpdate();
            if (k > 0)
            {
                inserted = true;
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError::::::", se);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return inserted;
    }

    public int insertData(String trackingId,String STAN,String authIdentificationResponse,String merchantId,String terminalId,String amount,String sequenceNumber,String generationNumber,String CCTI_ID)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        int k=0;
        try
        {
            conn = Database.getConnection();
            String query = "insert into transaction_paynetics_details(id,tracking_id,stan,auth_identification_response,merchant_id,terminal_id,amount,sequence_number,ccti_id) values(NULL,?,?,?,?,?,?,?,?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,trackingId);
            stmt.setString(2,STAN);
            stmt.setString(3,authIdentificationResponse);
            stmt.setString(4,merchantId);
            stmt.setString(5,terminalId);
            stmt.setString(6,amount);
            stmt.setString(7,sequenceNumber);
            stmt.setString(8,CCTI_ID);
            k = stmt.executeUpdate();
        }
        catch(SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch(SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return k;
    }
    public Double getExchangeRate(String fromCurrency,String toCurrency)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet=null;
        Double exchangeRate=null;
        try
        {
            conn = Database.getConnection();
            String query = "select exchange_rate from currency_exchange_rates where from_currency=? and to_currency=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,fromCurrency);
            stmt.setString(2,toCurrency);
            resultSet=stmt.executeQuery();
            if(resultSet.next()){
                exchangeRate=resultSet.getDouble("exchange_rate");
            }
        }
        catch(SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch(SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return exchangeRate;
    }
    public boolean changeTerminalInfo(String amount,String currency,String accountId,String fromId,String templateAmount, String templateCurrency, String trackingId,String terminalId)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        int k=0;
        boolean result=false;
        try
        {
            conn = Database.getConnection();
            String query = "update transaction_common set amount=?,currency=?,accountid=?,fromid=?,templateamount=?,templatecurrency=?,terminalId=? where trackingid=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,amount);
            stmt.setString(2,currency);
            stmt.setString(3,accountId);
            stmt.setString(4,fromId);
            stmt.setString(5,templateAmount);
            stmt.setString(6,templateCurrency);
            stmt.setString(7,terminalId);
            stmt.setString(8,trackingId);
            k=stmt.executeUpdate();
            if(k>0){
                result=true;
            }
        }
        catch(SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch(SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return result;
    }
    public int insertDataNew(String trackingId,String STAN,String authIdentificationResponse,String merchantId,String terminalId,String amount,String sequenceNumber,String generationNumber,String CCTI_ID,String responseCode,String additionalResponseData,String transactionType)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        int k=0;
        try
        {
            conn = Database.getConnection();
            String query = "insert into transaction_paynetics_details(id,tracking_id,stan,auth_identification_response,merchant_id,terminal_id,amount,sequence_number,ccti_id,response_code,additional_response_data,transaction_type) values(NULL,?,?,?,?,?,?,?,?,?,?,?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,trackingId);
            stmt.setString(2,STAN);
            stmt.setString(3,authIdentificationResponse);
            stmt.setString(4,merchantId);
            stmt.setString(5,terminalId);
            stmt.setString(6,amount);
            stmt.setString(7,sequenceNumber);
            stmt.setString(8,CCTI_ID);
            stmt.setString(9,responseCode);
            stmt.setString(10,additionalResponseData);
            stmt.setString(11,transactionType);
            k = stmt.executeUpdate();
        }
        catch(SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch(SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return k;
    }

    public ISOMsg get_0100_SampleResponseMsg(ISOMsg requestIsoMsg)throws ISOException{

        Calendar calendar=Calendar.getInstance();
        ISOMsg isoMsg=new ISOMsg();
        Functions functions=new Functions();

        isoMsg.setMTI("0110");
        isoMsg.set("2", requestIsoMsg.getString("2"));
        isoMsg.set("3",requestIsoMsg.getString("3"));
        isoMsg.set("4",requestIsoMsg.getString("4"));
        isoMsg.set("11",requestIsoMsg.getString("11"));
        isoMsg.set("12",getLocalTime(calendar));
        isoMsg.set("13",getLocalDate(calendar));
        isoMsg.set("14",requestIsoMsg.getString("14"));
        isoMsg.set("15",getLocalDate(calendar));
        isoMsg.set("17",requestIsoMsg.getString("17"));
        isoMsg.set("32",requestIsoMsg.getString("32"));
        isoMsg.set("38",generateAuthorizationIdentificationResponse());
        /*isoMsg.set("39","00");*/
        if (requestIsoMsg.getString("4").equalsIgnoreCase("15000"))
        {
            isoMsg.set("39", "33");
            isoMsg.set("44", "33 Card Expired");
        }
        else if (requestIsoMsg.getString("4").equalsIgnoreCase("25000"))
        {
            isoMsg.set("39", "96");
            isoMsg.set("44", "96 Processing temporarily not possible");

        }
        else if (requestIsoMsg.getString("4").equalsIgnoreCase("35000"))
        {
            isoMsg.set("39", "04");
            isoMsg.set("44", "04 Retain card");
        }
        else if (requestIsoMsg.getString("4").equalsIgnoreCase("45000"))
        {
            isoMsg.set("39", "14");
            isoMsg.set("44", "14 Invalid card");
        }
        else if (requestIsoMsg.getString("4").equalsIgnoreCase("7500"))
        {
            isoMsg.set("39", "30");
            isoMsg.set("44", "30 Format Error");
        }
        else
        {
            isoMsg.set("39", "00");
            isoMsg.set("44", "00 APPROVED");
        }

        isoMsg.set("41",requestIsoMsg.getString("41"));
        isoMsg.set("42",requestIsoMsg.getString("42"));
        isoMsg.set("44","00 APPROVED");
        isoMsg.set("46",requestIsoMsg.getString("46"));
        isoMsg.set("57",requestIsoMsg.getString("57"));
        isoMsg.set("63",requestIsoMsg.getString("63"));

        transactionLogger.error("Sample MTI= " + isoMsg.getMTI());
        transactionLogger.error("Sample 3 = " + isoMsg.getString("3"));
        transactionLogger.error("Sample 4 = " + isoMsg.getString("4"));
        transactionLogger.error("Sample 11 = " + isoMsg.getString("11"));
        transactionLogger.error("Sample 12 = " + isoMsg.getString("12"));
        transactionLogger.error("Sample 13 = " + isoMsg.getString("13"));
        transactionLogger.error("Sample 15 = " + isoMsg.getString("15"));
        transactionLogger.error("Sample 17 = " + isoMsg.getString("17"));
        transactionLogger.error("Sample 32 = " + isoMsg.getString("32"));
        transactionLogger.error("Sample 38 = " + isoMsg.getString("38"));
        transactionLogger.error("Sample 39 = " + isoMsg.getString("39"));
        transactionLogger.error("Sample 41 = " + isoMsg.getString("41"));
        transactionLogger.error("Sample 42 = " + isoMsg.getString("42"));
        transactionLogger.error("Sample 44 = " + isoMsg.getString("44"));
        transactionLogger.error("Sample 46 = " + isoMsg.getString("46"));
        transactionLogger.error("Sample 57 = " + isoMsg.getString("57"));
        transactionLogger.error("Sample 63 = " + isoMsg.getString("63"));
        return isoMsg;
    }

    public ISOMsg get_0200_SampleResponseMsg(ISOMsg requestIsoMsg)throws ISOException
    {
        Calendar calendar=Calendar.getInstance();
        ISOMsg isoMsg=new ISOMsg();

        isoMsg.setMTI("0210");
        isoMsg.set("2",requestIsoMsg.getString("2"));
        isoMsg.set("3",requestIsoMsg.getString("3"));
        isoMsg.set("4",requestIsoMsg.getString("4"));
        isoMsg.set("11",requestIsoMsg.getString("11"));
        isoMsg.set("12",getLocalTime(calendar));
        isoMsg.set("13",getLocalDate(calendar));
        isoMsg.set("14",requestIsoMsg.getString("14"));
        isoMsg.set("15",getLocalDate(calendar));
        isoMsg.set("17",requestIsoMsg.getString("17"));
        isoMsg.set("32",requestIsoMsg.getString("32"));
        isoMsg.set("38",generateAuthorizationIdentificationResponse());

        if (requestIsoMsg.getString("4").equalsIgnoreCase("15000"))
        {
            isoMsg.set("39", "33");
            isoMsg.set("44", "33 Card Expired");
        }
        else if (requestIsoMsg.getString("4").equalsIgnoreCase("25000"))
        {
            isoMsg.set("39", "96");
            isoMsg.set("44", "96 Processing temporarily not possible");

        }
        else if (requestIsoMsg.getString("4").equalsIgnoreCase("35000"))
        {
            isoMsg.set("39", "04");
            isoMsg.set("44", "04 Retain card");
        }
        else if (requestIsoMsg.getString("4").equalsIgnoreCase("45000"))
        {
            isoMsg.set("39", "14");
            isoMsg.set("44", "14 Invalid card");
        }
        else if (requestIsoMsg.getString("4").equalsIgnoreCase("7500"))
        {
            isoMsg.set("39", "30");
            isoMsg.set("44", "30 Format Error");
        }
        else
        {
            isoMsg.set("39", "00");
            isoMsg.set("44", "00 APPROVED");
        }

        isoMsg.set("41",requestIsoMsg.getString("41"));
        isoMsg.set("42",requestIsoMsg.getString("42"));

        isoMsg.set("46",requestIsoMsg.getString("46"));
        isoMsg.set("57",requestIsoMsg.getString("57"));
        isoMsg.set("63",requestIsoMsg.getString("63"));

        /*transactionLogger.error("Sample MTI= " + isoMsg.getMTI());
        transactionLogger.error("Sample 2 = " + isoMsg.getString("2"));
        transactionLogger.error("Sample 3 = " + isoMsg.getString("3"));
        transactionLogger.error("Sample 4 = " + isoMsg.getString("4"));
        transactionLogger.error("Sample 11 = " + isoMsg.getString("11"));
        transactionLogger.error("Sample 12 = " + isoMsg.getString("12"));
        transactionLogger.error("Sample 13 = " + isoMsg.getString("13"));
        transactionLogger.error("Sample 14 = " + isoMsg.getString("14"));
        transactionLogger.error("Sample 15 = " + isoMsg.getString("15"));
        transactionLogger.error("Sample 17 = " + isoMsg.getString("17"));
        transactionLogger.error("Sample 32 = " + isoMsg.getString("32"));
        transactionLogger.error("Sample 38 = " + isoMsg.getString("38"));
        transactionLogger.error("Sample 39 = " + isoMsg.getString("39"));
        transactionLogger.error("Sample 41 = " + isoMsg.getString("41"));
        transactionLogger.error("Sample 42 = " + isoMsg.getString("42"));
        transactionLogger.error("Sample 44 = " + isoMsg.getString("44"));
        transactionLogger.error("Sample 46 = " + isoMsg.getString("46"));
        transactionLogger.error("Sample 57 = " + isoMsg.getString("57"));
        transactionLogger.error("Sample 63 = " + isoMsg.getString("63"));*/
        return isoMsg;
    }
    public ISOMsg get_0220_SampleResponseMsg(ISOMsg requestIsoMsg)throws ISOException
    {
        Calendar calendar=Calendar.getInstance();
        ISOMsg isoMsg=new ISOMsg();

        isoMsg.setMTI("0230");
        isoMsg.set("2",requestIsoMsg.getString("2"));
        isoMsg.set("3",requestIsoMsg.getString("3"));
        isoMsg.set("4",requestIsoMsg.getString("4"));
        isoMsg.set("11",requestIsoMsg.getString("11"));
        isoMsg.set("12",getLocalTime(calendar));
        isoMsg.set("13",getLocalDate(calendar));
        isoMsg.set("14",requestIsoMsg.getString("14"));
        isoMsg.set("15",getLocalDate(calendar));
        isoMsg.set("17",requestIsoMsg.getString("17"));
        isoMsg.set("32",requestIsoMsg.getString("32"));
        isoMsg.set("38",generateAuthorizationIdentificationResponse());
        isoMsg.set("39","00");
        isoMsg.set("41",requestIsoMsg.getString("41"));
        isoMsg.set("42",requestIsoMsg.getString("42"));
        isoMsg.set("44","00 APPROVED");
        isoMsg.set("46",requestIsoMsg.getString("46"));
        isoMsg.set("57",requestIsoMsg.getString("57"));
        isoMsg.set("63",requestIsoMsg.getString("63"));

        /*transactionLogger.error("Sample MTI= " + isoMsg.getMTI());
        transactionLogger.error("Sample 2 = " + isoMsg.getString("2"));
        transactionLogger.error("Sample 3 = " + isoMsg.getString("3"));
        transactionLogger.error("Sample 4 = " + isoMsg.getString("4"));
        transactionLogger.error("Sample 11 = " + isoMsg.getString("11"));
        transactionLogger.error("Sample 12 = " + isoMsg.getString("12"));
        transactionLogger.error("Sample 13 = " + isoMsg.getString("13"));
        transactionLogger.error("Sample 14 = " + isoMsg.getString("14"));
        transactionLogger.error("Sample 15 = " + isoMsg.getString("15"));
        transactionLogger.error("Sample 17 = " + isoMsg.getString("17"));
        transactionLogger.error("Sample 32 = " + isoMsg.getString("32"));
        transactionLogger.error("Sample 38 = " + isoMsg.getString("38"));
        transactionLogger.error("Sample 39 = " + isoMsg.getString("39"));
        transactionLogger.error("Sample 41 = " + isoMsg.getString("41"));
        transactionLogger.error("Sample 42 = " + isoMsg.getString("42"));
        transactionLogger.error("Sample 44 = " + isoMsg.getString("44"));
        transactionLogger.error("Sample 46 = " + isoMsg.getString("46"));
        transactionLogger.error("Sample 57 = " + isoMsg.getString("57"));
        transactionLogger.error("Sample 63 = " + isoMsg.getString("63"));*/
        return isoMsg;
    }
    public ISOMsg get_0400_SampleResponseMsg(ISOMsg requestIsoMsg)throws ISOException
    {
        Calendar calendar=Calendar.getInstance();
        ISOMsg isoMsg=new ISOMsg();

        isoMsg.setMTI("0410");
        isoMsg.set("2",requestIsoMsg.getString("2"));
        isoMsg.set("3",requestIsoMsg.getString("3"));
        isoMsg.set("4",requestIsoMsg.getString("4"));
        isoMsg.set("11",requestIsoMsg.getString("11"));
        isoMsg.set("12",getLocalTime(calendar));
        isoMsg.set("13",getLocalDate(calendar));
        isoMsg.set("14",requestIsoMsg.getString("14"));
        isoMsg.set("15",getLocalDate(calendar));
        isoMsg.set("17",requestIsoMsg.getString("17"));
        isoMsg.set("32",requestIsoMsg.getString("32"));
        isoMsg.set("38",generateAuthorizationIdentificationResponse());
        isoMsg.set("39","00");
        isoMsg.set("41",requestIsoMsg.getString("41"));
        isoMsg.set("42",requestIsoMsg.getString("42"));
        isoMsg.set("44","00 APPROVED");
        isoMsg.set("46",requestIsoMsg.getString("46"));
        isoMsg.set("57",requestIsoMsg.getString("57"));
        isoMsg.set("63",requestIsoMsg.getString("63"));

        /*transactionLogger.error("Sample MTI= " + isoMsg.getMTI());
        transactionLogger.error("Sample 2 = " + isoMsg.getString("2"));
        transactionLogger.error("Sample 3 = " + isoMsg.getString("3"));
        transactionLogger.error("Sample 4 = " + isoMsg.getString("4"));
        transactionLogger.error("Sample 11 = " + isoMsg.getString("11"));
        transactionLogger.error("Sample 12 = " + isoMsg.getString("12"));
        transactionLogger.error("Sample 13 = " + isoMsg.getString("13"));
        transactionLogger.error("Sample 14 = " + isoMsg.getString("14"));
        transactionLogger.error("Sample 15 = " + isoMsg.getString("15"));
        transactionLogger.error("Sample 17 = " + isoMsg.getString("17"));
        transactionLogger.error("Sample 32 = " + isoMsg.getString("32"));
        transactionLogger.error("Sample 38 = " + isoMsg.getString("38"));
        transactionLogger.error("Sample 39 = " + isoMsg.getString("39"));
        transactionLogger.error("Sample 41 = " + isoMsg.getString("41"));
        transactionLogger.error("Sample 42 = " + isoMsg.getString("42"));
        transactionLogger.error("Sample 44 = " + isoMsg.getString("44"));
        transactionLogger.error("Sample 46 = " + isoMsg.getString("46"));
        transactionLogger.error("Sample 57 = " + isoMsg.getString("57"));
        transactionLogger.error("Sample 63 = " + isoMsg.getString("63"));*/
        return isoMsg;
    }


    public ISOMsg get_SequenceNumberError_SampleResponseMsg(ISOMsg requestIsoMsg)throws ISOException
    {
        Calendar calendar=Calendar.getInstance();
        ISOMsg isoMsg=new ISOMsg();

        isoMsg.setMTI("0210");
        isoMsg.set("2",requestIsoMsg.getString("2"));
        isoMsg.set("3",requestIsoMsg.getString("3"));
        isoMsg.set("4",requestIsoMsg.getString("4"));
        isoMsg.set("11",requestIsoMsg.getString("11"));
        isoMsg.set("12",getLocalTime(calendar));
        isoMsg.set("13",getLocalDate(calendar));
        isoMsg.set("14",requestIsoMsg.getString("14"));
        isoMsg.set("15",getLocalDate(calendar));
        isoMsg.set("17",requestIsoMsg.getString("17"));
        isoMsg.set("32",requestIsoMsg.getString("32"));
        isoMsg.set("38","XXXXXX");
        isoMsg.set("39","06");
        isoMsg.set("41",requestIsoMsg.getString("41"));
        isoMsg.set("42",requestIsoMsg.getString("42"));
        isoMsg.set("44","06 SEQUENCE NUMBER ERROR");
        isoMsg.set("46",requestIsoMsg.getString("46"));
        isoMsg.set("57",requestIsoMsg.getString("57"));
        isoMsg.set("63",requestIsoMsg.getString("63"));
        return isoMsg;
    }

    public ISOMsg get_SequenceNumberError_SampleResponseMsgFor100(ISOMsg requestIsoMsg)throws ISOException
    {
        Calendar calendar=Calendar.getInstance();
        ISOMsg isoMsg=new ISOMsg();

        isoMsg.setMTI("0110");
        isoMsg.set("2",requestIsoMsg.getString("2"));
        isoMsg.set("3",requestIsoMsg.getString("3"));
        isoMsg.set("4",requestIsoMsg.getString("4"));
        isoMsg.set("11",requestIsoMsg.getString("11"));
        isoMsg.set("12",getLocalTime(calendar));
        isoMsg.set("13",getLocalDate(calendar));
        isoMsg.set("14",requestIsoMsg.getString("14"));
        isoMsg.set("15",getLocalDate(calendar));
        isoMsg.set("17",requestIsoMsg.getString("17"));
        isoMsg.set("32",requestIsoMsg.getString("32"));
        isoMsg.set("38","XXXXXX");
        isoMsg.set("39","06");
        isoMsg.set("41",requestIsoMsg.getString("41"));
        isoMsg.set("42",requestIsoMsg.getString("42"));
        isoMsg.set("44","06 SEQUENCE NUMBER ERROR");
        isoMsg.set("46",requestIsoMsg.getString("46"));
        isoMsg.set("57",requestIsoMsg.getString("57"));
        isoMsg.set("63",requestIsoMsg.getString("63"));
        return isoMsg;
    }

    public String generateAuthorizationIdentificationResponse(){
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder((100000 + rnd.nextInt(900000))+"");
        return sb.toString();
    }
    public String getTrackingid(String ccnum)
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String newTrackingid = "";
        String firstSix=ccnum.substring(0,6);
        String lastFour=ccnum.substring((ccnum.length()-4),ccnum.length());
        try
        {
            conn = Database.getConnection();
            String query = "SELECT tc.trackingid FROM transaction_common tc,bin_details bd WHERE tc.trackingid=bd.icicitransid AND  bd.first_six=? AND bd.last_four=? AND tc.trackingid>'2861470' AND TIMESTAMP>'2019-05-29 22:02:39' AND fromtype='paynetics' AND STATUS='capturesuccess' LIMIT 1";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,firstSix);
            stmt.setString(2,lastFour);
            transactionLogger.error("stmt--------->"+stmt);
            rs = stmt.executeQuery();
            if(rs.next()){
                newTrackingid=rs.getString("trackingid");
                transactionLogger.error("responseTranStamp from DB---"+newTrackingid);
            }
        }
        catch(SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch(SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeResultSet(rs);
            Database.closePreparedStatement(stmt);
            Database.closeConnection(conn);
        }
        return newTrackingid;
    }

}
