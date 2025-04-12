package com.decade.practice.security.model

interface CredentialModifierInformation {
      fun getPasswordVersion(): Int
      fun getUsername(): String
}