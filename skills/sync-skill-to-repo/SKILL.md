---
name: sync-skill-to-repo
description: Sync a specific skill from your global ~/.agents/skills directory to the current repository's skills/ directory. Supports full Git workflow (branch, commit, push, PR). Use when you want to import a global skill and immediately propose it as a PR.
---

# Sync Skill to Repo

This skill allows you to import a single skill from your global `~/.agents/skills` directory into your current project's `skills/` directory.

## Quick start

To sync a skill **without** Git operations:

```bash
bash ./skills/sync-skill-to-repo/scripts/sync_to_repo.sh <skill-name>
```

To sync a skill **with** a full Git workflow (checkout, commit, push, PR):

```bash
bash ./skills/sync-skill-to-repo/scripts/sync_to_repo.sh <skill-name> --git
```

## Workflow

1. Identify a global skill you want to import.
2. Run the sync script.
3. If `--git` is used:
   - A new branch `sync-skill-<name>-<timestamp>` is created.
   - The skill is synced to `./skills/<name>`.
   - Changes are committed and pushed to `origin`.
   - A Pull Request is created using the `gh` CLI.

## Note

- Requires `rsync` for syncing.
- Requires `git` and optionally `gh` CLI for the full workflow.
- Global skills are expected in `~/.agents/skills/`.
