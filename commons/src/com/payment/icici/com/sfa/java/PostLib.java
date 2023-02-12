package com.payment.icici.com.sfa.java;

import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.Timestamp;
import javax.servlet.http.*;
import java.security.*;

import com.directi.pg.LoadProperties;
import com.payment.icici.com.sfa.java.PGResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 *
 * PostLib is the main class used to pass the information to the Payment Gateway.The merchant invokes the method postMoto() if he is a Moto Merchant and a method postSSL() if he is a SSLMerchant.
 *
 */


public class PostLib
{
	private URL urlDataSources;
	private String motoURL;
	private String sslURL;
	private boolean verbose = false;
	private String strKeyDir;
	private String strOsType;
	private String epgURL;
	private static final String MERCHANT	  = "MerchantID";
	private static final String VENDOR 	  = "Vendor";
	private static final String PARTNER    = "Partner";
	private static final String MER_TXN_ID = "MerchantTxnID";
	private static final String MSG_TYPE   = "MessageType";
	private static final String INV_NO     = "InvoiceNo";
	private static final String ORD_REFNO  = "OrdRefNo";
	private static final String INV_DT 	  = "InvoiceDate";
	private static final String GMT_OFFSET = "GMTOffset";
	private static final String TIME_STAMP = "TimeStamp";
	private static final String INV_AMT 	  = "Amount";
	private static final String CURR_CODE  = "CurrCode";
	private static final String BT_CUSTID  = "CustomerId";
	private static final String BT_CUSTNM  = "CustomerName";
	private static final String BT_STREET1 = "BillAddrLine1";
	private static final String BT_STREET2 = "BillAddrLine2";
	private static final String BT_STREET3 = "BillAddrLine3";
	private static final String BT_CITY 	  = "BillCity";
	private static final String BT_STATE   = "BillState";
	private static final String BT_ZIP 	  = "BillZip";
	private static final String BT_COUNTRY = "BillCountryAlphaCode";
	private static final String BT_MAIL 	  = "BillEmail";
	private static final String ST_STREET1 = "ShipAddrLine1";
	private static final String ST_STREET2 = "ShipAddrLine2";
	private static final String ST_STREET3 = "ShipAddrLine3";
	private static final String ST_CITY 	  = "ShipCity";
	private static final String ST_STATE   = "ShipState";
	private static final String ST_ZIP 	  = "ShipZip";
	private static final String ST_COUNTRY = "ShipCountryAlphaCode";
	private static final String CRD_TYP    = "CardType";
	private static final String CRD_NUM 	  = "CardNum";
	private static final String EXP_DT_MON = "ExpDtMon";
	private static final String EXP_DT_YR  = "ExpDtYr";
	private static final String CVVNUM	  = "CVVNum";
	private static final String CRD_NAME	  = "NameOnCard";
	private static final String RESPMETH	  = "RespMethod";
	private static final String RESPURL	  = "RespURL";
	private static final String EXT1		=	"Ext1";
	private static final String EXT2		=	"Ext2";
	private static final String EXT3		=	"Ext3";
	private static final String EXT4		=	"Ext4";
	private static final String EXT5		=	"Ext5";
	private static final String MRT_IP_ADDR = "MrtIpAddr";
	private static final String ENCRYPTED_DATA = "EncryptedData";
	private static final String LANGUAGE_TYPE = "LanguageType";
	private static final String OS_TYPE = "OsType";
	private static final String REQUEST_TYPE = "RequestType";
	private static final String TRN_ORG    = "RootTxnSysRefNum";
	private static final String RRN_ORG    = "RootPNRefNum";
	private static final String AUTH_CD    = "RootAuthCode";
	private static final String MerchantType    = "MerchantType";
	private static final String REQUEST_TYPE_RELATED = "RelatedTxn";
	private static final String REQUEST_TYPE_STATUS_INQUIRY = "SFAStatusInquiry";
	private static final String REQUEST_TYPE_TXN_SEARCH = "SFATxnSearch";
	private static final String REQUEST_TYPE_ROOTSSLPOST = "SFARootSSLPost";
	private static final String REQUEST_TYPE_ROOTMOTOPOST = "SFARootMotoPost";
	public static final String REQUEST_TYPE_ROOTSSLREDIRECT = "SFARootSSLRedirect";
	private static final String LANGUAGE_TYPE_JAVA = "Java";
	private static final String SSL_PURCHASE_AMOUNT="PurchaseAmount";
	private static final String SSL_DISPLAY_AMOUNT="DisplayAmount";
	private static final String SSL_CURRENCY_VAL="CurrencyVal";
	private static final String SSL_EXPONENT="Exponent";
	private static final String SSL_ORDERDESC="OrderDesc";
	private static final String SSL_RECURFREQ="RecurFreq";
	private static final String SSL_RECUREND="RecurEnd";
	private static final String SSL_INSTALLMENT="Installment";
	private static final String SSL_DEVICECATEGORY="DeviceCategory";
	private static final String SSL_WHATIUSE="WhatIUse";
	private static final String SSL_ACCEPTHDR="AcceptHdr";
	private static final String SSL_USERAGENT="UserAgent";
	/*start SSL Moto customization changes*/
	private static final String MOTO_PURCHASE_AMOUNT="PurchaseAmount";
	private static final String MOTO_DISPLAY_AMOUNT="DisplayAmount";
	private static final String MOTO_CURRENCY_VAL="CurrencyVal";
	private static final String MOTO_EXPONENT="Exponent";
	private static final String MOTO_ORDERDESC="OrderDesc";
	private static final String MOTO_RECURFREQ="RecurFreq";
	private static final String MOTO_RECUREND="RecurEnd";
	private static final String MOTO_INSTALLMENT="Installment";
	private static final String MOTO_DEVICECATEGORY="DeviceCategory";
	private static final String MOTO_WHATIUSE="WhatIUse";
	private static final String MOTO_ACCEPTHDR="AcceptHdr";
	private static final String MOTO_USERAGENT="UserAgent";
	/*end SSL Moto customization changes*/
	private static final String MOTO_VBV_STATUS ="status";
	private static final String MOTO_VBV_CAVV = "cavv";
	private static final String MOTO_VBV_ECI = "eci";
	private static final String MOTO_VBV_PURCHASE_AMOUNT = "purchaseAmount";
	private static final String MOTO_VBV_CURR_VAL = "currencyVal";
	private static final String MOTO_VBV_CURR_XID = "xid";
	private static final String MOTO_VBV_CURR_SHOPPING_CONTEXT = "shoppingcontext";
	private static final String INSTRUMENT_TYPE = "InstrType";
	private static final String CUST_IP_ADDR = "CustIPAddress";
	private static final String TXN_SEARCH_START_DATE = "StartDate";
	private static final String TXN_SEARCH_END_DATE = "EndDate";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	private static String traceLog = null;
	private static FileWriter fos = null;
	private static PrintWriter pw = null;


	//<Ketan 13-04-2005> CR:3837
	private static final String PG_RESP_RESERVE1 = "Reserve1";
	private static final String PG_RESP_RESERVE2 = "Reserve2";
	private static final String PG_RESP_RESERVE3 = "Reserve3";
	private static final String PG_RESP_RESERVE4 = "Reserve4";
	private static final String PG_RESP_RESERVE5 = "Reserve5";
	private static final String PG_RESP_RESERVE6 = "Reserve6";
	private static final String PG_RESP_RESERVE7 = "Reserve7";
	private static final String PG_RESP_RESERVE8 = "Reserve8";
	private static final String PG_RESP_RESERVE9 = "Reserve9";
	private static final String PG_RESP_RESERVE10 = "Reserve10";


	//Added for Bharosa by Sandeep Patil on 11-06-2007 for ELTOPUSPRD-18063

	// For Customer Details
	private static final String OFF_STREET1 = "OfficeAddrLine1";
	private static final String OFF_STREET2 = "OfficeAddrLine2";
	private static final String OFF_STREET3 = "OfficeAddrLine3";
	private static final String OFF_CITY 	= "OfficeCity";
	private static final String OFF_STATE   = "OfficeState";
	private static final String OFF_ZIP 	= "OfficeZip";
	private static final String OFF_COUNTRY = "OfficeCountryAlphaCode";
	private static final String HOME_STREET1= "HomeAddrLine1";
	private static final String HOME_STREET2= "HomeAddrLine2";
	private static final String HOME_STREET3= "HomeAddrLine3";
	private static final String HOME_CITY 	= "HomeCity";
	private static final String HOME_STATE  = "HomeState";
	private static final String HOME_ZIP 	= "HomeZip";
	private static final String HOME_COUNTRY= "HomeCountryAlphaCode";
	private static final String MOBILE="MobileNo";
	private static final String CUSTINFOPRESENT = "CustInfoPresent";
	private static final String FIRSTNAME="FirstName";
	private static final String LASTNAME="LastName";
	private static final String ISBillNSHIPADDRMATCH="IsBillNShipAddrMatch";
	private static final String REGDATE="RegistrationDate";

