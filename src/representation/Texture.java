package representation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texture
{
	public String src;
	public String name;
	public BufferedImage bi;
	
	public Texture(String src, String name) 
	{
		this.src = src;
		this.name = name;
		loadImage();
	}
	
	public void loadImage()
	{
		try 
		{
			bi = ImageIO.read(new File("textures/"+src));
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public float[] getRGB(int u, int v)
	{
		if(u>=getWidth()) u = getWidth()-1;
		if(v>=getHeight()) v = getHeight()-1;
		//System.out.println("u: " + u +" - width: " + getWidth() + " - v: " + v + " - height: " + getHeight());
		int rgb = bi.getRGB(u, v);
		float r = (rgb >>16 ) & 0xFF;
		float g = (rgb >> 8 ) & 0xFF;
		float b = rgb & 0xFF;
		float[] res = new float[3];
		res[0] = r/255; res[1] = g/255; res[2] = b/255;
		return res;
	}
	
	public int getWidth()
	{
		return bi.getWidth();
	}
	
	public int getHeight()
	{
		return bi.getHeight();
	}
	
}