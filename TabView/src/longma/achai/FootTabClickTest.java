package longma.achai;

import java.util.ArrayList;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.RadioButton;

import com.jayway.android.robotium.solo.Solo;


@SuppressWarnings("rawtypes")
public class FootTabClickTest extends ActivityInstrumentationTestCase2 {
	public Solo solo;
	public Activity activity;
	private static Class<?> launchActivityClass;
	private Solo tabSolo;
	static {
		try {
			launchActivityClass = Class.forName("com.swftest.TestActivity");
		} catch (ClassNotFoundException e) {

			throw new RuntimeException(e);

		}

	}

	@SuppressWarnings("unchecked")
	public FootTabClickTest() {
		super("com.swftest", launchActivityClass);
	}

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		// 2,��ʼ�����ǵĲ��Զ���
		tabSolo = new Solo(getInstrumentation(), getActivity());
	}

	@Smoke
	public void testClickEachTab() {
		// 3,��ȡ����view �����еĵ�ѡ��ť,û�������ô�򵥡�
		ArrayList<RadioButton> radioGroup = tabSolo.getCurrentRadioButtons();
		// 4,�Զ����ÿ����ѡ��ť
		for (int i = 0; i < radioGroup.size(); i++) {
			tabSolo.clickOnRadioButton(i);
			// 5,��ͣ1s ������Ч���ġ�

			tabSolo.sleep(1000);
		}
		// 6,��һ��ǿ��Ĺ���,ֱ��ʶ������ǰview��ƥ����ı�,��ؼ��޹�!!!!!!!
		tabSolo.clickOnText("(?i).*?�ҵ��ղ�.*");
		tabSolo.sleep(1000); // 7,����ǳ���,ע�����⶯��
		tabSolo.clickLongOnText("����");
		tabSolo.sleep(2000);
		// tabSolo.clickOnRadioButton(1);
	}

	@Override
	public void tearDown() throws Exception {
		try {
			this.solo.finishOpenedActivities();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		this.activity.finish();

		super.tearDown();

	}

}
