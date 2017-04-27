package com.jadyer.seed.simulator.cronbuilder;

import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Set;
import java.util.StringTokenizer;

public class CronExpressionEx extends CronExpression {
    private static final long serialVersionUID = 2558244669540040228L;
    static final Integer ALL_SPEC = ALL_SPEC_INT;
    static final int NO_SPEC_INT = 98;
    private String secondsExp;
    private String minutesExp;
    private String hoursExp;
    private String daysOfMonthExp;
    private String monthsExp;
    private String daysOfWeekExp;

    CronExpressionEx(String cronExpression) throws ParseException {
        super(cronExpression);
        StringTokenizer exprsTok = new StringTokenizer(cronExpression, " \t", false);
        this.secondsExp = exprsTok.nextToken().trim();
        this.minutesExp = exprsTok.nextToken().trim();
        this.hoursExp = exprsTok.nextToken().trim();
        this.daysOfMonthExp = exprsTok.nextToken().trim();
        this.monthsExp = exprsTok.nextToken().trim();
        this.daysOfWeekExp = exprsTok.nextToken().trim();
    }

    Set<?> getSecondsSet(){
        return seconds;
    }
    public String getSecondsField(){
        return getExpressionSetSummary(seconds);
    }
    Set<?> getMinutesSet(){
        return minutes;
    }
    public String getMinutesField(){
        return getExpressionSetSummary(minutes);
    }
    Set<?> getHoursSet(){
        return hours;
    }
    public String getHoursField(){
        return getExpressionSetSummary(hours);
    }
    Set<?> getDaysOfMonthSet(){
        return daysOfMonth;
    }
    public String getDaysOfMonthField(){
        return getExpressionSetSummary(daysOfMonth);
    }
    Set<?> getMonthsSet(){
        return months;
    }
    public String getMonthsField(){
        return getExpressionSetSummary(months);
    }
    Set<?> getDaysOfWeekSet(){
        return daysOfWeek;
    }
    public String getDaysOfWeekField(){
        return getExpressionSetSummary(daysOfWeek);
    }
    String getSecondsExp() {
        return secondsExp;
    }
    String getMinutesExp() {
        return minutesExp;
    }
    String getHoursExp() {
        return hoursExp;
    }
    String getDaysOfMonthExp() {
        return daysOfMonthExp;
    }
    String getMonthsExp() {
        return monthsExp;
    }
    String getDaysOfWeekExp() {
        return daysOfWeekExp;
    }
}