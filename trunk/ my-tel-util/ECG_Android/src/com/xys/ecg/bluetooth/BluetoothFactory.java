package com.xys.ecg.bluetooth;

import java.util.ArrayList;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.xys.ecg.activity.ECGApplication;
import com.xys.ecg.activity.ECG_Android;
import com.xys.ecg.activity.ECG_Android.MainEventHandler;
import com.xys.ecg.bean.HandlerWhat;
import com.xys.ecg.business.EcgBusiness;
import com.xys.ecg.file.EcgXmlFileOperate;
import com.xys.ecg.log.Logger;
import com.xys.ecg.utils.TypeConversion;

/**
 * �����������
 * 
 * @author Administrator
 * 
 */
public class BluetoothFactory extends Thread {

	// constructor
	private BluetoothFactory() {
	}

	// singleton
	private static BluetoothFactory instance = new BluetoothFactory();

	// get singleton
	public static BluetoothFactory getInstance() {

		return instance;
	}

	private int nSN; // �������

	private boolean isConnected;// �Ƿ�����������

	private ArrayList<Byte> recvBuffer = new ArrayList<Byte>(); // ���յ������ݻ�����

	public BluetoothSocket BTSocket = null;// �׽���

	private static MainEventHandler mainEventHandler = null;
	private EcgBusiness business = EcgBusiness.getInstance();// ��������ʵ��
	private Logger logger = Logger.getLogger(BluetoothFactory.class);
	private static BluetoothControl btControl = BluetoothControl.getInstance();

	// ����״̬
	public static class PARSE_STATE {
		public static final int ASYNC_L = 0;
		public static final int ASYNC_H = 1;
		public static final int CHANNEL = 2;
		public static final int FLAG = 3;
		public static final int APP_ID = 4;
		public static final int SN = 5;
		public static final int APP_LEN_L = 6;
		public static final int APP_LEN_H = 7;
		public static final int RESERVE = 8;
		public static final int CRC = 9;
		public static final int DATA = 10;
		public static final int APP_CHECK = 11;
	}

	// �������
	public static class PACKET {
		byte lead_code[] = new byte[2];
		byte channel = 0;
		byte flag = 0;
		byte app_id = 0;
		byte sn = 0;
		byte len_low = 0;
		byte len_high = 0;
		byte reserve = 0;
		byte crc = 0;
		byte appBuf[] = new byte[4096]; // ��󻺳���Ϊ4K
		byte app_check = 0;

		public PACKET() {
			lead_code[0] = 0x55;
			lead_code[1] = (byte) 0xAA;
		}

		public byte[] getpacketHead() {
			byte[] head = new byte[9];
			head[0] = lead_code[0];
			head[1] = lead_code[1];
			head[2] = channel;
			head[3] = flag;
			head[4] = app_id;
			head[5] = sn;
			head[6] = len_low;
			head[7] = len_high;
			head[8] = reserve;
			return head;
		}
	}

	public static void startBthThread(MainEventHandler handler) {
		mainEventHandler = handler;
		try {
			instance.start();
		} catch (Exception e) {

		}

	}

	// ����Ŀ�������豸
	public boolean connectBT(String strAddr, String strPsw) {
		// �������
		if (!pair(strAddr, strPsw)) {
			logger.debug("pair failed");
			return false;
		}

		// ����android��socket����Ŀ���豸
		try {
			BTSocket.connect();
		} catch (Exception e) {
			logger.debug("Bluetooth Connect failed!" + e.getMessage());
			isConnected = false;
			return false;
		}

		isConnected = true;

		logger.debug("Bluetooth Connect successful!");

		return true;
	}

