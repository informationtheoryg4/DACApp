package Adapter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.progettoit.MainActivity;
import com.example.progettoit.Model.User;
import com.example.progettoit.R;
import com.example.progettoit.UsersActivity;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    String uName;

    public UserAdapter (Context mContext, List<User> mUsers){
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final User user = mUsers.get(position);
        holder.username_item.setText(user.getUsername());
        //holder.email_item.setText(user.getId());
        //if(user.getImageURL().equals("default")){
        //    holder.profile_image.setImageResource(R.mipmap.io_launcher);
        //} else{
        //    Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        //}

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(mContext, "Sending message to "+holder.username_item.getText().toString()+"...", Toast.LENGTH_LONG).show();
                //confirmDialog();


            }
        });
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder
                .setMessage("Are you sure?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Yes-code
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username_item, email_item;
        //public ImageView profile_image;

        public ViewHolder(View itemView){
            super(itemView);

            username_item =  itemView.findViewById(R.id.username_item);
            email_item = itemView.findViewById(R.id.email_item);
            //profile_image = itemView.findViewById(R.id.profile_image)
        }
    }
}
