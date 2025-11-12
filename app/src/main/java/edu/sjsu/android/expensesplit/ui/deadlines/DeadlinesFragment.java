package edu.sjsu.android.expensesplit.ui.deadlines;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Date;

import edu.sjsu.android.expensesplit.R;

public class DeadlinesFragment extends Fragment {

    private final String AUTHORITY = "dataprovider.expensesplit";
    private final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private ListView list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_deadlines, container, false);
        list = root.findViewById(R.id.deadlineList);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        long now = System.currentTimeMillis();

        String selection = "due_date IS NOT NULL";
        String[] args = null;
        String sort = "due_date ASC";

        Cursor c = requireContext().getContentResolver()
                .query(CONTENT_URI, null, selection, args, sort);

        String[] from = new String[] {"title", "name", "amount", "due_date"};
        int[] to   = new int[] { android.R.id.text1, android.R.id.text2, android.R.id.text2, android.R.id.text2 };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                requireContext(),
                android.R.layout.simple_list_item_2,
                c,
                from,
                to,
                0
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
                String name  = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double amt   = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                int colDue   = cursor.getColumnIndex("due_date");
                Long dueMs   = cursor.isNull(colDue) ? null : cursor.getLong(colDue);

                t1.setText(title);

                StringBuilder line2 = new StringBuilder();
                line2.append(name).append(" • $").append(String.format("%.2f", amt));
                if (dueMs != null) {
                    String dStr = DateFormat.getDateFormat(context).format(new Date(dueMs));
                    line2.append(" • due ").append(dStr);

                    // Overdue in red
                    if (dueMs < System.currentTimeMillis()) {
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

        list.setAdapter(adapter);
    }
}


