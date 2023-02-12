
package com.directi.pg.core.jpbanktransfer;

import com.payment.jpbanktransfer.JPBankTransferUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;


/*
 Created by Sagar Sonar on 21-April-2020.
 */

public class JPBankTransfer
{
    public static void main(String[] args)
    {
        sale();
    }

    public static void sale()
    {
        com.directi.pg.core.valueObjects.JPBankTransferVO jpBankTransferVO = new com.directi.pg.core.valueObjects.JPBankTransferVO();
        String bankName = "HDFC";
        String shitenName = "Mumbai Branch(444)";
        String kouzaType = "Saving Account";
        String kouzaMeigi = "abc";
        String company="Worthfuture agency";
        String kouzaNm = "";
        String result="";
        String item="";
        String shitenNm ="";
        String transferId = "";
        String data = "";
        String isTest = "Y";
        String currency = "JPY";
        String items = "";
        String bid="";
        String tel="";
        String email="";
        String sid = "509999221";
        String password = "del0022";
        String uid = "wm";
        String amount = "10";
        String nameId = "";


        String url = "http://f3payment.com/wm/?sid=509999221&uid=12345abcde&am=1000&mode=json";
        try
        {
            JPBankTransfer jpBankTransfer = new JPBankTransfer();
            String response = jpBankTransfer.doHttpPostConnection(url);
            System.out.println("response---------" + response);
            JPBankTransferUtils jpBankTransfer_Utils = new JPBankTransferUtils();
            jpBankTransferVO =jpBankTransfer_Utils.readResponse(response);

            jpBankTransferVO.setBankName(bankName);
            jpBankTransferVO.setShitenName(shitenName);
            jpBankTransferVO.setKouzaType(kouzaType);
            jpBankTransferVO.setCompany(company);
            jpBankTransferVO.setKouzaMeigi(kouzaMeigi);

            System.out.println("shitenNm--->"+jpBankTransferVO.getShitenNm());
            System.out.println("bankName--->" + jpBankTransferVO.getBankName());
            System.out.println("shitenName--->" + jpBankTransferVO.getShitenName());
            System.out.println("kouzaType--->"+jpBankTransferVO.getKouzaType());
            System.out.println("kouzaNm--->"+jpBankTransferVO.getKouzaNm());
            System.out.println("kouzaMeigi--->"+jpBankTransferVO.getKouzaMeigi());
            System.out.println("bid--->"+jpBankTransferVO.getBid());
            System.out.println("tel--->"+jpBankTransferVO.getTel());
            System.out.println("email--->"+jpBankTransferVO.getEmail());
            System.out.println("company--->"+jpBankTransferVO.getCompany());
            System.out.println("nameId--->"+jpBankTransferVO.getNameId());
            System.out.println("result--->"+jpBankTransferVO.getResult());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static String doHttpPostConnection(String url)
    {
        String result = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            //post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            //post.setRequestBody(saleRequest);
            httpClient.executeMethod(post);
            String response = new String(post.getResponseBody());
            result = response;
        }
        catch (HttpException e)
        {
            System.out.println("HttpException --->" + e);
        }
        catch (IOException e)
        {
            System.out.println("IOException --->" + e);
        }
        return result;
    }
}
