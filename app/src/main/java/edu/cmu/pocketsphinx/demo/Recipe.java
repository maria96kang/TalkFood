package edu.cmu.pocketsphinx.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Recipe extends Activity {

    public Button buttonBegin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        buttonBegin = (Button)findViewById(R.id.four);
        buttonBegin.setOnClickListener(listener);
    }

    //Button allows user to stop and exit application from menu screen
    private View.OnClickListener listener = new View.OnClickListener() {
        public void onClick(View v) {
            Button b = (Button)v;
            b.setTypeface(null, Typeface.BOLD);
            Intent intent = new Intent(Recipe.this, PocketSphinxActivity.class);
            startActivity(intent);
        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recipe, menu);
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
