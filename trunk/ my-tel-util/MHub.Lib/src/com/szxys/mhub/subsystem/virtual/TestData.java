package com.szxys.mhub.subsystem.virtual;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.util.Log;

import com.szxys.mhub.base.communication.webservice.WebUtils;
import com.szxys.mhub.interfaces.Platform;

public class TestData {
	public static final int testZhangSanUserID = 6;

	/**
	 * 获取用于测试的设备绑定信息。
	 */
	public static byte[] getDeviceBindDataForTest() {
		try {
			String s = "{\"DeviceList\":["
					+ "{\"Desc\":\"心电采集器\",\"DeviceType\":1,\"HeartBeatInterval\":10000,\"Id\":1,\"Mac\":\"00:19:5D:24:00:00\",\"NumOfChannels\":3,\"PairingCode\":\"1234\",\"PassiveMode\":0,\"PhysicalCode\":\"心电采集器1\",\"ProtocolType\":1},"
					+ "{\"Desc\":\"尿量采集器\",\"DeviceType\":2,\"HeartBeatInterval\":10000,\"Id\":2,\"Mac\":\"00:19:5D:24:CC:11\",\"NumOfChannels\":3,\"PairingCode\":\"1234\",\"PassiveMode\":0,\"PhysicalCode\":\"尿量采集器1\",\"ProtocolType\":1}],"
					+ "\"PatientList\":["
					+ "{\"FullName\":\"赵晓敏\",\"MemberID\":\"421024\",\"UserID\":6},"
					+ "{\"FullName\":\"李四\",\"MemberID\":\"lisi\",\"UserID\":2},"
					+ "{\"FullName\":\"梅超风1\",\"MemberID\":\"421023\",\"UserID\":7}]}";

			byte[] data = s.getBytes("UTF-8");
			byte[] retData = new byte[data.length + 4];
			System.arraycopy(data, 0, retData, 4, data.length);
			return retData;
		} catch (Exception e) {
			Log.e("TestData", "Failed to getDeviceBindDataForTest!", e);
			return null;
		}
	}

	/**
	 * 获取用于测试的服务器地址信息。
	 */
	public static byte[] getServerAdressDataForTest() {
		String url;
		byte[] data;
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {
			// 平台URL
			url = "http://172.18.14.31:8888/Services/RpcService.ashx";
			byteStream.write(WebUtils.toLH(Platform.SUBBIZ_VIRTUAL));
			data = url.getBytes("UTF-8");
			byteStream.write(data.length);
			byteStream.write(data);

			// 心电监护URL
			url = "";
			byteStream.write(WebUtils.toLH(Platform.SUBBIZ_ECG));
			data = url.getBytes("UTF-8");
			byteStream.write(data.length);
			byteStream.write(data);

			// 排尿日记URL
			url = "";
			byteStream.write(WebUtils.toLH(Platform.SUBBIZ_METS));
			data = url.getBytes("UTF-8");
			byteStream.write(data.length);
			byteStream.write(data);

			// 尿动力监测URL
			url = "";
			byteStream.write(WebUtils.toLH(Platform.SUBBIZ_UFR));
			data = url.getBytes("UTF-8");
			byteStream.write(data.length);
			byteStream.write(data);

			// 糖尿病随访URL
			url = "";
			byteStream.write(WebUtils.toLH(Platform.SUBBIZ_DMFS));
			data = url.getBytes("UTF-8");
			byteStream.write(data.length);
			byteStream.write(data);

			// 膀胱癌随访URL
			url = "";
			byteStream.write(WebUtils.toLH(Platform.SUBBIZ_PFUS));
			data = url.getBytes("UTF-8");
			byteStream.write(data.length);
			byteStream.write(data);

			// 前列腺炎随访URL
			url = "";
			byteStream.write(WebUtils.toLH(Platform.SUBBIZ_PROSTATITISFU));
			data = url.getBytes("UTF-8");
			byteStream.write(data.length);
			byteStream.write(data);

			// 前列腺增生随访URL
			url = "";
			byteStream.write(WebUtils.toLH(Platform.SUBBIZ_BPHFU));
			data = url.getBytes("UTF-8");
			byteStream.write(data.length);
			byteStream.write(data);

			// 血压监护URL
			url = "";
			byteStream.write(WebUtils.toLH(Platform.SUBBIZ_ABPMS));
			data = url.getBytes("UTF-8");
			byteStream.write(data.length);
			byteStream.write(data);

			// 睡眠监护URL
			url = "";
			byteStream.write(WebUtils.toLH(Platform.SUBBIZ_APS));
			data = url.getBytes("UTF-8");
			byteStream.write(data.length);
			byteStream.write(data);

			// 健康管理URL
			url = "";
			byteStream.write(WebUtils.toLH(Platform.SUBBIZ_HM));
			data = url.getBytes("UTF-8");
			byteStream.write(data.length);
			byteStream.write(data);

			// 健康档案URL
			url = "";
			byteStream.write(WebUtils.toLH(Platform.SUBBIZ_HEALTHRECORD));
			data = url.getBytes("UTF-8");
			byteStream.write(data.length);
			byteStream.write(data);

			// 胎心监护URL
			url = "";
			byteStream.write(WebUtils.toLH(Platform.SUBBIZ_FETALHEART));
			data = url.getBytes("UTF-8");
			byteStream.write(data.length);
			byteStream.write(data);

			// 血糖监护URL
			url = "";
			byteStream.write(WebUtils.toLH(Platform.SUBBIZ_RMBGMS));
			data = url.getBytes("UTF-8");
			byteStream.write(data.length);
			byteStream.write(data);

			byteStream.flush();
			return byteStream.toByteArray();
		} catch (Exception e) {
			Log.e("TestData", "Failed to getServerAdressDataForTest!", e);
			return null;
		} finally {
			try {
				byteStream.close();
			} catch (IOException e) {
				Log.e("TestData", "Failed to close the ByteArrayOutputStream!",
						e);
			}
		}
	}
}
