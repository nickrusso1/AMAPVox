/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.amap.lidar.amapvox.simulation.hemi;

import fr.amap.lidar.amapvox.commons.LidarScan;
import fr.amap.lidar.amapvox.voxelisation.EchoFilter;
import java.io.File;
import java.util.List;
import javax.vecmath.Point3d;

/**
 *
 * @author calcul
 */
public class HemiParameters {
    
    public enum Mode{
        
        ECHOS(0),
        PAD(1);
        
        private final int mode;

        private Mode(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return mode;
        }
    }
    
    public enum BitmapMode{
        
        PIXEL(0),
        COLOR(1);
        
        private final int mode;

        private BitmapMode(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return mode;
        }
    }
    
    private Mode mode;
    
    //echos mode
    private List<LidarScan> rxpScansList;
    private EchoFilter echoFilter;
    
    //PAD mode
    private File voxelFile;
    //private Point3d sensorPosition;
    private List<Point3d> sensorPositions;
    
    //common parameters
    private int pixelNumber;
    private int azimutsNumber = 36;
    private int zenithsNumber = 9;
    
    //output
    private File outputTextFile;
    private File outputBitmapFile;
    private BitmapMode bitmapMode;
    private boolean generateBitmapFile;
    private boolean generateTextFile;

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public List<LidarScan> getRxpScansList() {
        return rxpScansList;
    }

    public void setRxpScansList(List<LidarScan> rxpScansList) {
        this.rxpScansList = rxpScansList;
    }

    public File getVoxelFile() {
        return voxelFile;
    }

    public void setVoxelFile(File voxelFile) {
        this.voxelFile = voxelFile;
    }

    /*public Point3d getSensorPosition() {
        return sensorPosition;
    }

    public void setSensorPosition(Point3d sensorPosition) {
        this.sensorPosition = sensorPosition;
    }*/

    public List<Point3d> getSensorPositions() {
        return sensorPositions;
    }

    public void setSensorPositions(List<Point3d> sensorPositions) {
        this.sensorPositions = sensorPositions;
    }

    public int getPixelNumber() {
        return pixelNumber;
    }

    public void setPixelNumber(int pixelNumber) {
        this.pixelNumber = pixelNumber;
    }

    public int getAzimutsNumber() {
        return azimutsNumber;
    }

    public void setAzimutsNumber(int azimutsNumber) {
        this.azimutsNumber = azimutsNumber;
    }

    public int getZenithsNumber() {
        return zenithsNumber;
    }

    public void setZenithsNumber(int zenithsNumber) {
        this.zenithsNumber = zenithsNumber;
    }

    public File getOutputTextFile() {
        return outputTextFile;
    }

    public void setOutputTextFile(File outputTextFile) {
        this.outputTextFile = outputTextFile;
    }

    public File getOutputBitmapFile() {
        return outputBitmapFile;
    }

    public void setOutputBitmapFile(File outputBitmapFile) {
        this.outputBitmapFile = outputBitmapFile;
    }

    public BitmapMode getBitmapMode() {
        return bitmapMode;
    }

    public void setBitmapMode(BitmapMode bitmapMode) {
        this.bitmapMode = bitmapMode;
    }

    public boolean isGenerateBitmapFile() {
        return generateBitmapFile;
    }

    public void setGenerateBitmapFile(boolean generateBitmapFile) {
        this.generateBitmapFile = generateBitmapFile;
    }

    public boolean isGenerateTextFile() {
        return generateTextFile;
    }

    public void setGenerateTextFile(boolean generateTextFile) {
        this.generateTextFile = generateTextFile;
    }

    public EchoFilter getEchoFilter() {
        return echoFilter;
    }

    public void setEchoFilter(EchoFilter echoFilter) {
        this.echoFilter = echoFilter;
    }
    
}
