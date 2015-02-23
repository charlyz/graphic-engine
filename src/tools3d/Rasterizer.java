package tools3d;

import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import light.Light;
import light.PointLight;
import material.DiffuseMaterial;
import material.Material;

import representation.ClosestTriangle;
import representation.Color3f;
import representation.Matrix4f;
import representation.Point2f;
import representation.Point3f;
import representation.Ray;
import representation.Scene;
import representation.SceneGraph;
import geometry.Geometry;
import geometry.Triangle;
import representation.Vector3f;
import transformation.Shape;
import transformation.Transformation;

/**
 * This class draws in a window
 * the content of a scene by rasterization.
 * @author Dessart Charles-Eric
 *
 */
public class Rasterizer 
{
	// the scene to draw
	public Scene scene;
	// the window where the scene is drawn
	public CgPanel cgPanel;
	// viewing matrix
	public Matrix4f Mv;
	// perspective matrix
	public Matrix4f Mp;
	// projection matrix
	public Matrix4f Mo;
	// total matrix
	public Matrix4f M;
	// array containing the depth of the
	// triangle the nearest for a point x y.
	public float[][] zBuffer;
	// array containing the color of
	// pixel according to the zBuffer
	public Color3f[][] zBufferColor;
	// l b n r t f points
	HashMap<String, Float> LBNRTF;
	
	/**
	 * Constructor
	 * @param scene the scene to draw
	 * @param cgPanel the window where the scene'll draw
	 * @param cr value of the diffuse reflectance
	 */
	public Rasterizer(Scene scene, CgPanel cgPanel) 
	{
		this.scene = scene;
		this.cgPanel = cgPanel;
		computeLBNRTF();
		computeM();
		initZBuffer();
	}
	
	/**
	 * Initializes values of zBuffer to Float.max
	 */
	public void initZBuffer()
	{
		zBuffer = new float[scene.Nx][scene.Ny];
		zBufferColor = new Color3f[scene.Nx][scene.Ny];
		
		int i = 0;
		int j = 0;
		while(i<zBuffer.length)
		{
			float[] tmp = zBuffer[i];
			j = 0;
			while(j<tmp.length)
			{
				zBuffer[i][j] = Float.MAX_VALUE;
				j++;
			}
			i++;
		}
	}
	
	
	/**
	 * Computes perspective matrix
	 */
	public void computeMp()
	{
		// algorithm p170 2nd
		float n = LBNRTF.get("n");
		float f = LBNRTF.get("f");
		
		Mp = new Matrix4f(n, 0, 0, 0,
									0, n, 0, 0,
									0, 0, n+f, -f*n, 
									0, 0, 1, 0);

	}
	
	/**
	 * Compute projection matrix
	 */
	public void computeMo()
	{
		// algorithm p163 2nd
		float Nx = scene.Nx;
		float Ny = scene.Ny;
		
		float l = LBNRTF.get("l");
		float b = LBNRTF.get("b");
		float n = LBNRTF.get("n");
		float r = LBNRTF.get("r");
		float t = LBNRTF.get("t");
		float f = LBNRTF.get("f");
		
		// CVV -> 2D matrix
		Matrix4f m1 = new Matrix4f(Nx/2, 0,   0, (Nx-1)/2, 
								  0,    Ny/2, 0, (Ny-1)/2, 
								  0,    0,    1, 0,
								  0,    0,    0, 1);
		
		// scaling according to the "3d box"
		Matrix4f m2 = new Matrix4f(2/(r-l), 0,       0,       0, 
				  				   0,       2/(t-b), 0,       0, 
				  				   0,       0,       2/(n-f), 0,
				  				   0,       0,       0,       1);
		
		// translation to the origin
		Matrix4f m3 = new Matrix4f(1, 0, 0, -((l+r)/2), 
								   0, 1, 0, -((b+t)/2), 
								   0, 0, 1, -((n+f)/2),
								   0, 0, 0, 1);
		
		Matrix4f res = Matrix4f.computeProduct(m1, m2);
		Mo = Matrix4f.computeProduct(res, m3);
	}
	
