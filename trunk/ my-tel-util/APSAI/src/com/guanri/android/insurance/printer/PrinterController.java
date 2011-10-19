package com.guanri.android.insurance.printer;

public class PrinterController {

	//水平制表符
	public static final int PRINTER_CMD_HT = 0;
	//打印并走纸ֽ
	public static final int PRINTER_CMD_LF = 1;
	//如果处于页面模式，打印结束后，打印机将返回至正常模式
	//如果标签功能被使用，将进纸至下一个打印位置
	public static final int PRINTER_CMD_FF = 2;
	//打印并返舱
	public static final int PRINTER_CMD_CR = 3;
	//在页面模式下取消数据打印
	public static final int PRINTER_CMD_CAN = 4;
	//传输实时状态
	public static final int PRINTER_CMD_DLE_EOF = 5;
	//页面模式下打印数据
	public static final int PRINTER_CMD_ESC_FF = 6;
	//设置字符右侧间距
	public static final int PRINTER_CMD_ESC_SP = 7;
	//选择打印模式
	public static final int PRINTER_CMD_ESC_EXCLAMATION = 8;
	//设置绝对打印位置
	public static final int PRINTER_CMD_ESC_DALLOR = 9;
	//选择位图模式
	public static final int PRINTER_CMD_ESC_STAR = 10;
	//开启或关闭下划线模式
	public static final int PRINTER_CMD_ESC_UNDERLINE = 11;	
	//选择默认行间距
	public static final int PRINTER_CMD_ESC_2 = 12;
	//设置行间距
	public static final int PRINTER_CMD_ESC_3 = 13;
	//初始化打印机
	public static final int PRINTER_CMD_ESC_AT = 14;
	//设置水平制表位置
	public static final int PRINTER_CMD_ESC_D = 15;
	//开启或关闭着重模式
	public static final int PRINTER_CMD_ESC_E_ON = 16;
	public static final int PRINTER_CMD_ESC_E_OFF = 57;
	
	//开启或关闭倍宽模式
	public static final int PRINTER_CMD_ESC_G = 17;
	//打印并走纸ֽ
	public static final int PRINTER_CMD_ESC_UPPER_J = 18;
	//选择页面模式
	public static final int PRINTER_CMD_ESC_L = 19;
	//选择字符字体/MSR读卡
	public static final int PRINTER_CMD_ESC_M = 20;
	//取消读卡模式
	public static final int PRINTER_CMD_EOT = 21;
	//选择标准模式
	public static final int PRINTER_CMD_ESC_S = 22;
	//选择页面模式下打印方向
	public static final int PRINTER_CMD_ESC_T = 23;
	//设置相对打印位置
	public static final int PRINTER_CMD_ESC_BACKSLASH = 24;
	//选择对齐
	public static final int PRINTER_CMD_ESC_LOWER_A = 25;
	//打印并走纸n行
	public static final int PRINTER_CMD_ESC_LOWER_D = 26;
	//设置字符行间距
	public static final int PRINTER_CMD_ESC_UPPER_A = 27;
	//设置字符倍搞打印（n倍）
	public static final int PRINTER_CMD_ESC_H = 28;
	//设置字符倍搞打印（双倍）
	public static final int PRINTER_CMD_ESC_I = 29;
	//设置页面模式打印区域
	public static final int PRINTER_CMD_ESC_W = 30;
	//设置字符倍宽打印
	public static final int PRINTER_CMD_ESC_X = 31;
	//打印并走纸n点行
	public static final int PRINTER_CMD_ESC_LOWER_J = 32;
	//设置字符倍宽打印
	public static final int PRINTER_CMD_FS_SO = 33;
	//解除字符倍宽打印设置
	public static final int PRINTER_CMD_FS_DC_4 = 34;
	//设置字符打印模式
	public static final int PRINTER_CMD_FS_EXCLAMATION = 35;
	//定义下装点图
	public static final int PRINTER_CMD_GS_STAR = 36;
	//在页面模式下设置绝对垂直打印位置
	public static final int PRINTER_CMD_GS_BACKSLASH = 37;
	//设置字符打印模式
	public static final int PRINTER_CMD_GS_EXCLAMATION = 38;
	//在页面模式下设置绝对垂直打印位置
	public static final int PRINTER_CMD_GS_DALLOR = 39;
	//打印机测试
	public static final int PRINTER_CMD_GS_LEFT_BRACKET_1 = 40;
	//
	public static final int PRINTER_CMD_GS_LEFT_BRACKET_2 = 41;
	public static final int PRINTER_CMD_GS_LEFT_BRACKET_3 = 42;
	public static final int PRINTER_CMD_GS_LEFT_BRACKET_4 = 43;
	public static final int PRINTER_CMD_GS_LEFT_BRACKET_5 = 44;
	public static final int PRINTER_CMD_GS_LEFT_BRACKET_6 = 45;
	
