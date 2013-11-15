package com.styling.plaintextchips;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;




public class MainActivityPlain extends Activity {

	EditText ed;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainp_layout);
        ed = (EditText) findViewById(R.id.MyeditText);

        
    }
    
public void buttonClicked(View v) {
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText(ed.getText().toString());
	}
   
 
    
    

}
