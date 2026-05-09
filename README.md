# Spring Event Notifications

Spring Boot sample app that simulates SaaS notification events and uses Mailtrap API for emails:

- teammate invited
- task assigned
- comment posted
- weekly activity digest

No real product features are implemented. The app exposes API endpoints and a minimal UI to trigger each event type against seeded sample data.

## Stack

- Java 21 target
- Spring Boot 3.4.13
- Spring Data JPA
- H2
- Thymeleaf
- `io.mailtrap:mailtrap-java`

## How it works

Each simulator endpoint persists the minimal data it needs, publishes a Spring application event, and lets independent listeners react to it.

```text
POST /api/events/invite         -> TeammateInvitedEvent
                                 -> SendTeammateInvitedEmailListener
                                 -> RecordTeammateInvitedActivityListener

POST /api/events/task-assigned  -> TaskAssignedEvent
                                 -> SendTaskAssignedEmailListener
                                 -> RecordTaskAssignedActivityListener

POST /api/events/comment        -> CommentPostedEvent
                                 -> SendCommentPostedEmailListener
                                 -> RecordCommentPostedActivityListener

POST /api/digest/trigger        -> DigestService

@Scheduled weekly job           -> DigestService
```

The event setup is decoupled: adding a new `@EventListener` does not require editing existing listeners.

Mail delivery failures are isolated:

- `MailtrapNotificationMailer` catches SDK errors and returns `false`
- the activity-recording listeners still run
- digest sending continues to the next team if one send fails
- the event multicaster is configured with an error handler so one listener failure does not crash the app

## Prerequisites

- JDK 21 recommended
- newer JDKs also work because Gradle compiles with `--release 21`

## Setup

1. Set Mailtrap variables if you want real email delivery.

```powershell
$env:MAILTRAP_API_TOKEN="your-token"
$env:MAIL_FROM_ADDRESS="[email protected]"
$env:MAIL_FROM_NAME="Spring Event Notifications"

$env:MAILTRAP_TEMPLATE_TEAMMATE_INVITED="template-uuid-1"
$env:MAILTRAP_TEMPLATE_TASK_ASSIGNED="template-uuid-2"
$env:MAILTRAP_TEMPLATE_COMMENT_POSTED="template-uuid-3"
$env:MAILTRAP_TEMPLATE_WEEKLY_DIGEST="template-uuid-4"
```

Optional sandbox mode:

```powershell
$env:MAILTRAP_SANDBOX="true"
$env:MAILTRAP_INBOX_ID="123456"
```

2. Run the app.

```powershell
.\gradlew bootRun
```

3. Open:

- UI: `http://localhost:8080/`
- H2 console: `http://localhost:8080/h2-console`

H2 connection settings:

- JDBC URL: `jdbc:h2:mem:event-notifications`
- user: `sa`
- password: empty

## Seed data

The app seeds sample data on startup with a `CommandLineRunner`.

Teams:

- Acme Engineering
- Globex Product

Users:

- `alice@acme.test` owner
- `bob@acme.test` member
- `carol@acme.test` member
- `dave@globex.test` owner
- `erin@globex.test` member

Tasks:

- `#1 Ship the Q2 release notes`
- `#2 Review onboarding flow copy`
- `#3 Draft Q3 roadmap outline`

## API

All simulator endpoints are JSON.

### Invite teammate

`POST /api/events/invite`

```json
{
  "inviterEmail": "alice@acme.test",
  "inviteeName": "Diana Ward",
  "inviteeEmail": "diana@acme.test"
}
```

Success: `201 Created`

Effect:

- creates the invitee in the inviter's team
- publishes `TeammateInvitedEvent`
- emails the invitee with the teammate-invited Mailtrap template
- records the activity for the weekly digest

### Assign task

`POST /api/events/task-assigned`

```json
{
  "taskId": 1,
  "assignerEmail": "alice@acme.test",
  "assigneeEmail": "bob@acme.test"
}
```

Success: `200 OK`

Effect:

- updates the task assignee
- publishes `TaskAssignedEvent`
- emails the assignee
- records the activity

### Post comment

`POST /api/events/comment`

```json
{
  "taskId": 1,
  "authorEmail": "bob@acme.test",
  "body": "I pushed a fix for the performance issue."
}
```

Success: `201 Created`

Effect:

- creates a task comment
- publishes `CommentPostedEvent`
- emails the task owner
- records the activity

If the comment author is also the task owner, the owner email is skipped but the activity is still recorded.

### Trigger digest manually

`POST /api/digest/trigger`

```json
{
  "teamId": 1,
  "days": 7
}
```

Both fields are optional. With an empty body, the app sends digests for all teams using the configured default window.

Success: `200 OK`

Response example:

```json
{
  "sent": 1,
  "skipped": 1,
  "failed": 0,
  "windowDays": 7
}
```

## Scheduled digest

The digest job runs via `@Scheduled`.

Default schedule:

- cron: `0 0 9 * * MON`
- zone: `Europe/Kiev`

Override with:

- `DIGEST_CRON`
- `DIGEST_ZONE`
- `DIGEST_WINDOW_DAYS`

## Mailtrap templates

One Mailtrap template UUID is used per notification type.

| Env var | Variables |
|---|---|
| `MAILTRAP_TEMPLATE_TEAMMATE_INVITED` | `invitee_name`, `inviter_name`, `team_name` |
| `MAILTRAP_TEMPLATE_TASK_ASSIGNED` | `assignee_name`, `assigner_name`, `task_title`, `task_description`, `task_id` |
| `MAILTRAP_TEMPLATE_COMMENT_POSTED` | `owner_name`, `author_name`, `task_title`, `task_id`, `comment_body` |
| `MAILTRAP_TEMPLATE_WEEKLY_DIGEST` | `owner_name`, `team_name`, `window_days`, `event_count`, `events[]` |

Digest `events[]` entries contain:

- `type`
- `type_label`
- `occurred_at`
- `payload`

## Validation behavior

The API returns `422 Unprocessable Entity` for invalid payloads, unknown users/tasks, duplicate invitee emails, or cross-team assignment/comment attempts.

## Tests

Run:

```powershell
.\gradlew test
```

The test suite covers real flows only:

- invite endpoint records activity
- comment event emails the task owner
- digest sending continues even when a mail send fails

## Project structure

```text
src/main/java/com/event/notifications/
  config/        configuration properties and event multicaster
  domain/        entities, repositories, event records
  listener/      email and activity listeners
  service/       simulator logic, seeding, digest, mailer
  web/           REST controllers, DTOs, error handling
  web/view/      dashboard controller
src/main/resources/
  templates/     Thymeleaf page
  static/        CSS
```

## Local verification

The current implementation was verified locally with:

- `.\gradlew test`
- `.\gradlew bootRun`
- HTTP `200` from `http://localhost:8080/`
