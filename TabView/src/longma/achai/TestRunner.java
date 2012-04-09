package longma.achai;

import junit.framework.Test;
import junit.framework.TestListener;
import junit.framework.TestSuite;
import android.app.Activity;
import android.test.AndroidTestRunner;
import android.test.AssertionFailedError;
import android.util.Log;

public class TestRunner implements Runnable, TestListener

{

	static final String LOG_TAG = "TestRunner";

	Activity parentActivity;

	public TestRunner(Activity parentActivity)

	{

		this.parentActivity = parentActivity;

	}

	public void run()

	{

		Log.e(LOG_TAG, "Test started");

		AndroidTestRunner testRunner = new AndroidTestRunner();

		testRunner.setTest(new ExampleSuite());

		testRunner.addTestListener(this);

		testRunner.setContext(parentActivity);

		testRunner.runTest();

		Log.e(LOG_TAG, "Test ended");

	}

	// TestListener

	public void addError(Test test, Throwable t)

	{

		Log.e(LOG_TAG, "addError: " + test.getClass().getName());

	}

	public void addFailure(Test test, AssertionFailedError t)

	{

		Log.e(LOG_TAG, "addFailure: " + test.getClass().getName());

	}

	public void endTest(Test test)

	{

		Log.e(LOG_TAG, "endTest: " + test.getClass().getName());

	}

	public void startTest(Test test)

	{

		Log.e(LOG_TAG, "startTest: " + test.getClass().getName());

	}

	@Override
	public void addFailure(Test test, junit.framework.AssertionFailedError t) {
		// TODO Auto-generated method stub

	}

	public class ExampleSuite extends TestSuite {
		public ExampleSuite() {
			addTestSuite(FootTabClickTest.class);
		}
	}

}
