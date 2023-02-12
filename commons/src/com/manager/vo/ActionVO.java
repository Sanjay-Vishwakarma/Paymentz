package com.manager.vo;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 9/2/14
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionVO
{

    String actionCriteria;
    String yesOrNoCriteria;//if any present for use not always has to be initialized

    boolean view = false;
    boolean edit = false;
    boolean create = false;

    boolean update=false;
    boolean add=false;
    boolean delete=false;
    boolean mail=false;

    public boolean isView()
    {
        return view;
    }

    public void setView()
    {
        this.view = true;
    }

    public boolean isEdit()
    {
        return edit;
    }

    public void setEdit()
    {
        this.edit = true;
    }

    public boolean isCreate()
    {
        return create;
    }

    public void setCreate()
    {
        this.create = true;
    }

    public String getActionCriteria()
    {
        return actionCriteria;
    }

    public void setActionCriteria(String actionCriteria)
    {
        this.actionCriteria = actionCriteria;
    }

    public boolean isUpdate()
    {
        return update;
    }

    public void setUpdate()
    {
        this.update = true;
    }

    public boolean isAdd()
    {
        return add;
    }

    public void setAdd()
    {
        this.add = true;
    }

    public boolean isDelete()
    {
        return delete;
    }

    public void setDelete()
    {
        this.delete = true;
    }

    public String getYesOrNoCriteria()
    {
        return yesOrNoCriteria;
    }

    public void setYesOrNoCriteria(String yesOrNoCriteria)
    {
        this.yesOrNoCriteria = yesOrNoCriteria;
    }

    public boolean isMail()
    {
        return mail;
    }

    public void setMail()
    {
        this.mail = true;
    }

    public void setAllContentAuto(String actionString)
    {
        if(actionString.contains("View"))
        {
            setView();
        }
        if(actionString.contains("Edit"))
        {
            setEdit();
        }
        if(actionString.contains("Update"))
        {
            setUpdate();
        }
        if(actionString.contains("Add"))
        {
            setAdd();
        }
        if(actionString.contains("Delete"))
        {
            setDelete();
        }
        if(actionString.contains("Mail"))
        {
            setMail();
        }

        setActionCriteria(actionString);
    }
}
