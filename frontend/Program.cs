var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllersWithViews();

// Backend API layer (Spring Boot). Base URL comes from appsettings.json -> BackendApi:BaseUrl.
builder.Services.AddHttpClient<CARD_system.Services.BackendApiClient>(client =>
{
    var baseUrl = builder.Configuration["BackendApi:BaseUrl"]
        ?? throw new InvalidOperationException("BackendApi:BaseUrl is not configured");
    client.BaseAddress = new Uri(baseUrl);
});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Home/Error");
    // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
    app.UseHsts();
}

app.UseHttpsRedirection();
app.UseRouting();

app.UseAuthorization();

app.MapStaticAssets();

app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Home}/{action=Index}/{id?}")
    .WithStaticAssets();


app.Run();
