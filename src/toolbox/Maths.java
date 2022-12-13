package toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Player;

public class Maths {
	
	public static float baryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		double det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		double l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		double l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		double l3 = 1.0f - l1 - l2;
		return (float) (l1 * p1.y + l2 * p2.y + l3 * p3.y);
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float)Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale,scale,scale), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale, float rotation) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		Matrix4f.rotate((float) (rotation), new Vector3f(0, 0, 1), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createViewMatrix(Player camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        Matrix4f.rotate((float) camera.getAngles().getY(), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
        Matrix4f.rotate((float) camera.getAngles().getX(), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
        return viewMatrix;
    }
	
	public static Vector3f calcAngle(Vector3f src, Vector3f dir) {
		Vector3f vAngle = new Vector3f(0, 0, 0);
		Vector3f delta = new Vector3f( (dir.x-src.x), (dir.y-src.y), (dir.z-src.z) );
		//double hyp = Math.sqrt( delta.x*delta.x + delta.y*delta.y );
		
		vAngle.x = (float)(Math.atan(delta.x/delta.z)* 180f/Math.PI);
		vAngle.y = (float)(Math.atan(delta.y/delta.x )* 180f/Math.PI);
		vAngle.z = 0.0f;
		
		if(delta.z <= 0.0)
			vAngle.x += 180.0f;
		
		return vAngle;
	}
	
	public static Vector3f vectorTo(Vector3f a, Vector3f b) {
		Vector3f vectorTo = new Vector3f(0, 0, 0);
		
		vectorTo.x = a.x - b.x;
		vectorTo.y = a.y - b.y;
		vectorTo.z = a.z - b.z;
		
		return vectorTo;
	}
	
	public static double distance(Vector3f a, Vector3f b) {
		return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2) + Math.pow(a.getZ() - b.getZ(), 2));
	}
	
	public static double distance2D(Vector3f a, Vector3f b) {
		return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getZ() - b.getZ(), 2));
	}
}
