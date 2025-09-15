package med.prep.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import med.prep.R;
import med.prep.model.DiagramUtil;
import med.prep.model.dialog.EditorProperties;
import med.prep.model.impl.DiagramExpose;
import med.prep.model.impl.DiagramStore;
import med.prep.model.meta.Store;
import med.prep.model.meta.UniversalModel;

public class Analyzer extends Fragment {

    final static String namespace = "medprep.xml";


    DiagramExpose expo;
    public DiagramExpose expo() { return expo; }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.diagram_analyzer, container, false);


        expo = new DiagramExpose(getContext(), view.findViewById(R.id.diagram), view.findViewById(R.id.scroll));

        Store store = new DiagramStore(expo(), namespace);
        expo().createStore(store, namespace, "");

        registerActions(view);


        //store.createDefaultModel("A", "A");

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        expo().getDiagram().setLayoutManager(manager);

        ModelAdapter adapter = new ModelAdapter(expo().getContext());
        expo().getDiagram().setAdapter(adapter);


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void registerActions(View view) {
        view.findViewById(R.id.record_add).setOnClickListener(
                v -> {
                    Toast.makeText(getContext(), R.string.diagram_notice_error, Toast.LENGTH_SHORT).show();
                }
        );

        view.findViewById(R.id.record_remove).setOnClickListener(
                v -> {
                    Toast.makeText(getContext(), R.string.diagram_notice_error, Toast.LENGTH_SHORT).show();
                }
        );


        view.findViewById(R.id.record_share).setOnClickListener(
                v -> {

                    Toast.makeText(getContext(), "loading...", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getContext(), AnalyzerReport.class);
                    intent.putExtra("namespace", namespace);

                    view.getContext().startActivity(intent);

                }
        );


        view.findViewById(R.id.record_search).setOnClickListener(
                v -> {

                    Toast.makeText(getContext(), "coming soon...", Toast.LENGTH_SHORT).show();

                }
        );
    }




    public View.OnClickListener selectCell() { return cellSelect; }


    public View.OnClickListener openCell() { return cellOpen; }


    public View.OnClickListener editCell() { return cellEdit; }



    private final View.OnClickListener cellSelect = view -> {
        String modelId = view.getContentDescription().toString();

        //getDiagram().setSelected(modelId);

        expo().setFocus(modelId, false);

        UniversalModel m = expo().getStore().findModel(modelId);

    };

    private final View.OnClickListener cellEdit = view -> {
        String id = view.getContentDescription().toString();

        expo().setFocus(id, false);

        UniversalModel model = expo().getStore().findModel(id);



        Resources res = getContext().getResources();

        String[] array_type = res.getStringArray(R.array.type);
        ArrayList<String> types = new ArrayList<>(Arrays.asList(array_type));


        String[] array_state = res.getStringArray(R.array.state);
        ArrayList<String> states = new ArrayList<>(Arrays.asList(array_state));


        EditorProperties editor = new EditorProperties(expo(), types, states, model);
        editor.show(getChildFragmentManager(), "");
    };

    private final View.OnClickListener cellOpen = view -> {
        String id = view.getContentDescription().toString();

        if (!expo().getSelected().equals(id)) {
            //getDiagram().setSelected(id);
            expo().setFocus(id, false);

            UniversalModel model = expo().getStore().findModel(id);
            String subject = model.getSubject();

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












    class ModelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
                mv.getDate().setText(model.getDate());
                mv.getDate().setContentDescription(id);
                //mv.getDate().setOnClickListener(editCell());


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
                mv.getTitle().setText(model.getSubject());
                mv.getTitle().setContentDescription(id);
                //mv.getTitle().setOnClickListener(selectCell());
                /*
                mv.getTitle().addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String id = mv.getTitle().getContentDescription().toString();
                        UniversalModel edit = expo().getStore().findModel(id);

                        edit.setTitle(DiagramUtil.trim(mv.getTitle()));
                        expo().getStore().saveLocalModel(expo(), expo().getFolder());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                 */



                mv.getSubject().setText(model.getTitle());
                mv.getSubject().setContentDescription(id);
                //mv.getSubject().setOnClickListener(selectCell());
                /*
                mv.getSubject().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String id = mv.getTitle().getContentDescription().toString();
                        UniversalModel edit = expo().getStore().findModel(id);

                        edit.setSubject(DiagramUtil.trim(mv.getSubject()));
                        expo().getStore().saveLocalModel(expo(), expo().getFolder());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                 */

            }// title, subject


            {
                mv.getContent().setText(model.getContent());
                mv.getSpecs().setText(model.getSpecs());
                mv.getTags().setText(model.getTags());
            }// content, specs, tags


            {
                mv.getOpenLocation().setContentDescription(id);
                //mv.getOpenLocation().setOnClickListener(openMap());

                //mv.getLocation().setText(shortLocation(model.getLocation(), 1));
                mv.getLocation().setContentDescription(id);


                // TODO analyze
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);


                    Date model_day = sdf.parse(model.getDate());
                    Date today = sdf.parse(expo().getStore().today());


                    long diffInMillies = Math.abs(today.getTime() - model_day.getTime());
                    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);



                    int type = Integer.parseInt(model.getType());
                    int tagesdosis = 0;

                    switch (type) {

                        case 0:
                            tagesdosis = 1;
                            break;

                        case 1:
                            tagesdosis = 2;
                            break;

                        case 2:
                            tagesdosis = 3;
                            break;

                        case 3:
                            tagesdosis = 3;
                            break;

                        case 4:
                            tagesdosis = 5;
                            break;


                        case 5:
                            tagesdosis = 1;
                            break;

                        case 6:
                            tagesdosis = 1;
                            break;

                        case 7:
                            tagesdosis = 2;
                            break;

                        default:
                            tagesdosis = 0;
                    }

                    long benutzt = diff * tagesdosis;



                    int vorrat = 0;

                    if (!model.getCoordinates().isEmpty()) {

                        vorrat = Integer.parseInt(model.getCoordinates());
                    }


                    long rest = vorrat - benutzt;

                    long restdays = rest/tagesdosis;




                    //mv.getLocation().setText(diff + " Tage   " + benutzt + "/" + vorrat + " Tabletten");

                    mv.getLocation().setText(vorrat + " Stück, noch " + restdays + " Tage");




                    if (restdays < 11) {
                        mv.getLocation().setTextColor(Color.RED);
                        mv.getLocation().setText(vorrat + " Stück, nur " + restdays + " Tage");
                    }

                } catch (Exception e) {
                    mv.getLocation().setText(model.getDate());
                }





                //mv.getLocation().setOnClickListener(wrongLocation());
            }// location


            {
                mv.getImage().setContentDescription(id);
                //mv.getImage().setOnClickListener(editCell());


                expo().setImage(mv.getImage(), model.getSymbol(), getResources().getInteger(R.integer.cell_size_small));

            }// image

        }







        Context context;
        public ModelAdapter(Context classContext) {
            context = classContext;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return expo().createViewHolder(LayoutInflater.from(context).inflate(R.layout.app_analyzer, parent, false));
        }

        @Override
        public int getItemCount() {
            return expo().getStore().size();
        }


    }//ModelAdapter
}