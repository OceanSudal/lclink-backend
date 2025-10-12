package com.sudal.lclink.repository;

import com.sudal.lclink.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    /**
     * 특정 사용자의 모든 채팅방 조회
     */
    List<ChatRoom> findAllByUserId(String userId);


    // 사용자 입장에서 방 목록 조회
    List<ChatRoom> findAllByUserIdOrPartnerId(String userId1, String userId2);

    // 단일 채팅방 조회 (정렬된 userId, partnerId)
    @Query("SELECT c FROM ChatRoom c WHERE c.userId = :userId AND c.partnerId = :partnerId")
    Optional<ChatRoom> findByUserIdAndPartnerId(@Param("userId") String userId,
                                                @Param("partnerId") String partnerId);
}