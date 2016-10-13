package com.x8.ui;

import com.x8.wifi_login.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class AboutActivity extends Activity{
	
	private TextView title;
	private ImageButton leftBn;
	private ImageButton rightBn;
	private ButtonClick buttonClick;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		init();
	}
	
	private void init(){
		title = (TextView) this.findViewById(R.id.title);
		leftBn = (ImageButton) this.findViewById(R.id.left_bn);
		rightBn = (ImageButton) this.findViewById(R.id.right_bn);
		
		title.setText(AboutActivity.this.getResources().getString(R.string.title_about));
		leftBn.setVisibility(View.VISIBLE);
		rightBn.setVisibility(View.GONE);
		
		buttonClick = new ButtonClick();
		leftBn.setOnClickListener(buttonClick);
	}
	
	private class ButtonClick implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(v.getId() == leftBn.getId())
				AboutActivity.this.finish();
		}
	}
}
