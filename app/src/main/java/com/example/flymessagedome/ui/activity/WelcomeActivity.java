package com.example.flymessagedome.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
import com.example.flymessagedome.FlyMessageApplication;
import com.example.flymessagedome.R;
import com.example.flymessagedome.base.BaseActivity;
import com.example.flymessagedome.component.AppComponent;
import com.example.flymessagedome.component.DaggerMessageComponent;
import com.example.flymessagedome.model.One;
import com.example.flymessagedome.ui.contract.WelcomeContract;
import com.example.flymessagedome.ui.presenter.WelcomePresenter;
import com.example.flymessagedome.utils.NetworkUtils;
import com.example.flymessagedome.utils.SharedPreferencesUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class WelcomeActivity extends BaseActivity implements WelcomeContract.View {

    @BindView(R.id.one_text)
    EditText oneText;
    @BindView(R.id.one_img)
    ImageView oneImg;

    @Inject
    WelcomePresenter welcomePresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMessageComponent.builder().appComponent(appComponent).build().inject(this);
    }

    @Override
    public void initDatas() {
        if (LoginActivity.loginUser!=null){
            MainActivity.startActivity(WelcomeActivity.this);
            finish();
        }else if(!NetworkUtils.isConnected(WelcomeActivity.this)) {
            LoginActivity.startActivity(this);
            finish();
        }else {
            welcomePresenter.getOneData();
            new Thread(){
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(5000);
                        if (!WelcomeActivity.this.isDestroyed()){
                            if (LoginActivity.loginUser!=null){
                                MainActivity.startActivity(WelcomeActivity.this);
                            }else {
                                if (SharedPreferencesUtil.getInstance().getBoolean("isFirstUse",true))
                                    startActivity(new Intent(mContext,GuideActivity.class));
                                else
                                    LoginActivity.startActivity(WelcomeActivity.this);
                            }
                            finish();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    @Override
    public void configViews() {
        welcomePresenter.attachView(this);
    }

    @OnClick({R.id.one_img_view,R.id.jump})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.one_img_view:
                if (LoginActivity.loginUser!=null){
                    MainActivity.startActivity(WelcomeActivity.this);
                }else {
                    if (SharedPreferencesUtil.getInstance().getBoolean("isFirstUse",true))
                        startActivity(new Intent(mContext,GuideActivity.class));
                    else
                        LoginActivity.startActivity(WelcomeActivity.this);
                }
                Intent intent= new Intent(WelcomeActivity.this,WebActivity.class);
                intent.putExtra("URLString","http://wufazhuce.com/");
                finish();
                startActivity(intent);
                break;
            case R.id.jump:
                welcomePresenter.detachView();
                if (SharedPreferencesUtil.getInstance().getBoolean("isFirstUse",true))
                    startActivity(new Intent(mContext,GuideActivity.class));
                else
                    LoginActivity.startActivity(WelcomeActivity.this);
                finish();
                break;
        }
    }

    @Override
    public void showOne(One.DataBean data) {
        oneText.setText(data.getText());
        HttpProxyCacheServer proxy=FlyMessageApplication.getProxy(WelcomeActivity.this);
        data.setSrc(proxy.getProxyUrl(data.getSrc()));
        Glide.with(WelcomeActivity.this).load(data.getSrc()).into(oneImg);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void showError() {
        if (!this.isDestroyed())
            showError("获取每日一句失败");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void showError(String msg) {
        if (!this.isDestroyed())
            showError(msg);
    }

    @Override
    public void complete() {

    }

    @Override
    public void tokenExceed() {

    }
}
