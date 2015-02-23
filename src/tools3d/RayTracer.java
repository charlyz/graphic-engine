package tools3d;

import java.util.Scanner;

import light.Light;
import light.PointLight;
import material.DiffuseMaterial;
import material.Material;
import material.PhongMaterial;
import geometry.Geometry;
import geometry.Plane;
import geometry.Triangle;
import representation.BarycentricCoords;
import representation.ClosestTriangle;
import representation.Color3f;
import representation.Matrix4f;
import representation.Point3f;
import representation.Ray;
import representation.Scene;
import representation.SceneGraph;
import representation.TexCoord2f;
import representation.Texture;

import representation.Vector3f;
import transformation.Shape;
import transformation.Transformation;

/**
 * This class draws in a window
 * the content of a scene by ray tracing.
 * @author Dessart Charles-Eric
 *
 */
public class RayTracer 
{
	// the scene to draw
	public Scene scene;
	// the window where the scene is drawn
	public CgPanel cgPanel;
	// l b t r t points
	public float l, r, t, b, n;
	//
	public static float epsylon = 0.001f;
	
	
	/**
	 * Constructor
	 * @param scene the scene to draw
	 * @param cgPanel the window where the scene is drawn
	 * @param cr diffuse reflectance
	 */
	public RayTracer(Scene scene, CgPanel cgPanel) 
	{
		this.scene = scene;
		this.cgPanel = cgPanel;
		computeLRBTNPoints();
	}
	
	/**
	 * Computes l r b t n points
	 */
	public void computeLRBTNPoints()
	{
		// formulas p173 2nd
		// we look through the center of the window
		// so: l = -r and b = -t	
		r = -scene.Nx.floatValue()/2; // figure 7.14 p174 2nd
		t = scene.Ny.floatValue()/2;
		l = -r;
		b = -t;
		n = new Float(Math.abs(t/Math.tan(scene.activeCamera.fovy/2)));
	}
	
	public Color3f raycolor(Ray ray, float t0, float t1, int depth)
	{
		ClosestTriangle ct = new ClosestTriangle();
		//closestTriangle(ray, ct);
		
		if (scene.rootBSPTree.hit(ray, t0, Float.MAX_VALUE, ct)) 
		//if(t1<Float.MAX_VALUE)
		{
			Integer closestTriangle = ct.indexTriangle; 
			Shape closestShape = ct.closestShape;
			t1 = ct.t;
			
			// compute s
			Point3f s = new Point3f(ray.base.x + t1 * ray.direction.x, 
									ray.base.y + t1 * ray.direction.y,
									ray.base.z + t1 * ray.direction.z);

			// compute normal vector of s
			Vector3f normalVector = getNormalVector(closestShape, closestTriangle, s);
			// compute color of the pixel
			Color3f color = computeColor(s, closestShape, closestTriangle, normalVector, ct.bc);
			
			Color3f reflection = closestShape.reflection;
			if(depth<=0 /*|| (reflection.x==0 && reflection.y==0 && reflection.z==0)*/)	
				return color;
			else
			{
				Color3f raycolorRefl = null;
				Color3f raycolorRefra = null;
				
				if(!(closestShape.reflection.x==0 && closestShape.reflection.y==0 && closestShape.reflection.z==0))
				{
					// compute r, formula p212 2nd
					Vector3f r = reflect(ray.direction, normalVector);
					Ray reflectRay = new Ray(s, r);
					raycolorRefl = raycolor(reflectRay, epsylon, Float.MAX_VALUE, depth-1);
					if(raycolorRefl!=null)
					{
						color = new Color3f(Vector3f.computeAddition(new Vector3f(color), Vector3f.computeMultiply(new Vector3f(reflection), new Vector3f(raycolorRefl))));
					}
				}
				
				
				Vector3f t = new Vector3f();
				if(closestShape.refraction!=0 && refract(new Vector3f(ray.direction), normalVector, 1f, closestShape.refraction, t))
				{
					Ray refraRay = new Ray(s, t);
					//System.out.println("refraRAy: " + refraRay);
					raycolorRefra = raycolor(refraRay, epsylon, Float.MAX_VALUE, depth-1);
					
					/*System.out.println("v refra: " + refraRay);
					System.out.println("v vrai: " + new Ray(s, ray.direction));
					System.out.println("Comparaison refra: " + raycolorRefra);
					System.out.println("Comparaison vrai: " + raycolor(new Ray(s, ray.direction), epsylon, Float.MAX_VALUE, depth-1));
					*/
					
					/*Color3f vrai = raycolor(new Ray(s, ray.direction), epsylon, Float.MAX_VALUE, depth-1);
					if(vrai!=null && vrai.z!=raycolorRefra.z)
					{
						System.out.println("vrai: " + vrai);
						System.out.println("refra: " + raycolorRefra);
						raycolorRefra = vrai;
					}*/
						
					
					if(raycolorRefra!=null)
					{
						/*System.out.println("D: " + ray.direction);
						System.out.println("t: " + t);
						System.out.println("Color: " + color);*/
					//System.out.println("Refra: " + raycolorRefra);
					
						color = new Color3f(Vector3f.computeAddition(new Vector3f(color), Vector3f.computeMultiply(new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(raycolorRefra))));
						/*System.out.println("Result: " + color);
						System.out.println();*/
					}
				}
				
				color.x = Math.min(1, color.x);
				color.y = Math.min(1, color.y);
				color.z = Math.min(1, color.z);
				return color;
			}
		}else
			return null;
	}
	
