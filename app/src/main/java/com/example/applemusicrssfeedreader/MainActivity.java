/*
* Jack Murphy
* jackmurphy569@gmail.com
* 10-28-2019
* */

package com.example.applemusicrssfeedreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

//This code would need to be fixed in a lot of places to eliminate spaghetti

public class MainActivity extends AppCompatActivity {

    //declare global variables
    ArrayList<String> titles;
    ArrayList<String> artists;
    ArrayList<String> imgLink;
    ArrayList<String> ids;
    ArrayList<String> combinedText;

    RecyclerViewAdapter adapter;
    boolean updating = false;

    //rss feed url broken up to make adding quantity later
    String RSSFeed1 = "https://rss.itunes.apple.com/api/v1/us/apple-music/top-albums/all/";
    String RSSFeed2 = "/explicit.atom";
    //preset to start at 10 albums loaded, increments by 10 on scroll
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

        //load initial set of albums
        new ProcessInBackground().execute();
    }

    //initializes recycler and onScrollListener
    //listener will load more albums in background when scroll has hit bottom of the page
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

    //combines the album title and the artist name to make loading into the recycler view easier.
    //adds rank as well
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

    //loader for initial set of albums
    public class ProcessInBackground extends AsyncTask<Integer, Void, Exception>{
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        Exception exception = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //set loading message
            progressDialog.setMessage("Loading Feed...");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... params){

            //clear arraylists of data
            //not needed anymore, but things break without them for some reason
            titles.clear();
            ids.clear();
            combinedText.clear();
            artists.clear();
            imgLink.clear();

            try{
                //assemble url from halves
                URL url = new URL(RSSFeed1 + loaded + RSSFeed2);

                //init xmlPullParser with url input stream
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF-8");

                //used to check if xpp is currently inside a entry
                boolean insideEntry = false;

                int eventType = xpp.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT){

                    if(eventType == XmlPullParser.START_TAG){
                        if(xpp.getName().equalsIgnoreCase("entry")){
                            //indicates that xpp is now inside entry
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
                        //corresponds to apple music url to album
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
                        //reset entry boolean
                        insideEntry = false;
                    }

                    //increment xpp through xml output
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

            //run post processing on artist and album names
            //display array in recycler
            initCombineText();
            initRecyclerView();

            progressDialog.dismiss();
        }
    }

    //just like the name says, used to add more entries to the list of current albums.
    //basically a stripped down version of the class ProcessInBackground
    public class UpdateInBackground extends AsyncTask<Integer, Void, Exception>{

        Exception exception = null;

        @Override
        protected Exception doInBackground(Integer... integers) {
            //clear data from arraylists, needed due to apple not providing a way to only get new album entries.
            titles.clear();
            ids.clear();
            combinedText.clear();
            artists.clear();
            imgLink.clear();

            try{
                //assemble url
                URL url = new URL(RSSFeed1 + loaded + RSSFeed2);

                //build XmlPullParser with url input stream
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF-8");

                //used to check if xpp is currently inside a entry
                boolean insideEntry = false;

                int eventType = xpp.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT){

                    if(eventType == XmlPullParser.START_TAG){
                        if(xpp.getName().equalsIgnoreCase("entry")){
                            //indicates that xpp is now inside entry
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
                        //corresponds to apple music url to album
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
                        //reset entry boolean
                        insideEntry = false;
                    }

                    //increment xpp through xml output
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

            //assemble album and artist names into single strings
            initCombineText();

            //caused issues with loosing scroll position more than notifyItemRangeInserted
            //also caused crashing, probably due to large reads and writes to memory, but I don't know for sure
            //adapter.notifyDataSetChanged();

            //alert recycler of added 10 items
            //still has issues, but they are uncommon enough to not be a major issue
            //the number of items added per updated was increased to 10 due to needing less updates, resulting in a lower crash and scroll position issue frequency
            adapter.notifyItemRangeInserted(imgLink.size() - 11, 10);

            //reset update indicator
            updating = false;
        }
    }
}
