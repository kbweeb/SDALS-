using System.Net.Http.Headers;
using System.Net.Http.Json;

namespace CARD_system.Services;

/// <summary>
/// The only place the MVC frontend talks to the Spring Boot backend.
/// Controllers call this; they never build HTTP requests themselves.
/// </summary>
public class BackendApiClient
{
    private readonly HttpClient _http;

    public BackendApiClient(HttpClient http)
    {
        _http = http;
    }

    public async Task<LoginResult?> LoginAsync(string username, string password, CancellationToken ct = default)
    {
        var response = await _http.PostAsJsonAsync("api/auth/login", new { username, password }, ct);
        if (!response.IsSuccessStatusCode)
        {
            return null;
        }
        return await response.Content.ReadFromJsonAsync<LoginResult>(ct);
    }

    public Task<T?> GetAsync<T>(string path, string token, CancellationToken ct = default)
    {
        using var request = new HttpRequestMessage(HttpMethod.Get, path);
        request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
        return SendAsync<T>(request, ct);
    }

    public async Task<HttpResponseMessage> PostAsync(string path, object? body, string token, CancellationToken ct = default)
    {
        using var request = new HttpRequestMessage(HttpMethod.Post, path);
        request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
        if (body is not null)
        {
            request.Content = JsonContent.Create(body);
        }
        return await _http.SendAsync(request, ct);
    }

    private async Task<T?> SendAsync<T>(HttpRequestMessage request, CancellationToken ct)
    {
        var response = await _http.SendAsync(request, ct);
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<T>(ct);
    }
}

public record LoginResult(string Token, long ExpiresInSeconds);
