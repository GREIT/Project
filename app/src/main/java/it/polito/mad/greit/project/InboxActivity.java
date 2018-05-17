package it.polito.mad.greit.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class InboxActivity extends AppCompatActivity {

    RecyclerView rv;
    ChatListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        Toolbar t;
        t = findViewById(R.id.inbox_toolbar);
        t.setTitle(R.string.inbox);
        setSupportActionBar(t);
        t.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        t.setNavigationOnClickListener(view -> onBackPressed());

        FirebaseUser fbu = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbref = db.getReference("USER_CHATS").child(fbu.getUid());

        rv = findViewById(R.id.list_inbox);

        adapter = new ChatListAdapter(this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(linearLayoutManager);

        rv.setAdapter(adapter);

        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Chat> chats = new ArrayList<>();
                chats.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    chats.add(ds.getValue(Chat.class));
                }
                //adapter.clear();
                adapter.addAll(chats);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ItemClickSupport.addTo(rv).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Chat c = adapter.get(position);
                openChat(c);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(InboxActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void openChat(Chat chat){
        Intent intent = new Intent(InboxActivity.this,ChatActivity.class);
        intent.putExtra("chat",chat);
        startActivity(intent);
    }

    public void deleteChat(Chat c,String user){
        //user is the current session FireBaseUser
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        db.getReference("USER_CHATS").child(user).child(c.getChatID()).removeValue();

        DatabaseReference dbref = db.getReference("USER_CHATS").child(c.getUserID()).child(c.getChatID());
        dbref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if(mutableData == null){
                    db.getReference("USER_MESSAGES").child(c.getChatID()).removeValue();
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }
}
