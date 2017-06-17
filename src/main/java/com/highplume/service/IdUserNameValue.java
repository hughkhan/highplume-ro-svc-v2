package com.highplume.service;

/**
 * Created by Hugh on 2/16/2017.
 */
public class IdUserNameValue implements Comparable<IdUserNameValue> {
    private String id;
    private String nameFirst;
    private String nameMiddle;
    private String nameLast;
    private double value;
    private String diagnostics;

    public IdUserNameValue(String id, String nameFirst, String nameMiddle, String nameLast, double value, String diagnostics) {
        this.id = id;
        this.nameFirst = nameFirst;
        this.nameMiddle = nameMiddle;
        this.nameLast = nameLast;
        this.value = value;
        this.diagnostics = diagnostics;
    }
    public IdUserNameValue(String id, String nameFirst, String nameMiddle, String nameLast, double value) {
        this.id = id;
        this.nameFirst = nameFirst;
        this.nameMiddle = nameMiddle;
        this.nameLast = nameLast;
        this.value = value;
        this.diagnostics = "";
    }
    public IdUserNameValue() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameFirst() {
        return nameFirst;
    }

    public void setNameFirst(String nameFirst) {
        this.nameFirst = nameFirst;
    }

    public String getNameMiddle() {
        return nameMiddle;
    }

    public void setNameMiddle(String nameMiddle) {
        this.nameMiddle = nameMiddle;
    }

    public String getNameLast() {
        return nameLast;
    }

    public void setNameLast(String nameLast) {
        this.nameLast = nameLast;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDiagnostics() {
        return diagnostics;
    }

    public void setDiagnostics(String diagnostics) {
        this.diagnostics = diagnostics;
    }

    public int compareTo(IdUserNameValue compareIdUserNameValue) {

        double compareValue = ((IdUserNameValue) compareIdUserNameValue).getValue();

        //ascending order
        //return (int)(this.value - compareValue);

        //descending order
//        return (int) (compareValue - this.value);
        return (this.value < compareValue ) ? -1: (this.value > compareValue) ? 1:0 ;


    }

}
