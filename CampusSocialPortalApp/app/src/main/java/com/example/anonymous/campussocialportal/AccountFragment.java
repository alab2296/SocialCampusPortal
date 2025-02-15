package com.example.anonymous.campussocialportal;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private RecyclerView blog_list_view;
    private List<BlogPost> blog_list;
    private List<User> user_list;
String user_id;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    public  View view;

    TextView tname,temail;



    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    private CircleImageView blogUserImage;



    public AccountFragment() {
        // Required empty public constructor
    }

    public void setUserData(String image,String name,String email){

       tname = view.findViewById(R.id.textViewname);
       temail = view.findViewById(R.id.textViewrollno);
        tname.setText(name);
        temail.setText(email);

        blogUserImage = view.findViewById(R.id.setup_image);

        RequestOptions placeholderOption = new RequestOptions();
        placeholderOption.placeholder(R.drawable.profile_placeholder);

        Glide.with(this).applyDefaultRequestOptions(placeholderOption).load(image).into(blogUserImage);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       view = inflater.inflate(R.layout.fragment_account, container, false);
        blog_list = new ArrayList<>();
        user_list = new ArrayList<>();
        blog_list_view = view.findViewById(R.id.blog_list_view_account);

        firebaseAuth = FirebaseAuth.getInstance();
        //String user_id =mAuth.getCurrentUser().getUid();

        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list,user_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blog_list_view.setAdapter(blogRecyclerAdapter);
        blog_list_view.setHasFixedSize(true);

        if(firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();
            user_id =firebaseAuth.getCurrentUser().getUid();







            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        String userImage = task.getResult().getString("image");
                        String userName = task.getResult().getString("name");
                        String userEmail= firebaseAuth.getCurrentUser().getEmail();


                        setUserData( userImage,userName,userEmail);


                    } else {

                        //Firebase Exception

                    }

                }
            });






            blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if(reachedBottom){

                        loadMorePost();

                    }

                }
            });

            Query firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("user_id",user_id).orderBy("timestamp", Query.Direction.DESCENDING).limit(3);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    try {

                        if (!documentSnapshots.isEmpty()) {

                            if (isFirstPageFirstLoad) {

                                lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                                blog_list.clear();
                                user_list.clear();

                            }

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {


                                    String blogPostId = doc.getDocument().getId();
                                    final BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                                    String blogUserId = doc.getDocument().getString("user_id");
                                    firebaseFirestore.collection("Users").document(blogUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {


                                                User user = task.getResult().toObject(User.class);

                                                if (isFirstPageFirstLoad) {
                                                    user_list.add(user);

                                                    blog_list.add(blogPost);

                                                } else {
                                                    user_list.add(0, user);

                                                    blog_list.add(0, blogPost);

                                                }
                                                blogRecyclerAdapter.notifyDataSetChanged();
                                                try {


                                                } catch (Exception e) {

                                                }

                                            }

                                        }
                                    });


                                }
                            }

                            isFirstPageFirstLoad = false;

                        }
                    }
                    catch (Exception ee){

                    }

                }

            });

        }

        // Inflate the layout for this fragment
        return view;
    }

    public void loadMorePost(){

        if(firebaseAuth.getCurrentUser() != null) {


            Query nextQuery = firebaseFirestore.collection("Posts").whereEqualTo("user_id",user_id)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);


            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    try {

                        if (!documentSnapshots.isEmpty()) {

                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String blogPostId = doc.getDocument().getId();
                                    final BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                                    String blogUserId = doc.getDocument().getString("user_id");
                                    firebaseFirestore.collection("Users").document(blogUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                User user = task.getResult().toObject(User.class);
                                                user_list.add(user);

                                                blog_list.add(blogPost);


                                                blogRecyclerAdapter.notifyDataSetChanged();

                                            }

                                        }
                                    });
                                }

                            }
                        }
                    }
                    catch ( Exception ee){

                    }






                }
            });

        }

    }

}

