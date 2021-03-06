package cn.kkk.live.data.restapi;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import cn.kkk.live.I;
import cn.kkk.live.LiveApplication;
import cn.kkk.live.data.model.Gift;
import cn.kkk.live.data.model.LiveRoom;
import cn.kkk.live.data.restapi.model.LiveStatusModule;
import cn.kkk.live.data.restapi.model.ResponseModule;
import cn.kkk.live.data.restapi.model.StatisticsType;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;

import java.io.IOException;
import java.util.List;

import cn.kkk.live.utils.L;
import cn.kkk.live.utils.Result;
import cn.kkk.live.utils.ResultUtils;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by wei on 2017/2/14.
 */

public class ApiManager {
    private static final String TAG = "ApiManager";

    private String appkey;
    private ApiService apiService;
    private LiveService mLiveService;
    private static ApiManager instance;

    private ApiManager(){
        try {
            ApplicationInfo appInfo = LiveApplication.getInstance().getPackageManager().getApplicationInfo(
                    LiveApplication.getInstance().getPackageName(), PackageManager.GET_META_DATA);
            appkey = appInfo.metaData.getString("EASEMOB_APPKEY");
            appkey = appkey.replace("#","/");
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("must set the easemob appkey");
        }

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new RequestInterceptor())
                .addInterceptor(httpLoggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://a1.easemob.com/"+appkey+"/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        apiService = retrofit.create(ApiService.class);

        Retrofit liveRetrofit = new Retrofit.Builder()
                .baseUrl(I.SERVER_ROOT)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(httpClient)
                .build();
        mLiveService = liveRetrofit.create(LiveService.class); // 实例化接口LiveService


    }


    static class RequestInterceptor implements Interceptor {

