package ashcorp.myapplication;

import android.content.Context;
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
import android.content.Intent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class responsbile for the display of the notes within a noteset
 */
public class Main6Activity extends AppCompatActivity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    //messages used for intent redirects
    public static boolean empty;
    public final static String EXTRA_MESSAGE  = "MESSAGE";
    public final static String EXTRA_MESSAGE2 = "MESSAGE2";
    public final static String EXTRA_MESSAGE3 = "MESSAGE3";
    public final static String EXTRA_MESSAGE4 = "MESSAGE4";
    public final static String EXTRA_MESSAGE5 = "MESSAGE5";

    //variables required for the logic of this class
    public static JSONObject obj;
    public static JSONArray jArray;
    private static final String TAG = "AshtonsMessage";
    private static long id;
    private static long notesetId;
    private GestureDetectorCompat gestureDetector;
    public static ArrayList<Note> noteList;
    private static int CURRENT = 0;
    private static boolean isFront = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);
        Log.i(TAG, "onCreate");

        //array list that stores the notes
        noteList = new ArrayList<Note>();

        this.gestureDetector = new GestureDetectorCompat(this, this);
        //this line is required to listen for double taps
        gestureDetector.setOnDoubleTapListener(this);

        //information is gathered from the intent
        Intent intent = getIntent();
        id =        intent.getLongExtra(EXTRA_MESSAGE, -1);
        notesetId = intent.getLongExtra(EXTRA_MESSAGE2, -1);

        //user is able to go back to the screen to view the noteset lists
        Button button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), Main2Activity.class);
                        intent.putExtra(EXTRA_MESSAGE, id);
                        startActivity(intent);
                    }
                }
        );

        //send users to the screen where they can edit the contents of a note
        Button button7 = (Button) findViewById(R.id.button7);
        button7.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), Main7Activity.class);
                        intent.putExtra(EXTRA_MESSAGE, id);
                        intent.putExtra(EXTRA_MESSAGE2, notesetId);
                        intent.putExtra(EXTRA_MESSAGE3, Main6Activity.noteList.get(CURRENT).frontText);
                        intent.putExtra(EXTRA_MESSAGE4, Main6Activity.noteList.get(CURRENT).backText);
                        intent.putExtra(EXTRA_MESSAGE5, Main6Activity.noteList.get(CURRENT).noteId);
                        startActivity(intent);
                    }
                }
        );

        //allows users to delete the current note on the screen
        Button button8 = (Button) findViewById(R.id.button8);
        button8.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        //if this is the last note in the noteset this logic is run
                        if (Main6Activity.noteList.size() == 1) {
                            //the entire noteset is deleted
                            try {
                                Deleter deleter = new Deleter(Long.toString(notesetId), "noteset");
                                deleter.start();
                                deleter.join();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //and the user is re-directed to the noteset list page
                            Intent intent = new Intent(getBaseContext(), Main2Activity.class);
                            intent.putExtra(EXTRA_MESSAGE, id);
                            startActivity(intent);

                        //if other notes exist in the noteset this logic is run
                        } else {
                            //the individual note is deleted
                            try {
                                Deleter deleter = new Deleter(Main6Activity.noteList.get(CURRENT).noteId, "note");
                                deleter.start();
                                deleter.join();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //and the user is re-directed to the note viewing page
                            Intent intent = new Intent(getBaseContext(), Main6Activity.class);
                            intent.putExtra(EXTRA_MESSAGE, id);
                            intent.putExtra(EXTRA_MESSAGE2, notesetId);
                            startActivity(intent);
                        }
                        //and the existing file is deleted to be refreshed after alteration
                        File dir = getFilesDir();
                        File file = new File(dir, Long.toString(notesetId));
                        boolean deleted = file.delete();
                    }
                }
        );

        //this logic determines if the information for a noteset has already been cached locally on the phone
        boolean exists = false;
        int index = -1;
        File[] files =getFilesDir().listFiles();
        for (int i=0; i < files.length; i++) {
            if (getFilesDir().listFiles()[i].getName().equals(Long.toString(notesetId))) {
                exists = true;
                index = i;
            }
        }

        String JSONstring = "";

        //if there is a file on the phone, the data from this file is used
        if (exists) {
            try {
                //data is read from the file
                InputStream inputStream = openFileInput(getFilesDir().listFiles()[index].getName());
                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((receiveString = bufferedReader.readLine()) != null) {
                        stringBuilder.append(receiveString);
                    }
                    inputStream.close();
                    JSONstring = stringBuilder.toString();
                    //and stored within these static variables of the main class
                    try {
                        Main6Activity.obj = new JSONObject(JSONstring);
                        Main6Activity.jArray = Main6Activity.obj.names();
                        Main6Activity.empty = false;
                    } catch (JSONException e) {
                        e.printStackTrace();;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        //if there is no file on the phone this logic runs
        } else {
            //POST request occurs to gather data from the API
            try {
                NoteRetriever noteRetriever = new NoteRetriever(notesetId);
                noteRetriever.start();
                noteRetriever.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //and afterwards the information is cached into a local file
            try {
                FileOutputStream fos = openFileOutput(Long.toString(notesetId), Context.MODE_PRIVATE);
                fos.write(Main6Activity.obj.toString().getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();;
            }
        }

        //array list of the notes
        ArrayList<Note> noteList = new ArrayList<Note>();
        String noteId;

        //the information yielded is stored within the noteList
        try {
            for (int i=0; i < jArray.length(); i++) {
                noteId = (String) Main6Activity.jArray.get(i);
                String get = Main6Activity.obj.getString(noteId);
                JSONObject innerArray = new JSONObject(get);
                String frontText = (String) innerArray.get("frontText");
                String backText = (String) innerArray.get("backText");
                Note nextNote = new Note(frontText, backText, noteId);
                Main6Activity.noteList.add(nextNote);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //and the first note is displayed on the screen
        TextView viewScreen = (TextView) findViewById(R.id.textView17);
        viewScreen.setText(Main6Activity.noteList.get(CURRENT).frontText);

    }

    //allows user to cycle through notes by swiping right
    public void onSwipeRight() {
        CURRENT++;
        if (CURRENT == Main6Activity.noteList.size()) {
            CURRENT = 0;
        }
        TextView viewScreen = (TextView) findViewById(R.id.textView17);
        viewScreen.setText(Main6Activity.noteList.get(CURRENT).frontText);
        isFront = true;
    }

    //allows user to cycle through notes by swiping left
    public void onSwipeLeft() {
        CURRENT--;
        if (CURRENT < 0) {
            CURRENT = Main6Activity.noteList.size() - 1;
        }
        TextView viewScreen = (TextView) findViewById(R.id.textView17);
        viewScreen.setText(Main6Activity.noteList.get(CURRENT).frontText);
        isFront = true;
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
        TextView viewScreen = (TextView) findViewById(R.id.textView17);
        if (isFront) {
            viewScreen.setText(Main6Activity.noteList.get(CURRENT).backText);
            isFront = false;
        } else {
            viewScreen.setText(Main6Activity.noteList.get(CURRENT).frontText);
            isFront = true;
        }
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
    public void onLongPress(MotionEvent e) {;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        boolean result = false;

        int SWIPE_THRESHOLD = 100;
        int SWIPE_VELOCITY_THRESHOLD = 100;
        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
            }
            result = true;
        }
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
 * Class responsible for retrieving individual notes
 */
class NoteRetriever extends Thread {

    //variable used for constructor
    long notesetId;
    public NoteRetriever(long id){
        notesetId = id;
    }

    public void run() {
        try {
            //new request is built and executed
            OkHttpClient client = new OkHttpClient();
            String url = "http://homeworktwo-1272.appspot.com/getnotes/" + notesetId;
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            //response stored and if it is empty flag is set to true
            String responseBody = response.body().string();
            if (responseBody.equals("[]")) {
                Main6Activity.empty = true;
            //otherwise the information is saved in the main classes static variables
            } else {
                String jsonInfo = responseBody;
                Main6Activity.obj = new JSONObject(jsonInfo);
                Main6Activity.jArray = Main6Activity.obj.names();
                Main6Activity.empty = false;
            }
        } catch (Exception e) {
            e.printStackTrace();;
        }
    }
}

/**
 * Class responsible for deleting notes and note sets
 */
class Deleter extends Thread {

    //variables required for constructor
    String id;
    String type;
    public Deleter(String theId, String theType){
        id   = theId;
        type = theType;
    }

    public void run() {
        try {
            //url is chosen based on the type fed into the constructor
            OkHttpClient client = new OkHttpClient();
            String url = "";
            if (type.equals("note")) {
                url = "http://homeworktwo-1272.appspot.com/delete/note/" + id;
            } else if (type.equals("noteset")) {
                url = "http://homeworktwo-1272.appspot.com/delete/noteset/" + id;
            }
            Request request = new Request.Builder().url(url).delete().build();
            Response response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();;
        }
    }
}

/**
 * Note structure, self-explanitory
 */
class Note {
    String frontText;
    String backText;
    String noteId;

    public Note(String front, String back, String id) {
        frontText = front;
        backText  = back;
        noteId    = id;
    }
}