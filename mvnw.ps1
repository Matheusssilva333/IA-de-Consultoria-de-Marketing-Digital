param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$MavenArgs
)

$env:JAVA_HOME = "C:\Users\mathe\.jdks\openjdk-26.0.1"
$mavenCmd = "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.2\plugins\maven\lib\maven3\bin\mvn.cmd"

& $mavenCmd @MavenArgs