	public boolean refract(Vector3f d, Vector3f n, float indice1, float indice2, Vector3f t)
	{
		indice1 = 1.53f;
		indice2 = 1f;
		//d.normalize();
		//n.normalize();
		Vector3f left = Vector3f.computeScalarProduct(
							Vector3f.computeScalarProduct(
									Vector3f.computeMinus(d, Vector3f.computeScalarProduct(n, Vector3f.computeDot(d, n))), indice1),
							1/indice2);
		//d.normalize();
		double sqrt = 1-((Math.pow(indice1, 2)*(1-Math.pow(Vector3f.computeDot(d, n), 2)))/Math.pow(indice2, 2));
		
		if(sqrt<0) return false;
		
		Vector3f right = Vector3f.computeScalarProduct(
									n, 
									(float)Math.sqrt(sqrt));
		/*System.out.println(indice1 + " - " + indice2);
		System.out.println("left: " + left);
		System.out.println("right: " + right);*/
		Vector3f res = Vector3f.computeMinus(left, right);
		
		t.x = res.x;
		t.y = res.y;
		t.z = res.z;
		/*System.out.println("D: " + d);
		System.out.println("t: " + t);
		System.out.println();*/
		return true;
	}
	
	public Vector3f reflect(Vector3f d, Vector3f n)
	{
		return Vector3f.computeMinus(d, 
				Vector3f.computeScalarProduct(
						n, 2*Vector3f.computeDot(d, n)));
	}
	
	/**
	 * Traces all transformations in the window
	 * @param shape
	 */
	public void trace()
	{
		// algorithm p210 2nd
		// for each pixel of the window
		int n;
		if(scene.antialiasing)
			n = 4;
		else
			n = 1;

		for (int i = 0; i < scene.Nx; i++) 
		{
			for (int j = 0; j < scene.Ny; j++) 
			{	
				Color3f c = new Color3f();
				boolean drawPixel = false;
				for(int p=0; p<n;p++)
				{
					for(int q=0; q<n;q++)
					{
						// compute viewing ray
						float random = scene.antialiasing ? (float)Math.random():0.5f;
						Ray ray = computeViewingRay(i+(p+random)/n, j+(q+random)/n);
						Color3f raycolor = raycolor(ray, 0, Float.MAX_VALUE, 2);
						if(raycolor!=null)
						{
							drawPixel = true;
							c.x += raycolor.x;
							c.y += raycolor.y;
							c.z += raycolor.z;
						}else
						{
							c.x += scene.background.x;
							c.y += scene.background.y;
							c.z += scene.background.z;
						}
					}
					c.x /= Math.pow(n, 2);
					c.y /= Math.pow(n, 2);
					c.z /= Math.pow(n, 2);
				}
				
				
				
				if(drawPixel)
					cgPanel.drawPixel(i, j, c.x, c.y, c.z);
				else
					cgPanel.drawPixel(i, j, scene.background.x, scene.background.y, scene.background.z);
			}
		}
	}
	
