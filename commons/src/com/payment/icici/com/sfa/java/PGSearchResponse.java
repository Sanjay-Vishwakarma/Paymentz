package com.payment.icici.com.sfa.java;

import java.util.*;

public class PGSearchResponse {

		private String mstrRespCode;
		private String mstrRespMessage;
		ArrayList moPGResponseObjects;
		private static final String TXN_SUCCESS_RESP = "0";



		// default constructor
		public PGSearchResponse() {
		}

		public String getRespCode(){
			return this.mstrRespCode;
		}

		public void setRespCode(String astrRespCode) {
			this.mstrRespCode = astrRespCode;
		}

		public String getRespMessage(){
			return this.mstrRespMessage;
		}

		public void setRespMessage(String astrRespMessage) {
			this.mstrRespMessage = astrRespMessage;
		}

		public ArrayList getPGResponseObjects(){
			return this.moPGResponseObjects;
		}

		public void setPGResponseObjects(ArrayList  astrPGResponseObjects){
			moPGResponseObjects = astrPGResponseObjects;
		}

		public PGSearchResponse(String strResponse)	{

			//System.out.println("Response received in PGSearchResponse:" + strResponse);
			StringTokenizer oTokenizer = new StringTokenizer(strResponse, "\n");
			int index = 1;

			try {
				while(oTokenizer.hasMoreElements()) {
					String strToken = oTokenizer.nextToken();
					PGResponse oPGResponse = new PGResponse(strToken);
					if(index == 1) {
						mstrRespCode = oPGResponse.getRespCode();
						mstrRespMessage = oPGResponse.getRespMessage();
						if(mstrRespCode.equals(TXN_SUCCESS_RESP)) {
							moPGResponseObjects = new ArrayList();
						}
						else {
							break;
						}
					}
					else {
						moPGResponseObjects.add(oPGResponse);
					}
					index++;
				}
			}
			catch(Exception oException) {
			}
			finally {
			}

		}

		public String toString() {
			int i;
			 String str="mstrRespCode: " + mstrRespCode + "\n"+
						"mstrRespMessage : " + mstrRespMessage + "\n" ;
			 String  str2="";

			if(moPGResponseObjects != null)  {
				 for( i=0; i < moPGResponseObjects.size() ; i++  ){
						str2 = str2 +  (( PGResponse)(moPGResponseObjects.get(i))).toString() + "\n" ;

				 }
			}
			 return  str + str2 ;
	    }


}