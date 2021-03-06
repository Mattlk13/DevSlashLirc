#!/bin/sh

set -e
if [ ! -d target ] ; then
    mkdir target
fi

doxygen

WORKDIR=gh-pages
ORIGINURL=`git remote get-url origin`

rm -rf ${WORKDIR}
git clone --depth 1 -b gh-pages ${ORIGINURL} ${WORKDIR}
cd ${WORKDIR}
#rm -f *
cp -rf ../target/api-doc/* cpp
cp -rf ../target/site/apidocs/* java
git add *
git commit -a -m "Update of API documentation"
echo Now perform \"git push\" from ${WORKDIR}
