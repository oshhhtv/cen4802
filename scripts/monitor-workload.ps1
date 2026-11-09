
param(
    [int]$DurationSeconds = 60,
    [int]$Workers = 8,
    [string]$BaseUrl = "http://localhost:8084"
)

$url = "$BaseUrl/api/readiness?passedTests=95&totalTests=100&criticalDefects=0"

Write-Host "===== CEN4802 MONITORING WORKLOAD ====="
Write-Host "Duration: $DurationSeconds seconds"
Write-Host "Concurrent workers: $Workers"
Write-Host "Endpoint: $url"
Write-Host "Starting: $(Get-Date)"

$jobs = 1..$Workers | ForEach-Object {
    Start-Job -ArgumentList $url, $DurationSeconds -ScriptBlock {
        param($url, $duration)

        $ProgressPreference = 'SilentlyContinue'

        $end = (Get-Date).AddSeconds($duration)
        $success = 0
        $failed = 0

        while ((Get-Date) -lt $end) {
            try {
                $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 5
                if ($response.StatusCode -eq 200) {
                    $success++
                } else {
                    $failed++
                }
            } catch {
                $failed++
            }
        }

        [PSCustomObject]@{
            Successful = $success
            Failed = $failed
        }
    }
}

$jobs | Wait-Job | Out-Null
$results = $jobs | Receive-Job
$jobs | Remove-Job

$totalSuccess = ($results | Measure-Object Successful -Sum).Sum
$totalFailed = ($results | Measure-Object Failed -Sum).Sum

Write-Host "Finished: $(Get-Date)"
Write-Host "Successful requests: $totalSuccess"
Write-Host "Failed requests: $totalFailed"
Write-Host "===== WORKLOAD COMPLETE ====="
