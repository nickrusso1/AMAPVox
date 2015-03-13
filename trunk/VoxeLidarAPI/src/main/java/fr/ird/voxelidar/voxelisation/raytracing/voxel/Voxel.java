/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ird.voxelidar.voxelisation.raytracing.voxel;

import java.io.Serializable;
import java.lang.reflect.Field;
import javax.vecmath.Point3d;
import org.apache.log4j.Logger;

/**
 *
 * @author Julien
 */
public class Voxel implements Serializable {

        /**
         * indice du voxel, position en x
         */
        public int i;

        /**
         * indice du voxel, position en y
         */
        public int j;

        /**
         * indice du voxel, position en z
         */
        public int k;
        
        /**
         * Nombre de fois où un rayon à échantillonné le voxel
         */
        public int nbSampling = 0;
        
        /**
         * Nombre de fois où un rayon à échantillonné le voxel
         */
        public int nbOutgoing = 0;

        /**
         * Nombre d'échos dans le voxel
         */
        public int nbEchos = 0;

        /**
         * Longueurs cumulées des trajets optiques dans le voxel dans le cas 
         * où il n'y a pas eu d'interceptions dans le voxel
         */
        public double Lg_NoInterception = 0;

        /**
         * Longueurs cumulées des trajets optiques dans le voxel
         * Lorqu'il y a interception dans le voxel vaut: distance du point d'entrée au point d'interception
         * Lorsqu'il y n'y a pas d'interception dans le voxel vaut: distance du point d'entré au point de sortie
         */
        public double Lg_Exiting = 0;

        /**
         * PAD beam fraction, calcul du PAD selon la formule:
         * transmittance = (bfEntering - bfIntercepted) / vox.bfEntering;
         * PAD = log(transmittance) / (-0.5 * lMean2);
         * 
         */
        public double PadBF = 0;

        /**
         * PAD beam section, calcul du PAD selon la formule:
         * transmittance = (bsEntering - bsIntercepted) / vox.bsEntering;
         * PAD = log(transmittance) / (-0.5 * lMean2);
         * 
         */
        public double PadBS = 0;

        /**
         * Beam fraction Entering, fraction de faisceau entrante
         * Nombre de fois où un rayon a échantillonné le voxel,
         * contrairement à nbSampling, peut etre pondéré
         */
        public double bfEntering = 0;

        /**
         * Beam fraction Intercepted, fraction de faisceau interceptée
         * Nombre d'échos dans le voxel, contrairement à nbEchos peut être pondéré
         */
        public double bfIntercepted = 0;

        /**
         * Beam Section Entering, section de faisceau entrant,
         * est calculé par rapport à la surface selon la formule:
         * tan(laserBeamDivergence / 2) * distance * Math.PI;
         * 
         */
        public float bsEntering = 0;

        /**
         * Beam Section Intercepted, section de faisceau intercepté 
         */
        public float bsIntercepted = 0;

        /**
         * Distance du voxel par rapport au sol
         */
        public float ground_distance = 10;

        /**
         *  Position du voxel dans l'espace
         *  Note: un attribut de voxel commençant par underscore _ 
         * signifie que l'attribut ne doit pas être exporté
         */
        public Point3d _position;

        /**
         * Longueur moyenne du trajet optique dans le voxel
         * En ALS est égal à: pathLength / (nbSampling)
         * En TLS est égal à: lgTraversant / (nbSampling - nbEchos)
         */
        public double LMean_Exiting = 0;
        public double LMean_NoInterception = 0;
        
        public double angleMean = 0;
        
        private static final Field[] _fields = Voxel.getFields();
        private final static Logger _logger = Logger.getLogger(Voxel.class);
        

        /**
         *
         * @param i
         * @param j
         * @param k
         */
        public Voxel(int i, int j, int k) {

            this.i = i;
            this.j = j;
            this.k = k;
        }
        
        public void setPosition(Point3d position){
            this._position = new Point3d(position);
        }
        
        public void setDist(float dist){
            this.ground_distance = dist;
        }
        
        public static String getHeader(){
            
            String header = "";
            
            for (Field field : _fields) {
                String fieldName = field.getName();
                if(!fieldName.startsWith("_")){
                    header += fieldName+" ";
                }
            }
            
            header = header.trim();
            
            return header;
        }
        
        private static Field[] getFields(){
                   
            Field[] fields = Voxel.class.getFields();
            
            for (Field field : fields) {
                field.setAccessible(true);
            }
            
            return fields;
        }

        @Override
        public String toString() {

            String voxelString = "";

            // compare values now
            for (Field _field : _fields) {
                
                String fieldName = _field.getName();
                
                if (!fieldName.startsWith("_")) {
                    
                    try {
                        Object newObj = _field.get(this);
                        voxelString += newObj + " ";
                    }catch (IllegalArgumentException | IllegalAccessException ex) {
                        _logger.error(ex);
                    }
                }
            }
            
            voxelString = voxelString.trim();
            return voxelString;
            /*
            return sb.append(i).append(" ").
                    append(j).append(" ").
                    append(k).append(" ").
                    append(bfEntering).append(" ").
                    append(bfIntercepted).append(" ").
                    append(bsEntering).append(" ").
                    append(bsIntercepted).append(" ").
                    append(Lg_Exiting).append(" ").
                    append(Lg_NoInterception).append(" ").
                    append(PadBF).append(" ").
                    append(PadBS).append(" ").
                    append(ground_distance).append(" ").
                    append(nbSampling).append(" ").
                    append(nbEchos).append(" ").
                    append(nbOutgoing).append(" ").
                    append(LMean_Exiting).append(" ").
                    append(angleMean).append(" ").toString();
                    */
        }
    }