package cn.kkk.live.utils;

import android.content.Context;



public class LiveModel {
//    UserDao dao = null;  // UserDao处理数据库，相关信息全部删除
    protected Context context = null;

    public LiveModel(Context ctx){
        context = ctx;
        PreferenceManager.init(context);
    }

    /**
     * 联系人相关信息全部删除
     * @param contactList
     * @return
     */

    
    /**
     * save current username
     * @param username
     */
    public void setCurrentUserName(String username){
        PreferenceManager.getInstance().setCurrentUserName(username);
    }

    /**
     * 从SharePreference获得用户名
     * @return
     */
    public String getCurrentUsernName(){
        return PreferenceManager.getInstance().getCurrentUsername();
    }
    



    





}
