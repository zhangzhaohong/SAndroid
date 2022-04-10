package com.tristana.sandroid.ui.login;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.gson.Gson;
import com.tristana.customViewLibrary.tools.log.Timber;
import com.tristana.customViewLibrary.view.customLayout.CustomEditTextView;
import com.tristana.sandroid.R;
import com.tristana.sandroid.model.HandlerType;
import com.tristana.sandroid.model.data.DataModel;
import com.tristana.sandroid.model.login.LoginRespModel;
import com.tristana.sandroid.tools.http.HttpUtils;
import com.tristana.sandroid.tools.http.RequestInfo;
import com.tristana.sandroid.tools.sharedPreferences.SpUtils;
import com.tristana.sandroid.tools.text.TextUtils;
import com.tristana.sandroid.tools.toast.ToastUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private boolean isRequest = false;
    private final Timber timber = new Timber("LoginFragment");
    private CustomEditTextView username;
    private CustomEditTextView password;
    private AppCompatCheckBox rememberPassword;
    private AppCompatCheckBox autoLogin;

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case HandlerType.TYPE_TOAST_SUCCESS:
                    password.dismissError();
                    ToastUtils.showToast(requireActivity(), message.obj.toString());
                    break;
                case HandlerType.TYPE_TOAST:
                    password.showError(R.drawable.ic_warning, message.obj.toString(), true);
                    break;
                case HandlerType.TYPE_LOGIN_STATUS:
                    timber.d("LOGIN STATUS!" + message.obj);
//                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
//                    navController.navigate(R.id.nav_home);
                    break;
            }
            return false;
        }
    });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (loginViewModel == null)
            loginViewModel =
                    ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(LoginViewModel.class);
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        username = root.findViewById(R.id.username);
        password = root.findViewById(R.id.password);
        AppCompatTextView login = root.findViewById(R.id.login);
        rememberPassword = root.findViewById(R.id.rememberPassWd);
        autoLogin = root.findViewById(R.id.autoLogin);
        AppCompatTextView register = root.findViewById(R.id.register);
        username.initParameter(
                R.drawable.ic_user_icon,
                InputType.TYPE_CLASS_TEXT,
                0,
                1,
                "请输入账号",
                0,
                0,
                false,
                R.drawable.ic_clear,
                true
        );
        password.initParameter(
                R.drawable.ic_password_icon,
                InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT,
                0,
                1,
                "请输入密码",
                R.drawable.ic_show_password,
                R.drawable.ic_hide_password,
                true,
                R.drawable.ic_clear,
                true
        );
        Boolean rememberStatus = (Boolean) SpUtils.get(requireActivity(), DataModel.REMEMBER_PASSWORD, false);
        Boolean autoLoginStatus = (Boolean) SpUtils.get(requireActivity(), DataModel.AUTO_LOGIN, false);
        if (rememberStatus != null && autoLoginStatus != null) {
            if (rememberStatus) {
                rememberPassword.setChecked(true);
                username.setText(Objects.requireNonNull(SpUtils.get(requireActivity(), DataModel.LAST_USERNAME, "")).toString());
                password.setText(Objects.requireNonNull(SpUtils.get(requireActivity(), DataModel.LAST_PASSWORD, "")).toString());
            }
            if (autoLoginStatus) {
                autoLogin.setChecked(true);
                loginViewModel.doLogin(Objects.requireNonNull(username.getText()), Objects.requireNonNull(password.getText()));
            }
        }
        loginViewModel.getToast().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                ToastUtils.showToast(getActivity(), s);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginViewModel.doLogin(Objects.requireNonNull(username.getText()), Objects.requireNonNull(password.getText()));
            }
        });
        loginViewModel.getLoginStatus().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean && rememberPassword.isChecked()) {
                    SpUtils.put(requireActivity(), DataModel.LAST_USERNAME, Objects.requireNonNull(username.getText()));
                    SpUtils.put(requireActivity(), DataModel.LAST_PASSWORD, Objects.requireNonNull(password.getText()));
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
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password.dismissError();
                testHandler(Objects.requireNonNull(username.getText()), Objects.requireNonNull(password.getText()));
            }

            private void testHandler(final String username, final String password) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.checkEmpty(username) && TextUtils.checkEmpty(password)) {
                            if (!isRequest) {
                                isRequest = true;
                                Map<String, Object> header = new HashMap<>();
                                Map<String, Object> urlParams = new HashMap<>();
                                urlParams.put("api_key", RequestInfo.REQUEST_API_KEY);
                                urlParams.put("account", username);
                                urlParams.put("password", password);
                                String[] data = new HttpUtils().getDataFromUrlByOkHttp3(RequestInfo.REQUEST_LOGIN, urlParams, header);
                                if (Integer.parseInt(data[0]) == -1 || Integer.parseInt(data[0]) > 400) {
                                    sendMessage(HandlerType.TYPE_TOAST, "请求失败 code:" + data[0] + "\n" + data[1]);
                                    sendMessage(HandlerType.TYPE_LOGIN_STATUS, false);
                                } else {
                                    String json = data[1];
                                    Gson gson = new Gson();
                                    LoginRespModel loginRespModel = gson.fromJson(json, LoginRespModel.class);
                                    int code = Integer.parseInt(loginRespModel.getCode());
                                    String msg = loginRespModel.getMsg();
                                    if (code == 0) {
                                        sendMessage(HandlerType.TYPE_TOAST_SUCCESS, msg);
                                        sendMessage(HandlerType.TYPE_LOGIN_STATUS, true);
                                    } else {
                                        sendMessage(HandlerType.TYPE_TOAST, "登录失败 code:" + code + "\n" + msg);
                                        sendMessage(HandlerType.TYPE_LOGIN_STATUS, false);
                                    }
                                }
                                isRequest = false;
                            } else {
                                sendMessage(HandlerType.TYPE_TOAST, "上一个请求正在进行中，请稍后重试！");
                                sendMessage(HandlerType.TYPE_LOGIN_STATUS, false);
                            }
                        } else {
                            sendMessage(HandlerType.TYPE_TOAST, "用户名密码不能为空！");
                            sendMessage(HandlerType.TYPE_LOGIN_STATUS, false);
                        }
                    }
                }).start();
            }
        });
        return root;
    }

    private void sendMessage(int type, Object object) {
        Message message = new Message();
        message.what = type;
        message.obj = object;
        handler.sendMessage(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}