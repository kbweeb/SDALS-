namespace CARD_system.Models;

public record UserDto(int Id, string FirstName, string LastName, string Role, bool Active, DateTime? CreatedAt);
public record CredentialDto(int Id, int UserId, string Type, int Index, string? CardUid);
public record DeviceStatusDto(string? LockState, int? Battery, int? LinkQuality, DateTimeOffset? LastSeen);
public record AccessLogDto(int Id, int? UserId, string? UserName, string? Method, string? Status, DateTime? LoggedAt);

public class PageResult<T>
{
    public List<T> Content { get; set; } = [];
    public int TotalElements { get; set; }
    public int TotalPages { get; set; }
    public int Number { get; set; }
    public int Size { get; set; }
}

public class DashboardViewModel
{
    public List<UserDto> Users { get; set; } = [];
    public Dictionary<int, List<CredentialDto>> Credentials { get; set; } = [];
    public DeviceStatusDto Device { get; set; } = new(null, null, null, null);
    public List<AccessLogDto> Logs { get; set; } = [];
    public string? Error { get; set; }
    public int ActiveUsers => Users.Count(u => u.Active);
    public int InactiveUsers => Users.Count - ActiveUsers;
    public int GrantedToday => Logs.Count(l => string.Equals(l.Status, "GRANTED", StringComparison.OrdinalIgnoreCase)
        && l.LoggedAt?.Date == DateTime.Today);
    public int DeniedToday => Logs.Count(l => string.Equals(l.Status, "DENIED", StringComparison.OrdinalIgnoreCase)
        && l.LoggedAt?.Date == DateTime.Today);
}