	public void closestTriangle(Ray ray, ClosestTriangle ct)
	{
		for(Shape shape : scene.shapes)
		{
			int indexTriangle = 0;
			Geometry geometry = shape.geometry;
				
			for(Triangle triangle : geometry.triangles)
			{
				Float res;
				// transform triangle
				Triangle transformedTriangle = triangle;
					
				// if the ray intersects a triangle ..
				if ((res = Intersection.RayTriangleIntersection(ray, transformedTriangle, 0, ct.t))!=null) 
					if (ct.t > res) 
					{
						ct.t = res;
						ct.indexTriangle = indexTriangle;
						ct.closestShape = shape;
					}
				indexTriangle++;
			}
		}
	}
	
	public void closestTriangleWithSceneGraph(SceneGraph sg, Float t0, Ray ray, ClosestTriangle ct)
	{
		int index = 0;
		for(SceneGraph sgChild : sg.children)
		{	
			Transformation transf = sgChild.value;
			if(transf instanceof Shape)
			{
				Shape shape = (Shape)transf;
				int indexTriangle = 0;
				Geometry geometry = shape.geometry;
				
				for(Triangle triangle : geometry.triangles)
				{
					Float res;
					// transform triangle
					Triangle transformedTriangle = triangle;
					
					
					// if the ray intersects a triangle ..
					if ((res = Intersection.RayTriangleIntersection(ray, transformedTriangle, t0, ct.t))!=null) 
						if (ct.t > res) 
						{
							ct.t = res;
							ct.indexTriangle = indexTriangle;
							ct.closestShape = shape;
							ct.Mt = sg.Mi;
						}
					indexTriangle++;
				}
			}else
			{
				//sgChild.setMi(sg.Mi, transf.Mt());
				closestTriangleWithSceneGraph(sgChild, t0, ray, ct);
			}
			index++;
		}
	}
	
	public boolean isShadow(Point3f s, Light l)
	{
		if(l instanceof PointLight)
		{
			PointLight pl = (PointLight)l;
			Ray r = new Ray(s, pl.position);
			return isShapeBetweenTwoPoints(r);
		}else 
			return false;
	}
	
	public boolean isShapeBetweenTwoPointsOld(Ray ray)
	{
		for(Shape shape : scene.shapes)
		{
			Geometry geometry = shape.geometry;
				
			for(Triangle triangle : geometry.triangles)
			{
					// if the ray intersects a triangle ..
					if (Intersection.RayTriangleIntersection(ray, triangle, epsylon, Float.MAX_VALUE)<Float.MAX_VALUE) 
						return true;
			}
		}
		return false;
	}
	
	public boolean isShapeBetweenTwoPoints(Ray ray)
	{
		return scene.rootBSPTree.hit(ray, 0.05f, Float.MAX_VALUE, new ClosestTriangle());
	}
	
