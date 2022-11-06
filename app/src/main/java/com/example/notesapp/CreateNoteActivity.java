package com.example.notesapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateNoteActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    FloatingActionButton fabSave;
    EditText editTitle, editSubtitle, editContent;
    MaterialToolbar materialToolbar;

    LinearLayout toolBigger, toolSmaller, toolUnderline, toolBold, toolItalic, toolStrike, toolAddPhoto, layoutAddImage, btnTakePhoto;
    ImageView newImgView;
    Uri imageUri;
    private static final int PERMISSION_CODE = 123;
    private static final int CAPTURE_CODE = 1001;

    ImageView imgReminder;
    TextView txtNoti;

    DatabaseReference noteDatabase;
    StorageReference imageStorage;
    ArrayList<String> sharer = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        setDatabase();
        setControl();
        setEvent();
    }

    private void setDatabase() {
        imageStorage = FirebaseStorage.getInstance().getReference("images");
        noteDatabase = FirebaseDatabase.getInstance().getReference("users").child(StaticUtilities.getUsername(CreateNoteActivity.this)).child("noteModels");
    }

    private void setControl() {
        setTitle("Create Note");
        materialToolbar = findViewById(R.id.toolbarCreate);
        fabSave = findViewById(R.id.fabSavedNote);
        editTitle = findViewById(R.id.noteTitle);
        editSubtitle = findViewById(R.id.editNoteSubtitle);
        editContent = findViewById(R.id.noteContent);
        toolBigger = findViewById(R.id.btnBigger);
        toolSmaller = findViewById(R.id.btnSmaller);
        toolUnderline = findViewById(R.id.btnUnderline);
        toolBold = findViewById(R.id.btnBold);
        toolItalic = findViewById(R.id.btnItalic);
        toolStrike = findViewById(R.id.btnStrikethrough);
        toolAddPhoto = findViewById(R.id.btnAddPhoto);
        layoutAddImage = findViewById(R.id.layoutAddImage);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        imgReminder = findViewById(R.id.imgReminder);
        txtNoti = findViewById(R.id.txtNoti);
    }

    private void setEvent() {
        turnBack();
        saveBtn();
        toolModified();
        showReminder();
    }

    private void showReminder() {
        imgReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(CreateNoteActivity.this, MainActivity2.class));
//                Toast.makeText(CreateNoteActivity.this, "Reminder clicked", Toast.LENGTH_SHORT).show();
                DialogFragment timePicker2 = new TimePickerFragment();
                timePicker2.show(getSupportFragmentManager(), "time picker");

            }
        });
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        updateTimeText(c);
        startAlarm(c);
    }

    private void updateTimeText(Calendar c) {
        String timeText = "Alarm set for: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        txtNoti.setText(timeText);
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notiIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, notiIntent, PendingIntent.FLAG_MUTABLE);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

    }

    private void turnBack() {
        setSupportActionBar(materialToolbar);
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveBtn() {
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharer.add(StaticUtilities.getUsername(CreateNoteActivity.this));
                saveNoteDetail();
            }
        });
    }

    private void saveNoteDetail() {
        if (imageUri != null) {
            StorageReference storageReference1 = imageStorage.child(System.currentTimeMillis() + "." + GetFileExtension(imageUri));
            storageReference1.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String id = noteDatabase.push().getKey();

                                    @SuppressLint("SimpleDateFormat") NoteModel noteModel = new NoteModel(id,
                                            editTitle.getText().toString(),
                                            editSubtitle.getText().toString(),
                                            editContent.getText().toString(),
                                            new SimpleDateFormat("MMM dd yyyy HH:mm").format(new Date()),
                                            uri.toString(),
                                            sharer);

                                    assert id != null;
                                    noteDatabase.child(id).setValue(noteModel);
                                    startActivity(new Intent(CreateNoteActivity.this, MainActivity.class));
                                }
                            });
                        }
                    });
        } else {
            // set data for new note model without image
            String id = noteDatabase.push().getKey();
            @SuppressLint("SimpleDateFormat") NoteModel noteModel = new NoteModel(id,
                    editTitle.getText().toString(),
                    editSubtitle.getText().toString(),
                    editContent.getText().toString(),
                    new SimpleDateFormat("MMM dd yyyy HH:mm").format(new Date()),
                    "",
                    sharer);
            assert id != null;
            noteDatabase.child(id).setValue(noteModel);
            startActivity(new Intent(CreateNoteActivity.this, MainActivity.class));
        }

    }

    private void toolModified() {
        toolBigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float textSize = editContent.getTextSize();
                editContent.setTextSize(0, editContent.getTextSize() + 2.0f);
            }
        });

        toolSmaller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float textSize = editContent.getTextSize();
                editContent.setTextSize(0, editContent.getTextSize() - 2.0f);
            }
        });

        toolAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newImgView = new ImageView(CreateNoteActivity.this);
                layoutAddImage.addView(newImgView);
                openGallery();
            }
        });

        toolBold.setOnClickListener(new View.OnClickListener() {
            int flag3 = 0;

            @Override
            public void onClick(View view) {
                if (flag3 == 0) {
                    Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "whitney_bold.otf");
                    editContent.setTypeface(tf);
                    flag3 = 1;
                } else if (flag3 == 1) {
                    Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "whitney_medium.otf");
                    editContent.setTypeface(tf);
                    flag3 = 0;
                }

            }
        });

        toolItalic.setOnClickListener(new View.OnClickListener() {
            int flag4 = 0;

            @Override
            public void onClick(View view) {
                if (flag4 == 0) {
                    Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "whitney_mediumitalic.otf");
                    editContent.setTypeface(tf);
                    flag4 = 1;
                } else if (flag4 == 1) {
                    Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "whitney_medium.otf");
                    editContent.setTypeface(tf);
                    flag4 = 0;
                }
            }
        });

        toolUnderline.setOnClickListener(new View.OnClickListener() {
            int flag5 = 0;

            @Override
            public void onClick(View view) {
                if (flag5 == 0) {
                    editContent.setPaintFlags(editContent.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    flag5 = 1;
                } else if (flag5 == 1) {
                    editContent.setPaintFlags(View.INVISIBLE);
                    flag5 = 0;
                }
            }
        });

        toolStrike.setOnClickListener(new View.OnClickListener() {
            int flag6 = 0;

            @Override
            public void onClick(View view) {
                if (flag6 == 0) {
                    editContent.setPaintFlags(editContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    flag6 = 1;
                } else if (flag6 == 1) {
                    editContent.setPaintFlags(View.INVISIBLE);
                    flag6 = 0;
                }
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newImgView = new ImageView(CreateNoteActivity.this);
                layoutAddImage.addView(newImgView);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_DENIED
                    ) {
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

                        requestPermissions(permission, PERMISSION_CODE);
                    } else {
                        openCamera();
                    }
                } else {
                    openCamera();
                }
            }
        });
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "new image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(camIntent, CAPTURE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    //  select photo
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        //  mo ta thao tac get content
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //  start activity
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
    }

    //  nhan ket qua tra ve tu activity tren
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            imageUri = data.getData();
            newImgView.setImageURI(imageUri);
        }
        if (resultCode == RESULT_OK && requestCode == CAPTURE_CODE) {
            newImgView.setImageURI(imageUri);
        }
    }

    private String GetFileExtension(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }
}