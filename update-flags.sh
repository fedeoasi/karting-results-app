#/usr/bin/sh
mv $1 flags.zip
unzip flags.zip
mv flags.css src/main/webapp/css/
mv flags.png src/main/webapp/css/
