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

    // http://101.251.196.90:8080/SuperWeChatServerV2.0/live/createChatRoom?auth=1IFgE&name=%E7%BE%8E%E5%91%B3%E5%8D%88%E9%A4%90&description=%E7%94%9F%E7%85%8E%E7%8C%AA%E8%84%9A&owner=zzz15901&maxusers=300&members=zzz15901
    @GET("live/createChatRoom")
    Call<String> createChatRoom(
            @Query("auth") String auth,
            @Query("name") String name,
            @Query("description") String description,
            @Query("owner") String owner,
            @Query("owner") int maxusers,
            @Query("members") String members

    );
    // GET http://101.251.196.90:8080/SuperWeChatServerV2.0/live/deleteChatRoom?auth=1IFgE&chatRoomId=123459835
    @GET("live/deleteChatRoom")
    Call<String> deleteChatRoom(
            @Query("auth") String auth,
            @Query("chatRoomId") String chatRoomId
    );
}
