# Step 12: Jenkins Notifications — Microsoft Teams + Email

Pipeline alerts on **SUCCESS**, **FAILURE**, and **UNSTABLE** builds.  
**Slack is not used** in this project — only Teams and Email.

---

## What Was Configured

| Channel | Mechanism | When it fires |
|---------|-----------|---------------|
| **Microsoft Teams** | Incoming Webhook (MessageCard JSON) | Every build end state |
| **Email** | Email Extension plugin (`emailext`) | Every build end state |

Updated in `Jenkinsfile`:
- Removed Slack webhook and notification code
- Enhanced Teams card (status icon, commit, duration, View Build button)
- HTML email body with build details table
- Attaches console log on **FAILURE** / **UNSTABLE**

---

## Prerequisites

### Jenkins plugins

**Manage Jenkins → Plugins → Installed**

| Plugin | Required for |
|--------|----------------|
| **Email Extension Plugin** | HTML email notifications |
| **Pipeline** | Jenkinsfile |

### Jenkins credentials

**Manage Jenkins → Credentials → System → Global credentials → Add Credentials**

| Credential ID | Type | Description |
|---------------|------|-------------|
| `teams-webhook-url` | **Secret text** | Microsoft Teams Incoming Webhook URL |
| *(optional)* `github-credentials-id` | Username/Password | Already from Step 7 |

> **Important:** Credential ID must be exactly `teams-webhook-url` (matches `Jenkinsfile`).

---

## Part 1 — Microsoft Teams Setup (Workflows — current method)

> **Why you don't see "Connectors":** Microsoft retired the old **Office 365 Connectors** menu in Teams. Incoming webhooks are now created via the **Workflows** app. This is normal — use the steps below.

### Method 1 — From your channel (easiest)

1. Open **Microsoft Teams** (desktop or web)
2. Go to the **team** and **channel** where you want Jenkins alerts (e.g. `DevOps` → `General` or create `#jenkins-alerts`)
3. Click **⋯** (three dots) next to the channel name at the top
4. Look for **Workflows** (not Connectors)
5. Search or pick template: **Send webhook alerts to a channel**
6. Sign in / authenticate if prompted
7. Confirm **Team** and **Channel** are correct
8. Click **Add workflow** or **Save**
9. On the workflow details page, copy the **HTTP POST URL** (webhook link)

The URL often looks like:
```
https://prod-XX.westus.logic.azure.com:443/workflows/.../triggers/manual/paths/invoke?...
```

10. In Jenkins → **Credentials** → Add **Secret text**:
    - **ID:** `teams-webhook-url`
    - **Secret:** paste the full webhook URL

### Method 2 — From the Workflows app (if ⋯ menu has no Workflows)

1. In Teams left sidebar, click **⋯** (Apps) or **Apps**
2. Search **Workflows** → open the **Workflows** app
3. Go to the **Create** tab
4. Search template: **Send webhook alerts to a channel**
5. Follow the wizard → choose team/channel → **Save**
6. Copy the **HTTP POST URL** from the workflow details page

### Method 3 — Create from blank (advanced)

1. Open **Workflows** app → **Create** → **Create from blank**
2. **Trigger:** `When a Teams webhook request is received`
3. **Action:** `Post card in chat or channel` (Teams connector)
4. Set channel and card content → **Save**
5. Copy webhook URL from the trigger step

### Can't find Workflows at all?

| Try this | Details |
|----------|---------|
| Update Teams | Use latest **New Teams** client |
| Ask IT admin | Workflows may be disabled by org policy |
| Use Power Automate | https://make.powerautomate.com → Create cloud flow with same trigger |
| Email only for now | Skip Teams credential; configure SMTP — pipeline still sends email |

### Test Teams webhook (from your PC)

Replace `YOUR_WEBHOOK_URL` with your copied URL:

```powershell
$body = @{
  "@type" = "MessageCard"
  "@context" = "http://schema.org/extensions"
  "themeColor" = "00B050"
  "summary" = "Test from EMS project"
  "sections" = @(
    @{
      "activityTitle" = "Jenkins notification test"
      "text" = "If you see this in Teams, your webhook is working."
    }
  )
} | ConvertTo-Json -Depth 5

Invoke-RestMethod -Uri "YOUR_WEBHOOK_URL" -Method POST -ContentType "application/json" -Body $body
```

You should see a message in the channel within a few seconds.

> **Note:** Workflows webhooks support **MessageCard** JSON (what our `Jenkinsfile` sends). The **View Build** button may not appear — that's a Microsoft limitation; build details still show in the card body.

### Legacy Connectors (deprecated — skip if not visible)

Old path was: Channel → **⋯** → **Connectors** → **Incoming Webhook**. Microsoft is retiring this. If you still have an old `outlook.office.com/webhook/...` URL, it may work until fully retired — prefer **Workflows** for new setups.

You should see a card in the Teams channel within a few seconds.

---

## Part 2 — Email Setup

