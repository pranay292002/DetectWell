package com.DetectWell.Appllication.ui.contactUs;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.DetectWell.Appllication.R;
import com.DetectWell.Appllication.databinding.FragmentContactUsBinding;




public class ContactUs extends Fragment {

    TextView Contactdetails;

    private FragmentContactUsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentContactUsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Contactdetails = root.findViewById(R.id.details);

        Contactdetails.setMovementMethod(LinkMovementMethod.getInstance());
        Contactdetails.setClickable(true);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}