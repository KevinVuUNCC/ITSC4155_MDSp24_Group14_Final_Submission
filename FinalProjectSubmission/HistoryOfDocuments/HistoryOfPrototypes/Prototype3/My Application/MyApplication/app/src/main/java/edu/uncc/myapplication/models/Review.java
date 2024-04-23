package edu.uncc.myapplication.models;

public class Review {

    String name,text, uid, docid;

    public Review() {
    }

    public Review(String name, String text, String uid, String recipeDocId) {
        this.name = name;
        this.text = text;
        this.uid = uid;
        this.docid = recipeDocId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRecipeDocId() {
        return docid;
    }

    public void setRecipeDocId(String recipeDocId) {
        this.docid = recipeDocId;
    }
}
