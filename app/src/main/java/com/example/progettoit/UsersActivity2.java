package com.example.progettoit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;



import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.progettoit.Model.Message;
import com.example.progettoit.Model.User;
import com.example.progettoit.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsersActivity2 extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<User> mUsers;
    private List<String> uNames;
    private ListView listView;
    private FirebaseUser fUser;
    private TextView textView;
    private String senderUName;
    private String msg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users2);
        listView = (ListView)findViewById(R.id.list_view_msg);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        textView = findViewById(R.id.textView3);

        mUsers = new ArrayList<>();
        uNames = new ArrayList<>();

        this.arrayAdapterListView();

    }

    // This method use an ArrayAdapter to add data in ListView.
    private void arrayAdapterListView()
    {

        readUsers(this);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Object clickItemObj = adapterView.getAdapter().getItem(index);
                //Toast.makeText(UsersActivity2.this, "You clicked " + clickItemObj.toString(), Toast.LENGTH_SHORT).show();
                confirmDialog(clickItemObj.toString());
            }
        });
    }

    private void confirmDialog(final String uName) {
        final String userId = mUsers.get(uNames.indexOf(uName)).getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setMessage("Do you want to send this message to "+uName+"?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        Intent data = new Intent();
                        data.putExtra("senderUName", senderUName);
                        data.putExtra("userId", userId);
                        data.putExtra("uName", uName);

                        setResult(RESULT_OK, data);
                        finish();

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

    private void sendMessage(Message message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Messages").push().setValue(message);

    }

    private void readUsers(final Context context) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                uNames.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    assert firebaseUser != null;
                    if(!user.getId().equals(firebaseUser.getUid())){
                        mUsers.add(user);
                        uNames.add(user.getUsername());
                    } else {
                        senderUName = user.getUsername();
                    }
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, uNames);
                listView.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendMessage(String sender, String receiver, String message, String senderUserName, int length){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("senderUserName", senderUserName);
        hashMap.put("length", length);

        reference.child("Messages").push().setValue(hashMap);

    }
}
