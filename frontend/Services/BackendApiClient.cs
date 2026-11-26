using System.Net;
using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace CARD_system.Services;

/// <summary>
/// The only place the MVC frontend talks to Spring Boot.
/// The JWT is supplied per request and is never exposed to browser JavaScript.
/// </summary>
public class BackendApiClient
{
    private static readonly JsonSerializerOptions JsonOptions = CreateJsonOptions();
    private readonly HttpClient _http;

    public BackendApiClient(HttpClient http) => _http = http;

    private static JsonSerializerOptions CreateJsonOptions()
    {
        var options = new JsonSerializerOptions(JsonSerializerDefaults.Web);
        options.Converters.Add(new JsonStringEnumConverter());
        return options;
    }

    public async Task<LoginResult?> LoginAsync(string username, string password, CancellationToken ct = default)
    {
        using var response = await _http.PostAsJsonAsync("api/auth/login", new { username, password }, JsonOptions, ct);
        if (!response.IsSuccessStatusCode) return null;
        return await response.Content.ReadFromJsonAsync<LoginResult>(JsonOptions, ct);
    }

    public async Task<T?> GetAsync<T>(string path, string token, CancellationToken ct = default)
    {
        using var request = CreateRequest(HttpMethod.Get, path, token);
        using var response = await _http.SendAsync(request, ct);
        await EnsureSuccess(response);
        return await response.Content.ReadFromJsonAsync<T>(JsonOptions, ct);
    }

    public async Task<HttpResponseMessage> PostAsync(string path, object? body, string token, CancellationToken ct = default)
        => await SendBodyAsync(HttpMethod.Post, path, body, token, ct);

    public async Task<HttpResponseMessage> PutAsync(string path, object? body, string token, CancellationToken ct = default)
        => await SendBodyAsync(HttpMethod.Put, path, body, token, ct);

    public async Task<HttpResponseMessage> DeleteAsync(string path, string token, CancellationToken ct = default)
    {
        using var request = CreateRequest(HttpMethod.Delete, path, token);
        return await _http.SendAsync(request, ct);
    }

    private async Task<HttpResponseMessage> SendBodyAsync(HttpMethod method, string path, object? body, string token, CancellationToken ct)
    {
        var request = CreateRequest(method, path, token);
        if (body is not null) request.Content = JsonContent.Create(body, options: JsonOptions);
        return await _http.SendAsync(request, ct);
    }

    private static HttpRequestMessage CreateRequest(HttpMethod method, string path, string token)
    {
        var request = new HttpRequestMessage(method, path);
        request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
        return request;
    }

    private static async Task EnsureSuccess(HttpResponseMessage response)
    {
        if (response.IsSuccessStatusCode) return;
        var detail = await response.Content.ReadAsStringAsync();
        throw new BackendApiException(response.StatusCode, detail);
    }
}

public record LoginResult(string Token, long ExpiresInSeconds);

public sealed class BackendApiException(HttpStatusCode statusCode, string detail)
    : Exception($"Backend API returned {(int)statusCode} ({statusCode}). {detail}")
{
    public HttpStatusCode StatusCode { get; } = statusCode;
}