	// ���
	public boolean pair(String strAddr, String strPsw) {
		// ��ѯ��û����ԣ�û��ԵĻ��������
		try {
			UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
			BluetoothAdapter bluetoothAdapter = BluetoothAdapter
					.getDefaultAdapter();

			// ȡ�����ܴ��ڵ�ϵͳ����
			bluetoothAdapter.cancelDiscovery();

			// �ж������Ƿ��,���û�򿪣�������ʾ��ǿ�д�
			if (!bluetoothAdapter.isEnabled()) {
				bluetoothAdapter.enable();
			}

			if (!BluetoothAdapter.checkBluetoothAddress(strAddr)) {
				logger.debug("address invalid");
				return false;
			}

			BluetoothDevice device = bluetoothAdapter.getRemoteDevice(strAddr);

			if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
				ClsUtils.setPin(device.getClass(), device, strPsw); //
				ClsUtils.createBond(device.getClass(), device);
			}

			BTSocket = device.createRfcommSocketToServiceRecord(uuid);

		} catch (Exception e) {
			logger.debug("Bluetooth Paire is Failed!" + e.getMessage());
			return false;// ���ʧ�ܾͷ���false
		}

		return true;
	}

	// ����ҵ������
	// Ĭ��ͨ��0
	public synchronized boolean send(byte[] data, int len) {
		byte[] finalPacket = packet(data, len);// ���

		// ����android�ķ��͹���
		nSN++;
		if (BTSocket == null) {
			logger.error("send failed, BTSocket is null");
			return false;
		}

		try {
			BTSocket.getOutputStream().write(finalPacket);// ��������

		} catch (Exception e) {
			logger.debug("Send data to bluetooth failed!" + e.getMessage());
			btControl.setConnectState(false);
			return false;
		}

		return true;
	}

	// ��ȡ����
	// ����ֵ�����ȡ�ɹ���ʧ��
	public boolean read() {

		if (BTSocket == null) {
			logger.debug("BTSocket is null");
			return false;
		}

		try {
			byte[] recvbuf = new byte[1024];

			int nReadbyte = BTSocket.getInputStream().read(recvbuf);

			if (nReadbyte == -1) {
				logger.debug("read data end");
				return false;
			}
			// �����������ݲ���ȫ�����ݻ�����

			logger.debug("recv bytes: " + nReadbyte);
			for (int i = 0; i < nReadbyte; i++) {
				recvBuffer.add(recvbuf[i]);
			}

		} catch (Exception e) {
			logger.debug("Read data is failed!" + e.getMessage());
			return false;
		}

		return true;
	}

	// �������������
	// ����ֵ�����Ƿ������ҵ�����ݰ�
	public synchronized boolean parse() {

		if (recvBuffer.size() < 1) {
			logger.debug("No more data to parse");
			return false;
		}

		PACKET packet = new PACKET();
		boolean bFindPacket = false; // �ҵ�һ����

		int ps = PARSE_STATE.ASYNC_L;// ����״̬
		int app_len = 0; // ҵ�������ܳ���
		int recv_len = 0; // ����ҵ�����ݵĳ���

		for (int i = 0; i < recvBuffer.size(); i++) {
			byte temp = recvBuffer.get(i);

			switch (ps) {
			case PARSE_STATE.ASYNC_L:
				if (temp == 0x55) {
					ps++;
				}
				break;

			case PARSE_STATE.ASYNC_H:
				if (temp == -86) { // 0xAA
					ps++; // ��ȡ����ͬ���ֶ�
				} else {
					ps--;// �˻�ͬ��
				}
				break;

			case PARSE_STATE.CHANNEL:
				packet.channel = temp;
				ps++;
				break;

			case PARSE_STATE.FLAG:
				packet.flag = temp;
				ps++;
				break;
			case PARSE_STATE.APP_ID:
				packet.app_id = temp;
				ps++;
				break;
			case PARSE_STATE.SN:
				packet.sn = temp;
				ps++;
				break;
			case PARSE_STATE.APP_LEN_L:
				packet.len_low = temp;
				ps++;
				break;
			case PARSE_STATE.APP_LEN_H:
				packet.len_high = temp;
				app_len = packet.len_low;
				app_len += packet.len_high << 8;
				ps++;
				break;
			case PARSE_STATE.RESERVE:
				packet.reserve = temp;
				ps++;
				break;
			case PARSE_STATE.CRC:
				packet.crc = temp;
				Checkout check = new Checkout();
				if (packet.crc != check.crc8(packet.getpacketHead(), 9)) {
					// ��ͷУ�����
					logger.debug("crc8_check head failed\n");
					for (int j = 0; j < i + 1; j++) {// �Ƴ��Ѿ�������������
						recvBuffer.remove(0);
					}

					// �ص���ʼ����̬
					i = 0;
					ps = PARSE_STATE.ASYNC_L;
				} else {
					if (app_len == 0) {
						bFindPacket = true;// ���û��ҵ�����ݣ�������ͷ�������һ�����ݰ���
					} else {
						ps++;
					}
				}
				break;
			case PARSE_STATE.DATA:
				if (recv_len < app_len) {
					// logger.debug("data:" + temp);
					packet.appBuf[recv_len] = temp;
					recv_len++;
				} else {
					// �����������ݵĵ�һ���ֽ���ҵ��У����
					packet.app_check = temp;
					bFindPacket = true; // ������һ���������ݰ�

					// TODO: У��ҵ������
				}
				break;
			default:
				logger.debug("wangning, unknown data");
				break;
			}// end switch

			// �Ƴ��Ѿ�������������
			if (bFindPacket) {
				for (int j = 0; j < i + 1; j++) {
					recvBuffer.remove(0);
				}

				logger.debug("remove " + (i + 1) + " bytes");

				break;// ����FOR
			}

		}// end for

		if (bFindPacket) {
			// �ѽ�������ҵ������ֱ���ύ��ҵ���
			if (packet.flag == 0x10) {
				logger.debug("*recv data packet:" + packet.sn);

				// ���ݰ���ֱ���ύ��ҵ��
				try {
					if (btControl == null) {
						btControl = BluetoothControl.getInstance();
					}

					if (btControl != null) {
						btControl.savePacket(packet.appBuf, app_len,
								packet.channel);
					}

				} catch (Exception e) {
					logger.debug(e.getMessage());
				}

				// ��Ӧack
				Checkout check = new Checkout();
				// �ظ�ACK���ɼ���
				byte ack[] = new byte[10];
				ack[0] = 0x55;
				ack[1] = (byte) 0xAA;
				ack[2] = packet.channel;
				ack[3] = 0x20;
				ack[4] = 1;
				ack[5] = packet.sn;
				ack[6] = 0;
				ack[7] = 0;
				ack[8] = 0;
				ack[9] = check.crc8(ack, ack.length - 1);

				logger.debug("**send ack:" + packet.sn);
				try {
					BTSocket.getOutputStream().write(ack);
				} catch (Exception e) {
					// TODO: disconnect
					logger.debug("send ack failed! " + e.getMessage());
				}

				return true;
			} else if (packet.flag == 0x20) {
				// ack
				logger.debug("***recv ack:" + packet.sn);
				return true;
			} else if (packet.flag == 0x40) {
				// other packet
				logger.debug("recv disconnect packet");
				isConnected = false;
				Message msg = mainEventHandler.obtainMessage(
						HandlerWhat.Bluetooth2Main, false);
				mainEventHandler.sendMessage(msg);
				business.stopTask(mainEventHandler);
				btControl.setConnectState(false);

				while (true) {
					if (!isConnected()) {
						// ���û�����ӣ�������һ�Σ�����Ϣ30s
						try {
							Thread.sleep(5000);
						} catch (Exception e) {
							logger.debug(e.getMessage());
						}
						// ����Ŀ�������豸
						String strBTAddress = null;// "00:19:5D:24:CB:98";//
													// 00:15:83:15:A3:10
						EcgXmlFileOperate xmlOperate = new EcgXmlFileOperate(
								"Device", ECGApplication.getInstance());
						try {

							strBTAddress = xmlOperate.selectEcgXmlNode(
									"CollectorBtAddr")
									.getParentNodeAttributeValue();
							xmlOperate.close();
						} catch (Exception e) {
							logger.debug("Get BTAddress failed!");
						}
						// ����Ŀ�������豸
						if (strBTAddress == null) {
							logger.debug("The BTAddress is NULL.");
						}
						connectBT(strBTAddress, "1234"); // 00:15:83:15:A3:10
						// ����XML
						// EcgXmlFileOperate xmlOperate = new
						// EcgXmlFileOperate("Device",ECGApplication.getInstance());
						// try {
						// xmlOperate.updateEcgXmlCurrentNode("CollectorBtAddr",
						// strBTAddress);
						// xmlOperate.close();
						// } catch (Exception e) {
						// logger.debug("Write configFile failed ");
						// e.printStackTrace();
						// }

						
						// ����XML
						EcgXmlFileOperate xmlOperate2 = new EcgXmlFileOperate(
								"Device", ECGApplication.getInstance());
						try {
							xmlOperate2.updateEcgXmlCurrentNode(
									"CollectorBtAddr", strBTAddress);
							xmlOperate2.close();
						} catch (Exception e) {
							logger.debug("Write configFile failed ");
							e.printStackTrace();
						}

						nSN = 0;

					} else {
						// ������֪ͨ�Ѿ�������
						Message msgto = mainEventHandler.obtainMessage(
								HandlerWhat.Bluetooth2Main, true);
						mainEventHandler.sendMessage(msgto);

						break;// ������Ͼ�����ѭ��
					}

				}

				return true;
			} else {
				logger.debug("recv other packet");
				return true;
			}
		}

		return false;
	}

	// �˷��ֻ��ҵ���������ݰ�
	public byte[] packet(byte[] data, int len) {
		// ��װ�������
		int length = 11 + len;
		byte[] packetHead = new byte[10];
		byte[] sendBuf = new byte[length];

		Checkout check = new Checkout();

		packetHead[0] = 0x55;
		packetHead[1] = (byte) 0xAA; // ������
		packetHead[2] = 0x00; // ͨ����
		packetHead[3] = 0x10; // Flag
		packetHead[4] = 1; // ҵ������ID
		packetHead[5] = (byte) nSN; // �����
		packetHead[6] = (byte) TypeConversion.intToBytes(len)[0]; // ҵ�����ݳ��ȵ��ֽ�
		packetHead[7] = (byte) TypeConversion.intToBytes(len)[1]; // ���ֽ�
		packetHead[8] = 0; // �����ֽ�
		packetHead[9] = check.crc8(packetHead, packetHead.length - 1); // CRC8У����

		try {
			// ����������ͷ�����ͻ�����
			java.lang.System.arraycopy(packetHead, 0, sendBuf, 0,
					packetHead.length);

			// ����ҵ�����ݵ����ͻ�����
			java.lang.System.arraycopy(data, 0, sendBuf, packetHead.length,
					data.length);

			// ����ҵ������У��
			sendBuf[packetHead.length + data.length] = check.SumCheck(data,
					data.length);

			return sendBuf;
		} catch (Exception e) {
			//
			logger.debug("copy array exception" + e.getMessage());
			return null;
		}
	}

	// ����Ƿ�����
	public synchronized boolean isConnected() {
		return isConnected;
	}

	public void run() {
		Looper.prepare();

		while (true) {
			if (!isConnected()) {
				// ���û�����ӣ�������һ�Σ�����Ϣ30s
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					logger.debug(e.getMessage());
				}

				// ����Ŀ�������豸
				String strBTAddress = null;// "00:19:5D:24:CB:98";//
											// 00:15:83:15:A3:10
				EcgXmlFileOperate xmlOperate = new EcgXmlFileOperate("Device",
						ECGApplication.getInstance());
				try {

					strBTAddress = xmlOperate.selectEcgXmlNode(
							"CollectorBtAddr").getParentNodeAttributeValue();
					xmlOperate.close();
				} catch (Exception e) {
					logger.debug("Get BTAddress failed!");
				}
				// ����Ŀ�������豸
				if (strBTAddress == null) {
					logger.debug("The BTAddress is NULL.");
				}
				connectBT(strBTAddress, "1234"); // 00:15:83:15:A3:10
				// ����XML
				// EcgXmlFileOperate xmlOperate = new
				// EcgXmlFileOperate("Device",ECGApplication.getInstance());
				// try {
				// xmlOperate.updateEcgXmlCurrentNode("CollectorBtAddr",
				// strBTAddress);
				// xmlOperate.close();
				// } catch (Exception e) {
				// logger.debug("Write configFile failed ");
				// e.printStackTrace();
				// }

				nSN = 0;

			} else {
				// ������֪ͨ�Ѿ�������
				Message msg = mainEventHandler.obtainMessage(
						HandlerWhat.Bluetooth2Main, true);
				mainEventHandler.sendMessage(msg);

				break;// ������Ͼ�����ѭ��
			}

		}

		// ֪ͨҵ��ģ��
		try {
			btControl.setConnectState(true);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}

		while (true) {
			if (!isConnected) {
				break;
			}

			// ��ȡ�������ݣ�ֱ�������쳣
			logger.debug("ready to read");
			if (!read()) {
				// log
				isConnected = false;
				logger.debug("read data failed");
				// ������֪ͨ�Ѿ��Ͽ�����
				Message msg = mainEventHandler.obtainMessage(
						HandlerWhat.Bluetooth2Main, false);
				mainEventHandler.sendMessage(msg);
				business.stopTask(mainEventHandler);
				btControl.setConnectState(false);
				break;
			}

			// ��⻺�������������ݣ�ֱ�������������ݰ�
			while (true) {
				try {
					if (!parse()) {
						break;
					}
				} catch (Exception e) {
					logger.debug("parse exception " + e.getMessage());
					break;
				}
			}
		}
	}

	public static class Checkout {
		/**
		 * ��У��
		 * 
		 * @param buf
		 * 
		 * @param len
		 * @return
		 */
		public byte SumCheck(byte[] buf, int len) {
			byte sum_check = 0;
			for (int i = 0; i < len; i++) {
				sum_check ^= buf[i] & 0xff;
			}

			return sum_check;
		}

		/**
		 * crc8У��
		 * 
		 * @param buf
		 * @param len
		 * @return
		 */
		public byte crc8(byte[] buf, int len) {
			byte crc = 0x00;
			int i = 0;
			while (len-- > 0) {
				crc = crc_table[0xff & crc ^ 0xff & buf[i++]];
			}
			return crc;
		}

		byte[] crc_table = new byte[] { (byte) 0x00, (byte) 0x07, (byte) 0x0E,
				(byte) 0x09, (byte) 0x1C, (byte) 0x1B, (byte) 0x12,
				(byte) 0x15, (byte) 0x38, (byte) 0x3F, (byte) 0x36,
				(byte) 0x31, (byte) 0x24, (byte) 0x23, (byte) 0x2A,
				(byte) 0x2D, (byte) 0x70, (byte) 0x77, (byte) 0x7E,
				(byte) 0x79, (byte) 0x6C, (byte) 0x6B, (byte) 0x62,
				(byte) 0x65, (byte) 0x48, (byte) 0x4F, (byte) 0x46,
				(byte) 0x41, (byte) 0x54, (byte) 0x53, (byte) 0x5A,
				(byte) 0x5D, (byte) 0xE0, (byte) 0xE7, (byte) 0xEE,
				(byte) 0xE9, (byte) 0xFC, (byte) 0xFB, (byte) 0xF2,
				(byte) 0xF5, (byte) 0xD8, (byte) 0xDF, (byte) 0xD6,
				(byte) 0xD1, (byte) 0xC4, (byte) 0xC3, (byte) 0xCA,
				(byte) 0xCD, (byte) 0x90, (byte) 0x97, (byte) 0x9E,
				(byte) 0x99, (byte) 0x8C, (byte) 0x8B, (byte) 0x82,
				(byte) 0x85, (byte) 0xA8, (byte) 0xAF, (byte) 0xA6,
				(byte) 0xA1, (byte) 0xB4, (byte) 0xB3, (byte) 0xBA,
				(byte) 0xBD, (byte) 0xC7, (byte) 0xC0, (byte) 0xC9,
				(byte) 0xCE, (byte) 0xDB, (byte) 0xDC, (byte) 0xD5,
				(byte) 0xD2, (byte) 0xFF, (byte) 0xF8, (byte) 0xF1,
				(byte) 0xF6, (byte) 0xE3, (byte) 0xE4, (byte) 0xED,
				(byte) 0xEA, (byte) 0xB7, (byte) 0xB0, (byte) 0xB9,
				(byte) 0xBE, (byte) 0xAB, (byte) 0xAC, (byte) 0xA5,
				(byte) 0xA2, (byte) 0x8F, (byte) 0x88, (byte) 0x81,
				(byte) 0x86, (byte) 0x93, (byte) 0x94, (byte) 0x9D,
				(byte) 0x9A, (byte) 0x27, (byte) 0x20, (byte) 0x29,
				(byte) 0x2E, (byte) 0x3B, (byte) 0x3C, (byte) 0x35,
				(byte) 0x32, (byte) 0x1F, (byte) 0x18, (byte) 0x11,
				(byte) 0x16, (byte) 0x03, (byte) 0x04, (byte) 0x0D,
				(byte) 0x0A, (byte) 0x57, (byte) 0x50, (byte) 0x59,
				(byte) 0x5E, (byte) 0x4B, (byte) 0x4C, (byte) 0x45,
				(byte) 0x42, (byte) 0x6F, (byte) 0x68, (byte) 0x61,
				(byte) 0x66, (byte) 0x73, (byte) 0x74, (byte) 0x7D,
				(byte) 0x7A, (byte) 0x89, (byte) 0x8E, (byte) 0x87,
				(byte) 0x80, (byte) 0x95, (byte) 0x92, (byte) 0x9B,
				(byte) 0x9C, (byte) 0xB1, (byte) 0xB6, (byte) 0xBF,
				(byte) 0xB8, (byte) 0xAD, (byte) 0xAA, (byte) 0xA3,
				(byte) 0xA4, (byte) 0xF9, (byte) 0xFE, (byte) 0xF7,
				(byte) 0xF0, (byte) 0xE5, (byte) 0xE2, (byte) 0xEB,
				(byte) 0xEC, (byte) 0xC1, (byte) 0xC6, (byte) 0xCF,
				(byte) 0xC8, (byte) 0xDD, (byte) 0xDA, (byte) 0xD3,
				(byte) 0xD4, (byte) 0x69, (byte) 0x6E, (byte) 0x67,
				(byte) 0x60, (byte) 0x75, (byte) 0x72, (byte) 0x7B,
				(byte) 0x7C, (byte) 0x51, (byte) 0x56, (byte) 0x5F,
				(byte) 0x58, (byte) 0x4D, (byte) 0x4A, (byte) 0x43,
				(byte) 0x44, (byte) 0x19, (byte) 0x1E, (byte) 0x17,
				(byte) 0x10, (byte) 0x05, (byte) 0x02, (byte) 0x0B,
				(byte) 0x0C, (byte) 0x21, (byte) 0x26, (byte) 0x2F,
				(byte) 0x28, (byte) 0x3D, (byte) 0x3A, (byte) 0x33,
				(byte) 0x34, (byte) 0x4E, (byte) 0x49, (byte) 0x40,
				(byte) 0x47, (byte) 0x52, (byte) 0x55, (byte) 0x5C,
				(byte) 0x5B, (byte) 0x76, (byte) 0x71, (byte) 0x78,
				(byte) 0x7F, (byte) 0x6A, (byte) 0x6D, (byte) 0x64,
				(byte) 0x63, (byte) 0x3E, (byte) 0x39, (byte) 0x30,
				(byte) 0x37, (byte) 0x22, (byte) 0x25, (byte) 0x2C,
				(byte) 0x2B, (byte) 0x06, (byte) 0x01, (byte) 0x08,
				(byte) 0x0F, (byte) 0x1A, (byte) 0x1D, (byte) 0x14,
				(byte) 0x13, (byte) 0xAE, (byte) 0xA9, (byte) 0xA0,
				(byte) 0xA7, (byte) 0xB2, (byte) 0xB5, (byte) 0xBC,
				(byte) 0xBB, (byte) 0x96, (byte) 0x91, (byte) 0x98,
				(byte) 0x9F, (byte) 0x8A, (byte) 0x8D, (byte) 0x84,
				(byte) 0x83, (byte) 0xDE, (byte) 0xD9, (byte) 0xD0,
				(byte) 0xD7, (byte) 0xC2, (byte) 0xC5, (byte) 0xCC,
				(byte) 0xCB, (byte) 0xE6, (byte) 0xE1, (byte) 0xE8,
				(byte) 0xEF, (byte) 0xFA, (byte) 0xFD, (byte) 0xF4, (byte) 0xF3 };

	}
}
