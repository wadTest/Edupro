package com.prospec.edupro;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.prospec.edupro.Utils.UserParcelable;

//class Main
public class MainActivity extends AppCompatActivity
//    ประกาศตัวแปร โดยมีเครื่องมือ Navigation (เบอร์เกอร์)
        implements NavigationView.OnNavigationItemSelectedListener {
    private int ident;
    private UserParcelable user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//       Add Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Add Navigation ( class นี่เชื่อมโยงกับ nav_header_main.xml)
        View header = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        ImageView photo = (ImageView) header.findViewById(R.id.image_menu);

        try {
            Bundle bundle = getIntent().getExtras();
            user = bundle.getParcelable("DATA_USER");
            if (bundle != null) {
                ident = user.getId();
                // ชื่อผู้ใช้งาน
                ((TextView) header.findViewById(R.id.tv_nombre_user_nav_header)).setText(user.getNombre());
                // อีเมล์ผู้ัใช้งาน
                ((TextView) header.findViewById(R.id.tv_email_user_nav_header)).setText(user.getEmail());
                if (!user.getImage().equals("no image")) {
                    String url_image = "http://119.59.103.121/app_mobile/edupro/" + user.getImage();
                    url_image = url_image.replace(" ", "%20");
                    try {
//                        เช็ครูปภาพ
                        Log.i("IMAGE: ", "" + url_image);
                        Glide.with(this).load(url_image).into(photo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        fab ของ emailยังไม่ใช้งาน
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        รูปที่จะนำไปแสดงในแถบเครื่องมือ เบอร์เกอร์ drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

//    menu item
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //    ส่วนของmenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // ขยายเมนู สิ่งนี้จะเพิ่มรายการในแถบการกระทำหากมีอยู่
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // จัดการการคลิกรายการแถบการกระทำที่นี่ แถบแอ็คชั่นจะ
        // จัดการการคลิกที่ปุ่ม Home / Up โดยอัตโนมัตินาน ๆ
        //ตามที่คุณระบุผู้ปกครอง activity ใน AndroidManifest.xml.
        int id = item.getItemId();

        //menu settings
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // จัดการรายการคลิกมุมมองการนำทางที่นี่
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // จัดการกับการเคลื่อนไหวของกล้อง
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}//Main Class