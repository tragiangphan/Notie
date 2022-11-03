package com.example.notesapp;

import java.util.ArrayList;

public class NoteModel {
    String id, noteTitle,
            noteSubtitle,
            noteContent,
            createTime,
            imageURL;
    ArrayList<String> sharer;

    public NoteModel() {
    }

    public NoteModel(String id, String noteTitle, String noteSubtitle, String noteContent, String createTime, String imageURL, ArrayList<String> sharer) {
        this.id = id;
        this.noteTitle = noteTitle;
        this.noteSubtitle = noteSubtitle;
        this.noteContent = noteContent;
        this.createTime = createTime;
        this.imageURL = imageURL == null ? "" : imageURL;
        this.sharer = sharer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteSubtitle() {
        return noteSubtitle;
    }

    public void setNoteSubtitle(String noteSubtitle) {
        this.noteSubtitle = noteSubtitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public ArrayList<String> getSharer() {
        return sharer;
    }

    public void setSharer(ArrayList<String> sharer) {
        this.sharer = sharer;
    }
}