	//For Sesion Details
	private static final String SESSIONDETAILSINFOPRESENT = "SessionDetailsInfoPresent";
	private static final String TRANS_IP_ADDR = "TransIPAddress";
	private static final String COOKIE="Cookie";
	private static final String BROW_CNT="BrowserCountry";
	private static final String BROW_LOC_LANG="BrowserLocalLang";
	private static final String BROW_LOC_VAR="BrowserLocalLangVariant";
	private static final String BROW_USR_AGT="BrowserUserAgent";

	//For AirLineMerchants
	private static final String AIRLINEINFOPRESENT = "AirLineInfoPresent";
	private static final String BOOK_DATE="BookingDate";
	private static final String FLT_DATE="FlightDate";
	private static final String FLT_TIME="FlightTime";
	private static final String FLT_NO="FlightNumber";
	private static final String PASS_NAME="PassengerName";
	private static final String NO_OF_TICKET="NumberOfTickets";
	private static final String CARD_NAME_N_CUST_NAME_MATCH="CardNameNCustomerNameMatch";
	private static final String PNR="PNR";
	private static final String SEC_FROM="SectorFrom";
	private static final String SEC_TO="SectorTo";

	//For MerchanDise
	private static final String MERCHANTDISEINFOPRESENT = "MerchantDiseInfoPresent";
	private static final String ITEM_PUR="ItemPurchased";
	private static final String QUANT="Quantity";
	private static final String BRAND="Brand";
	private static final String MOD_NO="ModelNumber";
	private static final String BUY_NAME="BuyersName";
	private static final String CARD_NAME_N_BUY_NAME_MATCH="CardNameNBuyerNameMatch";

	private static int i=0;


	private static final ResourceBundle sfaProp = LoadProperties.getProperty("com.directi.pg.sfa");
	public PostLib() throws Exception
	{

		try
		{
			/*InputStream oFIS = getClass().getResourceAsStream("sfa.properties");
			Properties mpiProps = new Properties();
			mpiProps.load(oFIS);*/

			motoURL = sfaProp.getString("motoURL");
			if(motoURL == null ||"".equals(motoURL)){
				throw new Exception("Error in the properties file. Value for motoURL is not mentioned or is invalid");
			}

			sslURL	= sfaProp.getString("sslURL");
			if(sslURL == null || "".equals(sslURL)){
				throw new Exception("Error in the properties file. Value for sslURL is not mentioned or is invalid");
			}

			String strVerbose = sfaProp.getString("verbose");
			if(strVerbose != null && strVerbose.trim().equals("true")){
				verbose = true;

			}

			strKeyDir = sfaProp.getString("Key.Directory");
			if(strKeyDir == null || "".equals(strKeyDir)){
				throw new Exception("Error in the properties file. Value for key Directory is not mentioned or is invalid");
			}

			strOsType = sfaProp.getString("OS.Type");
			if(strOsType == null || "".equals(strOsType)){
				throw new Exception("Error in the properties file. Value for OS.Type is not mentioned or is invalid");
			}

			epgURL= sfaProp.getString("epgURL");
			if(epgURL == null || "".equals(epgURL)){
				throw new Exception("Error in the properties file. Value for relatedTxnURL is not mentioned or is invalid");
			}

			traceLog =sfaProp.getString("traceLog");

			if(traceLog == null || "".equals(traceLog))
			{
				throw new Exception(" first time Error in the properties file. Value of tracelog must be specified in the property file");
			}
			if(i==0)
			{
				trace("SFA 1.0","FDMS SFA ");
				i++;
			}

		}
		catch (Exception exc)
		{
			if(verbose){
				trace("PostLib","An exception occured while loading the Properties file.");
				trace("PostLib", exc.toString());
			}
			//exc.printStackTrace();
			throw exc;
		}
	}



