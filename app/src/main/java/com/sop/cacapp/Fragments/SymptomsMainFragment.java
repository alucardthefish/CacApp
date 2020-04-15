package com.sop.cacapp.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sop.cacapp.Adapters.SymptomOccurrenceAdapter;
import com.sop.cacapp.Classes.Symptom;
import com.sop.cacapp.CustomViewClasses.CustomRecyclerView;
import com.sop.cacapp.LoaderDialog;
import com.sop.cacapp.Persistence.SymptomPersistent;
import com.sop.cacapp.R;

import java.util.ArrayList;
import java.util.List;


public class SymptomsMainFragment extends Fragment {

    private View view;

    private SymptomOccurrenceAdapter symptomAdapter;
    private CustomRecyclerView customRecyclerView;
    private ArrayList<Symptom> symptomArrayList;
    private TextView emptyView;
    private FloatingActionButton fabAddSymptom;
    private ListenerRegistration loadSymptomsListener;
    private LoaderDialog loaderDialog;

    private ActionMode actionMode;

    public SymptomsMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_symptoms_main, container, false);



        emptyView = view.findViewById(R.id.tvNoSymptoms);
        fabAddSymptom = view.findViewById(R.id.fabAddSymptom);
        loaderDialog = new LoaderDialog(view.getContext());
        loaderDialog.startLoading();

        customRecyclerView = view.findViewById(R.id.customRecyclerView);
        customRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        customRecyclerView.setEmptyView(emptyView);

        symptomArrayList = new ArrayList<>();

        symptomAdapter = new SymptomOccurrenceAdapter(view.getContext(), symptomArrayList);

        customRecyclerView.setAdapter(symptomAdapter);
        //feedRecycler();

        fabAddSymptom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSymptom();
                Snackbar.make(v, "Agregando nuevo sintoma", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        symptomAdapter.setOnClickListener(new SymptomOccurrenceAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, int position, Symptom symptom) {
                if (symptomAdapter.getSelectedSymptomsCount() > 0) {
                    enableActionMode(position);
                } else {
                    seeSymptom(symptom);

                }
            }

            @Override
            public void onItemLongClick(View view, int position, Symptom symptom) {
                enableActionMode(position);

                Toast.makeText(view.getContext(), "Se quiere seleccionar el elemento " + symptom.toString(), Toast.LENGTH_LONG).show();
            }
        });

        loadSymptoms();

        return view;
    }

    private void feedRecycler() {
        Symptom s1 = new Symptom("Hola mundo este es mi primer sintoma vamos a ver como nos va con la app", 2.0f);
        Symptom s2 = new Symptom("Como esta mi pierna hoy muy llevada pues me la tronche como un putas", 2.5f);
        Symptom s3 = new Symptom("Hola mundo no te digo mucho hoy", 4.0f);
        Symptom s4 = new Symptom("Hola mundo3", 4.0f);
        symptomArrayList.add(s1);
        symptomArrayList.add(s2);
        symptomArrayList.add(s3);
        symptomArrayList.add(s4);

        symptomAdapter = new SymptomOccurrenceAdapter(getContext(), symptomArrayList);
        customRecyclerView.setAdapter(symptomAdapter);
    }

    private void addSymptom() {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new CreateSymptomFragment()).addToBackStack("symptomMain").commit();
        //enableActionMode();
        //Symptom s1 = new Symptom("Hola mundo este es mi primer sintoma vamos a ver como nos va con la app", 2.0f);

        //SymptomPersistent symptomPersistent = new SymptomPersistent();
        //symptomPersistent.addSymptom(s1, view);
    }

    private void seeSymptom(Symptom symptom) {

        Bundle bundle = new Bundle();
        bundle.putFloat("symptomIntensity", symptom.getIntensity());
        bundle.putString("symptomDesc", symptom.getDescription());
        bundle.putLong("symptomDateLong", symptom.getOccurrenceTimestamp().toDate().getTime());

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ViewSymptomFragment viewSymptomFragment = new ViewSymptomFragment();
        viewSymptomFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.container, viewSymptomFragment).addToBackStack("symptomMain").commit();
    }

    private void loadSymptoms() {
        CollectionReference db = new SymptomPersistent().getSymptomsRef();
        Query query = db.orderBy("occurrenceTimestamp", Query.Direction.DESCENDING);
        loadSymptomsListener = query
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d("loadSymptoms", e.getMessage());
                            return;
                        }

                        //loaderDialog.startLoading();
                        Log.d("loaderDialog", "start");
                        String source = queryDocumentSnapshots != null &&
                                queryDocumentSnapshots.getMetadata().hasPendingWrites() ? "Local" : "Server";

                        if (!queryDocumentSnapshots.isEmpty()) {
                            /*for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                Symptom currentSymptom = ds.toObject(Symptom.class);
                                symptomArrayList.add(currentSymptom);
                            }*/

                            int counter = 1;
                            List<DocumentChange> changes = queryDocumentSnapshots.getDocumentChanges();
                            Log.d("LenChanges", "Number of changed elements: " + changes.size() + " and SOURCE = " + source);
                            for (DocumentChange dc : changes) {
                                Log.d("COUNTER", "Element " + counter + " Date_: " + dc.getDocument().getTimestamp("occurrenceTimestamp").toDate().toString());
                                switch (dc.getType()) {
                                    case ADDED:
                                        Log.d("ADDED", "Algun dato se agrego y su oldindex es " + dc.getOldIndex() + " y su newindex es " + dc.getNewIndex());
                                        symptomArrayList.add(dc.getNewIndex(), dc.getDocument().toObject(Symptom.class));
                                        break;
                                    case REMOVED:
                                        Log.d("REMOVED", "Algun dato se elimino y su oldindex es" + dc.getOldIndex() + " y su newindex es " + dc.getNewIndex());
                                        symptomArrayList.remove(dc.getOldIndex());
                                        break;
                                    case MODIFIED:
                                        Log.d("MODIFIED", "Algun dato se modifico y su oldindex es" + dc.getOldIndex() + " y su newindex es " + dc.getNewIndex());
                                        symptomArrayList.set(dc.getOldIndex(), dc.getDocument().toObject(Symptom.class));
                                        break;
                                    default:
                                        Log.d("DEFAULT", "Solo carga de dato");
                                        break;
                                }
                                counter++;
                            }
                            loaderDialog.stopLoading();
                            Log.d("loaderDialog", "stop");
                            symptomAdapter.notifyDataSetChanged();
                        }
                    }
                });
        if (symptomArrayList.isEmpty()) {
            loaderDialog.stopLoading();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SMF", "on Destroy fragment");
        loadSymptomsListener.remove();
        loaderDialog.stopLoading();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("SMF", "on Pause fragment");
        loadSymptomsListener.remove();
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            AppCompatActivity mainActivity = ((AppCompatActivity) getActivity());
            actionMode = mainActivity.startSupportActionMode(new ActionModeCallback());
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        symptomAdapter.toggleSelection(position);
        int count = symptomAdapter.getSelectedSymptomsCount();
        final int EDITION_ITEM = 0;

        if (count == 0) {
            actionMode.finish();
            fabAddSymptom.show();
        } else {
            if (count == 1) {
                actionMode.getMenu().getItem(EDITION_ITEM).setVisible(true);
            } else {
                actionMode.getMenu().getItem(EDITION_ITEM).setVisible(false);
            }
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
            fabAddSymptom.hide();
        }
    }

    private void deleteSymptoms() {
        SymptomPersistent symptomPersistent = new SymptomPersistent();
        symptomPersistent.deleteSymptoms(symptomAdapter.getSelectedSymptoms(), view);
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.symptoms_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            boolean flag = false;
            int id = item.getItemId();
            switch (id) {
                case R.id.action_delete:
                    deleteSymptoms();
                    mode.finish();
                    fabAddSymptom.show();
                    flag = true;
                    break;
                case R.id.action_edit:
                    mode.finish();
                    flag = true;
                default:
                    break;
            }

            return flag;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            symptomAdapter.clearSelectedSymptoms();
            actionMode = null;
            fabAddSymptom.show();
        }
    }
}
