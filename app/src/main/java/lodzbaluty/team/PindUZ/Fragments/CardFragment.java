package lodzbaluty.team.PindUZ.Fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import lodzbaluty.team.PindUZ.Objects.UserObject;
import lodzbaluty.team.PindUZ.Adapter.CardAdapter;
import lodzbaluty.team.PindUZ.Activity.MainActivity;
import lodzbaluty.team.PindUZ.R;
import lodzbaluty.team.PindUZ.Utils.SendNotification;
import lodzbaluty.team.PindUZ.Activity.ZoomCardActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Wyswietla karty
 */
public class CardFragment  extends Fragment {

    private CardAdapter cardAdapter;
    int searchDistance = 100;

    private FirebaseAuth mAuth;

    private String currentUId;

    private DatabaseReference usersDb;

    List<UserObject> rowItems;

    View view;

    public CardFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_card, container, false);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null)
            return view;
        currentUId = mAuth.getCurrentUser().getUid();

        fetchUserSearchParams();

        rowItems = new ArrayList<>();

        cardAdapter = new CardAdapter(getContext(), R.layout.item_card, rowItems );

        final SwipeFlingAdapterView flingContainer = view.findViewById(R.id.frame);

        flingContainer.setAdapter(cardAdapter);

        //Handling swipe of cards
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                cardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {

                UserObject obj = (UserObject) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                UserObject obj = (UserObject) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
                isConnectionMatch(userId);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });



        flingContainer.setOnItemClickListener((itemPosition, dataObject) -> {
            UserObject UserObject = (UserObject) dataObject;
            Intent i = new Intent(getContext(), ZoomCardActivity.class);
            i.putExtra("UserObject", UserObject);
            startActivity(i);
        });




        FloatingActionButton fabLike = view.findViewById(R.id.fabLike);
        FloatingActionButton fabNope = view.findViewById(R.id.fabNope);


        fabLike.setOnClickListener(v -> {
            if(rowItems.size()!=0)
                flingContainer.getTopCardListener().selectRight();
        });
        fabNope.setOnClickListener(v -> {
            if(rowItems.size()!=0)
                flingContainer.getTopCardListener().selectLeft();
        });

        return view;
    }


    GeoQuery geoQuery;
///////////Lokalizacja
    public void getCloseUsers(Location lastKnowLocation){
        rowItems.clear();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("location");
        GeoFire geoFire = new GeoFire(ref);

        if(geoQuery!=null)
            geoQuery.removeAllListeners();
        geoQuery = geoFire.queryAtLocation(new GeoLocation(lastKnowLocation.getLatitude(),lastKnowLocation.getLongitude()), 100);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                getUsersInfo(key);
            }
            @Override
            public void onKeyExited(String key) {
            }
            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            }
            @Override
            public void onGeoQueryReady() {
            }
            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        });
    }


    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUId).child("connections").child("yeps").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Toast.makeText(getContext(), "new Connection", Toast.LENGTH_LONG).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).child("ChatId").setValue(key);
                    usersDb.child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);

                    SendNotification sendNotification = new SendNotification();
                    sendNotification.SendNotification("check it out!", "new Connection!", dataSnapshot.getKey());

                    Snackbar.make(view.findViewById(R.id.layout), "new Connection!", Snackbar.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private String  userInterest = "Male";


    private void fetchUserSearchParams(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    if (dataSnapshot.child("interest").getValue() != null)
                        userInterest = dataSnapshot.child("interest").getValue().toString();
                    if (dataSnapshot.child("search_distance").getValue() != null)
                        searchDistance = Integer.parseInt(dataSnapshot.child("search_distance").getValue().toString());

                    ((MainActivity)getActivity()).isLocationEnable();
                    rowItems.clear();
                    cardAdapter.notifyDataSetChanged();


                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getUsersInfo(String userId){
        for(UserObject mCard : rowItems){
            if(mCard.getUserId().equals(userId)){return;}
        }
        usersDb.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){return;}

                for(UserObject mCard : rowItems){
                    if(mCard.getUserId().equals(dataSnapshot.getKey())){return;}
                }

                UserObject mUser = new UserObject();
                mUser.parseObject(dataSnapshot);

                if(mUser.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){return;}
                if(dataSnapshot.child("connections").child("nope").hasChild(currentUId)){return;}
                if(dataSnapshot.child("connections").child("yeps").hasChild(currentUId)) {return;}
                if(!mUser.getUserSex().equals(userInterest) && !userInterest.equals("Both")){return;}

                for(UserObject mCard : rowItems){
                    if(mCard.getUserId().equals(userId)){return;}
                }

                rowItems.add(mUser);
                cardAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}