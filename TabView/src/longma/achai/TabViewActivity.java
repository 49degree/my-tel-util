package longma.achai;

import java.util.ArrayList;
import java.util.List;

import longma.achai.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TabViewActivity extends TabActivity implements
		OnCheckedChangeListener {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initRaioButton();
		initViewpage();

	}

	private RadioButton rbBus;
	private RadioButton rbMine;

	private void initRaioButton() {
		rbBus = (RadioButton) findViewById(R.id.radio_button0);
		rbMine = (RadioButton) findViewById(R.id.radio_button5);
		rbBus.setOnCheckedChangeListener(this);
		rbMine.setOnCheckedChangeListener(this);

	}

	ViewPager mPage;
	List<View> contentView;

	private void initViewpage() {
		mPage = (ViewPager) findViewById(R.id.activity_page);
		contentView = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		contentView.add(mInflater.inflate(R.layout.content_bus, null));
		contentView.add(mInflater.inflate(R.layout.content_mine, null));

		mPage.setAdapter(new PagerAdapter() {

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				// TODO Auto-generated method stub
				super.destroyItem(container, position, object);
				container.removeView(contentView.get(position));
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return contentView.size();
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(contentView.get(position), 0);
				return contentView.get(position);
			}
		});
		mPage.setCurrentItem(0);
		mPage.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					rbBus.setChecked(true);
					break;
				case 1:
					rbMine.setChecked(true);
					initFavListener();
					break;
				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	LayoutInflater mineLayout = null;
	View favButton;

	public void initFav() {
		mineLayout = LayoutInflater.from(this);
		favButton = mineLayout.inflate(R.layout.content_mine, null);
		initFavRow(favButton);
		// initfavlistView(favButton);
	}

	public void initFavListener() {
		View current = (View) mPage.getParent();
		favText = (TextView) current.findViewById(R.id.textView5);
		favText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent favIntent = new Intent().setClass(TabViewActivity.this, FavActivity.class);
				startActivity(favIntent);
			}
		});
	}

	private TableRow favRow = null;
	private TextView favText = null;

	private void initFavRow(View favButton2) {
		favRow = (TableRow) favButton2.findViewById(R.id.tableRow3);

		favText = (TextView) favButton2.findViewById(R.id.textView5);

		favText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(TabViewActivity.this, "ok", Toast.LENGTH_LONG)
						.show();
			}
		});

		favRow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				@SuppressWarnings("unused")
				String ok = null;
			}
		});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.radio_button0:
				mPage.setCurrentItem(0);
				break;
			case R.id.radio_button5:
				mPage.setCurrentItem(1);
				break;
			default:
				break;
			}
		}
	}
}