package com.styling.plaintextchips;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ChipsActivity extends EditText {
	
	private int beforeChangeIndex = 0, afterChangeIndex = 0, stringLength = 0;
	private String changeString;
	private String lastString;
	
	public boolean isTextAdditionInProgress = false, isTextDeletedFromTouch = false, isExecuted = false;
	
	/* Constructor */
	public ChipsActivity(Context context) {
		super(context);
		init(context);
	}
	/* Constructor */
	public ChipsActivity(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	/* Constructor */
	public ChipsActivity(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	/* set listeners for item click and text change */
	public void init(Context context){
		//setOnItemClickListener(this);
		addTextChangedListener(textWather);
	}
	/*TextWatcher, If user type any word and presses space then following code will regenerate chips */
	private TextWatcher textWather = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			String addedString = s.toString();
			if (!isTextAdditionInProgress) {
				if (stringLength < addedString.length()) {
					// something is added
					int numberLettersAdded = addedString.length() - stringLength;
					if(numberLettersAdded == 1){
					if (!TextUtils.isEmpty(addedString.trim())) {
						int startIndex =  ChipsActivity.this.getSelectionEnd();
						startIndex = startIndex < 1 ? 1 : startIndex;
						String charAtStartIndex = Character
								.toString(addedString
										.charAt(startIndex - 1));
						if (charAtStartIndex.equals(" ")) {
							isTextAdditionInProgress = true;
							setChips(s.toString());
						}
					}
					}
					if (numberLettersAdded > 1 && !isExecuted){
						isExecuted = true;
						setChips(s.toString());
						
					}
					isExecuted = false;
				}
			}

			stringLength = addedString.length();
			
			
//			if(count >=1){
//				if(s.charAt(start) == ' ')
//					setChips(); // generate chips
//			}
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			beforeChangeIndex = ChipsActivity.this.getSelectionStart();
			changeString = s.toString();
		}
		@Override
		public void afterTextChanged(Editable s) {
			afterChangeIndex = ChipsActivity.this.getSelectionEnd();
			lastString = s.toString();
		}
	};
	/*This function has whole logic for chips generate*/
	public void setChips(String s){
		
			
			SpannableStringBuilder ssb = new SpannableStringBuilder(getText());
			// splits string with space
			String chips[] = s.trim().split(" ");
			int x =0;
			// loop will generate ImageSpan for every word separated by space
			for(String c : chips){
				// inflate my chips "textview" layout 
				LayoutInflater lf = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				TextView textView = (TextView) lf.inflate(R.layout.textview, null);
				textView.setText(c); // set text
				// converts TextView to drawable Bitmap.
				int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
				textView.measure(spec, spec);
				textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
				Bitmap b = Bitmap.createBitmap(textView.getWidth(), textView.getHeight(),Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(b);
				canvas.translate(-textView.getScrollX(), -textView.getScrollY());
				textView.draw(canvas);
				textView.setDrawingCacheEnabled(true);
				Bitmap cacheBmp = textView.getDrawingCache();
				Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
				textView.destroyDrawingCache();  // destroy drawable
				// create bitmap drawable for imagespan
				BitmapDrawable bmpDrawable = new BitmapDrawable(this.getResources(), viewBmp);
				bmpDrawable.setBounds(0, 0,bmpDrawable.getIntrinsicWidth(),bmpDrawable.getIntrinsicHeight());
				// create and set imagespan 
				ssb.setSpan(new ImageSpan(bmpDrawable),x ,x + c.length() , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				this.setMovementMethod(LinkMovementMethod
						.getInstance());
				ClickableSpan clickSpan = new ClickableSpan() {

					@Override
					public void onClick(View view) {
	                  deleteString();
					}

				};
				ssb.setSpan(clickSpan, x, x + c.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				x = x+ c.length()+1;
				
			}
			// set chips span 
			setSpannableText(ssb);
			// move cursor to last 
			setSelection(getText().length());
		
	}
	
//	ClickableSpan clickSpan = new ClickableSpan() {
//		 
//        @Override
//        public void onClick(View view) {
//        
//        	deleteString();
//        }
//
//    };
	

    
    private void deleteString(){
		int[] startEnd = getSelectionStartAndEnd();
		int i = startEnd[0];
		int j = startEnd[1];
		isTextDeletedFromTouch = true;
		isTextAdditionInProgress = true;
		
		final SpannableStringBuilder sb = new SpannableStringBuilder(this.getText()
				);

			
			String deletedSubString = sb.subSequence(Math.min(i, j),
					Math.max(i, j)).toString();
			
		boolean hasSpaceAtLast = true;
		try {
			sb.subSequence(Math.min(i, j + 1), Math.max(i, j + 1))
					.toString();
		} catch (Exception e) {
			hasSpaceAtLast = false;
		}
	
	    sb.replace(Math.min(i, hasSpaceAtLast ? j + 1 : j),
				Math.max(i, hasSpaceAtLast ? j + 1 : j), "");
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				ChipsActivity.this.setText(sb);
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						isTextAdditionInProgress = false;
						stringLength = ChipsActivity.this.getText().toString().length();
						isTextDeletedFromTouch = false;
						//Log.i("I am replacing text","I am replacing text 4");
						ChipsActivity.this.setMovementMethod(LinkMovementMethod.getInstance());
					}
				},50);
				
			}
		}, 10);
		
		setSelection(getText().length());
    }
    
		
    /**
	 * @return int[]
	 * method which simply gets the getSelectionStart and getSelectionEnd
	 * of this TextView
	 */
	
	private int[] getSelectionStartAndEnd(){
		int[] startEnd = new int[2];
		startEnd[0] = this.getSelectionStart()<0 ? 0 : this.getSelectionStart();
		startEnd[1] = this.getSelectionEnd()<0 ? 0 : this.getSelectionEnd();
		return startEnd;
	}
	
	
	public void setSpannableText(final Spannable ssb){
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				ChipsActivity.this.setText(ssb);
				ChipsActivity.this.setSelection(ChipsActivity.this.getText().toString().length());
				isTextAdditionInProgress = false;
				
			}
		}, 20);
	}
	
		
}		
	

	
