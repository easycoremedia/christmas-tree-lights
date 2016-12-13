package com.easycore.ChristmasTreeLights.firebase;

import com.easycore.ChristmasTreeLights.entity.LightRequest;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ChangeWatcher {

    private ChangeHandler<LightRequest> changeHandler;

    public void setConfiguration(String configurationFilename) throws IOException, JSONException {
        byte[] configurationBytes = Files.readAllBytes(Paths.get(configurationFilename));
        JSONObject jsonData = new JSONObject(new String(configurationBytes, StandardCharsets.UTF_8));
        ByteArrayInputStream account = new ByteArrayInputStream(configurationBytes);

        String databaseUrl = String.format("https://%s.firebaseio.com", jsonData.get("project_id"));

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(account)
                .setDatabaseUrl(databaseUrl)
                .build();

        FirebaseApp.initializeApp(options);
    }

    public void setChangeHandler(ChangeHandler<LightRequest> changeHandler) {
        this.changeHandler = changeHandler;
    }

    public void watchRequests() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("request");

        ref.addChildEventListener(childEventListener);
    }

    private void handleRequestSnapshot(DataSnapshot dataSnapshot) {
        LightRequest lightRequest = null;
        try {
            lightRequest = dataSnapshot.getValue(LightRequest.class);
        } catch (com.google.firebase.database.DatabaseException ignored) {
        }

        if (handleLightRequest(lightRequest)) {
            dataSnapshot.getRef().setValue(lightRequest);
        }
    }

    private boolean handleLightRequest(@Nullable LightRequest lightRequest) {
        if (lightRequest == null || !lightRequest.shouldBeDisplayed()) {
            return false;
        }

        if (changeHandler != null) {
            changeHandler.onChange(lightRequest);
        }

        lightRequest.setDisplayedNow();
        return true;
    }

    private final ChildEventListener childEventListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            handleRequestSnapshot(dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            handleRequestSnapshot(dataSnapshot);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };
}
