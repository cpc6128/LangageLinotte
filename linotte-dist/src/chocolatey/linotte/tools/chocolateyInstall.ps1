
$ErrorActionPreference = 'Stop'; # stop on all errors

$binRoot = $(Split-Path -parent $MyInvocation.MyCommand.Definition)
$toolsDir = Join-Path $binRoot "linotte"
$exeDir = Join-Path $toolsDir "Linotte.exe"

$packageName= 'linotte' 
$url        = 'https://github.com/cpc6128/LangageLinotte/releases/download/v3.12/Linotte_3.12-2022-01-31-19-35.zip'

$packageArgs = @{
  packageName   = $packageName
  unzipLocation = $toolsDir
  url           = $url
  checksum      = '694a62a382a140e723fe051b2a02cdfe85ae2407c989b8311309f9a3865a1356'
  checksumType  = 'sha256'
}

Install-ChocolateyZipPackage @packageArgs 

Install-ChocolateyShortcut -shortcutFilePath (Join-Path $env:ALLUSERSPROFILE "Microsoft\Windows\Start Menu\Atelier Linotte.lnk") -TargetPath (Join-Path $toolsDir "Linotte.exe") `
    