package com.postagain.realim.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.postagain.realim.R;
import com.postagain.realim.R.id;
import com.postagain.realim.R.layout;

public class DialogJoinRoom extends Dialog implements android.view.View.OnClickListener {

	private Context context = null;
	
	private EditText txtName = null;
	private Button btnDismiss = null;
	private Button btnConfirm = null;
	
	private ProgressDialog loading = null;
	
	private Handler loginHandler = null;
	
	public DialogJoinRoom(Context context, Handler loginHandler, int theme) {
		super(context, theme);
		this.context = context;
		this.loginHandler = loginHandler;
		
		loading = new ProgressDialog(context);
		loading.setMessage("Please wait...");
		loading.setCancelable(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_join_room);
		
		initViews();
		setListeners();
	}
	
	private void initViews()
	{
		txtName = (EditText)findViewById(R.id.txtName);
		btnDismiss = (Button)findViewById(R.id.btnDismiss);
		btnConfirm = (Button)findViewById(R.id.btnConfirm);
	}
	
	private void setListeners()
	{
		btnDismiss.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.btnDismiss:
				dismiss();
				break;
			case R.id.btnConfirm:
				final String name = txtName.getText().toString();
				if(TextUtils.isEmpty(name))
				{
					showMessage("Name required.");
				}
				else
				{
					loading.show();
					ParseAnonymousUtils.logIn(new LogInCallback() {
				        @Override
				        public void done(ParseUser user, ParseException e) {
				        	loading.dismiss();
			                if (e != null) {
			                	showMessage("Login failed.");
			                } else {
			                	Message msg = new Message();
			                	msg.obj = name;
			                	loginHandler.sendMessage(msg);
			                	dismiss();
			                }
			            }
			        });
				}
				break;
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		InputMethodManager imm = ((InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE));
		imm.hideSoftInputFromWindow(txtName.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	private void showMessage(String msg)
	{
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
}
