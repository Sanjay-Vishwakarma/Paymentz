package com.directi.pg.core;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.logicboxes.util.Util;
import com.manager.dao.TerminalDAO;
import com.manager.vo.TerminalVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Feb 27, 2007
 * Time: 2:21:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class GatewayTypeService
{
    private static Logger log = new Logger(GatewayTypeService.class.getName());
    private static Hashtable<String, GatewayType> gatewayTypes;

    static
    {
        try
        {
            loadGatewayTypes();
        }
        catch (Exception e)
        {
            log.info("Exception in loading gateway types " + Util.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

   /* public static void main(String[] args) throws Exception
    {

*//*                Connection conn=Database.getConnection();
        Database.executeQuery("insert into gateway_type(gateway,currency,name,merchantid,chargepercentage,withdrawalcharge,reversalcharge,chargebackcharge,creationdt)\n" +
                "VALUES\n" +
                "('sdfsdfsbm','sdfsdfUSD','SBM sfsfdUsdfsdfSD Gateway','5345345',55,615,165,160,unix_timestamp(now()));",conn);

        GatewayTypeService.loadGatewayTypes();

        Database.closeConnection(conn);*//*
    }*/

    public static GatewayType getGatewayType(String typeid)
    {
        return gatewayTypes.get(typeid);
    }

    public static Hashtable<String, String> getGatewayTypes()
    {


        Hashtable<String, String> hash = new Hashtable<String, String>();

        Enumeration enumr = gatewayTypes.keys();

        while (enumr.hasMoreElements())
        {
            String accid = (String) enumr.nextElement();
            GatewayType gatewayType = gatewayTypes.get(accid);
            String displayName = gatewayType.getName();
            String gateway = gatewayType.getPgTypeId();
            hash.put(gateway, displayName);

        }
        return hash;

    }

    public static Hashtable<String, String> getGatewayTypesShipping()
    {


        Hashtable<String, String> hash = new Hashtable<String, String>();

        Enumeration enumr = gatewayTypes.keys();

        while (enumr.hasMoreElements())
        {
            String accid = (String) enumr.nextElement();
            GatewayType gatewayType = gatewayTypes.get(accid);
            String displayName = gatewayType.getName();
            String gateway = gatewayType.getGateway();
            hash.put(gateway, displayName);
        }
        return hash;

    }

    public static Hashtable<String, String> getCommonGatewayTypes()
    {
        Hashtable<String, String> hash = new Hashtable<String, String>();

        Enumeration enumr = gatewayTypes.keys();

        while (enumr.hasMoreElements())
        {
            String accid = (String) enumr.nextElement();
            GatewayType gatewayType = gatewayTypes.get(accid);
            String gateway = gatewayType.getGateway();

            if (Database.getTableName(gateway).equals("transaction_common"))
            {
                String displayName = gatewayType.getName();
                String pgTypeId = gatewayType.getPgTypeId();
                hash.put(pgTypeId, displayName);
            }
        }
        return hash;
    }

    public static Set<String> getGateways()
    {
        Set<String> hash = new HashSet<String>();

        Enumeration enumr = gatewayTypes.keys();

        while (enumr.hasMoreElements())
        {
            String accid = (String) enumr.nextElement();
            GatewayType gatewayType = gatewayTypes.get(accid);
            hash.add(gatewayType.getGateway());
        }
        return hash;
    }

    public static void loadGatewayTypes() throws Exception
    {
        log.info("Loading GatewayTypes......");
        gatewayTypes = new Hashtable<String, GatewayType>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from gateway_type", conn);
            while (rs.next())
            {
                GatewayType gateway = new GatewayType(rs);
                gatewayTypes.put(gateway.getPgTypeId(), gateway);
            }
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }

    public static LinkedHashMap<Integer, String> loadGatewayTypesAll() throws Exception
    {
        log.info("Loading GatewayTypes......");
        LinkedHashMap<Integer, String> gatewayTypes = new LinkedHashMap<Integer, String>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from gateway_type ORDER BY gateway ASC", conn);
            while (rs.next())
            {
                int pgTypeId = rs.getInt("pgTypeId");
                String gateway = rs.getString("name");
                gatewayTypes.put(pgTypeId, gateway);
            }
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return gatewayTypes;
    }

    public static LinkedHashMap<Integer, String> loadGatewayTypesByid(String id) throws Exception
    {
        log.info("Loading GatewayTypes......");
        LinkedHashMap<Integer, String> gatewayTypes = new LinkedHashMap<Integer, String>();
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            ResultSet rs = Database.executeQuery("select * from gateway_type where gateway=(select gateway from gateway_type where pgtypeid='"+id+"') ORDER BY gateway ASC", conn);
            while (rs.next())
            {
                int pgTypeId = rs.getInt("pgTypeId");
                String gateway = rs.getString("name");
                gatewayTypes.put(pgTypeId, gateway);
            }
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return gatewayTypes;
    }

    public static List<String> loadcurrency(String pgtypeid) throws Exception
    {
        List<String> gatewayCurrency = new ArrayList<String>();

        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT currency FROM gateway_type WHERE pgtypeid=?";
            PreparedStatement p = conn.prepareStatement(query);
            ResultSet rs = p.executeQuery();
            while (rs.next())
            {
                String gCurrency = "";
                gCurrency = rs.getString("currency");
                gatewayCurrency.add(gCurrency);

            }
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return gatewayCurrency;

    }

    /* public static void loadGateway() throws Exception
     {
         log.info("Loading GatewayTypes......");
         gatewayTypes = new Hashtable<String, GatewayType>();
         Connection conn = null;
         try
         {
             conn = Database.getConnection();
             ResultSet rs = Database.executeQuery("SELECT DISTINCT gateway FROM gateway_type;", conn);
             while (rs.next())
             {
                 GatewayType gateway = new GatewayType(rs);
                 gatewayTypes.put(gateway.getPgTypeId(), gateway);
             }
         }
         finally
         {
             Database.closeConnection(conn);
         }
     }*/


    public static List<String> loadCurrency() throws Exception
    {
        List<String> gatewayCurrency = new LinkedList<String>();

        Connection connection = null;

        try
        {
            connection = Database.getConnection();
            String query = "SELECT DISTINCT currency FROM gateway_type ORDER BY currency ASC";
            PreparedStatement p = connection.prepareStatement(query);

            ResultSet rs = p.executeQuery();
            while (rs.next())
            {
                String gCurrency = "";
                gCurrency = rs.getString("currency");
                gatewayCurrency.add(gCurrency);
            }
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return gatewayCurrency;
    }

    public static List<String> loadGateway() throws Exception
    {
        List<String> gatewayName = new LinkedList<String>();

        Connection connection = null;
        String gateway = "";
        try
        {
            connection = Database.getConnection();
            String query = "SELECT DISTINCT gateway from gateway_type ORDER BY gateway ASC";
            PreparedStatement p = connection.prepareStatement(query);

            ResultSet rs = p.executeQuery();
            while (rs.next())
            {
                gateway = rs.getString("gateway");
                gatewayName.add(gateway);
            }
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return gatewayName;
    }

    public static ArrayList<GatewayType> getAllGatewayTypes()
    {

        ArrayList<GatewayType> gatewayTypesArrayList = new ArrayList<GatewayType>();
        Enumeration enumr = gatewayTypes.keys();


        while (enumr.hasMoreElements())
        {
            String accid = (String) enumr.nextElement();
            GatewayType gatewayType = gatewayTypes.get(accid);
            String displayName = gatewayType.getName();
            String gateway = gatewayType.getPgTypeId();
            gatewayTypesArrayList.add(gatewayType);
        }
        return gatewayTypesArrayList;
    }

    public static TreeMap<String,GatewayType> getAllGatewayTypesMap()
    {
        TreeMap<String,GatewayType> gatewayTypesArrayMap = new TreeMap();
        for(String gatewayId : gatewayTypes.keySet()){
            GatewayType gatewayType = gatewayTypes.get(gatewayId);
            gatewayTypesArrayMap.put(gatewayType.getGateway().toUpperCase()+"-"+gatewayType.getCurrency()+"-"+gatewayType.getPgTypeId(),gatewayType);
        }
        return gatewayTypesArrayMap;
    }
    public static TreeMap<String,GatewayType> getAllGatewayTypesMap1()
    {
        TreeMap<String,GatewayType> gatewayTypesArrayMap = new TreeMap();
        for(String gatewayId : gatewayTypes.keySet()){
            GatewayType gatewayType = gatewayTypes.get(gatewayId);
            gatewayTypesArrayMap.put(gatewayId ,gatewayType);
        }
        return gatewayTypesArrayMap;
    }
    public static TreeMap<String,GatewayType> getAllGatewayTypesSortByGateway()
    {
        TreeMap<String,GatewayType> gatewayTypesArrayMap = new TreeMap();
        for(String gatewayId : gatewayTypes.keySet()){
            GatewayType gatewayType = gatewayTypes.get(gatewayId);
            gatewayTypesArrayMap.put(gatewayType.getGateway().toUpperCase()+"-"+gatewayType.getCurrency()+"-"+gatewayType.getPgTypeId() ,gatewayType);
        }
        return gatewayTypesArrayMap;
    }
    public static TreeMap<String,GatewayType> getAllGatewayTypesSortByGateway1()
    {
        TreeMap<String,GatewayType> gatewayTypesArrayMap = new TreeMap();
        for(String gatewayId : gatewayTypes.keySet()){
            GatewayType gatewayType = gatewayTypes.get(gatewayId);
            gatewayTypesArrayMap.put(gatewayType.getGateway().toUpperCase()+" "+gatewayType.getCurrency()+" "+gatewayType.getPgTypeId() ,gatewayType);
        }
        return gatewayTypesArrayMap;
    }
    public static Set<String> getGatewayHash(String gateway)
    {
        Set<String> gatewaySet = new HashSet();

        if(gateway==null || gateway.equals("") || gateway.equals("null"))
        {
            gatewaySet.addAll(GatewayTypeService.getGateways());
        }
        else
        {
            gatewaySet.add(GatewayTypeService.getGatewayType(gateway).getGateway());
        }
        return gatewaySet;
    }



    public static Map<String,List<TerminalVO>> getAllTerminalsGroupByMerchant()throws PZDBViolationException
    {

        Map<String,List<TerminalVO>> stringListMap =new HashMap<String,List<TerminalVO>>();
        List<TerminalVO> terminalVOs=null;
        TerminalVO terminalVO=null;
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            String  query= "SELECT DISTINCT mam.accountid,ga.merchantid,mam.memberid,m.contact_persons,m.company_name, gt.currency, gt.gateway FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt,members AS m WHERE mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.memberid=m.memberid ORDER BY gateway ASC";

            PreparedStatement pstmt= conn.prepareStatement(query);
            ResultSet res = pstmt.executeQuery();
            while(res.next())
            {
                terminalVO=new TerminalVO();
                String accountid= res.getString("accountid");
                terminalVO.setMemberId(res.getString("memberid"));
                terminalVO.setAccountId(accountid);
                terminalVO.setGateway(res.getString("gateway"));
                terminalVO.setCurrency(res.getString("currency"));
                terminalVO.setGwMid(res.getString("merchantid"));
                terminalVO.setContactPerson(res.getString("contact_persons"));
                terminalVO.setCompany_name(res.getString("company_name"));
                //terminalVO.set();

                //System.out.println("if vo---"+terminalVO.getMemberId()+terminalVO.getAccountId()+terminalVO.getGateway()+terminalVO.getCurrency());
                if(accountid!=null)
                {
                    terminalVOs = stringListMap.get(accountid);
                    if (terminalVOs == null)
                    {
                        terminalVOs = new ArrayList();
                        terminalVOs.add(terminalVO);

                    }
                    else
                    {
                        terminalVOs.add(terminalVO);
                    }
                    stringListMap.put(accountid, terminalVOs);
                }
            }
        }
        catch(SystemError systemError)
        {
            log.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getAllTerminalsGroupByMerchant()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getAllTerminalsGroupByMerchant()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }

        finally
        {
            Database.closeConnection(conn);
        }
        return  stringListMap;
    }

    public static Map<String,List<TerminalVO>> getAllTerminalsGroupByMerchantforCommon()throws PZDBViolationException
    {

        Map<String,List<TerminalVO>> stringListMap =new HashMap<String,List<TerminalVO>>();
        List<TerminalVO> terminalVOs=null;
        TerminalVO terminalVO=null;
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            String  query= "SELECT DISTINCT mam.accountid,ga.merchantid,mam.memberid,m.contact_persons, gt.currency, gt.gateway FROM member_account_mapping AS mam, gateway_accounts AS ga, gateway_type AS gt,members AS m WHERE mam.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND mam.memberid=m.memberid AND gt.gateway NOT IN ('qwipi','ecore') ORDER BY gateway ASC";

            PreparedStatement pstmt= conn.prepareStatement(query);
            ResultSet res = pstmt.executeQuery();
            while(res.next())
            {
                terminalVO=new TerminalVO();
                String accountid= res.getString("accountid");
                terminalVO.setMemberId(res.getString("memberid"));
                terminalVO.setAccountId(accountid);
                terminalVO.setGateway(res.getString("gateway"));
                terminalVO.setCurrency(res.getString("currency"));
                terminalVO.setGwMid(res.getString("merchantid"));
                terminalVO.setContactPerson(res.getString("contact_persons"));
                //terminalVO.set();

                //System.out.println("if vo---"+terminalVO.getMemberId()+terminalVO.getAccountId()+terminalVO.getGateway()+terminalVO.getCurrency());
                if(accountid!=null)
                {
                    terminalVOs = stringListMap.get(accountid);
                    if (terminalVOs == null)
                    {
                        terminalVOs = new ArrayList();
                        terminalVOs.add(terminalVO);

                    }
                    else
                    {
                        terminalVOs.add(terminalVO);
                    }
                    stringListMap.put(accountid, terminalVOs);
                }
            }
        }
        catch(SystemError systemError)
        {
            log.error("Leaving TerminalDAO throwing System Exception as SystemError :::: ",systemError);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(), "getAllTerminalsGroupByMerchant()", null, "Common", "System Error while connecting to the members table::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            log.error("Leaving TerminalDAO throwing SQL Exception as SystemError :::: ",e);
            PZExceptionHandler.raiseDBViolationException(TerminalDAO.class.getName(),"getAllTerminalsGroupByMerchant()",null,"Common","SQL Exception due to In correct query::", PZDBExceptionEnum.INCORRECT_QUERY,null,e.getMessage(),e.getCause());
        }

        finally
        {
            Database.closeConnection(conn);
        }
        return  stringListMap;
    }

    public static boolean isValidGatewayType(String typeid)
    {
        if(gatewayTypes.containsKey(typeid)){
            return  true;
        }
        return false;

    }
}
