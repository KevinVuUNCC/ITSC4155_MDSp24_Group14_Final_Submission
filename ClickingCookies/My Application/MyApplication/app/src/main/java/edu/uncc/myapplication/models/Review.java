package edu.uncc.myapplication.models;

public class Review {

    String name;
    String text;
    String uid;
    String docid;

    float rating;

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getReviewdocid() {
        return reviewdocid;
    }

    public void setReviewdocid(String reviewdocid) {
        this.reviewdocid = reviewdocid;
    }


    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    String reviewdocid;

    public Review() {
    }

    public Review(String name, String text, String uid, String recipeDocId, float rating) {
        this.name = name;
        this.text = text;
        this.uid = uid;
        this.docid = recipeDocId;
        this.rating = rating;
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
