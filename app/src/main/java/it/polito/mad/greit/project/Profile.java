package it.polito.mad.greit.project;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.Serializable;


public class Profile implements Serializable{
  private String username;
  private String name;
  private String email;
  private String location;
  private String coordinates;
  private String bio;
  private String token;
  //private String photoUri;
  
  public Profile() {}
  
  public String getUsername() {
    return username;
  }
  
  public void setUsername(String username) {
    this.username = username;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public String getLocation() {
    return location;
  }
  
  public void setLocation(String location) {
    this.location = location;
  }
  
  public String getBio() {
    return bio;
  }
  
  public void setBio(String bio) {
    this.bio = bio;
  }

  public void setCoordinates(String coords){ this.coordinates = coords; }

  public String getCoordinates(){ return this.coordinates; }

  public String getToken(){ return this.token; }

  public void setToken(String tk) { this.token = tk; }
  
 // public String getPhotoUri(){return photoUri; }
  
  //public void setPhotoUri(String photoUri) {this.photoUri = photoUri;}
  
}