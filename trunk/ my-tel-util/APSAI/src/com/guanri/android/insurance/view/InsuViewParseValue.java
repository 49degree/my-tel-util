package com.guanri.android.insurance.view;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.widget.Spinner;
import android.widget.TextView;

import com.guanri.android.insurance.bean.InsuViewPlanBean.InsuPlanContent;
import com.guanri.android.insurance.bean.SaleOrderBean;

/**
 * 解析出单视图中的值
 * @author Administrator
 *
 */
public class InsuViewParseValue {
	private Activity activity = null;
	private LinkedHashMap<String,InsuPlanContent> viewIdInsuPlanContentMap = null;
	private HashMap<String,Integer> attributeTimes = null;
	private HashMap<String, String> attributeValues;// 提交到服务时使用
	private SaleOrderBean saleOrderBean = null;
	private HashMap<String,Integer> viewOrderMap = null;//KEY 表示VIEW ID,VALUE表示该VIEW 添加的顺序，便于在打印模板中使用
	private HashMap<Integer, String> inputValueMap = null;// 重保险销售界面输入的相关值，其中key为界面方案配置文件中的列表顺序
	
	public InsuViewParseValue(Activity activity,LinkedHashMap<String,InsuPlanContent> viewIdInsuPlanContentMap,
			HashMap<String,Integer> attributeTimes,HashMap<String,Integer> viewOrderMap){
		this.activity = activity;
		this.viewIdInsuPlanContentMap = viewIdInsuPlanContentMap;
		this.attributeTimes = attributeTimes;
		this.viewOrderMap = viewOrderMap;
		attributeValues = new HashMap<String,String>();
		saleOrderBean = new SaleOrderBean();
		inputValueMap = new HashMap<Integer,String>();
		parseViewValues();
	}
	

//	private Map<Integer, String> viewIdValues;// 打印的时候使用
//	private Map<Integer,String> otherView;// 保存255多对应控件的ID和控件类型
	/**
	 * 获取BEAN
	 */
	public SaleOrderBean getSaleOrderBean(){
		return saleOrderBean;
	}
	/**
	 * 获取attributeValues
	 * @return
	 */
	public HashMap<String, String> getAttributeValues(){
		return attributeValues;
	}
	
