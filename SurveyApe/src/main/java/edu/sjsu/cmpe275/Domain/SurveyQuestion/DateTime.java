package edu.sjsu.cmpe275.Domain.SurveyQuestion;

import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.stream.IntStream;

@Proxy(lazy=false)
@Entity
@DiscriminatorValue("DateTime")
public class DateTime extends SurveyQuestion{

    @Column
    private int month; //1-12
    @Column
    private int day; //1-31
    @Column
    private int year; //xxxx

    private static int[] months  = IntStream.rangeClosed(1, 12).toArray();
    private static int[] days  = IntStream.rangeClosed(1, 31).toArray();
    private static int[] years  = IntStream.rangeClosed(1990, 2018).toArray();

    public DateTime(){

    }

    public DateTime(String question){
        super(question);
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public static int[] getMonths() {
        return months;
    }

    public static void setMonths(int[] months) {
        DateTime.months = months;
    }

    public static int[] getDays() {
        return days;
    }

    public static void setDays(int[] days) {
        DateTime.days = days;
    }

    public static int[] getYears() {
        return years;
    }

    public static void setYears(int[] years) {
        DateTime.years = years;
    }
}
