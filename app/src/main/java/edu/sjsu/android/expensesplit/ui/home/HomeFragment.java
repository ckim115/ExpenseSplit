package edu.sjsu.android.expensesplit.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import edu.sjsu.android.expensesplit.R;
import edu.sjsu.android.expensesplit.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.btnSplit.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.splitFragment));
        binding.btnDeadlines.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.deadlinesFragment));
        binding.btnHistory.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.historyFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
