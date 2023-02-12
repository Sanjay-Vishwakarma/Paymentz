package com.manager.vo;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 26/7/14
 * Time: 7:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccountsVO
{

    private List<AccountVO> accountVOList = new ArrayList<AccountVO>();
    
    private Set<AccountVO> accountVOSet =  new HashSet<AccountVO>();
    
    private Map<String,AccountVO>  accountVOMap = new HashMap<String, AccountVO>();


    public List<AccountVO> getAccountVOList()
    {
        return accountVOList;
    }

    public void setAccountVOList(List<AccountVO> accountVOList)
    {
        this.accountVOList = accountVOList;
    }
    
    public void addAccountVOList(AccountVO accountVO)
    {
        accountVOList.add(accountVO); 
    }

    public Map<String, AccountVO> getAccountVOMap()
    {
        return accountVOMap;
    }

    public void setAccountVOMap(Map<String, AccountVO> accountVOMap)
    {
        this.accountVOMap = accountVOMap;
    }

    public void putAccountVOMap(String key,AccountVO accountVO)
    {
        accountVOMap.put(key,accountVO);
    }

    public Set<AccountVO> getAccountVOSet()
    {
        return accountVOSet;
    }

    public void setAccountVOSet(Set<AccountVO> accountVOSet)
    {
        this.accountVOSet = accountVOSet;
    }

    public void addAccountVOSet(AccountVO accountVO)
    {
        accountVOSet.add(accountVO);
    }
}
