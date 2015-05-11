package carnegiemellonuniversity.pinfood;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;


public class DisplayMessageActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_search_activity);

        //set parent activity
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the message from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Create the text view
        TextView t = (TextView) findViewById(R.id.display_message_from_mainActivity);
        t.setText(message);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Search your favorites...");

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:

                return true;
            case R.id.action_settings:
                //openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
