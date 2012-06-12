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
			image = ImageIO.read(file); // �����ļ�

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


	//===============�ҶȻ�====================================================================//
	/**
	 * ��Ȩƽ��ֵ
	 */
	public void addAvaGrey(){
		for (int i = 0; i < iw * ih; i++) {
			int alpha = Color.alpha(pixels[i]);
			int red = Color.red(pixels[i]);
			int green = Color.green(pixels[i]);
			int blue = Color.blue(pixels[i]);

			// ������ͼ�������
			red = green = blue = (int)(red*0.299+green*0.587+blue*0.144);

			pixels[i] = Color.argb(alpha ,red ,green ,blue);
		}
	}
	
	/**
	 * ���ֵ��
	 */
	public void maxGrey(){
		for (int i = 0; i < iw * ih; i++) {
			int alpha = Color.alpha(pixels[i]);
			int red = Color.red(pixels[i]);
			int green = Color.green(pixels[i]);
			int blue = Color.blue(pixels[i]);
			
			// ������ͼ�������
			red = red>green?red:green;
			red = green = blue = red>blue?red:blue;
			pixels[i] = Color.argb(alpha ,red ,green ,blue);
		}
	}
	/**
	 * ��Сֵ��
	 */
	public void minGrey(){
		for (int i = 0; i < iw * ih; i++) {
			int alpha = Color.alpha(pixels[i]);
			int red = Color.red(pixels[i]);
			int green = Color.green(pixels[i]);
			int blue = Color.blue(pixels[i]);
			
			// ������ͼ�������
			red = red<green?red:green;
			red = green = blue = red<blue?red:blue;
			pixels[i] = Color.argb(alpha ,red ,green ,blue);
		}
	}
	/**
	 * ƽ��ֵ��
	 */
	public void avaGrey(){
		for (int i = 0; i < iw * ih; i++) {
			int alpha = Color.alpha(pixels[i]);
			int red = Color.red(pixels[i]);
			int green = Color.green(pixels[i]);
			int blue = Color.blue(pixels[i]);
			
			// ������ͼ�������
			red = red<green?red:green;
			red = green = blue = (red+blue+blue)/3;
			pixels[i] = Color.argb(alpha ,red ,green ,blue);
		}
	}
	//=============== ȥ��====================================================================//
	/**
	 *�ԻҶ�ͼ���о�ֵ�˲� 
	 *����3*3
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
	 *�ԻҶ�ͼ������ֵ�˲� 
	 *����3*3
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
	 * ����
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
	
	
	//=============== ��ֵ��====================================================================//
	/** 
	 * ͼ���ֵ��
	 */
	public void changeGrey(int grey) {
		// �趨��ֵ������ֵ��Ĭ��ֵΪ100
		// ��ͼ����ж�ֵ������Alphaֵ���ֲ���
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
		        
		        //1.���ȻҶȻ����Ҷ�ֵ=0.3R+0.59G+0.11B�� 
		        int gray = (int) (0.3 * Color.red(rgb) + 0.59
		            * Color.green(rgb) + 0.11 * Color.blue(rgb));
		        //2.����ǻҶȷ�ת��
		        gray = Color.rgb( Color.red(gray),
		        		Color.green(gray),
		        		Color.blue(gray));
		        //3.�ٴ��Ƕ�ֵ����ȡͼƬ��ƽ���Ҷ���Ϊ��ֵ�����ڸ�ֵ��ȫ��Ϊ0�����ڸ�ֵ��ȫ��Ϊ255��
		        
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
	 * <p>Title: BMP�ļ����ݵ�ͷ�ṹ</p>
	 *
	 * <p>Description: BMP�ļ����ݵ�ͷ�ṹ�̶���40���ֽڣ��䶨�����£�</p>
	 * <p>
	 * byte[4] biSize;                            ָ������ṹ�ĳ��ȣ�Ϊ40
	 * byte[4] biWidth;                            ָ��ͼ��Ŀ�ȣ���λ������
	 * byte[4] biHeight;                        ָ��ͼ��ĸ߶ȣ���λ������
	 * byte[2] biPlanes;                        ������1�����ÿ���
	 * byte[2] biBitCount;                    ָ����ʾ��ɫʱҪ�õ���λ�������õ�ֵΪ1(�ڰ׶�ɫͼ), 4(16ɫͼ), 8(256ɫ), 24(���ɫͼ)
	 * byte[4] biCompression;                ָ��λͼ�Ƿ�ѹ��
	 * byte[4] biSizeImage;                    ָ��ʵ�ʵ�λͼ����ռ�õ��ֽ���
	 * byte[4] biXPelsPerMeter;            ָ��Ŀ���豸��ˮƽ�ֱ��ʣ���λ��ÿ�׵����ظ���
	 * byte[4] biYPelsPerMeter;            ָ��Ŀ���豸�Ĵ�ֱ�ֱ��ʣ���λ��ÿ�׵����ظ���
	 * byte[4] biClrUsed;                        ָ����ͼ��ʵ���õ�����ɫ���������ֵΪ�㣬���õ�����ɫ��Ϊ2biBitCount
	 * byte[4] biClrImportant;            ָ����ͼ������Ҫ����ɫ���������ֵΪ�㣬����Ϊ���е���ɫ������Ҫ��
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
	 * <p> Title: BMP�ļ���ͷ�ṹ</p>
	 * 
	 * <p> Description: BMP�ļ���ͷ�ṹ�̶���14���ֽڣ��䶨�����£�</p>
	 * <p>
	 * byte[2] bfType;                    ָ���ļ����ͣ�������0x424D�����ַ�����BM����Ҳ����˵����.bmp�ļ���ͷ�����ֽڶ��ǡ�BM��
	 * byte[4] bfSize;                    ָ���ļ���С��������14���ֽ�
	 * byte[2] bfReserved1;            ������
	 * byte[2] bfReserved2;            ������
	 * byte[4] bfOffBits;                Ϊ���ļ�ͷ��ʵ�ʵ�λͼ���ݵ�ƫ���ֽ���
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
