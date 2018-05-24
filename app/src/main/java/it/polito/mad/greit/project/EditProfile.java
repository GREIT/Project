package it.polito.mad.greit.project;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfile extends AppCompatActivity {

  private Toolbar t;
  private Profile profile;
  private CircleImageView ciw;
  private byte[] photo = null;
  private String coordinates=null;
  private String location=null;
 // boolean def = false;


  @Override
  protected void onCreate(Bundle b) {
    super.onCreate(b);
    setContentView(R.layout.activity_edit_profile);

    t = findViewById(R.id.edit_profile_toolbar);
    t.setTitle(R.string.activity_edit_profile);
    setSupportActionBar(t);
    t.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
    t.setNavigationOnClickListener(v -> RevertInfo());

    Setup(b);

    ciw = findViewById(R.id.edit_pic);
    ciw.setOnClickListener(v -> pic_action());

    PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
            getFragmentManager().findFragmentById(R.id.edit_location);

    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
      @Override
      public void onPlaceSelected(Place place) {
        location = place.getAddress().toString();
        coordinates = place.getLatLng().latitude + ";" + place.getLatLng().longitude;
      }

      @Override
      public void onError(Status status) {

      }
    });

    //setuplocation();

  }

  public static void hideKeyboard(Activity activity) {
    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    //Find the currently focused view, so we can grab the correct window token from it.
    View view = activity.getCurrentFocus();
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
      view = new View(activity);
    }
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  public  void setuplocation() {

    /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && !def) {
      ActivityCompat.requestPermissions(EditProfile.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.FINE_LOCATION_PERMISSION);
      ActivityCompat.requestPermissions(EditProfile.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.COARSE_LOCATION_PERMISSION);
    } else {

      try {*/

       /* if(def){
          northEast = new LatLng(41.5 + radiusDegrees, 12.3 + radiusDegrees);
          southWest = new LatLng(41.5 - radiusDegrees, 12.3 - radiusDegrees);
          Log.d("POSPOSPOS", "setuplocation: DEFAULT");
        }else{
          LocationManager mLocationManager;
          Location myLocation;

          mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
          List<String> providers = mLocationManager.getProviders(true);
          Location bestLocation = null;
          for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
              continue;
            }
            if (bestLocation == null || l.getAccuracy() > bestLocation.getAccuracy()) {
              bestLocation = l;
            }
          }*/

    double radiusDegrees = 30;
    LatLng northEast,southWest;
    northEast = new LatLng(41.5 + radiusDegrees, 12.3 + radiusDegrees);
    southWest = new LatLng(41.5 - radiusDegrees, 12.3 - radiusDegrees);
    LatLngBounds bounds = LatLngBounds.builder().include(northEast).include(southWest).build();
    final ArrayAdapter<String> autoComplete = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
    AutoCompleteTextView ACTV = null;//findViewById(R.id.edit_location);
    ACTV.setAdapter(autoComplete);
    HashMap<String,String> place_ids = new HashMap<>();
    ACTV.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Task<AutocompletePredictionBufferResponse> results =
                Places.getGeoDataClient(EditProfile.this)
                        .getAutocompletePredictions(charSequence.toString(), bounds, null);
        results.addOnSuccessListener(new OnSuccessListener<AutocompletePredictionBufferResponse>() {
          @Override
          public void onSuccess(AutocompletePredictionBufferResponse autocompletePredictions) {
            try {
              autoComplete.clear();
              for (int i = 0; i < results.getResult().getCount() && i < 10; i++) {
                autoComplete.add(results.getResult().get(i).getFullText(null).toString());
                place_ids.put(results.getResult().get(i).getFullText(null).toString()
                        ,results.getResult().get(i).getPlaceId());
              }
                results.getResult().release();
              Log.d("DEBUGDEBUGDEBUG", "onSuccess: ENTERED,autocomplete has " + autoComplete.getCount() + "elements");
            } catch (Exception e) {
                results.getResult().release();
                autoComplete.clear();
            }

          }
            }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            results.getResult().release();
            autoComplete.clear();
            place_ids.clear();
          }
        });
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }

    });

        ACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideKeyboard(EditProfile.this);
            String place_id = place_ids.get(autoComplete.getItem(position));
            Log.d("DEBUGDEBUGDEBUG", "onItemClick: " + place_ids.get(autoComplete.getItem(position)));
            Places.getGeoDataClient(EditProfile.this).getPlaceById(place_id).addOnSuccessListener(new OnSuccessListener<PlaceBufferResponse>() {
              @Override
              public void onSuccess(PlaceBufferResponse places) {
                LatLng coords = places.get(0).getLatLng();
                coordinates = coords.latitude + ";" + coords.longitude;
              }
            });
          }
        });

  }

  private void pic_action() {
    if(!EditProfile.this.isFinishing()){
      final CharSequence[] items = {"Upload Pic", "Snap Pic"};
      AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
      builder.setTitle("Select source for image")
              .setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  if(which == 0){
                    UploadPic();
                  }
                  else if(which == 1){
                    Camera();
                  }
                  else{
                    dialog.dismiss();
                  }
                }
              });
      builder.show();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.edit_profile_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if (R.id.confirm == item.getItemId()) {
      SaveInfo();
      return true;
    } else return super.onOptionsItemSelected(item);
  }

  void Setup(Bundle b) {
    profile = new Profile();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbref = db.getReference("USERS").child(user.getUid());

    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        profile = dataSnapshot.getValue(Profile.class);
        Fill(b);
      }

      @Override
      public void onCancelled(DatabaseError e) {
      }
    });

  }

  void Fill(Bundle b) {
    TextView tv = findViewById(R.id.edit_name);
    tv.setText(profile.getName());
    tv = findViewById(R.id.edit_nickname);
    tv.setText(profile.getUsername());
    tv = findViewById(R.id.edit_email);
    tv.setText(profile.getEmail());
    if(b!=null){
      if(b.containsKey("bio")){
        //tv = findViewById(R.id.edit_location);
        //tv.setText(b.getString("location"));
        location = b.getString("location");
        coordinates = b.getString("coords");
        tv = findViewById(R.id.edit_biography);
        tv.setText(b.getString("bio"));
      }
    }
    else{
      //tv = findViewById(R.id.edit_location);
      //tv.setText(profile.getLocation());
      location = profile.getLocation();
      coordinates = profile.getCoordinates();
      tv = findViewById(R.id.edit_biography);
      tv.setText(profile.getBio());
    }

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    File pic = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString(),"pic.jpg");
    if(pic.exists()){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        createpic(BitmapFactory.decodeFile(pic.toString(), options));
    }
    else {
      StorageReference sr = FirebaseStorage.getInstance().getReference()
              .child("profile_pictures/" + user.getUid() + ".jpg");
      sr.getBytes(Constants.SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
        @Override
        public void onSuccess(byte[] bytes) {
          try{
            File pic = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString(),"pic.jpg");
            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            OutputStream outs = new FileOutputStream(pic);
            bm.compress(Bitmap.CompressFormat.JPEG, 85,outs);
            createpic(bm);
          }catch (Exception e){
            e.printStackTrace();
            pic.delete();
            ciw.setImageResource(R.mipmap.ic_launcher_round);
          }
        }
      }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
          // Handle any errors
          exception.printStackTrace();
          ciw.setImageResource(R.mipmap.ic_launcher_round);
        }
      });
    }
  }

  void SaveInfo() {
    profile.setCoordinates(coordinates);
    profile.setLocation(location);
    TextView tv = findViewById(R.id.edit_name);
    profile.setName(tv.getText().toString());
    tv = findViewById(R.id.edit_nickname);
    profile.setUsername(tv.getText().toString());
    tv = findViewById(R.id.edit_email);
    profile.setEmail(tv.getText().toString());
    //tv = findViewById(R.id.edit_location);
    //profile.setLocation(tv.getText().toString());
    /*if(tv.getText().toString().isEmpty()){
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder
              .setMessage(R.string.location_error)
              .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          dialog.dismiss();
        }
      });
      AlertDialog dialog = builder.create();
      dialog.show();
      return;
    }*/
    tv = findViewById(R.id.edit_biography);
    profile.setBio(tv.getText().toString());

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbref = db.getReference("USERS").child(user.getUid());
    dbref.setValue(profile);

    if (this.photo != null) {
      try {
        StorageReference sr = FirebaseStorage.getInstance().getReference().child("profile_pictures/" + user.getUid() + ".jpg");
        ProgressDialog dialog = ProgressDialog.show(EditProfile.this, "", "Uploading, please wait...", true);
        sr.putBytes(this.photo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
          @Override
          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            try{
              Bitmap bm = BitmapFactory.decodeByteArray(photo,0,photo.length);
              OutputStream outs = new FileOutputStream(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString(),"pic.jpg"));
              bm.compress(Bitmap.CompressFormat.JPEG, 85, outs);
              outs.close();
              dialog.dismiss();
              Intent swap = new Intent(EditProfile.this, ShowProfile.class);
              swap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
              startActivity(swap);
            }catch (Exception e){
              if(dialog.isShowing()) {
                dialog.dismiss();
              }
                Intent swap = new Intent(EditProfile.this, ShowProfile.class);
                swap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(swap);
            }
          }
        }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception exception) {
            exception.printStackTrace();
            dialog.dismiss();
            Intent swap = new Intent(EditProfile.this, ShowProfile.class);
            swap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(swap);
          }
        });
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    else{
      Intent swap = new Intent(EditProfile.this, ShowProfile.class);
      swap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(swap);
    }
  }

  private void RevertInfo() {
    Intent swap = new Intent(EditProfile.this, ShowProfile.class);
    swap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(swap);
  }


  private void UploadPic() {

    Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
    gallery.setType("image/*");
    //gallery.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    gallery.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    startActivityForResult(gallery, Constants.REQUEST_GALLERY);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Bitmap bm = null;

    if (requestCode == Constants.REQUEST_GALLERY && resultCode == RESULT_OK) {
      try {
        Uri uri = data.getData();
        bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    else if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      try{
        bm = (Bitmap) data.getExtras().get("data");
      }catch (Exception e){
        e.printStackTrace();
      }
    }
    /*else if (requestCode == Constants.PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
      try {
        Place place = PlacePicker.getPlace(this, data);
        TextView tv = findViewById(R.id.edit_location);
        tv.setText(place.getAddress().toString());
        Toast.makeText(this, R.string.location_msg + place.getAddress().toString(), Toast.LENGTH_LONG).show();
      }catch (Exception e){
        e.printStackTrace();
      }
    }*/

    if (bm != null) {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      bm.compress(Bitmap.CompressFormat.JPEG, 85, stream);
      this.photo = stream.toByteArray();
      this.createpic(bm);
      bm.recycle();
      try{
          stream.close();
      }catch (Exception e){
          e.printStackTrace();
      }
    }
  }

  void Camera() {

    if (ContextCompat.checkSelfPermission(EditProfile.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(EditProfile.this, new String[]{Manifest.permission.CAMERA}, Constants.CAMERA_PERMISSION);
    } else {
      Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    if (requestCode == Constants.CAMERA_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      Camera();
    }
    /*else if(requestCode == Constants.FINE_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
     setuplocation();
    }
    else if(requestCode == Constants.COARSE_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
      setuplocation();
    }
    else if(requestCode == Constants.COARSE_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
      def=true;
      setuplocation();
    }
    else if(requestCode == Constants.FINE_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
      def=true;
      setuplocation();
    }*/
  }

  @Override
  public void onBackPressed() {
    Intent intent = new Intent(EditProfile.this, ShowProfile.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }


  private void createpic(Bitmap bm) {

    int width = ciw.getWidth();
    int height = ciw.getHeight();
    Bitmap imageBitmap = Bitmap.createScaledBitmap(bm, width, height, false);
    Canvas canvas = new Canvas(imageBitmap);

    Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    p.setColor(Color.WHITE);
    p.setStyle(Paint.Style.FILL);

    canvas.drawRect((float)(width/2 - (0.03*width)),(float)(0.7*height),(float)(width/2 + (0.03*width)),(float)(0.3*height),p);
    canvas.drawRect((float)(0.7*width),(float)(height/2 - (0.03*height)),(float)(0.3*width),(float)(height/2 + (0.03*height)),p);
    ciw.setImageBitmap(imageBitmap);
  }

  protected void onSaveInstanceState(Bundle b) {
    super.onSaveInstanceState(b);
    TextView tv;
    //tv = findViewById(R.id.edit_location);
    //b.putString("location",tv.getText().toString());
    b.putString("coords",coordinates);
    b.putString("location",location);
    tv = findViewById(R.id.edit_biography);
    b.putString("bio",tv.getText().toString());

  }


}