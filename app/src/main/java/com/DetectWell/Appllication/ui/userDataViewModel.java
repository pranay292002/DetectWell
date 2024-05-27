package com.DetectWell.Appllication.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class userDataViewModel extends ViewModel {


    private MutableLiveData<String> userName = new MutableLiveData<>();
    private MutableLiveData<String> userEmail = new MutableLiveData<>();

    // Getter methods
    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<String> getUserEmail() {
        return userEmail;
    }

    // Setter methods
    public void setUserName(String name) {
        userName.setValue(name);
    }

    public void setUserEmail(String email) {
        userEmail.setValue(email);
    }
}
