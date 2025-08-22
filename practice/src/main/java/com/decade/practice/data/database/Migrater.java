package com.decade.practice.data.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class Migrater {

        @Autowired
        private JdbcTemplate jdbcTemplate;

        void run() {
//                jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS user_username_idx ON UserMember(username) USING HASH;");
        }
}
