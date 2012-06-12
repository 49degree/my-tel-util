package net.sourceforge.tess4j;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;


public class ImageFilter {
	private BufferedImage image;

	private int iw, ih;

	private int[] pixels;

	public ImageFilter(File file){
		try {
			image = ImageIO.read(file); // 读入文件

			iw = image.getWidth();
			ih = image.getHeight();
			pixels = new int[iw * ih];
			for (int j = 0; j < ih; j++) {
				for (int i = 0; i < iw; i++) {
					pixels[j * iw + i] = image.getRGB(i, j) & 0xFFFFFF;
				}
			}
		}catch(Exception e){
			
		}
	}
	public ImageFilter(String srcImageFile) {
		this(new File(srcImageFile));

	}


	//===============灰度化====================================================================//
	/**
	 * 加权平均值
	 */
	public void addAvaGrey(){
		for (int i = 0; i < iw * ih; i++) {
			int alpha = Color.alpha(pixels[i]);
			int red = Color.red(pixels[i]);
			int green = Color.green(pixels[i]);
			int blue = Color.blue(pixels[i]);

			// 增加了图像的亮度
			red = green = blue = (int)(red*0.299+green*0.587+blue*0.144);

			pixels[i] = Color.argb(alpha ,red ,green ,blue);
		}
	}
	
	/**
	 * 最大值法
	 */
	public void maxGrey(){
		for (int i = 0; i < iw * ih; i++) {
			int alpha = Color.alpha(pixels[i]);
			int red = Color.red(pixels[i]);
			int green = Color.green(pixels[i]);
			int blue = Color.blue(pixels[i]);
			
			// 增加了图像的亮度
			red = red>green?red:green;
			red = green = blue = red>blue?red:blue;
			pixels[i] = Color.argb(alpha ,red ,green ,blue);
		}
	}
	/**
	 * 最小值法
	 */
	public void minGrey(){
		for (int i = 0; i < iw * ih; i++) {
			int alpha = Color.alpha(pixels[i]);
			int red = Color.red(pixels[i]);
			int green = Color.green(pixels[i]);
			int blue = Color.blue(pixels[i]);
			
			// 增加了图像的亮度
			red = red<green?red:green;
			red = green = blue = red<blue?red:blue;
			pixels[i] = Color.argb(alpha ,red ,green ,blue);
		}
	}
	/**
	 * 平均值法
	 */
	public void avaGrey(){
		for (int i = 0; i < iw * ih; i++) {
			int alpha = Color.alpha(pixels[i]);
			int red = Color.red(pixels[i]);
			int green = Color.green(pixels[i]);
			int blue = Color.blue(pixels[i]);
			
			// 增加了图像的亮度
			red = red<green?red:green;
			red = green = blue = (red+blue+blue)/3;
			pixels[i] = Color.argb(alpha ,red ,green ,blue);
		}
	}
	//=============== 去噪====================================================================//
	/**
	 *对灰度图进行均值滤波 
	 *窗口3*3
	 */
	public void avaFilter(int minValue,int maxValue) {
		int temp = 0;
		for (int i = 1; i < ih - 1; i++) {
			for (int j = 1; j < iw - 1; j++) {
				int alpha = Color.alpha(pixels[i*iw+j]);
				if(Color.red(pixels[(i-1)*iw+j-1])>minValue&&Color.red(pixels[(i-1)*iw+j-1])<maxValue){
					temp = Color.red(pixels[(i-1)*iw+j-1])
				       +Color.red(pixels[(i-1)*iw+j])
				       + Color.red(pixels[(i-1)*iw+j+1])
				       +Color.red(pixels[i*iw+j-1])
				       +Color.red(pixels[i*iw+j+1])
				       +Color.red(pixels[(i+1)*iw+j-1])
				       + Color.red(pixels[(i+1)*iw+j])
				       + Color.red(pixels[(i+1)*iw+j+1]);
					pixels[i*iw+j] = Color.argb(alpha, temp, temp, temp);
				}

			}
		}
	}

