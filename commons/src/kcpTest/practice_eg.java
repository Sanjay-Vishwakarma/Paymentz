/*
package kcpTest;

import com.kcp.J_PP_CLI_N;

*/
/**
 * Created by Admin on 4/15/2020.
 *//*

public class practice_eg
{
    public static void main(String[] args)
    {

    }
    public static void sale()
    {
        J_PP_CLI_N c_PayPlus = new J_PP_CLI_N();
        String g_conf_site_cd   = "A52HN";//A52HN
        String g_conf_site_key  = "";
        String g_conf_site_name = "";
        String url    = "testpaygw.kcp.co.kr";
        String log_dir  = "D:/tomcat8/logs";
        String log_level = "3";
        String gw_port   = "8090";//gateway port
        int    g_conf_tx_mode   = 0;
        String cust_ip="192.168.1.12";
        String tran_cd  = "00100000";
        String amount  = "100000";
        String trackingId="45123132";
        String orderDescription="gbhnjmk";
        String buyr_name="test test";
        String buyr_mail="test@gmail.com";
        String phone="9858475869";
        String zip="400150";
        String rcvr_add1="14 Streets";
        String cardNumber="4546289950108110";//4546289950108110
        String expiryMonth="12";
        String expiryYear="24";
        String cvv="123";

        int payx_data_set;
        int common_data_set;

        c_PayPlus.mf_init(" ",url,gw_port,g_conf_tx_mode,log_dir);
        c_PayPlus.mf_init_set();

        payx_data_set = c_PayPlus.mf_add_set("payx_data");
        common_data_set=c_PayPlus.mf_add_set("common");

        c_PayPlus.mf_set_us(common_data_set,"amount",amount);
        c_PayPlus.mf_set_us();

    }
}
*/