	public PGResponse postSSL(BillToAddress aoBTA,ShipToAddress aoSTA,ICICIMerchant aoICICIMerchant,MPIData aoMPI,HttpServletResponse aoResponse,PGReserveData aoPGReserveData,
							  CustomerDetails aoCustomer, SessionDetail aoSession,AirLineTransaction aoAirLine,
							  MerchanDise aoMerchanDise)
	{
		String strMerchantTxnId =null;
		String strFunctionName="postSSL";
		EPGMerchantEncryptionLib oEncryptionLib = null;
		String strEncryptedData = null;
		try {
			if(verbose){
				trace(strFunctionName,"Entered");
			}

			if(aoICICIMerchant == null){
				if(verbose){
					trace(strFunctionName,"Error. Merchant Object passed is null");
				}

				throw new SFAApplicationException ("Invalid Merchant Object passed to postSSL method. Object is null. Transaction cannot proceed.");
			}

			strMerchantTxnId = aoICICIMerchant.getMerchantTxnID();
			if(aoICICIMerchant.getMerchantID() == null || "".equals(aoICICIMerchant.getMerchantID())) {
				if(verbose) {
					trace(strFunctionName,"Error. Merchant Id passed is null");
				}
				throw new SFAApplicationException("Merchant Id is Invalid or null");

			}



			if(aoICICIMerchant.getMessageType() == null) {
				if(verbose) {
					trace(strFunctionName,"Message Type passed is null");
				}
				throw new SFAApplicationException("Invalid Message Type. Transaction cannot be processed");

			}


			StringBuffer oBuffer = new StringBuffer();
			oBuffer.append(buildMerchantBillShip(aoICICIMerchant,aoBTA,aoSTA));
			try{

				oEncryptionLib = new EPGMerchantEncryptionLib();
				strEncryptedData = oEncryptionLib.encryptMerchantData(aoICICIMerchant.getMerchantID(), strKeyDir , aoICICIMerchant.getMerchantTxnID(), aoICICIMerchant.getAmount());
			}catch(SFAApplicationException oException) {
				if(verbose){
					trace(strFunctionName, "SFAApplicationException in encrypting data. " +oException.getMessage());
				}
				throw oException;
			}
			catch(Exception oException) {
				if(verbose){
					trace(strFunctionName, "Exception in encrypting data. " +oException.getMessage());
				}
				throw new SFAApplicationException("Error while encrypting data. Transaction cannot be processed.");
			}


			oBuffer.append("&" + ENCRYPTED_DATA + "=" + strEncryptedData);
			oBuffer.append("&" + OS_TYPE + "=" + strOsType);
			oBuffer.append("&" + LANGUAGE_TYPE + "=" + LANGUAGE_TYPE_JAVA);

			oBuffer.append("&" + SSL_PURCHASE_AMOUNT + "=" + getValue((aoMPI==null)? null:aoMPI.getPurchaseAmount()));
			oBuffer.append("&" + SSL_DISPLAY_AMOUNT + "=" + getValue((aoMPI==null)? null:aoMPI.getDisplayAmount()));
			oBuffer.append("&" + SSL_CURRENCY_VAL + "=" + getValue((aoMPI==null)? null:aoMPI.getCurrencyVal()));
			oBuffer.append("&" + SSL_EXPONENT + "=" + getValue((aoMPI==null)? null:aoMPI.getExponent()));
			oBuffer.append("&" + SSL_ORDERDESC + "=" + getValue((aoMPI==null)? null:aoMPI.getOrderDesc()));
			oBuffer.append("&" + SSL_RECURFREQ + "=" + getValue((aoMPI==null)? null:aoMPI.getRecurFreq()));
			oBuffer.append("&" + SSL_RECUREND + "=" + getValue((aoMPI==null)? null:aoMPI.getRecurEnd()));
			oBuffer.append("&" + SSL_INSTALLMENT + "=" + getValue((aoMPI==null)? null:aoMPI.getInstallment()));
			oBuffer.append("&" + SSL_DEVICECATEGORY + "=" + getValue((aoMPI==null)? null:aoMPI.getDeviceCategory()));
			oBuffer.append("&" + SSL_WHATIUSE + "=" + getValue((aoMPI==null)? null:aoMPI.getWhatIUse()));
			oBuffer.append("&" + SSL_ACCEPTHDR + "=" + getValue((aoMPI==null)? null:aoMPI.getAcceptHdr()));
			oBuffer.append("&" + SSL_USERAGENT + "=" + getValue((aoMPI==null)? null:aoMPI.getAgentHdr()));

			oBuffer.append("&" + PG_RESP_RESERVE1 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField1()));
			oBuffer.append("&" + PG_RESP_RESERVE2 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField2()));
			oBuffer.append("&" + PG_RESP_RESERVE3 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField3()));
			oBuffer.append("&" + PG_RESP_RESERVE4 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField4()));
			oBuffer.append("&" + PG_RESP_RESERVE5 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField5()));
			oBuffer.append("&" + PG_RESP_RESERVE6 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField6()));
			oBuffer.append("&" + PG_RESP_RESERVE7 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField7()));
			oBuffer.append("&" + PG_RESP_RESERVE8 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField8()));
			oBuffer.append("&" + PG_RESP_RESERVE9 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField9()));
			oBuffer.append("&" + PG_RESP_RESERVE10 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField10()));

			//Added for Bharosa by Sandeep Patil on 11-06-2007 for ELTOPUSPRD-18063
			oBuffer.append(buildCustomerDetail(aoCustomer));
			oBuffer.append("&" + SESSIONDETAILSINFOPRESENT + "="  + getValue((aoSession==null)? null:aoSession.getSessionDetailFlag()));
			oBuffer.append("&" + TRANS_IP_ADDR + "="  + getValue((aoSession==null)? null:aoSession.getTransactionIPAddr()));
			oBuffer.append("&" + COOKIE + "=" + getValue((aoSession==null)? null:aoSession.getSecureCookie()));
			oBuffer.append("&" + BROW_CNT + "=" + getValue((aoSession==null)? null:aoSession.getBrowserCountry()));
			oBuffer.append("&" + BROW_LOC_LANG + "=" + getValue((aoSession==null)? null:aoSession.getBrowserLocalLang()));
			oBuffer.append("&" + BROW_LOC_VAR + "=" + getValue((aoSession==null)? null:aoSession.getBrowserLocalLangVariant()));
			oBuffer.append("&" + BROW_USR_AGT + "=" + getValue((aoSession==null)? null:aoSession.getBrowserUserAgent()));

			oBuffer.append(buildAirlLineAndMerchanDise(aoAirLine,aoMerchanDise));
			String retData	= postData(sslURL,oBuffer.toString());

			if(verbose){
				trace(strFunctionName,"Data returned is:" + retData);
			}

			PGResponse oPGResponse = new PGResponse(retData);
			oPGResponse.toString();
			if(oPGResponse.getRedirectionTxnId() != null) {
				oPGResponse.setRedirectionUrl(sslURL+"?txnId="+oPGResponse.getRedirectionTxnId());
			}
			return(oPGResponse);
		}
		catch(SFAApplicationException oException){
			if(verbose){
				trace(strFunctionName, "SFAApplicationException. " +oException.getMessage());
			}
			PGResponse oPGResponse = new PGResponse();
			oPGResponse.setRespCode("2");
			oPGResponse.setRespMessage(oException.getMessage());
			oPGResponse.setTxnId(strMerchantTxnId);
			return(oPGResponse);
		}
		catch(Exception oException ){
			if(verbose){
				trace(strFunctionName, "Exception. " +oException.getMessage());
			}
			PGResponse oPGResponse = new PGResponse();
			oPGResponse.setRespCode("2");
			oPGResponse.setRespMessage("Internal Processing Error ");
			oPGResponse.setTxnId(strMerchantTxnId);
			return(oPGResponse);
		}
		finally {
			oEncryptionLib = null;
			strEncryptedData = null;
			if(verbose){
				trace(strFunctionName, "Exiting");
			}
		}
	}




	public PGResponse postMOTO(BillToAddress aoBTA,ShipToAddress aoSTA,ICICIMerchant aoICICIMerchant,MPIData aoMPI,CardInfo aoCInfo,PGReserveData aoPGReserveData,
							   CustomerDetails aoCustomer, SessionDetail aoSession, AirLineTransaction aoAirLine,
							   MerchanDise aoMerchanDise){
		String strMerchantTxnId =null;
		String strFunctionName="postMOTO";
		String strName="sfa_log";
		String RespCode=null;
		String RespCodeMsg=null;
		String EpgTxnId=null;
		String AuthId=null;
		String RRN=null;
		String RespMessage=null;
		String CVRespCode=null;
		String Exp=null;
		EPGMerchantEncryptionLib oEncryptionLib = null;
		String strEncryptedData = null;

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss.SSS");
		Date requestDate = new Date();
		Date responseDate = null;

		try {

			if(verbose) {
				trace(strFunctionName, "Entered");
			}

			if(aoICICIMerchant == null){
				if(verbose) {
					trace(strFunctionName, "Error. Merchant object passed is null");
				}
				throw new SFAApplicationException ("Invalid merchant object passed. Transaction cannot proceed.");
			}

			if(aoCInfo == null){
				if(verbose) {
					trace(strFunctionName, "Error. Card Info object passed is null");
				}
				throw new SFAApplicationException ("Invalid card object passed. Transaction cannot proceed.");
			}

			strMerchantTxnId = aoICICIMerchant.getMerchantTxnID();

			if(aoICICIMerchant.getMerchantID() == null || "".equals(aoICICIMerchant.getMerchantID())) {
				if(verbose) {
					trace(strFunctionName, "Error. Merchant id is Invalid");
				}
				throw new SFAApplicationException("Invalid merchant id. Transaction cannot be processed");

			}


			if(aoICICIMerchant.getMessageType() == null) {
				if(verbose) {
					trace(strFunctionName,"Message Type passed is null");
				}
				throw new SFAApplicationException("Invalid message type. Transaction cannot be processed");

			}

			StringBuffer oBuffer = new StringBuffer();
			oBuffer.append(buildMerchantBillShip(aoICICIMerchant,aoBTA,aoSTA));

			try {
				oEncryptionLib = new EPGMerchantEncryptionLib();

				strEncryptedData = oEncryptionLib.encryptMerchantData(aoICICIMerchant.getMerchantID(), strKeyDir , aoICICIMerchant.getMerchantTxnID(), aoICICIMerchant.getAmount());

			}
			catch(SFAApplicationException oException) {
				if(verbose){
					trace(strFunctionName, "SFAApplicationException in encrypting data. " +oException.getMessage());
				}
				throw oException;
			}
			catch(Exception oException) {
				if(verbose){
					trace(strFunctionName, "Exception in encrypting data." +oException.getMessage());
				}
				throw new SFAApplicationException("Error while encrypting data. Transaction cannot be processed.");
			}


			oBuffer.append("&" + ENCRYPTED_DATA + "=" + strEncryptedData);
			oBuffer.append("&" + OS_TYPE + "=" + strOsType);
			oBuffer.append("&" + LANGUAGE_TYPE + "=" + LANGUAGE_TYPE_JAVA);

			oBuffer.append("&" + CUST_IP_ADDR + "=" + getValue(aoICICIMerchant.getCustIPAddress()));
			/* start SSL Moto customization changes*/
			oBuffer.append("&" + MOTO_PURCHASE_AMOUNT + "=" + getValue((aoMPI==null)? null:aoMPI.getPurchaseAmount()));
			oBuffer.append("&" + MOTO_DISPLAY_AMOUNT + "=" + getValue((aoMPI==null)? null:aoMPI.getDisplayAmount()));
			oBuffer.append("&" + MOTO_CURRENCY_VAL + "=" + getValue((aoMPI==null)? null:aoMPI.getCurrencyVal()));
			oBuffer.append("&" + MOTO_EXPONENT + "=" + getValue((aoMPI==null)? null:aoMPI.getExponent()));
			oBuffer.append("&" + MOTO_ORDERDESC + "=" + getValue((aoMPI==null)? null:aoMPI.getOrderDesc()));
			oBuffer.append("&" + MOTO_RECURFREQ + "=" + getValue((aoMPI==null)? null:aoMPI.getRecurFreq()));
			oBuffer.append("&" + MOTO_RECUREND + "=" + getValue((aoMPI==null)? null:aoMPI.getRecurEnd()));
			oBuffer.append("&" + MOTO_INSTALLMENT + "=" + getValue((aoMPI==null)? null:aoMPI.getInstallment()));
			oBuffer.append("&" + MOTO_DEVICECATEGORY + "=" + getValue((aoMPI==null)? null:aoMPI.getDeviceCategory()));
			oBuffer.append("&" + MOTO_WHATIUSE + "=" + getValue((aoMPI==null)? null:aoMPI.getWhatIUse()));
			oBuffer.append("&" + MOTO_ACCEPTHDR + "=" + getValue((aoMPI==null)? null:aoMPI.getAcceptHdr()));
			oBuffer.append("&" + MOTO_USERAGENT + "=" + getValue((aoMPI==null)? null:aoMPI.getAgentHdr()));
			
		 	/* end SSL Moto customization changes*/
			/*
			Commented for SSL Moto customization changes
			oBuffer.append("&" + MOTO_VBV_STATUS +"=" + 	getValue((aoMPI==null)? null:aoMPI.getVBVStatus()));
			// Added by Amit Paliwal encrypting CAVV value
			if((aoMPI.getCAVV()!= null && aoMPI.getCAVV().length() > 0 ) && (aoMerchant != null && "true".equalsIgnoreCase(aoMerchant.getExt2()))) {
				String strCAVV = "<CAVV>"+aoMPI.getCAVV()+"</CAVV>";
				strCAVV = oEncryptionLib.encryptMerchantData(aoMerchant.getMerchantID(), strKeyDir,strCAVV,"");
				oBuffer.append("&" + MOTO_VBV_CAVV +"="+strCAVV);
			}else {
				oBuffer.append("&" + MOTO_VBV_CAVV +"="+getValue((aoMPI==null)? null:aoMPI.getCAVV()));
			}
			oBuffer.append("&" + MOTO_VBV_ECI +"=" + 	getValue((aoMPI==null)? null:aoMPI.getECI()));
			oBuffer.append("&" + MOTO_VBV_PURCHASE_AMOUNT + "=" + getValue((aoMPI==null)? null:aoMPI.getPurchaseAmount()));
			oBuffer.append("&" + MOTO_VBV_CURR_VAL + "=" +  	getValue((aoMPI==null)? null:aoMPI.getCurrencyVal()));
			oBuffer.append("&" + MOTO_VBV_CURR_XID + "=" + getValue((aoMPI==null)? null:aoMPI.getXID()));
			oBuffer.append("&" + MOTO_VBV_CURR_SHOPPING_CONTEXT + "="  + 	getValue((aoMPI==null)? null:aoMPI.getShoppingContext()));*/

			oBuffer.append("&" + INSTRUMENT_TYPE + "=" + getValue((aoCInfo==null)? null:aoCInfo.getInstrType()));
			oBuffer.append("&" + CRD_TYP + "=" + getValue((aoCInfo==null)? null:aoCInfo.getCardType()));
			oBuffer.append("&" + CRD_NUM + "=" + getValue((aoCInfo==null)? null:aoCInfo.getCardNum()));
			oBuffer.append("&" + EXP_DT_YR+ "=" + getValue((aoCInfo==null)? null:aoCInfo.getExpDtYr()));
			oBuffer.append("&" + EXP_DT_MON + "=" + getValue((aoCInfo==null)? null:aoCInfo.getExpDtMon()));
			oBuffer.append("&" + CVVNUM + "="  + getValue((aoCInfo==null)? null:aoCInfo.getCVVNum()));
			oBuffer.append("&" + CRD_NAME + "=" + getValue((aoCInfo==null)? null:aoCInfo.getNameOnCard()));

			oBuffer.append("&" + PG_RESP_RESERVE1 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField1()));
			oBuffer.append("&" + PG_RESP_RESERVE2 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField2()));
			oBuffer.append("&" + PG_RESP_RESERVE3 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField3()));
			oBuffer.append("&" + PG_RESP_RESERVE4 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField4()));
			oBuffer.append("&" + PG_RESP_RESERVE5 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField5()));
			oBuffer.append("&" + PG_RESP_RESERVE6 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField6()));
			oBuffer.append("&" + PG_RESP_RESERVE7 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField7()));
			oBuffer.append("&" + PG_RESP_RESERVE8 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField8()));
			oBuffer.append("&" + PG_RESP_RESERVE9 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField9()));
			oBuffer.append("&" + PG_RESP_RESERVE10 + "=" + getValue((aoPGReserveData==null)? null:aoPGReserveData.getReserveField10()));

			//Added for Bharosa by Sandeep Patil on 11-06-2007 for ELTOPUSPRD-18063
			oBuffer.append(buildCustomerDetail(aoCustomer));
			oBuffer.append("&" + SESSIONDETAILSINFOPRESENT + "="  + getValue((aoSession==null)? null:aoSession.getSessionDetailFlag()));
			oBuffer.append("&" + TRANS_IP_ADDR + "="  + getValue((aoSession==null)? null:aoSession.getTransactionIPAddr()));
			oBuffer.append("&" + COOKIE + "=" + getValue((aoSession==null)? null:aoSession.getSecureCookie()));
			oBuffer.append("&" + BROW_CNT + "=" + getValue((aoSession==null)? null:aoSession.getBrowserCountry()));
			oBuffer.append("&" + BROW_LOC_LANG + "=" + getValue((aoSession==null)? null:aoSession.getBrowserLocalLang()));
			oBuffer.append("&" + BROW_LOC_VAR + "=" + getValue((aoSession==null)? null:aoSession.getBrowserLocalLangVariant()));
			oBuffer.append("&" + BROW_USR_AGT + "=" + getValue((aoSession==null)? null:aoSession.getBrowserUserAgent()));

			oBuffer.append(buildAirlLineAndMerchanDise(aoAirLine,aoMerchanDise));
			//System.out.println("request parameter===>"+oBuffer.toString());

			String strRetData	= postData(motoURL,oBuffer.toString());

