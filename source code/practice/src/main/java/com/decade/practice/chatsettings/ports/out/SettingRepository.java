package com.decade.practice.chatsettings.ports.out;

import com.decade.practice.chatsettings.domain.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
      Optional<Setting> findByIdentifier(String chatId);

      List<Setting> findByIdentifierIn(Set<String> chatIds);
}
