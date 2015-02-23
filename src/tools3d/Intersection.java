package tools3d;

import geometry.Plane;
import geometry.Triangle;
import representation.BarycentricCoords;
import representation.Ray;
import representation.Vector3f;


/**
 * This class contains few methods to 
 * find intersection point(s) between
 * different kind of objects.
 * 
 * @author Dessart Charles-Eric
 *
 */
public class Intersection 
{
	/**
	 * Returns t, the "point" on the ray when
	 * the ray intersects the triangle.
	 * @param ray
	 * @param triangle
	 * @param t0 the minimum distance
	 * @param t1 the maximum distance
	 * @return
	 */
	public static Float RayTriangleIntersection(Ray ray, Triangle triangle, float t0, float t1, BarycentricCoords bc)  
	{
		// algorithm p208 2nd.
		float a = triangle.getA().x - triangle.getB().x;
		float b = triangle.getA().y - triangle.getB().y;
		float c = triangle.getA().z - triangle.getB().z;
		float d = triangle.getA().x - triangle.getC().x;
		float e = triangle.getA().y - triangle.getC().y;
		float f = triangle.getA().z - triangle.getC().z;
		float g = ray.getDirection().x;
		float h = ray.getDirection().y;
		float i = ray.getDirection().z;
		float j = triangle.getA().x - ray.getBase().x;
		float k = triangle.getA().y - ray.getBase().y;
		float l = triangle.getA().z - ray.getBase().z;
		float m = a*(e*i-h*f) + b*(g*f-d*i) + c*(d*h-e*g);
		
		float t = ((f*(a*k-j*b)+e*(j*c-a*l)+d*(b*l-k*c))/m)*(-1);
		if (t < t0 || t > t1) return Float.MAX_VALUE/*null*/;

		float gamma  = (i*(a*k-j*b)+h*(j*c-a*l)+g*(b*l-k*c))/m; 
		if (gamma < 0 || gamma > 1) return Float.MAX_VALUE/*null*/;
		
		float beta = (j*(e*i-h*f) + k*(g*f-d*i) + l*(d*h-e*g)) / m;
		if (beta < 0 || beta > (1-gamma)) return Float.MAX_VALUE/*null*/;
		
		if(bc!=null)
		{
			bc.beta = beta;
			bc.gamma = gamma;
		}
		
		return t;
	}
	
	public static Float RayTriangleIntersection(Ray ray, Triangle triangle, float t0, float t1)
	{
		return RayTriangleIntersection(ray, triangle, t0, t1, null);
	}
	
	/**
	 * Returns t, the "point" on the ray when
	 * the ray intersects the plane.
	 * @param ray
	 * @param plane
	 * @return
	 */
	public static Float RayPlaneIntersection(Ray ray, Plane plane)
	{
		// algorithm found on this page
		// http://www.cgafaq.info/wiki/Ray_Plane_Intersection
		Float a = -Vector3f.computeDot(plane.normal, Vector3f.computeMinus(new Vector3f(ray.base), new Vector3f(plane.p)));
		Float b = Vector3f.computeDot(new Vector3f(plane.normal), new Vector3f(ray.direction));
		if(b==0)
			return null;
		return a/b;
	}
}
