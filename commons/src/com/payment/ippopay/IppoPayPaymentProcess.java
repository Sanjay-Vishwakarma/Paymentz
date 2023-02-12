package com.payment.ippopay;

import com.directi.pg.Functions;
import com.directi.pg.IppoPayTransactionLogger;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZRefundRequest;
import com.payment.validators.vo.CommonValidatorVO;

import java.util.HashMap;

/**
 * Created by Admin on 10/17/2020.
 */
public class IppoPayPaymentProcess  extends CommonPaymentProcess
{

        //private static TransactionLogger transactionLogger=new TransactionLogger(IppoPayPaymentProcess.class.getName());
        private static IppoPayTransactionLogger transactionLogger = new IppoPayTransactionLogger(IppoPayPaymentProcess.class.getName());

    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D){
            transactionLogger.error("inside  IppoPayPaymentProcess Form---");
            String order_id =response3D.getTransactionId();
            String responsePublic_key=response3D.getResponseHashInfo();
            transactionLogger.error("inside  order_id ---"+order_id);
            transactionLogger.error("inside responsePublic_key---"+responsePublic_key);
            String form="";
            form+="<html>";
            form+="<head></head>";
            form+="<body>";
            form+="<script type=\"text/javascript\" src=\"https://js.ippopay.com/scripts/ippopay.v1.js\"></script>";
            form+="<script type=\"text/javascript\">";
            form+="var order_id;";
            form+="var options = {\n" +
                    "    \"order_id\" : \""+order_id+"\", //Get order_id params value from Create Order from next step\n" +
                    "    \"public_key\" : \""+responsePublic_key+"\"\n" +
                    "}\n" +
                    "var ipay = new Ippopay(options);\n" +
                    "ipay.open();\n" +
                    "ipay.close();\n" +
                    "\n" +
                    "ippopayHandler(response, function (e) {\n" +
                    "    if(e.data.status == 'success'){\n" +
                    "        console.log(e.data)\n" +
                    "document.getElementById(\"data\").value=JSON.stringify(e.data);\n"+
                    "document.getElementById(\"status\").value=e.data.status;\n"+
                    "document.IppopayResponse.submit();\n"+
                    "    }\n" +
                    "    if(e.data.status == 'failure'){\n" +
                    "        console.log(e.data)\n" +
                    "document.getElementById(\"data\").value=JSON.stringify(e.data);\n"+
                    "document.getElementById(\"status\").value=e.data.status;\n"+
                    "document.IppopayResponse.submit();\n"+
                    "    }\n" +
                    "});";
            form+= "</script>" ;
            form+="<form name=\"IppopayResponse\" method=\"POST\" action=\"" +response3D.getTerURL()+ "\">" ;
            form+="<input type=\"hidden\" name=\"TermUrl\"  value=\"\">";
            form+="<input type=\"hidden\" name=\"data\" id=\"data\"  value=\"\">";
            form+="<input type=\"hidden\" name=\"status\" id=\"status\"  value=\"\">";
            form+="</form>";
            form+="</body>";
            form+="</html>";
            transactionLogger.error("ippopayPaymentProcess Form---"+form.toString());
            return form.toString();
        }


    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {

        transactionLogger.debug("Inside ICardPaymentProcess setRefundVOParamsextension -----");
        int trackingid = refundRequest.getTrackingId();
        IppoPayUtils ippoPayUtils=new IppoPayUtils();
        CommTransactionDetailsVO transactionDetailsVO = requestVO.getTransDetailsVO();
        String paymentid=ippoPayUtils.getPaymentid(String.valueOf(trackingid));
        transactionLogger.error("paymentid---"+paymentid);
        transactionDetailsVO.setPreviousTransactionId(paymentid);
        requestVO.setTransDetailsVO(transactionDetailsVO);
    }
}
