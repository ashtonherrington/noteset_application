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

import java.io.File;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.FormBody;

public class Main7Activity extends AppCompatActivity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    public final static String EXTRA_MESSAGE  = "MESSAGE";
    public final static String EXTRA_MESSAGE2 = "MESSAGE2";
    public final static String EXTRA_MESSAGE3 = "MESSAGE3";
    public final static String EXTRA_MESSAGE4 = "MESSAGE4";
    public final static String EXTRA_MESSAGE5 = "MESSAGE5";

    private static final String TAG = "AshtonsMessage";
    private GestureDetectorCompat gestureDetector;
    private TextView message;

    private static long notesetId;
    private static long userId;
    private static String incomingFrontText;
    private static String incomingBackText;
    private static String incomingNoteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        Log.i(TAG, "onCreate");

        Intent intent = getIntent();
        userId = intent.getLongExtra(EXTRA_MESSAGE, -1);
        notesetId    = intent.getLongExtra(EXTRA_MESSAGE2, -1);
        incomingFrontText = intent.getStringExtra(EXTRA_MESSAGE3);
        incomingBackText = intent.getStringExtra(EXTRA_MESSAGE4);
        incomingNoteId = intent.getStringExtra(EXTRA_MESSAGE5);

        EditText editText115 = (EditText) findViewById(R.id.editText115);
        EditText editText116 = (EditText) findViewById(R.id.editText116);
        editText115.setText(incomingFrontText);
        editText116.setText(incomingBackText);

        //now the screen is able to recognize gestures due to this
        this.gestureDetector = new GestureDetectorCompat(this, this);
        //this line is required to listen for double taps
        gestureDetector.setOnDoubleTapListener(this);

        //reference to button
        Button button15 = (Button) findViewById(R.id.button15);
        //event listener for button
        button15.setOnClickListener(
                //callback method
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        EditText editText115 = (EditText) findViewById(R.id.editText115);
                        EditText editText116 = (EditText) findViewById(R.id.editText116);

                        String text115 = editText115.getText().toString();
                        String text116 = editText116.getText().toString();

                        int text115Length = text115.length();
                        int text116Length = text116.length();

                        TextView changeText = (TextView) findViewById(R.id.textView114);
                        if (text115Length < 1 || text116Length < 1) {
                            changeText.setPadding(10, 10, 0, 0);
                            changeText.setTextSize(13);
                            changeText.setTextColor(Color.RED);
                            changeText.setText("ERROR, ALL FIELDS REQUIRED");
                        } else {
                            try {
                                NoteDeleter noteCreator = new NoteDeleter(text115, text116, incomingNoteId, notesetId);
                                noteCreator.start();
                                noteCreator.join();

                                File dir = getFilesDir();
                                File file = new File(dir, Long.toString(notesetId));
                                boolean deleted = file.delete();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Intent intent = new Intent(getBaseContext(), Main6Activity.class);
                            intent.putExtra(EXTRA_MESSAGE, userId);
                            intent.putExtra(EXTRA_MESSAGE2, notesetId);
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

class NoteDeleter extends Thread {
    String frontText;
    String backText;
    String noteId;
    String notesetId;

    //Constructor
    public NoteDeleter(String front, String back, String id, Long nsId){
        frontText = front;
        backText = back;
        noteId = id;
        notesetId = Long.toString(nsId);
    }

    public void run() {
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder().add("id", this.noteId)
                    .add("frontText", this.frontText)
                    .add("backText", this.backText)
                    .add("notesetId", this.notesetId)
                    .build();

            Request request = new Request.Builder().url("http://homeworktwo-1272.appspot.com/updatenote").put(body).build();
            Response response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}