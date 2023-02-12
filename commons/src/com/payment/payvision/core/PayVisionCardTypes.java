package com.payment.payvision.core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 4/28/13
 * Time: 11:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayVisionCardTypes
{

    private static HashMap<String, Integer> cardTypes = new HashMap<String, Integer>();


    static
    {

        cardTypes.put("EurocardMastercard".toUpperCase(), 1);
        cardTypes.put("Visa".toUpperCase(), 2);
        cardTypes.put("Amex".toUpperCase(), 3);
        cardTypes.put("Diners".toUpperCase(), 4);
        cardTypes.put("Jcb".toUpperCase(), 5);
        cardTypes.put("CarteBleue".toUpperCase(), 6);
        cardTypes.put("Galeria".toUpperCase(), 7);
        cardTypes.put("Delta".toUpperCase(), 8);
        cardTypes.put("Laser".toUpperCase(), 9);
        cardTypes.put("Solo".toUpperCase(), 10);
        cardTypes.put("Switch".toUpperCase(), 11);
        cardTypes.put("EnRoute".toUpperCase(), 12);
        cardTypes.put("Discover".toUpperCase(), 13);
        cardTypes.put("VisaDebit".toUpperCase(), 14);
        cardTypes.put("VisaElectron".toUpperCase(), 15);
        cardTypes.put("MastercardDebit".toUpperCase(), 16);
        cardTypes.put("CarteBlanche".toUpperCase(), 17);
        cardTypes.put("PrivateLabel".toUpperCase(), 18);
        cardTypes.put("Beneficial".toUpperCase(), 19);
        cardTypes.put("Gecc".toUpperCase(), 20);


    }


    public static Integer getNumericCardType(String cardTypeString)
    {
        return cardTypes.get(cardTypeString.toUpperCase());
    }


}
