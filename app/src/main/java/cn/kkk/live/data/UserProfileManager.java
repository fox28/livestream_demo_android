package cn.kkk.live.data;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.kkk.live.I;
import cn.kkk.live.data.model.IUserModel;
import cn.kkk.live.data.model.OnCompleteListener;
import cn.kkk.live.data.model.UserModel;
import cn.kkk.live.data.restapi.ApiManager;
import cn.kkk.live.utils.L;
import cn.kkk.live.utils.PreferenceManager;
import cn.kkk.live.utils.Result;
import cn.kkk.live.utils.ResultUtils;

/**
 * 直播项目的客户端，没有好友关系，只保存用户名昵称头像，所以不需要数据库，只需要内存和SharePreference
 */

public class UserProfileManager {
	private static final String TAG = "UserProfileManager";

	/**
	 * application context
	 */
	protected Context appContext = null;

	/**
	 * init flag: test if the sdk has been inited before, we don't need to init
	 * again
	 */
	private boolean sdkInited = false;

	/**
	 * HuanXin sync contact nick and avatar listener
	 */

	private boolean isSyncingContactInfosWithServer = false;

	private User currentAppUser;
	IUserModel userModel;

	public UserProfileManager() {
	}

	public synchronized boolean init(Context context) {
		if (sdkInited) {
			return true;
		}
		// 实例化
		appContext = context;
		userModel = new UserModel();
		sdkInited = true;
		return true;
	}


	public synchronized void reset() {
		isSyncingContactInfosWithServer = false;
		currentAppUser = null;
		PreferenceManager.getInstance().removeCurrentUserInfo();
	}


	public synchronized User getCurrentAppUserInfo() {
		L.e(TAG, "getCurrentAppUserInfo, currentAppUser = "+currentAppUser);
		if (currentAppUser == null||currentAppUser.getMUserName()==null) {
			String username = EMClient.getInstance().getCurrentUser();
			currentAppUser = new User(username);
			String nick = getCurrentUserNick();
			currentAppUser.setMUserNick((nick != null) ? nick : username);
		}
		return currentAppUser;
	}

	public boolean updateCurrentUserNickName(final String nickname) {
		userModel.updateUserNick(appContext, EMClient.getInstance().getCurrentUser(), nickname,
				new OnCompleteListener<String>() {
					boolean isUpdateNick = false;
					@Override
					public void onSuccess(String jsonStr) {
						L.e(TAG, "updateCurrentUserNickName, onSuccess, jsonStr = "+jsonStr);
						if (jsonStr != null) {
							Result result = ResultUtils.getResultFromJson(jsonStr, User.class);
							if (result != null && result.isRetMsg()) {
								User user = (User) result.getRetData();
								if (user != null) {
									isUpdateNick = true;
									setCurrentAppUserNick(user.getMUserNick());// 更新内存和SharePreference
								}
							}
						}
						L.e(TAG, "标识3， updateRemoteNick, isUpdateNick = "+isUpdateNick+", nickname = "+nickname);
						appContext.sendBroadcast(new Intent(I.BROADCAST_UPDATE_USER_NICK)
								.putExtra(I.User.NICK, isUpdateNick));
					}
					@Override
					public void onError(String error) {
						appContext.sendBroadcast(new Intent(I.BROADCAST_UPDATE_USER_NICK)
								.putExtra(I.User.NICK, false));
					}
				});
		return false;
	}

	public void uploadUserAvatar(File file) {
		userModel.updateAvatar(appContext, EMClient.getInstance().getCurrentUser(), file,
				new OnCompleteListener<String>() {
					@Override
					public void onSuccess(String jsonStr) {
						boolean success = false;
						L.e(TAG, "uploadUserAvatar, jsonStr = "+jsonStr);
						if (jsonStr != null) {
							Result result = ResultUtils.getResultFromJson(jsonStr, User.class);
							if (result != null && result.isRetMsg()) {
								User user = (User) result.getRetData();
//								L.e(TAG, "保存头像成功... updateUserAvatar|onSuccess, user = "+user);
								if (user != null) {
									// 保存用户头像的全部数据
									currentAppUser = user;
//									L.e(TAG, "uploadUserAvatar|onSuccess, avatar = "+user.getAvatar());
									success = true;
									setCurrentAppUserAvatar(user.getAvatar()); // 保存到内存和SharePreference；
								}
							}
						}
						appContext.sendBroadcast(new Intent(I.BROADCAST_UPDATE_AVATAR).
								putExtra(I.Avatar.UPDATE_TIME, success));
					}

					@Override
					public void onError(String error) {
						appContext.sendBroadcast(new Intent(I.BROADCAST_UPDATE_AVATAR)
								.putExtra(I.Avatar.UPDATE_TIME, false));
					}
				});
//		String avatarUrl = ParseManager.getInstance().uploadParseAvatar(data);
//		if (avatarUrl != null) {
//			setCurrentUserAvatar(avatarUrl);
//		}
//		return avatarUrl;
	}



	public void updateCurrentAppUserInfo(User user) {
		currentAppUser = user;
		L.e(TAG, "updateCurrentAppUserInfo, currentAppUser = "+currentAppUser);
		// 将user.getAvatar()保存到内存 SharePreference中
		setCurrentAppUserNick(user.getMUserNick());
		setCurrentAppUserAvatar(user.getAvatar());
	}

	public void asyncGetCurrentAppUserInfo(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					User user = ApiManager.getInstance().loadUserInfo(EMClient.getInstance().getCurrentUser());
					L.e(TAG, "asyncGetCurrentAppUserInfo, user = "+user);
					if (user != null) {
						updateCurrentAppUserInfo(user);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
//		userModel.loadUserInfo(appContext, EMClient.getInstance().getCurrentUser(),
//				new OnCompleteListener<String>() {
//			@Override
//			public void onSuccess(String jsonStr) {
//				if (jsonStr != null) {
//					Result result = ResultUtils.getResultFromJson(jsonStr, User.class);
//					if (result != null &&result.isRetMsg()) {
//						User user = (User) result.getRetData();
//						if (user!=null) {
//							updateCurrentAppUserInfo(user);
//						}
//
//					}
//				}
//			}
//
//			@Override
//			public void onError(String error) {
//			}
//		});
	}


	private void setCurrentAppUserNick(String nickname) {
		getCurrentAppUserInfo().setMUserNick(nickname); //昵称保存到currentAppUser
		PreferenceManager.getInstance().setCurrentUserNick(nickname);
	}

	private void setCurrentAppUserAvatar(String avatar) {
		getCurrentAppUserInfo().setAvatar(avatar);
		PreferenceManager.getInstance().setCurrentUserAvatar(avatar);
	}

	private String getCurrentUserNick() {
		return PreferenceManager.getInstance().getCurrentUserNick();
	}

	private String getCurrentUserAvatar() {
		return PreferenceManager.getInstance().getCurrentUserAvatar();
	}

}
