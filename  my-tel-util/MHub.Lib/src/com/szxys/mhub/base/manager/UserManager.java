package com.szxys.mhub.base.manager;

import java.io.StringReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.stream.JsonReader;
import com.szxys.mhub.interfaces.LightUser;
import com.szxys.mhub.interfaces.Org;
import com.szxys.mhub.interfaces.User;

import android.database.Cursor;
import android.util.Log;

public class UserManager {
	/**
	 * 用户管理构造函数。
	 */
	private UserManager() {
	}

	/**
	 * 获取所有用户的轻量级信息。
	 */
	public static Map<Integer, LightUser> getAllLightUsers() {
		Map<Integer, LightUser> map = null;
		MHubDBHelper helper = null;
		Cursor cur = null;
		try {
			helper = new MHubDBHelper();
			helper.open(true);
			cur = helper.query("select ID, Name, MemberID from "
					+ DBConstDef.TABLE_USER);
			if (cur != null && cur.getCount() > 0) {
				map = new HashMap<Integer, LightUser>();
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					int userId = cur.getInt(cur.getColumnIndex("ID"));
					map.put(userId,
							new LightUser(userId, cur.getString(cur
									.getColumnIndex("Name")), cur.getString(cur
									.getColumnIndex("MemberID")),
									getUserDevices(helper, userId)));
				}
			}
		} catch (Exception e) {
			Log.e("UserManager", "Failed to getAllLightUser!", e);
			map = null;
		} finally {
			if (cur != null) {
				cur.close();
			}
			if (helper != null) {
				helper.close();
			}
		}
		return map;
	}

	/**
	 * 获取指定用户的所有信息。
	 * 
	 * @param userId
	 *            ：用户编码。
	 */
	public static User getUser(int userId) {
		User user = null;
		MHubDBHelper helper = null;
		try {
			helper = new MHubDBHelper();
			helper.open(true);

			Cursor cur = null;
			try {
				cur = helper.query("select * from " + DBConstDef.TABLE_USER
						+ " where ID = " + userId);
				if (cur != null && cur.getCount() == 1) {
					cur.moveToFirst();
					user = new User();
					user.ID = cur.getInt(cur.getColumnIndex("ID"));
					user.Name = cur.getString(cur.getColumnIndex("Name"));
					user.MemberID = cur.getString(cur
							.getColumnIndex("MemberID"));
					user.TreatmentID = cur.getString(cur
							.getColumnIndex("TreatmentID"));
					user.Sex = cur.getString(cur.getColumnIndex("Sex"));

					String birthday = cur.getString(cur
							.getColumnIndex("Birthday"));
					if (birthday != null && !birthday.equals("null")) {
						user.Birthday = Date.valueOf(birthday);
					}

					user.MaritalStatus = cur.getString(cur
							.getColumnIndex("MaritalStatus"));
					user.Nationality = cur.getString(cur
							.getColumnIndex("Nationality"));
					user.AreaID = cur.getInt(cur.getColumnIndex("AreaID"));
					user.Nation = cur.getString(cur.getColumnIndex("Nation"));
					user.BornPlace = cur.getString(cur
							.getColumnIndex("BornPlace"));
					user.Education = cur.getString(cur
							.getColumnIndex("Education"));
					user.CredNO = cur.getString(cur.getColumnIndex("CredNO"));
					user.CredType = cur.getString(cur
							.getColumnIndex("CredType"));
					user.Address = cur.getString(cur.getColumnIndex("Address"));
					user.Postalcode = cur.getString(cur
							.getColumnIndex("Postalcode"));
					user.Urgency1 = cur.getString(cur
							.getColumnIndex("Urgency1"));
					user.UrgentPhone1 = cur.getString(cur
							.getColumnIndex("UrgentPhone1"));
					user.Urgency2 = cur.getString(cur
							.getColumnIndex("Urgency2"));
					user.UrgentPhone2 = cur.getString(cur
							.getColumnIndex("UrgentPhone2"));
					user.Phone = cur.getString(cur.getColumnIndex("Phone"));
					user.Mobile = cur.getString(cur.getColumnIndex("Mobile"));
					user.WorkPhone = cur.getString(cur
							.getColumnIndex("WorkPhone"));
					user.Email = cur.getString(cur.getColumnIndex("Email"));
					user.WorkOrg = cur.getString(cur.getColumnIndex("WorkOrg"));
					user.Job = cur.getString(cur.getColumnIndex("Job"));
					user.Creator = cur.getString(cur.getColumnIndex("Creator"));
					user.PayType = cur.getString(cur.getColumnIndex("PayType"));

					String regDate = cur.getString(cur
							.getColumnIndex("RegDate"));
					if (regDate != null && !regDate.equals("null")) {
						user.RegDate = Date.valueOf(regDate);
					}

					String beginDate = cur.getString(cur
							.getColumnIndex("BeginDate"));
					if (beginDate != null && !beginDate.equals("null")) {
						user.BeginDate = Date.valueOf(beginDate);
					}

					String endDate = cur.getString(cur
							.getColumnIndex("EndDate"));
					if (endDate != null && !endDate.equals("null")) {
						user.EndDate = Date.valueOf(endDate);
					}

					user.Status = cur.getString(cur.getColumnIndex("Status"));
					user.Devices = getUserDevices(helper, userId);
				}
			} catch (Exception ex) {
				Log.e("UserManager",
						"Failed to get the user's basic information!", ex);
				user = null;
			} finally {
				if (cur != null) {
					cur.close();
				}
			}

			if (user != null) {
				List<Org> orgs = getOrgs(helper, userId);
				if (orgs != null) {
					ArrayList<Org> hospitalList = new ArrayList<Org>();
					ArrayList<Org> deptList = new ArrayList<Org>();
					for (Org org : orgs) {
						if (org.IsHospital) {
							hospitalList.add(org);
						} else {
							deptList.add(org);
						}
					}
					user.Hospitals = hospitalList;
					user.Depts = deptList;

					hospitalList = null;
					deptList = null;
					orgs = null;
				}
			}
		} catch (Exception e) {
			Log.e("UserManager",
					"Failed to get the user's organization information!", e);
			user = null;
		} finally {
			if (helper != null) {
				helper.close();
			}
		}
		return user;
	}

	/**
	 * 获取指定组织的信息。
	 * 
	 * @param orgId
	 *            ：组织编码。
	 */
	public static Org getOrg(int orgId) {
		Org org = null;
		MHubDBHelper helper = null;
		Cursor cur = null;
		try {
			helper = new MHubDBHelper();
			helper.open(true);
			cur = helper.query("select * from " + DBConstDef.TABLE_ORGANIZATION
					+ " where ID = " + orgId);
			if (cur != null && cur.getCount() == 1) {
				cur.moveToFirst();
				org = new Org(
						cur.getInt(cur.getColumnIndex("ID")),
						cur.getInt(cur.getColumnIndex("IsHospital")) == 1 ? true
								: false, cur.getInt(cur
								.getColumnIndex("ParentOrgID")),
						cur.getString(cur.getColumnIndex("Name")));
			}
		} catch (Exception e) {
			Log.e("UserManager", "Failed to getOrg!", e);
			org = null;
		} finally {
			if (cur != null) {
				cur.close();
			}
			if (helper != null) {
				helper.close();
			}
		}
		return org;
	}

	/**
	 * 添加轻量级用户。
	 * 
	 * @param lightUser
	 *            ：轻量级用户
	 */
	public static boolean addLightUser(LightUser lightUser) {
		MHubDBHelper helper = null;
		try {
			helper = new MHubDBHelper();
			helper.open(false);
			return addLightUserInner(helper, lightUser);
		} catch (Exception e) {
			Log.e("UserManager", "Failed to addLightUser!", e);
			return false;
		} finally {
			if (helper != null) {
				helper.close();
			}
		}
	}

	/**
	 * 保存用户信息。
	 * 
	 * @param data
	 *            ：数据信息。
	 * @param length
	 *            ：数据长度。
	 */
	public static boolean saveUser(byte[] data, int length) {
		try {
			// 数据异常
			if (length < 4) {
				return false;
			}

			byte[] tmpData = new byte[length - 4];
			System.arraycopy(data, 4, tmpData, 0, tmpData.length);
			String userInfo = new String(tmpData, "UTF-8");

			User user = new User();
			user.Depts = new ArrayList<Org>();
			user.Hospitals = new ArrayList<Org>();

			JsonReader reader = new JsonReader(new StringReader(userInfo));
			reader.beginObject();
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equals("Address")) {
					user.Address = reader.nextString();
				} else if (tagName.equals("AreaID")) {
					user.AreaID = reader.nextInt();
				} else if (tagName.equals("BeginDate")) {
					String dateString = reader.nextString();
					if (!dateString.equals("")) {
						user.BeginDate = Date.valueOf(dateString);
					}
				} else if (tagName.equals("Birthday")) {
					String dateString = reader.nextString();
					if (!dateString.equals("")) {
						user.Birthday = Date.valueOf(dateString);
					}
				} else if (tagName.equals("BornPlace")) {
					user.BornPlace = reader.nextString();
				} else if (tagName.equals("Creator")) {
					user.Creator = reader.nextString();
				} else if (tagName.equals("CredNO")) {
					user.CredNO = reader.nextString();
				} else if (tagName.equals("CredType")) {
					user.CredType = reader.nextString();
				} else if (tagName.equals("Education")) {
					user.Education = reader.nextString();
				} else if (tagName.equals("Email")) {
					user.Email = reader.nextString();
				} else if (tagName.equals("EndDate")) {
					String dateString = reader.nextString();
					if (!dateString.equals("")) {
						user.EndDate = Date.valueOf(dateString);
					}
				} else if (tagName.equals("Job")) {
					user.Job = reader.nextString();
				} else if (tagName.equals("MaritalStatus")) {
					user.MaritalStatus = reader.nextString();
				} else if (tagName.equals("MemberID")) {
					user.MemberID = reader.nextString();
				} else if (tagName.equals("Mobile")) {
					user.Mobile = reader.nextString();
				} else if (tagName.equals("Nation")) {
					user.Nation = reader.nextString();
				} else if (tagName.equals("Nationality")) {
					user.Nationality = reader.nextString();
				} else if (tagName.equals("PatientID")) {
					user.ID = reader.nextInt();
				} else if (tagName.equals("PatientName")) {
					user.Name = reader.nextString();
				} else if (tagName.equals("PatientSex")) {
					user.Sex = reader.nextString();
				} else if (tagName.equals("PayType")) {
					user.PayType = reader.nextString();
				} else if (tagName.equals("Phone")) {
					user.Phone = reader.nextString();
				} else if (tagName.equals("Postalcode")) {
					user.Postalcode = reader.nextString();
				} else if (tagName.equals("RegDate")) {
					String dateString = reader.nextString();
					if (!dateString.equals("")) {
						user.RegDate = Date.valueOf(dateString);
					}
				} else if (tagName.equals("Status")) {
					user.Status = reader.nextString();
				} else if (tagName.equals("TreatmentID")) {
					user.TreatmentID = reader.nextString();
				} else if (tagName.equals("Urgency1")) {
					user.Urgency1 = reader.nextString();
				} else if (tagName.equals("Urgency2")) {
					user.Urgency2 = reader.nextString();
				} else if (tagName.equals("UrgentPhone1")) {
					user.UrgentPhone1 = reader.nextString();
				} else if (tagName.equals("UrgentPhone2")) {
					user.UrgentPhone2 = reader.nextString();
				} else if (tagName.equals("WorkOrg")) {
					user.WorkOrg = reader.nextString();
				} else if (tagName.equals("WorkPhone")) {
					user.WorkPhone = reader.nextString();
				} else if (tagName.equals("Depts")) {
					reader.beginArray();
					while (reader.hasNext()) {
						reader.beginObject();
						Org org = new Org();
						while (reader.hasNext()) {
							String name = reader.nextName();
							if (name.equals("OrgID")) {
								org.ID = reader.nextInt();
							} else if (name.equals("OrgName")) {
								org.Name = reader.nextString();
							} else if (name.equals("ParentOrgID")) {
								org.ParentOrgID = reader.nextInt();
							}
						}
						org.IsHospital = false;
						user.Depts.add(org);
						reader.endObject();
					}
					reader.endArray();
				} else if (tagName.equals("Orgs")) {
					reader.beginArray();
					while (reader.hasNext()) {
						reader.beginObject();
						Org org = new Org();
						while (reader.hasNext()) {
							String name = reader.nextName();
							if (name.equals("OrgID")) {
								org.ID = reader.nextInt();
							} else if (name.equals("OrgName")) {
								org.Name = reader.nextString();
							} else if (name.equals("ParentOrgID")) {
								org.ParentOrgID = reader.nextInt();
							}
						}
						org.IsHospital = true;
						user.Hospitals.add(org);
						reader.endObject();
					}
					reader.endArray();
				}
			}
			reader.endObject();

			return updateUser(user);
		} catch (Exception e) {
			Log.e("UserManager", "Failed to saveUser!", e);
			return false;
		}
	}

	/**
	 * 更新用户信息。
	 * 
	 * @param user
	 *            ：用户对象
	 */
	public static boolean updateUser(User user) {
		MHubDBHelper helper = null;
		try {
			if (user != null) {
				helper = new MHubDBHelper();
				helper.open(false);
				try {
					helper.execSQL("update " + DBConstDef.TABLE_USER + " set "
							+ " ID = " + user.ID + ", Name = '" + user.Name
							+ "', MemberID = '" + user.MemberID
							+ "', TreatmentID = '" + user.TreatmentID
							+ "', Sex = '" + user.Sex + "', Birthday = '"
							+ user.Birthday + "', MaritalStatus = '"
							+ user.MaritalStatus + "', Nationality = '"
							+ user.Nationality + "', AreaID = " + user.AreaID
							+ ", Nation = '" + user.Nation + "', BornPlace = '"
							+ user.BornPlace + "', Education = '"
							+ user.Education + "', CredNO = '" + user.CredNO
							+ "', CredType = '" + user.CredType
							+ "', Address = '" + user.Address
							+ "', Postalcode = '" + user.Postalcode
							+ "', Urgency1 = '" + user.Urgency1
							+ "', UrgentPhone1 = '" + user.UrgentPhone1
							+ "', Urgency2 = '" + user.Urgency2
							+ "', UrgentPhone2 = '" + user.UrgentPhone2
							+ "', Phone = '" + user.Phone + "', Mobile = '"
							+ user.Mobile + "', WorkPhone = '" + user.WorkPhone
							+ "', Email = '" + user.Email + "', WorkOrg = '"
							+ user.WorkOrg + "', Job = '" + user.Job
							+ "', Creator = '" + user.Creator
							+ "', PayType = '" + user.PayType
							+ "', RegDate = '" + user.RegDate
							+ "', BeginDate = '" + user.BeginDate
							+ "', EndDate = '" + user.EndDate + "', Status = '"
							+ user.Status + "' where ID = " + user.ID);
				} catch (Exception ex) {
					Log.e("UserManager",
							"Failed to update the user's basic information!",
							ex);
				}

				deleteUserOrgRelational(helper, user.ID);
				if (user.Hospitals != null) {
					for (Org org : user.Hospitals) {
						helper.execSQL("replace into "
								+ DBConstDef.TABLE_ORGANIZATION
								+ " (ID, IsHospital, ParentOrgID, Name) values("
								+ org.ID + ", " + (org.IsHospital ? 1 : 0)
								+ ", " + org.ParentOrgID + ", '" + org.Name
								+ "')");
						helper.execSQL("insert into "
								+ DBConstDef.TABLE_USER_ORG_RELATIONAL
								+ " (UserID, OrgID) values(" + user.ID + ", "
								+ org.ID + ")");
					}
				}
				if (user.Depts != null) {
					for (Org org : user.Depts) {
						helper.execSQL("replace into "
								+ DBConstDef.TABLE_ORGANIZATION
								+ " (ID, IsHospital, ParentOrgID, Name) values("
								+ org.ID + ", " + (org.IsHospital ? 1 : 0)
								+ ", " + org.ParentOrgID + ", '" + org.Name
								+ "')");
						helper.execSQL("insert into "
								+ DBConstDef.TABLE_USER_ORG_RELATIONAL
								+ " (UserID, OrgID) values(" + user.ID + ", "
								+ org.ID + ")");
					}
				}
				return true;
			}
		} catch (Exception e) {
			Log.e("UserManager",
					"Failed to update the user's organization information!", e);
		} finally {
			if (helper != null) {
				helper.close();
			}
		}
		return false;
	}

	/**
	 * 删除用户。
	 * 
	 * @param userId
	 *            ：要删除的用户ID
	 */
	public static boolean removeUser(int userId) {
		MHubDBHelper helper = null;
		try {
			helper = new MHubDBHelper();
			helper.open(false);
			helper.execSQL("delete from " + DBConstDef.TABLE_USER
					+ " where ID = " + userId);
			return true;
		} catch (Exception e) {
			Log.e("UserManager", "Failed to removeUser!", e);
			return false;
		} finally {
			if (helper != null) {
				helper.close();
			}
		}
	}

	/**
	 * 获取指定用户的所有监护组织。
	 * 
	 * @param userId
	 *            ：用户编码。
	 */
	static List<Org> getUserOrgs(int userId) {
		List<Org> list = null;
		MHubDBHelper helper = null;
		try {
			helper = new MHubDBHelper();
			helper.open(true);
			list = getOrgs(helper, userId);
		} catch (Exception e) {
			Log.e("UserManager", "Failed to getUserOrgs!", e);
			list = null;
		} finally {
			if (helper != null) {
				helper.close();
			}
		}
		return list;
	}

	/**
	 * 通过 DB 操作对象获取指定用户的所有采集器。
	 * 
	 * @param helper
	 *            ：DB操作对象。
	 * @param userId
	 *            ：用户编码。
	 */
	static Map<Byte, Integer> getUserDevices(MHubDBHelper helper, int userId) {
		Cursor cur = null;
		Map<Byte, Integer> devices = new HashMap<Byte, Integer>();
		try {
			cur = helper.query("select DeviceType, CollectorID from "
					+ DBConstDef.TABLE_USER_COLLECTOR_RELATIONAL
					+ " where UserID = " + userId);
			if (cur != null && cur.getCount() > 0) {
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					byte deviceType = (byte) cur.getInt(cur
							.getColumnIndex("DeviceType"));
					int collectorId = cur.getInt(cur
							.getColumnIndex("CollectorID"));
					devices.put(deviceType, collectorId);
				}
			}
		} catch (Exception e) {
			Log.e("UserManager", "Failed to get user's devices!", e);
		} finally {
			if (cur != null) {
				cur.close();
			}
		}

		return devices;
	}

	/**
	 * 添加轻量级用户内部方法。
	 * 
	 * @param helper
	 *            ：DB操作对象
	 * @param lightUser
	 *            ：轻量级用户
	 */
	static boolean addLightUserInner(MHubDBHelper helper, LightUser lightUser) {
		try {
			helper.execSQL("replace into " + DBConstDef.TABLE_USER
					+ " (ID, Name, MemberID) values(" + lightUser.ID + ", '"
					+ lightUser.Name + "', '" + lightUser.MemberId + "')");
			return true;
		} catch (Exception e) {
			Log.e("UserManager", "Failed to addLightUser!", e);
			return false;
		}
	}

	/**
	 * 添加用户、采集器关联信息。
	 * 
	 * @param helper
	 *            ：DB操作对象
	 * @param userId
	 *            ：用户ID
	 * @param deviceType
	 *            ：采集器类型
	 * @param collectorId
	 *            ：采集器ID
	 */
	static boolean addUserCollectorRelational(MHubDBHelper helper, int userId,
			byte deviceType, int collectorId) {
		try {
			helper.execSQL("replace into "
					+ DBConstDef.TABLE_USER_COLLECTOR_RELATIONAL
					+ " (UserID, DeviceType, CollectorID) values(" + userId
					+ ", " + deviceType + ", " + collectorId + ")");
			return true;
		} catch (Exception e) {
			Log.e("UserManager", "Failed to addUserCollectorRelational!", e);
			return false;
		}
	}

	/**
	 * 通过 DB 操作对象删除指定用户的所有监护组织关系。
	 * 
	 * @param helper
	 *            ：DB操作对象。
	 * @param userId
	 *            ：用户编码。
	 */
	private static void deleteUserOrgRelational(MHubDBHelper helper, int userId) {
		try {
			helper.execSQL("delete from "
					+ DBConstDef.TABLE_USER_ORG_RELATIONAL + " where UserID = "
					+ userId);
		} catch (Exception e) {
			Log.e("UserManager", "Failed to deleteUserOrgRelational!", e);
		}
	}

	/**
	 * 通过 DB 操作对象获取指定用户的所有监护组织。
	 * 
	 * @param helper
	 *            ：DB操作对象。
	 * @param userId
	 *            ：用户编码。
	 */
	private static List<Org> getOrgs(MHubDBHelper helper, int userId) {
		List<Org> list = null;
		Cursor cur = null;
		try {
			cur = helper.query("select ID, IsHospital, ParentOrgID, Name from "
					+ DBConstDef.TABLE_ORGANIZATION
					+ " where ID in (select OrgID from "
					+ DBConstDef.TABLE_USER_ORG_RELATIONAL + " where UserID = "
					+ userId + ")");
			if (cur != null && cur.getCount() > 0) {
				list = new ArrayList<Org>();
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					list.add(new Org(
							cur.getInt(cur.getColumnIndex("ID")),
							cur.getInt(cur.getColumnIndex("IsHospital")) == 1 ? true
									: false, cur.getInt(cur
									.getColumnIndex("ParentOrgID")), cur
									.getString(cur.getColumnIndex("Name"))));
				}
			}
		} catch (Exception e) {
			Log.e("UserManager", "Failed to getOrgs(private)!", e);
			list = null;
		} finally {
			if (cur != null) {
				cur.close();
			}
		}

		return list;
	}
}