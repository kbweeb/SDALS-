using CARD_system.Services;
using Microsoft.AspNetCore.Mvc;

namespace CARD_system.Controllers;

public class AccountController(BackendApiClient api, ILogger<AccountController> logger) : Controller
{
    [HttpGet]
    public IActionResult Login(string? returnUrl = null)
    {
        if (!string.IsNullOrEmpty(HttpContext.Session.GetString("sdals_token")))
            return RedirectToAction("Index", "Dashboard");
        ViewBag.ReturnUrl = returnUrl;
        return View();
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> Login(string username, string password, string? returnUrl, CancellationToken ct)
    {
        if (string.IsNullOrWhiteSpace(username) || string.IsNullOrWhiteSpace(password))
        {
            ViewBag.Error = "Enter both your username and password.";
            return View();
        }

        try
        {
            var result = await api.LoginAsync(username.Trim(), password, ct);
            if (result is null || string.IsNullOrWhiteSpace(result.Token))
            {
                ViewBag.Error = "Sign-in failed. Check your credentials and try again.";
                return View();
            }

            HttpContext.Session.SetString("sdals_token", result.Token);
            HttpContext.Session.SetString("sdals_username", username.Trim());
            return Url.IsLocalUrl(returnUrl)
                ? Redirect(returnUrl!)
                : RedirectToAction("Index", "Dashboard");
        }
        catch (HttpRequestException ex)
        {
            logger.LogError(ex, "Could not reach SDALS API during login.");
            ViewBag.Error = "The SDALS API could not be reached. Check that the backend is running.";
            return View();
        }
        catch (TaskCanceledException)
        {
            ViewBag.Error = "The SDALS API took too long to respond. Please try again.";
            return View();
        }
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public IActionResult Logout()
    {
        HttpContext.Session.Clear();
        return RedirectToAction(nameof(Login));
    }
}
