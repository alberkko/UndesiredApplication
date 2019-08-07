package com.example.alber.undesiredapplication.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.alber.undesiredapplication.R;
import com.example.alber.undesiredapplication.activities.MainActivity;
import com.example.alber.undesiredapplication.model.Buildings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Buildings> mUploads;
    private OnItemClickListener mListener;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private boolean checkUid;


    public RecyclerViewAdapter(Context context, List<Buildings> uploads) {
        mContext = context;
        mUploads = uploads;


          // GET NAME OF THE CURRENT USER TO DISPLAY DELETE OR NOT
        //DELETE ONLY IF USER IS THE ADMIN (aaaName)


        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        if(mCurrentUser != null) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            DatabaseReference name = ref.child("Users").child(mCurrentUser.getUid()).child("name");
            name.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.getValue(String.class);

                    if (username.equals("aaaName")) {

                        checkUid = true;

                    } else {
                        checkUid = false;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


            });
        }


        //END OF  ---------
        //GET NAME OF THE CURRENT USER TO DISPLAY DELETE OR NOT
        //DELETE ONLY IF USER IS THE ADMIN (aaaName)
        // END OF ---------


    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.buildings_row_item, parent, false);
        return new ImageViewHolder(v);



    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Buildings uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getName());
        holder.textViewAddress.setText(uploadCurrent.getAddress());
        Picasso.get().load(mUploads.get(position).getImage_url()).fit().centerCrop().into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView textViewName;
        public ImageView imageView;
        public TextView textViewAddress;
        LinearLayout view_container;

        public ImageViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.building_name);
            imageView = itemView.findViewById(R.id.thumbnail);
            view_container = itemView.findViewById(R.id.container);
            textViewAddress = itemView.findViewById(R.id.address);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }


        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            if(checkUid =true && mCurrentUser != null){
                MenuItem delete = menu.add(Menu.NONE, 1, 1, "Delete");
                delete.setOnMenuItemClickListener(this);
            }
            else{
                //do nothing
            }


        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}
