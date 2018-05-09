package cn.kkk.live;

import android.content.Context;

import java.util.List;
import java.util.Map;

import cn.kkk.live.data.model.Gift;
import cn.kkk.live.db.GiftDao;
import cn.kkk.live.utils.PreferenceManager;


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
    
    public Map<Integer, Gift> getGiftList(){
        GiftDao dao = new GiftDao();
        return dao.getAppGiftList();
    }

    public void saveGiftList(List<Gift> list) {
        GiftDao dao = new GiftDao();
        dao.saveAppGiftList(list);
    }

    





}
