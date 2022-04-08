package lodzbaluty.team.PindUZ.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class SendNotification {

    public void SendNotification(final String message, final String heading,final String sendUid)
    {
        FirebaseDatabase.getInstance().getReference().child("Users").child(sendUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                    return;

                String notificationKey = "";

                if (dataSnapshot.child("notificationKey").getValue()!=null)
                    notificationKey = dataSnapshot.child("notificationKey").getValue().toString();

                try {
                    JSONObject notificationContent = new JSONObject("{'contents': {'en': '"+ message + "'}," +
                            "'include_player_ids': ['" + notificationKey + "'], " +
                            "'headings': {'en': '"+ heading + "'}}");
                    OneSignal.postNotification(notificationContent, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}