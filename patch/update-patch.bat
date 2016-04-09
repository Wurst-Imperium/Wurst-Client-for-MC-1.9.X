@echo off
rem Batch script for updating the Minecraft patch.
rem Requires a local repo with the Minecraft source code.

rem Branches:
rem master    vanilla MC
rem Wurst     modified Wurst MC
rem tmp       temporary branch (will be created & deleted automatically)

rem Change the paths to match your workspace layout:
set mc-dir="C:\Users\alexa\Documents\Eclipse\Minecraft 1.9.X"
set wurst-dir="C:\Users\alexa\Documents\GitHub\Wurst-Client-for-MC-1.9.X"

cd %mc-dir%
git checkout master
git checkout -b tmp
git merge --squash Wurst
git commit -a -m "Wurst"
git format-patch master --ignore-space-change
git checkout Wurst
git branch -D tmp
move /Y 0001-Wurst.patch %wurst-dir%\patch\minecraft.patch
rem pause