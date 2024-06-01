package com.aqsoft.bookingappnoran.AI;

import com.aqsoft.bookingappnoran.AI.SimilarityClassifier;

import java.util.HashMap;

public interface OnDataReceivedCallback {
    void onDataReceived(HashMap<String, SimilarityClassifier.Recognition> data);
}
