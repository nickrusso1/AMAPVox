/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ird.voxelidar.util;

import fr.ird.voxelidar.math.vector.Vec3D;

/**
 *
 * @author Julien
 */
public class Als {
    
    //public double x;
    //public double y;
    //public double z;
    Vec3D location;
    public int i;
    public int r;
    public int n;
    public int c;
    public int a;
    public int p;
    public double t;
    public double T;

    public Als(Vec3D location, int i, int r, int n, int c, int a, int p, double t) {
        this.location = location;
        this.i = i;
        this.r = r;
        this.n = n;
        this.c = c;
        this.a = a;
        this.p = p;
        this.t = t;
        this.T = 0;
    }
    
}
