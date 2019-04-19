cd client && npm run build
cd ..
rmdir src\main\resources\public /s/q
xcopy client\build  src\main\resources\public /e/i
