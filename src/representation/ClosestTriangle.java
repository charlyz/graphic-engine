package representation;

import transformation.Shape;

public class ClosestTriangle 
{
	public int indexTriangle;
	public Shape closestShape;
	public float t = Float.MAX_VALUE;
	public Matrix4f Mt;
	public BarycentricCoords bc;
	
	public ClosestTriangle() 
	{
	}

}
