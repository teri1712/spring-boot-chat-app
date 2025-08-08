package com.decade.practice.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class Migrate {

        @Autowired
        private JdbcTemplate jdbcTemplate;


        void run() {
                jdbcTemplate.execute("CREATE INDEX user_username_idx ON UserMember(username) USING HASH;");
        }
}
