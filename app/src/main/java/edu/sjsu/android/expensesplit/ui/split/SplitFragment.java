package edu.sjsu.android.expensesplit.ui.split;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "addButton = " + binding.addButton);
        // when clicking the '+' button, add the payer from the edittext to the existing list of payers
        binding.addButton.setOnClickListener(v ->
                addPayer(v));

        // for now, just output a log message whenever submit/cancel pressed
        binding.save.setOnClickListener(this::save);
        binding.cancel.setOnClickListener(this::cancel);
    }

    private void addPayer(View v) {
        Log.i(TAG, "Add player");
        String name = binding.payerName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(getActivity(), R.string.invalid, Toast.LENGTH_SHORT).show();
        } else {
            CheckBox box = new CheckBox(getActivity());
            box.setText(name);
            binding.payers.addView(box);
        }
    }

    private void cancel(View v) {
        Log.i(TAG, "Cancelled");
    }

    private void save(View v) {
        Log.i(TAG, "Cancelled");
    }
}