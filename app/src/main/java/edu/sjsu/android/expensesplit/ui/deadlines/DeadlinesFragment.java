package edu.sjsu.android.expensesplit.ui.deadlines;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

import edu.sjsu.android.expensesplit.R;

public class DeadlinesFragment extends Fragment {

    private final String AUTHORITY = "dataprovider.expensesplit";
    private final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    private Spinner spinner;

    private CheckBox sort;
    private ListView list;
    SimpleCursorAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_deadlines, container, false);
        list = root.findViewById(R.id.deadlineList);
        sort = root.findViewById(R.id.sortOrder);
        spinner = root.findViewById(R.id.sort);
        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(
                requireActivity(),
                R.array.sort_types,
                android.R.layout.simple_spinner_item
        );
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);

        // Creating cursor adapter
        String[] from = {"title", "name", "amount", "due_date"};
        int[] to = {
                android.R.id.text1,
                android.R.id.text2,
                android.R.id.text2,
                android.R.id.text2
        };

        adapter = new SimpleCursorAdapter(
                requireContext(),
                android.R.layout.simple_list_item_2,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        ) {
            @Override
            public void setViewText(TextView v, String text) {
                // We'll custom-format fields
                super.setViewText(v, text);
            }

            @Override
            public void bindView(View view, android.content.Context context, Cursor cursor) {
                TextView t1 = view.findViewById(android.R.id.text1);
                TextView t2 = view.findViewById(android.R.id.text2);

                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double amt = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                int dateIndex = cursor.getColumnIndex("due_date");
                String dueDate = dateIndex == -1 ? null : cursor.getString(dateIndex);

                t1.setText(title);

                StringBuilder line2 = new StringBuilder();
                line2.append(name).append(" • $").append(String.format("%.2f", amt));
                if (dueDate != null) {
                    LocalDate parsed = LocalDate.parse(dueDate); // expects yyyy-MM-dd
                    String formatted = parsed.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

                    line2.append(" • due ").append(formatted);

                    LocalDate today = LocalDate.now();

                    // Overdue in red
                    if (parsed.isBefore(today)) {
                        t1.setTextColor(0xFFB00020);
                        t2.setTextColor(0xFFB00020);
                    } else {
                        t1.setTextColor(0xFF000000);
                        t2.setTextColor(0xFF666666);
                    }
                }
                t2.setText(line2.toString());
            }
        };

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // get id
                Cursor c = (Cursor) list.getItemAtPosition(i);

                int id = c.getInt(c.getColumnIndexOrThrow("_id"));
                String selection = "_id = " + id;

                ContentValues values = new ContentValues();
                values.put("_id", id);
                values.put("complete", 1);

                if (requireContext().getContentResolver().update(CONTENT_URI, values, selection, null) == 1) {
                    Toast.makeText(getActivity(), "Sent to History", Toast.LENGTH_SHORT).show();
                    loadData();
                }
                return true;
            }
        });


        list.setAdapter(adapter);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                loadData();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Code to run when nothing is selected
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        String sort_op = spinner.getSelectedItem().toString().toLowerCase().replace(' ', '_');

        String selection = "complete IS 0"; //"due_date IS NOT NULL";
        String[] args = null;
        String sortOrder = sort.isChecked() ? " DESC" : " ASC";
        String sort = " (CASE WHEN " + sort_op + " IS NULL then 1 ELSE 0 END)," + sort_op + sortOrder;

        Cursor c = requireContext().getContentResolver()
                .query(CONTENT_URI, null, selection, args, sort);

        adapter.changeCursor(c);
        adapter.notifyDataSetChanged();
    }
}