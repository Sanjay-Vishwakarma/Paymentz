package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/1/13
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class Analysis
{
    public Criterion1 getCriterion1()
    {
        return criterion1;
    }

    public void setCriterion1(Criterion1 criterion1)
    {
        this.criterion1 = criterion1;
    }

    /*

public List<Criterion1> getCriterion()
{
    return criterion;
}

public void setCriterion(List<Criterion1> criterion)
{
    this.criterion = criterion;
}
@XStreamImplicit
private List<Criterion1> criterion =new ArrayList<Criterion1>();*/
    @XStreamAlias("Criterion")
    private Criterion1 criterion1;

    public Criterion2 getCriterion2()
    {
        return criterion2;
    }

    public void setCriterion2(Criterion2 criterion2)
    {
        this.criterion2 = criterion2;
    }

    @XStreamAlias("Criterion")
    private Criterion2 criterion2;

    public Criterion3 getCriterion3()
    {
        return criterion3;
    }

    public void setCriterion3(Criterion3 criterion3)
    {
        this.criterion3 = criterion3;
    }

    @XStreamAlias("Criterion")
    private Criterion3 criterion3;

    public Criterion4 getCriterion4()
    {
        return criterion4;
    }

    public void setCriterion4(Criterion4 criterion4)
    {
        this.criterion4 = criterion4;
    }

    @XStreamAlias("Criterion")
    private Criterion4 criterion4;
}
