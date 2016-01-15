package carnegiemellonuniversity.pinfood;

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
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;

public class DisplaySearchResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_search_result_activity);

        TextView test1 = (TextView) findViewById(R.id.display_search_result);
        test1.setText(getIntent().getStringExtra("latitude"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Search your favorites...");

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
                //hideSoftKeyboard(DisplaySearchResultActivity.this);
                searchView.clearFocus();//hide keyboard after searching

                /* Set up online search */
                String uri = "https://www.google.com/maps/search/" + query +"/@"
                        + MainActivity.lati + "," + MainActivity.longi + "z?hl=en";
                System.out.println(uri);
                Uri webpage = Uri.parse(uri);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);

                /* Check app available for searching */
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
}
