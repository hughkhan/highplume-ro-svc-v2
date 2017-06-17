package com.highplume.service;

import java.util.List;

/**
 * Created by Hugh on 3/4/2017.
 */
public class Analysis {
    public Analysis(){};

    private static double[] _msds (double[] x) {
        int n = x.length;
        if (n==0) return new double[]{0,0,0};
        if (n==1) return new double[]{x[0],0,x[0]};
        double summ = 0.0;
        double mull = 1.0;
        for (int j=0; j<x.length; j++){
            summ += x[j];
            mull *= x[j];
        }
        double theMean = summ / n;
        double summSquared = 0.0;
        for (int k=0; k<x.length; k++){
            double squaredDiff = Math.pow((theMean - x[k]),2.0);
            summSquared += squaredDiff;
        }
        double std = Math.sqrt(summSquared / (n - 1));

        double[] ans= {theMean, std, summ};
        return ans;
    }

    public static double COTScore(List<Object[]> results, double totNumOfDepts, int ownDeptIdx, double corpGRAvg, boolean diag, StringBuilder diagInfo) {      //Assumes deptTotals inversely sorted, ownDeptIdx=-1 means no GR
        if (results.size() == 0){
            diagInfo.append("0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,-1");
            return 0.0;
        }

        double[] userTotalsPerDept = new double[results.size()];
        for (int j=0; j < results.size(); j++) {
            userTotalsPerDept[j] = Double.parseDouble(results.get(j)[0].toString());   //load array to send to msds
        }
        double[] ans = _msds (userTotalsPerDept);                            //mean=ans[0], stddev=ans[1], sum=ans[2]

        double score = 0.0;
        boolean ownDeptGR = (ownDeptIdx != -1);                     //OwnDeptIdx==-1 means no giving or receiving to own dept
        boolean ownDeptHighest = (ownDeptIdx == 0);                 //deptTotals is inversely sorted.

        if (ownDeptGR) score += corpGRAvg * 0.5;

        //If user gave to or received the most from his/her own dept
        if (ownDeptHighest) {
            score += corpGRAvg;                                     //If GR activity is higher in own dept than other departments
            score += ans[1]*2.0;                                    //Add std dev because higher the better.  Skewed result around own dept best
        }
        else {
            score += ans[1]/2.0;                                    //If the COT lies outside his/her own then still give but give half
        }

        //Figure out the skew and then score based on that.
        //Only count depts whose totals (each) are higher than
        //50% of the highest dept total.
        int i = 1;                                                  //not counting own dept [0]
        for (; i<userTotalsPerDept.length; i++){
            if (userTotalsPerDept[i] < userTotalsPerDept[0]/2.0 ){                    //find how many dept totals are higher than 50% of the highest dept.
                break;
            }
        }

        if (i < totNumOfDepts/2.0){                               //If skewed enough give score.  That is, if less than half of depts
            if (ownDeptHighest)                                     //have totals higher than 50% of the highest total
                //score += 5.0;
                score += corpGRAvg;
            else
                //score += 2.0;
                score += corpGRAvg * 0.5;
        }

        if (diag) {
            diagInfo.append(String.format("%.2f", score) + ",");
            diagInfo.append(String.format("%.2f", ans[0]) + ",");
            diagInfo.append(String.format("%.2f", ans[1]) + ",");
            diagInfo.append(String.format("%.2f", ans[2]) + ",");
            diagInfo.append(String.format("%.2f", userTotalsPerDept.length/totNumOfDepts) + ",");
            diagInfo.append(results.size() + ",");
            diagInfo.append(totNumOfDepts);

            for (int k = 0; k < userTotalsPerDept.length; k++) {
                diagInfo.append("," + userTotalsPerDept[k]);
            }
            diagInfo.append("," + ownDeptIdx);
        }

        return (ans[2] + score)/corpGRAvg;
    }

/*    public static double COTScore(double[] deptTots, double totNumOfDepts, int ownDeptIdx, double corpGRAvg){      //Assumes deptTotals inversely sorted, ownDeptIdx=-1 means no GR

        if (deptTots.length == 0) return 0.0;  //No TU given or received

        double score = 0.0;
        boolean ownDeptGR = (ownDeptIdx != -1);                     //OwnDeptIdx==-1 means no giving or receiving to own dept
        boolean ownDeptHighest = (ownDeptIdx == 0);                 //deptTotals is inversely sorted.
        double[] ans = _msds (deptTots);                            //mean=ans[0], stddev=ans[1], sum=ans[2]

        //If user gave to or received from his/her own dept
//        if (ownDeptGR) score += 3.0;
        if (ownDeptGR) score += corpGRAvg * 0.5;

        //If user gave to or received the most from his/her own dept
        if (ownDeptHighest) {
            //score += 5.0;                                         //If GR activity is higher in own dept than other departments
            score += corpGRAvg;                                     //If GR activity is higher in own dept than other departments
            score += ans[1]*2.0;                                    //Add std dev because higher the better.  Skewed result around own dept best
        }
        else {
            score += ans[1]/2.0;                                    //If the COT lies outside his/her own then still give but give half
        }

        //Figure out the skew and then score based on that.
        //Only count depts whose totals are higher than
        //50% of the highest dept total.
        int i = 1;                                                  //not counting own dept [0]
        for (; i<deptTots.length; i++){
            if (deptTots[i] < deptTots[0]/2.0 ){                    //find how many dept totals are higher than 50% of the highest dept.
                break;
            }
        }

//        if (i < deptTots.length/2.0){                             //If skewed enough give score.  That is, if less than half of depts
          if (i < totNumOfDepts/2.0){                               //If skewed enough give score.  That is, if less than half of depts
            if (ownDeptHighest)                                     //have totals higher than 50% of the highest total
                //score += 5.0;
                score += corpGRAvg;
            else
                //score += 2.0;
                score += corpGRAvg * 0.5;
        }

        return (ans[2] + score)/corpGRAvg;
    }*/

