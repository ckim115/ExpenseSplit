package edu.sjsu.android.expensesplit.ui.split;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;

public class PayerTableRow extends TableRow {

    private final int id;
    private final String name;
    private final boolean visibility;
    private CheckBox checkBox;
    private SeekBar seekBar;
    private EditText editText;
    private TextView textView;
    final boolean[] isUpdating = {false};

    public PayerTableRow(Context context, int id, String name, boolean visibility) {
        super(context);
        this.id = id;
        this.name = name;
        this.visibility = visibility;
        // Set Table Row Layout Parameters
        LayoutParams rowParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        this.setLayoutParams(rowParams);
        this.setPadding(0, 16, 0, 16);
        this.setId(id);

        initializeCheckBox();
        initializeSeekBar();
        initializeEditText();
        initializeTextView();

        this.addView(checkBox);
        this.addView(seekBar);
        this.addView(editText);
        this.addView(textView);
    }

    private void initializeCheckBox() {
        // Initialize CheckBox and Set Layout Parameters
        checkBox = new CheckBox(getContext());
        LayoutParams checkboxParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        checkBox.setLayoutParams(checkboxParams);
        checkBox.setId(id);
        checkBox.setText(name);
        checkBox.setChecked(true);
    }

    private void initializeSeekBar() {
        // Initialize SeekBar and Set Layout Parameters
        seekBar = new SeekBar(getContext());
        LayoutParams seekBarParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                1.0f
        );
        seekBarParams.gravity = Gravity.CENTER_VERTICAL;
        seekBar.setLayoutParams(seekBarParams);
        seekBar.setId(id);
        seekBar.setMax(1000);
        seekBar.setMin(0);
        seekBar.setProgress(500);
        seekBar.setVisibility(visibility ? View.VISIBLE : View.GONE);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && !isUpdating[0]) {
                    isUpdating[0] = true;
                    editText.setText(String.valueOf(progress / 10));
                    isUpdating[0] = false;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void initializeEditText() {
        // Initialize EditText and Set Layout Parameters
        editText = new EditText(getContext());
        LayoutParams percentTextParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        editText.setLayoutParams(percentTextParams);
        editText.setId(id);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setText("50");
        editText.setEms(2);
        editText.setGravity(Gravity.CENTER);
        editText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(3) });
        editText.setVisibility(visibility ? View.VISIBLE : View.GONE);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isUpdating[0]) {
                    isUpdating[0] = true;
                    try {
                        int value = Integer.parseInt(s.toString()) * 10;
                        // Clamp between 0 and 1000
                        value = Math.max(0, Math.min(1000, value));
                        seekBar.setProgress(value);
                    } catch (NumberFormatException e) {
                        // handle empty input gracefully
                        seekBar.setProgress(0);
                    }
                    isUpdating[0] = false;
                }
            }
        });
    }

    private void initializeTextView() {
        // Initialize TextView and Set Layout Parameters
        textView = new TextView(getContext());
        LayoutParams textviewParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(textviewParams);
        textView.setText("%");
        textView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void changeVisibility(boolean visibility) {
        seekBar.setVisibility(visibility ? View.VISIBLE : View.GONE);
        editText.setVisibility(visibility ? View.VISIBLE : View.GONE);
        textView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }
}
