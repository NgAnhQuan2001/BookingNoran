package com.aqsoft.bookingappnoran.AI;

public class FaceData {
    private float smilingProbability;
    private float leftEyeOpenProbability;
    private float rightEyeOpenProbability;

    public FaceData() {
        // Default constructor required for calls to DataSnapshot.getValue(FaceData.class)
    }

    public FaceData(float smilingProbability, float leftEyeOpenProbability, float rightEyeOpenProbability) {
        this.smilingProbability = smilingProbability;
        this.leftEyeOpenProbability = leftEyeOpenProbability;
        this.rightEyeOpenProbability = rightEyeOpenProbability;
    }

    public float getSmilingProbability() {
        return smilingProbability;
    }

    public float getLeftEyeOpenProbability() {
        return leftEyeOpenProbability;
    }

    public float getRightEyeOpenProbability() {
        return rightEyeOpenProbability;
    }
}
