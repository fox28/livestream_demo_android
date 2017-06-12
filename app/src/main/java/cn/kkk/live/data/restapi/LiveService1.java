package cn.kkk.live.data.restapi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
/**
 * Created by apple on 2017/6/11.
 */

public interface LiveService1 {
    /** 客户端发送的全部礼物信息并展示，包括礼物的名称、图片地址和价格的请求 */
    @GET("live/getAllGifts")
    Call<String> getAllGifts();

    /** 客户端发送的根据用户名获取账户余额的请求 */
    @GET("live/getBalance")
    Call<String> getBalance(@Query("uname") String username);

    /** 客户端发送的用户给主播赠送礼物的请求 */
    @GET("live/givingGifts")
    Call<String> givingGifts(
            @Query("uname") String username,
            @Query("anchor") String anchor,
            @Query("giftId") int giftId,
            @Query("giftNum") int giftNum
    );
    /** 客户端发送的分页加载充值流水的请求 */
    @GET("live/getRechargeStatements")
    Call<String> getRechargeStatements(
            @Query("uname") String username,
            @Query("pageId") int giftId,
            @Query("pageSize") int giftNum
    );

    /** 客户端发送的统计主播收到礼物的次数、数量及礼物信息等的请求 */
    @GET("live/getGiftStatementsByAnchor")
    Call<String> getGiftStatementsByAnchor(@Query("anchor") String anchor);

    /** 客户端发送的用户充值的请求 */
    @GET("live/recharge")
    Call<String> recharge(
            @Query("uname") String username,
            @Query("rmb") int rmb
    );

    /** 客户端发送的用户给主播赠送礼物的请求 */
    @GET("live/getAllChatRoom")
    Call<String> getAllChatRoom();

    /** 客户端发送的创建直播室 */
    @GET("live/createChatRoom")
    Call<String> createChatRoom(
            @Query("auth") String auth,
            @Query("name") String name,
            @Query("description") String description,
            @Query("owner") String owner,
            @Query("maxusers") String maxusers,
            @Query("members") String members
    );

    /** 客户端发送的删除直播室 */
    @GET("live/deleteChatRoom")
    Call<String> deleteChatRoom(
            @Query("auth") String auth,
            @Query("chatRoomId") String chatRoomId
    );

    /** 客户端分页加载送礼物流水 */
    @GET("live/getGivingGiftStatements")
    Call<String> getGivingGiftStatements(
            @Query("uname") String username,
            @Query("pageId") int giftId,
            @Query("pageSize") int giftNum
    );

    /** 客户端分页加载主播收礼物流水 */
    @GET("live/getReceivingGiftStatementsServlet")
    Call<String> getReceivingGiftStatementsServlet(
            @Query("uname") String username,
            @Query("pageId") int giftId,
            @Query("pageSize") int giftNum
    );
}
