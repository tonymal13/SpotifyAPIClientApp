package ru.mal.SpotifyClient.entity;

import lombok.Data;

import java.util.List;

@Data
public class Album {
    private String href;
    private List<AlbumItem> items;
    private int limit;
    private String next;
    private int offset;
    private String previous;
    private int total;
}
