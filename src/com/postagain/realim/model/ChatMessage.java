package com.postagain.realim.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

	
@ParseClassName("Message")
public class ChatMessage extends ParseObject {
	
	public static final String USER_NAME_KEY = "userName";
	public static final String MESSAGE_KEY = "message";
	public static final String PHOTO_KEY = "photo";
	public static final String CREATED_ON_KEY = "createdOn";
	
    public String getUserName() {
        return getString(USER_NAME_KEY);
    }

    public String getMessage() {
        return getString(MESSAGE_KEY);
    }
    
    public long getCreatedon() {
        return getLong(CREATED_ON_KEY);
    }
    
    public ParseFile getPhoto() {
        return getParseFile(PHOTO_KEY);
    }

    public void setUserName(String userName) {
        put(USER_NAME_KEY, userName);  
    }

    public void setMessage(String body) {
        put(MESSAGE_KEY, body);
    }
    
    public void setPhote(ParseFile photo)
    {
    	put(PHOTO_KEY, photo);
    }
    
    public void setCreatedon(long createdon)
    {
    	put(CREATED_ON_KEY, createdon);
    }
}

