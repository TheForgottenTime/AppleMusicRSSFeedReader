package com.example.applemusicrssfeedreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> titles;
    ArrayList<String> artists;
    ArrayList<String> imgLink;
    ArrayList<String> ids;
    ArrayList<String> combinedText;

    RecyclerViewAdapter adapter;
    boolean updating = false;

    String RSSFeed1 = "https://rss.itunes.apple.com/api/v1/us/apple-music/top-albums/all/";
    String RSSFeed2 = "/explicit.atom";
    int loaded = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titles = new ArrayList<String>();
        artists = new ArrayList<String>();
        imgLink = new ArrayList<String>();
        ids = new ArrayList<String>();
        combinedText = new ArrayList<String>();

        new ProcessInBackground().execute();



    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.RVDisplayAlbum);
        adapter = new RecyclerViewAdapter(combinedText, imgLink, ids, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if(loaded < 101 && updating == false){
                        updating = true;
                        loaded = loaded + 10;
                        new UpdateInBackground().execute();
                    }
                }
            }
        });

    }

    private void initCombineText(){
        for(int i = 0; i < titles.size(); i++){
            combinedText.add((i + 1) + ": " + titles.get(i) + " - " + artists.get(i));
        }
    }

    public InputStream getInputStream(URL url){
        try{
            //Log.d("URLERROR", "URL opening");
            return url.openConnection().getInputStream();
        }
        catch (IOException e){
            //Log.d("URLERROR", "URL Failed to open");
            return null;
        }
    }

    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception>{
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        Exception exception = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            progressDialog.setMessage("Loading Feed...");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... params){
            titles.clear();
            ids.clear();
            combinedText.clear();
            artists.clear();
            imgLink.clear();

            try{
                URL url = new URL(RSSFeed1 + loaded + RSSFeed2);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false);

                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url), "UTF-8");

                boolean insideEntry = false;

                int eventType = xpp.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT){

                    if(eventType == XmlPullParser.START_TAG){
                        if(xpp.getName().equalsIgnoreCase("entry")){
                            insideEntry = true;
                        }
                        else if(xpp.getName().equalsIgnoreCase("im:name")){
                            if(insideEntry){
                                titles.add(xpp.nextText());
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("im:artist")){
                            if(insideEntry){
                                artists.add(xpp.nextText());
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("id")){
                            if(insideEntry){
                                ids.add(xpp.nextText());
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("im:image")){
                            if(insideEntry){
                                imgLink.add(xpp.nextText());
                            }
                        }
                    }
                    else if(eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("entry")){
                        insideEntry = false;
                    }

                    eventType = xpp.next();
                }
            }
            catch (MalformedURLException e){
                exception = e;
            }
            catch (XmlPullParserException e){
                exception = e;
            }
            catch (IOException e){
                exception = e;
            }

            return exception;
        }

        @Override
        protected void onPostExecute(Exception s){
            super.onPostExecute(s);

            initCombineText();
            initRecyclerView();

            progressDialog.dismiss();
        }
    }

    public class UpdateInBackground extends AsyncTask<Integer, Void, Exception>{

        Exception exception = null;

        @Override
        protected Exception doInBackground(Integer... integers) {
            titles.clear();
            ids.clear();
            combinedText.clear();
            artists.clear();
            imgLink.clear();

            try{
                URL url = new URL(RSSFeed1 + loaded + RSSFeed2);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                factory.setNamespaceAware(false);

                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(getInputStream(url), "UTF-8");

                boolean insideEntry = false;

                int eventType = xpp.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT){

                    if(eventType == XmlPullParser.START_TAG){
                        if(xpp.getName().equalsIgnoreCase("entry")){
                            insideEntry = true;
                        }
                        else if(xpp.getName().equalsIgnoreCase("im:name")){
                            if(insideEntry){
                                titles.add(xpp.nextText());
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("im:artist")){
                            if(insideEntry){
                                artists.add(xpp.nextText());
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("id")){
                            if(insideEntry){
                                ids.add(xpp.nextText());
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("im:image")){
                            if(insideEntry){
                                imgLink.add(xpp.nextText());
                            }
                        }
                    }
                    else if(eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("entry")){
                        insideEntry = false;
                    }

                    eventType = xpp.next();
                }
            }
            catch (MalformedURLException e){
                exception = e;
            }
            catch (XmlPullParserException e){
                exception = e;
            }
            catch (IOException e){
                exception = e;
            }

            return exception;
        }

        @Override
        protected void onPostExecute(Exception s){
            super.onPostExecute(s);

            initCombineText();

            //adapter.notifyDataSetChanged();
            adapter.notifyItemRangeInserted(imgLink.size() - 11, 10);

            updating = false;
        }
    }
}
