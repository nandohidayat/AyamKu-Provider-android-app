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

import android.view.View;

/**
 * Instantiated for the Edit and Delete buttons in AyamListAdapter.
 */
public class MyButtonOnClickListener implements View.OnClickListener {
    private static final String TAG = View.OnClickListener.class.getSimpleName();

    int id;
    String name;
    double price;
    String desc;
    String image;

    public MyButtonOnClickListener(int id, String name, double price, String desc, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.image = image;
    }

    public void onClick(View v) {
        // Implemented in AyamListAdapter
    }
}
