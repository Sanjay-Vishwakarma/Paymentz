package com.payment.kcpmain;

import com.directi.pg.Functions;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.kcp.J_PP_CLI_N;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.apache.axis.i18n.RB;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by Admin on 1/22/2020.
 */
public class KcpMain
{
    public static void main(String[] args)
    {
        sale();
    }
    public static String doHttpPostConnection(String url, String request)
    {
        String result = "";
        PostMethod post = new PostMethod(url);
        try
        {
            HttpClient httpClient = new HttpClient();
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestBody(request);
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
    public static void sale()
    {
        String  g_conf_gw_url="testpaygw.kcp.co.kr";
        String  g_conf_home_dir ="home/ v7_hub_mac_jsp";
        String  g_conf_key_dir="C:\\kcp\\v7_hub_card_windows_asp\\bin\\pub.key";
        String  g_conf_log_dir="C:\\kcp\\log ";
        String  g_conf_log_level="3";
        String  g_conf_gw_port="8090";
        String  g_conf_tx_mode="1";
        String  g_conf_site_cd=" ";
        String  g_conf_site_key="site_key";
        String  g_conf_site_name="TEST SHOP";
        StringBuffer request = new StringBuffer();
        request.append("g_conf_gw_url=" + g_conf_gw_url + "&g_conf_home_dir=" + g_conf_home_dir + "&g_conf_key_dir=" + g_conf_key_dir + "&g_conf_log_dir=" + g_conf_log_dir + "&g_conf_log_level=" + g_conf_log_level +"&g_conf_gw_port=" + g_conf_gw_port + "&g_conf_tx_mode=" + g_conf_tx_mode + "&g_conf_site_cd=" + g_conf_site_cd
                + "&g_conf_site_key=" + g_conf_site_key + "&g_conf_site_name=" + g_conf_site_name);

        System.out.println("request-------" + request);
        String response = doHttpPostConnection(g_conf_gw_url,request.toString());
        System.out.println("response---------" + response);
    }
    public static void refund()
    {
        String g_conf_site_cd   = "AO01W";//A52HN
        String g_conf_site_key  = "";
        String g_conf_site_name = "";
        String ip="198.168.1.2";
        String trackingID="198.168.1.2";

        J_PP_CLI_N c_PayPlus = new J_PP_CLI_N();
        int g_conf_tx_mode = 0;
        String log_level = "3";
        String gw_port   = "8090";//gateway port
        String log_dir  = "D:/tomcat8/logs";

        String refundUrl = "testpaygw.kcp.co.kr";
        c_PayPlus.mf_init("", refundUrl, gw_port, g_conf_tx_mode, log_dir);
        c_PayPlus.mf_init_set();

        int     mod_data_set_no;

        String tran_cd = "00200000";
        mod_data_set_no = c_PayPlus.mf_add_set( "mod_data" );

        c_PayPlus.mf_set_us( mod_data_set_no, "req_tx", "mod");
        c_PayPlus.mf_set_us( mod_data_set_no, "tno", "20286929969943");
        c_PayPlus.mf_set_us( mod_data_set_no, "mod_type",  "STPC");      // Type of original transaction change request
        c_PayPlus.mf_set_us( mod_data_set_no, "mod_ip", ip);      // refund requester IP
        c_PayPlus.mf_set_us( mod_data_set_no, "mod_desc","fvgbhj");      // refund for change
        //c_PayPlus.mf_set_us( mod_data_set_no, "mod_desc",desc);      // refund for change


        if ( tran_cd.length() > 0 )
        {
            c_PayPlus.mf_do_tx( g_conf_site_cd, g_conf_site_key, tran_cd, ip, trackingID, log_level, "1" );
        }
        else
        {
            c_PayPlus.m_res_cd  = "9562";
            c_PayPlus.m_res_msg = "연동 오류";
        }
        String res_code  = c_PayPlus.m_res_cd;                      // res_code
        String res_msg = c_PayPlus.m_res_msg;
        System.out.println("res_code---"+res_code);
        System.out.println("res_msg---"+res_msg);
    }

    public static void enquiry()
    {
        String mod_type  = "STSQ";
        String pay_type = "PACA";
        String tno="20284929938060";//Payemnt Id
        String trackingID="173437";String g_conf_site_cd   = "AO01W";//A52HN
        String g_conf_site_key  = "";
        String g_conf_site_name = "";
        String ip="198.168.1.2";
        String shop_status="";
        String app_time="";
        String app_no="";



        J_PP_CLI_N c_PayPlus = new J_PP_CLI_N();
        int g_conf_tx_mode = 0;
        String log_level = "3";
        String gw_port   = "8090";//gateway port
        String log_dir  = "D:/tomcat8/logs";

        String refundUrl = "testpaygw.kcp.co.kr";
        c_PayPlus.mf_init("", refundUrl, gw_port, g_conf_tx_mode, log_dir);
        c_PayPlus.mf_init_set();

        int     mod_data_set_no;

        String tran_cd = "00200000";
        mod_data_set_no = c_PayPlus.mf_add_set("mod_data");

        c_PayPlus.mf_set_us( mod_data_set_no, "req_tx", "mod");
        c_PayPlus.mf_set_us( mod_data_set_no, "tno", tno);
        c_PayPlus.mf_set_us( mod_data_set_no, "mod_type",  mod_type);      // Type of original transaction change request
        c_PayPlus.mf_set_us( mod_data_set_no, "pay_type", pay_type);      // refund requester IP
        c_PayPlus.mf_set_us( mod_data_set_no, "mod_ordr_idxx",trackingID);      // refund for change
        //c_PayPlus.mf_set_us( mod_data_set_no, "mod_desc",desc);      // refund for change


        if ( tran_cd.length() > 0 )
        {
            c_PayPlus.mf_do_tx( g_conf_site_cd, g_conf_site_key, tran_cd, ip, trackingID, log_level, "1" );
        }
        else
        {
            c_PayPlus.m_res_cd  = "9562";
            c_PayPlus.m_res_msg = "연동 오류";
        }
        String res_code  = c_PayPlus.m_res_cd;                      // res_code
        String res_msg = c_PayPlus.m_res_msg;

        System.out.println("res_code---"+res_code);
        System.out.println("res_msg---"+res_msg);
        System.out.println("shop_status---"+c_PayPlus.mf_get_res("shop_status"));
        System.out.println("app_time---"+c_PayPlus.mf_get_res("app_time"));
        System.out.println("app_no---"+c_PayPlus.mf_get_res("app_no"));
        System.out.println("status---"+c_PayPlus.mf_get_res("stat_ca_cd"));
    }
}
