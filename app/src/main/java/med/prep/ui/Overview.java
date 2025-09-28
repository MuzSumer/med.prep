package med.prep.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import med.prep.R;
import med.prep.model.DiagramUtil;
import med.prep.model.dialog.EditorProperties;
import med.prep.model.dialog.RemoveMany;
import med.prep.model.impl.DiagramExpose;
import med.prep.model.impl.DiagramStore;
import med.prep.model.meta.Store;
import med.prep.model.meta.UniversalModel;

public class Overview extends Fragment implements TextToSpeech.OnInitListener {


    final static String namespace = "medprep.xml";

    DiagramExpose expo;
    public DiagramExpose expo() { return expo; }



    int emergency = 11;
    int order = 37;

    String fullname;
    String birthdate;


    TextToSpeech tts;
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.GERMAN);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getContext(), "error tts", Toast.LENGTH_LONG).show();
            }

        }
    }
    private void speak(String subject) {

        if (!Reports.speakMode(getContext())) return;
        tts.speak(subject, TextToSpeech.QUEUE_FLUSH, null, null);
        //Toast.makeText(getContext(), subject, Toast.LENGTH_SHORT).show();

    }

    private void loadPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());


        String e = preferences.getString("emergency", "");
        if (!e.isEmpty()) {
            emergency = Integer.parseInt(e);
        }

        String o = preferences.getString("order", "");
        if (!o.isEmpty()) {
            order = Integer.parseInt(o);
        }

        fullname = preferences.getString("FirstName", "") + " " + preferences.getString("LastName", "");
        birthdate = preferences.getString("BirthDate", "");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.diagram_overview, container, false);



        expo = new DiagramExpose(getContext(), view.findViewById(R.id.diagram), view.findViewById(R.id.scroll));

        Store store = new DiagramStore(expo(), namespace);
        expo().createStore(store, namespace, "");

        registerActions(view);


        loadPreferences();

        tts = new TextToSpeech(getContext(), this);


        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        expo().getDiagram().setLayoutManager(manager);

        ModelAdapter adapter = new ModelAdapter(expo().getContext());
        expo().getDiagram().setAdapter(adapter);


        return view;
    }




    private String body() {

        String body = fullname + ", " + birthdate + "\n\n";

        for (UniversalModel model : expo().getStore().getModels()) {


            String result = "";

            long restdays = Reports.restdays(model, expo.getStore().today());


            result = "noch " + restdays + " Tage";
            //days + " Tage   " + benutzt + "/" + vorrat + " Tabletten"


            body = body + "\n" + model.getSubject() + " " + result;


        }
        return body;
    }


    private void registerActions(View view) {
        view.findViewById(R.id.record_add).setOnClickListener(
                v -> {

                    UniversalModel model = null;

                    Resources res = getContext().getResources();

                    String[] array_type = res.getStringArray(R.array.type);
                    ArrayList<String> types = new ArrayList<>(Arrays.asList(array_type));


                    String[] array_state = res.getStringArray(R.array.state);
                    ArrayList<String> states = new ArrayList<>(Arrays.asList(array_state));


                    EditorProperties editor = new EditorProperties(expo(), types, states, model);
                    editor.show(getChildFragmentManager(), "");

                    
                    /*
                    UniversalModel model = expo().getStore().createDefaultModel("Anwendungsgebiet", "Medikament");
                        //expo().getStore().saveLocalModel(expo(), expo().getFolder());

                        expo().setFocus(model.getId(), false);

                        expo().scrollToEnd();
                     */
                }
        );

        view.findViewById(R.id.record_remove).setOnClickListener(
                v -> {

                    RemoveMany editor = new RemoveMany(expo());
                    editor.show(getChildFragmentManager(), "remove");
                }
        );


        view.findViewById(R.id.record_share).setOnClickListener(
                v -> {

                    Toast.makeText(getContext(), getString(R.string.report_generation), Toast.LENGTH_SHORT).show();
                    speak(getString(R.string.report_generation));

                    Toast.makeText(getContext(), getString(R.string.action_data_warning), Toast.LENGTH_LONG).show();
                    speak(getString(R.string.action_data_warning));

                    Intent intent = new Intent(getContext(), OverviewReport.class);
                    intent.putExtra("namespace", namespace);

                    view.getContext().startActivity(intent);

                }
        );


        view.findViewById(R.id.record_search).setOnClickListener(
                v -> {


                    long average_days = 0;
                    long best_days = 0;
                    long worst_days = 360;

                    for (UniversalModel model : expo().getStore().getModels()) {

                        long restdays = Reports.restdays(model, expo().getStore().today());

                        average_days = (average_days + restdays)/2;

                        if (restdays > best_days) { best_days = restdays; }

                        if (restdays < worst_days) { worst_days = restdays; }


                    }//next model



                    String result = "";

                    if (worst_days < emergency) {

                        /* Nachschub umgehend erforderlich */
                        result = getString(R.string.analyze_emergency);



                    } else if (worst_days < order) {

                        /* Nachschub baldmöglich empfohlen */
                        result = getString(R.string.analyze_stockup);


                    } else {



                        long diff = worst_days - order;


                        // Nachschub in # Tagen
                        result = getString(R.string.analyze_ok).replace("%s", Long.toString(diff));


                    }

                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                    speak(result);


                    /* email
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/html");
                    //intent.putExtra(Intent.EXTRA_EMAIL, "");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Bestand");
                    intent.putExtra(Intent.EXTRA_TEXT, body());

                    Toast.makeText(getContext(), getString(R.string.action_email_warning), Toast.LENGTH_LONG).show();
                    startActivity(Intent.createChooser(intent, "Send Email"));
                     */ //email

                }
        );
    }


    private void customViewItem(DiagramExpose.UniversalModelViewHolder mv, UniversalModel model) {

        /*
        mv.getTitle().setOnClickListener(selectCell());
        mv.getSubject().setOnClickListener(selectCell());
        mv.getDate().setOnClickListener(editCell());
        mv.getType().setOnClickListener(editCell());
        mv.getState().setOnClickListener(editCell());
        mv.getOpenLocation().setOnClickListener(openMap());
        mv.getLocation().setOnClickListener(wrongLocation());
        mv.getImage().setOnClickListener(editCell());
         */


        {
            mv.getSubject().setText(model.getSubject() + " " + model.getContent());
        }//subject

        {
            Resources res = getContext().getResources();

            String[] array_type = res.getStringArray(R.array.type_speak);
            ArrayList<String> types = new ArrayList<>(Arrays.asList(array_type));

            int index = Integer.parseInt(model.getType());


            long restdays = Reports.restdays(model, expo.getStore().today());
            String result = ", noch " + restdays + " Tage";

            mv.getTags().setTextColor(Color.GRAY);
            //mv.getTags().setTextColor(getColor(getContext(), android.R.color.system_primary_light));

            if (restdays < emergency) {
                result = ", nur noch " + restdays + " Tage";
                mv.getTags().setTextColor(Color.RED);
            }

            mv.getTags().setText(types.get(index) + result);
        }//tags




        // model editor
        mv.getImage().setOnClickListener(editCell());


        // stock up
        mv.getDate().setOnClickListener(v -> {

            if (Reports.quickMode(getContext())) {
                expo().setFocus(model.getId(), false);
                expo().redraw(true);
                speak(model.getSubject());

                StockUpDialog dialog = new StockUpDialog(expo(), model);
                dialog.show(getChildFragmentManager(), "");
                expo().redraw(true);

                return;
            }

            UniversalModel focused = expo().getSelectedModel();

            if (focused == null) {
                expo().setFocus(model.getId(), false);
                expo().redraw(true);

                speak(model.getSubject());
                return;
            }

            if (model.getId() != focused.getId()) {
                expo().setFocus(model.getId(), false);
                expo().redraw(true);

                speak(model.getSubject());
                return;
            }

            if (model.getId() == focused.getId()) {
                expo().setFocus(model.getId(), false);
                expo().redraw(true);

                speak(model.getSubject());
                StockUpDialog dialog = new StockUpDialog(expo(), model);
                dialog.show(getChildFragmentManager(), "");
                expo().redraw(true);
            }

        });


    }


    class ModelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            UniversalModel model = expo().getStore().getModelAt(position);
            String id = model.getId();

            DiagramExpose.UniversalModelViewHolder mv = (DiagramExpose.UniversalModelViewHolder) holder;


            mv.itemView.setContentDescription(id);
            mv.itemView.setOnClickListener(openCell());



            ConstraintLayout layout = mv.getLayout();
            layout.setContentDescription(id);


            // hovering
            /*
            mv.getLayout().setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    String id1 = v.getContentDescription().toString();

                    message(id);
                }
            });

            mv.getLayout().setOnHoverListener((view1, event) -> {

                String id1 = view1.getContentDescription().toString();


                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        message("enter " + id1);
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                        message("move " + id1);
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        message("exit " + id1);
                        break;
                }
                return false;
            });
             */


            {
                if (id.equals(expo().getSelected())) {
                    mv.itemView.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.app_rbox_selected));
                    registerForContextMenu(mv.getImage());
                } else {
                    mv.itemView.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.app_rbox_background));
                    unregisterForContextMenu(mv.getImage());
                }
            }// selection


            {
                if (id.equals(expo().getSelected())) {
                    layout.post(() -> {
                        int l = layout.getMeasuredWidth()*3/5;
                        // select
                        Drawable d = getContext().getDrawable(R.drawable.item_dot_green);
                        int t = 23;
                        DiagramUtil.setDBounds(d, 16, l, t);

                        // edit
                        Drawable e = getContext().getDrawable(R.drawable.item_dot_blue);
                        t = 77;
                        DiagramUtil.setDBounds(e, 16, l, t);

                        // location
                        Drawable f = getContext().getDrawable(R.drawable.item_dot_red);
                        t = layout.getMeasuredHeight() - 23;
                        DiagramUtil.setDBounds(f, 16, l, t);


                        // image
                        Drawable g = getContext().getDrawable(R.drawable.item_dot_white);
                        l = mv.getImage().getMeasuredWidth()/2;
                        t = mv.itemView.getMeasuredHeight()/2;
                        DiagramUtil.setDBounds(g, 32, l, t);



                        layout.getOverlay().clear();
                        layout.getOverlay().add(d);
                        layout.getOverlay().add(e);
                        layout.getOverlay().add(f);
                        //layout.getOverlay().add(g);
                    });
                } else {
                    layout.post(() -> {
                        int l = layout.getMeasuredWidth()*3/5;

                        Drawable d = getContext().getDrawable(R.drawable.item_dot_yellow);
                        int t = 23;

                        DiagramUtil.setDBounds(d, 16, t, l);


                        layout.getOverlay().clear();
                        layout.getOverlay().add(d);
                        //layout.getOverlay().add(e);
                    });
                }
            }// overlay



            {

                mv.getDate().setText(model.getDate() + " \uD83D\uDCB1");
                mv.getDate().setContentDescription(id);



                try {
                    mv.getType().setText(model.getType());
                    mv.getState().setText(model.getState());
                } catch (Exception e) {
                    mv.getType().setText(model.getType());
                    mv.getState().setText(model.getState());
                }


                mv.getType().setContentDescription(id);
                mv.getState().setContentDescription(id);

                //mv.getType().setOnClickListener(editCell());
                //mv.getState().setOnClickListener(editCell());

            }// date, type, state


            {
                mv.getTitle().setContentDescription(id);
                mv.getTitle().setText(model.getTitle());

                mv.getSubject().setContentDescription(id);
                mv.getSubject().setText(model.getSubject());

            }// title, subject


            {
                mv.getContent().setText(model.getContent());
                mv.getSpecs().setText(model.getSpecs());
                mv.getTags().setText(model.getTags());
            }// content, specs, tags


            {
                mv.getOpenLocation().setContentDescription(id);
                mv.getLocation().setContentDescription(id);

                mv.getLocation().setText(model.getLocation());
            }// location


            {
                mv.getImage().setContentDescription(id);
                expo().setImage(mv.getImage(), model.getSymbol(), getResources().getInteger(R.integer.cell_size_small));

            }// image


            customViewItem(mv, model);
        }







        Context context;
        public ModelAdapter(Context classContext) {
            context = classContext;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return expo().createViewHolder(LayoutInflater.from(context).inflate(R.layout.item_overview, parent, false));
        }

        @Override
        public int getItemCount() {
            return expo().getStore().size();
        }


    }//ModelAdapter





    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, v.getId(), 0, getString(R.string.dialog_selected_up));
        menu.add(0, v.getId(), 0, getString(R.string.dialog_selected_down));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        UniversalModel source = expo.getStore().findModel(expo.getSelected());
        if (source == null) {
            Toast.makeText(getContext(), getString(R.string.report_select_model), Toast.LENGTH_SHORT).show();
            return false;
        }


        int p0 = findPosition(expo.getSelected());


        if (item.getTitle() == getString(R.string.dialog_selected_up)) {

            int p1 = p0 - 1;
            if (p1 < 0) { return false; }

            UniversalModel older = expo.getStore().getModelAt(p1);
            expo.getStore().swapModel(source, older);
            expo.setSelected(older.getId());

            expo.getStore().saveLocalModel(expo, expo.getFolder());

            expo.redraw(true);
            expo.setFocus(expo.getSelected(), false);
        }

        if (item.getTitle() == getString(R.string.dialog_selected_down)) {

            int p1 = p0 + 1;
            if (p1 > expo.getStore().size() - 1) { return false; }


            UniversalModel younger = expo.getStore().getModelAt(p1);
            expo.getStore().swapModel(source, younger);
            expo.setSelected(younger.getId());

            expo.getStore().saveLocalModel(expo, expo.getFolder());

            expo.redraw(true);
            expo.setFocus(expo.getSelected(), false);
        }

        return true;
    }

    public int findPosition(String id) {
        int found = -1;

        int p = 0;
        for (UniversalModel model : expo.getStore().getModels()) {
            if (model.getId().equals(id)) {

                if (found < 0) { // find first
                    found = p;
                }
            }
            p++;
        }

        return found;
    }









    public View.OnClickListener selectCell() { return cellSelect; }


    public View.OnClickListener openCell() { return cellOpen; }


    public View.OnClickListener editCell() { return cellEdit; }



    private final View.OnClickListener cellSelect = view -> {
        String id = view.getContentDescription().toString();

        expo().setFocus(id, false);

        UniversalModel m = expo().getStore().findModel(id);

        speak(m.getSubject());

    };

    private final View.OnClickListener cellEdit = view -> {
        String id = view.getContentDescription().toString();
        UniversalModel model = expo().getStore().findModel(id);

        speak(model.getSubject());

        if (Reports.quickMode(getContext())) {

            expo().setFocus(model.getId(), false);
            expo().redraw(false);



            Resources res = getContext().getResources();

            String[] array_type = res.getStringArray(R.array.type_speak);
            ArrayList<String> types = new ArrayList<>(Arrays.asList(array_type));


            String[] array_state = res.getStringArray(R.array.state);
            ArrayList<String> states = new ArrayList<>(Arrays.asList(array_state));

            EditorProperties editor = new EditorProperties(expo(), types, states, model);
            editor.show(getChildFragmentManager(), "");

            return;
        }

        UniversalModel focused = expo().getSelectedModel();

        if (focused == null) {
            expo().setFocus(model.getId(), false);
            expo().redraw(false);

            return;
        }

        if (model.getId() != focused.getId()) {
            expo().setFocus(model.getId(), false);
            expo().redraw(true);

            return;
        }

        if (model.getId() == focused.getId()) {

            Resources res = getContext().getResources();

            String[] array_type = res.getStringArray(R.array.type_speak);
            ArrayList<String> types = new ArrayList<>(Arrays.asList(array_type));


            String[] array_state = res.getStringArray(R.array.state);
            ArrayList<String> states = new ArrayList<>(Arrays.asList(array_state));

            EditorProperties editor = new EditorProperties(expo(), types, states, model);
            editor.show(getChildFragmentManager(), "");
        }


    };

    private final View.OnClickListener cellOpen = view -> {
        String id = view.getContentDescription().toString();

        if (!expo().getSelected().equals(id)) {

            expo().setFocus(id, false);

            UniversalModel model = expo().getStore().findModel(id);
            String subject = model.getSubject();

            speak(subject);
            return;
        }


        /*
        Intent intent = new Intent(getActivity(), ViewPlace.class);
        intent.putExtra("namespace", expo().getNamespace());
        intent.putExtra("folder", expo().getFolder());
        intent.putExtra("id", id);

        startActivity(intent);
         */

    };

}