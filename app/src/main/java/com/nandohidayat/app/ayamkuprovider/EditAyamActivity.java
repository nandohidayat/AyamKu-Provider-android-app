/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nandohidayat.app.ayamkuprovider;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.nandohidayat.app.ayamkuprovider.AyamListOpenHelper.CURRENT_SIZE;


/**
 * Activity for entering a new ayam or editing an existing one.
 */
public class EditAyamActivity extends AppCompatActivity {

    private static final int NO_ID = -99;
    private static final String NO_WORD = "";

    private EditText mEditNameView;
    private EditText mEditPriceView;
    private EditText mEditDescView;
    private EditText mEditImageView;

    // Unique tag for the intent reply.
    public static final String EXTRA_NAME = "com.app.android.ayamkuprovider.NAME";
    public static final String EXTRA_PRICE = "com.app.android.ayamkuprovider.PRICE";
    public static final String EXTRA_DESC = "com.app.android.ayamkuprovider.DESC";
    public static final String EXTRA_IMAGE = "com.app.android.ayamkuprovider.IMAGE";

    private static final String TAG = EditAyamActivity.class.getSimpleName();

    int mId = MainActivity.WORD_ADD;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ayam);

        mEditNameView = (EditText) findViewById(R.id.edit_name);
        mEditPriceView = (EditText) findViewById(R.id.edit_price);
        mEditDescView = (EditText) findViewById(R.id.edit_desc);
        mEditImageView = (EditText) findViewById(R.id.edit_image);

        // Get data sent from calling activity.
        Bundle extras = getIntent().getExtras();

        // If we are passed content, fill it in for the user to edit.
        if (extras != null) {
            int id = extras.getInt(AyamListAdapter.EXTRA_ID, NO_ID);
            String name = extras.getString(AyamListAdapter.EXTRA_WORD, NO_WORD);
            double price = extras.getDouble(AyamListAdapter.EXTRA_PRICE, 0.0);
            String desc = extras.getString(AyamListAdapter.EXTRA_DESC, NO_WORD);
            String image = extras.getString(AyamListAdapter.EXTRA_IMAGE, NO_WORD);
            if (id != NO_ID && !name.equals(NO_WORD)) {
                mId = id;
                mEditNameView.setText(name);
                mEditPriceView.setText(price + "");
                mEditDescView.setText(desc);
                mEditImageView.setText(image);
            }
        } // Otherwise, start with empty fields.
    }

     /**
      * Click handler for the Save button.
      * Creates a new intent for the reply, adds the reply message to it as an extra,
      * sets the intent result, and closes the activity.
      */
    public void returnReply(View view) {
        String name = ((EditText) findViewById(R.id.edit_name)).getText().toString();
        double price = Double.parseDouble(((EditText) findViewById(R.id.edit_price)).getText().toString());
        String desc = ((EditText) findViewById(R.id.edit_desc)).getText().toString();
        String image = ((EditText) findViewById(R.id.edit_image)).getText().toString();

        image = saveToInternalStorage(BitmapFactory.decodeFile(image));

        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_NAME, name);
        replyIntent.putExtra(EXTRA_PRICE, price);
        replyIntent.putExtra(EXTRA_DESC, desc);
        replyIntent.putExtra(EXTRA_IMAGE, image);
        replyIntent.putExtra(AyamListAdapter.EXTRA_ID, mId);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    public void pickImage(View view) {
        ImagePicker.create(EditAyamActivity.this)
                .language("in")
                .theme(R.style.ImagePickerTheme)
                .toolbarImageTitle("Tap to select")
                .toolbarDoneButtonText("DONE")
                .single()
                .showCamera(false)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        EditText image = (EditText) findViewById(R.id.edit_image);

        List<Image> images = ImagePicker.getImages(data);
        if(images != null && !images.isEmpty()) {
            image.setText(images.get(0).getPath());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
//        File directory = cw.getDir("assets", Context.MODE_WORLD_WRITEABLE);
        File directory = cw.getExternalFilesDir("media");
        // Create imageDir
        int id;
        if (mId == MainActivity.WORD_ADD) {
            id = CURRENT_SIZE;
        } else {
            id = mId;
        }
        File mypath = new File(directory, "ayam" + Integer.toString(id) + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }
}