//			System.out.println("strRetData===>"+strRetData);
				/* start ssl Moto Customization changes*/
			responseDate = new Date();
			PGResponse oPGResponse=new	PGResponse(strRetData);
			oPGResponse.toString();
//			System.out.println("response parameters are =====>"+oPGResponse.toString());


			if(oPGResponse.getRedirectionTxnId() != null) {
				oPGResponse.setRedirectionUrl(motoURL+"?txnId="+oPGResponse.getRedirectionTxnId());
			}
//			System.out.println("i am last");
			return(oPGResponse);
			/* end ssl Moto Customization changes*/

			//CODE ADDED FOR SFA LOGS
			/*
			Commented for ssl Moto customization changes
			PGResponse oPgResp=new	PGResponse(strRetData);
			RespCode=oPgResp.getRespCode();
			EpgTxnId=oPgResp.getEpgTxnId();
			AuthId=oPgResp.getAuthIdCode();
			RRN=oPgResp.getRRN();
			RespMessage=oPgResp.getRespMessage();
			CVRespCode=oPgResp.getCVRespCode();
			responseDate = new Date();
			if(RespCode.equalsIgnoreCase("0"))
			{
				RespCodeMsg="Successful";
			}else
			{
				if(RespCode.equalsIgnoreCase("1")){
					RespCodeMsg="Rejected by switch";
				}
				else
				{
					if(RespCode.equalsIgnoreCase("2")){
						RespCodeMsg="Rejected by Gateway";
					}
					else{
						RespCodeMsg=" ";
					}

				}
			}
			return	oPgResp;*/
		}
		catch(SFAApplicationException oException){

			if(verbose){

				trace(strFunctionName, "SFAApplicationException. " +oException.getMessage());
			}


			PGResponse oPGResponse = new PGResponse();
			oPGResponse.setRespCode("2");
			oPGResponse.setRespMessage(oException.getMessage());
			oPGResponse.setTxnId(strMerchantTxnId);
			return(oPGResponse);
		}
		catch(Exception oException ){
			if(verbose){


				trace(strFunctionName, "Exception. " +oException.getMessage());
			}

			PGResponse oPGResponse = new PGResponse();
			oPGResponse.setRespCode("2");
			oPGResponse.setRespMessage("Internal Processing Error  ");
			oPGResponse.setTxnId(strMerchantTxnId);
			return(oPGResponse);
		}
		finally {
			oEncryptionLib = null;
			strEncryptedData = null;
			if(verbose){

				//code added for sfa logs

//				Commented for ssl Moto customization changes

				/*System.out.print(strName + " | " + aoMerchant.getMerchantID() + " | "  + "MerTxnId(" + strMerchantTxnId + ") | " + "Request_Date("+dateFormat.format(requestDate)+ ") | " +
						"Request_Time(" + dateFormat1.format(requestDate) + " ) | " +
						"Response_Date(" + dateFormat.format(responseDate)+ " ) | " + "Response_Time( " + dateFormat1.format(responseDate) + " )" );
				System.out.print((EpgTxnId==null)? " | ePGTxnID( )" : "| ePGTxnID(" + EpgTxnId + ")");
				System.out.print((AuthId==null)? " | AuthIdCode( )" : "| AuthIdCode(" + AuthId + ")");
				System.out.print((RRN==null)? " | RRN( )" : "| RRN(" + RRN + ")");
				System.out.print((RespCode==null)? " | Response Message( )" : "| Response Message(" + RespCodeMsg + ")");
				System.out.print((CVRespCode==null)? " | CVRespCode( )" : "| CVRespCode(" + CVRespCode + ")");
				System.out.println("response message is"+RespMessage);
				
				/** Modified to redirect log into file mention in sfa.properties */

			/*  EpgTxnId = EpgTxnId != null ? EpgTxnId : "" ;
				AuthId = AuthId != null ? AuthId : "" ;
				RRN = RRN != null ? RRN : "" ;
				RespCode = RespCode != null ? RespCode : "" ;
				RespCodeMsg = RespCodeMsg !=null ? RespCodeMsg : "" ;
				CVRespCode = CVRespCode !=null ? CVRespCode : "" ;
				
				if(!RespCode.equalsIgnoreCase("0"))
				{
					System.out.print(" | " + RespMessage);
					trace("sfa_log", aoMerchant.getMerchantID() + "|" + "MerTxnId(" + strMerchantTxnId + ")|" + "Request_Date("+dateFormat.format(requestDate)+ ")|Request_Time(" + dateFormat1.format(requestDate) + ")|Response_Date(" + dateFormat.format(responseDate)+ ")|" + "Response_Time(" + dateFormat1.format(responseDate) + ")|ePGTxnID(" + EpgTxnId + ")|AuthIdCode(" + AuthId + ")|RRN(" + RRN + ")|Response Message(" + RespCodeMsg + ")|CVRespCode(" + CVRespCode + ")|" + RespMessage);
				}else{
					trace("sfa_log", aoMerchant.getMerchantID() + "|" + "MerTxnId(" + strMerchantTxnId + ")|" + "Request_Date("+dateFormat.format(requestDate)+ ")|Request_Time(" + dateFormat1.format(requestDate) + ")|Response_Date(" + dateFormat.format(responseDate)+ ")|" + "Response_Time(" + dateFormat1.format(responseDate) + ")|ePGTxnID(" + EpgTxnId + ")|AuthIdCode(" + AuthId + ")|RRN(" + RRN + ")|Response Message(" + RespCodeMsg + ")|CVRespCode(" + CVRespCode + ")");
				}

				/** Modified to redirect log into file mention in sfa.properties */
				
				/*System.out.print((Exp==null)? " ": " | " + Exp);
				System.out.println();*/
				//end of code added for SFA logs

				trace(strFunctionName, "Exiting");
			}
		}
	}



	private String getValue(String astrValue){
		if(astrValue==null){
			return "";
		}
		else{
			return URLEncoder.encode(astrValue);
		}
	}



	private String buildMerchantBillShip(ICICIMerchant aoICICIMerchant,BillToAddress aoBTA,ShipToAddress aoSTA){

		if(verbose){
			trace("buildMerchantBillShip","Entered ");

		}
		StringBuffer oBuffer = new StringBuffer();
		oBuffer.append(MERCHANT + "="+ getValue(aoICICIMerchant.getMerchantID()));
		oBuffer.append("&" + VENDOR + "="+ getValue(aoICICIMerchant.getVendor()));
		oBuffer.append("&" + PARTNER + "="+ getValue(aoICICIMerchant.getPartner()));
		oBuffer.append("&" + MSG_TYPE + "=" +  getValue(aoICICIMerchant.getMessageType()));
		oBuffer.append("&" + MER_TXN_ID + "=" +  getValue(aoICICIMerchant.getMerchantTxnID()));
		oBuffer.append("&" + RESPURL + "=" +  getValue(aoICICIMerchant.getRespURL()));
		oBuffer.append("&" + RESPMETH + "=" +  getValue(aoICICIMerchant.getRespMethod()));
		oBuffer.append("&" + ORD_REFNO + "=" + getValue(aoICICIMerchant.getOrderReferenceNo()));
		oBuffer.append("&" + INV_DT + "=" +  getValue(aoICICIMerchant.getInvoiceDate()));
		oBuffer.append("&" + INV_NO + "=" +  getValue(aoICICIMerchant.getInvoiceNo()));
		oBuffer.append("&" + INV_AMT + "=" +  getValue(aoICICIMerchant.getAmount()));
		oBuffer.append("&" + CURR_CODE + "=" +  getValue(aoICICIMerchant.getCurrCode()));
		oBuffer.append("&" + EXT1 + "=" +  getValue(aoICICIMerchant.getExt1()));
		oBuffer.append("&" + EXT2 + "=" +  getValue(aoICICIMerchant.getExt2()));
		oBuffer.append("&" + EXT3 + "=" +  getValue(aoICICIMerchant.getExt3()));
		oBuffer.append("&" + EXT4 + "=" +  getValue(aoICICIMerchant.getExt4()));
		oBuffer.append("&" + EXT5 + "=" +  getValue(aoICICIMerchant.getExt5()));
		oBuffer.append("&" + MRT_IP_ADDR + "=" +  getValue(aoICICIMerchant.getMrtIPAddress()));
		oBuffer.append("&" + GMT_OFFSET + "=" +  getValue(aoICICIMerchant.getGMTTimeOffset()));

		oBuffer.append("&" + BT_CUSTID + "=" +  getValue((aoBTA==null)? null:aoBTA.getCustomerId()));
		oBuffer.append("&" + BT_CUSTNM + "=" + getValue((aoBTA==null)? null:aoBTA.getName()));
		oBuffer.append("&" + BT_STREET1 + "=" + getValue((aoBTA==null)? null:aoBTA.getAddrLine1()));
		oBuffer.append("&" + BT_STREET2 + "=" + getValue((aoBTA==null)? null:aoBTA.getAddrLine2()));
		oBuffer.append("&" + BT_STREET3 + "=" + getValue((aoBTA==null)? null:aoBTA.getAddrLine3()));
		oBuffer.append("&" + BT_CITY + "=" + getValue((aoBTA==null)? null:aoBTA.getCity()));
		oBuffer.append("&" + BT_STATE + "=" +  getValue((aoBTA==null)? null:aoBTA.getState()));
		oBuffer.append("&" + BT_ZIP + "=" + getValue((aoBTA==null)? null:aoBTA.getZip()));
		oBuffer.append("&" + BT_COUNTRY + "=" + getValue((aoBTA==null)? null:aoBTA.getCountryAlphaCode()));
		oBuffer.append("&" + BT_MAIL + "=" + getValue((aoBTA==null)? null:aoBTA.getEmail()));

		oBuffer.append("&" + ST_STREET1 + "=" + getValue((aoSTA==null)? null:aoSTA.getAddrLine1()));
		oBuffer.append("&" + ST_STREET2 + "=" + getValue((aoSTA==null)? null:aoSTA.getAddrLine2()));
		oBuffer.append("&" + ST_STREET3 + "=" + getValue((aoSTA==null)? null:aoSTA.getAddrLine3()));
		oBuffer.append("&" + ST_CITY + "=" + getValue((aoSTA==null)? null:aoSTA.getCity()));
		oBuffer.append("&" + ST_STATE + "=" + getValue((aoSTA==null)? null:aoSTA.getState()));
		oBuffer.append("&" + ST_ZIP + "=" + getValue((aoSTA==null)? null:aoSTA.getZip()));
		oBuffer.append("&" + ST_COUNTRY + "=" + getValue((aoSTA==null)? null:aoSTA.getCountryAlphaCode()));
		if(verbose){
			trace("buildMerchantBillShip","Exiting");
		}

		return oBuffer.toString();
	}

	private String postSearchData(String astrUrlToPostTo,String astrDataToPost)
			throws SFAApplicationException {
		URL urlDataSources = null;
		URL jsseUrl = null;
		HttpURLConnection urlConnection = null;
		InputStream oInputStream = null;
		DataOutputStream outputStream = null;
		BufferedReader in = null;
		StringBuffer inputPage = null;
		String inputLine = null;
		String strFunctionName="postSearchData";
		try{
			if(verbose){
				trace(strFunctionName,"Entered");
			}
			try {
				System.setProperty("java.protocol.handler.pkgs","com.sun.net.ssl.internal.www.protocol");
				Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
				urlDataSources = new URL(astrUrlToPostTo);
				jsseUrl = new URL( "https", urlDataSources.getHost() , urlDataSources.getPort(),urlDataSources.getFile(),
						new com.sun.net.ssl.internal.www.protocol.https.Handler() );
			}
			catch(MalformedURLException oException) {
				if(verbose){
					trace(strFunctionName,"Malformed url Error : "  + oException.toString());
				}
				throw new SFAApplicationException("Invalid URL passed. Transaction cannot be processed");
			}
			if(verbose){
				trace(strFunctionName,"Created URL object");
			}


			try {

				urlConnection=(HttpURLConnection)jsseUrl.openConnection();
			}
			catch(IOException oException ) {
				if(verbose){
					trace(strFunctionName,"IOException while opening connection : "  + oException.toString());
				}
				throw new SFAApplicationException("Error while opening connection. Transaction cannot be processed");
			}


			if(verbose){
				trace(strFunctionName,"Opened URL Connection");
			}


			try {
				urlConnection.setDoOutput( true );
				urlConnection.setUseCaches (false);
				urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
				urlConnection.setRequestProperty( "Accept", "image/gif, image/x-xbitmap, image/jpeg,image/pjpeg, image/png, */*");
				urlConnection.setRequestProperty( "Accept-Language", "en");
				outputStream=new DataOutputStream(urlConnection.getOutputStream());
				outputStream.writeBytes(astrDataToPost);
				outputStream.flush();
				outputStream.close();
				outputStream = null;

			}
			catch(IOException oException) {
				if(verbose){
					trace(strFunctionName,"IOException while writing data : "  + oException.toString());
				}
				throw new SFAApplicationException("Error while writing data. Transaction cannot be processed");
			}

			if(verbose){
				trace(strFunctionName,"Written data on the output stream");
			}


			try {
				oInputStream = urlConnection.getInputStream();
				in = new BufferedReader(new InputStreamReader(oInputStream));
				inputPage	=	new	StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					if(inputLine.length() !=0 ) {
						inputPage.append(inputLine.trim()+"\n");
					}
				}
				in.close();
				in = null;
			}
			catch(IOException oException) {
				if(verbose){
					trace(strFunctionName,"IOException while reading response : "  + oException.toString() );
				}
				throw new SFAApplicationException("Error while reading data. Transaction cannot be processed");
			}

			if(verbose){
				trace(strFunctionName,"Read response on the stream");
			}

			return(inputPage.toString());

		}
		catch(SFAApplicationException oException) {
			throw oException;
		}
		catch(Exception oException) {
			if(verbose){
				trace(strFunctionName,"Error while posting data : "  + oException.toString());
			}
			throw new SFAApplicationException("Error while posting data. Transaction cannot be processed");
		}
		finally {
			try {
				if(urlConnection != null) {
					urlConnection.disconnect();
				}
			}
			catch(Exception oException) {
			}
			urlDataSources = null;
			jsseUrl = null;
			urlConnection = null;
			oInputStream = null;
			outputStream = null;
			in = null;
			inputPage = null;
			inputLine = null;
		}
	}




	private String postData(String astrUrlToPostTo,String astrDataToPost)
			throws SFAApplicationException {
		URL urlDataSources = null;
		URL jsseUrl = null;
		HttpURLConnection urlConnection = null;
		InputStream oInputStream = null;
		DataOutputStream outputStream = null;
		BufferedReader in = null;
		StringBuffer inputPage = null;
		String inputLine = null;
		String strFunctionName="postData";
		try{
			if(verbose){
				trace(strFunctionName,"Entered");
			}
			try {
				System.setProperty("java.protocol.handler.pkgs","com.sun.net.ssl.internal.www.protocol");
				Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
				urlDataSources = new URL(astrUrlToPostTo);
				jsseUrl = new URL( "https", urlDataSources.getHost() , urlDataSources.getPort(),urlDataSources.getFile(),
						new com.sun.net.ssl.internal.www.protocol.https.Handler() );
			}
			catch(MalformedURLException oException) {
				if(verbose){
					trace(strFunctionName,"Malformed url Error : "  + oException.toString() );
				}
				throw new SFAApplicationException("Invalid URL passed. Transaction cannot be processed");
			}
			if(verbose){
				trace(strFunctionName,"Created URL object");
			}


			try {

				urlConnection=(HttpURLConnection)jsseUrl.openConnection();
			}
			catch(IOException oException ) {
				if(verbose){
					trace(strFunctionName,"IOException while opening connection : "  + oException.toString());
				}
				throw new SFAApplicationException("Error while opening connection. Transaction cannot be processed");
			}


			if(verbose){
				trace(strFunctionName,"Opened URL Connection");
			}


			try {
				urlConnection.setDoOutput( true );
				urlConnection.setUseCaches (false);
				urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
				urlConnection.setRequestProperty( "Accept", "image/gif, image/x-xbitmap, image/jpeg,image/pjpeg, image/png, */*");
				urlConnection.setRequestProperty( "Accept-Language", "en");
				outputStream=new DataOutputStream(urlConnection.getOutputStream());

				outputStream.writeBytes(astrDataToPost);



				outputStream.flush();
				outputStream.close();
				outputStream = null;

			}
			catch(IOException oException) {
				if(verbose){
					trace(strFunctionName,"IOException while writing data : "  + oException.toString());
				}
				throw new SFAApplicationException("Error while writing data. Transaction cannot be processed");
			}

			if(verbose){
				trace(strFunctionName,"Written data on the output stream");
			}


			try {
				oInputStream = urlConnection.getInputStream();

				in = new BufferedReader(new InputStreamReader(oInputStream));
				inputPage	=	new	StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					if(inputLine.length() !=0 ) {
						inputPage.append(inputLine.trim());

					}
				}
				in.close();
				in = null;
			}
			catch(IOException oException) {
				if(verbose){
					trace(strFunctionName,"IOException while reading response : "  + oException.toString());
				}
				throw new SFAApplicationException("Error while reading data. Transaction cannot be processed");
			}

			if(verbose){
				trace(strFunctionName,"Read response on the stream");
			}
//			System.out.println("inputPage.toString()==>"+inputPage.toString());

			return(inputPage.toString());

		}
		catch(SFAApplicationException oException) {
			throw oException;
		}
		catch(Exception oException) {
			if(verbose){
				trace(strFunctionName,"Error while posting data : "  + oException.toString());
			}
			throw new SFAApplicationException("Error while posting data. Transaction cannot be processed");
		}
		finally {
			try {
				if(urlConnection != null) {
					urlConnection.disconnect();
				}
			}
			catch(Exception oException) {
			}
			urlDataSources = null;
			jsseUrl = null;
			urlConnection = null;
			oInputStream = null;
			outputStream = null;
			in = null;
			inputPage = null;
			inputLine = null;
		}
	}




	public PGResponse postRelated(ICICIMerchant aoICICIMerchant) {
		String strMerchantTxnId =null;
		String strFunctionName="postRelated";
		EPGMerchantEncryptionLib oEncryptionLib = null;
		String strEncryptedData = null;
		try {
			if(verbose){
				trace(strFunctionName," Entered");
			}

			if(aoICICIMerchant == null){
				if(verbose){
					trace(strFunctionName," Merchant Object passed is null ");
				}

				throw new SFAApplicationException ("Invalid merchant object passed. Transaction cannot proceed.");
			}
			strMerchantTxnId = aoICICIMerchant.getMerchantTxnID();
			if(aoICICIMerchant.getMerchantID() == null || "".equals(aoICICIMerchant.getMerchantID())) {
				if(verbose) {
					trace(strFunctionName,"Merchant Id passed is null");
				}
				throw new SFAApplicationException ("Invalid merchant id passed. Transaction cannot proceed.");

			}
			if(aoICICIMerchant.getRootTxnSysRefNum() == null) {
				if(verbose) {
					trace(strFunctionName,"Previous txn refrence no is null");
				}
				throw new SFAApplicationException ("Invalid root txn ref number passed. Transaction cannot proceed.");
			}


			if(aoICICIMerchant.getMessageType() == null) {
				if(verbose) {
					trace(strFunctionName,"Message Type passed is null");
				}
				throw new SFAApplicationException ("Invalid message type passed. Transaction cannot proceed.");

			}


			StringBuffer oStringBuffer = new StringBuffer();

			oStringBuffer.append(buildMerchantRelatedTxn(aoICICIMerchant));


			try {
				oEncryptionLib = new EPGMerchantEncryptionLib();
				strEncryptedData = oEncryptionLib.encryptMerchantData(aoICICIMerchant.getMerchantID(), strKeyDir , aoICICIMerchant.getMerchantTxnID(), aoICICIMerchant.getAmount());
			}
			catch(SFAApplicationException oException) {
				if(verbose){
					trace(strFunctionName, "SFAApplicationException in encrypting data. " +oException.getMessage());
				}
				throw oException;
			}
			catch(Exception oException) {
				if(verbose){
					trace(strFunctionName, "Exception in encrypting data. " +oException.getMessage());
				}
				throw new SFAApplicationException("Error while encrypting data. Transaction cannot be processed.");
			}


			oStringBuffer.append("&" + ENCRYPTED_DATA + "="+ strEncryptedData);
			oStringBuffer.append("&" + OS_TYPE + "="+ strOsType);
			oStringBuffer.append("&" + LANGUAGE_TYPE + "="+ aoICICIMerchant.getLanguageCode());
			oStringBuffer.append("&" + REQUEST_TYPE + "="+ REQUEST_TYPE_RELATED);


			String strRetData	= postData(epgURL,oStringBuffer.toString());

			return	new	PGResponse(strRetData);
		}
		catch(SFAApplicationException oException) {
			if(verbose){
				trace(strFunctionName,"Exception :" + oException.getMessage());
			}
			PGResponse oPGResponse = new PGResponse();
			oPGResponse.setRespCode("2");
			oPGResponse.setRespMessage(oException.getMessage());
			oPGResponse.setTxnId(strMerchantTxnId);
			return(oPGResponse);
		}
		catch(Exception oException) {
			if(verbose){
				trace("postRelatedTxn","Exception :" + oException.getMessage());
			}
			PGResponse oPGResponse = new PGResponse();
			oPGResponse.setRespCode("2");
			oPGResponse.setRespMessage("Internal Processing Error");
			oPGResponse.setTxnId(strMerchantTxnId);
			return(oPGResponse);
		}
		finally {
			oEncryptionLib = null;
			strEncryptedData = null;
			if(verbose){
				trace(strFunctionName, "Exiting");
			}
		}
	}




	public PGResponse postRelatedTxn(ICICIMerchant aoICICIMerchant) {
		if(aoICICIMerchant != null) {
			aoICICIMerchant.setLanguageCode(LANGUAGE_TYPE_JAVA);
		}
		return(postRelated(aoICICIMerchant));
	}


	public PGSearchResponse postStatusInq(ICICIMerchant aoICICIMerchant){
		if(aoICICIMerchant != null) {
			aoICICIMerchant.setLanguageCode(LANGUAGE_TYPE_JAVA);
		}
		return (postStatusInquiry(aoICICIMerchant));
	}


	public PGSearchResponse postSearchTransaction(ICICIMerchant aoICICIMerchant) {
		if(aoICICIMerchant != null) {
			aoICICIMerchant.setLanguageCode(LANGUAGE_TYPE_JAVA);
		}
		return (postTxnSearch(aoICICIMerchant));
	}


	public PGSearchResponse postStatusInquiry(ICICIMerchant aoICICIMerchant) {

		String strFunctionName="postStatusInquiry";
		EPGMerchantEncryptionLib oEncryptionLib = null;
		String strEncryptedData = null;

		try {

			if(verbose){
				trace(strEncryptedData,"Entered");
			}

			if(aoICICIMerchant == null) {
				if(verbose) {
					trace(strFunctionName,"Merchant object passed is null");
				}
				throw new SFAApplicationException("Merchant object is null");

			}

			if(aoICICIMerchant.getMerchantID() == null || "".equals(aoICICIMerchant.getMerchantID())) {
				if(verbose) {
					trace(strFunctionName,"Merchant Id passed is null");
				}
				throw new SFAApplicationException("Merchant Id is null or Invalid");

			}

			if(aoICICIMerchant.getMerchantTxnID() == null) {
				if(verbose) {
					trace(strFunctionName," Merchant Transaction Id passed is null");
				}
				throw new SFAApplicationException("Merchant Transaction Id is null");

			}

			StringBuffer oStringBuffer = new StringBuffer();

			oStringBuffer.append(MERCHANT + "="+ getValue(aoICICIMerchant.getMerchantID()));
			appendData(MER_TXN_ID , aoICICIMerchant.getMerchantTxnID(), oStringBuffer);


			try {
				oEncryptionLib = new EPGMerchantEncryptionLib();

				/*System.out.println(aoICICIMerchant.getMerchantID());
				System.out.println(strKeyDir);
				System.out.println(aoICICIMerchant.getMerchantTxnID());
				System.out.println(aoICICIMerchant.getAmount());*/

				strEncryptedData = oEncryptionLib.encryptMerchantData(aoICICIMerchant.getMerchantID(), strKeyDir , aoICICIMerchant.getMerchantTxnID(), aoICICIMerchant.getAmount());

			}
			catch(SFAApplicationException oException) {
				if(verbose){
					trace(strFunctionName, "SFAApplicationException in encrypting data. " +oException.getMessage());
				}
				throw oException;
			}
			catch(Exception oException) {
				if(verbose){
					trace(strFunctionName, "Exception in encrypting data. " +oException.getMessage());
				}


				throw new SFAApplicationException("Error while encrypting data. Transaction cannot be processed.");
			}


			appendData(ENCRYPTED_DATA ,strEncryptedData, oStringBuffer);
			appendData(OS_TYPE ,strOsType, oStringBuffer);
			appendData(LANGUAGE_TYPE, aoICICIMerchant.getLanguageCode(), oStringBuffer);
			appendData(REQUEST_TYPE, REQUEST_TYPE_STATUS_INQUIRY, oStringBuffer);

			String strRetData	= postSearchData(epgURL,oStringBuffer.toString());
			return	new	PGSearchResponse(strRetData);
		}catch(SFAApplicationException oException) {
			if(verbose){
				trace(strFunctionName,"Exception :" + oException.getMessage());
			}
			PGSearchResponse oPGSearchResponse = new PGSearchResponse();
			oPGSearchResponse.setRespCode("2");
			oPGSearchResponse.setRespMessage(oException.getMessage());
			return(oPGSearchResponse);
		}
		catch(Exception oException) {
			if(verbose){
				trace(strFunctionName,"Exception :" + oException.getMessage());
			}
			PGSearchResponse oPGSearchResponse = new PGSearchResponse();
			oPGSearchResponse.setRespCode("2");
			oPGSearchResponse.setRespMessage("Internal Processing Error");
			return(oPGSearchResponse);
		}
		finally {
			oEncryptionLib = null;
			strEncryptedData = null;
			if(verbose){
				trace(strFunctionName, "Exiting");
			}
		}
	}



	public PGSearchResponse postTxnSearch(ICICIMerchant aoICICIMerchant) {


		String strFunctionName="postTxnSearch";
		EPGMerchantEncryptionLib oEncryptionLib = null;
		String strEncryptedData = null;

		try {
			if(verbose){
				trace(strFunctionName,"Entered");
			}

			if(aoICICIMerchant == null) {
				if(verbose) {
					trace(strFunctionName,"Merchant object passed is null");
				}
				throw new SFAApplicationException("Merchant object is null");
			}
			if(aoICICIMerchant.getMerchantID() == null || "".equals(aoICICIMerchant.getMerchantID())) {
				if(verbose) {
					trace(strFunctionName,"Merchant Id passed is null");
				}
				throw new SFAApplicationException("Merchant Id is null or Invalid");

			}
			if(aoICICIMerchant.getStartDate() == null) {
				if(verbose) {
					trace(strFunctionName,"Merchant txn Start Date is null");
				}
				throw new SFAApplicationException("Merchant txn Start Date is null");

			}
			if(aoICICIMerchant.getEndDate() == null) {
				if(verbose) {
					trace(strFunctionName,"Merchant txn End Date is null");
				}
				throw new SFAApplicationException("Merchant txn End Date is null");

			}


			StringBuffer oStringBuffer = new StringBuffer();

			oStringBuffer.append(MERCHANT + "="+ getValue(aoICICIMerchant.getMerchantID()));
			appendData(TXN_SEARCH_START_DATE , aoICICIMerchant.getStartDate(), oStringBuffer);
			appendData(TXN_SEARCH_END_DATE , aoICICIMerchant.getEndDate(), oStringBuffer);

			try{

				oEncryptionLib = new EPGMerchantEncryptionLib();
				strEncryptedData = oEncryptionLib.encryptMerchantData(aoICICIMerchant.getMerchantID(), strKeyDir , aoICICIMerchant.getMerchantTxnID(), aoICICIMerchant.getAmount());
			}catch(SFAApplicationException oException) {
				if(verbose){
					trace(strFunctionName, "SFAApplicationException in encrypting data. " +oException.getMessage());
				}
				throw oException;
			}
			catch(Exception oException) {
				if(verbose){
					trace(strFunctionName, "Exception in encrypting data. " +oException.getMessage());
				}
				throw new SFAApplicationException("Error while encrypting data. Transaction cannot be processed.");
			}

			appendData(ENCRYPTED_DATA ,strEncryptedData, oStringBuffer);
			appendData(OS_TYPE ,strOsType, oStringBuffer);
			appendData(LANGUAGE_TYPE, aoICICIMerchant.getLanguageCode(), oStringBuffer);
			appendData(REQUEST_TYPE, REQUEST_TYPE_TXN_SEARCH, oStringBuffer);


			String strRetData	= postSearchData(epgURL,oStringBuffer.toString());
			return	new	PGSearchResponse(strRetData);
		}
		catch(SFAApplicationException oException) {
			if(verbose){
				trace(strFunctionName,"Exception :" + oException.getMessage());
			}
			PGSearchResponse oPGSearchResponse = new PGSearchResponse();
			oPGSearchResponse.setRespCode("2");
			oPGSearchResponse.setRespMessage(oException.getMessage());
			return(oPGSearchResponse);
		}
		catch(Exception oException) {
			if(verbose){
				trace(strFunctionName,"Exception :" + oException.getMessage());
			}
			PGSearchResponse oPGSearchResponse = new PGSearchResponse();
			oPGSearchResponse.setRespCode("2");
			oPGSearchResponse.setRespMessage("Internal Processing Error ");
			return(oPGSearchResponse);
		}
		finally {
			oEncryptionLib = null;
			strEncryptedData = null;
			if(verbose){
				trace(strFunctionName, "Exiting");
			}
		}

	}


	private String buildMerchantRelatedTxn(ICICIMerchant aoICICIMerchant) {
		try {
			if(verbose){
				trace("buildMerchantRelatedTxn","Entered");
			}
			StringBuffer oBuffer = new StringBuffer();
			oBuffer.append(MERCHANT + "="+ getValue(aoICICIMerchant.getMerchantID()));
			appendData(VENDOR, aoICICIMerchant.getVendor(), oBuffer);
			appendData(PARTNER, aoICICIMerchant.getPartner(), oBuffer);
			appendData(CURR_CODE, aoICICIMerchant.getCurrCode(), oBuffer);
			appendData(TRN_ORG, aoICICIMerchant.getRootTxnSysRefNum(), oBuffer);
			appendData(RRN_ORG, aoICICIMerchant.getRootPNRefNum(), oBuffer);
			appendData(AUTH_CD, aoICICIMerchant.getRootAuthCode(), oBuffer);
			appendData(MSG_TYPE, aoICICIMerchant.getMessageType(), oBuffer);
			appendData(INV_AMT, aoICICIMerchant.getAmount(), oBuffer);
			appendData(EXT1, aoICICIMerchant.getExt1(), oBuffer);
			appendData(EXT2, aoICICIMerchant.getExt2(), oBuffer);
			appendData(EXT3, aoICICIMerchant.getExt3(), oBuffer);
			appendData(EXT4, aoICICIMerchant.getExt4(), oBuffer);
			appendData(EXT5, aoICICIMerchant.getExt5(), oBuffer);
			appendData(MRT_IP_ADDR, aoICICIMerchant.getMrtIPAddress(), oBuffer);
			appendData(MER_TXN_ID, aoICICIMerchant.getMerchantTxnID(), oBuffer);
			appendData(GMT_OFFSET, aoICICIMerchant.getGMTTimeOffset(), oBuffer);

			return oBuffer.toString();
		}
		finally {
			if(verbose){
				trace("buildMerchantRelatedTxn","Exiting");
			}
		}
	}

	private void appendData(String astrKey, String astrValue, StringBuffer oBuffer) {
		if(astrValue != null) {
			oBuffer.append("&"+astrKey+"="+getValue(astrValue));
		}
	}

	private void trace(String s, String s1)
	{
		try
		{
			//System.out.println("[" + (new Timestamp(System.currentTimeMillis())).toString() + "]" + " |PostLib|" + s + "|" + s1);
			if(fos == null)
			{
				fos = new FileWriter("" + traceLog + "", true);
			}
			if(pw == null)
			{
				pw = new PrintWriter(fos, true);
			}
			pw.println("[" + (new Timestamp(System.currentTimeMillis())).toString() + "]" + " |PostLib|" + s + "|" + s1);
		}
		catch(IOException ioexception)
		{
			//System.out.println("Append IO error:" + ioexception);
			pw = null;
			fos = null;
		}
	}