	/**
	 * Compute the normal vector of a 3D point
	 * @param shape
	 * @param index index of the triangle where "s" is contained
	 * @param s the 3D point
	 * @return
	 */
	public Vector3f getNormalVector(Shape shape, int index, Point3f s)
	{
		Triangle triangle = shape.geometry.triangles.get(index);
		Point3f a = triangle.a;
		Point3f b = triangle.b;
		Point3f c = triangle.c;
		Integer[][] indexFace = shape.geometry.faces.get(index);

		// if normal vectors of each vertex are not defined
		if(indexFace[0][2] == -1 || indexFace[1][2] == -1 || indexFace[2][2] == -1 /*|| true*/)//<-- always true
		{
			return shape.geometry.faceNormals.get(index);
		}
		
		// Interpolating Normals For Ray-Tracing
		// http://www.flipcode.com/archives/Interpolating_Normals_For_Ray-Tracing.shtml
		
		// select the farthest point of the triangle from s
		double d0 = Point3f.distance(a, s);
		double d1 = Point3f.distance(b, s);
		double d2 = Point3f.distance(c, s);
		
		Vector3f aN, bN, cN;
		
		// re-orient point such as "a" is the farthest
		if (d0 > d1 && d0 > d2)
		{
			aN = shape.geometry.normals.get(shape.geometry.faces.get(index)[0][2]);
	        bN = shape.geometry.normals.get(shape.geometry.faces.get(index)[1][2]);
	        cN = shape.geometry.normals.get(shape.geometry.faces.get(index)[2][2]);
		}
		else if (d1 > d0 && d1 > d2)
	    {
	        a = triangle.b;
	        b = triangle.a;
	        c = triangle.c;
	        aN = shape.geometry.normals.get(shape.geometry.faces.get(index)[1][2]);
	        bN = shape.geometry.normals.get(shape.geometry.faces.get(index)[0][2]);
	        cN = shape.geometry.normals.get(shape.geometry.faces.get(index)[2][2]);
	    }
	    else //if (d2 > d0 && d2 > d1)
	    {
	        a = triangle.c;
	        b = triangle.a;
	        c = triangle.b;
	        aN = shape.geometry.normals.get(shape.geometry.faces.get(index)[2][2]);
	        bN = shape.geometry.normals.get(shape.geometry.faces.get(index)[0][2]);
	        cN = shape.geometry.normals.get(shape.geometry.faces.get(index)[1][2]);
	    }
	    
		// compute normal of the triangle
		Vector3f polygonNormal = shape.geometry.faceNormals.get(index);
		
		// compute the plane b->c where its normal is the orthogonal of polygonnormal and b->c
	    Plane plane = new Plane(Vector3f.computeCrossProduct(Vector3f.computeMinus(new Vector3f(c), new Vector3f(b)), polygonNormal), c);  
	    // compute the ray a->s
	    Ray ray = new Ray(a, Vector3f.computeMinus(new Vector3f(s), new Vector3f(a)));
	    // compute the intersection between plane and ray
	    Float t = Intersection.RayPlaneIntersection(ray, plane);
	    Point3f q = new Point3f(Vector3f.computeAddition(new Vector3f(ray.base), Vector3f.computeScalarProduct(ray.direction, t)));
	    
	    // interpolation 
	    double distanceBQ = Point3f.distance(b, q);
	    double distanceBC = Point3f.distance(b, c);
	    Vector3f qN1 = Vector3f.computeMinus(cN, bN);
	    Vector3f qN2 = Vector3f.computeScalarProduct(qN1, (float)(distanceBQ/distanceBC));
	    Vector3f qN = Vector3f.computeAddition(bN, qN2);
	    
	    double distanceQS = Point3f.distance(q, s);
	    double distanceQA = Point3f.distance(q, a);
	    
	    Vector3f sN1 = Vector3f.computeMinus(aN, qN);
	    Vector3f sN2 = Vector3f.computeScalarProduct(sN1, (float)(distanceQS/distanceQA));
	    Vector3f sN = Vector3f.computeAddition(qN, sN2);

		return sN;
	}

