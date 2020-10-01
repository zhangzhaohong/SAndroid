package com.tristana.sandroid.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.tristana.sandroid.R;
import com.tristana.sandroid.model.data.DataModel;
import com.tristana.sandroid.tools.sharedPreferences.SpUtils;
import com.tristana.sandroid.tools.toast.ToastUtils;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        loginViewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(LoginViewModel.class);
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        final AppCompatEditText username = (AppCompatEditText) root.findViewById(R.id.username);
        final AppCompatEditText password = (AppCompatEditText) root.findViewById(R.id.password);
        final AppCompatTextView login = (AppCompatTextView) root.findViewById(R.id.login);
        final AppCompatCheckBox rememberPassword = (AppCompatCheckBox) root.findViewById(R.id.rememberPassWd);
        final AppCompatCheckBox autoLogin = (AppCompatCheckBox) root.findViewById(R.id.autoLogin);
        Object rememberStatus = SpUtils.get(requireActivity(), DataModel.REMEMBER_PASSWORD, false);
        Object autoLoginStatus = SpUtils.get(requireActivity(), DataModel.AUTO_LOGIN, false);
        if (rememberStatus != null && autoLoginStatus != null) {
            if ((Boolean) rememberStatus) {
                rememberPassword.setChecked((Boolean) rememberStatus);
                username.setText(Objects.requireNonNull(SpUtils.get(requireActivity(), DataModel.LAST_USERNAME, "")).toString());
                password.setText(Objects.requireNonNull(SpUtils.get(requireActivity(), DataModel.LAST_PASSWORD, "")).toString());
            }
            if ((Boolean) autoLoginStatus) {
                autoLogin.setChecked((Boolean) autoLoginStatus);
                loginViewModel.doLogin(Objects.requireNonNull(username.getText()).toString(), Objects.requireNonNull(password.getText()).toString());
            }
        }
        loginViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        loginViewModel.getToast().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                ToastUtils.showToast(getActivity(), s);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginViewModel.doLogin(Objects.requireNonNull(username.getText()).toString(), Objects.requireNonNull(password.getText()).toString());
            }
        });
        loginViewModel.getLoginStatus().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean && rememberPassword.isChecked()) {
                    SpUtils.put(requireActivity(), DataModel.LAST_USERNAME, Objects.requireNonNull(username.getText()).toString());
                    SpUtils.put(requireActivity(), DataModel.LAST_PASSWORD, Objects.requireNonNull(password.getText()).toString());
                }
            }
        });
        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rememberPassword.setChecked(true);
                    ToastUtils.showToast(requireActivity(), "开启自动登录后会自动保存当前账户密码");
                }
                SpUtils.put(requireActivity(), DataModel.REMEMBER_PASSWORD, rememberPassword.isChecked());
                SpUtils.put(requireActivity(), DataModel.AUTO_LOGIN, autoLogin.isChecked());
            }
        });
        rememberPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SpUtils.put(requireActivity(), DataModel.REMEMBER_PASSWORD, rememberPassword.isChecked());
            }
        });
        return root;
    }

}