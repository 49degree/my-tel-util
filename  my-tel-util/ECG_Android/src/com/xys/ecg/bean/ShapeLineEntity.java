package com.xys.ecg.bean;

public class ShapeLineEntity {
	EcgDataEntity ecgDataEntity = null;//心电数据包
	int ecgTate;//心率值
	public ShapeLineEntity(){
		
	}
	public ShapeLineEntity(EcgDataEntity ecgDataEntity,int ecgTate){
		this.ecgDataEntity = ecgDataEntity;
		this.ecgTate = ecgTate;
	}
	public EcgDataEntity getEcgDataEntity() {
		return ecgDataEntity;
	}
	public void setEcgDataEntity(EcgDataEntity ecgDataEntity) {
		this.ecgDataEntity = ecgDataEntity;
	}
	public int getEcgTate() {
		return ecgTate;
	}
	public void setEcgTate(int ecgTate) {
		this.ecgTate = ecgTate;
	}


}
