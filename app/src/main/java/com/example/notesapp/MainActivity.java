package com.example.notesapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteClickListener, NavigationView.OnNavigationItemSelectedListener {
    Context thisContext = MainActivity.this;

    List<NoteModel> noteModels = new ArrayList<>();
    RecyclerView noteList;
    FloatingActionButton fabCreateNote;
    DatabaseReference databaseReference;
    NoteItemsRecyclerView noteItemsRecyclerView;

    MaterialToolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextInputLayout searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setControl();
        FetchingData();
        setEvent();
        //  Check First Item in Navbar
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_notes);
        }
        if (thisContext == MainActivity.this) {
            navigationView.setCheckedItem(R.id.nav_notes);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_add_note:
                Intent editDirect = new Intent(MainActivity.this, CreateNoteActivity.class);
                startActivity(editDirect);
                break;
            case R.id.nav_lgout:
                Intent intent = new Intent(this, SignupLoginActivity.class);
                intent.putExtra(Constants.username, "");
                startActivity(intent);
                break;
            case R.id.nav_helps:
                Intent helpLink = new Intent(Intent.ACTION_VIEW);
                helpLink.setData(Uri.parse("https://creativecommons.org/terms"));
                startActivity(helpLink);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btnLogOut) {
            Intent intent = new Intent(this, SignupLoginActivity.class);
            intent.putExtra(Constants.username, "");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void ToggleNavbar() {
        setSupportActionBar(toolbar);
        // Show memubar icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        // Set open/close for drawer navigation
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(toggle);
        // Synchronized drawer navigation
        toggle.syncState();
    }

    private void setEvent() {
        ToggleNavbar();
        fabCreateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                startActivity(intent);
            }
        });
        SearchNotes();
    }

    private void SearchNotes() {
        searchBar.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                noteItemsRecyclerView.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (noteModels.size() != 0) {
                    noteItemsRecyclerView.searchNotes(editable.toString());
                }
            }
        });
    }

    private void FetchingData() {
        databaseReference.child(StaticUtilities.getUsername(MainActivity.this)).child("noteModels").addValueEventListener(new ValueEventListener() {
            // saved change on database (each snapshot)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    // query data for each attribute return strings
                    NoteModel noteModel = dataSnapshot.getValue(NoteModel.class);
                    noteModels.add(noteModel);
                }
                setAdapter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setAdapter() {
        noteItemsRecyclerView = new NoteItemsRecyclerView(noteModels, MainActivity.this);
        noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteList.setHasFixedSize(true);
        noteList.setAdapter(noteItemsRecyclerView);
    }

    private void setControl() {
        noteList = findViewById(R.id.noteList);
        fabCreateNote = findViewById(R.id.fabCreateNote);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        toolbar = findViewById(R.id.toolbarNotes);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        searchBar = findViewById(R.id.inputSearch);
    }

    @Override
    public void onClickItem(NoteModel noteModel) {
        Intent intent = new Intent(this, ViewNoteActivity.class);
        intent.putExtra(Constants.id, noteModel.getId());
        intent.putExtra(Constants.noteTitle, noteModel.getNoteTitle());
        intent.putExtra(Constants.noteSubtitle, noteModel.getNoteSubtitle());
        intent.putExtra(Constants.noteContent, noteModel.getNoteContent());
        intent.putExtra(Constants.createTime, noteModel.getCreateTime());
        intent.putExtra(Constants.imageURL, noteModel.getImageURL());
        startActivity(intent);
    }
}