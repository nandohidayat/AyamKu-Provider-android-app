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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static com.nandohidayat.app.ayamkuprovider.Contract.CONTENT_PATH;
import static com.nandohidayat.app.ayamkuprovider.Contract.CONTENT_URI;

/**
 * Simple Adapter for a RecyclerView with click handler for each item in the ViewHolder.
 */
public class AyamListAdapter extends RecyclerView.Adapter<AyamListAdapter.AyamViewHolder> {

    class AyamViewHolder extends RecyclerView.ViewHolder {
        public final TextView ayamItemView;
        Button delete_button;
        Button edit_button;

        public AyamViewHolder(View itemView) {
            super(itemView);
            ayamItemView = (TextView) itemView.findViewById(com.nandohidayat.app.ayamkuprovider.R.id.ayam);
            delete_button = (Button)itemView.findViewById(R.id.delete_button);
            edit_button = (Button)itemView.findViewById(R.id.edit_button);
        }
    }

    public static final String EXTRA_ID = "ID";
    public static final String EXTRA_WORD = "NAME";
    public static final String EXTRA_PRICE = "PRICE";
    public static final String EXTRA_DESC = "DESC";
    public static final String EXTRA_IMAGE = "IMAGE";
    public static final String EXTRA_POSITION = "POSITION";

    private static final String TAG = AyamListAdapter.class.getSimpleName();

    // Query parameters are very similar to SQL queries.
    private String queryUri = CONTENT_URI.toString();
    private static final String[] projection = new String[] {CONTENT_PATH};
    private String selectionClause = null;
    private String selectionArgs[] = null;
    private String sortOrder = "ASC";

    private final LayoutInflater mInflater;
    private Context mContext;

    public AyamListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @Override
    public AyamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.wordlist_item, parent, false);
        return new AyamViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(AyamViewHolder holder, int position) {
        // Create a reference to the view holder for the click listener
        // Must be final for use in callback
        final AyamViewHolder h = holder;

        String name = "";
        double price = 0;
        String desc = "";
        String image = "";
        int id = -1;

        // Position != id !!!
        // position == index == row; can't get nth row, so have to get all and then pick row
        Cursor cursor =
                mContext.getContentResolver().query(Uri.parse(
                        queryUri), null, null, null, sortOrder);
        if (cursor != null) {
            if (cursor.moveToPosition(position)) {
                int indexWord = cursor.getColumnIndex(Contract.AyamList.KEY_NAME);
                int indexPrice = cursor.getColumnIndex(Contract.AyamList.KEY_PRICE);
                int indexDesc = cursor.getColumnIndex(Contract.AyamList.KEY_DESC);
                int indexImage = cursor.getColumnIndex(Contract.AyamList.KEY_IMAGE);
                name = cursor.getString(indexWord);
                price = cursor.getDouble(indexPrice);
                desc = cursor.getString(indexDesc);
                image = cursor.getString(indexImage);
                holder.ayamItemView.setText(name);
                int indexId = cursor.getColumnIndex(Contract.AyamList.KEY_ID);
                id = cursor.getInt(indexId);
            } else {
                holder.ayamItemView.setText(R.string.error_no_word);
            }
            cursor.close();
        } else {
            Log.e (TAG, "onBindViewHolder: Cursor is null.");
        }

        // Attach a click listener to the DELETE button
        holder.delete_button.setOnClickListener(new MyButtonOnClickListener(id, name, price, desc, image) {

            @Override
            public void onClick(View v) {
                selectionArgs = new String[]{Integer.toString(id)};
                int deleted = mContext.getContentResolver().delete(CONTENT_URI, CONTENT_PATH,
                        selectionArgs);
                if (deleted > 0) {
                    // Need both calls
                    notifyItemRemoved(h.getAdapterPosition());
                    notifyItemRangeChanged(h.getAdapterPosition(), getItemCount());
                } else {
                    Log.d (TAG, mContext.getString(R.string.not_deleted) + deleted);
                }
            }
        });

        // Attach a click listener to the EDIT button
        holder.edit_button.setOnClickListener(new MyButtonOnClickListener(id, name, price, desc, image) {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditAyamActivity.class);

                intent.putExtra(EXTRA_ID, id);
                intent.putExtra(EXTRA_POSITION, h.getAdapterPosition());
                intent.putExtra(EXTRA_WORD, name);
                intent.putExtra(EXTRA_PRICE, price);
                intent.putExtra(EXTRA_DESC, desc);
                intent.putExtra(EXTRA_IMAGE, image);

                ((Activity) mContext).startActivityForResult(intent, MainActivity.WORD_EDIT);
            }
        });
    }

    @Override
    public int getItemCount() {
        Cursor cursor =
                mContext.getContentResolver().query(
                        Contract.ROW_COUNT_URI, new String[] {"count(*) AS count"},
                        selectionClause, selectionArgs, sortOrder);
        try {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        }
        catch (Exception e){
            Log.d(TAG, "EXCEPTION getItemCount: " + e);
            return -1;
        }
    }
}



