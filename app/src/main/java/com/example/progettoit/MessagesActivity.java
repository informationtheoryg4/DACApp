package com.example.progettoit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.example.progettoit.Model.Message;
import com.example.progettoit.channelCoding.ConcatenatedDecoder;
import com.example.progettoit.channelCoding.convolutionalCoding.Joint;
import com.example.progettoit.utils.RequestPermissionHandler;
import com.example.progettoit.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import pl.droidsonroids.gif.GifImageView;

import static com.example.progettoit.channelCoding.ConcatenatedCoder.concatEncode;
import static com.example.progettoit.channelCoding.ConcatenatedDecoder.concatDecode;
import static com.example.progettoit.compression.Deflate.decompress;
import static com.example.progettoit.compression.LZ4.lz4decompress;
import static com.example.progettoit.utils.Utils.binaryToByteArray;
import static com.example.progettoit.utils.Utils.byteArrayToBinary;

public class MessagesActivity extends AppCompatActivity {


    private List<Message> myMessages;
    private List <String> msgsPreview, msgIds;
    DatabaseReference reference;
    private ListView listView;
    /** Duration of wait **/
    private final int GIF_DISPLAY_LENGTH = 2000;
    private final int BRANCH_SIZE = 4608;
    private final int BRANCH_SIZE2 = 96;
    private final long MAX_DIM_FILE = 1024*1024*500;
    GifImageView gif_image_inflate,lz4_gif_decompression;
    String ss;
    String decomprMsg;
    int count;
    TextView textView;
    Button save;
    String selectedForSave;
    private ProgressDialog progressDialog;
    private RequestPermissionHandler mRequestPermissionHandler;
    private int index;
    private String storageFileName;
    StorageReference storageReference;
    private ProgressDialog pd, pd1, pd2;
    private long t0, t1, t2, t3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        listView = findViewById(R.id.list_view_msg);
        setTitle("Received Messages");

        gif_image_inflate=findViewById(R.id.gif_image_inflate);
        gif_image_inflate.setVisibility(View.INVISIBLE);
        lz4_gif_decompression=findViewById(R.id.gif_lz4_decompression);
        lz4_gif_decompression.setVisibility(View.INVISIBLE);
        storageReference = FirebaseStorage.getInstance().getReference("files/messages/");

