<#
  Verifie la coherence des tableaux Markdown du projet (Q0.3 / Q8.1).

  Objectif : attraper automatiquement les tableaux malformes (lignes tronquees,
  colonnes manquantes) qui font mauvais effet dans une documentation de rendu et
  qui sont difficiles a voir a l'oeil nu.

  Utilisation :
    powershell -NoProfile -ExecutionPolicy Bypass -File tools/check-markdown-tables.ps1
    powershell -NoProfile -ExecutionPolicy Bypass -File tools/check-markdown-tables.ps1 TRAVAIL_MEMBRE_A.md

  Sans argument, le script controle tous les fichiers *.md suivis par Git.
  Code de sortie : 0 = aucun probleme, 1 = au moins un tableau incoherent.
#>
param(
  [string[]]$ExtraFiles = @()
)

$ErrorActionPreference = 'Stop'

function Get-RepoRoot {
  try {
    $root = (git rev-parse --show-toplevel 2>$null)
    if ($LASTEXITCODE -eq 0 -and $root) { return $root.Trim() }
  } catch { }
  return (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
}

function Get-MarkdownFiles([string]$Root, [string[]]$Extra) {
  $files = @()
  try {
    $files += (git ls-files '*.md' 2>$null)
  } catch { }
  foreach ($f in $Extra) {
    if (Test-Path $f) { $files += $f }
  }
  return ($files | Where-Object { $_ } | Select-Object -Unique | ForEach-Object {
    if ([System.IO.Path]::IsPathRooted($_)) { $_ } else { Join-Path $Root $_ }
  })
}

function Get-ColumnCount([string]$Line) {
  # Un tuyau echappe (\|) appartient au contenu de la cellule : il ne separe pas.
  $segments = [regex]::Split($Line, '(?<!\\)\|')
  return $segments.Count
}

$root = Get-RepoRoot
$files = Get-MarkdownFiles -Root $root -Extra $ExtraFiles

$issues = 0
$tables = 0

foreach ($file in $files) {
  $lines = @(Get-Content -LiteralPath $file -Encoding UTF8)
  $inCode = $false
  $block = @()
  $blockStart = 0

  for ($i = 0; $i -le $lines.Count; $i++) {
    $line = if ($i -lt $lines.Count) { $lines[$i] } else { '' }

    if ($line -match '^\s*(```|~~~)') { $inCode = -not $inCode }

    $isRow = (-not $inCode) -and ($line -match '^\s*\|')

    if ($isRow) {
      if ($block.Count -eq 0) { $blockStart = $i + 1 }
      $block += ,$line
      continue
    }

    if ($block.Count -ge 2) {
      $tables++
      $expected = Get-ColumnCount $block[0]
      for ($j = 0; $j -lt $block.Count; $j++) {
        $count = Get-ColumnCount $block[$j]
        if ($count -ne $expected) {
          $relative = $file.Replace($root + [System.IO.Path]::DirectorySeparatorChar, '')
          Write-Host ("ANOMALIE {0}:{1} -> {2} colonne(s) au lieu de {3}" -f $relative, ($blockStart + $j), $count, $expected)
          $issues++
        }
      }
    }
    $block = @()
  }
}

$fileCount = @($files).Count
if ($issues -eq 0) {
  Write-Host ("Tableaux Markdown : OK ({0} fichier(s), {1} tableau(x) controle(s))." -f $fileCount, $tables)
  exit 0
}

Write-Host ("Tableaux Markdown : {0} anomalie(s) sur {1} tableau(x)." -f $issues, $tables)
exit 1
