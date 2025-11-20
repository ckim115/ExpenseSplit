package edu.sjsu.android.expensesplit.ui.split;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DateViewModel extends ViewModel {

    // Create a LiveData with a String
    private MutableLiveData<String> currentDate;

    public MutableLiveData<String> getCurrentDate() {
        if (currentDate == null) {
            currentDate = new MutableLiveData<String>();
        }
        return currentDate;
    }

    // Rest of the ViewModel...
}