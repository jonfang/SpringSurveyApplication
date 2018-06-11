package edu.sjsu.cmpe275.Domain.SurveyResult;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("YesNoResult")
public class YesNoResult extends SurveyResult{
    @Column
    private int yes;
    @Column
    private int no;

    public int getYes() {
        return yes;
    }

    public void setYes(int yes) {
        this.yes = yes;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }
}
