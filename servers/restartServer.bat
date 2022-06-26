title Starting server
@echo off

Taskkill /FI "WINDOWTITLE eq [FK]"

timeout /t 3 > nul

cd /d .\test1_0_14_8_1.19
cmd /C start ShenandoahGC_4GB.bat

exit