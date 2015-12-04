/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.amap.amapvox.commons.util;

/**
 *
 * @author calcul
 */
public class Statistic {
    
    private double minValue;
    private double maxValue;
    private double mean;
    private int nbValues;
    private int nbNaNValues;
    private int nbInfiniteValues;
    
    private boolean firstValue = true;

    public Statistic() {
        
        
    }
    
    public void addValue(double value){
        
        if(Double.isNaN(value)){
            nbNaNValues++;
        }else if(Double.isInfinite(value)){
            nbInfiniteValues++;
        }else{
            if(firstValue){
            
                minValue = value;
                maxValue = value;
            }else{
                minValue= Double.min(minValue, value);
                maxValue = Double.max(maxValue, value);
            }

            mean += value;
            nbValues++;

            firstValue = false;
        }
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }
    
    public double getMean(){
        return mean / nbValues;
    }

    public int getNbNaNValues() {
        return nbNaNValues;
    }

    public int getNbInfiniteValues() {
        return nbInfiniteValues;
    }

    public int getNbValues() {
        return nbValues;
    }
    
    public int getNbTotalValues() {
        return (nbValues+nbInfiniteValues+nbNaNValues);
    }
}
