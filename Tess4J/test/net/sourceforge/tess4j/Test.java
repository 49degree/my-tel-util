package net.sourceforge.tess4j;

import java.io.File;

public class Test {

	public static void main(String[] args){
		try{
	        System.out.println("doOCR on a PNG image");
	        File imageFile = new File("test.bmp");
	        Tesseract1 instance = new Tesseract1();
	        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
	        
	        String result = instance.doOCR(imageFile);
	        System.out.println(result);
		}catch(Exception e){
			
		}

	}
}