	/**
	 *对灰度图进行中值滤波 
	 *窗口3*3
	 */
	public void medianFilter(int minValue,int maxValue) {
		int[] temp = new int[9];
		for (int i = 1; i < ih - 1; i++) {
			for (int j = 1; j < iw - 1; j++) {
				int alpha = Color.alpha(pixels[i*iw+j]);
				if(Color.red(pixels[(i-1)*ih+j-1])>minValue&&Color.red(pixels[(i-1)*iw+j-1])<maxValue){
					temp[0] = Color.red(pixels[(i-1)*iw+j-1]);
					temp[1] = Color.red(pixels[(i-1)*iw+j]);
					temp[2] = Color.red(pixels[(i-1)*iw+j+1]);
					
					temp[3] = Color.red(pixels[i*iw+j-1]);
					temp[4] = Color.red(pixels[i*iw+j]);
					temp[5] = Color.red(pixels[i*iw+j+1]);
					
					temp[6] = Color.red(pixels[(i+1)*iw+j-1]);
					temp[7] = Color.red(pixels[(i+1)*iw+j]);
					temp[8] = Color.red(pixels[(i+1)*iw+j+1]);
					sort(temp);
					pixels[i*iw+j] = Color.argb(alpha, temp[4], temp[4], temp[4]);
				}

			}
		}
	}	
	/**
	 * 排序
	 * @param data
	 */
	private void sort(int[] data){
		for(int i=0;i<data.length;i++){
			for(int j=i+1;j<data.length;j++){
				if(data[i]>=data[j]){
					data[i] = data[i]^data[j];
					data[j]=data[j]^data[i];
					data[i] = data[j]^data[i];
				}
			}
		}
			
	}
	
	
	//=============== 二值化====================================================================//
	/** 
	 * 图像二值化
	 */
	public void changeGrey(int grey) {
		// 设定二值化的域值，默认值为100
		// 对图像进行二值化处理，Alpha值保持不变
		int red;
		for (int i = 0; i < iw * ih; i++) {
			
			int alpha = Color.alpha(pixels[i]);
			if (Color.red(pixels[i]) > grey) {
				red = 255;
			} else {
				red = 0;
			}
			pixels[i] = Color.argb(alpha, red, red, red);
		}
	}
	
	//===================================================================================//
	
	
	
	
	
	public void myFilter(){
		for (int y = 0; y < ih; y++) {
		    for (int x = 0; x < iw; x++) {
		        int rgb = pixels[y*iw+x];
		        
		        //1.首先灰度化，灰度值=0.3R+0.59G+0.11B： 
		        int gray = (int) (0.3 * Color.red(rgb) + 0.59
		            * Color.green(rgb) + 0.11 * Color.blue(rgb));
		        //2.其次是灰度反转：
		        gray = Color.rgb( Color.red(gray),
		        		Color.green(gray),
		        		Color.blue(gray));
		        //3.再次是二值化，取图片的平均灰度作为阈值，低于该值的全都为0，高于该值的全都为255：
		        
		        int value = Color.blue(gray);
		        if (value > 100) {
		        	gray = Color.argb(0xFF,255,255,255);
		        } else {
		        	gray = Color.argb(0xFF,0,0,0);
		        }
		        pixels[y*iw+x]=gray;
		    }
		}
	}
	
