@echo off

cd /d %~dp0

title [FK]

:loop

".\java\Adoptium\jdk-17.0.3+7-jre\bin\java.exe" -jar -server -Xms4G -Xmx4G -XX:+UseLargePages -XX:LargePageSizeInBytes=2M -XX:+UnlockExperimentalVMOptions -XX:+UseShenandoahGC -XX:ShenandoahGCMode=iu -XX:+UseNUMA -XX:+AlwaysPreTouch -XX:-UseBiasedLocking -XX:+DisableExplicitGC -Dfile.encoding=UTF-8 -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 fabric-server-launch.jar -nogui

if "%1"=="stop" goto end

timeout /t 20 /nobreak

goto loop

:end

exit
