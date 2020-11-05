package dev.cheerfun.pixivic.biz.web.user.mapper;

import dev.cheerfun.pixivic.biz.web.user.dto.UserListDTO;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.ArtistPreView;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import dev.cheerfun.pixivic.common.util.json.JsonTypeHandler;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

//@Mapper
public interface BusinessMapper {

    @Update("update users set star=star+#{increment}  where user_id=#{userId}")
    int updateUserStar(int userId, int increment);

    @Insert("insert into user_artist_followed (user_id,username, artist_id,create_date) values (#{userId},#{username}, #{artistId}, #{now,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler})")
    int follow(int userId, int artistId, String username, LocalDateTime now);

    @Delete("delete from user_artist_followed where user_id=#{userId} and artist_id = #{artistId}")
    int cancelFollow(int userId, int artistId);

    @Select("select a.* from (select artist_id from user_artist_followed where user_id = #{userId} order by create_date desc  limit #{currIndex} , #{pageSize}) u  join artists a on u.artist_id = a.artist_id")
    @Results({
            @Result(property = "id", column = "artist_id"),
    })
    List<Artist> queryFollowed(int userId, int currIndex, int pageSize);

    @Select("select  count(id) from user_artist_followed where user_id = #{userId} and artist_id=#{artistId}")
    int queryIsFollowed(int userId, int artistId);

    @Insert("replace into user_illust_bookmarked (user_id, illust_id,username,create_date) values (#{userId}, #{illustId},#{username}, #{now,typeHandler=org.apache.ibatis.type.LocalDateTimeTypeHandler})")
    int bookmark(int userId, int illustId, String username, LocalDateTime now);

    @Delete("delete from user_illust_bookmarked where id=#{relationId} ")
    int cancelBookmarkByid(int relationId);

    @Delete("delete from user_illust_bookmarked where user_id=#{userId} and illust_id=#{illustId} ")
    int cancelBookmark(int userId, int illustId);

    @Select("select i.illust_id from (select illust_id from user_illust_bookmarked where user_id=#{userId} order by id desc limit #{currIndex} , #{pageSize}) u left join illusts i on  u.illust_id=i.illust_id where type=#{type}")
    List<Integer> queryBookmarked(int userId, String type, int currIndex, int pageSize);

    @Select("select  illust_id from (select    artist_id from user_artist_followed where user_id=#{userId}) u  join illusts  i   on i.artist_id=u.artist_id   where i.type=#{type} and create_date >= (SELECT DATE_ADD(now(),INTERVAL -1 MONTH))")
    List<Integer> queryFollowedLatestIllustId(int userId, String type);

    @Insert("insert into user_collection_bookmarked (user_id,username,collection_id) values (#{userId},#{username}, #{collectionId})")
    Integer bookmarkCollection(Integer userId, String username, Integer collectionId);

    @Delete("delete from user_collection_bookmarked where user_id = #{userId} and collection_id = #{collectionId}")
    Integer cancelBookmarkCollection(int userId, int collectionId);

    @Insert("insert into user_user_followed (user_id,username,followed_user_id) values (#{userId},#{username}, #{followedUserId})")
    Integer followUser(Integer userId, String username, Integer followedUserId);

    @Delete("delete from user_user_followed where user_id = #{userId} and followed_user_id = #{followedUserId}")
    Integer cancelFollowUser(Integer userId, Integer followedUserId);

    @Select("select collection_id from user_collection_bookmarked where user_id = #{userId} order by create_time desc limit #{currIndex},#{pageSize}")
    List<Integer> queryBookmarkCollection(Integer userId, int currIndex, Integer pageSize);

    @Select("select user_id,username,create_date from user_illust_bookmarked where illust_id=#{illustId} order by id desc  limit #{currIndex} , #{pageSize}")
    @Results({
            @Result(property = "illustId", column = "illust_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "createDate", column = "create_Date", typeHandler = org.apache.ibatis.type.LocalDateTimeTypeHandler.class)
    })
    List<UserListDTO> queryUserListBookmarkedIllust(Integer illustId, int currIndex, int pageSize);

}
