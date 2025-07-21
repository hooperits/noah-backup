# VSS Backup PowerShell Script
# Creates a Volume Shadow Copy snapshot and copies files to backup directory

param(
    [Parameter(Mandatory=$true)][string]$SourcePath,
    [Parameter(Mandatory=$true)][string]$DestinationPath,
    [string]$LogFile = "vss-backup.log"
)

function Write-Log {
    param([string]$Message, [string]$Level = "INFO")
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $logEntry = "[$timestamp] [$Level] $Message"
    Write-Host $logEntry
    Add-Content -Path $LogFile -Value $logEntry
}

function Test-AdminPrivileges {
    $currentUser = [Security.Principal.WindowsIdentity]::GetCurrent()
    $principal = New-Object Security.Principal.WindowsPrincipal($currentUser)
    return $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

try {
    Write-Log "Starting VSS backup process"
    Write-Log "Source: $SourcePath"
    Write-Log "Destination: $DestinationPath"

    # Check if running as administrator
    if (-not (Test-AdminPrivileges)) {
        throw "This script must be run as Administrator to access VSS"
    }

    # Extract drive letter from source path
    $driveLetter = (Get-Item $SourcePath).Root.Name.TrimEnd('\')
    Write-Log "Target drive: $driveLetter"

    # Create destination directory if it doesn't exist
    if (-not (Test-Path $DestinationPath)) {
        New-Item -ItemType Directory -Path $DestinationPath -Force | Out-Null
        Write-Log "Created destination directory: $DestinationPath"
    }

    # Create VSS snapshot
    Write-Log "Creating VSS snapshot for volume: $driveLetter"
    $shadowCopy = (Get-WmiObject -List Win32_ShadowCopy).Create($driveLetter, "ClientAccessible")
    
    if ($shadowCopy.ReturnValue -ne 0) {
        throw "Failed to create VSS snapshot. Return code: $($shadowCopy.ReturnValue)"
    }

    $shadowId = $shadowCopy.ShadowID
    Write-Log "VSS snapshot created with ID: $shadowId"

    # Get the shadow copy device path
    $shadowDevice = (Get-WmiObject Win32_ShadowCopy | Where-Object {$_.ID -eq $shadowId}).DeviceObject
    if (-not $shadowDevice) {
        throw "Failed to retrieve shadow copy device path"
    }
    
    Write-Log "Shadow device path: $shadowDevice"

    # Build the shadow path by replacing the drive letter
    $relativePath = $SourcePath.Substring(2) # Remove "C:" part
    $shadowSourcePath = $shadowDevice + $relativePath
    Write-Log "Shadow source path: $shadowSourcePath"

    # Verify shadow path exists
    if (-not (Test-Path $shadowSourcePath)) {
        throw "Shadow copy source path does not exist: $shadowSourcePath"
    }

    # Copy files from shadow copy to destination
    Write-Log "Starting file copy operation"
    $copyParams = @{
        Path = $shadowSourcePath
        Destination = $DestinationPath
        Recurse = $true
        Force = $true
        ErrorAction = 'Stop'
    }

    if (Test-Path $shadowSourcePath -PathType Container) {
        # If source is a directory, copy contents
        $copyParams.Path = Join-Path $shadowSourcePath "*"
    }

    Copy-Item @copyParams
    Write-Log "File copy completed successfully"

    # Get copy statistics
    $copiedItems = Get-ChildItem -Path $DestinationPath -Recurse -File
    $totalSize = ($copiedItems | Measure-Object -Property Length -Sum).Sum
    $totalFiles = $copiedItems.Count

    Write-Log "Copy statistics: $totalFiles files, $([math]::Round($totalSize/1MB, 2)) MB total"

    # Cleanup: Remove the VSS snapshot
    Write-Log "Cleaning up VSS snapshot"
    $deleteResult = (Get-WmiObject Win32_ShadowCopy | Where-Object {$_.ID -eq $shadowId}).Delete()
    
    if ($deleteResult) {
        Write-Log "VSS snapshot cleaned up successfully"
    } else {
        Write-Log "Warning: Failed to cleanup VSS snapshot" "WARN"
    }

    Write-Log "VSS backup completed successfully"
    exit 0

} catch {
    Write-Log "ERROR: $($_.Exception.Message)" "ERROR"
    Write-Log "Stack trace: $($_.ScriptStackTrace)" "ERROR"
    
    # Attempt to cleanup snapshot on error
    if ($shadowId) {
        try {
            Write-Log "Attempting emergency cleanup of VSS snapshot"
            (Get-WmiObject Win32_ShadowCopy | Where-Object {$_.ID -eq $shadowId}).Delete()
        } catch {
            Write-Log "Failed to cleanup VSS snapshot: $($_.Exception.Message)" "ERROR"
        }
    }
    
    exit 1
}