package vn.something.barberfinal;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
                Intent intent = new Intent(FacebookLoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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
        //this is user acess token: EAANT54jMnpMBOyqRAgE8ZB1RJe6sZBgAdBgib4aHcZBBOsVKcq309RPtApQLTMFJ10RvW4yvQ8FDfEL1k5AAOFqMPtpMR6kctAZAwy1zFTgeafC4C9HuW4DzfiZAeZC34hUadJYCxhusokumPZCfnIW7VbA4KTwCcqqjrvBWrPlFRLWhgOoQydcEYBy4O4TUj2C8wonWxY1UJ9wkF22h5wtRqZA1xOyHGcYZD
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
}