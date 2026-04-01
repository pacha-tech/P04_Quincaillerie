package com.ict300.P04.repository.interfaces.message;

import com.ict300.P04.Entite.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageInterface extends JpaRepository<Message , String> , MessageCustomInterface {
}
