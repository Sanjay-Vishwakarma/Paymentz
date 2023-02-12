package com.payment.flwBarter;

import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;

/**
 * Created by Balaji on 17-Jan-20.
 */
public class FlutterWaveBarterPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(FlutterWaveBarterPaymentProcess.class.getName());
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D){

//        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\">"+
//                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
//                "</form>"+
//                "<script language=\"javascript\"> document.launch3D.submit(); </script>";

        transactionLogger.error("3d page url --" + response3D.getUrlFor3DRedirect());
        transactionLogger.error("term url = " + response3D.getTerURL());
        String iFrame = "" +
                "<iframe src=\""+response3D.getUrlFor3DRedirect()+"\" style=\"height:100%;width:100%;border:none;\"></iframe>" +
                "<form method='POST' action='"+response3D.getTerURL()+"/transaction/FlutterWaveBarterFrontEndServlet' id='confirmForm' name='confirmForm'></form>" +
                "<script type = \"text/javascript\" src=\""+response3D.getTerURL()+"/merchant/transactionCSS/js/jquery.min.js\"></script>\n" +
                "<script>" +
                "var apiCall = \"\";" +
                "apiCall =  setInterval('inquiryCall()',3000);" +
                "function inquiryCall() {" +
                "console.log('inside inquiry');" +
                "$.ajax({\n" +
                "        url: '"+response3D.getTerURL()+"/transaction/FlutterWaveBarterInquiryServlet',\n" +
                "        async: false,\n" +
                "        type: 'POST',\n" +
                "        data: {trackingId: \""+trackingId+"\"},\n" +
                "        success: function (data, status)\n" +
                "        {\n" +
                "            console.log(data);\n" +
                "            if(data.status != 'pending3DConfirmation'){\n" +
                "               clearInterval(apiCall);\n" +
                "                   var f = document.getElementById(\"confirmForm\");\n" +
                "                   var inp = document.createElement(\"input\");\n" +
                "                   inp.setAttribute(\"type\",\"hidden\");\n" +
                "                   inp.setAttribute(\"name\",\"trackingId\");\n" +
                "                   inp.setAttribute(\"value\",\""+trackingId+"\");\n" +
                "                   f.appendChild(inp)\n" +
                "                   f.submit()" +
                "           }" +
                "        },\n" +
                "        error: function (xhr, status, error)\n" +
                "        {\n" +
                "               clearInterval(apiCall);\n" +
                "            console.log(status, error);\n" +
                "        }\n" +
                "    });" +
                "}" +
                "</script>"
                ;

        return iFrame;
    }
    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        transactionLogger.error("inside FlutterWavePaymentProcess set3DResponseVO method---" + response3D.getUrlFor3DRedirect());
            String trackingId = directKitResponseVO.getTrackingId();
            String iFrame = "" +
                    "<iframe src=\""+response3D.getUrlFor3DRedirect()+"\" style=\"height:100%;width:100%;border:none;\"></iframe>" +
                    "<form method='POST' action='"+response3D.getTerURL()+"/transaction/FlutterWaveBarterFrontEndServlet' id='confirmForm' name='confirmForm'></form>" +
                    "<script type = \"text/javascript\" src=\""+response3D.getTerURL()+"/merchant/transactionCSS/js/jquery.min.js\"></script>" +
                    "<script>" +
                    "var apiCall = \"\";" +
                    "apiCall =  setInterval('inquiryCall()',3000);" +
                    "function inquiryCall() {" +
                    "console.log('inside inquiry');" +
                    "$.ajax({" +
                    "        url: '"+response3D.getTerURL()+"/transaction/FlutterWaveBarterInquiryServlet'," +
                    "        async: false," +
                    "        type: 'POST'," +
                    "        data: {trackingId: \""+trackingId+"\"}," +
                    "        success: function (data, status)" +
                    "        {" +
                    "            console.log(data);" +
                    "            if(data.status != 'pending3DConfirmation'){" +
                    "               clearInterval(apiCall);" +
                    "                   var f = document.getElementById(\"confirmForm\");" +
                    "                   var inp = document.createElement(\"input\");" +
                    "                   inp.setAttribute(\"type\",\"hidden\");" +
                    "                   inp.setAttribute(\"name\",\"trackingId\");" +
                    "                   inp.setAttribute(\"value\",\""+trackingId+"\");" +
                    "                   f.appendChild(inp);" +
                    "                   f.submit();" +
                    "           }" +
                    "        }," +
                    "        error: function (xhr, status, error)" +
                    "        {" +
                    "               clearInterval(apiCall);\n" +
                    "            console.log(status, error);" +
                    "        }" +
                    "    });" +
                    "}" +
                    "</script>"
                    ;
        directKitResponseVO.setBankRedirectionUrl(iFrame);
    }
}
