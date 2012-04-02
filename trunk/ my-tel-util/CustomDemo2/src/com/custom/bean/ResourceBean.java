package com.custom.bean;

import java.util.List;

import android.graphics.Bitmap;

import com.custom.utils.Constant.DirType;

public class ResourceBean {
	public enum ResourceType{
		fold,apk,swf,pic,txt,pdf,flv,doc,xls,ppt
	}
	public static class ResourceRaws{
		String rawPath = null;
		ResourceType type = null;
		DirType dirType = null;
		
		
		public ResourceRaws(String rawPath, ResourceType type, DirType dirType) {
			super();
			this.rawPath = rawPath;
			this.type = type;
			this.dirType = dirType;
		}
		public String getRawPath() {
			return rawPath;
		}
		public void setRawPath(String rawPath) {
			this.rawPath = rawPath;
		}
		public ResourceType getType() {
			return type;
		}
		public void setType(ResourceType type) {
			this.type = type;
		}
		public DirType getDirType() {
			return dirType;
		}
		public void setDirType(DirType dirType) {
			this.dirType = dirType;
		}
		
		
	}
	
	String btnKey = null;
	String btnPic = null;
	String name = null;
	String foldPath = null;
	int foldDepth = 0;
	int x;
	int y;
	List<ResourceRaws> raws = null;
	DirType dirType = DirType.assets;
	Bitmap bm = null;
	
	
	

	public String getBtnKey() {
		return btnKey;
	}
	public void setBtnKey(String btnKey) {
		this.btnKey = btnKey;
	}
	public String getBtnPic() {
		return btnPic;
	}
	public void setBtnPic(String btnPic) {
		this.btnPic = btnPic;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFoldPath() {
		return foldPath;
	}
	public void setFoldPath(String foldPath) {
		this.foldPath = foldPath;
	}
	public int getFoldDepth() {
		return foldDepth;
	}
	public void setFoldDepth(int foldDepth) {
		this.foldDepth = foldDepth;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public List<ResourceRaws> getRaws() {
		return raws;
	}
	public void setRaws(List<ResourceRaws> raws) {
		this.raws = raws;
	}
	public DirType getDirType() {
		return dirType;
	}
	public void setDirType(DirType dirType) {
		this.dirType = dirType;
	}
	public Bitmap getBm() {
		return bm;
	}
	public void setBm(Bitmap bm) {
		this.bm = bm;
	}

	
	
}
