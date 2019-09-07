package algebra.hr.aadbdt.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import algebra.hr.aadbdt.R;
import algebra.hr.aadbdt.ui.classes.ViewDialog;
import algebra.hr.database.DatabaseOpenHelper;
import algebra.hr.database.User;

public class FragmentProfil extends Fragment {
    DatabaseOpenHelper db;
    User user;
    ImageView edit;
    TextView tvName, tvUsername, tvNameSurname, tvPassword, tvEmail, tvDateStart, tvDateEnd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profil_fragment, container, false);

        db = new DatabaseOpenHelper(getActivity());
        user = db.getUser();

        setControls(view);
        setValues();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditUserDialog(user);
            }
        });

        return view;
    }

    private void setControls(View view) {
        edit = (ImageView) view.findViewById(R.id.edit);
        if (user.getROLE().equals("Anonimn")) {
            edit.setVisibility(View.INVISIBLE);
        }
        tvName = (TextView) view.findViewById(R.id.name);
        tvUsername = (TextView) view.findViewById(R.id.tvUsername);
        tvNameSurname = (TextView) view.findViewById(R.id.tvNameSurname);
        tvPassword = (TextView) view.findViewById(R.id.tvPassword);
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        tvDateStart = (TextView) view.findViewById(R.id.tvDateStart);
        tvDateEnd = (TextView) view.findViewById(R.id.tvDateEnd);
    }

    private void setValues() {
        tvName.setText(user.getNAME_SURNAME());
        tvUsername.setText(user.getUSERNAME());
        tvNameSurname.setText(user.getNAME_SURNAME());
        tvEmail.setText(user.getEMAIL());
        tvDateStart.setText(formatDate(user.getPACKET_DATE_START()));
        tvDateEnd.setText(formatDate(user.getPACKET_DATE_END()));
    }

    private String formatDate(String old_date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(old_date);
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy. HH:mm:ss");
            return sdf2.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    private void showEditUserDialog(User u) {
        ViewDialog alert = new ViewDialog();
        alert.showDialog(getActivity(), "", u);
    }

    @Override
    public void onResume() {
        super.onResume();
        user = db.getUser();
        setValues();
    }
}