	/**
	 * Computes viewing matrix
	 */
	public void computeMv()
	{
		// algorithm p165 2nd
		Vector3f u = scene.u;
		Vector3f v = scene.v;
		Vector3f w = scene.w;
		
		// rotate scene 
		Matrix4f m1 = new Matrix4f(u.x, u.y, u.z, 0, 
								   v.x, v.y, v.z, 0, 
								   w.x, w.y, w.z, 0,
								   0,   0,   0,   1);
		
		// translate eye to the origin
		Matrix4f m2 = new Matrix4f(1, 0, 0, -scene.activeCamera.position.x, 
				   				   0, 1, 0, -scene.activeCamera.position.y,
				   				   0, 0, 1, -scene.activeCamera.position.z,
				   				   0, 0, 0, 1);

		Mv = Matrix4f.computeProduct(m1, m2);

	}
	

	/**
	 * Traces all transformations in the window
	 */
	public void trace()
	{
		scene.sceneGraph.Mi = new Matrix4f(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
		trace(scene.sceneGraph);
		traceBackground();
	}
	
	public void trace(SceneGraph sg)
	{
		int index = 0;
		for(SceneGraph sgChild : sg.children)
		{	
			Transformation transf = sgChild.value;
			if(transf instanceof Shape)
			{	
				Shape shape = (Shape)transf;
				// here Mi is the final transformation matrix
				// of the shape
				trace(shape, sg.Mi); 
			}else
			{
				//sgChild.setMi(sg.Mi, transf.Mt());
				trace(sgChild);
			}
			index++;
		}
	}
	
	/**
	 * Fill the background window
	 */
	public void traceBackground()
	{
		int i = 0, j = 0;
		while(i<zBuffer.length)
		{
			float pix[] = zBuffer[i];
			j = 0;
			while(j<pix.length)
			{
				// if there is not a shape on this pixel
				// he has the background color
				if(zBuffer[i][j] == Float.MAX_VALUE)
					cgPanel.drawPixel(i, j, scene.background.x, scene.background.y, scene.background.z);
				j++;
			}
			i++;
		}
	}
	
	/**
	 * Computes total matrix
	 */
	public void computeM()
	{
		// algorithm p171 2nd
		computeMo();
		computeMp();
		computeMv();
		
		M = Matrix4f.computeProduct(Mo, Mp);
		M = Matrix4f.computeProduct(M, Mv);
	}
	
	/**
	 * Traces a shape in the window
	 * @param shape
	 */
	public void trace(Shape shape, Matrix4f Mt)
	{
		int indexTriangle = 0;
		
		// for each triangle of this shape
		for(Triangle triangle : shape.geometry.triangles)
		{
			// we compute the 2D coordinates of the triangle
			Point3f a = getCoord2D(triangle.a, Mt);
			Point3f b = getCoord2D(triangle.b, Mt);
			Point3f c = getCoord2D(triangle.c, Mt);
			
			Scanner scan = new Scanner(System.in);
			// and then we raster the triangle expressed in 2d
			rasterize(shape, indexTriangle, a, b, c, Mt);//scan.nextLine();	
			indexTriangle++;
		}
	}
	
	/**
	 * Defines the depth and the color of the triangle
	 * the nearest of the pixel x y
	 * @param i x 
	 * @param j y
	 * @param c color of the pixel
	 * @param z depth of the triangle
	 */
	public void setPixel(int i, int j, Color3f c, float z)
	{
		if(i < 0 || i >= zBuffer.length || j < 0 || j >= zBuffer[0].length)
			return;
		
		if(z < zBuffer[i][j])
		{
			zBuffer[i][j] = z;
			zBufferColor[i][j] = c;
		}
	}

	/**
	 * Draws a line
	 * @param a first extreme point of the line
	 * @param b second extreme point of the line
	 */
	public void drawLine(Point2f a, Point2f b)
	{	
		float x0 = a.x;
		float x1 = b.x;
		float y0 = a.y;
		float y1 = b.y;
		float x;
		float y;
		
		// if x coordinates are the same
		if(x1==x0)
		{
			if(y0 > y1)
			{
				float ytmp = y0;
				y0 = y1;
				y1 =ytmp;
			}

			for(y = y0; y < y1; y++)
			{
				cgPanel.drawPixel((int)x0, (int)y, 1, 1, 1);
			}
			return;
		}
		
		// algorithm p61 2nd
		if(x0 > x1)
		{
			float xtmp = x0;
			x0 = x1;
			x1 = xtmp;
			float ytmp = y0;
			y0 = y1;
			y1 =ytmp;
		}
		
		float diffy = (y1-y0)/(x1-x0);
		y = y0;

		for(x = x0; x < x1; x++)
		{
			cgPanel.drawPixel((int)x, Math.round(y), 1, 1, 1);
			y = y + diffy;
		}
	}

	/**
	 * Returns the 2d coordinates of a 3d point
	 * @param a the 3d point
	 * @return
	 */
	public Point3f getCoord2D(Point3f a, Matrix4f Mt)
	{
		// compute Mtot
		Matrix4f res = Matrix4f.computeProduct(M, Mt);
		// res = M*a
		res = Matrix4f.computeProduct(res, a);
		// res = [x/h, y/h, z/h, 1]
		return new Point3f(res.getElement(0, 0)/res.getElement(3, 0), res.getElement(1, 0)/res.getElement(3, 0), res.getElement(2, 0)/res.getElement(3, 0));
	}
	
	/**
	 * Returns the 3d coordinates of a 3d point
	 * according to Mt
	 * @param a the 3d point
	 * @return
	 */
	public Point3f getCoord3D(Point3f a, Matrix4f Mt)
	{
		Matrix4f res = Matrix4f.computeProduct(Mt, a);
		return new Point3f(res.getElement(0, 0), res.getElement(1, 0), res.getElement(2, 0));
	}
	
	/**
	 * Returns a vector
	 * according to transformation matrix
	 * @param a the 3d point
	 * @return
	 */
	public Vector3f getTransformedNormalVector(Vector3f a, Matrix4f Mt)
	{
		Matrix4f N = new Matrix4f(	Mt.m22*Mt.m33-Mt.m23*Mt.m32, Mt.m23*Mt.m31-Mt.m21*Mt.m33, Mt.m21*Mt.m32-Mt.m22*Mt.m31, 0,
									Mt.m13*Mt.m32-Mt.m12*Mt.m33, Mt.m11*Mt.m33-Mt.m13*Mt.m31, Mt.m12*Mt.m31-Mt.m11*Mt.m32, 0,
									Mt.m12*Mt.m23-Mt.m13*Mt.m22, Mt.m13*Mt.m21-Mt.m11*Mt.m23, Mt.m11*Mt.m22-Mt.m12*Mt.m21, 0,
									0, 0, 0, 0);
		
		Matrix4f res = Matrix4f.computeProduct(N, new Point3f(a));
		/*System.out.println("avant");
		System.out.println(a);
		System.out.println("après");
		System.out.println(new Vector3f(res.getElement(0, 0), res.getElement(1, 0), res.getElement(2, 0)));*/
		return new Vector3f(res.getElement(0, 0), res.getElement(1, 0), res.getElement(2, 0));
	}

	
	/**
	 * Rasters a triangle
	 * @param shape the shape
	 * @param indexTriangle the index of the triangle to raster
	 * @param a first 2D point of the triangle
	 * @param b second 2D point of the triangle
	 * @param c third 2D point of the triangle
	 */
	public void rasterize(Shape shape, int indexTriangle, Point3f a, Point3f b, Point3f c, Matrix4f Mt)
	{
		// algorithm p66 2nd
		float Xmin = Math.max(0, getXmin(a, b, c));
		float Xmax = Math.min(scene.Nx-1, getXmax(a, b, c));
		float Ymin = Math.max(0, getYmin(a, b, c));
		float Ymax = Math.min(scene.Ny-1, getYmax(a, b, c));
		float alpha = 0;
		float beta = 0;
		float gamma = 0;

		float falpha = f12(a.x, a.y, b, c);
		float fbeta = f20(b.x, b.y, a, c);
		float fgamma = f01(c.x, c.y, a, b);
		
		for(float y = Ymin; y <= Ymax; y++)
		{
			for(float x = Xmin; x <= Xmax; x++)
			{
				alpha = f12(x, y, b, c) / f12(a.x, a.y, b, c);
				beta = f20(x, y, a, c) / f20(b.x, b.y, a, c);
				gamma = f01(x, y, a, b) / f01(c.x, c.y, a, b);

				if( alpha > 0 && beta > 0 && gamma > 0)
				{
					if((alpha>0 || falpha*f12(-1, -1, b, c)>0) && (beta>0 || fbeta*f20(-1, -1, a, c)>0) && (gamma>0 || fgamma*f01(-1, -1, a, b)>0))
					{
						// compute z
						float zEval = a.z*alpha + b.z*beta + c.z*gamma;
						
						// this triangle is the nearest
						if(zEval <= zBuffer[(int)x][(int)y])
						{
							// compute s
							// interpolation of triangle points
							Vector3f a3D = new Vector3f(shape.geometry.triangles.get(indexTriangle).a);
							Vector3f b3D = new Vector3f(shape.geometry.triangles.get(indexTriangle).b);
							Vector3f c3D = new Vector3f(shape.geometry.triangles.get(indexTriangle).c);
							a3D = Vector3f.computeScalarProduct(a3D, alpha);
							b3D = Vector3f.computeScalarProduct(b3D, beta);
							c3D = Vector3f.computeScalarProduct(c3D, gamma);
							Point3f s = new Point3f(Vector3f.computeAddition(a3D, b3D, c3D));
							
							// compute n
							Vector3f normalVector = getNormalVector(shape, indexTriangle, (int)x, (int)y, a, b, c);
							// transform n according to Mt
							normalVector = getTransformedNormalVector(normalVector, Mt);
							
							// compute color
							Color3f color = computeColor(s, shape, indexTriangle, normalVector);
							setPixel((int)x, (int)y, color, zEval);
							
							Color3f zColor = zBufferColor[(int)x][(int)y];
							cgPanel.drawPixel((int)x, (int)y, zColor.x, zColor.y, zColor.z);
						}	
					}
				}
			}
		}
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
	public Color3f computeColor(Point3f s, Shape shape, Integer indexTriangle, Vector3f n)
	{
		Material mat = shape.material;
		if(mat instanceof DiffuseMaterial)
		{
			return computeDiffuseMaterialColor(s, shape, n);
		}
		return null;
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
	public Color3f computeDiffuseMaterialColor(Point3f s, Shape shape, Vector3f n)
	{
		// algorithm p192 2nd
		Color3f cr = new Color3f(shape.material.color);
		Color3f colorLight = new Color3f();

		for(Light light : scene.activeLights.values())
		{
			PointLight pl = (PointLight)light;
			Vector3f l = new Vector3f(pl.position.x - s.x,
									  pl.position.y - s.y,
									  pl.position.z - s.z);
			
			Color3f cl = pl.color;
			float LxN = Vector3f.computeDot(n, l);
			
			// Problème avec la lumière à l'intérieur des cones/cylindre.
			// Logique puisque les vecteurs ne pointent pas vers l'intérieur.
			colorLight.x += cl.x * cr.x * Math.max(0, LxN * (1/(l.length()*n.length())));
			colorLight.y += cl.y * cr.y * Math.max(0, LxN * (1/(l.length()*n.length())));
			colorLight.z += cl.z * cr.z * Math.max(0, LxN * (1/(l.length()*n.length())));
				
		}
		// maximum 1 sinon il y a des couleurs bizarres
		colorLight.x = Math.min(1, colorLight.x);
		colorLight.y = Math.min(1, colorLight.y);
		colorLight.z = Math.min(1, colorLight.z);
		
		return colorLight;
	}
	
	/**
	 * Compute the normal vector of a 3D point
	 * @param shape
	 * @param index index of the triangle in the shape
	 * @param x pixel x
	 * @param y pixel y
	 * @param a first 2D point of the triangle according to index
	 * @param b second 2D point of the triangle according to index
	 * @param c third 2D point of the triangle according to index 
	 * @return
	 */
	public Vector3f getNormalVector(Shape shape, int index, int x, int y, Point3f a, Point3f b, Point3f c)
	{	
		//algorithm p197 2nd
		Integer[][] indexFace = shape.geometry.faces.get(index);

		// if normal vectors of each vertex are not defined
		if(indexFace[0][2] == -1 || indexFace[1][2] == -1 || indexFace[2][2] == -1 /*|| true*/)
		{
			return shape.geometry.faceNormals.get(index);
		}
		
		// normal vectors found, we gonna interpolate them
		Vector3f n1 = shape.geometry.normals.get(indexFace[0][2]);
		Vector3f n2 = shape.geometry.normals.get(indexFace[1][2]);
		Vector3f n3 = shape.geometry.normals.get(indexFace[2][2]);

		float alpha = f12(x, y, b, c) / f12(a.x, a.y, b, c);
		float beta = f20(x, y, a, c) / f20(b.x, b.y, a, c);
		float gamma = f01(x, y, a, b) / f01(c.x, c.y, a, b);

		n1 = Vector3f.computeScalarProduct(n1, alpha);
		n2 = Vector3f.computeScalarProduct(n2, beta);
		n3 = Vector3f.computeScalarProduct(n3, gamma);
		
		return Vector3f.computeAddition(n1, n2, n3);
	}
	
	/**
	 * Computes l b n r t f points
	 */
	public void computeLBNRTF()
	{
		// Technique personnelle basée sur le fait
		// que les points sont définis en fonction de la caméra.
		// Plus facile à utiliser avec les fichiers sdl.
		LBNRTF = new HashMap<String, Float>();

		LBNRTF.put("l", 1f);
		LBNRTF.put("b", -1f);
		LBNRTF.put("n", 1f);
		LBNRTF.put("r", -1f);
		LBNRTF.put("t", 1f);
		LBNRTF.put("f", 2f);


	}
	
	/*public void defineNF(SceneGraph sg, Float near, Float far)
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
					if(near<Math.abs(triangle.a.z)) near = triangle.a.z;
					if(near<Math.abs(triangle.b.z)) near = triangle.b.z;
					if(near<Math.abs(triangle.c.z)) near = triangle.c.z;
					if(far>Math.abs(triangle.a.z)) far = triangle.a.z;
					if(far>Math.abs(triangle.b.z)) far = triangle.b.z;
					if(far>Math.abs(triangle.c.z)) far = triangle.c.z;
				}
			}else
			{
				defineNF(sgChild, n, f);
			}
			index++;
		}
	}*/
	
	/**
	 * Function defined in the algorithm p64 2nd
	 */
	public float f01(float x , float y, Point3f a, Point3f b)
	{
		return (a.y - b.y)*x + (b.x - a.x)*y + a.x*b.y - b.x*a.y;
	}
	
	/**
	 * Function defined in the algorithm p64 2nd
	 */
	public float f12(float x , float y, Point3f b, Point3f c)
	{
		return (b.y - c.y)*x + (c.x - b.x)*y + b.x*c.y - c.x*b.y;
	}
	
	/**
	 * Function defined in the algorithm p64 2nd
	 */
	public float f20(float x , float y, Point3f a, Point3f c)
	{
		return (c.y - a.y)*x + (a.x - c.x)*y + c.x*a.y - a.x*c.y;
	}
	
	/**
	 * Returns the minimum x coordinate among three points
	 * @param a first point
	 * @param b second point
	 * @param c third point
	 * @return
	 */
	public float getXmin(Point3f a, Point3f b, Point3f c)
	{
		return new Float(Math.floor(Math.min(a.x, Math.min(b.x, c.x)))).intValue();
	}
	
	/**
	 * Returns the maximum x coordinate among three points
	 * @param a first point
	 * @param b second point
	 * @param c third point
	 * @return
	 */
	public float getXmax(Point3f a, Point3f b, Point3f c)
	{
		return new Float(Math.ceil(Math.max(a.x, Math.max(b.x, c.x)))).intValue();
	}
	
	/**
	 * Returns the minimum y coordinate among three points
	 * @param a first point
	 * @param b second point
	 * @param c third point
	 * @return
	 */
	public float getYmin(Point3f a, Point3f b, Point3f c)
	{
		return new Float(Math.floor(Math.min(a.y, Math.min(b.y, c.y)))).intValue();
	}
	
	/**
	 * Returns the maximum y coordinate among three points
	 * @param a first point
	 * @param b second point
	 * @param c third point
	 * @return
	 */
	public float getYmax(Point3f a, Point3f b, Point3f c)
	{
		return new Float(Math.ceil(Math.max(a.y, Math.max(b.y, c.y)))).intValue();
	}
	
}