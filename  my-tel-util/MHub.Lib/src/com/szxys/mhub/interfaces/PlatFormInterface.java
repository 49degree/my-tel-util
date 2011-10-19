package com.szxys.mhub.interfaces;

/**
 * 获取平台接口操作对象
 * 
 * @author 黄仕龙
 * 
 */
public class PlatFormInterface {
	static public IPlatFormInterface getIPlatFormInterface() {
		return PlatFormInterfaceImpl.instance();
	}
}
