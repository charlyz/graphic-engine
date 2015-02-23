package transformation;

import java.util.ArrayList;

import representation.Matrix4f;

/**
 * This class is an abstract
 * representation of a transformation
 * (shape, translation, rotation, scaling)
 * @author Dessart Charles-Eric
 *
 */
public abstract class Transformation
{
	// transformation matrix of this transformation
	public Matrix4f Mt;
	public abstract Matrix4f Mt();
	
}