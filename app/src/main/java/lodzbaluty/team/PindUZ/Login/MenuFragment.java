package lodzbaluty.team.PindUZ.Login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import lodzbaluty.team.PindUZ.R;


/**
 * wybor czy rejestracja czy logowanie
 */
public class MenuFragment extends Fragment implements View.OnClickListener {

    Button mLogin, mRegistration;

    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_menu, container, false);
        else
            container.removeView(view);


        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeObjects();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.registration:
                ((AuthenticationActivity) getActivity()).registrationClick();
                break;
            case R.id.login:
                ((AuthenticationActivity) getActivity()).loginClick();
                break;
        }
    }

    private void initializeObjects(){
        mLogin = view.findViewById(R.id.login);
        mRegistration = view.findViewById(R.id.registration);

        mRegistration.setOnClickListener(this);
        mLogin.setOnClickListener(this);

    }
}