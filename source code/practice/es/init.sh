#!/usr/bin/env bash
curl -X PUT http://elasticsearch:9200/messages -H "Content-Type: application/json" -d @./schemas/messages_v1.json
curl -X PUT http://elasticsearch:9200/users -H "Content-Type: application/json" -d @./schemas/users_v1.json