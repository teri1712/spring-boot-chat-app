update conversation
set recents = (select jsonb_agg(
                              case
                                  when (recent ->> 'sequenceId')::int = 203
                                      then jsonb_set(recent, '{seenByIds}', '[
                                    "00000000-0000-0000-0000-000000000004"
                                  ]'::jsonb)

                                  when (recent ->> 'sequenceId')::int = 204
                                      then jsonb_set(recent, '{seenByIds}', '[]'::jsonb)
                                  end
                      )


               from jsonb_array_elements(recents) recent)
where chat_id = '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004';
delete
from seen_pointer;
insert into seen_pointer(id, sender_id, chat_id, at, message_id)
values (0, '00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004', now(), 203)
;