//Added for Bharosa by Sandeep Patil on 11-06-2007 for ELTOPUSPRD-18063

	private String buildCustomerDetail(CustomerDetails aoCustomer){

		if(verbose){
			trace("buildCustomerDetail","Entered ");

		}
		StringBuffer oBuffer = new StringBuffer();
		Address aoOfficeAddress=null;
		Address aoHomeAddress=null;
		if(aoCustomer!=null)
		{
			aoOfficeAddress= aoCustomer.getOfficeAddress();
			aoHomeAddress= aoCustomer.getHomeAddress();
		}

		oBuffer.append("&" + CUSTINFOPRESENT + "=" +  getValue((aoCustomer==null)? null:aoCustomer.getCustAvailFlag()));
		oBuffer.append("&" + FIRSTNAME + "=" +  getValue((aoCustomer==null)? null:aoCustomer.getFirstName()));
		oBuffer.append("&" + LASTNAME + "=" + getValue((aoCustomer==null)? null:aoCustomer.getLastName()));
		oBuffer.append("&" + MOBILE + "=" + getValue((aoCustomer==null)? null:aoCustomer.getMobileNo()));
		oBuffer.append("&" + REGDATE + "=" + getValue((aoCustomer==null)? null:aoCustomer.getRegDate()));
		oBuffer.append("&" + ISBillNSHIPADDRMATCH + "=" + getValue((aoCustomer==null)? null:aoCustomer.getBillNShipAddrMatch()));

		oBuffer.append("&" + OFF_STREET1 + "=" + getValue((aoOfficeAddress==null)? null:aoOfficeAddress.getAddrLine1()));
		oBuffer.append("&" + OFF_STREET2 + "=" + getValue((aoOfficeAddress==null)? null:aoOfficeAddress.getAddrLine2()));
		oBuffer.append("&" + OFF_STREET3 + "=" + getValue((aoOfficeAddress==null)? null:aoOfficeAddress.getAddrLine3()));
		oBuffer.append("&" + OFF_CITY + "=" + getValue((aoOfficeAddress==null)? null:aoOfficeAddress.getCity()));
		oBuffer.append("&" + OFF_STATE + "=" +  getValue((aoOfficeAddress==null)? null:aoOfficeAddress.getState()));
		oBuffer.append("&" + OFF_ZIP + "=" + getValue((aoOfficeAddress==null)? null:aoOfficeAddress.getZip()));
		oBuffer.append("&" + OFF_COUNTRY + "=" + getValue((aoOfficeAddress==null)? null:aoOfficeAddress.getCountryAlphaCode()));

		oBuffer.append("&" + HOME_STREET1 + "=" + getValue((aoHomeAddress==null)? null:aoHomeAddress.getAddrLine1()));
		oBuffer.append("&" + HOME_STREET2 + "=" + getValue((aoHomeAddress==null)? null:aoHomeAddress.getAddrLine2()));
		oBuffer.append("&" + HOME_STREET3 + "=" + getValue((aoHomeAddress==null)? null:aoHomeAddress.getAddrLine3()));
		oBuffer.append("&" + HOME_CITY + "=" + getValue((aoHomeAddress==null)? null:aoHomeAddress.getCity()));
		oBuffer.append("&" + HOME_STATE + "=" + getValue((aoHomeAddress==null)? null:aoHomeAddress.getState()));
		oBuffer.append("&" + HOME_ZIP + "=" + getValue((aoHomeAddress==null)? null:aoHomeAddress.getZip()));
		oBuffer.append("&" + HOME_COUNTRY + "=" + getValue((aoHomeAddress==null)? null:aoHomeAddress.getCountryAlphaCode()));
		if(verbose){
			trace("buildCustomerDetail","Exiting");
		}

		return oBuffer.toString();
	}

	//Added for Bharosa by Sandeep Patil on 11-06-2007 for ELTOPUSPRD-18063
	private String buildAirlLineAndMerchanDise(AirLineTransaction aoAirLine,MerchanDise aoMerchanDise){

		if(verbose){
			trace("buildAirlLineAndMerchanDise","Entered ");

		}
		StringBuffer oBuffer = new StringBuffer();
		oBuffer.append("&" + AIRLINEINFOPRESENT + "=" +  getValue((aoAirLine==null)? null:aoAirLine.getAirLineFlag()));
		oBuffer.append("&" + BOOK_DATE + "=" +  getValue((aoAirLine==null)? null:aoAirLine.getBookingDate()));
		oBuffer.append("&" + FLT_DATE + "=" + getValue((aoAirLine==null)? null:aoAirLine.getFlightDate()));
		oBuffer.append("&" + FLT_TIME + "=" + getValue((aoAirLine==null)? null:aoAirLine.getFlighttime()));
		oBuffer.append("&" + FLT_NO + "=" + getValue((aoAirLine==null)? null:aoAirLine.getFlightNumber()));
		oBuffer.append("&" + PASS_NAME + "=" + getValue((aoAirLine==null)? null:aoAirLine.getPassengerName()));
		oBuffer.append("&" + NO_OF_TICKET + "=" + getValue((aoAirLine==null)? null:aoAirLine.getNumberOfTickets()));
		oBuffer.append("&" + CARD_NAME_N_CUST_NAME_MATCH + "=" + getValue((aoAirLine==null)? null:aoAirLine.getIsCardNameNCustomerNameMatch()));
		oBuffer.append("&" + PNR + "=" + getValue((aoAirLine==null)? null:aoAirLine.getPNR()));
		oBuffer.append("&" + SEC_FROM + "=" + getValue((aoAirLine==null)? null:aoAirLine.getSectorFrom()));
		oBuffer.append("&" + SEC_TO + "=" +  getValue((aoAirLine==null)? null:aoAirLine.getSecotrTo()));

		oBuffer.append("&" + MERCHANTDISEINFOPRESENT + "=" +  getValue((aoMerchanDise==null)? null:aoMerchanDise.getMerchantFlag()));
		oBuffer.append("&" + ITEM_PUR + "=" + getValue((aoMerchanDise==null)? null:aoMerchanDise.getItemPurchased()));
		oBuffer.append("&" + QUANT + "=" + getValue((aoMerchanDise==null)? null:aoMerchanDise.getQuantity()));
		oBuffer.append("&" + BRAND + "=" + getValue((aoMerchanDise==null)? null:aoMerchanDise.getBrand()));
		oBuffer.append("&" + MOD_NO + "=" + getValue((aoMerchanDise==null)? null:aoMerchanDise.getModelNumber()));
		oBuffer.append("&" + BUY_NAME + "=" + getValue((aoMerchanDise==null)? null:aoMerchanDise.getBuyersName()));
		oBuffer.append("&" + CARD_NAME_N_BUY_NAME_MATCH + "=" + getValue((aoMerchanDise==null)? null:aoMerchanDise.getIsCardNameNBuyerNameMatch()));

		if(verbose){
			trace("buildAirlLineAndMerchanDise","Exiting");
		}

		return oBuffer.toString();
	}
}
