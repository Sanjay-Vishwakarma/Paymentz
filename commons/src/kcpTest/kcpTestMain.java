package kcpTest;

/**
 * Created by Admin on 1/30/2020.
 */

import com.kcp.J_PP_CLI_N;
/**
 * Created by Admin on 1/29/2020.
 */
class kcpTestMain
{
    public static void main(String[] args)
    {
        sale();
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

        c_PayPlus.mf_init( "", url, gw_port, g_conf_tx_mode, log_dir );
        c_PayPlus.mf_init_set();

        payx_data_set   = c_PayPlus.mf_add_set( "payx_data" );
        common_data_set = c_PayPlus.mf_add_set( "common" );

        c_PayPlus.mf_set_us( common_data_set, "amount"  , amount);
        c_PayPlus.mf_set_us( common_data_set, "currency", "840"       ); // 840 USD
        c_PayPlus.mf_set_us( common_data_set, "cust_ip" , cust_ip     );


        c_PayPlus.mf_add_rs( payx_data_set, common_data_set );


        int ordr_data_set;
        ordr_data_set = c_PayPlus.mf_add_set( "ordr_data" );
        c_PayPlus.mf_set_us( ordr_data_set, "ordr_idxx", trackingId );
        c_PayPlus.mf_set_us( ordr_data_set, "good_name", orderDescription );
        c_PayPlus.mf_set_us( ordr_data_set, "buyr_name", buyr_name );
        c_PayPlus.mf_set_us( ordr_data_set, "buyr_mail", buyr_mail );

        int rcvr_data_set;
        rcvr_data_set = c_PayPlus.mf_add_set( "rcvr_data" );

        c_PayPlus.mf_set_us( rcvr_data_set, "rcvr_name", buyr_name );
        c_PayPlus.mf_set_us( rcvr_data_set, "rcvr_tel1", phone );
        //c_PayPlus.mf_set_us( rcvr_data_set, "rcvr_tel2", rcvr_tel2 );
        c_PayPlus.mf_set_us( rcvr_data_set, "rcvr_mail", buyr_mail );
        c_PayPlus.mf_set_us( rcvr_data_set, "rcvr_zipx", zip );
        c_PayPlus.mf_set_us( rcvr_data_set, "rcvr_add1", rcvr_add1 );
        //c_PayPlus.mf_set_us( rcvr_data_set, "rcvr_add2", rcvr_add2 );

        int card_data_set;

        card_data_set = c_PayPlus.mf_add_set( "card" );

        c_PayPlus.mf_set_us( card_data_set, "card_tx_type", "11111000" );
        c_PayPlus.mf_set_us( card_data_set, "card_mny",     amount   );
        c_PayPlus.mf_set_us( card_data_set, "quota",        "00" );
        c_PayPlus.mf_set_us( card_data_set, "card_no",      cardNumber );
        c_PayPlus.mf_set_us( card_data_set, "card_expiry",   expiryYear+ expiryMonth );
        c_PayPlus.mf_set_us( card_data_set, "card_cvn",     cvv );

        c_PayPlus.mf_add_rs( payx_data_set, card_data_set );
        if ( tran_cd.length() > 0 )
        {
            c_PayPlus.mf_do_tx( g_conf_site_cd, g_conf_site_key, tran_cd, cust_ip, trackingId,log_level, "1" );
        }
        else
        {
            c_PayPlus.m_res_cd  = "9562";
            c_PayPlus.m_res_msg = "연동 오류(Interlocking error)";
        }
        String res_code  = c_PayPlus.m_res_cd;  //response Code
        String res_msg = c_PayPlus.m_res_msg;  //response description

        System.out.println("response code--->"+res_code);
        System.out.println("response msg--->"+res_msg);
    }
}

