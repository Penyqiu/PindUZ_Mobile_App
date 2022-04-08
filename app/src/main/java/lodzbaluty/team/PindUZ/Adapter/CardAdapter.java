package lodzbaluty.team.PindUZ.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import lodzbaluty.team.PindUZ.Objects.UserObject;
import lodzbaluty.team.PindUZ.R;

import java.util.List;


/**
 * Handlowanie kart usera
 */
public class CardAdapter extends ArrayAdapter<UserObject>{

    Context context;

    public CardAdapter(Context context, int resourceId, List<UserObject> items){
        super(context, resourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        UserObject card_item = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_card, parent, false);
        }

        TextView name = convertView.findViewById(R.id.name);
        ImageView image = convertView.findViewById(R.id.image);

        name.setText(card_item.getName() + ", " + card_item.getAge());

        if(!card_item.getProfileImageUrl().equals("default"))
            Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);



        return convertView;

    }
}
