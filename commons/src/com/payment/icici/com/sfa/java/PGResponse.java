package com.payment.icici.com.sfa.java;

import java.util.*;
import java.net.*;

public class PGResponse{

	private String strRespCode;
	private String strRespMessage;
	private String strTxnId;
	private String strEPGTxnId;
	private String strRedirectionTxnId;
	private String strRedirectionUrl;
	private String strAuthIdCode;
	private String strRRN;
	private String strTxnType;
	private String strTxnDateTime;

	//<Ketan 13-04-2005> CR:3837

	private String strCVRespCode;
	private String strCookie;
	private String strFDMSResult;
	private String strFDMSScore;
	private String strReserveFld1;
	private String strReserveFld2;
	private String strReserveFld3;
	private String strReserveFld4;
	private String strReserveFld5;
	private String strReserveFld6;
	private String strReserveFld7;
	private String strReserveFld8;
	private String strReserveFld9;
	private String strReserveFld10;



	private static final String PG_RESP_RESPCODE = "RespCode";
	private static final String PG_RESP_RESPMSG = "Message";
	private static final String PG_RESP_EPG_TXN_ID = "ePGTxnID";
	private static final String PG_RESP_MRT_TXN_ID = "TxnID";
	private static final String PG_RESP_REDIRECT_TXN_ID = "RedirectionTxnID";
	private static final String PG_RESP_AUTH_ID = "AuthIdCode";
	private static final String PG_RESP_RRN = "RRN";
	private static final String PG_RESP_TXNTYPE = "TxnType";
	private static final String PG_RESP_TXN_DATE_TIME = "TxnDateTime";

	//<Ketan 13-04-2005> CR:3837

	private static final String PG_RESP_CVRESP_CODE = "CVRespCode";
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
	private static final String PG_RESP_COOKIE = "Cookie";
	private static final String PG_RESP_FDMSRESULT = "FDMSResult";
	private static final String PG_RESP_FDMSSCORE = "FDMSScore";


	public String getCVRespCode(){

		return this.strCVRespCode;
	}

	public void setCVRespCode(String astrCVRespCode) {
		this.strCVRespCode = astrCVRespCode;
	}

	public String getCookie(){

		return this.strCookie;
	}

	public void setCookie(String astrCookie) {
		this.strCookie = astrCookie;
	}
	public String getFDMSResult(){
		return this.strFDMSResult;
	}
	public void setFDMSResult(String astrFDMSResult) {
		this.strFDMSResult = astrFDMSResult;
	}
	public String getFDMSScore(){
		return this.strFDMSScore;
	}
	public void setFDMSScore(String astrFDMSScore) {
		this.strFDMSScore = astrFDMSScore;
	}
	public String getReserveFld1(){
		return this.strReserveFld1;
	}

	public void setReserveFld1(String astrReserveFld1) {
		this.strReserveFld1 = astrReserveFld1;
	}

	public String getReserveFld2(){
			return this.strReserveFld2;
	}

	public void setReserveFld2(String astrReserveFld2) {
		this.strReserveFld2 = astrReserveFld2;
	}

	public String getReserveFld3(){
		return this.strReserveFld3;
	}

	public void setReserveFld3(String astrReserveFld3) {
		this.strReserveFld3 = astrReserveFld3;
	}

	public String getReserveFld4(){
		return this.strReserveFld4;
	}

	public void setReserveFld4(String astrReserveFld4) {
		this.strReserveFld4 = astrReserveFld4;
	}

	public String getReserveFld5(){
		return this.strReserveFld5;
	}

	public void setReserveFld5(String astrReserveFld5) {
		this.strReserveFld5 = astrReserveFld5;
	}

	public String getReserveFld6(){
		return this.strReserveFld6;
	}

	public void setReserveFld6(String astrReserveFld6) {
		this.strReserveFld6 = astrReserveFld6;
	}

	public String getReserveFld7(){
		return this.strReserveFld7;
	}

	public void setReserveFld7(String astrReserveFld7) {
		this.strReserveFld7 = astrReserveFld7;
	}

	public String getReserveFld8(){
		return this.strReserveFld8;
	}

	public void setReserveFld8(String astrReserveFld8) {
		this.strReserveFld8 = astrReserveFld8;
	}

	public String getReserveFld9(){
		return this.strReserveFld9;
	}

	public void setReserveFld9(String astrReserveFld9) {
		this.strReserveFld9 = astrReserveFld9;
	}

	public String getReserveFld10(){
		return this.strReserveFld10;
	}

	public void setReserveFld10(String astrReserveFld10) {
		this.strReserveFld10 = astrReserveFld10;
	}





	public String getRespCode(){
		return this.strRespCode;
	}

	public void setRespCode(String astrRespCode) {
		this.strRespCode = astrRespCode;
	}

	public String getRespMessage(){
		return this.strRespMessage;
	}


	public void setRespMessage(String astrRespMessage) {
		this.strRespMessage = astrRespMessage;
	}

	public String getTxnId(){
		return this.strTxnId;
	}


	public void setTxnId(String astrTxnId) {
		this.strTxnId = astrTxnId;
	}

	public String getEpgTxnId(){
		return this.strEPGTxnId;
	}


	public void setEpgTxnId(String astrEPGTxnId) {
		this.strEPGTxnId = astrEPGTxnId;
	}


	public String getRedirectionTxnId() {
		return(strRedirectionTxnId);
	}

