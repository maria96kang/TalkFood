/* ====================================================================
 * Copyright (c) 2014 Alpha Cephei Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALPHA CEPHEI INC. ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package edu.cmu.pocketsphinx.demo;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.content.Intent;
import java.util.Locale;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.w3c.dom.Text;

import android.os.AsyncTask;
import java.util.ArrayList;

import java.net.HttpURLConnection;
import java.net.URL;
import android.os.StrictMode;
//import com.example.append(line);le.maria96kang.talkfood.Recipe;

import java.io.*;
import android.widget.TextView;
import android.view.View;


public class PocketSphinxActivity extends Activity implements
        RecognitionListener, OnInitListener {

    ArrayList<String> ingredients = new ArrayList<String>();
    ArrayList<String> instructions = new ArrayList<String>();



    String name = "";
    String url = "";
    //Recipe r = new Recipe("", "", "", "", "", 0, ingredients, instructions);

    private static final String KWS_SEARCH = "wakeup";
    private static final String FORECAST_SEARCH = "platypus";
    private static final String DIGITS_SEARCH = "november";
    private static final String MENU_SEARCH = "menu";
    private static final String KEYPHRASE = "start"; //1. searches for this first
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;

    int count = 0;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        //Checks if text-to-speech is possible
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        //System.out.println("PROGRAM STARTED");
        // Prepare the data for UI
        captions = new HashMap<String, Integer>();
        captions.put(KWS_SEARCH, R.string.kws_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(DIGITS_SEARCH, R.string.digits_caption);
        captions.put(FORECAST_SEARCH, R.string.forecast_caption);
        setContentView(R.layout.main);
        ((TextView) findViewById(R.id.caption_text))
                .setText("Preparing the recognizer");

        instructions.add("In bowl, toss pineapple with jam. Set aside.");
        instructions.add("Gently break apart meringue nests into bite-sized pieces. Set 12 pieces of meringue aside. Stir remaining meringue pieces into yogurt.");
        instructions.add("Using four parfait or wine glasses, spoon 1/4 cup (50 mL) pineapple mixture into each glass. Top each with one-quarter of yogurt mixture, then with another 1/4 cup (50 mL) pineapple mixture.");
        instructions.add("Top each serving with 3 reserved meringue pieces. Garnish with mint sprigs, if desired.");

        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(PocketSphinxActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    ((TextView) findViewById(R.id.caption_text))
                            .setText("Failed to init recognizer " + result);
                } else {
                    //makeText(getApplicationContext(), "Say wakeup", Toast.LENGTH_SHORT).show();
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();

        /*
        String text = "";
        String[] names = {"candy_cane_chocolate_ice_cream_cake", };
        name = names[(int)(Math.random()*names.length)];
        url = "http://www.loblaws.ca/en_CA/recipes/recipeslisting/"+name+".html";

        new DownloadFilesTask().execute(url);
        */

    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {

        String text = hypothesis.getHypstr();
        //Handler myHandler = new Handler();

        if (text.equals(KEYPHRASE)) {
            ((TextView) findViewById(R.id.instructionDisplay)).setText(instructions.get(0));
            switchSearch(MENU_SEARCH); //2.
        }else if (text.equals("next")){
            count +=1;
            //((TextView) findViewById(R.id.result_text)).setText("you said next");
            ((TextView) findViewById(R.id.instructionDisplay)).setText(instructions.get(Math.min(count,3)));
            //recognizer.stop();
            //myHandler.postDelayed(mMyRunnable, 1000);
            System.out.println("CURRENT NUMBER "+ count);
            if (count > 3){
                speakWords("Bon appetee");
                count --;
                System.exit(0);
            }
            switchSearch(MENU_SEARCH);
        }
        else if (text.equals("previous")){
            count -=1;
            //((TextView) findViewById(R.id.result_text)).setText("you said previous");
            ((TextView) findViewById(R.id.instructionDisplay)).setText(instructions.get(Math.max(count,0)));

            //recognizer.stop();

            // myHandler.postDelayed(mMyRunnable, 1000);
            System.out.println("CURRENT NUMBER " + count);

            if (count < 0){
                count = 0;
            }
            switchSearch(MENU_SEARCH);
        }
        else if (text.equals("computers")){
            //((TextView) findViewById(R.id.result_text)).setText("reading text out loud");
            // myHandler.postDelayed(mMyRunnable, 1000);
            //recognizer.stop();

            System.out.println("CURRENT NUMBER " + count);
            switchSearch(MENU_SEARCH);

            speakWords(instructions.get(count));

            /*try {
                recognizer.wait(new Long(1000));
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }*/
        }
        //else if (text.equals(DIGITS_SEARCH))
            //switchSearch(DIGITS_SEARCH);
        //else if (text.equals(FORECAST_SEARCH))
            //switchSearch(FORECAST_SEARCH);
        else
            ((TextView) findViewById(R.id.result_text)).setText("'" + text + "' means what?!");
            //displays what it didn't understand
            //text = back (all the time?)
            //text = hypothesis?, matching to cases?

    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        ((TextView) findViewById(R.id.result_text)).setText("");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            //makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {
        if (DIGITS_SEARCH.equals(recognizer.getSearchName())
                || FORECAST_SEARCH.equals(recognizer.getSearchName()))
            makeText(getApplicationContext(), "Say wakeup", Toast.LENGTH_SHORT).show();
        switchSearch(KWS_SEARCH);
    }

    private void switchSearch(String searchName) {
    //send in menu

        recognizer.stop();

        recognizer.startListening(searchName);
        //menu
        String caption = getResources().getString(captions.get(searchName));
        //((TextView) findViewById(R.id.caption_text)).setText(caption);
        //finds and dislpays menu caption
    }

    private void setupRecognizer(File assetsDir) {
        File modelsDir = new File(assetsDir, "models");
        recognizer = defaultSetup()
                .setAcousticModel(new File(modelsDir, "hmm/en-us-semi"))
                .setDictionary(new File(modelsDir, "dict/cmu07a.dic"))
                .setRawLogDir(assetsDir).setKeywordThreshold(1e-20f)
                .getRecognizer();
        recognizer.addListener(this);

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE); //x
        // Create grammar-based searches.
        File menuGrammar = new File(modelsDir, "grammar/menu.gram"); //contains next or back
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
        File digitsGrammar = new File(modelsDir, "grammar/digits.gram"); //uneeded
        recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
        // Create language model search.
        File languageModel = new File(modelsDir, "lm/weather.dmp"); //uneeded
        recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);
    }

    //May be unnecesssary
    private Runnable mMyRunnable = new Runnable()
    {
        @Override
        public void run()
        {

        }
    };

    private void speakWords(String speech) {
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Starts up TTS if user has the required data
        // Else prompts user to install the required data

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(this, this);
            }
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    public void onInit(int initStatus) {
        // Checks if TTS successfully initializes
        if (initStatus == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        }

        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    private class DownloadFilesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            System.out.println(urls[0]);
            String html = "";
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(urls[0]);
                HttpResponse response = client.execute(request);


                InputStream in = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder str = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("itemprop=\"ingredients") || line.contains("recipeInstructions"))
                    {
                        if (line.contains("ingredients"))
                        {
                            line = line.substring(51, line.length()-6);
                            String actual = reader.readLine();
                            boolean hasHref = false;
                            while (actual.contains("href")) {
                                if (actual.contains("href"))
                                {
                                    hasHref = true;
                                }
                                actual = reader.readLine();
                            }
                            actual = actual.substring(14, actual.length()-((hasHref)?8:5));
                            line+= actual;
                            line+= "\n";
                        }
                        if (line.contains("recipeInstructions"))
                        {
                            line = line.replaceAll("<li itemprop=\"recipeInstructions\"><span class=\"pcbl-instructions\">", "");
                            // str.append(line);
                            int end = line.indexOf("</span>");
                            line = line.replaceAll("</span></li>", "");

                            String extra = "";

                            while (end < 0)
                            {
                                extra = reader.readLine();
                                end = extra.indexOf("</span>");
                                extra = extra.replaceAll("</span></li>", "");
                                line += extra;
                            }
                            line += "*\n";

                        }
                        str.append(line);
                    }
                    if (line.indexOf("<meta http-equiv=\"keywords\" content=")>0)
                    {
                        line = line.substring(38, line.length()-4);
                        String parts[] = line.split(", ");
                        // parts[0];
                    }
                }
                in.close();
                html = str.toString();

            }
            catch (IOException e)
            {

            }
            return html;
        }


    }


}