	/**
	 * Computes the color of a 3D point s which is
	 * also a point of the triangle shape.triangles(index).
	 * @param s point of the triangle 
	 * @param shape 
	 * @param indexTriangle index of the triangle containing s
	 * @param n normal vector of the point s
	 * @return
	 */
	public Color3f computeColor(Point3f s, Shape shape, Integer indexTriangle, Vector3f n, BarycentricCoords bc)
	{
		Material mat = shape.material;
		if(mat!=null)
		{
			if(mat instanceof DiffuseMaterial)
			{
				return computeDiffuseMaterialColor(s, shape, n, mat.color);
			}else if(mat instanceof PhongMaterial)
			{
				return computePhongMaterialColor(s, shape, n);
			}
		}
		
		Texture tex = shape.texture;
		if(tex!=null)
		{
			return computeTextureColor(s, shape, indexTriangle, n, bc);
		}
		
		return null;
	}
	
	public Color3f computeTextureColor(Point3f s, Shape shape, Integer indexTriangle, Vector3f n, BarycentricCoords bc)
	{
		//algorithm p249 2nd
		Texture tex = shape.texture;
		Integer[][] face = shape.geometry.faces.get(indexTriangle);
		TexCoord2f a = shape.geometry.textures.get(face[0][1]);
		TexCoord2f b = shape.geometry.textures.get(face[1][1]);
		TexCoord2f c = shape.geometry.textures.get(face[2][1]);
		/*System.out.println("a: " + a);
		System.out.println("b: " + b);
		System.out.println("c: " + c);*/
		float u = (a.x+bc.beta*(b.x-a.x)+bc.gamma*(c.x-a.x));
		float v = (a.y+bc.beta*(b.y-a.y)+bc.gamma*(c.y-a.y));
		if(u<0) u = 0;
		if(v<0) v = 0;
		//System.out.println("u: " + u + " - v: " + v);
		if(shape.geometry.maxU==0) shape.geometry.maxU = 1;
		if(shape.geometry.maxV==0) shape.geometry.maxV = 1;
		float difU = (tex.getWidth()/shape.geometry.maxU);
		float difV = (tex.getHeight()/shape.geometry.maxV);
		
		
		float[] rgb = tex.getRGB((int)(u*difU), (int)(v*difV));
		//System.out.println(rgb[0] + " - " + rgb[1] + " - " + rgb[2]);
		return computeDiffuseMaterialColor(s, shape, n, new Color3f(rgb[0], rgb[1], rgb[2]));
	}
	
	/**
	 * Compute the color of a 3D point s which is
	 * also a point of the triangle shape.triangles(index).
	 * Color is computed according to a diffuse material.
	 * @param s point of the triange
	 * @param shape
	 * @param n normal vector of the point s
	 * @return
	 */
	public Color3f computeDiffuseMaterialColor(Point3f s, Shape shape, Vector3f n, Color3f cr)
	{
		// algorithm p192 2nd
		Color3f ca = scene.ambientColor;
		Color3f colorLight = new Color3f();

		for(Light light : scene.activeLights.values())
		{
			PointLight pl = (PointLight)light;
			Vector3f l = new Vector3f(pl.position.x - s.x,
									  pl.position.y - s.y,
									  pl.position.z - s.z);
			l.normalize();
			
			Color3f cl = pl.color;
			float LxN = Vector3f.computeDot(n, l);

			if(!isShadow(s, light))
			{
				colorLight.x += cr.x * (ca.x + cl.x * Math.max(0, LxN/* * (1/(l.length()*n.length()))*/));
				colorLight.y += cr.y * (ca.y + cl.y * Math.max(0, LxN/* * (1/(l.length()*n.length()))*/));
				colorLight.z += cr.z * (ca.z + cl.z * Math.max(0, LxN/* * (1/(l.length()*n.length()))*/));
			}
			else
			{
				//compute shadow
				Color3f colorShadow = new Color3f(scene.ambientColor);
				colorShadow.x *= cr.x;
				colorShadow.y *= cr.y;
				colorShadow.z *= cr.z;
				colorLight.x += colorShadow.x;
				colorLight.y += colorShadow.y;
				colorLight.z += colorShadow.z;
			}
				
		}
		// maximum 1 sinon il y a des couleurs bizarres
		colorLight.x = Math.min(1, colorLight.x);
		colorLight.y = Math.min(1, colorLight.y);
		colorLight.z = Math.min(1, colorLight.z);
		
		return colorLight;
	}
	
