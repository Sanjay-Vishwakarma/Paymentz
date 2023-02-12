package payment;
import com.directi.pg.*;
import com.invoice.dao.InvoiceEntry;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.reference.DefaultEncoder;
import org.w3c.dom.Document;
import payment.util.InvoiceAPIUtil;
import payment.util.ReadXMLRequestInvoice;
import payment.util.WriteXMLResponseInvoice;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 5/10/14
 * Time: 7:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class InvoiceAPIController extends HttpServlet
{
    private static Logger log = new Logger(InvoiceAPIController.class.getName());

    private static TransactionLogger transactionLogger = new TransactionLogger(InvoiceAPIController.class.getName());



    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        PrintWriter pw=response.getWriter();

        ReadXMLRequestInvoice readXMLRequestInvoice = new ReadXMLRequestInvoice();

        WriteXMLResponseInvoice WriteXMLResponse = new  WriteXMLResponseInvoice();

        InvoiceAPIUtil invoiceAPIUtil   = new InvoiceAPIUtil();

        Merchants merchants=new Merchants();

        InvoiceEntry invoiceEntry = new InvoiceEntry();

        List<String> error = new ArrayList<String>();

        String action= "";

        String memberid= "";

        String key="";

        String statusMsg = "";

        String reponseXml ="";

        Hashtable<String,String> hashtable= new Hashtable<String,String>();

        String data=request.getParameter("data");

        Hashtable invoiceDetails=null;


        if(data.isEmpty() || "".equals(data) || data.equals("null") || data==null)
        {

            log.error("InValid Request");
            transactionLogger.error("InValid Request");

            reponseXml=WriteXMLResponse.writeResponseForCommonError("","","","InValid data");

            pw.write(reponseXml);

            return;

        }
        try
        {

            Document doc = ReadXMLRequestInvoice.createDocumentFromString(data);

            action =ReadXMLRequestInvoice.getAction(doc);



            if("generate".equals(action))
            {


                Hashtable xmlRequestHash=readXMLRequestInvoice.readRequestForGenerate(doc);

                log.debug("Entering into Invoive Generating Request :");
                transactionLogger.debug("Entering into Invoive Generating Request :");



                error = validateMandatoryParameterGenerate(xmlRequestHash);


                if(error.size()>0)
                {
                    statusMsg = "Invalid Input values---";

                    Iterator errorIt = error.iterator();

                    while (errorIt.hasNext())
                    {

                        statusMsg += errorIt.next() + "|";

                    }
                    reponseXml=WriteXMLResponse.writeResponseForCommonError(action,(String)xmlRequestHash.get("memberid"),"",statusMsg);

                    pw.write(reponseXml);

                    return;

                }

                error = validateOptionalParameterGenerate(xmlRequestHash);



                if(error.size()>0)
                {
                    statusMsg = "Invalid Input values---";

                    Iterator errorIt = error.iterator();

                    while (errorIt.hasNext())
                    {

                        statusMsg += errorIt.next() + "|";

                    }
                    reponseXml=WriteXMLResponse.writeResponseForCommonError(action,(String)xmlRequestHash.get("memberid"),"",statusMsg);

                    pw.write(reponseXml);

                    return;
                }

                memberid=(String)xmlRequestHash.get("memberid");

                String amount=(String)xmlRequestHash.get("amount");

                String orderid=(String)xmlRequestHash.get("orderid");

                String orderdesciption=(String)xmlRequestHash.get("orderdesc");

                String emailaddr=(String)xmlRequestHash.get("custemail");

                String clientChecksum=(String)xmlRequestHash.get("checksum");

                String redirecturl=(String)xmlRequestHash.get("redirecturl");

                Hashtable merchantHash=merchants.getMemberDetailsForTransaction(memberid);

                key=(String)merchantHash.get("clkey");

                String serverChecksum =invoiceAPIUtil.generateMD5ChecksumForInvoiceGenerate(memberid,amount,orderid,orderdesciption,emailaddr,redirecturl,key);

                log.debug("Server Generated CheckSum :"+serverChecksum);
                transactionLogger.debug("Server Generated CheckSum :"+serverChecksum);

                log.debug("Client checksum: "+clientChecksum);
                transactionLogger.debug("Client checksum: "+clientChecksum);

                if(!clientChecksum.equals(serverChecksum))
                {

                    log.error("InValid Checksum");
                    transactionLogger.error("InValid Checksum");

                    reponseXml=WriteXMLResponse.writeResponseForCommonError(action,memberid,"","InValid Checksum");

                    pw.write(reponseXml);

                    return;

                }

                //Put Hash Variable to Generate Invoice

                String ctoken= ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

                xmlRequestHash.put("ctoken",ctoken);

                xmlRequestHash.put("ipaddress", Functions.getIpAddress(request));

                xmlRequestHash.put("isCredential","");

                xmlRequestHash.put("username","");

                xmlRequestHash.put("password","");

                xmlRequestHash.put("question","");

                xmlRequestHash.put("answer","");

                //Call Invoice API to create invoice

                Hashtable tempHash =invoiceEntry.insertInvoice(xmlRequestHash,false);

                Integer invoiceno =(Integer)tempHash.get("invoiceno");

                invoiceDetails =(Hashtable) invoiceEntry.getInvoiceDetails(invoiceno.toString());

                //Send response

                String xmlResponseForGenerate=WriteXMLResponse.writeResponseForGenerate(invoiceDetails);

                pw.write(xmlResponseForGenerate);

                log.debug("ResponseXMl Fro Generate :"+xmlResponseForGenerate);
                transactionLogger.debug("ResponseXMl Fro Generate :"+xmlResponseForGenerate);

                log.debug("Response is Sent back to Merchant");
                transactionLogger.debug("Response is Sent back to Merchant");

                return;



            }
            else if("cancel".equals(action))
            {
                log.debug("Entering into Cancel Invoice Request");
                transactionLogger.debug("Entering into Cancel Invoice Request");

                Hashtable xmlRequestHash=readXMLRequestInvoice.readRequestForCancel(doc);

                //Validate Input Parameters mandatory & Optional

                 error = validateMandatoryParameterCancel(xmlRequestHash);

                if(error.size()>0)
                {

                    statusMsg = "Invalid Input values---";

                    Iterator errorIt = error.iterator();

                    while (errorIt.hasNext())
                    {

                        statusMsg += errorIt.next() + "|";

                    }

                    reponseXml=WriteXMLResponse.writeResponseForCommonError(action,(String)xmlRequestHash.get("memberid"),"",statusMsg);

                    pw.write(reponseXml);

                    return;

                }


                memberid=(String)xmlRequestHash.get("memberid");

                String invoiceno=(String)xmlRequestHash.get("invoiceno");

                String cancelreason=(String)xmlRequestHash.get("cancelreason");

                String redirecturl=(String)xmlRequestHash.get("redirecturl");

                String clientChecksum=(String)xmlRequestHash.get("checksum");


                //get key from db

                Hashtable merchantHash=merchants.getMemberDetailsForTransaction(memberid);

                key=(String)merchantHash.get("clkey");


                //Create the checksum for cancel invoice

                String serverChecksum =invoiceAPIUtil.generateMD5ChecksumForInvoiceCancelRegenerateRemind(memberid,invoiceno,redirecturl,key);

                log.debug("Server side CheckSum :"+serverChecksum);
                transactionLogger.debug("Server side CheckSum :"+serverChecksum);

                log.debug("Client side CheckSum :"+clientChecksum);
                transactionLogger.debug("Client side CheckSum :"+clientChecksum);

                if(!clientChecksum.equals(serverChecksum))
                {

                    log.error("InValid Checksum");
                    transactionLogger.error("InValid Checksum");

                    reponseXml=WriteXMLResponse.writeResponseForCommonError(action,memberid,"","InValid Checksum");

                    pw.write(reponseXml);

                    return;

                }



                //Call Invoice API to Cancel invoice

                  invoiceEntry.cancelInvoice(invoiceno,cancelreason);

                  log.debug("Incoice Cancelled");
                  transactionLogger.debug("Incoice Cancelled");

                //Send  Response

                 invoiceDetails=invoiceEntry.getInvoiceDetails(invoiceno);

                String xmlResponseForCancel=WriteXMLResponse.writeResponseForCancel(invoiceDetails);

                 log.debug("response for cancel :"+xmlResponseForCancel);
                 transactionLogger.debug("response for cancel :"+xmlResponseForCancel);

                 pw.write(xmlResponseForCancel);

                return;



            }
            else if("regenerate".equals(action))
            {
                log.debug("Entering into Invoice Regenerate  Invoice Request");
                transactionLogger.debug("Entering into Invoice Regenerate  Invoice Request");

                Hashtable xmlRequestHash=readXMLRequestInvoice.readRequestForRegenerate(doc);


                // Validate Input Parameters mandatory & Optional

                error = validateMandatoryParameterRegenerateRemind(xmlRequestHash);

                if(error.size()>0)
                {

                    statusMsg = "Invalid Input values---";

                    Iterator errorIt = error.iterator();

                    while (errorIt.hasNext())
                    {

                        statusMsg += errorIt.next() + "|";

                    }
                    reponseXml=WriteXMLResponse.writeResponseForCommonError(action,(String)xmlRequestHash.get("memberid"),"",statusMsg);

                    pw.write(reponseXml);

                    return;

                }


                memberid=(String)xmlRequestHash.get("memberid");

                String invoiceno=(String)xmlRequestHash.get("invoiceno");

                String clientChecksum=(String)xmlRequestHash.get("checksum");

                String redirecturl=(String)xmlRequestHash.get("redirecturl");


                // get key from db

                Hashtable merchantHash=merchants.getMemberDetailsForTransaction(memberid);

                key=(String)merchantHash.get("clkey");




                //Create the checksum for regenerate invoice

                String serverChecksum =invoiceAPIUtil.generateMD5ChecksumForInvoiceCancelRegenerateRemind(memberid,invoiceno,redirecturl,key);

                log.debug("Server side CheckSum :"+serverChecksum);
                transactionLogger.debug("Server side CheckSum :"+serverChecksum);

                log.debug("Client side CheckSum :"+clientChecksum);
                transactionLogger.debug("Client side CheckSum :"+clientChecksum);

                if(!clientChecksum.equals(serverChecksum))
                {

                    log.error("InValid Checksum");
                    transactionLogger.error("InValid Checksum");

                    reponseXml=WriteXMLResponse.writeResponseForCommonError(action,memberid,"","InValid Checksum");

                    pw.write(reponseXml);

                    return;

                }


                //get Invoice details from DB

                invoiceDetails =(Hashtable) invoiceEntry.getInvoiceDetails(invoiceno);

                //Put Hash Variable to Generate Invoice

                String ctoken= ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

                invoiceDetails.put("ctoken",ctoken);

                invoiceDetails.put("ipaddress", Functions.getIpAddress(request));


                //Call Invoice API to update status of old  invoice

                invoiceEntry.regenerateInvoice(invoiceno);

                //Call Invoice API to regenerate new   invoice
                Hashtable tempHash =invoiceEntry.insertInvoice(invoiceDetails,true);

                Integer newInvoiceno =(Integer)tempHash.get("invoiceno");

                //get invoice details of regenerated invoice
                invoiceDetails =(Hashtable) invoiceEntry.getInvoiceDetails(newInvoiceno.toString());

                //Send response

                String xmlResponseForGenerate=WriteXMLResponse.writeResponseForRegenerate(invoiceDetails);

                pw.write(xmlResponseForGenerate);

                log.debug("ResponseXMl Fro Generate :"+xmlResponseForGenerate);
                transactionLogger.debug("ResponseXMl Fro Generate :"+xmlResponseForGenerate);

                log.debug("Response is Sent back to Merchant");
                transactionLogger.debug("Response is Sent back to Merchant");

                return;


            }
            else if("remind".equals(action))
            {

                log.debug("Entering into Invoice Remind Request");
                transactionLogger.debug("Entering into Invoice Remind Request");

                Hashtable xmlRequestHash=readXMLRequestInvoice.readRequestForRemind(doc);

                // Validate Input Parameters mandatory & Optional

                error = validateMandatoryParameterRegenerateRemind(xmlRequestHash);

                if(error.size()>0)
                {

                    statusMsg = "Invalid Input values---";

                    Iterator errorIt = error.iterator();

                    while (errorIt.hasNext())
                    {

                        statusMsg += errorIt.next() + "|";

                    }

                    reponseXml=WriteXMLResponse.writeResponseForCommonError(action,(String)xmlRequestHash.get("memberid"),"",statusMsg);

                    pw.write(reponseXml);

                    return;

                }




                memberid=(String)xmlRequestHash.get("memberid");

                String invoiceno=(String)xmlRequestHash.get("invoiceno");

                String clientChecksum=(String)xmlRequestHash.get("checksum");

                String redirecturl=(String)xmlRequestHash.get("redirecturl");


                Hashtable merchantHash=merchants.getMemberDetailsForTransaction(memberid);


                // get key from db

                key=(String)merchantHash.get("clkey");

               //Create the checksum for remind invoice

                String serverChecksum =invoiceAPIUtil.generateMD5ChecksumForInvoiceCancelRegenerateRemind(memberid,invoiceno,redirecturl,key);

                log.debug("Server side CheckSum :"+serverChecksum);
                transactionLogger.debug("Server side CheckSum :"+serverChecksum);

                log.debug("Client side CheckSum :"+clientChecksum);
                transactionLogger.debug("Client side CheckSum :"+clientChecksum);

                if(!clientChecksum.equals(serverChecksum))
                {

                    log.error("InValid Checksum");
                    transactionLogger.error("InValid Checksum");

                    reponseXml=WriteXMLResponse.writeResponseForCommonError(action,memberid,"","InValid Checksum");

                    pw.write(reponseXml);

                    return;

                }



                //Call Invoice API to Remind invoice

                 InvoiceEntry.remindInvoice(invoiceno);

                //Send response

                 invoiceDetails=invoiceEntry.getInvoiceDetails(invoiceno);

                 String xmlReponseForRemind=WriteXMLResponse.writeResponseForRemind(invoiceDetails);

                 pw.write(xmlReponseForRemind);

                return;

            }
            else
            {

                log.error("InValid Action");
                transactionLogger.error("InValid Action");

                reponseXml=WriteXMLResponse.writeResponseForCommonError(action,memberid,null,"InValid Action");

                pw.write(reponseXml);

                return;

            }


        }

        catch (ParserConfigurationException e)
        {

            log.error(e);
            transactionLogger.error(e);

        }
        catch(NoSuchAlgorithmException e)
        {

            log.error(e);
            transactionLogger.error(e);

        }
        catch (Exception e)
        {

            log.error(e);
            transactionLogger.error(e);

        }


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

      doPost(request,response);

    }

    private List<String> validateMandatoryParameterGenerate(Hashtable<String, String> otherDetail)
    {

        log.error("Enter Into validateMandatoryParameterGenerate() method");
        transactionLogger.error("Enter Into validateMandatoryParameterGenerate() method");

        InputValidator inputValidator = new InputValidator();

        List<String> error = new ArrayList<String>();

        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        log.debug(" Start Reading All Mandatory Parameter");
        transactionLogger.debug(" Start Reading All Mandatory Parameter");

        hashTable.put(InputFields.MEMBERID, (String) otherDetail.get("memberid"));

        hashTable.put(InputFields.ORDERID, (String) otherDetail.get("orderid"));

        hashTable.put(InputFields.AMOUNT, (String) otherDetail.get("amount"));

        hashTable.put(InputFields.CUSTNAME, (String) otherDetail.get("custname"));

        hashTable.put(InputFields.CURRENCY, (String) otherDetail.get("currency"));

        hashTable.put(InputFields.REDIRECT_URL, (String) otherDetail.get("redirecturl"));

        hashTable.put(InputFields.EMAILADDR,(String) otherDetail.get("custemail"));

        hashTable.put(InputFields.CHECKSUM,(String) otherDetail.get("checksum"));

        log.debug("End Of  Reading All Mandatory Parameter");
        transactionLogger.debug("End Of  Reading All Mandatory Parameter");

        ValidationErrorList errorList = new ValidationErrorList();

        inputValidator.InputValidations(hashTable,errorList,false);

        if(!errorList.isEmpty())
        {

            for(InputFields inputFields :hashTable.keySet())
            {

                if(errorList.getError(inputFields.toString())!=null)
                {

                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());

                    error.add(errorList.getError(inputFields.toString()).getMessage());

                }

            }

        }

        return error;



    }

    private List<String> validateOptionalParameterGenerate(Hashtable<String, String> otherDetail)
    {
        log.error("Enter Into validateOptionalParameterGenerate() method");
        transactionLogger.error("Enter Into validateOptionalParameterGenerate() method");

        InputValidator inputValidator = new InputValidator();

        List<String> error = new ArrayList<String>();

        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        log.debug("Start Of  Reading All Optional Parameter");
        transactionLogger.debug("Start Of  Reading All Optional Parameter");

        hashTable.put(InputFields.ORDERDESCRIPTION, (String) otherDetail.get("orderdesc"));

        hashTable.put(InputFields.PAYMODE, (String) otherDetail.get("paymodeid"));

        hashTable.put(InputFields.COUNTRYCODE, (String) otherDetail.get("country"));

        hashTable.put(InputFields.STREET, (String) otherDetail.get("address"));

        hashTable.put(InputFields.CITY, (String) otherDetail.get("city"));

        hashTable.put(InputFields.TELNO, (String) otherDetail.get("phone"));

        hashTable.put(InputFields.TELCC, (String) otherDetail.get("phonecc"));

        hashTable.put(InputFields.ZIP, (String) otherDetail.get("zipcode"));

        log.debug("End Of  Reading All Optional Parameter");
        transactionLogger.debug("End Of  Reading All Optional Parameter");

        ValidationErrorList errorList = new ValidationErrorList();

        inputValidator.InputValidations(hashTable,errorList,true);

        if(!errorList.isEmpty())
        {

            for(InputFields inputFields :hashTable.keySet())
            {

                if(errorList.getError(inputFields.toString())!=null)
                {

                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());

                    error.add(errorList.getError(inputFields.toString()).getMessage());

                }

            }

        }
               return error;

    }


    private List<String> validateMandatoryParameterCancel(Hashtable<String, String> otherDetail)
    {

        log.error("Enter Into validateMandatoryParameterCancel() method");
        transactionLogger.error("Enter Into validateMandatoryParameterCancel() method");

        InputValidator inputValidator = new InputValidator();

        List<String> error = new ArrayList<String>();

        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        log.debug(" Start Reading All Mandatory Parameter");
        transactionLogger.debug(" Start Reading All Mandatory Parameter");

        hashTable.put(InputFields.MEMBERID, (String) otherDetail.get("memberid"));

        hashTable.put(InputFields.INVOICENO,(String) otherDetail.get("invoiceno"));

        hashTable.put(InputFields.INVOICE_CANCEL_REASON,(String) otherDetail.get("cancelreason"));

        hashTable.put(InputFields.REDIRECT_URL, (String) otherDetail.get("redirecturl"));

        hashTable.put(InputFields.CHECKSUM,(String) otherDetail.get("checksum"));

        log.debug("End Of  Reading All Mandatory Parameter");
        transactionLogger.debug("End Of  Reading All Mandatory Parameter");

        ValidationErrorList errorList = new ValidationErrorList();

        inputValidator.InputValidations(hashTable,errorList,false);

        if(!errorList.isEmpty())
        {

            for(InputFields inputFields :hashTable.keySet())
            {

                if(errorList.getError(inputFields.toString())!=null)
                {

                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());

                    error.add(errorList.getError(inputFields.toString()).getMessage());

                }

            }

        }


        return error;



    }


    private List<String> validateMandatoryParameterRegenerateRemind(Hashtable<String, String> otherDetail)
    {

        log.error("Enter Into validateMandatoryParameterCancelRegenerateRemind() method");
        transactionLogger.error("Enter Into validateMandatoryParameterCancelRegenerateRemind() method");

        InputValidator inputValidator = new InputValidator();

        List<String> error = new ArrayList<String>();

        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        log.debug(" Start Reading All Mandatory Parameter");
        transactionLogger.debug(" Start Reading All Mandatory Parameter");

        hashTable.put(InputFields.MEMBERID, (String) otherDetail.get("memberid"));

        hashTable.put(InputFields.INVOICENO,(String) otherDetail.get("invoiceno"));

        hashTable.put(InputFields.REDIRECT_URL, (String) otherDetail.get("redirecturl"));

        hashTable.put(InputFields.CHECKSUM,(String) otherDetail.get("checksum"));

        log.debug("End Of  Reading All Mandatory Parameter");
        transactionLogger.debug("End Of  Reading All Mandatory Parameter");

        ValidationErrorList errorList = new ValidationErrorList();

        inputValidator.InputValidations(hashTable,errorList,false);

        if(!errorList.isEmpty())
        {

            for(InputFields inputFields :hashTable.keySet())
            {

                if(errorList.getError(inputFields.toString())!=null)
                {

                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());

                    error.add(errorList.getError(inputFields.toString()).getMessage());

                }

            }

        }


        return error;


    }


}
