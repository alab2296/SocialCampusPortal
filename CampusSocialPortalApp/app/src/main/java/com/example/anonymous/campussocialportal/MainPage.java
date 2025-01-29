package com.example.anonymous.campussocialportal;


import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainPage extends AppCompatActivity {
    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String current_user_id;

    private FloatingActionButton addPostBtn;

    private BottomNavigationView mainbottomNav;

    private HomeFragment homeFragment;
   // private AccountFragment accountFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;
   public DrawerLayout mdrawerLayout;
   private ActionBarDrawerToggle actionBarDrawerToggle;


    private CircleImageView blogUserImage;









  //  UserSessionManager session;
    TextView tname,temail;
    private void sendToLogin() {
       // session = new UserSessionManager(getApplicationContext());


      //  HashMap<String, String> user = session.getUserDetails();
        Intent loginIntent = new Intent(MainPage.this, LoginActivity.class);
       // String email = user.get(UserSessionManager.KEY_EMAIL);

       // loginIntent.putExtra("email",email);
        startActivity(loginIntent);
        finish();

    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){

            sendToLogin();

        } else {


            firebaseFirestore = FirebaseFirestore.getInstance();
                      current_user_id = mAuth.getCurrentUser().getUid();

           firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        if(!task.getResult().exists()){

                            Intent setupIntent = new Intent(MainPage.this, SetupActivity.class);
                            startActivity(setupIntent);
                            finish();

                        }

                    } else {

                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(MainPage.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();


                    }

                }
            });


        }

    }




    public void setUserData(String image,String name,String email){
        tname.setText(name);
        temail.setText(email);

        blogUserImage = findViewById(R.id.profile_image);

        RequestOptions placeholderOption = new RequestOptions();
        placeholderOption.placeholder(R.drawable.profile_placeholder);

        Glide.with(this).applyDefaultRequestOptions(placeholderOption).load(image).into(blogUserImage);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        mdrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,mdrawerLayout,R.string.open,R.string.close);
        mdrawerLayout.addDrawerListener(actionBarDrawerToggle);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        NavigationView mdrawer = (NavigationView) findViewById(R.id.nav) ;






        tname = (TextView) findViewById(R.id.nameheader);
        temail = (TextView) findViewById(R.id.emailheader);

        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupdrawercontent(mdrawer);






















        //FRAGMENT
       // android.support.v4.app.Fragment myfragment=null;
       // Class fragmentClass;
      //  fragmentClass=HomeFragment.class;
       // try{
        //    myfragment = (android.support.v4.app.Fragment) fragmentClass.newInstance();

      //  }catch (Exception e){
       //     e.printStackTrace();
       // }
       // FragmentManager fragmentManager = getSupportFragmentManager();
      //  fragmentManager.beginTransaction().replace(R.id.flowcontent,myfragment).commit();
        //FRAGMENT END

        if(mAuth.getCurrentUser() != null) {
            // FRAGMENTS
            homeFragment = new HomeFragment();
            notificationFragment = new NotificationFragment();
            accountFragment = new AccountFragment();

            initializeFragment();

            mainbottomNav = findViewById(R.id.mainBottomNav);

            mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    android.support.v4.app.Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                    switch (item.getItemId()) {

                        case R.id.bottom_action_home:

                            replaceFragment(homeFragment, currentFragment);
                            return true;

                        case R.id.bottom_action_account:

                            replaceFragment(accountFragment, currentFragment);
                            return true;

                        case R.id.bottom_action_notif:

                            replaceFragment(notificationFragment, currentFragment);
                            return true;

                        default:
                            return false;


                    }

                }
            });

            addPostBtn = findViewById(R.id.add_post_btn);
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent newPostIntent = new Intent(MainPage.this, NewPostActivity.class);
                    startActivity(newPostIntent);

                }
            });


            String user_id =mAuth.getCurrentUser().getUid();







                    firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        String userImage = task.getResult().getString("image");
                        String userName = task.getResult().getString("name");
                        String userEmail= mAuth.getCurrentUser().getEmail();


                       setUserData( userImage,userName,userEmail);


                    } else {

                        //Firebase Exception

                    }

                }
            });





        }




      //  session = new UserSessionManager(getApplicationContext());


       // HashMap<String, String> user = session.getUserDetails();

        // get name
     //   String name = user.get(UserSessionManager.KEY_NAME);

        // get email
       // String email = user.get(UserSessionManager.KEY_EMAIL);


        View header = mdrawer.getHeaderView(0);
        tname = (TextView) header.findViewById(R.id.nameheader);
        temail = (TextView) header.findViewById(R.id.emailheader);
          //  tname.setText(name);
          //  temail.setText(email);


       // if(session.checkLogin())
         //   finish();


    }



    public void selectitemdrawer(MenuItem menuItem){

        switch (menuItem.getItemId()){
            case R.id.myprofile:
                Intent intent = new Intent(MainPage.this,myprofile.class);
                startActivity(intent);
                break;
            case R.id.two:
                break;
            case R.id.zero:
                Intent i4 = new Intent(MainPage.this,pdfview.class);
                i4.putExtra("id", "timetable");
                startActivity(i4);
                break;
            case R.id.three:
                Intent i3 = new Intent(MainPage.this,pdfview.class);
                i3.putExtra("id", "attendance");
                startActivity(i3);
                break;
            case R.id.four:
                Intent i = new Intent(MainPage.this,pdfview.class);
                i.putExtra("id", "sessionals");
                startActivity(i);
                break;
            case R.id.five:
                Intent i2 = new Intent(MainPage.this,pdfview.class);
                i2.putExtra("id", "result");
                startActivity(i2);
                break;
            case R.id.six:
                Intent in = new Intent(MainPage.this,Selection.class);
                startActivity(in);
                break;
            case R.id.setting:
                Intent intent1 = new Intent(MainPage.this,SetupActivity.class);

                startActivity(intent1);
                break;
            case R.id.logout:

                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                              //  session.logoutUser();

                                mAuth.signOut();
                               // sendToLogin();
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                break;
                default:


        }




    }
    public void setupdrawercontent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectitemdrawer(item);
                return true;
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, homeFragment);
        fragmentTransaction.add(R.id.main_container, notificationFragment);
        fragmentTransaction.add(R.id.main_container, accountFragment);

        fragmentTransaction.hide(notificationFragment);
        fragmentTransaction.hide(accountFragment);

        fragmentTransaction.commit();

    }



    private void replaceFragment(android.support.v4.app.Fragment fragment, android.support.v4.app.Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == homeFragment){

            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(notificationFragment);

        }

        if(fragment == accountFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(notificationFragment);

        }

        if(fragment == notificationFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(accountFragment);

        }
        fragmentTransaction.show(fragment);

        //fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }







}
