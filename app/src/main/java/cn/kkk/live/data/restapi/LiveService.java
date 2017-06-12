package cn.kkk.live.data.restapi;

import cn.kkk.live.I;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by apple on 2017/6/13.
 */

public interface LiveService {

    //GET http://101.251.196.90:8080/SuperWeChatServerV2.0/live/getAllGifts
    @GET("live/getAllGifts")
    Call<String> getAllGifts();

    // http://101.251.196.90:8080/SuperWeChatServerV2.0/findUserByUserName?m_user_name=zzz15901
    @GET("findUserByUserName")
    Call<String> loadUserInfo(@Query(I.User.USER_NAME) String username);
}