	public void setRedirectionTxnId(String astrRedirectionTxnId) {
		this.strRedirectionTxnId = astrRedirectionTxnId;
	}


	public String getRedirectionUrl() {
		return this.strRedirectionUrl;
	}


	public void setRedirectionUrl(String astrRedirectionUrl) {
		this.strRedirectionUrl = astrRedirectionUrl;
	}


	public String getAuthIdCode() {
		return(strAuthIdCode);
	}


	public void setAuthIdCode(String astrAuthIdCode) {
		this.strAuthIdCode = astrAuthIdCode;
	}

	public String getRRN() {
		return(strRRN);
	}

	public void setRRN(String astrRRN) {
		this.strRRN = astrRRN;
	}

	public String getTxnType() {
		return(strTxnType);
	}

	public void setTxnType(String astrTxnType) {
		this.strTxnType = astrTxnType;
	}

	public void setTxnDateTime(String astrDateTime) {
		this.strTxnDateTime = astrDateTime;
	}

	public String getTxnDateTime() {
		return(this.strTxnDateTime);
	}

	public PGResponse() {
	}

	public PGResponse(String strResponse){

		Hashtable oHashtable= new Hashtable();
		try{
			try {
				StringTokenizer oItems	= new StringTokenizer(URLDecoder.decode(strResponse),"&");
				while(oItems.hasMoreElements()) {
					try {
						String strTokenData = oItems.nextToken();
						StringTokenizer oItems1 = new StringTokenizer(strTokenData,"=");
						String strToken = oItems1.nextToken();
						String strData = oItems1.nextToken();
						oHashtable.put(strToken, strData);
					}
					catch(Exception Exception) {
					}
				}
			}
			catch(Exception oException) {
			}
			strRespCode = (String)oHashtable.get(PG_RESP_RESPCODE);


			strRespMessage = (String)oHashtable.get(PG_RESP_RESPMSG);

			strTxnId = (String)oHashtable.get(PG_RESP_MRT_TXN_ID);
			strEPGTxnId = (String)oHashtable.get(PG_RESP_EPG_TXN_ID);
			strRedirectionTxnId = (String)oHashtable.get(PG_RESP_REDIRECT_TXN_ID);
			strAuthIdCode=(String)oHashtable.get(PG_RESP_AUTH_ID);
			strRRN=(String)oHashtable.get(PG_RESP_RRN);
			strTxnType=(String)oHashtable.get(PG_RESP_TXNTYPE);
			strTxnDateTime = (String)oHashtable.get(PG_RESP_TXN_DATE_TIME);
			strCVRespCode = (String)oHashtable.get(PG_RESP_CVRESP_CODE);
			strCookie = (String)oHashtable.get(PG_RESP_COOKIE);

			strFDMSResult = (String)oHashtable.get(PG_RESP_FDMSRESULT);
			strFDMSScore  = (String)oHashtable.get(PG_RESP_FDMSSCORE);
			strReserveFld1 = (String)oHashtable.get(PG_RESP_RESERVE1);
			strReserveFld2 = (String)oHashtable.get(PG_RESP_RESERVE2);
			strReserveFld3 = (String)oHashtable.get(PG_RESP_RESERVE3);
			strReserveFld4 = (String)oHashtable.get(PG_RESP_RESERVE4);
			strReserveFld5 = (String)oHashtable.get(PG_RESP_RESERVE5);
			strReserveFld6 = (String)oHashtable.get(PG_RESP_RESERVE6);
			strReserveFld7 = (String)oHashtable.get(PG_RESP_RESERVE7);
			strReserveFld8= (String)oHashtable.get(PG_RESP_RESERVE8);
			strReserveFld9 = (String)oHashtable.get(PG_RESP_RESERVE9);
			strReserveFld10 = (String)oHashtable.get(PG_RESP_RESERVE10);

		}catch(Exception oEx){
		}
	}

	public String toString() {
		return("strRespCode: " + strRespCode + "\n"+
			"strRespMessage : " + strRespMessage + "\n" +
			"strRedirectionTxnId : " + strRedirectionTxnId+ "\n" +
			"strTxnId : " + strTxnId + "\n" +
			"strEPGTxnId : " + strEPGTxnId + "\n" +
			"strAuthIdCode : " + strAuthIdCode+ "\n" +
			"strRRN : " + strRRN + "\n" +
			"strTxnType : " + strTxnType +"\n" +
			"strTxnDateTime : " + strTxnDateTime+ "\n" +
			"strCVRespCode : " + strTxnDateTime+ "\n" +
			"strCookie : " + strCookie+ "\n" +
			"strFDMSResult : " + strFDMSResult+ "\n" +
			"strFDMSScore : " + strFDMSScore+ "\n" +
			"strReserveFld1 : " + strReserveFld1+ "\n" +
			"strReserveFld2 : " + strReserveFld2+ "\n" +
			"strReserveFld3 : " + strReserveFld3+ "\n" +
			"strReserveFld4 : " + strReserveFld4+ "\n" +
			"strReserveFld5 : " + strReserveFld5+ "\n" +
			"strReserveFld6 : " + strReserveFld6+ "\n" +
			"strReserveFld7 : " + strReserveFld7+ "\n" +
			"strReserveFld8 : " + strReserveFld8+ "\n" +
			"strReserveFld9 : " + strReserveFld9+ "\n" +
			"strReserveFld10 : " + strReserveFld10+ "\n");

	}


}