javac -d . -cp CommandLineJunit/*;jpf-core/build/jpf-annotations.jar;jpf-core/build/jpf.jar;src/ src/*.java 
echo Main-Class: BeanCounterGUI > MANIFEST.MF
jar cvmf MANIFEST.MF BeanCounter.jar *.class
del *.class MANIFEST.MF
