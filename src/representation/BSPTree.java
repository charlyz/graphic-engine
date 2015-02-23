package representation;

import geometry.Geometry;
import geometry.Plane;
import geometry.Triangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import tools3d.Intersection;
import transformation.Shape;
import transformation.Transformation;

public class BSPTree 
{
	public BSPTree left;
	public BSPTree right;
	public int axis;
	public float D;
	public static final int XAXIS = 0;
	public static final int YAXIS = 1;
	public static final int ZAXIS = 2;
	public static int epsylon = 1;

	public BSPTree(int axis, float d) 
	{
		this.axis = axis;
		D = d;
	}
	
	public BSPTree(){}
	
	public Vector3f getPlaneNormal()
	{
		if(axis==XAXIS) return new Vector3f(1, 0, 0);
		else if(axis==YAXIS) return new Vector3f(0, 1, 0);
		else return new Vector3f(0, 0, 1);
	}
	
	public Point3f getPlanePoint()
	{
		if(axis==XAXIS) return new Point3f(D, 0, 0);
		else if(axis==YAXIS) return new Point3f(0, D, 0);
		else return new Point3f(0, 0, D);
	}

	public Point3f computeCuttedPoint(Point3f point, Point3f cp)
	{
		// algorithm p185 2nd edition
		Plane partition = new Plane(getPlaneNormal(), getPlanePoint());
		Vector3f p = new Vector3f(point);
		Vector3f n = partition.normal;
		Vector3f c = new Vector3f(cp);
		float D = partition.D;
		float t = -((Vector3f.computeDot(n, p)+D)
					/
					Vector3f.computeDot(n, Vector3f.computeMinus(c, p)));

		return new Point3f(Vector3f.computeAddition(p, Vector3f.computeScalarProduct(Vector3f.computeMinus(c, p), t)));
	}
	
	public float f(Point3f p)
	{
		if(axis==XAXIS) return p.x;
		else if(axis==YAXIS) return p.y;
		else return p.z;
	}
	
	public void add(Shape shape, Integer indexTriangle, Triangle triangle)
	{
		// algorithm p183 2nd edition
		Point3f a = triangle.a;
		Point3f b = triangle.b;
		Point3f c = triangle.c;

		float fa = f(a);
		float fb = f(b);
		float fc = f(c);
		/*if(Math.abs(fa)<epsylon) fa=0;
		if(Math.abs(fb)<epsylon) fb=0;
		if(Math.abs(fc)<epsylon) fc=0;*/
		//System.out.println("avant: " + fa + " - " + fb + " - " + fc);

		if(fa<=D && fb<=D && fc<=D)
		{
			left.add(shape, indexTriangle, triangle);
		}else if(fa>=D && fb>=D && fc>=D)
		{
			right.add(shape, indexTriangle, triangle);
		}else
		{
			float tmp;
			Point3f ptmp;
			if((fa<=D && fc<=D)||(fa>=D && fc>=D))
			{
				tmp = fb; fb = fc; fc = tmp;		
				ptmp = b; b = c; c = ptmp;		
				tmp = fa; fa = fb; fb = tmp;
				ptmp = a; a = b; b = ptmp;
			}else if((fb<=D && fc<=D)||(fb>=D && fc>=D))
			{
				tmp = fa; fa = fc; fc = tmp;
				ptmp = a; a = c; c = ptmp;
				tmp = fa; fa = fb; fb = tmp;
				ptmp = a; a = b; b = ptmp;
			}
			
			Point3f A = computeCuttedPoint(a, c);
			Point3f B = computeCuttedPoint(b, c);
			
			Triangle T1 = new Triangle(a, b, A);
			Triangle T2 = new Triangle(b, B, A);
			Triangle T3 = new Triangle(A, B, c);
			
			/*System.out.println("Base: " + triangle);
			System.out.println("T1: " + T1);
			System.out.println("T2: " + T2);
			System.out.println("T3: " + T3);
			System.out.println();*/
			
			if(fc>=D)
			{
				left.add(shape, indexTriangle, T1);
				left.add(shape, indexTriangle, T2);
				right.add(shape, indexTriangle, T3);
			}else
			{
				right.add(shape, indexTriangle, T1);
				right.add(shape, indexTriangle, T2);
				left.add(shape, indexTriangle, T3);
			}
		}
	}
	
