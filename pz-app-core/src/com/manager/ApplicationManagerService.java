package com.manager;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.logicboxes.util.Util;
import com.vo.applicationManagerVOs.BankTypeVO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TreeMap;

/**
 * Created by Sneha on 6/15/2017.
 */
public class ApplicationManagerService
{
    private static Logger log = new Logger(ApplicationManagerService.class.getName());
    private static Hashtable<String, BankTypeVO> bankTypes;
    private static TreeMap<String, BankTypeVO> parterbankTypes;

       public static BankTypeVO getBankType(String typeid)
    {
        try
        {
            loadBankTypes();

        }
        catch (Exception e)
        {
            log.info("Exception in loading bank types " + Util.getStackTrace(e));
            throw new RuntimeException(e);
        }
        return bankTypes.get(typeid);
    }

    public static ArrayList<BankTypeVO> getAllBankTypes()
    {

        try
        {
            loadBankTypes();

        }
        catch (Exception e)
        {
            log.info("Exception in loading bank types " + Util.getStackTrace(e));
            throw new RuntimeException(e);
        }
        ArrayList<BankTypeVO> bankTypesArrayList = new ArrayList<BankTypeVO>();
        Enumeration enumr = bankTypes.keys();

        while (enumr.hasMoreElements())
        {
            String accid = (String) enumr.nextElement();
            BankTypeVO bankType = bankTypes.get(accid);
            String displayName = bankType.getBankName();
            String gateway = bankType.getBankId();
            bankTypesArrayList.add(bankType);
        }
        return bankTypesArrayList;
    }

    public static TreeMap<String,BankTypeVO> getAllPartnerBankTypeMap()
    {


        try
        {
            loadPartnerBankTypes();

        }
        catch (Exception e)
        {
            log.info("Exception in loading bank types " + Util.getStackTrace(e));
            throw new RuntimeException(e);
        }
        return parterbankTypes;
    }

    public static void loadBankTypes() throws Exception
    {
        log.info("Loading BankTypeVO......");
        bankTypes = new Hashtable<String, BankTypeVO>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from bank_template_mapping", conn);
            while (rs.next())
            {
                BankTypeVO bankTypeVO = new BankTypeVO();
                bankTypeVO.setBankId(rs.getString("bank_id"));
                bankTypeVO.setBankName(rs.getString("bank_name"));
                bankTypeVO.setFileName(rs.getString("template_name"));
                bankTypes.put(bankTypeVO.getBankId(), bankTypeVO);
            }
            log.debug("bankTypes--->"+bankTypes);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public static void loadPartnerBankTypes() throws Exception
    {
        log.info("Loading BankTypeVO......");
        parterbankTypes = new TreeMap<String, BankTypeVO>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("SELECT PB.bank_id,BT.bank_name,BT.template_name,PB.partner_id FROM bank_template_mapping AS BT JOIN partner_bank_mapping AS PB ON PB.bank_id=BT.bank_id", conn);
            while (rs.next())
            {
                BankTypeVO bankTypeVO = new BankTypeVO();
                bankTypeVO.setBankId(rs.getString("bank_id"));
                bankTypeVO.setBankName(rs.getString("bank_name"));
                bankTypeVO.setFileName(rs.getString("template_name"));
                bankTypeVO.setPartnerId(rs.getString("partner_id"));
                parterbankTypes.put(rs.getString("bank_name").toUpperCase()+ "-" +rs.getString("partner_id")+ "-"  +rs.getString("bank_id"), bankTypeVO);
            }
            log.debug("parterbankTypes--->"+parterbankTypes);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
}