        @Override public okhttp3.Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("Authorization", "Bearer " + EMClient.getInstance().getAccessToken())
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .method(original.method(), original.body())
                    .build();
            okhttp3.Response response =  chain.proceed(request);
            return response;
        }
    }

    public static ApiManager getInstance(){
        if(instance == null){
            instance = new ApiManager();
        }
        return instance;
    }

    /**
     * 创建直播室
     * @param name 直播室名称
     * @param description 直播室描述
     * @param coverUrl 直播封面图片url
     * @return
     * @throws LiveException
     */
    public LiveRoom createLiveRoom(String name, String description, String coverUrl) throws LiveException {
        return createLiveRoomWithRequest(name, description, coverUrl, null);
    }

    /**
     * 根据指定的已经关联的直播室id创建直播
     * @param name 直播室名称
     * @param description 直播室描述
     * @param coverUrl 直播封面图片url
     * @param liveRoomId 要关联直播的直播室id
     * @return
     * @throws LiveException
     */
    public LiveRoom createLiveRoom(String name, String description, String coverUrl, String liveRoomId) throws LiveException {
        return createLiveRoomWithRequest(name, description, coverUrl, liveRoomId);
    }

    private LiveRoom createLiveRoomWithRequest(String name, String description, String coverUrl, String liveRoomId) throws LiveException {
        LiveRoom liveRoom = new LiveRoom();
        liveRoom.setName(name);
        liveRoom.setDescription(description);
        liveRoom.setAnchorId(EMClient.getInstance().getCurrentUser());
        liveRoom.setCover(coverUrl);

        String id = createChatRoom(name, description);
        L.e(TAG, "createLiveRoomWithRequest, id = "+id);
        if (id != null) {
            liveRoom.setId(id);
            liveRoom.setChatroomId(id);
        } else {
            liveRoom.setId(id);
        }

//        Call<ResponseModule<LiveRoom>> responseCall;
//        if(liveRoomId != null){
//            responseCall = apiService.createLiveShow(liveRoomId, liveRoom);
//
//        }else {
//            responseCall = apiService.createLiveRoom(liveRoom);
//        }
//        ResponseModule<LiveRoom> response = handleResponseCall(responseCall).body();
//        LiveRoom room = response.data;
//        if(room.getId() != null) {
//            liveRoom.setId(room.getId());
//        }else {
//            liveRoom.setId(liveRoomId);
//        }
//        liveRoom.setChatroomId(room.getChatroomId());
        //liveRoom.setAudienceNum(1);
//        liveRoom.setLivePullUrl(room.getLivePullUrl());
//        liveRoom.setLivePushUrl(room.getLivePushUrl());
        return liveRoom;
    }

    /**
     * 更新直播室封面
     * @param roomId
     * @param coverUrl
     * @throws LiveException
     */
    public void updateLiveRoomCover(String roomId, String coverUrl) throws LiveException {
        JSONObject jobj = new JSONObject();
        JSONObject picObj = new JSONObject();
        try {
            picObj.put("cover_picture_url", coverUrl);
            jobj.put("liveroom", picObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<ResponseModule> responseCall = apiService.updateLiveRoom(roomId, jsonToRequestBody(jobj.toString()));
        handleResponseCall(responseCall);
    }



    //public void joinLiveRoom(String roomId, String userId) throws LiveException {
    //    JSONObject jobj = new JSONObject();
    //    String[] arr = new String[]{userId};
    //    JSONArray jarr = new JSONArray(Arrays.asList(arr));
    //    try {
    //        jobj.put("usernames", jarr);
    //    } catch (JSONException e) {
    //        e.printStackTrace();
    //    }
    //    handleResponseCall(apiService.joinLiveRoom(roomId, jsonToRequestBody(jobj.toString())));
    //}



    //public void updateLiveRoom(LiveRoom liveRoom) throws LiveException {
    //    Call respCall = apiService.updateLiveRoom(liveRoom.getId(), liveRoom);
    //    handleResponseCall(respCall);
    //}

    public List<Gift> getAllGifts() throws LiveException {
        Call<String> call = mLiveService.getAllGifts();
        Result<List<Gift>> result = handleResponseCallToResultList(call, Gift.class);
        if (result != null&&result.isRetMsg()) {
            return result.getRetData();
        }
        return null;
    }



    public User loadUserInfo(String username) throws IOException, LiveException {
        Call<String> call = mLiveService.loadUserInfo(username);
        Result<User> result = handleResponseCallToResult(call, User.class);
        if (result != null && result.isRetMsg()) {
            return result.getRetData();
        }
        return null;
    }

    /**
     * 本地服务器创建聊天室，获得id后在创建直播室
     * @param name
     * @param description
     */
    public String createChatRoom(String name, String description) {
        return createChatRoom("1IFgE", name, description, EMClient.getInstance().getCurrentUser(),
                300, EMClient.getInstance().getCurrentUser()+",bbb15901,bbb15903,bbb15905,bbb15906" +
                        ",bbb15907,bb15907,bb15904,bb15902,bb15901");
    }

    /**
     * 本地服务器创建聊天室，获得id后在创建直播室
     * @param auth
     * @param name
     * @param description
     * @param owner
     * @param maxusers
     * @param members
     * @return
     */
    public String createChatRoom(String auth, String name, String description, String owner,int maxusers, String members) {
        Call<String> call = mLiveService.createChatRoom(auth, name, description, owner, maxusers, owner);
        try {
            Response<String> response = call.execute();
            String s = response.body();
            L.e(TAG, "createChatRoom, s = "+s);
            if (s != null) {
                return ResultUtils.getEMResultFromJson(s); // 返回id
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteChatRoomFromAppService(String chatRoomId){
        Call<String> call = mLiveService.deleteChatRoom("1IFgE", chatRoomId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String s = response.body();
                if (s != null) {
                    boolean deleteSuccess = ResultUtils.getEMResultWithSuccessFromJson(s);
                    L.e(TAG, "deleteChatRoomFromAppService, isSuccess = "+deleteSuccess);
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                    L.e(TAG, "deleteChatRoomFromAppService, onFailure = "+t.toString());
            }
        });
    }

    /**
     * 获取直播室直播状态
     * @param roomId
     * @return
     * @throws LiveException
     */
    public LiveStatusModule.LiveStatus getLiveRoomStatus(String roomId) throws LiveException {
        Call<ResponseModule<LiveStatusModule>> respCall = apiService.getStatus(roomId);
        return handleResponseCall(respCall).body().data.status;
    }

    /**
     * 结束直播
     * @param roomId
     * @throws LiveException
     */
    public void terminateLiveRoom(String roomId) throws LiveException {
        LiveStatusModule module = new LiveStatusModule();
        module.status = LiveStatusModule.LiveStatus.completed;
        handleResponseCall(apiService.updateStatus(roomId, module));
    }

    //public void closeLiveRoom(String roomId) throws LiveException {
    //    Call respCall = apiService.closeLiveRoom(roomId);
    //    handleResponseCall(respCall);
    //}

    public List<LiveRoom> getLiveRoomList(int pageNum, int pageSize) throws LiveException {
        Call<ResponseModule<List<LiveRoom>>> respCall = apiService.getLiveRoomList(pageNum, pageSize);

        ResponseModule<List<LiveRoom>> response = handleResponseCall(respCall).body();
        return response.data;
    }

    /**
     * 获取正在直播的直播室列表
     * @param limit 取多少
     * @param cursor 在这个游标基础上取数据，首次获取传null
     * @return
     * @throws LiveException
     */
    public ResponseModule<List<LiveRoom>> getLivingRoomList(int limit, String cursor) throws LiveException {
        Call<ResponseModule<List<LiveRoom>>> respCall = apiService.getLivingRoomList(limit, cursor);

        ResponseModule<List<LiveRoom>> response = handleResponseCall(respCall).body();

        return response;
    }

    /**
     * 获取直播间详情
     * @param roomId
     * @return
     * @throws LiveException
     */
    public LiveRoom getLiveRoomDetails(String roomId) throws LiveException {
        return handleResponseCall(apiService.getLiveRoomDetails(roomId)).body().data;
    }

    /**
     * 获取用户已经关联的直播间
     * @param userId
     * @return
     * @throws LiveException
     */
    public List<String> getAssociatedRooms(String userId) throws LiveException {
        ResponseModule<List<String>> response = handleResponseCall(apiService.getAssociatedRoom(userId)).body();
        return response.data;
    }

    //public void grantLiveRoomAdmin(String roomId, String adminId) throws LiveException {
    //    GrantAdminModule module = new GrantAdminModule();
    //    module.newAdmin = adminId;
    //    handleResponseCall(apiService.grantAdmin(roomId, module));
    //}
    //
    //public void revokeLiveRoomAdmin(String roomId, String adminId) throws LiveException {
    //    handleResponseCall(apiService.revokeAdmin(roomId, adminId));
    //}
    //
    //public void grantLiveRoomAnchor(String roomId, String anchorId) throws LiveException {
    //    handleResponseCall(apiService.grantAnchor(roomId, anchorId));
    //}
    //
    //public void revokeLiveRoomAnchor(String roomId, String anchorId) throws LiveException {
    //    handleResponseCall(apiService.revokeAdmin(roomId, anchorId));
    //}
    //
    //public void kickLiveRoomMember(String roomId, String memberId) throws LiveException {
    //    handleResponseCall(apiService.kickMember(roomId, memberId));
    //}

    public void postStatistics(StatisticsType type, String roomId, int count) throws LiveException {
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("type", type);
            jobj.put("count", count);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        handleResponseCall(apiService.postStatistics(roomId, jsonToRequestBody(jobj.toString())));
    }

    //public void postStatistics(StatisticsType type, String roomId, String username) throws LiveException {
    //    JSONObject jobj = new JSONObject();
    //    try {
    //        jobj.put("type", type);
    //        jobj.put("count", username);
    //    } catch (JSONException e) {
    //        e.printStackTrace();
    //    }
    //    handleResponseCall(apiService.postStatistics(roomId, jsonToRequestBody(jobj.toString())));
    //}

    private <T> Response<T> handleResponseCall(Call<T> responseCall) throws LiveException{
        try {
            Response<T> response = responseCall.execute();
            if(!response.isSuccessful()){
                throw new LiveException(response.code(), response.errorBody().string());
            }
            return response;
        } catch (IOException e) {
            throw new LiveException(e.getMessage());
        }
    }
    private <T> Result<T> handleResponseCallToResult(Call<String> responseCall, Class<T> clazz) throws LiveException{
        try {
            Response<String> response = responseCall.execute();
            if(!response.isSuccessful()){
                throw new LiveException(response.code(), response.errorBody().string());
            }
            String s = response.body();
            return ResultUtils.getResultFromJson(s, clazz);
        } catch (IOException e) {
            throw new LiveException(e.getMessage());
        }
    }
    private <T> Result<List<T>> handleResponseCallToResultList(Call<String> responseCall, Class<T> clazz) throws LiveException{
        try {
            Response<String> response = responseCall.execute();
            if(!response.isSuccessful()){
                throw new LiveException(response.code(), response.errorBody().string());
            }
            String s = response.body();
            return ResultUtils.getListResultFromJson(s, clazz);
        } catch (IOException e) {
            throw new LiveException(e.getMessage());
        }
    }

    private RequestBody jsonToRequestBody(String jsonStr){
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);
    }
}
