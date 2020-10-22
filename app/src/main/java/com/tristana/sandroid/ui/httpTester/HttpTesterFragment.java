package com.tristana.sandroid.ui.httpTester;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tristana.sandroid.R;
import com.tristana.sandroid.tools.toast.ToastUtils;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class HttpTesterFragment extends Fragment {

    private HttpTesterViewModel httpTesterViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        httpTesterViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(HttpTesterViewModel.class);
        View root = inflater.inflate(R.layout.fragment_http_tester, container, false);
        final TextView textView = root.findViewById(R.id.text_http_tester);
        httpTesterViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        final AppCompatEditText appCompatEditText = root.findViewById(R.id.inputUrl);
        final AppCompatButton appCompatButton = root.findViewById(R.id.requestUrl);
        appCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                httpTesterViewModel.startGetRequest(Objects.requireNonNull(appCompatEditText.getText()).toString());
            }
        });
        appCompatButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                httpTesterViewModel.startPostRequest(Objects.requireNonNull(appCompatEditText.getText()).toString());
                return true;
            }
        });
        httpTesterViewModel.getToast().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                ToastUtils.showToast(getActivity(), s);
            }
        });
        return root;
    }
}