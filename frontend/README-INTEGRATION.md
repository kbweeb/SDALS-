# SDALS Web Dashboard integration

This bundle is designed for the existing `frontend/` ASP.NET Core MVC project in `kbweeb/SDALS-`.

## Included files
Copy the files into the matching paths under `frontend/`, replacing the existing `Program.cs`,
`Services/BackendApiClient.cs`, `Views/Shared/_Layout.cshtml`, and `Views/Home/Index.cshtml`.

New files:
- `Controllers/AccountController.cs`
- `Controllers/DashboardController.cs`
- `Models/DashboardModels.cs`
- `Views/Account/Login.cshtml`
- `Views/Dashboard/Index.cshtml`
- `wwwroot/css/dashboard.css`
- `wwwroot/js/dashboard.js`

## Configuration
Keep `BackendApi:BaseUrl` in `appsettings.json` pointed at the Spring Boot API, e.g.
`http://localhost:8080/`. For production, use HTTPS and store the URL in environment-specific
configuration or environment variables rather than committing secrets.

## Run
From the repository root:
```bash
cd frontend
dotnet restore
dotnet run
```
Open the URL printed by ASP.NET Core. Sign in with an ADMIN account provisioned by the backend.

## Important integration notes
- This is an ASP.NET Core MVC server-rendered web app, not a second backend or a direct database client.
- The browser talks only to ASP.NET Core. `BackendApiClient` forwards requests to Spring Boot using the
  ADMIN JWT held in server-side session.
- The UI reflects the existing API contract: `ADMIN`, `STUDENT`, `GUEST`; `FINGERPRINT`, `RFID_CARD`,
  `PASSWORD`; access methods `FINGERPRINT`, `RFID`, `PIN`, `REMOTE`.
- The UI does not claim a physical lock command succeeded until the backend accepts the command.
- Credential enrollment here maps a slot that has already been enrolled on the physical lock. It does
  not itself enroll a fingerprint/card/PIN on the lock.
- The existing README notes that Zigbee2MQTT hardware/telemetry payloads still need validation.
  Device readings may therefore show as unavailable until the lock reports them.
