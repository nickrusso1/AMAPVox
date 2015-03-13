/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ird.voxelidar.util;

import java.util.ArrayList;

/**
 *
 * @author Julien
 */
public class SimpleFilter implements FilterInterface{

    @Override
    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    @Override
    public boolean doFilter(ArrayList<String> attributsNames, float[] attributs) {
        
        return false;
    }

    @Override
    public boolean doFilter(ArrayList<String> attributsNames, float attribut) {
        
        for(Filter filter : filters){
            
            float value = attribut;
            
            switch(filter.getCondition()){
                case Filter.EQUAL:
                    if(value != filter.getValue())return false;
                    break;
                case Filter.GREATER_THAN:
                    if(value <= filter.getValue())return false;
                    break;
                case Filter.GREATER_THAN_OR_EQUAL:
                    if(value < filter.getValue())return false;
                    break;
                case Filter.LESS_THAN:
                    if(value >= filter.getValue())return false;
                    break;
                case Filter.LESS_THAN_OR_EQUAL:
                    if(value > filter.getValue())return false;
                    break;
                case Filter.NOT_EQUAL:
                    if(value == filter.getValue())return false;
                    break;
            }
        }
        
        return true;
    }
    
    
}