package com.ByteCrunchers.TransGo.RealTimeOCR.language;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.ByteCrunchers.TransGo.RealTimeOCR.CaptureActivity;
import com.ByteCrunchers.TransGo.RealTimeOCR.R;

/**
 * Created by Faizi on 10-Apr-2018.
 */

public class YandexTranslatorBackgroundTask extends AsyncTask<String, Void, String> {
    //Declare Context
    Context ctx;
    private CaptureActivity activity;
    private TextView textView;
    private View progressView;
    private TextView targetLanguageTextView;
    private String sourceLanguageCode;
    private String targetLanguageCode;
    private String sourceText;
    private String translatedText = "";
    String resultString;
    public YandexTranslatorBackgroundTask(CaptureActivity activity, String sourceLanguageCode, String targetLanguageCode,
                                   String sourceText){
        this.activity = activity;
        this.sourceLanguageCode = sourceLanguageCode;
        this.targetLanguageCode = targetLanguageCode;
        this.sourceText = sourceText;
        textView = (TextView) activity.findViewById(R.id.translation_text_view);
        progressView = (View) activity.findViewById(R.id.indeterminate_progress_indicator_view);
        targetLanguageTextView = (TextView) activity.findViewById(R.id.translation_language_text_view);

    }

    @Override
    protected String doInBackground(String... params) {
        //String variables
        String textToBeTranslated = sourceText;
        String languagePair = sourceLanguageCode+"-"+targetLanguageCode;

        String jsonString;

        try {
            //Set up the translation call URL
            String yandexKey = "trnsl.1.1.20180102T204517Z.04fa0d17c710294a.a073b91196a242b91ee168e0bf70a0b472aec91a";
            String yandexUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey
                    + "&text=" + textToBeTranslated + "&lang=" + languagePair;
            URL yandexTranslateURL = new URL(yandexUrl);

            //Set Http Conncection, Input Stream, and Buffered Reader
            HttpURLConnection httpJsonConnection = (HttpURLConnection) yandexTranslateURL.openConnection();
            InputStream inputStream = httpJsonConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            //Set string builder and insert retrieved JSON result into it
            StringBuilder jsonStringBuilder = new StringBuilder();
            while ((jsonString = bufferedReader.readLine()) != null) {
                jsonStringBuilder.append(jsonString + "\n");
            }

            //Close and disconnect
            bufferedReader.close();
            inputStream.close();
            httpJsonConnection.disconnect();

            //Making result human readable
            resultString = jsonStringBuilder.toString().trim();
            //Getting the characters between [ and ]
            resultString = resultString.substring(resultString.indexOf('[')+1);
            resultString = resultString.substring(0,resultString.indexOf("]"));
            //Getting the characters between " and "
            resultString = resultString.substring(resultString.indexOf("\"")+1);
            resultString = resultString.substring(0,resultString.indexOf("\""));

            Log.d("Translation Result:", resultString);
            return jsonStringBuilder.toString().trim();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result!="") {
            //Log.i(TAG, "SUCCESS");
            if (targetLanguageTextView != null) {
                targetLanguageTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL), Typeface.NORMAL);
            }
            textView.setText(resultString);
            textView.setVisibility(View.VISIBLE);
            textView.setTextColor(activity.getResources().getColor(R.color.translation_text));

            // Crudely scale betweeen 22 and 32 -- bigger font for shorter text
            int scaledSize = Math.max(22, 32 - translatedText.length() / 4);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);

        } else {
            //Log.e(TAG, "FAILURE");
            targetLanguageTextView.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC), Typeface.ITALIC);
            targetLanguageTextView.setText("Unavailable");

        }

        // Turn off the indeterminate progress indicator
        if (progressView != null) {
            progressView.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}