package edu.sjsu.android.expensesplit.ui.split;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private List<PayerTableRow> candidates = new ArrayList<>();
    private List<String> payers = new ArrayList<>();

    private final String AUTHORITY = "dataprovider.expensesplit";
    private final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    private ExpensesDB db;
    private DateViewModel model;

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

        // Get the ViewModel.
        model = new ViewModelProvider(requireActivity()).get(DateViewModel.class);
        // Create the observer which updates the UI.
        final Observer<String> dateObserver = newDate -> {
            // Update the UI, in this case, a TextView.
            LocalDate parsed = LocalDate.parse(newDate); // expects yyyy-MM-dd
            String formatted = parsed.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            binding.dateOutput.setText(formatted);
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        model.getCurrentDate().observe(getViewLifecycleOwner(), dateObserver);

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

        binding.pickDate.setOnClickListener((View v) -> {
            DatePickerFragment dateFragment = new DatePickerFragment();
            dateFragment.show(getChildFragmentManager(), "datePicker");
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
        binding.cancel.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.homeFragment));
    }

    private void addPayer(View v) {
        String name = binding.payerName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(getActivity(), R.string.invalid_name, Toast.LENGTH_SHORT).show();
        } else {
            PayerTableRow tableRow = new PayerTableRow(getActivity(), candidates.size(), name, binding.radioButton2.isChecked());
            binding.payers.addView(tableRow);
            candidates.add(tableRow);
            binding.payerName.setText("");
        }
    }

    private void save(View v) {
        String title = binding.title.getText().toString();
        String amount = binding.amount.getText().toString();
        String date = binding.dateOutput.getText().toString();
        setPayers();

        if (!isValid(amount) || title.isEmpty()) {
            Toast.makeText(getActivity(), R.string.invalid_amount, Toast.LENGTH_SHORT).show();
        } else if (payers.isEmpty() && candidates.isEmpty()) {
            Toast.makeText(getActivity(), R.string.invalid_payers, Toast.LENGTH_SHORT).show();
        } else {
            double A = Double.parseDouble(amount);
            double S = A / candidates.size();
            // Add new instance to database
            for (PayerTableRow candidate : candidates) {
                ContentValues values = new ContentValues();
                values.put("title", title);
                values.put("name", candidate.getName());
                values.put("amount", S);
                if (!date.isEmpty()) {
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    LocalDate olddate = LocalDate.parse(date, inputFormatter);

                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String newDate = olddate.format(outputFormatter);
                    values.put("due_date", newDate);
                }

                if (binding.radioButton.isChecked()) {
                    values.put("amount", S);
                } else {
                    values.put("amount", A * candidate.getPercentage() * 0.001);
                }
                if (getActivity().getContentResolver().insert(CONTENT_URI, values) != null) {
                    Toast.makeText(getActivity(), "Split Created", Toast.LENGTH_LONG).show();
                }
            }
            NavHostFragment.findNavController(this).navigate(R.id.homeFragment);
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
