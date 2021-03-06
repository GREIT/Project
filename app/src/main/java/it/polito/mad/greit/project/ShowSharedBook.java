package it.polito.mad.greit.project;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ShowSharedBook extends AppCompatActivity {
  
  private Toolbar t;
  private SharedBook sb;
  private boolean owned = false;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_show_book);
    
    Intent intent = getIntent();
    sb = (SharedBook) intent.getSerializableExtra("book");
    t = findViewById(R.id.show_book_toolbar);
    t.setTitle(R.string.show_book_title);
    setSupportActionBar(t);
    t.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
    t.setNavigationOnClickListener(v -> onBackPressed());
    
    if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(sb.getOwnerUid())) {
      //activate ask to borrow and star
      owned = false;
    } else {
      //activate edit and delete
      owned = true;
    }
    
    setup();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      if (owned) {
          //inflater.inflate(R.menu.show_sharedbook_menu, menu);
      } else {
          inflater.inflate(R.menu.show_sharedbook_menu, menu);
      }
      return true;
  }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

            if (R.id.book_show_action_borrow == item.getItemId()) {
            /*Context context = getApplicationContext();
            CharSequence text = "Borrow Asked!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();*/
                //openchat();
                Chat.openchat(ShowSharedBook.this,sb);
                return true;
            } else if (R.id.book_show_action_star == item.getItemId()) {
                Context context = getApplicationContext();
                CharSequence text = "Starred!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return true;
            } /*else if (R.id.book_card_action_delete == item.getItemId()) {
                Context context = getApplicationContext();
                CharSequence text = "Delete Asked!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return true;
            } else if (R.id.book_card_action_edit == item.getItemId()) {
            Intent intent = new Intent(ShowBookActivity.this, CompleteBookRegistration.class);
            intent.putExtra("book", sb);
            startActivity(intent);
                Context context = getApplicationContext();
                CharSequence text = "Edit Asked!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return true;
            } */else
                return super.onOptionsItemSelected(item);
        }
  
  public void setup() {
    TextView tv;
    Intent intent = getIntent();
    
    tv = findViewById(R.id.show_book_title);
    tv.setText(sb.getTitle());
    
    tv = findViewById(R.id.show_book_authors);
    StringBuilder authorsSB = new StringBuilder();
    for (String k : sb.getAuthors().keySet()) {
      authorsSB.append(k + ", ");
    }
    authorsSB.deleteCharAt(authorsSB.lastIndexOf(" "));
    authorsSB.deleteCharAt(authorsSB.lastIndexOf(","));
    tv.setText(authorsSB.toString());
    
    tv = findViewById(R.id.show_book_ISBN);
    tv.setText(sb.getISBN());
    
    tv = findViewById(R.id.show_book_publisher);
    tv.setText(sb.getPublisher());
    
    tv = findViewById(R.id.show_book_year);
    tv.setText(sb.getYear());
    
    ImageView iv = findViewById(R.id.show_book_pic);
    
    if (intent.hasExtra("pic")) {
      try {
        byte[] bytes = intent.getByteArrayExtra("pic");
        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = 300 * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        Bitmap scaled = Bitmap.createScaledBitmap(bm, (int) px, (int) px, false);
        iv.setImageBitmap(scaled);
      } catch (Exception e) {
        iv.setImageResource(R.drawable.ic_book_blue_grey_900_48dp);
      }
    } else if (sb.getKey() != null && !sb.getKey().isEmpty()) {
      StorageReference sr = FirebaseStorage.getInstance().getReference().child("shared_books_pictures/" + sb.getKey() + ".jpg");
      sr.getBytes(Constants.SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
        @Override
        public void onSuccess(byte[] bytes) {
          try {
            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Resources resources = getApplicationContext().getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float px = 300 * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            Bitmap scaled = Bitmap.createScaledBitmap(bm, (int) px, (int) px, false);
            iv.setImageBitmap(scaled);
          } catch (Exception e) {
            iv.setImageResource(R.drawable.ic_book_blue_grey_900_48dp);
          }
        }
      }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
          // Handle any errors
          iv.setImageResource(R.drawable.ic_book_blue_grey_900_48dp);
        }
      });
    } else {
        iv.setImageResource(R.drawable.ic_book_blue_grey_900_48dp);
    }
    
  }

}
