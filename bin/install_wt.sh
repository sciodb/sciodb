cd ../wiredtiger_2_8_0


make distclean
rm -r wt-libs/*


./configure –-enable-java && make
make install


mvn install:install-file -Dfile=lang/java/wiredtiger.jar -DgroupId=com.wiredtiger -DartifactId=wiredtiger -Dversion=2.8.0 -Dpackaging=jar -DgeneratePom=true


cp lang/java/wiredtiger.jar ../lib
cp .libs/* ../wt-libs