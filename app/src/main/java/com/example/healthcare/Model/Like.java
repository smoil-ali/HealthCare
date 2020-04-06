package com.example.healthcare.Model;

public class Like {
    boolean Liked;

    public Like() {
    }

    public boolean isLiked() {
        return Liked;
    }

    public void setLiked(boolean liked) {
        Liked = liked;
    }

    public Like(boolean liked) {
        Liked = liked;
    }
}
