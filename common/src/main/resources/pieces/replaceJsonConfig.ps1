# Base path
$basePath = "C:\Users\tsofn\Desktop\Tsufit\programming\bootcamp\CTD\project\KFChessClientServerProject\common\src\main\resources\pieces"

# List of states
$states = @("idle", "jump", "long_rest", "move", "short_rest")

foreach ($state in $states) {
    $sourceConfig = Join-Path $basePath "B\states\$state\config.json"

    if (-not (Test-Path $sourceConfig)) {
        Write-Host "Source config not found: $sourceConfig"
        continue
    }

    Get-ChildItem -Path $basePath -Recurse -Filter "config.json" |
        Where-Object { $_.FullName -match "\\$state\\config.json" -and $_.FullName -notmatch "\\B\\" } |
        ForEach-Object {
            Write-Host "Replacing $($_.FullName) with $sourceConfig"
            Copy-Item -Path $sourceConfig -Destination $_.FullName -Force
        }
}
