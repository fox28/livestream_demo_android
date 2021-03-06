package cn.kkk.live.data.model;

import android.content.Context;

import java.io.File;

import cn.kkk.live.I;
import cn.kkk.live.utils.OkHttpUtils;


/**
 * Created by apple on 2017/5/23.
 */

public class UserModel implements IUserModel {
    @Override
    public void register(Context context, String username, String nickname, String password, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_REGISTER)
                .addParam(I.User.USER_NAME,username)
                .addParam(I.User.NICK,nickname)
                .addParam(I.User.PASSWORD,password)
                .post()
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void login(Context context, String username, String password, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_LOGIN)
                .addParam(I.User.USER_NAME,username)
                .addParam(I.User.PASSWORD,password)
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void unregister(Context context, String username, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UNREGISTER)
                .addParam(I.User.USER_NAME,username)
                .targetClass(String.class)
                .execute(listener);
    }
    // http://101.251.196.90:8080/SuperWeChatServerV2.0/findUserByUserName?m_user_name=bbb15906
    @Override
    public void loadUserInfo(Context context, String username, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_USER)
                .addParam(I.User.USER_NAME, username)
                .targetClass(String.class)
                .execute(listener);
    }

    @Override
    public void updateUserNick(Context context, String username, String nick, OnCompleteListener<String> listener) {
//        http://101.251.196.90:8080/SuperWeChatServerV2.0/updateNick?m_user_name=bbb15901&m_user_nick=volley01
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_USER_NICK)
                .addParam(I.User.USER_NAME, username)
                .addParam(I.User.NICK, nick)
                .targetClass(String.class)
                .execute(listener);

    }

    @Override
    public void updateAvatar(Context context, String username, File file, OnCompleteListener<String> listener) {
        // http://101.251.196.90:8080/SuperWeChatServerV2.0/updateAvatar?name_or_hxid=bbb15907&avatarType=user_avatar
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_AVATAR)
                .addParam(I.NAME_OR_HXID, username)
                .addParam(I.AVATAR_TYPE, I.AVATAR_TYPE_USER_PATH)
                .addFile2(file)
                .targetClass(String.class)
                .post()
                .execute(listener);
    }

    @Override
    public void addContact(Context context, String username, String cname, OnCompleteListener<String> listener) {
        // http://101.251.196.90:8080/SuperWeChatServerV2.0/addContact?m_contact_user_name=bb15907&m_contact_cname=bbb15907
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_ADD_CONTACT)
                .addParam(I.Contact.USER_NAME, username)
                .addParam(I.Contact.CU_NAME, cname)
                .targetClass(String.class)
                .execute(listener);

    }

    @Override
    public void loadContacts(Context context, String username, OnCompleteListener<String> listener) {
        // http://101.251.196.90:8080/SuperWeChatServerV2.0/downloadContactAllList?m_contact_user_name=bbb15907
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME, username)
                .targetClass(String.class)
                .execute(listener);

    }

    @Override
    public void deleteConatact(Context context, String username, String cname, OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_CONTACT)
                .addParam(I.Contact.USER_NAME, username)
                .addParam(I.Contact.CU_NAME, cname)
                .targetClass(String.class)
                .execute(listener);
    }
}
