package com.example.notesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    MaterialToolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setControl();
        setEvent();
        //  Check First Item in Navbar
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_notes);
            switchFragment(new NotesFragment());
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
            case R.id.nav_notes:
                setTitle("Shared Notes");
                switchFragment(new NotesFragment());
                setTitle("All Notes");
                break;
            case R.id.nav_add_note:
                Intent editDirect = new Intent(MainActivity.this, CreateNoteActivity.class);
                startActivity(editDirect);
                break;
            case R.id.nav_share_note:
                setTitle("Shared Notes");
                switchFragment(new SharedFragment());
                setTitle("Shared Notes");
                break;
            case R.id.nav_lgout:
                Intent intent = new Intent(MainActivity.this, SignupLoginActivity.class);
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

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btnLogOut) {
            Intent intent = new Intent(MainActivity.this, SignupLoginActivity.class);
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
    }

    private void setControl() {
        setTitle("All Notes");
        toolbar = findViewById(R.id.toolbarNotes);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

}