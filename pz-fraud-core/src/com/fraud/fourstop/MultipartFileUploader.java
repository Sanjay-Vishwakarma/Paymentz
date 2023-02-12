package com.fraud.fourstop;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Suraj on 2/8/2018.
 */
public class MultipartFileUploader
{
    public static void main(String[] args) {String charset = "UTF-8";
        File uploadFile1 = new File("D:/image1.jpg");
        File uploadFile2 = new File("D:/image2.jpg");
        File uploadFile3 = new File("D:/Requirements-to-get-driving-license-for-H4-Visa-holders-No-SSN.jpg");
       // File uploadFile1 = new File("D:/IMAGES_4STOP/image1.jpg");
       // File uploadFile2 = new File("D:/IMAGES_4STOP/image2.jpg");
        String requestURL = "https://api.verifyglobalrisk.com/index.php/documentIdVerify";

        try {

            MultipartUtility multipart = new MultipartUtility(requestURL, charset);

            multipart.addFormField("merchant_id", "INT-U3cz7Wi733Is1uK");
            multipart.addFormField("password", "R574QrN22yQ8XgH");
            multipart.addFormField("user_name", "suraj.tambewagh@pz.com");
            multipart.addFormField("user_number", "");
            multipart.addFormField("customer_registration_id", "438585");
            multipart.addFormField("method", "1");

            multipart.addFilePart("doc", uploadFile1);
            multipart.addFilePart("doc2", uploadFile2);
            multipart.addFilePart("doc3", uploadFile3);

            List<String> response = multipart.finish();

            //System.out.println("SERVER REPLIED:");

            for (String line : response) {
                //System.out.println(line);
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
