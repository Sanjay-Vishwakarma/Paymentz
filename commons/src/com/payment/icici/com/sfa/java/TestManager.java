package com.payment.icici.com.sfa.java;
//package commons.src.com.payment.icici.com.opus.epg.sfa.java;

import com.directi.pg.TransactionLogger;

/**
 * Created by Kiran on 20/6/15.
 */
public class TestManager
{
    private static TransactionLogger transactionLogger = new TransactionLogger(EPGMerchantEncryptionLib.class.getName());
    public static void main(String[] args)
    {
        String astrMerchantID = "00002600";
        String astrVendor = "";
        String astrPartner = "";
        String astrCustIPAddress = "122.169.97.70";
        String astrMerchantTxnID = "order97sdsdsdss";
        String astrOrderReferenceNo = "order97sdsdsdss";
        String astrRespURL = "";
        String astrRespMethod = "POST";
        String astrCurrCode = "INR";
        String astrInvoiceNo = "INV123";
        String astrMessageType = "req.Preauthorization";
        String astrAmount = "100.00";
        String astrGMTTimeOffset = "GMT+05:30";
        String astrExt1 = "Ext1";
        String astrExt2 = "Ext2";
        String astrExt3 = "Ext3";
        String astrExt4 = "Ext4";
        String astrExt5 = "Ext5a";

        ICICIMerchant ICICIMerchantDetails = new ICICIMerchant();
        ICICIMerchantDetails.setMerchantDetails(astrMerchantID, astrVendor, astrPartner, astrCustIPAddress, astrMerchantTxnID,
                astrOrderReferenceNo, astrRespURL, astrRespMethod, astrCurrCode,
                astrInvoiceNo, astrMessageType, astrAmount, astrGMTTimeOffset,
                astrExt1, astrExt2, astrExt3, astrExt4, astrExt5);

        String astrRootTxnSysRefNum = "";
        String astrRootPNRef = "";
        String astrRootAuthCode = "";

        ICICIMerchantDetails.setMerchantRelatedTxnDetails(astrMerchantID, astrVendor,
                astrPartner,
                astrMerchantTxnID, astrRootTxnSysRefNum,
                astrRootPNRef, astrRootAuthCode,
                astrRespURL, astrRespMethod, astrCurrCode,
                astrMessageType, astrAmount, astrGMTTimeOffset,
                astrExt1, astrExt2, astrExt3, astrExt4, astrExt5);


        String astrCardType = "VISA";
        String astrCardNum = "4072210290536663";
        String astrCVVNum = "377$1";
        String astrExpDtYr = "2018";
        String astrExpDtMon = "03";
        String astrNameOnCard = "";
        String astrInstrType = "CREDI";

        CardInfo cardDetails = new CardInfo();
        cardDetails.setCardDetails(astrCardType, astrCardNum, astrCVVNum,
                astrExpDtYr, astrExpDtMon, astrNameOnCard, astrInstrType);


        String astrAddrLine1 = "mumbai";
        String astrAddrLine2 = "";
        String astrAddrLine3 = "";
        String astrCity = "Mumbai";
        String astrState = "MH";
        String astrZip = "413314";
        String astrCountryAlpha = "IND";
        String astrEmail = "kiran.n@pz.com";
        ShipToAddress shipToaddressDetails = new ShipToAddress();
        shipToaddressDetails.setAddressDetails(astrAddrLine1, astrAddrLine2, astrAddrLine3, astrCity,
                astrState, astrZip, astrCountryAlpha, astrEmail);


        String astrCustomerId = "";
        String astrCustomerName = "";
        BillToAddress billToAddressDetails = new BillToAddress();
        billToAddressDetails.setAddressDetails(astrCustomerId, astrCustomerName,
                astrAddrLine1, astrAddrLine2, astrAddrLine3,
                astrCity, astrState, astrZip, astrCountryAlpha, astrEmail);


        String astrFirstName = "kiran";
        String astrLastName = "nibe";
        Address astrOfficeAddress = null;
        Address astrHomeAddress = null;
        String astrMobileNo = "";
        String astrRegDate = "";
        String astrIsBillNShipAddrMatch = "N";
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setCustomerDetails(astrFirstName, astrLastName, astrOfficeAddress,
                astrHomeAddress, astrMobileNo, astrRegDate, astrIsBillNShipAddrMatch);

//        String strRemoteAddr=request.getRemoteAddr();
        String strRemoteAddr = "122.169.97.70";
        String astrSecureCookie = "";
        String astrBrowserCountry = "";
        String astrBrowserLocalLang = "en-US";
        String astrBrowserLocalLangVariant = "";
        String astrBrowserUserAgent = "Mozilla/5.0";
        SessionDetail sessionDetails = new SessionDetail();
        sessionDetails.setSessionDetails(strRemoteAddr, astrSecureCookie, astrBrowserCountry,
                astrBrowserLocalLang, astrBrowserLocalLangVariant, astrBrowserUserAgent);


        String astrItemPurchased = "Computer";
        String astrQuantity = "4";
        String astrBrand = "";
        String astrModelNumber = "";
        String astrBuyersName = "";
        String astrIsCardNameNBuyerNameMatch = "";

        MerchanDise merchanDiseDetails = new MerchanDise();
        merchanDiseDetails.setMerchanDiseDetails(astrItemPurchased, astrQuantity, astrBrand,
                astrModelNumber, astrBuyersName,
                astrIsCardNameNBuyerNameMatch);


        String astrBookingDate = "2015-05-10";
        String astrFlightDate = "2015-05-15";
        String astrFlightTime = "12:24";
        String astrFlightNumber = "king";
        String astrPassengerName = "tim cook";
        String astrNumberOfTickets = "";
        String astrIsCardNameNCustomerNameMatch = "";
        String asrtPNR = "confirm";
        String astrSectorFrom = "boot";
        String astrSectorTo = "swap";
        AirLineTransaction airLineTransactionDetails = new AirLineTransaction();
        airLineTransactionDetails.setAirLineTransactionDetails(astrBookingDate, astrFlightDate, astrFlightTime,
                astrFlightNumber, astrPassengerName, astrNumberOfTickets,
                astrIsCardNameNCustomerNameMatch, asrtPNR, astrSectorFrom,
                astrSectorTo);


        String astrECI = "01";
        String astrXID = "NTBlZjRjMThjMjc1NTUxYzk1MTY=";
        String astrVBVStatus = "Y";
        String astrCAVV = "AAAAAAAAAAAAAAAAAAAAAAAAAAA=";
        String astrShoppingContext = "84759435";
        String astrPurchaseAmount = "1245";
        String astrCurrencyVal = "356";
        MPIData motoMPIResponseDetails = new MPIData();
        motoMPIResponseDetails.setMPIResponseDetails(astrECI,
                astrXID, astrVBVStatus,
                astrCAVV, astrShoppingContext,
                astrPurchaseAmount, astrCurrencyVal);


        String astrResPurchaseAmount = "100.20";
        String astrDisplayAmount = "INR1,234.56 ";
        String astrResCurrencyVal = "356";
        String astrExponent = "2";
        String astrOrderDesc = "G5";
        String astrRecurFreq = "12";
        String astrRecurEnd = "20011212";
        String astrInstallment = "12";
        String astrDeviceCategory = "0";
        String astrWhatIUse = "";
        String astrAcceptHdr = "image/gif";
        String astrAgentHdr = "Mozilla/4.0";

        MPIData sslMPIRequestDetails = new MPIData();
        sslMPIRequestDetails.setMPIRequestDetails(astrResPurchaseAmount, astrDisplayAmount, astrResCurrencyVal,
                astrExponent, astrOrderDesc, astrRecurFreq, astrRecurEnd, astrInstallment,
                astrDeviceCategory, astrWhatIUse, astrAcceptHdr, astrAgentHdr);


        try
        {


            PostLib postLib = new PostLib();
            PGResponse pgResponse=  postLib.postMOTO(billToAddressDetails,shipToaddressDetails, ICICIMerchantDetails,motoMPIResponseDetails,cardDetails,new PGReserveData(),customerDetails,
                    sessionDetails,airLineTransactionDetails,merchanDiseDetails) ;

            if(pgResponse!=null)
            {
                /*System.out.println("Response code is==>"+pgResponse.getRespCode());
                System.out.println("response message"+pgResponse.getRespMessage());
                System.out.println("TxnId is"+pgResponse.getTxnId());

                System.out.println("cookie are"+pgResponse.getCookie());
                System.out.println("rrn is"+pgResponse.getRRN());
                System.out.println("auth code is"+pgResponse.getAuthIdCode());
                System.out.println("cv response code"+pgResponse.getCVRespCode());
                System.out.println("epgtxnid"+pgResponse.getEpgTxnId());
                System.out.println("fdms result"+pgResponse.getFDMSResult());
                System.out.println("fdms score"+pgResponse.getFDMSScore());
                System.out.println("RedirectionTxnId"+pgResponse.getRedirectionTxnId());
                System.out.println("RedirectionUrl"+pgResponse.getRedirectionUrl());
                System.out.println("ReserveFld1"+pgResponse.getReserveFld1());
                System.out.println("ReserveFld2"+pgResponse.getReserveFld2());
                System.out.println("ReserveFld3"+pgResponse.getReserveFld3());
                System.out.println("ReserveFld4"+pgResponse.getReserveFld4());
                System.out.println("ReserveFld5"+pgResponse.getReserveFld5());
                System.out.println("ReserveFld6"+pgResponse.getReserveFld6());
                System.out.println("ReserveFld7"+pgResponse.getReserveFld7());
                System.out.println("ReserveFld8"+pgResponse.getReserveFld8());
                System.out.println("ReserveFld9"+pgResponse.getReserveFld9());
                System.out.println("ReserveFld10"+pgResponse.getReserveFld10());
                System.out.println("trans type"+pgResponse.getTxnType());
*/
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception :::::::",e);

        }















    }
}
