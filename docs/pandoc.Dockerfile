FROM pandoc/latex:latest-ubuntu

COPY . /data
WORKDIR /data
# Generate md file with pandoc page break tag
RUN echo '\\newpage' > /data/newpage.md
# Replace links to sub pages with anchors
RUN /bin/bash -c "sed -i -E 's/\((.*).md\)/\(#\L\1\)/g' /data/Architecture.md"
# Generate doc as pdf
RUN pandoc -s -o Documentation.pdf /data/README.md /data/newpage.md /data/Architecture.md /data/Feature-*.md /data/Test.md

ENTRYPOINT ["tail", "-f", "/dev/null"]