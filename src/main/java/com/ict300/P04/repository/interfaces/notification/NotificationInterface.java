package com.ict300.P04.repository.interfaces.notification;

import com.ict300.P04.Entite.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationInterface extends JpaRepository<Notification , String> , NotificationCustomInterface {
}
