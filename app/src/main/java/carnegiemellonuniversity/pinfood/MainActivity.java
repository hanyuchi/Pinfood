package carnegiemellonuniversity.pinfood;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;


public class MainActivity extends Activity{
    public final static String EXTRA_MESSAGE = "hello!";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTitle("lalalaaa");
        //setContentView(R.layout.main);

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        String tag1 = "Quick Search";
        String tag2 = "Drop Pin on Map";
        TabListener<QuickSearchActivity> tl1 = new TabListener<QuickSearchActivity>(this,
                tag1, QuickSearchActivity.class);
        TabListener<DropPinOnMapActivity> tl2 = new TabListener<DropPinOnMapActivity>(this,
                tag2, DropPinOnMapActivity.class);

        actionBar.addTab(actionBar.newTab()
                .setText(tag1)
                .setTabListener(tl1));
        actionBar.addTab(actionBar.newTab()
                .setText(tag2)
                .setTabListener(tl2));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Online search...");

        /*
         * change searchView plate background color
         */
        int searchPlateId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_plate", null, null);
        View searchPlateView = searchView.findViewById(searchPlateId);
        searchPlateView.setBackgroundColor(Color.WHITE);

        /*
         * change searchView text color
         */
        int searchSrcTextId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(searchSrcTextId);
        textView.setTextColor(Color.BLACK);

        /*
         * set searchView cursor color
         */
        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView
                .findViewById(searchSrcTextId);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {}

        /*
         * searchView listener
         */
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.e("onQueryTextChange", "called");
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                hideSoftKeyboard(MainActivity.this);
                //searchView.clearFocus();//hide keyboard after searching

                String uri = "https://www.google.com/maps/search/" + query +"/@"
                        + DropPinOnMapActivity.lati + ","
                        + DropPinOnMapActivity.longi + "z?hl=en";
                System.out.println(uri);
                Uri webpage = Uri.parse(uri);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);

                //check app available for searching
                PackageManager packageManager = getPackageManager();
                List activities = packageManager.queryIntentActivities(webIntent,
                        PackageManager.MATCH_DEFAULT_ONLY);
                boolean isIntentSafe = activities.size() > 0;

                if(isIntentSafe) {
                    startActivity(webIntent);
                }else{
                    /*
                     * prompt alert
                     */
                    AlertDialog.Builder alert = new AlertDialog.Builder(searchView.getContext());

                    //alert.setTitle("Title");
                    alert.setMessage("We need Google Map to search your favorites."
                            + "Do you want to download it?");

                    alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

                    alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=com.google.android.apps.maps"));
                            startActivity(intent);
                        }
                    });

                    alert.show();
                }
                return false;
            }
        });

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

    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}