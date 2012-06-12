package net.sourceforge.tess4j;

import java.io.File;

public class Test {

	public static void main(String[] args){
		try{
	        System.out.println("doOCR on a PNG image");
	        File imageFile = new File("IMAG0043.jpg");
	        
	        ImageFilter imageFilter = new ImageFilter(imageFile);
	        imageFilter.addAvaGrey();
	        imageFilter.saveImageFile("test1.bmp");
	        
	        imageFilter.changeGrey(90);
	        imageFilter.saveImageFile("test2.bmp");
	        
	        imageFilter.medianFilter(0, 180);
	        imageFilter.saveImageFile("test3.bmp");
	        
	        imageFilter.avaFilter(180, 255);
	        imageFilter.saveImageFile("test4.bmp");
	        
	        imageFilter.changeGrey(254);
	        imageFilter.saveImageFile("test5.bmp");
	        imageFile = new File("test5.bmp");
	        
	        
	        Tesseract1 instance = new Tesseract1();
	        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
	        
	        String result = instance.doOCR(imageFile);
	        System.out.println(result);
		}catch(Exception e){
			
		}

	}
}
