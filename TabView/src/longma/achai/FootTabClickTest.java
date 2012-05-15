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
		// 2,初始化我们的测试对象
		tabSolo = new Solo(getInstrumentation(), getActivity());
	}

	@Smoke
	public void testClickEachTab() {
		// 3,获取我们view 中所有的单选按钮,没错就是这么简单…
		ArrayList<RadioButton> radioGroup = tabSolo.getCurrentRadioButtons();
		// 4,自动点击每个单选按钮
		for (int i = 0; i < radioGroup.size(); i++) {
			tabSolo.clickOnRadioButton(i);
			// 5,暂停1s 用来看效果的…

			tabSolo.sleep(1000);
		}
		// 6,又一个强大的功能,直接识别点击当前view中匹配的文本,与控件无关!!!!!!!
		tabSolo.clickOnText("(?i).*?我的收藏.*");
		tabSolo.sleep(1000); // 7,这个是长按,注意留意动画
		tabSolo.clickLongOnText("返回");
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