	public boolean hit(Ray ray, float t0, float t1, ClosestTriangle ct)
	{
		float a = f(ray.base);
		float b = f(new Point3f(ray.direction));
		float p = (a + t0*b);
		
		if(p<D)
		{
			if(b<0)
			{
				return left.hit(ray, t0, t1, ct);
			}
			
			float t = Math.abs(((D-a)/b));
			
			if(t>t1)
			{
				return left.hit(ray, t0, t1, ct);
			}
			
			if(left.hit(ray, t0, t, ct))
				return true;
			
			return right.hit(ray, t, t1, ct);
		}else
		{
			if(b>0)
			{
				return right.hit(ray, t0, t1, ct);
			}
			
			float t = Math.abs(((D-a)/b));
			
			if(t>t1)
			{
				return right.hit(ray, t0, t1, ct);
			}

			if(right.hit(ray, t0, t, ct))
				return true;
			
			return left.hit(ray, t, t1, ct);
		}
	}
	
	public static void buildTree(Scene scene)
	{
		/*scene.rootBSPTree = new BSPTree(BSPTree.XAXIS, 0);
		scene.rootBSPTree.left = new BSPTree(BSPTree.YAXIS, 0);
		scene.rootBSPTree.left.left = new BSPLeaf();
		scene.rootBSPTree.left.right = new BSPLeaf();
		scene.rootBSPTree.right = new BSPTree(BSPTree.YAXIS, 0);
		scene.rootBSPTree.right.left = new BSPLeaf();
		scene.rootBSPTree.right.right = new BSPLeaf();*/

		/*scene.rootBSPTree = new BSPTree(BSPTree.XAXIS, 0);
		scene.rootBSPTree.left = new BSPLeaf();
		scene.rootBSPTree.right = new BSPLeaf();*/
		ArrayList<Integer>[] coords = getListOfCoords(scene.shapes);
		ArrayList<Integer> Xs = coords[0];
		ArrayList<Integer> Ys = coords[1];
		//for(int i : Xs) System.out.println("Xs: " + i);
		//for(int i : Ys) System.out.println("Ys: " + i);
		scene.rootBSPTree = new BSPTree();
		balanceTree(0, scene.rootBSPTree, Xs, Ys);
		fillTree(scene);
	}
	
	public static ArrayList<Integer>[] getListOfCoords(ArrayList<Shape> shapes)
	{
		ArrayList<Integer> resx;
		ArrayList<Integer> resy;
		HashMap<Integer, Boolean> buffx = new HashMap<Integer, Boolean>();
		HashMap<Integer, Boolean> buffy = new HashMap<Integer, Boolean>();
		for(Shape shape : shapes)
		{
			for(Triangle t : shape.geometry.triangles)
			{
				int coordAx;
				int coordBx;
				int coordCx;
				int coordAy;
				int coordBy;
				int coordCy;
				coordAx = (int)t.a.x;
				coordAy = (int)t.a.y;
				coordBx = (int)t.b.x;
				coordBy = (int)t.b.y;
				coordCx = (int)t.c.x;
				coordCy = (int)t.c.y;
				
				buffx.put(coordAx, true);
				buffx.put(coordBx, true);
				buffx.put(coordCx, true);
				buffy.put(coordAy, true);
				buffy.put(coordBy, true);
				buffy.put(coordCy, true);
			}
		}
		resx = new ArrayList<Integer>(buffx.keySet());
		Collections.sort(resx);
		resy = new ArrayList<Integer>(buffy.keySet());
		Collections.sort(resy);
		
		ArrayList<Integer>[] res = new ArrayList[2];
		res[0] = resx;
		res[1] = resy;
		return res;
	}
	