### Step 1 — Configure SMTP in Jenkins

1. **Manage Jenkins → System** (Configure System)
2. Scroll to **E-mail Notification**
3. Set **SMTP server**, for example:

| Provider | SMTP server | Port | TLS |
|----------|-------------|------|-----|
| **Gmail** | `smtp.gmail.com` | `587` | ✅ |
| **Outlook / Office 365** | `smtp.office365.com` | `587` | ✅ |
| **Custom** | your company SMTP host | `587` or `465` | per IT |

4. Click **Advanced** → enable **Use SMTP Authentication**
5. Enter SMTP username and password (or app password)
6. Set **System Admin e-mail address** (sender), e.g. `jenkins@yourcompany.com`
7. Check **Use SSL** or **Use TLS** as required
8. Click **Test configuration** → enter your email → **Test**
9. **Save**

### Step 2 — Gmail app password (if using Gmail)

1. Google Account → **Security** → enable **2-Step Verification**
2. **App passwords** → generate password for "Mail"
3. Use that 16-character password in Jenkins SMTP (not your normal Gmail password)

### Step 3 — Set email recipients

Recipients are controlled by Jenkins environment variable **`EMAIL_RECIPIENTS`**.

**Option A — Jenkins job / folder environment**

1. Pipeline job → **Configure**
2. **Build Environment** or pipeline properties → add:
   ```
   EMAIL_RECIPIENTS=you@email.com,team@company.com
   ```

**Option B — Global environment variable**

1. **Manage Jenkins → System → Global properties**
2. Check **Environment variables**
3. Add:
   - Name: `EMAIL_RECIPIENTS`
   - Value: `you@email.com,qa-team@company.com`

**Option C — Default in Jenkinsfile**

If not set, defaults to `team@company.com` — change this in `Jenkinsfile` or set the env var above.

Multiple recipients: comma-separated, no spaces:
```
you@email.com,colleague@company.com
```

---

## Part 3 — Run Pipeline and Verify

### Recommended first run

| Parameter | Value |
|-----------|--------|
| `SKIP_DEPLOY` | `true` |
| `SKIP_SMOKE_TESTS` | `true` |
| `RUN_SONAR` | `false` |

This runs build + unit tests quickly and triggers notifications.

### After build completes

**Teams:** Check your channel for a card like:

```
✅ employee-management-system
Build #1 — SUCCESS
Status: SUCCESS | Branch: main | Commit: abc1234
[View Build in Jenkins]
```

**Email:** Check inbox for:

```
Subject: [SUCCESS] employee-management-system — Build #1 (dev)
```

**Console log** (Jenkins build output):

```
>>> Pipeline SUCCEEDED — sending notifications...
Teams notification sent.
Email notification sent to you@email.com.
```

---

## Notification Content Reference

### Teams MessageCard fields

| Field | Source |
|-------|--------|
| Status | SUCCESS / FAILURE / UNSTABLE |
| Branch | Git branch name |
| Commit | Short commit hash |
| Environment | `DEPLOY_ENV` parameter (dev/staging/prod) |
| Duration | Jenkins build duration |
| Button | Link to `BUILD_URL` |

### Email

| Build result | Log attachment |
|--------------|----------------|
| SUCCESS | No |
| FAILURE | Yes (console log) |
| UNSTABLE | Yes (console log) |

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| Build fails before notifications: `teams-webhook-url` not found | Add Jenkins credential with exact ID `teams-webhook-url` |
| `Teams notification skipped` in log | Webhook URL wrong/expired; re-create webhook; test with PowerShell curl above |
| No Teams message but log says sent | Check correct channel; webhook may be disabled |
| `Email notification skipped` | Install Email Extension plugin; configure SMTP |
| SMTP test fails in Jenkins | Check firewall, app password, TLS port 587 |
| Email goes to spam | Use company SMTP; set valid **System Admin e-mail address** |
| Notifications on every branch | Expected — all builds notify; filter in Teams/email rules if needed |
| `sh: curl: not found` (Windows agent) | Install curl on Jenkins agent or use Windows agent with Git Bash |

---

## Security Notes

- **Never commit** webhook URLs or SMTP passwords to Git
- Store Teams URL only in Jenkins **Secret text** credential
- Rotate webhooks if exposed
- Use dedicated channel for CI alerts (not general team chat)

---

## Jenkinsfile Reference

```groovy
environment {
    TEAMS_WEBHOOK_URL = credentials('teams-webhook-url')
    EMAIL_RECIPIENTS  = "${env.EMAIL_RECIPIENTS ?: 'team@company.com'}"
}

post {
    success { sendNotifications('SUCCESS') }
    failure { sendNotifications('FAILURE') }
    unstable { sendNotifications('UNSTABLE') }
}
```

---

## Next Step

**Step 13 — Final Project Review** — end-to-end demo checklist, portfolio talking points, and interview Q&A.

Say **"proceed to Step 13"** when ready.
