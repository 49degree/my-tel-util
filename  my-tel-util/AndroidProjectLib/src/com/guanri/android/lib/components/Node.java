package com.guanri.android.lib.components;

import java.util.List;

public class Node {
	//包含子控件集合
	private List<Node> childNodes;
	
	//当前控件属性保存bean
	private ViewAttribute viewAttribute;
	
	public List<Node> getChildNodes() {
		return childNodes;
	}
	public void setChildNodes(List<Node> childNodes) {
		this.childNodes = childNodes;
	}
	public void setViewAttribute(ViewAttribute viewAttribute) {
		this.viewAttribute = viewAttribute;
	}
	public ViewAttribute getViewAttribute() {
		return viewAttribute;
	}
}
