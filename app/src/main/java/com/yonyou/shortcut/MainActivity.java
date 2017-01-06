package com.yonyou.shortcut;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText nameEt;
    private EditText valueEt;
    private Spinner actionSpinner;
    private ArrayList<Action> actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameEt = (EditText) findViewById(R.id.edit_name);
        valueEt = (EditText) findViewById(R.id.edit_action_value);
        actionSpinner = (Spinner) findViewById(R.id.spinner_action);
        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCreateClick();
            }
        });
        actions = ShortCutUtils.getAllAction();
        ArrayAdapter<Action> actionAdapter = new ArrayAdapter<Action>(this, android.R.layout.simple_spinner_item, actions);
        actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionSpinner.setAdapter(actionAdapter);
    }

    private void handleCreateClick() {
        if ("".equals(nameEt.getText().toString())) {
            Toast.makeText(this, "Please Input Name", Toast.LENGTH_SHORT).show();
            return;
        }

        Action action = (Action) actionSpinner.getSelectedItem();
        Action shortCutAction = null;
        if (action != null) {
            try {
                shortCutAction = (Action) action.clone();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        shortCutAction.setName(nameEt.getText().toString());
        shortCutAction.setValue(valueEt.getText().toString());
        ShortCutUtils.createShortCut(getApplicationContext(), shortCutAction);
        Toast.makeText(this, "Create Success", Toast.LENGTH_SHORT).show();
    }

}
