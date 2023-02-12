package com.directi.pg.core.paylineVoucher;

import com.directi.pg.Logger;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.PayLineVoucherGateway;
import com.directi.pg.core.valueObjects.*;
import com.directi.pg.SystemError;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.common.core.CommAddressDetailsVO;

import java.net.InetAddress;

/**
 * Created by IntelliJ IDEA.
 * User: admin1
 * Date: Sep 8, 2012
 * Time: 9:30:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestPaylineVoucher
{
    private static Logger log = new Logger(TestPaylineVoucher.class.getName());
    public static void main(String[] args)
    {

        //System.out.println("Calling auth ====");
               processSales();

    }


    public static void processSales()
    {

        PayLineVoucherGateway pg = null;
        String accountid ="4";

         GenericCardDetailsVO cardDetailVO= new GenericCardDetailsVO();
         CommAddressDetailsVO addressDetailVO= new CommAddressDetailsVO();
         CommTransactionDetailsVO transDetailVO = new CommTransactionDetailsVO();
         PayLineVoucherRequestVO requestDetail=null;
         PayLineVoucherResponseVO  transRespDetails=null;
         try
        {
            log.debug("Getting Abstarct gateway===  account Id=" + accountid);
            pg =(PayLineVoucherGateway) AbstractPaymentGateway.getGateway(accountid);

           // if(PayVTPaymentGateway.class.isIs)
        }
        catch (SystemError systemError)
        {   log.error("System Error:::::::::"+systemError);


        }



        InetAddress ownIP = null;
        try{
             ownIP=InetAddress.getLocalHost();
             log.debug("IP of my system is := "+ownIP.getHostAddress());
       }catch (Exception e){
       log.error("Exception caught ="+e.getMessage());
       }



        String  transAmount = "21";
        String trackingID="11111111111";
        String  id = "b1d4da97-bd32-460e-838e-4c27aeae0b3f";
        String  currency ="USD";
                //pg.getCurrency();
        String  ipaddress = ownIP.getHostAddress();
        String  orderDes = "Test Item";




        String  firstname = "Joanne";
        String  lastname = "Farley";
        String  address = "42 Lake Road";
        String  city = "Clemons";
        String  state = "New York";
        String  ccode = "US";
        String  zip = "12819";
        String  phone = "1234567890";
        String  email = "joannefarley@mail.com";



         try
         {
         addressDetailVO.setStreet(address);
         addressDetailVO.setCity(city);
         addressDetailVO.setState(state);
         addressDetailVO.setZipCode(zip);
         addressDetailVO.setCountry(ccode);
         addressDetailVO.setPhone(phone);
         addressDetailVO.setEmail(email);;
         addressDetailVO.setIp(ipaddress);
         addressDetailVO.setFirstname(firstname);
         addressDetailVO.setLastname(lastname);


        transDetailVO.setAmount(transAmount);
        transDetailVO.setCurrency(currency);
        transDetailVO.setOrderId(trackingID);
        transDetailVO.setOrderDesc(orderDes);



          requestDetail = new PayLineVoucherRequestVO();
          requestDetail.setCompany("");
          requestDetail.setSalutation("");
          requestDetail.setTitle("");
          requestDetail.setMobile("");
          requestDetail.setAddressDetailsVO(addressDetailVO);
          requestDetail.setTransDetailsVO(transDetailVO);
          requestDetail.setId(id);

          transRespDetails = (PayLineVoucherResponseVO) pg.processSale(trackingID, requestDetail);

          /* transDetailVO.setReferenceId("ff80808139de74470139ee179b6572c9");
           requestDetail = new PayLineVoucherRequestVO(id,addressDetailVO, transDetailVO);
           transRespDetails = (PayLineVoucherResponseVO) pg.processVoid(trackingID, requestDetail);
*/
             
           //System.out.println(transRespDetails.toString());

         }
       catch(Exception e)
       {
           log.error("Exception Thrown "+e.getMessage());
       }




    }

}
