package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class CreateNoteActivity extends AppCompatActivity {
    FloatingActionButton fabSave;
    EditText editTitle, editSubtitle, editContent;
    ActionBar actionBar;

    LinearLayout toolBigger, toolSmaller, toolUnderline, toolBold, toolItalic, toolStrike, toolAddPhoto, layoutAddImage;
    ImageView newImgView;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        setControl();
        setEvent();
    }

    private void setEvent() {
        turnBack();
        saveBtn();
        toolModified();
    }

    private void toolModified() {
        toolBigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float textSize = editContent.getTextSize();
                editContent.setTextSize(0, editContent.getTextSize() + 2.0f);
//                Toast.makeText(CreateNoteActivity.this, "Size is clicked", Toast.LENGTH_SHORT).show();
            }
        });

        toolSmaller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float textSize = editContent.getTextSize();
                editContent.setTextSize(0, editContent.getTextSize() - 2.0f);
//                Toast.makeText(CreateNoteActivity.this, "Size is clicked", Toast.LENGTH_SHORT).show();
            }
        });

        toolAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newImgView = new ImageView(CreateNoteActivity.this);
                layoutAddImage.addView(newImgView);
                openGallery();

//                Toast.makeText(CreateNoteActivity.this, "Add photo is clicked", Toast.LENGTH_SHORT).show();
            }
        });

        toolBold.setOnClickListener(new View.OnClickListener() {
            int flag3 = 0;
            @Override
            public void onClick(View view) {
                if (flag3 == 0) {
                    Typeface tf= Typeface.createFromAsset(getApplicationContext().getAssets(), "whitney_bold.otf");
                    editContent.setTypeface(tf);
                    flag3 = 1;
                } else if (flag3 == 1) {
                    Typeface tf= Typeface.createFromAsset(getApplicationContext().getAssets(), "whitney_medium.otf");
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
                    Typeface tf= Typeface.createFromAsset(getApplicationContext().getAssets(), "whitney_mediumitalic.otf");
                    editContent.setTypeface(tf);
                    flag4 = 1;
                } else if (flag4 == 1) {
                    Typeface tf= Typeface.createFromAsset(getApplicationContext().getAssets(), "whitney_medium.otf");
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
    }

    //select photo
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//mo ta thao tac get content
        //start activity
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);

    }

    //nhan ket qua tra ve tu activity tren
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            imageUri = data.getData();
            newImgView.setImageURI(imageUri);
//            imageAddPhoto.clearColorFilter();
        }
    }

    private void turnBack() {
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
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
                savedNote(
                        editTitle.getText().toString(),
                        editSubtitle.getText().toString(),
                        editContent.getText().toString());
            }
        });
    }

    private void savedNote(String title, String subtitle, String content) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(StaticUtilities.getUsername(CreateNoteActivity.this)).child("noteModels");
        String id = databaseReference.push().getKey();
        NoteModel noteModel = new NoteModel(id, title, subtitle, content, new Date().toString());
        databaseReference.child(id).setValue(noteModel);
        startActivity(new Intent(CreateNoteActivity.this, MainActivity.class));
    }

    private void setControl() {
        setTitle("Create Note");
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
    }
}