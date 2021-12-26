
$ErrorActionPreference = 'Stop'; # stop on all errors

$binRoot = $(Split-Path -parent $MyInvocation.MyCommand.Definition)
$toolsDir = Join-Path $binRoot "linotte"
$exeDir = Join-Path $toolsDir "Linotte.exe"

$packageName= 'linotte' 
$url        = 'https://github.com/cpc6128/LangageLinotte/releases/download/v3.11/Linotte_3.11-2021-12-17-16-32.zip'

$packageArgs = @{
  packageName   = $packageName
  unzipLocation = $toolsDir
  url           = $url
  checksum      = '0beb5b9dc758a13144d4cecf4df5eafd50c28f8da15a5e561138145d960071ab'
  checksumType  = 'sha256'
}

Install-ChocolateyZipPackage @packageArgs 

Install-ChocolateyShortcut -shortcutFilePath (Join-Path $env:ALLUSERSPROFILE "Microsoft\Windows\Start Menu\Atelier Linotte.lnk") -TargetPath (Join-Path $toolsDir "Linotte.exe") `
    