	/**
	 * Compute the color of a 3D point s which is
	 * also a point of the triangle shape.triangles(index).
	 * Color is computed according to a phong material.
	 * @param s point of the triange
	 * @param shape
	 * @param n normal vector of the point s
	 * @return
	 */
	public Color3f computePhongMaterialColor(Point3f s, Shape shape, Vector3f n)
	{
		// algorithm p196 2nd
		PhongMaterial pMat = (PhongMaterial)shape.material;
		Color3f cr = new Color3f(pMat.color);
		Color3f ca = scene.ambientColor;
		float p = pMat.shininess;
		Color3f colorLight = new Color3f();
		
		// vector s -> camera
		Vector3f e = new Vector3f(scene.activeCamera.position.x - s.x,
								  scene.activeCamera.position.y - s.y,
								  scene.activeCamera.position.z - s.z);
		e.normalize();

		for(Light light : scene.activeLights.values())
		{
			PointLight pl = (PointLight)light;
			// vector s -> point light
			Vector3f l = new Vector3f(pl.position.x - s.x,
									  pl.position.y - s.y,
									  pl.position.z - s.z);
			l.normalize();
			
			Vector3f h = Vector3f.computeAddition(e, l);
			h.normalize();
			
			Color3f cl = pl.color;
			float LxN = Vector3f.computeDot(n, l);
			float HxN = Vector3f.computeDot(h, n);	

			if(!isShadow(s, light))
			{	
				// cp = highlight color
				// cp = cr here
				colorLight.x += cr.x * (ca.x + cl.x * Math.max(0, LxN /** (1/(l.length()*n.length()))*/)) + cl.x * cr.x * Math.pow(HxN, p);
				colorLight.y += cr.y * (ca.y + cl.y * Math.max(0, LxN /** (1/(l.length()*n.length()))*/)) + cl.y * cr.y * Math.pow(HxN, p);
				colorLight.z += cr.z * (ca.z + cl.z * Math.max(0, LxN /** (1/(l.length()*n.length()))*/)) + cl.z * cr.z * Math.pow(HxN, p);
			}
			else
			{
				//compute shadow 
				Color3f colorShadow = new Color3f(scene.ambientColor);
				colorShadow.x *= cr.x;
				colorShadow.y *= cr.y;
				colorShadow.z *= cr.z;
				colorLight.x += colorShadow.x;
				colorLight.y += colorShadow.y;
				colorLight.z += colorShadow.z;
			}
		}
		// maximum 1 sinon il y a des couleurs bizarres
		colorLight.x = Math.min(1, colorLight.x);
		colorLight.y = Math.min(1, colorLight.y);
		colorLight.z = Math.min(1, colorLight.z);

		return colorLight;
	}
	
	
	
	/**
	 * Computes the viewing ray from the camera to 
	 * the pixel i j
	 * @param i
	 * @param j
	 * @return
	 */
	public Ray computeViewingRay(float i, float j) 
	{
		// formulas p204 2nd
		float ws = n;
		float us = l + (r - l)*((i/* + 0.5F*/)/scene.Nx);
		float vs = b + (t - b)*((j /*+ 0.5F*/)/scene.Ny);
		
		Point3f e = scene.activeCamera.position;
		Vector3f uxU = Vector3f.computeScalarProduct(scene.u, us);
		Vector3f vxV = Vector3f.computeScalarProduct(scene.v, vs);
		Vector3f wxW = Vector3f.computeScalarProduct(scene.w, ws);
		Vector3f sum = Vector3f.computeAddition(uxU, vxV, wxW);

		Point3f s = new Point3f(-e.x - sum.x,
								-e.y - sum.y, 
								-e.z - sum.z);
		
		return new Ray(e, s);
	}
	
}
