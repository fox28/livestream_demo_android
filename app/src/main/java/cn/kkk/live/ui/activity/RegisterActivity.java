package cn.kkk.live.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.kkk.live.I;
import cn.kkk.live.R;
import cn.kkk.live.data.model.IUserModel;
import cn.kkk.live.data.model.OnCompleteListener;
import cn.kkk.live.data.model.UserModel;
import cn.kkk.live.utils.CommonUtils;
import cn.kkk.live.utils.L;
import cn.kkk.live.utils.MD5;
import cn.kkk.live.utils.MFGT;
import cn.kkk.live.utils.PreferenceManager;
import cn.kkk.live.utils.Result;
import cn.kkk.live.utils.ResultUtils;

public class RegisterActivity extends BaseActivity {
    private static final String TAG = "RegisterActivity";

    @BindView(R.id.email)
    EditText etUsername;
    @BindView(R.id.password)
    EditText etPassword;
    @BindView(R.id.register)
    Button register;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nick)
    EditText etNickname;
    @BindView(R.id.password_confirm)
    EditText etConfirmPassword;

    String username, nickname, pwd;
    ProgressDialog pd;
    IUserModel model; // 注意实例化

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        model = new UserModel();

    }

    private void showDialog() {
        pd = new ProgressDialog(RegisterActivity.this);
        pd.setMessage("正在注册...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    private boolean checkInput() {
        username = etUsername.getText().toString().trim();
        nickname = etNickname.getText().toString().trim();
        pwd = etPassword.getText().toString().trim();
        String confirm_pwd = etConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            etUsername.requestFocus();
            return false;
        } else if (! username.matches("[a-zA-Z]\\w{5,15}")) {// 注意前面的'!'
            etUsername.requestFocus();
            etUsername.setError(getString(R.string.illegal_user_name));
            return false;
        } else if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(this, getResources().getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
            etNickname.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            etConfirmPassword.requestFocus();
            return false;
        } else if (!pwd.equals(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    private void registerAppServer() {
        if (checkInput()) {
            showDialog();
            L.e(TAG, "registerAppServer(), username = " + username + ", pwd = " + MD5.getMessageDigest(pwd));
            model.register(RegisterActivity.this, username, nickname, MD5.getMessageDigest(pwd),
                    new OnCompleteListener<String>() {
                        @Override
                        public void onSuccess(String jsonStr) {
                            L.e(TAG, "registerAppServer|onSuccess, jsonStr = " + jsonStr);
                            boolean success = false; // 设置标识，控制dialog的关闭
                            if (jsonStr != null) {
                                Result result = ResultUtils.getResultFromJson(jsonStr, String.class);
                                if (result != null) {
                                    if (result.isRetMsg()) {
                                        success = true;
                                        registerEMServer();
                                    } else if (result.getRetCode() == I.MSG_REGISTER_USERNAME_EXISTS) {
                                        CommonUtils.showShortToast(R.string.User_already_exists);
                                    } else {
                                        CommonUtils.showShortToast(R.string.Registration_failed);
                                    }
                                }
                            }
                            if (!success) {// 如果注册不成功，关闭dialog
                                pd.dismiss();

                            }


                        }

                        @Override
                        public void onError(String error) { // 发送请求不成功，关闭dialog（上面是发送成功但是结果失败）
                            pd.dismiss();
                            L.e(TAG, "registerAppServer|onError = " + error);
                            CommonUtils.showShortToast(R.string.Registration_failed);
                        }
                    });
        }
    }

    /**
     * 本服务器注册成功、环信注册不成功时，需要删除已注册的账户
     */
    private void unregister() {
        model.unregister(RegisterActivity.this, username, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String result) {
                L.e(TAG, "unregister|onSuccess, result = " + result);
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void registerEMServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(username, MD5.getMessageDigest(pwd));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            showToast("注册成功");
                            PreferenceManager.getInstance().setCurrentUserName(username);
                            MFGT.gotoLogin(RegisterActivity.this);
                        }
                    });
                } catch (final HyphenateException e) {
                    unregister(); // 环信注册失败后、删除本地已注册的相应账户，保持两个服务器数据一致
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            showLongToast("注册失败：" + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    @OnClick(R.id.register)
    public void onRegisterClick() {
        if (checkInput()) {
            showDialog();
            registerAppServer();
        }
    }

}
