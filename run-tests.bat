@echo off
echo ============================================
echo KS Expert Automation - %date% %time%
echo ============================================

cd /d C:\Users\JalalAhamadKhan\Desktop\Admin\ks-expert-automation

rmdir /s /q target 2>nul
rmdir /s /q reports 2>nul
rmdir /s /q screenshots 2>nul

call mvn clean test -DsuiteXmlFile=testng.xml -Dmaven.test.failure.ignore=true

echo ============================================
echo Execution complete - %date% %time%
echo ============================================
