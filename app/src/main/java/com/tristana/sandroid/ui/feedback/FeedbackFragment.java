package com.tristana.sandroid.ui.feedback;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tristana.sandroid.R;
import com.tristana.sandroid.tools.toast.ToastUtils;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class FeedbackFragment extends Fragment {

    private FeedbackViewModel feedbackViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        feedbackViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(FeedbackViewModel.class);
        View root = inflater.inflate(R.layout.fragment_feedback, container, false);
        feedbackViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        final AppCompatTextView appCompatTextView = (AppCompatTextView) root.findViewById(R.id.feedbackSubmit);
        final AppCompatEditText title = (AppCompatEditText) root.findViewById(R.id.feedbackTitle);
        final AppCompatEditText content = (AppCompatEditText) root.findViewById(R.id.feedbackContent);
        final AppCompatEditText phoneNum = (AppCompatEditText) root.findViewById(R.id.feedbackPhoneNum);
        final AppCompatImageView feedbackPhoneInvalid = (AppCompatImageView) root.findViewById(R.id.feedbackPhoneInvalid);
        feedbackPhoneInvalid.setVisibility(View.GONE);
        phoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                feedbackViewModel.checkPhone(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        appCompatTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedbackViewModel.sendFeed(Objects.requireNonNull(title.getText()).toString(), Objects.requireNonNull(content.getText()).toString(), Objects.requireNonNull(phoneNum.getText()).toString());
            }
        });
        feedbackViewModel.getToast().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                ToastUtils.showToast(getActivity(), s);
            }
        });
        feedbackViewModel.getPhoneInvalid().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    feedbackPhoneInvalid.setVisibility(View.GONE);
                } else {
                    feedbackPhoneInvalid.setVisibility(View.VISIBLE);
                }
            }
        });
        return root;
    }
}