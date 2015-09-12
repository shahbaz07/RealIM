package com.postagain.realim.activities;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.postagain.realim.R;
import com.postagain.realim.model.ChatMessage;

public class ActivityChat extends Activity implements OnClickListener {

	private static final int RECEIVE_MESSAGE_INTERVAL = 100;
	private static final int RECEIVE_MESSAGE_COUNT = 50;
	
	private Handler chatMessageHandler = new Handler();
	
	private EditText txtMessage = null;
	private Button btnSend = null;
	private ImageView imgTakePicture = null;
	private ImageView imgPreview = null;
	
	private ListView chatMessagesListview = null;
	private ChatMessageAdapter chatMessageAdapter = null;
	
	private List<ChatMessage> chatMessages = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		initViews();
		setListeners();
		
		chatMessageHandler.postDelayed(chatMessageRunnable, RECEIVE_MESSAGE_INTERVAL);
		//removeAllMessages();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(imgPreview.getVisibility() == View.VISIBLE)
			{
				imgPreview.setVisibility(View.GONE);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0)
		{
			final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RealIM", "IMG.jpg");
			if(file.exists())
			{
				FileInputStream fileInputStream=null;
		        
		        
		        byte[] bFile = new byte[(int) file.length()];
		        
		        try {
				    fileInputStream = new FileInputStream(file);
				    fileInputStream.read(bFile);
				    fileInputStream.close();
					final ParseFile fileRequest = new ParseFile("IMG.jpg", bFile);
					fileRequest.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException arg0) {
							if(arg0 == null)
							{
								file.delete();
								ChatMessage chatMessage = new ChatMessage();
								chatMessage.setUserName(ParseUser.getCurrentUser().getUsername());
								chatMessage.setPhote(fileRequest);
								chatMessage.setCreatedon(System.currentTimeMillis());
								chatMessage.saveInBackground(new SaveCallback() {
				                    @Override
				                    public void done(ParseException e) {
				                        if(e == null)
				                        {
											txtMessage.setText("");
				                        }
				                        else
				                        {
				                        	showMessage("Unable to send message.");
				                        }
				                    }
				                });
							}
							else
							{
								showMessage("Unable to send image.");
							}
						}
					});
		        }
		        catch(Exception ex)
		        {
		        	
		        }
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageLoader.getInstance().clearMemoryCache();
		//ImageLoader.getInstance().clearDiskCache();
	}

	private void initViews()
	{
		chatMessages = new ArrayList<ChatMessage>();
		txtMessage = (EditText)findViewById(R.id.txtMessage);
		btnSend = (Button)findViewById(R.id.btnSend);
		imgTakePicture = (ImageView)findViewById(R.id.imgTakePicture);
		imgPreview = (ImageView)findViewById(R.id.imgPreview);
		
		chatMessagesListview = (ListView)findViewById(R.id.chatMessagesListview);
		chatMessageAdapter = new ChatMessageAdapter(this, R.layout.chat_message_row);
		chatMessagesListview.setAdapter(chatMessageAdapter);
		
	}
	
	private void setListeners()
	{
		btnSend.setOnClickListener(this);
		imgTakePicture.setOnClickListener(this);
		imgPreview.setOnClickListener(this);
		chatMessagesListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				ChatMessage message = chatMessages.get(position);
				if(message.getPhoto() != null)
				{
					imgPreview.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().loadImage(message.getPhoto().getUrl(), new ImageLoadingListener() {
						
						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							
						}
						
						@Override
						public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
							
						}
						
						@Override
						public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
							imgPreview.setImageBitmap(arg2);
						}
						
						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							
						}
					});
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.btnSend:
				final String message = txtMessage.getText().toString();
				if(TextUtils.isEmpty(message))
				{
					showMessage("Please write something.");
				}
				else
				{
					ChatMessage chatMessage = new ChatMessage();
					chatMessage.setUserName(ParseUser.getCurrentUser().getUsername());
					chatMessage.setMessage(message);
					chatMessage.setCreatedon(System.currentTimeMillis());
					chatMessage.saveInBackground(new SaveCallback() {
	                    @Override
	                    public void done(ParseException e) {
	                        if(e == null)
	                        {
								txtMessage.setText("");
	                        }
	                        else
	                        {
	                        	showMessage("Unable to send message.");
	                        }
	                    }
	                });
				}
				break;
			case R.id.imgTakePicture:
				
				File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RealIM");
				if(!file.exists())
				{
					file.mkdirs();
				}
				file = new File(file.getAbsolutePath(), "IMG.jpg");
				if(file.exists())
				{
					file.delete();
				}
				Uri outputFileUri = Uri.fromFile(file);

				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
				cameraIntent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri);
				cameraIntent.putExtra("return-data", true);
                startActivityForResult(cameraIntent, 0);
				break;
			case R.id.imgPreview:
				imgPreview.setVisibility(View.GONE);
				break;
		}
	}
	
	private void receiveChatMessages() {
		ParseQuery<ChatMessage> query = ParseQuery.getQuery(ChatMessage.class);
		query.setLimit(RECEIVE_MESSAGE_COUNT);
		query.orderByAscending("createdAt");
		query.findInBackground(new FindCallback<ChatMessage>() {
		    public void done(List<ChatMessage> messages, ParseException e) {
		        if (e == null) {
		        	//chatMessages.clear();
		        	int diff = messages.size() - chatMessages.size();
		        	if(diff > 0)
		        	{
		        		int size = chatMessages.size();
		        		for(int i = messages.size() - 1; i >= size; i--)
		        		{
		        			chatMessages.add(messages.get(i));
		        		}
			        	//chatMessages.addAll(messages);
			            chatMessageAdapter.notifyDataSetChanged();
			            chatMessagesListview.setSelection(chatMessages.size() - 1);
		        	}
		        }
		        chatMessageHandler.postDelayed(chatMessageRunnable, RECEIVE_MESSAGE_INTERVAL);
		    }
		});
	}
	
	private Runnable chatMessageRunnable = new Runnable() {
	    @Override
	    public void run() {
	       receiveChatMessages();
	    }
	};
	
	private void showMessage(final String msg)
	{
		Toast.makeText(ActivityChat.this, msg, Toast.LENGTH_SHORT).show();
	}
	
	private class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

		private LayoutInflater inflater = null;
		
	    public ChatMessageAdapter(Context context, int textViewResourceId) {
	    	super(context, textViewResourceId);
	    	inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    @Override
		public int getCount() {
			return chatMessages.size();
		}

		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
	    	if(convertView == null)
	    	{
	    		convertView = inflater.inflate(R.layout.chat_message_row, null, false);
	    		holder = new ViewHolder();
	    		holder.lblName = (TextView) convertView.findViewById(R.id.lblName);
	    		holder.lblMessage = (TextView) convertView.findViewById(R.id.lblMessage);
	    		holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
	    		holder.imageView.setTag(position);
	    		convertView.setTag(holder);
	    	}
	    	else
	    	{
	    		holder = (ViewHolder) convertView.getTag();
	    	}
	    	
	    	final ChatMessage chatMessage = chatMessages.get(position);
	    	
	    	holder.lblName.setText(chatMessage.getUserName() + ":");
	    	if(chatMessage.getPhoto() == null)
	    	{
	    		holder.lblMessage.setVisibility(View.VISIBLE);
	    		holder.lblMessage.setText(chatMessage.getMessage());
	    		holder.imageView.setVisibility(View.GONE);
	    	}
	    	else
	    	{
	    		holder.lblMessage.setVisibility(View.GONE);
	    		int tag = (Integer) holder.imageView.getTag();
	    		if(tag != position || holder.imageView.getDrawable() == null || ((BitmapDrawable)holder.imageView.getDrawable()).getBitmap() == null)
	    		{
		    		holder.imageView.setImageBitmap(null);
		    		holder.imageView.setVisibility(View.VISIBLE);
		    		ImageLoader.getInstance().loadImage(chatMessage.getPhoto().getUrl(), new ImageLoadingListener() {
						
						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							
						}
						
						@Override
						public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
							
						}
						
						@Override
						public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
							holder.imageView.setImageBitmap(arg2);
						}
						
						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							
						}
					});
	    		}
	    	}
		    return convertView;
	    }
		
	}
	
	private class ViewHolder
	{
		public TextView lblName = null;
		public TextView lblMessage = null;
		public ImageView imageView = null;
	}
	
	private void removeAllMessages()
	{
		ParseQuery<ChatMessage> query = ParseQuery.getQuery(ChatMessage.class);
		query.findInBackground(new FindCallback<ChatMessage>() {
		    public void done(List<ChatMessage> messages, ParseException e) {
		        if (e == null) {
		        	for (ChatMessage chatMessage : messages) {
						try {
							chatMessage.delete();
						} catch (ParseException e1) {
						}
					}
		        }
		    }
		});
	}

}
