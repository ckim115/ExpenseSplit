package edu.sjsu.android.expensesplit.ui.split;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import edu.sjsu.android.expensesplit.R;
import edu.sjsu.android.expensesplit.database.ExpensesDB;
import edu.sjsu.android.expensesplit.databinding.FragmentSplitBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SplitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplitFragment extends Fragment {
    private FragmentSplitBinding binding;
    private static final String TAG = "SplitFragmentLogger";
    private Spinner spinner;
    private List<PayerTableRow> candidates = new ArrayList<>();
    private List<String> payers = new ArrayList<>();

    private final String AUTHORITY = "dataprovider.expensesplit";
    private final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    private ExpensesDB db;

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
        spinner = binding.spinner;
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
        db = new ExpensesDB(getContext());

        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean payMode = checkedId == R.id.radioButton2;
//             Loop through table rows and toggle visibility
            for (int i = 0; i < binding.payers.getChildCount(); i++) {
                View row = binding.payers.getChildAt(i);
                if (row instanceof PayerTableRow) {
                    ((PayerTableRow) row).changeVisibility(payMode);
                }
            }
        });

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
        }

        PayerTableRow tableRow = new PayerTableRow(getActivity(), candidates.size(), name, binding.radioButton2.isChecked());
        binding.payers.addView(tableRow);
        candidates.add(tableRow);
        binding.payerName.setText("");
    }

    private void cancel(View v) {
        Log.i(TAG, "Cancelled");
    }

    private void save(View v) {
        String type = spinner.getSelectedItem().toString();
        String title = binding.title.getText().toString();
        String amount = binding.amount.getText().toString();
        setPayers();

        if (!isValid(amount) || title.isEmpty()) {
            Toast.makeText(getActivity(), R.string.invalid_amount, Toast.LENGTH_SHORT).show();
        } else if (payers.isEmpty() && candidates.isEmpty()) {
            Toast.makeText(getActivity(), R.string.invalid_payers, Toast.LENGTH_SHORT).show();
        } else {
            double A = Double.parseDouble(amount);
            if (binding.radioButton.isChecked()) {
                double S = A / payers.size();
                // Add new instance to database
                for (String payer : payers) {
                    ContentValues values = new ContentValues();
                    values.put("title", title);
                    values.put("name", payer);
                    values.put("amount", S);
                    if (getActivity().getContentResolver().insert(CONTENT_URI, values) != null)
                        Toast.makeText(getActivity(), "Student Added", Toast.LENGTH_SHORT).show();
                }
            } else {
                for (PayerTableRow candidate : candidates) {
                    ContentValues values = new ContentValues();
                    values.put("title", title);
                    values.put("name", candidate.getName());
                    values.put("type", type);
                    values.put("amount", A * candidate.getPercentage() * 0.001);
                    if (getActivity().getContentResolver().insert(CONTENT_URI, values) != null)
                        Toast.makeText(getActivity(), "Student Added", Toast.LENGTH_SHORT).show();
                }
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
