package med.prep.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;

import med.prep.R;
import med.prep.model.impl.DiagramExpose;
import med.prep.model.impl.DiagramStore;
import med.prep.model.meta.Store;
import med.prep.model.meta.UniversalModel;

public class AppMaintainReport extends AppReport {


    WebView web;

    boolean directPrint = false;

    String namespace;

    DiagramExpose expo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_report);


        namespace = getIntent().getStringExtra("namespace");

        expo = new DiagramExpose(getApplicationContext(), null, null);

        Store store = new DiagramStore(expo, namespace);
        expo.createStore(store, namespace, "");


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle("Report");
        }



        web = findViewById(R.id.web_view);
        web.getSettings().setAllowFileAccess(true);


        // >>> expose model
        createBrowser();
    }



    /* --------------------------------ExpoDrive-------------------------------- */

    private void createBrowser() {
        String base = "file:///data/user/0/med.prep/files/";


        /*
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        branding = sharedPreferences.getString("report_branding", "");

        layout = sharedPreferences.getString("report_layout", "business");
        width = sharedPreferences.getString("report_image_width", "400");
         */


        String html = "Test";
        html = ltrPage();



        web.loadDataWithBaseURL(base, html, "text/html", "UTF-8", null);

        if (directPrint) createPrintJob();
    }



    private String ltrPage() {

        // >>> header
        String html = createHeader();

        // >>> model
        for (UniversalModel model : expo.getStore().getModels()) {
            html = append(html, ltrModel(model));
        }// model


        html += "</table>";

        // >>> end
        html += "</body>";


        return html;
    }

    private String ltrModel(UniversalModel model) {
        String html = "";

        {// outline table
            html += "<table width='100%' border='1'><tr><td>";


            {// inner table
                html += "<table width='100%' border='0' valign='top'><tr>";


                // *** image ***
                /*
                html += "<th rowspan='2' width='" + width + "'>";
                html += "<img src='./" + folder + "/" + model.getSymbol() + "'" + " width='" + width + "'>";
                html += "</th>";
                 */



                {// text table
                    html += "<th style='padding:2px' valign='top'>";
                    html += "<table width='100%' border='0'>";


                    // *** title, date ***

                    html += "<tr>";
                    html += "<th style='text-align:left'>" + model.getTitle() + "</th>";
                    html += "<th style='text-align:right;font-size:9'>" + model.getDate() + "</th>";
                    html += "</tr>";




                    // *** type ***


                    html += "<tr>";
                    html += "<th colspan='2' style='text-align:right;font-size:9'>" + model.getSubject() + "</th>";
                    html += "</tr>";

                    // *** state ***

                    html += "<tr>";
                    html += "<th colspan='2' style='text-align:right;font-size:9'>" + model.getState() + "</th>";
                    html += "</tr>";




                    // *** title ***

                    html += "<tr>";
                    html += "<th colspan='2' style='text-align:left;font-size:17;color:darkgreen'><br>" + model.getTitle() + "</th>";
                    html += "</tr>";

                    // *** subject ***
                    html += "<tr>";
                    html += "<th colspan='2' style='text-align:left;font-size:19'><br>" + model.getSubject() + "</th>";
                    html += "</tr>";




                    // *** content ***
                    html += "<tr>";
                    html += "<th colspan='2' style='text-align:left'><br><br>" + model.getContent() + "</th>";
                    html += "</tr>";

                    // *** specs ***
                    html += "<tr>";
                    html += "<th colspan='2' style='text-align:left'>" + model.getSpecs() + "</th>";
                    html += "</tr>";

                    // *** tags ***
                    html += "<tr>";
                    html += "<th colspan='2' style='text-align:left;color:darkblue'>" + model.getTags() + "</th>";
                    html += "</tr>";


                    html += "</table>";
                }//text table



                // *** bottom ***

                html += "<tr>";
                html += "<th style='text-align:right;vertical-align:bottom'>" + model.getTags() + "</th>";
                html += "</tr>";

                html += "</table>";
            }// inner table


            html += "</td></tr></table>";
        }// outline table



        return html;
    }













    private String append(String source, String text) {

        return source + text;
    }


    private void createPrintJob() {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);


        String jobName = "Analyse";

        PrintDocumentAdapter printAdapter = web.createPrintDocumentAdapter(jobName);
        PrintJob printJob = printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());

        if (printJob.isBlocked() || printJob.isFailed()) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }

    class ExposePrinter extends PrintDocumentAdapter {

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        }

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        }

        @Override
        public void onFinish() {
            finish();
        }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_html, menu);

        MenuCompat.setGroupDividerEnabled(menu, true);
        /*

        MenuItem mode = menu.findItem(R.id.mode_switch);
        if (mode != null) {

            mode.setChecked(true);
            if (mode.isChecked()) message("on");
            else message("off");
        }

        mode.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                beep();
                return false;
            }
        });
         */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        // show settings
        if (id == R.id.action_print) {

            createPrintJob();

            return true;
        }




        return super.onOptionsItemSelected(item);
    }//menu
}
