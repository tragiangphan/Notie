package com.example.notesapp;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

public class NoteItemsRecyclerView extends RecyclerView.Adapter {
    // Search notes
    Timer timer;
    List<NoteModel> noteModelsSource;
    // Show Notes
    List<NoteModel> noteModelList;
    NoteClickListener noteClickListener;
    int[] colors = {
            R.color.light_blue,
            R.color.light_red,
            R.color.light_green,
            R.color.light_orange,
            R.color.light_yellow
    };

    // Constructor RecyclerView
    public NoteItemsRecyclerView(List<NoteModel> noteModels, NoteClickListener noteClickListener) {
        this.noteModelList = noteModels;
        this.noteClickListener = noteClickListener;
        noteModelsSource = noteModels;
    }

    // set control
    public class NoteViewHolder extends RecyclerView.ViewHolder {

        public TextView noteTitle, noteSubtitle, noteContent, createTime;
        public ImageView noteImage;
        CardView noteCard;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteCard = itemView.findViewById(R.id.noteCard);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteSubtitle = itemView.findViewById(R.id.noteSubtitle);
            noteContent = itemView.findViewById(R.id.noteContent);
            createTime = itemView.findViewById(R.id.createTime);
            noteImage = itemView.findViewById(R.id.imgCard);
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_row, null);
        return new NoteViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        NoteViewHolder noteViewHolder = (NoteViewHolder) holder;

        // Random Card Color
        Random random = new Random();
        int randomNoteColor = random.nextInt(colors.length);
        noteViewHolder.noteCard.setCardBackgroundColor(holder.itemView.getResources().getColor(colors[randomNoteColor], null));

        // Setting into NoteModel attribute
        noteViewHolder.noteTitle.setText(noteModelList.get(position).getNoteTitle());
        noteViewHolder.noteSubtitle.setText(noteModelList.get(position).getNoteSubtitle());
        noteViewHolder.noteContent.setText(noteModelList.get(position).getNoteContent());
        noteViewHolder.createTime.setText(noteModelList.get(position).getCreateTime());
        noteViewHolder.noteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteClickListener.onClickItem(noteModelList.get(position));
            }
        });

        // retrieve image from firebase
        if (!Objects.equals(noteModelList.get(position).getImageURL(), "")) {
            Picasso.get()
                    .load(noteModelList.get(position).getImageURL())
                    .resize(350, 0)
                    .centerCrop()
                    .into(noteViewHolder.noteImage);
        }
    }

    @Override
    public int getItemCount() {
        return noteModelList.size();
    }

    // Search note
    public void searchNotes (String searchKey) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKey.trim().isEmpty()) {
                    noteModelList = noteModelsSource;
                } else {
                    ArrayList<NoteModel> temp = new ArrayList<>();
                    for (NoteModel note :
                            noteModelsSource) {
                        if (note.getNoteTitle().toLowerCase().contains(searchKey.toLowerCase()) ||
                        note.getNoteSubtitle().toLowerCase().contains(searchKey.toLowerCase()) ||
                        note.getNoteContent().toLowerCase().contains(searchKey.toLowerCase())) {
                            temp.add(note);
                        }
                    }
                    noteModelList = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 200);
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

}
