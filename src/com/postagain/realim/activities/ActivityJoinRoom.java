package com.postagain.realim.activities;

import com.parse.ParseUser;
import com.postagain.realim.R;
import com.postagain.realim.R.id;
import com.postagain.realim.R.layout;
import com.postagain.realim.dialog.DialogJoinRoom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityJoinRoom extends Activity implements OnClickListener {

	private Button btnJoinRoom = null;
	
	private DialogJoinRoom dialogJoinRoom = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_room);
		
		initViews();
		setListeners();
	}
	
	private void initViews()
	{
		btnJoinRoom = (Button)findViewById(R.id.btnJoinRoom);
		dialogJoinRoom = new DialogJoinRoom(this, loginHandler, android.R.style.Theme_Translucent_NoTitleBar);
	}
	
	private void setListeners()
	{
		btnJoinRoom.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.btnJoinRoom:
				dialogJoinRoom.show();
				break;
		}
	}
	
	private Handler loginHandler = new Handler()
	{
		@Override
        public void handleMessage(Message msg)
        {
			ParseUser.getCurrentUser().setUsername(msg.obj.toString());
			startActivity(new Intent(ActivityJoinRoom.this, ActivityChat.class));
			finish();
        }
	};

}
