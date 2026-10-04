<#
  Installe le hook Git local pre-commit (fichier propre a chaque poste, jamais versionne).

  Utilisation :
    powershell -NoProfile -ExecutionPolicy Bypass -File tools/install-git-hooks.ps1

  Le hook execute tools/check-markdown-tables.ps1 avant chaque commit et refuse le
  commit si un tableau Markdown est incoherent (contournement : git commit --no-verify).
  Un hook pre-commit deja present est sauvegarde avant remplacement.
#>
$ErrorActionPreference = 'Stop'

$root = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$hookDir = Join-Path $root '.git\hooks'

if (-not (Test-Path -LiteralPath $hookDir)) {
  throw "Dossier .git/hooks introuvable : $hookDir (le dossier .git est-il present ?)"
}

$hookPath = Join-Path $hookDir 'pre-commit'
if (Test-Path -LiteralPath $hookPath) {
  $backup = '{0}.bak-{1}' -f $hookPath, (Get-Date -Format 'yyyyMMdd-HHmmss')
  Copy-Item -LiteralPath $hookPath -Destination $backup -Force
  Write-Host "Hook existant sauvegarde : $backup"
}

$content = @'
#!/bin/sh
# Hook local (non versionne) installe par le membre A le 04/10/2026.
# Bloque un commit qui casserait la coherence des tableaux Markdown.
# Contournement volontaire : git commit --no-verify

root="$(git rev-parse --show-toplevel)"

if command -v powershell >/dev/null 2>&1; then
  powershell -NoProfile -ExecutionPolicy Bypass -File "$root/tools/check-markdown-tables.ps1" || {
    echo "[pre-commit] Tableau(x) Markdown incoherent(s) : corrigez, ou utilisez 'git commit --no-verify'."
    exit 1
  }
else
  echo "[pre-commit] powershell introuvable : controle des tableaux ignore."
fi

exit 0
'@

# Un script sh ne doit pas contenir de CRLF : on force des fins de ligne LF.
$content = $content -replace "`r`n", "`n"
[System.IO.File]::WriteAllText($hookPath, $content, (New-Object System.Text.UTF8Encoding($false)))

Write-Host "Hook installe : $hookPath"
Write-Host "Sous Git Bash, rendre executable : chmod +x .git/hooks/pre-commit"
