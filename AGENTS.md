# AGENTS.md

Conventions for working in the WorkTimeTracker repository. Keep this file tight,
with short bullet lists of facts and minimal prose.

## Workflow

- Default branch is `master`.
- All changes go through a branch and a pull request.
- Never force-push. Correct earlier commits by adding new commits.
- GitHub Copilot reviews pull requests.
- Build and Dependency Graph workflows run on pushes.
- SonarCloud runs in the Build workflow for pushes outside `dependabot/...`, using branch analysis with the current Git branch name.
- Leave `dependabot/...` branches to Dependabot.

## Project layout

- Single-project Gradle build, Java Swing desktop application.
- Entry point is `net.markwalder.tools.worktime.Main`.
- `src/main/java/net/markwalder/tools/worktime/` contains the controller, `db/`, `tracker/`, `ui/`, and utilities.
- `src/test/java/` contains JUnit 4 tests using Mockito; fixtures live in `src/test/resources/`.
- `docs/jarhc-report.html` and `docs/jarhc-report.txt` are committed generated reports.
- Add the Apache 2.0 license header to new source files.

## Java and Gradle

- Application and tests use a Java 17 toolchain; CI runs Gradle on Java 17.
- Use `./gradlew` for builds; the Gradle version is defined in `gradle/wrapper/gradle-wrapper.properties`.
- Keep JaCoCo on Gradle's default version.
- Application version lives in `gradle.properties` and is expanded into `version.properties` during resource processing.

## Building and testing

- `./gradlew build` compiles, tests, builds the application JAR, and generates JarHC reports.
- The application JAR includes runtime dependencies and the CSV dependency-license report.
- `./gradlew jacocoTestReport` runs tests and generates XML and HTML coverage reports.
- Tests run headless with time zone `Europe/Zurich`; `BuildSystemTest` also checks UTF-8 encoding.
- For expected fixture changes, use `./gradlew test -Pgenerate.test.resources`, review the diff, then rerun without the generation flag.
- Review generated report changes after building. Retain the original Java home value in JarHC reports.

## Dependencies

- Library and plugin versions live in `gradle/libs.versions.toml`.
- Project and buildscript dependencies are locked in `gradle.lockfile` and `buildscript-gradle.lockfile`.
- SHA-256 dependency verification is enabled through `gradle/verification-metadata.xml`; metadata files are verified too.
- Check for updates with `./gradlew dependencyUpdates`.
- Refresh locks with `./gradlew dependencies buildEnvironment --write-locks`.
- After dependency or plugin changes, regenerate verification metadata with `./gradlew dependencies buildEnvironment build jacocoTestReport --write-verification-metadata sha256`. Add `--write-locks` when updating locks in the same invocation.
- Review new checksums against published artifacts, then run the build with verification enforced. Use a fresh Gradle cache when validating metadata completeness; CI can fetch `.module` files absent from a warm local cache.
- Guice 6+ is explicitly excluded from update recommendations; the application uses `javax.inject`. Review that compatibility before a major Guice upgrade.
- Guava is a constraint on Guice's transitive dependency. JSON-java is constrained in the `jarhc` tool configuration. Reassess these constraints when upgrading their parent dependencies.
- For Dependabot alerts, check `dependency.manifest_path` and trace the resolved dependency path.
- Monthly schedules exist for Gradle and GitHub Actions, but version-update PR creation is disabled for both with `open-pull-requests-limit: 0`; upgrade manually.

## GitHub Actions

- Pin every action by full commit SHA with a `# vX.Y.Z` comment.
- Keep `cache-provider: basic` on Gradle Actions.

## Project-specific behavior

- Persistence uses binary `.dat` files through `FileStoreImpl`. Preserve compatibility with existing files when changing storage code.
- `WorkContract` has date-dependent working-hour rules. Cover both sides of a transition when changing those rules.
- Preserve the separate switch labels and their explanatory line comments in `WorkDay`.
- Preserve the TODO comments in `MouseActivityTrackerImplTest` until explicitly asked to address them.
- `DayOfWeek` is an enum; identity comparisons with its constants are valid.

## Categories

Shared terms for commit subjects and branch names:

- `project` for dependency, tooling, version, and build configuration changes.
- `feature` for new features.
- `bugfix` for bug fixes.
- `code` for refactoring.
- `tests` for test changes.
- `docs` for documentation.
- `ci` for GitHub Actions and other CI changes.
- `release` for release activities.
- `hotfix` for urgent fixes.

## Commit messages

- Start the subject with a capitalized category followed by a colon, for example `Project: Upgrade Gradle`.
- Use imperative mood with a capitalized first word after the prefix.
- Combine closely related changes as separate sentences in the subject.
- Add a body only when rationale is needed; explain why.

## Branch and pull request names

- Branches use `<category>/<kebab-case-description>`, for example `project/upgrade-gradle`.
- Pull request titles match the branch name.

## Handling Copilot comments

- Review each comment and any low-confidence suppressed comments; assess suggestions before applying them.
- Evaluate Copilot's proposed fix first. When proposing an alternative, show both and explain the choice.
- Propose fixes and wait for the user's approval before changing code, unless the existing request already authorizes them.
- Once approved, apply the fix, run the relevant build, commit and push, then reply and resolve the thread.
- Disclose any incomplete-review or review-tier warning when summarizing the review.

## Handling SonarCloud findings

- Review findings and security hotspots, distinguish bugs from false positives, and propose fixes before applying them.
- Apply approved fixes, run the build and coverage report, then commit and push.
- Obtain approval before marking findings false positive or accepting them.
- Project key is `smarkwal_WorkTimeTracker`; the default branch in SonarCloud is `master`.
- `./gradlew sonar` runs tests and coverage first. It needs `SONAR_TOKEN` or the `sonar.token` system property; keep token values out of the repository and logs.
- Run Sonar locally for a concrete reason such as validating a scanner, Gradle, or Sonar configuration change; normal branch analysis runs in CI.
- Check both **SonarCloud Code Analysis** (quality gate) and **SonarCloud** (GitHub code scanning) against the current PR head.
- Query SonarCloud directly for full issue and hotspot counts; GitHub code scanning shows the security findings exported to it.
- Check accepted findings separately from open issues. The commented switch labels and mouse-tracker TODOs are intentional accepted findings.
- After merging, confirm SonarCloud analyzed the merge commit before reporting the state of `master`.

## Merging pull requests

- Default to a merge commit.
- Squash only when all commits form one logical change; if unclear, recommend a method and let the user decide.
- After merging, ask before switching to `master`, pulling, and deleting the local branch.
- If the work came from a GitHub issue, comment on the result and any differences from the issue description.
