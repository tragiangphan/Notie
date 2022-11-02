package com.example.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment implements NoteClickListener {
    List<NoteModel> noteModels = new ArrayList<>();
    RecyclerView noteList;
    NoteItemsRecyclerView noteItemsRecyclerView;
    FloatingActionButton fabCreateNote;
    TextInputLayout searchBar;
    View view;
    MainActivity mainActivity;

    DatabaseReference databaseReference;

    public NotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notes, container, false);
        setControl(view);
        setEvent();
        return view;
    }

    private void setControl(View view) {
        noteList = view.findViewById(R.id.noteList);
        fabCreateNote = view.findViewById(R.id.fabCreateNote);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        searchBar = view.findViewById(R.id.inputSearch);
        mainActivity = (MainActivity) getActivity();
    }

    private void setEvent() {
        FetchingData();
        fabCreateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, CreateNoteActivity.class);
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
        databaseReference.child(StaticUtilities.getUsername(mainActivity)).child("noteModels").addValueEventListener(new ValueEventListener() {
            // saved change on database (each snapshot)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
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
        noteItemsRecyclerView = new NoteItemsRecyclerView(noteModels, NotesFragment.this);
        noteList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteList.setHasFixedSize(true);
        noteList.setAdapter(noteItemsRecyclerView);
    }


    @Override
    public void onClickItem(NoteModel noteModel) {
        Intent intent = new Intent(mainActivity, ViewNoteActivity.class);
        intent.putExtra(Constants.id, noteModel.getId());
        intent.putExtra(Constants.noteTitle, noteModel.getNoteTitle());
        intent.putExtra(Constants.noteSubtitle, noteModel.getNoteSubtitle());
        intent.putExtra(Constants.noteContent, noteModel.getNoteContent());
        intent.putExtra(Constants.createTime, noteModel.getCreateTime());
        intent.putExtra(Constants.imageURL, noteModel.getImageURL());
        startActivity(intent);
    }
}