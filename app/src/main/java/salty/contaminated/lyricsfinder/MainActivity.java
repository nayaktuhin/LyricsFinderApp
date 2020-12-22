package salty.contaminated.lyricsfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NoCache;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // Declaring the UI Components
    Button fetchLyricsButton;
    EditText songName, artistName;
    TextView lyricsText;

    RequestQueue rqstQueue;
    JsonObjectRequest jsonObjRqst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialising the UI Components
        artistName = findViewById(R.id.edtArtistName);
        songName = findViewById(R.id.edtSongName);
        lyricsText = findViewById(R.id.lyricsTextView);
        fetchLyricsButton = findViewById(R.id.fetchLyrics);

        // Button Listener
        fetchLyricsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String artName = artistName.getText().toString();
                final String sngName = songName.getText().toString();

                if (artName.isEmpty()) artistName.setError("Enter Artist Name");
                else if (sngName.isEmpty()) songName.setError("Enter Song Name");
                else {

                Toast.makeText(getApplicationContext(), R.string.fetching, Toast.LENGTH_SHORT).show();
                
                final String lyricsURL = "https://api.lyrics.ovh/v1/" + artName + "/" + sngName;

                rqstQueue = Volley.newRequestQueue(getApplicationContext()); // to get the Volley Library to request from internet

                jsonObjRqst = new JsonObjectRequest(Request.Method.GET, lyricsURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) { // actionable tasks to do when request is received
                        try {
                            String lyricsLines = response.getString("lyrics");
                            if (lyricsLines.equals("")) {
                                throw new JSONException("Incorrect Artist/Song Name");
                            }
                            else {
                                lyricsText.setText(lyricsLines);
                            }
                        } catch (JSONException error) {
                            lyricsText.setText(error.getMessage());
                        }
                    }
                }, new Response.ErrorListener() { // to handle error for user faults
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), R.string.failedToFetch, Toast.LENGTH_SHORT).show();
                        if (!error.toString().equals("com.android.volley.ClientError"))
                            lyricsText.setText(R.string.noInternet);
                    }
                });
                jsonObjRqst.setShouldCache(false);
                rqstQueue.getCache().clear();
                rqstQueue.add(jsonObjRqst); // to access the JSON request to the API to added to queue
                    
            }
            }
        });

    }
}