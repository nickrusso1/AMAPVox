/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.amap.commons.math.vector;

import fr.amap.commons.math.point.Point3D;

/**
 *
 * @author Julien Heurtebize (julienhtbe@gmail.com)
 */
public class Vec3D extends Point3D{
    
    /**
     * Constructs and initialize a new 3d double precision vector filled with zeros
     */
    public Vec3D(){
        
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }
    
    public Vec3D(double x, double y, double z){
        
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * Create a new double precision 3d vector from two 3d points
     * @param point1 The first point
     * @param point2 The second point
     * @return A 3d vector constructed with the two given points
     */
    public static Vec3D createVec3DFromPoints(Point3D point1, Point3D point2){
        
        Vec3D result = new Vec3D();
        
        result.x = point2.x - point1.x;
        result.y = point2.y - point1.y;
        result.z = point2.z - point1.z;
        
        return result;
    }
    
    /**
     * Get the cross product of two 3d vectors
     * @param vec First vector
     * @param vec2 Second vector
     * @return The cross product as a 3d double precision vector
     */
    public static Vec3D cross(Vec3D vec, Vec3D vec2){
        
        Vec3D dest = new Vec3D();
        
        double x = vec.x, y = vec.y, z = vec.z;
        double x2 = vec2.x, y2 = vec2.y, z2 = vec2.z;
        
        dest.x = y*z2 - z*y2;
        dest.y = z*x2 - x*z2;
        dest.z = x*y2 - y*x2;
        
        return dest;
    }
    
    /**
     * Normalize a 3d vector
     * @param vec The vector to normalize
     * @return The normalized vector
     */
    public static Vec3D normalize(Vec3D vec){
        
        Vec3D dest = new Vec3D();
        
        double x = vec.x, y = vec.y, z = vec.z;
        double len = Math.sqrt(x*x + y*y + z*z);
        
        if (len == 0) {
                dest.x = 0;
                dest.y = 0;
                dest.z = 0;
                return dest;
        } else if (len == 1) {
                dest.x = x;
                dest.y = y;
                dest.z = z;
                return dest;
        }
        
        len = 1 / len;
        dest.x = (double) (x*len);
        dest.y = (double) (y*len);
        dest.z = (double) (z*len);
        
        return dest;
    }
    
    public static double angle(Vec3D vec1, Vec3D vec2){
        
        double angle = Math.acos(Vec3D.dot(vec1, vec2)/((Vec3D.length(vec1) * Vec3D.length(vec2))));
        return angle;
    }
    
    
    
    /**
     * Add a 3d vector to another
     * @param vec1 The first vector
     * @param vec2 The second vector
     * @return The vector addition as a 3d double precision vector
     */
    public static Vec3D add(Vec3D vec1, Vec3D vec2){
        
        Vec3D result = new Vec3D();
        
        result.x = vec1.x + vec2.x;
        result.y = vec1.y + vec2.y;
        result.z = vec1.z + vec2.z;
        
        return result;
    }
    
    /**
     * Substract a 3d vector from another
     * @param vec1 The original vector
     * @param vec2 The second vector
     * @return The original 3d vector, substracted by the other
     */
    public static Vec3D substract(Vec3D vec1, Vec3D vec2){
        
        Vec3D result = new Vec3D();
        
        result.x = vec1.x - vec2.x;
        result.y = vec1.y - vec2.y;
        result.z = vec1.z - vec2.z;
        
        return result;
    }
    
    /**
     * Multiply a 3d vector by another
     * @param vec1 The first vector
     * @param vec2 The second vector
     * @return A 3d vector, result of the product of the first vector by the second one
     */
    public static Vec3D multiply(Vec3D vec1, Vec3D vec2){
        
        Vec3D result = new Vec3D();
        
        result.x = vec1.x * vec2.x;
        result.y = vec1.y * vec2.y;
        result.z = vec1.z * vec2.z;
        
        return result;
    }
    
    /**
     * Multiply a 3d vector by a double precision number
     * @param vec The 3d vector
     * @param multiplier The factor
     * @return A 3d vector, result of the product of the vector by the factor
     */
    public static Vec3D multiply(Vec3D vec, double multiplier){
        
        Vec3D result = Vec3D.multiply(vec, new Vec3D(multiplier, multiplier, multiplier));
        
        return result;
    }
    
    /**
     * Get the length of a double precision 3d vector
     * @param vec The vector
     * @return The length of the vector, as a double precision number
     */
    public static double length(Vec3D vec){
        
        double result = (double) Math.sqrt((vec.x * vec.x)+(vec.y * vec.y)+(vec.z * vec.z));
        
        return result;
    }
    
    /**
     * Get the dot product of two 3d vector
     * @param vec The first vector
     * @param vec2 The second vector
     * @return The result of the dot product as a double precision number
     */
    public static double dot(Vec3D vec, Vec3D vec2){
        
        double result = vec.x*vec2.x + vec.y*vec2.y + vec.z*vec2.z;
        
        return result;
    }
    
    /**
     * Convert the given vector to a 1 dimensional array with a size of 3
     * @param vec The 3d vector
     * @return a one dimensional double precision array with a length of 3
     */
    public static double[] toArray(Vec3D vec){
        
        double array[] = new double[3];
        
        array[0] = vec.x;
        array[1] = vec.y;
        array[2] = vec.z;
        
        return array;
    }

    
}
