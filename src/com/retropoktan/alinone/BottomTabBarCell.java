package com.retropoktan.alinone;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BottomTabBarCell extends LinearLayout{

	private Context mContext = null;  
    private ImageView mImageView = null;  
    private TextView mTextView = null;  
    private final static int DEFAULT_IMAGE_WIDTH = 64;  
    private final static int DEFAULT_IMAGE_HEIGHT = 64;  
    private int CHECKED_COLOR = Color.rgb(29, 118, 199); //选中蓝色  
    private int UNCHECKED_COLOR = Color.GRAY;   //自然灰色 
    
    public BottomTabBarCell(Context context) {  
        super(context);  
        // TODO Auto-generated constructor stub  
        mContext = context;  
        initViews(context);
    }

	public BottomTabBarCell(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initViews(context);
	}

	public void initViews(Context context) {
        mContext = context;  
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        View parentView = inflater.inflate(R.layout.image_text, this, true);  
        mImageView = (ImageView)parentView.findViewById(R.id.image_text_imageview);
        mTextView = (TextView)parentView.findViewById(R.id.image_text_textview);  
	}
    public void setImage(int id){  
        if(mImageView != null){  
            mImageView.setImageResource(id);  
            setImageSize(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);  
        }  
    }  
  
    public void setText(String string){  
        if(mTextView != null){  
            mTextView.setText(string);  
        }  
    }
    
    public void setDefaultTextColor() {
    	if (mTextView != null) {
			mTextView.setTextColor(UNCHECKED_COLOR);
		}
    }
  
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent ev) {  
        // TODO Auto-generated method stub  
        return true;  
    }  
    private void setImageSize(int width, int height){  
        if(mImageView != null){  
            ViewGroup.LayoutParams params = mImageView.getLayoutParams();  
            params.width = width;  
            params.height = height;  
            mImageView.setLayoutParams(params);  
        }  
    }  
    public void setChecked(int id){  
        if(mTextView != null){  
            mTextView.setTextColor(CHECKED_COLOR);  
        }  
        int checkDrawableId = -1;  
        switch (id) {
        case 0:
            checkDrawableId = R.drawable.order_pressed;  
            break;  
        case 1:  
        	checkDrawableId = R.drawable.order_pressed; 
            break;  
        case 2:  
        	checkDrawableId = R.drawable.order_pressed;  
            break;  
        case 3:  
        	checkDrawableId = R.drawable.order_pressed; 
            break;  
        default:break;  
        }  
        if(mImageView != null){  
            mImageView.setImageResource(checkDrawableId);  
        }  
    } 
}
