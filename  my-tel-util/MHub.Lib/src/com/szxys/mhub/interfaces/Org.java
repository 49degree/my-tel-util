package com.szxys.mhub.interfaces;

/**
 * 监护组织，医院或者科室。
 */
public class Org {
	/**
	 * 编码。
	 */
	public int ID;

	/**
	 * 是否是医院。
	 */
	public boolean IsHospital;

	/**
	 * 上级组织编码。
	 */
	public int ParentOrgID;

	/**
	 * 组织名称。
	 */
	public String Name;

	/**
	 * 初始化 Org 对象。
	 */
	public Org() {
	}

	/**
	 * 初始化 Org 对象。
	 * 
	 * @param id
	 *            ：组织编码。
	 * @param isHospital
	 *            ：是否是医院。
	 * @param parentOrgID
	 *            ：上级组织编码。
	 * @param name
	 *            ：组织名称。
	 */
	public Org(int id, boolean isHospital, int parentOrgID, String name) {
		this.ID = id;
		this.IsHospital = isHospital;
		this.ParentOrgID = parentOrgID;
		this.Name = name;
	}
}