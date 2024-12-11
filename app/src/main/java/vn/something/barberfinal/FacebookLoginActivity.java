package vn.something.barberfinal;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.facebook.AccessToken;
import com.facebook.AccessTokenManager;
import com.facebook.AccessTokenSource;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vn.something.barberfinal.DataModel.BarberShop;


public class FacebookLoginActivity extends AppCompatActivity {

    LoginButton loginButton;
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private CallbackManager mCallbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
        mAuth = FirebaseAuth.getInstance();

        // [START initialize_fblogin]
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button_fb);
        loginButton.setPermissions("pages_show_list", "pages_messaging", "pages_manage_metadata");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FB login", "facebook:onSuccess:" + loginResult);

                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d("FB login", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("FB login", "facebook:onError", error);
            }
        });
        // [END initialize_fblogin]
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void handleFacebookAccessToken(AccessToken token) {

        Log.d("FB login", "handleFacebookAccessToken:" + token.getToken());
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Firebase facebook auth", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            getFbPageAcessToken(token);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Firebase facebook auth", "signInWithCredential:failure", task.getException());
                            Toast.makeText(FacebookLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        //retrieve page access token

    }
    private void getFbPageAcessToken(AccessToken token){
        Log.d("spexcial", "hello");
        GraphRequest request = GraphRequest.newGraphPathRequest(
                token,
                "/me/accounts",
                response -> {
                    try {

                        JSONObject jsonObject = response.getJSONObject();
                        if (jsonObject != null) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject page = data.getJSONObject(i);
                                String pageName = page.getString("name");
                                String pageId = page.getString("id");
                                String pageAccessToken = page.getString("access_token");

                                createBarberShopFromPage(pageId,pageAccessToken);
                                Log.d("PageAccessToken", "Page Name: " + pageName + ", Access Token: " + pageAccessToken);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

        request.executeAsync();
    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
           
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }
    public void createBarberShopFromPage(String pageId, String pageAccessToken) {
        //https://developers.facebook.com/docs/graph-api/reference/page/
        String fields = "name,emails,location,phone,picture,link,website,hours,followers_count";

        // Make a Graph API request to get the page information
        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + pageId,
                null,
                HttpMethod.GET,
                (GraphRequest.Callback) response -> {

                    if (response.getError() == null) {
                        JSONObject pageObject = response.getJSONObject();
                        assert pageObject != null;
                        String email = null;
                        if(pageObject.optJSONArray("emails") != null && pageObject.optJSONArray("emails").length() > 0){
                            email = pageObject.optJSONArray("emails").optString(0,null);
                        }
                        String name = pageObject.optString("name");
                        JSONObject location = pageObject.optJSONObject("location");
                        String address = location != null ? location.optString("street") + " " +location.optString("city") : "Address not available";
                        String phoneNumber = pageObject.optString("phone");
                        JSONObject pictureObj = pageObject.optJSONObject("picture").optJSONObject("data");
                        String pictureUrl = null;
                        if(pictureObj !=null){
                            pictureUrl = pictureObj.optString("url");

                        }
                        String link = pageObject.optString("link");
                        String website = pageObject.optString("website");
                        int follower_count = pageObject.optInt("followers_count");
                        // Create a new BarberShop object with the retrieved information
                        BarberShop barberShop = new BarberShop(name,email,pictureUrl,website,address,phoneNumber,follower_count,pageId,pageAccessToken,FirebaseAuth.getInstance().getCurrentUser().getUid());

                        saveBarberShop(barberShop);
                    } else {
                        // Handle errors or no response
                        if (response.getError() != null) {
                            // Handle the error (e.g., invalid token, no access)
                            System.err.println("Error fetching page info: " + response.getError().getErrorMessage());
                        }
                    }
                }
        );


        Bundle params = new Bundle();
        params.putString("access_token", pageAccessToken);
        params.putString("fields", fields);
        request.setParameters(params);

        // Execute the request
        request.executeAsync();
    }
    public void saveBarberShop(BarberShop barberShop){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference shopRef = database.collection("shops").document(barberShop.getPageId());
        String shopId = barberShop.getPageId();
        barberShop.setShopId(shopId);

        if (shopId != null) {
            shopRef.set(barberShop)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("firestoresave", "shop saved successfully");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("firestoresave", "Error saving shop", e);
                    });
        }
    }

}