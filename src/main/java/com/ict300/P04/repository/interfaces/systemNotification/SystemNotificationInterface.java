package com.ict300.P04.repository.interfaces.systemNotification;

import com.ict300.P04.Entite.SystemNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SystemNotificationInterface extends JpaRepository<SystemNotification , String> , SystemNotificationCustomInterface {
    List<SystemNotification> findByUser_IdUserOrderByCreatedAtDesc(String userId);
    List<SystemNotification> findByQuincaillerieIdQuincaillerieOrderByCreatedAtDesc(String quincaillerieId);
}
