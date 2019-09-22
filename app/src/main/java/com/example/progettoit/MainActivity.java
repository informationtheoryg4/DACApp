package com.example.progettoit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import pl.droidsonroids.gif.GifImageView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.example.progettoit.Model.Message;
import com.example.progettoit.channelCoding.convolutionalCoding.Joint;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import static com.example.progettoit.channelCoding.ConcatenatedCoder.concatEncode;
import static com.example.progettoit.compression.Deflate.compress;
import static com.example.progettoit.compression.LZ4.lz4compress;
import static com.example.progettoit.utils.Utils.binaryToByteArray;
import static com.example.progettoit.utils.Utils.byteArrayToBinary;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final int SEND_CODE = 2;
    private final int BRANCH_SIZE = 1024;
    private final int BRANCH_SIZE2 = 32;
    private final int COMPRESS_SIZE = 64000;
    /** Durata wait **/
    private final int GIF_DISPLAY_LENGTH = 2000;
    private int STORAGE_PERMISSION_CODE=1;
    private static final int PICK_IMAGE = 100;
    GifImageView gif_image_deflate, lz4_gif_compression;
    ImageView imageView, imageCompressed;
    TextView textview, fileName;
    Uri imageUri;
    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2, write_msg;
    MenuItem register;
    MenuItem login;
    MenuItem logout, nav_gal, nav_msg;
    NavigationView navView;
    Menu menu;
    DrawerLayout dLayout;
    Button btn_compress, btn_send, btn_write;
    EditText editText;
    private int bufferSize;
    private byte [] byteArrayToSend;
    private boolean wiFi, lte, deflate, concat, convol;
    private int channelCoding;
    TextView username;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private byte[] comprMsg;
    private int compressedDataLength;
    private String senderUName, userId, uName;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private static final int SETTINGS_REQUEST = 3;
    private StorageTask uploadTask;
    private ProgressDialog pd;
    private String imageId;
    private boolean IMAGE_SEND;
    private static final int READ_REQUEST_CODE = 42;
    private static final int PICK_FROM_GALLERY = 1;
    private long t0, t1, t2, t3, t4;

    private final double ERROR_PROBABILITY = 0.01;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        storageReference = FirebaseStorage.getInstance().getReference("files/");
        deflate = true;
        imageCompressed = findViewById(R.id.imageCompressed);
        gif_image_deflate=findViewById(R.id.gif_image_deflate);
        lz4_gif_compression=findViewById(R.id.gif_lz4_compression);
        gif_image_deflate.setVisibility(View.INVISIBLE);
        lz4_gif_compression.setVisibility(View.INVISIBLE);
        fileName = findViewById(R.id.file_name);
        imageView = (ImageView) findViewById(R.id.imageView2);
        textview=(TextView) findViewById(R.id.textView3);
        write_msg = findViewById(R.id.write_msg);
        imageView.setVisibility(View.INVISIBLE);
        textview.setVisibility(View.INVISIBLE);
        imageCompressed.setVisibility(View.INVISIBLE);
        btn_write = findViewById(R.id.btn_write);
        editText=findViewById(R.id.editText);
        editText.setVisibility(View.INVISIBLE);
        pd = new ProgressDialog(this);
        pd.setMessage("Encoding message...");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        navView = (NavigationView) findViewById(R.id.nav_view);
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,dLayout, toolbar,R.string.app_name, R.string.app_name);
        dLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        menu = navView.getMenu();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        register = menu.findItem(R.id.nav_register);
        login = menu.findItem(R.id.nav_login);
        logout = menu.findItem(R.id.nav_logout);
        nav_gal = menu.findItem(R.id.nav_gallery);
        nav_msg = menu.findItem(R.id.nav_messages);
        btn_compress = findViewById(R.id.btn_compress);
        btn_send = findViewById(R.id.btn_send);
        textview.setMovementMethod(new ScrollingMovementMethod());
        if (firebaseUser!=null) {
            register.setVisible(false);
            login.setVisible(false);
            nav_gal.setVisible(true);
            nav_msg.setVisible(true);
            logout.setVisible(true);
            toolbar.setSubtitle(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        } else{
            register.setVisible(true);
            login.setVisible(true);
            logout.setVisible(false);
            nav_gal.setVisible(false);
            nav_msg.setVisible(false);
        }
        fab = findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        ==PackageManager.PERMISSION_GRANTED){
                    if(!fab1.isActivated()) {
                        fab1.setActivated(true);
                        fab2.setActivated(true);
                        write_msg.setActivated(true);
                        fab1.setVisibility(View.VISIBLE);
                        fab2.setVisibility(View.VISIBLE);
                        write_msg.setVisibility(View.VISIBLE);
                        fab1.setClickable(true);
                        fab2.setClickable(true);
                        write_msg.setClickable(true);
                    }
                    else {
                        fab1.setActivated(false);
                        fab2.setActivated(false);
                        write_msg.setActivated(false);
                        fab1.setVisibility(View.INVISIBLE);
                        fab2.setVisibility(View.INVISIBLE);
                        write_msg.setVisibility(View.INVISIBLE);
                        fab1.setClickable(false);
                        fab2.setClickable(false);
                        write_msg.setClickable(false);
                    }
                }else{
                    requestStoragePermission();
                }
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                fab1.setActivated(false);
                fab2.setActivated(false);
                write_msg.setActivated(false);
                fab1.setVisibility(View.INVISIBLE);
                fab2.setVisibility(View.INVISIBLE);
                write_msg.setVisibility(View.INVISIBLE);
                fab1.setClickable(false);
                fab2.setClickable(false);
                write_msg.setClickable(false);
                openGallery();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                fab1.setActivated(false);
                fab2.setActivated(false);
                write_msg.setActivated(false);
                fab1.setVisibility(View.INVISIBLE);
                fab2.setVisibility(View.INVISIBLE);
                write_msg.setVisibility(View.INVISIBLE);
                fab1.setClickable(false);
                fab2.setClickable(false);
                write_msg.setClickable(false);
                performFileSearch();
            }
        });
        write_msg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                fab1.setActivated(false);
                fab2.setActivated(false);
                write_msg.setActivated(false);
                fab1.setVisibility(View.INVISIBLE);
                fab2.setVisibility(View.INVISIBLE);
                write_msg.setVisibility(View.INVISIBLE);
                fab1.setClickable(false);
                fab2.setClickable(false);
                write_msg.setClickable(false);
                imageView.setVisibility(View.INVISIBLE);
                imageCompressed.setVisibility(View.INVISIBLE);
                fileName.setVisibility(View.INVISIBLE);
                btn_compress.setVisibility(View.INVISIBLE);
                btn_send.setVisibility(View.INVISIBLE);
                textview.setVisibility(View.INVISIBLE);
                editText.setVisibility(View.VISIBLE);
                btn_write.setVisibility(View.VISIBLE);
                editText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                btn_write.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textview.setVisibility(View.VISIBLE);
                        IMAGE_SEND = false;
                        textview.setText(editText.getText());
                        editText.setVisibility(View.INVISIBLE);
                        btn_write.setVisibility(View.INVISIBLE);
                        btn_compress.setVisibility(View.VISIBLE);
                        btn_send.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        btn_compress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Compressione
                t0 = System.currentTimeMillis();
                if(!IMAGE_SEND) { //Compressione testo
                    try {
                        comprMsg = textview.getText().toString().getBytes("Windows-1252");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(deflate)
                        gif_image_deflate.setVisibility(View.VISIBLE);
                    else
                        lz4_gif_compression.setVisibility(View.VISIBLE);
                    if(comprMsg.length<=COMPRESS_SIZE){
                        bufferSize=comprMsg.length+64;
                    } else{
                        bufferSize=COMPRESS_SIZE;
                    }
                    byteArrayToSend = deflate? compress(comprMsg, bufferSize):lz4compress(comprMsg, bufferSize);
                } else{ //Compressione immagine
                    if(deflate)
                        gif_image_deflate.setVisibility(View.VISIBLE);
                    else
                        lz4_gif_compression.setVisibility(View.VISIBLE);
                    if(comprMsg.length<=COMPRESS_SIZE){
                        bufferSize=comprMsg.length+64;
                    } else{
                        bufferSize=COMPRESS_SIZE;
                    }
                    byteArrayToSend = deflate? compress(comprMsg, bufferSize) : lz4compress(comprMsg, bufferSize);
                }
                t1 = System.currentTimeMillis();
                //Codifica di Canale
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(deflate)
                            gif_image_deflate.setVisibility(View.INVISIBLE);
                        else
                            lz4_gif_compression.setVisibility(View.INVISIBLE);
                        imageCompressed.setVisibility(View.VISIBLE);
                        textview.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.INVISIBLE);
                        btn_send.setVisibility(View.VISIBLE);
                        btn_compress.setVisibility(View.INVISIBLE);
                        pd.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(concat) {
                                    //Codici concatenati
                                    ArrayList<Byte> al = new ArrayList<Byte>();
                                    int i = 0;
                                    for (; i + BRANCH_SIZE <= byteArrayToSend.length; i += BRANCH_SIZE) {
                                        String tmp = concatEncode(byteArrayToBinary(Arrays.copyOfRange(byteArrayToSend, i, i + BRANCH_SIZE)));

                                        //introduco errori

                                        /*char[] err = tmp.toCharArray();
                                        int numErr = (int) Math.round(BRANCH_SIZE*ERROR_PROBABILITY);

                                        while(numErr>0){
                                            System.out.println("Errore introdotto.");
                                            numErr--;
                                            int e = (int) Math.random()*(BRANCH_SIZE-1);
                                            err[e]=err[e]=='0'?'1':'0';
                                        }

                                        tmp = new String(err);*/

                                        //introduco errori

                                        byte[] tmpByte = binaryToByteArray(tmp);

                                        for (byte b : tmpByte) {
                                            al.add(b);
                                        }
                                    }
                                    String tmp = concatEncode(byteArrayToBinary(Arrays.copyOfRange(byteArrayToSend, i, byteArrayToSend.length)));
                                    byte[] tmpByte = binaryToByteArray(tmp);
                                    for (byte b : tmpByte) {
                                        al.add(b);
                                    }
                                    byteArrayToSend = new byte[al.size()];
                                    for (int j = 0; j < al.size(); j++) {
                                        byteArrayToSend[j] = al.get(j);
                                    }
                                }else if(convol){
                                    // Codici convoluzionali
                                    ArrayList<Byte> al = new ArrayList<Byte>();
                                    Joint jt = new Joint("CodifEsempioLibro540.txt", 0.2);
                                    int i = 0;
                                    for (; i + BRANCH_SIZE2 <= byteArrayToSend.length; i += BRANCH_SIZE2) {
                                        String tmp = jt.CodeSeq(byteArrayToBinary(Arrays.copyOfRange(byteArrayToSend, i, i + BRANCH_SIZE2)));
                                        //introduco errori
                                        /*char[] err = tmp.toCharArray();
                                        double numErrProb = BRANCH_SIZE2*ERROR_PROBABILITY;
                                        int numErr=0;
                                        if(numErrProb>0.5) {
                                            numErr = (int) Math.round(numErrProb);
                                        }
                                        else {
                                            double random = Math.random();
                                            if(random<numErrProb)
                                                numErr=1;
                                            else
                                                numErr=0;
                                        }

                                        while(numErr>0){
                                            System.out.println("Errore introdotto.");
                                            numErr--;
                                            int e = (int) Math.random()*(BRANCH_SIZE2-1);
                                            err[e]=err[e]=='0'?'1':'0';
                                        }

                                        tmp = new String(err);*/

                                        //introduco errori
                                        byte[] tmpByte = binaryToByteArray(tmp);
                                        for (byte b : tmpByte) {
                                            al.add(b);
                                        }
                                        jt.resetEnc();
                                    }
                                    String tmp = jt.CodeSeq(byteArrayToBinary(Arrays.copyOfRange(byteArrayToSend, i, byteArrayToSend.length)));



                                    byte[] tmpByte = binaryToByteArray(tmp);
                                    for (byte b : tmpByte) {
                                        al.add(b);
                                    }
                                    byteArrayToSend = new byte[al.size()];
                                    for (int j = 0; j < al.size(); j++) {
                                        byteArrayToSend[j] = al.get(j);
                                    }
                                }
                                t2 = System.currentTimeMillis();
                                pd.dismiss();
                            }
                        }).start();
                    }
                }, GIF_DISPLAY_LENGTH);
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Gestione invio

                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if(firebaseUser!=null) {
                    Intent sendIntent = new Intent(MainActivity.this, UsersActivity2.class);
                    startActivityForResult(sendIntent, SEND_CODE);
                }else{
                    Toast.makeText(view.getContext(), "You must be logged in to send a file.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this).setTitle("Permission needed").setMessage("This permission is " +
                    "needed because of this and that").setPositiveButton("ok", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                }
            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }

    @SuppressLint("RestrictedApi")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]permissions, @NonNull int[]grantResults){
        if(requestCode==STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission GRANTED", Toast.LENGTH_SHORT).show();
                if(!fab1.isActivated()) {
                    fab1.setActivated(true);
                    fab2.setActivated(true);
                    fab1.setVisibility(View.VISIBLE);
                    fab2.setVisibility(View.VISIBLE);
                    write_msg.setVisibility(View.VISIBLE);
                    write_msg.setClickable(true);
                    fab1.setClickable(true);
                    fab2.setClickable(true);
                }
                else {
                    fab1.setActivated(false);
                    fab2.setActivated(false);
                    fab1.setVisibility(View.INVISIBLE);
                    fab2.setVisibility(View.INVISIBLE);
                    write_msg.setVisibility(View.INVISIBLE);
                    write_msg.setClickable(false);
                    fab1.setClickable(false);
                    fab2.setClickable(false);
                }
            }else{
                Toast.makeText(this,"Permission DENIED",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendMessage(String sender, String receiver, String message, String senderUserName, int length, boolean deflate, int channelCoding){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("senderUserName", senderUserName);
        hashMap.put("length", length);
        hashMap.put("deflate", deflate);
        hashMap.put("channelCoding", channelCoding);
        reference.child("Messages").push().setValue(hashMap);
    }

    private void sendImage(String sender, String receiver, String imageUrl, String senderUserName, int length, boolean deflate, int channelCoding){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", imageUrl);
        hashMap.put("senderUserName", senderUserName);
        hashMap.put("length", length);
        hashMap.put("deflate", deflate);
        hashMap.put("channelCoding", channelCoding);
        reference.child("Images").push().setValue(hashMap);
    }

    private void sendMessage(Message message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Messages").push().setValue(message);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings1:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(settingsIntent.putExtra("wiFi", wiFi).putExtra("lte", lte).putExtra("deflate", deflate).putExtra("concat", concat).putExtra("convol", convol), SETTINGS_REQUEST);
                return true;
            case R.id.action_settings2:
                openDialog();
                return true;
            case R.id.nav_login:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return true;
            case R.id.nav_register:
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                return true;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
                return true;
            case R.id.nav_messages:
                startActivity(new Intent(MainActivity.this, MessagesActivity.class));
                return true;
            case R.id.nav_gallery:
                startActivity(new Intent(MainActivity.this, ImagesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About");
        builder.setMessage("Project realized for Information Theory course at UNICAL (A.A.2018/19).\n" +
                "Students:\n"+ "Pierfrancesco D'Amico - 189243\n" +"Cosimo Loiero - 195328\n" +"Giovanni Aloia - 195325\n" +
                "Professor: Ing. De Rango Floriano\n"+"\n"+"Few suggestions to begin:\n"+"1. If you don't have already an account, please" +
                " submit your registration.\n"+"2. Login to your account to use DACApp\n"+"3. Enjoy!");
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            IMAGE_SEND = true;
            editText.setVisibility(View.INVISIBLE);
            btn_write.setVisibility(View.INVISIBLE);
            textview.setVisibility(View.INVISIBLE);
            imageCompressed.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            fileName.setVisibility(View.VISIBLE);
            imageView.setImageURI(imageUri);
            String path = getRealPathFromImageURI(this, imageUri);
            String filename = path.substring(path.lastIndexOf("/")+1);
            String file;
            if (filename.indexOf(".") > 0) {
                file = filename.substring(0, filename.lastIndexOf("."));
            } else {
                file =  filename;
            }
            Log.d(TAG, "Real Path: " + path);
            Log.d(TAG, "Filename With Extension: " + filename);
            Log.d(TAG, "File Without Extension: " + file);
            fileName.setText(filename);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis;
            try {
                fis = new FileInputStream(new File(path));
                byte[] buf = new byte[1024];
                int n;
                while (-1 != (n = fis.read(buf)))
                    baos.write(buf, 0, n);
            }catch(Exception e){
                e.printStackTrace();
            }
            comprMsg = baos.toByteArray();
            btn_compress.setVisibility(View.VISIBLE);
            btn_send.setVisibility(View.INVISIBLE);

        }

        else if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                try {
                    readTextFromUri(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            IMAGE_SEND = false;
            btn_compress.setVisibility(View.VISIBLE);
            btn_send.setVisibility(View.INVISIBLE);
        }

        else if(resultCode == RESULT_OK && requestCode == SEND_CODE) {
            t3 = System.currentTimeMillis();
            if(!IMAGE_SEND) {//Invio Testo
                if (data.hasExtra("senderUName")) {
                    senderUName = data.getStringExtra("senderUName");
                    userId = data.getStringExtra("userId");
                    uName = data.getStringExtra("uName");
                }
                String uId = firebaseUser.getUid();
                final ProgressDialog pd = new ProgressDialog(this);
                pd.setMessage("Sending file to " + uName + "...");
                pd.show();
                String timeName = String.valueOf(System.currentTimeMillis())+".dac";
                Message message = new Message(uId, userId, timeName, senderUName, bufferSize, deflate, channelCoding);
                channelCoding = 0;
                if(convol) channelCoding = 2;
                if(concat) channelCoding = 1;
                sendMessage(uId, userId, timeName, senderUName, bufferSize, deflate, channelCoding);
                UploadTask uploadTask = storageReference.child("messages/" + timeName).putBytes(byteArrayToSend);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        pd.dismiss();
                        t4 = System.currentTimeMillis();
                        double compr = (double)(t1-t0)/1000;
                        double coding = (double)((t2-t1)-GIF_DISPLAY_LENGTH)/1000;
                        double send = (double)(t4-t3)/1000;
                        System.out.println("Ritardo compressione: "+compr+
                                "\nRitardo codifica di canale: "+coding+
                                "\nRitardo invio: "+send);
                        Toast.makeText(MainActivity.this, "Message sended", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });
            } else{ //Invio immagine
                if (data.hasExtra("senderUName")) {
                    senderUName = data.getStringExtra("senderUName");
                    userId = data.getStringExtra("userId");
                    uName = data.getStringExtra("uName");
                }
                String uId = firebaseUser.getUid();
                final ProgressDialog pd = new ProgressDialog(this);
                pd.setMessage("Sending file to " + uName + "...");
                pd.show();
                //Utilizzo Storage di Firebase
                String timeName = String.valueOf(System.currentTimeMillis())+".dac";
                Message message = new Message(uId, userId, timeName, senderUName, bufferSize, deflate, channelCoding);
                channelCoding = 0;
                if(convol) channelCoding = 2;
                if(concat) channelCoding = 1;
                sendImage(uId, userId, timeName, senderUName, bufferSize, deflate, channelCoding);
                UploadTask uploadTask = storageReference.child("images/" + timeName).putBytes(byteArrayToSend);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        pd.dismiss();
                        t4 = System.currentTimeMillis();
                        double compr = (double)(t1-t0)/1000;
                        double coding = (double)((t2-t1)-GIF_DISPLAY_LENGTH)/1000;
                        double send = (double)(t4-t3)/1000;
                        System.out.println("Ritardo compressione: "+compr+
                                "\nRitardo codifica di canale: "+coding+
                                "\nRitardo invio: "+send);
                        Toast.makeText(MainActivity.this, "Message sended", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });

            }

        }
        else if(resultCode == RESULT_OK && requestCode == SETTINGS_REQUEST){
            wiFi = data.getBooleanExtra("wiFi", wiFi);
            lte = data.getBooleanExtra("lte", lte);
            deflate = data.getBooleanExtra("deflate", deflate);
            concat = data.getBooleanExtra("concat", concat);
            convol = data.getBooleanExtra("convol", convol);
            imageCompressed.setVisibility(View.INVISIBLE);
            btn_compress.setVisibility(View.INVISIBLE);
            btn_send.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            textview.setVisibility(View.INVISIBLE);
            fileName.setVisibility(View.INVISIBLE);
            editText.setVisibility(View.INVISIBLE);
            btn_write.setVisibility(View.INVISIBLE);
        }
    }

    private String getRealPathFromImageURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e(TAG, "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String selectionTxtType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
            String txtType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt");
            String[] proj = new String[]{txtType};
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(txtType);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e(TAG, "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream, "Windows-1252"));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        inputStream.close();
        imageView.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.INVISIBLE);
        btn_write.setVisibility(View.INVISIBLE);
        textview.setVisibility(View.VISIBLE);
        imageCompressed.setVisibility(View.INVISIBLE);
        textview.setText(stringBuilder.toString());
        fileName.setVisibility(View.VISIBLE);
        fileName.setText(uri.getLastPathSegment());
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_gallery) {
        } else if (id == R.id.nav_messages) {
        } else if (id == R.id.nav_login) {
        } else if (id == R.id.nav_logout) {
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
