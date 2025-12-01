package edu.sjsu.android.expensesplit.ui.split;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import edu.sjsu.android.expensesplit.R;
import edu.sjsu.android.expensesplit.database.ExpensesDB;
import edu.sjsu.android.expensesplit.databinding.FragmentSplitBinding;
import edu.sjsu.android.expensesplit.notifications.Notification;

public class SplitFragment extends Fragment {
    private FragmentSplitBinding binding;
    private List<PayerTableRow> candidates = new ArrayList<>();
    private List<String> payers = new ArrayList<>();

    private final String AUTHORITY = "dataprovider.expensesplit";
    private final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    private ExpensesDB db;
    private DateViewModel model;

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
            if (newDate == null || newDate.isEmpty()) {
                binding.dateOutput.setText("");
                return;
            }
            // Update the UI, in this case, a TextView.
            LocalDate parsed = LocalDate.parse(newDate); // expects yyyy-MM-dd
            String formatted = parsed.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            binding.dateOutput.setText(formatted);
        };

        model.setCurrentDate("");

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
        {
            model.setCurrentDate("");
            NavHostFragment.findNavController(this).navigate(R.id.homeFragment);
        });
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

        // GET VALID CANDIDATES
        List<PayerTableRow> validCandidates = new ArrayList<>();
        double total = 0;
        for (PayerTableRow candidate : candidates) {
            if (!candidate.isChecked()) continue;
            validCandidates.add(candidate);
            total += candidate.getPercentage() * 0.001;
        }

        if (!isValid(amount) || title.isEmpty()) { // CHECK IF TITLE AND AMOUNT ARE VALID INPUTS
            Toast.makeText(getActivity(), R.string.invalid_amount, Toast.LENGTH_SHORT).show();
        } else if (validCandidates.isEmpty()) { // CHECK IF CANDIDATES EXIST
            Toast.makeText(getActivity(), R.string.invalid_payers, Toast.LENGTH_SHORT).show();
        } else if (binding.radioButton2.isChecked() && total != 1.0) { // CHECK IF PERCENTAGE ADDS UP (only if Custom selected)
            Toast.makeText(getActivity(), R.string.invalid_percentage, Toast.LENGTH_SHORT).show();
        } else {
            double A = Double.parseDouble(amount);
            double S = A / validCandidates.size();

            // Add new instance to database
            for (PayerTableRow candidate : validCandidates) {
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

                    if (checkNotificationPermissions(requireActivity())) {
                        // Schedule a notification
                        scheduleNotification();
                    }
                }

                if (binding.radioButton.isChecked()) {
                    values.put("amount", S);
                } else {
                    values.put("amount", A * candidate.getPercentage() * 0.001);
                }
                requireActivity().getContentResolver().insert(CONTENT_URI, values);
            }
            Toast.makeText(getActivity(), "Split Created", Toast.LENGTH_LONG).show();

            model.setCurrentDate("");
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

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification() {
        // Create an intent for the Notification BroadcastReceiver
        Intent intent = new Intent(requireActivity().getApplicationContext(), Notification.class);

        // Extract title and message from user input
        String title = binding.title.getText().toString();
        String amount = binding.amount.getText().toString();
        StringBuilder message = new StringBuilder();

        int count = candidates.size();
        int maxShown = 3;
        int shown = Math.min(count, maxShown);
        for (int i = 0; i < shown; i++) {
            message.append(candidates.get(i).getName());
            if (i < shown - 1) {
                message.append(", ");
            }
        }
        if (count > maxShown) {
            int others = count - maxShown;
            message.append(" and ").append(others).append(" other(s)");
        }

        message.append(" owe(s) " + amount + " today");

        // Add title and message as extras to the intent
        intent.putExtra("titleExtra", title);
        intent.putExtra("messageExtra", message.toString());

        // Create a PendingIntent for the broadcast
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireActivity().getApplicationContext(),
                121,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Get the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);

        // Get the selected time and schedule the notification
        String date = binding.dateOutput.getText().toString();
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        LocalDateTime localDateTime = localDate.atStartOfDay();
        long time = localDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
        );
    }
    boolean checkNotificationPermissions(Context context) {
        // Check if notification permissions are granted
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        boolean isEnabled = notificationManager.areNotificationsEnabled();

        if (!isEnabled) {
            // Open the app notification settings if notifications are not enabled
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            context.startActivity(intent);

            return false;
        }

        // Permissions are granted
        return true;
    }
}
