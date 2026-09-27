# Work Handoff Rules

Work quota is scarce. Use Work for execution that benefits from it, not for broad rediscovery.

Each batch must be independently resumable:
1. Read Master Plan, Current Status, Next Task.
2. Execute only the current bounded task.
3. Test.
4. Commit/push early enough that quota loss cannot erase progress.
5. Update Current Status.
6. Write the next single task.
7. Append Changelog/Test Results.
8. Stop cleanly.

If Work quota ends, another ChatGPT session continues from GitHub. Never depend on undocumented chat-only state.

GitHub is the durable source of truth. Do not put Golden eMMC images, secrets, sensitive device data or unnecessarily huge binaries in the repository.

## Autonomous continuation policy
- Minimize owner interaction. Do not ask for routine "continue", approval, or confirmation when the next step is already defined and safe.
- The executor may complete multiple tightly related actions inside the **same bounded batch** (inspect -> implement -> test -> fix observed defects -> rerun -> document -> commit/push) without returning to the owner between actions.
- Stop and ask the owner only for a decision that materially changes user-facing behavior, requires physical-device access, creates irreversible/risky hardware/system changes, needs credentials/permissions the executor cannot obtain, or when evidence reveals a real conflict with an approved decision.
- Technical implementation details, dependency choices within project constraints, test fixes, documentation and CI corrections are executor decisions.
- Never consume Work quota by asking the owner to type "continue" between routine steps. Finish the current bounded batch and leave GitHub in a resumable state.
- Do not interpret autonomy as permission to start the next project batch: checkpoint the completed batch first, then follow the current execution-mode instruction.
