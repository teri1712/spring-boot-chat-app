update conversation
set recents = (select jsonb_agg(jsonb_set(recent, '{postingId}', to_jsonb(gen_random_uuid())))
               from jsonb_array_elements(recents) recent);



update conversation
set modified_at = now() - interval '1 second'
where chat_id = '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004';

update conversation
set modified_at = now() - interval '2 seconds'
where chat_id = '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003';

update conversation
set modified_at = now() - interval '3 seconds'
where chat_id = '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005';