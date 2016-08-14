package ashcorp.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.MediaType;

/**
 * This class is designed to handle the login screen
 */
public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener,
GestureDetector.OnDoubleTapListener{

    //variables used by class
    public final static String EXTRA_MESSAGE = "MESSAGE";
    public static long userId;

    private static final String TAG = "AshtonsMessage";
    private GestureDetectorCompat gestureDetector;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");

        message = (TextView)findViewById(R.id.textView2);
        //now the screen is able to recognize gestures due to this
        this.gestureDetector = new GestureDetectorCompat(this, this);
        //this line is required to listen for double taps
        gestureDetector.setOnDoubleTapListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //login button logic
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(
                //callback method
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        EditText editText = (EditText) findViewById(R.id.editText);
                        EditText editText2 = (EditText) findViewById(R.id.editText2);
                        TextView textView2 = (TextView) findViewById(R.id.textView2);

                        //check to ensure that the fields are not empty prior to login call
                        String text1 = editText.getText().toString();
                        String text2 = editText2.getText().toString();
                        int text1Length = text1.length();
                        int text2Length = text2.length();
                        TextView changeText = (TextView) findViewById(R.id.textView2);
                        if (text1Length < 1 || text2Length < 1) {
                            changeText.setPadding(30, 10, 0, 0);
                            changeText.setTextSize(13);
                            changeText.setTextColor(Color.RED);
                            changeText.setText("PLEASE ENTER USERNAME AND PASSWORD");
                        } else {
                            try {
                                Login login = new Login(textView2, text1, text2);
                                login.start();
                                login.join();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //login returns userId which either stores the user's ID or -1
                            if (userId > 0) {
                                Intent intent = new Intent(getBaseContext(), Main2Activity.class);
                                intent.putExtra(EXTRA_MESSAGE, userId);
                                startActivity(intent);
                            //userId of -1 is considered invalid user/pw combo
                            } else {
                                changeText.setPadding(10, 10, 0, 0);
                                changeText.setTextSize(13);
                                changeText.setTextColor(Color.RED);
                                changeText.setText("INVALID USER CREDENTIALS");
                            }
                        }
                    }
                }
        );

        //this sends the user to the create new user screen
        Button button33 = (Button) findViewById(R.id.button33);
        button33.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), Main3Activity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    //default message called when user touches a screen, must be overridden
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //pass the touch event to the gesture detector first
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
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
 * Class used to send login post requests
 */
class Login extends Thread {

    TextView theResponse;
    String userName;
    String password;

    //Constructor
    public Login(TextView response, String theUserName, String thePassword){
        theResponse = response;
        userName = theUserName;
        password = thePassword;
    }

    //Run command, exec'd on Thread instantiation
    public void run() {

        //post request is sent, and the results are saved in main activities static userId variable
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, "{}");
            String url = "http://homeworktwo-1272.appspot.com/login/" + userName + "/" + password;
            Request request = new Request.Builder().url(url).post(body).build();
            Response response = client.newCall(request).execute();
            String jsonString = response.body().string();
            JSONObject obj = new JSONObject(jsonString);
            String stringId = obj.getString("message");
            if (stringId.equals("Username|password combination invalid")) {
                MainActivity.userId = -1;
            } else {
                long id = Long.valueOf(stringId);
                MainActivity.userId = id;
            }
        } catch (Exception e) {
            e.printStackTrace();;
        }
    }
}
