package cn.kkk.live;

import android.app.Application;
import android.content.Intent;
import cn.kkk.live.ui.activity.MainActivity;
import cn.kkk.live.utils.PreferenceManager;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.controller.EaseUI;
import com.ucloud.ulive.UStreamingContext;

/**
 * Created by wei on 2016/5/27.
 */
public class LiveApplication extends Application{

  private static LiveApplication instance; // instance初始化的值为this


  @Override public void onCreate() {
    super.onCreate();
    instance = this;

    LiveHelper.getInstance().init(this);

    //UEasyStreaming.initStreaming("publish3-key");

    UStreamingContext.init(getApplicationContext(), "publish3-key");
  }

  public static LiveApplication getInstance(){
    return instance;
  }


}
