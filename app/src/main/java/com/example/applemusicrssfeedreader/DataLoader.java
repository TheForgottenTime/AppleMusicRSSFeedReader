package com.example.applemusicrssfeedreader;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DataLoader {
    ArrayList<String> dataSourceUrls = new ArrayList<>();
    ArrayList<int> loadedEntries = new ArrayList<>();
    String suffixExplicit = "/explicit.atom";
    String suffixNonExplicit = "/non-explicit.atom";
    int dataSelection;

    public DataLoader() {
        dataSourceUrls.add("https://rss.itunes.apple.com/api/v1/us/apple-music/coming-soon/all/");
        dataSourceUrls.add("https://rss.itunes.apple.com/api/v1/us/apple-music/hot-tracks/all/");
        dataSourceUrls.add("https://rss.itunes.apple.com/api/v1/us/apple-music/new-releases/all/");
        dataSourceUrls.add("https://rss.itunes.apple.com/api/v1/us/apple-music/top-albums/all/");
        dataSourceUrls.add("https://rss.itunes.apple.com/api/v1/us/apple-music/top-songs/all/");

        loadedEntries.add(10);
        loadedEntries.add(10);
        loadedEntries.add(10);
        loadedEntries.add(10);
        loadedEntries.add(10);
    }

    public ArrayList<Album> loadAlbums(int numToLoad){

    }
}


public class InitLoadAlbums extends AsyncTask<Integer, Void, Exception> {
    ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

    DataLoader dLoader = new DataLoader();
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

        try{
            //init url
            URL url = new URL(dLoader.dataSourceUrls.get(3) + );

            //init xmlPullParser with url input stream
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(getUrlInputStream(url), "UTF-8");

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
//        initCombineText();
//        initRecyclerView();

        progressDialog.dismiss();
    }

    public InputStream getUrlInputStream(URL url){
        try{
            //Log.d("URLERROR", "URL opening");
            return url.openConnection().getInputStream();
        }
        catch (IOException e){
            //Log.d("URLERROR", "URL Failed to open");
            return null;
        }
    }
}