package com.guanri.android.insurance.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 打印定义对象
 * @author 杨雪平
 *
 */
public class InsuPrintBean {
	private PrintDefine printDefine  = null;//第一段落：{打印模板定义表}
	/*
	 * 第二部分：打印控制部分
	 * 打印指令部分（printComanndList）的位置对应走纸命令部分的key,
	 * 如果在走纸命令(paperStepMap)中没有对应的KEY，则表示该打印指令前无需走纸
	 */
	private List<String[]> printComanndList = new ArrayList<String[]>();//打印指令部分
	private Map<Integer,Integer> paperStepMap = new HashMap<Integer,Integer>();//<走纸命令>列表,
	
	public synchronized void addPrintComanndMap(String[] cmd) {
		this.printComanndList.add(cmd);
	}
	public synchronized void addPaperStepMap(Integer id, Integer lines) {
		this.paperStepMap.put(id, lines);
	}
	
	public PrintDefine getPrintDefine() {
		return printDefine;
	}

	public void setPrintDefine(PrintDefine printDefine) {
		this.printDefine = printDefine;
	}

	public List<String[]> getPrintComanndList() {
		return printComanndList;
	}

	public void setPrintComanndList(List<String[]> printComanndList) {
		this.printComanndList = printComanndList;
	}

	public Map<Integer, Integer> getPaperStepMap() {
		return paperStepMap;
	}

	public void setPaperStepMap(Map<Integer, Integer> paperStepMap) {
		this.paperStepMap = paperStepMap;
	}


	/**
	 * 第一段落：{打印模板定义表}
	 * primary key (prntcode) 
	 */
	public static class PrintDefine{
		public String prntcode		= null;//char(8),			--{打印模板代码},
		public String prntname		= null;//char(100),		--{打印模板名称},
		public String blacklabel	= null;//	char(5)	--{黑标的位置},
	}
}
