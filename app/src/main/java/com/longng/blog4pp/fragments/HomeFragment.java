package com.longng.blog4pp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.longng.blog4pp.R;
import com.longng.blog4pp.adapters.BlogRecyclerAdapter;
import com.longng.blog4pp.models.BlogPostModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private RecyclerView blogListView;
    //Create list of model class type
    private List<BlogPostModel> blogList;
    private DocumentSnapshot lastVisible;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private BlogRecyclerAdapter blogRecycleAdapter;
    private boolean firstPageLoaded = true;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Get the recycler view
        blogListView = view.findViewById(R.id.blog_list_view);
        blogList = new ArrayList<>();
        blogRecycleAdapter = new BlogRecyclerAdapter(blogList);

        //Set adapter to RecyclerView
        blogListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        blogListView.setAdapter(blogRecycleAdapter);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            //Get last item scrolled in RecyclerView
            blogListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    boolean lastItem = !recyclerView.canScrollVertically(1);
                    if (lastItem) {
                        //Toast.makeText(getContext(),"end of 3 posts",Toast.LENGTH_SHORT).show();
                        loadMorePosts();
                    }
                }
            });

            if (mAuth.getCurrentUser() != null) {
                //Order according to date
                Query firstQuery = firebaseFirestore.collection("Posts")
                        .orderBy("timeStamp", Query.Direction.DESCENDING)
                        .limit(3);
                firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (firstPageLoaded) {
                            // Get the last visible documentSnapshot
                            lastVisible = documentSnapshots.getDocuments()
                                    .get(documentSnapshots.size() - 1);
                        }
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String BlogPostId = doc.getDocument().getId();
                                BlogPostModel blogPost = doc.getDocument().toObject(BlogPostModel.class).withId(BlogPostId);
                                if (firstPageLoaded)
                                    blogList.add(blogPost);
                                else
                                    blogList.add(0, blogPost);
                                blogRecycleAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                });

            }
        }
        // Inflate the layout for this fragment
        return view;
    }

    private void loadMorePosts() {
        if (mAuth.getCurrentUser() != null) {
            Query nextQuery = firebaseFirestore.collection("Posts")
                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);
            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    //If no more posts than documentSnapshots will be empty leading to crash
                    if (!documentSnapshots.isEmpty()) {
                        // Get the last visible documentSnapshot
                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() - 1);
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String BlogPostId = doc.getDocument().getId();
                                BlogPostModel blogPost = doc.getDocument().toObject(BlogPostModel.class).withId(BlogPostId);
                                blogList.add(blogPost);
                                blogRecycleAdapter.notifyDataSetChanged();
                            }
                        }

                    }

                }
            });
        }
    }
}