package fr.amap.lidar.amapvox.jeeb.workspace.sunrapp.geometry;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;

/**
 * Construct a 4x4 transformation matrix combining successive transformations
 * (rotations, translations and ISOTROPIC scaling).
 * <li>The "apply" method applies the transformation to the tuple in argument.
 * @author Dauzat  -August 2012
 */
public class Transformations {

	Matrix4f mat;
	/**
	 *  Single precision floating transforms
	 */
	public Transformations () {
		mat = new Matrix4f ();
		mat.setIdentity ();
	}
	
	public Transformations (Vector3f translation) {
		mat = new Matrix4f ();
		mat.setIdentity ();
		setTranslation(translation);
	}
	
		/**
	 * Set a counter clockwise rotation of tuple around the X axis
	 */
	public void setRotationAroundX (float angle) {
		Matrix4f matRot = new Matrix4f ();
		matRot.rotX (angle);
		mat.mul (matRot, mat);
	}
	/**
	 * Set a counter clockwise rotation of tuple around the Y axis
	 */
	public void setRotationAroundY (float angle) {
		Matrix4f matRot = new Matrix4f ();
		matRot.rotY (angle);
		mat.mul (matRot, mat);
	}
	/**
	 * Set a counter clockwise rotation of tuple around the Z axis
	 */
	public void setRotationAroundZ (float angle) {
		Matrix4f matRot = new Matrix4f ();
		matRot.rotZ (angle);
		mat.mul (matRot, mat);
	}
	/**
	 * Set a counter clockwise rotation of tuple around the specified axis
	 */
	public void setRotationAroundAxis (Vector3f rotAxis, float angle) {
		AxisAngle4f axisAngle = new AxisAngle4f (rotAxis, angle);
		Matrix4f matRot = new Matrix4f ();
		matRot.set (axisAngle);
		mat.mul (matRot, mat);
	}
	
	/**
	 * Set a translation
	 */
	public void setTranslation (Vector3f translation) {
		Matrix4f matScale = new Matrix4f ();
		matScale.setIdentity ();
		matScale.setTranslation (translation);
		mat.mul (matScale, mat);
	}
	
	/**
	 * Set a scaling factor
	 */
	public void setScale (float scale) {
		Matrix4f matScale = new Matrix4f ();
		matScale.setIdentity ();
		matScale.setScale (scale);
		mat.mul (matScale, mat);
	}

	/**
	 * Set transformation matrix
	 */
	public void setMatrix (Matrix4f matrix) {
		mat = matrix;
	}

	/**
	 * Applies the transformations to the tuple
	 * @param tuple (Point3f or Vector3f) to transform
	 */
	public void apply (Tuple3f tuple) {
		Tuple4f t = new Point4f (tuple.x, tuple.y, tuple.z, 1);
		mat.transform (t);
		tuple.x = t.x;
		tuple.y = t.y;
		tuple.z = t.z;

	}

	
	public Matrix4f getMatrix () {
		return mat;
	}

	//==================== statics methods ====================//
	/**
	 * Applies a counter clockwise rotation of tuple around the X axis
	 */
	public static void rotateAroundX (Tuple3f tuple, float angle) {
		Matrix3f mat = new Matrix3f ();
		mat.rotX (angle);
		mat.transform (tuple);
	}
	/**
	 * Applies a counter clockwise rotation of tuple around the Y axis.
	 */
	public static void rotateAroundY (Tuple3f tuple, float angle) {
		Matrix3f mat = new Matrix3f ();
		mat.rotY (angle);
		mat.transform (tuple);
	}
	/**
	 * Applies a counter clockwise rotation of tuple around the Z axis
	 */
	public static void rotateAroundZ (Tuple3f tuple, float angle) {
		Matrix3f mat = new Matrix3f ();
		mat.rotZ (angle);
		mat.transform (tuple);
	}
	/**
	 * Applies a counter clockwise rotation of tuple around the specified axis
	 */
	public static void rotateAroundAxis (Tuple3f tuple, Vector3f axis, float angle) {
		Matrix3f mat = new Matrix3f ();
		AxisAngle4f axisAngle = new AxisAngle4f (axis, angle);
		mat.set (axisAngle);
		mat.transform (tuple);
	}
	/**
	 * Applies a translation on the tuple
	 */
	public static void translate (Tuple3f tuple, Tuple3f translation) {
		tuple.x += translation.x;
		tuple.y += translation.y;
		tuple.z += translation.z;
	}
	/**
	 * Applies a scalar multiplication to tuple coordinates
	 */
	public static void scale (Tuple3f tuple, float scale) {
		tuple.scale (scale);
	}

}