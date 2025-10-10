package edu.sjsu.android.expensesplit.ui.split;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.sjsu.android.expensesplit.R;
import edu.sjsu.android.expensesplit.databinding.FragmentSplitBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SplitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplitFragment extends Fragment {
    private FragmentSplitBinding binding;
    private static final String TAG = "SplitFragmentLogger";
    List<String> candidates = new ArrayList<>();
    List<String> payers = new ArrayList<>();

    public SplitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SplitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SplitFragment newInstance() {
        SplitFragment fragment = new SplitFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSplitBinding.inflate(inflater, container, false);

        // Initialize spinner with values
        Spinner spinner = binding.spinner;
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireActivity(),
                R.array.types,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        spinner.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // when clicking the '+' button, add the payer from the edittext to the existing list of payers
        binding.addButton.setOnClickListener(this::addPayer);

        // for now, just output a log message whenever submit/cancel pressed
        binding.save.setOnClickListener(this::save);
        binding.cancel.setOnClickListener(this::cancel);
    }

    private void addPayer(View v) {
        Log.i(TAG, "Add player");
        String name = binding.payerName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(getActivity(), R.string.invalid_name, Toast.LENGTH_SHORT).show();
        } else {
            CheckBox box = new CheckBox(getActivity()); //set on choose textbox, add that name to payers
            box.setId(candidates.size());
            box.setText(name);
            binding.payers.addView(box);
            candidates.add(name);
        }
    }

    private void cancel(View v) {
        Log.i(TAG, "Cancelled");
    }

    private void save(View v) {
        String title = binding.title.getText().toString();
        String amount = binding.amount.getText().toString();
        setPayers();

        if (!isValid(amount) || title.isEmpty()) {
            Toast.makeText(getActivity(), R.string.invalid_amount, Toast.LENGTH_SHORT).show();
        } else if (payers.isEmpty()) {
            Toast.makeText(getActivity(), R.string.invalid_payers, Toast.LENGTH_SHORT).show();
        } else {
            double A = Double.parseDouble(amount);
            double S = A / payers.size();
            if (binding.radioButton.isChecked()) {
                // equal split
                Log.i(TAG, "Expense " + title + " Equal pay: " + S);
            } else {
                // custom split
                Log.i(TAG, "Custom pay. Total: " + A);
            }
        }
    }

    private void setPayers() {
        TableLayout table = binding.payers;
        for (int i = 0; i < table.getChildCount(); i++) {
            View child = table.getChildAt(i);
            if (child instanceof CheckBox checkBox) {
                if(checkBox.isChecked()) payers.add((String) checkBox.getText());
            }
        }
    }

    private boolean isValid (String input) {
        return !input.isEmpty() && (input.indexOf('.') == -1 || input.substring(input.indexOf('.')).length() <= 3);
    }
}