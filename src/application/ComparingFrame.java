package application;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ComparingFrame {

	public ComparingFrame() {
		// TODO Auto-generated constructor stub
	}
	
	public double compareImageToFrameColor(String frame, String imageFile) throws IOException
	{
		double matchPercentage = 0;
		ProjectIRController c  = new ProjectIRController();
		//frame = "src/application/images/a.jpg"; //testing purposes
		//imageFile = "src/application/images/a1.jpg";
		String actualImageFile = imageFile.replace("file:", ""); //the image file has a "file:" prefix, so we remove that so that the image can be loaded correctly
	    BufferedImage comparingImage = null;
		if(imageFile.contains("http"))
		{
			c.ProgramLog(0, "Image is URL");
			URL url = new URL(imageFile);
		    comparingImage = ImageIO.read(url);
		}
		else
		{
		    comparingImage = ImageIO.read(new File(actualImageFile));
			c.ProgramLog(0, "Image is Local");

		}
		c.ProgramLog(0, "Comparing " + frame + " to " + imageFile);
		BufferedImage currentFrame = ImageIO.read(new File(frame));
		int frameWidth = currentFrame.getWidth();
		int frameHeight = currentFrame.getHeight();
		int imageWidth = comparingImage.getWidth();
		int imageHeight = comparingImage.getHeight();
		int lowerWidth = frameWidth < imageWidth? frameWidth : imageWidth;
		int lowerHeight = frameHeight < imageHeight? frameHeight : frameHeight;
	    if(frameWidth != imageWidth || frameHeight != imageHeight)
	    {
	    	c.ProgramLog(1, "The image dimensions are not the same, there will be an accuracy issue");
	    }
	    long differenceRed = 0;
	    long differenceGreen = 0;
	    long differenceBlue = 0;

	    for (int i = 0; i < lowerHeight; i++) //y position
	    {
	    	for(int j = 0; j < lowerWidth; j++) //x position
	    	{
	    		//get the RGB value of each pixel
	    		int framePixel = currentFrame.getRGB(j, i);
	    		Color framePixelRGB = new Color(framePixel, true);
	    		int frameRedValue = framePixelRGB.getRed();
	    		int frameGreenValue = framePixelRGB.getGreen();
	    		int frameBlueValue = framePixelRGB.getBlue();
	    		
	    		int imagePixel = comparingImage.getRGB(j, i);	    
	    		Color imagePixelRGB = new Color(imagePixel, true);
	    		int imageRedValue = imagePixelRGB.getRed();
	    		int imageGreenValue = imagePixelRGB.getGreen();
	    		int imageBlueValue = imagePixelRGB.getBlue();
	    		differenceRed += Math.abs(frameRedValue - imageRedValue);
	    		differenceGreen += Math.abs(frameGreenValue - imageGreenValue);
	    		differenceBlue += Math.abs(frameBlueValue - imageBlueValue);


	    	}
	    }
	    
	    long totalDifference = differenceRed + differenceGreen + differenceBlue;
	    long avgDifference = (totalDifference / 3) / (lowerHeight * lowerWidth);
	    double totalPixelAvg = (avgDifference / 255.0) * 100.0;
	    double frameSimilarity = 100.0 - totalPixelAvg; // total pixel avg calculates the % of frame difference, so to get the % of frame similarity, we just subtract total pixel from 100
	    return frameSimilarity;
		   // c.ProgramLog(0, "" + avgDifference);
	   // c.ProgramLog(0, "" + ((differenceRed/(255* lowerHeight * lowerWidth)) * 100));\
		/*
		 * c.ProgramLog(0, "" + differenceRed / (255 * lowerHeight * lowerWidth)); long
		 * d = differenceRed/(255*lowerHeight * lowerWidth); c.ProgramLog(0, ""+ d);
		 * double colorAvg = (differenceRed + differenceGreen + differenceBlue) / 3;
		 * float colorAvgTotal = differenceRed / (255 * lowerHeight * lowerWidth);
		 * c.ProgramLog(0, "" + differenceRed + "" + "255 " + lowerHeight + " " +
		 * lowerWidth); c.ProgramLog(0, ""+ colorAvgTotal); matchPercentage = 100 -
		 * ((colorAvgTotal/255)*100); return matchPercentage;
		 */
	}

}
