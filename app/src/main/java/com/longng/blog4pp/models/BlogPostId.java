package com.longng.blog4pp.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

//Extend class
public class BlogPostId {
    @Exclude
    public String BlogPostId;
    public <T extends BlogPostId> T withId(@NonNull final String id){
        this.BlogPostId = id;
        return (T) this;
    }
}
