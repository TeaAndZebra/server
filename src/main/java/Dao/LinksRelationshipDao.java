package Dao;

import databaseOperation.LinksRelationship;
import org.apache.ibatis.annotations.Param;
import java.util.ArrayList;

public interface LinksRelationshipDao {
    void insertLinks(@Param("linksRelationship") LinksRelationship linksRelationship);
    ArrayList<String> selectLinksBySource(@Param("devSource") String devSource, @Param("type") String type, @Param("channel") int channel);
    void delAllLinksBySource(String devSource);
    void delLinksBySource(@Param("linksRelationship")LinksRelationship linksRelationship);
}