	public static Integer getMedian(ArrayList<Integer> P, SplittedList sl)
	{
		if (P.size() == 0)
		{
			return null;
		}
		else if (P.size() % 2 == 1)
		{
			int i = P.size()/2;
			int median = P.get(i);
			sl.P1 = new ArrayList<Integer>(P.subList(0, i));
			sl.P2 = new ArrayList<Integer>(P.subList(i+1, P.size()));
			return median;
		}else
		{
			int i = P.size()/2;
			int median = (P.get(i) + P.get((P.size()/2)-1))/2;
			sl.P1 = new ArrayList<Integer>(P.subList(0, i));
			sl.P2 = new ArrayList<Integer>(P.subList(i+1, P.size()));
			return median;
		}
	}
	
	public static void balanceTree(Integer depth, BSPTree node, ArrayList<Integer> Xs, ArrayList<Integer> Ys)
	{
		ArrayList<Integer> P, nextP;
		int axis;
		if(depth % 2 == 0){ P = Xs; nextP = Ys; axis = BSPTree.XAXIS;}
		else {P = Ys; nextP = Xs; axis = BSPTree.YAXIS;}

		SplittedList sl = new SplittedList();
		int median = getMedian(P, sl);
		
		ArrayList<Integer> P1 = sl.P1;
		ArrayList<Integer> P2 = sl.P2;
		
		
		node.D = median;
		node.axis = axis;
		//System.out.println("median: " + median + " - axis: " + axis);
		
		if(nextP.isEmpty())
		{
			node.left = new BSPLeaf();
			node.right = new BSPLeaf();
			return;
		}
		
		node.left = new BSPTree();
		node.right = new BSPTree();
		
		if(depth % 2 == 0)
		{
			balanceTree(depth+1, node.left, P1, Ys);
			balanceTree(depth+1, node.right, P2, new ArrayList<Integer>(Ys));
		}else
		{
			balanceTree(depth+1, node.left, Xs, P1);
			balanceTree(depth+1, node.right, new ArrayList<Integer>(Xs), P2);
		}
	}
	
	public static void fillTreeOldSceneGraph(SceneGraph sg, Scene scene)
	{
		BSPTree rootNode = scene.rootBSPTree;
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
					rootNode.add(shape, indexTriangle, triangle); 
					indexTriangle++;
				}
			}else
			{
				fillTreeOldSceneGraph(sgChild, scene);
			}
		}
	}
	
	public static void fillTree(Scene scene)
	{
		for(Shape shape : scene.shapes)
		{
			int indexTriangle = 0;
			Geometry geometry = shape.geometry;
				
			for(Triangle triangle : geometry.triangles)
			{
				scene.rootBSPTree.add(shape, indexTriangle, triangle); 
				indexTriangle++;
			}
		}
	}
	

}

class BSPLeaf extends BSPTree
{
	public ArrayList<Shape> shapes;
	public ArrayList<Integer> indexesTriangle;
	
	public BSPLeaf()
	{
		shapes = new ArrayList<Shape>();
		indexesTriangle = new ArrayList<Integer>();
	}

	public void add(Shape shape, Integer indexTriangle, Triangle triangle)
	{
		shapes.add(shape);
		indexesTriangle.add(indexTriangle);
	}
	
	public boolean hit(Ray ray, float t0, float t1, ClosestTriangle ct)
	{
		int i = 0;
		boolean hitBest = false;
		
		for(Shape s : shapes)
		{
			int indexTriangle = indexesTriangle.get(i);
			Triangle triangle = s.geometry.triangles.get(indexTriangle);
			BarycentricCoords bc = new BarycentricCoords();
			Float t = Intersection.RayTriangleIntersection(ray, triangle, t0, t1, bc);

			if(t<ct.t)
			{
				hitBest = true;
				ct.t = t;
				ct.closestShape = s;
				ct.indexTriangle = indexTriangle;
				ct.bc = bc;
			}
			i++;
		}
		return hitBest;
		
	}
}

class SplittedList
{
	public ArrayList<Integer> P1;
	public ArrayList<Integer> P2;
}
