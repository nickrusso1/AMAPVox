/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ird.voxelidar.voxelisation;

/**
 *
 * @author Julien Heurtebize (julienhtbe@gmail.com)
 */
public abstract class ProcessToolAdapter implements ProcessToolListener{

    @Override
    public void processProgress(String progress, int ratio) {}

    @Override
    public void processFinished(float duration) {}
    
}