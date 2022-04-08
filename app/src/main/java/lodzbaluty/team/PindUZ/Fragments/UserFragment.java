package lodzbaluty.team.PindUZ.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import lodzbaluty.team.PindUZ.Activity.EditProfileActivity;
import lodzbaluty.team.PindUZ.Objects.UserObject;
import lodzbaluty.team.PindUZ.R;
import lodzbaluty.team.PindUZ.Activity.SettingsActivity;

/**
 * Panel uzytkownika
 */
public class UserFragment extends Fragment {

    private TextView mName;

    private ImageView mProfileImage, mSettings, mEditProfile;


    public UserFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);

        mName = view.findViewById(R.id.name);
        mProfileImage = view.findViewById(R.id.profileImage);

        mSettings = view.findViewById(R.id.settings);
        mEditProfile = view.findViewById(R.id.editProfile);

        mEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivity(intent);
        });
        mSettings.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        });

        getUserInfo();

        return view;
    }


    private void getUserInfo() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserObject mUser = new UserObject();
                mUser.parseObject(dataSnapshot);


                mName.setText(mUser.getName() + ", " + mUser.getAge());
                if(getContext() != null && !mUser.getProfileImageUrl().equals("default"))
                    Glide.with(getContext()).load(mUser.getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}