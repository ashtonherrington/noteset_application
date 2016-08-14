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
 * Class responsible for creation of a new note
 */
public class Main5Activity extends AppCompatActivity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    //required variables for this page's operations
    public final static String EXTRA_MESSAGE = "MESSAGE";
    public final static String EXTRA_MESSAGE2 = "MESSAGE2";
    private static final String TAG = "AshtonsMessage";
    private GestureDetectorCompat gestureDetector;
    private static long notesetId;
    private static long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        Log.i(TAG, "onCreate");
        Intent intent = getIntent();
        notesetId = intent.getLongExtra(EXTRA_MESSAGE, -1);
        userId    = intent.getLongExtra(EXTRA_MESSAGE2, -1);

        //now the screen is able to recognize gestures due to this
        this.gestureDetector = new GestureDetectorCompat(this, this);
        //this line is required to listen for double taps
        gestureDetector.setOnDoubleTapListener(this);

        //submit button for the creation of a new note
        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        //the fields are gathered and their lengths are calculated
                        EditText editText15 = (EditText) findViewById(R.id.editText15);
                        EditText editText16 = (EditText) findViewById(R.id.editText16);
                        String text15 = editText15.getText().toString();
                        String text16 = editText16.getText().toString();
                        int text15Length = text15.length();
                        int text16Length = text16.length();

                        //ensures that the fields have information present in them
                        TextView changeText = (TextView) findViewById(R.id.textView14);
                        if (text15Length < 1 || text16Length < 1) {
                            changeText.setPadding(10, 10, 0, 0);
                            changeText.setTextSize(13);
                            changeText.setTextColor(Color.RED);
                            changeText.setText("ERROR, ALL FIELDS REQUIRED");
                        } else {
                            //new note is created
                            try {
                                NoteCreator noteCreator = new NoteCreator(text15, text16, notesetId);
                                noteCreator.start();
                                noteCreator.join();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //and user is redirected to this same page to add another note
                            Intent intent = new Intent(getBaseContext(), Main5Activity.class);
                            intent.putExtra(EXTRA_MESSAGE, notesetId);
                            intent.putExtra(EXTRA_MESSAGE2, userId);
                            startActivity(intent);
                        }
                    }
                }
        );

        //submit button for the creation of a new note (final note)
        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        //the fields are gathered and their lengths are calculated
                        EditText editText15 = (EditText) findViewById(R.id.editText15);
                        EditText editText16 = (EditText) findViewById(R.id.editText16);
                        String text15 = editText15.getText().toString();
                        String text16 = editText16.getText().toString();
                        int text15Length = text15.length();
                        int text16Length = text16.length();

                        //ensures that the fields have information present in them
                        TextView changeText = (TextView) findViewById(R.id.textView14);
                        if (text15Length < 1 || text16Length < 1) {
                            changeText.setPadding(10, 10, 0, 0);
                            changeText.setTextSize(13);
                            changeText.setTextColor(Color.RED);
                            changeText.setText("ERROR, ALL FIELDS REQUIRED");
                        } else {
                            //new note is created
                            try {
                                NoteCreator noteCreator = new NoteCreator(text15, text16, notesetId);
                                noteCreator.start();
                                noteCreator.join();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //user is redirected to the page that lists the notesets as this is final note created in noteset
                            Intent intent = new Intent(getBaseContext(), Main2Activity.class);
                            intent.putExtra(EXTRA_MESSAGE, userId);
                            startActivity(intent);
                        }
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
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
 * Class responsible for sending POST request to create a new note
 */
class NoteCreator extends Thread {

    //variables required for the constructor
    String frontText;
    String backText;
    long notesetId;
    public NoteCreator(String front, String back, long id){
        frontText = front;
        backText = back;
        notesetId = id;
    }

    public void run() {
        try {
            //request information is compiled
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder().add("notesetId", Long.toString(notesetId))
                    .add("frontText", this.frontText)
                    .add("backText", this.backText)
                    .build();
            //new note is sent
            Request request = new Request.Builder().url("http://homeworktwo-1272.appspot.com/addnote").post(body).build();
            Response response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();;
        }
    }
}
