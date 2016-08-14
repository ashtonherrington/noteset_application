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
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.FormBody;

/**
 * Class that handles the creation of new notesets
 */
public class Main4Activity extends AppCompatActivity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    //variables required for the logic on this page
    public final static String EXTRA_MESSAGE = "MESSAGE";
    public final static String EXTRA_MESSAGE2 = "MESSAGE2";
    private static final String TAG = "AshtonsMessage";
    private GestureDetectorCompat gestureDetector;
    private static long id;
    public static long notesetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        Log.i(TAG, "onCreate");
        Intent intent = getIntent();
        id = intent.getLongExtra(MainActivity.EXTRA_MESSAGE, -1);

        //now the screen is able to recognize gestures due to this
        this.gestureDetector = new GestureDetectorCompat(this, this);
        //this line is required to listen for double taps
        gestureDetector.setOnDoubleTapListener(this);

        //creation of new noteset button
        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        //fields are gathered and their lengths are measured
                        EditText editText4 = (EditText) findViewById(R.id.editText4);
                        EditText editText8 = (EditText) findViewById(R.id.editText8);
                        String text4 = editText4.getText().toString();
                        String text8 = editText8.getText().toString();
                        int text4Length = text4.length();
                        int text8Length = text8.length();

                        //ensures that the lengths of the fields are not 0
                        TextView changeText = (TextView) findViewById(R.id.textView4);
                        if (text4Length < 1 || text8Length < 1) {
                            changeText.setPadding(10, 10, 0, 0);
                            changeText.setTextSize(13);
                            changeText.setTextColor(Color.RED);
                            changeText.setText("ERROR, ALL FIELDS REQUIRED");
                        } else {
                            //new noteset is created
                            try {
                                NotesetCreator notesetCreator = new NotesetCreator(text4, text8, id);
                                notesetCreator.start();
                                notesetCreator.join();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //and then user is redirected to the add note screen to add notes to noteset
                            Intent intent = new Intent(getBaseContext(), Main5Activity.class);
                            intent.putExtra(EXTRA_MESSAGE, notesetId);
                            intent.putExtra(EXTRA_MESSAGE2, id);
                            startActivity(intent);
                        }
                    }
                }
        );

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), Main2Activity.class);
        intent.putExtra(EXTRA_MESSAGE, id);
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
 * Class sends post requests to create a new noteset
 */
class NotesetCreator extends Thread {

    //required variables for the constructor
    String title;
    String school;
    long userId;
    public NotesetCreator(String tit, String sch, long id){
        title = tit;
        school = sch;
        userId = id;
    }

    public void run() {
        try {
            //new request is compiled
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder().add("userId", Long.toString(userId))
                    .add("title", this.title)
                    .add("school", this.school)
                    .build();

            //request is sent, and the notesetId is saved in the main classes static variable
            Request request = new Request.Builder().url("http://homeworktwo-1272.appspot.com/addnoteset").post(body).build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            JSONObject obj = new JSONObject(responseBody);
            String stringId = obj.getString("id");
            long id = Long.valueOf(stringId);
            Main4Activity.notesetId = id;

        } catch (Exception e) {
            e.printStackTrace();;
        }
    }
}