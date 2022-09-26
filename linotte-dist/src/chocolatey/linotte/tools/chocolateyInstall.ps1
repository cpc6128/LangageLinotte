
$ErrorActionPreference = 'Stop'; # stop on all errors

$binRoot = $(Split-Path -parent $MyInvocation.MyCommand.Definition)
$toolsDir = Join-Path $binRoot "linotte"
$exeDir = Join-Path $toolsDir "Linotte.exe"

$packageName= 'linotte' 
$url        = 'https://github.com/cpc6128/LangageLinotte/releases/download/v3.14/Linotte_3.14-2022-09-22-20-02.zip'

$packageArgs = @{
  packageName   = $packageName
  unzipLocation = $toolsDir
  url           = $url
  checksum      = '1E5A8E56B883815548FDA2AE35964EBAC608C7615A3C5B931AD0EBDAE736BF1B'
  checksumType  = 'sha256'
}

Install-ChocolateyZipPackage @packageArgs 

Install-ChocolateyShortcut -shortcutFilePath (Join-Path $env:ALLUSERSPROFILE "Microsoft\Windows\Start Menu\Atelier Linotte.lnk") -TargetPath (Join-Path $toolsDir "Linotte.exe") `
    