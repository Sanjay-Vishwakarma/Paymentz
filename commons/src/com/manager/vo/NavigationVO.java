package com.manager.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/21/15
 * Time: 6:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class NavigationVO
{
    private int i =1;
    Map<Integer,String> stepAndPageName=new HashMap<Integer,String>();
    Integer nextPageNO=0;
    Integer previousPageNO=0;
    Integer currentPageNO=0;
    boolean conditionalValidation=true;
    boolean uploadHit=false;

    public Map<Integer, String> getStepAndPageName()
    {
        return stepAndPageName;
    }

    public void addStepAndPageName(String pageName)
    {
        this.stepAndPageName.put(i,pageName);
        i++;
    }

    public String getShowPageName(Integer stepNo)
    {
        return stepAndPageName.get(stepNo);
    }

    public Integer getStepNo(String pageName)
    {
        for(Map.Entry<Integer,String> entry : stepAndPageName.entrySet())
        {
            if(pageName.equals(entry.getValue()))
            {
                return entry.getKey() ;
            }
        }
        return 0;
    }

    public int getNextPageNO()
    {
        return nextPageNO;
    }

    public void setNextPageNO(Integer nextPageNO)
    {
        this.nextPageNO = nextPageNO;
    }

    public int getPreviousPageNO()
    {
        return previousPageNO;
    }

    public void setPreviousPageNO(Integer previousPageNO)
    {
        this.previousPageNO = previousPageNO;
    }

    public void setCurrentPageNO(Integer currentPageNO)
    {
        this.currentPageNO = currentPageNO;
    }

    public int getCurrentPageNO()
    {
        if(this.nextPageNO!=0)
        {
            return nextPageNO-1;
        }
        else if(this.previousPageNO!=0)
        {
            return previousPageNO+1;
        }
        else if(this.currentPageNO!=0)
        {
            return currentPageNO;
        }
        return 0;
    }

    public boolean isConditionalValidation()
    {
        return conditionalValidation;
    }

    public void setConditionalValidation(boolean conditionalValidation)
    {
        this.conditionalValidation = conditionalValidation;
    }

    public boolean isUploadHit()
    {
        return uploadHit;
    }

    public void setUploadHit(boolean uploadHit)
    {
        this.uploadHit = uploadHit;
    }
}
