package com.decade.practice.model.local

import com.decade.practice.model.domain.ChatSnapshot

class AccountEntry(
      val account: Account,
      val chatSnapshots: List<ChatSnapshot>
)
