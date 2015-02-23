import java.util.ArrayList;

import geometry.Plane;
import geometry.Triangle;

import javax.swing.JFrame;

import parser.SceneBuilder;
import representation.BSPTree;
import representation.Matrix4f;
import representation.Point3f;
import representation.Ray;
import representation.Scene;
import representation.Vector3f;
import tools3d.CgPanel;
import tools3d.Intersection;
import tools3d.Rasterizer;
import tools3d.RayTracer;


public class Test
{
	
	public static void main(String[] args)
	{	
		if(args.length!=5)
		{
			System.out.println("Wrong parameters.");
			System.out.println("Usage: java Test <sdl_file> <width> <height> <cr> <technique> <save_image>");
			System.out.println(" - <sdl_file>: path of the SDL file containing the scene.");
			System.out.println(" - <width>: width of the window in pixels.");
			System.out.println(" - <height>: height of the window in pixels.");
			System.out.println(" - <technique>: define which algorithm to use. values rasterization|raytracing.");
			System.out.println(" - <save_image>: if you want to save the image. values true|false");
		}else
		{
			long start = System.currentTimeMillis();
			String file = args[0];
			int width = Integer.parseInt(args[1]);
			int height = Integer.parseInt(args[2]);
			String tech = args[3];
			boolean save = Boolean.parseBoolean(args[4]);
			
			if(tech.equals("rasterization"))
			{
				Rasterizer(file, save, width, height);
			}else if(tech.equals("raytracing"))
			{
				RayTracer(file, save, width, height);
			}
			long stop = System.currentTimeMillis();
			System.out.println("Temps: " + (stop-start)/1000);
		}
	}
	
	public static void testIntesectionRayPlane()
	{
		Ray ray = new Ray(new Point3f(-2, -2, -2), new Vector3f(2, 2, 2));
		Plane plane = new Plane(new Vector3f(1, 1, 1), new Point3f(0, 0, 0));
		System.out.println("t: " + Intersection.RayPlaneIntersection(ray, plane));
	}
	
	public static void Rasterizer(String file, boolean save, int width, int height)
	{
		JFrame frame;
		CgPanel panel;
		
		try 
		{
			SceneBuilder sceneBuilder = new SceneBuilder();
			Scene scene = sceneBuilder.loadScene(file);
			panel = new CgPanel();
			
			scene.Nx = width;
			scene.Ny = height;
			Rasterizer rt = new Rasterizer(scene, panel);

			frame = new JFrame();
			frame.setSize(scene.Nx,scene.Ny );
			frame.getContentPane().add(panel);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			// Pour les machines plus lentes,
			// il faut attendre quelques secondes
			// que la fenetre s'initialise
			Thread.sleep(1000);
			
			rt.trace();
			
			if(save)
				panel.saveImage("rasterizedImage.png");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void RayTracer(String file, boolean save, int width, int height)
	{
		JFrame frame;
		CgPanel panel;
		
		try 
		{
			SceneBuilder sceneBuilder = new SceneBuilder();
			Scene scene = sceneBuilder.loadScene(file);
			
			System.out.println("Compute BSP Tree...");
	    	BSPTree.buildTree(scene);
	    	System.out.println("BSP Tree computed...");
			
			panel = new CgPanel();
			
			scene.Nx = width;
			scene.Ny = height;
			
			RayTracer rt = new RayTracer(scene, panel);

			frame = new JFrame();
			frame.setSize(scene.Nx,scene.Ny );
			frame.getContentPane().add(panel);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			// Pour les machines plus lentes,
			// il faut attendre quelques secondes
			// que la fenetre s'initialise
			Thread.sleep(1000);
			
			rt.trace();
			
			if(save)
				panel.saveImage("raytracedImage.png");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void testIntersection()
	{
		Triangle triangle = new Triangle(new Point3f(2, 0, 0), 
				 new Point3f(0, 2, 0),
				 new Point3f(0, 0, 0));

		Ray ray = new Ray(new Point3f(1, 1, 1), new Point3f(1, 1, -2));
		
		System.out.println(Intersection.RayTriangleIntersection(ray, triangle, 0, Float.MAX_VALUE));
	}
	
	public static void testMatrix()
	{
		Matrix4f a = new Matrix4f(1f, 3f, 2f, 1f, 2f, 1f, 1f, 3f, 3f, 2f, 2f, 4f, 4f, 5f, 2f, 1f);
		Matrix4f b = new Matrix4f(1, 3, 4, 2, 3, 2, 3, 3, 2, 4, 2, 1, 3, 4, 3, 4);
		System.out.println(Matrix4f.computeProduct(a, b));
	}

}