	//开启或关闭，黑白反转打印模式
	public static final int PRINTER_CMD_GS_B = 46;
	//传输电池状态̬
	public static final int PRINTER_CMD_GS_I_B = 47;
	//传输打印机标识码ID
	public static final int PRINTER_CMD_GS_I_N = 48;
	//设置左边距
	public static final int PRINTER_CMD_GS_L = 49;
	//设置打印区宽度
	public static final int PRINTER_CMD_GS_W_NL_NH = 50;
	//设置条形码宽度
	public static final int PRINTER_CMD_GS_W_N = 51;
	//打印下装点图
	public static final int PRINTER_CMD_GS_SLASH = 52;
	//选择HRI字体字体
	public static final int PRINTER_CMD_GS_F = 53;
	//设置条码高度
	public static final int PRINTER_CMD_GS_H = 54;
	//打印条码
	public static final int PRINTER_CMD_GS_K = 55;
	//设置条码宽度
	public static final int PRINTER_CMD_GS_R = 56;
	
	
	public static byte[] getPrinterCmd(int cmdType) {
		byte[] printerCmdBuf = null;

		switch (cmdType) {
		case PRINTER_CMD_HT:
			printerCmdBuf = new byte [1];
			printerCmdBuf[0] = 0x09;
			break;
		case PRINTER_CMD_LF:
			printerCmdBuf = new byte [1];
			printerCmdBuf[0] = 0x0A;
			break;
		case PRINTER_CMD_FF:
			printerCmdBuf = new byte [1];
			printerCmdBuf[0] = 0x0C;
			break;
		case PRINTER_CMD_CR:
			printerCmdBuf = new byte [1];
			printerCmdBuf[0] = 0x0D;
			break;
		case PRINTER_CMD_CAN:
			printerCmdBuf = new byte [1];
			printerCmdBuf[0] = 0x18;
			break;
		case PRINTER_CMD_DLE_EOF:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x10;
			printerCmdBuf[1] = 0x04;
			printerCmdBuf[2] = 1;//n:1-4
			break;

		case PRINTER_CMD_ESC_FF:
			printerCmdBuf = new byte [2];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x0C;
			break;
		case PRINTER_CMD_ESC_SP:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x20;
			printerCmdBuf[2] = 0;//n:0-255
			break;
		case PRINTER_CMD_ESC_EXCLAMATION:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x21;
			printerCmdBuf[2] = 0;//n:0-255
			break;
		case PRINTER_CMD_ESC_DALLOR:
			printerCmdBuf = new byte [4];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x24;
			printerCmdBuf[2] = 0;//nL:0-255
			printerCmdBuf[3] = 0;//nH:0-255
			break;
		case PRINTER_CMD_ESC_STAR:
			printerCmdBuf = new byte [6];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x2A;
			printerCmdBuf[2] = 0;//m:0,1,32,33
			printerCmdBuf[3] = 0;//nL:0-255
			printerCmdBuf[4] = 0;//nH:0-3
			printerCmdBuf[5] = 0;//d:0-255
			break;
		case PRINTER_CMD_ESC_UNDERLINE:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x2D;
			printerCmdBuf[2] = 0;//n:0-2, 48-50
			break;
		case PRINTER_CMD_ESC_2:
			printerCmdBuf = new byte [2];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x32;
			break;
		case PRINTER_CMD_ESC_3:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x33;
			printerCmdBuf[2] = 0;//n:0-255
			break;
		case PRINTER_CMD_ESC_AT:
			printerCmdBuf = new byte [2];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x40;
			break;
		case PRINTER_CMD_ESC_D:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x64;
			printerCmdBuf[2] = 4;//n:8,16,24,32,40,......232,240,248


			break;
		case PRINTER_CMD_ESC_E_ON:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x45;
			printerCmdBuf[2] = 1;//n:1-255 ȱʡΪ0
			break;
		case PRINTER_CMD_ESC_E_OFF:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x45;
			printerCmdBuf[2] = 0;//n:1-255 ȱʡΪ0
			break;
			
		case PRINTER_CMD_ESC_G:
			
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x47;
			printerCmdBuf[2] = 0;//n:1-255 ȱʡΪ0

			break;
		case PRINTER_CMD_ESC_UPPER_J:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x4A;
			printerCmdBuf[2] = 0;//n:1-255 ȱʡΪ0

			break;
		case PRINTER_CMD_ESC_L:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x4C;
			break;
		case PRINTER_CMD_ESC_M:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x4D;
			printerCmdBuf[2] = 0;//n:0,1,48,49,67,68,69,70,71,72,73 ȱʡΪ0
			break;
		case PRINTER_CMD_EOT:
			printerCmdBuf = new byte [1];
			printerCmdBuf[0] = 0x04;
			break;
		case PRINTER_CMD_ESC_S:
			printerCmdBuf = new byte [2];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x53;
			break;
		case PRINTER_CMD_ESC_T:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x54;
			printerCmdBuf[2] = 0;//n:1-3, 48-51 ȱʡΪ0

			break;
		case PRINTER_CMD_ESC_BACKSLASH:
			printerCmdBuf = new byte [4];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x5C;
			printerCmdBuf[2] = 0;//nL:0-255
			printerCmdBuf[3] = 0;//nH:0-255
			break;
		case PRINTER_CMD_ESC_LOWER_A:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x61;
			printerCmdBuf[2] = 0;//n:0-2, 48-50 ȱʡΪ0
			break;
		case PRINTER_CMD_ESC_LOWER_D:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x64;
			printerCmdBuf[2] = 0;//n:0-255
			break;

		case PRINTER_CMD_ESC_UPPER_A:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x41;
			printerCmdBuf[2] = 0;//n:0-255
			break;
		case PRINTER_CMD_ESC_H:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x48;
			printerCmdBuf[2] = 0;//n:1-8
			break;
		case PRINTER_CMD_ESC_I:
			printerCmdBuf = new byte [2];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x49;
			break;
		case PRINTER_CMD_ESC_W:
			printerCmdBuf = new byte [10];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x57;
			
			printerCmdBuf[2] = 0x00;//xL:0-255
			printerCmdBuf[3] = 0x00;//xH:0-255
			printerCmdBuf[4] = 0x00;//yL:0-255
			printerCmdBuf[5] = 0x00;//yH:0-255
			printerCmdBuf[6] = 0x00;//dxL:0-255
			printerCmdBuf[7] = 0x00;//dxH:0-255
			printerCmdBuf[8] = 0x00;//dyL:0-255
			printerCmdBuf[9] = 0x00;//dyH:0-255
			break;
		case PRINTER_CMD_ESC_X:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x58;
			printerCmdBuf[2] = 0;//n:1-8
			break;
		case PRINTER_CMD_ESC_LOWER_J:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1B;
			printerCmdBuf[1] = 0x6A;
			printerCmdBuf[2] = 0;//n:0-255
			break;

		case PRINTER_CMD_FS_SO:
			printerCmdBuf = new byte [2];
			printerCmdBuf[0] = 0x1C;
			printerCmdBuf[1] = 0x0E;
			break;
		case PRINTER_CMD_FS_DC_4:
			printerCmdBuf = new byte [2];
			printerCmdBuf[0] = 0x1C;
			printerCmdBuf[1] = 0x14;
			break;
		case PRINTER_CMD_FS_EXCLAMATION:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1C;
			printerCmdBuf[1] = 0x21;
			printerCmdBuf[2] = 4;//0<n<255
			break;
		case PRINTER_CMD_GS_STAR:
			printerCmdBuf = new byte [5];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x2A;
			printerCmdBuf[2] = 2;//1 <= n1 <= 255
			printerCmdBuf[3] = 1;//1 <= n2<= 48      n1*n2<=1536
			printerCmdBuf[4] = 0;//0 <= d <= 255

			break;
		case PRINTER_CMD_GS_BACKSLASH:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x2F;
			printerCmdBuf[2] = 0;//0<=m<=3, 48<=m<=51
			break;
		case PRINTER_CMD_GS_EXCLAMATION:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x21;
			printerCmdBuf[2] = 0;//1<=n<=255

			break;
		case PRINTER_CMD_GS_DALLOR:
			printerCmdBuf = new byte [4];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x24;
			printerCmdBuf[2] = 0;//0<=nL<=255
			printerCmdBuf[3] = 0;//0<=nH<=255                    0<=(nL+ nH*256)<=65535

			break;
		case PRINTER_CMD_GS_LEFT_BRACKET_1:
			printerCmdBuf = new byte [7];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x28;
			
			printerCmdBuf[2] = 0x41;
			printerCmdBuf[3] = 2;//pL = 2
			printerCmdBuf[4] = 0;//pH = 0      (pL + pH *256) = 2
			printerCmdBuf[5] = 0x00;//0<=n<=2, 48<=n<=50
			printerCmdBuf[6] = 0x00;//1<=m<=3, 49<=m<=51
			break;
		case PRINTER_CMD_GS_LEFT_BRACKET_2:
			printerCmdBuf = new byte [8];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x28;
			
			printerCmdBuf[2] = 0x45;
			printerCmdBuf[3] = 3;//pL = 3
			printerCmdBuf[4] = 0;//pH = 0      (pL + pH *256) = 3
			printerCmdBuf[5] = 1;
			printerCmdBuf[6] = 73;
			printerCmdBuf[7] = 72;
			
			break;
		case PRINTER_CMD_GS_LEFT_BRACKET_3:
			printerCmdBuf = new byte [9];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x28;
			
			printerCmdBuf[2] = 0x45;
			printerCmdBuf[3] = 4;//pL = 4
			printerCmdBuf[4] = 0;//pH = 0      (pL + pH *256) = 4
			printerCmdBuf[5] = 2;
			printerCmdBuf[6] = 79;
			printerCmdBuf[7] = 85;
			printerCmdBuf[8] = 84;
			
			break;
		case PRINTER_CMD_GS_LEFT_BRACKET_4:
			
			printerCmdBuf = new byte [8];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x28;
			
			printerCmdBuf[2] = 0x45;
			printerCmdBuf[3] = 4;//pL = 4
			printerCmdBuf[4] = 0;//pH = 0      (pL + pH *256) = 4
			printerCmdBuf[5] = 3;
			printerCmdBuf[6] = 5;
			printerCmdBuf[7] = 48;//b: 48,49,50			
			break;
		case PRINTER_CMD_GS_LEFT_BRACKET_5:
			printerCmdBuf = new byte [9];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x28;
			
			printerCmdBuf[2] = 0x45;
			printerCmdBuf[3] = 3;//3<=pL<=8
			printerCmdBuf[4] = 0;//0<=pH<=255      3 <=(pL + pH *256) <= 8
			printerCmdBuf[5] = 0x0B;
			printerCmdBuf[6] = 1;
			printerCmdBuf[7] = 48;
			printerCmdBuf[8] = 48;
			
			break;
		case PRINTER_CMD_GS_LEFT_BRACKET_6:
			
			printerCmdBuf = new byte [7];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x28;
			
			printerCmdBuf[2] = 0x45;
			printerCmdBuf[3] = 2;//pL = 4
			printerCmdBuf[4] = 0;//pH = 0      (pL + pH *256) = 2
			printerCmdBuf[5] = 12;
			printerCmdBuf[6] = 1;//1<=a<=4			
			
			break;
		case PRINTER_CMD_GS_B:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x42;
			printerCmdBuf[2] = 0;//0<=n<=255
			break;
		case PRINTER_CMD_GS_I_B:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x49;
			printerCmdBuf[2] = 0x62;//0<=n<=255
			break;
		case PRINTER_CMD_GS_I_N:
			printerCmdBuf = new byte [4];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x49;
			printerCmdBuf[2] = 65;//65<=n<=69
			break;
		case PRINTER_CMD_GS_L:
			printerCmdBuf = new byte [4];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x4C;
			printerCmdBuf[2] = 2;//1<= nL <=255    
			printerCmdBuf[3] = 3;//0<=nH<=255            (nL + nH*256)=0(nL =0, nH = 0)

			break;
		case PRINTER_CMD_GS_W_NL_NH:
			printerCmdBuf = new byte [4];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x57;
			printerCmdBuf[2] = (byte)128;//0<= nL <=255    
			printerCmdBuf[3] = 1;//0<=nH<=255            (nL + nH*256)=384(nL =128, nH = 1)
			
			break;
		case PRINTER_CMD_GS_W_N:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x77;
			printerCmdBuf[2] = 3;//2<=n<=6
			break;
		case PRINTER_CMD_GS_SLASH:
			printerCmdBuf = new byte [4];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x5C;
			printerCmdBuf[2] = 0;//0<= nL <=255    
			printerCmdBuf[3] = 0;//0<=nH<=255            (nL + nH*256)
			break;
		case PRINTER_CMD_GS_F:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x66;
			printerCmdBuf[2] = 0;//n:0,1,48,49
			break;
		case PRINTER_CMD_GS_H:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x68;
			printerCmdBuf[2] = (byte) 0xA2;//1<=n<=255
			break;
		case PRINTER_CMD_GS_K:
			break;
		case PRINTER_CMD_GS_R:
			printerCmdBuf = new byte [3];
			printerCmdBuf[0] = 0x1D;
			printerCmdBuf[1] = 0x72;
			printerCmdBuf[2] = 1;//n:1,2,49,50

			break;
		default:
			break;
		}

		return printerCmdBuf;
	}
	
	

	
}
