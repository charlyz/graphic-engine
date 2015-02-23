package representation;

import java.util.ArrayList;
import java.util.Iterator;

import transformation.Transformation;

// recursive tree
public class SceneGraph 
{
	public SceneGraph(Transformation value, SceneGraph parent) 
	{
		this.value = value;
		this.parent = parent;
	}
	
	public SceneGraph() 
	{
	}
	
	public void setMi(Matrix4f M1, Matrix4f M2)
	{
		if(Mi == null)
			Mi = Matrix4f.computeProduct(M1, M2);
	}
	
	public Matrix4f Mi;
	public Transformation value;
	public Transformation transformedValue;
	public ArrayList<SceneGraph> children = new ArrayList<SceneGraph>();
	public SceneGraph parent;
	
	
}