	public static final int BIT_COUNT_TRUECOLORS = 24;
	public void saveImageFile(String fileName) {
		BufferedImage bi = new BufferedImage(iw, ih,
				BufferedImage.TYPE_INT_ARGB);
		File file = new File(fileName);

        
		try {
			OutputStream out = new FileOutputStream(file);

	        int width = bi.getWidth();
	        int height = bi.getHeight();
	        
	        boolean needBlank = (width % 4 != 0);
	        int size = width * height * 3;
	        if (needBlank) {
	            size += (width % 4) * height;
	        }
	        BMPFileHeader fileHeader = new BMPFileHeader(size, 54);
	        BMPInfoHeader infoHeader = new BMPInfoHeader(width, height, BIT_COUNT_TRUECOLORS);

	        byte[] rgbs = new byte[3];
	        byte[] blank = new byte[width % 4];
			
			out.write(fileHeader.getData());
			out.write(infoHeader.getData());
			int index = 0;
			for (int j = ih-1; j >=0; j--) {
				for (int i = 0; i < iw; i++) {
					index += 3;
					rgbs[0] = (byte) pixels[j * iw + i];
					rgbs[1] = (byte) (pixels[j * iw + i]>>> 8);
					rgbs[2] = (byte) (pixels[j * iw + i]>>> 16);
					out.write(rgbs);
					if (needBlank && (index % (width * 3) == 0)) {
						out.write(blank);
					}
				}
			}

		}catch(Exception e){
        	
        }
        

		
		
		
		
		
		
		
		
		
		

       
        
//		for (int j = 0; j < ih; j++) {
//			for (int i = 0; i < iw; i++) {
//				im.setRGB(i, j, pixels[j*iw+i]);
//			}
//		}
//		
//		try {
//			ImageIO.write(im, "png", file);
//			im.flush();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}
	
	public BufferedImage getImage() {
		return image;
	}

	public int[] getPixels() {
		return pixels;
	}

	/**
	 * <p>Title: BMP文件内容的头结构</p>
	 *
	 * <p>Description: BMP文件内容的头结构固定是40个字节，其定义如下：</p>
	 * <p>
	 * byte[4] biSize;                            指定这个结构的长度，为40
	 * byte[4] biWidth;                            指定图象的宽度，单位是象素
	 * byte[4] biHeight;                        指定图象的高度，单位是象素
	 * byte[2] biPlanes;                        必须是1，不用考虑
	 * byte[2] biBitCount;                    指定表示颜色时要用到的位数，常用的值为1(黑白二色图), 4(16色图), 8(256色), 24(真彩色图)
	 * byte[4] biCompression;                指定位图是否压缩
	 * byte[4] biSizeImage;                    指定实际的位图数据占用的字节数
	 * byte[4] biXPelsPerMeter;            指定目标设备的水平分辨率，单位是每米的象素个数
	 * byte[4] biYPelsPerMeter;            指定目标设备的垂直分辨率，单位是每米的象素个数
	 * byte[4] biClrUsed;                        指定本图象实际用到的颜色数，如果该值为零，则用到的颜色数为2biBitCount
	 * byte[4] biClrImportant;            指定本图象中重要的颜色数，如果该值为零，则认为所有的颜色都是重要的
	 * </p>
	 *
	 * <p>Copyright: Copyright (c) 2005</p>
	 *
	 * <p>Company: 21Lotus</p>
	 *
	 * @author George Hill
	 * @version 1.0
	 */

	class BMPInfoHeader {

	    private byte[] data = new byte[40];
	    
	    public byte[] getData() {
	        return this.data;
	    }
	    
	    private int width;
	    
	    public int getWidth() {
	        return this.width;
	    }
	    
	    private int height;
	    
	    public int getHeight() {
	        return this.height;
	    }
	    
	    public int bitCount;
	    
	    public int getBitCount() {
	        return this.bitCount;
	    }
	    
	    public BMPInfoHeader(int width, int height, int bitCount) {
	        this.width = width;
	        this.height = height;
	        this.bitCount = bitCount;
	        
	        data[0] = 40;

	        int value = width;
	        data[4] = (byte) value;
	        value = value >>> 8;
	        data[5] = (byte) value;
	        value = value >>> 8;
	        data[6] = (byte) value;
	        value = value >>> 8;
	        data[7] = (byte) value;

	        value = height;
	        data[8] = (byte) value;
	        value = value >>> 8;
	        data[9] = (byte) value;
	        value = value >>> 8;
	        data[10] = (byte) value;
	        value = value >>> 8;
	        data[11] = (byte) value;

	        data[12] = 1;

	        data[14] = (byte) bitCount;

	        value = width * height * 3;
	        if (width % 4 != 0)
	          value += (width % 4) * height;
	        data[20] = (byte) value;
	        value = value >>> 8;
	        data[21] = (byte) value;
	        value = value >>> 8;
	        data[22] = (byte) value;
	        value = value >>> 8;
	        data[23] = (byte) value;
	    }
	    
	}


	/**
	 * <p> Title: BMP文件的头结构</p>
	 * 
	 * <p> Description: BMP文件的头结构固定是14个字节，其定义如下：</p>
	 * <p>
	 * byte[2] bfType;                    指定文件类型，必须是0x424D，即字符串“BM”，也就是说所有.bmp文件的头两个字节都是“BM“
	 * byte[4] bfSize;                    指定文件大小，包括这14个字节
	 * byte[2] bfReserved1;            保留字
	 * byte[2] bfReserved2;            保留字
	 * byte[4] bfOffBits;                为从文件头到实际的位图数据的偏移字节数
	 * </p>
	 * 
	 * <p> Copyright: Copyright (c) 2005</p>
	 * 
	 * <p> Company: 21Lotus</p>
	 * 
	 * @author George Hill
	 * @version 1.0
	 */

	class BMPFileHeader {

	    // Header data
	    private byte[] data = new byte[14];

	    public byte[] getData() {
	        return this.data;
	    }
	    
	    // BMP file size
	    private int size;
	    
	    public int getSize() {
	        return this.size;
	    }
	    
	    private int offset;
	    
	    public int getOffset() {
	        return this.offset;
	    }
	    
	    BMPFileHeader(int size, int offset) {
	        this.size = size;
	        this.offset = offset;
	        
	        data[0] = 'B';
	        data[1] = 'M';

	        int value = size;
	        data[2] = (byte) value;
	        value = value >>> 8;
	        data[3] = (byte) value;
	        value = value >>> 8;
	        data[4] = (byte) value;
	        value = value >>> 8;
	        data[5] = (byte) value;

	        value = offset;
	        data[10] = (byte) value;
	        value = value >>> 8;
	        data[11] = (byte) value;
	        value = value >>> 8;
	        data[12] = (byte) value;
	        value = value >>> 8;
	        data[13] = (byte) value;
	    }
	    
	}

}
