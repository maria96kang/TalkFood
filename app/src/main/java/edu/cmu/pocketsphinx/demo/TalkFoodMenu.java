package edu.cmu.pocketsphinx.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class TalkFoodMenu extends Activity {

    public Button buttonStart;
    public Button buttonInstruct;
    public Button buttonExit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_food_menu);

        buttonStart = (Button)findViewById(R.id.one);
        buttonStart.setOnClickListener(listener);

        buttonExit = (Button)findViewById(R.id.three);
        buttonExit.setOnClickListener(listener);

        buttonInstruct = (Button)findViewById(R.id.two);
        buttonInstruct.setOnClickListener(listener);

    }

    //Button allows user to stop and exit application from menu screen
    private OnClickListener listener = new OnClickListener() {
        public void onClick(View v) {
            Button b = (Button)v;
            Intent intent = new Intent();
            b.setTypeface(null, Typeface.BOLD);

            if(b.equals(buttonExit)) {
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }else if (b.equals(buttonStart)) {
                intent = new Intent(TalkFoodMenu.this, Recipe.class);
                TalkFoodMenu.this.startActivity(intent);

            }else{
                intent = new Intent(TalkFoodMenu.this, Instructions.class);
                TalkFoodMenu.this.startActivity(intent);
            }

            startActivity(intent);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.talk_food_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
