package ashcorp.myapplication;

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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class that handles the notesets related to the user
 */
public class Main2Activity extends AppCompatActivity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    //requied variables for page actions
    public static boolean empty;
    public final static String EXTRA_MESSAGE = "MESSAGE";
    public final static String EXTRA_MESSAGE2 = "MESSAGE2";
    public static JSONObject obj;
    public static JSONArray jArray;
    private static final String TAG = "AshtonsMessage";
    private static long id;
    private GestureDetectorCompat gestureDetector;
    private TextView message;
    int theLength;

    //ids for the notesets
    public static long note0Id;
    public static long note1Id;
    public static long note2Id;
    public static long note3Id;
    public static long note4Id;
    public static long note5Id;
    public static long note6Id;
    public static long note7Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Log.i(TAG, "onCreate");

        //now the screen is able to recognize gestures due to this
        this.gestureDetector = new GestureDetectorCompat(this, this);
        //this line is required to listen for double taps
        gestureDetector.setOnDoubleTapListener(this);

        //the buttons are dynamically allocated
        for (int i = 0; i < 8; i++) {
            String name = "noteset";
            String specificId = name.concat(String.valueOf(i));
            int theId = getResources().getIdentifier(specificId, "id", "ashcorp.myapplication");
            Button button = (Button) findViewById(theId);
            button.setVisibility(View.INVISIBLE);
            button.setText(null);
            button.setHint(null);
        }

        //retrieve the user's id from the intent
        Intent intent = getIntent();
        id = intent.getLongExtra(MainActivity.EXTRA_MESSAGE, -1);

        //logs the user out
        Button button50 = (Button) findViewById(R.id.button50);
        button50.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
        );

        //allows user to add a new noteset
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), Main4Activity.class);
                        intent.putExtra(EXTRA_MESSAGE, id);
                        startActivity(intent);
                    }
                }
        );

        //acquires information relative to the noteset
        try {
            NotesetRetriever notesetRetriever = new NotesetRetriever(id);
            notesetRetriever.start();
            notesetRetriever.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //determines if the notesets are present, if they are it sets their related button to visible
        String firstIndex;
        if (Main2Activity.empty == false) {
            this.theLength = jArray.length();
            try {
                for (int i = 0; i < this.theLength; i++) {
                    firstIndex = (String) Main2Activity.jArray.get(i);
                    String get = Main2Activity.obj.getString(firstIndex);
                    JSONObject innerArray = new JSONObject(get);
                    String title = (String) innerArray.get("title");
                    String school = (String) innerArray.get("school");
                    String noteButtonId = "noteset";
                    String specificId = noteButtonId.concat(String.valueOf(i));
                    int theId = getResources().getIdentifier(specificId, "id", "ashcorp.myapplication");
                    Button button = (Button) findViewById(theId);
                    button.setVisibility(View.VISIBLE);
                    button.setText(school + " - " + title);
                    button.setHint(firstIndex);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //buttons related to each of the notesets are acquired as objects
        Button noteset0 = (Button) findViewById(R.id.noteset0);
        Button noteset1 = (Button) findViewById(R.id.noteset1);
        Button noteset2 = (Button) findViewById(R.id.noteset2);
        Button noteset3 = (Button) findViewById(R.id.noteset3);
        Button noteset4 = (Button) findViewById(R.id.noteset4);
        Button noteset5 = (Button) findViewById(R.id.noteset5);
        Button noteset6 = (Button) findViewById(R.id.noteset6);
        Button noteset7 = (Button) findViewById(R.id.noteset7);

        //the IDs of the notesets are reaped from their respective buttons
        if (noteset0.getHint() != null) {
            note0Id = Long.valueOf((String) noteset0.getHint());
        }
        if (noteset1.getHint() != null) {
            note1Id = Long.valueOf((String) noteset1.getHint());
        }
        if (noteset2.getHint() != null) {
            note2Id = Long.valueOf((String) noteset2.getHint());
        }
        if (noteset3.getHint() != null) {
            note3Id = Long.valueOf((String) noteset3.getHint());
        }
        if (noteset4.getHint() != null) {
            note4Id = Long.valueOf((String) noteset4.getHint());
        }
        if (noteset5.getHint() != null) {
            note5Id = Long.valueOf((String) noteset5.getHint());
        }
        if (noteset6.getHint() != null) {
            note6Id = Long.valueOf((String) noteset6.getHint());
        }
        if (noteset7.getHint() != null) {
            note7Id = Long.valueOf((String) noteset7.getHint());
        }

        //actions for each of the buttons are defined using the IDs reaped above
        if (noteset0.getHint() != null) {
            noteset0.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), Main6Activity.class);
                            intent.putExtra(EXTRA_MESSAGE, id);
                            intent.putExtra(EXTRA_MESSAGE2, Main2Activity.note0Id);
                            startActivity(intent);
                        }
                    }
            );
        }
        if (noteset1.getHint() != null) {
            noteset1.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), Main6Activity.class);
                            intent.putExtra(EXTRA_MESSAGE, id);
                            intent.putExtra(EXTRA_MESSAGE2, Main2Activity.note1Id);
                            startActivity(intent);
                        }
                    }
            );
        }
        if (noteset2.getHint() != null) {
            noteset2.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), Main6Activity.class);
                            intent.putExtra(EXTRA_MESSAGE, id);
                            intent.putExtra(EXTRA_MESSAGE2, Main2Activity.note2Id);
                            startActivity(intent);
                        }
                    }
            );
        }
        if (noteset3.getHint() != null) {
            noteset3.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), Main6Activity.class);
                            intent.putExtra(EXTRA_MESSAGE, id);
                            intent.putExtra(EXTRA_MESSAGE2, Main2Activity.note3Id);
                            startActivity(intent);
                        }
                    }
            );
        }
        if (noteset4.getHint() != null) {
            noteset4.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), Main6Activity.class);
                            intent.putExtra(EXTRA_MESSAGE, id);
                            intent.putExtra(EXTRA_MESSAGE2, Main2Activity.note4Id);
                            startActivity(intent);
                        }
                    }
            );
        }
        if (noteset5.getHint() != null) {
            noteset5.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), Main6Activity.class);
                            intent.putExtra(EXTRA_MESSAGE, id);
                            intent.putExtra(EXTRA_MESSAGE2, Main2Activity.note5Id);
                            startActivity(intent);
                        }
                    }
            );
        }
        if (noteset6.getHint() != null) {
            noteset6.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), Main6Activity.class);
                            intent.putExtra(EXTRA_MESSAGE, id);
                            intent.putExtra(EXTRA_MESSAGE2, Main2Activity.note6Id);
                            startActivity(intent);
                        }
                    }
            );
        }
        if (noteset7.getHint() != null) {
            noteset7.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(getBaseContext(), Main6Activity.class);
                            intent.putExtra(EXTRA_MESSAGE, id);
                            intent.putExtra(EXTRA_MESSAGE2, Main2Activity.note7Id);
                            startActivity(intent);
                        }
                    }
            );
        }
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
    public void onLongPress(MotionEvent e) {;
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
 * Acquires notesets
 */
class NotesetRetriever extends Thread {

    //requires a long variable of the users id
    long userId;
    public NotesetRetriever(long id){
        userId = id;
    }

    public void run() {
        try {
            //send the request
            OkHttpClient client = new OkHttpClient();
            String url = "http://homeworktwo-1272.appspot.com/getnoteset/" + userId;
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            //if it is empty set a flag to true
            if (responseBody.equals("[]")) {
                Main2Activity.empty = true;
            //otherwise save the information in static variables of the main class
            } else {
                String jsonInfo = responseBody;
                Main2Activity.obj = new JSONObject(jsonInfo);
                Main2Activity.jArray = Main2Activity.obj.names();
                Main2Activity.empty = false;
            }

        } catch (Exception e) {
            e.printStackTrace();;
        }
    }
}
