#!/usr/bin/env bash

export PGPASSWORD=root
psql -h localhost -U root -d chatapp -f fanout_perf_seeding_500.sql || return

python3 jwt_seed.py

