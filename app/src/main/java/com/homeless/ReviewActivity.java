package com.homeless;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.CheckBox;
import android.widget.EditText;

public class ReviewActivity extends AppCompatActivity {

    private EditText addressField;

    private CheckBox AC,bars,parking,elevators,accessFoDisabled,safetyRoom,terrace,sunTerrace,storage,renovated;
    private CheckBox shared,petsAllowed,isLongTermLease,isUnit,furnished,boiler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

    }
}
