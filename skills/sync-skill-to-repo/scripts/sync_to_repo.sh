#!/bin/bash

# Sync a specific skill from ~/.agents/skills to the current repo's skills/ directory.
# Optionally handles Git operations (checkout, commit, push, PR).

SKILL_NAME=$1
GIT_FLOW=false

# Simple flag parsing
shift
while [[ "$#" -gt 0 ]]; do
    case $1 in
        --git) GIT_FLOW=true ;;
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
    shift
done

if [ -z "$SKILL_NAME" ]; then
  echo "Error: No skill name provided."
  echo "Usage: $0 <skill-name> [--git]"
  exit 1
fi

SOURCE_DIR="$HOME/.agents/skills/$SKILL_NAME"
TARGET_DIR="./skills/$SKILL_NAME"

if [ ! -d "$SOURCE_DIR" ]; then
  echo "Error: Skill '$SKILL_NAME' not found in $HOME/.agents/skills/"
  exit 1
fi

if [ "$GIT_FLOW" = true ]; then
    BRANCH_NAME="sync-skill-$SKILL_NAME-$(date +%Y%m%d%H%M%S)"
    echo "Creating new branch: $BRANCH_NAME"
    git checkout -b "$BRANCH_NAME"
fi

mkdir -p ./skills

echo "Syncing '$SKILL_NAME' from global to local..."
rsync -av --delete "$SOURCE_DIR/" "$TARGET_DIR/"

if [ "$GIT_FLOW" = true ]; then
    echo "Committing and pushing changes..."
    git add "$TARGET_DIR"
    git commit -m "feat: sync skill '$SKILL_NAME' from global storage"
    git push origin "$BRANCH_NAME"
    
    if command -v gh &> /dev/null; then
        echo "Creating Pull Request..."
        gh pr create --title "feat: sync skill '$SKILL_NAME'" --body "Synced from ~/.agents/skills/$SKILL_NAME"
    else
        echo "Warning: 'gh' CLI not found. Please create PR manually."
    fi
fi

echo "✅ Skill '$SKILL_NAME' synced to $TARGET_DIR"
