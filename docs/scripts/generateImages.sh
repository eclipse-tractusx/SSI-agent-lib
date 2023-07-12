#!/bin/bash
# converts all puml files to png images

BASEDIR=$(dirname "$0")/../puml
TARGETDIR=$BASEDIR/../images
mkdir -p $TARGETDIR
for FILE in $BASEDIR/*.puml; do
  echo Converting $FILE
  FILE_PNG=${FILE//\.puml/\.png}
  cat $FILE | docker run --rm -i think/plantuml -tpng > $FILE_PNG
done
mv $BASEDIR/*.png $TARGETDIR/
echo Done