    //------------------

/*//    public static double COIScore(double[] deptTots, double totNumOfDepts, int ownDeptIdx, double corpGRAvg) {      //Assumes deptTotals inversely sorted, ownDeptIdx=-1 means no GR
    public static double COIScore(List<Object[]> results, double totNumOfDepts, int ownDeptIdx, double corpGRAvg) {      //Assumes deptTotals inversely sorted, ownDeptIdx=-1 means no GR

        if (deptTots.length == 0) return 0.0;                       //No TU given or received

        double score = 0.0;
        double[] ans = _msds (deptTots);                            //mean=ans[0], stddev=ans[1], sum=ans[2]
        double ratio = deptTots.length/totNumOfDepts;

        if (ratio > 0.90){
            score += corpGRAvg * 2;
        } else if (ratio > 0.66){
            score += corpGRAvg;
        } else if (ratio > 0.33){
            score += corpGRAvg * 0.50;
        }

        score -= deptTots[ownDeptIdx]/2;    //count only half from own department or none?

        return (ans[2] + score)/corpGRAvg;
    }*/

    public static double COIScore(List<Object[]> results, double totNumOfDepts, int ownDeptIdx, double corpGRAvg, boolean diag, StringBuilder diagInfo) {      //Assumes deptTotals inversely sorted, ownDeptIdx=-1 means no GR

        if (results.size() == 0){
            diagInfo.append("0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,-1");
            return 0.0;
        }

        double[] userTotalsPerDept = new double[results.size()];
        boolean ownDeptGR = (ownDeptIdx != -1);                     //OwnDeptIdx==-1 means no giving or receiving to own dept


        for (int j=0; j < results.size(); j++) {
            userTotalsPerDept[j] = Double.parseDouble(results.get(j)[0].toString());   //load array to send to msds
        }

//        if (userTotalsPerDept.length == 0) return 0.0;                       //No TU given or received

        double score = 0.0;
        double[] ans = _msds (userTotalsPerDept);                            //mean=ans[0], stddev=ans[1], sum=ans[2]
        double ratio = userTotalsPerDept.length/totNumOfDepts;

        if (ratio > 0.90){
            score += corpGRAvg * 2;
        } else if (ratio > 0.66){
            score += corpGRAvg;
        } else if (ratio > 0.33){
            score += corpGRAvg * 0.50;
        }

        if (ownDeptGR) score -= userTotalsPerDept[ownDeptIdx]/2;    //count only half from own department or (none?)

        if (diag) {
            diagInfo.append(String.format("%.2f", score) + ",");
            diagInfo.append(String.format("%.2f", ans[0]) + ",");
            diagInfo.append(String.format("%.2f", ans[1]) + ",");
            diagInfo.append(String.format("%.2f", ans[2]) + ",");
            diagInfo.append(String.format("%.2f", ratio) + ",");
            diagInfo.append(results.size() + ",");
            diagInfo.append(totNumOfDepts);

            for (int k = 0; k < userTotalsPerDept.length; k++) {
                diagInfo.append("," + userTotalsPerDept[k]);
            }
            diagInfo.append("," + ownDeptIdx);
        }
        return (ans[2] + score)/corpGRAvg;
    }
}
