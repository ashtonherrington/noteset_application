package ashcorp.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.GestureDetectorCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.content.Intent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.FormBody;

/**
 * Class that allows users to create new user profiles
 */
public class Main3Activity extends AppCompatActivity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    private static final String TAG = "AshtonsMessage";
    private GestureDetectorCompat gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Log.i(TAG, "onCreate");

        //now the screen is able to recognize gestures due to this
        this.gestureDetector = new GestureDetectorCompat(this, this);
        //this line is required to listen for double taps
        gestureDetector.setOnDoubleTapListener(this);

        //submit button
        Button button32 = (Button) findViewById(R.id.button32);
        button32.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        //text fields of the 4 fields are gathered and their lengths are measured
                        EditText editText3 = (EditText) findViewById(R.id.editText3);
                        EditText editText5 = (EditText) findViewById(R.id.editText5);
                        EditText editText6 = (EditText) findViewById(R.id.editText6);
                        EditText editText7 = (EditText) findViewById(R.id.editText7);
                        String text3 = editText3.getText().toString();
                        String text5 = editText5.getText().toString();
                        String text6 = editText6.getText().toString();
                        String text7 = editText7.getText().toString();
                        int text3Length = text3.length();
                        int text5Length = text5.length();
                        int text6Length = text6.length();
                        int text7Length = text7.length();

                        //ensure that the lengths of all the fields are not empty
                        TextView changeText = (TextView) findViewById(R.id.textView9);
                        if (text3Length < 1 || text5Length < 1 || text6Length < 1 || text7Length < 1) {
                            changeText.setPadding(10, 10, 0, 0);
                            changeText.setTextSize(13);
                            changeText.setTextColor(Color.RED);
                            changeText.setText("ERROR, ALL FIELDS REQUIRED");
                        } else {
                            //send the request to create a new user
                            try {
                                UserCreator userCreator = new UserCreator(text6, text3, text5, text7);
                                userCreator.start();
                                userCreator.join();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //and then redirect to the login screen
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }
        );

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }

    //default message called when user touches a screen, must be overridden
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //pass the touch event to the gesture detector first
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //Begin gestures

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    //end gestures

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

/**
 * Class handles the creation of new users
 */
class UserCreator extends Thread {

    //required fields given to constructor
    String firstName;
    String userName;
    String password;
    String school;
    public UserCreator(String fn, String un, String pw, String sch){
        firstName = fn;
        userName = un;
        password = pw;
        school = sch;
    }

    public void run() {
        try {
            //new request is compiled
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder().add("firstName", firstName)
                    .add("userName", userName)
                    .add("password", password)
                    .add("school", school)
                    .build();
            //post request sent
            Request request = new Request.Builder().url("http://homeworktwo-1272.appspot.com/adduser").post(body).build();
            Response response = client.newCall(request).execute();

        } catch (Exception e) {
            e.printStackTrace();;
        }
    }
}