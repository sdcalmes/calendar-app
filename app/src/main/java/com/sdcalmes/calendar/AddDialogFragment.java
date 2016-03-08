package com.sdcalmes.calendar;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddDialogFragment extends DialogFragment {


    private Button mAddEvent;
    private EditText mDescription;
    private EditText mTitle;
    private TimePicker mTimePicker;

    public AddDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_add_dialog, container);

        mAddEvent = (Button)view.findViewById(R.id.button);
        mTitle = (EditText)view.findViewById(R.id.editText2);
        mDescription = (EditText)view.findViewById(R.id.editText);
        mTimePicker = (TimePicker)view.findViewById(R.id.timePicker);
        mAddEvent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                System.out.println("TITLE: " + mTitle.getText());
                if(mTitle.getText().toString().matches("") ||
                        mDescription.getText().toString().matches("")){
                    Toast.makeText(getActivity(), "You need to include all information!", Toast.LENGTH_SHORT).show();
                }
                else {
                    ((MainActivity) getActivity()).getData(mTitle.getText().toString(), mDescription.getText().toString(),
                            mTimePicker.getCurrentHour(),
                            mTimePicker.getCurrentMinute());
                    dismiss();
                }
            }
        });

        return view;
    }

}
