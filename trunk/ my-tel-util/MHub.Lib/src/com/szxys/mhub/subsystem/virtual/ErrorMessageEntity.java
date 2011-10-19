package com.szxys.mhub.subsystem.virtual;

/**
 * 异常信息实体类
 * 
 * @author 张丹
 * 
 */
public class ErrorMessageEntity {
	private int _id; // ID自增
	private int _appId; // 业务ID
	private String _alarmType; // 报警类型
	private int _alarmLevelId; // 报警级别
	private String _alarmDescription;// 报警描述
	private String _alarmTime; // 报警时间

	public ErrorMessageEntity() {
		super();
	}

	public ErrorMessageEntity(int _id, int _appId, String _alarmType,
			int _alarmLevelId, String _alarmDescription, String _alarmTime) {
		super();
		this._id = _id;
		this._appId = _appId;
		this._alarmType = _alarmType;
		this._alarmLevelId = _alarmLevelId;
		this._alarmDescription = _alarmDescription;
		this._alarmTime = _alarmTime;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int get_appId() {
		return _appId;
	}

	public void set_appId(int _appId) {
		this._appId = _appId;
	}

	public String get_alarmType() {
		return _alarmType;
	}

	public void set_alarmType(String _alarmType) {
		this._alarmType = _alarmType;
	}

	public int get_alarmLevelId() {
		return _alarmLevelId;
	}

	public void set_alarmLevelId(int _alarmLevelId) {
		this._alarmLevelId = _alarmLevelId;
	}

	public String get_alarmDescription() {
		return _alarmDescription;
	}

	public void set_alarmDescription(String _alarmDescription) {
		this._alarmDescription = _alarmDescription;
	}

	public String get_alarmTime() {
		return _alarmTime;
	}

	public void set_alarmTime(String _alarmTime) {
		this._alarmTime = _alarmTime;
	}

}
