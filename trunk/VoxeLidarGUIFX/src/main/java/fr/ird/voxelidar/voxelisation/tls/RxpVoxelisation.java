/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ird.voxelidar.voxelisation.tls;

import fr.ird.voxelidar.voxelisation.raytracing.voxel.VoxelAnalysis;
import fr.ird.voxelidar.voxelisation.VoxelParameters;
import fr.ird.voxelidar.engine3d.math.matrix.Mat3D;
import fr.ird.voxelidar.engine3d.math.matrix.Mat4D;
import fr.ird.voxelidar.engine3d.math.vector.Vec3D;
import fr.ird.voxelidar.engine3d.math.vector.Vec4D;
import fr.ird.voxelidar.engine3d.object.scene.Dtm;
import fr.ird.voxelidar.octree.Octree;
import fr.ird.voxelidar.util.Filter;
import fr.ird.voxelidar.util.TimeCounter;
import fr.ird.voxelidar.voxelisation.PointCloud;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import org.apache.log4j.Logger;

/**
 *
 * @author Julien Heurtebize (julienhtbe@gmail.com)
 */
public class RxpVoxelisation implements Callable{
    
    private final static Logger logger = Logger.getLogger(RxpVoxelisation.class);
    private int nbVoxelisationFinished;
    private final File inputFile;
    private VoxelAnalysis voxelAnalysis;
    final Mat4D transfMatrix;
    final Mat3D rotation;

    public int getNbVoxelisationFinished() {
        return nbVoxelisationFinished;
    }

    public void setNbVoxelisationFinished(int nbVoxelisationFinished) {
        this.nbVoxelisationFinished = nbVoxelisationFinished;
    }    
    
    public RxpVoxelisation(File inputFile, File outputFile, Mat4D vopMatrix, Mat4D popMatrix, Mat4D sopMatrix, VoxelParameters parameters, Dtm terrain, Octree pointcloud, List<Filter> filters){
                
        nbVoxelisationFinished = 0;
        this.inputFile = inputFile;
        
        if(vopMatrix == null){
            vopMatrix = Mat4D.identity();
        }
        Mat4D popVop = Mat4D.multiply(popMatrix, vopMatrix);
        transfMatrix = Mat4D.multiply(sopMatrix, popVop);
        
        rotation = new Mat3D();
        rotation.mat = new double[]{
            transfMatrix.mat[0],transfMatrix.mat[1],transfMatrix.mat[2],
            transfMatrix.mat[4],transfMatrix.mat[5],transfMatrix.mat[6],
            transfMatrix.mat[8],transfMatrix.mat[9],transfMatrix.mat[10]
        };
        
        if(terrain != null){
            terrain.setTransformationMatrix(vopMatrix);
        }
        
        voxelAnalysis = new VoxelAnalysis(terrain, pointcloud, filters);
        voxelAnalysis.init(parameters, outputFile);
    }

    @Override
    public Object call() {
        
        
        try {
            logger.info("rxp extraction is started");
            
            long startTime = System.currentTimeMillis();
        
            voxelAnalysis.createVoxelSpace();
            fr.ird.voxelidar.voxelisation.extraction.tls.RxpExtraction rxpExtraction = new fr.ird.voxelidar.voxelisation.extraction.tls.RxpExtraction();
            rxpExtraction.openRxpFile(inputFile);
            
            Iterator<fr.ird.voxelidar.voxelisation.extraction.Shot> iterator = rxpExtraction.iterator();

            fr.ird.voxelidar.voxelisation.extraction.Shot shot;
            while(iterator.hasNext()){

                shot = iterator.next();
                if(shot != null){
                    
                    Vec4D locVector = Mat4D.multiply(transfMatrix, new Vec4D(shot.origin.x, shot.origin.y, shot.origin.z, 1.0d));

                    Vec3D uVector = Mat3D.multiply(rotation, new Vec3D(shot.direction.x, shot.direction.y, shot.direction.z));

                    shot.setOriginAndDirection(new Point3d(locVector.x, locVector.y, locVector.z), new Vector3d(uVector.x, uVector.y, uVector.z));
                    voxelAnalysis.processOneShot(shot);

                }

            }
            
            logger.info("Shots processed: " + voxelAnalysis.nbShotsTreated);
            logger.info("voxelisation is finished ( " + TimeCounter.getElapsedStringTimeInSeconds(startTime) + " )");

            rxpExtraction.close();
            voxelAnalysis.calculatePADAndWrite(0);
            

            if(voxelAnalysis.parameters.isCalculateGroundEnergy() && !voxelAnalysis.parameters.isTLS()){
                voxelAnalysis.writeGroundEnergy();
            }
            
            //permet de signaler au garbage collector que cet élément peut être supprimé
            voxelAnalysis = null;
        
        }catch(Exception e){
            logger.error("rxp voxelisation failed, "+e);
        }
        
        return null;
    }
}
