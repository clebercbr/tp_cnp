rm tp_cnp.jar
ant -f bin/build.xml jar
java -jar tp_cnp.jar
valgrind --tool=memcheck java -jar tp_cnp.jar
time java -jar tp_cnp.jar
