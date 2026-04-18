package com.bookrecommend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnalysisMapper {

    @Select("SELECT behavior_type as type, COUNT(*) as count FROM user_behavior " +
            "WHERE created_at BETWEEN #{startDate} AND #{endDate} GROUP BY behavior_type")
    List<Map<String, Object>> countBehaviorByRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT DATE(created_at) as date, COUNT(*) as count FROM user_behavior " +
            "WHERE created_at BETWEEN #{startDate} AND #{endDate} GROUP BY DATE(created_at)")
    List<Map<String, Object>> behaviorTrend(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT b.id, b.title, b.author, SUM(CASE " +
            "WHEN ub.behavior_type = 4 THEN 4 " +
            "WHEN ub.behavior_type = 3 THEN 3 " +
            "WHEN ub.behavior_type = 2 THEN 2 " +
            "ELSE 1 END) as hot_score " +
            "FROM user_behavior ub JOIN book b ON ub.book_id = b.id " +
            "GROUP BY b.id ORDER BY hot_score DESC LIMIT #{limit}")
    List<Map<String, Object>> hotBooks(@Param("limit") Integer limit);

    @Select("SELECT bc.name, COUNT(b.id) as bookCount FROM book_category bc " +
            "LEFT JOIN book b ON bc.id = b.category_id GROUP BY bc.id")
    List<Map<String, Object>> categoryDistribution();

    @Select("SELECT COUNT(*) FROM recommendation WHERE created_at BETWEEN #{startDate} AND #{endDate}")
    long countTotalRecommended(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT COUNT(DISTINCT r.id) FROM recommendation r " +
            "JOIN user_behavior ub ON r.user_id = ub.user_id AND r.book_id = ub.book_id " +
            "WHERE r.created_at BETWEEN #{startDate} AND #{endDate} " +
            "AND ub.created_at >= r.created_at")
    long countTotalClicked(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT algorithm, COUNT(*) as total, " +
            "(SELECT COUNT(*) FROM user_behavior ub JOIN recommendation r2 ON ub.user_id = r2.user_id AND ub.book_id = r2.book_id " +
            " WHERE r2.algorithm = r.algorithm AND r2.created_at BETWEEN #{startDate} AND #{endDate} AND ub.created_at >= r2.created_at) as clicked " +
            "FROM recommendation r WHERE created_at BETWEEN #{startDate} AND #{endDate} GROUP BY algorithm")
    List<Map<String, Object>> algorithmBreakdown(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
