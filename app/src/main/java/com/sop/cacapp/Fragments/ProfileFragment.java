package com.sop.cacapp.Fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sop.cacapp.Classes.HealthBackground;
import com.sop.cacapp.Classes.OccurrenceDateTimeHandler;
import com.sop.cacapp.Classes.Profile;
import com.sop.cacapp.MainActivity;
import com.sop.cacapp.Persistence.PoopOccurrencePersistent;
import com.sop.cacapp.Persistence.ProfilePersistent;
import com.sop.cacapp.R;

import java.util.Date;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private View rootView;

    private ImageView ivGenderAvatar;
    private TextView tvProfileUsername;
    private TextView tvProfileNumDays;
    private TextView tvProfileNumSymptoms;
    private TextView tvProfileNumDepositions;
    private TextView tvActivateEdition;

    private TextView tvProfileEmail;
    private TextView tvProfileAge;
    private TextView tvProfileWeight;
    private TextView tvProfileHeight;
    private TextView tvSurgicalProcedure;
    private TextView tvDiagnosedConditions;
    private TextView tvAllergies;
    private TextView tvFamilyBackground;

    private Profile mProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews();
        initListeners();
        loadData();


        return rootView;
    }

    private void initViews() {
        ivGenderAvatar = rootView.findViewById(R.id.ivGenderAvatar);
        tvProfileUsername = rootView.findViewById(R.id.tvProfileUsername);
        tvProfileNumDays = rootView.findViewById(R.id.tvProfileNumDays);
        tvProfileNumSymptoms = rootView.findViewById(R.id.tvProfileNumSymptoms);
        tvProfileNumDepositions = rootView.findViewById(R.id.tvProfileNumDepositions);

        tvActivateEdition = rootView.findViewById(R.id.tvActivateEdition);

        tvProfileEmail = rootView.findViewById(R.id.tvProfileEmail);
        tvProfileAge = rootView.findViewById(R.id.tvProfileAge);
        tvProfileWeight = rootView.findViewById(R.id.tvProfileWeight);
        tvProfileHeight = rootView.findViewById(R.id.tvProfileHeight);

        tvAllergies = rootView.findViewById(R.id.tvAllergies);
        tvSurgicalProcedure = rootView.findViewById(R.id.tvSurgicalProcedure);
        tvDiagnosedConditions = rootView.findViewById(R.id.tvDiagnosedConditions);
        tvFamilyBackground = rootView.findViewById(R.id.tvFamilyBackground);

        mProfile = null;
    }

    private void initListeners() {
        tvActivateEdition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProfile != null) {
                    Toast.makeText(rootView.getContext(), "A editar won", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(rootView.getContext(), "No se puede editar en el momento", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadData() {
        loadCounters();
        loadProfileData();
    }

    private void loadProfileData() {
        loadAvatar();
        ProfilePersistent profilePersistent = new ProfilePersistent();

        profilePersistent.GetProfile(new ProfilePersistent.MyCallback() {
            @Override
            public void onCallBack(boolean isSuccess, Profile profile) {
                if (isSuccess) {
                    mProfile = profile;
                    tvProfileUsername.setText(profile.getName());

                    OccurrenceDateTimeHandler timer = new OccurrenceDateTimeHandler(profile.getRegisterDate(), rootView.getContext());
                    tvProfileNumDays.setText(Integer.toString(timer.getTimeElapsedInDaysToDate(new Date())));
                    tvProfileEmail.setText(profile.getEmail());
                    timer = new OccurrenceDateTimeHandler(profile.getBirthDate(), rootView.getContext());
                    tvProfileAge.setText(Integer.toString(timer.getTimeElapsedInYearsToDate(new Date())));
                    tvProfileHeight.setText(Integer.toString(profile.getHeight()));
                    tvProfileWeight.setText(Double.toString(profile.getWeight()));

                    HealthBackground healthBackground = profile.getHealthBackground();
                    tvAllergies.setText(healthBackground.getAlergies());
                    tvDiagnosedConditions.setText(healthBackground.getConditionsOrIllnessesDiagnosed());
                    tvSurgicalProcedure.setText(healthBackground.getSurgicalProcedures());
                    tvFamilyBackground.setText(healthBackground.getFamilyBackground());
                }
            }
        });

    }

    private void loadAvatar() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getPhotoUrl().toString().equals("male")) {
                ivGenderAvatar.setImageResource(R.drawable.icons8maleuser100);
            }
        }
    }

    private void loadCounters() {
        PoopOccurrencePersistent poopOccurrencePersistent = new PoopOccurrencePersistent();
        poopOccurrencePersistent.getCalculatedData(new PoopOccurrencePersistent.CalculatedDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> calculatedData) {
                long numDepos = (long) calculatedData.get("deposition_counter");
                long numSymps = (long) calculatedData.get("symptoms_counter");
                tvProfileNumDepositions.setText(String.valueOf(numDepos));
                tvProfileNumSymptoms.setText(String.valueOf(numSymps));
            }

            @Override
            public void onFailure(String message) {
                Log.d("loadCounters", message);
            }
        });
    }
}