	/**
	 * 获取inputValueMap
	 * @return
	 */
	public HashMap<Integer, String> getInputValueMap(){
		return inputValueMap;
	}
	/**
	 * 获取提交的数据
	 * @return
	 */
	private void parseViewValues() {
		int id = 0;
		for (String attribute:attributeTimes.keySet()) {//遍历已经记录的属性列表，取UI的值
			int count = attributeTimes.get(attribute).intValue();
			try{
				id = Integer.parseInt(attribute);
				id += InsuViewByModeFile.CONTENT_INIT_ID;
				id = Integer.parseInt(String.valueOf(id)+1);
				
				String value = getValueByViewId(attribute,id);
				if(Integer.parseInt(attribute)>50){
					String tempStr = attributeValues.get("255");
					attributeValues.put("255", tempStr==null?"":(tempStr+";")+viewIdInsuPlanContentMap.get(String.valueOf(id)).TSChar+":"+value);
				}else{
					attributeValues.put(attribute, value);
				}
				//取出该ID添加到界面的顺序，即在方案配置文件中的列表顺序
				inputValueMap.put(viewOrderMap.get(String.valueOf(id)),value);
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			//如果同一attribute编号有多个输入项，则去剩余项的值
			if(count>1){
				for(int i=2;i<=count;i++){
					try{
						id = Integer.parseInt(attribute);
						id += InsuViewByModeFile.CONTENT_INIT_ID;
						id = Integer.parseInt(String.valueOf(id)+i);
					}catch(Exception e){
						e.printStackTrace();
					}
					String value = getValueByViewId(attribute,id);
					String tempStr = attributeValues.get("255");
					attributeValues.put("255", tempStr==null?"":(tempStr+";")+viewIdInsuPlanContentMap.get(String.valueOf(id)).TSChar+value);
					//取出该ID添加到界面的顺序，即在方案配置文件中的列表顺序
					inputValueMap.put(viewOrderMap.get(String.valueOf(id)),value);
				}
			}
		}
		parseSaleOrderBean(attributeValues);
	}
	
	/**
	 * 获取提交的数据
	 * @return
	 */
	private void parseSaleOrderBean(Map<String, String> attributeValues) {
		
		for(String artribute:attributeValues.keySet()){
			int artributeId = Integer.parseInt(artribute);
			String value = attributeValues.get(artribute);
			switch(artributeId){
			//以下为简单输入框
			case 1: //1.	单证号
				saleOrderBean.setBillNo(value);
				break;
			case 2:   //2.	保险起期(格式固定为YYYYMMDD)	
				saleOrderBean.setInsu_begin_date(value);
				break;
			case 3:   //3.	保险起期时间(格式固定为HH[MM[SS]])
				saleOrderBean.setInsu_begin_time(value);
				break;
			case 4:   //4.	保险止期(格式固定为YYYYMMDD)	
				saleOrderBean.setInsu_end_date(value);
				break;
			case 5:   //5.	保险止期时间(格式固定为HH[MM[SS]])
				saleOrderBean.setInsu_end_time(value);
				break;				
			case 6:   //6.	投保人	
				saleOrderBean.setPlyh_name(value);
				break;
			case 7:   //7.	投保人性别	
				saleOrderBean.setPlyh_sex(Integer.parseInt(value));
				break;
			case 8:   //8.	投保人生日
				saleOrderBean.setPlyh_Brithday(value);
				break;
			case 9:   //9.	投保人证件类型
				saleOrderBean.setPlyh_Card_type(Integer.parseInt(value));
				break;
			case 10:  //10.	投保人证件号码	
				saleOrderBean.setPlyh_Card_no(value);
				break;
			case 11:  //11.	被保人
				saleOrderBean.setInsured_name(value);
				break;
			case 12:  //12.	被保人性别
				saleOrderBean.setInsured_Sex(Integer.parseInt(value));
				break;
			case 13:  //13.	被保人生日
				saleOrderBean.setInsured_brithday(value);
				break;			
			case 14:  //14.	被保人证件类型
				saleOrderBean.setInsured_card_type(Integer.parseInt(value));
				break;
			case 15:  //15.	被保人证件号码
				saleOrderBean.setInsured_card_No(value);
				break;			
			case 16:  //16.	投保人和被保人关系
				saleOrderBean.setInsured_relation(Integer.parseInt(value));
				break;				
			case 17:  // 受益人
				saleOrderBean.setBeneficlary_name(value);
				break;		
			case 18:  // 18. 保险保费（单位为元，可以输入两位小数  交的多少）
				try{
					saleOrderBean.setInsured_amount((int)Float.parseFloat(value)*100);
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			case 19:  // 19. 保险金额（单位为元   赔偿多少）
				try{
					saleOrderBean.setInsured_money((int)Float.parseFloat(value)*100);
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			case 20:  // 20. 学校
				saleOrderBean.setSchool(value);
				break;
			case 21:  // 21. 班级
				saleOrderBean.setSchoolClass(value);
				break;
			case 22: // 22. 航班号/客运班次
				saleOrderBean.setTrainnumber(value);
				break;
			
			case 23:// 23. 客票/门票号码，卡号等
				saleOrderBean.setTrainticket(value);
				break;
			case 49:// 49. 组合
				saleOrderBean.setInsuAssembled(Integer.parseInt(value));
				break;
			
			case 255: //attribute>=50以后的值都是放在其他当中, 255. 其他
				saleOrderBean.setRemark(value);
				break;
			default:
				break;
			}
		}
		

	}
	
	/**
	 * 根据属性编号和VIEW编号获取值
	 * @param attribute
	 * @param id
	 * @return
	 */
	private String getValueByViewId(String attribute,int id){
		String value = null;

		if(InsuViewByModeFile.spinnerAttributeSet.contains(attribute)){//如果是选择项
			//只有当attribute!=255或者attribute=255同时InputEnAble=128时
			if(!"255".equals(attribute)||"128".equals(viewIdInsuPlanContentMap.get(String.valueOf(id)).InputEnAble)){
				Spinner spinner = (Spinner)activity.findViewById(id);
				value = spinner.getSelectedItem().toString();
				value = findValueFromSpinner(value,viewIdInsuPlanContentMap.get(String.valueOf(id)).HelpChar);
			}else{
				TextView v = (TextView)activity.findViewById(id);
				value = v.getText().toString();
			}
		}else{
			
			TextView v = (TextView)activity.findViewById(id);
			value = v.getText().toString();
		}
		return value;
	}
	
	/**
	 * 通过选中的字符串查找 字符串对应的ID
	 * 在  1="男";2="女" 中查找 "男"---->1
	 * @param ID
	 * @param string
	 * @return
	 */
	private String findValueFromSpinner(String choice, String spinnerStr) {
		// TODO Auto-generated method stub
		int index = -1,j=0;
		String resultstr = "";
		if (spinnerStr!=null&&(index = spinnerStr.indexOf(choice)) != -1) {
			String tempstr = spinnerStr.substring(0, index-1);
			if ((j = tempstr.lastIndexOf(";")) > -1) {
				resultstr = tempstr.substring(j + 1);
			} else {
				resultstr = tempstr;
			}
		}
		return resultstr;
	}
	
}
