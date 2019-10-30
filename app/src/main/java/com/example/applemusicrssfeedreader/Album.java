package com.example.applemusicrssfeedreader;

public class Album {
    private String title;
    private String artist;
    private String albumArt;
    private String appleMusicUrl;
    private String rights;
    private String releaseDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getAppleMusicUrl() {
        return appleMusicUrl;
    }

    public void setAppleMusicUrl(String appleMusicUrl) {
        this.appleMusicUrl = appleMusicUrl;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
