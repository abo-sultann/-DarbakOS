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
