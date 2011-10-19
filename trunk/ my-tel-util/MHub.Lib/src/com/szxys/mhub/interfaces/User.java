package com.szxys.mhub.interfaces;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户。
 */
public class User {
	/**
	 * 用户编码。
	 */
	public int ID;

	/**
	 * 用户姓名。
	 */
	public String Name;

	/**
	 * 会员编号。
	 */
	public String MemberID;

	/**
	 * 诊疗卡号。
	 */
	public String TreatmentID;

	/**
	 * 性别：男、女、未知。
	 */
	public String Sex;

	/**
	 * 出生日期。
	 */
	public Date Birthday;

	/**
	 * 婚否：未婚、已婚、不详。
	 */
	public String MaritalStatus;

	/**
	 * 国籍。
	 */
	public String Nationality;

	/**
	 * 地域编码。
	 */
	public int AreaID;

	/**
	 * 民族。
	 */
	public String Nation;

	/**
	 * 籍贯。
	 */
	public String BornPlace;

	/**
	 * 文化程度：小学/初中/高中/大专/本科/研究生/博士。
	 */
	public String Education;

	/**
	 * 证件号码。
	 */
	public String CredNO;

	/**
	 * 证件类型：身份证、护照、驾照、社保号等。
	 */
	public String CredType;

	/**
	 * 家庭住址。
	 */
	public String Address;

	/**
	 * 邮政编码。
	 */
	public String Postalcode;

	/**
	 * 紧急联系人1。
	 */
	public String Urgency1;

	/**
	 * 紧急联系电话1。
	 */
	public String UrgentPhone1;

	/**
	 * 紧急联系人2。
	 */
	public String Urgency2;

	/**
	 * 紧急联系电话2。
	 */
	public String UrgentPhone2;

	/**
	 * 家庭电话。
	 */
	public String Phone;

	/**
	 * 手机号码。
	 */
	public String Mobile;

	/**
	 * 工作电话。
	 */
	public String WorkPhone;

	/**
	 * 电子邮件。
	 */
	public String Email;

	/**
	 * 工作单位。
	 */
	public String WorkOrg;

	/**
	 * 职业。
	 */
	public String Job;

	/**
	 * 创建人。
	 */
	public String Creator;

	/**
	 * 付费方式：现金、医保、其他。
	 */
	public String PayType;

	/**
	 * 注册时间。
	 */
	public Date RegDate;

	/**
	 * 服务开始时间。
	 */
	public Date BeginDate;

	/**
	 * 服务结束时间。
	 */
	public Date EndDate;

	/**
	 * 服务状态：未开通、开通、已结束。
	 */
	public String Status;

	/**
	 * 所有监护医院。
	 */
	public List<Org> Hospitals;

	/**
	 * 所有监护科室。
	 */
	public List<Org> Depts;

	/**
	 * 用户关联的设备类型、编号字典。
	 */
	public Map<Byte, Integer> Devices;
}