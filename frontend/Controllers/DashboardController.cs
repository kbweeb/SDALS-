using CARD_system.Models;
using CARD_system.Services;
using Microsoft.AspNetCore.Mvc;

namespace CARD_system.Controllers;

public class DashboardController(BackendApiClient api, ILogger<DashboardController> logger) : Controller
{
    private string? Token => HttpContext.Session.GetString("sdals_token");

    private IActionResult RequireLogin()
        => RedirectToAction("Login", "Account", new { returnUrl = Url.Action("Index", "Dashboard") })!;

    [HttpGet]
    public async Task<IActionResult> Index(CancellationToken ct)
    {
        if (Token is null) return RequireLogin();

        var model = new DashboardViewModel();
        try
        {
            var token = Token!;
            var usersTask = api.GetAsync<List<UserDto>>("api/users", token, ct);
            var deviceTask = api.GetAsync<DeviceStatusDto>("api/device/status", token, ct);
            var logsTask = api.GetAsync<PageResult<AccessLogDto>>("api/access-logs?page=0&size=100", token, ct);

            await Task.WhenAll(usersTask, deviceTask, logsTask);
            model.Users = usersTask.Result ?? [];
            model.Device = deviceTask.Result ?? new(null, null, null, null);
            model.Logs = logsTask.Result?.Content ?? [];

            var credentialTasks = model.Users.Select(async user =>
            {
                try
                {
                    var items = await api.GetAsync<List<CredentialDto>>($"api/users/{user.Id}/credentials", token, ct);
                    model.Credentials[user.Id] = items ?? [];
                }
                catch (Exception ex)
                {
                    logger.LogWarning(ex, "Could not load credentials for user {UserId}", user.Id);
                    model.Credentials[user.Id] = [];
                }
            });
            await Task.WhenAll(credentialTasks);
        }
        catch (BackendApiException ex) when (ex.StatusCode == System.Net.HttpStatusCode.Unauthorized
            || ex.StatusCode == System.Net.HttpStatusCode.Forbidden)
        {
            HttpContext.Session.Clear();
            return RedirectToAction("Login", "Account");
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "Dashboard data could not be loaded.");
            model.Error = "Some system data could not be loaded. Check the backend connection and refresh.";
        }

        ViewBag.Username = HttpContext.Session.GetString("sdals_username") ?? "Administrator";
        return View(model);
    }

    [HttpPost, ValidateAntiForgeryToken]
    public async Task<IActionResult> CreateUser(string firstName, string lastName, string role, CancellationToken ct)
    {
        if (Token is null) return RequireLogin();
        if (string.IsNullOrWhiteSpace(firstName) || string.IsNullOrWhiteSpace(lastName)
            || !new[] { "ADMIN", "STUDENT", "GUEST" }.Contains(role))
            return RedirectToAction(nameof(Index));

        await SendAction(() => api.PostAsync("api/users", new { firstName, lastName, role }, Token!, ct),
            "User added.", "Could not add user.");
        return RedirectToAction(nameof(Index));
    }

    [HttpPost, ValidateAntiForgeryToken]
    public async Task<IActionResult> UpdateUser(int id, string firstName, string lastName, string role, CancellationToken ct)
    {
        if (Token is null) return RequireLogin();
        if (string.IsNullOrWhiteSpace(firstName) || string.IsNullOrWhiteSpace(lastName)
            || !new[] { "ADMIN", "STUDENT", "GUEST" }.Contains(role))
            return RedirectToAction(nameof(Index));

        await SendAction(() => api.PutAsync($"api/users/{id}", new { firstName, lastName, role }, Token!, ct),
            "User updated.", "Could not update user.");
        return RedirectToAction(nameof(Index));
    }

    [HttpPost, ValidateAntiForgeryToken]
    public async Task<IActionResult> DeactivateUser(int id, CancellationToken ct)
    {
        if (Token is null) return RequireLogin();
        await SendAction(() => api.PostAsync($"api/users/{id}/deactivate", null, Token!, ct),
            "User deactivated.", "Could not deactivate user.");
        return RedirectToAction(nameof(Index));
    }

    [HttpPost, ValidateAntiForgeryToken]
    public async Task<IActionResult> AddCredential(int userId, string type, int index, string? cardUid, CancellationToken ct)
    {
        if (Token is null) return RequireLogin();
        if (!new[] { "FINGERPRINT", "RFID_CARD", "PASSWORD" }.Contains(type) || index < 0)
            return RedirectToAction(nameof(Index));

        await SendAction(() => api.PostAsync("api/credentials",
            new { userId, type, index, cardUid = string.IsNullOrWhiteSpace(cardUid) ? null : cardUid.Trim() }, Token!, ct),
            "Credential slot mapped.", "Could not map credential.");
        return RedirectToAction(nameof(Index));
    }

    [HttpPost, ValidateAntiForgeryToken]
    public async Task<IActionResult> DeleteCredential(int id, CancellationToken ct)
    {
        if (Token is null) return RequireLogin();
        await SendAction(() => api.DeleteAsync($"api/credentials/{id}", Token!, ct),
            "Credential mapping removed.", "Could not remove credential.");
        return RedirectToAction(nameof(Index));
    }

    [HttpPost, ValidateAntiForgeryToken]
    public async Task<IActionResult> Lock(string command, CancellationToken ct)
    {
        if (Token is null) return RequireLogin();
        if (command is not ("lock" or "unlock")) return RedirectToAction(nameof(Index));
        await SendAction(() => api.PostAsync($"api/device/{command}", null, Token!, ct),
            command == "lock" ? "Lock command accepted by the backend." : "Unlock command accepted by the backend.",
            "The lock command was not accepted.");
        return RedirectToAction(nameof(Index));
    }

    private async Task SendAction(Func<Task<HttpResponseMessage>> action, string success, string failure)
    {
        try
        {
            using var response = await action();
            TempData[response.IsSuccessStatusCode ? "Notice" : "Error"] =
                response.IsSuccessStatusCode ? success : failure;
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "SDALS action failed.");
            TempData["Error"] = failure + " Check the backend connection and your permissions.";
        }
    }
}