        mRequestPermissionHandler = new RequestPermissionHandler();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connecting..."); // Setting Message
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
        pd = new ProgressDialog(MessagesActivity.this);
        pd.setMessage("Loading message...");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);

        pd1 = new ProgressDialog(MessagesActivity.this);
        pd1.setMessage("Loading and decoding msg <Concatenated Codes>...");
        pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd1.setCancelable(false);

        pd2 = new ProgressDialog(MessagesActivity.this);
        pd2.setMessage("Loading and decoding msg <Convolutional Codes>...");
        pd2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd2.setCancelable(false);

        readMessage(FirebaseAuth.getInstance().getUid(), this);

        textView = findViewById(R.id.finalTextView);
        textView.setMovementMethod(new ScrollingMovementMethod());

        textView.setVisibility(View.INVISIBLE);
        save = findViewById(R.id.button_save);
        save.setVisibility(View.INVISIBLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Object clickItemObj = adapterView.getAdapter().getItem(i);

                String prev = clickItemObj.toString();



                //index = Integer.parseInt(prev.substring(7,8))-1;
                index = i;



                Message selectedMsg = myMessages.get(index);
                selectedForSave="Message_from_" +selectedMsg.getSenderUserName();
                confirmDialog(selectedMsg);

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save
                //uso la classe RequestPermissionHandler
                mRequestPermissionHandler.requestPermission(MessagesActivity.this, new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 123, new RequestPermissionHandler.RequestPermissionListener() { //il requestCode può essere un intero a caso
                    @Override
                    public void onSuccess() {
                        String file_name = selectedForSave+"(1).txt";
                        String text = textView.getText().toString();
                        //imposto cartella di destinazione "DOWNLOADS"
                        File textFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), file_name);
                        int incr=1;
                        while(textFile.exists()){
                            file_name = selectedForSave+"("+incr+")"+".txt";
                            textFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), file_name);
                            incr++;
                        }
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(textFile);
                            fos.write(text.getBytes());
                            Toast.makeText(MessagesActivity.this, "File Saved in Download folder!", Toast.LENGTH_LONG).show();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        reference.child(msgIds.get(index)).removeValue();
                        storageReference.child(storageFileName).delete();

                    }

                    @Override
                    public void onFailed() {
                        Toast.makeText(MessagesActivity.this, "Request permission failed", Toast.LENGTH_SHORT).show();
                    }
                });


                listView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.INVISIBLE);
                save.setVisibility(View.INVISIBLE);

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRequestPermissionHandler.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
    }



    private void confirmDialog(final Message m) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setMessage("Do you want to decompress this message?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        if(m.getChannelCoding()==0) {
                            pd.show();
                        }else if(m.getChannelCoding()==1){
                            pd1.show();
                        }else{
                            pd2.show();
                        }
                        final String s = m.getMessage();
                        t0 = System.currentTimeMillis();

                        storageReference.child(s).getBytes(1042*1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {

                                storageFileName = s;

                                byte[]bytes1=bytes;
                                t1 = System.currentTimeMillis();
                                //DECODIFICA DI CANALE
                                if (m.getChannelCoding()==1) {//CONCAT CODING
                                    ArrayList<Byte> al = new ArrayList<Byte>();
                                    int i = 0;
                                    for (; i + BRANCH_SIZE <= bytes.length; i += BRANCH_SIZE) {
                                        String tmp = concatDecode(byteArrayToBinary(Arrays.copyOfRange(bytes, i, i + BRANCH_SIZE)));
                                        byte[] tmpByte = binaryToByteArray(tmp);
                                        for (byte b : tmpByte) {
                                            al.add(b);
                                        }

                                    }
                                    System.out.println(i+" "+bytes.length);
                                    String tmp = concatDecode(byteArrayToBinary(Arrays.copyOfRange(bytes, i, bytes.length)));
                                    byte[] tmpByte = binaryToByteArray(tmp);
                                    for (byte b : tmpByte) {
                                        al.add(b);
                                    }

                                    bytes1 = new byte[al.size()];
                                    for (int j = 0; j < al.size(); j++) {
                                        bytes1[j] = al.get(j);
                                    }

                                }else if(m.getChannelCoding()==2){
                                    //convol
                                    ArrayList<Byte> al = new ArrayList<Byte>();
                                    Joint jt = new Joint("CodifEsempioLibro540.txt", 0.2);
                                    int i = 0;
                                    for (; i + BRANCH_SIZE2 <= bytes.length; i += BRANCH_SIZE2) {
                                        String tmp = jt.Decode(byteArrayToBinary(Arrays.copyOfRange(bytes, i, i + BRANCH_SIZE2)));
                                        byte[] tmpByte = binaryToByteArray(tmp);
                                        for (byte b : tmpByte) {
                                            al.add(b);
                                        }
                                        jt.resetEnc();
                                    }
                                    String tmp = jt.Decode(byteArrayToBinary(Arrays.copyOfRange(bytes, i, bytes.length)));
                                    byte[] tmpByte = binaryToByteArray(tmp);
                                    for (byte b : tmpByte) {
                                        al.add(b);
                                    }

                                    bytes1 = new byte[al.size()];
                                    for (int j = 0; j < al.size(); j++) {
                                        bytes1[j] = al.get(j);
                                    }
                                }

                                pd.dismiss();
                                pd1.dismiss();
                                pd2.dismiss();
                                if(m.isDeflate()){
                                    gif_image_inflate.setVisibility(View.VISIBLE);
                                }
                                else {
                                    lz4_gif_decompression.setVisibility(View.VISIBLE);
                                }
                                try {
                                    t2 = System.currentTimeMillis();
                                    if(m.isDeflate()){
                                        decomprMsg = new String( decompress(bytes1, m.getLength()), "Windows-1252");
                                    }
                                    else {
                                        decomprMsg = new String(lz4decompress(bytes1, m.getLength()), "Windows-1252");
                                    }

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                t3 = System.currentTimeMillis();
                                double download = (double)(t1-t0)/1000;
                                double decode = (double)(t2-t1)/1000;
                                double decompr = (double)(t3-t2)/1000;
                                System.out.println("Ritardo Download: "+download+
                                        "\nRitardo decodifica: "+decode+
                                        "\nRitardo decompressione: "+decompr+
                                        "\nErrori corretti: "+ ConcatenatedDecoder.errorCount);
                                ConcatenatedDecoder.errorCount=0;

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(m.isDeflate()){
                                            gif_image_inflate.setVisibility(View.INVISIBLE);
                                        }
                                        else {
                                            lz4_gif_decompression.setVisibility(View.INVISIBLE);
                                        }
                                        textView.setVisibility(View.VISIBLE);
                                        save.setVisibility(View.VISIBLE);
                                        listView.setVisibility(View.INVISIBLE);
                                        textView.setText(decomprMsg);

                                    }
                                },GIF_DISPLAY_LENGTH);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MessagesActivity.this, "Error", Toast.LENGTH_LONG).show();
                            }
                        });


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

    private void readMessage(final String myId, final Context context){
        myMessages = new ArrayList<>();
        msgsPreview = new ArrayList<>();
        msgIds = new ArrayList<>();

        count = 0;

        reference = FirebaseDatabase.getInstance().getReference("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                myMessages.clear();
                msgsPreview.clear();
                msgIds.clear();
                count=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    String receiver = snapshot.child("receiver").getValue().toString();
                    if (receiver.equals(myId)){
                        Message msg = snapshot.getValue(Message.class);
                        count++;

                        msgIds.add(snapshot.getKey());
                        myMessages.add(msg);
                        ss = "Message"+count;
                        String type = msg.isDeflate()? "Deflate" : "LZ4";
                        String chCod;
                        if(msg.getChannelCoding()==0)
                            chCod="";
                        else if(msg.getChannelCoding()==1)
                            chCod="<Concatenated>";
                        else
                            chCod="<Convolutional>";

                        msgsPreview.add(ss+"\nFrom: "+msg.getSenderUserName()+ " <"+type+"> "+chCod);
                    }


                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, msgsPreview);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
