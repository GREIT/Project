package it.polito.mad.greit.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainEmptyActivity extends AppCompatActivity {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
      FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }catch (DatabaseException dbe){
      dbe.printStackTrace();
    }
   
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Intent intent;
  
    if (user == null) {
      intent = new Intent(this, SignInActivity.class);
    } else {
      DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("TOKENS").child(user.getUid());
      String token = FirebaseInstanceId.getInstance().getToken();
      dbref.setValue(token);
      Log.d("MyFirebaseIIDService", "onCreate: sent token " + token);
      intent = new Intent(this, MainActivity.class);
    }

    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
  }
}
