max=20
for i in `seq 2 $max`
do
    mvn test -Dtest=org.eclipse.tractusx.ssi.lib.validation.JsonLdValidatorTest
done