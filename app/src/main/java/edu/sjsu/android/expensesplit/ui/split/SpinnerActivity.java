package edu.sjsu.android.expensesplit.ui.split;

import android.view.View;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;

public class SpinnerActivity extends Fragment implements AdapterView.OnItemSelectedListener {
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item is selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos).
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback.
    }
}