package y.getwebpagesourcecode;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Thread.setDefaultUncaughtExceptionHandler;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    TextView result_link;
    Spinner pilihan;
    EditText url_text;
    ArrayAdapter<CharSequence> list_spinner;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pilihan = (Spinner) findViewById(R.id.spinner_protokol);
        url_text = (EditText) findViewById(R.id.input_link);
        result_link = (TextView) findViewById(R.id.sumber_HTML);
        loading = (ProgressBar) findViewById(R.id.progress_loading);

        list_spinner = ArrayAdapter.createFromResource(this, R.array.protokol, android.R.layout.simple_spinner_item);
        list_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pilihan.setAdapter(list_spinner);

        setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                loading.setVisibility(View.GONE);
                Log.e("Error" + Thread.currentThread().getStackTrace()[2], paramThrowable.getLocalizedMessage());
            }
        });
        if (getSupportLoaderManager().getLoader(0) !=null){
            getSupportLoaderManager().initLoader(0, null, this);
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Really Exit")
                .setMessage("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void TUNGGUBOSQU(View view) {
        String link_url, protokol, url;
        protokol = pilihan.getSelectedItem().toString();
        url = url_text.getText().toString();

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        if (!url.isEmpty()) {
            if (url.contains(".")) {
                if (checkConnection()) {
                    result_link.setText("");
                    loading.setVisibility(View.VISIBLE);

                    link_url = protokol + url;

                    Bundle bundle = new Bundle();
                    bundle.putString("url_link", link_url);
                    getSupportLoaderManager().restartLoader(0, null, this);

                } else {
                    Toast.makeText(this, "check your internet connection", Toast.LENGTH_SHORT).show();
                    result_link.setText("No Internet Connection");

                }
            } else {
                result_link.setText("Invalid URL");

            }

        } else {
            result_link.setText("URL can\'t empty");
        }


    }

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new PageSource(this, args.getString("url_link"));
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        loading.setVisibility(View.GONE);
        result_link.setText(data);
    }


    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
