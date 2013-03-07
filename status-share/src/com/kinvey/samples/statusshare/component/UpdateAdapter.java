package com.kinvey.samples.statusshare.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.kinvey.samples.statusshare.R;
import com.kinvey.samples.statusshare.model.Update;

import java.util.List;

public class UpdateAdapter extends ArrayAdapter<Update> {

    private LayoutInflater mInflater;


    public UpdateAdapter(Context context, List<Update> objects,
                         LayoutInflater inf) {
        // NOTE: I pass an arbitrary textViewResourceID to the super
        // constructor-- Below I override
        // getView(...), which causes the underlying adapter to ignore this
        // field anyways, it is just needed in the constructor.
        super(context, R.id.text, objects);
        this.mInflater = inf;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UpdateViewHolder holder = null;

        ImageView mAvatar = null;
        TextView mBlurb = null;
        TextView mAuthor = null;
        TextView mWhen = null;
        ImageView mAttachment = null;

        Update rowData = getItem(position);

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.row_status_share, null);
            holder = new UpdateViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder = (UpdateViewHolder) convertView.getTag();

        if (rowData.getAvatar() != null){
            mAvatar = holder.getAvatar();
            mAvatar.setImageBitmap(rowData.getAvatar());
        }

        if (rowData.getText() != null){
            mBlurb = holder.getBlurb();
            mBlurb.setText(rowData.getText());
        }

        if (rowData.getAuthorName() != null){
            mAuthor = holder.getAuthor();
            mAuthor.setText(rowData.getAuthorName());
        }
        if (rowData.getSince() != null){
            mWhen = holder.getWhen();
            mWhen.setText(rowData.getSince());

        }
//        if (rowData.getThumbnail() != null){
//            mAttachment = holder.getAttachment();
//            mAttachment.setImageBitmap(rowData.getThumbnail());
//
//        }




        return convertView;
    }


    /**
     * This pattern is used as an optimization for Android ListViews.
     * <p/>
     * Since every row uses the same layout, the View object itself can be
     * recycled, only the data/content of the row has to be updated.
     * <p/>
     * This allows for Android to only inflate enough Row Views to fit on
     * screen, and then they are recycled. This allows us to avoid creating
     * a new view for every single row, which can have a negative effect on
     * performance (especially with large lists on large screen devices).
     */
    private class UpdateViewHolder {
        private View mRow;

        private ImageView mAvatar = null;
        private TextView mBlurb = null;
        private TextView mAuthor = null;
        private TextView mWhen = null;
//        private ImageView mAttachment = null;

        public UpdateViewHolder(View row) {
            mRow = row;
        }

//        public ImageView getAttachment() {
//            if (null == mAttachment) {
//                mAttachment = (ImageView) mRow.findViewById(R.id.attachment);
//            }
//            return mAttachment;
//        }

        public TextView getWhen() {
            if (null == mWhen) {
                mWhen = (TextView) mRow.findViewById(R.id.when);
            }
            return mWhen;
        }

        public TextView getAuthor() {
            if (null == mAuthor) {
                mAuthor = (TextView) mRow.findViewById(R.id.author);
            }
            return mAuthor;
        }

        public TextView getBlurb() {
            if (null == mBlurb) {
                mBlurb = (TextView) mRow.findViewById(R.id.text);
            }
            return mBlurb;
        }

        public ImageView getAvatar() {
            if (null == mAvatar) {
                mAvatar = (ImageView) mRow.findViewById(R.id.avatar);
            }
            return mAvatar;
        }
    }
}