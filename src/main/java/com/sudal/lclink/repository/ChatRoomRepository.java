package com.sudal.lclink.repository;

import com.sudal.lclink.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    /**
     * 특정 사용자의 모든 채팅방 조회
     */
    List<ChatRoom> findAllByUserId(String userId);

    /**
     * 특정 사용자와 특정 상대방의 채팅방 조회
     */
    Optional<ChatRoom> findByUserIdAndPartnerId(String userId, String partnerId);
}